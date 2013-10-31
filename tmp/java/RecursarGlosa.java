package br.com.infowaypi.ecare.services.recurso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.ItemGlosavel;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

@SuppressWarnings({"rawtypes", "unchecked"})
public class RecursarGlosa extends Service {

	public GuiaCompleta buscarGuias(String autorizacao, UsuarioInterface usuario) throws Exception {
		SearchAgent sa = new SearchAgent();
		if(!Utils.isStringVazia(autorizacao)) {			
			sa.addParameter(new Equals("autorizacao", autorizacao.trim()));
		}
		sa.addParameter(new GreaterEquals("dataTerminoAtendimento", Utils.parse(ManagerBuscaGRG.DATA_IMPLANTACAO_RECURSO_GLOSA)));

		GuiaCompleta guia = sa.uniqueResult(GuiaCompleta.class);

		if (guia == null) {
			throw new ValidateException("Guia não encontrada!");
		}
		
		guia.setUsuarioDoFluxo(usuario);
		guia.setRecursando(true);
		if (guia.getSituacao().getDescricao().equals(SituacaoEnum.GLOSADO.descricao())) {
			guia.fillLayerGuiaGlosada();
		} else {
			guia.fillLayersRecurso();
		}
		validate(guia);
		guia.tocarObjetos();
		return guia;
	}
	
	public GuiaRecursoGlosa recursarGlosa(GuiaCompleta guia, UsuarioInterface usuario) {
		guia.tocarObjetos();
		gerarRecursos(guia, usuario);
		guia.getGuiaRecursoGlosa().setDataRecurso(new Date());
		guia.getGuiaRecursoGlosa().getItensRecurso().size();
		guia.getGuiaRecursoGlosa().mudarSituacao(usuario, SituacaoEnum.RECURSADO.descricao(), "Guia de Recurso de Glosa gerada.", new Date());
		guia.getGuiaRecursoGlosa().recalcularValores();
		return guia.getGuiaRecursoGlosa();
	}

	public void salvarGuia(GuiaRecursoGlosa guia) throws Exception{
		ImplDAO.save(guia);
	}

	private void gerarRecursos(GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario) {
		if (guia.getGuiaRecursoGlosa() == null) {
			guia.setGuiaRecursoGlosa(new GuiaRecursoGlosa(guia));
		}
		List<ItemRecursoLayer> itensRecursados = new ArrayList<ItemRecursoLayer>();

		itensRecursados.addAll(getItensRecursados(guia.getLayerRecursoGuia()));
		itensRecursados.addAll(getItensRecursados(guia.getLayersRecursoItensGasoterapia()));
		itensRecursados.addAll(getItensRecursados(guia.getLayersRecursoItensTaxa()));
		itensRecursados.addAll(getItensRecursados(guia.getLayersRecursoItensDiaria()));
		itensRecursados.addAll(getItensRecursados(guia.getLayersRecursoItensPacote()));
		itensRecursados.addAll(getItensRecursados(guia.getLayersRecursoProcedimentoCirurgico()));
		itensRecursados.addAll(getItensRecursados(guia.getLayersRecursoProcedimentosExame()));
		itensRecursados.addAll(getItensRecursados(guia.getLayersRecursoProcedimentosOutros()));
		for (ItemRecursoLayer item : itensRecursados) {
			ItemGlosavel itemGlosado = item.getItem();
			String justificativa = item.getJustificativa();
			GuiaRecursoGlosa recurso = guia.getGuiaRecursoGlosa();
			guia.getGuiaRecursoGlosa().getItensRecurso().add(new ItemRecursoGlosa(usuario, itemGlosado, recurso, justificativa));

		}
		guia.getGuiaRecursoGlosa().setLayerFlowRecurso(true);
	}	
	
	private List<ItemRecursoLayer> getItensRecursados(List<ItemRecursoLayer> layers) {
		List<ItemRecursoLayer> itensRecursados = new ArrayList<ItemRecursoLayer>();
		for (ItemRecursoLayer item : layers) {
			if (item.isRecursar()) {
				Assert.isNotNull(item.getJustificativa(), "A justificativa de recurso deve ser preenchida.");
				itensRecursados.add(item);
			}
		}
		
		return itensRecursados;
	}
	
	private void validate(GuiaCompleta guia) {
		List<ItemRecursoLayer> iRL = new ArrayList<ItemRecursoLayer>();
		if (guia.getItemRecurso() != null && guia.getItemRecurso().getSituacao().getDescricao().equals(SituacaoEnum.INDEFERIDO.descricao())) {
			Assert.fail("Esta guia não possui itens recursáveis. Aguarde o deferimento/indeferimento do recurso.");
		} else {
			if (guia.getLayersRecursoItensDiaria()!= null
					&& guia.getLayersRecursoItensGasoterapia() != null
					&& guia.getLayersRecursoItensPacote() != null
					&& guia.getLayersRecursoItensTaxa() != null
					&& guia.getLayersRecursoProcedimentoCirurgico() != null
					&& guia.getLayersRecursoProcedimentosExame() != null
					&& guia.getLayersRecursoProcedimentosOutros() != null
					&& guia.getLayerRecursoGuia() != null) {
				iRL.addAll(guia.getLayerRecursoGuia());
				iRL.addAll(guia.getLayersRecursoItensDiaria());
				iRL.addAll(guia.getLayersRecursoItensGasoterapia());
				iRL.addAll(guia.getLayersRecursoItensPacote());
				iRL.addAll(guia.getLayersRecursoItensTaxa());
				iRL.addAll(guia.getLayersRecursoProcedimentoCirurgico());
				iRL.addAll(guia.getLayersRecursoProcedimentosExame());
				iRL.addAll(guia.getLayersRecursoProcedimentosOutros());
				Assert.isNotEmpty(iRL, "Esta guia não possui nenhum item glosado para recursar.");
			}
		}
	}
}