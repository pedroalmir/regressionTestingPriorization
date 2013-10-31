package br.com.infowaypi.ecarebc.atendimentos.alta;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import br.com.infowaypi.molecular.HibernateUtil;

public class TesteMotivo {

	@Test
	public void criaMotivo(){
		Session s = HibernateUtil.currentSession();
		Transaction tx = s.beginTransaction();
		MotivoAlta motivoAlta = new MotivoAlta();
		motivoAlta.setCodigo("111");
		motivoAlta.setDescricao("Recuperado");
		s.save(motivoAlta);
		tx.commit();
	}
}
