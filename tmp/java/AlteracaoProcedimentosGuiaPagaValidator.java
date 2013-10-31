package br.com.infowaypi.ecare.services.honorariomedico;

import java.util.Collection;
import java.util.Set;

import br.com.infowaypi.ecare.resumos.ResumoGuiasHonorarioMedico;
import br.com.infowaypi.ecare.services.honorariomedico.adapter.AdapterProcedimento;
import br.com.infowaypi.ecarebc.associados.validators.AbstractValidator;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

public class AlteracaoProcedimentosGuiaPagaValidator extends AbstractValidator<ResumoGuiasHonorarioMedico>{
	
	@Override
	protected boolean templateValidator(ResumoGuiasHonorarioMedico resumo) throws ValidateException {
		
		SituacaoInterface situacao = resumo.getGuiaMae().getSituacao(); 
		boolean isGuiaPagaOuFAturada =  situacao.getDescricao().equals(SituacaoEnum.PAGO.descricao()) || situacao.getDescricao().equals(SituacaoEnum.FATURADA.descricao()); 
		
		if(isGuiaPagaOuFAturada && resumo.isAuditarProcedimentosCirurgicos() && isPeloMenosUmProcedimentoCirurgicoMarcadoParaAuditar(resumo.getProcedimentosCirurgicos())){
			verificarAlteracaoEmProcedimentosCirurgicosDeGuiaPagaOuFaturada(UtilsAuditarGuiaHonorarioMedico.getProcedimentosCirurgicosMarcadosParaAuditar(resumo.getAdapterProcedimentosTela()));
		}
		
		return true;
	}	
	
	private boolean isPeloMenosUmProcedimentoCirurgicoMarcadoParaAuditar(Set<AdapterProcedimento> procedimentos){
		for(AdapterProcedimento adapterProcedimento : procedimentos){
			if(adapterProcedimento.getProcedimento().isAuditado()){
				return true;
			}
		}
		return false;
	}
	
	private void verificarAlteracaoEmProcedimentosCirurgicosDeGuiaPagaOuFaturada(Collection<AdapterProcedimento> adapterProcedimentos){
		
		for(AdapterProcedimento adapterProcedimento : adapterProcedimentos){
			
			ProcedimentoCirurgico procedimentoPersistido = (ProcedimentoCirurgico) new SearchAgent().findById(adapterProcedimento.getProcedimento().getIdProcedimento(), ProcedimentoCirurgico.class);
			
			String codigoCBHPMPersistido = procedimentoPersistido.getProcedimentoDaTabelaCBHPM().getCodigo();
			String codigoCBHPMTela = adapterProcedimento.getCbhpm().getCodigo();
			
			Assert.isTrue(adapterProcedimento.getCbhpm().equals(procedimentoPersistido.getProcedimentoDaTabelaCBHPM()), MensagemErroEnum.NAO_E_POSSIVEL_ALTERAR_PROCEDIMENTO_DE_GUIA_PAGA__CBHPM.getMessage(codigoCBHPMPersistido, codigoCBHPMTela));
			Assert.isTrue(adapterProcedimento.getPorcentagem().equals(procedimentoPersistido.getPorcentagem()), MensagemErroEnum.NAO_E_POSSIVEL_ALTERAR_PROCEDIMENTO_DE_GUIA_PAGA__PORCENTAGEM.getMessage(codigoCBHPMPersistido));
			Assert.isFalse(adapterProcedimento.getProcedimento().isGlosar(), MensagemErroEnum.NAO_E_POSSIVEL_ALTERAR_PROCEDIMENTO_DE_GUIA_PAGA__GLOSA.getMessage(codigoCBHPMPersistido));
			Assert.isTrue(Utils.compareData(adapterProcedimento.getDataRealizacao(), procedimentoPersistido.getDataRealizacao()) == 0, MensagemErroEnum.NAO_E_POSSIVEL_ALTERAR_PROCEDIMENTO_DE_GUIA_PAGA__DATA_REALIZACAO.getMessage(codigoCBHPMPersistido));
			
		}
	}
}
