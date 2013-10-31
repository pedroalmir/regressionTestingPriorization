package br.com.infowaypi.ecare.services.enuns;


public enum TipoLimiteEnum {

	TODOS("TODOS") {
		@Override
		public boolean isConsulta() {
			return true;
		}
		@Override
		public boolean isConsultaOdontologica() {
			return true;
		}
		@Override
		public boolean isExame() {
			return true;
		}
		@Override
		public boolean isTratamentoOdontologico() {
			return true;
		}
	},
	CONSULTA("CONSULTA") {
		@Override
		public boolean isConsulta() {
			return true;
		}
	},
	EXAME("EXAME") {
		@Override
		public boolean isExame() {
			return true;
		}
	},
	CONSULTA_ODONTOLOGICA("CONSULTA ODONTOLÓGICA") {
		@Override
		public boolean isConsultaOdontologica() {
			return true;
		}
	},
	TRATAMENTO_ODONTOLOGICO("TRATAMENTO ODONTOLÓGICO") {
		@Override
		public boolean isTratamentoOdontologico() {
			return true;
		}
	};
	
	private String tipoGuia;

	private TipoLimiteEnum(String tipoLimite) {
		this.tipoGuia = tipoLimite;
	}
	public String getTipoGuia() {
		return tipoGuia;
	}
	public void setTipoGuia(String tipoLimite) {
		this.tipoGuia = tipoLimite;
	}
	public TipoLimiteEnum getTipoLimiteEnum() {
		return this;
	}
	public boolean isConsulta() {
		return false;
	}
	public boolean isExame() {
		return false;
	}
	public boolean isConsultaOdontologica() {
		return false;
	}
	public boolean isTratamentoOdontologico() {
		return false;
	}
	
}
