package br.com.infowaypi.ecare.financeiro.faturamento;

import java.util.Date;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AbstractFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ResumoFaturamentos;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.utils.Assert;

public class GeracaoFaturamentoServiceSR extends FinanceiroServiceSR{
	
	public void cancelarFaturamento(ResumoFaturamentos resumo) throws Exception{
		Assert.isNotNull(resumo.getFaturamentos(), "Não há faturamento a cancelar nessa competência.");
		
		for(AbstractFaturamento faturamento : resumo.getFaturamentos()){
			
			if(!faturamento.getPrestador().getTipoPrestador().equals(Prestador.TIPO_PRESTADOR_ANESTESISTA)){
				this.cancelarFaturamento(faturamento);
			}
			else{
				this.cancelarFaturamento(faturamento);
			}
			
		}
	}
	
	public void cancelarFaturamento(FaturamentoSR faturamento) throws Exception{
		this.cancelarFaturamento(faturamento);
		for (ProcedimentoInterface procedimento : faturamento.getProcedimentosAnestesicos()){
			procedimento.mudarSituacao(null, procedimento.getSituacaoAnterior(procedimento.getSituacao()).getDescricao(), MotivoEnum.CANCELAMENTO_DE_FATURAS.getMessage(), new Date());
			ImplDAO.save(procedimento);
		}
		ImplDAO.save(faturamento);
	}
	
}