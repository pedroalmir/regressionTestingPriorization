package br.com.infowaypi.ecarebc.service.exames;

import static br.com.infowaypi.msr.utils.Assert.isNotEmpty;
import static br.com.infowaypi.msr.utils.Assert.isNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Prestador.TipoIncremento;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.atendimentos.validators.TetoExamesPrestadorValidator;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.ecarebc.procedimentos.validators.AbstractProcedimentoValidator;
import br.com.infowaypi.ecarebc.procedimentos.validators.ProcedimentosAssociadosValidator;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Service para a confirmação de exames marcados pelo auditor do plano de saúde
 * @author root
 */
public class ConfirmacaoExameService extends Service {
	
	public static Collection<AbstractProcedimentoValidator> procedimentoConfirmacaoValidators = new ArrayList<AbstractProcedimentoValidator>();
	static{
		procedimentoConfirmacaoValidators.add(new ProcedimentosAssociadosValidator());
	}
	
	public ConfirmacaoExameService(){
		super();
	}
	
	public ResumoGuias<GuiaExame> buscarGuiasConfirmacao(SeguradoInterface segurado,String dataInicial, String dataFinal, Prestador prestador) throws Exception{
		List<GuiaExame> guias = super.buscarGuias(dataInicial,dataFinal,segurado, prestador,true, GuiaExame.class, SituacaoEnum.AGENDADA);
		isNotEmpty(guias, "Não existem guias desse segurado para serem confirmadas!");
		return new ResumoGuias<GuiaExame>(guias, ResumoGuias.SITUACAO_TODAS, false);
	}

	
	public GuiaExame buscarGuiasConfirmacao(String autorizacao, Prestador prestador) throws Exception{
		if (Utils.isStringVazia(autorizacao))
			throw new ValidateException(MensagemErroEnum.NUMERO_AUTORIZACAO_INVALIDO.getMessage());
		GuiaExame guia = super.buscarGuias(autorizacao,prestador,true, GuiaExame.class, SituacaoEnum.AGENDADA);
		Assert.isNotNull(guia, "Guia não encontrada!");
		guia.tocarObjetos();
		return guia;
	}
	
	@Override
	protected <G extends GuiaSimples> G selecionarGuia(G guia) throws Exception {
		if(guia.getProcedimentosNaoRealizadosNaoNaoAutorizadosENaoSolicitados().isEmpty())
			throw new RuntimeException("Prezado(a) usuario(a), não existem procedimentos nessa guia para serem confirmados.");
		return super.selecionarGuia(guia);
	}
	
	public GuiaExame<Procedimento> selecionarProcedimentos(Collection<Procedimento> procedimentos, 
			GuiaExame<Procedimento> guiaAntiga, UsuarioInterface usuario, Prestador prestador) throws Exception {
		
		guiaAntiga.tocarObjetos();
		prestador.tocarObjetos();
		
		isNotEmpty(procedimentos, "Selecione um ou mais exames.");
		
		GuiaExame<Procedimento> guiaNova = new GuiaExame<Procedimento>(usuario);
		guiaNova.setSegurado(guiaAntiga.getSegurado());
		guiaNova.setSolicitante(guiaAntiga.getSolicitante());
		guiaNova.getSituacao().setUsuario(guiaAntiga.getSituacao().getUsuario());
		guiaNova.getSituacao().setDataSituacao(guiaAntiga.getSituacao().getDataSituacao());
		guiaNova.getSituacao().setDescricao(guiaAntiga.getSituacao().getDescricao());
		guiaNova.getSituacao().setMotivo(guiaAntiga.getSituacao().getMotivo());
		guiaNova.setPrestador(prestador);
		guiaNova.setSolicitadoComPrestador(guiaAntiga.isSolicitadoComPrestador());
		guiaNova.setFromPrestador(guiaAntiga.isFromPrestador());
		guiaNova.setEspecial(guiaAntiga.isEspecial());
		guiaNova.setGuiaOrigem(guiaAntiga);
		guiaNova.setExameExterno(guiaAntiga.isExameExterno());
		guiaNova.setDataAtendimento(new Date());
		guiaNova.setDataTerminoAtendimento(new Date());
		guiaNova.setDataMarcacao(guiaAntiga.getDataMarcacao());
		guiaNova.setLiberadaForaDoLimite(guiaAntiga.getLiberadaForaDoLimite());
		
		for (ProcedimentoInterface procedimento : procedimentos) {
			if(GuiaExame.CODIGOS_CBHPM_ANESTESISTA.contains(procedimento.getProcedimentoDaTabelaCBHPM().getCodigo()) && !prestador.isPrestadorAnestesista())
				throw new RuntimeException("O exame de código "+procedimento.getProcedimentoDaTabelaCBHPM().getCodigo()+" pode ser confirmado apenas por prestadores do tipo ANESTESISTA.");
			
			Procedimento novoProcedimento = new Procedimento(usuario);
			novoProcedimento.setValorAtualDoProcedimento(procedimento.getValorAtualDoProcedimento());
			novoProcedimento.setProcedimentoDaTabelaCBHPM(procedimento.getProcedimentoDaTabelaCBHPM());
			novoProcedimento.setValorAtualDaModeracao(procedimento.getValorAtualDaModeracao());
			novoProcedimento.setBilateral(procedimento.getBilateral());
			
			novoProcedimento.setGuia(guiaNova);	
			novoProcedimento.setQuantidade(procedimento.getQuantidade());
			novoProcedimento.setValorCoParticipacao(procedimento.getValorCoParticipacao());
			novoProcedimento.mudarSituacao(usuario, SituacaoEnum.CONFIRMADO.descricao(), MotivoEnum.CONFIRMADA_NO_PRESTADOR.getMessage(), new Date());
			guiaAntiga.getSegurado().atualizarLimites(guiaAntiga, TipoIncremento.NEGATIVO, procedimento.getQuantidade());

			guiaNova.addProcedimento(novoProcedimento);
			novoProcedimento.aplicaValorAcordo();
			
			for (AbstractProcedimentoValidator validator : procedimentoConfirmacaoValidators) {
				validator.execute(novoProcedimento, guiaNova);
			}
		}
		
		guiaNova.tocarObjetos();
		new TetoExamesPrestadorValidator().execute(guiaNova);
		guiaNova.recalcularValores();
		guiaNova.setValorAnterior(guiaNova.getValorTotal());
		
		return guiaNova;		
	}
	
	public void confirmarGuiaDeExame(GuiaExame<Procedimento> guiaAntiga, GuiaExame<Procedimento> guiaNova, UsuarioInterface usuario) throws Exception {
		isNotNull(guiaNova, "A guia de confirmação não foi criada!");
		isNotNull(guiaAntiga, "Guia de agendamento invalida!");
		
		guiaAntiga.tocarObjetos();
		guiaNova.tocarObjetos();

		guiaAntiga.setValorAnterior(guiaAntiga.getValorTotal());
		
		for (TabelaCBHPM cbhpm : guiaNova.getProcedimentosDaTabelaCBHPM()) 
			atualizarProcedimentos(guiaAntiga, cbhpm,  usuario);
		
		if (guiaNova.isPermiteMatMed()) {
			guiaNova.mudarSituacao(usuario, SituacaoEnum.ABERTO.descricao(), MotivoEnum.GUIA_EXAME_AGUARDANDO_FECHAMENTO.getMessage(), new Date());
		} else { 
			guiaNova.mudarSituacao(usuario, SituacaoEnum.CONFIRMADO.descricao(), MotivoEnum.CONFIRMADA_NO_PRESTADOR.getMessage(), new Date());
		}
		
		boolean mudarSituacao = true;
		for (ProcedimentoInterface procedimento : guiaAntiga.getProcedimentos()) {
			if(procedimento != null && (procedimento.isSituacaoAtual(SituacaoEnum.AGENDADA.descricao()) || procedimento.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao()) || procedimento.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao()))) {
				mudarSituacao = false;
				break;
			}
		}
		
		if (mudarSituacao){
			guiaAntiga.mudarSituacao(usuario, SituacaoEnum.REALIZADO.descricao(), MotivoEnum.GUIA_REALIZADA.getMessage(), new Date());	
		}
		
		guiaNova.setDataAtendimento(new Date());
		
		ImplDAO.save(guiaAntiga.getSegurado().getConsumoIndividual());
		super.salvarGuia(guiaNova);
		ImplDAO.save(guiaAntiga);
		
	}
	
	
	public void salvarGuia(GuiaSimples guia) throws Exception {
		super.salvarGuia(guia);
	}
	
	
}