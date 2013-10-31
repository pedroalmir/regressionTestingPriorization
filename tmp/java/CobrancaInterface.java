package br.com.infowaypi.ecare.financeiro;

import java.util.Date;

import br.com.infowaypi.ecare.segurados.TitularFinanceiroSR;
import br.com.infowaypi.ecarebc.financeiro.FluxoFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.conta.ComponenteColecaoContas;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * Interface a ser usada por todas as classes que utilizam cobrança.
 */
public interface CobrancaInterface extends FluxoFinanceiroInterface {
	
	public static final String COBRANCA_MENSAL = "Cobranca";

	public void mudarSituacao(UsuarioInterface usuario,String descricao,String motivo,Date dataSituacao);
	
	public void processarVencimento();
	
 	public boolean isCompetencia(Date competencia);
 	
	public boolean isVencida();
	
	public Long getIdFluxoFinanceiro();
	
	public boolean isPaga();
	
	public boolean isCancelada();
	
	public void cancelar(UsuarioInterface usuario, String motivo) throws Exception;
	
	public long getCodigoLegado();
	
	public void setCodigoLegado(long codigoLegado);
	
	public TitularFinanceiroSR getTitular();
	
	public void setTitular(TitularFinanceiroSR titular);
	
	public void setColecaoContas(ComponenteColecaoContas colecaoContas);
	
}