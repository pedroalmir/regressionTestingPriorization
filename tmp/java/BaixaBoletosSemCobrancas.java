package br.com.infowaypi.ecare.financeiro.boletos;

import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecare.constantes.Constantes;
import br.com.infowaypi.ecare.financeiro.Cobranca;
import br.com.infowaypi.ecare.financeiro.CobrancaInterface;
import br.com.infowaypi.ecare.segurados.Dependente;
import br.com.infowaypi.ecare.segurados.Pensionista;
import br.com.infowaypi.ecare.segurados.TitularFinanceiroSR;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.conta.Conta;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.TransactionManagerHibernate;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.utils.Utils;

public class BaixaBoletosSemCobrancas {
	
	private static String DATA_VENCIMENTO = "10/11/2007";
	private static String DATA_PAGAMENTO = "10/11/2007";
	private static String COMPETENCIA = "01/11/2007";

	public static void main(String[] args) throws Exception {
//		fromFilePDF();
		justNamesAndValuesPayers();
	}

	private static void justNamesAndValuesPayers() throws Exception{
		TransactionManagerHibernate tm = new TransactionManagerHibernate();
		tm.beginTransaction();
		
		Scanner sc = new Scanner(new FileReader("C:\\DIOGO\\Eclipse\\Workspace Diogo\\ecareRecife\\arquivos\\financeiro\\RelacaoDeTitulosPagosNovembro.csv"));
		StringTokenizer st;
		String nome, cpf, valor;
		int achados = 0;
		List<String> seguradosNaoAchados = new ArrayList<String>();
		List<AbstractSegurado> seguradosAchadosMaisDeUm = new ArrayList<AbstractSegurado>();
		Set<AbstractSegurado> suplementares = new HashSet<AbstractSegurado>();
		SearchAgent sa = new SearchAgent();
		
		while(sc.hasNext()){
			st    = new StringTokenizer(sc.nextLine(),";");
			nome  = st.nextToken();
			cpf   = st.nextToken();
			valor = st.nextToken();
			
			sa.clearAllParameters();
			sa.addParameter(new Equals("pessoaFisica.cpf", cpf.trim()));
			List<AbstractSegurado> segurados = sa.list(AbstractSegurado.class);
			TitularFinanceiroSR titular;// =  new TitularFinanceiroSR();
			if(segurados.size() == 1){
				achados++;
				AbstractSegurado segurado = segurados.get(0);
				
				if (segurado.isSeguradoPensionista()) {
					Pensionista pen = (Pensionista) segurado;
					titular = pen;
					System.out.println("PENSIONISTA.....");
				}
				if (segurado.isSeguradoDependente()) {
					Dependente dep = (Dependente) segurado;
					titular = dep.getTitular();
					System.out.println("SUPLEMENTARRRRR.....");
				}
				else {
					titular = (TitularFinanceiroSR) segurado;
				}
				criarBaixarCobranca(titular, null, null, valor, valor, DATA_VENCIMENTO, DATA_PAGAMENTO);
			}
			else if(segurados.size() > 1) {
				seguradosAchadosMaisDeUm.addAll(segurados);
				System.out.println(segurados.size() + " Segurados");
				for (AbstractSegurado abstractSegurado : segurados) {
					if(abstractSegurado.isSeguradoDependente()){
						suplementares.add(abstractSegurado);
						titular = abstractSegurado.getTitular();
						System.out.println("Dando Baixa em dependentes suplementares");
						criarBaixarCobranca(titular, null, null, valor, valor, DATA_VENCIMENTO, DATA_PAGAMENTO);
					}
				}
			}
			else {
				System.out.println("Segurado não encontrado: " + cpf);
				seguradosNaoAchados.add(cpf);
			}
			System.out.println(segurados.size() + " - " + cpf + " - " + nome);			
	
		}
		System.out.println("Achados: " + achados + " / Achados mais de um: " + seguradosAchadosMaisDeUm.size());
		
		tm.commit();

		System.out.println();
		System.out.println();
		System.out.println("SEGURADOS ENCONTRADOS MAIS DE UM COM O MESMO CPF:");
		seguradosAchadosMaisDeUm.removeAll(suplementares);
		for (AbstractSegurado lost : seguradosAchadosMaisDeUm) {
			System.out.println("CPF: " + lost.getPessoaFisica().getCpf() + " - Nome: " + lost.getPessoaFisica().getNome());
		}
		
		System.out.println("SEGURADOS SUPLEMENTARES");
		for (AbstractSegurado abstractSegurado2 : suplementares) {
			System.out.println("CPF: " + abstractSegurado2.getPessoaFisica().getCpf() + " - Nome: " + abstractSegurado2.getPessoaFisica().getNome());
		}
		
		System.out.println("SEGURADOS NÃO ENCONTRADOS");
		for (String s : seguradosNaoAchados) {
			System.out.println("CPF: " + s);
		}
		
	}
	
	private static void fromFilePDF() throws Exception {
		TransactionManagerHibernate tm = new TransactionManagerHibernate();
		tm.beginTransaction();
		
		Scanner sc = new Scanner(new FileReader("C:\\DIOGO\\Eclipse\\Workspace Diogo\\ecareRecife\\arquivos\\boletosSemCobrancas.csv"));
		StringTokenizer st;
		String nome, seuNumero, nossoNumero, valor, valorPago, vencimento, situacao;
		int achados = 0, naoAchados = 0, pagamentos = 0;
		
		while(sc.hasNext()){
			st          = new StringTokenizer(sc.nextLine(),";");
			nome        = st.nextToken();
			seuNumero   = st.nextToken();
			nossoNumero = st.nextToken();
			valor       = st.nextToken();
			valorPago   = st.nextToken();
			vencimento  = st.nextToken();
			situacao    = st.nextToken();
			
			Criteria criteria = HibernateUtil.currentSession().createCriteria(TitularFinanceiroSR.class);
			criteria.createAlias("matriculas", "mats");
			criteria.add(Expression.eq("mats.tipoPagamento", Constantes.BOLETO));
			criteria.add(Expression.eq("pessoaFisica.nome", nome));
			criteria.add(Expression.eq("situacao.descricao", SituacaoEnum.ATIVO.descricao()));
			List<TitularFinanceiroSR> titulares = criteria.list();
			if(titulares.isEmpty())
				naoAchados++;
			else {
				achados++;
				if(situacao.equals("Pago/Baixado")){
					pagamentos++;
					criarBaixarCobranca(titulares.get(0), seuNumero, nossoNumero, valor, valorPago, vencimento, vencimento);
				}
			}
			System.out.println(titulares.size() + " - " + nome);
	
		}
		System.out.println("Achados: " + achados + " / Não Achados: " + naoAchados + " / Pagamentos: " + pagamentos);
		
		tm.commit();
	}
	
	private static void criarBaixarCobranca(
			TitularFinanceiroSR titular, 
			String seuNumero, 
			String nossoNumero, 
			String valor, 
			String valorPago, 
			String vencimento, 
			String dataPagamento) throws Exception{

		Conta conta = new Conta();
		CobrancaInterface cobranca = titular.getCobranca(Utils.createData(COMPETENCIA));
		if(cobranca == null)
			cobranca = new Cobranca();
		else
			System.out.println("@@@@@@@@ COBRANÇAS ENCONTRADAS");
		
		titular.getFluxosFinanceiros().add(cobranca);
		
		cobranca.setTitular(titular);
//		cobranca.setNossoNumero(nossoNumero);
		cobranca.getColecaoContas().add(conta);
		
		cobranca.setValorCobrado(cobranca.getValorCobrado().add(getValorMonetario(valorPago)));
		cobranca.setValorPago(cobranca.getValorPago().add(getValorMonetario(valorPago)));
		cobranca.setCompetencia(Utils.createData(COMPETENCIA));
		cobranca.setDataVencimento(Utils.createData(vencimento));
		cobranca.setDataPagamento(Utils.createData(dataPagamento));
		cobranca.mudarSituacao(null, SituacaoEnum.PAGO.descricao(), "Processamento Automático", Utils.createData(dataPagamento));
		
		conta.setValorCobrado(conta.getValorCobrado().add(getValorMonetario(valorPago)));
		conta.setValorPago(conta.getValorPago().add(getValorMonetario(valorPago)));
		conta.setCompetencia(Utils.createData(COMPETENCIA));
		conta.setDataVencimento(Utils.createData(vencimento));
		conta.setDataPagamento(Utils.createData(dataPagamento));
		conta.mudarSituacao(null, SituacaoEnum.PAGO.descricao(), "Processamento Automático", Utils.createData(dataPagamento));
		
		ImplDAO.save(cobranca);
	}
	
	private static BigDecimal getValorMonetario(String valor){
		valor = valor.replace(',', '.');
		int pos = valor.length() - 2;
		return new BigDecimal(valor.substring(0,pos) + valor.substring(pos,valor.length()));
	}

}
