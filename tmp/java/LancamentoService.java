package br.com.infowaypi.ecare.services.financeiro.faturamento;

import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.financeiro.faturamento.LancamentoDeGlosaService;
import br.com.infowaypi.msr.exceptions.ValidateException;

public class LancamentoService extends LancamentoDeGlosaService{
	
	public ResumoGuias buscarGuias(Segurado segurado, String autorizacao) throws ValidateException {
		return super.buscarGuias(segurado, autorizacao);
	}

	public GuiaSimples buscarGuias(String autorizacao) throws ValidateException {
		return super.buscarGuias(autorizacao);
	}
}
