package br.com.infowaypi.ecare.segurados;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.FlushMode;

import br.com.infowaypi.ecare.constantes.Constantes;
import br.com.infowaypi.ecare.financeiro.Cobranca;
import br.com.infowaypi.ecare.financeiro.CobrancaInterface;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsulta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.DependenteFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.FluxoFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.consignacao.Consignacao;
import br.com.infowaypi.ecarebc.financeiro.conta.ContaInterface;
import br.com.infowaypi.ecarebc.planos.Plano;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.pessoa.PessoaFisica;
import br.com.infowaypi.msr.situations.Situacao;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe que representa um Titular no sistema. 
 * @author Mário Sérgio Coelho Marroquim
 * @changes Diogo Vinícius / Danilo Nogueira Portela
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class Titular extends TitularFinanceiroSR {

	private static final long serialVersionUID = 2966316738132423117L;
	
	private CargoInterface cargo;
	private Set<DependenteSR> dependentes;
	private Set<DependenteSuplementar> dependentesSuplementares;
	private boolean beneficiario;
	private Set<Pensionista> pensionistas;
	private String situacaoNaPrefeitura;
	
	public Titular(){
		this(null);
		Situacao sitCadastral = new Situacao();
		sitCadastral.setDescricao(SituacaoEnum.CADASTRADO.descricao());
		sitCadastral.setDataSituacao(new Date());
		sitCadastral.setMotivo("Novo titular cadastrado.");
		this.setSituacaoCadastral(sitCadastral);
		this.dependentes = new HashSet<DependenteSR>();
		this.dependentesSuplementares = new HashSet<DependenteSuplementar>();
		this.pensionistas = new HashSet<Pensionista>();
	}
	
	public Titular(UsuarioInterface usuario) {
		super();
		this.beneficiario = true;
	}

	public static List<Titular> getTitularesAtivos(){
		List<Titular> titularesAtivos = null;
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("situacao.descricao", "Ativo(a)"));
		titularesAtivos = sa.list(Titular.class);			
		return titularesAtivos;
	}
	
	
	public Boolean validate(UsuarioInterface usuario) throws ValidateException {
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
		
		Assert.isNotEquals(getPessoaFisica().getEstadoCivil(),-1, "O campo Estado Civil deve ser preenchido.");

		if (!Utils.isStringVazia(getPessoaFisica().getNomeDaMae())){
			recadastrar(usuario);
		}
		
		if (this.getPessoaFisica().getEstadoCivil().equals(PessoaFisica.ESTADO_CIVIL_CASADO)) {
			if(Utils.isStringVazia(this.pessoaFisica.getNomeConjuge())) {
				throw new ValidateException("O nome do cônjuge deve ser preenchido!");
			}
			if(Utils.isStringVazia(this.pessoaFisica.getCpfDoConjugue())) {
				throw new ValidateException("O CPF do cônjuge deve ser informado");
			}
			if(!Utils.isCpfValido(this.pessoaFisica.getCpfDoConjugue())) {
				throw new ValidateException("O CPF do cônjuge inválido");
			}
		}			

		if(!Utils.isCpfValido(this.pessoaFisica.getCpf())){
			throw new ValidateException("CPF inválido");
		}
		
		if(this.getDataAdesao() != null){
			if(this.getDataAdesao().compareTo(this.getPessoaFisica().getDataNascimento()) < 0 ){
				throw new ValidateException("A Data de Adesão não pode ser menor que a Data de Nascimento.");
			}
		}
		
		boolean cpfDuplicado = Utils.isCampoDuplicado(this, "pessoaFisica.cpf", this.getPessoaFisica().getCpf());
		if(cpfDuplicado)
			throw new ValidateException("Este CPF já foi cadastrado para outro Titular.");
			
					
		if(this.getPessoaFisica().getIdade() < Constantes.IDADE_MINIMA_PARA_EXIGENCIA_DE_DOCUMENTO){
			throw new ValidateException("O titular deve ser maior de idade!");
		}		

		return super.validate(usuario);
	}
	
	/*
	 * Retorna um identificador para ser usado pelo banco 
	 * esse identificador deverá ser mostrado após o cadastro do titular
	 */
	public String getIdentificador() {
		return StringUtils.leftPad(this.getIdSegurado().toString(), 6, "0");
	}
	
	public Set<Pensionista> getPensionistas() {
		return pensionistas;
	}

	public void setPensionistas(Set<Pensionista> pensionistas) {
		this.pensionistas = pensionistas;
	}
	
	public String getSituacaoNaPrefeitura() {
		return situacaoNaPrefeitura;
	}

	public void setSituacaoNaPrefeitura(String situacaoNaPrefeitura) {
		this.situacaoNaPrefeitura = situacaoNaPrefeitura;
	}
	
	@Override
	public void reativar(String motivo, Date dataAdesao,  UsuarioInterface usuario) throws Exception {
		super.reativar(motivo, dataAdesao, usuario);
	}
	
	@Override
	public void suspender(String motivo, UsuarioInterface usuario) throws Exception {
		super.suspender(motivo, usuario);
	}
	
	@Override
	public void cancelar(String motivo, UsuarioInterface usuario) throws Exception {
		super.cancelar(motivo, usuario);
		for (DependenteSR dependente: getDependentes()) {
			if (!dependente.isSituacaoAtual(Constantes.SITUACAO_CANCELADO)) {
				dependente.mudarSituacao(usuario, Constantes.SITUACAO_CANCELADO,  "Cancelamento automático (cancelamento de titular)", new Date());
			}
		}
		for (DependenteSuplementar dependenteSuplementar : getDependentesSuplementares()) {
			if (!dependenteSuplementar.isSituacaoAtual(Constantes.SITUACAO_CANCELADO)) {
				dependenteSuplementar.mudarSituacao(usuario, Constantes.SITUACAO_CANCELADO,  "Cancelamento automático (cancelamento de titular)", new Date());
			}
		}
	}
	
	public static String calcularDigitoVerificadorDaMatricula(String matricula){
		Integer posicao1 = Integer.parseInt(matricula.substring(0, 1));
		Integer posicao2 = Integer.parseInt(matricula.substring(1, 2));
		Integer posicao3 = Integer.parseInt(matricula.substring(2, 3));
		Integer posicao4 = Integer.parseInt(matricula.substring(3, 4));
		Integer posicao5 = Integer.parseInt(matricula.substring(4, 5));
		Integer valorTotal = (posicao1 * 6) + (posicao2 * 5) + (posicao3 * 4) + (posicao4 * 3) + (posicao5 * 2);
		Integer valorDivisao = Math.abs(valorTotal/11);
		Integer caractereVerificador = (11 - (valorTotal - (valorDivisao * 11)));
		String dv = null;
		
		switch (caractereVerificador) {
        case 10: dv = "0"; break;
        case 11: dv = "X"; break;
		}
		
		if(dv == null)
			dv = String.valueOf(caractereVerificador);

		return dv;
	}


	public CargoInterface getCargo() {
		return this.cargo;
	}

	public void setCargo(CargoInterface cargo) {
		this.cargo = cargo;
	}

	public boolean isBeneficiario() {
		return beneficiario;
	}

	public void setBeneficiario(boolean beneficiario) {
		this.beneficiario = beneficiario;
	}
	
	
	private BigDecimal roundedValue(BigDecimal valor){
		return new BigDecimal(Math.floor(valor.doubleValue()* 10 + 0.4)/10);
		 
	}
	
	public CobrancaInterface getUltimaCobranca() {
		Cobranca ultima = null;
		for (Cobranca cobranca : this.getCobrancas()) {
			if(ultima == null)
				ultima = cobranca;
			else{
				if(0 < Utils.compareData(cobranca.getDataVencimento(),ultima.getDataVencimento()))
					ultima = cobranca;
			}
		}
		return ultima;
	}
	
	public CobrancaInterface getPrimeiraCobranca() {
		Cobranca primeira = null;
		for (Cobranca cobranca : this.getCobrancas()) {
			if(primeira == null)
				primeira = cobranca;
			else{
				if(0 > Utils.compareData(cobranca.getDataVencimento(),primeira.getDataVencimento()))
					primeira = cobranca;
			}
		}
		return primeira;
	}
	
	public Cobranca getCobranca(Date competencia) {
		for (Cobranca cobranca : this.getCobrancas()) {
			if (Utils.compararCompetencia(cobranca.getCompetencia(),competencia)== 0) {
				return cobranca;
			}
		}
		return null;
	}
	
	private ContaInterface getContaNaoEnviada(){
		ContaInterface ultimaConta = null;
		for (ContaInterface conta : this.getUltimaCobranca().getColecaoContas().getContas()) {
			if(ultimaConta == null)
				ultimaConta = conta;
			else{
				if(0 > Utils.compareData(conta.getDataVencimento(),ultimaConta.getDataVencimento()))
					ultimaConta = conta;
			}
		}
		return ultimaConta ;
	}
	
	private boolean isTemCobrancaPendente() {
		for(Cobranca cobranca : this.getCobrancas()){
			if (!cobranca.isSituacaoAtual(CobrancaInterface.PAGO)){
				return true;
			}
		}
		return false;
	}
	
	private Date getDataVencimento(Date competencia){
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(competencia);
		calendar.set(Calendar.DATE, this.getDiaBase());
		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY )
			calendar.add(Calendar.DATE, 2);
			else
				if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
					calendar.add(Calendar.DATE, 1);	
		
		return calendar.getTime();
		
	}

	@Override
	public String getTipoPlano() {
		return "Ind/Tit";
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.DEFAULT_STYLE)
		.append("id", this.getIdSegurado())
		.append("nome", this.pessoaFisica.getNome())
		.append("cpf", this.pessoaFisica.getCpf())
		.toString();
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

	public Set<FluxoFinanceiroInterface> getFluxosFinanceiros() {
		Set<FluxoFinanceiroInterface> fluxos = new HashSet<FluxoFinanceiroInterface>();
		for(Consignacao consignacao : this.getConsignacoes())
			fluxos.add(consignacao);
		return fluxos;
	}

	@SuppressWarnings("unchecked")
	public  TitularInterface getTitular() {
		return this;
	}

	public Plano getPlano() {
		return null;
	}

	public void setPlano(Plano plano) {
		
	}
	public BigDecimal getValorContrato() {
		return null;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof SeguradoInterface)) {
			return false;
		}
		SeguradoInterface otherObject = (SeguradoInterface) object;
		return new EqualsBuilder()
				.append(this.getIdSegurado(), otherObject.getIdSegurado())
				.append(this.getPessoaFisica().getCpf(),otherObject.getPessoaFisica().getCpf())
				.isEquals();
	}

	public Set<DependenteSR> getDependentes() {
		return dependentes;
	}

	public void setDependentes(Set<DependenteSR> dependentes) {
		this.dependentes = dependentes;
	}

	@Override
	public boolean isSeguradoTitular() {
		return true;
	}

	public Set<DependenteFinanceiroInterface> getDependentesFinanceiro() {
		Set<DependenteFinanceiroInterface> dependentes = new HashSet<DependenteFinanceiroInterface>();
		for(DependenteInterface dep : this.dependentes){
			dependentes.add(dep);
		}
		return dependentes;
	}
	@Override
	public boolean isSeguradoDependenteSuplementar() {
		return false;
	}
	@Override
	public boolean isSeguradoPensionista() {
		return false;
	}
	
	@Override
	public boolean isSeguradoDependente() {
		return false; 
	}	

	public Set<DependenteSuplementar> getDependentesSuplementares() {
		return dependentesSuplementares;
	}

	public void setDependentesSuplementares(Set<DependenteSuplementar> dependentesSuplementares) {
		this.dependentesSuplementares = dependentesSuplementares;
	}
	
	public void atualizaOrdemDependente() {
		List<DependenteSR> dependentes = new ArrayList<DependenteSR>(this.getDependentes());
		List<DependenteSR> dependentesExcluidos = new ArrayList<DependenteSR>();
		
		boolean isCancelado = false ;
		boolean isSuspenso = false ;
		
		for (DependenteSR dependenteSR : dependentes) {
			isCancelado = dependenteSR.getSituacao().getDescricao().equals(SituacaoEnum.CANCELADO.descricao());
			isSuspenso = dependenteSR.getSituacao().getDescricao().equals(SituacaoEnum.SUSPENSO.descricao());
			if(isCancelado || isSuspenso) {
				dependentesExcluidos.add(dependenteSR);
			}
		}
		dependentes.removeAll(dependentesExcluidos);
		
		Collections.sort(dependentes);
		
		int ordem = 0;
		
		for (DependenteSR dep : dependentes) {
			ordem++;
			dep.setOrdem(ordem);
		}
		
		for (DependenteSR depe : dependentesExcluidos) {
			ordem++;
			depe.setOrdem(ordem);
		}
	}
	
	@Override
	public void updateDataInicioCarencia() {
		if(dataAdesao == null){
			this.setInicioDaCarencia(null);
			return;
		} if (Utils.compareData(this.dataAdesao, diaD) < 0) {
			this.setInicioDaCarencia(dataAdesao);	
		} else {
			Date dataDoPrimeiroPagamento = getDataDoPrimeiroPagamento();
			
			if(dataDoPrimeiroPagamento != null) {
				this.setInicioDaCarencia(dataDoPrimeiroPagamento);
			}
		}
	}
	
	public Date getDataDoCreditoPrimeiraConsignacaoApos(Date dataBase) {
		List<Consignacao> consignacoes = new ArrayList<Consignacao>(this.getConsignacoes());
		
		if(!consignacoes.isEmpty()) {
			Collections.sort(consignacoes, new Comparator<Consignacao>(){
				public int compare(Consignacao c1, Consignacao c2) {
					return c1.getDataDoCredito().compareTo(c2.getDataDoCredito());
				}
			});
		}	
		
		for (Consignacao consignacao : consignacoes) {
			if(consignacao.getStatusConsignacao() == 'P' && Utils.compareData(consignacao.getDataDoCredito(), dataBase) >= 0) {
				return consignacao.getDataDoCredito();
			}
		}
		
		return null;
	}
	
	public Date getDataPagamentoPrimeiraCobrancaApos(Date dataBase) {
		List<Cobranca> cobrancas = new ArrayList<Cobranca>();
		cobrancas.addAll(this.getCobrancas());
		
		if(cobrancas != null) {
			Collections.sort(cobrancas, new Comparator<Cobranca>(){
				public int compare(Cobranca c1, Cobranca c2) {
						if(c1.getDataPagamento() != null && c2.getDataPagamento() != null)
							return c1.getDataPagamento().compareTo(c2.getDataPagamento());
						return 0;
				}
			});
		}
		
		for (Cobranca cobranca : cobrancas) {
			if(cobranca.isSituacaoAtual(SituacaoEnum.PAGO.descricao()) && Utils.compareData(cobranca.getDataPagamento(),dataBase) >= 0) {
				return cobranca.getDataPagamento();
			}
		}
		
		return null;
	}
	
	public boolean isPagaEmBoleto(){
		for (AbstractMatricula  matricula: this.getMatriculas() ) {
			if(matricula.isAtiva() && matricula.getTipoPagamento().equals(Matricula.FORMA_PAGAMENTO_BOLETO)){
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void zerarDetalhesCobranca(Date competencia) {
		super.zerarDetalhesCobranca(competencia);
		for (DependenteSR dependenteSR : dependentes) {
			dependenteSR.getDetalheContaDependente(competencia).zerarValoresDetalhe();
		}
	}
}