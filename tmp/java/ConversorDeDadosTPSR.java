package br.com.infoway.ecare.services.tarefasCorrecao.migracaoNovaTabela;

import java.math.BigDecimal;

import br.com.infowaypi.ecarebc.enums.SexoEnum;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.msr.utils.Utils;

public class ConversorDeDadosTPSR {

	public Integer processaLocal(String localAtendimento) {
		if (!Utils.isStringVazia(localAtendimento)) {
			String localUpperCase = localAtendimento.trim().toUpperCase();
			if (localUpperCase.equals("AMBOS")) {
				return TabelaCBHPM.TIPO_AMBOS;
			}
			if (localUpperCase.startsWith("AMB")) {
				return TabelaCBHPM.TIPO_AMBULATORIAL;
			}
			if (localUpperCase.startsWith("HOSP")) {
				return TabelaCBHPM.TIPO_HOSPITALAR;
			}
			return null;
		} else {
			return null;
		}
	}

	public String processaString(String str) {
		if (Utils.isStringVazia(str)) {
			return null;
		}
		return str;
	}
	
	public Boolean processaSimNao(String str) {
		if (Utils.isStringVazia(str)) {
			return null;
		}
		String upperCase = str.trim().toUpperCase();
		boolean sim = upperCase.equals("SIM");
		boolean nao = upperCase.matches("N[AÃ]O");
		if (sim || nao) {
			return sim;
		} else {
			return null;
		}
	}

	public boolean processaMarcacao(String str) {
		return !Utils.isStringVazia(str);
	}

	public Integer processaQuantidade(String str) {
		if (Utils.isStringVazia(str)) {
			return null;
		} else {
			return Integer.valueOf(str);
		}
	}

	public Integer processaSexo(String str) {
		if (!Utils.isStringVazia(str)) {
			char primeiraLetra = str.trim().toUpperCase().charAt(0);
			if (primeiraLetra == 'F') {
				return SexoEnum.FEMININO.value();
			}
			if (primeiraLetra == 'M') {
				return SexoEnum.MASCULINO.value();
			}
			return SexoEnum.AMBOS.value();
		} else {
			return null;
		}
	}

	public Integer processaUnicidade(String str) {
		if (Utils.isStringVazia(str)) {
			return 3;
		} else {
			try {
				return Integer.valueOf(str);
			} catch (Exception e) {
				return null;
			}
		}
	}

	public BigDecimal processaValor(String str) {
		if (Utils.isStringVazia(str)) {
			return null;
		}
		return new BigDecimal(str);
	}
	
	public Integer processaComplexidade(String str) {
		if (str.equals("PBC")) {
			return TabelaCBHPM.COMPLEXIDADE_PBC;
		} else if (str.equals("PMC")) {
			return TabelaCBHPM.COMPLEXIDADE_PMC;
		} else if (str.equals("PAC")) {
			return TabelaCBHPM.COMPLEXIDADE_PAC;
		} else {
			return null;
		}
	}
	
	public String processaPorte(String str) {
		if (str.length() == 2) {
			return "P0" + str;
		} else if (str.length() == 3) {
			return "P" + str;
		} else if (str.length() == 4) {
			return str;
		}
		return null;
	}
}
