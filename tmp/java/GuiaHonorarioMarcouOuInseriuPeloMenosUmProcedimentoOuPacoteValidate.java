package br.com.infowaypi.ecare.services.honorarios.validators;

import java.util.Set;

import br.com.infowaypi.ecarebc.enums.GrauDeParticipacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;

public class GuiaHonorarioMarcouOuInseriuPeloMenosUmProcedimentoOuPacoteValidate extends CommandValidator {

	@Override
	public void execute() throws ValidateException {
		Set<ProcedimentoInterface> procedimentosHonorarios = this.getGuiaOrigem().getProcedimentosQueVaoGerarHonorario();
		
		boolean possuiHonorarios  = procedimentosHonorarios != null && !procedimentosHonorarios.isEmpty();
		boolean possuiProcedimentos = this.getProcedimentosOutros() != null && !this.getProcedimentosOutros().isEmpty();
		
		if (!this.getPrestador().isPrestadorAnestesista()) {
			
			Assert.isTrue((possuiHonorarios || possuiProcedimentos || this.possuiPacotesHonorario()), getMensagem());
			Assert.isFalse(this.possuiPacotesHonorario() && (possuiHonorarios || possuiProcedimentos),
					"Não se pode registrar honorários para pacotes e inserir/marcar procedimentos ao mesmo tempo.");
			
		} else {
			Assert.isTrue(possuiHonorarios, "Marque pelo menos um dos procedimentos listados.");
		}
		
		if(this.possuiPacotesHonorario()){
			Assert.isEquals(GrauDeParticipacaoEnum.RESPONSAVEL.getCodigo(), this.getGrauDeParticipacao(), "Honorários de pacote só podem ser gerados para o grau RESPONSÁVEL.");
		}
	}

	private String getMensagem() {
		String mensagemMarcarAlgo = "Adicione um procedimento clinico ou marque um dos procedimentos listados";
		if(!this.getPrestador().getAcordosPacoteHonorarioAtivos().isEmpty()){
			mensagemMarcarAlgo += " ou insira um pacote de honorário";
		}
		mensagemMarcarAlgo+=".";
		return mensagemMarcarAlgo;
	}

}
