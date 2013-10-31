package br.com.infowaypi.ecarebc.odonto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.odonto.enums.EstruturaOdontoEnum;
import br.com.infowaypi.ecarebc.odonto.enums.FaceEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * Classe que representa uma estrutura odontológica que engloba apenas elemento e face
 * @author Danilo Nogueira Portela
 */
public class EstruturaOdontoEFLayer implements Serializable {
 
	private static final long serialVersionUID = 1L;
	
	private Long idEstruturaOdontoEFLayer;
	private Dente dente;
	private boolean mesial;
	private boolean oclusal_Incisiva;
	private boolean lingual_Palatina;
	private boolean vestibular;
	private boolean distal;

	private Set<EstruturaOdontoEF> estruturasEF;
	
	public EstruturaOdontoEFLayer(){
		this.estruturasEF = new HashSet<EstruturaOdontoEF>();
	}

	public Long getIdEstruturaOdontoEFLayer() {
		return idEstruturaOdontoEFLayer;
	}

	public void setIdEstruturaOdontoEFLayer(Long idEstruturaOdontoEFLayer) {
		this.idEstruturaOdontoEFLayer = idEstruturaOdontoEFLayer;
	}

	public Dente getDente() {
		return dente;
	}

	public void setDente(Dente dente) {
		this.dente = dente;
	}

	public boolean isMesial() {
		return mesial;
	}

	public void setMesial(boolean mesial) {
		this.mesial = mesial;
	}

	public boolean isOclusal_Incisiva() {
		return oclusal_Incisiva;
	}

	public void setOclusal_Incisiva(boolean oclusal_Incisiva) {
		this.oclusal_Incisiva = oclusal_Incisiva;
	}

	public boolean isLingual_Palatina() {
		return lingual_Palatina;
	}

	public void setLingual_Palatina(boolean lingual_Palatina) {
		this.lingual_Palatina = lingual_Palatina;
	}

	public boolean isVestibular() {
		return vestibular;
	}

	public void setVestibular(boolean vestibular) {
		this.vestibular = vestibular;
	}

	public boolean isDistal() {
		return distal;
	}

	public void setDistal(boolean distal) {
		this.distal = distal;
	} 
	
	public String getMesialDescricao() {
		return getFaceDescricao(this.mesial, "M");
	}
	
	public String getDistalDescricao() {
		return getFaceDescricao(this.distal, "D");
	}
	
	public String getLingualDescricao() {
		return getFaceDescricao(this.lingual_Palatina, "L");
	}
	
	public String getOclusalDescricao() {
		return getFaceDescricao(this.oclusal_Incisiva, "O");
	}
	
	public String getVestibularDescricao() {
		return getFaceDescricao(this.vestibular, "V");
	}

	public Set<EstruturaOdontoEF> getEstruturasEF() {
		return estruturasEF;
	}

	public void setEstruturasEF(Set<EstruturaOdontoEF> estruturasEF) {
		this.estruturasEF = estruturasEF;
	}

	private String getFaceDescricao(boolean face, String sigla) {
		if (face) {
			return sigla;
		}
		return "";
	}
	
	public String getDescricaoDenteEFace() {
		String denteEFace = getDente().getNumero() + " " 
				+ getDistalDescricao() 
				+ getLingualDescricao() 
				+ getMesialDescricao() 
				+ getOclusalDescricao() 
				+ getVestibularDescricao();
		return denteEFace;
	}
	
	public Boolean validate() throws Exception {
		if (this.getDente() == null) {
			throw new ValidateException(MensagemErroEnum.ESTRUTURA_NAO_INFORMADA.getMessage(EstruturaOdontoEnum.DENTE.getDescricao()));
		}
		if (!this.distal && !this.lingual_Palatina && !this.mesial && !this.oclusal_Incisiva && !this.vestibular) {
			throw new ValidateException("Preencha pelo menos uma face.");
		}
		
		setDenteEFaces(this.isDistal(),"D");
		setDenteEFaces(this.isLingual_Palatina(),"L");
		setDenteEFaces(this.isMesial(),"M");
		setDenteEFaces(this.isOclusal_Incisiva(),"O");
		setDenteEFaces(this.isVestibular(),"V");
		
		return true;
	}
	
	private void setDenteEFaces(boolean faceMarcada, String tipo) throws Exception {
		if (faceMarcada) {
			Face face = null;
			List<Face> faces = this.getDente().getFaces(this.getDente());
			for (Face f : faces) {
				if (f.getDescricaoFormatada().equals(tipo)) {
					face = f;
				}
			}
			
			if (face == null) {
				throw new ValidateException("O dente " + this.getDente().getDescricao() + " não tem a face " + getNomeFace(tipo) + ".");
			}
			
			EstruturaOdontoEF ef = new EstruturaOdontoEF();
			ef.setDenteEF(this.getDente());
			ef.setFaceEF(face);
			
			ef.validate();
			this.getEstruturasEF().add(ef);
		}
	}

	private String getNomeFace(String tipo) {
		String nomeFace = "";
		if (tipo.equals("D")) {
			nomeFace = FaceEnum.DISTAL.getDescricao();
		}
		if (tipo.equals("L")) {
			nomeFace = FaceEnum.LINGUAL_PALATINA.getDescricao();
		}
		if (tipo.equals("M")) {
			nomeFace = FaceEnum.MESIAL.getDescricao();
		}
		if (tipo.equals("O")) {
			nomeFace = FaceEnum.OCLUSAL_INCISIVA.getDescricao();
		}
		if (tipo.equals("V")) {
			nomeFace = FaceEnum.VESTIBULAR.getDescricao();
		}
		return nomeFace;
	}
}