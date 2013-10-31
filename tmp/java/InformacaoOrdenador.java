/**
 * 
 */
package br.com.infowaypi.ecarebc.financeiro.faturamento.ordenador;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 Classe que representa um conjunto de informaçoes necessarias ao processamento do ordenador.
 * @author <a href="mailto:mquixaba@gmail.com">Marcus Quixabeira</a>
 * @since 2010-07-19 15:09
 *
 */
public class InformacaoOrdenador implements Serializable, Cloneable{

	private static final long serialVersionUID = 1L;
	private Long idInformacaoOrdenador;
	private BigDecimal valorNormal;
	private BigDecimal tetoNormal;
	private BigDecimal valorPassivo;
	private BigDecimal tetoPassivo;
	private Prestador prestador;
	private Ordenador ordenador;
	private Date competencia;
	
	private BigDecimal valorAFaturarNormal;
	private BigDecimal valorAFaturarPassivo;
	
	public InformacaoOrdenador() {
		this.valorNormal = BigDecimal.ZERO;
		this.valorPassivo = BigDecimal.ZERO;
		this.valorAFaturarNormal 	= BigDecimal.ZERO;
		this.valorAFaturarPassivo 	= BigDecimal.ZERO;
		this.tetoNormal = null;
		this.tetoPassivo = null;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		InformacaoOrdenador clone = new InformacaoOrdenador();
		clone.setPrestador(this.getPrestador());
		clone.setCompetencia(this.getCompetencia());
		clone.setOrdenador(this.getOrdenador());
		
		clone.setTetoNormal(this.getTetoNormal());
		clone.setTetoPassivo(this.getTetoPassivo());
		clone.setValorNormal(this.getValorNormal());
		clone.setValorPassivo(this.getValorPassivo());
		clone.setValorAFaturarNormal(this.getValorAFaturarNormal());
		clone.setValorAFaturarPassivo(this.getValorAFaturarPassivo());
		
		return clone;
	}
	
	public boolean validate() throws Exception {
		boolean isTetoNormalNulo = this.getTetoNormal() == null;
		boolean isTetoPassivoNulo = this.getTetoPassivo() == null;
		
		if(!isTetoNormalNulo  && this.getTetoNormal().compareTo(this.getValorNormal()) > 0) {
			throw new ValidateException("O Teto Normal informado para o prestador "+ this.getPrestador().getPessoaJuridica().getFantasia() + " supera o Valor Normal.");
		}
		
		if(!isTetoPassivoNulo && this.getTetoPassivo().compareTo(this.getValorPassivo()) > 0) {
			throw new ValidateException("O Teto Passivo informado para o prestador "+ this.getPrestador().getPessoaJuridica().getFantasia() + " supera o Valor Passivo.");
		}
		return true;
	}
	
	public BigDecimal getValorTotal() {
		return this.valorAFaturarNormal.add(this.valorAFaturarPassivo);
	}
	
	
	private String format(BigDecimal vn2) {
		String s = StringUtils.leftPad(new DecimalFormat("###,###,##0.00")
		.format(vn2.setScale(2, BigDecimal.ROUND_HALF_UP)), 9,
		" ");
		return s;
	}
	
	public String getValorNormalFormatado() {
		return format(valorNormal);
	}
	
	public String getValorPassivoFormatado() {
		return format(valorPassivo);
	}
	
	public String getTetoNormalFormatado() {
		return format(tetoNormal);
	}
	
	public String getTetoPassivoFormatado() {
		return format(tetoPassivo);
	}
	
	public String getValorTotalFormatado() {
		return format(getValorTotal());
	}
	
	public String getValorAFaturarNormalFormatado() {
		return format(getValorAFaturarNormal());
	}
	
	public String getValorAFaturarPassivoFormatado() {
		return format(getValorAFaturarPassivo());
	}
	
	public Long getIdInformacaoOrdenador() {
		return idInformacaoOrdenador;
	}
	
	public void setIdInformacaoOrdenador(Long idInformacaoOrdenador) {
		this.idInformacaoOrdenador = idInformacaoOrdenador;
	}
	
	public BigDecimal getValorNormal() {
		return valorNormal;
	}
	
	public void setValorNormal(BigDecimal valorNormal) {
		this.valorNormal = valorNormal;
	}
	
	public BigDecimal getTetoNormal() {
		return tetoNormal;
	}
	
	public void setTetoNormal(BigDecimal tetoNormal) {
		this.tetoNormal = tetoNormal;
	}
	
	public BigDecimal getValorPassivo() {
		return valorPassivo;
	}
	
	public void setValorPassivo(BigDecimal valorPassivo) {
		this.valorPassivo = valorPassivo;
	}
	
	public BigDecimal getTetoPassivo() {
		return tetoPassivo;
	}
	
	public void setTetoPassivo(BigDecimal tetoPassivo) {
		this.tetoPassivo = tetoPassivo;
	}
	
	public Prestador getPrestador() {
		return prestador;
	}
	
	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
	}
	
	public Ordenador getOrdenador() {
		return ordenador;
	}
	
	public void setOrdenador(Ordenador ordenador) {
		this.ordenador = ordenador;
	}
	
	public Date getCompetencia() {
		return this.competencia;
	}
	
	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}
	
	public BigDecimal getValorAFaturarNormal() {
		return valorAFaturarNormal;
	}

	public void setValorAFaturarNormal(BigDecimal valorAFaturarNormal) {
		this.valorAFaturarNormal = valorAFaturarNormal;
	}

	public BigDecimal getValorAFaturarPassivo() {
		return valorAFaturarPassivo;
	}

	public void setValorAFaturarPassivo(BigDecimal valorAFaturarPassivo) {
		this.valorAFaturarPassivo = valorAFaturarPassivo;
	}
	
}
