package br.com.infowaypi.ecarebc.atendimentos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.utils.GuiaUtils;

@SuppressWarnings("unchecked")
public class ResumoGuiasAptasParaPagamento<G extends GuiaSimples> {

	private String competencia;
	private Prestador prestador;
	private Map<Prestador, Set<G>> mapaPrestador;
	private List<G> guias;
	private List<ResumoTotalizacaoGuiasAptasParaPagamento> resumos;
	private List<ResumoTotalizacaoGuiasAptasParaPagamento> resumoGeral;
	private boolean todosPrestadores;
	
	public ResumoGuiasAptasParaPagamento(String competencia, Map<Prestador, Set<G>> mapaPrestador) {
		this.competencia = competencia;
		this.mapaPrestador = mapaPrestador;
		this.todosPrestadores = true;
		this.resumos = new ArrayList<ResumoTotalizacaoGuiasAptasParaPagamento>();
		this.resumoGeral = new ArrayList<ResumoTotalizacaoGuiasAptasParaPagamento>();
		this.totalizarGuiasPrestadores();
	}
	
	public ResumoGuiasAptasParaPagamento(String competencia, Prestador prestador, List<G> guias) {
		this.competencia = competencia;
		this.prestador = prestador;
		this.guias = guias;
		this.todosPrestadores = false;
		this.resumos = new ArrayList<ResumoTotalizacaoGuiasAptasParaPagamento>();
		this.totalizarGuias();
	}
	
	private void totalizarGuiasPrestadores() {
		BigDecimal totalApresentadoGeral = BigDecimal.ZERO;
		BigDecimal totalPagoAoPrestadorGeral = BigDecimal.ZERO;
		BigDecimal somaGuiasGeral = BigDecimal.ZERO;
		BigDecimal resultadoSomaGuiasGeral = BigDecimal.ZERO;
		String porcentagemGuiasGeralString;
		
		int quantidadeGuiasAuditadasGeral = 0;
		int quantidadeGuiasRecebidasGeral = 0;
		BigDecimal somaQuantidadeGuiasGeral = BigDecimal.ZERO;
		BigDecimal resultadoSomaQuantidadeGuiasGeral = BigDecimal.ZERO;
		String porcentagemQuantidadeGeralString;
		
		for (Prestador prestador : mapaPrestador.keySet()) {
			Set<G> guiasPrestador = mapaPrestador.get(prestador);
			Iterator<G> iterator = guiasPrestador.iterator();
			
			BigDecimal totalApresentadoGuiasRecebidas = BigDecimal.ZERO;
			BigDecimal totalPagoAoPrestadorGuiasAuditadas = BigDecimal.ZERO;
			BigDecimal somaGuias = BigDecimal.ZERO;
			BigDecimal resultadoSomaGuias = BigDecimal.ZERO;
			String porcentagemGuiasString;
			
			int quantidadeGuiasAuditadas = 0;
			int quantidadeGuiasRecebidas = 0;
			BigDecimal somaQuantidadeGuias = BigDecimal.ZERO;
			BigDecimal resultadoSomaQuantidadeGuias = BigDecimal.ZERO;
			String porcentagemQuantidadeString;
			
			Set<G> guiasAuditadas = new HashSet<G>();
			Set<G> guiasRecebidas = new HashSet<G>();
			
			while (iterator.hasNext()) {
				G g = (G) iterator.next();
				if (g.getSituacao().getDescricao().equals(SituacaoEnum.AUDITADO.descricao())) {
					if (g.getValorPagoPrestador() != null) {
						totalPagoAoPrestadorGuiasAuditadas = totalPagoAoPrestadorGuiasAuditadas.add(g.getValorPagoPrestador());
					}
					guiasAuditadas.add(g);
				}else if (g.getSituacao().getDescricao().equals(SituacaoEnum.RECEBIDO.descricao())) {
					if (g.getValorTotalApresentado() != null) {
						totalApresentadoGuiasRecebidas = totalApresentadoGuiasRecebidas.add(g.getValorTotalApresentado());
					}
					guiasRecebidas.add(g);
				}
			}
			
			quantidadeGuiasAuditadas = guiasAuditadas.size();
			quantidadeGuiasRecebidas = guiasRecebidas.size();
			
			quantidadeGuiasAuditadasGeral = quantidadeGuiasAuditadasGeral + quantidadeGuiasAuditadas;
			quantidadeGuiasRecebidasGeral = quantidadeGuiasRecebidasGeral + quantidadeGuiasRecebidas;
			
			somaQuantidadeGuias = somaQuantidadeGuias.add(new BigDecimal(quantidadeGuiasAuditadas)).add(new BigDecimal(quantidadeGuiasRecebidas));
			if (!somaQuantidadeGuias.equals(BigDecimal.ZERO)) {
				resultadoSomaQuantidadeGuias = new BigDecimal(quantidadeGuiasAuditadas).divide(somaQuantidadeGuias,4,BigDecimal.ROUND_HALF_EVEN).movePointRight(2);
				porcentagemQuantidadeString = resultadoSomaQuantidadeGuias.toString()+" %";
				
				totalApresentadoGuiasRecebidas.setScale(2, BigDecimal.ROUND_HALF_UP);
				totalPagoAoPrestadorGuiasAuditadas.setScale(2, BigDecimal.ROUND_HALF_UP);
				
				somaGuias = somaGuias.add(totalPagoAoPrestadorGuiasAuditadas).add(totalApresentadoGuiasRecebidas);
				if (somaGuias.compareTo(BigDecimal.ZERO) > 0) {
					resultadoSomaGuias = totalPagoAoPrestadorGuiasAuditadas.divide(somaGuias,4,BigDecimal.ROUND_HALF_UP).movePointRight(2);
					porcentagemGuiasString = resultadoSomaGuias.toString()+" %";
				} else {
					porcentagemGuiasString = "";
					porcentagemQuantidadeString = "";
				}
				
				totalApresentadoGeral = totalApresentadoGeral.add(totalApresentadoGuiasRecebidas).setScale(2, BigDecimal.ROUND_HALF_UP);
				totalPagoAoPrestadorGeral = totalPagoAoPrestadorGeral.add(totalPagoAoPrestadorGuiasAuditadas).setScale(2, BigDecimal.ROUND_HALF_UP);
				
				this.resumos.add(new ResumoTotalizacaoGuiasAptasParaPagamento(prestador.getNome(), totalPagoAoPrestadorGuiasAuditadas, totalApresentadoGuiasRecebidas, quantidadeGuiasAuditadas, quantidadeGuiasRecebidas, porcentagemGuiasString, porcentagemQuantidadeString));
			}
		}
		
		somaQuantidadeGuiasGeral = somaQuantidadeGuiasGeral.add(new BigDecimal(quantidadeGuiasAuditadasGeral)).add(new BigDecimal(quantidadeGuiasRecebidasGeral));
		if (!somaQuantidadeGuiasGeral.equals(BigDecimal.ZERO)) {
			resultadoSomaQuantidadeGuiasGeral = new BigDecimal(quantidadeGuiasAuditadasGeral).divide(somaQuantidadeGuiasGeral,4,RoundingMode.HALF_UP).movePointRight(2);
			porcentagemQuantidadeGeralString = resultadoSomaQuantidadeGuiasGeral.toString()+" %";
			
			somaGuiasGeral = somaGuiasGeral.add(totalPagoAoPrestadorGeral).add(totalApresentadoGeral);
			if (somaGuiasGeral.compareTo(BigDecimal.ZERO) > 0) {
				resultadoSomaGuiasGeral = totalPagoAoPrestadorGeral.divide(somaGuiasGeral,4,BigDecimal.ROUND_HALF_UP).movePointRight(2);
				porcentagemGuiasGeralString = resultadoSomaGuiasGeral.toString()+" %";
			} else {
				porcentagemGuiasGeralString = "";
				porcentagemQuantidadeGeralString = "";
			}
			
			this.resumoGeral.add(new ResumoTotalizacaoGuiasAptasParaPagamento("TOTAL GERAL", totalPagoAoPrestadorGeral, totalApresentadoGeral, quantidadeGuiasAuditadasGeral, quantidadeGuiasRecebidasGeral, porcentagemGuiasGeralString, porcentagemQuantidadeGeralString));
		}
	}
	
	private void totalizarGuias(){
		
		BigDecimal totalApresentadoGuiasConfirmadas = BigDecimal.ZERO;
		BigDecimal totalApresentadoGuiasAuditadas = BigDecimal.ZERO;
		BigDecimal totalApresentadoGuiasRecebidas = BigDecimal.ZERO;
		BigDecimal totalApresentadoGeral = BigDecimal.ZERO;
		
		BigDecimal totalPagoAoPrestadorGuiasConfirmadas = BigDecimal.ZERO;
		BigDecimal totalPagoAoPrestadorGuiasAuditadas = BigDecimal.ZERO;
		BigDecimal totalPagoAoPrestadorGuiasRecebidas = BigDecimal.ZERO;
		BigDecimal totalPagoAoPrestadorGeral = BigDecimal.ZERO;
		
		for(G g : guias){
			BigDecimal vTA, vPA;
			if (g.getValorTotalApresentado()==null) {
				vTA = g.getValorTotal();
			} else {
				vTA = g.getValorTotalApresentado();
			}
			if (g.getValorPagoPrestador()==null) {
				vPA = g.getValorTotal();
			} else {
				vPA = g.getValorPagoPrestador();
			}
			if(g.getSituacao().getDescricao().equals(SituacaoEnum.CONFIRMADO.descricao())){
				totalApresentadoGuiasConfirmadas = totalApresentadoGuiasConfirmadas.add(vTA);
				totalPagoAoPrestadorGuiasConfirmadas = totalPagoAoPrestadorGuiasConfirmadas.add(vPA);
			}else if (g.getSituacao().getDescricao().equals(SituacaoEnum.AUDITADO.descricao())) {
				totalApresentadoGuiasAuditadas = totalApresentadoGuiasAuditadas.add(vTA);
				totalPagoAoPrestadorGuiasAuditadas = totalPagoAoPrestadorGuiasAuditadas.add(vPA);
			}else if (g.getSituacao().getDescricao().equals(SituacaoEnum.RECEBIDO.descricao())) {
				totalApresentadoGuiasRecebidas = totalApresentadoGuiasRecebidas.add(vTA);
				totalPagoAoPrestadorGuiasRecebidas = totalPagoAoPrestadorGuiasRecebidas.add(vPA);
			}
		}
		
		totalApresentadoGuiasConfirmadas.setScale(2, BigDecimal.ROUND_HALF_UP);
		totalApresentadoGuiasAuditadas.setScale(2, BigDecimal.ROUND_HALF_UP);
		totalApresentadoGuiasRecebidas.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		totalPagoAoPrestadorGuiasConfirmadas.setScale(2, BigDecimal.ROUND_HALF_UP);
		totalPagoAoPrestadorGuiasAuditadas.setScale(2, BigDecimal.ROUND_HALF_UP);
		totalPagoAoPrestadorGuiasRecebidas.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		totalApresentadoGeral = totalApresentadoGeral.add(totalApresentadoGuiasConfirmadas).add(totalApresentadoGuiasAuditadas).add(totalApresentadoGuiasRecebidas).setScale(2, BigDecimal.ROUND_HALF_UP);
		totalPagoAoPrestadorGeral = totalPagoAoPrestadorGeral.add(totalPagoAoPrestadorGuiasConfirmadas).add(totalPagoAoPrestadorGuiasAuditadas).add(totalPagoAoPrestadorGuiasRecebidas).setScale(2, BigDecimal.ROUND_HALF_UP);
		
		this.resumos.add(new ResumoTotalizacaoGuiasAptasParaPagamento("Valor total apresentado", totalApresentadoGuiasConfirmadas, totalApresentadoGuiasAuditadas, totalApresentadoGuiasRecebidas, totalApresentadoGeral));
		this.resumos.add(new ResumoTotalizacaoGuiasAptasParaPagamento("Valor total pago ao prestador", totalPagoAoPrestadorGuiasConfirmadas, totalPagoAoPrestadorGuiasAuditadas, totalPagoAoPrestadorGuiasRecebidas, totalPagoAoPrestadorGeral));
	}
	
	public String getCompetencia() {
		return competencia;
	}

	public void setCompetencia(String competencia) {
		this.competencia = competencia;
	}

	public Prestador getPrestador() {
		return prestador;
	}

	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
	}

	public List<G> getGuias() {
		return guias;
	}

	public void setGuias(List<G> guias) {
		this.guias = guias;
	}

	public List<G> getGuiasOrdenadas() {
		return GuiaUtils.ordenarGuiasPorTipoEAutorizacao(guias);
	}

	public List<ResumoTotalizacaoGuiasAptasParaPagamento> getResumos() {
		return resumos;
	}

	public void setResumos(List<ResumoTotalizacaoGuiasAptasParaPagamento> resumos) {
		this.resumos = resumos;
	}

	public Map<Prestador, Set<G>> getMapaPrestador() {
		return mapaPrestador;
	}

	public void setMapaPrestador(Map<Prestador, Set<G>> mapaPrestador) {
		this.mapaPrestador = mapaPrestador;
	}

	public boolean isTodosPrestadores() {
		return todosPrestadores;
	}

	public void setTodosPrestadores(boolean todosPrestadores) {
		this.todosPrestadores = todosPrestadores;
	}
	
	public List<ResumoTotalizacaoGuiasAptasParaPagamento> getResumoGeral() {
		return resumoGeral;
	}

	public void setResumoGeral(List<ResumoTotalizacaoGuiasAptasParaPagamento> resumoGeral) {
		this.resumoGeral = resumoGeral;
	}

}
