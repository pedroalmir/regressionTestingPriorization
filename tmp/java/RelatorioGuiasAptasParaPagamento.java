package br.com.infowaypi.ecare.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.quartz.xml.ValidationException;

import br.com.infowaypi.ecare.enums.SituacaoGuiaEnum;
import br.com.infowaypi.ecare.enums.TipoGuiaEnum;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuiasAptasParaPagamento;
import br.com.infowaypi.ecarebc.utils.CompetenciaUtils;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.molecular.parameter.LowerEquals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.CollectionUtils;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe responsável pela busca das guias que estão aptas para pagamento.
 * @changes Luciano Rocha
 * @since 25/12/2012
 * @param <G>
 */
@SuppressWarnings("rawtypes")
public class RelatorioGuiasAptasParaPagamento<G extends GuiaSimples> {
	
	public ResumoGuiasAptasParaPagamento<G> gerarRelatorio(String competencia, Date dataRecebimento, Prestador prestador, TipoGuiaEnum tipoGuia, SituacaoGuiaEnum situacaoGuia) throws Exception {
		
//		Assert.isNotEmpty(competencia, "A competência deve ser informada.");
		
		String[] tipos = null;
		
		if (tipoGuia.getTipo().equals(TipoGuiaEnum.TODOS.getTipo())) {
			tipos = new String[]{TipoGuiaEnum.ATENDIMENTO_SUBSEQUENTE.getTipo(),TipoGuiaEnum.CONSULTA_URGENCIA.getTipo(),
					TipoGuiaEnum.EXAME.getTipo(),TipoGuiaEnum.INTERNACAO_ELETIVA.getTipo(),TipoGuiaEnum.INTERNACAO_URGENCIA.getTipo(),
					TipoGuiaEnum.HONORARIO_MEDICO.getTipo()};
		} else {
			tipos = new String[]{tipoGuia.getTipo()};
		}
		
		String[] situacoes = null;
		
		if (situacaoGuia.getDescricao().equals(SituacaoGuiaEnum.TODAS.getDescricao())) {
			situacoes = new String[]{SituacaoGuiaEnum.CONFIRMADO.getDescricao(),SituacaoGuiaEnum.AUDITADO.getDescricao(),SituacaoGuiaEnum.RECEBIDO.getDescricao()};
		} else {
			situacoes = new String[]{SituacaoGuiaEnum.CONFIRMADO.getDescricao(),situacaoGuia.getDescricao()};
		}
		
		ResumoGuiasAptasParaPagamento<G> resumo;
		
		SearchAgent sa = new SearchAgent();
		
		if (prestador == null) {
			List<G> guiasAptas = buscaGuiasAptas(competencia, tipos, situacoes, sa, dataRecebimento);
			
			Map<Prestador, Set<G>> mapaPrestador = CollectionUtils.groupBy(guiasAptas, "prestador", Prestador.class);
			
			resumo = new ResumoGuiasAptasParaPagamento<G>(competencia, mapaPrestador);
		} else {
			sa.addParameter(new Equals("prestador", prestador));
			List<G> guiasAptas = buscaGuiasAptas(competencia, tipos, situacoes, sa, dataRecebimento);
			
			resumo = new ResumoGuiasAptasParaPagamento<G>(competencia, prestador, guiasAptas);
		}
		
		return resumo;
	}

	/**
	 * Método que encapsula a busca de todas as guias aptas para pagamento.
	 * @author Luciano Rocha
	 * @param competencia
	 * @param tipos
	 * @param situacoes
	 * @param sa
	 * @return
	 * @throws ValidateException
	 * @throws ValidationException 
	 */
	private List<G> buscaGuiasAptas(String competencia, String[] tipos,
			String[] situacoes, SearchAgent sa, Date dataRecebimento) throws ValidateException {
		
		sa.addParameter(new In("situacao.descricao", Arrays.asList(situacoes)));
		sa.addParameter(new In("tipoDeGuia", Arrays.asList(tipos)));
		
		if(dataRecebimento == null){
			Date comp = Utils.gerarCompetencia(competencia);
			sa.addParameter(new GreaterEquals("dataTerminoAtendimento", CompetenciaUtils.getInicioCompetencia(comp)));
			sa.addParameter(new LowerEquals("dataTerminoAtendimento", CompetenciaUtils.getFimCompetencia(comp)));
		}
		else {
			sa.addParameter(new Equals("dataRecebimento", dataRecebimento));
		}
		
		List<G> guias = sa.list(GuiaSimples.class);
		
		if (guias == null || guias.size() < 1) {
			throw new ValidateException("Nenhuma guia foi encontrada!");
		}
		
		return guias;
	}
}
