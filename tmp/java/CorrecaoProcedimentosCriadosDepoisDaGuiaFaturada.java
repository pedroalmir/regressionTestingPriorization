package br.com.infowaypi.ecare.correcaomanual;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Transaction;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AbstractFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AlteracaoFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.GuiaFaturavel;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ItemGuiaFaturamento;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

 public class CorrecaoProcedimentosCriadosDepoisDaGuiaFaturada {
	private StringBuilder stringBuilder;
	
	private Date dataMaximaSituacao = Utils.createData("15/01/2012");
	
	private UsuarioInterface drNadja = null;
	private UsuarioInterface root = (UsuarioInterface) new SearchAgent().findById(1L, Usuario.class);
	
	public CorrecaoProcedimentosCriadosDepoisDaGuiaFaturada() {
		stringBuilder = new StringBuilder();
		drNadja = (UsuarioInterface) new SearchAgent().findById(13628046L, Usuario.class);
	}

	private BigDecimal getTotalGuias(AbstractFaturamento faturamento){
		BigDecimal somatorioGuias = BigDecimal.ZERO;
		
		Set<GuiaFaturavel> guias = faturamento.getGuiasFaturaveis();
		for(GuiaFaturavel guia : guias){
			somatorioGuias = somatorioGuias.add(guia.getValorPagoPrestador());
//			somatorioGuias = somatorioGuias.add(guia.getValorTotal());
		}
		
		Set<AlteracaoFaturamento> alteracoesFaturamento = faturamento.getAlteracoesFaturamentoAtivos();
		for(AlteracaoFaturamento af : alteracoesFaturamento ){
			somatorioGuias = somatorioGuias.add(af.getSaldo());
		}

		Set<ItemGuiaFaturamento> itensGuiaFaturamento = faturamento.getItensGuiaFaturamento();
		for(ItemGuiaFaturamento ig : itensGuiaFaturamento){
			somatorioGuias = somatorioGuias.add(ig.getValor());
//			somatorioGuias = somatorioGuias.add(ig.getValorPago());
		}
		return somatorioGuias;
	}
	
	public void corrigirFaturamentos(List<Long> ids) throws Exception {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new In("idFluxoFinanceiro", ids));
		List<AbstractFaturamento> faturamentos = sa.list(AbstractFaturamento.class);
		for(AbstractFaturamento faturamento : faturamentos){
			System.out.println("Competência: " + faturamento.getCompetenciaFormatada() + "; Tipo: " + faturamento.getTipoFaturamento() + "; Prestador: " + faturamento.getPrestador().getPessoaJuridica().getFantasia());
			buscarProcedimentoAlteradoPosFaturamento(faturamento);
		}
	}
	
	private void verificarSucesso(AbstractFaturamento faturamento) {
		BigDecimal totalGuias = getTotalGuias(faturamento);
		BigDecimal valorBruto = faturamento.getValorBruto();
		if(MoneyCalculation.compare(valorBruto, totalGuias) != 0){
			System.out.println("A intervenção não corrigiu o faturamento. Valor faturamento: " + valorBruto + " Valor somatorio guias: " + totalGuias);
		}else {
			System.out.println("A intervenção corrigiu o faturamento.\n");
		}
	}
	
	private void recalcularGuia(GuiaFaturavel guia) {
		GuiaSimples guiaRecalculo = ImplDAO.findById(guia.getIdGuia(), GuiaSimples.class); 
		guiaRecalculo.recalcularValores();
	}
	
	private void recalcularGuias(AbstractFaturamento faturamento) {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("faturamento", faturamento));
		List<GuiaSimples> guias = sa.list(GuiaSimples.class);
		
		for(GuiaSimples g : guias){
			g.recalcularValores();
		}
	}
	
	private void buscarProcedimentoAlteradoPosFaturamento(AbstractFaturamento faturamento) throws Exception{
		Date dataFaturamento = null;
		Set<GuiaFaturavel> guias = faturamento.getGuiasFaturaveis();
		for(GuiaFaturavel guia : guias){
			dataFaturamento = guia.getSituacao(SituacaoEnum.FATURADA.descricao()).getDataSituacao();
			GuiaSimples<Procedimento> guiaSimples = (GuiaSimples<Procedimento>)guia;
			Set<Procedimento> procedimentos = guiaSimples.getProcedimentos();
			
			for(ProcedimentoInterface procedimento : procedimentos){
				if(Utils.compareDataHora(procedimento.getSituacao().getDataSituacao(), dataFaturamento) > 0){
					ajustarProcedimentoCriadoAposFaturamentoDrNadja(procedimento);
					ajustarProcedimentoGlosadoAposFaturamento(faturamento, procedimento, procedimentos);
				}
			}

			if(guia.getAutorizacao().equals("1463114")){
				guiaSimples.recalcularValores();
				guiaSimples.updateValorCoparticipacao();
			}
		}
		verificarSucesso(faturamento);
	}
	
	public void corrigirProcedimentos(GuiaFaturavel guia) throws Exception{
		Date dataFaturamento = guia.getSituacao(SituacaoEnum.FATURADA.descricao()).getDataSituacao();
		Set<Procedimento> procedimentos = ((GuiaSimples<Procedimento>)guia).getProcedimentos();
		for(ProcedimentoInterface procedimento : procedimentos){
			if(Utils.compareDataHora(procedimento.getSituacao().getDataSituacao(), dataFaturamento) > 0){
				ajustarProcedimentoCriadoAposFaturamentoDrNadja(procedimento);
				ajustarProcedimentoGlosadoAposFaturamento(guia.getFaturamento(), procedimento, procedimentos);
			}
		}
	}

	private void ajustarProcedimentoGlosadoAposFaturamento(AbstractFaturamento faturamento, ProcedimentoInterface procedimentoCriadoAposFaturamento, Set<Procedimento> procedimentos) throws Exception{
		for(ProcedimentoInterface procedimentoGlosadoAposFaturamento : procedimentos){
			if(procedimentoGlosadoAposFaturamento.getProcedimentoDaTabelaCBHPM().getCodigo().equals(procedimentoCriadoAposFaturamento.getProcedimentoDaTabelaCBHPM().getCodigo()) 
					&& procedimentoGlosadoAposFaturamento.getSituacao().getDescricao().equals(SituacaoEnum.GLOSADO.descricao())){
				mudarSituacaoProcedimento(faturamento, procedimentoCriadoAposFaturamento, procedimentoGlosadoAposFaturamento);
			}
		}
	}
	
	private void ajustarProcedimentoCriadoAposFaturamentoDrNadja(ProcedimentoInterface procedimento) throws Exception {
		if ((procedimento.getSituacao().getDescricao().equals(SituacaoEnum.PROCEDIMENTO_COM_HONORARIO_GERADO.descricao())
				|| procedimento.getSituacao().getDescricao().equals(SituacaoEnum.CONFIRMADO.descricao()))
				&& procedimento.getSituacao().getUsuario().equals(drNadja)) {
			
			System.out.println("Corrigindo guia alterada por Nadja!!");
			
			System.out.println(procedimento.getGuia().getAutorizacao() + " --> procedimento: " + procedimento.getProcedimentoDaTabelaCBHPM().getCodigo());
			System.out.println("Valor antes: " + procedimento.getGuia().getValorPagoPrestador() + " Situação antes: " + procedimento.getSituacao().getDescricao());
			
			procedimento.mudarSituacao(root, SituacaoEnum.CANCELADO.descricao(), "Ajuste por registro de procedimento após faturamento.", new Date());
			
			procedimento.getGuia().recalcularValores();
			procedimento.getGuia().updateValorCoparticipacao();
			
			System.out.println("Valor depois: " + procedimento.getGuia().getValorPagoPrestador()+ " Situação depois: " + procedimento.getSituacao().getDescricao());
			
			ImplDAO.save(procedimento);
			ImplDAO.save(procedimento.getGuia());
		}
	}

	private void mudarSituacaoProcedimento(AbstractFaturamento faturamento, ProcedimentoInterface procedimentoCriadoAposFaturamento, ProcedimentoInterface procedimentoGlosadoAposFaturamento) throws Exception {
		
		System.out.println("Corrigindo via cancelamento e reativação de procedimento");
		
		System.out.println(procedimentoCriadoAposFaturamento.getGuia().getAutorizacao() + " --> procedimento: " + procedimentoCriadoAposFaturamento.getProcedimentoDaTabelaCBHPM().getCodigo());
		System.out.println("Valor antes: " + procedimentoGlosadoAposFaturamento.getGuia().getValorPagoPrestador() + " Situação antes: " + procedimentoGlosadoAposFaturamento.getSituacao().getDescricao());
		
		procedimentoCriadoAposFaturamento.mudarSituacao(root, SituacaoEnum.CANCELADO.descricao(), "Ajuste por registro de procedimento após faturamento.", new Date());
		procedimentoGlosadoAposFaturamento.mudarSituacao(root, procedimentoGlosadoAposFaturamento.getSituacaoAnterior(procedimentoGlosadoAposFaturamento.getSituacao()).getDescricao(), "Ajuste por registro de procedimento após faturamento.", new Date());

		procedimentoGlosadoAposFaturamento.getGuia().recalcularValores();
		procedimentoGlosadoAposFaturamento.getGuia().updateValorCoparticipacao();
		
		System.out.println("Valor depois: " + procedimentoGlosadoAposFaturamento.getGuia().getValorPagoPrestador() + " Situação depois: " + procedimentoGlosadoAposFaturamento.getSituacao().getDescricao());
		
		ImplDAO.save(procedimentoCriadoAposFaturamento);
		ImplDAO.save(procedimentoGlosadoAposFaturamento);
		ImplDAO.save(procedimentoGlosadoAposFaturamento.getGuia());
		
	}

	public StringBuilder getStringBuilder() {
		return stringBuilder;
	}
	
	public static void main(String[] args) throws Exception {
		Long[] ids = new Long[]{1585385L};
								
		Transaction tx = HibernateUtil.currentSession().beginTransaction();
		CorrecaoProcedimentosCriadosDepoisDaGuiaFaturada correcao = new CorrecaoProcedimentosCriadosDepoisDaGuiaFaturada();
		correcao.corrigirFaturamentos(Arrays.asList(ids));
//		tx.commit();
		
		System.out.println("Done!");
	}
}
