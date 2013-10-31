package br.com.infowaypi.ecare.services;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.service.autorizacoes.AutorizarInternacaoEletivaService;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.user.UsuarioInterface;

public class AutorizarInternacaoEletiva extends AutorizarInternacaoEletivaService {
	
	public ResumoGuias buscarGuias(String autorizacao, String dataInicial, String dataFinal,Prestador prestador) throws Exception {
		return super.buscarGuias(autorizacao, dataInicial, dataFinal, prestador);
	}
	
	public GuiaSimples selecionarGuia(GuiaSimples guia, UsuarioInterface usuario) {
		guia.tocarObjetos();
		guia.setUsuarioDoFluxo(usuario);
		return guia;
	}
	
	public GuiaCompleta conferirDados(GuiaCompleta guia) throws Exception {
		ImplDAO.save(guia);
		ImplDAO.save(guia.getPrestador().getConsumoIndividual());
		ImplDAO.save(guia.getSegurado().getConsumoIndividual());
		return guia;
	}
	
	public void finalizar(GuiaCompleta guia) throws Exception {
	}

}
