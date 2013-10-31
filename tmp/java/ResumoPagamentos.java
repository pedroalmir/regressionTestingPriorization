package br.com.infowaypi.ecarebc.consumo;


/**
 * Resumo de pagamentos.
 * @author Mário Sérgio Coelho Marroquim
 */
public class ResumoPagamentos {
/*
	private float previsaoAPargar;
	private float confirmadoAPargar;
	private float totalAPargar;
	
	private float previsaoAReceber;
	private float confirmadoAReceber;
	private float totalAReceber;
	
	private List<ContaInterface> contas;
	
	public ResumoPagamentos(List<ContaInterface> contas) {
		super();
		this.contas = contas;
		computarResumo();
	}

	private void computarResumo() {
		
		for (ContaInterface conta : this.contas){
			if(conta.isSituacaoAtual(ContaInterface.CONTA_CANCELADA))
				continue;
			
			boolean isContaPaga = conta.isSituacaoAtual(ContaInterface.CONTA_PAGA);
			boolean isContaAPagar = conta.getTipoDeConta().equals(ContaInterface.TIPO_A_PAGAR);
			boolean isContaAReceber = conta.getTipoDeConta().equals(ContaInterface.TIPO_A_RECEBER);
			
			if (isContaAPagar){
				if(isContaPaga)
					confirmadoAPargar += conta.getValorPago();
				else
					previsaoAPargar += conta.getValorCobrado();
				
				totalAPargar += (confirmadoAPargar+previsaoAPargar);
			}
			else if (isContaAReceber){
				if(isContaPaga)
					confirmadoAReceber += conta.getValorPago();
				else
					previsaoAReceber += conta.getValorCobrado();
				
				totalAReceber += (confirmadoAReceber+previsaoAReceber);
			}
		}
	}
	
	public boolean isVazio(){
		int totalAPargar = Math.round(this.totalAPargar);
		int totalAReceber = Math.round(this.totalAReceber);
		
		if(totalAPargar == 0 && totalAReceber == 0)
			return true;

		return false;
	}

	public float getConfirmadoAPargar() {
		return confirmadoAPargar;
	}

	public void setConfirmadoAPargar(float confirmadoAPargar) {
		this.confirmadoAPargar = confirmadoAPargar;
	}

	public float getConfirmadoAReceber() {
		return confirmadoAReceber;
	}

	public void setConfirmadoAReceber(float confirmadoAReceber) {
		this.confirmadoAReceber = confirmadoAReceber;
	}

	public List<ContaInterface> getContas() {
		return contas;
	}

	public void setContas(List<ContaInterface> contas) {
		this.contas = contas;
	}

	public float getPrevisaoAPargar() {
		return previsaoAPargar;
	}

	public void setPrevisaoAPargar(float previsaoAPargar) {
		this.previsaoAPargar = previsaoAPargar;
	}

	public float getPrevisaoAReceber() {
		return previsaoAReceber;
	}

	public void setPrevisaoAReceber(float previsaoAReceber) {
		this.previsaoAReceber = previsaoAReceber;
	}

	public float getTotalAPargar() {
		return totalAPargar;
	}

	public void setTotalAPargar(float totalAPargar) {
		this.totalAPargar = totalAPargar;
	}

	public float getTotalAReceber() {
		return totalAReceber;
	}

	public void setTotalAReceber(float totalAReceber) {
		this.totalAReceber = totalAReceber;
	}
*/
}