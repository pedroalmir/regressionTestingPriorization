package br.com.infowaypi.ecarebc.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecarebc.atendimentos.Diaria;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemGasoterapia;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemTaxa;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.GuiaFaturavel;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ItemGuiaFaturamento;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

public class GuiaUtils {


	public static BigDecimal getSomatorioProcedimentos(GuiaSimples<Procedimento> guia){
		BigDecimal somatorio = BigDecimal.ZERO;
		
		if(guia.isSituacaoAtual(SituacaoEnum.AUDITADO.descricao()) || guia.isSituacaoAtual(SituacaoEnum.FATURADA.descricao())){
			for (Procedimento procedimento : guia.getProcedimentos()) {
				if(procedimento.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao()))
					procedimento.mudarSituacao(guia.getSituacao().getUsuario(), SituacaoEnum.AUTORIZADO.descricao(), "Auditoria", guia.getSituacao().getDataSituacao());
				if(!procedimento.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()) 
					&& !procedimento.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao())
					&& !procedimento.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())){
					somatorio = somatorio.add(procedimento.getValorTotal());
				}
			}
		}else{
			for (Procedimento procedimento : guia.getProcedimentos()) {
				if(!procedimento.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()) 
						&& !procedimento.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao())){
					somatorio = somatorio.add(procedimento.getValorTotal());
				}
			}
		}
		
		return MoneyCalculation.rounded(somatorio);
	}
		
	public static BigDecimal getSomatorioDiarias(GuiaCompleta<ProcedimentoInterface> guia){
		
		BigDecimal somatorio = BigDecimal.ZERO;
		
		if(guia.isSituacaoAtual(SituacaoEnum.AUDITADO.descricao()) || guia.isSituacaoAtual(SituacaoEnum.FATURADA.descricao())){
			for (ItemDiaria diaria : guia.getItensDiaria()) {
				if(diaria.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao()))
					diaria.mudarSituacao(guia.getSituacao().getUsuario(), SituacaoEnum.AUTORIZADO.descricao(), "Auditoria", guia.getSituacao().getDataSituacao());
				
				if(!diaria.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()) 
						&& !diaria.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao())
						&& !diaria.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())){
					somatorio = somatorio.add(new BigDecimal(diaria.getValorTotal()));
				}
			}
		}else{
			for (ItemDiaria diaria : guia.getItensDiaria()) {
				if(!diaria.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()) 
						&& !diaria.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao())){
					somatorio = somatorio.add(new BigDecimal(diaria.getValorTotal()));
				}
			}
		}
		
		return MoneyCalculation.rounded(somatorio);
	}
	
	public static BigDecimal getSomatorioTaxas(GuiaCompleta<ProcedimentoInterface> guia){
		BigDecimal somatorio = BigDecimal.ZERO;
		for (ItemTaxa taxa : guia.getItensTaxa()) {
			if(!taxa.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()) 
				&& !taxa.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao())){
				somatorio = somatorio.add(new BigDecimal(taxa.getValorTotal()));
			}
		}
		return MoneyCalculation.rounded(somatorio);
	}
	
	public static BigDecimal getSomatorioGasoterapias(GuiaCompleta<ProcedimentoInterface> guia){
		BigDecimal somatorio = BigDecimal.ZERO;
		for (ItemGasoterapia gasoterapia : guia.getItensGasoterapia()) {
			if(!gasoterapia.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()) 
				&& !gasoterapia.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao())){
				
				somatorio = somatorio.add(new BigDecimal(gasoterapia.getValorTotal()));
			}
		}
		return MoneyCalculation.rounded(somatorio);
	}
	
	public static BigDecimal getSomatorioPacotes(GuiaCompleta<ProcedimentoInterface> guia){
		BigDecimal somatorio = BigDecimal.ZERO;
		for (ItemPacote pacote : guia.getItensPacote()) {
			if(!pacote.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())
				&& !pacote.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao())){
				somatorio = somatorio.add(new BigDecimal(pacote.getValorTotal()));
			}
		}
		return MoneyCalculation.rounded(somatorio);
	}
	
	public static BigDecimal getSomatorioValoresMatMed(GuiaCompleta<ProcedimentoInterface> guia){
		BigDecimal somatorio = BigDecimal.ZERO;
		if(guia.isSituacaoAtual(SituacaoEnum.AUDITADO.descricao()) || guia.isSituacaoAtual(SituacaoEnum.FATURADA.descricao()))
			somatorio = somatorio.add(guia.getValoresMatMed().getValorTotalAuditado());
		else 
			somatorio = somatorio.add(guia.getValoresMatMed().getValorTotalInformado());
		
		return MoneyCalculation.rounded(somatorio);
	}
	
	
	public static BigDecimal getSomatorioHonorariosMedicos(GuiaCompleta<ProcedimentoInterface> guia){
		BigDecimal somatorio = BigDecimal.ZERO;
		// recalcula-se o valor da guia baseado no porte dos procedimentos e profissionais inseridos
		if(guia.isSituacaoAtual(SituacaoEnum.AUDITADO.descricao()) || guia.isSituacaoAtual(SituacaoEnum.FATURADA.descricao())){
			for (ProcedimentoCirurgico procedimento : guia.getProcedimentosCirurgicos()) {
				if(!procedimento.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()) 
					&& !procedimento.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao())
					&& !procedimento.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())){
					
					if(procedimento.getProfissionalAuxiliar1() != null) {
						somatorio = somatorio.add(procedimento.getValorAuxiliar1());
					}
					if(procedimento.getProfissionalAuxiliar2() != null) {
						somatorio = somatorio.add(procedimento.getValorAuxiliar2());
					}
					if(procedimento.getProfissionalAuxiliar3() != null) {
						somatorio = somatorio.add(procedimento.getValorAuxiliar3());
					}
				}
			}
		}else {
			for (ProcedimentoCirurgico procedimento : guia.getProcedimentosCirurgicos()) {
				if(!procedimento.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()) 
						&& !procedimento.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao())){
					if(procedimento.getProfissionalAuxiliar1() != null) {
						somatorio = somatorio.add(procedimento.getValorAuxiliar1());
					}
					if(procedimento.getProfissionalAuxiliar2() != null) {
						somatorio = somatorio.add(procedimento.getValorAuxiliar2());
					}
					if(procedimento.getProfissionalAuxiliar3() != null) {
						somatorio = somatorio.add(procedimento.getValorAuxiliar3());
					}
				}
			}
		}
		
		return MoneyCalculation.rounded(somatorio);
	}
	
	/**
	 * 
	 * @param guia
	 * @param diarias
	 * @return retorna a quantidade de dias que o paciente ficou nas acomodações informadas
	 */
	public static int getQuantidadeDiarias(GuiaInternacao guia, Diaria... diarias){
		List diariasList = Arrays.asList(diarias);
		int count = 0;
		for (ItemDiaria item : guia.getDiariasAutorizadas()) {
			if(diariasList.contains(item.getDiaria())){
				count += item.getValor().getQuantidade();
			}
		}
		return count;
	}

	/**
	 * 
	 * @param guia
	 * @param diarias
	 * @return retorna o valor gasto com as acomodações informadas
	 */
	public static BigDecimal getSomatorioDiarias(GuiaInternacao guia, Diaria... diarias){
		List diariasList = Arrays.asList(diarias);
		BigDecimal somatorio = BigDecimal.ZERO;
		for (ItemDiaria item : guia.getDiariasAutorizadas()) {
			if(diariasList.contains(item.getDiaria())){
				somatorio = MoneyCalculation.getSoma(somatorio, item.getValorTotal());
			}
		}
		return somatorio;
	}
	
	/**
	 * Ordena uma coleção de guias. Primeiro pelo tipo, depois pelo numero da autorização.
	 * A ordenação de autorização ocorre primeiro pela parte numerica, depois pela parte letra,
	 * na ordem em que são geradas as autorizações (ex.: 123Z, 123AA, 123AB, 234, 234A).
	 * 
	 * @param <P extends GuiaFaturavel>
	 * @param guias
	 * @return
	 */
	public static <P extends GuiaFaturavel> List<P> ordenarGuiasPorTipoEAutorizacao(List<P> guias) {
		Collections.sort(guias, new Comparator<P>() {
				@Override
				public int compare(P g1, P g2) {
					if ((g1.getTipoDeGuia() != null) && g1.getTipoDeGuia().compareTo(g2.getTipoDeGuia()) != 0){
						return g1.getTipoDeGuia().compareTo(g2.getTipoDeGuia());
					}
					
					if(g1.getAutorizacao() == null){
						return -1;
					}	
					if(g2.getAutorizacao() == null){
						return 1;
					}
					
					String a1 = g1.getAutorizacao();
					String a2 = g2.getAutorizacao();
					
					String numero1 = a1.substring(0,getPosicaoDaPromeiraLetra(a1));
					String numero2 = a2.substring(0,getPosicaoDaPromeiraLetra(a2));
					
					boolean g1ESoNumero = a1.length() == numero1.length() ? true : false; 
					boolean g2ESoNumero = a2.length() == numero2.length() ? true : false;
					
					if(numero1.length() < numero2.length()) {
						return -1;
					} else if (numero1.length() >  numero2.length()) {
						return 1;
					} else {
						if(g1ESoNumero && g2ESoNumero){
							return a1.compareTo(a2);
						}
					}
					
					if (numero1.compareTo(numero2) != 0){
						return numero1.compareTo(numero2);
					}
					
					String letra1 = a1.substring(getPosicaoDaPromeiraLetra(a1));
					String letra2 = a2.substring(getPosicaoDaPromeiraLetra(a2));
					
					if(letra1.length() < letra2.length()) {
						return -1;
					} else if (letra1.length() >  letra2.length()) {
						return 1;
					} else {
						return letra1.compareTo(letra2);
					}
				}
			}
		);
		return guias;
	}
	/**
	 * Pega a posição da primeira letra de uma String
	 */
	public static int getPosicaoDaPromeiraLetra(String string){
		char[] charArray = string.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			if (Character.isLetter(charArray[i])){
				return i;
			}
		}
		return charArray.length;	
	}
	
	public static BigDecimal aplicarMultaPorAtraso(BigDecimal valor, BigDecimal porcentagemDaMulta) {
		if(porcentagemDaMulta != null && MoneyCalculation.compare(BigDecimal.ZERO, (porcentagemDaMulta)) != 0){
			BigDecimal valorComMulta = valor.multiply(BigDecimal.ONE.subtract(porcentagemDaMulta.movePointLeft(2))); 
			return MoneyCalculation.rounded(valorComMulta);
		}
		return valor;
	}
	
	/**
	 * Método utilizado para verificar se uma guia foi autorizadoa, geralmente utilizado
	 * via delegação pelas entidades que implementam a interface criticável.
	 * @param situacao
	 * @return
	 */
	public static boolean  isAutorizado(String situacao){
		if(SituacaoEnum.AUTORIZADO.descricao().equals(situacao) || SituacaoEnum.ABERTO.descricao().equals(situacao)){
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
	
	/**
	 * retorna lista de situações a partir de fechado
	 * @return
	 */
	public  static List<String> getSituacoesAPartirFechado() {
		List<String> situacoes = new ArrayList<String>();
		situacoes.add(SituacaoEnum.FECHADO.descricao());
		situacoes.add(SituacaoEnum.ENVIADO.descricao());
		situacoes.add(SituacaoEnum.DEVOLVIDO.descricao());
		situacoes.add(SituacaoEnum.RECEBIDO.descricao());
		situacoes.add(SituacaoEnum.AUDITADO.descricao());
		situacoes.add(SituacaoEnum.FATURADA.descricao());
		situacoes.add(SituacaoEnum.PAGO.descricao());
		return situacoes;
	}

	/**
	 * retorna lista de situações a partir de confirmado
	 * @return
	 */
	public static List<String> getSituacoesAPartirConfirmado() {
		List<String> situacoes = new ArrayList<String>();
		situacoes.add(SituacaoEnum.CONFIRMADO.descricao());
		situacoes.add(SituacaoEnum.ENVIADO.descricao());
		situacoes.add(SituacaoEnum.RECEBIDO.descricao());
		situacoes.add(SituacaoEnum.DEVOLVIDO.descricao());
		situacoes.add(SituacaoEnum.AUDITADO.descricao());
		return situacoes;
	}

	public static List<String> getSituacoesAPartirRecebido() {
		List<String> situacoes = new ArrayList<String>();
		situacoes.add(SituacaoEnum.RECEBIDO.descricao());
		situacoes.add(SituacaoEnum.AUDITADO.descricao());
		situacoes.add(SituacaoEnum.FATURADA.descricao());
		situacoes.add(SituacaoEnum.PAGO.descricao());
		return situacoes;
	}
	
	
	
	/**
	 * verifica se duas datas estão cronológicamente corretas. A ordem é dataMaior,  dataMenor
	 * @param dataMaior : data posterior a dataMenor
	 * @param dataMenor :  data anterior a dataMaior
	 * @param mensagemDeErro : mensagem de erro que deve ser exibida
	 */
	public static void verificarErroNasDatasDaGuia(Date dataMaior,Date dataMenor,String mensagemDeErro) {
		int compareData = Utils.compareData(dataMaior,dataMenor);
		if (compareData < 0) {
			throw new RuntimeException(mensagemDeErro);
		}
	}

	/**
	 * Usado para calcular o valor dos itesn guia faturamento.
	 * @param itensGuiaFaturamento
	 * @return
	 */
	public static BigDecimal getSomatorioItensGuiaFaturamento(Set<ItemGuiaFaturamento> itensGuiaFaturamento) {
		BigDecimal resultado = BigDecimal.ZERO;
		
		for (ItemGuiaFaturamento item : itensGuiaFaturamento) {
			resultado = resultado.add(item.getValor());
		}
		
		return resultado;
	}
	
}
