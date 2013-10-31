package br.com.infowaypi.ecare.financeiro.faturamento;

import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecare.financeiro.ordenador.ProgressBarFinanceiro;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AbstractFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.FaturamentoPassivo;
import br.com.infowaypi.ecarebc.financeiro.faturamento.GuiaFaturavel;
import br.com.infowaypi.ecarebc.financeiro.faturamento.HonorarioMedico;
import br.com.infowaypi.msr.utils.Utils;

public class GeracaoFaturamentoPassivo extends AbstractGeracaoFaturamento {
	
	public GeracaoFaturamentoPassivo(ProgressBarFinanceiro progressBar) {
		this();
		this.progressBar = progressBar;
	}

	public GeracaoFaturamentoPassivo() {
		super();
		this.setFaturamentoPassivo(Boolean.TRUE);
	}

	@Override
	protected AbstractFaturamento createFaturamento(Date competencia, Prestador prestador) {
		return new FaturamentoPassivo(competencia, Constantes.FATURAMENTO_ABERTO, prestador);
	}

	@Override
	protected AbstractFaturamento getFaturamentoPrestadorParaACompetencia(
			Date competencia, Prestador prestadorDestinoHonorario) {
		return prestadorDestinoHonorario.getFaturamentoPassivo(competencia);
	}

	@Override
	protected HonorarioMedico getHonorarioDoProfissionalParaACompetencia(
			Date competencia, Profissional profissional) {
		return profissional.getHonorarioMedicoPassivo(competencia);
	}

	@Override
	protected void ordenarListaDeGuias(List<GuiaFaturavel> todasAsGuias) {
		Utils.sort(todasAsGuias, "dataTerminoAtendimento");
	}
	
	@Override
	protected String getTipoGeracaoFaturamento() {
		return AbstractGeracaoFaturamento.GERACAO_PASSIVO;
	}
	

	@Override
	protected void updateProgressBarData(int numeroDePretadores) {
		progressBar.startIntervaloNaBarra(80, 99, numeroDePretadores);
	}
}
