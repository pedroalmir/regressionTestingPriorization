package br.com.infowaypi.ecare.correcaomanual;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Transaction;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.utils.Utils;

public class DataRecebimentoGEX082011 {
	public static void main(String[] args) {
		Transaction transacao = HibernateUtil.currentSession().beginTransaction();
		
		String sql = "select g from GuiaSimples g where g.dataTerminoAtendimento between '2011-08-21' and '2011-09-20' and g.situacao.descricao = 'Auditado(a)'";
		
		Query createQuery = HibernateUtil.currentSession().createQuery(sql);
		
		List<GuiaSimples> guias = createQuery.list();
		System.out.println("Quantidad ede guias: " + guias.size());
		BigDecimal valorTotal = BigDecimal.ZERO;
		for (GuiaSimples guiaCompleta : guias) {
			SituacaoInterface situacao = guiaCompleta.getSituacao(SituacaoEnum.RECEBIDO.descricao());
			
			if (situacao != null){
				Date dataAnterior = guiaCompleta.getDataRecebimento();
				System.out.print(guiaCompleta.getAutorizacao()+ "; " + guiaCompleta.getValorPagoPrestador()+ "; " + guiaCompleta.getDataTerminoAtendimento() + "; " + dataAnterior + ";");
				guiaCompleta.setDataRecebimento(situacao.getDataSituacao());
				System.out.print(guiaCompleta.getDataRecebimento());
				if (dataAnterior != null && guiaCompleta.getDataRecebimento() != null){
					if(Utils.compareData(dataAnterior, guiaCompleta.getDataRecebimento()) != 0){
						System.out.print("; MUDOU");
						valorTotal = valorTotal.add(guiaCompleta.getValorTotal());
					}
				}
				System.out.println();
			} else {
				System.out.println("Guia não processada: " + guiaCompleta.getAutorizacao());
			}
		}
		
		System.out.println("Valor Total: " + valorTotal);
		System.out.println("Commitou...");
		transacao.commit();
//		SearchAgent sa = new SearchAgent();
//		sa.addParameter(new IsNull("dataRecebimento"));
//		sa.addParameter(new Equals("situacao.descricao", "Auditado(a)"));
//		Date dataInicial = Utils.parse("21/07/2011");
//		Date dataFinal 	= Utils.parse("20/08/2011");
//		System.out.println("datas: " + dataInicial + dataFinal);
//		sa.addParameter(new Between("dataTerminoAtendimento", dataInicial, dataFinal));
//		List<GuiaExame> quantidadeDeGuias = sa.list(GuiaExame.class);
//		System.out.println("Quantidade de guias: " + quan;tidadeDeGuias.size());
	}
}
