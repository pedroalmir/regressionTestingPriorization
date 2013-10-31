/**
 * 
 */
package br.com.infowaypi.ecare.services.suporte;

import java.util.Iterator;

import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.ecarebc.atendimentos.alta.AltaHospitalar;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.IsNotNull;
import br.com.infowaypi.molecular.parameter.NotIn;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;


/**
 * Service respons�vel pela retirada de altas de guias de Interna��o, inseridas de forma
 * equivocada pelos prestadores.
 * 
 * @author <a href="mailto:mquixaba@gmail.com">Marcus bOolean</a>
 * @since 2009-02-05 11:56 AM
 * @see br.com.infowaypi.ecarebc.services.Service
 */
public class RetirarAltaInternacoesService extends Service{
	
	/**
	 * M�todo respons�vel por buscar uma guia de Interna��o apartir da autorizacao informada pelo usu�rio no 
	 * primeiro passo do fluxo. Guias canceladas, auditadas, fechadas ou faturadas n�o podem ser alteradas.
	 * @param autorizacao autorizacao da guia informada pelo usuario.
	 * @return guia encontrada.
	 * @throws Exception 
	 */
	public GuiaInternacao buscarGuia(String autorizacao) throws Exception {
		if(Utils.isStringVazia(autorizacao)) {
			throw new RuntimeException("Caro usu�rio, informe a Autoriza��o da Interna��o para prosseguir.");
		}
		String[] situacoes = {SituacaoEnum.CANCELADO.descricao(),SituacaoEnum.AUDITADO.descricao(),
					SituacaoEnum.FATURADA.descricao(), SituacaoEnum.PAGO.descricao()};
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new NotIn("situacao.descricao",situacoes));
		sa.addParameter(new Equals("autorizacao", autorizacao));
		sa.addParameter(new IsNotNull("altaHospitalar"));
		GuiaInternacao guia = sa.uniqueResult(GuiaInternacao.class);
		Assert.isNotNull(guia, "Caro usu�rio, n�o foram encontradas guias de Interna��o aptas a este processo.");
		return guia;
	}
	
	
	/**
	 * M�todo respons�vel por retirar a alta da guia de interna��o escolhida no primeiro passo
	 * e salvar as altera��es.
	 * @param guia guia de interna��o que ter� sua alta retirada, mesmo que ja tenha sido fechada.
	 * @return guia de interna�ao com alta hospitalar retirada.
	 * @throws Exception 
	 */
	public GuiaInternacao alterarGuia(GuiaInternacao guia) throws Exception {
		AltaHospitalar alta = guia.getAltaHospitalar();
		guia.setAltaHospitalar(null);
		ImplDAO.delete(alta);
		ImplDAO.save(guia);
		
		if (guia.isSituacaoAtual(SituacaoEnum.FECHADO.descricao())){
			voltarSituacao(guia, SituacaoEnum.FECHADO);
		}
		voltarSituacao(guia, SituacaoEnum.ALTA_REGISTRADA);
		
		return guia;
	}
	
	/**
	 * M�todo respons�vel por prover ao Jheat a guia pra ser mostrada na ultima tela.
	 * @param guia guia alterada e salva no segundo passo.
	 */
	public void finalizar(GuiaInternacao guia) {}
	
	
	/**
	 * M�todo respons�vel por voltar a situa�ao de uma guia de Internacao
	 * para a ultima situa��o diferente da situacaoAtual.
	 * @param guia que ter� sua situa��o voltada
	 * @throws Exception 
	 */
	private void voltarSituacao(GuiaInternacao guia, SituacaoEnum situacaoAtual) throws Exception {
		Iterator<SituacaoInterface> situacoes = guia.getColecaoSituacoes().getSituacoes().iterator();
		SituacaoInterface situacao;
		SituacaoInterface situacaoAnterior = getSituacaoAnterior(guia, situacaoAtual);
		
		while (situacoes.hasNext()) {
			situacao = situacoes.next();
			if(situacao.getDescricao().equals(situacaoAtual.descricao())){
				situacoes.remove();
				ImplDAO.delete(situacao);
			}
		}
		guia.setSituacao(situacaoAnterior);
	}
	
	private SituacaoInterface getSituacaoAnterior(GuiaInternacao guia, SituacaoEnum situacaoAtual) {
		SituacaoInterface anterior = null;
		for (int i = guia.getSituacoes().size(); i < 0; i--) {
			if(guia.getSituacao(i).equals(situacaoAtual.descricao())){
				anterior = guia.getSituacao(i-1);
			}
		}
		return anterior;
	}

}
