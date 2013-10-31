package br.com.infowaypi.ecarebc.atendimentos.validators;

import java.util.Set;

import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaOdonto;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * Classe para validação de guias consulta odontológica
 * @author Danilo Nogueira Portela
 */
public class GuiaConsultaOdontoValidator extends AbstractGuiaValidator<GuiaConsultaOdonto>{
 	

	public boolean templateValidator(GuiaConsultaOdonto guia)throws ValidateException{
		
		Especialidade clinicoPromotor = (Especialidade) HibernateUtil
			.currentSession().createCriteria(Especialidade.class).add(Expression.eq("descricao", "Clinico Promotor")).uniqueResult();
		Especialidade pericia = (Especialidade) HibernateUtil
			.currentSession().createCriteria(Especialidade.class).add(Expression.eq("descricao", "PERITO ODONTOLOGICO")).uniqueResult();
		
		Set<Especialidade> especialidadesDoProfissional = guia.getProfissional().getEspecialidades();
		
		// profissional com especialidade PERITO ODONTOLÓGICO só pode realizar consultas de pericia inicial.
		if(especialidadesDoProfissional.contains(pericia) && (!guia.isConsultaOdontoPericiaInicial())) {
			throw new ValidateException(MensagemErroEnum.PROFISSIONAL_NAO_HABILITADO_PARA_ESTA_OPERACAO.getMessage(guia.getProfissional().getNome()));
		}

		if (!especialidadesDoProfissional.contains(pericia)) { 
			if (guia.isConsultaOdontoPericiaInicial()) {
				throw new ValidateException(MensagemErroEnum.PROFISSIONAL_NAO_ATENDE_ESPECIALIDADE.getMessage(pericia.getDescricao()));
			}
		}else if (!especialidadesDoProfissional.contains(clinicoPromotor)) {
			if (guia.isConsultaOdontoClinicoPromotor()) { 
				throw new ValidateException(MensagemErroEnum.PROFISSIONAL_NAO_ATENDE_ESPECIALIDADE.getMessage(clinicoPromotor.getDescricao()));
			}
		}
		return true;
	}
	
}
