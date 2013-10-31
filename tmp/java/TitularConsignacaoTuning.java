package br.com.infowaypi.ecare.segurados.tuning;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.infowaypi.ecare.atendimentos.GuiaSimplesTuning;
import br.com.infowaypi.ecare.financeiro.consignacao.PisoFinanciamentoTitular;
import br.com.infowaypi.ecare.financeiro.consignacao.tuning.ConsignacaoSeguradoTuning;
import br.com.infowaypi.ecare.segurados.SeguradoConsignacaoInterface;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.DependenteFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.FluxoFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.consignacao.Consignacao;
import br.com.infowaypi.ecarebc.painelDeControle.PainelDeControle;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.financeiro.InformacaoFinanceiraInterface;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @change SR Team - Marcos Roberto 17.11.2011
 * 
 */
@SuppressWarnings("serial")
public class TitularConsignacaoTuning extends SeguradoConsignacaoTuning implements TitularFinanceiroTuning,SeguradoConsignacaoInterface{
	//CONSTANTES PARA CONSIGNACAO
	public static final BigDecimal LIMITE_COPARTICIPACAO = new BigDecimal(50.0F).setScale(2,BigDecimal.ROUND_HALF_UP);
		
	private Set<MatriculaTuning> matriculas;
	private Set<ConsignacaoSeguradoTuning> consignacoes;
	private Set<DependenteConsignacaoTuning> dependentes;
	
	public TitularConsignacaoTuning() {
		matriculas 		= new HashSet<MatriculaTuning>();
		consignacoes 	= new HashSet<ConsignacaoSeguradoTuning>();
		dependentes 	= new HashSet<DependenteConsignacaoTuning>();
	}
	
	public Set<MatriculaTuning> getMatriculas() {
		return matriculas;
	}
	
	public void setMatriculas(Set<MatriculaTuning> matriculas) {
		this.matriculas = matriculas;
	}
	
	public Set<ConsignacaoSeguradoTuning> getConsignacoes() {
		return consignacoes;
	}
	
	public void setConsignacoes(Set<ConsignacaoSeguradoTuning> consignacoes) {
		this.consignacoes = consignacoes;
	}
	
	public Set<DependenteConsignacaoTuning> getDependentes() {
		return dependentes;
	}
	
	public void setDependentes(Set<DependenteConsignacaoTuning> dependentes) {
		this.dependentes = dependentes;
	}
	
	public ConsignacaoSeguradoTuning getConsignacao(Date competencia) {
		for (ConsignacaoSeguradoTuning consignacao : this.consignacoes) {
			if(consignacao.getCompetencia().compareTo(competencia) == 0){
				return consignacao;
			}
		}
		return null;
	}
	
	public BigDecimal getValorCoparticipacao(Consignacao consignacao, Date competenciaAjustada){

		BigDecimal valorTotalCoparticipacaoGuias = BigDecimal.ZERO;
		valorTotalCoparticipacaoGuias.setScale(2, BigDecimal.ROUND_HALF_UP);

		Calendar cal = new GregorianCalendar();
		cal.setTime(competenciaAjustada);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DATE));
		Date dataInicial = cal.getTime();
		System.out.println();
		
		List<GuiaSimplesTuning> guias = getGuiasParaCoparticipacao(this ,competenciaAjustada, dataInicial);
		
		for(DependenteConsignacaoTuning d : this.getDependentes())
			guias.addAll(getGuiasParaCoparticipacao(d, competenciaAjustada, dataInicial));
		
		System.out.println("TitularGuias: " + this.getPessoaFisica().getCpf() + " Qt: " + guias.size());
		for (GuiaSimplesTuning guia : guias) {
				guia.setFluxoFinanceiro(consignacao);
				consignacao.getGuias().add(guia);
				System.out.println("Guia: " + guia.getAutorizacao()+ /*" Tipo: "+ guia.getTipoDeGuia()+*/ " Valor Coparticipacao: " + (new DecimalFormat("######0.00")).format(guia.getValorCoParticipacao())+" Situacao: " + guia.getSituacao().getDescricao() + " Data atendimento: " + Utils.format(guia.getDataAtendimento()));
				if(valorTotalCoparticipacaoGuias.compareTo(LIMITE_COPARTICIPACAO) != 0)
					if(valorTotalCoparticipacaoGuias.add(guia.getValorCoParticipacao()).compareTo(LIMITE_COPARTICIPACAO) < 0)
						valorTotalCoparticipacaoGuias = valorTotalCoparticipacaoGuias.add(guia.getValorCoParticipacao());
					else
						valorTotalCoparticipacaoGuias = LIMITE_COPARTICIPACAO;
			
		}
		if(consignacao != null){
			consignacao.setValorCoparticipacao(valorTotalCoparticipacaoGuias);
		}
		return valorTotalCoparticipacaoGuias;
	}
	
	private List<GuiaSimplesTuning> getGuiasParaCoparticipacao(SeguradoConsignacaoTuning segurado,
			Date competenciaAjustada, Date dataInicial) {
		
		String[] situacoes = {SituacaoEnum.CONFIRMADO.descricao(),SituacaoEnum.AUDITADO.descricao(),SituacaoEnum.FATURADA.descricao()}; 
		
		List<GuiaSimplesTuning> guias = HibernateUtil.currentSession()
		.createFilter(segurado.getGuias(), "where this.fluxoFinanceiro is null and this.situacao.descricao in (:situacoes) and dataAtendimento between :dataInicial and :dataFinal ")
		.setParameterList("situacoes", situacoes)
		.setParameter("dataInicial", dataInicial)
		.setParameter("dataFinal", competenciaAjustada)
		.list();
		return guias;
	}
	
	
	/**
	 * Método que calcula o valor de financiamento do titular
	 * @param salarioBase
	 * @return um Array de BigDecimal onde a primeira posição é a alíquota
	 * e a segunda posição é o valor de financiamento do titular.
	 */
	public BigDecimal[] getValorFinanciamento(BigDecimal salarioBase){
		BigDecimal aliquota_financiamento = PainelDeControle.getPainel().getAliquotaDeFinanciamento();
		BigDecimal[] aliquotaValor = new BigDecimal[2];
		if(salarioBase.compareTo(BigDecimal.ZERO) > 0)
			aliquotaValor[0] = aliquota_financiamento;
		else{
			aliquotaValor[0] = BigDecimal.ZERO;
			aliquotaValor[1] = BigDecimal.ZERO;
			return aliquotaValor;
		}	
		
		
		BigDecimal contribuicaoTitular = salarioBase.multiply(aliquota_financiamento.divide(new BigDecimal(100))).setScale(2, BigDecimal.ROUND_HALF_UP);
		
		//verifica o teto mínimo para a contribuição do Titular
		if(contribuicaoTitular.compareTo(PisoFinanciamentoTitular.getPisoPorFaixaSalarial(salarioBase)) < 0) {
			contribuicaoTitular = PisoFinanciamentoTitular.getPisoPorFaixaSalarial(salarioBase);
		}
	
		aliquotaValor[1] = contribuicaoTitular; 
		return aliquotaValor;
	}
	public Set<DependenteFinanceiroInterface> getDependentesFinanceiro() {
		return null;
	}
	public Set<FluxoFinanceiroInterface> getFluxosFinanceiros() {
		return null;
	}
	public String getIdentificacao() {
		return this.getPessoaFisica().getCpf();
	}
	public InformacaoFinanceiraInterface getInformacaoFinanceira() {
		return null;
	}
	public String getNome() {
		return null;
	}
	public String getTipo() {
		return null;
	}

	/**
	 * Método que ordena os dependentes pelo atributo <code>dataAdesao</code>
	 * @return um mapa com a ordem como chave do mapa e o dependente como valor do mapa
	 */
	public Map<Integer, DependenteConsignacaoTuning> ordenaDependentes() {
		Map<Integer,DependenteConsignacaoTuning> ordemDependentes = new HashMap<Integer, DependenteConsignacaoTuning>();
		int countDeps = 1;
		for(DependenteConsignacaoTuning dependente : this.getDependentes()){
			if(dependente.isSituacaoAtual(SituacaoEnum.ATIVO.descricao())){
				ordemDependentes.put(countDeps++, dependente);
			}
		}
		return ordemDependentes;
	}

	public Integer getOrdemDependente(DependenteConsignacaoTuning dependente){
		List dependentes = new ArrayList<DependenteConsignacaoTuning>(this.dependentes);
		return dependentes.indexOf(dependente) + 1;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Date data = Utils.parse("01/12/2007");
		data = ajustarCompetencia(data);
		System.out.println("COMP. AJUSTADA: "+Utils.format(data));
		Calendar cal = new GregorianCalendar();
		cal.setTime(data);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DATE));
		Date dataInicial = cal.getTime();
		System.out.println("DATA INICIAL: "+Utils.format(dataInicial));
		System.out.println(dataInicial);
	}
	
	private static Date ajustarCompetencia(Date competencia) {
		GregorianCalendar calendario = new GregorianCalendar();
		calendario.setTime(competencia);
		calendario.set(Calendar.MONTH, calendario.get(Calendar.MONTH) - 1);
		calendario.set(Calendar.DAY_OF_MONTH, calendario.getActualMaximum(Calendar.DATE));
		return calendario.getTime();
	}
	
	public List<MatriculaTuning> getMatriculasAtivas() {
		List<MatriculaTuning> matriculasAtivas = new ArrayList<MatriculaTuning>();
		for (MatriculaTuning matricula : this.getMatriculas()) {
			if(matricula.isAtiva()) {
				matriculasAtivas.add(matricula);
			}
		}
		
		return matriculasAtivas;
	}
	
	public List<MatriculaTuning> getMatriculasInativas() {
		List<MatriculaTuning> matriculasInativas = new ArrayList<MatriculaTuning>();
		for (MatriculaTuning matricula : this.getMatriculas()) {
			if(!matricula.isAtiva()) {
				matriculasInativas.add(matricula);
			}
		}
		return matriculasInativas;
	}
 	
}
