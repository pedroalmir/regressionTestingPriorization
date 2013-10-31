package br.com.infowaypi.ecarebc.procedimentos.validators;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecarebc.atendimentos.GuiaExameOdonto;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.odonto.EstruturaOdonto;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOdonto;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe para validação de periodicidade de um procedimento odontológico para um segurado no e-Care. 
 * <b>Periodicidade Odontológica</b> é um conceito do e-Care que garante que um determinado procedimento só poderá 
 * ser realizado em um mesmo elemento por um mesmo segurado após um certo período de espera, depois de ter sido realizado uma vez.
 * @author Danilo Nogueira Portela
 */
public class ProcedimentoOdontoPeriodicidadeValidator extends AbstractProcedimentoValidator<ProcedimentoOdonto, GuiaExameOdonto>{
 
	public boolean templateValidator(ProcedimentoOdonto proc, GuiaExameOdonto guia) throws ValidateException{
		TabelaCBHPM cbhpm = proc.getProcedimentoDaTabelaCBHPM();
		Integer periodo = cbhpm.getPeriodicidade();
		
		//Busca as guias do segurado que estão dentro do prazo de periodicidade do procedimento
		List<GuiaExameOdonto> guiasDoSegurado = new Service().buscarGuiasPorPeriodo(null, guia.getSegurado(), false, periodo, 
				GuiaExameOdonto.class, SituacaoEnum.SOLICITADO, SituacaoEnum.AUTORIZADO, SituacaoEnum.CONFIRMADO, SituacaoEnum.ABERTO, SituacaoEnum.FECHADO, 
				SituacaoEnum.ENVIADO, SituacaoEnum.RECEBIDO, SituacaoEnum.AGENDADA,
				SituacaoEnum.FATURADA, SituacaoEnum.AUDITADO, SituacaoEnum.PAGO);
		
		Boolean isExistemGuias = !guiasDoSegurado.isEmpty() && guiasDoSegurado != null;
		
		if(isExistemGuias){
			for (GuiaExameOdonto g : guiasDoSegurado) {
				Boolean isMesmaGuia = guia.equals(g);
				
				if(!isMesmaGuia){
					for (ProcedimentoOdonto p :  g.getProcedimentos()) {
						
						for (EstruturaOdonto e1 : (Set<EstruturaOdonto>)p.getEstruturas()) {
							for (EstruturaOdonto e2 : (Set<EstruturaOdonto>)proc.getEstruturas()) {
								Boolean isMesmoCBHPM = proc.getProcedimentoDaTabelaCBHPM().equals(p.getProcedimentoDaTabelaCBHPM());
								Boolean isMesmaEstrutura = e1.equals(e2);
								
								if(isMesmoCBHPM && isMesmaEstrutura){
									Date dataUltimaMarcacao = g.getDataMarcacao();
									Calendar dataVencimentoPeriodicidade = Calendar.getInstance();
									dataVencimentoPeriodicidade.setTime(dataUltimaMarcacao);
									dataVencimentoPeriodicidade.add(Calendar.DAY_OF_MONTH, periodo);
									Integer resultado = Utils.compareData(dataVencimentoPeriodicidade.getTime(), Calendar.getInstance().getTime());
									
									Assert.isTrue(resultado <= 0, MensagemErroEnum.PROCEDIMENTO_NAO_CUMPRIU_PERIODICIDADE.
											getMessage(cbhpm.getCodigo(), Utils.format(dataUltimaMarcacao),Utils.format(dataVencimentoPeriodicidade.getTime()), g.getAutorizacao()));
								}
							}
						}
					}
				}
			}
		}
		return true;
	}

}
