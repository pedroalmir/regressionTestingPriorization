package br.com.infowaypi.ecare.services.suporte;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import br.com.infowaypi.ecare.atendimentos.TipoGuiaEnum;
import br.com.infowaypi.ecare.resumos.ResumoParametro;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.atendimentos.validators.EnumFlowValidator;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * Esta classe � respons�vel por verificar todas as valida��es para mudan�a de situa��o de uma guia.
 * E estando tudo certo quanto �s valida��es, muda a situa��o da guia.
 * 
 * @author Luciano Rocha
 *
 */
public class MudarSituacaoGuiaService extends Service {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <G extends GuiaSimples> ResumoGuias buscarGuias(String autorizacao,
			byte[] conteudo) throws Exception {
		List<String> todasAutorizacoes = new ArrayList<String>();

		if (autorizacao != null) {
			todasAutorizacoes.add(autorizacao);
		} else if (conteudo != null) {
			Scanner sc = new Scanner(new ByteArrayInputStream(conteudo));
			String autorizacaoDoArquivo = "";
			while (sc.hasNext()) {
				autorizacaoDoArquivo = sc.nextLine().replace(" ", "");
				todasAutorizacoes.add(autorizacaoDoArquivo);
			}
		} else {
			throw new ValidateException("Informe pelo menos um par�metro.");
		}

		SearchAgent sa = new SearchAgent();
		sa.addParameter(new In("autorizacao", todasAutorizacoes));
		List<GuiaSimples<ProcedimentoInterface>> guias = sa.list(GuiaSimples.class);

		ResumoGuias<GuiaSimples<ProcedimentoInterface>> resumo = new ResumoGuias<GuiaSimples<ProcedimentoInterface>>();
		resumo.getGuias().addAll(guias);

		if (resumo.getGuias().isEmpty()){
			throw new RuntimeException(MensagemErroEnum.NENHUMA_GUIA_ENCONTRADA.getMessage());
		}

		for (GuiaSimples<ProcedimentoInterface> guiaDoResumo : resumo.getGuias()) {
			guiaDoResumo.getColecaoSituacoes().getSituacoes().size();
		}

		return resumo;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <G extends GuiaSimples> ResumoGuias selecionarGuias(String novaSituacao,
			String motivo, Date dataAtendimento,Date dataTerminoAtendimento,Date dataRecebimento,
			Collection<GuiaSimples<ProcedimentoInterface>> guiasSelecionadas,
			UsuarioInterface usuario) throws Exception {
		
		validaNulidadeGuias(guiasSelecionadas);
		validaNulidadeMotivoMudanca(motivo);
		
		ResumoGuias<GuiaSimples<ProcedimentoInterface>> resumo = new ResumoGuias<GuiaSimples<ProcedimentoInterface>>();
		resumo.getGuiasFiltradas().addAll(guiasSelecionadas);

		removeGuiasIrregulares(novaSituacao, resumo);

		for (GuiaSimples<ProcedimentoInterface> guiaSimples : resumo.getGuiasFiltradas()) {
			validarDataAtendimento(guiaSimples, dataAtendimento);
			ResumoParametro resumoParametro = new ResumoParametro(guiaSimples,dataAtendimento, dataTerminoAtendimento, dataRecebimento, novaSituacao);
			validarMudancaDeSituacao(resumoParametro, novaSituacao);
			EnumFlowValidator.DATA_TERMINO_ATENDIMENTO_GUIA.getValidator().execute(resumoParametro);	
			EnumFlowValidator.DATA_RECEBIMENTO_GUIA.getValidator().execute(resumoParametro);
			guiaSimples.mudarSituacao(usuario, novaSituacao, motivo, new Date());
		}
		return resumo;
	}
	/**
	 * Este m�todo tem a finalidade de verificar se o motivo para mudan�a de situa��o n�o foi preenchido e se alguma guia foi selecionada 
	 * para mudan�a de situa��o.
	 * @param guiasSelecionadas
	 * @param motivo
	 * @throws ValidateException
	 */
	public void validaNulidadeGuias(Collection<GuiaSimples<ProcedimentoInterface>> guiasSelecionadas) throws ValidateException{
		if (guiasSelecionadas == null || guiasSelecionadas.size() < 1) {
			throw new ValidateException("Selecione alguma guia para mudar a situa��o.");
		}
		
	}
	
	public void validaNulidadeMotivoMudanca(String motivo) throws Exception {
		if (motivo == null) {
			throw new ValidateException("O motivo da mudan�a de situa��o da guia � obrigat�rio.");
		}
	}
	
	/**
	 * Este m�todo tem a finalidade de verificar se a situa��o da guia mudou.
	 * @author Luciano Rocha
	 * @param resumo
	 * @param novaSituacao
	 * @throws ValidateException
	 */
	public void validarMudancaDeSituacao(ResumoParametro resumo,  String novaSituacao) throws ValidateException {
		boolean dontChanged = resumo.getGuia().getSituacao().getDescricao().equals(novaSituacao);
		if (dontChanged) {
			throw new ValidateException("A guia j� se encontra na situa��o "+novaSituacao+".");	
		}
	}

	private void removeGuiasIrregulares(String situacao,
			ResumoGuias<GuiaSimples<ProcedimentoInterface>> resumo) {
		boolean isFaturadaOuPaga;
		boolean possuiSituacaoInformadaNaMaquinaDeEstados;
		String motivoNaoPossuiSituacaoNaMaquinaDeEstados = "Guia n�o possui a situa��o "
			+ situacao + " na sua m�quina de estados.";
		String motivoFaturadaOuPaga = "Guia est� faturada ou paga e n�o pode ter sua situa��o alterada.";

		Iterator<GuiaSimples<ProcedimentoInterface>> iterator = resumo.getGuiasFiltradas().iterator();
		resumo.getGuias().clear();
		
		while (iterator.hasNext()) {
			GuiaSimples<ProcedimentoInterface> guiaSimples = iterator.next();
			isFaturadaOuPaga = guiaSimples.isSituacaoAtual(SituacaoEnum.FATURADA.descricao())|| guiaSimples.isSituacaoAtual(SituacaoEnum.PAGO.descricao());
			
			possuiSituacaoInformadaNaMaquinaDeEstados = TipoGuiaEnum.getTipoDeGuia(guiaSimples.getTipoDeGuia())
																	.getSituacoesPossiveis().contains(situacao);
			
			if (isFaturadaOuPaga) {
				iterator.remove();
				// como essas guias n�o ser�o salvas, o motivo pra glosa total
				// vai servir s� pra mostrar na tela
				guiaSimples.setMotivoParaGlosaTotal(motivoFaturadaOuPaga);
				// vai ser a colecao de guias excluidas por estarem faturadas ou
				// pagas
				resumo.getGuias().add(guiaSimples);
			} else if (!possuiSituacaoInformadaNaMaquinaDeEstados) {
				iterator.remove();
				// como essas guias n�o ser�o salvas, o motivo pra glosa total
				// vai servir s� pra mostrar na tela
				guiaSimples.setMotivoParaGlosaTotal(motivoNaoPossuiSituacaoNaMaquinaDeEstados);
				// vai ser a colecao de guias excluidas por estarem faturadas ou
				// pagas
				resumo.getGuias().add(guiaSimples);
			}
		}
	}

	private void validarDataAtendimento(GuiaSimples<ProcedimentoInterface> guiaSimples, Date dataAtendimentoInput)
	throws ValidateException {
		boolean dataDeAtendimentoGuiaNula = guiaSimples.getDataAtendimento() == null;
		if (dataDeAtendimentoGuiaNula) {
			if (dataAtendimentoInput == null) {
				throw new ValidateException(
						MensagemErroEnum.DATA_ATENDIMENTO_NAO_PREENCHIDA_PARA_MUDANCA_SITUACAO.getMessage());
			} else {
				guiaSimples.setDataAtendimento(dataAtendimentoInput);
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public <G extends GuiaSimples> ResumoGuias conferirDados(
			ResumoGuias<GuiaSimples<ProcedimentoInterface>> resumo)
	throws Exception {
		for (GuiaSimples<ProcedimentoInterface> guiaSimples : resumo.getGuiasFiltradas()) {
			ImplDAO.save(guiaSimples);
		}
		return resumo;
	}

	public void finalizar(GuiaInternacao guia) {
	}

}
