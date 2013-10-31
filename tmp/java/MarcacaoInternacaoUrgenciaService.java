package br.com.infowaypi.ecarebc.service.internacao;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacaoEletiva;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacaoUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.Taxa;
import br.com.infowaypi.ecarebc.atendimentos.acordos.AcordoPacote;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemTaxa;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.CID;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.ecarebc.service.Agente;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.exceptions.AssertionFailedException;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/*if[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO]
import br.com.infowaypi.ecare.services.ValidacaoAtendimentoSegmentacaoAssistencial;
import br.com.infowaypi.ecarebc.produto.SegmentacaoAssistencialEnum;
end[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */

/**
 * Service para solicitação de internações de urgência no plano de saúde
 * @author root
 */
public class MarcacaoInternacaoUrgenciaService<S extends SeguradoInterface> extends MarcacaoInternacaoService<GuiaInternacaoUrgencia> {
	
	public Long autorizacaoUrgencia;
	private GuiaCompleta guiaUrgencia;

	public MarcacaoInternacaoUrgenciaService(){
		super();
	}
	
	@Override
	public GuiaInternacaoUrgencia getGuiaInstanceFor(UsuarioInterface usuario) {
		return new GuiaInternacaoUrgencia(usuario);
	}

	public GuiaInternacaoUrgencia criarGuiaUrgencia(GuiaCompleta guiaOrigem, Integer tipoTratamento, Prestador prestador, UsuarioInterface usuario,
			Profissional solicitante, Especialidade especialidade, Collection<CID> cids, String justificativa) throws Exception {
		
		/* if_not[NAO_OBRIGATORIEDADE_DE_INFORMAR_PROFISSIONAL_NO_ATENDIMENTO]
		Assert.isNotNull(solicitante, MensagemErroEnum.SOLICITANTE_INVALIDO.getMessage());
		end[NAO_OBRIGATORIEDADE_DE_INFORMAR_PROFISSIONAL_NO_ATENDIMENTO] */
		
		Assert.isNotEmpty(justificativa,MensagemErroEnum.GUIA_URGENCIA_SEM_QUADRO_CLINICO.getMessage());

		this.guiaUrgencia = guiaOrigem;
		
		prestador = ImplDAO.findById(prestador.getIdPrestador(), Prestador.class);
		
		if(guiaOrigem.getSegurado().isInternado())
			throw new ValidateException(MensagemErroEnum.SEGURADO_INTERNADO_OU_COM_SOLICITACAO.getMessage());
		
		AbstractSegurado segurado = guiaOrigem.getSegurado();
		segurado.tocarObjetos();
		
		GuiaInternacaoUrgencia guia = super.criarGuia(guiaOrigem.getSegurado(),tipoTratamento, null, prestador, usuario, solicitante, 
				null, especialidade, null, cids, justificativa, Utils.hoje(), MotivoEnum.INTERNACAO_URGENCIA.getMessage(),Agente.PRESTADOR);
		
		/*if[INTERNACAO_OBSERVACAO_SEGURADO_CUMPRINDO_CARENCIA]
		//Adiciona uma taxa de obcervação se o segurado estiver cumprindo carência
		if(guia.getSegurado().isRegistraInternacaoDeObcervacaoParaDependenteEmCarencia()){
			guia.getSituacao().setMotivo("Beneficiário em cumprimento de carência. Assegurado somente consulta de urgência e observação em PS. O atendimento foi autorizado apenas para observação, e é válido por 12hs.");
		}
		configuraGuiaParaSeguradoEmCarencia(guia);
		end[INTERNACAO_OBSERVACAO_SEGURADO_CUMPRINDO_CARENCIA] */

		guia.setUsuarioDoFluxo(usuario);
		return guia;
	}

	/**
	 * Utilizado pelo cliente Uniplam para admitir na internação segurado em cumprimento de carência. 
	 * @param guia
	 * @throws Exception 
	 */
	private void configuraGuiaParaSeguradoEmCarencia(GuiaInternacaoUrgencia guia) throws Exception {
		if(guia.getSegurado().isRegistraInternacaoDeObcervacaoParaDependenteEmCarencia()){
			ItemTaxa itemTaxa = new ItemTaxa();
			itemTaxa.setGuia(guia);
			Taxa taxa = ImplDAO.findById(107L, Taxa.class);
			itemTaxa.getValor().setQuantidade(1);
			itemTaxa.getValor().setValor(BigDecimal.valueOf(taxa.getValor()));
			itemTaxa.setTaxa(taxa);
			itemTaxa.mudarSituacao(Usuario.getInstance("sistema"), SituacaoEnum.AUTORIZADO.descricao(), "Autorizado pelo sistema", new Date());
			guia.addItemTaxa(itemTaxa);
			
			guia.mudarSituacao(Usuario.getInstance("sistema"), SituacaoEnum.ABERTO.descricao(), "Autorizado pelo sistema", new Date());
		}
	}
	
	public GuiaCompleta buscarGuiaUrgencia(String autorizacao) throws Exception {
		if (autorizacao != null) {
			autorizacao = autorizacao.trim();
			boolean isValidValue = !StringUtils.isEmpty(autorizacao)&& StringUtils.isNumeric(autorizacao)&& (Long.parseLong(autorizacao) > 0);
			
			if (isValidValue){
				this.autorizacaoUrgencia = Long.parseLong(autorizacao.trim());
			} else {
				throw new AssertionFailedException(MensagemErroEnum.NUMERO_AUTORIZACAO_INVALIDO.getMessage());
			}
		}
		
		List<GuiaCompleta> guias = super.buscarGuias(getNotSearchSituacoes(SituacaoEnum.CANCELADO),autorizacao, null,null,null,null,false,false,GuiaCompleta.class, null);
		Assert.isNotEmpty(guias, MensagemErroEnum.GUIA_NAO_ENCONTRADA.getMessage(autorizacao));
		GuiaCompleta<ProcedimentoInterface> guia = guias.get(0);
		
		for (GuiaSimples guiaSimples : guia.getGuiasFilhas()) {
			if(!guiaSimples.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()) && guia.isInternacao()){
				throw new ValidateException(MensagemErroEnum.GUIA_JA_ORIGINOU_INTERNACAO.getMessage());
			}
		}
		
		Assert.isTrue((guia.isAtendimentoUrgencia() || guia.isConsultaUrgencia()),MensagemErroEnum.GUIA_NAO_PERTENCE_AO_TIPO.getMessage("CONSULTA ou ATENDIMENTO de URGÊNCIA"));
		
		guia.tocarObjetos();
		
		/*if[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO]
		ValidacaoAtendimentoSegmentacaoAssistencial.verificarSegmentacaoSegurado(guia.getSegurado(), 
				SegmentacaoAssistencialEnum.OBSTETRICIA_URGENCIA.getSegmentacaoAssistencial(),
				SegmentacaoAssistencialEnum.HOSPITALAR_URGENCIA.getSegmentacaoAssistencial());		
		end[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */
		
		
		return guia;
	}

	public GuiaInternacaoUrgencia addProcedimentos(Collection<Procedimento> procedimentos,Collection<ProcedimentoCirurgico> procedimentosCirurgicos,
			GuiaInternacaoUrgencia guia) throws Exception {
		
		return (GuiaInternacaoUrgencia) super.addProcedimentos(null,procedimentos, procedimentosCirurgicos, guia, null,Agente.PRESTADOR);
	}
	
	public GuiaInternacaoUrgencia addProcedimentos(Collection<ProcedimentoCirurgico> procedimentosCirurgicos,GuiaInternacaoUrgencia guia) throws Exception {
		return (GuiaInternacaoUrgencia) super.addProcedimentos(null,null, procedimentosCirurgicos, guia, null,Agente.PRESTADOR);
	}
	
	public GuiaInternacaoUrgencia addProcedimentos(Collection<ItemDiaria> diarias,GuiaInternacaoUrgencia guia,
			Collection<ItemPacote> pacotes) throws Exception {
		
		Collection<ProcedimentoCirurgico> procedimentosCirurgicos = guia.getProcedimentosCirurgicosDaSolicitacao();
		
		Boolean isPossuiPacote = !pacotes.isEmpty();
		Boolean isPossuiDiaria = !diarias.isEmpty();
		Boolean isGuiaTratamentoClinico = guia.getTipoTratamento() == GuiaInternacaoEletiva.TRATAMENTO_CLINICO;
		Boolean isPossuiProcedimentos = procedimentosCirurgicos != null && !procedimentosCirurgicos.isEmpty();
		Boolean existeUmaCidUrgencia = Boolean.FALSE;
		Boolean existeProcedimentoUrgencia = Boolean.FALSE;
		
		if(isPossuiDiaria && isPossuiPacote) {
			throw new RuntimeException(MensagemErroEnum.INTERNACAO_APENAS_PACOTES_OU_ACOMODACAO.getMessage());
		}
		
		if(!isGuiaTratamentoClinico && !isPossuiPacote && !isPossuiProcedimentos) {
			throw new RuntimeException(MensagemErroEnum.INTERNACAO_CIRURGICA_DEVE_CONTER_PROCEDIMENTOS.getMessage());
		}
		
		if(!isPossuiDiaria && !isPossuiPacote && !isPossuiProcedimentos) {
			throw new RuntimeException(MensagemErroEnum.INTERNACAO_VAZIA_NAO_PERMITIDA.getMessage());
		}
		
		/* if_not[INTEGRACAO]*/
		if(!isPossuiPacote && !isPossuiDiaria){
			Assert.isTrue(isPossuiDiaria, MensagemErroEnum.PACOTE_OU_ACOMODACAO_DEVE_SER_INFORMADO.getMessage());
		}
		/* end[INTEGRACAO]*/
		
		if(!diarias.isEmpty()){
			Assert.isFalse(diarias.size() > 1, MensagemErroEnum.URGENCIA_DIARIAS_DEMAIS.getMessage());
			/* if_not[INTEGRACAO]*/
			for (ItemDiaria diaria : diarias)
				Assert.isEquals(diaria.getValor().getQuantidade(), new Integer(1), MensagemErroEnum.URGENCIA_DIARIA_24H.getMessage());
			/* end[INTEGRACAO]*/
		}
			
		for (ItemDiaria diaria : diarias){
			diaria.setJustificativa("Solicitação de Internação");
			guia.addItemDiaria(diaria);
		}
		
		// verifica se há acordo, caso seja true, adiciona na guia senão levanta exceção
		boolean isPrestadorPossuiAcordoPacote;
		for (ItemPacote itemPacote : pacotes){
			isPrestadorPossuiAcordoPacote = false;
			for (AcordoPacote acordo : guia.getPrestador().getAcordosPacoteAtivos()) {
				if(acordo.getPacote().getDescricao().equals(itemPacote.getPacote().getDescricao())){
					isPrestadorPossuiAcordoPacote = true;
					itemPacote.getValor().setValor(new BigDecimal(acordo.getValor()));
					guia.addItemPacote(itemPacote);
				}
			}
			if(!isPrestadorPossuiAcordoPacote)
				throw new RuntimeException(MensagemErroEnum.PRESTADOR_SEM_ACORDO_ATIVO_PARA_O_PACOTE.getMessage(itemPacote.getPacote().getDescricao()));	
		}
		
		guia = (GuiaInternacaoUrgencia) super.addProcedimentos(null, null, procedimentosCirurgicos, guia, null, Agente.PRESTADOR);
		guia.validate();
		guia.recalcularValores();
		return guia;
	}	

	public void salvarGuia(GuiaSimples guia) throws Exception {
		guia.tocarObjetos();
		super.salvarGuia(guia);
	}

	@Override
	public void onAfterCriarGuia(GuiaInternacaoUrgencia guia, Collection<? extends ProcedimentoInterface> procedimentos)throws Exception {
		guia.setGuiaOrigem(guiaUrgencia);
		// guiaUrgencia.getGuiasFilhas().add(guia);
		super.onAfterCriarGuia(guia, procedimentos);
	}
}