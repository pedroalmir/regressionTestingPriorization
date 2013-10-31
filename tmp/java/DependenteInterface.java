package br.com.infowaypi.ecare.segurados;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.financeiro.DependenteFinanceiroInterface;
import br.com.infowaypi.ecarebc.segurados.DependenteInterfaceBC;

/**
 * Interface para a classe Dependente. 
 * @author Mário Sérgio Coelho Marroquim
 */
public interface DependenteInterface extends DependenteInterfaceBC, SeguradoBasico,DependenteFinanceiroInterface{
	
	/* 
	 * Constantes para os tipos de Dependente. 
	 */
	public static final String TIPO_DEPENDENTE_SUPLEMENTAR = "Dependente Suplementar";
	public static final String TIPO_DEPENDENTE_DIRETO = "Dependente Direto";

	public abstract Integer getTipoDeDependencia();

	public abstract void setTipoDeDependencia(Integer tipoDeDependencia);
	
	public abstract String getDescricaoDaDependencia();

	public abstract void setDescricaoDaDependencia(String descricaoDaDependencia);
	
	public BigDecimal[] getValorFinanciamentoDependente(BigDecimal salarioBase, Date dataBase);

	public BigDecimal getValorCoparticipacao(Date competencia);
	
	public Set<GuiaSimples> getGuiasAtendimentoEm(Date competencia);
	
//	public void regularizar(Usuario usuario, DependenteInterface dependente) throws Exception;
}