package br.com.infowaypi.ecarebc.atendimentos.validators;

import java.util.Collection;
import java.util.Set;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoHonorario;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.msr.utils.Assert;

public class GuiaHonorarioMarcouOuInseriuPeloMenosUmProcedimentoValidate {
	public static void validate(Prestador prestador, Collection<ProcedimentoHonorario> procedimentosOutros, Set<ProcedimentoInterface> procedimentosHonorarios) {
		//ss
		boolean possuiHonorarios  = procedimentosHonorarios == null || !procedimentosHonorarios.isEmpty();
		boolean possuiProcedimentos = procedimentosOutros == null || !procedimentosOutros.isEmpty();
		
		if (!prestador.isPrestadorAnestesista()) { 
			Assert.isTrue((possuiHonorarios || possuiProcedimentos), "Adicione pelo menos um procedimento na guia ou marque um dos procedimentos listados.");
		} else {
			Assert.isTrue((possuiHonorarios), "Marque pelo menos um dos procedimentos listados.");
		}
	}
}
