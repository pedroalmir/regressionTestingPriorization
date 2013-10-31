package br.com.infowaypi.ecare.financeiro.boletos;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.hibernate.FlushMode;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecare.constantes.Constantes;
import br.com.infowaypi.ecare.financeiro.Cobranca;
import br.com.infowaypi.ecare.segurados.DependenteSR;
import br.com.infowaypi.ecare.segurados.TitularFinanceiroSR;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.CobrancaBC;
import br.com.infowaypi.ecarebc.financeiro.arquivos.ArquivoDeRetorno;
import br.com.infowaypi.ecarebc.financeiro.conta.Conta;
import br.com.infowaypi.ecarebc.financeiro.conta.ContaInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * Service do mapeamento <b>ProcessarRetorno.jhm.xml</b> responsável por <br>
 * dar baixa nos boletos através de arquivo de retorno enviado pela Caixa.
 * 
 * @author Diogo Vinícius
 *
 */
public class ProcessaRetornoBoletosService {

	public static final String PROCESSAMENTO_AUTOMATICO_RETORNO_BOLETO = "Processamento automático de retorno de boletos"; 
	private static Calendar calendar;
	
	/**
	 * Método do 1º step-method
	 * @param conteudo
	 * @return
	 * @throws Exception
	 */
	public ArquivoDeRetorno processarRetorno(byte[] conteudo) throws Exception {
		
		if(conteudo.length == 0) {
			throw new ValidateException("Selecione um arquivo!");
		}
		
		Scanner sc = new Scanner(new ByteArrayInputStream(conteudo));
		ArquivoDeRetorno arquivo = new ArquivoDeRetorno();
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
		HibernateUtil.currentSession().flush();
		try {
			readAllFile(sc, arquivo);
		} catch (StringIndexOutOfBoundsException e) {
			throw new ValidateException("O arquivo é inválido!");
		}
		
		arquivo.setDataProcessamento(new Date());
		arquivo.setArquivo(conteudo);
		arquivo.setStatusArquivo('R');
		
		return arquivo;
	}

	/**
	 * Método do 2º step-method
	 * @param arquivo
	 * @return
	 * @throws Exception
	 */
	public ArquivoDeRetorno salvar(ArquivoDeRetorno arquivo) throws Exception{
		ImplDAO.save(arquivo);
		for (ContaInterface conta : arquivo.getContas()) {
			ImplDAO.save(conta);
		}
		
		for (CobrancaBC cobranca : arquivo.getCobrancas()) {
			ImplDAO.save(cobranca);
		}
		
		return arquivo;
	}
	
	private void readAllFile(Scanner sc, ArquivoDeRetorno arquivo) throws ValidateException, Exception {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("situacao.descricao", SituacaoEnum.ABERTO.descricao()));
		List<Cobranca> cobrancas = sa.list(Cobranca.class);
		
		String registro;
		StringBuilder registrosNaoEncontrados = new StringBuilder();
		
		while(sc.hasNext()) {
			registro = sc.nextLine();
			
			if (registro.charAt(7) == '0') {
				readHeaderFile(registro,arquivo);
			} else if (registro.charAt(7) == '1') {
				readHeaderLote(registro,arquivo);
			} else if ((registro.charAt(7) == '3') && (registro.charAt(13) == 'T')) {
				String registroT = registro;
				String registroU = sc.nextLine();
				readSegmentosTeU(registroT, registroU, arquivo, cobrancas,registrosNaoEncontrados);
			} else if (registro.charAt(7) == '5') {
				readTraillerLote(registro,arquivo);
			} else if (registro.charAt(7) == '9') {
				readTraillerFile(registro,arquivo);
			} else { 
				throw new ValidateException("Este arquivo não é um Arquivo de Retorno válido!");
			}
		}
		
		arquivo.setBoletosNaoEncontrados(registrosNaoEncontrados.toString().getBytes());
		System.out.println(registrosNaoEncontrados);
		System.out.println(arquivo.getBoletosNaoEncontrados());
		
		//Caso o arquivo esteja vazio, mostra no fluxo uma mensagem de alerta.
		arquivo.setVazio(arquivo.getContas().isEmpty());
	}

	private void readHeaderFile(String registro, ArquivoDeRetorno arqRetorno) throws ValidateException{
		
		boolean isSaudeRecife = registro.substring(72, 102).equals("AUT MUN PREV SAUDE SERV RPPS  ");
		boolean isCaixa       = registro.substring(0,3).equals("104");
		boolean isRetorno     = registro.charAt(142) == '2';
		
		if (!isSaudeRecife) {
			throw new ValidateException("Este arquivo não é do Saúde Recife!");
		}
		if (!isCaixa) {
			throw new ValidateException("Este arquivo não é da Caixa!");
		}
		if (!isRetorno) {
			throw new ValidateException("Este arquivo não é de Retorno de Boletos!");
		}
		
		Integer sequencial = Integer.parseInt(registro.substring(157,163));

		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("sequencial", sequencial));
		if(sa.resultCount(ArquivoDeRetorno.class) != 0) {
			throw new ValidateException("O arquivo de sequencial "+sequencial+" já foi processado.");
		}
		
		int sequencialAnterior = sequencial - 1;
		SearchAgent buscaArquivoAnterior = new SearchAgent();
		buscaArquivoAnterior.addParameter(new Equals("sequencial", sequencialAnterior));
		
		if(buscaArquivoAnterior.resultCount(ArquivoDeRetorno.class) == 0) {
	//		throw new ValidateException("O arquivo de sequencial "+sequencialAnterior+" ainda não foi processado.");
		}
		
		arqRetorno.setSequencial(sequencial);
		arqRetorno.setTipoPagamento(Constantes.BOLETO);
	}
	
	private void readHeaderLote(String registro, ArquivoDeRetorno arqRetorno){}
	
	/**
	 * Método que reativa os segurados suspensos por cobrança.
	 * 
	 * @param registroT
	 * @param registroU
	 * @param arqRetorno
	 * @param cobrancas
	 * @param registrosNaoEncontrados
	 * @throws Exception
	 */
	private void readSegmentosTeU(String registroT, String registroU, ArquivoDeRetorno arqRetorno, List<Cobranca> cobrancas,StringBuilder registrosNaoEncontrados) throws Exception{
		
		String codigoDeMovimento 		= registroT.substring(15, 17);
		String modalidadeNossoNumero 	= registroT.substring(39, 41);
		String nossoNumero       		= registroT.substring(41, 56);
		
		boolean isPaga 				= codigoDeMovimento.equals("06"); 
		String dataPagamento     	= registroU.substring(137, 145);
		String valorPago         	= registroU.substring(77, 92);
		String jurosMultaEncargos	= registroU.substring(18, 32);
		
		Cobranca cobranca = null;
		if(!modalidadeNossoNumero.equals("11")) {
			cobranca = getCobranca(nossoNumero, cobrancas);
		}
		
		if (isPaga) {
			if (cobranca != null) {
				Date dataPagamentoDate = getData(dataPagamento);
				for (ContaInterface conta : cobranca.getColecaoContas().getContas()) {
					if (conta.getArquivoRetorno() != null) {
						throw new RuntimeException("Este arquivo já foi processado!");
					}
					
					Conta novaConta = (Conta)conta;
					novaConta.mudarSituacao(null, SituacaoEnum.PAGO.descricao(), PROCESSAMENTO_AUTOMATICO_RETORNO_BOLETO, new Date());
					novaConta.setValorPago(getValorMonetario(valorPago));
					novaConta.setDataPagamento(dataPagamentoDate);
					novaConta.setValorJurosMultaEncargosPago(getValorMonetario(jurosMultaEncargos));
					arqRetorno.getContas().add(novaConta);
					novaConta.setArquivoRetorno(arqRetorno);
					arqRetorno.setValorContas(arqRetorno.getValorContas() + novaConta.getValorPago().floatValue());
					arqRetorno.setQuantidadeContas(arqRetorno.getQuantidadeContas() + 1);
				}
				
				cobranca.mudarSituacao(null, SituacaoEnum.PAGO.descricao(), PROCESSAMENTO_AUTOMATICO_RETORNO_BOLETO, new Date());
				cobranca.setValorPago(getValorMonetario(valorPago));
				cobranca.setDataPagamento(dataPagamentoDate);
				cobranca.setValorJurosMultaEncargosPago(getValorMonetario(jurosMultaEncargos));
				
				TitularFinanceiroSR titular = cobranca.getTitular();
				if(titular.isSituacaoAtual(SituacaoEnum.SUSPENSO.descricao())){
					titular.reativarTitularEDependente(null,dataPagamentoDate, PROCESSAMENTO_AUTOMATICO_RETORNO_BOLETO, new Date());
				}
				
				Set<DependenteSR> dependentes = titular.getDependentes();
				if(titular.getInicioDaCarencia() == null){
					titular.updateDataInicioCarencia();
					for (DependenteSR dependente : dependentes) {
						dependente.updateDataInicioCarencia();
					}
				} else {
					for (DependenteSR dependente : dependentes) {
						if(dependente.getInicioDaCarencia() == null){
							dependente.updateDataInicioCarencia();
						}
					}
				}
				
				tocarObjetos(cobranca);
				arqRetorno.getCobrancas().add(cobranca);
			} else {
				registrosNaoEncontrados.append(registroT)
										.append(System.getProperty("line.separator"))
										.append(registroU)
										.append(System.getProperty("line.separator"));
			}
		}
	}


	private void readTraillerLote(String registro, ArquivoDeRetorno arqRetorno){}
	
	private void readTraillerFile(String registro, ArquivoDeRetorno arqRetorno){}

	private Date getData(String data){
		if (calendar == null) {
			calendar = new GregorianCalendar();
		}
		calendar.set(Integer.parseInt(data.substring(4,8)),Integer.parseInt(data.substring(2,4))-1,Integer.parseInt(data.substring(0,2)));
		return calendar.getTime();	
	}
	
	private BigDecimal getValorMonetario(String valor){
		int pos = valor.length() - 2;
		return new BigDecimal(valor.substring(0,pos)+"."+valor.substring(pos,valor.length()));
	}
	
	private Cobranca getCobranca(String nossoNumero, List<Cobranca> cobrancas) {
		
		Long nossoNumeroLong = new Long(nossoNumero);
		
		for (Cobranca cobranca : cobrancas) {
			for (ContaInterface conta : cobranca.getColecaoContas().getContas()) {
				if (conta.getNossoNumero() != null && new Long(conta.getNossoNumero()).equals(nossoNumeroLong)) { 
					return cobranca;
				}
			}
		}
		return null;
	}
	
	public void tocarObjetos(Cobranca cobranca){
		cobranca.getTitular().getConsultasPromocionais().size();
		for (DependenteSR dependente : cobranca.getTitular().getDependentes()) {
			dependente.getConsultasPromocionais().size();
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		System.out.println("INICIOU");
		
		Transaction t = HibernateUtil.currentSession().beginTransaction();
		ArquivoDeRetorno arquivo = (ArquivoDeRetorno) HibernateUtil.currentSession().createCriteria(ArquivoDeRetorno.class)
																	.add(Expression.eq("sequencial", 350))
																	.uniqueResult();
		
		System.out.println("INICIOU 2 ");
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("situacao.descricao", SituacaoEnum.ABERTO.descricao()));
		List<Cobranca> cobrancas = sa.list(Cobranca.class);
		
		Scanner sc = new Scanner(new ByteArrayInputStream(arquivo.getBoletosNaoEncontrados()));
		String registro;
		StringBuilder registrosNaoEncontrados = new StringBuilder();
		
		ProcessaRetornoBoletosService service = new ProcessaRetornoBoletosService();
		
		while(sc.hasNext()) {
			registro = sc.nextLine();
			if ((registro.charAt(7) == '3') && (registro.charAt(13) == 'T')) {
				String registroT = registro;
				String registroU = sc.nextLine();
				service.readSegmentosTeU(registroT, registroU, arquivo, cobrancas, registrosNaoEncontrados);
			}
		}
		
		arquivo.setBoletosNaoEncontrados(registrosNaoEncontrados.toString().getBytes());
		service.salvar(arquivo);
		System.out.println(registrosNaoEncontrados);
		t.commit();
	}
}
