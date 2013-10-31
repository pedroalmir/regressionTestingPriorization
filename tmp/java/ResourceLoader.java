package br.com.infowaypi.ecare.scheduller.sms;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Esta classe provê acesso em tempo de execução a arquivos (resources) situados no classpath.
 * @author Alan Santos
 *
 */
public class ResourceLoader {

	private ResourceBundle resource;

	public ResourceLoader(String resourceName) {
		resource = ResourceBundle.getBundle(resourceName, Locale.getDefault(), Thread.currentThread().getContextClassLoader());
	}

	public String getValue(String key){
		String value = null;
		Enumeration<String> keys = resource.getKeys(); 
		while(keys.hasMoreElements()) {
			if (keys.nextElement().equals(key)) {
				value = resource.getString(key);
				break;
			}
		}
		
		if (value != null)
			value = value.trim();
		
		return value;
	}
}
