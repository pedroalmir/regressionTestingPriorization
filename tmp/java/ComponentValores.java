package br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


/**
 * 
 * @author Marcus Boolean
 *
 */
@SuppressWarnings("serial")
public class ComponentValores implements Serializable, Cloneable {
	
	private Long idComponentValores;
	private Set<Valor> valores;
	private Valor valor;
	
	public ComponentValores() {
		this.valores = new HashSet<Valor>();
	}
	
	public void setValor(Valor valor) {
		this.valores.add(valor);
		if (this.valores != null) {
			valor.setOrdem(this.valores.size());
		}
		this.valor = valor;
	}
	
	/**
	 * Método responsável por buscar o último valor inserido no component 
	 * @return Valor 
	 */
	public Valor getValor() {
		if (this.valor == null){
			Valor valorDeMaiorOrdem = null;
			int ordem = 0;	
			for (Valor valor : this.valores) {
				if(valor.getOrdem() > ordem) {
					ordem = valor.getOrdem();
					valorDeMaiorOrdem = valor;
				}
			}
			return valorDeMaiorOrdem;
		}else {
			return this.valor;
		}
	}
	
	public Set<Valor> getValores() {
		return this.valores;
	}

	public Long getIdComponentValores() {
		return idComponentValores;
	}

	@SuppressWarnings("unused")
	private void setIdComponentValores(Long idComponentValores) {
		this.idComponentValores = idComponentValores;
	}

	@SuppressWarnings("unused")
	private void setValores(Set<Valor> valores) {
		this.valores = valores;
	}
	
	@Override
	public Object clone(){
		ComponentValores clone = new ComponentValores();
		
		for(Valor valor: this.valores){
			Valor valorClonado = (Valor)valor.clone();
			
			valorClonado.setComponentValores(clone);
			clone.getValores().add(valorClonado);
		}
		
		return clone;
	}
}
