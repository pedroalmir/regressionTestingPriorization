package br.com.infowaypi.ecarebc.procedimentos.validators;

import br.com.infowaypi.ecarebc.atendimentos.GuiaExameOdonto;
import br.com.infowaypi.ecarebc.odonto.EstruturaOdonto;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOdonto;

/**
 * Classe para validação básica EstruturasOdonto
 * @author Dannylvan
 */
public class ProcedimentoEstruturasOdontoValidator extends AbstractProcedimentoValidator<ProcedimentoOdonto<EstruturaOdonto>, GuiaExameOdonto> {

	@Override
	protected boolean templateValidator(ProcedimentoOdonto<EstruturaOdonto> proc, GuiaExameOdonto guia) throws Exception {
		for (EstruturaOdonto e : proc.getEstruturas()){
			e.validate(proc);
		}	
		return false;
	}

}
