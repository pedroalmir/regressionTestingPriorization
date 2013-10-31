package br.com.infowaypi.ecare.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.enums.MotivoEnumSR;
import br.com.infowaypi.ecare.resumos.ResumoGuia;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaAcompanhamentoAnestesico;
import br.com.infowaypi.ecarebc.atendimentos.GuiaAtendimentoUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCirurgiaOdonto;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.LoteDeGuias;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GuiaHonorarioMedico;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.painelDeControle.PainelDeControle;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.utils.CompetenciaUtils;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Between;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.molecular.parameter.OrderBy;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

public class EntregarLote {

	@SuppressWarnings("unchecked")
	public ResumoGuia buscarGuias(Date competencia, Integer tipoDeGuia, Prestador prestador) {
		
		Assert.isTrue(prestador.isExigeEntregaLote(), "Esse prestador não está habilitado para registrar o envio de lotes.");
		
		validaTiposDeGuiaPrestadorAnestesita(tipoDeGuia, prestador);
		validaTiposDeGuiaPrestadorNaoAnestesita(tipoDeGuia, prestador);
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("prestador",prestador));
		Date inicioCompetencia 	= CompetenciaUtils.getInicioCompetencia(competencia);
		Date fimCompetencia 	= CompetenciaUtils.getFimCompetencia(competencia);
		sa.addParameter(new Between("dataTerminoAtendimento", 
						inicioCompetencia,
						fimCompetencia));
		sa.addParameter(new In("situacao.descricao", 
						SituacaoEnum.FECHADO.descricao(), 
						SituacaoEnum.DEVOLVIDO.descricao()));
		
		if (tipoDeGuia.equals(LoteDeGuias.HONORARIOS)) {
			sa.addParameter(new OrderBy("dataMarcacao"));
		} else {
			sa.addParameter(new OrderBy("dataTerminoAtendimento"));
		}
		
		List<GuiaSimples> guias = new ArrayList<GuiaSimples>();
		if(tipoDeGuia.equals(LoteDeGuias.INTERNACOES)){
			guias.addAll(sa.list(GuiaInternacao.class));
		}else if(tipoDeGuia.equals(LoteDeGuias.URGENCIA)){
			guias.addAll(sa.list(GuiaConsultaUrgencia.class));
			guias.addAll(sa.list(GuiaAtendimentoUrgencia.class));
		}else if(tipoDeGuia.equals(LoteDeGuias.EXAME)){
			guias.addAll(sa.list(GuiaExame.class));
		}else if(tipoDeGuia.equals(LoteDeGuias.CIRURGIA_ODONTO)){
			guias.addAll(sa.list(GuiaCirurgiaOdonto.class));
		}else if (tipoDeGuia.equals(LoteDeGuias.HONORARIOS)){
			guias.addAll(sa.list(GuiaHonorarioMedico.class));
		}else if (tipoDeGuia.equals(LoteDeGuias.ACOMPANHAMENTO_ANESTESICO)){
			guias.addAll(sa.list(GuiaAcompanhamentoAnestesico.class));
		}
		
		Assert.isNotEmpty(guias, MensagemErroEnumSR.NENHUMA_GUIA_APTA_A_SER_ENTREGUE.getMessage(Utils.format(competencia, "MM/yyyy")));
		
		this.tocarObjetos(guias);
		prestador.tocarObjetos();
		ResumoGuia resumo = new ResumoGuia(guias, ResumoGuia.SITUACAO_TODAS, false);
		
		return resumo;
	}
	
	/**
	 * Método responsável por validar se o tipode guia selecionado é do tipo Guia de Honorario ou Guia de Acompanhamento anestésico, quando o 
	 * prestador for do tipo anestesita.
	 * @param tipoDeGuia
	 * @param prestador
	 */
	private void validaTiposDeGuiaPrestadorNaoAnestesita(Integer tipoDeGuia, Prestador prestador) {
		boolean isPrestadorAnestesista 		= prestador.isPrestadorAnestesista(); 
		boolean isAcompanhamentoAnestesico 	= tipoDeGuia.equals(LoteDeGuias.ACOMPANHAMENTO_ANESTESICO);
		
		if(!isPrestadorAnestesista && isAcompanhamentoAnestesico) {
			throw new RuntimeException("Tipo de Guia inválido para o prestador " + prestador.getPessoaJuridica().getFantasia());
		}
	}

	/**
	 * Método responsável por validar se o tipode guia selecionado é do tipo Guia de Honorario ou Guia de Acompanhamento anestésico, quando o 
	 * prestador for do tipo anestesita.
	 * @param tipoDeGuia
	 * @param prestador
	 */
	private void validaTiposDeGuiaPrestadorAnestesita(Integer tipoDeGuia, Prestador prestador) {
		boolean isPrestadorAnestesista 		= prestador.isPrestadorAnestesista(); 
		boolean isHonorario 				= tipoDeGuia.equals(LoteDeGuias.HONORARIOS);
		boolean isAcompanhamentoAnestesico 	= tipoDeGuia.equals(LoteDeGuias.ACOMPANHAMENTO_ANESTESICO);
		
		if(isPrestadorAnestesista && !(isHonorario || isAcompanhamentoAnestesico)) {
			throw new RuntimeException("Tipo de Guia inválido para Prestador Anestesista.");
		}
	}
	
	public LoteDeGuias selecionarGuias(ResumoGuia resumo, Collection<GuiaSimples<ProcedimentoInterface>> guias, Prestador prestador, UsuarioInterface usuario) throws ValidateException{
		
		Assert.isNotEmpty(guias, "Selecione pelo menos uma guia!");
		
		GuiaSimples<ProcedimentoInterface> primeiraGuia = new ArrayList<GuiaSimples<ProcedimentoInterface>>(guias).get(0);
		
		if (primeiraGuia.isInternacao()) {
			validaEnvioOutrasParciais(guias);
		}
		
		Date competencia = CompetenciaUtils.getCompetencia(primeiraGuia.getDataTerminoAtendimento());
		Integer tipo = LoteDeGuias.tipoDaGuia(primeiraGuia);
		LoteDeGuias lote = new LoteDeGuias(tipo, new ArrayList<GuiaSimples<ProcedimentoInterface>>(guias), prestador, competencia);
		
		Collections.sort(lote.getGuiasEnviadas(), new Comparator<GuiaSimples>(){
			@Override
			public int compare(GuiaSimples g1, GuiaSimples g2) {
				return g1.getDataTerminoAtendimento().compareTo(g2.getDataTerminoAtendimento());
			}
		});
		
		lote.validate();
		
		prestador.tocarObjetos();
		
		for (GuiaSimples<ProcedimentoInterface> guiaSimples : guias) {
			guiaSimples.getSituacoes().size();
		}

		return lote;
	}
	
	public LoteDeGuias salvarLote(LoteDeGuias lote, UsuarioInterface usuario) throws Exception{
		
		lote.mudarSituacao(usuario, SituacaoEnum.ENVIADO.descricao(), LoteDeGuias.ENVIO_PRESTADOR, new Date());
		for (GuiaSimples<ProcedimentoInterface> guia : lote.getGuiasEnviadas()) {
			guia.mudarSituacao(usuario, SituacaoEnum.ENVIADO.descricao(), MotivoEnumSR.GUIA_ENVIADA.getMessage(), new Date());
		}
		
		LoteDeGuias.salvarLote(lote);
		return lote;
	}
	
	/**
	 * Valida, para as guias selecionadas para gerar lote, se todas as parciais anteriores já foram entreges,
	 * ou seja contém uma das seguintes sittuações (ver {@link GuiaInternacao#foiEnviada()}):
	 * <ul>
	 * <li> Enviado(a) para o SAÚDE RECIFE </li>
	 * <li> Recebido(a) </li>
	 * <li> Devolvido(a) </li>
	 * <li> Auditado(a) </li>
	 * <li> Glosado(a) </li>
	 * <li> Faturado(a) </li>
	 * <li> Pago(a) </li>
	 * <li> Inapto(a) </li>
	 * </ul>
	 * @param guias
	 */
	private void validaEnvioOutrasParciais(Collection<GuiaSimples<ProcedimentoInterface>> guias){
		Set<GuiaInternacao> outrasParciais = new HashSet<GuiaInternacao>();
		boolean isMesmoTamanho;
		for (GuiaSimples<ProcedimentoInterface> guiaSimples : guias) {
			outrasParciais.addAll(((GuiaInternacao)guiaSimples).getOutrasParciais());
			
			for (GuiaInternacao outraParcial : outrasParciais) {
				isMesmoTamanho = guiaSimples.getAutorizacao().length() == outraParcial.getAutorizacao().length();
				if (isMesmoTamanho) { 
					if (guiaSimples.getAutorizacao().compareTo(outraParcial.getAutorizacao()) > 0){
						validaOutraParcial(guias, guiaSimples, outraParcial);
					}
				} else {
					if (guiaSimples.getAutorizacao().length() > outraParcial.getAutorizacao().length()){
						validaOutraParcial(guias, guiaSimples, outraParcial);
					}
				}
			}
			
			outrasParciais.clear();
		}
	}
	
	/**
	 * Verifica, comparando 2 guias, se a <strong> guiaSimples </strong> é anterior à <strong> outraParcial </strong>,
	 * lançando uma mensagem de erro caso o resultado seja positivo e a  <strong> outraParcial </strong>
	 * ainda não tiver sido enviada ou não estiver contida no mesmo lote.
	 * @param guias
	 * @param guiaSimples
	 * @param outraParcial
	 */
	private void validaOutraParcial(Collection<GuiaSimples<ProcedimentoInterface>> guias, GuiaSimples guiaSimples,GuiaInternacao outraParcial){
		boolean isPossuiSituacaoEnviado, isContidoNoMesmoLote, isCancelada;
		
		Calendar prazoEnvio = Calendar.getInstance();
		prazoEnvio.add(Calendar.DAY_OF_MONTH, -PainelDeControle.getPainel().getPrazoFinalParaEntregaDeLote());
		
		Date dataFinal = outraParcial.getDataTerminoAtendimento() != null? outraParcial.getDataTerminoAtendimento() : outraParcial.getSituacao().getDataSituacao();
		boolean isUltrapassouPrazo = Utils.compareData(dataFinal, prazoEnvio.getTime()) < 0;
		
		isPossuiSituacaoEnviado = outraParcial.foiEnviada() ;
		isContidoNoMesmoLote = guias.contains(outraParcial);
		isCancelada = outraParcial.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao());
		
		Assert.isTrue(isPossuiSituacaoEnviado || isContidoNoMesmoLote || isCancelada|| isUltrapassouPrazo, "A guia "+guiaSimples.getAutorizacao()+" não pode ser enviada antes da "+outraParcial.getAutorizacao()+".");
	}
	
	public void finalizar(LoteDeGuias lote){}
	
	private void tocarObjetos(Collection<GuiaSimples> guias) {
		for (GuiaSimples<ProcedimentoInterface> guia : guias) {
			guia.getValorCoParticipacao();
			guia.getGuiaOrigem();
			guia.isInternacao();
			guia.getSegurado().getPessoaFisica().getNome();
			guia.getSituacoes().size();
			if(guia.isInternacao()){
				((GuiaInternacao)guia).getOutrasParciais().size();
				for (GuiaInternacao guiaInternacao : ((GuiaInternacao)guia).getOutrasParciais()) {
					guiaInternacao.getSituacoes().size();
				}
			}
		}
	}

}
