package br.com.infowaypi.ecare.services;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.MoneyCalculation;

/**
 * Relatório de exames por consulta em um determinado mês.
 * @author benedito
 *
 */
public class RelatorioExamesPorConsulta {
	public static final String TIPO_GUIA_CONSULTA_ELETIVA = "GCS";
	public static final String TIPO_GUIA_CONSULTA_URGENCIA = "CUR";
	
	private static final String TIPO_GUIA_EXAME = "GEX";
	private static final String PROCEDIMENTO_CONSULTA_DE_URGENCIA = "10101039";

	/**
	 * Intervalo para geração do relatório de exames por consulta.
	 * Ex.: 01/2008 (Janeiro) - 01/01/2008 a 31/01/2008.
	 */
	private Date mes;
	
	/**
	 * Tipo de consulta informado pelo usuário.
	 * Ex.: Consulta Eletiva.
	 */
	private String tipoConsulta;

	/**
	 * Quantidade de consultas realizadas em um determinado mês.
	 * <h2>Observação:</h2>
	 * <ul>Consultas Eletivas - Confirmadas ou Faturadas.</ul>
	 * <ul>Consultas de Urgência - Fechadas ou Faturadas.</ul>
	 */
	private int qtdeConsultasRealizadas;
	
	/**
	 * Quantidade de consultas que deram origem a procedimentos.
	 */
	private int qtdeConsultasComProcedimentos;
	
	/**
	 * Quantidade de procedimentos realizados em um determinado mês.
	 */
	private int qtdeProcedimentos;
	
	/**
	 * Razão entre a quantidade de procedimentos realizados em um determinado mês e a quantidade de consultas que deram origem a procedimentos. 
	 */
	private BigDecimal mediaExamesPorConsulta;

	public RelatorioExamesPorConsulta() {
		this.mediaExamesPorConsulta = BigDecimal.ZERO;
	}
	
	/**
	 * Gera o relatório de exames por consulta de um determinado mês.
	 * @param mes
	 * @param tipoConsulta
	 * @return Resumo e detalhamento do relatório.
	 * @throws ValidateException
	 */
	@SuppressWarnings("unchecked")
	public RelatorioExamesPorConsulta gerarRelatorio(Date mes, String tipoConsulta) throws ValidateException{
		this.validarParametros(mes, tipoConsulta);
		
		List<GuiaSimples<ProcedimentoInterface>> consultasRealizadas = this.buscarConsultasRealizadas(mes, tipoConsulta);
		
		Set<ProcedimentoInterface> procedimentos = this.getProcedimentos(consultasRealizadas);
		
		Set<GuiaSimples<ProcedimentoInterface>> consultasComProcedimentos = this.getConsultasComProcedimentos(procedimentos);
		
		this.mes = mes;
		if (tipoConsulta.equals(TIPO_GUIA_CONSULTA_ELETIVA))
			this.tipoConsulta = "Consulta Eletiva";
		if (tipoConsulta.equals(TIPO_GUIA_CONSULTA_URGENCIA))
			this.tipoConsulta = "Consulta de Urgência";
		this.qtdeConsultasRealizadas = consultasRealizadas.size();
		this.qtdeConsultasComProcedimentos = consultasComProcedimentos.size();
		this.qtdeProcedimentos = this.getQtdeProcedimentosRealizados(procedimentos);
		this.mediaExamesPorConsulta = this.getMedia(this.qtdeProcedimentos, this.qtdeConsultasComProcedimentos);
		
		return this;
	}
	
	/**
	 * Busca no banco de dados todas as consultas (eletivas e de urgência) realizadas em um determinado mês.
	 * <h2>Observação:</h2>
	 * <ul>Consultas Eletivas - Confirmadas ou Faturadas.</ul>
	 * <ul>Consultas de Urgência - Fechadas ou Faturadas.</ul>
	 * @param mes
	 * @param tipoDeGuia
	 * @return Lista de guias.
	 */
	@SuppressWarnings("unchecked")
    private List<GuiaSimples<ProcedimentoInterface>> buscarConsultasRealizadas(Date mes, String tipoDeGuia){
		Calendar dataInicial = new GregorianCalendar();
		dataInicial.setTime(mes);
		
		Calendar dataFinal = new GregorianCalendar();
		dataFinal.setTime(mes);
		dataFinal.set(Calendar.DAY_OF_MONTH, dataFinal.getActualMaximum(Calendar.DAY_OF_MONTH));

        Criteria criteria = HibernateUtil.currentSession().createCriteria(GuiaSimples.class);
        criteria.add(Expression.eq("tipoDeGuia", tipoDeGuia));
        criteria.add(Expression.between("dataAtendimento", dataInicial.getTime(), dataFinal.getTime()));
       
        if (tipoDeGuia.equals(TIPO_GUIA_CONSULTA_ELETIVA))
            criteria.add(Expression.or(Expression.eq("situacao.descricao", SituacaoEnum.CONFIRMADO.descricao()), Expression.eq("situacao.descricao", SituacaoEnum.FATURADA.descricao())));
       
        if (tipoDeGuia.equals(TIPO_GUIA_CONSULTA_URGENCIA))
            criteria.add(Expression.or(Expression.eq("situacao.descricao", SituacaoEnum.FECHADO.descricao()), Expression.eq("situacao.descricao", SituacaoEnum.FATURADA.descricao())));
       
        return criteria.list();
    }
   
	/**
	 * Retorna todos os procedimentos realizados.
	 * @param consultasRealizadas
	 * @return Conjunto de procedimentos.
	 */
    private Set<ProcedimentoInterface> getProcedimentos(List<GuiaSimples<ProcedimentoInterface>> consultasRealizadas){
        Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();
       
        if (consultasRealizadas.size() > 0 && consultasRealizadas.get(0).getTipoDeGuia().equals(TIPO_GUIA_CONSULTA_URGENCIA)){
            procedimentos.addAll(this.getProcedimentosInternos(consultasRealizadas));
        }
        procedimentos.addAll(this.getProcedimentosExternos(consultasRealizadas));

        return procedimentos;
    }
   
    /**
     * Retorna todos os procedimentos internos das consultas de urgência.
     * @param consultasUrgencia
     * @return Conjunto de procedimentos.
     */
    private Set<ProcedimentoInterface> getProcedimentosInternos(List<GuiaSimples<ProcedimentoInterface>> consultasUrgencia){
        Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();
       
        for (GuiaSimples<ProcedimentoInterface> consultaUrgencia : consultasUrgencia) {
            for (ProcedimentoInterface procedimento : consultaUrgencia.getProcedimentosNaoCanceladosENegados()) {
                if (!procedimento.getProcedimentoDaTabelaCBHPM().getCodigo().equals(PROCEDIMENTO_CONSULTA_DE_URGENCIA)){
                    procedimentos.add(procedimento);
                }
            }
        }
       
        return procedimentos;
    }

    /**
     * Retorna todos os procedimentos gerados por guias de exame cuja guia origem é uma guia de consulta.
     * @param consultasRealizadas
     * @return Conjunto de procedimentos.
     */
    private Set<ProcedimentoInterface> getProcedimentosExternos(List<GuiaSimples<ProcedimentoInterface>> consultasRealizadas){
        Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();

        for (GuiaSimples<ProcedimentoInterface> consultaRealizada : consultasRealizadas) {
            for (GuiaSimples<ProcedimentoInterface> guiaFilha : consultaRealizada.getGuiasFilhas()) {
                boolean isGuiaExame = guiaFilha.getTipoDeGuia().equals(TIPO_GUIA_EXAME);
				boolean isConfirmada = guiaFilha.isSituacaoAtual(SituacaoEnum.CONFIRMADO.descricao());
				boolean isFechada = guiaFilha.isSituacaoAtual(SituacaoEnum.FECHADO.descricao());
				boolean isFaturada = guiaFilha.isSituacaoAtual(SituacaoEnum.FATURADA.descricao());
				
				if (isGuiaExame && (isConfirmada || isFechada || isFaturada)){
                    procedimentos.addAll(guiaFilha.getProcedimentosNaoCanceladosENegados());
                }
            }
        }
       
        return procedimentos;
    }

    /**
     * Retorna todas as consultas que geraram exames.
     * @param procedimentos
     * @return Conjunto de guias.
     */
    private Set<GuiaSimples<ProcedimentoInterface>> getConsultasComProcedimentos(Set<ProcedimentoInterface> procedimentos){
        Set<GuiaSimples<ProcedimentoInterface>> consultas = new HashSet<GuiaSimples<ProcedimentoInterface>>();
       
        for (ProcedimentoInterface procedimento : procedimentos) {
            GuiaSimples<ProcedimentoInterface> guia = procedimento.getGuia();
            if (guia.getTipoDeGuia().equals(TIPO_GUIA_EXAME)){
                consultas.add(guia.getGuiaOrigem());
            } else {
                consultas.add(guia);
            }
        }
       
        return consultas;
    }

    /**
     * Retorna a quantidade de procedimentos realizados.
     * @param procedimentos
     * @return Quantidade.
     */
    private Integer getQtdeProcedimentosRealizados(Set<ProcedimentoInterface> procedimentos){
    	Integer quantidade = 0;
    	
    	for (ProcedimentoInterface procedimento : procedimentos) {
			quantidade += procedimento.getQuantidade();
		}
    	
    	return quantidade;
    }
    
    /**
     * Retorna a média de exames por consulta.
     * @param qtdeProcedimentos
     * @param qtdeConsultas
     * @return Média
     */
	private BigDecimal getMedia(Integer qtdeProcedimentos, Integer qtdeConsultas) {
		BigDecimal media = BigDecimal.ZERO;
		
		media = MoneyCalculation.divide(new BigDecimal(qtdeProcedimentos), new BigDecimal(qtdeConsultas).floatValue());
		
		return media.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * Valida os parâmetros necessários para geração do relatório de exames por consulta.
	 * @param mes
	 * @param tipoConsulta
	 * @throws ValidateException
	 */
	private void validarParametros(Date mes, String tipoConsulta) throws ValidateException{
		if (mes == null){
			throw new ValidateException("O intervalo para geração do relatório deve ser informado.");
		}
		
		if (!tipoConsulta.equals(TIPO_GUIA_CONSULTA_ELETIVA) && !tipoConsulta.equals(TIPO_GUIA_CONSULTA_URGENCIA)){
			throw new ValidateException("Tipo de Consulta Inválido.");
		}
	}

	public Date getMes() {
		return mes;
	}

	public void setMes(Date mes) {
		this.mes = mes;
	}

	public String getTipoConsulta() {
		return tipoConsulta;
	}

	public void setTipoConsulta(String tipoConsulta) {
		this.tipoConsulta = tipoConsulta;
	}

	public int getQtdeConsultasRealizadas() {
		return qtdeConsultasRealizadas;
	}

	public void setQtdeConsultasRealizadas(int qtdeConsultasRealizadas) {
		this.qtdeConsultasRealizadas = qtdeConsultasRealizadas;
	}

	public int getQtdeConsultasComProcedimentos() {
		return qtdeConsultasComProcedimentos;
	}

	public void setQtdeConsultasComProcedimentos(int qtdeConsultasComProcedimentos) {
		this.qtdeConsultasComProcedimentos = qtdeConsultasComProcedimentos;
	}

	public int getQtdeProcedimentos() {
		return qtdeProcedimentos;
	}

	public void setQtdeProcedimentos(int qtdeProcedimentos) {
		this.qtdeProcedimentos = qtdeProcedimentos;
	}

	public BigDecimal getMediaExamesPorConsulta() {
		return mediaExamesPorConsulta;
	}

	public void setMediaExamesPorConsulta(BigDecimal mediaExamesPorConsulta) {
		this.mediaExamesPorConsulta = mediaExamesPorConsulta;
	}
	
}
