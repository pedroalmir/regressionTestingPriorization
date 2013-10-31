package br.com.infowaypi.ecare.questionarioqualificado;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import br.com.infowaypi.ecarebc.procedimentos.GrupoCBHPM;
import br.com.infowaypi.ecarebc.procedimentos.SubgrupoCBHPM;

/**
 * 
 * @author DANNYLVAN
 * 
 * Adaptador necessário para inserir o combo dependente na interface do Jheat.
 *
 */
public class AdapterSubgrupoCBHPM {

	private SubgrupoCBHPM subgrupo;
	
	private GrupoCBHPM grupo;

	public AdapterSubgrupoCBHPM() {}
	
	public AdapterSubgrupoCBHPM(SubgrupoCBHPM subgrupo) {
		this.setSubgrupo(subgrupo);
		this.setGrupo(subgrupo.getGrupoCBHPM());
	}

	public SubgrupoCBHPM getSubgrupo() {
		return subgrupo;
	}

	public void setSubgrupo(SubgrupoCBHPM subgrupo) {
		this.subgrupo = subgrupo;
	}

	public GrupoCBHPM getGrupo() {
		return grupo;
	}

	public void setGrupo(GrupoCBHPM grupo) {
		this.grupo = grupo;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.getGrupo())
									.append(this.getSubgrupo())
									.toHashCode();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
				.append(this.getGrupo())
				.append(this.getSubgrupo())
				.toString();
	}
	
}
