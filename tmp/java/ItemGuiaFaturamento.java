package br.com.infowaypi.ecarebc.financeiro.faturamento;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.enums.GrauDeParticipacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;

/**
 * Classe que representa os itens da guia que comporão um faturamento
 * 
 * @author Dannylvan
 */
public class ItemGuiaFaturamento implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long idItemGuiaFaturamento;

	private GrauDeParticipacaoEnum grauParticipacao;
	private GuiaFaturavel guia;
	private Profissional profissional;
	private Prestador prestadorDestino;
	private ProcedimentoInterface procedimento;
	private String nomeSegurado;
	private AbstractFaturamento faturamento;
	private BigDecimal valor;

	public GrauDeParticipacaoEnum getGrauParticipacaoEnum() {
		return grauParticipacao;
	}

	public void setGrauParticipacaoEnum(GrauDeParticipacaoEnum grauParticipacao) {
		this.grauParticipacao = grauParticipacao;
	}

	public Integer getGrauParticipacao() {
		if (grauParticipacao != null) {
			return grauParticipacao.getCodigo();
		}
		return null;
	}

	public void setGrauParticipacao(Integer grauParticipacao) {
		this.grauParticipacao = GrauDeParticipacaoEnum.getEnum(grauParticipacao);
	}

	public GuiaFaturavel getGuia() {
		return guia;
	}

	public void setGuia(GuiaFaturavel guia) {
		this.guia = guia;
	}

	public Prestador getPrestadorDestino() {
		return prestadorDestino;
	}

	public void setPrestadorDestino(Prestador prestadorDestino) {
		this.prestadorDestino = prestadorDestino;
	}

	public AbstractFaturamento getFaturamento() {
		return faturamento;
	}

	public void setFaturamento(AbstractFaturamento faturamento) {
		this.faturamento = faturamento;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public Long getIdItemGuiaFaturamento() {
		return idItemGuiaFaturamento;
	}

	public void setIdItemGuiaFaturamento(Long idItemGuiaFaturamento) {
		this.idItemGuiaFaturamento = idItemGuiaFaturamento;
	}

	public Profissional getProfissional() {
		return profissional;
	}

	public void setProfissional(Profissional profissional) {
		this.profissional = profissional;
	}

	public ProcedimentoInterface getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(ProcedimentoInterface procedimento) {
		this.procedimento = procedimento;
	}

	public String getNomeSegurado() {
		return nomeSegurado;
	}

	public void setNomeSegurado(String nomeDoSegurado) {
		this.nomeSegurado = nomeDoSegurado;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ItemGuiaFaturamento)) {
			return false;
		}

		ItemGuiaFaturamento otherObject = (ItemGuiaFaturamento) object;

		return new EqualsBuilder().append(this.getValor(),
				otherObject.getValor()).append(this.getGrauParticipacao(),
				otherObject.getGrauParticipacao()).append(this.getGuia(),
				otherObject.getGuia()).append(this.getProcedimento(),
				otherObject.getProcedimento()).append(this.getProfissional(),
				otherObject.getProfissional()).append(
				this.getPrestadorDestino(), otherObject.getPrestadorDestino())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.valor)
									.append(this.grauParticipacao)
									.append(this.guia)
									.append(this.procedimento)
									.append(this.profissional)
									.append(this.prestadorDestino)
									.toHashCode();
	}

}
