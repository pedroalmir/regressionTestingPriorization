/**
 * 
 */
package br.com.infowaypi.ecarebc.atendimentos;

import java.math.BigDecimal;

import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.msr.utils.MoneyCalculation;

/**
 * Classe que tem como objetivo guardar momentaneamente dados
 * referentes a profissionais e suas funçoes desempenhadas em
 * um dado procedimento cirurgico de uma guia completa. Esses
 * objetos sao transientes.
 * @author Marcus bOolean
 *
 */
public class ValoresProfissional {
	public static final String PRIMEIRO_AUXILIAR = "1º Auxiliar";
	public static final String SEGUNDO_AUXILIAR = "2º Auxiliar";
	public static final String TERCEIRO_AUXILIAR = "3º Auxiliar";
	public static final String ANESTESISTA = "Anestesista";
	
	
	private Profissional profissional;
	private ProcedimentoInterface procedimento;
	private BigDecimal valor;
	private String funcao;
	
	public Profissional getProfissional() {
		return profissional;
	}
	public void setProfissional(Profissional profissional) {
		this.profissional = profissional;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = MoneyCalculation.rounded(valor);
	}
	public String getFuncao() {
		return funcao;
	}
	public void setFuncao(String funcao) {
		this.funcao = funcao;
	}
	public ProcedimentoInterface getProcedimento() {
		return procedimento;
	}
	public void setProcedimento(ProcedimentoInterface procedimento) {
		this.procedimento = procedimento;
	}

}
