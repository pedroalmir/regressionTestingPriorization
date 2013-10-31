package br.com.infowaypi.ecare.financeiro.boletos;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;

import javax.swing.text.NumberFormatter;

import br.com.infowaypi.jbank.bancos.Banco;
import br.com.infowaypi.jbank.boletos.BoletoBean;
import br.com.infowaypi.jbank.boletos.PDFGenerator;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.BarcodeInter25;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

public class PDFGeneratorBoletoIndividual extends PDFGenerator {
	
	public static final float MARGEM = 28;
	public static final float MARGEM_INTERNA = 39;
	public static final float IMAGEM_BOLETO_WIDTH 	= PageSize.A4.getWidth() - 56; // 595.00f;
	public static final float IMAGEM_BOLETO_HEIGHT 	= 271.76f; 
	public static final float ENVELOPE_BOLETO_HEIGHT = 800f;
	
	public PDFGeneratorBoletoIndividual(){
		this(null,0);
	}
	
	public PDFGeneratorBoletoIndividual(Banco banco, int modeloDeTopo) {
		
		baos = new ByteArrayOutputStream();
		
		formatter 	= new NumberFormatter(new DecimalFormat("#,##0.00"));
		document 	= new Document(PageSize.A4);

		try {
			PdfWriter writer = PdfWriter.getInstance(document, baos);

			document.open(); 

			pdfContentByte = writer.getDirectContent();

			// gera template com o fundo do boleto
			imgTitulo = Image.getInstance(getClass().getResource("/img/templateNovo.gif"));
			imgTitulo.scaleAbsolute(IMAGEM_BOLETO_WIDTH, IMAGEM_BOLETO_HEIGHT);
			imgTitulo.setAbsolutePosition(0, 0);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	
	@Override
	public void addBoleto(BoletoBean boleto, Banco banco, int modeloDeTopo,	float alturaTemplate) {
		
		try {

			tempImgBoleto = pdfContentByte.createTemplate(IMAGEM_BOLETO_WIDTH, IMAGEM_BOLETO_HEIGHT);
			tempImgBoleto.addImage(imgTitulo);
			
			// gera template com a imagem do logo do banco
			Image imgBanco = Image.getInstance(getClass().getResource("/img/" + banco.getNumero() + ".png"));
			imgBanco.scaleAbsolute(IMAGEM_BANCO_WIDTH , IMAGEM_BANCO_HEIGHT);
			imgBanco.setAbsolutePosition(0, 0);

			tempImgBanco = pdfContentByte.createTemplate(IMAGEM_BANCO_WIDTH, IMAGEM_BANCO_HEIGHT);
			tempImgBanco.addImage(imgBanco);

			float altura = alturaTemplate + 230;
			
			pdfContentByte.addTemplate(tempImgBoleto, MARGEM, alturaTemplate);
//			pdfContentByte.addTemplate(tempImgBanco, margem, altura);

			BaseFont bfTextoSimples = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
			BaseFont bfTextoCB = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.EMBEDDED);

			/**
			 * Neste momento este método vai "escrever" o topo do boleto,
			 * levando em consideração todos os posicionables.
			 */
			banco.writeTopo(this, boleto);
			
			//CAMPO DIGITÁVEL
			pdfContentByte.beginText();
			pdfContentByte.setFontAndSize(bfTextoCB, 12);
			pdfContentByte.showTextAligned(PdfContentByte.ALIGN_LEFT, banco.getNumero() + "-0", 127, altura, 0);
			int MARGEM_DIREITA = 525;
			pdfContentByte.showTextAligned(PdfContentByte.ALIGN_RIGHT, banco.getLinhaDigitavel(), MARGEM_DIREITA, altura, 0);
			pdfContentByte.endText();

			//CAMPOS DO BOLETO
			pdfContentByte.beginText();
			pdfContentByte.setFontAndSize(bfTextoSimples, 7);
			//L1
			pdfContentByte.showTextAligned(PdfContentByte.ALIGN_LEFT, boleto.getLocalPagamento(), MARGEM_INTERNA + 7, altura - 25, 0);
			pdfContentByte.showTextAligned(PdfContentByte.ALIGN_RIGHT, boleto.getDataVencimento(), MARGEM_DIREITA, altura - 25, 0);
			//L2
			pdfContentByte.showTextAligned(PdfContentByte.ALIGN_LEFT, boleto.getCedente(), MARGEM_INTERNA + 7, altura - 44, 0);
			pdfContentByte.showTextAligned(PdfContentByte.ALIGN_RIGHT, boleto.getAgencia() + " / " + boleto.getContaCorrente() + "-" + boleto.getDvContaCorrente(), MARGEM_DIREITA, altura - 44, 0);
			//L3
			pdfContentByte.showTextAligned(PdfContentByte.ALIGN_LEFT, boleto.getDataDocumento(), MARGEM_INTERNA + 7, altura - 61, 0);
			pdfContentByte.showTextAligned(PdfContentByte.ALIGN_LEFT, (!(boleto.getNoDocumento().length() == 0)) ? boleto.getNoDocumento() : banco.getNossoNumeroFormatado(), MARGEM + 74, altura - 61, 0);
			pdfContentByte.showTextAligned(PdfContentByte.ALIGN_LEFT, boleto.getEspecieDocumento(), MARGEM_INTERNA + 165, altura - 61, 0);
			pdfContentByte.showTextAligned(PdfContentByte.ALIGN_LEFT, boleto.getAceite(), MARGEM_INTERNA + 255, altura - 61, 0);
			pdfContentByte.showTextAligned(PdfContentByte.ALIGN_LEFT, boleto.getDataProcessamento(), MARGEM_INTERNA + 328, altura - 61, 0);
//			pdfContentByte.showTextAligned(PdfContentByte.ALIGN_RIGHT, banco.getNossoNumeroFormatado() + "-" + boleto.getDigitoVerificadorNossoNumero(), MARGEM_DIREITA, altura - 70, 0);
			pdfContentByte.showTextAligned(PdfContentByte.ALIGN_RIGHT, banco.getNossoNumeroFormatado(), MARGEM_DIREITA, altura - 61, 0);
			
			//L4
			pdfContentByte.showTextAligned(PdfContentByte.ALIGN_LEFT, boleto.getCarteira(), MARGEM_INTERNA + 115, altura - 80, 0);
			pdfContentByte.showTextAligned(PdfContentByte.ALIGN_LEFT, "R$", MARGEM_INTERNA + 195, altura - 80, 0);
			pdfContentByte.showTextAligned(PdfContentByte.ALIGN_RIGHT, "R$ " + formatter.valueToString(new Double(boleto.getValorBoleto())), MARGEM_DIREITA, altura - 80, 0);
			//INSTRUÇÕES
			pdfContentByte.showTextAligned(PdfContentByte.ALIGN_LEFT, boleto.getInstrucao1(), MARGEM_INTERNA +7, altura - 100, 0);
			pdfContentByte.showTextAligned(PdfContentByte.ALIGN_LEFT, boleto.getInstrucao2(), MARGEM_INTERNA +7, altura - 110, 0);
			pdfContentByte.showTextAligned(PdfContentByte.ALIGN_LEFT, boleto.getInstrucao3(), MARGEM_INTERNA +7, altura - 120, 0);
			pdfContentByte.showTextAligned(PdfContentByte.ALIGN_LEFT, boleto.getInstrucao4(), MARGEM_INTERNA +7, altura - 130, 0);
			//O TAMANHO DA FONTE PARA AS INSTRUÇÕES 5 E 6 MUDA
			pdfContentByte.setFontAndSize(bfTextoSimples, 7);
			pdfContentByte.showTextAligned(PdfContentByte.ALIGN_LEFT, boleto.getInstrucao5(), MARGEM_INTERNA +7, altura - 140, 0);
			pdfContentByte.showTextAligned(PdfContentByte.ALIGN_LEFT, boleto.getInstrucao6(), MARGEM_INTERNA +7, altura - 150, 0);
			
			// pdfContentByte.showTextAligned(PdfContentByte.ALIGN_LEFT, boleto.getCedente(), margem - 14, altura - 242, 0);
			pdfContentByte.setFontAndSize(bfTextoSimples, 7);
			pdfContentByte.showTextAligned(PdfContentByte.ALIGN_LEFT, boleto.getNomeSacado(), MARGEM_INTERNA + 35, altura - 188, 0);
			pdfContentByte.showTextAligned(PdfContentByte.ALIGN_LEFT, boleto.getCpfSacado(), MARGEM_INTERNA + 333, altura - 188, 0);			
			pdfContentByte.showTextAligned(PdfContentByte.ALIGN_LEFT, boleto.getEnderecoSacado(), MARGEM_INTERNA + 35, altura - 198, 0);
			pdfContentByte.showTextAligned(PdfContentByte.ALIGN_LEFT, boleto.getBairroSacado(), MARGEM_INTERNA + 35, altura - 208, 0);
			pdfContentByte.showTextAligned(PdfContentByte.ALIGN_LEFT, boleto.getCepSacado() + " " + boleto.getBairroSacado() + " - " + boleto.getCidadeSacado() + " " + boleto.getUfSacado(), MARGEM_INTERNA + 35, altura - 218, 0);
			pdfContentByte.endText();
			
			BarcodeInter25 code = new BarcodeInter25();
			code.setCode(boleto.getCodigoBarras());
			code.setExtended(true);

			code.setTextAlignment(Element.ALIGN_LEFT);
			code.setBarHeight(37.00f);
			code.setFont(null);
			code.setX(0.73f);
			code.setN(3);

			PdfTemplate imgCB = code.createTemplateWithBarcode(pdfContentByte, null, null);
			pdfContentByte.addTemplate(imgCB, MARGEM_INTERNA + 20, alturaTemplate -35);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	
		// gera template com a imagem do logo do banco
		try {
			BaseFont bfTextoCB = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
			getPdfContentByte().beginText();
			getPdfContentByte().setFontAndSize(bfTextoCB, 8);
			getPdfContentByte().showTextAligned(0, boleto.getNomeSacado(), 825, 420, 0);
			getPdfContentByte().showTextAligned(0, boleto.getEnderecoSacado(), 825, 412, 0);
			getPdfContentByte().showTextAligned(0, boleto.getCepSacado() + " " + boleto.getBairroSacado() + " - " + boleto.getCidadeSacado() + " " + boleto.getUfSacado(), 825, 404, 0);
			getPdfContentByte().endText();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		System.out.println("PageSize.A4.getWidth()" + PageSize.A4.getWidth());
	}
}
