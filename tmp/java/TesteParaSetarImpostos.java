package br.com.infowaypi.ecare.financeiro;

import java.math.BigDecimal;
import java.util.Date;

import br.com.infowaypi.ecarebc.financeiro.faturamento.retencao.AbstractImposto;
import br.com.infowaypi.ecarebc.financeiro.faturamento.retencao.ImpostoDeRenda;
import br.com.infowaypi.ecarebc.financeiro.faturamento.retencao.Inss;
import br.com.infowaypi.ecarebc.financeiro.faturamento.retencao.Iss;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Seta os valores dos Impostos cobrados no Faturamento
**/


public class TesteParaSetarImpostos {

	public static void main(String[] args) throws Exception {
		
		Date competenciaAtual = Utils.incrementaMes(Utils.gerarCompetencia(), -1);


		
		//IMPOSTO DE RENDA
		//---------------------------------------------------//
		
		HibernateUtil.currentSession().beginTransaction().begin();
		ImpostoDeRenda impostoDeRenda1 = new ImpostoDeRenda();
		ImpostoDeRenda impostoDeRenda2 = new ImpostoDeRenda();
		ImpostoDeRenda impostoDeRenda3 = new ImpostoDeRenda();
		ImpostoDeRenda impostoDeRenda4 = new ImpostoDeRenda();
		System.out.println("Setando valores de Imposto de Renda... ");
		
		//OBJETO 1
		impostoDeRenda1.setAliquota(0);
		impostoDeRenda1.setValorFaixaDe(new BigDecimal(0));
		impostoDeRenda1.setValorFaixaAte(new BigDecimal(1313.69));
		impostoDeRenda1.setValorDeducao(new BigDecimal(0));
		impostoDeRenda1.setTipoDePessoa(AbstractImposto.PESSOA_FISICA);
		impostoDeRenda1.setCompetencia(competenciaAtual);
		ImplDAO.save(impostoDeRenda1);
		
		//OBJETO 2
		impostoDeRenda2.setAliquota(15);
		impostoDeRenda2.setValorFaixaDe(new BigDecimal(1313.7));
		impostoDeRenda2.setValorFaixaAte(new BigDecimal(2625.12));
		impostoDeRenda2.setValorDeducao(new BigDecimal(197.05));
		impostoDeRenda2.setTipoDePessoa(AbstractImposto.PESSOA_FISICA);
		impostoDeRenda2.setCompetencia(competenciaAtual);
		ImplDAO.save(impostoDeRenda2);
		
		//OBJETO 3
		impostoDeRenda3.setAliquota(27.5f);
		impostoDeRenda3.setValorFaixaDe(new BigDecimal(2625.12));
		impostoDeRenda3.setValorFaixaAte(new BigDecimal(10000000.00));
		impostoDeRenda3.setValorDeducao(new BigDecimal(525.19));
		impostoDeRenda3.setTipoDePessoa(AbstractImposto.PESSOA_FISICA);
		impostoDeRenda3.setCompetencia(competenciaAtual);
		ImplDAO.save(impostoDeRenda3);
	
		//OBJETO 4
		impostoDeRenda4.setAliquota(1.5f);
		impostoDeRenda4.setValorFaixaDe(new BigDecimal(0));
		impostoDeRenda4.setValorFaixaAte(new BigDecimal(0));
		impostoDeRenda4.setValorDeducao(new BigDecimal(0));
		impostoDeRenda4.setTipoDePessoa(AbstractImposto.PESSOA_JURIDICA);
		impostoDeRenda4.setCompetencia(competenciaAtual);
		ImplDAO.save(impostoDeRenda4);
		
		
		System.out.println("Setando valores de INSS...");
		//INSS
		//-----------------------------------------------------//
		Inss inss = new Inss();
		inss.setAliquota(11);
		inss.setValorDescontoMaximo(new BigDecimal(2894.28));
		inss.setValorDescontoMinimo(new BigDecimal(29.6));
		inss.setCompetencia(competenciaAtual);
		ImplDAO.save(inss);
		
		System.out.println("Setando valores de ISS...");
		//ISS
		//-----------------------------------------------------//
		Iss iss1 = new Iss();
		Iss iss2 = new Iss();
		
		//OBJETO 1
		iss1.setAliquota(3);
		iss1.setTipoDePessoa(AbstractImposto.PESSOA_FISICA);
		iss1.setCompetencia(competenciaAtual);
		ImplDAO.save(iss1);
		
		//OBJETO 2
		iss2.setAliquota(3);
		iss2.setTipoDePessoa(AbstractImposto.PESSOA_JURIDICA);
		iss2.setCompetencia(competenciaAtual);
		ImplDAO.save(iss2);
		
		
		System.out.println("Comitando...");
		HibernateUtil.currentSession().beginTransaction().rollback();
		HibernateUtil.currentSession().close();
		
	}
}
