package br.com.infowaypi.ecare.correcaomanual;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.hibernate.Transaction;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.acordos.AcordoDiaria;
import br.com.infowaypi.ecarebc.atendimentos.acordos.AcordoGasoterapia;
import br.com.infowaypi.ecarebc.atendimentos.acordos.AcordoPacote;
import br.com.infowaypi.ecarebc.atendimentos.acordos.AcordoTaxa;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemGasoterapia;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemTaxa;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Between;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.msr.utils.Utils;

public class CorrecaoAplicarAcordoProntoclinica {

	public static void main(String[] args) throws Exception {
		Transaction tx = HibernateUtil.currentSession().beginTransaction();
//		atualizarValoresDeAcordos();
//		recalcularGuias();
//		tx.rollback();
//		tx.commit();
		
		corrigirGuiasQueMudaramDeAUR_para_CUR();
//		tx.commit();
	}

	private static void corrigirGuiasQueMudaramDeAUR_para_CUR() {
		SearchAgent sa = new SearchAgent();
//		sa.addParameter(new In("autorizacao", Arrays.asList("5537", "5615")));
		sa.addParameter(new Equals("autorizacao", "5275"));
		List<GuiaCompleta> guias = sa.list(GuiaCompleta.class);
		
		for(GuiaCompleta g : guias){
			System.out.println("valor antes: " + g.getValorTotalFormatado());
			Set<ProcedimentoInterface> procedimentos = g.getProcedimentos();
			for(ProcedimentoInterface p : procedimentos){
//				p.calcularCampos();
				p.aplicaValorAcordo();
			}
			g.recalcularValores();
			System.out.println("valor depois: " + g.getValorTotalFormatado());
		}
	}

	private static void atualizarValoresDeAcordos() throws Exception  {
		List<GuiaCompleta> guias = getGuiasDoAjuste();
		System.out.println("Qtd guias: " + guias.size());
		int proced = 0, dia = 0, tax = 0, gas = 0, pac = 0;
		List<String> guiasAjsutadas = new ArrayList<String>();		
		
		for(GuiaCompleta guia : guias){
			
			//Procedimentos
			Set<ProcedimentoInterface> procedimentos = guia.getProcedimentos();
			for(ProcedimentoInterface proc : procedimentos){
				BigDecimal valorAntes = proc.getValorTotal();
				proc.aplicaValorAcordo();
				if(valorAntes.compareTo(proc.getValorTotal()) != 0){
					proced++;
				}
				ImplDAO.save(proc);
			}
			
			//Diarias
			Set<ItemDiaria> diarias = guia.getItensDiaria();
			for(ItemDiaria itemDiaria : diarias){
				BigDecimal valorAntes = itemDiaria.getValor().getValor();
				for (AcordoDiaria acordo : guia.getPrestador().getAcordosDiariaAtivos()) {
					if(acordo.getDiaria().getCodigo().equals(itemDiaria.getDiaria().getCodigo())){
						if(valorAntes.compareTo(acordo.getValor()) != 0){
							dia++;
							itemDiaria.getValor().setValor(acordo.getValor());
						}
					}
				}
				ImplDAO.save(itemDiaria);
			}
			
			//Taxas
			Set<ItemTaxa> taxas = guia.getItensTaxa();
			for(ItemTaxa itemTaxa : taxas){
				itemTaxa.recalcularCampos();
				BigDecimal valorAntes = itemTaxa.getValor().getValor();
				for (AcordoTaxa acordo : guia.getPrestador().getAcordosTaxaAtivos()) {
					if(acordo.getTaxa().getCodigo().equals(itemTaxa.getTaxa().getCodigo())){
						if(valorAntes.compareTo(acordo.getValor()) != 0){
							tax++;
							itemTaxa.getValor().setValor(acordo.getValor());
						}
					}
				}
			}
			
			//Gasoterapias
			Set<ItemGasoterapia> gasoterapias = guia.getItensGasoterapia();
			for(ItemGasoterapia itemGasoterapia : gasoterapias){
				BigDecimal valorAntes = itemGasoterapia.getValor().getValor();
				for (AcordoGasoterapia acordo : guia.getPrestador().getAcordosGasoterapiaAtivos()) {
					if (acordo.getGasoterapia().getCodigo().equals(itemGasoterapia.getGasoterapia().getCodigo())) {
						if(valorAntes.compareTo(acordo.getValor()) != 0){
							gas++;
							itemGasoterapia.getValor().setValor(acordo.getValor());
						}
					}
				}
				ImplDAO.save(itemGasoterapia);
			}
			
			//Pacotes
			Set<ItemPacote> pacotes = guia.getItensPacote();
			for(ItemPacote itemPacote : pacotes){
				BigDecimal valorAntes = itemPacote.getValor().getValor();
				for (AcordoPacote acordo : guia.getPrestador().getAcordosPacoteAtivos()) {
					if(acordo.getPacote().getDescricao().equals(itemPacote.getPacote().getDescricao())){
						BigDecimal porcentagemDecimal = itemPacote.getPorcentagem().movePointLeft(2);//divide por 100
						BigDecimal valorAcordo = new BigDecimal(acordo.getValor());
						if(valorAntes.compareTo(valorAcordo.multiply(porcentagemDecimal)) != 0){
							pac++;
							itemPacote.getValor().setValor(valorAcordo.multiply(porcentagemDecimal));
						}
					}
				}
				ImplDAO.save(itemPacote);
			}
		}
		System.out.println("Alterações realiadas: Procedimentos: [" + proced + "] Diarias: ["+  dia + "] Taxas: [" + tax + "] Gasoterapia: [" +  gas + "] Pacotes: [" + pac + "]");
	}

	private static void recalcularGuias() throws Exception {
		List<GuiaCompleta> guias = getGuiasDoAjuste();
		System.out.println("Somatório das guias antes da intervenção: " + getSomatorioGuia(guias));
		List<String> guiasAjsutadas = new ArrayList<String>();
		
		for(GuiaSimples guia : guias){
			BigDecimal valorAntesDoAjuste = guia.getValorTotal();
			guia.recalcularValores();
			BigDecimal valorDepoisDoAjuste = guia.getValorTotal();
			
			if(valorAntesDoAjuste.compareTo(valorDepoisDoAjuste) != 0){
				guiasAjsutadas.add(guia.getAutorizacao());
				ImplDAO.save(guia);
			}else{
				System.out.println("Não ajustada: " + guia.getAutorizacao());
			}
			
		}
		
		System.out.println("QTD guias ajustadas: " + guiasAjsutadas.size());
		System.out.println(guiasAjsutadas);
		System.out.println("Somatório das guias depois da intervenção: " + getSomatorioGuia(guias));
		
	}
	
	private static List<GuiaCompleta> getGuiasDoAjuste(){
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Between("dataAtendimento", Utils.createData("01/08/2013"), Utils.createData("31/08/2013")));
		sa.addParameter(new Equals("situacao.descricao", "Auditado(a)"));
		sa.addParameter(new Equals("prestador", ImplDAO.findById(55L, Prestador.class)));
		return sa.list(GuiaCompleta.class);
	}
	
	private static BigDecimal getSomatorioGuia(List<GuiaCompleta> guias){
		BigDecimal total = BigDecimal.ZERO;
		for(GuiaSimples guia : guias){
			total = total.add(guia.getValorTotal());
		}
		return total;
	}
	
}
