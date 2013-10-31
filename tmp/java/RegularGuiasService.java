package br.com.infowaypi.ecare.services;

import static br.com.infowaypi.msr.utils.Assert.isNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecare.atendimentos.acordos.itensAcordos.ItemDiariaAuditoria;
import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.enums.TipoLiberacaoForaLimiteEnum;
import br.com.infowaypi.ecare.exceptions.AutorizacaoExameException;
import br.com.infowaypi.ecare.services.auditor.AutorizarExamesEletivosService;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Prestador.TipoIncremento;
import br.com.infowaypi.ecarebc.atendimentos.Critica;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.Observacao;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.atendimentos.enums.TipoConsultaEnum;
import br.com.infowaypi.ecarebc.atendimentos.enums.TipoCriticaEnum;
import br.com.infowaypi.ecarebc.atendimentos.validators.GuiaProcedimentoPorcentagemValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.ManagerCritica;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MensagemInformacaoEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.exceptions.ConsumoException;
import br.com.infowaypi.ecarebc.opme.ItemOpme;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOutros;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.ecarebc.utils.ProcedimentoUtils;
import br.com.infowaypi.ecarebc.utils.TocarObjetoUtils;
import br.com.infowaypi.ecarebc.utils.TocarObjetoUtils.TocarObjetoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.molecular.parameter.Like;
import br.com.infowaypi.molecular.parameter.LowerEquals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

/*if[VISITA_AUTOMATICA_POR_DIARIA]
import br.com.infowaypi.ecarebc.consumo.ColecaoConsumosInterface;
import br.com.infowaypi.ecarebc.consumo.ConsumoInterface;
import br.com.infowaypi.ecarebc.consumo.periodos.Periodo;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.msr.situations.ComponenteColecaoSituacoes;
import br.com.infowaypi.msr.user.Usuario;
end[VISITA_AUTOMATICA_POR_DIARIA] */

/**
 * Unifica��o dos fluxos de autoriza��o de Interna��o, Prorroga��o, Exames/Procedimentos/Pacotes especiais 
 * e Exames eletivos
 * @author Leonardo Sampaio
 * @since 13/07/2012
 * @changes Luciano Rocha: Add OPMEs no fluxo.
 */
@SuppressWarnings("rawtypes")
public class RegularGuiasService {

    private static final int PERIODO_PARA_ZERAR_CONSULTA_DE_URGENCIA = 12;
    private static int NIVEL_DE_ACESSO_MAXIMO_ROLE_REGULADOR  = 3;
    private static int NIVEL_DE_ACESSO_MAXIMO_ROLE_DIRETORIA_MEDICA_E_ROOT  = 4;
    
    /**
     * Busca guias de Interna��o, Prorroga��o, Exames e
     * Exames/Pacotes/Procedimentos especiais.
     * 
     * @param autorizacao
     *            n�mero de autoriza��o da guia
     * @param dataInicial
     *            limite inferior da busca por data de marca��o
     * @param dataFinal
     *            limite superior da busca por data de marca��o
     * @param prestador
     *            prestador respons�vel pela guia
     * @param usuario
     *            usu�rio da sess�o
     * @return guias encontradas
     * @throws Exception
     *             erros
     * @since 13/07/2012
     * @author Leonardo Sampaio
     */
    public <G extends GuiaCompleta> ResumoGuias buscarGuias(String autorizacao,	Date dataInicial, Date dataFinal, Prestador prestador, UsuarioInterface usuario) throws Exception {

		Set<GuiaCompleta> guias = new HashSet<GuiaCompleta>();
	
		guias.addAll(buscarGuiasExamesProcedimentosPacotesEspeciaisOPMEs(autorizacao,dataInicial, dataFinal, prestador));
		guias.addAll(buscarGuiasComSolicitacaoDeInternacao(autorizacao,	dataInicial, dataFinal, prestador));
		guias.addAll(buscarGuiasComSolicitacaoDeProrrogacao(autorizacao, dataInicial, dataFinal, prestador, usuario));
	
		Assert.isNotEmpty(guias, MensagemErroEnum.NENHUMA_GUIA_ENCONTRADA.getMessage());
		ResumoGuias<GuiaCompleta> resumo = new ResumoGuias<GuiaCompleta>(new ArrayList<GuiaCompleta>(guias), ResumoGuias.SITUACAO_TODAS, false);
	
		return resumo;

    }

    /**
     * Esse m�todo valida se o usuario possui n�vel de acesso para liberar os
     * exames da guia e se o beneficiario tem limite para exames estourado.
     * 
     * @param guia
     *            guia selecionada
     * @param usuario
     *            usuario logado
     * @return retorna a guia de exame que foi selecionada
     * @throws Exception
     *             erro
     * @author Leonardo Sampaio
     * @since 13/07/2012
     */
    public GuiaCompleta<ProcedimentoInterface> selecionarGuia(GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario) throws Exception {

		Assert.isNotNull(guia, MensagemErroEnum.NENHUMA_GUIA_SELECIONADA.getMessage());
		    
		if (guia.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())){
		    throw new RuntimeException(MensagemErroEnumSR.GUIA_CANCELADA.getMessage());
		}
	
		// internacoes
	
		guia.getSegurado().getConsultasPromocionais().size();
	
		if (guia.getGuiaOrigem() != null) {
		    guia.getGuiaOrigem().getProcedimentos();
		}
	
		guia.setUsuarioDoFluxo(usuario);
	
		// se guia � de interna��o, deve ser preenchida essa lista auxiliar (procedimentosCirurgicosTemp)
		//if (guia.getSituacao().getDescricao().equals(SituacaoEnum.SOLICITADO_INTERNACAO.descricao())) { //Forma antiga
		if (guia.isInternacao()) {
		    guia.getProcedimentosCirurgicosTemp().addAll(guia.getProcedimentosCirurgicosSolicitados());
		}
	
		// exames
	
		validaNivelDeAcesso(guia, usuario);
	
		try {
		    guia.getSegurado().temLimite(new Date(), guia);
		} catch (ConsumoException e) { // mensagens de limite
		    guia.setMensagemLimite(e.getMessage());
		    guia.setLiberadaForaDoLimite(TipoLiberacaoForaLimiteEnum.LIBERACAO_AUDITOR.codigo());
		}
	
		filtrarCriticasApresentaveis(guia);
	
		guia.tocarObjetos();
		
		preencheQuantidadeLayer(guia);
		
		return guia;
    }

    /**
     * Realiza valida��es sobre as autoriza��es da guia.
     * 
     * @param guia guia selecionada
     * @param usuario usu�rio da sess�o
     * @return guia autorizada
     * @throws Exception erros
     * @since 17/07/2012
     * @author Leonardo Sampaio
     */
    public GuiaCompleta<ProcedimentoInterface> autorizarGuia(GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario) throws Exception {

		Set<ProcedimentoInterface> examesAutorizados = 		 guia.getExamesAutorizados(); // m�todos em GuiaSimples
		Set<ProcedimentoInterface> cirurgiasAutorizados = 	 guia.getCirurgiasAutorizados();
		Set<ProcedimentoInterface> procedimentosNaoAutorizados = guia.getProcedimentosAutorizacaoNegada();
		Set<ItemPacote> pacotesAutorizados = 			 guia.getItensPacoteAutorizadosEmAutorizacao();
		Set<ItemPacote> pacotesNegados = 			 guia.getItensPacoteNegadosEmAutorizacao();
		Set<ItemOpme> opmesAutorizados = guia.getItensOpmeAutorizadosEmAutorizacao();
		Set<ItemOpme> opmesNegados = guia.getItensOpmeNaoAutorizadosEmAutorizacao();
		
		if (guia.isExame() && guia.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())){
		    Set<ProcedimentoInterface> procedimentosSolicitados = new HashSet<ProcedimentoInterface>(guia.getProcedimentosSolicitados());
		    
		    procedimentosSolicitados.removeAll(examesAutorizados);
		    procedimentosSolicitados.removeAll(cirurgiasAutorizados);
		    procedimentosSolicitados.removeAll(procedimentosNaoAutorizados);
	
		    if (!procedimentosSolicitados.isEmpty()) {
		    	throw new RuntimeException("Caro Auditor(a), todos os procedimentos dessa guia devem ser AUTORIZADOS ou N�O AUTORIZADOS.");
		    }
	
		    boolean isPossuiAlgumProcedimentoAutorizado = !cirurgiasAutorizados.isEmpty() || !examesAutorizados.isEmpty();
	
		    if (isPossuiAlgumProcedimentoAutorizado) {
		    	guia.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), "Autoriza��o de procedimentos especiais", new Date());
		    } else {
		    	guia.mudarSituacao(usuario, SituacaoEnum.NAO_AUTORIZADO.descricao(), "Nenhum procedimento foi autorizado", new Date());
		    }
		}
	
		if (guia.isInternacao() || guia.isAtendimentoUrgencia() || guia.isConsultaUrgencia()) {
		    if (!cirurgiasAutorizados.isEmpty()) {
		    	GuiaProcedimentoPorcentagemValidator
		    		.verificaPercentualDeReducao(cirurgiasAutorizados,((GuiaCompleta<ProcedimentoInterface>)guia)
		    				.isPossuiProcedimentoAutorizadoA100Porcento());
		    }
		}
	
		for (ProcedimentoInterface procedimento : cirurgiasAutorizados) {
		    Assert.isNotNull(procedimento.getMotivo(), "O motivo de autoriza��o deve ser informado.");
		    procedimento.mudarSituacao(guia.getUsuarioDoFluxo(), SituacaoEnum.AUTORIZADO.descricao(), procedimento.getMotivo(), new Date());
		}
	
		for (ProcedimentoInterface procedimento : examesAutorizados) {
		    Assert.isNotNull(procedimento.getMotivo(), "O motivo de autoriza��o deve ser informado para EXAMES.");
		    procedimento.mudarSituacao(guia.getUsuarioDoFluxo(), SituacaoEnum.AUTORIZADO.descricao(), procedimento.getMotivo(), new Date());
		}
	
		for (ProcedimentoInterface procedimento : procedimentosNaoAutorizados) {
		    Assert.isNotNull(procedimento.getMotivo(), "O motivo da n�o autoriza��o deve ser informado.");
		    procedimento.mudarSituacao(guia.getUsuarioDoFluxo(), SituacaoEnum.NAO_AUTORIZADO.descricao(), procedimento.getMotivo(), new Date());
		    guia.getSegurado().atualizarLimites(guia, TipoIncremento.NEGATIVO, procedimento.getQuantidade());
		}
	
		for (ItemPacote itemPacote : pacotesAutorizados) {
			Assert.isNotNull(itemPacote.getObservacaoRegulacao(), "O motivo de autoriza��o deve ser informado para PACOTES.");
		    itemPacote.mudarSituacao(guia.getUsuarioDoFluxo(), SituacaoEnum.AUTORIZADO.descricao(),MotivoEnum.AUTORIZADO_PELO_AUDITOR.getMessage(), new Date());
		}
	
		for (ItemPacote itemPacote : pacotesNegados) {
			Assert.isNotNull(itemPacote.getObservacaoRegulacao(), "O motivo da n�o autoriza��o deve ser informado para PACOTES.");
		    itemPacote.mudarSituacao(guia.getUsuarioDoFluxo(), SituacaoEnum.NAO_AUTORIZADO.descricao(), MotivoEnum.NAO_AUTORIZADO_AUDITOR.getMessage(), new Date());
		}
	
		for (ItemOpme itemOpme : opmesAutorizados) {
			Assert.isNotNull(itemOpme.getFornecedor(), "O Fornecedor deve ser informado para OPMEs.");
			Assert.isNotNull(itemOpme.getObservacaoRegulacao(), "O motivo de autoriza��o deve ser informado para OPMEs.");
		    itemOpme.mudarSituacao(guia.getUsuarioDoFluxo(), SituacaoEnum.AUTORIZADO.descricao(), itemOpme.getObservacaoRegulacao(), new Date());
		}
	
		for (ItemOpme itemOpme : opmesNegados) {
			Assert.isNotNull(itemOpme.getObservacaoRegulacao(), "O motivo da n�o autoriza��o deve ser informado para OPMEs.");
			itemOpme.mudarSituacao(guia.getUsuarioDoFluxo(), SituacaoEnum.NAO_AUTORIZADO.descricao(), itemOpme.getObservacaoRegulacao(), new Date());
		}
	
		if (guia.isInternacao()) {
		    ProcedimentoUtils.aplicaDescontoDaViaDeAcesso(guia);
		}
	
		guia.recalcularValores();
	
		ManagerCritica.processaSituacao(guia);
		
		return guia;
    }

    /**
     * M�todo que verifica a altera��o em di�rias.
     * Regra:
     * Se houver mais di�rias que o solicitado e
     * @author Luciano Infoway
     * @hardchanges Wislanildo
     * @since 20/05/2013
     * @param guia
     * @throws ValidateException 
     */
	private void processarDiariasSolicitadas(GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario) throws ValidateException {
		for (ItemDiaria itemDiaria : guia.getItensDiaria(SituacaoEnum.SOLICITADO.descricao())) {
			int quantidadeAtual = itemDiaria.getValor().getQuantidade();
			int quantidadeAutorizada = itemDiaria.getQuantidadeAutorizadaTransient();
			boolean foiAutorizadoMaisDiariasDoQueSolicitado = quantidadeAtual < quantidadeAutorizada;
			
	    	if(itemDiaria.isAutorizado()){
	    		if(quantidadeAutorizada == 0){
	    			itemDiaria.mudarSituacao(usuario, SituacaoEnum.NAO_AUTORIZADO.descricao(), MotivoEnum.NAO_AUTORIZACAO_SOLICITACAO_PRORROGACAO.getMessage(), new Date());
	    			itemDiaria.setDataInicial(null);
	    		}else{
	    			itemDiaria.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZACAO_SOLICITACAO_PRORROGACAO.getMessage(), new Date());
	    			
	    			if (foiAutorizadoMaisDiariasDoQueSolicitado){
						Critica critica = new Critica();
						critica.setMensagem("Foram autorizadas mais di�rias que o solicitado.");
						critica.setGuia(guia);
						guia.getCriticas().add(critica);
					}
	    		}
			} else {
			    itemDiaria.mudarSituacao(usuario, SituacaoEnum.NAO_AUTORIZADO.descricao(), MotivoEnum.NAO_AUTORIZACAO_SOLICITACAO_PRORROGACAO.getMessage(), new Date());
			    itemDiaria.setDataInicial(null);
			}
	    	itemDiaria.setQuantidadeAutorizada(itemDiaria.getQuantidadeAutorizadaTransient());
			itemDiaria.getValor().setQuantidade(itemDiaria.getQuantidadeAutorizadaTransient());
	    }
		
	}


	/**
     * M�todo que prenche o layer da quantidade de di�rias.
     * @author Luciano Infoway
     * @param guia
     */
	private void preencheQuantidadeLayer(GuiaCompleta<ProcedimentoInterface> guia) {
		for (ItemDiaria diaria : guia.getItensDiaria()) {
			diaria.setQuantidadeAutorizadaTransient(diaria.getValor().getQuantidade());
		}
	}

    /**
     * Realiza valida��es sobre as autoriza��es da guia.
     * 
     * @param guia guia selecionada
     * @param usuario usu�rio da sess�o
     * @param observacao observa��o inserida em caso de Interna��o Eletiva ou de Urg�ncia
     * @return guia autorizada
     * @throws Exception erros
     * @since 17/07/2012
     * @author Leonardo Sampaio
     */
    public GuiaCompleta<ProcedimentoInterface> autorizarGuia (String observacao, GuiaCompleta<ProcedimentoInterface> guia, Collection<ItemDiariaAuditoria> diariasInseridas, UsuarioInterface usuario) throws Exception {

		guia.setValorAnterior(guia.getValorTotal());
	
		boolean isAutorizado = guia.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao());
		boolean isNaoAutorizado = guia.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao());
		boolean isAberto = guia.isSituacaoAtual(SituacaoEnum.ABERTO.descricao());
	
		if (!isAutorizado && !isNaoAutorizado && !isAberto){
			throw new RuntimeException("Prezado usu�rio, por favor informe se a interna��o ser� autorizada ou n�o.");
		}
	
		if (isNaoAutorizado){
		    if (Utils.isStringVazia(observacao)){
		    	removerSituacao(guia);
		    	throw new ValidateException("Deve ser informado uma justificativa para a n�o autoriza��o da guia.");
		    }
		}
		
		int count = 0;
		boolean isAutorizadoOuAberto = isAutorizado || isAberto;
	
		if (isAutorizadoOuAberto) {
		    for (ProcedimentoCirurgicoInterface procedimento : guia.getProcedimentosCirurgicosTemp()) {
		        Assert.isNotNull(procedimento.getMotivo(), "O motivo da autoriza��o para procedimentos deve ser informado.");

		        boolean porcentagem100 = procedimento.getPorcentagem().compareTo(Constantes.PORCENTAGEM_100) == 0;
				boolean passouPelaAutorizacao =	procedimento.isPassouPelaAutorizacao();
		
				if (passouPelaAutorizacao && porcentagem100) {
				    count++;
				    if (count > 1) {
						removerSituacao(guia);
						throw new RuntimeException(
							"Caro usu�rio, n�o � permitido autoriza��o de mais de um procedimento cirurgico 100%");
				    }
				}
				if (guia.containsProcedimentosCirurgicosIguaisEComMesmaPorcentagem(procedimento)) {
				    removerSituacao(guia);
				    throw new RuntimeException(
					    "Caro usu�rio, o procedimento " + procedimento.getProcedimentoDaTabelaCBHPM().getCodigo() 
					    + " j� possui a porcentagem " + procedimento.getPorcentagemFormatada()
					    + ". Insira uma porcentagem diferente.");
				}
		    }
	
		    if (guia.getGuiaOrigem() != null) {
				@SuppressWarnings("unchecked")
				GuiaSimples<ProcedimentoInterface> guiaOrigem = (GuiaSimples<ProcedimentoInterface>) guia.getGuiaOrigem();
				if (guiaOrigem.isConsultaUrgencia()){
				    guiaOrigem = ImplDAO.findById(guiaOrigem.getIdGuia(), GuiaConsultaUrgencia.class);
				    guia.setGuiaOrigem(guiaOrigem);
		
				    for (ProcedimentoInterface procedimentoDaConsultaOrigem : guiaOrigem.getProcedimentos()) {
						if (procedimentoDaConsultaOrigem.getProcedimentoDaTabelaCBHPM().getCodigo().equals(TipoConsultaEnum.URGENCIA.getCodigo())) {
						    Calendar agora = Calendar.getInstance();
						    Calendar dataAutorizacao = Calendar.getInstance();
						    dataAutorizacao.setTime(guiaOrigem.getSituacao(Constantes.SITUACAO_ABERTO).getDataSituacao());
						    if (Utils.diferencaEmHoras(dataAutorizacao, agora) <= PERIODO_PARA_ZERAR_CONSULTA_DE_URGENCIA){
						    	guiaOrigem.setValorTotal(guiaOrigem.getValorTotal().subtract(procedimentoDaConsultaOrigem.getValorTotal()));
			
						    	procedimentoDaConsultaOrigem.setValorAtualDoProcedimento(MoneyCalculation.rounded(BigDecimal.ZERO));
			
						    	procedimentoDaConsultaOrigem.setValorCoParticipacao(MoneyCalculation.rounded(BigDecimal.ZERO));
						    	((GuiaConsultaUrgencia)guiaOrigem).addObservacao(new Observacao(MensagemInformacaoEnum.ZERAR_INTERNACAO_URGENCIA.descricao(), null));
			
						    	guiaOrigem.updateValorCoparticipacao();
						    }
						    break;
						}
				   }
				}
		    }
	
		    if (!guia.getProcedimentosCirurgicosTemp().isEmpty() && count < 1) {
		    	removerSituacao(guia);
		    	throw new RuntimeException("Caro usu�rio, � necess�rio que ao menos um procedimento cirurgico seja de 100%");
		    }
		}
	
		if (StringUtils.isNotEmpty(observacao)){
		    Observacao observ = new Observacao(observacao, usuario);
		    guia.addObservacao(observ);
		}
		
		ProcedimentoUtils.aplicaDescontoDaViaDeAcesso(guia);
		
		for (ProcedimentoInterface procedimento : guia.getProcedimentosCirurgicosNaoCanceladosENegados()){
		    guia.corrigeValorProcedimentoInclusoEmPacoteQueInclueHonorario(procedimento);
		}
	
		ManagerCritica.processaSituacao(guia);
		
		processarDiarias(guia, diariasInseridas, usuario);
		
		// autoriza demais exames/pacotes/procedimentos
		return autorizarGuia(guia, usuario);
    }

	private void processarDiarias(GuiaCompleta<ProcedimentoInterface> guia, Collection<ItemDiariaAuditoria> diariasInseridas, UsuarioInterface usuario) throws ValidateException, Exception {
		//Respeitar a sequencia 'processarDiariasSolicitadas' e 'depois adicionarDiariasDoAuditor'.
		processarDiariasSolicitadas(guia, usuario);
		adicionarDiariasDoAuditor(guia, diariasInseridas, usuario);
	}

    /**
     * Realiza valida��es sobre as autoriza��es da guia.
     * 
     * @param guia guia selecionada
     * @param diariasInseridas diarias inseridas em caso de Prorroga��o de Interna��o
     * @param usuario usu�rio da sess�o
     * @return guia autorizada
     * @throws Exception erros
     * @since 17/07/2012
     * @author Leonardo Sampaio
     */
    public GuiaCompleta<ProcedimentoInterface> autorizarGuia (GuiaCompleta<ProcedimentoInterface> guia, Collection<ItemDiariaAuditoria> diariasInseridas, UsuarioInterface usuario) throws Exception {
		this.tocarObjeto(guia);
	
		if (guia.getProfissional() != null) {
		    guia.getProfissional().tocarObjetos();
		}
	
		boolean autorizaProrrogacao = false;
	
		// se houver diarias inseridas pelo auditor, a guia dever� ser
		// autorizada
		if (!diariasInseridas.isEmpty()) {
		    autorizaProrrogacao = true;
		}
	
		// se ao menos uma das diarias solicitadas pelo prestador forem
		// autorizadas pelo auditor, a guia dever� ser autorizada.
		for (ItemDiaria diaria : guia.getDiariasSolicitadas()) {
		    if (diaria.isAutorizado()){
		    	autorizaProrrogacao = true;
		    }
		    Assert.isNotNull(diaria.getJustificativaNaoAutorizacao(), MensagemErroEnum.MOTIVO_AUTORIZACAO_ACOMODACAO_REQUERIDO.getMessage(diaria.getDiaria().getCodigoDescricao()));
		}
	
		if (autorizaProrrogacao) {
		    guia.mudarSituacao(usuario, SituacaoEnum.PRORROGADO.descricao(), MotivoEnum.AUTORIZACAO_PRORROGACAO_GUIA.getMessage(), new Date());
		} else{
		    guia.mudarSituacao(usuario,SituacaoEnum.NAO_PRORROGADO.descricao(), MotivoEnum.PRORROGACAO_GUIA_NAO_AUTORIZADA.getMessage(),new Date());
		}
		
		processarDiarias(guia, diariasInseridas, usuario);
		
		// autoriza demais exames/pacotes/procedimentos
		return autorizarGuia(guia, usuario);
    }

	private void adicionarDiariasDoAuditor(GuiaCompleta<ProcedimentoInterface> guia, Collection<ItemDiariaAuditoria> diariasInseridas, UsuarioInterface usuario) throws Exception {
		for (ItemDiariaAuditoria itemDiariaAuditoria : diariasInseridas){
		    itemDiariaAuditoria.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.INCLUIDO_PELO_AUDITOR.getMessage(), new Date());
		    itemDiariaAuditoria.setJustificativa(MotivoEnum.INCLUIDO_PELO_AUDITOR.getMessage());
		    itemDiariaAuditoria.setAutorizado(Boolean.TRUE);
		    
		    itemDiariaAuditoria.setQuantidadeSolicitada(itemDiariaAuditoria.getValor().getQuantidade());
		    itemDiariaAuditoria.setQuantidadeAutorizada(itemDiariaAuditoria.getValor().getQuantidade());
		    
		    itemDiariaAuditoria.recalcularCampos();
		    itemDiariaAuditoria.setGuia(guia);
		    guia.addItemDiaria(itemDiariaAuditoria);
		}
	}

    /**
     * Salva a guia.
     * 
     * @param guia guia alterada
     * @return guia salva
     * @throws Exception erros
     */
    public GuiaCompleta<ProcedimentoInterface> conferirDados(GuiaCompleta<ProcedimentoInterface> guia) throws Exception {
		if (guia.getGuiaOrigem()!=null){
		    ImplDAO.save(guia.getGuiaOrigem());
		}
		salvarGuia(guia);
		return guia;
    }

    /**
     * M�todo pai para atualiza��o de guias no bd
     * @param <G>
     * @param guia
     * @throws Exception
     */
    public <G extends GuiaSimples> void salvarGuia(G guia) throws Exception{
	isNotNull(guia,"Guia Inv�lida!");
	
	/*if[VISITA_AUTOMATICA_POR_DIARIA]
	List<ItemDiaria> diariasAutorizadas = ((GuiaCompleta)guia).getDiariasAutorizadas();
	for (ItemDiaria itemDiaria : diariasAutorizadas) {
		Date data = itemDiaria.getDataInicial();
		
		for (int i = 0; i < itemDiaria.getValor().getQuantidade(); i++) {
			if (verificaVisitas((GuiaCompleta)guia, data)) {
				TabelaCBHPM visitaHospitalar = ImplDAO.findById(3L, TabelaCBHPM.class);
				
				ProcedimentoOutros procedimentoVisita = new ProcedimentoOutros();
				procedimentoVisita.setAutorizado(true);
				procedimentoVisita.setColecaoSituacoes(new ComponenteColecaoSituacoes());
				procedimentoVisita.setDataRealizacao(data);
				procedimentoVisita.setGuia(guia);
				procedimentoVisita.setProcedimentoDaTabelaCBHPM(visitaHospitalar);
				procedimentoVisita.setPassouPelaAutorizacao(true);
				procedimentoVisita.mudarSituacao(Usuario.getInstance("sistema"), SituacaoEnum.AUTORIZADO.descricao(), "Visita Hospitalar referente � diaria autorizada.", new Date());
				procedimentoVisita.setVisitaInseridaAutomaticamente(true);
				procedimentoVisita.aplicaValorAcordo();
				
				guia.addProcedimento(procedimentoVisita);
				
			}
			data = Utils.incrementaDias(data, 1);
		}
	}
	end[VISITA_AUTOMATICA_POR_DIARIA] */

	gambyForNonUniqueObjectException();
	
	//Incrementa consumos de prestador e de segurado
	Prestador prestador = guia.getPrestador();
	SeguradoInterface segurado = guia.getSegurado();
	if (guia.isSituacaoAtual(SituacaoEnum.CONFIRMADO.descricao()) || guia.isSituacaoAtual(SituacaoEnum.FECHADO.descricao())){
	    if (prestador != null) {
		prestador.incrementarConsumoFinanceiro(guia);
		ImplDAO.save(guia.getPrestador().getConsumoIndividual());
	    }
	    if (segurado != null) {
		segurado.incrementarLimites(guia);
		ImplDAO.save(guia.getSegurado().getConsumoIndividual());
	    }

	}else if (guia.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())){
	    if (prestador != null && (!guia.getSituacaoAnterior(guia.getSituacao()).getDescricao().equals(SituacaoEnum.AUTORIZADO.descricao()) || !guia.isFromPrestador())){
		prestador.decrementarConsumoFinanceiro(guia);
		ImplDAO.save(guia.getPrestador().getConsumoIndividual());
	    }
	    if (segurado != null){
		segurado.decrementarLimites(guia);
		ImplDAO.save(guia.getSegurado().getConsumoIndividual());
	    }
	}else if (guia.getIdGuia() == null){
	    if (prestador != null && !guia.isFromPrestador()){
		prestador.incrementarConsumoFinanceiro(guia);
		ImplDAO.save(guia.getPrestador().getConsumoIndividual());
	    }
	    if (segurado != null){
		segurado.incrementarLimites(guia);
		ImplDAO.save(guia.getSegurado().getConsumoIndividual());
	    }
	}else{
	    if(prestador != null)
		ImplDAO.save(guia.getPrestador().getConsumoIndividual());
	    ImplDAO.save(guia.getSegurado().getConsumoIndividual());
	}

	if(guia.isInternacao()){
	    ((GuiaCompleta)guia).setTipoAcomodacao(GuiaCompleta.TIPO_ACOMODACAO_INTERNO);
	}

	ImplDAO.save(guia);

	if(guia.getAutorizacao() == null){
	    guia.setAutorizacao(guia.getIdGuia().toString());
	}
    }

    /**
     * M�todo que verifica se j� existe uma visita associada na data passada.
     * @author Luciano Infoway
     * @since 24/06/2013
     * @param data
     * @return
     */
    private boolean verificaVisitas(GuiaCompleta guia, Date data) {
		for (Object proc : guia.getProcedimentosOutros()) {
			if ((Utils.compareData(((ProcedimentoOutros)proc).getDataRealizacao(), data) == 0) 
				&& ((ProcedimentoOutros)proc).getProcedimentoDaTabelaCBHPM().getCodigo().equals("10102019")) {
				return false;
			}
		}
		return true;
	}

	/**
     * Tentativa de resolver uma NonUniqueObjectException.
     * Resolveu, mas aceito solu��es alternativas.
     * 
     * @author Eduardo
     */
    private void gambyForNonUniqueObjectException() {
	Session currentSession = HibernateUtil.currentSession();
	currentSession.flush();
	currentSession.clear();
    }


    /**
     * Remove situa��o atual da lista de situa��es 
     * 
     * @param guia
     */
    private void removerSituacao(GuiaCompleta<ProcedimentoInterface> guia) {
	if (guia.getSituacao().getIdSituacao() == null) {
	    SituacaoInterface situacaoAtual = guia.getSituacao();
	    guia.setSituacao(guia.getSituacaoAnterior(situacaoAtual));
	    guia.getSituacoes().remove(situacaoAtual);
	}
    }

    /**
     * Verifica o n�vel m�ximo de acesso necess�rio para os procedimentos da guia 
     * 
     * @param guia
     * @param usuario
     * 
     * @see AutorizarExamesEletivosService.validarNivelAcesso
     */
    private void validaNivelDeAcesso(GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario) 
    {
	int nivelDeAcesso = getNivelMaximo(guia.getProcedimentosSolicitados());
	
	if (nivelDeAcesso == NIVEL_DE_ACESSO_MAXIMO_ROLE_REGULADOR && usuario.isPossuiRole(UsuarioInterface.ROLE_REGULADOR))
	{
	    throw new AutorizacaoExameException(MensagemErroEnumSR.USUARIO_SEM_NIVEL_DE_AUTORIZACAO.getMessage(String.valueOf(nivelDeAcesso)));
	}
	if (nivelDeAcesso == NIVEL_DE_ACESSO_MAXIMO_ROLE_DIRETORIA_MEDICA_E_ROOT && (!usuario.isPossuiRole(UsuarioInterface.ROLE_DIRETORIA_MEDICA) && !usuario.isPossuiRole(UsuarioInterface.ROLE_ROOT)))
	{
	    throw new AutorizacaoExameException(MensagemErroEnumSR.USUARIO_SEM_NIVEL_DE_AUTORIZACAO.getMessage(String.valueOf(nivelDeAcesso)));
	}
    }
    
    /**
     * Filtra as cr�ticas apresent�veis da guia
     * 
     * @param guia
     */
    private void filtrarCriticasApresentaveis(GuiaSimples<?> guia) {
	ManagerCritica.processaApresentaveis(guia,TipoCriticaEnum.CRITICA_DLP_SUBGRUPO.valor(),TipoCriticaEnum.CRITICA_DLP_CID.valor());
    }

    /**
     * Retorna o maior n�vel de acesso em um conjunto de procedimentos
     * 
     * @param procedimentos lista de procedimentos
     * @return n�vel m�ximo
     */
    private int getNivelMaximo(Collection<ProcedimentoInterface> procedimentos) {
	int nivel = 1;
	for (ProcedimentoInterface procedimentoInterface : procedimentos) {
	    if (procedimentoInterface.getProcedimentoDaTabelaCBHPM().getNivel() > nivel)
		nivel = procedimentoInterface.getProcedimentoDaTabelaCBHPM().getNivel();
	}
	return nivel;
    }

    /**
     * Busca guias com Solicita��es de Exames/Procedimentos/Pacotes especiais.
     * 
     * @param autorizacao
     *            n�mero de autoriza��o
     * @param dataInicial
     *            limite inferior da busca por data de marca��o
     * @param dataFinal
     *            limite superior da busca por data de marca��o
     * @param prestador
     *            prestador associado � solicita��o
     * @param usuario
     *            usu�rio da sess�o
     * @return guias encontradas
     * @throws Exception
     *             erros
     * @author Leonardo Sampaio
     * @since 17/07/2012
     */
    @SuppressWarnings("unchecked")
    private List<GuiaCompleta> buscarGuiasExamesProcedimentosPacotesEspeciaisOPMEs(String autorizacao, Date dataInicial, Date dataFinal, Prestador prestador) throws Exception 
    {
	
	List<GuiaCompleta> guias = new ArrayList<GuiaCompleta>();
	SearchAgent sa = new SearchAgent();

	String[] situacoesExamesProcedimentosPacotesEspeciaisOPMEs = {
		SituacaoEnum.SOLICITADO_PRORROGACAO.descricao(),
		SituacaoEnum.NAO_PRORROGADO.descricao(),
		SituacaoEnum.PRORROGADO.descricao(),
		SituacaoEnum.ABERTO.descricao(),
		SituacaoEnum.SOLICITADO.descricao() };

	if (!Utils.isStringVazia(autorizacao)) {
	    // busca por autorizacao
	    sa.addParameter(new Equals("autorizacao", autorizacao));
	    GuiaCompleta<ProcedimentoInterface> guia = sa.uniqueResult(GuiaCompleta.class);

	    if (guia != null  && Arrays.asList (situacoesExamesProcedimentosPacotesEspeciaisOPMEs).contains(guia.getSituacao().getDescricao())) 
	    {
		// guia v�lida
		boolean isPossuiExamesProcedimentosPacotesOPMEsParaAutorizar = !guia.getProcedimentosSolicitados().isEmpty() || !guia.getItensPacoteSolicitados().isEmpty() || !guia.getOpmesSolicitados().isEmpty();

		if (isPossuiExamesProcedimentosPacotesOPMEsParaAutorizar) 
		{
		    guias.add(guia);
		}
	    }

	} 
	else 
	{

	    if (dataInicial != null) {
		sa.addParameter(new GreaterEquals("dataMarcacao", dataInicial));
	    }

	    if (dataFinal != null) {
		sa.addParameter(new LowerEquals("dataMarcacao", dataFinal));
	    }

	    if (prestador != null) {
		sa.addParameter(new Equals("prestador", prestador));
	    }

	    Set<GuiaCompleta> guiasInternacao = new HashSet<GuiaCompleta>();
	    Criteria criteria = sa
		    .createCriteriaFor(GuiaCompleta.class)
		    .add(Expression.in("situacao.descricao", situacoesExamesProcedimentosPacotesEspeciaisOPMEs))
		    .createAlias("procedimentos", "procs", CriteriaSpecification.LEFT_JOIN)
		    .createAlias("itensPacote", "itensPcte", CriteriaSpecification.LEFT_JOIN)
		    .createAlias("itensOpme", "opmes", CriteriaSpecification.LEFT_JOIN)
		    .add(Expression.or((Expression.or(Expression.eq("procs.situacao.descricao", SituacaoEnum.SOLICITADO.descricao()),
			    Expression.eq ("itensPcte.situacao.descricao", SituacaoEnum.SOLICITADO.descricao()))), 
			    Expression.eq("opmes.situacao.descricao", SituacaoEnum.SOLICITADO.descricao())));

	    guiasInternacao.addAll(criteria.list());

	    List<GuiaExame> guiasExameExterno = sa.createCriteriaFor(GuiaExame.class)
		    .add(Expression.eq("situacao.descricao", SituacaoEnum.SOLICITADO.descricao()))
		    .add(Expression.sql("this_.idguiaorigem in (select  idguia "
					 + "from atendimento_guiasimples where tipodeguia in ('IUR','IEL','AUR','CUR'))"))
					 .list();

	    guias.addAll(guiasInternacao);
	    guias.addAll(guiasExameExterno);
	}

	return guias;
    }

    /**
     * Busca guias com Solicita��es de Prorroga��o.
     * 
     * @param autorizacao
     *            n�mero de autoriza��o
     * @param dataInicial
     *            limite inferior da busca por data de marca��o
     * @param dataFinal
     *            limite superior da busca por data de marca��o
     * @param prestador
     *            prestador associado � solicita��o
     * @param usuario
     *            usu�rio da sess�o
     * @return guias encontradas
     * @throws Exception
     *             erros
     * @author Leonardo Sampaio
     * @since 17/07/2012
     */
    @SuppressWarnings("unchecked")
    private List<GuiaCompleta> buscarGuiasComSolicitacaoDeProrrogacao(String autorizacao, Date dataInicial, Date dataFinal, Prestador prestador, UsuarioInterface usuario) throws Exception {

		ArrayList<GuiaCompleta> guias = new ArrayList<GuiaCompleta>();
	
		if (!Utils.isStringVazia(autorizacao)) {
		    // busca por uma �nica guia
		    GuiaCompleta<ProcedimentoInterface> guia = (GuiaCompleta<ProcedimentoInterface>) HibernateUtil.currentSession().createCriteria(GuiaCompleta.class)
			    .add(Expression.eq("autorizacao", autorizacao))
			    .uniqueResult();
	
		    if (guia != null && guia.isSituacaoAtual(SituacaoEnum.SOLICITADO_PRORROGACAO.descricao())){
		    	this.tocarObjeto(guia);
		    	guia.setUsuarioDoFluxo(usuario);
		    	guias.add(guia);
		    }
	
		}else {
		    SearchAgent sa = new SearchAgent();
		    Criteria criteria = sa.createCriteriaFor(GuiaCompleta.class);
	
		    if (prestador != null){
		    	criteria.add(Expression.or(Expression.isNull("prestador"), Expression.eq("prestador", prestador)));
		    } else{
		    	criteria.add(Expression.eq("prestador", prestador));
		    }
		    
		    if (dataInicial != null){
		    	criteria.add(Expression.ge("dataMarcacao", dataInicial));
		    }
	
		    if (dataFinal != null){
		    	criteria.add(Expression.le("dataMarcacao", dataFinal));
		    }
	
		    guias.addAll(criteria.add(Expression.eq("situacao.descricao", SituacaoEnum.SOLICITADO_PRORROGACAO.descricao())).list());
	
		}
		return guias;
    }

    /**
     * Tocar guia.
     * 
     * @param guia
     * 
     */
    private void tocarObjeto(GuiaCompleta<ProcedimentoInterface> guia) {
    	TocarObjetoUtils.tocarObjeto(guia, TocarObjetoEnum.CID_GUIA, 
    			TocarObjetoEnum.OBSERVACOES_GUIA, 
    			TocarObjetoEnum.ITENS_PACOTE_GUIA,
    			TocarObjetoEnum.ITENS_GASOTERAPIA_GUIA,
    			TocarObjetoEnum.ITENS_TAXA_GUIA,
    			TocarObjetoEnum.PROCEDIMENTOS_GUIA);

    	guia.getSegurado().tocarObjetos();
    	guia.getPrestador().tocarObjetos();
    	guia.getColecaoSituacoes().getSituacoes().size();

		for (SituacaoInterface situacao : guia.getColecaoSituacoes().getSituacoes()) {
		    situacao.getDataSituacao();
		}

		for (ItemDiaria itemdiaria : guia.getItensDiaria()) {
		    for (SituacaoInterface situacao : itemdiaria.getColecaoSituacoes().getSituacoes()) {
		    	situacao.getDataSituacao();
		    }
		}

    }

    /**
     * Busca guias com Solicita��es de Interna��o.
     * 
     * @param autorizacao
     *            n�mero de autoriza��o
     * @param dataInicial
     *            limite inferior da busca por data de situa��o
     * @param dataFinal
     *            limite superior da busca por data de situa��o
     * @param prestador
     *            prestador associado � solicita��o
     * @return guias encontradas
     * @throws Exception
     *             erros
     * @author Leonardo Sampaio
     * @since 17/07/2012
     */
    @SuppressWarnings("unchecked")
    private List<GuiaInternacao> buscarGuiasComSolicitacaoDeInternacao(String autorizacao, Date dataInicial, Date dataFinal, Prestador prestador) throws Exception {

	SearchAgent sa = new SearchAgent();

	sa.addParameter(new Equals("situacao.descricao", SituacaoEnum.SOLICITADO_INTERNACAO.descricao()));

	if (Utils.isStringVazia(autorizacao) && prestador == null
		&& dataInicial == null && dataFinal == null) {
	    throw new Exception(MensagemErroEnum.AUSENCIA_DE_PARAMETROS.getMessage());
	}

	List<GuiaInternacao> guiasEncontradas = new ArrayList<GuiaInternacao>();

	if (!Utils.isStringVazia(autorizacao)) {
	    // buscar por autorizacao
	    sa.addParameter(new Like("autorizacao", autorizacao.trim()));
	    Criteria criteria = sa.createCriteriaFor(GuiaInternacao.class);

	    if (prestador != null) {
		criteria.add(Expression.eq("prestador", prestador));
	    }
	    guiasEncontradas.addAll(criteria.list());

	} else {// busca por demais parametros
	    Calendar dataFinalCalendar = Calendar.getInstance();

	    if (dataFinal != null) {
		dataFinalCalendar.setTime(dataFinal);
		dataFinalCalendar.set(GregorianCalendar.HOUR_OF_DAY, 23);
		dataFinalCalendar.set(GregorianCalendar.MINUTE, 59);
		dataFinalCalendar.set(GregorianCalendar.SECOND, 59);
	    }

	    if (dataInicial != null) {
		sa.addParameter(new GreaterEquals("situacao.dataSituacao", dataInicial));
	    }

	    if (dataFinal != null) {
		sa.addParameter(new LowerEquals("situacao.dataSituacao", dataFinalCalendar.getTime()));
	    }

	    Criteria criteria = sa.createCriteriaFor(GuiaInternacao.class);

	    if (prestador != null) {
		criteria.add(Expression.eq("prestador",prestador));
	    }

	    guiasEncontradas.addAll(criteria.list());
	}

	Collections.sort(guiasEncontradas, new Comparator<GuiaInternacao>() {
	    public int compare(GuiaInternacao g1, GuiaInternacao g2) {
		return g1.getDataMarcacao().compareTo(g2.getDataMarcacao());
	    }
	});

	return guiasEncontradas;
    }

}
