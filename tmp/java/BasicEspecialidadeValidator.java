package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SexoEnum;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;
/**
 * Classe para validação de especialidade nas guias
 * @author root
 * @changes Danilo Nogueira Portela
 */
@SuppressWarnings("unchecked")
public class BasicEspecialidadeValidator extends AbstractGuiaValidator<GuiaSimples> {
 
	public boolean templateValidator(GuiaSimples guia) throws ValidateException {
		Boolean isEspecialidadeNula = guia.getEspecialidade() == null;
		Boolean isMesmoSexo = guia.getSegurado().getPessoaFisica().getSexo().equals(guia.getEspecialidade().getSexo());
		Boolean isSexoAmbos = guia.getEspecialidade().getSexo().intValue() == SexoEnum.AMBOS.value();
		
		Assert.isTrue(isSexoAmbos || isMesmoSexo, MensagemErroEnum.SEGURADO_COM_SEXO_INVALIDO_PARA_ESPECIALIDADE.getMessage(guia.getEspecialidade().getDescricao()));
		
		Boolean isProfissionalNulo = guia.getProfissional() == null;
		Boolean isPrestadorNulo = guia.getPrestador() == null;
		if(!isPrestadorNulo){
			Prestador prest = (Prestador)ImplDAO.findById(guia.getPrestador().getIdPrestador(), Prestador.class);
			Boolean isPrestadorComMesmaEspecialidade = prest.getEspecialidades().contains(guia.getEspecialidade());
			if(!isEspecialidadeNula && !isPrestadorComMesmaEspecialidade)
				throw new ValidateException(MensagemErroEnum.ESPECIALIDADE_NAO_CADASTRADA_PARA_O_PRESTADOR.getMessage(guia.getEspecialidade().getDescricao()));
		}
		if(!isProfissionalNulo){
			Profissional prof = (Profissional)ImplDAO.findById(guia.getProfissional().getIdProfissional(), Profissional.class);
			Boolean isProfissionalComMesmaEspecialidade = prof.getEspecialidades().contains(guia.getEspecialidade());
			if(!isEspecialidadeNula && !isProfissionalComMesmaEspecialidade)
				throw new ValidateException(MensagemErroEnum.ESPECIALIDADE_NAO_CADASTRADA_PARA_O_PROFISSIONAL.getMessage(prof.getPessoaFisica().getNome(),guia.getEspecialidade().getDescricao()));

		}
		return true;
	}

}
