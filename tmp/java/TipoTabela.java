package br.com.infowaypi.ecarebc.planos;
/**
 * @author Diogo Vinícius
 */
public enum TipoTabela {

	TIPO_A {
		public int getQuantMinimaFuncionarios() {
			return QUANT_MIN_FUNC_TAB_TIPO_A;
		}
		
		public int getQuantMaximaFuncionarios() {
			return QUANT_MAX_FUNC_TAB_TIPO_A;
		}
		
		@Override
		public String getDescricaoTipo() {
			return "A";
		}
	},
	TIPO_B{
		public int getQuantMinimaFuncionarios() {
			return QUANT_MIN_FUNC_TAB_TIPO_B;
		}
		
		public int getQuantMaximaFuncionarios() {
			return QUANT_MAX_FUNC_TAB_TIPO_B;
		}
		@Override
		public String getDescricaoTipo() {
			return "B";
		}
	},
	TIPO_C{	
		
		public int getQuantMinimaFuncionarios() {
			return QUANT_MIN_FUNC_TAB_TIPO_C;
		}
	
		public int getQuantMaximaFuncionarios() {
			return Integer.MAX_VALUE;
		}
		
		@Override
		public String getDescricaoTipo() {
			return "C";
		}
	},
	TIPO_I{	
		public int getQuantMinimaFuncionarios() {
			return Integer.MIN_VALUE;
		}
	
		public int getQuantMaximaFuncionarios() {
			return Integer.MAX_VALUE;
		}
		
		@Override
		public String getDescricaoTipo() {
			return "I";
		}
	};
	
	//TODO carregar o arquivo de configuração
//	static{
//		//TODO necessário para carregar os valores das idades referentes às faixas
//		ConfigFactory.getInstance();
//	}
	
	
	protected Integer quantMinimaFuncionarios;
	protected Integer quantMaximaFuncionarios;
	public static final int NUMERO_TIPOS = values().length;
	public static final int QUANT_MIN_FUNC_TAB_TIPO_A = 0;
	public static final int QUANT_MAX_FUNC_TAB_TIPO_A = 19;
	public static final int QUANT_MAX_FUNC_TAB_TIPO_B = 20;
	public static final int QUANT_MIN_FUNC_TAB_TIPO_B = 49;
	public static final int QUANT_MIN_FUNC_TAB_TIPO_C = 50;
	

	public abstract int getQuantMinimaFuncionarios();
	
	public abstract int getQuantMaximaFuncionarios();
	
		/**
	 * @param quantMaximaFuncionarios the quantMaximaFuncionarios to set
	 */
	public void setQuantMaximaFuncionarios(Integer quantMaximaFuncionarios) {
		this.quantMaximaFuncionarios = quantMaximaFuncionarios;
	}

	/**
	 * @param quantMinimaFuncionarios the quantMinimaFuncionarios to set
	 */
	public void setQuantMinimaFuncionarios(Integer quantMinimaFuncionarios) {
		this.quantMinimaFuncionarios = quantMinimaFuncionarios;
	}

	public abstract String getDescricaoTipo();
	
	
	//TODO refazer este método
	public static TipoTabela getTipoTabela(int numeroSegurados){
		if (numeroSegurados < 0)
			throw new RuntimeException("Número de segurados inválido");
		if (numeroSegurados >= 50)
			return TIPO_C;
		else
			if (numeroSegurados <= 19)
				return TIPO_A;
				else return TIPO_B;
	}

}