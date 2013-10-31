package br.com.infowaypi.ecare.services.auditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.RegistroTecnicoDaAuditoria;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.NotIn;
import br.com.infowaypi.molecular.parameter.OrderBy;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Classe responsável pela inserção de observações de auditores externos em guias de internação.<br>
 * Essas observações, aqui denominadas <i>Registros Técnicos</i>, só poderão ser visualizadas pelos roles
 * <b>auditor</b>, <b>faturista</b> e <b>root</b>.
 * 
 * @author patricia
 * @version
 * @see br.com.infowaypi.ecarebc.atendimentos.RegistroTecnicoDaAuditoria
 * 
 */
public class InserirRegistroTecnicoDaAuditoriaService extends Service {
	
	/**
	 * Método responsável por encontrar a(s) guia(s) de internação para a qual será
	 * inserida um novo registro técnico.
	 * Retorna um conjunto de guias, do qual uma delas deverá ser seleciona no passo seguinte.
	 * @param autorizacao
	 * @return ResumoGuias<GuiaInternacao>
	 * @throws Exception
	 */
	public ResumoGuias<GuiaInternacao> buscarGuias(String autorizacao, String dataInicial, String dataFinal,Prestador prestador) throws Exception {
		List<String> situacoes = Arrays.asList(
				 SituacaoEnum.SOLICITADO_INTERNACAO.descricao(),
				 SituacaoEnum.NAO_AUTORIZADO.descricao(),
				 SituacaoEnum.AUTORIZADO.descricao(),
				 SituacaoEnum.CANCELADO.descricao(),
				 SituacaoEnum.INAPTO.descricao(),
				 SituacaoEnum.GLOSADO.descricao(),
				 SituacaoEnum.FATURADA.descricao(),
				 SituacaoEnum.PAGO.descricao());
		
		List<GuiaInternacao> guias = new ArrayList<GuiaInternacao>();
		SearchAgent sa = new SearchAgent();
		
		sa.addParameter(new NotIn("situacao.descricao",situacoes));
		sa.addParameter(new OrderBy("dataAtendimento"));
		guias = super.buscarGuias(sa, autorizacao, null, dataInicial, dataFinal, prestador, true, false, GuiaInternacao.class, GuiaSimples.DATA_DE_ATENDIMENTO);
		
		
		ResumoGuias<GuiaInternacao> resumo = new ResumoGuias<GuiaInternacao>(guias, ResumoGuias.SITUACAO_TODAS,false);
		Assert.isNotEmpty(resumo.getGuias(), MensagemErroEnum.NENHUMA_GUIA_ENCONTRADA.getMessage());

		return resumo; 
	}
	
	public GuiaInternacao selecionarGuia(GuiaInternacao guia, UsuarioInterface usuario) {
		guia.tocarObjetos();
		guia.setUsuarioDoFluxo(usuario);
		guia.setValorAnterior(guia.getValorTotal());
		//p/poder comparar, no passo seguinte, se os campos mudaram
		for (RegistroTecnicoDaAuditoria rta : guia.getRegistrosTecnicosDaAuditoriaDoUsuarioDoFluxo()) {
			rta.setStringsAntesDeAlgumaAlteracao();
		}
		
		return guia;
	}
	
	/**
	 * Método que adiciona um conjunto de registros técnicos na guia de internação e para cada
	 * registro tecnico desse conjunto, ajusta-o como registro tecnico vindo do auditor externo.
	 * 
	 * @param usuario
	 * @param guia
	 * @param registros
	 * @return GuiaInternacao
	 */
	public GuiaInternacao gravarRegistrosTecnicos(UsuarioInterface usuario, Collection<RegistroTecnicoDaAuditoria> registros, GuiaInternacao guia) {
		guia.tocarObjetos();
		
		guia.addRegistrosTecnicosDaAuditoria(registros, usuario);
		
		for (RegistroTecnicoDaAuditoria rta : guia.getRegistrosTecnicosDaAuditoriaDoUsuarioDoFluxo()) {
			if(rta.getIdObservacao() == null)
				rta.setFromAuditorExterno(true);
			if (rta.isAlterouStrings())
				rta.setDataAlteracao(new Date());
		}
		return guia;
	}
	
	/**
	 * Responsável por salvar na base de dados as alterações realizadas na guia.
	 * @param guia
	 * @throws Exception
	 */
	public void salvarGuia(GuiaInternacao guia) throws Exception{
		ImplDAO.save(guia);
	}
	
	/**
	 * Método sem implementação, criado apenas para permitir a visualização da guia ao final da execução do fluxo.
	 */
	public void finalizar(){}
}
