package br.com.infowaypi.ecare.services.honorarios.validators;

import java.util.Date;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GeradorGuiaHonorarioInterface;
import br.com.infowaypi.ecarebc.atendimentos.validators.EnumDatasAuxiliares;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.utils.Utils;


public class GuiaHonorarioProcedimentoSemProfissionalEmGuiaPagaAjusteTemporarioValidator extends CommandValidator{
	
	public void execute() {
		GuiaSimples<ProcedimentoInterface> guiaOrigem = (GuiaSimples<ProcedimentoInterface>) this.getGuiaOrigem();
		
		SituacaoInterface situacaoGuia = guiaOrigem.getSituacao();
		
		if(situacaoGuia.getDescricao().equals(SituacaoEnum.FATURADA.descricao()) || situacaoGuia.getDescricao().equals(SituacaoEnum.PAGO.descricao())){
			
			Date dataPagamentoGuia = situacaoGuia.getData();
			Date dataImplantacao = EnumDatasAuxiliares.DATA_IMPLANTACAO_DEMANDA_27.getData();
			
			boolean guiaPagaDepoisDaImplantacao = Utils.compareData(dataPagamentoGuia, dataImplantacao) > 0;
			
			Procedimento procedimento = getProcedimentoSemProfissional(this.getGuiaOrigem());
			
			if(!guiaPagaDepoisDaImplantacao && procedimento != null){
				String codigoCBHPM = procedimento.getProcedimentoDaTabelaCBHPM().getCodigo();
				throw new RuntimeException(MensagemErroEnum.PROCEDIMENTO_COM_HONORARIO_PAGO.getMessage(codigoCBHPM));
			}
		}
	}
	
	private Procedimento getProcedimentoSemProfissional(GeradorGuiaHonorarioInterface guia){
		for (ProcedimentoInterface proc : guia.getProcedimentosQueVaoGerarHonorario()) {
			boolean isProcedimentoCirugico 		  = proc.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO;
			boolean temProfissional 			  = proc.getProfissionalResponsavel() != null;
			
			if (isProcedimentoCirugico && !temProfissional)  {
				return (Procedimento) proc;
			}
		}
		return null;
	}
}
