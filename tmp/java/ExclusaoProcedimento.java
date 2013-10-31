package br.com.infowaypi.ecarebc.service;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.com.infowaypi.ecare.validacao.services.InclusaoProcedimentoSituacaoGuiaValidator;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.msr.exceptions.AssertionFailedException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;


public class ExclusaoProcedimento<S extends SeguradoInterface> extends MarcacaoService<GuiaCompleta>{
	
	public GuiaCompleta buscarGuia(String autorizacao, Prestador prestador) throws Exception{

		if (autorizacao != null) {
			boolean isValidValue = !StringUtils.isEmpty(autorizacao)
					&& StringUtils.isNumeric(autorizacao)
					&& (Long.parseLong(autorizacao) > 0);

			if (!isValidValue) {
				throw new AssertionFailedException(
						MensagemErroEnum.NUMERO_AUTORIZACAO_INVALIDO
								.getMessage());
			}
		}

		List<GuiaCompleta> guias = super.buscarGuias(new SearchAgent(),autorizacao, null,null,null,prestador,false,
				false,GuiaCompleta.class, null);
		
		Assert.isNotEmpty(guias, MensagemErroEnum.GUIA_NAO_ENCONTRADA.getMessage(
				 autorizacao));
		GuiaCompleta guia = guias.get(0);
		
		new InclusaoProcedimentoSituacaoGuiaValidator(guia, false);
		
		Assert.isTrue((guia.isAtendimentoUrgencia() || guia
				.isConsultaUrgencia() || guia.isInternacao()),
				MensagemErroEnum.GUIA_NAO_PERTENCE_AO_TIPO
						.getMessage("CONSULTA ou ATENDIMENTO de URGÊNCIA"));
		guia.tocarObjetos();
		
		return guia;
	}
	
	public GuiaCompleta removerProcedimentos(Collection<Procedimento> procedimentos,GuiaCompleta guia, UsuarioInterface usuario) throws Exception{
		for (ProcedimentoInterface procedimento : procedimentos) {
			
			if(procedimento.getProcedimentoDaTabelaCBHPM().getCodigo().equals(GuiaSimples.PROCEDIMENTO_CONSULTA_URGENCIA))
				throw new RuntimeException(MensagemErroEnum.PROCEDIMENTO_PADRAO_NAO_PODE_SER_EXCLUIDO.getMessage("de código \"10101039\" de uma Consulta de urgência."));
				
			guia.removeProcedimento(procedimento, usuario);
			//ImplDAO.save(procedimento);
		}
		guia.recalcularValores();
		return guia;
		
	}
	
	public void salvarGuia(GuiaSimples guia) throws Exception {
		super.salvarGuia(guia);
	}
	
	@Override
	public GuiaCompleta getGuiaInstanceFor(UsuarioInterface usuario) {
		return null;
	}
}
