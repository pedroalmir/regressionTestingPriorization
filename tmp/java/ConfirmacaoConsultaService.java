package br.com.infowaypi.ecarebc.service.consultas;

import static br.com.infowaypi.msr.utils.Assert.isNotEmpty;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsulta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.atendimentos.enums.ValidateGuiaEnum;
import br.com.infowaypi.ecarebc.atendimentos.validators.ServiceWithValidator;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Service para a confirmação de uma guia de consulta do plano de saúde 
 * @author root
 */
@SuppressWarnings("rawtypes")
public class ConfirmacaoConsultaService<S extends SeguradoInterface> extends Service implements ServiceWithValidator{
	
	public ConfirmacaoConsultaService(){
		super();
	}
	
	public ResumoGuias<GuiaConsulta> buscarGuiasConfirmacao(S segurado, String dataInicial, String dataFinal, Prestador prestador) throws Exception{
		List<GuiaConsulta> guias = super.buscarGuias(dataInicial, dataFinal,segurado, prestador,true, GuiaConsulta.class, SituacaoEnum.AGENDADA);
		isNotEmpty(guias, MensagemErroEnum.GUIAS_NAO_ENCONTRADAS_PARA_O_SEGURADO.getMessage("confirmadas"));
		return new ResumoGuias<GuiaConsulta>(guias, ResumoGuias.SITUACAO_TODAS, false);
	}
 
	public ResumoGuias<GuiaConsulta> buscarGuiasConfirmacao(Collection<S> segurados, Prestador prestador) throws Exception{
		SearchAgent sa = getSearchSituacoes(SituacaoEnum.AGENDADA);
		List<GuiaConsulta> guias = super.buscarGuias(sa,segurados, prestador,true, GuiaConsulta.class);
		isNotEmpty(guias, MensagemErroEnum.GUIAS_NAO_ENCONTRADAS_PARA_O_SEGURADO.getMessage("confirmadas"));
		return new ResumoGuias<GuiaConsulta>(guias, ResumoGuias.SITUACAO_TODAS, false);
	} 
	
	public GuiaConsulta buscarGuiasConfirmacao(String autorizacao, Prestador prestador) throws Exception{
		if (Utils.isStringVazia(autorizacao))
			throw new ValidateException(MensagemErroEnum.NUMERO_AUTORIZACAO_INVALIDO.getMessage());
		GuiaConsulta guia = super.buscarGuias(autorizacao, prestador,true, GuiaConsulta.class, SituacaoEnum.AGENDADA);
		Assert.isNotNull(guia, MensagemErroEnum.GUIA_NAO_ENCONTRADA.getMessage(autorizacao));
		guia.tocarObjetos();
		return guia;
	}
	
	public void confirmarGuiaDeConsulta(GuiaConsulta<Procedimento> guia, UsuarioInterface usuario) throws Exception {
		
		this.addValidators(guia);

		if(usuario.isPrestador()) {
			guia.getSegurado().decrementarLimites(guia);
			guia.setDataAtendimento(new Date()); 
			guia.getSegurado().incrementarLimites(guia);
		}
		
		guia.setValorAnterior(guia.getValorTotal());
		
		String motivo = usuario.isPrestador() ? MotivoEnum.CONFIRMADA_NO_PRESTADOR.getMessage() :	MotivoEnum.CONFIRMADA_NO_LANCAMENTO_MANUAL.getMessage();
		guia.mudarSituacao(usuario, SituacaoEnum.CONFIRMADO.descricao(), motivo, new Date());
		
		for (ProcedimentoInterface procedimento : guia.getProcedimentos()) {
			procedimento.mudarSituacao(usuario, SituacaoEnum.CONFIRMADO.descricao(), motivo, new Date());
		}
		
		guia.recalcularValores();
		super.salvarGuia(guia);
	}
	
	protected <G extends GuiaSimples> G selecionarGuia(G guia) throws Exception {
		return super.selecionarGuia(guia);
	}
	
	public void finalizar(GuiaConsulta guia) {
		super.finalizar(guia);
	}
	public void salvarGuia(GuiaSimples guia) throws Exception {
		guia.recalcularValores();
		super.salvarGuia(guia);
	}
	
	/**
	 * Método responsável por adicionar validações específicas em um fluxo. Você pode utilizá-lo de duas formas:
	 * 
	 * - Adicionar as validações para serem executadas em conjunto com os outros validates, ou seja, somente chamar o addFlowValidator() da guia;
	 * - Adicionar as validações do fluxo e executá-las somente. Caso abaixo.
	 * @throws Exception 
	 */
	@Override
	public void addValidators(GuiaSimples guia) throws Exception {
		guia.addFlowValidator(ValidateGuiaEnum.SEGURADO_VALIDATOR.getValidator())
			.addFlowValidator(ValidateGuiaEnum.PRESTADOR_VALIDATOR.getValidator())
			.addFlowValidator(ValidateGuiaEnum.PROFISSIONAL_VALIDATOR.getValidator())
			.addFlowValidator(ValidateGuiaEnum.DATA_GUIA_VALIDATOR.getValidator())
			.addFlowValidator(ValidateGuiaEnum.CONSULTA_PERIODICIDADE_VALIDATOR.getValidator()).executeFlowValidators();
	}
	
}
