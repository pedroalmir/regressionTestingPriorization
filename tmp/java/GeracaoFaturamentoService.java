package br.com.infowaypi.ecarebc.service.financeiro;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.financeiro.conta.ContaInterface;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AbstractFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.Faturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.HonorarioMedico;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ResumoFaturamentos;
import br.com.infowaypi.ecarebc.financeiro.faturamento.TetoPrestadorFaturamento;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.financeiro.Banco;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

public class GeracaoFaturamentoService extends FinanceiroService {

	public String competencia;
	
	public GeracaoFaturamentoService informarCompetencia(String competencia){
		this.competencia = competencia;
		return this;
	}
	
	public ResumoFaturamentos gerarFaturamento(Date competencia, UsuarioInterface usuario, Date dataGeracaoPlanilha, Collection<TetoPrestadorFaturamento> tetos) throws Exception {
		return gerarFaturamento(competencia, null, usuario, dataGeracaoPlanilha,tetos);
	}
	
	public ResumoFaturamentos salvarFaturamento(ResumoFaturamentos resumo) throws Exception{
		
		for (AbstractFaturamento faturamento : resumo.getFaturamentos()) {
//			Prestador prestador = faturamento.getPrestador();
//			Map<TipoIndice, BigDecimal> limites = prestador.loadLimitesConsumoMensais();
//			
//			ColecaoConsumosInterface colecaoConsumos =  prestador.getConsumoIndividual();
//			String descricaoRole = prestador.getUsuario().getRole();
//			Role role = Role.getRoleByValor(descricaoRole);
			
//			colecaoConsumos.criarConsumoBI(new Date(), Periodo.MENSAL_FATURAMENTO, limites, true, TipoConsumo.PRESTADOR, role);
//			
//			for (GuiaSimples guia : faturamento.getGuias()){
//				//Atualizando o consumo financeiro 
//				StateMachineConsumo.updateConsumoFaturamento(guia, true, true);
//				
//				//Atualizando o consumo financeiro anestesistas
//				StateMachineConsumo.updateCustoAnestesista(guia, false);
//			}
			
			ImplDAO.save(faturamento);
		}
		
		System.out.println("Fazendo commit...");
		return resumo;
	}
	
	public ResumoFaturamentos gerarFaturamento(Date competencia, Banco banco, UsuarioInterface usuario, Date dataGeracaoPlanilha, Collection<TetoPrestadorFaturamento> tetos) throws Exception {
//		Date competenciaEscolhida = getCompetencia(competencia);
		
		if(Utils.compararCompetencia(competencia, Utils.gerarCompetencia().getTime()) >= 0)
			throw new ValidateException("Não é possível gerar faturamento para um mês corrente ou posterior.");
		
		List<Faturamento> faturasGeradas = getFaturamentos(competencia,banco, true);
//		if(!faturasGeradas.isEmpty())
//			throw new ValidateException("Já foi gerado faturamento para esta competência.");
		
		List<Faturamento> faturamentos = this.gerarFaturamento(banco,competencia, usuario, dataGeracaoPlanilha, tetos);
		Assert.isNotEmpty(faturamentos, "Nenhum faturamento foi gerado.");
		return new ResumoFaturamentos(faturamentos, null,ResumoFaturamentos.RESUMO_CATEGORIA, competencia, false);
	}

	public ResumoFaturamentos gerarContasFaturamento(String competencia, Banco banco, UsuarioInterface usuario) throws Exception {
		Assert.isNotEmpty(competencia, "A competência deve ser informada.");
		Date competenciaEscolhida = super.getCompetencia(competencia);
		
//		verificar se é necessário recalcular retenções
//		fService.processarRetencoes(competenciaEscolhida);
		
		List<? extends AbstractFaturamento> faturas = gerarContasFaturamento(banco,competenciaEscolhida,usuario);
		return new ResumoFaturamentos(faturas,null,ResumoFaturamentos.RESUMO_CATEGORIA, competenciaEscolhida, false);
	}
	
	public ResumoFaturamentos gerarContasFaturamento(String competencia, UsuarioInterface usuario) throws Exception {
		Assert.isNotEmpty(competencia, "A competência deve ser informada.");
		Date competenciaEscolhida = super.getCompetencia(competencia);
		SearchAgent sa = getSearchAgent();
		List<AbstractFaturamento> faturas = new ArrayList<AbstractFaturamento>();
		List<Prestador> prestadores = sa.list(Prestador.class);
		List<Banco> bancos = sa.list(Banco.class);
		
		for (Prestador prestador : prestadores) {
			if (prestador.getInformacaoFinanceira().getBanco() != null)
				bancos.add((Banco) prestador.getInformacaoFinanceira().getBanco());
		}
		
		for (Banco bancoAtual : bancos) {
			faturas.addAll(gerarContasFaturamento(bancoAtual,competenciaEscolhida,usuario));
		}
		
		ResumoFaturamentos resumo = new ResumoFaturamentos(faturas,null,ResumoFaturamentos.RESUMO_CATEGORIA, competenciaEscolhida, false);
		Assert.isNotEmpty(resumo.getFaturamentos(), "Nenhum Faturamento foi encontrado.");
		return resumo;
	}
	
	public ResumoFaturamentos procurarFaturamento(String competencia) throws ValidateException{
		Assert.isNotEmpty(competencia, "A competência deve ser informada.");
		Date competeciaEscolhida = getCompetencia(competencia);
		List<Faturamento> faturasGeradas = getFaturamentos(competeciaEscolhida, null, true);
		Assert.isNotEmpty(faturasGeradas, "Não há faturamento a cancelar nessa competência.");
		ResumoFaturamentos resumo = new ResumoFaturamentos(faturasGeradas,null,ResumoFaturamentos.RESUMO_CATEGORIA, competeciaEscolhida, false);
		return resumo;
	}	
	
	public void cancelarFaturamento(ResumoFaturamentos resumo) throws Exception{
		Assert.isNotNull(resumo.getFaturamentos(), "Não há faturamento a cancelar nessa competência.");
		
		for(AbstractFaturamento faturamento : resumo.getFaturamentos()){
			this.cancelarFaturamento(faturamento);
		}
	}
	
	public void cancelarFaturamento(AbstractFaturamento faturamento) throws Exception{
		faturamento.tocarObjetos();
		faturamento.setStatus(Constantes.FATURAMENTO_CANCELADO);
		
		for(ContaInterface conta : faturamento.getColecaoContas().getContas())
			conta.mudarSituacao(null, Constantes.SITUACAO_CANCELADO, MotivoEnum.CANCELAMENTO_DE_FATURAS.getMessage(), new Date());
		
		//Atualizando o consumo
//		Prestador prestador = faturamento.getPrestador();
//		ColecaoConsumosInterface colecaoConsumos = prestador.getConsumoIndividual();
//		
//		ConsumoBIInterface consumoFaturamento = colecaoConsumos.getConsumoBI(faturamento.getDataGeracao(), PeriodoSW.MENSAL_FATURAMENTO);
//		consumoFaturamento.cancelar();
//		ImplDAO.save(consumoFaturamento);
		
		for (HonorarioMedico honorarioMedico : faturamento.getHonorariosMedicos()) {
			honorarioMedico.setProfissional(null);
			ImplDAO.save(honorarioMedico);
		}
		
		ImplDAO.save(faturamento);
	}


}
