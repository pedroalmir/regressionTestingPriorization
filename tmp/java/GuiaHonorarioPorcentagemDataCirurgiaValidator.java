package br.com.infowaypi.ecare.services.honorarios.validators;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.CollectionUtils;

public class GuiaHonorarioPorcentagemDataCirurgiaValidator extends
		CommandValidator {


	/**
	 * Verifica se existe mais de um procedimento com porcentagem 100% na mesma data de realização.
	 * Caso exista lança uma exceção.
	 * 
	 * @param procedimentos
	 * @throws Exception
	 */
	@Override
	public void execute() throws ValidateException {
		Set<ProcedimentoInterface> procedimentosQueVaoGerarHonorarios = this.getGuiaOrigem().getProcedimentosQueVaoGerarHonorario();
		
		Map<Date, Set<ProcedimentoInterface>> procedimentosAgrupadosPordataRealizacao;
		try {
			procedimentosAgrupadosPordataRealizacao = CollectionUtils.groupBy(procedimentosQueVaoGerarHonorarios, "dataRealizacao", Date.class);
		} catch (Exception e) {
			throw new ValidateException(e.getMessage());
		}
		Set<Date> datasDeRealizacao = procedimentosAgrupadosPordataRealizacao.keySet();
		for (Date data : datasDeRealizacao) {
			if (data != null) {
				int procedimentosComPorcentagem100 = 0;
				Set<ProcedimentoInterface> procedimentosNaMesmaData = procedimentosAgrupadosPordataRealizacao.get(data);
				for (ProcedimentoInterface procedimentoInterface : procedimentosNaMesmaData) {
					if (procedimentoInterface.getPorcentagemProxy().equals(Constantes.PORCENTAGEM_100)) {
						procedimentosComPorcentagem100++;
					}
					if (procedimentosComPorcentagem100 > 1) {
						throw new ValidateException("Não é possível realizar mais de um procedimento com porcentagem 100% com a mesma data de cirurgia.");
					}
				}
			}
		}
	}

}
