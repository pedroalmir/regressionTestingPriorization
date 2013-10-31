package br.com.infowaypi.ecarebc.atendimentos;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.com.infowaypi.ecare.atendimentos.TipoGuiaEnum;
import br.com.infowaypi.ecare.cadastros.MotivoGlosa;
import br.com.infowaypi.ecare.enums.MotivoEnumSR;
import br.com.infowaypi.ecare.services.recurso.GuiaRecursoGlosa;
import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Prestador.TipoIncremento;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.enums.GuiaFactoryValidateEnum;
import br.com.infowaypi.ecarebc.atendimentos.enums.TipoConsultaEnum;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GuiaHonorarioMedico;
import br.com.infowaypi.ecarebc.atendimentos.honorario.Honorario;
import br.com.infowaypi.ecarebc.atendimentos.validators.AbstractGuiaValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.BasicPrestadorValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.BasicProfissionalValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.BasicSeguradoValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.ConsumoSeguradoValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.Criticavel;
import br.com.infowaypi.ecarebc.atendimentos.validators.DataGuiaSimplesValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.EspecialidadeLimiteIdadeValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.GuiaSimplesValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.TetoConsumoEExamesValidator;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.enums.GrauDeParticipacaoEnum;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.Role;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.FluxoFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AbstractFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.GuiaFaturavel;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ItemGuiaFaturamento;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOutros;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.ecarebc.procedimentos.validators.AtendimentoValidator;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.ecarebc.utils.CommandCorrecaoValorGuia;
import br.com.infowaypi.ecarebc.utils.GuiaUtils;
import br.com.infowaypi.ecarebc.utils.SituacaoUtils;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.situations.Situacao;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe que representa a estrutura básica de uma guia no sistema
 * 
 * @author Rondinele
 * @changes Erick Passos / Danilo Nogueira Portela
 */
@SuppressWarnings({"unchecked"})
public abstract class GuiaSimples<P extends ProcedimentoInterface> extends 
		ImplColecaoSituacoesComponent implements Criticavel, GuiaFaturavel, Visualizavel {

	private static final long serialVersionUID = 1L;
	// Constantes
	public static final String PROCEDIMENTO_PADRAO_CONSULTA 			= "10101012";
	public static final String PROCEDIMENTO_CONSULTA_PSICOLOGIA 		= "95000002";//nao usado//PSICOTERAPIA INDIVIDUAL - POR SESSAO, ok 
	public static final String PROCEDIMENTO_CONSULTA_FISIOTERAPIA 		= "10101014";//nao usado E ANTIGO
	public static final String PROCEDIMENTO_CONSULTA_URGENCIA 			= "10101039";
	public static final String PROCEDIMENTO_CONSULTA_ODONTOLOGICA 		= "99000002";
	public static final String PROCEDIMENTO_CONSULTA_INICIAL_CLINICO_PROMOTOR = "99000001";
	public static final String PROCEDIMENTO_CONSULTA_ODONTOLOGICA_INICIAL = "99000004";
	
	public static final Integer DATA_DE_ATENDIMENTO = 1;
	public static final Integer DATA_DE_MARCACAO 	= 2;
	public static final Integer DATA_DE_SITUACAO 	= 3;
	public static final Integer DATA_DE_TERMINO 	= 4;
	public static final Integer DATA_DE_RECEBIMENTO	= 5;
	
	public static final Integer PRAZO_VENCIMENTO_CONSULTAS_E_EXAMES = 30;
	public static final Integer PRAZO_VENCIMENTO_INTERNACOES 		= 60;
	public static final int 	PERIODO_VALIDADE_CONSULTA_PARA_SOLICITACAO_EXAME = 60;

	public static final Integer LIBERACAO_AUDITOR = 1;
	public static final Integer LIBERACAO_MPPS = 2;
	
	private Long idGuia;
	private String autorizacao;
	private AbstractSegurado segurado;
	private Profissional profissional;
	private Profissional solicitante;
	private Prestador prestador;
	private Especialidade especialidade;
	private Set<Observacao> observacoes;
	private Set<ItemGuiaFaturamento> itensGuiaFaturamento;
	
	/**
	 * Atributo criado para auxiliar a conformização do valor apresentado pelo prestador.
	 */
	private transient BigDecimal valorTotalMatMedAntesDaAuditoria;
	
	/**
	 * Data em que a guia foi criada.
	 */
	private Date dataMarcacao;
	
	/**
	 * Matheus: 
	 * 
	 * (GuiaInternacaoEletiva): armazena o dia em que a internação foi confirmada. No caso de internação eletiva, é possível ter datas retroativas para sua confirmação. 
	 *  
	 */
	private Date dataAtendimento;
	/**
	 * Data de termino do atendimento. Guias de Consulta, Exame, Atendimento de urgência e 
	 * Atendimento Subseqüente possuem esta data igual à data de atendimento
	 */
	private Date dataTerminoAtendimento;
	
	/**
	 * Data em que a guia foi recebida pela central.
	 */
	private Date dataRecebimento;
	
	/**
	 * Indica se a guia foi não foi criada pelo prestador.
	 * True quer dizer que a guia foi criada na central.
	 */
	private boolean fromPrestador;
	
	/**
	 * Indica se a guia saiu do fluxo de autorizacao da central com prestador.
	 * Por padrão, as guias são solicitadas sem prestador.
	 */
	private boolean solicitadoComPrestador = false;
	
	/**
	 * Indica se a guia foi não foi criada pelo prestador, tal e qual o campo {@link #fromPrestador}.
	 * True quer dizer que a guia foi criada na central.
	 */
	private boolean especial;
	private boolean coParticipacaoLiberada = false;
	private BigDecimal valorTotalApresentado;
	private BigDecimal valorTotal;
	private BigDecimal valorPagoPrestador;
	private Set<P> procedimentos;
	private String tipoDeGuia;
	private Set<GuiaSimples> guiasFilhas;
	private GuiaSimples guiaOrigem;
	private AbstractFaturamento faturamento;
	private UsuarioInterface usuarioDoFluxo;
	
	/**
	 * Indica se a guia foi gerada excedendo os limites de consumo do segurado.
	 * Atributo que é responsável pela verificação se a guia foi liberada por fora do limite. 
	 */
	private Integer liberadaForaDoLimite;
	private String mensagemLimite;
	private BigDecimal valorParcial;
	private BigDecimal valorParcialAnestesista;
	private FluxoFinanceiroInterface fluxoFinanceiro;
	private BigDecimal valorCoParticipacao;
	private Long idSeguradoUniplam;
	private boolean loadSegurado;
	private BigDecimal valorAnterior;
	private MotivoGlosa motivoGlosa;
	private String motivoParaGlosaTotal;
	private Integer quantidadeProcedimentos;
	
	/**
	 * Informa se a guia possui ou não guia externas de exame
	 */
	private boolean exameExterno;
	private Boolean recebido; 
	List<AtendimentoValidator> validacoes;
	
	private LoteDeGuias ultimoLote;
	private Set<LoteDeGuias> lotesDeGuias;
	
	private Set<Critica> criticas;
	
	/**
	 * Coleção transiente usada para armazenar as criticas que serão apresentadas previamente em determinados fluxos.
	 * Tais críticas, serão as que ainda não foram geradas em outros fluxos e que ainda não foram avaliadas.
	 */
	private Set<Critica> criticasParaApresentacaoPrevia;
	
	/**
	 * O atributo motivo é transiente e é utilizado no fluxo recebimento de lote
	 */
	private MotivoDevolucaoDeLote motivo;
	
	/**
	 * Atributo transiente para avisar utilizadono fluxo de solicitar exames pela central
	 */
	private boolean possuiPeriodicidadeEmProcedimento = false;
	
	/**
	 * Atributo vindo da classe GuiaHonorarioMedico. Esse campo de preenchimento do prestador anestesista será requerido
	 * durante o fechamento de guias de acompanhamento anestésico.
	 */
	private String 	numeroDeRegistro;

	/**
	 * Atributo que determina a porcentagem de deflação que a guia sofrerá por
	 * ter sido entregue fora do prazo (determinado no painel de controle).
	 */
	private BigDecimal multaPorAtrasoDeEntrega;

	/**
	 * Conjunto de validadores para as guias (command pattern)
	 */
	private static Collection<AbstractGuiaValidator> basicValidators = new ArrayList<AbstractGuiaValidator>();
	static{
		basicValidators.add(new GuiaSimplesValidator());
		basicValidators.add(new BasicSeguradoValidator());
		basicValidators.add(new ConsumoSeguradoValidator());
		basicValidators.add(new TetoConsumoEExamesValidator());
		basicValidators.add(new BasicPrestadorValidator());
		basicValidators.add(new BasicProfissionalValidator());
		basicValidators.add(new DataGuiaSimplesValidator());
		basicValidators.add(new EspecialidadeLimiteIdadeValidator());
	}

	private Collection<AtendimentoValidator> flowValidators = new ArrayList<AtendimentoValidator>();

	public GuiaSimples(){
		this(null);
	}
	
	public GuiaSimples(UsuarioInterface usuario) {
		this.dataAtendimento = new Date();
		this.dataMarcacao = new Date();
		this.procedimentos = new HashSet<P>();
		this.guiasFilhas = new HashSet<GuiaSimples>();
		this.valorTotal = BigDecimal.valueOf(0.0);
		this.valorPagoPrestador = BigDecimal.valueOf(0.0);
		this.valorParcial = new BigDecimal(0);
		this.valorAnterior = new BigDecimal(0);
		this.valorParcialAnestesista = new BigDecimal(0);
		this.valorCoParticipacao = BigDecimal.ZERO;
		this.lotesDeGuias = new HashSet<LoteDeGuias>();
		this.criticas = new HashSet<Critica>();
		this.criticasParaApresentacaoPrevia = new HashSet<Critica>();
		this.observacoes = new HashSet<Observacao>();
		this.itensGuiaFaturamento = new HashSet<ItemGuiaFaturamento>();
		this.liberadaForaDoLimite = 0;
	}

	public Long getIdSeguradoUniplam() {
		return idSeguradoUniplam;
	}

	public void setIdSeguradoUniplam(Long idSeguradoUniplam) {
		this.idSeguradoUniplam = idSeguradoUniplam;
	}

	public String getTipoDeGuia() {
		return tipoDeGuia;
	}

	public void setTipoDeGuia(String tipoDeGuia) {
		this.tipoDeGuia = tipoDeGuia;
	}

	public void setValorParcial(BigDecimal valorParcial) {
		this.valorParcial = valorParcial;
	}

	public BigDecimal getValorParcial() {
		return valorParcial;
	}

	public abstract String getTipo();

	public Date getDataAtendimento() {
		return dataAtendimento;
	}

	public BigDecimal getValorAnterior() {
		return valorAnterior;
	}

	public void setValorAnterior(BigDecimal valorAnterior) {
		this.valorAnterior = valorAnterior;
	}

	public void setDataAtendimento(Date dataAtendimento) {
		this.dataAtendimento = dataAtendimento;
	}

	public Date getDataMarcacao() {
		return dataMarcacao;
	}

	public void setDataMarcacao(Date dataMarcacao) {
		this.dataMarcacao = dataMarcacao;
	}

	public Date getDataAutorizacao(){
		SituacaoInterface situacao = this.getSituacao(SituacaoEnum.AUTORIZADO.descricao());
		if (situacao!=null){
			return situacao.getDataSituacao();
		}
		
		return null;
	}
	
	public String getDataConfirmacaoFormatada() {
		for (SituacaoInterface situacao : this.getColecaoSituacoes()
				.getSituacoes()) {
			if (situacao.getDescricao().equals(Constantes.SITUACAO_CONFIRMADO)) {
				return Utils.format(situacao.getDataSituacao(),
						"dd/MM/yyyy HH:mm:ss");
			}
		}
		return "--/--/-- --:--:--";
	}

	public Especialidade getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(Especialidade especialidade) {
		this.especialidade = especialidade;
	}

	public boolean isEspecial() {
		return especial;
	}

	public void setEspecial(boolean especial) {
		this.especial = especial;
	}

	public boolean isFromPrestador() {
		return fromPrestador;
	}

	public void setFromPrestador(boolean fromPrestador) {
		this.fromPrestador = fromPrestador;
	}
	
	public void setSolicitadoComPrestador(boolean solicitadoComPrestador) {
		this.solicitadoComPrestador = solicitadoComPrestador;
	}

	public boolean isSolicitadoComPrestador() {
		return solicitadoComPrestador;
	}

	public Long getIdGuia() {
		return idGuia;
	}

	public void setIdGuia(Long idGuia) {
		this.idGuia = idGuia;
	}

	public boolean isLoadSegurado() {
		return loadSegurado;
	}

	public void setLoadSegurado(boolean loadSegurado) {
		this.loadSegurado = loadSegurado;
	}

	public Prestador getPrestador() {
		return prestador;
	}

	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
	}

	public Set<P> getProcedimentos() {
		return procedimentos;
	}

	public List<ProcedimentoInterface> getProcedimentosOrdenado() {
		return ordenarProcedimentosPorSituacaoEDescricao((Collection<ProcedimentoInterface>) getProcedimentos());
	}

	public <T extends ProcedimentoInterface> Set<T> getProcedimentos(Class<T> klassProcedimento) {
		Set<T> procedimentos = new HashSet<T>();
		for (ProcedimentoInterface procedimento : this.procedimentos) {
			if (klassProcedimento.isInstance(procedimento))
				procedimentos.add((T)procedimento);
		}
		return procedimentos;
	}

	public Set<ProcedimentoInterface> getProcedimentosParaAlteracao() {
		Set<ProcedimentoInterface> result = new HashSet<ProcedimentoInterface>();
		for (ProcedimentoInterface proc : this.getProcedimentosNaoGlosadosNemCanceladosNemNegados()) {
			boolean procedimentoConsultaUrgencia = proc.getProcedimentoDaTabelaCBHPM().getCodigo().equals(
							PROCEDIMENTO_CONSULTA_URGENCIA);
			boolean procedimentoConsultaOdonto = proc.getProcedimentoDaTabelaCBHPM().getCodigo().equals(
							PROCEDIMENTO_CONSULTA_ODONTOLOGICA);
			boolean isProcedimentoCirurgico = proc.getTipoProcedimento() == Procedimento.PROCEDIMENTO_CIRURGICO;
			if (!isProcedimentoCirurgico
					&& !proc.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())
					&& (!procedimentoConsultaUrgencia)
					&& (!procedimentoConsultaOdonto)) {
				result.add(proc);
			}
		}
		return result;
	}

	public void setProcedimentos(Set<P> procedimentos) {
		this.procedimentos = procedimentos;
	}

	public Profissional getProfissional() {
		return profissional;
	}

	public void setProfissional(Profissional profissional) {
		this.profissional = profissional;
	}

	public AbstractSegurado getSegurado() {
		if (!loadSegurado){
			segurado = ImplDAO.findById(idSeguradoUniplam, AbstractSegurado.class);
		}
		return segurado;
	}

	public void setSegurado(AbstractSegurado segurado) {
		this.segurado = segurado;
		this.loadSegurado = true;
	}

	public BigDecimal getValorTotalApresentado() {
		return valorTotalApresentado;
	}

	/**
	 * @param valorTotalApresentado
	 */
	/*
	 * Pedro Almir - 02/08/2013
	 * 
	 * Alteração do modificador de acesso de private para protected visando
	 * permitir que todas as guias dentro da hierarquia de GuiaSimples
	 * possam setar esse valor.
	 * */
	protected void setValorTotalApresentado(BigDecimal valorTotalApresentado) {
		this.valorTotalApresentado = valorTotalApresentado;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public String getValorTotalFormatado() {
		return StringUtils.leftPad(new DecimalFormat("###,###,##0.00")
				.format(valorTotal.setScale(2, BigDecimal.ROUND_HALF_UP)), 9,
				" ");
	}

	public String getValorTotalRecursoFormatado() {
		return StringUtils.leftPad(new DecimalFormat("###,###,##0.00")
				.format(valorTotal.setScale(2, BigDecimal.ROUND_HALF_UP)), 9,
				" ");
	}
	
	public BigDecimal getValorPagoPrestador() {
		return valorPagoPrestador;
	}
	
	/**
	 * Retorna o valor pago ao prestador apenas a partir da situação Auditado(a), caso contrário, retorna null.
	 * @return
	 */
	public BigDecimal getValorPagoPrestadorApresentacao() {
		BigDecimal resultado = null;
		
		if(this.isMostrarValorPagoPrestador()){
			resultado =  this.valorPagoPrestador;
		}
		
		return resultado;
	}
	
	public void setValorPagoPrestador(BigDecimal valorPagoPrestador) {
		this.valorPagoPrestador = valorPagoPrestador;
	}

	public String getValorCoParticipacaoFormatado() {

		return StringUtils.leftPad(new DecimalFormat("###,###,##0.00")
				.format(this.getValorCoParticipacao().setScale(2,
						BigDecimal.ROUND_HALF_UP)), 9, " ");
	}

	public BigDecimal getValorCoParticipacao() {
		return valorCoParticipacao;
	}

	public void setValorCoParticipacao(BigDecimal valorCoParticipacao) {
		this.valorCoParticipacao = valorCoParticipacao;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public AbstractFaturamento getFaturamento() {
		return faturamento;
	}

	public void setFaturamento(AbstractFaturamento faturamento) {
		this.faturamento = faturamento;
	}

	public Profissional getSolicitante() {
		return solicitante;
	}

	public void setSolicitante(Profissional solicitante) {
		this.solicitante = solicitante;
	}

	public Set<GuiaSimples> getGuiasFilhas() {
		return guiasFilhas;
	}
	
	public boolean isCoParticipacaoLiberada() {
		return coParticipacaoLiberada;
	}

	public void setCoParticipacaoLiberada(boolean coParticipacaoLiberada) {
		this.coParticipacaoLiberada = coParticipacaoLiberada;
	}

	/**
	 * Retorna todas as guias filhas do tipo Internação.
	 * @return
	 */
	public Set<GuiaInternacao> getGuiasFilhasDoFechamentoParcial() {
        Set<GuiaInternacao> guias = new HashSet<GuiaInternacao>();
        
        for (GuiaSimples<ProcedimentoInterface> guia : this.guiasFilhas) {
                if (guia.isInternacao()) {
                	guia = ImplDAO.findById(guia.getIdGuia(), GuiaInternacao.class);
    				guias.add((GuiaInternacao)guia);
                }
        }

        return guias;
	}
	
	public Set<GuiaHonorarioMedico> getGuiasFilhasDeHonorarioMedico() {
		Set<GuiaHonorarioMedico> guias = new HashSet<GuiaHonorarioMedico>();

		for (GuiaSimples<ProcedimentoInterface> guia : this.guiasFilhas) {
			boolean isCancelado = guia.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao());
			
			if (guia.isHonorarioMedico() && !isCancelado) {
				guias.add(ImplDAO.findById(guia.getIdGuia(), GuiaHonorarioMedico.class));
				
			}
		}

		return guias;
	}
	
	public Set<GuiaHonorarioMedico> getGuiasFilhasDeHonorarioMedicoAptasPraAuditoria() {
		Set<GuiaHonorarioMedico> guias = new HashSet<GuiaHonorarioMedico>();

		for (GuiaHonorarioMedico guia : this.getGuiasFilhasDeHonorarioMedico()) {
			boolean isRecebido 	= guia.isSituacaoAtual(SituacaoEnum.RECEBIDO.descricao());
			boolean isAuditado 	= guia.isSituacaoAtual(SituacaoEnum.AUDITADO.descricao());
			
			if (isRecebido || isAuditado) {
				guias.add(guia);
			}
		}

		return guias;
	}

	public void setGuiasFilhas(Set<GuiaSimples> guiaGeradas) {
		this.guiasFilhas = guiaGeradas;
	}

	public GuiaSimples getGuiaOrigem() {
		return guiaOrigem;
	}

	public void setGuiaOrigem(GuiaSimples guiaOrigem) {
		this.guiaOrigem = guiaOrigem;
	}

	public String getAutorizacao() {
		return autorizacao;
	}

	public void setAutorizacao(String autorizacao) {
		this.autorizacao = autorizacao;
	}

	public boolean isGuiaFilha() {
		return (this.getGuiaOrigem() != null);
	}

	public abstract Date getDataVencimento();

	public void addGuiaFilha(GuiaSimples guia) {
		guia.setGuiaOrigem(this);
		this.getGuiasFilhas().add(guia);
	}
	
	/**
	 * Método inserido para permitir datas retroativas para alguns procedimentos (inserido o atributo validaDataRetroativa).
	 *   
	 * @author Matheus
	 * @param procedimento
	 * @param validaDataRetroativa
	 * @throws Exception
	 */
	public void addProcedimentoNaGuia(P procedimento, boolean validaDataRetroativa) throws Exception {
		
		validacaoGeralProcedimento(procedimento, validaDataRetroativa);
		
		if (usuarioDoFluxo != null && (usuarioDoFluxo.isPossuiRole(Role.ROOT.getValor())
			|| usuarioDoFluxo.isPossuiRole(Role.AUDITOR.getValor()) 
			|| usuarioDoFluxo.isPossuiRole(Role.DIRETORIA_MEDICA.getValor()))){
			
			procedimento.getSituacao().setUsuario(this.usuarioDoFluxo);
			procedimento.getSituacao().setDescricao(SituacaoEnum.AUTORIZADO.descricao());
			String motivo = Utils.isStringVazia(procedimento.getMotivoInsercao()) ? "Alteração de guia" : procedimento.getMotivoInsercao();
			procedimento.getSituacao().setMotivo(motivo);
		}
		
		procedimento.setGuia(this);
		procedimento.aplicaValorAcordo();
		this.valorTotal = this.valorTotal.add(procedimento.getValorTotal());
		this.getProcedimentos().add(procedimento);
		updateValorCoparticipacao();
		this.setValorParcial(procedimento.getValorTotal());
		this.atualizaValorApresentado(procedimento.getValorItem());
	}

	/**
	 * Método refatorado para permitir a inserção de procedimentos sem validação de data retroativa, e retrocompatível com o código legado.
	 *  
	 * @changes Matheus (24/09/2013)
	 * @param procedimento
	 * @throws Exception
	 */
	public void addProcedimento(P procedimento) throws Exception {
		addProcedimentoNaGuia(procedimento, true);
	}

	protected void validaSeDataDeRealizacaoEPosteriorADataAtendimento(P procedimento) throws ValidateException {
		Procedimento proc = (Procedimento) procedimento;
		GuiaSimples guia = proc.getGuia();
		if (guia != null) {
			Date dataAtendimento = guia.getDataAtendimento();
			Date dataRealizacao = proc.getDataRealizacao();
			if(dataAtendimento != null && dataRealizacao != null && Utils.compareData(dataRealizacao, dataAtendimento) < 0 ){
				throw new ValidateException("Não é posivel adicionar procedimentos com a data de realização anterior a data de atendimento da guia.");
			}
		}
	}
	
	/**
	 * Método criado para colocar validações gerais para inserção de procedimentos.
	 * 
	 * @changes Matheus: inseri a possibilidade de evitar a validação de dataretroativa
	 * @author Luciano Infoway
	 * @param procedimento
	 * @throws ValidateException
	 */
	protected void validacaoGeralProcedimento(P procedimento, Boolean validaDataRetroativa) throws ValidateException {
		
		if(validaDataRetroativa){
			if(!((ProcedimentoOutros.class.isInstance(procedimento) && ((ProcedimentoOutros)procedimento).isVisitaInseridaAutomaticamente()))){
				validaSeDataDeRealizacaoEPosteriorADataAtendimento(procedimento);
			}
		}
		
		procedimento.aplicaValorAcordo();
		
		if (procedimento.getPorcentagem() != null) {
			if (MoneyCalculation.compare(procedimento.getPorcentagem(), new BigDecimal(100)) == 0){
				boolean isMesmaTabelaCBHPM, isPorcentagem100,isMarcadoParaGlosa, isGlosado;
				boolean isMesmaData = true;
				for (ProcedimentoInterface procDaGuia : this.getProcedimentosCirurgicosNaoCanceladosENegados()) {
					isMesmaTabelaCBHPM = procDaGuia.getProcedimentoDaTabelaCBHPM().equals(procedimento.getProcedimentoDaTabelaCBHPM());
					isPorcentagem100 = MoneyCalculation.compare(procDaGuia.getPorcentagem(), new BigDecimal(100)) == 0;
					isMarcadoParaGlosa = ((ProcedimentoCirurgico) procDaGuia).isGlosar();
					isGlosado = procDaGuia.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao());
					
					boolean isProcedimentoCirurgico = procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO;
					
					if (isProcedimentoCirurgico) {
						Date dataRealizacao = ((ProcedimentoCirurgico) procedimento).getDataRealizacao();
						Date dtRealizacaoProcdaGuia = ((ProcedimentoCirurgico)procDaGuia).getDataRealizacao();
						
						boolean isDatasNulas = dataRealizacao == null && dtRealizacaoProcdaGuia == null;
						if (isMesmaTabelaCBHPM && !isMarcadoParaGlosa && !isGlosado && isPorcentagem100 && isDatasNulas) {
							throw new ValidateException("O procedimento já se encontra na guia a 100%.");
						}
						
						if (dataRealizacao != null && dtRealizacaoProcdaGuia != null) {
							isMesmaData = (Utils.compareData(dataRealizacao, dtRealizacaoProcdaGuia) == 0);
						} else {
							isMesmaData = false;
						}
						
						/*
						 * Pedro Almir:
						 * 
						 * Procedimentos cirurgicos possuem porcentagem variável, a qual representa a via de acesso utilizada.
						 * Por isso deve-se verificar se há dois procedimentos a 100% na guia. Entretanto, quando o procedimento 
						 * não é cirurgico sua porcentagem sempre retorna 100%, pois ele será pago por completo. Nesse caso não
						 * há necessidade de verificar se há dois procedimento a 100% dentro da guia.
						 * 
						 * Correção realizada: Alterar o local da validação.
						 * */
						if (isMesmaTabelaCBHPM && isPorcentagem100 && !isMarcadoParaGlosa && !isGlosado && isMesmaData) {
							throw new ValidateException("O procedimento já se encontra na guia a 100%.");
						}
					}
				}
			}
		}
		
		if(this.isInternacao() || this.isCirurgiaOdonto()) {
			procedimento.setValorCoParticipacao(BigDecimal.ZERO);
		}
	}
	
	public Set<ProcedimentoCirurgicoInterface> getProcedimentosCirurgicosNaoCanceladosENegados(){
		Set<ProcedimentoCirurgicoInterface> procedimentos = new HashSet<ProcedimentoCirurgicoInterface>();
		for(ProcedimentoInterface procedimento : getProcedimentosNaoCanceladosENegados()){
			if (procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO){
				procedimentos.add((ProcedimentoCirurgicoInterface)procedimento);
			}
		}
		return procedimentos;
	}

	/**
	 * Método que insere novos procedimentos na guia em caso de alteração
	 * @param procedimento
	 * @throws Exception
	 */
	public void addProcedimentoAlteracao(P procedimento) throws Exception {

		addProcedimento(procedimento);
		
		//Tipos de guias que sairão com procedimentos autorizado(a)s
		List<String> guiasParaProcedimentoAutorizado = Arrays.asList("IEL", "IUR", "AUR", "CIROD", "CUR", "GEX");
		
		if(guiasParaProcedimentoAutorizado.contains(this.getTipoDeGuia()))
			procedimento.mudarSituacao(this.usuarioDoFluxo,	SituacaoEnum.AUTORIZADO.descricao(), "Alteração de guia", new Date());
		
		//Guias de exame com situação aberta recebem procedimentos em situação autorizado(a)
		if(this.isExame() && this.getSituacao().getDescricao().equals(SituacaoEnum.ABERTO.descricao()))
			procedimento.mudarSituacao(this.usuarioDoFluxo, SituacaoEnum.AUTORIZADO.descricao(), "Alteração de guia", new Date());

		//Guias de exame com situação confirmada recebem procedimentos em situação confirmado(a)
		if(this.isExame() && this.getSituacao().getDescricao().equals(SituacaoEnum.CONFIRMADO.descricao()))
			procedimento.mudarSituacao(this.usuarioDoFluxo, SituacaoEnum.CONFIRMADO.descricao(), "Alteração de guia", new Date());
		
		//Guias de exame odontologico com situação solicitado(a) recebem procedimentos em situação solicitado(a) 
		if((this.isExameOdonto() || this.isExame()) && this.getSituacao().getDescricao().equals(SituacaoEnum.SOLICITADO.descricao()))
			procedimento.mudarSituacao(this.usuarioDoFluxo, SituacaoEnum.SOLICITADO.descricao(), "Alteração de guia", new Date());
		
		//Guias de exame odontologico com situacao autorizado(a) recebem procedimentos em situacao autorizado(a)
		if(this.isExameOdonto() && this.getSituacao().getDescricao().equals(SituacaoEnum.AUTORIZADO.descricao()))
			procedimento.mudarSituacao(this.usuarioDoFluxo, SituacaoEnum.AUTORIZADO.descricao(), "Alteração de guia", new Date());
		
		if (this.getIdGuia() != null) {
			if (this.prestador != null){
				this.getPrestador().atualizarConsumoFinanceiro(this, procedimento.getQuantidade(), TipoIncremento.POSITIVO);
			}
		}
	}

	public void cancelarProcedimento(P procedimento) throws Exception {
		procedimento.mudarSituacao(this.usuarioDoFluxo, SituacaoEnum.CANCELADO.descricao(), 
									MotivoEnum.CANCELADO_DURANTE_AUDITORIA.getMessage(), new Date());
		
		this.setValorTotal(this.getValorTotal().subtract(procedimento.getValorTotal()));
		
		updateValorCoparticipacao();
	}

	public void negarProcedimento(P procedimento) throws Exception {
		procedimento.mudarSituacao(this.getUsuarioDoFluxo(), SituacaoEnum.NEGADO.descricao(), "Negado(a) durante auditoria", new Date());
		this.setValorTotal(this.getValorTotal().subtract(procedimento.getValorTotal()));
	}

	public void addAllProcedimentos(Collection<? extends ProcedimentoInterface> procedimentos) throws Exception {
		for (Iterator<?> iter = procedimentos.iterator(); iter.hasNext();) {
			P element = (P) iter.next();
			this.addProcedimento(element);
		}
	}

	public void removeProcedimento(P procedimento, UsuarioInterface usuario) throws Exception {
		for (ProcedimentoInterface proced : this.getProcedimentos()) {
			if (proced.equals(procedimento) && !proced.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())) {

				// Atualizando valores
				this.valorTotal = this.valorTotal.subtract(proced.getValorTotal());

				if (this.getPrestador() != null){
					this.getPrestador().atualizarConsumoFinanceiro(this, proced.getQuantidade(), TipoIncremento.NEGATIVO);
				}

				this.getSegurado().atualizarLimites(this, TipoIncremento.NEGATIVO, proced.getQuantidade());
				
				String motivoCancelamento;
				
				if(procedimento.getMotivo() != null && procedimento.getMotivo().isEmpty()){ 
					motivoCancelamento = procedimento.getMotivo();
				} else {
					motivoCancelamento = MotivoEnum.EXCLUSAO_EXAME_PROCEDIMENTO.getMessage();
				}

				proced.mudarSituacao(usuario, SituacaoEnum.CANCELADO.descricao(), motivoCancelamento, new Date());
			}
		}
		updateValorCoparticipacao();
	}

	public void removeProcedimento(P procedimento) throws Exception {
		this.removeProcedimento(procedimento, this.usuarioDoFluxo);
		//TODO: descobrir o porquê que o valor apresentado está chegando null
		if(this.getValorTotalApresentado()!=null){
			this.atualizaValorApresentado(procedimento.getValorItem().negate());
		}
	}

	public int getNumeroAutenticacao() {
		return new HashCodeBuilder().append(this.getIdGuia()).append(
				this.getSegurado()).append(this.getPrestador()).append(
				this.getProfissional()).toHashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof GuiaSimples)) {
			return false;
		}
		GuiaSimples otherObject = (GuiaSimples) object;
		return new EqualsBuilder().append(this.idGuia, otherObject.getIdGuia())
				.append(this.getTipo(), otherObject.getTipo()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.getIdGuia()).append(this.getTipo()).toHashCode();
	}

	public List<P> getProcedimentosNaoRealizados() {
		List<P> procedimentosValidos = new ArrayList<P>();
		if (this.getProcedimentos() != null) {
			for (P procedimento : this.getProcedimentos()) {
				if (procedimento != null && !procedimento.isSituacaoAtual(SituacaoEnum.REALIZADO.descricao())){
					procedimentosValidos.add(procedimento);
				}
			}
		}
		return procedimentosValidos;
	}

	public List<P> getProcedimentosNaoAutorizados() {
		List<P> procedimentosValidos = new ArrayList<P>();
		if (this.getProcedimentos() != null) {
			for (P procedimento : this.getProcedimentos()) {
				if (procedimento != null && procedimento.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao())){
					procedimentosValidos.add(procedimento);
				}
			}
		}
		return procedimentosValidos;
	}
	
	public List<ProcedimentoInterface> getProcedimentosNaoAutorizadosOrdenado() {
		return ordenarProcedimentosPorSituacaoEDescricao((Collection<ProcedimentoInterface>) getProcedimentosNaoAutorizados());
	}

	/**
	 * Fornece os procedimentos que não estejam nas situações a seguir:
	 * <ul>
	 * <li>Realizado(a)</li>
	 * <li>Não Autorizado(a)</li>
	 * <li>Solicitado(a)</li>
	 * <li>Glosado(a)</li>
	 * <li>Cancelado(a)</li>
	 * <li>Negado(a)</li>
	 * </ul>
	 * @return
	 * 
	 */
	public List<P> getProcedimentosNaoRealizadosNaoNaoAutorizadosENaoSolicitados() {
		List<P> procedimentosValidos = new ArrayList<P>();
		if (this.getProcedimentos() != null) {
			for (P procedimento : this.getProcedimentos()) {
				if (procedimento != null
						&& !procedimento.isSituacaoAtual(SituacaoEnum.REALIZADO.descricao())
						&& !procedimento.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao())
						&& !procedimento.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())
						&& !procedimento.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao())
						&& !procedimento.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())
						&& !procedimento.isSituacaoAtual(SituacaoEnum.NEGADO.descricao())
						&& !procedimento.isSituacaoAtual(SituacaoEnum.PENDENTE.descricao()))
					procedimentosValidos.add(procedimento);
			}
		}
		return procedimentosValidos;
	}

	public List<P> getProcedimentosNaoRealizadosNaoNaoAutorizadosENaoSolicitadosOrdenado() {
		return ordenarProcedimentosPorDescricao(getProcedimentosNaoRealizadosNaoNaoAutorizadosENaoSolicitados());
	}

	public List<P> getProcedimentosRealizados() {
		List<P> procedimentosValidos = new ArrayList<P>();
		if (this.getProcedimentos() != null) {
			for (P procedimento : this.getProcedimentos()) {
				if (procedimento != null && procedimento.isSituacaoAtual(SituacaoEnum.REALIZADO.descricao()))
					procedimentosValidos.add(procedimento);
			}
		}
		return procedimentosValidos;
	}

	public <T extends TabelaCBHPM> List<T> getProcedimentosDaTabelaCBHPM() {
		List<T> procedimentos = new ArrayList<T>();
		if (this.getProcedimentos() != null) {
			for (P procedimentoAtual : this.getProcedimentos()) {
				if (!procedimentoAtual.getSituacao().equals(
						SituacaoEnum.CANCELADO.descricao())
						&& !procedimentoAtual.getSituacao().equals(
								SituacaoEnum.NAO_AUTORIZADO.descricao()))
					if (procedimentoAtual != null)
						procedimentos.add((T) procedimentoAtual
								.getProcedimentoDaTabelaCBHPM());
			}
		}
		return procedimentos;
	}

	public List<P> getProcedimentosConfirmados() {
		List<P> procedimentosValidos = new ArrayList<P>();
		if (this.getProcedimentos() != null) {
			for (P procedimento : this.getProcedimentos()) {
				if (procedimento.isSituacaoAtual(SituacaoEnum.CONFIRMADO
						.descricao()))
					procedimentosValidos.add((P) procedimento);
			}
		}
		return procedimentosValidos;
	}

	public void tocarObjetos() {
		Prestador prestador = this.getPrestador();
		if(this.getUltimoLote() != null){
			this.getUltimoLote().getIdentificador(); 
		}
		
		if (prestador != null){
			prestador.tocarObjetos();
		}
		
		SeguradoInterface segurado = this.getSegurado();
		if (segurado != null){
			segurado.tocarObjetos();
		}

		if (this.getProfissional() != null){
			this.getProfissional().tocarObjetos();
		}

		if (this.getSolicitante() != null)
			this.getSolicitante().getSituacao().getDescricao();
		this.getEspecialidade();

		for (SituacaoInterface situacao : getSituacoes()) {
			situacao.getUsuario();
			if (situacao.getUsuario() != null){
				situacao.getUsuario().getLogin();
			}
		}
		
		this.getRecebido();

		if (this.getEspecialidade() != null)
			this.getEspecialidade().tocarObjetos();

		this.getIdGuia();

		if(getGuiaOrigem() != null)
			this.getGuiaOrigem().tocarObjetos();

		this.getProcedimentos().size();
		for (P procedimento : this.getProcedimentos())
			procedimento.tocarObjetos();

		this.getGuiasFilhas().size();

		if (this.getFaturamento() != null) {
			this.getFaturamento().getGuiasRecursoGlosa();
		}
		
	}

	public Boolean isConfirmacao() {
		return this.isSituacaoAtual(SituacaoEnum.CONFIRMADO.descricao());
	}

	public Boolean isAgendamento() {
		return this.isSituacaoAtual(SituacaoEnum.AGENDADA.descricao());
	}

	// TODO retirar este metodo
	@SuppressWarnings("unused")
	private BigDecimal getValor(int i, boolean somenteConfirmados) {
		BigDecimal valor = new BigDecimal(0f);
		for (ProcedimentoInterface procedimento : this.getProcedimentos()) {
			if (somenteConfirmados
					&& (procedimento.isSituacaoAtual(SituacaoEnum.CONFIRMADO
							.descricao()) || procedimento
							.isSituacaoAtual(SituacaoEnum.FATURADA.descricao()))) {
				valor = valor.add(this.getValor(i, procedimento));
			} else {
				if (!procedimento.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao())
						|| !procedimento.isSituacaoAtual(SituacaoEnum.REALIZADO.descricao())
						|| !procedimento.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())) {
					valor = valor.add(this.getValor(i, procedimento));
				}
			}
		}
		return valor;
	}

	private BigDecimal getValor(int i, ProcedimentoInterface procedimento) {
		return procedimento.getValorTotal();
	}

	public void recalcularValores() {
		CommandCorrecaoValorGuia command = new CommandCorrecaoValorGuia(this);
		command.execute();
	}

	public boolean isPossuiProcedimentosAtivos() {
		return !this.getProcedimentosAtivos().isEmpty();
	}

	public List<P> getProcedimentosAtivos() {
		List<P> procedimentosValidos = new ArrayList<P>();
		if (this.getProcedimentos() != null) {
			for (P procedimento : this.getProcedimentos()) {
				if (!procedimento.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao())
						&& !procedimento.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())
						&& !procedimento.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao())
						&& !procedimento.isSituacaoAtual(SituacaoEnum.NEGADO.descricao())
						&& !procedimento.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao()))
					
					procedimentosValidos.add(procedimento);
			}
		}
		return procedimentosValidos;
	}

	/**
	 * Procedimentos que entram na contagem do consumo devido à natureza do
	 * mesmo
	 * 
	 * Procedimentos que não estejam nas seguintes situações contam nos consumos:
	 *  
	 * <ul>
	 * <li>Glosado(a)</li>
	 * <li>Cancelado(a)</li>
	 * <li>Não Autorizado(a)</li>
	 * <li>Negado(a)</li>
	 * <li>Realizado(a)</li>
	 * </ul>
	 * @return
	 * 
	 * 
	 * @return List
	 */
	public List<P> getProcedimentosValidosParaConsumo() {
		List<P> procedimentosConsumo = new ArrayList<P>();
		for (P procedimento : this.getProcedimentos()) {
			if (!TipoConsultaEnum.isConsulta(procedimento.getProcedimentoDaTabelaCBHPM().getCodigo())
					&& !procedimento.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao())
					&& !procedimento.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())
					&& !procedimento.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao())
					&& !procedimento.isSituacaoAtual(SituacaoEnum.NEGADO.descricao())
					&& !procedimento.isSituacaoAtual(SituacaoEnum.REALIZADO.descricao()))
				
				procedimentosConsumo.add(procedimento);
		}

		return procedimentosConsumo;
	}
	
	public Integer getQuantidadeProcedimentos() {
		if(quantidadeProcedimentos == null){
			return 0;
		}
		return quantidadeProcedimentos;
	}
	
	public Integer getQuantidadeDeProcedimentos(TabelaCBHPM procedimento) {
		Integer quantidade = 0;
		for (ProcedimentoInterface proc : this.getProcedimentosNaoCanceladosENegados()) {
			if (proc.getProcedimentoDaTabelaCBHPM().equals(procedimento)) {
				quantidade += proc.getQuantidade();
			}
		}
		return quantidade;
	}
	
	public float getValorTotalDoProcedimento(TabelaCBHPM procedimento) {
		BigDecimal valorTotal = BigDecimal.ZERO;
		for (ProcedimentoInterface proc : this.getProcedimentosNaoCanceladosENegados()) {
			if (proc.getProcedimentoDaTabelaCBHPM().equals(procedimento)) {
				valorTotal = valorTotal.add(proc.getValorTotal());
			}
		}
		return valorTotal.floatValue();
	}
	
	@SuppressWarnings("unused")
	private void setQuantidadeProcedimentos(Integer quantidadeProcedimentos) {
		this.quantidadeProcedimentos = quantidadeProcedimentos;
	}

	public int getQuantidadeProcedimentosValidosParaConsumo() {
		 int quantidade = 0;
		 for (P procedimento : this.getProcedimentosValidosParaConsumo())
		 quantidade += procedimento.getQuantidade();
				
		 return quantidade;
	}

	public List<P> getProcedimentosSolicitados() {
		return getProcedimentos(SituacaoEnum.SOLICITADO.descricao());
	}

	public List<P> getProcedimentosSolicitadosOuPendentes() {
		return getProcedimentos(SituacaoEnum.SOLICITADO.descricao(), SituacaoEnum.PENDENTE.descricao());
	}
	
	public List<P> getProcedimentosAutorizados() {
		return getProcedimentos(SituacaoEnum.AUTORIZADO.descricao());
	}

	public Set<ProcedimentoInterface> getExamesEspeciaisNaoAutorizados() {
		Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();

		Set<ProcedimentoInterface> procedimentosSolicitados = new HashSet<ProcedimentoInterface>(
				getProcedimentos(SituacaoEnum.SOLICITADO.descricao()));

		Boolean isProcedimentosSimples;
		for (ProcedimentoInterface procedimento : procedimentosSolicitados) {
			isProcedimentosSimples = !(procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO)
					&& !(procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_OUTROS);

			if (isProcedimentosSimples)
				procedimentos.add(procedimento);
		}
		return procedimentos;
	}
	
	public Set<ProcedimentoInterface> getExamesAutorizados(){

		boolean isAutorizado = false;

		Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();
		for (ProcedimentoInterface procedimento : getProcedimentos(SituacaoEnum.SOLICITADO.descricao())) {
			if (procedimento.getTipoProcedimento() != ProcedimentoInterface.PROCEDIMENTO_CIRURGICO) {
				isAutorizado = procedimento.getAutorizado() == null? false : procedimento.getAutorizado();
				if(procedimento.isPassouPelaAutorizacao() && isAutorizado){
					procedimentos.add(procedimento);
				}
			}
		}
		return procedimentos;
	}
	
	public Set<ProcedimentoInterface> getCirurgiasAutorizados(){

		boolean isAutorizado = false;

		Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();
		for (ProcedimentoInterface procedimento : getProcedimentos(SituacaoEnum.SOLICITADO.descricao())) {
			if (procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO) {
				isAutorizado = procedimento.getAutorizado() == null? false : procedimento.getAutorizado();
				if(procedimento.isPassouPelaAutorizacao() && isAutorizado){
					procedimentos.add(procedimento);
				}
			}
		}
		return procedimentos;
	}

	public Set<ProcedimentoInterface> getProcedimentosAutorizacaoNegada(){

		boolean isNegado = false;

		Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();
		for (ProcedimentoInterface procedimento : getProcedimentos(SituacaoEnum.SOLICITADO.descricao())) {
			isNegado = procedimento.getAutorizado() != null  && !procedimento.getAutorizado();
			if(procedimento.isPassouPelaAutorizacao() && isNegado){
				procedimentos.add(procedimento);
			}
		}
		return procedimentos;
	}

	public Set<ProcedimentoInterface> getProcedimentosCirurgicosNaoAutorizados() {
		Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();

		Set<ProcedimentoInterface> procedimentosSolicitados = new HashSet<ProcedimentoInterface>(
				getProcedimentos(SituacaoEnum.SOLICITADO.descricao()));

		Boolean isProcedimentosCirurgicos;
		for (ProcedimentoInterface procedimento : procedimentosSolicitados) {
			isProcedimentosCirurgicos = (procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO);

			if (isProcedimentosCirurgicos)
				procedimentos.add(procedimento);
		}
		return procedimentos;
	}

	public List<P> getProcedimentos(String... descricao) {
		List<P> procedimentosValidos = new ArrayList<P>();
		
		if (this.getProcedimentos() != null) {
			for (P procedimento : this.getProcedimentos()) {
				boolean isValido = false;
				for (String situacao : descricao)
					if (procedimento.isSituacaoAtual(situacao)) {
						isValido = true;
						break;
					}
				
				if (isValido)
					procedimentosValidos.add(procedimento);
			}
		}
		
		return procedimentosValidos;
	}
	
	public <T extends ProcedimentoInterface> List<T> getProcedimentosIn(
			Class<T> tipoProcedimento, SituacaoEnum... descricao) {
		return getProcedimentos(tipoProcedimento, true, descricao);
	}
	
	public <T extends ProcedimentoInterface> List<T> getProcedimentosNotIn(
			Class<T> tipoProcedimento, SituacaoEnum... descricao) {
		return getProcedimentos(tipoProcedimento, false, descricao);
	}

	private <T extends ProcedimentoInterface> List<T> getProcedimentos(
			Class<T> tipoProcedimento, boolean vai, SituacaoEnum... descricao) {
		List<T> procedimentosValidos = new ArrayList<T>();
		if (this.getProcedimentos() != null) {
			for (T procedimento : this.getProcedimentos(tipoProcedimento)) {
				if(vai){
					for (SituacaoEnum situacao : descricao){
						if (procedimento.isSituacaoAtual(situacao.descricao())) {
							procedimentosValidos.add(procedimento);
							break;
						}
					}	
				} else {
					boolean isValido = true;
					for (SituacaoEnum situacao : descricao){
						if (procedimento.isSituacaoAtual(situacao.descricao())) {
							isValido = false;
							break;
						}
					}
					if (isValido){
						procedimentosValidos.add(procedimento);
					}
				}
			}
		}
		return procedimentosValidos;
	}

	public boolean temProcedimentoGlosado() {
		boolean temProcedimento = false;
		for (ProcedimentoInterface procedimento : this.procedimentos) {
			if (procedimento.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao()))
				temProcedimento = true;
		}
		return temProcedimento;
	}

	public BigDecimal getValorProcedimentosGlosados() {
		BigDecimal valor = BigDecimal.ZERO;
		if (!this.temProcedimentoGlosado()) {
			return valor;
		} else {
			for (ProcedimentoInterface procedimento : this.getProcedimentos()) {
				if (procedimento.isSituacaoAtual(SituacaoEnum.GLOSADO
						.descricao())) {
					valor = valor.add(procedimento.getValorTotal());
				}
			}
		}
		return valor;
	}

	public boolean isGuiaValidaParaGlosa() {
		String situacaoAtual = this.getSituacao().getDescricao();
		boolean isGuiaAgendada = situacaoAtual.equals(SituacaoEnum.AGENDADA.descricao());
		boolean isGuiaConfirmada = situacaoAtual.equals(SituacaoEnum.CONFIRMADO.descricao());
		boolean isGuiaFaturada = situacaoAtual.equals(SituacaoEnum.FATURADA.descricao());

		if (isGuiaAgendada || isGuiaConfirmada || isGuiaFaturada)
			return true;

		return false;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE).append(
				"id", this.idGuia).toString();
	}

	public UsuarioInterface getUsuarioDoFluxo() {
		return usuarioDoFluxo;
	}

	public void setUsuarioDoFluxo(UsuarioInterface usuarioDoFluxo) {
		this.usuarioDoFluxo = usuarioDoFluxo;
	}

	public FluxoFinanceiroInterface getFluxoFinanceiro() {
		return fluxoFinanceiro;
	}

	public void setFluxoFinanceiro(FluxoFinanceiroInterface fluxoFinanceiro) {
		this.fluxoFinanceiro = fluxoFinanceiro;
	}

	public String getMensagemLimite() {
		return mensagemLimite;
	}

	public void setMensagemLimite(String mensagemLimite) {
		this.mensagemLimite = mensagemLimite;
	}

	public SituacaoInterface getSituacaoAnterior(SituacaoInterface situacao) {
		int ordemSituacao = situacao.getOrdem();
		if (ordemSituacao == 1) {
			Situacao sit = new Situacao();
			sit.setDescricao("not null");
			return sit;
		}
		for (SituacaoInterface situacaoAtual : this.getSituacoes()) {
			if (situacaoAtual.getOrdem() == ordemSituacao - 1)
				return situacaoAtual;
		}
		return situacao;
	}

	public Boolean isPossuiProcedimentosSolicitados() {
		for (ProcedimentoInterface procedimento : procedimentos) {
			if (procedimento.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())
				|| procedimento.isSituacaoAtual(SituacaoEnum.PENDENTE.descricao()))
				return true;
			}

		return false;
	}

	/**
	 * Busca a guia origem desta guia dentro de um periodo.
	 */
	private <G extends GuiaSimples> List<G> buscarGuiaOrigem(Class<G> klass, Integer periodo) throws ValidateException {
		return buscarGuiaOrigem(klass, true, periodo);
	}

	public List<GuiaConsulta> buscarConsultaOrigem() throws ValidateException {
		return buscarGuiaOrigem(GuiaConsulta.class, PERIODO_VALIDADE_CONSULTA_PARA_SOLICITACAO_EXAME);
	}

	public List<GuiaConsultaOdonto> buscarConsultaOdontoOrigem()
			throws ValidateException {
		return buscarGuiaOrigem(GuiaConsultaOdonto.class, 90);
	}
	
	/**
	 * Busca guia origem a partir dos parâmetros informados
	 * 
	 * @param klass classe  
	 * @param comProfissional levar em consideração profissional da guia atual como parâmetro
	 * @param periodo período em dias decrescente a partir da data atual
	 * @return guia origem
	 */
	public <G extends GuiaSimples> List<G> buscarGuiaOrigem(Class<G> klass, boolean comProfissional, Integer periodo) {

	    if (this.getDataAtendimento() == null){
			return new ArrayList<G>();
	    }

		Calendar calendario = Calendar.getInstance();
		calendario.setTime(this.getDataAtendimento());
		calendario.add(Calendar.DAY_OF_MONTH, -(periodo));
		Date dataLimite = calendario.getTime();

		Session session = HibernateUtil.currentSession();
		Criteria criteria = session.createCriteria(klass);

		criteria.add(Expression.eq("segurado", this.getSegurado()));
	    
	    if (comProfissional) {
		criteria.add(Expression.eq("profissional", this.getSolicitante()));
	    }
	    
		criteria.add(Expression.ge("dataAtendimento", dataLimite));
		criteria.add(Expression.or(Expression.eq("situacao.descricao",
				SituacaoEnum.CONFIRMADO.descricao()), Expression.eq(
				"situacao.descricao", SituacaoEnum.FATURADA.descricao())));
		criteria.addOrder(Order.desc("dataAtendimento"));

		return criteria.list();
	}

	/**
	 * Fornece dinamicamente os honorários gerados na guia.
	 * Substituído pelo método {@link #getHonorarios()}
	 */
	@Deprecated
	public List<ValoresProfissional> getValoresProfissionais() {
		List<ValoresProfissional> valores = new ArrayList<ValoresProfissional>();
		for (ProcedimentoInterface procedimento : getProcedimentosNaoCanceladosENegados()) {
			boolean isGuiaFaturada = procedimento
					.isSituacaoAtual(SituacaoEnum.FATURADA.descricao());
			boolean isGuiaFechada = procedimento
					.isSituacaoAtual(SituacaoEnum.FECHADO.descricao());

			if (isGuiaFechada || isGuiaFaturada) {
				ValoresProfissional valorProfissional = new ValoresProfissional();
				valorProfissional.setProcedimento(procedimento);
				valorProfissional.setFuncao(ValoresProfissional.ANESTESISTA);
				valorProfissional.setValor(procedimento.getValorAnestesista());
				valorProfissional
						.setProfissional(procedimento.getAnestesista());
				valores.add(valorProfissional);
			}
		}
		return valores;
	}
	
	/**
	 * Fornece todos os honorários gerados dentro dessa guia.
	 * Se for uma guia origem, <b>NÃO</b> inclui os honorarios das guias de honorario médico (ou seja, das guias filhas).
	 * Se for a guia de honorário médico, retorna, naturalmente, seus próprios honorários 
	 * ({@link GuiaHonorarioMedico#getHonorarios()}).
	 * @return List<Honorario>
	 */
	public List<Honorario> getHonorarios(){
		List<Honorario> honorariosDaGuia = new ArrayList<Honorario>();
		for (ProcedimentoInterface procedimento : getProcedimentos()) {
			honorariosDaGuia.addAll(procedimento.getHonorariosGuiaOrigem());
		}
		return honorariosDaGuia;
	}
	
	public List<Honorario> getHonorariosInternosNaoGlosado(){
		List<Honorario> honorariosDaGuia = new ArrayList<Honorario>();
		for (ProcedimentoInterface procedimento : getProcedimentos()) {
			for (Honorario honorario : procedimento.getHonorariosGuiaOrigem()) {
				if (!honorario.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao())) {
					honorariosDaGuia.add(honorario);
				}
			}
		}
		return honorariosDaGuia;
	}
	
	/**
	 * Este método cria os objetos honorários que serão pagos dentro da própria guia
	 * que contém os procedimentos cirúrgicos. Para resgatá-los, usar o {@link #getHonorarios()}.
	 * @throws ValidateException 
	 */
	public void calculaHonorariosInternos(UsuarioInterface usuario) throws ValidateException{
		validaHonorariosInternos();
		//TODO pq q ele ja n add la msm, ja q tds essas informacoes estao nele msm?
		//colocar a porcentagem aki ou lá?
		for (ProcedimentoInterface procedimento : getProcedimentosNaoCanceladosENegados()) {
			calculaHonorario(usuario, procedimento, procedimento.getProfissionalAuxiliar1Temp(), GrauDeParticipacaoEnum.PRIMEIRO_AUXILIAR.getCodigo(), procedimento.getValorAuxiliar1());
			calculaHonorario(usuario, procedimento, procedimento.getProfissionalAuxiliar2Temp(), GrauDeParticipacaoEnum.SEGUNDO_AUXILIAR.getCodigo(), procedimento.getValorAuxiliar2());
			calculaHonorario(usuario, procedimento, procedimento.getProfissionalAuxiliar3Temp(), GrauDeParticipacaoEnum.TERCEIRO_AUXILIAR.getCodigo(), procedimento.getValorAuxiliar3());
		}
	}
	
	private void calculaHonorario(UsuarioInterface usuario, ProcedimentoInterface procedimento, Profissional profissional, int grauDeParticipacao, BigDecimal valor) {
		//desse jeito aki, ele n deixa+alterar...
		if(profissional != null) {
			if (procedimento.containsHonorario(grauDeParticipacao)) {
				Honorario honorarioInterno = procedimento.getHonorarioInterno(grauDeParticipacao);
				if (!honorarioInterno.getProfissional().equals(profissional)) {
					honorarioInterno.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), MotivoEnum.HONORARIO_GLOSADO.getMessage(), new Date());
					procedimento.addHonorarioGuiaOrigem(usuario, grauDeParticipacao, profissional, valor);
				}
			} else {
				procedimento.addHonorarioGuiaOrigem(usuario, grauDeParticipacao, profissional, valor);
			}
		}
	}
	
	private void validaHonorariosInternos() throws ValidateException{
		for (ProcedimentoInterface procedimento : getProcedimentosNaoCanceladosENegados()) {
			if (procedimento.getProfissionalResponsavelTemp() != null && procedimento.containsHonorarioExterno(GrauDeParticipacaoEnum.RESPONSAVEL.getCodigo())) {
				throw new ValidateException(MensagemErroEnum.HONORARIO_DUPLICADO.getMessage(GrauDeParticipacaoEnum.RESPONSAVEL.getDescricao(), procedimento.getProcedimentoDaTabelaCBHPM().getCodigoEDescricao()));
			}

			if (procedimento.getProfissionalAuxiliar1Temp() != null && procedimento.containsHonorarioExterno(GrauDeParticipacaoEnum.PRIMEIRO_AUXILIAR.getCodigo())) {
				throw new ValidateException(MensagemErroEnum.HONORARIO_DUPLICADO.getMessage(GrauDeParticipacaoEnum.PRIMEIRO_AUXILIAR.getDescricao(), procedimento.getProcedimentoDaTabelaCBHPM().getCodigoEDescricao()));
			}

			if (procedimento.getProfissionalAuxiliar2Temp() != null && procedimento.containsHonorarioExterno(GrauDeParticipacaoEnum.SEGUNDO_AUXILIAR.getCodigo())) {
				throw new ValidateException(MensagemErroEnum.HONORARIO_DUPLICADO.getMessage(GrauDeParticipacaoEnum.SEGUNDO_AUXILIAR.getDescricao(), procedimento.getProcedimentoDaTabelaCBHPM().getCodigoEDescricao()));
			}

			if (procedimento.getProfissionalAuxiliar3Temp() != null && procedimento.containsHonorarioExterno(GrauDeParticipacaoEnum.TERCEIRO_AUXILIAR.getCodigo())) {
				throw new ValidateException(MensagemErroEnum.HONORARIO_DUPLICADO.getMessage(GrauDeParticipacaoEnum.TERCEIRO_AUXILIAR.getDescricao(), procedimento.getProcedimentoDaTabelaCBHPM().getCodigoEDescricao()));
			}
		}
	}

	public Set<ProcedimentoInterface> getProcedimentosParaFechamentoAnestesista() {
		Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();

		for (ProcedimentoInterface procedimento : this.getProcedimentosNaoCanceladosENegados()) {
			
			boolean isAutorizado = procedimento.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao());
			
			if (procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO && isAutorizado)
				procedimentos.add(procedimento);

			boolean isConfirmadoOuRealizado = procedimento
					.isSituacaoAtual(SituacaoEnum.CONFIRMADO.descricao())
					|| procedimento.isSituacaoAtual(SituacaoEnum.REALIZADO
							.descricao());
			boolean isPossuiAnestesia = procedimento
					.getProcedimentoDaTabelaCBHPM()
					.getPorteAnestesicoFormatado() > 0;
			
			if (procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_NORMAL	&& isConfirmadoOuRealizado && isPossuiAnestesia)
				procedimentos.add(procedimento);
			}

		return procedimentos;
	}
	
	/**
	 * Fornece um conjunto de procedimentos que não esteja nas situações
	 * Cancelado(a), Não Autorizado(a) e Negado(a).
	 * @return
	 */
	public Set<ProcedimentoInterface> getProcedimentosNaoCanceladosENegados() {
		Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();
		for (ProcedimentoInterface procedimento : this.getProcedimentos()) {
			if (!procedimento.getSituacao().getDescricao().equals(SituacaoEnum.CANCELADO.descricao())
					&& !procedimento.getSituacao().getDescricao().equals(SituacaoEnum.NAO_AUTORIZADO.descricao())
					&& !procedimento.getSituacao().getDescricao().equals(SituacaoEnum.NEGADO.descricao()))
				procedimentos.add(procedimento);
		}
		return procedimentos;
	}
	
	public List<ProcedimentoInterface> getProcedimentosNaoCanceladosNaoNegadosNaoExcluidosOrdenado(){
		Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();
		for (ProcedimentoInterface procedimento : this.getProcedimentos()) {
			if (!procedimento.getSituacao().getDescricao().equals(SituacaoEnum.CANCELADO.descricao())
					&& !procedimento.getSituacao().getDescricao().equals(SituacaoEnum.NAO_AUTORIZADO.descricao())
					&& !procedimento.getSituacao().getDescricao().equals(SituacaoEnum.NEGADO.descricao())
					&& !procedimento.getSituacao().getDescricao().equals(SituacaoEnum.REMOVIDO.descricao()))
				procedimentos.add(procedimento);
		}
		return ordenarProcedimentosPorSituacaoEDescricao(procedimentos);
	}
	
	public List<ProcedimentoInterface> getProcedimentosNaoCanceladosENegadosOrdenado(){
		return ordenarProcedimentosPorSituacaoEDescricao(getProcedimentosNaoCanceladosENegados());
	}
	
	public int getQuantidadeProcedimentosNaoCanceladosENegados(){
		int quant = 0;
		for (ProcedimentoInterface procedimento : getProcedimentosNaoCanceladosENegados()) {
			quant += procedimento.getQuantidade();
		}
		return quant;
	}
	
	public BigDecimal getTotalProcedimentosNaoCanceladosENegados(){
		BigDecimal total = MoneyCalculation.rounded(BigDecimal.ZERO);
		for (ProcedimentoInterface procedimento : getProcedimentosNaoCanceladosENegados()) {
			total = total.add(procedimento.getValorTotal());
		}
		return total;
	}
	
	/**
	 * Fornece um conjunto de procedimentos que não esteja nas situações
	 * Cancelado(a), Não Autorizado(a), Negado(a) e Glosado(a).
	 * Semelhante a {@link #getProcedimentosNaoCanceladosENegados()}, só que também
	 * considera a situação Glosado(a), como já foi dito.
	 * @return
	 */
	public Set<ProcedimentoInterface> getProcedimentosNaoGlosadosNemCanceladosNemNegados() {
		Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();
		for (ProcedimentoInterface procedimento : this.getProcedimentos()) {
			if (!procedimento.getSituacao().getDescricao().equals(SituacaoEnum.CANCELADO.descricao())
					&& !procedimento.getSituacao().getDescricao().equals(SituacaoEnum.NAO_AUTORIZADO.descricao())
					&& !procedimento.getSituacao().getDescricao().equals(SituacaoEnum.NEGADO.descricao())
					&& !procedimento.getSituacao().getDescricao().equals(SituacaoEnum.GLOSADO.descricao())
					&& !procedimento.getSituacao().getDescricao().equals(SituacaoEnum.REMOVIDO.descricao())
					&& !procedimento.getSituacao().getDescricao().equals(SituacaoEnum.PENDENTE.descricao())
					)
				procedimentos.add(procedimento);
		}
		return procedimentos;
	}
	
	public abstract boolean isRegulaConsumo();

	/**
	 * Verifica todas as condições necessárias para que uma guia seja
	 * contabilizada<br>
	 * no cálculo da coparticipação do segurado.<be>
	 * 
	 * @return <code>true</code> caso a guia possa ser contabilizada na
	 *         coparticipação e<br>
	 *         <code>false</code> caso contrário.
	 */
	public boolean isAptaAoCalculoDeCoparticipacao() {

		if(this.isInternacao() || this.isExameExterno())
			return false;
		
		boolean isConfirmado = this.isSituacaoAtual(SituacaoEnum.CONFIRMADO
				.descricao());
		boolean isFaturado = this.isSituacaoAtual(SituacaoEnum.FATURADA
				.descricao());
		boolean isAuditado = this.isSituacaoAtual(SituacaoEnum.AUDITADO
				.descricao());
		boolean isAptaAoCalculoDaCoparticipacao = this.getFluxoFinanceiro() == null;
		
		if ((isConfirmado || isFaturado || isAuditado)
				&& (isAptaAoCalculoDaCoparticipacao)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Verifica se a guia está numa situação favorável ao fechamento pelo
	 * anestesista, e verifica também se a guia possui procedimentos para serem
	 * fechados.
	 * 
	 * @return true caso esteja apta ao fechamento.
	 */
	public boolean isAptaParaFechamentoAnestesista() {
		if (this.isPossuiItemParaFechamentoAnestesista()
				&& (!this.isSituacaoAtual(SituacaoEnum.REALIZADO.descricao()))
				&& (!this.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao()))
				&& (!this.isSituacaoAtual(SituacaoEnum.SOLICITADO_INTERNACAO
						.descricao()))
				&& (!this.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())))
			return true;

		return false;
	}

	/**
	 * calcula o somatório do valor pago aos anestesistas
	 * 
	 * @return
	 */
	public BigDecimal getValorTotalAnestesista() {
		BigDecimal valorTotal = BigDecimal.ZERO;
		if (getProcedimentos().size() > 0) {
			for (ProcedimentoInterface procedimento : getProcedimentos(
					SituacaoEnum.FECHADO.descricao(), SituacaoEnum.FATURADA
							.descricao())) {
				valorTotal = valorTotal.add(procedimento.getValorAnestesista());
			}
		}

		return valorTotal;
	}

	public String getValorTotalAnestesistaFormatado() {
		return StringUtils.leftPad(new DecimalFormat("###,###,##0.00")
				.format(getValorTotalAnestesista().setScale(2,
						BigDecimal.ROUND_HALF_UP)), 9, " ");
	}

	/**
	 * Verifica se há algum procedimento para ser fechado pelo prestador
	 * Anestesista nesta guia. Estão aptos para fechamento: procedimentos
	 * cirúrgicos AUTORIZADOS e exames que estejam CONFIRMADOS e tenham porte
	 * anestesico maior que 1
	 * 
	 * @return true se encontrar algum procedimento para ser fechado.
	 */
	public boolean isPossuiItemParaFechamentoAnestesista() {
		for (ProcedimentoInterface procedimento : this.getProcedimentosNaoCanceladosENegados()) {
			
			boolean isAutorizado = procedimento
					.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao());
			if (procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO
					&& isAutorizado)
				return true;

			boolean isConfirmadoOuRealizado = procedimento
					.isSituacaoAtual(SituacaoEnum.CONFIRMADO.descricao())
					|| procedimento.isSituacaoAtual(SituacaoEnum.REALIZADO.descricao());
			
			boolean isPossuiAnestesia = procedimento
					.getProcedimentoDaTabelaCBHPM()
					.getPorteAnestesicoFormatado() > 0;
			if (procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_NORMAL
					&& isConfirmadoOuRealizado && isPossuiAnestesia)
				return true;

		}
		
		return false;
	}

	/**
	 * Atualiza o valor coparticipação da guia. Soma o valor da coparticipação de cada 
	 * procedimento que não esteja "Cancelado(a)" ou "Não Autorizado(a)".
	 */
	public void updateValorCoparticipacao() {
		if (this.isExameExternoInternacao() || this.isInternacao() || this.isHonorarioMedico()) {
			valorCoParticipacao =  BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		}else{
			BigDecimal valor = BigDecimal.ZERO;
			for (ProcedimentoInterface procedimento : this.getProcedimentos()) {
				if (procedimento.getValorCoParticipacao() != null
					&& !procedimento.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())
					&& !procedimento.isSituacaoAtual(SituacaoEnum.REMOVIDO.descricao())
					&& !procedimento.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao())
					&& !procedimento.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao())){
					valor = MoneyCalculation.getSoma(valor, procedimento.getValorCoParticipacao().floatValue());
				}
			}
			
			BigDecimal valorMaximoCoparticipacao = Procedimento.TETO_CO_PARTICIPACAO;
			if (valor.compareTo(valorMaximoCoparticipacao) > 0){
				valor = valorMaximoCoparticipacao;
			}
			
			valorCoParticipacao = valor;
		}
		
	}

	public BigDecimal getValorParcialAnestesista() {
		return valorParcialAnestesista;
	}

	public void setValorParcialAnestesista(BigDecimal valorParcialAnestesista) {
		this.valorParcialAnestesista = valorParcialAnestesista;
	}

	public boolean isExameExternoInternacao() {
		if (this.isExameExterno() && this.getGuiaOrigem().isInternacao()){
			return true;
		}
		return false;
	}

	public boolean isExameExternoAtendimentosUrgencia() {
		if (this.isExameExterno() && (this.getGuiaOrigem().isConsultaUrgencia() || this.getGuiaOrigem().isAtendimentoUrgencia())){
			return true;
		}
		return false;
	}

	public boolean isPermiteMatMed() {

		for (ProcedimentoInterface procedimento : getProcedimentosNaoCanceladosENegados()) {

			if (procedimento.getProcedimentoDaTabelaCBHPM().isPermiteMaterialComplementar()) {
				return true;
			}
			if (procedimento.getProcedimentoDaTabelaCBHPM().isPermiteMedicamentoComplementar()) {
				return true;
			}
		}

		return false;
	}

	public boolean isPrazoParaFechamentoTerminou() {
		return true;
	}

	public MotivoGlosa getMotivoGlosa() {
		return motivoGlosa;
	}

	public void setMotivoGlosa(MotivoGlosa motivoGlosa) {
		this.motivoGlosa = motivoGlosa;
	}

	public String getMotivoParaGlosaTotal() {
		return motivoParaGlosaTotal;
	}

	public void setMotivoParaGlosaTotal(String motivoParaGlosaTotal) {
		this.motivoParaGlosaTotal = motivoParaGlosaTotal;
	}

	public boolean isGuiaVencida() {
		if (Utils.compareData(getDataVencimento(), new Date()) < 0){
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isExameExterno() {
		return exameExterno;
	}
	
	public void setExameExterno(boolean exameExterno) {
		this.exameExterno = exameExterno;
	}
	
	public Date getDataTerminoAtendimento() {
		return dataTerminoAtendimento;
	}

	public void setDataTerminoAtendimento(Date dataTerminoAtendimento) {
		this.dataTerminoAtendimento = dataTerminoAtendimento;
	}

	public Set<LoteDeGuias> getLotesDeGuias() {
		return lotesDeGuias;
	}

	public void setLotesDeGuias(Set<LoteDeGuias> lotesDeGuias) {
		this.lotesDeGuias = lotesDeGuias;
	}

	public Boolean getRecebido() {
		return recebido;
	}

	public void setRecebido(Boolean recebido) {
		this.recebido = recebido;
	}

	public LoteDeGuias getUltimoLote() {
		return ultimoLote;
	}

	public void setUltimoLote(LoteDeGuias ultimoLote) {
		this.ultimoLote = ultimoLote;
	}

	public MotivoDevolucaoDeLote getMotivo() {
		return motivo;
	}

	public void setMotivo(MotivoDevolucaoDeLote motivo) {
		this.motivo = motivo;
	}
	
	public Set<Critica> getCriticas() {
		return criticas;
	}

	public void setCriticas(Set<Critica> criticas) {
		this.criticas = criticas;
	}
	
	public Set<Critica> getCriticasParaApresentacaoPrevia() {
		return criticasParaApresentacaoPrevia;
	}

	public void setCriticasParaApresentacaoPrevia(
			Set<Critica> criticasParaApresentacaoPrevia) {
		this.criticasParaApresentacaoPrevia = criticasParaApresentacaoPrevia;
	}

	public boolean isPossuiPeriodicidadeEmProcedimento() {
		return possuiPeriodicidadeEmProcedimento;
	}

	public void setPossuiPeriodicidadeEmProcedimento(boolean possuiPeriodicidadeEmProcedimento) {
		this.possuiPeriodicidadeEmProcedimento = possuiPeriodicidadeEmProcedimento;
	}

	public String getDescricaoDevolucao() {
		return this.getSituacao(SituacaoEnum.DEVOLVIDO.descricao()).getMotivo();
	}


	public boolean isGuiaImpressaoNova(){
		return false;	
	}
	
	public String getCriticasFormatado() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<ul>");
		for (Critica critica : this.criticas) {
			buffer.append("<li>");
			buffer.append(critica.getMensagem());
			buffer.append("</li><br/>");
		}
		buffer.append("</ul>");
		return buffer.toString();
	}	

	public boolean isNaoAutorizada() {
		return this.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao());
	}
	
	public boolean isCancelada() {
		return this.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao());
	}
	
	public boolean isNecessitaAuditoria(){

		boolean isSituacaoFechadaEAuditavel = this.isSituacaoAtual(SituacaoEnum.FECHADO.descricao()) && !prestador.isExigeEntregaLote();
		boolean isNecessitaAuditoria = isSituacaoFechadaEAuditavel ||  this.isSituacaoAtual(SituacaoEnum.RECEBIDO.descricao()) || this.isSituacaoAtual(SituacaoEnum.AUDITADO.descricao());
		return isNecessitaAuditoria;
	}
	
	public boolean isAberta() {
		return this.isSituacaoAtual(SituacaoEnum.ABERTO.descricao());
	}
	
	public void aplicaValorAcordo() {
		for (P procedimento : this.procedimentos) {
			procedimento.aplicaValorAcordo();
		}
		this.recalcularValores();
		this.updateValorCoparticipacao();
	}
	
	public Set<Observacao> getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(Set<Observacao> observacoes) {
		this.observacoes = observacoes;
	}

	public void addObservacao(Observacao observacao) {
		this.observacoes.add(observacao);
		if (observacao != null){
			observacao.setGuia(this);
		}
	}
	
	public Set<RegistroTecnicoDaAuditoria> getRegistrosTecnicosDaAuditoria() {
		Set<RegistroTecnicoDaAuditoria> registros = new HashSet<RegistroTecnicoDaAuditoria>();
		
		for (AbstractObservacao observacao : this.observacoes) {
			if(observacao.isRegistroAuditoria()) {
				registros.add((RegistroTecnicoDaAuditoria) observacao);
			}
		}
		return registros;
	}
	
	public String getNumeroDeRegistro() {
		return numeroDeRegistro;
	}

	public void setNumeroDeRegistro(String numeroDeRegistro) {
		this.numeroDeRegistro = numeroDeRegistro;
	}

	public boolean isEmUmaDasSituacoes(String... situacoes) {
		for (String situacao : situacoes) {
			if (this.isSituacaoAtual(situacao)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isGeraExameExterno() {
		return false;
	}
	
	public boolean isGeraExameExternoParaPacientesCronicos() {
		return false;
	}
	
	@Override
	public Set<SituacaoInterface> getSituacoes() {
		Set<SituacaoInterface> situacoes = super.getSituacoes();
		SortedSet<SituacaoInterface> situacoesOrdenadas = new TreeSet<SituacaoInterface>(new Comparator<SituacaoInterface>() {
			@Override
			public int compare(SituacaoInterface o1, SituacaoInterface o2) {
				return - new Integer(o1.getOrdem()).compareTo(new Integer(o2.getOrdem()));
			}
		});
		situacoesOrdenadas.addAll(situacoes);
		
		return situacoesOrdenadas;
	}
	
	public Date getDataRecebimento() {
		return dataRecebimento;
	}

	public void setDataRecebimento(Date dataRecebimento) {
		this.dataRecebimento = dataRecebimento;
	}
	
	@Override
	public Set<ItemGuiaFaturamento> getItensGuiaFaturamento() {
		return itensGuiaFaturamento;
	}

	@Override
	public void setItensGuiaFaturamento(Set<ItemGuiaFaturamento> itensFaturamento) {
		this.itensGuiaFaturamento = itensFaturamento;
	}
	
	@Override
	public void addItemGuiaFaturamento(ItemGuiaFaturamento itemGuiaFaturamento) {
		if(itemGuiaFaturamento != null){
			this.itensGuiaFaturamento.add(itemGuiaFaturamento);
			itemGuiaFaturamento.setGuia(this);
		}
	}
	
	public <E extends ProcedimentoInterface> ArrayList<E> ordenarProcedimentosPorSituacaoEDescricao(Collection<E> procedimentos){
		ArrayList<E> list = new ArrayList<E>(procedimentos);
		Utils.sort(list, "situacao.descricao", "procedimentoDaTabelaCBHPM.descricao");
		return list;
	}
	
	public <E extends ProcedimentoInterface> ArrayList<E> ordenarProcedimentosPorDescricao(Collection<E> procedimentos){
		ArrayList<E> list = new ArrayList<E>(procedimentos);
		Utils.sort(list, "procedimentoDaTabelaCBHPM.descricao");
		return list;
	}
	
	public BigDecimal getMultaPorAtrasoDeEntrega() {
		return multaPorAtrasoDeEntrega;
	}

	public void setMultaPorAtrasoDeEntrega(BigDecimal multaPorAtrasoDeEntrega) {
		this.multaPorAtrasoDeEntrega = multaPorAtrasoDeEntrega;
	}
	
	/**
	 * @return O valor da que foi descontado da guia por motivo de multa por atraso na entrega de lote
	 */
	public String getValorMultaPorAtrasoEntrega() {
		BigDecimal valorMulta = this.getValorPagoPrestador()
									.multiply(this.getMultaPorAtrasoDeEntrega())
									.divide(new BigDecimal("100.00").subtract(this.getMultaPorAtrasoDeEntrega()), 5);
				
		return MoneyCalculation.rounded(valorMulta).toString();
	}
	
	/**
	 * Indica se foi aplicada multa por atraso na entrega para a guia.
	 * @return
	 */
	public boolean isPossuiMulta(){
		return this.multaPorAtrasoDeEntrega != null;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		GuiaSimples<ProcedimentoInterface> clone = newInstance();
		
		for (P procedimento : this.procedimentos) {
			ProcedimentoInterface procedimentoNovo = (ProcedimentoInterface) procedimento.clone();
			procedimentoNovo.setGuia(clone);
			clone.getProcedimentos().add(procedimentoNovo);
		}
		
		if (this.isCompleta()) {
			for (Observacao observacao : this.observacoes) {
				Observacao obsNova = (Observacao) observacao.clone();
				obsNova.setGuia((GuiaCompleta) clone);
				clone.getObservacoes().add(obsNova);
			}
		}
		
		clone.setColecaoSituacoes(SituacaoUtils.clone(this.getColecaoSituacoes()));
		
		for (GuiaSimples guiaSimples : this.getGuiasFilhas()) {
			if(!guiaSimples.isHonorarioMedico()){
				clone.addGuiaFilha((GuiaSimples)guiaSimples.clone());
			}
		}
		
		clone.setSegurado(segurado);
		clone.setPrestador(prestador);
		clone.setEspecialidade(especialidade);
		clone.setAutorizacao(autorizacao);
		clone.setDataAtendimento(dataAtendimento);
		clone.setDataMarcacao(dataMarcacao);
		clone.setDataRecebimento(dataRecebimento);
		clone.setDataTerminoAtendimento(dataTerminoAtendimento);
		clone.setEspecial(especial);
		clone.setExameExterno(exameExterno);
//		clone.setForaDoLimite(foraDoLimite);
//		clone.setForaDoLimite(foraDoLimite);
		clone.setLiberadaForaDoLimite(liberadaForaDoLimite);
		clone.setSituacao(SituacaoUtils.clone(this.getSituacao()));
		clone.setMensagemLimite(mensagemLimite);
		
		clone.profissional = this.profissional;
		clone.solicitante = this.solicitante;
		clone.tipoDeGuia = this.tipoDeGuia;
		
		return clone;
	}
	
	protected abstract GuiaSimples<ProcedimentoInterface> newInstance();
	
	/**
	 * Método abstrato implementado em todas as subclasses para utilização no novo validate do SR (@link ValidateFlyEnum})
	 * @return
	 */
	public abstract TipoGuiaEnum getTipoGuiaEnum();
	
	/**
	 * Aplica o command pattern de validação à guia e classes relacionadas
	 */
	public final Boolean validate() throws Exception {
		
		List<AtendimentoValidator> validacoes = this.getValidacoes();
		
		if(!flowValidators.isEmpty()){
			validacoes.addAll(flowValidators);
		}
		
		for (AtendimentoValidator validate : validacoes) {
			validate.execute(this);
		}

		for (P procedimento : this.getProcedimentosValidosParaConsumo()){
			procedimento.validate(this);
		}
		
		return true;
	}

	/**
	 * Retorna a coleção de validações de fluxos para uma guia
	 * @return
	 */
	public Collection<AtendimentoValidator> getFlowValidators() {
		return flowValidators;
	}
	
	/**
	 * Método responsável por adicionar validações específicas de um fluxo em "tempo de execução" em uma guia.
	 * Este método obedece o padrão interface fluente para poder facilitar na chamada ao mesmo; 
	 * @param validator
	 * @return
	 */
	public GuiaSimples addFlowValidator(AtendimentoValidator validator){
		this.getFlowValidators().add(validator);
		return this;
	}
	
	/**
	 * Método responsável por remover validações específicas de um fluxo em "tempo de execução" em uma guia.
	 * Este método obedece o padrão interface fluente para poder facilitar na chamada ao mesmo; 
	 * @param validator
	 * @return
	 */
	public GuiaSimples removeFlowValidator(AtendimentoValidator validator){
		this.getValidacoes().remove(validator);
		return this;
	}
	
	/**
	 * Executa somente as validações dos fluxos
	 * @throws Exception
	 */
	public void executeFlowValidators() throws Exception{
		for (AtendimentoValidator validate : this.getFlowValidators()) {
			validate.execute(this);
		}
	}
	
	public List<AtendimentoValidator> getValidacoes() {
		if (this.validacoes == null){
			this.validacoes = GuiaFactoryValidateEnum.getValidacoes(this.getTipoGuiaEnum());
		}
		
		return this.validacoes;
	}
	
	/**
	 * Verifica as situações nas quais se pode mostrar o valor pago prestador.
	 */	
	public boolean isMostrarValorPagoPrestador(){
		if (this.isSituacaoAtual(SituacaoEnum.AUDITADO.descricao())
				|| this.isSituacaoAtual(SituacaoEnum.FATURADA.descricao())
				|| this.isSituacaoAtual(SituacaoEnum.PAGO.descricao())){
			return true;
		}
		return false;
	}
	
	/**
	 * verifica se deve ser mostrados os valores da multa 
	 */
	public boolean isMostrarValoresMulta(){
		if (this.isMostrarValorPagoPrestador() && (this.getMultaPorAtrasoDeEntrega() != null)){
			return true;
		}
		return false;
	}
	
	/**
	 * verifica se deve ser mostrados os valores pagos externamente 
	 */
	public boolean isMostrarValoresPagosExternamente(){
		if (itensGuiaFaturamento.isEmpty()){
			return false;
		}
		return true;
	}
	
	public Date getDataInicioPrazoRecebimento(){
		return this.getDataAtendimento();
	}
	
	/**
	 * Verifica se a guia foi autorizada.
	 */
	@Override
	public boolean autorizado() {
		return GuiaUtils.isAutorizado(this.getSituacao().getDescricao());
	}
	
	/**
	 * Verifica se a guia etá com status de solicitada.
	 */
	public boolean solicitado(){
		return GuiaUtils.isSolicitado(this.getSituacao().getDescricao());
	}
	
	public void receberGuia(UsuarioInterface usuario) {
		Date newDate = new Date();
		this.mudarSituacao(usuario, SituacaoEnum.RECEBIDO.descricao(), MotivoEnumSR.GUIA_RECEBIDA.getMessage(), newDate);
		this.setDataRecebimento(newDate);
		this.recalcularValores();
		this.setValorTotalApresentado(this.getValorTotal());
	}
	
	/**
	 * Método que conformiza o valor apresentado da guia no processo de auditoria(situações: Recebido(a), Auditado(a)).
	 * REGRA: Novos itens da guia são contabilizados como tendo sido apresentados no fechamento pelo prestador.
	 * @author Luciano Infoway
	 * @since 03/07/2013
	 * @param acrescimoValor
	 */
	public void atualizaValorApresentado(BigDecimal acrescimoValor){
		if (this.isSituacaoAtual(SituacaoEnum.RECEBIDO.descricao()) 
			|| this.isSituacaoAtual(SituacaoEnum.AUDITADO.descricao())) {
			BigDecimal novoValor = this.getValorTotalApresentado().add(acrescimoValor);
			this.setValorTotalApresentado(novoValor);
		}
	}
	
	@Override
	public boolean isRecursoGlosa() {
		return false;
	}
	
	public BigDecimal getValorPagoExternamente() {
		BigDecimal resultado = BigDecimal.ZERO;
		
		for (ItemGuiaFaturamento item : itensGuiaFaturamento) {
			resultado = resultado.add(item.getValor());
		}
		
		return resultado;
	}
	
	public BigDecimal getValorItem() {
		return this.getValorTotal();
	}
	
	public boolean isGRG() {
		return false;
	}
	
	public GuiaRecursoGlosa getGuiaRecursoGlosa() {
		return null;
	}

	public Integer getLiberadaForaDoLimite() {
		return liberadaForaDoLimite;
	}

	public void setLiberadaForaDoLimite(Integer liberadaForaDoLimite) {
		this.liberadaForaDoLimite = liberadaForaDoLimite;
	}

	public BigDecimal getValorTotalMatMedAntesDaAuditoria() {
		return valorTotalMatMedAntesDaAuditoria;
	}

	public void setValorTotalMatMedAntesDaAuditoria(
			BigDecimal valorTotalMatMedAntesDaAuditoria) {
		this.valorTotalMatMedAntesDaAuditoria = valorTotalMatMedAntesDaAuditoria;
	}
}