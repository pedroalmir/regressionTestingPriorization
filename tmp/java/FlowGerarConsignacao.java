package br.com.infowaypi.ecare.services.financeiro.consignacao;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.hibernate.Query;
import org.hibernate.SQLQuery;

import br.com.infowaypi.ecare.arquivos.ArquivoBase;
import br.com.infowaypi.ecare.financeiro.arquivo.ArquivoDeEnvioConsignacao;
import br.com.infowaypi.ecare.financeiro.consignacao.GerarArquivoConsignacaoProgressBar;
import br.com.infowaypi.ecarebc.consumo.periodos.Periodo;
import br.com.infowaypi.ecarebc.financeiro.arquivos.ArquivoInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.utils.Utils;

public class FlowGerarConsignacao {
	
	private byte[] consignacoesZip;
	private String nomeArquivoConsignacoes;
	
	private byte[] logsZip;
	private String nomeArquivoLogs;
	
	private static FlowGerarConsignacao flowModificadoPeloProgressBar= null;
	
	public FlowGerarConsignacao verificarGeracao(Date competencia) throws Exception {
		if (GerarArquivoConsignacaoProgressBar.getExcecaoDoFluxo() != null){
			throw GerarArquivoConsignacaoProgressBar.getExcecaoDoFluxo();
		}
		return GerarArquivoConsignacaoProgressBar.getService();
	}
	
	/**
	 * Método que gera os arquivos de consignação da competencia informada.
	 * Este método é chamado pela classe GerarArquivoConsignacaoProgressBar 
	 */
	public void gerarConsignacao(Date competencia, GerarArquivoConsignacaoProgressBar progressBar) throws Exception {
		progressBar.setProgessTitulo("Inicializando");
		progressBar.setProgessStatus("Lendo arquivo de CPFs EFETIVOS...");
		
		if(competencia == null){
			throw new Exception("A competência deve ser informada.");
		}
		
		if (existeArquivoDeConsignacaoGerado(competencia)){
			throw new Exception("Os arquivos de consignação da competência informada já foram gerados.");
		}
		
		String sqlQuery = "UPDATE atendimento_guiasimples SET autorizacao = idguia WHERE autorizacao is null";
		SQLQuery query 	= HibernateUtil.currentSession().createSQLQuery(sqlQuery);
		int numeroGuiasSemAutorizacao = query.executeUpdate();
		System.out.println("numeroGuiasSemAutorizacao: " + numeroGuiasSemAutorizacao);
		
		List<String> cpfsURBEfetivos = lerURBEfetivos();

		GeracaoConsignacaoService geracaoConsignacao = new GeracaoConsignacaoService();
		
		geracaoConsignacao.setCpfsSeguradosURBEfetivos(cpfsURBEfetivos);
		Date dataPagamento = Periodo.MENSAL.getDataFinalEmMeses(competencia);
		
		geracaoConsignacao.informarDados(competencia, dataPagamento, progressBar);
		
		progressBar.setProgessTitulo("Finalizando");
		progressBar.setProgessStatus("Criando arquivos...");
		List<ArquivoDeEnvioConsignacao> arquivoGerados = geracaoConsignacao.gerarArquivosPorEmpresa(competencia);
		
		manipularArquivos(competencia, geracaoConsignacao, arquivoGerados);
		
		progressBar.setProgessStatus("Salvando arquivos no banco de dados...");
	}

	/**
	 * Faz a leitura em disco dos CPF's URB Efetivos.
	 */
	protected List<String> lerURBEfetivos() throws FileNotFoundException {
		Scanner sc = new Scanner(new File(ArquivoInterface.REPOSITORIO+"CPFS_URB_EFETIVOS.txt"));
		List<String> cpfsURBEfetivos = new ArrayList<String>();
		while (sc.hasNext()) {
			cpfsURBEfetivos.add(Utils.applyMask(sc.nextLine(), "###.###.###-##"));
		}
		return cpfsURBEfetivos;
	}

	/**
	 * Faz a manipulação dos arquivos de consignação gerados, bem como o log.
	 */
	private void manipularArquivos(Date competencia, GeracaoConsignacaoService geracaoConsignacao,
			List<ArquivoDeEnvioConsignacao> arquivoGerados) throws Exception {
		consignacoesZip = gerarArquivoZipConsignacoes(arquivoGerados);
		logsZip 		= gerarArquivoZipLogs(geracaoConsignacao.getLogs());
		
		nomeArquivoConsignacoes = getNomeArquivo("consignacoes", competencia);
		nomeArquivoLogs 		= getNomeArquivo("logs", competencia);
		
		ArquivoBase arquivoLog = new ArquivoBase();
		arquivoLog.setArquivo(logsZip);
		arquivoLog.setDataCriacao(new Date());
		arquivoLog.setZipado(true);
		arquivoLog.setTituloArquivo(nomeArquivoLogs);
		arquivoLog.setTipoArquivo("zip");
		
		ImplDAO.save(arquivoLog);
		
		for (ArquivoDeEnvioConsignacao arquivoDeEnvioConsignacao : arquivoGerados) {
			arquivoDeEnvioConsignacao.setArquivoLog(arquivoLog);
			ImplDAO.save(arquivoDeEnvioConsignacao);
		}
		
	}

	/**
	 * Compacta os arquivos de consignações gerados.
	 */
	private byte[] gerarArquivoZipConsignacoes(List<ArquivoDeEnvioConsignacao> arquivoGerados) throws Exception {
		if(arquivoGerados.isEmpty()){
			throw new Exception("Não foram gerados novos arquivos de consignação.");
		}
		Map<String, byte[]> arquivosParaCompactar = new HashMap<String, byte[]>();
		
		for (ArquivoDeEnvioConsignacao arquivo : arquivoGerados) {
			arquivosParaCompactar.put(arquivo.getFileName(), arquivo.getArquivo());
		}
		
		return Utils.zipFiles(arquivosParaCompactar);
	}
	
	private byte[] gerarArquivoZipLogs(List<byte[]> logs) throws Exception {
		Map<String, byte[]> arquivosParaCompactar = new HashMap<String, byte[]>();
		int nomeador = 1;
		for (byte[] log : logs) {
			arquivosParaCompactar.put("log"+nomeador+".txt", log);
			nomeador++;
		}
		
		return Utils.zipFiles(arquivosParaCompactar);
	}

	private boolean existeArquivoDeConsignacaoGerado(Date compentecia) {
		
		String hql = "select count(*) from ArquivoDeEnvioConsignacao where competencia = :competencia" +
						" AND statusArquivo <> 'C'";
		Query query = HibernateUtil.currentSession()
									.createQuery(hql)
									.setDate("competencia", compentecia);
		Long quantidade = (Long) query.list().get(0);

		if (quantidade > 0){
			return true;
		}
		
		return false;
	}
	
	private String getNomeArquivo(String nomeArquivo, Date competencia){
		return nomeArquivo + Utils.format(competencia, "_MM_yyyy")+".zip";
	}

	public byte[] getConsignacoesZip() {
		return consignacoesZip;
	}

	public byte[] getLogsZip() {
		return logsZip;
	}

	public String getNomeArquivoConsignacoes() {
		return nomeArquivoConsignacoes;
	}

	public String getNomeArquivoLogs() {
		return nomeArquivoLogs;
	}

	public static FlowGerarConsignacao getFlowModificadoPeloProgressBar() {
		return flowModificadoPeloProgressBar;
	}

	public static void setFlowModificadoPeloProgressBar(FlowGerarConsignacao flowModificadoPeloProgressBar) {
		FlowGerarConsignacao.flowModificadoPeloProgressBar = flowModificadoPeloProgressBar;
	}
}
