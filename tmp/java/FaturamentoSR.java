package br.com.infowaypi.ecare.financeiro.faturamento;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.financeiro.TitularFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.faturamento.Faturamento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;

@SuppressWarnings("unused")
public class FaturamentoSR extends Faturamento{
	
	private static final long serialVersionUID = 1L;
	private Set<ProcedimentoInterface> procedimentosAnestesicos = new HashSet<ProcedimentoInterface>();
	
	public FaturamentoSR(Date competencia, char status, Prestador prestador) {
		super(competencia, status, prestador);
	}
	
	public FaturamentoSR(){
		super();
		this.procedimentosAnestesicos = new HashSet<ProcedimentoInterface>();
	}

	private int getQuantidadeProcedimentosAnestesicos(){
		return this.getProcedimentosAnestesicos().size();
	}
	
	public String getTipoFaturamento(){
		return TIPO_FATURAMENTO_SR;
	}
	
	public void tocarObjetos(){
		this.getProcedimentosAnestesicos().size();
		super.tocarObjetos();
	}

	public Set<ProcedimentoInterface> getProcedimentosAnestesicos() {
		return procedimentosAnestesicos;
	}

	public void setProcedimentosAnestesicos(
			Set<ProcedimentoInterface> procedimentosAnestesicos) {
		this.procedimentosAnestesicos = procedimentosAnestesicos;
	}

	@Override
	public TitularFinanceiroInterface getTitularFinanceiro() {
		return super.getProfissional();
	}

}
