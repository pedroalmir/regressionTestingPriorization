package br.com.infowaypi.ecare.correcaomanual;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Transaction;

import edu.emory.mathcs.backport.java.util.Arrays;

import br.com.infowaypi.ecarebc.atendimentos.GuiaAcompanhamentoAnestesico;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GuiaHonorarioMedico;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.molecular.parameter.NotIn;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

public class RecalcularGuias2013 {

	
	public static void main(String[] args) {
		
		int count = 0;
		
		List<GuiaSimples> guias = new ArrayList<GuiaSimples>();
		guias.addAll(getGHMsCOOPANEST());
		
		List<String> guiasNaoAlteradas = new ArrayList<String>();
		
		for(GuiaSimples guia : guias){
			
			Transaction tx = HibernateUtil.currentSession().beginTransaction();
			
			BigDecimal valorAntes = guia.getValorPagoPrestador();
			
			guia.recalcularValores();

			BigDecimal valorDepois = guia.getValorPagoPrestador();
			BigDecimal diferenca = valorDepois.subtract(valorAntes);
			
			if (MoneyCalculation.compare(diferenca, BigDecimal.ZERO) > 0){
				System.out.println("[Situação: "
						+ guia.getSituacao().getDescricao() + "] [Tipo: "
						+ guia.getTipo() + "] [Guia: " + guia.getAutorizacao()
						+ "] [valor antes: " + valorAntes + "] [valor depois: "
						+ valorDepois + "] [diferença: " + diferenca + "]");
				count++;
			}else{
				guiasNaoAlteradas.add("[Situação: "+ guia.getSituacao().getDescricao() + "] [Tipo: "
					+ guia.getTipo() + "] [Guia: " + guia.getAutorizacao() + "]  [Data situacao: " + guia.getSituacao().getData());
			}
			
			
			tx.commit();
		}
		
		System.out.println("Honorários ajustados: " + count);
		System.out.println("\n\n");

		for(String str : guiasNaoAlteradas){
			System.out.println(str);
		}
		
	}

	private static List<GuiaHonorarioMedico> getGHMsCOOPANEST() {
		String [] sit = {"Solicitado(a)", "Cancelado(a)", "Glosado(a)"};
		List<String> situacoes = Arrays.asList(sit);
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("prestador.idPrestador", 373690L));
		sa.addParameter(new GreaterEquals("dataAtendimento", Utils.createData("01/01/2013")));
		sa.addParameter(new NotIn("situacao.descricao", situacoes));
		List<GuiaHonorarioMedico> guias = sa.list(GuiaHonorarioMedico.class);
		System.out.println("Qtd GGHM's:" + guias.size());
		return guias;
	}
	
}
