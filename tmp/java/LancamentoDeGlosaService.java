package br.com.infowaypi.ecarebc.financeiro.faturamento;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Service para lançamentos de glosas e reativação de guias.  
 */
public class LancamentoDeGlosaService {

	public final static String MOTIVO_GLOSA_OUTROS = "Outros";
	public final static String MOTIVO_GLOSA_EXAME_NAO_REALIZADO = "Exame não foi realizado";
	public final static String MOTIVO_GLOSA_GUIA_SEM_ASSINATURA_DO_PRESTADOR = "Guia sem assinatura do prestador";
	public final static String MOTIVO_GLOSA_GUIA_SEM_ASSINATURA_DO_SEGURADO = "Guia sem assinatura do segurado";
	public final static String MOTIVO_GLOSA_EXAME_COM_RESULTADO_INCOMPLETO = "Exame com resultado incompleto";
	
	public ResumoGuias buscarGuias(SeguradoInterface segurado, String autorizacao) throws ValidateException {
		List<GuiaSimples> guias = new ArrayList<GuiaSimples>();
	
		if(!Utils.isStringVazia(autorizacao)){
			
			GuiaSimples guiaEncontrada = this.buscarGuias(autorizacao);
			
			if(guiaEncontrada == null)
				throw new ValidateException("Guia inválida!");
			guias.add(guiaEncontrada);
		}
		else if(segurado != null){
			guias.addAll(segurado.getGuias());
		}
		else {
			throw new ValidateException("Informe algum parâmetro.");
		}
		
		if(guias == null || guias.isEmpty())
			throw new ValidateException("Nenhuma guia foi encontrada.");
		
		List<GuiaSimples> guiasFiltradas = new ArrayList<GuiaSimples>();
		for(GuiaSimples guia : guias){
			boolean isGuiaAgendada = guia.isSituacaoAtual(SituacaoEnum.ABERTO.descricao());
			boolean isGuiaConfirmada = guia.isSituacaoAtual(SituacaoEnum.CONFIRMADO.descricao());
			boolean isNotGuiaDeFaturamentoCancelado = 
				((guia.isSituacaoAtual(SituacaoEnum.FATURADA.descricao())) && !(guia.getFaturamento().getStatus() == 'C'));
			
			if(isGuiaAgendada || isGuiaConfirmada || isNotGuiaDeFaturamentoCancelado){
				if(guia.isPossuiProcedimentosAtivos()){
					guia.tocarObjetos();
					guiasFiltradas.add(guia);
				}
			}
		}
		
		if(guiasFiltradas.isEmpty())
			throw new ValidateException("Não há guias para serem glosadas.");

		return new ResumoGuias<GuiaSimples>(guiasFiltradas, ResumoGuias.SITUACAO_TODAS, true);
	}
	
	public GuiaSimples buscarGuias(String autorizacao) throws ValidateException {
		if(Utils.isStringVazia(autorizacao))
			throw new ValidateException(MensagemErroEnum.AUSENCIA_DE_PARAMETROS.getMessage());
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("autorizacao", autorizacao));
		List<GuiaSimples> guias = sa.list(GuiaSimples.class);
		
		if(!guias.isEmpty())
			return guias.get(0);
		else
			throw new ValidateException(MensagemErroEnum.GUIA_NAO_ENCONTRADA.getMessage(autorizacao));
		
		}
	
	public GuiaSimples glosarProcedimentosGuia(String motivo, Collection<Procedimento> procedimentos, GuiaSimples guia, UsuarioInterface usuario) throws Exception {
		if(guia == null)
			throw new ValidateException("Guia inválida!");
		
		if(Utils.isStringVazia(motivo))
			throw new ValidateException("O motivo deve ser preenchido!");

		if(procedimentos == null || procedimentos.isEmpty())
			throw new ValidateException("Selecione um ou mais procedimentos!");

		if(!guia.isGuiaValidaParaGlosa())
			throw new ValidateException("A guia não pode ser utilizada para glosa. Ela está na situação " + guia.getSituacao().getDescricao() + "!");
		
		
		BigDecimal valorAtualGuia = guia.getValorTotal();
		BigDecimal valorProcedimentosGlosados = BigDecimal.ZERO;
		
		for(ProcedimentoInterface procedimento : procedimentos){
			if(!procedimento.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao()) &&
			   !procedimento.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())){
				valorProcedimentosGlosados = valorProcedimentosGlosados.add(procedimento.getValorTotal());
				procedimento.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), motivo, new Date());
				ImplDAO.save(procedimento);
			}
			else
				throw new ValidateException("O procedimento de código " + procedimento.getProcedimentoDaTabelaCBHPM().getCodigo() + " não pode ser glosado porque sua situação é " + procedimento.getSituacao().getDescricao());
		}
		guia.setValorTotal(valorAtualGuia.subtract(valorProcedimentosGlosados));
//		BigDecimal valorGuiaAtual = guia.getValorTotal();
//		guia.recalcularValores();
		BigDecimal diferenca = valorAtualGuia.subtract(guia.getValorTotal()); 
		ImplDAO.save(guia);
		
		if(guia.getFaturamento() != null){
			BigDecimal valorFaturamento = guia.getFaturamento().getValorBruto();
			
			guia.getFaturamento().setValorBruto(MoneyCalculation.rounded(valorFaturamento.subtract(diferenca)));
//			guia.getFaturamento().processarRetencoes();
			ImplDAO.save(guia.getFaturamento());
		}
		
		guia.tocarObjetos();
		return guia;
	}

	//TODO Exibir só os procedimentos GLOSADOS.
	//TODO Criar nova Guia?
	@Deprecated
	public GuiaSimples reativarProcedimentosGuia(String motivo, GuiaSimples guia, Collection<Procedimento> procedimentos, UsuarioInterface usuario) throws Exception {
		if(guia == null)
			throw new ValidateException("Guia inválida!");
		
		if(Utils.isStringVazia(motivo))
			throw new ValidateException("O motivo deve ser preenchido!");
		
//		if(!guia.isGuiaValidaParaGlosa()){
//			throw new ValidateException("A guia não pode ser utilizada para glosa. Ela está na situação " + guia.getSituacao().getDescricao() + "!");
//		}
		
		for(ProcedimentoInterface procedimento : procedimentos){
			if(procedimento.getSituacao().getDescricao().equals(SituacaoEnum.GLOSADO.descricao())){
				procedimento.mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(), motivo, new Date());
				ImplDAO.save(procedimento);
			}
		}

		guia.getValorTotal();
		ImplDAO.save(guia);
		
		return guia;
	}
	
	public GuiaSimples selecionarGuia(GuiaSimples guia) throws Exception {
		Assert.isNotNull(guia, "Guia Inválida.");			
		guia.tocarObjetos();
		return guia;		
	}
	
	public void finalizar(GuiaSimples guia){}

}