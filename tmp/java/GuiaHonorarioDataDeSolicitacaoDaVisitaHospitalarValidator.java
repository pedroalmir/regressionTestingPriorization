package br.com.infowaypi.ecare.services.honorarios.validators;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.utils.ProcedimentoUtils;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

public class GuiaHonorarioDataDeSolicitacaoDaVisitaHospitalarValidator extends	CommandValidator {
	
	/**
	 * Responsável pela validação de solicitação de visitas hospitalares por um determinado profissional.
	 * Se o profissional não tiver sido responsável por nenhum dos procedimentos cirurgicos contidos na guia, uma exceção 
	 * será laçada e, caso a visita esteja sendo solicitada para um procedimento cirurgico realizado há menos de 10 dias 
	 * outra exceção será lançada
	 * @param procedimento
	 * @throws ValidateException
	 */
	@Override
	public void execute() throws ValidateException {
		boolean temProcedimentosAptos = this.temProcedimenosComPorteAnestesicoMaiorQueZero();
		if (!this.getProcedimentosOutros().isEmpty() && !temProcedimentosAptos) {
			
			ProcedimentoCirurgicoInterface procedimentoMaisRecente = this.getGuiaOrigem().getProcedimentoMaisRecenteRealizadoPeloProfissional(getMedico());
			
			
			if (this.getProcedimentosOutros().size() > 0){
				
//				solicitacao da dra cinara para nao fazer essa validacao. em: 12/05/2010
//				Assert.isNotNull(procedimentoMaisRecente, MensagemErroEnum.PROFISSIONAL_NAO_RESPONSAVEL.getMessage());
				if (procedimentoMaisRecente != null) {
					Date dataDeRealizacaoDoProcedimento = procedimentoMaisRecente.getDataRealizacao();
					
					boolean todosParaRecemNascido = true;
					for (ProcedimentoInterface procedimento : this.getProcedimentosOutros()) {
						if (!ProcedimentoUtils.isAtendimentoAoRecemNascido(procedimento.getProcedimentoDaTabelaCBHPM())) {
							todosParaRecemNascido = false;
							break;
						}
					}
					
					if (!todosParaRecemNascido) {
						Calendar realizacaoDoProcedimento = Calendar.getInstance();
						realizacaoDoProcedimento.setTime(dataDeRealizacaoDoProcedimento);
						
						Calendar hoje = Calendar.getInstance();
						
						int diferenca = Utils.diferencaEmDias(realizacaoDoProcedimento, hoje);
						
						if (diferenca <= 10) {
							Date dataParaSolicitarVisita = DateUtils.addDays(procedimentoMaisRecente.getDataRealizacao(), 10);
							throw new ValidateException(MensagemErroEnum.PROFISSIONAL_SOLICITANDO_VISITA_COM_MENOS_DE_DEZ_DIAS.getMessage(procedimentoMaisRecente.getProcedimentoDaTabelaCBHPM().getCodigo(), Utils.format(dataParaSolicitarVisita)));
						}
					}
				}
			}
		}
	}

}
