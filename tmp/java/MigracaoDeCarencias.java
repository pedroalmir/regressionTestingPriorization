package br.com.infowaypi.ecare.correcaomanual;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.TransactionManagerHibernate;

public class MigracaoDeCarencias {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		TransactionManagerHibernate tmh = new TransactionManagerHibernate();
		tmh.beginTransaction();
		
		List<Segurado> seguradosComCarenciaNula = HibernateUtil.currentSession().createQuery("Select s from Segurado s where s.inicioDaCarencia is null").list();
		for (Segurado segurado : seguradosComCarenciaNula) {
			if (segurado.isSeguradoTitular() 
					|| segurado.isSeguradoPensionista() 
					|| segurado.isSeguradoDependenteSuplementar() ){
				
				atualizarCarencia(segurado, segurado.getIdSegurado());
				
			} else {
				atualizarCarencia(segurado, segurado.getTitular().getIdSegurado());
			}
		} 
		
		tmh.commit();
	}
	
	private static boolean atualizarCarencia(Segurado segurado, Long idTitular) throws Exception {
		String sqlConsignacao = "select min(c.dataDoCredito) from ConsignacaoSegurado c  where c.titular.idSegurado = :idTitular "
				+ "and c.statusConsignacao  = 'P' and c.dataDoCredito >= :dataAdesao";

		String sqlCobranca = "select min(c.dataPagamento) from Cobranca c where c.titular.idSegurado = :idTitular  "
				+ "and descricao = 'Pago(a)' and c.dataPagamento >= :dataAdesao";

		Query queryConsignacao = HibernateUtil.currentSession().createQuery(sqlConsignacao);
		Query queryCobranca = HibernateUtil.currentSession().createQuery(sqlCobranca);
		
		queryConsignacao.setLong("idTitular", idTitular);
		queryConsignacao.setDate("dataAdesao", segurado.getDataAdesao());
		
		Date inicioCarencia = (Date) queryConsignacao.uniqueResult();

		if (inicioCarencia == null) {
			queryCobranca.setLong("idTitular", idTitular);
			queryCobranca.setDate("dataAdesao", segurado.getDataAdesao());
			inicioCarencia = (Date) queryCobranca.uniqueResult();
		}

		if (inicioCarencia != null) {
			System.out.println(segurado.getNumeroDoCartao() + " - " + inicioCarencia);
			segurado.setInicioDaCarencia(inicioCarencia);
			ImplDAO.save(segurado);
			return true;
		}
		return false;
	}
}
