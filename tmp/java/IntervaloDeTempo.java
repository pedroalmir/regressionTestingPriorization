package br.com.infowaypi.ecare.scheduller.sms;

import java.io.Serializable;

import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.painelDeControle.PainelDeControle;
import br.com.infowaypi.msr.utils.Assert;

public class IntervaloDeTempo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private Long idIntervaloDeTempo;
    private String inicioDoIntervalo;
    private String finalDoIntervalo;
    private PainelDeControle painel;
    private TipoDeMensagem tipoDeMensagem;
    
    
    @Override
    public boolean equals(Object obj) {
		if ((obj instanceof IntervaloDeTempo) && 
			((IntervaloDeTempo) obj).getFinalDoIntervalo().equals(this.finalDoIntervalo) &&
			((IntervaloDeTempo) obj).getInicioDoIntervalo().equals(this.inicioDoIntervalo) &&
			((IntervaloDeTempo) obj).getIdIntervaloDeTempo().equals(this.idIntervaloDeTempo) &&
			((IntervaloDeTempo) obj).getTipoDeMensagem().equals(this.tipoDeMensagem)){
		    return true;
		}
		return false;
    }
    
    public TipoDeMensagem getTipoDeMensagem() {
        return tipoDeMensagem;
    }
    
    public void setTipoDeMensagem(TipoDeMensagem tipoDeMensagem) {
        this.tipoDeMensagem = tipoDeMensagem;
    }
    
    public String getTipoDeMensagemDescricao() {
		if(tipoDeMensagem != null){
			return tipoDeMensagem.getDescricao();
		} 
		return null;
	}

	public void setTipoDeMensagemDescricao(String tipoDeMensagemDescricao) {
		for (TipoDeMensagem tipo : TipoDeMensagem.values()) {
			if (tipo.getDescricao().equals(tipoDeMensagemDescricao)) {
				this.tipoDeMensagem = tipo;
				break;
			}
		}
	}
	
    public PainelDeControle getPainel() {
        return painel;
    }
    
    public void setPainel(PainelDeControle painel) {
        this.painel = painel;
    }
    
    public Long getIdIntervaloDeTempo() {
        return idIntervaloDeTempo;
    }
    
    public void setIdIntervaloDeTempo(Long idIntervaloDeTempo) {
        this.idIntervaloDeTempo = idIntervaloDeTempo;
    }
    
    public String getInicioDoIntervalo() {
        return inicioDoIntervalo;
    }
    
    public void setInicioDoIntervalo(String inicioDoIntervalo) {
        this.inicioDoIntervalo = inicioDoIntervalo;
    }
    
    public String getFinalDoIntervalo() {
        return finalDoIntervalo;
    }
    
    public void setFinalDoIntervalo(String finalDoIntervalo) {
        this.finalDoIntervalo = finalDoIntervalo;
    }
    
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    
    public String toString() {
    	return inicioDoIntervalo+","+finalDoIntervalo;
    }
    
    public Boolean validate() {
	
		validateHoraMinuto(inicioDoIntervalo, finalDoIntervalo);
		
		Assert.isFalse(inicioDoIntervalo.equals(finalDoIntervalo), MensagemErroEnum.INTERVALOS_IGUAIS.getMessage());
		
		return true;
    }
    
    public int hora(String horario) {
    	return Integer.parseInt(horario.substring(0,2));
    }
    
    public int minuto(String horario) {
    	return Integer.parseInt(horario.substring(3,5));
    }
    
    public boolean entreDoisDias() {
	
		int horaInicio = hora(this.inicioDoIntervalo);
		
		int horaFim = hora(this.finalDoIntervalo);
		
		if (horaInicio > horaFim) {
		    return true;
		}
		
		return false;
    }
    
    public void inverter() {
		String inicioDoIntervaloAntigo = this.inicioDoIntervalo;
		this.inicioDoIntervalo = this.finalDoIntervalo;
		this.finalDoIntervalo = inicioDoIntervaloAntigo;
    }
    
    private void validateHoraMinuto (String... intervalos) {
	
		for (String intervalo : intervalos) {
		    
		    int hora = hora(intervalo);
		    int minuto = minuto(intervalo);
		    
		    boolean horarioInvalido = hora > 23 || hora < 0 || minuto > 59 || minuto < 0;
		    
		    Assert.isFalse(horarioInvalido, MensagemErroEnum.HORA_INVALIDA.getMessage(intervalo));
		}
    }

}
