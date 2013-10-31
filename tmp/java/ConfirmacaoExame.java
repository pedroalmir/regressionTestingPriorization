package br.com.infowaypi.ecare.services.exame;

import static br.com.infowaypi.ecarebc.enums.SituacaoEnum.AGENDADA;
import static br.com.infowaypi.msr.utils.Assert.isNotEmpty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.service.exames.ConfirmacaoExameService;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;
import br.com.infowaypi.sensews.client.SenseManager;

@SuppressWarnings("rawtypes")
public class ConfirmacaoExame extends ConfirmacaoExameService {
	
	public ResumoSegurados buscarSegurados(String cpfDoTitular,String numeroDoCartao) {
		return BuscarSegurados.buscar(cpfDoTitular, numeroDoCartao, Segurado.class);
	}
	
	public ResumoGuias selecionarSegurado(Prestador prestador, Segurado segurado) {
		prestador.tocarObjetos();
		segurado.tocarObjetos();
		List<GuiaSimples> guias = new ArrayList<GuiaSimples>();
		guias.addAll(segurado.getGuias());
		ResumoGuias resumoGuia = new ResumoGuias<GuiaSimples>(guias, ResumoGuias.SITUACAO_AGENDADA, false);
		resumoGuia.setPrestador(prestador);
		Assert.isNotEmpty(resumoGuia.getGuiasExamePeloPrestador(),MensagemErroEnum.GUIAS_NAO_ENCONTRADAS_PARA_O_SEGURADO.getMessage("confirmadas"));
		return resumoGuia;
	}
	
	public ResumoGuias selecionarSeguradoAnestesista(Segurado segurado, Prestador prestador) {
		List<String> situacoes = Arrays.asList(SituacaoEnum.AUTORIZADO.descricao());
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("segurado",segurado));
		sa.addParameter(new In("situacao.descricao",situacoes));
		
		segurado.tocarObjetos();
		
		List<GuiaSimples> guias = sa.list(GuiaExame.class);
			
		guias.addAll(segurado.getGuias());
		ResumoGuias resumoGuia = new ResumoGuias<GuiaSimples>(guias, ResumoGuias.SITUACAO_TODAS, false);
		resumoGuia.setPrestador(prestador);

		Assert.isNotEmpty(resumoGuia.getGuiasExamePeloPrestador(),MensagemErroEnum.GUIAS_NAO_ENCONTRADAS_PARA_O_SEGURADO.getMessage("confirmadas"));
		return resumoGuia;
	}
	
	public GuiaSimples selecionarGuia(GuiaExame guia, Prestador prestador) throws Exception {
		Calendar calendar = new GregorianCalendar();
		Date dataLimite;
		
		if (guia.isGuiaCoopanest())
			dataLimite = Utils.incrementaDias(calendar, -90);
		else
			dataLimite = Utils.incrementaDias(calendar, -60);
		
		if (guia.isExameExterno()) {
			if (prestador.equals(guia.getGuiaOrigem().getPrestador())) {
				throw new RuntimeException("Uma guia de EXAME EXTERNO não pode ser confirmada pelo mesmo prestador que a solicitou.");
			}
		}
		Date dataSituacao = null;
		if (guia.getSituacao(SituacaoEnum.AUTORIZADO.descricao()) != null) {
			dataSituacao = guia.getSituacao(SituacaoEnum.AUTORIZADO.descricao()).getDataSituacao();
		} else if (guia.getSituacao(SituacaoEnum.PARCIALMENTE_AUTORIZADO.descricao()) != null) {
			dataSituacao = guia.getSituacao(SituacaoEnum.PARCIALMENTE_AUTORIZADO.descricao()).getDataSituacao();
		}
		if(Utils.compareData(dataSituacao, dataLimite) < 0){
			if (guia.isGuiaCoopanest())
				throw new ValidateException(MensagemErroEnumSR.DATA_DE_MARCACAO_COM_PRAZO_ULTRAPASSADO.getMessage(guia.getAutorizacao(),"90"));
			else
				throw new ValidateException(MensagemErroEnumSR.DATA_DE_MARCACAO_COM_PRAZO_ULTRAPASSADO.getMessage(guia.getAutorizacao(),"60"));
		}
		return super.selecionarGuia(guia);
	}

	public ResumoGuias<GuiaExame> buscarGuiasConfirmacao(String numeroDoCartao,String cpfDoTitular, Prestador prestador) throws Exception {
		List<GuiaSimples> guiasEncontradas =  (List<GuiaSimples>) BuscarSegurados.buscar(cpfDoTitular, numeroDoCartao, Segurado.class).getGuias();
		isNotEmpty(guiasEncontradas, "Não existem guias para esse segurado!");
		List<GuiaExame> guiasAgendadas = new ArrayList<GuiaExame>();
		
		for (GuiaSimples guiaAtual : guiasEncontradas) {
			if(guiaAtual.isSituacaoAtual(AGENDADA.descricao()) && guiaAtual.isExame()){
				if (guiaAtual.getPrestador().equals(prestador) || 
						guiaAtual.getSituacao().getUsuario().isPossuiRole(UsuarioInterface.ROLE_CENTRAL_DE_SERVICO)) {
					guiasAgendadas.add((GuiaExame) guiaAtual);
				}
			}
		}
		
		isNotEmpty(guiasAgendadas,"Não existem guias desse segurado para serem confirmadas!");
		return new ResumoGuias(guiasAgendadas,AGENDADA.descricao(),false);
	}
	
	public void confirmarGuiaDeExame(GuiaExame<Procedimento> guiaAntiga, GuiaExame<Procedimento> guiaNova, UsuarioInterface usuario) throws Exception {
		for (Procedimento procedimento : guiaAntiga.getProcedimentos()) {
			if(!super.geraCoParticipacao(procedimento)){
				procedimento.setGeraCoParticipacao(false);
				procedimento.setValorCoParticipacao(BigDecimal.ZERO);
			}
		}
		guiaAntiga.updateValorCoparticipacao();
		guiaNova.updateValorCoparticipacao();
		super.confirmarGuiaDeExame(guiaAntiga, guiaNova, usuario);
		/* if[SENSE_MANAGER] */
		SenseManager.CONFIRMACAO_EXAME.analisar(guiaNova);
		/* end[SENSE_MANAGER] */
	}
	
	public static final String procedimento_1 = "90000001";
	public static final String procedimento_2 = "90000002";
	//CODIGOS_CBHPM_ANESTESISTA
	
	public GuiaSimples<Procedimento> buscarGuiasConfirmacaoCoopanest(String autorizacao) throws Exception {
		if (Utils.isStringVazia(autorizacao.trim()))
			throw new ValidateException(MensagemErroEnum.NUMERO_AUTORIZACAO_INVALIDO.getMessage());
		GuiaExame<Procedimento> guiaEncontrada = super.buscarGuias(autorizacao, GuiaExame.class, null, SituacaoEnum.AUTORIZADO,SituacaoEnum.AGENDADA);
		
		Boolean achouProcedimento1 = Boolean.FALSE;
		Boolean achouProcedimento2 = Boolean.FALSE;
		
		if(guiaEncontrada != null) {
			guiaEncontrada.tocarObjetos();
			for (Procedimento procedimento : guiaEncontrada.getProcedimentos()) {
				if(procedimento.getProcedimentoDaTabelaCBHPM().getCodigo().equals(procedimento_1)) {
					achouProcedimento1 = Boolean.TRUE;
					procedimento.setGeraCoParticipacao(Boolean.FALSE);
				}else if(procedimento.getProcedimentoDaTabelaCBHPM().getCodigo().equals(procedimento_2)) {
					procedimento.setGeraCoParticipacao(Boolean.FALSE);
					achouProcedimento2 = Boolean.TRUE;
				}
			}
			
			if(achouProcedimento1 || achouProcedimento2) {
				return guiaEncontrada;
			}else {
				throw new RuntimeException(MensagemErroEnum.GUIA_NAO_PODE_SER_CONFIRMADA.getMessage(guiaEncontrada.getAutorizacao()));
			}
		}else {
			throw new RuntimeException(MensagemErroEnum.GUIA_NAO_PODE_SER_CONFIRMADA.getMessage(guiaEncontrada.getAutorizacao()));
		}
	}
	
	public GuiaExame<Procedimento> selecionarProcedimentos(
			Collection<Procedimento> procedimentos, String numeroDeRegistro,
			GuiaExame<Procedimento> guiaAntiga, UsuarioInterface usuario,
			Prestador prestador) throws Exception {
		
		GuiaExame<Procedimento> guiaNova = super.selecionarProcedimentos(procedimentos, guiaAntiga, usuario,prestador);
		
		guiaNova.setNumeroDeRegistro(numeroDeRegistro);
		return guiaNova;
	}

}
