package br.com.infowaypi.ecare.financeiro.consignacao;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import br.com.infowaypi.ecare.scheduller.ProcessaRetornoConsignacaoOld;
import br.com.infowaypi.molecular.TransactionManagerHibernate;
import br.com.infowaypi.progressBar.ProgressiveProcess;

/**
 * Barra de progresso para o fluxo de processamento de retorno de consigna��es.
 * 
 * Pega o objeto 'resumo' da sess�o e chama o service passando o resumo.
 * Os dados da barra s�o atualizados dos atributos do service (Ex.: service.progressoAtual).
 * essa maneira de atualiza��o da barra facilita os testes do service, pois n�o � necess�rio mudar
 * a interface dos services para aceitar a barra de progresso, apesar de deixar os atributos 
 * do service como public.
 * 
 * @author Dannylvan
 *
 */
public class ProcessarRetornoConsignacaoProgressBar extends ProgressiveProcess {
	
	private ProcessaRetornoConsignacaoOld service;
	private ResumoRetorno resumo;
	
	public ProcessarRetornoConsignacaoProgressBar() {
		service = new ProcessaRetornoConsignacaoOld();
	}

	@Override
	public long getProgress() {
		return service.progressoAtual;
	}

	@Override
	public String getProgressFinishedTitle() {
		return "Conclu�do";
	}

	@Override
	public long getProgressMax() {
		return service.progressoMaximo;
	}

	@Override
	public long getProgressMin() {
		return -1;
	}

	@Override
	public String getProgressStatusText() {
		return service.progessStatus;
	}

	@Override
	public String getProgressTitle() {
		return service.progessTitulo;
	}

	@Override
	public void run() {
		TransactionManagerHibernate tmh = resumo.getTransaction();
		try {
			resumo.setExcecaoDoFluxo(null);
			tmh.beginTransaction();
			
			service.processarRetorno(resumo);
			setProgress(getProgressMax());
		} catch (Exception e) {
			resumo.setExcecaoDoFluxo(e);
			e.printStackTrace();
			try {
				System.out.println("RoolBack dentro da barra de progresso.");
				tmh.rollback();
				tmh.closeSession();
			} catch (Exception e1) {}
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setParameters(HttpServletRequest arg0, HttpSession arg1) {
		resumo = (ResumoRetorno) arg1.getAttribute("resumo");
	}

	@Override
	public void onFinished() { }

	@Override
	public void onStart() { }

	@Override
	public void onUpdate() { }

	@Override
	public boolean interrupt() {
		return false;
	}

	@Override
	public void onCancelled() { }

	@Override
	protected void setProgress(long progress) { }
	
}
