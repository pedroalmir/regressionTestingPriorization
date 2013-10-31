package br.com.infowaypi.ecare.correcaomanual;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.hibernate.Transaction;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * 
 * @author wislanildo
 *
 */

public class AjusteCorrecaoConsultaUrgenciaEEletivaSemProcedimentoPadrao {

	public static final String PROCEDIMENTO_CONSULTA_URGENCIA = "10101039";
	public static final String PROCEDIMENTO_CONSULTA_ELETIVA = "10101012";
	
	private static UsuarioInterface usuario = null;
	
	private int countGuiaConsultaUrgencia = 0;
	private int countGuiaConsultaEletiva = 0;
	private int countProcGU = 0;
	private int countProcGE = 0;
	
	static {
		SearchAgent sa = new SearchAgent();
		usuario = (UsuarioInterface) sa.findById(1L, Usuario.class);
	}

	private void processar() throws Exception {
		
		Transaction tx =  HibernateUtil.currentSession().beginTransaction();
		
		analisarGuiasConsultaUrgencia(getGuiasAptasACorrecao(getAutorizacoesGuias("mozoi")));
		System.err.println("######################################################################################################");
		analisarGuiasConsultaEletiva(getGuiasAptasACorrecao(getAutorizacoesGuias("eletiva")));
		
		tx.commit();
//		tx.rollback();
		
		System.out.println();
		System.err.println("Devem ser iguais!");
		System.out.println();
		System.out.println("Qtd de Guia de Consulta Urgência analisadas: " + countGuiaConsultaUrgencia);
		System.out.println("Qtd de procedimentos para alteração: " + countProcGU);
		System.out.println("--------------------------------------------------------------");
		System.out.println("Qtd de Guia Consulta Eletiva analisadas: " + countGuiaConsultaEletiva);
		System.out.println("Qtd de procedimentos para alteração: " + countProcGE);
	}

	private List<GuiaSimples> getGuiasAptasACorrecaoEletivo(List<GuiaSimples> guiasAptasACorrecao) {
		List<GuiaSimples> guiasAptasACorrecaoEletivo = new ArrayList<GuiaSimples>();
		for(GuiaSimples g : guiasAptasACorrecao){
			if(g.isConsultaEletiva()){
				guiasAptasACorrecaoEletivo.add(g);
			}
		}
		return guiasAptasACorrecaoEletivo;
	}

	private List<GuiaSimples> getGuiasAptasACorrecaoUrgencia(List<GuiaSimples> guiasAptasACorrecao) {
		List<GuiaSimples> guiasAptasACorrecaoUrgencia = new ArrayList<GuiaSimples>();
		for(GuiaSimples g : guiasAptasACorrecao){
			if(g.isConsultaUrgencia()){
				guiasAptasACorrecaoUrgencia.add(g);
			}
		}
		return guiasAptasACorrecaoUrgencia;
	}
	
	private void analisarGuiasConsultaUrgencia(Set<GuiaSimples> guiasAptasACorrecao) throws Exception{
		for(GuiaSimples guia : guiasAptasACorrecao){
			
			countGuiaConsultaUrgencia++;
			
			ProcedimentoInterface procedimentoAlvo = getProcedimentoAlvo(guia, PROCEDIMENTO_CONSULTA_URGENCIA);
			if(procedimentoAlvo != null) {
				ajustarProcedimento("urgência", guia, procedimentoAlvo);
				countProcGU++;
			}
			
			guia.recalcularValores();
			guia.updateValorCoparticipacao();
			ImplDAO.save(guia);
		}
	}

	private void ajustarProcedimento(String desc, GuiaSimples guia,ProcedimentoInterface procedimentoAlvo) throws Exception {
		procedimentoAlvo.setValorAtualDoProcedimento(new BigDecimal("30.00"));
		procedimentoAlvo.calcularCampos();
		procedimentoAlvo.calculaCoParticipacao();
		procedimentoAlvo.aplicaValorAcordo();
		if(!procedimentoAlvo.isSituacaoAtual(SituacaoEnum.CONFIRMADO.descricao())){
			procedimentoAlvo.mudarSituacao(usuario, SituacaoEnum.CONFIRMADO.descricao(), "Correção do valor do procedimento", new Date());
		}
		ImplDAO.save(procedimentoAlvo);
		imprimirAutorizacaoGuia(guia, " - " + desc);
		imprimirSeparador();
	}

	private void analisarGuiasConsultaEletiva(Set<GuiaSimples> guiasAptasACorrecao) throws Exception {
		for(GuiaSimples guia : guiasAptasACorrecao){
			
			countGuiaConsultaEletiva++;
			
			ProcedimentoInterface procedimentoAlvo = getProcedimentoAlvo(guia, PROCEDIMENTO_CONSULTA_ELETIVA);
			if(procedimentoAlvo != null) {
				ajustarProcedimento("eletiva", guia, procedimentoAlvo);
				countProcGE++;
			}
			
			guia.recalcularValores();
			guia.updateValorCoparticipacao();
			ImplDAO.save(guia);
		}
	}

	private void imprimirSeparador() {
		System.out.println("----------------------------------------------------------------------");
	}
	
	private void imprimirAutorizacaoGuia(GuiaSimples guia, String desc) {
		System.out.println(guia.getAutorizacao() + " : " + desc);
	}

	private ProcedimentoInterface getProcedimentoAlvo(GuiaSimples guia, String codProcedimento) {
		for(Iterator<ProcedimentoInterface> it = guia.getProcedimentos().iterator(); it.hasNext();){
			ProcedimentoInterface procedimento = it.next();
			if(procedimento.getProcedimentoDaTabelaCBHPM().getCodigo().equals(codProcedimento)){
				return procedimento;
			}
		}
		return null;
	}
	
	private List<String> getAutorizacoesGuias(String modalidade) throws FileNotFoundException{
		List<String> autorizacoes = new ArrayList<String>(); 
		File file = null;
		
		if(modalidade.equals("eletiva")){
			file = new File("/home/wislanildo/guias_eletivas_ajustar.txt");
		}else {
			file = new File("/home/wislanildo/guias_urgencia_ajustar.txt");
		}
		
		Scanner scanner = new Scanner(file);
		while(scanner.hasNext()){
			autorizacoes.add(scanner.next());
		}
		
		return autorizacoes;
	}

	private Set<GuiaSimples> getGuiasAptasACorrecao(List<String> autorizacoes) {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new In("autorizacao" ,autorizacoes));
		List<GuiaSimples> guias =  sa.list(GuiaSimples.class);
		
		return new HashSet<GuiaSimples>(guias);
	}

	public static void main(String[] args) throws Exception {
		AjusteCorrecaoConsultaUrgenciaEEletivaSemProcedimentoPadrao cg = new AjusteCorrecaoConsultaUrgenciaEEletivaSemProcedimentoPadrao();
		cg.processar();
		
		Transaction tx = HibernateUtil.currentSession().beginTransaction();
		
//		SearchAgent sa = new SearchAgent();
//		sa.addParameter(new Equals("autorizacao", "1485773"));
//		GuiaSimples guia = sa.uniqueResult(GuiaSimples.class);
		
//		for(Iterator<ProcedimentoInterface> it = guia.getProcedimentos().iterator(); it.hasNext();){
//			ProcedimentoInterface p = it.next();
//			if(p.getIdProcedimento().equals(30648165L)){
//				
//				p.setValorAtualDoProcedimento(new BigDecimal("80.00"));
//				p.calcularCampos();
//				p.calculaCoParticipacao();
//				p.aplicaValorAcordo();
//				p.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), "Correção do valor do procedimento", new Date());
//				ImplDAO.save(p);
//				
//			}
//			if(p.getSituacao().equals(SituacaoEnum.CONFIRMADO.descricao())){
//				p.mudarSituacao(usuario, SituacaoEnum.CANCELADO.descricao(), "Correção do valor do procedimento", new Date());
//				ImplDAO.save(p);
//			}
//		}
//		guia.recalcularValores();
//		guia.updateValorCoparticipacao();
//		ImplDAO.save(guia);
//		tx.commit();
	}
}
