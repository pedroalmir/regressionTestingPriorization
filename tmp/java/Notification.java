package br.com.infowaypi.servlets;

import java.util.List;

public class Notification {

	public static int SUCCESS = 1;
	public static int ERROR_CODE_NO_ROLE_ALLOWED = 2;
	public static int ERROR_CODE_GUIAS_NAO_ENCONTRADAS = 3;
	
	private int code;
	private String message;
	private List<String> data;
	private List<String> dataSegurado;
	private List<String> tipoDeGuia;
	
	public Notification(int code, String message, List<String> data, List<String> dataSegurado, List<String> links) { 
		this.code = code;
		this.message = message;
		this.data = data;
		this.dataSegurado = dataSegurado;
		this.tipoDeGuia = links;
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<String> getData() {
		return data;
	}
	public void setData(List<String> data) {
		this.data = data;
	}

	public List<String> getDataSegurado() {
		return dataSegurado;
	}

	public void setDataSegurado(List<String> dataSegurado) {
		this.dataSegurado = dataSegurado;
	}

	public List<String> getLinks() {
		return tipoDeGuia;
	}

	public void setLinks(List<String> links) {
		this.tipoDeGuia = links;
	}
	
}
