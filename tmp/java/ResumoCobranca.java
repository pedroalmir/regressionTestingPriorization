package br.com.infowaypi.ecare.relatorio.financeiro;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecare.financeiro.Cobranca;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;

/**
 * @author anderson
 * Classe que representa o resumo de uma cobranca usando no relatorio de cobrancas
 * */
public class ResumoCobranca {

	/**
	 * Constantes para filtro de situacao dos boletos
	 * */
	public static final String TODOS_BOLETOS = "Todas";
	public static final String BOLETOS_SITUACAO_ABERTO = SituacaoEnum.ABERTO.descricao();
	public static final String BOLETOS_SITUACAO_PAGO = SituacaoEnum.PAGO.descricao();
	public static final String BOLETOS_SITUACAO_CANCELADO = SituacaoEnum.CANCELADO.descricao();
	
	public static final Integer DATA_DE_VENCIMENTO = 1;
	public static final Integer DATA_DE_PAGAMENTO = 2;
		
	/**
	 * Atributos referentes aos boletos que serão 'setados' nesta classe
	 */	
	private int quantidadeBoletosAbertos = 0;
	private BigDecimal valorBoletosAbertos = BigDecimal.ZERO;	
	
	private int quantidadeBoletosPagos = 0;
	private BigDecimal valorBoletosPagos = BigDecimal.ZERO;	
	private BigDecimal valorBoletosCobrado = BigDecimal.ZERO;	
	
	private int quantidadeBoletosCancelados = 0;
	private BigDecimal valorBoletosCancelados = BigDecimal.ZERO;	
	
	private int quantidadeTodosBoletos = 0;
	private BigDecimal valorSomaTodosBoletos = BigDecimal.ZERO;
		
	Cobranca cobranca;
	//Collection que armazena todos os boletos
	private List<Cobranca> todosOsBoletos = new ArrayList<Cobranca>();
	
	public ResumoCobranca(){}
	
	public ResumoCobranca(List<Cobranca> todosOsBoletos) 
	{
		this.todosOsBoletos = todosOsBoletos;
		this.computarQuantidadeSituacoes();
	}

	/**
	 * Metodo que retorna a quantidade de boletos abertos
	 * */
	public int getQuantidadeBoletosAbertos() {
		return quantidadeBoletosAbertos;
	}
	/**
	 * @param quantidadeBoletosAbertos
	 * Metodo que configura a quantidade de boletos em aberto
	 * */
	public void setQuantidadeBoletosAbertos(int quantidadeBoletosAbertos) {
		this.quantidadeBoletosAbertos = quantidadeBoletosAbertos;
	}
	/**
	 * Metodo que retorna o valor dos boletos em Aberto
	 * */
	public BigDecimal getValorBoletosAbertos() {
		return valorBoletosAbertos;
	}
	/**
	 * @param valorBoletosAbertos
	 * Metodo que configura o valor dos boletos em Aberto
	 * */
	public void setValorBoletosAbertos(BigDecimal valorBoletosAbertos) {
		this.valorBoletosAbertos = valorBoletosAbertos;
	}
	/**
	 * Metodo que retorna a quantidade de boletos pagos
	 * */
	public int getQuantidadeBoletosPagos() {
		return quantidadeBoletosPagos;
	}
	/**
	 * @param quantidadeBoletosPagos
	 * Metodo qu configura a quantidade de boletos Pagos
	 * */
	public void setQuantidadeBoletosPagos(int quantidadeBoletosPagos) {
		this.quantidadeBoletosPagos = quantidadeBoletosPagos;
	}
	/**
	 *  Metodo que retorna o valor dos boletos Pagos
	 * */
	public BigDecimal getValorBoletosPagos() {
		return valorBoletosPagos;
	}
	/**
	 * @param valorBoletosPagos
	 * Metodo que configura o valor dos boletos Pagos
	 * */
	public void setValorBoletosPagos(BigDecimal valorBoletosPagos) {
		this.valorBoletosPagos = valorBoletosPagos;
	}
	/**
	 *  Metodo que retorna o valor dos boletos Cobrados
	 * */
	public BigDecimal getValorBoletosCobrado() {
		return valorBoletosCobrado;
	}
	/**
	 * @param valorBoletosCobrado
	 * Metodo que configura o valor dos boletos Cobrados
	 * */
	public void setValorBoletosCobrado(BigDecimal valorBoletosCobrado) {
		this.valorBoletosCobrado = valorBoletosCobrado;
	}
	/**
	 * Metodo que retorna a quantidade de boletos cancelados
	 * */
	public int getQuantidadeBoletosCancelados() {
		return quantidadeBoletosCancelados;
	}
	/**
	 * @param quantidadeBoletosCancelados
	 * Metodo que configura a qunatidade de oletos cancelados
	 * */
	public void setQuantidadeBoletosCancelados(int quantidadeBoletosCancelados) {
		this.quantidadeBoletosCancelados = quantidadeBoletosCancelados;
	}
	/**
	 *  Metodo que retorna o valor dos boletos Cancelados 
	 * */
	public BigDecimal getValorBoletosCancelados() {
		return valorBoletosCancelados;
	}
	/**
	 * @param valorBoletosCancelados
	 * Metodo que configura o valor dos boletos cancelados
	 * */
	public void setValorBoletosCancelados(BigDecimal valorBoletosCancelados) {
		this.valorBoletosCancelados = valorBoletosCancelados;
	}
	/**
	 * Metodo que retorna a quantidade de todos os boletos 
	 * */
	public int getQuantidadeTodosBoletos() {
		return quantidadeTodosBoletos;
	}
	/**
	 * @param quantidadeTodosBoletos
	 * Metodo que configura a quantidade de todos os boletos
	 * */
	public void setQuantidadeTodosBoletos(int quantidadeTodosBoletos) {
		this.quantidadeTodosBoletos = quantidadeTodosBoletos;
	}
	/**
	 * Metodo que retorna a soma dos valores de todos os boletos
	 * */
	public BigDecimal getValorSomaTodosBoletos() {
		return valorSomaTodosBoletos;
	}
	/**
	 * @param valorSomaTodosBoletos
	 * Metodo que configura a soma de todos os boletos 
	 * */
	public void setValorSomaTodosBoletos(BigDecimal valorSomaTodosBoletos) {
		this.valorSomaTodosBoletos = valorSomaTodosBoletos;
	}
	/**
	 * Metodo que retorna todos os boletos 
	 * */
	public List<Cobranca> getTodosOsBoletos() {
		return todosOsBoletos;
	}
	/**
	 * @param todosOsBoletos
	 * Metodo que configura o collection de todos os boletos  
	 * */
	public void setTodosOsBoletos(List<Cobranca> todosOsBoletos) {
		this.todosOsBoletos = todosOsBoletos;
	}

	/**
	 * Metodo que configura a quantidade de boletos por situação 
	 * */
	public void computarQuantidadeSituacoes(){
		
		quantidadeTodosBoletos = todosOsBoletos.size();
		
		for (Cobranca cobranca : todosOsBoletos) {
			
			if(cobranca.getSituacao().getDescricao().equals(BOLETOS_SITUACAO_ABERTO)){
				quantidadeBoletosAbertos++;
				this.setValorBoletosAbertos(valorBoletosAbertos.add(cobranca.getValorCobrado()));
			}
			else if(cobranca.getSituacao().getDescricao().equals(BOLETOS_SITUACAO_PAGO)){
				quantidadeBoletosPagos++;
				this.setValorBoletosCobrado(valorBoletosCobrado.add(cobranca.getValorCobrado()));
				this.setValorBoletosPagos(valorBoletosPagos.add(cobranca.getValorPago()));
			}
			else if(cobranca.getSituacao().getDescricao().equals(BOLETOS_SITUACAO_CANCELADO)){
				quantidadeBoletosCancelados++;
				this.setValorBoletosCancelados(valorBoletosCancelados.add(cobranca.getValorCobrado()));
			}
		}
		
		this.setValorSomaTodosBoletos(valorSomaTodosBoletos.add(valorBoletosAbertos).add(valorBoletosPagos).add(valorBoletosCancelados));
	}
		
}
