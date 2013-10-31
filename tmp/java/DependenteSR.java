package br.com.infowaypi.ecare.segurados;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.FlushMode;

import br.com.infowaypi.ecare.enums.SituacaoCartaoEnum;
import br.com.infowaypi.ecare.financeiro.consignacao.PisoFinanciamentoDependente;
import br.com.infowaypi.ecare.financeiro.conta.DetalheContaDependente;
import br.com.infowaypi.ecare.services.Faixa;
import br.com.infowaypi.ecarebc.associados.Prestador.TipoIncremento;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsulta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.TitularFinanceiroInterface;
import br.com.infowaypi.ecarebc.planos.Plano;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.Situacao;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe que representa um Dependente no sistema. 
 * @author Mário Sérgio Coelho Marroquim
 */
@SuppressWarnings("serial")
public abstract class DependenteSR extends Segurado implements DependenteInterface, Comparable<DependenteSR>{
	
	private String codigoLegado;
	private Integer tipoDeDependencia;
	private String descricaoDaDependencia;
	private Titular titular;
	private String numeroDoProcesso;
	private Set<DetalheContaDependente> detalhesContaDependente;
	
	public DependenteSR(){
		this(null);
		this.tipoDeDependencia = 0;
		Situacao sitCadastral = new Situacao();
		sitCadastral.setDescricao(SituacaoEnum.CADASTRADO.descricao());
		sitCadastral.setDataSituacao(new Date());
		sitCadastral.setMotivo("Novo dependente cadastrado.");
		this.setSituacaoCadastral(sitCadastral);
		this.detalhesContaDependente = new HashSet<DetalheContaDependente>();
	}
	
	public DependenteSR(UsuarioInterface usuario){
		super();
	}

	public Boolean validate(UsuarioInterface usuario) throws ValidateException {
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);

		Assert.isNotNull(titular.getDataAdesao(), "Caro usuário, informe a data de adesão do titular antes de cadastrar este dependente.");
		Assert.isTrue(Utils.compareData(this.dataAdesao,titular.getDataAdesao()) >= 0, "O dependente não pode possuir DATA DE ADESÃO inferior a data de adesão do titular.");
		Assert.isNotEquals(getTipoDeDependencia(),-1, "O campo Parentesco deve ser preenchido.");
		Assert.isNotEmpty(this.getTitular().getMatriculas(), "O Titular deve ter no mínimo uma matrícula.");
		
		if(this.ordem == 0)
			this.ordem = titular.getDependentes().size()+1;

		if(!Utils.isStringVazia(this.pessoaFisica.getCpf()) && !Utils.isCpfValido(this.pessoaFisica.getCpf())){
			throw new ValidateException("CPF inválido");
		}

		if (!this.isRecadastrado()){
			recadastrar(usuario);
		}
		
		if(this.getTitular() == null)
			throw new ValidateException("O Titular não foi informado.");

		if(Utils.isStringVazia(this.contrato))
			this.contrato = this.getTitular().getContrato();

		if(this.getTipoDeDependencia().equals(DependenteInterface.TIPO_CONJUGUE)){
			int count = 0;
			for (DependenteInterface dep : this.getTitular().getDependentes()) {
				if (dep.getTipoDeDependencia() == DependenteInterface.TIPO_CONJUGUE)
					count++;
				if (count > 1)
					throw new ValidateException("O Titular deste contrato já possui um dependente cadastrado com o parentesco Cônjuge.");
				
			}
		}
		if(this.getDataAdesao() != null){
			if(this.getDataAdesao().compareTo(this.getPessoaFisica().getDataNascimento()) < 0 ){
				throw new ValidateException("A Data de Adesão não pode ser menor que a Data de Nascimento.");
			}
		}
		
		try {
			if(Utils.isStringVazia(this.getNumeroDoCartao())){
				ImplDAO.save(this);
				this.gerarCartao();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(MensagemErroEnum.ERRO_AO_GERAR_CARTAO.getMessage());
		}
		
		if(this.isSituacaoAtual(SituacaoEnum.CADASTRADO.descricao()))
			this.mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(), MotivoEnum.CADASTRO_NOVO.getMessage(), new Date());
		
		return super.validate(usuario);
	}

	@SuppressWarnings("unchecked")
	public Titular  getTitular() {
		return this.titular;
	}
	
	public void setTitular(Titular titular) {
		this.titular = titular;
	}
	
	public Integer getTipoDeDependencia() {
		return this.tipoDeDependencia;
	}

	public void setTipoDeDependencia(Integer tipoDeDependencia) {
		this.tipoDeDependencia = tipoDeDependencia;
	}
	
	public String getNumeroDoProcesso() {
		return this.numeroDoProcesso;
	}

	public void setNumeroDoProcesso(String numeroDoProcesso) {
		this.numeroDoProcesso = numeroDoProcesso;
	}
	
	@Override
	public String getDescricaoDaDependencia() {
		
		switch(this.tipoDeDependencia) {
		case 1:
			return SeguradoBasico.DESCRICAO_TIPO_FILHO_MENOR_Q_21;
		case 3: 
			return SeguradoBasico.DESCRICAO_TIPO_FILHO_MENOR_25_ANOS;
		case 4:
			return SeguradoBasico.DESCRICAO_TIPO_FILHO_EXCEPCIONAL_INVALIDO;
		case 5:
			return SeguradoBasico.DESCRICAO_TIPO_ENTEADO;
		case 6:
			return SeguradoBasico.DESCRICAO_TIPO_TUTELADO;
		case 10:
			return SeguradoBasico.DESCRICAO_TIPO_CONJUGUE;
		case 11:
			return SeguradoBasico.DESCRICAO_TIPO_COMPANHEIRO;
		case 12:
			return SeguradoBasico.DESCRICAO_TIPO_EX_ESPOSA;
		case 13:
			return SeguradoBasico.DESCRICAO_TIPO_EX_COMPANHEIRO;
		case 15:
			return SeguradoBasico.DESCRICAO_TIPO_MAE;
		case 16:
			return SeguradoBasico.DESCRICAO_TIPO_PAI;
		case 20:
			return SeguradoBasico.DESCRICAO_TIPO_IRMAO;
		}	
		return null;
	}
	
	public void setDescricaoDaDependencia(String descricaoDaDependencia) {
		this.descricaoDaDependencia = descricaoDaDependencia;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof DependenteInterface)) {
			return false;
		}
		DependenteSR otherObject = (DependenteSR) object;
		return new EqualsBuilder()
			.append(this.getIdSegurado(), otherObject.getIdSegurado())
			.append(super.contrato, otherObject.getContrato())
			.append(this.tipoDeDependencia, otherObject.getTipoDeDependencia())
			.append(this.pessoaFisica.getNome(), otherObject.getPessoaFisica().getNome())
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.getIdSegurado())
			.append(super.getContrato())
			.append(this.getTipoDeDependencia())
			.toHashCode();
	}

	public String getCodigoLegado() {
		return codigoLegado;
	}

	public void setCodigoLegado(String codigoLegado) {
		this.codigoLegado = codigoLegado;
	}

	public String getDescricaoLesao() {
		return descricaoLesao;
	}

	public void setDescricaoLesao(String descricaoLesao) {
		this.descricaoLesao = descricaoLesao;
	}

	public Plano getPlano() {
		return titular.getPlano();
	}

	public Date getDataDoPrimeiroPagamento() {
		return this.getTitular().getDataDoPrimeiroPagamento();
	}
	
	@Override
	public String getTipoPlano() {
		return "Ind/Dep";
	}

	public Boolean getCumpreCarencia() {
		return false;
	}

	public void setCumpreCarencia(Boolean arg0) {
	}

	public Collection<GuiaConsulta> buscarConsultasPorPeriodo(Integer arg0) {
		return null;
	}

	public Collection<GuiaExame> buscarExamesPorPeriodo(Integer arg0) {
		return null;
	}

	public Collection<GuiaCompleta> getEletivas() {
		return null;
	}

	public Collection<GuiaCompleta> getUrgencias() {
		return null;
	}

	public void atualizarLimites(GuiaCompleta arg0, TipoIncremento arg1, int arg2) throws Exception {
	}

	public boolean isBeneficiario() {
		return true;
	}

	public TitularFinanceiroInterface getTitularFinanceiro() {
		return titular;
	}

	@Override
	public boolean isSeguradoTitular() {
		return false;
	}

	@Override
	public boolean isSeguradoPensionista() {
		return false;
	}

	@Override
	public boolean isSeguradoDependente() {
		return true;
	}
	
	@Override
	public boolean isCumpriuCarencia(Integer carencia){
		if(this.getCarenciaCumprida() >= carencia) {
			return true;
		}else {
			return false;
		}	
	}
	
	/**
	 * Calcula o valor do financiamento do dependente dado o salário base
	 * @return um array de BigDecimal onde a primeira posição é referente
	 * à alíquota e a segunda posição do array é referente ao valor do 
	 * financiamento.
	 */
	public BigDecimal[] getValorFinanciamentoDependente(BigDecimal salarioBase, Date dataBase){
		BigDecimal[] aliquotaValor = new BigDecimal[2];
		double aliquota = Faixa.getFaixaPorIdade(this.getPessoaFisica().getIdadeAt(dataBase)).getAliquota();
			
		if(salarioBase.compareTo(BigDecimal.ZERO) > 0)
			aliquotaValor[0] = new BigDecimal(aliquota);
		else
			aliquotaValor[0] = BigDecimal.ZERO;
		
		BigDecimal valorFinanciamento = salarioBase.multiply(new BigDecimal(aliquota/100)).setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal pisoFinanciamento = PisoFinanciamentoDependente.getPisoPorFaixaSalarial(salarioBase);
		if(valorFinanciamento.compareTo(pisoFinanciamento) < 0)
			valorFinanciamento = pisoFinanciamento;
		
		aliquotaValor[1] =  valorFinanciamento;
		return aliquotaValor;

	}
	
	private Faixa getFaixaFinanciamento(){
		Faixa faixaEncontrada = null;
		int idade = this.getPessoaFisica().getIdade();
		for (Faixa faixa : Faixa.values()) {
			if((idade >= faixa.getIdadeMinima()) && (idade <= faixa.getIdadeMaxima()))
				faixaEncontrada = faixa;
		}
		
		return faixaEncontrada;
	}
	
	public BigDecimal calculaValorSegundaViaCartao() {
		Set<Cartao> cartoes = new HashSet<Cartao>(); 
		cartoes = this.getCartoes();
		BigDecimal valorTotalCartoes = BigDecimal.ZERO;
		for (Cartao cartao : cartoes) {
			boolean isGerado = cartao.getSituacao().equals(SituacaoCartaoEnum.GERADO.getDescricao());
			boolean isPrimeiraVia = cartao.getViaDoCartao() == 1;
			if(isGerado && !isPrimeiraVia){
				valorTotalCartoes = valorTotalCartoes.add(Cartao.VALOR_CARTAO);
				cartao.setSituacao(SituacaoCartaoEnum.COBRADO.getDescricao());
			}
		}
		return valorTotalCartoes;
	}
	
	/**
	 * Retorna um objeto DetalheContaDependente para a competência informada, caso
	 * não exista, é instanciado um objeto com essa competência e retornado. 
	 */
	public DetalheContaDependente getDetalheContaDependente(Date competencia) {
		DetalheContaDependente detalhe = null;
		for (DetalheContaDependente det : this.getDetalhesContaDependente()) {
			if (Utils.compararCompetencia(det.getCompetencia(), competencia) == 0){
				detalhe = det;
			}
		}

		if (detalhe != null){
			return detalhe;
		}else{
			return new DetalheContaDependente(competencia, this);
		}
	}
	
	public Set<DetalheContaDependente> getDetalhesContaDependente() {
		return this.detalhesContaDependente;
	}

	public void setDetalhesContaDependente(Set<DetalheContaDependente> detalhesContaDependente) {
		this.detalhesContaDependente = detalhesContaDependente;
	}
	
	public void addDetalheConta(DetalheContaDependente detalhe){
		this.detalhesContaDependente.add(detalhe);
		detalhe.setDependente(this);
	}
	
	public void removeDetalheConta(DetalheContaDependente detalhe){
		this.detalhesContaDependente.remove(detalhe);
		detalhe.setDependente(null);
	}
	
	@Override
	public void tocarObjetos() {
		super.tocarObjetos();
		getTitular().getConsignacoes().size();
		getTitular().getCobrancas().size();
	}
	
	public int compareTo(DependenteSR dep) {
		
		if(this.getDataAdesao() == null && dep.getDataAdesao() != null) {
			return -1;
		}
		if(this.getDataAdesao() != null && dep.getDataAdesao() == null) {
			return 1;
		}
		
		if (this.getDataAdesao() == null && dep.getDataAdesao() == null) {
			return 0;
		} else {
			int compareDataAdesao = Utils.compareData(this.getDataAdesao(), dep.getDataAdesao());
			if(compareDataAdesao != 0) {
				return compareDataAdesao;
			}
			
			return Utils.compareData(this.getPessoaFisica().getDataNascimento(), dep.getPessoaFisica().getDataNascimento());
		}	
	}
	
	public Date getDataPrimeiroPagamentoAposAdesao() {
		Date dataConsignacao = null;
		Date dataCobranca = null;
		
		dataConsignacao = titular.getDataDoCreditoPrimeiraConsignacaoApos(this.dataAdesao);
		dataCobranca 	= titular.getDataPagamentoPrimeiraCobrancaApos(this.dataAdesao);
		
		if (dataConsignacao != null) {
			return dataConsignacao;
		} else if(dataCobranca != null) {
			return dataCobranca;
		} else {
			return null;
		}
	}

	public abstract void updateDataInicioCarencia();
	
}