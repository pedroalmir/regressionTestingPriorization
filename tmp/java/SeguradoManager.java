package br.com.infowaypi.ecare.manager;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecare.enums.TipoLiberacaoForaLimiteEnum;
import br.com.infowaypi.ecare.programaPrevencao.AssociacaoSeguradoPrograma;
import br.com.infowaypi.ecare.programaPrevencao.ProfissionalEspecialidade;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Tem por finalidade organizar as operacoes da entidade @Segurado.
 * @author Emanuel
 * @changes Luciano Rocha 
 * @since 20/09/2012
 */
public class SeguradoManager {

	public static Segurado buscarSegurado(UsuarioInterface usuario){
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("usuario", usuario));
		return sa.uniqueResult(Segurado.class);
	}
	
	public static <S extends Segurado> S buscarPorCartao(String numeroCartao, Class tipoSegurado, boolean liberarSuspensos) {
		Criteria criteria = HibernateUtil.currentSession().createCriteria(tipoSegurado)
				.add(Expression.eq("numeroDoCartao", numeroCartao));
							
		S segurado = (S) criteria.uniqueResult();
		
		Assert.isNotNull(segurado, MensagemErroEnum.CARTAO_SEGURADO_NAO_ENCONTRADO.getMessage(numeroCartao));
		
		BuscarSegurados.validaSituacao(segurado, liberarSuspensos, false);
		
		return segurado;
	}

	/**
	 * Verifica se o segurado está associado a algum PPS ativo e se o profissional da guia
	 * está associado a este mesmo PPS. Depois retorna o tipo de liberação fora do limite de consumos do segurado.
	 * @author Luciano Rocha
	 * @param segurado
	 * @param guia
	 * @return Tipo de liberação fora do limite de consumos do segurado.
	 */
	public static Integer pegaTipoDeLiberacao(AbstractSegurado segurado, Profissional profissional){
		boolean temPPSAtivo = false;
		boolean profissionalDoPPS = false;
		if (segurado.getAssociacaoSegurados() != null) {
			for (AssociacaoSeguradoPrograma associacao : segurado.getAssociacaoSegurados()) {
				if (associacao.getPrograma().isSituacaoAtual(SituacaoEnum.CADASTRADO.descricao())) {
					temPPSAtivo = true;
					for (ProfissionalEspecialidade profissionalEspecialidade : associacao.getPrograma().getMedicos()) {
						if (profissionalEspecialidade.getProfissional().equals(profissional)) {
							profissionalDoPPS = true;
							break;
						}
					}
				} 	
			}
		}
		
		if (temPPSAtivo && profissionalDoPPS) {
			return TipoLiberacaoForaLimiteEnum.LIBERACAO_MPPS.codigo();
		} else {
			return TipoLiberacaoForaLimiteEnum.LIBERACAO_AUDITOR.codigo();
		}
	}
	
	/**
	 * Método responsável pela contabilização das liberações fora do limite de consumos do segurado.
	 * @author Luciano Rocha
	 * @param segurado
	 * @param tipoLiberacao
	 */
	public static void contabilizaLiberacoesForaDoLimite(AbstractSegurado segurado, Integer tipoLiberacao){
		if (tipoLiberacao == TipoLiberacaoForaLimiteEnum.LIBERACAO_AUDITOR.codigo()) {
			segurado.setContadorLiberacaoPeloAuditor(segurado.getContadorLiberacaoPeloAuditor()+1);
		} else if (tipoLiberacao == TipoLiberacaoForaLimiteEnum.LIBERACAO_MPPS.codigo()){
			segurado.setContadorLiberacaoPeloMpps(segurado.getContadorLiberacaoPeloMpps()+1);
		}
	}
}
