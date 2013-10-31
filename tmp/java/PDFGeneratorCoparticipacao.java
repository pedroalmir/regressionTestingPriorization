package br.com.infowaypi.ecare.relatorio.portalBeneficiario.extratoCoparticipacao;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import br.com.infowaypi.jbank.boletos.PDFGenerator;
import br.com.infowaypi.jbank.boletos.Posicionable;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;

public class PDFGeneratorCoparticipacao extends PDFGenerator{
	public static final float MARGEM = 28;
	public static final float MARGEM_INTERNA = 39;
	public static final float IMAGEM_BOLETO_WIDTH 	= PageSize.A4.getWidth() - 56; // 595.00f;
	public static final float IMAGEM_BOLETO_HEIGHT 	= 271.76f; 
	public static final float ENVELOPE_BOLETO_HEIGHT = 800f;
	
	public PDFGeneratorCoparticipacao() {
		
		baos = new ByteArrayOutputStream();
		document 	= new Document(PageSize.A4);

		try {
			PdfWriter writer = PdfWriter.getInstance(document, baos);
			document.open(); 
			pdfContentByte = writer.getDirectContent();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	public void generate(ResumoDemonstrativoCoparticipacao resumo) {
		try {
			write(resumo);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void write(ResumoDemonstrativoCoparticipacao resumo) throws Exception {
		pdfContentByte.beginText();
		for (Posicionable posicionable : resumo.getPosicionables()) {
			posicionable.write(this);
		}
		pdfContentByte.endText();
	}
	
	/**
     * Metodo que cria o arquivo local no disco
     */    
    public void writeToFile(String path){
    	document.close();
        try{        
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(baos.toByteArray());
            fos.flush();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        } 
    }
    
    /**
     * 
     * @param arquivo
     * @return array de bytes do arquivo passado como parâmetro
     * @throws Exception
     */
    public byte[] getBoletoFile(File arquivo) throws Exception{
    	InputStream out = new FileInputStream(arquivo);
    	ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
    	
    	while (out.available() > 0) {
    		byteArray.write(out.read());
    	}
    	
    	return byteArray.toByteArray();
    }

}
