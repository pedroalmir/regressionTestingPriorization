package br.com.infowaypi.ecare.services.honorariomedico;

import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.resumos.ResumoGuiasHonorarioMedico;
import br.com.infowaypi.ecarebc.associados.validators.AbstractValidator;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * Validações do flow @AuditarGuiaHonorarioMedico <br>
 * Executa validações relacionadas a existencia de honorarios na guia quando se seleciona: cirurgico, anestesico, clinico ou pacote<br>
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
			throw new ValidateException(MensagemErroEnumSR.GUIA_SEM_HONORARIO.getMessage("cirúrgicos")); 
		} else if (isHonorarioClinico && !hasProcedimentosClinicos) {
			throw new ValidateException(MensagemErroEnumSR.GUIA_SEM_HONORARIO.getMessage("clínicos")); 
		} else if (isHonorarioAnestesista && !hasProcedimentosCirurgicoAnestesico) {
			throw new ValidateException(MensagemErroEnumSR.GUIA_SEM_HONORARIO.getMessage("anestésicos")); 
		} else if (isHonorarioPacote && !hasHonorariosPacotes) {
			throw new ValidateException(MensagemErroEnumSR.GUIA_SEM_HONORARIO.getMessage("pacotes")); 
		} else if(isHonorarioCirurgicoClinico && (!hasProcedimentosCirurgicos && !hasProcedimentosClinicos)){
			throw new ValidateException(MensagemErroEnumSR.GUIA_SEM_HONORARIO.getMessage("cirúrgicos e clínicos")); 
		} else if(isHonorarioCirurgicoAnestesico && (!hasProcedimentosCirurgicos && !hasProcedimentosCirurgicoAnestesico)){
			throw new ValidateException(MensagemErroEnumSR.GUIA_SEM_HONORARIO.getMessage("cirúrgicos e anestésicos")); 
		} else if(isHonorarioCirurgicoPacote && (!hasProcedimentosCirurgicos && !hasHonorariosPacotes)){
			throw new ValidateException(MensagemErroEnumSR.GUIA_SEM_HONORARIO.getMessage("cirúrgicos e pacotes")); 
		} else if(isHonorarioClinicoAnestesico && (!hasProcedimentosClinicos && !hasProcedimentosCirurgicoAnestesico)){
			throw new ValidateException(MensagemErroEnumSR.GUIA_SEM_HONORARIO.getMessage("clínicos e anestésicos")); 
		} else if(isHonorarioClinicoPacote && (!hasProcedimentosClinicos && !hasHonorariosPacotes)){
			throw new ValidateException(MensagemErroEnumSR.GUIA_SEM_HONORARIO.getMessage("clínicos e pacotes")); 
		} else if(isHonorarioCirurgicoClinicoAnestesico && (!hasProcedimentosCirurgicos && !hasProcedimentosClinicos && !hasProcedimentosCirurgicoAnestesico)){
			throw new ValidateException(MensagemErroEnumSR.GUIA_SEM_HONORARIO.getMessage("cirúrgico, clínicos e anestésicos")); 
		} else if(isHonorarioClinicoAnestesicoPacote && (!hasProcedimentosClinicos && !hasProcedimentosCirurgicoAnestesico && !hasHonorariosPacotes)){
			throw new ValidateException(MensagemErroEnumSR.GUIA_SEM_HONORARIO.getMessage("clínicos, anestésicos e pacotes")); 
		} else if(isHonorarioAnestesicoPacote && (!hasProcedimentosCirurgicoAnestesico && !hasHonorariosPacotes)){
			throw new ValidateException(MensagemErroEnumSR.GUIA_SEM_HONORARIO.getMessage("anestésicos e pacotes")); 
		} else if(!hasProcedimentosCirurgicos && !hasProcedimentosClinicos && !hasProcedimentosCirurgicoAnestesico && !hasHonorariosPacotes){
			throw new ValidateException(MensagemErroEnumSR.GUIA_SEM_HONORARIO.getMessage("cirúrgicos, clínicos, anestésicos e pacotes")); 
		}
		
		return true;
	}

}
