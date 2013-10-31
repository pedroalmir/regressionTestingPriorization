/**
 * 
 */
package br.com.infowaypi.ecare.services.suporte;

import java.util.Date;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.ecarebc.atendimentos.validators.fechamento.FechamentoAltaValidator;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.ecarebc.service.FecharGuiaService;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.NotIn;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

public class FecharParcialSuporteService extends Service{
	
	public GuiaInternacao buscarGuia(String autorizacao) throws Exception {
		if(Utils.isStringVazia(autorizacao)) {
			throw new RuntimeException("Caro usuário, informe a Autorização da Internação para prosseguir.");
		}
		String[] situacoes = {SituacaoEnum.SOLICITADO_INTERNACAO.descricao(),SituacaoEnum.AUTORIZADO.descricao(),
							  SituacaoEnum.ALTA_REGISTRADA.descricao(), SituacaoEnum.FECHADO.descricao(),
							  SituacaoEnum.ENVIADO.descricao(), SituacaoEnum.RECEBIDO.descricao(),
							  SituacaoEnum.CANCELADO.descricao(),SituacaoEnum.AUDITADO.descricao(),
							  SituacaoEnum.FATURADA.descricao(), SituacaoEnum.PAGO.descricao()};
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new NotIn("situacao.descricao",situacoes));
		sa.addParameter(new Equals("autorizacao", autorizacao));
		GuiaInternacao guia = sa.uniqueResult(GuiaInternacao.class);
		Assert.isNotNull(guia, "Caro usuário, não foram encontradas guias de Internação aptas a este processo.");
		guia.tocarObjetos();
		return guia;
	}
	
	public GuiaInternacao fecharGuia(UsuarioInterface usuario, boolean receber, GuiaInternacao guia, String motivo, Date dataFinal) throws Exception{
		if (guia.isAberta()){
			new FechamentoAltaValidator().execute(guia, true, dataFinal, usuario);
			guia.getSituacao(SituacaoEnum.ALTA_REGISTRADA.descricao()).setDataSituacao(dataFinal);
			guia.getSituacao().setMotivo(guia.getSituacao().getMotivo()+". "+motivo);
			guia.fechar(true, true, null, dataFinal, usuario);
			guia.getSituacao().setMotivo(guia.getSituacao().getMotivo()+" "+motivo);
			guia.getSituacao().setDataSituacao(dataFinal);
			
		}
		if (receber){
			guia.mudarSituacao(usuario, SituacaoEnum.ENVIADO.descricao(), motivo, dataFinal);
			guia.mudarSituacao(usuario, SituacaoEnum.RECEBIDO.descricao(), motivo, dataFinal);
			guia.setDataRecebimento(dataFinal);
		}
		
		for (GuiaInternacao gi : guia.getOutrasParciais()) {
			gi.getAutorizacao();
		}
		
		return guia;
	}
	
	public GuiaInternacao conferirDados(GuiaInternacao guia) throws Exception {
		FecharGuiaService<SeguradoInterface> service = new FecharGuiaService();
		GuiaCompleta<ProcedimentoInterface> parcial = service.salvarGuia(guia);
		
		parcial.getSituacao().setDataSituacao(guia.getDataTerminoAtendimento());
		parcial.getSituacao().setUsuario(guia.getSituacao().getUsuario());
		parcial.getSituacao().setMotivo(parcial.getSituacao().getMotivo()+ " "+guia.getSituacao().getMotivo());
		
		ImplDAO.save(parcial);
		return guia;
	}
	
	public void finalizar(GuiaInternacao guia) {}
	

}
