package br.com.infowaypi.ecare.financeiro.faturamento;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecare.segurados.DependenteSR;
import br.com.infowaypi.ecare.segurados.DependenteSuplementar;
import br.com.infowaypi.ecare.segurados.Pensionista;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.segurados.Titular;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe para gerar um arquivo com a utilização dos titulares mais seus dependentes
 * @author Diogo Vinícius
 *
 */
public class GeradorArquivoFaturamentoSegurados {

	/**
	 * Método principal
	 * @param args
	 * @throws ValidateException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws ValidateException, IOException {
		System.out.println("A cada mil guias é impresso: #");
		System.out.println();
		System.out.println("Mês 07");
		gerarArquivoRelatorio(Utils.gerarCompetencia("07/2007"));
		System.out.println();
		System.out.println("Mês 08");
		gerarArquivoRelatorio(Utils.gerarCompetencia("08/2007"));
		System.out.println();
		System.out.println("Mês 09");
		gerarArquivoRelatorio(Utils.gerarCompetencia("09/2007"));
		System.out.println();
		System.out.println("Mês 10");
		gerarArquivoRelatorio(Utils.gerarCompetencia("10/2007"));
	}

	/**
	 * Classe interna para encapsular os parâmetros para a criação do arquivo.
	 * @author Diogo Vinícius
	 *
	 */
	private static class FileParameters {
		
		private File file;
		private FileWriter writer;
		private PrintWriter saida;
		
		private FileParameters(File file, FileWriter writer, PrintWriter saida) {
			this.file = file;
			this.writer = writer;
			this.saida = saida;
		}
		
	}
	
	private static void gerarArquivoRelatorio(Date competencia) throws IOException, ValidateException {
		String mes = Utils.format(competencia, "MM/yyyy");
		mes = mes.substring(0, 2);
		File arquivo = new File("utilizacao_segurados_mes_"+ mes + ".csv");
		if (arquivo.exists()) {
			arquivo.delete();
		}
		FileWriter writer = new FileWriter(arquivo, true);
		PrintWriter saida = new PrintWriter(writer, true);
		FileParameters fileParameters = new FileParameters(arquivo, writer, saida);
		fileParameters.saida.print("CPF DO TITULAR;");
		fileParameters.saida.print("VALOR DO GRUPO;");
		fileParameters.saida.println();			
	
		Criteria criteria = HibernateUtil.currentSession().createCriteria(GuiaSimples.class);
		List<GuiaSimples> guias = criteria.createAlias("faturamento", "f")
				.add(Expression.eq("situacao.descricao", SituacaoEnum.FATURADA.descricao()))
				.add(Expression.eq("f.competencia", competencia))
				.add(Expression.eq("f.status", 'F'))
				.list();
		
		Map<String, BigDecimal> registro = new HashMap<String, BigDecimal>();
		Titular titular = null;
		DependenteSR dependente = null;
		DependenteSuplementar suplementar = null;
		Pensionista pensionista = null;
		String cpf = null;
		BigDecimal valor = BigDecimal.ZERO;
		
		int count = 0;
		BigDecimal valorTotal = BigDecimal.ZERO;
		for (GuiaSimples guia : guias) {
			if (count++ % 1000 == 0)
				System.out.print("#");
			
			valor = BigDecimal.ZERO;
			valor = valor.add(guia.getValorTotal());
			valorTotal = valorTotal.add(valor);
			Segurado segurado = ImplDAO.findById(guia.getSegurado().getIdSegurado(), Segurado.class);
			
			if (segurado.isSeguradoDependenteSuplementar()) {
				suplementar = (DependenteSuplementar) segurado;
				cpf = suplementar.getTitular().getPessoaFisica().getCpf();
			}
			else if (segurado.isSeguradoDependente()) {
				dependente = (DependenteSR) segurado;
				cpf = dependente.getTitular().getPessoaFisica().getCpf();
			} 
			else if (segurado.isSeguradoPensionista()) {
				pensionista = (Pensionista) segurado;
				cpf = pensionista.getTitular().getPessoaFisica().getCpf();
			}
			else if (segurado.isSeguradoTitular()) {
				titular = (Titular) segurado;
				cpf = titular.getPessoaFisica().getCpf();
			}
			else {
				throw new ValidateException("Está errado! Que tipo de segurado dessa guia é este? Guia:" + guia.getAutorizacao());
			}
			
			if (registro.containsKey(cpf)) {
				valor = valor.add(registro.get(cpf));
			}
			registro.put(cpf, valor);
			
		}
		System.out.println();
		System.out.println("Quantidade de Guias: " + count);
		System.out.println("Valor Total: " + valorTotal);
		
		for (String cpfDoTitular : registro.keySet()) {
			setInFile(fileParameters, cpfDoTitular, String.valueOf(registro.get(cpfDoTitular)));	
		}
		
		fileParameters.saida.close();
		fileParameters.writer.close();
	}

	private static BigDecimal calcularValorGrupo(Titular titular, Date competencia) {
		BigDecimal valor = BigDecimal.ZERO;
		for (GuiaSimples guia : titular.getGuias()) {
			if ((guia.getFaturamento() != null) && (guia.getFaturamento().getCompetencia().compareTo(competencia) == 0)) {
				valor = valor.add(guia.getValorTotal());
			}
		}
		for (DependenteSuplementar dependente : titular.getDependentesSuplementares()) {
			for (GuiaSimples guia : dependente.getGuias()) {
				if ((guia.getFaturamento() != null) && (guia.getFaturamento().getCompetencia().compareTo(competencia) == 0)) {
					valor = valor.add(guia.getValorTotal());
				}
			}
		}
		for (DependenteSR dependente : titular.getDependentes()) {
			for (GuiaSimples guia : dependente.getGuias()) {
				if ((guia.getFaturamento() != null) && (guia.getFaturamento().getCompetencia().compareTo(competencia) == 0)) {
					valor = valor.add(guia.getValorTotal());
				}
			}			
		}
		return valor;
	}

	/**
	 * Seta no arquivo uma nova linha com os dados passados como parâmetro.
	 * @param fileParameters
	 * @param cpfDoTitular
	 * @param valorDoGrupo
	 */
	private static void setInFile (FileParameters fileParameters, String cpfDoTitular, String valorDoGrupo) {
		fileParameters.saida.print(cpfDoTitular + ";");
		fileParameters.saida.print(valorDoGrupo);
		fileParameters.saida.println();
	}
	
}
