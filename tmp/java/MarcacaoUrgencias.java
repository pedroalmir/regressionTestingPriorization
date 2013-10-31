package br.com.infowaypi.ecare.services.urgencias;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.LockMode;

import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecare.utils.ProfissionalUtils;
import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaOdonto;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.procedimentos.CID;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.promocao.PromocaoConsulta;
import br.com.infowaypi.ecarebc.service.urgencia.MarcacaoUrgenciasService;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

/* if_not[NAO_OBRIGATORIEDADE_DE_INFORMAR_PROFISSIONAL_NO_ATENDIMENTO]
import br.com.infowaypi.ecare.utils.ProfissionalUtils;
end[NAO_OBRIGATORIEDADE_DE_INFORMAR_PROFISSIONAL_NO_ATENDIMENTO] */

/*if[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO]
import br.com.infowaypi.ecare.services.ValidacaoAtendimentoSegmentacaoAssistencial;
import br.com.infowaypi.ecarebc.produto.SegmentacaoAssistencialEnum;
end[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */

public class MarcacaoUrgencias extends MarcacaoUrgenciasService<Segurado> {

	public ResumoSegurados buscarSegurado(String cpfDoTitular, String numeroDoCartao) throws Exception {
		/*if[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO]
		ResumoSegurados resumo = ValidacaoAtendimentoSegmentacaoAssistencial.buscar(cpfDoTitular, numeroDoCartao, Segurado.class, false,
				SegmentacaoAssistencialEnum.OBSTETRICIA_ELETIVO.getSegmentacaoAssistencial(),
				SegmentacaoAssistencialEnum.OBSTETRICIA_URGENCIA.getSegmentacaoAssistencial(),
				SegmentacaoAssistencialEnum.HOSPITALAR_URGENCIA.getSegmentacaoAssistencial(), 
				SegmentacaoAssistencialEnum.AMBULATORIAL_URGENCIA.getSegmentacaoAssistencial());
		else[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */
		ResumoSegurados resumo = BuscarSegurados.buscar(cpfDoTitular, numeroDoCartao,  Segurado.class, true);
		resumo.inicializaConsultasPromocionaisLiberadas();
		/* end[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */
		return resumo;
	}

	public GuiaCompleta criarGuiaUrgencia(ResumoSegurados resumo,Segurado segurado, Integer tipoDeGuia, Profissional profissionaisCRM, Profissional profissionaisNOME, Especialidade especialidade,Prestador prestador, UsuarioInterface usuario) throws Exception {
		return criarGuiaUrgencia(resumo, segurado, tipoDeGuia, profissionaisCRM, profissionaisNOME, especialidade, prestador, usuario, new Date(), null, null);
	}
	
	public GuiaCompleta criarGuiaUrgencia(ResumoSegurados resumo,Segurado segurado, Integer tipoDeGuia, Profissional profissionaisCRM, Profissional profissionaisNOME, Especialidade especialidade,Prestador prestador, UsuarioInterface usuario, Collection<CID> cids) throws Exception {
		return criarGuiaUrgencia(resumo, segurado, tipoDeGuia, profissionaisCRM, profissionaisNOME, especialidade, prestador, usuario, new Date(), cids, null);
	}
	
	public GuiaCompleta criarGuiaUrgencia(ResumoSegurados resumo,Segurado segurado, Integer tipoDeGuia, Profissional profissionaisCRM, Profissional profissionaisNOME, Especialidade especialidade,Prestador prestador, UsuarioInterface usuario, Date dataAtendimento) throws Exception {
		return criarGuiaUrgencia(resumo, segurado, tipoDeGuia, profissionaisCRM, profissionaisNOME, especialidade, prestador, usuario, dataAtendimento, null, null);
	}
	
	public GuiaCompleta criarGuiaUrgencia(ResumoSegurados resumo,Segurado segurado, Integer tipoDeGuia, Profissional profissionaisCRM, Profissional profissionaisNOME, Especialidade especialidade,Prestador prestador, UsuarioInterface usuario, String justificativa) throws Exception {
		return criarGuiaUrgencia(resumo, segurado, tipoDeGuia, profissionaisCRM, profissionaisNOME, especialidade, prestador, usuario, new Date(), null, justificativa);
	}
	
	public GuiaCompleta criarGuiaUrgencia(ResumoSegurados resumo,Segurado segurado, Integer tipoDeGuia, Profissional profissionaisCRM, Profissional profissionaisNOME, Especialidade especialidade,Prestador prestador, UsuarioInterface usuario, Collection<CID> cids, String justificativa) throws Exception {
		return criarGuiaUrgencia(resumo, segurado, tipoDeGuia, profissionaisCRM, profissionaisNOME, especialidade, prestador, usuario, new Date(), cids, justificativa);
	}
	
	public GuiaCompleta criarGuiaUrgencia(ResumoSegurados resumo,Segurado segurado, Integer tipoDeGuia, Profissional profissionaisCRM, Profissional profissionaisNOME, Especialidade especialidade,Prestador prestador, UsuarioInterface usuario, Date dataAtendimento, Collection<CID> cids, String justificativa) throws Exception {
		
		validaLimiteDeIdadePorEspecialidade(segurado, especialidade);
		
		/**
		 * Faz a validação de intervalo do limite de idade permitido para utilização de atendimento por especialidade.
		 * Quando não houver valor informado, ou seja estiver nulo, esta restrição não será executada.
		 * Ex: até 14 anos para especialidade (Pediatria)
		 *     acima de 60 anos (Geriatria)
		 */
		if(especialidade.getIdadeLimiteInicio()!=null || especialidade.getIdadeLimiteFim()!=null) {
			Integer idadeLimiteInicio = especialidade.getIdadeLimiteInicio();
			Integer idadeLimiteFim = especialidade.getIdadeLimiteFim();
			Integer idadeSegurado = Utils.getIdade(segurado.getPessoaFisica().getDataNascimento());
			
			boolean isIdadeSeguradoMaiorIgualIdadeLimite = idadeSegurado>=idadeLimiteInicio;
			boolean isIdadeSeguradoMenorIgualIdadeLimite = idadeSegurado<=idadeLimiteFim;
			
			if(!(isIdadeSeguradoMaiorIgualIdadeLimite && isIdadeSeguradoMenorIgualIdadeLimite)){
				throw new RuntimeException(MensagemErroEnum.LIMITE_IDADE_NAO_PERMITIDO_ESPECIALIDADE.getMessage(
						idadeSegurado.toString(), idadeLimiteInicio.toString(), idadeLimiteFim.toString()));
			}
		}
		
		Profissional profissional = getProfissional(profissionaisCRM, profissionaisNOME);
		
		GuiaCompleta<ProcedimentoInterface> guia = super.criarGuiaUrgencia(segurado, tipoDeGuia, profissional, especialidade, prestador, usuario, dataAtendimento, cids, justificativa);
		guia.getSegurado().getConsultasPromocionais().size();
		aplicarCalculosDaGuia(guia);
		if (guia.temConsultaPromocionalDeUrgenciaLiberada()){
			consumirConsultaPromocional(guia);
		}

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

	private void validaLimiteDeIdadePorEspecialidade(Segurado segurado, Especialidade especialidade) {
		/**
		 * Faz a validação de intervalo do limite de idade permitido para utilização de atendimento por especialidade.
		 * Quando não houver valor informado, ou seja estiver nulo, esta restrição não será executada.
		 * Ex: até 14 anos para especialidade (Pediatria)
		 *     acima de 60 anos (Geriatria)
		 */
		if(especialidade.getIdadeLimiteInicio()!=null || especialidade.getIdadeLimiteFim()!=null) {
			Integer idadeLimiteInicio = especialidade.getIdadeLimiteInicio();
			Integer idadeLimiteFim = especialidade.getIdadeLimiteFim();
			Integer idadeSegurado = Utils.getIdade(segurado.getPessoaFisica().getDataNascimento());
			
			boolean isIdadeSeguradoMaiorIgualIdadeLimite = idadeSegurado>=idadeLimiteInicio;
			boolean isIdadeSeguradoMenorIgualIdadeLimite = idadeSegurado<=idadeLimiteFim;
			
			if(!(isIdadeSeguradoMaiorIgualIdadeLimite && isIdadeSeguradoMenorIgualIdadeLimite)){
				throw new RuntimeException(MensagemErroEnum.LIMITE_IDADE_NAO_PERMITIDO_ESPECIALIDADE.getMessage(
						idadeSegurado.toString(), idadeLimiteInicio.toString(), idadeLimiteFim.toString()));
			}
		}
	}

	/**
	 * @param guia
	 */
	private void consumirConsultaPromocional(GuiaCompleta<ProcedimentoInterface> guia) {
		guia.setValorCoParticipacao(BigDecimal.ZERO);
		for (ProcedimentoInterface procedimento : guia.getProcedimentos()) {
			procedimento.setGeraCoParticipacao(false);
		}
		PromocaoConsulta promocaoConsultaLiberada = guia.getPromocaoConsultaLiberada();
		promocaoConsultaLiberada.setGuia(guia);
		promocaoConsultaLiberada.mudarSituacao(guia.getSituacao().getUsuario(), PromocaoConsulta.UTILIZADO, MotivoEnum.UTILIZADO_CONFIRMACAO_CONSULTA_URGENCIA.getMessage(), new Date());
	}

	public void salvarGuia(GuiaCompleta<ProcedimentoInterface> guia) throws Exception {
		super.salvarGuia(guia);
		
		if (guia.getPromocaoConsultaLiberada() != null){
			PromocaoConsulta promocaoConsultaLiberada = guia.getPromocaoConsultaLiberada();
			ImplDAO.save(promocaoConsultaLiberada);
		}
	}
	
	private void consumirConsultaPromocional(GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario) throws Exception {
		Set<PromocaoConsulta> consultasPromocionais = guia.getSegurado().getConsultasPromocionais();
		for (PromocaoConsulta consultaPromocional : consultasPromocionais) {
			if(consultaPromocional.getEspecialidade() != null)
				HibernateUtil.currentSession().lock(consultaPromocional.getEspecialidade(), LockMode.NONE);
			if(guia.isConsultaUrgencia() && consultaPromocional.isUrgencia() && !consultaPromocional.isVencido() && !consultaPromocional.isUtilizado()) {
				for (ProcedimentoInterface procedimento : guia.getProcedimentos()) {
					procedimento.setGeraCoParticipacao(false);
				}
				consultaPromocional.setGuia(guia);
				consultaPromocional.mudarSituacao(usuario, PromocaoConsulta.UTILIZADO, MotivoEnum.UTILIZADO_CONFIRMACAO_CONSULTA_URGENCIA.getMessage(), new Date());
				ImplDAO.save(consultaPromocional);
				break;
			}
		}
	}
	
	private void aplicarCalculosDaGuia(GuiaCompleta<ProcedimentoInterface> guia) {
		guia.updateValorCoparticipacao();
		guia.recalcularValores();
	}
	
}
