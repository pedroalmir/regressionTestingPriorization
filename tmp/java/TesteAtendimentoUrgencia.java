package br.com.infowaypi.ecare.services.urgencias.teste;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.services.urgencias.MarcacaoUrgencias;
import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaAtendimentoUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.procedimentos.CID;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.InterfaceTransactionManager;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.TransactionManagerHibernate;

public class TesteAtendimentoUrgencia extends TestCase {
	
	InterfaceTransactionManager tm= new TransactionManagerHibernate();
	MarcacaoUrgencias fluxo = new MarcacaoUrgencias();
	ResumoSegurados resumoSegurados;
	GuiaCompleta guia;
	Prestador prestador;
	Profissional profissional;
	Especialidade especialidade;
	List<CID> cids = new ArrayList<CID>();
	
	
//	public void testMarcacaoAtendimentoUrgencia() throws Exception{
//		
//		passoBuscarSegurados();
//		passoCriarGuiaUrgencia();
//		passoSalvarGuia();
//		
//	}
	
	
//	private void passoBuscarSegurados() throws Exception{
//		tm.beginTransaction();
//		fluxo = new MarcacaoUrgencias();
//		Grupo grupo = (Grupo) new SearchAgent().findById(1L,Grupo.class);	
//		resumoSegurados = fluxo.buscarSegurado("5061",grupo);
//		tm.rollback();
//		
//	}

	private void passoCriarGuiaUrgencia() throws Exception{
		
		tm.beginTransaction();
		fluxo = new MarcacaoUrgencias();
		fluxo.setIgnoreValidacao(false);
		prestador = (Prestador) new SearchAgent().findById(1L,Prestador.class);
		profissional = prestador.getProfissionais().iterator().next();
		especialidade = profissional.getEspecialidades().iterator().next();
		cids.add((CID) ImplDAO.findById(1L,CID.class));
		cids.add((CID) ImplDAO.findById(2L,CID.class));
		cids.add((CID) ImplDAO.findById(3L,CID.class));
 		guia = fluxo.criarGuiaUrgencia(resumoSegurados.getSegurados().get(0),2,profissional,especialidade,prestador, prestador.getUsuario(), new Date());
		
//		assertFalse("Validate Guia True",guia.validate());
		System.out.println("id antes do save: " + guia.getIdGuia());
		tm.rollback();
	}
	
	private void passoSalvarGuia() throws Exception{
		tm.beginTransaction();
		fluxo = new MarcacaoUrgencias();
		fluxo.salvarGuia(guia);
//		HibernateUtil.currentSession().save(guia);
		System.out.println("Id guia salva: " + guia.getIdGuia());
		tm.commit();
		
	}
	
	public static void main(String[] args) {
		Criteria c = HibernateUtil.currentSession().createCriteria(GuiaAtendimentoUrgencia.class);
		c.addOrder(Order.desc("dataatendimento"));
		List<GuiaAtendimentoUrgencia> list = c.list();
		int count = 0;
		for (GuiaAtendimentoUrgencia urgencia : list) {
			System.out.println("Guia "+count + urgencia.getDataAtendimento());
		}
		
		
	}
	
}
