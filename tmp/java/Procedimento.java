package br.com.infowaypi.ecarebc.procedimentos;


import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.com.infowaypi.ecare.cadastros.MotivoGlosa;
import br.com.infowaypi.ecare.services.recurso.ItemRecursoGlosa;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ItemGlosavel;
import br.com.infowaypi.ecarebc.atendimentos.acordos.AcordoCBHPM;
import br.com.infowaypi.ecarebc.atendimentos.enums.ProcedimentoFactoryValidateEnum;
import br.com.infowaypi.ecarebc.atendimentos.enums.TipoProcedimentoEnum;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GuiaHonorarioMedico;
import br.com.infowaypi.ecarebc.atendimentos.honorario.Honorario;
import br.com.infowaypi.ecarebc.atendimentos.honorario.HonorarioExterno;
import br.com.infowaypi.ecarebc.atendimentos.honorario.HonorarioInterno;
import br.com.infowaypi.ecarebc.atendimentos.validators.Criticavel;
import br.com.infowaypi.ecarebc.atendimentos.visitors.ItemGlosavelVisitor;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.enums.GrauDeParticipacaoEnum;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.Role;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.Faturamento;
import br.com.infowaypi.ecarebc.procedimentos.validators.AbstractProcedimentoValidator;
import br.com.infowaypi.ecarebc.utils.ProcedimentoUtils;
import br.com.infowaypi.ecarebc.utils.SituacaoUtils;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe que representa um procedimento de uma guia
 * @author root
 * @changes Danilo Nogueira Portela
 * @changes Jefferson, Marcos Roberto - 14.09.2011
 * @changes Wislanildo - 19/09/2011
 * @changes Leonardo Sampaio - 12/09/2012
 */
@SuppressWarnings({"unchecked"})
public class Procedimento extends ImplColecaoSituacoesComponent implements ProcedimentoInterface, Constantes, Criticavel, ItemGlosavel {

	public static final BigDecimal TETO_CO_PARTICIPACAO = new BigDecimal(50.00);

	private static final long serialVersionUID = 1L;

	private Long idProcedimento;
	private TabelaCBHPM procedimentoDaTabelaCBHPM;
	private Float valorAtualDaModeracao;
	private BigDecimal valorAtualDoProcedimento;
	private BigDecimal valorCoParticipacao;
	private BigDecimal valorAnestesista;
	private boolean bilateral;
	private boolean geraCoParticipacao;
	private Integer quantidade;
	private String discriminator;
	private GuiaSimples guia;
	private Profissional anestesistaTemp;
	private ItemRecursoGlosa itemRecurso;
	
	/**
	 * Matheus: 
	 * 
	 * Utilizado para armazenar a data para realizacao do procedimento, por exemplo, da solicitação. 
	 * É possível realizar procedimentos de forma retroativa para procedimentos de internações eletivas, e este campo armazena a data de sua realização.
	 *  
	 */
	private Date dataRealizacao;
	private Profissional auxiliarAnestesistaTemp;
	private Boolean incluiVideo;
	private boolean horarioEspecial;
	private Faturamento faturamento;
	private Prestador prestadorAnestesista;

	private Profissional profissionalResponsavel;
	
	private Profissional profissionalResponsavelTemp;
	private Profissional profissionalAuxiliar1Temp;
	private Profissional profissionalAuxiliar2Temp;
	private Profissional profissionalAuxiliar3Temp;
	
	/**
	 * Informa se o procedimento está com seus honorarios auditados
	 */
	private boolean auditado;
	
	/**
	 * Informa os honorários criados dentro da guia que contém este procedimento.
	 */
	private Set<Honorario> honorariosGuiaOrigem;
	
	/**
	 * Informa os honorários criados em guias de honorário médico individuais para este procedimento.
	 */
	private Set<HonorarioExterno> honorariosGuiaHonorarioMedico;

	private boolean passouPelaAutorizacao = false;
	private Boolean autorizado;//transiente?
	
	/**
	 * os atributos "selecionado" e "motivo" são transientes e são utilizado em fluxos onde é informado motivo via update-param, 
	 * por exemplo procedimentos ou autorizar procedimentos
	 */
	private transient Boolean selecionado;
	private transient String motivo;
	
		/**
	 * Atributo transiente para avisar utilizadono fluxo de solicitar exames pela central
	 */
	private boolean possuiPeriodicidade = false;
	
	/**
	 * Atributos transientes usados no fluxo de registrar honorarios.
	 */
	private transient Boolean adicionarHonorario = Boolean.FALSE;
	private transient Boolean incluiVideoProxy = Boolean.FALSE;
	private transient Boolean horarioEspecialProxy = Boolean.FALSE;
	private transient BigDecimal porcentagemProxy;
	
	private MotivoGlosa motivoGlosaProcedimento;
	private transient String motivoInsercao;

	private transient boolean glosar;
	private transient boolean desfazerGlosa;
	
	private String justificativaGlosa;
	
	private Set<AbstractProcedimentoValidator> validacoes;
	
	private transient boolean cancelar;
	
	private transient Integer actionRegulacao;
	
	private transient boolean selecionadoParaExclusao;
	
	private transient BigDecimal valorCalculadoProcedimento;
	
	private ItemGlosavel itemGlosavelAnterior;
	
	private Collection<AbstractProcedimentoValidator> flowValidators = new ArrayList<AbstractProcedimentoValidator>();

	private boolean anular;
	
	/**
	 * Armazena o valor moderado atual da tabela CBHPM no momento em que é realizada a dobra do valor do procedimento.
	 * 
	 * @see {CommandCorrecaoCalculoValorProcedimento}
	 * 
	 * */
	private BigDecimal valorModeradoDaTabelaCbhpmAoDobrar;

	public Procedimento() {
		this(null);
	}

	public Procedimento(UsuarioInterface usuario){
		valorAtualDaModeracao 			= 0f;
		valorAtualDoProcedimento 		= BigDecimal.ZERO;
		valorCoParticipacao 			= BigDecimal.ZERO;
		valorAnestesista 				= BigDecimal.ZERO;
		bilateral 						= false;
		geraCoParticipacao 				= true;
		quantidade 						= 1;
		honorariosGuiaOrigem 			= new HashSet<Honorario>();
		honorariosGuiaHonorarioMedico 	= new HashSet<HonorarioExterno>();
		this.mudarSituacao(usuario, SituacaoEnum.SOLICITADO.descricao(), MotivoEnum.SUJEITO_AUDITORIA.getMessage(), new Date());
	}

	/**
	 * Aplica o command pattern de validação o procedimento.
	 * o método templateValidator() esta sendo chamado  
	 * pelo fato de algumas subclasses implementarem lógica dentro desses métodos.
	 */
	@SuppressWarnings("rawtypes")
	public final Boolean validate(GuiaSimples guia) throws Exception {
		if(!guia.getSituacao().getUsuario().isPossuiRole(Role.DIRETORIA_MEDICA.getValor())){
			Set<AbstractProcedimentoValidator> validacoes = this.getValidacoes();
			
			if(!flowValidators.isEmpty()){
				validacoes.addAll(flowValidators);
			}
			
			for (AbstractProcedimentoValidator validate : validacoes) {
				validate.execute(this, guia);
			}
		}
		
		return true;
	}
	
	@SuppressWarnings("rawtypes")
	public Set<AbstractProcedimentoValidator> getValidacoes() {
		if (this.validacoes == null){
			this.validacoes = ProcedimentoFactoryValidateEnum.getValidacoes(this.getTipoProcedimentoEnum());
			}
		
		return this.validacoes;
		}

	/**
	 * Método responsável por adicionar validações específicas de um fluxo em "tempo de execução" em um procedimento.
	 * Este método obedece o padrão interface fluente para poder facilitar na chamada ao mesmo; 
	 * @param validator
	 * @return
	 */
	public Procedimento addFlowValidator(AbstractProcedimentoValidator validator){
		this.getFlowValidators().add(validator);
		return this;
	}
	
	public boolean getSelecionadoParaExclusao() {
		return selecionadoParaExclusao;
	}

	public void setSelecionadoParaExclusao(boolean selecionadoParaExclusao) {
		this.selecionadoParaExclusao = selecionadoParaExclusao;
	}

	/**
	 * Retorna a coleção de validações de fluxos para uma guia
	 * @return
	 */
	public Collection<AbstractProcedimentoValidator> getFlowValidators() {
		return flowValidators;
	}

	public Boolean getIncluiVideoProxy() {
		return incluiVideoProxy;
	}

	public void setIncluiVideoProxy(Boolean incluiVideoProxy) {
		this.incluiVideoProxy = incluiVideoProxy;
	}

	public Boolean getHorarioEspecialProxy() {
		return horarioEspecialProxy;
	}

	public void setHorarioEspecialProxy(Boolean horarioEspecialProxy) {
		this.horarioEspecialProxy = horarioEspecialProxy;
	}
	
	public SituacaoInterface getSituacaoAnterior(SituacaoInterface situacao) {
		int ordemSituacao = situacao.getOrdem();
		if (ordemSituacao == 1)
			return null;

		for (SituacaoInterface situacaoAtual : this.getSituacoes()) {
			if (situacaoAtual.getOrdem() == ordemSituacao - 1)
				return situacaoAtual;
		}
		return situacao;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#calcularCampos()
	 */
	public void calcularCampos(){
		TabelaCBHPM cbhpm = this.getProcedimentoDaTabelaCBHPM();

		if(cbhpm != null){

			if(MoneyCalculation.compare(this.getValorAtualDoProcedimento(), BigDecimal.ZERO) == 0){
				this.valorAtualDoProcedimento = cbhpm.getValorModerado();	
			}	

			if(this.getValorAtualDaModeracao() == null || this.getValorAtualDaModeracao().equals(0f)){
				this.valorAtualDaModeracao = cbhpm.getModeracao();
			}

			Boolean isValorCoParticipacaoZero = this.getValorCoParticipacao().equals(BigDecimal.ZERO);
			Boolean isProcedimentoCirurgico = this.getTipoProcedimento() == PROCEDIMENTO_CIRURGICO;
			
			if(isValorCoParticipacaoZero && !isProcedimentoCirurgico && this.geraCoParticipacao){
				calculaCoParticipacao();
			}
		} 
	}

	public void calculaCoParticipacao() {
		if (valorAtualDaModeracao != null) { 
			BigDecimal valorAtualModeracao = new BigDecimal(valorAtualDaModeracao).movePointLeft(2);//movePointLeft (2) é igual a /100
			BigDecimal coParticipacao = MoneyCalculation.multiplica(this.getValorTotal(), valorAtualModeracao.floatValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
			
			
			if(coParticipacao.compareTo(TETO_CO_PARTICIPACAO) > 0){
				this.valorCoParticipacao = TETO_CO_PARTICIPACAO;
			}else{
				this.valorCoParticipacao = coParticipacao;
			}
		}
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#validate(br.com.infowaypi.msr.user.UsuarioInterface)
	 */
	public Boolean validate(UsuarioInterface usuario) throws Exception {
		getSituacao().setUsuario(usuario);
		//Validações básicas de procedimento
		if (!this.isSituacaoAtual(SituacaoEnum.PRE_AUTORIZADO.descricao())){
			TabelaCBHPM cbhpm = this.getProcedimentoDaTabelaCBHPM();
			
			if(cbhpm == null)
				throw new ValidateException(MensagemErroEnum.PROCEDIMENTO_NAO_INFORMADO.getMessage());
			
			this.getProcedimentoDaTabelaCBHPM().tocarObjetos();
			
			if(!cbhpm.isSituacaoAtual(SituacaoEnum.ATIVO.descricao()))
				throw new ValidateException(MensagemErroEnum.PROCEDIMENTO_INATIVO_NO_SISTEMA.getMessage(cbhpm.getCodigo()));
			
			if(this.getBilateral() && !cbhpm.getBilateral())
				throw new ValidateException(MensagemErroEnum.PROCEDIMENTO_NAO_PODE_SER_BILATERAL.getMessage(cbhpm.getCodigo()));
		}

		if(usuario.getRole().equals(Role.AUDITOR.getValor()) || usuario.getRole().equals(Role.DIRETORIA_MEDICA.getValor())) {
			this.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZADO_PELO_AUDITOR.getMessage(), new Date());
		}

		if(usuario.getRole().equals(Role.DIGITADOR.getValor())) {
			this.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZADO_PELO_DIGITADOR.getMessage(), new Date());
		}

		if(!usuario.isPossuiRole(UsuarioInterface.ROLE_AUDITOR) && !usuario.getLogin().equals("hemope") && this.getQuantidade() > procedimentoDaTabelaCBHPM.getQuantidade())
			throw new ValidateException(MensagemErroEnum.PROCEDIMENTO_QUANTIDADE_INVALIDA.getMessage(String.valueOf(procedimentoDaTabelaCBHPM.getQuantidade())));

		//Cálculo do valor do procedimento
		if(this.getValorAtualDoProcedimento().equals(BigDecimal.ZERO))
			this.calcularCampos();
		return true;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#aplicaValorAcordo()
	 */
	public void aplicaValorAcordo(){
		BigDecimal valorAcordo = null;
		
		valorAcordo = getValorAcordoProcedimento();
		
		if(valorAcordo != null){
			this.setValorAtualDoProcedimento(valorAcordo);
		}
	}

	private BigDecimal getValorAcordoProcedimento() {
		boolean isPossuiEspecialidade = false;
		BigDecimal resultado = null;
		
		if(this.guia != null && this.procedimentoDaTabelaCBHPM != null){
			if(this.guia.getPrestador() != null){
				for (AcordoCBHPM acordoCBHPM : guia.getPrestador().getAcordosCBHPMAtivos()) {

					if(acordoCBHPM.getEspecialidade() != null 
						&& acordoCBHPM.getEspecialidade().equals(this.guia.getEspecialidade()) 
						&& acordoCBHPM.getTabelaCBHPM().equals(this.procedimentoDaTabelaCBHPM)){
						resultado = acordoCBHPM.getValor();
						processaCoParticipacao(acordoCBHPM);
						isPossuiEspecialidade = true;
					}
				}
				
				if(!isPossuiEspecialidade){
					for (AcordoCBHPM acordoCBHPM : guia.getPrestador().getAcordosCBHPMAtivos()) {
						boolean isPossuiEspacialidade = acordoCBHPM.getEspecialidade() != null;
						boolean isProcedimentoEquals = acordoCBHPM.getTabelaCBHPM().getCodigo().equals(this.procedimentoDaTabelaCBHPM.getCodigo());
						if(!isPossuiEspacialidade && isProcedimentoEquals){
							resultado = acordoCBHPM.getValor();
							processaCoParticipacao(acordoCBHPM);
						}
					}
				}
			}
		}
		
		return resultado;
	}

	private void processaCoParticipacao(AcordoCBHPM acordoCBHPM) {
		if(acordoCBHPM.isLiberarCoParticipacao()){
			this.setValorCoParticipacao(MoneyCalculation.rounded(BigDecimal.ZERO));
			this.setGeraCoParticipacao(false);
		}else{
			if(!acordoCBHPM.getValor().equals(BigDecimal.ZERO)) {				
				this.setValorAtualDoProcedimento(acordoCBHPM.getValor());
			}
			calculaCoParticipacao();
		}
	}
	

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#getBilateral()
	 */
	public boolean getBilateral() {
 		return bilateral;
	}
	
	/**
	 * Retorna a string Sim para true e Não para false, utilizado inicialmente no fluxo de comfirmar exame.
	 */
	public String getBilateralFormatado(){
		if(getBilateral()){
			return "Sim";
		}else {
			return "Não";
		}
	}
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#setBilateral(boolean)
	 */
	public void setBilateral(boolean bilateral) {
		this.bilateral = bilateral;
	}
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#getGuia()
	 */
	public GuiaSimples getGuia() {
		return guia;
	}
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#setGuia(br.com.infowaypi.ecarebc.atendimentos.GuiaSimples)
	 */
	public void setGuia(GuiaSimples guia) {
		this.guia = guia;
	}
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#getIdProcedimento()
	 */
	public Long getIdProcedimento() {
		return idProcedimento;
	}
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#setIdProcedimento(java.lang.Long)
	 */
	public void setIdProcedimento(Long idProcedimento) {
		this.idProcedimento = idProcedimento;
	}
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#getProcedimentoDaTabelaCBHPM()
	 */
	public TabelaCBHPM getProcedimentoDaTabelaCBHPM() {
		return procedimentoDaTabelaCBHPM;
	}
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#setProcedimentoDaTabelaCBHPM(br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM)
	 */
	public void setProcedimentoDaTabelaCBHPM(TabelaCBHPM procedimentoDaTabelaCBHPM) {
		this.procedimentoDaTabelaCBHPM = procedimentoDaTabelaCBHPM;
	}
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#getValorAtualDaModeracao()
	 */
	public Float getValorAtualDaModeracao() {
		return valorAtualDaModeracao;
	}
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#setValorAtualDaModeracao(java.lang.Float)
	 */
	public void setValorAtualDaModeracao(Float valorAtualDaModeracao) {
		this.valorAtualDaModeracao = valorAtualDaModeracao;
	}
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#getValorAtualDoProcedimento()
	 */
	public BigDecimal getValorAtualDoProcedimento() {
		return valorAtualDoProcedimento;
	}
	
	/**
	 * Fornece o valor, antes de ser zerado, do procedimento, caso ja tenha sido gerado um honorario
	 * de responsável para o mesmo.
	 * @return
	 */
	public BigDecimal getValorVirtualDoProcedimento() {
		if (this.containsHonorarioExterno(GrauDeParticipacaoEnum.RESPONSAVEL.getCodigo())){
			Honorario he = this.getHonorarioExterno(GrauDeParticipacaoEnum.RESPONSAVEL.getCodigo());
			
			return MoneyCalculation.rounded(he.getValorTotal());
		}
		
		return this.getValorAtualDoProcedimento();
	}
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#setValorAtualDoProcedimento(java.math.BigDecimal)
	 */
	public void setValorAtualDoProcedimento(BigDecimal valorAtualDoProcedimento) {
		this.valorAtualDoProcedimento = valorAtualDoProcedimento;
	}
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#getValorTotal()
	 */
	public BigDecimal getValorTotal() {
		if(quantidade == null){
			quantidade = 1;
		}
		
		BigDecimal valor = MoneyCalculation.multiplica(valorAtualDoProcedimento, quantidade);
		if(this.getBilateral()){
			return MoneyCalculation.multiplica(valor, 1.7f);
		}
		return MoneyCalculation.rounded(valor);
	}

	public BigDecimal getValorTotalComAcordo(Prestador prestador) {
		BigDecimal valor = BigDecimal.ZERO;

		for (AcordoCBHPM acordo : prestador.getAcordosCBHPM()) {
			if(acordo.getTabelaCBHPM().equals(this.procedimentoDaTabelaCBHPM)) {
				valor = MoneyCalculation.multiplica(acordo.getValor(), this.quantidade);
				if(this.getBilateral()){
					return MoneyCalculation.multiplica(valor, 1.7f);
				}
				
				return valor;
			}
		}
		
		return this.getValorTotal();
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#getValorTotalFormatado()
	 */
	public String getValorTotalFormatado() {
		return StringUtils.leftPad(new DecimalFormat("###,###,##0.00").format(getValorTotal().setScale(2,BigDecimal.ROUND_HALF_UP)), 9, " "); 
	}
	
	public String getValorAtualDoProcedimentoFormatado() {
		return StringUtils.leftPad(new DecimalFormat("###,###,##0.00").format(getValorAtualDoProcedimento().setScale(2,BigDecimal.ROUND_HALF_UP)), 9, " "); 
	}


	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#getDataUltimaMarcacao(java.util.Collection)
	 */
	public Date getDataUltimaMarcacao(Collection<GuiaSimples> guias) throws ValidateException {
		Date dataUltimaMarcacao = null;
		Boolean isExistemGuiasDoSegurado = guias == null || guias.isEmpty();
		TabelaCBHPM cbhpm1 = this.getProcedimentoDaTabelaCBHPM();

		if(!isExistemGuiasDoSegurado){
			guia : for(GuiaSimples<Procedimento> guia : guias){
				Boolean isGuiaCancelada = guia.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao());

				if(!isGuiaCancelada){
					for(ProcedimentoInterface proc : guia.getProcedimentos()){
						TabelaCBHPM cbhpm2 = proc.getProcedimentoDaTabelaCBHPM();
						Boolean isMesmoProcedimento = cbhpm1.equals(cbhpm2);

						if(isMesmoProcedimento){
							if(dataUltimaMarcacao == null)
								dataUltimaMarcacao = guia.getDataAtendimento();
							else if(Utils.compareData(dataUltimaMarcacao, guia.getDataAtendimento()) < 0)
								dataUltimaMarcacao = guia.getDataAtendimento();

							continue guia;
						}
					}
				}
			}
		}

		return dataUltimaMarcacao;
	}


	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#tocarObjetos()
	 */
	public void tocarObjetos(){
		this.getProcedimentoDaTabelaCBHPM().tocarObjetos();
		this.getGuia();
		this.getSituacao();
		this.getColecaoSituacoes().getSituacoes().size();
		for (SituacaoInterface situcao: getColecaoSituacoes().getSituacoes()) {
			if(situcao.getUsuario() != null)
				situcao.getUsuario().tocarObjetos();
		}
		this.getValorAtualDaModeracao();
		this.getValorAtualDoProcedimento();
		
		//		this.getValorTotal();
		this.getHonorariosGuiaOrigem().size();
		for (Honorario honorario : this.getHonorariosGuiaOrigem()) {
			honorario.getSituacoes().size();
		}
		this.getHonorariosGuiaHonorarioMedico().size();
		for (HonorarioExterno honorario : this.getHonorariosGuiaHonorarioMedico()) {
			Set<ProcedimentoInterface> procedimentos = honorario.getGuiaHonorario().getProcedimentos();
			
			if(procedimentos != null){
				procedimentos.size();
			}
			
			honorario.getSituacoes().size();
		}
		if (getProfissionalResponsavel() != null){
			getProfissionalResponsavel().tocarObjetos();
		}
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#getTipoProcedimento()
	 */
	public int getTipoProcedimento() {
		return PROCEDIMENTO_NORMAL;
	}

	@Override
	public TipoProcedimentoEnum getTipoProcedimentoEnum() {
		return TipoProcedimentoEnum.PROCEDIMENTO;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ProcedimentoInterface))
			return false;

		ProcedimentoInterface procedimento = (ProcedimentoInterface) obj;

		return new EqualsBuilder()
			.append(this.getIdProcedimento(), procedimento.getIdProcedimento())
			.append(this.getProcedimentoDaTabelaCBHPM(), procedimento.getProcedimentoDaTabelaCBHPM())
			.append(this.getGuia(), procedimento.getGuia())
			.append(this.getPorcentagem(), procedimento.getPorcentagem())
			.isEquals();
	}


	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.getIdProcedimento())
			.append(this.getProcedimentoDaTabelaCBHPM())
			.append(this.getGuia())
			.append(this.getPorcentagem())
			.toHashCode();
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#getValorCoParticipacao()
	 */
	public BigDecimal getValorCoParticipacao() {
		if(!geraCoParticipacao){
			return BigDecimal.ZERO;
		} else {
			return valorCoParticipacao;
		}
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#getValorCoParticipacaoFormatado()
	 */
	public String getValorCoParticipacaoFormatado() {
		if (valorCoParticipacao == null)
			return "0,00";
		return StringUtils.leftPad(new DecimalFormat("###,###,##0.00").format(valorCoParticipacao.setScale(2,BigDecimal.ROUND_HALF_UP)), 9, " "); 
	}	

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#setValorCoParticipacao(java.math.BigDecimal)
	 */
	public void setValorCoParticipacao(BigDecimal valorCoParticipacao) {
		this.valorCoParticipacao = valorCoParticipacao;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#isGeraCoParticipacao()
	 */
	public boolean isGeraCoParticipacao() {
		return geraCoParticipacao;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#setGeraCoParticipacao(boolean)
	 */
	public void setGeraCoParticipacao(boolean geraCoParticipacao) {
		this.geraCoParticipacao = geraCoParticipacao;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#getQuantidade()
	 */
	public Integer getQuantidade() {
		return quantidade;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#setQuantidade(java.lang.Integer)
	 */
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#setQuantidadeText(int)
	 */
	public void setQuantidadeText(int quantidade) {
		this.setQuantidade(quantidade);
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#getQuantidadeText()
	 */
	public int getQuantidadeText() {
		return this.getQuantidade();
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#getAutorizado()
	 */
	public Boolean getAutorizado() {
		return autorizado;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#setAutorizado(java.lang.Boolean)
	 */
	public void setAutorizado(Boolean autorizado) {
		this.autorizado = autorizado;
		setPassouPelaAutorizacao(true);
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#getAnestesista()
	 */
	public Profissional getAnestesista() {
		if (this.getHonorarioInterno(GrauDeParticipacaoEnum.ANESTESISTA.getCodigo()) != null)
			return this.getHonorarioInterno(GrauDeParticipacaoEnum.ANESTESISTA.getCodigo()).getProfissional();
		return null;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#setAnestesista(br.com.infowaypi.ecarebc.associados.Profissional)
	 */
	/**
	 * Seta o atributo temporário (transiente) relativo ao anestesista.
	 */
	public void setAnestesista(Profissional anestesista) throws ValidateException {
		this.anestesistaTemp = anestesista;
	}
	
	public Profissional getAuxiliarAnestesista() {
		if (this.getHonorarioInterno(GrauDeParticipacaoEnum.AUXILIAR_ANESTESISTA.getCodigo()) != null)
			return this.getHonorarioInterno(GrauDeParticipacaoEnum.AUXILIAR_ANESTESISTA.getCodigo()).getProfissional();
		return null;
	}
	
		/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#setAnestesista(br.com.infowaypi.ecarebc.associados.Profissional)
	 */
	/**
	 * Seta o atributo temporário (transiente) relativo ao auxiliarAnestesista.
	 */
	public void setAuxiliarAnestesista(Profissional auxiliarAnestesista) throws ValidateException {
		this.auxiliarAnestesistaTemp = auxiliarAnestesista;
	}

	public Profissional getAnestesistaTemp() {
		return anestesistaTemp;
	}

	public Profissional getAuxiliarAnestesistaTemp() {
		return auxiliarAnestesistaTemp;
	}

	public Profissional getProfissionalAuxiliar1Temp() {
		return profissionalAuxiliar1Temp;
	}

	public Profissional getProfissionalAuxiliar2Temp() {
		return profissionalAuxiliar2Temp;
	}

	public Profissional getProfissionalAuxiliar3Temp() {
		return profissionalAuxiliar3Temp;
	}
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#getValorAnestesista()
	 */
	public BigDecimal getValorAnestesista() {
		return valorAnestesista;
	}

	/**
	 * Se para esse procedimento já tiver sido gerado uma guia de honorário médico individual para o anestesista,
	 * esse método retorna o valor do honorário dessa guia. Caso não, o método retorna o valor
	 * do anestesista ({@link #getValorAnestesista()}.
	 * @return
	 */
	public BigDecimal getValorAnestesistaDoHonorario() {
		for (Honorario honorario : this.getHonorariosExternosNaoCanceladosEGlosados()) {
			if (honorario.getGrauDeParticipacao() == GrauDeParticipacaoEnum.ANESTESISTA.getCodigo())
				return MoneyCalculation.rounded(honorario.getValorTotal());
		}
		return MoneyCalculation.rounded(this.getValorAnestesista());
	}
	
	public void setValorAnestesista(BigDecimal valorAnestesista) {
		this.valorAnestesista = valorAnestesista;
	}
	
	public BigDecimal getValorAuxiliarAnestesista() {
		return MoneyCalculation.multiplica(this.getValorAnestesistaDoHonorario(), 0.3f);
	}
	
	public void atualizaValorAnestesista() {
		if(this.getProcedimentoDaTabelaCBHPM().getPorteAnestesico()!= null) {
			BigDecimal valorPorte = new BigDecimal(this.getProcedimentoDaTabelaCBHPM().getPorteAnestesico().getValorPorte());
			BigDecimal porcentagem = this.getPorcentagem().divide(new BigDecimal(100));
			valorPorte = valorPorte.multiply(porcentagem);
			
//			não é mais para acrescentar 50% ao valor do porte desde a competencia 03/2010
//			valorPorte = valorPorte.multiply(new BigDecimal(1.5));
			if(incluiVideo != null && incluiVideo) {
				valorPorte = valorPorte.multiply(new BigDecimal(1.5));
			}

			if(horarioEspecial) {
				valorPorte = valorPorte.multiply(new BigDecimal(1.3));
			}
			valorAnestesista =  MoneyCalculation.rounded(valorPorte);
		}else {
			valorAnestesista =  BigDecimal.ZERO;
		}
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#getPorcentagem()
	 */
	public BigDecimal getPorcentagem() {
		return new BigDecimal(100);
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#getValorAnestesistaFormatado()
	 */
	public String getValorAnestesistaFormatado() {
		return StringUtils.leftPad(new DecimalFormat("###,###,##0.00").format(getValorAnestesista().setScale(2,BigDecimal.ROUND_HALF_UP)), 9, " ");
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#isIncluiVideo()
	 */
	public Boolean isIncluiVideo() {
		return incluiVideo;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#getIncluiVideo()
	 */
	public Boolean getIncluiVideo() {
		return incluiVideo;
	}

	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#setIncluiVideo(boolean)
	 */
	public void setIncluiVideo(Boolean incluiVideo) {
		this.incluiVideo = incluiVideo;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#isHorarioEspecial()
	 */
	public boolean isHorarioEspecial() {
		return horarioEspecial;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#setHorarioEspecial(boolean)
	 */
	public void setHorarioEspecial(boolean horarioEspecial) {
		this.horarioEspecial = horarioEspecial;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#setPrestadorAnestesista(br.com.infowaypi.ecarebc.associados.Prestador)
	 */
	public void setPrestadorAnestesista(Prestador prestadorAnestesista) {
		this.prestadorAnestesista = prestadorAnestesista;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#getPrestadorAnestesista()
	 */
	public Prestador getPrestadorAnestesista() {
		return prestadorAnestesista;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#setFaturamento(br.com.infowaypi.ecarebc.financeiro.faturamento.Faturamento)
	 */
	public void setFaturamento(Faturamento faturamento) {
		this.faturamento = faturamento;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface#getFaturamento()
	 */
	public Faturamento getFaturamento() {
		return faturamento;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public boolean isPassouPelaAutorizacao() {
		return passouPelaAutorizacao;
	}

	public void setPassouPelaAutorizacao(boolean passouPelaAutorizacao) {
		this.passouPelaAutorizacao = passouPelaAutorizacao;
	}

	public Profissional getProfissionalResponsavel() {
		return this.profissionalResponsavel;
	}

	public void setProfissionalResponsavel(Profissional profissionalResponsavel) {
		this.profissionalResponsavel = profissionalResponsavel;
	}

	public Profissional getProfissionalAuxiliar1() {
		if (this.getHonorarioInterno(GrauDeParticipacaoEnum.PRIMEIRO_AUXILIAR.getCodigo()) != null)
			return this.getHonorarioInterno(GrauDeParticipacaoEnum.PRIMEIRO_AUXILIAR.getCodigo()).getProfissional();
		return null;
	}

	/**
	 * Seta o atributo temporário (transiente) relativo ao profissionalAuxiliar1.
	 */
	public void setProfissionalAuxiliar1(Profissional profissionalAuxiliar1) {
		this.profissionalAuxiliar1Temp = profissionalAuxiliar1;
	}

	public Profissional getProfissionalAuxiliar2() {
		if (this.getHonorarioInterno(GrauDeParticipacaoEnum.SEGUNDO_AUXILIAR.getCodigo()) != null)
			return this.getHonorarioInterno(GrauDeParticipacaoEnum.SEGUNDO_AUXILIAR.getCodigo()).getProfissional();
		return null;
	}

	/**
	 * Seta o atributo temporário (transiente) relativo ao profissionalAuxiliar2.
	 */
	public void setProfissionalAuxiliar2(Profissional profissionalAuxiliar2) {
		this.profissionalAuxiliar2Temp = profissionalAuxiliar2;
	}

	public Profissional getProfissionalAuxiliar3() {
		if (this.getHonorarioInterno(GrauDeParticipacaoEnum.TERCEIRO_AUXILIAR.getCodigo()) != null)
			return this.getHonorarioInterno(GrauDeParticipacaoEnum.TERCEIRO_AUXILIAR.getCodigo()).getProfissional();
		return null;
	}
	
	/**
	 * Seta o atributo temporário (transiente) relativo ao profissionalAuxiliar3.
	 */
	public void setProfissionalAuxiliar3(Profissional profissionalAuxiliar3) {
		this.profissionalAuxiliar3Temp = profissionalAuxiliar3;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface#getValorAuxiliar1()
	 */
	public BigDecimal getValorAuxiliar1() {
		return MoneyCalculation.multiplica(new BigDecimal(this.getValorProfissionalResponsavelDoHonorario().floatValue()), 0.3f);

	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface#getValorAuxiliar2()
	 */
	public BigDecimal getValorAuxiliar2() {
		return MoneyCalculation.multiplica(new BigDecimal(this.getValorProfissionalResponsavelDoHonorario().floatValue()), 0.2f);
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface#getValorAuxiliar3()
	 */
	public BigDecimal getValorAuxiliar3() {
		return this.getValorAuxiliar2();
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface#getValorProfissionalResponsavel()
	 */
	public BigDecimal getValorProfissionalResponsavel() {
		//honorario nao calculado aki pq se n vai interferir na correcao do valor da guia
		return MoneyCalculation.rounded(this.getValorAtualDoProcedimento());
	}

	/**
	 * Se para esse procedimento já tiver sido gerado uma guia de honorário médico individual para o responsável,
	 * esse método retorna o valor do honorário dessa guia. Caso não, o método retorna o valor
	 * do responsável calculado da maneira convencional ({@link #getValorProfissionalResponsavel()}.
	 * @return
	 */
	public BigDecimal getValorProfissionalResponsavelDoHonorario() {
		for (Honorario honorario : this.getHonorariosExternosNaoCanceladosEGlosados()) {
			if (honorario.getGrauDeParticipacao() == GrauDeParticipacaoEnum.RESPONSAVEL.getCodigo())
				return MoneyCalculation.rounded(honorario.getValorTotal());
		}
		return MoneyCalculation.rounded(this.getValorProfissionalResponsavel());
	}

	@Override
	public boolean isPossuiPeriodicidade() {
		return this.possuiPeriodicidade;
	}

	@Override
	public void setPossuiPeriodicidade(boolean possuiPeriodicidade) {
		this.possuiPeriodicidade = possuiPeriodicidade;
	}

	public String getDiscriminator() {
		return discriminator;
	}
	
	private void setDiscriminator(String discriminator) {
		this.discriminator = discriminator;
	}
	
	public Boolean getAdicionarHonorario() {
		return adicionarHonorario;
	}

	public void setAdicionarHonorario(Boolean adicionarHonorario) {
		this.adicionarHonorario = adicionarHonorario;
	}

	public boolean isAuditado() {
		return auditado;
	}

	public void setAuditado(boolean auditado) {
		this.auditado = auditado;
	}

	public Set<Honorario> getHonorariosGuiaOrigem() {
		return honorariosGuiaOrigem;
	}
	
	public void setHonorariosGuiaOrigem(Set<Honorario> honorariosGuiaOrigem) {
		this.honorariosGuiaOrigem = honorariosGuiaOrigem;
	}
	
	public Set<HonorarioExterno> getHonorariosGuiaHonorarioMedico() {
		return honorariosGuiaHonorarioMedico;
	}
	
	/**
	 * Fornece a coleção de honorários externos que não foram cancelados nem glosados, ou seja,
	 * aqueles cujas guias (de honorário médico) que os contém não estejam canceladas ou que eles
	 * próprios não estejam cancelados ou glosados.
	 * @return
	 */
	public Set<HonorarioExterno> getHonorariosExternosNaoCanceladosEGlosados() {
		Set<HonorarioExterno> honorariosExternosNaoCanceladosEGlosados = new HashSet<HonorarioExterno>();
		boolean isGuiaCancelada, isGuiaGlosada, isHonorarioCancelado, isHonorarioGlosado;
		
		for (HonorarioExterno honorario : getHonorariosGuiaHonorarioMedico()) {
			isGuiaCancelada = honorario.getGuiaHonorario()!=null && honorario.getGuiaHonorario().isSituacaoAtual(SituacaoEnum.CANCELADO.descricao());
			isGuiaGlosada =  honorario.getGuiaHonorario()!=null && honorario.getGuiaHonorario().isSituacaoAtual(SituacaoEnum.GLOSADO.descricao());
			isHonorarioCancelado = honorario.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao());
			isHonorarioGlosado = honorario.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao());
			
			if (!isGuiaCancelada && !isGuiaGlosada && !isHonorarioCancelado && !isHonorarioGlosado)
				honorariosExternosNaoCanceladosEGlosados.add((HonorarioExterno) honorario);
		}
		
		return honorariosExternosNaoCanceladosEGlosados;
	}
	
	//TODO fazer alguma coisa p n poder add por aki, só no momento de criar a guiadehonor.
	public void setHonorariosGuiaHonorarioMedico(Set<HonorarioExterno> honorariosGuiaHonorarioMedico) {
		this.honorariosGuiaHonorarioMedico = honorariosGuiaHonorarioMedico;
	}
	
	/**
	 * Cria um honorário de acordo com os parâmetros informados,
	 * e adiciona na coleção de honorário do procedimento (originados na própria guia).
	 * @param grauDeParticipacao
	 * @param profissional
	 * @param valorHonorario
	 * @throws ValidateException 
	 */
	public void addHonorarioGuiaOrigem(UsuarioInterface usuario, int grauDeParticipacao, Profissional profissional, BigDecimal valorHonorario) {
		HonorarioInterno honorario = new HonorarioInterno(usuario);
		this.getHonorariosGuiaOrigem().add(createHonorario(honorario, grauDeParticipacao,profissional, valorHonorario, this.getPorcentagem()));
	}
	
	/**
	 * Cria um honorário de acordo com os parâmetros informados,
	 * e adiciona na coleção de honorário do procedimento
	 * (originados em uma guia de honorário médico individual).
	 * @see GuiaHonorarioMedico#addHonorario(int, Profissional, BigDecimal)
	 * @param honorario
	 * @throws ValidateException 
	 */
	public void addHonorarioGuiaHonorarioMedico(HonorarioExterno honorario){
		this.getHonorariosGuiaHonorarioMedico().add(honorario);
	}
	
	/**
	 * Cria um objeto honorário.
	 * @param grauDeParticipacao
	 * @param profissional
	 * @param valorHonorario
	 * @return
	 */
	private Honorario createHonorario (Honorario honorario, int grauDeParticipacao, Profissional profissional, BigDecimal valorHonorario, BigDecimal porcentagem) {
		honorario.setGrauDeParticipacao(grauDeParticipacao);
		honorario.setProfissional(profissional);
		honorario.setProcedimento(this);
		honorario.setValorTotal(valorHonorario);
		honorario.setPorcentagem(porcentagem);
		
		return honorario;
	}
	
	/**
	 * Indica se já foi criado algum honorário, esteja ele dentro de uma guia
	 * de honorário médico ou dentro de uma guia simples, para esse procedimento e segundo o grau de participação informados.
	 * @param grauDeParticipacao
	 * @return
	 */
	public boolean containsHonorario(int grauDeParticipacao){
		return containsHonorarioInterno(grauDeParticipacao) || containsHonorarioExterno(grauDeParticipacao);
	}
	
	/**
	 * Indica se já foi criado algum honorário, dentro de uma guia simples (ou seja, de qualquer guia
	 * que <b>não</b> é de honorário médico), para esse procedimento e segundo o grau de participação informados.
	 * @param grauDeParticipacao
	 * @return
	 */
	public boolean containsHonorarioInterno(int grauDeParticipacao){
		return this.getHonorarioInterno(grauDeParticipacao) != null;
	}
	
	/**
	 * Indica se já foi criado algum honorário, dentro de uma guia simples de honorário médico,
	 * para esse procedimento e segundo o grau de participação informado.
	 * @param grauDeParticipacao
	 * @return
	 */
	public boolean containsHonorarioExterno(int grauDeParticipacao){
		for (Honorario honorario : this.getHonorariosExternosNaoCanceladosEGlosados()) {
			if (honorario.getGrauDeParticipacao() == grauDeParticipacao)
				return true;
		}
		return false;
	}
	
	/**
	 * Retorna o honorário (criado na guia origem) desse procedimento que possui o grau de participação informado,
	 * @param grauDeParticipacao
	 * @return
	 */
	public Honorario getHonorarioInterno(int grauDeParticipacao){
		for (Honorario honorario : this.honorariosGuiaOrigem) {
			if ((honorario.getGrauDeParticipacao() == grauDeParticipacao) && (honorario.isSituacaoAtual(SituacaoEnum.GERADO.descricao())))
				return honorario;
		}
		return null;
	}
	
	/**
	 * Retorna o honorário (criado na guia de honorário médico) desse procedimento que possui o grau de 
	 * participação informado,
	 * @param grauDeParticipacao
	 * @return
	 */
	public Honorario getHonorarioExterno(int grauDeParticipacao){
		for (Honorario honorarioExterno : this.getHonorariosExternosNaoCanceladosEGlosados()) {
			if (honorarioExterno.getGrauDeParticipacao() == grauDeParticipacao) { 
					return honorarioExterno;
			}
		}
		return null;
	}

	public BigDecimal getPorcentagemProxy() {
		if(porcentagemProxy == null){
			porcentagemProxy = this.getPorcentagem();
		}
		return porcentagemProxy;
	}

	public void setPorcentagemProxy(BigDecimal porcentagemProxy) {
		this.porcentagemProxy = porcentagemProxy;
	}

	/**
	 * Retorna os honorários externos e internos de um procedimento
	 * @param procedimento
	 * @return
	 */
	public Set<Honorario> getTodosOsHonorariosDoProcedimento(){
		Set<Honorario> honorarios = new HashSet<Honorario>();
					
		honorarios.addAll(this.getHonorariosExternosNaoCanceladosEGlosados());
		honorarios.addAll(this.getHonorariosGuiaOrigem());
		
		return honorarios;
	}

	@Override
	public Map<GrauDeParticipacaoEnum, Profissional> getProfissionaisTemp() {
		Map<GrauDeParticipacaoEnum, Profissional> profissionais = new HashMap<GrauDeParticipacaoEnum, Profissional>();
		if (this.profissionalResponsavel != null) {
			profissionais.put(GrauDeParticipacaoEnum.RESPONSAVEL, profissionalResponsavel);
		}
		if (this.profissionalAuxiliar1Temp != null) {
			profissionais.put(GrauDeParticipacaoEnum.PRIMEIRO_AUXILIAR, profissionalAuxiliar1Temp);
		}
		if (this.profissionalAuxiliar2Temp != null) {
			profissionais.put(GrauDeParticipacaoEnum.SEGUNDO_AUXILIAR, profissionalAuxiliar2Temp);
		}
		if (this.profissionalAuxiliar3Temp != null) {
			profissionais.put(GrauDeParticipacaoEnum.TERCEIRO_AUXILIAR, profissionalAuxiliar3Temp);
		}
		return profissionais;
	}
	
	@Override
	public Map<GrauDeParticipacaoEnum, Profissional> getProfissionaisAnestesistasTemp() {
		Map<GrauDeParticipacaoEnum, Profissional> anestesistas = new HashMap<GrauDeParticipacaoEnum, Profissional>();
		if (this.anestesistaTemp != null) {
			anestesistas.put(GrauDeParticipacaoEnum.ANESTESISTA, anestesistaTemp);
		}
		if (this.auxiliarAnestesistaTemp != null) {
			anestesistas.put(GrauDeParticipacaoEnum.AUXILIAR_ANESTESISTA, auxiliarAnestesistaTemp);
		}
		return anestesistas;
	}

	@Override
	public void setProfissionalAuxiliar1Temp(Profissional profissionalAuxiliar1) {
		this.profissionalAuxiliar1Temp = profissionalAuxiliar1;
	}

	@Override
	public void setProfissionalAuxiliar2Temp(Profissional profissionalAuxiliar2) {
		this.profissionalAuxiliar2Temp = profissionalAuxiliar2;
	}

	@Override
	public void setProfissionalAuxiliar3Temp(Profissional profissionalAuxiliar3) {
		this.profissionalAuxiliar3Temp = profissionalAuxiliar3;
	}

	@Override
	public boolean contemAlgumHonorario() {
		boolean possuiHonorariosExternos = !this.getHonorariosExternosNaoCanceladosEGlosados().isEmpty();
		boolean possuiHonorariosInternos = false;
		for (Honorario honorario : this.honorariosGuiaOrigem) {
			boolean naoCancelado = !honorario.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao());
			boolean naoGlosado = !honorario.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao());
			if (naoCancelado && naoGlosado) {
				possuiHonorariosInternos = true;
				break;
			}
		}
		boolean possuiHonorarios = possuiHonorariosExternos || possuiHonorariosInternos;
	
		return possuiHonorarios;
	}
	
	public boolean contemAlgumHonorarioExterno() {
		return !this.getHonorariosExternosNaoCanceladosEGlosados().isEmpty();
	}
	
	@Override
	public boolean jaPossuiuAlgumHonorario(){
		return !this.getHonorariosGuiaHonorarioMedico().isEmpty() || !this.getHonorariosGuiaOrigem().isEmpty();
	}

	/**
	 * Método utilizado para a busca dos profissionais no auto-complete para a posição de profissional responsável 
	 * @param caracteres
	 * @return
	 */
	public List<Profissional>  getProfissionalResponsavel(String caracteres){
		return getCredenciados(caracteres);
	}
	
	/**
	 * Método utilizado para a busca dos profissionais no auto-complete para a posição de primeiro auxiliar 
	 * @param caracteres
	 * @return
	 */
	public List<Profissional>  getProfissionalAuxiliar1(String caracteres){
		return getCredenciados(caracteres);
	}
	
	/**
	 * Método utilizado para a busca dos profissionais no auto-complete para a posição de segundo auxiliar 
	 * @param caracteres
	 * @return
	 */
	public List<Profissional>  getProfissionalAuxiliar2(String caracteres){
		return getCredenciados(caracteres);
	}
	
	/**
	 * Método utilizado para a busca dos profissionais no auto-complete para a posição de teceiro auxiliar 
	 * @param caracteres
	 * @return
	 */
	public List<Profissional>  getProfissionalAuxiliar3(String caracteres){
		return getCredenciados(caracteres);
	}
	
	public List<Profissional> getCredenciados(String caracteres){
		List<Profissional> profissionais  = null;
		
		profissionais =  buscaProfissionais(caracteres);

//		profissionais = removerNaoCredenciados(profissionais);
		
		return profissionais;
	}
	
	private List<Profissional> buscaProfissionais(String caracteres){
		SearchAgent sa = new SearchAgent();
		
		Criteria crit = sa.createCriteriaFor(Profissional.class);
		crit.add(Restrictions.ilike("pessoaFisica.nome", caracteres, MatchMode.ANYWHERE));
		crit.add(Restrictions.eq("situacao.descricao", SituacaoEnum.ATIVO.descricao()));
		
		return (List<Profissional>)crit.list();
	}
	
	private List<Profissional> removerNaoCredenciados(List<Profissional> profissionais) {
		List<Profissional> credenciados = new ArrayList<Profissional>();
		
		for (Iterator iterator = profissionais.iterator(); iterator.hasNext();) {
			Profissional profissional = (Profissional) iterator.next();
			
			if(profissional.isCredenciado())
				credenciados.add(profissional);
		}
		
		return credenciados;
	}

	@Override
	public MotivoGlosa getMotivoGlosaProcedimento() {
		return this.motivoGlosaProcedimento;
	}

	@Override
	public boolean isGlosar() {
		return this.glosar;
	}

	@Override
	public void setGlosar(boolean glosar) {
		this.glosar = glosar;
	}

	@Override
	public void setMotivoGlosaProcedimento(MotivoGlosa motivoGlosaProcedimento) {
		this.motivoGlosaProcedimento = motivoGlosaProcedimento;
	}

	@Override
	public BigDecimal getValorDoProcedimento100PorCento() {
		BigDecimal valorDoProcedimento = BigDecimal.ZERO;
		
		valorDoProcedimento = getValorAcordoProcedimento();
		
		if(valorDoProcedimento == null){
			valorDoProcedimento = this.getProcedimentoDaTabelaCBHPM().getValorModerado();
		}
		
		return MoneyCalculation.rounded(valorDoProcedimento);
	}
	
	@Override
	public void setPorcentagem(BigDecimal porcentagem) {
		
	}
	
	/**
	 * Método que busca a situação atual no componente coleção situações.
	 * Usado para mostrar um drill-down de situação em procedimentos.tag.
	 * 
	 * Na maioria dos casos prefirar usar o {@link #getSituacao()}.
	 * @return
	 */
	public SituacaoInterface getSituacaoAtual() {
		String descricao = this.getSituacao().getDescricao();
		return this.getColecaoSituacoes().getSituacao(descricao);
	}

	public Profissional getProfissionalResponsavelTemp() {
		return profissionalResponsavelTemp;
	}

	public void setProfissionalResponsavelTemp(
			Profissional profissionalResponsavelTemp) {
		this.profissionalResponsavelTemp = profissionalResponsavelTemp;
	}
	
	@Override
	public ItemGlosavel clone() {
		Procedimento clone = (Procedimento) newInstance();

		for (Honorario honorario : this.honorariosGuiaOrigem) {
			Honorario honorarioClonado = null;
			try {
				honorarioClonado = (Honorario) honorario.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			honorarioClonado.setProcedimento(clone);
			clone.getHonorariosGuiaOrigem().add(honorarioClonado);
		}
		
		clone.setProfissionalResponsavel(this.profissionalResponsavel);
		clone.setIncluiVideo(incluiVideo);
		clone.setMotivoGlosaProcedimento(motivoGlosaProcedimento);
		clone.setJustificativaGlosa(justificativaGlosa);
		clone.setPorcentagem(this.getPorcentagem());
		clone.bilateral = this.bilateral;
		clone.horarioEspecial = this.horarioEspecial;
		clone.procedimentoDaTabelaCBHPM = this.procedimentoDaTabelaCBHPM;
		clone.setColecaoSituacoes(SituacaoUtils.clone(this.getColecaoSituacoes()));
		clone.setSituacao(this.getSituacao());
		clone.quantidade = this.quantidade;
		clone.valorAtualDaModeracao = this.valorAtualDaModeracao;
		clone.valorAtualDoProcedimento = this.valorAtualDoProcedimento;
		clone.valorCoParticipacao = this.valorCoParticipacao;
		
		return clone;
	}
	public String getMotivoInsercao() {
		return motivoInsercao;
	}

	public void setMotivoInsercao(String motivoInsercao) {
		this.motivoInsercao = motivoInsercao;
	}

	public boolean isCancelar() {
		return cancelar;
	}

	public void setCancelar(boolean cancelar) {
		this.cancelar = cancelar;
	}
	
	@Override
	public ProcedimentoInterface newInstance(){
		return new Procedimento();
	}
	
	@Override
	public ProcedimentoInterface newInstanceOthers(){
		return new ProcedimentoOutros();
	}
	
	/**
	 * Verifica se o procedimento foi autorizado.
	 */
	@Override
	public boolean autorizado() {
		return ProcedimentoUtils.isAutorizado(this.getSituacao().getDescricao());
	}
	
	/**
	 * Verifica se a guia etá com status de solicitada.
	 */
	@Override
	public boolean solicitado() {
		return ProcedimentoUtils.isSolicitado(this.getSituacao().getDescricao());
	}
	
	@Override
	public boolean isAnular() {
		return anular;
	}

	@Override
	public void setAnular(boolean anular) {
		this.anular = anular;
		
	}

	@Override
	public boolean getAnular() {
		return anular;
	}

	@Override
	public void anular(UsuarioInterface usuario) {
		if(!this.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao()) && !this.isSituacaoAtual(SituacaoEnum.AGUARDANDO_COBRANCA.descricao())){
			this.mudarSituacao(usuario, SituacaoEnum.AGUARDANDO_COBRANCA.descricao(), null, new Date());
		}
	}
	public BigDecimal getValorCalculadoProcedimento() {
		recalculaValorProcedimento();
		return valorCalculadoProcedimento;
	}

	private void recalculaValorProcedimento() {
		BigDecimal valorA100PorCento = this.getValorDoProcedimento100PorCento();
		BigDecimal porcentagem = this.getPorcentagem().movePointLeft(2);
		this.valorCalculadoProcedimento = valorA100PorCento.multiply(porcentagem);
	}
	
	@Override
	public void desfazerAnular(UsuarioInterface usuario) {
		if(this.isSituacaoAtual(SituacaoEnum.AGUARDANDO_COBRANCA.descricao())){
			this.mudarSituacao(usuario, this.getSituacaoAnterior(this.getSituacao()).getDescricao(), null, new Date());
		}
	}

	public BigDecimal getValorModeradoDaTabelaCbhpmAoDobrar() {
	    return valorModeradoDaTabelaCbhpmAoDobrar;
	}

	public void setValorModeradoDaTabelaCbhpmAoDobrar(
		BigDecimal valorModeradoDaTabelaCbhpmAoDobrar) {
	    this.valorModeradoDaTabelaCbhpmAoDobrar = valorModeradoDaTabelaCbhpmAoDobrar;
	}

	public boolean isDesfazerGlosa() {
		return desfazerGlosa;
	}

	public void setDesfazerGlosa(boolean desfazerGlosa) {
		this.desfazerGlosa = desfazerGlosa;
	}
	
	public boolean isGlosado(){
		return this.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao());
	}
	
	@Override
	public void setItemGlosavelAnterior(ItemGlosavel anterior) {
		this.itemGlosavelAnterior = anterior;
	}

	@Override
	public ItemGlosavel getItemGlosavelAnterior() {
		return itemGlosavelAnterior;
	}

	@Override
	public ItemGlosavel accept(ItemGlosavelVisitor visitor) {
		return visitor.visit(this);
	}
	
	public boolean isTipoProcedimento() {
		return true;
	}

	@Override
	public boolean isTipoGuia() {
		return false;
	}

	@Override
	public boolean isTipoItemGuia() {
		return false;
	}

	public ItemRecursoGlosa getItemRecurso() {
		return itemRecurso;
	}

	public void setItemRecurso(ItemRecursoGlosa itemRecurso) {
		this.itemRecurso = itemRecurso;
	}

	@Override
	public void setJustificativaGlosa(String justificativaGlosa) {
		this.justificativaGlosa = justificativaGlosa;
	}

	@Override
	public String getJustificativaGlosa() {
		return justificativaGlosa;
	}
	
	public BigDecimal getValorItem() {
		return this.getValorTotal();
	}

	public Date getDataRealizacao() {
		return dataRealizacao;
	}

	public void setDataRealizacao(Date dataRealizacao) {
		this.dataRealizacao = dataRealizacao;
	}
	
	public boolean isConsulta() {
		if (this.getProcedimentoDaTabelaCBHPM().getGrupo() == TabelaCBHPM.CONSULTAS_E_VISITAS) {
			return true;
		}
		return false;
	}
	
	/**
	 * Método que verifica se o procedimento pode gerar honorário clínico.
	 * @author Luciano Rocha
	 * @since 01/02/2013
	 * @return
	 */
	public boolean isPermiteHonorarioClinico(){
		if (this.getProcedimentoDaTabelaCBHPM().getCodigo().equals("10103015") || 
			this.getProcedimentoDaTabelaCBHPM().getCodigo().equals("10102019") ||
			this.getProcedimentoDaTabelaCBHPM().getCodigo().equals("10104020") ||
			this.getProcedimentoDaTabelaCBHPM().getCodigo().equals("10103023")) {
			return true;
		}
		return false;
	}
	
	public Integer getActionRegulacao() {
		return actionRegulacao;
	}

	public void setActionRegulacao(Integer actionRegulacao) {
		this.actionRegulacao = actionRegulacao;
	}
}
