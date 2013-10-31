package br.com.infowaypi.ecare.segurados.validators;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import br.com.infowaypi.ecare.constantes.Constantes;
import br.com.infowaypi.ecare.enums.MotivoEnumSR;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe para validação básica de seguradosdo sistema
 * @author Danilo Nogueira Portela
 */
public class SeguradoValidator extends AbstractSeguradoValidator<Segurado>{
 
	protected boolean templateValidator(Segurado seg) throws ValidateException{
		Calendar calendario = new GregorianCalendar();
		calendario.setTime(new Date());
		calendario.set(Calendar.YEAR, calendario.get(Calendar.YEAR) + 1);
		seg.setDataVencimentoCarteira(calendario.getTime());

		Date hoje = new Date();
		if(seg.getDataAdesao() != null){
			if (Utils.compareData(seg.getDataAdesao(),hoje)>0)
				throw new ValidateException(MensagemErroEnum.DATA_DE_ADESAO_INVALIDA.getMessage());
			}
		if (Utils.isStringVazia(seg.getPessoaFisica().getNome()))
			throw new ValidateException("O Nome informado é inválido.");

		if (seg.getPessoaFisica().getDataNascimento() == null)
			throw new ValidateException(
					"A Data de Nascimento informada é inválida.");
		
		if (seg.getPessoaFisica().getIdade() >= Constantes.IDADE_MINIMA_PARA_EXIGENCIA_DE_DOCUMENTO) {
			if (Utils.isStringVazia(seg.getPessoaFisica().getIdentidade()))
				throw new ValidateException("A Identidade informada é inválida.");
		}
		
		if(seg.getAplicacaoQuestionario() != null) {
			seg.getSituacaoCadastral().setDescricao(SituacaoEnum.RECADASTRADO.descricao());
		}else {
			seg.getSituacaoCadastral().setDescricao(SituacaoEnum.CADASTRADO.descricao());
		}
	
		return true;
	}
}
