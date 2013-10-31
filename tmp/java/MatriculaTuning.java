package br.com.infowaypi.ecare.segurados.tuning;

import java.util.Date;

import org.apache.commons.lang.builder.HashCodeBuilder;

import br.com.infowaypi.ecare.financeiro.consignacao.ConsignacaoMatriculaInterface;
import br.com.infowaypi.ecare.segurados.AbstractMatricula;
import br.com.infowaypi.ecare.segurados.Matricula;
import br.com.infowaypi.ecarebc.financeiro.consignacao.Consignacao;

public class MatriculaTuning extends AbstractMatricula {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	TitularConsignacaoTuning seguradoConsignacao;
	
	public TitularConsignacaoTuning getSeguradoConsignacao() {
		return seguradoConsignacao;
	}
	
	public void setSeguradoConsignacao(
			TitularConsignacaoTuning seguradoConsignacao) {
		this.seguradoConsignacao = seguradoConsignacao;
	}
	
	public ConsignacaoMatriculaInterface getConsignacao(Date competencia){
		for(Consignacao<Matricula> consignacao : this.getConsignacoes()){
			if(consignacao.getCompetencia().compareTo(competencia) == 0)
				return (ConsignacaoMatriculaInterface) consignacao;
		}
		
		return null;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(super.getEmpresa())
			.append(super.getDescricao())
			.append(super.getSegurado())
			.toHashCode();
	}
}
