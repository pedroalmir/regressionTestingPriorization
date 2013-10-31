package br.com.infowaypi.ecarebc.consumo;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import br.com.infowaypi.ecarebc.associados.Prestador.TipoIncremento;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.consumo.periodos.Periodo;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.exceptions.ConsumoException;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Esta classe representa um conjunto de informações acerca dos limites financeiros de um prestador,
 * organizados por competência, afim de manter um histórico de limites e possíveis autorizações.
 * @author Mário Sérgio Coelho Marroquim 
 * @changes Danilo Nogueira Portela 
 * Inserção de consumos odontológicos
 */
@SuppressWarnings("serial") 
public class Consumo implements ConsumoInterface {

	private Long idConsumo;
	private BigDecimal somatorioConsultasAgendadas;
	private BigDecimal somatorioConsultasConfirmadas;
	private int numeroConsultasAgendadas;
	private int numeroConsultasConfirmadas;
	private int numeroConsultasCanceladas;
	private BigDecimal limiteConsultas;
	
	private BigDecimal somatorioExamesAgendados;
	private BigDecimal somatorioExamesConfirmados;
	private int numeroExamesAgendados;
	private int numeroExamesConfirmados;
	private int numeroExamesCancelados;
	private BigDecimal limiteExames;
	
	private int numeroProcedimentosExamesAgendados;
	private int numeroProcedimentosExamesConfirmados;
	
	private BigDecimal somatorioConsultasOdontoConfirmadas;
	private int numeroConsultasOdontoConfirmadas;
	private int numeroConsultasOdontoCanceladas;
	private BigDecimal limiteConsultasOdonto;
	
	private BigDecimal somatorioConsultasOdontoUrgenciaConfirmadas;
	private int numeroConsultasOdontoUrgenciaConfirmadas;
	private BigDecimal somatorioConsultasOdontoUrgenciaCanceladas;
	private int numeroConsultasOdontoUrgenciaCanceladas;
	
	private BigDecimal somatorioTratamentosOdontoAgendados;
	private BigDecimal somatorioTratamentosOdontoConfirmados;
	private int numeroTratamentosOdontoAgendados;
	private int numeroTratamentosOdontoConfirmados;
	private int numeroTratamentosOdontoCancelados;
	private BigDecimal limiteTratamentosOdonto;
	
	private int numeroProcedimentosOdontoAgendados;
	private int numeroProcedimentosOdontoConfirmados;

	private BigDecimal somatorioUrgenciasAbertas;
	private BigDecimal somatorioUrgenciasFechadas;
	private int numeroUrgenciasFechadas;
	private int numeroUrgenciasAbertas;
	private int numeroUrgenciasCanceladas;
	private BigDecimal limiteUrgencias;

	private int numeroProcedimentosUrgenciasAbertas;
	private int numeroProcedimentosUrgenciasFechadas;
	
	private BigDecimal somatorioConsultasAtendimentosUrgenciasAbertas;
	private BigDecimal somatorioConsultasAtendimentosUrgenciasFechadas;
	private int numeroConsultasAtendimentosUrgenciasFechadas;
	private int numeroConsultasAtendimentosUrgenciasAbertas;
	private int numeroConsultasAtendimentosUrgenciasCanceladas;
	private BigDecimal limiteConsultasAtendimentosUrgencias;

	private int numeroProcedimentosConsultasAtendimentosUrgenciasAbertas;
	private int numeroProcedimentosConsultasAtendimentosUrgenciasFechadas;
	
	private BigDecimal somatorioInternacoesAbertas;
	private BigDecimal somatorioInternacoesFechadas;
	private int numeroInternacoesAbertas;
	private int numeroInternacoesFechadas;
	private int numeroInternacoesCanceladas;
	private BigDecimal limiteInternacoes;

	private int numeroProcedimentosInternacoesAbertas;
	private int numeroProcedimentosInternacoesFechadas;

	
	private Date dataInicial;
	private Date dataFinal;
	private Periodo periodo;
	private Integer tipoPeriodo;
	
	private String descricao;
	 
	private ColecaoConsumosInterface colecaoConsumos;

	public Consumo() {
		
		somatorioConsultasAgendadas = BigDecimal.ZERO;
		somatorioConsultasConfirmadas = BigDecimal.ZERO;
		limiteConsultas = BigDecimal.ZERO;
		
		somatorioExamesAgendados = BigDecimal.ZERO;
		somatorioExamesConfirmados = BigDecimal.ZERO;
		limiteExames = BigDecimal.ZERO;
		
		somatorioConsultasOdontoConfirmadas = BigDecimal.ZERO;
		limiteConsultasOdonto = BigDecimal.ZERO;
		
		somatorioTratamentosOdontoAgendados = BigDecimal.ZERO;
		somatorioTratamentosOdontoConfirmados = BigDecimal.ZERO;
		limiteTratamentosOdonto = BigDecimal.ZERO;
		
		somatorioUrgenciasAbertas = BigDecimal.ZERO;
		somatorioUrgenciasFechadas = BigDecimal.ZERO;
		limiteUrgencias = BigDecimal.ZERO;
		
		somatorioInternacoesAbertas = BigDecimal.ZERO;
		somatorioInternacoesFechadas = BigDecimal.ZERO;
		limiteInternacoes = BigDecimal.ZERO;
		
		somatorioConsultasAtendimentosUrgenciasAbertas = BigDecimal.ZERO;
		somatorioConsultasAtendimentosUrgenciasFechadas = BigDecimal.ZERO;
		limiteConsultasAtendimentosUrgencias = BigDecimal.ZERO;
		
		somatorioConsultasOdontoUrgenciaConfirmadas = BigDecimal.ZERO;
		somatorioConsultasOdontoUrgenciaCanceladas = BigDecimal.ZERO;
		
	}		
	
	public String getMediaNumeroProcedimentosPorGuia(){
		int numeroGuias = this.numeroExamesAgendados + this.numeroExamesConfirmados + this.numeroTratamentosOdontoAgendados + this.numeroTratamentosOdontoConfirmados
		+ this.numeroUrgenciasAbertas + this.numeroUrgenciasFechadas + this.numeroInternacoesAbertas + this.numeroInternacoesFechadas;
		
		int numeroProcedimentos = this.numeroProcedimentosExamesAgendados + this.numeroProcedimentosExamesConfirmados + 
								  this.numeroProcedimentosOdontoAgendados + this.numeroProcedimentosOdontoConfirmados + 
								  this.numeroProcedimentosInternacoesAbertas + this.numeroProcedimentosInternacoesFechadas +
								  this.numeroProcedimentosUrgenciasAbertas + this.numeroProcedimentosUrgenciasFechadas;
		if(numeroProcedimentos > 0 && numeroGuias > 0)
			return Utils.format(((float)numeroProcedimentos /(float)numeroGuias));
		return "0";
	}

	public String getMediaNumeroProcedimentosPorGuiaExame(){
		int numeroExames = this.numeroExamesAgendados + this.numeroExamesConfirmados;
		int numeroProcedimentos = this.numeroProcedimentosExamesAgendados + this.numeroProcedimentosExamesConfirmados;
		if(numeroProcedimentos > 0 && numeroExames > 0)
			return Utils.format(((float)numeroProcedimentos /(float)numeroExames));
		return "0";
	}
	
	public String getMediaNumeroProcedimentosPorGuiaTratamentoOdonto(){
		int numeroTratamentos = this.numeroTratamentosOdontoAgendados + this.numeroTratamentosOdontoConfirmados;
		int numeroProcedimentos = this.numeroProcedimentosOdontoAgendados + this.numeroProcedimentosOdontoConfirmados;
		if(numeroProcedimentos > 0 && numeroTratamentos > 0)
			return Utils.format(((float)numeroProcedimentos /(float)numeroTratamentos));
		return "0";
	}

	public String getMediaNumeroProcedimentosPorGuiaUrgencia(){
		int numeroGuias = this.numeroUrgenciasAbertas + this.numeroUrgenciasFechadas;
		
		int numeroProcedimentos =  this.numeroProcedimentosUrgenciasAbertas + this.numeroProcedimentosUrgenciasFechadas;
		if(numeroProcedimentos > 0 && numeroGuias > 0)
			return Utils.format(((float)numeroProcedimentos /(float)numeroGuias));
		return "0";
	}
	
	public String getMediaNumeroProcedimentosPorGuiaInternacao(){
		int numeroGuias = this.numeroInternacoesAbertas + this.numeroInternacoesFechadas;
		
		int numeroProcedimentos = this.numeroProcedimentosInternacoesAbertas + this.numeroProcedimentosInternacoesFechadas;
		if(numeroProcedimentos > 0 && numeroGuias > 0)
			return Utils.format(((float)numeroProcedimentos /(float)numeroGuias));
		return "0";
	}
	
	public Long getIdConsumo() {
		return idConsumo;
	}

	public void setIdConsumo(Long idConsumo) {
		this.idConsumo = idConsumo;
	}

	public BigDecimal getSomatorioConsultas() {
		return this.somatorioConsultasAgendadas.add(this.somatorioConsultasConfirmadas);
	}	
	
	public void incrementaSomatorioConsultasAgendadas(BigDecimal valorConsulta,TipoIncremento tipoIncremento){
		if (this.getIdConsumo() != null){
				this.setSomatorioConsultasAgendadas(this.getSomatorioConsultasAgendadas().add(valorConsulta));
			
			if(TipoIncremento.POSITIVO.equals(tipoIncremento))
				this.setNumeroConsultasAgendadas(this.getNumeroConsultasAgendadas() + 1);
			else{
				if(this.getNumeroConsultasAgendadas() < 0)
					this.setNumeroConsultasAgendadas(0);
				else
					this.setNumeroConsultasAgendadas(this.getNumeroConsultasAgendadas() - 1);
				
				this.setNumeroConsultasCanceladas(this.getNumeroConsultasCanceladas() + 1);
			}
		}
		else {
			this.setNumeroConsultasAgendadas(this.getNumeroConsultasAgendadas() + 1);
			this.setSomatorioConsultasAgendadas(this.getSomatorioConsultasAgendadas().add(valorConsulta));
		}
	}

	public void incrementaSomatorioConsultasConfirmadas(BigDecimal valorConsulta,TipoIncremento tipoIncremento, Date competencia, boolean decrementaAgendadas){
//		if (this.getIdConsumo() != null){
				
			this.setSomatorioConsultasConfirmadas(this.getSomatorioConsultasConfirmadas().add(valorConsulta));
			
			if(TipoIncremento.POSITIVO.equals(tipoIncremento)){
				this.setNumeroConsultasConfirmadas(this.getNumeroConsultasConfirmadas() + 1);
				
				if(Utils.between(this.getDataInicial(), this.getDataFinal(), competencia) && decrementaAgendadas){
					
					if(this.getNumeroConsultasAgendadas() < 0)
						this.setNumeroConsultasAgendadas(0);
					else
						this.setNumeroConsultasAgendadas(this.getNumeroConsultasAgendadas() - 1);
					
					this.setSomatorioConsultasAgendadas(this.getSomatorioConsultasAgendadas().subtract(valorConsulta));
				}
			}
			else{
				
				if(this.getNumeroConsultasConfirmadas() < 0)
					this.setNumeroConsultasConfirmadas(0);
				else
					this.setNumeroConsultasConfirmadas(this.getNumeroConsultasConfirmadas() - 1);
				
				this.setNumeroConsultasCanceladas(this.getNumeroConsultasCanceladas() + 1);
			}
	}
	
	public void incrementaSomatorioConsultasOdontoConfirmadas(BigDecimal valorConsulta, Date competencia,TipoIncremento tipoIncremento){
		if (this.getIdConsumo() != null){
			this.setSomatorioConsultasOdontoConfirmadas(this.getSomatorioConsultasOdontoConfirmadas().add(valorConsulta));
			
			if(TipoIncremento.POSITIVO.equals(tipoIncremento))
				this.setNumeroConsultasOdontoConfirmadas(this.getNumeroConsultasOdontoConfirmadas() + 1);
			else{
				if(this.getNumeroConsultasOdontoConfirmadas() < 0)
					this.setNumeroConsultasOdontoConfirmadas(0);
				else
					this.setNumeroConsultasOdontoConfirmadas(this.getNumeroConsultasOdontoConfirmadas() - 1);
				
				this.setNumeroConsultasOdontoCanceladas(this.getNumeroConsultasOdontoCanceladas() + 1);
			}
		}
		else {
			this.setNumeroConsultasOdontoConfirmadas(this.getNumeroConsultasOdontoConfirmadas() + 1);
			this.setSomatorioConsultasOdontoConfirmadas(this.getSomatorioConsultasOdontoConfirmadas().add(valorConsulta));
		}
	}
	
	public void incrementaSomatorioConsultasOdontoUrgenciaConfirmadas(BigDecimal valorConsulta, Date competencia,TipoIncremento tipoIncremento){
		if (this.getIdConsumo() != null){
			this.setSomatorioConsultasOdontoUrgenciaConfirmadas(this.getSomatorioConsultasOdontoUrgenciaConfirmadas().add(valorConsulta));
			
			if(TipoIncremento.POSITIVO.equals(tipoIncremento))
				this.setNumeroConsultasOdontoUrgenciaConfirmadas(this.getNumeroConsultasOdontoUrgenciaConfirmadas() + 1);
			
			else{
				if(this.getNumeroConsultasOdontoUrgenciaConfirmadas() < 0)
					this.setNumeroConsultasOdontoUrgenciaConfirmadas(0);
				else
					this.setNumeroConsultasOdontoUrgenciaConfirmadas(this.getNumeroConsultasOdontoUrgenciaConfirmadas() - 1);
				
				this.setNumeroConsultasOdontoCanceladas(this.getNumeroConsultasOdontoCanceladas() + 1);
			}
		}
		else {
			this.setNumeroConsultasOdontoUrgenciaConfirmadas(this.getNumeroConsultasOdontoUrgenciaConfirmadas() + 1);
			this.setSomatorioConsultasOdontoUrgenciaConfirmadas(this.getSomatorioConsultasOdontoUrgenciaConfirmadas().add(valorConsulta));
		}
	}
	
	
	
	
	
	

	public BigDecimal getSomatorioExames() {
		return this.somatorioExamesAgendados.add(this.somatorioExamesConfirmados);
	}
	
	public BigDecimal getSomatorioTratamentosOdonto() {
		return this.somatorioTratamentosOdontoAgendados.add(somatorioTratamentosOdontoConfirmados);
	}

	public void incrementaSomatorioExamesAgendados(BigDecimal valorExame, int numeroDeProcedimentos,TipoIncremento tipoIncremento) {
		if (this.getIdConsumo() != null){
			this.setSomatorioExamesAgendados(this.getSomatorioExamesAgendados().add(valorExame));
			
			if(TipoIncremento.POSITIVO.equals(tipoIncremento)){
				this.setNumeroExamesAgendados(this.getNumeroExamesAgendados() + 1);
				this.setNumeroProcedimentosExamesAgendados(this.getNumeroProcedimentosExamesAgendados() + numeroDeProcedimentos);
			}
			else {
				if(this.getNumeroExamesAgendados() < 0)
					this.setNumeroExamesAgendados(0);
				else
					this.setNumeroExamesAgendados(this.getNumeroExamesAgendados() - 1);
				
				if(this.getNumeroProcedimentosExamesAgendados() < 0)
					this.setNumeroProcedimentosExamesAgendados(0);
				else
					this.setNumeroProcedimentosExamesAgendados(this.getNumeroProcedimentosExamesAgendados() - numeroDeProcedimentos);

				this.setNumeroExamesCancelados(this.getNumeroExamesCancelados() + 1);
			}
		}
		else {
			this.setNumeroExamesAgendados(this.getNumeroExamesAgendados() + 1);
			this.setNumeroProcedimentosExamesAgendados(this.getNumeroProcedimentosExamesAgendados() + numeroDeProcedimentos);
			this.setSomatorioExamesAgendados(this.getSomatorioExamesAgendados().add(valorExame));
		}
	}
	
	public void incrementaSomatorioTratamentosOdontoAgendados(BigDecimal valorTratamento, int numeroDeProcedimentos,TipoIncremento tipoIncremento) {
		if (this.getIdConsumo() != null){
			this.setSomatorioTratamentosOdontoAgendados(this.getSomatorioTratamentosOdontoAgendados().add(valorTratamento));
			
			if(TipoIncremento.POSITIVO.equals(tipoIncremento)){
				this.setNumeroTratamentosOdontoAgendados(this.getNumeroTratamentosOdontoAgendados() + 1);
				this.setNumeroProcedimentosOdontoAgendados(this.getNumeroProcedimentosOdontoAgendados() + numeroDeProcedimentos);
			}
			else {
				if(this.getNumeroTratamentosOdontoAgendados() < 0)
					this.setNumeroTratamentosOdontoAgendados(0);
				else
					this.setNumeroTratamentosOdontoAgendados(this.getNumeroTratamentosOdontoAgendados() - 1);
				
				if(this.getNumeroProcedimentosOdontoAgendados() < 0)
					this.setNumeroProcedimentosOdontoAgendados(0);
				else
					this.setNumeroProcedimentosOdontoAgendados(this.getNumeroProcedimentosOdontoAgendados() - numeroDeProcedimentos);

				this.setNumeroTratamentosOdontoCancelados(this.getNumeroTratamentosOdontoCancelados() + 1);
			}
		}
		else {
			this.setNumeroTratamentosOdontoAgendados(this.getNumeroTratamentosOdontoAgendados() + 1);
			this.setNumeroProcedimentosOdontoAgendados(this.getNumeroProcedimentosOdontoAgendados() + numeroDeProcedimentos);
			this.setSomatorioTratamentosOdontoAgendados(this.getSomatorioTratamentosOdontoAgendados().add(valorTratamento));
		}
	}

	public void incrementaSomatorioExamesConfirmados(BigDecimal valorExame, int numeroDeProcedimentos,TipoIncremento tipoIncremento, Date competencia, boolean decrementaProcedimentosAgendados, boolean decrementaExamesAgendados) {
		if (this.getIdConsumo() != null){
			this.setSomatorioExamesConfirmados(this.getSomatorioExamesConfirmados().add(valorExame));
			if(TipoIncremento.POSITIVO.equals(tipoIncremento)){
				this.setNumeroExamesConfirmados(this.getNumeroExamesConfirmados() + 1);
				this.setNumeroProcedimentosExamesConfirmados(this.getNumeroProcedimentosExamesConfirmados() + numeroDeProcedimentos);
				if(Utils.between(this.getDataInicial(), this.getDataFinal(), competencia) && decrementaProcedimentosAgendados){
					
					if(decrementaProcedimentosAgendados)
						this.setSomatorioExamesAgendados(this.getSomatorioExamesAgendados().subtract(valorExame));
					
					if(decrementaExamesAgendados){
						if(this.getNumeroExamesAgendados() < 0)
							this.setNumeroExamesAgendados(0);
						else
							this.setNumeroExamesAgendados(this.getNumeroExamesAgendados() - 1);
						this.setNumeroProcedimentosExamesAgendados(this.getNumeroProcedimentosExamesAgendados() - numeroDeProcedimentos);
					}
				}
			}
			else {
				if(this.getNumeroExamesConfirmados() < 0)
					this.setNumeroExamesConfirmados(0);
				else
					this.setNumeroExamesConfirmados(this.getNumeroExamesConfirmados() - 1);
				
				if(this.getNumeroProcedimentosExamesConfirmados() < 0)
					this.setNumeroProcedimentosExamesConfirmados(0);
				else
					this.setNumeroProcedimentosExamesConfirmados(this.getNumeroProcedimentosExamesConfirmados() - numeroDeProcedimentos);
				
				this.setNumeroExamesCancelados(this.getNumeroExamesCancelados() + 1);
			}
		}
		else {
			this.setNumeroExamesConfirmados(this.getNumeroExamesConfirmados() + 1);
			this.setNumeroProcedimentosExamesConfirmados(this.getNumeroProcedimentosExamesConfirmados() + numeroDeProcedimentos);
			this.setSomatorioExamesConfirmados(this.getSomatorioExamesConfirmados().add(valorExame));
		}
	}
	
	public void incrementaSomatorioTratamentosOdontoConfirmados(BigDecimal valorTratamento,TipoIncremento tipoIncremento, int numeroDeProcedimentos, Date competencia, boolean decrementaAgendadas) {
		if (this.getIdConsumo() != null){
			this.setSomatorioTratamentosOdontoConfirmados(this.getSomatorioTratamentosOdontoConfirmados().add(valorTratamento));
			if(TipoIncremento.POSITIVO.equals(tipoIncremento)){
				this.setNumeroTratamentosOdontoConfirmados(this.getNumeroTratamentosOdontoConfirmados() + 1);
				this.setNumeroProcedimentosOdontoConfirmados(this.getNumeroProcedimentosOdontoConfirmados() + numeroDeProcedimentos);
				if(Utils.between(this.getDataInicial(), this.getDataFinal(), competencia) && decrementaAgendadas){
					
					this.setSomatorioTratamentosOdontoAgendados(this.getSomatorioTratamentosOdontoAgendados().subtract(valorTratamento));
					
					if(this.getNumeroTratamentosOdontoAgendados() < 0)
						this.setNumeroTratamentosOdontoAgendados(0);
					else
						this.setNumeroTratamentosOdontoAgendados(this.getNumeroTratamentosOdontoAgendados() - 1);
					
					this.setNumeroProcedimentosOdontoAgendados(this.getNumeroProcedimentosOdontoAgendados() - numeroDeProcedimentos);
				}
			}
			else {
				if(this.getNumeroTratamentosOdontoConfirmados() < 0)
					this.setNumeroTratamentosOdontoConfirmados(0);
				else
					this.setNumeroTratamentosOdontoConfirmados(this.getNumeroTratamentosOdontoConfirmados() - 1);
				
				if(this.getNumeroProcedimentosOdontoConfirmados() < 0)
					this.setNumeroProcedimentosOdontoConfirmados(0);
				else
					this.setNumeroProcedimentosOdontoConfirmados(this.getNumeroProcedimentosOdontoConfirmados() - numeroDeProcedimentos);
				
				this.setNumeroTratamentosOdontoCancelados(this.getNumeroTratamentosOdontoCancelados() + 1);
			}
		}
		else {
			this.setNumeroTratamentosOdontoConfirmados(this.getNumeroTratamentosOdontoConfirmados()+1);
			this.setNumeroProcedimentosOdontoConfirmados(this.getNumeroProcedimentosOdontoConfirmados()+numeroDeProcedimentos);
			this.setSomatorioTratamentosOdontoConfirmados(this.getSomatorioTratamentosOdontoConfirmados().add(valorTratamento));
		}
	}
	
	public Date getDataInicial() {
		return dataInicial;
	}
	
	public String getDataInicialFormatada() {
		return Utils.format(getDataInicial());
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public String getDataFinalFormatada() {
		return Utils.format(getDataFinal());
	}
	
	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}
	
	/**
	 * Retorna o primeiro dia de limite válido do período.
	 * @return Date
	 */
	public Date getProximaDataValida() {
		return Utils.incrementaDias(dataFinal, 1);
	}

	public Periodo getPeriodo() {
		int tipoPeriodo = this.getTipoPeriodo();
		
		for(Periodo periodo : Periodo.values()){
			if(periodo.getValor() == tipoPeriodo)
				return periodo;
		}
		return null;
	}

	public void setPeriodo(Periodo periodo) {
		this.periodo = periodo;
		this.setTipoPeriodo(periodo.getValor());
	}
	
	public Integer getTipoPeriodo() {
		return tipoPeriodo;
	}

	public void setTipoPeriodo(Integer tipoPeriodo) {
		this.tipoPeriodo = tipoPeriodo;
	}
	
	public ColecaoConsumosInterface getColecaoConsumos() {
		return colecaoConsumos;
	}

	public void setColecaoConsumos(ColecaoConsumosInterface colecaoConsumos) {
		this.colecaoConsumos = colecaoConsumos;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public Float getConsumoGeral(){
		BigDecimal consumoGeral = BigDecimal.ZERO;
		consumoGeral = consumoGeral.add(this.getSomatorioConsultasConfirmadas());
		consumoGeral = consumoGeral.add(this.getSomatorioExamesConfirmados());
		consumoGeral = consumoGeral.add(this.getSomatorioInternacoes());
		consumoGeral = consumoGeral.add(this.getSomatorioUrgencias());
		consumoGeral = consumoGeral.add(this.getSomatorioConsultasOdontoConfirmadas());
		consumoGeral = consumoGeral.add(this.getSomatorioTratamentosOdonto());
		return consumoGeral.floatValue();
	}

	public int getNumeroGeral(){
		return this.getNumeroConsultasConfirmadas() + this.getNumeroExamesConfirmados() + this.getNumeroInternacoes() + 
			this.getNumeroUrgencias() + this.getNumeroConsultasOdontoConfirmadas() + this.getNumeroTratamentosOdonto();
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ConsumoInterface)) {
			return false;
		}
		ConsumoInterface otherObject = (ConsumoInterface) object;
		return new EqualsBuilder()
			.append(this.idConsumo, otherObject.getIdConsumo())
			.append(this.dataInicial, otherObject.getDataInicial())
			.append(this.dataFinal, otherObject.getDataFinal())
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.idConsumo)
			.append(this.dataInicial)
			.append(this.dataFinal)
			.append(this.tipoPeriodo)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
			.append("id", this.idConsumo)
			.append("Data Inicial", this.dataInicial)
			.append("Data Final", this.dataFinal)
			.append("Tipo periodo", this.tipoPeriodo)
			.append("limite consultas", this.limiteConsultas)
			.append("limite exames", this.limiteExames)
			.append("limite consultas odontológicas", this.limiteConsultasOdonto)
			.append("limite procedimentos odontológico", this.limiteTratamentosOdonto)
			.append("soma das consultas", this.getSomatorioConsultas())
			.append("soma dos exames", this.getSomatorioExames())
			.append("soma das consultas odontológicas", this.getSomatorioConsultasOdontoConfirmadas())
			.append("soma dos tratamentos odontológicos", this.getSomatorioTratamentosOdonto())
			.toString();
	}

	public int compareTo(Object o) {
		ConsumoInterface consumo = (ConsumoInterface)o;
		return this.getDescricao().compareTo(consumo.getDescricao());
	}

	public int getNumeroConsultas() {
		return this.numeroConsultasAgendadas + this.numeroConsultasConfirmadas;
	}

	public int getNumeroExames() {
		return this.numeroExamesAgendados + this.numeroExamesConfirmados;
	}
	
	public int getNumeroTratamentosOdonto() {
		return this.numeroTratamentosOdontoAgendados + this.numeroTratamentosOdontoConfirmados;
	}
	
	public void incrementaSomatorioInternacoesAbertas(BigDecimal valorInternacao, int numeroDeProcedimentos,TipoIncremento tipoIncremento) {
		if (this.getIdConsumo() != null){
			this.setSomatorioInternacoesAbertas(this.getSomatorioInternacoesAbertas().add(valorInternacao));
			
			if(TipoIncremento.POSITIVO.equals(tipoIncremento)){
				this.setNumeroInternacoesAbertas(this.getNumeroInternacoesAbertas() + 1);
				this.setNumeroProcedimentosInternacoesAbertas(this.getNumeroProcedimentosInternacoesAbertas() + numeroDeProcedimentos);					
			}
			else {
				if(this.getNumeroInternacoesAbertas() < 0)
					this.setNumeroInternacoesAbertas(0);
				else
					this.setNumeroInternacoesAbertas(this.getNumeroInternacoesAbertas() - 1);
				
				if(this.getNumeroProcedimentosInternacoesAbertas() < 0)
					this.setNumeroProcedimentosInternacoesAbertas(0);
				else
					this.setNumeroProcedimentosInternacoesAbertas(this.getNumeroProcedimentosInternacoesAbertas() - numeroDeProcedimentos);
				
				this.setNumeroInternacoesCanceladas(this.getNumeroInternacoesCanceladas() + 1);
			}
		}
		else {
			this.setNumeroInternacoesAbertas(this.getNumeroInternacoesAbertas() + 1);
			this.setSomatorioInternacoesAbertas(this.getSomatorioInternacoesAbertas().add(valorInternacao));
			this.setNumeroProcedimentosInternacoesAbertas(this.getNumeroProcedimentosInternacoesAbertas() + numeroDeProcedimentos);
		}
		
	}
	
	public void incrementaSomatorioInternacoesFechadas(BigDecimal valorPreFechamento, int numeroDeProcedimentosPreFechamento,BigDecimal valorPosFechamento, int numeroDeProcedimentosPosFechamento,TipoIncremento tipoIncremento) {
		if (this.getIdConsumo() != null){
			this.setSomatorioInternacoesFechadas(this.getSomatorioInternacoesFechadas().add(valorPosFechamento));
			if(TipoIncremento.POSITIVO.equals(tipoIncremento)){
				this.setNumeroInternacoesFechadas(this.getNumeroInternacoesFechadas() + 1);
				this.setNumeroProcedimentosInternacoesFechadas(this.getNumeroProcedimentosInternacoesFechadas() + numeroDeProcedimentosPosFechamento);
				this.setSomatorioInternacoesAbertas(this.getSomatorioInternacoesAbertas().subtract(valorPreFechamento));
				
				if(this.getNumeroInternacoesAbertas() < 0)
					this.setNumeroInternacoesAbertas(0);
				else
					this.setNumeroInternacoesAbertas(this.getNumeroInternacoesAbertas() - 1);
				
				if(this.getNumeroProcedimentosInternacoesAbertas() < 0)
					this.setNumeroProcedimentosInternacoesAbertas(0);
				else
					this.setNumeroProcedimentosInternacoesAbertas(this.getNumeroProcedimentosInternacoesAbertas() - numeroDeProcedimentosPreFechamento);
			}
			else {
				if(this.getNumeroInternacoesFechadas() < 0)
					this.setNumeroInternacoesFechadas(0);
				else
					this.setNumeroInternacoesFechadas(this.getNumeroInternacoesFechadas() - 1);
				
				if(this.getNumeroProcedimentosInternacoesFechadas() < 0)
					this.setNumeroProcedimentosInternacoesFechadas(0);
				else
					this.setNumeroProcedimentosInternacoesFechadas(this.getNumeroProcedimentosInternacoesFechadas() - numeroDeProcedimentosPosFechamento);
				
				this.setNumeroInternacoesCanceladas(this.getNumeroInternacoesCanceladas() + 1);
			}
		}
		else {
			this.setNumeroInternacoesFechadas(this.getNumeroInternacoesFechadas() + 1);
			this.setSomatorioInternacoesFechadas(this.getSomatorioInternacoesFechadas().add(valorPosFechamento));
			this.setNumeroProcedimentosInternacoesAbertas(this.getNumeroProcedimentosInternacoesAbertas() + numeroDeProcedimentosPosFechamento);
		}
	}

	public void incrementaSomatorioUrgenciasAbertas(BigDecimal valorInternacao, int numeroDeProcedimentos,TipoIncremento tipoIncremento) {
		if (this.getIdConsumo() != null){
			this.setSomatorioUrgenciasAbertas(this.getSomatorioUrgenciasAbertas().add(valorInternacao));
			if(TipoIncremento.POSITIVO.equals(tipoIncremento)){
				this.setNumeroUrgenciasAbertas(this.getNumeroUrgenciasAbertas() + 1);
				this.setNumeroProcedimentosUrgenciasAbertas(this.getNumeroProcedimentosUrgenciasAbertas() + numeroDeProcedimentos);					
			}
			else {
				if(this.getNumeroUrgenciasAbertas() < 0)
					this.setNumeroUrgenciasAbertas(0);
				else
					this.setNumeroUrgenciasAbertas(this.getNumeroUrgenciasAbertas() - 1);
				
				if(this.getNumeroProcedimentosUrgenciasAbertas() < 0)
					this.setNumeroProcedimentosUrgenciasAbertas(0);
				else
					this.setNumeroProcedimentosUrgenciasAbertas(this.getNumeroProcedimentosUrgenciasAbertas() - numeroDeProcedimentos);
				
				this.setNumeroUrgenciasCanceladas(this.getNumeroUrgenciasCanceladas() + 1);
			}
		}
		else {
			this.setNumeroUrgenciasAbertas(this.getNumeroUrgenciasAbertas()+1);
			this.setSomatorioUrgenciasAbertas(this.getSomatorioUrgenciasAbertas().add(valorInternacao));
		}
	}
	
	public void incrementaSomatorioUrgenciasFechadas(BigDecimal valorPreFechamento, int numeroDeProcedimentosPreFechamento,BigDecimal valorPosFechamento, int numeroDeProcedimentosPosFechamento,TipoIncremento tipoIncremento) {
		if (this.getIdConsumo() != null){
			this.setSomatorioUrgenciasFechadas(this.getSomatorioUrgenciasFechadas().add(valorPosFechamento));
			if(TipoIncremento.POSITIVO.equals(tipoIncremento)){
				this.setNumeroUrgenciasFechadas(this.getNumeroUrgenciasFechadas() + 1);
				this.setNumeroProcedimentosUrgenciasFechadas(this.getNumeroProcedimentosUrgenciasFechadas() + numeroDeProcedimentosPosFechamento);
				
				this.setSomatorioUrgenciasAbertas(this.getSomatorioUrgenciasAbertas().subtract(valorPreFechamento));
				
				if(this.getNumeroUrgenciasAbertas() < 0)
					this.setNumeroUrgenciasAbertas(0);
				else
					this.setNumeroUrgenciasAbertas(this.getNumeroUrgenciasAbertas() - 1);
				
				if(this.getNumeroProcedimentosUrgenciasAbertas() < 0)
					this.setNumeroProcedimentosUrgenciasAbertas(0);
				else
					this.setNumeroProcedimentosUrgenciasAbertas(this.getNumeroProcedimentosUrgenciasAbertas() - numeroDeProcedimentosPreFechamento);
			}
			else {
				if(this.getNumeroUrgenciasFechadas() < 0)
					this.setNumeroUrgenciasFechadas(0);
				else
					this.setNumeroUrgenciasFechadas(this.getNumeroUrgenciasFechadas() - 1);
				
				if(this.getNumeroProcedimentosUrgenciasFechadas() < 0)
					this.setNumeroProcedimentosUrgenciasFechadas(0);
				else
					this.setNumeroProcedimentosUrgenciasFechadas(this.getNumeroProcedimentosUrgenciasFechadas() - numeroDeProcedimentosPosFechamento);
				
				this.setNumeroUrgenciasCanceladas(this.getNumeroUrgenciasCanceladas() + 1);
			}
		}
		else {
			this.setNumeroUrgenciasFechadas(this.getNumeroUrgenciasFechadas()+1);
			this.setSomatorioUrgenciasFechadas(this.getSomatorioUrgenciasFechadas().add(valorPosFechamento));
			this.setNumeroProcedimentosUrgenciasFechadas(this.getNumeroProcedimentosUrgenciasFechadas()+numeroDeProcedimentosPosFechamento);
			
		}
	}
	
	public void incrementaSomatorioConsultasAtendimentosUrgenciasAbertas(BigDecimal valorInternacao, int numeroDeProcedimentos,TipoIncremento tipoIncremento) {
		if (this.getIdConsumo() != null){
			this.setSomatorioConsultasAtendimentosUrgenciasAbertas(this.getSomatorioConsultasAtendimentosUrgenciasAbertas().add(valorInternacao));
			if(TipoIncremento.POSITIVO.equals(tipoIncremento)){
				this.setNumeroConsultasAtendimentosUrgenciasAbertas(this.getNumeroConsultasAtendimentosUrgenciasAbertas() + 1);
				this.setNumeroProcedimentosConsultasAtendimentosUrgenciasAbertas(this.getNumeroProcedimentosConsultasAtendimentosUrgenciasAbertas() + numeroDeProcedimentos);					
			}
			else {
				if(this.getNumeroConsultasAtendimentosUrgenciasAbertas() < 0)
					this.setNumeroConsultasAtendimentosUrgenciasAbertas(0);
				else
					this.setNumeroConsultasAtendimentosUrgenciasAbertas(this.getNumeroConsultasAtendimentosUrgenciasAbertas() - 1);
				
				if(this.getNumeroProcedimentosConsultasAtendimentosUrgenciasAbertas() < 0)
					this.setNumeroProcedimentosConsultasAtendimentosUrgenciasAbertas(0);
				else
					this.setNumeroProcedimentosConsultasAtendimentosUrgenciasAbertas(this.getNumeroProcedimentosConsultasAtendimentosUrgenciasAbertas() - numeroDeProcedimentos);
				
				this.setNumeroConsultasAtendimentosUrgenciasCanceladas(this.getNumeroConsultasAtendimentosUrgenciasCanceladas() + 1);
			}
		}
		else {
			this.setNumeroConsultasAtendimentosUrgenciasAbertas(this.getNumeroConsultasAtendimentosUrgenciasAbertas()+1);
			this.setSomatorioConsultasAtendimentosUrgenciasAbertas(this.getSomatorioConsultasAtendimentosUrgenciasAbertas().add(valorInternacao));
		}
			
	}
	
	public void incrementaSomatorioConsultasAtendimentosUrgenciasFechadas(BigDecimal valorPreFechamento, int numeroDeProcedimentosPreFechamento,BigDecimal valorPosFechamento, int numeroDeProcedimentosPosFechamento,TipoIncremento tipoIncremento) {
		if (this.getIdConsumo() != null){
			this.setSomatorioConsultasAtendimentosUrgenciasFechadas(this.getSomatorioConsultasAtendimentosUrgenciasFechadas().add(valorPosFechamento));
			if(TipoIncremento.POSITIVO.equals(tipoIncremento)){
				this.setNumeroConsultasAtendimentosUrgenciasFechadas(this.getNumeroConsultasAtendimentosUrgenciasFechadas() + 1);
				this.setNumeroProcedimentosConsultasAtendimentosUrgenciasFechadas(this.getNumeroProcedimentosConsultasAtendimentosUrgenciasFechadas() + numeroDeProcedimentosPosFechamento);
				
				this.setSomatorioConsultasAtendimentosUrgenciasAbertas(this.getSomatorioConsultasAtendimentosUrgenciasAbertas().subtract(valorPreFechamento));
				
				if(this.getNumeroConsultasAtendimentosUrgenciasAbertas() < 0)
					this.setNumeroConsultasAtendimentosUrgenciasAbertas(0);
				else
					this.setNumeroConsultasAtendimentosUrgenciasAbertas(this.getNumeroConsultasAtendimentosUrgenciasAbertas() - 1);
				
				if(this.getNumeroProcedimentosConsultasAtendimentosUrgenciasAbertas() < 0)
					this.setNumeroProcedimentosConsultasAtendimentosUrgenciasAbertas(0);
				else
					this.setNumeroProcedimentosConsultasAtendimentosUrgenciasAbertas(this.getNumeroProcedimentosConsultasAtendimentosUrgenciasAbertas() - numeroDeProcedimentosPreFechamento);
			}
			else {
				if(this.getNumeroConsultasAtendimentosUrgenciasFechadas() < 0)
					this.setNumeroConsultasAtendimentosUrgenciasFechadas(0);
				else
					this.setNumeroConsultasAtendimentosUrgenciasFechadas(this.getNumeroConsultasAtendimentosUrgenciasFechadas() - 1);
				
				if(this.getNumeroProcedimentosConsultasAtendimentosUrgenciasFechadas() < 0)
					this.setNumeroProcedimentosConsultasAtendimentosUrgenciasFechadas(0);
				else
					this.setNumeroProcedimentosConsultasAtendimentosUrgenciasFechadas(this.getNumeroProcedimentosConsultasAtendimentosUrgenciasFechadas() - numeroDeProcedimentosPosFechamento);
				
				this.setNumeroConsultasAtendimentosUrgenciasCanceladas(this.getNumeroConsultasAtendimentosUrgenciasCanceladas() + 1);
			}
		}
		else {
			this.setNumeroConsultasAtendimentosUrgenciasFechadas(this.getNumeroConsultasAtendimentosUrgenciasFechadas()+1);
			this.setSomatorioConsultasAtendimentosUrgenciasFechadas(this.getSomatorioConsultasAtendimentosUrgenciasFechadas().add(valorPosFechamento));
			this.setNumeroProcedimentosConsultasAtendimentosUrgenciasFechadas(this.getNumeroProcedimentosConsultasAtendimentosUrgenciasFechadas()+numeroDeProcedimentosPosFechamento);
			
		}
			
	}
	
	public void atualizaSomatorioExamesAgendados(BigDecimal valorExame,int numeroDeProcedimentos,TipoIncremento tipoIncremento) {
		this.setSomatorioExamesAgendados(this.getSomatorioExamesAgendados().add(valorExame));
		
		if(TipoIncremento.POSITIVO.equals(tipoIncremento)){
			this.setNumeroProcedimentosExamesAgendados(this.getNumeroProcedimentosExamesAgendados() + numeroDeProcedimentos);
		}
		else {
			if(this.getNumeroProcedimentosExamesAgendados() < 0)
				this.setNumeroProcedimentosExamesAgendados(0);
			else
				this.setNumeroProcedimentosExamesAgendados(this.getNumeroProcedimentosExamesAgendados() - numeroDeProcedimentos);
		}
	}
	
	public void atualizaSomatorioExamesConfirmados(BigDecimal valorExame,int numeroDeProcedimentos,TipoIncremento tipoIncremento) {
		this.setSomatorioExamesConfirmados(this.getSomatorioExamesConfirmados().add(valorExame));
		
		if(TipoIncremento.POSITIVO.equals(tipoIncremento)){
			this.setNumeroProcedimentosExamesConfirmados(this.getNumeroProcedimentosExamesConfirmados() + numeroDeProcedimentos);
		}
		else {
			if(this.getNumeroProcedimentosExamesConfirmados() < 0)
				this.setNumeroProcedimentosExamesConfirmados(0);
			else
				this.setNumeroProcedimentosExamesConfirmados(this.getNumeroProcedimentosExamesConfirmados() - numeroDeProcedimentos);
		}
	}
	
	public void atualizaSomatorioInternacoesAbertas(BigDecimal valorInternacao,int numeroDeProcedimentos,TipoIncremento tipoIncremento) {
		this.setSomatorioInternacoesAbertas(this.getSomatorioInternacoesAbertas().add(valorInternacao));
		
		if(TipoIncremento.POSITIVO.equals(tipoIncremento)){
			this.setNumeroProcedimentosInternacoesAbertas(this.getNumeroProcedimentosInternacoesAbertas() + numeroDeProcedimentos);
		}
		else {
			if(this.getNumeroProcedimentosInternacoesAbertas() < 0)
				this.setNumeroProcedimentosInternacoesAbertas(0);
			else
				this.setNumeroProcedimentosInternacoesAbertas(this.getNumeroProcedimentosInternacoesAbertas() - numeroDeProcedimentos);
		}
	}
	
	public void atualizaSomatorioInternacoesFechadas(BigDecimal valorInternacao,int numeroDeProcedimentos,TipoIncremento tipoIncremento) {
		this.setSomatorioInternacoesFechadas(this.getSomatorioInternacoesFechadas().add(valorInternacao));
		
		if(TipoIncremento.POSITIVO.equals(tipoIncremento)){
			this.setNumeroProcedimentosInternacoesFechadas(this.getNumeroProcedimentosInternacoesFechadas() + numeroDeProcedimentos);
		}
		else {
			if(this.getNumeroProcedimentosInternacoesFechadas() < 0)
				this.setNumeroProcedimentosInternacoesFechadas(0);
			else
				this.setNumeroProcedimentosInternacoesFechadas(this.getNumeroProcedimentosInternacoesFechadas() - numeroDeProcedimentos);
		}
	}
	
	public void atualizaSomatorioUrgenciasAbertas(BigDecimal valorInternacao,int numeroDeProcedimentos,TipoIncremento tipoIncremento) {
		this.setSomatorioUrgenciasAbertas(this.getSomatorioUrgenciasAbertas().add(valorInternacao));
		
		if(TipoIncremento.POSITIVO.equals(tipoIncremento)){
			this.setNumeroProcedimentosUrgenciasAbertas(this.getNumeroProcedimentosUrgenciasAbertas() + numeroDeProcedimentos);
		}
		else {
			if(this.getNumeroProcedimentosUrgenciasAbertas() < 0)
				this.setNumeroProcedimentosUrgenciasAbertas(0);
			else
				this.setNumeroProcedimentosUrgenciasAbertas(this.getNumeroProcedimentosUrgenciasAbertas() - numeroDeProcedimentos);
			
		}
	}
	
	public void atualizaSomatorioUrgenciasFechadas(BigDecimal valorInternacao,int numeroDeProcedimentos,TipoIncremento tipoIncremento) {
		this.setSomatorioUrgenciasFechadas(this.getSomatorioUrgenciasFechadas().add(valorInternacao));
		
		if(TipoIncremento.POSITIVO.equals(tipoIncremento)){
			this.setNumeroProcedimentosUrgenciasFechadas(this.getNumeroProcedimentosUrgenciasFechadas() + numeroDeProcedimentos);
		}
		else {
			
			if(this.getNumeroProcedimentosUrgenciasFechadas() < 0)
				this.setNumeroProcedimentosUrgenciasFechadas(0);
			else
				this.setNumeroProcedimentosUrgenciasFechadas(this.getNumeroProcedimentosUrgenciasFechadas() - numeroDeProcedimentos);
		}
	}
	
	public void atualizaSomatorioConsultasAtendimentosUrgenciasAbertas(BigDecimal valorInternacao,int numeroDeProcedimentos,TipoIncremento tipoIncremento) {
		
	}
	public void atualizaSomatorioConsultasAtendimentosUrgenciasFechadas(BigDecimal valorInternacao,int numeroDeProcedimentos,TipoIncremento tipoIncremento) {
		
	}
	
	public int getNumeroUrgencias() {
		return this.getNumeroUrgenciasAbertas() + this.getNumeroUrgenciasFechadas();
	}
	
	public BigDecimal getSomatorioUrgencias() {
		return this.getSomatorioUrgenciasAbertas().add(this.getSomatorioUrgenciasFechadas());
	}

	public int getNumeroConsultasAtendimentosUrgencias() {
		return this.getNumeroConsultasAtendimentosUrgenciasAbertas() + this.getNumeroConsultasAtendimentosUrgenciasFechadas();
	}
	
	public BigDecimal getSomatorioConsultasAtendimentosUrgencias() {
		return this.getSomatorioConsultasAtendimentosUrgenciasAbertas().add(this.getSomatorioConsultasAtendimentosUrgenciasFechadas());
	}

	public int getNumeroInternacoes() {
		return this.getNumeroInternacoesAbertas() + this.getNumeroInternacoesFechadas();
	}

	public BigDecimal getSomatorioInternacoes() {
		return this.getSomatorioInternacoesAbertas().add(this.getSomatorioInternacoesFechadas());
	}

	public int getNumeroProcedimentosExames(){
		return this.getNumeroProcedimentosExamesAgendados() + this.getNumeroProcedimentosExamesConfirmados();
		
	}

	public boolean isLimiteEstourado(GuiaSimples guia) {
		
		if(guia.isConsulta()){
			if(guia.isConsultaOdonto()) {
				return this.getLimiteConsultasOdonto().compareTo(this.getSomatorioConsultasOdontoConfirmadas()) <= 0;
			}else{
				return this.getLimiteConsultas().compareTo(this.getSomatorioConsultasAgendadas().add(this.getSomatorioConsultasConfirmadas())) <= 0;
			}
		}
		
		if(guia.isExame()){
			if(guia.isExameOdonto()){
				return this.getLimiteTratamentosOdonto().compareTo(new BigDecimal(this.getNumeroTratamentosOdontoAgendados()).add(new BigDecimal(this.getNumeroTratamentosOdontoConfirmados()))) <= 0;
			}else{
				boolean isConsumoAnual = this.getPeriodo().equals(Periodo.ANUAL);

				if(isConsumoAnual){
					int saldoParaExames = this.getLimiteExames().subtract(new BigDecimal(this.getNumeroProcedimentosExamesAgendados() +  this.getNumeroProcedimentosExamesConfirmados())).intValue();
					boolean isLimiteEstourado = saldoParaExames <= 0;

					//interrompe o processamento
					if(isLimiteEstourado){
						return true;
					}

					if (isConsumoAnual){
						//Só valida o consumo anual de exames
						if (saldoParaExames < guia.getQuantidadeProcedimentosValidosParaConsumo()){
							throw new ConsumoException(MensagemErroEnum.SEGURADO_SEM_LIMITE_PARA_CONSUMO_DE_EXAMES.getMessage(String.valueOf(saldoParaExames),"EXAMES"));
						}
					}				
				}
				return false;
			}
		}
		
		return false;
	}
	
	public int getNumeroProcedimentosUrgencias(){
		return this.getNumeroProcedimentosUrgenciasAbertas() + this.getNumeroProcedimentosUrgenciasFechadas();
	}
	
	public int getNumeroProcedimentosInternacoes(){
		return this.getNumeroProcedimentosInternacoesAbertas() + this.getNumeroProcedimentosInternacoesFechadas();
	}
	
	public int getNumeroProcedimentos(){
		return this.getNumeroProcedimentosExames() + this.getNumeroProcedimentosInternacoes() + this.getNumeroProcedimentosUrgencias(); 
		
	}

	public BigDecimal getLimiteConsultas() {
		return limiteConsultas;
	}

	public BigDecimal getLimiteConsultasOdonto() {
		return limiteConsultasOdonto;
	}

	public BigDecimal getLimiteExames() {
		return limiteExames;
	}

	public BigDecimal getLimiteInternacoes() {
		return limiteInternacoes;
	}

	public BigDecimal getLimiteTratamentosOdonto() {
		return limiteTratamentosOdonto;
	}

	public BigDecimal getLimiteUrgencias() {
		return limiteUrgencias;
	}

	public int getNumeroConsultasAgendadas() {
		return numeroConsultasAgendadas;
	}

	public void setNumeroConsultasAgendadas(int numeroConsultasAgendadas) {
		this.numeroConsultasAgendadas = numeroConsultasAgendadas;
	}

	public int getNumeroConsultasCanceladas() {
		return numeroConsultasCanceladas;
	}

	public void setNumeroConsultasCanceladas(int numeroConsultasCanceladas) {
		this.numeroConsultasCanceladas = numeroConsultasCanceladas;
	}

	public int getNumeroConsultasConfirmadas() {
		return numeroConsultasConfirmadas;
	}

	public void setNumeroConsultasConfirmadas(int numeroConsultasConfirmadas) {
		this.numeroConsultasConfirmadas = numeroConsultasConfirmadas;
	}

	public int getNumeroConsultasOdontoCanceladas() {
		return numeroConsultasOdontoCanceladas;
	}

	public void setNumeroConsultasOdontoCanceladas(
			int numeroConsultasOdontoCanceladas) {
		this.numeroConsultasOdontoCanceladas = numeroConsultasOdontoCanceladas;
	}

	public int getNumeroConsultasOdontoConfirmadas() {
		return numeroConsultasOdontoConfirmadas;
	}

	public void setNumeroConsultasOdontoConfirmadas(
			int numeroConsultasOdontoConfirmadas) {
		this.numeroConsultasOdontoConfirmadas = numeroConsultasOdontoConfirmadas;
	}

	public int getNumeroExamesAgendados() {
		return numeroExamesAgendados;
	}
	public void setNumeroExamesAgendados(int numeroExamesAgendados) {
		this.numeroExamesAgendados = numeroExamesAgendados;
	}

	public int getNumeroExamesCancelados() {
		return numeroExamesCancelados;
	}
	public void setNumeroExamesCancelados(int numeroExamesCancelados) {
		this.numeroExamesCancelados = numeroExamesCancelados;
	}

	public int getNumeroExamesConfirmados() {
		return numeroExamesConfirmados;
	}

	public void setNumeroExamesConfirmados(int numeroExamesConfirmados) {
		this.numeroExamesConfirmados = numeroExamesConfirmados;
	}

	public int getNumeroInternacoesAbertas() {
		return numeroInternacoesAbertas;
	}

	public void setNumeroInternacoesAbertas(int numeroInternacoesAbertas) {
		this.numeroInternacoesAbertas = numeroInternacoesAbertas;
	}

	public int getNumeroInternacoesCanceladas() {
		return numeroInternacoesCanceladas;
	}

	public void setNumeroInternacoesCanceladas(int numeroInternacoesCanceladas) {
		this.numeroInternacoesCanceladas = numeroInternacoesCanceladas;
	}

	public int getNumeroInternacoesFechadas() {
		return numeroInternacoesFechadas;
	}

	public void setNumeroInternacoesFechadas(int numeroInternacoesFechadas) {
		this.numeroInternacoesFechadas = numeroInternacoesFechadas;
	}

	public int getNumeroProcedimentosExamesAgendados() {
		return numeroProcedimentosExamesAgendados;
	}

	public void setNumeroProcedimentosExamesAgendados(
			int numeroProcedimentosExamesAgendados) {
		this.numeroProcedimentosExamesAgendados = numeroProcedimentosExamesAgendados;
	}

	public int getNumeroProcedimentosExamesConfirmados() {
		return numeroProcedimentosExamesConfirmados;
	}

	public void setNumeroProcedimentosExamesConfirmados(
			int numeroProcedimentosExamesConfirmados) {
		this.numeroProcedimentosExamesConfirmados = numeroProcedimentosExamesConfirmados;
	}

	public int getNumeroProcedimentosInternacoesAbertas() {
		return numeroProcedimentosInternacoesAbertas;
	}

	public void setNumeroProcedimentosInternacoesAbertas(
			int numeroProcedimentosInternacoesAbertas) {
		this.numeroProcedimentosInternacoesAbertas = numeroProcedimentosInternacoesAbertas;
	}

	public int getNumeroProcedimentosInternacoesFechadas() {
		return numeroProcedimentosInternacoesFechadas;
	}

	public void setNumeroProcedimentosInternacoesFechadas(
			int numeroProcedimentosInternacoesFechadas) {
		this.numeroProcedimentosInternacoesFechadas = numeroProcedimentosInternacoesFechadas;
	}

	public int getNumeroProcedimentosOdontoAgendados() {
		return numeroProcedimentosOdontoAgendados;
	}

	public void setNumeroProcedimentosOdontoAgendados(
			int numeroProcedimentosOdontoAgendados) {
		this.numeroProcedimentosOdontoAgendados = numeroProcedimentosOdontoAgendados;
	}

	public int getNumeroProcedimentosOdontoConfirmados() {
		return numeroProcedimentosOdontoConfirmados;
	}

	public void setNumeroProcedimentosOdontoConfirmados(
			int numeroProcedimentosOdontoConfirmados) {
		this.numeroProcedimentosOdontoConfirmados = numeroProcedimentosOdontoConfirmados;
	}

	public int getNumeroProcedimentosUrgenciasAbertas() {
		return numeroProcedimentosUrgenciasAbertas;
	}

	public void setNumeroProcedimentosUrgenciasAbertas(
			int numeroProcedimentosUrgenciasAbertas) {
		this.numeroProcedimentosUrgenciasAbertas = numeroProcedimentosUrgenciasAbertas;
	}

	public int getNumeroProcedimentosUrgenciasFechadas() {
		return numeroProcedimentosUrgenciasFechadas;
	}

	public void setNumeroProcedimentosUrgenciasFechadas(
			int numeroProcedimentosUrgenciasFechadas) {
		this.numeroProcedimentosUrgenciasFechadas = numeroProcedimentosUrgenciasFechadas;
	}

	public int getNumeroTratamentosOdontoAgendados() {
		return numeroTratamentosOdontoAgendados;
	}

	public void setNumeroTratamentosOdontoAgendados(
			int numeroTratamentosOdontoAgendados) {
		this.numeroTratamentosOdontoAgendados = numeroTratamentosOdontoAgendados;
	}

	public int getNumeroTratamentosOdontoCancelados() {
		return numeroTratamentosOdontoCancelados;
	}

	public void setNumeroTratamentosOdontoCancelados(
			int numeroTratamentosOdontoCancelados) {
		this.numeroTratamentosOdontoCancelados = numeroTratamentosOdontoCancelados;
	}

	public int getNumeroTratamentosOdontoConfirmados() {
		return numeroTratamentosOdontoConfirmados;
	}

	public void setNumeroTratamentosOdontoConfirmados(
			int numeroTratamentosOdontoConfirmados) {
		this.numeroTratamentosOdontoConfirmados = numeroTratamentosOdontoConfirmados;
	}

	public int getNumeroUrgenciasAbertas() {
		return numeroUrgenciasAbertas;
	}

	public void setNumeroUrgenciasAbertas(int numeroUrgenciasAbertas) {
		this.numeroUrgenciasAbertas = numeroUrgenciasAbertas;
	}

	public int getNumeroUrgenciasCanceladas() {
		return numeroUrgenciasCanceladas;
	}

	public void setNumeroUrgenciasCanceladas(int numeroUrgenciasCanceladas) {
		this.numeroUrgenciasCanceladas = numeroUrgenciasCanceladas;
	}

	public int getNumeroUrgenciasFechadas() {
		return numeroUrgenciasFechadas;
	}

	public void setNumeroUrgenciasFechadas(int numeroUrgenciasFechadas) {
		this.numeroUrgenciasFechadas = numeroUrgenciasFechadas;
	}

	public BigDecimal getSomatorioConsultasAtendimentosUrgenciasAbertas() {
		return somatorioConsultasAtendimentosUrgenciasAbertas;
	}

	public void setSomatorioConsultasAtendimentosUrgenciasAbertas(
			BigDecimal somatorioConsultasAtendimentosUrgenciasAbertas) {
		this.somatorioConsultasAtendimentosUrgenciasAbertas = somatorioConsultasAtendimentosUrgenciasAbertas;
	}

	public BigDecimal getSomatorioConsultasAtendimentosUrgenciasFechadas() {
		return somatorioConsultasAtendimentosUrgenciasFechadas;
	}

	public void setSomatorioConsultasAtendimentosUrgenciasFechadas(
			BigDecimal somatorioConsultasAtendimentosUrgenciasFechadas) {
		this.somatorioConsultasAtendimentosUrgenciasFechadas = somatorioConsultasAtendimentosUrgenciasFechadas;
	}

	public int getNumeroConsultasAtendimentosUrgenciasFechadas() {
		return numeroConsultasAtendimentosUrgenciasFechadas;
	}

	public void setNumeroConsultasAtendimentosUrgenciasFechadas(
			int numeroConsultasAtendimentosUrgenciasFechadas) {
		this.numeroConsultasAtendimentosUrgenciasFechadas = numeroConsultasAtendimentosUrgenciasFechadas;
	}

	public int getNumeroConsultasAtendimentosUrgenciasAbertas() {
		return numeroConsultasAtendimentosUrgenciasAbertas;
	}

	public void setNumeroConsultasAtendimentosUrgenciasAbertas(
			int numeroConsultasAtendimentosUrgenciasAbertas) {
		this.numeroConsultasAtendimentosUrgenciasAbertas = numeroConsultasAtendimentosUrgenciasAbertas;
	}

	public int getNumeroConsultasAtendimentosUrgenciasCanceladas() {
		return numeroConsultasAtendimentosUrgenciasCanceladas;
	}

	public void setNumeroConsultasAtendimentosUrgenciasCanceladas(
			int numeroConsultasAtendimentosUrgenciasCanceladas) {
		this.numeroConsultasAtendimentosUrgenciasCanceladas = numeroConsultasAtendimentosUrgenciasCanceladas;
	}

	public BigDecimal getLimiteConsultasAtendimentosUrgencias() {
		return limiteConsultasAtendimentosUrgencias;
	}

	public void setLimiteConsultasAtendimentosUrgencias(
			BigDecimal limiteConsultasAtendimentosUrgencias) {
		this.limiteConsultasAtendimentosUrgencias = limiteConsultasAtendimentosUrgencias;
	}

	public int getNumeroProcedimentosConsultasAtendimentosUrgenciasAbertas() {
		return numeroProcedimentosConsultasAtendimentosUrgenciasAbertas;
	}

	public void setNumeroProcedimentosConsultasAtendimentosUrgenciasAbertas(
			int numeroProcedimentosConsultasAtendimentosUrgenciasAbertas) {
		this.numeroProcedimentosConsultasAtendimentosUrgenciasAbertas = numeroProcedimentosConsultasAtendimentosUrgenciasAbertas;
	}

	public int getNumeroProcedimentosConsultasAtendimentosUrgenciasFechadas() {
		return numeroProcedimentosConsultasAtendimentosUrgenciasFechadas;
	}

	public void setNumeroProcedimentosConsultasAtendimentosUrgenciasFechadas(
			int numeroProcedimentosConsultasAtendimentosUrgenciasFechadas) {
		this.numeroProcedimentosConsultasAtendimentosUrgenciasFechadas = numeroProcedimentosConsultasAtendimentosUrgenciasFechadas;
	}

	public BigDecimal getSomatorioConsultasAgendadas() {
		return somatorioConsultasAgendadas;
	}

	public void setSomatorioConsultasAgendadas(
			BigDecimal somatorioConsultasAgendadas) {
		this.somatorioConsultasAgendadas = somatorioConsultasAgendadas;
	}

	public BigDecimal getSomatorioConsultasConfirmadas() {
		return somatorioConsultasConfirmadas;
	}

	public void setSomatorioConsultasConfirmadas(
			BigDecimal somatorioConsultasConfirmadas) {
		this.somatorioConsultasConfirmadas = somatorioConsultasConfirmadas;
	}

	public BigDecimal getSomatorioConsultasOdontoConfirmadas() {
		return somatorioConsultasOdontoConfirmadas;
	}

	public void setSomatorioConsultasOdontoConfirmadas(
			BigDecimal somatorioConsultasOdontoConfirmadas) {
		this.somatorioConsultasOdontoConfirmadas = somatorioConsultasOdontoConfirmadas;
	}

	public BigDecimal getSomatorioExamesAgendados() {
		return somatorioExamesAgendados;
	}

	public void setSomatorioExamesAgendados(BigDecimal somatorioExamesAgendados) {
		this.somatorioExamesAgendados = somatorioExamesAgendados;
	}

	public BigDecimal getSomatorioExamesConfirmados() {
		return somatorioExamesConfirmados;
	}

	public void setSomatorioExamesConfirmados(BigDecimal somatorioExamesConfirmados) {
		this.somatorioExamesConfirmados = somatorioExamesConfirmados;
	}

	public BigDecimal getSomatorioInternacoesAbertas() {
		return somatorioInternacoesAbertas;
	}

	public void setSomatorioInternacoesAbertas(
			BigDecimal somatorioInternacoesAbertas) {
		this.somatorioInternacoesAbertas = somatorioInternacoesAbertas;
	}

	public BigDecimal getSomatorioInternacoesFechadas() {
		return somatorioInternacoesFechadas;
	}

	public void setSomatorioInternacoesFechadas(
			BigDecimal somatorioInternacoesFechadas) {
		this.somatorioInternacoesFechadas = somatorioInternacoesFechadas;
	}

	public BigDecimal getSomatorioTratamentosOdontoAgendados() {
		return somatorioTratamentosOdontoAgendados;
	}

	public void setSomatorioTratamentosOdontoAgendados(
			BigDecimal somatorioTratamentosOdontoAgendados) {
		this.somatorioTratamentosOdontoAgendados = somatorioTratamentosOdontoAgendados;
	}

	public BigDecimal getSomatorioTratamentosOdontoConfirmados() {
		return somatorioTratamentosOdontoConfirmados;
	}

	public void setSomatorioTratamentosOdontoConfirmados(
			BigDecimal somatorioTratamentosOdontoConfirmados) {
		this.somatorioTratamentosOdontoConfirmados = somatorioTratamentosOdontoConfirmados;
	}

	public BigDecimal getSomatorioUrgenciasAbertas() {
		return somatorioUrgenciasAbertas;
	}

	public void setSomatorioUrgenciasAbertas(BigDecimal somatorioUrgenciasAbertas) {
		this.somatorioUrgenciasAbertas = somatorioUrgenciasAbertas;
	}

	public BigDecimal getSomatorioUrgenciasFechadas() {
		return somatorioUrgenciasFechadas;
	}

	public void setSomatorioUrgenciasFechadas(BigDecimal somatorioUrgenciasFechadas) {
		this.somatorioUrgenciasFechadas = somatorioUrgenciasFechadas;
	}

	public void setLimiteConsultas(BigDecimal limiteConsultas) {
		this.limiteConsultas = limiteConsultas;
	}

	public void setLimiteConsultasOdonto(BigDecimal limiteConsultasOdonto) {
		this.limiteConsultasOdonto = limiteConsultasOdonto;
	}

	public void setLimiteExames(BigDecimal limiteExames) {
		this.limiteExames = limiteExames;
	}

	public void setLimiteInternacoes(BigDecimal limiteInternacoes) {
		this.limiteInternacoes = limiteInternacoes;
	}

	public void setLimiteTratamentosOdonto(BigDecimal limiteTratamentosOdonto) {
		this.limiteTratamentosOdonto = limiteTratamentosOdonto;
	}

	public void setLimiteUrgencias(BigDecimal limiteUrgencias) {
		this.limiteUrgencias = limiteUrgencias;
	}

	public BigDecimal getSomatorioConsultasOdontoUrgenciaConfirmadas() {
		return somatorioConsultasOdontoUrgenciaConfirmadas;
	}

	public void setSomatorioConsultasOdontoUrgenciaConfirmadas(
			BigDecimal somatorioConsultasOdontoUrgencia) {
		this.somatorioConsultasOdontoUrgenciaConfirmadas = somatorioConsultasOdontoUrgencia;
	}

	public int getNumeroConsultasOdontoUrgenciaConfirmadas() {
		return numeroConsultasOdontoUrgenciaConfirmadas;
	}

	public void setNumeroConsultasOdontoUrgenciaConfirmadas(int numeroConsultasOdontoUrgencia) {
		this.numeroConsultasOdontoUrgenciaConfirmadas = numeroConsultasOdontoUrgencia;
	}

	public BigDecimal getSomatorioConsultasOdontoUrgenciaCanceladas() {
		return somatorioConsultasOdontoUrgenciaCanceladas;
	}

	public void setSomatorioConsultasOdontoUrgenciaCanceladas(
			BigDecimal somatorioConsultasOdontoUrgenciaCanceladas) {
		this.somatorioConsultasOdontoUrgenciaCanceladas = somatorioConsultasOdontoUrgenciaCanceladas;
	}

	public int getNumeroConsultasOdontoUrgenciaCanceladas() {
		return numeroConsultasOdontoUrgenciaCanceladas;
	}

	public void setNumeroConsultasOdontoUrgenciaCanceladas(
			int numeroConsultasOdontoUrgenciaCanceladas) {
		this.numeroConsultasOdontoUrgenciaCanceladas = numeroConsultasOdontoUrgenciaCanceladas;
	}

	public String getStatus() {
		return null;
	}

	public void setStatus(String status) {
	}

	public void cancelar() {
	}

	public void fechar() {
	}
}