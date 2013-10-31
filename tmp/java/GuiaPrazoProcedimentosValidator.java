package br.com.infowaypi.ecarebc.atendimentos.validators;

import static br.com.infowaypi.ecarebc.enums.SituacaoEnum.ABERTO;
import static br.com.infowaypi.ecarebc.enums.SituacaoEnum.AGENDADA;
import static br.com.infowaypi.ecarebc.enums.SituacaoEnum.PRORROGADO;
import static br.com.infowaypi.ecarebc.enums.SituacaoEnum.SOLICITADO_INTERNACAO;
import static br.com.infowaypi.ecarebc.enums.SituacaoEnum.SOLICITADO_PRORROGACAO;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.SituacaoInterface;

/**
 * Classe para validação de inclusão de procedimentos em guias abertas.
 * @author Rondinele
 * @changes Danilo Nogueira Portela
 */
public class GuiaPrazoProcedimentosValidator extends AbstractGuiaValidator<GuiaCompleta> {
 
	@Override
	protected boolean templateValidator(GuiaCompleta guia) throws ValidateException {
		/* EXPLICAÇÃO
		 * essa validação se propõe a não permitir a solicitação de procedimentos fora do prazo da internação,
		 * por exemplo:  internação foi aberta em 22/08/2010 00:16, com o prazo autorizado de somente um dia,
		 * então o prestador so poderia solicitar um procedimento até a data 23/08/2010 00:16, após isso, a
		 * mensagem de prazo de solicitação excedido seria mostrada. Essa regra não existe no sr,
		 * logo a validação foi removida dos validators de guia.
		 */
		Boolean isGuiaNula = guia.getProcedimentos().isEmpty() || guia.getProcedimentos() == null;
		if(!isGuiaNula) {
			SituacaoInterface situacaoAtual = guia.getSituacao();
			Boolean isAgendada = AGENDADA.descricao().equals(situacaoAtual.getDescricao());
			Boolean isAberta = ABERTO.descricao().equals(situacaoAtual.getDescricao());
			Boolean isProrrogado = PRORROGADO.descricao().equals(situacaoAtual.getDescricao());
			Boolean isSolicitadoProrrogacao = SOLICITADO_PRORROGACAO.descricao().equals(situacaoAtual.getDescricao());
			Boolean isSolicitadoInternacao = SOLICITADO_INTERNACAO.descricao().equals(situacaoAtual.getDescricao());
			
			//Verifica se a guia é internação eletiva e ainda não está salva no banco
			if (isAgendada && guia.isInternacaoEletiva() && guia.getIdGuia() == null)
				return true;
			
			if (isSolicitadoProrrogacao || isSolicitadoInternacao)
				situacaoAtual = getSituacaoAnterior(situacaoAtual, guia);
			
			Date dataSituacao = situacaoAtual.getDataSituacao();
			Date dataAtual = new Date(); 
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(dataSituacao);
		
			if (isAberta || isProrrogado || isSolicitadoInternacao || isSolicitadoProrrogacao)
				calendar.add(Calendar.HOUR, guia.getPrazoProrrogado());
			else
				throw new ValidateException(MensagemErroEnum.GUIA_NAO_PODE_SOLICITAR_PROCEDIMENTOS.getMessage());

			
			if (dataAtual.compareTo(calendar.getTime()) > 0)
				throw new ValidateException(MensagemErroEnum.PRAZO_EXCEDIDO_PARA_INCLUSAO_DE_PROCEDIMENTOS.getMessage());
		}
		return true;
	}
	
	private SituacaoInterface getSituacaoAnterior(SituacaoInterface situacao, GuiaCompleta<ProcedimentoInterface> guia){
		int ordemSituacao = situacao.getOrdem();
		if (ordemSituacao == 1)
			return situacao;
		
		for(SituacaoInterface situacaoAtual : guia.getSituacoes()){
			if (situacaoAtual.getOrdem() == ordemSituacao - 1)
				return situacaoAtual;
		}
		return situacao;
	}

}
