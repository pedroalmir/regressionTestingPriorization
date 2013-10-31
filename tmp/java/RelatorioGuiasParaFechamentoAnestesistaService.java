package br.com.infowaypi.ecare.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecare.resumos.ResumoGuia;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.In;


public class RelatorioGuiasParaFechamentoAnestesistaService extends Service{

	@SuppressWarnings("unchecked")
	public <G extends GuiaCompleta> ResumoGuia buscarGuias(String dataInicial, String dataFinal, Prestador prestador) throws Exception{
		List<GuiaCompleta> guias  = getGuiasParaFechamento(dataInicial, dataFinal, prestador);

		Iterator<GuiaCompleta> iteratorGuias = guias.iterator();
		List<GuiaSimples> resultado =  new ArrayList<GuiaSimples>();
		while (iteratorGuias.hasNext()) {
			GuiaCompleta guia = iteratorGuias.next();
			if(guia.isInternacao() && guia.isAptaParaFechamentoAnestesista())
				resultado.add(guia);
		}
		ResumoGuia resumo = new ResumoGuia(resultado,ResumoGuia.SITUACAO_TODAS,false); 		
		return resumo;
	}

	private List<GuiaCompleta> getGuiasParaFechamento(String dataInicial, String dataFinal,
			Prestador prestador) throws Exception {
		
		Set<Long> idGuias = new HashSet<Long>();
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("situacao.descricao",SituacaoEnum.AUTORIZADO.descricao()));
		List<ProcedimentoCirurgico> procedimentos = sa.list(ProcedimentoCirurgico.class);
		for (ProcedimentoCirurgico procedimento : procedimentos) {
			if(!procedimento.getGuia().isSituacaoAtual(SituacaoEnum.FATURADA.descricao()) 
				&& !procedimento.getGuia().isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao())
				&& !procedimento.getGuia().isSituacaoAtual(SituacaoEnum.AUDITADO.descricao())){
			
				idGuias.add(procedimento.getGuia().getIdGuia());
			}
		}
		
		sa.clearAllParameters();
		sa.addParameter(new In("idGuia",idGuias));
		
		if(prestador != null)
			sa.addParameter(new Equals("prestador",prestador));
		
		return  sa.list(GuiaCompleta.class);
	}
	
	public ResumoGuia finalizar(ResumoGuia resumo) {
		return resumo;
	}
	
	public static void main(String[] args) {
		GuiaSimples guia = ImplDAO.findById(66l, GuiaSimples.class);
		
		GuiaCompleta guia2 = (GuiaCompleta) guia;
		
		System.out.println("FIM");
	}
	
}
