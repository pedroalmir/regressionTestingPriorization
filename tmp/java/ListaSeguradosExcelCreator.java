package br.com.infowaypi.ecare.segurados;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author Marcos Roberto - 11.10.2011
 */
public class ListaSeguradosExcelCreator {

	private int linha = 0;
	private WritableWorkbook planilha;
	private WritableSheet sheet;
	
	public void createPlanilha(ResumoSegurados resumo)throws IOException, RowsExceededException, WriteException {	
		planilha = Workbook.createWorkbook(new File(resumo.getFileName()));
		
		sheet = planilha.createSheet("Segurados", 0);
		
		escreverCabecalho();
		escreverDetalhes(resumo);

		planilha.write();       
		planilha.close();
		
		File arquivo = new File(resumo.getPath());
		arquivo.getAbsoluteFile();
	}

	private void escreverCabecalho() throws RowsExceededException, WriteException {
		sheet.addCell(new Label(0, linha, "Número do Cartão"));
		sheet.addCell(new Label(1, linha, "Nome"));
		sheet.addCell(new Label(2, linha, "Tipo de Segurado"));
		sheet.addCell(new Label(3, linha, "Data de Adesao"));
		sheet.addCell(new Label(4, linha, "Idade"));
		
		linha++;
	}

	private void escreverDetalhes(ResumoSegurados resumo)throws IOException, RowsExceededException, WriteException  {
		for (Segurado segurado : resumo.getSegurados()) {
			sheet.addCell(new Label(0, linha, segurado.getNumeroDoCartao()));
			sheet.addCell(new Label(1, linha, segurado.getPessoaFisica().getNome()));
			sheet.addCell(new Label(2, linha, segurado.getTipoDeSegurado()));
			sheet.addCell(new Label(3, linha, Utils.format(segurado.getDataAdesao())));
			sheet.addCell(new Label(4, linha, String.valueOf(segurado.getIdade())));
			
			linha++;
		}
	}
    
    /**
     * 
     * @param arquivo
     * @return array de bytes do arquivo passado como parâmetro
     * @throws Exception
     */
    public byte[] getFile(File arquivo) throws Exception{
    	InputStream out = new FileInputStream(arquivo);
    	ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
    	
    	while (out.available() > 0) {
    		byteArray.write(out.read());
    	}
    	
    	return byteArray.toByteArray();
    }
}