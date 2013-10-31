package br.com.infowaypi.ecare.services;

import static br.com.infowaypi.ecarebc.atendimentos.ResumoGuias.SITUACAO_TODAS;
import static br.com.infowaypi.ecarebc.constantes.Constantes.TIPO_GUIA_ACOMPANHAMENTO_ANESTESICO;
import static br.com.infowaypi.ecarebc.constantes.Constantes.TIPO_GUIA_ATENDIMENTO_SUBSEQUENTE;
import static br.com.infowaypi.ecarebc.constantes.Constantes.TIPO_GUIA_CIRURGIA_ODONTO;
import static br.com.infowaypi.ecarebc.constantes.Constantes.TIPO_GUIA_CONSULTA;
import static br.com.infowaypi.ecarebc.constantes.Constantes.TIPO_GUIA_CONSULTA_ODONTO;
import static br.com.infowaypi.ecarebc.constantes.Constantes.TIPO_GUIA_CONSULTA_ODONTO_URGENCIA;
import static br.com.infowaypi.ecarebc.constantes.Constantes.TIPO_GUIA_CONSULTA_URGENCIA;
import static br.com.infowaypi.ecarebc.constantes.Constantes.TIPO_GUIA_EXAME;
import static br.com.infowaypi.ecarebc.constantes.Constantes.TIPO_GUIA_HONORARIO_MEDICO;
import static br.com.infowaypi.ecarebc.constantes.Constantes.TIPO_GUIA_INTERNACAO_ELETIVA;
import static br.com.infowaypi.ecarebc.constantes.Constantes.TIPO_GUIA_INTERNACAO_URGENCIA;
import static br.com.infowaypi.ecarebc.constantes.Constantes.TIPO_GUIA_RECURSO_GLOSA;
import static br.com.infowaypi.ecarebc.constantes.Constantes.TIPO_GUIA_TODAS_INTERNACAO;
import static br.com.infowaypi.ecarebc.constantes.Constantes.TIPO_GUIA_TRATAMENTO_ODONTO;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import br.com.infowaypi.ecare.services.recurso.GuiaRecursoGlosa;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaAcompanhamentoAnestesico;
import br.com.infowaypi.ecarebc.atendimentos.GuiaAtendimentoUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCirurgiaOdonto;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsulta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaOdonto;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaOdontoUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExameOdonto;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacaoEletiva;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacaoUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GuiaHonorarioMedico;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.GuiaFaturavel;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.molecular.parameter.OR;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author Idelvane Santana
 * @changes Danilo Nogueira Portela Service utilizado na construção do relatório de guias.
 * @changes Luciano Rocha Foi alterada a validação das datas.
 * @changes Luciano Rocha Intervalo da busca de 90 dias para o Uniplam, com corte do Munge.
 */
@SuppressWarnings("unchecked")
public class RelatorioGuias extends Service {
	
	/**
	 * Esse método é utilizado no listagem de guas do Root. Ele possui mais parametros que o listagem de guias do prestador.
	 * @param <G>
	 * @param dataInicial
	 * @param dataFinal
	 * @param exibirGuias
	 * @param prestador
	 * @param tipoDeGuia
	 * @param tipoDeOperador
	 * @param permanencia
	 * @param situacao
	 * @param tipoDeData
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <G extends GuiaFaturavel> ResumoGuias buscarGuias(String dataInicial, String dataFinal, Boolean exibirGuias, Prestador prestador, Profissional profissional, Boolean responsavel, Boolean solicitante, TabelaCBHPM procedimento, Integer tipoDeGuia, Integer tipoDeOperador, Integer permanencia, String situacao, Integer tipoDeData) throws Exception {
		
		if (prestador == null && Utils.isStringVazia(dataInicial) && Utils.isStringVazia(dataFinal)){
			throw new Exception(MensagemErroEnum.AUSENCIA_DE_PARAMETROS.getMessage());
		}
		
		Date dataInicialFormatada = Utils.parse(dataInicial);
		Date dataFinalFormatada = Utils.parse(dataFinal);

		Calendar dataInicalCalendar = Calendar.getInstance();
		Calendar dataFinalCalendar = Calendar.getInstance();

		dataInicalCalendar.setTime(dataInicialFormatada);
		dataFinalCalendar.setTime(dataFinalFormatada);

		if (dataFinal != null) {
			dataFinalFormatada = Utils.parse(dataFinal);
		}
		
		if (dataInicialFormatada.after(new Date())) {
			throw new ValidateException("A data inicial não pode ser maior que a data de hoje.");
		}
		
		if (dataFinalFormatada.before(dataInicialFormatada)) {
			throw new ValidateException("A data final não pode ser maior que a inicial.");
		}
		
		/* if[LISTAGEM_GUIAS_90_DIAS]
		if ((Utils.diferencaEmDias(dataInicalCalendar, dataFinalCalendar)) > Service.LIMITE_DE_DIAS_UNIPLAM){ 
			throw new ValidateException(MensagemErroEnum.LIMITE_DIAS_ULTRAPASSADO_UNIPLAM.getMessage());
		}
		else[LISTAGEM_GUIAS_90_DIAS]*/
		if ((Utils.diferencaEmDias(dataInicalCalendar, dataFinalCalendar)) > Service.LIMITE_DE_DIAS){ 
			throw new ValidateException(MensagemErroEnum.LIMITE_DIAS_ULTRAPASSADO.getMessage());
		}
		/* end[LISTAGEM_GUIAS_90_DIAS]*/
		
		SearchAgent sa = new SearchAgent();

		if (situacao == null || situacao.equals(SITUACAO_TODAS)){
			sa = super.getNotSearchSituacoes(SituacaoEnum.REALIZADO);
		}else {
			sa.addParameter(new In("situacao.descricao", situacao));
		}
		
		if(profissional != null){
			
			if(responsavel == false && solicitante == false)
				throw new RuntimeException(MensagemErroEnum.PROFISSIONAL_PELO_MENOS_UMA_OPCAO.getMessage());
			
			if(responsavel && solicitante){
				sa.addParameter(new OR(new Equals("profissional", profissional), new Equals("solicitante", profissional)));
			}else if(responsavel){
				sa.addParameter(new Equals("profissional", profissional));
			}else if(solicitante){
				sa.addParameter(new Equals("solicitante", profissional));
			}
		}
		
		if (procedimento != null) {
			sa.addParameter(new Equals("procedimentos.procedimentoDaTabelaCBHPM", procedimento));
			sa.addParameter(new In("procedimentos.situacao.descricao", "Agendado(a)", "Ativo(a)", "Autorizado(a)", "Confirmado(a)", "Faturado(a)", "Fechado(a)", "Honorário Gerado", "Pré-autorizado", "Realizado(a)", "Solicitado(a)"));
		}
		
		Class tipoGuiaClass = this.getKlass(tipoDeGuia);
		List<G> guias = super.buscarGuias(sa, "", null, dataInicial, dataFinal, prestador, false, false, tipoGuiaClass, tipoDeData);
		
		boolean isClasseConsulta = tipoGuiaClass.equals(GuiaConsulta.class);
		boolean isClasseConsultaOdonto = tipoGuiaClass.equals(GuiaConsultaOdonto.class);
		
		if (isClasseConsulta || isClasseConsultaOdonto ){
			this.filtrarGuiasResgatadasPolimorificamente(guias, isClasseConsulta);
		}
		
		ResumoGuias<G> resumo = new ResumoGuias<G>(guias, situacao, true, procedimento);
		
		if (prestador != null)
			prestador.getPessoaJuridica();
			
		resumo.setParametrosDeBuscaDaListagemDeGuia(dataInicialFormatada, dataFinalFormatada,
				exibirGuias, prestador, profissional, responsavel, solicitante, procedimento,
				getTipoDeGuia(tipoDeGuia), situacao, tipoDeData);
		
		if(resumo.getGuiasFiltradas().isEmpty())
			throw new RuntimeException(MensagemErroEnum.NENHUMA_GUIA_ENCONTRADA.getMessage());
		
		for (G guia : resumo.getGuias()) {
			if(guia instanceof GuiaSimples){
				((GuiaSimples) guia).getSegurado().getPessoaFisica().getNome();
				if (((GuiaSimples) guia).getGuiaOrigem() != null) {
					((GuiaSimples) guia).getGuiaOrigem().getAutorizacao();
				}
			}
			else if (guia instanceof GuiaRecursoGlosa){
				((GuiaRecursoGlosa) guia).getSegurado().getPessoaFisica().getNome();
				if (((GuiaRecursoGlosa) guia).getGuiaOrigem() != null) {
					((GuiaRecursoGlosa) guia).getGuiaOrigem().getAutorizacao();
				}
			} 
			if(guia.getPrestador() != null) {
				guia.getPrestador().getNome();
			}
			if (guia.isCompleta()) {
				((GuiaCompleta) guia).getItensDiaria().size();
			}
		}
		
		return resumo;
	} 
	
	/*
	 * Método inserido aqui para resolver o problema da busca polimorfica das classes de GuiaConsulta e GuiaConsultaOndonto. Ao contrário do que 
	 * diz a hierarquia de classes, objetos das subclasses destes dois tipos de guias, não devem ser retornados em uma busca.
	 */
	private <G extends GuiaFaturavel> void filtrarGuiasResgatadasPolimorificamente(List<G> guias, boolean isConsulta) {
		if (isConsulta){
			for (Iterator iterator = guias.iterator(); iterator.hasNext();) {
				G guia = (G) iterator.next();
				
				if (guia.isConsultaOdonto() || guia.isConsultaOdontoUrgencia()){
					iterator.remove();
				}
			}
		}else{
			for (Iterator iterator = guias.iterator(); iterator.hasNext();) {
				G guia = (G) iterator.next();
				
				if (guia.isConsultaOdontoUrgencia()){
					iterator.remove();
				}
			}
		}
	}
	
	

	public <G extends GuiaFaturavel> ResumoGuias buscarGuias(String dataInicial, String dataFinal, Boolean exibirGuias, Prestador prestador, Profissional profissional, Boolean responsavel, Boolean solicitante, TabelaCBHPM procedimento, Integer tipoDeGuia, String situacao, Integer tipoDeData) throws Exception {
		
		Integer tipoDeOperador = null;
		Integer permanencia = null;
		return buscarGuias(dataInicial, dataFinal, exibirGuias, prestador, profissional, responsavel, solicitante, procedimento, tipoDeGuia, tipoDeOperador, permanencia, situacao, tipoDeData);
	}

	/**
	 * Retorna um <code>Class</code> a partir do tipo de guia informado.
	 * 
	 * @param <G>
	 * @param tipoDeGuia
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public <G extends GuiaFaturavel> Class getKlass(Integer tipoDeGuia) {
		switch (tipoDeGuia) {
			case TIPO_GUIA_CONSULTA: {
				return GuiaConsulta.class;
			}
			case TIPO_GUIA_CONSULTA_URGENCIA: {
				return GuiaConsultaUrgencia.class;
			}
			case TIPO_GUIA_CONSULTA_ODONTO: {
				return GuiaConsultaOdonto.class;
			}
			case TIPO_GUIA_CONSULTA_ODONTO_URGENCIA: {
				return GuiaConsultaOdontoUrgencia.class;	
			}
			case TIPO_GUIA_ATENDIMENTO_SUBSEQUENTE: {
				return GuiaAtendimentoUrgencia.class;
			}
			case TIPO_GUIA_EXAME: {
				return GuiaExame.class;
			}
			case TIPO_GUIA_TRATAMENTO_ODONTO: {
				return GuiaExameOdonto.class;
			}
			case TIPO_GUIA_CIRURGIA_ODONTO: {
				return GuiaCirurgiaOdonto.class;
			}
			case TIPO_GUIA_INTERNACAO_ELETIVA: {
				return GuiaInternacaoEletiva.class;
			}
			case TIPO_GUIA_INTERNACAO_URGENCIA: {
				return GuiaInternacaoUrgencia.class;
			}
			case TIPO_GUIA_TODAS_INTERNACAO: {
				return GuiaInternacao.class;
			}
			case TIPO_GUIA_HONORARIO_MEDICO: {
				return GuiaHonorarioMedico.class;
			}
			case TIPO_GUIA_ACOMPANHAMENTO_ANESTESICO: {
				return GuiaAcompanhamentoAnestesico.class;
			}
			case TIPO_GUIA_RECURSO_GLOSA: {
				return GuiaRecursoGlosa.class;
			}
			default: {
				return GuiaFaturavel.class;
			}
		}
	}
	
	/**
	 * Retorna um String partir do tipo de guia informado.
	 * @param tipoDeGuia
	 */
	public String getTipoDeGuia(Integer tipoDeGuia) {
		switch (tipoDeGuia) {
		case TIPO_GUIA_CONSULTA: {
			return "Consulta";
		}
		case TIPO_GUIA_CONSULTA_URGENCIA: {
			return "Consulta Urgência";
		}
		case TIPO_GUIA_CONSULTA_ODONTO: {
			return "Consulta Odonto";
		}
		case TIPO_GUIA_CONSULTA_ODONTO_URGENCIA: {
			return "Consulta Odonto Urgência";
		}
		case TIPO_GUIA_ATENDIMENTO_SUBSEQUENTE: {
			return "Atendimeto Subsequente";
		}
		case TIPO_GUIA_EXAME: {
			return "Exame";
		}
		case TIPO_GUIA_TRATAMENTO_ODONTO: {
			return "Tratamento Odonto";
		}
		case TIPO_GUIA_CIRURGIA_ODONTO: {
			return "Cirurgia Odonto";
		}
		case TIPO_GUIA_INTERNACAO_ELETIVA: {
			return "Internação Eletiva";
		}
		case TIPO_GUIA_INTERNACAO_URGENCIA: {
			return "Internação Urgência";
		}
		case TIPO_GUIA_TODAS_INTERNACAO: {
			return "Todas Internações";
		}
		case TIPO_GUIA_HONORARIO_MEDICO: {
			return "Honorário Médico";
		}
		case TIPO_GUIA_ACOMPANHAMENTO_ANESTESICO: {
			return "Acompanhamento Anestésico";
		}
		case TIPO_GUIA_RECURSO_GLOSA: {
			return "Recurso de Glosa";
		}
		default: {
			return "Todas";
		}
		}
	}
}
