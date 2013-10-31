package br.com.infowaypi.ecare.financeiro.ordenador;

import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import br.com.infowaypi.ecarebc.financeiro.faturamento.ordenador.Ordenador;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.utils.Utils;
import br.com.infowaypi.progressBar.ProgressiveProcess;

/**
 * @author Dannylvan
 *
 * Classe que representa a barra de progresso no fluxo de gerar ordenador
 */
public class ProgressBarFinanceiro extends ProgressiveProcess{
	private boolean continuaProcesso;
	private Date competencia;
	private Date dataRecebimento;
	
	protected Usuario usuario;
	protected Integer idOrdenador;
	
	private long progressMax;
	private long progress;
	
	protected String progressTitle;
	private String progressStatusText;
	
	private static Ordenador ordenador;
	
	protected static Exception excecaoDoFluxo;
	
	private static RuntimeException excecaoProgressBar = new RuntimeException("[ProgressBarFinanceiro] O processo deve ser interrompido.");
	
	public ProgressBarFinanceiro() {
		setOrdenador(new Ordenador());
		excecaoDoFluxo = null;
		progressMax = 100;
		progress = 0;

		progressTitle = "Inicializando";
		progressStatusText = "";
		continuaProcesso = true;
	}

	@Override
	public long getProgress() {
		return progress;
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
		return progressStatusText;
	}

	@Override
	public String getProgressTitle() {
		return progressTitle;
	}

	@Override
	public void run() {
		try {
			executeService();
		} catch (Exception e) {
			if (!e.equals(excecaoProgressBar)){
				excecaoDoFluxo = e;
			}
			e.printStackTrace();
		}
		stopMockProgress();
		setProgressMax(100);
		setProgress(100);
		setProgressStatusText("Concluído");
		HibernateUtil.closeSession();
	}

	protected void executeService() throws Exception {
		GerarOrdenadorService service = new GerarOrdenadorService();
		setOrdenador(service.gerarOrdenador(idOrdenador, competencia, dataRecebimento, usuario, this));
	}

	@Override
	public void setParameters(HttpServletRequest request, HttpSession session) {
		this.usuario = (Usuario) session.getAttribute("usuario");
		try {
			System.out.println("-----------------------------------------");
			if (!Utils.isStringVazia(request.getParameter("idOrdenador"))) {
				idOrdenador = Integer.parseInt(request.getParameter("idOrdenador"));
			}
			
			if (!Utils.isStringVazia(request.getParameter("competencia"))) {
				competencia = Utils.gerarCompetencia(request.getParameter("competencia"));
			}
			
			if (!Utils.isStringVazia(request.getParameter("dataRecebimento"))) {
				dataRecebimento = Utils.createData(request.getParameter("dataRecebimento"));
			}
			Enumeration parameterNames = request.getParameterNames();
			System.out.println("Requisição: ");
			while(parameterNames.hasMoreElements()){
				String value = parameterNames.nextElement().toString();
				System.out.println("--Parametro: "+value + " --> "+request.getParameter(value));
			}
			System.out.println("-----------------------------------------");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void setProgressMax(long progressMax) {
		this.verificarInterrupcaoDaThread();
		this.progressMax = progressMax;
	}

	public void setProgressTitle(String progressTitle) {
		this.verificarInterrupcaoDaThread();
		this.progressTitle = progressTitle; 
	}

	public void setProgressStatusText(String progressStatusText) {
		this.verificarInterrupcaoDaThread();
		this.progressStatusText = progressStatusText;
	}

	public static Ordenador getOrdenador() {
		return ordenador;
	}

	public static void setOrdenador(Ordenador ordenador) {
		ProgressBarFinanceiro.ordenador = ordenador;
	}
	
	public static Exception getExcecaoDoFluxo() {
		return excecaoDoFluxo;
	}

	@Override
	public boolean interrupt() {
		this.continuaProcesso = false;
		return true;
	}

	@Override
	public void onCancelled() {}
	
	@Override
	public void onFinished() {}

	@Override
	public void onStart() {}

	@Override
	public void onUpdate() {}

	@Override
	public void setProgress(long progress) {
		this.verificarInterrupcaoDaThread();
		this.progress = progress;
	}
	
	public void incrementarProgresso(){
		this.verificarInterrupcaoDaThread();
		progress++;
	}
	
	public void startIntervaloNaBarra(long percentagemInicio, long percentagemFim, long unidades){
		this.verificarInterrupcaoDaThread();
		this.stopMockProgress();
		long maximo = (100 * unidades) /(percentagemFim - percentagemInicio);
		long progressAtual = maximo * percentagemInicio / 100;
		
		this.setProgressMax(maximo);
		this.setProgress(progressAtual);
	}
	
	public void startMockProgress(long percentagemDeDeslocamento, long time) {
		this.verificarInterrupcaoDaThread();
		long progress = this.getProgress() + this.getProgressMax() * percentagemDeDeslocamento / 100;
		startMockProgress(null, progress, time);
	}

	public boolean isContinuaProcesso() {
		return continuaProcesso;
	}

	public void setContinuaProcesso(boolean continuaProcesso) {
		this.continuaProcesso = continuaProcesso;
	}
	
	private void verificarInterrupcaoDaThread(){
		if (!isContinuaProcesso()) {
			throw excecaoProgressBar;
		}
	}

}
