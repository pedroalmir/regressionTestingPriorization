package br.com.infowaypi.ecare.resumos;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.infowaypi.ecare.financeiro.faturamento.FaturamentoSR;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AbstractFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.DetalheHonorarioMedico;
import br.com.infowaypi.ecarebc.financeiro.faturamento.DetalheValoresFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.Faturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.GuiaFaturavel;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ResumoFaturamentos;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
 
public class ResumoFaturamentosSR extends ResumoFaturamentos{

	public ResumoFaturamentosSR(List<AbstractFaturamento> faturamentos){
		super();
		this.faturamentos = new HashSet<AbstractFaturamento>(faturamentos);
		for (AbstractFaturamento faturamento : faturamentos) {
			this.getFaturamento().setCompetencia(faturamento.getCompetencia());
			this.getFaturamento().setDataGeracao(faturamento.getDataGeracao());
			this.getFaturamento().setNumeroEmpenho(faturamento.getNumeroEmpenho());
			this.getFaturamento().setDataPagamento(faturamento.getDataPagamento());
			this.getFaturamento().setValorConsultas(this.getFaturamento().getValorConsultas().add(faturamento.getValorConsultas()));
			this.getFaturamento().setValorConsultasOdonto(this.getFaturamento().getValorConsultasOdonto().add(faturamento.getValorConsultasOdonto()));
			this.getFaturamento().setValorExames(this.getFaturamento().getValorExames().add(faturamento.getValorExames()));
			this.getFaturamento().setValorExamesOdonto(this.getFaturamento().getValorExamesOdonto().add(faturamento.getValorExamesOdonto()));
			this.getFaturamento().setValorAtendimentosUrgencia(this.getFaturamento().getValorAtendimentosUrgencia().add(faturamento.getValorAtendimentosUrgencia()));
			this.getFaturamento().setValorInternacoes(this.getFaturamento().getValorInternacoes().add(faturamento.getValorInternacoes()));
			this.getFaturamento().getAlteracoesFaturamento().addAll(faturamento.getAlteracoesFaturamento());
			this.getFaturamento().setValorBruto(this.getFaturamento().getValorBruto().add(faturamento.getValorBruto()));
			this.getFaturamento().setPrestador(faturamento.getPrestador());
			this.getFaturamento().setCategoria(faturamento.getCategoria());
			this.getFaturamento().setStatus(faturamento.getStatus());
			this.getFaturamento().setValorRecursosDeferidos(this.getFaturamento().getValorRecursosDeferidos().add(faturamento.getValorRecursosDeferidos()));
		}
	}
	
	public ResumoFaturamentosSR( Map<Prestador, Set<AbstractFaturamento>> faturamentosPorPrestador, List<? extends AbstractFaturamento> faturamentos, int tipoResumo, Date competencia, Boolean exibirGuias) {
		super(faturamentosPorPrestador, faturamentos, tipoResumo, competencia, exibirGuias);
	}
			
	public ResumoFaturamentosSR(int indiceCategoria, boolean computaSubnivel, int tipoResumo, Date competencia, Boolean exibirGuias) {
		super(indiceCategoria, computaSubnivel, tipoResumo, competencia, exibirGuias);
	}
	
	public ResumoFaturamentosSR(List<AbstractFaturamento> faturamentos, List<Prestador> prestadores, int tipoResumo, Date competencia, Boolean exibirGuias) {
		super(faturamentos, prestadores, tipoResumo, competencia, exibirGuias);
	}
	
	public ResumoFaturamentosSR(List<AbstractFaturamento> faturamentos, Prestador prestador, int tipoResumo, Date competencia, boolean exibirGuias) {
		super(faturamentos, tipoResumo, prestador,  competencia, exibirGuias);
	}
	
	@Override
	public void computarHonorario(AbstractFaturamento faturamento) {
		super.computarHonorario(faturamento);		
		if(faturamento.getTipoFaturamento().equals("FaturamentoSR")){
			FaturamentoSR fat = (FaturamentoSR) faturamento;
			for (ProcedimentoInterface proc : fat.getProcedimentosAnestesicos()) {
				Profissional prof = proc.getAnestesista();
				DetalheHonorarioMedico detalheHonorarioMedico = new DetalheHonorarioMedico();
				detalheHonorarioMedico.setGuia(proc.getGuia());
				detalheHonorarioMedico.setNomeSegurado(proc.getGuia().getSegurado().getPessoaFisica().getNome());
				detalheHonorarioMedico.setProcedimento(proc);
				detalheHonorarioMedico.setProfissional(proc.getAnestesista() == null ? new Profissional() : proc.getAnestesista());
				detalheHonorarioMedico.setFuncao(DetalheHonorarioMedico.ANESTESISTA);
				detalheHonorarioMedico.setValor(detalheHonorarioMedico.updateValor());
				this.getDetalhesHonorariosMedicos().add(detalheHonorarioMedico);
			}
		}
	}
	@Override
	protected void computarDetalheValoresFaturamentos(AbstractFaturamento faturamento){
		if(faturamento.getTipoFaturamento().equals("FaturamentoSR")){
			FaturamentoSR fat = (FaturamentoSR) faturamento;
			DetalheValoresFaturamento detalhe;
			for (GuiaFaturavel guia : fat.getGuiasFaturaveis()) {
				detalhe = getDetalheValoresFaturamento(guia.getDataAtendimento());
				this.povoarDetalheValoresFaturamentos(detalhe, guia);
				this.getDetalhesValoresFaturamentos().add(detalhe);
			}
			detalhe = getDetalheValoresFaturamento(fat.getCompetencia());
			for (ProcedimentoInterface procedimento : fat.getProcedimentosAnestesicos()) {
				detalhe.setValorInternacoes(detalhe.getValorInternacoes().add(procedimento.getValorAnestesista()));
			}
			detalhe.setQuantidadeInternacoes(fat.getProcedimentosAnestesicos().size());
			this.getDetalhesValoresFaturamentos().add(detalhe);
		} else {
			super.computarDetalheValoresFaturamentos(faturamento);		
		}
	}
	
	public List<AbstractFaturamento> getFaturamentosCapitacao(){
		List<AbstractFaturamento> faturamentosCapitacao = new ArrayList<AbstractFaturamento>();
		for (AbstractFaturamento faturamento : getFaturamentos()) {
			if(faturamento.isFaturamentoNormal() && ((Faturamento)faturamento).isCapitacao()){
				faturamentosCapitacao.add(faturamento);
			}
		}
		return faturamentosCapitacao;
	}
	
}
