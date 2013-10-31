package br.com.infowaypi.ecare.validacao;

import br.com.infowaypi.msr.exceptions.ValidateException;

public class ValidatorTipoArquivoPDF {
	
	/**
	 * Retorna false de o array de bytes n�o representar uma imagem PDF.
	 * @param arquivo
	 * @return
	 * @throws ValidateException
	 */
	public static boolean isArquivoPDF(byte[] arquivo) throws ValidateException{
		
		byte[] pdf = new byte[] { (byte) 0x25, (byte) 0x50, (byte) 0x44, (byte) 0x46 };
		boolean match = true;
		
		for (int i = 0; i < pdf.length; i++) {
			if (pdf[i] != arquivo[i]) {
				match = false;
				break;
			}
		}
		
		if(!match){
			throw new ValidateException("Caro usu�rio, s� � permitido a importa��o de arquivos no formato PDF.");
		}
		
		return match;
	}

}
