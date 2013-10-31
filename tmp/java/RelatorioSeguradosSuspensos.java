package br.com.infowaypi.ecare.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Relatório de consultas (eletivas e de urgência) realizadas em um determinado mês, agrupadas por seus respectivos prestadores.
 * @author benedito
 *
 */
public class RelatorioSeguradosSuspensos {
	
	List<AbstractSegurado> segurados;
	
	public RelatorioSeguradosSuspensos() {
		segurados = new ArrayList<AbstractSegurado>();
	}
	
	public RelatorioSeguradosSuspensos gerarRelatorio(int intervalo){
		
		Assert.isTrue(intervalo >= 0, "O intervalo de dias deve ser maior que zero.");
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("situacao.descricao", SituacaoEnum.SUSPENSO.descricao()));
		List<AbstractSegurado> seguradosSuspensos = sa.list(AbstractSegurado.class);
		
		for(AbstractSegurado segurado : seguradosSuspensos){
			int diferencaEmDias = Math.abs(Utils.diferencaEmDias(Calendar.getInstance(), getDataSuspensaoSegurado(segurado)));
			if(diferencaEmDias <= intervalo){
				segurados.add(segurado);
			}
		}
		
		Utils.sort(segurados, "situacao.data");
		
		return this;
	}

	private Calendar getDataSuspensaoSegurado(AbstractSegurado segurado) {
		Calendar dataSituacao = Calendar.getInstance();
		dataSituacao.setTime(segurado.getSituacao().getDataSituacao());
		return dataSituacao;
	}

	public List<AbstractSegurado> getSegurados() {
		return segurados;
	}

	public void setSegurados(List<AbstractSegurado> segurados) {
		this.segurados = segurados;
	}

	public static void main(String[] args) {
		RelatorioSeguradosSuspensos relatorio = new RelatorioSeguradosSuspensos();
		RelatorioSeguradosSuspensos resultado = relatorio.gerarRelatorio(60);
		
		for(AbstractSegurado seg : resultado.getSegurados()){
			System.out.println(seg.getPessoaFisica().getNome()  + " - " + seg.getSituacao().getDescricao() + " - " + seg.getSituacao().getDataSituacao());
		}
	}
}
