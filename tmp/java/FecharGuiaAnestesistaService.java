package br.com.infowaypi.ecare.services.internacao;

import java.math.BigDecimal;
import java.util.Date;

import br.com.infowaypi.ecare.associados.PrestadorAnestesista;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

//	select * from associados_prestador  where idPrestador = '373690'
//	select * from procedimentos_procedimento where tipoprocedimento = 'PC'
//
//	update procedimentos_procedimento set tipoprocedimento = 'PCSR' where tipoprocedimento = 'PC'
//	update associados_prestador set discriminator = 'PA' where idPrestador = '373690'

public class FecharGuiaAnestesistaService extends Service{

	public GuiaSimples buscarGuia(String autorizacao, Prestador prestador){
		
		SearchAgent sa  = new SearchAgent();
		sa.addParameter(new Equals("autorizacao",autorizacao.trim()));
		GuiaSimples<Procedimento> guia = (GuiaSimples) sa.createCriteriaFor(GuiaSimples.class).uniqueResult();

		Assert.isNotNull(guia, MensagemErroEnum.NENHUMA_GUIA_ENCONTRADA.getMessage());
		
//		Assert.isTrue(guia.isInternacao(), MensagemErroEnum.GUIA_NAO_E_DE_INTERNACAO.getMessage());
		if(!guia.isAptaParaFechamentoAnestesista())
			validaSituacoes(guia);
		
		tocarObjetos(guia);
		for (ProcedimentoInterface procedimento : guia.getProcedimentos()) {
			procedimento.atualizaValorAnestesista();
		}
		Assert.isTrue(guia.isPossuiItemParaFechamentoAnestesista(), "Não existe nenhum PROCEDIMENTO nessa guia para ser fechado.");
		return guia;
		
	}

	private void tocarObjetos(GuiaSimples<Procedimento> guia) {
		guia.tocarObjetos();
		for (ProcedimentoInterface procedimento : guia.getProcedimentos()) {
			if(procedimento.getPrestadorAnestesista() != null){ 
				procedimento.getPrestadorAnestesista().getNome();
			}
		}
	}
	
	public GuiaSimples fecharGuia(GuiaSimples<ProcedimentoInterface> guia){
		
		BigDecimal valorParcialAnestesista = guia.getValorTotalAnestesista();
		guia.setValorParcialAnestesista(valorParcialAnestesista);
		for (ProcedimentoInterface procedimento : guia.getProcedimentosParaFechamentoAnestesista()) {
			if(!procedimento.isSituacaoAtual(SituacaoEnum.FATURADA.descricao())){
				procedimento.atualizaValorAnestesista();
			}
		}
		
		return guia;
	}
	
	@Override
	protected <G extends GuiaSimples> void finalizar(G guia) {
		super.finalizar(guia);
	}
	
	public GuiaSimples salvarGuia(GuiaSimples<Procedimento> guia, Prestador prestador, UsuarioInterface usuario) throws Exception {

		PrestadorAnestesista prestadorAnestesista = (PrestadorAnestesista)prestador;

		BigDecimal valorParcialAnestesista = guia.getValorTotalAnestesista();
		guia.setValorParcialAnestesista(valorParcialAnestesista);
		
		for (ProcedimentoInterface procedimento : guia.getProcedimentosParaFechamentoAnestesista()) {
			
			boolean isAutorizado = procedimento.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao());
			boolean isConfirmado = procedimento.isSituacaoAtual(SituacaoEnum.CONFIRMADO.descricao());
			boolean isRealizado = procedimento.isSituacaoAtual(SituacaoEnum.REALIZADO.descricao());
			if(isAutorizado || isConfirmado || isRealizado){
				procedimento.mudarSituacao(usuario,SituacaoEnum.FECHADO.descricao(), MotivoEnum.FECHAMENTO_GUIA_ANESTESISTA.getMessage(), new Date());
				procedimento.setPrestadorAnestesista(prestadorAnestesista);
			}
		}
		
//		for (ItemPacote itemPacote : guia.getItensPacote()) {
//			if(itemPacote.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao())){
//				itemPacote.mudarSituacao(usuario, SituacaoEnum.FECHADO.descricao(), MotivoEnum.FECHAMENTO_GUIA_ANESTESISTA.getMessage(), new Date());
//				prestadorAnestesista.addItemPacote(itemPacote);
//			}
//		}

		ImplDAO.save(guia);
		
		//Atualizando o índice de guias com o valor dos procedimentos da coopanest
//		StateMachineConsumo.updateCustoAnestesista(guia, true);
		
		return guia;
	}

	
	
	private void validaSituacoes(GuiaSimples<Procedimento> guia) {
		
		if(guia.isSituacaoAtual(SituacaoEnum.SOLICITADO_INTERNACAO.descricao()))
			throw new RuntimeException("Não é permitido fechar guias de internação que não estão confirmadas.");
		
		if(guia.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()))
			throw new RuntimeException("Não é permitido fechar guias de internação canceladas.");
		
		if(!guia.isPossuiItemParaFechamentoAnestesista())
			throw new RuntimeException(MensagemErroEnum.GUIA_SEM_PROCEDIMENTOS_PARA_FLUXO.getMessage("","FECHADOS"));
		
	}
	
}
