package br.com.infowaypi.ecarebc.procedimentos;

import br.com.infowaypi.ecarebc.odonto.EstruturaOdontoEF;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * Classe que representa um procedimento de um tratamento odontológico
 * @author Danilo Nogueira Portela
 */
public class ProcedimentoOdontoRestauracao extends ProcedimentoOdonto<EstruturaOdontoEF> {
  
	private static final long serialVersionUID = 1L;
	
	public ProcedimentoOdontoRestauracao(){
		super();
	}
	
	public ProcedimentoOdontoRestauracao(UsuarioInterface usuario){
		super(usuario);
	}
	
	@Override
	public int getTipoProcedimento() {
		return PROCEDIMENTO_ODONTO_RESTAURACAO;
	}
	
	@Override
	public Boolean isForRestauracao(){
		return true;
	}
}
