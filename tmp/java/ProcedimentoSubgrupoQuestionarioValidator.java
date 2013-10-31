package br.com.infowaypi.ecarebc.procedimentos.validators;

import java.util.Date;

import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecarebc.atendimentos.Critica;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.enums.TipoCriticaEnum;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.SubgrupoCBHPM;
import br.com.infowaypi.molecular.ImplDAO;

/**
 * ProcedimentoSubgrupoQuestionarioValidator é responsável por adicionar criticas à Guia que possuir Procedimentos 
 * com subgrupo CBHPM que estejam cadastrados no Questionário Qualificado do Segurado que ainda estiver em vigencia de carência.
 * @author wislanildo
 *
 * @param <P>
 * @param <G>
 */

@SuppressWarnings("rawtypes")
public class ProcedimentoSubgrupoQuestionarioValidator <P extends ProcedimentoInterface, G extends GuiaSimples> extends AbstractProcedimentoValidator<P, G>{

	@SuppressWarnings("unchecked")
	@Override
	protected boolean templateValidator(P proc, G guia) throws Exception {
		SubgrupoCBHPM subgrupoCBHPM = proc.getProcedimentoDaTabelaCBHPM().getSubgrupoCBHPM();
		Segurado segurado = ImplDAO.findById(guia.getSegurado().getIdSegurado(), Segurado.class); 
		
		boolean possuiCarencia = segurado.getCarenciaRestanteParaDoencasPreExistentes() > 0;
		boolean possuiQuestionario = segurado.getQuestionario() != null;
		
		if(possuiQuestionario && possuiCarencia && segurado.getQuestionario().getSubgruposProcedimentos().contains(subgrupoCBHPM) 
				&& proc.getIdProcedimento()==null){
			Critica critica = new Critica();
			critica.setGuia(guia);
			critica.setProcedimentoOrigem(proc);
			critica.setUsuario(guia.getUsuarioDoFluxo());
			critica.setTipoCritica(TipoCriticaEnum.CRITICA_DLP_SUBGRUPO.valor());
			critica.setData(new Date());
			critica.setMensagem(MensagemErroEnum.QUESTIONARIO_ALERTA_SUB_GRUPO.getMessage(proc.getProcedimentoDaTabelaCBHPM().getCodigo()));
			guia.getCriticas().add(critica);
			
			return false;
		}
		return true;
	}

}
