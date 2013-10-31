package br.com.infowaypi.ecare.financeiro.conta;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.com.infowaypi.ecare.constantes.Constantes;
import br.com.infowaypi.ecare.segurados.AbstractMatricula;
import br.com.infowaypi.ecare.segurados.Cartao;
import br.com.infowaypi.ecare.segurados.DependenteSR;
import br.com.infowaypi.ecare.segurados.Matricula;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.consignacao.Consignacao;
import br.com.infowaypi.ecarebc.painelDeControle.PainelDeControle;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * @author Marcus bOolean / Diogo Vinícius
 */
@SuppressWarnings("unchecked")
public class ContaMatricula extends ImplColecaoSituacoesComponent {
	
	private static final long serialVersionUID = 1L;
	private long idContaMatricula;
	private AbstractMatricula matricula;
	private BigDecimal valorFinanciamento;
	private BigDecimal valorCoParticipacoes;
	private BigDecimal valorSegundaViaCartao;
	private Set<GuiaSimples> guias;
	private Set<Cartao> cartoes;
	private Date competencia;

	private BigDecimal salario_base_limite = BigDecimal.ZERO;
	private BigDecimal aliquota_financiamento = BigDecimal.ZERO;
	private BigDecimal limite_Financiamento = BigDecimal.ZERO;
	
	public ContaMatricula(){
		this.cartoes = new HashSet<Cartao>();
		this.guias = new HashSet<GuiaSimples>();
	}
	
	public ContaMatricula(AbstractMatricula matricula,Date competencia, UsuarioInterface usuario, Date dataVencimento) {
		this();
		this.matricula = matricula;
		this.matricula.getContasMatriculas().add(this);
		this.competencia = competencia;
		this.valorFinanciamento = calculaValorFinanciamento(dataVencimento);
		this.valorSegundaViaCartao = calculaValorSegundaViaCartao();
		mudarSituacao(usuario, SituacaoEnum.ABERTO.descricao(), "Geração de cobranca", new Date());
	}
	
	public ContaMatricula(AbstractMatricula matricula,Date competencia,Map<AbstractSegurado, Set<GuiaSimples>> mapGuiasParaCobranca, UsuarioInterface usuario, Date dataVencimento) {
		this(matricula, competencia, usuario, dataVencimento);
		this.valorCoParticipacoes = calculaValorCoParticipacoes(mapGuiasParaCobranca);
	}

	/**
	 * Método que calcula o valor total da ContaMatrícula
	 * @return um BigDecimal com soma de valorFianciamento + valorCoParticipacoes + ValorCartao
	 */
	public BigDecimal getValorTotal() {
		BigDecimal valorTotal = BigDecimal.ZERO;
		valorTotal = valorTotal.add(this.getValorFinanciamento());
		valorTotal = valorTotal.add(this.getValorCoParticipacoes());
		valorTotal = valorTotal.add(this.getValorSegundaViaCartao());
		return valorTotal.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * Verifica se as consignações da competência já não atingiram o limite de financiamento. 
	 * Calcula o valor da conta. <br>
	 * Verifica se a soma da conta + consignações > limite. <br>
	 * Se sim: calcular a diferença restante para atingir o limite e gerar a conta com este valor. <br> 
	 * Se não: criar conta. <br>
	 * Este método não gera valor de financiamento caso o titular esteja cancelado 
	 * @param dataVencimento 
	 * @return o valor para gerar a conta.
	 */
	private BigDecimal calculaValorFinanciamento(Date dataVencimento) {
		if (this.matricula.getTitular().isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())){
			return BigDecimal.ZERO;
		}
		
		Consignacao consignacao 	= this.getMatricula().getSegurado().getConsignacao(this.getCompetencia());
		BigDecimal valorConsignacao = BigDecimal.ZERO;
		BigDecimal valor 			= BigDecimal.ZERO;
		
		salario_base_limite = PainelDeControle.getPainel().getSalarioBaseLimite();
		aliquota_financiamento = PainelDeControle.getPainel().getAliquotaDeFinanciamento();
		limite_Financiamento = salario_base_limite.multiply(aliquota_financiamento.divide(new BigDecimal(100))).setScale(2, BigDecimal.ROUND_HALF_UP);

		if (consignacao != null) {
			valorConsignacao = consignacao.getValorFinanciamento();
		}
		
		boolean isValorConsignacaoSuperaLimiteFinanciamento = valorConsignacao.compareTo(limite_Financiamento) < 0;
		if (isValorConsignacaoSuperaLimiteFinanciamento) {
			BigDecimal salarioBase = this.getMatricula().getSalario(); 
			BigDecimal[] valorFinanciamento = {BigDecimal.ZERO, BigDecimal.ZERO};
			valorFinanciamento = matricula.getTitular().getValorTotalFinanciamento(salarioBase, dataVencimento);
			valor = valorFinanciamento[1];
			detalharValorFinanciamento(salarioBase, dataVencimento);

			boolean isValorConsignacaoMaisValorBoletoSuperaLimiteFinanciamento = (valor.add(valorConsignacao)).compareTo(limite_Financiamento) > 0;
			
			if (isValorConsignacaoMaisValorBoletoSuperaLimiteFinanciamento) {
				return limite_Financiamento.subtract(valorConsignacao).setScale(2, BigDecimal.ROUND_HALF_UP);
			} else {
				return valor.setScale(2, BigDecimal.ROUND_HALF_UP);
			}
		}
		
		return valor.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * @param salarioBase
	 * @param dataVencimento 
	 */
	private void detalharValorFinanciamento(BigDecimal salarioBase, Date dataVencimento) {
		//detalhando o financiamento do titular
		DetalheContaTitular detalheContaTitular = matricula.getSegurado().getDetalheContaTitular(this.competencia);
		BigDecimal[] valorTitular = matricula.getSegurado().getValorFinanciamento(salarioBase);
		detalheContaTitular.setValorFinanciamento(detalheContaTitular.getValorFinanciamento().add(valorTitular[1]));
		//detalhando o financiamento dos dependentes
		DetalheContaSegurado detalheContaDependente; 
		for (DependenteSR dependente : matricula.getSegurado().getDependentesCobranca(this.competencia)) {
			detalheContaDependente = dependente.getDetalheContaDependente(this.competencia);
			BigDecimal[] valorDependente = dependente.getValorFinanciamentoDependente(salarioBase, dataVencimento);
			detalheContaDependente.setValorFinanciamento(detalheContaDependente.getValorFinanciamento().add(valorDependente[1]));
		}
	}
	
	/**
	 * Metódo reponsável por calcular a co-particiapação total do segurado.
	 * Pega-se a matrícula de maior salário do titular e compara com o atributo
	 * <code>matricula</code> da classe ContaMatricula. 
	 * Se as matrículas forem iguais, a coparticipação é calculada tendo como base
	 * todas as guias do titular mais seus dependentes dentro da competência, senão
	 * é retornado o valor 0 (zero).
	 * @return o valor total de co-participações do segurado possuidor dessa matricula
	 */
	public BigDecimal calculaValorCoParticipacoes(Map<AbstractSegurado, Set<GuiaSimples>> mapGuiasParaCobranca) {
		
		AbstractMatricula matriculaDeMaiorSalario = this.matricula.getTitular().getMatriculaMaiorSalario(Matricula.FORMA_PAGAMENTO_BOLETO);
		BigDecimal valor = BigDecimal.ZERO;
		boolean isMaiorSalario;
		if (matriculaDeMaiorSalario == null) {
			isMaiorSalario = false;
		}
		else {
			isMaiorSalario = matriculaDeMaiorSalario.equals(this.matricula);
		}
		
		GregorianCalendar ultimoDiaDaCompetencia = new GregorianCalendar();
		ultimoDiaDaCompetencia.setTime(this.competencia);
		ultimoDiaDaCompetencia.set(Calendar.DAY_OF_MONTH, ultimoDiaDaCompetencia.getActualMaximum(Calendar.DATE));
		
		if (isMaiorSalario) {

			//coparticipação do titular
			DetalheContaTitular detalheContaTitular = matricula.getSegurado().getDetalheContaTitular(this.competencia);
			if(mapGuiasParaCobranca.get(matricula.getSegurado()) != null){
				for (GuiaSimples guia : mapGuiasParaCobranca.get(matricula.getSegurado())) {
					this.guias.add(guia);
					detalheContaTitular.getGuias().add(guia);
					detalheContaTitular.setValorCoparticipacao(detalheContaTitular.getValorCoparticipacao().add(guia.getValorCoParticipacao()));
				}
			}
			detalheContaTitular.setValorCoparticipacao(verificaTetoCoparticipacao(detalheContaTitular.getValorCoparticipacao()));
			valor = valor.add(detalheContaTitular.getValorCoparticipacao());
			//coparticipação dos dependentes
			for (DependenteSR dependente : matricula.getTitular().getDependentes(SituacaoEnum.ATIVO.descricao())) {
				DetalheContaSegurado detalheContaDependente = dependente.getDetalheContaDependente(this.competencia);
				
				if(mapGuiasParaCobranca.get(dependente) != null){
					for (GuiaSimples guia : mapGuiasParaCobranca.get(dependente)) {
						this.guias.add(guia);
						detalheContaDependente.getGuias().add(guia);
						detalheContaDependente.setValorCoparticipacao(detalheContaDependente.getValorCoparticipacao().add(guia.getValorCoParticipacao()));
					}
				}
				detalheContaDependente.setValorCoparticipacao(verificaTetoCoparticipacao(detalheContaDependente.getValorCoparticipacao()));
				valor = valor.add(detalheContaDependente.getValorCoparticipacao());
			}

		}
		
		return valor;
	}

	/**
	 * Verifica o Teto da Coparticipação do Segurado para retornar o valor respeitando este teto
	 * @param valor
	 * @return valor de coparticipação obedecendo o teto definido
	 */
	public static BigDecimal verificaTetoCoparticipacao(BigDecimal valor) {
		if(valor.compareTo(Constantes.TETO_COPARTICIPACAO) > 0){
			return Constantes.TETO_COPARTICIPACAO.setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		return valor.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * Este Método verifica se o Beneficiário solicitou <br>
	 * alguma segunda via de cartão para que este seja cobrado <br>
	 * @return um BigDecimal com o valor da segunda via do cartão
	 */
	private BigDecimal calculaValorSegundaViaCartao() {
		BigDecimal valorTotalCartoes = BigDecimal.ZERO;
		//cartões do titular
		DetalheContaTitular detalheContaTitular = matricula.getTitular().getDetalheContaTitular(this.competencia);
		BigDecimal valorCartoesTitular 			= matricula.getTitular().calculaValorSegundaViaCartao(detalheContaTitular);
		
		detalheContaTitular.setValorSegundaViaCartao(detalheContaTitular.getValorSegundaViaCartao().add(valorCartoesTitular));
		valorTotalCartoes = valorTotalCartoes.add(valorCartoesTitular);
		for (Cartao cartao : detalheContaTitular.getCartoes()) {
			this.cartoes.add(cartao);
		}
		
		//cartões dos dependentes
		for (DependenteSR dependente : matricula.getTitular().getDependentes(SituacaoEnum.ATIVO.descricao())) {
			DetalheContaSegurado detalheContaDependente = dependente.getDetalheContaDependente(this.competencia);
			BigDecimal valorCartoesDependente = dependente.calculaValorSegundaViaCartao(detalheContaDependente);
			detalheContaDependente.setValorSegundaViaCartao(detalheContaDependente.getValorSegundaViaCartao().add(valorCartoesDependente));
			
			valorTotalCartoes = valorTotalCartoes.add(valorCartoesDependente);
			for (Cartao cartao : detalheContaDependente.getCartoes()) {
				this.cartoes.add(cartao);
			}
		}
		
		return valorTotalCartoes.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	//getters n' setters from fields
	public AbstractMatricula getMatricula() {
		return matricula;
	}
	public void setMatricula(AbstractMatricula matricula) {
		this.matricula = matricula;
	}

	public Date getCompetencia() {
		return competencia;
	}
	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}
	
	public long getIdContaMatricula() {
		return idContaMatricula;
	}
	public void setIdContaMatricula(long idContaMatricula) {
		this.idContaMatricula = idContaMatricula;
	}

	public BigDecimal getValorFinanciamento() {
		return valorFinanciamento;
	}
	public void setValorFinanciamento(BigDecimal valorFinanciamento) {
		this.valorFinanciamento = valorFinanciamento;
	}

	public BigDecimal getValorCoParticipacoes() {
		return valorCoParticipacoes;
	}
	public void setValorCoParticipacoes(BigDecimal valorCoParticipacoes) {
		this.valorCoParticipacoes = valorCoParticipacoes;
	}

	public BigDecimal getValorSegundaViaCartao() {
		return valorSegundaViaCartao;
	}
	public void setValorSegundaViaCartao(BigDecimal valorSegundaViaCartao) {
		this.valorSegundaViaCartao = valorSegundaViaCartao;
	}

	public Set<GuiaSimples> getGuias() {
		return guias;
	}
	public void setGuias(Set<GuiaSimples> guias) {
		this.guias = guias;
	}

	public Set<Cartao> getCartoes() {
		return cartoes;
	}

	public void setCartoes(Set<Cartao> cartoes) {
		this.cartoes = cartoes;
	}
	
}
