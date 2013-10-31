package br.com.infowaypi.ecarebc.utils;

import br.com.infowaypi.msr.situations.ComponenteColecaoSituacoes;
import br.com.infowaypi.msr.situations.Situacao;
import br.com.infowaypi.msr.situations.SituacaoInterface;

public class SituacaoUtils {

	public static ComponenteColecaoSituacoes clone(ComponenteColecaoSituacoes colecaoSituacoes ) {
		ComponenteColecaoSituacoes clone = new ComponenteColecaoSituacoes();
		
		for (SituacaoInterface situacao : colecaoSituacoes.getSituacoes()) {
			SituacaoInterface situacaoClone = clone(situacao);
		
			situacaoClone.setColecaoSituacoes(clone);
			clone.getSituacoes().add(situacaoClone );
		}
		return clone;
	}

	public static SituacaoInterface clone(SituacaoInterface situacao) {
		SituacaoInterface situacaoClone = new Situacao();
		situacaoClone.setData(situacao.getData());
		situacaoClone.setDataSituacao(situacao.getDataSituacao());
		situacaoClone.setDescricao(situacao.getDescricao());
		situacaoClone.setMotivo(situacao.getMotivo());
		situacaoClone.setOrdem(situacao.getOrdem());
		situacaoClone.setUsuario(situacao.getUsuario());
		return situacaoClone;
	}
}
