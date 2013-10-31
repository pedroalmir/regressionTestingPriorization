package br.com.infowaypi.ecarebc.service.autorizacoes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Prestador.TipoIncremento;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.Observacao;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.atendimentos.validators.GuiaProcedimentoPorcentagemValidator;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.ecarebc.utils.CommandCorrecaoCalculoValorProcedimento;
import br.com.infowaypi.ecarebc.utils.CommandCorrecaoValorGuia;
import br.com.infowaypi.ecarebc.utils.ProcedimentoUtils;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.molecular.parameter.LowerEquals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Service para autorização de exames especiais no plano de saúde
 * @author Marcus Boolean
 * @changes Idelvane
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class AutorizarExamesProcedimentosEspeciaisService extends Service{
	
	public AutorizarExamesProcedimentosEspeciaisService(){
		super();
	}
	
	public ResumoGuias buscarGuias(String autorizacao, Date dataInicial, Date dataFinal,Prestador prestador) throws Exception {
		List<GuiaCompleta> guias = new ArrayList<GuiaCompleta>();
		SearchAgent sa = new SearchAgent();
		
		String[] situacoes = {SituacaoEnum.SOLICITADO_PRORROGACAO.descricao(), SituacaoEnum.NAO_PRORROGADO.descricao()
				,SituacaoEnum.PRORROGADO.descricao(),SituacaoEnum.ABERTO.descricao(),SituacaoEnum.SOLICITADO.descricao()};
			
		if(!Utils.isStringVazia(autorizacao)){
			sa.addParameter(new Equals("autorizacao",autorizacao));
			GuiaCompleta<ProcedimentoInterface> guia = sa.uniqueResult(GuiaCompleta.class);
			Assert.isNotNull(guia, "Guia não encontrada.");
			
			if(!Arrays.asList(situacoes).contains(guia.getSituacao().getDescricao()))
				throw new RuntimeException("Caro usuário, a guia de autorização " +guia.getAutorizacao()+" possui situação \""+guia.getSituacao().getDescricao()+"\". "+
						"Não é permitido a autorização de exames/procedimentos em guias com situação \" "+guia.getSituacao().getDescricao()+"\".");
			boolean isPossuiExamesProcedimentosPacotesParaAutorizar = !guia.getProcedimentosSolicitados().isEmpty() || !guia.getItensPacoteSolicitados().isEmpty();
			Assert.isTrue(isPossuiExamesProcedimentosPacotesParaAutorizar, "Esta guia não possui procedimentos ou pacotes para serem autorizados.");
			guias.add(guia);
		} else {
			if(dataInicial != null){
				sa.addParameter(new GreaterEquals("dataMarcacao",dataInicial));
			}
			
			if(dataFinal != null){
				sa.addParameter(new LowerEquals("dataMarcacao",dataFinal));
			}
			
			if(prestador != null){
				sa.addParameter(new Equals("prestador",prestador));
			}
			
			Set<GuiaCompleta> guiasInternacao = new HashSet<GuiaCompleta>();
			Criteria criteria = sa.createCriteriaFor(GuiaCompleta.class)
									.add(Expression.in("situacao.descricao", situacoes))
									.createAlias("procedimentos", "procs",CriteriaSpecification.LEFT_JOIN)
									.createAlias("itensPacote", "itensPcte",CriteriaSpecification.LEFT_JOIN)
									.add(Expression.or(
											Expression.eq("procs.situacao.descricao", SituacaoEnum.SOLICITADO.descricao()),
											Expression.eq("itensPcte.situacao.descricao", SituacaoEnum.SOLICITADO.descricao())));
			
			guiasInternacao.addAll(criteria.list());
			
			List<GuiaExame> guiasExameExterno = sa.createCriteriaFor(GuiaExame.class)
													.add(Expression.eq("situacao.descricao", SituacaoEnum.SOLICITADO.descricao()))
													.add(Expression.sql("this_.idguiaorigem in (select  idguia " +
															"from atendimento_guiasimples where tipodeguia in ('IUR','IEL','AUR','CUR'))"))
													.list();
			
			guias.addAll(guiasInternacao);
			guias.addAll(guiasExameExterno);
		}
		
		ResumoGuias<GuiaCompleta> resumo = new ResumoGuias<GuiaCompleta>(guias, ResumoGuias.SITUACAO_TODAS,false);
		Assert.isNotEmpty(resumo.getGuiasComExamesParaAutorizacao(), MensagemErroEnum.NENHUMA_GUIA_ENCONTRADA.getMessage());
		return resumo;
	}
	
	@SuppressWarnings("unchecked")
	public GuiaSimples selecionarGuia(GuiaSimples guia) {
		Assert.isNotNull(guia, MensagemErroEnum.NENHUMA_GUIA_SELECIONADA.getMessage());
		return guia;
	}
	
	public GuiaCompleta autorizarExamesProcedimentosEspeciais(GuiaCompleta guia, Boolean ignorarValidacao, Collection<Procedimento> procedimentos, Collection<ProcedimentoCirurgico> procedimentosCirurgicos,String observacoes, UsuarioInterface usuario) throws Exception {
		BigDecimal valorAnterior = guia.getValorTotal();
		
		if(procedimentos != null) {
			guia.addAllProcedimentos(procedimentos);
		}
		if(procedimentosCirurgicos != null) {
			guia.addAllProcedimentos(procedimentosCirurgicos);
		}
		if(!Utils.isStringVazia(observacoes)) {
			Observacao observacao = new Observacao(observacoes,usuario);
			guia.addObservacao(observacao);
		}
		if(!ignorarValidacao) {
			guia.validate();
		}
		guia.setValorAnterior(valorAnterior);
		
		CommandCorrecaoCalculoValorProcedimento cmd = new CommandCorrecaoCalculoValorProcedimento(guia);
		cmd.execute();
		
		return guia;
	}

	public GuiaSimples autorizarExamesProcedimentosEspeciais(Collection<Procedimento> procedimentosAutorizados, Collection<ProcedimentoCirurgico> procedimentosCirurgicos,GuiaSimples<ProcedimentoInterface> guia,UsuarioInterface usuario) throws Exception {
		if(procedimentosAutorizados.isEmpty() && procedimentosCirurgicos.isEmpty() && !guia.isExameExterno())
			throw new ValidateException("Selecione pelo menos um exame e/ou procedimento cirúrgico para ser autorizado.");

		if(!procedimentosCirurgicos.isEmpty()) {
			for (ProcedimentoCirurgico procedimentoCirurgico : procedimentosCirurgicos) {
				procedimentoCirurgico.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZADO_PELO_AUDITOR.getMessage(), new Date());
			}
		}
		
		if(!procedimentosAutorizados.isEmpty()) {
			for (Procedimento procedimento : procedimentosAutorizados) {
				procedimento.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZADO_PELO_AUDITOR.getMessage(), new Date());
			}
		}
		
		if(guia.isExameExterno() && guia.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())){
			for (ProcedimentoInterface procedimento : guia.getProcedimentosSolicitados()) {
				if(!procedimentosAutorizados.contains(procedimento))
					procedimento.mudarSituacao(usuario, SituacaoEnum.NAO_AUTORIZADO.descricao(), "Procedimento não autorizado", new Date());
					
			}
			if(procedimentosAutorizados.isEmpty()){
				guia.mudarSituacao(usuario, SituacaoEnum.NAO_AUTORIZADO.descricao(), "Nenhum procedimento foi autorizado", new Date());
			}else{
				guia.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), "Autorização de procedimentos especiais", new Date());
				CommandCorrecaoValorGuia command = new CommandCorrecaoValorGuia(guia);
				command.execute();
			}
		}
			
		
		return guia;
	}
	
	//METODO QUE É USADO PARA A AUTORIZAÇÃO DE EXAMES.
	public GuiaCompleta<ProcedimentoInterface> autorizarExamesProcedimentosEspeciais(GuiaCompleta<ProcedimentoInterface> guia,UsuarioInterface usuario) throws Exception {
		//TODO mandar esses dois metodos para GuiaCompleta
		Set<ProcedimentoInterface> examesAutorizados 	= getExamesAutorizados(guia);
		Set<ProcedimentoInterface> cirurgiasAutorizados 	= getCirurgiasAutorizados(guia);
		Set<ProcedimentoInterface> procedimentosNaoAutorizados 	= getProcedimentosAutorizacaoNegada(guia);
		
		Set<ItemPacote> pacotesAutorizados 	= guia.getItensPacoteAutorizadosEmAutorizacao();
		Set<ItemPacote> pacotesNegados 		= guia.getItensPacoteNegadosEmAutorizacao();
		
		boolean isGuiaExameSolicitado = guia.isExame() && guia.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao());
		
		if(isGuiaExameSolicitado) {
			Set<ProcedimentoInterface> procedimentosSolicitados = new HashSet<ProcedimentoInterface>(guia.getProcedimentosSolicitados());
			procedimentosSolicitados.removeAll(cirurgiasAutorizados);
			procedimentosSolicitados.removeAll(examesAutorizados);
			procedimentosSolicitados.removeAll(procedimentosNaoAutorizados);
			
			if(!procedimentosSolicitados.isEmpty()) {
				throw new RuntimeException("Caro Auditor(a), todos os procedimentos dessa guia devem ser AUTORIZADOS ou NÃO AUTORIZADOS.");
			}
			
			boolean isPossuiAlgumProcedimentoAutorizado = !cirurgiasAutorizados.isEmpty() || !examesAutorizados.isEmpty();
			
			if(isPossuiAlgumProcedimentoAutorizado) {
				guia.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), "Autorização de procedimentos especiais", new Date());
			}else {
				guia.mudarSituacao(usuario, SituacaoEnum.NAO_AUTORIZADO.descricao(), "Nenhum procedimento foi autorizado", new Date());
			}
		}
		
		if(guia.isInternacao() || guia.isAtendimentoUrgencia() || guia.isConsultaUrgencia()) {
			if (!cirurgiasAutorizados.isEmpty()) {
				GuiaProcedimentoPorcentagemValidator.verificaPercentualDeReducao(cirurgiasAutorizados, ((GuiaCompleta) guia).isPossuiProcedimentoAutorizadoA100Porcento());
			}
		}
		
		for (ProcedimentoInterface procedimento : cirurgiasAutorizados) {
			Assert.isNotNull(procedimento.getMotivo(), "O motivo de autorização deve ser informado.");
			procedimento.mudarSituacao(guia.getUsuarioDoFluxo(), SituacaoEnum.AUTORIZADO.descricao(), procedimento.getMotivo(), new Date());
		}
		for (ProcedimentoInterface procedimento : examesAutorizados) {
			Assert.isNotNull(procedimento.getMotivo(), "O motivo de autorização deve ser informado.");
			procedimento.mudarSituacao(guia.getUsuarioDoFluxo(), SituacaoEnum.AUTORIZADO.descricao(), procedimento.getMotivo(), new Date());
		}
		for (ProcedimentoInterface procedimento : procedimentosNaoAutorizados) {
			Assert.isNotNull(procedimento.getMotivo(), "O motivo da não autorização deve ser informado.");
			procedimento.mudarSituacao(guia.getUsuarioDoFluxo(), SituacaoEnum.NAO_AUTORIZADO.descricao(), procedimento.getMotivo(), new Date());
			guia.getSegurado().atualizarLimites(guia, TipoIncremento.NEGATIVO, procedimento.getQuantidade());
		}
		
		for (ItemPacote itemPacote : pacotesAutorizados) {
			itemPacote.mudarSituacao(guia.getUsuarioDoFluxo(), SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZADO_PELO_AUDITOR.getMessage(), new Date());
		}
		for (ItemPacote itemPacote : pacotesNegados) {
			itemPacote.mudarSituacao(guia.getUsuarioDoFluxo(), SituacaoEnum.NAO_AUTORIZADO.descricao(), MotivoEnum.NAO_AUTORIZADO_AUDITOR.getMessage(), new Date());
		}
		
		if (guia.isInternacao()) {
			ProcedimentoUtils.aplicaDescontoDaViaDeAcesso((GuiaCompleta) guia);
		}
		
		guia.recalcularValores();
		
		return guia;
		
	}


	
	private Set<ProcedimentoInterface> getExamesAutorizados(GuiaSimples<ProcedimentoInterface> guia){
		
		boolean isAutorizado = false;
		
		Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();
		for (ProcedimentoInterface procedimento : guia.getProcedimentos(SituacaoEnum.SOLICITADO.descricao())) {
			if (procedimento.getTipoProcedimento() != ProcedimentoInterface.PROCEDIMENTO_CIRURGICO) {
				isAutorizado = procedimento.getAutorizado() == null? false : procedimento.getAutorizado();
				if(procedimento.isPassouPelaAutorizacao() && isAutorizado){
					procedimentos.add(procedimento);
				}
			}
		}
		return procedimentos;
	}
	
	private Set<ProcedimentoInterface> getCirurgiasAutorizados(GuiaSimples<ProcedimentoInterface> guia){
		
		boolean isAutorizado = false;
		
		Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();
		for (ProcedimentoInterface procedimento : guia.getProcedimentos(SituacaoEnum.SOLICITADO.descricao())) {
			if (procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO) {
				isAutorizado = procedimento.getAutorizado() == null? false : procedimento.getAutorizado();
				if(procedimento.isPassouPelaAutorizacao() && isAutorizado){
					procedimentos.add(procedimento);
				}
			}
		}
		return procedimentos;
	}
	
	private Set<ProcedimentoInterface> getProcedimentosAutorizacaoNegada(GuiaSimples<ProcedimentoInterface> guia){
		
		boolean isNegado = false;
		
		Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();
		for (ProcedimentoInterface procedimento : guia.getProcedimentos(SituacaoEnum.SOLICITADO.descricao())) {
			isNegado = procedimento.getAutorizado() != null  && !procedimento.getAutorizado();
			if(procedimento.isPassouPelaAutorizacao() && isNegado){
				procedimentos.add(procedimento);
			}
		}
		return procedimentos;
	}
	
	public GuiaCompleta selecionarGuiaAutorizacao(GuiaCompleta guia, UsuarioInterface usuario) throws ValidateException {
		Assert.isNotNull(guia, MensagemErroEnum.NENHUMA_GUIA_SELECIONADA.getMessage());
		Assert.isTrue(guia.isPossuiItensSolicitados(), "Esta guia não possui procedimentos especiais/pacotes para serem autorizados.");
		if (guia.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()))
			throw new RuntimeException("Caro usuário, não é possível autorizar exames/procedimentos em guias CANCELADAS.");
		guia.tocarObjetos();
		guia.setUsuarioDoFluxo(usuario);
		return guia;
	}
	
	public GuiaCompleta conferirDados(GuiaCompleta guia) throws Exception {
		ImplDAO.save(guia.getSegurado().getConsumoIndividual());
		salvarGuia(guia);
		return guia;
	}

	private <P extends ProcedimentoInterface> void autorizarProcedimentos(GuiaSimples guia,Collection<P> procedimentosDaGuia,Collection<P> procedimentosAutorizados, UsuarioInterface usuario) throws Exception{
		for(P procCirurgico : procedimentosDaGuia){
			boolean negaProc = true;
			for(P procedimentoAutorizado : procedimentosAutorizados){
				if(procCirurgico.equals(procedimentoAutorizado)){
					negaProc = false;
					break;
				}
			}
			if(negaProc){
				guia.negarProcedimento(procCirurgico);
				procCirurgico.getSituacao().setUsuario(usuario);
			}
			else
				procCirurgico.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), "Autorizado pelo auditor", new Date());
		}

	}
	
}