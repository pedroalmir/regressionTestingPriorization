package br.com.infowaypi.ecarebc.service.internacao;

import static br.com.infowaypi.msr.utils.Assert.isNotNull;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacaoEletiva;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Service para a confirmação de guias de internação eletivas no plano de saúde
 * @author root
 */
public class ConfirmacaoGuiaInternacaoEletivaService<S extends SeguradoInterface> extends Service{
	
	public ConfirmacaoGuiaInternacaoEletivaService(){
		super();
		//Modificando a situação da guia no índice de guias
	}
	
	public ResumoGuias<GuiaInternacaoEletiva> buscarGuiasConfirmacao(Collection<S> segurados,Prestador prestador) throws Exception{
		SearchAgent sa = getSearchSituacoes(SituacaoEnum.AGENDADA);	
		List<GuiaInternacaoEletiva> guias = super.buscarGuias(sa,segurados,	prestador,true, GuiaInternacaoEletiva.class);
		return new ResumoGuias<GuiaInternacaoEletiva>(guias,ResumoGuias.SITUACAO_TODAS,true);
	}	
	/**
	 * Metodo que busca uma guia de internação autorizada e valida o prazo desta busca entre 60 dias
	 * para confirmação
	 * @param autorizacao
	 * @param prestador
	 * @param usuario
	 * **/	
	public GuiaInternacao buscarGuiasConfirmacao(String autorizacao, Prestador prestador , UsuarioInterface usuario) throws Exception{
		if (Utils.isStringVazia(autorizacao))
			throw new ValidateException(MensagemErroEnum.NUMERO_AUTORIZACAO_INVALIDO.getMessage());
		
		GuiaInternacao guia = super.buscarGuias(autorizacao,prestador,true, GuiaInternacaoEletiva.class, SituacaoEnum.AUTORIZADO);
		Assert.isNotNull(guia, "Guia não encontrada!");
	
		Calendar dataAtual = Calendar.getInstance();
		
		Calendar dataAutorizacao = new GregorianCalendar();
		dataAutorizacao.setTime(guia.getDataAutorizacao());
	
		Integer diferenca = Utils.diferencaEmDias(dataAutorizacao,dataAtual);

		if(diferenca > GuiaInternacaoEletiva.PRAZO_GUIA_INTERNACAO){
			
			org.hibernate.Transaction transicao =  HibernateUtil.currentSession().beginTransaction(); 
			guia.mudarSituacao(usuario, SituacaoEnum.CANCELADO.descricao(),"Cancelamento Automatico. Prazo de Autorização superior a "+GuiaInternacaoEletiva.PRAZO_GUIA_INTERNACAO+" dias" , new Date());
			transicao.commit();
			ImplDAO.save(guia);
			
			throw new ValidateException(MensagemErroEnum.DATA_DE_AUTORIZACAO_COM_PRAZO_ULTRAPASSADO.getMessage(String.valueOf(diferenca), String.valueOf(GuiaInternacaoEletiva.PRAZO_GUIA_INTERNACAO)));
		}
		guia.tocarObjetos();
		return guia;
	}

	
	@SuppressWarnings("unchecked")
	public GuiaCompleta selecionarGuia(GuiaInternacao guia) throws Exception {
		return super.selecionarGuia(guia);
	}
	 
	public GuiaInternacao salvarGuia(GuiaInternacao guia, UsuarioInterface usuario) throws Exception{
		isNotNull(guia,"Guia Inválida!");
		
		guia.setValorAnterior(guia.getValorTotal());
		
		guia.mudarSituacao(usuario, SituacaoEnum.ABERTO.descricao(), MotivoEnum.PACIENTE_INTERNACAO_ELETIVA.getMessage(), new Date());
		
		if(guia.isFromPrestador()) {
			guia.getPrestador().incrementarConsumoFinanceiro(guia);
		}	
		
		if(guia.getDataAtendimento() == null)
			guia.setDataAtendimento(new Date());
		
		for (ItemDiaria item : guia.getItensDiaria()) {
			if(item.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao()) && item.getDataInicial()==null)
				item.calculaDataInicial();
		}
		
		ImplDAO.save(guia);
		ImplDAO.save(guia.getPrestador().getConsumoIndividual());
		ImplDAO.save(guia.getSegurado().getConsumoIndividual());
		
		//Atualização do consumo financeiro
//		StateMachineConsumo.updateConsumoAtendimento(guia, true, true);
		
		return guia;
	}
	

}
