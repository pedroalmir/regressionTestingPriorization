package br.com.infowaypi.ecare.services;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import br.com.infowaypi.ecare.cadastros.MotivoGlosa;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaAtendimentoUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ManagerGuia;
import br.com.infowaypi.ecarebc.atendimentos.RegistroTecnicoDaAuditoria;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.atendimentos.validators.GuiaPacotePorcentagemValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.GuiaProcedimentoPorcentagemValidator;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.service.autorizacoes.AuditarGuiaService;
import br.com.infowaypi.ecarebc.utils.CommandCorrecaoCalculoValorProcedimento;
import br.com.infowaypi.msr.user.UsuarioInterface;

@SuppressWarnings({"unchecked", "rawtypes"})
public class AuditarGuias extends AuditarGuiaService {
	
	public ResumoGuias<GuiaCompleta> buscarGuias(String autorizacao, String dataInicial, String dataFinal,Prestador prestador) throws Exception {
		return super.buscarGuias(autorizacao, dataInicial, dataFinal, prestador);
	}
	
	@Override
	public GuiaSimples auditarGuia(Boolean glosarTotalmente, MotivoGlosa motivoGlosa, String observacaoMotivoDeGlosa, GuiaCompleta guia, UsuarioInterface usuario)
			throws Exception {
		
		atualizarValoresDoProcedimentoDeConsulta(guia);
		
		GuiaSimples guiaAuditada = super.auditarGuia(glosarTotalmente, motivoGlosa, observacaoMotivoDeGlosa, guia, usuario);

		guiaAuditada.calculaHonorariosInternos(usuario);
		guiaAuditada.updateValorCoparticipacao();
		guiaAuditada.recalcularValores();
		guiaAuditada.updateValorCoparticipacao();
		
		CommandCorrecaoCalculoValorProcedimento cmd = new CommandCorrecaoCalculoValorProcedimento(guiaAuditada);
		cmd.execute();
		
		return guiaAuditada;
	}

	private void atualizarValoresDoProcedimentoDeConsulta(GuiaCompleta guia) {
		ProcedimentoInterface procedimentoConsultaNaoCancelado = guia.getProcedimentoConsultaNaoCancelado();
		if(procedimentoConsultaNaoCancelado != null){
			procedimentoConsultaNaoCancelado.setValorAtualDoProcedimento(BigDecimal.ZERO);
			procedimentoConsultaNaoCancelado.calcularCampos();
			procedimentoConsultaNaoCancelado.calculaCoParticipacao();
			procedimentoConsultaNaoCancelado.aplicaValorAcordo();
		}
	}
	
	/**
	 * Método usado para os casos em que ñ exista procedimentos cirurgicos, alteração nescessária por conta do uso do update-component-collection
	 */
	public GuiaSimples auditarGuia(Boolean glosarTotalmente, MotivoGlosa motivoDeGlosa, String observacaoMotivoDeGlosa,
			Collection<RegistroTecnicoDaAuditoria> registros, GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario) throws Exception {
		return auditarGuia(false, glosarTotalmente, motivoDeGlosa, observacaoMotivoDeGlosa, registros, guia, usuario);
	}
	
	public GuiaSimples auditarGuia(boolean valorar, Boolean glosarTotalmente, MotivoGlosa motivoDeGlosa, String observacaoMotivoDeGlosa,
			Collection<RegistroTecnicoDaAuditoria> registros, GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario) throws Exception {
		if (guia instanceof GuiaAtendimentoUrgencia) {
			((GuiaAtendimentoUrgencia)guia).setValorada(valorar);
		}
		return auditarGuia(glosarTotalmente, motivoDeGlosa, observacaoMotivoDeGlosa, registros, null, guia, usuario);
	}
	
	/*
	 * A diferença desse pro anterior é que esse insere registros técnicos de auditoria.
	 * Foram criados 2 metodos p o fluxo usa restriction no mapeamento
	 */
	public GuiaSimples auditarGuia(Boolean glosarTotalmente, MotivoGlosa motivoDeGlosa, String observacaoMotivoDeGlosa,
			Collection<RegistroTecnicoDaAuditoria> registros, Collection<Procedimento> procedimentosCirurgicos,
			GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario)
	throws Exception {
		return auditarGuia(false, glosarTotalmente, motivoDeGlosa, observacaoMotivoDeGlosa,  registros, procedimentosCirurgicos, guia, usuario);
	}
	public GuiaSimples auditarGuia(boolean valorar, Boolean glosarTotalmente,
			MotivoGlosa motivoGlosa,
			String observacaoMotivoDeGlosa,
			Collection<RegistroTecnicoDaAuditoria> registros,
			Collection<Procedimento> procedimentosCirurgicos,
			GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario) throws Exception {

		if (guia.isInternacao() || guia.isUrgencia()) {
			guia.addRegistrosTecnicosDaAuditoria(registros, usuario);
		}
			
		alterarEspecialidade(guia, usuario);
		
		Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();
		for(ProcedimentoCirurgicoLayer layer : guia.getLayerProcedimentos()) {
			ProcedimentoCirurgico procedimentoCirurgicoNovo = layer.getProcedimentoCirurgicoNovo();
			if(!layer.isGlosar()){
				guia.validacaoDataDeRealizacaoDoProcedimentoCirurgicoNaAuditoria(procedimentoCirurgicoNovo);
			}
			procedimentos.add(procedimentoCirurgicoNovo);
			guia.atualizaValorApresentado(procedimentoCirurgicoNovo.getValorItem());
		}
		
		GuiaProcedimentoPorcentagemValidator.verificaPercentualDeReducao(ManagerGuia.getProcedimentosAceitosPelaAuditoria(guia),
				guia.isPossuiProcedimentoAutorizadoA100Porcento());
		
		new GuiaPacotePorcentagemValidator().execute(guia);
		
		CommandCorrecaoCalculoValorProcedimento cmd = new CommandCorrecaoCalculoValorProcedimento(guia);
		cmd.execute();
	
		return this.auditarGuia(glosarTotalmente, motivoGlosa, observacaoMotivoDeGlosa, guia, usuario);
	}
	
	/**
	 * Caso haja mudança de especialidade durante a auditoria, o método muda a especialidade 
	 * e adiciona à guia um registro técnico da auditoria descrevendo a mudança de especialidade 
	 */
	private void alterarEspecialidade(GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario){
		if(guia.getEspecialidadeTemp()!= null && guia.getEspecialidade() != null && !guia.getEspecialidade().equals(guia.getEspecialidadeTemp())){
			RegistroTecnicoDaAuditoria registro = new RegistroTecnicoDaAuditoria(
							"Mudança de especialidade pela auditoria de "
							+ guia.getEspecialidade().getDescricao() + " para "
							+ guia.getEspecialidadeTemp().getDescricao(), usuario);
			registro.setGuia(guia);
			registro.setTitulo("Mudança de especialidade");
			guia.getRegistrosTecnicosDaAuditoria().add(registro);
			guia.setEspecialidade(guia.getEspecialidadeTemp());
		}
	}
	
	public void salvarGuia(GuiaCompleta<ProcedimentoInterface> guia) throws Exception {
		guia.recalcularValores();
		super.salvarGuia(guia);
	}
	
	
}
