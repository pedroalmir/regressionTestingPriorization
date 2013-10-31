package br.com.infowaypi.ecarebc.financeiro.faturamento;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;

import br.com.infowaypi.ecare.services.recurso.GuiaRecursoGlosa;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.retencao.AbstractImposto;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.utils.GuiaUtils;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Resumo de faturamentos.
 * @author Mário Sérgio Coelho Marroquim
 * @changes Luciano Rocha
 */
public class ResumoFaturamentos {

	public static final int RESUMO_CATEGORIA = 1;
	public static final int RESUMO_PESSOA = 2;
	
	private Date competencia;
	private int categoria;
	private BigDecimal valorTotalBruto;
	private BigDecimal valorTotalLiquido;
	private BigDecimal valorTotalIr;
	private BigDecimal valorTotalInss;
	private BigDecimal valorTotalIss;
	protected BigDecimal valorRecursosDeferidos;
	private Float numeroProcedimentosGlosados;
	private Float valorProcedimentosGlosados;
	private List<GuiaFaturavel> guias;
	private boolean computaSubnivel = false;
	private int tipoResumo = RESUMO_CATEGORIA;
	private String tipoFaturamento;
	/**
	 * Resumo dos por categoria doa faturamentos encontrados.
	 */
	private List<ResumoFaturamentos> resumos;
	/**
	 * Resumo dos faturamentos segregados por prestador.
	 */
	private List<ResumoFaturamentos> resumosPorPrestador = new LinkedList<ResumoFaturamentos>();
	protected Set<AbstractFaturamento> faturamentos;
	private Faturamento faturamento;
	private Set<Prestador> prestadores;
	private List<DetalheHonorarioMedico> 	detalhesHonorariosMedicos;
	private List<DetalheRecursoDeferido> 	detalhesRecursosDeferidos;
	private Set<DetalheValoresFaturamento> 	detalhesValoresFaturamentos;
	private Set<Procedimento> 				procedimentos = new HashSet<Procedimento>();
	
	private Set<GuiaFaturavel> guiasExamesAmbulatoriais;
	private int numeroGuiasExamesAmbulatoriais;
	private long numeroExamesAmbulatoriais;
	private BigDecimal valorExamesAmbulatoriais;
	
	private Set<GuiaFaturavel> guiasExamesExternoInternacao;
	private int numeroGuiasExamesExternoInternacao;
	private long numeroExamesExternoInternacao;

	private BigDecimal valorExamesExternoInternacao;
	
	private Set<GuiaFaturavel> guiasExamesExternoAtendimentosUrgencia;
	private int numeroGuiasExamesExternoAtendimentosUrgencia;
	private long numeroExamesExternoAtendimentosUrgencia;
	private BigDecimal valorExamesExternoAtendimentosUrgencia;
	
	private Boolean exibirGuias;
	
	private String status;
	
	private boolean alteraSituacaoDasGuias = true;
	
	private Boolean detalhar = Boolean.TRUE;
	
	/**
	 * Campos utilizados no Fluxo de registro de pagamento de @see RegistrarPagamentoService.java
	 */
	private Date dataGeracao;
	private Prestador prestador;
	private Date dataPagamento;
	private Set<OcorrenciaFaturamento> ocorrencias 	= new HashSet<OcorrenciaFaturamento>();
	
	/**
	 * Campo utilizado no Fluxo @MudarSituacaoFaturamentoService.java
	 */
	private String motivo;
	
	public Boolean getDetalhar() {
		return detalhar;
	}
	
	public void setDetalhar(Boolean detalharMeses) {
		this.detalhar = detalharMeses;
	}
	
	public ResumoFaturamentos(){
		this.faturamentos 	= new HashSet<AbstractFaturamento>();
		this.faturamento 	= new Faturamento();
	}
	
	public ResumoFaturamentos(Map<Prestador, Set<AbstractFaturamento>> faturamentosPorPrestador, List<? extends AbstractFaturamento> faturamentos, int tipoResumo, Date competencia, Boolean exibirGuias) {
		this(0, true, tipoResumo, competencia, exibirGuias);
		this.prestadores = faturamentosPorPrestador.keySet();
		
		computarResumo(faturamentos);
		computarResumosPorPrestador(faturamentosPorPrestador);
	}
	
	private void computarResumosPorPrestador(Map<Prestador, Set<AbstractFaturamento>> faturamentosPorPrestador) {
		for (Prestador prestador : faturamentosPorPrestador.keySet()) {
			ResumoFaturamentos resumoPrestador 		= new ResumoFaturamentos(prestador.getCategoria(), false, RESUMO_CATEGORIA, this.competencia, false);
			List<AbstractFaturamento> faturamentos 	= new ArrayList(faturamentosPorPrestador.get(prestador));
			resumoPrestador.computarResumo(faturamentos);
			
			this.getResumosPorPrestador().add(resumoPrestador);
		}
	}

	public ResumoFaturamentos(List<? extends AbstractFaturamento> faturamentos, List<Prestador> prestadores, int tipoResumo, Date competencia, Boolean exibirGuias) {
		this(0, true, tipoResumo, competencia, exibirGuias);
		
		if (prestadores != null){
			this.prestadores = new HashSet<Prestador>(prestadores);
		}else{ 
			this.prestadores = new HashSet<Prestador>();
		}
		
		computarResumo(faturamentos);
	}
	
	public ResumoFaturamentos(List<? extends AbstractFaturamento> faturamentos, int tipoResumo, Prestador prestador, Date competencia, boolean exibirGuias) {		
		this(0, true, tipoResumo, competencia, exibirGuias);
		
		this.prestador = prestador;
		
		for (AbstractFaturamento faturamento : faturamentos) {
			this.dataGeracao = faturamento.getDataGeracao();
			for(OcorrenciaFaturamento ocorrencia: faturamento.getOcorrencias()){
				this.ocorrencias.add(ocorrencia);
			}
		}
		
		computarResumo(faturamentos);
	}

	public ResumoFaturamentos(int indiceCategoria, boolean computaSubnivel,  int tipoResumo, Date competencia, Boolean exibirGuias) {
		super();
		this.competencia 	= competencia;
		this.tipoResumo 	= tipoResumo;
		valorTotalBruto 	= BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		valorTotalLiquido 	= BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		valorTotalIr 		= BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		valorTotalInss 		= BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		valorTotalIss 		= BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		valorRecursosDeferidos          = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		valorExamesAmbulatoriais 		= BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		valorExamesExternoInternacao 	= BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		valorExamesExternoAtendimentosUrgencia = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		this.guias 				= new ArrayList<GuiaFaturavel>();
		this.exibirGuias 		= exibirGuias;
		this.faturamento 		= new Faturamento();
		this.resumos 			= new ArrayList<ResumoFaturamentos>();
		this.faturamentos 		= new HashSet<AbstractFaturamento>();
		this.computaSubnivel	= computaSubnivel;
		this.categoria 			= indiceCategoria;
		this.detalhesRecursosDeferidos              = new ArrayList<DetalheRecursoDeferido>();
		this.detalhesHonorariosMedicos 				= new ArrayList<DetalheHonorarioMedico>();
		this.detalhesValoresFaturamentos 			= new TreeSet<DetalheValoresFaturamento>();
		this.guiasExamesAmbulatoriais 				= new HashSet<GuiaFaturavel>();
		this.guiasExamesExternoAtendimentosUrgencia = new HashSet<GuiaFaturavel>();
		this.guiasExamesExternoInternacao 			= new HashSet<GuiaFaturavel>();
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status){
		this.status = status;
	}

	protected void computarResumo(List<? extends AbstractFaturamento> faturamentos) {
		computaFaturamentos(faturamentos);

		Set<Integer> categorias = computaCategoriasFaturamento(faturamentos);
		computaSubNivel(faturamentos, categorias);
	}

	private void computaFaturamentos(List<? extends AbstractFaturamento> faturamentos) {
		for(AbstractFaturamento faturamento : faturamentos){
		
			if(this.exibirGuias){
				Set<GuiaFaturavel> guias = new HashSet<GuiaFaturavel>();
				for (GuiaFaturavel guia : faturamento.getGuiasFaturaveis()) {
					if (!guia.isRecursoGlosa()) {
						guias.add(guia);
					}
				}
				this.guias.addAll(guias);
			}
			
			if (faturamento.getGuiasRecursoGlosa() != null) {
				computarRecursosDeferidos(faturamento);
			}
			computarHonorario(faturamento);
			computarDetalheValoresFaturamentos(faturamento);
			computaFaturamento(faturamento);
			
			valorTotalBruto = valorTotalBruto.add(MoneyCalculation.rounded(faturamento.getValorBruto().add(faturamento.getValorOutros())));
			
			this.faturamentos.add(faturamento);
			
			faturamento.tocarObjetos();
			
			this.faturamento.setCompetencia(faturamento.getCompetencia());
			this.faturamento.setDataGeracao(faturamento.getDataGeracao());
			this.faturamento.setNumeroEmpenho(faturamento.getNumeroEmpenho());
			this.faturamento.setDataPagamento(faturamento.getDataPagamento());
			this.faturamento.setValorConsultas(this.faturamento.getValorConsultas().add(faturamento.getValorConsultas()));
			this.faturamento.setValorConsultasOdonto(this.faturamento.getValorConsultasOdonto().add(faturamento.getValorConsultasOdonto()));
			this.faturamento.setValorExames(this.faturamento.getValorExames().add(faturamento.getValorExames()));
			this.faturamento.setValorExamesOdonto(this.faturamento.getValorExamesOdonto().add(faturamento.getValorExamesOdonto()));
			this.faturamento.setValorAtendimentosUrgencia(this.faturamento.getValorAtendimentosUrgencia().add(faturamento.getValorAtendimentosUrgencia()));
			this.faturamento.setValorInternacoes(this.faturamento.getValorInternacoes().add(faturamento.getValorInternacoes()));
			this.faturamento.getAlteracoesFaturamento().addAll(faturamento.getAlteracoesFaturamento());
			this.faturamento.setValorBruto(this.faturamento.getValorBruto().add(faturamento.getValorBruto()));
			this.faturamento.setPrestador(faturamento.getPrestador());
			this.faturamento.setCategoria(faturamento.getCategoria());
			this.faturamento.setStatus(faturamento.getStatus());
			this.faturamento.setValorRecursosDeferidos(this.faturamento.getValorRecursosDeferidos().add(faturamento.getValorRecursosDeferidos()));
		}
		
		this.detalhesValoresFaturamentos.add(getDetalheGeral());
		
		String[] situacoes = {SituacaoEnum.CANCELADO.descricao(),SituacaoEnum.NAO_AUTORIZADO.descricao()};
		
		numeroExamesAmbulatoriais 				= getNumeroProcedimentos(guiasExamesAmbulatoriais, situacoes);
		numeroExamesExternoAtendimentosUrgencia = getNumeroProcedimentos(guiasExamesExternoAtendimentosUrgencia, situacoes);
		numeroExamesExternoInternacao 			= getNumeroProcedimentos(guiasExamesExternoInternacao, situacoes);
		
		guiasExamesAmbulatoriais 				= null;
		guiasExamesExternoAtendimentosUrgencia 	= null;
		guiasExamesExternoInternacao 			= null;
	}

	private Set<Integer> computaCategoriasFaturamento(List<? extends AbstractFaturamento> faturamentos) {
		Set<Integer> resultado = new HashSet<Integer>();

		for(AbstractFaturamento faturamento : faturamentos){
			if (computaSubnivel){
				if (tipoResumo == RESUMO_CATEGORIA){
					resultado.add(faturamento.getCategoria());
				} else {
					if (faturamento.getTipoPessoa() == Constantes.PESSOA_JURIDICA){
						resultado.add(1);
					} else {
						resultado.add(2);
					}
				}
			}
		}
		
		return resultado;
	}

	private void computaSubNivel(List<? extends AbstractFaturamento> faturamentos, Set<Integer> categorias) {
		if (computaSubnivel){
			for (Integer indiceCategoria : categorias){
				this.resumos.add(new ResumoFaturamentos(indiceCategoria, false, this.tipoResumo, this.competencia, this.exibirGuias));
			}
			
			boolean iterouCoopanest = false;
			
			for(AbstractFaturamento faturamento : faturamentos){
				
				if (iterouCoopanest && faturamento.getNome().equals("COOPANEST")) {
					continue;
				}
				
				if (faturamento.getNome().equals("COOPANEST")) {
					iterouCoopanest = true;
				}
				
				for (ResumoFaturamentos resumo : resumos){
					if (tipoResumo == RESUMO_CATEGORIA && resumo.getCategoria() == faturamento.getCategoria()){
						resumo.computaFaturamento(faturamento);
						resumo.faturamentos.add(faturamento);
						resumo.valorTotalBruto = resumo.valorTotalBruto.add(MoneyCalculation.rounded(faturamento.getValorBruto().add(faturamento.getValorOutros())));
					} else if (tipoResumo == RESUMO_PESSOA && resumo.getCategoria() == 1 && faturamento.getTipoPessoa() == AbstractImposto.PESSOA_JURIDICA){
						resumo.computaFaturamento(faturamento);
					} else if (tipoResumo == RESUMO_PESSOA && resumo.getCategoria() == 2 && faturamento.getTipoPessoa() == AbstractImposto.PESSOA_FISICA){
						resumo.computaFaturamento(faturamento);
					}
				}
			}
		}
	}

	private DetalheValoresFaturamento getDetalheGeral() {
		Date competencia = null;
		DetalheValoresFaturamento detalheGeral = new DetalheValoresFaturamento(competencia);
		for (DetalheValoresFaturamento detalhe : this.detalhesValoresFaturamentos) {
			detalheGeral.setQuantidadeConsultas(detalheGeral.getQuantidadeConsultas()+ detalhe.getQuantidadeConsultas());
			detalheGeral.setValorConsultas(detalheGeral.getValorConsultas().add(detalhe.getValorConsultas()));
			
			detalheGeral.setQuantidadeExames(detalheGeral.getQuantidadeExames()+ detalhe.getQuantidadeExames());
			detalheGeral.setValorExames(detalheGeral.getValorExames().add(detalhe.getValorExames()));
			
			detalheGeral.setQuantidadeConsultasOdonto(detalheGeral.getQuantidadeConsultasOdonto()+ detalhe.getQuantidadeConsultasOdonto());
			detalheGeral.setValorConsultasOdonto(detalheGeral.getValorConsultasOdonto().add(detalhe.getValorConsultasOdonto()));
			
			detalheGeral.setQuantidadeExamesOdonto(detalheGeral.getQuantidadeExamesOdonto()+ detalhe.getQuantidadeExamesOdonto());
			detalheGeral.setValorExamesOdonto(detalheGeral.getValorExamesOdonto().add(detalhe.getValorExamesOdonto()));
			
			detalheGeral.setQuantidadeAtendimentosUrgencia(detalheGeral.getQuantidadeAtendimentosUrgencia()+ detalhe.getQuantidadeAtendimentosUrgencia());
			detalheGeral.setValorAtendimentosUrgencia(detalheGeral.getValorAtendimentosUrgencia().add(detalhe.getValorAtendimentosUrgencia()));
			
			detalheGeral.setQuantidadeInternacoes(detalheGeral.getQuantidadeInternacoes()+ detalhe.getQuantidadeInternacoes());
			detalheGeral.setValorInternacoes(detalheGeral.getValorInternacoes().add(detalhe.getValorInternacoes()));
			
			detalheGeral.setQuantidadeGuiasHonorario(detalheGeral.getQuantidadeGuiasHonorario()+ detalhe.getQuantidadeGuiasHonorario());
			detalheGeral.setValorGuiasHonorario(detalheGeral.getValorGuiasHonorario().add(detalhe.getValorGuiasHonorario()));
			
			detalheGeral.setQuantidadeAcompanhamentoAnestesico(detalheGeral.getQuantidadeAcompanhamentoAnestesico() + detalhe.getQuantidadeAcompanhamentoAnestesico());
			detalheGeral.setValorAcompanhamentoAnestesico(detalheGeral.getValorAcompanhamentoAnestesico().add(detalhe.getValorAcompanhamentoAnestesico()));
			
			detalheGeral.setValorRecursosDeferidos(detalheGeral.getValorRecursosDeferidos().add(detalhe.getValorRecursosDeferidos()));
		}
		
		return detalheGeral;
	}

	public Long getNumeroProcedimentos(Set<GuiaFaturavel> guias, String[] situacoes){
		if(guias.isEmpty()){
			return 0L;
		}
			
		Long result = (Long) HibernateUtil.currentSession().createCriteria(Procedimento.class)
								.setProjection(Projections.rowCount())
								.add(Expression.in("guia", guias))
								.add(Expression.not(Expression.in("situacao.descricao", situacoes)))
								.uniqueResult();
		
		return result;
	}
	
	protected void computarHonorario(AbstractFaturamento faturamento) {
		for (HonorarioMedico honorario : faturamento.getHonorariosMedicos()) {
			for (ProcedimentoInterface procedimento : honorario.getProcedimentos()) {
				setDetalheHonorario(procedimento, honorario.getProfissional());
			}
		}
	}

	protected void computarRecursosDeferidos(AbstractFaturamento faturamento) {
		for (GuiaRecursoGlosa recurso : faturamento.getGuiasRecursoGlosa()) {
			if(recurso.getSituacao().getDescricao().equals(SituacaoEnum.DEFERIDO.descricao())
				|| recurso.getSituacao().getDescricao().equals(SituacaoEnum.FATURADA.descricao())
				|| recurso.getSituacao().getDescricao().equals(SituacaoEnum.PAGO.descricao())){
				setDetalheRecursosDeferidos(recurso, faturamento);
			}
		}
	}
	
	protected void setDetalheRecursosDeferidos(GuiaRecursoGlosa guia, AbstractFaturamento faturamento) {
		DetalheRecursoDeferido detalheRecursoDeferido = createDetalheRecursoDeferido(guia);
		this.getDetalhesRecursosDeferidos().add(detalheRecursoDeferido);
	}

	protected void computarDetalheValoresFaturamentos(AbstractFaturamento faturamento){
		DetalheValoresFaturamento detalhe;
		for (GuiaFaturavel guia : faturamento.getGuiasFaturaveis()) {
			if (!guia.isRecursoGlosa()) {
				detalhe = getDetalheValoresFaturamento(guia.getDataAtendimento());
				this.povoarDetalheValoresFaturamentos(detalhe, guia);
				this.getDetalhesValoresFaturamentos().add(detalhe);
			}
		}

		detalhe = getDetalheValoresFaturamento(faturamento.getCompetencia());

		//Usa-se um set para que uma mesma autorização passa mais de uma vez pela contagem, assim, ela não será contada duas vezes.
		Set<String> autorizacoesGuiasOdonto		 	= new HashSet<String>();
		Set<String> autorizacoesGuiasInternacoes 	= new HashSet<String>();
		Set<String> autorizacoesGuiasUrgencia 		= new HashSet<String>();

		for (DetalheHonorarioMedico detalheHonorario: this.getDetalhesHonorariosMedicos()){
			GuiaFaturavel guia = detalheHonorario.getGuia();
			if (guia.isInternacao()){
				autorizacoesGuiasInternacoes.add(guia.getAutorizacao());
				detalhe.setValorInternacoes(detalhe.getValorInternacoes().add(detalheHonorario.getValor()));
			}else if (guia.isUrgencia()){
				autorizacoesGuiasUrgencia.add(guia.getAutorizacao());
				detalhe.setValorAtendimentosUrgencia(detalhe.getValorAtendimentosUrgencia().add(detalheHonorario.getValor()));
			}else if(guia.isCirurgiaOdonto() || guia.isExameOdonto()){
				autorizacoesGuiasOdonto.add(guia.getAutorizacao());
				detalhe.setValorExamesOdonto(detalhe.getValorExamesOdonto().add(detalheHonorario.getValor()));
			}
		}
		
//		for (DetalheRecursoDeferido detalheRecurso : this.getDetalhesRecursosDeferidos()) {
//			detalhe.setValorRecursosDeferidos(detalhe.getValorRecursosDeferidos().add(detalheRecurso.getValor()));
//		}

		detalhe.setQuantidadeAtendimentosUrgencia(autorizacoesGuiasUrgencia.size());
		detalhe.setQuantidadeInternacoes(autorizacoesGuiasInternacoes.size());
		detalhe.setQuantidadeExamesOdonto(autorizacoesGuiasOdonto.size());

		this.getDetalhesValoresFaturamentos().add(detalhe);
	} 
	
	protected DetalheValoresFaturamento getDetalheValoresFaturamento(Date competencia){

		for (DetalheValoresFaturamento det : this.getDetalhesValoresFaturamentos()) {
			if(competencia == null)
				continue;
				
			if (Utils.compararCompetencia(det.getCompetencia(), competencia) == 0)
				return det;
		}

		return new DetalheValoresFaturamento(competencia);
	}
	
	protected void povoarDetalheValoresFaturamentos(DetalheValoresFaturamento detalhe, GuiaFaturavel guia) {
		Calendar competencia = new GregorianCalendar();
		competencia.setTime(guia.getDataAtendimento());
		competencia.set(Calendar.DAY_OF_MONTH, 01);
	
		guia.getFaturamento().getHonorariosMedicos();
		
		detalhe.getGuias().add(guia);

		if (guia.isConsultaOdonto() || guia.isConsultaOdontoUrgencia()){
			BigDecimal valor = detalhe.getValorConsultasOdonto().add(guia.getValorPagoPrestador());
			detalhe.setValorConsultasOdonto(valor);
			detalhe.setQuantidadeConsultasOdonto(detalhe.getQuantidadeConsultasOdonto() + 1);
		}else if (guia.isConsulta()){
			BigDecimal valor = detalhe.getValorConsultas().add(guia.getValorPagoPrestador());
			detalhe.setValorConsultas(valor);
			detalhe.setQuantidadeConsultas(detalhe.getQuantidadeConsultas() + 1);
		}else if (guia.isExameOdonto() || guia.isCirurgiaOdonto()){
			BigDecimal valor = detalhe.getValorExamesOdonto().add(guia.getValorPagoPrestador());
			detalhe.setValorExamesOdonto(valor);
			detalhe.setQuantidadeExamesOdonto(detalhe.getQuantidadeExamesOdonto() + 1);
		}else if (guia.isExame()){
			if(guia.isExameExternoAtendimentosUrgencia()){
				this.numeroGuiasExamesExternoAtendimentosUrgencia++;
				guiasExamesExternoAtendimentosUrgencia.add(guia);
				this.valorExamesExternoAtendimentosUrgencia = this.valorExamesExternoAtendimentosUrgencia.add(guia.getValorPagoPrestador());
			}else if(guia.isExameExternoInternacao()){
				this.numeroGuiasExamesExternoInternacao++;
				guiasExamesExternoInternacao.add(guia);
				this.valorExamesExternoInternacao = this.valorExamesExternoInternacao.add(guia.getValorPagoPrestador());
			}else{
				this.numeroGuiasExamesAmbulatoriais++;
				guiasExamesAmbulatoriais.add(guia);
				this.valorExamesAmbulatoriais = this.valorExamesAmbulatoriais.add(guia.getValorPagoPrestador());
			}
			
			BigDecimal valor = detalhe.getValorExames().add(guia.getValorPagoPrestador());
			detalhe.setValorExames(valor);
			detalhe.setQuantidadeExames(detalhe.getQuantidadeExames() + 1);
		} else if (guia.isAtendimentoUrgencia() || guia.isConsultaUrgencia()){
			BigDecimal valor = detalhe.getValorAtendimentosUrgencia().add(guia.getValorPagoPrestador());
			detalhe.setValorAtendimentosUrgencia(valor);
			detalhe.setQuantidadeAtendimentosUrgencia(detalhe.getQuantidadeAtendimentosUrgencia() + 1);
		} else if (guia.isInternacao()){
			BigDecimal valor = detalhe.getValorInternacoes().add(guia.getValorPagoPrestador());
			detalhe.setValorInternacoes(valor);
			detalhe.setQuantidadeInternacoes(detalhe.getQuantidadeInternacoes() + 1);
		} else if (guia.isHonorarioMedico()){
			BigDecimal valor = detalhe.getValorGuiasHonorario().add(guia.getValorPagoPrestador());
			detalhe.setValorGuiasHonorario(valor);
			detalhe.setQuantidadeGuiasHonorario(detalhe.getQuantidadeGuiasHonorario() + 1);
		} else if (guia.isAcompanhamentoAnestesico()){
			BigDecimal valor = detalhe.getValorAcompanhamentoAnestesico().add(guia.getValorPagoPrestador());
			detalhe.setValorAcompanhamentoAnestesico(valor);
			detalhe.setQuantidadeAcompanhamentoAnestesico(detalhe.getQuantidadeAcompanhamentoAnestesico() + 1);
		}
		detalhe.setValorRecursosDeferidos(detalhe.getValorRecursosDeferidos().add(valorRecursosDeferidos));
	}
	
	protected void setDetalheHonorario(ProcedimentoInterface procedimento, Profissional profissional) {
		boolean isSituacaoHonorarioGerado, isValorZero;
	
		if (procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO) {
			isSituacaoHonorarioGerado = procedimento.isSituacaoAtual(SituacaoEnum.PROCEDIMENTO_COM_HONORARIO_GERADO.descricao());
			isValorZero = procedimento.getValorProfissionalResponsavel() != null ? MoneyCalculation.compare(procedimento.getValorProfissionalResponsavel(), BigDecimal.ZERO)==0 : true;
			
			if ((procedimento.getProfissionalResponsavel() != null) && (procedimento.getProfissionalResponsavel().equals(profissional)) && !isSituacaoHonorarioGerado && !isValorZero) {
				DetalheHonorarioMedico detalheHonorarioMedico = createDetalhe(procedimento, DetalheHonorarioMedico.CIRURGIAO, profissional);
				this.getDetalhesHonorariosMedicos().add(detalheHonorarioMedico);
			}
			
			isValorZero = procedimento.getValorAuxiliar1() != null ? MoneyCalculation.compare(procedimento.getValorAuxiliar1(), BigDecimal.ZERO)==0 : true;
			
			if ((procedimento.getProfissionalAuxiliar1() != null) && (procedimento.getProfissionalAuxiliar1().equals(profissional)) && !isValorZero) {
				DetalheHonorarioMedico detalheHonorarioMedico = createDetalhe(procedimento, DetalheHonorarioMedico.AUXILIAR_1, profissional);
				this.getDetalhesHonorariosMedicos().add(detalheHonorarioMedico);
			}
			
			isValorZero = procedimento.getValorAuxiliar2() != null ? MoneyCalculation.compare(procedimento.getValorAuxiliar2(), BigDecimal.ZERO)==0 : true;
			
			if ((procedimento.getProfissionalAuxiliar2() != null) && (procedimento.getProfissionalAuxiliar2().equals(profissional)) && !isValorZero) {
				DetalheHonorarioMedico detalheHonorarioMedico = createDetalhe(procedimento, DetalheHonorarioMedico.AUXILIAR_2, profissional);
				this.getDetalhesHonorariosMedicos().add(detalheHonorarioMedico);
			}
			
			isValorZero = procedimento.getValorAuxiliar3() != null ? MoneyCalculation.compare(procedimento.getValorAuxiliar3(), BigDecimal.ZERO)==0 : true;
			
			if ((procedimento.getProfissionalAuxiliar3() != null) && (procedimento.getProfissionalAuxiliar3().equals(profissional)) && !isValorZero) {
				DetalheHonorarioMedico detalheHonorarioMedico = createDetalhe(procedimento, DetalheHonorarioMedico.AUXILIAR_3, profissional);
				this.getDetalhesHonorariosMedicos().add(detalheHonorarioMedico);
			}
			
			isValorZero = procedimento.getValorAnestesista() != null ? MoneyCalculation.compare(procedimento.getValorAnestesista(), BigDecimal.ZERO)==0 : true;
			if ((procedimento.getAnestesista() != null) && (procedimento.getAnestesista().equals(profissional)) && !isValorZero) {
				DetalheHonorarioMedico detalheHonorarioMedico = createDetalhe(procedimento, DetalheHonorarioMedico.ANESTESISTA, profissional);
				this.getDetalhesHonorariosMedicos().add(detalheHonorarioMedico);
			}
		} else if (procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_OUTROS) {
			isValorZero = procedimento.getValorProfissionalResponsavel() != null ? MoneyCalculation.compare(procedimento.getValorProfissionalResponsavel(), BigDecimal.ZERO)==0 : true;
			
			if ((procedimento.getProfissionalResponsavel() != null) && (procedimento.getProfissionalResponsavel().equals(profissional)) && !isValorZero) {
				DetalheHonorarioMedico detalheHonorarioMedico = createDetalhe(procedimento, DetalheHonorarioMedico.PROFISSIONAL_RESPONSAVEL, profissional);
				this.getDetalhesHonorariosMedicos().add(detalheHonorarioMedico);
			}			
		}
	}

	private DetalheHonorarioMedico createDetalhe(ProcedimentoInterface proc,
			String funcao, Profissional profissional) {
		DetalheHonorarioMedico detalheHonorarioMedico = new DetalheHonorarioMedico();
		detalheHonorarioMedico.setGuia(proc.getGuia());
		detalheHonorarioMedico.setNomeSegurado(proc.getGuia().getSegurado().getPessoaFisica().getNome());
		detalheHonorarioMedico.setPrestador(proc.getGuia().getPrestador());
		detalheHonorarioMedico.setProcedimento(proc);
		detalheHonorarioMedico.setProfissional(profissional);
		detalheHonorarioMedico.setFuncao(funcao);
		detalheHonorarioMedico.setValor(detalheHonorarioMedico.updateValor());
		return detalheHonorarioMedico;
	}
	
	private DetalheRecursoDeferido createDetalheRecursoDeferido(GuiaRecursoGlosa guia){
		DetalheRecursoDeferido detalheRecursoDeferido = new DetalheRecursoDeferido();
		detalheRecursoDeferido.setGuia(guia);
		detalheRecursoDeferido.setValor(guia.getValorTotal());
		return detalheRecursoDeferido;
	}
	
	protected void computaFaturamento(AbstractFaturamento faturamento) {
		
		valorTotalIr 		= valorTotalIr.add(faturamento.getValorImpostoDeRenda());
		valorTotalInss 		= valorTotalInss.add(faturamento.getValorInss());
		valorTotalIss 		= valorTotalIss.add(faturamento.getValorIss());
		valorTotalLiquido 	= valorTotalLiquido.add(faturamento.getValorLiquido());
		valorRecursosDeferidos = valorRecursosDeferidos.add(faturamento.getValorRecursosDeferidos()); 
		
		if(faturamento.getStatus() == Constantes.FATURAMENTO_ABERTO){
			this.setStatus("Aberto");
		} 
		if(faturamento.getStatus() == Constantes.FATURAMENTO_FECHADO){
			this.setStatus("Fechado");
		}
		if(faturamento.getStatus() == Constantes.FATURAMENTO_PAGO){
			this.setStatus("Pago");
		}
	}
	
	public int getNumeroDeFaturas(){
		return this.faturamentos.size();
	}
	
	public int getNumeroDePrestadores(){
		if (this.prestadores != null)
			return this.prestadores.size();
		else
			return 0;
	}

	public List<AbstractFaturamento> getFaturamentos() {
		List<AbstractFaturamento> resultado = new ArrayList<AbstractFaturamento>(faturamentos);
		Utils.sort(resultado,true,"descricaoCategoria","descricaoStatus","valorBruto");
		return resultado;
	}

	public List<DetalheHonorarioMedico> getDetalhesHonorariosMedicos() {
		return detalhesHonorariosMedicos;
	}

	public BigDecimal getImpostoDeRenda() {
		return valorTotalIr;
	}

	public BigDecimal getValorBrutoSemAlteracoes() {
		BigDecimal valor = BigDecimal.ZERO;
		for (AbstractFaturamento faturamento : this.faturamentos) {
			valor = valor.add(faturamento.getValorBrutoSemAlteracoes());
		}
		return valor;
	}
	
	public BigDecimal getValorSaldoAlteracoes() {
		BigDecimal valor = BigDecimal.ZERO;
		
		for (AbstractFaturamento faturamento : this.faturamentos) {
			valor = valor.add(faturamento.getValorSaldoAlteracoes());
		}
		
		return valor;
	}
	
	public BigDecimal getValorConsultas() {
		BigDecimal valor = BigDecimal.ZERO;
		for (DetalheValoresFaturamento det : this.getDetalhesValoresFaturamentos()) {
			if(!det.getDescricao().equals(DetalheValoresFaturamento.DESCRICAO_TOTAL)) {
				valor = valor.add(det.getValorConsultas());
			}
		}
		return valor;
	}

	public BigDecimal getValorConsultasOdonto() {
		BigDecimal valor = BigDecimal.ZERO;
		for (DetalheValoresFaturamento det : this.getDetalhesValoresFaturamentos()) {
			if(!det.getDescricao().equals(DetalheValoresFaturamento.DESCRICAO_TOTAL)) {
				valor = valor.add(det.getValorConsultasOdonto());
			}
		}
		return valor;
	}

	public BigDecimal getValorExames() {
		BigDecimal valor = BigDecimal.ZERO;
		for (DetalheValoresFaturamento det : this.getDetalhesValoresFaturamentos()) {
			if(!det.getDescricao().equals(DetalheValoresFaturamento.DESCRICAO_TOTAL)) {
				valor = valor.add(det.getValorExames());
			}
		}
		return valor;
	}
	
	public int getQuantidadeProcedimentos() {
		int quantidade = 0;
		for (DetalheValoresFaturamento det : this.getDetalhesValoresFaturamentos()) {
			if(!det.getDescricao().equals(DetalheValoresFaturamento.DESCRICAO_TOTAL)) {
				quantidade += det.getQuantidadeProcedimentos();
			}
		}
		return quantidade;
	}

	public BigDecimal getValorExamesOdonto() {
		BigDecimal valor = BigDecimal.ZERO;
		for (DetalheValoresFaturamento det : this.getDetalhesValoresFaturamentos()) {
			if(!det.getDescricao().equals(DetalheValoresFaturamento.DESCRICAO_TOTAL)) {
				valor = valor.add(det.getValorExamesOdonto());
			}
		}
		return valor;
	}

	public BigDecimal getValorAtendimentosUrgencia() {
		BigDecimal valor = BigDecimal.ZERO;
		for (DetalheValoresFaturamento det : this.getDetalhesValoresFaturamentos()) {
			if(!det.getDescricao().equals(DetalheValoresFaturamento.DESCRICAO_TOTAL)) {
				valor = valor.add(det.getValorAtendimentosUrgencia());
			}
		}
		return valor;
	}

	public BigDecimal getValorInternacoes() {
		BigDecimal valor = BigDecimal.ZERO;
		for (DetalheValoresFaturamento det : this.getDetalhesValoresFaturamentos()) {
			if(!det.getDescricao().equals(DetalheValoresFaturamento.DESCRICAO_TOTAL)) {
				valor = valor.add(det.getValorInternacoes());
			}
		}
		return valor;
	}

	public BigDecimal getValorGuiasHonorario() {
		BigDecimal valor = BigDecimal.ZERO;
		for (DetalheValoresFaturamento det : this.getDetalhesValoresFaturamentos()) {
			if(!det.getDescricao().equals(DetalheValoresFaturamento.DESCRICAO_TOTAL)) {
				valor = valor.add(det.getValorGuiasHonorario());
			}
		}
		return valor;
	}
	
	public BigDecimal getValorGuiasAcompanhamentoAnestesico() {
		BigDecimal valor = BigDecimal.ZERO;
		for (DetalheValoresFaturamento det : this.getDetalhesValoresFaturamentos()) {
			if(!det.getDescricao().equals(DetalheValoresFaturamento.DESCRICAO_TOTAL)) {
				valor = valor.add(det.getValorAcompanhamentoAnestesico());
			}
		}
		return valor;
	}
	
	public BigDecimal getValorBruto() {
		return valorTotalBruto;
	}

	public Date getCompetencia() {
		return competencia;
	}
	
	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}
	
	public String getCompetenciaFormatada() {
		return Utils.format(this.competencia,"MM/yyyy");
	}

	public String getDataAtual() {
		return Utils.format(new Date());
	}

	public Faturamento getFaturamento() {
		return faturamento;
	}

	public boolean isComputaSubnivel() {
		return computaSubnivel;
	}

	public int getTipoResumo() {
		return tipoResumo;
	}

	public BigDecimal getValorInss() {
		return valorTotalInss;
	}

	public BigDecimal getValorIss() {
		return valorTotalIss;
	}

	public BigDecimal getValorLiquido() {
		return valorTotalLiquido;
	}

	public String getDescricao() {
		if (tipoResumo == RESUMO_CATEGORIA)
			return getDescricaoCategoria();
		else 
			return getDescricaoPessoa();
	}

	private String getDescricaoPessoa() {
		if (this.categoria == 1) 
			return "Pessoa Jurídica";
		else 
			return "Pessoa Física";
	}
	
	 public Set<Prestador> getPrestadores() {
		return prestadores;
	}
	 
	 public void setPrestadores(Set<Prestador> prestadores) {
		this.prestadores = prestadores;
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

	public List<ResumoFaturamentos> getResumos() {
		return resumos;
	}

	public Integer getCategoria() {
		return categoria;
	}

	public List<GuiaFaturavel> getGuias() {
		return guias;
	}
	
	public List<GuiaFaturavel> getGuiasOrdenado() {
		return GuiaUtils.ordenarGuiasPorTipoEAutorizacao(guias);
	}

	public Float getNumeroProcedimentosGlosados() {
		return numeroProcedimentosGlosados;
	}

	public Float getValorProcedimentosGlosados() {
		return valorProcedimentosGlosados;
	}

	public Set<DetalheValoresFaturamento> getDetalhesValoresFaturamentos() {
		return detalhesValoresFaturamentos;
	}

	public long getNumeroExamesAmbulatoriais() {
		return numeroExamesAmbulatoriais;
	}

	public BigDecimal getValorExamesAmbulatoriais() {
		return valorExamesAmbulatoriais;
	}

	public long getNumeroExamesExternoInternacao() {
		return numeroExamesExternoInternacao;
	}

	public BigDecimal getValorExamesExternoInternacao() {
		return valorExamesExternoInternacao;
	}

	public long getNumeroExamesExternoAtendimentosUrgencia() {
		return numeroExamesExternoAtendimentosUrgencia;
	}

	public BigDecimal getValorExamesExternoAtendimentosUrgencia() {
		return valorExamesExternoAtendimentosUrgencia;
	}

	public int getNumeroGuiasExamesAmbulatoriais() {
		return numeroGuiasExamesAmbulatoriais;
	}

	public int getNumeroGuiasExamesExternoInternacao() {
		return numeroGuiasExamesExternoInternacao;
	}

	public int getNumeroGuiasExamesExternoAtendimentosUrgencia() {
		return numeroGuiasExamesExternoAtendimentosUrgencia;
	}

	public String getTipoFaturamento() {
		return tipoFaturamento;
	}

	public void setTipoFaturamento(String tipoFaturamento) {
		this.tipoFaturamento = tipoFaturamento;
	}

	public boolean isAlteraSituacaoDasGuias() {
		return alteraSituacaoDasGuias;
	}

	public void setAlteraSituacaoDasGuias(boolean alteraSituacaoDasGuias) {
		this.alteraSituacaoDasGuias = alteraSituacaoDasGuias;
	}

	public Set<Procedimento> getProcedimentos() {
		return procedimentos;
	}

	public void setProcedimentos(Set<Procedimento> procedimentos) {
		this.procedimentos = procedimentos;
	}
	
	public Prestador getPrestador() {
		return prestador;
	}
	
	public Date getDataGeracao(){
		return this.dataGeracao;
	}
	
	public Set<OcorrenciaFaturamento> getOcorrencias() {
		return ocorrencias;
	}

	public void setDataPagamento(Date dataPagamento){
		this.dataPagamento = dataPagamento;
	}
	
	public Date getDataPagamento() {
		return dataPagamento;
	}

	public List<ResumoFaturamentos> getResumosPorPrestador() {
		return resumosPorPrestador;
	}

	public void setResumosPorPrestador(List<ResumoFaturamentos> resumosPorPrestador) {
		this.resumosPorPrestador = resumosPorPrestador;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}
	
	public List<DetalheRecursoDeferido> getDetalhesRecursosDeferidos() {
		return detalhesRecursosDeferidos;
	}

	public void setDetalhesRecursosDeferidos(
			List<DetalheRecursoDeferido> detalhesRecursosDeferidos) {
		this.detalhesRecursosDeferidos = detalhesRecursosDeferidos;
	}

	public BigDecimal getValorRecursosDeferidos() {
		return valorRecursosDeferidos;
	}

	public void setValorRecursosDeferidos(BigDecimal valorRecursosDeferidos) {
		this.valorRecursosDeferidos = valorRecursosDeferidos;
	}
}