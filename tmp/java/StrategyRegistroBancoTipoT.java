package br.com.infowaypi.ecarebc.financeiro.arquivos.strategy.tipoArquivo;
//Retorno para tipo boleto

//package br.com.infowaypi.ecare.financeiro.arquivos.strategy.tipoArquivo;
//
//import static br.com.infowaypi.ehealth.factorys.EhealthFactory.BANCO;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.List;
//
//import org.apache.commons.lang.StringUtils;
//
//import br.com.infowaypi.ehealth.boleto.BoletoBO;
//import br.com.infowaypi.ehealth.boleto.BoletoInterface;
//import br.com.infowaypi.ehealth.core.BancoInterface;
//import br.com.infowaypi.ehealth.exceptions.ObjetoInexistenteException;
//import br.com.infowaypi.ehealth.factorys.EhealthFactory;
//import br.com.infowaypi.molecular.MolecularFactory;
//import br.com.infowaypi.molecular.Parameter;
//import br.com.infowaypi.molecular.ParameterInterface;
//import br.com.infowaypi.molecular.SearchAgentInterface;
//import br.com.infowaypi.msr.user.UsuarioInterface;
//
//public class StrategyRegistroBancoTipoT implements StrategyRegistroBancoInterface {
//
//	public void executar(String linha, ArquivoInterface arquivo, UsuarioInterface usuario) throws Exception {
//		
//		arquivo.setDataGeracao(new Date());
//		arquivo.setDataCriacao(new Date());
//		this.inserirBancoNoArquivo(arquivo);
//		
//		String nossoNumero = linha.substring(40, 56);
//		boolean isPago = (linha.substring(15, 17).equals(StrategyRegistroBancoInterface.MOVIMENTO_BOLETO_PAGO));
//
//		SearchAgentInterface searchAgent = MolecularFactory.getSearchAgent();
//		searchAgent.addParameter("nossoNumero", nossoNumero, "equals");
//		Iterator boletos = searchAgent.find(BoletoBO.class); 
//		
//		if(!boletos.hasNext())
//			throw new ObjetoInexistenteException("Nenhum boleto encontrato com esse Nosso Número: " + StringUtils.substring(nossoNumero, 1,nossoNumero.length()));
//		
//		BoletoInterface boletoEncontrado = (BoletoInterface) boletos.next();
//		boletoEncontrado.setValorPago(boletoEncontrado.getValorDoTituloFormatado());
//		
//		if(boletoEncontrado.getArquivoRetorno() != null){
//			return;
//		}
//		
//		if(boletoEncontrado != null && isPago)
//			boletoEncontrado.mudarSituacao(usuario, BoletoInterface.PAGO, "Processamento do arquivo de retorno.", new Date());
//		else if(boletoEncontrado != null)
//			boletoEncontrado.mudarSituacao(usuario, BoletoInterface.INADIMPLENTE, "Processamento do arquivo de retorno.", new Date());
//
//		boletoEncontrado.setArquivoRetorno(arquivo);
//		boletoEncontrado.save();
//		arquivo.getBoletos().add(boletoEncontrado);
//	}
//	
//	private void inserirBancoNoArquivo(ArquivoInterface arquivo) throws Exception {
//    	List<ParameterInterface> parametros = new ArrayList<ParameterInterface>();
//    	parametros.add(new Parameter("codigoFebraban", "104"));
//    	BancoInterface banco = (BancoInterface)EhealthFactory.getInstance(BANCO);
//    	Iterator<BancoInterface> bancos = banco.findByParam(parametros.iterator());
//    	if(bancos != null && bancos.hasNext()){
//    		banco = bancos.next();
//    		arquivo.setBanco(banco);
//    		arquivo.save();
//    	}
//    	else
//    		throw new ObjetoInexistenteException("Nenhum Banco encontrado.");
//	}
//	
//}