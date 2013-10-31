package br.com.infowaypi.ecare.services.honorarios.validators;

import java.util.HashSet;
import java.util.Set;

import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacoteHonorario;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;

/**
 * @author Dannylvan
 * 
 * Verifica se existir pelo menos um procedimento da guia comum aos procedimentos do pacote 
 */
public class GuiaHonorarioPacoteDeHonorarioCompativelComAGuiaValidator extends CommandValidator {

	@Override
	public void execute() throws ValidateException {

		if (this.possuiPacotesHonorario()) {
			Set<ProcedimentoInterface> procedimentosDaGuia = this.getGuiaOrigem().getProcedimentosAptosAGerarHonorariosMedicos();
			
			Set<TabelaCBHPM> procedimentosCBHPMDaGuia = new HashSet<TabelaCBHPM>();
	
			for (ProcedimentoInterface proc : procedimentosDaGuia) {
				procedimentosCBHPMDaGuia.add(proc.getProcedimentoDaTabelaCBHPM());
			}
	
			for (ItemPacoteHonorario item : this.getItensPacoteHonorario()) {
				boolean pacoteIsCompativel = false;
				if (!procedimentosCBHPMDaGuia.isEmpty()) {
					for (TabelaCBHPM procPacote : item.getPacote().getProcedimentosCBHPM()) {
						if (procedimentosCBHPMDaGuia.contains(procPacote)) {
							pacoteIsCompativel = true;
						}
					}
	
					Assert.isTrue(pacoteIsCompativel, "Os procedimentos do pacote "+item.getPacote().getDescricao()+" inserido não são compatíveis com os procedimentos da guia.");
				}
			}
		}
	}
}
