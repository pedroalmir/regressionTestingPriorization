package br.com.infowaypi.ecare.financeiro.boletos;

import org.apache.commons.lang.StringUtils;

import br.com.infowaypi.jbank.bancos.Banco;
import br.com.infowaypi.jbank.boletos.BoletoBean;

public class CaixaEconomicaSIGCB extends Banco{

	public static final String CARTEIRA_REGISTRADA = "1";
	public static final String CARTEIRA_SEM_REGISTRO = "2";
	public static final String EMISSAO_DO_BLOQUETO = "4";
	public static final String C1 = "2";
	public static final String C2 = "4";
	
	private BoletoBean boleto;
	
	public CaixaEconomicaSIGCB(BoletoBean boleto) {
		this.boleto = boleto;
	}
	
	

    public String getCampoLivre() {
    	String codigoCedente = boleto.getCodCliente();
		String campoLivre =  codigoCedente + getDigitoVerificadorCodigoCedente(codigoCedente) + getNossoNumeroSequencia1() + C1 + getNossoNumeroSequencia2() + C2 + getNossoNumeroSequencia3();
    	return campoLivre + this.getDigitoVerificador(campoLivre);
    }
    
	@Override
	public String getCodigoBarras() {
		return getNumero() + String.valueOf(boleto.getMoeda()) + String.valueOf(getCampo4()) + String.valueOf(getCampo5()) + getCampoLivre();
	}
	
    /**
     * Metodo que monta o primeiro campo do codigo de barras 
     * Este campo como os demais e feito a partir do da documentacao do banco
     * A documentacao do banco tem a estrutura de como monta cada campo, depois disso
     * é só concatenar os campos como no exemplo abaixo.
     */
	private String getCampo1() {
		String campo = getNumero() + String.valueOf(boleto.getMoeda()) + getCampoLivre().substring(0,5);

		return boleto.getDigitoCampo(campo,2);		
	}
	
    /**
     * ver documentacao do banco para saber qual a ordem deste campo 
     */
	private String getCampo2() {
		String campo = getCampoLivre().substring(5,15);
		
		return boleto.getDigitoCampo(campo,1);
	}

    /**
     * ver documentacao do banco para saber qual a ordem deste campo
     */
	private String getCampo3() {
		String campo = getCampoLivre().substring(15);
		
		return boleto.getDigitoCampo(campo,1);		
	}
	
    /**
     * ver documentacao do banco para saber qual a ordem deste campo
     */
	private String getCampo4() {
		String campo = 	getNumero() + String.valueOf(boleto.getMoeda()) +
						boleto.getFatorVencimento() + boleto.getValorTitulo() + getCampoLivre();
								
		return boleto.getDigitoCodigoBarras(campo);
	}
	
    /**
     * ver documentacao do banco para saber qual a ordem deste campo
     */
	private String getCampo5() {
		String campo = boleto.getFatorVencimento() + boleto.getValorTitulo();
		return campo;
	}
	
	private String getNossoNumeroSequencia1() {
		return boleto.getNossoNumero().substring(0, 3);
	}
	
	private String getNossoNumeroSequencia2() {
		return boleto.getNossoNumero().substring(3,6);
	}

	private String getNossoNumeroSequencia3() {
		return boleto.getNossoNumero().substring(6);
	}

	private int getDigitoVerificador(String campoLivre) {

		if(campoLivre == null || campoLivre.trim()=="" || campoLivre.length() ==0)
			throw new RuntimeException("O boleto n�o possui campo livre.");
		
		int     d1, pesoFinal;
		int     digitoVerificador, pesoInicial, resto;
		int     digitoCorrente;
		
		
		d1 = 0;
		digitoVerificador= resto = 0;
		pesoInicial = 2;
		int     incremento = pesoInicial;
		pesoFinal = 9;
		String campo = "";
		campo = StringUtils.reverse(campoLivre);
		for (int nCount = 0; nCount < campo.length(); nCount++){
			digitoCorrente = Integer.valueOf (campo.substring(nCount,nCount+1)).intValue();
			d1 = d1 + (incremento * digitoCorrente);
			incremento++;
			if(incremento > pesoFinal)
				incremento = pesoInicial;
			
		}
		resto = (d1 % 11);
		digitoVerificador = 11 - resto;
		if(digitoVerificador >9)
			digitoVerificador = 0;
		
		return digitoVerificador;
	}

	private String getDigitoVerificadorCodigoCedente(
			String campoLivre) {
		return "" + getDigitoVerificador(campoLivre); 
	}

	@Override
	public String getLinhaDigitavel() {
		return 	getCampo1().substring(0,5) + "." + getCampo1().substring(5) + "  " + 
		getCampo2().substring(0,5) + "." + getCampo2().substring(5) + "  " +
		getCampo3().substring(0,5) + "." + getCampo3().substring(5) + "  " +
		getCampo4() + "  " + getCampo5(); 
	}
	@Override
	public String getNossoNumeroFormatado() {
		String nossoNumeroFormatado = CARTEIRA_SEM_REGISTRO + EMISSAO_DO_BLOQUETO + boleto.getNossoNumero();
		
		return nossoNumeroFormatado + "-" + boleto.getDigitoVerificadorNossoNumeroSemNove(nossoNumeroFormatado);
	}
	@Override
	public String getNumero() {
        return "104";
	}
	

}
