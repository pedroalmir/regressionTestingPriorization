package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Certifica se uma guia de internação com pacotes possui também pelo menos um dos procedimentos de cada pacote inclusos.
 * Essa obrigatoriedade visa possibilitar o registro dos honorários médicos e anestesistas em todos os atendimentos,
 * impedindo que uma internação contenha só pacotes.
 * @author Patrícia Fontinele
 * @since 03/08/2010
 */
public class InternacaoPossuiPacoteSemProcedimentoValidator extends AbstractGuiaValidator<GuiaCompleta<ProcedimentoInterface>> {
 
	public boolean templateValidator(GuiaCompleta<ProcedimentoInterface> guia) throws ValidateException {
		for (ItemPacote item : guia.getItensPacoteNaoCanceladosENegados()) {
			boolean guiaContainsProcedimento = false;
			StringBuilder string = new StringBuilder();
			if (!item.getPacote().getProcedimentosCBHPM().isEmpty()) {
				//valida se a guia tem os procedimentos dentro dela
				for (ProcedimentoInterface proc : guia.getProcedimentosNaoCanceladosENegados()) {
					if (item.getPacote().getProcedimentosCBHPM().contains(proc.getProcedimentoDaTabelaCBHPM())) {
						guiaContainsProcedimento = true;
						break;
					}
				}
				for (TabelaCBHPM tabela : item.getPacote().getProcedimentosCBHPM()) {
					string.append(tabela.getCodigoEDescricao()+"; ");
				}
				
				Assert.isTrue(guiaContainsProcedimento, "Não é possível solicitar o pacote "
														+ item.getPacote().getCodigoDescricao() +
														" sem a solicitação de pelo menos um dos seguintes procedimentos: "
														+string);
			}
		}
		return true;
	}

}
