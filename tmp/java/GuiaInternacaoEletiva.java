package br.com.infowaypi.ecarebc.atendimentos;

import java.math.BigDecimal;
import java.util.Date;

import br.com.infowaypi.ecare.atendimentos.TipoGuiaEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.MoneyCalculation;

/** 
 * Classe que  representa uma guia de internação eletiva (cirurgia)
 * @author Erick Passos
 * @changes Danilo Nogueira Portela
 */
public class GuiaInternacaoEletiva extends GuiaInternacao {

	private static final long serialVersionUID = 1L;
	public static final int PRAZO_GUIA_INTERNACAO = 60;//dias
	
	public GuiaInternacaoEletiva() {
		this(null);
	}
	
	public GuiaInternacaoEletiva(UsuarioInterface usuario){
		super(usuario);
		this.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.INTERNACAO_ELETIVA.getMessage(), new Date());
		
		//TODO Refatorar na inserção de tratamentos externos
		this.setTipoAcomodacao(GuiaCompleta.TIPO_ACOMODACAO_INTERNO);
	}
	
	@Override
	public TipoGuiaEnum getTipoGuiaEnum() {
		return TipoGuiaEnum.INTERNACAO_ELETIVA;
	}
	
	@Override
	public String getTipo() {
		return "Internação Eletiva";
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
		return true;
	}

	@Override
	public boolean isInternacaoEletiva() {
		return true;
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
		return PRAZO_2DIAS;
	}

	@Override
	public boolean isUrgencia() {
		return false;
	}
	
	@Override
	public BigDecimal getValorCoParticipacao() {
		return MoneyCalculation.rounded(BigDecimal.ZERO);
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
	public boolean isUrgenciaOuAtendimentoSubsequente() {
		return false;
	}

	@Override
	protected GuiaSimples<ProcedimentoInterface> newInstance() {
		return new GuiaInternacaoEletiva();
	}
	
}