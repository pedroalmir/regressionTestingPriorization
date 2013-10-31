package br.com.infowaypi.ecarebc.service.autorizacoes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.com.infowaypi.ecare.atendimentos.acordos.itensAcordos.ItemDiariaAuditoria;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.Observacao;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemGuia;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.atendimentos.enums.TipoConsultaEnum;
import br.com.infowaypi.ecarebc.atendimentos.enums.TipoCriticaEnum;
import br.com.infowaypi.ecarebc.atendimentos.validators.ManagerCritica;
import br.com.infowaypi.ecarebc.atendimentos.validators.ServiceApresentacaoCriticasFiltradas;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MensagemInformacaoEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.ecarebc.utils.ProcedimentoUtils;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

@SuppressWarnings({"rawtypes","unchecked"})
public class AutorizarInternacoes extends Service implements ServiceApresentacaoCriticasFiltradas {

	private static final int PERIODO_PARA_ZERAR_CUR = 12;

	public ResumoGuias buscarGuias(String autorizacao, String dataInicial, String dataFinal,Prestador prestador, Class<? extends GuiaInternacao> tipoInternacao) throws Exception {
		List<GuiaInternacao> guias = new ArrayList<GuiaInternacao>();
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("situacao.descricao",SituacaoEnum.SOLICITADO_INTERNACAO.descricao()));
		
		guias = super.buscarGuias(sa, autorizacao, null, dataInicial, dataFinal, prestador,  false,false, GuiaInternacao.class, GuiaSimples.DATA_DE_SITUACAO);
		
		Collections.sort(guias, new Comparator<GuiaInternacao>(){
			public int compare(GuiaInternacao g1, GuiaInternacao g2) {
				return g1.getDataMarcacao().compareTo(g2.getDataMarcacao());
			}
		});
		
		ResumoGuias<GuiaInternacao> resumo = new ResumoGuias<GuiaInternacao>(guias, ResumoGuias.SITUACAO_SOLICITADA_INTERNACAO,false);
		Assert.isNotEmpty(resumo.getGuias(), "Nenhuma guia foi encontrada!");
		return resumo;
	}
	
	public ResumoGuias buscarGuias(String autorizacao, String dataInicial, String dataFinal,Prestador prestador) throws Exception {
		return buscarGuias(autorizacao, dataInicial,dataFinal,prestador, GuiaInternacao.class);
	}
	
	public GuiaInternacao selecionarGuia(GuiaInternacao guia, UsuarioInterface usuario) {
		guia.tocarObjetos();
		guia.getSegurado().getConsultasPromocionais().size();
		if (guia.getGuiaOrigem()!=null)
			guia.getGuiaOrigem().getProcedimentos();
		guia.setUsuarioDoFluxo(usuario);
		guia.getProcedimentosCirurgicosTemp().addAll(guia.getProcedimentosCirurgicos());
		
		filtrarCriticasApresentaveis(guia);
		
		return guia;
	}

	public GuiaCompleta autorizarInternacao(Boolean autorizar, GuiaCompleta<ProcedimentoInterface> guia, Collection<ItemDiariaAuditoria> diariasInseridas, String observacao, UsuarioInterface usuario) throws Exception{

		
		if(!autorizar && Utils.isStringVazia(observacao)){
			throw new ValidateException("Deve ser informado uma justificativa para a não autorização da guia.");
		}
		
		if(!autorizar){ 
			guia.mudarSituacao(usuario, SituacaoEnum.NAO_AUTORIZADO.descricao(), MotivoEnum.SOLICITACAO_NAO_AUTORIZADA.getMessage(), new Date());
			
			for (ItemGuia item : guia.getItensDiaria()) {
				if(item.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())){
					item.mudarSituacao(usuario, SituacaoEnum.NAO_AUTORIZADO.descricao(), MotivoEnum.NAO_AUTORIZADO_AUDITOR.getMessage(), new Date());
				}
			}
			
		} else {
			boolean existeDiaria = false;
			for (ItemDiaria diaria : guia.getDiariasSolicitadas()) {
				if(diaria.isAutorizado()){
					existeDiaria = true;
				}
				Assert.isNotNull(diaria.getJustificativaNaoAutorizacao(), MensagemErroEnum.MOTIVO_AUTORIZACAO_ACOMODACAO_REQUERIDO.getMessage(diaria.getDiaria().getCodigoDescricao()));
			}
			
			if(!existeDiaria && diariasInseridas.isEmpty()){
				throw new ValidateException("Necessário informar pelo menos uma diária autorizada.");
			}
			
			validarProcedimentos(guia);
			
			for (ItemDiariaAuditoria itemDiariaAuditoria : diariasInseridas) {
				itemDiariaAuditoria.mudarSituacao(usuario, SituacaoEnum.SOLICITADO.descricao(), MotivoEnum.INCLUIDO_PELO_AUDITOR.getMessage(), new Date());
				itemDiariaAuditoria.setAutorizado(true);
				itemDiariaAuditoria.setJustificativa(MotivoEnum.INCLUIDO_PELO_AUDITOR.getMessage());
				guia.addItemDiaria(itemDiariaAuditoria);
			}
			
			for (ItemDiaria diaria : guia.getItensDiaria()) {
				if(diaria.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao()) && diaria.isAutorizado()){
					diaria.calculaDataInicial();
					diaria.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.INCLUIDO_PELO_AUDITOR.getMessage(), new Date());
				}else if (diaria.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao()) && !diaria.isAutorizado()) {
					diaria.mudarSituacao(usuario, SituacaoEnum.NAO_AUTORIZADO.descricao(), MotivoEnum.NAO_AUTORIZADO_AUDITOR.getMessage(), new Date());
				}
			}
			
			for (ProcedimentoCirurgicoInterface procedimento : guia.getProcedimentosCirurgicos()) {
				procedimento.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZACAO_PROCEDIMENTO_CIRURGICO.getMessage(), new Date());
			}
			
			if(guia.isInternacaoUrgencia()){
				guia.mudarSituacao(usuario, SituacaoEnum.ABERTO.descricao(), MotivoEnum.PACIENTE_INTERNACAO_URGENCIA.getMessage(), new Date());
			} else {
				guia.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.INTERNACAO_ELETIVA.getMessage(), new Date());
			}
			
			for (ItemPacote item : guia.getItensPacote()) {
				if(item.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())){
					item.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZADO_PELO_AUDITOR.getMessage(), new Date());
				}
			}
		}
		
		guia.setValorAnterior(guia.getValorTotal());
		
		if (guia.getGuiaOrigem() != null){
			atualizarGuiaOrigem(guia);
		}
		
		inserirObservacao(guia, observacao, usuario);
		
		ProcedimentoUtils.aplicaDescontoDaViaDeAcesso(guia);
		
		corrigirValorProcedimentos(guia);
		
		processaSituacaoCriticas(guia);
		
		return guia;
	}

	public GuiaCompleta autorizarInternacao(GuiaCompleta<ProcedimentoInterface> guia, String observacao, UsuarioInterface usuario) throws Exception {
		
		guia.setValorAnterior(guia.getValorTotal());
		
		boolean isAutorizado = guia.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao());
		boolean isNaoAutorizado = guia.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao()); 
		boolean isAberto = guia.isSituacaoAtual(SituacaoEnum.ABERTO.descricao());

		if(!isAutorizado && !isNaoAutorizado && !isAberto)
			throw new RuntimeException("Prezado usuário, por favor informe se a internação será autorizada ou não.");
			
		if(isNaoAutorizado){
			if(Utils.isStringVazia(observacao)){
				removerSituacao(guia);
				throw new ValidateException("Deve ser informado uma justificativa para a não autorização da guia.");
			}
		}
		
		boolean isAutorizadoOuAberto = isAutorizado || isAberto;
		
		
		if (isAutorizadoOuAberto) {
			try {
				validarProcedimentos(guia);
			} catch (Exception e) {
						removerSituacao(guia);
				throw e;
					}

			if (guia.getGuiaOrigem() != null){
				atualizarGuiaOrigem(guia);
			}

		}
			
		inserirObservacao(guia, observacao, usuario);
		
		ProcedimentoUtils.aplicaDescontoDaViaDeAcesso(guia);
		
		corrigirValorProcedimentos(guia);
		
		processaSituacaoCriticas(guia);
		
		return guia;
	}

	private void corrigirValorProcedimentos(GuiaCompleta<ProcedimentoInterface> guia) {
		for (ProcedimentoInterface procedimento : guia.getProcedimentosCirurgicosNaoCanceladosENegados()) {
			guia.corrigeValorProcedimentoInclusoEmPacoteQueInclueHonorario(procedimento);
		}
	}

	private void inserirObservacao(GuiaCompleta<ProcedimentoInterface> guia, String observacao, UsuarioInterface usuario) {
		if (StringUtils.isNotEmpty(observacao)) {
			Observacao observ = new Observacao(observacao,usuario);
			guia.addObservacao(observ);
		}
	}

	private void atualizarGuiaOrigem(GuiaCompleta<ProcedimentoInterface> guia) {
		GuiaSimples<ProcedimentoInterface> guiaOrigem = guia.getGuiaOrigem();
		if (guiaOrigem.isConsultaUrgencia()) {
					
			guiaOrigem = ImplDAO.findById(guiaOrigem.getIdGuia(), GuiaConsultaUrgencia.class);
			guia.setGuiaOrigem(guiaOrigem);
					
			for (ProcedimentoInterface procedimentoDaConsultaOrigem : guiaOrigem.getProcedimentos()) {
				if (procedimentoDaConsultaOrigem.getProcedimentoDaTabelaCBHPM().getCodigo().equals(TipoConsultaEnum.URGENCIA.getCodigo())) {
					Calendar agora = Calendar.getInstance();
					Calendar dataAutorizacao = Calendar.getInstance();
					dataAutorizacao.setTime(guiaOrigem.getSituacao(Constantes.SITUACAO_ABERTO).getDataSituacao());
					if (Utils.diferencaEmHoras(dataAutorizacao, agora) <= PERIODO_PARA_ZERAR_CUR) {
						guiaOrigem.setValorTotal(guiaOrigem.getValorTotal().subtract(procedimentoDaConsultaOrigem.getValorTotal()));
						procedimentoDaConsultaOrigem.setValorAtualDoProcedimento(MoneyCalculation.rounded(BigDecimal.ZERO));
						procedimentoDaConsultaOrigem.setValorCoParticipacao(MoneyCalculation.rounded(BigDecimal.ZERO));
						((GuiaConsultaUrgencia)guiaOrigem).addObservacao(new Observacao(MensagemInformacaoEnum.ZERAR_INTERNACAO_URGENCIA.descricao(),null));
						guiaOrigem.updateValorCoparticipacao();
					}
					break;
				}
			}
		}
	}

	private void validarProcedimentos(GuiaCompleta<ProcedimentoInterface> guia) {
		
		Integer count = 0;
		
		for (ProcedimentoCirurgicoInterface procedimento : guia.getProcedimentosCirurgicosTemp()) {
			boolean porcentagem100 = procedimento.getPorcentagem().compareTo(Constantes.PORCENTAGEM_100) == 0;
			boolean passouPelaAutorizacao = procedimento.isPassouPelaAutorizacao();
			if (passouPelaAutorizacao && porcentagem100) {
				count++;
				if (count > 1) {
					throw new RuntimeException("Caro usuário, não é permitido autorização de mais de um procedimento cirurgico 100%");
				}
			}
			if (guia.containsProcedimentosCirurgicosIguaisEComMesmaPorcentagem(procedimento)){
				throw new RuntimeException("Caro usuário, o procedimento "+procedimento.getProcedimentoDaTabelaCBHPM().getCodigo()
						+" já possui a porcentagem "+procedimento.getPorcentagemFormatada()
						+". Insira uma porcentagem diferente.");
			}
		}
		
		if(!guia.getProcedimentosCirurgicosTemp().isEmpty() && count < 1) {
			throw new RuntimeException("Caro usuário, é necessário que ao menos um procedimento cirurgico seja de 100%");
		}
		
	}

	private void removerSituacao(GuiaCompleta guia) {
		if(guia.getSituacao().getIdSituacao() == null){
			SituacaoInterface situacaoAtual = guia.getSituacao();
			guia.setSituacao(guia.getSituacaoAnterior(situacaoAtual));
			guia.getSituacoes().remove(situacaoAtual);
		}
	}
	
	/**
	 * aplica desconto em cima do valor moderado do procedimento
	 * @param guia
	 */
	public void aplicaDescontoDaViaDeAcesso(GuiaCompleta<ProcedimentoInterface> guia) {
		
		BigDecimal diferencaProcedimentos = BigDecimal.ZERO;
		for (ProcedimentoCirurgico p : guia.getProcedimentosCirurgicos()) {
			if(p.getPorcentagem() == null)
				p.setPorcentagem(new BigDecimal(100));
			
			//recalcula valor do procedimento e aplica acordo se tiver algum
			p.setValorAtualDoProcedimento(BigDecimal.ZERO);
			p.calcularCampos();
			p.aplicaValorAcordo();
			//fim 
			
			BigDecimal valorTotal = p.getValorAtualDoProcedimento();//p.valorAtualDoProcedimento;
			BigDecimal porcentagem = p.getPorcentagem().divide(new BigDecimal(100));
			BigDecimal valorAtualizado = p.getValorTotal().multiply(porcentagem);
			p.setValorAtualDoProcedimento(valorAtualizado);
			
			diferencaProcedimentos = diferencaProcedimentos.add(valorTotal.subtract(valorAtualizado)); 
		}
		guia.setValorTotal(guia.getValorTotal().subtract(diferencaProcedimentos));
	}
	
	public void finalizar(GuiaCompleta guia) throws Exception {
	}
	
	public GuiaCompleta conferirDados(GuiaCompleta guia) throws Exception {
		ImplDAO.save(guia);
		if (guia.getGuiaOrigem()!=null)
			ImplDAO.save(guia.getGuiaOrigem());
		
		/* if_not[REMOVE_OBRIGATORIEDADE_DE_INFORMAR_PRESTADOR_PARA_INTERNACAO] */
		ImplDAO.save(guia.getPrestador().getConsumoIndividual());
		/* end[REMOVE_OBRIGATORIEDADE_DE_INFORMAR_PRESTADOR_PARA_INTERNACAO] */
		
		ImplDAO.save(guia.getSegurado().getConsumoIndividual());
		
		//Atualização do consumo financeiro
//		StateMachineConsumo.updateConsumoAtendimento(guia, true, true);
		return guia;
	}

	@Override
	public void filtrarCriticasApresentaveis(GuiaSimples<?> guia) {
		ManagerCritica.processaApresentaveis(guia, TipoCriticaEnum.CRITICA_DLP_SUBGRUPO.valor(), TipoCriticaEnum.CRITICA_DLP_CID.valor());
	}

	@Override
	public void processaSituacaoCriticas(GuiaSimples<?> guia) {
		ManagerCritica.processaSituacao(guia);
	}
}
