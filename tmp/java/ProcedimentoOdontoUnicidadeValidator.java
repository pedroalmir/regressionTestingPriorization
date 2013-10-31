package br.com.infowaypi.ecarebc.procedimentos.validators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import br.com.infowaypi.ecarebc.atendimentos.GuiaExameOdonto;
import br.com.infowaypi.ecarebc.atendimentos.enums.ProcedimentoSuperUnicidadeEnum;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.odonto.EstruturaOdonto;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOdonto;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Classe para valida��o de unicidade de um procedimento odontol�gico para um segurado no e-Care. 
 * <b>Unicidade Odontol�gica</b> � um tipo de valida��o do m�dulo e-Care odontol�gico que impede um procedimento 
 * de ser realizado mais de uma vez em um mesmo elemento para um mesmo segurado. 
 * @author Danilo Nogueira Portela
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class ProcedimentoOdontoUnicidadeValidator extends AbstractProcedimentoValidator<ProcedimentoOdonto, GuiaExameOdonto> {

	public boolean templateValidator(ProcedimentoOdonto proc, GuiaExameOdonto guia) throws Exception {
		TabelaCBHPM cbhpm = proc.getProcedimentoDaTabelaCBHPM();

		// Valida��o de unicidade entre os procedimentos da guia atual
		if (isProcedimentoSuperUnicidade(proc)) {
			for (ProcedimentoOdonto p : guia.getProcedimentos()) {
				for (EstruturaOdonto e1 : (Set<EstruturaOdonto>)proc.getEstruturas()) {
					for (EstruturaOdonto e2 : (Set<EstruturaOdonto>)p.getEstruturas()) {
						Boolean isMesmaEstrutura = e1.equals(e2);
						Boolean isMesmoProcedimento = proc.equals(p);

						if (!isMesmoProcedimento) {
							Assert.isFalse(isMesmaEstrutura, MensagemErroEnum.ELEMENTO_JA_APLICADO_UNICIDADE_NA_GUIA.getMessage(cbhpm.getCodigo()));
						}
					}
				}
			}

			Collection<GuiaExameOdonto> guiasDoSegurado = new ArrayList<GuiaExameOdonto>();

			try {
				// Busca todas as guias do segurado j� realizadas
				guiasDoSegurado = new Service().buscarGuias(guia.getSegurado(), GuiaExameOdonto.class, SituacaoEnum.CONFIRMADO, SituacaoEnum.FATURADA);
			} catch (Exception e) {
				throw e;
			}

			Boolean isExistemGuias = !guiasDoSegurado.isEmpty() && !(guiasDoSegurado == null);
			List<ProcedimentoOdonto> procedimentosRealizados = new ArrayList<ProcedimentoOdonto>();
			if (isExistemGuias && proc.getProcedimentoDaTabelaCBHPM().getUnicidade()) {
				for (GuiaExameOdonto g : guiasDoSegurado) {
					for (ProcedimentoOdonto p : g.getProcedimentos()) {
						procedimentosRealizados.add(p);
					}
				}
				for (ProcedimentoOdonto p : procedimentosRealizados) {
					if (proc.getProcedimentoDaTabelaCBHPM().equals(p.getProcedimentoDaTabelaCBHPM())) {
						Collection intersection = CollectionUtils.intersection(proc.getEstruturas(), p.getEstruturas());
						Assert.isTrue(intersection.isEmpty(), MensagemErroEnum.PROCEDIMENTO_COM_ELEMENTO_JA_APLICADO_UNICIDADE.getMessage(proc.getProcedimentoDaTabelaCBHPM().getDescricao()));
					}
				}
				
				
			}
		}
		return true;
	}

	/**
	 * Verifica��o se o procedimento possui unicidade odontol�gica
	 * 
	 * @param proc
	 * @return
	 */
	public Boolean isProcedimentoSuperUnicidade(ProcedimentoOdonto proc) {
		TabelaCBHPM cbhpm = proc.getProcedimentoDaTabelaCBHPM();
		Boolean isProcedimentoSuperUnicidade = false;

		for (ProcedimentoSuperUnicidadeEnum procUnico : ProcedimentoSuperUnicidadeEnum.values()){
			if (cbhpm.getCodigo().equals(procUnico.getCodigo())){
				isProcedimentoSuperUnicidade = true;
			}
		}
		return isProcedimentoSuperUnicidade;
	}
}
