package br.com.infowaypi.ecare.relatorio.portalBeneficiario.demostrativoIR;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import br.com.infowaypi.msr.utils.Utils;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

public class PDFGeneratorDemonstrativoIR {

	private ByteArrayOutputStream baos;
	private Document document;
	private PdfContentByte pdfContentByte;
	BaseFont baseFont;
	private Font font;
	
	public PDFGeneratorDemonstrativoIR() throws DocumentException, IOException {
		
		baos = new ByteArrayOutputStream();
		document = new Document(PageSize.A4);
		document.setMargins(60f, 60f, 0f, 0f);
		PdfWriter writer = PdfWriter.getInstance(document, baos);
		document.open();
		pdfContentByte = writer.getDirectContent();
		
		baseFont = BaseFont.createFont(getClass().getResource("/fonts/Arial.ttf").getPath(), BaseFont.WINANSI, BaseFont.EMBEDDED);
		font = new Font(baseFont, 12);
	}
	
	public void generate(ResumoDemonstrativoPagamento resumo) throws DocumentException, MalformedURLException, IOException {
		colocarPanoDeFundo();
		escreverDeclaracao(resumo);
		escreverAnoEValor(resumo);
		escreverDataPorExtenco(resumo);
	}
	
	private void colocarPanoDeFundo() throws MalformedURLException, IOException, DocumentException {
		Image imagemFundo = Image.getInstance(getClass().getResource("/br/com/infowaypi/ecare/portal/template_relatorio_ir.png").getPath());
		imagemFundo.scaleAbsolute(PageSize.A4.getWidth(), PageSize.A4.getHeight());
		imagemFundo.setAbsolutePosition(0, 0);
		document.add(imagemFundo);
	}

	private void escreverDeclaracao(ResumoDemonstrativoPagamento resumo) throws DocumentException, IOException{
		Paragraph paragraph = new Paragraph(resumo.getTextoDeclacacao(), font);
		paragraph.setSpacingBefore(250f);
		paragraph.setAlignment(Element.ALIGN_JUSTIFIED_ALL);
		document.add(paragraph);
	}

	private void escreverAnoEValor(ResumoDemonstrativoPagamento resumo){
		pdfContentByte.beginText();
		pdfContentByte.setFontAndSize(this.baseFont, 12f);
		pdfContentByte.showTextAligned(PdfContentByte.ALIGN_LEFT, resumo.getAno().toString(), 162f, 350f, 0f);
		pdfContentByte.showTextAligned(PdfContentByte.ALIGN_LEFT, Utils.format(resumo.getValorPago()), 350f, 350f, 0f);
		pdfContentByte.showTextAligned(PdfContentByte.ALIGN_LEFT, Utils.format(resumo.getValorPago()), 350f, 325f, 0f);
		pdfContentByte.endText();
	}
	
	private void escreverDataPorExtenco(ResumoDemonstrativoPagamento resumo) {
		pdfContentByte.beginText();
		pdfContentByte.setFontAndSize(this.baseFont, 12f);
		pdfContentByte.showTextAligned(PdfContentByte.ALIGN_LEFT, getDataPorExtenso(), 220f, 70f, 0f);
		pdfContentByte.endText();
	}
	
	private String getDataPorExtenso(){
		DateFormat dfmt = new SimpleDateFormat("'Recife,' dd 'de' MMMM 'de' yyyy");  
        Date hoje = Calendar.getInstance(Locale.getDefault()).getTime();  
        return dfmt.format(hoje); 
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
