package br.com.infowaypi.ecarebc.service;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
/**
 * ExcluirExameService fornece um serviço de busca para a classe ExluirExame.
 * 
 * @author jefferson
 *
 */
public class ExcluirExameService extends Service{

	public GuiaExame buscarGuiaParaExcluirExame(String autorizacao,Prestador prestador) throws Exception {
		return super.buscarGuias(autorizacao, prestador,false, GuiaExame.class,SituacaoEnum.ABERTO,SituacaoEnum.CONFIRMADO);	
	}
}
