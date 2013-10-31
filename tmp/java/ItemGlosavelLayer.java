package br.com.infowaypi.ecarebc.atendimentos;

import br.com.infowaypi.ecare.cadastros.MotivoGlosa;

public interface ItemGlosavelLayer {
	
	public void setJustificativaGlosa(String justificativaGlosa);
	
	public String getJustificativaGlosa();
	
	public void setMotivoGlosa(MotivoGlosa motivoGlosa);
	
	public MotivoGlosa getMotivoGlosa();
	
	public void setGlosar(boolean glosar);
	
	public boolean isGlosar();
	
}