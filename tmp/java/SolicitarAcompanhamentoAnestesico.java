package br.com.infowaypi.ecare.services;

import java.util.Set;

import br.com.infowaypi.ecare.atendimentos.GeradorGuiaAcompanhamentoAnestesico;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaAcompanhamentoAnestesico;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author Dannylvan
 * @since 12-12-2010
 */
public class SolicitarAcompanhamentoAnestesico extends SolicitarProcedimentos {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public GuiaAcompanhamentoAnestesico buscarGuia(String autorizacao, Prestador prestador, UsuarioInterface usuario) throws Exception {
		if (Utils.isStringVazia(autorizacao)) {
			throw new ValidateException(MensagemErroEnum.AUSENCIA_DE_PARAMETROS.getMessage());
		}
		
		GuiaCompleta guia = super.buscarGuias(autorizacao, prestador, false,
				GuiaCompleta.class, SituacaoEnum.SOLICITADO, SituacaoEnum.CONFIRMADO, 
				SituacaoEnum.ABERTO, SituacaoEnum.FECHADO, SituacaoEnum.ENVIADO,
				SituacaoEnum.RECEBIDO, SituacaoEnum.DEVOLVIDO, SituacaoEnum.AUDITADO,
				SituacaoEnum.AUTORIZADO, SituacaoEnum.PARCIALMENTE_AUTORIZADO);
		
		guia.tocarObjetos();
		
		Set<GuiaSimples> guiasFilhas = guia.getGuiasFilhas();
		for(GuiaSimples guiaFilha : guiasFilhas){
			Assert.isFalse(guiaFilha.isAcompanhamentoAnestesico(), MensagemErroEnum.GUIA_ACOMPANHAMENTO_ANESTESICO_JA_EXISTE.getMessage());
		}
		
		AbstractSegurado segurado = guia.getSegurado();
		String motivo 				= "Acompanhamento anestésico solicitado posteriormente à solicitação da guia.";
		String situacao 			= SituacaoEnum.SOLICITADO.descricao();

		GuiaAcompanhamentoAnestesico guiaAA = new GeradorGuiaAcompanhamentoAnestesico().
				gerarGuiaDeAcompanhamentoAnestesico(
				guia.getProcedimentos(SituacaoEnum.AUTORIZADO.descricao(),
						SituacaoEnum.SOLICITADO.descricao(), SituacaoEnum.CONFIRMADO.descricao()), guia, usuario, segurado, motivo, situacao);

		return guiaAA;
	}
	
	public GuiaAcompanhamentoAnestesico conferirDados(GuiaAcompanhamentoAnestesico gaa) throws Exception {
		super.salvarGuia(gaa.getGuiaOrigem());

		super.salvarGuia(gaa, false, false);
		
		return gaa;
	}
}
