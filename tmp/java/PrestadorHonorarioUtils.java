package br.com.infowaypi.ecare.services.honorariomedico;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GuiaHonorarioMedico;
import br.com.infowaypi.ecarebc.atendimentos.honorario.HonorarioExterno;
import br.com.infowaypi.ecarebc.enums.GrauDeParticipacaoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.ImplDAO;

class PrestadorHonorarioUtils {
	
	private static Prestador coopanest;
	public static Prestador getPrestadorDestino(Prestador prestadorGuiaMae, ProcedimentoInterface procedimento, Profissional profissional, 
			int grauDeParticipacao){
		
		Prestador resultado = null;
		GuiaHonorarioMedico guiaHonorarioCirurgiao = null;
		boolean fazParteCorpoClinicoPrestadorGuiaMae = prestadorGuiaMae.getProfissionais().contains(profissional);
		
		HonorarioExterno honorarioCirurgiao = (HonorarioExterno) procedimento.getHonorarioExterno(GrauDeParticipacaoEnum.RESPONSAVEL.getCodigo());
		if(honorarioCirurgiao != null){
			guiaHonorarioCirurgiao 	= honorarioCirurgiao.getGuiaHonorario();
		}
		
		Prestador prestadorProprio = profissional.getPrestadorProprioPeloCPF();
		if(prestadorProprio == null){
			prestadorProprio = profissional.getPrestadorProprio();
		}
		
		boolean isAnestesista 			= grauDeParticipacao == GrauDeParticipacaoEnum.ANESTESISTA.getCodigo();
		boolean isAuxiliarAnestesista 	= grauDeParticipacao == GrauDeParticipacaoEnum.AUXILIAR_ANESTESISTA.getCodigo();
		
		if(isAnestesista || isAuxiliarAnestesista){
			resultado = getCoopanest();
		}else if (prestadorProprio != null && prestadorProprio.isSituacaoAtual(SituacaoEnum.ATIVO.descricao())) {
			resultado = prestadorProprio;
		} else if(fazParteCorpoClinicoPrestadorGuiaMae){
			resultado = prestadorGuiaMae;
		} else if(guiaHonorarioCirurgiao != null){
			resultado = guiaHonorarioCirurgiao.getPrestador();
		} else {
			resultado = prestadorGuiaMae;
		}
		
		return resultado;
	}
	
	public static Prestador getPrestadorDestinoHonorariosPacotes(Profissional profissional){
		Prestador resultado = null;
		Prestador prestadorProprio = profissional.getPrestadorProprioPeloCPF();
		if(prestadorProprio == null){
			prestadorProprio = profissional.getPrestadorProprio();
			resultado = prestadorProprio;
			if(prestadorProprio == null){
				Prestador coopercardio = ImplDAO.findById(24079243L, Prestador.class);
				resultado = coopercardio;
			}
		}
		return resultado;
	}
	

	private static Prestador getCoopanest() {
		if(coopanest == null){
			coopanest = ImplDAO.findById(373690L, Prestador.class);
		}
		
		return coopanest;
	}
}
