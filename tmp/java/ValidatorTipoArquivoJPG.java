package br.com.infowaypi.ecare.questionarioqualificado.validators;

import br.com.infowaypi.msr.exceptions.ValidateException;

public class ValidatorTipoArquivoJPG {
	
	/**
	 * Retorna false de o array de bytes não representar uma imagem JPG.
	 * @param arquivo
	 * @return
	 * @throws ValidateException
	 */
	public static boolean isArquivoJPG(byte[] arquivo) throws ValidateException{
		
		byte[] jpg = new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0 };
		boolean match = true;
		
		for (int i = 0; i < jpg.length; i++) {
			if (jpg[i] != arquivo[i]) {
				match = false;
				break;
			}
		}
		
		if(!match){
			throw new ValidateException("Só é possível fazer upload de arquivos no formato JPEG.");
		}
		
		return match;
	}

}
