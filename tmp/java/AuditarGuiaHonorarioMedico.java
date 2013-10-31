package br.com.infowaypi.ecare.services.honorariomedico;

import static br.com.infowaypi.msr.utils.Assert.isNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import br.com.infowaypi.ecare.resumos.ResumoGuiasHonorarioMedico;
import br.com.infowaypi.ecare.services.honorariomedico.adapter.AdapterHonorario;
import br.com.infowaypi.ecare.services.honorariomedico.adapter.AdapterProcedimento;
import br.com.infowaypi.ecare.services.honorarios.validators.GuiaHonorarioHonorarioDuplicado;
import br.com.infowaypi.ecare.services.honorarios.validators.GuiaHonorarioPacoteDeHonorarioCompativelComAGuiaValidator;
import br.com.infowaypi.ecare.services.honorarios.validators.GuiaHonorarioProfissionalCredenciadoValidator;
import br.com.infowaypi.ecare.services.honorarios.validators.GuiaHonorarioProfissionalResponsavelDiferenteValidator;
import br.com.infowaypi.ecare.services.honorarios.validators.RegistrarHonorarioIndividualCommandRunner;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ItemGlosavel;
import br.com.infowaypi.ecarebc.atendimentos.LoteDeGuias;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacoteHonorario;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacoteHonorarioAuditoria;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GeradorGuiaHonorarioInterface;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GuiaHonorarioMedico;
import br.com.infowaypi.ecarebc.atendimentos.honorario.Honorario;
import br.com.infowaypi.ecarebc.atendimentos.honorario.HonorarioExterno;
import br.com.infowaypi.ecarebc.atendimentos.validators.EnumFlowValidator;
import br.com.infowaypi.ecarebc.atendimentos.visitors.ItemGlosavelCompararClonarVisitor;
import br.com.infowaypi.ecarebc.atendimentos.visitors.ItemGlosavelGlosarVisitor;
import br.com.infowaypi.ecarebc.enums.GrauDeParticipacaoEnum;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoHonorario;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoHonorarioLayer;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.ecarebc.service.autorizacoes.ManagerLayersAuditoria;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.molecular.parameter.OrderBy;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/**
 * @author Junior, Eduardo
 * @changes Emanuel
 *
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class AuditarGuiaHonorarioMedico extends Service{
	
	
	private final List<String> situacoes = Arrays.asList(SituacaoEnum.ABERTO.descricao(), SituacaoEnum.AUDITADO.descricao(), 
													SituacaoEnum.CONFIRMADO.descricao(), SituacaoEnum.SOLICITADO_PRORROGACAO.descricao(), 
													SituacaoEnum.PRORROGADO.descricao(), SituacaoEnum.NAO_PRORROGADO.descricao(), 
													SituacaoEnum.ALTA_REGISTRADA.descricao(), SituacaoEnum.FECHADO.descricao(), 
													SituacaoEnum.ENVIADO.descricao(), SituacaoEnum.DEVOLVIDO.descricao(), 
													SituacaoEnum.RECEBIDO.descricao(),  SituacaoEnum.FATURADA.descricao(), 
													SituacaoEnum.PAGO.descricao(), SituacaoEnum.AUTORIZADO.descricao());
	
	/**
	 * Step 1
	 * @param autorizacao
	 * @param dataInicial
	 * @param dataFinal
	 * @param prestador
	 * @param prioridade
	 * @return
	 * @throws Exception
	 */
	public ResumoGuias<GuiaCompleta> buscarGuias(String autorizacao, String dataInicial, String dataFinal, Prestador prestador, Integer prioridade) throws Exception {
		
		List<GuiaCompleta> guias 	= new ArrayList<GuiaCompleta>();
		SearchAgent sa 				= new SearchAgent();

		if(prestador != null) {
			sa.addParameter(new Equals("prestador",prestador));
		}
		
		sa.addParameter(new In("situacao.descricao", situacoes));
		sa.addParameter(new OrderBy("dataTerminoAtendimento"));
		
		guias = super.buscarGuias(sa, autorizacao, null, dataInicial, dataFinal, prestador, true, false, GuiaCompleta.class, GuiaSimples.DATA_DE_SITUACAO);
		guias = this.removeGuiasSemGuiasFilhaDeHonorario(guias);
		this.removeGuiasPrioridadeDiferente(prioridade, guias);
		
		ResumoGuias<GuiaCompleta> resumo = new ResumoGuias<GuiaCompleta>(guias, ResumoGuias.SITUACAO_TODAS,false);
		Assert.isNotEmpty(resumo.getGuias(), MensagemErroEnum.GUIAS_NAO_ENCONTRADAS_PARA_AUDITORIA.getMessage());
		resumo.ordenarGuias("prioridadeEmAuditoria", "situacao.dataSituacao");
		return resumo;
	}

	private void removeGuiasPrioridadeDiferente(Integer prioridade, List<GuiaCompleta> guias) {
		if (prioridade != null) {
			Iterator<GuiaCompleta> iterator = guias.iterator();
			while (iterator.hasNext()) {
				GuiaCompleta guia = iterator.next();
				int prioridadeEmAuditoria = ((GeradorGuiaHonorarioInterface) guia).getPrioridadeEmAuditoria();
				if (prioridadeEmAuditoria != prioridade) {
					iterator.remove();
				}
			}
		}
	}

	/**
	 * Método responsável por remover do resumo as guias de internação que não possuem guias de honorários aptas para auditoria.
	 * @param guias
	 * @return
	 */
	private List<GuiaCompleta> removeGuiasSemGuiasFilhaDeHonorario(List<GuiaCompleta> guias) {
		for (Iterator<GuiaCompleta> it = guias.iterator(); it.hasNext();) {
			GuiaCompleta guia = it.next();
			
			if (guia.getGuiasFilhasDeHonorarioMedicoAptasPraAuditoria().isEmpty() && !guia.isExame()){
				it.remove();
			}
		}
		
		return guias;
	}
	
	/**
	 * Step 2
	 * @param auditarProcedimentosCirurgicos
	 * @param auditarProcedimentosClinicos
	 * @param auditarPacotes
	 * @param guia
	 * @param usuario
	 * @return
	 * @throws Exception
	 */
	public ResumoGuiasHonorarioMedico selecionarGuia(Boolean auditarProcedimentosCirurgicos, Boolean auditarProcedimentosGrauAnestesista, Boolean auditarProcedimentosClinicos, Boolean auditarPacotes, GuiaCompleta guia, UsuarioInterface usuario) throws Exception {
		guia.tocarObjetos();
		guia.getPrestador().getAcordosCBHPM().size();
		
		ResumoGuiasHonorarioMedico resumoGuias = new ResumoGuiasHonorarioMedico(guia, usuario);
		
		boolean hasProcedimentosCirurgicos 			= !resumoGuias.getProcedimentos().isEmpty();
		boolean hasProcedimentosClinicos 			= !resumoGuias.getProcedimentosVisitaAtuais().isEmpty();
		boolean hasHonorariosPacotes 				= !guia.getHonorariosPacote().isEmpty();
		boolean hasProcedimentosCirurgicoAnestesico	= !resumoGuias.getProcedimentosCirurgicosAnestesicos().isEmpty();


		resumoGuias.setAuditarProcedimentosCirurgicos(auditarProcedimentosCirurgicos);
		resumoGuias.setAuditarProcedimentosClinicos(auditarProcedimentosClinicos);
		resumoGuias.setAuditarPacotes(auditarPacotes);
		resumoGuias.setAuditarProcedimentosGrauAnestesista(auditarProcedimentosGrauAnestesista);
		
		EnumFlowValidator.EXISTE_HONORARIO_APTO_AUDITAR.getValidator().execute(resumoGuias);
		
		resumoGuias.resetFlags();
		
		if(hasProcedimentosCirurgicos && auditarProcedimentosCirurgicos){
			resumoGuias.setAuditarProcedimentosCirurgicos(auditarProcedimentosCirurgicos);
		}
		
		if(hasProcedimentosClinicos && auditarProcedimentosClinicos) {
			resumoGuias.setAuditarProcedimentosClinicos(auditarProcedimentosClinicos);
		}
		
		if(hasHonorariosPacotes && auditarPacotes){
			resumoGuias.setAuditarPacotes(auditarPacotes);
		}
		
		if(hasProcedimentosCirurgicoAnestesico && auditarProcedimentosGrauAnestesista){
			resumoGuias.setAuditarProcedimentosGrauAnestesista(auditarProcedimentosGrauAnestesista);
		}
				
		resumoGuias.atualizarResumo();
		new ManagerLayersAuditoria(resumoGuias);
		this.tocarObjetos(resumoGuias);
		return resumoGuias;
	}

	private void tocarObjetos(ResumoGuiasHonorarioMedico resumo) {
		LoteDeGuias ultimoLote = resumo.getGuiaMae().getUltimoLote();
		if (ultimoLote != null) {
			ultimoLote.getIdentificador();
		}
		
		for (GuiaHonorarioMedico guiaHonorario: resumo.getGuiasHonorarioMedico()){
			if (guiaHonorario.getGuiaOrigem().getFaturamento() != null) {
				guiaHonorario.getGuiaOrigem().getFaturamento().getGuiasRecursoGlosa();
			}
			guiaHonorario.tocarObjetos();
			for (HonorarioExterno honorario : guiaHonorario.getHonorariosMedico()) {
				honorario.getProfissional().getPrestadores().size();
				honorario.getSituacoes().size();
			}
		}
		
		for (AdapterProcedimento adapter : resumo.getProcedimentos()) {
			for (AdapterHonorario adapterHonorario : adapter.getHonorariosAnestesistas()) {
				adapterHonorario.getHonorario().getProfissional().getPrestadores().size();
			}
		}
	}

	/**
	 * Utilizado quando se marca apenas honorarios cirurgicos
	 * @param adapterProcedimentos
	 * @param resumo
	 * @param usuario
	 * @return
	 * @throws Exception
	 */
	public ResumoGuiasHonorarioMedico auditarGuia(Collection<AdapterProcedimento> adapterProcedimentosExame, Collection<AdapterProcedimento> adapterProcedimentos, ResumoGuiasHonorarioMedico resumo, UsuarioInterface usuario) throws Exception {
		adapterProcedimentos.addAll(adapterProcedimentosExame);
		return auditarGuia(adapterProcedimentos, resumo, null, usuario);
	}

	/**
	 * Utilizado quando se marca apenas honorarios pacotes
	 * @param resumo
	 * @param itensPacoteAuditoria
	 * @param usuario
	 * @return
	 * @throws Exception
	 */
	public ResumoGuiasHonorarioMedico auditarGuia(ResumoGuiasHonorarioMedico resumo, Collection<ItemPacoteHonorarioAuditoria> itensPacoteAuditoria, UsuarioInterface usuario) throws Exception {
		return auditarGuia(null, resumo, itensPacoteAuditoria, usuario);
	}

	/**
	 * Utilizado quando se marca apenas honorarios clinicos
	 * @param resumo
	 * @param usuario
	 * @return
	 * @throws Exception
	 */
	public ResumoGuiasHonorarioMedico auditarGuia(ResumoGuiasHonorarioMedico resumo, UsuarioInterface usuario) throws Exception {
		return auditarGuia(null, resumo, null, usuario);
	}
	/**
	 * Step 3
	 * @param adapterProcedimentos
	 * @param resumo
	 * @param itensPacoteAuditoria
	 * @param usuario
	 * @return
	 * @throws Exception
	 */
	public ResumoGuiasHonorarioMedico auditarGuia(Collection<AdapterProcedimento> adapterProcedimentos,	ResumoGuiasHonorarioMedico resumo, 
			Collection<ItemPacoteHonorarioAuditoria> itensPacoteAuditoria, UsuarioInterface usuario) throws Exception {
		GuiaSimples guiaMae 		= resumo.getGuiaMae();
		guiaMae.setValorAnterior(guiaMae.getValorTotal());
		
		if(adapterProcedimentos != null && !adapterProcedimentos.isEmpty()){
			resumo.getAdapterProcedimentosTela().addAll(adapterProcedimentos);
		}
		
		if(itensPacoteAuditoria != null && !itensPacoteAuditoria.isEmpty()){
			resumo.getItensPacoteAuditoriaTela().addAll(itensPacoteAuditoria);
		}
		
		EnumFlowValidator.ALTERACAO_PROCEDIMENTOS_EM_GUIAS_FATURADAS_OU_PAGAS.getValidator().execute(resumo);
		
		if(resumo.isAuditarProcedimentosCirurgicos()){
			this.glosarHonorarios(adapterProcedimentos, usuario);
			this.direcionarHonorariosNovos(resumo, adapterProcedimentos, usuario);
		}

		if(resumo.isAuditarProcedimentosGrauAnestesista()){
			this.glosarHonorarios(adapterProcedimentos, usuario);
			this.direcionarHonorariosNovos(resumo, adapterProcedimentos, usuario);
		}

		if(resumo.isAuditarProcedimentosClinicos()){
			this.processarProcedimentosDeVisita(resumo, usuario);
		}
		
		if(resumo.isAuditarPacotes()){
			processarPacotesDeHonorario(resumo, itensPacoteAuditoria, usuario);
		}

		EnumFlowValidator.ALTERACAO_HONORARIOS_SELECIONADOS_AUDITORIA.getValidator().execute(resumo);
		this.mudarSituacaoDasGuias(resumo.getGuiasHonorarioMedico(), usuario, resumo);

		guiaMae.recalcularValores();
		return resumo;
	}
	
	/**
	 * Trata a glosa e da inserção de novos pacotes de honorários
	 * @param adapterProcedimentos 
	 * @param novosItemPacoteAuditoria 
	 * @throws Exception 
	 */
	private void processarPacotesDeHonorario(ResumoGuiasHonorarioMedico resumo, Collection<ItemPacoteHonorarioAuditoria> novosItemPacoteAuditoria, UsuarioInterface usuario) throws Exception {
		GuiaCompleta guia = resumo.getGuia();
		GeradorGuiaHonorarioInterface guiaHonorario = (GeradorGuiaHonorarioInterface) guia;
//		TODO Coisa horrível feita pelo Dannylvan pra funcionar durante a homologação
		Prestador coopercardio = ImplDAO.findById(24079243L, Prestador.class);
		Integer grau = GrauDeParticipacaoEnum.RESPONSAVEL.getCodigo();
		

		RegistrarHonorarioIndividualCommandRunner commandRunner = new RegistrarHonorarioIndividualCommandRunner(
			new GuiaHonorarioProfissionalCredenciadoValidator()
		);
		
		processaPacotesAlterados(resumo.getHonorariosPacote(), usuario, resumo);

		commandRunner = new RegistrarHonorarioIndividualCommandRunner(
				new GuiaHonorarioProfissionalCredenciadoValidator(),
				new GuiaHonorarioProfissionalResponsavelDiferenteValidator(),
				new GuiaHonorarioPacoteDeHonorarioCompativelComAGuiaValidator(),
				new GuiaHonorarioHonorarioDuplicado()
		);
		
		for (ItemPacoteHonorarioAuditoria itemPacote : novosItemPacoteAuditoria) {
			Set<ItemPacoteHonorario> itensPacote = new HashSet<ItemPacoteHonorario>();
			itensPacote.add(itemPacote);
			
			Profissional profissional = ImplDAO.findById(itemPacote.getProfissional().getIdProfissional(), Profissional.class);
			commandRunner.setValores(grau, guiaHonorario, profissional, coopercardio, guia.getSegurado(), null, itensPacote);
			commandRunner.execute();
			
			boolean guiaExistente = false;
			for (GuiaHonorarioMedico gh : resumo.getGuiasHonorariosPacote()) {
				if(gh.getProfissional().equals(profissional)){
					guiaExistente = true;
					for (ItemPacoteHonorario itemPacoteHonorario : itensPacote) {
						gh.addHonorario(usuario, itemPacoteHonorario);
						marcarUltimoHonorarioAuditado(gh,itemPacoteHonorario);
						gh.recalcularValores();
					}
				}
			}
			if (!guiaExistente) {
				GuiaHonorarioMedico ghm = GuiaHonorarioMedico.criarGuiaHonorarioPacote(profissional, guiaHonorario, usuario, guia.getSegurado(), coopercardio, itensPacote);
				setSituacaoAuditada(ghm);
				resumo.addGuiaHonorarioMedico(ghm);
			}
		}
	}

	private void setSituacaoAuditada(GuiaHonorarioMedico ghm) {
		ghm.getSituacao().setDescricao(SituacaoEnum.AUDITADO.descricao());
		ghm.getSituacao().setMotivo( MotivoEnum.GERADO_AUTOMATICAMENTE_DURANTE_AUDITORIA.getMessage());
	}

	private void processaPacotesAlterados(Set<AdapterHonorario> honorarios, UsuarioInterface usuario, ResumoGuiasHonorarioMedico resumo) throws Exception {
		for (AdapterHonorario adapterHonorario: honorarios){
			HonorarioExterno honorario 			= adapterHonorario.getHonorario();
			if(honorario.isHonorarioPacote()){
				Profissional profissional 			= adapterHonorario.getProfissional();
				GuiaHonorarioMedico guiaHonorario  	= adapterHonorario.getHonorario().getGuiaHonorario();

				boolean alterouProfissional = !profissional.equals(honorario.getProfissional());
				boolean marcadoParaGlosa 	= honorario.isGlosar();
				boolean isGlosado 			= honorario.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao());
				boolean mudouPorcentagem 	= !adapterHonorario.getPorcentagem().equals(honorario.getPorcentagem());

				boolean mudouProfissionalPorcentagem = alterouProfissional || mudouPorcentagem;
				boolean naoEGlosa = !marcadoParaGlosa && !isGlosado;

				Prestador prestadorDestino = PrestadorHonorarioUtils.getPrestadorDestinoHonorariosPacotes(profissional);
				if(mudouProfissionalPorcentagem && naoEGlosa){
					if (mudouPorcentagem) {
						honorario.getItemPacoteHonorario().setPorcentagem(adapterHonorario.getPorcentagem());
						Assert.isNotEmpty(honorario.getMotivoGlosaTransiente(), "Caro usuário, informe o motivo da alteração da porcentagem do pacote.");
					}

					boolean geraNovaGuia = geraGuiaNovaPacote(adapterHonorario, guiaHonorario);
					honorario.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), MotivoEnum.AUDITADO_AUTOMATICAMENTE.getMessage(), new Date());
					if(geraNovaGuia){
						boolean guiaExistente = false;
						for (GuiaHonorarioMedico gh : resumo.getGuiasHonorariosPacote()) {
							if(gh.getProfissional().equals(profissional)){
								gh.addHonorario(usuario, honorario.getItemPacoteHonorario());

								marcarUltimoHonorarioAuditado(honorario, gh);

								gh.recalcularValores();
								resumo.addGuiaHonorarioMedico(guiaHonorario);
								guiaExistente = true;
								break;
							}
						}
						if(!guiaExistente){
							GuiaSimples guiaMae = resumo.getGuiaMae();
							GuiaHonorarioMedico guiaAlvo = GuiaHonorarioMedico.criarGuiaHonorarioPacote(profissional, (GeradorGuiaHonorarioInterface) guiaMae, usuario, guiaMae.getSegurado(), prestadorDestino, new ArrayList<ItemPacoteHonorario>());
							setSituacaoAuditada(guiaAlvo);
							guiaAlvo.addHonorario(usuario, honorario.getItemPacoteHonorario());

							marcarUltimoHonorarioAuditado(honorario, guiaAlvo);

							guiaAlvo.setPrestador(prestadorDestino);
							guiaAlvo.recalcularValores();
							resumo.addGuiaHonorarioMedico(guiaAlvo);
						}
						//pergunta se gera uma nova guia
						//Percorre os adapterHonorario pra saber se tem honorario com profissional diferente.
					} else{
						//senão o codigo anterior
						guiaHonorario.setProfissional(profissional);
						guiaHonorario.addHonorario(usuario, honorario.getItemPacoteHonorario());

						marcarUltimoHonorarioAuditado(honorario, guiaHonorario);

						guiaHonorario.setPrestador(prestadorDestino);

						if (guiaHonorario != null && alterouProfissional){
							guiaHonorario.setProfissional(profissional);
							resumo.addGuiaHonorarioMedico(guiaHonorario);
						}
					}
				} else if(marcadoParaGlosa){
					Assert.isNotEmpty(honorario.getMotivoGlosaTransiente(), "O motivo de glosa do honorario de pacote deve ser preenchido.");
					String motivo = MotivoEnum.GLOSADO_DURANTE_AUDITORIA.getMessage()+": "+honorario.getMotivoGlosaTransiente();
					honorario.glosar(usuario, new Date(), motivo);
				}
				
				guiaHonorario.recalcularValores();
			}
		}
	}
	
	private void marcarUltimoHonorarioAuditado(GuiaHonorarioMedico gh, ItemPacoteHonorario itemPacote) {
		for (HonorarioExterno h : gh.getHonorariosPacote()) {
			boolean mesmoItemPacote = h.getItemPacoteHonorario().equals(itemPacote);
			if(mesmoItemPacote && h.getSituacao().getDescricao().equals(SituacaoEnum.GERADO.descricao())){
				h.setAuditado(true);
			}
		}
	}

	private void marcarUltimoHonorarioAuditado(HonorarioExterno honorario, GuiaHonorarioMedico gh) {
		for (HonorarioExterno h : gh.getHonorariosPacote()) {
			boolean mesmoItemPacote = h.getItemPacoteHonorario().equals(honorario.getItemPacoteHonorario());
			if(mesmoItemPacote && h.getSituacao().getDescricao().equals(SituacaoEnum.GERADO.descricao())){
				h.setAuditado(honorario.isAuditado());
			}
		}
	}

	private boolean geraGuiaNovaPacote(AdapterHonorario adapterHonorario, GuiaHonorarioMedico guiaHonorario) {
		Set<HonorarioExterno> honorariosGuia = guiaHonorario.getHonorariosEmSituacaoGerado();
		for (HonorarioExterno honorarioExterno : honorariosGuia) {
			Profissional profissionalHonorario = honorarioExterno.getProfissional();
			if(!adapterHonorario.getProfissional().equals(profissionalHonorario)){
				return true;
			}
		}
		
		return false;
	}

	private boolean isTodosProcedimentoClinicosECirurgicosMarcadosParaAuditar(ResumoGuiasHonorarioMedico resumo){
		return isTodosProcedimentosClinicosMarcadosParaAuditar(resumo.getProcedimentosVisitaAtuais()) && isTodosProcedimentosCirurgicosMarcadosParaAuditar(resumo.getProcedimentos());
	}
	
	private boolean isTodosProcedimentosCirurgicosMarcadosParaAuditar(Set<AdapterProcedimento> procedimentos){
		for(AdapterProcedimento adapterProcedimento : procedimentos){
			if(!adapterProcedimento.getProcedimento().isAuditado()){
				return false;
			}
		}
		
		return true;
	}
	
	private boolean isTodosProcedimentosClinicosMarcadosParaAuditar(Set<ProcedimentoHonorario> procedimentosVisitaAtuais){
		for(ProcedimentoHonorario procedimeHonorario : procedimentosVisitaAtuais){
			if(!procedimeHonorario.isAuditado()){
				return false;
			}
		}
		
		return true;
	}

	private boolean isTodosProcedimentosAnestesicosMarcadosParaAuditar(Set<AdapterProcedimento> procedimentos){
		for(AdapterProcedimento adapterProcedimento : procedimentos){
			if(!adapterProcedimento.getProcedimento().isAuditado()){
				return false;
			}
		}
		
		return true;
	}
	
	private void processarProcedimentosDeVisita(ResumoGuiasHonorarioMedico resumo, UsuarioInterface usuario) throws Exception {
		Set<ProcedimentoHonorario> procedimentosVisitaNovos  = resumo.getProcedimentosVisitaNovos();
		Set<ProcedimentoHonorario> procedimentosVisitaAtuais = resumo.getProcedimentosVisitaAtuais();
		Set<ProcedimentoHonorarioLayer> procedimentosLayer = resumo.getProcedimentosLayer();
		
		GuiaHonorarioMedico guiaHonorarioMedico = null;
		for (ProcedimentoHonorario procedimentosVisitaNovo : procedimentosVisitaNovos) {
			Profissional profissional 	= procedimentosVisitaNovo.getProfissionalResponsavel();
			 
			guiaHonorarioMedico = this.getGuiaDeCirurgiaoProfissional(profissional, resumo);
			
			if (guiaHonorarioMedico == null){
				guiaHonorarioMedico = geraNovaGHM(resumo, profissional, procedimentosVisitaNovo, usuario);
				
			} else {
				guiaHonorarioMedico.addProcedimento(procedimentosVisitaNovo);
			}
			
			guiaHonorarioMedico.recalcularValores();
		}
		
		for (ProcedimentoHonorarioLayer procedimentoHonorarioLayer : procedimentosLayer) {
			Set<Procedimento> procedimentosDaGuia = procedimentoHonorarioLayer.getProcedimento().getGuia().getProcedimentos();
			
			procedimentoHonorarioLayer.getProcedimento().setAuditado(procedimentoHonorarioLayer.isAuditado());
			
			if (procedimentoHonorarioLayer.isAuditado()) {
				if (!procedimentoHonorarioLayer.isGlosar()) {
					ItemGlosavel item = procedimentoHonorarioLayer.getProcedimento().accept(new ItemGlosavelCompararClonarVisitor(procedimentoHonorarioLayer));
					if (item != null) {
						try {
							for (ProcedimentoHonorario p : procedimentosVisitaAtuais) {
								if (procedimentoHonorarioLayer.getProcedimento().equals(p)) {
									p.setMotivoGlosa(procedimentoHonorarioLayer.getMotivo());
									p.accept(new ItemGlosavelGlosarVisitor(usuario));
								}
								for (Iterator<Procedimento> iterator = procedimentosDaGuia.iterator(); iterator.hasNext();) {
									Procedimento proc = iterator.next();
									if (proc.equals(procedimentoHonorarioLayer.getProcedimento())) {
										((ProcedimentoHonorario) proc).setMotivoGlosa(procedimentoHonorarioLayer.getMotivo());
										((ProcedimentoHonorario) proc).accept(new ItemGlosavelGlosarVisitor(usuario));
									}
								}
								procedimentosVisitaAtuais.add((ProcedimentoHonorario) item);
								GuiaHonorarioMedico ghm = this.getGuiaDeCirurgiaoProfissional(procedimentoHonorarioLayer.getProfissional(), resumo);
								if (ghm == null) {
									ghm = geraNovaGHM(resumo, procedimentoHonorarioLayer.getProfissional(), (ProcedimentoHonorario) item, usuario);
								} else {
									ghm.addProcedimento((ProcedimentoInterface) item);
								}
								ghm.recalcularValores();
							}
						} catch (Exception e) {
							throw new ValidateException(e.getMessage());
						}
					} 
				}else {
					for (ProcedimentoHonorario p : procedimentosVisitaAtuais) {
						if (procedimentoHonorarioLayer.getProcedimento().equals(p)) {
							p.setMotivoGlosa(procedimentoHonorarioLayer.getMotivo());
							p.accept(new ItemGlosavelGlosarVisitor(usuario));
						}
					}
					for (Iterator<Procedimento> iterator = procedimentosDaGuia.iterator(); iterator.hasNext();) {
						Procedimento proc = iterator.next();
						if (proc.equals(procedimentoHonorarioLayer.getProcedimento())) {
							((ProcedimentoHonorario) proc).setMotivoGlosa(procedimentoHonorarioLayer.getMotivo());
							((ProcedimentoHonorario) proc).accept(new ItemGlosavelGlosarVisitor(usuario));
						}
					}
				}
			}
		}
	}

	private GuiaHonorarioMedico geraNovaGHM(ResumoGuiasHonorarioMedico resumo, Profissional profissional, ProcedimentoHonorario procedimentosVisitaNovo, UsuarioInterface usuario) throws Exception {
		GuiaSimples guiaOrigem 	   = resumo.getGuiaMae();
		AbstractSegurado segurado  = guiaOrigem.getSegurado();
		Prestador prestadorGuiaMae = guiaOrigem.getPrestador();
		Prestador prestadorDestino = PrestadorHonorarioUtils.getPrestadorDestino(prestadorGuiaMae, procedimentosVisitaNovo, profissional, GrauDeParticipacaoEnum.RESPONSAVEL.getCodigo());
		
		GuiaHonorarioMedico guiaHonorarioMedico = GuiaHonorarioMedico.criarGuiaHonorario(profissional, GrauDeParticipacaoEnum.RESPONSAVEL.getCodigo(), (GeradorGuiaHonorarioInterface) guiaOrigem, usuario, segurado, prestadorDestino, null);
		guiaHonorarioMedico.addProcedimento(procedimentosVisitaNovo);
		resumo.addGuiaHonorarioMedico(guiaHonorarioMedico);
		return guiaHonorarioMedico;
	}

	/**
	 * Resgata uma guia de Grau "Cirugião" para o profissional informado.
	 * @param profissional
	 * @param resumo
	 * @return
	 */
	private GuiaHonorarioMedico getGuiaDeCirurgiaoProfissional(Profissional profissional, ResumoGuiasHonorarioMedico resumo){
		GuiaHonorarioMedico resultado = null;
		
		for (GuiaHonorarioMedico guiaHonorario: resumo.getGuiasHonorarioMedico()){
			if (guiaHonorario.getProfissional().equals(profissional)){
				resultado = guiaHonorario;
				break;
			}
		}
		
		return resultado;
	}

	private void glosarHonorarios(Collection<AdapterProcedimento> adapterProcedimentos, UsuarioInterface usuario) {
		
		for (AdapterProcedimento adapter : adapterProcedimentos) {
			Procedimento procedimento = adapter.getProcedimento();
			
			boolean mudouCBHPM 			= !adapter.getCbhpm().equals(procedimento.getProcedimentoDaTabelaCBHPM());
			boolean mudouPorcentagem 	= !adapter.getPorcentagem().equals(procedimento.getPorcentagem());
			boolean glosar 				= mudouCBHPM || mudouPorcentagem || procedimento.isGlosar(); 
				
				//SE MUDAR o PROCEDIMENTO, PORCENTAGEM ou for MARCADA PRA GLOSA
				if (glosar) {
					boolean possuiMotivo = procedimento.getMotivoGlosaProcedimento() != null;
					
					if (mudouCBHPM) {
					String situacao 				= adapter.getProcedimento().getSituacao().getDescricao();
						boolean isProcedimentoGlosado	= situacao.equals(SituacaoEnum.GLOSADO.descricao());
						
						if(isProcedimentoGlosado){
							Assert.isFalse(mudouCBHPM, "Não é possível alterar o procedimento quando o mesmo se encontra em situação Glosado(a)");
						}
						
					Assert.isTrue(possuiMotivo, "Caro usuário, informe o motivo da substituição do procedimento "+procedimento.getProcedimentoDaTabelaCBHPM().getCodigoEDescricao()+ " pelo "+adapter.getCbhpm().getCodigoEDescricao());
					}
					
					if (mudouPorcentagem) {
						Assert.isTrue(possuiMotivo, "Caro usuário, informe o motivo da alteração da porcentagem do procedimento.");
					}
					
					//GLOSA O PROCEDIMENTO e os seus respectivos HONORÁRIOS
					this.glosarProcedimento(usuario, procedimento);
				} else {
					//SENÃO, sai GLOSAndo HONORÁRIO por honorário
					for (AdapterHonorario adapterHonorario : adapter.getTodosHonorarios()) {
						HonorarioExterno honorario = adapterHonorario.getHonorario();
						if (honorario.getGlosado()) {
							Assert.isNotEmpty(honorario.getMotivoDeGlosa(), "O motivo de glosa do " + honorario.getGrauDeParticipacaoFormatado() + " do procedimento " + adapter.getCbhpm().getCodigoEDescricao() + " deve ser preenchido.");
							honorario.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), MotivoEnum.AUDITADO_AUTOMATICAMENTE.getMessage(), new Date());
							if(GrauDeParticipacaoEnum.RESPONSAVEL.getCodigo() == honorario.getGrauDeParticipacao()){
								this.voltaSituacaoProcedimento(usuario, (ProcedimentoCirurgicoInterface) procedimento);
							}
						}
					}
				}
			}
		}
	
	private void direcionarHonorariosNovos(ResumoGuiasHonorarioMedico resumo, Collection<AdapterProcedimento> adapterProcedimentos, UsuarioInterface usuario) throws Exception, ValidateException {
		
		for (AdapterProcedimento adapter : adapterProcedimentos) {
			Procedimento procedimento = adapter.getProcedimento();
			
			if(procedimento.isAuditado()){
				boolean mudouCBHPM 			= !adapter.getCbhpm().equals(procedimento.getProcedimentoDaTabelaCBHPM());
				boolean mudouPorcentagem 	= !adapter.getPorcentagem().equals(procedimento.getPorcentagem());
					
				boolean marcadoPraGlosa 				= procedimento.isGlosar();
				boolean procedimentoMudouENaoFoiGlosado = !marcadoPraGlosa && (mudouCBHPM || mudouPorcentagem);
	
				// Se o procedimento MUDOU e NÃO FOI GLOSADO
				if (procedimentoMudouENaoFoiGlosado) {
					Set<AdapterHonorario> honorariosExternos = adapter.getTodosHonorariosNaoMarcadosPraGlosa();
					this.redirecionarHonorario(resumo, usuario, adapter, honorariosExternos, procedimentoMudouENaoFoiGlosado);
				}
					
				//Gera novos honorários para o caso dos honorarios tiveram profissional subistiuido
				Prestador prestador = resumo.getGuiaMae().getPrestador();
				this.processaHonorariosAlterados(adapter.getTodosHonorarios(), usuario, prestador, resumo, procedimento);
					
				// Não foi marcado pra Glosa
				// Seta Inclue video e Horário especial
				if (!marcadoPraGlosa) {
					Set<HonorarioExterno> honorariosExternosNaoCanceladosEGlosados = procedimento.getHonorariosExternosNaoCanceladosEGlosados();
					for (HonorarioExterno honorario : honorariosExternosNaoCanceladosEGlosados) {
						boolean isProcedimentoIncluiVideo 		= procedimento.isIncluiVideo() != null ? procedimento.isIncluiVideo() : false;
						boolean isProcedimentoHorarioEspecial 	= procedimento.isHorarioEspecial();
						
						honorario.setIncluiVideo(isProcedimentoIncluiVideo);
						honorario.setHorarioEspecial(isProcedimentoHorarioEspecial);
						if (procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO)
							((ProcedimentoCirurgicoInterface) procedimento).setDataRealizacao(adapter.getDataRealizacao());
						
						honorario.recalculaValorTotal();
						honorario.getGuiaHonorario().recalcularValores();
					}
				}
					
				//Redirecionamento dos honorários para novos graus inseridos durante o processo de auditoria
				Set<HonorarioExterno> honorariosNovos = adapter.getHonorariosNovos();
				
				for (HonorarioExterno honorarioExterno : honorariosNovos) {
					int grau = honorarioExterno.getGrauDeParticipacao();
					boolean isGrauAnestesico = grau == GrauDeParticipacaoEnum.ANESTESISTA.getCodigo() ||  grau == GrauDeParticipacaoEnum.AUXILIAR_ANESTESISTA.getCodigo();
					if(resumo.isAuditarProcedimentosGrauAnestesista() && !resumo.isAuditarProcedimentosCirurgicos()){
						Assert.isTrue(isGrauAnestesico, "Você tentou adicionar um novo honorário. " +
						"So é pemitida adicionar honorários anestésicos para os grau de participação Anestesista ou Auxiliar de Anestesista.");
					}
					if(!resumo.isAuditarProcedimentosGrauAnestesista() && resumo.isAuditarProcedimentosCirurgicos()){
						Assert.isFalse(isGrauAnestesico, "Você tentou adicionar um novo honorário. " +
						"So é pemitida adicionar honorários cirúrgicos para graus de participação diferentes de Anestesista ou Auxiliar de Anestesista.");
					}
				}
				
				if (!honorariosNovos.isEmpty()) {
					this.redirecionarHonorarioNovos(resumo, usuario, adapter, honorariosNovos, procedimentoMudouENaoFoiGlosado);
				}
			}
		}
	}
	
	private void redirecionarHonorarioNovos(ResumoGuiasHonorarioMedico resumo, UsuarioInterface usuario, AdapterProcedimento adapterProcedimento,
											Set<HonorarioExterno> honorariosNovos,	boolean procedimentoMudouENaoFoiGlosado) throws Exception {
		
		Map<Integer, GuiaHonorarioMedico> guiasNovasPorGrau = this.montaMapaPorGrauParticipacaoHonorarios(resumo, usuario, honorariosNovos, adapterProcedimento);

		this.distribuirHonorarios(usuario, adapterProcedimento, procedimentoMudouENaoFoiGlosado, guiasNovasPorGrau);
	}

	private Map<Integer, GuiaHonorarioMedico> montaMapaPorGrauParticipacaoHonorarios(ResumoGuiasHonorarioMedico resumo, UsuarioInterface usuario,
																					Set<HonorarioExterno> honorariosNovos, AdapterProcedimento adapterProcedimento) throws Exception {
		
		Map<Integer, GuiaHonorarioMedico> resultado = new HashMap<Integer, GuiaHonorarioMedico>();
		
		for (Iterator<HonorarioExterno> iterator = honorariosNovos.iterator(); iterator.hasNext(); ) {
			
			HonorarioExterno honorario 	= iterator.next();
			boolean naoCancelado 		= !honorario.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao());
			Boolean naoGlosado 			= (honorario.getGlosado() == null || !honorario.getGlosado());

			if (naoCancelado && naoGlosado) {
				GuiaHonorarioMedico guiaAlvo = getGuiaAlvoHonorariosNovos(resumo, honorario, usuario, true, adapterProcedimento);

				resultado.put(guiaAlvo.getGrauDeParticipacao(), guiaAlvo);
			}
		}
		
		return resultado;
	}

	private void processaHonorariosAlterados(Set<AdapterHonorario> honorarios, UsuarioInterface usuario, Prestador prestadorGuiaMae, ResumoGuiasHonorarioMedico resumo, Procedimento procedimento) throws Exception {
		for (AdapterHonorario adapterHonorario: honorarios){
			Profissional profissional 			= adapterHonorario.getProfissional();
			HonorarioExterno honorario 			= adapterHonorario.getHonorario();
			GuiaHonorarioMedico guiaHonorario  	= adapterHonorario.getHonorario().getGuiaHonorario();
 
			boolean alterouProfissional = !profissional.equals(honorario.getProfissional());
			boolean marcadoPraGlosa 	= honorario.getGlosado();
			boolean isGlosado 			= honorario.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao());

			if(alterouProfissional && !marcadoPraGlosa && !isGlosado){

				boolean geraNovaGuia = geraGuiaNova(adapterHonorario, guiaHonorario);

				int grauDeParticipacao  = guiaHonorario.getGrauDeParticipacao();
				honorario.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), MotivoEnum.AUDITADO_AUTOMATICAMENTE.getMessage(), new Date());
				Prestador prestadorDestino = PrestadorHonorarioUtils.getPrestadorDestino(prestadorGuiaMae, procedimento, profissional, grauDeParticipacao);

				if(geraNovaGuia){
					GuiaSimples guiaMae = resumo.getGuiaMae();
					GuiaHonorarioMedico guiaAlvo = GuiaHonorarioMedico.criarGuiaHonorario(profissional, grauDeParticipacao, (GeradorGuiaHonorarioInterface) guiaMae, usuario, guiaMae.getSegurado(), guiaMae.getPrestador(), null);
					guiaAlvo.setPrestador(prestadorDestino);
					guiaAlvo.addHonorario(usuario, procedimento);
					resumo.addGuiaHonorarioMedico(guiaAlvo);
					//pergunta se gera uma nova guia
					//Percorre os adapterHonorario pra saber se tem honorario com profissional diferente.
				}else{
					//senão o codigo anterior
					guiaHonorario.setProfissional(profissional);
					guiaHonorario.addHonorario(usuario, procedimento);
					guiaHonorario.setPrestador(prestadorDestino);

					if (guiaHonorario != null){
						resumo.addGuiaHonorarioMedico(guiaHonorario);
					}

				}
				
				guiaHonorario.recalcularValores();
			}
		}
	}

	private boolean geraGuiaNova(AdapterHonorario adapterHonorario, GuiaHonorarioMedico guiaHonorario) {
		Set<HonorarioExterno> honorariosGuia = guiaHonorario.getHonorariosEmSituacaoGerado();
		for (HonorarioExterno honorarioExterno : honorariosGuia) {
			Profissional profissionalHonorario = honorarioExterno.getProfissional();
			//Se tem outro honorário dentro desta guia com outro profissional
			if(!honorarioExterno.equals(adapterHonorario.getHonorario()) && !adapterHonorario.getProfissional().equals(profissionalHonorario)){
				return true;
			}
		}
		
		return false;
	}

	private void voltaSituacaoProcedimento(UsuarioInterface usuario, ProcedimentoCirurgicoInterface procedimento) {
		if(procedimento.isSituacaoAtual(SituacaoEnum.PROCEDIMENTO_COM_HONORARIO_GERADO.descricao())){
			SituacaoInterface situacaoAnterior = procedimento.getSituacaoAnterior(procedimento.getSituacao());
			procedimento.mudarSituacao(usuario, situacaoAnterior.getDescricao(), "O Procedimento teve sua situação alterada para a situação imediadamente anterior à situação \"Honorário Gerado(a)\" após glosa da guia de honorário médico do cirugião", new Date());
			procedimento.setProfissionalResponsavel(null);
		}
	}

	private boolean verificaSeAGuiaEstaComTodosOsItensGlosado(GuiaHonorarioMedico guiaHonorario) {
		boolean isSemProcedimento 	= guiaHonorario.getProcedimentos(SituacaoEnum.AUTORIZADO.descricao()).isEmpty();
		boolean isSemHonorarios		= guiaHonorario.getHonorariosEmSituacaoGerado().isEmpty();
				
		return isSemHonorarios && isSemProcedimento;
	}

	private void mudarSituacaoDasGuias(Set<GuiaHonorarioMedico> guiasHonorarioMedico, UsuarioInterface usuario, ResumoGuiasHonorarioMedico resumo) {
		Date data = new Date();

		for (GuiaHonorarioMedico guia : getGuiasAptasAMudarSituacao(guiasHonorarioMedico, resumo)) {
			boolean isAuditado = guia.isSituacaoAtual(SituacaoEnum.AUDITADO.descricao());
			boolean isRecebido = guia.isSituacaoAtual(SituacaoEnum.RECEBIDO.descricao());
			
			if(verificaSeAGuiaEstaComTodosOsItensGlosado(guia)){
				guia.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), MotivoEnum.GLOSADO_AUTOMATICAMENTE.getMessage(), data);
			} else if (isRecebido || isAuditado) {
				
				String situacao = SituacaoEnum.AUDITADO.descricao();
				
				boolean isApenasHonorarioCirurgico = resumo.isAuditarProcedimentosCirurgicos() && !resumo.isAuditarProcedimentosClinicos() && !resumo.isAuditarProcedimentosGrauAnestesista() && !resumo.isAuditarPacotes();
				boolean isApenasHonorarioClinico = resumo.isAuditarProcedimentosClinicos() && !resumo.isAuditarProcedimentosCirurgicos() && !resumo.isAuditarProcedimentosGrauAnestesista() && !resumo.isAuditarPacotes();
				boolean isApenasHonorarioAnestesista = resumo.isAuditarProcedimentosGrauAnestesista() && !resumo.isAuditarProcedimentosCirurgicos() && !resumo.isAuditarProcedimentosClinicos() && !resumo.isAuditarPacotes();
				boolean isApenasHonorarioPacote = resumo.isAuditarPacotes() && !resumo.isAuditarProcedimentosGrauAnestesista() && !resumo.isAuditarProcedimentosCirurgicos() && !resumo.isAuditarProcedimentosClinicos();
				
				boolean isHonorarioCirurgicoClinico = resumo.isAuditarProcedimentosCirurgicos() && resumo.isAuditarProcedimentosClinicos() && !resumo.isAuditarProcedimentosGrauAnestesista() && !resumo.isAuditarPacotes();
				boolean isHonorarioCirurgicoAnestesico = resumo.isAuditarProcedimentosCirurgicos() && !resumo.isAuditarProcedimentosClinicos() && resumo.isAuditarProcedimentosGrauAnestesista() && !resumo.isAuditarPacotes();
				boolean isHonorarioCirurgicoPacote = resumo.isAuditarProcedimentosCirurgicos() && !resumo.isAuditarProcedimentosClinicos() && !resumo.isAuditarProcedimentosGrauAnestesista() && resumo.isAuditarPacotes();
				boolean isHonorarioCirurgicoClinicoAnestesico = resumo.isAuditarProcedimentosCirurgicos() && resumo.isAuditarProcedimentosClinicos() && resumo.isAuditarProcedimentosGrauAnestesista() && !resumo.isAuditarPacotes();
				
				boolean isHonorarioClinicoAnestesico = !resumo.isAuditarProcedimentosCirurgicos() && resumo.isAuditarProcedimentosClinicos() && resumo.isAuditarProcedimentosGrauAnestesista() && !resumo.isAuditarPacotes();
				boolean isHonorarioClinicoPacote = !resumo.isAuditarProcedimentosCirurgicos() && resumo.isAuditarProcedimentosClinicos() && !resumo.isAuditarProcedimentosGrauAnestesista() && resumo.isAuditarPacotes();
				boolean isHonorarioClinicoAnestesicoPacote = !resumo.isAuditarProcedimentosCirurgicos() && resumo.isAuditarProcedimentosClinicos() && resumo.isAuditarProcedimentosGrauAnestesista() && resumo.isAuditarPacotes();
				
				boolean isHonorarioAnestesicoPacote = !resumo.isAuditarProcedimentosCirurgicos() && !resumo.isAuditarProcedimentosClinicos() && resumo.isAuditarProcedimentosGrauAnestesista() && resumo.isAuditarPacotes();
				
				String descricaoHonorario = "";
				if(isApenasHonorarioCirurgico){
					descricaoHonorario += "cirúrgico"; 
				} else if (isApenasHonorarioClinico) {
					descricaoHonorario += "clínicos";
				} else if (isApenasHonorarioAnestesista) {
					descricaoHonorario += "anestésicos";
				} else if (isApenasHonorarioPacote) {
					descricaoHonorario += "pacotes";
				} else if(isHonorarioCirurgicoClinico){
					descricaoHonorario += "cirúrgicos e clínicos";
				} else if(isHonorarioCirurgicoAnestesico){
					descricaoHonorario += "cirúrgicos e anestésicos";
				} else if(isHonorarioCirurgicoPacote){
					descricaoHonorario += "cirúrgicos e pacotes";
				} else if(isHonorarioClinicoAnestesico){
					descricaoHonorario += "clínicos e anestésicos";
				} else if(isHonorarioClinicoPacote){
					descricaoHonorario += "clínicos e pacotes";
				} else if(isHonorarioCirurgicoClinicoAnestesico){
					descricaoHonorario += "cirúrgico, clínicos e anestésicos";
				} else if(isHonorarioClinicoAnestesicoPacote){
					descricaoHonorario += "clínicos, anestésicos e pacotes";
				} else if(isHonorarioAnestesicoPacote){
					descricaoHonorario += "anestésicos e pacotes";
				} else {
					descricaoHonorario += "cirúrgico, clínicos, anestésicos e pacotes";
				}
				
				String motivo = MotivoEnum.AUDITORIA_DE_HONORARIOS.getMessage(descricaoHonorario);
				
				guia.mudarSituacao(usuario, situacao, motivo, data);
				guia.recalcularValores();
			}
		}
	}

	private Set<GuiaHonorarioMedico> getGuiasAptasAMudarSituacao(Set<GuiaHonorarioMedico> guiasHonorarioMedico, ResumoGuiasHonorarioMedico resumo) {
		Set<GuiaHonorarioMedico> guiasDeHonorarioMedicoAptasParaMudarSituacao = new HashSet<GuiaHonorarioMedico>();
		for(GuiaHonorarioMedico guia : guiasHonorarioMedico){
			
			if(!guia.getProcedimentos().isEmpty()){
				for(ProcedimentoHonorarioLayer procedimentoClinico : resumo.getProcedimentosLayer()){
					if(procedimentoClinico.getProcedimento().getGuia().equals(guia) && procedimentoClinico.isAuditado()){
						guiasDeHonorarioMedicoAptasParaMudarSituacao.add(guia);
					}
				}
			}
			
			if(!guia.getHonorariosMedico().isEmpty()){
				boolean isTodosProcedimentosCirurgicosMarcadosParaAuditar = true;
				for(HonorarioExterno honorarioProcedimentoCirurgico : guia.getHonorariosMedico()){
					if(!honorarioProcedimentoCirurgico.isHonorarioPacote()){
						boolean isAuditado = honorarioProcedimentoCirurgico.getProcedimento().isAuditado();
						boolean isGlosado = honorarioProcedimentoCirurgico.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao());
						if(!isAuditado  && !isGlosado){
							isTodosProcedimentosCirurgicosMarcadosParaAuditar = false;
						}
					} else{
						if(!honorarioProcedimentoCirurgico.isAuditado()){
							isTodosProcedimentosCirurgicosMarcadosParaAuditar = false;
						}
					}
				}
				
				if(isTodosProcedimentosCirurgicosMarcadosParaAuditar && resumo.isAuditarProcedimentosCirurgicos() && !guia.isGuiaHonorarioAnestesista()){
					guiasDeHonorarioMedicoAptasParaMudarSituacao.add(guia);
				}
				if(isTodosProcedimentosCirurgicosMarcadosParaAuditar && resumo.isAuditarProcedimentosGrauAnestesista() && guia.isGuiaHonorarioAnestesista()){
					guiasDeHonorarioMedicoAptasParaMudarSituacao.add(guia);
				}
				
				if(isTodosProcedimentosCirurgicosMarcadosParaAuditar && resumo.isAuditarPacotes()){
					guiasDeHonorarioMedicoAptasParaMudarSituacao.add(guia);
				}
			}
			
		}
		return guiasDeHonorarioMedicoAptasParaMudarSituacao; 
	}
	
	/**
	 * Método responsável por inserir um determinado honorário dentro da sua respectiva guia.
	 * @param resumo
	 * @param usuario
	 * @param adapterProcedimento
	 * @param honorariosExternos
	 * @param procedimentoMudouENaoFoiGlosado
	 * @throws Exception
	 * @throws ValidateException
	 */
	private void redirecionarHonorario(ResumoGuiasHonorarioMedico resumo, UsuarioInterface usuario, AdapterProcedimento adapterProcedimento, 
			Set<AdapterHonorario> honorariosExternos, boolean procedimentoMudouENaoFoiGlosado) throws Exception, ValidateException {
		
		//Monta mapa por grau de participação
		 Map<Integer, GuiaHonorarioMedico> guiasNovasPorGrau = this.montaMapaPorGrauParticipacao(resumo, usuario, honorariosExternos);
		 
		this.distribuirHonorarios(usuario, adapterProcedimento, procedimentoMudouENaoFoiGlosado, guiasNovasPorGrau);
	}

	private void distribuirHonorarios(UsuarioInterface usuario, AdapterProcedimento adapterProcedimento, boolean procedimentoMudouENaoFoiGlosado,
			Map<Integer, GuiaHonorarioMedico> guiasNovasPorGrau) throws ValidateException, Exception {
		
		Procedimento procedimento = adapterProcedimento.getProcedimento();
		BigDecimal porcentagem 						= adapterProcedimento.getPorcentagem();

		GuiaHonorarioMedico guiaDeResponsavel = guiasNovasPorGrau.get(GrauDeParticipacaoEnum.RESPONSAVEL.getCodigo());
		if (guiaDeResponsavel != null) {
			this.atualizarHonorario(usuario, adapterProcedimento, procedimentoMudouENaoFoiGlosado, procedimento, porcentagem, guiaDeResponsavel);
		}
		
		GuiaHonorarioMedico guiaDeAnestesista = guiasNovasPorGrau.get(GrauDeParticipacaoEnum.ANESTESISTA.getCodigo());
		if (guiaDeAnestesista != null) {
			this.atualizarHonorario(usuario, adapterProcedimento, procedimentoMudouENaoFoiGlosado, procedimento, porcentagem, guiaDeAnestesista);
		}
		
		for (Entry<Integer, GuiaHonorarioMedico> entry: guiasNovasPorGrau.entrySet()) {
			GuiaHonorarioMedico guia 	= entry.getValue();
			boolean isResponsavel 		= entry.getKey().equals(GrauDeParticipacaoEnum.RESPONSAVEL.getCodigo());
			boolean isAnestesista 		= entry.getKey().equals(GrauDeParticipacaoEnum.ANESTESISTA.getCodigo());
			
			if (!isResponsavel && !isAnestesista) {
				this.atualizarHonorario(usuario, adapterProcedimento, procedimentoMudouENaoFoiGlosado, procedimento, porcentagem, guia);
			}
		}
	}

	private Map<Integer, GuiaHonorarioMedico> montaMapaPorGrauParticipacao(ResumoGuiasHonorarioMedico resumo, UsuarioInterface usuario, Set<AdapterHonorario> honorariosExternos) 
			throws Exception {
		
		Map<Integer, GuiaHonorarioMedico> resultado = new HashMap<Integer, GuiaHonorarioMedico>();
		
		for (Iterator<AdapterHonorario> iterator = honorariosExternos.iterator(); iterator.hasNext(); ) {
			
			AdapterHonorario adapterHonorario 	= iterator.next();
			HonorarioExterno honorario 			= adapterHonorario.getHonorario();
			boolean naoCancelado 				= !honorario.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao());
			Boolean naoGlosado 					= (honorario.getGlosado() == null || !honorario.getGlosado());

			boolean aindaNaoTemOGrau = resultado.get(honorario.getGrauDeParticipacao()) != null;
			if (naoCancelado && naoGlosado && !aindaNaoTemOGrau) {
				GuiaHonorarioMedico guiaAlvo = this.getGuiaAlvo(resumo, honorario, usuario, true);

				resultado.put(guiaAlvo.getGrauDeParticipacao(), guiaAlvo);
			}
		}
		
		return resultado;
	}

	
	private void atualizarHonorario(UsuarioInterface usuario, AdapterProcedimento adapter, boolean procedimentoMudouENaoFoiGlosado,
			Procedimento procedimento, BigDecimal porcentagemProcedimento, GuiaHonorarioMedico guia)	throws ValidateException, Exception {
		
		HonorarioExterno honorarioExterno;

		if (procedimentoMudouENaoFoiGlosado) {
			//Se o PROCEDIMENTO tiver Mudado, CRIA-SE OUTRO a partir do NOVO PROCEDIMENTO INFORMADO
			honorarioExterno = guia.addHonorario(usuario, adapter.getNovoProcedimento(usuario));
		} else {
			//Senão o insere dentro da guia
			honorarioExterno = guia.addHonorario(usuario, procedimento);
			procedimento.setPorcentagem(porcentagemProcedimento);
		}
		
		honorarioExterno.recalculaValorTotal();
		guia.recalcularValores();
	}

	private GuiaHonorarioMedico getGuiaAlvo(ResumoGuiasHonorarioMedico resumo, HonorarioExterno honorario, UsuarioInterface usuario, boolean verificaProfissional) throws Exception {
		GuiaHonorarioMedico guiaAlvo = null;
		
		Set<GuiaHonorarioMedico> 	guiasHonorarioMedico 	= resumo.getGuiasHonorarioMedico();
		Profissional 				profissional 			= honorario.getProfissional();
		int 						grauDeParticipacao 		= honorario.getGrauDeParticipacao();
		
		guiaAlvo = getGuiJaExistente(guiasHonorarioMedico, grauDeParticipacao, verificaProfissional, profissional);
		
		if (guiaAlvo == null) {
			GuiaSimples guiaMae = resumo.getGuiaMae();
			guiaAlvo = GuiaHonorarioMedico.criarGuiaHonorario(profissional, grauDeParticipacao, (GeradorGuiaHonorarioInterface) guiaMae, usuario, guiaMae.getSegurado(), guiaMae.getPrestador(), null);
			resumo.addGuiaHonorarioMedico(guiaAlvo);
		}
		
		return guiaAlvo;
	}

	private GuiaHonorarioMedico getGuiaAlvoHonorariosNovos(ResumoGuiasHonorarioMedico resumo, HonorarioExterno honorario, UsuarioInterface usuario, boolean verificaProfissional, AdapterProcedimento adapterProcedimento) throws Exception {
		GuiaHonorarioMedico guiaAlvo = null;
		
		Set<GuiaHonorarioMedico> 	guiasHonorarioMedico 	= resumo.getGuiasHonorarioMedico();
		Profissional 				profissional 			= honorario.getProfissional();
		int 						grauDeParticipacao 		= honorario.getGrauDeParticipacao();
		ProcedimentoInterface procedimento					= adapterProcedimento.getProcedimento();
		Prestador prestadorGuiaMae							= resumo.getGuiaMae().getPrestador();
		
		guiaAlvo = getGuiJaExistente(guiasHonorarioMedico, grauDeParticipacao, verificaProfissional, profissional);
		
		if (guiaAlvo == null) {
			GuiaSimples guiaMae = resumo.getGuiaMae();
			guiaAlvo = GuiaHonorarioMedico.criarGuiaHonorario(profissional, grauDeParticipacao, (GeradorGuiaHonorarioInterface) guiaMae, usuario, guiaMae.getSegurado(), guiaMae.getPrestador(), null);
			Prestador prestadorDestino = PrestadorHonorarioUtils.getPrestadorDestino(prestadorGuiaMae, procedimento, profissional, grauDeParticipacao);
			guiaAlvo.setPrestador(prestadorDestino);
			resumo.addGuiaHonorarioMedico(guiaAlvo);
		}
		
		return guiaAlvo;
	}
	
	private GuiaHonorarioMedico getGuiJaExistente(Set<GuiaHonorarioMedico> guiasHonorarioMedico, int grauDeParticipacao, boolean verificaProfissional, Profissional profissional) {
		
		for (GuiaHonorarioMedico guia : guiasHonorarioMedico) {
			boolean isMesmoGrau  = guia.getGrauDeParticipacao() == grauDeParticipacao;
			boolean isNotGlosada = !guia.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao());
			
			if(verificaProfissional){
				
				boolean isMesmoProfissional = guia.getProfissional().equals(profissional);
				if (isMesmoProfissional && isMesmoGrau && isNotGlosada) {
					return guia;
				}
			} else {
				if (isMesmoGrau && isNotGlosada) {
					return guia;
				}
			}
		}
		
		return null;
	}

	private void glosarProcedimento(UsuarioInterface usuario, Procedimento procedimento) {
		ItemGlosavelGlosarVisitor visitor = new ItemGlosavelGlosarVisitor(usuario);
		procedimento.accept(visitor);
		
		Set<HonorarioExterno> honorarios = procedimento.getHonorariosExternosNaoCanceladosEGlosados();
		
		for (Honorario honorario : honorarios) {
			HonorarioExterno honorarioExterno = (HonorarioExterno) honorario;
			honorarioExterno.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), MotivoEnum.GLOSADO_AUTOMATICAMENTE.getMessage(), new Date());
		}
	}

	/**
	 * Step 4
	 * @param resumo
	 * @throws Exception
	 */
	public void salvarGuia(ResumoGuiasHonorarioMedico resumo) throws Exception{
		Set<GuiaHonorarioMedico> guias = getGuiasAptasAMudarSituacao(resumo.getGuiasHonorarioMedico(), resumo);
				
		if(resumo.isAuditarPacotes()){
			for (GuiaHonorarioMedico guia : resumo.getGuiasHonorariosPacote()) {
				if(!guias.contains(guia)){
					for (HonorarioExterno hon : guia.getHonorariosPacote()) {
						if(hon.isAuditado()){
							guias.add(guia);
							break;
						}
					}
				}
			}
		}
		
		guias.addAll(resumo.getGuiasHonorariosPacoteGeradasAutomaticamenteDuranteAuditoria());
		
		for (GuiaHonorarioMedico guiaHonorario: guias){
			this.salvarGuia(guiaHonorario);
			resumo.getAutorizacoes().add(guiaHonorario.getAutorizacao());
		}
		resumo.getGuiasHonorarioMedico();
		
		this.salvarGuia(resumo.getGuiaMae());
	}
	
	public <G extends GuiaSimples> void salvarGuia(G guia) throws Exception{
		isNotNull(guia,"Guia Inválida!");

		SeguradoInterface segurado = guia.getSegurado();
		if (guia.isSituacaoAtual(SituacaoEnum.CONFIRMADO.descricao()) || guia.isSituacaoAtual(SituacaoEnum.FECHADO.descricao())){
			if (segurado != null) {
				segurado.incrementarLimites(guia);
				ImplDAO.save(guia.getSegurado().getConsumoIndividual());
			}
		}else if (guia.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())){
			if (segurado != null){
				segurado.decrementarLimites(guia);
				ImplDAO.save(guia.getSegurado().getConsumoIndividual());
			}
		}else if (guia.getIdGuia() == null){
			if (segurado != null){
				segurado.incrementarLimites(guia);
				ImplDAO.save(guia.getSegurado().getConsumoIndividual());
			}
		}else{
			ImplDAO.save(guia.getSegurado().getConsumoIndividual());
		}
		if(guia.isInternacao()){
			((GuiaCompleta)guia).setTipoAcomodacao(GuiaCompleta.TIPO_ACOMODACAO_INTERNO);
		}
		
		ImplDAO.save(guia);
		
		if(guia.getAutorizacao() == null) {
			guia.setAutorizacao(guia.getIdGuia().toString());
		}
	}
}