/**
 * 
 */
package br.com.infowaypi.ecare.financeiro.faturamento;

import java.math.BigInteger;
import java.util.Date;

import br.com.infowaypi.ecarebc.financeiro.faturamento.ResumoFaturamentos;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.utils.Assert;

/**
 * @author Marcus Vinicius
 *
 */
public class FecharFaturamentoService {
	
	private static final String FATURAMENTO_NORMAL_TABLE = "associados_faturamento";
	private static final String FATURAMENTO_PASSIVO_TABLE = "associados_faturamentopassivo";
	private static final String FATURAMENTO_COOPANEST_TABLE = "associados_faturamento_anestesico";
	
	
	public ResumoFaturamentos fecharFaturamentos(Date competencia) throws Exception {
		
		Assert.isFalse(executeSelect(FATURAMENTO_NORMAL_TABLE, competencia) == 0, "O faturamento desta competencia já foi fechado ou ainda não foi gerado.");
//		Assert.isFalse(executeSelect(FATURAMENTO_PASSIVO_TABLE, competencia) == 0, "O faturamento desta competencia já foi fechado ou ainda não foi gerado.");
//		Assert.isFalse(executeSelect(FATURAMENTO_COOPANEST_TABLE, competencia) == 0, "O faturamento desta competencia já foi fechado ou ainda não foi gerado.");
		
		setFechamento(FATURAMENTO_NORMAL_TABLE, competencia);
		setFechamento(FATURAMENTO_PASSIVO_TABLE, competencia);
		setFechamento(FATURAMENTO_COOPANEST_TABLE, competencia);
		
		ResumoFaturamentos resumo = new ResumoFaturamentos();
		resumo.setCompetencia(competencia);
		return resumo;
	}
	
	private int executeSelect(String table, Date competencia) {
		String sqlCount = "select count(*) from :table where competencia = :competencia and status = 'A' and valorbruto > 0";
		sqlCount = sqlCount.replaceFirst(":table", table);
		return ((BigInteger) HibernateUtil.currentSession().createSQLQuery(sqlCount)
				.setDate("competencia", competencia)
				.uniqueResult()).intValue();
	}
	
	private void setFechamento(String table, Date competencia) {
		String update = "update :table set status = 'F' where competencia = :competencia and status = 'A' and valorbruto > 0";
		update = update.replace(":table", table);
		HibernateUtil.currentSession().createSQLQuery(update)
				.setDate("competencia", competencia)
				.executeUpdate();
	}

}
