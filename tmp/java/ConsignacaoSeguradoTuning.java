package br.com.infowaypi.ecare.financeiro.consignacao.tuning;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import br.com.infowaypi.ecare.financeiro.consignacao.ConsignacaoMatriculaInterface;
import br.com.infowaypi.ecare.segurados.AbstractMatricula;
import br.com.infowaypi.molecular.ImplDAO;


public class ConsignacaoSeguradoTuning extends ConsignacaoTuning {
	
	public void updateStatusEValoresConsignacaoSegurado(Date competencia, Date dataDePagamento) throws Exception{

		Set<ConsignacaoMatriculaInterface> consignacoesMatricula = new HashSet<ConsignacaoMatriculaInterface>();
		
		for (AbstractMatricula matricula : this.getTitular().getMatriculas()) {
			ConsignacaoMatriculaInterface consignacao = matricula.getConsignacao(competencia);
			if(consignacao != null) {
				consignacoesMatricula.add(consignacao);
			}
		}
		
		boolean isConsignacaoSeguradoPaga = true;
		this.setValorCoparticipacao(BigDecimal.ZERO);
		this.setValorFinanciamento(BigDecimal.ZERO);
		this.setValorParcelamento(BigDecimal.ZERO);
		for (ConsignacaoMatriculaInterface consignacaoMatricula : consignacoesMatricula) {
			this.setValorCoparticipacao(this.getValorCoparticipacao().add(consignacaoMatricula.getValorCoparticipacao()));
			this.setValorFinanciamento(this.getValorFinanciamento().add(consignacaoMatricula.getValorFinanciamento()));
			this.setValorParcelamento(this.getValorParcelamento().add(consignacaoMatricula.getValorParcelamento()));
			if(consignacaoMatricula.getStatusConsignacao() == STATUS_ABERTO)
				isConsignacaoSeguradoPaga = false;
		}
		
		
		boolean isValorCoparticipacaoZerado = this.getValorCoparticipacao().compareTo(BigDecimal.ZERO) == 0;
		boolean isValorFinanciamentoZerado = this.getValorFinanciamento().compareTo(BigDecimal.ZERO) == 0;
		boolean isValorParcelamentoZerado = this.getValorParcelamento().compareTo(BigDecimal.ZERO) == 0;
		boolean isConsignacaoSeguradoZerada = (isValorCoparticipacaoZerado && isValorFinanciamentoZerado && isValorParcelamentoZerado);
		
		if(isConsignacaoSeguradoPaga && !isConsignacaoSeguradoZerada){
			this.setStatusConsignacao(STATUS_PAGO);
			this.setDataDoCredito(dataDePagamento);
		}else{
			this.setStatusConsignacao(STATUS_ABERTO);
		}

		ImplDAO.save(this);
	}
	
}
