package br.com.infowaypi.ecarebc.atendimentos.validators.fechamento;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

@SuppressWarnings("unchecked")
public class FechamentoProcedimentosSolicitadosValidator implements FechamentoGuiaCompletaValidator {

	@Override
	public void execute(GuiaCompleta guia, Boolean parcial, Date dataFinal,
			UsuarioInterface usuario) throws ValidateException {
		String procedimentosSolicitados = getProcedimentosSolicitados(guia);
		if (!Utils.isStringVazia(procedimentosSolicitados)) {
			throw new ValidateException(MensagemErroEnum.FECHAMENTO_DE_GUIAS_COM_PROCEDIMENTOS_SOLICITADOS.getMessage(procedimentosSolicitados));
		}
	}

	private String getProcedimentosSolicitados(GuiaCompleta guia) {
		List<ProcedimentoInterface> procedimentos = guia.getProcedimentosSolicitados();
		StringBuilder builder = new StringBuilder();
		Iterator<ProcedimentoInterface> iterator = procedimentos.iterator();
		while (iterator.hasNext()) {
			ProcedimentoInterface procedimento = iterator.next();
			if(procedimento.getIdProcedimento() != null) {
				builder.append(procedimento.getProcedimentoDaTabelaCBHPM().getCodigo());
					if (iterator.hasNext()) {
						builder.append(", ");
					}
			}	
		}
		return builder.toString();
	}

}
