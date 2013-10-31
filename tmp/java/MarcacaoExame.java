package br.com.infowaypi.ecare.services.exame;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecare.services.Service;
import br.com.infowaypi.ecare.utils.ProfissionalUtils;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.Critica;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.service.exames.MarcacaoExameService;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.sensews.client.SenseManager;


public class MarcacaoExame extends MarcacaoExameService{ 
	
	private final int PRAZO_MAXIMO_GUIA_CONSULTA = 60;

	public ResumoSegurados buscarSegurado(String cpfDoTitular,String numeroDoCartao){
		return BuscarSegurados.buscar(cpfDoTitular,numeroDoCartao,Segurado.class);
	}
	
	public GuiaExame<Procedimento> criarGuiaPrestador(Segurado segurado, Prestador prestador,
			Profissional solicitanteCRM, Profissional solicitanteNOME, Collection<Procedimento> procedimentos,UsuarioInterface usuario)
			throws Exception {
		 
		Assert.isNotNull(segurado, "Segurado deve ser informado!");
		Assert.isNotNull(prestador, "Prestador deve ser informado!");
		
		if(segurado.getDataAdesao() == null){
			throw new RuntimeException(MensagemErroEnumSR.SEGURADO_SEM_DATA_DE_ADESAO.getMessage());
		}
		
		if(segurado.getSituacao().getDescricao().equals(SituacaoEnum.SUSPENSO)
				&& segurado.getSituacao().getMotivo().equals("Não recadastrado")) {
				throw new ValidateException(MensagemErroEnumSR.SEGURADO_NAO_RECADASTRADO.getMessage());
		}
		
		Profissional profissional = ProfissionalUtils.getProfissionais(solicitanteCRM, solicitanteNOME);
		
		Assert.isTrue(profissional.isCredenciado(), MensagemErroEnum.PROFISSIONAl_NAO_CREDENCIADO.getMessage(profissional.getCrmNome()));
		
		GuiaExame<Procedimento> guia = criarGuiaPrestador(segurado, prestador, usuario, profissional, procedimentos);
		guia.setDataTerminoAtendimento(new Date());
		
		String nomeProfissional = profissional.getPessoaFisica().getNome();
		String nomeSegurado 	= segurado.getPessoaFisica().getNome();
		Assert.isNotNull(guia.getGuiaOrigem(), MensagemErroEnum.GUIA_SEM_CONSULTA_ORIGEM.getMessage(String.valueOf(PRAZO_MAXIMO_GUIA_CONSULTA)));
		
		if(guia.isPermiteMatMed()){
			guia.getSituacao().setDescricao(SituacaoEnum.ABERTO.descricao());
			guia.getSituacao().setMotivo(MotivoEnum.GUIA_EXAME_AGUARDANDO_FECHAMENTO.getMessage());
		}
		return guia;

	} 
	
	public void salvarGuia(GuiaExame<ProcedimentoInterface> guia) throws Exception {
		for (ProcedimentoInterface procedimento : guia.getProcedimentos()) {
			if(!super.geraCoParticipacao(procedimento)){
				procedimento.setGeraCoParticipacao(false);
				procedimento.setValorCoParticipacao(BigDecimal.ZERO);
			}
		}
		guia.recalcularValores();
		
		processarCriticas(guia.getCriticas());
		
		super.salvarGuia(guia);
		/* if[SENSE_MANAGER] */
		SenseManager.CONFIRMACAO_EXAME.analisar(guia);
		/* end[SENSE_MANAGER] */
	}
	
	public void onAfterCriarGuia(GuiaExame<ProcedimentoInterface> guia, Collection<? extends ProcedimentoInterface> procedimentos) throws Exception {
		Service.verificaRecadastramento(guia);
		super.onAfterCriarGuia(guia, procedimentos);
	}
	
	/**
	 * Método usado para efetuar o processamento especifico das críticas geradas neste fluxo.
	 * Neste fluxo as criticas não são exibidas, elas apenas são processadas como não avaliada e autorizada.
	 * @param criticas
	 */
	private void processarCriticas(Set<Critica> criticas){
		for(Critica critica : criticas){
			if(critica.getIdCritica() == null){
				critica.setAvaliada(false);
				critica.setAutorizada(true);
			}
		}
	}
	
}
