package br.com.infowaypi.ecare.segurados;

import java.util.Date;
import java.util.Set;

import br.com.infowaypi.ecare.constantes.Constantes;
import br.com.infowaypi.ecare.financeiro.Cobranca;
import br.com.infowaypi.ecare.financeiro.CobrancaInterface;
import br.com.infowaypi.ecare.financeiro.DetalhePagamentoInterface;
import br.com.infowaypi.ecarebc.financeiro.TitularFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.consignacao.Consignacao;
import br.com.infowaypi.ecarebc.planos.Plano;
import br.com.infowaypi.ecarebc.segurados.TitularInterfaceBC;

/**
 * Interface para a a classe Titular.
 * 
 * @author Mário Sérgio Coelho Marroquim
 * @change SR Team - Marcos Roberto 17.11.2011
*/
@SuppressWarnings("rawtypes")
public interface TitularInterface extends TitularInterfaceBC, SeguradoBasico,TitularFinanceiroInterface{
 
	/*(non-Javadoc)
	 * Constantes para as formas de pagamento.
	 */
	public static final int CONTA_CORRENTE = Constantes.CONTA_CORRENTE;
	public static final int FOLHA 	= Constantes.FOLHA; 
	public static final int BOLETO 	= Constantes.BOLETO;
	
	public static final int CARENCIA_CONSULTA_EXAMES 				= 30;
	public static final int CARENCIA_EXAMES_COMPLEXOS_INTERNACAO 	= 180;
	
	//CONSTANTES PARA CONSIGNACAO
	/**
	 * Corresponde a 12 vezes o salário mínimo vigente. Em 22/03/2011, R$ 545
	 */
	public String getIdentificador();
	
	public Date getDataAdesao();

	public Set<DependenteSR> getDependentes();
	
	public String getDescricaoLesao();

	public int getDiaBase();

	public String getGrauDeDependencia();

	public String getObservacao();

	public void setDataAdesao(Date dataAdesao);

	public void setDescricaoLesao(String descricaoLesao);

	public void setDiaBase(int diaBase);

	public void setObservacao(String observacao);
	
	public DetalhePagamentoInterface getDetalhePagamento();

	public void setDetalhePagamento(DetalhePagamentoInterface  detalhePagamento);

	public Set<Cobranca> getCobrancas();
	
	public CobrancaInterface getCobranca(Date competencia);
	
	public void setCobrancas(Set<Cobranca> cobrancas);

	public CobrancaInterface getUltimaCobranca();
	
	public CobrancaInterface getPrimeiraCobranca();
	
	public Consignacao getUltimaConsignacao();
	
	public Consignacao getPrimeiraConsignacao();

	public Set<AbstractMatricula> getMatriculas();
	public void setMatriculas(Set<AbstractMatricula> matriculas);
	
	public Plano getPlano();
	public void setPlano(Plano plano);
}