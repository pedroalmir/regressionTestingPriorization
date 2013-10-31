package br.com.infowaypi.ecarebc.atendimentos.validators;

import java.util.Date;

import br.com.infowaypi.msr.utils.Utils;
 
/**
  * @author wislanildo
  */
public enum EnumDatasAuxiliares {
	
	DATA_IMPLANTACAO_DEMANDA_27(Utils.parse("21/03/2012"), "Demanda 27. Não pagar procedimentos sem nome do profissional.");
	
	private EnumDatasAuxiliares(Date data, String descricao) {
		this.data = data;  
		this.descricao = descricao;
	}
	
	private Date data;
	private String descricao;
	
	public Date getData() {
		return data;
	}

}
