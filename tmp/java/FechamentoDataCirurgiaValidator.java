package br.com.infowaypi.ecarebc.atendimentos.validators.fechamento;

import java.util.Date;

import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

public class FechamentoDataCirurgiaValidator implements FechamentoGuiaInternacaoValidator {

	@Override
	public void execute(GuiaInternacao guia, Boolean parcial, Date dataFinal,
			UsuarioInterface usuario) throws ValidateException {
		
		Date dataCirurgia = guia.getDataCirurgia();
	
		if(!guia.getProcedimentosCirurgicosNaoCanceladosENegados().isEmpty()) {
//			Assert.isNotNull(dataCirurgia, MensagemErroEnum.DATA_CIRURGIA_REQUERIDA.getMessage());
			
			if (dataCirurgia.before(guia.getDataAtendimento()))
				throw new ValidateException(MensagemErroEnum.DATA_CIRURGIA_INFERIOR_A_DATA_ATENDIMENTO.getMessage());
			
			if(dataCirurgia.after(guia.getDataTerminoAtendimento())) {
				throw new ValidateException(MensagemErroEnum.DATA_CIRURGIA_SUPERIOR_A_DATA_FECHAMENTO.getMessage());
			}
		}
	}

}
