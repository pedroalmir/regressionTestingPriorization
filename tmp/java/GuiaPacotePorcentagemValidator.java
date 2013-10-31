package br.com.infowaypi.ecarebc.atendimentos.validators;

import java.util.Set;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;

public class GuiaPacotePorcentagemValidator extends AbstractGuiaValidator<GuiaCompleta<ProcedimentoInterface>>{

	/**
	 * Verifica a existencia de pelo menos um pacote a 100% na guia.
	 */
	@Override
	protected boolean templateValidator(GuiaCompleta<ProcedimentoInterface> guia) throws Exception {
		Set<ItemPacote> itensPacote = guia.getItensPacoteNaoCanceladosENegados();
		if (itensPacote.isEmpty()){
			return true;
		}
	
		for (ItemPacote pacote : itensPacote) {
			if(pacote.getPorcentagem().compareTo(Constantes.PORCENTAGEM_100) == 0){
				return true;
			}
		}
		throw new RuntimeException(MensagemErroEnum.NENHUM_PACOTE_A_100_PORCENTO_NA_GUIA.getMessage());
	}

}
