/**
 * 
 */
package br.com.infowaypi.ecare.services.internacao;

import java.util.Collection;

import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.enums.MotivoEnumSR;
import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecare.utils.ProfissionalUtils;
import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacaoEletiva;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.atendimentos.validators.InternacaoPossuiPacoteSemProcedimentoValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.InternacaoPossuiProcedimentoSemPacoteValidator;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.opme.ItemOpme;
import br.com.infowaypi.ecarebc.procedimentos.CID;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.service.internacao.MarcacaoInternacaoEletivaService;
import br.com.infowaypi.ecarebc.utils.ProcedimentoUtils;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/* if_not[NAO_OBRIGATORIEDADE_DE_INFORMAR_PROFISSIONAL_NO_ATENDIMENTO]
import br.com.infowaypi.ecare.utils.ProfissionalUtils;
end[NAO_OBRIGATORIEDADE_DE_INFORMAR_PROFISSIONAL_NO_ATENDIMENTO] */

/*if[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO]
import br.com.infowaypi.ecare.services.ValidacaoAtendimentoSegmentacaoAssistencial;
import br.com.infowaypi.ecarebc.produto.SegmentacaoAssistencialEnum;
/* end[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */
 
/**
 * @author Marcus bOolean
 *
 */
public class SolicitarInternacaoEletivaCentral extends MarcacaoInternacaoEletivaService<Segurado>{
	
	public ResumoSegurados buscarSegurado(String cpfDoTitular, String numeroDoCartao) throws Exception {
		/*if[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO]
		ResumoSegurados resumo = ValidacaoAtendimentoSegmentacaoAssistencial.buscar(cpfDoTitular, numeroDoCartao, Segurado.class, false, 
				SegmentacaoAssistencialEnum.HOSPITALAR_ELETIVO.getSegmentacaoAssistencial(),
				SegmentacaoAssistencialEnum.OBSTETRICIA_ELETIVO.getSegmentacaoAssistencial());
		else[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */
		ResumoSegurados resumo = BuscarSegurados.buscar(cpfDoTitular, numeroDoCartao, Segurado.class);
		/* end[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */
		return resumo;
	}
	
	public GuiaInternacaoEletiva criarGuiaEletiva(Segurado segurado,
			Integer tipoTratamento, Prestador prestador, UsuarioInterface usuario,Profissional solicitanteCRM, Profissional solicitanteNOME, 
			Especialidade especialidade, Collection<CID> cids, String justificativa) throws Exception {
		
		Assert.isNotNull(segurado, MensagemErroEnum.SEGURADO_NAO_INFORMADO.getMessage());
		
		Profissional profissional = getProfissional(solicitanteCRM, solicitanteNOME);
		
		/* if[VALIDACAO_PROFISSIONAL_REDE_CREDENCIADA]*/
		if(profissional != null){
			Assert.isTrue(profissional.isCredenciado(), "O Médico Solicitante deve ser um médico da rede credenciada.");
		}
		/* end[VALIDACAO_PROFISSIONAL_REDE_CREDENCIADA]*/
		
		GuiaInternacaoEletiva guia  = super.criarGuiaEletiva(segurado, tipoTratamento, null, prestador, usuario, profissional, especialidade, cids, justificativa); 
//		guia.addItensDiaria(itensDiarias);
		
		if(segurado.getDataAdesao() == null)
			throw new RuntimeException(MensagemErroEnumSR.SEGURADO_SEM_DATA_DE_ADESAO.getMessage());
		
		if(segurado.getSituacao().getDescricao().equals(SituacaoEnum.SUSPENSO)
				&& segurado.getSituacao().getMotivo().equals("Não recadastrado")) {
				throw new ValidateException(MensagemErroEnumSR.SEGURADO_NAO_RECADASTRADO.getMessage());
		}
		
		guia.getSituacao().setDescricao(SituacaoEnum.SOLICITADO_INTERNACAO.descricao());
		guia.getSituacao().setMotivo(MotivoEnumSR.SOLICITADO_PELA_CENTRAL.getMessage());
		guia.tocarObjetos();
		return guia;
		
	}
	
	public GuiaInternacaoEletiva criarGuiaEletiva(Segurado segurado, Integer tipoTratamento,  UsuarioInterface usuario,
			Profissional solicitanteCRM, Profissional solicitanteNOME, Especialidade especialidade,
			Collection<CID> cids, String justificativa) throws Exception {
		
		Assert.isNotNull(segurado, MensagemErroEnum.SEGURADO_NAO_INFORMADO.getMessage());
		
		Profissional profissional = getProfissional(solicitanteCRM, solicitanteNOME);
		
		/* if[VALIDACAO_PROFISSIONAL_REDE_CREDENCIADA]*/
		Assert.isTrue(profissional.isCredenciado(), "O Médico Solicitante deve ser um médico da rede credenciada.");
		/* end[VALIDACAO_PROFISSIONAL_REDE_CREDENCIADA]*/
		
		GuiaInternacaoEletiva guia  = super.criarGuiaEletiva(segurado, tipoTratamento, null, null, usuario, profissional, especialidade, cids, justificativa); 
		
		if(segurado.getDataAdesao() == null)
			throw new RuntimeException(MensagemErroEnumSR.SEGURADO_SEM_DATA_DE_ADESAO.getMessage());
		
		if(segurado.getSituacao().getDescricao().equals(SituacaoEnum.SUSPENSO)
				&& segurado.getSituacao().getMotivo().equals("Não recadastrado")) {
				throw new ValidateException(MensagemErroEnumSR.SEGURADO_NAO_RECADASTRADO.getMessage());
		}
		
		guia.getSituacao().setDescricao(SituacaoEnum.SOLICITADO_INTERNACAO.descricao());
		guia.getSituacao().setMotivo(MotivoEnumSR.SOLICITADO_PELA_CENTRAL.getMessage());
		guia.tocarObjetos();
		return guia;
		
	}

	private Profissional getProfissional(Profissional solicitanteCRM, Profissional solicitanteNOME) {
		Profissional profissional = null; 
		
		/*if[NAO_OBRIGATORIEDADE_DE_INFORMAR_PROFISSIONAL_NO_ATENDIMENTO]
		profissional = ProfissionalUtils.getProfissionalSeInformado(solicitanteCRM, solicitanteNOME);
		else[NAO_OBRIGATORIEDADE_DE_INFORMAR_PROFISSIONAL_NO_ATENDIMENTO]*/
		profissional = ProfissionalUtils.getProfissionais(solicitanteCRM, solicitanteNOME);
		/*end[NAO_OBRIGATORIEDADE_DE_INFORMAR_PROFISSIONAL_NO_ATENDIMENTO]*/
		
		return profissional;
	}
	
	public GuiaInternacaoEletiva addProcedimentos(
			UsuarioInterface usuario,
			Collection<ItemDiaria> diarias,
			GuiaInternacaoEletiva guia,
			Collection<ItemPacote> pacotes) throws Exception {
		
		GuiaInternacaoEletiva guiaAuditada = super.addProcedimentosCentral(guia, usuario, diarias, guia.getProcedimentosCirurgicosDaSolicitacao(), pacotes);
		
		for (ItemOpme itemOpme : guiaAuditada.getOpmesSolicitados()) {
			Assert.isNotNull(itemOpme.getObservacaoSolicitacao(), "O motivo da solicitação deve ser informado para OPMEs.");
			itemOpme.getSituacao().setMotivo(itemOpme.getObservacaoSolicitacao());
			itemOpme.getSituacao().setUsuario(usuario);
		}
		
		ProcedimentoUtils.aplicaDescontosDaViaDeAcessoComCalculoDobrado(guia);
		
		new InternacaoPossuiPacoteSemProcedimentoValidator().execute(guiaAuditada);
		
		/* if_not[REMOVE_OBRIGATORIEDADE_DE_INFORMAR_PRESTADOR_PARA_INTERNACAO] */
		new InternacaoPossuiProcedimentoSemPacoteValidator().execute(guiaAuditada);
		/* end[REMOVE_OBRIGATORIEDADE_DE_INFORMAR_PRESTADOR_PARA_INTERNACAO] */
		
		atualizarDadosDasDiarias(guiaAuditada);
		
		guiaAuditada.recalcularValores();
		
		return guiaAuditada;
	}
	
	/**
     * Método que atualiza dados da diaria
     * @author Luciano Infoway
     * @param guia
     */
	private void atualizarDadosDasDiarias(GuiaCompleta<ProcedimentoInterface> guia) {
		for (ItemDiaria diaria : guia.getItensDiaria()) {
			diaria.setQuantidadeSolicitada(diaria.getValor().getQuantidade());
		}
	}

}
