package br.com.infowaypi.ecarebc.procedimentos;


import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * @author Wislanildo - 07/12/2011
 */

@SuppressWarnings({"unchecked"})
public class ProcedimentoSimplesFecharGuia extends Procedimento{

	public ProcedimentoSimplesFecharGuia() {
		this(null);
	}

	public ProcedimentoSimplesFecharGuia(UsuarioInterface usuario){
		super(usuario);
	}

}
