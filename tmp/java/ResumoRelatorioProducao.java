package br.com.infowaypi.ecare.relatorio.producao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.infowaypi.msr.utils.Utils;
import br.com.infowaypi.ecarebc.associados.Prestador;

/**
 * 
 * @author Danilo Medeiros
 * @changes Klebet
 */
public class ResumoRelatorioProducao{

	private ProducaoCompetencia totais;
	private Map<Prestador,ProducaoCompetenciaPrestador> mapProducoes;
	
	public ResumoRelatorioProducao(){
		this.mapProducoes = new HashMap<Prestador, ProducaoCompetenciaPrestador>();
		this.totais = new ProducaoCompetencia();
	}
	
	public ProducaoCompetencia getTotais() {
		return totais;
	}
	public void setTotais(ProducaoCompetencia totais) {
		this.totais = totais;
	}

	public Map<Prestador, ProducaoCompetenciaPrestador> getMapProducoes() {
		return mapProducoes;
	}
	
	public void setMapProducoes(Map<Prestador, ProducaoCompetenciaPrestador> mapProducoes) {
		this.mapProducoes = mapProducoes;
	}

	/**
	 * Retorna as producoes ordenadas pelo nome do prestador
	 */
	public List<ProducaoCompetenciaPrestador> getProducoes(){
		
		List<ProducaoCompetenciaPrestador> producoes = new ArrayList<ProducaoCompetenciaPrestador>(mapProducoes.values());
		Collections.sort(producoes, new Comparator<ProducaoCompetenciaPrestador>() {

			@Override
			public int compare(ProducaoCompetenciaPrestador p1, ProducaoCompetenciaPrestador p2) {
				String nomeP1 = "";
				String nomeP2 = "";
				
				if(p1.getPrestador().getPessoaJuridica() != null && !Utils.isStringVazia(p1.getPrestador().getPessoaJuridica().getNome())){
					nomeP1 = p1.getPrestador().getPessoaJuridica().getNome();
				}
				
				if(p2.getPrestador().getPessoaJuridica() != null && !Utils.isStringVazia(p2.getPrestador().getPessoaJuridica().getNome())){
					nomeP2 = p2.getPrestador().getPessoaJuridica().getNome();
				}
				
				return nomeP1.compareTo(nomeP2);
			}
		});

		return producoes;
	}

	@Override
	public String toString() {
	    return "ResumoReportProducao [totais=" + totais + ", mapProducoes="
		    + mapProducoes + ", getTotais()=" + getTotais()
		    + ", getMapProducoes()=" + getMapProducoes()
		    + ", getProducoes()=" + getProducoes() + ", getClass()="
		    + getClass() + ", hashCode()=" + hashCode()
		    + ", toString()=" + super.toString() + "]";
	}
	
}