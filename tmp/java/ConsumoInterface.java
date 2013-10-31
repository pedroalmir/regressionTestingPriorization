package br.com.infowaypi.ecarebc.consumo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import br.com.infowaypi.ecarebc.associados.Prestador.TipoIncremento;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.consumo.periodos.Periodo;

public interface ConsumoInterface extends Serializable, Comparable {
	
	public static final Collection<ColecaoConsumosInterface> colecaoConsumosInterface = null;
	
	public abstract Integer getTipoPeriodo();
	
	public abstract String getMediaNumeroProcedimentosPorGuia();

	public abstract String getMediaNumeroProcedimentosPorGuiaExame();

	public abstract String getMediaNumeroProcedimentosPorGuiaUrgencia();

	public abstract String getMediaNumeroProcedimentosPorGuiaInternacao();

	public abstract Long getIdConsumo();

	public abstract void setIdConsumo(Long idConsumo);

	public abstract BigDecimal getSomatorioConsultas();

	public abstract BigDecimal getSomatorioConsultasAgendadas();

	public abstract void setSomatorioConsultasAgendadas(BigDecimal somatorioConsultas);

	public abstract BigDecimal getSomatorioConsultasConfirmadas();

	public abstract void setSomatorioConsultasConfirmadas(
			BigDecimal somatorioConsultas);

	public abstract void incrementaSomatorioConsultasAgendadas(
			BigDecimal valorConsulta,TipoIncremento tipoIncremento);

	public abstract void incrementaSomatorioConsultasConfirmadas(
			BigDecimal valorConsulta,TipoIncremento tipoIncremento, Date competencia, boolean decrementaAgendadas);

	public abstract BigDecimal getSomatorioExames();

	public abstract BigDecimal getSomatorioExamesAgendados();

	public abstract void setSomatorioExamesAgendados(BigDecimal somatorioExames);

	public abstract BigDecimal getSomatorioExamesConfirmados();

	public abstract void setSomatorioExamesConfirmados(BigDecimal somatorioExames);

	public abstract void incrementaSomatorioExamesAgendados(BigDecimal valorExame,
			int numeroDeProcedimentos,TipoIncremento tipoIncremento);

	public abstract void incrementaSomatorioExamesConfirmados(BigDecimal valorExame,
			int numeroDeProcedimentos,TipoIncremento tipoIncremento, Date competencia, boolean decrementaProcedimentosAgendados, boolean decrementaExamesAgendados);

	public abstract BigDecimal getLimiteConsultas();

	public abstract void setLimiteConsultas(BigDecimal limiteConsultas);

	public abstract BigDecimal getLimiteExames();

	public abstract void setLimiteExames(BigDecimal limiteExames);

	public abstract Date getDataInicial();

	public abstract void setDataInicial(Date dataInicial);

	public abstract Date getDataFinal();

	public abstract void setDataFinal(Date dataFinal);
	
	public abstract Date getProximaDataValida();

	public abstract Periodo getPeriodo();

	public abstract void setPeriodo(Periodo periodo);

	public abstract ColecaoConsumosInterface getColecaoConsumos();

	public abstract void setColecaoConsumos(
			ColecaoConsumosInterface colecaoConsumos);

	public abstract String getDescricao();

	public abstract void setDescricao(String descricao);

	public abstract Float getConsumoGeral();

	public abstract int getNumeroGeral();

	public abstract boolean equals(Object object);

	public abstract int hashCode();

	public abstract String toString();

	public abstract int compareTo(Object o);

	public abstract int getNumeroConsultas();

	public abstract int getNumeroConsultasAgendadas();

	public abstract void setNumeroConsultasAgendadas(int numeroConsultas);

	public abstract int getNumeroConsultasConfirmadas();

	public abstract void setNumeroConsultasConfirmadas(int numeroConsultas);

	public abstract int getNumeroConsultasCanceladas();

	public abstract void setNumeroConsultasCanceladas(int numeroConsultas);

	public abstract int getNumeroExames();

	public abstract int getNumeroExamesConfirmados();

	public abstract void setNumeroExamesConfirmados(int numeroExames);

	public abstract int getNumeroExamesCancelados();

	public abstract void setNumeroExamesCancelados(int numeroExames);

	public abstract int getNumeroExamesAgendados();

	public abstract void setNumeroExamesAgendados(int numeroExames);

	public abstract int getNumeroProcedimentosExamesAgendados();

	public abstract void setNumeroProcedimentosExamesAgendados(
			int numeroProcedimentos);

	public abstract int getNumeroProcedimentosExamesConfirmados();

	public abstract void setNumeroProcedimentosExamesConfirmados(
			int numeroProcedimentos);

	public abstract int getNumeroProcedimentosInternacoesAbertas();

	public abstract void setNumeroProcedimentosInternacoesAbertas(
			int numeroProcedimentos);

	public abstract int getNumeroProcedimentosInternacoesFechadas();

	public abstract void setNumeroProcedimentosInternacoesFechadas(
			int numeroProcedimentos);

	public abstract BigDecimal getSomatorioInternacoesAbertas();

	public abstract void setSomatorioInternacoesAbertas(
			BigDecimal somatorioInternacoes);

	public abstract BigDecimal getSomatorioInternacoesFechadas();

	public abstract void setSomatorioInternacoesFechadas(
			BigDecimal somatorioInternacoes);

	public abstract int getNumeroInternacoesAbertas();

	public abstract void setNumeroInternacoesAbertas(int numeroInternacoes);

	public abstract int getNumeroInternacoesFechadas();

	public abstract void setNumeroInternacoesFechadas(int numeroInternacoes);

	public abstract int getNumeroProcedimentosUrgenciasAbertas();

	public abstract void setNumeroProcedimentosUrgenciasAbertas(
			int numeroProcedimentos);

	public abstract int getNumeroProcedimentosUrgenciasFechadas();

	public abstract void setNumeroProcedimentosUrgenciasFechadas(
			int numeroProcedimentos);

	public abstract BigDecimal getSomatorioUrgenciasAbertas();

	public abstract void setSomatorioUrgenciasAbertas(BigDecimal somatorioInternacoes);

	public abstract BigDecimal getSomatorioUrgenciasFechadas();

	public abstract void setSomatorioUrgenciasFechadas(
			BigDecimal somatorioInternacoes);

	public abstract int getNumeroUrgenciasAbertas();

	public abstract void setNumeroUrgenciasAbertas(int numeroInternacoes);

	public abstract int getNumeroUrgenciasFechadas();

	public abstract void setNumeroUrgenciasFechadas(int numeroInternacoes);

	public abstract int getNumeroInternacoesCanceladas();

	public abstract void setNumeroInternacoesCanceladas(int numeroInternacoes);

	public abstract void incrementaSomatorioInternacoesAbertas(
			BigDecimal valorInternacao, int numeroDeProcedimentos,TipoIncremento tipoIncremento);

	public abstract void incrementaSomatorioInternacoesFechadas(
			BigDecimal valorPreFechamento, int numeroDeProcedimentosPreFechamento,BigDecimal valorPosFechamento, int numeroDeProcedimentosPosFechamento,TipoIncremento tipoIncremento);

	public abstract void incrementaSomatorioUrgenciasAbertas(
			BigDecimal valorInternacao, int numeroDeProcedimentos,TipoIncremento tipoIncremento);

	public abstract void incrementaSomatorioUrgenciasFechadas(
			BigDecimal valorPreFechamento, int numeroDeProcedimentosPreFechamento,BigDecimal valorPosFechamento, int numeroDeProcedimentosPosFechamento,TipoIncremento tipoIncremento);

	public void incrementaSomatorioConsultasAtendimentosUrgenciasAbertas(
			BigDecimal valorInternacao, int numeroDeProcedimentos,TipoIncremento tipoIncremento);
	
	public void incrementaSomatorioConsultasAtendimentosUrgenciasFechadas(
			BigDecimal valorPreFechamento, int numeroDeProcedimentosPreFechamento,BigDecimal valorPosFechamento, int numeroDeProcedimentosPosFechamento,TipoIncremento tipoIncremento);
	
	public void atualizaSomatorioExamesAgendados(BigDecimal valorInternacao,int numeroDeProcedimentos,TipoIncremento tipoIncremento);
	
	public void atualizaSomatorioExamesConfirmados(BigDecimal valorInternacao,int numeroDeProcedimentos,TipoIncremento tipoIncremento);
	
	public abstract void atualizaSomatorioInternacoesAbertas(
			BigDecimal valorInternacao, int numeroDeProcedimentos,TipoIncremento tipoIncremento);

	public abstract void atualizaSomatorioInternacoesFechadas(
			BigDecimal valorInternacao, int numeroDeProcedimentos,TipoIncremento tipoIncremento);

	public abstract void atualizaSomatorioUrgenciasAbertas(
			BigDecimal valorInternacao, int numeroDeProcedimentos,TipoIncremento tipoIncremento);

	public abstract void atualizaSomatorioUrgenciasFechadas(
			BigDecimal valorInternacao, int numeroDeProcedimentos,TipoIncremento tipoIncremento);

	public void atualizaSomatorioConsultasAtendimentosUrgenciasAbertas(
			BigDecimal valorInternacao,int numeroDeProcedimentos,TipoIncremento tipoIncremento);
	
	public void atualizaSomatorioConsultasAtendimentosUrgenciasFechadas(
			BigDecimal valorInternacao,int numeroDeProcedimentos,TipoIncremento tipoIncremento);
	
	public void incrementaSomatorioConsultasOdontoUrgenciaConfirmadas(
			BigDecimal valorConsulta, Date competencia,TipoIncremento tipoIncremento);
	
	public abstract int getNumeroUrgencias();

	public abstract BigDecimal getSomatorioUrgencias();

	public abstract int getNumeroInternacoes();

	public abstract BigDecimal getSomatorioInternacoes();

	public abstract BigDecimal getLimiteInternacoes();

	public abstract void setLimiteInternacoes(BigDecimal limiteInternacoes);

	public abstract BigDecimal getLimiteUrgencias();

	public abstract void setLimiteUrgencias(BigDecimal limiteUrgencias);

	public abstract int getNumeroUrgenciasCanceladas();

	public abstract void setNumeroUrgenciasCanceladas(
			int numeroUrgenciasCanceladas);

	public BigDecimal getSomatorioConsultasAtendimentosUrgenciasAbertas();
	public void setSomatorioConsultasAtendimentosUrgenciasAbertas(
			BigDecimal somatorioConsultasAtendimentosUrgenciasAbertas);
	
	public BigDecimal getSomatorioConsultasAtendimentosUrgenciasFechadas();
	public void setSomatorioConsultasAtendimentosUrgenciasFechadas(
			BigDecimal somatorioConsultasAtendimentosUrgenciasFechadas);

	public int getNumeroConsultasAtendimentosUrgenciasFechadas();
	public void setNumeroConsultasAtendimentosUrgenciasFechadas(
			int numeroConsultasAtendimentosUrgenciasFechadas);

	public int getNumeroConsultasAtendimentosUrgenciasAbertas();
	public void setNumeroConsultasAtendimentosUrgenciasAbertas(
			int numeroConsultasAtendimentosUrgenciasAbertas);
	
	public int getNumeroConsultasAtendimentosUrgenciasCanceladas();
	public void setNumeroConsultasAtendimentosUrgenciasCanceladas(
			int numeroConsultasAtendimentosUrgenciasCanceladas);
	
	public BigDecimal getLimiteConsultasAtendimentosUrgencias();
	public void setLimiteConsultasAtendimentosUrgencias(
			BigDecimal limiteConsultasAtendimentosUrgencias);
	
	public int getNumeroProcedimentosConsultasAtendimentosUrgenciasAbertas();
	public void setNumeroProcedimentosConsultasAtendimentosUrgenciasAbertas(
			int numeroProcedimentosConsultasAtendimentosUrgenciasAbertas);
	
	public int getNumeroProcedimentosConsultasAtendimentosUrgenciasFechadas();
	public void setNumeroProcedimentosConsultasAtendimentosUrgenciasFechadas(
			int numeroProcedimentosConsultasAtendimentosUrgenciasFechadas);
	
	public int getNumeroProcedimentosExames();

	public int getNumeroProcedimentosUrgencias();
	public int getNumeroProcedimentosInternacoes();
	
	public boolean isLimiteEstourado(GuiaSimples guia) ;
	
	public BigDecimal getLimiteConsultasOdonto();

	public void setLimiteConsultasOdonto(BigDecimal limiteConsultasOdonto);

	public BigDecimal getLimiteTratamentosOdonto();

	public void setLimiteTratamentosOdonto(BigDecimal limiteTratamentosOdonto);
	
	public String getMediaNumeroProcedimentosPorGuiaTratamentoOdonto();
	
	public void incrementaSomatorioConsultasOdontoConfirmadas(BigDecimal valorConsulta, Date competencia,TipoIncremento tipoIncremento);
	
	public void incrementaSomatorioTratamentosOdontoAgendados(BigDecimal valorTratamento, int numeroDeProcedimentos,TipoIncremento tipoIncremento) ;
	
	public void incrementaSomatorioTratamentosOdontoConfirmados(BigDecimal valorTratamento,TipoIncremento tipoIncremento, int numeroDeProcedimentos, Date competencia, boolean decrementaAgendadas);
	
	public int getNumeroConsultasOdontoCanceladas();

	public void setNumeroConsultasOdontoCanceladas(int numeroConsultasOdontoCanceladas);

	public int getNumeroConsultasOdontoConfirmadas();

	public void setNumeroConsultasOdontoConfirmadas(int numeroConsultasOdontoConfirmadas);

	public int getNumeroTratamentosOdontoAgendados();

	public void setNumeroTratamentosOdontoAgendados(int numeroTratamentosOdontoAgendados);

	public int getNumeroTratamentosOdontoCancelados();

	public void setNumeroTratamentosOdontoCancelados(int numeroTratamentosOdontoCancelados);

	public int getNumeroTratamentosOdontoConfirmados();

	public void setNumeroTratamentosOdontoConfirmados(int numeroTratamentosOdontoConfirmados);

	public BigDecimal getSomatorioConsultasOdontoConfirmadas();

	public void setSomatorioConsultasOdontoConfirmadas(BigDecimal somatorioConsultasOdontoConfirmadas);

	public BigDecimal getSomatorioTratamentosOdontoAgendados();

	public void setSomatorioTratamentosOdontoAgendados(BigDecimal somatorioTratamentosOdontoAgendados);

	public BigDecimal getSomatorioTratamentosOdontoConfirmados();

	public void setSomatorioTratamentosOdontoConfirmados(BigDecimal somatorioTratamentosOdontoConfirmados);

	public int getNumeroProcedimentosOdontoAgendados();

	public void setNumeroProcedimentosOdontoAgendados(int numeroProcedimentosOdontoAgendados);

	public int getNumeroProcedimentosOdontoConfirmados();

	public void setNumeroProcedimentosOdontoConfirmados(int numeroProcedimentosOdontoConfirmados);
	
	public BigDecimal getSomatorioConsultasOdontoUrgenciaConfirmadas();

	public void setSomatorioConsultasOdontoUrgenciaConfirmadas(BigDecimal somatorioConsultasOdontoUrgencia);

	public int getNumeroConsultasOdontoUrgenciaConfirmadas();

	public void setNumeroConsultasOdontoUrgenciaConfirmadas(int numeroConsultasOdontoUrgencia);

	public BigDecimal getSomatorioConsultasOdontoUrgenciaCanceladas();

	public void setSomatorioConsultasOdontoUrgenciaCanceladas(BigDecimal somatorioConsultasOdontoUrgenciaCanceladas);

	public int getNumeroConsultasOdontoUrgenciaCanceladas();

	public void setNumeroConsultasOdontoUrgenciaCanceladas(int numeroConsultasOdontoUrgenciaCanceladas);
	
}