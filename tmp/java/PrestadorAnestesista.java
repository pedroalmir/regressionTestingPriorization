package br.com.infowaypi.ecare.associados;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import br.com.infowaypi.ecare.financeiro.faturamento.FaturamentoSR;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.consumo.ConsumoInterface;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AbstractFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.Faturamento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.utils.Utils;

public class PrestadorAnestesista extends PrestadorRelacionamento{

	private Set<ProcedimentoInterface> procedimentosFaturamento;
//	private Set<ItemPacote> itensPacote;
	
	public PrestadorAnestesista(){
		this.procedimentosFaturamento = new HashSet<ProcedimentoInterface>();
//		this.itensPacote = new HashSet<ItemPacote>();
	}
	
	public Set<ProcedimentoInterface> getProcedimentosFaturamento() {
		return procedimentosFaturamento;
	}
	
	public void setProcedimentosFaturamento(
			Set<ProcedimentoInterface> procedimentosFaturamento) {
		this.procedimentosFaturamento = procedimentosFaturamento;
	}

//	public Set<ItemPacote> getItensPacote() {
//		return itensPacote;
//	}
//
//	public void setItensPacote(Set<ItemPacote> itensPacote) {
//		this.itensPacote = itensPacote;
//	}
//	
//	public void addItemPacote(ItemPacote item) throws Exception{
//		this.getItensPacote().add(item);
//		this.atualizarConsumoFinanceiro(item);
//	}
	
	public void addProcedimento(ProcedimentoInterface item) throws Exception{
		this.getProcedimentosFaturamento().add(item);
		item.setPrestadorAnestesista(this);
//		this.atualizarConsumoFinanceiro(item);
	}
	
	public void atualizarConsumoFinanceiro(ProcedimentoInterface procedimento) throws Exception{
		ConsumoInterface consumoAtual = getConsumo();
		BigDecimal valorInternacoes = procedimento.getValorAnestesista().add(consumoAtual.getSomatorioInternacoesFechadas());
		consumoAtual.setSomatorioInternacoesFechadas(valorInternacoes);
		consumoAtual.setNumeroProcedimentosInternacoesFechadas(consumoAtual.getNumeroProcedimentosInternacoesFechadas() + 1);
		ImplDAO.save(consumoAtual);
	}

//	public void atualizarConsumoFinanceiro(ItemPacote itemPacote) throws Exception{
//		ConsumoInterface consumoAtual = getConsumo();
//		BigDecimal valorInternacoes = itemPacote.getPacote().getValorTotalAnestesia().add(consumoAtual.getSomatorioInternacoesFechadas());
//		consumoAtual.setNumeroProcedimentosInternacoesFechadas(consumoAtual.getNumeroProcedimentosInternacoesFechadas() + itemPacote.getPacote().getQuantidadeDiarias());
//		consumoAtual.setSomatorioInternacoesFechadas(valorInternacoes);
//		ImplDAO.save(consumoAtual);
//	}
	
	private ConsumoInterface getConsumo() throws Exception {
		ConsumoInterface consumoAtual = this.getConsumo(new Date());
		if (consumoAtual == null)
			throw new RuntimeException("Ocorreu um erro na criação do "
					+ "consumo para a competência "
					+ Utils.format(new Date(), "MM/yyyy"));
		return consumoAtual;
	}
	
	public FaturamentoSR getFaturamentoAnestesista(Date competencia) {
		for (AbstractFaturamento faturamento : getFaturamentos()) {
			boolean faturamentoCancelado = (faturamento.getStatus() == Constantes.FATURAMENTO_CANCELADO);
			if (Utils.compararCompetencia(faturamento.getCompetencia(),
					competencia) == 0
					&& !faturamentoCancelado && faturamento.getTipoFaturamento().equals(FaturamentoSR.TIPO_FATURAMENTO_SR))
				return (FaturamentoSR) faturamento;
		}
		return null;
	}
}
