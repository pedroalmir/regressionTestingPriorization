package br.com.infowaypi.ecarebc.procedimentos.validators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.hibernate.FlushMode;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * Classe para validação de unicidade de um procedimento para um segurado no e-Care. 
 * <b>Unicidade</b> é um tipo de validação do módulo e-Care que impede um procedimento de ser realizado mais de uma vez 
 * para um mesmo segurado. 
 * @author Danilo Nogueira Portela
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class ProcedimentoUnicidadeValidator<P extends ProcedimentoInterface, G extends GuiaSimples> extends AbstractProcedimentoValidator<P, G>{

	public boolean templateValidator(P proc, G guia) throws Exception{
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
		if(!guia.isExameOdonto()){
			TabelaCBHPM cbhpm = proc.getProcedimentoDaTabelaCBHPM();
			Collection<GuiaSimples> guiasDoSegurado = new ArrayList<GuiaSimples>();
			
			//Busca todas as guias do segurado já realizadas
			guiasDoSegurado = new Service().buscarGuias(guia.getSegurado(), GuiaSimples.class, 
					SituacaoEnum.CONFIRMADO, SituacaoEnum.FATURADA, SituacaoEnum.AUDITADO);
			
			Boolean isExistemGuias = !guiasDoSegurado.isEmpty() && !(guiasDoSegurado == null);
			if(isExistemGuias){
				for (GuiaSimples g : guiasDoSegurado) {
					Boolean isMesmaGuia = guia.equals(g);
					
					if(!isMesmaGuia){
						for (P p : (Set<P>)g.getProcedimentos()) {
							
							Boolean isProcedimentoUnicidade = cbhpm.getUnicidade();
							if(isProcedimentoUnicidade){
								Boolean isMesmoCBHPM = cbhpm.equals(p.getProcedimentoDaTabelaCBHPM());
								Boolean isProcedimentoCancelado = p.getSituacao().getDescricao().equals(SituacaoEnum.CANCELADO.descricao()); 
								
								if(isMesmoCBHPM && !isProcedimentoCancelado);
									throw new ValidateException(MensagemErroEnum.PROCEDIMENTO_UNICIDADE_JA_APLICADO_PARA_BENEFICIARIO.
											getMessage(cbhpm.getCodigo()));
							}
						}
					}
				}
			}
		}
		return true;
	}
}
