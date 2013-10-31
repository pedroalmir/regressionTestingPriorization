package br.com.infowaypi.ecare.services;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Transaction;

import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.Role;
import br.com.infowaypi.ecarebc.financeiro.faturamento.GuiaFaturavel;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.service.ReimpressaoGuiasService;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

public class ReimpressaoDeGuiasService extends ReimpressaoGuiasService<Segurado> {
	
	public ResumoGuias<GuiaFaturavel> buscarGuias(String cpfDoTitular,String numeroDoCartao, Prestador prestador, UsuarioInterface usuario) throws Exception{
		ResumoSegurados resumo = BuscarSegurados.buscar(cpfDoTitular, numeroDoCartao, Segurado.class);
		return super.buscarGuiasReimpressao(resumo.getSegurados(), prestador, usuario); 
	}
	
	public GuiaFaturavel selecionarGuia(GuiaFaturavel guia) throws Exception {
		Assert.isNotNull(guia, MensagemErroEnum.NENHUMA_GUIA_SELECIONADA.getMessage());
		if (guia instanceof GuiaSimples) {
			super.tocarObjeto((GuiaSimples) guia);
		}
		return guia;
	}
	
	public GuiaFaturavel buscarGuia(String autorizacao, Prestador prestador) throws Exception {
		return buscarGuiasReimpressaoPrestadorAnestesista(autorizacao).get(0);
	}

	public ResumoGuias<GuiaFaturavel> buscarGuias(String autorizacao, String cpfDoTitular,String numeroDoCartao,
			Integer tipoDeGuia, Date dataInicial, Date dataFinal, Prestador prestador, UsuarioInterface usuario) throws Exception{
		int count = 0;
		boolean isCPFInformado = !Utils.isStringVazia(cpfDoTitular);
		boolean isAutorizacaoInformada = !Utils.isStringVazia(autorizacao);
		boolean isNumeroDoCartaoInformado = !Utils.isStringVazia(numeroDoCartao);
		
		if(isAutorizacaoInformada){ 
			count++;
			autorizacao = autorizacao.trim();
		}
		if(isCPFInformado) {
			count++;
		}
		if(isNumeroDoCartaoInformado) {
			count++;
		}
		if ((count > 1)) {
			throw new RuntimeException("Preencha apenas um desses campos: Autorização, CPF, Numero Do Cartão.");
		}
		else if (count == 0) {
			throw new RuntimeException("Preencha pelo menos um desses campos: Autorização, CPF, Numero Do Cartão.");
		}
	
		boolean isUsuarioPrestadorAnestesista = usuario.getRole().equals(Role.PRESTADOR_ANESTESISTA.getValor());

		if(isAutorizacaoInformada){
			if(!usuario.isPrestador()){
				return new ResumoGuias<GuiaFaturavel>(buscarGuiasReimpressao(autorizacao),ResumoGuias.SITUACAO_TODAS,true);
			}else if(isUsuarioPrestadorAnestesista) {
				return new ResumoGuias<GuiaFaturavel>(buscarGuiasReimpressaoPrestadorAnestesista(autorizacao), ResumoGuias.SITUACAO_TODAS, true);
			}else{
				GuiaFaturavel guia = this.buscarGuiasReimpressao(autorizacao, prestador);
				List<GuiaFaturavel> guias = new ArrayList<GuiaFaturavel>();
				guias.add(guia);
				return new ResumoGuias<GuiaFaturavel>(guias, ResumoGuias.SITUACAO_TODAS,true);
			}
			
		}else{
			List<Segurado> segurados = BuscarSegurados.buscarReimpressao(cpfDoTitular, numeroDoCartao, Segurado.class).getSegurados();
			if(!usuario.isPrestador()){
				return this.buscarGuiasReimpressao(segurados, usuario, dataInicial, dataFinal, tipoDeGuia);
			}else{			
				return this.buscarGuiasReimpressao(segurados, prestador, usuario, dataInicial, dataFinal, tipoDeGuia);
			}
		}	
	}
	
	protected <G extends GuiaFaturavel> void finalizarReimpressao(G guia) {
	}

	public void finalizarReimpressao() {
	}
}

