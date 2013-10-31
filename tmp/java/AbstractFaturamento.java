package br.com.infowaypi.ecarebc.financeiro.faturamento;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.com.infowaypi.ecare.services.recurso.GuiaRecursoGlosa;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Prestador.TipoIncremento;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.FluxoFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.TitularFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.arquivos.ArquivoDeEnvio;
import br.com.infowaypi.ecarebc.financeiro.conta.ComponenteColecaoContas;
import br.com.infowaypi.ecarebc.financeiro.conta.Conta;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ordenador.Ordenador;
import br.com.infowaypi.ecarebc.financeiro.faturamento.retencao.AbstractImposto;
import br.com.infowaypi.ecarebc.financeiro.faturamento.retencao.Imposto;
import br.com.infowaypi.ecarebc.financeiro.faturamento.retencao.Retencao;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.financeiro.InformacaoFinanceiraInterface;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author Marcus bOolean
 *
 */
public abstract class AbstractFaturamento extends ImplColecaoSituacoesComponent implements FluxoFinanceiroInterface{
	
	public static enum StatusFaturamentoEnum {
		ABERTO(Constantes.FATURAMENTO_ABERTO, SituacaoEnum.ABERTO.descricao()),
		FECHADO(Constantes.FATURAMENTO_FECHADO, SituacaoEnum.FECHADO.descricao()),
		PAGO(Constantes.FATURAMENTO_PAGO, SituacaoEnum.PAGO.descricao()),
		CANCELADO(Constantes.FATURAMENTO_CANCELADO, SituacaoEnum.CANCELADO.descricao());
		
		private Character codigo;
		private String descricao;
		
		private StatusFaturamentoEnum(Character codigo, String descricao) {
			this.codigo = codigo;
			this.descricao = descricao;
		}
		
		public Character getCodigo() {
			return codigo;
		}
		
		public String getDescricao() {
			return descricao;
		}
		
		public static StatusFaturamentoEnum getStatus(Character codigo) {
			StatusFaturamentoEnum[] statusFaturamentos = StatusFaturamentoEnum.values();
			for (StatusFaturamentoEnum status : statusFaturamentos) {
				if (status.getCodigo().equals(codigo)) {
					return status;
				}
			}
			throw new RuntimeException("Não existe status de faturamento correspondente com o código " + codigo + ".");
		}
	}
	
	public static final String FATURAMENTO_NORMAL 	= "Faturamento Normal";
	public static final String FATURAMENTO_PASSIVO 	= "Faturamento Passivo";
	public static final String TIPO_FATURAMENTO_SR 	= "FaturamentoSR";
	
	private static final long serialVersionUID = 1L;
	private Long idFluxoFinanceiro;
	private Date competencia;
	private Date dataGeracao;
	private Date dataPagamento;
	
	private BigDecimal valorBruto 		= BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP);
	private BigDecimal valorOutros 		= BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP);
	private BigDecimal valorLiquido 	= BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP);
	private BigDecimal valorIss 		= BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP);
	private BigDecimal valorInss 		= BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP);
	private BigDecimal valorImpostoDeRenda = BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP);
	
	private BigDecimal valorConsultas 		= BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP);
	private BigDecimal valorConsultasOdonto = BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP);
	private BigDecimal valorExames 			= BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP);
	private BigDecimal valorExamesOdonto 	= BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP);
	private BigDecimal valorInternacoes		= BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP);
	private BigDecimal valorAtendimentosUrgencia = BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP);
	private BigDecimal valorRecursosDeferidos    = BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP);
	
	private int quantidadeConsultas;
	private int quantidadeConsultasOdonto;
	private int quantidadeExames;
	private int quantidadeExamesOdonto;
	private int quantidadeAtendimentosUrgencia;
	private int quantidadeInternacoes;
	
	private Prestador prestador;
	private Profissional profissional;
	private Set<Retencao> retencoes		= new HashSet<Retencao>();
	private Set<GuiaFaturavel> guias	= new HashSet<GuiaFaturavel>();
	private char status 				= Constantes.FATURAMENTO_ABERTO;
	private String pisPasep;
	private String nome;
	private String motivoValorOutros;
	private int categoria;
	private Integer tipoPessoa = AbstractImposto.PESSOA_JURIDICA;
	private ResumoGuias resumoGuias;
	private ResumoProcedimentos resumoProcedimentos;
	private InformacaoFinanceiraInterface informacaoFinanceira;
	private ComponenteColecaoContas 	colecaoContas 			= new ComponenteColecaoContas();
	private Set<HonorarioMedico> 		honorariosMedicos 		= new HashSet<HonorarioMedico>();
	private Set<AlteracaoFaturamento> 	alteracoesFaturamento 	= new HashSet<AlteracaoFaturamento>();
	private boolean geradoPosteriormente;
	private String motivoGeracaoPosterior;
	private String numeroEmpenho;
	private List<OcorrenciaFaturamento> ocorrencias;
	private Ordenador ordenador;
	private Set<ItemGuiaFaturamento> 	itensGuiaFaturamento;
	
	//não persistidos
	private BigDecimal valorProcedimentosGlosados = new BigDecimal(0f);
	
	public  AbstractFaturamento() {
		super();
		this.ocorrencias = new ArrayList<OcorrenciaFaturamento>();
		this.itensGuiaFaturamento = new HashSet<ItemGuiaFaturamento>();
	}
	
	public AbstractFaturamento(Date competencia, char status, Prestador prestador) {
		this();
		this.setCategoria(prestador.getTipoPrestador());
		this.setCompetencia(competencia);
		this.setInformacaoFinanceira(prestador.getInformacaoFinanceira());
		this.setPrestador(prestador);
		this.setPisPasep(prestador.getPessoaJuridica().getPispasep());
		this.setNome(Utils.normalize(prestador.getPessoaJuridica().getFantasia()));
		this.setTipoPessoa(prestador.isPessoaFisica() ? AbstractImposto.PESSOA_FISICA : AbstractImposto.PESSOA_JURIDICA);
		this.setDataGeracao(new Date());
		this.setStatus(status);
	}
	
	public BigDecimal getValorMedioPorProcedimento(){
		BigDecimal valorTotalDeProcedimentos 		=  new BigDecimal(this.getResumoProcedimentos().getValorTotalProcedimentos());
		BigDecimal quantidadeTotalDeProcedimentos 	=  new BigDecimal(this.getResumoProcedimentos().getQuantidadeTotalProcedimentos());
		return MoneyCalculation.divide(valorTotalDeProcedimentos,quantidadeTotalDeProcedimentos.floatValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	public BigDecimal getValorBrutoSemAlteracoes(){
		return this.getValorBruto().subtract(this.getValorSaldoAlteracoes());
	}

	public BigDecimal getValorSaldoAlteracoes(){
		return this.getValorTotalAlteracaoIncremento().subtract(this.getValorTotalAlteracaoDecremento());
	}
	
	public BigDecimal getValorTotalAlteracaoIncremento(){
		BigDecimal valor = BigDecimal.ZERO;
		for (AlteracaoFaturamento alteracao : this.getAlteracoesFaturamentoAtivos()) {
			valor = valor.add(alteracao.getValorIncremento());
		}
		return valor;
	}
	
	public BigDecimal getValorTotalAlteracaoDecremento(){
		BigDecimal valor = BigDecimal.ZERO;
		for (AlteracaoFaturamento alteracao : this.getAlteracoesFaturamentoAtivos()) {
			valor = valor.add(alteracao.getValorDecremento());
		}
		return valor;
	}
	
	public int getQuantidadeTotalGuias(){
		return this.getGuias().size();
	}
	
	public Set<HonorarioMedico> getHonorariosMedicos() {
		return honorariosMedicos;
	}

	public void setHonorariosMedicos(Set<HonorarioMedico> honorariosMedicos) {
		this.honorariosMedicos = honorariosMedicos;
	}

	public int getQuantidadeConsultas() {
		return quantidadeConsultas;
	}

	public void setQuantidadeConsultas(int quantidadeConsultas) {
		this.quantidadeConsultas = quantidadeConsultas;
	}

	public int getQuantidadeConsultasOdonto() {
		return quantidadeConsultasOdonto;
	}

	public void setQuantidadeConsultasOdonto(int quantidadeConsultasOdonto) {
		this.quantidadeConsultasOdonto = quantidadeConsultasOdonto;
	}

	public int getQuantidadeExames() {
		return quantidadeExames;
	}

	public void setQuantidadeExames(int quantidadeExames) {
		this.quantidadeExames = quantidadeExames;
	}

	public int getQuantidadeExamesOdonto() {
		return quantidadeExamesOdonto;
	}

	public void setQuantidadeExamesOdonto(int quantidadeExamesOdonto) {
		this.quantidadeExamesOdonto = quantidadeExamesOdonto;
	}

	public int getQuantidadeAtendimentosUrgencia() {
		return quantidadeAtendimentosUrgencia;
	}

	public void setQuantidadeAtendimentosUrgencia(int quantidadeAtendimentosUrgencia) {
		this.quantidadeAtendimentosUrgencia = quantidadeAtendimentosUrgencia;
	}

	public int getQuantidadeInternacoes() {
		return quantidadeInternacoes;
	}

	public void setQuantidadeInternacoes(int quantidadeInternacoes) {
		this.quantidadeInternacoes = quantidadeInternacoes;
	}

	public BigDecimal getValorConsultas() {
		return valorConsultas;
	}

	public void setValorConsultas(BigDecimal valorConsultas) {
		this.valorConsultas = valorConsultas;
	}

	public BigDecimal getValorConsultasOdonto() {
		return valorConsultasOdonto;
	}

	public void setValorConsultasOdonto(BigDecimal valorConsultasOdonto) {
		this.valorConsultasOdonto = valorConsultasOdonto;
	}

	public BigDecimal getValorExames() {
		return valorExames;
	}

	public void setValorExames(BigDecimal valorExames) {
		this.valorExames = valorExames;
	}

	public BigDecimal getValorExamesOdonto() {
		return valorExamesOdonto;
	}

	public void setValorExamesOdonto(BigDecimal valorExamesOdonto) {
		this.valorExamesOdonto = valorExamesOdonto;
	}

	public BigDecimal getValorAtendimentosUrgencia() {
		return valorAtendimentosUrgencia;
	}

	public void setValorAtendimentosUrgencia(BigDecimal valorAtendimentosUrgencia) {
		this.valorAtendimentosUrgencia = valorAtendimentosUrgencia;
	}

	public BigDecimal getValorInternacoes() {
		return valorInternacoes;
	}

	public void setValorInternacoes(BigDecimal valorInternacoes) {
		this.valorInternacoes = valorInternacoes;
	}

	public Set<AlteracaoFaturamento> getAlteracoesFaturamento() {
		return this.alteracoesFaturamento;
	}
	
	public Set<AlteracaoFaturamento> getAlteracoesFaturamentoAtivos() {
		Set<AlteracaoFaturamento> alteracoes = new HashSet<AlteracaoFaturamento>();
		for (AlteracaoFaturamento alteracao : this.alteracoesFaturamento) {
			if(alteracao.getStatus().equals(AlteracaoFaturamento.STATUS_ATIVO)) {
				alteracoes.add(alteracao);
			}
		}
		return alteracoes;
	}

	public void setAlteracoesFaturamento(Set<AlteracaoFaturamento> alteracoesFaturamento) {
		this.alteracoesFaturamento = alteracoesFaturamento;
	}

	public void atualizarSomatorioGuias(GuiaFaturavel guia, BigDecimal valorAtualizador, TipoIncremento tipoDeIncremento){
		if(tipoDeIncremento.equals(TipoIncremento.POSITIVO)){
			if (guia.isAtendimentoUrgencia() || guia.isConsultaUrgencia()){
				this.setValorAtendimentosUrgencia(this.getValorAtendimentosUrgencia().add(valorAtualizador));
			} else if (guia.isInternacao()) {
				this.setValorInternacoes(this.getValorInternacoes().add(valorAtualizador));
			}
		}
		else if(tipoDeIncremento.equals(TipoIncremento.NEGATIVO)){
			if (guia.isAtendimentoUrgencia() || guia.isConsultaUrgencia()){
				this.setValorAtendimentosUrgencia(this.getValorAtendimentosUrgencia().subtract(valorAtualizador));
			} else if (guia.isInternacao()) {
				this.setValorInternacoes(this.getValorInternacoes().subtract(valorAtualizador));
			}
		}
	}
	
	public void setSomatoriosGuias(GuiaFaturavel guia) {
		if (guia.isConsultaOdonto() || guia.isConsultaOdontoUrgencia()) {
			BigDecimal valor = this.getValorConsultasOdonto().add(guia.getValorTotal());
			this.setValorConsultasOdonto(valor);
			this.setQuantidadeConsultasOdonto(this.getQuantidadeConsultasOdonto() + 1);
		} else if (guia.isConsulta()) {
			BigDecimal valor = this.getValorConsultas().add(guia.getValorTotal());
			this.setValorConsultas(valor);
			this.setQuantidadeConsultas(this.getQuantidadeConsultas() + 1);
		} else if (guia.isExameOdonto()) {
			BigDecimal valor = this.getValorExamesOdonto().add(guia.getValorTotal());
			this.setValorExamesOdonto(valor);
			this.setQuantidadeExamesOdonto(this.getQuantidadeExamesOdonto() + 1);
		} else if (guia.isExame()) {
			BigDecimal valor = this.getValorExames().add(guia.getValorTotal());
			this.setValorExames(valor);
			this.setQuantidadeExames(this.getQuantidadeExames() + 1);
		} else if (guia.isAtendimentoUrgencia() || guia.isConsultaUrgencia()) {
			BigDecimal valor = this.getValorAtendimentosUrgencia().add(guia.getValorTotal());
			this.setValorAtendimentosUrgencia(valor);
			this.setQuantidadeAtendimentosUrgencia(this.getQuantidadeAtendimentosUrgencia() + 1);
		} else if (guia.isInternacao()) {
			BigDecimal valor = this.getValorInternacoes().add(guia.getValorTotal());
			this.setValorInternacoes(valor);
			this.setQuantidadeInternacoes(this.getQuantidadeInternacoes() + 1);
		}
	}
	
	public Retencao addRetencao(Retencao retencao) {
		Retencao retencaoEncontrada = this.getRetencao(retencao.getTipoDeRetencao());
		if(retencaoEncontrada == null){  // NOVA RETENCAO.
			this.getRetencoes().add(retencao);
			retencao.setFaturamento(this);
			retencaoEncontrada = retencao;
		}
		else{ // ATUALIZAR RETENCAO.
			retencaoEncontrada.setValorBaseDoCalculo(retencao.getValorBaseDoCalculo());
			retencaoEncontrada.setValorDeducaoBaseDoCalculo(retencao.getValorDeducaoBaseDoCalculo());
			retencaoEncontrada.setPercentualDoCalculo(retencao.getPercentualDoCalculo());
			retencaoEncontrada.setValor(retencao.getValorTotal());
		}
		return retencaoEncontrada;
	}
	
	public Retencao getRetencao(Integer tipoDeRetencao) {
		for (Retencao retencao: this.getRetencoes()) {
			if(retencao.getTipoDeRetencao().equals(tipoDeRetencao))
				return retencao;
		}
		return null;
	}
	
	public String getCompetenciaFormatada(){
		return Utils.format(competencia, "MM/yyyy");
	}
	
	public String getDescricaoCategoria() {
		if(this.categoria == Constantes.TIPO_PRESTADOR_TODOS)
			return "Todos";
		
		if(this.categoria == Constantes.TIPO_PRESTADOR_CLINICAS_DE_EXAMES)
			return "Clínica de Exames";

		if(this.categoria == Constantes.TIPO_PRESTADOR_CLINICAS_AMBULATORIAIS)
			return "Clínica Ambulatorial";

		if(this.categoria == Constantes.TIPO_PRESTADOR_CLINICAS_DE_ODONTOLOGIA)
			return "Clínica de Odontologia";

		if(this.categoria == Constantes.TIPO_PRESTADOR_DENTISTAS)
			return "Dentistas";
		
		if(this.categoria == Constantes.TIPO_PRESTADOR_HOSPITAL)
			return "Hospital";

		if(this.categoria == Constantes.TIPO_PRESTADOR_LABORATORIO)
			return "Laboratório";

		if(this.categoria == Constantes.TIPO_PRESTADOR_MEDICOS)
			return "Médicos Credenciados";

		if(this.categoria == Constantes.TIPO_PRESTADOR_ANESTESISTA)
			return "Anestesista";

		if(this.categoria == Constantes.TIPO_PRESTADOR_OUTROS)
			return "Outros Profissionais";
		
		return "Outras Categorias";
	}
	
	public String getDescricaoStatus(){
		if (this.status == Constantes.FATURAMENTO_ABERTO){
			return "Aberto";
		}
		
		if (this.status == Constantes.FATURAMENTO_CANCELADO){
			return "Cancelado";
		}
		
		if (this.status == Constantes.FATURAMENTO_FECHADO){
			return "Fechado";
		}
		
		if (this.status == Constantes.FATURAMENTO_EM_DEBITO){
			return "Em Débito";
		}
		return "Pago";
	}
	
	public void processarRetencoes() throws Exception {
		
		Prestador prestador = this.getPrestador(); 
		if(prestador == null) return;
		
		Integer tipoDePessoa = prestador.isPessoaFisica() ? AbstractImposto.PESSOA_FISICA : AbstractImposto.PESSOA_JURIDICA;
		BigDecimal valorBase = MoneyCalculation.rounded(this.getValorBruto().add(this.getValorOutros()));

		Retencao retencao = null;
		
		if(prestador.isPagaINSS()){
			retencao = (Retencao) Imposto.getRetencao(Constantes.INSS, valorBase, tipoDePessoa);
			if(retencao != null){
				retencao = this.addRetencao(retencao);
				this.setValorInss(retencao.getValorTotal());
				valorBase = valorBase.subtract(retencao.getValorTotal());
			}
		}else{
			this.setValorInss(BigDecimal.ZERO);
		}
		
		if(prestador.isPagaIR()){
			retencao = (Retencao) Imposto.getRetencao(Constantes.IMPOSTO_DE_RENDA, valorBase, tipoDePessoa);
			if(retencao != null){
				retencao = this.addRetencao(retencao);
				this.setValorImpostoDeRenda(retencao.getValorTotal());
				valorBase = valorBase.subtract(retencao.getValorTotal());
			}
		}else{
			this.setValorImpostoDeRenda(BigDecimal.ZERO);
		}
		
		if(prestador.isPagaIss()){
			BigDecimal valorBaseIss = MoneyCalculation.rounded(this.getValorBruto().add(this.getValorOutros()));
			valorBaseIss.setScale(2, BigDecimal.ROUND_HALF_UP);
			retencao = Imposto.getRetencao(Constantes.ISS, valorBaseIss, tipoDePessoa);
			if(retencao != null){
				retencao = this.addRetencao(retencao);
				this.setValorIss(retencao.getValorTotal());
				valorBase = valorBase.subtract(retencao.getValorTotal());
			}
		}else {
			this.setValorIss(BigDecimal.ZERO);
		}
		
		this.valorLiquido = valorBase;
	}
	
	public void addGuia(GuiaFaturavel guia) {
		this.getGuiasFaturaveis().add(guia);
		guia.setFaturamento(this);
	}
	
	public void addItemGuiaFaturamento(ItemGuiaFaturamento itemGuiaFaturamento) {
		this.getItensGuiaFaturamento().add(itemGuiaFaturamento);
		itemGuiaFaturamento.setFaturamento(this);
	}
	
	public void gerarContas(ArquivoDeEnvio arquivo, UsuarioInterface usuario) throws Exception {
		Conta novaConta = null;
		if(this.getColecaoContas().getContas().isEmpty()){
			novaConta = new Conta();

			if((this.getValorLiquido().compareTo(BigDecimal.ZERO) < 0))
				return;
			
			novaConta.setArquivoEnvio(arquivo);
			novaConta.setColecaoContas(this.getColecaoContas());
			novaConta.setDataVencimento(new Date());
			novaConta.setCompetencia(this.getCompetencia());
			novaConta.setValorCobrado(MoneyCalculation.rounded(this.getValorLiquido()));
			ImplDAO.save(novaConta);
			
			if (arquivo != null){
				arquivo.populate(novaConta,this.getPrestador().getInformacaoFinanceira());
			}
		}
	}
	
	public void tocarObjetos(){
		this.getGuiasFaturaveis().size();
		this.getRetencoes().size();
		this.getColecaoContas().getContas().size();
		this.getAlteracoesFaturamento().size();
		this.getHonorariosMedicos().size();
		this.getGuiasRecursoGlosa().size();
	}
	
	public int getCategoria() {
		return this.categoria;
	}
	public void setCategoria(int categoria) {
		this.categoria = categoria;
	}
	public Date getCompetencia() {
		return competencia;
	}
	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}
	public Date getDataGeracao() {
		return dataGeracao;
	}
	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}
	@Deprecated
	public Set<GuiaSimples> getGuias() {
		throw new UnsupportedOperationException("Operação inválida!");
	}
	@Deprecated
	public void setGuias(Set<GuiaSimples> guias) {
		throw new UnsupportedOperationException("Operação inválida!");
	}
	public Set<GuiaFaturavel> getGuiasFaturaveis() {
		return this.guias; 
	}
	public void setGuiasFaturaveis(Set<GuiaFaturavel> guias) {
		this.guias = guias;
	}
	public String getMotivoValorOutros() {
		return motivoValorOutros;
	}
	public void setMotivoValorOutros(String motivoValorOutros) {
		this.motivoValorOutros = motivoValorOutros;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getPisPasep() {
		return pisPasep;
	}
	public void setPisPasep(String pisPasep) {
		this.pisPasep = pisPasep;
	}
	public Prestador getPrestador() {
		return prestador;
	}
	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
	}
	public Profissional getProfissional() {
		return profissional;
	}
	public void setProfissional(Profissional profissional) {
		this.profissional = profissional;
	}
	public ResumoGuias getResumoGuias() {
		return resumoGuias;
	}
	public void setResumoGuias(ResumoGuias resumoGuias) {
		this.resumoGuias = resumoGuias;
	}
	public ResumoProcedimentos getResumoProcedimentos() {
		if(resumoProcedimentos == null){
			resumoProcedimentos = new ResumoProcedimentos(new ArrayList(guias));
		}
		return resumoProcedimentos;
	}
	public void setResumoProcedimentos(ResumoProcedimentos resumoProcedimentos) {
		this.resumoProcedimentos = resumoProcedimentos;
	}
	public Set<Retencao> getRetencoes() {
		return retencoes;
	}
	public void setRetencoes(Set<Retencao> retencoes) {
		this.retencoes = retencoes;
	}
	public char getStatus() {
		return status;
	}
	public void setStatus(char status) {
		this.status = status;
	}
	public Integer getTipoPessoa() {
		return tipoPessoa;
	}
	public void setTipoPessoa(Integer tipoDePessoa) {
		this.tipoPessoa = tipoDePessoa;
	}
	public BigDecimal getValorBruto() {
		return valorBruto;
	}
	public void setValorBruto(BigDecimal valorBruto) {
		this.valorBruto = valorBruto;
	}
	public BigDecimal getValorImpostoDeRenda() {
		return valorImpostoDeRenda;
	}
	public void setValorImpostoDeRenda(BigDecimal valorImpostoDeRenda) {
		this.valorImpostoDeRenda = valorImpostoDeRenda;
	}
	public BigDecimal getValorInss() {
		return valorInss;
	}
	public void setValorInss(BigDecimal valorInss) {
		this.valorInss = valorInss;
	}
	public BigDecimal getValorIss() {
		return valorIss;
	}
	public void setValorIss(BigDecimal valorIss) {
		this.valorIss = valorIss;
	}
	public BigDecimal getValorLiquido() {
		return valorLiquido;
	}
	public void setValorLiquido(BigDecimal valorLiquido) {
		this.valorLiquido = valorLiquido;
	}
	public BigDecimal getValorOutros() {
		return valorOutros;
	}
	public void setValorOutros(BigDecimal valorOutros) {
		this.valorOutros = valorOutros;
	}
	public BigDecimal getValorProcedimentosGlosados() {
		return valorProcedimentosGlosados;
	}
	public void setValorProcedimentosGlosados(BigDecimal valorProcedimentosGlosados) {
		this.valorProcedimentosGlosados = valorProcedimentosGlosados;
	}
	public ComponenteColecaoContas getColecaoContas() {
		return colecaoContas;
	}
	public void setColecaoContas(ComponenteColecaoContas colecaoContas) {
		this.colecaoContas = colecaoContas;
	}
	public InformacaoFinanceiraInterface getInformacaoFinanceira() {
		return informacaoFinanceira;
	}
	public void setInformacaoFinanceira(InformacaoFinanceiraInterface informacaoFinanceira) {
		this.informacaoFinanceira = informacaoFinanceira;
	}
	public TitularFinanceiroInterface getTitularFinanceiro() {
		return this.prestador;
	}
	public Long getIdFluxoFinanceiro() {
		return idFluxoFinanceiro;
	}
	public void setIdFluxoFinanceiro(Long idFluxoFinanceiro) {
		this.idFluxoFinanceiro = idFluxoFinanceiro;
	}
	public String getTipoFaturamento(){
		return "FaturamentoBC";
	}

	//Refatorar os nomes dos métodos definindo novos campos
	public Date getDataPagamento() {
		return dataPagamento;
	}

	public Date getDataVencimento() {
		return null;
	}

	public BigDecimal getValorCobrado() {
		return BigDecimal.ZERO;
	}

	public BigDecimal getValorPago() {
		return BigDecimal.ZERO;
	}

	public void processarVencimento() {
		// TODO Auto-generated method stub
		
	}

	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public void setDataVencimento(Date dataVencimento) {
		// TODO Auto-generated method stub
		
	}

	public void setValorCobrado(BigDecimal valorCobrado) {
		// TODO Auto-generated method stub
		
	}

	public void setValorPago(BigDecimal valorPago) {
		// TODO Auto-generated method stub
	}

	public boolean isCobranca() {
		return false;
	}

	public boolean isConsignacao() {
		return false;
	}

	public boolean isFaturamento() {
		return true;
	}

	public boolean isGeradoPosteriormente() {
		return geradoPosteriormente;
	}

	public void setGeradoPosteriormente(boolean geradoPosteriormente) {
		this.geradoPosteriormente = geradoPosteriormente;
	}

	public String getMotivoGeracaoPosterior() {
		return motivoGeracaoPosterior;
	}

	public void setMotivoGeracaoPosterior(String motivoGeracaoPosterior) {
		this.motivoGeracaoPosterior = motivoGeracaoPosterior;
	}
	
	public String getNumeroEmpenho() {
		return numeroEmpenho;
	}
	
	public void setNumeroEmpenho(String numeroEmpenho) {
		this.numeroEmpenho = numeroEmpenho;
	}
	
	public abstract boolean isFaturamentoNormal();
	public abstract boolean isFaturamentoPassivo();

	public List<OcorrenciaFaturamento> getOcorrencias() {
		return ocorrencias;
	}

	public void setOcorrencias(List<OcorrenciaFaturamento> ocorrencias) {
		this.ocorrencias = ocorrencias;
	}

	public Ordenador getOrdenador() {
		return ordenador;
	}

	public void setOrdenador(Ordenador ordenador) {
		this.ordenador = ordenador;
	}
	
	public Set<ItemGuiaFaturamento> getItensGuiaFaturamento() {
		return itensGuiaFaturamento;
	}

	public void setItensGuiaFaturamento(
			Set<ItemGuiaFaturamento> itensGuiaFaturamento) {
		this.itensGuiaFaturamento = itensGuiaFaturamento;
	}
	
	/**
	 * Método que mostra a descricao da situacao do faturamento, é utilizado nos jhm/jsp/tag.
	 * @return
	 */
	public StatusFaturamentoEnum getStatusFaturamento() {
		return StatusFaturamentoEnum.getStatus(this.status);
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof AbstractFaturamento)) {
			return false;
		}
		AbstractFaturamento otherObject = (AbstractFaturamento) object;
		return new EqualsBuilder()
				.append(this.getNome(), otherObject.getNome())
				.append(this.getTipoFaturamento(), otherObject.getTipoFaturamento())
				.append(this.getCompetenciaFormatada(), otherObject.getCompetenciaFormatada())
				.append(this.getValorBruto(), otherObject.getValorBruto())
				.append(this.getOrdenador(), otherObject.getOrdenador())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
					.append(this.getNome())
					.append(this.getTipoFaturamento())
					.append(this.getCompetenciaFormatada())
					.append(this.getValorBruto())
					.append(this.getOrdenador())
					.toHashCode();
	}

	public Set<GuiaRecursoGlosa> getGuiasRecursoGlosa() {
		Set<GuiaRecursoGlosa> grgs = new HashSet<GuiaRecursoGlosa>();
		
		for (GuiaFaturavel guia : this.getGuiasFaturaveis()) {
			if (guia.isRecursoGlosa()) {
				grgs.add((GuiaRecursoGlosa) guia);
			}
		}
		
		return grgs;
	}

	public BigDecimal getValorRecursosDeferidos() {
		return valorRecursosDeferidos;
	}

	public void setValorRecursosDeferidos(BigDecimal valorRecursosDeferidos) {
		this.valorRecursosDeferidos = valorRecursosDeferidos;
	}
}
