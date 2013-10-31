package br.com.infowaypi.ecarebc.odonto;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaOdonto;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.utils.Utils;

public class HistoricoOdontologico {
	
	Odontograma<EstruturaOdonto> odontograma;
	List<GuiaConsultaOdonto> guias = new ArrayList<GuiaConsultaOdonto>();
	
	public HistoricoOdontologico(Odontograma odontograma){
		this.odontograma = odontograma;
		buscarGuiasConsultaOdontoBeneficiario();
	}

	private void buscarGuiasConsultaOdontoBeneficiario() {
		Criteria crit = HibernateUtil.currentSession().createCriteria(GuiaConsultaOdonto.class);
		crit.add(Expression.eq("segurado", odontograma.getBeneficiario()));
		crit.add(Expression.ne("situacao.descricao", SituacaoEnum.CANCELADO.descricao()));
		
		List<GuiaConsultaOdonto> guiasOdonto = crit.list();
		
		Utils.sort(guiasOdonto, true, "dataAtendimento");
		
		guias.addAll(guiasOdonto);
	}

	public Odontograma<EstruturaOdonto> getOdontograma() {
		return odontograma;
	}

	public void setOdontograma(Odontograma<EstruturaOdonto> odontograma) {
		this.odontograma = odontograma;
	}

	public List<GuiaConsultaOdonto> getGuias() {
		return guias;
	}

	public void setGuias(List<GuiaConsultaOdonto> guias) {
		this.guias = guias;
	}

}
