package br.com.infowaypi.ecare.scheduller.sms;

public enum TipoDeMensagem {
    
    EMAIL("E-mail"),
    SMS("SMS");
    
    private String descricao;
	
    TipoDeMensagem(String descricao){
		this.descricao = descricao;
	}
	
	public String descricao(){
		return descricao;
	}
	
	public String getDescricao() {
	    return descricao;
	}
	
	public static TipoDeMensagem getEnum(String descricao){
	    TipoDeMensagem[] tipos = TipoDeMensagem.values();
		for (TipoDeMensagem tipo : tipos) {
			if (tipo.descricao == descricao) {
				return tipo;
			}
		}
		return null;
	}
	
}
