package br.com.infowaypi.ecarebc.atendimentos.validators.fechamento;

import java.math.BigDecimal;
import java.util.Date;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;


/**
 * Verifica se o campo OUTROS VALORES está preenchido sem sua descrição (DESCRIÇÃO OUTROS).
 * 
 * @author Eduardo
 *
 */
public class FechamentoOutrosValoresValidator implements FechamentoGuiaCompletaValidator {

	@SuppressWarnings("unchecked")
	public void execute(GuiaCompleta guia, Boolean parcial, Date dataFinal, UsuarioInterface usuario) throws ValidateException {
		BigDecimal valorOutros = guia.getValoresMatMed().getValorOutros();
		valorOutros = valorOutros == null? BigDecimal.ZERO : valorOutros;
		boolean isOutrosValoresDiferenteDeZero = valorOutros.compareTo(BigDecimal.ZERO) != 0;
		boolean isStringVazia = Utils.isStringVazia(guia.getValoresMatMed().getDescricaoOutros());
		if (isOutrosValoresDiferenteDeZero && isStringVazia) {
				throw new ValidateException(MensagemErroEnum.DESCRICAO_VALOR_OUTROS_DEVE_SER_INFORMADA.getMessage());
		}
	}
}
