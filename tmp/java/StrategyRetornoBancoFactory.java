package br.com.infowaypi.ecarebc.financeiro.arquivos;

import br.com.infowaypi.msr.financeiro.BancoInterface;

public class StrategyRetornoBancoFactory {

	private static StrategyRetornoBancoFactory factory = null;
//	private static StrategyRetornoBancoInterface strategy_bep = null;
	private static StrategyRetornoBancoInterface strategy_bb = null;
	private static StrategyRetornoBancoInterface strategy_cef = null;
	private static StrategyRetornoBancoInterface strategy_bradesco = null;
//	private static StrategyRetornoBancoInterface strategy_bep_detalhe = null;
//	private static StrategyRetornoBancoInterface strategy_bep_trailler = null;

	private StrategyRetornoBancoFactory(){
//		strategy_bep = new StrategyRetornoBep();
		strategy_bb = new StrategyRetornoBB();
		strategy_cef = new StrategyRetornoCEF();
		strategy_bradesco = new StrategyRetornoBradesco();
	}

	public static StrategyRetornoBancoInterface getInstance(BancoInterface banco){
		if(factory == null)
			factory = new StrategyRetornoBancoFactory();

		if (StrategyRetornoBancoInterface.BANCO_DO_BRASIL == Integer.parseInt(banco.getCodigoFebraban()))
			return strategy_bb;
		else if (StrategyRetornoBancoInterface.CAIXA_ECONOMICA == Integer.parseInt(banco.getCodigoFebraban()))
			return strategy_cef;
//		else if (StrategyRetornoBancoInterface.BEP == Integer.parseInt(banco.getCodigoFebraban()))
//			return strategy_bep;
		else if (StrategyRetornoBancoInterface.BRADESCO == Integer.parseInt(banco.getCodigoFebraban()))
			return strategy_bradesco;

		return null;
	}
}
