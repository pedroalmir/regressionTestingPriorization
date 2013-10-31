package br.com.infowaypi.ecare.services;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;

import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.MoneyCalculation;

public class RelatorioConsultasPorBeneficiario {
	public static final String TIPO_GUIA_CONSULTA_ELETIVA = "GCS";
	public static final String TIPO_GUIA_CONSULTA_URGENCIA = "CUR";

	private Date mes;

	private Long qtdeSegurados;
	
	private Long qtdeGuiasEletivas;
	private Long qtdeGuiasUrgencia;
	private Long qtdeTotalGuias;
	
	private BigDecimal guiasEletivasBeneficiario;
	private BigDecimal guiasUrgenciaBeneficiario;
	private BigDecimal consultasBeneficiario;
	
	public RelatorioConsultasPorBeneficiario(){
		this.guiasEletivasBeneficiario = BigDecimal.ZERO;
		this.guiasUrgenciaBeneficiario = BigDecimal.ZERO;
		this.consultasBeneficiario = BigDecimal.ZERO;
	}
	
	public RelatorioConsultasPorBeneficiario gerarRelatorio(Date mes) throws ValidateException{
		this.mes = mes;
		
		this.qtdeSegurados = this.buscarSegurados(mes);
		
		this.qtdeGuiasEletivas = this.buscarGuias(TIPO_GUIA_CONSULTA_ELETIVA, mes);
		this.qtdeGuiasUrgencia = this.buscarGuias(TIPO_GUIA_CONSULTA_URGENCIA, mes);
		this.qtdeTotalGuias = this.qtdeGuiasEletivas + this.qtdeGuiasUrgencia;
		
		this.guiasEletivasBeneficiario = this.getMedia(qtdeSegurados, qtdeGuiasEletivas);
		this.guiasUrgenciaBeneficiario = this.getMedia(qtdeSegurados, qtdeGuiasUrgencia);
		this.consultasBeneficiario = this.getMedia(this.qtdeSegurados, this.qtdeTotalGuias);
		
		return this;
	}

	public Long buscarSegurados(Date mes){
		Criteria criteria = HibernateUtil.currentSession().createCriteria(Segurado.class);
		criteria.add(Expression.eq("situacao.descricao", SituacaoEnum.ATIVO.descricao()));
		
		Calendar dataAuxiliar = new GregorianCalendar();
		dataAuxiliar.setTime(this.getTermino(mes));
		dataAuxiliar.add(Calendar.DAY_OF_MONTH, 1);
		
//        A dataAuxiliar é necessária, pois dataSituacao é um Timestamp.
		criteria.add(Expression.le("situacao.dataSituacao", dataAuxiliar.getTime()));
		criteria.setProjection(Projections.rowCount());

		return (Long) criteria.list().get(0);
	}

	private Long buscarGuias(String tipoDeGuia, Date mes) throws ValidateException{
		Criteria criteria = HibernateUtil.currentSession().createCriteria(GuiaSimples.class);
		criteria.add(Expression.between("dataAtendimento", this.getInicio(mes), this.getTermino(mes)));
		criteria.add(Expression.eq("tipoDeGuia", tipoDeGuia));
		
		if (tipoDeGuia.equals(TIPO_GUIA_CONSULTA_ELETIVA)){
			criteria.add(Expression.or(Expression.eq("situacao.descricao", SituacaoEnum.CONFIRMADO.descricao()), Expression.eq("situacao.descricao", SituacaoEnum.FATURADA.descricao())));
		}
		if (tipoDeGuia.equals(TIPO_GUIA_CONSULTA_URGENCIA)){
			criteria.add(Expression.or(Expression.eq("situacao.descricao", SituacaoEnum.AUDITADO.descricao()), Expression.eq("situacao.descricao", SituacaoEnum.FATURADA.descricao())));
		}
		
		criteria.setProjection(Projections.rowCount());
		
		return (Long) criteria.list().get(0);
	}

	public BigDecimal getMedia(Long qtdeSegurados, Long qtdeGuias) {
		BigDecimal media = BigDecimal.ZERO;
		
		media = MoneyCalculation.divide(new BigDecimal(qtdeGuias), new BigDecimal(qtdeSegurados).floatValue());
		
		return media.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	private Date getInicio(Date mes){
		return mes;
	}
	
	private Date getTermino(Date mes){
		GregorianCalendar dataFinal = new GregorianCalendar();
		dataFinal.setTime(mes);
		dataFinal.set(Calendar.DAY_OF_MONTH, dataFinal.getActualMaximum(Calendar.DAY_OF_MONTH));

		return dataFinal.getTime();
	}

	public Date getMes() {
		return mes;
	}

	public void setMes(Date mes) {
		this.mes = mes;
	}

	public Long getQtdeSegurados() {
		return qtdeSegurados;
	}

	public void setQtdeSegurados(Long qtdeSegurados) {
		this.qtdeSegurados = qtdeSegurados;
	}

	public Long getQtdeGuiasEletivas() {
		return qtdeGuiasEletivas;
	}

	public void setQtdeGuiasEletivas(Long qtdeGuiasEletivas) {
		this.qtdeGuiasEletivas = qtdeGuiasEletivas;
	}

	public Long getQtdeGuiasUrgencia() {
		return qtdeGuiasUrgencia;
	}

	public void setQtdeGuiasUrgencia(Long qtdeGuiasUrgencia) {
		this.qtdeGuiasUrgencia = qtdeGuiasUrgencia;
	}

	public Long getQtdeTotalGuias() {
		return qtdeTotalGuias;
	}

	public void setQtdeTotalGuias(Long qtdeTotalGuias) {
		this.qtdeTotalGuias = qtdeTotalGuias;
	}

	public BigDecimal getGuiasEletivasBeneficiario() {
		return guiasEletivasBeneficiario;
	}

	public void setGuiasEletivasBeneficiario(BigDecimal guiasEletivasBeneficiario) {
		this.guiasEletivasBeneficiario = guiasEletivasBeneficiario;
	}

	public BigDecimal getGuiasUrgenciaBeneficiario() {
		return guiasUrgenciaBeneficiario;
	}

	public void setGuiasUrgenciaBeneficiario(BigDecimal guiasUrgenciaBeneficiario) {
		this.guiasUrgenciaBeneficiario = guiasUrgenciaBeneficiario;
	}

	public BigDecimal getConsultasBeneficiario() {
		return consultasBeneficiario;
	}

	public void setConsultasBeneficiario(BigDecimal consultasBeneficiario) {
		this.consultasBeneficiario = consultasBeneficiario;
	}

}
