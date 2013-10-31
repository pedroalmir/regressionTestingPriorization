package br.com.infowaypi.ecare.services.internacao;

import java.util.Collection;
import java.util.Date;

import br.com.infowaypi.ecare.atendimentos.acordos.itensAcordos.ItemDiariaAuditoria;
import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.service.internacao.ProrrogarInternacaoService;
import br.com.infowaypi.ecarebc.utils.CommandCorrecaoCalculoValorProcedimento;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

@SuppressWarnings("unchecked")
public class AutorizarProrrogacaoInternacao extends ProrrogarInternacaoService<Segurado> {
	
	public static final Boolean AUTORIZA = true;
	public static final Boolean NAO_AUTORIZA = false;
	
	public ResumoGuias<GuiaCompleta> buscarGuias(String numeroDoCartao,String cpfDoTitular, Prestador prestador) throws Exception {
		ResumoSegurados resumo = BuscarSegurados.buscar(numeroDoCartao, cpfDoTitular, Segurado.class);
		return super.buscarGuiasProrrogadas(resumo.getSegurados(), prestador);
	}
	
	public GuiaCompleta autorizarProrrogacao(GuiaCompleta<ProcedimentoInterface> guia, Collection<ItemDiariaAuditoria> diariasInseridas, UsuarioInterface usuario) throws Exception{
		super.tocarObjeto(guia);
		if (guia.getProfissional() != null) {
			guia.getProfissional().tocarObjetos();
		}
		
		boolean autorizaProrrogacao = false;
		
		// se houver diarias inseridas pelo auditor, a guia deverá ser autorizada
		if(!diariasInseridas.isEmpty()) {
			autorizaProrrogacao = true;
		}
		
		//se ao menos uma das diarias solicitadas pelo prestador forem autorizadas pelo auditor, a guia deverá ser autorizada.
		for (ItemDiaria diaria : guia.getDiariasSolicitadas()) {
			if(diaria.isAutorizado()){
				autorizaProrrogacao = true;
			}
			
			Assert.isNotNull(diaria.getJustificativaNaoAutorizacao(), MensagemErroEnum.MOTIVO_AUTORIZACAO_ACOMODACAO_REQUERIDO.getMessage(diaria.getDiaria().getCodigoDescricao()));
		}
		
		for (ItemDiariaAuditoria itemDiariaAuditoria : diariasInseridas) {
			itemDiariaAuditoria.mudarSituacao(usuario, SituacaoEnum.SOLICITADO.descricao(), MotivoEnum.INCLUIDO_PELO_AUDITOR.getMessage(), new Date());
			itemDiariaAuditoria.setAutorizado(true);
			itemDiariaAuditoria.setJustificativa(MotivoEnum.INCLUIDO_PELO_AUDITOR.getMessage());
			guia.addItemDiaria(itemDiariaAuditoria);
		}
		
		GuiaCompleta guiaAutorizada = this.autorizarProrrogacao(autorizaProrrogacao, guia, usuario);
		
		CommandCorrecaoCalculoValorProcedimento cmd = new CommandCorrecaoCalculoValorProcedimento(guiaAutorizada);
		cmd.execute();
		
		return guiaAutorizada;
	}
	
	public GuiaCompleta autorizarProrrogacao(Boolean autorizaProrrogacao, GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario) throws Exception{
		if (autorizaProrrogacao){
			//TODO tirar o atributo prazoAutorizado na lista de parametros
			guia.mudarSituacao(usuario, SituacaoEnum.PRORROGADO.descricao(), MotivoEnum.AUTORIZACAO_PRORROGACAO_GUIA.getMessage(), new Date());
			
			for (ItemDiaria diaria : guia.getItensDiaria()) {
				if(diaria.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao()) && diaria.isAutorizado()){
					diaria.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZACAO_SOLICITACAO_PRORROGACAO.getMessage(), new Date());
				}else if (diaria.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao()) && !diaria.isAutorizado()) {
					diaria.mudarSituacao(usuario, SituacaoEnum.NAO_AUTORIZADO.descricao(), MotivoEnum.NAO_AUTORIZACAO_SOLICITACAO_PRORROGACAO.getMessage(), new Date());
				}
			}
		}
		else{
			guia.mudarSituacao(usuario, SituacaoEnum.NAO_PRORROGADO.descricao(), MotivoEnum.PRORROGACAO_GUIA_NAO_AUTORIZADA.getMessage(), new Date());
			
			for (ItemDiaria diaria : guia.getItensDiaria()) {
				if(diaria.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao()) && !diaria.isAutorizado()){
					diaria.mudarSituacao(usuario, SituacaoEnum.NAO_AUTORIZADO.descricao(), MotivoEnum.NAO_AUTORIZACAO_SOLICITACAO_PRORROGACAO.getMessage(), new Date());
				}
			}
		}
		return guia;
	}
}
