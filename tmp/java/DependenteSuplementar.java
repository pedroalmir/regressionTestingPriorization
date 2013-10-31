package br.com.infowaypi.ecare.segurados;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.FlushMode;

import br.com.infowaypi.ecare.financeiro.Cobranca;
import br.com.infowaypi.ecare.financeiro.conta.ContaMatricula;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.Role;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.enums.TipoSeguradoEnum;
import br.com.infowaypi.ecarebc.financeiro.DependenteFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.TitularFinanceiroInterface;
import br.com.infowaypi.ecarebc.planos.Plano;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

@SuppressWarnings("unchecked")
public class DependenteSuplementar extends TitularFinanceiroSR implements DependenteInterface{
	
	//Data em que a cobranca do dependente suplementar deixou de ser cobrada junto com a de seu titular
	private static final Date DATA_MUDANCA_COBRANCA = Utils.createData("26/02/2008");
	private static final long serialVersionUID = 1L;
	private Titular titular;
	private Integer tipoDeDependencia;
	private String descricaoDaDependencia;
	private String numeroDoProcesso;
		
	public DependenteSuplementar() {
		super();
	}
	
	public DependenteSuplementar(UsuarioInterface usuario) {
		super();
	}
	
	@Override
	public String getDescricaoDaDependencia() {
		if(this.descricaoDaDependencia != null)
			return this.descricaoDaDependencia;

		switch(tipoDeDependencia) {
			case TIPO_SUPLEMENTAR_PAIS:
				return SeguradoBasico.DESCRICAO_TIPO_SUPLEMENTAR_PAIS;
			case TIPO_SUPLEMENTAR_AVOS: 
				return SeguradoBasico.DESCRICAO_TIPO_SUPLEMENTAR_AVOS;
			case TIPO_SUPLEMENTAR_BISAVOS:
				return SeguradoBasico.DESCRICAO_TIPO_SUPLEMENTAR_BISAVOS;
			case TIPO_SUPLEMENTAR_TATARAVOS:
				return SeguradoBasico.DESCRICAO_TIPO_SUPLEMENTAR_TATARAVOS;
			case TIPO_SUPLEMENTAR_SOBRINHO:
				return SeguradoBasico.DESCRICAO_TIPO_SUPLEMENTAR_SOBRINHO;
			case TIPO_SUPLEMENTAR_FILHO:
				return SeguradoBasico.DESCRICAO_TIPO_SUPLEMENTAR_FILHO;
			case TIPO_SUPLEMENTAR_NETO:
				return SeguradoBasico.DESCRICAO_TIPO_SUPLEMENTAR_NETO;
			case TIPO_SUPLEMENTAR_BISNETO:
				return SeguradoBasico.DESCRICAO_TIPO_SUPLEMENTAR_BISNETO;
			case TIPO_SUPLEMENTAR_TATARANETO:
				return SeguradoBasico.DESCRICAO_TIPO_SUPLEMENTAR_TATARANETO;
			case TIPO_SUPLEMENTAR_TIOS:
				return SeguradoBasico.DESCRICAO_TIPO_SUPLEMENTAR_TIOS;
			case TIPO_IRMAO:
				return SeguradoBasico.DESCRICAO_TIPO_IRMAO;
		}	
		return null;
	}
	
	@Override
	public String getTipoDeSegurado() {
		return TipoSeguradoEnum.DEPENDENTE_SUPLEMENTAR.descricao();
	}
	
	public BigDecimal getValorIndividual(Date dataBase) {
		int idade = this.getPessoaFisica().getIdadeAt(dataBase);
		
		return EnumFinanciamentoDependenteSuplementar.getValorFinanciamento(idade);
	}
	
	/**
	 * Calcula o valor do financiamento do dependente dado o sal�rio base
	 * @return um array de BigDecimal onde a primeira posi��o � referente
	 * � al�quota e a segunda posi��o do array � referente ao valor do 
	 * financiamento.
	 */
	public BigDecimal[] getValorFinanciamentoDependente(BigDecimal salarioBase, Date dataBase) {
		BigDecimal[] valor = new BigDecimal[2];
		valor[0] = BigDecimal.ZERO;
		valor[1] = this.getValorIndividual(dataBase);
		return valor;
	}
	
	
		
	@Override
	public BigDecimal getValorCoparticipacao(Date date) {
		BigDecimal valorCoparticipacao = super.getValorCoparticipacao(date);
		
		if(valorCoparticipacao.compareTo(TETO_COPARTICIPACAO) > 0)
			return TETO_COPARTICIPACAO;
		
		return valorCoparticipacao;
	}
	
	/**
	 * Calcula o valor da coparticipa��o do segurado considerando todas as guias <br>
	 * com data de atendimento menor do que a data passada como par�metro. 
	 * @param dataLimite Data limite para a data de atendimento das guias
	 * @param cobranca Ter� vinculada todas as guias que geraram a coparticipa��o 
	 * @return o valor de coparticipa��o de todas as guias vinculadas � cobran�a
	 */
	public BigDecimal calculaValorCoparticipacao(Date dataLimite, Cobranca cobranca){
		BigDecimal valor = BigDecimal.ZERO;
		for (GuiaSimples guia : this.getGuiasAptasAoCalculoDeCoparticipacao(dataLimite)) {
			cobranca.addGuia(guia);
			valor = valor.add(guia.getValorCoParticipacao());
		}
		return ContaMatricula.verificaTetoCoparticipacao(valor);
	}
	
	
	
	public Titular getTitular() {
		return this.titular;
	}
	
	public void setTitular(Titular titular) {
		this.titular = titular;
	}
	
	public String getCodigoLegado() {
		return codigoLegado;
	}

	public void setCodigoLegado(String codigoLegado) {
		this.codigoLegado = codigoLegado;
	}

	public Integer getTipoDeDependencia() {
		return tipoDeDependencia;
	}

	public void setTipoDeDependencia(Integer tipoDeDependencia) {
		this.tipoDeDependencia = tipoDeDependencia;
	}

	public String getNumeroDoProcesso() {
		return numeroDoProcesso;
	}

	public void setNumeroDoProcesso(String numeroDoProcesso) {
		this.numeroDoProcesso = numeroDoProcesso;
	}

	public void setDescricaoDaDependencia(String descricaoDaDependencia) {
		this.descricaoDaDependencia = descricaoDaDependencia;
	}

	@Override
	public void reativar(String motivo, Date dataAdesao, UsuarioInterface usuario) throws Exception {
		super.reativar(motivo, dataAdesao,usuario);
	}
	
	@Override
	public void suspender(String motivo, UsuarioInterface usuario) throws Exception {
		super.suspender(motivo, usuario);
	}
	
	@Override
	public void cancelar(String motivo, UsuarioInterface usuario) throws Exception {
		super.cancelar(motivo, usuario);
	}

	@Override
	public Set<DependenteSR> getDependentes() {
		return new HashSet<DependenteSR>();
	}

	@Override
	public String getTipoPlano() {
		return null;
	}

	@Override
	public boolean isSeguradoDependenteSuplementar() {
		return true;
	}
	@Override
	public boolean isSeguradoDependente() {
		return true;
	}

	@Override
	public boolean isSeguradoPensionista() {
		return false;
	}

	@Override
	public boolean isSeguradoTitular() {
		return false;
	}

	@Override
	public boolean isBeneficiario() {
		return true;
	}

	public TitularFinanceiroInterface getTitularFinanceiro() {
		return null;
	}

	public Plano getPlano() {
		return null;
	}

	public void setPlano(Plano plano) {
	}

	public BigDecimal getValorContrato() {
		return null;
	}

	public Set<DependenteFinanceiroInterface> getDependentesFinanceiro() {
		return new HashSet<DependenteFinanceiroInterface>();
	}
	

	//TODO Refatorar esse m�todo, codigo igual ao validate de DependenteSR
	public Boolean validate(UsuarioInterface usuario) throws ValidateException {
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);

		Assert.isTrue(Utils.compareData(this.dataAdesao,titular.getDataAdesao()) >= 0, "O dependente n�o pode possuir DATA DE ADES�O inferior a data de ades�o do titular.");
		Assert.isNotEquals(getTipoDeDependencia(),-1, "O campo Parentesco deve ser preenchido.");
		Assert.isNotEmpty(this.getTitular().getMatriculas(), "O Titular deve ter no m�nimo uma matr�cula.");
		
		if(this.ordem == 0)
			this.ordem = titular.getDependentes().size()+1;

		if(!Utils.isStringVazia(this.pessoaFisica.getCpf()) && !Utils.isCpfValido(this.pessoaFisica.getCpf())){
			throw new ValidateException("CPF inv�lido");
		}
		
		if (!this.isRecadastrado()){
			recadastrar(usuario);
		}
		
		if(this.getTitular() == null)
			throw new ValidateException("O Titular n�o foi informado.");

		if(Utils.isStringVazia(this.contrato))
			this.contrato = this.getTitular().getContrato();

		if(this.getTipoDeDependencia().equals(DependenteInterface.TIPO_CONJUGUE)){
			int count = 0;
			
			for (DependenteInterface dep : this.getTitular().getDependentes()) {
				if (dep.getTipoDeDependencia() == DependenteInterface.TIPO_CONJUGUE){
					count++;
				}
				
				if (count > 1){
					throw new ValidateException("O Titular deste contrato j� possui um dependente cadastrado com o parentesco C�njuge.");
				}
			}
		}
		
		if(this.getDataAdesao() != null){
			if(this.getDataAdesao().compareTo(this.getPessoaFisica().getDataNascimento()) < 0 ){
				throw new ValidateException("A Data de Ades�o n�o pode ser menor que a Data de Nascimento.");
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
		
		if(this.isSituacaoAtual(SituacaoEnum.CADASTRADO.descricao())){
			this.mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(), MotivoEnum.CADASTRO_NOVO.getMessage(), new Date());
		}
		
		Boolean retorno = super.validate(usuario);
		
		//Cria usu�rio que d� acesso ao segurado 'dependente suplementar' ao portal do benefici�rio.
		this.criarUsuarioSegurado(Role.TITULAR.getValor());
		
		return retorno;
	}
	
	@Override
	public void tocarObjetos() {
		super.tocarObjetos();
		this.getTitular().tocarObjetos();
	}
	
	@Override
	public void updateDataInicioCarencia() {
		
		if(Utils.compareData(this.dataAdesao, diaD) < 0) {
			this.setInicioDaCarencia(dataAdesao);
		} else {
			Date dataDoPrimeiroPagamento = this.getDataDoPrimeiroPagamento();
			this.setInicioDaCarencia(dataDoPrimeiroPagamento);
		}
	}
	 
	public Date getDataDoPrimeiroPagamento() {
		Date dataAdesao = this.getDataAdesao();
		
		boolean aderiuAntesDaMudancaCobranca = Utils.compareData(dataAdesao, DATA_MUDANCA_COBRANCA) < 0;
		if (aderiuAntesDaMudancaCobranca) {
			// nesse caso � buscado no banco o primeiro pagamento do titular, 
			// caso n�o exista ele busca a cobranca no dependente suplementar
			if (this.getTitular().getCobrancas().isEmpty()) {
				return this.getDataDoPrimeiroPagamentoCobranca();
			}
			return this.getTitular().getDataDoPrimeiroPagamentoCobranca(); 
		} else {
			return this.getDataDoPrimeiroPagamentoCobranca();
		}
	}
}
