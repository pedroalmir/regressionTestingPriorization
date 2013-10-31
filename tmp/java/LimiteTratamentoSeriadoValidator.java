package br.com.infowaypi.ecarebc.atendimentos.validators;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;

import br.com.infowaypi.ecarebc.atendimentos.GuiaTratamentoSeriado;
import br.com.infowaypi.ecarebc.atendimentos.enums.TratamentoSeriadoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.molecular.HibernateUtil;

/**
 * Valida o limite mensal e anual de um determinado tipo de tratamento.
 * Exemplo:
 * Fisioterapia.
 * Limite Mensal - 10 sessões.
 * Limite Anual - 60 sessões.
 * 		
 * @author Josino.
 * @changes Benedito.
 *
 */
public class LimiteTratamentoSeriadoValidator extends AbstractGuiaValidator<GuiaTratamentoSeriado<ProcedimentoInterface>>{

	@Override
	protected boolean templateValidator(GuiaTratamentoSeriado<ProcedimentoInterface> guia) throws Exception {
		System.out.println("################################ " + guia.getTratamento() + " #################################");
		TratamentoSeriadoEnum tipoTratamento = TratamentoSeriadoEnum.getTratamento(guia.getTratamento());
		
		Long qtdeSessoesAnual = this.getQuantidadeSessoes(guia.getSegurado(), getDataInicioAno(guia.getDataAtendimento()), tipoTratamento.valor());
		Long qtdeSessoesMensal = this.getQuantidadeSessoes(guia.getSegurado(), getDataInicioMes(guia.getDataAtendimento()), tipoTratamento.valor());
		Long qtdeSessoesDesejadas = getQtdeSessoesDesejadas(guia.getProcedimentos());
		
		this.compararQtdeSessoes(qtdeSessoesAnual, qtdeSessoesDesejadas, tipoTratamento.limiteAnual());
		this.compararQtdeSessoes(qtdeSessoesMensal, qtdeSessoesDesejadas, tipoTratamento.limiteMensal());
		
		return true;
	}
	
	private void compararQtdeSessoes(Long qtdeSessoesRealizadas, Long qtdeSessoesDesejadas, Integer limiteSessoes){
		if(limiteSessoes == null){
			return;
		}
		
		if (Long.valueOf(limiteSessoes) == qtdeSessoesRealizadas){
			throw new RuntimeException("O beneficiário atingiu o limite: " + limiteSessoes + " sessões.");
		} else {
			Long totalSessoes = qtdeSessoesRealizadas + qtdeSessoesDesejadas;
			
			System.out.println("Qtde Sessões: " + totalSessoes);
			System.out.println("Limite de sessões: " + limiteSessoes);
			if (totalSessoes > limiteSessoes){
				Long qtdeSessoesPermitidas = limiteSessoes - qtdeSessoesRealizadas;
				if(qtdeSessoesRealizadas == 0)
					throw new RuntimeException("O limite do Beneficiário permite apenas " + qtdeSessoesPermitidas + " sessões.");
				throw new RuntimeException("O beneficiário já realizou " + qtdeSessoesRealizadas + " sessões. São permitidas apenas " + qtdeSessoesPermitidas + " sessões.");
			}
		}
	}
	
	private Long getQtdeSessoesDesejadas(Set<ProcedimentoInterface> procedimentos){
		Long qtdeSessoesDesejadas = 0L;
		
		for (ProcedimentoInterface procedimento : procedimentos) {
			qtdeSessoesDesejadas += procedimento.getQuantidade();
		}
		
		return qtdeSessoesDesejadas;
	}
	
	private Long getQuantidadeSessoes(AbstractSegurado segurado, Date dataInicial, Integer tipoTratamento){
		Criteria criteria = HibernateUtil.currentSession().createCriteria(GuiaTratamentoSeriado.class);
		criteria.add(Expression.eq("segurado", segurado));
		criteria.add(Expression.eq("tipoTratamentoSeriado", tipoTratamento));
		criteria.add(Expression.between("dataAtendimento", dataInicial, new Date()));
		criteria.setProjection(Projections.sum("quantidadeProcedimentos"));
		
		Long quant = (Long) criteria.uniqueResult();
		return quant == null ? 0 : quant ;
	}
	
	private Date getDataInicioAno(Date dataAtendimento){
		Calendar dataInicial = new GregorianCalendar();
		dataInicial.setTime(dataAtendimento);
		dataInicial.set(Calendar.DAY_OF_MONTH, 1);
		dataInicial.set(Calendar.MONTH, 1);
		
		return dataInicial.getTime();
	}
	
	private Date getDataInicioMes(Date dataAtendimento){
		Calendar dataInicial = new GregorianCalendar();
		dataInicial.setTime(dataAtendimento);
		dataInicial.set(Calendar.DAY_OF_MONTH, 1);
		
		return dataInicial.getTime();
	}
	
}
