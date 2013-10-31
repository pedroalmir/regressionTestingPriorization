package br.com.infowaypi.ecarebc.atendimentos.validators.fechamento;

import java.util.Date;

import org.hibernate.FlushMode;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.ecarebc.atendimentos.alta.MotivoAlta;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

public class FechamentoAltaValidator implements FechamentoGuiaInternacaoValidator{

	public static final String CODIGO_ALTA_ADMINISTRATIVA = "16";

	@Override
	public void execute(GuiaInternacao guia, Boolean parcial, Date dataFinal,
			UsuarioInterface usuario) throws ValidateException {

		boolean isPossuiAlta = guia.getAltaHospitalar() != null;
		
		if(!isPossuiAlta) {
			if (parcial) {
				validaDataTerminoAtendimento(guia, dataFinal);
				MotivoAlta motivo = getMotivoDeAltaAdministrativa();
				guia.registrarAlta(dataFinal, usuario, motivo);
			} else {
				throw new ValidateException(MensagemErroEnum.FECHAMENTO_EXIGE_ALTA.getMessage());
			}
		} else {
			Assert.isFalse(parcial, "Caro usuário, esta guia de internação possui um registro de alta e só pode ser fechada totalmente.");
			Assert.isNull(dataFinal, "Caro usuário, não é possível alterar a data do término de atendimento. Guia já possui alta.");
		}
	}

	protected MotivoAlta getMotivoDeAltaAdministrativa() {
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);				
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new  Equals("codigo",CODIGO_ALTA_ADMINISTRATIVA));
		MotivoAlta motivo = sa.uniqueResult(MotivoAlta.class);
		return motivo;
	}
	
	private void validaDataTerminoAtendimento(GuiaCompleta guia, Date dataFinal) throws ValidateException {
		Assert.isNotNull(dataFinal, MensagemErroEnum.DATA_FECHAMENTO_REQUERIDA.getMessage());
		if(Utils.compareData(dataFinal, new Date()) > 0)
			throw new ValidateException(MensagemErroEnum.DATA_FECHAMENTO_SUPERIOR_A_DATA_ATUAL.getMessage());
		if(dataFinal.before(guia.getDataAtendimento()))
			throw new ValidateException(MensagemErroEnum.DATA_FECHAMENTO_INFERIOR_A_DATA_ATENDIMENTO.getMessage());
	}

}
