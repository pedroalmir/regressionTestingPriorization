package br.com.infowaypi.ecarebc.atendimentos;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import br.com.infowaypi.ecare.cadastros.ItemAplicavel;
import br.com.infowaypi.ecare.cadastros.MotivoGlosa;
import br.com.infowaypi.ecare.services.recurso.ItemRecursoLayer;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemGasoterapia;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemTaxa;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.painelDeControle.PainelDeControle;
import br.com.infowaypi.ecarebc.procedimentos.Pacote;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.utils.CollectionUtils;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe responsável por gerenciar os layers para recurso de glosa e relatório de recurso de glosa.
 * @author Luciano Rocha
 * @changes Coloquei o campo recursando na guia para que seja identificado de está recursando ou apenas gerando o relatório.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ManagerLayerRecurso {

	private Calendar calendarHoje = Calendar.getInstance();
	private GuiaCompleta<ProcedimentoInterface> guia;
	private int tempoLimiteRecurso;
	
	public ManagerLayerRecurso(GuiaCompleta<ProcedimentoInterface> guia, boolean isRecursando) {
		this.guia = guia;
		tempoLimiteRecurso = PainelDeControle.getPainel().getTempoLimiteRecursoGlosa();
		boolean permiteRecurso = false;
		
		if(!guia.isSituacaoAtual(SituacaoEnum.PAGO.descricao()) && isRecursando){
			throw new RuntimeException("A guia informada não pode ser recursada, pois ainda não foi paga.");
		}
		
		permiteRecurso = dataPermiteRecurso(guia.getSituacao().getDataSituacao());
		if (!permiteRecurso && isRecursando) {
			throw new RuntimeException("A guia informada não pode mais ser recursada, pois ultrapassou o prazo para recurso que é de " + tempoLimiteRecurso + " dias.");
		}

		try {
			fillLayerRecursoItemGasoterapia();
			fillLayerRecursoItemTaxa();
			fillLayerRecursoItemDiaria();
			fillLayerRecursoItemPacote();
			fillLayerRecursoProcedimentosOutros();
			fillLayerRecursoProcedimentosCirurgicos();
			fillLayerRecursoProcedimentosExames();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	/** Preenche os layers de Itens Gasoterapia glosados voltados ao Recurso de Glosa
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException **/
	private void fillLayerRecursoItemGasoterapia() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		guia.setLayersRecursoItensGasoterapia(new ArrayList<ItemRecursoLayer>());
		Set<ItemGasoterapia> itensGlosados = new HashSet<ItemGasoterapia>();
		for (ItemGasoterapia itemGasoterapia : guia.getItensGasoterapia()) {
			itensGlosados.add(itemGasoterapia);
		}
		Map<Gasoterapia, Set<ItemGasoterapia>> itensGasoterapiaAgrupadosPorGasoterapia = CollectionUtils.groupBy(itensGlosados, "gasoterapia", Gasoterapia.class);
		for (Entry<Gasoterapia, Set<ItemGasoterapia>> entry : itensGasoterapiaAgrupadosPorGasoterapia.entrySet()) {
			List<ItemGasoterapia> lista = new ArrayList<ItemGasoterapia>(entry.getValue());			
			Utils.sort(lista, true, "situacao.dataSituacao");

			ItemGasoterapia itemAuditado = null;
			itemAuditado = lista.get(0);

			boolean isRecursavel = false;
			
			if (itemAuditado.getItemRecurso() == null) {
				isRecursavel = true;
			} 
			else if (itemAuditado.getItemRecurso().getSituacao().getDescricao().equals(SituacaoEnum.INDEFERIDO.descricao())) {
				isRecursavel = true;
			}
			
			if (isRecursavel) {
				ItemGasoterapia itemIteracao = itemAuditado;
				ItemGasoterapia itemApresentado = null;
				ItemRecursoLayer itemRecursoLayer = null;
				int jumps = 0;
				
				while (itemIteracao.getItemGlosavelAnterior() != null) {
					itemIteracao = (ItemGasoterapia) itemIteracao.getItemGlosavelAnterior();
					itemApresentado = itemIteracao;
					jumps++;
				}
				if (jumps == 0) {
					itemApresentado = itemAuditado;
					if (itemApresentado.getSituacao().getDescricao().equals(SituacaoEnum.GLOSADO.descricao())) {
						itemRecursoLayer = new ItemRecursoLayer(itemAuditado, itemApresentado, itemAuditado.getMotivoGlosa().getDescricao());
						if (itemRecursoLayer.getDiferencaValorTotalGasoterapia().compareTo(BigDecimal.ZERO) != 0) {
							guia.getLayersRecursoItensGasoterapia().add(itemRecursoLayer);
						}
					}
				} else {
					itemRecursoLayer = new ItemRecursoLayer(itemAuditado, itemApresentado, ((ItemGasoterapia) itemAuditado.getItemGlosavelAnterior()).getMotivoGlosa().getDescricao());
					if (itemRecursoLayer.getDiferencaValorTotalGasoterapia().compareTo(BigDecimal.ZERO) != 0) {
						guia.getLayersRecursoItensGasoterapia().add(itemRecursoLayer);
					}
				}
			}
		}
	}

	/** Preenche os layers de Itens Taxa glosados voltados ao Recurso de Glosa
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException **/
	private void fillLayerRecursoItemTaxa() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		guia.setLayersRecursoItensTaxa(new ArrayList<ItemRecursoLayer>());
		Set<ItemTaxa> itensGlosados = new HashSet<ItemTaxa>();
		for (ItemTaxa itemTaxa : guia.getItensTaxa()) {
			itensGlosados.add(itemTaxa);
		}
		Map<Taxa, Set<ItemTaxa>> itensTaxaAgrupadosPorTaxa = CollectionUtils.groupBy(itensGlosados, "taxa", Taxa.class);
		for (Entry<Taxa, Set<ItemTaxa>> entry : itensTaxaAgrupadosPorTaxa.entrySet()) {
			List<ItemTaxa> lista = new ArrayList<ItemTaxa>(entry.getValue());
			Utils.sort(lista, true, "situacao.dataSituacao");

			ItemTaxa itemAuditado = null;
			itemAuditado = lista.get(0);
				
			boolean isRecursavel = false;

			if (itemAuditado.getItemRecurso() == null) {
				isRecursavel = true;
			} 
			else if (itemAuditado.getItemRecurso().getSituacao().getDescricao().equals(SituacaoEnum.INDEFERIDO.descricao())) {
				isRecursavel = true;
			}

			if (isRecursavel) {
				ItemTaxa itemIteracao = itemAuditado;
				ItemTaxa itemApresentado = null;
				ItemRecursoLayer itemRecursoLayer = null;
				int jumps = 0;
				
				while (itemIteracao.getItemGlosavelAnterior() != null) {
					itemIteracao = (ItemTaxa) itemIteracao.getItemGlosavelAnterior();
					itemApresentado = itemIteracao;
					jumps++;
				}
				if (jumps == 0) {
					itemApresentado = itemAuditado;
					if (itemApresentado.getSituacao().getDescricao().equals(SituacaoEnum.GLOSADO.descricao())) {
						itemRecursoLayer = new ItemRecursoLayer(itemAuditado, itemApresentado, itemAuditado.getMotivoGlosa().getDescricao());
						if (itemRecursoLayer.getDiferencaValorTotalTaxa().compareTo(BigDecimal.ZERO) != 0) {
							guia.getLayersRecursoItensTaxa().add(itemRecursoLayer);
						}
					}
				} else {
					itemRecursoLayer = new ItemRecursoLayer(itemAuditado, itemApresentado, ((ItemTaxa) itemAuditado.getItemGlosavelAnterior()).getMotivoGlosa().getDescricao());
					if (itemRecursoLayer.getDiferencaValorTotalTaxa().compareTo(BigDecimal.ZERO) != 0) {
						guia.getLayersRecursoItensTaxa().add(itemRecursoLayer);
					}
				}
			}	
		}
	}

	/** Preenche os layers de Itens Diaria glosados voltados ao Recurso de Glosa
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException **/
	private void fillLayerRecursoItemDiaria() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		guia.setLayersRecursoItensDiaria(new ArrayList<ItemRecursoLayer>());
		Set<ItemDiaria> itensGlosados = new HashSet<ItemDiaria>();
		for (ItemDiaria itemDiaria : guia.getItensDiaria()) {
			itensGlosados.add(itemDiaria);
		}
		Map<Diaria, Set<ItemDiaria>> itensDiariaAgrupadosPorDiaria = CollectionUtils.groupBy(itensGlosados, "diaria", Diaria.class);
		for (Entry<Diaria, Set<ItemDiaria>> entry : itensDiariaAgrupadosPorDiaria.entrySet()) {
			List<ItemDiaria> lista = new ArrayList<ItemDiaria>(entry.getValue());
			Utils.sort(lista, true, "situacao.dataSituacao");

			ItemDiaria itemAuditado = null;
			itemAuditado = lista.get(0);

			boolean isRecursavel = false;
			
			if (itemAuditado.getItemRecurso() == null) {
				isRecursavel = true;
			} 
			else if (itemAuditado.getItemRecurso().getSituacao().getDescricao().equals(SituacaoEnum.INDEFERIDO.descricao())) {
				isRecursavel = true;
			}

			if (isRecursavel) {
				ItemDiaria itemIteracao = itemAuditado;
				ItemDiaria itemApresentado = null;
				ItemRecursoLayer itemRecursoLayer = null;
				int jumps = 0;
				
				while (itemIteracao.getItemGlosavelAnterior() != null) {
					itemIteracao = (ItemDiaria) itemIteracao.getItemGlosavelAnterior();
					itemApresentado = itemIteracao;
					jumps++;
				}
				if (jumps == 0) {
					itemApresentado = itemAuditado;
					if (itemApresentado.getSituacao().getDescricao().equals(SituacaoEnum.GLOSADO.descricao())) {
						itemRecursoLayer = new ItemRecursoLayer(itemAuditado, itemApresentado, itemAuditado.getMotivoGlosa().getDescricao());
						if (itemRecursoLayer.getDiferencaValorTotalDiaria().compareTo(BigDecimal.ZERO) != 0) {
							guia.getLayersRecursoItensDiaria().add(itemRecursoLayer);
						}
					}
				} else {
					itemRecursoLayer = new ItemRecursoLayer(itemAuditado, itemApresentado, ((ItemDiaria) itemAuditado.getItemGlosavelAnterior()).getMotivoGlosa().getDescricao());
					if (itemRecursoLayer.getDiferencaValorTotalDiaria().compareTo(BigDecimal.ZERO) != 0) {
						guia.getLayersRecursoItensDiaria().add(itemRecursoLayer);
					}
				}
			}
		}
	}
	
	/** Preenche os layers de Itens Pacotes glosados voltados ao Recurso de Glosa
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException **/
	private void fillLayerRecursoItemPacote() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		guia.setLayersRecursoItensPacote(new ArrayList<ItemRecursoLayer>());
		Set<ItemPacote> itensGlosados = new HashSet<ItemPacote>();
		for (ItemPacote itemPacote : guia.getItensPacote()) {
			itensGlosados.add(itemPacote);
		}
		Map<Pacote, Set<ItemPacote>> itensPacoteAgrupadosPorPacote = CollectionUtils.groupBy(itensGlosados, "pacote", Pacote.class);
		for (Entry<Pacote, Set<ItemPacote>> entry : itensPacoteAgrupadosPorPacote.entrySet()) {
			List<ItemPacote> lista = new ArrayList<ItemPacote>(entry.getValue());
			Utils.sort(lista, true, "situacao.dataSituacao");

			ItemPacote itemAuditado = null;
			itemAuditado = lista.get(0);

			boolean isRecursavel = false;
			
			if (isRecursavel) {
				ItemPacote itemIteracao = itemAuditado;
				ItemPacote itemApresentado = null;
				ItemRecursoLayer itemRecursoLayer = null;
				int jumps = 0;
				
				while (itemIteracao.getItemGlosavelAnterior() != null) {
					itemIteracao = (ItemPacote) itemIteracao.getItemGlosavelAnterior();
					itemApresentado = itemIteracao;
					jumps++;
				}
				if (jumps == 0) {
					itemApresentado = itemAuditado;
					if (itemApresentado.getSituacao().getDescricao().equals(SituacaoEnum.GLOSADO.descricao())) {
						itemRecursoLayer = new ItemRecursoLayer(itemAuditado, itemApresentado, itemAuditado.getMotivoGlosa().getDescricao());
						if (itemRecursoLayer.getDiferencaValorTotalPacote().compareTo(BigDecimal.ZERO) != 0) {
							guia.getLayersRecursoItensPacote().add(itemRecursoLayer);
						}
					}
				} else {
					itemRecursoLayer = new ItemRecursoLayer(itemAuditado, itemApresentado, ((ItemPacote) itemAuditado.getItemGlosavelAnterior()).getMotivoGlosa().getDescricao());
					if (itemRecursoLayer.getDiferencaValorTotalPacote().compareTo(BigDecimal.ZERO) != 0) {
						guia.getLayersRecursoItensPacote().add(itemRecursoLayer);
					}
				}
			}
		}
	}
	
	/** Preenche os layers de Procedimentos glosados voltados ao Recurso de Glosa
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException **/
	private void fillLayerRecursoProcedimentosOutros() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		guia.setLayersRecursoProcedimentosOutros(new ArrayList<ItemRecursoLayer>());
		
		List<Procedimento> procedimentosGlosados = new ArrayList<Procedimento>();
		
		for (Procedimento procedimento: guia.getProcedimentos(Procedimento.class)) {
			if (procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_OUTROS) {
				procedimentosGlosados.add(procedimento);
			}
		}
		Map<TabelaCBHPM, Set<Procedimento>> procedimentosAgrupadosPorID = CollectionUtils.groupBy(procedimentosGlosados, "procedimentoDaTabelaCBHPM", TabelaCBHPM.class);
		for (Entry<TabelaCBHPM, Set<Procedimento>> entry : procedimentosAgrupadosPorID.entrySet()) {
			List<Procedimento> lista = new ArrayList<Procedimento>(entry.getValue());
			Utils.sort(lista, true, "situacao.dataSituacao");
			
			Procedimento procedimentoAuditado = null;
			procedimentoAuditado = lista.get(0);

			boolean isRecursavel = false;

			if (procedimentoAuditado.getItemRecurso() == null) {
				isRecursavel = true;
			} 
			else if (procedimentoAuditado.getItemRecurso().getSituacao().getDescricao().equals(SituacaoEnum.INDEFERIDO.descricao())) {
				isRecursavel = true;
			}

			if (isRecursavel) {
				Procedimento procedimentoIteracao = procedimentoAuditado;
				Procedimento procedimentoApresentado = null;
				ItemRecursoLayer itemRecursoLayer = null;
				int jumps = 0;
				
				while (procedimentoIteracao.getItemGlosavelAnterior() != null) {
					procedimentoIteracao = (Procedimento) procedimentoIteracao.getItemGlosavelAnterior();
					procedimentoApresentado = procedimentoIteracao;
					jumps++;
				}
				if (jumps == 0) {
					procedimentoApresentado = procedimentoAuditado;
					if (procedimentoApresentado.getSituacao().getDescricao().equals(SituacaoEnum.GLOSADO.descricao())) {
						itemRecursoLayer = new ItemRecursoLayer(procedimentoAuditado, procedimentoApresentado, procedimentoAuditado.getMotivoGlosaProcedimento().getDescricao());
						if (itemRecursoLayer.getDiferencaValorTotalProcedimento().compareTo(BigDecimal.ZERO) != 0) {
							guia.getLayersRecursoProcedimentosOutros().add(itemRecursoLayer);
						}
					}
				} else {
					itemRecursoLayer = new ItemRecursoLayer(procedimentoAuditado, procedimentoApresentado, ((Procedimento) procedimentoAuditado.getItemGlosavelAnterior()).getMotivoGlosaProcedimento().getDescricao());
					if (itemRecursoLayer.getDiferencaValorTotalProcedimento().compareTo(BigDecimal.ZERO) != 0) {
						guia.getLayersRecursoProcedimentosOutros().add(itemRecursoLayer);
					}
				}
			}	
		}
	}

	/** Preenche os layers de Procedimentos glosados voltados ao Recurso de Glosa
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException **/
	private void fillLayerRecursoProcedimentosCirurgicos() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		guia.setLayersRecursoProcedimentoCirurgico(new ArrayList<ItemRecursoLayer>());
		
		List<Procedimento> procedimentosGlosados = new ArrayList<Procedimento>();
		
		for (Procedimento procedimento: guia.getProcedimentos(Procedimento.class)) {
			if (procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO) {
				procedimentosGlosados.add(procedimento);				
			}
		}
		Map<TabelaCBHPM, Set<Procedimento>> procedimentosAgrupadosPorID = CollectionUtils.groupBy(procedimentosGlosados, "procedimentoDaTabelaCBHPM", TabelaCBHPM.class);
		for (Entry<TabelaCBHPM, Set<Procedimento>> entry : procedimentosAgrupadosPorID.entrySet()) {
			List<Procedimento> lista = new ArrayList<Procedimento>(entry.getValue());
			Utils.sort(lista, true, "situacao.dataSituacao");

			Procedimento procedimentoAuditado = null;
			procedimentoAuditado = lista.get(0);
			
			boolean isRecursavel = false;

			if (procedimentoAuditado.getItemRecurso() == null) {
				isRecursavel = true;
			} 
			else if (procedimentoAuditado.getItemRecurso().getSituacao().getDescricao().equals(SituacaoEnum.INDEFERIDO.descricao())) {
				isRecursavel = true;
			}

			if (isRecursavel) {
				Procedimento procedimentoIteracao = procedimentoAuditado;
				Procedimento procedimentoApresentado = null;
				ItemRecursoLayer itemRecursoLayer = null;
				int jumps = 0;

				while (procedimentoIteracao.getItemGlosavelAnterior() != null) {
					procedimentoIteracao = (Procedimento) procedimentoIteracao.getItemGlosavelAnterior();
					procedimentoApresentado = procedimentoIteracao;
					jumps ++;
				}
				if (jumps == 0) {
					procedimentoApresentado = procedimentoAuditado;
					if (procedimentoApresentado.getSituacao().getDescricao().equals(SituacaoEnum.GLOSADO.descricao())) {
						MotivoGlosa motivoPadrao = new MotivoGlosa();
						if (procedimentoAuditado.getMotivoGlosaProcedimento() == null) {
							motivoPadrao.setAplicavelProcedimentos(true);
							motivoPadrao.setAtivo(true);
							motivoPadrao.setCodigoMensagem(99977);
							motivoPadrao.setDescricao("Glosa feita antes da padronização dos motivos de glosa.");
							motivoPadrao.setItensGuiaAplicaveis(new HashSet<ItemAplicavel>());
							procedimentoAuditado.setMotivoGlosaProcedimento(motivoPadrao);
							try {
								ImplDAO.save(procedimentoAuditado);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						else {
							motivoPadrao = procedimentoAuditado.getMotivoGlosaProcedimento();
						}
						itemRecursoLayer = new ItemRecursoLayer(procedimentoAuditado, procedimentoApresentado, motivoPadrao.getDescricao());
						if (itemRecursoLayer.getDiferencaValorTotalProcedimento().compareTo(BigDecimal.ZERO) != 0) {
							guia.getLayersRecursoProcedimentoCirurgico().add(itemRecursoLayer);
						}
					}
				} else {
					itemRecursoLayer = new ItemRecursoLayer(procedimentoAuditado, procedimentoApresentado, ((Procedimento) procedimentoAuditado.getItemGlosavelAnterior()).getMotivoGlosaProcedimento().getDescricao());
					if (itemRecursoLayer.getDiferencaValorTotalProcedimento().compareTo(BigDecimal.ZERO) != 0) {
						guia.getLayersRecursoProcedimentoCirurgico().add(itemRecursoLayer);
					}
				}
			}
		}
	}

	/** Preenche os layers de Procedimentos glosados voltados ao Recurso de Glosa
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException **/
	private void fillLayerRecursoProcedimentosExames() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		guia.setLayersRecursoProcedimentosExame(new ArrayList<ItemRecursoLayer>());
		
		List<Procedimento> procedimentosGlosados = new ArrayList<Procedimento>();
		for (Procedimento procedimento: guia.getProcedimentos(Procedimento.class)) {
			if (procedimento.getTipoProcedimento() != ProcedimentoInterface.PROCEDIMENTO_CIRURGICO
					&& procedimento.getTipoProcedimento() != ProcedimentoInterface.PROCEDIMENTO_OUTROS){
				procedimentosGlosados.add(procedimento);
			}
		}
		Map<TabelaCBHPM, Set<Procedimento>> procedimentosAgrupadosPorID = CollectionUtils.groupBy(procedimentosGlosados, "procedimentoDaTabelaCBHPM", TabelaCBHPM.class);
		for (Entry<TabelaCBHPM, Set<Procedimento>> entry : procedimentosAgrupadosPorID.entrySet()) {
			List<Procedimento> lista = new ArrayList<Procedimento>(entry.getValue());
			Utils.sort(lista, true, "situacao.dataSituacao");

			Procedimento procedimentoAuditado = null;
			procedimentoAuditado = lista.get(0);

			boolean isRecursavel = false;
			
			if (procedimentoAuditado.getItemRecurso() == null) {
				isRecursavel = true;
			} 
			else if (procedimentoAuditado.getItemRecurso().getSituacao().getDescricao().equals(SituacaoEnum.INDEFERIDO.descricao())) {
					isRecursavel = true;
			}

			if (isRecursavel) {
				Procedimento procedimentoIteracao = procedimentoAuditado;
				Procedimento procedimentoApresentado = null;
				ItemRecursoLayer itemRecursoLayer = null;
				int jumps = 0;
				
				while (procedimentoIteracao.getItemGlosavelAnterior() != null) {
					procedimentoIteracao = (Procedimento) procedimentoIteracao.getItemGlosavelAnterior();
					procedimentoApresentado = procedimentoIteracao;
					jumps++;
				}
				if (jumps == 0) {
					procedimentoApresentado = procedimentoAuditado;
					if (procedimentoApresentado.getSituacao().getDescricao().equals(SituacaoEnum.GLOSADO.descricao())) {
						itemRecursoLayer = new ItemRecursoLayer(procedimentoAuditado, procedimentoApresentado, procedimentoAuditado.getMotivoGlosaProcedimento().getDescricao());
						if (itemRecursoLayer.getDiferencaValorTotalProcedimento().compareTo(BigDecimal.ZERO) != 0) {
							guia.getLayersRecursoProcedimentosExame().add(itemRecursoLayer);
						}
					}
				} else {
					itemRecursoLayer = new ItemRecursoLayer(procedimentoAuditado, procedimentoApresentado, ((Procedimento) procedimentoAuditado.getItemGlosavelAnterior()).getMotivoGlosaProcedimento().getDescricao());
					if (itemRecursoLayer.getDiferencaValorTotalProcedimento().compareTo(BigDecimal.ZERO) != 0) {
						guia.getLayersRecursoProcedimentosExame().add(itemRecursoLayer);
					}
				}
			}
		}
	}

	private boolean dataPermiteRecurso(Date dataSituacao) {

		Calendar calendarSituacao = Calendar.getInstance();
		calendarSituacao.setTime(dataSituacao);
		calendarSituacao.add(Calendar.DAY_OF_MONTH, tempoLimiteRecurso);
		Calendar cal = Calendar.getInstance();

		calendarSituacao.set(calendarSituacao.get(Calendar.YEAR), 
				calendarSituacao.get(Calendar.MONTH), 
				calendarSituacao.get(Calendar.DAY_OF_MONTH), 
				calendarSituacao.getMaximum(Calendar.HOUR_OF_DAY), 
				calendarSituacao.getMaximum(Calendar.MINUTE), 
				calendarSituacao.getMaximum(Calendar.SECOND));
	
		
		if (calendarSituacao.before(calendarHoje)){
			return false;
		} 
		return true;
	}
	
	public GuiaCompleta getGuia() {
		return guia;
	}

	public void setGuia(GuiaCompleta guia) {
		this.guia = guia;
	}
}
