package br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos;

import java.math.BigDecimal;

import org.apache.tools.ant.types.CommandlineJava.SysProperties;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.acordos.AcordoPacoteHonorario;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * 
 * @author Dannylvan
 * 
 * Classe para gerar view para o Jheat, no fluxo de auditar guia de honorário
 *
 */
public class ItemPacoteHonorarioAuditoria extends ItemPacoteHonorario{

	private static final long serialVersionUID = 1L;

	/**
	 * Profissional transitente para inserção de honorários no fluxo de
	 * auditoria de honorários médicos
	 */
	private transient Profissional profissional;

	public Profissional getProfissional() {
		return profissional;
	}

	public void setProfissional(Profissional profissional) {
		this.profissional = profissional;
	}
	
	/**
	 * Valida se o prestador possui acordo para o pacote informado
	 * @throws ValidateException 
	 */
	public Boolean validate() throws ValidateException{
		
		Prestador prestador = ImplDAO.findById(24079243L, Prestador.class);
		
		if(prestador != null){
			for (AcordoPacoteHonorario acordo : prestador.getAcordosPacoteHonorarioAtivos()) {
				if(acordo.getPacote().equals(this.getPacote())){
					this.setValorAcordo(BigDecimal.valueOf(acordo.getValor()));
					return true;
				}
			}
			
			throw new ValidateException(MensagemErroEnum.PRESTADOR_SEM_ACORDO_ATIVO_PARA_O_PACOTE
					.getMessage(this.getPacote().getDescricao()));
		}
	    
		return true;
	}
}
