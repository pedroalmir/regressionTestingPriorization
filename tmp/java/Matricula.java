package br.com.infowaypi.ecare.segurados;

import java.io.Serializable;
import java.util.HashSet;

import br.com.infowaypi.ecare.financeiro.conta.ContaMatricula;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.financeiro.consignacao.Consignacao;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Classe para representar uma matrícula do titular no sistema
 * @author Danilo Nogueira Portela
 */

public class Matricula extends AbstractMatricula implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public Matricula() {
		super();
		this.consignacoes = new HashSet<Consignacao>();
		this.contasMatriculas = new HashSet<ContaMatricula>();
	}

	public void ativar(UsuarioInterface usuario) {
		Assert.isEquals(AbstractMatricula.FORMA_PAGAMENTO_BOLETO, this.getTipoPagamento(), MensagemErroEnum.SOMENTE_MATRICULA_BOLETO_PODE_SER_REATIVADA.getMessage());
		Assert.isFalse(this.isAtiva(), "Esta matrícula já está ativa.");
		this.setAtiva(true);
	}

}