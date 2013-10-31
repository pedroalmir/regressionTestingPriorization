package br.com.infowaypi.ecare.arquivos;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.jheat.reports.JHeatReport;

public class ArquivoRedeCredenciadaPrinter {

	private static String getUrl() {
		return "";
	}
	
	public byte[] createPDF(List<Prestador> prestadores) throws Exception{
		List<Object> itens = new ArrayList<Object>();

		itens.add(new DataSourceRedeCredenciada(prestadores));
		
		String template = getClass().getResource("/templates/RedeCredenciada.xml").getPath();
		
		
		JHeatReport report = new JHeatReport(template, itens);
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		report.createPDF(byteOutput);
		
		return byteOutput.toByteArray();
	}

}
