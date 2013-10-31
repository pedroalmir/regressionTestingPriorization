package br.com.infowaypi.ecare.services.odonto;

import java.util.ArrayList;
import java.util.List;

import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExameOdonto;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.odonto.Odontograma;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOdonto;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.ecarebc.service.odonto.ConfirmacaoTratamentoOdontoService;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/*if[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO]
import br.com.infowaypi.ecare.services.ValidacaoAtendimentoSegmentacaoAssistencial;
import br.com.infowaypi.ecarebc.produto.SegmentacaoAssistencialEnum;
end[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */

/* if[ATUALIZAR_ODONTOGRAMA_NA_CONFIRMACAO_DE_CONSULTA]	
import org.hibernate.FlushMode;
import br.com.infowaypi.ecarebc.odonto.EstruturaOdonto;
import br.com.infowaypi.molecular.HibernateUtil;
end[ATUALIZAR_ODONTOGRAMA_NA_CONFIRMACAO_DE_CONSULTA] */

/** 
 * Service para a confirmação de um tratamento odontológico
 * @author Danilo Nogueira Portela
 */
public class ConfirmacaoTratamentoOdonto extends ConfirmacaoTratamentoOdontoService {
	
	public ResumoSegurados buscarSegurados(String cpfDoTitular,String numeroDoCartao) throws ValidateException {
		/*if[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO]
		ResumoSegurados resumo = ValidacaoAtendimentoSegmentacaoAssistencial.buscar(cpfDoTitular, numeroDoCartao, Segurado.class, false, SegmentacaoAssistencialEnum.ODONTOLOGICO.getSegmentacaoAssistencial());
		else[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */
		ResumoSegurados resumo = BuscarSegurados.buscar(cpfDoTitular, numeroDoCartao, Segurado.class);
		/* end[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */
		return resumo;
	}
	
	public ResumoGuias<GuiaExameOdonto> selecionarSeguradoConfirmacao(Prestador prestador,Segurado segurado) {
		ResumoGuias<GuiaExameOdonto> resumo = selecionarSegurado(prestador, segurado, ResumoGuias.SITUACAO_AGENDADA);
		Assert.isNotEmpty(resumo.getGuiasTratamentoOdontoPeloPrestador(),MensagemErroEnum.GUIAS_NAO_ENCONTRADAS_PARA_O_SEGURADO.getMessage("confirmadas"));
		return resumo;
	}

//	public ResumoGuias<GuiaExameOdonto> selecionarSeguradoAutorizacao(Segurado segurado) {
//		ResumoGuias<GuiaExameOdonto> resumo = selecionarSegurado(null, segurado, ResumoGuias.SITUACAO_SOLICITADA);
//		Assert.isNotEmpty(resumo.getGuiasTratamentoOdontoPeloAuditor(),MensagemErroEnum.GUIAS_NAO_ENCONTRADAS_PARA_O_SEGURADO.getMessage("autorizadas"));
//		return resumo;
//	}
	
	/**
	 * Busca as guias para o segurado selecionado com um tipo de situação
	 * @param prestador
	 * @param segurado
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected ResumoGuias<GuiaExameOdonto> selecionarSegurado(Prestador prestador, Segurado segurado, String situacao) {
		List<GuiaSimples> guias = new ArrayList<GuiaSimples>();
		guias.addAll(segurado.getGuias());
		ResumoGuias resumoGuia = new ResumoGuias<GuiaSimples>(guias, situacao, false);
		resumoGuia.setPrestador(prestador);
		
		return resumoGuia;
	}
	
	public GuiaExameOdonto selecionarGuia(GuiaExameOdonto guia) throws Exception {
		guia.tocarObjetos();
		
		for (ProcedimentoOdonto p : guia.getProcedimentos()) 
			p.tocarObjetos();
		
		//Construção do odontograma do segurado
		AbstractSegurado segurado = guia.getSegurado();
		Odontograma od= new Odontograma(segurado);
		
		segurado.setOdontograma(od);
		
		/* if[ATUALIZAR_ODONTOGRAMA_NA_CONFIRMACAO_DE_CONSULTA]	
		if(segurado.getOdontogramaCompleto() == null){
			HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
			Odontograma<EstruturaOdonto> odontograma = new Odontograma<EstruturaOdonto>();
			odontograma.setBeneficiario(segurado);
			segurado.setOdontogramaCompleto(odontograma);
			odontograma.construirOdontogramaDente();
		}
		end[ATUALIZAR_ODONTOGRAMA_NA_CONFIRMACAO_DE_CONSULTA]*/

		
		return guia;
	}
	
	public GuiaExameOdonto confirmarTratamentoOdonto(GuiaExameOdonto guiaAntiga, GuiaExameOdonto guiaNova, UsuarioInterface usuario) throws Exception {
//		for (ProcedimentoOdonto procedimento : guiaAntiga.getProcedimentos()) {
//			if(!geraCoParticipacao(procedimento)){ 
//				procedimento.setGeraCoParticipacao(true);
//				procedimento.setValorCoParticipacao(BigDecimal.ZERO); 
//			}
//		}
		return super.confirmarTratamentoOdonto(guiaAntiga, guiaNova, usuario);
	}
}
