package br.com.infowaypi.ecarebc.service.odonto;

import static br.com.infowaypi.msr.utils.Assert.isNotNull;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCirurgiaOdonto;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Service básico para confirmação de cirurgias odontológicas pelo prestador
 * @author Danilo Nogueira Portela
 */
public class ConfirmacaoCirurgiaOdontoService<S extends SeguradoInterface> extends Service{
	
	public ConfirmacaoCirurgiaOdontoService(){
		super();
	}
	
	public ResumoGuias<GuiaCirurgiaOdonto> buscarGuiasConfirmacao(Collection<S> segurados,Prestador prestador) throws Exception{
		SearchAgent sa = getSearchSituacoes(SituacaoEnum.AGENDADA);	
		List<GuiaCirurgiaOdonto> guias = super.buscarGuias(sa,segurados,prestador,true, GuiaCirurgiaOdonto.class);
		return new ResumoGuias<GuiaCirurgiaOdonto>(guias,ResumoGuias.SITUACAO_TODAS,true);
	}	
	
	public GuiaCirurgiaOdonto buscarGuiasConfirmacao(String autorizacao, Prestador prestador) throws Exception{
		Assert.isNotEmpty(autorizacao, MensagemErroEnum.NUMERO_AUTORIZACAO_INVALIDO.getMessage());
		
		GuiaCirurgiaOdonto guia = super.buscarGuias(autorizacao,prestador,true, GuiaCirurgiaOdonto.class, SituacaoEnum.AUTORIZADO);
		
		Assert.isNotNull(guia, MensagemErroEnum.GUIA_NAO_ENCONTRADA.getMessage(autorizacao));
		guia.tocarObjetos();
		return guia;
	}

	
	@SuppressWarnings("unchecked")
	public GuiaCompleta selecionarGuia(GuiaCirurgiaOdonto guia) throws Exception {
		return super.selecionarGuia(guia);
	}
	 
	public void salvarGuia(GuiaCirurgiaOdonto<ProcedimentoInterface> guia, UsuarioInterface usuario) throws Exception{
		isNotNull(guia, MensagemErroEnum.GUIA_INVALIDA.getMessage());
		
		guia.setValorAnterior(guia.getValorTotal());
		guia.setDataTerminoAtendimento(new Date());
		
		guia.mudarSituacao(usuario, SituacaoEnum.ABERTO.descricao(), MotivoEnum.PACIENTE_CIRURGIA_ODONTO.getMessage(), new Date());
		
		if(guia.isFromPrestador()) {
			guia.getPrestador().incrementarConsumoFinanceiro(guia);
		}
		
		for (ItemDiaria diaria : guia.getItensDiaria()) {
			diaria.calculaDataInicial();
		}
		
		guia.setDataAtendimento(new Date());
		ImplDAO.save(guia);
		ImplDAO.save(guia.getPrestador().getConsumoIndividual());
		ImplDAO.save(guia.getSegurado().getConsumoIndividual());
		
		//Atualização do consumo financeiro
//		StateMachineConsumo.updateConsumoAtendimento(guia, true, true);
	}
	

}
