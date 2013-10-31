package br.com.infowaypi.ecare.resumos;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Transient;

import br.com.infowaypi.ecare.procedimentos.ProcedimentoCirurgicoParaAuditoriaDeHonorarios;
import br.com.infowaypi.ecare.services.honorariomedico.adapter.AdapterHonorario;
import br.com.infowaypi.ecare.services.honorariomedico.adapter.AdapterProcedimento;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacoteHonorarioAuditoria;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GeradorGuiaHonorarioInterface;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GuiaHonorarioMedico;
import br.com.infowaypi.ecarebc.atendimentos.honorario.Honorario;
import br.com.infowaypi.ecarebc.atendimentos.honorario.HonorarioExterno;
import br.com.infowaypi.ecarebc.atendimentos.validators.GuiaHonorarioPorteAnestesistaValidator;
import br.com.infowaypi.ecarebc.enums.GrauDeParticipacaoEnum;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoHonorario;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoHonorarioAuditor;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoHonorarioLayer;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Resumo utilizado para viabilizar a auditoria de diversas guias de honorario simultaneamente. Tudo isso a partir da guia de origem. 
 */
@SuppressWarnings("rawtypes")
public class ResumoGuiasHonorarioMedico {
	private UsuarioInterface 			usuario;
	private Set<Honorario> 				honorariosOrdenados;
	private Set<Honorario> 				honorariosNaoAnestesistaNovos;
	private Set<Honorario> 				honorariosAnestesistasOrdenados;
	private Set<GuiaHonorarioMedico>	guiasHonorarioMedico;
	
	/**procedimentos cirurgicos: com grau anestisista ou nao*/
	private Set<AdapterProcedimento>  	procedimentos;
	
	/**pacotes*/
	private Set<AdapterHonorario>  		honorariosPacote;

	private Set<ProcedimentoHonorario> procedimentosVisitaAtuais;
	private Set<ProcedimentoHonorario> procedimentosVisitaNovos;
	private Set<ProcedimentoHonorarioLayer> procedimentosLayer;
	
	private Map<ProcedimentoCirurgico, Set<Honorario>> mapHonorariosPorProcedimento;
	private Procedimento procedimentoCorrente;
	private Set<Honorario> honorariosCorrentes;
	boolean flag;
	private GuiaCompleta guiaMae;
	private Set<String> autorizacoes;
	
	/** Variáveis utilizadas para possibilitar auditar procedimentos clínicos e cirúrgicos de forma separada.*/
	boolean auditarProcedimentosClinicos;
	boolean auditarProcedimentosCirurgicos;
	boolean auditarPacotes;
	boolean auditarProcedimentosGrauAnestesista;
	
	/** Variaveis transientes, usados para validação*/
	@Transient
	private List<AdapterProcedimento> adapterProcedimentosTela = new ArrayList<AdapterProcedimento>();
	@Transient
	private List<ItemPacoteHonorarioAuditoria> itensPacoteAuditoriaTela = new ArrayList<ItemPacoteHonorarioAuditoria>();
	

	public ResumoGuiasHonorarioMedico(){
		procedimentos						= new HashSet<AdapterProcedimento>();
		honorariosPacote					= new HashSet<AdapterHonorario>();
		guiasHonorarioMedico 				= new HashSet<GuiaHonorarioMedico>();
		honorariosAnestesistasOrdenados 	= new HashSet<Honorario>();
		honorariosOrdenados 				= new HashSet<Honorario>();
		honorariosNaoAnestesistaNovos 		= new HashSet<Honorario>();
		mapHonorariosPorProcedimento 		= new HashMap<ProcedimentoCirurgico, Set<Honorario>>();
		autorizacoes						= new HashSet<String>();
		procedimentosVisitaAtuais			= new HashSet<ProcedimentoHonorario>();
		procedimentosVisitaNovos			= new HashSet<ProcedimentoHonorario>();
		
		auditarProcedimentosClinicos 		= false;
		auditarProcedimentosCirurgicos  	= false;
		auditarPacotes 						= false;
		auditarProcedimentosGrauAnestesista	= false;
	}
	
	public Set<String> getAutorizacoes() {
		return autorizacoes;
	}

	public void setAutorizacoes(Set<String> autorizacoes) {
		this.autorizacoes = autorizacoes;
	}

	public ResumoGuiasHonorarioMedico(GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario){
		this();
		this.usuario = usuario;
		this.guiaMae = guia;
		
		this.tocarObjetos(guia);
		
		Set<ProcedimentoCirurgico> procedimentosEmSituacoesValidas = ((GeradorGuiaHonorarioInterface)guia).getProcedimentosQueGeraramHonorariosExternos();
//		Set<Procedimento> procedimentosExameEmSituacoesValidas = ((GeradorGuiaHonorarioInterface)guia).getProcedimentosExameQueGeraramHonorariosExternos();
		
		for (Procedimento procedimentoCirurgico: procedimentosEmSituacoesValidas){
			procedimentos.add(new AdapterProcedimento(procedimentoCirurgico));
		}

//		for (Procedimento procedimentoExame : procedimentosExameEmSituacoesValidas) {
//			procedimentos.add(new AdapterProcedimento(procedimentoExame));
//		}
		
		for(GuiaHonorarioMedico ghm: guia.getGuiasFilhasDeHonorarioMedicoAptasPraAuditoria()){
			Set<ProcedimentoHonorario> procedimentosDaGuia = ghm.getProcedimentosHonorarioNaoGlosadosNemCanceladosNemNegados();
			procedimentosVisitaAtuais.addAll(procedimentosDaGuia);
			for (ProcedimentoHonorario procedimentoHonorario : procedimentosVisitaAtuais) {
				procedimentoHonorario.setJaAuditado(procedimentoHonorario.isAuditado());
			}
			
			ghm.tocarObjetos();
			guiasHonorarioMedico.add(ghm);
			for (HonorarioExterno honorario : ghm.getHonorariosEmSituacaoGerado()) {
				if(honorario.isHonorarioPacote()){
					honorariosPacote.add(new AdapterHonorario(honorario));
				}
			}
		}
		
	}

	/**
	 * Necessario para filtrar procedimentos cirurgicos de cirurgicos com grau anestesista.
	 * Utilizado na classe @AuditarGuiaHonorarioMedico, metodo selecionarGuia
	 */
	public void atualizarResumo(){
		Set<AdapterProcedimento> paraRemover = new HashSet<AdapterProcedimento>();
		if(isAuditarProcedimentosCirurgicos() && !isAuditarProcedimentosGrauAnestesista()){
			for (AdapterProcedimento adapter : procedimentos) {
				if(adapter.getHonorarios().isEmpty() && !adapter.getHonorariosAnestesistas().isEmpty()){
					paraRemover.add(adapter);
				}
			}
		}
		if(!isAuditarProcedimentosCirurgicos() && isAuditarProcedimentosGrauAnestesista()){
			for (AdapterProcedimento adapter : procedimentos) {
				if(!adapter.getHonorarios().isEmpty() && adapter.getHonorariosAnestesistas().isEmpty()){
					paraRemover.add(adapter);
				}
			}
		}
		procedimentos.removeAll(paraRemover);
	}
	
	private void tocarObjetos(GuiaCompleta<ProcedimentoInterface> guia) {
		guia.getPrestador().getProfissionais().size();
	}
	
	private void segregaHonorariosAnestesistaCirurgiaoEAnestesista(Set<Honorario> honorariosCorrentes) {
		this.honorariosCorrentes.addAll(honorariosCorrentes);
		for (Honorario honorario : honorariosCorrentes) {
			if (honorario.isHonorarioAnestesista()){
				honorariosAnestesistasOrdenados.add(honorario);
			} else {
				honorariosOrdenados.add(honorario);
			}
		}
	}

	public void removeProcedimento(ProcedimentoHonorario procedimento) throws Exception {
		procedimento.mudarSituacao(usuario, SituacaoEnum.CANCELADO.descricao(), "Excluído pelo auditor", new Date());
	}


	public void addProcedimentoGuiaOrigem(ProcedimentoCirurgicoParaAuditoriaDeHonorarios procedimento) throws Exception {
		
		boolean temAnestesita = procedimento.getAnestesistaTemp() != null;
		boolean temAuxiliarAnestesita = procedimento.getAuxiliarAnestesistaTemp() != null;
		
		if (procedimento.getProfissionalResponsavel() != null){
			String descricao = SituacaoEnum.PROCEDIMENTO_COM_HONORARIO_GERADO.descricao();
			String motivo 	 = MotivoEnum.GERACAO_DE_HONORARIO.getMessage();

			procedimento.getSituacao().setDescricao(descricao);
			procedimento.getSituacao().setMotivo(motivo);
		}
		
		GuiaHonorarioPorteAnestesistaValidator.validaProcedimento(temAuxiliarAnestesita, temAnestesita, procedimento);
		
	}

	public void addProcedimento(ProcedimentoHonorario procedimento) throws Exception {
		GeradorGuiaHonorarioInterface guiaGeradora = (GeradorGuiaHonorarioInterface)guiaMae;
		ProcedimentoCirurgicoInterface proc = guiaGeradora.getProcedimentoMaisRecenteRealizadoPeloProfissional(procedimento.getProfissionalResponsavel());
		
		Assert.isNotNull(proc, "O profissional para o qual foi solicitado visita não foi foi responsável por nenhum procedimento cirurgico contido na guia de Origem");
		
		Honorario honorario = proc.getHonorarioExterno(GrauDeParticipacaoEnum.RESPONSAVEL.getCodigo());
		
		for (GuiaHonorarioMedico guiaHonorario: this.getGuiasHonorarioMedico()){
			
			if (guiaHonorario.getHonorariosMedico().contains(honorario)){
				guiaHonorario.addProcedimentoAlteracao(procedimento);
			}
		}
		
		procedimento.aplicaValorAcordo();
	}
	
	/**
	 * @param procedimento
	 */
	public static void tocarObjetosProfissionais(ProcedimentoCirurgicoInterface procedimento){
		Profissional profissionalResponsavel 	= procedimento.getProfissionalResponsavel();
		Profissional profissionalAuxiliar1 		= procedimento.getProfissionalAuxiliar1Temp();
		Profissional profissionalAuxiliar2 		= procedimento.getProfissionalAuxiliar2Temp();
		Profissional profissionalAuxiliar3 		= procedimento.getProfissionalAuxiliar3Temp();
		Profissional anestesista 				= procedimento.getAnestesistaTemp();
		Profissional auxiliarAnestesista 		= procedimento.getAuxiliarAnestesistaTemp();

		if(profissionalResponsavel != null){
			profissionalResponsavel.getPrestadores().size();
		}
		if(profissionalAuxiliar1!= null){
			profissionalAuxiliar1.getPrestadores().size();
		}
		if(profissionalAuxiliar2 != null){
			profissionalAuxiliar2.getPrestadores().size();
		}
		if(profissionalAuxiliar3 != null){
			profissionalAuxiliar3.getPrestadores().size();
		}
		if(anestesista!= null){
			anestesista.getPrestadores().size();
		}
		if(auxiliarAnestesista!= null){
			auxiliarAnestesista.getPrestadores().size();
		}
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	@SuppressWarnings("unchecked")
	public Set<AdapterProcedimento> getProcedimentosExame() {
		Set<AdapterProcedimento> procedimentosExame = new HashSet<AdapterProcedimento>();
		
		for (AdapterProcedimento adapter : procedimentos) {
			if (adapter.getProcedimento().getGuia().isExame()) {
				procedimentosExame.add(adapter);
			}
		}		
		return procedimentosExame;
	}
	
	public Set<AdapterProcedimento> getProcedimentosNaoExame() {
		Set<AdapterProcedimento> procedimentosCirurgicos = new HashSet<AdapterProcedimento>();
		
		for (AdapterProcedimento adapter : procedimentos) {
			if (adapter.getProcedimento().getTipoProcedimento() != ProcedimentoInterface.PROCEDIMENTO_NORMAL) {
				procedimentosCirurgicos.add(adapter);
			}
		}		
		return procedimentosCirurgicos;
	}
	
	public Set<AdapterProcedimento> getProcedimentos() {
		return procedimentos;
	}

	
	public void setProcedimentos(Set<AdapterProcedimento> procedimentos) {
		this.procedimentos = procedimentos;
	}

	
	/** procedimentos cirurgicos com grau diferente de anestesista*/
	public Set<AdapterProcedimento> getProcedimentosCirurgicos() {
		Set<AdapterProcedimento> procedimentos = new HashSet<AdapterProcedimento>();
		
		for(AdapterProcedimento adapter: getProcedimentos()){
			if(!adapter.getHonorarios().isEmpty()){
				procedimentos.add(adapter);
			}
		}
		
		return procedimentos;
	}
	
	
	/** procedimentos cirurgicos com grau anestesista*/
	public Set<AdapterProcedimento> getProcedimentosCirurgicosAnestesicos() {
		Set<AdapterProcedimento> procedimentos = new HashSet<AdapterProcedimento>();
		
		for(AdapterProcedimento adapter: getProcedimentos()){
			if(!adapter.getHonorariosAnestesistas().isEmpty()){
				procedimentos.add(adapter);
			}
		}
		
		return procedimentos;
	}
	
	public Procedimento getProcedimentoCorrente() {
		return procedimentoCorrente;
	}

	public void setProcedimentoCorrente(Procedimento procedimentoCorrente) {
		this.procedimentoCorrente = procedimentoCorrente;
	}

	public Set<Honorario> getHonorariosCorrentes() {
		return honorariosCorrentes;
	}

	public void setHonorariosCorrentes(Set<Honorario> honorariosCorrentes) {
		this.honorariosCorrentes = honorariosCorrentes;
		this.segregaHonorariosAnestesistaCirurgiaoEAnestesista(honorariosCorrentes);
	}

	public Set<Honorario> getHonorariosOrdenados() {
		return honorariosOrdenados;
	}

	public void setHonorariosOrdenados(Set<Honorario> honorariosOrdenados) {
		this.honorariosOrdenados = honorariosOrdenados;
	}

	public Set<Honorario> getHonorariosAnestesistasOrdenados() {
		return honorariosAnestesistasOrdenados;
	}

	public void setHonorariosAnestesistasOrdenados(
			Set<Honorario> honorariosAnestesistasOrdenados) {
		this.honorariosAnestesistasOrdenados = honorariosAnestesistasOrdenados;
	}

	public Set<Honorario> getHonorariosNaoAnestesistaNovos() {
		return honorariosNaoAnestesistaNovos;
	}

	public void setHonorariosNaoAnestesistaNovos(Set<Honorario> honorariosNaoAnestesistaNovos) {
		this.honorariosNaoAnestesistaNovos = honorariosNaoAnestesistaNovos;
	}

	public GuiaSimples getGuiaMae() {
		return guiaMae;
	}
	
	public GuiaCompleta getGuia() {
		return guiaMae;
	}

	public void setGuiaMae(GuiaCompleta guiaMae) {
		this.guiaMae = guiaMae;
	}

	public Set<GuiaHonorarioMedico> getGuiasHonorarioMedico() {
		return guiasHonorarioMedico;
	}

	public Set<GuiaHonorarioMedico> getGuiasHonorariosPacote() {
		Set<GuiaHonorarioMedico> guias = new HashSet<GuiaHonorarioMedico>();
		for (GuiaHonorarioMedico guia : getGuiasHonorarioMedico()) {
			if(!guia.getHonorariosPacote().isEmpty()){
				guias.add(guia);
			}
		}
		return guias;
	}
	
	public Set<GuiaHonorarioMedico> getGuiasHonorariosPacoteGeradasAutomaticamenteDuranteAuditoria() {
		Set<GuiaHonorarioMedico> guias = new HashSet<GuiaHonorarioMedico>();
		
		String situacao = SituacaoEnum.AUDITADO.descricao();
		String motivo = MotivoEnum.GERADO_AUTOMATICAMENTE_DURANTE_AUDITORIA.getMessage();
		
		for (GuiaHonorarioMedico guia : getGuiasHonorarioMedico()) {
			if(guia.getSituacao().getDescricao().equals(situacao) && guia.getSituacao().getMotivo().equals(motivo)){
				guias.add(guia);
			}
		}
		return guias;
	}
	
	public Set<GuiaHonorarioMedico> getGuiasHonorarioMedicoAuditadas() {
		Set<GuiaHonorarioMedico> guiasDeHonorarioMedicoAptasParaMudarSituacao = new HashSet<GuiaHonorarioMedico>();
		for(GuiaHonorarioMedico guia : guiasHonorarioMedico){
			if(!guia.getProcedimentos().isEmpty() && isAuditarProcedimentosClinicos()){
				boolean isTodosProcedimentosClinicosMarcadosParaAuditar = true;
				for(ProcedimentoInterface procedimentoClinico : guia.getProcedimentos()){
					if(!procedimentoClinico.isAuditado()){
						isTodosProcedimentosClinicosMarcadosParaAuditar = false;
					}
				}
				if(isTodosProcedimentosClinicosMarcadosParaAuditar){
					guiasDeHonorarioMedicoAptasParaMudarSituacao.add(guia);
				}
			}
			if(!guia.getHonorariosMedico().isEmpty() && isAuditarProcedimentosCirurgicos()){
				boolean isTodosProcedimentosCirurgicosMarcadosParaAuditar = true;
				for(HonorarioExterno honorario : guia.getHonorariosMedico()){
					if(!honorario.isHonorarioAnestesista()){
						if(!honorario.isHonorarioPacote() && !honorario.getProcedimento().isAuditado() && !honorario.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao())){
							isTodosProcedimentosCirurgicosMarcadosParaAuditar = false;
						}
					}
				}
				if(isTodosProcedimentosCirurgicosMarcadosParaAuditar && !guia.isGuiaHonorarioAnestesista()){
					guiasDeHonorarioMedicoAptasParaMudarSituacao.add(guia);
				}
			}

			if(!guia.getHonorariosMedico().isEmpty() && isAuditarProcedimentosGrauAnestesista()){
				boolean isTodosProcedimentosCirurgicosMarcadosParaAuditar = true;
				for(HonorarioExterno honorario : guia.getHonorariosMedico()){
					if(honorario.isHonorarioAnestesista()){
						if(!honorario.isHonorarioPacote() && !honorario.getProcedimento().isAuditado() && !honorario.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao())){
							isTodosProcedimentosCirurgicosMarcadosParaAuditar = false;
						}
					}
				}
				if(isTodosProcedimentosCirurgicosMarcadosParaAuditar && guia.isGuiaHonorarioAnestesista()){
					guiasDeHonorarioMedicoAptasParaMudarSituacao.add(guia);
				}
			}
			
			if(!guia.getHonorariosPacote().isEmpty() && isAuditarPacotes()){
				guiasDeHonorarioMedicoAptasParaMudarSituacao.add(guia);
			}
		}
		return guiasDeHonorarioMedicoAptasParaMudarSituacao; 
	}
	
	public void addGuiaHonorarioMedico(GuiaHonorarioMedico guiaHonorario) {
		this.autorizacoes.add(guiaHonorario.getAutorizacao());
		guiasHonorarioMedico.add(guiaHonorario);
	}
	
	public void setGuiasHonorarioMedico(Set<GuiaHonorarioMedico> guiasHonorarioMedico) {
		this.guiasHonorarioMedico = guiasHonorarioMedico;
	}
	
	public boolean isAuditarProcedimentosClinicos() {
		return auditarProcedimentosClinicos;
	}

	public void setAuditarProcedimentosClinicos(boolean auditarProcedimentosClinicos) {
		this.auditarProcedimentosClinicos = auditarProcedimentosClinicos;
	}

	public boolean isAuditarProcedimentosCirurgicos() {
		return auditarProcedimentosCirurgicos;
	}

	public void setAuditarProcedimentosCirurgicos(boolean auditarProcedimentosCirurgicos) {
		this.auditarProcedimentosCirurgicos = auditarProcedimentosCirurgicos;
	}

	public boolean isAuditarPacotes() {
		return auditarPacotes;
	}

	public void setAuditarPacotes(boolean auditarPacotes) {
		this.auditarPacotes = auditarPacotes;
	}
	
	public boolean isAuditarProcedimentosGrauAnestesista() {
		return auditarProcedimentosGrauAnestesista;
	}

	public void setAuditarProcedimentosGrauAnestesista(boolean auditarProcedimentosGrauAnestesista) {
		this.auditarProcedimentosGrauAnestesista = auditarProcedimentosGrauAnestesista;
	}

	public void addProcedimentoVisita(ProcedimentoHonorarioAuditor procedimentoVisita) throws Exception{
		Profissional profissionalResponsavel = procedimentoVisita.getProfissionalResponsavel();
		Assert.isNotNull(profissionalResponsavel, MensagemErroEnum.PROFISSIONAL_NAO_INFORMADO.getMessage());
		tocarObjetos(profissionalResponsavel);
		procedimentoVisita.calcularCampos();
		this.procedimentosVisitaNovos.add(procedimentoVisita);
	}

	private void tocarObjetos(Profissional profissionalResponsavel) {
		profissionalResponsavel.getPrestadores().size();
		
		Prestador prestadorProprio = profissionalResponsavel.getPrestadorProprioPeloCPF();
		if(prestadorProprio == null){
			prestadorProprio = profissionalResponsavel.getPrestadorProprio();
		}
		
		if(prestadorProprio !=null){
			prestadorProprio.tocarObjetos();
		}
	}
	
	public void removeProcedimentoVisita(ProcedimentoHonorarioAuditor procdimentoVisita){
		procdimentoVisita.mudarSituacao(usuario, SituacaoEnum.CANCELADO.descricao(), "Excluído pelo auditor", new Date());
	}

	public Set<ProcedimentoHonorario> getProcedimentosVisitaAtuais() {
		return procedimentosVisitaAtuais;
	}

	public void setProcedimentosVisitaAtuais(Set<ProcedimentoHonorario> procedimentosVisitaExistentes) {
		this.procedimentosVisitaAtuais = procedimentosVisitaExistentes;
	}

	public Set<ProcedimentoHonorario> getProcedimentosVisitaNovos() {
		return procedimentosVisitaNovos;
	}

	public void setProcedimentosVisitaNovos(Set<ProcedimentoHonorario> procedimentosVisitaNovos) {
		this.procedimentosVisitaNovos = procedimentosVisitaNovos;
	}

	public Set<AdapterHonorario> getHonorariosPacote() {
		return honorariosPacote;
	}

	public void setHonorariosPacote(Set<AdapterHonorario> honorariosPacote) {
		this.honorariosPacote = honorariosPacote;
	}

	/**
	 * Metodo que retorna adapterProcedimentosTela
	 * É Transiente e é usado apenas para validação
	 * @return
	 */
	public List<AdapterProcedimento> getAdapterProcedimentosTela() {
		return adapterProcedimentosTela;
	}

	public void setAdapterProcedimentosTela(List<AdapterProcedimento> adapterProcedimentosTela) {
		this.adapterProcedimentosTela = adapterProcedimentosTela;
	}

	/**
	 * Metodo que retorna itensPacoteAuditoriaTela
	 * É Transiente e é usado apenas para validação
	 * @return
	 */
	public List<ItemPacoteHonorarioAuditoria> getItensPacoteAuditoriaTela() {
		return itensPacoteAuditoriaTela;
	}

	public void setItensPacoteAuditoriaTela(List<ItemPacoteHonorarioAuditoria> itensPacoteAuditoriaTela) {
		this.itensPacoteAuditoriaTela = itensPacoteAuditoriaTela;
	}
	
	public void resetFlags(){
		auditarProcedimentosCirurgicos = auditarProcedimentosClinicos = auditarProcedimentosGrauAnestesista = auditarPacotes = false;
	}

	public Set<ProcedimentoHonorarioLayer> getProcedimentosLayer() {
		return procedimentosLayer;
	}

	public void setProcedimentosLayer(Set<ProcedimentoHonorarioLayer> procedimentosLayer) {
		this.procedimentosLayer = procedimentosLayer;
	}
}
