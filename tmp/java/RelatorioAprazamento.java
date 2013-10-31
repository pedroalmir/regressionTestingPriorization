package br.com.infowaypi.ecare.relatorio;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import br.com.infowaypi.ecare.resumos.ResumoRelatorioAprazamento;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

public class RelatorioAprazamento {
	
	@SuppressWarnings("deprecation")
	public ResumoRelatorioAprazamento gerarRelatorio(Date dataInicial, Date dataFinal, Boolean exibirGuias) throws Exception {
		String [] situacoes = {SituacaoEnum.PAGO.descricao(),SituacaoEnum.FATURADA.descricao(),SituacaoEnum.CONFIRMADO.descricao()};
					
		Connection conn = HibernateUtil.currentSession().connection();
		PreparedStatement pstAprazamento;
		
		java.sql.Date sqlDataInicial = new java.sql.Date(dataInicial.getTime());
		java.sql.Date sqlDataFinal = new java.sql.Date(dataFinal.getTime());
		
		String sql = "select autorizacao,g.datamarcacao,g.dataatendimento,g.descricao,s.nome,p.fantasia, valortotal,g.dataatendimento - g.datamarcacao as dias from atendimento_guiasimples g, associados_prestador p, segurados_segurado s where "+
					 "g.descricao in (?,?,?) and tipodeguia = 'GCS' and g.idprestador  = p.idprestador and  s.idsegurado = g.idsegurado and "+
					 "g.dataatendimento between ? and ?";
		
		pstAprazamento = conn.prepareStatement(sql);
		pstAprazamento.setString(1, SituacaoEnum.PAGO.descricao());
		pstAprazamento.setString(2, SituacaoEnum.FATURADA.descricao());
		pstAprazamento.setString(3, SituacaoEnum.CONFIRMADO.descricao());
		
		pstAprazamento.setDate(4, sqlDataInicial);
		pstAprazamento.setDate(5, sqlDataFinal);
		
		ResultSet resAprazamento = pstAprazamento.executeQuery();
		
		ResumoRelatorioAprazamento resumo = new ResumoRelatorioAprazamento(resAprazamento, exibirGuias);
		
		return resumo;
	}
	
	public static void main(String[] args) throws SQLException {
//		Date dtInicial = Utils.parse("01/01/2008");
//		Date dtFinal = Utils.parse("31/01/2008");
//		Connection conn = HibernateUtil.currentSession().connection();
//		PreparedStatement pstAprazamento;
//		
//		java.sql.Date sqlDataInicial = new java.sql.Date(dtInicial.getTime());
//		java.sql.Date sqlDataFinal = new java.sql.Date(dtFinal.getTime());
//		
//		String sql = "select autorizacao,datamarcacao,datasituacao, date_part('day',(datasituacao-datamarcacao)) as dias from atendimento_guiasimples where "+
//		"descricao in (?,?,?) and tipodeguia = 'GCS' and "+
//		"datasituacao between ? and ?";
//		
//		pstAprazamento = conn.prepareStatement(sql);
//		pstAprazamento.setString(1, SituacaoEnum.PAGO.descricao());
//		pstAprazamento.setString(2, SituacaoEnum.FATURADA.descricao());
//		pstAprazamento.setString(3, SituacaoEnum.CONFIRMADO.descricao());
//		
//		pstAprazamento.setDate(4, sqlDataInicial);
//		pstAprazamento.setDate(5, sqlDataFinal);
//		
//		ResultSet resAprazamento = pstAprazamento.executeQuery();
//		
//		while (resAprazamento.next()) {
//			System.out.println(resAprazamento.getString(1));
//			System.out.println(resAprazamento.getDate(2));
//			System.out.println(resAprazamento.getDate(3));
//			System.out.println(resAprazamento.getInt(4));
//		}
		
		BigDecimal um = new BigDecimal(100012).setScale(2,BigDecimal.ROUND_HALF_UP);
		BigDecimal dois = new BigDecimal(2121212).setScale(2,BigDecimal.ROUND_HALF_UP);
		
		System.out.println(um.divide(dois,2,BigDecimal.ROUND_HALF_UP));
	}
	
}
