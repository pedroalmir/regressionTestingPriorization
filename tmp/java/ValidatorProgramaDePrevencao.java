package br.com.infowaypi.ecare.programaPrevencao.fluxos;

import java.util.Date;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.infowaypi.ecare.programaPrevencao.AssociacaoSeguradoPrograma;
import br.com.infowaypi.ecare.programaPrevencao.ProgramaDePrevencao;
import br.com.infowaypi.ecare.programaPrevencao.SeguradoDoPrograma;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;

public class ValidatorProgramaDePrevencao extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		try {
			ProgramaDePrevencao programa = (ProgramaDePrevencao) req.getSession().getAttribute("programa");
			String cpf = req.getParameter("cpf");
			String motivo = req.getParameter("motivo");
			for (SeguradoDoPrograma segurado : programa.getSeguradosSelecionados()) {
				if (segurado.getCpf().equals(cpf)) {
					resp.setContentType("text/html");
					resp.getWriter().print(segurado.getCpf());
					segurado.setDescricao(SituacaoEnum.REMOVIDO.descricao());
					segurado.setMotivo(motivo);
					removeSegurado(segurado,motivo,programa);
				}
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}
	
	public void removeSegurado(SeguradoDoPrograma seguradoDoPrograma, String motivo, ProgramaDePrevencao programa) throws ValidateException{
		Segurado seguradoBuscado = BuscarSegurados.getSegurado(seguradoDoPrograma.getNumeroDoCartao(),seguradoDoPrograma.getCpf(),  Segurado.class, false);
		if (seguradoBuscado != null ){
			Set<AssociacaoSeguradoPrograma> associacaoSegurados2 = programa.getAssociacaoSegurados();
			for (AssociacaoSeguradoPrograma associacaoSeguradoPrograma : associacaoSegurados2) {
				if(associacaoSeguradoPrograma.getSegurado().equals(seguradoBuscado)){
					associacaoSeguradoPrograma.mudarSituacao(null,SituacaoEnum.REMOVIDO.descricao(), motivo , new Date());
				}
			}
		}
	}

}
