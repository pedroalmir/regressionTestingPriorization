package br.com.infowaypi.ecare.services.honorariomedico;

import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.resumos.ResumoGuiasHonorarioMedico;
import br.com.infowaypi.ecarebc.associados.validators.AbstractValidator;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * Valida��es do flow @AuditarGuiaHonorarioMedico <br>
 * Executa valida��es relacionadas a existencia de honorarios na guia quando se seleciona: cirurgico, anestesico, clinico ou pacote<br>
 * chamado no step 2: selecionarGuia
 * @author Emanuel
 *
 */
public class HonorarioAptorAuditarValidator extends AbstractValidator<ResumoGuiasHonorarioMedico>{

	@Override
	protected boolean templateValidator(ResumoGuiasHonorarioMedico resumo) throws ValidateException{
		boolean auditarProcedimentosCirurgicos = resumo.isAuditarProcedimentosCirurgicos();
		boolean auditarProcedimentosGrauAnestesista = resumo.isAuditarProcedimentosGrauAnestesista();
		boolean auditarProcedimentosClinicos = resumo.isAuditarProcedimentosClinicos();
		boolean auditarPacotes = resumo.isAuditarPacotes();
		
		boolean hasProcedimentosCirurgicos 			= !resumo.getProcedimentosCirurgicos().isEmpty();
		boolean hasProcedimentosClinicos 				= !resumo.getProcedimentosVisitaAtuais().isEmpty();
		boolean hasHonorariosPacotes 					= !((GuiaCompleta) resumo.getGuiaMae()).getHonorariosPacote().isEmpty();
		boolean hasProcedimentosCirurgicoAnestesico	= !resumo.getProcedimentosCirurgicosAnestesicos().isEmpty();
		
		if(!auditarProcedimentosCirurgicos && !auditarProcedimentosClinicos && !auditarPacotes && !auditarProcedimentosGrauAnestesista){
			throw new ValidateException(MensagemErroEnumSR.NENHUM_TIPO_HONORARIO_SELECIONADO.getMessage());
		}
		
		boolean isHonorarioCirurgico = auditarProcedimentosCirurgicos && !auditarProcedimentosClinicos && !auditarProcedimentosGrauAnestesista && !auditarPacotes;
		boolean isHonorarioClinico = auditarProcedimentosClinicos && !auditarProcedimentosCirurgicos && !auditarProcedimentosGrauAnestesista && !auditarPacotes;
		boolean isHonorarioAnestesista = auditarProcedimentosGrauAnestesista && !auditarProcedimentosCirurgicos && !auditarProcedimentosClinicos && !auditarPacotes;
		boolean isHonorarioPacote = auditarPacotes && !auditarProcedimentosGrauAnestesista && !auditarProcedimentosCirurgicos && !auditarProcedimentosClinicos;
		
		boolean isHonorarioCirurgicoClinico = auditarProcedimentosCirurgicos && auditarProcedimentosClinicos && !auditarProcedimentosGrauAnestesista && !auditarPacotes;
		boolean isHonorarioCirurgicoAnestesico = auditarProcedimentosCirurgicos && !auditarProcedimentosClinicos && auditarProcedimentosGrauAnestesista && !auditarPacotes;
		boolean isHonorarioCirurgicoPacote = auditarProcedimentosCirurgicos && !auditarProcedimentosClinicos && !auditarProcedimentosGrauAnestesista && auditarPacotes;
		boolean isHonorarioCirurgicoClinicoAnestesico = auditarProcedimentosCirurgicos && auditarProcedimentosClinicos && auditarProcedimentosGrauAnestesista && !auditarPacotes;
		
		boolean isHonorarioClinicoAnestesico = !auditarProcedimentosCirurgicos && auditarProcedimentosClinicos && auditarProcedimentosGrauAnestesista && !auditarPacotes;
		boolean isHonorarioClinicoPacote = !auditarProcedimentosCirurgicos && auditarProcedimentosClinicos && !auditarProcedimentosGrauAnestesista && auditarPacotes;
		boolean isHonorarioClinicoAnestesicoPacote = !auditarProcedimentosCirurgicos && auditarProcedimentosClinicos && auditarProcedimentosGrauAnestesista && auditarPacotes;
		
		boolean isHonorarioAnestesicoPacote = !auditarProcedimentosCirurgicos && !auditarProcedimentosClinicos && auditarProcedimentosGrauAnestesista && auditarPacotes;
		
		if(isHonorarioCirurgico && !hasProcedimentosCirurgicos){
			throw new ValidateException(MensagemErroEnumSR.GUIA_SEM_HONORARIO.getMessage("cir�rgicos")); 
		} else if (isHonorarioClinico && !hasProcedimentosClinicos) {
			throw new ValidateException(MensagemErroEnumSR.GUIA_SEM_HONORARIO.getMessage("cl�nicos")); 
		} else if (isHonorarioAnestesista && !hasProcedimentosCirurgicoAnestesico) {
			throw new ValidateException(MensagemErroEnumSR.GUIA_SEM_HONORARIO.getMessage("anest�sicos")); 
		} else if (isHonorarioPacote && !hasHonorariosPacotes) {
			throw new ValidateException(MensagemErroEnumSR.GUIA_SEM_HONORARIO.getMessage("pacotes")); 
		} else if(isHonorarioCirurgicoClinico && (!hasProcedimentosCirurgicos && !hasProcedimentosClinicos)){
			throw new ValidateException(MensagemErroEnumSR.GUIA_SEM_HONORARIO.getMessage("cir�rgicos e cl�nicos")); 
		} else if(isHonorarioCirurgicoAnestesico && (!hasProcedimentosCirurgicos && !hasProcedimentosCirurgicoAnestesico)){
			throw new ValidateException(MensagemErroEnumSR.GUIA_SEM_HONORARIO.getMessage("cir�rgicos e anest�sicos")); 
		} else if(isHonorarioCirurgicoPacote && (!hasProcedimentosCirurgicos && !hasHonorariosPacotes)){
			throw new ValidateException(MensagemErroEnumSR.GUIA_SEM_HONORARIO.getMessage("cir�rgicos e pacotes")); 
		} else if(isHonorarioClinicoAnestesico && (!hasProcedimentosClinicos && !hasProcedimentosCirurgicoAnestesico)){
			throw new ValidateException(MensagemErroEnumSR.GUIA_SEM_HONORARIO.getMessage("cl�nicos e anest�sicos")); 
		} else if(isHonorarioClinicoPacote && (!hasProcedimentosClinicos && !hasHonorariosPacotes)){
			throw new ValidateException(MensagemErroEnumSR.GUIA_SEM_HONORARIO.getMessage("cl�nicos e pacotes")); 
		} else if(isHonorarioCirurgicoClinicoAnestesico && (!hasProcedimentosCirurgicos && !hasProcedimentosClinicos && !hasProcedimentosCirurgicoAnestesico)){
			throw new ValidateException(MensagemErroEnumSR.GUIA_SEM_HONORARIO.getMessage("cir�rgico, cl�nicos e anest�sicos")); 
		} else if(isHonorarioClinicoAnestesicoPacote && (!hasProcedimentosClinicos && !hasProcedimentosCirurgicoAnestesico && !hasHonorariosPacotes)){
			throw new ValidateException(MensagemErroEnumSR.GUIA_SEM_HONORARIO.getMessage("cl�nicos, anest�sicos e pacotes")); 
		} else if(isHonorarioAnestesicoPacote && (!hasProcedimentosCirurgicoAnestesico && !hasHonorariosPacotes)){
			throw new ValidateException(MensagemErroEnumSR.GUIA_SEM_HONORARIO.getMessage("anest�sicos e pacotes")); 
		} else if(!hasProcedimentosCirurgicos && !hasProcedimentosClinicos && !hasProcedimentosCirurgicoAnestesico && !hasHonorariosPacotes){
			throw new ValidateException(MensagemErroEnumSR.GUIA_SEM_HONORARIO.getMessage("cir�rgicos, cl�nicos, anest�sicos e pacotes")); 
		}
		
		return true;
	}

}
