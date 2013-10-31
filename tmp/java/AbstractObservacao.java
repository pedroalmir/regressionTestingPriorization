package br.com.infowaypi.ecarebc.atendimentos;

import java.util.Date;

import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Classe abstrata respons�vel por representar um texto descrevendo alguma considera��o adicional em uma guia.
 * 
 * <p>
 * Limita��es:<br>
 * Atualmente � usada somente em GuiaExameOdonto e em GuiaInternacao, apesar de possuir uma GuiaCompleta. 
 * </p>
 * @author patricia
 * @version
 */
@SuppressWarnings({ "unchecked", "serial" })
public abstract class AbstractObservacao extends AbstractInformacaoAdicional {

	/**
	 * Guia que cont�m a observa��o.
	 */
	private GuiaSimples guia;
	
	public AbstractObservacao(){
		this.usuario = new Usuario();
		this.dataDeCriacao = new Date();
	}
	
	public AbstractObservacao(Date dataDeCriacao, String texto, UsuarioInterface usuario) {
		this.dataDeCriacao = dataDeCriacao;
		this.texto = texto;
		this.usuario = usuario;
	}
	
	public AbstractObservacao (String texto, UsuarioInterface usuario) {
		this.texto = texto;
		this.dataDeCriacao = new Date();
		this.usuario = usuario;
	}

	public GuiaSimples getGuia() {
		return guia;
	}
	
	public void setGuia(GuiaSimples guia) {
		this.guia = guia;
	}

	/**
	 * M�todo que carrega os campos do objeto que s�o requeridos na sess�o do Hibernate.
	 */
	public void tocarObjetos() {
		this.getTexto();
		this.getDataDeCriacao();
	}
	
	/**
	 * Valida��o que impede a inser��o de uma observa��o vazia.
	 * @return Boolean
	 */
	public Boolean validate(){
		Assert.isNotEmpty(texto, "Digite um texto para a observa��o!");
		return true;
	}

	public abstract boolean isRegistroAuditoria();
	public abstract boolean isObervacao();
	public abstract boolean isMotivo();

}
