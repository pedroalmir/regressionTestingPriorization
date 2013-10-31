package br.com.infowaypi.ecarebc.procedimentos.validators;

import java.util.Date;
import java.util.Set;

import br.com.infowaypi.ecare.questionarioqualificado.Questionario;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecarebc.atendimentos.Critica;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.enums.TipoCriticaEnum;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.procedimentos.CID;
import br.com.infowaypi.molecular.ImplDAO;
/**
 * Validação das CIDs do questionário qualificado
 * @author jefferson
 *
 * @param <P>
 * @param <G>
 */
@SuppressWarnings("rawtypes")
public class QuestionarioCIDsValidator <G extends GuiaCompleta> implements AtendimentoValidator<G> {

	@Override
	public boolean execute(G guia) throws Exception {
		
		Segurado segurado 		 	=  ImplDAO.findById(guia.getSegurado().getIdSegurado(), Segurado.class);
		Questionario questionario 	= segurado.getQuestionario();
		
		boolean possuiQuestionario 	= questionario != null;
		boolean possuiCarencia 		= segurado.getCarenciaRestanteParaDoencasPreExistentes() > 0;
		
		if (possuiQuestionario && possuiCarencia){
			
			Set<CID>cidsQuestionario = questionario.getCids();
			Set<CID>cidsGuiaSegurado = guia.getCids();
			
			boolean isContemCIDs = contemCIDsEmListaDaGuia(cidsQuestionario,cidsGuiaSegurado);
			
			if (isContemCIDs){
				Critica critica = new Critica();
				critica.setGuia(guia);
				critica.setUsuario(guia.getUsuarioDoFluxo());
				critica.setTipoCritica(TipoCriticaEnum.CRITICA_DLP_CID.valor());
				critica.setData(new Date());
				critica.setMensagem(MensagemErroEnum.QUESTIONARIO_ALERTA_CID.getMessage());
				guia.getCriticas().add(critica);
				
				return false;
			}
		}
		return true;
	}

	private boolean contemCIDsEmListaDaGuia(Set<CID> cidsQuestionario, Set<CID> cidsGuiaSegurado) {
		for(CID cidQuestionario: cidsQuestionario){
			if (cidsGuiaSegurado.contains(cidQuestionario)){
				return true;
			}
		}
		return false;
	}

}
