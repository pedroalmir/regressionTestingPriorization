/**
 * 
 */
package br.com.infowaypi.ecare.services;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.procedimentos.ProcedimentoCirurgicoSR;
import br.com.infowaypi.ecarebc.atendimentos.Critica;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.enums.TipoCriticaEnum;
import br.com.infowaypi.ecarebc.atendimentos.validators.ManagerCritica;
import br.com.infowaypi.ecarebc.atendimentos.validators.ServiceApresentacaoCriticasFiltradas;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.service.AlteracaoDeProcedimentosService;
import br.com.infowaypi.ecarebc.utils.ProcedimentoUtils;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.MoneyCalculation;

/**
 * Service para a alteração de procedimentos de uma guia do sistema.
 * @author Marcus bOolean
 * @changes Danilo Portela
 */
 @SuppressWarnings({"unchecked","rawtypes"})
public class AlteracaoDeProcedimentos extends AlteracaoDeProcedimentosService implements ServiceApresentacaoCriticasFiltradas{
	
	public GuiaSimples buscarGuiaAlteracaoDeProcedimento(String autorizacao, UsuarioInterface usuario) throws Exception {
		GuiaSimples guia = super.buscarGuiaCancelamentoDeProcedimento(autorizacao, usuario);
		filtrarCriticasApresentaveis(guia);
		return guia;
	}
	/**
	 * Seta qual a porcentamgem a ser paga pelo procedimento em caso de a guia ser uma guia de Cirúrgia Odontológica
	 */
	@Override
	public <P extends ProcedimentoInterface> GuiaSimples alterarProcedimentos(GuiaSimples guia, UsuarioInterface usuario) throws Exception {
		
		if (guia.isExame() && !guia.isExameOdonto()){
			validaGuiaVazia(guia);
		}
		
		if(guia.isCirurgiaOdonto()){
			ProcedimentoUtils.aplicaDescontoDaViaDeAcesso((GuiaCompleta<ProcedimentoInterface>) guia); 
		}
		super.alterarProcedimentos(guia, usuario);
		
		if (guia.isExame() && !guia.isPermiteMatMed() && guia.isAberta()){
			guia.mudarSituacao(usuario, SituacaoEnum.CONFIRMADO.descricao(), "Guia não comporta materiais/medicamentos após alteração de procedimentos.", new Date());
		}
		
		validarProcedimentosInseridosNoFluxo(guia);
		
		filtrarCriticasApresentaveis(guia);

		processaSituacaoCriticasEspecificasProcedimentosExcluidosNoFluxo(guia);
		
		return guia;
	}
	
	private void validarProcedimentosInseridosNoFluxo(GuiaSimples guia)	throws Exception {
		/**
		 * Trecho usado para validar especificamente os procedimentos inseridos neste fluxo, evitando assim chamar o validate da guia.
		 */
		for(Iterator<Procedimento> it = guia.getProcedimentosValidosParaConsumo().iterator(); it.hasNext();){
			Procedimento procedimento = it.next();
			if(procedimento.getIdProcedimento() == null){
				procedimento.validate(guia);
			}
		}
	}
	
	private void processaSituacaoCriticasEspecificasProcedimentosExcluidosNoFluxo(GuiaSimples guia){
		/**
		 * Código usado para processar a situação das críticas geradas pelos procedimentos que foram excluidos neste fluxo.
		 */
		for(Iterator<Procedimento> it = guia.getProcedimentos(SituacaoEnum.CANCELADO.descricao()).iterator(); it.hasNext();){
			Procedimento procedimento = it.next();
			boolean isMotivoExclusao = procedimento.getSituacao().getMotivo().equals(MotivoEnum.EXCLUSAO_EXAME_PROCEDIMENTO.getMessage());
			
			if(isMotivoExclusao){
				processaSituacao(guia, procedimento);
			}
		}
	}
	
	private void processaSituacao(GuiaSimples guia, ProcedimentoInterface procedimento){
		
		for(Iterator<Critica> it = guia.getCriticas().iterator(); it.hasNext();){
			Critica critica = it.next();
			
			boolean criticaDoFluxo = critica.getIdCritica() == null;
			boolean procedimentoExcluidoNoFluxo = procedimento != null && critica.getOrigem().equals(procedimento);
			
			if(criticaDoFluxo || procedimentoExcluidoNoFluxo){
				critica.setAvaliada(true);
				if(!procedimentoExcluidoNoFluxo){
					critica.setAutorizada(true);
				}else{
					critica.setAutorizada(false);
				}
			}
		}
	}
	
	private void validaGuiaVazia(GuiaSimples guia) throws ValidateException {
		boolean temProcedimentos = !guia.getProcedimentosNaoRealizadosNaoNaoAutorizadosENaoSolicitados().isEmpty();
		boolean temPacotes = !(((GuiaCompleta)guia).getItensPacoteNaoCanceladosENegados()).isEmpty(); 
		boolean guiaEstaZerada = MoneyCalculation.compare(guia.getValorTotal(), BigDecimal.ZERO) == 0;
		
		if (!temProcedimentos && !temPacotes && guiaEstaZerada){
			throw new ValidateException(MensagemErroEnumSR.NENHUM_PROCEDIMENTO_PACOTE_NA_GUIA.getMessage());
		}
	}

	public <P extends ProcedimentoInterface> GuiaSimples alterarProcedimentos(GuiaSimples guia, Collection<ProcedimentoCirurgicoSR> procedimentosInsert, UsuarioInterface usuario) throws Exception {
		for (ProcedimentoCirurgicoSR procedimentoCirurgicoSR : procedimentosInsert) {
			guia.addProcedimento(procedimentoCirurgicoSR);
		}
		
		return this.alterarProcedimentos(guia, usuario);
	}
	
	@Override
	public void filtrarCriticasApresentaveis(GuiaSimples<?> guia) {
		ManagerCritica.processaApresentaveis(guia, TipoCriticaEnum.CRITICA_DLP_SUBGRUPO.valor(), TipoCriticaEnum.CRITICA_DLP_CID.valor());
	}
	
	@Override
	public void processaSituacaoCriticas(GuiaSimples<?> guia) {
		ManagerCritica.processaSituacao(guia);
	}
}
