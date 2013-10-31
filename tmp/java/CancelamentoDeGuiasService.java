package br.com.infowaypi.ecare.services;
import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.service.CancelamentoGuiasService;
import br.com.infowaypi.msr.user.UsuarioInterface;

public class CancelamentoDeGuiasService extends CancelamentoGuiasService<Segurado> {
	
	public ResumoGuias<GuiaSimples> buscarGuiasCancelamento(String cpfDoTitular,String numeroDoCartao,  Prestador prestador, UsuarioInterface usuario) throws Exception{
		ResumoSegurados resumo = BuscarSegurados.buscar(numeroDoCartao, cpfDoTitular, Segurado.class);
		return buscarGuiasCancelamento(resumo.getSegurados(), null, null, prestador, usuario);
	}

	public GuiaSimples selecionarGuia(GuiaSimples guia) throws Exception {
		return guia;
	}
}
