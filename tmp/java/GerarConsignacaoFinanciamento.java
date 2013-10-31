//package br.com.infowaypi.ecare.financeiro.consignacao;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.LineNumberReader;
//import java.math.BigDecimal;
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collections;
//import java.util.Date;
//import java.util.GregorianCalendar;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.StringTokenizer;
//
//import org.apache.commons.lang.StringUtils;
//
//import br.com.infowaypi.ecare.financeiro.arquivo.ArquivoDeEnvioConsignacao;
//import br.com.infowaypi.ecare.financeiro.arquivo.ContaConsignacao;
//import br.com.infowaypi.ecare.segurados.DependenteInterface;
//import br.com.infowaypi.ecare.segurados.Empresa;
//import br.com.infowaypi.ecare.segurados.Matricula;
//import br.com.infowaypi.ecare.segurados.TitularFinanceiroSR;
//import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
//import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
//import br.com.infowaypi.ecarebc.financeiro.arquivos.ArquivoInterface;
//import br.com.infowaypi.ecarebc.financeiro.consignacao.Consignacao;
//import br.com.infowaypi.ecarebc.financeiro.conta.ComponenteColecaoContas;
//import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
//import br.com.infowaypi.molecular.ImplDAO;
//import br.com.infowaypi.molecular.SearchAgent;
//import br.com.infowaypi.molecular.TransactionManagerHibernate;
//import br.com.infowaypi.molecular.parameter.Equals;
//import br.com.infowaypi.molecular.parameter.In;
//import br.com.infowaypi.msr.utils.Utils;
//
///**
// * @author Rondinele
//*/
//public class GerarConsignacaoFinanciamento {
//	private static int quantMatriculasNaoEncontradas = 0;
//	private static int quantConsignacoesMatriculas = 0;
//	private static int quantConsignacoesMatriculasNaoZeradas = 0;
//	private static 	Map<String, Empresa> empresas = new HashMap<String, Empresa>();
//	private static 	Map<Empresa, List<ContaConsignacao>> consignacoesPorEmpresa = new HashMap<Empresa, List<ContaConsignacao>>();
//	
//	private static 	Map<TitularFinanceiroSR, List<GuiaSimples<Procedimento>>> guiasPorTitular = new HashMap<TitularFinanceiroSR, List<GuiaSimples<Procedimento>>>();
//	
//	private static BigDecimal valorTotalConsignacoes = BigDecimal.ZERO;
//	private static BigDecimal valorTotalCoparticipacao = BigDecimal.ZERO;
//	private static List<Consignacao> consignacoesMatricula = new ArrayList<Consignacao>();
//	private static List<Consignacao> consignacoesSegurado = new ArrayList<Consignacao>();
//	
//	public static void main(String[] args) throws Exception {
//		empresas = getEmpresas();
//		valorTotalConsignacoes.setScale(2, BigDecimal.ROUND_HALF_UP);
//		File abstractFile = new File("arquivos\\arq-vrb-933.csv");
//		String path = abstractFile.getAbsolutePath();
//
//		Map<String, List<String[]>> map = getArquivoAgrupadoPorCpf(path,3);
//		List<String> cpfs = new ArrayList<String>();
//		cpfs.addAll(map.keySet());
//		System.out.println();
//		System.out.println("Cpfs filtrados: " + cpfs.size());
//		System.out.println();
//		System.out.println("Inicio da operação: " + Utils.format(new Date(),"HH:mm:ss"));
//		long inicio = System.nanoTime();
//		
//		Calendar calendar = new GregorianCalendar();
//		Date competencia = Utils.getCompetencia("09/2007");
//		calendar.setTime(competencia);
//		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
//		Date dataPagamento = calendar.getTime();
//		List<TitularFinanceiroSR> titulares = getTitulares(cpfs);
//		Set<String> tits = new HashSet<String>();
//		
//		Set<String> cpfsDupls = new HashSet<String>();
//		System.out.println("Matrículas não encontradas:");
//		TransactionManagerHibernate tm = new TransactionManagerHibernate();
//		tm.beginTransaction();
//		System.out.println();
//		System.out.println("Geração de consignações: " + Utils.format(new Date(),"HH:mm:ss"));
//		int numCpfsNaoINclusos = 0;
//		
//		for(TitularFinanceiroSR titular : titulares){
//			if(map.get(titular.getPessoaFisica().getCpf()) != null){
//				if(tits.add(titular.getPessoaFisica().getCpf()))
//					gerarConsignacao(titular, map.get(titular.getPessoaFisica().getCpf()), competencia, dataPagamento);
//				else
//					cpfsDupls.add(titular.getPessoaFisica().getCpf());
//			
//				ImplDAO.save(titular);
//			}else numCpfsNaoINclusos++;
//		}
//		
//		System.out.println("Fim Geração de consignações: " + Utils.format(new Date(),"HH:mm:ss"));
//		System.out.println();
//		int numeroDeTitularesInexistentes = 0;
//		
//		for(String cpfMap : cpfs){
//			if(!tits.contains(cpfMap)){
//				numeroDeTitularesInexistentes++;
////				System.out.println("Cpf não encontrado: " + cpfMap+";");
//			}
//		}
//		
//		System.out.println("Geracao de arquivo: " + Utils.format(new Date(),"HH:mm:ss"));
//		gerarArquivosPorEmpresa(competencia);
//		System.out.println("Fim Geracao de arquivo: " + Utils.format(new Date(),"HH:mm:ss"));
//		
//		tm.rollback();
//		
//		long fim = System.nanoTime();
//		System.out.println("Fim: " + Utils.format(new Date(),"HH:mm:ss"));
//		System.out.println();
//		System.out.println("###################");
//		System.out.println("Tempo decorrido da pesquisa: " + (fim - inicio)/1000000000);
//		System.out.println();
//		System.out.println("Valor total das consignações geradas: " + (new DecimalFormat("######0.00")).format(valorTotalConsignacoes));
//		System.out.println("Valor total das coparticipações geradas: " + (new DecimalFormat("######0.00")).format(valorTotalCoparticipacao));
//		System.out.println();
//		System.out.println("Titulares encontrados: " + titulares.size());
//		System.out.println("Titulares encontrados 2: " + tits.size());
//		System.out.println("Titulares encontrados 3: " + cpfsDupls.size());
//		System.out.println("Titulares não encontrados: " + numeroDeTitularesInexistentes);
//		System.out.println("Matrículas não encontradas: " + quantMatriculasNaoEncontradas);
//		System.out.println("Consignações mat criadas: "  + quantConsignacoesMatriculas);
//		System.out.println("Consignações mat criadas não zeradas: "  + quantConsignacoesMatriculasNaoZeradas);
//		System.out.println("Quantidade de contas: " + consignacoesPorEmpresa.values().size());
//		System.out.println("Empresas: " + consignacoesPorEmpresa.keySet().size());
//		System.out.println("Consignações segurado: "  + consignacoesSegurado.size());
//		System.out.println("Consignações mat: "  + consignacoesMatricula.size());
//		System.out.println("Num de cpfs não inclusos: "  + numCpfsNaoINclusos);
//	}
//
//	private static void gerarArquivosPorEmpresa(Date competencia) throws Exception {
//		for(Empresa empresa : consignacoesPorEmpresa.keySet()){
//			System.out.println("Geracao de arquivo  " + empresa.getDescricao() + " : "  + Utils.format(new Date(),"HH:mm:ss"));
//			ArquivoDeEnvioConsignacao arquivoEmpresa = new ArquivoDeEnvioConsignacao();
//			arquivoEmpresa.setEmpresa(empresa);
//			arquivoEmpresa.setCompetencia(competencia);
//			arquivoEmpresa.setTipoDeArquivo(ArquivoInterface.REMESSA);
//			arquivoEmpresa.setStatusArquivo(ArquivoInterface.ARQUIVO_ENVIADO);
//			for(ContaConsignacao contaEmpresa : consignacoesPorEmpresa.get(empresa)){
//				arquivoEmpresa.populate(contaEmpresa, null);
//			}
//			ImplDAO.save(arquivoEmpresa);
//			if(!arquivoEmpresa.getContas().isEmpty())
//				arquivoEmpresa.criarArquivo();
//			else 
//				System.out.println("Não foi gerado arquivo para a empresa: " + empresa.getDescricao());
//			System.out.println("Fim Geracao de arquivo  " + empresa.getDescricao() + " : "  + Utils.format(new Date(),"HH:mm:ss"));
//		}
//	}
//
//	@SuppressWarnings("unchecked")
//	private static Map<String, Empresa> getEmpresas() {
//		List<Empresa> empresas = ImplDAO.findByParam(new HashSet(), Empresa.class);
//		Map<String, Empresa> mapEmpresas = new HashMap<String, Empresa>();
//		for (Empresa empresa : empresas) {
//			mapEmpresas.put(empresa.getCodigoLegado(), empresa);
//		}
//		return mapEmpresas;
//	}
//
//	@SuppressWarnings("unchecked")
//	private static void gerarConsignacao(TitularFinanceiroSR titular, List<String[]> valores, Date competencia, Date dataPagamento) throws Exception{
//		BigDecimal valorTotalFinanciamentoDependentes = BigDecimal.ZERO;
//		BigDecimal valorTotalFinanciamentoTitular = BigDecimal.ZERO;
//		BigDecimal limiteFinanciamentoTitular = new BigDecimal(133);
//		BigDecimal limiteBase = new BigDecimal(3800);
//		BigDecimal salarioBase = BigDecimal.ZERO;
//		BigDecimal salarioTotal = BigDecimal.ZERO;
//		salarioTotal.setScale(2, BigDecimal.ROUND_HALF_UP);
//		
//		Consignacao consignacao = new ConsignacaoSegurado();
//		consignacao.setCompetencia(competencia);
//		
//		consignacao.setStatusConsignacao('P');
//		consignacao.setTitular(titular);
//		consignacao.setValorCoparticipacao(BigDecimal.ZERO);
//		consignacao.setValorFinanciamento(BigDecimal.ZERO);
//		consignacao.setValorPrevidencia(BigDecimal.ZERO);
//		ComponenteColecaoContas colecaoContas = new ComponenteColecaoContas();
//		consignacao.setColecaoContas(colecaoContas);
//		consignacao.setDataDoCredito(dataPagamento);
//		consignacao.setDataPagamento(dataPagamento);
//		consignacoesSegurado.add(consignacao);
//		titular.getConsignacoes().add(consignacao);
//		
//		Map<Integer, DependenteInterface> ordemDependentes = titular.ordenaDependentes();
//		String matriculaMaiorSalario = getMatriculaMaiorSalario(valores, titular.getMatriculas());	
//		List<String> matriculasPagamentoTipoFolha = getMatriculasTipoFolha(titular);
//		
//		System.out.println("");
//		System.out.println("################");
//		System.out.println("Situação titular: " + titular.getSituacao().getDescricao());
//		int numMat = 0;
//		for(String[] valor : valores){
//			for(Matricula matricula : titular.getMatriculas()){
//				if(matricula.getDescricao().equals(valor[1]) && matricula.getTipoPagamento().equals(Matricula.FORMA_PAGAMENTO_FOLHA)){
//					quantConsignacoesMatriculas++;
//					numMat++;
//					String valorCalculado = StringUtils.replace(valor[2],",",".");
//					matricula.setSalario(new BigDecimal(Double.valueOf(valorCalculado)).divide(new BigDecimal(100)));
//					BigDecimal valorFinanciamento = BigDecimal.ZERO;
//					BigDecimal valorFinanciamentoTitular = BigDecimal.ZERO;
//					BigDecimal aliquotaTitularTotal = BigDecimal.ZERO;
//					aliquotaTitularTotal.setScale(2, BigDecimal.ROUND_HALF_UP);
//					valorFinanciamentoTitular.setScale(2, BigDecimal.ROUND_HALF_UP);
//					BigDecimal valorFinanciamentoDependentes = BigDecimal.ZERO;
//					valorFinanciamentoDependentes.setScale(2, BigDecimal.ROUND_HALF_UP);
//					valorFinanciamento.setScale(2, BigDecimal.ROUND_HALF_UP);
//				
//
//					//Verifica se o salário atingiu o teto máximo, retornando o salário correspondente para o cálculo do financiamento
//					salarioBase = getSalarioMaximo(limiteBase, salarioTotal, matricula);
//						 
//					//Cálculo do financiamento do titular
//					if(valorTotalFinanciamentoTitular.compareTo(limiteFinanciamentoTitular) < 0){
//						BigDecimal[] aliquotaValorTit = titular.getValorFinanciamento(salarioBase);
//						aliquotaTitularTotal = aliquotaTitularTotal.add(aliquotaValorTit[0]);
//						valorFinanciamentoTitular = aliquotaValorTit[1];
//						valorTotalFinanciamentoTitular = valorTotalFinanciamentoTitular.add(valorFinanciamentoTitular);
//						
//						if(valorTotalFinanciamentoTitular.compareTo(limiteFinanciamentoTitular) >= 0){
//						valorFinanciamento = limiteFinanciamentoTitular;
//							valorTotalFinanciamentoTitular = limiteFinanciamentoTitular;
//						}
//						else
//							valorFinanciamento = valorFinanciamento.add(valorFinanciamentoTitular);
//					}
//					
//					System.out.println("Matricula: " + matricula.getDescricao());
//					System.out.println();
//					System.out.println("Qt Matriculas: " + titular.getMatriculas().size() + " Qt Dependentes: " + titular.getDependentes().size());
//					System.out.println();	
//					System.out.println(" Salário Base: " + (new DecimalFormat("######0.00")).format(salarioBase) + 
//							" Salário Titular: " +(new DecimalFormat("######0.00")).format(matricula.getSalario()));
//					
//					if(!titular.getDependentes().isEmpty()){
//						System.out.println();
//						System.out.println("Dependentes:");
//					}
//					
//					//Cálculo do financiamento do dependente
//					for(Integer ordem : ordemDependentes.keySet()){
//						DependenteInterface dep = ordemDependentes.get(ordem);
//						BigDecimal[] aliquotaValorDep = dep.getValorFinanciamentoDependente(salarioBase);
//						aliquotaTitularTotal = aliquotaTitularTotal.add(aliquotaValorDep[0]);
//						valorFinanciamentoDependentes = valorFinanciamentoDependentes.add(aliquotaValorDep[1]);
//						valorTotalFinanciamentoDependentes = valorTotalFinanciamentoDependentes.add(aliquotaValorDep[1]);
//						valorFinanciamento = valorFinanciamento.add(aliquotaValorDep[1]);
//						System.out.println("Nome: "+ dep.getPessoaFisica().getNome()+" Ordem: " + ordem + " Idade: " + dep.getPessoaFisica().getIdade() + " Financiamento: " + aliquotaValorDep[1] + " Porcentagem financimento: " + aliquotaValorDep[0].setScale(1,BigDecimal.ROUND_HALF_UP));
//					}
//					
//					BigDecimal valorCoparticipacao = BigDecimal.ZERO;
//					
//					if(valorFinanciamento.compareTo(BigDecimal.ZERO) > 0){
//						quantConsignacoesMatriculasNaoZeradas++;
//						valorTotalConsignacoes = valorTotalConsignacoes.add(valorFinanciamento);
//						consignacao.setValorFinanciamento(consignacao.getValorFinanciamento().add(valorFinanciamento));
//						
//						if(matricula.getDescricao().equals(matriculaMaiorSalario)){
//							valorCoparticipacao = titular.getValorCoparticipacao(consignacao, ajustarCompetencia(competencia) );
//						}
//						valorTotalCoparticipacao = valorTotalCoparticipacao.add(valorCoparticipacao);
//						ContaConsignacao contaGerada = gerarConta(matricula, valorFinanciamento,valorCoparticipacao,aliquotaTitularTotal, competencia, dataPagamento);
//						contaGerada.setColecaoContas(colecaoContas);
//						
//						//Agrupa as contas por empresa
//						if(!consignacoesPorEmpresa.containsKey(contaGerada.getEmpresa())){
//							List<ContaConsignacao> contas = new ArrayList<ContaConsignacao>();
//							contas.add(contaGerada);
//							consignacoesPorEmpresa.put(contaGerada.getEmpresa(), contas);
//						}else{
//							consignacoesPorEmpresa.get(contaGerada.getEmpresa()).add(contaGerada);
//						}
//					}
//
//					System.out.println();
//					System.out.println("Titular: " + titular.getPessoaFisica().getCpf().replace(".", "").replace("-", "") + 
//							" Valor financimento Titular: " + (new DecimalFormat("######0.00")).format(valorFinanciamentoTitular) +
//							" Valor financimento Dependente: " + (new DecimalFormat("######0.00")).format(valorFinanciamentoDependentes)+
//							" Valor total financimento Titular: " + (new DecimalFormat("######0.00")).format(valorTotalFinanciamentoTitular) +
//							" Valor financimento Total da Matricula: " + (new DecimalFormat("######0.00")).format(valorFinanciamento) + 
//							" Valor Coparticipacao: " + (new DecimalFormat("######0.00")).format(valorCoparticipacao));
//					
//				}
//			}
//			if(!matriculasPagamentoTipoFolha.contains(valor[1])){
//				quantMatriculasNaoEncontradas++;
//			}
//		}
//		
//	}
//
//	/**
//	 * @param limiteBase
//	 * @param matricula
//	 * @return
//	 */
//	public static BigDecimal getSalarioMaximo(BigDecimal limiteBase, BigDecimal salarioTotal, Matricula matricula) {
//		BigDecimal salarioBase;
//
//		if(salarioTotal.compareTo(limiteBase) < 0){
//			if(salarioTotal.add(matricula.getSalario()).compareTo(limiteBase) < 0){
//				salarioTotal = salarioTotal.add(matricula.getSalario());
//				salarioBase = matricula.getSalario();
//			}
//			else {
//				salarioBase = limiteBase.add(salarioTotal.negate());
//				salarioTotal = limiteBase;
//			}
//		}
//		else
//			salarioBase = BigDecimal.ZERO;
//		return salarioBase;
//	}
//
//	/**
//	 * @param titular
//	 * @return
//	 */
//	private static List<String> getMatriculasTipoFolha(
//			TitularFinanceiroSR titular) {
//		List<String> matriculas = new ArrayList<String>();
//		for(Matricula matricula : titular.getMatriculas()){
//			if(matricula.getTipoPagamento().equals(Matricula.FORMA_PAGAMENTO_FOLHA))
//				matriculas.add(matricula.getDescricao());
//		}
//		return matriculas;
//	}
//
//	/**
//	 * @param valores
//	 * @param mats
//	 * @return
//	 */
//	private static String getMatriculaMaiorSalario(List<String[]> valores,
//			Set<Matricula> mats) {
//		String matriculaMaiorSalario = "";
//		BigDecimal maiorSalarioMatricula = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
//		for(String[] valor : valores){
//			for(Matricula matricula : mats){
//				if(matricula.getTipoPagamento() == null)
//					matricula.setTipoPagamento(Matricula.FORMA_PAGAMENTO_FOLHA);
//				
//				if(matricula.getDescricao().equals(valor[1]) && matricula.getTipoPagamento().equals(Matricula.FORMA_PAGAMENTO_FOLHA)){
//					String valorCalculado = StringUtils.replace(valor[2],",",".");
//					BigDecimal salario = new BigDecimal(Double.valueOf(valorCalculado)).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
//					if(salario.compareTo(maiorSalarioMatricula) > 0){
//						maiorSalarioMatricula = salario;
//						matriculaMaiorSalario = valor[1];
//					}
//				}
//			}
//		}
//		return matriculaMaiorSalario;
//	}
//
//	private static ContaConsignacao gerarConta(Matricula matricula, BigDecimal valorFinanciamento,BigDecimal valorCoparticipacao,BigDecimal aliquota, Date competencia, Date dataPagamento) throws Exception {
//		ConsignacaoMatricula consignacao = new ConsignacaoMatricula();
//		consignacao.setCompetencia(competencia);
//		consignacao.setStatusConsignacao('P');
//		consignacao.setMatricula(matricula);
//		consignacao.setDataDoCredito(dataPagamento);
//		consignacao.setDataPagamento(dataPagamento);
//		consignacao.setMatriculaNoEstado(matricula.getDescricao());
//		consignacao.setValorFinanciamento(valorFinanciamento);
//		consignacao.setValorCoparticipacao(valorCoparticipacao);
//		
//		ContaConsignacao conta = new ContaConsignacao();
////		colecaoContas.add(conta);
//		conta.setCompetencia(competencia);
//		conta.setDataPagamento(dataPagamento);
//		conta.setIdentificadorTitular(matricula.getDescricao());
//		conta.setValorFinanciamento(valorFinanciamento);
//		conta.setValorCoparticipacao(BigDecimal.ZERO);
//		conta.setPercentualAliquota(aliquota);
//		conta.setEmpresa(matricula.getEmpresa());
//		conta.setValorCoparticipacao(valorCoparticipacao);
//		consignacao.setConta(conta);
////		ImplDAO.save(consignacao);
//		matricula.getConsignacoes().add(consignacao);
//		consignacoesMatricula.add(consignacao);
//		return conta;
//	}
//	
//	@SuppressWarnings("unchecked")
//	public static List<TitularFinanceiroSR> getTitulares(List<String> cpfs ){
//		SearchAgent sa = new SearchAgent();
//		sa.addParameter(new In("pessoaFisica.cpf", cpfs));
//		sa.addParameter(new Equals("situacao.descricao","Ativo(a)"));
//		return sa.list(TitularFinanceiroSR.class);
//
//	}
//	
//	/**
//	 * @throws IOException
//	 * @return Map
//	 * contendo como chave o cpf do segurado e uma lista de array como valores<br>
//	 * O array contém os seguintes dados:
//	 * <li><b>index 0</b> Códido da Empresa;</li>
//	 * <li><b>index 1</b> Matrícula do segurado;</li>
//	 * <li><b>index 2</b> Salário do segurado;</li>
//	*/
//
//	public static Map<String, List<String[]>> getArquivoAgrupadoPorCpf(String path, int chaveCpf) throws IOException{
//		int numeroDeCpfsComDuasMatriculas = 0;
//		LineNumberReader buffer = new LineNumberReader(new InputStreamReader(new FileInputStream(path)));
//		String linha = "";
//		Map<String, List<String[]>> map = new HashMap<String, List<String[]>>();
//		Map<String, List<String[]>> mapCpfsDuplicados = new HashMap<String, List<String[]>>();
//		while((linha = buffer.readLine()) != null){
//			if(buffer.getLineNumber() != 1){
//				StringTokenizer tokens = new StringTokenizer(linha,";");
//				String[] fields = new String[tokens.countTokens() - 1];
//				int index = 0;
//				int count = 1;
//				String cpf = "";
//				while(tokens.hasMoreTokens()){
//					if(count == chaveCpf){
//						cpf = Utils.applyMask(StringUtils.leftPad(tokens.nextToken().trim(),11,"0"), "###.###.###-##");
//						count++;
//					}else{
//						fields[index] = tokens.nextToken().trim();
//						count++;
//						index++;
//					}
//				}
//				
//				if(map.containsKey(cpf)){
//					if(mapCpfsDuplicados.containsKey(cpf)){
//						if(fields[0].equals("1") || fields[0].equals("01"))
//							mapCpfsDuplicados.get(cpf).add(fields);
//					}else{
//						if(fields[0].equals("1") || fields[0].equals("01")){
//							List<String[]> list = new ArrayList<String[]>();
//							list.add(fields);
//							mapCpfsDuplicados.put(cpf, list);
//						}
//					}
//					numeroDeCpfsComDuasMatriculas++;
//					if(fields[0].equals("1") || fields[0].equals("01"))
//						map.get(cpf).add(fields);
//				}else{
//					if(fields[0].equals("1") || fields[0].equals("01")){
//						List<String[]> list = new ArrayList<String[]>();
//						list.add(fields);
//						map.put(cpf, list);
//					}
//				}
//			}
//		}
//		
//		System.out.println();
//		System.out.println("NUmero de cpfs com duas ou mais matriculas " + numeroDeCpfsComDuasMatriculas);
//		System.out.println("Quantidade de registros: " + (buffer.getLineNumber() - 1));
//
//		return map;
//	}
//	
//
//	
//	private static Date ajustarCompetencia(Date competencia) {
//		GregorianCalendar calendario = new GregorianCalendar();
//		calendario.setTime(competencia);
//		calendario.set(Calendar.MONTH, calendario.get(Calendar.MONTH) - 1);
//		calendario.set(Calendar.DAY_OF_MONTH, calendario.getActualMaximum(Calendar.DATE));
//		return calendario.getTime();
//	}
//
//}
