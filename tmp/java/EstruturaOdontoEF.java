package br.com.infowaypi.ecarebc.odonto;

import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.odonto.enums.EstruturaOdontoEnum;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Classe que representa uma estrutura odontológica que engloba apenas elemento e face
 * @author Danilo Nogueira Portela
 */
public class EstruturaOdontoEF extends EstruturaOdonto{
 
	private static final long serialVersionUID = 1L;

	public EstruturaOdontoEF(){
		super();
	} 
	
	@Override
	public Boolean validate() throws Exception {
//		super.validate();
		
		Assert.isNotNull(this.getDente(), MensagemErroEnum.ESTRUTURA_NAO_INFORMADA.getMessage(EstruturaOdontoEnum.DENTE.getDescricao(), ""));
		Assert.isNotNull(this.getFace(), MensagemErroEnum.ESTRUTURA_NAO_INFORMADA.getMessage(EstruturaOdontoEnum.FACE.getDescricao(), ""));
		
		//Setando as estruturas restantes
		Quadrante quadrante = this.getDente().getQuadrante();
		Arcada arcada = quadrante.getArcada();
		Denticao denticao = arcada.getDenticao();
		
		this.setDenticao(denticao);
		this.setArcada(arcada);
		this.setQuadrante(quadrante);
		
		this.tocarObjetos();
		return true;
	}
	
	public Dente getDenteEF(){
		return super.getDente();
	}
	
	public void setDenteEF(Dente dente){
		super.setDente(dente);
	}
	
	public Face getFaceEF(){
		return super.getFace();
	}
	
	public void setFaceEF(Face face){
		super.setFace(face);
	}
	
	@Override
	public Boolean isForRestauracao(){
		return true;
	}
}
