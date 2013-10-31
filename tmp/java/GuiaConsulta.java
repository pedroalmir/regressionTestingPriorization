package br.com.infowaypi.ecarebc.atendimentos;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import br.com.infowaypi.ecare.atendimentos.TipoGuiaEnum;
import br.com.infowaypi.ecarebc.atendimentos.enums.TipoConsultaEnum;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;
 
/** 
 * Classe que representa uma guia de consulta médica
 * @author Danilo Nogueira Portela
 */
@SuppressWarnings("unchecked")
public class GuiaConsulta<P extends ProcedimentoInterface> extends GuiaSimples<Procedimento>{

	private static final long serialVersionUID = 1L;

	public GuiaConsulta() {
		this(null);
	}

	public GuiaConsulta(UsuarioInterface usuario) {
		super(usuario);
		this.mudarSituacao(usuario, SituacaoEnum.AGENDADA.descricao(), MotivoEnum.AGENDAMENTO_CONSULTA_ELETIVA.getMessage(), new Date());
	}

	/**
	 * Cria uma guia de consulta com o procedimento padrão
	 * @param guia
	 * @throws Exception 
	 */
	public void addProcedimentoConsulta(TipoConsultaEnum tipoConsulta) throws Exception {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("codigo",tipoConsulta.getCodigo()));
		List<TabelaCBHPM> procedimentoConsulta = sa.list(TabelaCBHPM.class);
		
		if(procedimentoConsulta.isEmpty()){
			throw new ValidateException(MensagemErroEnum.PROCEDIMENTO_INVALIDO_PARA_CONSULTA.getMessage(tipoConsulta.getCodigo()));
		}
		
		TabelaCBHPM tabelaCBHPM = procedimentoConsulta.get(0);
		
		Procedimento procedimento = new Procedimento();
		procedimento.setProcedimentoDaTabelaCBHPM(tabelaCBHPM);
		procedimento.getSituacao().setDescricao(SituacaoEnum.AGENDADA.descricao());
		procedimento.setGuia(this);
		procedimento.calcularCampos();
		this.addProcedimento(procedimento);
	}

	@Override
	public TipoGuiaEnum getTipoGuiaEnum() {
		return TipoGuiaEnum.CONSULTA;
	}
	
	@Override
	public String getTipo() {
		return "Consulta";
	}

	@Override
	public boolean isConsulta() {
		return true;
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
	public boolean isConsultaOdonto() {
		return false;
	}

	@Override
	public boolean isCompleta() {
		return false;
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
		return true;
	}

	@Override
	public boolean isTratamentoSeriado() {
		return false;
	}

	@Override
	public boolean isConsultaEletiva() {
		return true;
	}

	@Override
	public boolean isExameEletivo() {
		return false;
	}

//	@Override
//	public VariavelIndice getVariavelIndice() {
//		return VariavelIndice.CONSULTAS_ELETIVAS;
//	}

	@Override
	public Date getDataVencimento() {
		Calendar vencimento = Calendar.getInstance();
		vencimento.setTime(getDataAtendimento());
		vencimento.add(Calendar.DAY_OF_MONTH, PRAZO_VENCIMENTO_CONSULTAS_E_EXAMES);
		return vencimento.getTime();
	}
	
	public boolean isGuiaImpressaoNova(){
		if (this.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao())|| this.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao()))
			return true;
		return false;	
	}
	
	public int getDiferencaAgendamentoEConfirmacao() {
		Calendar dtAgendamento = GregorianCalendar.getInstance();
		Calendar dtConfirmacao = GregorianCalendar.getInstance();
		if(this.getSituacao(SituacaoEnum.AGENDADA.descricao()) != null && this.getSituacao(SituacaoEnum.CONFIRMADO.descricao()) != null) {
			dtAgendamento.setTime(this.getSituacao(SituacaoEnum.AGENDADA.descricao()).getDataSituacao());
			dtConfirmacao.setTime(this.getSituacao(SituacaoEnum.CONFIRMADO.descricao()).getDataSituacao());
			
			return Utils.diferencaEmDias(dtAgendamento, dtConfirmacao);
		}else {
			return 0;
		}
	}

	@Override
	public boolean isUrgenciaOuAtendimentoSubsequente() {
		return false;
	}

	@Override
	public final boolean isHonorarioMedico() {
		return false;
	}

	@Override
	public boolean isAcompanhamentoAnestesico() {
		return false;
	}
	
	@Override
	public boolean isGeraExameExterno() {
		return false;
	}
	
	@Override
	public boolean isGeraExameExternoParaPacientesCronicos() {
		return true;
	}

	@Override
	protected GuiaSimples<ProcedimentoInterface> newInstance() {
		return null;
	}
}
