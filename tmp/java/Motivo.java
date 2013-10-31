package br.com.infowaypi.ecarebc.atendimentos;

import java.util.Date;

import br.com.infowaypi.msr.user.UsuarioInterface;

@SuppressWarnings({"serial", "unchecked"})
public class Motivo extends AbstractObservacao {
	
	private String fluxo;
	private GuiaSimples guiaCompleta;
	
	public Motivo(){
		super();
	}
	
	public Motivo(Date dataDeCriacao, String texto, UsuarioInterface usuario) {
		super(dataDeCriacao, texto, usuario);
	}
	
	public Motivo (String texto, UsuarioInterface usuario) {
		super(texto, usuario);
	}

	public String getFluxo() {
		return fluxo;
	}

	public void setFluxo(String fluxo) {
		this.fluxo = fluxo;
	}

	@Override
	public boolean isRegistroAuditoria() {
		return false;
	}

	@Override
	public boolean isMotivo() {
		return true;
	}

	@Override
	public boolean isObervacao() {
		return false;
	}
	
	/**
	 * Criados por conta do hibernate.
	 * Quando uma classe tem dois atributos de classes diferentes (Motivo e Observacao) 
	 * que pertencem a mesma tabela (AbstractObservacao), ele busca o objeto da classe
	 * errada (Observacao.class) lança uma org.hibernate.WrongClassException.
	 * 
	 * @param guia
	 */
	public void setGuiaCompleta(GuiaSimples guia) {
		this.guiaCompleta = guia;
	}
	
	public GuiaSimples getGuiaCompleta() {
		return this.guiaCompleta;
	}
	
	public static Motivo createMotivo(Date dataDeCriacao, String texto, UsuarioInterface usuario, String fluxo) {
		Motivo motivo = new Motivo(texto, usuario);
		
		motivo.setFluxo(fluxo);
		if (dataDeCriacao != null) {
			motivo.setDataDeCriacao(dataDeCriacao);
		}
		
		return motivo;
	}

}
