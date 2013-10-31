package br.com.infowaypi.ecare.arquivos;

import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.NotEquals;
import br.com.infowaypi.molecular.parameter.OrderBy;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Representa um arquivo disponível para download na aba Download
 * 
 * @author Dannylvan
 */
public class ArquivoDownload extends ImplColecaoSituacoesComponent{
	
	private static final long serialVersionUID = 1L;
	
	private Long idArquivoDownload;
	private Date dataUpload;
	private String descricaoArquivo;
	private byte[] arquivo;
	
	private boolean disponivelSomenteParaDiretoriaAuditorERoot;
	
	/**
	 * ordem que será mostrado na tela
	 */
	private int ordemArquivo;

	public Boolean validate(UsuarioInterface usuario) throws Exception{
		if(this.getSituacoes().size() == 0){
			setDataUpload(new Date());
			this.mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(),
					"Cadastro do arquivo para Download", new Date());
		}
		reordenarArquivos();
		return true;
	}
	
	/**
	 * reorganiza a ordem dos arquivos do banco
	 * @throws Exception 
	 */
	private void reordenarArquivos() throws Exception{
		SearchAgent sa = new SearchAgent();
		if(this.getIdArquivoDownload() != null){
			sa.addParameter(new NotEquals("idArquivoDownload", this.getIdArquivoDownload()));
		}
		sa.addParameter(new OrderBy("ordemArquivo"));
		
		List<ArquivoDownload> arquivos = sa.list(ArquivoDownload.class);
		boolean setouOrdemDoArquivoAtual = false;
		int ordem = 1;
		for (ArquivoDownload arquivo : arquivos) {
			if(ordem >= this.getOrdemArquivo() && !setouOrdemDoArquivoAtual){
				arquivo.setOrdemArquivo(ordem);
				ordem++;
				setouOrdemDoArquivoAtual = true;
			}
			arquivo.setOrdemArquivo(ordem);
			ordem++;
			ImplDAO.save(arquivo);
		}
		if(!setouOrdemDoArquivoAtual){
			this.setOrdemArquivo(ordem);
		}
	}
	
	public void reativar(String motivo, UsuarioInterface usuario) throws Exception {
		if (Utils.isStringVazia(motivo))
			throw new Exception("O motivo deve ser preenchido!");
		
		if (this.isSituacaoAtual(SituacaoEnum.ATIVO.descricao()))
			throw new Exception("Este download já está ativo.");
		
		this.mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(), motivo, new Date());
	}
	
	public void inativar(String motivo, UsuarioInterface usuario) throws Exception {
		if (Utils.isStringVazia(motivo))
			throw new Exception("O motivo deve ser preenchido!");
		
		if (this.isSituacaoAtual(SituacaoEnum.INATIVO.descricao()))
			throw new Exception("Este download já está inativo.");
		
		this.mudarSituacao(usuario, SituacaoEnum.INATIVO.descricao(), motivo, new Date());
	}
	
	public Long getIdArquivoDownload() {
		return idArquivoDownload;
	}
	public void setIdArquivoDownload(Long idArquivoDownload) {
		this.idArquivoDownload = idArquivoDownload;
	}
	public Date getDataUpload() {
		return dataUpload;
	}
	public void setDataUpload(Date dataUpload) {
		this.dataUpload = dataUpload;
	}
	public byte[] getArquivo() {
		return arquivo;
	}
	public void setArquivo(byte[] arquivo) {
		this.arquivo = arquivo;
	}
	public String getDescricaoArquivo() {
		return descricaoArquivo;
	}

	public void setDescricaoArquivo(String descricaoArquivo) {
		this.descricaoArquivo = descricaoArquivo;
	}

	public int getOrdemArquivo() {
		return ordemArquivo;
	}

	public void setOrdemArquivo(int ordemArquivo) {
		this.ordemArquivo = ordemArquivo;
	}

	public boolean isDisponivelSomenteParaDiretoriaAuditorERoot() {
		return disponivelSomenteParaDiretoriaAuditorERoot;
	}

	public void setDisponivelSomenteParaDiretoriaAuditorERoot(
			boolean disponivelSomenteParaDiretoriaAuditorERoot) {
		this.disponivelSomenteParaDiretoriaAuditorERoot = disponivelSomenteParaDiretoriaAuditorERoot;
	}

}
