package br.com.infowaypi.ecarebc.atendimentos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;

import br.com.infowaypi.ecarebc.financeiro.consignacao.Consignacao;
import br.com.infowaypi.ecarebc.segurados.TitularInterfaceBC;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

public class ResumoConsignacoes {

	private BigDecimal valorTotalCoparticipacao;
	private BigDecimal valorTotalFinanciamento;
	
	private int quantidadeConsignacoesEnviadas;
	
	private TitularInterfaceBC titular;
	private List<Consignacao> consignacoesGeradas;
	private List<Consignacao> consignacoesNaoGeradas;
	private List<GuiaSimples> guias;
	
	private String tarefa;
		
	public ResumoConsignacoes() {
	}

	public String getTarefa() {
		return tarefa;
	}

	public void setTarefa(String tarefa) {
		this.tarefa = tarefa;
	}

	public ResumoConsignacoes(TitularInterfaceBC titular, boolean exibirGuias) throws ValidateException{
		this.guias = new ArrayList<GuiaSimples>();
		this.consignacoesNaoGeradas = new ArrayList<Consignacao>();
		this.valorTotalCoparticipacao = new BigDecimal(0f);
		this.valorTotalFinanciamento = new BigDecimal(0f);
		this.titular = titular;
		Long ultimoId = new Long(0);
		
		ArrayList<Consignacao> consigs = new ArrayList<Consignacao>(titular.getConsignacoes()) ;
		Collections.sort(consigs, new Comparator<Consignacao>(){
			public int compare(Consignacao o1, Consignacao o2) {
				return o2.getCompetencia().compareTo(o1.getCompetencia());
			}
		});
		
		this.consignacoesGeradas = consigs;
		this.quantidadeConsignacoesEnviadas = this.consignacoesGeradas.size();
		
		for(Consignacao consignacao : this.consignacoesGeradas){
			if (consignacao.getIdFluxoFinanceiro() > ultimoId){
				ultimoId = consignacao.getIdFluxoFinanceiro(); 
			}
			if(consignacao.getValorCoparticipacao() != null) {
				valorTotalCoparticipacao = 
					MoneyCalculation.getSoma(valorTotalCoparticipacao, consignacao.getValorCoparticipacao().floatValue());
			}
			if(consignacao.getValorFinanciamento() != null) {
				valorTotalFinanciamento = 
					MoneyCalculation.getSoma(valorTotalFinanciamento, consignacao.getValorFinanciamento().floatValue());
			}
		
			if(exibirGuias)
				this.guias.addAll(consignacao.getGuias());
		}

		simulaConsignacoes(titular,	ultimoId);
		
	}
	
	private void simulaConsignacoes(TitularInterfaceBC titular, Long id) throws ValidateException{
		id += 100;		
		List<Consignacao> consignacoesNaoGeradas = new ArrayList<Consignacao>();

		Calendar competenciaAtual = new GregorianCalendar();
		competenciaAtual.set(Calendar.DAY_OF_MONTH, 01);

		Calendar i = new GregorianCalendar();
		Consignacao ultimaConsignacaoGerada = new Consignacao();
		i.set(2007, Calendar.JANUARY, 01);
		if(!this.consignacoesGeradas.isEmpty()){
			int indexUltimo = this.consignacoesGeradas.size() - 1;
			ultimaConsignacaoGerada = this.consignacoesGeradas.get(indexUltimo);			
			i.setTime(ultimaConsignacaoGerada.getCompetencia());
		}			
		
		int compare = Utils.compararCompetencia(i.getTime(), competenciaAtual.getTime());
		while (compare < 0){
			i.add(Calendar.MONTH, 1);
			Calendar comp = new GregorianCalendar();
			comp.setTime(i.getTime());
			if(titular.getGuiasConfirmadasEFechadas(i.getTime()).isEmpty()){
				compare = Utils.compararCompetencia(i.getTime(), competenciaAtual.getTime());
				continue;
			}
			id++;
			Consignacao consignacaoNaoGerada = new Consignacao();
			BigDecimal valorCoparticipacao = BigDecimal.ZERO;
			BigDecimal valorFinanciamento = BigDecimal.ZERO;
			consignacaoNaoGerada.getGuias().addAll(titular.getGuiasConfirmadasEFechadas(i.getTime()));
			for (GuiaSimples guia : titular.getGuiasConfirmadasEFechadas(i.getTime())) {
				guia.tocarObjetos();
				valorCoparticipacao = guia.getValorCoParticipacao().add(valorCoparticipacao);
//				valorFinanciamento = guia.getValorTotal().add(valorFinanciamento);
				consignacaoNaoGerada.setCompetencia(comp.getTime());
				consignacaoNaoGerada.setTitular(titular);
				consignacaoNaoGerada.addGuia(guia);
			}
			consignacaoNaoGerada.setValorCoparticipacao(valorCoparticipacao);
//			consignacaoNaoGerada.setValorFinanciamento(valorFinanciamento);
			consignacaoNaoGerada.setIdFluxoFinanceiro(id);
			titular.getConsignacoes().add(consignacaoNaoGerada);
			consignacoesNaoGeradas.add(consignacaoNaoGerada);
			compare = Utils.compararCompetencia(i.getTime(), competenciaAtual.getTime());
		}
		
		Collections.sort(consignacoesNaoGeradas, new Comparator<Consignacao>(){
			public int compare(Consignacao o1, Consignacao o2) {
				return o2.getCompetencia().compareTo(o1.getCompetencia());
			}
		});
		this.consignacoesNaoGeradas = consignacoesNaoGeradas;
	}
		
	public List<Consignacao> getConsignacoesGeradas() {
		return consignacoesGeradas;
	}

	public List<Consignacao> getConsignacoesNaoGeradas() {
		return consignacoesNaoGeradas;
	}

	public List<GuiaSimples> getGuias() {
		return guias;
	}
	
	public BigDecimal getValorTotalCoparticipacao() {
		return MoneyCalculation.rounded(valorTotalCoparticipacao);
	}
	
	public BigDecimal getValorTotalFinanciamento() {
		return MoneyCalculation.rounded(valorTotalFinanciamento);
	}

	public int getQuantidadeConsignacoesEnviadas() {
		return quantidadeConsignacoesEnviadas;
	}

	public TitularInterfaceBC getTitular() {
		return titular;
	}

	public void setTitular(TitularInterfaceBC titular) {
		this.titular = titular;
	}
	
	public Consignacao getConsignacao(Long idConsignacao){
		for (Consignacao cons : consignacoesNaoGeradas)
			if (idConsignacao.equals(cons.getIdFluxoFinanceiro()))
				return cons;
		return null;
	}

}
