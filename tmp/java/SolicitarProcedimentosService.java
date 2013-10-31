package br.com.infowaypi.ecarebc.service;

import java.util.Collection;
import java.util.Date;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * @author Marcus Quixabeira
 *
 */
public class SolicitarProcedimentosService extends Service {
	
	public GuiaCompleta adicionarProcedimentos(Collection<ProcedimentoCirurgico> procedimentosCirurgicos, 
			GuiaCompleta guia, UsuarioInterface usuario) throws Exception {
		
		for (ProcedimentoCirurgicoInterface cirurgico : procedimentosCirurgicos) {
			cirurgico.mudarSituacao(usuario, SituacaoEnum.SOLICITADO.descricao(), "Solicitação de Exames/Procedimentos Especiais", new Date());
			cirurgico.setGuia(guia);
			guia.addProcedimento(cirurgico);
		}
		
		return guia;
	}	
	
}
