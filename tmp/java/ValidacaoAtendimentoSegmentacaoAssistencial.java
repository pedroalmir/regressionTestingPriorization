package br.com.infowaypi.ecare.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.produto.Produto;
import br.com.infowaypi.ecarebc.produto.SegmentacaoAssistencial;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * Classe intermediária que pega os resultados do BuscarSegurado e
 * filtra os segurados de acordo com as segmentações Assistenciais
 * de dos produtos dos segurados retornados.
 * 
 * @author DANNYLVAN
 *
 */
public class ValidacaoAtendimentoSegmentacaoAssistencial {
	
	public static ResumoSegurados buscar(String cpfDoTitular,
			String numeroDoCartao, Class<? extends Segurado> tipoSegurado,
			boolean liberarSuspensos, SegmentacaoAssistencial... segmentacoes) throws ValidateException {
		
		ResumoSegurados resumo = BuscarSegurados.buscar(cpfDoTitular, numeroDoCartao, tipoSegurado, liberarSuspensos);
		verificarSegmentacaoByResumo(resumo, segmentacoes);
		return resumo;
	}
	
	public static ResumoSegurados buscarReimpressao(String cpfDoTitular,String numeroDoCartao, 
			Class<? extends Segurado> tipoSegurado, SegmentacaoAssistencial... segmentacoes) throws ValidateException {
		
		ResumoSegurados resumo = BuscarSegurados.buscarReimpressao(cpfDoTitular, numeroDoCartao, tipoSegurado);
		verificarSegmentacaoByResumo(resumo, segmentacoes);
		return resumo;
	}
	
	public static <S extends Segurado> List<S> getSegurados(String cartao,
			String cpf, Class<S> tipoSegurado, boolean inclueSuspenso,
			SegmentacaoAssistencial... segmentacoes) throws ValidateException {
		
		List<S> segurados = BuscarSegurados.getSegurados(cartao, cpf, tipoSegurado, inclueSuspenso);
		return filtrarSeguradosPorSegmentacao(segurados, segmentacoes);
	}
	
	private static ResumoSegurados verificarSegmentacaoByResumo(ResumoSegurados resumo, SegmentacaoAssistencial... segmentacoes) throws ValidateException{
		List<Segurado> seguradosAptosAtendimento = filtrarSeguradosPorSegmentacao(resumo.getSegurados(), segmentacoes);
		resumo.setSegurados(seguradosAptosAtendimento);
		return resumo;
	}

	private static <S extends Segurado> List<S> filtrarSeguradosPorSegmentacao(
			Collection<S> segurados, SegmentacaoAssistencial... segmentacoes) throws ValidateException {
		
		List<S> seguradosAptosAtendimento = new ArrayList<S>();
		for (S segurado : segurados) {
			boolean segmentacaoOK = verificarSegmentacaoProduto(segurado.getContratoAtual().getProduto(), segmentacoes);
			if (segmentacaoOK){
				seguradosAptosAtendimento.add(segurado);
			}
		}
		
		if (seguradosAptosAtendimento.isEmpty()){
			throw new ValidateException(MensagemErroEnum.SEGMENTACAO_INCOMPATIVEL.getMessage());
		}
		
		return seguradosAptosAtendimento;
	}
	
	public static void verificarSegmentacaoSegurado(AbstractSegurado segurados, SegmentacaoAssistencial... segmentacoes) throws ValidateException{
		if (!verificarSegmentacaoProduto(segurados.getContratoAtual().getProduto(), segmentacoes)){
			throw new ValidateException(MensagemErroEnum.SEGMENTACAO_INCOMPATIVEL.getMessage());
		}
	}
	
	/**
	 * Verifica se o produto contém alguma das segmentaçãoes Assistenciais passadas como parâmetro
	 */
	private static boolean verificarSegmentacaoProduto(Produto produto, SegmentacaoAssistencial... segmentacoes) throws ValidateException{
		Set<SegmentacaoAssistencial> segmentacoesProduto = produto.getSegmentacoesAssistenciais();
		for (SegmentacaoAssistencial segmentacaoParametro : segmentacoes) {
			if (segmentacoesProduto.contains(segmentacaoParametro)){
				return true;
			}
		}
		return false;
	}
}
