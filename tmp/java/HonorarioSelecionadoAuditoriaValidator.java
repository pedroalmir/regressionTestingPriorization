package br.com.infowaypi.ecare.services.honorariomedico;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import br.com.infowaypi.ecare.resumos.ResumoGuiasHonorarioMedico;
import br.com.infowaypi.ecare.services.honorariomedico.adapter.AdapterHonorario;
import br.com.infowaypi.ecare.services.honorariomedico.adapter.AdapterProcedimento;
import br.com.infowaypi.ecarebc.associados.validators.AbstractValidator;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacoteHonorarioAuditoria;
import br.com.infowaypi.ecarebc.atendimentos.alta.AltaHospitalar;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GuiaHonorarioMedico;
import br.com.infowaypi.ecarebc.atendimentos.honorario.HonorarioExterno;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoHonorario;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoHonorarioLayer;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Validações do flow @AuditarGuiaHonorarioMedico <br>
 * Executa as verificações pertinentes a alterações nas GHM realizadas durante a auditoria <br>
 * chamad no step 3: auditarGuia.
 * @author Emanuel
 *
 */
@SuppressWarnings("rawtypes")
public class HonorarioSelecionadoAuditoriaValidator extends AbstractValidator<ResumoGuiasHonorarioMedico>{
	
	private static final BigDecimal PORCENTAGEM_100 = new BigDecimal(100).setScale(2, BigDecimal.ROUND_HALF_UP);
	
	@Override
	protected boolean templateValidator(ResumoGuiasHonorarioMedico resumo) throws ValidateException {
		Collection<AdapterProcedimento> adapterProcedimentos = resumo.getAdapterProcedimentosTela();
		Collection<ItemPacoteHonorarioAuditoria> itensPacoteAuditoria = resumo.getItensPacoteAuditoriaTela();
		GuiaSimples guiaMae 		= resumo.getGuiaMae();
		Date dataAtendimentoGuia 	= guiaMae.getDataAtendimento();
		
//		Assert.isTrue(isPeloMenosUmHonorarioMarcadoParaAuditar(resumo, itensPacoteAuditoria), "Para proseguir, é nescessário marcar pelo menos um procedimento ou pacote para auditar.");
		
		isHonorarioAuditadoParaNaoAuditado(resumo);
		
		if(resumo.isAuditarProcedimentosCirurgicos()){
			Assert.isTrue(isPeloMenosUmProcedimentoCirurgicoMarcadoParaAuditar(resumo.getProcedimentos()), "Para proseguir, é nescessário marcar pelo menos um procedimento cirúrgico para auditar.");
		}
		
		if(resumo.isAuditarProcedimentosGrauAnestesista()){
			Assert.isTrue(isPeloMenosUmProcedimentoCirurgicoMarcadoParaAuditar(resumo.getProcedimentosCirurgicosAnestesicos()), "Para proseguir, é nescessário marcar pelo menos um procedimento para auditar.");
		}
		
		if(resumo.isAuditarProcedimentosClinicos()){
			Assert.isTrue(isPeloMenosUmProcedimentoClinicoMarcadoParaAuditar(resumo.getProcedimentosLayer()), "Para proseguir, é nescessário marcar pelo menos um procedimento clínico para auditar.");
		}
		
		if(resumo.isAuditarPacotes()){
			Assert.isTrue(isPeloMenosUmPacoteParaAuditar(resumo, itensPacoteAuditoria), "Para proseguir, é nescessário auditar pelo menos um pacote.");
		}
		
		if(resumo.isAuditarProcedimentosCirurgicos() && isPeloMenosUmProcedimentoCirurgicoMarcadoParaAuditar(resumo.getProcedimentosCirurgicos())){
			verificarAlteracaoEmProcedimentosCirurgicosNaoMarcadoParaAuditar(UtilsAuditarGuiaHonorarioMedico.getProcedimentosCirurgicosNaoMarcadosParaAuditar(adapterProcedimentos));
			validaSeTemPeloMenosUmProcedimentoA100PorCento(adapterProcedimentos);
			validaDataDeAtendimento(adapterProcedimentos, dataAtendimentoGuia, guiaMae);
		}
		
		if(resumo.isAuditarProcedimentosGrauAnestesista() && isPeloMenosUmProcedimentoCirurgicoMarcadoParaAuditar(resumo.getProcedimentosCirurgicosAnestesicos())){
			this.verificarAlteracaoEmProcedimentosCirurgicosNaoMarcadoParaAuditar(UtilsAuditarGuiaHonorarioMedico.getProcedimentosCirurgicosNaoMarcadosParaAuditar(adapterProcedimentos));
			this.validaSeTemPeloMenosUmProcedimentoA100PorCento(adapterProcedimentos);
		}
		
		if(resumo.isAuditarProcedimentosClinicos() && isPeloMenosUmProcedimentoClinicoMarcadoParaAuditar(resumo.getProcedimentosLayer())){
			verificarAlteracaoEmProcedimentosClinicosNaoMarcadoParaAuditar(UtilsAuditarGuiaHonorarioMedico.getProcedimentosClinicosNaoMarcadosParaAuditar(resumo.getProcedimentosVisitaAtuais()));
		}
		
		if(resumo.isAuditarPacotes() && isPeloMenosUmPacoteParaAuditar(resumo, itensPacoteAuditoria)){
			verificarAlteracaoEmPacotesNaoMarcadoParaAuditar(UtilsAuditarGuiaHonorarioMedico.getPacotesNaoMarcadosParaAuditar(resumo.getHonorariosPacote()));
		}
		return true;
	}	
	
	public void validarHonorariosEncontradosParaAuditar(ResumoGuiasHonorarioMedico resumo) throws Exception{
	}
	
	private boolean isPeloMenosUmProcedimentoCirurgicoMarcadoParaAuditar(Set<AdapterProcedimento> procedimentos){
		for(AdapterProcedimento adapterProcedimento : procedimentos){
			if(adapterProcedimento.getProcedimento().isAuditado()){
				return true;
			}
		}
		return false;
	}
	
	private boolean isPeloMenosUmProcedimentoClinicoMarcadoParaAuditar(Set<ProcedimentoHonorarioLayer> procedimentosVisitaAtuais){
		Set<GuiaSimples> guias = new HashSet<GuiaSimples>();

		for(ProcedimentoHonorarioLayer procedimentoHonorario : procedimentosVisitaAtuais){
			if(procedimentoHonorario.isAuditado()){
				guias.add(procedimentoHonorario.getProcedimento().getGuia());
			}
		}
		
		if(guias.isEmpty()){
			return false;
		}
		
		for (GuiaSimples guiaSimples : guias) {
			Set<ProcedimentoHonorario> procedimentos = ((GuiaHonorarioMedico) guiaSimples).getProcedimentosHonorarioNaoGlosadosNemCanceladosNemNegados();
			for (ProcedimentoHonorarioLayer proc : procedimentosVisitaAtuais) {
				if (procedimentos.contains(proc.getProcedimento())) {
					Assert.isTrue(proc.isAuditado(), "Para proseguir, é nescessário marcar para auditar todos os procedimento clínicos de uma mesma autorização.");
				}
			}
		}
		
		return true;
	}
	
	private boolean isPeloMenosUmPacoteParaAuditar(ResumoGuiasHonorarioMedico resumo, Collection<ItemPacoteHonorarioAuditoria> itensPacoteAuditoria){
		boolean achou = false;
		for(AdapterHonorario adapterHonorario : resumo.getHonorariosPacote()){
			if(adapterHonorario.getHonorario().isAuditado()){
				achou = true;
			}
		}
		
		if(!itensPacoteAuditoria.isEmpty()){
			achou = true;
		}
		
		return achou;
	}	
	
	private void verificarAlteracaoEmProcedimentosClinicosNaoMarcadoParaAuditar(Set<ProcedimentoHonorario> procedimentos){
		for(ProcedimentoHonorario procedimentoHonorario : procedimentos){
			ProcedimentoHonorario procedimentoPersistido = (ProcedimentoHonorario) ImplDAO.findById(procedimentoHonorario.getIdProcedimento(), ProcedimentoHonorario.class);
			Assert.isTrue(procedimentoHonorario.isGlosar() == procedimentoPersistido.isGlosar(), "Existe um procedimento clínico que não foi marcado para auditar mas teve a propriedade (Glosar) do honorário alterada.");
		}
	}	

	/**
	 * Não permite para procedimentos/pacotes , uma vez auditados, serem marcados como nao auditados 
	 * @param resumo
	 * @throws ValidateException 
	 */
	private void isHonorarioAuditadoParaNaoAuditado(ResumoGuiasHonorarioMedico resumo) throws ValidateException{
		if(resumo.isAuditarProcedimentosCirurgicos()){
			for(AdapterProcedimento adapterProcedimento : resumo.getProcedimentos()){
				if(adapterProcedimento.isAuditado() && !adapterProcedimento.getProcedimento().isAuditado()){
					throw new ValidateException("Não é possível mudar um procedimento de auditado para não auditado.");
				}
			}
		}

		if(resumo.isAuditarProcedimentosClinicos()){
			for(ProcedimentoHonorario procedimeHonorario : resumo.getProcedimentosVisitaAtuais()){
				if(procedimeHonorario.isJaAuditado() && !procedimeHonorario.isAuditado()){
					throw new ValidateException("Não é possível mudar um procedimento de auditado para não auditado.");
				}
			}
		}

		if(resumo.isAuditarPacotes()){
			for(AdapterHonorario adapterHonorario : resumo.getHonorariosPacote()){
				if(adapterHonorario.isAuditado() && !adapterHonorario.getHonorario().isAuditado()){
					throw new ValidateException("Não é possível mudar um pacote de auditado para não auditado.");
				}
			}
		}
	}
	
	private void validaSeTemPeloMenosUmProcedimentoA100PorCento(Collection<AdapterProcedimento> adapterProcedimentos) throws ValidateException {
		boolean temProcedimentoA100PorCento = false;
		
		for(AdapterProcedimento adapterProcedimento: adapterProcedimentos){
			if(adapterProcedimento.getPorcentagem().compareTo(PORCENTAGEM_100) == 0){
				temProcedimentoA100PorCento = true;
			}
		}
		
		if(!temProcedimentoA100PorCento && !adapterProcedimentos.isEmpty()){
			throw new ValidateException("É necessário que a guia contenha pelo menos um procedimento a 100%");
		}
	}
	
	private void validaDataDeAtendimento(Collection<AdapterProcedimento> adapterProcedimentos, Date dataAtendimento, GuiaSimples guiaMae) {
		for(AdapterProcedimento adapterProcedimento : adapterProcedimentos){
			if(adapterProcedimento.getProcedimento().isAuditado()){
				Date        dataRealizacaoDoProcedimento = adapterProcedimento.getDataRealizacao();
				TabelaCBHPM procedimentoDaTabelaCBHPM    = adapterProcedimento.getProcedimento().getProcedimentoDaTabelaCBHPM();
				
				Assert.isNotNull(dataRealizacaoDoProcedimento, "É Necessario informar a data de realização do procedimento " + procedimentoDaTabelaCBHPM.getCodigoEDescricao());
				
				if (dataAtendimento != null && dataRealizacaoDoProcedimento != null){
					boolean isRealizacaoAntesDoAtendimento 	= Utils.compareData(dataRealizacaoDoProcedimento, dataAtendimento) < 0;
					Assert.isFalse(isRealizacaoAntesDoAtendimento, MensagemErroEnum.DATA_CIRURGIA_INFERIOR_A_DATA_DE_ATENDIMENTO.getMessage(procedimentoDaTabelaCBHPM.getCodigoEDescricao()));
				}
				
				//Verifica alta para guias de internação. 
				//Caso exista alta, a data atendimento não pode ser maior que a data de registro de alta.
				if(guiaMae.isInternacao()){
					AltaHospitalar alta = ((GuiaInternacao)guiaMae).getAltaHospitalar();
					if(alta != null){
						Date dataAlta = alta.getDataDeAlta();
						
						boolean isRealizacaoDepoisDaAlta = Utils.compareData(dataAlta, dataRealizacaoDoProcedimento) < 0;
						Assert.isFalse(isRealizacaoDepoisDaAlta, "A data de Realização do procedimento " + procedimentoDaTabelaCBHPM.getCodigoEDescricao() + " não pode ser posterior a data de registro de alta" );
					}
				}
			}
		}
	}
	
	private void verificarAlteracaoEmProcedimentosCirurgicosNaoMarcadoParaAuditar(Collection<AdapterProcedimento> adapterProcedimentos){
		
		for(AdapterProcedimento adapterProcedimento : adapterProcedimentos){
			
			Procedimento procedimentoPersistido = (Procedimento) new SearchAgent().findById(adapterProcedimento.getProcedimento().getIdProcedimento(), Procedimento.class);
			
			Assert.isTrue(adapterProcedimento.getCbhpm().equals(procedimentoPersistido.getProcedimentoDaTabelaCBHPM()), "Existe um procedimento que não foi marcado para auditar mas teve seu procedimento CBHPM alterado.");
			if (adapterProcedimento.getProcedimento().getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO) {
				Assert.isTrue(adapterProcedimento.getPorcentagem().equals(procedimentoPersistido.getPorcentagem()), "Existe um procedimento que não foi marcado para auditar mas teve sua porcentagem alterada.");
				Assert.isTrue(Utils.compareData(adapterProcedimento.getDataRealizacao(), ((ProcedimentoCirurgico) procedimentoPersistido).getDataRealizacao()) == 0, "Existe um procedimento que não foi marcado para auditar mas teve a data de realização alterada.");
			}
			Assert.isTrue(adapterProcedimento.getProcedimento().isGlosar() == procedimentoPersistido.isGlosar(), "Existe um procedimento que não foi marcado para auditar mas teve sua propriedade (Glosar) alterada.");
			Assert.isTrue(adapterProcedimento.getProcedimento().isIncluiVideo() == procedimentoPersistido.isIncluiVideo(), "Existe um procedimento que não foi marcado para auditar mas teve sua propriedade (Inclui Video) alterada.");
			Assert.isTrue(adapterProcedimento.getProcedimento().isHorarioEspecial() == procedimentoPersistido.isHorarioEspecial(), "Existe um procedimento que não foi marcado para auditar mas teve sua propriedade (Horário Especial) alterada.");
			
			//Usado para facilitar o acesso a honorarios de cirúrgião e honorarios de anestesistas separadamente.
			AdapterProcedimento adapterProcedimentoPersistido = new AdapterProcedimento(procedimentoPersistido);
			
			verificarAlteracaoEmHonorariosDeCirurgiao(adapterProcedimento, adapterProcedimentoPersistido);
			verificarAlteracaoEmHonorariosDeAnestesista(adapterProcedimento, adapterProcedimentoPersistido);
		}
	}

	private void verificarAlteracaoEmHonorariosDeCirurgiao(AdapterProcedimento adapterProcedimento, AdapterProcedimento adapterProcedimentoPersistido) {
		
		Assert.isTrue(adapterProcedimento.getHonorarios().size() == adapterProcedimentoPersistido.getHonorarios().size(), "Existe um procedimento cirúrgico que não foi marcado para auditar mas teve a quantidade de honorários de cirurgião alterada.");
		
		AdapterHonorario[] facadeHonorarios = adapterProcedimento.getHonorarios().toArray(new AdapterHonorario[adapterProcedimento.getHonorarios().size()]); 
		AdapterHonorario[] honorariosPersistidos = adapterProcedimentoPersistido.getHonorarios().toArray(new AdapterHonorario[adapterProcedimentoPersistido.getHonorarios().size()]);
		for(int i=0; i < facadeHonorarios.length; i++){
			AdapterHonorario facadeHonorario = facadeHonorarios[i];
			AdapterHonorario honorarioPersistido = honorariosPersistidos[i];
			
			Assert.isTrue(facadeHonorario.getProfissional().equals(honorarioPersistido.getProfissional()), "Existe um procedimento cirúrgico que não foi marcado para auditar mas teve o profissional do honorário cirúrgico alterado.");
			Assert.isTrue(facadeHonorario.getHonorario().getGlosado() == honorarioPersistido.getHonorario().getGlosado(), "Existe um procedimento cirúrgico que não foi marcado para auditar mas teve a propriedade (Glosar) do honorário cirúrgico alterado.");
		}
	}
	
	private void verificarAlteracaoEmHonorariosDeAnestesista(AdapterProcedimento adapterProcedimento, AdapterProcedimento adapterProcedimentoPersistido) { 
		
		Assert.isTrue(adapterProcedimento.getHonorariosAnestesistas().size() == adapterProcedimentoPersistido.getHonorariosAnestesistas().size(), "Existe um procedimento cirúrgico que não foi marcado para auditar mas teve a quantidade de honorários de anestesista alterada.");
		
		AdapterHonorario[] facadeHonorarios = (AdapterHonorario[]) adapterProcedimento.getHonorariosAnestesistas().toArray(new AdapterHonorario[adapterProcedimento.getHonorariosAnestesistas().size()]);
		AdapterHonorario[] honorariosPersistidos = (AdapterHonorario[]) adapterProcedimentoPersistido.getHonorariosAnestesistas().toArray(new AdapterHonorario[adapterProcedimentoPersistido.getHonorariosAnestesistas().size()]);
		for(int i=0; i < facadeHonorarios.length; i++){
			AdapterHonorario facadeHonorario = facadeHonorarios[i];
			AdapterHonorario honorarioPersistido = honorariosPersistidos[i];
			
			Assert.isTrue(facadeHonorario.getProfissional().equals(honorarioPersistido.getProfissional()), "Existe um procedimento cirúrgico que não foi marcado para auditar mas teve o profissional do honorário anestésico alterado.");
			Assert.isTrue(facadeHonorario.getHonorario().getGlosado() == honorarioPersistido.getHonorario().getGlosado(), "Existe um procedimento cirúrgico que não foi marcado para auditar mas teve a propriedade (Glosar) do honorário anestésico alterado.");
		}
	}
	
	private void verificarAlteracaoEmPacotesNaoMarcadoParaAuditar(Collection<AdapterHonorario> adapterHonorarios){
		for (AdapterHonorario adapterHonorario : adapterHonorarios) {
			HonorarioExterno honorario = adapterHonorario.getHonorario();
			boolean mudouProfissional = !adapterHonorario.getProfissional().equals(honorario.getProfissional());
			boolean mudouPercentual   = !adapterHonorario.getPorcentagem().equals(honorario.getPorcentagem());
			boolean marcadoParaGlosa  = honorario.isGlosar();
			Assert.isFalse(mudouProfissional, "Existe um pacote que não foi marcado para auditar mas teve o profissional alterado.");
			Assert.isFalse(mudouPercentual, "Existe um pacote que não foi marcado para auditar mas teve o percentual alterado.");
			Assert.isFalse(marcadoParaGlosa, "Existe um pacote que não foi marcado para auditar mas foi marcado para glosar.");
		}
	}
}
