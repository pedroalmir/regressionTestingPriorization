/**
 * 
 */
package br.com.infowaypi.ecarebc.atendimentos;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.com.infowaypi.ecarebc.atendimentos.enums.TipoCriticaEnum;
import br.com.infowaypi.ecarebc.atendimentos.validators.Criticavel;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * Classe responsável pelo registro de críticas feitas pelo sistema a uma Guia.
 * @author <a href="mailto:mquixaba@gmail.com">Marcus BOolean</a>
 * @since 2008-12-19 10:01
 * @update Wislanildo
 * 
 */
@SuppressWarnings({ "serial" })
public class Critica implements Serializable {
	
	private Long idCritica;
	
	/**
	 * Atributo utilizado para referênciar a guia a qual a critica pertence, é também utilizada para armazenar a origem da critica
	 * quando esta estiver relacionada com CIDs.
	 */
	private GuiaSimples<?> guia;
	
	private String mensagem;
	private Date data;
	/**
	 * Indica o usuário que estava manipulando o sistema no momento da geração da crítica
	 */
	private UsuarioInterface usuario;
	/**
	 * Indica se a crítica foi ou não avaliada, por alguém com tais poderes. Ex: Um crítica gerada a partir de uma validação de tipo de acomodação
	 * estará avaliada, quando a solicitação passar por um profissional regulador, e for aceita.
	 */
	private boolean avaliada;
	
	/**
	 * Indica se a origem da crítica foi ou não autorizada (Guia ou Procedimento). 
	 */
	private boolean autorizada;
	
	/**
	 * @see {@link TipoCriticaEnum}
	 */
	private int tipoCritica;
	
	/**
	 * Toda crítica deverá referênciar sua origem sendo ela uma guia ou procedimento, no caso da crítica ter como origem um
	 * procedimento o atributo procedimentoOrigem será utilizado para armazenar o procedimento de origem.
	 */
	private ProcedimentoInterface procedimentoOrigem;
	
	
	public Critica() {
		
	}
	
	public Critica(GuiaSimples<?> guia, String mensagem, UsuarioInterface usuario, int tipoCritica, GuiaSimples<?> guiaOrigem, ProcedimentoInterface procedimentoOrigem) {
		this();
		this.guia = guia;
		this.mensagem = mensagem;
		this.data = new Date();
		this.usuario = usuario;
		this.tipoCritica = tipoCritica;
	}	
	
	public String getMensagem() {
		return mensagem;
	}
	
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	
	public Date getData() {
		return data;
	}
	
	public void setData(Date data) {
		this.data = data;
	}
	
	public GuiaSimples<?> getGuia() {
		return guia;
	}


	public void setGuia(GuiaSimples<?> guia) {
		this.guia = guia;
	}


	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Critica)){
			return false;
		}
		
		Critica critica = (Critica) obj;
		return new EqualsBuilder().append(this.mensagem, critica.getMensagem())
									.append(this.guia, critica.getGuia())
									.append(this.tipoCritica, critica.tipoCritica)
									.append(this.data, critica.getData())
									.append(this.usuario, critica.getUsuario())
									.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.getMensagem())
									.append(this.getGuia())
									.append(this.getTipoCritica())
									.append(this.getData())
									.append(this.getUsuario())
									.toHashCode();
	}

	public Long getIdCritica() {
		return idCritica;
	}

	public void setIdCritica(Long idCritica) {
		this.idCritica = idCritica;
	}
	
	public UsuarioInterface getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioInterface usuario) {
		this.usuario = usuario;
	}

	public boolean isAvaliada() {
		return avaliada;
	}

	public void setAvaliada(boolean avaliada) {
		this.avaliada = avaliada;
	}

	public int getTipoCritica() {
		return tipoCritica;
	}

	public void setTipoCritica(int tipoCritica) {
		this.tipoCritica = tipoCritica;
	}

	public boolean isAutorizada() {
		return autorizada;
	}

	public void setAutorizada(boolean autorizada) {
		this.autorizada = autorizada;
	}

	public ProcedimentoInterface getProcedimentoOrigem() {
		return procedimentoOrigem;
	}

	public void setProcedimentoOrigem(ProcedimentoInterface procedimentoOrigem) {
		this.procedimentoOrigem = procedimentoOrigem;
	}

	/**
	 * Retorna o Criticável que originou a critica.
	 * @return
	 */
	public Criticavel getOrigem(){
		if(procedimentoOrigem != null){
			return procedimentoOrigem;
		} else{
			return guia;
		}
	}

}
