package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GuiaHonorarioMedico;
import br.com.infowaypi.ecarebc.enums.GrauDeParticipacaoEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;

public class GuiaHonorarioAnestesistaVinculadoCoopanestValidator{
	
	public static void execute(GuiaHonorarioMedico guia) throws Exception{
		validate(guia.getProfissional(),guia.getPrestador(), guia.getGrauDeParticipacao());
	}

	public static void validate(Profissional profissional, Prestador prestador, int grauDeParticipacao) throws Exception {
		boolean isAnestesista = grauDeParticipacao == GrauDeParticipacaoEnum.ANESTESISTA.getCodigo();
		boolean isAuxiliarAnestesista = grauDeParticipacao == GrauDeParticipacaoEnum.AUXILIAR_ANESTESISTA.getCodigo();
		
		if (isAnestesista || isAuxiliarAnestesista) {
			if (prestador.isPrestadorAnestesista()) {
				if (!prestador.getProfissionais().contains(profissional)) {
					throw new ValidateException("Este profisional não está viculado a este prestador.");
				}
			}
		}
	}

}
