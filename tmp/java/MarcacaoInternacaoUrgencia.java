package br.com.infowaypi.ecare.services.internacao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecare.utils.ProfissionalUtils;
import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacaoUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.atendimentos.validators.InternacaoPossuiPacoteSemProcedimentoValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.InternacaoPossuiProcedimentoSemPacoteValidator;
import br.com.infowaypi.ecarebc.opme.ItemOpme;
import br.com.infowaypi.ecarebc.procedimentos.CID;
import br.com.infowaypi.ecarebc.produto.TipoAcomodacao;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.ecarebc.service.internacao.MarcacaoInternacaoUrgenciaService;
import br.com.infowaypi.ecarebc.utils.CommandCorrecaoCalculoValorProcedimento;
import br.com.infowaypi.ecarebc.utils.ProcedimentoUtils;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/* if_not[NAO_OBRIGATORIEDADE_DE_INFORMAR_PROFISSIONAL_NO_ATENDIMENTO]
import br.com.infowaypi.ecare.utils.ProfissionalUtils;
end[NAO_OBRIGATORIEDADE_DE_INFORMAR_PROFISSIONAL_NO_ATENDIMENTO] */

public class MarcacaoInternacaoUrgencia extends MarcacaoInternacaoUrgenciaService<Segurado> {

	public ResumoSegurados buscarSegurado(String numeroDoCartao,String cpfDoTitular) throws Exception {
		return BuscarSegurados.buscar(numeroDoCartao, cpfDoTitular,  Segurado.class);
	}
	
	public GuiaInternacaoUrgencia criarGuiaUrgencia(GuiaCompleta guia, Date dataAtendimento, Integer tipoTratamento, Prestador prestador, UsuarioInterface usuario,
			Profissional solicitanteCRM, Profissional solicitanteNOME, Collection<CID> cids, String justificativa) throws Exception {
		return criarGuiaUrgencia(guia, dataAtendimento, tipoTratamento, prestador, usuario, solicitanteCRM, solicitanteNOME, null, cids, justificativa);
	}
	
	public GuiaInternacaoUrgencia criarGuiaUrgencia(GuiaCompleta guia, Date dataAtendimento, Integer tipoTratamento, Prestador prestador, UsuarioInterface usuario,
			Profissional solicitanteCRM, Profissional solicitanteNOME, Especialidade especialidade, Collection<CID> cids, String justificativa) throws Exception {
		
		Profissional solicitante = getProfissional(solicitanteCRM, solicitanteNOME);
		
		GuiaInternacaoUrgencia internacao = super.criarGuiaUrgencia(guia, tipoTratamento, prestador, usuario, solicitante, especialidade, cids,justificativa);
		if (dataAtendimento != null) {
			internacao.setDataAtendimento(dataAtendimento);
		}
		return internacao;
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
	
	@Override
	public GuiaInternacaoUrgencia addProcedimentos(
			Collection<ItemDiaria> diarias, GuiaInternacaoUrgencia guia,
			Collection<ItemPacote> pacotes) throws Exception {
		
		/* if[VALIDAR_TIPO_ACOMODACAO_SOLICITADA]
		validarCoberturaDiariaSolicitada(guia.getSegurado(), diarias);
		end[VALIDAR_TIPO_ACOMODACAO_SOLICITADA] */
		
		ProcedimentoUtils.aplicaDescontoDaViaDeAcesso(guia);

		for (ItemOpme itemOpme : guia.getOpmesSolicitados()) {
			Assert.isNotNull(itemOpme.getObservacaoSolicitacao(), "O motivo da solicitação deve ser informado para OPMEs.");
			itemOpme.getSituacao().setMotivo(itemOpme.getObservacaoSolicitacao());
			itemOpme.getSituacao().setUsuario(guia.getUsuarioDoFluxo());
		}
		
		GuiaInternacaoUrgencia guiaReturn = super.addProcedimentos(diarias, guia, pacotes);
		
		atualizaDiariasSolicitadas(guia);
		
		new InternacaoPossuiPacoteSemProcedimentoValidator().execute(guiaReturn);
		new InternacaoPossuiProcedimentoSemPacoteValidator().execute(guiaReturn);
		
		CommandCorrecaoCalculoValorProcedimento cmd = new CommandCorrecaoCalculoValorProcedimento(guiaReturn);
		cmd.execute();
		
		guia.recalcularValores();
		
		return guiaReturn;
	}
	
	/**
     * Método que conformiza a quantidade de diárias solicitadas
     * @author Wislanildo
     * @since 08/07/2013
     * @param <G>
     * @param guia
     */
	private void atualizaDiariasSolicitadas(GuiaCompleta guia) {
		List<ItemDiaria> diariasSolicitadas = guia.getDiariasSolicitadas();
		for (ItemDiaria diaria : diariasSolicitadas) {
			diaria.setQuantidadeSolicitada((diaria.getValor().getQuantidade()));
		}
	}

	/**
	 * Verificar se o tipo de acomodação informada na internação é a mesma existente no produto do beneficiário.
	 * 
	 * @param segurado
	 * @param diarias
	 */
	private void validarCoberturaDiariaSolicitada(AbstractSegurado segurado, Collection<ItemDiaria> diarias) {
		if (segurado.getContratoAtual() == null || segurado.getContratoAtual().getProduto() == null){
			return;
		}
		
		for (ItemDiaria itemDiaria : diarias) {
			br.com.infowaypi.ecarebc.procedimentos.TipoAcomodacao acomodacaoDiaria = itemDiaria.getDiaria().getTipoAcomodacao();
			
			if(acomodacaoDiaria != null && acomodacaoDiaria.getDescricao().toUpperCase().equals(TipoAcomodacao.APARTAMENTO.getDescricao().toUpperCase())){
				TipoAcomodacao acomodacaoProduto = segurado.getContratoAtual().getProduto().getTipoAcomodacao();
				
				Assert.isEquals(
						acomodacaoProduto.getDescricao().toUpperCase(),
						TipoAcomodacao.APARTAMENTO.getDescricao().toUpperCase(),
						"A cobetura do beneficiário não permite o tipo de acomodação '"+TipoAcomodacao.APARTAMENTO.getDescricao()+"'");
			}
		}
	}
	
}
