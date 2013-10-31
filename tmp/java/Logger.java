package br.com.infoway.ecare.services.tarefasCorrecao.migracaoNovaTabela;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

	private StringBuilder log;
	private File arquivo;
	private FileWriter writer;

	public Logger(File arquivo) throws IOException {
		this.log = new StringBuilder();
		this.arquivo = arquivo;
		this.writer = new FileWriter(arquivo);
	}

	public void log(String str) {
		this.log.append(str+";");
	}

	public void log(StringBuilder builder) {
		this.log.append(builder);
	}
	
	public void newRecord() {
		this.log.append(System.getProperty("line.separator"));
	}

	public File getArquivo() {
		return arquivo;
	}

	public void save() throws IOException {
		String logValue = this.log.toString();
		this.writer.write(logValue);
		this.writer.close();
	}

}
