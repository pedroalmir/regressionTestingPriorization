package br.com.infowaypi.ecare.segurados;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;

import br.com.infowaypi.ecare.arquivos.ArquivoBase;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.enums.TipoSeguradoEnum;
import br.com.infowaypi.ecarebc.promocao.PromocaoConsulta;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.molecular.parameter.IsNotNull;
import br.com.infowaypi.molecular.parameter.LowerEquals;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author Idelvane e Mário Sérgio Coelho Marroquim
 */
public class ResumoSegurados {
	
	private static final String INICIO_SAUDE_RECIFE = "01/07/2007";
	private static String[] situacoesRelatorioDeAdesoes = {SituacaoEnum.ATIVO.descricao(),SituacaoEnum.SUSPENSO.descricao()};
	public static final String TIPO_TITULAR = "Titular";
	public static final String TIPO_DEPENDENTE = "Dependente";
	public static final String TIPO_PENSIONISTA = "Pensionista";
	public static final String TIPO_DEPENDENTE_SUPLEMENTAR = "Dependente Suplementar";

	private int totalSegurados;
	//relatorio de segurados recadastrados
	private int totalDeSeguradosRecadastrados;
	private int totalDeSeguradosComQuestionarioAplicado;
	
	private int totalDeTitularesRecadastrados;
	private int totalDeTitularesComQuestionarioAplicado;
	
	private int totalDeDependentesRecadastrados;
	private int totalDeDependentesComQuestionarioAplicado;
	
	private int totalDePensionistasRecadastrados;
	private int totalDePensionistasComQuestionarioAplicado;
	
	private int totalDeDependentesSuplementaresRecadastrados;
	private int totalDeDependentesSuplementaresComQuestionarioAplicado;

	private BigDecimal porcentagemSeguradosRecadastrados;
	private BigDecimal porcentagemTitularesRecadastrados;
	private BigDecimal porcentagemDependentesRecadastrados;
	private BigDecimal porcentagemDependentesSuplementaresRecadastrados;
	private BigDecimal porcentagemPensionistasRecadastrados;
	
	private BigDecimal porcentagemSeguradosComQuestionarioAplicado;
	private BigDecimal porcentagemTitularesComQuestionarioAplicado;
	private BigDecimal porcentagemDependentesComQuestionarioAplicado;
	private BigDecimal porcentagemDependentesSuplementaresComQuestionarioAplicado;
	private BigDecimal porcentagemPensionistasComQuestionarioAplicado;
	
	private Date dataInicial;
	private Date dataFinal;
	
	///outros services
	private int totalTitulares;
	private int totalDependentes;
	
	private int seguradosAtivos;
	private int seguradosInativos;
	private int seguradosSuspensos;
	private int seguradosCancelados;
	private int seguradosCadastrados;

	private int titularesAtivos;
	private int titularesInativos;
	private int titularesSuspensos;
	private int titularesCancelados;
	private int titularesCadastrados;
	
	private int dependentesAtivos;
	private int dependentesInativos;
	private int dependentesSuspensos;
	private int dependentesCancelados;
	private int dependentesCadastrados;
	
	private int pensionistasAtivos;
	private int pensionistasCancelados;
	private int pensionistasInativos;
	private int pensionistasSuspensos;
	private int pensionistasCadastrados;
	
	private List<Segurado> segurados;
	private List<Segurado> titulares;
	private List<Segurado> dependentes;     
	private List<GuiaSimples> guias;
	private List<PromocaoConsulta> consultasPromocionaisLiberadas;
	private int dependentesSuplementaresAtivos;
	private int dependentesSuplementaresInativos;
	private int dependentesSuplementaresSuspensos;
	private int dependentesSuplementaresCancelados;
	private int dependentesSuplementaresCadastrados;
	
	private float quantidadeSeguradosTitulares = 0;
	private float quantidadeSeguradosDependentes = 0;
	private float quantidadeSeguradosPensionistas = 0;
	private float quantidadeSeguradosDependentesSuplementares = 0;

	private boolean detalharSegurados;
	
	// Atributos referente ao arquivo do relatório detalhado.
	private final  String PATH = "D:\\workspace_sr\\sauderecife\\arquivos\\";
	public String nomeArquivo;
	private ArquivoBase arquivoXLS;
	
	public ResumoSegurados(){
		this.porcentagemSeguradosRecadastrados = BigDecimal.ZERO;
		segurados = new ArrayList<Segurado>();
		titulares = new ArrayList<Segurado>();
		dependentes = new ArrayList<Segurado>();
		consultasPromocionaisLiberadas = new ArrayList<PromocaoConsulta>();
	}
	
	public ResumoSegurados(String dataInicial, String dataFinal, Boolean exibirSegurados){
		this();
		Date dataInitial = Utils.parse(dataInicial);
		Calendar cal = setLeniente(dataInitial);
		this.dataInicial = cal.getTime();
		Date dataFim  = Utils.parse(dataFinal);
		
		cal = setLeniente(dataFim);
		this.dataFinal = cal.getTime();
		this.porcentagemSeguradosRecadastrados = BigDecimal.ZERO;
		this.porcentagemDependentesRecadastrados = BigDecimal.ZERO;
		this.porcentagemTitularesRecadastrados = BigDecimal.ZERO;
		this.porcentagemDependentesSuplementaresRecadastrados = BigDecimal.ZERO;
		this.porcentagemPensionistasRecadastrados = BigDecimal.ZERO;
		
		this.porcentagemSeguradosComQuestionarioAplicado = BigDecimal.ZERO;
		this.porcentagemTitularesComQuestionarioAplicado = BigDecimal.ZERO;
		this.porcentagemDependentesComQuestionarioAplicado = BigDecimal.ZERO;
		this.porcentagemDependentesSuplementaresComQuestionarioAplicado = BigDecimal.ZERO;
		this.porcentagemPensionistasComQuestionarioAplicado = BigDecimal.ZERO;
	}

	private Calendar setLeniente(Date data) {
		Calendar cal =  Calendar.getInstance();
		cal.setTime(data);
		cal.setLenient(true);
		return cal;
	}
	
	public ResumoSegurados(List<Segurado> segurados){
		this();
		this.segurados = segurados;
	}
	
	
	@SuppressWarnings("unchecked")
	public ResumoSegurados computarRecadastramento(){
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("recadastrado", Boolean.TRUE));
		sa.addParameter(new In("situacao.descricao", situacoesRelatorioDeAdesoes));
		sa.addParameter(new GreaterEquals("situacaoCadastral.dataSituacao", dataInicial));
		sa.addParameter(new LowerEquals("situacaoCadastral.dataSituacao", dataFinal));
		
//		this.segurados = sa.list(Segurado.class);
//		Collections.sort(this.segurados, new Comparator<Segurado>(){
//			public int compare(Segurado seg1, Segurado seg2) {
//				return seg1.getSituacaoCadastral().getDataSituacao().compareTo(seg2.getSituacaoCadastral().getDataSituacao()) * -1;
//			}
//		});
//		this.dependentes = sa.list(Dependente.class);
//		this.titulares = sa.list(Titular.class);
		
		this.totalDeSeguradosRecadastrados = getCountRecadastrados(Segurado.class);
		this.totalDeDependentesRecadastrados = getCountRecadastrados(DependenteSR.class);
		this.totalDeTitularesRecadastrados = getCountRecadastrados(Titular.class);
		this.totalDeDependentesSuplementaresRecadastrados = getCountRecadastrados(DependenteSuplementar.class);
		this.totalDePensionistasRecadastrados = getCountRecadastrados(Pensionista.class);
		
		this.totalDeSeguradosComQuestionarioAplicado = getCountSeguradosComQuestionarioAplicado(Segurado.class);
		this.totalDeDependentesComQuestionarioAplicado = getCountSeguradosComQuestionarioAplicado(DependenteSR.class);
		this.totalDeTitularesComQuestionarioAplicado = getCountSeguradosComQuestionarioAplicado(Titular.class);
		this.totalDeDependentesSuplementaresComQuestionarioAplicado = getCountSeguradosComQuestionarioAplicado(DependenteSuplementar.class);
		this.totalDePensionistasComQuestionarioAplicado = getCountSeguradosComQuestionarioAplicado(Pensionista.class);
		
		this.computarResultado();
		
		Long numeroAdesores = this.getNumeroTotalDeAdesoes();
		
		if(numeroAdesores > 0)
			this.porcentagemSeguradosRecadastrados = new BigDecimal(this.totalDeSeguradosRecadastrados).divide(new BigDecimal(numeroAdesores), 4,BigDecimal.ROUND_HALF_UP);
		
		numeroAdesores = this.getNumeroTotalDeAdesoesDeDependentes();
		if(numeroAdesores > 0)
			this.porcentagemDependentesRecadastrados  = new BigDecimal(this.totalDeDependentesRecadastrados).divide(new BigDecimal(numeroAdesores), 4,BigDecimal.ROUND_HALF_UP);
		
		numeroAdesores = this.getNumeroTotalDeAdesoesDeTitulares();
		if(numeroAdesores > 0)
			this.porcentagemTitularesRecadastrados = new BigDecimal(this.totalDeTitularesRecadastrados).divide(new BigDecimal(numeroAdesores), 4,BigDecimal.ROUND_HALF_UP);
		
		numeroAdesores = this.getNumeroTotalDeAdesoesDeDependentesSuplementares();
		if(numeroAdesores > 0)
			this.porcentagemDependentesSuplementaresRecadastrados = new BigDecimal(this.totalDeDependentesSuplementaresRecadastrados).divide(new BigDecimal(numeroAdesores), 4,BigDecimal.ROUND_HALF_UP);
		
		numeroAdesores = this.getNumeroTotalDeAdesoesDePensionistas();
		if(numeroAdesores > 0)
			this.porcentagemPensionistasRecadastrados = new BigDecimal(this.totalDePensionistasRecadastrados).divide(new BigDecimal(numeroAdesores), 4,BigDecimal.ROUND_HALF_UP);
		
		numeroAdesores = this.getNumeroTotalDeAdesoes();
		if(numeroAdesores > 0)
			this.porcentagemSeguradosComQuestionarioAplicado = new BigDecimal(this.totalDeSeguradosComQuestionarioAplicado).divide(new BigDecimal(numeroAdesores), 4,BigDecimal.ROUND_HALF_UP);
		
		numeroAdesores = this.getNumeroTotalDeAdesoesDeDependentes();
		if(numeroAdesores > 0)
			this.porcentagemDependentesComQuestionarioAplicado  = new BigDecimal(this.totalDeDependentesComQuestionarioAplicado).divide(new BigDecimal(numeroAdesores), 4,BigDecimal.ROUND_HALF_UP);
		
		numeroAdesores = this.getNumeroTotalDeAdesoesDeTitulares();
		if(numeroAdesores > 0)
			this.porcentagemTitularesComQuestionarioAplicado = new BigDecimal(this.totalDeTitularesComQuestionarioAplicado).divide(new BigDecimal(numeroAdesores), 4,BigDecimal.ROUND_HALF_UP);
		
		numeroAdesores = this.getNumeroTotalDeAdesoesDeDependentesSuplementares();
		if(numeroAdesores > 0)
			this.porcentagemDependentesSuplementaresComQuestionarioAplicado = new BigDecimal(this.totalDeDependentesSuplementaresComQuestionarioAplicado).divide(new BigDecimal(numeroAdesores), 4,BigDecimal.ROUND_HALF_UP);
		
		numeroAdesores = this.getNumeroTotalDeAdesoesDePensionistas();
		if(numeroAdesores > 0)
			this.porcentagemPensionistasComQuestionarioAplicado = new BigDecimal(this.totalDePensionistasComQuestionarioAplicado).divide(new BigDecimal(numeroAdesores), 4,BigDecimal.ROUND_HALF_UP);
		
		
		return this;
	}
	
	private int getCountRecadastrados(Class tipoBeneficiario){
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("recadastrado", Boolean.TRUE));
		sa.addParameter(new In("situacao.descricao", situacoesRelatorioDeAdesoes));
		sa.addParameter(new GreaterEquals("situacaoCadastral.dataSituacao", dataInicial));
		sa.addParameter(new LowerEquals("situacaoCadastral.dataSituacao", dataFinal));
		return sa.resultCount(tipoBeneficiario);
		
	}
	
	private int getCountSeguradosComQuestionarioAplicado(Class tipoBeneficiario){
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new IsNotNull("aplicacaoQuestionario"));
		sa.addParameter(new In("situacao.descricao", situacoesRelatorioDeAdesoes));
		sa.addParameter(new GreaterEquals("situacaoCadastral.dataSituacao", dataInicial));
		sa.addParameter(new LowerEquals("situacaoCadastral.dataSituacao", dataFinal));
		return sa.resultCount(tipoBeneficiario);
		
	}
	
	
	public static void main(String[] args) {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new IsNotNull("aplicacaoQuestionario"));
		sa.addParameter(new In("situacao.descricao", situacoesRelatorioDeAdesoes));
		sa.addParameter(new GreaterEquals("situacaoCadastral.dataSituacao", Utils.parse("01/01/2005")));
		sa.addParameter(new LowerEquals("situacaoCadastral.dataSituacao", Utils.parse("01/04/2008")));
		System.out.println(sa.resultCount(Segurado.class));
	}
	
	public ResumoSegurados computarResultado(){
		try {
			
			if(this.totalSegurados == 0) {
				
				this.titularesAtivos = getCount("select count(*) from SEGURADOS_Segurado where descricao = 'Ativo(a)' and tipoSegurado='T'");
				this.titularesInativos = getCount("select count(*) from SEGURADOS_Segurado where descricao = 'Inativo(a)' and tipoSegurado='T'");
				this.titularesSuspensos = getCount("select count(*) from SEGURADOS_Segurado where descricao = 'Suspenso(a)' and tipoSegurado='T'");
				this.titularesCancelados = getCount("select count(*) from SEGURADOS_Segurado where descricao = 'Cancelado(a)' and tipoSegurado='T'");
				this.titularesCadastrados = getCount("select count(*) from SEGURADOS_Segurado where descricao = 'Cadastrado' and tipoSegurado='T'");

				this.dependentesAtivos = getCount("select count(*) from SEGURADOS_Segurado where descricao = 'Ativo(a)' and tipoSegurado='D'");
				this.dependentesInativos = getCount("select count(*) from SEGURADOS_Segurado where descricao = 'Inativo(a)' and tipoSegurado='D'");
				this.dependentesSuspensos = getCount("select count(*) from SEGURADOS_Segurado where descricao = 'Suspenso(a)' and tipoSegurado='D'");
				this.dependentesCancelados = getCount("select count(*) from SEGURADOS_Segurado where descricao = 'Cancelado(a)' and tipoSegurado='D'");
				this.dependentesCadastrados = getCount("select count(*) from SEGURADOS_Segurado where descricao = 'Cadastrado' and tipoSegurado='D'");
				
				this.dependentesSuplementaresAtivos = getCount("select count(*) from SEGURADOS_Segurado where descricao = 'Ativo(a)' and tipoSegurado='DS'");
				this.dependentesSuplementaresInativos = getCount("select count(*) from SEGURADOS_Segurado where descricao = 'Inativo(a)' and tipoSegurado='DS'");
				this.dependentesSuplementaresSuspensos = getCount("select count(*) from SEGURADOS_Segurado where descricao = 'Suspenso(a)' and tipoSegurado='DS'");
				this.dependentesSuplementaresCancelados = getCount("select count(*) from SEGURADOS_Segurado where descricao = 'Cancelado(a)' and tipoSegurado='DS'");
				this.dependentesSuplementaresCadastrados = getCount("select count(*) from SEGURADOS_Segurado where descricao = 'Cadastrado' and tipoSegurado='DS'");
				
				this.pensionistasAtivos = getCount("select count(*) from SEGURADOS_Segurado where descricao = 'Ativo(a)' and tipoSegurado='P'");
				this.pensionistasInativos = getCount("select count(*) from SEGURADOS_Segurado where descricao = 'Inativo(a)' and tipoSegurado='P'");
				this.pensionistasSuspensos = getCount("select count(*) from SEGURADOS_Segurado where descricao = 'Suspenso(a)' and tipoSegurado='P'");
				this.pensionistasCancelados = getCount("select count(*) from SEGURADOS_Segurado where descricao = 'Cancelado(a)' and tipoSegurado='P'");
				this.pensionistasCadastrados = getCount("select count(*) from SEGURADOS_Segurado where descricao = 'Cadastrado' and tipoSegurado='P'");
				
				this.seguradosAtivos = this.titularesAtivos + this.dependentesAtivos + dependentesSuplementaresAtivos + this.pensionistasAtivos ;
				this.seguradosInativos = this.titularesInativos + this.dependentesInativos + dependentesSuplementaresInativos + this.pensionistasInativos ;
				this.seguradosSuspensos = this.titularesSuspensos + this.dependentesSuspensos + dependentesSuplementaresSuspensos + this.pensionistasSuspensos ;
				this.seguradosCancelados = this.titularesCancelados + this.dependentesCancelados + this.pensionistasCancelados + dependentesSuplementaresCancelados;
				this.seguradosCadastrados = this.titularesCadastrados + this.dependentesCadastrados + dependentesSuplementaresCadastrados+ this.pensionistasCadastrados ;
				
				this.totalSegurados = this.seguradosAtivos + this.seguradosInativos + this.seguradosSuspensos + this.seguradosCancelados + this.seguradosCadastrados;
				this.totalTitulares = this.titularesAtivos + this.titularesInativos + this.titularesSuspensos + this.titularesCancelados + this.titularesCadastrados;
				this.totalDependentes = this.dependentesAtivos + this.dependentesInativos + this.dependentesSuspensos + this.dependentesCancelados + this.dependentesCadastrados;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	private int getCount(String sql) throws SQLException {
		Connection conne = HibernateUtil.currentSession().connection();
		
		if(conne == null || conne.isClosed())
			return 0;
		
		Statement state = conne.createStatement();
		ResultSet resultSet = state.executeQuery(sql);
		
		if(resultSet != null && resultSet.next()) {
			return resultSet.getInt(1);
		}
		resultSet.close();
		state.close();
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	public List<GuiaSimples> getGuias() {
		if (guias == null) {
			guias = new ArrayList<GuiaSimples>();
			for (Segurado segurado : segurados) {
				guias.addAll((Collection<? extends GuiaSimples>) segurado.getGuias()); 
			}
		}		
		return guias;
	}

	public List<Segurado> getSegurados() {
		return segurados;
	}
	
	public GuiaSimples getGuiaSimples(){
		if(this.guias != null && !this.guias.isEmpty()){
			return this.guias.iterator().next();
		}
		return null;
	}
	
	public boolean isGuiaUnica(){
		if(this.guias != null && !this.guias.isEmpty() && this.guias.size() == 1){
			return true;
		}
		
		return false;
	}
	
	public long getNumeroTodalDeSeguradosCanceladosNoPeriodo() {
		List<Long> ints = HibernateUtil.currentSession().createCriteria(Segurado.class)
		.setProjection(Projections.rowCount())
		.add(Expression.ge("situacao.dataSituacao", dataInicial))
		.add(Expression.le("situacao.dataSituacao", dataFinal))
	    .add(Expression.eq("situacao.descricao", SituacaoEnum.CANCELADO.descricao()))
		.list();
		
		Long result = getSoma(ints);
		
		return result;
	}
	
	public BigDecimal getPorcentagemDeSeguradosCancelados() {
		
		BigDecimal totalCancelamentos = new BigDecimal(this.seguradosCancelados);
		BigDecimal totalCancelamentosNoPeriodo = new BigDecimal(this.getNumeroTodalDeSeguradosCanceladosNoPeriodo());
		BigDecimal resultado = BigDecimal.ZERO;
		resultado = MoneyCalculation.divide(totalCancelamentosNoPeriodo, totalCancelamentos.floatValue());
		
		return resultado.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	

	public long getNumeroTodalDeTitularesCanceladosNoPeriodo() {
		List<Long> ints = HibernateUtil.currentSession().createCriteria(Titular.class)
		.setProjection(Projections.rowCount())
		.add(Expression.ge("situacao.dataSituacao", dataInicial))
		.add(Expression.le("situacao.dataSituacao", dataFinal))
	    .add(Expression.eq("situacao.descricao", SituacaoEnum.CANCELADO.descricao()))
		.list();
		
		Long result = getSoma(ints);
		
		return result;
	}
	
	public BigDecimal getPorcentagemDeTitularesCancelados() {
		
		BigDecimal totalCancelamentos = new BigDecimal(this.titularesCancelados);
		BigDecimal totalCancelamentosNoPeriodo = new BigDecimal(this.getNumeroTodalDeTitularesCanceladosNoPeriodo());
		BigDecimal resultado = BigDecimal.ZERO;
		resultado = MoneyCalculation.divide(totalCancelamentosNoPeriodo, totalCancelamentos.floatValue());
		
		return resultado.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	public long getNumeroTodalDeDependentesCanceladosNoPeriodo() {
		List<Long> ints = HibernateUtil.currentSession().createCriteria(Dependente.class)
		.setProjection(Projections.rowCount())
		.add(Expression.ge("situacao.dataSituacao", dataInicial))
		.add(Expression.le("situacao.dataSituacao", dataFinal))
	    .add(Expression.eq("situacao.descricao", SituacaoEnum.CANCELADO.descricao()))
		.list();
		
		Long result = getSoma(ints);
		
		return result;
	}
	
	public long getNumeroTodalDeDependentesSuplementaresCanceladosNoPeriodo() {
		List<Long> ints = HibernateUtil.currentSession().createCriteria(DependenteSuplementar.class)
		.setProjection(Projections.rowCount())
		.add(Expression.ge("situacao.dataSituacao", dataInicial))
		.add(Expression.le("situacao.dataSituacao", dataFinal))
	    .add(Expression.eq("situacao.descricao", SituacaoEnum.CANCELADO.descricao()))
		.list();
		
		Long result = getSoma(ints);
		
		return result;
	}
	
	public BigDecimal getPorcentagemDeDependentesCancelados() {
		
		BigDecimal totalCancelamentos = new BigDecimal(this.dependentesCancelados);
		BigDecimal totalCancelamentosNoPeriodo = new BigDecimal(this.getNumeroTodalDeDependentesCanceladosNoPeriodo());
		BigDecimal resultado = BigDecimal.ZERO;
		resultado = MoneyCalculation.divide(totalCancelamentosNoPeriodo, totalCancelamentos.floatValue());
		
		return resultado.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	public BigDecimal getPorcentagemDeDependentesSuplementaresCancelados() {
		
		BigDecimal totalCancelamentos = new BigDecimal(this.dependentesSuplementaresCancelados);
		BigDecimal totalCancelamentosNoPeriodo = new BigDecimal(this.getNumeroTodalDeDependentesSuplementaresCanceladosNoPeriodo());
		BigDecimal resultado = BigDecimal.ZERO;
		resultado = MoneyCalculation.divide(totalCancelamentosNoPeriodo, totalCancelamentos.floatValue());
		
		return resultado.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	public long getNumeroTodalDePensionistasCanceladosNoPeriodo() {
		List<Long> ints = HibernateUtil.currentSession().createCriteria(Pensionista.class)
		.setProjection(Projections.rowCount())
		.add(Expression.ge("situacao.dataSituacao", dataInicial))
		.add(Expression.le("situacao.dataSituacao", dataFinal))
	    .add(Expression.eq("situacao.descricao", SituacaoEnum.CANCELADO.descricao()))
		.list();
		
		Long result = getSoma(ints);
		
		return result;
	}
	
	public BigDecimal getPorcentagemDePensionistasCancelados() {
		
		BigDecimal totalCancelamentos = new BigDecimal(this.pensionistasCancelados);
		BigDecimal totalCancelamentosNoPeriodo = new BigDecimal(this.getNumeroTodalDePensionistasCanceladosNoPeriodo());
		BigDecimal resultado = BigDecimal.ZERO;
		resultado = MoneyCalculation.divide(totalCancelamentosNoPeriodo, totalCancelamentos.floatValue());
		
		return resultado.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public long getNumeroTotalDeAdesoes() {
		List<Long> ints = HibernateUtil.currentSession().createCriteria(Segurado.class)
		.setProjection(Projections.rowCount())
	    .add(Expression.in("situacao.descricao", situacoesRelatorioDeAdesoes))
		.list();
		
		Long result = getSoma(ints);
		
		return result;
	}
	
	public long getNumeroTotalDeAdesoesDeTitulares() {
	
		List<Long> ints = (List<Long>) HibernateUtil.currentSession().createCriteria(Titular.class)
		.setProjection(Projections.rowCount())
	    .add(Expression.in("situacao.descricao", situacoesRelatorioDeAdesoes))
		.list();
		
		Long result = getSoma(ints);
		
		return result;
	}
	
	public long getNumeroTotalDeAdesoesDePensionistas() {
		
		List<Long> ints = (List<Long>) HibernateUtil.currentSession().createCriteria(Pensionista.class)
		.setProjection(Projections.rowCount())
	    .add(Expression.in("situacao.descricao", situacoesRelatorioDeAdesoes))
		.list();
		
		Long result = getSoma(ints);
		
		return result;
	}
	private Long getSoma(List<Long> ints) {
		Long result = 0L;
		for (Long count : ints) {
			result += count;
		}
		return result;
	}
	
	public int getNumeroTotalDeAdesoesNoPeriodoTitulares() {
		int resultado = 0;
		for (Segurado segurado : this.segurados) {
			if(segurado.getTipoDeSegurado().equals(TIPO_TITULAR)) {
				++resultado;
			}
		}
		return resultado;
	}
	
	public int getNumeroTotalDeAdesoesNoPeriodoPensionistas() {
		int resultado = 0;
		for (Segurado segurado : this.segurados) {
			if(segurado.getTipoDeSegurado().equals(TIPO_PENSIONISTA)) {
				++resultado;
			}
		}
		return resultado;
	}
	
	public int getNumeroTotalDeAdesoesNoPeriodoDependentes() {
		int resultado = 0;
		for (Segurado segurado : this.segurados) {
			if(segurado.getTipoDeSegurado().equals(TipoSeguradoEnum.DEPENDENTE.descricao())) {
				++resultado;
			}
		}
		return resultado;
	}
	
	public BigDecimal getSomatorioIdadesTitulares() {
		Integer somatorioIdades = 0;
		for (Segurado segurado : this.segurados) {
			if(segurado.getTipoDeSegurado().equals(TIPO_TITULAR)) {
				somatorioIdades  += segurado.getPessoaFisica().getIdade();
				this.quantidadeSeguradosTitulares += 1;
			}
		}
		BigDecimal valor = new BigDecimal(somatorioIdades);
		return valor;
	}
	
	public BigDecimal getSomatorioIdadesDependentes() {
		Integer somatorioIdades = 0;
		for (Segurado segurado : this.segurados) {
			if(segurado.getTipoDeSegurado().equals(TIPO_DEPENDENTE)) {
				somatorioIdades  += segurado.getPessoaFisica().getIdade();
				this.quantidadeSeguradosDependentes += 1;
			}
		}
		BigDecimal valor = new BigDecimal(somatorioIdades);
		return valor;
	}
	
	public BigDecimal getSomatorioIdadesPensionistas() {
		Integer somatorioIdades = 0;
		for (Segurado segurado : this.segurados) {
			if(segurado.getTipoDeSegurado().equals(TIPO_PENSIONISTA)) {
				somatorioIdades  += segurado.getPessoaFisica().getIdade();
				this.quantidadeSeguradosPensionistas += 1;
			}
		}
		BigDecimal valor = new BigDecimal(somatorioIdades);
		return valor;
	}
	
	public BigDecimal getSomatorioIdadesDependentesSuplementares() {
		Integer somatorioIdades = 0;
		for (Segurado segurado : this.segurados) {
			if(segurado.getTipoDeSegurado().equals(TIPO_DEPENDENTE_SUPLEMENTAR)) {
				somatorioIdades  += segurado.getPessoaFisica().getIdade();
				this.quantidadeSeguradosDependentesSuplementares += 1;
			}
		}
		BigDecimal valor = new BigDecimal(somatorioIdades);
		return valor;
	}
	
	public BigDecimal getMediaIdadeTitulares(){
		return MoneyCalculation.divide(this.getSomatorioIdadesTitulares(), this.quantidadeSeguradosTitulares);
	}
	
	public BigDecimal getMediaIdadeDependentes(){
		return MoneyCalculation.divide(this.getSomatorioIdadesDependentes(), this.quantidadeSeguradosDependentes);
	}
	
	public BigDecimal getMediaIdadePensionistas(){
		return MoneyCalculation.divide(this.getSomatorioIdadesPensionistas(), this.quantidadeSeguradosPensionistas);
	}
	
	public BigDecimal getMediaIdadeDependentesSuplementares(){
		return MoneyCalculation.divide(this.getSomatorioIdadesDependentesSuplementares(), this.quantidadeSeguradosDependentesSuplementares);
	}
	
	public int getNumeroTotalDeAdesoesNoPeriodoDependentesSuplementares() {
		int resultado = 0;
		for (Segurado segurado : this.segurados) {
			if(segurado.getTipoDeSegurado().equals(TipoSeguradoEnum.DEPENDENTE_SUPLEMENTAR.descricao())) {
				++resultado;
			}
		}
		return resultado;
	}

	public long getNumeroTotalDeAdesoesDeDependentes() {
		
		List<Long> ints= HibernateUtil.currentSession().createCriteria(Dependente.class)
		.setProjection(Projections.rowCount())
	    .add(Expression.in("situacao.descricao", situacoesRelatorioDeAdesoes))
		.list();
		
		Long result = getSoma(ints);
		return result;
	}

	public long getNumeroTotalDeAdesoesDeDependentesSuplementares() {
		
		List<Long> ints= HibernateUtil.currentSession().createCriteria(DependenteSuplementar.class)
		.setProjection(Projections.rowCount())
	    .add(Expression.in("situacao.descricao", situacoesRelatorioDeAdesoes))
		.list();
		
		Long result = getSoma(ints);
		return result;
	}

	
	public BigDecimal getPorcentagemTotalDeAdesoes() {
		
		BigDecimal totalDeAdesoes = new BigDecimal(getNumeroTotalDeAdesoes());
		BigDecimal quantidadeTotalDeAdesoesNoPeriodo = new BigDecimal(getNumeroTotalDeAdesoesNoPeriodo());
		BigDecimal porcentagemAdesoes = BigDecimal.ZERO;
		
		porcentagemAdesoes = quantidadeTotalDeAdesoesNoPeriodo.divide(totalDeAdesoes, 5, BigDecimal.ROUND_HALF_UP);
		
		return porcentagemAdesoes;
	}
	
	public BigDecimal getPorcentagemTotalDeAdesoesTitulares() {
		
		BigDecimal totalDeAdesoes = new BigDecimal(this.getNumeroTotalDeAdesoesDeTitulares());
		BigDecimal quantidadeTotalDeAdesoesNoPeriodo = new BigDecimal(this.getNumeroTotalDeAdesoesNoPeriodoTitulares());
		BigDecimal porcentagemAdesoes = BigDecimal.ZERO;
		
		porcentagemAdesoes = quantidadeTotalDeAdesoesNoPeriodo.divide(totalDeAdesoes, 5, BigDecimal.ROUND_HALF_UP);
		
		return porcentagemAdesoes;
	}
	
	public BigDecimal getPorcentagemTotalDeAdesoesPensionistas() {
		
		BigDecimal totalDeAdesoes = new BigDecimal(this.getNumeroTotalDeAdesoesDePensionistas());
		BigDecimal quantidadeTotalDeAdesoesNoPeriodo = new BigDecimal(this.getNumeroTotalDeAdesoesNoPeriodoPensionistas());
		BigDecimal porcentagemAdesoes = BigDecimal.ZERO;
		
		porcentagemAdesoes = quantidadeTotalDeAdesoesNoPeriodo.divide(totalDeAdesoes, 5, BigDecimal.ROUND_HALF_UP);
		
		return porcentagemAdesoes;
	}
	
	public BigDecimal getPorcentagemTotalDeAdesoesDependentes() {
		
		BigDecimal totalDeAdesoes = new BigDecimal(this.getNumeroTotalDeAdesoesDeDependentes());
		BigDecimal quantidadeTotalDeAdesoesNoPeriodo = new BigDecimal(this.getNumeroTotalDeAdesoesNoPeriodoDependentes());
		BigDecimal porcentagemAdesoes = BigDecimal.ZERO;
		
		porcentagemAdesoes = quantidadeTotalDeAdesoesNoPeriodo.divide(totalDeAdesoes, 5, BigDecimal.ROUND_HALF_UP);
		
		return porcentagemAdesoes;
	}
	
	public BigDecimal getPorcentagemTotalDeAdesoesDependentesSuplementares() {
		
		BigDecimal totalDeAdesoes = new BigDecimal(this.getNumeroTotalDeAdesoesDeDependentesSuplementares());
		BigDecimal quantidadeTotalDeAdesoesNoPeriodo = new BigDecimal(this.getNumeroTotalDeAdesoesNoPeriodoDependentesSuplementares());
		BigDecimal porcentagemAdesoes = BigDecimal.ZERO;
		
		porcentagemAdesoes = quantidadeTotalDeAdesoesNoPeriodo.divide(totalDeAdesoes, 5, BigDecimal.ROUND_HALF_UP);
		
		return porcentagemAdesoes;
	}
	
	public int getNumeroTotalDeAdesoesNoPeriodo() {
		return this.segurados.size();
	}

	public void setSegurados(List<Segurado> segurados) {
		this.segurados = segurados;
	}

	public int getDependentesAtivos() {
		return dependentesAtivos;
	}

	public int getDependentesCancelados() {
		return dependentesCancelados;
	}

	public int getTitularesAtivos() {
		return titularesAtivos;
	}

	public int getTitularesCancelados() {
		return titularesCancelados;
	}

	public int getTotalSegurados() {
		return totalSegurados;
	}

	public int getDependentesInativos() {
		return dependentesInativos;
	}

	public void setDependentesInativos(int dependentesInativos) {
		this.dependentesInativos = dependentesInativos;
	}

	public int getDependentesSuspensos() {
		return dependentesSuspensos;
	}

	public void setDependentesSuspensos(int dependentesSuspensos) {
		this.dependentesSuspensos = dependentesSuspensos;
	}

	public int getSeguradosAtivos() {
		return seguradosAtivos;
	}

	public void setSeguradosAtivos(int seguradosAtivos) {
		this.seguradosAtivos = seguradosAtivos;
	}

	public int getSeguradosCancelados() {
		return seguradosCancelados;
	}

	public void setSeguradosCancelados(int seguradosCancelados) {
		this.seguradosCancelados = seguradosCancelados;
	}

	public int getSeguradosInativos() {
		return seguradosInativos;
	}

	public void setSeguradosInativos(int seguradosInativos) {
		this.seguradosInativos = seguradosInativos;
	}

	public int getSeguradosSuspensos() {
		return seguradosSuspensos;
	}

	public void setSeguradosSuspensos(int seguradosSuspensos) {
		this.seguradosSuspensos = seguradosSuspensos;
	}

	public int getTitularesInativos() {
		return titularesInativos;
	}

	public void setTitularesInativos(int titularesInativos) {
		this.titularesInativos = titularesInativos;
	}

	public int getTitularesSuspensos() {
		return titularesSuspensos;
	}

	public void setTitularesSuspensos(int titularesSuspensos) {
		this.titularesSuspensos = titularesSuspensos;
	}

	public void setDependentesAtivos(int dependentesAtivos) {
		this.dependentesAtivos = dependentesAtivos;
	}

	public void setDependentesCancelados(int dependentesCancelados) {
		this.dependentesCancelados = dependentesCancelados;
	}

	public void setGuias(List<GuiaSimples> guias) {
		this.guias = guias;
	}

	public void setTitularesAtivos(int titularesAtivos) {
		this.titularesAtivos = titularesAtivos;
	}

	public void setTitularesCancelados(int titularesCancelados) {
		this.titularesCancelados = titularesCancelados;
	}

	public void setTotalSegurados(int totalSegurados) {
		this.totalSegurados = totalSegurados;
	}

	public int getTotalDependentes() {
		return totalDependentes;
	}

	public void setTotalDependentes(int totalDependentes) {
		this.totalDependentes = totalDependentes;
	}

	public int getTotalTitulares() {
		return totalTitulares;
	}

	public void setTotalTitulares(int totalTitulares) {
		this.totalTitulares = totalTitulares;
	}

	public int getDependentesCadastrados() {
		return dependentesCadastrados;
	}

	public void setDependentesCadastrados(int dependentesCadastrados) {
		this.dependentesCadastrados = dependentesCadastrados;
	}

	public int getSeguradosCadastrados() {
		return seguradosCadastrados;
	}

	public void setSeguradosCadastrados(int seguradosCadastrados) {
		this.seguradosCadastrados = seguradosCadastrados;
	}

	public int getTitularesCadastrados() {
		return titularesCadastrados;
	}

	public void setTitularesCadastrados(int titularesCadastrados) {
		this.titularesCadastrados = titularesCadastrados;
	}

	public BigDecimal getPorcentagemSeguradosRecadastrados() {
		return porcentagemSeguradosRecadastrados;
	}

	public void setPorcentagemSeguradosRecadastrados(BigDecimal porcentagemRecadastrados) {
		this.porcentagemSeguradosRecadastrados = porcentagemRecadastrados;
	}

	public int getTotalDeSeguradosRecadastrados() {
		return totalDeSeguradosRecadastrados;
	}

	public void setTotalDeSeguradosRecadastrados(int totalDeSeguradosRecadastrados) {
		this.totalDeSeguradosRecadastrados = totalDeSeguradosRecadastrados;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public List<Segurado> getDependentes() {
		return dependentes;
	}

	public void setDependentes(List<Segurado> dependentes) {
		this.dependentes = dependentes;
	}

	public List<Segurado> getTitulares() {
		return titulares;
	}

	public void setTitulares(List<Segurado> titulares) {
		this.titulares = titulares;
	}

	public int getTotalDeDependentesRecadastrados() {
		return totalDeDependentesRecadastrados;
	}

	public void setTotalDeDependentesRecadastrados(
			int totalDeDependentesRecadastrados) {
		this.totalDeDependentesRecadastrados = totalDeDependentesRecadastrados;
	}

	public int getTotalDeTitularesRecadastrados() {
		return totalDeTitularesRecadastrados;
	}

	public void setTotalDeTitularesRecadastrados(int totalDeTitularesRecadastrados) {
		this.totalDeTitularesRecadastrados = totalDeTitularesRecadastrados;
	}

	public BigDecimal getPorcentagemDependentesRecadastrados() {
		return porcentagemDependentesRecadastrados;
	}

	public void setPorcentagemDependentesRecadastrados(
			BigDecimal porcentagemDependentesRecadastrados) {
		this.porcentagemDependentesRecadastrados = porcentagemDependentesRecadastrados;
	}

	public BigDecimal getPorcentagemTitularesRecadastrados() {
		return porcentagemTitularesRecadastrados;
	}

	public void setPorcentagemTitularesRecadastrados(
			BigDecimal porcentagemTitularesRecadastrados) {
		this.porcentagemTitularesRecadastrados = porcentagemTitularesRecadastrados;
	}
	

	public int getPensionistasCancelados() {
		return pensionistasCancelados;
	}

	public void setPensionistasCancelados(int pensionistasCancelados) {
		this.pensionistasCancelados = pensionistasCancelados;
	}

	public float getQuantidadeSeguradosDependentes() {
		return quantidadeSeguradosDependentes;
	}

	public void setQuantidadeSeguradosDependentes(
			float quantidadeSeguradosDependentes) {
		this.quantidadeSeguradosDependentes = quantidadeSeguradosDependentes;
	}

	public float getQuantidadeSeguradosDependentesSuplementares() {
		return quantidadeSeguradosDependentesSuplementares;
	}

	public void setQuantidadeSeguradosDependentesSuplementares(
			float quantidadeSeguradosDependentesSuplementares) {
		this.quantidadeSeguradosDependentesSuplementares = quantidadeSeguradosDependentesSuplementares;
	}

	public float getQuantidadeSeguradosPensionistas() {
		return quantidadeSeguradosPensionistas;
	}

	public void setQuantidadeSeguradosPensionistas(
			float quantidadeSeguradosPensionistas) {
		this.quantidadeSeguradosPensionistas = quantidadeSeguradosPensionistas;
	}

	public float getQuantidadeSeguradosTitulares() {
		return quantidadeSeguradosTitulares;
	}

	public void setQuantidadeSeguradosTitulares(float quantidadeSeguradosTitulares) {
		this.quantidadeSeguradosTitulares = quantidadeSeguradosTitulares;
	}

	/**
	 * Este método inicializa a coleção de consultas promocionais do resumo com todas as consultas promocionais 
	 * liberadas para os segurados contidos no resumo.
	 */
	public void inicializaConsultasPromocionaisLiberadas(){
		consultasPromocionaisLiberadas = new ArrayList<PromocaoConsulta>();
		for (Segurado segurado : segurados) {
			for (PromocaoConsulta promocaoConsulta : segurado.getConsultasPromocionais()) {
				if(!promocaoConsulta.isVencido() && !promocaoConsulta.isUtilizado()){
					consultasPromocionaisLiberadas.add(promocaoConsulta);
				}
			}
		}
	}
	
	public List<PromocaoConsulta> getConsultasPromocionais() {
		return consultasPromocionaisLiberadas;
	}
	
	public List<PromocaoConsulta> getConsultasPromocionaisEletivas() {
		List<PromocaoConsulta> consultasPromocionaisEletivas  = new ArrayList<PromocaoConsulta>();
		for (PromocaoConsulta promocaoConsulta : getConsultasPromocionais()) {
			if(promocaoConsulta.isEletiva())
				consultasPromocionaisEletivas.add(promocaoConsulta);
		}
		return consultasPromocionaisEletivas;
	}
	
	public List<PromocaoConsulta> getConsultasPromocionaisUrgencia() {
		List<PromocaoConsulta> consultasPromocionaisUrgencia  = new ArrayList<PromocaoConsulta>();
		for (PromocaoConsulta promocaoConsulta : getConsultasPromocionais()) {
			if(promocaoConsulta.isUrgencia())
				consultasPromocionaisUrgencia.add(promocaoConsulta);
		}
		return consultasPromocionaisUrgencia;
	}

	public int getPensionistasAtivos() {
		return pensionistasAtivos;
	}

	public void setPensionistasAtivos(int pensionistasAtivos) {
		this.pensionistasAtivos = pensionistasAtivos;
	}

	public int getPensionistasInativos() {
		return pensionistasInativos;
	}

	public void setPensionistasInativos(int pensionistasInativos) {
		this.pensionistasInativos = pensionistasInativos;
	}

	public int getPensionistasSuspensos() {
		return pensionistasSuspensos;
	}

	public void setPensionistasSuspensos(int pensionistasSuspensos) {
		this.pensionistasSuspensos = pensionistasSuspensos;
	}

	public int getPensionistasCadastrados() {
		return pensionistasCadastrados;
	}

	public void setPensionistasCadastrados(int pensionistasCadastrados) {
		this.pensionistasCadastrados = pensionistasCadastrados;
	}

	public int getDependentesSuplementaresAtivos() {
		return dependentesSuplementaresAtivos;
	}

	public void setDependentesSuplementaresAtivos(int dependentesSuplementaresAtivos) {
		this.dependentesSuplementaresAtivos = dependentesSuplementaresAtivos;
	}

	public int getDependentesSuplementaresInativos() {
		return dependentesSuplementaresInativos;
	}

	public void setDependentesSuplementaresInativos(
			int dependentesSuplementaresInativos) {
		this.dependentesSuplementaresInativos = dependentesSuplementaresInativos;
	}

	public int getDependentesSuplementaresSuspensos() {
		return dependentesSuplementaresSuspensos;
	}

	public void setDependentesSuplementaresSuspensos(
			int dependentesSuplementaresSuspensos) {
		this.dependentesSuplementaresSuspensos = dependentesSuplementaresSuspensos;
	}

	public int getDependentesSuplementaresCancelados() {
		return dependentesSuplementaresCancelados;
	}

	public void setDependentesSuplementaresCancelados(
			int dependentesSuplementaresCancelados) {
		this.dependentesSuplementaresCancelados = dependentesSuplementaresCancelados;
	}

	public int getDependentesSuplementaresCadastrados() {
		return dependentesSuplementaresCadastrados;
	}

	public void setDependentesSuplementaresCadastrados(
			int dependentesSuplementaresCadastrados) {
		this.dependentesSuplementaresCadastrados = dependentesSuplementaresCadastrados;
	}

	public Integer getTotalDePensionistasRecadastrados() {
		return totalDePensionistasRecadastrados;
	}

	public void setTotalDePensionistasRecadastrados(
			Integer totalDePensionistasRecadastrados) {
		this.totalDePensionistasRecadastrados = totalDePensionistasRecadastrados;
	}

	public Integer getTotalDeDependentesSuplementaresRecadastrados() {
		return totalDeDependentesSuplementaresRecadastrados;
	}

	public void setTotalDeDependentesSuplementaresRecadastrados(
			Integer totalDeDependentesSuplementaresRecadastrados) {
		this.totalDeDependentesSuplementaresRecadastrados = totalDeDependentesSuplementaresRecadastrados;
	}

	public BigDecimal getPorcentagemDependentesSuplementaresRecadastrados() {
		return porcentagemDependentesSuplementaresRecadastrados;
	}

	public void setPorcentagemDependentesSuplementaresRecadastrados(
			BigDecimal porcentagemDependentesSuplementaresRecadastrados) {
		this.porcentagemDependentesSuplementaresRecadastrados = porcentagemDependentesSuplementaresRecadastrados;
	}

	public BigDecimal getPorcentagemPensionistasRecadastrados() {
		return porcentagemPensionistasRecadastrados;
	}

	public void setPorcentagemPensionistasRecadastrados(
			BigDecimal porcentagemPensionistasRecadastrados) {
		this.porcentagemPensionistasRecadastrados = porcentagemPensionistasRecadastrados;
	}

	public int getTotalDeSeguradosComQuestionarioAplicado() {
		return totalDeSeguradosComQuestionarioAplicado;
	}

	public void setTotalDeSeguradosComQuestionarioAplicado(
			int totalDeSeguradosComQuestionarioAplicado) {
		this.totalDeSeguradosComQuestionarioAplicado = totalDeSeguradosComQuestionarioAplicado;
	}

	public int getTotalDeTitularesComQuestionarioAplicado() {
		return totalDeTitularesComQuestionarioAplicado;
	}

	public void setTotalDeTitularesComQuestionarioAplicado(
			int totalDeTitularesComQuestionarioAplicado) {
		this.totalDeTitularesComQuestionarioAplicado = totalDeTitularesComQuestionarioAplicado;
	}

	public int getTotalDeDependentesComQuestionarioAplicado() {
		return totalDeDependentesComQuestionarioAplicado;
	}

	public void setTotalDeDependentesComQuestionarioAplicado(
			int totalDeDependentesComQuestionarioAplicado) {
		this.totalDeDependentesComQuestionarioAplicado = totalDeDependentesComQuestionarioAplicado;
	}

	public int getTotalDePensionistasComQuestionarioAplicado() {
		return totalDePensionistasComQuestionarioAplicado;
	}

	public void setTotalDePensionistasComQuestionarioAplicado(
			int totalDePensionistasComQuestionarioAplicado) {
		this.totalDePensionistasComQuestionarioAplicado = totalDePensionistasComQuestionarioAplicado;
	}

	public int getTotalDeDependentesSuplementaresComQuestionarioAplicado() {
		return totalDeDependentesSuplementaresComQuestionarioAplicado;
	}

	public void setTotalDeDependentesSuplementaresComQuestionarioAplicado(
			int totalDeDependentesSuplementaresComQuestionarioAplicado) {
		this.totalDeDependentesSuplementaresComQuestionarioAplicado = totalDeDependentesSuplementaresComQuestionarioAplicado;
	}

	public BigDecimal getPorcentagemSeguradosComQuestionarioAplicado() {
		return porcentagemSeguradosComQuestionarioAplicado;
	}

	public void setPorcentagemSeguradosComQuestionarioAplicado(
			BigDecimal porcentagemSeguradosComQuestionarioAplicado) {
		this.porcentagemSeguradosComQuestionarioAplicado = porcentagemSeguradosComQuestionarioAplicado;
	}

	public BigDecimal getPorcentagemTitularesComQuestionarioAplicado() {
		return porcentagemTitularesComQuestionarioAplicado;
	}

	public void setPorcentagemTitularesComQuestionarioAplicado(
			BigDecimal porcentagemTitularesComQuestionarioAplicado) {
		this.porcentagemTitularesComQuestionarioAplicado = porcentagemTitularesComQuestionarioAplicado;
	}

	public BigDecimal getPorcentagemDependentesComQuestionarioAplicado() {
		return porcentagemDependentesComQuestionarioAplicado;
	}

	public void setPorcentagemDependentesComQuestionarioAplicado(
			BigDecimal porcentagemDependentesComQuestionarioAplicado) {
		this.porcentagemDependentesComQuestionarioAplicado = porcentagemDependentesComQuestionarioAplicado;
	}

	public BigDecimal getPorcentagemDependentesSuplementaresComQuestionarioAplicado() {
		return porcentagemDependentesSuplementaresComQuestionarioAplicado;
	}

	public void setPorcentagemDependentesSuplementaresComQuestionarioAplicado(
			BigDecimal porcentagemDependentesSuplementaresComQuestionarioAplicado) {
		this.porcentagemDependentesSuplementaresComQuestionarioAplicado = porcentagemDependentesSuplementaresComQuestionarioAplicado;
	}

	public BigDecimal getPorcentagemPensionistasComQuestionarioAplicado() {
		return porcentagemPensionistasComQuestionarioAplicado;
	}

	public void setPorcentagemPensionistasComQuestionarioAplicado(
			BigDecimal porcentagemPensionistasComQuestionarioAplicado) {
		this.porcentagemPensionistasComQuestionarioAplicado = porcentagemPensionistasComQuestionarioAplicado;
	}

	public void setTotalDePensionistasRecadastrados(
			int totalDePensionistasRecadastrados) {
		this.totalDePensionistasRecadastrados = totalDePensionistasRecadastrados;
	}

	public void setTotalDeDependentesSuplementaresRecadastrados(
			int totalDeDependentesSuplementaresRecadastrados) {
		this.totalDeDependentesSuplementaresRecadastrados = totalDeDependentesSuplementaresRecadastrados;
	}
	
	// Métodos relativo ao gerador de arquivo do relatório detalhado de segurados.
	public void criarNomeArquivo() {
		nomeArquivo = this.getClass().getSimpleName()+".xls";
	}
	
	public byte[] getFile() throws Exception{
		ListaSeguradosExcelCreator listaCreator = new ListaSeguradosExcelCreator(); 
		listaCreator.createPlanilha(this);

		byte[] conteudoArquivo = listaCreator.getFile(new File(this.getFileName()));
		
		return conteudoArquivo;
	}
	
	public String getFileName(){
		return this.PATH + this.nomeArquivo;
	}
	
	public String getPath(){
		return this.PATH;
	}

	public ArquivoBase getArquivoXLS() {
		return arquivoXLS;
	}

	public void setArquivoXLS(ArquivoBase arquivoXLS) {
		this.arquivoXLS = arquivoXLS;
	}

	public boolean isDetalharSegurados() {
		return detalharSegurados;
	}

	public void setDetalharSegurados(boolean detalharSegurados) {
		this.detalharSegurados = detalharSegurados;
	}
}