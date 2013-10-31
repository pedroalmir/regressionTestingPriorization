package br.com.infowaypi.ecare.services.suporte;

import java.util.Date;
import java.util.Set;

import org.hibernate.SQLQuery;

import br.com.infowaypi.ecare.financeiro.Cobranca;
import br.com.infowaypi.ecare.segurados.DependenteSuplementar;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * Funcionalidade para reverter a migração de dependente suplementar para dependente regular. 
 * @author Luciano Rocha
 *
 */
public class ReverterDependenteSuplementarParaRegularFlow {
	
	//1º passo
	public DependenteSuplementar buscaSegurado(String cartao) throws Exception {
		
		validaBuscaSegurado(cartao);
		
		//faz a busca do segurado dependente suplementar
		DependenteSuplementar seguradoASerRevertidoParaRegular = buscaDependenteSuplementar(cartao);
		
		return seguradoASerRevertidoParaRegular;
	}

	//2º passo
	public SeguradoInterface reverterDependente(UsuarioInterface usuario, DependenteSuplementar seguradoASerRevertidoParaRegular) throws Exception{
		
		Long idSegurado = seguradoASerRevertidoParaRegular.getIdSegurado();
		
		//cancela as cobrancas do dependente suplementar
		Set<Cobranca> cobrancas = seguradoASerRevertidoParaRegular.getCobrancas();
		cancelarCobrancas(usuario, cobrancas);
		
		//reverte o suplementar para regular
			reverterDependenteSuplementarParaRegular(seguradoASerRevertidoParaRegular);
		seguradoASerRevertidoParaRegular.mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(), 
				"Migração para dependente suplementar desfeita.", new Date());
		
		//faz uma nova busca pra retornar o dependente convertido
		SeguradoInterface dr = buscaDependente(idSegurado);
		
		return dr;
	}

	public void validaBuscaSegurado(String cartao) throws Exception{
		if (cartao == null){
			throw new Exception("O número do cartão é obrigatório.");
		}
	}
	
	public DependenteSuplementar buscaDependenteSuplementar(String cartao) throws Exception{
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("numeroDoCartao", cartao));
		
		DependenteSuplementar seguradoASerRevertidoParaRegular = sa.uniqueResult(DependenteSuplementar.class);
		
		if (seguradoASerRevertidoParaRegular == null){
			throw new Exception("Não foi encontrado um dependente suplementar para este número de cartão.");
		}
		
		return seguradoASerRevertidoParaRegular;
	}
	
	public void cancelarCobrancas(UsuarioInterface usuario, Set<Cobranca> cobrancas){
		if (cobrancas != null) {
			for (Cobranca cobranca : cobrancas) {
				try {
					cobranca.cancelar(usuario, "Migração para dependente suplementar desfeita.");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void reverterDependenteSuplementarParaRegular(DependenteSuplementar seguradoASerRevertidoParaRegular) {
		SQLQuery query = 
			HibernateUtil.currentSession()
			.createSQLQuery("update segurados_segurado " +
					"set tiposegurado = 'D', idTitularSuplementar = null, idTitular = ?, diaBase = 1 " +
			"where numeroDoCartao = ?");
		query.setLong(0, seguradoASerRevertidoParaRegular.getTitular().getIdSegurado());
		query.setString(1, seguradoASerRevertidoParaRegular.getNumeroDoCartao());
	
		query.executeUpdate();
	}

	public SeguradoInterface buscaDependente(Long idSegurado){
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("idSegurado", idSegurado));
		SeguradoInterface depReg = sa.uniqueResult(SeguradoInterface.class);
		return depReg;
	}
	
}
