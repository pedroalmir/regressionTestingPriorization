package br.com.infowaypi.ecare.services;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import org.apache.struts.action.ActionForward;

import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.enums.MotivoEnumSR;
import br.com.infowaypi.ecare.services.recurso.GuiaRecursoGlosa;
import br.com.infowaypi.ecarebc.atendimentos.LoteDeGuias;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.painelDeControle.PainelDeControle;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

public class ReceberLoteGRG {

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
		lote.validateGRG();
		BigDecimal valorDoLote = lote.getValorTotal();
		Date now = new Date();
		
		for (GuiaRecursoGlosa guia : lote.getGuiasDeRecursoEnviadas()) {
			String motivo = (guia.getMotivoDevolucaoLote() == null)? "" : guia.getMotivoDevolucaoLote().getDescricao() ;

			if(guia.isRecebido() == null) {
				throw new RuntimeException(MensagemErroEnumSR.OPCAO_NAO_MARCADA.getMessage());
			}else if(guia.isRecebido()) {
				guia.receberGuia(usuario);
				lote.getGuiasDeRecursoRecebidas().add(guia);
			}else {
				if (Utils.isStringVazia(motivo)){
					throw new RuntimeException(MensagemErroEnumSR.MOTIVO_DEVOLUCAO_REQUERIDO.getMessage(guia.getAutorizacao()));
				}

				valorDoLote = valorDoLote.subtract(guia.getValorTotal());
				guia.mudarSituacao(usuario, SituacaoEnum.DEVOLVIDO.descricao(), motivo, now);
				lote.getGuiasDeRecursoDevolvidas().add(guia);
			}
		}
		this.setarMultaPorAtrasoDeRecebimento(lote.getGuiasDeRecursoRecebidas());
		lote.setValorTotal(valorDoLote);
		lote.mudarSituacao(usuario, SituacaoEnum.RECEBIDO.descricao(), MotivoEnumSR.LOTE_RECEBIDO.getMessage(), now);
		
		return lote;
	}
	
	/**
	 * Seta nas guias o valor da porcentagem da multa, caso seja sejam recebidas após o prazo
	 */
	public void setarMultaPorAtrasoDeRecebimento(Set<GuiaRecursoGlosa> guias) throws ValidateException {
		int prazoDeEntregaDeLoteSemMulta 	= PainelDeControle.getPainel().getPrazoDeEntregaDeLoteSemMulta();
		BigDecimal multaPorAtrasoDeEntrega 	= PainelDeControle.getPainel().getMultaPorAtrasoDeEntregaDeLote();
		Calendar dataInicioPrazo 			= Calendar.getInstance();
		
		for (GuiaRecursoGlosa guia : guias) {
			dataInicioPrazo.setTime(guia.getDataRecurso());
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
		
		for (GuiaRecursoGlosa guia : lote.getGuiasDeRecursoDevolvidas()) {
			incializaColecoes(guia);
		}
		
		for (GuiaRecursoGlosa guia : lote.getGuiasDeRecursoRecebidas()) {
			incializaColecoes(guia);
		}
		
		for (GuiaRecursoGlosa guia : lote.getGuiasDeRecursoEnviadas()) {
			guia.setRecebido(true);
			incializaColecoes(guia);
		}
	}

	private void incializaColecoes(GuiaRecursoGlosa guia) {
		guia.getGuiaOrigem();
		guia.getSegurado().getPessoaFisica().getNome();
		guia.getSituacoes().size();
	}
}
