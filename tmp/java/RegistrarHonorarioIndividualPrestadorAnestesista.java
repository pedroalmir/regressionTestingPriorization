package br.com.infowaypi.ecare.services.honorarios;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.services.honorarios.validators.GuiaHonorarioDataDeRealizacaoProcedimentoCirurgicoValidator;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GeradorGuiaHonorarioInterface;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GuiaHonorarioMedico;
import br.com.infowaypi.ecarebc.atendimentos.honorario.HonorarioExterno;
import br.com.infowaypi.ecarebc.atendimentos.validators.GuiaHonorarioDataProcedimentoCirurgicoValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.GuiaHonorarioHonorarioDuplicado;
import br.com.infowaypi.ecarebc.atendimentos.validators.GuiaHonorarioMarcouOuInseriuPeloMenosUmProcedimentoValidate;
import br.com.infowaypi.ecarebc.atendimentos.validators.GuiaHonorarioPorteAnestesistaValidator;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoHonorario;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

@SuppressWarnings("rawtypes")
public class RegistrarHonorarioIndividualPrestadorAnestesista extends Service{

	private static List<String> situacoes = Arrays.asList(SituacaoEnum.ABERTO.descricao(), SituacaoEnum.SOLICITADO_PRORROGACAO.descricao(), 
															SituacaoEnum.PRORROGADO.descricao(), SituacaoEnum.NAO_PRORROGADO.descricao(), 
															SituacaoEnum.ALTA_REGISTRADA.descricao(), SituacaoEnum.FECHADO.descricao(), 
															SituacaoEnum.ENVIADO.descricao(), SituacaoEnum.RECEBIDO.descricao(), 
															SituacaoEnum.AUDITADO.descricao(),SituacaoEnum.CONFIRMADO.descricao(),
															SituacaoEnum.AUTORIZADO.descricao(),SituacaoEnum.FATURADA.descricao(),SituacaoEnum.PAGO.descricao());
					
	public GeradorGuiaHonorarioInterface buscarGuia(String autorizacao, Prestador prestador) {
		GeradorGuiaHonorarioInterface guiaComHonorario = null;
		prestador.tocarObjetos();
		
		SearchAgent sa = new SearchAgent();
		
		Criteria crit = sa.createCriteriaFor(GuiaCompleta.class);
		
		GuiaCompleta guia = (GuiaCompleta) crit.add(Expression.eq("autorizacao", autorizacao))
												.add(Expression.in("situacao.descricao", situacoes))
												.uniqueResult();
		
		Assert.isNotNull(guia, "Nenhuma guia foi encontrada.");
			
		if (guia instanceof GeradorGuiaHonorarioInterface) {
			guia.tocarObjetos();
				
			guiaComHonorario = (GeradorGuiaHonorarioInterface) guia;
			
			Set<ProcedimentoInterface> procedimentos = guiaComHonorario.getProcedimentosQueAindaPodemGerarHonorariosAnestesitas();

			verificaSeHaProcedimentosComPorteAnestesicoNaGuia(procedimentos);
						
			return guiaComHonorario;
		}
		
		tocarObjetos(guia);
		throw new RuntimeException("Guia não encontrada.");
		
	}

	/**
	 * Tocar objetos específico do fluxo.
	 * @author Luciano Rocha
	 * @param guia
	 */
	public void tocarObjetos(GuiaCompleta guia) {
		if (guia.getGuiaRecursoGlosa() != null) {
			guia.getGuiaRecursoGlosa();
			guia.getGuiaRecursoGlosa().getItensRecurso().size();
		}
	}
	
	private void verificaSeHaProcedimentosComPorteAnestesicoNaGuia(Set<ProcedimentoInterface> procedimentos) {
		Assert.isNotEmpty(procedimentos, MensagemErroEnumSR.NAO_HA_PROCEDIMENTOS_APTOS_PARA_GERAR_HONORARIOS.getMessage());
	}

	public GuiaHonorarioMedico informarHonorarios(Prestador prestador,	Profissional profissional, int grauDeParticipacao,
													GeradorGuiaHonorarioInterface guiaOrigem, 
													GeradorGuiaHonorarioInterface guiaOrigemDuplicadaNaoUsadaNoMetodo, 
													String registro, UsuarioInterface usuario) throws Exception {
		GuiaCompleta guiaCompleta = (GuiaCompleta) guiaOrigem;
		AbstractSegurado segurado = guiaCompleta.getSegurado();

		validaInformacoesFornecidas(profissional, grauDeParticipacao, guiaOrigem, segurado, prestador, null);
		GuiaHonorarioMedico guiaHonorario = criarGuiaHonorario(profissional, grauDeParticipacao, guiaOrigem, usuario, segurado, prestador);
		guiaHonorario.setNumeroDeRegistro(registro);
		
		guiaHonorario.recalcularValores();
		tocarObjetos(guiaHonorario);

		return guiaHonorario;
	}
	
	private  void tocarObjetos(GuiaHonorarioMedico guiaHonorario){
		guiaHonorario.getHonorariosMedico();
		guiaHonorario.getPrestador().tocarObjetos();
	}

	private GuiaHonorarioMedico criarGuiaHonorario(Profissional medico,	int grauDeParticipacao, GeradorGuiaHonorarioInterface guiaOrigem,
													UsuarioInterface usuario, AbstractSegurado segurado, Prestador prestador) 
													throws Exception {

		GuiaHonorarioMedico guiaHonorario = new GuiaHonorarioMedico();
		GuiaSimples guiaSimples = (GuiaSimples) guiaOrigem;
		Set<ProcedimentoInterface> procedimentosComHonorariosMedicos = guiaOrigem.getProcedimentosQueVaoGerarHonorarioAnestesista();

		guiaHonorario.setProfissional(medico);
		guiaHonorario.setGrauDeParticipacao(grauDeParticipacao);
		guiaHonorario.setGuiaOrigem(guiaSimples);
		guiaHonorario.setSegurado(segurado);
		guiaHonorario.setPrestador(prestador);

		guiaHonorario.mudarSituacao(usuario, SituacaoEnum.FECHADO.descricao(), "Geração de honorário", new Date());
		
		for (ProcedimentoInterface procedimento : procedimentosComHonorariosMedicos) {
			guiaHonorario.addHonorario(usuario, procedimento);
		}
		
		if(!guiaSimples.isExame()){
			final Date dataRealizacaoProcedimentoMaisAntigo = getDataRealizacaoProcedimentoMaisAntigo(guiaHonorario);
			guiaHonorario.setDataTerminoAtendimento(dataRealizacaoProcedimentoMaisAntigo);
			guiaHonorario.setDataAtendimento(dataRealizacaoProcedimentoMaisAntigo);
		} else {
			Date data = guiaSimples.getDataAtendimento() != null? guiaSimples.getDataAtendimento() : guiaSimples.getDataAutorizacao();
			guiaHonorario.setDataTerminoAtendimento(data);
			guiaHonorario.setDataAtendimento(data);
		}
		
		guiaSimples.addGuiaFilha(guiaHonorario);

		return guiaHonorario;
	}

	/**
	 * 
	 * @param guia
	 * @return
	 */
	private Date getDataRealizacaoProcedimentoMaisAntigo(GuiaHonorarioMedico guia) {
		Date dataRealizacao = new Date();
		boolean existeProcedimentoNormal = false;
		for (HonorarioExterno honorario : guia.getHonorariosMedico()) {
			ProcedimentoCirurgico procedimento;
			if (honorario.getProcedimento() instanceof ProcedimentoCirurgico){
				procedimento = (ProcedimentoCirurgico) honorario.getProcedimento();
			} else {
				existeProcedimentoNormal = true;
				continue;
			}
			if(Utils.compareData(procedimento.getDataRealizacao(), dataRealizacao) < 0){
				dataRealizacao = procedimento.getDataRealizacao();
			}
		}
		
		if(existeProcedimentoNormal && (Utils.compareData(dataRealizacao, new Date()) == 0)){
			return null;
		}
		return dataRealizacao; 
	}

	private void validaInformacoesFornecidas(Profissional profissional,	int grauDeParticipacao, GeradorGuiaHonorarioInterface guiaOrigem,
												AbstractSegurado segurado, Prestador prestador, 
												Collection<ProcedimentoHonorario> procedimentosOutros) throws Exception {
		
		Set<ProcedimentoInterface> procedimentos = guiaOrigem.getProcedimentosQueVaoGerarHonorarioAnestesista();
		
		GuiaHonorarioPorteAnestesistaValidator.validate(procedimentos, grauDeParticipacao);
		GuiaHonorarioDataProcedimentoCirurgicoValidator.validate(procedimentos);
		
		GuiaHonorarioMarcouOuInseriuPeloMenosUmProcedimentoValidate.validate(prestador, procedimentosOutros, procedimentos);
		GuiaHonorarioHonorarioDuplicado.validate(procedimentos, grauDeParticipacao);
		
		GuiaHonorarioDataDeRealizacaoProcedimentoCirurgicoValidator dataDeRealizacaoValidator = new GuiaHonorarioDataDeRealizacaoProcedimentoCirurgicoValidator();
		dataDeRealizacaoValidator.setGuiaOrigem(guiaOrigem);
		dataDeRealizacaoValidator.execute();
	}
	
	public GuiaHonorarioMedico conferirDados(GuiaHonorarioMedico guia) throws Exception {
		super.salvarGuia(guia);
		ImplDAO.save(guia.getGuiaOrigem());
		return guia;
	}
	
}
