/*
 * Atualizado em 02/02/2006
 */
package br.com.infowaypi.ecarebc.consumo;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import br.com.infowaypi.ecarebc.consumo.periodos.Periodo;

public interface ColecaoConsumosInterface extends Serializable {

	/*
 * (non-javadoc)
 */
public static final ConsumoInterface consumoInterface = null;

	public Set<ConsumoInterface> getConsumos();
	
	public abstract ConsumoInterface getConsumo(Date data, Periodo periodo);
	
	public abstract ConsumoInterface criarConsumo(Date data, Periodo periodo, float limiteConsulta, float limiteExame,
			float limiteConsultaOdonto, float limiteTratamentoOdonto);
}