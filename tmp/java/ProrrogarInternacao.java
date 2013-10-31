package br.com.infowaypi.ecare.services.internacao;

import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.service.internacao.ProrrogarInternacaoService;

public class ProrrogarInternacao extends ProrrogarInternacaoService<Segurado> {
	public ResumoGuias<GuiaCompleta> buscarGuias(String numeroDoCartao,String cpfDoTitular, Prestador prestador) throws Exception {
		ResumoSegurados resumo = BuscarSegurados.buscar(numeroDoCartao, cpfDoTitular, Segurado.class);
		return super.buscarGuiasAProrrogar(resumo.getSegurados(), prestador);
	}

}
