package br.com.infowaypi.ecarebc.atendimentos;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import br.com.infowaypi.ecare.atendimentos.TipoGuiaEnum;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GeradorGuiaHonorarioComponent;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GeradorGuiaHonorarioInterface;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoHonorario;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * Classe que representa uma guia de cirurgia odontológica
 * @author Danilo Nogueira Portela
 */
public class GuiaCirurgiaOdonto<P extends ProcedimentoInterface> extends GuiaCompleta<P> implements GeradorGuiaHonorarioInterface {
	
	private static final long serialVersionUID = -1314386288975035551L;
	private GeradorGuiaHonorarioComponent geradorHonorarioComponent;

	public GuiaCirurgiaOdonto(){
		super();
		this.geradorHonorarioComponent = new GeradorGuiaHonorarioComponent(this);
	}
	
	public GuiaCirurgiaOdonto(UsuarioInterface usuario){
		super(usuario);
		this.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.CIRURGIA_ODONTOLOGICA.getMessage(), new Date());
		this.geradorHonorarioComponent = new GeradorGuiaHonorarioComponent(this);
	}
	
	@Override
	public int getPrazoInicial() {
		return 0;
	}
	
	@Override
	public TipoGuiaEnum getTipoGuiaEnum() {
		return TipoGuiaEnum.CIRURGIA_ODONTOLOGICA;
	}

	@Override
	public String getTipo() {
		return "Cirurgia Odontológica";
	}

	@Override
	public boolean isAtendimentoUrgencia() {
		return false;
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
	public boolean isConsultaOdontoUrgencia() {
		return false;
	}

	@Override
	public boolean isConsultaUrgencia() {
		return false;
	}

	@Override
	public boolean isExame() {
		return false;
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
	public boolean isSimples() {
		return false;
	}

	@Override
	public boolean isUrgencia() {
		return false;
	}

	@Override
	public boolean isCirurgiaOdonto() {
		return true;
	}

	@Override
	public boolean isRegulaConsumo() {
		return false;
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
		return false;
	}
	
	@Override
	public Date getDataVencimento() {
		Calendar vencimento = Calendar.getInstance();
		vencimento.setTime(getDataMarcacao());
		vencimento.add(Calendar.DAY_OF_MONTH, PRAZO_VENCIMENTO_INTERNACOES);
		return vencimento.getTime();
	}

	@Override
	public boolean isGuiaImpressaoNova() {
		return false;
	}

	@Override
	public boolean isUrgenciaOuAtendimentoSubsequente() {
		return false;
	}

	@Override
	public Set<Diaria> getAcomodacoes() {
		return this.geradorHonorarioComponent.getAcomodacoes();
	}

	@Override
	public Set<ProcedimentoInterface> getProcedimentosAptosAGerarHonorariosMedicos() {	
		return this.geradorHonorarioComponent.getProcedimentosAptosAGerarHonorariosMedicos();
	}

	@Override
	public Set<ProcedimentoHonorario> getProcedimentosHonorario() {
		return geradorHonorarioComponent.getProcedimentosHonorario();
	}

	@Override
	public Set<ProcedimentoInterface> getProcedimentosQueVaoGerarHonorario() {
		return this.geradorHonorarioComponent.getProcedimentosQueVaoGerarHonorario();
	}
	
	@Override
	public Set<ProcedimentoInterface> getProcedimentosQueVaoGerarHonorarioAnestesista() {
		return this.geradorHonorarioComponent.getProcedimentosQueVaoGerarHonorarioAnestesista();
	}

	@Override
	public Set<ProcedimentoInterface> getProcedimentosQueAindaPodemGerarHonorariosAnestesitas() {
		return this.geradorHonorarioComponent.getProcedimentosQueAindaPodemGerarHonorariosAnestesitas();
	}

	@Override
	public Set<ProcedimentoInterface> getProcedimentosQueAindaPodemGerarHonorarios() {
		return this.geradorHonorarioComponent.getProcedimentosQueAindaPodemGerarHonorarios();
	}

	@Override
	public boolean isPossuiProcdimentosQueAindaGeramHonorarios() {
		return this.geradorHonorarioComponent.isPossuiProcdimentosQueAindaGeramHonorarios();
	}

	@Override
	public ProcedimentoCirurgicoInterface getProcedimentoMaisRecenteRealizadoPeloProfissional(Profissional profissional) {
		return this.geradorHonorarioComponent.getProcedimentoMaisRecenteRealizadoPeloProfissional(profissional);
	}

	@Override
	public Set<ProcedimentoCirurgicoInterface> getProcedimentoQueJaPossuemDataDeRealizacao() {
		return this.geradorHonorarioComponent.getProcedimentoQueJaPossuemDataDeRealizacao();
	}

	@Override
	public Set<ProcedimentoCirurgicoInterface> getProcedimentoQueNaoPossuemDataDeRealizacao() {
		return this.geradorHonorarioComponent.getProcedimentoQueNaoPossuemDataDeRealizacao();
	}
	
	@Override
	public boolean isProfissionalPodeRegistrarHonorarioIndividual(Profissional profissional) {
		return this.geradorHonorarioComponent.isProfissionalPodeRegistrarHonorarioIndividual(profissional);
	}

	@Override
	public void setProfissionalDoFluxo(Profissional profissional) {
		this.geradorHonorarioComponent.setProfissionalDoFluxo(profissional);
	}

	@Override
	public void setGeracaoParaPrestadorMedico(boolean geracaoParaHonorarioMedico) {
		this.geradorHonorarioComponent.setGeracaoParaPrestadorMedico(geracaoParaHonorarioMedico);
	}

	@Override
	public Set<ProcedimentoCirurgico> getProcedimentosQueGeraramHonorariosExternos() {
		return this.geradorHonorarioComponent.getProcedimentosQueGeraramHonorariosExternos();
	}

	@Override
	public Set<ProcedimentoInterface> getProcedimentosAptosAGerarHonorariosAnestesista() {
		return this.geradorHonorarioComponent.getProcedimentosAptosAGerarHonorariosAnestesista();
	}

	@Override
	public int getPrioridadeEmAuditoria() {
		return this.geradorHonorarioComponent.getPrioridadeEmAuditoria();
	}

	@Override 
	public Set<ProcedimentoCirurgico> getProcedimentosCirurgicosQueAindaPodemGerarHonorariosAnestesitas() {
		return this.geradorHonorarioComponent.getProcedimentosCirurgicosQueAindaPodemGerarHonorariosAnestesitas();
	}
	
	@Override 
	public Set<Procedimento> getProcedimentosNormaisQueAindaPodemGerarHonorariosAnestesitas() {
		return this.geradorHonorarioComponent.getProcedimentosNormaisQueAindaPodemGerarHonorariosAnestesitas();
	}
	
	@Override
	public Set<Procedimento> getProcedimentosExameQueGeraramHonorariosExternos() {
		return this.geradorHonorarioComponent.getProcedimentosExameQueGeraramHonorariosExternos();
	}
	
	@Override
	protected GuiaSimples<ProcedimentoInterface> newInstance() {
		return new GuiaCirurgiaOdonto<ProcedimentoInterface>();
	}
}
