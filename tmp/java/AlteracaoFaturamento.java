package br.com.infowaypi.ecarebc.financeiro.faturamento;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * Classe que representa um item da colecao de <code>alteracoesFaturamento</code>
 * do objeto fatumento que traz o histórico das alterações feitas no faturamento
 * @author Diogo Vinícius
 *
 */
public class AlteracaoFaturamento implements Serializable{
	
	public static final Character STATUS_ATIVO = 'A';
	public static final Character STATUS_CANCELADO = 'C';
	
	private static final long serialVersionUID = 1L;
	private Long idAlteracaoFaturamento;
	private AbstractFaturamento faturamento;
	private Date data;
	private BigDecimal valorIncremento;
	private BigDecimal valorDecremento;
	private String motivo;
	private String motivoCancelamento;
	private Character status;
	private UsuarioInterface usuario;
	private UsuarioInterface usuarioCancelamento;
	private boolean cancelar;
	
		
	public UsuarioInterface getUsuarioCancelamento() {
		return usuarioCancelamento;
	}

	public void setUsuarioCancelamento(UsuarioInterface usuarioCancelamento) {
		this.usuarioCancelamento = usuarioCancelamento;
	}

	public AlteracaoFaturamento(){
		this.valorIncremento = BigDecimal.ZERO;
		this.valorDecremento = BigDecimal.ZERO;
	}

	//getters n' setters from fields
	public Long getIdAlteracaoFaturamento() {
		return idAlteracaoFaturamento;
	}

	public void setIdAlteracaoFaturamento(Long idAlteracaoFaturamento) {
		this.idAlteracaoFaturamento = idAlteracaoFaturamento;
	}

	public AbstractFaturamento getFaturamento() {
		return faturamento;
	}

	public void setFaturamento(AbstractFaturamento faturamento) {
		this.faturamento = faturamento;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public BigDecimal getValorIncremento() {
		return valorIncremento;
	}

	public void setValorIncremento(BigDecimal valorIncremento) {
		this.valorIncremento = valorIncremento;
	}

	public BigDecimal getValorDecremento() {
		return valorDecremento;
	}

	public void setValorDecremento(BigDecimal valorDecremento) {
		this.valorDecremento = valorDecremento;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public UsuarioInterface getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioInterface usuario) {
		this.usuario = usuario;
	}
	
	public BigDecimal getSaldo() {
		return this.valorIncremento.subtract(this.valorDecremento).setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public String getMotivoCancelamento() {
		return motivoCancelamento;
	}

	public void setMotivoCancelamento(String motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}

	public Character getStatus() {
		return status;
	}

	public void setStatus(Character status) {
		this.status = status;
	}

	public boolean isCancelar() {
		return cancelar;
	}

	public void setCancelar(boolean cancelar) {
		this.cancelar = cancelar;
	}
	
	
	
}
