package br.com.infowaypi.ecare.services.suporte;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.molecular.parameter.LowerEquals;
import br.com.infowaypi.molecular.parameter.OR;
import br.com.infowaypi.msr.utils.Utils;

@SuppressWarnings("unchecked")
public class GeracaoPlanilhaGuiasSuporte {
	
	private ByteArrayOutputStream planilhaGuias;
	
	public byte[] getPlanilhaGuias() {
		return planilhaGuias.toByteArray();
	}
	
	@SuppressWarnings("unchecked")
	private Set<GuiaSimples> buscarGuias(String dataInicial, String dataFinal, Prestador prestador, Profissional profissional, Boolean responsavel, Boolean solicitante, TabelaCBHPM procedimento, Collection<String> tiposDeGuia, Collection<String> situacoes, Integer tipoDeData) throws Exception {

		if (prestador == null && Utils.isStringVazia(dataInicial) && Utils.isStringVazia(dataFinal)){
			throw new Exception(MensagemErroEnum.AUSENCIA_DE_PARAMETROS.getMessage());
		}
		
		SearchAgent sa = new SearchAgent();

		sa.addParameter(new In("situacao.descricao", situacoes));
		sa.addParameter(new In("tipoDeGuia", tiposDeGuia));
		
		if (prestador != null) {
			sa.addParameter(new Equals("prestador", prestador));
		}
		
		restringeBuscaPorData(dataInicial, dataFinal, tipoDeData, sa);	
		restringeBuscaPorProfissional(profissional, responsavel, solicitante, sa);
		restringeBuscaPorProcedimento(procedimento, sa);
		
		Set<GuiaSimples> guias = new HashSet<GuiaSimples>();
		guias.addAll(sa.list(GuiaSimples.class));
		
		
		if(guias.isEmpty())
			throw new RuntimeException(MensagemErroEnum.NENHUMA_GUIA_ENCONTRADA.getMessage());
		
		return guias;
	}

	private void restringeBuscaPorProcedimento(TabelaCBHPM procedimento,
			SearchAgent sa) {
		if (procedimento != null) {
			sa.addParameter(new Equals("procedimentos.procedimentoDaTabelaCBHPM", procedimento));
			sa.addParameter(new In("procedimentos.situacao.descricao", "Agendado(a)", "Ativo(a)", "Autorizado(a)", "Confirmado(a)", "Faturado(a)", "Fechado(a)", "Honorário Gerado", "Pré-autorizado", "Realizado(a)", "Solicitado(a)"));
		}
	}

	private void restringeBuscaPorProfissional(Profissional profissional,
			Boolean responsavel, Boolean solicitante, SearchAgent sa) {
		if(profissional != null){
			
			if(responsavel == false && solicitante == false)
				throw new RuntimeException(MensagemErroEnum.PROFISSIONAL_PELO_MENOS_UMA_OPCAO.getMessage());
			
			if(responsavel && solicitante){
				sa.addParameter(new OR(new Equals("profissional", profissional), new Equals("solicitante", profissional)));
			}else if(responsavel){
				sa.addParameter(new Equals("profissional", profissional));
			}else if(solicitante){
				sa.addParameter(new Equals("solicitante", profissional));
			}
		}
	}

	private void restringeBuscaPorData(String dataInicial, String dataFinal,
			Integer tipoDeData, SearchAgent sa) {
		Date dataInicialFormatada = Utils.parse(dataInicial);
		Date dataFinalFormatada = Utils.parse(dataFinal);
		
		Calendar dataInicalCalendar = Calendar.getInstance();
		dataInicalCalendar.setTime(dataInicialFormatada);
		
		Calendar dataFinalCalendar = Calendar.getInstance();
		dataFinalCalendar.setTime(dataFinalFormatada);
		dataFinalCalendar.set(Calendar.HOUR_OF_DAY, 23);
		dataFinalCalendar.set(Calendar.MINUTE, 59);
		dataFinalCalendar.set(Calendar.SECOND, 59);
		if(tipoDeData.equals(GuiaSimples.DATA_DE_MARCACAO)){
			if (!Utils.isStringVazia(dataInicial))
				sa.addParameter(new GreaterEquals("dataMarcacao", Utils.parse(dataInicial)));
		
			if (!Utils.isStringVazia(dataFinal))
				sa.addParameter(new LowerEquals("dataMarcacao", dataFinalCalendar.getTime()));
		}
		
		if(tipoDeData.equals(GuiaSimples.DATA_DE_ATENDIMENTO)){
			if (!Utils.isStringVazia(dataInicial))
				sa.addParameter(new GreaterEquals("dataAtendimento", Utils.parse(dataInicial)));
		
			if (!Utils.isStringVazia(dataFinal))
				sa.addParameter(new LowerEquals("dataAtendimento", dataFinalCalendar.getTime()));
		}
		if(tipoDeData.equals(GuiaSimples.DATA_DE_SITUACAO)){
			if (!Utils.isStringVazia(dataInicial))
				sa.addParameter(new GreaterEquals("situacao.dataSituacao", Utils.parse(dataInicial)));
		
			if (!Utils.isStringVazia(dataFinal))
				sa.addParameter(new LowerEquals("situacao.dataSituacao", dataFinalCalendar.getTime()));
		}
		if(tipoDeData.equals(GuiaSimples.DATA_DE_TERMINO)){
			if (!Utils.isStringVazia(dataInicial))
				sa.addParameter(new GreaterEquals("dataTerminoAtendimento", Utils.parse(dataInicial)));
		
			if (!Utils.isStringVazia(dataFinal))
				sa.addParameter(new LowerEquals("dataTerminoAtendimento", dataFinalCalendar.getTime()));
		}
		if(tipoDeData.equals(GuiaSimples.DATA_DE_RECEBIMENTO)){
			if (!Utils.isStringVazia(dataInicial)) {
				sa.addParameter(new GreaterEquals("dataRecebimento", Utils.parse(dataInicial)));
			}
			if (!Utils.isStringVazia(dataFinal)) {
				sa.addParameter(new LowerEquals("dataRecebimento", dataFinalCalendar.getTime()));
			}
		}
	}
	
	public GeracaoPlanilhaGuiasSuporte geracaoPlanilhaGuias(String dataInicial, String dataFinal, Prestador prestador, Profissional profissional, Boolean responsavel, Boolean solicitante, TabelaCBHPM procedimento, Collection<String> tiposDeGuia, Collection<String> situacoes, Integer tipoDeData) throws Exception {
		Set<GuiaSimples> guias = this.buscarGuias(dataInicial, dataFinal, prestador, profissional, responsavel, solicitante, procedimento, tiposDeGuia, situacoes, tipoDeData);
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
		WritableSheet s = workbook.createSheet("GUIAS", 0);
		
		/* Formata a fonte */
		WritableFont wf = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD);
		WritableCellFormat cf = new WritableCellFormat(wf);
		cf.setWrap(true);
		
		int linha = 0;
		int coluna = 0;
		s.addCell(new Label(coluna++, linha, "AUTORIZAÇÃO",cf));
		s.addCell(new Label(coluna++, linha, "DATA DE ATENDIMENTO",cf));
		s.addCell(new Label(coluna++, linha, "DATA DE TÉRMINO DE ATENDIMENTO",cf));
		s.addCell(new Label(coluna++, linha, "CARTÃO",cf));
		s.addCell(new Label(coluna++, linha, "NOME DO SEGURADO",cf));
		s.addCell(new Label(coluna++, linha, "SITUAÇÃO DA GUIA",cf));
		s.addCell(new Label(coluna++, linha, "DATA DA SITUAÇÃO",cf));
		s.addCell(new Label(coluna++, linha, "TIPO DE GUIA",cf));
		s.addCell(new Label(coluna++, linha, "VALOR TOTAL",cf));
		s.addCell(new Label(coluna++, linha, "VALOR PAGO",cf));
		String dataAtendimento, dataTermino;
		for (GuiaSimples<ProcedimentoInterface> guia : guias) {
			linha++;
			coluna = 0;
			s.addCell(new Label(coluna++, linha, guia.getAutorizacao()));
			dataAtendimento = guia.getDataAtendimento()== null? "" : Utils.format(guia.getDataAtendimento());
			s.addCell(new Label(coluna++, linha, dataAtendimento));
			dataTermino = guia.getDataTerminoAtendimento()== null? "" : Utils.format(guia.getDataTerminoAtendimento());
			s.addCell(new Label(coluna++, linha, dataTermino));
			s.addCell(new Label(coluna++, linha, guia.getSegurado().getNumeroDoCartao()));
			s.addCell(new Label(coluna++, linha, guia.getSegurado().getPessoaFisica().getNome()));
			s.addCell(new Label(coluna++, linha, guia.getSituacao().getDescricao()));
			s.addCell(new Label(coluna++, linha, Utils.format(guia.getSituacao().getDataSituacao())));
			s.addCell(new Label(coluna++, linha, guia.getTipo()));
			s.addCell(new Label(coluna++, linha, Utils.format(guia.getValorTotal())));
			s.addCell(new Label(coluna++, linha, Utils.format(guia.getValorPagoPrestador())));
		}
		workbook.write();
		workbook.close();
		
		planilhaGuias = outputStream;
		
		return this;
	}
	
	public String getFileGuias(){
		return "guias.xls";
	}

	
}
