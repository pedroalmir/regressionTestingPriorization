package br.com.infowaypi.ecare.atendimentos;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;

import br.com.infowaypi.ecare.segurados.tuning.SeguradoConsignacaoTuning;
import br.com.infowaypi.ecarebc.financeiro.FluxoFinanceiroInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.user.UsuarioInterface;

/** 
 * Classe que representa a estrutura básica de uma guia no sistema
 */
public class GuiaSimplesTuning<P extends ProcedimentoInterface> extends ImplColecaoSituacoesComponent {

	private static final long serialVersionUID = 1L;
	
	private Long idGuia;
	private SeguradoConsignacaoTuning segurado;
	private FluxoFinanceiroInterface fluxoFinanceiro;
	private String autorizacao;
	private Date dataAtendimento;
	private BigDecimal valorTotal;
	private BigDecimal valorCoParticipacao;

	public GuiaSimplesTuning(){
		this(null);
	}
	
	public GuiaSimplesTuning(UsuarioInterface usuario){
		this.dataAtendimento = new Date();
		this.valorTotal = BigDecimal.valueOf(0.0);
	}

	public Long getIdGuia() {
		return idGuia;
	}

	public void setIdGuia(Long idGuia) {
		this.idGuia = idGuia;
	}

	public SeguradoConsignacaoTuning getSegurado() {
		return segurado;
	}

	public void setSegurado(SeguradoConsignacaoTuning segurado) {
		this.segurado = segurado;
	}

	public FluxoFinanceiroInterface getFluxoFinanceiro() {
		return fluxoFinanceiro;
	}

	public void setFluxoFinanceiro(FluxoFinanceiroInterface fluxoFinanceiro) {
		this.fluxoFinanceiro = fluxoFinanceiro;
	}

	public String getAutorizacao() {
		return autorizacao;
	}

	public void setAutorizacao(String autorizacao) {
		this.autorizacao = autorizacao;
	}

	public Date getDataAtendimento() {
		return dataAtendimento;
	}

	public void setDataAtendimento(Date dataAtendimento) {
		this.dataAtendimento = dataAtendimento;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public BigDecimal getValorCoParticipacao() {
		return valorCoParticipacao;
	}

	public void setValorCoParticipacao(BigDecimal valorCoParticipacao) {
		this.valorCoParticipacao = valorCoParticipacao;
	}
	
	@Override
	public int hashCode() {
		return autorizacao.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof GuiaSimplesTuning)) {
			return false;
		}
		GuiaSimplesTuning otherObject = (GuiaSimplesTuning) object;
		return new EqualsBuilder()
			.append(this.getAutorizacao(), otherObject.getAutorizacao())
			.isEquals();
	}
}