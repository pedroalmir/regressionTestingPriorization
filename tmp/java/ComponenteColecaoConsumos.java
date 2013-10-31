package br.com.infowaypi.ecarebc.consumo;


import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import br.com.infowaypi.ecarebc.consumo.periodos.Bimestre;
import br.com.infowaypi.ecarebc.consumo.periodos.Mes;
import br.com.infowaypi.ecarebc.consumo.periodos.Periodo;
import br.com.infowaypi.ecarebc.consumo.periodos.Quinzena;
import br.com.infowaypi.ecarebc.consumo.periodos.Semana;
import br.com.infowaypi.ecarebc.consumo.periodos.Semestre;
import br.com.infowaypi.ecarebc.consumo.periodos.Trimestre;
import br.com.infowaypi.ecarebc.utils.DateUtils;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe que encapsula o pattern component para uma colecao de classes Consumo de um usuário do sistema.
 * Modificações para utilização de coleção de classes ConsumoBI, com nova modelagem para consumo.
 * @author Danilo Nogueira Portela
 */
@SuppressWarnings("serial")
public class ComponenteColecaoConsumos implements ColecaoConsumosInterface {

	private Long idColecaoConsumos;
	private Set<ConsumoInterface> consumos;

	public ComponenteColecaoConsumos() {
		super();
		consumos = new LinkedHashSet<ConsumoInterface>();
	}
	
	public Long getIdColecaoConsumos() {
		return idColecaoConsumos;
	}

	public void setIdColecaoConsumos(Long idColecaoConsumos) {
		this.idColecaoConsumos = idColecaoConsumos;
	}
	
	public Set<ConsumoInterface> getConsumos() {
		return consumos;
	}

	void setConsumos(Set<ConsumoInterface> consumos) {
		this.consumos = consumos;
	}

	/**
	 * Retorna o consumo referente ao periodo e a data
	 */
	public ConsumoInterface getConsumo(Date data, Periodo periodo) {
		for(ConsumoInterface consumo : this.getConsumos()){
			 
			boolean isTipoPeriodoIgual = consumo.getTipoPeriodo().equals(periodo.getValor());
			boolean isConsumoReferenteAData = Utils.between(consumo.getDataInicial(),consumo.getDataFinal(),data);
			if(isTipoPeriodoIgual&& isConsumoReferenteAData){
				return consumo;
			}
		}
		return null;
	}
	

	
	public ConsumoInterface criarConsumo(Date data, Periodo periodo, float limiteConsulta, float limiteExame, float limiteConsultaOdonto,
			float limiteTratamentoOdonto) {
		
		ConsumoInterface consumo = new Consumo();  
		Date dataInicial = null;
		Date dataFinal = null;

		switch (periodo) {
	        case DIARIO: dataInicial = data; dataFinal = data; break;
	        case SEMANAL: dataInicial = Semana.getDataInicial(data); dataFinal = Semana.getDataFinal(data); break;
	        case QUINZENAL: dataInicial = Quinzena.getDataInicial(data); dataFinal = Quinzena.getDataFinal(data); break;
	        case MENSAL:  dataInicial = Mes.getDataInicial(data); dataFinal = Mes.getDataFinal(data); break;
	        case BIMESTRAL: dataInicial = Bimestre.getDataInicial(data); dataFinal = Bimestre.getDataFinal(data); break;
	        case TRIMESTRAL: dataInicial = Trimestre.getDataInicial(data); dataFinal = Trimestre.getDataFinal(data); break;
	        case SEMESTRAL: dataInicial = Semestre.getDataInicial(data); dataFinal = Semestre.getDataFinal(data); break;
	        case ANUAL: dataInicial = DateUtils.getDataInicialAno(data); dataFinal = DateUtils.getDataFinalAno(data); break;
		}

		consumo.setDataInicial(dataInicial);
		consumo.setDataFinal(dataFinal);
		consumo.setLimiteConsultas(new BigDecimal(limiteConsulta));
		consumo.setLimiteExames(new BigDecimal(limiteExame));
		consumo.setLimiteConsultasOdonto(new BigDecimal(limiteConsultaOdonto));
		consumo.setLimiteTratamentosOdonto(new BigDecimal(limiteTratamentoOdonto));
		consumo.setPeriodo(periodo);
		consumo.setColecaoConsumos(this);
		this.getConsumos().add(consumo);
		return consumo;
	}
	
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
			.append("Id", this.idColecaoConsumos)
			.append("Numero de consumos", this.consumos.size())
			.toString();
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ComponenteColecaoConsumos)) {
			return false;
		}
		ComponenteColecaoConsumos otherObject = (ComponenteColecaoConsumos) object;
		return new EqualsBuilder()
			.append(this.idColecaoConsumos, otherObject.getIdColecaoConsumos())
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.idColecaoConsumos)
			.toHashCode();
	}
}