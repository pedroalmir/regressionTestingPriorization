package br.com.infowaypi.ecarebc.atendimentos;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import br.com.infowaypi.ecare.atendimentos.TipoGuiaEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ItemGuiaFaturamento;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;

@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class GuiaAcompanhamentoAnestesico extends GuiaSimples implements GuiaFechavel{

	/**
	 * Atributo transiente usado no fluxo de Autorizar Acompanhamento
	 * Anestésico.
	 */
	private boolean autorizado;

	public GuiaAcompanhamentoAnestesico() {
		this(null);
	}

	public GuiaAcompanhamentoAnestesico(UsuarioInterface usuario) {
		super(usuario);
	}

	@Override
	public Date getDataVencimento() {
		return null;
	}

	@Override
	public TipoGuiaEnum getTipoGuiaEnum() {
		return TipoGuiaEnum.ACOMPANHAMENTO_ANESTESICO;
	}

	@Override
	public String getTipo() {
		return "Acompanhamento Anestésico";
	}

	@Override
	public boolean isAtendimentoUrgencia() {
		return false;
	}

	@Override
	public boolean isCirurgiaOdonto() {
		return false;
	}

	@Override
	public boolean isCompleta() {
		return false;
	}

	@Override
	public boolean isConsulta() {
		return false;
	}

	@Override
	public boolean isConsultaEletiva() {
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
	public boolean isExameEletivo() {
		return false;
	}

	@Override
	public boolean isExameOdonto() {
		return false;
	}

	@Override
	public boolean isHonorarioMedico() {
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
	public boolean isRegulaConsumo() {
		return false;
	}

	@Override
	public boolean isSimples() {
		return false;
	}

	@Override
	public boolean isTratamentoSeriado() {
		return false;
	}

	@Override
	public boolean isUrgencia() {
		return false;
	}

	@Override
	public boolean isUrgenciaOuAtendimentoSubsequente() {
		return false;
	}

	@Override
	public boolean isAcompanhamentoAnestesico() {
		return true;
	}

	public boolean isAutorizado() {
		return autorizado;
	}

	public void setAutorizado(boolean autorizado) {
		this.autorizado = autorizado;
	}

	/**
	 * Apenas para o fluxo de fechamento, reutilizado um atributo transiente.
	 * 
	 * @return
	 */
	public boolean isFechado() {
		return this.autorizado;
	}

	public void setFechado(boolean fechado) {
		this.autorizado = fechado;
	}
	@Override
	protected GuiaSimples newInstance() {
		return new GuiaAcompanhamentoAnestesico();
	}
	
	@Override
	public void updateValorCoparticipacao() {
		super.updateValorCoparticipacao();
		if(isZeraCoparticipacao()) {
			this.setValorCoParticipacao(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP));
		}
	}
	
	@Override
	public boolean isGuiaImpressaoNova(){
		return true;
	}

	public boolean isZeraCoparticipacao() {
		boolean guiaOrigemIsInternacao = this.getGuiaOrigem().isInternacao();
		boolean isExameOriginadoDeInternacao = this.getGuiaOrigem().getGuiaOrigem() == null? false : this.getGuiaOrigem().getGuiaOrigem().isInternacao();
		boolean guiaOrigemIsExameOriginadoDeInternacao = this.getGuiaOrigem().isExame() && isExameOriginadoDeInternacao;

		return guiaOrigemIsInternacao || guiaOrigemIsExameOriginadoDeInternacao;
	}
	
	public Date getDataInicioPrazoRecebimento(){
		SituacaoInterface situacao;
		if (this.isSituacaoAtual(SituacaoEnum.ABERTO.descricao())){
			situacao = this.getSituacao(SituacaoEnum.ABERTO.descricao());
		} else {
			situacao = this.getSituacao(SituacaoEnum.AUTORIZADO.descricao());
		}
		
		if(situacao != null){
			return situacao.getDataSituacao();
		}
		
		return null;
	}

	@Override
	public boolean isRecursoGlosa() {
		// TODO Auto-generated method stub
		return false;
	}

}
