package br.com.infowaypi.config;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Essa classe guarda as configurações para locaclização de arquivos no servidor.<br>
 * Adaptação da classe Config.java do Uniplam.<br>
 * A classe trabalha de forma generica os caracteres separadores no caminho de um arquivo.
 * @author Emanuel
 *
 */
public class Config {
	private Config() { }
	
	/*
	 * Caminhos para a pasta dos arquivos
	 */

	public static final String filesPathLinux = File.separatorChar + "home" + File.separatorChar + "homologacao" + File.separatorChar + "arquivosDeConfiguracao";
	public static final String filesPathWindows = "C:" + File.separatorChar + "Users" + File.separatorChar + "Emanuel" + File.separatorChar + "WorkColmeia" + File.separatorChar + "SaudeRecife_Desenvolvimento" + File.separatorChar + "template" + File.separatorChar + "arquivos" + File.separatorChar + "redeCredenciada";
	
	//definir host do servidor antes do deploy
	public static final String HOST = "uniplam.com.br";
	public static final String DESENVOLVEDORES_UNIPLAM = "desenvolvedores.UN@infoway-pi.com.br";
	public static final String TELEFONE_UNIPLAM = "(86)8812-8548";

	public static String getFilesPath() {
		if (System.getProperty("os.name").equalsIgnoreCase("Linux")) {
			return filesPathLinux;
		}
		
		return filesPathWindows;
	}
	
	public static String getFilesPath(String file) {
		return getFilesPath().concat(System.getProperty("file.separator") + file);
	}

	public static String buildPath(String... folders) {
		String path = "";
		for (String folder : folders) {
			path = path.concat(folder).concat(File.separator);
		}
		
		return path;
	}
	
	public static String verificarHostName(){
		try {
			return InetAddress.getLocalHost().getHostName();
		    
		} catch (UnknownHostException e) {}
		
		return null;
	}
	
}
