package br.com.infowaypi.ecare.financeiro.arquivo.strategy;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.FieldPosition;

import org.apache.commons.lang.StringUtils;

import br.com.infowaypi.ecare.financeiro.arquivo.ArquivoDeEnvioConsignacao;
import br.com.infowaypi.ecare.financeiro.consignacao.ConsignacaoMatriculaInterface;
import br.com.infowaypi.ecare.segurados.Empresa;
import br.com.infowaypi.msr.financeiro.InformacaoFinanceiraInterface;
import br.com.infowaypi.msr.utils.Utils;

public class StrategyRemessaConsignacaoRecife {
	
	private static final DecimalFormat DECIMAL_FORMAT_ALIQUOTA = new DecimalFormat("######0.0000");
	private static final DecimalFormat DECIMAL_FORMAT_MONEY = new DecimalFormat("######0.00");
	private static final String QUANTIDADE_DE_PARCELAS = "01";
	private static final String CALCULO = "01";
	private static final String VIRGULA = ",";

	/**
	 * Numero fixo. Campo de 1 caracter que identifica que a linha do registro é o corpo		
	 */
	private static final String TIPO_REGISTRO_CORPO = "2";
	
	/**
	 * Campo de 17 carateres. Contém somente zeros.
	 */
	private static final String FILTER = StringUtils.rightPad("",3, "0");
	
	/**
	 * Campo que identifica o estabelecimento. O valor desse é fixo.
	 */
	private static final String ESTABELECIMENTO = "0001";
	
	private Empresa empresa;

	public void executar(ConsignacaoMatriculaInterface conta, InformacaoFinanceiraInterface informacaoFinanceira) throws Exception {
//		Nome do lote.Campo de 10 caracteres composto pela constante WSAUDREC e o codigo da empresa.
		String nomeDoLote = StringUtils.rightPad("WSAUDREC" + empresa.getCodigoLegado(), 10, " ");
//		 Campo de 3 caracteres. Guarda o código da empresa do segurado		
		final String codigoEmpresa = StringUtils.rightPad(empresa.getCodigoLegado(),3 ," ");
//		Campo que identifica a matrícula do segurado. Campo de 10 caracteres
		String identTitular = conta.getMatricula().getDescricao() != null ? conta.getMatricula().getDescricao() : " ";
		final String prontuario = StringUtils.leftPad(identTitular,10 , "0");
		final String competencia = Utils.format(conta.getCompetencia(), "MMyyyy");
		final String dataBase = Utils.format(conta.getCompetencia(), "MMyyyy");
//		Campo q identifia o código da verba. Possui 3 caracteres
		
		String identificacao = conta.getMatricula().getDescricao();
		BigDecimal valorFinanciamento = conta.getValorFinanciamento();
		StringBuffer valorCobradoFormatado = new StringBuffer();
		valorCobradoFormatado = DECIMAL_FORMAT_MONEY.format(valorFinanciamento,valorCobradoFormatado,new FieldPosition(DecimalFormat.FRACTION_FIELD));
		valorCobradoFormatado.deleteCharAt(valorCobradoFormatado.indexOf(VIRGULA));
		leftPad(valorCobradoFormatado,17,"0");

		BigDecimal valorCoparticipacao = conta.getValorCoparticipacao();
		StringBuffer valorCoParticipacaoFormatado = new StringBuffer();
		valorCoParticipacaoFormatado = DECIMAL_FORMAT_MONEY.format(valorCoparticipacao,valorCoParticipacaoFormatado,new FieldPosition(DecimalFormat.FRACTION_FIELD));
		valorCoParticipacaoFormatado.deleteCharAt(valorCoParticipacaoFormatado.indexOf(VIRGULA));
		leftPad(valorCoParticipacaoFormatado,17,"0");

//		Percentual da verba
		BigDecimal aliquota = conta.getPercentualAliquota();
		StringBuffer aliquotaFormatado = new StringBuffer();
		aliquotaFormatado = DECIMAL_FORMAT_ALIQUOTA.format(aliquota,aliquotaFormatado,new FieldPosition(DecimalFormat.FRACTION_FIELD));
		aliquotaFormatado.deleteCharAt(aliquotaFormatado.indexOf(VIRGULA));
		leftPad(aliquotaFormatado,11,"0");
		
		if(valorFinanciamento != null && valorFinanciamento.compareTo(BigDecimal.ZERO) > 0){
			final String codigoVerbaFinancimento;
			if(conta.isCobrancaFuncionarioURBEfetivo()) {
				codigoVerbaFinancimento = StringUtils.leftPad("877",3,"0");
			}else {
				codigoVerbaFinancimento = StringUtils.leftPad("777",3,"0");
			}

			StringBuffer linhaArquivo = new StringBuffer();
			linhaArquivo = linhaArquivo.append(TIPO_REGISTRO_CORPO);
			linhaArquivo = linhaArquivo.append(nomeDoLote);
			linhaArquivo = linhaArquivo.append(ESTABELECIMENTO);
			linhaArquivo = linhaArquivo.append(codigoEmpresa);
			linhaArquivo = linhaArquivo.append(prontuario);
			linhaArquivo = linhaArquivo.append(competencia);
			linhaArquivo = linhaArquivo.append(dataBase);
			linhaArquivo = linhaArquivo.append(codigoVerbaFinancimento);
			linhaArquivo = linhaArquivo.append(valorCobradoFormatado);
			linhaArquivo = linhaArquivo.append(aliquotaFormatado);
			linhaArquivo = linhaArquivo.append(CALCULO);
			linhaArquivo = linhaArquivo.append(QUANTIDADE_DE_PARCELAS);
			linhaArquivo = linhaArquivo.append(FILTER);
			
			
			conta.getArquivoEnvio().addConteudo(linhaArquivo, valorFinanciamento);
		}
		
		if(valorCoparticipacao != null && valorCoparticipacao.compareTo(BigDecimal.ZERO) > 0){
			final String codigoVerbaCoParticipacao;
			if(conta.isCobrancaFuncionarioURBEfetivo()) {
				codigoVerbaCoParticipacao = StringUtils.leftPad("879",3,"0");
			}else {
				codigoVerbaCoParticipacao  = StringUtils.leftPad("755",3,"0");
			}
			
			StringBuffer linhaArquivo = new StringBuffer();
			linhaArquivo = linhaArquivo.append(TIPO_REGISTRO_CORPO);
			linhaArquivo = linhaArquivo.append(nomeDoLote);
			linhaArquivo = linhaArquivo.append(ESTABELECIMENTO);
			linhaArquivo = linhaArquivo.append(codigoEmpresa);
			linhaArquivo = linhaArquivo.append(prontuario);
			linhaArquivo = linhaArquivo.append(competencia);
			linhaArquivo = linhaArquivo.append(dataBase);
			linhaArquivo = linhaArquivo.append(codigoVerbaCoParticipacao);
			linhaArquivo = linhaArquivo.append(valorCoParticipacaoFormatado);
			linhaArquivo = linhaArquivo.append(aliquotaFormatado);
			linhaArquivo = linhaArquivo.append(CALCULO);
			linhaArquivo = linhaArquivo.append(QUANTIDADE_DE_PARCELAS);
			linhaArquivo = linhaArquivo.append(FILTER);
			
			conta.getArquivoEnvio().addConteudo(linhaArquivo, valorCoparticipacao);
		}

	}
	
	
	public String getIdentificador(ArquivoDeEnvioConsignacao arquivo) {
//		Numero fixo.arquivo Campo de 1 caraceter que identifica a linha do registro como identificador
		final String tipoRegistro = "0";
		
//		 Campo de 3 caracteres. Guarda o código da empresa do segurado				
		String empresa = StringUtils.rightPad(arquivo.getEmpresa().getCodigoLegado(),3 ," ");
		
//		Campo fixo de 8 caracteres que indica o usuario
		String usuario = StringUtils.rightPad("RHMASTER",8 ," ");
		
		
		StringBuffer identificador = new StringBuffer();
		identificador.append(tipoRegistro);
		identificador.append(empresa);
		identificador.append(usuario);
		identificador.append(System.getProperty("line.separator"));
		return identificador.toString();
	}
	

	public String getCabecalho(ArquivoDeEnvioConsignacao arquivo) {
//		 Numero fixo. Campo de 1 caracter que identifica que a linha do registro é o cabeçalho
		final String tipoRegistro = "1"; 
//		Campo de 10 caracteres
		String nomeDoLote = "WSAUDREC" + arquivo.getEmpresa().getCodigoLegado();
		nomeDoLote = StringUtils.rightPad(nomeDoLote, 10, " ");
//		Campo de 4 carateres. Contém somente espaços em branco.
		final String filler1 = StringUtils.rightPad("",4 , " ");
//		 Campo de 3 caracteres. Guarda o código da empresa do segurado		
		final String empresa = StringUtils.rightPad(arquivo.getEmpresa().getCodigoLegado(),3 ," ");
//		Campo de 21 carateres. Contém somente espaços em branco.		
		final String filler2 = StringUtils.rightPad("",21 , " ");
//		Campo de 39 carateres. Contém somente zeros.		
		final String filler3 = StringUtils.rightPad("",39 , "0");
		
		StringBuffer cabecalho = new StringBuffer();
		cabecalho.append(tipoRegistro);
		cabecalho.append(nomeDoLote);
		cabecalho.append(filler1);
		cabecalho.append(empresa);
		cabecalho.append(filler2);
		cabecalho.append(filler3);
		cabecalho.append(System.getProperty("line.separator"));
		return cabecalho.toString();
	}

	public String getCorpo(ArquivoDeEnvioConsignacao arquivo, StringBuilder conteudo) {
		int numeroLinhasConteudo = arquivo.getConsignacoes().size();
		StringBuffer corpo = new StringBuffer();
		int contador = 1;
		corpo.append(conteudo);

		corpo.append(System.getProperty("line.separator"));
		return corpo.toString();

	}

	public String getRodape(ArquivoDeEnvioConsignacao arquivo, int quantidadeCobrancas, BigDecimal valorTotalRemessa) {
		StringBuffer rodape = new StringBuffer();
		String totalDeLinhas = String.valueOf(quantidadeCobrancas + 2);
		totalDeLinhas = StringUtils.leftPad(totalDeLinhas, 7, "0");
		String filler = StringUtils.rightPad("",70," ");
		rodape.append("9");
		rodape.append(filler);
		rodape.append(totalDeLinhas);
		return rodape.toString();
	}
	
	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	
	public Empresa getEmpresa() {
		return empresa;
	}
	
	public static void main(String[] args) {
		BigDecimal aliquota = new BigDecimal(44444443.45555);
		System.out.println((DECIMAL_FORMAT_ALIQUOTA).format(aliquota));
	}

	private static void leftPad(StringBuffer string,int size,String padChar) {
		while(string.length() < size){
			string.insert(0, padChar);
		}
	}

}
