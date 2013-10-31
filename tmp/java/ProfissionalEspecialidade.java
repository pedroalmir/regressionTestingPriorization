package br.com.infowaypi.ecare.programaPrevencao;

import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.msr.exceptions.ValidateException;

public class ProfissionalEspecialidade {

	private Long idProfissionalEspecialidade;
	private Profissional profissional;
	private Especialidade especialidade;
	
	public Long getIdProfissionalEspecialidade() {
		return idProfissionalEspecialidade;
	}

	public void setIdProfissionalEspecialidade(Long idProfissionalEspecialidade) {
		this.idProfissionalEspecialidade = idProfissionalEspecialidade;
	}
	
	public Profissional getProfissional() {
		return profissional;
	}

	public void setProfissional(Profissional profissional) {
		this.profissional = profissional;
	}

	public Especialidade getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(Especialidade especialidade) {
		this.especialidade = especialidade;
	}
	
	public Boolean validate() throws ValidateException {
		boolean containsEspecialidade = profissional.getEspecialidades().contains(this.especialidade);
		if (!containsEspecialidade) {
			throw new ValidateException("A especialidade deve estar associada a um profissional.");
		}
		return containsEspecialidade;
	}
	
}
