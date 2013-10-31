package br.com.infowaypi.ecare.services;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCirurgiaOdonto;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExameOdonto;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.service.autorizacoes.AuditarTratamentoOdontoService;
import br.com.infowaypi.ecarebc.utils.CommandCorrecaoValorGuia;
import br.com.infowaypi.ecarebc.utils.ProcedimentoUtils;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * Service básico para a realização de perícia final em tratamentos odontológicos e auditoria em guias de cirurgia odontológica.
 * @author Danilo Nogueira Portela
 */
public class AuditarTratamentoOdonto extends AuditarTratamentoOdontoService {

	public ResumoGuias<GuiaCirurgiaOdonto> buscarGuiasCirurgia(String autorizacao, String dataInicial, String dataFinal, Prestador prestador) throws Exception {
		return super.buscarGuias(GuiaCirurgiaOdonto.class, autorizacao, dataInicial, dataFinal, prestador);
	}

	public ResumoGuias<GuiaExameOdonto> buscarGuiasTratamento(String autorizacao, String dataInicial, String dataFinal, Prestador prestador) throws Exception {
		return super.buscarGuias(GuiaExameOdonto.class, autorizacao, dataInicial, dataFinal, prestador);
	}

	@Override
	public GuiaCirurgiaOdonto auditarGuia(Boolean glosarTotalmente, String motivoDeGlosa, GuiaCirurgiaOdonto guia, UsuarioInterface usuario) throws Exception {
		ProcedimentoUtils.aplicaDescontoDaViaDeAcesso(guia); 
		
		guia = super.auditarGuia(glosarTotalmente, motivoDeGlosa, guia, usuario);
		guia.calculaHonorariosInternos(usuario);
		guia.recalcularValores();
		
		return guia;
	}

}
