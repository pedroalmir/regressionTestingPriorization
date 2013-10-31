/**
 * 
 */
package br.com.infowaypi.ecarebc.associados;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecare.arquivos.ArquivoBase;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.LikeFull;
import br.com.infowaypi.molecular.parameter.OR;
import br.com.infowaypi.msr.address.Municipio;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author Marcus bOolean
 * @changes Emanuel
 * 
 */
@SuppressWarnings("unchecked")
public class ResumoPrestadores {

	public static final Integer TIPO_RESULTADO_PRESTADORES = 1;
	public static final Integer TIPO_RESULTADO_PROFISSIONAIS = 2;

	Set<Prestador> prestadoresEncontrados = new HashSet<Prestador>();
	Set<Profissional> profissionaisEncontrados = new HashSet<Profissional>();
	Integer tipoResultado;

	private ArquivoBase arquivoPdf;

	public ResumoPrestadores(Especialidade especialidade, String prestador, Profissional profissional,
			TabelaCBHPM procedimento, String bairro, Municipio municipio, Integer tipoResultado,
			boolean eletivo, boolean urgencia, boolean odontologico) {

		this.tipoResultado = tipoResultado;

		boolean prestadorInformado 	= !Utils.isStringVazia(prestador);
		boolean municipioInformando = municipio != null;
		boolean profissionalInformado = profissional != null;
		boolean bairroInformado = !Utils.isStringVazia(bairro);
		boolean especialidadeInformada = especialidade != null;
		boolean procedimentoInformado = procedimento != null; 

		SearchAgent sa = new SearchAgent();
		sa.clearAllParameters();
		
		sa.addParameter(new Equals("situacao.descricao", SituacaoEnum.ATIVO.descricao()));
		
		if(prestadorInformado){
			sa.addParameter(new LikeFull("pessoaJuridica.fantasia", prestador));
		}
		if(municipioInformando){
			sa.addParameter(new Equals("pessoaJuridica.endereco.municipio", municipio));
		}
		if(bairroInformado){
			sa.addParameter(new LikeFull("pessoaJuridica.endereco.bairro", bairro));
		}

		if (procedimentoInformado) {
			sa.addParameter(new Equals("procedimentos.codigo", procedimento.getCodigo()));
		}

		if(eletivo && urgencia && odontologico){
			sa.addParameter(new OR(new Equals("fazInternacao", eletivo),
							new OR(new Equals("fazInternacaoEletiva", eletivo),
							new OR(new Equals("fazConsulta", eletivo), new Equals("fazExame", eletivo)))));
			
			sa.addParameter(new OR(new Equals("fazInternacaoUrgencia", urgencia),
								   new Equals("fazAtendimentoUrgencia", urgencia)));

			sa.addParameter(new Equals("fazOdontologico", odontologico));
			
		} else if(eletivo && urgencia){
			sa.addParameter(new OR(new Equals("fazInternacao", eletivo),
					new OR(new Equals("fazInternacaoEletiva", eletivo),
					new OR(new Equals("fazConsulta", eletivo), new Equals("fazExame", eletivo)))));
	
			sa.addParameter(new OR(new Equals("fazInternacaoUrgencia", urgencia),
								   new Equals("fazAtendimentoUrgencia", urgencia)));
			
		} else if(eletivo && odontologico){
			sa.addParameter(new OR(new Equals("fazInternacao", eletivo),
					new OR(new Equals("fazInternacaoEletiva", eletivo),
					new OR(new Equals("fazConsulta", eletivo), new Equals("fazExame", eletivo)))));
	
			sa.addParameter(new Equals("fazOdontologico", odontologico));
			
		} else if(urgencia && odontologico){
			sa.addParameter(new OR(new Equals("fazInternacaoUrgencia", urgencia),
						   new Equals("fazAtendimentoUrgencia", urgencia)));
			
			sa.addParameter(new Equals("fazOdontologico", odontologico));
			
		} else if(eletivo){
			sa.addParameter(new OR(new Equals("fazInternacao", eletivo),
					new OR(new Equals("fazInternacaoEletiva", eletivo),
					new OR(new Equals("fazConsulta", eletivo), new Equals("fazExame", eletivo)))));
			
		} else if(urgencia){
			sa.addParameter(new OR(new Equals("fazInternacaoUrgencia", urgencia),
						  	new Equals("fazAtendimentoUrgencia", urgencia)));
			
		} else if (odontologico){
			sa.addParameter(new Equals("fazOdontologico", odontologico));
		}


		List<Prestador> prestadoresSa = sa.list(Prestador.class);

		if(!especialidadeInformada && !profissionalInformado) {
			this.prestadoresEncontrados.addAll(prestadoresSa);
		}

		if(especialidadeInformada){
			for (Prestador prestadorFull : prestadoresSa) {
				boolean contemEspecialidade = prestadorFull.getEspecialidades(urgencia,eletivo).contains(especialidade);
				if(contemEspecialidade){
					this.prestadoresEncontrados.add(prestadorFull);
				}
			}
		}

		if(profissionalInformado){
			profissionaisEncontrados.add(profissional);
			for (Prestador prestadorFull : prestadoresSa) {
				if(prestadorFull.getProfissionais().contains(profissional)){
					this.prestadoresEncontrados.add(prestadorFull);
				}
			}
		}

		if(!this.prestadoresEncontrados.isEmpty() && !profissionalInformado) {
			Set<Profissional> profissionais = new HashSet<Profissional>();
			for (Prestador prest : prestadoresEncontrados) {
				for (Profissional prof : prest.getProfissionais()) {
					if(!profissionais.contains(prof)) {
						profissionais.add(prof);
					}
				}
			}
			this.profissionaisEncontrados.addAll(profissionais);
		}
	}

	public List<Prestador> getPrestadoresEncontrados() {
		List<Prestador> prestadores = new ArrayList<Prestador>(prestadoresEncontrados);
		Collections.sort(prestadores, new Comparator<Prestador>(){
			public int compare(Prestador prestador1, Prestador prestador2) {
				return prestador1.getPessoaJuridica().getFantasia().compareTo(prestador2.getPessoaJuridica().getFantasia());
			}
		});
		return prestadores;
	}

	public List<Profissional> getProfissionaisEncontrados() {
		List<Profissional> profissionais = new ArrayList<Profissional>(profissionaisEncontrados);
		Collections.sort(profissionais, new Comparator<Profissional>(){
			public int compare(Profissional profissional1, Profissional profissional2) {
				return profissional1.getPessoaFisica().getNome().compareTo(profissional2.getPessoaFisica().getNome());
			}
		});

		return profissionais;
	}

	public Integer getTipoResultado() {
		return tipoResultado;
	}

	public ArquivoBase getArquivoPdf() {
		return arquivoPdf;
	}

	public void setArquivoPdf(ArquivoBase arquivoPdf) {
		this.arquivoPdf = arquivoPdf;
	}

}
