package br.com.infowaypi.ecare.correcaomanual;

import java.util.Date;

import org.hibernate.Transaction;

import br.com.infowaypi.ecare.cadastros.MotivoGlosa;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.user.Usuario;

/**
 * Classe de associação de um profissional a um prestador.
 * @author Luciano Rocha
 * @since 07/02/2013
 */
public class AssociarProfissionalPrestador {

//	public static void main(String[] args) {
//		
//		Transaction tx = HibernateUtil.currentSession().beginTransaction();
//		
//		Prestador prestador = ImplDAO.findById(38575896L, Prestador.class);
//
//		Profissional profissional = ImplDAO.findById(509112L, Profissional.class);	
//		
//		prestador.getProfissionais().add(profissional);
//		
//		System.out.println("Profissional: " + profissional.getNome());
//		System.out.println("Prestador: " + prestador.getNome());
//		
//		try {
//			ImplDAO.save(prestador);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		for (Profissional p : prestador.getProfissionais()) {
////			if (profissional.getCrm().equals(profissional.getCrm())) {
//				System.out.println("Profissional associado: " + p.getNome());
////			}
//		}
//		
//		//rodar apontando pro valendo insert into associados_prestadorprofissional VALUES  (idprestador, idprofissional)
////		tx.commit();
//	}
	
//	public static void main(String[] args) {
//		
//		Transaction tx = HibernateUtil.currentSession().beginTransaction();
//		
//		SearchAgent sa = new SearchAgent();
//		
//		sa.addParameter(new Equals("autorizacao", "1891223"));
//		
//		GuiaSimples guia = sa.uniqueResult(GuiaSimples.class);
//		
//		guia.getSituacao().setMotivo("1813 - COBRANCA DE PROCEDIMENTO SEM JUSTIFICATIVA PARA REALIZACAO OU COM JUSTIFICATIVA INSUFICIENTE");
//		
//		try {
//			ImplDAO.save(guia);
//		} catch (Exception e) {}
//		
//		tx.commit();
//	}
}
