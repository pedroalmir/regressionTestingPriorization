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
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Relat�rio que apresenta o total de exames autorizados por interna��o em um determinado periodo.
 * Inclui somente os exames que necessitam de autoriza��o pr�via.
 * @author benedito
 *
 */
public class RelatorioExamesAutorizadosPorInternacao {
    public static final String TIPO_INTERNACAO_ELETIVA = "IEL";
    public static final String TIPO_INTERNACAO_URGENCIA = "IUR";
    
    private static final String TIPO_GUIA_EXAME = "GEX";

    /**
     * Tipo de interna��o informado pelo usu�rio.
     * Ex.: Interna��o Eletiva.
     */
    private String tipoInternacao;
    
    /**
     * Quantidade de interna��es autorizadas no intervalo informado.
     */
    private int qtdeInternacoesAutorizadas;
    
    /**
     * Quantidade de interna��es que deram origem a procedimentos autorizados.
     */
    private int qtdeInternacoesComProcedimentosAutorizados;
    
    /**
     * Quantidade de procedimentos autorizados.
     */
    private int qtdeProcedimentosAutorizados;
    
    /**
     * Raz�o entre a quantidade de procedimentos autorizados e a quantidade de interna��es que deram origem a procedimentos autorizados. 
     */
    private BigDecimal mediaExamesAutorizadosPorInternacao;
    
	public RelatorioExamesAutorizadosPorInternacao() {
		this.mediaExamesAutorizadosPorInternacao = BigDecimal.ZERO;
	}

	/**
	 * Gera o relat�rio de exames autorizados por interna��o.
	 * @param dataInicial
	 * @param dataFinal
	 * @param tipoInternacao
	 * @return Resumo e detalhamento do relat�rio.
	 * @throws ValidateException
	 */
	public RelatorioExamesAutorizadosPorInternacao gerarRelatorio(Date dataInicial, Date dataFinal, String tipoInternacao) throws ValidateException{
		this.validarIntervalo(dataInicial, dataFinal);
		this.validarTipoInternacao(tipoInternacao);
		
		List<GuiaInternacao> internacoesAutorizadas = this.buscarInternacoesAutorizadas(dataInicial, dataFinal, tipoInternacao);
		Set<ProcedimentoInterface> procedimentosAutorizados = this.getProcedimentosAutorizados(internacoesAutorizadas);

        if (tipoInternacao.equals(TIPO_INTERNACAO_ELETIVA))
        	this.tipoInternacao = "Interna��o Eletiva";
        if (tipoInternacao.equals(TIPO_INTERNACAO_URGENCIA))
        	this.tipoInternacao = "Interna��o de Urg�ncia";
		this.qtdeInternacoesAutorizadas = internacoesAutorizadas.size();
		this.qtdeInternacoesComProcedimentosAutorizados = this.getInternacoesComProcedimentosAutorizados(procedimentosAutorizados).size();
		this.qtdeProcedimentosAutorizados = this.getQtdeProcedimentosPreAutorizaveis(procedimentosAutorizados);
		this.mediaExamesAutorizadosPorInternacao = this.getMedia(this.qtdeProcedimentosAutorizados, this.qtdeInternacoesComProcedimentosAutorizados);
		
		return this;
	}
	
	/**
	 * Busca no banco de dados todas as interna��es autorizadas no intervalo informado.
	 * <h2>Observa��o:</h2>
	 * <ul>Interna��o Eletiva - Autorizada.</ul>
	 * <ul>Interna��o Urg�ncia - Aberta.</ul>
	 * @param dataInicial
	 * @param dataFinal
	 * @param tipoInternacao
	 * @return Lista de guias.
	 */
	@SuppressWarnings("unchecked")
	private List<GuiaInternacao> buscarInternacoesAutorizadas(Date dataInicial, Date dataFinal, String tipoInternacao){
		Criteria criteria = HibernateUtil.currentSession().createCriteria(GuiaInternacao.class);
		criteria.add(Expression.eq("tipoDeGuia", tipoInternacao));
		criteria.createAlias("colecaoSituacoes", "col");
		criteria.createAlias("col.situacoes", "sit");

		if (tipoInternacao.equals(TIPO_INTERNACAO_ELETIVA)){
			criteria.add(Expression.eq("sit.descricao", SituacaoEnum.AUTORIZADO.descricao()));
		}
		if (tipoInternacao.equals(TIPO_INTERNACAO_URGENCIA)){
			// TODO Substituir a situa��o ABERTO por AUTORIZADO quando a situa��o das guias estiverem bem definidas.
			criteria.add(Expression.eq("sit.descricao", SituacaoEnum.ABERTO.descricao()));
		}

		Calendar dataAuxiliar = new GregorianCalendar();
		dataAuxiliar.setTime(dataFinal);
		dataAuxiliar.add(Calendar.DAY_OF_MONTH, 1);
//		A dataAuxiliar � necess�ria, pois dataSituacao � um Timestamp.
		criteria.add(Expression.between("sit.dataSituacao", dataInicial, dataAuxiliar.getTime()));

		return criteria.list();
	}
	
	/**
	 * Retorna os procedimentos autorizados que necessitam de autoriza��o pr�via.
	 * @param internacoesAutorizadas
	 * @return Lista de procedimentos.
	 */
	private Set<ProcedimentoInterface> getProcedimentosAutorizados(List<GuiaInternacao> internacoesAutorizadas){
		Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();

		procedimentos.addAll(this.getProcedimentosInternosAutorizados(internacoesAutorizadas));
		procedimentos.addAll(this.getProcedimentosExternosAutorizados(internacoesAutorizadas));
		
		return procedimentos;
	}
	
    /**
     * Retorna todos os procedimentos internos das guias de interna��o que foram autorizados.
     * @param internacoesAutorizadas
     * @return Conjunto de procedimentos.
     */
    private Set<ProcedimentoInterface> getProcedimentosInternosAutorizados(List<GuiaInternacao> internacoesAutorizadas){
        Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();
       
        for (GuiaInternacao internacaoAutorizada : internacoesAutorizadas) {
			for (ProcedimentoInterface procedimentoAutorizado : internacaoAutorizada.getProcedimentosAutorizados()) {
				if (!procedimentoAutorizado.getProcedimentoDaTabelaCBHPM().getNivel().equals(TabelaCBHPM.NIVEL_1)){
					procedimentos.add(procedimentoAutorizado);
				}
			}
        }
       
        return procedimentos;
    }

    /**
     * Retorna todos os procedimentos externos das guias de interna��o que foram autorizados.
     * @param internacoesAutorizadas
     * @return Conjunto de procedimentos.
     */
    private Set<ProcedimentoInterface> getProcedimentosExternosAutorizados(List<GuiaInternacao> internacoesAutorizadas){
        Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();

        for (GuiaInternacao internacaoAutorizada : internacoesAutorizadas) {
            for (GuiaSimples<ProcedimentoInterface> guiaExame : internacaoAutorizada.getGuiasExameExterno()) {
				boolean isConfirmada = guiaExame.isSituacaoAtual(SituacaoEnum.CONFIRMADO.descricao());
				boolean isFechada = guiaExame.isSituacaoAtual(SituacaoEnum.FECHADO.descricao());
				boolean isFaturada = guiaExame.isSituacaoAtual(SituacaoEnum.FATURADA.descricao());
            	
                if (isConfirmada || isFechada || isFaturada){
        			for (ProcedimentoInterface procedimentoAutorizado : guiaExame.getProcedimentosAutorizados()) {
        				if (!procedimentoAutorizado.getProcedimentoDaTabelaCBHPM().getNivel().equals(TabelaCBHPM.NIVEL_1)){
        					procedimentos.add(procedimentoAutorizado);
        				}
        			}
                }
            }
        }
       
        return procedimentos;
    }
    
	/**
	 * Retorna as interna��es que possuem procedimentos autorizados.
	 * Inclui somente procedimentos que necessitam de autoriza��o pr�via.
	 * @param procedimentosAutorizados
	 * @return Conjunto de guias.
	 */
    private Set<GuiaSimples<ProcedimentoInterface>> getInternacoesComProcedimentosAutorizados(Set<ProcedimentoInterface> procedimentosAutorizados){
    	Set<GuiaSimples<ProcedimentoInterface>> internacoes = new HashSet<GuiaSimples<ProcedimentoInterface>>();
    
        for (ProcedimentoInterface procedimentoAutorizado : procedimentosAutorizados) {
            GuiaSimples<ProcedimentoInterface> guia = procedimentoAutorizado.getGuia();
            if (guia.getTipoDeGuia().equals(TIPO_GUIA_EXAME)){
                internacoes.add(guia.getGuiaOrigem());
            } else {
                internacoes.add(guia);
            }
        }

    	return internacoes;
    }
    
    /**
     * Retorna a quantidade de procedimentos autorizados.
     * @param procedimentos
     * @return Quantidade.
     */
    private Integer getQtdeProcedimentosPreAutorizaveis(Set<ProcedimentoInterface> procedimentos){
    	Integer quantidade = 0;
    	
    	for (ProcedimentoInterface procedimento : procedimentos) {
    		quantidade += procedimento.getQuantidade();
    	}
    	
    	return quantidade;
    }
    
    /**
     * Retorna a m�dia de exames autorizados por interna��o.
     * @param qtdeProcedimentos
     * @param qtdeInternacoes
     * @return
     */
	public BigDecimal getMedia(Integer qtdeProcedimentos, Integer qtdeInternacoes) {
		BigDecimal media = BigDecimal.ZERO;
		
		media = MoneyCalculation.divide(new BigDecimal(qtdeProcedimentos), new BigDecimal(qtdeInternacoes).floatValue());
		
		return media.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * Valida o intervalo para gera��o do relat�rio de exames autorizados por interna��o.
	 * O intervalo m�ximo de pesquisa � 01(um) m�s.
	 * @param dataInicial
	 * @param dataFinal
	 * @throws ValidateException
	 */
	private void validarIntervalo(Date dataInicial, Date dataFinal) throws ValidateException{
		Calendar inicio = new GregorianCalendar();
		inicio.setTime(dataInicial);
		
		Calendar termino = new GregorianCalendar();
		termino.setTime(dataFinal);

		if(Utils.compareData(inicio.getTime(), termino.getTime()) > 0){
			throw new ValidateException("A data inicial n�o pode ser maior que a data final.");
		}
		
		if(Utils.diferencaEmDias(inicio, termino) > 30){
			throw new ValidateException("O intervalo entre a data inicial e a data final n�o pode ser maior que 30 dias.");
		}
	}
	
	/**
	 * Valida o tipo de interna��o selecionada pelo usu�rio.
	 * � permitido somente interna��o eletiva e de urg�ncia.
	 * @param tipoInternacao
	 * @throws ValidateException
	 */
	private void validarTipoInternacao(String tipoInternacao) throws ValidateException{
		if (tipoInternacao == null || tipoInternacao.equals("")){
			throw new ValidateException("Tipo Nulo ou Vazio");
		}
		
		if (!tipoInternacao.equals(TIPO_INTERNACAO_ELETIVA) && !tipoInternacao.equals(TIPO_INTERNACAO_URGENCIA)){
			throw new ValidateException("Tipo Inv�lido");
		}
	}

	public String getTipoInternacao() {
		return tipoInternacao;
	}

	public void setTipoInternacao(String tipoInternacao) {
		this.tipoInternacao = tipoInternacao;
	}

	public int getQtdeInternacoesAutorizadas() {
		return qtdeInternacoesAutorizadas;
	}

	public void setQtdeInternacoesAutorizadas(int qtdeInternacoesAutorizadas) {
		this.qtdeInternacoesAutorizadas = qtdeInternacoesAutorizadas;
	}

	public int getQtdeInternacoesComProcedimentosAutorizados() {
		return qtdeInternacoesComProcedimentosAutorizados;
	}

	public void setQtdeInternacoesComProcedimentosAutorizados(int qtdeInternacoesComProcedimentosAutorizados) {
		this.qtdeInternacoesComProcedimentosAutorizados = qtdeInternacoesComProcedimentosAutorizados;
	}

	public int getQtdeProcedimentosAutorizados() {
		return qtdeProcedimentosAutorizados;
	}

	public void setQtdeProcedimentosAutorizados(int qtdeProcedimentosAutorizados) {
		this.qtdeProcedimentosAutorizados = qtdeProcedimentosAutorizados;
	}

	public BigDecimal getMediaExamesAutorizadosPorInternacao() {
		return mediaExamesAutorizadosPorInternacao;
	}

	public void setMediaExamesAutorizadosPorInternacao(BigDecimal mediaExamesAutorizadosPorInternacao) {
		this.mediaExamesAutorizadosPorInternacao = mediaExamesAutorizadosPorInternacao;
	}

}
