/**
 * 
 */
package br.com.infowaypi.ecarebc.associados;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;

import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.LikeFull;
import br.com.infowaypi.msr.address.Municipio;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author Marcus Boolean
 *
 */
public class ResumoProfissionais {

	Set<Profissional> profissionaisEncontrados;
	
	public ResumoProfissionais() {
		// TODO Auto-generated constructor stub
	}
	
	public ResumoProfissionais(Especialidade especialidade, Prestador prestador, String profissional, String bairro, Municipio municipio) throws Exception {
		this.profissionaisEncontrados = new HashSet<Profissional>();
		
		boolean profissionalCitado = !Utils.isStringVazia(profissional);
		boolean prestadorCitado = prestador != null;
		boolean especialidadeCitada = especialidade != null;
		boolean bairroCitado = !Utils.isStringVazia(bairro);
		boolean municipioCitado = municipio != null;
		
		if(profissionalCitado) {
			 findProfissionais(profissional);
		}else if(profissionalCitado && prestadorCitado) {
			 findProfissionais(profissional, prestador);
		}else if(profissionalCitado && prestadorCitado && especialidadeCitada) {
			 findProfissionais(profissional, prestador, especialidade);
		}else if(profissionalCitado && prestadorCitado && especialidadeCitada && bairroCitado) {
			 findProfissionais(profissional, prestador, especialidade, bairro);
		}else if(profissionalCitado && prestadorCitado && especialidadeCitada && bairroCitado && municipioCitado) {
			 findProfissionais(profissional, prestador, especialidade, bairro, municipio);
		}else if(prestadorCitado) {
			 findProfissionais(prestador);
		}else if(prestadorCitado && especialidadeCitada) {
			 findProfissionais(prestador, especialidade);
		}else if(prestadorCitado && especialidadeCitada && bairroCitado) {
			 findProfissionais(prestador, especialidade, bairro);
		}else if(prestadorCitado && especialidadeCitada && bairroCitado && municipioCitado) {
			 findProfissionais(prestador, especialidade, bairro, municipio);
		}else if(especialidadeCitada) {
			 findProfissionais(especialidade);
		}else if(especialidadeCitada && bairroCitado) {
			 findProfissionais(especialidade, bairro);
		}else if(especialidadeCitada && bairroCitado && municipioCitado) {
			 findProfissionais(especialidade, bairro, municipio);
		}else if(bairroCitado) {
			 findProfissionaisPeloBairro(bairro);
		}else if(bairroCitado && municipioCitado) {
			 findProfissionais(bairro, municipio);
		}else if(municipioCitado) {
			 findProfissionais(municipio);
		}else {
			findProfissionais();
		}
	}

	public Set<Profissional> getProfissionaisEncontrados() throws Exception {
		return this.profissionaisEncontrados;
	}

	private void findProfissionaisPeloBairro(String bairro) {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new LikeFull("pessoaJuridica.endereco.bairro", bairro));
		List<Prestador> prestadoresSa = sa.list(Prestador.class);
		Set<Profissional> profissionaisEncontrados = new HashSet<Profissional>();
		for (Prestador prestador : prestadoresSa) {
			for (Profissional profissional : prestador.getProfissionais()) {
				if(profissional.isCredenciado() == true)
					profissionaisEncontrados.add(profissional);
			}
		}
		this.profissionaisEncontrados = profissionaisEncontrados;
	}

	private void findProfissionais() throws Exception {
		throw new RuntimeException("Pelo menos um parâmetro de pesquisa deve ser informado!");
	}

	private void findProfissionais(Municipio municipio) {
		SearchAgent sa = new SearchAgent();
		Set<Profissional> profissionaisEncontrados = new HashSet<Profissional>();
		sa.addParameter(new Equals("pessoaJuridica.endereco.municipio",municipio));
		List<Prestador> prestadoresSa = sa.list(Prestador.class);
		for (Prestador prestador : prestadoresSa) {
			for (Profissional profissional : prestador.getProfissionais()) {
				if(profissional.isCredenciado() == true)
					profissionaisEncontrados.add(profissional);
			}
		}
		this.profissionaisEncontrados = profissionaisEncontrados;
	}

	private void findProfissionais(String bairro, Municipio municipio) {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new LikeFull("pessoaJuridica.endereco.bairro", bairro));
		sa.addParameter(new Equals("pessoaJuridica.endereco.Municipio", municipio));
		List<Prestador> prestadoresSa = sa.list(Prestador.class);
		Set<Profissional> profissionaisEncontrados = new HashSet<Profissional>();
		for (Prestador prestador : prestadoresSa) {
			for (Profissional profissional : prestador.getProfissionais()) {
				if(profissional.isCredenciado() == true)
					profissionaisEncontrados.add(profissional);
			}
		}
		this.profissionaisEncontrados = profissionaisEncontrados;
	}

	private void findProfissionais(Especialidade especialidade, String bairro, Municipio municipio) {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new LikeFull("pessoaJuridica.endereco.bairro", bairro));
		sa.addParameter(new Equals("pessoaJuridica.endereco.Municipio", municipio));
		List<Prestador> prestadoresSa = sa.list(Prestador.class);
		Set<Profissional> profissionaisEncontrados = new HashSet<Profissional>();
		for (Prestador prestador : prestadoresSa) {
			for (Profissional profissional : prestador.getProfissionais()) {
				if(profissional.isCredenciado() == true && profissional.getEspecialidades().contains(especialidade))
					profissionaisEncontrados.add(profissional);
			}
		}
		this.profissionaisEncontrados = profissionaisEncontrados;
	}

	private void findProfissionais(Especialidade especialidade, String bairro) {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new LikeFull("pessoaJuridica.endereco.bairro", bairro));
		List<Prestador> prestadoresSa = sa.list(Prestador.class);
		Set<Profissional> profissionaisEncontrados = new HashSet<Profissional>();
		for (Prestador prestador : prestadoresSa) {
			for (Profissional profissional : prestador.getProfissionais()) {
				if(profissional.isCredenciado() == true && profissional.getEspecialidades().contains(especialidade))
					profissionaisEncontrados.add(profissional);
			}
		}
		this.profissionaisEncontrados = profissionaisEncontrados;
	}

	private void findProfissionais(Especialidade especialidade) {
		
		Set<Profissional> profissionaisEncontrados = new HashSet<Profissional>();
		List<Profissional> profissionaisSa = HibernateUtil.currentSession().createCriteria(Profissional.class)
			.add(Expression.sizeGt("prestadores", 0)).list();
		
		for (Profissional profissional : profissionaisSa) {
			if (profissional.getEspecialidades().contains(especialidade))
			profissionaisEncontrados.add(profissional);
		}
		
		this.profissionaisEncontrados = profissionaisEncontrados;
	}

	private void findProfissionais(Prestador prestador2, Especialidade especialidade2, String bairro2, Municipio municipio2) {
		Set<Profissional> profissionaisEncontrados = new HashSet<Profissional>();
		for (Profissional profissional : prestador2.getProfissionais()) {
			if(profissional.isCredenciado() == true)
				profissionaisEncontrados.add(profissional);
		}
		this.profissionaisEncontrados = profissionaisEncontrados;
	}

	private void findProfissionais(Prestador prestador2, Especialidade especialidade2, String bairro2) {
		Set<Profissional> profissionaisEncontrados = new HashSet<Profissional>();
		for (Profissional profissional : prestador2.getProfissionais()) {
			if(profissional.isCredenciado() == true && profissional.getEspecialidades().contains(especialidade2))
				profissionaisEncontrados.add(profissional);
		}
		this.profissionaisEncontrados = profissionaisEncontrados;
	}

	private void findProfissionais(Prestador prestador, Especialidade especialidade) {
		Set<Profissional> profissionaisEncontrados = new HashSet<Profissional>();
		for (Profissional profissional : prestador.getProfissionais()) {
			if(profissional.getEspecialidades().contains(especialidade) && profissional.isCredenciado() == true)
				profissionaisEncontrados.add(profissional);
		}
		this.profissionaisEncontrados = profissionaisEncontrados;
	}

	private void findProfissionais(Prestador prestador) {
		this.profissionaisEncontrados = prestador.getProfissionais();
	}

	private void findProfissionais(String profissionalCitado2, Prestador prestador2, Especialidade especialidade2, String bairro2, Municipio municipio2) {
		findProfissionais(profissionalCitado2, prestador2, especialidade2);
		// Ignora-se o endereço do prestador uma vez que o mesmo ja foi informado
	}

	private void findProfissionais(String profissionalCitado2, Prestador prestador2, Especialidade especialidade2, String bairro2) {
		findProfissionais(profissionalCitado2, prestador2, especialidade2);
		// Ignora-se o endereço do prestador uma vez que o mesmo ja foi informado
	}

	private void findProfissionais(String profissionalCitado2, Prestador prestador, Especialidade especialidade2) {
		Set<Profissional> profissionaisEncontrados = new HashSet<Profissional>();
		for (Profissional profissional : prestador.getProfissionais()) {
			if(profissional.isCredenciado() == true && profissional.getEspecialidades().contains(especialidade2) && profissional.getPessoaFisica().getNome().contains(profissionalCitado2))
				profissionaisEncontrados.add(profissional);
		}
		this.profissionaisEncontrados = profissionaisEncontrados;
	}

	private void findProfissionais(String profissionalCitado, Prestador prestador) {
		Set<Profissional> profissionaisEncontrados = new HashSet<Profissional>();
		for (Profissional profissional : prestador.getProfissionais()) {
			if(profissional.getPessoaFisica().getNome().contains(profissionalCitado) && profissional.isCredenciado() == true)
				profissionaisEncontrados.add(profissional);
		}
		this.profissionaisEncontrados = profissionaisEncontrados;
	}

	private void findProfissionais(String profissionalCitado) {
//		SearchAgent sa = new SearchAgent();
//		sa.addParameter(new LikeFull("pessoaFisica.nome",profissionalCitado));
//		sa.addParameter(new Equals("credenciado",false));
		
		List<Profissional> profissionaisSa = HibernateUtil.currentSession().createCriteria(Profissional.class)
		.add(Expression.sizeNe("prestadores", 0))
		.add(Expression.ilike("pessoaFisica.nome", profissionalCitado, MatchMode.ANYWHERE)).list();
		
		for (Profissional profissional : profissionaisSa) {
			if (profissional.isCredenciado())
				this.profissionaisEncontrados.add(profissional);
		}

	}
	

	public void setProfissionaisEncontrados(
			Set<Profissional> profissionaisEncontrados) {
		this.profissionaisEncontrados = profissionaisEncontrados;
	}
	
	public int getNumeroProfissionaisEncontrados() {
		return this.profissionaisEncontrados.size();
	}
	
}