package br.com.infowaypi.ecare.scheduller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecare.financeiro.consignacao.ConsignacaoMatriculaInterface;
import br.com.infowaypi.ecare.financeiro.consignacao.ResumoRetorno;
import br.com.infowaypi.ecare.financeiro.consignacao.tuning.ConsignacaoMatriculaTuning;
import br.com.infowaypi.ecare.financeiro.consignacao.tuning.ConsignacaoSeguradoTuning;
import br.com.infowaypi.ecare.segurados.AbstractMatricula;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.segurados.tuning.MatriculaTuning;
import br.com.infowaypi.ecare.segurados.tuning.TitularConsignacaoTuning;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.arquivos.ArquivoInterface;
import br.com.infowaypi.ecarebc.financeiro.consignacao.Consignacao;
import br.com.infowaypi.ecarebc.financeiro.conta.ComponenteColecaoContas;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Service de processamento de retorno de consignação.
 */
public class ProcessaRetornoConsignacaoOld {
	
	public final static String VERBA_FINANCIAMENTO 		= "777";
	public final static String VERBA_FINANCIAMENTO_URB 	= "877";
	public final static String VERBA_COPARTICIPACAO 	= "755";
	public final static String VERBA_PARCELAMENTO 		= "779";
	public final static String VERBA_PARCELAMENTO_URB 	= "879";
	
	/**
	 * Atributos que atualizam a barra de progresso.
	 * A barra de progresso (ProcessarRetornoConsignacaoProgressBar)
	 * lê esses atributos para se atualizar.
	 */
	public int progressoMaximo = 100;
	public int progressoAtual = 0;
	
	public String progessTitulo = "";
	public String progessStatus = "";
	
	/**
	 * Primeiro passo do fluxo de processamento de retorno de consignação.
	 */
	public ResumoRetorno informarArquivo(byte[] conteudoArquivo, Date competencia, Date dataPagamento) throws Exception {
		ResumoRetorno resumo = new ResumoRetorno();
		resumo.setConteudoArquivo(conteudoArquivo);
		resumo.setCompetencia(competencia);
		resumo.setDataPagamento(dataPagamento);
		
		return resumo;
	}
	
	/**
	 * Verifica se foi registrado algum erro (exceção) no resumo, durante o processamento.
	 * Esse método é chamado após a finalização da barra de progresso, que se encarrega de
	 * setar no resumo as exceções levantadas.
	 */
	public ResumoRetorno verificarProcessamentoRetorno(ResumoRetorno resumo) throws Exception {
		System.gc();
		if (resumo.getExcecaoDoFluxo() != null) {
			throw resumo.getExcecaoDoFluxo();
		} else {
			try {
				resumo.getTransaction().openSession().flush();
				resumo.getTransaction().commit();
			} catch (Exception e) {
				e.printStackTrace();
				resumo.getTransaction().rollback();
			} finally {
				resumo.getTransaction().closeSession();
			}
		}
		
		return resumo;
	}
	
	/**
	 * Processa o retorno de consignação. Chamado pela barra de progresso.
	 */
	public ResumoRetorno processarRetorno(ResumoRetorno resumo) throws Exception {
		progessTitulo = "Inicializando";
		progessStatus = "Lendo arquivo de retorno...";
		
		Scanner sc 				= new Scanner(new ByteArrayInputStream(resumo.getConteudoArquivo()));
		
		Map<String, List<String[]>> map = getArquivoAgrupadoPorCpf(sc,3);
		List<String> cpfs 				= new ArrayList<String>();
		cpfs.addAll(map.keySet());
		
		progessStatus = "Buscando titulares no banco de dados...";
		
		Set<String> tits 		= new HashSet<String>();
		Set<String> cpfsDupls 	= new HashSet<String>();

		progessTitulo = "Processando...";
		progessStatus = "__value__ de __max__ segurados";
		
		progressoMaximo = cpfs.size();
		progressoAtual  = 0;
		
		int cpfsProcessados = 0;
		for (String cpf : cpfs) {
			List<TitularConsignacaoTuning> titulares = getTitulares(cpf);
			cpfsProcessados += processarConsignacaoTitulares(resumo, map, tits, cpfsDupls, titulares);
			HibernateUtil.currentSession().flush();
			HibernateUtil.currentSession().clear();
		}
		
		resumo.setNumeroDeCPFS(cpfsProcessados);
		resumo.setNumeroDeCPFSDuplicados(cpfsDupls.size());
		
		progessStatus = "Atualizando início de carências...";
		atualizaInicioDaCarencia();
		progressoAtual = progressoMaximo;
		return resumo;
	}

	/**
	 * Processa somente os titulares passados como parâmetro.
	 * Criado por questão de performance.
	 */
	private int processarConsignacaoTitulares(ResumoRetorno resumo, Map<String, List<String[]>> map, Set<String> tits,
			Set<String> cpfsDupls, List<TitularConsignacaoTuning> titulares) throws Exception {
		int cpfsProcessados = 0;
		for(TitularConsignacaoTuning titular : titulares){
			if(map.get(titular.getPessoaFisica().getCpf()) != null){
				if(tits.add(titular.getPessoaFisica().getCpf())){
					processarConsignacaoRefeita(titular, map.get(titular.getPessoaFisica().getCpf()), resumo.getCompetencia(), resumo.getDataPagamento());
					cpfsProcessados ++;
				}else{
					cpfsDupls.add(titular.getPessoaFisica().getCpf());
				}
			}
			progressoAtual++;
		}
		return cpfsProcessados;
	}
	
	public Map<String, List<String[]>> getArquivoAgrupadoPorCpf(Scanner sc, int chaveCpf) throws IOException{
		int numeroDeCpfsComDuasMatriculas = 0;
		String linha = "";
		Map<String, List<String[]>> map = new HashMap<String, List<String[]>>();
		Map<String, List<String[]>> mapCpfsDuplicados = new HashMap<String, List<String[]>>();
		while(sc.hasNext()){
			linha = sc.nextLine();
			StringTokenizer tokens = new StringTokenizer(linha,";");
			String[] fields = new String[tokens.countTokens() - 1];
			int index = 0;
			int count = 1;
			String cpf = "";
			while(tokens.hasMoreTokens()){
				if(count == chaveCpf){
					cpf = Utils.applyMask(StringUtils.leftPad(tokens.nextToken().trim(),11,"0"), "###.###.###-##");
					count++;
				}else{
					fields[index] = tokens.nextToken().trim();
					count++;
					index++;
				}
			}
			
			if(map.containsKey(cpf)){
				if(mapCpfsDuplicados.containsKey(cpf)){
					mapCpfsDuplicados.get(cpf).add(fields);
				}else{
					List<String[]> list = new ArrayList<String[]>();
					list.add(fields);
					mapCpfsDuplicados.put(cpf, list);
				}
				numeroDeCpfsComDuasMatriculas++;
				map.get(cpf).add(fields);
			}else{
				List<String[]> list = new ArrayList<String[]>();
				list.add(fields);
				map.put(cpf, list);
			}
		}
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public List<TitularConsignacaoTuning> getTitulares(String cpf){
		List<TitularConsignacaoTuning> lista = HibernateUtil.currentSession()
															.createCriteria(TitularConsignacaoTuning.class)
															.add(Expression.ne("situacao.descricao", SituacaoEnum.CANCELADO.descricao()))
															.add(Expression.eq("pessoaFisica.cpf", cpf))
															.setFetchMode("cobrancas", FetchMode.SELECT)
															.setFetchMode("detalhePagamento", FetchMode.SELECT)
															.setFetchMode("informacaoFinanceira", FetchMode.SELECT)
															.setFetchMode("grupo", FetchMode.SELECT)
															.setFetchMode("secretaria", FetchMode.SELECT)
															.setFetchMode("consultasPromocionais", FetchMode.SELECT)
															.setFetchMode("cartoes", FetchMode.SELECT)
															.setFetchMode("consumoIndividual", FetchMode.SELECT)
															.setFetchMode("odontograma", FetchMode.SELECT)
															.setFetchMode("guias", FetchMode.SELECT)
															.setFetchMode("dependentes", FetchMode.SELECT)
															.setFetchSize(500)
															.list();
		
		return lista;
	}
	
	/**
	 * Procura nas matriculas ativas as consignações, se não tiver cria.
	 * Caso não encontre matriculas ativas, procura-se nas inativas.
	 */
	private static void processarConsignacaoRefeita(TitularConsignacaoTuning titular, List<String[]> valores, Date competencia, Date dataPagamento) throws Exception{
		
		for(String[] valor : valores){
			String verba = valor[2];
			boolean achouConsignacaoMatricula = false;
			
			String codigoMatriculaDoArquivo = new Long(valor[1]).toString();
			boolean existeMatriculaInativaComConsignacaoGerada = existeMatriculaInativaComConsignacaoGerada(
					titular, codigoMatriculaDoArquivo, competencia);
			
			for(MatriculaTuning matricula : titular.getMatriculas()){
				if(matricula.getDescricao().equals(codigoMatriculaDoArquivo)) {
					ConsignacaoMatriculaTuning consignacaoMatricula = (ConsignacaoMatriculaTuning) matricula.getConsignacao(competencia);
					if(consignacaoMatricula == null && matricula.isAtiva() && !existeMatriculaInativaComConsignacaoGerada) {
						gerarConsignacaoMatriculaRefeita(verba, matricula, valor[3], competencia, dataPagamento);
						achouConsignacaoMatricula = true;
					} else if (consignacaoMatricula != null){//só atualiza se tiver consignação
						atualizaConsignacaoMatricula(verba, consignacaoMatricula, valor[3],dataPagamento);
						achouConsignacaoMatricula = true;
					}
				}
			}
			
			ConsignacaoSeguradoTuning consignacaoSegurado = (ConsignacaoSeguradoTuning) titular.getConsignacao(competencia);
			
			if (consignacaoSegurado == null) {
				if(achouConsignacaoMatricula) {
					consignacaoSegurado = gerarConsignacaoSegurado(titular, competencia, dataPagamento);
				} else {
					continue;
				}
			}	
			
			consignacaoSegurado.updateStatusEValoresConsignacaoSegurado(competencia,dataPagamento);
		}

	}
	
	/**
	 * Verifica se existe matricula inativa com consignação gerada.
	 */
	private static boolean existeMatriculaInativaComConsignacaoGerada(TitularConsignacaoTuning titular, String codigo, Date competencia) {
		for(MatriculaTuning matricula : titular.getMatriculasInativas()){
			if(matricula.getDescricao().equals(codigo) && matricula.getConsignacao(competencia) != null) {
				return true;
			}
		}
		return false;
	}


	private static void atualizaConsignacaoMatricula(String verba, ConsignacaoMatriculaInterface consignacaoMatricula, 
													String valor, Date dataPagamento) throws Exception {
		
		BigDecimal valorACobrar = new BigDecimal(valor).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
		
		if(verba.equals(VERBA_COPARTICIPACAO)) {
			consignacaoMatricula.setValorCoparticipacao(valorACobrar);
		}
		
		if(verba.equals(VERBA_PARCELAMENTO) || verba.equals(VERBA_PARCELAMENTO_URB)) {
			consignacaoMatricula.setValorParcelamento(valorACobrar);
		}
		
		if(verba.equals(VERBA_FINANCIAMENTO) || verba.equals(VERBA_FINANCIAMENTO_URB)) {
			consignacaoMatricula.setValorFinanciamento(valorACobrar);
		}
		
		boolean isValorCoparticipacaoZerado = consignacaoMatricula.getValorCoparticipacao().compareTo(BigDecimal.ZERO) == 0;
		boolean isValorFinanciamentoZerado 	= consignacaoMatricula.getValorFinanciamento().compareTo(BigDecimal.ZERO) == 0;
		boolean isValorParcelamentoZerado 	= consignacaoMatricula.getValorParcelamento().compareTo(BigDecimal.ZERO) == 0;
		
		consignacaoMatricula.setStatusConsignacao(Consignacao.STATUS_ABERTO);
		
		boolean isVerbaFinanciamentoURB = verba.equals(VERBA_FINANCIAMENTO_URB);
		boolean isVerbaParcelamentoURB 	= verba.equals(VERBA_PARCELAMENTO_URB);
		
		if(!(isValorCoparticipacaoZerado && isValorFinanciamentoZerado && isValorParcelamentoZerado) || (isVerbaFinanciamentoURB ||isVerbaParcelamentoURB) ) {
			consignacaoMatricula.setStatusConsignacao(Consignacao.STATUS_PAGO);
			consignacaoMatricula.setDataDoCredito(dataPagamento);
			consignacaoMatricula.setDataPagamento(dataPagamento);
		}
		
		ImplDAO.save(consignacaoMatricula);
	}


	private static ConsignacaoMatriculaTuning gerarConsignacaoMatriculaRefeita(String verba,
			AbstractMatricula matricula, String valor, Date competencia, Date dataPagamento) throws Exception {
		
		ConsignacaoMatriculaTuning consignacao = new ConsignacaoMatriculaTuning();
		consignacao.setCompetencia(competencia);
		consignacao.setStatusConsignacao(Consignacao.STATUS_ABERTO);
		consignacao.setMatricula(matricula);
		matricula.getConsignacoes().add(consignacao);
		consignacao.setDataDoCredito(dataPagamento);
		consignacao.setDataPagamento(dataPagamento);
		consignacao.setMatriculaNoEstado(matricula.getDescricao());
		consignacao.setValorCoparticipacao(BigDecimal.ZERO);
		consignacao.setValorFinanciamento(BigDecimal.ZERO);
		
		BigDecimal valorACobrar = new BigDecimal(valor).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
		
		if(verba.equals(VERBA_COPARTICIPACAO) ) {
			consignacao.setValorCoparticipacao(valorACobrar);
		}
		
		if(verba.equals(VERBA_PARCELAMENTO) || verba.equals(VERBA_PARCELAMENTO_URB)) {
			consignacao.setValorParcelamento(valorACobrar);
		}
		
		if(verba.equals(VERBA_FINANCIAMENTO) || verba.equals(VERBA_FINANCIAMENTO_URB)) {
			consignacao.setValorFinanciamento(valorACobrar);
		}
		
		boolean isValorCoparticipacaoZerado = consignacao.getValorCoparticipacao().compareTo(BigDecimal.ZERO) == 0;
		boolean isValorFinanciamentoZerado 	= consignacao.getValorFinanciamento().compareTo(BigDecimal.ZERO) == 0;
		boolean isValorParcelamentoZerado 	= consignacao.getValorParcelamento().compareTo(BigDecimal.ZERO) == 0;
		
		boolean isVerbaFinanciamentoURB = verba.equals(VERBA_FINANCIAMENTO_URB);
		boolean isVerbaParcelamentoURB 	= verba.equals(VERBA_PARCELAMENTO_URB);
		if(!(isValorCoparticipacaoZerado && isValorFinanciamentoZerado && isValorParcelamentoZerado) || (isVerbaFinanciamentoURB ||isVerbaParcelamentoURB) ) {
			consignacao.setStatusConsignacao(Consignacao.STATUS_PAGO);
			consignacao.setDataDoCredito(dataPagamento);
			consignacao.setDataPagamento(dataPagamento);
		}
		
		ImplDAO.save(consignacao);
		
		return consignacao;
			
	}


	private static ConsignacaoSeguradoTuning gerarConsignacaoSegurado(TitularConsignacaoTuning titular, Date competencia, Date dataPagamento) throws Exception {
		ConsignacaoSeguradoTuning consignacao = new ConsignacaoSeguradoTuning();
		consignacao.setCompetencia(competencia);
		
		consignacao.setStatusConsignacao('A');
		consignacao.setTitular(titular);
		titular.getConsignacoes().add(consignacao);
	
		consignacao.setValorCoparticipacao(BigDecimal.ZERO);
		consignacao.setValorFinanciamento(BigDecimal.ZERO);
		consignacao.setValorPrevidencia(BigDecimal.ZERO);
		ComponenteColecaoContas colecaoContas = new ComponenteColecaoContas();
		consignacao.setColecaoContas(colecaoContas);
		consignacao.setDataDoCredito(dataPagamento);
		consignacao.setDataPagamento(dataPagamento);
		titular.getConsignacoes().add(consignacao);
		return consignacao;
	}
	
	public byte[] getConteudo(File arquivo) throws Exception{
    	InputStream out = new FileInputStream(arquivo);
    	ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
    	
    	while (out.available() > 0) {
    		byteArray.write(out.read());
    	}
    	
    	return byteArray.toByteArray();
    }
	
	/**
	 * Faz o calculo da data de início das carência de todos 
	 * os segurados com carência "null" no banco de dados
	 */
	@SuppressWarnings("unchecked")
	public void atualizaInicioDaCarencia() throws Exception {
		progessTitulo = "Atualizando Início das Carências";
		progessStatus = "Inicializando...";
		progressoAtual = 0;
		progressoMaximo = 100;
		
		List<Segurado> seguradosComCarenciaNula = HibernateUtil.currentSession()
				.createQuery("Select s from Segurado s where s.inicioDaCarencia is null").list();
		
		progressoMaximo = seguradosComCarenciaNula.size();
		progessStatus = "__value__ de __max__ atualizados";
		
		for (Segurado segurado : seguradosComCarenciaNula) {
			if (segurado.isSeguradoTitular() || segurado.isSeguradoPensionista() || segurado.isSeguradoDependenteSuplementar() ){
				atualizarCarencia(segurado, segurado.getIdSegurado());
			} else {
				atualizarCarencia(segurado, segurado.getTitular().getIdSegurado());
			}
			progressoAtual++;
		}
		progressoAtual = progressoMaximo;
	}
	
	/** Atualiza carência do segurado */
	private void atualizarCarencia(Segurado segurado, Long idTitular) throws Exception {
		String sqlConsignacao = "select min(c.dataDoCredito) from ConsignacaoSegurado c  where c.titular.idSegurado = :idTitular "
				+ "and c.statusConsignacao  = 'P' and c.dataDoCredito >= :dataAdesao";

		String sqlCobranca = "select min(c.dataPagamento) from Cobranca c where c.titular.idSegurado = :idTitular  "
				+ "and descricao = 'Pago(a)' and c.dataPagamento >= :dataAdesao";

		Query queryConsignacao = HibernateUtil.currentSession().createQuery(sqlConsignacao);
		Query queryCobranca = HibernateUtil.currentSession().createQuery(sqlCobranca);
		
		queryConsignacao.setLong("idTitular", idTitular);
		queryConsignacao.setDate("dataAdesao", segurado.getDataAdesao());
		
		Date inicioCarencia = (Date) queryConsignacao.uniqueResult();

		if (inicioCarencia == null) {
			queryCobranca.setLong("idTitular", idTitular);
			queryCobranca.setDate("dataAdesao", segurado.getDataAdesao());
			inicioCarencia = (Date) queryCobranca.uniqueResult();
		}

		if (inicioCarencia != null) {
			segurado.setInicioDaCarencia(inicioCarencia);
			ImplDAO.save(segurado);
		}
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println("começou "+new Date());
        
        ProcessaRetornoConsignacaoOld service = new ProcessaRetornoConsignacaoOld();
        processaArquivo(service, "parte1.txt");
        processaArquivo(service, "parte2.txt");
        System.gc();
        processaArquivo(service, "parte3.txt");
        processaArquivo(service, "parte4.txt");
        System.gc();
	}
	
	/**
	 * Método auxiliar para processamento via Schedule (main).
	 */
	private static void processaArquivo (ProcessaRetornoConsignacaoOld service, String path){
		System.out.println(path);
		Transaction tm = HibernateUtil.currentSession().beginTransaction();
		try{
        	byte[] conteudo = service.getConteudo(new File(ArquivoInterface.REPOSITORIO+path));
        	service.informarArquivo(conteudo, Utils.parse("01/12/2010"), Utils.parse("24/12/2010"));
        	System.out.println("comitando em "+new Date());
        	tm.commit();
		}catch (Exception e) {
			e.printStackTrace();
			tm.rollback();
		} finally {
			System.out.println("acabou "+new Date());
		}
	}
	
	/*
	 * SQL PRA VERIFICAR QUEM NAO TEVE RETORNO PROCESSADO
	 * 
	 * select seg.numerodocartao,mat.descricao from
		FINANCEIRO_ConsignacaoMatricula cons,segurados_matricula mat , segurados_segurado seg
		where cons.competencia = '2010-04-01' and cons.statusconsignacao = 'A'
		and cons.idmatricula = mat.idmatricula and mat.idsegurado = seg.idsegurado
		order by seg.numerodocartao
	 */
	
}