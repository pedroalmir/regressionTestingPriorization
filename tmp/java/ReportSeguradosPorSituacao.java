package br.com.infowaypi.ecare.relatorio;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;

import br.com.infowaypi.ecare.segurados.Dependente;
import br.com.infowaypi.ecare.segurados.DependenteSuplementar;
import br.com.infowaypi.ecare.segurados.Pensionista;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.segurados.Titular;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;

/**
 * 
 * @author Infoway
 */
public class ReportSeguradosPorSituacao {
	
	
	public static final String ATIVO = SituacaoEnum.ATIVO.descricao();
	public static final String SUSPENSO = SituacaoEnum.SUSPENSO.descricao();
	public static final String CANCELADO = SituacaoEnum.CANCELADO.descricao();
	
	private String situacao;
	private Long quantidadeTitulares;
	private Long quantidadeDependentes;
	private Long quantidadeDependentesSuplementares;
	private Long quantidadePensionistas;

	public ReportSeguradosPorSituacao gerarRelatorio(String situacao) {
		
		this.situacao = situacao;
		this.quantidadeTitulares = getQuantidade(Titular.class, situacao);
		this.quantidadeDependentes = getQuantidade(Dependente.class, situacao);
		this.quantidadeDependentesSuplementares = getQuantidade(DependenteSuplementar.class, situacao);
		this.quantidadePensionistas = getQuantidade(Pensionista.class, situacao);
		
		return this;
	}
	
	public Long getQuantidade(Class<? extends Segurado> klass, String situacao){
		Criteria criteria = HibernateUtil.currentSession().createCriteria(klass);
		
		criteria.add(Expression.eq("situacao.descricao", situacao))
				.setProjection(Projections.rowCount());
		
		return (Long)criteria.uniqueResult();
	}
	
	public Long getQuantidadeSegurados() {
		return quantidadeTitulares + quantidadeDependentes
				+ quantidadeDependentesSuplementares + quantidadePensionistas;
	}

	public Long getQuantidadeTitulares() {
		return quantidadeTitulares;
	}

	public Long getQuantidadeDependentes() {
		return quantidadeDependentes;
	}


	public Long getQuantidadeDependentesSuplementares() {
		return quantidadeDependentesSuplementares;
	}


	public Long getQuantidadePensionistas() {
		return quantidadePensionistas;
	}

	public String getSituacao() {
		return situacao;
	}
}
