package br.com.infowaypi.ecare.services.odonto;

import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecarebc.odonto.HistoricoOdontologico;
import br.com.infowaypi.ecarebc.odonto.Odontograma;
import br.com.infowaypi.msr.exceptions.ValidateException;

/*if[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO]
import br.com.infowaypi.ecare.services.ValidacaoAtendimentoSegmentacaoAssistencial;
import br.com.infowaypi.ecarebc.produto.SegmentacaoAssistencialEnum;
/* end[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */

/**
 * Classe que retorna o histórico de utilização odontológica do segurado
 * @author Danilo Nogueira Portela
 */
public class GerarHistoricoOdontologico {

	public ResumoSegurados buscarSegurados(String cpf, String numeroCartao) throws ValidateException{
		/*if[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO]
		ResumoSegurados resumo = ValidacaoAtendimentoSegmentacaoAssistencial.buscar(cpf, numeroCartao, Segurado.class, false, SegmentacaoAssistencialEnum.ODONTOLOGICO.getSegmentacaoAssistencial());
		else[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */
		ResumoSegurados resumo = BuscarSegurados.buscar(cpf, numeroCartao, Segurado.class);
		/* end[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */
		return resumo;
	}
	
	public HistoricoOdontologico gerarHistorico(Segurado segurado){
		Odontograma od = new Odontograma(segurado);
		segurado.setOdontograma(od);
		HistoricoOdontologico historico = new HistoricoOdontologico(od);
		return historico;
	}
	
	public void finalizar(HistoricoOdontologico historico){
		
	}
}
