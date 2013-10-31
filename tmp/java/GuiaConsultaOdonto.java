package br.com.infowaypi.ecarebc.atendimentos;

import java.io.Serializable;

import br.com.infowaypi.ecare.atendimentos.TipoGuiaEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOdonto;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * Classe que representa uma guia de consulta odontológica
 * 
 * <br>Exemplo de uso:
 * 
 * <pre>
 *    
 *
 * </pre>
 * 
 * <br>Limitações:
 * 
 * @author Danilo Nogueira Portela
 * @version 
 * @see
 */
@SuppressWarnings("rawtypes")
public class GuiaConsultaOdonto extends GuiaConsulta<ProcedimentoOdonto> implements Serializable{

	private static final long serialVersionUID = 1L;
	
	//Constantes
	public static final long ID_PROCEDIMENTO_PADRAO_CONSULTA_ODONTO = 4000L;

	public GuiaConsultaOdonto() {
		this(null);
	}
	
	public GuiaConsultaOdonto(UsuarioInterface usuario) {
		super(usuario);
		
		this.getSituacao().setDescricao(SituacaoEnum.AGENDADA.descricao());
		this.getSituacao().setMotivo(MotivoEnum.AGENDADA_NO_PRESTADOR.getMessage());
	}
	
	@SuppressWarnings("static-access")
	public Long getIdProcedimentoPadrao(){
		return this.ID_PROCEDIMENTO_PADRAO_CONSULTA_ODONTO;
	}
	
	@Override
	public TipoGuiaEnum getTipoGuiaEnum() {
		return TipoGuiaEnum.CONSULTA_ODONTOLOGIA;
	}
	
	@Override
	public String getTipo() {
		return "Consulta Odontológica";
	}
	
	@Override
	public boolean isConsultaOdonto() {
		return true;
	}

	@Override
	public boolean isConsultaEletiva() {
		return false;
	}
	
	public boolean isConsultaOdontoNormal() {
		for (Procedimento procedimento : this.getProcedimentos()) {
			if(procedimento.getProcedimentoDaTabelaCBHPM().getCodigo().equals(PROCEDIMENTO_CONSULTA_ODONTOLOGICA)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isConsultaOdontoClinicoPromotor() {
		for (Procedimento procedimento : this.getProcedimentos()) {
			if(procedimento.getProcedimentoDaTabelaCBHPM().getCodigo().equals(PROCEDIMENTO_CONSULTA_INICIAL_CLINICO_PROMOTOR)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isConsultaOdontoPericiaInicial() {
		for (Procedimento procedimento : this.getProcedimentos()) {
			if(procedimento.getProcedimentoDaTabelaCBHPM().getCodigo().equals(PROCEDIMENTO_CONSULTA_ODONTOLOGICA_INICIAL)) {
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean isGeraExameExterno() {
		return false;
	}
	
	@Override
	public boolean isGeraExameExternoParaPacientesCronicos() {
		return false;
	}
}
