package br.com.infowaypi.ecarebc.atendimentos;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecare.atendimentos.TipoGuiaEnum;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GeradorGuiaHonorarioComponent;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GeradorGuiaHonorarioInterface;
import br.com.infowaypi.ecarebc.enums.GrauDeParticipacaoEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.Role;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoHonorario;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.utils.ProcedimentoUtils;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;
 
/**
 * Classe que representa uma guia de exame médico
 * @author Danilo Nogueira Portela
 * @CHANGES IDELVANE
 */
@SuppressWarnings("unchecked")
public class GuiaExame<P extends ProcedimentoInterface> extends GuiaCompleta<P> implements GeradorGuiaHonorarioInterface {

	private static final long serialVersionUID = 1L;
	public static final int NUMERO_MAXIMO_PROCEDIMENTOS = 5;
	public static final int NUMERO_MAXIMO_PROCEDIMENTOS_HEMOTERAPIA = 15;
	
	public static final int PRIORIDADE_ALTA = 1;
	public static final int PRIORIDADE_MEDIA = 2;
	public static final int PRIORIDADE_BAIXA = 3;
	
	public static final int PRAZO_SOLICITACAO_EXAMES_URGENCIA = 12;
	public static List<String> CODIGOS_CBHPM_ANESTESISTA = Arrays.asList("90000001", "90000002","90000003","90000004");
	public static List<String> CODIGOS_ASSISTENCIA_DOMICILIAR = Arrays.asList("98000012", "98000013");
	
	private boolean exigeSolicitante;
	private BigDecimal valorMaterialComplementarSolicitado;
	private BigDecimal valorMaterialComplementarAuditado;
	
	private GeradorGuiaHonorarioComponent geradorHonorarioComponent;
	
	private BigDecimal valorMedicamentoComplementarSolicitado;
	private BigDecimal valorMedicamentoComplementarAuditado;
	
	public GuiaExame() {
		super(null);
	}

	public GuiaExame(UsuarioInterface usuario) {
		super(usuario);
		this.valorMaterialComplementarAuditado = BigDecimal.ZERO;
		this.valorMaterialComplementarSolicitado = BigDecimal.ZERO;
		this.valorMedicamentoComplementarAuditado = BigDecimal.ZERO;
		this.valorMedicamentoComplementarSolicitado = BigDecimal.ZERO;
		this.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AGENDAMENTO_EXAME.getMessage(), new Date());
	}
	
	@Override
	public TipoGuiaEnum getTipoGuiaEnum() {
		return TipoGuiaEnum.EXAME;
	}
	
	@Override
	public String getTipo() {
		return "Exame";
	}

	@Override
	public boolean isConsulta() {
		return false;
	}

	@Override
	public boolean isConsultaOdonto() {
		return false;
	}

	@Override
	public boolean isConsultaUrgencia() {
		return false;
	}

	@Override
	public boolean isExame() {
		return true;
	}

	@Override
	public boolean isExameOdonto() {
		return false;
	}

	@Override
	public boolean isInternacao() {
		return false;
	}

	@Override
	public boolean isInternacaoEletiva() {
		return false;
	}

	@Override
	public boolean isInternacaoUrgencia() {
		return false;
	}

	@Override
	public boolean isCompleta() {
		return true;
	}

	@Override
	public boolean isSimples() {
		return true;
	}

	@Override
	public boolean isAtendimentoUrgencia() {
		return false;
	}

	@Override
	public boolean isUrgencia() {
		return false;
	}

	public boolean isExigeSolicitante() {
		return exigeSolicitante;
	}

	public void setExigeSolicitante(boolean exigeSolicitante) {
		this.exigeSolicitante = exigeSolicitante;
	}

	public BigDecimal getValorMaterialComplementarSolicitado() {
		return valorMaterialComplementarSolicitado;
	}

	public void setValorMaterialComplementarSolicitado(BigDecimal valorMaterialComplementarSolicitado) {
		this.valorMaterialComplementarSolicitado = valorMaterialComplementarSolicitado;
	}

	public BigDecimal getValorMaterialComplementarAuditado() {
		return valorMaterialComplementarAuditado;
	}

	public void setValorMaterialComplementarAuditado(BigDecimal valorMaterialComplementarAuditado) {
		this.valorMaterialComplementarAuditado = valorMaterialComplementarAuditado;
	}

	public BigDecimal getValorMedicamentoComplementarSolicitado() {
		return valorMedicamentoComplementarSolicitado;
	}

	public void setValorMedicamentoComplementarSolicitado(BigDecimal valorMedicamentoComplementarSolicitado) {
		this.valorMedicamentoComplementarSolicitado = valorMedicamentoComplementarSolicitado;
	}

	public BigDecimal getValorMedicamentoComplementarAuditado() {
		return valorMedicamentoComplementarAuditado;
	}

	public void setValorMedicamentoComplementarAuditado(BigDecimal valorMedicamentoComplementarAuditado) {
		this.valorMedicamentoComplementarAuditado = valorMedicamentoComplementarAuditado;
	}
	
	public BigDecimal getValorTotalProcedimentos() {
		BigDecimal valorProcedimentos = BigDecimal.ZERO;
		
		for (ProcedimentoInterface procedimento : this.getProcedimentos()) {
			if(!procedimento.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())){
				valorProcedimentos = valorProcedimentos.add(procedimento.getValorTotal());
			}
		}
		
		return valorProcedimentos;
	}
	
	public BigDecimal getValorTotalSolicitado() {
		if(this.valorMaterialComplementarSolicitado == null){
			this.valorMaterialComplementarSolicitado = BigDecimal.ZERO;
		}
		
		if(this.valorMedicamentoComplementarSolicitado == null){
			this.valorMedicamentoComplementarSolicitado = BigDecimal.ZERO;
		}
		
		return MoneyCalculation.getSoma(this.getValorMaterialComplementarSolicitado(), this.getValorMedicamentoComplementarSolicitado().floatValue());
	}
	
	public BigDecimal getValorTotalAuditado() {
		if(this.valorMaterialComplementarAuditado == null){
			this.valorMaterialComplementarAuditado = BigDecimal.ZERO;
		}
		
		if(this.valorMedicamentoComplementarAuditado == null){
			this.valorMedicamentoComplementarAuditado = BigDecimal.ZERO;
		}
		
		return MoneyCalculation.getSoma(this.getValorMaterialComplementarAuditado(), this.getValorMedicamentoComplementarAuditado().floatValue());
	}

	@Override
	public BigDecimal getValorCoParticipacao() {
		if(!isExameExterno()){
			return super.getValorCoParticipacao();
		} else {
			return MoneyCalculation.rounded(BigDecimal.ZERO);
		}
	}

	@Override
	public boolean isConsultaOdontoUrgencia() {
		return false;
	}

	@Override
	public boolean isCirurgiaOdonto() {
		return false;
	}

	@Override
	public boolean isRegulaConsumo() {
		if(isExameExterno()){
			return false;
		}
		
		return true;
	}

	@Override
	public boolean isTratamentoSeriado() {
		return false;
	}

	@Override
	public boolean isConsultaEletiva() {
		return false;
	}

	@Override
	public boolean isExameEletivo() {
		return true;
	}
	
	/**
	 * Retorna todos os procedimentos anestésicos contidos na guia de exame.
	 */
	public Set<ProcedimentoInterface> getProcedimentosAnestesiologia() {
		
		Set<ProcedimentoInterface> procedimentosAnestesiologia = new HashSet<ProcedimentoInterface>();
		for (ProcedimentoInterface procedimento : this.getProcedimentosNaoRealizadosNaoNaoAutorizadosENaoSolicitados()) {
			if(CODIGOS_CBHPM_ANESTESISTA.contains(procedimento.getProcedimentoDaTabelaCBHPM().getCodigo())){
				procedimentosAnestesiologia.add(procedimento);
			}
		}
		return procedimentosAnestesiologia;
	}
		
	/**
	 * Indica se a guia de exame possui algum procedimento anestésico.
	 * Códigos: 90000001, 90000002, 90000003 e 90000004
	 * @return boolean
	 */
	public boolean isGuiaCoopanest(){
		return !this.getProcedimentosAnestesiologia().isEmpty();
	}
	
	@Override
	public Date getDataVencimento() {
		Calendar vencimento = Calendar.getInstance();
		vencimento.setTime(getDataMarcacao());
		vencimento.add(Calendar.DAY_OF_MONTH, PRAZO_VENCIMENTO_CONSULTAS_E_EXAMES);
		return vencimento.getTime();
	}
	
	public List<P> getProcedimentosSolicitadosOuAutorizados() {
		return getProcedimentos(SituacaoEnum.REALIZADO.descricao(), 
								SituacaoEnum.SOLICITADO.descricao(),
								SituacaoEnum.AUTORIZADO.descricao(),
								SituacaoEnum.CONFIRMADO.descricao(),
								SituacaoEnum.FATURADA.descricao());
	}
	
	public List<P> getProcedimentosSolicitadosAutorizadosCanceladosEGlosados() {
		return getProcedimentos(SituacaoEnum.REALIZADO.descricao(), 
				SituacaoEnum.SOLICITADO.descricao(),
				SituacaoEnum.AUTORIZADO.descricao(),
				SituacaoEnum.CONFIRMADO.descricao(),
				SituacaoEnum.FATURADA.descricao(),
				SituacaoEnum.CANCELADO.descricao(),
				SituacaoEnum.REMOVIDO.descricao(),
				SituacaoEnum.GLOSADO.descricao());
	}
	
	public List<P> getProcedimentosNaoCanceladoENaoNaoAutorizados() {
		return getProcedimentos(SituacaoEnum.SOLICITADO.descricao(),
								SituacaoEnum.AUTORIZADO.descricao(),
								SituacaoEnum.CONFIRMADO.descricao(),
								SituacaoEnum.GLOSADO.descricao());
	}
	
	public List<P> getProcedimentosSolicitadosOuAutorizadosOrdenado() {
		return ordenarProcedimentosPorSituacaoEDescricao(getProcedimentosSolicitadosOuAutorizados());
	}
	
	public List<P> getProcedimentosSolicitadosAutorizadosCanceladosEGlosadosOrdenado() {
		return ordenarProcedimentosPorSituacaoEDescricao(getProcedimentosSolicitadosAutorizadosCanceladosEGlosados());
	}

	@Override
	public boolean isPrazoParaFechamentoTerminou() {
		
		if(this.getDataAtendimento() == null){
			return false;
		}
		
		Calendar limiteParaFechamento = Calendar.getInstance();
		limiteParaFechamento.setTime(this.getDataAtendimento());
		
		int prazoRestanteParaFechamento = Utils.diferencaEmDias(limiteParaFechamento,Calendar.getInstance());
		if(this.isSituacaoAtual(SituacaoEnum.CONFIRMADO.descricao()) && this.isPermiteMatMed() && prazoRestanteParaFechamento <= 45){
			return false;
		}
		
		return true;
	}
	
	public int getPrioridadeAutorizacao(){
		if (this.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())){
			Calendar dtSolicitacao = new GregorianCalendar();
			dtSolicitacao.setTime(this.getDataSolicitacao());
			int diferenca = Utils.diferencaEmDias(dtSolicitacao, Calendar.getInstance());
			
			if(diferenca <=2){
				return PRIORIDADE_BAIXA;
			} if (diferenca > 2 && diferenca<=3) {
				return PRIORIDADE_MEDIA;
			}
			
			return PRIORIDADE_ALTA;
		} else {
			return 0;
		}
	}
	
	@Override
	public boolean isGuiaImpressaoNova(){
		if (this.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao())|| this.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())){
			return true;
		}
		
		return false;	
	}
	
	public boolean isAutorizada(){
		if (this.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao())){
			return true;
		}
		
		return false;
	}
	
	public boolean isSolicitada(){
		if (this.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())){
			return true;
		}
		
		return false;
	}
	
	public Date getDataSolicitacao(){
		SituacaoInterface situacao = this.getSituacao(SituacaoEnum.SOLICITADO.descricao());
		if (situacao!=null){
			return situacao.getDataSituacao();
		}
		
		return null;
	}
	
	public Date getDataAutorizacao(){
		SituacaoInterface situacao = this.getSituacao(SituacaoEnum.AUTORIZADO.descricao());
		if (situacao != null){
			return situacao.getDataSituacao();
		}
		
		return null;
	}
	
	public boolean isSolicitadaPelaCentral() {
		
		boolean isSolicitada = this.isSolicitada();
		boolean isSituacaoDaCentral = this.getSituacao().getUsuario().isPossuiRole(Role.CENTRAL_DE_SERVICOS.getValor());
		
		return isSolicitada && isSituacaoDaCentral;
	}

	@Override
	public int getPrazoInicial() {
		return 0;	
	}
	
	@Override
	public boolean isUrgenciaOuAtendimentoSubsequente() {
		return false;
	}
	
	public Set<GuiaSimples> getGuiasConfirmacao() {	
		Set<GuiaSimples> guiasFilhasDeExame = new HashSet<GuiaSimples>();
		for (GuiaSimples guia : super.getGuiasFilhas()) {
			if (guia.isExame()){
				guiasFilhasDeExame.add(guia);
			}
		}
		
		return guiasFilhasDeExame;
	}
	
	public void addGuiaAcompanhamento(GuiaSimples guia) {
		this.getGuiasFilhas().add(guia);
		guia.setGuiaOrigem(this);
	}
	
	public Set<ProcedimentoInterface> getProcedimentosComPorteAnestesico() {
		Set<ProcedimentoInterface> procedimentosComPorteAnestesico = new HashSet<ProcedimentoInterface>();
		Set<P> procedimentos = this.getProcedimentos();
		for (P procedimento : procedimentos) {
			if (procedimento.getProcedimentoDaTabelaCBHPM().getPorteAnestesico() != null) {
				procedimentosComPorteAnestesico.add(procedimento);
			}
		}
		return procedimentosComPorteAnestesico;
	}
	
	public Set<ProcedimentoInterface> getProcedimentosComPorteAnestesicoSemHonorariosAnestesista() {
		Set<ProcedimentoInterface> result = new HashSet<ProcedimentoInterface>();
		
		Set<ProcedimentoInterface> procedimentosComPorteAnestesico = this.getProcedimentosComPorteAnestesico();
		for (ProcedimentoInterface procedimento : procedimentosComPorteAnestesico) {
			if (!procedimento.containsHonorarioExterno(GrauDeParticipacaoEnum.ANESTESISTA.getCodigo())) {
				result.add(procedimento);
			}
		}
		return result;
	}
	
	@Override
	public int getPrioridadeEmAuditoria() {
		return getGeradorHonorarioComponent().getPrioridadeEmAuditoria();
	}
	
	@Override
	public Set<ProcedimentoInterface> getProcedimentosAptosAGerarHonorariosMedicos() {	
		return getGeradorHonorarioComponent().getProcedimentosAptosAGerarHonorariosMedicos();
	}
	
	@Override
	public Set<Diaria> getAcomodacoes() {
		return getGeradorHonorarioComponent().getAcomodacoes();
	}
	
	@Override
	public boolean isGeraExameExterno() {
		return false;
	}
	
	@Override
	public Set<ProcedimentoHonorario> getProcedimentosHonorario() {
		return getGeradorHonorarioComponent().getProcedimentosHonorario();
	}

	@Override
	public Set<ProcedimentoInterface> getProcedimentosQueVaoGerarHonorario() {
		return getGeradorHonorarioComponent().getProcedimentosQueVaoGerarHonorario();
	}
	
	@Override
	public Set<ProcedimentoInterface> getProcedimentosQueVaoGerarHonorarioAnestesista() {
		return this.getProcedimentosComPorteAnestesico();
	}
	
	@Override
	public Set<ProcedimentoInterface> getProcedimentosQueAindaPodemGerarHonorariosAnestesitas() {
		return this.getProcedimentosComPorteAnestesicoSemHonorariosAnestesista();
	}
	
	@Override
	public Set<ProcedimentoInterface> getProcedimentosQueAindaPodemGerarHonorarios() {
		return getGeradorHonorarioComponent().getProcedimentosQueAindaPodemGerarHonorarios();
	}
	
	@Override
	public boolean isPossuiProcdimentosQueAindaGeramHonorarios() {
		return getGeradorHonorarioComponent().isPossuiProcdimentosQueAindaGeramHonorarios();
	}
	
	@Override
	public ProcedimentoCirurgicoInterface getProcedimentoMaisRecenteRealizadoPeloProfissional(Profissional profissional) {
		return getGeradorHonorarioComponent().getProcedimentoMaisRecenteRealizadoPeloProfissional(profissional);
	}
	
	@Override
	public Set<ProcedimentoCirurgicoInterface> getProcedimentoQueJaPossuemDataDeRealizacao() {
		return getGeradorHonorarioComponent().getProcedimentoQueJaPossuemDataDeRealizacao();
	}
	
	@Override
	public Set<ProcedimentoCirurgicoInterface> getProcedimentoQueNaoPossuemDataDeRealizacao() {
		return getGeradorHonorarioComponent().getProcedimentoQueNaoPossuemDataDeRealizacao();
	}
	
	@Override
	public boolean isProfissionalPodeRegistrarHonorarioIndividual(Profissional profissional) {
		return getGeradorHonorarioComponent().isProfissionalPodeRegistrarHonorarioIndividual(profissional);
	}
	
	@Override
	public void setProfissionalDoFluxo(Profissional profissional) {
		getGeradorHonorarioComponent().setProfissionalDoFluxo(profissional);
	}
	
	@Override
	public void setGeracaoParaPrestadorMedico(boolean geracaoParaHonorarioMedico) {
		getGeradorHonorarioComponent().setGeracaoParaPrestadorMedico(geracaoParaHonorarioMedico);
	}

	@Override
	public Set<ProcedimentoCirurgico> getProcedimentosQueGeraramHonorariosExternos() {
		return getGeradorHonorarioComponent().getProcedimentosQueGeraramHonorariosExternos();
	}

	@Override
	public Set<Procedimento> getProcedimentosExameQueGeraramHonorariosExternos() {
		return getGeradorHonorarioComponent().getProcedimentosExameQueGeraramHonorariosExternos();
	}

	
	@Override
	public boolean isGeraExameExternoParaPacientesCronicos() {
		Set<ProcedimentoInterface> procedimentos = this.getProcedimentosNaoCanceladosENegados();
		for (ProcedimentoInterface procedimento : procedimentos) {
			String codigo = procedimento.getProcedimentoDaTabelaCBHPM().getCodigo();
			if (CODIGOS_ASSISTENCIA_DOMICILIAR.contains(codigo)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Set<ProcedimentoInterface> getProcedimentosAptosAGerarHonorariosAnestesista() {
		return getGeradorHonorarioComponent().getProcedimentosAptosAGerarHonorariosAnestesista();
	}

	@Override 
	public Set<ProcedimentoCirurgico> getProcedimentosCirurgicosQueAindaPodemGerarHonorariosAnestesitas() {
		return ProcedimentoUtils.getProcedimentosDaHeranca(ProcedimentoCirurgico.class, this.getProcedimentosQueAindaPodemGerarHonorariosAnestesitas());
	}
	
	@Override 
	public Set<Procedimento> getProcedimentosNormaisQueAindaPodemGerarHonorariosAnestesitas() {
		return ProcedimentoUtils.getProcedimentosDaClasse(Procedimento.class, getProcedimentosQueAindaPodemGerarHonorariosAnestesitas());
	}
	
	@Override
	protected GuiaSimples<ProcedimentoInterface> newInstance() {
		return new GuiaExame<ProcedimentoInterface>();
	}
	
	public void removeProcedimento(P procedimento) throws Exception{
		super.removeProcedimento(procedimento);
	}

	public GeradorGuiaHonorarioComponent getGeradorHonorarioComponent() {
		if (geradorHonorarioComponent == null) {
			geradorHonorarioComponent = new GeradorGuiaHonorarioComponent(this);
		}
		return geradorHonorarioComponent;
	}
}
