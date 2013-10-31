package br.com.infowaypi.ecarebc.service;

import static br.com.infowaypi.ecarebc.constantes.Constantes.TIPO_GUIA_ATENDIMENTO_SUBSEQUENTE;
import static br.com.infowaypi.ecarebc.constantes.Constantes.TIPO_GUIA_CONSULTA;
import static br.com.infowaypi.ecarebc.constantes.Constantes.TIPO_GUIA_CONSULTA_ODONTO;
import static br.com.infowaypi.ecarebc.constantes.Constantes.TIPO_GUIA_CONSULTA_ODONTO_URGENCIA;
import static br.com.infowaypi.ecarebc.constantes.Constantes.TIPO_GUIA_CONSULTA_URGENCIA;
import static br.com.infowaypi.ecarebc.constantes.Constantes.TIPO_GUIA_EXAME;
import static br.com.infowaypi.ecarebc.constantes.Constantes.TIPO_GUIA_INTERNACAO_ELETIVA;
import static br.com.infowaypi.ecarebc.constantes.Constantes.TIPO_GUIA_INTERNACAO_URGENCIA;
import static br.com.infowaypi.ecarebc.constantes.Constantes.TIPO_GUIA_RECURSO_GLOSA;
import static br.com.infowaypi.ecarebc.constantes.Constantes.TIPO_GUIA_TODAS_INTERNACAO;
import static br.com.infowaypi.ecarebc.constantes.Constantes.TIPO_GUIA_TRATAMENTO_ODONTO;
import static br.com.infowaypi.msr.utils.Assert.isNotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecare.services.recurso.GuiaRecursoGlosa;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaAcompanhamentoAnestesico;
import br.com.infowaypi.ecarebc.atendimentos.GuiaAtendimentoUrgencia;
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
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.GuiaFaturavel;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.ecarebc.utils.TocarObjetoUtils;
import br.com.infowaypi.ecarebc.utils.TocarObjetoUtils.TocarObjetoEnum;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.molecular.parameter.Like;
import br.com.infowaypi.molecular.parameter.LowerEquals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

@SuppressWarnings("unchecked")
public class ReimpressaoGuiasService<S extends SeguradoInterface> extends Service {
	
	public ResumoGuias<GuiaFaturavel> buscarGuiasReimpressao(Collection<S>segurados,Prestador prestador, UsuarioInterface usuario) throws Exception{
		SearchAgent sa = new SearchAgent();
		sa = getSearchSegurados(sa,segurados);
		List<GuiaFaturavel> guias = buscarGuiasReimpressaoNovo(sa,"", null, "", "", prestador, false,false, GuiaFaturavel.class, null);
		return new ResumoGuias<GuiaFaturavel>(guias,ResumoGuias.SITUACAO_TODAS,true);
	}
	
	public GuiaFaturavel buscarGuiasReimpressao(String autorizacao, Prestador prestador) throws Exception{
		if (Utils.isStringVazia(autorizacao))
			throw new ValidateException(MensagemErroEnum.NUMERO_AUTORIZACAO_INVALIDO.getMessage());
		GuiaFaturavel guia = this.buscarGuiasReimpressaoGAA(autorizacao, prestador);
		if (guia == null) {
			guia = buscarGuiasNovo(autorizacao, prestador,false, GuiaFaturavel.class);
		}
		Assert.isNotNull(guia, "Guia não encontrada!");
		if (guia instanceof GuiaSimples) {
			this.tocarObjeto((GuiaSimples) guia);
		}
		return guia;
	}
	
	/**
	 * Método que busca uma gaa.
	 * @author Luciano Rocha
	 * @param autorizacao
	 * @param prestador
	 * @return
	 * @since 03/12/2012
	 */
	private GuiaFaturavel buscarGuiasReimpressaoGAA(String autorizacao, Prestador prestador) {
		
		SearchAgent sa = new SearchAgent();
		
		sa.addParameter(new Equals("autorizacao", autorizacao));
		
		GuiaFaturavel guia =  sa.uniqueResult(GuiaAcompanhamentoAnestesico.class);
		
		if (guia != null && guia.isAcompanhamentoAnestesico()) {
			System.out.println(verificaGuiaOrigemGAA(sa, guia, prestador));
			if (verificaGuiaOrigemGAA(sa, guia, prestador)) {
				return guia;
			}
		}
		
		return null;
	}

	/**
	 * Método que verifica se a gaa foi gerada de alguma guiaOrigem do prestador.
	 * @author Luciano Rocha
	 * @param sa
	 * @param guia
	 * @return
	 * @since 03/12/2012
	 */
	public boolean verificaGuiaOrigemGAA(SearchAgent sa, GuiaFaturavel guia, Prestador prestador) {
		sa.clearAllParameters();
		
		sa.addParameter(new Equals("autorizacao", ((GuiaAcompanhamentoAnestesico)guia).getGuiaOrigem().getAutorizacao()));
		
		GuiaFaturavel guiaOrigem = sa.uniqueResult(GuiaFaturavel.class);
		
		if (guiaOrigem != null){
			boolean ehGAADeGuiaOrigemDoPrestador = false;
			if(guiaOrigem.getPrestador() == null){
				ehGAADeGuiaOrigemDoPrestador = ((GuiaSimples)guiaOrigem).getGuiaOrigem().getPrestador().equals(prestador);	
			}
			else {
				ehGAADeGuiaOrigemDoPrestador = guiaOrigem.getPrestador().equals(prestador);
			}
			if(ehGAADeGuiaOrigemDoPrestador){
				return true;
			}
		}
		
		return false;
	}

	public GuiaFaturavel buscarGuiasReimpressaoAnestesista(String autorizacao, Prestador prestador) throws Exception{
		if (Utils.isStringVazia(autorizacao)){
			throw new ValidateException(MensagemErroEnum.NUMERO_AUTORIZACAO_INVALIDO.getMessage());
		}
		GuiaFaturavel guia = this.buscarGuiaPrestadorAnestesista(getSearchAgent(),prestador,autorizacao.trim(), GuiaFaturavel.class) ;
		System.out.println(guia);
		if (guia ==null){
			guia = this.buscarGuiasInternacaoPrestadorAnestesista(getSearchAgent(),autorizacao.trim(), GuiaInternacao.class);
		}
		Assert.isNotNull(guia, "Guia não encontrada!");
		if (guia instanceof GuiaSimples) {
			this.tocarObjeto((GuiaSimples) guia);
		}
		return guia;
	}
	
	private GuiaFaturavel buscarGuiaPrestadorAnestesista(SearchAgent sa,Prestador prestador, String autorizacao, Class<GuiaFaturavel> classe) {
		sa.addParameter(new Equals("autorizacao", autorizacao.trim()));
		GuiaFaturavel guia = sa.uniqueResult(classe);
		return guia;
	}

	public  <G extends GuiaFaturavel> G buscarGuiasInternacaoPrestadorAnestesista(SearchAgent sa,String autorizacao,Class<G> classe){
		sa.addParameter(new Equals("autorizacao", autorizacao.trim()));
		G guia = sa.uniqueResult(classe);
		return guia;
	}

	public void tocarObjeto(GuiaSimples guiaSimples){
		if (guiaSimples.isCompleta()){
			GuiaCompleta guiaCompleta = (GuiaCompleta) guiaSimples;
			TocarObjetoUtils.tocarObjeto(guiaCompleta,
					TocarObjetoEnum.CID_GUIA, TocarObjetoEnum.OBSERVACOES_GUIA,
					TocarObjetoEnum.ITENS_PACOTE_GUIA, TocarObjetoEnum.ITENS_GASOTERAPIA_GUIA,
					TocarObjetoEnum.ITENS_TAXA_GUIA, TocarObjetoEnum.PROCEDIMENTOS_GUIA);
		} else {
			guiaSimples.tocarObjetos();
		}
	}
	
	public List<GuiaFaturavel> buscarGuiasReimpressao(String autorizacao) throws Exception{
		if (Utils.isStringVazia(autorizacao))
			throw new ValidateException(MensagemErroEnum.NUMERO_AUTORIZACAO_INVALIDO.getMessage());
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("autorizacao", autorizacao.trim()));
		List<GuiaFaturavel> guias = sa.list(GuiaFaturavel.class);
		Assert.isNotEmpty(guias, "Guia não encontrada!");
		return guias;
	}
	
	/**
	 * Método que faz a busca das guias para reimpressão pelo prestador anestesista.
	 * @author Luciano Rocha
	 * @param autorizacao
	 * @return
	 * @throws Exception
	 */
	public List<GuiaFaturavel> buscarGuiasReimpressaoPrestadorAnestesista(String autorizacao) throws Exception{
		List<GuiaFaturavel> guiasFiltradas = buscaGuiasPrestadorAnestesista(autorizacao);
		
		if(guiasFiltradas.size() == 0){
			guiasFiltradas = buscaPrestadorAnestesistaAlternativa(autorizacao);
		}
		
		if(guiasFiltradas.size() == 0){
			GuiaFaturavel guia = this.buscarGuiasReimpressaoAnestesista(autorizacao, null);
			if(guia != null){
				guiasFiltradas.add(guia);
			}
		}
		
		Assert.isNotEmpty(guiasFiltradas, "Guia não encontrada!");
		return guiasFiltradas;
	}

	/**
	 * Método que faz uma busca alternativa para prestador anestesista. Faz uma busca pela guia de origem.
	 * @author Luciano Rocha
	 * @param autorizacao
	 * @return {@link List}
	 */
	public List<GuiaFaturavel> buscaPrestadorAnestesistaAlternativa(String autorizacao) {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("autorizacao", autorizacao));
		
		List<GuiaFaturavel> guias = sa.list(GuiaFaturavel.class);
		
		List<GuiaFaturavel> guiasFiltradas = new ArrayList<GuiaFaturavel>();
		for (GuiaFaturavel guia : guias) {
			boolean temGuiaAcompanhamentoAnestesico = verificaGuiasFilhas(guia);
			if (guia.isExame() && temGuiaAcompanhamentoAnestesico) {
				guiasFiltradas.add(guia);
			}
		}
		
		return guiasFiltradas;
	}

	/**
	 * Método que verifica se a guia possui alguma guiaFilha que seja de acompanhamento anestésico.
	 * @author Luciano Rocha
	 * @param guia
	 * @return
	 */
	private boolean verificaGuiasFilhas(GuiaFaturavel guia) {
		if (guia.isSimples()) {
			Set<GuiaFaturavel> guiasFilhas = ((GuiaSimples)guia).getGuiasFilhas();
			for (GuiaFaturavel guiaFilha : guiasFilhas) {
				if(guiaFilha.isAcompanhamentoAnestesico()){
					return true;
				}
			}
		}
		
		return false;
	}

	/**
	 * Método extraído da busca para reimpressão de guias pelo prestador anestesista.
	 * @author Luciano Rocha
	 * @param autorizacao
	 * @return
	 * @throws ValidateException
	 */
	private List<GuiaFaturavel> buscaGuiasPrestadorAnestesista(String autorizacao) throws ValidateException {
		if (Utils.isStringVazia(autorizacao)){
			throw new ValidateException(MensagemErroEnum.NUMERO_AUTORIZACAO_INVALIDO.getMessage());
		}
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("autorizacao", autorizacao));
		
		List<GuiaFaturavel> guias = sa.list(GuiaFaturavel.class);
		
		List<GuiaFaturavel> guiasFiltradas = new ArrayList<GuiaFaturavel>();
		for (GuiaFaturavel guia : guias) {
			if (guia.isInternacao()) {
				guiasFiltradas.add(guia);
			}
		}
		
		return guiasFiltradas;
	}
	
	public ResumoGuias<GuiaFaturavel> buscarGuiasReimpressao(Collection<S>segurados,UsuarioInterface usuario) throws Exception{
		SearchAgent sa = new SearchAgent();
		sa = getSearchSegurados(sa,segurados);
		List<GuiaFaturavel> guias = sa.list(GuiaFaturavel.class);
		return new ResumoGuias<GuiaFaturavel>(guias,ResumoGuias.SITUACAO_TODAS,true);
	}
	
	@SuppressWarnings("unchecked")
	public GuiaFaturavel selecionarGuia(GuiaFaturavel guia) throws Exception {
		return selecionarGuiaReimpressao(guia);
	}
	
	protected <G extends GuiaFaturavel> G selecionarGuiaReimpressao(G guia) throws Exception {
		isNotNull(guia, "Nenhuma guia foi selecionada!");
		if(guia instanceof GuiaSimples){
			((GuiaSimples) guia).tocarObjetos();
		}
		return guia;
	}
	
	public ResumoGuias<GuiaFaturavel> buscarGuiasReimpressao(Collection<S> segurados , UsuarioInterface usuario, Date dataInicial, Date dataFinal, Integer tipoDeGuia) {
		SearchAgent sa = new SearchAgent();
		boolean isSeguradosInformados = !segurados.isEmpty();
		boolean isDataInicialInformada = dataInicial != null;
		boolean isDataFinalInformada = dataFinal != null;
		
		if(isDataInicialInformada && dataInicial.after(new Date())) {
			throw new RuntimeException("A Data Inicial informada não pode ser posterior a data atual: "+ Utils.format(new Date(),"dd/MM/yyyy"));
		}
		
		if(isDataFinalInformada && dataFinal.after(new Date())) {
			throw new RuntimeException("A Data Final informada não pode ser posterior a data atual: "+ Utils.format(new Date(),"dd/MM/yyyy"));
		}
		
		if(isDataInicialInformada && isDataFinalInformada && dataInicial.after(dataFinal)) {
			throw new RuntimeException("A Data Inicial informada não pode ser posterior à Data Final informada.");
		}
		
		if(isSeguradosInformados) {
			sa = getSearchSegurados(sa, segurados);
		}
		if(isDataInicialInformada) {
			sa.addParameter(new GreaterEquals("dataMarcacao", dataInicial));
		}
		if(isDataFinalInformada) {
			sa.addParameter(new LowerEquals("dataMarcacao", dataFinal));
		}
		
		List<GuiaFaturavel> guiasEncontradas = new ArrayList<GuiaFaturavel>();
		guiasEncontradas.addAll(sa.list(getKlass(tipoDeGuia)));
		
		if(guiasEncontradas.isEmpty()) {
			throw new RuntimeException("Nenhuma Guia foi encontrada.");
		}
		
		return new ResumoGuias<GuiaFaturavel>(guiasEncontradas, ResumoGuias.SITUACAO_TODAS, true);
	}
	
	public ResumoGuias<GuiaFaturavel> buscarGuiasReimpressao(Collection<S> segurados, Prestador prestador, UsuarioInterface usuario, Date dataInicial, Date dataFinal, Integer tipoDeGuia) {
		SearchAgent sa = new SearchAgent();
		boolean isSeguradosInformados = !segurados.isEmpty();
		boolean isDataInicialInformada = dataInicial != null;
		boolean isDataFinalInformada = dataFinal != null;
		boolean isPrestadorInformado = prestador != null;
		
		if(isDataInicialInformada && dataInicial.after(new Date())) {
			throw new RuntimeException("A Data Inicial informada não pode ser posterior a data atual: "+ Utils.format(new Date(),"dd/MM/yyyy"));
		}
		
		if(isDataFinalInformada && dataFinal.after(new Date())) {
			throw new RuntimeException("A Data Final informada não pode ser posterior a data atual: "+ Utils.format(new Date(),"dd/MM/yyyy"));
		}
		
		if(isDataInicialInformada && isDataFinalInformada && dataInicial.after(dataFinal)) {
			throw new RuntimeException("A Data Inicial informada não pode ser posterior à Data Final informada.");
		}
		
		if(isSeguradosInformados) {
			sa = getSearchSegurados(sa, segurados);
		}
		if(isDataInicialInformada) {
			sa.addParameter(new GreaterEquals("dataMarcacao", dataInicial));
		}
		if(isDataFinalInformada) {
			sa.addParameter(new LowerEquals("dataMarcacao", dataFinal));
		}
		if(isPrestadorInformado) {
			sa.addParameter(new Equals("prestador", prestador));
		}
		
		List<GuiaFaturavel> guiasEncontradas = new ArrayList<GuiaFaturavel>();
		guiasEncontradas.addAll(sa.list(getKlass(tipoDeGuia)));
		
		if(guiasEncontradas.isEmpty()) {
			throw new RuntimeException("Nenhuma Guia foi encontrada.");
		}
		
		return new ResumoGuias<GuiaFaturavel>(guiasEncontradas, ResumoGuias.SITUACAO_TODAS, true);
	}
	
	protected <G extends GuiaFaturavel> List<G> buscarGuiasReimpressaoNovo(SearchAgent sa, String autorizacao, SeguradoInterface segurado, String dataInicial, String dataFinal,Prestador prestador, boolean exigirParametros, boolean incluiPrestadorNulo, Class<G> klassGuia, Integer tipoDeData) throws Exception{
		
		if(exigirParametros && Utils.isStringVazia(autorizacao) && segurado == null  &&
			prestador == null && Utils.isStringVazia(dataInicial) && Utils.isStringVazia(dataFinal)){
			throw new Exception(MensagemErroEnum.AUSENCIA_DE_PARAMETROS.getMessage());
		}
		
		List<G> guiasEncontradas = new ArrayList<G>();
		sa = sa != null ? sa : getSearchAgent();
		if(!Utils.isStringVazia(autorizacao)){
			sa.addParameter(new Like("autorizacao", autorizacao.trim()));
			Criteria criteria = sa.createCriteriaFor(klassGuia);
			
			if(prestador != null){
				if(incluiPrestadorNulo){
					criteria.add(Expression.or(Expression.isNull("prestador"),Expression.eq("prestador", prestador)));
				} else {
					criteria.add(Expression.eq("prestador", prestador));
				}
			}
			guiasEncontradas.addAll(criteria.list());
			
		} else {
			if(tipoDeData != null){
				Calendar dataFinalCalendar = Calendar.getInstance();
				if (!Utils.isStringVazia(dataFinal)){
					dataFinalCalendar.setTime(Utils.parse(dataFinal));
					dataFinalCalendar.set(GregorianCalendar.HOUR_OF_DAY, 23);
					dataFinalCalendar.set(GregorianCalendar.MINUTE, 59);
					dataFinalCalendar.set(GregorianCalendar.SECOND, 59);
				}

				if(tipoDeData.equals(GuiaSimples.DATA_DE_MARCACAO)){
					if (!Utils.isStringVazia(dataInicial))
						sa.addParameter(new GreaterEquals("dataMarcacao", Utils.parse(dataInicial)));

					if (!Utils.isStringVazia(dataFinal))
						sa.addParameter(new LowerEquals("dataMarcacao", dataFinalCalendar.getTime()));
				}

				if(tipoDeData.equals(GuiaSimples.DATA_DE_ATENDIMENTO)){
					if (!Utils.isStringVazia(dataInicial))
						sa.addParameter(new GreaterEquals("dataAtendimento", Utils.parse(dataInicial)));

					if (!Utils.isStringVazia(dataFinal))
						sa.addParameter(new LowerEquals("dataAtendimento", dataFinalCalendar.getTime()));
				}
				if(tipoDeData.equals(GuiaSimples.DATA_DE_SITUACAO)){
					if (!Utils.isStringVazia(dataInicial))
						sa.addParameter(new GreaterEquals("situacao.dataSituacao", Utils.parse(dataInicial)));

					if (!Utils.isStringVazia(dataFinal))
						sa.addParameter(new LowerEquals("situacao.dataSituacao", dataFinalCalendar.getTime()));
				}
				if(tipoDeData.equals(GuiaSimples.DATA_DE_TERMINO)){
					if (!Utils.isStringVazia(dataInicial))
						sa.addParameter(new GreaterEquals("dataTerminoAtendimento", Utils.parse(dataInicial)));

					if (!Utils.isStringVazia(dataFinal))
						sa.addParameter(new LowerEquals("dataTerminoAtendimento", dataFinalCalendar.getTime()));
				}
				if(tipoDeData.equals(GuiaSimples.DATA_DE_RECEBIMENTO)){
					if (!Utils.isStringVazia(dataInicial)) {
						sa.addParameter(new GreaterEquals("dataRecebimento", Utils.parse(dataInicial)));
					}
					if (!Utils.isStringVazia(dataFinal)) {
						sa.addParameter(new LowerEquals("dataRecebimento", dataFinalCalendar.getTime()));
					}
				}	
			}	

			if(segurado != null){
				segurado.addParameterEquals(sa);
			}

			Criteria criteria = sa.createCriteriaFor(klassGuia);

			if (prestador != null){
				if(incluiPrestadorNulo){
					criteria.add(Expression.or(Expression.isNull("prestador"),Expression.eq("prestador", prestador)));
				} else {
					criteria.add(Expression.eq("prestador", prestador));
				}
			}
			
			guiasEncontradas.addAll(criteria.list());
		}
		
		return guiasEncontradas;
	}
	
	protected <G extends GuiaFaturavel> G buscarGuiasNovo (String autorizacao,Prestador prestador,boolean incluiPrestadorNulo, Class<G> klassGuia, SituacaoEnum... situacoesGuia) throws Exception{
		SearchAgent sa = new SearchAgent();
		if (situacoesGuia.length > 0){
			sa = getSearchSituacoes(situacoesGuia);
		}
		
		List<G> guias = buscarGuiasReimpressaoNovo(sa,autorizacao.trim(), null, "", "", prestador, true,incluiPrestadorNulo, klassGuia, null);
		Assert.isNotEmpty(guias, "Nenhuma guia foi encontrada.");
		return guias.get(0);
		
	}

	/**
	 * Retorna um <code>Class</code> a partir do tipo de guia informado.
	 * 
	 * @param <G>
	 * @param tipoDeGuia
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <G extends GuiaFaturavel> Class getKlass(Integer tipoDeGuia) {
		switch (tipoDeGuia) {
		case TIPO_GUIA_CONSULTA:
			return GuiaConsulta.class;
		case TIPO_GUIA_CONSULTA_URGENCIA:
			return GuiaConsultaUrgencia.class;
		case TIPO_GUIA_CONSULTA_ODONTO:
			return GuiaConsultaOdonto.class;
		case TIPO_GUIA_CONSULTA_ODONTO_URGENCIA:
			return GuiaConsultaOdontoUrgencia.class;	
		case TIPO_GUIA_ATENDIMENTO_SUBSEQUENTE:
			return GuiaAtendimentoUrgencia.class;
		case TIPO_GUIA_EXAME:
			return GuiaExame.class;
		case TIPO_GUIA_TRATAMENTO_ODONTO:
			return GuiaExameOdonto.class;
		case TIPO_GUIA_INTERNACAO_ELETIVA:
			return GuiaInternacaoEletiva.class;
		case TIPO_GUIA_INTERNACAO_URGENCIA:
			return GuiaInternacaoUrgencia.class;
		case TIPO_GUIA_TODAS_INTERNACAO:
			return GuiaInternacao.class;
		case TIPO_GUIA_RECURSO_GLOSA:
			return GuiaRecursoGlosa.class;
		default:
			return GuiaFaturavel.class;
		}
	}
	
}

