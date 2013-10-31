package br.com.infowaypi.ecarebc.atendimentos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.com.infowaypi.ecare.services.recurso.GuiaRecursoGlosa;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.GuiaFaturavel;
import br.com.infowaypi.ecarebc.painelDeControle.PainelDeControle;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe que representa um lote de guias.
 * O lote deve:
 * <ul>
 * 		<li> conter no máximo 30 guias; </li>
 * 		<li> conter guias com data de término de atendimento de, no máximo, 60 dias antes da entrega </li>
 * 		<li> conter guias do mesmo tipo; </li>
 * 		<li> guias do mesmo prestador; </li>
 * 		<li> os tipos só podem ser guias de internação, urgência ou exames; </li>
 * </ul>
 * O lote deve ser recebido em até 48 horas após o envio. Caso contrário deve ser reenviado.
 * 
 * 
 * @author Eduardo
 * @see GuiaSimples.class
 */
public class LoteDeGuias extends ImplColecaoSituacoesComponent implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Constante que representa que o lote terá somente guias de internação
	 */
	public static final Integer INTERNACOES = 0;
	/**
	 * Constante que representa que o lote terá somente guias de urgência
	 */
	public static final Integer URGENCIA = 1;
	/**
	 * Constante que representa que o lote terá somente guias de exame
	 */
	public static final Integer EXAME = 2;
	/**
	 * Constante que representa que o lote terá somente guias de cirurgia odontológica
	 */
	public static final Integer CIRURGIA_ODONTO = 3;
	
	/**
	 * Constante que representa que o lote terá somente guias de cirurgia odontológica
	 */
	public static final Integer HONORARIOS = 4;
	
	/**
	 * Constante que representa que o lote terá somente guias de acompanhamento anestésico
	 */
	public static final Integer ACOMPANHAMENTO_ANESTESICO = 5;
	
	/**
	 * Constante que representa que o lote terá somente guias de recurso de glosa
	 */
	public static final Integer RECURSO_DE_GLOSA = 6;
	
	/**
	 * Motivo de envio do lote
	 */
	public static final String ENVIO_PRESTADOR = "Lote enviado pelo prestador.";

	/**
	 * Máximo de guias em um lote
	 */
	public static final int NUMERO_DE_GUIAS = 30;
	/**
	 * Tempo máximo entre o término de atendimento da guia e seu envio para o Saúde Recide
	 */
	public static final int PRAZO_DE_ENTREGA = 60;//dias

	/**
	 * Representa no banco a identificação do lote
	 */
	private Long idLote;
	/**
	 * Representa a identificação do lote para o Saúde Recife - possui o mesmo valor que o idLote
	 */
	private String identificador;

	/**
	 * Prestador que enviou o lote
	 */
	private Prestador prestador;
	/**
	 * Nome da pessoa encarregada de entregar o lote
	 */
	private String entregador;

	/**
	 * Competência a qual o lote se refere
	 */
	private Date competencia;

	/**
	 * Informa o tipo de guias que irá conter no lote
	 */
	private Integer tipoDeGuia;
	/**
	 * Quantidade de guias contidas no lote
	 */
	private Integer numeroDeGuias;
	/**
	 * Somatório dos valores totais das guias
	 */
	private BigDecimal valorTotal;
	/**
	 * Somatório dos valores apresentados das guias
	 */
	private BigDecimal valorTotalApresentado;
	
	/**
	 * Lista de guias que foram enviadas no lote 
	 */
	private List<GuiaSimples<ProcedimentoInterface>> guiasEnviadas;
	/**
	 * Conjunto com as guias recebidas do lote
	 */
	private Set<GuiaSimples<ProcedimentoInterface>> guiasRecebidas;
	/**
	 * Conjunto com as guias devolvidas do lote
	 */
	private Set<GuiaSimples<ProcedimentoInterface>> guiasDevolvidas;
	
	
	/**
	 * Lista de guias de recurso de glosa que foram enviadas no lote 
	 */
	private List<GuiaRecursoGlosa> guiasDeRecursoEnviadas;
	/**
	 * Conjunto com as guias de recurso de glosa recebidas do lote
	 */
	private Set<GuiaRecursoGlosa> guiasDeRecursoRecebidas;
	/**
	 * Conjunto com as guias de recurso de glosa devolvidas do lote
	 */
	private Set<GuiaRecursoGlosa> guiasDeRecursoDevolvidas;
	
	

	/**
	 * Construtor padrão da classe. 
	 * Instancia as coleções (guiasEnviadas, guiasRecebidas, guiasDevolvidas) e inicia o valorTotal do lote com ZERO.
	 */
	public LoteDeGuias() {
		
		this.guiasEnviadas = new ArrayList<GuiaSimples<ProcedimentoInterface>>();
		this.guiasRecebidas = new TreeSet<GuiaSimples<ProcedimentoInterface>>(new ComparatorGuiaLote());
		this.guiasDevolvidas = new TreeSet<GuiaSimples<ProcedimentoInterface>>(new ComparatorGuiaLote());
		this.valorTotal = BigDecimal.ZERO;
		this.valorTotalApresentado = BigDecimal.ZERO;
		
	}

	/**
	 * Construtor que recebe os valores iniciais do lote. 
	 *  
	 * @param tipoDeGuia
	 * @param guias
	 * @param prestador
	 * @param competencia
	 */
	public LoteDeGuias(Integer tipoDeGuia, List<GuiaSimples<ProcedimentoInterface>> guias, Prestador prestador, Date competencia) {
		
		this.prestador = prestador;
		this.competencia = competencia;
		this.tipoDeGuia = tipoDeGuia;
		this.guiasEnviadas = guias;
		this.numeroDeGuias = this.guiasEnviadas.size();
		this.valorTotal = this.getValorTotalGuias();
		
		final boolean temGuia = ((guias != null) && (!guias.isEmpty()));
		if (temGuia) {
			for (GuiaSimples<ProcedimentoInterface> guiaSimples : guias) {
				guiaSimples.setUltimoLote(this);
			}
		}
		
		this.guiasRecebidas = new HashSet<GuiaSimples<ProcedimentoInterface>>();
		this.guiasDevolvidas = new HashSet<GuiaSimples<ProcedimentoInterface>>();	
	}
	
	public LoteDeGuias(List<GuiaRecursoGlosa> guias, Prestador prestador, Date competencia) {
		
		this.prestador = prestador;
		this.competencia = competencia;
		this.tipoDeGuia = RECURSO_DE_GLOSA;
		this.guiasDeRecursoEnviadas = guias;
		this.numeroDeGuias = this.guiasDeRecursoEnviadas.size();
		this.valorTotal = this.getValorTotalGuiasRecursoDeGlosa();
		
		final boolean temGuia = ((guias != null) && (!guias.isEmpty()));
		if (temGuia) {
			for (GuiaRecursoGlosa guia : guias) {
				guia.setUltimoLote(this);
			}
		}
		
		this.guiasDeRecursoRecebidas = new HashSet<GuiaRecursoGlosa>();
		this.guiasDeRecursoDevolvidas = new HashSet<GuiaRecursoGlosa>();		
	}

	/**
	 * Verifica se o lote:
	 * <ul>
	 * 		<li> possui uma quantidade válida de guias </li>
	 * 		<li> possui todas as guias válidas </li>
	 * 		<li>ou, se as guias estão aptas para o recebimento </li>
	 * </ul>
	 * 
	 * @return 
	 * @throws ValidateException
	 */
	public Boolean validate() throws ValidateException {
		
		if (isSituacaoAtual(SituacaoEnum.ENVIADO.descricao())){
			this.validarGuiasRecebimento();
		} else {
			this.validarQtdeGuias();
			this.validarGuiasEnvio();
		}
		
		return Boolean.TRUE;
	}
	
	/**
	 * Verifica se o lote:
	 * <ul>
	 * 		<li> possui uma quantidade válida de guias </li>
	 * 		<li> está dentro do prazo de recebimento </li>
	 * </ul>
	 * 
	 * @return 
	 * @throws ValidateException
	 */
	public Boolean validateGRG() throws ValidateException {
		
		if (isSituacaoAtual(SituacaoEnum.ENVIADO.descricao())){
			this.validarGuiasRecebimentoGRG();
		} else {
			this.validarQtdeGuias();
		}
		
		return Boolean.TRUE;
	}

	/**
	 * Verifica se o lote possui mais de uma guia e menos de 30.
	 * 
	 * @throws ValidateException
	 */
	private void validarQtdeGuias() throws ValidateException {
		
		if ((this.guiasEnviadas != null && this.guiasEnviadas.size() > NUMERO_DE_GUIAS) 
				|| (this.guiasDeRecursoEnviadas != null && this.guiasDeRecursoEnviadas.size() > NUMERO_DE_GUIAS)){
			throw new ValidateException(MensagemErroEnum.EXCESSO_DE_GUIAS.getMessage(String.valueOf(NUMERO_DE_GUIAS)));
		}

		if ((this.guiasEnviadas != null && this.guiasEnviadas.size() <= 0) 
				|| (this.guiasDeRecursoEnviadas != null && this.guiasDeRecursoEnviadas.size() <= 0)){
			throw new ValidateException(MensagemErroEnum.MINIMO_DE_GUIAS.getMessage());
		}
		
	}

	/**
	 * Verifica se as guias:
	 * <ul>
	 * 		<li> são do mesmo tipo do lote </li>
	 * 		<li> estão fechadas ou devolvidas - condição para que o lote seja enviado <li>
	 * 		<li> são do mesmo prestador que o lote </li>
	 * 		<li> está dentro do prazo de entrega
	 * </ul>
	 * 
	 * @throws ValidateException
	 */
	private void validarGuiasEnvio() throws ValidateException {
		
		for (GuiaSimples<ProcedimentoInterface> guia : this.guiasEnviadas) {

			final boolean guiaDoMesmoTipoDoLote = this.tipoDeGuia.equals(tipoDaGuia(guia)); 
			if (!guiaDoMesmoTipoDoLote){
				throw new ValidateException(MensagemErroEnum.TIPO_DE_GUIAS.getMessage());
			}	
			if (!guia.isSituacaoAtual(SituacaoEnum.FECHADO.descricao())	&& !guia.isSituacaoAtual(SituacaoEnum.DEVOLVIDO.descricao())){
				throw new ValidateException(MensagemErroEnum.SITUACAO_GUIAS_LOTE.getMessage());
			}	
			if (!guia.getPrestador().equals(this.prestador)) {
				throw new ValidateException(MensagemErroEnum.GUIAS_PRESTADOR.getMessage());
			}
			
			validaPrazoEntrega(guia);
		}
	}
	
	/**
	 * Verifica se todas as guias estão na situação ENVIADO
	 * @throws ValidateException
	 */
	private void validarGuiasRecebimento() throws ValidateException {
		
		for (GuiaSimples<ProcedimentoInterface> guia : this.guiasEnviadas) {
			if(!guia.getRecebido()){
				continue;
			}	
			
			validaPrazoEntrega(guia);
		}
	}
	/**
	 * Verifica se todas as guias de recurso estão na situação ENVIADO
	 * @throws ValidateException
	 */
	private void validarGuiasRecebimentoGRG() throws ValidateException {
		
		for (GuiaRecursoGlosa guia : this.guiasDeRecursoEnviadas) {
			if(!guia.getRecebido()){
				continue;
			}	
			
			validaPrazoEntrega(guia);
		}
	}

	/**
	 * Verifica se a aguia esta dentro do prazo de entrega de lote.
	 * o prazo é escolhido de acordo com a data de vigência do prazo cadastraod no Painel de controle
	 */
	private void validaPrazoEntrega(GuiaFaturavel guia) throws ValidateException {
		
		final Calendar dataInicioDoPrazo = Utils.createCalendarFromDate(guia.getDataInicioPrazoRecebimento());
		final Calendar hoje = Calendar.getInstance();
		
		final Date dataVigenciaPrazoFinalEntregaDeLote = PainelDeControle.getPainel().getDataVigenciaPrazoFinalEntregaDeLote();
		
		int diferencaEmDias = Utils.diferencaEmDias(dataInicioDoPrazo, hoje);
		int prazoFinalParaEntregaDeLotePainel = PainelDeControle.getPainel().getPrazoFinalParaEntregaDeLote();
		
		boolean entraNaVigenciaDeMulta = Utils.compareData(dataInicioDoPrazo.getTime(),  dataVigenciaPrazoFinalEntregaDeLote) < 0;
		
		if(entraNaVigenciaDeMulta && (diferencaEmDias > LoteDeGuias.PRAZO_DE_ENTREGA)){
			Date dataMaximaEntega = Utils.incrementaDias(dataInicioDoPrazo, LoteDeGuias.PRAZO_DE_ENTREGA);
			throw new ValidateException(MensagemErroEnum.GUIA_ULTRAPASSADA.getMessage(guia.getAutorizacao(), Utils.format(dataMaximaEntega)));
		}
		
		if(!entraNaVigenciaDeMulta && (diferencaEmDias > prazoFinalParaEntregaDeLotePainel)){
			Date dataMaximaEntega = Utils.incrementaDias(dataInicioDoPrazo, prazoFinalParaEntregaDeLotePainel);
			throw new ValidateException(MensagemErroEnum.GUIA_ULTRAPASSADA.getMessage(guia.getAutorizacao(), Utils.format(dataMaximaEntega)));
		}
		
	}
	
	public Map<Integer, GuiaSimples<ProcedimentoInterface>> getGuiasDevolvidasParaImpressao(){
		return getMapGuias(guiasDevolvidas);
	}
	
	public Map<Integer, GuiaRecursoGlosa> getGuiasDeRecursoDevolvidasParaImpressao(){
		return getMapGuiasGRG(guiasDeRecursoDevolvidas);
	}

	public Map<Integer, GuiaSimples<ProcedimentoInterface>> getGuiasRecebidasSemMultaParaImpressao(){
		return getMapGuias(this.getGuiasRecebidasSemMulta());
	}
	
	public Map<Integer, GuiaRecursoGlosa> getGuiasRecebidasSemMultaParaImpressaoGRG(){
		return getMapGuiasGRG(this.getGuiasRecebidasSemMultaGRG());
	}
	
	public Map<Integer, GuiaSimples<ProcedimentoInterface>> getGuiasRecebidasComMultaParaImpressao(){
		return getMapGuias(this.getGuiasRecebidasComMulta());
	}
	
	public Map<Integer, GuiaRecursoGlosa> getGuiasRecebidasComMultaParaImpressaoGRG(){
		return getMapGuiasGRG(this.getGuiasRecebidasComMultaGRG());
	}
	
	public Map<Integer, GuiaSimples<ProcedimentoInterface>> getGuiasEnviadasParaImpressao(){
		return getMapGuias(guiasEnviadas);
	}
	
	public Map<Integer, GuiaRecursoGlosa> getGuiasEnviadasParaImpressaoGRG(){
		return getMapGuiasGRG(guiasDeRecursoEnviadas);
	}

	/**
	 * Organiza as guias para seirem sempre na mesma ordem na hora da impressão.
	 * 
	 * @param <G>
	 * @param guias
	 * @return
	 */
	private <G extends Collection<? extends GuiaRecursoGlosa>> Map<Integer, GuiaRecursoGlosa> getMapGuiasGRG(G guias) {
		Map<Integer, GuiaRecursoGlosa> mapGuias = new TreeMap<Integer, GuiaRecursoGlosa>();
		int indice = 0;
		
		for (GuiaRecursoGlosa guia : guias) {
			indice = guiasDeRecursoEnviadas.indexOf(guia) + 1;
			mapGuias.put(indice, guia);
		}
		
		return mapGuias;
	}
	/**
	 * Organiza as guias para seirem sempre na mesma ordem na hora da impressão.
	 * 
	 * @param <G>
	 * @param guias
	 * @return
	 */
	private <G extends Collection<? extends GuiaSimples>> Map<Integer, GuiaSimples<ProcedimentoInterface>> getMapGuias(G guias) {
		Map<Integer, GuiaSimples<ProcedimentoInterface>> mapGuias = new TreeMap<Integer, GuiaSimples<ProcedimentoInterface>>();
		int indice = 0;
		
		for (GuiaSimples guia : guias) {
			indice = guiasEnviadas.indexOf(guia) + 1;
			mapGuias.put(indice, guia);
		}
		
		return mapGuias;
	}

	/**
	 * Retorna um inteiro referente ao tipo da guia de acordo com as constantes da classe.
	 * 
	 * @param guia
	 * @return
	 * @throws ValidateException
	 */
	public static Integer tipoDaGuia(GuiaSimples<ProcedimentoInterface> guia) throws ValidateException {
		
		if (guia.isInternacao()){
			return LoteDeGuias.INTERNACOES;
		}	
		else if (guia.isConsultaUrgencia() || guia.isAtendimentoUrgencia()){
			return LoteDeGuias.URGENCIA;
		}	
		else if (guia.isExame()){
			return LoteDeGuias.EXAME;
		}	
		else if (guia.isCirurgiaOdonto()){
			return LoteDeGuias.CIRURGIA_ODONTO;
		}	
		else if (guia.isHonorarioMedico()){
			return LoteDeGuias.HONORARIOS;
		}	
		else if (guia.isAcompanhamentoAnestesico()){
			return LoteDeGuias.ACOMPANHAMENTO_ANESTESICO;
		}	
		
		throw new ValidateException(MensagemErroEnum.TIPO_DE_GUIA_INVALIDO.getMessage("tipo"));
		
	}

	/**
	 * retorna o somatório dos valores totais das guias
	 * 
	 * @return
	 */
	private BigDecimal getValorTotalGuias() {
		
		BigDecimal valor = BigDecimal.ZERO;

		for (GuiaSimples<ProcedimentoInterface> guia : this.guiasEnviadas) {
			valor = valor.add(guia.getValorTotal());
		}

		return valor;
	}
	
	/**
	 * retorna o somatório dos valores apresentados das guias
	 * 
	 * @author Matheus
	 * @return
	 */
	private BigDecimal getValorTotalApresentadoGuias() {
		BigDecimal valor = BigDecimal.ZERO;

		for (GuiaSimples<ProcedimentoInterface> guia : this.guiasEnviadas) {
			if(this.isSituacaoAtual(SituacaoEnum.RECEBIDO.descricao())){
				valor = valor.add(guia.getValorTotalApresentado());
			}
		}

		return valor;
	}
	
	/**
	 * retorna o somatório dos valores totais das guias de recurso de glosa
	 * 
	 * @return
	 */
	private BigDecimal getValorTotalGuiasRecursoDeGlosa() {
		
		BigDecimal valor = BigDecimal.ZERO;

		for (GuiaRecursoGlosa guia : this.guiasDeRecursoEnviadas) {
			valor = valor.add(guia.getValorTotal());
		}

		return valor;
	}

	public List<GuiaSimples<ProcedimentoInterface>> getGuiasEnviadas() {
		return guiasEnviadas;
	}

	public void setGuiasEnviadas(List<GuiaSimples<ProcedimentoInterface>> guiasEnviadas) {
		this.guiasEnviadas = guiasEnviadas;
	}

	public Set<GuiaSimples<ProcedimentoInterface>> getGuiasRecebidas() {
		return guiasRecebidas;
	}
	
	public Set<GuiaSimples<ProcedimentoInterface>> getGuiasRecebidasSemMulta() {
		Set<GuiaSimples<ProcedimentoInterface>> guiasRecebidasSemMulta = new HashSet<GuiaSimples<ProcedimentoInterface>>();
		for (GuiaSimples<ProcedimentoInterface> guiaSimples : guiasRecebidas) {
			if (!guiaSimples.isPossuiMulta()) {
				guiasRecebidasSemMulta.add(guiaSimples);
			}
		}
		return guiasRecebidasSemMulta;
	}
	
	public Set<GuiaRecursoGlosa> getGuiasRecebidasSemMultaGRG() {
		Set<GuiaRecursoGlosa> guiasRecebidasSemMulta = new HashSet<GuiaRecursoGlosa>();
		for (GuiaRecursoGlosa guia : guiasDeRecursoRecebidas) {
			if (!guia.isPossuiMulta()) {
				guiasRecebidasSemMulta.add(guia);
			}
		}
		return guiasRecebidasSemMulta;
	}
	
	public Set<GuiaSimples<ProcedimentoInterface>> getGuiasRecebidasComMulta() {
		Set<GuiaSimples<ProcedimentoInterface>> guiasRecebidasComMulta = new HashSet<GuiaSimples<ProcedimentoInterface>>();
		for (GuiaSimples<ProcedimentoInterface> guiaSimples : guiasRecebidas) {
			if (guiaSimples.isPossuiMulta()) {
				guiasRecebidasComMulta.add(guiaSimples);
			}
		}
		return guiasRecebidasComMulta;
	}
	
	public Set<GuiaRecursoGlosa> getGuiasRecebidasComMultaGRG() {
		Set<GuiaRecursoGlosa> guiasRecebidasComMulta = new HashSet<GuiaRecursoGlosa>();
		for (GuiaRecursoGlosa guia : guiasDeRecursoRecebidas) {
			if (guia.isPossuiMulta()) {
				guiasRecebidasComMulta.add(guia);
			}
		}
		return guiasRecebidasComMulta;
	}

	public void setGuiasRecebidas(
			Set<GuiaSimples<ProcedimentoInterface>> guiasRecebidas) {
		this.guiasRecebidas = guiasRecebidas;
	}

	public Set<GuiaSimples<ProcedimentoInterface>> getGuiasDevolvidas() {
		return guiasDevolvidas;
	}

	public void setGuiasDevolvidas(Set<GuiaSimples<ProcedimentoInterface>> guiasDevolvidas) {
		this.guiasDevolvidas = guiasDevolvidas;
	}

	/**
	 * Salva no banco o lote passado como parâmetro.
	 * Caso o identificador do lote seja nulo, a este será atribuido o mesmo valor do seu idLote.
	 * @param lote
	 * @throws Exception
	 */
	public static void salvarLote(LoteDeGuias lote) throws Exception{
		
		ImplDAO.save(lote);
		
		if (lote.getIdentificador() == null){
			lote.setIdentificador(lote.getIdLote().toString());
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.getIdentificador()).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null){
			return false;
		}
		
		LoteDeGuias lote = (LoteDeGuias) obj;
		return new EqualsBuilder().append(this.getIdentificador(), lote.getIdentificador()).isEquals();
	}

	public Long getIdLote() {
		return idLote;
	}

	public void setIdLote(Long idLote) {
		this.idLote = idLote;
	}

	public String getIdentificador() {
		return identificador;
	}

	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}

	public Prestador getPrestador() {
		return prestador;
	}

	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
	}

	public String getEntregador() {
		return entregador;
	}

	public void setEntregador(String entregador) {
		this.entregador = entregador;
	}

	public Date getCompetencia() {
		return competencia;
	}

	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}

	public Integer getTipoDeGuia() {
		return tipoDeGuia;
	}

	public void setTipoDeGuia(Integer tipoDeGuia) {
		this.tipoDeGuia = tipoDeGuia;
	}

	public Integer getNumeroDeGuias() {
		return numeroDeGuias;
	}

	public void setNumeroDeGuias(Integer numeroDeGuias) {
		this.numeroDeGuias = numeroDeGuias;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
	
	public BigDecimal getValorTotalApresentado() {
		return valorTotalApresentado;
	}

	public void setValorTotalApresentado(BigDecimal valorTotalApresentado) {
		this.valorTotalApresentado = valorTotalApresentado;
	}
	
	
	
	/**
	 * @return <code>true </code> se o lote está na situação ENVIADO. <code>false</code> caso contrário
	 */
	public boolean isEnviado(){
		 return this.isSituacaoAtual(SituacaoEnum.ENVIADO.descricao());
	}

	/**
	 * @return <code>true </code> se o lote está na situação RECEBIDO. <code>false</code> caso contrário
	 */
	public boolean isRecebido(){
		 return this.isSituacaoAtual(SituacaoEnum.RECEBIDO.descricao());
	}
	
	/**
	 * @return a quantidade de guias recebidas
	 */
	public int getNumeroDeGuiasRecebidas() {
		return this.guiasRecebidas.size();
	}
	
	/**
	 * @return a quantidade de guias recebidas
	 */
	public int getNumeroDeGuiasRecebidasGRG() {
		return this.guiasDeRecursoRecebidas.size();
	}
	
	/**
	 * @return a quantidade de guias devolvidas
	 */
	public int getNumeroDeGuiasDevolvidas() {
		return this.guiasDevolvidas.size();
	}
	
	/**
	 * @return a quantidade de guias devolvidas
	 */
	public int getNumeroDeGuiasDevolvidasGRG() {
		return this.guiasDeRecursoDevolvidas.size();
	}

	/**
	 * @return somatório dos valores totais das guias devolvidas
	 */
	public BigDecimal getValorDevolvido() {
		BigDecimal valorDevolvido = BigDecimal.ZERO;
		for (GuiaSimples guia : this.guiasDevolvidas) {
			valorDevolvido = valorDevolvido.add(guia.getValorTotal());
		}
		return valorDevolvido;
	}
	
	
	/**
	 * @return somatório dos valores totais das guias devolvidas
	 */
	public BigDecimal getValorDevolvidoGRG() {
		BigDecimal valorDevolvido = BigDecimal.ZERO;
		for (GuiaRecursoGlosa guia : this.guiasDeRecursoDevolvidas) {
			valorDevolvido = valorDevolvido.add(guia.getValorTotal());
		}
		return valorDevolvido;
	}
	
	/**
	 * @return descrição do tipo de guia do lote
	 */
	public String getDescricaoTipoGuia(){
		if (this.tipoDeGuia.equals(INTERNACOES)){
			return "Internações";
		}	
		else if (this.tipoDeGuia.equals(URGENCIA)){ 
			return "Consulta de Urgência/Atendimento Subsequente";
		}	
		else if (this.tipoDeGuia.equals(EXAME)){ 
			return "Guia de Exame";
		}	
		else if (this.tipoDeGuia.equals(CIRURGIA_ODONTO)){ 
			return "Cirurgia Odontológica";
		}
		else if (this.tipoDeGuia.equals(HONORARIOS)){ 
			return "Honorários";
		}	
		else if (this.tipoDeGuia.equals(RECURSO_DE_GLOSA)){ 
			return "Recurso de Glosa";
		}	
		
		
		return "Nenhum";
	}
	
	/**
	 * @return data de envio do lote
	 */
	public Date getDataEnvio(){
		if(getSituacao(SituacaoEnum.ENVIADO.descricao())== null){
			return null;
		}	
		return getSituacao(SituacaoEnum.ENVIADO.descricao()).getDataSituacao();
	}
	
	/**
	 * @return data de recebimento do lote
	 */
	public Date getDataRecebimento(){
		if(getSituacao(SituacaoEnum.RECEBIDO.descricao())== null){
			return null;
		}	
		return getSituacao(SituacaoEnum.RECEBIDO.descricao()).getDataSituacao();
	}
	
	/**
	 * @return usuário que efetuou o recebimento do lote
	 */
	public String getUsuarioRecebimento(){
		if(getSituacao(SituacaoEnum.RECEBIDO.descricao())== null){
			return "";
		}	
		return getSituacao(SituacaoEnum.RECEBIDO.descricao()).getUsuario().getNome();
	}
	
	/**
	 * Classe responsável por comparar as guias do lote. 
	 * @author Eduardo
	 */
	class ComparatorGuiaLote implements Comparator<GuiaSimples>{
		
		/**
		 * Compara as guias pelo valor de seu id.
		 */
		public int compare(GuiaSimples guia1, GuiaSimples guia2) {
			return guia1.getIdGuia().compareTo(guia2.getIdGuia());
		}
	}

	public List<GuiaRecursoGlosa> getGuiasDeRecursoEnviadas() {
		return guiasDeRecursoEnviadas;
	}

	public void setGuiasDeRecursoEnviadas(
			List<GuiaRecursoGlosa> guiasDeRecursoEnviadas) {
		this.guiasDeRecursoEnviadas = guiasDeRecursoEnviadas;
	}

	public Set<GuiaRecursoGlosa> getGuiasDeRecursoRecebidas() {
		return guiasDeRecursoRecebidas;
	}

	public void setGuiasDeRecursoRecebidas(
			Set<GuiaRecursoGlosa> guiasDeRecursoRecebidas) {
		this.guiasDeRecursoRecebidas = guiasDeRecursoRecebidas;
	}

	public Set<GuiaRecursoGlosa> getGuiasDeRecursoDevolvidas() {
		return guiasDeRecursoDevolvidas;
	}

	public void setGuiasDeRecursoDevolvidas(
			Set<GuiaRecursoGlosa> guiasDeRecursoDevolvidas) {
		this.guiasDeRecursoDevolvidas = guiasDeRecursoDevolvidas;
	}
	
	public boolean isLoteGRG(){
		return !guiasDeRecursoEnviadas.isEmpty() || !guiasDeRecursoRecebidas.isEmpty() || !guiasDeRecursoDevolvidas.isEmpty();
	}
	
	/**
	 * Retorna o valor total apresentado do lote de guias
	 * 
	 * @author Matheus
	 */
	public void atualizaValorTotalApresentado(){
		this.setValorTotalApresentado(this.getValorTotalApresentadoGuias());
	}
	
	/**
	 * Retorna o valor total auditado do lote de guias
	 * 
	 * @author Matheus
	 */
	public void atualizaValorTotal(){
		this.setValorTotal(this.getValorTotalGuias());
	}
	
}
