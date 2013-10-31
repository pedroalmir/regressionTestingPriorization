/**
 * 
 */
package br.com.infowaypi.ecare.services;

import java.util.Date;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.atendimentos.enums.TipoCriticaEnum;
import br.com.infowaypi.ecarebc.atendimentos.validators.ManagerCritica;
import br.com.infowaypi.ecarebc.atendimentos.validators.ServiceApresentacaoCriticasFiltradas;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.service.autorizacoes.AutorizarExamesProcedimentosEspeciaisService;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * @author Marcus Boolean
 * 
 */
@SuppressWarnings("rawtypes")
public class AutorizarExamesProcedimentosEspeciais extends AutorizarExamesProcedimentosEspeciaisService implements ServiceApresentacaoCriticasFiltradas {
	
	public ResumoGuias buscarGuias(String autorizacao, Date dataInicial, Date dataFinal,Prestador prestador) throws Exception {
		return super.buscarGuias(autorizacao, dataInicial, dataFinal, prestador);
	}
	
	@Override
	public GuiaCompleta selecionarGuiaAutorizacao(GuiaCompleta guia, UsuarioInterface usuario) throws ValidateException {
		
		GuiaCompleta guiaSelecionada = super.selecionarGuiaAutorizacao(guia, usuario);		
		
		filtrarCriticasApresentaveis(guiaSelecionada);
		
		return guiaSelecionada;
	}
	
	@Override
	public GuiaCompleta<ProcedimentoInterface> autorizarExamesProcedimentosEspeciais(GuiaCompleta<ProcedimentoInterface> guia, 
			UsuarioInterface usuario) throws Exception {
		
		GuiaCompleta<ProcedimentoInterface> guiaAutorizada = super.autorizarExamesProcedimentosEspeciais(guia,usuario);
		
		processaSituacaoCriticas(guiaAutorizada);
		
		return guiaAutorizada;
	}
	
	@Override
	public void filtrarCriticasApresentaveis(GuiaSimples<?> guia) {
		ManagerCritica.processaApresentaveis(guia, TipoCriticaEnum.CRITICA_DLP_SUBGRUPO.valor(), TipoCriticaEnum.CRITICA_DLP_CID.valor());
	}

	@Override
	public void processaSituacaoCriticas(GuiaSimples<?> guia) {
		ManagerCritica.processaSituacao(guia);
	}

}
