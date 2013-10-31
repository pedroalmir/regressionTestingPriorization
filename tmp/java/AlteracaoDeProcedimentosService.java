package br.com.infowaypi.ecarebc.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.utils.ProcedimentoUtils;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.NotEquals;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Service b�sico para altera��o dos procedimentos de uma guia
 * @author Marcus bOolean
 * @Changes Danilo Nogueira Portela
 */
public class AlteracaoDeProcedimentosService  extends CancelamentoGuiasService {
	
	public AlteracaoDeProcedimentosService(){
		super();
	}
	
	public GuiaSimples buscarGuiaCancelamentoDeProcedimento(String autorizacao, UsuarioInterface usuario) throws Exception {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("autorizacao", autorizacao));
		sa.addParameter(new NotEquals("situacao.descricao", SituacaoEnum.REALIZADO.descricao()));
		sa.addParameter(new NotEquals("situacao.descricao", SituacaoEnum.FATURADA.descricao()));
		sa.addParameter(new NotEquals("situacao.descricao", SituacaoEnum.CANCELADO.descricao()));
		GuiaSimples guia = sa.uniqueResult(GuiaSimples.class);
		
		Assert.isNotNull(guia, "Prezado(a) usuario(a), n�o foi encontrada uma guia com essa autoriza��o apta ao processo de altera��o de procedimentos.");
		
		//Situa��es n�o aceitas no Fluxo
		/*
		 * Pedro Almir: Altera��o nas situacoesNaoPermitidas:
		 * Todas as situa��es antes de Auditada podem entrar
		 * nesse fluxo.*/
		List<String> situacoesNaoPermitidas = Arrays.asList(SituacaoEnum.AUDITADO.descricao(),
															SituacaoEnum.FATURADA.descricao(),SituacaoEnum.INAPTO.descricao(),
															SituacaoEnum.GLOSADO.descricao(),SituacaoEnum.PAGO.descricao(),
															SituacaoEnum.CANCELADO.descricao(), SituacaoEnum.RECEBIDO.descricao());
		
		if(guia.isConsulta() || guia.isConsultaOdonto()) {
			throw new RuntimeException("N�o � permitido alterar guias de consulta.");
		} else if (!isGuiaExcecao(guia) && situacoesNaoPermitidas.contains(guia.getSituacao().getDescricao()))
			throw new RuntimeException(MensagemErroEnum.GUIA_INAPTA_PARA_ALTERACAO_PROCEDIMENTOS.getMessage(guia.getTipo(),guia.getSituacao().getDescricao()));
		
		guia.tocarObjetos();
		guia.setUsuarioDoFluxo(usuario);
		
		guia.setValorAnterior(guia.getValorTotal());
		
		return guia;
	}
	
	public boolean isGuiaExcecao(GuiaSimples guia){
		boolean isTratamentoOdontoEAuditado = guia.isExameOdonto() && guia.isSituacaoAtual(SituacaoEnum.AUDITADO.descricao());
		if (isTratamentoOdontoEAuditado){
			return true;
		}
		return false;
	}
	
	/**
	 * M�todo respons�vel por efetuar as altera��es nos procedimentos normais e cir�rgicos.
	 * @param <P>
	 * @param guia
	 * @param usuario
	 * @return
	 * @throws Exception
	 */
	public <P extends ProcedimentoInterface> GuiaSimples alterarProcedimentos(GuiaSimples guia, UsuarioInterface usuario) throws Exception {
		guia.tocarObjetos();
		guia.setUsuarioDoFluxo(usuario);
		
		for (P p : (Set<P>) guia.getProcedimentosParaAlteracao()) {
			Assert.isNotNull(p.getQuantidade(), "Informe a quantidade para o procedimento "+p.getProcedimentoDaTabelaCBHPM().getCodigo()+".");
			Assert.isTrue(p.getQuantidade().intValue() > 0, "A quantidade de um procedimento n�o pode ser menor que 1.");
			Assert.isTrue(p.getQuantidade().intValue() <= p.getProcedimentoDaTabelaCBHPM().getQuantidade(), "A quantidade de um procedimento de c�digo "+p.getProcedimentoDaTabelaCBHPM().getCodigo()+" possui quantidade superior a permitida.");
			p.setValorAtualDoProcedimento(BigDecimal.ZERO);
			p.setValorCoParticipacao(BigDecimal.ZERO);
			p.calcularCampos();
			p.aplicaValorAcordo();
		}
		
		if(guia.isInternacao()){
		    ProcedimentoUtils.aplicaDescontosDaViaDeAcessoComCalculoDobrado((GuiaInternacao)guia);
		}
		guia.calculaHonorariosInternos(usuario);		
		guia.recalcularValores();
		return guia;
	}
	
	public GuiaSimples conferirDados(GuiaSimples guia) throws Exception {
		if(guia.getPrestador() != null)
			ImplDAO.save(guia.getPrestador());
		
		ImplDAO.save(guia.getSegurado().getConsumoIndividual());
		ImplDAO.save(guia);
		
		return guia;
	}

}