package br.com.infowaypi.ecare.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.enums.MotivoEnumSR;
import br.com.infowaypi.ecare.resumos.ResumoEntregarLoteGRG;
import br.com.infowaypi.ecare.services.recurso.GuiaRecursoGlosa;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.LoteDeGuias;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.utils.CompetenciaUtils;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.molecular.parameter.LowerEquals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

public class EntregarLoteGRG {

	@SuppressWarnings("unchecked")
	public ResumoEntregarLoteGRG buscarGuias(Date competencia, Prestador prestador) {
		
		Assert.isTrue(prestador.isExigeEntregaLote(), "Esse prestador não está habilitado para registrar o envio de lotes.");
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("guiaOrigem.prestador", prestador));
		Date comp = Utils.gerarCompetencia(competencia);
		sa.addParameter(new GreaterEquals("situacao.dataSituacao", CompetenciaUtils.getInicioCompetencia(comp)));
		sa.addParameter(new LowerEquals("situacao.dataSituacao", Utils.incrementaDias(CompetenciaUtils.getFimCompetencia(comp), 1)));
		sa.addParameter(new In("situacao.descricao", SituacaoEnum.RECURSADO.descricao(), SituacaoEnum.DEVOLVIDO.descricao()));
//		sa.addParameter(new Between("situacao.dataSituacao", inicioCompetencia, fimCompetencia));
		
		List<GuiaRecursoGlosa> guias = sa.list(GuiaRecursoGlosa.class);
		
		Assert.isNotEmpty(guias, MensagemErroEnumSR.NENHUMA_GUIA_APTA_A_SER_ENTREGUE.getMessage(Utils.format(competencia, "MM/yyyy")));
		
		this.tocarObjetos(guias);
		prestador.tocarObjetos();
		ResumoEntregarLoteGRG resumo = new ResumoEntregarLoteGRG(guias, prestador);
		
		return resumo;
	}
	
	public LoteDeGuias selecionarGuias(ResumoEntregarLoteGRG resumo, Collection<GuiaRecursoGlosa> guias) throws ValidateException{
		
		Assert.isNotEmpty(guias, "Selecione pelo menos uma guia!");
		
		GuiaRecursoGlosa primeiraGuia = new ArrayList<GuiaRecursoGlosa>(guias).get(0);
		Date competencia = CompetenciaUtils.getCompetencia(primeiraGuia.getCompetencia());
		LoteDeGuias lote = new LoteDeGuias(new ArrayList<GuiaRecursoGlosa>(guias), resumo.getPrestador(), competencia);
		
		Collections.sort(lote.getGuiasDeRecursoEnviadas(), new Comparator<GuiaRecursoGlosa>(){
			@Override
			public int compare(GuiaRecursoGlosa g1, GuiaRecursoGlosa g2) {
				return g1.getDataTerminoAtendimento().compareTo(g2.getDataTerminoAtendimento());
			}
		});
		
		lote.validateGRG();
		
		for (GuiaRecursoGlosa guia : guias) {
			guia.getSituacoes().size();
		}

		return lote;
	}
	
	public LoteDeGuias salvarLote(LoteDeGuias lote, UsuarioInterface usuario) throws Exception{
		
		lote.mudarSituacao(usuario, SituacaoEnum.ENVIADO.descricao(), LoteDeGuias.ENVIO_PRESTADOR, new Date());
		for (GuiaRecursoGlosa guia : lote.getGuiasDeRecursoEnviadas()) {
			guia.mudarSituacao(usuario, SituacaoEnum.ENVIADO.descricao(), MotivoEnumSR.GUIA_ENVIADA.getMessage(), new Date());
		}
		
		LoteDeGuias.salvarLote(lote);
		return lote;
	}
	
	public void finalizar(LoteDeGuias lote){}
	
	private void tocarObjetos(List<GuiaRecursoGlosa> guias) {
		for (GuiaRecursoGlosa guia : guias) {
			guia.getGuiaOrigem();
			guia.getSegurado().getPessoaFisica().getNome();
			guia.getSituacoes().size();
		}
	}

}
