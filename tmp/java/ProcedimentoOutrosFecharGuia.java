package br.com.infowaypi.ecarebc.procedimentos;

import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * @author Marcus bOolean
 *
 */
public class ProcedimentoOutrosFecharGuia extends ProcedimentoOutros {

	public ProcedimentoOutrosFecharGuia() {
		this(null);
	}
	
	public ProcedimentoOutrosFecharGuia(UsuarioInterface usuario){
		super(usuario);
	}
}
