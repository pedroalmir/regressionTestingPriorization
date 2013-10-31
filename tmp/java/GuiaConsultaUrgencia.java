package br.com.infowaypi.ecarebc.atendimentos;

import java.util.Calendar;
import java.util.Date;

import br.com.infowaypi.ecare.atendimentos.TipoGuiaEnum;
import br.com.infowaypi.ecarebc.associados.ManagerPrestador;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.user.UsuarioInterface;
 
/**
 * Classe que representa uma guia de consulta médica de urgência
 * @author Danilo Nogueira Portela
 * @changes Idelvane
 */
@SuppressWarnings("serial")
public class GuiaConsultaUrgencia extends GuiaCompleta<ProcedimentoInterface> { 
	
	public GuiaConsultaUrgencia() {
		this(null);
	}
	
	public GuiaConsultaUrgencia(UsuarioInterface usuario){
		super(usuario);
		this.setDataTerminoAtendimento(new Date());
		this.mudarSituacao(usuario, SituacaoEnum.ABERTO.descricao(), MotivoEnum.CONSULTA_URGENCIA.getMessage(), new Date());
	}
	
	public void addProcedimentoConsulta() throws Exception {
		TabelaCBHPM tabelaCBHPM = ManagerPrestador.getConsultaEspecialidade(getPrestador(), getEspecialidade(), true);
		
		if(tabelaCBHPM == null){
			SearchAgent sa = new SearchAgent();
			sa.addParameter(new Equals("codigo",PROCEDIMENTO_CONSULTA_URGENCIA));
			tabelaCBHPM = (TabelaCBHPM) sa.list(TabelaCBHPM.class).get(0);
		}
		
		Procedimento procedimento = new Procedimento();
		procedimento.setProcedimentoDaTabelaCBHPM(tabelaCBHPM);
		procedimento.calcularCampos();
		procedimento.mudarSituacao(this.getSituacao().getUsuario(), SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.CONSULTA_URGENCIA.getMessage(), new Date());
		this.addProcedimento(procedimento);
	}
	
	@Override
	public TipoGuiaEnum getTipoGuiaEnum() {
		return TipoGuiaEnum.CONSULTA_URGENCIA;
	}
	
	@Override
	public String getTipo() {
		return "Consulta de Urgência";
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
		return true;
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
	public boolean isCompleta() {
		return true;
	}

	@Override
	public boolean isSimples() {
		return false;
	}

	@Override
	public boolean isAtendimentoUrgencia() {
		return false;
	}

	@Override
	public int getPrazoInicial() {
		return PRAZO_URGENCIA;
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
	
	@Override
	public Date getDataVencimento() {
		Calendar vencimento = Calendar.getInstance();
		vencimento.setTime(getDataAtendimento());
		vencimento.add(Calendar.DAY_OF_MONTH, PRAZO_VENCIMENTO_CONSULTAS_E_EXAMES);
		return vencimento.getTime();
	}
	
	
	public boolean isGuiaImpressaoNova(){
		return false;	
	}

	@Override
	public boolean isUrgenciaOuAtendimentoSubsequente() {
		return true;
	}

	@Override
	protected GuiaSimples<ProcedimentoInterface> newInstance() {
		return new GuiaConsultaUrgencia();
	}
	
}
