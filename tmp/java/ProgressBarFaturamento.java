package br.com.infowaypi.ecare.financeiro.faturamento;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import br.com.infowaypi.ecare.financeiro.ordenador.ProgressBarFinanceiro;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.utils.Utils;

public class ProgressBarFaturamento extends ProgressBarFinanceiro{
	
	private static ResumoFinanceiro resumoFinanceiro;
	
	public ProgressBarFaturamento() {
		super();
		resumoFinanceiro = null;
	}

	@Override
	public void run() {
	
		try {
			executeService();
		} catch (Exception e) {
			excecaoDoFluxo = e;
			e.printStackTrace();
		}
//		this.stopMockProgress();
		this.setProgressMax(100);
		this.setProgress(100);
		this.setProgressStatusText("Concluído");
		HibernateUtil.closeSession();
	}

	@Override
	protected void executeService() throws Exception {
		this.setProgressTitle("Geração faturamento");
		FaturamentoAlternativoService service = new FaturamentoAlternativoService();
		resumoFinanceiro = service.informarDados(idOrdenador, usuario, this);
	}

	@Override
	public void setParameters(HttpServletRequest request, HttpSession session) {
		this.usuario = ((Usuario) session.getAttribute("usuario"));
		try {
			if (!Utils.isStringVazia(request.getParameter("ordenador"))) {
				this.idOrdenador = Integer.parseInt(request.getParameter("ordenador"));
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setProgressTitle(String progressTitle) {
		this.progressTitle = "Geração de faturamento";
	}

	public static ResumoFinanceiro getResumoFinanceiro() {
		return resumoFinanceiro;
	}
}