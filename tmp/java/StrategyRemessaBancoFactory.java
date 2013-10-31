package br.com.infowaypi.ecarebc.financeiro.arquivos;

public class StrategyRemessaBancoFactory {
	
	private static StrategyRemessaBancoFactory factory = null;
	private static StrategyRemessaBancoInterface strategy_Default = null;
	private static StrategyRemessaBancoInterface strategy_Cef = null;
//	private static StrategyRemessaBancoInterface strategy_Bep = null;

	private StrategyRemessaBancoFactory(){
		strategy_Default = new StrategyRemessaBancoDefault();
		strategy_Cef = new StrategyRemessaBancoCef();
//		strategy_Bep = new StrategyRemessaBancoBep();
	}

	public static StrategyRemessaBancoInterface getInstance(int banco){
		if(factory == null)
			factory = new StrategyRemessaBancoFactory();

		if (StrategyRemessaBancoInterface.BANCO_DO_BRASIL == banco)
			return strategy_Default;
		else if (StrategyRemessaBancoInterface.CAIXA_ECONOMICA == banco)
			return strategy_Cef;
		else if (StrategyRemessaBancoInterface.BRADESCO == banco)
			return strategy_Default;
//		else if (StrategyRemessaBancoInterface.BEP == banco)
//			return strategy_Bep;

		return null;
	}
}
