package br.com.infowaypi.ecare.services;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.enums.MotivoEnumSR;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.LoteDeGuias;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GuiaHonorarioMedico;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.painelDeControle.PainelDeControle;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

public class ReceberLote {

	public LoteDeGuias buscarLote(String identificador) throws ValidateException {
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("identificador", identificador.trim()));
		
		LoteDeGuias lote  = sa.uniqueResult(LoteDeGuias.class);
		
		if (lote == null){
			throw new ValidateException(MensagemErroEnumSR.LOTE_NAO_ENCONTRADO.getMessage(identificador));
		}
		
		if (lote.getSituacao().getDescricao().equals(SituacaoEnum.RECEBIDO.descricao())){
			throw new ValidateException(MensagemErroEnumSR.LOTE_JA_RECEBIDO.getMessage(identificador));
		}
		
		tocarObjetos(lote);
		
		return lote;
		
	}
	
	public LoteDeGuias mostrarLote(LoteDeGuias lote, UsuarioInterface usuario) throws ValidateException {
		lote.validate();
		BigDecimal valorDoLote = lote.getValorTotal();
		Date newDate = new Date();
		for (GuiaSimples<ProcedimentoInterface> guia : lote.getGuiasEnviadas()) {
			
			String motivo = (guia.getMotivo() == null)? "":guia.getMotivo().getDescricao() ;
			
			if(guia.getRecebido() == null) {
				throw new RuntimeException(MensagemErroEnumSR.OPCAO_NAO_MARCADA.getMessage());
			}else if(guia.getRecebido()) {
				guia.receberGuia(usuario);
				lote.getGuiasRecebidas().add(guia);
			}else {
				if (Utils.isStringVazia(motivo)){
					throw new RuntimeException(MensagemErroEnumSR.MOTIVO_DEVOLUCAO_REQUERIDO.getMessage(guia.getAutorizacao()));
				}

				valorDoLote = valorDoLote.subtract(guia.getValorTotal());
				guia.mudarSituacao(usuario, SituacaoEnum.DEVOLVIDO.descricao(), motivo, newDate);
				lote.getGuiasDevolvidas().add(guia);
			}
		}
		this.setarMultaPorAtrasoDeRecebimento(lote.getGuiasRecebidas());
		lote.setValorTotal(valorDoLote);
		lote.mudarSituacao(usuario, SituacaoEnum.RECEBIDO.descricao(), MotivoEnumSR.LOTE_RECEBIDO.getMessage(), newDate);
		lote.atualizaValorTotalApresentado();
		return lote;
	}
	
	/**
	 * Seta nas guias o valor da porcentagem da multa, caso seja sejam recebidas após o prazo
	 */
	public void setarMultaPorAtrasoDeRecebimento(Collection<GuiaSimples<ProcedimentoInterface>> guias) throws ValidateException {
		int prazoDeEntregaDeLoteSemMulta 	= PainelDeControle.getPainel().getPrazoDeEntregaDeLoteSemMulta();
		BigDecimal multaPorAtrasoDeEntrega 	= PainelDeControle.getPainel().getMultaPorAtrasoDeEntregaDeLote();
		Calendar dataInicioPrazo 			= Calendar.getInstance();
		
		for (GuiaSimples<ProcedimentoInterface> guia : guias) {
			dataInicioPrazo.setTime(guia.getDataInicioPrazoRecebimento());
			int diferencaEmDias = Utils.diferencaEmDias(dataInicioPrazo, Calendar.getInstance());
			
			Date dataVigenciaPrazoFinalEntregaDeLote = PainelDeControle.getPainel().getDataVigenciaPrazoFinalEntregaDeLote();
			
			boolean entraNaVigenciaDeMulta = Utils.compareData(dataInicioPrazo.getTime(), dataVigenciaPrazoFinalEntregaDeLote) >= 0;
			
			if (entraNaVigenciaDeMulta && (diferencaEmDias > prazoDeEntregaDeLoteSemMulta)){
				guia.setMultaPorAtrasoDeEntrega(multaPorAtrasoDeEntrega);
				guia.recalcularValores();
			}
		}
	}
	
	public LoteDeGuias salvarLote(LoteDeGuias lote) throws Exception {
		ImplDAO.save(lote);
		return lote;
	}
	
	public void finalizar(LoteDeGuias lote) {}

	private void tocarObjetos(LoteDeGuias lote) {
		//tocar objetos
		lote.getPrestador().tocarObjetos();
		lote.getSituacoes().size();
		
		for (GuiaSimples<ProcedimentoInterface> guia : lote.getGuiasDevolvidas()) {
			incializaColecoes(guia);
		}
		
		for (GuiaSimples<ProcedimentoInterface> guia : lote.getGuiasRecebidas()) {
			incializaColecoes(guia);
		}
		
		for (GuiaSimples<ProcedimentoInterface> guia : lote.getGuiasEnviadas()) {
			guia.setRecebido(true);
			incializaColecoes(guia);
		}
	}

	private void incializaColecoes(GuiaSimples<ProcedimentoInterface> guia) {
		guia.getRecebido();
		guia.getSituacoes().size();
		guia.getValorCoParticipacao();
		guia.getHonorarios().size();
		
		if(guia.isCompleta()){
			GuiaCompleta guiaCompleta = (GuiaCompleta)guia;
			guiaCompleta.getItensTaxa().size();
			guiaCompleta.getItensDiaria().size();
			guiaCompleta.getItensGasoterapia().size();
			guiaCompleta.getItensPacote().size();
			guiaCompleta.getProcedimentos().size();
			guiaCompleta.getItensGuiaFaturamento().size();
			guiaCompleta.tocarOpmes();
		}else if (guia.isHonorarioMedico()) {
			GuiaHonorarioMedico guiaHonorario = (GuiaHonorarioMedico)guia;
			guia.getProcedimentos().size();
		}

	}
}
