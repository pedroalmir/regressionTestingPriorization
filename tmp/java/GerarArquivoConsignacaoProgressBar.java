package br.com.infowaypi.ecare.financeiro.consignacao;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;
import org.hibernate.Transaction;

import br.com.infowaypi.ecare.services.financeiro.consignacao.FlowGerarConsignacao;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.utils.Utils;
import br.com.infowaypi.progressBar.ProgressiveProcess;

public class GerarArquivoConsignacaoProgressBar extends ProgressiveProcess {
	
	private static FlowGerarConsignacao service;
	private Date competencia;
	
	private long progressMax;
	private long progresso;
	
	private String progessTitulo;
	private String progessStatus;
	
	private static Exception excecaoDoFluxo;
	
	public GerarArquivoConsignacaoProgressBar() {
		service = new FlowGerarConsignacao();
		excecaoDoFluxo = null;
		progressMax = 100;
		progresso = 0;
		progessTitulo = "";
		progessStatus = "";
	}

	@Override
	public long getProgress() {
		return progresso;
	}

	@Override
	public String getProgressFinishedTitle() {
		return "Concluído";
	}

	@Override
	public long getProgressMax() {
		return progressMax;
	}

	@Override
	public long getProgressMin() {
		return -1;
	}

	@Override
	public String getProgressStatusText() {
		return progessStatus;
	}

	@Override
	public String getProgressTitle() {
		return progessTitulo;
	}

	@Override
	public void run() {
		
		Session session = HibernateUtil.currentSession();
		Transaction t = session.beginTransaction();
		try {
			service.gerarConsignacao(competencia, this);
			session.flush();
			session.clear();
			t.commit();
		} catch (Exception e) {
			excecaoDoFluxo = e;
			e.printStackTrace();
			t.rollback();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setParameters(HttpServletRequest request, HttpSession session) {
		competencia = Utils.parse(request.getParameter("competencia"), "MM/yyyy");
	}

	public void setProgressMax(long progressoMaximo) {
		this.progressMax = progressoMaximo;
	}

	public String getProgessTitulo() {
		return progessTitulo;
	}

	public void setProgessTitulo(String progessTitulo) {
		this.progessTitulo = progessTitulo;
	}

	public String getProgessStatus() {
		return progessStatus;
	}

	public void setProgessStatus(String progessStatus) {
		this.progessStatus = progessStatus;
	}

	public static Exception getExcecaoDoFluxo() {
		return excecaoDoFluxo;
	}

	public static void setExcecaoDoFluxo(Exception excecaoDoFluxo) {
		GerarArquivoConsignacaoProgressBar.excecaoDoFluxo = excecaoDoFluxo;
	}

	public static FlowGerarConsignacao getService() {
		return service;
	}

	@Override
	public boolean interrupt() {
		return false;
	}

	@Override
	public void onCancelled() {
	}

	@Override
	public void onFinished() {
	}

	@Override
	public void onStart() {
	}

	@Override
	public void onUpdate() {
	}

	@Override
	protected void setProgress(long progress) {
		progresso = progress;
	}
	
	public void incrementarProgresso(){
		progresso++;
	}


}
