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
	 * Método que muda a situação do procedimento para Glosado(a).
	 * Não ignora a validação de honorários.
	 * @param usuario
	 */
	public void glosar(UsuarioInterface usuario);
}
