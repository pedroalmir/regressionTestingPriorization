
package br.com.infowaypi.ecarebc.service.autorizacoes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.FlushMode;

import br.com.infowaypi.ecare.cadastros.MotivoGlosa;
import br.com.infowaypi.ecare.services.ProcedimentoCirurgicoLayer;
import br.com.infowaypi.ecare.services.layer.ItemDiariaLayer;
import br.com.infowaypi.ecare.services.layer.ItemPacoteLayer;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ItemGasoterapiaLayer;
import br.com.infowaypi.ecarebc.atendimentos.ItemGlosavel;
import br.com.infowaypi.ecarebc.atendimentos.ItemTaxaLayer;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.atendimentos.ValoresMatMed;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemGasoterapia;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemTaxa;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GuiaHonorarioMedico;
import br.com.infowaypi.ecarebc.atendimentos.honorario.Honorario;
import br.com.infowaypi.ecarebc.atendimentos.visitors.ItemGlosavelCompararClonarVisitor;
import br.com.infowaypi.ecarebc.atendimentos.visitors.ItemGlosavelGlosarVisitor;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.opme.ItemOpme;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoLayer;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOutros;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOutrosLayer;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.ecarebc.utils.ProcedimentoUtils;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.molecular.parameter.OrderBy;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Service para realização de auditoria manual em guias do sistema
 * @author Marcus Boolean
 * @changes Danilo Nogueira Portela
 * @changes Luciano Rocha
 */
@SuppressWarnings("rawtypes")
public class AuditarGuiaService extends Service {
	
	public AuditarGuiaService(){
		super();
	}
	
	public ResumoGuias<GuiaCompleta> buscarGuias(String autorizacao, String dataInicial, String dataFinal,Prestador prestador) throws Exception {
		List<GuiaCompleta> guias = new ArrayList<GuiaCompleta>();
		SearchAgent sa = new SearchAgent();
		
		Collection situacoes = null;
		if(prestador != null){
			if(prestador.isExigeEntregaLote())
				situacoes = Arrays.asList(SituacaoEnum.RECEBIDO.descricao(), SituacaoEnum.AUDITADO.descricao());
			else situacoes = Arrays.asList(SituacaoEnum.FECHADO.descricao(), SituacaoEnum.AUDITADO.descricao());
		}else situacoes = Arrays.asList(SituacaoEnum.FECHADO.descricao(), SituacaoEnum.RECEBIDO.descricao(), SituacaoEnum.AUDITADO.descricao());
		
		sa.addParameter(new In("situacao.descricao",situacoes));
		sa.addParameter(new OrderBy("dataTerminoAtendimento"));
		guias = super.buscarGuias(sa, autorizacao, null, dataInicial, dataFinal, prestador,  true,false, GuiaCompleta.class, GuiaSimples.DATA_DE_TERMINO);
		
		Set<GuiaCompleta> guiasExame = new HashSet<GuiaCompleta>();
		for (GuiaCompleta<ProcedimentoInterface> guia : guias) {
			if(guia.isExame()) {
				for (ItemDiaria item : guia.getItensDiaria()) {
					item.getMotivoGlosa().getDescricao();
				}
				for (ItemPacote item : guia.getItensPacote()) {
					item.getMotivoGlosa().getDescricao();
				}
				for (ItemTaxa item : guia.getItensTaxa()) {
					item.getMotivoGlosa().getDescricao();
				}
				for (ItemGasoterapia item : guia.getItensGasoterapia()) {
					item.getMotivoGlosa().getDescricao();
				}
				guiasExame.add(guia);
			}
		}
		guias.removeAll(guiasExame);
		
		ResumoGuias<GuiaCompleta> resumo = new ResumoGuias<GuiaCompleta>(guias, ResumoGuias.SITUACAO_TODAS,false);
		Assert.isNotEmpty(resumo.getGuiasParaAuditoriaGeral(), MensagemErroEnum.GUIAS_NAO_ENCONTRADAS_PARA_AUDITORIA.getMessage());

		return resumo;
	}
	
	public GuiaCompleta selecionarGuia(GuiaCompleta guia, UsuarioInterface usuario) {
		guia.tocarObjetos();
		guia.setUsuarioDoFluxo(usuario);
		guia.setValorAnterior(guia.getValorTotal());
		inicializaValoresParaAuditoriaOPMEs(guia);
		new ManagerLayersAuditoria(guia);
		guia.setValorTotalMatMedAntesDaAuditoria(guia.getValoresMatMed().getValorTotalInformado());
		return guia;
	}

	public GuiaSimples auditarGuia(Boolean glosarTotalmente, MotivoGlosa motivoGlosa, String observacaoMotivoDeGlosa, GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario) throws Exception {
		HibernateUtil.currentSession().evict(MotivoGlosa.class);
		guia.setValorAnterior(guia.getValorTotal());
		validaMatMed(guia);
		ProcedimentoUtils.aplicaDescontoDaViaDeAcesso(guia);
		for (ProcedimentoCirurgicoLayer procedimentoCirurgicoLayer : guia.getProcedimentosCirurgicosLayer()) {
			
			ProcedimentoCirurgicoInterface procedimentoCirurgico = procedimentoCirurgicoLayer.getProcedimentoCirurgico();
			
			Profissional cirurgiaoDaSessao = procedimentoCirurgicoLayer.getProfissionalResponsavelTemp();
			Profissional cirurgiaoPersistido = procedimentoCirurgico.getProfissionalResponsavel();
			
			boolean isCirurgiaoUpdated = false;
			if(cirurgiaoPersistido != null  && cirurgiaoDaSessao!= null) {
				isCirurgiaoUpdated = !cirurgiaoPersistido.equals(cirurgiaoDaSessao);
			}
			
			boolean profissionalTemGuiaDeHonorarioCriada = false;
			for (GuiaHonorarioMedico guiaHonorario : guia.getGuiasFilhasDeHonorarioMedico()) {
				if(!guiaHonorario.isGuiaHonorarioAnestesista() && !guiaHonorario.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao()) && guiaHonorario.getProfissional().equals(cirurgiaoPersistido)) {
					profissionalTemGuiaDeHonorarioCriada = true;
					break;
				}	
			}
			
			if(isCirurgiaoUpdated && profissionalTemGuiaDeHonorarioCriada) {
				throw new ValidateException("O Profissional Responsável deste procedimento não pode ser alterado, pois o mesmo já possui uma Guia de Honorário criada.");
			}
			
			if ((cirurgiaoPersistido == null) || (isCirurgiaoUpdated && !profissionalTemGuiaDeHonorarioCriada)) {
				procedimentoCirurgico.setProfissionalResponsavelTemp(cirurgiaoDaSessao);
			}
			
			if(procedimentoCirurgico.getProfissionalResponsavelTemp() != null) {
				procedimentoCirurgico.setProfissionalResponsavel(procedimentoCirurgico.getProfissionalResponsavelTemp());
			}	
			
			HibernateUtil.currentSession().evict(cirurgiaoPersistido);
			
			if(procedimentoCirurgico.getIdProcedimento() == null){
				if(procedimentoCirurgico.getProfissionalAuxiliar1() != null) {
					guia.setValorTotal(guia.getValorTotal().add(procedimentoCirurgico.getValorAuxiliar1()));
				}
				if(procedimentoCirurgico.getProfissionalAuxiliar2() != null) {
					guia.setValorTotal(guia.getValorTotal().add(procedimentoCirurgico.getValorAuxiliar2()));
				}
				if(procedimentoCirurgico.getProfissionalAuxiliar3() != null) {
					guia.setValorTotal(guia.getValorTotal().add(procedimentoCirurgico.getValorAuxiliar3()));
				}
			}
			if (procedimentoCirurgico.isGlosar()) {
				procedimentoCirurgico.glosar(usuario);
			}
			
			if (procedimentoCirurgico.isAnular() && !procedimentoCirurgico.isSituacaoAtual(SituacaoEnum.PROCEDIMENTO_COM_HONORARIO_GERADO.descricao())) {
				procedimentoCirurgico.anular(usuario);
			}
			
			if (!procedimentoCirurgico.isAnular() && !procedimentoCirurgico.isSituacaoAtual(SituacaoEnum.PROCEDIMENTO_COM_HONORARIO_GERADO.descricao())) {
				procedimentoCirurgico.desfazerAnular(usuario);
			}
		}
		
		Procedimento procedimentoDeConsulta = (Procedimento) guia.getProcedimentoConsultaNaoCancelado();
		if(procedimentoDeConsulta != null){
			if (procedimentoDeConsulta.isGlosar()) {
				Assert.isNotNull(procedimentoDeConsulta.getMotivoGlosaProcedimento(), MensagemErroEnum.PROCEDIMENTO_GLOSADO_SEM_MOTIVO_DE_GLOSA.getMessage(procedimentoDeConsulta.getProcedimentoDaTabelaCBHPM().getCodigoEDescricao()));
				procedimentoDeConsulta.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), procedimentoDeConsulta.getMotivoGlosaProcedimento().getDescricao(), new Date());
			}
			
			if (procedimentoDeConsulta.isDesfazerGlosa()) {
				Assert.isNotNull(procedimentoDeConsulta.getMotivoGlosaProcedimento(), MensagemErroEnum.PROCEDIMENTO_GLOSA_DESFEITA_SEM_MOTIVO_DE_GLOSA.getMessage(procedimentoDeConsulta.getProcedimentoDaTabelaCBHPM().getCodigoEDescricao()));
				procedimentoDeConsulta.mudarSituacao(usuario, procedimentoDeConsulta.getSituacaoAnterior(procedimentoDeConsulta.getSituacao()).getDescricao(), procedimentoDeConsulta.getMotivoGlosaProcedimento().getDescricao(), new Date());
			}
		}
		
		/** Audita os ItemGasoterapias contidos na guia **/
		auditarGasoterapias(guia, usuario);
		
		/** Audita os ItemTaxa contidos na guia **/
		auditarTaxas(guia, usuario);
		
		//adiciona as novas gasoterapias inseridas pelo auditor
		adicionaNovasGasoterapias(guia, usuario);
		
		//adiciona as novas taxas inseridas pelo auditor
		adicionaNovasTaxas(guia, usuario);
		
		/** Audita os ItemPacote contidos na guia **/
		auditarPacotes(guia, usuario);
		
		/** Verifica se houve alteração ou glosa de algum item de diaria. */		
		auditarDiarias(guia, usuario);

		//adiciona as novas diárias inseridas pelo auditor
		adicionaNovasDiarias(guia, usuario);
		
		//adiciona os novos pacotes inseridos pelo auditor
		adicionaNovosPacotes(guia, usuario);
		
		/** Audita os ProcedimentoCirurgicos contidos na guia **/
		auditarProcedimentoCirurgicos(guia, usuario);
		
		/** Audita os ItemExame contidos na guia **/
		auditarProcedimentosDeExames(guia, usuario);
		
		/** Audita os ProcedimentosOutros contidos na guia **/
		auditarProcedimentosOutros(guia, usuario);
		
		/** Audita os OPMEs autorizados na regulação**/
		auditarOpmes(guia, usuario);
		
		//Deveria estar no recalcular?
		guia.setValorTotal(guia.getValorTotal().subtract(guia.getValoresMatMed().getValorTotalInformado()));
		guia.setValorTotal(guia.getValorTotal().add(guia.getValoresMatMed().getValorTotalAuditado()));
		
		for (ItemPacote itemPacote : guia.getItensPacoteSolicitados()) {
			itemPacote.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(),MotivoEnum.AUTORIZADO_PELO_AUDITOR.getMessage(usuario.getRole()), new Date());
		}
		guia.corrigeValorProcedimentoInclusoEmPacoteQueInclueHonorario(guia.getProcedimentosNaoCanceladosENegados());
		
		if(glosarTotalmente) {
			if (motivoGlosa == null) {
				throw new ValidateException("Informe o motivo de glosa para a guia.");
			} 
			if(!Utils.isStringVazia(observacaoMotivoDeGlosa)) {
				guia.setMotivoParaGlosaTotal(observacaoMotivoDeGlosa);
			}
			guia.setMotivoGlosa(motivoGlosa);
			guia.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), MotivoEnum.GUIA_GLOSADA_DURANTE_AUDITORIA.getMessage(), new Date());
			return guia;
		}
		
		atualizaValorInformadoMatMed(guia);
		
		guia.mudarSituacao(usuario, SituacaoEnum.AUDITADO.descricao() , MotivoEnum.GUIA_AUDITADA.getMessage(), new Date());
		
		autorizarItensDaGuiaAuditada(guia, usuario);
		
		if(guia.getDataRecebimento() == null){
			guia.setDataRecebimento(new Date());
		}
		
		//Ajustando as datas de inicio e fim das diarias autorizadas.
		corrigirDatasDasDiarias(guia);
		
		return guia;
	}

	private void corrigirDatasDasDiarias(GuiaCompleta<ProcedimentoInterface> guia) {
		if(guia.isInternacao()){
			//Retirando data inicial das diaruas não autorizadas 
			Set<ItemDiaria> itensDiariaNaoContabilizavel = guia.getItensDiaria("Não Autorizado(a)","Cancelado(a)","Glosado(a)");
			for(ItemDiaria itemDiaria : itensDiariaNaoContabilizavel){
				itemDiaria.setDataInicial(null);	
			}
			
			//reorganizando as datas
			List<ItemDiaria> itensDiariaAutorizados = new ArrayList<ItemDiaria>(guia.getItensDiariaAutorizados());
			Utils.sort(itensDiariaAutorizados, "situacao.dataSituacao");
			int posicao = 0;
			for(ItemDiaria itemDiaria : itensDiariaAutorizados){
				if(posicao == 0){
					Calendar dataInicialPrimeiraDiaria = Calendar.getInstance();
					dataInicialPrimeiraDiaria.setTime(guia.getDataAtendimento());
					dataInicialPrimeiraDiaria.set(Calendar.HOUR_OF_DAY, 12);
					dataInicialPrimeiraDiaria.set(Calendar.MINUTE, 1);
					dataInicialPrimeiraDiaria.set(Calendar.SECOND, 0);
					itemDiaria.setDataInicial(dataInicialPrimeiraDiaria.getTime());
				}else{
					Calendar dataInicialProximaDiaria = Calendar.getInstance();
					dataInicialProximaDiaria.setTime(itensDiariaAutorizados.get(posicao - 1).getDataFinal());
					itemDiaria.setDataInicial(dataInicialProximaDiaria.getTime());
				}
				posicao ++;
			}
		}
	}
	
	/**
	 * Atualiza o valor total apresentado da guia pelo prestador.
	 * REGRA: Subtrai o valor informado de matmed antes da auditoria do valor apresentado da guia. 
	 *        Depois, adiciona o novo valor informado de matmed. 
	 * @author Luciano Infoway
	 * @since 03/07/2013
	 * @param guia
	 */
	private void atualizaValorInformadoMatMed(GuiaCompleta<ProcedimentoInterface> guia) {
		guia.atualizaValorApresentado(guia.getValorTotalMatMedAntesDaAuditoria().negate());
		guia.atualizaValorApresentado(guia.getValoresMatMed().getValorTotalInformado());
	}

	/**
	 * Método que inicializa os valores auditados para OPMEs.
	 * É apenas para usabilidade, dado que a maioria dos OPMEs terão
	 * o quantidade auditada igual à quantidade autorizada.
	 * @author Luciano Infoway
	 * @since 19/04/2013
	 * @param guia
	 */
	public void inicializaValoresParaAuditoriaOPMEs(GuiaCompleta guia){
		if (guia.isSituacaoAtual(SituacaoEnum.RECEBIDO.descricao())) {
			for (Object item : guia.getOpmesAutorizados()) {
				((ItemOpme) item).setQuantidadeAuditada(((((ItemOpme)item).getQuantidadeRegulada())));
			}
		}
	}

	/**
	 * Método responsável pela auditoria de OPMEs.
	 * @author Luciano Infoway
	 * @since 19/04/2013
	 * @param guia
	 * @param usuario
	 */
	private void auditarOpmes(GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario) {
		 if (guia.getItensOpme() != null) {
		    for (ItemOpme item : guia.getItensOpme()) {
			    Boolean glosar = item.getGlosar();
			    if (glosar != null && glosar) {
				    Assert.isNotNull(item.getObservacaoAuditoria(), MensagemErroEnum.ITEM_GLOSADO_SEM_MOTIVO_DE_GLOSA.getMessage(item.getOpme().getDescricao()));
				    item.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), item.getObservacaoAuditoria(), new Date());
				    item.setQuantidadeAuditada(0);
			    } else {
				    if (item.getQuantidadeAuditada() != item.getQuantidadeRegulada()) {
					   Assert.isNotNull(item.getObservacaoAuditoria(), MensagemErroEnum.ITEM_GLOSADO_SEM_MOTIVO_DE_GLOSA.getMessage(item.getOpme().getDescricao()));
			        }
			    }
			    item.setAuditado(Boolean.TRUE);
		    }
	    }
	}
	
	private void validaMatMed(GuiaCompleta<ProcedimentoInterface> guia) {
		String message = "É necessário justificar as alterações nos Materiais/Medicamentos.";
		ValoresMatMed valores = guia.getValoresMatMed();
		if (valores.getValorTotalMateriaisMedicosApartamento().compareTo(valores.getValorTotalMateriaisMedicosApartamentoAuditado()) != 0) {
			Assert.isNotNull(valores.getJustificativaMateriaisMedicosApartamento(), message);
		}
		if (valores.getValorTotalMateriaisMedicosUTI().compareTo(valores.getValorTotalMateriaisMedicosUTIAuditado()) != 0) {
			Assert.isNotNull(valores.getJustificativaMateriaisMedicosUTI(), message);
		}
		if (valores.getValorTotalMateriaisMedicosBlocoCirurgico().compareTo(valores.getValorTotalMateriaisMedicosBlocoCirurgicoAuditado()) != 0) {
			Assert.isNotNull(valores.getJustificativaMateriaisMedicosBlocoCirurgico(), message);
		}
		if (valores.getValorTotalMateriaisProntoSocorro().compareTo(valores.getValorTotalMateriaisProntoSocorroAuditado()) != 0) {
			Assert.isNotNull(valores.getJustificativaMateriaisProntoSocorro(), message);
		}
		if (valores.getValorTotalMedicamentosApartamento().compareTo(valores.getValorTotalMedicamentosApartamentoAuditado()) != 0) {
			Assert.isNotNull(valores.getJustificativaMedicamentosApartamento(), message);
		}
		if (valores.getValorTotalMedicamentosUTI().compareTo(valores.getValorTotalMedicamentosUTIAuditado()) != 0) {
			Assert.isNotNull(valores.getJustificativaMedicamentosUTI(), message);
		}
		if (valores.getValorTotalMedicamentosBlocoCirurgico().compareTo(valores.getValorTotalMedicamentosBlocoCirurgicoAuditado()) != 0) {
			Assert.isNotNull(valores.getJustificativaMedicamentosBlocoCirurgico(), message);
		}
		if (valores.getValorTotalMedicamentosProntoSocorro().compareTo(valores.getValorTotalMedicamentosProntoSocorroAuditado()) != 0) {
			Assert.isNotNull(valores.getJustificativaMedicamentosProntoSocorro(), message);
		}
		if (valores.getValorTotalMateriaisEspeciais().compareTo(valores.getValorTotalMateriaisEspeciaisAuditado()) != 0) {
			Assert.isNotNull(valores.getJustificativaMateriaisEspeciais(), message);
		}
		if (valores.getValorTotalMedicamentosEspeciais().compareTo(valores.getValorTotalMedicamentosEspeciaisAuditado()) != 0) {
			Assert.isNotNull(valores.getJustificativaMedicamentosEspeciais(), message);
		}
		if (valores.getValorOutros().compareTo(valores.getValorOutrosAuditado()) != 0) {
			Assert.isNotNull(valores.getJustificativaOutros(), message);
		}

	}

	public void salvarGuia(GuiaSimples guia) throws Exception{
		String situacaoAnterior = guia.getSituacaoAnterior(guia.getSituacao()).getDescricao();
		String situacaoAtual = guia.getSituacao().getDescricao();
		
		Boolean updateQuantidades = situacaoAnterior.equals(SituacaoEnum.RECEBIDO.descricao()) || situacaoAtual.equals(SituacaoEnum.GLOSADO.descricao());
		
		//Salvando a guia e atualizando índices se for a primeira auditoria
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
		HibernateUtil.currentSession().flush();
		HibernateUtil.currentSession().clear();
		super.salvarGuia(guia, updateQuantidades, true);
	}
	
	@SuppressWarnings("unused")
	private void aplicaDescontoDaViaDeAcesso(GuiaCompleta<ProcedimentoInterface> guia) {
		BigDecimal diferencaProcedimentos = BigDecimal.ZERO;
		for (ProcedimentoCirurgicoInterface p : guia.getProcedimentosCirurgicos()) {
			if(p.getIdProcedimento() == null){
				if(p.getPorcentagem() == null)
					p.setPorcentagem(new BigDecimal(100));
				
				BigDecimal valorTotal = p.getValorAtualDoProcedimento();
				BigDecimal porcentagem = p.getPorcentagem().divide(new BigDecimal(100));
				BigDecimal valorAtualizado = p.getValorTotal().multiply(porcentagem);
				p.setValorAtualDoProcedimento(valorAtualizado);
				
				diferencaProcedimentos = diferencaProcedimentos.add(valorTotal.subtract(valorAtualizado));
			}
		}
		guia.setValorTotal(guia.getValorTotal().subtract(diferencaProcedimentos));
	}
	
	/**
	 * Esse método atualiza para situação Autorizado(o) os itens que passaram na auditoria, não foram glosados, mas ainda
	 * estavam na situação Solicitado(a).
	 * @author Luciano Rocha
	 * @since 24/01/2013
	 * @param guia
	 * @param usuario
	 */
	private void autorizarItensDaGuiaAuditada(GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario) {
		
		for (ItemTaxa taxa : guia.getItensTaxa()) {
			if (taxa.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())) {
				taxa.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZADO_PELO_AUDITOR.getMessage(), new Date());
			}
		}
		for (ItemGasoterapia gasoterapia : guia.getItensGasoterapia()) {
			if (gasoterapia.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())) {
				gasoterapia.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZADO_PELO_AUDITOR.getMessage(), new Date());
			}
		}
		for (ItemDiaria diaria : guia.getItensDiaria()) {
			if (diaria.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())) {
				diaria.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZADO_PELO_AUDITOR.getMessage(), new Date());
			}
		}
		for (ItemPacote pacote : guia.getItensPacote()) {
			if (pacote.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())) {
				pacote.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZADO_PELO_AUDITOR.getMessage(), new Date());
			}
		}
		for (ProcedimentoCirurgico procCirurgico : guia.getProcedimentosCirurgicos()) {
			if (!procCirurgico.isAnular() && procCirurgico.getProfissionalResponsavel() == null) {
				procCirurgico.mudarSituacao(usuario, SituacaoEnum.AGUARDANDO_COBRANCA.descricao(), "Situação gerada automaticamente durante a auditoria", new Date());
			}
		}
	}
	
	/**
	 * Método responsável pela adição de novas gasoterapias à guia.
	 * @author Luciano Rocha
	 * @param guia
	 */
	public void adicionaNovasGasoterapias(GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario){
		if (guia.getItensGasoterapiaTemp()!=null) {			
			Set<ItemGasoterapia> itensGasoterapiaTemp = new HashSet<ItemGasoterapia>(guia.getItensGasoterapiaTemp());
			
			if (itensGasoterapiaTemp != null) {
				Set<ItemGasoterapia> itens = guia.getItensGasoterapia();
				for (ItemGasoterapia itemGasoterapia : itensGasoterapiaTemp) {
					itemGasoterapia.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZADO_PELO_AUDITOR.getMessage(), new Date());
					itens.add(itemGasoterapia);
					guia.atualizaValorApresentado(itemGasoterapia.getValorItem());
				}
			}
		}
	}
	
	/**
	 * Método responsável pela adição de novas diárias à guia.
	 * @author Luciano Rocha
	 * @param guia
	 */
	public void adicionaNovasDiarias(GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario){
		if (guia.getItensDiariaTemp()!=null) {
			Set<ItemDiaria> itensDiariaTemp = new HashSet<ItemDiaria>(guia.getItensDiariaTemp());
			
			if (itensDiariaTemp != null) {
				Set<ItemDiaria> itens = guia.getItensDiaria();
				for (ItemDiaria itemDiaria : itensDiariaTemp) {
					itemDiaria.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZADO_PELO_AUDITOR.getMessage(), new Date());
					itens.add(itemDiaria);
					guia.atualizaValorApresentado(itemDiaria.getValorItem());
				}
			}
		}
	}
	
	/**
	 * Método responsável pela adição de novos pacotes à guia.
	 * @author Luciano Rocha
	 * @param guia
	 */
	public void adicionaNovosPacotes(GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario){
		if (guia.getItensPacoteTemp()!=null) {
			Set<ItemPacote> itensPacoteTemp = new HashSet<ItemPacote>(guia.getItensPacoteTemp());
			
			if (itensPacoteTemp != null) {
				Set<ItemPacote> itens = guia.getItensPacote();
				for (ItemPacote itemPacote : itensPacoteTemp) {
					itemPacote.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZADO_PELO_AUDITOR.getMessage(), new Date());
					itens.add(itemPacote);
					guia.atualizaValorApresentado(itemPacote.getValorItem());
				}
			}
		}
	}
	
	/**
	 * Método responsável pela adição de novas taxas à guia.
	 * @author Luciano Rocha
	 * @param guia
	 */
	public void adicionaNovasTaxas(GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario){
		if (guia.getItensTaxaTemp()!=null) {
			Set<ItemTaxa> itensTaxaTemp = new HashSet<ItemTaxa>(guia.getItensTaxaTemp());
			
			if (itensTaxaTemp != null) {
				Set<ItemTaxa> itens = guia.getItensTaxa();
				for (ItemTaxa itemTaxa : itensTaxaTemp) {
					itemTaxa.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZADO_PELO_AUDITOR.getMessage(), new Date());
					itens.add(itemTaxa);
					guia.atualizaValorApresentado(itemTaxa.getValorItem());
				}
			}
		}
	}
	
	/**
	 * Método responsável por auditar os itensGasoterapia da Guia.
	 * @author Luciano Rocha
	 * @param usuario
	 * @param guia
	 */
	public void auditarGasoterapias(GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario){
		for (ItemGasoterapiaLayer layer : guia.getItensGasoterapiaLayer()) {
			boolean glosar = layer.isGlosar();
			
			if (!glosar) {
				ItemGlosavel item = layer.getItemGasoterapia().accept(new ItemGlosavelCompararClonarVisitor(layer));
				
				if (item != null) {
					if (layer.getMotivoGlosa() != null) {
						layer.getItemGasoterapia().setMotivoGlosa(layer.getMotivoGlosa());
					}
					layer.getItemGasoterapia().accept(new ItemGlosavelGlosarVisitor(usuario));
					((ItemGasoterapia)item).setMotivoGlosa(layer.getMotivoGlosa());
					guia.getItensGasoterapia().add((ItemGasoterapia) item);
				}
			}
			else {
				layer.getItemGasoterapia().setMotivoGlosa(layer.getMotivoGlosa());
				layer.getItemGasoterapia().setJustificativaGlosa(layer.getJustificativaGlosa());
				layer.getItemGasoterapia().accept(new ItemGlosavelGlosarVisitor(usuario));
			}
		}
	}
	
	/**
	 * Método responsável por auditar os itensTaxa da Guia.
	 * @author Luciano Rocha
	 * @param usuario
	 * @param guia
	 */
	public void auditarTaxas(GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario){
		for (ItemTaxaLayer layer : guia.getItensTaxaLayer()) {
			boolean glosar = layer.isGlosar();
			if (!glosar) {
				ItemGlosavel item = layer.getItemTaxa().accept(new ItemGlosavelCompararClonarVisitor(layer));
				
				if (item != null) {
					if (layer.getMotivoGlosa() != null) {
						layer.getItemTaxa().setMotivoGlosa(layer.getMotivoGlosa());
					}
					layer.getItemTaxa().accept(new ItemGlosavelGlosarVisitor(usuario));
					((ItemTaxa)item).setMotivoGlosa(layer.getMotivoGlosa());
					guia.getItensTaxa().add((ItemTaxa) item); 
				}
			} else {
				layer.getItemTaxa().setMotivoGlosa(layer.getMotivoGlosa());
				layer.getItemTaxa().setJustificativaGlosa(layer.getJustificativaGlosa());
				layer.getItemTaxa().accept(new ItemGlosavelGlosarVisitor(usuario));
			}
		}
	}

	public void auditarPacotes(GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario) {
		for (ItemPacoteLayer layer : guia.getItensPacoteAuditoriaLayer()) {
			boolean glosar = layer.isGlosar();
			
			if (!glosar) {
				ItemPacote novoItem = (ItemPacote) layer.getItemPacote().accept(new ItemGlosavelCompararClonarVisitor(layer));
				
				if (novoItem != null) {
					layer.getItemPacote().setMotivoGlosa(layer.getMotivoGlosa());
					layer.getItemPacote().accept(new ItemGlosavelGlosarVisitor(usuario));
					novoItem.setMotivoGlosa(layer.getMotivoGlosa());
					guia.getItensPacote().add(novoItem);
				}
			} else {
				layer.getItemPacote().setMotivoGlosa(layer.getMotivoGlosa());
				layer.getItemPacote().setJustificativaGlosa(layer.getJustificativaGlosa());
				layer.getItemPacote().accept(new ItemGlosavelGlosarVisitor(usuario));
			}
		}
	}
	
	public void auditarDiarias(GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario) {
		for (ItemDiariaLayer layer : guia.getItensDiariaAuditoriaLayer()) {
			boolean glosar = layer.isGlosar();
			
			if (!glosar) {
				ItemDiaria novoItem = (ItemDiaria) layer.getItemDiaria().accept(new ItemGlosavelCompararClonarVisitor(layer));
				
				if (novoItem != null) {
					layer.getItemDiaria().setMotivoGlosa(layer.getMotivoGlosa());
					layer.getItemDiaria().accept(new ItemGlosavelGlosarVisitor(usuario));
					novoItem.setMotivoGlosa(layer.getMotivoGlosa());
					guia.getItensDiaria().add(novoItem);
				}
			} else {
				layer.getItemDiaria().setMotivoGlosa(layer.getMotivoGlosa());
				layer.getItemDiaria().setJustificativaGlosa(layer.getJustificativaGlosa());
				layer.getItemDiaria().accept(new ItemGlosavelGlosarVisitor(usuario));
			}
		}
	}
		
	public void auditarProcedimentoCirurgicos(GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario) throws ValidateException {
		for (ProcedimentoCirurgicoLayer layer : guia.getProcedimentosCirurgicosLayer()) {
			boolean glosar = layer.isGlosar();
			ProcedimentoInterface procCirurgico;
			if (!glosar) {
				ItemGlosavel item = layer.getProcedimentoCirurgico().accept(new ItemGlosavelCompararClonarVisitor(layer));
				if (item != null) {
					procCirurgico = (ProcedimentoInterface) item;
					if (layer.getMotivoGlosa() != null) {
						layer.getProcedimentoCirurgico().setMotivoGlosaProcedimento(layer.getMotivoGlosa());
					} else {
						layer.getProcedimentoCirurgico().setMotivoGlosaProcedimento(null);
					}
					layer.getProcedimentoCirurgico().accept(new ItemGlosavelGlosarVisitor(usuario));
					guia.getProcedimentos().add(procCirurgico);
					auditarHonorariosInternos(guia, usuario, layer, procCirurgico);
				} else {
					auditarHonorariosInternos(guia, usuario, layer, layer.getProcedimentoCirurgico());
				}
			} else {
				if (layer.getMotivoGlosa() != null) {
					layer.getProcedimentoCirurgico().setMotivoGlosaProcedimento(layer.getMotivoGlosa());
				} else {
					layer.getProcedimentoCirurgico().setMotivoGlosaProcedimento(null);
				}
				layer.getProcedimentoCirurgico().accept(new ItemGlosavelGlosarVisitor(usuario));
			}
		}
	}
	
	public void auditarHonorariosInternos(GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario, ProcedimentoCirurgicoLayer layer, ProcedimentoInterface procCirurgico) throws ValidateException {
		for (Honorario honorario : guia.getHonorariosInternosNaoGlosado()) {
			verificarGlosarHonorarioInterno(layer.isGlosarAuxiliar1(), layer.getMotivoGlosaAuxiliar1(), layer.getProfissionalAuxiliar1(), 
					honorario.getProfissional(), procCirurgico.getProfissionalAuxiliar1(), honorario, usuario);
			verificarGlosarHonorarioInterno(layer.isGlosarAuxiliar2(), layer.getMotivoGlosaAuxiliar2(), layer.getProfissionalAuxiliar2(), 
					honorario.getProfissional(), procCirurgico.getProfissionalAuxiliar2(), honorario, usuario);
			verificarGlosarHonorarioInterno(layer.isGlosarAuxiliar3(), layer.getMotivoGlosaAuxiliar3(), layer.getProfissionalAuxiliar3(), 
					honorario.getProfissional(), procCirurgico.getProfissionalAuxiliar3(), honorario, usuario);
		}
		if (procCirurgico.getProfissionalAuxiliar1()==null && layer.getProfissionalAuxiliar1()!=null) {
			procCirurgico.setProfissionalAuxiliar1(layer.getProfissionalAuxiliar1());
		}
		if (procCirurgico.getProfissionalAuxiliar2()==null && layer.getProfissionalAuxiliar2()!=null) {
			procCirurgico.setProfissionalAuxiliar2(layer.getProfissionalAuxiliar2());
		}
		if (procCirurgico.getProfissionalAuxiliar3()==null && layer.getProfissionalAuxiliar3()!=null) {
			procCirurgico.setProfissionalAuxiliar3(layer.getProfissionalAuxiliar3());
		}
	}
	
	public void verificarGlosarHonorarioInterno(boolean glosarAuxiliar, String motivo, Profissional profissionalAuxiliarLayer, Profissional profissionalHonorario, Profissional profissionalAuxiliarProcedimento, Honorario honorario, UsuarioInterface usuario) throws ValidateException {
		if (glosarAuxiliar) {
			if (profissionalHonorario.equals(profissionalAuxiliarProcedimento)) {
				if (motivo==null)
					throw new ValidateException("O motivo de glosa para o primeiro auxiliar "+profissionalHonorario.getNome()+" deve ser preenchido.");
				honorario.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), motivo, new Date());
			}
		} else {
			if (honorario.getProfissional().equals(profissionalAuxiliarProcedimento)) {
				if (!profissionalAuxiliarLayer.equals(profissionalAuxiliarProcedimento)) {
					honorario.setProfissional(profissionalAuxiliarLayer);
				}
			}
		}
	}
	
	/**
	 * Método responsável por auditar os procedimentos de Exames
	 * @author Silvano
	 */
	public void auditarProcedimentosDeExames(GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario) throws Exception {
		for (ProcedimentoLayer layer : guia.getProcedimentoLayer()) {
			boolean glosar = layer.isGlosar();
			
			if (!glosar) {
				Procedimento novoProcedimento = (Procedimento) layer.getProcedimento().accept(new ItemGlosavelCompararClonarVisitor(layer));
				
				if (novoProcedimento != null) {
					if (layer.getMotivoGlosa() != null) {
						layer.getProcedimento().setMotivoGlosaProcedimento(layer.getMotivoGlosa());
					} else {
						layer.getProcedimento().setMotivoGlosaProcedimento(null);
					}
					layer.getProcedimento().accept(new ItemGlosavelGlosarVisitor(usuario));
					novoProcedimento.validate(usuario);
					
					guia.getProcedimentos().add(novoProcedimento);
				}
			} else {
				if (layer.getMotivoGlosa() != null) {
					layer.getProcedimento().setMotivoGlosaProcedimento(layer.getMotivoGlosa());
				} else {
					layer.getProcedimento().setMotivoGlosaProcedimento(null);
				}
				layer.getProcedimento().accept(new ItemGlosavelGlosarVisitor(usuario));
			}
		}
	}
	
	/**
	 * Método responsável por auditar os procedimentos Outros
	 * @author Silvano
	 */
	public void auditarProcedimentosOutros(GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario) throws Exception {
		for (ProcedimentoOutrosLayer layer : guia.getProcedimentoOutrosLayer()) {
			boolean glosar = layer.isGlosar();
			
			if (!glosar) {
				ItemGlosavel novoProcedimento = layer.getProcedimentoOutros().accept(new ItemGlosavelCompararClonarVisitor(layer));
				
				if (novoProcedimento != null) {
					if (layer.getMotivoGlosa() != null) {
						layer.getProcedimentoOutros().setMotivoGlosaProcedimento(layer.getMotivoGlosa());
					} else {
						layer.getProcedimentoOutros().setMotivoGlosaProcedimento(null);
					}
					layer.getProcedimentoOutros().accept(new ItemGlosavelGlosarVisitor(usuario));
					((ProcedimentoOutros) novoProcedimento).validate(usuario);
					
					guia.getProcedimentos().add((ProcedimentoOutros) novoProcedimento);
				}
			} else {
				if (layer.getMotivoGlosa() != null) {
					layer.getProcedimentoOutros().setMotivoGlosaProcedimento(layer.getMotivoGlosa());
				} else {
					layer.getProcedimentoOutros().setMotivoGlosaProcedimento(null);
				}
				layer.getProcedimentoOutros().accept(new ItemGlosavelGlosarVisitor(usuario));
			}
		}
	}
}
