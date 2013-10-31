package br.com.infowaypi.ecare.services;

import java.math.BigDecimal;

import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * 
 * @author Idelvane
 *
 */
public class SimulacaoInclusaoService {
	
	public InclusaoTitularDependente simular(BigDecimal valorBruto, String numeroDoCartao, String cpf, InclusaoTitularDependente incluso) throws Exception{

		if(valorBruto == null)
			throw new ValidateException("O campo salário bruto deve ser informado!");
		
		BigDecimal valor = valorBruto;
		incluso.setValorBruto(valor);
		incluso.setNumeroDoCartao(numeroDoCartao);
		incluso.setCpf(cpf);

		incluso.getValorBrutoCalculado();
				
		return incluso;
	}
	
	public void finalizar(InclusaoTitularDependente incluso){}
}
