///**
// * 
// */
//package br.com.infowaypi.ecare.financeiro.consignacao;
//
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.math.BigDecimal;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Scanner;
//import java.util.Set;
//import java.util.StringTokenizer;
//
//import org.apache.tools.ant.types.CommandlineJava.SysProperties;
//import org.junit.Test;
//
//import br.com.infowaypi.ecare.financeiro.arquivo.ArquivoDeEnvioConsignacao;
//import br.com.infowaypi.ecare.financeiro.arquivo.ContaConsignacao;
//import br.com.infowaypi.ecare.financeiro.conta.ContaMatricula;
//import br.com.infowaypi.ecare.segurados.Empresa;
//import br.com.infowaypi.ecare.segurados.Matricula;
//import br.com.infowaypi.ecare.segurados.TitularFinanceiroSR;
//import br.com.infowaypi.ecarebc.financeiro.arquivos.ArquivoInterface;
//import br.com.infowaypi.ecarebc.financeiro.consignacao.Consignacao;
//import br.com.infowaypi.ecarebc.financeiro.conta.Conta;
//import br.com.infowaypi.ecarebc.financeiro.conta.ContaInterface;
//import br.com.infowaypi.molecular.SearchAgent;
//import br.com.infowaypi.molecular.parameter.Equals;
//import br.com.infowaypi.msr.utils.Utils;
//
//
///**
// * @author Marcus bOolean
// *
// */
//public class CorrecaoCobrancasDuplicadas {
//	
//	
//	private static final Date COMPETENCIA = Utils.parse("01/12/2007");
//	private Map<String, Set<AgrupamentoValores>> mapaValoresPorEmpresa = new HashMap<String, Set<AgrupamentoValores>>();
//	
//	@Test
//	public void execute1() throws Exception {
//		Scanner scanner = new Scanner(new FileReader("C:\\Arquivo_Restituicao.csv"));
//		
//		while(scanner.hasNext()) {
//			String linha = scanner.nextLine();
//			StringTokenizer token = new StringTokenizer(linha,";");
//			
//			String codigoEmpresa = token.nextToken();
//			String codigoMatricula = token.nextToken();
//			String cpf = token.nextToken();
//			String valorCoparticipacao = token.nextToken();
//			String valorDevolvido = token.nextToken();
//			String valorRestituicao = token.nextToken();
//	
//			
//			AgrupamentoValores valores = new AgrupamentoValores();
//			
//			valores.setCodigoEmpresa(codigoEmpresa);
//			valores.setCodigoMatricula(codigoMatricula);
//			valores.setCpf(cpf);
//			valores.setValorCoParticipacao(valorCoparticipacao);
//			valores.setValorDevolvido(valorDevolvido);
//			valores.setValorDeRestituicao(valorRestituicao);
//			
//			
//			if(mapaValoresPorEmpresa.keySet().contains(codigoEmpresa)) {
//				mapaValoresPorEmpresa.get(codigoEmpresa).add(valores);
//			}else {
//				Set<AgrupamentoValores> set = new HashSet<AgrupamentoValores>();
//				set.add(valores);
//				mapaValoresPorEmpresa.put(codigoEmpresa, set);
//			}
//		}
//		
//		processaValores();
//	}
//
//
//	private void processaValores() throws Exception {
//		SearchAgent sa = new SearchAgent();
//		Set<Conta> contas = new HashSet<Conta>();
//		
//		for (String  codigoEmpresa : mapaValoresPorEmpresa.keySet()) {
//			sa.clearAllParameters();
//			sa.addParameter(new Equals("codigoLegado", codigoEmpresa));
//			Empresa empresa = sa.uniqueResult(Empresa.class);
//			ArquivoDeEnvioConsignacao arquivoEmpresa = new ArquivoDeEnvioConsignacao();
//			arquivoEmpresa.setEmpresa(empresa);
//			arquivoEmpresa.setCompetencia(COMPETENCIA);
//			arquivoEmpresa.setTipoDeArquivo(ArquivoInterface.REMESSA);
//			arquivoEmpresa.setStatusArquivo(ArquivoInterface.ARQUIVO_ENVIADO);
//			
//			for (AgrupamentoValores agrupamentoValores : mapaValoresPorEmpresa.get(codigoEmpresa)) {
//				sa.clearAllParameters();
//				
//				sa.addParameter(new Equals("pessoaFisica.cpf", agrupamentoValores.getCpf()));
//				TitularFinanceiroSR titular = sa.uniqueResult(TitularFinanceiroSR.class);
//				
//				if(titular == null)
//					System.out.println("CPF: "+agrupamentoValores.getCpf());
//				
//				Matricula matriculaCorreta = null;
//				for (Matricula matricula : titular.getMatriculas()) {
//					if(matricula.getEmpresa().getCodigoLegado().equals(agrupamentoValores.getCodigoEmpresa())
//						&& matricula.getDescricao().equals(agrupamentoValores.getCodigoMatricula())){
//						matriculaCorreta = matricula;
//						break;
//					}
//						
//				}
//				
//				if(matriculaCorreta.getConsignacao(COMPETENCIA) == null){
//					System.out.println("MATRICULA: "+matriculaCorreta.getDescricao());
//					System.out.println("CPF: "+titular.getPessoaFisica().getCpf());
//				}
//				
//				ContaConsignacao conta = (ContaConsignacao) matriculaCorreta.getConsignacao(COMPETENCIA).getConta();
//				
//				conta.setValorFinanciamento(BigDecimal.ZERO);
//				conta.setValorCoparticipacao(agrupamentoValores.getValorDeRestituicao());
//				conta.setEmpresa(matriculaCorreta.getEmpresa());
//				conta.setPercentualAliquota(BigDecimal.ZERO);
//					
//				arquivoEmpresa.populate(conta, null);
//			}
//			
//			if(!arquivoEmpresa.getContas().isEmpty())
//				arquivoEmpresa.criarArquivo();
//			else 
//				System.out.println("Não foi gerado arquivo para a empresa: " + empresa.getDescricao());
//			System.out.println("Fim Geracao de arquivo  " + empresa.getDescricao() + " : "  + Utils.format(new Date(),"HH:mm:ss"));
//		}
//		
//	}
//}
