package br.com.infowaypi.ecarebc.service.odonto;

import static br.com.infowaypi.msr.utils.Assert.isNotEmpty;
import static br.com.infowaypi.msr.utils.Assert.isNotNull;

import java.util.Collection;
import java.util.Date;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExameOdonto;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.Role;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOdonto;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.ecarebc.service.Agente;
import br.com.infowaypi.ecarebc.service.MarcacaoService;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Service padrão para solicitação de um tratamento odontológicos
 * @author Danilo Nogueira Portela
 */
@SuppressWarnings("unchecked")
public class SolicitacaoTratamentoOdontoService extends MarcacaoService<GuiaExameOdonto> {

	public SolicitacaoTratamentoOdontoService(){
		super();
	}
	
	@Override
	public GuiaExameOdonto getGuiaInstanceFor(UsuarioInterface usuario) {
		return new GuiaExameOdonto(usuario);
	} 

	@Deprecated
	public GuiaExameOdonto criarGuiaPrestador(AbstractSegurado segurado, Prestador prestador, UsuarioInterface usuario, Profissional solicitante, 
			Collection<ProcedimentoOdonto> procedimentos) throws Exception {

		Assert.isNotEmpty(procedimentos, MensagemErroEnum.PROCEDIMENTO_NAO_INFORMADO.getMessage());
		Assert.isNotNull(segurado, MensagemErroEnum.SEGURADO_NAO_INFORMADO.getMessage());
		Assert.isNotNull(prestador, MensagemErroEnum.PRESTADOR_NAO_INFORMADO.getMessage());
		Assert.isNotNull(solicitante, MensagemErroEnum.SOLICITANTE_NAO_INFORMADO.getMessage());
		prestador.tocarObjetos();
		
		GuiaExameOdonto guiaGerada = criarGuia(segurado, prestador, usuario, solicitante, null, this.getEspecialidadeOdonto(), procedimentos, Utils.hoje(), 
				MotivoEnum.SOLICITACAO_DE_TRATAMENTO_ODONTOLOGICO.getMessage(), Agente.PRESTADOR);
		
		for (ProcedimentoOdonto proc : guiaGerada.getProcedimentos()) 
			proc.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZACAO_DE_PROCEDIMENTO_ODONTO.getMessage(), new Date());
		
		return guiaGerada;
	}

	public <P extends ProcedimentoOdonto> GuiaExameOdonto criarGuiaEspecial(AbstractSegurado segurado, Prestador prestador, UsuarioInterface usuario, Profissional solicitante, 
			Collection<P> procedimentos) throws Exception {

		Assert.isNotEmpty(procedimentos, MensagemErroEnum.PROCEDIMENTO_NAO_INFORMADO.getMessage());
		Assert.isNotNull(segurado, MensagemErroEnum.SEGURADO_NAO_INFORMADO.getMessage());
		Assert.isNotNull(prestador, MensagemErroEnum.PRESTADOR_NAO_INFORMADO.getMessage());
		Assert.isNotNull(solicitante, MensagemErroEnum.SOLICITANTE_NAO_INFORMADO.getMessage());
		prestador.tocarObjetos();

		GuiaExameOdonto guiaGerada = criarGuia(segurado, prestador, usuario, solicitante, null, this.getEspecialidadeOdonto(), procedimentos, Utils.hoje(), 
				MotivoEnum.SOLICITACAO_DE_TRATAMENTO_ODONTOLOGICO.getMessage(), Agente.PRESTADOR);
		
		return guiaGerada;
	}

	/**
	 * Método para o lançamento de guias de tratamento odontológico pelo auditor
	 */
	public <P extends ProcedimentoOdonto> GuiaExameOdonto criarGuiaLancamento(AbstractSegurado segurado, Boolean ignorarValidacao, String dataDeAtendimento, 
			Profissional solicitante, Collection<P> procedimentos, UsuarioInterface usuario) throws Exception {

		Assert.isNotEmpty(procedimentos, MensagemErroEnum.PROCEDIMENTO_NAO_INFORMADO.getMessage());
		isNotEmpty(dataDeAtendimento, MensagemErroEnum.DATA_ATENDIMENTO_NAO_INFORMADA.getMessage());
		Assert.isNotNull(segurado, MensagemErroEnum.SEGURADO_NAO_INFORMADO.getMessage());
		isNotNull(solicitante, MensagemErroEnum.PROFISSIONAL_NAO_INFORMADO.getMessage());

		setIgnoreValidacao(ignorarValidacao);

		GuiaExameOdonto guiaGerada  = criarGuia(segurado, null, usuario, solicitante, null, this.getEspecialidadeOdonto(), procedimentos, dataDeAtendimento, 
				MotivoEnum.SOLICITACAO_DE_TRATAMENTO_ODONTOLOGICO.getMessage(), Agente.SUPERVISOR);
		
		/* if[ESPECIALIDADE_ODONTOLOGIA_GERAL_SOLICITACAO_TRATAMENTO]
		guiaGerada.setEspecialidade(this.getEspecialidadeOdontologiaGeral());
		end[ESPECIALIDADE_ODONTOLOGIA_GERAL_SOLICITACAO_TRATAMENTO]
		*/
		
		guiaGerada.getSituacao().setDescricao(SituacaoEnum.AUTORIZADO.descricao());

		String motivo = "";
		String role = usuario.getRole();
		
		if (role.equals(Role.ROOT.getValor()))
			motivo = MotivoEnum.AUTORIZADO_PELO_TI.getMessage();
		if (role.equals(Role.CENTRAL_DE_SERVICOS.getValor()))
			motivo = MotivoEnum.AUTORIZADO_PELA_CENTRAL_DE_SERVICOS.getMessage();
		if (role.equals(Role.AUDITOR_ODONTO.getValor()))
			motivo = MotivoEnum.AUTORIZADO_PELO_AUDITOR.getMessage();
		if (role.equals(Role.DIRETORIA_MEDICA.getValor()))
			motivo = MotivoEnum.AUTORIZADO_PELA_DIRETORIA.getMessage();
		
		for (ProcedimentoOdonto p :  guiaGerada.getProcedimentos()) 
			p.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), motivo, new Date());
		
		return guiaGerada;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onAfterCriarGuia(GuiaExameOdonto guia, Collection<? extends ProcedimentoInterface> procedimentos) throws Exception {
		for (ProcedimentoOdonto procedimento : (Collection<ProcedimentoOdonto>) procedimentos) {
			procedimento.calcularCampos();
			procedimento.setPericiaInicial(false);
			procedimento.setPericiaFinal(false);
			guia.addProcedimento(procedimento);
		}
	}

	public void salvarGuia(GuiaSimples guia) throws Exception {
		super.salvarGuia(guia);
	}

}
