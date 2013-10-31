package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.Role;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * Classe para validação de permissões de usuários nas guias exame
 * @author Danilo Nogueira Portela
 */
public class ExameUsuarioValidator<E extends GuiaExame<ProcedimentoInterface>> extends AbstractGuiaValidator<E>{
 
	public boolean templateValidator(E guia) throws ValidateException {
			UsuarioInterface usuarioDaGuia = guia.getSituacao().getUsuario();
			
			Boolean isUserAtendente = Role.ATENDENTE.getValor().equals(usuarioDaGuia.getRole());
			
			Boolean isUserRoot = Role.ROOT.getValor().equals(usuarioDaGuia.getRole());
			Boolean isUserAuditor = (Role.AUDITOR.getValor().equals(usuarioDaGuia.getRole()) || Role.DIGITADOR.getValor().equals(usuarioDaGuia.getRole()));
			
			Integer outrosProcAuditor = 0;
			Integer outrosProcSupervisor = 0;
			
			for (ProcedimentoInterface procedimento : guia.getProcedimentos()) {
				TabelaCBHPM cbhpm = procedimento.getProcedimentoDaTabelaCBHPM();
				Boolean isProcEspecial =  cbhpm.getEspecial();
				
				if (isProcEspecial == null)
					isProcEspecial = false;
				
				Integer numeroProcAuditoria = 0;
				Integer numeroProcEspeciais = 0;
				
				if(isUserAuditor || isUserRoot){
					if(isProcEspecial)
						numeroProcAuditoria++;
					else
						outrosProcAuditor++;
					
					if(numeroProcAuditoria > 0){
						if(numeroProcAuditoria > 1)
							throw new ValidateException(MensagemErroEnum.GUIA_EXAME_COM_PROCEDIMENTOS_ESPECIAIS_DEMAIS.getMessage());
						
						if(outrosProcAuditor > 0)
							throw new ValidateException(MensagemErroEnum.GUIA_EXAME_COM_PROCEDIMENTO_ESPECIAL_E_OUTROS.getMessage());
					}
				}
				
				if((isUserAtendente)){
					if(cbhpm.getEspecial())
						numeroProcEspeciais++;
					else
						outrosProcSupervisor++;
					
					if(numeroProcEspeciais > 0){
						if(numeroProcEspeciais > 1)
							throw new ValidateException(MensagemErroEnum.GUIA_EXAME_COM_PROCEDIMENTOS_ESPECIAIS_DEMAIS.getMessage());
						
						if(outrosProcSupervisor > 0)
							throw new ValidateException(MensagemErroEnum.GUIA_EXAME_COM_PROCEDIMENTO_ESPECIAL_E_OUTROS.getMessage());
					}
				}
			}
		return true;
	}
}
