package br.com.infowaypi.ecare.segurados;

import static br.com.infowaypi.ecarebc.enums.SituacaoEnum.ATIVO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.FlushMode;
import org.hibernate.Query;

import br.com.infowaypi.ecare.constantes.Constantes;
import br.com.infowaypi.ecare.enums.SituacaoCartaoEnum;
import br.com.infowaypi.ecare.financeiro.conta.DetalheContaSegurado;
import br.com.infowaypi.ecare.questionarioqualificado.Questionario;
import br.com.infowaypi.ecare.segurados.validators.AbstractSeguradoValidator;
import br.com.infowaypi.ecare.segurados.validators.SeguradoValidator;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.factory.UsuarioFactory;
import br.com.infowaypi.ecarebc.atendimentos.GuiaAtendimentoUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsulta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExameOdonto;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacaoEletiva;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.painelDeControle.PainelDeControle;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.ecarebc.promocao.PromocaoConsulta;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.ecarebc.utils.Alteracao;
import br.com.infowaypi.ecarebc.utils.ComponentColecaoAlteracoes;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.SearchAgentInterface;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.Greater;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.molecular.parameter.IsNull;
import br.com.infowaypi.molecular.parameter.LowerEquals;
import br.com.infowaypi.molecular.parameter.NotIn;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.Situacao;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;
import br.com.infowaypi.ecare.contrato.ContratoSR;

/**
 * Classe abstrata com atributos comuns entre as classes Titular e Banco.
 * @author Mário Sérgio Coelho Marroquim
 * @changes Danilo Portela
 * @changes Pedro Almir
 * 15/08/2013: Requisito #1997 Introdução e pré-requisitos do modulo de guia SP/SADT ambulatorial
 * Refatorar o cadastro de beneficiário: Incluir o campo 'cartão nacional de saúde'. Esse não não será obrigatório durante o cadastro.
 */
@SuppressWarnings({"rawtypes","serial","static-access", "unchecked"})
public abstract class Segurado extends AbstractSegurado implements SeguradoBasico {
	protected static Date diaD = Utils.parse("01/06/2007");
	
	protected int ordem;
	protected String numeroDoCartao;
	/**
	 * Cartão Nacional de Saúde
	 */
	private String cartaoNacionalSaude;
	protected Date dataVencimentoCarteira;
	protected String descricaoLesao;
	protected String observacao;
	private int desconto;
	private String motivoDesconto;
	private String justificativaMotivo;
	private Set<PromocaoConsulta> consultasPromocionais = new HashSet<PromocaoConsulta>();
	private boolean recadastrado;
	private SituacaoInterface situacaoCadastral;
	private Date dataGeracaoDoCartao;
	private Set<Cartao> cartoes;
	private Date aplicacaoQuestionario;
	private Date dataUltimaAtualizacao;
	private UsuarioInterface usuarioUltimaAtualizacao;
	private Date dataPenultimaAtualizacao;
	private UsuarioInterface usuarioPenultimaAtualizacao;
	
	/** usuario do Segurado, usado no portal do Beneficiario*/
	private UsuarioInterface usuario;
	
	/**
	 * Questionário DLP
	 */
	private Set<Questionario> questionarios;
	private Questionario questionario;
	
	public static final String LIBERADO = "Liberado(a)";
	public static final int DIFERENCA_MAXIMA_EM_DIAS_REATIVACAO = -3;
	public static final int PERIODO_MAXIMA_EM_DIAS_REATIVACAO = -30;
	
	/**
	 * Coleção responsável por amazenar os logs do sistema 
	 */
	private ComponentColecaoAlteracoes colecaoAlteracoes;
	
	/**
	 * Campo transiente utilizado para registrar os motivos de alteração de data de
	 * nascimento e data de adesão.
	 */
	private transient String motivoAlteracao;
	
	public boolean isSeguradoOdonto(){
		ContratoSR contratoAtual = getContratoAtual();
		if(contratoAtual == null) { return false;}
		return contratoAtual.getProduto().isProdutoCoberturaOdontologica();
	}
	
	public boolean isSeguradoHospitalar(){
		ContratoSR contratoAtual = getContratoAtual();
		if(contratoAtual == null) { return true;}
		return contratoAtual.getProduto().isProdutoCoberturaHospitalar();
	}
	
	public Date getAplicacaoQuestionario() {
		return aplicacaoQuestionario;
	}

	public void setAplicacaoQuestionario(Date aplicacaoQuestionario) {
		this.aplicacaoQuestionario = aplicacaoQuestionario;
	}

	public Segurado() {
		this(null);
		this.questionarios = new HashSet<Questionario>();
	}
	
	public int getViaDoCartao(){
		return cartoes.size();
	}
	
	/**
	 * Calcula o valor de segunda via de cartão do segurado vinculando o cartão ao <br>
	 * objeto da classe DetalheContaSegurado do Segurado.
	 * @param detalhe
	 * @return o somatório dos valores de todos os cartões a serem cobrados a segunda via.
	 */
	public BigDecimal calculaValorSegundaViaCartao(DetalheContaSegurado detalhe){
		Set<Cartao> cartoes = new HashSet<Cartao>();
		cartoes = this.getCartoes();
		BigDecimal valorTotalCartoes = BigDecimal.ZERO;
		
		for (Cartao cartao : cartoes) {
			boolean isGerado = cartao.getSituacao().equals(SituacaoCartaoEnum.GERADO.getDescricao());
			boolean isPrimeiraVia = cartao.getViaDoCartao() == 1;
			
			if(isGerado && !isPrimeiraVia){
				detalhe.getCartoes().add(cartao);
				valorTotalCartoes = valorTotalCartoes.add(Cartao.VALOR_CARTAO);
				cartao.setSituacao(SituacaoCartaoEnum.COBRADO.getDescricao());
			}
		}
		
		return valorTotalCartoes;
	}
	
	public Set<Cartao> getCartoes() {
		return cartoes;
	}

	public void setCartoes(Set<Cartao> cartoes) {
		this.cartoes = cartoes;
	}
	
	public Segurado(UsuarioInterface usuario) {
		super(usuario);
		recadastrado 	= false;
		cartoes 		= new HashSet<Cartao>();
		
		this.consultasPromocionais 	= new HashSet<PromocaoConsulta>();
		this.colecaoAlteracoes		= new ComponentColecaoAlteracoes();
	}
	
	/**
	 * Conjunto de validadores para os segurados
	 */
	private static Collection<AbstractSeguradoValidator> seguradoValidators = new ArrayList<AbstractSeguradoValidator>();
	static{
		seguradoValidators.add(new SeguradoValidator());
	}
	
	public SituacaoInterface getSituacaoCadastral() {
		return situacaoCadastral;
	}

	public void setSituacaoCadastral(SituacaoInterface situacaoCadastral) {
		this.situacaoCadastral = situacaoCadastral;
	}

	public int getDesconto() {
		return desconto;
	}

	public void setDesconto(int desconto) {
		this.desconto = desconto;
	}

	public String getMotivoDesconto() {
		return motivoDesconto;
	}

	public void setMotivoDesconto(String motivoDesconto) {
		this.motivoDesconto = motivoDesconto;
	}

	public String getJustificativaMotivo() {
		return justificativaMotivo;
	}

	public void setJustificativaMotivo(String justificativaMotivo) {
		this.justificativaMotivo = justificativaMotivo;
	}

	@SuppressWarnings("unused")
	private SituacaoInterface getSituacaoAnteriorGuia(SituacaoInterface situacaoAtual, GuiaSimples<ProcedimentoInterface> guia) {
	
			int ordemSituacao = situacaoAtual.getOrdem();
			if (ordemSituacao == 1)
				return null;
			
			for(SituacaoInterface situacao : guia.getSituacoes()){
				if (situacao.getOrdem() == ordemSituacao - 1)
					return situacao;
			}
			return situacaoAtual;

	}

	public void cancelar(String motivo, UsuarioInterface usuario) throws Exception {
		boolean isSeguradoAtivo 	= this.isSituacaoAtual(SituacaoEnum.ATIVO.descricao());
		boolean isSeguradoSuspenso 	= this.isSituacaoAtual(SituacaoEnum.SUSPENSO.descricao());
		
		if(!isSeguradoAtivo && !isSeguradoSuspenso){
			throw new ValidateException("Só é possível cancelar segurados em situação Ativo(a) ou Suspenso(a)");
		}
		
		if (Utils.isStringVazia(motivo)){
			throw new Exception("O motivo deve ser preenchido!");
		}
		
		this.mudarSituacao(usuario, Constantes.SITUACAO_CANCELADO, motivo, new Date());
		
		/**
		 * Usado para cancelar o usuário do segurado no portal bo beneficiário.
		 */
		UsuarioInterface usuarioSegurado = this.getUsuario();
		usuarioSegurado.setStatus(Usuario.CANCELADO);
	}

	public void iniciarCarencia(Date dataInicioCarencia, UsuarioInterface usuario) throws Exception {
		this.setInicioDaCarencia(dataInicioCarencia);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof SeguradoInterface)) {
			return false;
		}
		SeguradoInterface otherObject = (SeguradoInterface) object;
		return new EqualsBuilder().append(this.getIdSegurado(), otherObject
				.getIdSegurado()).isEquals();
	}


	public Date getDataVencimentoCarteira() {
		return this.dataVencimentoCarteira;
	}

	public String getDescricaoDaDependencia() {
		if (this.isSeguradoTitular())
			return Constantes.DESCRICAO_TIPO_DEPENDENCIA_TITULAR;

		if (this.isSeguradoDependente()) {
			SearchAgentInterface sa = new SearchAgent();
			sa.addParameter(new Equals("contrato", this.getContrato()));
			List<SeguradoInterface> segurados = sa.list(Segurado.class);
			if (segurados != null && !segurados.isEmpty()) {
				DependenteSR dependente = (DependenteSR) segurados.get(0);
				return dependente.getDescricaoDaDependencia();
			}
		}
		return null;
	}

	public String getDescricaoLesao() {
		return descricaoLesao;
	}

	public String getGrauDeDependencia() {
		return this.getDescricaoDaDependencia();
	}


	public String getObservacao() {
		return observacao;
	}


	public Map<Date, TabelaCBHPM> getProcedimentosRealizados() throws ValidateException {
		Map<Date, TabelaCBHPM> procedimentos = new HashMap<Date, TabelaCBHPM>();
		if (this.guias != null) {
			for (GuiaSimples<Procedimento> guia : this.getGuias()) {
				if (guia.getProcedimentos() != null) {
					for (Procedimento procedimento : guia.getProcedimentos()) {
						SituacaoInterface situacaoAtiva = procedimento.getSituacao(ATIVO.descricao());
						Date dataDeInclusao = null;
						
						if (situacaoAtiva != null){
							dataDeInclusao = situacaoAtiva.getDataSituacao();
						}

						if (dataDeInclusao != null){
							throw new ValidateException("A guia possui procedimentos inválidos!");
						}

						procedimentos.put(dataDeInclusao, procedimento.getProcedimentoDaTabelaCBHPM());
					}
				}
			}
		}
		return procedimentos;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.contrato).toHashCode();
	}

	public boolean isAtivo() {
		return this.isSituacaoAtual(ATIVO.descricao());
	}

	public void reativar(String motivo, Date dataAdesao, UsuarioInterface usuario) throws Exception {
		ManagerSegurado.reativarTitular(this,motivo, dataAdesao, usuario);
	}

	public void setDataVencimentoCarteira(Date dataVencimentoCarteira) {
		this.dataVencimentoCarteira = dataVencimentoCarteira;
	}

	public void setDescricaoLesao(String descricaoLesao) {
		this.descricaoLesao = descricaoLesao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public void suspender(String motivo, UsuarioInterface usuario) throws Exception {
		ManagerSegurado.suspenderTitular(this,motivo, usuario);
	}
	
	/**
	 * Busca as guias do segurado por um período específico. Esse período é informado de acordo
	 * com a maior periodicidade dos procedimentos na guia.
	 */
	public <G extends GuiaSimples> Collection<G> buscarGuiasPorPeriodo(Integer periodo, Class<G> klassGuia){
		Calendar c = new GregorianCalendar();
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new GreaterEquals("dataAtendimento", Utils.incrementaDias(c,- periodo)));
		sa.addParameter(new Equals("segurado.idSegurado", this.getIdSegurado()));
		Collection<G> guias = sa.list(klassGuia);
			
		return guias;
	}
	
	/**
	 * Verifica se o segurado entrou no sistema com até 30 dias de vida
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean isEntrouRecemNascido(){
		if(this.dataAdesao != null){
			Calendar nascimento = Calendar.getInstance();
			nascimento.setTime(this.getPessoaFisica().getDataNascimento());
			nascimento.add(Calendar.DAY_OF_MONTH, 30);
			if(Utils.compareData(nascimento.getTime(), this.dataAdesao) <= 0){
				return true;
			}
		}
		return false;
	}
	
	public Boolean validate(UsuarioInterface usuario) throws ValidateException {
		for (AbstractSeguradoValidator validator : this.seguradoValidators) {
			validator.execute(this,null);
		}
		
		this.setDataPenultimaAtualizacao(this.getDataUltimaAtualizacao());
		this.setUsuarioPenultimaAtualizacao(this.usuarioUltimaAtualizacao);
				
		this.setDataUltimaAtualizacao(new Date());
		this.setUsuarioUltimaAtualizacao(usuario);
		
		this.processarAlteracaoDataAdesaoENascimento(usuario);
		
		return true;
	}
	
	public void criarUsuarioSegurado(String role){
		if(getUsuario() == null && !Utils.isStringVazia(getNumeroDoCartao())){
			UsuarioInterface usuarioSegurado = new UsuarioFactory().criarUsuario(getNumeroDoCartao(), role, getNomeFormatado());
			this.setUsuario(usuarioSegurado);
		}
	}
	
	protected void processarAlteracaoDataAdesaoENascimento(UsuarioInterface usuario) throws ValidateException {
		boolean altereouDataAdesao 		= this.isDataAdesaoAlterada();
		boolean alterouDataNascimento 	= this.isDataNascimentoAlterada();
		
		if((altereouDataAdesao || alterouDataNascimento)){
			if (Utils.isStringVazia(this.getMotivoAlteracao())){
				throw new ValidateException("É necessário informar o motivo de alteração da data de adesão e/ou data de nascimento.");
			}
			
			StringBuilder stringBuilder = new StringBuilder();
			if (altereouDataAdesao){
				Date dataAdesaoAnterior = this.getDataAdesaoAnterior();
				stringBuilder.append("Data de adesao anterior: ")
							.append(Utils.format(dataAdesaoAnterior))
							.append(".");
			}
			
			if (alterouDataNascimento) {
				Date dataNascimentoAnterior = this.getDataNascimentoAnterior();
				stringBuilder.append(" Data de nascimento anterior: ")
							.append(Utils.format(dataNascimentoAnterior));
			}
			
			stringBuilder.append("<br/> Motivo da alteração: ")
						.append(this.getMotivoAlteracao());
			
			
			String motivo = stringBuilder.toString();
			
			this.getColecaoAlteracoes().adicionarAlteracao(motivo, usuario, new Date());
			
		}
	}

	/*
	 * Retorna um identificador para ser usado pelo banco
	 * esse identificador deverá ser mostrado após o cadastro do titular
	 */
	public String getIdentificador() {
		return StringUtils.leftPad(this.getIdSegurado().toString(), 5, "0");
	}
	
	public String getNomeFormatado(){
		if (this.getPessoaFisica().getNome().length() < 34)
			return this.getPessoaFisica().getNome();
		String[] nomes = StringUtils.split(this.getPessoaFisica().getNome(), " ");
		for (int i = 1; i < nomes.length; i++){
			nomes[nomes.length - i - 1] ="" + nomes[nomes.length - i - 1].charAt(0);
			String nomeFormatado = "";
			for (String nome : nomes) {
				nomeFormatado += nome + " ";
			}
			if (nomeFormatado.trim().length() < 38)
				return nomeFormatado;
		}
		return this.getPessoaFisica().getNome();
	}
	
	public int getSexo() {
		return this.pessoaFisica.getSexo();
	}

	public int getIdade() {
		return this.pessoaFisica.getIdade();
	}

	
	public List<GuiaConsulta> getConsultas() {
		SearchAgent sa  = new SearchAgent();
		sa.addParameter(new Equals("segurado",this));
		return sa.list(GuiaConsulta.class);
	}

	public List<GuiaExame> getExames() {
		SearchAgent sa  = new SearchAgent();
		sa.addParameter(new Equals("segurado",this));
		return sa.list(GuiaExame.class);
	}
	
	public Collection<GuiaExameOdonto> getExamesOdonto() {
		SearchAgent sa  = new SearchAgent();
		sa.addParameter(new Equals("segurado",this));
		return sa.list(GuiaExameOdonto.class);
	}
	
	public List<GuiaAtendimentoUrgencia> getAtendimentosUrgencia() {
		SearchAgent sa  = new SearchAgent();
		sa.addParameter(new Equals("segurado",this));
		return sa.list(GuiaAtendimentoUrgencia.class);
	}

	public List<GuiaConsultaUrgencia> getConsultasUrgencia() {
		SearchAgent sa  = new SearchAgent();
		sa.addParameter(new Equals("segurado",this));
		return sa.list(GuiaConsultaUrgencia.class);
	}
	
	public List<GuiaInternacaoEletiva> getInternacoesEletivas() {
		SearchAgent sa  = new SearchAgent();
		sa.addParameter(new Equals("segurado",this));
		return sa.list(GuiaInternacaoEletiva.class);
	}
	
	public Set<GuiaSimples> getTodasGuias() {
		return this.getGuias();
	}
	
	
	public GuiaSimples getUltimaConsultaUrgencia(Prestador prestador){
		List<GuiaConsultaUrgencia> guias = getConsultasUrgencia();
		
		if(guias.size() == 0)
			return null;
		
		GuiaSimples ultimaGuia = null;
		for (GuiaConsultaUrgencia guia : guias) {
			if(guia.getPrestador().equals(prestador)){
				if(ultimaGuia == null )
					ultimaGuia = guia;
				else if (Utils.compareData(guia.getDataAtendimento(),ultimaGuia.getDataAtendimento()) > 0)
					ultimaGuia = guia;
			}
		}
		return ultimaGuia;
	}

	public Boolean getCumpreCarencia() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setCumpreCarencia(Boolean arg0) {
		// TODO Auto-generated method stub
	}
	
	public Collection<GuiaConsulta> buscarConsultasPorPeriodo(Integer arg0) {
		return buscarGuiasPorPeriodo(arg0,GuiaConsulta.class);
		
	}

	public Collection<GuiaExame> buscarExamesPorPeriodo(Integer arg0) {
		return buscarGuiasPorPeriodo(arg0,GuiaExame.class);
	}
	
	public String getNumeroDoCartao() {
		/* if[VERIFICAR_NUMERO_CARTAO_BASEADO_NO_CONTRATO]
			ContratoSR contratoAtual = getContratoAtual();
			if (contratoAtual != null){
				return contratoAtual.getNumeroCartaoAtual();
			}
			return null;
		else[VERIFICAR_NUMERO_CARTAO_BASEADO_NO_CONTRATO]*/
		return numeroDoCartao;
		/* end[VERIFICAR_NUMERO_CARTAO_BASEADO_NO_CONTRATO]*/
	}
	
	public void setNumeroDoCartao(String numeroDoCartao) {
		this.numeroDoCartao = numeroDoCartao;
	}
	
	public void setRecadastrado(boolean recadastrado) {
		this.recadastrado = recadastrado;
	}
	
	public boolean isRecadastrado() {
		return recadastrado;
	}
	
	public abstract String getTipoPlano();
	
	public int getCarenciaCumprida() {
		Calendar hoje = Calendar.getInstance();
		Calendar inicioDaCarencia = Calendar.getInstance();
		
		if(this.getInicioDaCarencia()!= null) {
			inicioDaCarencia.setTime(this.getInicioDaCarencia());
			return Utils.diferencaEmDias(inicioDaCarencia, hoje);
		}else {
			return 0;
		}
			
	}
	
	public int getCarenciaCumpridaEmHoras() {
		return getCarenciaCumprida()*24;
	}
	
	public List<PromocaoConsulta> getConsultasPromocionaisEmSituacao(String... descricao) {
		List<PromocaoConsulta> consultasValidas = new ArrayList<PromocaoConsulta>();
		if (this.getConsultasPromocionais() != null) {
			for (PromocaoConsulta consulta : this.getConsultasPromocionais()) {
				boolean isValida = false;
				for (String situacao : descricao){
					if (consulta.isSituacaoAtual(situacao)) {
						isValida = true;
						break;
					}
				}
				if (isValida){
					consultasValidas.add(consulta);
				}
			}
		}
		
		return consultasValidas;
	}
	
	public Set<PromocaoConsulta> getConsultasPromocionais() {
		return consultasPromocionais;
	}

	public void setConsultasPromocionais(Set<PromocaoConsulta> consultasPromocionais) {
		this.consultasPromocionais = consultasPromocionais;
	}
	
	public List<PromocaoConsulta> getConsultasPromocionaisLiberadas(){
		return this.getConsultasPromocionaisEmSituacao(LIBERADO);
	}
	
	public Cartao getCartaoAtual(){
		Cartao cartaoAtual = null;
		for (Cartao cartao : cartoes) {
			if(cartaoAtual == null)
				cartaoAtual = cartao;
			else if (cartaoAtual !=null && cartao.getViaDoCartao().compareTo(cartaoAtual.getViaDoCartao()) > 0){
				cartaoAtual = cartao;
			}
		}
		return cartaoAtual;
	}
	
	public Cartao gerarCartao(){
		Cartao cartao = new Cartao(this);
		this.setNumeroDoCartao(cartao.getNumeroDoCartao());
		return cartao;
	}
	
	public void setConsultaPromocional(PromocaoConsulta consultaPromocional) throws ValidateException{
		boolean consultaPromocionaJaExiste = false;
		if(this.getConsultasPromocionais().size() > 0) {
			for (PromocaoConsulta consultaPromo : this.getConsultasPromocionais()) {
				boolean isEspecialidadeIgual = consultaPromo.getEspecialidade().equals(consultaPromocional.getEspecialidade());
				if(isEspecialidadeIgual && consultaPromo.isLiberado()) {
					consultaPromocionaJaExiste = true;
					throw new ValidateException("Já existe para este segurado uma consulta sem co-participação liberada com essa especialidade.");
				}
			}
			if(!consultaPromocionaJaExiste) {
				this.consultasPromocionais.add(consultaPromocional);
			}
		}else{
			this.consultasPromocionais.add(consultaPromocional);
		}
		
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		
		this.ordem = ordem;
	}
	
	public int getCarenciaRestanteUrgencias() {
		int carenciaRestante = (24 - this.getCarenciaCumpridaEmHoras());
		if(carenciaRestante < 0) {
			return 0;
		}
		return  carenciaRestante;
	}
	
	public int getCarenciaRestanteConsultasExamesDeBaixaComplexidade() {
		int carenciaRestante = (15 - this.getCarenciaCumprida());
		if(carenciaRestante < 0) {
			return 0;
		}
		
		return carenciaRestante;
	}
	
	public String getCarenciaRestanteConsultasExamesDeBaixaComplexidadeDataFinal() {
		Date inicioDaCarencia = this.getInicioDaCarencia();
		if(inicioDaCarencia == null) { 
			return ""; 
		}
		
		Date dataCarencia = Utils.incrementaDias(inicioDaCarencia, 15);
		return formatarDataCarencia(dataCarencia);
	}
	
	public int getCarenciaRestanteExamesEspeciaisDeAltaComplexidadeCirurgiasEInternamento() {
		int carenciaRestante = (180 - this.getCarenciaCumprida());
		if(carenciaRestante < 0) {
			return 0;
		}
		
		return carenciaRestante;
	}
	
	public String getCarenciaRestanteExamesEspeciaisDeAltaComplexidadeCirurgiasEInternamentoDataFinal() {
		Date inicioDaCarencia = this.getInicioDaCarencia();
		if(inicioDaCarencia == null) { 
			return ""; 
		}
		
		Date dataCarencia = Utils.incrementaDias(inicioDaCarencia, 180);
		return formatarDataCarencia(dataCarencia);
	}
	
	public int getCarenciaRestanteParaPartos() {
		int carenciaRestante = (300 - this.getCarenciaCumprida());
		if(carenciaRestante < 0) {
			return 0;
		}
		
		return carenciaRestante;
	}
	
	public String getCarenciaRestanteParaPartosDataFinal() {
		Date inicioDaCarencia = this.getInicioDaCarencia();
		if(inicioDaCarencia == null) { 
			return ""; 
		}
		
		Date dataCarencia = Utils.incrementaDias(inicioDaCarencia, 300);
		return formatarDataCarencia(dataCarencia);
	}
	
	public int getCarenciaRestanteParaDoencasPreExistentes() {
		int carenciaRestante = (PainelDeControle.getPainel().getCarenciaDLPs() - this.getCarenciaCumprida());
		if(carenciaRestante < 0) {
			return 0;
		}
		
		return carenciaRestante;
	}
	
	public String getCarenciaRestanteParaDoencasPreExistentesDataFinal() {
		Date inicioDaCarencia = this.getInicioDaCarencia();
		if(inicioDaCarencia == null) { 
			return ""; 
		}
		
		int carenciaDLPs = PainelDeControle.getPainel().getCarenciaDLPs();
		
		Date dataCarencia = Utils.incrementaDias(inicioDaCarencia, carenciaDLPs);
		return formatarDataCarencia(dataCarencia);
	}
	
	public Date getCarenciaOdontologicoProcedimentosSimples(){
		Date inicioCarencia = this.getInicioDaCarencia();
		if(inicioCarencia!= null) {
			return Utils.incrementaDias(inicioCarencia, 30);
		}		
		return null;
	}
	
	public Date getCarenciaOdontologicoProcedimentosComplexos(){
		Date inicioCarencia = this.getInicioDaCarencia();
		if(inicioCarencia!= null) {
			return Utils.incrementaDias(inicioCarencia, 180);
		}		
		return null;
	}
	
	public String getCarenciaOdontologicoProcedimentosSimplesFormatada() {
		Date dataCarencia = getCarenciaOdontologicoProcedimentosSimples();
		if(dataCarencia == null) { 
			return ""; 
		}
		return formatarDataCarencia(dataCarencia);
	}

	public String getCarenciaOdontologicoProcedimentosComplexosFormatada() {
		Date dataCarencia = getCarenciaOdontologicoProcedimentosComplexos();
		if(dataCarencia == null) { 
			return ""; 
		}
		return formatarDataCarencia(dataCarencia);
	}
	
	private String formatarDataCarencia(Date dataCarencia) {
		if(dataCarencia == null){
			return "";
		}
		
		Calendar hoje = Calendar.getInstance();
		hoje.setTime(new Date());
		hoje.set(Calendar.HOUR_OF_DAY, 0);
		hoje.set(Calendar.MINUTE, 0);
		hoje.set(Calendar.SECOND, 0);
		hoje.set(Calendar.MILLISECOND, 0);
		
		Calendar inicioDaCarencia = Calendar.getInstance();
		inicioDaCarencia.setTime(dataCarencia);
		inicioDaCarencia.set(Calendar.HOUR_OF_DAY, 0);
		inicioDaCarencia.set(Calendar.MINUTE, 0);
		inicioDaCarencia.set(Calendar.SECOND, 0);
		inicioDaCarencia.set(Calendar.MILLISECOND, 0);
		
		int diferencaEmDias = Utils.diferencaEmDias(hoje, inicioDaCarencia);
		
		if(diferencaEmDias < 0) {
			diferencaEmDias = 0;
		}
		
		String dataCarenciaFormatada = Utils.format(dataCarencia, "dd/MM/yyyy");
		
		/* if[MOSTRAR_DIAS_RESTANTES_CARENCIA_ODONTO]
			return dataCarenciaFormatada;
		else[MOSTRAR_DIAS_RESTANTES_CARENCIA_ODONTO]*/
		
		return diferencaEmDias + " - " + dataCarenciaFormatada;
		/*end[MOSTRAR_DIAS_RESTANTES_CARENCIA_ODONTO]*/
	}
	
	public void setGuias(Set<GuiaSimples> guias) {
		this.guias = guias;
	}
	
	
	public Date getDataGeracaoDoCartao() {
		return dataGeracaoDoCartao;
	}

	public void setDataGeracaoDoCartao(Date dataGeracaoDoCartao) {
		this.dataGeracaoDoCartao = dataGeracaoDoCartao;
	}
	
	
	public Set<GuiaInternacao> getGuiasInternacaoAbertas(){
		
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
		
		Date dataInicioFluxoAlta = Utils.parse("15/06/2008");
		SearchAgent sa = new SearchAgent();
		
		List<String> listaDeSituacoes = new ArrayList<String>();
		listaDeSituacoes.add(SituacaoEnum.FATURADA.descricao());
		listaDeSituacoes.add(SituacaoEnum.AUDITADO.descricao());
		listaDeSituacoes.add(SituacaoEnum.FECHADO.descricao());
		listaDeSituacoes.add(SituacaoEnum.CANCELADO.descricao());
		listaDeSituacoes.add(SituacaoEnum.NAO_AUTORIZADO.descricao());
		listaDeSituacoes.add(SituacaoEnum.INAPTO.descricao());
		listaDeSituacoes.add(SituacaoEnum.PAGO.descricao());
		
		sa.addParameter(new NotIn("situacao.descricao",listaDeSituacoes));
		sa.addParameter(new Equals("segurado", this));
		sa.addParameter(new LowerEquals("dataAtendimento", dataInicioFluxoAlta));
		sa.addParameter(new Equals("guiaParcial", false));
		List<GuiaInternacao> guiasAntesFluxoAlta = sa.list(GuiaInternacao.class);
		
		listaDeSituacoes = new ArrayList<String>();
		listaDeSituacoes.add(SituacaoEnum.CANCELADO.descricao());
		listaDeSituacoes.add(SituacaoEnum.NAO_AUTORIZADO.descricao());
		
		sa.clearAllParameters();
		sa.addParameter(new NotIn("situacao.descricao",listaDeSituacoes));
		sa.addParameter(new Equals("segurado", this));
		sa.addParameter(new Greater("dataAtendimento", dataInicioFluxoAlta));
		sa.addParameter(new IsNull("altaHospitalar"));
		sa.addParameter(new Equals("guiaParcial", false));
		List<GuiaInternacao> guiasDepoisFluxoAlta = sa.list(GuiaInternacao.class);
		Set<GuiaInternacao> guiasDeInternacaoAbertas = new HashSet<GuiaInternacao>();
		guiasDeInternacaoAbertas.addAll(guiasAntesFluxoAlta);
		guiasDeInternacaoAbertas.addAll(guiasDepoisFluxoAlta);
		
		return guiasDeInternacaoAbertas;
	}

	
	public boolean isInternado() {
		if(getGuiasInternacaoAbertas().size() > 2)
			return true;
		else
			return false;
	}

	protected void recadastrar(UsuarioInterface usuario) {
		setRecadastrado(true);
		Situacao sitCadastral = new Situacao();
		sitCadastral.setUsuario(usuario);
		sitCadastral.setDescricao(SituacaoEnum.RECADASTRADO.descricao());
		sitCadastral.setDataSituacao(new Date());
		sitCadastral.setMotivo("Recadastramento de segurados.");
		setSituacaoCadastral(sitCadastral);
		if(this.isSituacaoAtual(SituacaoEnum.SUSPENSO.descricao()) && this.getSituacao().getMotivo().equals(MotivoEnum.BENEFICIARIO_NAO_RECADASTRADO.getMessage()))
			this.mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(), "Recadastramento", new Date());
	}
	
	
	public Date getDataUltimoCancelamento() {
		SituacaoInterface ultimaSituacao = null;
		for (SituacaoInterface situacao : this.getSituacoes()) {
			if(situacao.getDescricao().equals(SituacaoEnum.CANCELADO.descricao()))  {
				if(ultimaSituacao == null)
					ultimaSituacao = situacao;
				else {
					if (situacao.getDataSituacao().compareTo(ultimaSituacao.getDataSituacao()) > 0)
						ultimaSituacao = situacao;
				}
			}
		}
		
		if(ultimaSituacao == null) {
			return null;
		}else
			return ultimaSituacao.getDataSituacao();
	}
	
	/**
	 * Configura um sexo inicialmente definido como integer
	 * para String sendo [1]- MASCULINO [2]- FEMININO [3]- AMBOS
	 * @author anderson
	 * */
	public String getSexoFormatado(){
		
		Integer sexo = this.getSexo();
		
		if(sexo.equals(1)){
			return "MASCULINO";
			
		}else
			if(sexo.equals(2)){
				return "FEMININO";
			} else {
				return "AMBOS";
			}
	}
	
	
	
	/**
	 * Retorna a situação que o beneficiario tinha em uma determinada data.
	 * @param data
	 * @return
	 */
	public SituacaoInterface getSituacaoEm(Date data){
		int ordemUltimaSituacao = this.getSituacoes().size();
		
		for (int i = 1; i <= ordemUltimaSituacao; i++) {
			SituacaoInterface situacao = this.getSituacao(i);
			if (situacao == null){
				continue;
			}
			if (Utils.compareData(situacao.getDataSituacao(), data) > 0){
				return this.getSituacao(i - 1);
			}
		}

		return this.getSituacao(ordemUltimaSituacao);
	}
	
	public abstract void updateDataInicioCarencia();

	public Date getDataUltimaAtualizacao() {
		return dataUltimaAtualizacao;
	}

	public void setDataUltimaAtualizacao(Date dataUltimaAtualizacao) {
		this.dataUltimaAtualizacao = dataUltimaAtualizacao;
	}
	
	public Date getDataPenultimaAtualizacao() {
		return dataPenultimaAtualizacao;
	}
	
	public void setDataPenultimaAtualizacao(Date dataPenultimaAtualizacao) {
		this.dataPenultimaAtualizacao = dataPenultimaAtualizacao;
	}
	
	public UsuarioInterface getUsuarioUltimaAtualizacao() {
		return usuarioUltimaAtualizacao;
	}

	public void setUsuarioUltimaAtualizacao(
			UsuarioInterface usuarioUltimaAtualizacao) {
		this.usuarioUltimaAtualizacao = usuarioUltimaAtualizacao;
	}
	
	public UsuarioInterface getUsuarioPenultimaAtualizacao() {
		return usuarioPenultimaAtualizacao;
	}
	
	public void setUsuarioPenultimaAtualizacao(
			UsuarioInterface usuarioPenultimaAtualizacao) {
		this.usuarioPenultimaAtualizacao = usuarioPenultimaAtualizacao;
	}
		
	public ComponentColecaoAlteracoes getColecaoAlteracoes() {
		if(colecaoAlteracoes == null){
			this.colecaoAlteracoes = new ComponentColecaoAlteracoes();
		}
			
		return this.colecaoAlteracoes;
	}

	public void setColecaoAlteracoes(ComponentColecaoAlteracoes colecaoAlteracoes) {
		this.colecaoAlteracoes = colecaoAlteracoes;
	}

	public String getMotivoAlteracao() {
		return motivoAlteracao;
	}

	public void setMotivoAlteracao(String motivoAlteracao) {
		this.motivoAlteracao = motivoAlteracao;
	}
	
	private boolean isDataAdesaoAlterada(){
		Date dataAdesaoAnterior = getDataAdesaoAnterior();
		boolean resultado = false;
		
		if(dataAdesaoAnterior != null && this.getDataAdesao() != null){
			resultado = Utils.compareData(dataAdesaoAnterior, this.getDataAdesao()) != 0;
		}
		
		return resultado;
	}

	protected Date getDataAdesaoAnterior() {
		Long idSegurado = this.getIdSegurado();
		Date dataAdesao = null;
		
		if(idSegurado != null){
			String sql = "select s.dataAdesao from Segurado s where s.idSegurado = :idSegurado";
			 
			Query query = HibernateUtil.currentSession().createQuery(sql);
			query.setLong("idSegurado", idSegurado);
			dataAdesao = (Date) query.uniqueResult();
		}
		
		return dataAdesao;
	}
	
	private boolean isDataNascimentoAlterada(){
		Date dataNascimentoAnterior = getDataNascimentoAnterior();
		boolean resultado = false;
		
		if(dataNascimentoAnterior != null && this.getPessoaFisica().getDataNascimento() != null){
			resultado = Utils.compareData(dataNascimentoAnterior, this.getPessoaFisica().getDataNascimento()) != 0;
		}
		
		return resultado;
	}

	protected Date getDataNascimentoAnterior() {
		Long idSegurado = this.getIdSegurado();
		Date dataNascimento = null;

		String sql = "select dataNascimento from segurados_segurado where idSegurado = :idSegurado";
		
		if(idSegurado != null){
			Query query = HibernateUtil.currentSession().createSQLQuery(sql);
			query.setLong("idSegurado", idSegurado);
			
			dataNascimento = (Date) query.uniqueResult();
		}
		
		return dataNascimento;
	}
	
	public List<Alteracao> getAlteracoes() {
		List<Alteracao> alteracoes = new ArrayList<Alteracao>(this.getColecaoAlteracoes().getAlteracoes());
		
//		Collections.sort(alteracoes, new Comparator<Alteracao>() {
//			@Override
//			public int compare(Alteracao o1, Alteracao o2) {
//				return -o1.getOrdem().compareTo(o2.getOrdem());
//			}
//			
//		});
		return alteracoes;
	}

	public Questionario getQuestionario() {
		return questionario;
	}
	
	public void setQuestionario(Questionario questionario) {
		this.questionario = questionario;
	}

	public Set<Questionario> getQuestionarios() {
		if(this.questionario == null){
			this.questionarios.add(new Questionario(this.getNumeroDoCartao()));
		} else {
			this.questionarios.add(this.getQuestionario());
		}
		return questionarios;
	}

	public UsuarioInterface getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioInterface usuario) {
		this.usuario = usuario;
	}
	
	@Override
	public SituacaoInterface getSituacao() {
		/*if[ATENDIMENTO_BASEADO_NA_SITUACAO_CONTRATO]
			return  this.getContratoAtual().getSituacao();
		else[ATENDIMENTO_BASEADO_NA_SITUACAO_CONTRATO] */
			return super.getSituacao();
		/*end[ATENDIMENTO_BASEADO_NA_SITUACAO_CONTRATO]*/
	}
	
	@Override
	public boolean isSituacaoAtual(String descricao) {
		/*if[ATENDIMENTO_BASEADO_NA_SITUACAO_CONTRATO]
			return  this.getContratoAtual().isSituacaoAtual(descricao);
		else[ATENDIMENTO_BASEADO_NA_SITUACAO_CONTRATO] */
			return super.isSituacaoAtual(descricao);
		/*end[ATENDIMENTO_BASEADO_NA_SITUACAO_CONTRATO]*/
	}

	/**
	 * @return the cartaoNacionalSaude
	 */
	public String getCartaoNacionalSaude() {
		return cartaoNacionalSaude;
	}

	/**
	 * @param cartaoNacionalSaude the cartaoNacionalSaude to set
	 */
	public void setCartaoNacionalSaude(String cartaoNacionalSaude) {
		this.cartaoNacionalSaude = cartaoNacionalSaude;
	}
}