package br.com.infowaypi.ecare.services.honorarios.validators;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GeradorGuiaHonorarioInterface;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;


public class GuiaHonorarioProcedimentoComProfissionalEmGuiaPagaValidator extends CommandValidator{
	
	public void execute() {
		boolean isGuiaPaga = SituacaoEnum.PAGO.descricao().equals(((GuiaSimples<ProcedimentoInterface>) this.getGuiaOrigem()).getSituacao().getDescricao());
		Procedimento procedimento = getProcedimentoComProfissional(this.getGuiaOrigem());
		
		if(isGuiaPaga && procedimento != null){
			String codigoCBHPM = procedimento.getProcedimentoDaTabelaCBHPM().getCodigo();
			throw new RuntimeException("N�o � poss�vel registrar honor�rio para o procedimento " + codigoCBHPM 
					+ " para o grau cirurgi�o. Em guias na situa��o 'Pago(a)' s� � possivel gerar honorarios para procedimentos que ainda n�o possuem o profissional respons�vel.");
		}
	}
	
	private Procedimento getProcedimentoComProfissional(GeradorGuiaHonorarioInterface guia){
		for (ProcedimentoInterface proc : guia.getProcedimentosQueVaoGerarHonorario()) {
			boolean isProcedimentoCirugico 	= proc.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO;
			boolean temProfissional 		= proc.getProfissionalResponsavel()!=null;
			
			if (isProcedimentoCirugico && temProfissional)  {
				return (Procedimento) proc;
			}
		}
		return null;
	}
}
