/**
 * 
 */
package br.com.infowaypi.ecare.services.exame;

import java.util.Collection;

import br.com.infowaypi.ecare.atendimentos.GeradorGuiaAcompanhamentoAnestesico;
import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecare.services.Service;
import br.com.infowaypi.ecare.utils.ProfissionalUtils;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaAcompanhamentoAnestesico;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuiasComAcompanhamentoAnestesico;
import br.com.infowaypi.ecarebc.atendimentos.enums.ValidateGuiaEnum;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.service.exames.MarcacaoExameService;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/**
 * @author Marcus bOolean
 *
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class SolicitacaoExameCentral extends MarcacaoExameService{
	
	public ResumoSegurados buscarSegurado(String cpfDoTitular,String numeroDoCartao){
		return BuscarSegurados.buscar(cpfDoTitular, numeroDoCartao, Segurado.class);
	}
	
	public ResumoGuiasComAcompanhamentoAnestesico criarGuiaLancamento(Segurado segurado, Profissional profissionalCRM,Profissional profissionalNOME, 
														Collection<Procedimento> procedimentos, boolean acompanhamentoAnestesico, 
														UsuarioInterface usuario) throws Exception {
		
		Assert.isNotNull(segurado, MensagemErroEnum.SEGURADO_NAO_INFORMADO.getMessage());
		segurado.getPessoaFisica().getEndereco().tocarObjetos();
		
		if (segurado.getDataAdesao() == null){
			throw new RuntimeException(MensagemErroEnumSR.SEGURADO_SEM_DATA_DE_ADESAO.getMessage());
		}
		
		if (segurado.getSituacao().getDescricao().equals(SituacaoEnum.SUSPENSO)
				&& segurado.getSituacao().getMotivo().equals("Não recadastrado")) {
					throw new ValidateException(MensagemErroEnumSR.SEGURADO_NAO_RECADASTRADO.getMessage());
		}

		Profissional profissional = ProfissionalUtils.getProfissionais(profissionalCRM, profissionalNOME);
		
		if (!profissional.isCredenciado()) {
			throw new RuntimeException(MensagemErroEnumSR.PROFISSIONAL_NAO_CREDENCIADO.getMessage());
		}
		
		ResumoGuiasComAcompanhamentoAnestesico resumoGuiaComAcompanhamento = new ResumoGuiasComAcompanhamentoAnestesico();

		GuiaExame guiaExame = super.criarGuiaExameCentral(segurado,false, null, profissional, procedimentos, usuario);
		resumoGuiaComAcompanhamento.setGuiaOrigem(guiaExame);
		
		if (acompanhamentoAnestesico) {
			String motivo = guiaExame.getSituacao().getMotivo();
			String situacao = guiaExame.getSituacao().getDescricao();
			
			GuiaAcompanhamentoAnestesico guiaAA = new GeradorGuiaAcompanhamentoAnestesico().
			gerarGuiaDeAcompanhamentoAnestesico(
					procedimentos, guiaExame, usuario, segurado, motivo, situacao);
			
			resumoGuiaComAcompanhamento.setGuiaAcompanhamentoAnestesico(guiaAA);
		}
		
		Assert.isNotEmpty(resumoGuiaComAcompanhamento.getPrestadoresQueRealizamProcedimentos(), "Não existe(m) prestador(es) habilitado(s) para atender o(s) procedimento(s) informado(s).");
	
		return resumoGuiaComAcompanhamento;
	}


	@Override
	public void onAfterCriarGuia(GuiaExame<ProcedimentoInterface> guia, Collection<? extends ProcedimentoInterface> 
									procedimentos) throws Exception {
		
		Service.verificaRecadastramento(guia);
		super.onAfterCriarGuiaCentral(guia, procedimentos);
	}
	
	public ResumoGuiasComAcompanhamentoAnestesico selecionarPrestador(
				ResumoGuiasComAcompanhamentoAnestesico resumoGuiaComAcompanhamento, 
				Prestador prestador) throws Exception {
		
		Assert.isNotNull(prestador, MensagemErroEnum.PRESTADOR_NAO_INFORMADO.getMessage());
		
		prestador.setAtualizarConsumo(false);

		GuiaCompleta guia = resumoGuiaComAcompanhamento.getGuiaOrigem();
		guia.setPrestador(prestador);

//		new TetoExamesPrestadorValidator().execute(guia);
		
		guia.aplicaValorAcordo();
		guia.setSolicitadoComPrestador(true);
		
//		guia.validate();
		
		//Solução para validação duplicada da guia.
		guia.addFlowValidator(ValidateGuiaEnum.TETO_EXAME_PRESTADOR.getValidator());
		guia.addFlowValidator(ValidateGuiaEnum.EXAME_VALIDATOR.getValidator());
		guia.addFlowValidator(ValidateGuiaEnum.EXAME_PRESTADOR_VALIDATOR.getValidator());
		
		guia.executeFlowValidators();
		
		
		
		return resumoGuiaComAcompanhamento;
	}
	
	public void salvarGuia(ResumoGuiasComAcompanhamentoAnestesico resumoGuiaComAcompanhamento) throws Exception {
		resumoGuiaComAcompanhamento.getGuiaOrigem().recalcularValores();
		super.salvarGuia(resumoGuiaComAcompanhamento.getGuiaOrigem());
		
		if (resumoGuiaComAcompanhamento.getGuiaAcompanhamentoAnestesico() != null) {
			resumoGuiaComAcompanhamento.getGuiaAcompanhamentoAnestesico().recalcularValores();
			super.salvarGuia(resumoGuiaComAcompanhamento.getGuiaAcompanhamentoAnestesico(), false, false);
		}
	}
		
}
