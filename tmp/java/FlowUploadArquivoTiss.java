package br.com.infowaypi.ecarebc.portalTiss;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import br.com.infowaypi.ans.tiss.MensagemTISS;
import br.com.infowaypi.ecare.enums.TipoArquivoEnum;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.protocolo.ArquivoProtocoloRecebimento;
import br.com.infowaypi.upload.ImportarTiss;

/**
 * Esse fluxo faz a importacao do arquivo TISS, tipo Lote de Guias.
 * @author Emanuel
 *
 */
public class FlowUploadArquivoTiss {

	public ResumoUploadArquivoTiss importarArquivo(byte[] arquivo, String nomeArquivo, UsuarioInterface usuario) throws Exception{
		
		if(arquivo == null){
			throw new Exception("O Arquivo TISS deve ser informado.");
		}
		
		//Fazer validacao com o prestador(falta registro ANS) e o beneficiario
		MensagemTISS mensagemLoteGuias = new ImportarTiss().getMensagemTissXml(new String(arquivo), false);
		
		//verificar no futuro alterar entre registroANS e codigo
		String codigoPrestadorNaOperadora = mensagemLoteGuias.getCabecalho().getOrigem().getCodigoPrestadorNaOperadora().getCodigoPrestadorNaOperadora();
		Long codigoPrestadorLong = null;
		try {
			codigoPrestadorLong = Long.parseLong(codigoPrestadorNaOperadora);
		} catch (Exception e) {
			throw new Exception("O codigoPrestadorNaOperadora não está com um numero válido: " + codigoPrestadorNaOperadora);
		}
		
		Prestador prestador = ImplDAO.findById(codigoPrestadorLong, Prestador.class);
		Assert.isNotNull(prestador, "O campo 'codigoPrestadorNaOperadora' ("+ codigoPrestadorNaOperadora + ") não foi encontrado em nosso sistema.");
		String nomePrestador = prestador.getPessoaJuridica().getFantasia();
		String numeroLote = mensagemLoteGuias.getPrestadorParaOperadora().getLoteGuias().getNumeroLote().toString();
		
		ArquivoTiss arquivoXml = new ArquivoTiss();
		arquivoXml.setTituloArquivo(nomeArquivo);
		arquivoXml.setArquivo(arquivo);
		arquivoXml.setTipoArquivo(TipoArquivoEnum.XML.getDescricao());
		arquivoXml.setDataCriacao(new Date());
		
		arquivoXml.setTipoTransacao(ArquivoTissEnum.ENVIO_LOTE_GUIAS.toString());
		arquivoXml.setPrestador(prestador);
		arquivoXml.setUsuario(usuario);
		
		ArquivoTiss arquivoRetornoXml = new ArquivoTiss();
		
		ArquivoProtocoloRecebimento arquivoProtocoloRecebimento = new ArquivoProtocoloRecebimento();
		
		byte[] retorno = arquivoProtocoloRecebimento.gerarArquivoXml(codigoPrestadorNaOperadora, nomePrestador, numeroLote);
		arquivoRetornoXml.setArquivo(retorno);
		arquivoRetornoXml.setTipoArquivo(TipoArquivoEnum.XML.getDescricao());
		arquivoRetornoXml.setDataCriacao(new Date());
		MensagemTISS mensagemProtocolo = arquivoProtocoloRecebimento.getMensagem();
		arquivoRetornoXml.setTituloArquivo(StringUtils.leftPad(mensagemProtocolo.getCabecalho().getIdentificacaoTransacao().getSequencialTransacao().toString(),20,"0") + "_" + mensagemProtocolo.getEpilogo().getHash());

		arquivoRetornoXml.setTipoTransacao(ArquivoTissEnum.PROTOCOLO_RECEBIMENTO.toString());
		arquivoRetornoXml.setPrestador(prestador);
		arquivoRetornoXml.setUsuario(usuario);
		
		arquivoXml.setArquivoRelacionado(arquivoRetornoXml);
		
		ImplDAO.save(arquivoXml);
		ImplDAO.save(arquivoRetornoXml);
		
		return new ResumoUploadArquivoTiss(arquivoXml, arquivoRetornoXml, mensagemLoteGuias, mensagemProtocolo);
	}
	
}
