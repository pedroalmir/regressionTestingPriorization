package br.com.infowaypi.ecarebc.atendimentos.visitors;

import br.com.infowaypi.ecarebc.atendimentos.ItemGlosavel;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemGasoterapia;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemTaxa;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoHonorario;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOutros;

public interface ItemGlosavelVisitor {
	
	public ItemGlosavel visit(ItemPacote item);
	public ItemGlosavel visit(ItemDiaria item);
	public ItemGlosavel visit(ItemGasoterapia item);
	public ItemGlosavel visit(ItemTaxa item);
	public ItemGlosavel visit(ProcedimentoCirurgico procedimentoCirurgico);
	public ItemGlosavel visit(Procedimento procedimento);
	public ItemGlosavel visit(ProcedimentoOutros procedimentoOutros);
	public ItemGlosavel visit(ProcedimentoHonorario procedimentoHonorario);
}