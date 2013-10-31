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

import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.MoneyCalculation;

/**
 * Relatório de exames por internação de um determinado mês.
 * @author benedito
 *
 */
public class RelatorioExamesPorInternacao {
	public static final String TIPO_INTERNACAO_ELETIVA = "IEL";
	public static final String TIPO_INTERNACAO_URGENCIA = "IUR";
	
	private static final String TIPO_GUIA_EXAME = "GEX";

	/**
	 * Intervalo para geração do relatório de exames por internação.
	 * Ex.: 01/2008 (Janeiro) - 01/01/2008 a 31/01/2008.
	 */
	private Date mes;
	
	/**
	 * Tipo de internação informado pelo usuário.
	 * Ex.: Internação Eletiva.
	 */
	private String tipoInternacao;

	/**
	 * Quantidade de internações realizadas em um determinado mês.
	 * <h2>Observação:</h2>
	 * <ul>Internações Fechadas ou Faturadas.</ul>
	 */
	private int qtdeInternacoesRealizadas;
	
	/**
	 * Quantidade de internações que deram origem a procedimentos.
	 */
	private int qtdeInternacoesComProcedimentos;
	
	/**
	 * Quantidade de procedimentos realizados em um determinado mês.
	 */
	private int qtdeProcedimentos;
	
	/**
	 * Razão entre a quantidade de procedimentos realizados em um determinado mês e a quantidade de internações que deram origem a procedimentos. 
	 */
	private BigDecimal mediaExamesPorInternacao;
	
	public RelatorioExamesPorInternacao() {
		this.mediaExamesPorInternacao = BigDecimal.ZERO;
	}

	/**
	 * Gera o relatório de exames por internação de um determinado mês.
	 * @param mes
	 * @param tipoInternacao
	 * @return Resumo e detalhamento do relatório.
	 * @throws ValidateException
	 */
	@SuppressWarnings("unchecked")
	public RelatorioExamesPorInternacao gerarRelatorio(Date mes, String tipoInternacao) throws ValidateException{
		this.validarParametros(mes, tipoInternacao);
		
        List<GuiaInternacao> internacoesRealizadas = this.buscarInternacoesRealizadas(mes, tipoInternacao);
        
        Set<ProcedimentoInterface> procedimentos = this.getProcedimentos(internacoesRealizadas);

        Set<GuiaSimples<ProcedimentoInterface>> internacoesComProcedimentos = this.getInternacoesComProcedimentos(procedimentos);

        this.mes = mes;
        if (tipoInternacao.equals(TIPO_INTERNACAO_ELETIVA))
        	this.tipoInternacao = "Internação Eletiva";
        if (tipoInternacao.equals(TIPO_INTERNACAO_URGENCIA))
        	this.tipoInternacao = "Internação de Urgência";
        this.qtdeInternacoesRealizadas = internacoesRealizadas.size();
		this.qtdeInternacoesComProcedimentos = internacoesComProcedimentos.size();
		this.qtdeProcedimentos = this.getQtdeProcedimentosRealizados(procedimentos);
		this.mediaExamesPorInternacao = this.getMedia(this.qtdeProcedimentos, this.qtdeInternacoesComProcedimentos);
		
		return this;
	}

	/**
	 * Busca no banco de dados todas as internações realizada em um determinado mês.
	 * <h2>Observação:</h2>
	 * <ul>Internações Fechadas ou Faturadas.</ul>
	 * @param mes
	 * @param tipoInternacao
	 * @return Lista de guias.
	 */
	@SuppressWarnings("unchecked")
    private List<GuiaInternacao> buscarInternacoesRealizadas(Date mes, String tipoInternacao){
		Calendar dataInicial = new GregorianCalendar();
		dataInicial.setTime(mes);
		
		Calendar dataFinal = new GregorianCalendar();
		dataFinal.setTime(mes);
		dataFinal.set(Calendar.DAY_OF_MONTH, dataFinal.getActualMaximum(Calendar.DAY_OF_MONTH));

		Criteria criteria = HibernateUtil.currentSession().createCriteria(GuiaInternacao.class);
        criteria.add(Expression.eq("tipoDeGuia", tipoInternacao));
        criteria.add(Expression.between("dataAtendimento", dataInicial.getTime(), dataFinal.getTime()));
        criteria.add(Expression.or(Expression.eq("situacao.descricao", SituacaoEnum.FECHADO.descricao()), Expression.eq("situacao.descricao", SituacaoEnum.FATURADA.descricao())));
       
        return criteria.list();
    }
   
	/**
	 * Busca todos os procedimentos realizados.
	 * @param internacoesRealizadas
	 * @return Conjunto de procedimentos.
	 */
    private Set<ProcedimentoInterface> getProcedimentos(List<GuiaInternacao> internacoesRealizadas){
        Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();
       
        procedimentos.addAll(this.getProcedimentosInternos(internacoesRealizadas));
        procedimentos.addAll(this.getProcedimentosExternos(internacoesRealizadas));

        return procedimentos;
    }
   
    /**
     * Retorna todos os procedimentos internos das guias de internação.
     * @param internacoesRealizadas
     * @return Conjunto de procedimentos.
     */
    private Set<ProcedimentoInterface> getProcedimentosInternos(List<GuiaInternacao> internacoesRealizadas){
        Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();
       
        for (GuiaInternacao internacaoRealizada : internacoesRealizadas) {
            procedimentos.addAll(internacaoRealizada.getProcedimentosNaoCanceladosENegados());
        }
       
        return procedimentos;
    }

    /**
     * Retorna todos os procedimentos gerados por guias de exame cuja guia origem é uma guia de internação.
     * @param internacoesRealizadas
     * @return Conjunto de procedimentos.
     */
    private Set<ProcedimentoInterface> getProcedimentosExternos(List<GuiaInternacao> internacoesRealizadas){
        Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();

        for (GuiaInternacao internacaoRealizada : internacoesRealizadas) {
            for (GuiaSimples<ProcedimentoInterface> guiaExame : internacaoRealizada.getGuiasExameExterno()) {
				boolean isConfirmada = guiaExame.isSituacaoAtual(SituacaoEnum.CONFIRMADO.descricao());
				boolean isFechada = guiaExame.isSituacaoAtual(SituacaoEnum.FECHADO.descricao());
				boolean isFaturada = guiaExame.isSituacaoAtual(SituacaoEnum.FATURADA.descricao());
            	
                if (isConfirmada || isFechada || isFaturada){
                    procedimentos.addAll(guiaExame.getProcedimentosNaoCanceladosENegados());
                }
            }
        }
       
        return procedimentos;
    }
   
    /**
     * Retorna todas as internações que geraram exames.
     * @param procedimentos
     * @return Conjunto de guias.
     */
    private Set<GuiaSimples<ProcedimentoInterface>> getInternacoesComProcedimentos(Set<ProcedimentoInterface> procedimentos){
        Set<GuiaSimples<ProcedimentoInterface>> internacoes = new HashSet<GuiaSimples<ProcedimentoInterface>>();
       
        for (ProcedimentoInterface procedimento : procedimentos) {
            GuiaSimples<ProcedimentoInterface> guia = procedimento.getGuia();
            if (guia.getTipoDeGuia().equals(TIPO_GUIA_EXAME)){
                internacoes.add(guia.getGuiaOrigem());
            } else {
                internacoes.add(guia);
            }
        }
       
        return internacoes;
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
    * Retorna a média de exames por internação.
    * @param qtdeProcedimentos
    * @param qtdeInternacoes
    * @return Média.
    */
    private BigDecimal getMedia(Integer qtdeProcedimentos, Integer qtdeInternacoes) {
        BigDecimal media = BigDecimal.ZERO;
       
        media = MoneyCalculation.divide(new BigDecimal(qtdeProcedimentos), new BigDecimal(qtdeInternacoes).floatValue());
       
        return media.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

 	/**
	 * Valida os parâmetros necessários para geração do relatório de exames por consulta.
	 * @param mes
	 * @param tipoInternacao
	 * @throws ValidateException
	 */
	private void validarParametros(Date mes, String tipoInternacao) throws ValidateException{
		if (mes == null){
			throw new ValidateException("O intervalo para geração do relatório deve ser informado.");
		}
		
		if (!tipoInternacao.equals(TIPO_INTERNACAO_ELETIVA) && !tipoInternacao.equals(TIPO_INTERNACAO_URGENCIA)){
			throw new ValidateException("Tipo de Internação Inválido.");
		}
	}

	public Date getMes() {
		return mes;
	}

	public void setMes(Date mes) {
		this.mes = mes;
	}

	public String getTipoInternacao() {
		return tipoInternacao;
	}

	public void setTipoInternacao(String tipoInternacao) {
		this.tipoInternacao = tipoInternacao;
	}

	public int getQtdeInternacoesRealizadas() {
		return qtdeInternacoesRealizadas;
	}

	public void setQtdeInternacoesRealizadas(int qtdeInternacoesRealizadas) {
		this.qtdeInternacoesRealizadas = qtdeInternacoesRealizadas;
	}

	public int getQtdeInternacoesComProcedimentos() {
		return qtdeInternacoesComProcedimentos;
	}

	public void setQtdeInternacoesComProcedimentos(int qtdeInternacoesComProcedimentos) {
		this.qtdeInternacoesComProcedimentos = qtdeInternacoesComProcedimentos;
	}

	public int getQtdeProcedimentos() {
		return qtdeProcedimentos;
	}

	public void setQtdeProcedimentos(int qtdeProcedimentos) {
		this.qtdeProcedimentos = qtdeProcedimentos;
	}

	public BigDecimal getMediaExamesPorInternacao() {
		return mediaExamesPorInternacao;
	}

	public void setMediaExamesPorInternacao(BigDecimal mediaExamesPorInternacao) {
		this.mediaExamesPorInternacao = mediaExamesPorInternacao;
	}

}
