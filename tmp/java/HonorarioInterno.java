package br.com.infowaypi.ecarebc.atendimentos.honorario;

import br.com.infowaypi.ecarebc.utils.SituacaoUtils;
import br.com.infowaypi.msr.user.UsuarioInterface;

public class HonorarioInterno extends Honorario {

	public HonorarioInterno(){
		super();
	}
	
	public HonorarioInterno(UsuarioInterface usuario){
		super(usuario);
	}

	@Override
	public boolean isHonorarioExterno() {
		return false;
	}
	
	@Override
	public Object clone(){
		HonorarioInterno clone = new HonorarioInterno();
		
		clone.setColecaoSituacoes(SituacaoUtils.clone(SituacaoUtils.clone(this.getColecaoSituacoes())));
		clone.setGrauDeParticipacao(this.getGrauDeParticipacao());
		clone.setPorcentagem(this.getPorcentagem());
		clone.setProfissional(this.getProfissional());
		clone.setValorTotal(this.getValorTotal());
		clone.setSituacao(SituacaoUtils.clone(this.getSituacao()));
		
		return clone;
	}
}
