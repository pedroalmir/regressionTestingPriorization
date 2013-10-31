package br.com.infowaypi.ecare.utils;

import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.msr.utils.Assert;

public class ProfissionalUtils {
	
	
	public static Profissional getProfissionais(Profissional profissionalCRM,Profissional profissionalNOME){
		
		
		Boolean isNenhumProfissionalInformado = (profissionalCRM == null) && (profissionalNOME == null);
		Boolean isDoisProfissionaisInformados = (profissionalCRM != null) && (profissionalNOME != null);
		Boolean isCRMProfissionalInformado = (profissionalCRM != null) && (profissionalNOME == null);
		Boolean isNomeProfissionalInformado = (profissionalCRM == null) && (profissionalNOME != null);
		Boolean isDoisProfissionaisDiferentes = isDoisProfissionaisInformados && !profissionalCRM.equals(profissionalNOME);
		
		Assert.isFalse(isNenhumProfissionalInformado, MensagemErroEnum.PROFISSIONAL_NAO_INFORMADO.getMessage());
		Assert.isFalse(isDoisProfissionaisDiferentes, MensagemErroEnumSR.PROFISSIONAL_DIFERENTE.getMessage());
		
		Profissional profissional = null;
		
		if(isDoisProfissionaisInformados && profissionalCRM.equals(profissionalNOME))
			profissional = profissionalCRM;
		else if(isCRMProfissionalInformado)
			profissional = profissionalCRM;
		else if(isNomeProfissionalInformado)
			profissional = profissionalNOME;
	
		return profissional;
	}
	
	/**
	 * Faz as verificações e devolve o profissional correto se informado validando apenas se os profissionais são diferentes, caso contrario retorna nulo. 
	 * @param profissionalCRM
	 * @param profissionalNOME
	 * @return
	 */
	public static Profissional getProfissionalSeInformado(Profissional profissionalCRM,Profissional profissionalNOME){
		
		Boolean isDoisProfissionaisInformados = (profissionalCRM != null) && (profissionalNOME != null);
		Boolean isCRMProfissionalInformado = (profissionalCRM != null) && (profissionalNOME == null);
		Boolean isNomeProfissionalInformado = (profissionalCRM == null) && (profissionalNOME != null);
		Boolean isDoisProfissionaisDiferentes = isDoisProfissionaisInformados && !profissionalCRM.equals(profissionalNOME);
		
		Assert.isFalse(isDoisProfissionaisDiferentes, MensagemErroEnumSR.PROFISSIONAL_DIFERENTE.getMessage());
		
		Profissional profissional = null;
		
		if(isDoisProfissionaisInformados && profissionalCRM.equals(profissionalNOME))
			profissional = profissionalCRM;
		else if(isCRMProfissionalInformado)
			profissional = profissionalCRM;
		else if(isNomeProfissionalInformado)
			profissional = profissionalNOME;
		
		return profissional;
	}
	
}
