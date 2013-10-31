package br.com.infowaypi.ecare.services.recurso;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import br.com.infowaypi.ecare.atendimentos.TipoGuiaEnum;
import br.com.infowaypi.ecare.enums.MotivoEnumSR;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaReformuladaInterface;
import br.com.infowaypi.ecarebc.atendimentos.LoteDeGuias;
import br.com.infowaypi.ecarebc.atendimentos.MotivoDevolucaoDeLote;
import br.com.infowaypi.ecarebc.atendimentos.Visualizavel;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AbstractFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.GuiaFaturavel;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ItemGuiaFaturamento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.msr.situations.ComponenteColecaoSituacoes;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * Classe que represnta uma Guia de Recurso de Glosa (GRG), anteriormente
 * denominada RecursoGlosa.
 * 
 * @author Eduardo Vera
 * 
 */
public class GuiaRecursoGlosa extends ImplColecaoSituacoesComponent implements GuiaFaturavel, GuiaReformuladaInterface, Visualizavel, Serializable {

	private static final long serialVersionUID = 1L;
	private Long idGuia;
	private String autorizacao;
	private GuiaCompleta<ProcedimentoInterface> guiaOrigem;
	private Set<ItemRecursoGlosa> itensRecurso;
	private Date dataRecurso;
	private BigDecimal valorTotal;
	private AbstractFaturamento faturamento;
	
	/**
	 * Atributo transiente usado para selecionar as guias no fluxo de recebimento de lote.
	 */
	private Boolean recebido; 		
	
	/**
	 * O atributo motivo é transiente e é utilizado no fluxo recebimento de lote
	 */
	private MotivoDevolucaoDeLote motivoDevolucaoLote;
	
	/**
	 * Último lote ao qual a guia pertenceu
	 */
	private LoteDeGuias ultimoLote;
	
	/**
	 * Atributo que determina a porcentagem de deflação que a guia sofrerá por
	 * ter sido entregue fora do prazo (determinado no painel de controle).
	 */
	private BigDecimal multaPorAtrasoDeEntrega;
	
	private boolean layerFlowRecurso;
	
	private Date dataMarcacao;

	public GuiaRecursoGlosa() {

	}

	public GuiaRecursoGlosa(GuiaCompleta guia) {
		this.guiaOrigem = guia;
		this.itensRecurso = new HashSet<ItemRecursoGlosa>();
	}

	public GuiaCompleta<ProcedimentoInterface> getGuiaOrigem() {
		return guiaOrigem;
	}

	public void setGuiaOrigem(GuiaCompleta<ProcedimentoInterface> guia) {
		this.guiaOrigem = guia;
	}

	public Set<ItemRecursoGlosa> getItensRecurso() {
		return itensRecurso;
	}

	public void setItensRecurso(Set<ItemRecursoGlosa> itensRecurso) {
		this.itensRecurso = itensRecurso;
	}

	public Set<ItemRecursoGlosa> getItensRecursoGasoterapia() {
		Set<ItemRecursoGlosa> itensRecursoGasoterapia = new HashSet<ItemRecursoGlosa>();
		for (ItemRecursoGlosa item : itensRecurso) {
			if (item.getItemGuiaGlosada() != null) {
				if (item.getItemGuiaGlosada().isItemGasoterapia()) {
					itensRecursoGasoterapia.add(item);
				}
			}
		}
		return itensRecursoGasoterapia;
	}

	public Set<ItemRecursoGlosa> getItensRecursoGuia() {
		Set<ItemRecursoGlosa> itensRecursoGuia = new HashSet<ItemRecursoGlosa>();
		for (ItemRecursoGlosa item : itensRecurso) {
			if (item.getGuiaGlosada() != null) {
				itensRecursoGuia.add(item);
			}
		}
		return itensRecursoGuia;
	}

	
	public Set<ItemRecursoGlosa> getItensRecursoTaxa() {
		Set<ItemRecursoGlosa> itensRecursoTaxa = new HashSet<ItemRecursoGlosa>();
		for (ItemRecursoGlosa item : itensRecurso) {
			if (item.getItemGuiaGlosada() != null) {
				if (item.getItemGuiaGlosada().isItemTaxa()) {
					itensRecursoTaxa.add(item);
				}
			}
		}
		return itensRecursoTaxa;
	}

	public Set<ItemRecursoGlosa> getItensRecursoDiaria() {
		Set<ItemRecursoGlosa> itensRecursoDiaria = new HashSet<ItemRecursoGlosa>();
		for (ItemRecursoGlosa item : itensRecurso) {
			if (item.getItemGuiaGlosada() != null) {
				if (item.getItemGuiaGlosada().isItemDiaria()) {
					itensRecursoDiaria.add(item);
				}
			}
		}
		return itensRecursoDiaria;
	}

	public Set<ItemRecursoGlosa> getItensRecursoPacote() {
		Set<ItemRecursoGlosa> itensRecursoPacote = new HashSet<ItemRecursoGlosa>();
		for (ItemRecursoGlosa item : itensRecurso) {
			if (item.getItemGuiaGlosada() != null) {
				if (item.getItemGuiaGlosada().isItemPacote()) {
					itensRecursoPacote.add(item);
				}
			}
		}
		return itensRecursoPacote;
	}

	public Set<ItemRecursoGlosa> getItensRecursoProcedimentoCirurgico() {
		Set<ItemRecursoGlosa> itensRecursoProcedimento = new HashSet<ItemRecursoGlosa>();
		for (ItemRecursoGlosa item : itensRecurso) {
			if (item.getProcedimentoGlosado() != null) {
				if (item.getProcedimentoGlosado().getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO) {
					itensRecursoProcedimento.add(item);
				}
			}
		}
		return itensRecursoProcedimento;
	}

	public Set<ItemRecursoGlosa> getItensRecursoProcedimentoOutros() {
		Set<ItemRecursoGlosa> itensRecursoProcedimento = new HashSet<ItemRecursoGlosa>();
		for (ItemRecursoGlosa item : itensRecurso) {
			if (item.getProcedimentoGlosado() != null) {
				if (item.getProcedimentoGlosado().getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_OUTROS) {
					itensRecursoProcedimento.add(item);
				}
			}
		}
		return itensRecursoProcedimento;
	}

	public Set<ItemRecursoGlosa> getItensRecursoProcedimento() {
		Set<ItemRecursoGlosa> itensRecursoProcedimento = new HashSet<ItemRecursoGlosa>();
		for (ItemRecursoGlosa item : itensRecurso) {
			if (item.getProcedimentoGlosado() != null) {
				if (item.getProcedimentoGlosado().getTipoProcedimento() != ProcedimentoInterface.PROCEDIMENTO_CIRURGICO
						&& item.getProcedimentoGlosado().getTipoProcedimento() != ProcedimentoInterface.PROCEDIMENTO_OUTROS) {
					itensRecursoProcedimento.add(item);
				}
			}
		}
		return itensRecursoProcedimento;
	}

	public Date getDataRecurso() {
		return dataRecurso;
	}

	public void setDataRecurso(Date dataRecurso) {
		this.dataRecurso = dataRecurso;
	}

	public String getDataRecursoFormatada() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(this.getCompetencia());
	}

	public void recalcularValores() {
		ManagerCalculoValorGRG manager = new ManagerCalculoValorGRG(this);
		manager.recalcularValorGRG();
	}
	
	public Integer getQuantidadeItensRecurso(){
		return this.getItensRecurso().size();
	}
	
	@Override
	public void setSituacao(SituacaoInterface situacao) {
		super.setSituacao(situacao);
	}

	@Override
	public ComponenteColecaoSituacoes getColecaoSituacoes() {
		return super.getColecaoSituacoes();
	}

	@Override
	public void setColecaoSituacoes(ComponenteColecaoSituacoes colecaoSituacoes) {
		super.setColecaoSituacoes(colecaoSituacoes);
	}

	@Override
	public SituacaoInterface getSituacao() {
		return super.getSituacao();
	}

	@Override
	public SituacaoInterface getSituacao(String descricao) {
		return super.getSituacao(descricao);
	}

	@Override
	public SituacaoInterface getSituacao(int ordem) {
		return super.getSituacao(ordem);
	}

	@Override
	public Set<SituacaoInterface> getSituacoes() {
		return super.getSituacoes();
	}

	@Override
	public boolean isExisteSituacao(String descricao) {
		return super.isExisteSituacao(descricao);
	}

	@Override
	public boolean isSituacaoAtual(String descricao) {
		return super.isSituacaoAtual(descricao);
	}

	@Override
	public void mudarSituacao(UsuarioInterface usuario, String descricao,
			String motivo, Date dataSituacao) {
		super.mudarSituacao(usuario, descricao, motivo, dataSituacao);
	}

	@Override
	public void setAutorizacao(String autorizacao) {
		this.autorizacao = autorizacao;
	}

	@Override
	public void setSegurado(AbstractSegurado segurado) {
		/* Not implemented... */
	}

	@Override
	public AbstractSegurado getSegurado() {
		if (this.getGuiaOrigem() == null) {
			return null;
		}
		return this.getGuiaOrigem().getSegurado();
	}

	@Override
	public void setCompetencia(Date competencia) {
		/* Not implemented... */
	}

	@Override
	public Date getCompetencia() {
		return this.getSituacao(SituacaoEnum.RECURSADO.descricao()).getDataSituacao();
	}

	@Override
	public String getAutorizacao() {
		if (idGuia == null) {
			return null;
		}
		return idGuia.toString();
	}

	@Override
	public Long getIdGuia() {
		return this.idGuia;
	}

	@Override
	public void setIdGuia(Long idGuia) {
		this.idGuia = idGuia;
	}

	@Override
	public Prestador getPrestador() {
		if (this.getGuiaOrigem() == null) {
			return null;
		}
		return this.getGuiaOrigem().getPrestador();
	}

	public Profissional getProfissional() {
		if (this.getGuiaOrigem() == null) {
			return null;
		}
		return this.getGuiaOrigem().getProfissional();
	}

	
	@Override
	public void setPrestador(Prestador prestador) {
		
	}

	@Override
	public Date getDataAtendimento() {
		if (this.getGuiaOrigem() == null) {
			return null;
		}
		return this.getGuiaOrigem().getDataAtendimento();
	}

	@Override
	public Date getDataTerminoAtendimento() {
		return this.getCompetencia();
	}

	@Override
	public void setDataTerminoAtendimento(Date dataTerminoAtendimento) {
		/* Not implemented... */
	}

	@Override
	public Date getDataRecebimento() {
		return this.getSituacao(SituacaoEnum.RECEBIDO.descricao()).getDataSituacao();
	}

	@Override
	public void setDataRecebimento(Date dataRecebimento) {
		/* Not implemented... */
	}

	@Override
	public BigDecimal getValorTotal() {
		
		return this.valorTotal;
	}
	
	public String getValorTotalFormatado() {
		return StringUtils.leftPad(new DecimalFormat("###,###,##0.00")
		.format(valorTotal.setScale(2, BigDecimal.ROUND_HALF_UP)), 9, " ");
	}

	@Override
	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;

	}

	@Override
	public BigDecimal getValorPagoPrestador() {
		return this.getValorTotal();
	}

	@Override
	public void setValorPagoPrestador(BigDecimal valorPagoPrestador) {
		// TODO Auto-generated method stub

	}

	@Override
	public AbstractFaturamento getFaturamento() {
		return this.faturamento;
	}

	@Override
	public void setFaturamento(AbstractFaturamento faturamento) {
		this.faturamento = faturamento;

	}

	@Override
	public String getTipoDeGuia() {
		return TipoGuiaEnum.GUIA_RECURSO_GLOSA.descricao();
	}

	@Override
	public void setTipoDeGuia(String tipoDeGuia) {
		/* Not implemented... */
	}

	@Override
	public Set<ItemGuiaFaturamento> getItensGuiaFaturamento() {
		return new HashSet<ItemGuiaFaturamento>();
	}

	@Override
	public void setItensGuiaFaturamento(Set<ItemGuiaFaturamento> itensFaturamento) {
		/* Not implemented... */

	}

	@Override
	public void addItemGuiaFaturamento(ItemGuiaFaturamento itemGuiaFaturamento) {
		/* Not implemented... */
	}

	@Override
	public boolean isConsulta() {
		return false;
	}

	@Override
	public boolean isConsultaEletiva() {
		return false;
	}

	@Override
	public boolean isConsultaOdonto() {
		return false;
	}

	@Override
	public boolean isConsultaUrgencia() {
		return false;
	}

	@Override
	public boolean isInternacao() {
		return false;
	}

	@Override
	public boolean isUrgencia() {
		return false;
	}

	@Override
	public boolean isExame() {
		return false;
	}

	@Override
	public boolean isExameEletivo() {
		return false;
	}

	@Override
	public boolean isRecursoGlosa() {
		return true;
	}

	@Override
	public boolean isExameOdonto() {
		return false;
	}

	@Override
	public boolean isInternacaoEletiva() {
		return false;
	}

	@Override
	public boolean isInternacaoUrgencia() {
		return false;
	}

	@Override
	public boolean isCompleta() {
		return false;
	}

	@Override
	public boolean isSimples() {
		return false;
	}

	@Override
	public boolean isTratamentoSeriado() {
		return false;
	}

	@Override
	public boolean isAtendimentoUrgencia() {
		return false;
	}

	@Override
	public boolean isConsultaOdontoUrgencia() {
		return false;
	}

	@Override
	public boolean isCirurgiaOdonto() {
		return false;
	}

	@Override
	public boolean isUrgenciaOuAtendimentoSubsequente() {
		return false;
	}

	@Override
	public boolean isHonorarioMedico() {
		return false;
	}

	@Override
	public boolean isAcompanhamentoAnestesico() {
		return false;
	}

	@Override
	public boolean isExameExternoAtendimentosUrgencia() {
		return false;
	}

	@Override
	public boolean isExameExternoInternacao() {
		return false;
	}

	@Override
	public SituacaoInterface getSituacaoAnterior(SituacaoInterface situacao) {
		return super.getSituacaoAnterior(situacao);
	}

	public boolean isGRG() {
		return true;
	}

	public GuiaRecursoGlosa getGuiaRecursoGlosa() {
		return this;
	}
	
	public boolean isLayerFlowRecurso() {
		return layerFlowRecurso;
	}

	public void setLayerFlowRecurso(boolean layerFlowRecurso) {
		this.layerFlowRecurso = layerFlowRecurso;
	}
	
	@Override
	public String getNumeroDeRegistro() {
		return null;
	}

	public Boolean getRecebido() {
		return recebido;
	}
	
	public Boolean isRecebido() {
		return recebido;
	}

	public void setRecebido(Boolean recebido) {
		this.recebido = recebido;
	}

	public MotivoDevolucaoDeLote getMotivoDevolucaoLote() {
		return motivoDevolucaoLote;
	}

	public void setMotivoDevolucaoLote(MotivoDevolucaoDeLote motivoDevolucaoLote) {
		this.motivoDevolucaoLote = motivoDevolucaoLote;
	}

	public LoteDeGuias getUltimoLote() {
		return ultimoLote;
	}

	public void setUltimoLote(LoteDeGuias ultimoLote) {
		this.ultimoLote = ultimoLote;
	}
	
	public void receberGuia(UsuarioInterface usuario) {
		Date now = new Date();
		this.mudarSituacao(usuario, SituacaoEnum.RECEBIDO.descricao(), MotivoEnumSR.GUIA_RECEBIDA.getMessage(), now);
		this.setDataRecebimento(now);
		this.recalcularValores();
	}

	public BigDecimal getMultaPorAtrasoDeEntrega() {
		return multaPorAtrasoDeEntrega;
	}

	public void setMultaPorAtrasoDeEntrega(BigDecimal multaPorAtrasoDeEntrega) {
		this.multaPorAtrasoDeEntrega = multaPorAtrasoDeEntrega;
	}
	
	/**
	 * Indica se foi aplicada multa por atraso na entrega para a guia.
	 * @return
	 */
	public boolean isPossuiMulta(){
		return this.multaPorAtrasoDeEntrega != null;
	}

	@Override
	public Date getDataInicioPrazoRecebimento() {
		return this.getCompetencia();
	}

	public Date getDataMarcacao() {
		if (guiaOrigem != null) {
			dataMarcacao = guiaOrigem.getDataMarcacao();
		}
		return dataMarcacao;
	}
	
	public void setDataMarcacao(Date dataMarcacao) {
		this.dataMarcacao = dataMarcacao;
	}
	
	public BigDecimal getValorPagoPrestadorApresentacao() {
		BigDecimal resultado = null;
		
//		if(this.isMostrarValorPagoPrestador()){
//			resultado =  this.valorPagoPrestador;
//		}
		
		return resultado;
	}
	
	public boolean isGuiaImpressaoNova(){
		return true;
	}
	
	@Override
	public String getTipo() {
		return "Guia de Recurso de Glosa";
	}

}