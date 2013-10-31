package br.com.infowaypi.ecarebc.utils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.com.infowaypi.ecarebc.atendimentos.GuiaAcompanhamentoAnestesico;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoAnestesico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;

/**
 * Classe utilitária para ações em procedimentos de uma guia
 * @author Josino Rodrigues
 * @changes Danilo Portela
 */
public class ProcedimentoUtils {

	/**
	 * Aplica um desconto no valor dos procedimentos a partir da porcentagem do profissional responsável pelo mesmo
	 * @param procedimentosCirurgicos
	 */
	public static void aplicaDescontoDaViaDeAcesso(GuiaCompleta<ProcedimentoInterface> guia) {
		for (ProcedimentoCirurgico procedimento : guia.getProcedimentosCirurgicos()) {
			if (procedimento.getPorcentagem() == null){
				procedimento.setPorcentagem(new BigDecimal(100));
			}
			
			if(guia.isCirurgiaOdonto()){
				procedimento.setPorcentagem(ProcedimentoCirurgico.PORCENTAGEM_30);
			}
			
			if(procedimento.getIdProcedimento() == null){
				BigDecimal valorTotal = procedimento.getValorAtualDoProcedimento();
				BigDecimal porcentagem = procedimento.getPorcentagem().divide(new BigDecimal(100));
				BigDecimal valorAtualizado = valorTotal.multiply(porcentagem);
				procedimento.setValorAtualDoProcedimento(valorAtualizado);
			}
			
			if (procedimento.getGuia().isInternacao()){
				calculaCamposConsiderandoPorcentagem(procedimento);
			}
			guia.corrigeValorProcedimentoInclusoEmPacoteQueInclueHonorario(procedimento);
		}
		CommandCorrecaoValorGuia command = new CommandCorrecaoValorGuia(guia);
		command.execute();
	}

	public static void calculaCamposConsiderandoPorcentagem(ProcedimentoInterface procedimento) {
		aplicarValorDoAcordo(procedimento);
		calcularPorcentagem(procedimento);
	}
		
	private static void aplicarValorDoAcordo(ProcedimentoInterface procedimento) {
		//recalcula valor do procedimento e aplica acordo se tiver algum
		procedimento.setValorAtualDoProcedimento(BigDecimal.ZERO);
		procedimento.setValorCoParticipacao(BigDecimal.ZERO);
		procedimento.calcularCampos();
		procedimento.aplicaValorAcordo();
	}
		
	public  static void calcularPorcentagem(ProcedimentoInterface procedimento) {
		BigDecimal porcentagem = procedimento.getPorcentagem().movePointLeft(2);//divide(new BigDecimal(100));
		BigDecimal valorAtualizado = procedimento.getValorTotal().multiply(porcentagem);
		procedimento.setValorAtualDoProcedimento(valorAtualizado);
	}
	
	/**
	 * Aplica descontos da via de acesso com calculo dobrado do valor atual para procedimentos do capítulo 3.
	 * 
	 * @param guia guia
	 */
	public static void aplicaDescontosDaViaDeAcessoComCalculoDobrado(GuiaCompleta<ProcedimentoInterface> guia) {
	    
	    CommandCorrecaoCalculoValorProcedimento cmd = new CommandCorrecaoCalculoValorProcedimento(guia);
	    cmd.execute();

	    for (ProcedimentoCirurgico procedimento : guia.getProcedimentosCirurgicos()) {
		if (procedimento.getPorcentagem() == null){
		    procedimento.setPorcentagem(new BigDecimal(100));
		}

		if(guia.isCirurgiaOdonto()){
		    procedimento.setPorcentagem(ProcedimentoCirurgico.PORCENTAGEM_30);
		}

		if(procedimento.getIdProcedimento() == null){
		    BigDecimal valorTotal = procedimento.getValorAtualDoProcedimento();
		    BigDecimal porcentagem = procedimento.getPorcentagem().divide(new BigDecimal(100));
		    BigDecimal valorAtualizado = valorTotal.multiply(porcentagem);
		    procedimento.setValorAtualDoProcedimento(valorAtualizado);
		}

		if (procedimento.getGuia().isInternacao()){

		    procedimento.aplicaValorAcordo();//nao zera e recalcula valor

		    calcularPorcentagem(procedimento);
		}
		guia.corrigeValorProcedimentoInclusoEmPacoteQueInclueHonorario(procedimento);
	    }

	    CommandCorrecaoValorGuia command = new CommandCorrecaoValorGuia(guia);
	    command.execute();
	}
	
	/**
	 * Indica se um item da tabela cbhpm refere-se a um atendimento ao recem nascido.
	 * 
	 * @param procedimento
	 * @return
	 */
	public static boolean isAtendimentoAoRecemNascido(TabelaCBHPM procedimento) {
		return procedimento.getCodigo().startsWith("101030");
	}
	
	/**
	 * Filtra a coleção de procedimentos passados, retornando apenas os da herança da classe passada
	 * @param <T>
	 * @param klassProcedimento
	 * @param collection
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends ProcedimentoInterface> Set<T> getProcedimentosDaHeranca(
			Class<T> klassProcedimento,
			Collection<? extends ProcedimentoInterface> collection) {
		Set<T> procedimentos = new HashSet<T>();
		for (ProcedimentoInterface procedimento : collection) {
			if (klassProcedimento.isInstance(procedimento)){
				procedimentos.add((T)procedimento);
			}	
		}
		return procedimentos;
	}
	
	/**
	 * Filtra a coleção de procedimentos passados, retornando apenas os exatamente da classe passada
	 * @param <T>
	 * @param klassProcedimento
	 * @param collection
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends ProcedimentoInterface> Set<T> getProcedimentosDaClasse(
			Class<T> klassProcedimento,
			Collection<? extends ProcedimentoInterface> collection) {
		Set<T> procedimentos = new HashSet<T>();
		for (ProcedimentoInterface procedimento : collection) {
			if (procedimento.getClass().equals(klassProcedimento)){
				procedimentos.add((T)procedimento);
			}
		}
		return procedimentos;
	}
	
	/**
	 * Método utilizado para calcular o valor de taxa de vídeo e taxa de urgência nos procedimentos já contidos na GAA.
	 * @param guia
	 */
	public static void calcularVideoEHorarioEspecial(ProcedimentoInterface procedimento) {
			
		BigDecimal valor = procedimento.getValorAtualDoProcedimento();
		if (procedimento.isIncluiVideo() != null && procedimento.isIncluiVideo()){
			valor = valor.multiply(new BigDecimal(1.5).setScale(2, BigDecimal.ROUND_HALF_UP));
		}
		
		if (procedimento.isHorarioEspecial()){
			valor = valor.multiply(new BigDecimal(1.3).setScale(2, BigDecimal.ROUND_HALF_UP));
		}
		
		procedimento.setValorAtualDoProcedimento(valor);
		
	}

	/**
	 * Método utilizado para verificar se um procedimento foi autorizado,
	 * geralmente utilizado via delegação pelas entidades que implementam a interface criticável.
	 * @param situacao
	 * @return
	 */
	public static boolean isAutorizado(String situacao){
		if(SituacaoEnum.AUTORIZADO.descricao().equals(situacao) || SituacaoEnum.PRE_AUTORIZADO.descricao().equals(situacao)){
			return true;
		}
		return false;
	}
	
	public static boolean isSolicitado(String situacao){
		if(SituacaoEnum.SOLICITADO.descricao().equals(situacao) ){
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static void ajustarAPorcentagemDosProcedimentosDeAcordoComOPorte(GuiaAcompanhamentoAnestesico guia) throws Exception {
		Set<ProcedimentoAnestesico> procedimentos = guia.getProcedimentosNaoGlosadosNemCanceladosNemNegados();
		Map<ProcedimentoAnestesico, Integer> mapProcedimentos = mapearOsProcedimentosDeAcordoComSeusPortes(procedimentos);
		ProcedimentoAnestesico procDeMaiorPorte = elegerOProcedimentoDeMaiorPorte(mapProcedimentos);

		for (ProcedimentoAnestesico proc :  mapProcedimentos.keySet()) {
			if (procDeMaiorPorte != null  && proc.equals(procDeMaiorPorte)){
				proc.setPorcentagemProcedimentoAnestesico(new BigDecimal(100));
			}else {
				proc.setPorcentagemProcedimentoAnestesico(new BigDecimal(50));
			}
			aplicarPorcentagem(proc);
			proc.calcularCampos();
			proc.aplicaValorAcordo();
		}
	}
	
	private static void aplicarPorcentagem(ProcedimentoAnestesico procedimento) {
		BigDecimal porcentagem = procedimento.getPorcentagemProcedimentoAnestesico().movePointLeft(2);
		BigDecimal valorComPorcentagemAplicada = procedimento.getProcedimentoDaTabelaCBHPM().getValorModerado().multiply(porcentagem);
		procedimento.setValorAtualDoProcedimento(valorComPorcentagemAplicada);
	}
	
	/**
	 * @param mapProcedimentos
	 * @param keySet
	 * @return
	 */
	private static ProcedimentoAnestesico elegerOProcedimentoDeMaiorPorte(Map<ProcedimentoAnestesico, Integer> mapProcedimentos) {
		Integer maiorPorte = 0;
		ProcedimentoAnestesico procDeMaiorPorte = null;

		for (ProcedimentoAnestesico proc : mapProcedimentos.keySet()) {
			Integer porte = mapProcedimentos.get(proc);
			if (porte > maiorPorte){
				procDeMaiorPorte = proc;
				maiorPorte= porte;
			}
		}
		return procDeMaiorPorte;
	}
	
	private static Map<ProcedimentoAnestesico, Integer> mapearOsProcedimentosDeAcordoComSeusPortes(Collection<ProcedimentoAnestesico> procedimentos) {
		Map<ProcedimentoAnestesico, Integer> mapProcedimentos = new HashMap<ProcedimentoAnestesico, Integer>();
		for (ProcedimentoAnestesico procedimento : procedimentos) {
			mapProcedimentos.put(procedimento, procedimento.getProcedimentoDaTabelaCBHPM().getPorteAnestesicoFormatado());
		}
		return mapProcedimentos;
	}
	
}
