package br.com.infowaypi.ecarebc.painelDeControle;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import br.com.infowaypi.ecare.scheduller.sms.IntervaloDeTempo;
import br.com.infowaypi.ecarebc.procedimentos.TipoAcomodacao;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * Centralizar as atributos de configuração utilizadas no sistema.
 *
 * @author SR & iHealth Team - Dannylvan, Jefferson, Marcos Roberto
 */
public class PainelDeControle implements Serializable{
	
	private static final long serialVersionUID = 6971446999165410319L;
	
	/**
	 * Painel de controle que caracteriza o Singleton
	 */
	private static PainelDeControle painel;
	
	private Long idPainelDeControle;
	
	/*entrega de Lote*/
	/**
	 * Prazo para entrega de lote de guia sem aplicação de multa por atraso
	 */
	private int prazoDeEntregaDeLoteSemMulta;
	
	/**
	 * Porcentagem de multa sobre o valor total da guia, correspondente ao atraso na entrega
	 */
	private BigDecimal multaPorAtrasoDeEntregaDeLote;
	
	/**
	 * Prazo final para entrega do lote. Após este prazo as guias não serão mais recebidas.
	 */
	private int prazoFinalParaEntregaDeLote;
	
	private Date dataVigenciaPrazoFinalEntregaDeLote;
	
	/**
	 * carência para Doenças e Lesões Pré-existentes (DLPs)
	 */
	private int carenciaDLPs;
	
	/**
	 * Emails utilizados para o recebimento de alertas que informam
	 * sobre o acesso ao Portal do Beneficiário
	 */
	private Set<EmailPainel> emailsPortal;
	
	/**
	 * Emails utilizados para o recebimento de alertas que informam quais contratos 
	 * estão prestes a vencer, ou já estão vencidos (Marcos Roberto - 12.06.2012). 
	 */
	private Set<EmailPainel> emailsContratos;

	/**
	 * Quantidade de dias definidos para envio do 1o. alerta (através de email) que 
	 * existem prestadores com contratos prestes a vencer. 
	 */
	private Integer quantidadeDiasEnvioPrimeiroEmail;

	/**
	 * Total de salários base máximo, a ser considerado nos calculos de financiamento 
	 * tanto de boletos quanto de consignações (Marcos Roberto - 16.11.2011).
	 */
	private int quantidadeMaximaDeSalarios;
	
	/**
	 * Valor do salário mínimo vigente para uso no cálculo juntamente com o atributo
	 * acima <quantidadeMaximaDeSalarios> 16.11.2011.
	 */
	private BigDecimal valorSalarioMinimoVigente;

	/**
	 * Percentual a ser deduzido sobre o salário base para financiamento de consumo.
	 */
	private BigDecimal aliquotaDeFinanciamento;
	
	/**
	 * Verificar se o valor dos procedimento devem ser valorados pelo porte(true)
	 * caso contrário (false) o valor será extraído do próprio procedimento (classe TabelaCBHPM).
	 */
	private boolean procedimentoValoradoPeloPorte;
	
	/**
	 * Ativar validação: caso o tipo de acomodação de uma guia seja APARTAMENTO, dobrar valor dos procedimentos
	 * cirúrgicos pertencentes ao capítulo 3 da tabela CBHPM utilizando o porte da tabela ao
	 * invés do valor moderado da tabela
	 * */
	private boolean procedimentoDobrado;
	
	/**
	 * Tipos de acomodação que disparam a validação acima
	 * */
	private Set<TipoAcomodacao> tiposDeAcomodacaoDobradas; 
	
	/**
	 * Periodo em dias para a realização de tratamentos odontológicos simples.
	 */
	private int periodicidadeTratamentoOdonto;
	
	/**
	 * Quantidade de porcedimentos que podem ser realizados no intervalo 
	 * de periodicidade de tratamento simples odonto.
	 */
	private int quantidadeTratementoOdonto;
	
	/**
	 * Intervalo de verificação por novas guias para envio de mensagem SMS
	 * */
	private int smsIntervaloVerificacaoEmMinutos;
	
	/**
	 * Numero para envio de mensagens em horário NÃO comercial
	 * */
	private String smsNumeroEmHorarioNaoComercial;
	
	/**
	 * Email para aviso de pendencias
	 * */
	private String emailRegulador;
	
	/**
	 * Intervalo de horas para enviar sms
	 * 
	 * */
	private Set<IntervaloDeTempo> smsHorarios;
	
	/**
	 * Prazo máximo para a solicitação de exames / procedimento / pacotes
	 * em uma guia de consulta de urgência
	 */
	private Integer prazoSolicitacaoEmConsultaUrgencia;
	
	private Integer tempoLimiteRecursoGlosa;

	@Deprecated
	public PainelDeControle(){
		this.valorSalarioMinimoVigente = BigDecimal.ZERO;
		this.aliquotaDeFinanciamento = BigDecimal.ZERO;
		this.procedimentoValoradoPeloPorte = false;
		this.procedimentoDobrado = false;
		this.tiposDeAcomodacaoDobradas = new HashSet<TipoAcomodacao>();
		this.smsIntervaloVerificacaoEmMinutos = 5;
		this.smsHorarios = new HashSet<IntervaloDeTempo>();
		this.tempoLimiteRecursoGlosa = 0;
	}
	
	public static PainelDeControle reload(){
		painel = null;
		return getPainel();
	}
	
	public static PainelDeControle getPainel(){
		if(painel == null){
			loadPainel();
			if(painel == null){
				painel = new PainelDeControle();
			}
		}
		return painel;
	}

	public static PainelDeControle reloadPainel() {
		painel = null;
		return getPainel();
	}
	
	public String getEmailRegulador() {
	    return emailRegulador;
	}

	public void setEmailRegulador(String emailRegulador) {
	    this.emailRegulador = emailRegulador;
	}

	public String getSmsNumeroEmHorarioNaoComercial() {
	    return smsNumeroEmHorarioNaoComercial;
	}

	public void setSmsNumeroEmHorarioNaoComercial(
		String smsNumeroEmHorarioNaoComercial) {
	    this.smsNumeroEmHorarioNaoComercial = smsNumeroEmHorarioNaoComercial;
	}
	
	public Set<IntervaloDeTempo> getSmsHorarios() {
	    return smsHorarios;
	}

	public void setSmsHorarios(Set<IntervaloDeTempo> smsHorarios) {
	    this.smsHorarios = smsHorarios;
	}

	public static long getSerialversionuid() {
	    return serialVersionUID;
	}

	public int getSmsIntervaloVerificacaoEmMinutos() {
	    return smsIntervaloVerificacaoEmMinutos;
	}

	public void setSmsIntervaloVerificacaoEmMinutos(int smsIntervaloVerificacaoEmMinutos) {
	    this.smsIntervaloVerificacaoEmMinutos = smsIntervaloVerificacaoEmMinutos;
	}

	private static void loadPainel(){
		SearchAgent sa = new SearchAgent();
		painel = sa.uniqueResult(PainelDeControle.class);
	}
	
	public Long getIdPainelDeControle() {
		return idPainelDeControle;
	}

	public void setIdPainelDeControle(Long idPainelDeControle) {
		this.idPainelDeControle = idPainelDeControle;
	}

	public BigDecimal getMultaPorAtrasoDeEntregaDeLote() {
		return multaPorAtrasoDeEntregaDeLote;
	}

	public void setMultaPorAtrasoDeEntregaDeLote(BigDecimal multaPorAtrasoDeEntregaDeLote) {
		this.multaPorAtrasoDeEntregaDeLote = multaPorAtrasoDeEntregaDeLote;
	}

	public int getPrazoDeEntregaDeLoteSemMulta() {
		return prazoDeEntregaDeLoteSemMulta;
	}

	public void setPrazoDeEntregaDeLoteSemMulta(int prazoDeEntregaDeLoteSemMulta) {
		this.prazoDeEntregaDeLoteSemMulta = prazoDeEntregaDeLoteSemMulta;
	}

	public int getPrazoFinalParaEntregaDeLote() {
		return prazoFinalParaEntregaDeLote;
	}

	public void setPrazoFinalParaEntregaDeLote(int prazoFinalParaEntregaDeLote) {
		this.prazoFinalParaEntregaDeLote = prazoFinalParaEntregaDeLote;
	}
	
	public Date getDataVigenciaPrazoFinalEntregaDeLote() {
		return dataVigenciaPrazoFinalEntregaDeLote;
	}

	public void setDataVigenciaPrazoFinalEntregaDeLote(Date dataVigenciaPrazoFinalEntregaDeLote) {
		this.dataVigenciaPrazoFinalEntregaDeLote = dataVigenciaPrazoFinalEntregaDeLote;
	}
	
	public int getCarenciaDLPs() {
		return carenciaDLPs;
	}

	public void setCarenciaDLPs(int carenciaDLPs) {
		this.carenciaDLPs = carenciaDLPs;
	}

	public Boolean validate() throws ValidateException {
		return true;
	}

	public Set<EmailPainel> getEmailsPortal() {
		return emailsPortal;
	}

	public void setEmailsPortal(Set<EmailPainel> emailsPortal) {
		this.emailsPortal = emailsPortal;
	}

	public Set<EmailPainel> getEmailsContratos() {
		return emailsContratos;
	}

	public void setEmailsContratos(Set<EmailPainel> emailsContratos) {
		this.emailsContratos = emailsContratos;
	}

	public void addEmailPortal(EmailPainel email){
		if(!emailsPortal.contains(email)){
			emailsPortal.add(email);
		}
	}
	
	public void removeEmailPortal(EmailPainel email){
		if(emailsPortal.contains(email)){
			emailsPortal.add(email);
		}
	}
	
	public void removeTipoDeAcomodacaoDobrada(TipoAcomodacao tipoAcomodacao){
		if(tiposDeAcomodacaoDobradas.contains(tipoAcomodacao)){
		    tiposDeAcomodacaoDobradas.remove(tipoAcomodacao);
		}
	}
	
	public void addTipoDeAcomodacaoDobrada(TipoAcomodacao tipoAcomodacao){
		if(!tiposDeAcomodacaoDobradas.contains(tipoAcomodacao)){
		    tiposDeAcomodacaoDobradas.add(tipoAcomodacao);
		}
	}
	
	public void removeSmsHorario(IntervaloDeTempo intervaloDeTempo) {
		if(smsHorarios.contains(intervaloDeTempo)){
		    smsHorarios.remove(intervaloDeTempo);
		}
		
	}
	
	public void addSmsHorario(IntervaloDeTempo intervaloDeTempo) {
	    intervaloDeTempo.validate();
	    if(!smsHorarios.contains(intervaloDeTempo)){
	    	smsHorarios.add(intervaloDeTempo);
	    }
	    
	}
	
	public void addEmailContratos(EmailPainel email){
		if(!emailsContratos.contains(email)){
			emailsContratos.add(email);
		}
	}
	
	public void removeEmailContratos(EmailPainel email){
		if(emailsContratos.contains(email)){
			emailsContratos.add(email);
		}
	}
	
	public Integer getQuantidadeDiasEnvioPrimeiroEmail() {
		return quantidadeDiasEnvioPrimeiroEmail;
	}

	public void setQuantidadeDiasEnvioPrimeiroEmail(
			Integer quantidadeDiasEnvioPrimeiroEmail) {
		this.quantidadeDiasEnvioPrimeiroEmail = quantidadeDiasEnvioPrimeiroEmail;
	}
	
	public int getQuantidadeMaximaDeSalarios() {
		return quantidadeMaximaDeSalarios;
	}

	public void setQuantidadeMaximaDeSalarios(int quantidadeMaximaDeSalarios) {
		this.quantidadeMaximaDeSalarios = quantidadeMaximaDeSalarios;
	}

	public BigDecimal getValorSalarioMinimoVigente() {
		return valorSalarioMinimoVigente;
	}

	public void setValorSalarioMinimoVigente(BigDecimal valorSalarioMinimoVigente) {
		this.valorSalarioMinimoVigente = valorSalarioMinimoVigente;
	}

	/**
	 * Salário base máximo (Total de Salários X Salário mínimo vigente), a ser considerado nos 
	 * calculos de financimento tanto de boletos quanto de consingnações
	 * (Marcos Roberto - 16.11.2011).
	 *
	 * @return 
	 */
	public BigDecimal getSalarioBaseLimite() {
		if((this.quantidadeMaximaDeSalarios > 0) && (this.valorSalarioMinimoVigente!=null)) {			
			return this.valorSalarioMinimoVigente.multiply(new BigDecimal(this.quantidadeMaximaDeSalarios)); 
		}
		return null;
	}

	public BigDecimal getAliquotaDeFinanciamento() {
		return aliquotaDeFinanciamento;
	}
	
	public void setAliquotaDeFinanciamento(BigDecimal aliquotaDeFinanciamento) {
		this.aliquotaDeFinanciamento = aliquotaDeFinanciamento;
	}
	
	public boolean isProcedimentoValoradoPeloPorte() {
		return procedimentoValoradoPeloPorte;
	}

	public void setProcedimentoValoradoPeloPorte(boolean procedimentoValoradoPeloPorte) {
		this.procedimentoValoradoPeloPorte = procedimentoValoradoPeloPorte;
	}
	
	public boolean isProcedimentoDobrado() {
		return procedimentoDobrado;
	}

	public void setProcedimentoDobrado(boolean procedimentoDobrado) {
	    this.procedimentoDobrado = procedimentoDobrado;
	}
	
	public Set<TipoAcomodacao> getTiposDeAcomodacaoDobradas() {
	    return tiposDeAcomodacaoDobradas;
	}
	
	public void setTiposDeAcomodacaoDobradas(Set<TipoAcomodacao> tiposDeAcomodacaoDobradas) {
	    this.tiposDeAcomodacaoDobradas = tiposDeAcomodacaoDobradas;
	}

	public int getPeriodicidadeTratamentoOdonto() {
		return periodicidadeTratamentoOdonto;
	}

	public void setPeriodicidadeTratamentoOdonto(int periodicidadeTratamentoOdonto) {
		this.periodicidadeTratamentoOdonto = periodicidadeTratamentoOdonto;
	}

	public int getQuantidadeTratementoOdonto() {
		return quantidadeTratementoOdonto;
	}

	public void setQuantidadeTratementoOdonto(int quantidadeTratementoOdonto) {
		this.quantidadeTratementoOdonto = quantidadeTratementoOdonto;
	}

	
	public Integer getPrazoSolicitacaoEmConsultaUrgencia() {
		return prazoSolicitacaoEmConsultaUrgencia;
	}

	public void setPrazoSolicitacaoEmConsultaUrgencia(
			Integer prazoSolicitacaoEmConsultaUrgencia) {
		this.prazoSolicitacaoEmConsultaUrgencia = prazoSolicitacaoEmConsultaUrgencia;
	}

	@Override
	public boolean equals(Object obj) {
	    
	    if (obj instanceof PainelDeControle && 
		    ((PainelDeControle) obj).getIdPainelDeControle().equals(this.getIdPainelDeControle())) {
	    	return true;
	    }
	    
	    return false;
	  
	}

	public Integer getTempoLimiteRecursoGlosa() {
		return tempoLimiteRecursoGlosa;
	}

	public void setTempoLimiteRecursoGlosa(Integer tempoLimiteRecursoGlosa) {
		this.tempoLimiteRecursoGlosa = tempoLimiteRecursoGlosa;
	}
}
