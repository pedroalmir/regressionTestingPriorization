package br.com.infowaypi.ecarebc.atendimentos;

import java.util.Date;

import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Classe abstrata responsável por representar um texto descrevendo alguma consideração adicional em uma guia.
 * 
 * <p>
 * Limitações:<br>
 * Atualmente é usada somente em GuiaExameOdonto e em GuiaInternacao, apesar de possuir uma GuiaCompleta. 
 * </p>
 * @author patricia
 * @version
 */
@SuppressWarnings({ "unchecked", "serial" })
public abstract class AbstractObservacao extends AbstractInformacaoAdicional {

	/**
	 * Guia que contém a observação.
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
	 * Método que carrega os campos do objeto que são requeridos na sessão do Hibernate.
	 */
	public void tocarObjetos() {
		this.getTexto();
		this.getDataDeCriacao();
	}
	
	/**
	 * Validação que impede a inserção de uma observação vazia.
	 * @return Boolean
	 */
	public Boolean validate(){
		Assert.isNotEmpty(texto, "Digite um texto para a observação!");
		return true;
	}

	public abstract boolean isRegistroAuditoria();
	public abstract boolean isObervacao();
	public abstract boolean isMotivo();

}
