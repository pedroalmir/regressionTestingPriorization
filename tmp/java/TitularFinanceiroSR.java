package br.com.infowaypi.ecare.segurados;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import br.com.infowaypi.ecare.financeiro.Cobranca;
import br.com.infowaypi.ecare.financeiro.CobrancaInterface;
import br.com.infowaypi.ecare.financeiro.DetalhePagamento;
import br.com.infowaypi.ecare.financeiro.DetalhePagamentoInterface;
import br.com.infowaypi.ecare.financeiro.arquivo.ContaConsignacao;
import br.com.infowaypi.ecare.financeiro.consignacao.PisoFinanciamentoTitular;
import br.com.infowaypi.ecare.financeiro.conta.ContaMatricula;
import br.com.infowaypi.ecare.financeiro.conta.DetalheContaTitular;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.FluxoFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.consignacao.Consignacao;
import br.com.infowaypi.ecarebc.financeiro.conta.Conta;
import br.com.infowaypi.ecarebc.financeiro.conta.ContaInterface;
import br.com.infowaypi.ecarebc.painelDeControle.PainelDeControle;
import br.com.infowaypi.ecarebc.segurados.GrupoBC;
import br.com.infowaypi.ecarebc.segurados.SubGrupo;
import br.com.infowaypi.msr.financeiro.InformacaoFinanceira;
import br.com.infowaypi.msr.financeiro.InformacaoFinanceiraInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class TitularFinanceiroSR extends Segurado implements TitularInterface {
	
	private static final long serialVersionUID = 1L;
	private Set<Cobranca> cobrancas;
	private Set<Consignacao> consignacoes;
	private DetalhePagamentoInterface detalhePagamento;
	private InformacaoFinanceiraInterface informacaoFinanceira;
	private Set<AbstractMatricula> matriculas;
	private int diaBase;
	private GrupoBC grupo;
	private SubGrupo secretaria;
	private Set<DetalheContaTitular> detalhesContaTitular;
	
	public TitularFinanceiroSR() {
		super();
		this.diaBase 				= 0;
		this.cobrancas 				= new HashSet<Cobranca>();
		this.consignacoes 			= new HashSet<Consignacao>();
		this.detalhePagamento 		= new DetalhePagamento();
		this.informacaoFinanceira 	= new InformacaoFinanceira();
		this.matriculas 			= new HashSet<AbstractMatricula>();
		this.detalhesContaTitular 	= new HashSet<DetalheContaTitular>();
	}

	public Set<Cobranca> getCobrancas() {
		return cobrancas;
	}

	public void setCobrancas(Set<Cobranca> cobrancas) {
		this.cobrancas = cobrancas;
	}

	public Set<Consignacao> getConsignacoes() {
		return consignacoes;
	}

	@Override
	public GrupoBC getGrupo() {
		return grupo;
	}
	
	public void setGrupo(GrupoBC grupo) {
		this.grupo = grupo;
	}

	public SubGrupo getSecretaria() {
		return secretaria;
	}

	public void setSecretaria(SubGrupo secretaria) {
		this.secretaria = secretaria;
	}

	public void setConsignacoes(Set<Consignacao> consignacoes) {
		this.consignacoes = consignacoes;
	}

	public DetalhePagamentoInterface getDetalhePagamento() {
		return detalhePagamento;
	}

	public void setDetalhePagamento(DetalhePagamentoInterface detalhePagamento) {
		this.detalhePagamento = detalhePagamento;
	}

	public InformacaoFinanceiraInterface getInformacaoFinanceira() {
		return informacaoFinanceira;
	}

	public void setInformacaoFinanceira(
			InformacaoFinanceiraInterface informacaoFinanceira) {
		this.informacaoFinanceira = informacaoFinanceira;
	}

	public int getDiaBase() {
		return diaBase;
	}

	public void setDiaBase(int diaBase) {
		this.diaBase = diaBase;
	}
	
	public Set<AbstractMatricula> getMatriculas() {
		return matriculas;
	}
	
	public void setMatriculas(Set<AbstractMatricula> matriculas) {
		this.matriculas = matriculas;
	}
	
	public Set<ContaMatricula> getContasMatriculas(Date competencia){
		Set<ContaMatricula> contasMatriculas = new HashSet<ContaMatricula>();
		for (AbstractMatricula matricula : this.matriculas) {
			contasMatriculas.addAll(matricula.getContasMatriculas(competencia));
		}
		return contasMatriculas;
	}
	
	public BigDecimal getValorContasMatriculas(Date competencia){
		BigDecimal valor = BigDecimal.ZERO;
		for (AbstractMatricula matricula : this.matriculas) {
			valor = valor.add(matricula.getValorContasMatriculas(competencia));
		}
		return valor.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	public BigDecimal getValorFinanciamentoTotal(Date competencia){
		BigDecimal valor = BigDecimal.ZERO;
		for (AbstractMatricula matricula : this.matriculas) {
			for (ContaMatricula contaMatricula : matricula.getContasMatriculas(competencia)) {
				valor = valor.add(contaMatricula.getValorFinanciamento());
			}
		}
		return valor.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	public BigDecimal getValorCoParticipacoesTotal(Date competencia){
		BigDecimal valor = BigDecimal.ZERO;
		for (AbstractMatricula matricula : this.matriculas) {
			for (ContaMatricula contaMatricula : matricula.getContasMatriculas(competencia)) {
				valor = valor.add(contaMatricula.getValorCoParticipacoes());
			}
		}
		return valor.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	public BigDecimal getValorSegundaViaCartaoTotal(Date competencia){
		BigDecimal valor = BigDecimal.ZERO;
		for (AbstractMatricula matricula : this.matriculas) {
			for (ContaMatricula contaMatricula : matricula.getContasMatriculas(competencia)) {
				valor = valor.add(contaMatricula.getValorSegundaViaCartao());
			}
		}
		return valor.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	public BigDecimal getValorCoparticipacao(Consignacao consignacao, Date competenciaAjustada){
		Set<GuiaSimples> guias = this.getGuias();
		
		for(DependenteInterface d : this.getDependentes())
			guias.addAll(d.getGuias());
		
		BigDecimal valorTotalCoparticipacaoGuias = BigDecimal.ZERO;
		valorTotalCoparticipacaoGuias.setScale(2, BigDecimal.ROUND_HALF_UP);

		BigDecimal limiteCoparticipacaoGuias = new BigDecimal(38);
		limiteCoparticipacaoGuias.setScale(2, BigDecimal.ROUND_HALF_UP);
		Calendar cal = new GregorianCalendar();
		cal.setTime(competenciaAjustada);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DATE));
		Date dataInicial = cal.getTime();
		System.out.println();
		System.out.println("TitularGuias: " + this.getPessoaFisica().getCpf() + " Qt: " + guias.size());
		for (GuiaSimples guia : guias) {
			if((guia.isSituacaoAtual(SituacaoEnum.CONFIRMADO.descricao())
					||guia.isSituacaoAtual(SituacaoEnum.AUDITADO.descricao())
					||guia.isSituacaoAtual(SituacaoEnum.FATURADA.descricao()))
					&&(guia.getDataAtendimento().compareTo(competenciaAjustada) <= 0)
					&&(guia.getDataAtendimento().compareTo(dataInicial) >= 0)
					&& guia.getFluxoFinanceiro() == null){
				if(consignacao != null){
					guia.setFluxoFinanceiro(consignacao);
					consignacao.getGuias().add(guia);
				}
				System.out.println("Guia: " + guia.getAutorizacao()+ " Tipo: "+ guia.getTipo()+ " Valor Coparticipacao: " + (new DecimalFormat("######0.00")).format(guia.getValorCoParticipacao())+" Situacao: " + guia.getSituacao().getDescricao() + " Data atendimento: " + Utils.format(guia.getDataAtendimento()));
				if(valorTotalCoparticipacaoGuias.compareTo(limiteCoparticipacaoGuias) != 0)
					if(valorTotalCoparticipacaoGuias.add(guia.getValorCoParticipacao()).compareTo(limiteCoparticipacaoGuias) < 0)
						valorTotalCoparticipacaoGuias = valorTotalCoparticipacaoGuias.add(guia.getValorCoParticipacao());
					else
						valorTotalCoparticipacaoGuias = limiteCoparticipacaoGuias;
			}
		}
		if(consignacao != null){
			consignacao.setValorCoparticipacao(valorTotalCoparticipacaoGuias);
		}
		return valorTotalCoparticipacaoGuias;
	}

	public Set<FluxoFinanceiroInterface> getFluxosFinanceiros() {
		Set<FluxoFinanceiroInterface> fluxos = new HashSet<FluxoFinanceiroInterface>();
		for(Cobranca cobranca : this.cobrancas)
			fluxos.add(cobranca);
		return fluxos;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.DEFAULT_STYLE)
													.append("id", this.getIdSegurado())
													.append("diabase", this.diaBase)
													.toString();
	}
	
	public Cobranca getCobranca(Date competencia) {
		for (Cobranca cobranca : this.cobrancas) {
			if(cobranca.isCancelada())
				continue;
			
			if (Utils.compararCompetencia(cobranca.getCompetencia(),competencia)== 0) {
				return cobranca;
			}
		}
		return null;
	}
	
	public CobrancaInterface getPrimeiraCobranca() {
		Cobranca primeira = null;
		
		for (Cobranca cobranca : this.cobrancas) {
			if(primeira == null){
				primeira = cobranca;
			} else if(0 > Utils.compareData(cobranca.getDataVencimento(),primeira.getDataVencimento())) {
				primeira = cobranca;
			}
		}
		
		return primeira;
	}
	
	public CobrancaInterface getPrimeiraCobrancaPaga() {
		Cobranca primeira = null;

		for (Cobranca cobranca : this.cobrancas) {
			if (cobranca.isSituacaoAtual(SituacaoEnum.PAGO.descricao())) {
				if (primeira == null){
					primeira = cobranca;
				} else if(0 > Utils.compareData(cobranca.getDataVencimento(),primeira.getDataVencimento())){
					primeira = cobranca;
				}
			}
		}
		
		return primeira;
	}
	
	public CobrancaInterface getUltimaCobranca() {
		Cobranca ultima = null;
		for (Cobranca cobranca : this.cobrancas) {
			if(ultima == null)
				ultima = cobranca;
			else{
				if(0 < Utils.compareData(cobranca.getDataVencimento(),ultima.getDataVencimento()))
					ultima = cobranca;
			}
		}
		return ultima;
	}
	
	public Consignacao getUltimaConsignacao() {
		Consignacao ultima = null;
		for (Consignacao consignacao : this.consignacoes) {
			if(ultima == null)
				ultima = consignacao;
			else{
				if(0 < Utils.compareData(consignacao.getDataVencimento(),ultima.getDataVencimento()))
					ultima = consignacao;
			}
		}
		return ultima;
	}
	
	public Consignacao getPrimeiraConsignacao() {
		Consignacao primeira = null;
		for (Consignacao consignacao : this.consignacoes) {
			if(primeira == null)
				primeira = consignacao;
			else{
				if(0 > Utils.compareData(consignacao.getDataDoCredito(),primeira.getDataDoCredito()))
					primeira = consignacao;
			}
		}
		return primeira;
	}

	public Consignacao getPrimeiraConsignacaoPaga() {
		Consignacao primeira = null;
		
		for (Consignacao consignacao : this.consignacoes) {
			if(consignacao.getStatusConsignacao() == 'P' && consignacao.getDataDoCredito().compareTo(dataAdesao) >= 0){
				if(primeira == null) {
					primeira = consignacao;
				} else if (0 > Utils.compareData(consignacao.getDataDoCredito(), primeira.getDataDoCredito())){
					primeira = consignacao;
				}
			}
		}
		
		return primeira;
	}
	
	public abstract void updateDataInicioCarencia();
		
	
	public Date getDataDoPrimeiroPagamento() {
		Consignacao 		primeiraConsignacaoPaga = this.getPrimeiraConsignacaoPaga();
		CobrancaInterface 	primeiraCobrancaPaga	= this.getPrimeiraCobrancaPaga();
		
		if(primeiraConsignacaoPaga != null) {
			return primeiraConsignacaoPaga.getDataDoCredito();
		}else if(primeiraCobrancaPaga != null){
			return primeiraCobrancaPaga.getDataPagamento();
		}
		
		return null;
	}
	
	public Date getDataDoPrimeiroPagamentoCobranca() {
		CobrancaInterface primeiraCobrancaPaga = this.getPrimeiraCobrancaPaga();
		
		if (primeiraCobrancaPaga != null) {
			return primeiraCobrancaPaga.getDataPagamento();
		}

		return null;
	}

	public abstract Set<DependenteSR> getDependentes();
	
	public Set<DependenteSR> getDependentes(String ... situacoes){
		List<String> situacoesList = Arrays.asList(situacoes);
		Set<DependenteSR> dependentes = new HashSet<DependenteSR>();
		
		for (DependenteSR dependente : getDependentes()) {
			if(situacoesList.contains(dependente.getSituacao().getDescricao())){
				dependentes.add(dependente);
			}
		}
		
		return dependentes;
	}
	
	/**
	 * Método que fornece uma coleção de dependentes do titular em questão.
	 * O método busca todos o dependentes na situação 'Ativo(a)', bem como os na situação 'Cancelado(a)' cujo
	 * cancelamento ocorreu na competência informada.
	 * Nesse último caso, serão considerados apenas os dependentes que possuem coparticipação maior que zero.
	 * Se a competência não for informada, busca-se apenas os dependentes na situação 'Ativo(a)'.
	 * @param competencia
	 * @return Set<DependenteSR>
	 */
	public Set<DependenteSR> getDependentesCobranca(Date competencia){
		Set<DependenteSR> dependentesCobranca = new HashSet<DependenteSR>();
		dependentesCobranca.addAll(getDependentes(SituacaoEnum.ATIVO.descricao()));
		return dependentesCobranca;
	}

	@Override
	public boolean isCumpriuCarencia(Integer diasDeCarencia) {
		try {
			if(getCarenciaCumprida() >= diasDeCarencia) {
				return true;
			}else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public void tocarObjetos() {
		super.tocarObjetos();
		if (consignacoes != null) {
			this.consignacoes.size();
		}
		if (matriculas != null) {
			this.matriculas.size();
		}
		if (cobrancas != null) {
			this.cobrancas.size();
		}
		if (this.getConsultasPromocionais() != null) {
			this.getConsultasPromocionais().size();
		}
	}
 
	public String getIdentificacao() {
		return this.getPessoaFisica().getCpf();
	}

	public String getNome() {
		return this.getPessoaFisica().getNome();
	}

	public String getTipo() {
		return null;
	}
	
	@Override
	public void reativar(String motivo, Date dataAdesao, UsuarioInterface usuario) throws Exception {
		if (!this.isSeguradoDependenteSuplementar() && this.getMatriculas().isEmpty()){
			throw new Exception("Não é permitido ativar um " + this.getTipoDeSegurado() + " que não possua matrículas." );
		}
		
		super.reativar(motivo, dataAdesao, usuario);
	}
	
	public void reativarTitularEDependente(UsuarioInterface usuario, Date dataInicioCarencia, String motivo, Date dataSituacao){
		Date dataSituacaoSuspensoTitular 	= this.getSituacao().getDataSituacao();
		String motivoSuspensaoTitular		= this.getSituacao().getMotivo();
		
		// Ativa o titular caso ele tenha sido suspenso
		this.mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(), motivo, dataSituacao);
		// Seta data de inicio de carência caso seja o primeiro pagamento
		if(this.getInicioDaCarencia() == null) {
			this.setInicioDaCarencia(dataInicioCarencia);
		}
		
		for (DependenteSR dependente : this.getDependentes()) {
			if(dependente.isSituacaoAtual(SituacaoEnum.SUSPENSO.descricao())){

				Date dataSituacaoSuspensoDependente = dependente.getSituacao().getDataSituacao();
				String motivoSuspensaoDependete		= dependente.getSituacao().getMotivo(); 

				boolean foiSuspensoNoMesmoDia 		= Utils.compareData(dataSituacaoSuspensoTitular, dataSituacaoSuspensoDependente) == 0;
				boolean foiSuspensoPeloMesmoMotivo 	= motivoSuspensaoDependete.equalsIgnoreCase(motivoSuspensaoTitular);
				
				if (foiSuspensoNoMesmoDia && foiSuspensoPeloMesmoMotivo) { 
					dependente.mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(), motivo, dataSituacao);
					if(dependente.getInicioDaCarencia() == null) {
						dependente.setInicioDaCarencia(dataInicioCarencia);
					}
				}
			}
		}
	}
	
	/**
	 * Método que calcula o valor de financiamento do titular
	 * @param salarioBase
	 * @return um Array de BigDecimal onde a primeira posição é a alíquota
	 * e a segunda posição é o valor de financiamento do titular.
	 */
	public BigDecimal[] getValorFinanciamento(BigDecimal salarioBase){
		BigDecimal aliquota_financiamento = PainelDeControle.getPainel().getAliquotaDeFinanciamento();
		BigDecimal[] aliquotaValor = new BigDecimal[2];
		if(salarioBase.compareTo(BigDecimal.ZERO) > 0)
			aliquotaValor[0] = aliquota_financiamento;
		else
			aliquotaValor[0] = BigDecimal.ZERO;

		BigDecimal valorFinanciamento = salarioBase.multiply(aliquota_financiamento.divide(new BigDecimal(100))).setScale(2, BigDecimal.ROUND_HALF_UP);
		
		BigDecimal pisoFinanciamento = PisoFinanciamentoTitular.getPisoPorFaixaSalarial(salarioBase);
		if(valorFinanciamento.compareTo(pisoFinanciamento) < 0)
			valorFinanciamento = pisoFinanciamento;
		
		aliquotaValor[1] = valorFinanciamento; 
		return aliquotaValor;
	}


	public static void main(String[] args) {
		
		BigDecimal valorFinanciamento = new BigDecimal(10.00);
		BigDecimal pisoFinanciamento= new BigDecimal(35.00);
		if(valorFinanciamento.compareTo(pisoFinanciamento) < 0)
			System.out.println(pisoFinanciamento);
		else System.out.println(valorFinanciamento);
	}
	
	
	public BigDecimal getMaiorSalario(){
		BigDecimal valorSalario = BigDecimal.ZERO;
		valorSalario = this.getMatriculaMaiorSalario(0).getSalario();
		return valorSalario;
	}
	
	/**
	 * Método que calcula o valor de financiamento do titular mais os seus dependentes
	 * @param salarioBase
	 * @return Array de BigDecimal onde a primeira posição é a alíquota
	 * e a segunda posição é o valor de financiamento do titular somado com
	 * o dos seus dependentes
	 * @changes Patrícia
	 */
	public BigDecimal[] getValorTotalFinanciamento(BigDecimal salarioBase, Date dataBase){
		BigDecimal[] valorTotal = {BigDecimal.ZERO, BigDecimal.ZERO};
		
		BigDecimal[] valorTitular = this.getValorFinanciamento(salarioBase);
		valorTotal[0] = valorTotal[0].add(valorTitular[0]);
		valorTotal[1] = valorTotal[1].add(valorTitular[1]);
		
		for (DependenteInterface dependente : this.getDependentes()) {
			BigDecimal[] valorDependentes = dependente.getValorFinanciamentoDependente(salarioBase, dataBase);
			valorTotal[0] = valorTotal[0].add(valorDependentes[0]);
			if(dependente.isSituacaoAtual(SituacaoEnum.ATIVO.descricao())) {
			valorTotal[1] = valorTotal[1].add(valorDependentes[1]);			
		}
		}
		
		return valorTotal;
		
	}

	public Consignacao getConsignacao(Date competencia) {
		for (Consignacao consignacao : this.consignacoes) {
			if(Utils.compareData(consignacao.getCompetencia(), competencia) == 0){
				return consignacao;
			}
		}
		return null;
	}

	public Integer getOrdemDependente(DependenteInterface dependente){
		Map<Integer, DependenteInterface> dependentesOrdenados = this.ordenaDependentes();
		int ordem = 0;
		for (DependenteInterface dep : dependentesOrdenados.values()) {
			ordem ++;
			if(dep.equals(dependente)){
				return ordem;
			}
		}
		return ordem;
	}
	
	/**
	 * Método que ordena os dependentes pelo atributo <code>dataAdesao</code>
	 * @return um mapa com a ordem como chave do mapa e o dependente como valor do mapa
	 */
	public Map<Integer, DependenteInterface> ordenaDependentes() {
		Map<Integer,DependenteInterface> ordemDependentes = new HashMap<Integer, DependenteInterface>();
		int countDeps = 1;
		for(DependenteInterface dependente : this.getDependentes()){
			if(dependente.isSituacaoAtual(SituacaoEnum.ATIVO.descricao())){
				DependenteInterface dependenteEspera = dependente;
				List<Integer> chaves = new ArrayList<Integer>(ordemDependentes.keySet());
				Collections.sort(chaves);

				for(Integer key : chaves){
					DependenteInterface dependenteKey = ordemDependentes.get(key);

					boolean minDataAdesao = dependenteEspera.getDataAdesao() != null ? dependenteKey.getDataAdesao() != null ?  dependenteEspera.getDataAdesao().compareTo(dependenteKey.getDataAdesao()) < 0 :false: false;
					boolean eqDataAdesao = dependenteEspera.getDataAdesao() != null ? dependenteKey.getDataAdesao() != null ?  dependenteEspera.getDataAdesao().compareTo(dependenteKey.getDataAdesao()) == 0 :false: dependenteKey.getDataAdesao() != null ? false : true;
					
					if(minDataAdesao){
						ordemDependentes.put(key, dependenteEspera);
						dependenteEspera = dependenteKey;
					}else{
						if((dependenteEspera.getDataAdesao() != null && dependenteKey.getDataAdesao() == null) ||
							(eqDataAdesao && dependenteEspera.getPessoaFisica().getDataNascimento().compareTo(dependenteKey.getPessoaFisica().getDataNascimento()) < 0)){
							ordemDependentes.put(key, dependenteEspera);
							dependenteEspera = dependenteKey;
						}
					}
					
				}
				ordemDependentes.put(countDeps++, dependenteEspera);
			}
		}
		return ordemDependentes;
	}
	
	/**
	 * Retorna um objeto DetalheContaTitular para da competência informada, caso
	 * não exista, é instanciado um objeto com essa competência e retornado. 
	 */
	public DetalheContaTitular getDetalheContaTitular(Date competencia) {
		DetalheContaTitular detalhe = null;
		for (DetalheContaTitular det : this.getDetalhesContaTitular()) {
			if (Utils.compararCompetencia(det.getCompetencia(), competencia) == 0)
				detalhe = det;
		}

		if (detalhe != null) {
			return detalhe;
		}
		
		else
			return new DetalheContaTitular(competencia, this);
	}
	
	public Set<DetalheContaTitular> getDetalhesContaTitular() {
		return detalhesContaTitular;
	}

	public void setDetalhesContaTitular(Set<DetalheContaTitular> detalhesContaTitular) {
		this.detalhesContaTitular = detalhesContaTitular;
	}

	public void addDetalhe(DetalheContaTitular detalhe){
		this.detalhesContaTitular.add(detalhe);
		detalhe.setTitular(this);
	}
	
	public void removeDetalheConta(DetalheContaTitular detalhe){
		this.detalhesContaTitular.remove(detalhe);
		detalhe.setTitular(null);
	}
	/**
	 * Metódo que procura na coleção <code>Matricula matriculas</code> do segurado
	 * pela matricula de maior salario bruto.
	 * 
	 * @param tipoPagamento -> Se o tipo de pagamento for especificado ele retorna
	 *  a matricula de maior salário de acordo com a condição informada, caso contrário 
	 *  (igual a zero), ele faz a busca entre todas as matriculas.
	 *  
	 * @return retorna a matricula que contem o maior salario bruto do Titular.
	 */
	public AbstractMatricula getMatriculaMaiorSalario(Integer tipoPagamento) {
		AbstractMatricula matriculaMaiorSalario = null;
		
		if (this.matriculas.isEmpty()){
			return null;
		}
			
		for (AbstractMatricula matricula : this.matriculas) {
			if(tipoPagamento.equals(0) || tipoPagamento.equals(matricula.getTipoPagamento())) {				
				if (matriculaMaiorSalario == null){
					matriculaMaiorSalario = matricula;
				} else if(matricula.getSalario().compareTo(matriculaMaiorSalario.getSalario()) > 0) {
					matriculaMaiorSalario = matricula;
				}
			}
		}
		
		return matriculaMaiorSalario;
	}
	
	public Conta getConta(Date competencia,BigDecimal valorConta) {
		ContaConsignacao contaConsignacao = null;
		boolean isMesmaCompetencia;
		for (Consignacao consignacao : this.consignacoes) {
			for (ContaInterface conta : consignacao.getContas()) {
				contaConsignacao = (ContaConsignacao) conta;
				isMesmaCompetencia = Utils.compararCompetencia(contaConsignacao.getCompetencia(), competencia)==0; 
				if (isMesmaCompetencia) {
					return contaConsignacao;
				}
				
			}
		}
		return null;
	}
	
	public Set<AbstractMatricula> getMatriculasAtivas() {
		Set<AbstractMatricula> matriculasAtivas = new HashSet<AbstractMatricula>();
		for (AbstractMatricula matricula : this.matriculas) {
			if(matricula.isAtiva()) {
				matriculasAtivas.add(matricula);
			}
		}
		
		return matriculasAtivas;
	}

	/**
	 * Zera os valores dos detalhes da cobrança do titular e seus dependentes em uma competência
	 * @param competencia
	 */
	public void zerarDetalhesCobranca(Date competencia) {
		this.getDetalheContaTitular(competencia).zerarValoresDetalhe();
		for (DependenteSR dependente : getDependentesCobranca(competencia)) {
			dependente.getDetalheContaDependente(competencia).zerarValoresDetalhe();
		}
	}
	
	public BigDecimal getSalarioTotal(){
		BigDecimal salario = BigDecimal.ZERO;
		for (AbstractMatricula matricula : getMatriculasAtivas()) {
			salario = salario.add(matricula.getSalario());
		}
		return MoneyCalculation.rounded(salario);
	}
	
}
