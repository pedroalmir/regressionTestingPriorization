package br.com.infowaypi.ecare.arquivos;

import java.util.ArrayList;
import java.util.List;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.ResumoLimitesBeneficiario;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsulta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaOdonto;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExameOdonto;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe que representa os dados que serão visualizados no arquivo gerado pelo
 * JHeatReports
 * 
 * @author Jefferson
 * 
 */
public class DataSourceLimiteBeneficiario {

	ResumoLimitesBeneficiario resumo;

	public DataSourceLimiteBeneficiario(ResumoLimitesBeneficiario resumo) {
		this.resumo = resumo;
	}

	public Segurado getBeneficiario() {
		return resumo.getSegurado();
	}

	public ResumoLimitesBeneficiario getResumo() {
		return resumo;
	}

	public String getLogo() {
		return getClass().getResource("/templates/saude-recife-logo.png").getPath();
	}

	public List<List<String>> getConsultas() {
		List<List<String>> tabela = new ArrayList<List<String>>();
		for (GuiaConsulta<ProcedimentoInterface> guia : resumo.getConsultas()) {
			construirTabela(tabela, guia);
		}
		return tabela;
	}

	public List<List<String>> getExames() {
		List<List<String>> tabela = new ArrayList<List<String>>();
		for (GuiaExame<ProcedimentoInterface> guia : resumo.getExames()) {
			construirTabela(tabela, guia);
		}
		return tabela;
	}

	public List<List<String>> getConsultasOdonto() {
		List<List<String>> tabela = new ArrayList<List<String>>();
		for (GuiaConsultaOdonto guia : resumo.getConsultasOdonto()) {
			construirTabela(tabela, guia);
		}
		return tabela;
	}

	public List<List<String>> getTratamentoOdonto() {
		List<List<String>> tabela = new ArrayList<List<String>>();
		for (GuiaExameOdonto guia : resumo.getTratamentoOdonto()) {
			construirTabela(tabela, guia);
		}
		return tabela;
	}

	private void construirTabela(List<List<String>> tabela, GuiaSimples guia) {
		List<String> linha;
		String dataAtendimento = "";
		linha = new ArrayList<String>();
		linha.add(guia.getAutorizacao());
		if (guia.getDataAtendimento() != null) {
			dataAtendimento = Utils.format(guia.getDataAtendimento());
		}
		linha.add(dataAtendimento);
		linha.add(guia.getSituacao().getDescricao());
		linha.add(guia.getValorTotal().toString());
		tabela.add(linha);
	}

}
