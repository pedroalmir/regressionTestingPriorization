package br.com.infowaypi.ecarebc.procedimentos;

import java.math.BigDecimal;
import java.util.Date;

import br.com.infowaypi.msr.user.UsuarioInterface;

public interface ProcedimentoCirurgicoInterface extends ProcedimentoInterface {

	public abstract void setPorcentagem(BigDecimal porcentagem);

	public abstract String getPorcentagemFormatada();
	
	public abstract void setDataRealizacao(Date dataRealizacao);
	
	public abstract Date getDataRealizacao();
	
	/**
	 * M�todo que muda a situa��o do procedimento para Glosado(a).
	 * N�o ignora a valida��o de honor�rios.
	 * @param usuario
	 */
	public void glosar(UsuarioInterface usuario);
}
