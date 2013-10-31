package br.com.infowaypi.ecare.financeiro.boletos;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;

import br.com.infowaypi.ecare.enums.SituacaoCartaoEnum;
import br.com.infowaypi.ecare.financeiro.Cobranca;
import br.com.infowaypi.ecare.financeiro.consignacao.ConsignacaoSegurado;
import br.com.infowaypi.ecare.financeiro.conta.ContaMatricula;
import br.com.infowaypi.ecare.financeiro.conta.DetalheContaSegurado;
import br.com.infowaypi.ecare.financeiro.conta.DetalheContaTitular;
import br.com.infowaypi.ecare.segurados.AbstractMatricula;
import br.com.infowaypi.ecare.segurados.Cartao;
import br.com.infowaypi.ecare.segurados.DependenteSR;
import br.com.infowaypi.ecare.segurados.DependenteSuplementar;
import br.com.infowaypi.ecare.segurados.Matricula;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.segurados.TitularFinanceiroSR;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.consignacao.Consignacao;
import br.com.infowaypi.ecarebc.financeiro.conta.BoletoConfigurator;
import br.com.infowaypi.ecarebc.financeiro.conta.Conta;
import br.com.infowaypi.ecarebc.financeiro.conta.ContaInterface;
import br.com.infowaypi.ecarebc.painelDeControle.PainelDeControle;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.jbank.boletos.BoletoBean;
import br.com.infowaypi.jbank.boletos.BoletoFactory;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.CollectionUtils;
import br.com.infowaypi.msr.utils.Utils;

@SuppressWarnings("unchecked")
public class GeracaoBoletosService {
	
		protected static final Date DATA_INICIO_BOLETO_AUTOMATIZADO 	= Utils.parse("01/05/2010");
		protected Set<TitularFinanceiroSR> titulares = new HashSet<TitularFinanceiroSR>();
		protected Set<DependenteSuplementar> dependentesSuplementar =  new HashSet<DependenteSuplementar>();
		protected static List<ContaMatricula> contasMatriculas = new ArrayList<ContaMatricula>();
		protected static GregorianCalendar ultimoDiaDaCompetencia = new GregorianCalendar();
		private List<BoletoBean> boletos = new ArrayList<BoletoBean>();
		private BoletoConfigurator boletoConfigurator					= this.getBoletoConfigurator();

		private BigDecimal salario_base_limite = BigDecimal.ZERO;
		private BigDecimal aliquota_financiamento = BigDecimal.ZERO;
		private BigDecimal limite_Financiamento = BigDecimal.ZERO;
		
		public RemessaDeBoletos gerarBoletos(Date competencia, Date dataVencimento, UsuarioInterface usuario) throws Exception {
			
			HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
			
			if(dataVencimento.before(new Date())) {
				throw new ValidateException("Data de vencimento deve ser posterior à data atual!");
			}
			
			SearchAgent sa = new SearchAgent();
			sa.addParameter(new Equals("competencia", competencia));
			int cobrancasParaEssaCompetencia = sa.resultCount(RemessaDeBoletos.class);
			
			if (cobrancasParaEssaCompetencia != 0) {
				throw new RuntimeException("Já foi gerada remessa para essa competência.");
			}
			
			if(getUltimaCompetenciaProcessada() != null){
				Calendar competenciaValida = Calendar.getInstance();
				competenciaValida.setTime(getUltimaCompetenciaProcessada());
				competenciaValida.add(Calendar.MONTH, 1);
				
				if((Utils.compareData(competencia, competenciaValida.getTime()) != 0)) {
					throw new RuntimeException(MensagemErroEnum.NAO_E_POSSIVEL_GERAR_REMESSA_PARA_A_COMPETENCIA_INFORMADA.getMessage());
				}
			}
			
			ultimoDiaDaCompetencia.setTime(competencia);
			ultimoDiaDaCompetencia.set(Calendar.DAY_OF_MONTH, ultimoDiaDaCompetencia.getActualMaximum(Calendar.DATE));
			
			titulares = GeracaoBoletosDAO.buscarTitulares();
			dependentesSuplementar = GeracaoBoletosDAO.buscarDependentesSuplementares();
			
			Set<TitularFinanceiroSR> segurados = new HashSet<TitularFinanceiroSR>(titulares);
			segurados.addAll(dependentesSuplementar);
			
			List<TitularFinanceiroSR> seguradosComBoletoNaCompetenciaAnterior = GeracaoBoletosDAO.getTitularesComCobrancasPagasDaCompentenciaAnterior(competencia);
			
			//Pega as coleções de segurados ativos com matricula boleto ativa e mantem apenas os que tem cobranca paga na competencia anterior.
			segurados.retainAll(seguradosComBoletoNaCompetenciaAnterior); 
			titulares.retainAll(seguradosComBoletoNaCompetenciaAnterior);
			dependentesSuplementar.retainAll(seguradosComBoletoNaCompetenciaAnterior);
			
			Map<TitularFinanceiroSR, Set<Cobranca>> mapCobrancasNaCompetenciaInformada = CollectionUtils.groupBy(GeracaoBoletosDAO.getCobrancasNaoCanceladasDaCompetenciaInformada(competencia,segurados), "titular", TitularFinanceiroSR.class);

			Map<AbstractSegurado, Set<GuiaSimples>> mapGuiasParaCobranca = getMapGuiasParaCobranca(competencia, segurados);
			
			Map<Segurado, Set<Cartao>> mapCartoes = CollectionUtils.groupBy(GeracaoBoletosDAO.getCartoesParaCobranca(), "segurado", Segurado.class);

			Map<TitularFinanceiroSR, Consignacao> mapTitularConsignacao = getMapConsignacoesCompetencia(competencia);
			
			Cobranca cobranca = null;
			RemessaDeBoletos remessa = new RemessaDeBoletos();
			remessa.setCompetencia(competencia);
			BoletoFactory boletoFactoryRemessa = new BoletoFactory();
			boletoFactoryRemessa.setGenerator(new PDFGeneratorSR());
			BoletoBean boleto = new BoletoBean();
			
			boolean hasBoletoGerado = false;
			BoletoCreator boletoCreator = new BoletoCreator();

			for (DependenteSuplementar dependenteSuplementar : dependentesSuplementar) {
				
				if (mapCobrancasNaCompetenciaInformada.keySet().contains(dependenteSuplementar)){
					continue;
				}
			
				cobranca = getCobrancaDependenteSuplementar(competencia, dataVencimento, dependenteSuplementar, mapCartoes.get(dependenteSuplementar), mapGuiasParaCobranca.get(dependenteSuplementar), usuario);
				Set<GuiaSimples> guiasDoTitular = mapGuiasParaCobranca.get(cobranca.getTitularFinanceiro());
				if (guiasDoTitular != null && !guiasDoTitular.isEmpty()) {
					cobranca.addAllGuias(guiasDoTitular);
				}
				
				try{
					boleto = boletoCreator.create(cobranca, false);
				}
				catch(NullPointerException e){
					e.printStackTrace();
					//TODO ajeitar depois para não entrar aqui
				}
				hasBoletoGerado = true;
				
				// adiciona-se o boleto gerado à coleçao de boletos geral
				boletos.add(boleto);

				//TODO 
//				ImplDAO.save(cobranca);
				adicionaCobrancaEContaNaRemessa(cobranca.getColecaoContas().getContas(), cobranca, remessa);
				//TODO não esquecer de criar a conta
				
			}

			for (TitularFinanceiroSR titular : titulares) {
				
				if (mapCobrancasNaCompetenciaInformada.keySet().contains(titular)){
					continue;
				}
				
				BigDecimal valorCobrado = geraContasMatriculas(titular, competencia, mapGuiasParaCobranca, usuario, dataVencimento, mapTitularConsignacao, mapCartoes);
				
				if(valorCobrado.compareTo(BigDecimal.ZERO) > 0){
					
					cobranca = getCobrancaTitular(competencia, dataVencimento, titular, valorCobrado, usuario);
					Set<GuiaSimples> guiasDoTitular = mapGuiasParaCobranca.get(cobranca.getTitularFinanceiro());
					if (guiasDoTitular != null && !guiasDoTitular.isEmpty()) {
						cobranca.addAllGuias(guiasDoTitular);
					}
					
					try {
						boleto = boletoCreator.create(cobranca, false);
					}
					catch (NullPointerException e) {
						e.printStackTrace();
						//TODO ajeitar depois para não entrar aqui
					}
					
					hasBoletoGerado = true;
					
					// adiciona-se o boleto gerado à coleçao de boletos geral
					boletos.add(boleto);
					
					adicionaCobrancaEContaNaRemessa(cobranca.getColecaoContas().getContas(), cobranca, remessa);
				}
				
			}
			
			if (!hasBoletoGerado) {
				throw new ValidateException(MensagemErroEnum.NAO_HA_BOLETO_A_SER_GERADO.getMessage());
			}
			
			//ordena todos os boletos por nome do sacado em ordem alfabética. 
			Utils.sort(boletos,"nomeSacado");
			
			int contador = 0;
            for (BoletoBean bloqueto : boletos) {
                bloqueto.setContador(++contador);
                //esse 38 é a altura onde a imagem de baixo começa. Qto menor, mais prox da base da pag ela fica
                boletoFactoryRemessa.addBoleto(bloqueto, new CaixaEconomicaSIGCB(bloqueto), BoletoFactory.TOPO_MODELO_2, 38f);
            }
			
			remessa.setConteudoArquivo(boletoFactoryRemessa.writeToByteArray().toByteArray());
			remessa.setDataGeracao(new Date());
			
			return remessa;
		}
		
		protected Map<TitularFinanceiroSR, Consignacao> getMapConsignacoesCompetencia(
				Date competencia) {
			
			Map<TitularFinanceiroSR, Consignacao> mapConsignacoes = new HashMap<TitularFinanceiroSR, Consignacao>();
			List<ConsignacaoSegurado> consignacoes = GeracaoBoletosDAO.getConsignacoesDaCompetencia(competencia, titulares);
			for (ConsignacaoSegurado consignacao : consignacoes) {
				mapConsignacoes.put(consignacao.getTitular(), consignacao);
			}
			return mapConsignacoes;
		}

		protected Cobranca getCobrancaTitular(Date competencia,
				Date dataVencimento, TitularFinanceiroSR titular,
				BigDecimal valorCobrado, UsuarioInterface usuario) {
			ContaInterface conta;
			Cobranca cobranca;
			conta = createContaTitular(competencia, dataVencimento, valorCobrado);
			
			cobranca = createCobrancaTitular(competencia, dataVencimento, conta, titular, valorCobrado, usuario);
			return cobranca;
		}
		
		private ContaInterface createContaTitular(Date competencia,
				Date dataVencimento, BigDecimal valorCobrado) {
			ContaInterface conta;
			conta = new Conta();
			conta.setValorCobrado(valorCobrado);
			conta.setCompetencia(competencia);
			conta.setDataVencimento(dataVencimento);
			conta.setVinculado(true);
			conta.mudarSituacao(null, SituacaoEnum.ABERTO.descricao(), "Geração de Conta para Boletos", new Date());
			return conta;
		}
		
		protected Cobranca createCobrancaTitular(Date competencia,
				Date dataVencimento, ContaInterface conta,
				TitularFinanceiroSR titular, BigDecimal valorCobrado, UsuarioInterface usuario) {
			Cobranca cobranca;
			cobranca = new Cobranca();
			for (ContaMatricula contaMatricula : titular.getContasMatriculas(competencia)) {
				cobranca.getGuias().addAll(contaMatricula.getGuias());
			}
			
			cobranca.setValorCobrado(valorCobrado);
			cobranca.getColecaoContas().add(conta);
			cobranca.setCompetencia(competencia);
			cobranca.setDataVencimento(dataVencimento);
			cobranca.setTitular(titular);
			cobranca.mudarSituacao(usuario, SituacaoEnum.ABERTO.descricao(), "Geração de Cobrança para Boletos", new Date());
			
			Date dataValidade = this.processaDataValidade(dataVencimento, boletoConfigurator.getValidade());
			cobranca.setDataValidade(dataValidade);
			
			return cobranca;
		}
		
		public Cobranca getCobrancaDependenteSuplementar(
				Date competencia,
				Date dataVencimento,
				DependenteSuplementar dependenteSuplementar,
				Set<Cartao> cartoes,
				Set<GuiaSimples> mapGuiasParaCobranca, UsuarioInterface usuario) {
			
			ContaInterface conta = createConta(competencia, dataVencimento);
			Cobranca cobranca = createCobranca(competencia, dataVencimento, conta, dependenteSuplementar, usuario);
			DetalheContaTitular detalhe = createDetalhe(competencia, dataVencimento, cartoes, mapGuiasParaCobranca, cobranca, dependenteSuplementar);
			cobranca.setValorCobrado(detalhe.getValorTotal());
			conta.setValorCobrado(detalhe.getValorTotal());
			
			Date dataValidade = this.processaDataValidade(dataVencimento, boletoConfigurator.getValidade());
			cobranca.setDataValidade(dataValidade);
			
			return cobranca;
		}
		
		private ContaInterface createConta(Date competencia, Date dataVencimento) {
			ContaInterface conta;
			conta = new Conta();
			conta.setCompetencia(competencia);
			conta.setDataVencimento(dataVencimento);
			conta.setVinculado(true);
			conta.mudarSituacao(null, SituacaoEnum.ABERTO.descricao(), "Geração de Conta para Boletos", new Date());
			return conta;
		}
		
		private Cobranca createCobranca(Date competencia, Date dataVencimento,
				ContaInterface conta,
				DependenteSuplementar dependenteSuplementar, UsuarioInterface usuario) {
			Cobranca cobranca;
			cobranca = new Cobranca();
			cobranca.setTitular(dependenteSuplementar);
			cobranca.getColecaoContas().add(conta);
			cobranca.setCompetencia(competencia);
			cobranca.setDataVencimento(dataVencimento);
			cobranca.mudarSituacao(usuario, SituacaoEnum.ABERTO.descricao(), "Geração de Cobrança para Boletos", new Date());
			return cobranca;
		}
		
		public DetalheContaTitular createDetalhe(Date competencia, Date dataVencimento, Set<Cartao> cartoes, 
			Set<GuiaSimples> mapGuiasParaCobranca, Cobranca cobranca,
				DependenteSuplementar dependenteSuplementar) {
			DetalheContaTitular detalhe = dependenteSuplementar.getDetalheContaTitular(competencia);
			BigDecimal valorCoparticipacao = BoletoUtils.getValorCoparticipacaoSuplementar(dependenteSuplementar, mapGuiasParaCobranca, cobranca);
			BigDecimal valorFinanciamento = dependenteSuplementar.getValorFinanciamentoDependente(BigDecimal.ZERO, dataVencimento)[1];
			
			//Se o dependente suplementar estiver cancelado, nao se cobrará financiamento
			if(!dependenteSuplementar.isSituacaoAtual(SituacaoEnum.ATIVO.descricao())) {
				valorFinanciamento = BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP);
			}
			setValoresDetalhe(cobranca, dependenteSuplementar, detalhe, cartoes, valorCoparticipacao, valorFinanciamento);
			return detalhe;
		}

		private void setValoresDetalhe(Cobranca cobranca, DependenteSuplementar dependenteSuplementar, DetalheContaTitular detalhe,
				Set<Cartao> cartoes, BigDecimal valorCoparticipacao, BigDecimal valorFinanciamento) {
			
			detalhe.setValorCoparticipacao(ContaMatricula.verificaTetoCoparticipacao(valorCoparticipacao));
			
			BigDecimal valorSegundaViaCartao = getValorSegundaViaCartao(detalhe, cartoes);
			if (dependenteSuplementar.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())){
				detalhe.setValorFinanciamento(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			else {
				detalhe.setValorFinanciamento(valorFinanciamento.setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			
			detalhe.setValorSegundaViaCartao(valorSegundaViaCartao.setScale(2, BigDecimal.ROUND_HALF_UP));
			detalhe.getGuias().addAll(cobranca.getGuias());
		}

		private BigDecimal getValorSegundaViaCartao(DetalheContaTitular detalhe, Set<Cartao> cartoes) {
			BigDecimal valorSegundaViaCartao = BigDecimal.ZERO;
			if(cartoes != null){
				for (Cartao cartao : cartoes) {
					valorSegundaViaCartao = valorSegundaViaCartao.add(Cartao.VALOR_CARTAO);
					cartao.setSituacao(SituacaoCartaoEnum.COBRADO.getDescricao());
					detalhe.getCartoes().add(cartao);
				}
			}
			return valorSegundaViaCartao;
		}


		protected void adicionaCobrancaEContaNaRemessa(Set<ContaInterface> contas, Cobranca cobranca, RemessaDeBoletos remessa) {
			remessa.getCobranca().add(cobranca);
			remessa.getContas().addAll(contas);
		}
		

		/**
		 * Fornece um mapa com os segurados e suas respectivas guias aptas para cobrança. Baseando na data de geração da última competencia
		 * processada.
		 * @param competencia
		 * @param titulares
		 * @return Map<AbstractSegurado, Set<GuiaSimples>>
		 * @throws IllegalAccessException
		 * @throws InvocationTargetException
		 * @throws NoSuchMethodException
		 */
		protected Map<AbstractSegurado, Set<GuiaSimples>> getMapGuiasParaCobranca(Date competencia, Collection<TitularFinanceiroSR> titulares)
				throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

			Set<Segurado> segurados = this.getTitularesComSeusDependentes(titulares);
			Date dataGeracaoUltimaRemessaProcessada = GeracaoBoletosDAO.getDataGeracaoUltimaRemessaProcessada();
			dataGeracaoUltimaRemessaProcessada = dataGeracaoUltimaRemessaProcessada ==null ? competencia : dataGeracaoUltimaRemessaProcessada;
			List<GuiaSimples> listGuias = GeracaoBoletosDAO.getGuiasParaCobranca(segurados, dataGeracaoUltimaRemessaProcessada, new Date());
			
			return CollectionUtils.groupBy(listGuias, "segurado", AbstractSegurado.class);
		}

		/** Pega a coleção de titulares e insere nesta coleção todos os depentes deste titular
		 * @param titulares
		 * @param segurados
		 */
		protected Set<Segurado> getTitularesComSeusDependentes(Collection<TitularFinanceiroSR> titulares) {
			Set<Segurado> segurados = new HashSet<Segurado>(titulares);
			for (TitularFinanceiroSR titular : titulares) {
				for (Segurado dependente : titular.getDependentes()) {
					segurados.add(dependente);
				}
			}
			return segurados;
		}
		
		/**
		 * Método responsável por gerar as contas dos titulares com base na competência, no mapa de guias aptas para cobrança e na data de vencimento.
		 * @param titulares
		 * @param competencia
		 * @param mapGuiasParaCobranca
		 * @param usuario
		 * @param dataVencimento
		 * @param mapTitularConsignacao 
		 * @param mapCartoes
		 * @return retorna o somatório das contas matrícula que foram geradas
		 */
		public BigDecimal geraContasMatriculas(TitularFinanceiroSR titular, Date competencia, Map<AbstractSegurado, Set<GuiaSimples>> 
			mapGuiasParaCobranca, UsuarioInterface usuario, Date dataVencimento, Map<TitularFinanceiroSR, Consignacao> mapTitularConsignacao, 
			Map<Segurado, Set<Cartao>> mapCartoes) {
			
			ContaMatricula contaMatricula;
			BigDecimal valorTotal = BigDecimal.ZERO;
			
			salario_base_limite = PainelDeControle.getPainel().getSalarioBaseLimite();
			aliquota_financiamento = PainelDeControle.getPainel().getAliquotaDeFinanciamento();
			limite_Financiamento = salario_base_limite.multiply(aliquota_financiamento.divide(new BigDecimal(100))).setScale(2, BigDecimal.ROUND_HALF_UP);
			
			titular.zerarDetalhesCobranca(competencia);
			for (AbstractMatricula matricula : titular.getMatriculasAtivas()) {
				if(matricula.getTipoPagamento()!= null && matricula.getTipoPagamento() == Matricula.FORMA_PAGAMENTO_BOLETO) {
					contaMatricula = new ContaMatricula(matricula,competencia, mapGuiasParaCobranca, usuario, dataVencimento);
					calculaValorFinanciamento(dataVencimento, contaMatricula, mapTitularConsignacao, limite_Financiamento);
					contasMatriculas.add(contaMatricula);
					valorTotal = valorTotal.add(contaMatricula.getValorTotal());
				}
			}
			return valorTotal ;
		}

		/**
		 * Este Método verifica se o Beneficiário solicitou <br>
		 * alguma segunda via de cartão para que este seja cobrado <br>
		 * Caso exista um valor de 2ª via de cartão a ser cobrado esse valor é cobrado na conta matricula
		 */
		private void calculaValorSegundaViaCartao(ContaMatricula contaMatricula, Map<Segurado, Set<Cartao>> mapCartoes) {
			BigDecimal valorTotalCartoes = BigDecimal.ZERO;
			BigDecimal valorCartoesTitular = BigDecimal.ZERO;
			TitularFinanceiroSR titular = contaMatricula.getMatricula().getTitular();

			//cartões do titular
			DetalheContaTitular detalheContaTitular = titular.getDetalheContaTitular(contaMatricula.getCompetencia());
			Set<Cartao> cartoes = mapCartoes.containsKey(titular)? mapCartoes.get(titular) : new HashSet<Cartao>();
			
			for (Cartao cartao : cartoes) {
				contaMatricula.getCartoes().add(cartao);
				valorCartoesTitular = valorCartoesTitular.add(Cartao.VALOR_CARTAO);
			}
			detalheContaTitular.setValorSegundaViaCartao(detalheContaTitular.getValorSegundaViaCartao().add(valorCartoesTitular));
			
			//cartões dos dependentes
			BigDecimal valorCartoesDependente = BigDecimal.ZERO;
			for (DependenteSR dependente : titular.getDependentes(SituacaoEnum.ATIVO.descricao())) {
				DetalheContaSegurado detalheContaDependente = dependente.getDetalheContaDependente(contaMatricula.getCompetencia());
				cartoes = mapCartoes.containsKey(dependente)? mapCartoes.get(dependente) : new HashSet<Cartao>();
				for (Cartao cartao : cartoes) {
					contaMatricula.getCartoes().add(cartao);
					valorCartoesDependente = valorCartoesDependente.add(Cartao.VALOR_CARTAO);
				}
				detalheContaDependente.setValorSegundaViaCartao(detalheContaDependente.getValorSegundaViaCartao().add(valorCartoesDependente));
			}
			
			valorTotalCartoes = valorCartoesTitular.add(valorCartoesDependente);
			contaMatricula.setValorSegundaViaCartao(valorTotalCartoes.setScale(2, BigDecimal.ROUND_HALF_UP));
		}
		
		/**
		 * Verifica se as consignações da competência já não atingiram o limite de financiamento. 
		 * Calcula o valor da conta. <br>
		 * Verifica se a soma da conta + consignações > limite. <br>
		 * Se sim: calcular a diferença restante para atingir o limite e gerar a conta com este valor. <br> 
		 * Se não: criar conta. <br>
		 * Este método não gera valor de financiamento caso o titular esteja cancelado 
		 * @param dataVencimento 
		 * @param mapTitularConsignacao 
		 */
		private void calculaValorFinanciamento(Date dataVencimento,ContaMatricula contaMatricula, 
				Map<TitularFinanceiroSR, Consignacao> mapTitularConsignacao, BigDecimal limite_Financiamento) {
			
			boolean isTitularCancelado = contaMatricula.getMatricula().getTitular().isSituacaoAtual(SituacaoEnum.CANCELADO.descricao());
			if (!isTitularCancelado){
				BigDecimal valorFinanciamento = BigDecimal.ZERO;
				Consignacao consignacao = mapTitularConsignacao.get(contaMatricula.getMatricula().getSegurado());
				BigDecimal valorConsignacao = consignacao != null ? consignacao.getValorFinanciamento() : BigDecimal.ZERO;
				
				boolean isValorConsignacaoNaoSuperaLimiteFinanciamento = valorConsignacao.compareTo(limite_Financiamento) < 0;
				if (isValorConsignacaoNaoSuperaLimiteFinanciamento) {
					BigDecimal salarioBase = contaMatricula.getMatricula().getSalario(); 
					BigDecimal[] valorFinanciamentoArray = {BigDecimal.ZERO, BigDecimal.ZERO};
					valorFinanciamentoArray = contaMatricula.getMatricula().getTitular().getValorTotalFinanciamento(salarioBase, dataVencimento);
					valorFinanciamento = valorFinanciamentoArray[1];
					
					detalharValorFinanciamento(contaMatricula,salarioBase, dataVencimento);
					
					boolean isValorConsignacaoMaisValorBoletoSuperaLimiteFinanciamento = (valorFinanciamento.add(valorConsignacao)).compareTo(limite_Financiamento) > 0;
					if (isValorConsignacaoMaisValorBoletoSuperaLimiteFinanciamento) {
						contaMatricula.setValorFinanciamento(limite_Financiamento.subtract(valorConsignacao).setScale(2, BigDecimal.ROUND_HALF_UP));
					}
					else {
						contaMatricula.setValorFinanciamento(valorFinanciamento.setScale(2, BigDecimal.ROUND_HALF_UP));
					}
				}
				contaMatricula.setValorFinanciamento(valorFinanciamento.setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			
		}
		
		/**
		 * @param contaMatricula 
		 * @param salarioBase
		 * @param dataVencimento 
		 */
		private void detalharValorFinanciamento(ContaMatricula contaMatricula, BigDecimal salarioBase, Date dataVencimento) {
			//detalhando o financiamento do titular
			DetalheContaTitular detalheContaTitular = contaMatricula.getMatricula().getSegurado().getDetalheContaTitular(contaMatricula.getCompetencia());
			BigDecimal[] valorTitular = contaMatricula.getMatricula().getSegurado().getValorFinanciamento(salarioBase);
			detalheContaTitular.setValorFinanciamento(valorTitular[1]);
			
			//detalhando o financiamento dos dependentes
			DetalheContaSegurado detalheContaDependente;
			for (DependenteSR dependente : contaMatricula.getMatricula().getSegurado().getDependentesCobranca(contaMatricula.getCompetencia())) {
				detalheContaDependente = dependente.getDetalheContaDependente(contaMatricula.getCompetencia());
				BigDecimal[] valorDependente = dependente.getValorFinanciamentoDependente(salarioBase, dataVencimento);
				if(!dependente.isSituacaoAtual(SituacaoEnum.ATIVO.descricao())) {
					valorDependente[1] = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
				}
				detalheContaDependente.setValorFinanciamento(valorDependente[1]);
			}
			
		}
		
		public void salvarRemessa(RemessaDeBoletos remessa) throws Exception{
			saveRemessa(remessa);
			salvarCobrancas(remessa);
			salvarContasMatricula();
		}

		private void saveRemessa(RemessaDeBoletos remessa) throws Exception {
			ImplDAO.save(remessa);
		}

		private void salvarCobrancas(RemessaDeBoletos remessa) throws Exception {
			for (Cobranca cobranca : remessa.getCobranca()) {
				ImplDAO.save(cobranca);
			}
		}

		private void salvarContasMatricula() throws Exception {
			for (ContaMatricula contaMatricula : contasMatriculas) {
				ImplDAO.save(contaMatricula);
			}
		}
		
		protected BoletoConfigurator getBoletoConfigurator(){
			return (BoletoConfigurator) HibernateUtil.currentSession().createCriteria(BoletoConfigurator.class)
																		.add(Expression.eq("ativo", true))
																		.uniqueResult();
		}

		
		private Date processaDataValidade(Date dataVencimento, int quantDiasValidade) {

			Calendar calendarVencimento = Calendar.getInstance();
			calendarVencimento.setTime(dataVencimento);

			Date dateVencimento = Utils.incrementaDias(calendarVencimento, quantDiasValidade);

			return dateVencimento;		
		}

		private Date getUltimaCompetenciaProcessada() {
			Criteria crit = HibernateUtil.currentSession().createCriteria(RemessaDeBoletos.class);
			ProjectionList proList = Projections.projectionList();
			proList.add(Projections.max("competencia"));
			crit.setProjection(proList);
			Object competencia = crit.uniqueResult();

			return (Date) competencia;
		}

}
