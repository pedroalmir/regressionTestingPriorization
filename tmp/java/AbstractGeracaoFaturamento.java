package br.com.infowaypi.ecare.financeiro.faturamento;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.financeiro.ordenador.ProgressBarFinanceiro;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Prestador.TipoIncremento;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AbstractFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.Faturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.FaturamentoPassivo;
import br.com.infowaypi.ecarebc.financeiro.faturamento.GuiaFaturavel;
import br.com.infowaypi.ecarebc.financeiro.faturamento.HonorarioMedico;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ItemGuiaFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ResumoFaturamentos;
import br.com.infowaypi.ecarebc.financeiro.faturamento.TetoPrestadorFaturamento;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.NotEquals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.CollectionUtils;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe abstrata que contem o modelo a ser implementado pelas classes de faturamento (normal e passivo)
 * 
 * @author Eduardo
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class AbstractGeracaoFaturamento {
	
	public static final String GERACAO_NORMAL 	= "Geração de Faturamento Normal";
	public static final String GERACAO_PASSIVO 	= "Geração de Faturamento Passivo";
	public static final String GERACAO_PASSIVO_HONORARIO = "Geração de Faturamento Passivo Honorarios";

	protected List<AbstractFaturamento> faturamentos;
	protected Map<Prestador, Set<GuiaFaturavel>> mapaGuiasPorPrestador;
	protected Map<Prestador, BigDecimal> tetoPorPrestador;
	protected Boolean isFaturamentoPassivo = null;
	public static final String[] SITUACOES = {SituacaoEnum.CONFIRMADO.descricao(), SituacaoEnum.AUDITADO.descricao()};

	protected ProgressBarFinanceiro progressBar = new ProgressBarFinanceiro();
	private List<GuiaFaturavel> guias;
	
	public AbstractGeracaoFaturamento() {
		faturamentos = new ArrayList<AbstractFaturamento>();
		mapaGuiasPorPrestador = new LinkedHashMap<Prestador, Set<GuiaFaturavel>>();
	}
	
	protected void setFaturamentoPassivo(Boolean faturamentoPassivo) {
		this.isFaturamentoPassivo = faturamentoPassivo;
	}
	
	/**	
	 * Gera os faturamentos e popula um resumoFaturamentos 
	 * 
	 * @param competenciaBase
	 * @param dataGeracaoPlanilha
	 * @param tetos
	 * @return ResumoFaturamentos
	 * @throws Exception
	 */
	public ResumoFaturamentos gerarFaturamento(Date competenciaBase,
			Date dataGeracaoPlanilha,
			Collection<TetoPrestadorFaturamento> tetos,
			UsuarioInterface usuario, List<GuiaFaturavel> guias)
			throws Exception {
		
		gerarFaturamento(competenciaBase, dataGeracaoPlanilha, tetos, guias);
		
		ResumoFaturamentos resumo = new ResumoFaturamentos(faturamentos, null, ResumoFaturamentos.RESUMO_CATEGORIA, competenciaBase, false);
		if(isFaturamentoPassivo){
			resumo.setTipoFaturamento("Passivo");
		}else {
			resumo.setTipoFaturamento("Normal");
		}
		
		return resumo;
	}
	
	/**
	 * Método principal da classe, gera o faturamento para uma determinada competência. 
	 * @param competenciaBase
	 * @param dataGeracaoPlanilha
	 * @param tetos
	 * @throws Exception
	 */
	public List<AbstractFaturamento> gerarFaturamento(Date competenciaBase,
			Date dataGeracaoPlanilha,
			Collection<TetoPrestadorFaturamento> tetos, List<GuiaFaturavel> guias) throws Exception{
		this.guias = guias;
		
		faturamentos = new ArrayList<AbstractFaturamento>();
		
		validaDataFaturamento(competenciaBase);
		
		this.tetoPorPrestador = processarTetos(tetos);
		this.mapaGuiasPorPrestador = alimentarMapaGuiasPorPrestador(competenciaBase, dataGeracaoPlanilha, tetos);
		
		updateProgressBarData(mapaGuiasPorPrestador.keySet().size());
		for (Prestador prestador : mapaGuiasPorPrestador.keySet()) {
			progressBar.incrementarProgresso();
			gerarFaturamentoPrestador(competenciaBase, prestador, tetos, null, dataGeracaoPlanilha);
		}
		
		return faturamentos;
	}

	/**
	 * Agrupa as guias a ser faturadas por prestador e adiciona o prestador COOPANEST caso esse nao seja adicionado por falta de guias.
	 * 
	 * @param competencia
	 * @param dataGeracaoPlanilha
	 * @param tetos
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public final Map<Prestador, Set<GuiaFaturavel>> alimentarMapaGuiasPorPrestador(Date competencia, Date dataGeracaoPlanilha, Collection<TetoPrestadorFaturamento> tetos) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		Map<Prestador, Set<GuiaFaturavel>> guiasPorPrestador = CollectionUtils.groupBy(this.guias, "prestador", Prestador.class);
		
		Prestador prestadorCoopanest = ImplDAO.findById(373690L, Prestador.class);
		
		if(!guiasPorPrestador.keySet().contains(prestadorCoopanest)) {
			guiasPorPrestador.put(prestadorCoopanest, new HashSet<GuiaFaturavel>());
		}
		
		return CollectionUtils.groupBy(this.guias, "prestador", Prestador.class);
	}
	
	/**
	 * Seta o progresso atual da barra de progresso
	 * @param valorMaximo
	 * @return
	 */
	protected abstract void updateProgressBarData(int numeroDePretadores);
	
	/**
	 * Verifica se a competência gerada:
	 * <ul>
	 * 		<li>Não está nula</li>
	 * 		<li>Não é de uma competência </li>
	 * 		<li>Não foi gerado faturamento para a competência anterior</li>
	 * 		<li>Já foi gerado faturamento para a competência atual</li>
	 * </ul>
	 * Uma exceção é lançada se uma das condições não for satisfeita.
	 * 
	 * @param competencia
	 * @throws Exception
	 */
	public void validaDataFaturamento(Date competencia) throws Exception {
		Assert.isNotNull(competencia, MensagemErroEnumSR.COMPETENCIA_NAO_INFORMADA.getMessage());

		if(Utils.compararCompetencia(competencia, Utils.gerarCompetencia().getTime()) >= 0)
			throw new ValidateException(MensagemErroEnumSR.NAO_E_POSSIVEL_GERAR_FATURAMENTO_PARA_COMPETENCIA_POSTERIOR.getMessage());
		
		Date competenciaAnterior = Utils.getCompetenciaAnterior(competencia);
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new NotEquals("status", 'C'));
		Integer quantFaturamento = null;
		
		if (sa.resultCount(AbstractFaturamento.class) > 0) {
			sa.addParameter(new Equals("competencia", competenciaAnterior));
			quantFaturamento = sa.resultCount(AbstractFaturamento.class);
			Assert.isNotEquals(0, quantFaturamento,
					MensagemErroEnumSR.NAO_EXISTE_FATURAMENTO_PARA_COMPETENCIA_ANTERIOR.getMessage(Utils.format((competenciaAnterior), "MM/yyyy")));
		}
		
		sa.clearAllParameters();
				
		sa.addParameter(new Equals("competencia", competencia));
		sa.addParameter(new NotEquals("status", 'C'));
		if (isFaturamentoPassivo) {
			quantFaturamento = sa.resultCount(FaturamentoPassivo.class);
		} else {
			quantFaturamento = sa.resultCount(Faturamento.class);
		}
		Assert.isEquals(0, quantFaturamento,
				MensagemErroEnumSR.JA_EXISTE_FATURAMENTO_PARA_COMPETENCIA_ATUAL.getMessage(Utils.format((competencia), "MM/yyyy")));
		
	}

	/**
	 * Gera o faturamento para cada prestador.
	 * 
	 * @param competenciaBase
	 * @param competenciaAjustada
	 * @param prestador
	 * @param tetos
	 * @param mapProcedimentos
	 * @throws Exception
	 */
	public void gerarFaturamentoPrestador(Date competenciaBase, Prestador prestador, Collection<TetoPrestadorFaturamento> tetos,
			UsuarioInterface usuario, Date dataGeracaoPlanilha) throws Exception {
				
		List<GuiaFaturavel> todasAsGuias = new ArrayList(this.mapaGuiasPorPrestador.get(prestador));
	
		int quantidadeDeGuias = todasAsGuias.size();
		
		if (quantidadeDeGuias != 0) {

			AbstractFaturamento faturamento = getFaturamentoDaLista(prestador);
			if(faturamento == null){
				faturamento = createFaturamento(competenciaBase, prestador);
				this.faturamentos.add(faturamento);
			}
			
			BigDecimal valorTotalGuias 		= MoneyCalculation.rounded(BigDecimal.ZERO);
			BigDecimal valorAcumulado 		= BigDecimal.ZERO;
			ordenarListaDeGuias(todasAsGuias);
			boolean possuiTeto = this.tetoPorPrestador.keySet().contains(prestador);
			
			for (GuiaFaturavel guia : todasAsGuias) {
				boolean naoUltrapassouTeto = possuiTeto ? valorAcumulado.add(guia.getValorTotal()).compareTo(tetoPorPrestador.get(prestador)) <= 0 : true;
				if(naoUltrapassouTeto) {
					valorAcumulado = valorAcumulado.add(guia.getValorTotal());
					faturamento.addGuia(guia);
					valorTotalGuias = valorTotalGuias.add(guia.getValorPagoPrestador());
					faturamento.setSomatoriosGuias(guia);
				}else {
					continue;
				}
			}
			
			faturamento.setValorBruto(faturamento.getValorBruto().add(valorTotalGuias));
			faturamento.setValorLiquido(faturamento.getValorLiquido().add(valorTotalGuias));
		}
	}
	
	protected final boolean isTipoAptoADistribuirHonorarios(GuiaFaturavel guia){
		return (guia.isConsultaUrgencia() || guia.isAtendimentoUrgencia() || guia.isInternacao() || guia.isCirurgiaOdonto());
	}
	

	
	/**
	 * Ordena a lista de forma a priorizar as guias mais antigas no faturamento passivo, ou as mais caras,
	 * no faturamento normal.
	 *  
	 * @param todasAsGuias
	 */
	protected abstract void ordenarListaDeGuias(List<GuiaFaturavel> todasAsGuias);

	/**
	 * Percorre todas as guia selecionando apenas os tipos que estão aptos a distribuir honorários
	 * @return
	 * @throws IllegalAccessException
	Quando  * @throws NoSuchMethodException
	 */
	public final Set<GuiaCompleta> selecionarGuiasAptasADistribuirHonorariosMedicos(){
	
		Set<GuiaCompleta> guias = new HashSet<GuiaCompleta>();
		for (Set<GuiaFaturavel> set : this.mapaGuiasPorPrestador.values()) {
			for (GuiaFaturavel guia : set) {
				if(guia.isConsultaUrgencia() || guia.isAtendimentoUrgencia() || guia.isInternacao() || guia.isCirurgiaOdonto()) {
					guias.add((GuiaCompleta)guia);
				}
			}
		}
		
		return guias;
	}

	/**
	 * Método que lança exceção caso a coleção de guias esteja vazia para o faturamento normal, 
	 * mas que não faz nenhuma validação para o faturamento passivo.
	 * 
	 * @param guias
	 */
	public void verificarSeNaoTemGuias(Set<GuiaFaturavel> guias) {
		if (guias.isEmpty())
			throw new RuntimeException("Não existem guias para ser faturadas.");
	}

	/**
	 * Agrupa uma coleção de <code>TetoPrestadorFaturamento.java</code> por <code>Prestador.java</code>
	 * 
	 * @param tetos
	 * @return
	 */
	public final Map<Prestador, BigDecimal> processarTetos(Collection<TetoPrestadorFaturamento> tetos) {
		Map<Prestador, BigDecimal> tetoPorPrestador = new HashMap<Prestador, BigDecimal>();
		
		for (TetoPrestadorFaturamento teto : tetos){
			tetoPorPrestador.put(teto.getPrestador(), teto.getTeto());
		}
		return tetoPorPrestador;
	}

	/**
	 * Retorna o faturamento do prestador que está na lista, se ele tiver, caso contrário retorna <code>null</code>
	 * 
	 * @param prestador
	 * @return O faturamento do prestador, caso ele tenha um faturamento na lista, caso contrário, <code>null</code>
	 */
	public final AbstractFaturamento getFaturamentoDaLista(Prestador prestador) {
		for (AbstractFaturamento abstractFaturamento : this.faturamentos ) {
			if(abstractFaturamento.getPrestador().equals(prestador)){
				return abstractFaturamento;
			}
		}
		return null;
	}
	
	
	/**
	 * Retorna uma instancia da classe <code>HonorarioMedico.class</code> de uma das coleções de honorários 
	 * do profissional. A coleção será escolhida pela subclasse que implementar o método podendo ser 
	 * a de honorários passivos ou não.
	 * 
	 * @param competencia
	 * @param profissional
	 * @return
	 */
	protected abstract HonorarioMedico getHonorarioDoProfissionalParaACompetencia(Date competencia, Profissional profissional);
	
	/**
	 * Retorna uma <code>AbstractFaturamento.class</code> dependendo do tipo de faturamento 
	 * 
	 * @param competencia
	 * @param prestadorDestinoHonorario
	 * @return uma instancia de <code>Faturamento.class</code> ou <code>FaturamentoPassivo.class</code>
	 */
	protected abstract AbstractFaturamento getFaturamentoPrestadorParaACompetencia(Date competencia, Prestador prestadorDestinoHonorario);
	
	/**
	 * Subtrai do faturamento do prestador da guia o valor do procedimento 
	 * e o incrementa no prestador de destino do honorario.
	 * 
	 * @param competencia
	 * @param valorProfissional
	 * @param prestador
	 * @param procedimento
	 * @throws Exception
	 */
	public final void saveFaturamento(Date competencia, BigDecimal valorProfissional, Prestador prestador, Procedimento procedimento) throws Exception {
		
		AbstractFaturamento faturamento = getFaturamentoDaLista(prestador);
		if( faturamento == null){
			faturamento = createFaturamento(competencia, prestador);
			this.faturamentos.add(faturamento);
		}
		faturamento.setValorBruto(faturamento.getValorBruto().add(valorProfissional));
		faturamento.setValorLiquido(faturamento.getValorLiquido().add(valorProfissional));
		faturamento.atualizarSomatorioGuias(procedimento.getGuia(), valorProfissional, TipoIncremento.POSITIVO);
		
		AbstractFaturamento faturamentoPrestadorDaGuia = getFaturamentoDaLista(procedimento.getGuia().getPrestador());
		
		if(faturamentoPrestadorDaGuia == null){
			faturamentoPrestadorDaGuia = createFaturamento(competencia, prestador);
			this.faturamentos.add(faturamentoPrestadorDaGuia);
		}
		
		faturamentoPrestadorDaGuia.atualizarSomatorioGuias(procedimento.getGuia(), valorProfissional, TipoIncremento.NEGATIVO);
	}

	
	/**
	 * Método abstrato que decide que tipo de Faturamento será instanciado (Normal ou Passivo), dependendo
	 * da implementação da subclasse.
	 * 
	 * @param competencia
	 * @param prestador
	 * @return uma instância de <code>Faturamento.class</code> ou <code>FaturamentoPassivo.class</code>
	 */
	protected abstract AbstractFaturamento createFaturamento(Date competencia, Prestador prestador);
	
	/**
	 * Método abstrato responsável por informar o tipo de geraçao de faturamento:
	 * <li>Normal</li>
	 * <li>Passivo</li>
	 * <li>Passivo Honorários</li>
	 * 
	 * @return uma String informando o tipo de gerção de faturamento
	 */
	protected abstract String getTipoGeracaoFaturamento();
}
