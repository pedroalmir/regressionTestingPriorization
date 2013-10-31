package br.com.infowaypi.ecare.scheduller;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.junit.Test;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.com.infowaypi.ecare.constantes.Constantes;
import br.com.infowaypi.ecare.enums.MotivoEnumSR;
import br.com.infowaypi.ecare.segurados.Dependente;
import br.com.infowaypi.ecare.segurados.SeguradoBasico;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe que regulariza os dependentes de acordo com a idade dos segurados dependentes.
 * 
 * @author Luciano Rocha
 *
 */
public class TaskRegularizarDependente implements Job{// implements Job

	@SuppressWarnings("unchecked")
	@Test
	public void suspenderDependente(boolean commit) {
		Transaction transaction = HibernateUtil.currentSession().beginTransaction();
		
		Calendar dataEstipulada = new GregorianCalendar();
		dataEstipulada.add(Calendar.YEAR, -21);
		Calendar dataMaxima = new GregorianCalendar();
		dataMaxima.add(Calendar.YEAR, -25);
		
		List<Dependente> dependentes = HibernateUtil.currentSession().createCriteria(Dependente.class)
		.add(Expression.eq("situacao.descricao", SituacaoEnum.ATIVO.descricao()))
		.add(Expression.or(Expression.eq("tipoDeDependencia", SeguradoBasico.TIPO_FILHO_MENOR_Q_21), Expression.eq("tipoDeDependencia", SeguradoBasico.TIPO_FILHO_MENOR_25_ANOS)))
		.add(Expression.or(Expression.lt("pessoaFisica.dataNascimento", dataEstipulada.getTime()), Expression.eq("pessoaFisica.dataNascimento", dataEstipulada.getTime())))
		.add(Expression.or(Expression.gt("pessoaFisica.dataNascimento", dataMaxima.getTime()), Expression.eq("pessoaFisica.dataNascimento", dataMaxima.getTime())))
		.addOrder(Order.desc("pessoaFisica.dataNascimento"))
		.list();
		
		regularizaDependentes(dependentes);
		
		if (commit) {
			transaction.commit();
		}
	}

	/**
	 * Método criado para encapsular a regularização dos dependentes. Para que o código seja testável.
	 * 
	 * @author Luciano Rocha
	 * @param dependentes
	 * @return
	 */
	public List<Dependente> regularizaDependentes(List<Dependente> dependentes){
		
		for (Dependente dependente : dependentes) {
			boolean naoPossuiRegularizacoes = dependente.getRegularizacoes().isEmpty();
			if (naoPossuiRegularizacoes){
				suspendeIrregular(dependente);
			} else {
				boolean ultimaRegularizacaoHaMaisDe6Meses = verificaUltimaRegularizacao(dependente) <= 0;
				boolean temExatamente21Ou25Anos = verificaIdade21E25(dependente);
				if (ultimaRegularizacaoHaMaisDe6Meses || temExatamente21Ou25Anos){
					suspendeIrregular(dependente);
				}
			}
		}
		
		return dependentes;
	}
	
	/**
	 * Método que verifica se a idade do dependente é exatamente 21 ou 25 anos.
	 *@author Luciano Rocha 
	 * @param dependente
	 * @return
	 */
	public boolean verificaIdade21E25(Dependente dependente){
		Calendar dataEstipulada = new GregorianCalendar();
		dataEstipulada.add(Calendar.YEAR, -21);
		Calendar dataMaxima = new GregorianCalendar();
		dataMaxima.add(Calendar.YEAR, -25);
		
		int igualA21 = Utils.compareData(dependente.getPessoaFisica().getDataNascimento(), dataEstipulada.getTime());
		int igualA25 = Utils.compareData(dependente.getPessoaFisica().getDataNascimento(), dataMaxima.getTime());
		
		return (igualA21 == 0 || igualA25 == 0);
	}
	
	/**
	 * Método que verifica se a última regularização foi há 6 meses ou mais.
	 * @author Luciano Rocha
	 * @param dependente
	 * @return
	 */
	public int verificaUltimaRegularizacao(Dependente dependente){
		Date dataRegularizacao = dependente.getUltimaRegularizacao().getDataRegularizacao();
		
		Calendar cl = new GregorianCalendar();
		cl.setTime(dataRegularizacao);
		cl.add(Calendar.MONTH, 6);
		int i = Utils.compareData(cl.getTime(), new Date());
		
		return i;
	}
	
	private void suspendeIrregular(Dependente dependente) {

		Usuario user = ImplDAO.findById(1l, Usuario.class);

		dependente.mudarSituacao(user, Constantes.SITUACAO_SUSPENSO, MotivoEnumSR.REGULARIZACAO_DEPENDENTE.getMessage(), new Date());

		Assert.isEquals(dependente.getSituacao().getDescricao(), Constantes.SITUACAO_SUSPENSO, "Situação Atual Difere de Suspenso");
		Assert.isEquals(dependente.getSituacao().getMotivo(), MotivoEnumSR.REGULARIZACAO_DEPENDENTE.getMessage(), "Motivo difere de Regularizaçao de Dependente Estudante ");

		try {
			ImplDAO.save(dependente);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		suspenderDependente(true);
	}

}
