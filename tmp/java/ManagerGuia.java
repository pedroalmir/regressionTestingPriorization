package br.com.infowaypi.ecarebc.atendimentos;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import br.com.infowaypi.ecare.services.ProcedimentoCirurgicoLayer;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.msr.utils.MoneyCalculation;
/**
 * Classe que auxilia as guias em suas tarefas de rotina
 * @author Wislanildo
 * @changes Jefferson, Wislanildo
 *
 */
public class ManagerGuia {

	public static Set<ProcedimentoInterface> getProcedimentosAceitosPelaAuditoria(GuiaCompleta<ProcedimentoInterface> guia) {
		Set<ProcedimentoInterface> procedimentosParaAuditar = new HashSet<ProcedimentoInterface>();
		for (ProcedimentoCirurgicoLayer layer : guia.getProcedimentosCirurgicosLayer()) {
			if (!layer.isGlosar() && MoneyCalculation.compare(layer.getPorcentagem(), BigDecimal.ZERO) != 0) {
				ProcedimentoCirurgico procedimento = layer.getProcedimentoCirurgicoNovo();
				procedimento.setPassouPelaAutorizacao(true);
				procedimentosParaAuditar.add(procedimento);
			}
		}
		return procedimentosParaAuditar;
	}
	
	public static Set<Procedimento> getProcedimentosComProfissionalResponsavel(GuiaSimples<Procedimento> guia){
		Set<Procedimento> procedimentosComProfissional = new HashSet<Procedimento>();
		
		for (Procedimento proc : guia.getProcedimentos()) {
			
			boolean temProfissional 		= proc.getProfissionalResponsavel()!=null;
			boolean isProcedimentoCirugico 	= proc.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO;
			
			if (!isProcedimentoCirugico) {
				procedimentosComProfissional.add(proc);
			}else if (temProfissional){
				procedimentosComProfissional.add(proc);
			}
		}
		return procedimentosComProfissional;
	}
	
	public static boolean isZeraCoparticipacaoDeAcordoComAGuiaOrigem(GuiaSimples guia) {
		GuiaSimples guiaOrigem = guia.getGuiaOrigem();
		if (guiaOrigem !=null){
			boolean guiaOrigemIsInternacao =guiaOrigem.isInternacao();
			boolean isExameOriginadoDeInternacao = guiaOrigem.getGuiaOrigem() == null ? false : guiaOrigem.getGuiaOrigem().isInternacao();
			boolean guiaOrigemIsExameOriginadoDeInternacao = guiaOrigem.isExame() && isExameOriginadoDeInternacao;
			return guiaOrigemIsInternacao || guiaOrigemIsExameOriginadoDeInternacao;
		}
		return false;
	}
	
	public static boolean isProcedimentoDeConsulta(ProcedimentoInterface procedimento){
		if(procedimento.getProcedimentoDaTabelaCBHPM().getGrupo() == TabelaCBHPM.CONSULTAS_E_VISITAS){
			return true;
		}
		return false;
	}
}
