package br.com.infowaypi.ecare.services;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.service.ExcluirExameService;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.sensews.client.SenseManager;

/**
 * ExcluirExame é um fluxo para a exclusão de procedimentos  de  uma guia de exame.
 * O fluxo procura uma guia de exame do prestador, na situação de Aberto ou Confirmado e 
 * fornece uma interface para o usuário selecionar o(s) procedimento(s) desejado(s).
 * @author jefferson
 */

public class ExcluirExame extends ExcluirExameService {

	Set<Procedimento> procedimentosExcluidos = new HashSet<Procedimento>();

	public GuiaExame buscarGuiaExclusaoProcedimento(String autorizacao,Prestador prestador) throws Exception {
		GuiaExame guia =(GuiaExame) super.buscarGuiaParaExcluirExame(autorizacao,prestador);
		guia.tocarObjetos();
		return  guia;
	}

	public GuiaExame selecionarProcedimentos(UsuarioInterface usuario,String motivoExclusao,GuiaExame guia) throws Exception{
		ajustarProcedimentosParaExclusao(guia,motivoExclusao,usuario);
		ajustarGuiaOrigem(guia.getGuiaOrigem(),usuario);
		guia.recalcularValores();
		guia.updateValorCoparticipacao();
		guia.tocarObjetos();
		return guia;
	}

	public GuiaExame salvarGuia(GuiaExame guia) throws Exception{
		ImplDAO.save(guia);
		ImplDAO.save(guia.getGuiaOrigem());
		/* if[SENSE_MANAGER] */
		SenseManager.CANCELAMENTO_EXAME.analisar(guia);
		/* end[SENSE_MANAGER] */
		return guia;
	}

	public void finalizar(){}

	private void ajustarGuiaOrigem(GuiaSimples guiaOrigem, UsuarioInterface usuario) {

		if (guiaOrigem != null){
			boolean isSituacaoRealizado = guiaOrigem.getSituacao().getDescricao().equals(SituacaoEnum.REALIZADO.descricao());
			if (isSituacaoRealizado){
				guiaOrigem.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), "Restabelecido em virtude do cancelamento do procedimento em uma guia de confirmação", new Date());
			}
			guiaOrigem.recalcularValores();
		}
	}

	private void ajustarProcedimentosParaExclusao(GuiaExame guia,String motivoExclusao, UsuarioInterface usuario) throws Exception {
		validarExclusaoDeProcedimentos(guia.getProcedimentosNaoGlosadosNemCanceladosNemNegados());
		excluirProcedimentos(guia, motivoExclusao, usuario, guia.getProcedimentosNaoGlosadosNemCanceladosNemNegados()); 
	}

	private void excluirProcedimentos(GuiaExame guia, String motivoExclusao,
			UsuarioInterface usuario,
			Set<Procedimento> procedimentosAptosAExclusao) {
		for(Procedimento p : procedimentosAptosAExclusao) {
			if (p.getSelecionadoParaExclusao()){
				ajustarProcedimentosNaGuiaOrigem(guia.getGuiaOrigem(), p, motivoExclusao, usuario);
				p.mudarSituacao(usuario, SituacaoEnum.REMOVIDO.descricao(), motivoExclusao, new Date());
				p.setValorAtualDoProcedimento(new BigDecimal(0));
				p.setValorCoParticipacao(new BigDecimal(0));
			}
		}
	}

	private void validarExclusaoDeProcedimentos(Set<Procedimento> procedimentosAptosAExclusao)
			throws ValidateException {
		int qtdProcedimentosAptosAExclusao = procedimentosAptosAExclusao.size();
		
		int qtdProcedimentosMarcadosParaExclusao = 0;
		int qtdProcedimentosNaoMarcadosParaExclusao = 0;
		
		for(Procedimento p : procedimentosAptosAExclusao) {
			if (p.getSelecionadoParaExclusao()){
				qtdProcedimentosMarcadosParaExclusao++;
			}else {
				qtdProcedimentosNaoMarcadosParaExclusao++;
			}
		} 
		
		boolean  nenhumProcedimentoMarcadoParaExclusao = (qtdProcedimentosNaoMarcadosParaExclusao == qtdProcedimentosAptosAExclusao);
		boolean  todosProcedimentosMarcadosParaExclusao = (qtdProcedimentosMarcadosParaExclusao == qtdProcedimentosAptosAExclusao) || (qtdProcedimentosAptosAExclusao == 0);
		 
		if (nenhumProcedimentoMarcadoParaExclusao){
			 throw new ValidateException(MensagemErroEnum.ERRO_NENHUM_PROCEDIMENTO_SELECIONADO_PARA_EXCLUSAO_GUIA_EXAME.getMessage());
		 }
		
		if (todosProcedimentosMarcadosParaExclusao){
			 throw new ValidateException(MensagemErroEnum.ERRO_EXCLUSAO_TODOS_PROCEDIMENTOS_GUIA_EXAME.getMessage());
		 }

	}

	private void ajustarProcedimentosNaGuiaOrigem(GuiaSimples guiaOrigem,Procedimento proc, String motivoExclusao, UsuarioInterface usuario) {
		Iterator iterator = guiaOrigem.getProcedimentos().iterator();
		while (iterator.hasNext()) {
			Procedimento procedimento = (Procedimento) iterator.next();
			boolean temProcedimento = procedimento.getProcedimentoDaTabelaCBHPM().equals(proc.getProcedimentoDaTabelaCBHPM());
			if (temProcedimento){
				if (procedimento.getSituacao().getDescricao().equals(SituacaoEnum.REALIZADO.descricao())){
					procedimento.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), motivoExclusao, new Date());
				}
			}
		}
	}

}
