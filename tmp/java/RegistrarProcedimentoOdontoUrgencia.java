package br.com.infowaypi.ecare.odonto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;

import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaOdontoUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExameOdonto;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.enums.TipoCriticaEnum;
import br.com.infowaypi.ecarebc.atendimentos.validators.ManagerCritica;
import br.com.infowaypi.ecarebc.atendimentos.validators.ServiceApresentacaoCriticasFiltradas;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOdonto;
import br.com.infowaypi.ecarebc.service.Agente;
import br.com.infowaypi.ecarebc.service.MarcacaoService;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

/*if[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO]
import br.com.infowaypi.ecare.services.ValidacaoAtendimentoSegmentacaoAssistencial;
import br.com.infowaypi.ecarebc.produto.SegmentacaoAssistencialEnum;
end[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */

/**
 * 
 * @author Dannylvan
 *
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class RegistrarProcedimentoOdontoUrgencia extends MarcacaoService<GuiaExameOdonto> implements ServiceApresentacaoCriticasFiltradas{
	
	public static final int LIMITE_EXAMES_URGENCIA = 5;

	public ResumoSegurados buscarGuias(String autorizacao, String cpfDoTitular, String numeroDoCartao, Prestador prestador) throws Exception {
		ResumoSegurados resumo = new ResumoSegurados();
		resumo.setGuias(new ArrayList<GuiaSimples>());
		
		if(!Utils.isStringVazia(autorizacao)){
			GuiaConsultaOdontoUrgencia guia = buscarGuias(autorizacao, GuiaConsultaOdontoUrgencia.class, prestador, SituacaoEnum.CONFIRMADO);
			resumo.getGuias().add(guia);
		} else if(!Utils.isStringVazia(cpfDoTitular) || !Utils.isStringVazia(numeroDoCartao)){
			/*if[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO]
			resumo = ValidacaoAtendimentoSegmentacaoAssistencial.buscar(cpfDoTitular, numeroDoCartao, Segurado.class, false, SegmentacaoAssistencialEnum.ODONTOLOGICO.getSegmentacaoAssistencial());		
			else[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */
			resumo = BuscarSegurados.buscar(cpfDoTitular, numeroDoCartao, Segurado.class);
			/* end[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */			
			SearchAgent sa = getSearchSituacoes(SituacaoEnum.CONFIRMADO);
			List<GuiaConsultaOdontoUrgencia> guias = buscarGuias(sa, resumo.getSegurados(), prestador, false, GuiaConsultaOdontoUrgencia.class);
			resumo.getGuias().clear();
			resumo.getGuias().addAll(guias);
		} else {
			throw new ValidateException(MensagemErroEnum.AUSENCIA_DE_PARAMETROS.getMessage());
		}
		
		if(resumo.getGuias().isEmpty()){
			throw new ValidateException("Nenhuma consulta de urgência foi encontrada.");
		}
		
		prestador.tocarObjetos();
		resumo.getGuiaSimples().tocarObjetos();
		return resumo;
	}
	
	public ResumoSegurados selecionarGuia(ResumoSegurados resumo, GuiaConsultaOdontoUrgencia guia){
		guia.tocarObjetos();
		guia.getPrestador().tocarObjetos();
		
		resumo.getGuias().clear();
		
		List<GuiaSimples> guias = new ArrayList<GuiaSimples>();
		guias.add(guia);
		
		resumo.setGuias(guias);
		return resumo;
	}
	
	public GuiaExameOdonto criarGuiaExameOdonto(Prestador prestador, Collection<ProcedimentoOdonto> procedimentos,
			String justificativa, ResumoSegurados resumo, UsuarioInterface usuario) throws Exception {
		
		if(procedimentos.isEmpty()){
			throw new ValidateException("Necessário informar ao menos um procedimento.");
		}
		
		GuiaSimples guia = resumo.getGuiaSimples();
		
		GuiaExameOdonto guiaGerada = criarGuia(guia.getSegurado(), prestador, usuario, guia.getProfissional(), guia.getProfissional(), 
				this.getEspecialidadeOdontologiaGeral(), procedimentos, Utils.hoje(), 
				MotivoEnum.SOLICITACAO_DE_TRATAMENTO_ODONTOLOGICO.getMessage(), Agente.PRESTADOR);
		
		guiaGerada.setGuiaOrigem(guia);
		guia.getGuiasFilhas().add(guiaGerada);
		
		for (ProcedimentoOdonto proc : guiaGerada.getProcedimentos()){ 
			proc.mudarSituacao(usuario, SituacaoEnum.CONFIRMADO.descricao(), MotivoEnum.CONFIRMADA_NO_PRESTADOR.getMessage(), new Date());
		}
		
		guiaGerada.getSituacao().setDescricao(SituacaoEnum.CONFIRMADO.descricao());
		guiaGerada.getSituacao().setMotivo(MotivoEnum.CONFIRMADA_NO_PRESTADOR.getMessage());
		
		/*
		 * Pedro Almir:
		 * 
		 * Problema #3627: Odonto - Bug no recalculo do valor das guias de confirmação de tratamento odonto de urgência.
		 * Ao realizar o registro de um procedimento odonto de urgência a nova guia gerada estava ficando com o valorPagoPrestador
		 * igual a zero. Isso acaba prejudicando o faturamento. Essa linha foi incluida para que os valores da nova guia sejam
		 * recalculados e assim o valorPagoPrestador será setado corretamente.
		 * */
		guiaGerada.recalcularValores();
		return guiaGerada;
	}

	
	
	public void salvarGuia(GuiaSimples guia) throws Exception{
		processaSituacaoCriticas(guia);
		super.salvarGuia(guia);
	}
	
	@Override
	public void filtrarCriticasApresentaveis(GuiaSimples<?> guia) {
		ManagerCritica.processaApresentaveis(guia, TipoCriticaEnum.CRITICA_DLP_SUBGRUPO.valor(), TipoCriticaEnum.CRITICA_DLP_CID.valor());
	}

	@Override
	public void processaSituacaoCriticas(GuiaSimples<?> guia) {
		ManagerCritica.processaSituacao(guia);
	}

	@Override
	public GuiaExameOdonto getGuiaInstanceFor(UsuarioInterface usuario) {
		return new GuiaExameOdonto();
	}
	
	@Override
	public  void onAfterCriarGuia(GuiaExameOdonto guia, Collection<? extends ProcedimentoInterface> procedimentos) throws Exception {
		guia.setDataTerminoAtendimento(guia.getDataAtendimento());
		guia.addAllProcedimentos(procedimentos);
	}
}
