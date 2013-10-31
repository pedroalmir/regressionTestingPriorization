package br.com.infowaypi.ecare.services.financeiro.consignacao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import br.com.infowaypi.ecare.arquivos.ArquivoBase;
import br.com.infowaypi.ecare.financeiro.arquivo.ArquivoDeEnvioConsignacao;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Fluxo para download dos arquivos de consignação gerados (no fluxo Geração de consignações)
 * bem como o arquivo de log.
 * 
 * @author Dannylvan
 *
 */
public class FlowDownloadArquivosConsignacao {
	
	private String nomeArquivoConsignacao;
	private byte[] arquivosConsignacao;
	
	private String nomeArquivoLog;
	private byte[] arquivoLog;
	
	private Date competencia;
	
	private int quantidadeArquivos;
	
	public FlowDownloadArquivosConsignacao gerarArquivoZipConsignacoes(Date competencia) throws Exception {
		Map<String, byte[]> arquivosParaCompactar = new HashMap<String, byte[]>();
		List<ArquivoDeEnvioConsignacao> arquivosEnvioConsignacao = getArquivosDeConsignacao(competencia);
		
		if(arquivosEnvioConsignacao.isEmpty()){
			throw new Exception("Não foi gerado arquivo de consignação para esta competência.");
		}
		
		for (ArquivoDeEnvioConsignacao arquivo : arquivosEnvioConsignacao) {
			arquivosParaCompactar.put(arquivo.getFileName(), arquivo.getArquivo());
		}
		this.setCompetencia(competencia);
		this.setArquivosConsignacao(Utils.zipFiles(arquivosParaCompactar));
		
		ArquivoBase log = arquivosEnvioConsignacao.iterator().next().getArquivoLog();
		if(log != null){
			this.setArquivoLog(log.getArquivo());
			this.setNomeArquivoLog(log.getTituloArquivo());
		}
		
		this.setQuantidadeArquivos(arquivosParaCompactar.size());
		this.setNomeArquivoConsignacao(getNomeArquivo("Consigncoes", competencia));
		return this;
	}
	
	@SuppressWarnings("unchecked")
	private List<ArquivoDeEnvioConsignacao> getArquivosDeConsignacao(Date competencia) {
		
		String hql = "select a from ArquivoDeEnvioConsignacao a where competencia = :competencia" +
						" AND statusArquivo <> 'C' AND tipoDeArquivo = 'C'";
		Query query = HibernateUtil.currentSession().createQuery(hql)
					.setDate("competencia", competencia);
		
		return query.list();
	}
	
	private String getNomeArquivo(String nomeArquivo, Date competencia){
		return nomeArquivo + Utils.format(competencia, "_MM_yyyy")+".zip";
	}
	
	public String getNomeArquivoConsignacao() {
		return nomeArquivoConsignacao;
	}

	public void setNomeArquivoConsignacao(String nomeArquivoConsignacao) {
		this.nomeArquivoConsignacao = nomeArquivoConsignacao;
	}

	public byte[] getArquivosConsignacao() {
		return arquivosConsignacao;
	}

	public void setArquivosConsignacao(byte[] arquivosConsignacao) {
		this.arquivosConsignacao = arquivosConsignacao;
	}

	public Date getCompetencia() {
		return competencia;
	}

	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}

	public int getQuantidadeArquivos() {
		return quantidadeArquivos;
	}

	public void setQuantidadeArquivos(int quantidadeArquivos) {
		this.quantidadeArquivos = quantidadeArquivos;
	}

	public String getNomeArquivoLog() {
		return nomeArquivoLog;
	}

	public void setNomeArquivoLog(String nomeArquivoLog) {
		this.nomeArquivoLog = nomeArquivoLog;
	}

	public byte[] getArquivoLog() {
		return arquivoLog;
	}

	public void setArquivoLog(byte[] arquivoLog) {
		this.arquivoLog = arquivoLog;
	}
	
}
