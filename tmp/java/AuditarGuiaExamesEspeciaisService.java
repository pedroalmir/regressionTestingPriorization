package br.com.infowaypi.ecarebc.service.autorizacoes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecare.cadastros.MotivoGlosa;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.Observacao;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoAuditoriaExames;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.ecarebc.utils.CommandCorrecaoValorGuia;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.molecular.parameter.Not;
import br.com.infowaypi.molecular.parameter.OrderBy;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Service para auditoria de guias de exames especiais do plano de saúde
 * @author Marcus bOolean
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class AuditarGuiaExamesEspeciaisService extends Service {
	
	public AuditarGuiaExamesEspeciaisService(){
		super();
	}
	
	/**
	 * Busca guias de exame que possuem mat/med para serem auditadas.
	 * @param autorizacao
	 * @param dataInicial
	 * @param dataFinal
	 * @param prestador
	 * @return
	 * @throws Exception
	 */
	public ResumoGuias<GuiaSimples> buscarGuias(String autorizacao, String dataInicial, String dataFinal,Prestador prestador) throws Exception {
		List<GuiaSimples> guias = new ArrayList<GuiaSimples>();
		SearchAgent sa = new SearchAgent();
		
		Collection situacoes = null;
		if(prestador != null){
			if(prestador.isExigeEntregaLote()){
				situacoes = Arrays.asList(SituacaoEnum.RECEBIDO.descricao(), SituacaoEnum.AUDITADO.descricao());
			} else {
				situacoes = Arrays.asList(SituacaoEnum.FECHADO.descricao(), SituacaoEnum.AUDITADO.descricao());
			}
		}else {
			situacoes = Arrays.asList(SituacaoEnum.FECHADO.descricao(), SituacaoEnum.RECEBIDO.descricao(), SituacaoEnum.AUDITADO.descricao());
		}
		sa.addParameter(new In("situacao.descricao",situacoes));
		sa.addParameter(new Not(new Equals("tipoDeGuia","GEXOD")));
		sa.addParameter(new OrderBy("dataTerminoAtendimento"));
		
		guias = super.buscarGuias(sa, autorizacao, null, dataInicial, dataFinal, prestador,  true,false, GuiaSimples.class, GuiaSimples.DATA_DE_TERMINO);
		
		ResumoGuias<GuiaSimples> resumo = new ResumoGuias<GuiaSimples>(guias, ResumoGuias.SITUACAO_TODAS,false);
		
		Assert.isNotEmpty(resumo.getGuiasExamesEspeciaisParaAuditoria(), MensagemErroEnum.GUIAS_NAO_ENCONTRADAS_PARA_AUDITORIA.getMessage());
		return resumo;
	}
	
	public GuiaSimples selecionarGuia(GuiaSimples guia, UsuarioInterface usuario) {
		guia.tocarObjetos();
		guia.setUsuarioDoFluxo(usuario);
		return guia;
	}

	public GuiaExame auditarGuia(Boolean glosarGuia, MotivoGlosa motivoGlosa, Collection<ProcedimentoAuditoriaExames> procedimentosAuditoria, 
								GuiaExame<Procedimento> guia, String observacao,  UsuarioInterface usuario) throws Exception {
		
		GuiaExame guiaExame = guia;
		
		validaMotivoGlosaProcedimentos(guia);
		
		for (Procedimento procedimentoAuditoriaExames : procedimentosAuditoria) {
			guia.addProcedimento(procedimentoAuditoriaExames);
		}
		
		validaGlosaTodosProcedimentos(guia);
		
		if(glosarGuia) {
			if (motivoGlosa == null) {
				throw new ValidateException(MensagemErroEnum.CAMPO_MOTIVO_DE_GLOSA_REQUERIDO.getMessage());
			}
			else {
				guia.setMotivoGlosa(motivoGlosa);
				if(!Utils.isStringVazia(observacao)) {
					guia.setMotivoParaGlosaTotal(observacao);
				}
				guia.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), MotivoEnum.GUIA_GLOSADA_DURANTE_AUDITORIA.getMessage(), new Date());
				addObservacao(observacao, guia, usuario);
				return guia;
			}
		}else {
			for(Procedimento procedimento: guia.getProcedimentosNaoRealizadosNaoNaoAutorizadosENaoSolicitados()){
				if (procedimento.isGlosar()){
					procedimento.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), procedimento.getMotivoGlosaProcedimento().getDescricao(), new Date());
				}
			}
		}
		
		BigDecimal valorAnterior = guiaExame.getValorTotal();
		guiaExame.setValorAnterior(valorAnterior);
		
		guia.mudarSituacao(usuario, SituacaoEnum.AUDITADO.descricao() , MotivoEnum.GUIA_AUDITADA.getMessage(), new Date());
		
		addObservacao(observacao, guia, usuario);
		
		CommandCorrecaoValorGuia command = new CommandCorrecaoValorGuia(guiaExame);
		command.execute();
		return guia;
	}

	private void addObservacao(String observacao, GuiaExame<Procedimento> guia, UsuarioInterface usuario) {
		if (observacao != null && !observacao.isEmpty()){
			Observacao obs = new Observacao(new Date(), observacao, usuario);
			guia.addObservacao(obs);
			obs.setGuia(guia);
		}	
	}

	private void validaGlosaTodosProcedimentos(GuiaExame<Procedimento> guia) {
		int quantProcedimentosValidos = guia.getProcedimentosNaoRealizadosNaoNaoAutorizadosENaoSolicitados().size();
		int quantProcedimentosMarcadosParaGlosar = 0;
		for(Procedimento procedimento: guia.getProcedimentosNaoRealizadosNaoNaoAutorizadosENaoSolicitados()){
			if (procedimento.isGlosar()){
				quantProcedimentosMarcadosParaGlosar++;
			}
		}
		
		boolean todosOsProcedimentosGlosados = quantProcedimentosValidos == quantProcedimentosMarcadosParaGlosar;
		boolean temValorMatMed = MoneyCalculation.compare(guia.getValorMaterialComplementarAuditado(), BigDecimal.ZERO) != 0
								 || MoneyCalculation.compare(guia.getValorMedicamentoComplementarAuditado(), BigDecimal.ZERO) != 0;
		
		Assert.isFalse((todosOsProcedimentosGlosados && !temValorMatMed), MensagemErroEnum.GUIA_DEVE_SER_GLOSADA_TOTALMENTE.getMessage());
		
	}

	private void validaMotivoGlosaProcedimentos(GuiaExame<Procedimento> guia)
			throws ValidateException {
		for(Procedimento procedimento: guia.getProcedimentosNaoRealizadosNaoNaoAutorizadosENaoSolicitados()){
			if (procedimento.isGlosar()){
				if (procedimento.getMotivoGlosaProcedimento() == null){
					throw new ValidateException(MensagemErroEnum.PROCEDIMENTO_GLOSADO_SEM_MOTIVO_DE_GLOSA.getMessage(procedimento.getProcedimentoDaTabelaCBHPM().getCodigoEDescricao()));
				}
			}
		}
	}
}
