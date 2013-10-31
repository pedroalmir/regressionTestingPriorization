package br.com.infowaypi.ecarebc.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.painelDeControle.PainelDeControle;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.ecarebc.procedimentos.TipoAcomodacao;
import br.com.infowaypi.msr.utils.MoneyCalculation;

/**
 * 
 * Executa a atualização do valor de procedimentos cirúrgicos (dobra) em GuiaInternação com solicitações de ItemDiaria
 * com TipoAcomodacao configurados no PainelDeControle  
 * 
 * @author Leonardo Sampaio
 * @since 14/08/2012
 *
 */
public  class CommandCorrecaoCalculoValorProcedimento{

	private GuiaSimples<ProcedimentoInterface> guia;
	
	private List<String> situacoes = new ArrayList<String>();
	
	public CommandCorrecaoCalculoValorProcedimento(GuiaSimples<ProcedimentoInterface> guia) {
	    
	    situacoes.add(SituacaoEnum.CANCELADO.descricao());
		situacoes.add(SituacaoEnum.SOLICITADO_INTERNACAO.descricao());
		situacoes.add(SituacaoEnum.AUTORIZADO.descricao());
		situacoes.add(SituacaoEnum.NAO_AUTORIZADO.descricao());
		situacoes.add(SituacaoEnum.ABERTO.descricao());
		situacoes.add(SituacaoEnum.SOLICITADO_PRORROGACAO.descricao());
		situacoes.add(SituacaoEnum.PRORROGADO.descricao());
		situacoes.add(SituacaoEnum.NAO_PRORROGADO.descricao());
		situacoes.add(SituacaoEnum.ALTA_REGISTRADA.descricao());
		situacoes.add(SituacaoEnum.FECHADO.descricao());
		situacoes.add(SituacaoEnum.ENVIADO.descricao());
		situacoes.add(SituacaoEnum.RECEBIDO.descricao());
		situacoes.add(SituacaoEnum.DEVOLVIDO.descricao());
		situacoes.add(SituacaoEnum.AUDITADO.descricao());
		situacoes.add(SituacaoEnum.GLOSADO.descricao());
		
		this.guia = guia;
	}
	
	public void execute(){
	    if(guia.isInternacao() && guia.getSituacao()!=null && situacoes.contains(guia.getSituacao().getDescricao())){
	    	for (ProcedimentoInterface procedimento : guia.getProcedimentos()) {
	    		calcularCampos(procedimento);
	    	}
	    }
	}
	
	/**
	 * Recalcula valor atual e de moderação do procedimento passado como parâmetro.				<br>
	 * 													<br>
	 * Esse método tem comportamento semelhante ao método {@link Procedimento.calcularCampos}, no entanto	
	 * é sempre invocado ao ser realizada uma <b>alteração</b> em uma {@link GuiaInternacao}, ao contrário
	 *  do primeiro que só é invocado em situações específicas.						<br>
	 *													<br>
	 * Caso PainelDeControle.valoradoPeloPorte == true e o metodo Procedimento.calcularCampos() ja tenha sido 
	 * chamado o valorAtualDoProcedimento nao será mais zero, será o valor "original" do procedimento calculado
	 *  a partir do valorModerado. Isso garante que os procedimentos recém-incluídos sejam dobrados.	<br>
	 * 													<br>
	 * <b>Caso o valorAtualDoProcedimento tenha sido calculado anteriormente usando outra lógica, 
	 * não será dobrado o valor.</b>
	 *
	 * @param procedimento Procedimento
	 */
	private void calcularCampos(ProcedimentoInterface procedimento){
	    
	    TabelaCBHPM procedimentoDaTabelaCBHPM = procedimento.getProcedimentoDaTabelaCBHPM();

    	if(procedimentoDaTabelaCBHPM != null){
	
    		List<TipoAcomodacao> tiposDeAcomodacao = null;

    		@SuppressWarnings("unchecked")
    		GuiaSimples<ProcedimentoInterface> guia = procedimento.getGuia();

    		if (guia!=null && guia instanceof GuiaCompleta) {
    			tiposDeAcomodacao = getTiposDeAcomodacaoDasDiariasSolicitadasOuAutorizadas(((GuiaCompleta<ProcedimentoInterface>) guia));
    		}
	
    		if((procedimento.getValorAtualDoProcedimento() != null && MoneyCalculation.compare(procedimento.getValorAtualDoProcedimento(), BigDecimal.ZERO) == 0)
    				|| (procedimentoDaTabelaCBHPM.getValorModerado() != null && MoneyCalculation.compare(procedimento.getValorAtualDoProcedimento(),procedimentoDaTabelaCBHPM.getValorModerado())==0)) {
	    
    			if (PainelDeControle.getPainel().isProcedimentoDobrado() 
    					&& procedimentoDaTabelaCBHPM.getCodigo().startsWith("3") 
    					&& contemTipoDeAcomodacaoParaDobrar(tiposDeAcomodacao)) {
    				procedimento.setValorAtualDoProcedimento(procedimentoDaTabelaCBHPM.getValorModerado().multiply(new BigDecimal(2)));
		
    				//salva valor moderado atual, caso seja alterada a tabela no futuro o calculo poderá ser desfeito a partir desse valor
    				procedimento.setValorModeradoDaTabelaCbhpmAoDobrar(procedimentoDaTabelaCBHPM.getValorModerado());
    			} else {
    				procedimento.setValorAtualDoProcedimento(procedimentoDaTabelaCBHPM.getValorModerado());
    			}
    		} else {
    			if (procedimento.getValorModeradoDaTabelaCbhpmAoDobrar() != null 
    					&& (MoneyCalculation.compare(procedimento.getValorAtualDoProcedimento(),procedimento.getValorModeradoDaTabelaCbhpmAoDobrar().multiply(new BigDecimal(2)))==0) 
    					&& (PainelDeControle.getPainel().isProcedimentoDobrado() 
    							&& procedimentoDaTabelaCBHPM.getCodigo().startsWith("3") 
    							&& !contemTipoDeAcomodacaoParaDobrar(tiposDeAcomodacao))) { //foi dobrado mas nao possui mais acomodacao para dobra
    					procedimento.setValorAtualDoProcedimento(procedimento.getValorModeradoDaTabelaCbhpmAoDobrar()); //retorna valor atual para valor moderado
    			}
    		}

    		if(procedimento.getValorAtualDaModeracao() == null || procedimento.getValorAtualDaModeracao().equals(0f)){
    			procedimento.setValorAtualDaModeracao(procedimentoDaTabelaCBHPM.getModeracao());
    		}

    		Boolean isValorCoParticipacaoZero = procedimento.getValorCoParticipacao().equals(BigDecimal.ZERO);
    		Boolean isProcedimentoCirurgico = procedimento.getTipoProcedimento() == Procedimento.PROCEDIMENTO_CIRURGICO;

    		if(isValorCoParticipacaoZero && !isProcedimentoCirurgico && procedimento.isGeraCoParticipacao()){
    			procedimento.calculaCoParticipacao();
    		}
    	} 
	}
	
	/**
	 * Verifica se a lista informada possui algum tipo de acomodação que deve disparar calculo dobrado do valor do procedimento
	 * de acordo com os valores informados no {@link PainelDeControle}
	 * 
	 * @param tipos tipos de acomodacao da guia
	 * 
	 * @return true, se acomodacao deve disparar calculo dobrado
	 * 
	 * @author Leonardo Sampaio
	 * @since 26/07/2012
	 */
	private boolean contemTipoDeAcomodacaoParaDobrar(List<TipoAcomodacao> tipos) {

	    if (tipos!=null) {
			for (TipoAcomodacao tipoAcomodacao : PainelDeControle.getPainel().getTiposDeAcomodacaoDobradas()) {
			    if (tipos.contains(tipoAcomodacao)){
			    	return true;
			    }
			}
	    }
	    return false;
	}
	
	/**
	 * Retorna os tipos de acomodações associados a diarias de uma guia com
	 *  situações AUTORIZADO ou SOLICITADO
	 * 
	 * @param guia guia completa
	 * @return lista de diarias
	 * 
	 * @author Leonardo Sampaio
	 * @since 27/07/2012
	 */
	private List<TipoAcomodacao> getTiposDeAcomodacaoDasDiariasSolicitadasOuAutorizadas(GuiaCompleta<ProcedimentoInterface> guia) {
	    
	    List<TipoAcomodacao> tiposDeAcomodacao = new ArrayList<TipoAcomodacao>();

	    if (guia.getItensDiaria()!=null){
	    	for (ItemDiaria itemDiaria : guia.getItensDiaria()) {
		    
	    		if( itemDiaria.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao()) 
					|| itemDiaria.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao()) ) {
	    			if (itemDiaria.getDiaria().getTipoAcomodacao()!=null) {
	    				tiposDeAcomodacao.add(itemDiaria.getDiaria().getTipoAcomodacao());
	    			}
	    		}
		    }
			return tiposDeAcomodacao;
	    }
	    
	    return null;
	}
}
