package br.com.infowaypi.ecare.financeiro.faturamento;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.com.infowaypi.ecare.associados.PrestadorAnestesista;
import br.com.infowaypi.ecare.procedimentos.ProcedimentoCirurgicoSR;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Prestador.TipoIncremento;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.AbstractFinanceiroService;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AbstractFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.DetalheHonorarioMedico;
import br.com.infowaypi.ecarebc.financeiro.faturamento.Faturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.FaturamentoPassivo;
import br.com.infowaypi.ecarebc.financeiro.faturamento.HonorarioMedico;
import br.com.infowaypi.ecarebc.financeiro.faturamento.TetoPrestadorFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.retencao.AbstractImposto;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOutros;
import br.com.infowaypi.ecarebc.service.financeiro.FinanceiroService;
import br.com.infowaypi.ecarebc.service.financeiro.GeracaoFaturamentoService;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Like;
import br.com.infowaypi.msr.financeiro.Banco;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

public class FinanceiroServiceSR extends GeracaoFaturamentoService{
	
//	public static List<String> guiasNaoOrdenadas = new ArrayList<String>();
	
	public List<Faturamento> gerarFaturamento(Banco banco,Date competenciaBase,	int dataLimite, UsuarioInterface usuario, Date dataGeracaoPlanilha, Collection<TetoPrestadorFaturamento> tetos) throws Exception {
		Session session = HibernateUtil.currentSession();
		session.setFlushMode(FlushMode.COMMIT);
		Criteria criteria = session.createCriteria(Prestador.class);
		List<Faturamento> faturamentos = new ArrayList<Faturamento>();
		List<AbstractFaturamento> todosFaturamentos = new ArrayList<AbstractFaturamento>();
		if (banco != null)
			criteria.add(Expression.eq("informacaoFinanceira.banco",banco));
//			criteria.add(Expression.not(Expression.in("idPrestador", AbstractFinanceiroService.getIdsPrestadoresNaoPagos())));
			criteria.add(Expression.eq("idPrestador",528079L));
			
		List<Prestador> prestadores = criteria.list();

//		saveImpostos();
		int quantPrest = prestadores.size();
		int countPrest = 0;
		Date competenciaAjustada = ajustarCompetencia(competenciaBase);
		
//		alimentaLista();
		
		for (Prestador prestador : prestadores) {
			System.out.println(++countPrest + "/" + quantPrest + " - Prestador: " + prestador.getPessoaJuridica().getFantasia());
			if(!prestador.getTipoPrestador().equals(Prestador.TIPO_PRESTADOR_ANESTESISTA)){
//				gerarFaturamento(competenciaBase, faturamentos, competenciaAjustada, prestador, usuario, dataGeracaoPlanilha, tetos);
			}
			else{
				System.out.println("Gerando os faturamentos dos procedimentos...");
  				gerarFaturamento(competenciaBase, faturamentos, competenciaAjustada, (PrestadorAnestesista)prestador, usuario);
				System.out.println("Gerando os faturamentos das guias...");
//				gerarFaturamento(competenciaBase, faturamentos, competenciaAjustada, prestador, usuario, dataGeracaoPlanilha, tetos);
				System.out.println("Concluído Coopanest!");
			}				
		}
		
		return faturamentos;
	}
	

	public BigDecimal gerarFaturamentoProfissionaisCredenciados(GuiaSimples guia, Date competencia, 
			List<Faturamento> faturamentos, Faturamento faturamento) throws Exception{
		//TODO SETAR TODOS OS ATRIBUTOS DE FATURAMENTO NO CASO DO PRESTADOR NÃO TER GUIAS PARA SER FATURADA
		Set<ProcedimentoOutros> procedimentosOutros = guia.getProcedimentos(ProcedimentoOutros.class);
		Set<ProcedimentoCirurgicoSR> procedimentosCirurgicos = guia.getProcedimentos(ProcedimentoCirurgicoSR.class);
		BigDecimal valorTotalProfissionais = MoneyCalculation.rounded(BigDecimal.ZERO);
		
		for (ProcedimentoOutros procedimento : procedimentosOutros) {
			if(!procedimento.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())){
				if((procedimento.getProfissionalResponsavel() != null) && (procedimento.getProfissionalResponsavel().hasPrestadorProprio())){

					BigDecimal valorProfissional = BigDecimal.ZERO;
					valorProfissional.setScale(2, BigDecimal.ROUND_HALF_UP);
					valorProfissional = valorProfissional.add(procedimento.getValorProfissionalResponsavel());
					valorTotalProfissionais = valorTotalProfissionais.add(valorProfissional);
					Prestador prestadorProprio = procedimento.getProfissionalResponsavel().getPrestadorProprio();
					Prestador prestadorDestinoHonorario = null;
					if(prestadorProprio != null){
						System.out.println("Há prestadoresPróprios encontrados");
						prestadorDestinoHonorario = prestadorProprio;
					}
					else{
						for (Prestador prest : procedimento.getProfissionalResponsavel().getPrestadores()) {
							prestadorDestinoHonorario = prest;
							break;
						}	
					}	
					
					saveFaturamento(competencia, faturamentos, valorProfissional, prestadorDestinoHonorario, procedimento);
					procedimento.getProfissionalResponsavel().setDestinoFaturamento(prestadorDestinoHonorario.getIdPrestador());
					
					HonorarioMedico honorario = procedimento.getProfissionalResponsavel().getHonorarioMedico(competencia);
					if(honorario != null){
						honorario.getProcedimentos().add(procedimento);
						
						guia.setValorPagoPrestador(guia.getValorPagoPrestador().subtract(honorario.getValor(procedimento,DetalheHonorarioMedico.PROFISSIONAL_RESPONSAVEL)));
						guia.setValorPagoPrestador(MoneyCalculation.rounded(guia.getValorPagoPrestador()));
					}
					else{
						honorario = new HonorarioMedico(competencia, procedimento, procedimento.getProfissionalResponsavel(), prestadorDestinoHonorario, false);
						prestadorDestinoHonorario.getFaturamento(competencia).getHonorariosMedicos().add(honorario);
						
						guia.setValorPagoPrestador(guia.getValorPagoPrestador().subtract(honorario.getValor(procedimento,DetalheHonorarioMedico.PROFISSIONAL_RESPONSAVEL)));
						guia.setValorPagoPrestador(MoneyCalculation.rounded(guia.getValorPagoPrestador()));
					}
					
					procedimento.getProfissionalResponsavel().setDestinoFaturamento(prestadorDestinoHonorario.getIdPrestador());
					
				}
			}
		}
		
		for (ProcedimentoCirurgicoSR procedimento : procedimentosCirurgicos) {
			if(!procedimento.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())){
				if((procedimento.getProfissionalResponsavel() != null) && (procedimento.getProfissionalResponsavel().hasPrestadorProprio())){
					BigDecimal valorProfissional = BigDecimal.ZERO;
					valorProfissional.setScale(2, BigDecimal.ROUND_HALF_UP);
					valorProfissional = valorProfissional.add(procedimento.getValorProfissionalResponsavel());
					valorTotalProfissionais = valorTotalProfissionais.add(valorProfissional);
					Prestador prestadorProprio = procedimento.getProfissionalResponsavel().getPrestadorProprio();
					Prestador prestadorDestinoHonorario = null;
					if(prestadorProprio != null){
						System.out.println("Há prestadoresPróprios encontrados");
						prestadorDestinoHonorario = prestadorProprio;
					}
					else{
						for (Prestador prest : procedimento.getProfissionalResponsavel().getPrestadores()) {
							prestadorDestinoHonorario = prest;
							break;
						}	
					}	
					
					saveFaturamento(competencia, faturamentos, valorProfissional, prestadorDestinoHonorario, procedimento);
					procedimento.getProfissionalResponsavel().setDestinoFaturamento(prestadorDestinoHonorario.getIdPrestador());
					
					HonorarioMedico honorario = procedimento.getProfissionalResponsavel().getHonorarioMedico(competencia);
					if(honorario != null){
						honorario.getProcedimentos().add(procedimento);
						
						guia.setValorPagoPrestador(guia.getValorPagoPrestador().subtract(honorario.getValor(procedimento,DetalheHonorarioMedico.CIRURGIAO)));
						guia.setValorPagoPrestador(MoneyCalculation.rounded(guia.getValorPagoPrestador()));
					}
					else{
						honorario = new HonorarioMedico();
						honorario.getProcedimentos().add(procedimento);
						honorario.setProfissional(procedimento.getProfissionalResponsavel());
						procedimento.getProfissionalResponsavel().getHonorariosMedicos().add(honorario);
						honorario.setFaturamento(prestadorDestinoHonorario.getFaturamento(competencia));
						prestadorDestinoHonorario.getFaturamento(competencia).getHonorariosMedicos().add(honorario);
						
						guia.setValorPagoPrestador(guia.getValorPagoPrestador().subtract(honorario.getValor(procedimento,DetalheHonorarioMedico.CIRURGIAO)));
						guia.setValorPagoPrestador(MoneyCalculation.rounded(guia.getValorPagoPrestador()));
					}
					
					procedimento.getProfissionalResponsavel().setDestinoFaturamento(prestadorDestinoHonorario.getIdPrestador());
					
//					ImplDAO.save(honorario);
//					ImplDAO.save(guia);
//					ImplDAO.save(procedimento.getProfissionalResponsavel());
					
				}
				
				if((procedimento.getProfissionalAuxiliar1() != null) && (procedimento.getProfissionalAuxiliar1().hasPrestadorProprio())){
					BigDecimal valorProfissional = BigDecimal.ZERO;
					valorProfissional.setScale(2, BigDecimal.ROUND_HALF_UP);
					valorProfissional = valorProfissional.add(procedimento.getValorAuxiliar1());
					valorTotalProfissionais = valorTotalProfissionais.add(valorProfissional);
					Prestador prestadorProprio = procedimento.getProfissionalAuxiliar1().getPrestadorProprio();
					Prestador prestadorDestinoHonorario = null;
					if(prestadorProprio != null){
						System.out.println("Há prestadoresPróprios encontrados");
						prestadorDestinoHonorario = prestadorProprio;
					}
					else{
						for (Prestador prest : procedimento.getProfissionalAuxiliar1().getPrestadores()) {
							prestadorDestinoHonorario = prest;
							break;
						}	
					}
					
					saveFaturamento(competencia, faturamentos,valorProfissional, prestadorDestinoHonorario, procedimento);

					HonorarioMedico honorario = procedimento.getProfissionalAuxiliar1().getHonorarioMedico(competencia);
					if(honorario != null){
						honorario.getProcedimentos().add(procedimento);
						
						guia.setValorPagoPrestador(guia.getValorPagoPrestador().subtract(honorario.getValor(procedimento,DetalheHonorarioMedico.AUXILIAR_1)));
						guia.setValorPagoPrestador(MoneyCalculation.rounded(guia.getValorPagoPrestador()));
					}
					else{
						honorario = new HonorarioMedico();
						honorario.getProcedimentos().add(procedimento);
						honorario.setProfissional(procedimento.getProfissionalAuxiliar1());
						procedimento.getProfissionalAuxiliar1().getHonorariosMedicos().add(honorario);
						honorario.setFaturamento(prestadorDestinoHonorario.getFaturamento(competencia));
						prestadorDestinoHonorario.getFaturamento(competencia).getHonorariosMedicos().add(honorario);
						
						guia.setValorPagoPrestador(guia.getValorPagoPrestador().subtract(honorario.getValor(procedimento,DetalheHonorarioMedico.AUXILIAR_1)));
						guia.setValorPagoPrestador(MoneyCalculation.rounded(guia.getValorPagoPrestador()));
					}
					
					procedimento.getProfissionalAuxiliar1().setDestinoFaturamento(prestadorDestinoHonorario.getIdPrestador());
					
//					ImplDAO.save(honorario);
//					ImplDAO.save(guia);
//					ImplDAO.save(procedimento.getProfissionalAuxiliar1());
					
				}
				
				if((procedimento.getProfissionalAuxiliar2() != null) && (procedimento.getProfissionalAuxiliar2().hasPrestadorProprio())){
					BigDecimal valorProfissional = BigDecimal.ZERO;
					valorProfissional.setScale(2, BigDecimal.ROUND_HALF_UP);
					valorProfissional = valorProfissional.add(procedimento.getValorAuxiliar2());
					valorTotalProfissionais = valorTotalProfissionais.add(valorProfissional);
					Prestador prestadorProprio = procedimento.getProfissionalAuxiliar2().getPrestadorProprio();
					Prestador prestadorDestinoHonorario = null;
					if(prestadorProprio != null){
						System.out.println("Há prestadoresPróprios encontrados");
						prestadorDestinoHonorario = prestadorProprio;
					}
					else{
						for (Prestador prest : procedimento.getProfissionalAuxiliar2().getPrestadores()) {
							prestadorDestinoHonorario = prest;
							break;
						}	
					}
					
					saveFaturamento(competencia, faturamentos, valorProfissional, prestadorDestinoHonorario, procedimento);

					HonorarioMedico honorario = procedimento.getProfissionalAuxiliar2().getHonorarioMedico(competencia);
					if(honorario != null){
						honorario.getProcedimentos().add(procedimento);
						
						guia.setValorPagoPrestador(guia.getValorPagoPrestador().subtract(honorario.getValor(procedimento,DetalheHonorarioMedico.AUXILIAR_2)));
						guia.setValorPagoPrestador(MoneyCalculation.rounded(guia.getValorPagoPrestador()));
					}
					else{
						honorario = new HonorarioMedico();
						honorario.getProcedimentos().add(procedimento);
						honorario.setProfissional(procedimento.getProfissionalAuxiliar2());
						procedimento.getProfissionalAuxiliar2().getHonorariosMedicos().add(honorario);
						honorario.setFaturamento(prestadorDestinoHonorario.getFaturamento(competencia));
						prestadorDestinoHonorario.getFaturamento(competencia).getHonorariosMedicos().add(honorario);
						
						guia.setValorPagoPrestador(guia.getValorPagoPrestador().subtract(honorario.getValor(procedimento,DetalheHonorarioMedico.AUXILIAR_2)));
						guia.setValorPagoPrestador(MoneyCalculation.rounded(guia.getValorPagoPrestador()));
					}
					
					procedimento.getProfissionalAuxiliar2().setDestinoFaturamento(prestadorDestinoHonorario.getIdPrestador());
					
//					ImplDAO.save(honorario);
//					ImplDAO.save(guia);
//					ImplDAO.save(procedimento.getProfissionalAuxiliar2());
				}
				
				if((procedimento.getProfissionalAuxiliar3() != null) && (procedimento.getProfissionalAuxiliar3().hasPrestadorProprio())){
					BigDecimal valorProfissional = BigDecimal.ZERO;
					valorProfissional.setScale(2, BigDecimal.ROUND_HALF_UP);
					valorProfissional = valorProfissional.add(procedimento.getValorAuxiliar3());
					valorTotalProfissionais = valorTotalProfissionais.add(valorProfissional);
					Prestador prestadorProprio = procedimento.getProfissionalAuxiliar3().getPrestadorProprio();
					Prestador prestadorDestinoHonorario = null;
					if(prestadorProprio != null){
						System.out.println("Há prestadoresPróprios encontrados");
						prestadorDestinoHonorario = prestadorProprio;
					}
					else{
						for (Prestador prest : procedimento.getProfissionalAuxiliar3().getPrestadores()) {
							prestadorDestinoHonorario = prest;
							break;
						}	
					}	
					
					saveFaturamento(competencia, faturamentos, valorProfissional, prestadorDestinoHonorario, procedimento);

					HonorarioMedico honorario = procedimento.getProfissionalAuxiliar3().getHonorarioMedico(competencia);
					if(honorario != null){
						honorario.getProcedimentos().add(procedimento);
						
						guia.setValorPagoPrestador(guia.getValorPagoPrestador().subtract(honorario.getValor(procedimento,DetalheHonorarioMedico.AUXILIAR_3)));
						guia.setValorPagoPrestador(MoneyCalculation.rounded(guia.getValorPagoPrestador()));
					}
					else{
						honorario = new HonorarioMedico();
						honorario.getProcedimentos().add(procedimento);
						honorario.setProfissional(procedimento.getProfissionalAuxiliar3());
						procedimento.getProfissionalAuxiliar3().getHonorariosMedicos().add(honorario);
						honorario.setFaturamento(prestadorDestinoHonorario.getFaturamento(competencia));
						prestadorDestinoHonorario.getFaturamento(competencia).getHonorariosMedicos().add(honorario);
						
						guia.setValorPagoPrestador(guia.getValorPagoPrestador().subtract(honorario.getValor(procedimento,DetalheHonorarioMedico.AUXILIAR_3)));
						guia.setValorPagoPrestador(MoneyCalculation.rounded(guia.getValorPagoPrestador()));
					}
					
					procedimento.getProfissionalAuxiliar3().setDestinoFaturamento(prestadorDestinoHonorario.getIdPrestador());
					
//					ImplDAO.save(honorario);
//					ImplDAO.save(guia);
//					ImplDAO.save(procedimento.getProfissionalAuxiliar3());
					
				}
			}
		}
		return valorTotalProfissionais;
		
	}
	
	

	public void saveFaturamento(Date competencia, List<Faturamento> faturamentos, BigDecimal valorProfissional, Prestador prestador, Procedimento procedimento) throws Exception {
		Faturamento faturamento;
		if(prestador.getFaturamento(competencia) != null){
			System.out.println("PEGANDO UM FATURAMENTO JÁ INSTANCIADO...");
			faturamento = prestador.getFaturamento(competencia);
		}
		else{
			faturamento = new Faturamento();
			faturamento.setCompetencia(competencia);
			faturamento.setStatus(Constantes.FATURAMENTO_ABERTO);
			faturamentos.add(faturamento);
			prestador.getFaturamentos().add(faturamento);
			faturamento.setPrestador(prestador);
			faturamento.setCategoria(prestador.getTipoPrestador());
			faturamento.setNome(prestador.getPessoaJuridica().getFantasia());
		}
		faturamento.setValorBruto(faturamento.getValorBruto().add(valorProfissional));
		faturamento.setValorLiquido(faturamento.getValorLiquido().add(valorProfissional));
		faturamento.atualizarSomatorioGuias(procedimento.getGuia(), valorProfissional, TipoIncremento.POSITIVO);
//		ImplDAO.save(faturamento);
		
		Faturamento faturamentoPrestadorDaGuia = procedimento.getGuia().getPrestador().getFaturamento(competencia);
		if(faturamentoPrestadorDaGuia == null){
			faturamentoPrestadorDaGuia = new Faturamento();
			prestador.getFaturamentos().add(faturamentoPrestadorDaGuia);
			faturamentoPrestadorDaGuia.setPrestador(prestador);
		}
		faturamentoPrestadorDaGuia.atualizarSomatorioGuias(procedimento.getGuia(), valorProfissional, TipoIncremento.NEGATIVO);
//		ImplDAO.save(faturamentoPrestadorDaGuia);
		
	}
	
	public void gerarFaturamento(Date competenciaBase, List<Faturamento> faturamentos, Date competenciaAjustada, PrestadorAnestesista prestadorAnestesista, UsuarioInterface usuario) throws Exception {
		Session session = HibernateUtil.currentSession();
		Criteria criteria = session.createCriteria(ProcedimentoInterface.class);
		criteria.setLockMode(LockMode.WRITE);
		List<ProcedimentoInterface> procedimentos = criteria.createAlias("guia", "g")
				.add(Expression.eq("prestadorAnestesista", prestadorAnestesista))
				.add(Expression.eq("situacao.descricao",SituacaoEnum.FECHADO.descricao()))
//				.add(Expression.ge("g.situacao.dataSituacao",Utils.parse("06/08/2009")))
				.add(Expression.le("g.dataTerminoAtendimento", Utils.parse("20/06/2009"))).list();
		System.out.println("procedimentos " + procedimentos.size());
		if (!procedimentos.isEmpty()) {
			FaturamentoSR faturamento;
			if(prestadorAnestesista.getFaturamento(competenciaAjustada) != null){
				faturamento = (FaturamentoSR) prestadorAnestesista.getFaturamento(competenciaAjustada);
			}
			else{
				faturamento = new FaturamentoSR();
			}
			faturamento.setCategoria(prestadorAnestesista.getTipoPrestador());
			faturamento.setCompetencia(competenciaBase);
			faturamento.setInformacaoFinanceira(prestadorAnestesista.getInformacaoFinanceira());
			faturamento.setPrestador(prestadorAnestesista);

			faturamento.setPisPasep(prestadorAnestesista.getPessoaJuridica().getPispasep());
			faturamento.setNome(Utils.normalize(prestadorAnestesista.getPessoaJuridica().getFantasia()));
			faturamento.setTipoPessoa(prestadorAnestesista.isPessoaFisica() ? AbstractImposto.PESSOA_FISICA: AbstractImposto.PESSOA_JURIDICA);
			faturamento.setStatus(Constantes.FATURAMENTO_ABERTO);
			
//			ImplDAO.save(faturamento);

			BigDecimal valorTotalProcedimentos = BigDecimal.ZERO;
			valorTotalProcedimentos.setScale(2, BigDecimal.ROUND_HALF_UP);
			for (ProcedimentoInterface procedimento : procedimentos) {
				procedimento.setFaturamento(faturamento);
				faturamento.getProcedimentosAnestesicos().add(procedimento);
				valorTotalProcedimentos = valorTotalProcedimentos.add(procedimento.getValorAnestesista());

				if (!procedimento.isSituacaoAtual(SituacaoEnum.FATURADA.descricao())) {
					procedimento.mudarSituacao(usuario, SituacaoEnum.FATURADA.descricao(),MotivoEnum.GERACAO_FATURAMENTO.getMessage(), new Date());
				}
			}
			faturamento.setValorBruto(valorTotalProcedimentos);
			faturamento.setValorLiquido(valorTotalProcedimentos);
			faturamento.processarRetencoes();
			faturamentos.add(faturamento);
		}
	}
	
//	private void alimentaLista() throws Exception {
//		Scanner sc = new Scanner(new FileReader("C:\\Documents and Settings\\Infoway\\Desktop\\Faturamento 082008\\GuiasNaoOrdenadas.txt"));
//		while(sc.hasNext()) {
//			guiasNaoOrdenadas.add(sc.nextLine());
//		}
//	}
	
//	public static void main(String[] args) {
//		FinanceiroServiceSR service = new FinanceiroServiceSR();
//		try {
//			Transaction tm = HibernateUtil.currentSession().beginTransaction();
//			service.salvarFaturamento(service.gerarFaturamento("06/2009", ImplDAO.findById(698203L, Usuario.class)));
//			tm.commit();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		System.out.println("clonesr".hashCode());
//		
//	}

}
