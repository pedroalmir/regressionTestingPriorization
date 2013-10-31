package br.com.infowaypi.ecarebc.atendimentos.validators.fechamento;

import java.util.Date;

import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

public class FechamentoSituacaoSeguradoValidator implements FechamentoGuiaInternacaoValidator {

	@Override
	public void execute(GuiaInternacao guia, Boolean parcial, Date dataFinal,
			UsuarioInterface usuario) throws ValidateException {
		AbstractSegurado segurado = guia.getSegurado();
		boolean isSuspenso = segurado.isSituacaoAtual(SituacaoEnum.SUSPENSO.descricao());
		boolean isCancelado = segurado.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao());
		
		if (parcial) {
			Assert.isFalse((isCancelado || isSuspenso), "Caro usuário, esta guia não pode ser fechada parcialmente pois o segurado encontra-se na situação " + segurado.getSituacao().getDescricao() + ".");
			}
	}

}
