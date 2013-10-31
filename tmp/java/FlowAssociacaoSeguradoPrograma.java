package br.com.infowaypi.ecare.programaPrevencao.fluxos;

import br.com.infowaypi.ecare.programaPrevencao.ProgramaDePrevencao;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
/**
 * fluxo responsável por associar ou remover um segurado de um programa de prevenção
 */
public class FlowAssociacaoSeguradoPrograma {

	public ProgramaDePrevencao informarPrograma(ProgramaDePrevencao programa, UsuarioInterface usuario) {
		programa.tocarObjetos();
		return programa;
	}

	public ProgramaDePrevencao associarSegurado(ProgramaDePrevencao programa) throws ValidateException{
		return programa;
	}

	public ProgramaDePrevencao salvar(ProgramaDePrevencao programa) throws Exception{
		ImplDAO.save(programa);
		return programa;
	}

	public void finalizar(){
	}
}
