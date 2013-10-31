package br.com.infowaypi.ecarebc.atendimentos;

import java.util.Date;

import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.msr.user.UsuarioInterface;


@SuppressWarnings("serial")
public class Gasoterapia extends AbstractTaxa {
		
	public Gasoterapia(){
		super();
	}
	
	/**
	 * @param usuario
	 * @return Boolean
	 * Método usado para setar a situação do objeto como 'Ativo(a)' no momento de seu cadastramento.
	 */
	public Boolean validate(UsuarioInterface usuario){
		
		super.setCodigoDescricao(this.getCodigo() +" - "+  this.getDescricao());
		
		if (this.getSituacao()==null){
			mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(), "Cadastro de nova gasoterapia.", new Date());
			return true;
		}
		return true;
	}
}