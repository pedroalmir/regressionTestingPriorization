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
 * Service responsável pela retirada de altas de guias de Internação, inseridas de forma
 * equivocada pelos prestadores.
 * 
 * @author <a href="mailto:mquixaba@gmail.com">Marcus bOolean</a>
 * @since 2009-02-05 11:56 AM
 * @see br.com.infowaypi.ecarebc.services.Service
 */
public class RetirarAltaInternacoesService extends Service{
	
	/**
	 * Método responsável por buscar uma guia de Internação apartir da autorizacao informada pelo usuário no 
	 * primeiro passo do fluxo. Guias canceladas, auditadas, fechadas ou faturadas não podem ser alteradas.
	 * @param autorizacao autorizacao da guia informada pelo usuario.
	 * @return guia encontrada.
	 * @throws Exception 
	 */
	public GuiaInternacao buscarGuia(String autorizacao) throws Exception {
		if(Utils.isStringVazia(autorizacao)) {
			throw new RuntimeException("Caro usuário, informe a Autorização da Internação para prosseguir.");
		}
		String[] situacoes = {SituacaoEnum.CANCELADO.descricao(),SituacaoEnum.AUDITADO.descricao(),
					SituacaoEnum.FATURADA.descricao(), SituacaoEnum.PAGO.descricao()};
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new NotIn("situacao.descricao",situacoes));
		sa.addParameter(new Equals("autorizacao", autorizacao));
		sa.addParameter(new IsNotNull("altaHospitalar"));
		GuiaInternacao guia = sa.uniqueResult(GuiaInternacao.class);
		Assert.isNotNull(guia, "Caro usuário, não foram encontradas guias de Internação aptas a este processo.");
		return guia;
	}
	
	
	/**
	 * Método responsável por retirar a alta da guia de internação escolhida no primeiro passo
	 * e salvar as alterações.
	 * @param guia guia de internação que terá sua alta retirada, mesmo que ja tenha sido fechada.
	 * @return guia de internaçao com alta hospitalar retirada.
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
	 * Método responsável por prover ao Jheat a guia pra ser mostrada na ultima tela.
	 * @param guia guia alterada e salva no segundo passo.
	 */
	public void finalizar(GuiaInternacao guia) {}
	
	
	/**
	 * Método responsável por voltar a situaçao de uma guia de Internacao
	 * para a ultima situação diferente da situacaoAtual.
	 * @param guia que terá sua situação voltada
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
