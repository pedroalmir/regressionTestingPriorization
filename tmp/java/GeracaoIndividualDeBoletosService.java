package br.com.infowaypi.ecare.financeiro.boletos;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.FetchMode;
import org.hibernate.FlushMode;
import org.hibernate.NonUniqueResultException;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecare.constantes.Constantes;
import br.com.infowaypi.ecare.financeiro.Cobranca;
import br.com.infowaypi.ecare.financeiro.ResumoCobrancaIndividual;
import br.com.infowaypi.ecare.financeiro.conta.ContaMatricula;
import br.com.infowaypi.ecare.segurados.AbstractMatricula;
import br.com.infowaypi.ecare.segurados.Cartao;
import br.com.infowaypi.ecare.segurados.DependenteSuplementar;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.segurados.TitularFinanceiroSR;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.consignacao.Consignacao;
import br.com.infowaypi.ecarebc.financeiro.conta.BoletoConfigurator;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.ecarebc.utils.CompetenciaUtils;
import br.com.infowaypi.jbank.boletos.BoletoBean;
import br.com.infowaypi.jbank.boletos.BoletoFactory;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.CollectionUtils;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

import com.lowagie.text.DocumentException;

@SuppressWarnings("unchecked")
public class GeracaoIndividualDeBoletosService extends GeracaoBoletosService{
	
	BoletoConfigurator boletoConfigurator = super.getBoletoConfigurator();
		
	/*
	 * nao preciso de mapa de guias
	 * nao preciso de mapa de cobrancas
	 * nao preciso de mapa de cartoes
	 */
	public Cobranca gerarCobranca(String cpf, String numeroDoCartao,Date competencia, Date dataVencimento, UsuarioInterface usuario) throws Exception {		
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);

		ResumoCobrancaIndividual resumo = null;
		
		ultimoDiaDaCompetencia.setTime(competencia);
		ultimoDiaDaCompetencia.set(Calendar.DAY_OF_MONTH, ultimoDiaDaCompetencia.getActualMaximum(Calendar.DATE));
		
		Assert.isNotNull(boletoConfigurator, "Caro usuário, antes de gerar boletos por favor atualize o cadastro de \"Configuração de Boletos\"");
		
		TitularFinanceiroSR segurado = getUniqueSegurado(cpf, numeroDoCartao);
		
		Calendar vencimento = new GregorianCalendar();
		vencimento.setTime(dataVencimento);
		
		validaDataDeVencimento(dataVencimento);
		
		/**
		 * Foi retirada a crítica pelo requisito do redmine: 2539
		 * @author Luciano Infoway
		 * @since 26/02/2013
		 */
		/*Date ultimaCompetencia = getUltimaCompetenciaComBoleto(segurado);
		 
		if(ultimaCompetencia != null && Utils.compareData(competencia, ultimaCompetencia) < 0){
			throw new ValidateException("Não é possível gerar boleto para uma competência anterior à última processada para este segurado.");
		}*/
		
		Calendar hoje = new GregorianCalendar();
		hoje.setTime(new Date());
		
		if (segurado.isSeguradoTitular() || segurado.isSeguradoPensionista()){
			titulares.add(segurado);
		}else {
			dependentesSuplementar.add((DependenteSuplementar) segurado);
		}
		
		Set<TitularFinanceiroSR> segurados = new HashSet<TitularFinanceiroSR>(titulares);
		segurados.addAll(dependentesSuplementar);
		 
		Map<AbstractSegurado, Set<GuiaSimples>> mapGuiasParaCobranca = getMapGuiasParaCobranca(competencia, segurados);
		
		Map<Segurado, Set<Cartao>> mapCartoes = new HashMap<Segurado, Set<Cartao>>();//CollectionUtils.groupBy(GeracaoBoletosDAO.getCartoesParaCobranca(), "segurado", Segurado.class);
		
		Cobranca cobranca = null;
		
		BoletoFactory boletoFactoryRemessa = new BoletoFactory();
		boletoFactoryRemessa.setGenerator(new PDFGeneratorSR());
		BoletoBean boleto = new BoletoBean();
		
		boolean hasBoletoGerado = false;
		BoletoCreator boletoCreator = new BoletoCreator();
		
		for (DependenteSuplementar dependenteSuplementar : dependentesSuplementar) {
			dependenteSuplementar.getSituacoes().size();
			
			validaSituacaoDeBoletosDaCompetencia(dataVencimento, segurado, competencia);
			
			cobranca = getCobrancaDependenteSuplementar(competencia, dataVencimento, dependenteSuplementar, mapCartoes.get(dependenteSuplementar), mapGuiasParaCobranca.get(dependenteSuplementar), usuario);
			//cobrancas geradas individualmente vencem sempre no dia da sua geração
			cobranca.setDataValidade(new Date());
			
			cobranca = processaJurosEMultas(dataVencimento, cobranca, dependenteSuplementar, competencia);
			
			hasBoletoGerado = criaPdfBoleto(cobranca, boletoFactoryRemessa,boleto, boletoCreator);
		}
		
		for (TitularFinanceiroSR titular : titulares) {

			titular.getSituacoes().size();
			
			Map<TitularFinanceiroSR, Consignacao> mapTitularConsignacao = getMapConsignacoesCompetencia(competencia);
			
			BigDecimal valorCobrado = geraContasMatriculas(titular, competencia, mapGuiasParaCobranca, usuario, dataVencimento, mapTitularConsignacao, mapCartoes);
			
			if(valorCobrado.compareTo(BigDecimal.ZERO) > 0){

				validaSituacaoDeBoletosDaCompetencia(dataVencimento, segurado, competencia);
				
				cobranca = getCobrancaTitular(competencia, dataVencimento, titular, valorCobrado, usuario);
				//cobrancas geradas individualmente vencem sempre no dia da sua geração
				cobranca.setDataValidade(new Date());
			
				cobranca =  processaJurosEMultas(dataVencimento, cobranca, titular, competencia);
				
				hasBoletoGerado = criaPdfBoleto(cobranca, boletoFactoryRemessa,boleto, boletoCreator);
			}
			
			if (!hasBoletoGerado) {
				throw new ValidateException("Não há boleto a ser gerado!");
			}
			
			boletoFactoryRemessa.writeToFile("boletos_caixa.pdf");
		}
		
		return cobranca;
	}

	private void validaDataDeVencimento(Date dataVencimento)throws ValidateException {
		Date dataAtual = new Date();
		if (Utils.compareData(dataVencimento, new Date()) > 0){
			throw new ValidateException("Data de vencimento não pode ser superior a data atual!");
		}

		if (processaDiferencaEmDias(dataAtual,dataVencimento) > 60){
			throw new ValidateException("Data de vencimento não poder ser mais de 60 dias inferior à data atual.");
		}
	}

	private Date getUltimaCompetenciaComBoleto(TitularFinanceiroSR segurado) {
		List<Cobranca> cobrancas = new ArrayList<Cobranca>(segurado.getCobrancas()) ;
		
		Collections.sort(cobrancas, new Comparator<Cobranca>(){

			@Override
			public int compare(Cobranca c1, Cobranca c2) {
				return (c1.getCompetencia().compareTo(c2.getCompetencia())) * -1;
			}
			
		});
		
		Date ultimaCompetencia = cobrancas.isEmpty()? null : cobrancas.get(0).getCompetencia();
		return ultimaCompetencia;
	}

	/**
	 * Método responsável pelo processamento de juros e da multa para uma determinada cobrança
	 * @param dataVencimento
	 * @param cobranca
	 * @param ultimaCobranca
	 */
	private Cobranca processaJurosEMultas(Date dataVencimento, Cobranca cobranca, TitularFinanceiroSR segurado, Date competencia){
		validaSituacaoDeBoletosDaCompetencia(dataVencimento, segurado, competencia);
		
		Date dataCorrente = new Date();
		
		int diferencaEmDias = processaDiferencaEmDias(dataCorrente, dataVencimento);
		
		if(diferencaEmDias > 0){
			
			BigDecimal totalJuros = calculaJuros(boletoConfigurator.getJuros(), cobranca.getValorCobrado(), diferencaEmDias);
			BigDecimal multa = calculaMulta(cobranca.getValorCobrado(), diferencaEmDias);
			cobranca.setValorJurosMultaEncargosCobrado(totalJuros.add(multa));
		}
		return cobranca;
	}

	@SuppressWarnings("unchecked")
	private void validaSituacaoDeBoletosDaCompetencia(Date dataVencimento,TitularFinanceiroSR segurado, Date competencia) {
		List<Cobranca> cobrancas =  new ArrayList<Cobranca>();
		cobrancas = HibernateUtil.currentSession().createCriteria(Cobranca.class)
									.add(Expression.eq("titular", segurado))
									.add(Expression.eq("competencia", competencia))
									.setFetchMode("guias", FetchMode.SELECT)
									.list();
		
		if (!cobrancas.isEmpty()){
			//ordenacao da colecao por data da situacao
			Utils.sort(cobrancas, "situacao.dataSituacao");
			
			//captura a última cobranca que teve sua situação alterada
			Cobranca ultimaCobranca = cobrancas.get(cobrancas.size()-1);
			
			validaIsSituacaoPago(segurado, ultimaCobranca);
			validaIsSituacaoAbertaNaoVencido(segurado, ultimaCobranca);
			validaIsSituacaoAbertaVencido(segurado, ultimaCobranca);
			validaDataVencimento(segurado, ultimaCobranca, dataVencimento);
		}
	}

	private int processaDiferencaEmDias(Date dataVencimento, Date dataAtual){
		Calendar diaCorrente = new GregorianCalendar();
		diaCorrente.setTime(dataAtual);
		
		Calendar novaDataVencimento = new GregorianCalendar();
		novaDataVencimento.setTime(dataVencimento);
		
		return Utils.diferencaEmDias(diaCorrente,novaDataVencimento);
	}
	
	private void validaIsSituacaoPago(TitularFinanceiroSR segurado, Cobranca ultimaCobranca) {
		boolean isPago = ultimaCobranca.isSituacaoAtual(SituacaoEnum.PAGO.descricao());
		Assert.isFalse(isPago, "O beneficiário " + segurado.getNumeroDoCartao() + " já possui um boleto pago para esta competência. Não é possível regerá-lo.");
	}
	
	//Verifica se o último boleto desta competência está aberto, caso sim, pede para que o usuário cancele-o antes de regerar ou apenas reimprima.
	private void validaIsSituacaoAbertaNaoVencido(TitularFinanceiroSR segurado, Cobranca ultimaCobranca) {
		boolean isVencido = Utils.compareData(new Date(), ultimaCobranca.getDataVencimento()) == 1;
		boolean isAberto = ultimaCobranca.getSituacao().getDescricao().equals(SituacaoEnum.ABERTO.descricao());
		
		boolean isAbertoENaoVencido = isAberto && !isVencido;
		
		Assert.isFalse(isAbertoENaoVencido, "O beneficiário " + segurado.getNumeroDoCartao() + " já possui um boleto aberto e não vencido para esta competência, por favor, reimprima-o.");	
	}
	
	private void validaIsSituacaoAbertaVencido(TitularFinanceiroSR segurado, Cobranca ultimaCobranca) {
		boolean isVencido = Utils.compareData(new Date(), ultimaCobranca.getDataVencimento()) == 1;
		boolean isAberto = ultimaCobranca.getSituacao().getDescricao().equals(SituacaoEnum.ABERTO.descricao());
		
		boolean isAbertoENaoVencido = isAberto && isVencido;
		
		Assert.isFalse(isAbertoENaoVencido, "O beneficiário " + segurado.getNumeroDoCartao() + " já possui um boleto aberto e vencido para esta competência, por favor, " 
				+ "cancele-o antes de gerar outro boleto.");	
	}
	
	/**
	 * Método responsável pela regeração de um boleto  vencido. O boleto será gerado com a data atual como data de vencimento
	 * @param segurado
	 * @param ultimaCobranca
	 * @param dataVencimento
	 */
	private void validaDataVencimento(TitularFinanceiroSR segurado, Cobranca ultimaCobranca, Date dataVencimento) {
		boolean isVencido = Utils.compareData(new Date(), ultimaCobranca.getDataVencimento()) == 1;
		boolean novoVencimentoMaiorQueAtual = Utils.compareData(dataVencimento, new Date()) == 1;
		
		boolean vencidoEVencimentoMaior = isVencido && novoVencimentoMaiorQueAtual;
		
		Assert.isFalse(vencidoEVencimentoMaior, "O beneficiário " + segurado.getNumeroDoCartao() + " já possui um boleto vencido para esta competência. " 
					+ "Impossível regerá-lo com uma data de vencimento maior do que a atual.");
	}

	/**
	 * @see br.com.infowaypi.ecare.financeiro.boletos.BoletoCreator#calculaJuros
	 * @param percentual
	 * @param valor
	 * @param diferencaEmDias
	 * @return
	 */
	private BigDecimal calculaJuros(Float percentual, BigDecimal valor, int diferencaEmDias) {
		float numeroDeDiasNoMes = 30;
		 
		/**
		 * Regra anterior de cálculo dos juros. 
		 */
		//Cálculo da Taxa mensal de juros
//		BigDecimal jurosTotal = valor.multiply(new BigDecimal(percentual/100));
		
		//Cálculo da Taxa diária de juros
//		jurosTotal = jurosTotal.multiply(new BigDecimal(1/numeroDeDiasNoMes)).setScale(5, BigDecimal.ROUND_HALF_UP);// MoneyCalculation.divide(jurosTotal, numeroDeDiasNoMes);
		
		/**
		 * Foi alterada a regra, pois a anterior estava duvidosa.
		 * @author Luciano Infoway
		 * @since 21/02/2013
		 */
		BigDecimal porcentagemDia = new BigDecimal(numeroDeDiasNoMes/1000).setScale(2, BigDecimal.ROUND_HALF_UP); 
		
		//Taxa diárias de juros multiplicada pela quantidade de dias em atraso
		return porcentagemDia.multiply(new BigDecimal(diferencaEmDias));
	}
	
	/**
	 * Fornece um mapa com os segurados e suas respectivas guias aptas para cobrança.
	 * @param competencia
	 * @param titulares
	 * @return Map<AbstractSegurado, Set<GuiaSimples>>
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	 @Override
	protected Map<AbstractSegurado, Set<GuiaSimples>> getMapGuiasParaCobranca(Date competencia, Collection<TitularFinanceiroSR> titulares)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		//data que servirá como inicio do intervalo para busca das guias a serem cobradas
		Date dataInicial = null;
		
		TitularFinanceiroSR titular = (TitularFinanceiroSR) titulares.iterator().next();
		Set<Segurado> segurados = super.getTitularesComSeusDependentes(titulares);
		
		Date ultimaCompetenciaComBoletoPago = GeracaoBoletosDAO.getUltimaCompetenciaComBoletoPago(titular);
		
		if (ultimaCompetenciaComBoletoPago != null) {
			dataInicial = CompetenciaUtils.getInicioCompetencia(ultimaCompetenciaComBoletoPago);
		}
		
		List<GuiaSimples> listGuias = GeracaoBoletosDAO.getGuiasParaCobranca(segurados, dataInicial, new Date());
		
		return CollectionUtils.groupBy(listGuias, "segurado", AbstractSegurado.class);
	}
	
	/**
	 * Método criado para calcular a multa levando em consideração os dias após o vencimento do boleto.
	 * @author Luciano Infoway
	 * @since 19/02/2013
	 * @param valor
	 * @param diferencaEmDias
	 * @return
	 */
	private BigDecimal calculaMulta(BigDecimal valor, int diferencaEmDias) {
		BigDecimal multa = BigDecimal.ZERO;
		Float porcentagemAte60Dias = new Float(10);
		if (diferencaEmDias >= 1 && diferencaEmDias <= 30){
			multa = valor.multiply(new BigDecimal(boletoConfigurator.getMulta()/100));
		}
		else if (diferencaEmDias >= 31 && diferencaEmDias <= 60){
			multa = valor.multiply(new BigDecimal(porcentagemAte60Dias/100));
		}
		return MoneyCalculation.rounded(multa);
	}

	private boolean criaPdfBoleto(Cobranca cobranca, BoletoFactory boletoFactoryRemessa, BoletoBean boleto,
			BoletoCreator boletoCreator) throws MalformedURLException,
			IOException, DocumentException {
		
		boolean hasBoletoGerado;
		try {
			boleto = boletoCreator.create(cobranca, false);
		}
		catch (NullPointerException e) {
			e.printStackTrace();
			//TODO ajeitar depois para não entrar aqui
		}
		hasBoletoGerado = true;
		boletoFactoryRemessa.addBoleto(boleto, new CaixaEconomicaSIGCB(boleto), BoletoFactory.TOPO_MODELO_2,55f);
		
		return hasBoletoGerado;
	}

	private TitularFinanceiroSR getUniqueSegurado(String cpf, String numeroDoCartao) throws Exception {
		SearchAgent sa = new SearchAgent();
		
		if(Utils.isStringVazia(cpf) && Utils.isStringVazia(numeroDoCartao)){
			throw new Exception("Preencha ou o cpf ou o número do cartão.");
		}
		
		if(!Utils.isStringVazia(cpf))
			sa.addParameter(new Equals("pessoaFisica.cpf", cpf));
		else if(!Utils.isStringVazia(numeroDoCartao))
			sa.addParameter(new Equals("numeroDoCartao", numeroDoCartao));
		
		TitularFinanceiroSR segurado;// = sa.uniqueResult(TitularFinanceiroSR.class);
		
		//no caso de algm q migrou de dependente p pensionista, vai ter 2 cpfs no banco, um ativo e outro cancelado.
		try {
			segurado = sa.uniqueResult(TitularFinanceiroSR.class);
		} catch (NonUniqueResultException e) {
			sa.addParameter(new Equals("situacao.descricao", SituacaoEnum.ATIVO.descricao()));
			segurado = sa.uniqueResult(TitularFinanceiroSR.class);
		}
		
		Assert.isNotNull(segurado, "Não foi encontrado BENEFICIÁRIO com os dados informados");
//		Assert.isTrue(segurado.isAtivo(), MensagemErroEnum.SEGURADO_INATIVO_NO_SISTEMA.getMessage());
		
		Boolean pagaBoleto = false;
		if (segurado.isSeguradoPensionista()||segurado.isSeguradoTitular()){
			for (AbstractMatricula matricula : segurado.getMatriculasAtivas()) {
				if (matricula.getTipoPagamento() != null && matricula.getTipoPagamento().equals(Constantes.BOLETO)){
					pagaBoleto = true;
					break;
				}
			}
			Assert.isTrue(pagaBoleto, "O beneficiário não paga em boleto.");
		}
		
		return segurado;
	}
	
	public Cobranca salvarCobranca(Cobranca cobranca) throws Exception{
		ImplDAO.save(cobranca);
		
		for (ContaMatricula contaMatricula : contasMatriculas) {
			ImplDAO.save(contaMatricula);
		}
		
		return cobranca;
	}
}
