package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.acordos.AcordoPacote;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Certifica se uma guia de internação possui procedimentos contidos em pacotes para os quais o prestador possui acordo,
 * tornando obrigatória a inserção do respectivo pacote caso essa condição seja satisfeita.
 * @author Patrícia Fontinele
 * @since 03/08/2010
 */
public class InternacaoPossuiProcedimentoSemPacoteValidator extends AbstractGuiaValidator<GuiaCompleta<ProcedimentoInterface>> {
 
	public boolean templateValidator(GuiaCompleta<ProcedimentoInterface> guia) throws ValidateException {
		AcordoPacote acordo = null; 
		boolean guiaContainsPacote = false;
		StringBuilder string = new StringBuilder();
		
		for (ProcedimentoInterface proc : guia.getProcedimentosNaoCanceladosENegados()) {
			acordo = guia.getPrestador().getAcordoPacote(proc.getProcedimentoDaTabelaCBHPM());
			if (acordo != null) {
				for (ItemPacote item : guia.getItensPacoteNaoCanceladosENegados()) {
					if (item.getPacote().getProcedimentosCBHPM().contains(proc.getProcedimentoDaTabelaCBHPM())){
						guiaContainsPacote = true;
						break;
					}
				}
				string.append(acordo.getPacote().getCodigoDescricao()+"; ");
				Assert.isTrue(guiaContainsPacote, "Não é possível solicitar o procedimento "
												  +proc.getProcedimentoDaTabelaCBHPM().getCodigoEDescricao()
												  +" sem a solicitação de pelo menos um dos seguintes pacotes: "
												  +string);
			}
		}
		return true;
	}

}
