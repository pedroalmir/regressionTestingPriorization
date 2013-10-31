package br.com.infowaypi.ecare.services.financeiro.consignacao;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.com.infowaypi.ecare.atendimentos.GuiaSimplesTuning;
import br.com.infowaypi.ecare.constantes.Constantes;
import br.com.infowaypi.ecare.financeiro.arquivo.ArquivoDeEnvioConsignacao;
import br.com.infowaypi.ecare.financeiro.consignacao.ConsignacaoMatriculaInterface;
import br.com.infowaypi.ecare.financeiro.consignacao.GerarArquivoConsignacaoProgressBar;
import br.com.infowaypi.ecare.financeiro.consignacao.tuning.ConsignacaoMatriculaTuning;
import br.com.infowaypi.ecare.financeiro.consignacao.tuning.ConsignacaoSeguradoTuning;
import br.com.infowaypi.ecare.segurados.AbstractMatricula;
import br.com.infowaypi.ecare.segurados.Empresa;
import br.com.infowaypi.ecare.segurados.tuning.DependenteConsignacaoTuning;
import br.com.infowaypi.ecare.segurados.tuning.MatriculaTuning;
import br.com.infowaypi.ecare.segurados.tuning.TitularConsignacaoTuning;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.arquivos.ArquivoInterface;
import br.com.infowaypi.ecarebc.financeiro.consignacao.Consignacao;
import br.com.infowaypi.ecarebc.financeiro.conta.ComponenteColecaoContas;
import br.com.infowaypi.ecarebc.painelDeControle.PainelDeControle;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.utils.Utils;

@SuppressWarnings({"rawtypes", "unused", "unchecked"})
public class GeracaoConsignacaoService {

	private List<Consignacao> consignacoesSegurado 	= new ArrayList<Consignacao>();
	private List<Consignacao> consignacoesMatricula = new ArrayList<Consignacao>();
	private List<String> cpfsSeguradosURBEfetivos 	= new ArrayList<String>();
	private Map<String, GuiasValorCoParticipacao> coParticipacaoPorTitular = new HashMap<String, GuiasValorCoParticipacao>();
	private static Map<Empresa, List<ConsignacaoMatriculaInterface>> consignacoesPorEmpresa = new HashMap<Empresa, List<ConsignacaoMatriculaInterface>>();
	private int quantConsignacoesMatriculas = 0;
	private int quantConsignacoesMatriculasNaoZeradas = 0;
	private BigDecimal valorTotalConsignacoes = BigDecimal.ZERO;
	private BigDecimal valorTotalCoparticipacao = BigDecimal.ZERO;
	private BigDecimal valorTotalFinanciamento = BigDecimal.ZERO;
	private StringBuffer log = new StringBuffer();

	private BigDecimal salario_base_limite = BigDecimal.ZERO;
	private BigDecimal aliquota_financiamento = BigDecimal.ZERO;
	private BigDecimal limiteFinanciamentoTitular = BigDecimal.ZERO;
	
	private boolean isTitularEfetivoDaURB = false;
	
	public void informarDados(Date competencia, Date dataPagamento, GerarArquivoConsignacaoProgressBar progressBar) throws Exception{

		progressBar.setProgessStatus("Buscando titulares no banco de dados...");
		List<TitularConsignacaoTuning> titulares = buscarTitulares();
		
		initMapGuias(titulares, competencia);
		
		int count = 0;
		
		progressBar.setProgressMax(titulares.size());
		progressBar.setProgessTitulo("Geração de Consignações");
		progressBar.setProgessStatus("Processando Consignações...");
		
		for(TitularConsignacaoTuning titular : titulares){
			count++;
			progressBar.incrementarProgresso();
			if(!titular.getMatriculasAtivas().isEmpty()) {
				isTitularEfetivoDaURB = isTitularEfetivoDaURB(titular);
				gerarConsignacao(titular, competencia, dataPagamento);
				HibernateUtil.currentSession().save(titular);
			}	
		}
	}
	
	BigDecimal valorTotalFinanciamentoDependentes;
	BigDecimal valorTotalFinanciamentoTitular;
	BigDecimal salarioBase;
	BigDecimal salarioTotal;
	
	BigDecimal valorFinanciamento;
	BigDecimal valorFinanciamentoTitular;
	BigDecimal aliquotaTitularTotal;
	BigDecimal valorFinanciamentoDependentes;
	BigDecimal valorCoparticipacao;
	ComponenteColecaoContas colecaoContas;
	ConsignacaoSeguradoTuning consignacao; 
	Set<GuiaSimplesTuning> guias;
	
	public StringBuffer getLog() {
		return log;
	}

	public void setLog(StringBuffer log) {
		this.log = log;
	}

	private void gerarConsignacao(TitularConsignacaoTuning titular,Date competencia, Date dataPagamento) throws Exception{
		zeraValores();

		inicializaConsignacao(titular, competencia, dataPagamento);
		
		// a coleção de matriculas do TitularConsignacaoTuning é ordenada por order-by="salario desc, ativa desc"
		// garantindo que a primeira matricula é a de maior salario  dentre as matriculas ativas
		MatriculaTuning matriculaMaiorSalario = titular.getMatriculas().iterator().next();	
		
		log.append(System.getProperty("line.separator"));
		log.append("################");
		log.append(System.getProperty("line.separator"));
		log.append("CPF: "+ titular.getPessoaFisica().getCpf());
		log.append(System.getProperty("line.separator"));
		log.append("Situação titular: " + titular.getSituacao().getDescricao());
		log.append(System.getProperty("line.separator"));
		
		int numMat = 0;
		
		for(AbstractMatricula matricula : titular.getMatriculasAtivas()){
			if(!matricula.isAtiva())
				continue;
			
			quantConsignacoesMatriculas++;
			numMat++;
			valorFinanciamento = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
			valorFinanciamentoTitular = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
			aliquotaTitularTotal = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
			valorFinanciamentoDependentes = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);

			//Verifica se o salário atingiu o teto máximo, retornando o salário correspondente para o cálculo do financiamento
			salarioBase = getSalarioMaximo(matricula);
				 
			//Cálculo do financiamento do titular
			calculaFinanciamentoTitular(titular);
			
			log.append("Matricula: " + matricula.getDescricao());
			log.append(System.getProperty("line.separator"));
			log.append(System.getProperty("line.separator"));
			log.append("Qt Matriculas: " + titular.getMatriculas().size() + " Qt Dependentes: " + titular.getDependentes().size());
			log.append(System.getProperty("line.separator"));
			log.append(System.getProperty("line.separator"));
			log.append("Salário Base: " + (new DecimalFormat("######0.00")).format(salarioBase) + " Salário Titular: " +(new DecimalFormat("######0.00")).format(matricula.getSalario()));
			log.append(System.getProperty("line.separator"));
			
			calculaFinanciamentoDependentes(titular);
			valorCoparticipacao = BigDecimal.ZERO;
			
			if(valorFinanciamento.compareTo(BigDecimal.ZERO) > 0){
				quantConsignacoesMatriculasNaoZeradas++;
				valorTotalConsignacoes = valorTotalConsignacoes.add(valorFinanciamento);
				consignacao.setValorFinanciamento(consignacao.getValorFinanciamento().add(valorFinanciamento));
				valorTotalFinanciamento = valorTotalFinanciamento.add(valorFinanciamentoTitular).add(valorFinanciamentoDependentes);
				
				if(matricula.equals(matriculaMaiorSalario)){
					guias = coParticipacaoPorTitular.get(titular.getPessoaFisica().getCpf()).getGuias();
					valorCoparticipacao = coParticipacaoPorTitular.get(titular.getPessoaFisica().getCpf()).getValorCoParticipacao();
					valorTotalCoparticipacao = valorTotalCoparticipacao.add(valorCoparticipacao);
					
					log.append(" Qt de Guias: " + guias.size());
					log.append(System.getProperty("line.separator"));
					
					for (GuiaSimplesTuning guia : guias) {
						log.append("Guia: " + guia.getAutorizacao()+ " Valor Coparticipacao: " + (new DecimalFormat("######0.00")).format(guia.getValorCoParticipacao())+" Situacao: " + guia.getSituacao().getDescricao() + " Data atendimento: " + Utils.format(guia.getDataAtendimento()));
						log.append(System.getProperty("line.separator"));
					}
				}
				
				ConsignacaoMatriculaInterface consignacaoMatriculaGerada = gerarConta(matricula, valorFinanciamento,valorCoparticipacao,aliquotaTitularTotal, competencia, dataPagamento);
				consignacaoMatriculaGerada.setColecaoContas(colecaoContas);
				
				//Agrupa as contas por empresa
				if(!consignacoesPorEmpresa.containsKey(consignacaoMatriculaGerada.getMatricula().getEmpresa())){
					List<ConsignacaoMatriculaInterface> contas = new ArrayList<ConsignacaoMatriculaInterface>();
					contas.add(consignacaoMatriculaGerada);
					consignacoesPorEmpresa.put(consignacaoMatriculaGerada.getMatricula().getEmpresa(), contas);
				}else{
					consignacoesPorEmpresa.get(consignacaoMatriculaGerada.getMatricula().getEmpresa()).add(consignacaoMatriculaGerada);
				}
			}

			log.append(System.getProperty("line.separator"));
			log.append("Titular: " + titular.getPessoaFisica().getCpf().replace(".", "").replace("-", "") + 
							" Valor financimento Titular: " + (new DecimalFormat("######0.00")).format(valorFinanciamentoTitular) +
							" Valor financimento Dependente: " + (new DecimalFormat("######0.00")).format(valorFinanciamentoDependentes)+
							" Valor total financimento Titular: " + (new DecimalFormat("######0.00")).format(valorTotalFinanciamentoTitular) +
							" Valor financimento Total da Matricula: " + (new DecimalFormat("######0.00")).format(valorFinanciamento) + 
							" Valor Coparticipacao: " + (new DecimalFormat("######0.00")).format(valorCoparticipacao));
			log.append(System.getProperty("line.separator"));
			
		}
		
		ImplDAO.save(consignacao);
	}

	private void calculaFinanciamentoDependentes(
			TitularConsignacaoTuning titular) {
		int ordem = 1;
		for (DependenteConsignacaoTuning dep : titular.getDependentes()) {
			if(!dep.getSituacao().getDescricao().equals(SituacaoEnum.CANCELADO.descricao()) && !dep.getSituacao().getDescricao().equals(SituacaoEnum.SUSPENSO.descricao())) {
				BigDecimal[] aliquotaValorDep = dep.getValorFinanciamentoDependente(salarioBase);
				aliquotaTitularTotal = aliquotaTitularTotal.add(aliquotaValorDep[0]);
				valorFinanciamentoDependentes = valorFinanciamentoDependentes.add(aliquotaValorDep[1]);
				valorTotalFinanciamentoDependentes = valorTotalFinanciamentoDependentes.add(aliquotaValorDep[1]);
				valorFinanciamento = valorFinanciamento.add(aliquotaValorDep[1]);
				log.append("Nome: "+ dep.getPessoaFisica().getNome()+" Ordem: " + dep.getOrdem() + " Idade: " + dep.getPessoaFisica().getIdade() + " Financiamento: " + aliquotaValorDep[1] + " Porcentagem financimento: " + aliquotaValorDep[0].setScale(1,BigDecimal.ROUND_HALF_UP));
				log.append(System.getProperty("line.separator"));
				ordem++;
			}
		}
	}

	private void calculaFinanciamentoTitular(TitularConsignacaoTuning titular) {		
		if(valorTotalFinanciamentoTitular.compareTo(limiteFinanciamentoTitular) < 0){
			BigDecimal[] aliquotaValorTit = titular.getValorFinanciamento(salarioBase);
			aliquotaTitularTotal = aliquotaTitularTotal.add(aliquotaValorTit[0]);
			valorFinanciamentoTitular = aliquotaValorTit[1];
			valorTotalFinanciamentoTitular = valorTotalFinanciamentoTitular.add(valorFinanciamentoTitular);
			
			if(valorTotalFinanciamentoTitular.compareTo(limiteFinanciamentoTitular) >= 0){
				valorFinanciamento = valorFinanciamentoTitular;
				valorTotalFinanciamentoTitular = limiteFinanciamentoTitular;
			}
			else
				valorFinanciamento = valorFinanciamento.add(valorFinanciamentoTitular);
		}
	}

	private void zeraValores() {
		valorTotalFinanciamentoDependentes = BigDecimal.ZERO;
		valorTotalFinanciamentoTitular = BigDecimal.ZERO;
		salarioBase = BigDecimal.ZERO;
		salarioTotal = BigDecimal.ZERO;
		salarioTotal.setScale(2, BigDecimal.ROUND_HALF_UP);
		valorFinanciamento = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		valorFinanciamentoTitular = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		aliquotaTitularTotal = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		valorFinanciamentoDependentes = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		salario_base_limite = PainelDeControle.getPainel().getSalarioBaseLimite();
		aliquota_financiamento = PainelDeControle.getPainel().getAliquotaDeFinanciamento();
		limiteFinanciamentoTitular = salario_base_limite.multiply(aliquota_financiamento.divide(new BigDecimal(100))).setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	private void inicializaConsignacao(TitularConsignacaoTuning titular, Date competencia, Date dataPagamento) {
		
		consignacao = new ConsignacaoSeguradoTuning();
		consignacao.setCompetencia(competencia);
		consignacao.setStatusConsignacao('A');
		consignacao.setTitular(titular);
		consignacao.setValorFinanciamento(BigDecimal.ZERO);
		consignacao.setValorCoparticipacao(consignacao.getValorCoparticipacao().add(coParticipacaoPorTitular.get(titular.getPessoaFisica().getCpf()).getValorCoParticipacao()));
		consignacao.addGuias(coParticipacaoPorTitular.get(titular.getPessoaFisica().getCpf()).getGuias());
		consignacao.setValorPrevidencia(BigDecimal.ZERO);
		colecaoContas = new ComponenteColecaoContas();
		consignacao.setColecaoContas(colecaoContas);
		consignacao.setDataDoCredito(dataPagamento);
		consignacoesSegurado.add(consignacao);
	}
	
	private ConsignacaoMatriculaInterface gerarConta(AbstractMatricula matricula, BigDecimal valorFinanciamento,BigDecimal valorCoparticipacao,BigDecimal aliquota, Date competencia, Date dataPagamento) throws Exception {
		ConsignacaoMatriculaTuning consignacao = new ConsignacaoMatriculaTuning();
		consignacao.setCompetencia(competencia);
		consignacao.setStatusConsignacao('A');
		consignacao.setMatricula(matricula);
		consignacao.setDataDoCredito(dataPagamento);
		consignacao.setDataPagamento(dataPagamento);
		consignacao.setMatriculaNoEstado(matricula.getDescricao());
		consignacao.setValorFinanciamento(valorFinanciamento);
		consignacao.setValorCoparticipacao(valorCoparticipacao);
		consignacao.setPercentualAliquota(aliquota);
		if(isTitularEfetivoDaURB) {
			consignacao.setCobrancaFuncionarioURBEfetivo(true);
		}
		
		HibernateUtil.currentSession().save(consignacao);
		consignacoesMatricula.add(consignacao);
		return consignacao;
	}

	/**
	 * @param limiteBase
	 * @param matricula
	 * @return
	 */
	public BigDecimal getSalarioMaximo(AbstractMatricula matricula) {
		BigDecimal salarioBase;
		if(salarioTotal.compareTo(salario_base_limite) < 0 && matricula.getSalario() != null){
			if(salarioTotal.add(matricula.getSalario()).compareTo(salario_base_limite) < 0){
				salarioTotal = salarioTotal.add(matricula.getSalario());
				salarioBase = matricula.getSalario();
			}
			else {
				salarioBase = salario_base_limite.add(salarioTotal.negate());
				salarioTotal = salario_base_limite;
			}
		}else{
			salarioBase = BigDecimal.ZERO;
		}
		return salarioBase;
	}
	
	private BigDecimal getValorCoparticipacao(Set<GuiaSimplesTuning> guias){
		BigDecimal valorCoParticipacao = BigDecimal.ZERO;
		for (GuiaSimplesTuning guiaSimplesTuning : guias) {
			HibernateUtil.currentSession().evict(guiaSimplesTuning);
			valorCoParticipacao = valorCoParticipacao.add(guiaSimplesTuning.getValorCoParticipacao());
		}
		return valorCoParticipacao;
	}
	
	private void initMapGuias(final List<TitularConsignacaoTuning> titulares, Date competencia){
		
		List<GuiaSimplesTuning> guias = buscarGuiasConsignacao(competencia);
		
		Set<GuiaSimplesTuning> guiasValue = null;
		Map<Long, Set<GuiaSimplesTuning>> guiasPorSegurado = new HashMap<Long, Set<GuiaSimplesTuning>>();
		
		for (GuiaSimplesTuning guia : guias) {
			if(guiasPorSegurado.keySet().contains(guia.getSegurado().getIdSegurado())){ 
				guiasValue = guiasPorSegurado.get(guia.getSegurado().getIdSegurado());
				guiasValue.add(guia);
			}else{
				guiasValue = new HashSet<GuiaSimplesTuning>();
				guiasValue.add(guia);
				guiasPorSegurado.put(guia.getSegurado().getIdSegurado(), guiasValue);
			}
		}
		
		BigDecimal coParticipacaoTitular;
		Set<GuiaSimplesTuning> guiasCoparticipacao;
		Set<GuiaSimplesTuning> guiasTemp;
		for (TitularConsignacaoTuning titular : titulares) {
			guiasCoparticipacao = new HashSet<GuiaSimplesTuning>();
			coParticipacaoTitular = BigDecimal.ZERO;
			if(guiasPorSegurado.keySet().contains(titular.getIdSegurado())){
				guiasCoparticipacao.addAll(guiasPorSegurado.get(titular.getIdSegurado()));
				coParticipacaoTitular = getSomatorioCoParticipacao(guiasPorSegurado.get(titular.getIdSegurado()));
				for (DependenteConsignacaoTuning dependente : titular.getDependentes()) {
					guiasTemp = guiasPorSegurado.get(dependente.getIdSegurado());
					if(guiasTemp != null && !guiasTemp.isEmpty())
						guiasCoparticipacao.addAll(guiasTemp);
					coParticipacaoTitular = coParticipacaoTitular.add(getSomatorioCoParticipacao(guiasPorSegurado.get(dependente.getIdSegurado())));
				}
			}else{
				for (DependenteConsignacaoTuning dependente : titular.getDependentes()) {
					guiasTemp = guiasPorSegurado.get(dependente.getIdSegurado());
					if(guiasTemp != null && !guiasTemp.isEmpty())
						guiasCoparticipacao.addAll(guiasTemp);
					coParticipacaoTitular = coParticipacaoTitular.add(getSomatorioCoParticipacao(guiasPorSegurado.get(dependente.getIdSegurado())));
				}
			}
			coParticipacaoPorTitular.put(titular.getPessoaFisica().getCpf(), new GuiasValorCoParticipacao(guiasCoparticipacao, coParticipacaoTitular));
		}
	}

	/**
	 * @param competencia
	 * @return Guias sem consignação do mês anterior à competencia.
	 * As guias as guias confirmadas, pagas e faturadas, sem fluxo financeiro, portanto sem consignação.
	 */
	private List<GuiaSimplesTuning> buscarGuiasConsignacao(Date competencia) {
		String[] situacoes = {SituacaoEnum.CONFIRMADO.descricao(),SituacaoEnum.PAGO.descricao(),SituacaoEnum.FATURADA.descricao()};
		
		Date competenciaAjustada = ajustarCompetencia(competencia);
		Calendar primeiroDiaDoMes = Calendar.getInstance();
		Calendar ultimoDiaDoMes = Calendar.getInstance();
		primeiroDiaDoMes.setTime(competenciaAjustada);
		ultimoDiaDoMes.setTime(competenciaAjustada);
		ultimoDiaDoMes.set(Calendar.DAY_OF_MONTH, primeiroDiaDoMes.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		
		return (List<GuiaSimplesTuning>) HibernateUtil.currentSession().createCriteria("GuiaSimplesTuning")
		.add(Restrictions.in("situacao.descricao", situacoes))
		.add(Restrictions.between("dataAtendimento", primeiroDiaDoMes.getTime(),ultimoDiaDoMes.getTime()))
		.add(Restrictions.isNull("fluxoFinanceiro"))
		.addOrder(Order.asc("segurado"))
		.list();
	}

	private BigDecimal getSomatorioCoParticipacao(Set<GuiaSimplesTuning> guiasSegurado) {
		if(guiasSegurado == null){
			return BigDecimal.ZERO;
		}
		
		BigDecimal coParticipacaoBeneficiario = BigDecimal.ZERO;
		for (GuiaSimplesTuning guiaSimplesTuning : guiasSegurado) {
			if(coParticipacaoBeneficiario.compareTo(Constantes.TETO_COPARTICIPACAO) != 0){
				if(coParticipacaoBeneficiario.add(guiaSimplesTuning.getValorCoParticipacao()).compareTo(Constantes.TETO_COPARTICIPACAO) < 0){
					coParticipacaoBeneficiario = coParticipacaoBeneficiario.add(guiaSimplesTuning.getValorCoParticipacao());
				} else{
					coParticipacaoBeneficiario = Constantes.TETO_COPARTICIPACAO;
				}
			}
		}
		
		return coParticipacaoBeneficiario;
	}
	
	private List<TitularConsignacaoTuning> buscarTitulares() {
		String[] situacoes = {SituacaoEnum.ATIVO.descricao(),SituacaoEnum.SUSPENSO.descricao()};
		Criteria criteria = HibernateUtil.currentSession().createCriteria(TitularConsignacaoTuning.class);
		criteria.add(Expression.in("situacao.descricao", situacoes));
		return criteria.list();
	}
	
	private List<TitularConsignacaoTuning> buscarTitulares(Empresa empresa) {
		String[] situacoes = {SituacaoEnum.ATIVO.descricao(),SituacaoEnum.SUSPENSO.descricao()};
		Set<TitularConsignacaoTuning> titular = new HashSet<TitularConsignacaoTuning>();
		Criteria criteria = HibernateUtil.currentSession().createCriteria(TitularConsignacaoTuning.class);
		criteria.createAlias("matriculas", "mats");
		criteria.add(Expression.eq("mats.empresa", empresa));
		criteria.add(Expression.in("situacao.descricao",situacoes ));
		titular.addAll(criteria.list());
		
		return criteria.list();
	}
	
	/**
	 * @param competencia
	 * @return um List contendo os arquivos de envio de consignação gerados.
	 * @throws Exception
	 */
	public List<ArquivoDeEnvioConsignacao> gerarArquivosPorEmpresa(Date competencia) throws Exception {
		List<ArquivoDeEnvioConsignacao> arquivosGerados = new ArrayList<ArquivoDeEnvioConsignacao>();
		
		for(Empresa empresa : consignacoesPorEmpresa.keySet()){
			ArquivoDeEnvioConsignacao arquivoEmpresa = new ArquivoDeEnvioConsignacao();
			arquivoEmpresa.setEmpresa(empresa);
			arquivoEmpresa.setCompetencia(competencia);
			arquivoEmpresa.setTipoDeArquivo(ArquivoInterface.REMESSA);
			arquivoEmpresa.setStatusArquivo(ArquivoInterface.ARQUIVO_ENVIADO);
			
			for(ConsignacaoMatriculaInterface contaEmpresa : consignacoesPorEmpresa.get(empresa)){
				arquivoEmpresa.populate(contaEmpresa, null);
			}
			
			ImplDAO.save(arquivoEmpresa);
			if(!arquivoEmpresa.getConsignacoes().isEmpty()) {
				arquivoEmpresa.criarArquivo();
				arquivosGerados.add(arquivoEmpresa);
			}
		}
		return arquivosGerados;
	}
	
	public ArquivoDeEnvioConsignacao gerarArquivosPorEmpresa(Date competencia, Empresa empresa) throws Exception {
		
		log.append(System.getProperty("line.separator"));
		
		ArquivoDeEnvioConsignacao arquivoEmpresa = new ArquivoDeEnvioConsignacao();
		arquivoEmpresa.setEmpresa(empresa);
		arquivoEmpresa.setCompetencia(competencia);
		arquivoEmpresa.setTipoDeArquivo(ArquivoInterface.REMESSA);
		arquivoEmpresa.setStatusArquivo(ArquivoInterface.ARQUIVO_ENVIADO);
		
		log.append("Consignacoes: " + consignacoesPorEmpresa);
		log.append(System.getProperty("line.separator"));
		
		log.append("Empresa: "+ empresa);
		log.append(System.getProperty("line.separator"));
		
		for(ConsignacaoMatriculaInterface contaEmpresa : consignacoesPorEmpresa.get(empresa)){
			arquivoEmpresa.populate(contaEmpresa, null);
		}
		ImplDAO.save(arquivoEmpresa);
		if(!arquivoEmpresa.getConsignacoes().isEmpty())
			arquivoEmpresa.criarArquivo();
		
		log.append(System.getProperty("line.separator"));
		
		return arquivoEmpresa;
	}
	
	private static Date ajustarCompetencia(Date competencia) {
		GregorianCalendar calendario = new GregorianCalendar();
		calendario.setTime(competencia);
		calendario.set(Calendar.MONTH, calendario.get(Calendar.MONTH) - 1);
		calendario.set(Calendar.DAY_OF_MONTH, calendario.getActualMinimum(Calendar.DAY_OF_MONTH));
		return calendario.getTime();
	}
	
	public void setConsignacoesPorEmpresa(
			Map<Empresa, List<ConsignacaoMatriculaInterface>> consignacoesPorEmpresa) {
		this.consignacoesPorEmpresa = consignacoesPorEmpresa;
	}
	
	public void liberarMemoria(){
		consignacoesSegurado = null;
		consignacoesMatricula = null;
		coParticipacaoPorTitular = null;
	}

	public List<String> getCpfsSeguradosURBEfetivos() {
		return cpfsSeguradosURBEfetivos;
	}

	public void setCpfsSeguradosURBEfetivos(List<String> cpfsSeguradosURBEfetivos) {
		this.cpfsSeguradosURBEfetivos = cpfsSeguradosURBEfetivos;
	}
	
	private boolean isTitularEfetivoDaURB(TitularConsignacaoTuning tit) {		
		return (cpfsSeguradosURBEfetivos.contains(tit.getPessoaFisica().getCpf()));
	}
	
	public List<byte[]> getLogs(){
		List<byte[]> logs = new ArrayList<byte[]>();
		logs.add(log.toString().getBytes());
		return logs;
	}

	public Map<String, GuiasValorCoParticipacao> getCoParticipacaoPorTitular() {
		return coParticipacaoPorTitular;
	}
	
}

@SuppressWarnings({"rawtypes"})
class GuiasValorCoParticipacao {
	private Set<GuiaSimplesTuning> guias;
	private BigDecimal valorCoParticipacao;
	
	public GuiasValorCoParticipacao() {
	}
	
	public GuiasValorCoParticipacao(Set<GuiaSimplesTuning> guias, BigDecimal valorCoparticipacao) {
		this.guias = guias;
		this.valorCoParticipacao = valorCoparticipacao;
	}
	
	public BigDecimal getValorCoParticipacao() {
		return valorCoParticipacao;
	}
	
	public Set<GuiaSimplesTuning> getGuias() {
		return guias;
	}
	
}