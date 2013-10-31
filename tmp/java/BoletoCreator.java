package br.com.infowaypi.ecare.financeiro.boletos;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;

import br.com.infowaypi.ecare.financeiro.Cobranca;
import br.com.infowaypi.ecare.financeiro.conta.DetalheContaDependente;
import br.com.infowaypi.ecare.financeiro.conta.DetalheContaTitular;
import br.com.infowaypi.ecare.segurados.DependenteSR;
import br.com.infowaypi.ecare.segurados.DependenteSuplementar;
import br.com.infowaypi.ecare.segurados.TitularFinanceiroSR;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.conta.BoletoConfigurator;
import br.com.infowaypi.ecarebc.financeiro.conta.Conta;
import br.com.infowaypi.ecarebc.financeiro.conta.ContaInterface;
import br.com.infowaypi.jbank.bancos.Banco;
import br.com.infowaypi.jbank.boletos.BoletoBean;
import br.com.infowaypi.jbank.boletos.CampoBoleto;
import br.com.infowaypi.jbank.boletos.ImagemFundoBoleto;
import br.com.infowaypi.jbank.boletos.PDFGenerator;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;

public class BoletoCreator {

	private static final String IMG_TOPO_BOLETO = "/img/topo_modelo_2.gif";
	private static final int TAMANHO_MAX_DA_STRING = 25;
	private static Long nossoNumero = 0L;
	private static long INCREMENTO_NOSSO_NUMERO = 1000L;
	public static final int IMAGEM_BOLETO_WIDTH = new Float(PDFGeneratorSR.IMAGEM_BOLETO_WIDTH).intValue(); // 514.22f;
	public static final int IMAGEM_BOLETO_HEIGHT = 450; // 385.109f;
	private static BaseFont fonteConteudo;
	private static Float tamanhoDaFonte = 7f;
	private static BoletoConfigurator boletoConfigurator;
	private static Image imageFundoBoleto;
	
	public BoletoCreator() {
		Criteria criteria = HibernateUtil.currentSession().createCriteria(Conta.class);
		nossoNumero = (Long) criteria.setProjection(Projections.max("nossoNumeroLong")).uniqueResult();
		if (nossoNumero == null || nossoNumero == 0L) {
			nossoNumero = INCREMENTO_NOSSO_NUMERO;
		}
		
		boletoConfigurator = (BoletoConfigurator) HibernateUtil.currentSession()
																.createCriteria(BoletoConfigurator.class)
																.add(Expression.eq("ativo", true))
																.uniqueResult();
		
		BoletoConfigurator boletoConf =null;
		if(boletoConfigurator != null){
			boletoConf = boletoConfigurator;
		}else{
			boletoConf = new BoletoConfigurator();
		}
		
		Assert.isNotNull(boletoConf, "Caro usuário, antes de gerar boletos por favor atualize o cadastro de \"Configuração de Boletos\"");
		
		
		try {
			imageFundoBoleto = Image.getInstance(Image.class.getResource(IMG_TOPO_BOLETO));
			imageFundoBoleto.scaleAbsolute(IMAGEM_BOLETO_WIDTH, IMAGEM_BOLETO_HEIGHT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public BoletoBean create(Cobranca cobranca, boolean isReimpressao) throws MalformedURLException, IOException, DocumentException {
		return this.create(cobranca, cobranca.getValorCobrado(), isReimpressao);
	}
	
	public BoletoBean create(Cobranca cobranca,Cobranca cobrancaAnterior, boolean isReimpressao) throws MalformedURLException, IOException, DocumentException {
		if (cobrancaAnterior != null) {
			return this.create(cobranca, cobrancaAnterior.getValorCobrado(), isReimpressao);
		} else {
			return this.create(cobranca, cobranca.getValorCobrado(), isReimpressao);
		}
	}
	
	private BoletoBean create(Cobranca cobranca, BigDecimal valorBaseParaCalculoJurosMulta, boolean isReimpressao) throws MalformedURLException, IOException, DocumentException {
		
		fonteConteudo = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
		
		String numero = "";
		
		for (ContaInterface conta : cobranca.getColecaoContas().getContas()) {
			if (isReimpressao) {
				 numero = conta.getNossoNumero();
			}
			else {
				nossoNumero++;
				numero = String.valueOf(nossoNumero);
				conta.setNossoNumeroLong(nossoNumero);
			}
		}
		
		TitularFinanceiroSR titular;
		if (cobranca.getTitular().isSeguradoDependenteSuplementar()) {
			titular = ((DependenteSuplementar)cobranca.getTitular()).getTitular();
		}
		else {
			titular = cobranca.getTitular();
		}
		
		String valorBoleto = String.valueOf(cobranca.getValorCobrado());
		
		String vencimento = this.dataToString(cobranca.getDataVencimento());
		String complemento = titular.getPessoaFisica().getEndereco().getComplemento() != null ? " - " + titular.getPessoaFisica().getEndereco().getComplemento() : ""; 
		
		BoletoBean boletoBean = new BoletoBean();
        
        boletoBean.setCedente("AUT. MUNIC. DE PREVID. E ASSIST. A SAUDE DOS SERVIDORES          05.244.336.0001-13"); 
        
        boletoBean.setNomeSacado(PDFGenerator.breakString(retirarImpressaoNull(titular.getPessoaFisica().getNome()), 32));
        boletoBean.setEnderecoSacado(retirarImpressaoNull(titular.getPessoaFisica().getEndereco().getLogradouro()) + " - " + retirarImpressaoNull(titular.getPessoaFisica().getEndereco().getNumero()) + complemento);        
        boletoBean.setBairroSacado(retirarImpressaoNull(titular.getPessoaFisica().getEndereco().getBairro()));
        if (titular.getPessoaFisica().getEndereco().getMunicipio() != null) {
        	boletoBean.setCidadeSacado(retirarImpressaoNull(titular.getPessoaFisica().getEndereco().getMunicipio().getDescricao()));
        	if (titular.getPessoaFisica().getEndereco().getMunicipio().getEstado() != null) {
        		boletoBean.setUfSacado(retirarImpressaoNull(titular.getPessoaFisica().getEndereco().getMunicipio().getEstado().getUf()));
        	}
        	else {
        		boletoBean.setUfSacado("");
        	}
        }
        else {
        	boletoBean.setCidadeSacado("");
        	boletoBean.setUfSacado("");
        }
        
        boletoBean.setCepSacado(retirarImpressaoNull(titular.getPessoaFisica().getEndereco().getCep()));
        boletoBean.setCpfSacado(retirarImpressaoNull(titular.getPessoaFisica().getCpf()));
        boletoBean.setLocalPagamento("PREFERENCIALMENTE NAS CASAS LOTÉRICAS ATÉ O VALOR LIMITE");
                
        boletoBean.setInstrucao1("MULTA DE R$ " + calculaMulta(boletoConfigurator.getMulta(),valorBaseParaCalculoJurosMulta).toString().replace(".",",") + " APÓS " + vencimento);
        boletoBean.setInstrucao2("JUROS DE R$ " + calculaJuros(boletoConfigurator.getJuros(),valorBaseParaCalculoJurosMulta).toString().replace(".",",") + " AO DIA APÓS " + vencimento);
        
        String validade = this.dataToString(this.getDataValidade(cobranca));
        
        boletoBean.setInstrucao3("NÃO RECEBER APÓS " + validade);
        boletoBean.setInstrucao5(boletoConfigurator.getMensagemFichaDeCompensacao1());
        boletoBean.setInstrucao6(boletoConfigurator.getMensagemFichaDeCompensacao2());
        
        boletoBean.setMoraMulta(cobranca.getValorJurosMultaEncargosCobrado().toString().replace(".",","));
        boletoBean.setTotalComAcrescimosEMultas(cobranca.getValorCobradoComMultasEJuros().toString().replace(".",","));
                
        boletoBean.setAgencia("0050");
        boletoBean.setContaCorrente("029138");
        boletoBean.setDvContaCorrente("2");
        
        boletoBean.setCarteira("SR"); //pode ser 80 ou 81 ou 82 (Confirmar com gerente)
        boletoBean.setCodigoFornecidoAgencia("029138");
        boletoBean.setCodCliente("029138");
        
		boletoBean.setNumConvenio("");
        
        boletoBean.setNossoNumero(numero,15);
        
        boletoBean.setNoDocumento("");
        
        boletoBean.setValorBoleto(valorBoleto);
        boletoBean.setDataVencimento(vencimento);
		boletoBean.setDataDocumento(Utils.format(new Date(), "dd/MM/yyyy"));
        boletoBean.setDataProcessamento(Utils.format(new Date(), "dd/MM/yyyy"));
  
        writeDetalheValoresSegurados(cobranca, boletoBean);
        writeDetalheGuias(cobranca, boletoBean);
        writeDetalheSomatorioValoresSegurado(cobranca, boletoBean);
        writeDetalheSomatorioGuias(cobranca, boletoBean);
        writeDetalheCabecalhoBoleto(cobranca, boletoBean);
        writeDetalheInstrucoesSacado(cobranca, boletoBean);
        
        ImagemFundoBoleto imagemFundoBoleto = new ImagemFundoBoleto(0f,0f,IMAGEM_BOLETO_WIDTH,IMAGEM_BOLETO_HEIGHT,imageFundoBoleto);
        boletoBean.getPosicionables().add(0,imagemFundoBoleto);
        
		Banco banco = new CaixaEconomicaSIGCB(boletoBean); 
		
		boletoBean.setCodigoBarras(banco.getCodigoBarras());
		boletoBean.setLinhaDigitavel(banco.getLinhaDigitavel());
		
        return boletoBean;
	}
		
	private String dataToString(Date date) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		String vencimento = Utils.format(cal.getTime(), "dd/MM/yyyy");
		return vencimento;
	}
	
	/**
	 * Fornece a data até a qual o boleto pode ser pago com atraso.
	 * @param dataVencimento
	 * @return
	 */
	@SuppressWarnings("unused")
	private String calculaDataMaximaPagamento(Date dataVencimento) {
		Calendar dtVencimento = new GregorianCalendar();
		dtVencimento.setTime(dataVencimento);
		dtVencimento.add(Calendar.DAY_OF_MONTH, boletoConfigurator.getValidade());
		
		return Utils.format(dtVencimento.getTime());
	}
	
	/**
	 * @see br.com.infowaypi.ecare.financeiro.boletos.GeracaoIndividualDeBoletosService#calculaJuros
	 * @param percentual
	 * @param valor
	 * @return
	 */
	private BigDecimal calculaJuros(Float percentual, BigDecimal valor) {
		float numeroDeDiasNoMes = 30;
		
		BigDecimal jurosTotal = valor.multiply(new BigDecimal(percentual/100));
		jurosTotal = MoneyCalculation.divide(jurosTotal, numeroDeDiasNoMes);
		
		return MoneyCalculation.rounded(jurosTotal);
	}
	
	/**
	 * @see br.com.infowaypi.ecare.financeiro.boletos.GeracaoIndividualDeBoletosService#calculaMulta
	 * @param percentual
	 * @param valor
	 * @return
	 */
	private BigDecimal calculaMulta(Float percentual, BigDecimal valor) {
		BigDecimal multa = valor.multiply(new BigDecimal(percentual/100));
		return MoneyCalculation.rounded(multa);
	}

	private void writeDetalheInstrucoesSacado(Cobranca cobranca,BoletoBean boletoBean) {
		CampoBoleto campo = null;
		int coordenadaX = new Float(PDFGeneratorSR.MARGEM).intValue()+10;
		int coordenadaY = 406;
		int decremento = 8;
		campo = new CampoBoleto(coordenadaX,coordenadaY - decremento,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_LEFT,boletoConfigurator.getMensagemReciboDoSacado1());
		boletoBean.getPosicionables().add(campo);
		
		campo = new CampoBoleto(coordenadaX,coordenadaY - 2 * decremento,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_LEFT,boletoConfigurator.getMensagemReciboDoSacado2());
		boletoBean.getPosicionables().add(campo);
		
		campo = new CampoBoleto(coordenadaX,coordenadaY - 3 * decremento,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_LEFT,boletoConfigurator.getMensagemReciboDoSacado3());
		boletoBean.getPosicionables().add(campo);
		
		campo = new CampoBoleto(coordenadaX,coordenadaY - 4 * decremento,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_LEFT,boletoConfigurator.getMensagemReciboDoSacado4());
		boletoBean.getPosicionables().add(campo);
	}

	private void writeDetalheCabecalhoBoleto(Cobranca cobranca, BoletoBean boletoBean) {
		CampoBoleto campo = null;
		int coordenadaX = 175;
		int coordenadaY = 794;
		campo = new CampoBoleto(coordenadaX,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_LEFT,boletoBean.getNomeSacado());
		boletoBean.getPosicionables().add(campo);
		
		campo = new CampoBoleto(378,coordenadaY-1,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_LEFT,"AUTARQUIA MUNICIPAL DE PREVIDÊNCIA E ASS");
		boletoBean.getPosicionables().add(campo);
		
		campo = new CampoBoleto(coordenadaX - 15,coordenadaY - 9 ,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_LEFT,boletoBean.getCpfSacado());
		boletoBean.getPosicionables().add(campo);
		
		campo = new CampoBoleto(410,coordenadaY - 12 ,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_LEFT,"0050/029138-2");
		boletoBean.getPosicionables().add(campo);
		
		campo = new CampoBoleto(coordenadaX + 15,coordenadaY - 19,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_LEFT,boletoBean.getDataVencimento());
		boletoBean.getPosicionables().add(campo);
		
		campo = new CampoBoleto(400 + 5,coordenadaY - 21,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_LEFT,new CaixaEconomicaSIGCB(boletoBean).getNossoNumeroFormatado());
		boletoBean.getPosicionables().add(campo);
		
		campo = new CampoBoleto(coordenadaX + 47,coordenadaY - 29,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_LEFT,boletoBean.getValorBoleto());
		boletoBean.getPosicionables().add(campo);
		
		Calendar calendar  = new GregorianCalendar();
		calendar.setTime(cobranca.getDataVencimento());
		
		//so imprime valor de juros e multas se esses campos forem direfentes de zero
		if (MoneyCalculation.compare(cobranca.getValorJurosMultaEncargosCobrado(), BigDecimal.ZERO) != 0){
			campo = new CampoBoleto(coordenadaX + 318,coordenadaY - 635, fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_LEFT, "R$ " + boletoBean.getMoraMulta().toString());
			boletoBean.getPosicionables().add(campo);
			
			campo = new CampoBoleto(coordenadaX + 318,coordenadaY - 675, fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_LEFT, "R$ " + boletoBean.getTotalComAcrescimosEMultas().toString());
			boletoBean.getPosicionables().add(campo);
		}
	}

	/**
	 * Detalhes do Extrato
	 * @param cobranca
	 * @param boletoBean
	 * @throws IOException 
	 * @throws DocumentException 
	 */
	private static void writeDetalheValoresSegurados(Cobranca cobranca, BoletoBean boletoBean) throws DocumentException, IOException {
		Vector<String[]> detalhes = new Vector<String[]>();
        
        DetalheContaTitular detalheContaTitular = cobranca.getTitular().getDetalheContaTitular(cobranca.getCompetencia());
		String[] detalhe = new String[10];
		detalhe[0] = PDFGenerator.breakString(detalheContaTitular.getTitular().getPessoaFisica().getNome(), TAMANHO_MAX_DA_STRING);
		detalhe[1] = detalheContaTitular.getTitular().isSeguradoDependenteSuplementar() ? "Dep. Suplementar" : detalheContaTitular.getTitular().getTipoDeSegurado();
		detalhe[2] = detalheContaTitular.getTitular().getFaixa().getIdadeMinima() + " - " + detalheContaTitular.getTitular().getFaixa().getIdadeMaxima();
		detalhe[3] = Utils.format(detalheContaTitular.getValorFinanciamento());
		detalhe[4] = Utils.format(detalheContaTitular.getValorCoparticipacao());
		detalhe[5] = Utils.format(detalheContaTitular.getValorSegundaViaCartao());
		detalhe[6] = Utils.format(detalheContaTitular.getValorTotal());
		detalhe[7] = "";
		detalhe[8] = "";
		detalhe[9] = "";
			
		detalhes.add(detalhe);
		
		if(cobranca.getTitular().isSeguradoTitular()) {
			TitularFinanceiroSR titular = cobranca.getTitular();
	        for (DependenteSR dependente: titular.getDependentesCobranca(cobranca.getCompetencia())) {
	        	DetalheContaDependente detalheContaDependente = dependente.getDetalheContaDependente(cobranca.getCompetencia());
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
			boletoBean.getPosicionables().add(campo);
			
			campo = new CampoBoleto(posXTipo,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_LEFT,strings[1]);
			boletoBean.getPosicionables().add(campo);
			
			campo = new CampoBoleto(posXFaixaEtaria,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_LEFT,strings[2]);
			boletoBean.getPosicionables().add(campo);
			
			campo = new CampoBoleto(posXFinanciamento,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_RIGHT,strings[3]);
			boletoBean.getPosicionables().add(campo);
			
			campo = new CampoBoleto(posXCoparticipacao,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_RIGHT,strings[4]);
			boletoBean.getPosicionables().add(campo);
			
			campo = new CampoBoleto(posXCartao,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_RIGHT,strings[5]);
			boletoBean.getPosicionables().add(campo);
			
			campo = new CampoBoleto(posXSubtotal,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_RIGHT,strings[6]);
			boletoBean.getPosicionables().add(campo);
			coordenadaY -= 8;
		}
	}
	
	/**
	 * Detalhes das Coparticipações
	 * @param cobranca
	 * @param boletoBean
	 */
	@SuppressWarnings("unchecked")
	private static void writeDetalheGuias(Cobranca cobranca, BoletoBean boletoBean) {
        Vector<String[]> detalhes = new Vector<String[]>();
        int count = 0;
        
        List<GuiaSimples> guias = new ArrayList<GuiaSimples>(cobranca.getGuias());
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
			boletoBean.getPosicionables().add(campo);
			
			campo = new CampoBoleto(100,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_LEFT,strings[1]);
			boletoBean.getPosicionables().add(campo);
			
			campo = new CampoBoleto(253,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_LEFT,strings[2]);
			boletoBean.getPosicionables().add(campo);
			
			campo = new CampoBoleto(370,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_LEFT,strings[3]);
			boletoBean.getPosicionables().add(campo);
			
			campo = new CampoBoleto(495,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_RIGHT,strings[4]);
			boletoBean.getPosicionables().add(campo);
			
			campo = new CampoBoleto(542,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_RIGHT,strings[5]);
			boletoBean.getPosicionables().add(campo);
			
			coordenadaY -= 8;
		}
	}

	/**
	 * Detalhes do somatório dos valores do Extrato
	 * @param cobranca
	 * @param boletoBean
	 */
	private static void writeDetalheSomatorioValoresSegurado(Cobranca cobranca, BoletoBean boletoBean) {
		
		BigDecimal valorTotalFinanciamento    = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal valorTotalCoparticipacao   = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal valorTotalSegundaViaCartao = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal valorTotalAll              = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		DetalheContaTitular detalheContaTitular = cobranca.getTitular().getDetalheContaTitular(cobranca.getCompetencia());
		valorTotalFinanciamento    = valorTotalFinanciamento.add(detalheContaTitular.getValorFinanciamento());
		valorTotalCoparticipacao   = valorTotalCoparticipacao.add(detalheContaTitular.getValorCoparticipacao());
		valorTotalSegundaViaCartao = valorTotalSegundaViaCartao.add(detalheContaTitular.getValorSegundaViaCartao());
		valorTotalAll              = valorTotalAll.add(detalheContaTitular.getValorTotal());
		
		for (DependenteSR dependente : cobranca.getTitular().getDependentes(SituacaoEnum.ATIVO.descricao())) {
			DetalheContaDependente detalheContaDependente = dependente.getDetalheContaDependente(cobranca.getCompetencia());
			valorTotalFinanciamento    = valorTotalFinanciamento.add(detalheContaDependente.getValorFinanciamento());
			valorTotalCoparticipacao   = valorTotalCoparticipacao.add(detalheContaDependente.getValorCoparticipacao());
			valorTotalSegundaViaCartao = valorTotalSegundaViaCartao.add(detalheContaDependente.getValorSegundaViaCartao());
			valorTotalAll              = valorTotalAll.add(detalheContaDependente.getValorTotal());
		}
		
		int coordenadaY = 658;
		
		CampoBoleto campo = new CampoBoleto(410,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_RIGHT,Utils.format(valorTotalFinanciamento));
		boletoBean.getPosicionables().add(campo);
		
		campo = new CampoBoleto(458,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_RIGHT,Utils.format(valorTotalCoparticipacao));
		boletoBean.getPosicionables().add(campo);
		
		campo = new CampoBoleto(500,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_RIGHT,Utils.format(valorTotalSegundaViaCartao));
		boletoBean.getPosicionables().add(campo);
		
		campo = new CampoBoleto(542,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_RIGHT,Utils.format(valorTotalAll));
		boletoBean.getPosicionables().add(campo);
		
		Assert.isEquals(valorTotalAll, cobranca.getValorCobrado(), "Caro usuário, o sistema " +
				"se comportou de forma inesperada. Entre em contato com a equipe de suporte" +
				" passando as seguintes informações: "+cobranca.getValorCobrado()
				+";"+valorTotalAll
				+";"+cobranca.getTitular().getNumeroDoCartao());
		
	}
	
	/**
	 * Detalhes do somatório dos valores das Coparticipações
	 * @param cobranca
	 * @param boletoBean
	 */
	@SuppressWarnings("unchecked")
	private static void writeDetalheSomatorioGuias(Cobranca cobranca, BoletoBean boletoBean) {
		
		int quantidadeTotal = 0;
		BigDecimal valorTotalCoparticipacao = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);

		for (GuiaSimples guia : cobranca.getGuias()) {
			quantidadeTotal += guia.getQuantidadeProcedimentos();
			valorTotalCoparticipacao = valorTotalCoparticipacao.add(guia.getValorCoParticipacao());
		}

		int coordenadaY = 420;
		CampoBoleto campo = new CampoBoleto(505,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_RIGHT,String.valueOf(quantidadeTotal));
		boletoBean.getPosicionables().add(campo);
		
		campo = new CampoBoleto(542,coordenadaY,fonteConteudo,tamanhoDaFonte,PdfContentByte.ALIGN_RIGHT,Utils.format(valorTotalCoparticipacao));
		boletoBean.getPosicionables().add(campo);
		
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
	
	private Date getDataValidade(Cobranca cobranca){
		Date dataValidade = cobranca.getDataValidade();
		
        if (dataValidade == null){
        	Date dataVencimento = cobranca.getDataVencimento();
        	Calendar dtVencimento = Calendar.getInstance();
		
        	dtVencimento.setTime(dataVencimento);
        	dataValidade = Utils.incrementaDias(dtVencimento, boletoConfigurator.getValidade());
        }
		
		return dataValidade;
	}
		
}

@SuppressWarnings("unchecked")
class ComparatorGuia implements Comparator<GuiaSimples>{

	@Override
	public int compare(GuiaSimples g1, GuiaSimples g2) {
		return g1.getDataAtendimento().compareTo(g2.getDataAtendimento());
	}
	
}
