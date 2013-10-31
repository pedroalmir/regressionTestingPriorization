package br.com.infowaypi.ecarebc.planos;

import java.io.Serializable;


public class Carencia implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int consultaExame;
	private int ultrassonSimples;
	private int internacao;
	private int cirurgia;
	private int exameEspecial;
	private int parto;
	
	public enum TipoCarencia {
		CARENCIACONSULTAEXAME(1),
		CARENCIAULTRASSONSIMPLES(2),
		CARENCIAINTERNACAO(3),
		CARENCIACIRURGIA(4),
		CARENCIAEXAMEESPECIAL(5),
		CARENCIAPARTO(6);
		
		private int tipo;
		
		TipoCarencia(int tipoCarencia){
			this.tipo = tipoCarencia;
		}
		
		public int getTipo(){
			return this.tipo;
		}
	};

	public int getCirurgia() {
		return cirurgia;
	}

	public void setCirurgia(int cirurgia) {
		this.cirurgia = cirurgia;
	}

	public int getConsultaExame() {
		return consultaExame;
	}

	public void setConsultaExame(int consultaExame) {
		this.consultaExame = consultaExame;
	}

	public int getExameEspecial() {
		return exameEspecial;
	}

	public void setExameEspecial(int exameEspecial) {
		this.exameEspecial = exameEspecial;
	}

	public int getInternacao() {
		return internacao;
	}

	public void setInternacao(int internacao) {
		this.internacao = internacao;
	}

	public int getParto() {
		return parto;
	}
	
	public void setParto(int parto) {
		this.parto = parto;
	}

	public int getUltrassonSimples() {
		return ultrassonSimples;
	}

	public void setUltrassonSimples(int ultrassonSimples) {
		this.ultrassonSimples = ultrassonSimples;
	}
	
	public int getCarencia(TipoCarencia tipo){
		switch(tipo){
			case CARENCIACIRURGIA : return this.cirurgia;
			case CARENCIACONSULTAEXAME : return this.consultaExame;
			case CARENCIAEXAMEESPECIAL : return this.exameEspecial;
			case CARENCIAINTERNACAO : return this.internacao;
			case CARENCIAPARTO : return this.parto;
			case CARENCIAULTRASSONSIMPLES : return this.ultrassonSimples;
			default : return 0;
		}
	}
	
	

}
