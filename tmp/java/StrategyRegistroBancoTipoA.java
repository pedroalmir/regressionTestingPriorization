package br.com.infowaypi.ecarebc.financeiro.arquivos.strategy.tipoArquivo;


import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecarebc.financeiro.arquivos.ArquivoDeRetorno;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.financeiro.Banco;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

public class StrategyRegistroBancoTipoA implements StrategyRegistroBancoInterface {

	public void executar(String linha, ArquivoDeRetorno arquivo, UsuarioInterface usuario) throws Exception {
		setBanco(linha.substring(42, 45), arquivo);
		setDataGeracao(linha.substring(65, 73), arquivo);
		setDataProcessamento(arquivo);
		ImplDAO.save(arquivo);
	}
	
	private void setBanco(String codigoBanco, ArquivoDeRetorno arquivo) throws Exception{
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("codigoFebraban", codigoBanco));
    	
    	List<Banco> bancos = sa.list(Banco.class);
    	if (!bancos.isEmpty())
	    	for (Banco banco : bancos) {
	    		arquivo.setBanco(banco);
			}
    	else
    		throw new Exception("Nenhum Banco com o identificador: " + codigoBanco);
	}

	private void setDataGeracao(String dataGeracao, ArquivoDeRetorno arquivo) throws Exception{
		 arquivo.setDataGeracao(Utils.parse(dataGeracao, "yyyyMMdd"));
	}

	private void setDataProcessamento(ArquivoDeRetorno arquivo){
		arquivo.setDataProcessamento(new Date());
	}
}

