/**
 * 
 */
package br.com.infowaypi.ecarebc.service.exames;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.Observacao;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.MoneyCalculation;

/**
 * Service responsável pelo fechamento de guias de exames contendo procedimentos especiais.
 * @author Marcus bOolean, Thiago Void
 */
@SuppressWarnings("unchecked")
public class FecharGuiaExameEspecialService extends Service {
	
	public static final int LIMITE_DIAS_FECHAMENTO = 60;

	public FecharGuiaExameEspecialService(){
		super();
	}

	public GuiaExame fecharGuia(BigDecimal valorMedicamento, BigDecimal valorMaterial, String observacao, UsuarioInterface usuario, GuiaExame guia) throws Exception {
		guia.setValorAnterior(guia.getValorTotal());
		
		Set<Procedimento> procedimentos = guia.getProcedimentos();
		boolean permiteMaterial = false;
		boolean permiteMedicamento = false;
		if(valorMaterial == null)
			valorMaterial = MoneyCalculation.rounded(BigDecimal.ZERO);

		if(valorMedicamento == null)
			valorMedicamento = MoneyCalculation.rounded(BigDecimal.ZERO);
		
 		for (ProcedimentoInterface procedimento : procedimentos) {
 			if (procedimento.getProcedimentoDaTabelaCBHPM().isPermiteMaterialComplementar()){
 				permiteMaterial = true;
 			}
 			if (procedimento.getProcedimentoDaTabelaCBHPM().isPermiteMedicamentoComplementar()){
 				permiteMedicamento = true;
 			}
		}
 		
 		if (valorMaterial != null && !permiteMaterial && MoneyCalculation.compare(valorMaterial, BigDecimal.ZERO) != 0){
 			throw new RuntimeException("Prezado(a) usuario(a), essa guia de exame não permite a inserção de valores de MATERIAIS.");
 		}
 		BigDecimal zero = MoneyCalculation.rounded(BigDecimal.ZERO);
 		
 		if (valorMedicamento != null && !permiteMedicamento && MoneyCalculation.compare(valorMedicamento, BigDecimal.ZERO) != 0){
 			throw new RuntimeException("Prezado(a) usuario(a), essa guia de exame não permite a inserção de valores de MEDICAMENTOS.");
 		}
 		if(!permiteMaterial && !zero.equals(valorMaterial) && !permiteMedicamento && !zero.equals(valorMedicamento)) {
 			throw new RuntimeException("Prezado(a) usuario(a), essa guia de exame não permite a inserção de valores de MEDICAMENTOS ou MATERIAIS.");
 		}
 		
 		guia.setValorMaterialComplementarAuditado(valorMaterial);
 	 	guia.setValorMaterialComplementarSolicitado(valorMaterial);
 		guia.setValorMedicamentoComplementarAuditado(valorMedicamento);
 	 	guia.setValorMedicamentoComplementarSolicitado(valorMedicamento);
 		
 	 	if (observacao != null) {
 	 		Observacao abstractObservacao = new Observacao(new Date(), observacao, usuario);
 	 		abstractObservacao.setGuia(guia);
 	 		guia.addObservacao(abstractObservacao);
 	 	}
 		
 		guia.setValorTotal(guia.getValorTotalSolicitado().add(guia.getValorTotalProcedimentos()));
 		
 		guia.mudarSituacao(usuario, SituacaoEnum.FECHADO.descricao(), MotivoEnum.FECHAMENTO_GUIA.getMessage(), new Date());
 		
		return guia;
	}
	
	public <G extends GuiaSimples> void salvarGuia(G guia) throws Exception {
		ImplDAO.save(guia);
		//Atualização do consumo financeiro
//		StateMachineConsumo.updateConsumoAtendimento(guia, true, true);
	}
}
