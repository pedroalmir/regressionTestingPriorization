package br.com.infowaypi.ecarebc.procedimentos.validators;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.msr.exceptions.ValidateException;

@SuppressWarnings("unchecked")
public class ProcedimentoAtendimentoUrgenciaValidator extends AbstractProcedimentoValidator<ProcedimentoInterface, GuiaSimples> {

	@Override
	protected boolean templateValidator(ProcedimentoInterface procedimento,
			GuiaSimples guia) throws Exception {
		
		boolean isSeguradoAtivo = guia.getSegurado().isSituacaoAtual(SituacaoEnum.ATIVO.descricao());
		boolean isUrgenciaOuExameExterno = (guia.isAtendimentoUrgencia() || guia.isConsultaUrgencia() || guia.isExameExterno());

		TabelaCBHPM tabelaCBHPM = procedimento.getProcedimentoDaTabelaCBHPM();
		boolean isEspecial = (tabelaCBHPM.getNivel() > 1);
		
		if (isEspecial && !isSeguradoAtivo && isUrgenciaOuExameExterno) {
			throw new ValidateException(MensagemErroEnum.SEGURADO_SUSPESO_NAO_PODE_SOLICITAR_EXAME_COMPLEXO.getMessage());
		}
		
		return true;
	}
}
