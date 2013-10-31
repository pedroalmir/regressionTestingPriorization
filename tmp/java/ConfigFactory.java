package br.com.infowaypi.ecarebc.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class ConfigFactory {
	
	//definir as classes de configuração
	private static ConfigFactory config;
	private ResourceBundle resource;
	
	public static ConfigFactory getInstance(){
		if (config == null){
			return new ConfigFactory();
		} else {
			return config;
		}
	}
	
	private ConfigFactory (){
		try{
			resource =  ResourceBundle.getBundle("e-care", Locale.getDefault(), Thread.currentThread().getContextClassLoader());
			//carregar os campos de configuração
		} catch (Exception e) {
		}
	}
	
	public int getValorInteger(String chave){
		return Integer.parseInt(resource.getString(chave));
	}
	public String getValorString(String chave){
		return resource.getString(chave);
	}
}
