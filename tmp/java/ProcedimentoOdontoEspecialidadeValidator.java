package br.com.infowaypi.ecarebc.procedimentos.validators;

import java.util.List;
import java.util.Set;

import org.hibernate.criterion.Restrictions;

import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Classe que verifica se os procedimentos solicitados na guia podem ser realizados pelo prestador da sessão.				<br>
 * 																	<br>
 * As especialidades da tabela CBHPM do Procedimento devem estar contidas no conjunto de especialidades atendendidas pelo Prestador.	<br>
 * 																	<br>
 * Se necessário desativar essa validação, criar booleano em Prestador ou PainelDeControle.
 * 
 * @author Leonardo Sampaio
 * @since 03/08/2012
 * @see br.com.infowaypi.ecarebc.procedimentos.Procedimento
 */
public class ProcedimentoOdontoEspecialidadeValidator extends AbstractProcedimentoValidator<Procedimento, GuiaSimples<ProcedimentoInterface>> {

    @Override
    protected boolean templateValidator(Procedimento proc, GuiaSimples<ProcedimentoInterface> guia) throws Exception {
    	
    	Prestador prestador = guia.getPrestador();
    	
    	if(prestador == null){
    		return true;
    	}
    	
		boolean especialidadeInvalida = true;
		Set<Especialidade> especialidadesDoPrestador = prestador.getEspecialidades();
	
		@SuppressWarnings("unchecked")
		List<Especialidade> especialidadesDoProcedimento = 
		HibernateUtil.currentSession().createCriteria(Especialidade.class)
			.createCriteria("procedimentosDaTabelaCBHPM")
			.add(Restrictions.idEq(proc.getProcedimentoDaTabelaCBHPM().getIdTabelaCBHPM())).list();
	
		Assert.isTrue(especialidadesDoPrestador!=null&&especialidadesDoPrestador.size() > 0, 
				"O PRESTADOR não possui especialidades cadastradas!");
		
		for (Especialidade especialidadeDoProcedimento : especialidadesDoProcedimento) {
		    if (especialidadesDoPrestador.contains(especialidadeDoProcedimento)) {
		    	especialidadeInvalida = false;
		    	break;
		    }
		}
	
		boolean procedimentoTemEspecialidade = especialidadesDoProcedimento !=null && especialidadesDoProcedimento.size() > 0;
		
		boolean procedimentoValido = procedimentoTemEspecialidade && !especialidadeInvalida;
		
		Assert.isTrue(procedimentoValido, 
			MensagemErroEnum.PRESTADOR_NAO_HABILITADO_PARA_REALIZAR_ATIVIDADE.getMessage(
			" o procedimento \""+proc.getProcedimentoDaTabelaCBHPM().getCodigoEDescricao()+"\""));

	return true;
    }
}
