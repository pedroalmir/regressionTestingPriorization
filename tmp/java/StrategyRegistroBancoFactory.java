package br.com.infowaypi.ecarebc.financeiro.arquivos.strategy.tipoArquivo;

import static br.com.infowaypi.ecarebc.financeiro.arquivos.strategy.tipoArquivo.StrategyRegistroBancoInterface.TIPO_A;
import static br.com.infowaypi.ecarebc.financeiro.arquivos.strategy.tipoArquivo.StrategyRegistroBancoInterface.TIPO_B;
import static br.com.infowaypi.ecarebc.financeiro.arquivos.strategy.tipoArquivo.StrategyRegistroBancoInterface.TIPO_F;
import static br.com.infowaypi.ecarebc.financeiro.arquivos.strategy.tipoArquivo.StrategyRegistroBancoInterface.TIPO_J;
import static br.com.infowaypi.ecarebc.financeiro.arquivos.strategy.tipoArquivo.StrategyRegistroBancoInterface.TIPO_Z;

public class StrategyRegistroBancoFactory {
	
	public static StrategyRegistroBancoInterface getInstance(char tipoRegistro){
		if (TIPO_A == tipoRegistro)
			return new StrategyRegistroBancoTipoA();
		else if (TIPO_B == tipoRegistro)
			return new StrategyRegistroBancoTipoB();
		else if (TIPO_F == tipoRegistro)
			return new StrategyRegistroBancoTipoF();
		else if (TIPO_Z == tipoRegistro)
			return new StrategyRegistroBancoTipoZ();
//		else if (TIPO_T == tipoRegistro)
//			return new StrategyRegistroBancoTipoT();
		else if (TIPO_J == tipoRegistro)
			return new StrategyRegistroBancoTipoZ();
		return null;
	}
}