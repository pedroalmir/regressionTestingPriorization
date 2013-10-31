package br.com.infowaypi.ecare.services.auditor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.exceptions.AutorizacaoExameException;
import br.com.infowaypi.ecare.manager.SeguradoManager;
import br.com.infowaypi.ecarebc.associados.Prestador.TipoIncremento;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.atendimentos.enums.TipoCriticaEnum;
import br.com.infowaypi.ecarebc.atendimentos.validators.ManagerCritica;
import br.com.infowaypi.ecarebc.atendimentos.validators.ServiceApresentacaoCriticasFiltradas;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.exceptions.ConsumoException;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.ecarebc.utils.CommandCorrecaoValorGuia;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.molecular.parameter.LowerEquals;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class AutorizarExamesEletivosService extends Service implements ServiceApresentacaoCriticasFiltradas{
	
	/**
	 * Busca guias de exame que estão solicitada.Caso não seja informado nenhum parametro esse método retornará todas as guias solicitadas
	 * @param autorizacao
	 * @param dataInicial
	 * @param dataFinal
	 * @param prestador
	 * @return retorna um resumo contendo as guias encontradas
	 * @throws Exception
	 */
	public ResumoGuias<GuiaExame> buscarGuias(String autorizacao, String dataInicial, String dataFinal) throws Exception {
		List<GuiaExame> guias = new ArrayList<GuiaExame>();
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("situacao.descricao",SituacaoEnum.SOLICITADO.descricao()));
		sa.addParameter(new Equals("tipoDeGuia","GEX"));
		
		if(!Utils.isStringVazia(autorizacao))
			sa.addParameter(new Equals("autorizacao", autorizacao));
		
		if (!Utils.isStringVazia(dataInicial))
			sa.addParameter(new GreaterEquals("dataMarcacao", Utils.parse(dataInicial)));
	
		if (!Utils.isStringVazia(dataFinal))
			sa.addParameter(new LowerEquals("dataMarcacao", Utils.parse(dataFinal)));
		
		guias = sa.list(GuiaExame.class);
		
		Assert.isNotEmpty(guias, MensagemErroEnum.GUIAS_NAO_ENCONTRADAS_PARA_AUTORIZACAO.getMessage());
		
		ResumoGuias<GuiaExame> resumo = new ResumoGuias<GuiaExame>(guias, ResumoGuias.SITUACAO_SOLICITADA,false);
		return resumo;
	}

	/**
	 * Esse método valida se o usuario possui nível de acesso para liberar os exames da guia e se o 
	 * beneficiario tem limite para exames estourado.
	 * @param guia
	 * @param usuario
	 * @return retorna a guia de exame que foi selecionada
	 * @throws Exception
	 */
	public GuiaExame selecionarGuia(GuiaExame guia, UsuarioInterface usuario) throws Exception {
		validaNivelDeAcesso(guia, usuario);
		AbstractSegurado segurado = guia.getSegurado();
		try {
			segurado.temLimite(new Date(), guia);
		} catch (ConsumoException e) {
			guia.setMensagemLimite(e.getMessage());
			Integer tipoLiberacao = SeguradoManager.pegaTipoDeLiberacao(segurado, guia.getProfissional());
			guia.setLiberadaForaDoLimite(tipoLiberacao);
			SeguradoManager.contabilizaLiberacoesForaDoLimite(segurado, tipoLiberacao);
		}
		
		filtrarCriticasApresentaveis(guia);
		
		return super.selecionarGuia(guia);
	}
	
	/**
	 * Muda a situação dos procedimentos para Autorizado(a) caso eles tenham sido autorizados pelo 
	 * usuario (Médico Auditor, Médico Regulador ou Diretoria Medica).
	 * @param guia
	 */
	public GuiaExame<ProcedimentoInterface> autorizarProcedimentos(GuiaExame<ProcedimentoInterface> guia,UsuarioInterface usuario) throws Exception {
		
		boolean possuiPeloMenosUmAutorizado = false;
		boolean possuiPeloMenosUmPendente = false;
		int countAutorizados = 0;
		
		for (ProcedimentoInterface procedimento : guia.getProcedimentos()) {
			if (procedimento.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao()) && procedimento.getActionRegulacao() == null ) {
				throw new Exception("Todos os procedimentos da guia devem ser regulados.");
			}
		}
						
		
		for (ProcedimentoInterface procedimento : guia.getProcedimentos()) {
			if (procedimento.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())) {
				if (procedimento.getActionRegulacao().equals(Constantes.AUTORIZAR)) {
					possuiPeloMenosUmAutorizado = true;
					countAutorizados++;
					procedimento.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZADO_PELO_ROLE_INFORMADO.getMessage(getDescricaoRole(usuario)), new Date());
				} else if (procedimento.getActionRegulacao().equals(Constantes.NAO_AUTORIZAR)) {
					if (Utils.isStringVazia(procedimento.getMotivo())) {
						throw new AutorizacaoExameException(MensagemErroEnumSR.MOTIVO_REQUERIDO.getMessage(procedimento.getProcedimentoDaTabelaCBHPM().getCodigo()));
					}
					procedimento.mudarSituacao(usuario, SituacaoEnum.NAO_AUTORIZADO.descricao(), procedimento.getMotivo(), new Date());						
					guia.getSegurado().atualizarLimites(guia, TipoIncremento.NEGATIVO, procedimento.getQuantidade());		
				} else if (procedimento.getActionRegulacao().equals(Constantes.DEIXAR_PENDENTE)) {
					if (Utils.isStringVazia(procedimento.getMotivo())) {
						throw new AutorizacaoExameException(MensagemErroEnumSR.MOTIVO_REQUERIDO_PENDENTE.getMessage(procedimento.getProcedimentoDaTabelaCBHPM().getCodigo()));
					}
					procedimento.mudarSituacao(usuario, SituacaoEnum.PENDENTE.descricao(), procedimento.getMotivo(), new Date());						
					possuiPeloMenosUmPendente = true;
				}
			}				
		}
		if(guia.getProcedimentos().size() == countAutorizados) {
			guia.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZADO_PELO_ROLE_INFORMADO.getMessage(getDescricaoRole(usuario)), new Date());			
		} else{
			if (possuiPeloMenosUmAutorizado) {
				guia.mudarSituacao(usuario, SituacaoEnum.PARCIALMENTE_AUTORIZADO.descricao(), MotivoEnum.PARCIALMENTE_AUTORIZADA_AUDITOR.getMessage(getDescricaoRole(usuario)), new Date());			
			} else {
				if (possuiPeloMenosUmPendente) {
					guia.mudarSituacao(usuario, SituacaoEnum.PENDENTE.descricao(), MotivoEnum.PENDENTE_AUDITOR.getMessage(getDescricaoRole(usuario)), new Date());								
				} else {
					guia.mudarSituacao(usuario, SituacaoEnum.NAO_AUTORIZADO.descricao(), MotivoEnum.NAO_AUTORIZADO_PELO_ROLE_INFORMADO.getMessage(getDescricaoRole(usuario)), new Date());
				}
			}
		}
		
		CommandCorrecaoValorGuia command = new CommandCorrecaoValorGuia(guia);
		command.execute();
		
		processaSituacaoCriticas(guia);
		
		return guia;
		
	}
	
	/**
	 * Método para o passo de conferencia de dados, no flow esse método deve ser commited.
	 * @param guia
	 * @return
	 * @throws Exception
	 */
	public GuiaExame conferirDados(GuiaExame guia) throws Exception {
		ImplDAO.save(guia.getSegurado().getConsumoIndividual());
		super.salvarGuia(guia);
		return guia;
	}
	
	/**
	 * 
	 * @param procedimentos
	 * @return Retorna o maior nível de acesso em um conjunto de procedimentos 
	 */
	private int getNivelMaximo(Collection<ProcedimentoInterface> procedimentos){
		int nivel = 1;
		for (ProcedimentoInterface procedimentoInterface : procedimentos) {
			if(procedimentoInterface.getProcedimentoDaTabelaCBHPM().getNivel() > nivel)
				nivel = procedimentoInterface.getProcedimentoDaTabelaCBHPM().getNivel();
		}
		return nivel;
	}
	
	
	/**
	 * Valida se o usuario tem privilégio para liberar os exames contidos na guia.
	 * @param guia
	 * @param usuario
	 */
	private void validaNivelDeAcesso(GuiaExame<ProcedimentoInterface> guia, UsuarioInterface usuario){
		int nivel = getNivelMaximo(guia.getProcedimentosSolicitados());
		if(nivel == 3 && usuario.isPossuiRole(UsuarioInterface.ROLE_REGULADOR))
			throw new AutorizacaoExameException(MensagemErroEnumSR.USUARIO_SEM_NIVEL_DE_AUTORIZACAO.getMessage(String.valueOf(nivel)));
		
		if(nivel == 4 && (!usuario.isPossuiRole(UsuarioInterface.ROLE_DIRETORIA_MEDICA) || !usuario.isPossuiRole(UsuarioInterface.ROLE_ROOT)))
			throw new AutorizacaoExameException(MensagemErroEnumSR.USUARIO_SEM_NIVEL_DE_AUTORIZACAO.getMessage(String.valueOf(nivel)));
		
	}
	
	private String getDescricaoRole(UsuarioInterface usuario){
		if (usuario.isPossuiRole(UsuarioInterface.ROLE_DIRETORIA_MEDICA)) 
			return "Diretoria Médica";
		
		if (usuario.isPossuiRole(UsuarioInterface.ROLE_AUDITOR)) 
			return "Auditor";
		
		if (usuario.isPossuiRole(UsuarioInterface.ROLE_ROOT)) 
			return "Ti";
		
		if (usuario.isPossuiRole(UsuarioInterface.ROLE_REGULADOR)) 
			return "Médico Regulador";
		
		return "";
		
	}
	
	@Override
	public void filtrarCriticasApresentaveis(GuiaSimples<?> guia) {
		ManagerCritica.processaApresentaveis(guia, TipoCriticaEnum.CRITICA_DLP_SUBGRUPO.valor(), TipoCriticaEnum.CRITICA_DLP_CID.valor());
	}

	@Override
	public void processaSituacaoCriticas(GuiaSimples<?> guia) {
		ManagerCritica.processaSituacao(guia);	// TODO Auto-generated method stub
	}
}
