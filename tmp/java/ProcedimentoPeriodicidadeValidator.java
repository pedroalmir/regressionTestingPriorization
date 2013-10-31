package br.com.infowaypi.ecarebc.procedimentos.validators;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.FlushMode;

import br.com.infowaypi.ecarebc.atendimentos.Critica;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.Role;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe para validação de periodicidade de um procedimento para um segurado no e-Care. 
 * <b>Periodicidade</b> é um conceito do e-Care que garante que um determinado procedimento só poderá ser realizado por 
 * um mesmo segurado após um certo período de espera, depois de ter sido realizado uma vez.
 * @author Danilo Nogueira Portela
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ProcedimentoPeriodicidadeValidator<P extends ProcedimentoInterface, G extends GuiaSimples> extends AbstractProcedimentoValidator<P,G>{

	/**
	 * Validade se o segurado da guia possui periodicidade para o procedimento pedido.
	 * 
	 * @param proc, guia
	 * @throws ValidateException
	 * @return true
	 */
	public boolean templateValidator(P proc, G guia) throws ValidateException{
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
		Boolean isGuiaExternaDeInternacao = guia.getGuiaOrigem() == null ? false : guia.getGuiaOrigem().isInternacao(); 
		if((guia.isInternacao() || isGuiaExternaDeInternacao) && proc.getProcedimentoDaTabelaCBHPM().getVerificaPeriodicidadeNaInternacao().equals(Boolean.FALSE)) {
			return true;
		}
		//Validação de periodicidade de consultas é feita à parte
		//A periodicidade não é verificada nas guias completas
		boolean  isGuiaCompletaEGuiaExame = guia.isCompleta() && guia.isExame();
		if(!guia.isInternacao() && !guia.isConsulta() && isGuiaCompletaEGuiaExame && !guia.isExameOdonto() && !guia.isExameExterno()){
			
			TabelaCBHPM cbhpm = proc.getProcedimentoDaTabelaCBHPM();
			Integer periodo = cbhpm.getPeriodicidade();
			
			//Busca as guias do segurado que estão dentro do prazo de periodicidade do procedimento
			Collection<GuiaSimples> guiasDoSegurado = new Service().buscarGuiasPorPeriodo(null, guia.getSegurado(), false, periodo, 
					GuiaSimples.class, SituacaoEnum.SOLICITADO, SituacaoEnum.AUTORIZADO, SituacaoEnum.CONFIRMADO, SituacaoEnum.ABERTO, SituacaoEnum.FECHADO, 
					SituacaoEnum.ENVIADO, SituacaoEnum.RECEBIDO, SituacaoEnum.AGENDADA,
					SituacaoEnum.FATURADA, SituacaoEnum.AUDITADO, SituacaoEnum.PAGO);
			Boolean isExistemGuias = !guiasDoSegurado.isEmpty() && guiasDoSegurado != null;
			
			if(isExistemGuias){
				for (G g : (List<G>)guiasDoSegurado) {
					Boolean isMesmaGuia = guia.equals(g);
					
					if(!isMesmaGuia){
						for (P p : (Set<P>) g.getProcedimentos()) {
							Boolean isMesmoCBHPM = proc.getProcedimentoDaTabelaCBHPM().getCodigo().equals(p.getProcedimentoDaTabelaCBHPM().getCodigo());
							boolean isProcedimentoCancelado = p.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao());
							
							if(isMesmoCBHPM && !isProcedimentoCancelado){
								Date dataUltimaMarcacao = g.getDataMarcacao();
								Calendar dataVencimentoPeriodicidade = Calendar.getInstance();
								dataVencimentoPeriodicidade.setTime(dataUltimaMarcacao);
								dataVencimentoPeriodicidade.add(Calendar.DAY_OF_MONTH, periodo);
								Date hoje = Calendar.getInstance().getTime();
								
								Boolean isCumpriuPeriodicidade = Utils.compareData(dataVencimentoPeriodicidade.getTime(), hoje) <= 0;
								String mensagemDeErro = MensagemErroEnum.PROCEDIMENTO_NAO_CUMPRIU_PERIODICIDADE.
								getMessage(cbhpm.getCodigo(), Utils.format(dataUltimaMarcacao),Utils.format(dataVencimentoPeriodicidade.getTime()), g.getAutorizacao());
								if(guia.getSituacao().getUsuario().getRole().equals(Role.CENTRAL_DE_SERVICOS.getValor())) {
									Critica critica = new Critica();
									critica.setMensagem(mensagemDeErro);
									critica.setGuia(guia);
									guia.setPossuiPeriodicidadeEmProcedimento(true);
									proc.setPossuiPeriodicidade(true);
									guia.getCriticas().add(critica);
									return true;
								}else {
									Assert.isTrue(isCumpriuPeriodicidade, mensagemDeErro);
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
