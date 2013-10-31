package br.com.infowaypi.ecare.resumos;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AbstractFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.DetalheHonorarioMedico;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ItemGuiaFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ResumoFaturamentos;

public class ResumoFaturamentoNovo extends ResumoFaturamentos {
	
	/** 
	 * @param faturamentos encontrados no banco de dados de acordo com os parâmetros informados
	 * @param prestadores  aos quais os faturamentos percetencem
	 * @param tipoResumo indica a forma da organização do resumo
	 * @param competencia 
	 * @param exibirGuias 
	 */
	public ResumoFaturamentoNovo(List<? extends AbstractFaturamento> faturamentos, List<Prestador> prestadores, int tipoResumo, Date competencia, Boolean exibirGuias) {
		super(faturamentos, prestadores, tipoResumo, competencia, exibirGuias);
	}
	
	/**
	 * @param faturamentosPorPrestador mapa de prestadores com seus respctivos faturamentos para a competencia informada.
	 * @param faturamentos encontrados no banco de dados de acordo com os parâmetros informados
	 * @param prestadores  aos quais os faturamentos percetencem
	 * @param tipoResumo indica a forma da organização do resumo
	 * @param competencia 
	 * @param exibirGuias 
	 */
	public ResumoFaturamentoNovo(Map<Prestador, Set<AbstractFaturamento>> faturamentosPorPrestador, List<? extends AbstractFaturamento> faturamentos,  int tipoResumo, Date competencia, Boolean exibirGuias) {
		super(faturamentosPorPrestador, faturamentos,  tipoResumo, competencia, exibirGuias);
	}
	
	public ResumoFaturamentoNovo(List<? extends AbstractFaturamento> faturamentos, Prestador prestador, int tipoResumo, Date competencia, boolean exibirGuias) {
		super(faturamentos, tipoResumo, prestador, competencia, exibirGuias);
	}
	
	@Override
	protected void computarHonorario(AbstractFaturamento faturamento) {
		for (ItemGuiaFaturamento igf : faturamento.getItensGuiaFaturamento()) {
			
			DetalheHonorarioMedico detalhe = new DetalheHonorarioMedico();
			detalhe.setFuncao(igf.getGrauParticipacaoEnum().getDescricao());
			detalhe.setGuia(igf.getGuia());
			detalhe.setNomeSegurado(igf.getNomeSegurado());
			detalhe.setProcedimento(igf.getProcedimento());
			detalhe.setProfissional(igf.getProfissional());
			detalhe.setPrestador(igf.getPrestadorDestino());
			detalhe.setValor(igf.getValor());
			this.getDetalhesHonorariosMedicos().add(detalhe);
		}
	}
}
