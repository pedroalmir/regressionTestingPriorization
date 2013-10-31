import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import org.hibernate.Transaction;

import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.atendimentos.GuiaAtendimentoUrgencia;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Correção das guias de atendimento subsequente erroneamente valoradas.
 * @author Luciano Rocha
 * @since 06/12/2012
 */
public class CorrecaoGuiasAtendimentoSubsequenteValoradas {

	public static void main(String[] args) throws Exception {
		
//		corrigir();
		
		corrigirCoparticipacao();
	}

	private static void corrigirCoparticipacao() throws FileNotFoundException, Exception {
		Transaction tx = HibernateUtil.currentSession().beginTransaction();
		
		List<String> autorizacoes = getGuiasCorrigirCoParticipacao();
		System.out.println("Qtd guias buscadas: "+ autorizacoes.size());
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new In("autorizacao", autorizacoes));
		List<GuiaAtendimentoUrgencia> guias = sa.list(GuiaAtendimentoUrgencia.class);
		System.out.println("Qtd guias encontradas: "+ guias.size());
		
		for(GuiaAtendimentoUrgencia guia : guias){
			autorizacoes.remove(guia.getAutorizacao());
			guia.updateValorCoparticipacao();
			ImplDAO.save(guia);
		}
		
		for(String s : autorizacoes){
			System.out.println(s);
		}
		
		tx.commit();
	}

	private static List<String> getGuiasCorrigirCoParticipacao() throws FileNotFoundException {
		List<String> autorizacoes = new ArrayList<String>();
		File file = new File("C://Users//wislanildo//Desktop//Ajuste atendimento subsequente valorado//guiasParaAtualizarCoparticipacao.txt");
		Scanner sc = new Scanner(new FileReader(file));
		
		while(sc.hasNext()) {
			autorizacoes.add(sc.nextLine());
		}
		
		return autorizacoes;
	}

	private static void corrigir() {
		Transaction tx = HibernateUtil.currentSession().beginTransaction();
		System.out.println("Iniciando a busca " + new Date());
		
		SearchAgent sa = new SearchAgent();

		sa.addParameter(new GreaterEquals("valorTotal", new BigDecimal(40)));
//		sa.addParameter(new GreaterEquals("dataTerminoAtendimento", Utils.parse("21/11/2010")));
//		sa.addParameter(new LowerEquals("dataTerminoAtendimento", Utils.parse("20/12/2011")));
//		sa.addParameter(new OrderBy("dataTerminoAtendimento"));
		
		List<GuiaAtendimentoUrgencia> guias = sa.list(GuiaAtendimentoUrgencia.class);
		System.out.println("Carregou as guias " + new Date());
		
		int numeroDeAURValoradas = 0;
		
		Set<GuiaAtendimentoUrgencia> guiasPagasJAN = new HashSet<GuiaAtendimentoUrgencia>();
		Set<GuiaAtendimentoUrgencia> guiasPagasFEV = new HashSet<GuiaAtendimentoUrgencia>();
		Set<GuiaAtendimentoUrgencia> guiasPagasMAR = new HashSet<GuiaAtendimentoUrgencia>();
		Set<GuiaAtendimentoUrgencia> guiasPagasABR = new HashSet<GuiaAtendimentoUrgencia>();
		Set<GuiaAtendimentoUrgencia> guiasPagasMAI = new HashSet<GuiaAtendimentoUrgencia>();
		Set<GuiaAtendimentoUrgencia> guiasPagasJUN = new HashSet<GuiaAtendimentoUrgencia>();
		Set<GuiaAtendimentoUrgencia> guiasPagasJUL = new HashSet<GuiaAtendimentoUrgencia>();
		Set<GuiaAtendimentoUrgencia> guiasPagasAGO = new HashSet<GuiaAtendimentoUrgencia>();
		Set<GuiaAtendimentoUrgencia> guiasPagasSET = new HashSet<GuiaAtendimentoUrgencia>();
		Set<GuiaAtendimentoUrgencia> guiasPagasOUT = new HashSet<GuiaAtendimentoUrgencia>();
		Set<GuiaAtendimentoUrgencia> guiasPagasNOV = new HashSet<GuiaAtendimentoUrgencia>();
		Set<GuiaAtendimentoUrgencia> guiasPagasDEZ = new HashSet<GuiaAtendimentoUrgencia>();

		Set<GuiaAtendimentoUrgencia> guiasNaoPagas = new HashSet<GuiaAtendimentoUrgencia>();
		
		for (GuiaAtendimentoUrgencia guia : guias) {
			if(guia.isUrgenciaOuAtendimentoSubsequente()){
				int qntProcIndevidos = 0;
				float valorIndevido = 0;
				for (Object proc : guia.getProcedimentos()) {
					if (((Procedimento)proc).isConsulta() 
							&& ((((Procedimento)proc).getValorAtualDoProcedimento()).intValue() >= 40)) {
						qntProcIndevidos++;
						valorIndevido += ((Procedimento)proc).getValorAtualDoProcedimento().floatValue(); 
					}
				}
//				guia.setValorPagoIndevidamente(valorIndevido);
				if(qntProcIndevidos > 0 && valorIndevido > 0){
					if(guia.isSituacaoAtual(SituacaoEnum.PAGO.descricao()) 
							|| guia.isSituacaoAtual(SituacaoEnum.FATURADA.descricao())){
						if(Utils.between(Utils.parse("21/12/2010"), Utils.parse("20/01/2011"), guia.getDataTerminoAtendimento())){
							guiasPagasJAN.add(guia);
						}
						else if(Utils.between(Utils.parse("21/01/2011"), Utils.parse("20/02/2011"), guia.getDataTerminoAtendimento())){
							guiasPagasFEV.add(guia);
						}
						else if(Utils.between(Utils.parse("21/02/2011"), Utils.parse("20/03/2011"), guia.getDataTerminoAtendimento())){
							guiasPagasMAR.add(guia);
						}
						else if(Utils.between(Utils.parse("21/03/2011"), Utils.parse("20/04/2011"), guia.getDataTerminoAtendimento())){
							guiasPagasABR.add(guia);
						}
						else if(Utils.between(Utils.parse("21/04/2011"), Utils.parse("20/05/2011"), guia.getDataTerminoAtendimento())){
							guiasPagasMAI.add(guia);
						}
						else if(Utils.between(Utils.parse("21/05/2011"), Utils.parse("20/06/2011"), guia.getDataTerminoAtendimento())){
							guiasPagasJUN.add(guia);
						}
						else if(Utils.between(Utils.parse("21/06/2011"), Utils.parse("20/07/2011"), guia.getDataTerminoAtendimento())){
							guiasPagasJUL.add(guia);
						}
						else if(Utils.between(Utils.parse("21/07/2011"), Utils.parse("20/08/2011"), guia.getDataTerminoAtendimento())){
							guiasPagasAGO.add(guia);
						}
						else if(Utils.between(Utils.parse("21/08/2011"), Utils.parse("20/09/2011"), guia.getDataTerminoAtendimento())){
							guiasPagasSET.add(guia);
						}
						else if(Utils.between(Utils.parse("21/09/2011"), Utils.parse("20/10/2011"), guia.getDataTerminoAtendimento())){
							guiasPagasOUT.add(guia);
						}
						else if(Utils.between(Utils.parse("21/10/2011"), Utils.parse("20/11/2011"), guia.getDataTerminoAtendimento())){
							guiasPagasNOV.add(guia);
						}
						else if(Utils.between(Utils.parse("21/11/2011"), Utils.parse("20/12/2011"), guia.getDataTerminoAtendimento())){
							guiasPagasDEZ.add(guia);
						}
					}
					else {
						guiasNaoPagas.add(guia);
					}
					
					numeroDeAURValoradas++;
				}
			}
		}
		
		
		System.out.println("Número de guias de atendimento subsequentes valoradas erroneamente: " + numeroDeAURValoradas);
		System.out.println("#################### Guias Valoradas #########################");
		
		System.out.println("#################### Guias Pagas Janeiro #########################");
		for (GuiaAtendimentoUrgencia guia : guiasPagasJAN) {
			imprimirGuiaPaga(guia);
		}
		System.out.println("#################### Guias Pagas Fevereiro #########################");
		for (GuiaAtendimentoUrgencia guia : guiasPagasFEV) {
			imprimirGuiaPaga(guia);
		}
		System.out.println("#################### Guias Pagas Março #########################");
		for (GuiaAtendimentoUrgencia guia : guiasPagasMAR) {
			imprimirGuiaPaga(guia);
		}
		System.out.println("#################### Guias Pagas Abril #########################");
		for (GuiaAtendimentoUrgencia guia : guiasPagasABR) {
			imprimirGuiaPaga(guia);
		}
		System.out.println("#################### Guias Pagas Maio #########################");
		for (GuiaAtendimentoUrgencia guia : guiasPagasMAI) {
			imprimirGuiaPaga(guia);
		}
		System.out.println("#################### Guias Pagas Junho #########################");
		for (GuiaAtendimentoUrgencia guia : guiasPagasJUN) {
			imprimirGuiaPaga(guia);
		}
		System.out.println("#################### Guias Pagas Julho #########################");
		for (GuiaAtendimentoUrgencia guia : guiasPagasJUL) {
			imprimirGuiaPaga(guia);
		}
		System.out.println("#################### Guias Pagas Agosto #########################");
		for (GuiaAtendimentoUrgencia guia : guiasPagasAGO) {
			imprimirGuiaPaga(guia);
		}
		System.out.println("#################### Guias Pagas Setembro #########################");
		for (GuiaAtendimentoUrgencia guia : guiasPagasSET) {
			imprimirGuiaPaga(guia);
		}
		System.out.println("#################### Guias Pagas Outubro #########################");
		for (GuiaAtendimentoUrgencia guia : guiasPagasOUT) {
			imprimirGuiaPaga(guia);
		}
		System.out.println("#################### Guias Pagas Novembro #########################");
		for (GuiaAtendimentoUrgencia guia : guiasPagasNOV) {
			imprimirGuiaPaga(guia);
		}
		System.out.println("#################### Guias Pagas Dezembro #########################");
		for (GuiaAtendimentoUrgencia guia : guiasPagasDEZ) {
			imprimirGuiaPaga(guia);
		}
		
		System.out.println("#################### Guias Não Pagas #########################");
		for (GuiaAtendimentoUrgencia guia : guiasNaoPagas) {
			imprimirGuiaNaoPaga(guia);
		}
		
		for (GuiaAtendimentoUrgencia guia : guiasNaoPagas) {
			guia.recalcularValores();
			try {
				ImplDAO.save(guia);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		tx.commit();
	}

	/**
	 * Método para imprimir uma guia paga.
	 * @author Luciano Rocha
	 * @param guia
	 * @since 06/12/2012
	 */
	private static void imprimirGuiaPaga(GuiaAtendimentoUrgencia guia) {
		System.out.println(guia.getAutorizacao() + ";" +
				           guia.getDataAtendimento() + ";" +
				           guia.getDataTerminoAtendimento() + ";" +
				           guia.getSegurado().getPessoaFisica().getNome() + ";" +
				           guia.getPrestador().getNome() + ";" +
				           guia.getSituacao().getDescricao() + ";" +
				           Utils.format(guia.getSituacao().getDataSituacao()) + ";" +
				           guia.getTipo());
//				           guia.getValorPagoIndevidamente());
	}
	
	/**
	 * Método para imprimir uma guia não paga.
	 * @author Luciano Rocha
	 * @param guia
	 * @since 06/12/2012
	 */
	private static void imprimirGuiaNaoPaga(GuiaAtendimentoUrgencia guia) {
		System.out.println(guia.getAutorizacao() + ";" +
				           guia.getDataAtendimento() + ";" +
				           guia.getDataTerminoAtendimento() + ";" +
				           guia.getSegurado().getPessoaFisica().getNome() + ";" +
				           guia.getPrestador().getNome() + ";" +
				           guia.getSituacao().getDescricao() + ";" +
				           Utils.format(guia.getSituacao().getDataSituacao()) + ";" +
				           guia.getTipo());
//				           guia.getValorPagoIndevidamente());
	}
}
