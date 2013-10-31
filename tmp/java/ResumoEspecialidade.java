package br.com.infowaypi.ecare.resumos;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.parameter.ParameterInterface;
import br.com.infowaypi.msr.utils.Assert;

public class ResumoEspecialidade {

	private final Especialidade especialidade; 

	public ResumoEspecialidade() {
		this(null);
	}
	
	public ResumoEspecialidade(Especialidade especialidade) {
		this.especialidade = especialidade;
	}	

	public ResumoEspecialidade resumoEspecialidade(Especialidade especialidade) {
		Assert.isNotNull(especialidade, "Selecione a especialidade desejada.");		
		return new ResumoEspecialidade(especialidade);
	}

	public Especialidade getEspecialidade() {
		return especialidade;
	}
	
	public Collection<Prestador> getPrestadores() {
		return especialidade.getPrestadores();
	}
	
	public Collection<Profissional> getProfissionais() {
		return especialidade.getProfissionais();
	}
	
	public void mostrarDetalhes(ResumoEspecialidade resumo){
	}

	@SuppressWarnings("unchecked")
	public List<Especialidade> getEspecialidades() {
		return ImplDAO.findByParam((Iterator<ParameterInterface>)null,Especialidade.class);
	}


}
