package br.com.infowaypi.ecare.relatorio.portalBeneficiario.extratoCoparticipacao;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import br.com.infowaypi.ecare.financeiro.boletos.PDFGeneratorSR;
import br.com.infowaypi.ecare.financeiro.conta.DetalheContaDependente;
import br.com.infowaypi.ecare.financeiro.conta.DetalheContaTitular;
import br.com.infowaypi.ecare.segurados.DependenteSR;
import br.com.infowaypi.ecare.segurados.TitularFinanceiroSR;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.jbank.boletos.CampoBoleto;
import br.com.infowaypi.jbank.boletos.ImagemFundoBoleto;
import br.com.infowaypi.jbank.boletos.PDFGenerator;
import br.com.infowaypi.msr.utils.Utils;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;

public class ExtratoCoparticipacaoCreator {

	private static final int TAMANHO_MAX_DA_STRING = 25;
	public static final int IMAGEM_BOLETO_WIDTH = new Float(PDFGeneratorSR.IMAGEM_BOLETO_WIDTH).intValue(); // 514.22f;
	public static final int IMAGEM_BOLETO_HEIGHT = 450; // 385.109f;
	private static BaseFont fonteConteudo;
	private static Float tamanhoDaFonte = 7f;
	private static Image imageFundoBoleto;
	
	public ExtratoCoparticipacaoCreator() {
		try {
			imageFundoBoleto = Image.getInstance(getClass().getResource("/br/com/infowaypi/ecare/portal/topo_relatorio_coparticipacoes_2.gif").getPath());
			imageFundoBoleto.scaleAbsolute(IMAGEM_BOLETO_WIDTH, IMAGEM_BOLETO_HEIGHT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void create(ResumoDemonstrativoCoparticipacao resumo) throws MalformedURLException, IOException, DocumentException {
		
		fonteConteudo = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
		
        writeDetalheValoresSegurados(resumo);
        writeDetalheGuias(resumo);
        writeDetalheSomatorioValoresSegurado(resumo);
        writeDetalheSomatorioGuias(resumo);
        writeDetalheCabecalhoBoleto(resumo);
        
        ImagemFundoBoleto imagemFundoBoleto = new ImagemFundoBoleto(0f,0f,IMAGEM_BOLETO_WIDTH,IMAGEM_BOLETO_HEIGHT,imageFundoBoleto);
        resumo.getPosicionables().add(0,imagemFundoBoleto);
	}
		
	private void writeDetalheCabecalhoBoleto(ResumoDemonstrativoCoparticipacao resumo) {
		CampoBoleto campo = null;
		int coordenadaX = 175;
		int coordenadaY = 794;
		campo = new CampoBoleto(coordenadaX,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_LEFT, resumo.getTitular().getPessoaFisica().getNome());
		resumo.getPosicionables().add(campo);
		
		campo = new CampoBoleto(coordenadaX - 15,coordenadaY - 9 ,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_LEFT, resumo.getTitular().getPessoaFisica().getCpf());
		resumo.getPosicionables().add(campo);
		
	}

	/**
	 * Detalhes do Extrato
	 * @param cobranca
	 * @param boletoBean
	 * @throws IOException 
	 * @throws DocumentException 
	 */
	private static void writeDetalheValoresSegurados(ResumoDemonstrativoCoparticipacao resumo) throws DocumentException, IOException {
		Vector<String[]> detalhes = new Vector<String[]>();
        
        DetalheContaTitular detalheContaTitular = resumo.getTitular().getDetalheContaTitular(resumo.getCompetencia());
		String[] detalhe = new String[10];
		detalhe[0] = PDFGenerator.breakString(detalheContaTitular.getTitular().getPessoaFisica().getNome(), TAMANHO_MAX_DA_STRING);
		detalhe[1] = detalheContaTitular.getTitular().isSeguradoDependenteSuplementar() ? "Dep. Suplementar" : detalheContaTitular.getTitular().getTipoDeSegurado();
		detalhe[2] = detalheContaTitular.getTitular().getFaixa().getIdadeMinima() + " - " + detalheContaTitular.getTitular().getFaixa().getIdadeMaxima();
		detalhe[3] = Utils.format(resumo.getValorFinanciamento());
		detalhe[4] = Utils.format(resumo.getValorCoparticipacao());
		detalhe[5] = Utils.format(resumo.getValorSegundaViaCartao());
		detalhe[6] = Utils.format(resumo.getValorTotal());
		detalhe[7] = "";
		detalhe[8] = "";
		detalhe[9] = "";
			
		detalhes.add(detalhe);
		
		if(resumo.getTitular().isSeguradoTitular()) {
			TitularFinanceiroSR titular = resumo.getTitular();
	        for (DependenteSR dependente: titular.getDependentesCobranca(resumo.getCompetencia())) {
	        	DetalheContaDependente detalheContaDependente = dependente.getDetalheContaDependente(resumo.getCompetencia());
	        	if(detalheContaDependente.getValorTotal().compareTo(BigDecimal.ZERO) > 0) {
	        		String[] detalheDependente = new String[10];
	        		detalheDependente[0] = PDFGenerator.breakString(detalheContaDependente.getDependente().getPessoaFisica().getNome(), TAMANHO_MAX_DA_STRING - 6);
	        		detalheDependente[1] = detalheContaDependente.getDependente().isSeguradoDependenteSuplementar() ? "Dep. Supl." : detalheContaDependente.getDependente().getTipoDeSegurado();
	        		detalheDependente[2] = detalheContaDependente.getDependente().getFaixa().getIdadeMinima() + " - " + detalheContaDependente.getDependente().getFaixa().getIdadeMaxima();
	        		detalheDependente[3] = Utils.format(detalheContaDependente.getValorFinanciamento());
	        		detalheDependente[4] = Utils.format(detalheContaDependente.getValorCoparticipacao());
	        		detalheDependente[5] = Utils.format(detalheContaDependente.getValorSegundaViaCartao());
	        		detalheDependente[6] = Utils.format(detalheContaDependente.getValorTotal());
	        		detalheDependente[7] = "";
	        		detalheDependente[8] = "";
	        		detalheDependente[9] = "";
	        		detalhes.add(detalheDependente);
	        	}
			}
	        
		}
        
		CampoBoleto campo = null;
		
		int coordenadaY = 725;
		
		int posXBeneficiario = new Float(PDFGeneratorSR.MARGEM).intValue()+10;
		int posXTipo = 233;
		int posXFaixaEtaria = 323;
		int posXFinanciamento = 410;
		int posXCoparticipacao = 458;
		int posXCartao = 500;
		int posXSubtotal = 542;
		
		for (String[] strings : detalhes) {
			
			campo = new CampoBoleto(posXBeneficiario,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_LEFT,strings[0]);
			resumo.getPosicionables().add(campo);
			
			campo = new CampoBoleto(posXTipo,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_LEFT,strings[1]);
			resumo.getPosicionables().add(campo);
			
			campo = new CampoBoleto(posXFaixaEtaria,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_LEFT,strings[2]);
			resumo.getPosicionables().add(campo);
			
			campo = new CampoBoleto(posXFinanciamento,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_RIGHT,strings[3]);
			resumo.getPosicionables().add(campo);
			
			campo = new CampoBoleto(posXCoparticipacao,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_RIGHT,strings[4]);
			resumo.getPosicionables().add(campo);
			
			campo = new CampoBoleto(posXCartao,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_RIGHT,strings[5]);
			resumo.getPosicionables().add(campo);
			
			campo = new CampoBoleto(posXSubtotal,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_RIGHT,strings[6]);
			resumo.getPosicionables().add(campo);
			coordenadaY -= 8;
		}
	}
	
	/**
	 * Detalhes das Coparticipações
	 * @param cobranca
	 * @param boletoBean
	 */
	@SuppressWarnings("unchecked")
	private static void writeDetalheGuias(ResumoDemonstrativoCoparticipacao resumo) {
        Vector<String[]> detalhes = new Vector<String[]>();
        int count = 0;
        
        List<GuiaSimples> guias = new ArrayList<GuiaSimples>(resumo.getGuias());
        Collections.sort(guias, new ComparatorGuia());
        
        for (GuiaSimples guia : guias) {
	        	String[] detalhe = new String[6];
	        	detalhe[0] = Utils.format(guia.getDataAtendimento());
	        	detalhe[1] = PDFGenerator.breakString(guia.getSegurado().getPessoaFisica().getNome(), TAMANHO_MAX_DA_STRING + 5);
	        	detalhe[2] = PDFGenerator.breakString(guia.getPrestador().getPessoaJuridica().getFantasia(), TAMANHO_MAX_DA_STRING);
	        	detalhe[3] = guia.getAutorizacao() + " / " + guia.getTipo().replace("Tratamento", "Trat.").replace("Consulta Odontológica", "Consulta Odonto.");
	        	detalhe[4] = String.valueOf(guia.getQuantidadeProcedimentos());
	        	detalhe[5] = guia.getValorCoParticipacaoFormatado();
	            detalhes.add(detalhe);
	
	            if (count++ == 36) break;
		}
        
        CampoBoleto campo = null;
		int coordenadaY = 620;
		for (String[] strings : detalhes) {
			
			campo = new CampoBoleto(new Float(PDFGeneratorSR.MARGEM).intValue()+10,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_LEFT,strings[0]);
			resumo.getPosicionables().add(campo);
			
			campo = new CampoBoleto(100,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_LEFT,strings[1]);
			resumo.getPosicionables().add(campo);
			
			campo = new CampoBoleto(253,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_LEFT,strings[2]);
			resumo.getPosicionables().add(campo);
			
			campo = new CampoBoleto(370,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_LEFT,strings[3]);
			resumo.getPosicionables().add(campo);
			
			campo = new CampoBoleto(495,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_RIGHT,strings[4]);
			resumo.getPosicionables().add(campo);
			
			campo = new CampoBoleto(542,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_RIGHT,strings[5]);
			resumo.getPosicionables().add(campo);
			
			coordenadaY -= 8;
		}
	}

	/**
	 * Detalhes do somatório dos valores do Extrato
	 * @param cobranca
	 * @param boletoBean
	 */
	private static void writeDetalheSomatorioValoresSegurado(ResumoDemonstrativoCoparticipacao resumo) {
		
		BigDecimal valorTotalFinanciamento    = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal valorTotalCoparticipacao   = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal valorTotalSegundaViaCartao = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal valorTotalAll              = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		
//		DetalheContaTitular detalheContaTitular = resumo.getTitular().getDetalheContaTitular(resumo.getCompetencia());
		valorTotalFinanciamento    = valorTotalFinanciamento.add(resumo.getValorFinanciamento());
		valorTotalCoparticipacao   = valorTotalCoparticipacao.add(resumo.getValorCoparticipacao());
		valorTotalSegundaViaCartao = valorTotalSegundaViaCartao.add(resumo.getValorSegundaViaCartao());
		valorTotalAll              = valorTotalAll.add(resumo.getValorTotal());
		
		for (DependenteSR dependente : resumo.getTitular().getDependentes(SituacaoEnum.ATIVO.descricao())) {
			DetalheContaDependente detalheContaDependente = dependente.getDetalheContaDependente(resumo.getCompetencia());
			valorTotalFinanciamento    = valorTotalFinanciamento.add(detalheContaDependente.getValorFinanciamento());
			valorTotalCoparticipacao   = valorTotalCoparticipacao.add(detalheContaDependente.getValorCoparticipacao());
			valorTotalSegundaViaCartao = valorTotalSegundaViaCartao.add(detalheContaDependente.getValorSegundaViaCartao());
			valorTotalAll              = valorTotalAll.add(detalheContaDependente.getValorTotal());
		}
		
		int coordenadaY = 658;
		
		CampoBoleto campo = new CampoBoleto(410,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_RIGHT,Utils.format(valorTotalFinanciamento));
		resumo.getPosicionables().add(campo);
		
		campo = new CampoBoleto(458,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_RIGHT,Utils.format(valorTotalCoparticipacao));
		resumo.getPosicionables().add(campo);
		
		campo = new CampoBoleto(500,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_RIGHT,Utils.format(valorTotalSegundaViaCartao));
		resumo.getPosicionables().add(campo);
		
		campo = new CampoBoleto(542,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_RIGHT,Utils.format(valorTotalAll));
		resumo.getPosicionables().add(campo);
		
	}
	
	/**
	 * Detalhes do somatório dos valores das Coparticipações
	 * @param cobranca
	 * @param boletoBean
	 */
	@SuppressWarnings("unchecked")
	private static void writeDetalheSomatorioGuias(ResumoDemonstrativoCoparticipacao resumo) {
		
		int quantidadeTotal = 0;
		BigDecimal valorTotalCoparticipacao = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);

		for (GuiaSimples guia : resumo.getGuias()) {
			quantidadeTotal += guia.getQuantidadeProcedimentos();
			valorTotalCoparticipacao = valorTotalCoparticipacao.add(guia.getValorCoParticipacao());
		}

		int coordenadaY = 420;
		CampoBoleto campo = new CampoBoleto(505,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_RIGHT,String.valueOf(quantidadeTotal));
		resumo.getPosicionables().add(campo);
		
		campo = new CampoBoleto(542,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_RIGHT,Utils.format(valorTotalCoparticipacao));
		resumo.getPosicionables().add(campo);
		
	}

	public static String formatMoney(BigDecimal valor){
		valor = valor.setScale(2,BigDecimal.ROUND_HALF_UP);
		if(valor.compareTo(BigDecimal.ZERO) == 0)
			return "R$ 0,00";
		return "R$ " + valor;
	}
	
	public static String retirarImpressaoNull(String valor){
		return valor == null ? "" : valor;
	}
		
}

@SuppressWarnings("unchecked")
class ComparatorGuia implements Comparator<GuiaSimples>{

	@Override
	public int compare(GuiaSimples g1, GuiaSimples g2) {
		return g1.getDataAtendimento().compareTo(g2.getDataAtendimento());
	}
	
}