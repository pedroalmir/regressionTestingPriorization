package br.com.infowaypi.ecare.correcaomanual;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.Transaction;

import br.com.infowaypi.ecare.constantes.Constantes;
import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.financeiro.Cobranca;
import br.com.infowaypi.ecare.segurados.AbstractMatricula;
import br.com.infowaypi.ecare.segurados.TitularFinanceiroSR;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.Not;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.utils.Utils;

public class BaixaManualDeBoletos {
	
	
	public static void main(String[] args) {
		Transaction transacao = HibernateUtil.currentSession().beginTransaction();
		Usuario usuario = ImplDAO.findById(1L, Usuario.class);
		Cobranca boleto = ImplDAO.findById(1444822L, Cobranca.class);
		
		System.out.println("Boleto Errado: " + boleto);
		boleto.mudarSituacao(usuario, SituacaoEnum.CANCELADO.descricao(), "Situação anterior foi alterada por engano.", new Date());
		transacao.commit();
	}
	
//	public static void main(String[] args) throws Exception {
//		
//		//É nescessário comentar o trecho de código na classe Cobranca.java que sobrescreve o metodo mudarSituacao do msr
//		//para que este ajuste funcione corretamente.
//		
//		Usuario usuario = ImplDAO.findById(1L, Usuario.class);
//		
//		//	Cobranças do segurado Claudio Felix de Souza
//		Cobranca cobrancaClaudio0 = buscarBoleto("193.825.374-49", "", "07/2011");
//		cobrancaClaudio0.setValorCobrado(new BigDecimal("151.88"));
//		cobrancaClaudio0.setValorPago(new BigDecimal("151.88"));
//		cobrancaClaudio0.setDataVencimento(Utils.createData("07/07/2011"));
//		cobrancaClaudio0.setDataPagamento(Utils.createData("07/07/2011"));
//		cobrancaClaudio0.mudarSituacao(usuario, SituacaoEnum.PAGO.descricao(), "Ajuste de baixa manual solicitada pela diretoria financeira.", new Date());
//		salvar(cobrancaClaudio0);
//		System.out.println("cobrancaClaudio0: " + cobrancaClaudio0);
//		
//		Cobranca cobrancaClaudio1 = buscarBoleto("193.825.374-49", "", "08/2011");
//		cobrancaClaudio1.setValorCobrado(new BigDecimal("213.42"));
//		cobrancaClaudio1.setValorPago(new BigDecimal("213.42"));
//		cobrancaClaudio1.setDataVencimento(Utils.createData("08/08/2011"));
//		cobrancaClaudio1.setDataPagamento(Utils.createData("09/08/2011"));
//		cobrancaClaudio1.mudarSituacao(usuario, SituacaoEnum.PAGO.descricao(), "Ajuste de baixa manual solicitada pela diretoria financeira.", new Date());
//		salvar(cobrancaClaudio1);
//		System.out.println("cobrancaClaudio1: " + cobrancaClaudio1);
//		
//		Cobranca cobrancaClaudio2 = buscarBoleto("193.825.374-49", "", "09/2011");
//		cobrancaClaudio2.setValorCobrado(new BigDecimal("145.88"));
//		cobrancaClaudio2.setValorPago(new BigDecimal("145.88"));
//		cobrancaClaudio2.setDataVencimento(Utils.createData("09/09/2011"));
//		cobrancaClaudio2.setDataPagamento(Utils.createData("09/09/2011"));
//		cobrancaClaudio2.mudarSituacao(usuario, SituacaoEnum.PAGO.descricao(), "Ajuste de baixa manual solicitada pela diretoria financeira.", new Date());
//		salvar(cobrancaClaudio2);
//		System.out.println("cobrancaClaudio2: " + cobrancaClaudio2);
//		
//		//  Cobranças do segurado Everaldo Galdino de Moaura
//		Cobranca cobrancaEveraldo0 = buscarBoleto("136.558.094-68", "", "09/2011");
//		cobrancaEveraldo0.setValorCobrado(new BigDecimal("25.59"));
//		cobrancaEveraldo0.setValorPago(new BigDecimal("25.59"));
//		cobrancaEveraldo0.setDataVencimento(Utils.createData("09/09/2011"));
//		cobrancaEveraldo0.setDataPagamento(Utils.createData("09/09/2011"));
//		cobrancaEveraldo0.mudarSituacao(usuario, SituacaoEnum.PAGO.descricao(), "Ajuste de baixa manual solicitada pela diretoria financeira.", new Date());
//		salvar(cobrancaEveraldo0);
//		System.out.println("cobrancaEveraldo0: " + cobrancaEveraldo0);
//		
//		//  Cobranças do segurado Waldir de Barros Chaves
//		Cobranca cobrancaWaldir0 = buscarBoleto("144.092.164-49", "", "09/2011");
//		cobrancaWaldir0.setValorCobrado(new BigDecimal("79.56"));
//		cobrancaWaldir0.setValorPago(new BigDecimal("79.56"));
//		cobrancaWaldir0.setDataVencimento(Utils.createData("09/09/2011"));
//		cobrancaWaldir0.setDataPagamento(Utils.createData("09/09/2011"));
//		cobrancaWaldir0.mudarSituacao(usuario, SituacaoEnum.PAGO.descricao(), "Ajuste de baixa manual solicitada pela diretoria financeira.", new Date());
//		salvar(cobrancaWaldir0);
//		System.out.println("cobrancaWaldir0: " + cobrancaWaldir0);
//	}
//	
//	public static Cobranca buscarBoleto(String cpf, String numeroDoCartao, String competencia) throws ValidateException {
//		TitularFinanceiroSR titular;
//		
//		SearchAgent sa = new SearchAgent();
//		if(!Utils.isStringVazia(cpf)){
//			sa.addParameter(new Equals("pessoaFisica.cpf", cpf));
//		}
//		
//		if(!Utils.isStringVazia(numeroDoCartao)){
//			sa.addParameter(new Equals("numeroDoCartao", numeroDoCartao));
//		}
//		
//		sa.addParameter(new Not(new Equals("situacao.descricao", SituacaoEnum.CANCELADO.descricao())));
//		
//		String parametroExibido = cpf != null ? cpf : numeroDoCartao;
//		
//		List<TitularFinanceiroSR> titulares = sa.list(TitularFinanceiroSR.class);
//		if (titulares.size() > 1) {
//			throw new ValidateException(MensagemErroEnumSR.MAIS_DE_UM_BENEFICIARIO_PARA_O_CPF.getMessage(parametroExibido));
//		} else if (titulares.size() == 0) {
//			throw new ValidateException(MensagemErroEnumSR.BENEFICIARIO_NAO_ENCONTRADO_PARA_O_CPF.getMessage(parametroExibido));			
//		} else {
//			titular = titulares.get(0);
//		}
//		
//		boolean isPagaBoleto = false;
//		for (AbstractMatricula matricula : titular.getMatriculas()) {
//			if (matricula.getTipoPagamento().equals(Constantes.BOLETO)) {
//				isPagaBoleto = true;
//			}
//		}
//		
//		//Não entendi!!
//		if (!isPagaBoleto && !titular.isSeguradoDependenteSuplementar()) {
//			throw new ValidateException(MensagemErroEnumSR.BENEFICIARIO_SEM_MATRICULA_BOLETO.getMessage(titular.getPessoaFisica().getNome()));
//		}
//		
//		if(isPagaBoleto){
//			Date comp = Utils.createData("01/" + competencia);
//			Cobranca cobranca = (Cobranca) titular.getCobranca(comp);
//			return cobranca;
//		}
//		
//		return null;
//	}
//	
//	private static void salvar(Serializable obj) throws Exception {
//		Transaction tx = HibernateUtil.currentSession().beginTransaction();
//		ImplDAO.save(obj);
//		tx.commit();
//	}
}
