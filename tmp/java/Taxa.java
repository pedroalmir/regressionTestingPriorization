package br.com.infowaypi.ecarebc.atendimentos;

import java.util.Date;

import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.msr.user.UsuarioInterface;

public class Taxa extends AbstractTaxa{

	/**
	 * @param usuario
	 * @return Boolean
	 * Método usado para setar a situação do objeto como 'Ativo(a)' no momento de seu cadastramento.
	 */
	public Boolean validate(UsuarioInterface usuario){
		
		super.setCodigoDescricao(this.getCodigo() +" - "+  this.getDescricao());
		
		if (this.getSituacao()==null){
			mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(), "Cadastro de nova taxa", new Date());
			return true;
		}
		return true;
	}
}
