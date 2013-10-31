package br.com.infowaypi.ecare.financeiro.faturamento;

import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecare.financeiro.ordenador.ProgressBarFinanceiro;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AbstractFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.Faturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.GuiaFaturavel;
import br.com.infowaypi.ecarebc.financeiro.faturamento.HonorarioMedico;
import br.com.infowaypi.msr.utils.Utils;

public class GeracaoFaturamentoNormal extends AbstractGeracaoFaturamento {
	
	public GeracaoFaturamentoNormal(ProgressBarFinanceiro progressBar) {
		this();
		this.progressBar = progressBar; 
	}

	public GeracaoFaturamentoNormal() {
		super();
		this.setFaturamentoPassivo(Boolean.FALSE);
	}

	@Override
	protected AbstractFaturamento createFaturamento(Date competencia, Prestador prestador) {
		
		if(prestador.isPrestadorAnestesista())  {
			return new FaturamentoSR(competencia, Constantes.FATURAMENTO_ABERTO, prestador);
		}else {
			return new Faturamento(competencia, Constantes.FATURAMENTO_ABERTO, prestador);
		}
		
	}

	@Override
	protected AbstractFaturamento getFaturamentoPrestadorParaACompetencia(
			Date competencia, Prestador prestador) {
		return prestador.getFaturamento(competencia);
	}

	@Override
	protected HonorarioMedico getHonorarioDoProfissionalParaACompetencia(
			Date competencia, Profissional profissional) {
		return profissional.getHonorarioMedico(competencia);
	}

	@Override
	protected void ordenarListaDeGuias(List<GuiaFaturavel> todasAsGuias) {
		Utils.sort(todasAsGuias, true, "valorTotal");
	}

	@Override
	protected String getTipoGeracaoFaturamento() {
		return AbstractGeracaoFaturamento.GERACAO_NORMAL;
	}

	@Override
	protected void updateProgressBarData(int numeroDePretadores) {
		progressBar.startIntervaloNaBarra(30, 50, numeroDePretadores);
	}

}
