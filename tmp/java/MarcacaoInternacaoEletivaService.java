package br.com.infowaypi.ecarebc.service.internacao;

import java.util.Collection;
import java.util.Date;

import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacaoEletiva;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.acordos.AcordoPacote;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.Role;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.CID;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.ecarebc.service.Agente;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Service para a solicitação de internações eletivas no plano de saúde
 * @author root
 */
public class MarcacaoInternacaoEletivaService<S extends AbstractSegurado> extends MarcacaoInternacaoService<GuiaInternacaoEletiva> {

	public MarcacaoInternacaoEletivaService(){
		super();
	}
	
	@Override
	public GuiaInternacaoEletiva getGuiaInstanceFor(UsuarioInterface usuario) {
		return new GuiaInternacaoEletiva(usuario);
	}

	public GuiaInternacaoEletiva criarGuiaEletiva(S segurado,Integer tipoTratamento,
			Integer tipoAcomodacao, Prestador prestador, UsuarioInterface usuario, Profissional solicitante, 
			Especialidade especialidade, Collection<CID> cids, String justificativa) throws Exception {
		
		Assert.isNotNull(segurado, MensagemErroEnum.SEGURADO_NAO_INFORMADO.getMessage());
		
		/* if_not[NAO_OBRIGATORIEDADE_DE_INFORMAR_PROFISSIONAL_NO_ATENDIMENTO]
		Assert.isNotNull(solicitante, MensagemErroEnum.SOLICITANTE_NAO_INFORMADO.getMessage());
		end[NAO_OBRIGATORIEDADE_DE_INFORMAR_PROFISSIONAL_NO_ATENDIMENTO] */
		
		Assert.isNotEmpty(justificativa, "O quadro clínico deve ser preenchido!");
		
		if(segurado.isInternado() && !usuario.isPossuiRole(UsuarioInterface.ROLE_DIRETORIA_MEDICA))
			throw new ValidateException(MensagemErroEnum.SEGURADO_INTERNADO_OU_COM_SOLICITACAO.getMessage());
		
		GuiaInternacaoEletiva guia = super.criarGuia(segurado,tipoTratamento,tipoAcomodacao, prestador, usuario, solicitante,null, 
				especialidade, null, cids, justificativa, "", MotivoEnum.INTERNACAO_ELETIVA.getMessage(), Agente.SUPERVISOR);
		
		return guia;
	}
	 
	public GuiaInternacao addProcedimentos(Collection<Procedimento> procedimentos, Collection<ProcedimentoCirurgico> procedimentosCirurgicos,GuiaInternacaoEletiva guia) throws Exception {
		return (GuiaInternacao)super.addProcedimentos(null,procedimentos, procedimentosCirurgicos,guia,null, Agente.SUPERVISOR);
	}

	public GuiaInternacao addProcedimentos(Collection<ProcedimentoCirurgico> procedimentosCirurgicos,GuiaInternacaoEletiva guia) throws Exception {
		return (GuiaInternacao)super.addProcedimentos(null,null, procedimentosCirurgicos,guia,null, Agente.SUPERVISOR);
	}
	
	public GuiaInternacaoEletiva addProcedimentos(
			GuiaInternacaoEletiva guia,
			UsuarioInterface usuario,
			Collection<ItemDiaria> diarias,
			Collection<ProcedimentoCirurgico> procedimentosCirurgicos,
			Collection<ItemPacote> pacotes) throws Exception {
		
		Boolean isPossuiPacote = !pacotes.isEmpty();
		Boolean isPossuiDiaria = !diarias.isEmpty();
		Boolean isGuiaTratamentoClinico = guia.getTipoTratamento() == GuiaInternacaoEletiva.TRATAMENTO_CLINICO;
		Boolean isPossuiProcedimentos = !procedimentosCirurgicos.isEmpty();
		
		if(isPossuiDiaria && isPossuiPacote) {
			throw new RuntimeException(MensagemErroEnum.INTERNACAO_APENAS_PACOTES_OU_ACOMODACAO.getMessage());
		}
		
		if(!isGuiaTratamentoClinico && !isPossuiProcedimentos) {
			throw new RuntimeException(MensagemErroEnum.INTERNACAO_CIRURGICA_DEVE_CONTER_PROCEDIMENTOS.getMessage());
		}
		
		if(!diarias.isEmpty()){
			Assert.isFalse(diarias.size() > 1, MensagemErroEnum.URGENCIA_DIARIAS_DEMAIS.getMessage());
		}
		
		if(!isPossuiDiaria && !isPossuiPacote && !isPossuiProcedimentos) {
			throw new RuntimeException(MensagemErroEnum.INTERNACAO_VAZIA_NAO_PERMITIDA.getMessage());
		}
		
		if(!isPossuiPacote && !isPossuiDiaria){
			Assert.isTrue(isPossuiDiaria, MensagemErroEnum.PACOTE_OU_ACOMODACAO_DEVE_SER_INFORMADO.getMessage());
		}

		adicionarDiarias(guia, usuario, diarias);
		adicionarPacotes(guia, usuario, pacotes);
		autorizaProcedimentos(usuario, procedimentosCirurgicos);
		
		return  (GuiaInternacaoEletiva) super.addProcedimentos(null, null, procedimentosCirurgicos, guia, null, Agente.PRESTADOR);
	}
	
	public GuiaInternacaoEletiva addProcedimentosCentral(
			GuiaInternacaoEletiva guia,
			UsuarioInterface usuario,
			Collection<ItemDiaria> diarias,
			Collection<ProcedimentoCirurgico> procedimentosCirurgicos,
			Collection<ItemPacote> pacotes) throws Exception {
		
		Boolean isPossuiPacote = !pacotes.isEmpty();
		Boolean isPossuiDiaria = !diarias.isEmpty();
		Boolean isGuiaTratamentoClinico = guia.getTipoTratamento() == GuiaInternacaoEletiva.TRATAMENTO_CLINICO;
		Boolean isPossuiProcedimentos = !procedimentosCirurgicos.isEmpty();
		
		if(isPossuiDiaria && isPossuiPacote) {
			throw new RuntimeException(MensagemErroEnum.INTERNACAO_APENAS_PACOTES_OU_ACOMODACAO.getMessage());
		}
		
		if(!isGuiaTratamentoClinico && !isPossuiProcedimentos) {
			throw new RuntimeException(MensagemErroEnum.INTERNACAO_CIRURGICA_DEVE_CONTER_PROCEDIMENTOS.getMessage());
		}
		
		if(!diarias.isEmpty()){
			Assert.isFalse(diarias.size() > 1, MensagemErroEnum.URGENCIA_DIARIAS_DEMAIS.getMessage());
		}
		
		if(!isPossuiDiaria && !isPossuiPacote && !isPossuiProcedimentos) {
			throw new RuntimeException(MensagemErroEnum.INTERNACAO_VAZIA_NAO_PERMITIDA.getMessage());
		}
		
		if(!isPossuiPacote && !isPossuiDiaria){
			Assert.isTrue(isPossuiDiaria, MensagemErroEnum.PACOTE_OU_ACOMODACAO_DEVE_SER_INFORMADO.getMessage());
		}
		if(isPossuiProcedimentos) {
			for (ProcedimentoCirurgico procedimento : procedimentosCirurgicos) {
				if(procedimento.getSituacao() != null) {
					procedimento.getSituacao().setDescricao(SituacaoEnum.SOLICITADO.descricao());
					procedimento.getSituacao().setMotivo(MotivoEnum.SOLICITADO_PELA_CENTRAL_DE_SERVICOS.getMessage());
				}else {
					procedimento.mudarSituacao(null, SituacaoEnum.SOLICITADO.descricao(), MotivoEnum.SOLICITADO_PELA_CENTRAL_DE_SERVICOS.getMessage(), new Date());
				}
			}
		}

		adicionarDiariasCentral(guia, usuario, diarias);
		adicionarPacotesCentral(guia, usuario, pacotes);	
		return  (GuiaInternacaoEletiva) super.addProcedimentos(null, null, procedimentosCirurgicos, guia, null, Agente.PRESTADOR);
	}

	private void autorizaProcedimentos(UsuarioInterface usuario,Collection<ProcedimentoCirurgico> procedimentosCirurgicos) {
		if(usuario.isPossuiRole(Role.ROOT.getValor()) || usuario.isPossuiRole(Role.AUDITOR.getValor()) || usuario.getRole().equals(Role.DIGITADOR.getValor())
				|| usuario.isPossuiRole(Role.CENTRAL_DE_SERVICOS.getValor()) || usuario.isPossuiRole(Role.DIRETORIA_MEDICA.getValor())){
			
			String motivo = null;
			if(usuario.isPossuiRole(Role.ROOT.getValor()))
				motivo = MotivoEnum.AUTORIZADO_PELO_TI.getMessage();
			if(usuario.getRole().equals(Role.AUDITOR.getValor()))
				motivo = MotivoEnum.AUTORIZADO_PELO_AUDITOR.getMessage();
			if(usuario.getRole().equals(Role.DIGITADOR.getValor()))
				motivo = MotivoEnum.AUTORIZADO_PELO_DIGITADOR.getMessage();
			if(usuario.isPossuiRole(Role.CENTRAL_DE_SERVICOS.getValor()))
				motivo = MotivoEnum.AUTORIZADO_PELA_CENTRAL_DE_SERVICOS.getMessage();
			if(usuario.isPossuiRole(Role.DIRETORIA_MEDICA.getValor()))
				motivo = MotivoEnum.AUTORIZADO_PELA_DIRETORIA.getMessage();
			
			for (ProcedimentoCirurgicoInterface procedimento : procedimentosCirurgicos) {
				procedimento.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), motivo, new Date());
			}
		}
	}
	
	private void adicionarDiarias(GuiaInternacaoEletiva guia,
			UsuarioInterface usuario, Collection<ItemDiaria> diarias) throws Exception {
		if(usuario.isPossuiRole(Role.ROOT.getValor()) || usuario.isPossuiRole(Role.AUDITOR.getValor()) || usuario.getRole().equals(Role.DIGITADOR.getValor()) || usuario.isPossuiRole(Role.CENTRAL_DE_SERVICOS.getValor())){
			String motivo = null;
			if(usuario.isPossuiRole(Role.ROOT.getValor()))
				motivo = MotivoEnum.AUTORIZADO_PELO_TI.getMessage();
			if(usuario.getRole().equals(Role.AUDITOR.getValor()))
				motivo = MotivoEnum.AUTORIZADO_PELO_AUDITOR.getMessage();
			if(usuario.getRole().equals(Role.DIGITADOR.getValor()))
				motivo = MotivoEnum.AUTORIZADO_PELO_DIGITADOR.getMessage();
			if(usuario.isPossuiRole(Role.CENTRAL_DE_SERVICOS.getValor()))
				motivo = MotivoEnum.AUTORIZADO_PELA_CENTRAL_DE_SERVICOS.getMessage();
			
			for (ItemDiaria itemDiaria : diarias) {
				itemDiaria.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), motivo, new Date());
				guia.addItemDiaria(itemDiaria);	
			}
		}
	}
	
	private void adicionarDiariasCentral(GuiaInternacaoEletiva guia,
			UsuarioInterface usuario, Collection<ItemDiaria> diarias) throws Exception {
			String motivo = MotivoEnum.SOLICITADO_PELA_CENTRAL_DE_SERVICOS.getMessage();

			for (ItemDiaria itemDiaria : diarias) {
				itemDiaria.mudarSituacao(usuario, SituacaoEnum.SOLICITADO.descricao(), motivo, new Date());
				itemDiaria.setJustificativa("Solicitação de Internação");
				guia.addItemDiaria(itemDiaria);		
		}
	}

	private void adicionarPacotes(GuiaInternacaoEletiva guia,
			UsuarioInterface usuario, Collection<ItemPacote> pacotes)
			throws Exception {
		boolean isPrestadorPossuiAcordoPacote;
		for (ItemPacote itemPacote : pacotes){
			itemPacote.tocarObjetos();
			isPrestadorPossuiAcordoPacote = false;
			for (AcordoPacote acordo : guia.getPrestador().getAcordosPacoteAtivos()) {
				if(acordo.getPacote().equals(itemPacote.getPacote())){
					isPrestadorPossuiAcordoPacote = true;
					itemPacote.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZADO_PELO_AUDITOR.getMessage(), new Date());
					guia.addItemPacote(itemPacote);
				}
			}
			if(!isPrestadorPossuiAcordoPacote)
				throw new RuntimeException(MensagemErroEnum.PRESTADOR_SEM_ACORDO_ATIVO_PARA_O_PACOTE.getMessage(itemPacote.getPacote().getDescricao()));	
		}
	}
	
	//@see br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta#addItemPacote(ItemPacote item, Usuario usuario).
	private void adicionarPacotesCentral(GuiaInternacaoEletiva guia,
			UsuarioInterface usuario, Collection<ItemPacote> pacotes)
			throws Exception {
		boolean isPrestadorPossuiAcordoPacote;
		for (ItemPacote itemPacote : pacotes){
			itemPacote.tocarObjetos();
			isPrestadorPossuiAcordoPacote = false;
			for (AcordoPacote acordo : guia.getPrestador().getAcordosPacoteAtivos()) {
				if(acordo.getPacote().equals(itemPacote.getPacote())){
					isPrestadorPossuiAcordoPacote = true;
					itemPacote.mudarSituacao(usuario, SituacaoEnum.SOLICITADO.descricao(), MotivoEnum.SOLICITADO_PELA_CENTRAL_DE_SERVICOS.getMessage(), new Date());
					guia.addItemPacote(itemPacote);
				}
			}
			if(!isPrestadorPossuiAcordoPacote)
				throw new RuntimeException(MensagemErroEnum.PRESTADOR_SEM_ACORDO_ATIVO_PARA_O_PACOTE.getMessage(itemPacote.getPacote().getDescricao()));	
		}
	}
	
	public void salvarGuia(GuiaSimples guia) throws Exception {
		super.salvarGuia(guia);
	}
	

}
