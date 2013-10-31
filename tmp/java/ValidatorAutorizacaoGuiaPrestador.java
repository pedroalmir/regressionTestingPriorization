package br.com.infowaypi.ecare.validacao.services;

import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.exceptions.AutorizacaoExameException;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.msr.utils.Utils;

public class ValidatorAutorizacaoGuiaPrestador {

	/**
	 * Assegura que pelo menos um dos parametros foi informado
	 * @param autorizacao
	 * @param prestador
	 */
	public void validarEntrada(String autorizacao, Prestador prestador){
		boolean isPrestadorInformado = prestador == null;
		boolean isAutorizacaoInformada = Utils.isStringVazia(autorizacao);
		
		if(isAutorizacaoInformada && isPrestadorInformado)
			throw new AutorizacaoExameException(MensagemErroEnumSR.PREENCHER_PELO_MENOS_1_CAMPO.getMessage());
	}
	
	/**
	 * Valida se foi encontrado alguma guia
	 * @param resumo
	 */
	public void validaGuiasEncontradas(ResumoGuias resumo){
		if(resumo.getGuias().isEmpty())
			throw new AutorizacaoExameException(MensagemErroEnum.NENHUMA_GUIA_ENCONTRADA.getMessage());
	}
	
	

}
