package br.com.infowaypi.ecare.financeiro.boletos;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import br.com.infowaypi.ecare.financeiro.Cobranca;
import br.com.infowaypi.ecare.financeiro.conta.ContaMatricula;
import br.com.infowaypi.ecare.segurados.DependenteInterface;
import br.com.infowaypi.ecare.segurados.DependenteSuplementar;
import br.com.infowaypi.ecare.segurados.TitularFinanceiroSR;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe para gerar um arquivo <b>coparticipacao.csv</b> com os valores da coparticipa��o do titulares com
 * pagamentos do tipo boleto e/ou que t�m dependente(s) suplementar(es). <br>
 * � gerado tamb�m um outro arquivo <b>guiasContabilizadaCoparticipacao.csv</b> com todas as guias que foram
 * consideradas para o c�lculo das coparticipa��es.
 * @author Diogo Vin�cius
 *
 */
public class GeradorArquivoCoparticipacaoBoleto {
	
	/**
	 * M�todo principal
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		gerarArquivo();
	}
	
	/**
	 * Classe interna para encapsular os par�metros para a cria��o do arquivo.
	 * @author Diogo Vin�cius
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
	
	/**
	 * Cole��o de guias que entraram no c�lculo das coparticipa��es
	 */
	private static Set<GuiaSimples> guias = new HashSet<GuiaSimples>();
	
	/**
	 * M�todo principal que gerar� o arquivo <b>coparticipacao.csv</b> com as informa��es necess�rias e o 
	 * arquivo <b>guiasContabilizadaCoparticipacao.cvs</b> que conter� todas as guias calculadas.
	 * @param args
	 * @throws Exception
	 */
	public static void gerarArquivo() throws Exception {
		
		File arquivo = new File("coparticipacao.csv");
		if (arquivo.exists()) {
			arquivo.delete();
		}
		FileWriter writer = new FileWriter(arquivo, true);
		PrintWriter saida = new PrintWriter(writer, true);
		FileParameters fileParameters = new FileParameters(arquivo, writer, saida);
//		fileParameters.saida.print("EMPRESA;");
//		fileParameters.saida.print("MATR�CULA;");
		fileParameters.saida.print("CART�O;");
		fileParameters.saida.print("CPF;");
		fileParameters.saida.print("NOME;");
		fileParameters.saida.print("TIPO;");
		fileParameters.saida.print("COMPET�NCIA;");
		fileParameters.saida.print("VALOR COPARTICIPA��O");
		fileParameters.saida.println();
		
		System.out.println("Buscando titulares c/ matr�culas boleto...");
		Set<TitularFinanceiroSR> titularesComBoletos = GeracaoBoletosDAO.buscarTitulares();
		System.out.println("Buscando dependentes suplementares...");
		Set<DependenteSuplementar> dependentesSuplementares = GeracaoBoletosDAO.buscarDependentesSuplementares();

		System.out.println("Processando m�s de Fevereiro...");
		calcularCoparticipacoes(fileParameters, Utils.createData("01/04/2008"), Utils.createData("30/04/2008"), titularesComBoletos);
		calcularCoparticipacoes(fileParameters, Utils.createData("01/04/2008"), Utils.createData("30/04/2008"), dependentesSuplementares);
		
		System.out.println("Arquivos gerados!");
		
		fileParameters.saida.close();
		fileParameters.writer.close();
		
		arquivo = new File("guiasContabilizadaCoparticipacao.csv");
		writer = new FileWriter(arquivo, true);
		saida = new PrintWriter(writer, true);
		fileParameters = new FileParameters(arquivo, writer, saida);
		for (GuiaSimples guia : guias) {
			fileParameters.saida.println(guia.getAutorizacao());	
		}
		fileParameters.saida.close();
		fileParameters.writer.close();
		
	}
	
	/**
	 * Calcula a coparticipa��o de todos os titulares respeitando o teto de coparticipa��o.
	 * @param fileParameters
	 * @param dataLimite
	 * @param titulares
	 * @throws Exception
	 */
	private static void calcularCoparticipacoes(FileParameters fileParameters, Date dataInicial, Date dataLimite, Set<? extends TitularFinanceiroSR> titulares) throws Exception {
//		String empresa = "";
//		String matricula = "";
		String cartao = "";
		String cpf = "";
		String nome ="";
		
		for (TitularFinanceiroSR titular : titulares) {
			
			BigDecimal valorTotal = BigDecimal.ZERO;
			BigDecimal valorTitular = BigDecimal.ZERO;
			for (GuiaSimples guia : titular.getGuiasAptasAoCalculoDeCoparticipacao(dataInicial, dataLimite)) {
				valorTitular = valorTitular.add(guia.getValorCoParticipacao());
				guias.add(guia);
				guia.setFluxoFinanceiro(new Cobranca());
			}
			valorTotal = valorTotal.add(ContaMatricula.verificaTetoCoparticipacao(valorTitular));
			
			//Dependente suplementar � inclu�do em uma linha do arquivo separada do titular 
			if (titular.isSeguradoDependenteSuplementar()) {
				cartao = titular.getNumeroDoCartao();
				BigDecimal valorDependenteSuplementar = BigDecimal.ZERO;
				for (GuiaSimples guia : titular.getGuiasAptasAoCalculoDeCoparticipacao(dataInicial, dataLimite)) {
					valorDependenteSuplementar = valorDependenteSuplementar.add(guia.getValorCoParticipacao());
					guias.add(guia);
					guia.setFluxoFinanceiro(new Cobranca());
				}
				if (valorDependenteSuplementar.compareTo(BigDecimal.ZERO) != 0) {
					cpf = titular.getPessoaFisica().getCpf()!= null? titular.getPessoaFisica().getCpf() : "";
					nome = titular.getPessoaFisica().getNome();
					setInFile(fileParameters, cartao,  cpf, nome, titular.getTipoDeSegurado(), dataLimite, valorDependenteSuplementar);
				}
			}
			else {
				for (DependenteInterface dependente : titular.getDependentes()) {
					//Dependente normal tem o seu valor inclu�do junto ao seu titular
					BigDecimal valorDependenteNormal = BigDecimal.ZERO;
					for (GuiaSimples guia : dependente.getGuiasAptasAoCalculoDeCoparticipacao(dataInicial, dataLimite)) {
						valorDependenteNormal = valorDependenteNormal.add(guia.getValorCoParticipacao());
						guias.add(guia);
						guia.setFluxoFinanceiro(new Cobranca());
					}
					valorTotal = valorTotal.add(ContaMatricula.verificaTetoCoparticipacao(valorDependenteNormal));
				}
			}
			if (valorTotal.compareTo(BigDecimal.ZERO) != 0) {
//				if (titular.getMatriculaMaiorSalario() != null) {
//					empresa = titular.getMatriculaMaiorSalario().getEmpresa().getDescricao();
//					matricula = titular.getMatriculaMaiorSalario().getDescricao();
//				}
				cartao = titular.getNumeroDoCartao();
				cpf = titular.getPessoaFisica().getCpf()!= null? titular.getPessoaFisica().getCpf() : "";
				nome = titular.getPessoaFisica().getNome();
				setInFile(fileParameters, cartao, cpf, nome, titular.getTipoDeSegurado(), dataLimite, valorTotal);
			}
		}
		
	}
	
	/**
	 * Seta no arquivo uma nova linha com os dados passados como par�metro.
	 * @param fileParameters
	 * @param titular
	 * @param competencia
	 * @param valorCoparticipacao
	 */
	private static void setInFile (FileParameters fileParameters, String cartao, String cpf, String nome, String tipoSegurado, Date competencia, BigDecimal valorCoparticipacao) {
//		fileParameters.saida.print(empresa + ";");
//		fileParameters.saida.print(matricula + ";");
		fileParameters.saida.print(cartao + ";");
		fileParameters.saida.print(cpf + ";");
		fileParameters.saida.print(nome + ";");
		fileParameters.saida.print(tipoSegurado + ";");
		fileParameters.saida.print(Utils.format(competencia, "MM/yyyy") + ";");
		fileParameters.saida.print(valorCoparticipacao);
		fileParameters.saida.println();
	}
	
}
