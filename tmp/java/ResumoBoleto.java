package br.com.infowaypi.ecare.relatorio.financeiro;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecare.financeiro.Cobranca;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;

public class ResumoBoleto {

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
	 * Situacao
	 */	
	private int quantidadeBoletosAbertos = 0;
	private BigDecimal valorBoletosAbertos = BigDecimal.ZERO;	
	
	private int quantidadeBoletosPagos = 0;
	private BigDecimal valorBoletosPagos = BigDecimal.ZERO;	
	private BigDecimal valorBoletosCobrado = BigDecimal.ZERO;	
	
	private int quantidadeBoletosCancelados = 0;
	private BigDecimal valorBoletosCancelados = BigDecimal.ZERO;	
	
	/**
	 * Todos
	 */
	private int quantidadeTodosBoletos = 0;
	private BigDecimal valorSomaTodosBoletos = BigDecimal.ZERO;
	
	
	Cobranca cobranca;
	private List<Cobranca> todosOsBoletos = new ArrayList<Cobranca>();
	
	public ResumoBoleto(){}
	
	public ResumoBoleto(List<Cobranca> todosOsBoletos) {
				this.todosOsBoletos = todosOsBoletos;
				this.computarQuantidadeSituacoes();
				
	}

	
	public int getQuantidadeBoletosAbertos() {
		return quantidadeBoletosAbertos;
	}

	public void setQuantidadeBoletosAbertos(int quantidadeBoletosAbertos) {
		this.quantidadeBoletosAbertos = quantidadeBoletosAbertos;
	}

	public BigDecimal getValorBoletosAbertos() {
		return valorBoletosAbertos;
	}

	public void setValorBoletosAbertos(BigDecimal valorBoletosAbertos) {
		this.valorBoletosAbertos = valorBoletosAbertos;
	}

	public int getQuantidadeBoletosPagos() {
		return quantidadeBoletosPagos;
	}

	public void setQuantidadeBoletosPagos(int quantidadeBoletosPagos) {
		this.quantidadeBoletosPagos = quantidadeBoletosPagos;
	}

	public BigDecimal getValorBoletosPagos() {
		return valorBoletosPagos;
	}

	public void setValorBoletosPagos(BigDecimal valorBoletosPagos) {
		this.valorBoletosPagos = valorBoletosPagos;
	}

	public BigDecimal getValorBoletosCobrado() {
		return valorBoletosCobrado;
	}

	public void setValorBoletosCobrado(BigDecimal valorBoletosCobrado) {
		this.valorBoletosCobrado = valorBoletosCobrado;
	}

	public int getQuantidadeBoletosCancelados() {
		return quantidadeBoletosCancelados;
	}

	public void setQuantidadeBoletosCancelados(int quantidadeBoletosCancelados) {
		this.quantidadeBoletosCancelados = quantidadeBoletosCancelados;
	}

	public BigDecimal getValorBoletosCancelados() {
		return valorBoletosCancelados;
	}

	public void setValorBoletosCancelados(BigDecimal valorBoletosCancelados) {
		this.valorBoletosCancelados = valorBoletosCancelados;
	}

	public int getQuantidadeTodosBoletos() {
		return quantidadeTodosBoletos;
	}

	public void setQuantidadeTodosBoletos(int quantidadeTodosBoletos) {
		this.quantidadeTodosBoletos = quantidadeTodosBoletos;
	}

	public BigDecimal getValorSomaTodosBoletos() {
		return valorSomaTodosBoletos;
	}

	public void setValorSomaTodosBoletos(BigDecimal valorSomaTodosBoletos) {
		this.valorSomaTodosBoletos = valorSomaTodosBoletos;
	}

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
				this.setValorBoletosPagos(valorBoletosPagos.add(cobranca.getValorCobrado()));
			}
			else if(cobranca.getSituacao().getDescricao().equals(BOLETOS_SITUACAO_CANCELADO)){
				quantidadeBoletosCancelados++;
				this.setValorBoletosCancelados(valorBoletosCancelados.add(cobranca.getValorCobrado()));
			}
		}
		
		this.setValorSomaTodosBoletos(valorSomaTodosBoletos.add(valorBoletosAbertos).add(valorBoletosPagos).add(valorBoletosCancelados));
	}
	
	

	public List<Cobranca> getTodosOsBoletos() {
		return todosOsBoletos;
	}

	public void setTodosOsBoletos(List<Cobranca> todosOsBoletos) {
		this.todosOsBoletos = todosOsBoletos;
	}
	
}
