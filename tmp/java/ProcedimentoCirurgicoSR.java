package br.com.infowaypi.ecare.procedimentos;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.com.infowaypi.ecarebc.atendimentos.ItemGlosavel;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;

/**
 * Classe que representa um procedimento cirúrgico no Saúde Recife
 * @author root
 * @changes Danilo Nogueira Portela
 */
public class ProcedimentoCirurgicoSR extends ProcedimentoCirurgico{
	
	private static final long serialVersionUID = 1L;
	
	public ProcedimentoCirurgicoSR() {
		
	}
	

	@Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ProcedimentoInterface)) {
            return false;
        }

        ProcedimentoInterface procedimento = (ProcedimentoInterface) obj;

        EqualsBuilder equalsBuilder = new EqualsBuilder()
				            .append(this.getProcedimentoDaTabelaCBHPM(), procedimento.getProcedimentoDaTabelaCBHPM())
				            .append(this.getGuia(), procedimento.getGuia())
				            .append(this.getPorcentagem(), procedimento.getPorcentagem());
       
        if(this.getIdProcedimento() != null) {
            equalsBuilder.append(this.getIdProcedimento(), procedimento.getIdProcedimento());
        } else {
        	equalsBuilder.append(this.getSituacao(1).getDataSituacao(), procedimento.getSituacao(1).getDataSituacao());
        }
       
        return equalsBuilder.isEquals();
    }
	
	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder()
			.append(this.getProcedimentoDaTabelaCBHPM())
			.append(this.getGuia())
			.append(this.getPorcentagem());
		if (this.getIdProcedimento() != null) {
			builder.append(this.getIdProcedimento());
		} else {
			builder.append(this.getSituacao(1).getDataSituacao());
		}
		return builder.toHashCode();
	}
	
	@Override
	public ItemGlosavel clone() {
		return (ProcedimentoCirurgicoSR)super.clone();
	}
	
	@Override
	public ProcedimentoInterface newInstance(){
		return new ProcedimentoCirurgicoSR();
	}
}