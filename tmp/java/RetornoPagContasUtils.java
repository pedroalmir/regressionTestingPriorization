package br.com.infowaypi.ecare.financeiro;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

import org.hibernate.Transaction;

import br.com.infowaypi.ecare.constantes.Constantes;
import br.com.infowaypi.ecarebc.financeiro.arquivos.ArquivoDeRetorno;
import br.com.infowaypi.ecarebc.financeiro.conta.Conta;
import br.com.infowaypi.ecarebc.financeiro.conta.ContaInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.SearchAgent;

public class RetornoPagContasUtils {
	
//	private static String PATH = "/home/josino/Desktop/RetornosPgContas_25102006/PC231006.RET";
//	private static String PATH = "/home/josino/Desktop/RetornosPgContas_25102006/PC171006.RET";
//	private static String PATH = "/home/josino/Desktop/RetornosPgContas_25102006/PC181006.RET";
	private static String PATH = "/home/josino/Desktop/RetornosPgContas_25102006/PC201006.RET";
	
	private static Calendar calendar;
	
	public static Long getNossoNumero(String codigoDeBarras){
		Long id = Long.parseLong(codigoDeBarras.substring(24,44));
		return id;
	}
	
	public static void getBoletos() throws FileNotFoundException, Exception{
		Scanner in = new Scanner(new FileReader(PATH));
		ArquivoDeRetorno arqRetorno = new ArquivoDeRetorno();
		arqRetorno.setArquivo(getConteudo(PATH));
		String registro;
		while(in.hasNext()){
			registro = in.nextLine();
			if (registro.charAt(0)=='A')
				readHeader(registro,arqRetorno);
			else if (registro.charAt(0)=='G')
				readRegistro(registro,arqRetorno);
			else if (registro.charAt(0)=='Z')
				readTrailler(registro,arqRetorno);
		}
		HibernateUtil.currentSession().save(arqRetorno);
//		return null;
	}
	
	public static void readRegistro(String registro, ArquivoDeRetorno arqRetorno){
		Transaction tx = HibernateUtil.currentSession().beginTransaction();
		SearchAgent sa = new SearchAgent();
		ContaInterface conta =  (ContaInterface) sa.findById(getNossoNumero(registro.substring(37,81)),Conta.class);
		conta.setValorPago(new BigDecimal(getValorMonetario(registro.substring(81,93))));
		
		System.out.println("___________________");
		System.out.println("descricao ="+ conta.getSituacao().getDescricao());
		System.out.println("Motivo ="+conta.getSituacao().getMotivo());
		System.out.println("Conta = "+conta.getIdConta());
		System.out.println("valorCobrado ="+conta.getValorCobrado());
		System.out.println("valorPago="+conta.getValorPago());
		if(conta.getValorCobrado() == conta.getValorPago()){
			conta.mudarSituacao(null,Constantes.SITUACAO_PAGO,"Pagamento da 1ª parcela", new Date());
			System.out.println("Ativou ");
		}
			
		HibernateUtil.currentSession().update(conta);
		tx.commit();
		
		System.out.println("Código do registro: "+registro.substring(0,1));
		System.out.println("identificacao da agencia: "+registro.substring(1,21));
		System.out.println("Data pagamento: "+registro.substring(21,29));
		System.out.println("Data Crédito: "+registro.substring(29,37));
		System.out.println("Código do barras: "+registro.substring(37,81));
		System.out.println("nosso numero: "+getNossoNumero(registro.substring(37,81)));
		System.out.println("Valor recebido: "+registro.substring(81,93));
		System.out.println("Valor da tarifa: "+registro.substring(93,100));
		System.out.println("NSR: "+registro.substring(100,108));
		System.out.println("Código da agencia arrecadadora: "+registro.substring(108,116));
		System.out.println("Forma de arrecadação: "+registro.substring(116,117));
		System.out.println("codigo de transacao: "+registro.substring(117,140));
		System.out.println("Forma de pagamento: "+registro.substring(140,141));
		System.out.println("Reserva para o futuro: "+registro.substring(141,150));
		

	}
	
	public static void readHeader(String header, ArquivoDeRetorno arqRetorno) throws ParseException{

		arqRetorno.setDataGeracao(getData(header.substring(65,73)));
		arqRetorno.setDataProcessamento(new Date());
		
		System.out.println("Código do registro: "+header.substring(0,1));
		System.out.println("código de remessa: "+header.substring(1,2));
		System.out.println("código de convênio: "+header.substring(2,22));
		System.out.println("Nome da empresa: "+header.substring(22,42));
		System.out.println("Código de banco: "+header.substring(42,45));
		System.out.println("Nome do banco: "+header.substring(45,65));
//		System.out.println("Data da geracao do arquivo: "+header.substring(73,80));
		System.out.println("NSA: "+header.substring(73,80));
		System.out.println("versao do layout: "+header.substring(80,81));
		System.out.println("reservado para o futuro: "+header.substring(81,150));
		System.out.println();
	}
	
	public static void readTrailler(String trailler, ArquivoDeRetorno arqRetorno){
		System.out.println("________________________________________");
		System.out.println(" TRAILLER ");
		System.out.println();
		System.out.println("Código do registro: "+trailler.substring(0,1));
		System.out.println("Total de registros do arquivo: "+trailler.substring(1,7));
		System.out.println("valor total recebido dos registros do arquivo: "+getValorMonetario(trailler.substring(8,25).trim()));
		System.out.println("valor total recebido dos registros do arquivo: "+trailler.substring(8,25));
		System.out.println("Reservado para o futuro: "+trailler.substring(25,150));
		System.out.println();
	}
	
	public static void main(String[] args) throws Exception {
		getBoletos();
	}
	
	
	//UTEIS
	
	private static Date getData(String data){
		if (calendar == null)
		calendar = new GregorianCalendar();
		calendar.set(Integer.parseInt(data.substring(0,4)),Integer.parseInt(data.substring(4,6))-1,Integer.parseInt(data.substring(6,8)));
		return calendar.getTime();	
	}
	
	
	private static Float getValorMonetario(String valor){
		int pos = valor.length() - 2;
		Float f = new Float(valor.substring(0,pos)+"."+valor.substring(pos,valor.length()));
		return f;
	}
	
	
	public static byte[] getConteudo(String url){
		byte[] conteudo = null;
		try {
			FileInputStream file = new FileInputStream(url);
			conteudo = new byte[file.available()];
			file.read(conteudo);
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conteudo;
	}
	
}
