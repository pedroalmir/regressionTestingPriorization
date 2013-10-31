package br.com.infowaypi.ecarebc.utils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecarebc.atendimentos.GuiaAtendimentoUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ManagerGuia;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemGasoterapia;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemTaxa;
import br.com.infowaypi.ecarebc.atendimentos.honorario.Honorario;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ItemGuiaFaturamento;
import br.com.infowaypi.ecarebc.opme.ItemOpme;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.msr.utils.MoneyCalculation;

@SuppressWarnings("unchecked")
public  class CommandCorrecaoValorGuia{

	private GuiaSimples guia;
	private static List<String> situacoes = Arrays.asList(SituacaoEnum.FECHADO.descricao(), 
											SituacaoEnum.ENVIADO.descricao(), SituacaoEnum.RECEBIDO.descricao(), 
											SituacaoEnum.AUDITADO.descricao(), SituacaoEnum.FATURADA.descricao(), 
											SituacaoEnum.PAGO.descricao());
	
	public CommandCorrecaoValorGuia(GuiaSimples guia){    
		this.guia = guia;
	}
	
	public void execute(){
		BigDecimal valorTotalGuia 				= BigDecimal.ZERO;
		BigDecimal valorItensGuiaFaturamento 	= BigDecimal.ZERO; 
		
		valorTotalGuia = valorTotalGuia.add(getSomatorioProcedimentos(guia));
		if(guia.isExame()){
			GuiaExame ge = (GuiaExame) guia;
			if(isSituacaoAPartirDeAuditada(ge)){
				valorTotalGuia = valorTotalGuia.add(ge.getValorTotalAuditado());
			} else {
				valorTotalGuia = valorTotalGuia.add(ge.getValorTotalSolicitado());
			}
		}
		
		if(guia.isCompleta()) {
			valorTotalGuia = valorTotalGuia.add(getSomatorioTaxas((GuiaCompleta) guia));
			valorTotalGuia = valorTotalGuia.add(getSomatorioDiarias((GuiaCompleta) guia));
			valorTotalGuia = valorTotalGuia.add(getSomatorioGasoterapias((GuiaCompleta) guia));
			valorTotalGuia = valorTotalGuia.add(getSomatorioPacotes((GuiaCompleta) guia));
			valorTotalGuia = valorTotalGuia.add(getSomatorioValoresMatMed((GuiaCompleta) guia));
			valorTotalGuia = valorTotalGuia.add(getSomatorioHonorariosMedicos((GuiaCompleta) guia));
			valorItensGuiaFaturamento = getSomatorioItensGuiaFaturamento(guia.getItensGuiaFaturamento());
			valorTotalGuia = valorTotalGuia.add(getSomatorioItensOpme((GuiaCompleta) guia));
		}
		
		guia.setValorTotal(valorTotalGuia);
		BigDecimal valorPagoPrestador 	= valorTotalGuia.subtract(valorItensGuiaFaturamento);
		valorPagoPrestador 				= GuiaUtils.aplicarMultaPorAtraso(valorPagoPrestador, guia.getMultaPorAtrasoDeEntrega());
		guia.setValorPagoPrestador(valorPagoPrestador);
	}

	private BigDecimal getSomatorioItensOpme(GuiaCompleta guia){
		BigDecimal somatorio = BigDecimal.ZERO;
		if (guia.getItensOpme() != null) {
			for (Object item : ((GuiaCompleta)guia).getItensOpme()) {
				somatorio = somatorio.add(((ItemOpme) item).getValorTotal());
			}
		}
		return MoneyCalculation.rounded(somatorio);
	}
	
	private BigDecimal getSomatorioItensGuiaFaturamento(Set<ItemGuiaFaturamento> itensGuiaFaturamento) {
		BigDecimal resultado = BigDecimal.ZERO;
		
		for (ItemGuiaFaturamento item : itensGuiaFaturamento) {
			resultado = resultado.add(item.getValor());
		}
		
		return resultado;
	}

	private boolean isSituacaoAPartirDeAuditada(GuiaSimples guia) {
		return guia.isSituacaoAtual(SituacaoEnum.AUDITADO.descricao()) 
					|| guia.isSituacaoAtual(SituacaoEnum.FATURADA.descricao())
					|| guia.isSituacaoAtual(SituacaoEnum.PAGO.descricao());
	}
	
	private BigDecimal getSomatorioProcedimentos(GuiaSimples<Procedimento> guia){
		BigDecimal somatorio = BigDecimal.ZERO;
		
		if(this.isSituacaoAPartirDeAuditada(guia)){
			for (Procedimento procedimento :  ManagerGuia.getProcedimentosComProfissionalResponsavel(guia)) {
				if((guia instanceof GuiaAtendimentoUrgencia) && procedimento.isConsulta()){
					if (((GuiaAtendimentoUrgencia) guia).isValorada()) {
						procedimento.setValorAtualDoProcedimento(procedimento.getProcedimentoDaTabelaCBHPM().getValorModerado());
						procedimento.aplicaValorAcordo();
						somatorio = somatorio.add(procedimento.getValorTotal());
					}
					else {
						procedimento.setValorAtualDoProcedimento(BigDecimal.ZERO);
					}
					continue;
				}
				if(procedimento.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao()) && guia.isSituacaoAtual(SituacaoEnum.AUDITADO.descricao())){
					procedimento.mudarSituacao(guia.getSituacao().getUsuario(), SituacaoEnum.AUTORIZADO.descricao(), "Auditoria", guia.getSituacao().getDataSituacao());
				}
				if(!procedimento.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()) 
					&& !procedimento.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao())	
					&& !procedimento.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao())
					&& !procedimento.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())
					&& !procedimento.isSituacaoAtual(SituacaoEnum.PENDENTE.descricao())
					&& !procedimento.isSituacaoAtual(SituacaoEnum.PROCEDIMENTO_COM_HONORARIO_GERADO.descricao())
					&& !procedimento.isSituacaoAtual(SituacaoEnum.AGUARDANDO_COBRANCA.descricao())){
					somatorio = somatorio.add(procedimento.getValorTotal());
				}
			}
		}else{
			for (Procedimento procedimento : guia.getProcedimentos()) {
				if((guia instanceof GuiaAtendimentoUrgencia) && procedimento.isConsulta()){
					if (((GuiaAtendimentoUrgencia) guia).isValorada()) {
						procedimento.setValorAtualDoProcedimento(procedimento.getProcedimentoDaTabelaCBHPM().getValorModerado());
						procedimento.aplicaValorAcordo();
						somatorio = somatorio.add(procedimento.getValorTotal());
					}
					else {
						procedimento.setValorAtualDoProcedimento(BigDecimal.ZERO);
					}
					continue;
				}
				if(!procedimento.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()) 
						&& !procedimento.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao())
						&& !procedimento.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao())
						&& !procedimento.isSituacaoAtual(SituacaoEnum.PROCEDIMENTO_COM_HONORARIO_GERADO.descricao())
						&& !procedimento.isSituacaoAtual(SituacaoEnum.AGUARDANDO_COBRANCA.descricao())){
					somatorio = somatorio.add(procedimento.getValorTotal());
				}
			}
		}
		
		return MoneyCalculation.rounded(somatorio);
	}
	
	private BigDecimal getSomatorioDiarias(GuiaCompleta<ProcedimentoInterface> guia){
		
		BigDecimal somatorio = BigDecimal.ZERO;
		
		if(guia.isSituacaoAtual(SituacaoEnum.AUDITADO.descricao()) || guia.isSituacaoAtual(SituacaoEnum.FATURADA.descricao())){
			for (ItemDiaria diaria : guia.getItensDiaria()) {
				if(diaria.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao()) && guia.isSituacaoAtual(SituacaoEnum.AUDITADO.descricao())){
					diaria.calculaDataInicial();
					diaria.mudarSituacao(guia.getSituacao().getUsuario(), SituacaoEnum.AUTORIZADO.descricao(), "Auditoria", guia.getSituacao().getDataSituacao());
				}
				if(!diaria.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()) 
					&& !diaria.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao())
					&& !diaria.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())
					&& !diaria.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao())){
					somatorio = somatorio.add(new BigDecimal(diaria.getValorTotal()));
				}
			}
		}else{
			for (ItemDiaria diaria : guia.getItensDiaria()) {
				if(!diaria.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()) 
					&& !diaria.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao())
					&& !diaria.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao())){
					somatorio = somatorio.add(new BigDecimal(diaria.getValorTotal()));
				}
			}
		}
		
		return MoneyCalculation.rounded(somatorio);
	}
		
	private BigDecimal getSomatorioTaxas(GuiaCompleta<ProcedimentoInterface> guia){
		BigDecimal somatorio = BigDecimal.ZERO;
		for (ItemTaxa taxa : guia.getItensTaxa()) {
			if(!taxa.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()) 
				&& !taxa.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao())
				&& !taxa.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao())){
				somatorio = somatorio.add(new BigDecimal(taxa.getValorTotal()));
			}
		}
		return MoneyCalculation.rounded(somatorio);
	}
	
	private BigDecimal getSomatorioGasoterapias(GuiaCompleta<ProcedimentoInterface> guia){
		BigDecimal somatorio = BigDecimal.ZERO;
		for (ItemGasoterapia gasoterapia : guia.getItensGasoterapia()) {
			if(!gasoterapia.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()) 
				&& !gasoterapia.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao())
				&& !gasoterapia.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao())){
				
				somatorio = somatorio.add(new BigDecimal(gasoterapia.getValorTotal()));
			}
		}
		return MoneyCalculation.rounded(somatorio);
	}
	
	private BigDecimal getSomatorioPacotes(GuiaCompleta<ProcedimentoInterface> guia){
		BigDecimal somatorio = BigDecimal.ZERO;
		for (ItemPacote pacote : guia.getItensPacote()) {
			if(!pacote.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())
				&& !pacote.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao())
				&& !pacote.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao())){
				pacote.recalcularCampos();
				somatorio = somatorio.add(new BigDecimal(pacote.getValorTotal()));
			}
		}
		return MoneyCalculation.rounded(somatorio);
	}
	
	private BigDecimal getSomatorioValoresMatMed(GuiaCompleta guia){
		BigDecimal somatorio = BigDecimal.ZERO;
		if(isSituacaoAPartirDeAuditada(guia))
			somatorio = somatorio.add(guia.getValoresMatMed().getValorTotalAuditado());
		else 
			somatorio = somatorio.add(guia.getValoresMatMed().getValorTotalInformado());
		
		return MoneyCalculation.rounded(somatorio);
	}
	
	private BigDecimal getSomatorioHonorariosMedicos(GuiaCompleta<ProcedimentoInterface> guia){
		BigDecimal somatorio = BigDecimal.ZERO;
		// recalcula-se o valor da guia baseado no porte dos procedimentos e profissionais inseridos
		if(situacoes.contains(guia.getSituacao().getDescricao())){
			for (ProcedimentoInterface procedimento : guia.getProcedimentos()) {
				if(!procedimento.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()) 
					&& !procedimento.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao())
					&& !procedimento.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())){
					for (Honorario honorario : procedimento.getHonorariosGuiaOrigem()) {
						if (honorario.isSituacaoAtual(SituacaoEnum.GERADO.descricao()) && !honorario.isHonorarioAnestesista()) {
							somatorio = somatorio.add(honorario.getValorTotal());
						}
					}
				} 
			}
		}else {
			for (ProcedimentoInterface procedimento : guia.getProcedimentos()) {
				if(!procedimento.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()) 
						&& !procedimento.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao())){
					for (Honorario honorario : procedimento.getHonorariosGuiaOrigem()) {
						if (honorario.isSituacaoAtual(SituacaoEnum.GERADO.descricao()) && !honorario.isHonorarioAnestesista()) {
							somatorio = somatorio.add(honorario.getValorTotal());
						}
					}
				}
			}
		}

		return MoneyCalculation.rounded(somatorio);
	}
	
}
