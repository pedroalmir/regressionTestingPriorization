package br.com.infowaypi.ecarebc.financeiro.conta;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import br.com.infowaypi.msr.utils.MoneyCalculation;

public class GeradorCodigoBarras {
	 
	public static String multiplicarSequencia(String numero){
		//multiplicacao pela sequencia 1 2
		String multiplicacao="";
		for(int i =0; i<numero.length();i++){
			int n = Integer.parseInt(numero.substring(i,i+1));
			if(i%2==0)
				multiplicacao += String.valueOf(n*2);
			else
				multiplicacao += String.valueOf(n*1);
		}
		return multiplicacao;
	}
	
	public static int somarDigitos(String numero){
		int soma = 0;
		int index  = 0;
		while(index< numero.length()){
			soma += Integer.parseInt(numero.substring(index,index+1)); 
			index++;
		}
		return soma;
	}
	
	public static int getDAC(String numero){
		String multiplicacao = multiplicarSequencia(numero);
		int soma = somarDigitos(multiplicacao);
		int resto = soma%10;
		if (resto==0)
			return 0;
		else
			return 10-resto;
	}
	/*
	 * Nesse metodo deve-se passar uma string de 43 caracteres contendo
	 * os numeros 
	 */
	public static String inserirVerificadorGeral(StringBuilder numero){
		int verificadorGeral = getDAC(numero.toString());
		String resultado = numero.insert(3,verificadorGeral).toString();
		return resultado;
	}
	
	public static String inserirVerificadores(StringBuilder numero){
		String resultado = "";
		String parte1 = numero.substring(0,11);
		resultado += parte1+"-"+getDAC(parte1)+" " ;
		
		String parte2 = numero.substring(11,22);
		resultado += parte2+"-"+getDAC(parte2)+" ";

		String parte3 = numero.substring(22,33);
		resultado += parte3+"-"+getDAC(parte3)+" ";
		
		String parte4 = numero.substring(33,44);
		resultado += parte4+"-"+getDAC(parte4);
		
		return resultado;
	}
	
	private static String formataValor(BigDecimal valor){
		String result = MoneyCalculation.rounded(valor).toString();
		result = result.replace(".","");
		result = StringUtils.leftPad(result, 11, '0');
		return result;
	}
	
	
	public static String getCodigoBarrasFormatado(String ident_produto,String ident_segmento,
			String ident_valor_real,BigDecimal valor,String ident_empresa, String campo_livre){
		
		campo_livre = StringUtils.leftPad(campo_livre,21,"0");
		
		String resultado = ident_produto + ident_segmento + ident_valor_real;
		resultado += formataValor(valor) + ident_empresa + campo_livre;
		resultado = inserirVerificadorGeral(new StringBuilder(resultado));
		resultado = inserirVerificadores(new StringBuilder(resultado));
		return resultado;
		
	}	
	
	
	
	public static String getCodigoBarras(String ident_produto,String ident_segmento,
			String ident_valor_real,BigDecimal valor,String ident_empresa, String campo_livre){
		
		campo_livre = StringUtils.leftPad(campo_livre,21,"0");
		
		String resultado = ident_produto + ident_segmento + ident_valor_real;
		resultado += formataValor(valor) + ident_empresa + campo_livre;
		resultado = inserirVerificadorGeral(new StringBuilder(resultado));
		return resultado;
		
	}	
	
}
