package br.com.infowaypi.ecare.services;


import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.recurso.GuiaRecursoGlosa;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.LoteDeGuias;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.GuiaFaturavel;
import br.com.infowaypi.ecarebc.service.ReimpressaoGuiasService;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.utils.Assert;

public class ReimpressaoDeLotesService extends ReimpressaoGuiasService<Segurado> {
	
	public LoteDeGuias buscarLote(Long identificador, String autorizacao) throws Exception{
		Assert.isFalse((autorizacao == null && identificador == null), "Pelo menos um campo deve ser preenchido.");
		LoteDeGuias lote = null;
		
		//busca pelo identificador
		if (identificador != null){
			lote = ImplDAO.findById(identificador, LoteDeGuias.class);
			Assert.isNotNull(lote, MensagemErroEnum.LOTE_NAO_ENCONTRADO.getMessage(""+identificador));
		//busca por uma autorizacao de guia
		} else {
			SearchAgent sa = new SearchAgent();
			sa.addParameter(new Equals("autorizacao", autorizacao));
			GuiaFaturavel guia = sa.uniqueResult(GuiaFaturavel.class);
			Assert.isNotNull(guia, MensagemErroEnum.NENHUMA_GUIA_ENCONTRADA.getMessage());
			if (guia instanceof GuiaSimples) {
				Assert.isNotNull(((GuiaSimples)guia).getUltimoLote(), MensagemErroEnum.GUIA_NAO_POSSUI_LOTE.getMessage(autorizacao));
				lote = ((GuiaSimples)guia).getUltimoLote();
			} else if (guia instanceof GuiaRecursoGlosa){
				Assert.isNotNull(((GuiaRecursoGlosa)guia).getUltimoLote(), MensagemErroEnum.GUIA_NAO_POSSUI_LOTE.getMessage(autorizacao));
				lote = ((GuiaRecursoGlosa)guia).getUltimoLote();
			} else {
				Assert.isNotNull(lote, MensagemErroEnum.LOTE_NAO_ENCONTRADO.getMessage(""+identificador));
			}
		}
		
		/*
		 * Matheus: atualizar o valor total apresentado e o valor total auditado.
		 * OBS: valor persistido no banco (valortotal) representa na realidade o valor apresentado. Valores não deveriam ser persistidos em lotes.
		 */
		lote.atualizaValorTotalApresentado();
		lote.atualizaValorTotal();
		
		return lote;
	}
	
	public void finalizar(LoteDeGuias lote) {}
	
}

