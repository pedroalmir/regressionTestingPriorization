package br.com.infowaypi.ecare.financeiro.faturamento;

import java.util.Date;
import java.util.List;

import org.hibernate.FlushMode;

import br.com.infowaypi.ecare.constantes.Constantes;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AbstractFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.GuiaFaturavel;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ResumoFaturamentos;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ordenador.Ordenador;
import br.com.infowaypi.ecarebc.service.financeiro.FaturamentoDAO;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.NotEquals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.Situacao;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

public class FaturamentoAlternativoService {
	
	public ResumoFinanceiro informarDados(Integer identificador, UsuarioInterface usuario, ProgressBarFaturamento progressBar) throws Exception {
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
		
		progressBar.setProgressStatusText("Processando ordenador...");
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("identificador", identificador));
		Ordenador ordenador = sa.uniqueResult(Ordenador.class);
		
		Assert.isNotNull(ordenador, "Nº do ordenador não foi encontrado. Favor verificar se o número informado está correto.");
		validarDuplicidadeFaturamento(ordenador);
		
		ResumoFaturamentos resumoNormal = gerarFaturamentoNormal(usuario, progressBar, ordenador);
		ResumoFaturamentos resumoPassivo = gerarFaturamentoPassivo(usuario, progressBar, ordenador);
		
		progressBar.setProgressStatusText("Finalizando geração do faturamento...");
		
		ResumoFinanceiro resumo = new ResumoFinanceiro(ordenador, resumoNormal, resumoPassivo);
		return resumo;
	}

	private ResumoFaturamentos gerarFaturamentoPassivo(
			UsuarioInterface usuario, ProgressBarFaturamento progressBar,
			Ordenador ordenador)
			throws Exception {
		
		progressBar.setProgressStatusText("Buscando guias...");
		progressBar.startMockProgress(30, 80000);//50 segundos
		
		List<GuiaFaturavel> guiasFaturamentoPassivo = FaturamentoDAO.buscarGuiasFaturamentoPassivo(ordenador.getCompetencia(), ordenador.getDataRecebimento(), ordenador.getPrestadoresTetoZerado(false));
		GeracaoFaturamentoPassivo geracaoFaturamentoPassivo = new GeracaoFaturamentoPassivo(progressBar);
		ResumoFaturamentos resumoPassivo = geracaoFaturamentoPassivo.gerarFaturamento(ordenador.getCompetencia(), ordenador.getDataRecebimento(), ordenador.getTetos(false), usuario, guiasFaturamentoPassivo);
		return resumoPassivo;
	}

	private ResumoFaturamentos gerarFaturamentoNormal(UsuarioInterface usuario,
			ProgressBarFaturamento progressBar, Ordenador ordenador) throws Exception {
		progressBar.setProgressStatusText("Buscando guias ...");
		progressBar.startMockProgress(30, 80000);//50 segundos
		
		List<GuiaFaturavel> guiasFaturamentoNormal = FaturamentoDAO.buscarGuias(ordenador.getCompetencia(), ordenador.getDataRecebimento(), ordenador.getPrestadoresTetoZerado(true));
		GeracaoFaturamentoNormal geracaoFaturamentoNormal = new GeracaoFaturamentoNormal(progressBar);
		ResumoFaturamentos resumoNormal = geracaoFaturamentoNormal.gerarFaturamento(ordenador.getCompetencia(), ordenador.getDataRecebimento(), ordenador.getTetos(true), usuario, guiasFaturamentoNormal);

		return resumoNormal;
	}
	
	public ResumoFinanceiro verificarGeracao(Integer identificador, UsuarioInterface usuario) throws Exception{
		if (ProgressBarFaturamento.getExcecaoDoFluxo() != null){
			throw ProgressBarFaturamento.getExcecaoDoFluxo();
		}
		return ProgressBarFaturamento.getResumoFinanceiro();
	}
	
	public ResumoFinanceiro conferirDados(ResumoFinanceiro resumo, UsuarioInterface usuario) throws Exception {
		for (AbstractFaturamento faturamento : resumo.getFaturamentos()) {
			for (GuiaFaturavel guia: faturamento.getGuiasFaturaveis()) {
				if(!guia.isSituacaoAtual(SituacaoEnum.FATURADA.descricao())){
					mudarSituacao(usuario, guia);
				}
			}
			ImplDAO.save(faturamento);
		}
		return resumo;
	}
	
	/**
	 * Valida se já existe faturamento gerado para a competência do ordenador.
	 */
	private void validarDuplicidadeFaturamento(Ordenador ordenador) throws ValidateException{
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("competencia", ordenador.getCompetencia()));
		sa.addParameter(new NotEquals("status", Constantes.FATURAMENTO_CANCELADO));
		Integer faturamentos = sa.resultCount(AbstractFaturamento.class);
		if (faturamentos > 0){
			throw new ValidateException("Já foi gerado faturamento para a competência "+Utils.format(ordenador.getCompetencia(), "MM/yyyy"));
		}
	}

	private void mudarSituacao(UsuarioInterface usuario, GuiaFaturavel guia) 
			throws Exception {
		
		SituacaoInterface item = new Situacao();
		item.setDescricao(SituacaoEnum.FATURADA.descricao());
		item.setMotivo(MotivoEnum.GERACAO_FATURAMENTO.getMessage());
		item.setUsuario(usuario);
		item.setColecaoSituacoes(guia.getColecaoSituacoes());
		item.setDataSituacao(new Date());
		item.setOrdem(guia.getSituacao().getOrdem()+1);
		
		guia.setSituacao(item);
		ImplDAO.save(item);
	}
}
