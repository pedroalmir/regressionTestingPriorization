package br.com.infowaypi.ecare.scheduller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Between;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.molecular.parameter.IsNull;
import br.com.infowaypi.molecular.parameter.Not;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

public class TaskResumoDeBeneficiariosDeGuiasComCoParticipacoesErradas implements Job {
	private  String path = "/home/recife/web/arquivos/";
	 List<ResumoGuia> resumoGuias = null;
	 Integer totalGuiasCooparticipacaoErro = 0;
	 Boolean encontrou_um_evento = false;

	/**
	 * Task que é responsavel por gerar relatórios sobre beneficiários 	que tem guias com calculos errados sobre co-participações. A task
	 * gera arquivos no formato xls, nas competencias de 11, e 12 de 2010 , e 1 a 8 de 2011 
	 * @author Jefferson, Marcos
	 * @throws Exception 
	 */
	
	
	public static  void main(String[] args) throws JobExecutionException {
		TaskResumoDeBeneficiariosDeGuiasComCoParticipacoesErradas task = new TaskResumoDeBeneficiariosDeGuiasComCoParticipacoesErradas();
		
		task.execute(null);
	}

	public TaskResumoDeBeneficiariosDeGuiasComCoParticipacoesErradas() {
		resumoGuias = new ArrayList<ResumoGuia>();
	}

	private  List<ResumoBeneficiario> calcularEprepararResumoDosBeneficiarios(Map<String, List<ResumoGuia>> mapResumoBeneficiario) {
		
		List<ResumoBeneficiario>  listResumoBeneficiario  = new ArrayList<ResumoBeneficiario>();
        
		Iterator<String> iterator = mapResumoBeneficiario.keySet().iterator();
		
		while (iterator.hasNext()) {
			
			String keyBeneficiario = (String) iterator.next();
			List<ResumoGuia> listaResumoGuia = mapResumoBeneficiario.get(keyBeneficiario);
			BigDecimal totalCoPartPag = BigDecimal.ZERO;
			BigDecimal totalCoPartCorr = BigDecimal.ZERO;
			String beneficiario = null;
			int quantGuias = listaResumoGuia.size();
			Iterator<ResumoGuia> iterator2 = listaResumoGuia.iterator();
			while (iterator2.hasNext()) {
				ResumoGuia resumoGuia = (ResumoGuia) iterator2.next();
				if (beneficiario == null ){
					beneficiario =resumoGuia.beneficiario;
				}
				totalCoPartPag = totalCoPartPag.add(resumoGuia.vcopAntes);
				totalCoPartCorr  = totalCoPartCorr.add(resumoGuia.vcopDepois);
			} 
			
			listResumoBeneficiario.add( new ResumoBeneficiario(beneficiario, quantGuias, totalCoPartPag, totalCoPartCorr,totalCoPartPag.subtract(totalCoPartCorr)));
			
		}
		return listResumoBeneficiario;
	}

	private  Map<String , List<ResumoGuia>> organizarResumoPorBeneficiario() {
		Map<String , List<ResumoGuia>> guiasPorBeneficiarios = new HashMap<String, List<ResumoGuia>>();
		ArrayList<ResumoGuia> lista;
		for (ResumoGuia resumo : resumoGuias) {
			String chaveBeneficiario = resumo.beneficiario;
			if(!guiasPorBeneficiarios.containsKey(chaveBeneficiario)) {
				lista = new ArrayList<ResumoGuia>();
				lista.add(resumo);
				guiasPorBeneficiarios.put(chaveBeneficiario, lista);
			} else {
				guiasPorBeneficiarios.get(chaveBeneficiario).add(resumo);
			}
		}
		return guiasPorBeneficiarios;
	}

	private  void gerarArquivoDeSaida(List<ResumoBeneficiario> listaResumoBeneficiario, String data) throws IOException, RowsExceededException, WriteException, ParseException {

		System.out.println("gerando  arquivo de saida " );
		
		String dataString = data.replace("/", "");
		WritableWorkbook resultado = Workbook.createWorkbook(new File(path+"Relatorio_Co-Participacoes_Corrigidas" + dataString + ".xls"));

        Object[] list = listaResumoBeneficiario.toArray();		
		WritableSheet sheet = resultado.createSheet("Beneficiarios", 0);
			
			sheet.addCell(new Label(0, 0, "Beneficiário"));
			sheet.addCell(new Label(1, 0, "Número de Guias do Beneficiário"));
			sheet.addCell(new Label(2, 0, "Saldo"));

			for (int i = 1; i < list.length; i++) {
				ResumoBeneficiario resumoBeneficiario = (ResumoBeneficiario) list[i];
				sheet.addCell(new Label(0, i, resumoBeneficiario.nome));
				sheet.addCell(new Label(1, i, String.valueOf(resumoBeneficiario.quantidadeGuias)));
				sheet.addCell(new Label(2, i, resumoBeneficiario.saldo.toString()));
			} 
			
		resultado.write();       
		resultado.close();

	}

	private  void recalcularGuiasComCooParticipacaoErrada(List<GuiaSimples> guias) throws Exception {
		System.out.println("processando o recalculo das co-participações");
	     String progressBar =  "";
		int size = guias.size();
		int count  = 0;
		
		BigDecimal k = new BigDecimal(size).divide(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_UP);
		Iterator<GuiaSimples> iterator = guias.iterator();
		
		while (iterator.hasNext()) {
			GuiaSimples guiaSimples = (GuiaSimples) iterator.next();
			recalcularCoparticipacao(guiaSimples);
			count++;
			if (count ==k.intValue()){
				System.out.print(progressBar+".");
				count  = 0;
			}
		}

	}

	private  void recalcularCoparticipacao(GuiaSimples guiaSimples) throws Exception {
		BigDecimal valorCoParticipacaoGuiaAntes = guiaSimples.getValorCoParticipacao();
		guiaSimples.aplicaValorAcordo();

		if (MoneyCalculation.compare(valorCoParticipacaoGuiaAntes, guiaSimples.getValorCoParticipacao()) != 0){			
			resumoGuias.add( new ResumoGuia(guiaSimples.getAutorizacao(),
					guiaSimples.getTipo(),
					Utils.format(guiaSimples.getDataAtendimento()),
					guiaSimples.getPrestador().getNome(),
					guiaSimples.getValorPagoPrestador().toString(),
					"Cartão: "+guiaSimples.getSegurado().getNumeroDoCartao()+" - Beneficiário: "+guiaSimples.getSegurado().getPessoaFisica().getNome(),
					guiaSimples.getSituacao().getDescricao(),
					guiaSimples.getValorTotal(), 
					valorCoParticipacaoGuiaAntes,
					guiaSimples.getValorCoParticipacao(),
					""));
			encontrou_um_evento = true;
		}
	}

	private List <GuiaSimples> pesquisarGuiasComCooParticipacao(Date dataInicial, Date dataFinal) {

		System.out.println("pesquisando guias"); 

		System.out.println("data inicial: " + Utils.format(dataInicial) );

		System.out.println("data final: " + Utils.format(dataFinal) );

		String tipos [] = {"GHM","IEL","IUR"};
		
		SearchAgent sa  = new SearchAgent();
		sa.addParameter( new Not(new Equals("valorCoParticipacao", BigDecimal.ZERO)));
		sa.addParameter(new Not(new In("tipoDeGuia", tipos)));
		sa.addParameter(new IsNull("fluxoFinanceiro"));
		sa.addParameter(new Between("dataAtendimento", dataInicial, dataFinal));
	    List <GuiaSimples> list = sa.list(GuiaSimples.class);
		System.out.println("numero de guias encontradas : " + list.size());
		return list;
	}

	public     class ResumoGuia {


		public ResumoGuia(String autorizacao, String tipoDeGuia,
				String dataAtendimento, String prestador,
				String valorPagoPrestador, String beneficiario, String situacao,
				BigDecimal valorTotal, BigDecimal vcopAntes, BigDecimal vcopDepois,
				String obsercacao) {
			
			this.autorizacao = autorizacao;
			this.tipoDeGuia = tipoDeGuia;
			this.dataAtendimento = dataAtendimento;
			this.prestador = prestador;
			this.valorPagoPrestador = valorPagoPrestador;
			this.beneficiario = beneficiario;
			this.situacao = situacao;
			this.valorTotal = valorTotal;
			this.vcopAntes = vcopAntes;
			this.vcopDepois = vcopDepois;
			this.obsercacao = obsercacao;
		}


		@Override
		public String toString() {
			return autorizacao + "      " + tipoDeGuia + "        " + dataAtendimento + "        " + 
			prestador + " " + valorPagoPrestador + " " + beneficiario + " " + 
			situacao + " " + valorTotal + " " + vcopAntes + " " + vcopDepois + " " + obsercacao;
		}


		String autorizacao;
		String tipoDeGuia;
		String dataAtendimento;
		String prestador;
		String valorPagoPrestador;
		String beneficiario;
		String situacao;
		BigDecimal valorTotal;
		BigDecimal vcopAntes;
		BigDecimal vcopDepois;
		String obsercacao;

	}
	
	public  class ResumoBeneficiario{
		
		String nome;
		int quantidadeGuias;
		BigDecimal totalCoPartPagas;
		BigDecimal totalCoPartCorrigidas;
		BigDecimal saldo;

		public ResumoBeneficiario( String nome,
				int quantidadeGuias, BigDecimal totalCoPartPagas,
				BigDecimal totalCoPartCorrigidas, BigDecimal saldo) {
			this.nome = nome;
			this.quantidadeGuias = quantidadeGuias;
			this.totalCoPartPagas = totalCoPartPagas;
			this.totalCoPartCorrigidas = totalCoPartCorrigidas;
			this.saldo = saldo;
		}
		
	}
	
	public  BigDecimal processaValor(String str) throws ParseException {
		if (Utils.isStringVazia(str)) {
			return BigDecimal.ZERO;
		}
		DecimalFormat formato = new DecimalFormat("0.00");  
		double dValor = formato.parse(str.trim()).doubleValue();
		String valueOf = String.valueOf(dValor);
		return new BigDecimal(valueOf);

	}

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		int ano = 2010;
		int mes = 9;
		int limite = 11;
		int totalArquivos = 1;
		for (int i = mes; i <= limite; i++) {
			Long hora1 = System.currentTimeMillis();
			
			Calendar dataInicial = Calendar.getInstance();
			dataInicial.set(ano, i, 20);
			dataInicial.set(Calendar.HOUR, 0);
			dataInicial.set(Calendar.MINUTE, 0);
			dataInicial.set(Calendar.SECOND, 0);

			
			Calendar dataFinal = Calendar.getInstance();
			
			if(i == 11) {
				dataFinal.set((ano+1), 0, 20);
				
				if(i == 11 && ano == 2010) {
					i = -1;
					ano = 2011;
					limite = 8;
				}
			} else {
				dataFinal.set(ano, (i+1), 20);
			}
			
			System.out.println("Intervalo: " + Utils.format(dataInicial.getTime())  + " a " + Utils.format(dataFinal.getTime()));
			List<GuiaSimples> guias = pesquisarGuiasComCooParticipacao(dataInicial.getTime(),dataFinal.getTime());
			try {
				recalcularGuiasComCooParticipacaoErrada(guias);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("\n Total de Guias (erro em cooparticipacao): " + resumoGuias.size());
			Map<String, List<ResumoGuia>> mapResumoPorBeneficiario = organizarResumoPorBeneficiario();
			List<ResumoBeneficiario> listaResumoBeneficiario = calcularEprepararResumoDosBeneficiarios(mapResumoPorBeneficiario);
			try {
				gerarArquivoDeSaida(listaResumoBeneficiario,"Resumo"+Utils.format(dataInicial.getTime()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			totalArquivos++;
			System.out.println("PROCESSAMENTO ENCERRADO..."); 
			System.out.println("TEMPO DE GERAÇÃO DE ARQUIVO (Min): " + ((System.currentTimeMillis() - hora1)/1000)/60);
		}
	
		
	}
	
}



