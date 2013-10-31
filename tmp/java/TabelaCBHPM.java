package br.com.infowaypi.ecarebc.procedimentos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.Criteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;

import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.acordos.AcordoCBHPM;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.enums.Visibilidade;
import br.com.infowaypi.ecarebc.odonto.enums.EstruturaOdontoEnum;
import br.com.infowaypi.ecarebc.painelDeControle.PainelDeControle;
import br.com.infowaypi.ecarebc.planos.Carencia.TipoCarencia;
import br.com.infowaypi.ecarebc.procedimentos.validators.AbstractTabelaCBHPMValidator;
import br.com.infowaypi.ecarebc.procedimentos.validators.TabelaCBHPMSimplesValidator;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe que representa um procedimento da Tabela CBHPM
 * @author Danilo Nogueira Portela
 */
 
public class TabelaCBHPM  extends ImplColecaoSituacoesComponent implements Serializable, Constantes {
	private static final long serialVersionUID = 1L;
	
	//constante advinda da tabela CBHPM. Valor representado em reais.
	public static final BigDecimal constanteUco = new BigDecimal(11.5);
	
	//constantes de local de atendimento
	public static final Integer TIPO_AMBULATORIAL = 1;
	public static final Integer TIPO_HOSPITALAR = 2;
	public static final Integer TIPO_AMBOS = 3;
	
	public static final Integer NAO_CATEGORIZADO = 0;
	public static final Integer CONSULTAS_E_VISITAS = 1;
	public static final Integer EXAMES = 2;
	public static final Integer TRATAMENTO_CIRURGICO_AMBULATORIAL = 3;
	public static final Integer TRATAMENTO_CIRURGICO_INTERNADO = 4;
	public static final Integer TRATAMENTO_CLINICO_AMBULATORIAL = 5;
	public static final Integer TRATAMENTO_CLINICO_INTERNADO = 6;
	public static final Integer TRATAMENTO_CLINICO_EMERGENCIAL = 7;
	public static final Integer TRATAMENTO_QUIMIOTERAPICO = 8;
	public static final Integer TRATAMENTO_CLINICO_RADIOTERAPICO = 9;
	public static final Integer TRATAMENTO_PARA_QUEIMADOS = 10;
	public static final Integer TRATAMENTO_GLOBAL = 11;
	public static final Integer TRATAMENTO_SEQUENCIADO = 12;
	public static final Integer TRATAMENTO_HOME_CARE = 13;
	public static final Integer OUTROS = 14;
	
	public static final Integer NIVEL_1 = 1;
	public static final Integer NIVEL_2 = 2;
	public static final Integer NIVEL_3 = 3;
	public static final Integer NIVEL_4 = 4;
	
	public static final Integer COMPLEXIDADE_PBC = 1;
	public static final Integer COMPLEXIDADE_PMC = 2;
	public static final Integer COMPLEXIDADE_PAC = 3;
	
	public static final Integer SEM_AUXILIO = 0;
	public static final Integer UM_AUXILIO = 1;
	public static final Integer DOIS_AUXILIOS = 2;
	public static final Integer TRES_AUXILIOS = 3;
	
	private Long idTabelaCBHPM;
	private boolean ativo;
	private String codigo;
	
	/**
	 * campo auxiliar utilizado para não permitir a alteração do código CBHPM.
	 */
	private String codigoInicial;
	/**
	 * Nome do procedimento.
	 */
	private String descricao;
	private String descricaoGrupo;
	private String descricaoSubGrupo;
	private String codigoSubGrupoDescricao;
	/**
	 * Concatenação dos campos {@link #codigo} e {@link #descricao}.
	 */
	private String codigoEDescricao;
	
	/**
	 * Unidade de Custo Operacional.
	 */
	private BigDecimal uco;
	
	/**
	 * Valor da Unidade de Custo Operacional.
	 */
	private BigDecimal valorUco;
	
	/**
	 * Porcentagem do valor total do procedimento que será cobrado do beneficário a título de co-participacao.
	 */
	/*
	 * Após mudança nos requisitos este campo permaneceu com o nome moderação pela sua criticidade e a falta de testes automatizados. 
	 */
	private Float moderacao;
	
	/**
	 * Representa a razão entre o valor pago e o valorCBHPM(Porte do procedimento + UCO). Este é um campo calculado.
	 * porcetagemModeracao = (valormoderado / (valor + valoruco)) * 100 
	 */
	private BigDecimal porcentagemModeracao;
	private Porte porteAnestesico;
	private Porte porte;
	/**
	 * Valor do procedimento de acordo com a Tabela CBHPM (Classificação Brasileira Hierarquizada de Procedimentos Médicos).
	 */
	private BigDecimal valor;
	/**
	 * Valor do procedimento pago pelo Saúde Recife ao prestador.
	 */
	private BigDecimal valorModerado;
	/**
	 * Valor do procedimento considerando o valor cadastrado em tabela somado ao valorUCO. Este é um campo calculado.
	 * valorCBPHM = valor + valoruco 
	 */
	private BigDecimal valorCBHPM;
	private int grupo;
	
	private SubgrupoCBHPM subGrupoCBHPM;
	private int quantidadeDeAuxilios;
	/**
	 * Estrutura odontológica na qual o procedimento pode ser aplicado
	 */
	private EstruturaOdontoEnum elementoAplicado;
	private Set<CID> cids;
	private Set<Pacote> pacotes;
	private Set<AcordoCBHPM> acordos;
	
	private Set<TabelaCBHPM> procedimentosIncompativeis;
	/**
	 * Campo que representa o local permitido para a realização do procedimento.
	 * Será preenchido com os seguintes valores: 
	 * TIPO_AMBULATORIAL = 1;
	 * TIPO_HOSPITALAR = 2;
	 * TIPO_AMBOS = 3;  
	 */
	private Integer localDeAtendimento;
	private Set<TipoAcomodacao> tiposAcomodacao = new HashSet<TipoAcomodacao>();
	
	//Atributos de validação	
	private int sexo; 
	private int idadeMinima;
	private int idadeMaxima;
	
	//campo desmapeado
	private boolean especial;
	
	
	private boolean checarEspecialidade;
	private Set<Especialidade> especialidades;
	private boolean somenteDiretoria;
	/**
	 * Indica se o procedimento pode ser realizado em duas partes semelhantes do corpo (por exemplo: olho direito e esquerdo).
	 */
	private boolean bilateral;
	/**
	 * Indica se o procedimento só pode ser realizado uma única vez para cada segurado.
	 */
	private boolean unicidade;
	private boolean cirurgico;
	/**
	 * Intervalo de tempo, em dias, para a realização do procedimento.
	 */
	private int periodicidade;
	/**
	 * Lapso de tempo, após a adesão do plano, para a realização do procedimento.
	 */
	private int carencia;
	private int tempoPermanencia;
	private TipoCarencia tipoCarencia;
	private boolean verificaEspecialidade;
	private Visibilidade visibilidade;
	/**
	 * Indica a necessidade ou não de autorização pelo auditor da guia de internação de urgência que contém este procedimento.
	 * True significa que a guia não necessitará de autorização do mesmo.
	 * @see CID#isLiberadoParaUrgencia()
	 */
	private boolean permitePme;
	private boolean liberadoParaUrgencia;
	private int quantidade;
	private boolean permiteMaterialComplementar;
	private boolean permiteMedicamentoComplementar;
	private Boolean verificaPeriodicidadeNaInternacao;
	private Boolean exigePericia;
	private Set<Prestador> prestadores;
	private Integer tipo;
	private Integer categoria;

	/**
	 * Indica o nível de acesso ao procedimento nos fluxos do sistema.
	 * 1 = Total; 2 = Central; 3 = Prestador.
	 * @see Constantes#VISIBILIDADE_TOTAL
	 */
	 private Integer visibilidadeDoUsuario;

	/**
	 * Descricao do grupo SIP.
	 */
	private Integer tipoServico;
	
	/**
	 * Descrição do grupo SIP detalhado;
	 */
	private AreaEspecialidadeCBHPM areaEspecialidade;
	
	private Integer nivel;
	
	private boolean pagoNoPrestadorDaGuia;
	
	@SuppressWarnings("rawtypes")
	private static Collection<AbstractTabelaCBHPMValidator> tabelaCbhpmValidators = new ArrayList<AbstractTabelaCBHPMValidator>();
	static{
		tabelaCbhpmValidators.add(new TabelaCBHPMSimplesValidator());
	}
	
	public TabelaCBHPM(){
		this(null);
	}
	
	public TabelaCBHPM(UsuarioInterface usuario) {
		this.bilateral 	= false;
		this.setEspecial(false);
		this.setAtivo(true);
		this.cids 		= new HashSet<CID>();
		this.elementoAplicado = EstruturaOdontoEnum.NENHUM;
		this.procedimentosIncompativeis = new HashSet<TabelaCBHPM>();
		this.especialidades = new HashSet<Especialidade>();		
		this.pacotes 	= new HashSet<Pacote>();
		this.uco 		= BigDecimal.ZERO;
		this.nivel 		= 1;
	}
	
	public Integer getElementoAplicado() {
		return elementoAplicado.getValor();
	}
	
	public String getTipoEstrutura() {
		return elementoAplicado.getDescricao();
	}

	public void setElementoAplicado(Integer elementoAplicado) {
		for (EstruturaOdontoEnum e : EstruturaOdontoEnum.values()) {
			if(elementoAplicado.equals(e.getValor()))
				this.elementoAplicado = e;
		}
	}
	
	public Integer getVisibilidade() {
		if(visibilidade != null)
			return visibilidade.getValor();
		return null;
	}

	public void setVisibilidade(Integer visibilidade) {
		for (Visibilidade vis : Visibilidade.values()) {
			if(vis.getValor().equals(visibilidade))
				this.visibilidade = vis;
		}
	}

	public boolean getAtivo() {
		return ativo;
	}
	
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	public String getCodigo() {
		return codigo;
	}
	
	public void setCodigo(String codigo) {
		if(codigoInicial == null){
			codigoInicial = codigo.trim();
		}
		
		this.codigo = codigo.trim();
	}
	
	public String getCodigoEDescricao() {
		return codigoEDescricao;
	}
	
	public void setCodigoEDescricao(String codigoEDescricao) {
		this.codigoEDescricao = codigoEDescricao;
	}
	
	public String getCodigoSubGrupoDescricao() {
		return codigoSubGrupoDescricao;
	}
	
	public void setCodigoSubGrupoDescricao(String codigoSubGrupoDescricao) {
		this.codigoSubGrupoDescricao = codigoSubGrupoDescricao;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public String getDescricaoFormatado() {
		if(descricao.length() >= 45)
			return descricao.substring(0,45).concat("...");
		else
			return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public String getDescricaoGrupo() {
		return descricaoGrupo;
	}
	
	public void setDescricaoGrupo(String descricaoGrupo) {
		this.descricaoGrupo = descricaoGrupo;
	}
	
	public String getDescricaoSubGrupo() {
		return descricaoSubGrupo;
	}
	
	public void setDescricaoSubGrupo(String descricaoSubGrupo) {
		this.descricaoSubGrupo = descricaoSubGrupo;
	}
	
	public Long getIdTabelaCBHPM() {
		return idTabelaCBHPM;
	}
	
	public void setIdTabelaCBHPM(Long idTabelaCBHPM) {
		this.idTabelaCBHPM = idTabelaCBHPM;
	}
	
	public Porte getPorte() {
		return porte;
	}
	
	public void setPorte(Porte porte) {
		this.porte = porte;
	}	
	
	public BigDecimal getValorUco() {
		this.valorUco = constanteUco.multiply(this.uco);
		return this.valorUco;
	}
	
	public void setValorUco(BigDecimal valorUco) {
		this.valorUco = valorUco;
	}
	
	public boolean getBilateral() {
		return bilateral;
	}
	
	public void setBilateral(boolean bilateral) {
		this.bilateral = bilateral;
	}

	public boolean getEspecial() {
		if(this.nivel >= NIVEL_2)
			return true;
		else
			return false;
	}
	
	public void setEspecial(boolean especial) {
		this.especial = especial;
	}
	
	public int getIdadeMaxima() {
		return idadeMaxima;
	}
	
	public void setIdadeMaxima(int idadeMaxima) {
		this.idadeMaxima = idadeMaxima;
	}
	
	public int getIdadeMinima() {
		return idadeMinima;
	}
	
	public void setIdadeMinima(int idadeMinima) {
		this.idadeMinima = idadeMinima;
	}
	
	public Float getModeracao() {
		return moderacao;
	}
	
	public void setModeracao(Float moderacao) {
		this.moderacao = moderacao;
	}
	
	public boolean getVerificaEspecialidade() {
		return verificaEspecialidade;
	}
	
	public void setVerificaEspecialidade(boolean verificaEspecialidade) {
		this.verificaEspecialidade = verificaEspecialidade;
	}
	
	public int getCarencia() {
		return carencia;
	}
	
	public void setCarencia(int carencia) {
		this.carencia = carencia;
	}
	
	public int getPeriodicidade() {
		return periodicidade;
	}
	
	public void setPeriodicidade(int periodicidade) {
		this.periodicidade = periodicidade;
	}
	
	public int getSexo() {
		return sexo;
	}
	
	public void setSexo(int sexo) {
		this.sexo = sexo;
	}
	
	public boolean getUnicidade() {
		return unicidade;
	}
	
	public void setUnicidade(boolean unicidade) {
		this.unicidade = unicidade;
	}
	
	public Porte getPorteAnestesico() {
		return porteAnestesico;
	}
	
	public void setPorteAnestesico(Porte porte) {
		this.porteAnestesico = porte;
	}

	public BigDecimal getUco() {
		return uco;
	}

	public void setUco(BigDecimal uco) {
		this.uco = uco;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public BigDecimal getValorModerado() { //valor do porte
		if(PainelDeControle.getPainel().isProcedimentoValoradoPeloPorte()){
		    /* if[VAIDAR_VALOR_PORTE_NULL]
		    Assert.isNotNull(this.getPorte(), "Valor do porte deve ser informado para o procedimento \"" + this.getCodigoEDescricao()+"\"");
			else[VAIDAR_VALOR_PORTE_NULL] */
			if (this.getPorte() == null) return BigDecimal.ZERO;
		    /* end[VAIDAR_VALOR_PORTE_NULL]*/
			
			return new BigDecimal(this.getPorte().getValorPorte()); //porte deve ser não-nulo
		}
		return valorModerado;
	}

	public void setValorModerado(BigDecimal valorModerado) {
		this.valorModerado = valorModerado;
	}
	
	/**
	 * 
	 * @param usuario
	 * @return Boolean
	 * @throws ValidateException
	 * Neste método, além da validação, manipula-se a situação do objeto caso ele esteja sendo cadastrado. Se tiver sido marcado como ativo, 
	 * setamos sua situação como 'Ativo(a)', caso contrário setamos a situação como 'Inativo(a)'.
	 */
	public Boolean validate(UsuarioInterface usuario) throws ValidateException {
		
		codigoEDescricao = this.getCodigo() +" - "+  this.getDescricao();
		
		for (AbstractTabelaCBHPMValidator validator : tabelaCbhpmValidators) {
			validator.execute(this);
		}
		templateValidator();
		Assert.isEquals(codigoInicial, codigo, MensagemErroEnum.CAMPO_NAO_PODE_SER_ALTERADO.getMessage("CÓDIGO"));
		
		if (this.getSituacao()==null){
			if (this.getAtivo())
				mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(), "Cadastro de novo procedimento", new Date());
			else
				mudarSituacao(usuario, SituacaoEnum.INATIVO.descricao(), "Cadastro de novo procedimento", new Date());
		}
		
		this.setGrupo(Integer.valueOf(this.getCodigo().trim().substring(0, 1)));
		
		if(this.hasSalvoComMesmoCodigo()) {
			throw new ValidateException("Já existe um procedimento cadastrado com o mesmo código.");
		}
		
		atualizaValor();
		
		return true;
	}
	
	/**
	 * Método responsável por setar o campo {@link #valor} conforme o valor do {@link #porte}
	 * Seta o valor apenas se o este for nulo, caso contrário ele não sofre alteração
	 */
	private void atualizaValor() {
		if (this.valor == null) {
			if (this.porte != null)
				this.setValor(new BigDecimal(this.getPorte().getValorPorte()));
			else
				this.setValor(BigDecimal.ZERO);
		}
	}

	public int getTipoCarencia() {
		return tipoCarencia.getTipo();
	}
	
	public TipoCarencia getTipoCarenciaEnum() {
		return tipoCarencia;
	}

	public void setTipoCarencia(int tipoCarencia) {
		for (TipoCarencia tipo : TipoCarencia.values()){
			if (tipo.getTipo() == tipoCarencia)
				this.tipoCarencia = tipo;
		}
	}
	
	/**
	 * Template Method que deve ser sobrescrito nas subclasses para validações específicas
	 * @return
	 * @throws ValidateException
	 */
	public Boolean templateValidator()throws ValidateException {
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof TabelaCBHPM))
			return false;
		
		TabelaCBHPM tabela = (TabelaCBHPM) obj;
		return new EqualsBuilder()
		.append(this.getCodigo(), tabela.getCodigo())
		.append(this.getDescricao(), tabela.getDescricao())
		.append(this.getElementoAplicado(), tabela.getElementoAplicado())
		.isEquals();
		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(this.codigo)
				.append(this.descricao)
				.append(this.elementoAplicado)
				.toHashCode();
	}
	public boolean getChecarEspecialidade() {
		return checarEspecialidade;
	}

	public void setChecarEspecialidade(boolean checarEspecialidade) {
		this.checarEspecialidade = checarEspecialidade;
	}

	public Set<Especialidade> getEspecialidades() {
		return especialidades;
	}

	public void setEspecialidades(Set<Especialidade> especialidades) {
		this.especialidades = especialidades;
	}
	
	public void setSomenteDiretoria(boolean somenteDiretoria) {
		this.somenteDiretoria = somenteDiretoria;
	}
	
	public boolean isProcedimentoCirurgico(){
		if((this.tipo.equals(TabelaCBHPM.TIPO_AMBOS)) || (this.tipo.equals(TabelaCBHPM.TIPO_HOSPITALAR)))
			return true;
		else return false;
	}
	
	public boolean isExame(){
		if((this.tipo.equals(TabelaCBHPM.TIPO_AMBOS)) || (this.tipo.equals(TabelaCBHPM.TIPO_AMBULATORIAL)))
			return true;
		else return false;
	}
	
	public boolean isSomenteDiretoria() {
		return somenteDiretoria;
	}
	
	public void tocarObjetos(){
		this.getAtivo();
		this.getBilateral();
		this.getCarencia();
		this.getProcedimentosIncompativeis().size();
		this.getChecarEspecialidade();
		this.getCodigo();
		this.getCodigoEDescricao();
		this.getCodigoSubGrupoDescricao();
		this.getEspecial();
		this.getEspecialidades();
		this.getIdadeMaxima();
		this.getIdadeMinima();
		this.getPeriodicidade();
		this.getSexo();
		this.getUnicidade();
		this.getPorte();
		this.getPorteAnestesico();
		
		
		if(this.getPorte() != null)
			this.getPorte().getValorPorte();
		
		if(this.getPorteAnestesico() != null)
			this.getPorteAnestesico().getValorPorte();
	}

	public Set<CID> getCids() {
		return cids;
	}

	public void setCids(Set<CID> cids) {
		this.cids = cids;
	}

	public int getGrupo() {
		return grupo;
	}

	public void setGrupo(int grupo) {
		this.grupo = grupo;
	}

	public void setLiberadoParaUrgencia(boolean liberadoParaUrgencia) {
		this.liberadoParaUrgencia = liberadoParaUrgencia;
	}
	
	public boolean getLiberadoParaUrgencia() {
		return this.liberadoParaUrgencia;
	}

	public boolean isPermiteMaterialComplementar() {
		return permiteMaterialComplementar;
	}

	public void setPermiteMaterialComplementar(boolean permiteMaterialComplementar) {
		this.permiteMaterialComplementar = permiteMaterialComplementar;
	}

	public boolean isPermiteMedicamentoComplementar() {
		return permiteMedicamentoComplementar;
	}

	public void setPermiteMedicamentoComplementar(
			boolean permiteMedicamentoComplementar) {
		this.permiteMedicamentoComplementar = permiteMedicamentoComplementar;
	}

	public Set<Pacote> getPacotes() {
		return pacotes;
	}

	public void setPacotes(Set<Pacote> pacotes) {
		this.pacotes = pacotes;
	}
	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}
	
	public boolean getCirurgico() {
		return cirurgico;
	}
	
	public void setCirurgico(boolean cirurgico) {
		this.cirurgico = cirurgico;
	}

	public Boolean getVerificaPeriodicidadeNaInternacao() {
		return verificaPeriodicidadeNaInternacao;
	}

	public void setVerificaPeriodicidadeNaInternacao(
			Boolean verificaPeriodicidadeNaInternacao) {
		this.verificaPeriodicidadeNaInternacao = verificaPeriodicidadeNaInternacao;
	}

	public Set<Prestador> getPrestadores() {
		return prestadores;
	}

	public void setPrestadores(Set<Prestador> prestadores) {
		this.prestadores = prestadores;
	}
	
	public Integer getPorteAnestesicoFormatado(){
		if(getPorteAnestesico() == null)
			return 0;
		if(getPorteAnestesico().getDescricao().equals(DESCRICAO_PORTE_ANESTESICO_1))
			return 1;
		if(getPorteAnestesico().getDescricao().equals(DESCRICAO_PORTE_ANESTESICO_2))
			return 2;
		if(getPorteAnestesico().getDescricao().equals(DESCRICAO_PORTE_ANESTESICO_3))
			return 3;
		if(getPorteAnestesico().getDescricao().equals(DESCRICAO_PORTE_ANESTESICO_4))
			return 4;
		if(getPorteAnestesico().getDescricao().equals(DESCRICAO_PORTE_ANESTESICO_5))
			return 5;
		if(getPorteAnestesico().getDescricao().equals(DESCRICAO_PORTE_ANESTESICO_6))
			return 6;
		if(getPorteAnestesico().getDescricao().equals(DESCRICAO_PORTE_ANESTESICO_7))
			return 7;
		if(getPorteAnestesico().getDescricao().equals(DESCRICAO_PORTE_ANESTESICO_8))
			return 8;
		
		return 0;
	}
	
	public void setPorteAnestesicoFormatado(Integer porte){
		String descricaoPorte = null;
		if(porte.equals(1))
			descricaoPorte = DESCRICAO_PORTE_ANESTESICO_1;
		else if(porte.equals(2))
			descricaoPorte = DESCRICAO_PORTE_ANESTESICO_2;
		else if(porte.equals(3))
			descricaoPorte = DESCRICAO_PORTE_ANESTESICO_3;
		else if(porte.equals(4))
			descricaoPorte = DESCRICAO_PORTE_ANESTESICO_4;
		else if(porte.equals(5))
			descricaoPorte = DESCRICAO_PORTE_ANESTESICO_5;
		else if(porte.equals(6))
			descricaoPorte = DESCRICAO_PORTE_ANESTESICO_6;
		else if(porte.equals(7))
			descricaoPorte = DESCRICAO_PORTE_ANESTESICO_7;
		else if(porte.equals(8))
			descricaoPorte = DESCRICAO_PORTE_ANESTESICO_8;
		
		if(descricaoPorte == null)
			setPorteAnestesico(null);
		else{
			SearchAgent sa  = new SearchAgent();
			sa.addParameter(new Equals("descricao",descricaoPorte));
			setPorteAnestesico(sa.uniqueResult(Porte.class));
		}
		
	}

	public Integer getTipo() {
		return tipo;
	}

	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}

	public Integer getCategoria() {
		return categoria;
	}

	public void setCategoria(Integer categoria) {
		this.categoria = categoria;
	}

	public Integer getNivel() {
		return nivel;
	}

	public void setNivel(Integer nivel) {
		this.nivel = nivel;
	}

	public Set<AcordoCBHPM> getAcordos() {
		return acordos;
	}

	public void setAcordos(Set<AcordoCBHPM> acordos) {
		this.acordos = acordos;
	}
	
	/**
	 * 
	 * @return retorna um Set de prestadores que possuem esse procedimento em um acordo com coParticipação
	 * liberada
	 */
	public Set<Prestador> getPrestadoresCoParticipacaoLiberada(){
		Set<Prestador> prestadores = new HashSet<Prestador>();
		for (AcordoCBHPM acordo : acordos) {
			if(acordo.isLiberarCoParticipacao())
				prestadores.add(acordo.getPrestador());
		}
		return prestadores;
	}

	public Integer getTipoServico() {
		return tipoServico;
	}
	
	public void setTipoServico(Integer tipoServico) {
		this.tipoServico = tipoServico;
	}

	public AreaEspecialidadeCBHPM getAreaEspecialidade() {
		return areaEspecialidade;
	}

	public void setAreaEspecialidade(AreaEspecialidadeCBHPM areaEspecialidade) {
		this.areaEspecialidade = areaEspecialidade;
	}
	
	/**
	 * @param motivo
	 * @param usuario
	 * @throws Exception
	 * Método responsável por mudar a situação do Procedimento para 'Inativo(a)' no seu cadastro.
	 */
	public void inativar(String motivo, UsuarioInterface usuario) throws Exception {
		if (isSituacaoAtual(SituacaoEnum.INATIVO.descricao()))
			throw new Exception("Já se encontra inativado!");
		if (Utils.isStringVazia(motivo))
			throw new Exception("O motivo deve ser preenchido!");
		
		mudarSituacao(usuario, SituacaoEnum.INATIVO.descricao(), motivo, new Date());
	}
	
	public Set<TabelaCBHPM> getProcedimentosIncompativeis() {
		return procedimentosIncompativeis;
	}

	public void setProcedimentosIncompativeis(
			Set<TabelaCBHPM> procedimentosIncompativeis) {
		this.procedimentosIncompativeis = procedimentosIncompativeis;
	}
	
	/**
	 * @param motivo
	 * @param usuario
	 * @throws Exception
	 * Método responsável por mudar a situação da Procedimento para 'Ativo(a)' no seu cadastro.
	 */
	public void reativar(String motivo, UsuarioInterface usuario) throws Exception {
		if (isSituacaoAtual(SituacaoEnum.ATIVO.descricao()))
			throw new Exception("Já se encontra ativado!");
		if (Utils.isStringVazia(motivo))
			throw new Exception("O motivo deve ser preenchido!");
		
		mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(), motivo, new Date());
	}

	public Integer getVisibilidadeDoUsuario() {
		return visibilidadeDoUsuario;
	}

	public void setVisibilidadeDoUsuario(Integer visibilidadeDoUsuario) {
		this.visibilidadeDoUsuario = visibilidadeDoUsuario;
	}
	
	public Boolean getExigePericia() {
		return exigePericia;
	}

	public void setExigePericia(Boolean exigePericia) {
		this.exigePericia = exigePericia;
	}
	
	public int getTempoPermanencia() {
		return tempoPermanencia;
	}

	public void setTempoPermanencia(int tempoPermanencia) {
		this.tempoPermanencia = tempoPermanencia;
	}
	
    public boolean hasSalvoComMesmoCodigo() {
    	Criteria crit = HibernateUtil.currentSession().createCriteria(TabelaCBHPM.class);
        ProjectionList proList = Projections.projectionList();
        proList.add(Projections.property("codigo"));
        crit.setProjection(proList);
        
        SearchAgent agent = new SearchAgent();
        TabelaCBHPM tabelaDoBanco;
        List list = crit.list();
    	
    	for (Object tabelaCBHPM : list) {
			if(tabelaCBHPM.equals(this.getCodigo())) {
				agent.addParameter(new Equals("codigo",tabelaCBHPM));
				agent.addParameter(new Equals("situacao.descricao",SituacaoEnum.ATIVO.descricao()));
				tabelaDoBanco = agent.uniqueResult(TabelaCBHPM.class);
				if (tabelaDoBanco != null && !tabelaDoBanco.equals(this)) {
					return true;
				}
			}
		}
		return false;
	}
    
    public int getQuantidadeDeAuxilios() {
		return quantidadeDeAuxilios;
	}

	public void setQuantidadeDeAuxilios(int quantidadeDeAuxilios) {
		this.quantidadeDeAuxilios = quantidadeDeAuxilios;
	}
    
//	public Integer getLocalDeAtendimento() {
//		return localDeAtendimento;
//	}

//	public void setLocalDeAtendimento(Integer locaisDeAtendimento) {
//		this.localDeAtendimento = locaisDeAtendimento;
//	}
    
	public Set<TipoAcomodacao> getTiposAcomodacao() {
		return tiposAcomodacao;
	}

	public void setTiposAcomodacao(Set<TipoAcomodacao> tiposAcomodacao) {
		this.tiposAcomodacao = tiposAcomodacao;
	}

	public boolean isPermitePme() {
		return permitePme;
	}

	public void setPermitePme(boolean permitePme) {
		this.permitePme = permitePme;
	}
	
	public boolean isIncompativel(TabelaCBHPM tabela) {
		if(this.procedimentosIncompativeis != null && !this.procedimentosIncompativeis.isEmpty()) {
			if(this.getProcedimentosIncompativeis().contains(tabela)) {
				return true;
			}
		}
		return false;
	}
	
	public BigDecimal getValorCBHPM() {
		this.valorCBHPM = this.getValorUco().add(this.valor);
		
		return this.valorCBHPM;
	}

	public BigDecimal getPorcentagemModeracao() {
		this.porcentagemModeracao = BigDecimal.ZERO;
		BigDecimal valorCBPM = this.getValorCBHPM();
		
		if(MoneyCalculation.compare(valorCBPM, BigDecimal.ZERO) > 0){
			this.porcentagemModeracao =  this.valorModerado.divide(valorCBPM, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
		}
		
		return this.porcentagemModeracao;
	}
	
    /**
     * @return <ul> 
     * 		<li> <code>true</code>, se o valor dos honorarios é pago no mesmo prestador da guia; </li> 
     * 		<li> <code>false</code>, se o valor dos honorarios é pago no prestador destino do profissional;</li>
     * 	</ul>
     */
	public boolean isPagoNoPrestadorDaGuia() {
		return this.pagoNoPrestadorDaGuia;
	}

	public void setPagoNoPrestadorDaGuia(boolean pagoNoPrestadorDaGuia) {
		this.pagoNoPrestadorDaGuia = pagoNoPrestadorDaGuia;
    }
	
	public SubgrupoCBHPM getSubgrupoCBHPM() {
		return subGrupoCBHPM;
	}

	public void setSubgrupoCBHPM(SubgrupoCBHPM subgrupo) {
		this.subGrupoCBHPM = subgrupo;
	}
	
	public GrupoCBHPM getGrupoCBHPM() {
		if (this.subGrupoCBHPM != null) {
			return this.subGrupoCBHPM.getGrupoCBHPM();
		}
		return null;
	}
	
	public CapituloCBHPM getCapituloCBHPM() {
		if (this.getGrupoCBHPM() != null) {
			return this.getGrupoCBHPM().getCapituloCBHPM();
		}
		return null;
	}
   
}
