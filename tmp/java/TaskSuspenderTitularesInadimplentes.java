package br.com.infowaypi.ecare.scheduller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.com.infowaypi.ecare.enums.MotivoEnumSR;
import br.com.infowaypi.ecare.financeiro.Cobranca;
import br.com.infowaypi.ecare.segurados.DependenteSR;
import br.com.infowaypi.ecare.segurados.DependenteSuplementar;
import br.com.infowaypi.ecare.segurados.TitularFinanceiroSR;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.conta.BoletoConfigurator;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Descrição da classe
 *	Classe que suspende titulares inadimplentes bem como seus dependentes. Titular inadimplente é 
 * 	aquele que não pagou sua cobranca até um período dado pelo sistema.
 * 
 * Exemplo de uso:
 * <pre>
 *    @see RunTasks.java
 * </pre>
 *
 * Limitações: Não suspende dependentes suplementares.
 *
 * @author eduardo
 * @version 1.0
 * @see Job.java, TestTaskSuspenderTitularesInadimplentes.java, RunTasks.java
 */

public class TaskSuspenderTitularesInadimplentes implements Job{

	static List<TitularFinanceiroSR> seguradosComCobranca;
	//campo que possibilida que executemos essa task via main
	public Date hoje;
	
	/**
	 * É a data de vencimento da última remessa gerada.
	 */
	public Date dataVencimentoDaRemessa;
	
	public TaskSuspenderTitularesInadimplentes(Date hoje) {
		this.hoje = hoje;
		dataVencimentoDaRemessa = capturaVencimentoDaRemessa();
	}
	
	public TaskSuspenderTitularesInadimplentes() {
		this.hoje = new Date();
		dataVencimentoDaRemessa = capturaVencimentoDaRemessa();
	}

	@Override	
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Session sessao = HibernateUtil.currentSession();
		Transaction transacao = sessao.beginTransaction();
		
		try{
			System.out.println("Task PeriodoParaSuspenderTitular executando...");
			
			if (isDiaDeExecutar()) {
				this.suspenderInadimplentes();
			}

			transacao.commit();
			System.out.println("Task PeriodoParaSuspenderTitular finalizou.");

		} catch (Exception e) {
			transacao.rollback();
			e.printStackTrace();
			throw new JobExecutionException(e.getMessage());
		}

	}

	/**
	 * @throws Exception
	 */
	protected void suspenderInadimplentes() throws Exception {
		this.suspenderTitularesComBoletosForaDoPrazo();
		this.suspenderBeneficariosSemBoletos();
	}

	private void suspenderBeneficariosSemBoletos() throws Exception {
		List<TitularFinanceiroSR> seguradosParaSuspender 	= new LinkedList<TitularFinanceiroSR>();
		List<TitularFinanceiroSR> titulares 				= this.getSeguradosQuePagamPorBoletoQueNaoPossuemBoleto();
		List<TitularFinanceiroSR> dependentesSuplementares 	= this.getDependetesSuplementaresQuePagamPorBoletoENaoPossuemBoleto();
		
		seguradosParaSuspender.addAll(titulares);
		seguradosParaSuspender.addAll(dependentesSuplementares);
		
		for (TitularFinanceiroSR titularFinanceiroSR : seguradosParaSuspender) {
			this.suspenderTitularEDependentes(titularFinanceiroSR);
		}
	}

	private List<TitularFinanceiroSR> getDependetesSuplementaresQuePagamPorBoletoENaoPossuemBoleto() {
		List<TitularFinanceiroSR> depdentesSuplementares = getDependentesSuplementares();
		
		depdentesSuplementares.removeAll(getSeguradosQueTemCobranca());
		
		return depdentesSuplementares;
	}

	private void suspenderTitularesComBoletosForaDoPrazo() throws Exception {
		List<Cobranca> cobrancasParaCancelamentoDeTitular = this.getCobrancasForaDoPrazo();

		for (Cobranca cobranca : cobrancasParaCancelamentoDeTitular) {
			TitularFinanceiroSR titular = cobranca.getTitular();
			if (titular.isSituacaoAtual(SituacaoEnum.ATIVO.descricao())) {
				this.suspenderTitularEDependentes(titular);
				save(cobranca);
			}
		}
	}

	/**
	 * @param cobranca
	 * @throws Exception
	 */
	protected void save(Cobranca cobranca) throws Exception {
		ImplDAO.save(cobranca);
	}

	/**
	 * @param vencimento
	 * @return Cobrancas abertas (Vencidas e não pagas)
	 */
	@SuppressWarnings("unchecked")
	protected List<Cobranca> getCobrancasForaDoPrazo() {
		Date ultimaCompetencia = Utils.gerarCompetencia(new Date());

		List<Cobranca> cobrancas = HibernateUtil.currentSession()
												.createCriteria(Cobranca.class)
												.add(Expression.eq("situacao.descricao", SituacaoEnum.ABERTO.descricao()))
												.add(Expression.le("dataVencimento", dataVencimentoDaRemessa))
												.add(Expression.eq("competencia", ultimaCompetencia))
												.list();

		return cobrancas;
	}

	/**
	 * @return Retorna todos os segurados que pagam por boleto
	 */
	@SuppressWarnings("unchecked")
	public static List<TitularFinanceiroSR> getSeguradosQuePagamPorBoleto(){

		final int MATRICULA_BOLETO = 3;
		List<TitularFinanceiroSR> titulares = HibernateUtil.currentSession()
															.createCriteria(TitularFinanceiroSR.class)
															.createAlias("matriculas", "matricula")
															.add(Expression.eq("matricula.tipoPagamento", MATRICULA_BOLETO))
															.add(Expression.eq("matricula.ativa", true))
															.add(Expression.eq("situacao.descricao", SituacaoEnum.ATIVO.descricao()))
															.add(Expression.isNotEmpty("cobrancas"))
															.list();
		
		return titulares;
	}

	/**
	 * @return Retorna todos os dependentes suplementares ativos do sistema
	 */
	@SuppressWarnings("unchecked")
	public static List<TitularFinanceiroSR> getDependentesSuplementares(){

		List<TitularFinanceiroSR> titulares = HibernateUtil.currentSession()
															.createCriteria(DependenteSuplementar.class)
															.add(Expression.eq("situacao.descricao", SituacaoEnum.ATIVO.descricao()))
															.add(Expression.isNotEmpty("cobrancas"))
															.list();
		
		return titulares;
	}
	
	@SuppressWarnings("static-access")
	public List<TitularFinanceiroSR> getSeguradosQuePagamPorBoletoQueNaoPossuemBoleto(){
		List<TitularFinanceiroSR> titulares = this.getSeguradosQuePagamPorBoleto();
		
		titulares.removeAll(this.getSeguradosQueTemCobranca());
		
		return titulares;
	}
	
	/**
	 * @return Retorna todos os segurados que tem cobranca
	 */
	@SuppressWarnings("unchecked")
	public List<TitularFinanceiroSR> getSeguradosQueTemCobranca(){

		if (seguradosComCobranca == null){
			seguradosComCobranca = HibernateUtil.currentSession()
												.createCriteria(TitularFinanceiroSR.class)
												.createAlias("cobrancas", "cobranca")
												.add(Expression.eq("cobranca.competencia", Utils.gerarCompetencia(new Date())))
												.list();
		}
		
		return seguradosComCobranca;
	}
	

	private void suspenderTitularEDependentes(TitularFinanceiroSR titularFinanceiroSR) throws Exception {
		
		titularFinanceiroSR.mudarSituacao(null, SituacaoEnum.SUSPENSO.descricao(),MotivoEnumSR.SUSPENSO_POR_INADIMPLENCIA.getMessage()+". Competência: "+ Utils.format(new Date(),"MM/yyyy"), new Date());
		System.out.println(titularFinanceiroSR.getTipoDeSegurado()+";"+titularFinanceiroSR.getNumeroDoCartao()+";"+titularFinanceiroSR.getNome()+";"+titularFinanceiroSR.getSituacao().getMotivo());
		
		for (DependenteSR dependente : titularFinanceiroSR.getDependentes()) {
			if(dependente.isSituacaoAtual(SituacaoEnum.ATIVO.descricao())){
				System.out.println(dependente.getTipoDeSegurado()+";"+dependente.getNumeroDoCartao()+";"+dependente.getPessoaFisica().getNome());
				System.out.println("Situacao dependente: "+dependente.getSituacao().getDescricao());
				dependente.mudarSituacao(null, SituacaoEnum.SUSPENSO.descricao(),MotivoEnumSR.SUSPENSO_POR_INADIMPLENCIA.getMessage()+". Competência: "+ Utils.format(new Date(),"MM/yyyy"), new Date());
			}
		}
		
		ImplDAO.save(titularFinanceiroSR);
	}
	
	private BoletoConfigurator getBoletoConfiguratorAtivo(){
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("ativo",true));
		return sa.uniqueResult(BoletoConfigurator.class);
	}
	
	/**
	 * Infere se já passou o "período para suspensão" para que a rotina possa ser executada.
	 * @return 
	 */
	public boolean isDiaDeExecutar(){
		BoletoConfigurator configurator = this.getBoletoConfiguratorAtivo();
		
		int periodoParaSuspensao = configurator.getPeriodoParaSuspenderTitular();
		
		Date dataSuspensao = Utils.incrementaDias(dataVencimentoDaRemessa, periodoParaSuspensao);
		
		System.out.println("Data Vencimento: " + dataVencimentoDaRemessa);
		System.out.println("periodoParaSuspensao: " + periodoParaSuspensao);
		boolean isDiaDeExecutar = Utils.compareData(dataSuspensao, hoje) == 0;
		
		System.out.println("isDiaDeExecutar: " + isDiaDeExecutar);
		
		if(isDiaDeExecutar){
			return true;
		}

		return false;
	}

	/**
	 * Este método retorna a data de vencimento de um remessa. Para tal, captura-se a data de vencimento de uma conta contida em uma remessa
	 * da mesma competência, pois haverá apenas uma remessa por competencia. Para garantir que esta data seja referente à última competência
	 * considerar-se-á a maior data de vencimento contida no banco.
	 * @return
	 */
	private static Date capturaVencimentoDaRemessa() {
		String sql = "select max(c.dataVencimento) from RemessaDeBoletos r, Conta c where r.competencia = c.competencia and c.situacao.descricao in ('Aberto(a)', 'Vencido(a)')  and c member of r.contas";
		
		Timestamp datavencimento =  (Timestamp) HibernateUtil.currentSession()
															.createQuery(sql)
															.setMaxResults(1)
															.uniqueResult();
		
		Date dateVencimento = null;
		if (datavencimento != null) {
			dateVencimento = new Date(datavencimento.getTime());
		}
		
		return dateVencimento;
	}

	public static void main(String[] args) throws JobExecutionException {
		Date dataDaTask = Utils.parse("01/05/2012");
		System.out.println("Data vencimento: " + capturaVencimentoDaRemessa());
		TaskSuspenderTitularesInadimplentes task = new TaskSuspenderTitularesInadimplentes(dataDaTask);
		task.execute(null);
	}
}
