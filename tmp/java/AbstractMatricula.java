package br.com.infowaypi.ecare.segurados;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.FlushMode;

import br.com.infowaypi.ecare.constantes.Constantes;
import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.financeiro.DetalhePagamentoInterface;
import br.com.infowaypi.ecare.financeiro.consignacao.ConsignacaoMatricula;
import br.com.infowaypi.ecare.financeiro.consignacao.ConsignacaoMatriculaInterface;
import br.com.infowaypi.ecare.financeiro.conta.ContaMatricula;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.Role;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.DependenteFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.FluxoFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.TitularFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.consignacao.Consignacao;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.financeiro.InformacaoFinanceiraInterface;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

@SuppressWarnings({"serial","rawtypes"})
public class AbstractMatricula extends ImplColecaoSituacoesComponent implements TitularFinanceiroInterface {

	public static final int FORMA_PAGAMENTO_FOLHA = Constantes.FOLHA;
	public static final int FORMA_PAGAMENTO_BOLETO = Constantes.BOLETO;
	public static final int FORMA_PAGAMENTO_DEBITO_EM_CONTA = Constantes.CONTA_CORRENTE;
	private Long idMatricula;
	private TitularFinanceiroSR segurado;
	/**
	 * Órgão que contém essa matrícula.
	 */
	private Empresa empresa;
	/**
	 * É a matrícula do segurado no órgão em que trabalha.
	 */
	private String descricao;
	/**
	 * Número que indica o "índice" da matrícula do segurado dentro do seu conjunto de matrículas, 
	 * e serve para indicar qual delas sofrerá desconto primeiro.
	 */
	private int ordem;
	/**
	 * É o valor do salário correspondente a essa matrícula.
	 */
	private BigDecimal salario;
	/**
	 * Forma de pagamento para a respectiva matricula.
	 * Pode ser em follha {@link DetalhePagamentoInterface#FOLHA} ou por boleto {@link DetalhePagamentoInterface#BOLETO}
	 */
	private Integer tipoPagamento;
	protected Set<Consignacao> consignacoes;
	protected Set<ContaMatricula> contasMatriculas;
	/**
	 * Indica se a matrícula se encontra ativa no sistema.
	 */
	private boolean ativa;

	public AbstractMatricula() {
		super();
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Long getIdMatricula() {
		return idMatricula;
	}

	public void setIdMatricula(Long id) {
		this.idMatricula = id;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public TitularFinanceiroSR getTitular() {
		return segurado;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

	public Set<Consignacao> getConsignacoes() {
		return consignacoes;
	}

	public void setConsignacoes(Set<Consignacao> consignacoes) {
		this.consignacoes = consignacoes;
	}

	private boolean isMatriculaDuplicada() {
		Boolean isObjetoPersistido = this.getIdMatricula() != null;
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("empresa",this.getEmpresa()));
		sa.addParameter(new Equals("descricao",this.getDescricao()));
		Boolean isMatriculaExisteNoBanco = sa.resultCount(Matricula.class) > 0; 
		return !isObjetoPersistido && isMatriculaExisteNoBanco;
	}

	public Boolean validate(UsuarioInterface usuario) throws Exception {
			HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
			if(segurado.isSituacaoAtual(SituacaoEnum.CADASTRADO.descricao())){
				segurado.mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(),MotivoEnum.CADASTRO_NOVO.getMessage(), new Date());
			}
			// VERIFICA DUPLICIDADE DE MATRICULA
			if(this.getIdMatricula() == null){
				Assert.isFalse(this.isMatriculaDuplicada(), MensagemErroEnumSR.MATRICULA_DUPLICADA.getMessage());
				ordem = segurado.getMatriculas().size();
			}
			
			try {
				ImplDAO.save(this);
				if(segurado.getNumeroDoCartao() == null){
					segurado.gerarCartao();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(MensagemErroEnum.ERRO_AO_GERAR_CARTAO.getMessage());
			}
			
			segurado.criarUsuarioSegurado(Role.TITULAR.getValor());
			
			return true;
		}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof AbstractMatricula))
			return false;
		
		AbstractMatricula matricula = (AbstractMatricula) obj;
		return new EqualsBuilder()
		.append(this.getEmpresa(), matricula.getEmpresa())
		.append(this.getDescricao(), matricula.getDescricao())
		.append(this.getSalario(), matricula.getSalario())
		.isEquals();
		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(1308791639, 309108973).appendSuper(
				super.hashCode()).append(this.getEmpresa())
								 .append(this.getDescricao())
								 .append(this.getSalario())
								 .toHashCode();
	}

	public BigDecimal getSalario() {
		return salario;
	}

	public void setSalario(BigDecimal salario) {
		this.salario = salario;
	}

	public Integer getTipoPagamento() {
		return tipoPagamento;
	}

	public void setTipoPagamento(Integer tipoPagamento) {
		this.tipoPagamento = tipoPagamento;
	}

	public TitularFinanceiroSR getSegurado() {
		return segurado;
	}

	public void setSegurado(TitularFinanceiroSR segurado) {
		this.segurado = segurado;
	}

	public Set<DependenteFinanceiroInterface> getDependentesFinanceiro() {
		return getTitular().getDependentesFinanceiro();
	}

	public Set<FluxoFinanceiroInterface> getFluxosFinanceiros() {
		Set<FluxoFinanceiroInterface> fluxosFinanceiros = new HashSet<FluxoFinanceiroInterface>();
		for(Consignacao consignacao : this.getConsignacoes())
			fluxosFinanceiros.add(consignacao);
		return fluxosFinanceiros;
	}

	public InformacaoFinanceiraInterface getInformacaoFinanceira() {
		return getTitular().getInformacaoFinanceira();
	}

	@Override
	public String toString() {
			return new ToStringBuilder(this,ToStringStyle.DEFAULT_STYLE)
			.append("matricula", this.getDescricao())
			.append("empresa", this.empresa.getCodigoLegado())
	//		.append("banco", this.getDetalhePagamento().getBanco().getDescricao())
			.toString();
		}

	public String getIdentificacao() {
		return this.getDescricao();
	}

	public String getNome() {
		return this.getSegurado().getPessoaFisica().getNome();
	}

	public String getTipo() {
		return "Matrícula";
	}

	public Set<ContaMatricula> getContasMatriculas() {
		return contasMatriculas;
	}

	public Set<ContaMatricula> getContasMatriculas(Date competencia) {
		Set<ContaMatricula> contasMatriculas = new HashSet<ContaMatricula>();
		for (ContaMatricula contaMatricula : this.getContasMatriculas()) {
			if(Utils.compararCompetencia(competencia, contaMatricula.getCompetencia()) == 0 && contaMatricula.isSituacaoAtual(SituacaoEnum.ABERTO.descricao())){
				contasMatriculas.add(contaMatricula);
			}
		}
		return contasMatriculas;
	}

	public BigDecimal getValorContasMatriculas(Date competencia) {
		BigDecimal valor = BigDecimal.ZERO;
		Set<ContaMatricula> contasMatriculas = this.getContasMatriculas(competencia);
		for (ContaMatricula contaMatricula : contasMatriculas) {
			valor = valor.add(contaMatricula.getValorTotal());
		}
		return valor;
	}

	public void setContasMatriculas(Set<ContaMatricula> contasMatriculas) {
		this.contasMatriculas = contasMatriculas;
	}

	public ConsignacaoMatriculaInterface getConsignacao(Date competencia) {
		for(Consignacao<Matricula> consignacao : this.getConsignacoes()){
			if(consignacao.getCompetencia().compareTo(competencia) == 0)
				return (ConsignacaoMatricula) consignacao;
		}
		return null;
	}

	public boolean isAtiva() {
		return ativa;
	}

	public void setAtiva(boolean matriculaAtiva) {
		this.ativa = matriculaAtiva;
	}

}