package br.com.infowaypi.ecare.services.odonto;

import java.util.Collection;
import java.util.Date;

import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExameOdonto;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOdonto;
import br.com.infowaypi.ecarebc.service.odonto.SolicitacaoTratamentoOdontoService;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

/*if[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO]
import br.com.infowaypi.ecare.services.ValidacaoAtendimentoSegmentacaoAssistencial;
import br.com.infowaypi.ecarebc.produto.SegmentacaoAssistencialEnum;
/* end[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */

/**
 * Service para lançamento de tratamentos odontológicos pelo auditor
 * @author Danilo Nogueira Portela
 */
public class MarcacaoTratamentoOdontoAuditor extends SolicitacaoTratamentoOdontoService{
	
	public ResumoSegurados buscarSegurado(String cpfDoTitular, String numeroDoCartao) throws ValidateException{
		/*if[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO]
		ResumoSegurados resumo = ValidacaoAtendimentoSegmentacaoAssistencial.buscar(cpfDoTitular, numeroDoCartao, Segurado.class, false, SegmentacaoAssistencialEnum.ODONTOLOGICO.getSegmentacaoAssistencial());
		else[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */
		ResumoSegurados resumo = BuscarSegurados.buscar(cpfDoTitular, numeroDoCartao, Segurado.class);
		/* end[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */
		return resumo;
	}
	
	public <P extends ProcedimentoOdonto> GuiaExameOdonto criarGuiaLancamento(Segurado segurado, Profissional profissional, 
			Collection<P> procedimentos, UsuarioInterface usuario) throws Exception {
		return super.criarGuiaLancamento(segurado, false, Utils.format(new Date()), profissional, procedimentos, usuario);
	}
	
	public void salvarGuia(GuiaExameOdonto guia) throws Exception {
		super.salvarGuia(guia);
	}

}
 