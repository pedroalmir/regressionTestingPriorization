package br.com.infowaypi.ecarebc.associados;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecarebc.associados.validators.AbstractValidator;
import br.com.infowaypi.ecarebc.associados.validators.PrestadorPrazoRetornoUrgenciaValidator;
import br.com.infowaypi.ecarebc.associados.validators.PrestadorValidator;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.LoteDeGuias;
import br.com.infowaypi.ecarebc.atendimentos.acordos.AbstractAcordo;
import br.com.infowaypi.ecarebc.atendimentos.acordos.AcordoCBHPM;
import br.com.infowaypi.ecarebc.atendimentos.acordos.AcordoDiaria;
import br.com.infowaypi.ecarebc.atendimentos.acordos.AcordoGasoterapia;
import br.com.infowaypi.ecarebc.atendimentos.acordos.AcordoPacote;
import br.com.infowaypi.ecarebc.atendimentos.acordos.AcordoPacoteHonorario;
import br.com.infowaypi.ecarebc.atendimentos.acordos.AcordoPorte;
import br.com.infowaypi.ecarebc.atendimentos.acordos.AcordoTaxa;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.consumo.ColecaoConsumosInterface;
import br.com.infowaypi.ecarebc.consumo.ComponenteColecaoConsumos;
import br.com.infowaypi.ecarebc.consumo.ConsumoInterface;
import br.com.infowaypi.ecarebc.consumo.periodos.Mes;
import br.com.infowaypi.ecarebc.consumo.periodos.Periodo;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.Role;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.DependenteFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.FluxoFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.TitularFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AbstractFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.Faturamento;
import br.com.infowaypi.ecarebc.procedimentos.Pacote;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.NotEquals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.financeiro.InformacaoFinanceira;
import br.com.infowaypi.msr.financeiro.InformacaoFinanceiraInterface;
import br.com.infowaypi.msr.pessoa.PessoaJuridica;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe que representa um prestador no sistema.
 * 
 * @author root
 */
@SuppressWarnings("unchecked")
public class Prestador extends ImplColecaoSituacoesComponent implements
		TitularFinanceiroInterface {

	private static final long serialVersionUID = 1L;
	
	public static final int TIPO_PRESTADOR_HOSPITAL = 1;
	public static final int TIPO_PRESTADOR_LABORATORIO = 2;
	public static final int TIPO_PRESTADOR_MEDICOS = 3;
	public static final int TIPO_PRESTADOR_DENTISTAS = 4;
	public static final int TIPO_PRESTADOR_OUTROS = 5;
	public static final int TIPO_PRESTADOR_CLINICAS_DE_EXAMES = 6;
	public static final int TIPO_PRESTADOR_CLINICAS_DE_ODONTOLOGIA = 7;
	public static final int TIPO_PRESTADOR_CLINICAS_AMBULATORIAIS = 8;
	public static final int TIPO_PRESTADOR_ANESTESISTA = 9;
	public static final int TIPO_PRESTADOR_TODOS = 999;
	public static final int PRAZO_MAXIMO_RETORNO_URGENCIA = 30;
	public static final int PRAZO_MINIMO_RETORNO_URGENCIA = 1;
	
	public static Long HEMOPI = 1485626L;
	
	public enum TipoIncremento {
		POSITIVO {
			@Override
			public BigDecimal incrementar(BigDecimal valor1, BigDecimal valor2) {
				return valor1.add(valor2);
			}
		}, 
		NEGATIVO {
			@Override
			public BigDecimal incrementar(BigDecimal valor1, BigDecimal valor2) {
				return valor1.subtract(valor2);
			}
		};
		
		public abstract BigDecimal incrementar(BigDecimal valor1, BigDecimal valor2);
	};

	private Long idPrestador;

	private PessoaJuridica pessoaJuridica;

	/**
	 * Data de ínicio do contrato vigente.  
	 */
	private Date dataInicioVigencia;
	
	/**
	 * Data de término da vigente do contrato.  
	 */
	private Date dataTerminoVigencia;

	private boolean verificarTetos;

	private boolean verificaProcedimentosAssociados;
	
	private int categoria;

	private BigDecimal tetoFinanceiroParaInternacoes;

	private BigDecimal tetoFinanceiroParaUrgencias;

	private BigDecimal tetoFinanceiroParaConsultas;

	private BigDecimal consumoAtualDeConsultas;

	private BigDecimal tetoFinanceiroParaExames;
	
	private BigDecimal consumoAtualDeExames;

	private BigDecimal tetoFinanceiroParaConsultasOdonto;

	private BigDecimal tetoFinanceiroParaTratamentosOdonto;

	private UsuarioInterface usuario;

	private boolean pagaIss;

	private boolean pagaINSS;

	private boolean pagaIR;

	private boolean pessoaFisica;

	private boolean geraFaturamento;

	private boolean fazInternacao;

	private boolean fazInternacaoUrgencia;

	private boolean fazInternacaoEletiva;

	private boolean fazConsultaUrgencia;

	private boolean fazAtendimentoUrgencia;

	private boolean fazConsulta;

	private boolean fazExame;

	private boolean fazOdontologico;
	
	private boolean exigeEntregaLote;
	
	private boolean geraHonorario;
	
	private boolean atendePacientesCronicos;
	
	private boolean prestadorSemSalaObservacao;
		
	/**
	 * Representa a data em que o prestador foi marcado como "exige entrega de lote".
	 */
	private Date dataInicioEntregaLote;
	
	/**
	 * Representa prazo, em dias, para volta de um paciente para urgência no prestador, com a modalidade de atendimento subsequente.
	 */
	private int periodoParaVoltaNaUrgencia;

	private Long faixa;

	private Set<UsuarioDoPrestador> usuarios;

	private Set<Profissional> profissionais;

	private Set<GuiaSimples> guias;

	private Set<Especialidade> especialidades;

	private Set<ItemEspecialidade> itensEspecialidades = new HashSet<ItemEspecialidade>();
	
	 /**
	  * Estabelece o relacionalento entre  prestador,a especialidade e a tabela CBHPM.
	  * Este relacionamento é referente ao código - procedimento - de consulta para aquela especialidade e para aquele prestador. 
	  */
	private Set<PrestadorEspecialidadeTabelaCBHPM> prestadoresEspecialidadesTabelaCBHPM = new HashSet<PrestadorEspecialidadeTabelaCBHPM>();

	private Set<AbstractFaturamento> faturamentos;

	private Set<AcordoPorte> acordosPorte;

	private Set<AcordoCBHPM> acordosCBHPM;
	
	private Set<TabelaCBHPM> procedimentos;

//	private Set<AcordoMatMed> acordosMatmed;

	private Set<AcordoDiaria> acordosDiaria;

	private Set<AcordoTaxa> acordosTaxa;

	private Set<AcordoGasoterapia> acordosGasoterapia;

	private Set<AcordoPacote> acordosPacote;
	
	private Set<AcordoPacoteHonorario> acordosPacoteHonorarios = new HashSet<AcordoPacoteHonorario>();
	
	public static Collection<AbstractValidator> prestadorValidators = new ArrayList<AbstractValidator>();

	private InformacaoFinanceiraInterface informacaoFinanceira;

	private ColecaoConsumosInterface consumoIndividual;
	
	private boolean capitacao;
	
	private Set<LoteDeGuias> lotesDeGuias;

	/**
	 * Código do prestador anterior à migração da base de dados, para uma possível migração.
	 */
	private String codigoLegado;
	
	private Integer tipoPrestador;
	
	/**
	 * Atributo transiente usado para mostrar no jhm de solicatar exames, o valor total de uma guia
	 * que for confirmada neste prestador.
	 */
	private BigDecimal valorTotalProcedimentos;
	
	/**
	 * Atributo transiente para informar se o consumo deve ou não ser alterado.
	 * Por padrão ele permite que o consumo seja atualizado. 
	 * Caso não seja o desejado o valor da variavel deve ser atualizado.
	 * 
	 */
	private boolean atualizarConsumo = true;

	static {
		prestadorValidators.add(new PrestadorValidator());
		prestadorValidators.add(new PrestadorPrazoRetornoUrgenciaValidator());
	}

	public Prestador() {
		this(null);
	}

	public Prestador(UsuarioInterface usuario) {
		this.mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(), MotivoEnum.CADASTRO_PRESTADOR.getMessage(), new Date());
		pessoaJuridica 	= new PessoaJuridica();
		this.usuario 	= new Usuario();
		usuarios 		= new HashSet<UsuarioDoPrestador>();
		profissionais 	= new HashSet<Profissional>();
		guias 			= new HashSet<GuiaSimples>();
		acordosPorte 	= new HashSet<AcordoPorte>();
		acordosCBHPM 	= new HashSet<AcordoCBHPM>();
//		acordosMatmed 	= new HashSet<AcordoMatMed>();
		acordosDiaria 	= new HashSet<AcordoDiaria>();
		acordosTaxa 	= new HashSet<AcordoTaxa>();
		procedimentos 	= new HashSet<TabelaCBHPM>();
		consumoIndividual 	= new ComponenteColecaoConsumos();
		acordosGasoterapia 	= new HashSet<AcordoGasoterapia>();
		this.faturamentos 	= new HashSet<AbstractFaturamento>();
		this.especialidades = new HashSet<Especialidade>();
		this.informacaoFinanceira = new InformacaoFinanceira();
		this.geraFaturamento = true;
		this.tetoFinanceiroParaInternacoes = BigDecimal.ZERO;
		this.tetoFinanceiroParaUrgencias = BigDecimal.ZERO;
		this.tetoFinanceiroParaConsultas = BigDecimal.ZERO;
		this.tetoFinanceiroParaExames = BigDecimal.ZERO;
		this.tetoFinanceiroParaConsultasOdonto = BigDecimal.ZERO;
		this.tetoFinanceiroParaTratamentosOdonto = BigDecimal.ZERO;
		this.lotesDeGuias = new HashSet<LoteDeGuias>();
	}

	public BigDecimal getTetoFinanceiroParaUrgencias() {
		return tetoFinanceiroParaUrgencias;
	}

	public void setTetoFinanceiroParaUrgencias(
			BigDecimal tetoFinanceiroParaUrgencia) {
		this.tetoFinanceiroParaUrgencias = tetoFinanceiroParaUrgencia;
	}

	public int getCategoria() {
		return categoria;
	}

	public void setCategoria(int categoria) {
		this.categoria = categoria;
	}

	public void setInformacaoFinanceira(
			InformacaoFinanceiraInterface informacaoFinanceira) {
		this.informacaoFinanceira = informacaoFinanceira;
	}

	public Set<Especialidade> getEspecialidades(){
		Set<Especialidade> especialidades =  new HashSet<Especialidade>();
		for(ItemEspecialidade itemEspecialidade : getItensEspecialidadesAtivos()){
			especialidades.add(itemEspecialidade.getEspecialidade());
		}
		return especialidades;
	}
	
	public Set<Especialidade> getEspecialidadesOdonto() {
		Set<Especialidade> especialidades =  new HashSet<Especialidade>();
		Iterator<ItemEspecialidade> iterator = itensEspecialidades.iterator();
		while (iterator.hasNext()) {
			ItemEspecialidade itemEspecialidade = (ItemEspecialidade) iterator.next();
			Especialidade especialidade = itemEspecialidade.getEspecialidade();
			if(especialidade.isAtiva() && especialidade.getClasse().equals(2)){
				especialidades.add(especialidade);
			}
		}
		return especialidades;
	}
	
	public Set<Especialidade> getEspecialidades(boolean urgencia, boolean eletivo) {
		
		Set<Especialidade> especialidades =  new HashSet<Especialidade>();
		
		boolean opcaoUrgenciaEeletivo = urgencia && eletivo;
		boolean opcaoApenasUrgencia = urgencia && !eletivo;
		boolean opcaoApenasEletivo = !urgencia && eletivo;
		boolean nemhumaOpcao = !urgencia && !eletivo;
		
		for(ItemEspecialidade itemEspecialidade: getItensEspecialidadesAtivos()){
				
			boolean especialidadeUrgenciaOuEletivo = itemEspecialidade.isUrgencia() || itemEspecialidade.isEletivo();
			boolean especialidadeApenasUrgencia = itemEspecialidade.isUrgencia();
			boolean especialidadeApenasEletivo =  itemEspecialidade.isEletivo();
			
			if (opcaoUrgenciaEeletivo && especialidadeUrgenciaOuEletivo ){
				especialidades.add(itemEspecialidade.getEspecialidade());
			}
			
			if (opcaoApenasUrgencia && especialidadeApenasUrgencia){
				especialidades.add(itemEspecialidade.getEspecialidade());
			}
			
			if (opcaoApenasEletivo && especialidadeApenasEletivo){
				especialidades.add(itemEspecialidade.getEspecialidade());
			}
			
			if (nemhumaOpcao){
				especialidades.add(itemEspecialidade.getEspecialidade());
			}
		}
		return especialidades;
	}


	public void setEspecialidades(Set<Especialidade> especialidades) {
		this.especialidades = especialidades;
	}

	public Set<AbstractFaturamento> getFaturamentos() {
		return faturamentos;
	}

	public void setFaturamentos(Set<AbstractFaturamento> faturamentos) {
		this.faturamentos = faturamentos;
	}

	public Faturamento getFaturamento(Date competencia) {
		for (AbstractFaturamento faturamento : faturamentos) {
			boolean faturamentoCancelado = (faturamento.getStatus() == Constantes.FATURAMENTO_CANCELADO);
			if (Utils.compararCompetencia(faturamento.getCompetencia(),	competencia) == 0
					&& !faturamentoCancelado && faturamento.isFaturamentoNormal()){
				return (Faturamento) faturamento;
			}	
		}
		return null;
	}
	
	public AbstractFaturamento getFaturamentoPassivo(Date competencia) {
		for (AbstractFaturamento faturamento : faturamentos) {
			boolean faturamentoCancelado = (faturamento.getStatus() == Constantes.FATURAMENTO_CANCELADO);
			boolean isFaturamentoPassivo = faturamento.isFaturamentoPassivo();
			boolean isMesmaCompetencia = Utils.compararCompetencia(faturamento.getCompetencia(),competencia) == 0;
			
			if (isMesmaCompetencia && !faturamentoCancelado && isFaturamentoPassivo)
				return faturamento;
		}
		return null;
	}

	public boolean isFazConsulta() {
		return fazConsulta;
	}

	public void setFazConsulta(boolean fazConsulta) {
		this.fazConsulta = fazConsulta;
	}

	public boolean isFazExame() {
		return fazExame;
	}

	public void setFazExame(boolean fazExame) {
		this.fazExame = fazExame;
	}

	public boolean isFazInternacao() {
		return fazInternacao;
	}

	public void setFazInternacao(boolean fazInternacao) {
		this.fazInternacao = fazInternacao;
	}

	public boolean isFazInternacaoUrgencia() {
		return fazInternacaoUrgencia;
	}

	public void setFazInternacaoUrgencia(boolean fazInternacaoUrgencia) {
		this.fazInternacaoUrgencia = fazInternacaoUrgencia;
	}

	public boolean isFazOdontologico() {
		return fazOdontologico;
	}

	public void setFazOdontologico(boolean fazOdontologico) {
		this.fazOdontologico = fazOdontologico;
	}

	public List<GuiaSimples> getGuiasConfirmadasEFechadas(Date competencia)
			throws ValidateException {
		List<GuiaSimples> guiasConfirmadas = new ArrayList<GuiaSimples>();
		for (GuiaSimples guia : this.getGuias()) {
			Date competenciaAtendimento = Utils.gerarCompetencia(Utils.format(guia.getDataAtendimento(), "MM/yyyy"));
			competencia 				= Utils.convert(competencia);
			competenciaAtendimento 		= Utils.convert(competenciaAtendimento);
			boolean isGuiaConfirmada 	= guia.isSituacaoAtual(SituacaoEnum.CONFIRMADO.descricao());
			boolean isGuiaFechada 		= guia.isSituacaoAtual(SituacaoEnum.FECHADO.descricao());
			if (((isGuiaConfirmada) || ((isGuiaFechada)))
				&& (competenciaAtendimento.toString().equals(competencia.toString()))) {
				guiasConfirmadas.add(guia);
			}
		}
		return guiasConfirmadas;
	}
	
	public Set<GuiaSimples> getGuiasNaoCanceladas(){
		Set<GuiaSimples> guias = new HashSet<GuiaSimples>();
		for (GuiaSimples guia : getGuias()) {
			if(!guia.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()))
				guias.add(guia);
		}
		return guias;
	}
	
	public Set<GuiaSimples> getGuias() {
		return guias;
	}
	
	public Set<GuiaSimples> getGuias(Date competencia) {
		Set<GuiaSimples> guias = new HashSet<GuiaSimples>();
		for (GuiaSimples guia : this.getGuias()) {
			if(Utils.compararCompetencia(competencia, guia.getDataMarcacao()) == 0){
				guias.add(guia);
			}
		}
		return guias;
	}

	public Set<ItemEspecialidade> getItensEspecialidades() {
		return itensEspecialidades;
	}
	
	public Set<ItemEspecialidade> getItensEspecialidadesAtivos() {
		Set<ItemEspecialidade> itensEspecialidadesAtivos = new HashSet<ItemEspecialidade>();
		for(ItemEspecialidade itemEspecialidade : itensEspecialidades){
			if(itemEspecialidade.isSituacaoAtual(SituacaoEnum.ATIVO.descricao())){
				itensEspecialidadesAtivos.add(itemEspecialidade);
			}
		}
		return itensEspecialidadesAtivos;
	}
	
	public void setItensEspecialidades(Set<ItemEspecialidade> itensEspecialidades) {
		this.itensEspecialidades = itensEspecialidades;
	}

	public void setGuias(Set<GuiaSimples> guias) {
		this.guias = guias;
	}

	public Long getIdPrestador() {
		return idPrestador;
	}

	public void setIdPrestador(Long idPrestador) {
		this.idPrestador = idPrestador;
	}

	public boolean isPagaIss() {
		return pagaIss;
	}

	public void setPagaIss(boolean pagaIss) {
		this.pagaIss = pagaIss;
	}

	public boolean isPessoaFisica() {
		return pessoaFisica;
	}

	public void setPessoaFisica(boolean pessoaFisica) {
		this.pessoaFisica = pessoaFisica;
	}

	public PessoaJuridica getPessoaJuridica() {
		return pessoaJuridica;
	}

	public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
		this.pessoaJuridica = pessoaJuridica;
	}

	public Date getDataInicioVigencia() {
		return dataInicioVigencia;
	}

	public void setDataInicioVigencia(Date dataInicioVigencia) {
		this.dataInicioVigencia = dataInicioVigencia;
	}

	public Date getDataTerminoVigencia() {
		return dataTerminoVigencia;
	}

	public void setDataTerminoVigencia(Date dataTerminoVigencia) {
		this.dataTerminoVigencia = dataTerminoVigencia;
	}
	
	public boolean isGeraFaturamento() {
		return geraFaturamento;
	}

	public void setGeraFaturamento(boolean geraFaturamento) {
		this.geraFaturamento = geraFaturamento;
	}

	public Set<Profissional> getProfissionais() {
		return profissionais;
	}
	
	public Set<Profissional> getProfissionaisNaoOdontologicos() {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new NotEquals("odontologico",true));
		Set<Profissional> profissionais = (Set<Profissional>)sa.list(Profissional.class);
		return profissionais;
	}

	public void setProfissionais(Set<Profissional> profissionais) {
		this.profissionais = profissionais;
	}

	public BigDecimal getTetoFinanceiroParaConsultas() {
		return tetoFinanceiroParaConsultas;
	}

	public void setTetoFinanceiroParaConsultas(
			BigDecimal tetoFinanceiroParaConsultas) {
		this.tetoFinanceiroParaConsultas = tetoFinanceiroParaConsultas;
	}

	public BigDecimal getTetoFinanceiroParaExames() {
		return tetoFinanceiroParaExames;
	}

	public void setTetoFinanceiroParaExames(BigDecimal tetoFinanceiroParaExames) {
		this.tetoFinanceiroParaExames = tetoFinanceiroParaExames;
	}

	public BigDecimal getTetoFinanceiroParaInternacoes() {
		return tetoFinanceiroParaInternacoes;
	}

	public void setTetoFinanceiroParaInternacoes(
			BigDecimal tetoFinanceiroParaInternacao) {
		this.tetoFinanceiroParaInternacoes = tetoFinanceiroParaInternacao;
	}

	public UsuarioInterface getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioInterface usuario) {
		this.usuario = usuario;
	}

	public Set<UsuarioDoPrestador> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(Set<UsuarioDoPrestador> usuarios) {
		this.usuarios = usuarios;
	}

	public boolean isVerificarTetos() {
		return verificarTetos;
	}

	public void setVerificarTetos(boolean verificarTetos) {
		this.verificarTetos = verificarTetos;
	}

	public void tocarObjetos() {
		this.getPessoaJuridica().getEndereco().getPontoDeReferencia();
		this.getPessoaJuridica().getEndereco().getComplemento();
		this.getPessoaJuridica().getEndereco().getLogradouro();
		this.getPessoaJuridica().getEndereco().tocarObjetos();
		this.getConsumoIndividual().getConsumos().size();
		this.getProcedimentos().size();
		
		if (this.getAcordosDiaria() != null) {
			this.getAcordosDiaria();
			for (AcordoDiaria acordo : this.getAcordosDiaria()) {
				acordo.tocarObjetos();
			}
		}

		if (this.getAcordosTaxa() != null) {
			this.getAcordosTaxa();
			for (AcordoTaxa acordo : this.getAcordosTaxa()) {
				acordo.tocarObjetos();
			}
		}
		
		if (this.getAcordosGasoterapia() != null) {
			this.getAcordosGasoterapia();
			for (AcordoGasoterapia acordo : this.getAcordosGasoterapia()) {
				acordo.tocarObjetos();
			}
		}
		
		if (this.getAcordosPacote() != null) {
			this.getAcordosPacote();
			for (AcordoPacote acordo : this.getAcordosPacote()) {
				acordo.tocarObjetos();
			}
		}
		
		if(this.getAcordosCBHPM() != null){
			this.getAcordosCBHPM().size();
			for (AcordoCBHPM acordo : this.getAcordosCBHPM()) {
				acordo.tocarObjetos();
			}
		}
		
		if (this.getConsumoIndividual() != null){
			this.getConsumoIndividual().getConsumos().size();
		}
		
		for (Especialidade esp : this.getEspecialidades()) {
			esp.tocarObjetos();
		}
	}

	public Boolean validate(UsuarioInterface usuario) throws ValidateException {
		for (AbstractValidator validator : prestadorValidators) {
			validator.execute(this);
		}
		
		if (this.isExigeEntregaLote() && getDataInicioEntregaLote() == null){
			this.setDataInicioEntregaLote(new Date());
		} else if(!this.isExigeEntregaLote()){
			this.setDataInicioEntregaLote(null);
		}

		return true;
	}

	public Boolean isPrestadorAnestesista() {
		return this.tipoPrestador.equals(Prestador.TIPO_PRESTADOR_ANESTESISTA);
	}
	
	public Boolean isPrestadorConsultas() {
		return this.isFazConsulta() && !this.isFazExame()
				&& !this.isFazOdontologico()
				&& !this.isFazAtendimentoUrgencia()
				&& !this.isFazInternacaoUrgencia()
				&& !this.isFazInternacaoEletiva();
	}
	
	public Boolean isPrestadorExames() {
		return this.isFazExame() 
			&& !this.isFazConsulta()
			&& !this.isFazOdontologico()
			&& !this.isFazAtendimentoUrgencia()
			&& !this.isFazInternacaoUrgencia()
			&& !this.isFazInternacaoEletiva();
	}

	public Boolean isPrestadorConsultasExames() {
		return this.isFazExame() 
			&& this.isFazConsulta()
			&& !this.isFazOdontologico()
			&& !this.isFazAtendimentoUrgencia()
			&& !this.isFazInternacaoUrgencia()
			&& !this.isFazInternacaoEletiva();
	}

	public Boolean isPrestadorInternacaoUrgencia() {
		return !this.isFazExame()
			&& !this.isFazConsulta()
			&& !this.isFazOdontologico()
			&& (this.isFazAtendimentoUrgencia()
			|| this.isFazInternacaoUrgencia() 
			|| this.isFazInternacaoEletiva());
	}
	
	public Boolean isPrestadorConsExmIntUrg(){
		return this.isFazExame() 
			&& this.isFazConsulta()
			&& !this.isFazOdontologico()
			&& (this.isFazAtendimentoUrgencia()
			|| this.isFazInternacaoUrgencia()
			|| this.isFazInternacaoEletiva()); 
	}

	public Boolean isPrestadorOdonto() {
		return !this.isFazExame()  
			&& !this.isFazConsulta()
			&& this.isFazOdontologico() 
			&& !this.isFazAtendimentoUrgencia()
			&& !this.isFazInternacaoUrgencia()
			&& !this.isFazInternacaoEletiva();
	}

	public Boolean isPrestadorCompleto() {
		return this.isFazExame() 
			&& this.isFazConsulta()
			&& this.isFazOdontologico() 
			&& this.isFazAtendimentoUrgencia()
			&& this.isFazInternacaoUrgencia()
			&& this.isFazInternacaoEletiva();
	}
	public Boolean isPrestadorInternacaoExameUrgencia() {
		return this.isFazExame() 
			&& !this.isFazConsulta()
			&& !this.isFazOdontologico()
			&& (this.isFazAtendimentoUrgencia()
			|| this.isFazInternacaoUrgencia()
			|| this.isFazInternacaoEletiva());
	}

	public Boolean isPrestadorSemAtividades() {
		return !this.isFazExame() 
			&& !this.isFazConsulta()
			&& !this.isFazOdontologico()
			&& !this.isFazAtendimentoUrgencia()
			&& !this.isFazInternacaoUrgencia()
			&& !this.isFazInternacaoEletiva();
	}
	
	public Boolean isPrestadorConsInternacao() {
		return !this.isFazExame() 
			&& this.isFazConsulta()
			&& !this.isFazOdontologico()
			&& (this.isFazAtendimentoUrgencia()
			|| this.isFazInternacaoUrgencia()
			|| this.isFazInternacaoEletiva());
	}

	/**
	 * Seta as informações do usuário do prestador
	 * 
	 * @param prest
	 * @throws ValidateException
	 * @throws ValidateException
	 */
	public void atualizaUsuario() throws ValidateException {
		if (this.getUsuario() != null) {
			this.getUsuario().setNome(this.getPessoaJuridica().getFantasia());

			if (isPrestadorAnestesista()){
				this.getUsuario().setRole(Role.PRESTADOR_ANESTESISTA.getValor());
			}
			
			else if (isPrestadorCompleto()){
				this.getUsuario().setRole(Role.PRESTADOR_COMPLETO.getValor());
			}
			
			else if(isPrestadorConsExmIntUrg()){
				this.getUsuario().setRole(Role.PRESTADOR_CONS_EXM_INT_URG.getValor());
			}
			
			else if (isPrestadorConsultasExames()){
				this.getUsuario().setRole(Role.PRESTADOR_CONS_EXM.getValor());
			}
			
			else if (isPrestadorConsultas()){
				this.getUsuario().setRole(Role.PRESTADOR_CONSULTA.getValor());
			}
			
			else if (isPrestadorExames()){
				this.getUsuario().setRole(Role.PRESTADOR_EXAME.getValor());
			}
			
			else if (isPrestadorInternacaoUrgencia()){
				this.getUsuario().setRole(Role.PRESTADOR_INT_URG.getValor());
			}
			
			else if (isPrestadorInternacaoExameUrgencia()){
				this.getUsuario().setRole(Role.PRESTADOR_INT_EXM_URG.getValor());
			}
			
			else if (isPrestadorOdonto()){
				this.getUsuario().setRole(Role.PRESTADOR_ODONTOLOGICO.getValor());
			}

			else if (isPrestadorSemAtividades() && !isPrestadorAnestesista() && !this.isGeraHonorario()){
				throw new ValidateException(MensagemErroEnum.PRESTADOR_SEM_ATIVIDADE.getMessage());
			}
			else {
				this.getUsuario().setRole(Role.PRESTADOR_COMPLETO.getValor());
			}
			
			this.getUsuario().validate();
		}
	}

	/**
	 * Seta uma nova senha para o usuario prestador
	 * 
	 * @param prest
	 */
	public void alteraSenha() {
		String senhaDigitada = this.getUsuario().getNovaSenhaDigitada();
		String senhaConfirmada = this.getUsuario().getNovaSenhaConfirmacao();
		if ((Utils.isStringVazia(senhaDigitada))
				&& (Utils.isStringVazia(senhaConfirmada))) {
			Long id = this.getIdPrestador();
			Prestador prestador = (Prestador) ImplDAO.findById(id,
					Prestador.class);
			senhaDigitada = prestador.getUsuario().getNovaSenhaDigitada();
			senhaConfirmada = prestador.getUsuario().getNovaSenhaConfirmacao();
			this.getUsuario().setNovaSenhaDigitada(senhaDigitada);
			this.getUsuario().setNovaSenhaConfirmacao(senhaConfirmada);
		}
	}

	public Set<AcordoCBHPM> getAcordosCBHPM() {
		return acordosCBHPM;
	}

	public void setAcordosCBHPM(Set<AcordoCBHPM> acordosCBHPM) {
		this.acordosCBHPM = acordosCBHPM;
	}

	public Set<AcordoDiaria> getAcordosDiaria() {
		return acordosDiaria;
	}

	public void setAcordosDiaria(Set<AcordoDiaria> acordosDiaria) {
		this.acordosDiaria = acordosDiaria;
	}

	public Set<AcordoGasoterapia> getAcordosGasoterapia() {
		return acordosGasoterapia;
	}

	public void setAcordosGasoterapia(Set<AcordoGasoterapia> acordosGasoterapia) {
		this.acordosGasoterapia = acordosGasoterapia;
	}

//	public Set<AcordoMatMed> getAcordosMatmed() {
//		return acordosMatmed;
//	}
//
//	public void setAcordosMatmed(Set<AcordoMatMed> acordosMatmed) {
//		this.acordosMatmed = acordosMatmed;
//	}

	public Set<AcordoPorte> getAcordosPorte() {
		return acordosPorte;
	}

	public void setAcordosPorte(Set<AcordoPorte> acordosPorte) {
		this.acordosPorte = acordosPorte;
	}

	public Set<AcordoTaxa> getAcordosTaxa() {
		return acordosTaxa;
	}

	public void setAcordosTaxa(Set<AcordoTaxa> acordosTaxa) {
		this.acordosTaxa = acordosTaxa;
	}

	public boolean isFazAtendimentoUrgencia() {
		return fazAtendimentoUrgencia;
	}

	public void setFazAtendimentoUrgencia(boolean fazAtendimentoUrgencia) {
		this.fazAtendimentoUrgencia = fazAtendimentoUrgencia;
		// PROVIsório
		this.fazConsultaUrgencia = true;
	}

	public boolean isFazConsultaUrgencia() {
		return fazConsultaUrgencia;
	}

	public void setFazConsultaUrgencia(boolean fazConsultaUrgencia) {
		this.fazConsultaUrgencia = fazConsultaUrgencia;
	}

	public boolean isFazInternacaoEletiva() {
		return fazInternacaoEletiva;
	}

	public void setFazInternacaoEletiva(boolean fazInternacaoEletiva) {
		this.fazInternacaoEletiva = fazInternacaoEletiva;
	}

	public boolean isPagaINSS() {
		return pagaINSS;
	}

	public void setPagaINSS(boolean pagaINSS) {
		this.pagaINSS = pagaINSS;
	}

	public boolean isPagaIR() {
		return pagaIR;
	}

	public void setPagaIR(boolean pagaIR) {
		this.pagaIR = pagaIR;
	}

	public Set<AcordoPacote> getAcordosPacote() {
		return acordosPacote;
	}

	public void setAcordosPacote(Set<AcordoPacote> acordosPacote) {
		this.acordosPacote = acordosPacote;
	}
	
	public AcordoPacote getAcordoPacote(Pacote pacote) {
		for (AcordoPacote acordo : getAcordosPacoteAtivos()) {
			if (acordo.getPacote().getIdPacote().equals(pacote.getIdPacote())){
				return acordo;
			}
		}
		return null;
	}

	public Long getFaixa() {
		return faixa;
	}

	public void setFaixa(Long faixa) {
		this.faixa = faixa;
	}

	public String getDescricaoFaixa() {
		String nome = "";

		switch (this.faixa.intValue()) {
		case 1:
			nome = "De 0 a 50 guias";
			break;
		case 2:
			nome = "De 51 a 150 guias";
			break;
		case 3:
			nome = "De 151 a 500 guias";
			break;
		case 4:
			nome = "De 501 a 1000 guias";
			break;
		case 5:
			nome = "De 1001 a 20000 guias";
			break;
		}
		return nome;
	}

	// CONSUMO
	public ConsumoInterface getConsumoPorCompetencia(Date competencia)
			throws Exception {
		if (this.getConsumoIndividual() == null) {
			ComponenteColecaoConsumos colecaoConsumos = new ComponenteColecaoConsumos();
			ImplDAO.save(colecaoConsumos);
			return colecaoConsumos.getConsumo(competencia,Periodo.MENSAL);
		}
		return this.getConsumoIndividual().getConsumo(competencia,
				Periodo.MENSAL);
	}

	public ColecaoConsumosInterface getConsumoIndividual() {
		return consumoIndividual;
	}

	public void setConsumoIndividual(ColecaoConsumosInterface consumoIndividual) throws Exception {
		this.consumoIndividual = consumoIndividual;
	}
	
	public void incrementarConsumoFinanceiro(GuiaSimples guia) throws Exception {
		alterarConsumo(guia, TipoIncremento.POSITIVO);
	}

	public void decrementarConsumoFinanceiro(GuiaSimples guia) throws Exception {
		alterarConsumo(guia, TipoIncremento.NEGATIVO);
	}

	public void atualizarConsumoFinanceiro(GuiaSimples guia,int numeroProcedimentos, TipoIncremento tipo) throws Exception {
		atualizarConsumo(guia, numeroProcedimentos, tipo);
	}

	private void atualizarConsumo(GuiaSimples guia, int numeroProcedimentos,TipoIncremento tipo) throws Exception {
		boolean guiaFechada = guia.isSituacaoAtual(SituacaoEnum.FECHADO.descricao());
		boolean guiaAuditada = guia.isSituacaoAtual(SituacaoEnum.AUDITADO.descricao());
		boolean guiaAgendada = guia.isSituacaoAtual(SituacaoEnum.AGENDADA.descricao());
		boolean guiaConfirmada = guia.isSituacaoAtual(SituacaoEnum.CONFIRMADO.descricao());
		
		Date dataConsumo = guia.getDataAtendimento() != null ? guia.getDataAtendimento() : new Date();
		ConsumoInterface consumoAtual = this.getConsumo(dataConsumo);
		if (consumoAtual == null)
			throw new RuntimeException("Ocorreu um erro na criação do "
					+ "consumo para a competência "
					+ Utils.format(guia.getDataAtendimento(), "MM/yyyy"));

		BigDecimal valor = BigDecimal.ZERO;
		valor = TipoIncremento.POSITIVO.equals(tipo) ? guia.getValorParcial() : guia.getValorParcial().multiply(BigDecimal.valueOf(Double.valueOf(-1)));

		if (guia.isUrgencia()) {
			if (guiaFechada || guiaAuditada){
				consumoAtual.atualizaSomatorioUrgenciasFechadas(valor,numeroProcedimentos,tipo);
				if(guia.isConsultaUrgencia() || guia.isAtendimentoUrgencia()){
					consumoAtual.atualizaSomatorioConsultasAtendimentosUrgenciasFechadas(valor, numeroProcedimentos, tipo);
				}
			}
			else{
				consumoAtual.atualizaSomatorioUrgenciasAbertas(valor,numeroProcedimentos, tipo);
				if(guia.isConsultaUrgencia() || guia.isAtendimentoUrgencia()){
					consumoAtual.atualizaSomatorioConsultasAtendimentosUrgenciasAbertas(valor, numeroProcedimentos, tipo);
				}
			}
		}

		if (guia.isInternacao()) {
			if (guiaFechada || guiaAuditada)
				consumoAtual.atualizaSomatorioInternacoesFechadas(valor,numeroProcedimentos, tipo);
			else
				consumoAtual.atualizaSomatorioInternacoesAbertas(valor,numeroProcedimentos, tipo);
		}
		
		if(guia.isExame()){
			if(guiaConfirmada)
				consumoAtual.atualizaSomatorioExamesConfirmados(valor,numeroProcedimentos,tipo);
			else if (guiaAgendada)
				consumoAtual.atualizaSomatorioExamesAgendados(valor,numeroProcedimentos,tipo);
		}

		

	}

	private void alterarConsumo(GuiaSimples guia, TipoIncremento tipo) throws Exception {
		
		Date dataConsumo = guia.getDataAtendimento() != null ? guia.getDataAtendimento() : new Date();
		ConsumoInterface consumoAtual = this.getConsumo(dataConsumo);
		if (consumoAtual == null)
			throw new RuntimeException(MensagemErroEnum.ERRO_CRIACAO_CONSUMO.getMessage(Utils.format(guia.getDataAtendimento(),"MM/yyyy")));

		BigDecimal valor = TipoIncremento.POSITIVO.equals(tipo) ? guia
				.getValorTotal() : guia.getValorTotal().multiply(BigDecimal.valueOf(Float.valueOf(-1)));

		SituacaoInterface situacaoAtual = guia.getSituacao();
		if (SituacaoEnum.CANCELADO.descricao().equals(situacaoAtual.getDescricao()))
			situacaoAtual = guia.getSituacaoAnterior(situacaoAtual);

		if (guia.isConsulta()) {
			if (situacaoAtual.getDescricao().equals(SituacaoEnum.CONFIRMADO.descricao()))
				consumoAtual.incrementaSomatorioConsultasConfirmadas(valor,tipo,guia.getDataMarcacao(), !guia.isFromPrestador());
			else
				consumoAtual.incrementaSomatorioConsultasAgendadas(valor,tipo);
		}

		if (guia.isExame()) {
			
			boolean decrementaExamesAgendados = false;
			if((guia.getGuiaOrigem() != null) && (guia.getGuiaOrigem().isSituacaoAtual(SituacaoEnum.REALIZADO.descricao()))){
				decrementaExamesAgendados = true;
			}
			
			if (situacaoAtual.getDescricao().equals(SituacaoEnum.CONFIRMADO.descricao()))
				consumoAtual.incrementaSomatorioExamesConfirmados(valor, guia.getQuantidadeProcedimentosValidosParaConsumo(),tipo,
						guia.getDataMarcacao(),false,decrementaExamesAgendados);
			else
				consumoAtual.incrementaSomatorioExamesAgendados(valor, guia.getProcedimentos().size(),tipo);
		}

		if (guia.isUrgencia()) {
			if(guia.isConsultaOdontoUrgencia()) {
				consumoAtual.incrementaSomatorioConsultasOdontoUrgenciaConfirmadas(guia.getValorTotal(), guia.getDataMarcacao(), tipo);
			}else{
				BigDecimal valorPreFechamento = ((GuiaSimples<Procedimento>)guia).getValorParcial();
				int numeroProcedimentosPreFechamento =  ((GuiaCompleta)guia).getNumeroAnteriorProcedimentosGuia();

				if (situacaoAtual.getDescricao().equals(SituacaoEnum.FECHADO.descricao())){
					consumoAtual.incrementaSomatorioUrgenciasFechadas(valorPreFechamento,numeroProcedimentosPreFechamento,valor, ((GuiaSimples<Procedimento>)guia).getQuantidadeProcedimentosValidosParaConsumo(),tipo);
					if(guia.isConsultaUrgencia() || guia.isAtendimentoUrgencia()){
						consumoAtual.incrementaSomatorioConsultasAtendimentosUrgenciasFechadas(valorPreFechamento,numeroProcedimentosPreFechamento,valor, ((GuiaSimples<Procedimento>)guia).getQuantidadeProcedimentosValidosParaConsumo(),tipo);
					}
				}
				else{
					consumoAtual.incrementaSomatorioUrgenciasAbertas(valor, guia.getQuantidadeProcedimentosValidosParaConsumo(),tipo);
					if(guia.isConsultaUrgencia() || guia.isAtendimentoUrgencia()){
						consumoAtual.incrementaSomatorioConsultasAtendimentosUrgenciasAbertas(valor, numeroProcedimentosPreFechamento, tipo);
					}
				}

			}
		}

		if (guia.isInternacao()) {
			BigDecimal valorPreFechamento = ((GuiaSimples<Procedimento>)guia).getValorParcial();
			int numeroProcedimentosPreFechamento =  ((GuiaCompleta)guia).getNumeroAnteriorProcedimentosGuia();

			if (situacaoAtual.getDescricao().equals(SituacaoEnum.FECHADO.descricao())){
				valor = TipoIncremento.POSITIVO.equals(tipo) ? ((GuiaSimples<Procedimento>)guia).getValorParcial() : 
						((GuiaSimples<Procedimento>)guia).getValorParcial().multiply(BigDecimal.valueOf(Float.valueOf(-1)));

				consumoAtual.incrementaSomatorioInternacoesFechadas(valorPreFechamento,numeroProcedimentosPreFechamento,valor, ((GuiaCompleta)guia).getNumeroAnteriorProcedimentosGuia(),tipo);
			}
			else
				consumoAtual.incrementaSomatorioInternacoesAbertas(valor, guia.getQuantidadeProcedimentosValidosParaConsumo(),tipo);
		}
	}

	public ConsumoInterface getConsumo(Date competencia) throws Exception {
		if (competencia == null)
			return null;

		if (this.getConsumoIndividual() == null) {
			this.setConsumoIndividual(new ComponenteColecaoConsumos());
			ImplDAO.save(this);
		}
		ConsumoInterface consumo = this.getConsumoIndividual().getConsumo(competencia, Periodo.MENSAL);

		if (consumo == null) {
			consumo = this.getConsumoIndividual().criarConsumo(competencia,
					Periodo.MENSAL,
					this.getTetoFinanceiroParaConsultas().floatValue(),
					this.getTetoFinanceiroParaExames().floatValue(),
					this.getTetoFinanceiroParaConsultasOdonto().floatValue(),
					this.getTetoFinanceiroParaTratamentosOdonto().floatValue());
			consumo.setDescricao(this.pessoaJuridica.getFantasia());
		}
		return consumo;
	}
	
	public boolean temTetoFinanceiro(GuiaSimples guia) throws Exception {
		if (!this.verificarTetos) {
			return true;
		}

		BigDecimal valorFinal = guia.getValorTotal();
		Date dataBase = new Date();
		if (guia.getDataAtendimento() != null) {
			dataBase = guia.getDataAtendimento();
		}

		ConsumoInterface consumoAtual = this.getConsumo(dataBase);
		if (consumoAtual == null) {
			throw new RuntimeException("Ocorreu um erro na criação do "
					+ "consumo para a competência "
					+ Utils.format(dataBase, "MM/yyyy"));
		}
		if (guia.isConsulta()) {
			valorFinal = valorFinal.add(consumoAtual.getSomatorioConsultas());
			return (valorFinal.compareTo(consumoAtual.getLimiteConsultas())) <= 0;
		}

		if (guia.isExame()) {
			valorFinal = valorFinal.add(consumoAtual.getSomatorioExames());
			return (valorFinal.compareTo(consumoAtual.getLimiteExames())) <= 0;
		}

		if (guia.isInternacao())
			return true;

		return false;
	}

	public Set<FluxoFinanceiroInterface> getFluxosFinanceiros() {
		Set<FluxoFinanceiroInterface> fluxos = new HashSet<FluxoFinanceiroInterface>();
		for (AbstractFaturamento faturamento : this.getFaturamentos())
			fluxos.add(faturamento);
		return fluxos;
	}

	public Set<DependenteFinanceiroInterface> getDependentesFinanceiro() {
		return new HashSet<DependenteFinanceiroInterface>();
	}

	public InformacaoFinanceiraInterface getInformacaoFinanceira() {
		return this.informacaoFinanceira;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Prestador)) {
			return false;
		}
		Prestador prestador = (Prestador) object;
		return new EqualsBuilder().append(this.idPrestador,prestador.getIdPrestador())
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(1308791639, 309108973)
		.append(this.idPrestador).toHashCode();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE).append(
				"idPrestador", this.idPrestador).append("nome: ",
				this.pessoaJuridica.getFantasia()).toString();

	}

	public String getCodigoLegado() {
		return codigoLegado;
	}

	public void setCodigoLegado(String codigoLegado) {
		this.codigoLegado = codigoLegado;
	}

	public void ativar(String motivo) throws Exception {
		if (Utils.isStringVazia(motivo))
			throw new Exception("O motivo deve ser preenchido!");
		this.mudarSituacao(null, Constantes.SITUACAO_ATIVO, motivo, new Date());
	}

	public void desativar(String motivo) throws Exception {
		if (Utils.isStringVazia(motivo))
			throw new Exception("O motivo deve ser preenchido!");
		this.mudarSituacao(null, Constantes.SITUACAO_INATIVO, motivo,
				new Date());
	}

	public BigDecimal getTetoFinanceiroParaConsultasOdonto() {
		return tetoFinanceiroParaConsultasOdonto;
	}

	public void setTetoFinanceiroParaConsultasOdonto(
			BigDecimal tetoFinanceiroParaConsultasOdonto) {
		this.tetoFinanceiroParaConsultasOdonto = tetoFinanceiroParaConsultasOdonto;
	}

	public BigDecimal getTetoFinanceiroParaTratamentosOdonto() {
		return tetoFinanceiroParaTratamentosOdonto;
	}

	public void setTetoFinanceiroParaTratamentosOdonto(
			BigDecimal tetoFinanceiroParaTratamentosOdonto) {
		this.tetoFinanceiroParaTratamentosOdonto = tetoFinanceiroParaTratamentosOdonto;
	}

	public Integer getTipoPrestador() {
		return tipoPrestador;
	}

	public void setTipoPrestador(Integer tipoPrestador) {
		this.tipoPrestador = tipoPrestador;
	}

	public String getIdentificacao() {
		return this.getPessoaJuridica().getCnpj();
	}

	public String getNome() {
		return this.getPessoaJuridica().getFantasia();
	}

	public String getTipo() {
		return "Prestador";
	}
	
	public String getDescricaoCategoria() {
		if(this.getTipoPrestador() == Constantes.TIPO_PRESTADOR_TODOS)
			return "Todos";
		
		if(this.getTipoPrestador() == Constantes.TIPO_PRESTADOR_CLINICAS_DE_EXAMES)
			return "Clínica de Exames";

		if(this.getTipoPrestador() == Constantes.TIPO_PRESTADOR_CLINICAS_AMBULATORIAIS)
			return "Clínica Ambulatorial";

		if(this.getTipoPrestador() == Constantes.TIPO_PRESTADOR_CLINICAS_DE_ODONTOLOGIA)
			return "Clínica de Odontologia";

		if(this.getTipoPrestador() == Constantes.TIPO_PRESTADOR_DENTISTAS)
			return "Dentistas";
		
		if(this.getTipoPrestador() == Constantes.TIPO_PRESTADOR_HOSPITAL)
			return "Hospital";

		if(this.getTipoPrestador() == Constantes.TIPO_PRESTADOR_LABORATORIO)
			return "Laboratório";

		if(this.getTipoPrestador() == Constantes.TIPO_PRESTADOR_MEDICOS)
			return "Médicos Credenciados";

		if(this.getTipoPrestador() == Constantes.TIPO_PRESTADOR_ANESTESISTA)
			return "Anestesista";

		if(this.getTipoPrestador() == Constantes.TIPO_PRESTADOR_OUTROS)
			return "Outros Profissionais";
		
		return "Outras Categorias";
	}

	public Set<TabelaCBHPM> getProcedimentos() {
		return procedimentos;
	}
	
	public List<TabelaCBHPM> getProcedimentosAtivos() {
		List<TabelaCBHPM> procedimentosAtivos = new ArrayList<TabelaCBHPM>(this.getProcedimentos());
		Collections.sort(procedimentosAtivos, new Comparator<TabelaCBHPM>() {
			@Override
			public int compare(TabelaCBHPM a1, TabelaCBHPM a2) {
				String situacaoP1 = a1.getSituacao().getDescricao();
				String situacaoP2 = a2.getSituacao().getDescricao();
				if (situacaoP1.compareTo(situacaoP2) != 0){
					return situacaoP1.compareTo(situacaoP2);
				}
				return a1.getDescricao().compareTo(a2.getDescricao());
			}
		});
		
		return procedimentosAtivos;
	}

	public void setProcedimentos(Set<TabelaCBHPM> procedimentosPrestador) {
		this.procedimentos = procedimentosPrestador;
	}	
	
	/**
	 * Retorna todas as guias do prestador que entrarão no faturamento atual
	 * @param session
	 * @param competenciaAjustada
	 * @return
	 */
	public List<GuiaSimples> getGuiasParaFaturamento(Session session, Date competenciaAjustada){
		Criteria criteria = session.createCriteria(GuiaSimples.class);
		criteria.setLockMode(LockMode.WRITE);
		return criteria
			.add(Expression.eq("prestador", this))	
			.add(Expression.disjunction()
			.add(Expression.eq("situacao.descricao",SituacaoEnum.CONFIRMADO.descricao()))
			.add(Expression.eq("situacao.descricao",SituacaoEnum.AUDITADO.descricao()))
			.add(Expression.eq("situacao.descricao",SituacaoEnum.FATURADA.descricao())))
			.add(Expression.le("dataAtendimento", competenciaAjustada))
			.add(Expression.isNull("faturamento")).list();
	}

	public boolean isCapitacao() {
		return capitacao;
	}

	public void setCapitacao(boolean capitacao) {
		this.capitacao = capitacao;
	}

	public Set<LoteDeGuias> getLotesDeGuias() {
		return lotesDeGuias;
	}

	public void setLotesDeGuias(Set<LoteDeGuias> lotesDeGuias) {
		this.lotesDeGuias = lotesDeGuias;
	}
	
	public boolean isPossuiAcordo(TabelaCBHPM tabelaCBHPM){
		for (AcordoCBHPM acordo : getAcordosCBHPMAtivos()) {
			if(acordo.getTabelaCBHPM().equals(tabelaCBHPM))
				return true;
		}
		return false;
	}
	public boolean isVerificaProcedimentosAssociados() {
		return verificaProcedimentosAssociados;
	}

	public void setVerificaProcedimentosAssociados(boolean verificaProcedimentosAssociados) {
		this.verificaProcedimentosAssociados = verificaProcedimentosAssociados;
	}
	
	public boolean isExigeEntregaLote() {
		return exigeEntregaLote;
	}
	
	public void setExigeEntregaLote(boolean exigeEntregaLote) {
		this.exigeEntregaLote = exigeEntregaLote;
	}

	public Date getDataInicioEntregaLote() {
		return dataInicioEntregaLote;
	}

	public void setDataInicioEntregaLote(Date dataInicioEntregaLote) {
		this.dataInicioEntregaLote = dataInicioEntregaLote;
	}
	
	private <A extends AbstractAcordo> List<A> getAcordosAtivos(Set<A> acordos) {
		List<A> acordosAtivos = new ArrayList<A>();
		for (A acordo : acordos) {
			if (acordo.isSituacaoAtual(SituacaoEnum.ATIVO.descricao())) {
				acordosAtivos.add(acordo);
			}
		}
		return acordosAtivos;
	}
	
	public List<AcordoCBHPM> getAcordosCBHPMAtivos() {
		List<AcordoCBHPM> acordos = this.getAcordosAtivos(this.acordosCBHPM);
		Collections.sort(acordos, new Comparator<AcordoCBHPM>() {
			@Override
			public int compare(AcordoCBHPM arg0, AcordoCBHPM arg1) {
				if (arg0.getTabelaCBHPM().getCodigo() != null && arg1.getTabelaCBHPM().getCodigo() != null)
					return arg0.getTabelaCBHPM().getCodigo().compareTo(arg1.getTabelaCBHPM().getCodigo());
				else
					return arg0.getTabelaCBHPM().getSituacao().getDescricao().compareTo(arg1.getTabelaCBHPM().getSituacao().getDescricao());
			}
		});
		
		return acordos;
	}
	
	public BigDecimal getValorTotalProcedimentos(Collection<ProcedimentoInterface> procedimentos) {
		BigDecimal valorTotal = BigDecimal.ZERO;
		for (ProcedimentoInterface procedimento : procedimentos) {
			valorTotal = MoneyCalculation.getSoma(valorTotal, procedimento.getValorTotalComAcordo(this));
		}
		return valorTotal;
	}
	
	public List<AcordoDiaria> getAcordosDiariaAtivos() {
		List<AcordoDiaria> acordos = this.getAcordosAtivos(this.acordosDiaria);
		Collections.sort(acordos, new Comparator<AcordoDiaria>() {
			@Override
			public int compare(AcordoDiaria arg0, AcordoDiaria arg1) {
				if (arg0.getDiaria().getCodigo() != null && arg1.getDiaria().getCodigo() != null)
					return arg0.getDiaria().getCodigo().compareTo(arg1.getDiaria().getCodigo());
				else
					return arg0.getDiaria().getSituacao().getDescricao().compareTo(arg1.getDiaria().getSituacao().getDescricao());
			}
		});
		
		return acordos;
	}
	
	public List<AcordoGasoterapia> getAcordosGasoterapiaAtivos() {
		List<AcordoGasoterapia> acordos = this.getAcordosAtivos(this.acordosGasoterapia);
		Collections.sort(acordos, new Comparator<AcordoGasoterapia>() {
			@Override
			public int compare(AcordoGasoterapia arg0, AcordoGasoterapia arg1) {
				if (arg0.getGasoterapia().getCodigo() != null && arg1.getGasoterapia().getCodigo() != null)
					return arg0.getGasoterapia().getCodigo().compareTo(arg1.getGasoterapia().getCodigo());
				else
					return arg0.getGasoterapia().getSituacao().getDescricao().compareTo(arg1.getGasoterapia().getSituacao().getDescricao());
			}
		});
		
		return acordos;
	}
	
	public List<AcordoPacote> getAcordosPacoteAtivos() {
		List<AcordoPacote> acordos = this.getAcordosAtivos(this.acordosPacote);
		Collections.sort(acordos, new Comparator<AcordoPacote>() {
			@Override
			public int compare(AcordoPacote arg0, AcordoPacote arg1) {
				if (arg0.getPacote().getCodigo() != null && arg1.getPacote().getCodigo() != null)
					return arg0.getPacote().getCodigo().compareTo(arg1.getPacote().getCodigo());
				else
					return arg0.getPacote().getSituacao().getDescricao().compareTo(arg1.getPacote().getSituacao().getDescricao());
			}
		});
		
		return acordos;
	}
	
	public List<AcordoPacoteHonorario> getAcordosPacoteHonorarioAtivos() {
		List<AcordoPacoteHonorario> acordos = this.getAcordosAtivos(this.acordosPacoteHonorarios);
		Collections.sort(acordos, new Comparator<AcordoPacoteHonorario>() {
			@Override
			public int compare(AcordoPacoteHonorario arg0, AcordoPacoteHonorario arg1) {
				if (arg0.getPacote().getCodigo() != null && arg1.getPacote().getCodigo() != null)
					return arg0.getPacote().getCodigo().compareTo(arg1.getPacote().getCodigo());
				else
					return arg0.getPacote().getSituacao().getDescricao().compareTo(arg1.getPacote().getSituacao().getDescricao());
			}
		});
		
		return acordos;
	}
	
	public AcordoPacote getAcordoPacote(TabelaCBHPM procedimentoDaTabela){
		List<AcordoPacote> acordosPacoteAtivos = this.getAcordosPacoteAtivos();
		
		for (AcordoPacote acordoPacote: acordosPacoteAtivos){
			boolean temAcordoPacoteQueContenhaOProcedimento = acordoPacote.getPacote().getProcedimentosCBHPM().contains(procedimentoDaTabela);
			if (temAcordoPacoteQueContenhaOProcedimento){
				return acordoPacote;
			}
		}
		
		return null;
	}
	
	public List<AcordoTaxa> getAcordosTaxaAtivos() {
		List<AcordoTaxa> acordos = this.getAcordosAtivos(this.acordosTaxa);
		Collections.sort(acordos, new Comparator<AcordoTaxa>() {
			@Override
			public int compare(AcordoTaxa arg0, AcordoTaxa arg1) {
				if (arg0.getTaxa().getCodigo() != null && arg1.getTaxa().getCodigo() != null)
					return arg0.getTaxa().getCodigo().compareTo(arg1.getTaxa().getCodigo());
				else
					return arg0.getTaxa().getSituacao().getDescricao().compareTo(arg1.getTaxa().getSituacao().getDescricao());
			}
		});
		
		return acordos;
	}
	
	public boolean isGeraHonorario() {
		return geraHonorario;
	}

	public void setGeraHonorario(boolean geraHonorario) {
		this.geraHonorario = geraHonorario;
	}

	/**
	 * Indica se esse prestador está habilitado para gerar honorário individual para graus de
	 * participação distintos bem como para profissionais que não pertencem ao seu corpo clínico.
	 * @return true se o tipo prestador for {@link #TIPO_PRESTADOR_MEDICOS}
	 */
	public boolean isGeraHonorarioPessoaFisica() {
		return this.tipoPrestador.intValue() == TIPO_PRESTADOR_MEDICOS;
	}

	/**
	 * Percorrerá as guias de exames em situação 'Aberto(a)', 'Auditado(a)', 'Confirmado(a)', 'Devolvido(a)', 'Enviado(a) para o SAÚDE RECIFE',
	 * 'Faturado(a)', 'Fechado(a)', 'Pago(a)', 'Recebido(a)' e  retornará a soma dos valores dos procedimentos que não estejam nas situações
	 * dentro das 'Glosado(a)','Cancelado(a)','Não Autorizado(a)','Negado(a)', 'Realizado(a)' contidos dentro dessas guias. Sempre considera-se o 
	 * corrente mês como intervalo de tempo.
	 * @return
	 */
	public BigDecimal getConsumoAtualDeExames(){
		String query = "select sum(case p.bilateral when true then (valorAtualDoProcedimento * 1.7)  else (valorAtualDoProcedimento * " +
				" quantidade) end) from procedimentos_procedimento p where p.descricao not in  ('Glosado(a)','Cancelado(a)','Não Autorizado(a)'," +
				"'Negado(a)', 'Realizado(a)') and p.idguia in (select guia.idguia from atendimento_guiasimples as guia  where guia.descricao in" +
				" ('Aberto(a)', 'Auditado(a)', 'Confirmado(a)', 'Devolvido(a)', 'Enviado(a) para o SAÚDE RECIFE', 'Faturado(a)', 'Fechado(a)'," +
				" 'Pago(a)', 'Recebido(a)')  and guia.idprestador = :idprestador and (guia.dataatendimento >= :dataInicial and " +
				"guia.dataatendimento <= :dataFinal))";
		
		if (consumoAtualDeExames == null){
			Date primeiroDiaDoMes = Mes.getDataInicial(new Date());
			Date ultimoDiaDoMes   = Mes.getDataFinal(new Date());
			
			consumoAtualDeExames  = (BigDecimal) HibernateUtil.currentSession().createSQLQuery(query)	
													.setLong("idprestador", this.idPrestador)
													.setDate("dataInicial", primeiroDiaDoMes)
													.setDate("dataFinal", ultimoDiaDoMes)
													.uniqueResult(); 
			
			if (consumoAtualDeExames == null){	
				return BigDecimal.ZERO;
			}
		}
		return consumoAtualDeExames;
	}
	
	
	/**
	 * @return Soma do valor total de todas as guiuas de consulta em situação 'Confirmado(a)', 'Faturado(a)', 'Pago(a)'. Sempre considera-se o 
	 * corrente mês como intervalo de tempo.
	 */
	public BigDecimal getConsumoAtualDeConsultas(Date dataBase){
		String query = "select sum(valortotal) from atendimento_guiasimples as guia where guia.tipodeguia  = 'GCS' and guia.descricao in " +
						"('Confirmado(a)', 'Faturado(a)', 'Pago(a)') and guia.idprestador = :idprestador and guia.dataatendimento between "	+ 
						":dataInicial and :dataFinal";
		
		if (consumoAtualDeConsultas == null){
			Date primeiroDiaDoMes = Mes.getDataInicial(dataBase);
			Date ultimoDiaDoMes   = Mes.getDataFinal(dataBase);

			consumoAtualDeConsultas = (BigDecimal) HibernateUtil.currentSession().createSQLQuery(query)
															.setLong("idprestador", this.idPrestador)
															.setDate("dataInicial", primeiroDiaDoMes)
															.setDate("dataFinal", ultimoDiaDoMes)
															.uniqueResult();
			if (consumoAtualDeConsultas == null){
				return BigDecimal.ZERO;
			}
		}
		
		return consumoAtualDeConsultas;
	}
	
	public boolean isTetoExamesEstourado(BigDecimal valorDaGuia){

		BigDecimal teto = this.getTetoFinanceiroParaExames();
		BigDecimal consumoAtualExames = this.getConsumoAtualDeExames();

		if (MoneyCalculation.compare(teto, (consumoAtualExames.add(valorDaGuia))) < 0){
			return true;
		}
		
		return false;
	}
	
	public boolean isTetoConsultasEstourado(BigDecimal valorDaGuia, Date dataBase){

		BigDecimal teto = this.getTetoFinanceiroParaConsultas();
		BigDecimal consumoAtualConsultas = this.getConsumoAtualDeConsultas(dataBase);
		
		if (MoneyCalculation.compare(teto, (consumoAtualConsultas.add(valorDaGuia))) < 0){
			return true;
		}
		
		return false;
	}
	
	public BigDecimal getValorTotalProcedimentos() {
		return valorTotalProcedimentos;
	}

	public void setValorTotalProcedimentos(BigDecimal valorTotalProcedimentos) {
		this.valorTotalProcedimentos = valorTotalProcedimentos;
	}
	
	public void setAtualizarConsumo(boolean atualizarConsumo) {
		this.atualizarConsumo = atualizarConsumo;
	}

	public boolean isAtualizarConsumo() {
		return atualizarConsumo;
	}

	public boolean isAtendePacientesCronicos() {
		return atendePacientesCronicos;
	}

	public void setAtendePacientesCronicos(boolean atendePacientesCronicos) {
		this.atendePacientesCronicos = atendePacientesCronicos;
	}
	
	public boolean isClinica(){
		if(getTipoPrestador().equals(TIPO_PRESTADOR_CLINICAS_AMBULATORIAIS)
			|| getTipoPrestador().equals(TIPO_PRESTADOR_CLINICAS_DE_EXAMES)
			|| getTipoPrestador().equals(TIPO_PRESTADOR_CLINICAS_DE_ODONTOLOGIA)){
			
			return true;
		}
		return false;
	}

	public int getPeriodoParaVoltaNaUrgencia() {
		return periodoParaVoltaNaUrgencia;
	}

	public void setPeriodoParaVoltaNaUrgencia(int periodoParaVoltaNaUrgencia) {
		this.periodoParaVoltaNaUrgencia = periodoParaVoltaNaUrgencia;
	}
	
	public Set<AcordoPacoteHonorario> getAcordosPacoteHonorarios() {
		return acordosPacoteHonorarios;
	}

	public void setAcordosPacoteHonorarios(Set<AcordoPacoteHonorario> acordosPacoteHonorarios) {
		this.acordosPacoteHonorarios = acordosPacoteHonorarios;
	}
	
	public Set<PrestadorEspecialidadeTabelaCBHPM> getPrestadoresEspecialidadesTabelaCBHPM() {
		return prestadoresEspecialidadesTabelaCBHPM;
	}

	public Set<PrestadorEspecialidadeTabelaCBHPM> getPrestadoresEspecialidadesTabelaCBHPMAtivos() {
		Set<PrestadorEspecialidadeTabelaCBHPM> prestadoresEspecialidadesTabelaCBHPMAtivos = new HashSet<PrestadorEspecialidadeTabelaCBHPM>();
		for(PrestadorEspecialidadeTabelaCBHPM prestadorEspecialidadeTabelaCBHPM : prestadoresEspecialidadesTabelaCBHPM){
			if(prestadorEspecialidadeTabelaCBHPM.isSituacaoAtual(SituacaoEnum.ATIVO.descricao())){
				prestadoresEspecialidadesTabelaCBHPMAtivos.add(prestadorEspecialidadeTabelaCBHPM);
			}
		}
		return prestadoresEspecialidadesTabelaCBHPMAtivos;
	}

	public void setPrestadoresEspecialidadesTabelaCBHPM(Set<PrestadorEspecialidadeTabelaCBHPM> prestadoresEspecialidadesTabelaCBHPM) {
		this.prestadoresEspecialidadesTabelaCBHPM = prestadoresEspecialidadesTabelaCBHPM;
	}

	public boolean isPrestadorSemSalaObservacao() {
		return prestadorSemSalaObservacao;
	}

	public void setPrestadorSemSalaObservacao(boolean prestadorSemSalaObservacao) {
		this.prestadorSemSalaObservacao = prestadorSemSalaObservacao;
	}
	
}