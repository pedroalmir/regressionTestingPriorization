package br.com.infowaypi.ecarebc.portalTiss;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.XMLGregorianCalendar;

import br.com.infowaypi.ans.tiss.MensagemTISS;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Resumo do Flow @FlowUploadArquivoTiss
 * @author Emanuel
 *
 */
public class ResumoUploadArquivoTiss {

	private ArquivoTiss arquivoLoteGuias;
	private ArquivoTiss arquivoProtocoloRetorno;
	private MensagemTISS mensagemLoteDeGuias;
	private MensagemTISS mensagemProtocoloRecebimento;
	
	public ResumoUploadArquivoTiss(ArquivoTiss arquivoLoteGuias, ArquivoTiss arquivoProtocoloRetorno, MensagemTISS mensagemLoteDeGuias, MensagemTISS mensagemProtocoloRecebimento) {
		this.arquivoLoteGuias = arquivoLoteGuias;
		this.arquivoProtocoloRetorno = arquivoProtocoloRetorno;
		this.mensagemLoteDeGuias = mensagemLoteDeGuias;
		this.mensagemProtocoloRecebimento = mensagemProtocoloRecebimento;
	}
	
	public ArquivoTiss getArquivoLoteGuias() {
		return arquivoLoteGuias;
	}

	public void setArquivoLoteGuias(ArquivoTiss arquivoLoteGuias) {
		this.arquivoLoteGuias = arquivoLoteGuias;
	}

	public ArquivoTiss getArquivoProtocoloRetorno() {
		return arquivoProtocoloRetorno;
	}

	public void setArquivoProtocoloRetorno(ArquivoTiss arquivoProtocoloRetorno) {
		this.arquivoProtocoloRetorno = arquivoProtocoloRetorno;
	}

	public MensagemTISS getMensagemLoteDeGuias() {
		return mensagemLoteDeGuias;
	}

	public void setMensagemLoteDeGuias(MensagemTISS mensagemLoteDeGuias) {
		this.mensagemLoteDeGuias = mensagemLoteDeGuias;
	}

	public MensagemTISS getMensagemProtocoloRecebimento() {
		return mensagemProtocoloRecebimento;
	}

	public void setMensagemProtocoloRecebimento(MensagemTISS mensagemProtocoloRecebimento) {
		this.mensagemProtocoloRecebimento = mensagemProtocoloRecebimento;
	}

	public String getPrestador() throws JAXBException, IOException{
		return getMensagemProtocoloRecebimento().getOperadoraParaPrestador().getProtocoloRecebimento().getProtocoloRecebimento().getDadosPrestador().getNomeContratado();
	}

	public String getNome() throws JAXBException, IOException{
		return this.arquivoLoteGuias.getTituloArquivo();
	}

	public String getTipoArquivo() throws JAXBException, IOException{
		MensagemTISS m = getMensagemLoteDeGuias();
		return m.getCabecalho().getIdentificacaoTransacao().getTipoTransacao().name();
	}
	
	public BigInteger getNumeroLote(){
		return getMensagemLoteDeGuias().getPrestadorParaOperadora().getLoteGuias().getNumeroLote();
	}
	
	public int getQtdeGuias() throws JAXBException, IOException{
		int quant = 0;
		if(!getMensagemLoteDeGuias().getPrestadorParaOperadora().getLoteGuias().getGuias().getGuiaFaturamento().getGuiaSPSADT().isEmpty()){
			quant = getMensagemLoteDeGuias().getPrestadorParaOperadora().getLoteGuias().getGuias().getGuiaFaturamento().getGuiaSPSADT().size();
		}
		if(!getMensagemLoteDeGuias().getPrestadorParaOperadora().getLoteGuias().getGuias().getGuiaFaturamento().getGuiaResumoInternacao().isEmpty()){
			quant = getMensagemLoteDeGuias().getPrestadorParaOperadora().getLoteGuias().getGuias().getGuiaFaturamento().getGuiaResumoInternacao().size();
		}
		return quant;
	}
	
	public String getNumeroProtocolo(){
		return getMensagemProtocoloRecebimento().getOperadoraParaPrestador().getProtocoloRecebimento().getProtocoloRecebimento().getNumeroProtocoloRecebimento();
	}

	public String getDataEnvio(){
		XMLGregorianCalendar dataRegistroTransacao = getMensagemProtocoloRecebimento().getCabecalho().getIdentificacaoTransacao().getDataRegistroTransacao();
		XMLGregorianCalendar horaRegistroTransacao = getMensagemProtocoloRecebimento().getCabecalho().getIdentificacaoTransacao().getHoraRegistroTransacao();
		Date parseData = Utils.parse(dataRegistroTransacao + "", "yyyy-MM-dd");
		String dataRegistro = Utils.format(parseData,"dd/MM/yyyy");
		return dataRegistro + " " + horaRegistroTransacao;
	}
	
	public String getStatus(){
		return "Aguardando processamento.";
	}
	

	
}
