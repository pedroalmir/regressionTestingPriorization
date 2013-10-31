package br.com.infowaypi.ecarebc.service;

import static br.com.infowaypi.msr.utils.Assert.isNotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;

import br.com.infowaypi.ecare.services.recurso.GuiaRecursoGlosa;
import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsulta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.Observacao;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.ecarebc.promocao.PromocaoConsulta;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.molecular.parameter.Like;
import br.com.infowaypi.molecular.parameter.LowerEquals;
import br.com.infowaypi.molecular.parameter.NotEquals;
import br.com.infowaypi.molecular.parameter.NotIn;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe pai para a realização de um serviço no módulo e-Care.
 * @author root
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class Service {
	
	public static final int LIMITE_DE_DIAS = 31;
	public static final int LIMITE_DE_DIAS_UNIPLAM = 90;
	
	private SearchAgent sa;
	
	public List<Prestador> getPrestadores() {
		SearchAgent sa = getSearchAgent();
		sa.addParameter(new Equals("situacao.descricao", SituacaoEnum.ATIVO.descricao()));
		List<Prestador> prestadoresAtivos = sa.list(Prestador.class);
		return prestadoresAtivos;
	}
	
	protected SearchAgent getSearchAgent() {
		if (sa == null){
			sa = new SearchAgent();
		}
		sa.clearAllParameters();
		return sa;
	}
	
	protected <G extends GuiaSimples> G selecionarGuia(G guia) throws Exception {
		isNotNull(guia, "Nenhuma guia foi selecionada!");
		guia.tocarObjetos();
		return guia;
	}
	
	public <G extends GuiaSimples> void salvarGuia(G guia) throws Exception{
		this.salvarGuia(guia, true, true);
	}
	
	/**
	 * Método pai para atualização de guias no bd
	 * @param <G>
	 * @param guia
	 * @throws Exception
	 */
	public <G extends GuiaSimples> void salvarGuia(G guia, Boolean atualizarQuantidades, Boolean atualizarCustos) throws Exception{
		isNotNull(guia,"Guia Inválida!");
		
		gambyForNonUniqueObjectException();
		
		/* if[NAO_OBRIGATORIEDADE_DE_INFORMAR_PROFISSIONAL_NO_ATENDIMENTO]*/
		if(guia.isConsultaUrgencia() || guia.isAtendimentoUrgencia()){
			setarProfissinalSolicitanteEmGuiasDeExameSemProfissionalInformado((GuiaCompleta)guia);
		}
		/* end[NAO_OBRIGATORIEDADE_DE_INFORMAR_PROFISSIONAL_NO_ATENDIMENTO]*/

		//Incrementa consumos de prestador e de segurado
		Prestador prestador = guia.getPrestador();
		SeguradoInterface segurado = guia.getSegurado();
		if (guia.isSituacaoAtual(SituacaoEnum.CONFIRMADO.descricao()) || guia.isSituacaoAtual(SituacaoEnum.FECHADO.descricao())){
			if (prestador != null) {
				prestador.incrementarConsumoFinanceiro(guia);
				ImplDAO.save(guia.getPrestador().getConsumoIndividual());
			}
			if (segurado != null) {
				segurado.incrementarLimites(guia);
				ImplDAO.save(guia.getSegurado().getConsumoIndividual());
			}
			
		}else if (guia.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())){
			if (prestador != null && (!guia.getSituacaoAnterior(guia.getSituacao()).getDescricao().equals(SituacaoEnum.AUTORIZADO.descricao()) || !guia.isFromPrestador())){
				prestador.decrementarConsumoFinanceiro(guia);
				ImplDAO.save(guia.getPrestador().getConsumoIndividual());
			}
			if (segurado != null){
				segurado.decrementarLimites(guia);
				ImplDAO.save(guia.getSegurado().getConsumoIndividual());
			}
		}else if (guia.getIdGuia() == null){
			if (prestador != null && !guia.isFromPrestador()){
				prestador.incrementarConsumoFinanceiro(guia);
				ImplDAO.save(guia.getPrestador().getConsumoIndividual());
			}
			if (segurado != null){
				segurado.incrementarLimites(guia);
				ImplDAO.save(guia.getSegurado().getConsumoIndividual());
			}
		}else{
			if(prestador != null)
				ImplDAO.save(guia.getPrestador().getConsumoIndividual());
			ImplDAO.save(guia.getSegurado().getConsumoIndividual());
		}
		
		if(guia.isInternacao()){
			((GuiaCompleta)guia).setTipoAcomodacao(GuiaCompleta.TIPO_ACOMODACAO_INTERNO);
		}
		
		ImplDAO.save(guia);
		
		if(guia.getAutorizacao() == null){
			guia.setAutorizacao(guia.getIdGuia().toString());
		}
	}
	
	private void setarProfissinalSolicitanteEmGuiasDeExameSemProfissionalInformado(GuiaCompleta guia) throws Exception {
		Profissional profissional = guia.getProfissional() != null? guia.getProfissional() : guia.getSolicitante(); 
		Set<GuiaSimples> guiasExameExterno = guia.getGuiasExameExterno();
		for(GuiaSimples guiaExame : guiasExameExterno){
			if(guiaExame.getSolicitante() == null){
				guiaExame.setSolicitante(profissional);
				ImplDAO.save(guiaExame);
			}
		}
	}
	
	/**
	 * Tentativa de resolver uma NonUniqueObjectException.
	 * Resolveu, mas aceito soluções alternativas.
	 * 
	 * Ass.: Eduardo
	 */
	private void gambyForNonUniqueObjectException() {
		Session currentSession = HibernateUtil.currentSession();
		currentSession.flush();
		currentSession.clear();
	}

	public void gravarObservacoes( Collection<Observacao> observacoes, GuiaExame guia, UsuarioInterface usuario) {
		if (guia.getObservacoes() == null)
			guia.setObservacoes(new HashSet<Observacao>());

		for (Observacao obs : observacoes) {
			obs.setDataDeCriacao(new Date());
			obs.setGuia(guia);
			obs.setUsuario(usuario);
		}
		guia.getObservacoes().addAll(observacoes);
	}
	
	protected <G extends GuiaSimples> void finalizar(G guia) {
	}

	public void finalizar() {
	}
	
	protected <G extends GuiaSimples> List<G> buscarGuias(SearchAgent sa, String autorizacao, SeguradoInterface segurado, String dataInicial, String dataFinal,Prestador prestador, boolean exigirParametros, boolean incluiPrestadorNulo, Class<G> klassGuia, Integer tipoDeData) throws Exception{
		
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

				if(tipoDeData.equals(GuiaSimples.DATA_DE_MARCACAO) && !klassGuia.equals(GuiaRecursoGlosa.class)){
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
	
	protected <G extends GuiaSimples> G buscarGuias(String autorizacao, Class<G> klassGuia,Prestador prestador,SituacaoEnum... situacoesGuia){
		autorizacao = autorizacao.trim();
		sa = new SearchAgent();
		if(situacoesGuia.length > 0){
			sa = getSearchSituacoes(situacoesGuia);
		}
		
		sa.addParameter(new Like("autorizacao", autorizacao));
		Criteria criteria = sa.createCriteriaFor(klassGuia);
		if(prestador != null){
			criteria.add(Expression.eq("prestador", prestador));
		}
		
		List<G> guiasEncontradas = criteria.setMaxResults(1).list();
		Assert.isNotEmpty(guiasEncontradas, "Nenhuma guia foi encontrada!");
		G guia = guiasEncontradas.get(0); 
		guia.tocarObjetos();
		return guia;
	}
	
	protected <G extends GuiaSimples> G buscarGuias(SearchAgent sa, String autorizacao, Class<G> klassGuia, Prestador prestador,boolean incluiPrestadorNulo, SituacaoEnum ...situacaoEnums) throws Exception{
		List<G> guias =  this.buscarGuias(sa, autorizacao, null, "", "", prestador, true,incluiPrestadorNulo, klassGuia,null);
		Assert.isNotEmpty(guias, "Nenhuma guia foi encontrada!");
		G guiaCompleta = guias.get(0);
		return guiaCompleta;
		
	}
	
	protected <G extends GuiaSimples> List<G> buscarGuias (String dataInicial, String dataFinal,SeguradoInterface segurado, Prestador prestador,boolean incluiPrestadorNulo, Class<G> klassGuia, SituacaoEnum... situacoesGuia) throws Exception{
		SearchAgent sa = getSearchAgent();
		if (situacoesGuia.length > 0){
			sa = getSearchSituacoes(situacoesGuia);
		}
		return this.buscarGuias(sa,"", segurado, dataInicial, dataFinal, prestador, true,incluiPrestadorNulo, klassGuia,null);
		
	}

	protected <G extends GuiaSimples> List<G> buscarGuias (SearchAgent sa, Collection<? extends SeguradoInterface> segurados, Prestador prestador,boolean incluiPrestadorNulo, Class<G> klassGuia) throws Exception{
		sa = getSearchSegurados(sa,segurados);
		return this.buscarGuias(sa,"", null, "", "", prestador, true,incluiPrestadorNulo, klassGuia,null);
		
	}

	protected SearchAgent getSearchSegurados(SearchAgent sa, Collection<? extends SeguradoInterface> segurados) {
		sa = sa != null ? sa : getSearchAgent();
		this.addParameterIn(sa,segurados);
		return sa;
	}
	
	public void addParameterIn(SearchAgent sa, Collection<? extends SeguradoInterface> segurados) {
		sa.addParameter(new In("segurado", segurados));
	}

	protected <G extends GuiaSimples> G buscarGuias (String autorizacao,Prestador prestador,boolean incluiPrestadorNulo, Class<G> klassGuia, SituacaoEnum... situacoesGuia) throws Exception{
		if (situacoesGuia.length > 0){
			sa = getSearchSituacoes(situacoesGuia);
		}
		
		List<G> guias = this.buscarGuias(sa,autorizacao.trim(), null, "", "", prestador, true,incluiPrestadorNulo, klassGuia, null);
		Assert.isNotEmpty(guias, "Nenhuma guia foi encontrada.");
		return guias.get(0);
		
	}
	
	protected <G extends GuiaSimples> List<G> buscarGuias(SearchAgent sa, SeguradoInterface segurado, Class<G> klassGuia) throws Exception{
		return this.buscarGuias(sa,"",segurado,"","",null,true,false, klassGuia, null);
	}
	
	protected <G extends GuiaSimples> List<G> buscarGuias(SearchAgent sa, Class<G> klassGuia, SeguradoInterface segurado){
		sa = sa != null ? sa : getSearchAgent();
		
		if(segurado !=null){
			segurado.addParameterEquals(sa);
		}
		
		Criteria criteria = sa.createCriteriaFor(klassGuia);
		
		return criteria.list();
	}
	
	protected <G extends GuiaSimples> List<G> buscarGuias(SearchAgent sa, Prestador prestador,boolean incluiPrestadorNulo, Class<G> klassGuia) throws Exception{
		return this.buscarGuias(sa,"",null,"","",prestador,true,incluiPrestadorNulo, klassGuia, null);
	}

	public <G extends GuiaSimples> List<G> buscarGuias(SeguradoInterface segurado, Class<G> klassGuia, SituacaoEnum... situacoesGuia) throws Exception{
		SearchAgent sa = getSearchAgent();
		if (situacoesGuia.length > 0){
			sa = getSearchSituacoes(situacoesGuia);
		}
			
		return this.buscarGuias(sa,"",segurado,"","",null,true,false, klassGuia, null);
	}
	
	protected <G extends GuiaSimples> List<G> buscarGuias(SeguradoInterface segurado, Prestador prestador,boolean incluiPrestadorNulo, Class<G> klassGuia, SituacaoEnum... situacoesGuia) throws Exception{
		SearchAgent sa = getSearchAgent();
		if (situacoesGuia.length > 0){
			sa = getSearchSituacoes(situacoesGuia);
		}
		return this.buscarGuias(sa,"",segurado,"","",prestador,true,incluiPrestadorNulo, klassGuia, null);
	}
	
	protected SearchAgent getNotSearchSituacoes(SituacaoEnum... situacoesGuia) {
		List<String> situacoes = new ArrayList<String>(situacoesGuia.length);
		for (int i = 0; i < situacoesGuia.length; i++){
			situacoes.add(situacoesGuia[i].descricao());
		}
		SearchAgent sa =getSearchAgent();
		sa.addParameter(new NotIn("situacao.descricao", situacoes));
		
		return sa;
	}

	protected SearchAgent getSearchSituacoes(SituacaoEnum... situacoesGuia) {
		Object[] situacoes = new Object[situacoesGuia.length];
		for (int i = 0; i < situacoes.length; i++){
			situacoes[i] = situacoesGuia[i].descricao();
		}
		
		SearchAgent sa = getSearchAgent();
		sa.addParameter(new In("situacao.descricao", situacoes));
		
		return sa;
	}
	
	protected <G extends GuiaSimples> List<G> buscarGuiasPeriodo(int periodo, Class<G> klassGuia, SeguradoInterface segurado) throws Exception{
		SearchAgent sa = this.getSearchAgent();
		Calendar calendario = new GregorianCalendar();
		sa.addParameter(new GreaterEquals("dataAtendimento", Utils.incrementaDias(calendario, - periodo)));
		return buscarGuias(sa, "", segurado, "", "", null, true,false, klassGuia, null);
	}
	
	protected <G extends GuiaSimples> List<G> buscarGuias(Prestador prestador,boolean incluiPrestadorNulo, Class<G> klassGuia, SituacaoEnum... situacoesGuia) throws Exception{
		SearchAgent sa =getSearchAgent();
		if (situacoesGuia.length > 0){
			sa = getSearchSituacoes(situacoesGuia);
		}
		
		return this.buscarGuias(sa,"",null,"","",prestador,true,incluiPrestadorNulo, klassGuia, null);
	}
	
	public <G extends GuiaSimples> GuiaSimples getUltimaGuia(Prestador prestador, SeguradoInterface segurado, Especialidade especialidade, Class<G> klassGuia, SituacaoEnum... situacoesGuia){
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
		
		SearchAgent sa = getSearchAgent();
		sa.clearAllParameters();

		if (prestador != null){
			sa.addParameter(new Equals("prestador", prestador));
		}

		if (segurado != null){
			segurado.addParameterEquals(sa);
		}
		
		if (especialidade != null) {
			sa.addParameter(new Equals("especialidade", especialidade));
		}
		
		Criteria criteria = sa.createCriteriaFor(klassGuia);
		java.sql.Date ultimaDataAtendimento = (java.sql.Date)criteria.setProjection(Projections.max("dataAtendimento")).uniqueResult();
		
		sa.addParameter(new Equals("dataAtendimento", ultimaDataAtendimento));
		sa.addParameter(new NotEquals("situacao.descricao", SituacaoEnum.CANCELADO.descricao()));
		
		if (situacoesGuia.length > 0){
			sa = getSearchSituacoes(situacoesGuia);
		}
		
		List<GuiaSimples> guias = sa.createCriteriaFor(klassGuia)
			.setMaxResults(1)
			.list();

		if(guias.isEmpty()) {
			return null;
		}
		
		return (GuiaSimples) guias.get(0);
	}
	

	public <G extends GuiaSimples> List<G> buscarGuiasPorPeriodo(Prestador prestador, SeguradoInterface segurado,
			boolean isConfirmacao, int periodo, Class<G> klassGuia, SituacaoEnum... situacoesGuia){
		SearchAgent sa = getSearchAgent();
		sa.clearAllParameters();
		
		if (situacoesGuia.length > 0)
			sa = getSearchSituacoes(situacoesGuia);
		
		Calendar calendar = new GregorianCalendar();
		
		if (isConfirmacao){
		   sa.addParameter(new GreaterEquals("dataAtendimento",Utils.incrementaDias(calendar, -periodo)));
		}
		else {
			sa.addParameter(new GreaterEquals("dataMarcacao",Utils.incrementaDias(calendar, -periodo)));
		}

		if (prestador != null){
			sa.addParameter(new Equals("prestador", prestador));
		}

		if (segurado != null){
			segurado.addParameterEquals(sa);
		}
		
		sa.addParameter(new NotEquals("situacao.descricao", SituacaoEnum.REALIZADO.descricao()));
		sa.addParameter(new NotEquals("situacao.descricao", SituacaoEnum.CANCELADO.descricao()));
		sa.addParameter(new NotEquals("situacao.descricao", SituacaoEnum.INAPTO.descricao()));
		
		
		return sa.list(klassGuia);
	}
	
	protected Date getCompetencia(String competencia) throws ValidateException {
		Date competenciaEscolhida = null;
		if (!Utils.isStringVazia(competencia)){
			try {
				competenciaEscolhida = Utils.gerarCompetencia(competencia);
			} catch (Exception e){
				e.printStackTrace();
				throw new ValidateException("A competência informada é inválida.");
			}
		} else{
			throw new ValidateException("A competência deve ser informada.");
		}
		
		return competenciaEscolhida;
	}
	
	protected <P extends ProcedimentoInterface> boolean geraCoParticipacao(P procedimento){		
		int quantGuias = 0;
		Date dataAtual = new Date();		
		String situacoes[] = {SituacaoEnum.CONFIRMADO.descricao(), SituacaoEnum.FATURADA.descricao()};
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, -366);

		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("guia.segurado", procedimento.getGuia().getSegurado()));
		sa.addParameter(new GreaterEquals("guia.dataAtendimento", cal.getTime()));
		sa.addParameter(new LowerEquals("guia.dataAtendimento", dataAtual));
		sa.addParameter(new In("situacao.descricao",situacoes));
		sa.addParameter(new Equals("procedimentoDaTabelaCBHPM.codigo","41301102"));//Colposcopia
		quantGuias = sa.resultCount(Procedimento.class);
		if((quantGuias == 0) && (procedimento.getProcedimentoDaTabelaCBHPM().getCodigo().equals("41301102"))){
			return false;
		} else if(procedimento.getProcedimentoDaTabelaCBHPM().getCodigo().equals("40309045")){
			sa.clearAllParameters();
			sa.addParameter(new Equals("guia.segurado", procedimento.getGuia().getSegurado()));
			sa.addParameter(new GreaterEquals("guia.dataAtendimento", cal.getTime()));
			sa.addParameter(new LowerEquals("guia.dataAtendimento", dataAtual));
			sa.addParameter(new In("situacao.descricao",situacoes));
			sa.addParameter(new Equals("procedimentoDaTabelaCBHPM.codigo","40309045"));//Citologia Oncótica
			quantGuias = sa.resultCount(Procedimento.class);
			if(quantGuias == 0){
				return false;
			}
		}
		return true;
	}
	
	public <G extends GuiaExame, T extends TabelaCBHPM> void atualizarProcedimentos(G guia, T cbhpm, 
			UsuarioInterface usuario) throws Exception {
		for (ProcedimentoInterface procedimento : (Set<Procedimento>) guia.getProcedimentos()) {
			boolean procedimentoNaoRealizado = !procedimento.isSituacaoAtual(SituacaoEnum.REALIZADO.descricao());
			
			if(procedimento != null && procedimento.getProcedimentoDaTabelaCBHPM().equals(cbhpm)
				&& procedimentoNaoRealizado){
				boolean isUsuarioPrestador = usuario.getRole().equals(UsuarioInterface.ROLE_PRESTADOR);
				String descr = isUsuarioPrestador ? MotivoEnum.CONFIRMADA_NO_PRESTADOR.getMessage(): MotivoEnum.CONFIRMADA_NO_LANCAMENTO_MANUAL.getMessage();
				procedimento.mudarSituacao(usuario, SituacaoEnum.REALIZADO.descricao(),descr, new Date());
				break;
			}	
		}
	}
		
	public void consumirConsultaPromocional(GuiaConsulta guia, UsuarioInterface usuario) throws Exception {
		Set<PromocaoConsulta> consultasPromocionais = guia.getSegurado().getConsultasPromocionais();
		for (PromocaoConsulta consultaPromocional : consultasPromocionais) {
			if(consultaPromocional.getEspecialidade() != null)
				HibernateUtil.currentSession().lock(consultaPromocional.getEspecialidade(), LockMode.NONE);
			
			boolean isEletiva = consultaPromocional.isEletiva();
			boolean isEspecialidadesIguais = guia.getEspecialidade().equals(consultaPromocional.getEspecialidade());
			boolean isUtilizado = consultaPromocional.isUtilizado();
			boolean isVencido = consultaPromocional.isVencido();
			if(isEletiva && isEspecialidadesIguais && !isVencido && !isUtilizado) {
				for (ProcedimentoInterface procedimento : (Set<Procedimento>)guia.getProcedimentos()) {
					procedimento.setGeraCoParticipacao(false);
				}
				consultaPromocional.setGuia(guia);
				consultaPromocional.getSituacao().getDataSituacao();
				consultaPromocional.mudarSituacao(usuario, PromocaoConsulta.UTILIZADO, MotivoEnum.UTILIZADO_CONFIRMACAO_CONSULTA.getMessage(), new Date());
				ImplDAO.save(consultaPromocional);
				break;
			}
		}
	}

	public String getSufixo(int index) throws ValidateException {
		String[] letras = {"A","B","C","D","E","F","G","H","I","J","K","L","M",
				"N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	
		if (index > 26){
			int letra2 = index%26;
			int letra1 = (index/26)-1;
			return (letras[letra1]+letras[letra2]);
		} else if (index <= 26){
			return letras[index-1];
		} else { 
			throw new ValidateException(MensagemErroEnum.NAO_FOI_POSSIVEL_GERAR_AUTENTICACAO.getMessage());
		}
	}
}
