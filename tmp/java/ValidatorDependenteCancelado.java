package br.com.infowaypi.ecare.services.cadastros;

import br.com.infowaypi.ecare.segurados.Dependente;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;

public class ValidatorDependenteCancelado {
	
	public void validate(Dependente dependente){
		
		if(dependente.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()))
			throw new RuntimeException("Caro usuário, não é permitido migrar dependentes cancelados.");
		
		if(dependente.getTitular().isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()))
			throw new RuntimeException("Caro usuário, não é permitido migrar dependentes que possuam titular cancelado.");
	}
}