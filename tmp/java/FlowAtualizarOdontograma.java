package br.com.infowaypi.ecarebc.service.odonto;

import org.hibernate.FlushMode;

import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecarebc.odonto.EstruturaOdonto;
import br.com.infowaypi.ecarebc.odonto.Odontograma;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;

/*if[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO]
import br.com.infowaypi.ecare.services.ValidacaoAtendimentoSegmentacaoAssistencial;
import br.com.infowaypi.ecarebc.produto.SegmentacaoAssistencialEnum;
/* end[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */

/**
 * @author DANNYLVAN
 * Fluxo para a atualização/criação de um odontograma para um segurado.
 */
public class FlowAtualizarOdontograma {

	public ResumoSegurados buscarSegurado(String cpfDoTitular, String numeroDoCartao) throws Exception {
		/*if[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO]
		ResumoSegurados resumo = ValidacaoAtendimentoSegmentacaoAssistencial.buscar(cpfDoTitular, numeroDoCartao, Segurado.class, false, SegmentacaoAssistencialEnum.ODONTOLOGICO.getSegmentacaoAssistencial());
		else[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */
		ResumoSegurados resumo = BuscarSegurados.buscar(cpfDoTitular, numeroDoCartao, Segurado.class);
		/* end[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */
		return resumo;
	}
	
	public Segurado selecionarSegurado(Segurado segurado) throws Exception {
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
		if(segurado.getOdontogramaCompleto() == null){
			Odontograma<EstruturaOdonto> odontograma = new Odontograma<EstruturaOdonto>();
			odontograma.setBeneficiario(segurado);
			segurado.setOdontogramaCompleto(odontograma);
			odontograma.construirOdontogramaDente();
		}
		return segurado;
	}
	
	public void atualizarOdontograma(Odontograma<EstruturaOdonto> odontograma) throws Exception {
		ImplDAO.save(odontograma);
		ImplDAO.save(odontograma.getBeneficiario());
		HibernateUtil.currentSession().flush();
	}
	
	public void finalizar() throws Exception { }
	
}
