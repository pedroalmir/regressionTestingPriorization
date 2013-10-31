package br.com.infowaypi.ecare.services;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecare.cadastros.MotivoGlosa;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaAcompanhamentoAnestesico;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoAnestesico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.ecarebc.utils.ProcedimentoUtils;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.molecular.parameter.OrderBy;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

@SuppressWarnings("unchecked")
public class AuditarAcompanhamentoAnestesico extends Service {

	public ResumoGuias<GuiaAcompanhamentoAnestesico> buscarGuias(String autorizacao, String dataInicial, String dataFinal, Prestador prestador) throws Exception {
		Collection situacoes = Arrays.asList(SituacaoEnum.AUDITADO.descricao(), SituacaoEnum.RECEBIDO.descricao());
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new In("situacao.descricao",situacoes));
		sa.addParameter(new OrderBy("dataTerminoAtendimento"));
		List<GuiaAcompanhamentoAnestesico> guias = super.buscarGuias(sa, autorizacao, null, dataInicial, dataFinal, prestador, true, false, GuiaAcompanhamentoAnestesico.class, GuiaSimples.DATA_DE_TERMINO);
		
		ResumoGuias<GuiaAcompanhamentoAnestesico> resumo = new ResumoGuias<GuiaAcompanhamentoAnestesico>(guias, ResumoGuias.SITUACAO_TODAS, false);
		Assert.isNotEmpty(resumo.getGuias(), MensagemErroEnum.GUIAS_NAO_ENCONTRADAS_PARA_AUDITORIA.getMessage());

		return resumo; 
	}
	
	public GuiaAcompanhamentoAnestesico selecionarGuia(GuiaAcompanhamentoAnestesico guia, UsuarioInterface usuario) {
		Set<ProcedimentoInterface> procedimentos = guia.getProcedimentos();
		for (ProcedimentoInterface procedimento : procedimentos) {
			procedimento.tocarObjetos();
		}
		tocarObjetosPrestador(guia.getPrestador());
		guia.getSegurado().getConsumoIndividual().getConsumos().size();
		guia.tocarObjetos();
		guia.setUsuarioDoFluxo(usuario);
		
		return guia;
	}

	private void tocarObjetosPrestador(Prestador prestador) {
		prestador.getAcordosCBHPM().size();
		prestador.getConsumoIndividual().getConsumos().size();
	}
	
	public GuiaAcompanhamentoAnestesico auditarGuia(Boolean glosar, MotivoGlosa motivoDeGlosa, GuiaAcompanhamentoAnestesico guia, Collection<ProcedimentoAnestesico> procedimentosInseridos,  UsuarioInterface usuario) throws Exception {
		Date dataSituacao = new Date();
		if (glosar) {
			Assert.isNotNull(motivoDeGlosa, MensagemErroEnum.CAMPO_MOTIVO_DE_GLOSA_REQUERIDO.getMessage());
			this.glosarGuiaEProcedimentos(guia, usuario, motivoDeGlosa.getDescricao(), dataSituacao);
		} else {
			Set<ProcedimentoInterface> procedimentos = guia.getProcedimentosNaoGlosadosNemCanceladosNemNegados();
			Set<ProcedimentoInterface> procedimentosAnestesicos = new HashSet<ProcedimentoInterface>(procedimentosInseridos);
			
			this.validaGlosaTotalDaGuia(procedimentosAnestesicos , procedimentos);
			
			guia.addAllProcedimentos(procedimentosInseridos);
			procedimentos = guia.getProcedimentosNaoGlosadosNemCanceladosNemNegados();
			
			ProcedimentoUtils.ajustarAPorcentagemDosProcedimentosDeAcordoComOPorte(guia);
			
			this.mudarSituacaoDosProcedimentos(usuario, dataSituacao, procedimentos);
			
			guia.mudarSituacao(usuario, SituacaoEnum.AUDITADO.descricao(), MotivoEnum.GUIA_AUDITADA.getMessage(), dataSituacao);
		}
		
		guia.recalcularValores();
		guia.updateValorCoparticipacao();
		
		return guia;
	}
	
	private void glosarGuiaEProcedimentos(GuiaAcompanhamentoAnestesico guia, UsuarioInterface usuario, String motivoDeGlosa, Date dataSituacao){
		guia.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), motivoDeGlosa, dataSituacao);
		for (ProcedimentoInterface procedimento : (Set<ProcedimentoInterface>)guia.getProcedimentosNaoGlosadosNemCanceladosNemNegados()) {
			procedimento.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), MotivoEnum.GUIA_GLOSADA_DURANTE_AUDITORIA.getMessage(), dataSituacao);
		}
	}

	/**	Caso todos os procedimentos contidos dentro da guia tenham sido marcados para serem glosados, o sistema solicita que a guia seja glosada.
	 * @param procedimentosAnestesicos
	 * @param procedimentos
	 * @throws ValidateException
	 */
	private void validaGlosaTotalDaGuia(Collection<ProcedimentoInterface> procedimentosAnestesicos, Set<ProcedimentoInterface> procedimentos) throws ValidateException {
		boolean isTodosOsProcedimentosMarcadosParaGlosa = isTodosOsProcedimentosMarcadosParaGlosa(procedimentos);
		boolean isSemProcedimentosNovos = procedimentosAnestesicos.isEmpty();
		
		if(isTodosOsProcedimentosMarcadosParaGlosa && isSemProcedimentosNovos){
			throw new ValidateException(MensagemErroEnum.GUIA_DEVE_SER_GLOSADA_TOTALMENTE.getMessage());
		}
	}

	/**
	 * @param procedimentos
	 */
	private boolean isTodosOsProcedimentosMarcadosParaGlosa(Set<ProcedimentoInterface> procedimentos) {
		for(ProcedimentoInterface procedimento: procedimentos){
			if(!procedimento.isGlosar()){
				return false;
			}
			
		}
		return true;
	}

	private void mudarSituacaoDosProcedimentos(UsuarioInterface usuario, Date dataSituacao, Set<ProcedimentoInterface> procedimentos) {
		for (ProcedimentoInterface procedimento : procedimentos) {
			if (procedimento.isGlosar()) {
				String motivoGlosaProcedimento = procedimento.getMotivoGlosaProcedimento().getDescricao();
				Assert.isNotEmpty(motivoGlosaProcedimento, MensagemErroEnum.PROCEDIMENTO_GLOSADO_SEM_MOTIVO_DE_GLOSA.getMessage(procedimento.getProcedimentoDaTabelaCBHPM().getCodigo()));
				procedimento.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), motivoGlosaProcedimento, dataSituacao);
			} else {
				procedimento.mudarSituacao(usuario, SituacaoEnum.AUDITADO.descricao(), MotivoEnum.PROCEDIMENTO_AUDITADO.getMessage(), dataSituacao);
			}
		}
	}
	
	public void salvarGuia(GuiaAcompanhamentoAnestesico guia) throws Exception {
		super.salvarGuia(guia);
	}
	
}
