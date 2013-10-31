package br.com.infowaypi.ecare.programaPrevencao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * classe que da o resultado de uma pesquisa sobre programas de prevenção
 */
public class ResumoPrograma {
	
	private List<ProgramaDePrevencao> programas = new ArrayList<ProgramaDePrevencao>();
	private ProgramaDePrevencao programaSelecionado;
	private String tipoEvento;
	private Evento evento;
	private transient Set<SeguradoDoEvento> beneficiariosTransient;

	public ResumoPrograma() {
		beneficiariosTransient = new HashSet<SeguradoDoEvento>();
	}
	
	public List<ProgramaDePrevencao> getProgramas() {
		return programas;
	}

	public void setProgramas(List<ProgramaDePrevencao> programas) {
		this.programas = programas;
	}

	public ProgramaDePrevencao getProgramaSelecionado() {
		return programaSelecionado;
	}

	public void setProgramaSelecionado(ProgramaDePrevencao programaSelecionado) {
		this.programaSelecionado = programaSelecionado;
	}

	public String getTipoEvento() {
		return tipoEvento;
	}

	public void setTipoEvento(String tipoEvento) {
		this.tipoEvento = tipoEvento;
	}

	public Evento getEvento() {
		return evento;
	}

	public void setEvento(Evento evento) {
		this.evento = evento;
	}

	public Set<SeguradoDoEvento> getBeneficiariosTransient() {
		return beneficiariosTransient;
	}

	public void setBeneficiariosTransient(
			Set<SeguradoDoEvento> beneficiariosTransient) {
		this.beneficiariosTransient = beneficiariosTransient;
	}
	
	public void addSegurado(SeguradoDoEvento seguradoDoEvento) throws ValidateException{
		Segurado seguradoBuscado = BuscarSegurados.getSegurado(seguradoDoEvento.getNumeroDoCartao(), seguradoDoEvento.getCpf(),  Segurado.class, false);
		
		if (seguradoBuscado != null ) {
			Set<SeguradoDoEvento> beneficiarios = this.getBeneficiariosTransient();
			
			seguradoDoEvento.setSegurado(seguradoBuscado);
			
			seguradoDoEvento.setNome(seguradoBuscado.getPessoaFisica().getNome());
			seguradoDoEvento.setNumeroDoCartao(seguradoBuscado.getNumeroDoCartao());
			seguradoDoEvento.setCpf(seguradoBuscado.getPessoaFisica().getCpf());
			
			if(!beneficiarios.contains(seguradoBuscado)) {
				beneficiarios.add(seguradoDoEvento);				
			}
		}
	}

	public void removeSegurado(SeguradoDoEvento seguradoDoEvento) throws ValidateException {
		Segurado seguradoBuscado = BuscarSegurados.getSegurado(seguradoDoEvento.getNumeroDoCartao(), seguradoDoEvento.getCpf(),  Segurado.class, false);
		
		if (seguradoBuscado != null ) {
			Set<SeguradoDoEvento> beneficiarios = this.getBeneficiariosTransient();
			
			if(beneficiarios.contains(seguradoDoEvento)) {
				beneficiarios.remove(seguradoDoEvento);
			}
		}
	}
}