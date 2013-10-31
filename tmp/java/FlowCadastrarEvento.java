package br.com.infowaypi.ecare.programaPrevencao.fluxos;

import br.com.infowaypi.ecare.enums.TipoEventoEnum;
import br.com.infowaypi.ecare.programaPrevencao.Evento;
import br.com.infowaypi.ecare.programaPrevencao.ProgramaDePrevencao;
import br.com.infowaypi.ecare.programaPrevencao.ResumoPrograma;
import br.com.infowaypi.ecare.programaPrevencao.SeguradoDoEvento;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * @author SR Team - Marcos Roberto 17.02.2012
 * 
 * Fluxo responsável por cadastrar eventos e associar beneficiários a um evento.
 */
public class FlowCadastrarEvento {

	public ResumoPrograma informarPrograma(ProgramaDePrevencao programa, UsuarioInterface usuario) {
		programa.tocarObjetos();
		Evento evento = new Evento();
		ResumoPrograma resumo = new ResumoPrograma();
		resumo.setProgramaSelecionado(programa);
		resumo.setEvento(evento);
		return resumo;
	}

	public ResumoPrograma associarBeneficiario(Integer tipo, ResumoPrograma resumo) throws Exception {
		if(tipo == null) {
			throw new ValidateException("O campo Tipo de Evento deve ser informado.");
		} 
		
		if(resumo.getBeneficiariosTransient().isEmpty()) {
			throw new ValidateException("Nenhum beneficiário foi informado para ser associado a esse evento.");
		}
		
		resumo.setTipoEvento(TipoEventoEnum.descricao(tipo));
		resumo.getEvento().setTipo(tipo);
		
		return resumo;
	}

	public ResumoPrograma salvar(ResumoPrograma resumo) throws Exception {
		for (SeguradoDoEvento seguradoDoEvento : resumo.getBeneficiariosTransient()) {
			AbstractSegurado segurado = seguradoDoEvento.getSegurado();
			
			resumo.getEvento().getBeneficiarios().add(segurado);
		}
		
		resumo.getProgramaSelecionado().getEventos().add(resumo.getEvento());
		ImplDAO.save(resumo.getProgramaSelecionado());
		
		ImplDAO.save(resumo.getEvento());
		return resumo;
	}

	public void finalizar(ResumoPrograma resumo){}

}