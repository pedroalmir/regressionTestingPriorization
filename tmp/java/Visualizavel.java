package br.com.infowaypi.ecarebc.atendimentos;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import br.com.infowaypi.ecare.services.recurso.GuiaRecursoGlosa;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AbstractFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ItemGuiaFaturamento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.msr.situations.SituacaoInterface;

public interface Visualizavel {

	public String getAutorizacao();
	
	public GuiaSimples<ProcedimentoInterface> getGuiaOrigem();
	
	public Long getIdGuia();
	public void setIdGuia(Long idGuia);
	
	public String getNumeroDeRegistro();

	public LoteDeGuias getUltimoLote();
	public String getTipo();

	public Prestador getPrestador();
	public void setPrestador(Prestador prestador);
	
	public Date getDataAtendimento();
	
	public Date getDataTerminoAtendimento();
	public void setDataTerminoAtendimento(Date dataTerminoAtendimento);
	
	
	public Date getDataRecebimento();
	public void setDataRecebimento(Date dataRecebimento);
	
	public BigDecimal getValorTotal();
	public void setValorTotal(BigDecimal valorTotal);
	
	public BigDecimal getValorPagoPrestador();
	public void setValorPagoPrestador(BigDecimal valorPagoPrestador);
	
	public AbstractFaturamento getFaturamento();
	public void setFaturamento(AbstractFaturamento faturamento);

	public String getTipoDeGuia();
	public void setTipoDeGuia(String tipoDeGuia);
	
	public Set<ItemGuiaFaturamento> getItensGuiaFaturamento();
	public void setItensGuiaFaturamento(Set<ItemGuiaFaturamento> itensFaturamento);
	public void addItemGuiaFaturamento(ItemGuiaFaturamento itemGuiaFaturamento);
	
	public abstract boolean isConsulta();
	public abstract boolean isRecursoGlosa();
	public abstract boolean isConsultaEletiva();
	public abstract boolean isConsultaOdonto();
	public abstract boolean isConsultaUrgencia();
	public abstract boolean isInternacao();
	public abstract boolean isUrgencia();
	public abstract boolean isExame();
	public abstract boolean isExameEletivo();
	public abstract boolean isExameOdonto();
	public abstract boolean isInternacaoEletiva();
	public abstract boolean isInternacaoUrgencia();
	public abstract boolean isCompleta();
	public abstract boolean isSimples();
	public abstract boolean isTratamentoSeriado();
	public abstract boolean isAtendimentoUrgencia();
	public abstract boolean isConsultaOdontoUrgencia();
	public abstract boolean isCirurgiaOdonto();
	public abstract boolean isUrgenciaOuAtendimentoSubsequente();
	public abstract boolean isHonorarioMedico();
	public abstract boolean isAcompanhamentoAnestesico();
	public abstract boolean isExameExternoAtendimentosUrgencia();
	public abstract boolean isExameExternoInternacao();
	public abstract boolean isGRG();
	public GuiaRecursoGlosa getGuiaRecursoGlosa();
	public SituacaoInterface getSituacaoAnterior(SituacaoInterface situacao);
	public Set<SituacaoInterface> getSituacoes();
	public AbstractSegurado getSegurado();

	
}
