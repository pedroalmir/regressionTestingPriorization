package br.com.infowaypi.ecarebc.portalTiss;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import br.com.infowaypi.ans.tiss.MensagemTISS;
import br.com.infowaypi.ecare.arquivos.ArquivoBase;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.upload.ImportarTiss;

/**
 * Classe que representa um arquivo Tiss: contem um arquivo .xml, ligado a um prestador,
 * importado (Lote de Guias) ou gerado (Protocolo de Recebimento).
 * 
 * @author Emanuel
 *
 */
@SuppressWarnings("serial")
public class ArquivoTiss extends ArquivoBase{

	/**
	 * preenchido com a Enum @ArquivoTissEnum
	 */
	private String tipoTransacao;
	private Prestador prestador;
	private UsuarioInterface usuario;
	
	/**
	 * Um arquivo relacionado a um arquivo Tiss. Ex: sempre que importa um arquivo de Lote de Guia, é gerado um
	 * arquivo de Protocolo de recebimento.
	 */
	private ArquivoTiss arquivoRelacionado;

	public String getTipoTransacao() {
		return tipoTransacao;
	}
	public void setTipoTransacao(String tipoTransacao) {
		this.tipoTransacao = tipoTransacao;
	}
	
	public Prestador getPrestador() {
		return prestador;
	}
	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
	}
	
	public UsuarioInterface getUsuario() {
		return usuario;
	}
	public void setUsuario(UsuarioInterface usuario) {
		this.usuario = usuario;
	}
	
	public ArquivoTiss getArquivoRelacionado() {
		return arquivoRelacionado;
	}
	public void setArquivoRelacionado(ArquivoTiss arquivoRelacionado) {
		this.arquivoRelacionado = arquivoRelacionado;
	}
	
	public String getNumeroLote() throws JAXBException, IOException{
		String lote = "";
		if(tipoTransacao.equals(ArquivoTissEnum.ENVIO_LOTE_GUIAS.name())){
			MensagemTISS mensagem = new ImportarTiss().getMensagemTissXml(new String(this.getArquivo()), false);
			lote +=  mensagem.getPrestadorParaOperadora().getLoteGuias().getNumeroLote();
		}else{
			MensagemTISS mensagem = new ImportarTiss().getMensagemTissXml(new String(this.getArquivoRelacionado().getArquivo()), false);
			lote +=  mensagem.getOperadoraParaPrestador().getProtocoloRecebimento().getProtocoloRecebimento().getNumeroLote();
		}
		return lote;
	}
	
	public String getNumeroProtocolo() throws JAXBException, IOException{
		String protocolo = "";
		if(tipoTransacao.equals(ArquivoTissEnum.ENVIO_LOTE_GUIAS.name())){
			MensagemTISS mensagem = new ImportarTiss().getMensagemTissXml(new String(this.getArquivoRelacionado().getArquivo()), false);
			protocolo += mensagem.getOperadoraParaPrestador().getProtocoloRecebimento().getProtocoloRecebimento().getNumeroProtocoloRecebimento();
		}else{
			MensagemTISS mensagem = new ImportarTiss().getMensagemTissXml(new String(this.getArquivo()), false);
			protocolo += mensagem.getOperadoraParaPrestador().getProtocoloRecebimento().getProtocoloRecebimento().getNumeroProtocoloRecebimento();
		}
		return protocolo;
	}
	
}
