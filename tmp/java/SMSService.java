package br.com.infowaypi.ecare.scheduller.sms;

import org.apache.commons.lang.StringUtils;
import br.com.infowaypi.msr.utils.Utils;

public class SMSService {

	public static String retirarCaracteres(String string, char... characters) {
		for (char character : characters) {
			string = StringUtils.remove(string, character);
		}
		
		if(Utils.isStringVazia(string))
			return "";
		string = string.replaceAll("[a-zA-Z]", "");
		return string;
	}

}
