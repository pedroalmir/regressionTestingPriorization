/**
 * 
 */
package br.com.infowaypi.ecarebc.atendimentos;

import java.io.Serializable;

import br.com.infowaypi.ecare.atendimentos.TipoGuiaEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * Classe que representa guias de consulta de urgência no sistema
 * @author Marcus bOolean
 * @changes Danilo Portela
 */
@SuppressWarnings("serial")
public class GuiaConsultaOdontoUrgencia extends GuiaConsultaOdonto implements Serializable {

	public GuiaConsultaOdontoUrgencia() {
		this(null);
	}
	
	public GuiaConsultaOdontoUrgencia(UsuarioInterface usuario) {
		super(usuario);
		this.getSituacao().setDescricao(SituacaoEnum.CONFIRMADO.descricao());
		this.getSituacao().setMotivo(MotivoEnum.CONFIRMADA_NO_PRESTADOR.getMessage());
	}
	
	@Override
	public TipoGuiaEnum getTipoGuiaEnum() {
		return TipoGuiaEnum.CONSULTA_ODONTOLOGICA_URGENCIA;
	}
	
	@Override
	public String getTipo() {
		return "Consulta Odontológica de Urgência";
	}
	
	@Override
	public boolean isConsultaOdonto() {
		return false;
	}
	
	@Override
	public boolean isConsultaOdontoUrgencia() {
		return true;
	}
	
	@Override
	public boolean isUrgencia() {
		return true;
	}
	
	@Override
	public boolean isInternacao() {
		return false;
	}
	
	@Override
	public boolean isInternacaoUrgencia() {
		return false;
	}
	
	@Override
	public boolean isRegulaConsumo() {
		return true;
	}
	
	@Override
	public boolean isConsultaEletiva() {
		return false;
	}
	
	public boolean isConsulta() {
		return false;
	}
		
}
