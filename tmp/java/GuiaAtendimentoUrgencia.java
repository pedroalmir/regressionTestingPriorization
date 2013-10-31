package br.com.infowaypi.ecarebc.atendimentos;

import java.util.Calendar;
import java.util.Date;

import br.com.infowaypi.ecare.atendimentos.TipoGuiaEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * Classe que representa um atendimento de urgência
 * @author Rondinele
 *
 */
 @SuppressWarnings({ "unchecked", "rawtypes" })
public class GuiaAtendimentoUrgencia extends GuiaCompleta {
  
	private static final long serialVersionUID = 1L;
	
	private boolean valorada;
	
	public GuiaAtendimentoUrgencia() {
		this(null);
	}
	
	public GuiaAtendimentoUrgencia(UsuarioInterface usuario){
		super(usuario);
		this.valorada = false;
		this.setDataTerminoAtendimento(new Date());
		this.mudarSituacao(usuario, SituacaoEnum.ABERTO.descricao(), MotivoEnum.ATENDIMENTO_URGENCIA.getMessage(), new Date());
	}
	
	public void addProcedimentoConsultaZerado() throws Exception {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("codigo",PROCEDIMENTO_CONSULTA_URGENCIA));
		TabelaCBHPM tabelaCBHPM = (TabelaCBHPM) sa.list(TabelaCBHPM.class).get(0);
		
		Procedimento procedimento = new Procedimento();
		procedimento.setProcedimentoDaTabelaCBHPM(tabelaCBHPM);
		procedimento.mudarSituacao(this.getSituacao().getUsuario(), SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.ATENDIMENTO_SUBSEQUENTE.getMessage(), new Date());

		this.getProcedimentos().add(procedimento);
		procedimento.setGuia(this);
	}

	@Override
	public TipoGuiaEnum getTipoGuiaEnum() {
		return TipoGuiaEnum.ATENDIMENTO_URGENCIA;
	}
	
	@Override
	public String getTipo() {
		return "Atendimento Subsequente";
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
	public boolean isAtendimentoUrgencia() {
		return true;
	}

	@Override
	public int getPrazoInicial() {
		return PRAZO_ATENDIMENTO_URGENCIA;
	}

	@Override
	public boolean isUrgencia() {
		return true;
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
	
	public boolean isGuiaImpressaoNova(){
		return false;	
	}

	@Override
	public Date getDataVencimento() {
		Calendar vencimento = Calendar.getInstance();
		vencimento.setTime(getDataAtendimento());
		vencimento.add(Calendar.DAY_OF_MONTH, PRAZO_VENCIMENTO_CONSULTAS_E_EXAMES);
		return vencimento.getTime();
	}

	@Override
	public boolean isUrgenciaOuAtendimentoSubsequente() {
		return true;
	}

	@Override
	protected GuiaSimples newInstance() {
		return new GuiaAtendimentoUrgencia();
	}

	public boolean isValorada() {
		return valorada;
	}

	public void setValorada(boolean valorada) {
		this.valorada = valorada;
	}
	
	@Override
	public boolean isRecursoGlosa() {
		// TODO Auto-generated method stub
		return false;
	}
}
