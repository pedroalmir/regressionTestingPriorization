package br.com.infowaypi.ecarebc.service.financeiro;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.FlushMode;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Prestador.TipoIncremento;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AbstractFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.DetalheHonorarioMedico;
import br.com.infowaypi.ecarebc.financeiro.faturamento.Faturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.HonorarioMedico;
import br.com.infowaypi.ecarebc.financeiro.faturamento.TetoPrestadorFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.retencao.AbstractImposto;
import br.com.infowaypi.ecarebc.financeiro.faturamento.retencao.Imposto;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOutros;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.ecarebc.utils.CompetenciaUtils;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.molecular.parameter.NotEquals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.financeiro.Banco;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.CollectionUtils;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

@SuppressWarnings("unchecked")
public class FinanceiroService extends Service {
	
//	public static final BigDecimal TETO_HOSPITAL_SAO_MARCOS = new BigDecimal(1056473.28);
//	public static final Prestador HOSPITAL_SAO_MARCOS = ImplDAO.findById(374002L, Prestador.class);
//	public static  Map<Prestador, BigDecimal> tetoPorPrestador = processarArquivoTeto();
 	
	public List<Faturamento> gerarFaturamento(Banco banco,Date competenciaBase,	UsuarioInterface usuario, Date dataGeracaoPlanilha, Collection<TetoPrestadorFaturamento> tetos) throws Exception {
//		List<Prestador> prestadores = buscarPrestadores();

		List<Faturamento> faturamentos = new ArrayList<Faturamento>();
		Date competenciaAjustada = ajustarCompetencia(competenciaBase);
		List<GuiaSimples<ProcedimentoInterface>> guiasDaCompatencia = buscarGuias(competenciaBase, dataGeracaoPlanilha);
		
		Map<Prestador, Set<GuiaSimples<ProcedimentoInterface>>> mapGuias = CollectionUtils.groupBy(guiasDaCompatencia, "prestador", Prestador.class);
		Map<GuiaSimples, Set<ProcedimentoInterface>> mapProcedimentos = criaMapaProcedimentosHonorario(mapGuias);
		for (Prestador prestador : mapGuias.keySet()) {
				gerarFaturamento(competenciaBase, faturamentos, competenciaAjustada, prestador, usuario, dataGeracaoPlanilha, tetos, mapGuias,mapProcedimentos);
		}
	
		return faturamentos;
	}

	private Map<GuiaSimples, Set<ProcedimentoInterface>> criaMapaProcedimentosHonorario(Map<Prestador, Set<GuiaSimples<ProcedimentoInterface>>> mapaGuiasPorPrestador) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Set<GuiaSimples> guias = new HashSet<GuiaSimples>();
		for (Collection<GuiaSimples<ProcedimentoInterface>> set : mapaGuiasPorPrestador.values()) {
			for (GuiaSimples guia : set) {
				if(guia.isConsultaUrgencia() || guia.isAtendimentoUrgencia() 
						|| guia.isInternacao() || guia.isCirurgiaOdonto()){
					guias.add(guia);
				}
			}
		}
		System.out.println("GUIAS::::: "+guias.size());
		List<ProcedimentoInterface> procedimentos = HibernateUtil.currentSession().createCriteria(Procedimento.class)
		.add(Expression.in("discriminator", Arrays.asList("PCSR","POU")))
		.add(Expression.in("guia", guias))
		.add(Expression.not(Expression.in("situacao.descricao", Arrays.asList(SituacaoEnum.CANCELADO.descricao(),SituacaoEnum.NAO_AUTORIZADO.descricao()))))
		.list();
		
		Map<GuiaSimples, Set<ProcedimentoInterface>> mapProcedimentos = CollectionUtils.groupBy(procedimentos, "guia", GuiaSimples.class);
		return mapProcedimentos;
	}

	private List<Prestador> buscarPrestadores() {
		Session session = HibernateUtil.currentSession();
		Criteria criteria = session.createCriteria(Prestador.class);
		criteria.add(Expression.eq("situacao.descricao", SituacaoEnum.ATIVO.descricao()));
		List<Prestador> prestadores = criteria.list();
		return prestadores;
	}
	
	private static Map<Prestador, BigDecimal> processarTeto(Collection<TetoPrestadorFaturamento> tetos){
		Map<Prestador, BigDecimal> tetoPorPrestador = new HashMap<Prestador, BigDecimal>();
//		Scanner sc;
//		try {
//			sc = new Scanner(new FileReader("c:\\TetosPorPrestador.txt"));
//			while(sc.hasNext()) {
//				String linha = sc.next();
//				StringTokenizer token = new StringTokenizer(linha,";");
//				long idPrestador = Long.valueOf(token.nextToken());
//				BigDecimal teto = new BigDecimal(Float.valueOf(token.nextToken()));
//				Prestador prestador = ImplDAO.findById(idPrestador, Prestador.class);
//				tetoPorPrestador.put(prestador, teto);
//			}
			
//			for (Prestador prestador : tetoPorPrestador.keySet()) {
//				System.out.print(prestador.getPessoaJuridica().getFantasia() + ";");
//				System.out.println(tetoPorPrestador.get(prestador));
//			}
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
		
		for (TetoPrestadorFaturamento teto : tetos) {
			tetoPorPrestador.put(teto.getPrestador(), teto.getTeto());
		}		
		
		return tetoPorPrestador;
	}

	public BigDecimal gerarFaturamentoProfissionaisCredenciados(GuiaSimples guia, Map<GuiaSimples, 
			Set<ProcedimentoInterface>> mapProcedimentos, Date competencia, List<Faturamento> faturamentos, 
			Faturamento faturamento) throws Exception{
		
		
		
		Set<ProcedimentoInterface> procedimentos = mapProcedimentos.get(guia);
		if(procedimentos == null)
			return MoneyCalculation.rounded(BigDecimal.ZERO);
		
		Set<ProcedimentoOutros> procedimentosOutros = new HashSet<ProcedimentoOutros>();
		Set<ProcedimentoCirurgico> procedimentosCirurgicos = new HashSet<ProcedimentoCirurgico>();
		for (ProcedimentoInterface procedimento : procedimentos) {
			if(procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO)
				procedimentosCirurgicos.add((ProcedimentoCirurgico) procedimento);
			else if(procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_OUTROS) 
				procedimentosOutros.add((ProcedimentoOutros) procedimento);
		}
		//TODO SETAR TODOS OS ATRIBUTOS DE FATURAMENTO NO CASO DO PRESTADOR NÃO TER GUIAS PARA SER FATURADA
		BigDecimal valorTotalProfissionais = BigDecimal.ZERO;
		valorTotalProfissionais.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		for (ProcedimentoOutros procedimento : procedimentosOutros) {
			if(procedimento.getValorTotal().floatValue() == 0)
				continue;
			
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
						honorario = new HonorarioMedico();
						honorario.getProcedimentos().add(procedimento);
						honorario.setProfissional(procedimento.getProfissionalResponsavel());
						procedimento.getProfissionalResponsavel().getHonorariosMedicos().add(honorario);
						honorario.setFaturamento(prestadorDestinoHonorario.getFaturamento(competencia));
						prestadorDestinoHonorario.getFaturamento(competencia).getHonorariosMedicos().add(honorario);
						
						guia.setValorPagoPrestador(guia.getValorPagoPrestador().subtract(honorario.getValor(procedimento,DetalheHonorarioMedico.PROFISSIONAL_RESPONSAVEL)));
						guia.setValorPagoPrestador(MoneyCalculation.rounded(guia.getValorPagoPrestador()));
					}
					
					
//					ImplDAO.save(honorario);
//					ImplDAO.save(guia);
//					ImplDAO.save(procedimento.getProfissionalResponsavel());
				}
			}
		}
		
		for (ProcedimentoCirurgico procedimento : procedimentosCirurgicos) {
			if(procedimento.getValorTotal().floatValue() == 0)
				continue;
			
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
						System.out.println(procedimento.getProfissionalAuxiliar1().getPessoaFisica().getNome());
						System.out.println(prestadorDestinoHonorario);
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
		
		System.out.println(">>>"+prestador.getPessoaJuridica().getFantasia());
		Faturamento faturamento = getFaturamento(prestador, faturamentos);
//			prestador.getFaturamento(competencia);
		if( faturamento != null){
			System.out.println("PEGANDO UM FATURAMENTO JÁ INSTANCIADO...");
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
		
		Faturamento faturamentoPrestadorDaGuia = getFaturamento(procedimento.getGuia().getPrestador(), faturamentos);
//			procedimento.getGuia().getPrestador().getFaturamento(competencia);
		if(faturamentoPrestadorDaGuia == null){
			faturamentoPrestadorDaGuia = new Faturamento();
			faturamentoPrestadorDaGuia.setCompetencia(competencia);
			faturamentoPrestadorDaGuia.setStatus(Constantes.FATURAMENTO_ABERTO);
			faturamentos.add(faturamentoPrestadorDaGuia);
			procedimento.getGuia().getPrestador().getFaturamentos().add(faturamentoPrestadorDaGuia);
			faturamentoPrestadorDaGuia.setPrestador(procedimento.getGuia().getPrestador());
			faturamentoPrestadorDaGuia.setCategoria(procedimento.getGuia().getPrestador().getTipoPrestador());
			faturamentoPrestadorDaGuia.setNome(procedimento.getGuia().getPrestador().getPessoaJuridica().getFantasia());
		}
		faturamentoPrestadorDaGuia.atualizarSomatorioGuias(procedimento.getGuia(), valorProfissional, TipoIncremento.NEGATIVO);
//		ImplDAO.save(faturamentoPrestadorDaGuia);	
	}

	public void gerarFaturamento(Date competenciaBase, List<Faturamento> faturamentos, Date competenciaAjustada, 
			Prestador prestador, UsuarioInterface usuario, Date dataGeracaoPlanilha, Collection<TetoPrestadorFaturamento> tetos, Map<Prestador, Set<GuiaSimples<ProcedimentoInterface>>> mapGuias, Map<GuiaSimples, Set<ProcedimentoInterface>> mapProcedimentos) throws Exception {
		
//		List<GuiaSimples> todasAsGuias = buscarGuias(competenciaBase, prestador, dataGeracaoPlanilha);
		List<GuiaSimples> todasAsGuias = new ArrayList(mapGuias.get(prestador));
		System.out.println("N° GUIAS: "+ todasAsGuias.size());
		
		
		if (!todasAsGuias.isEmpty()) {
			
			for (GuiaSimples guia : todasAsGuias) {
				if(guia.getGuiaOrigem() != null) {
					guia.getGuiaOrigem().getValorCoParticipacao();
					guia.getGuiaOrigem().getAutorizacao();
				}
				guia.getValorCoParticipacao();
				guia.getAutorizacao();
			}
		
			Map<Prestador, BigDecimal> tetoPorPrestador = processarTeto(tetos);
			Faturamento faturamento = getFaturamento(prestador, faturamentos);
			if(faturamento == null){
				faturamento = new Faturamento();
				faturamentos.add(faturamento);
			}

			faturamento.setCategoria(prestador.getTipoPrestador());
			faturamento.setCompetencia(competenciaBase);
			faturamento.setInformacaoFinanceira(prestador.getInformacaoFinanceira());
			faturamento.setPrestador(prestador);
			prestador.getFaturamentos().add(faturamento);

			faturamento.setPisPasep(prestador.getPessoaJuridica().getPispasep());
			faturamento.setNome(Utils.normalize(prestador.getPessoaJuridica().getFantasia()));
			faturamento.setTipoPessoa(prestador.isPessoaFisica() ? AbstractImposto.PESSOA_FISICA : AbstractImposto.PESSOA_JURIDICA);
			faturamento.setDataGeracao(new Date());
			faturamento.setStatus(Constantes.FATURAMENTO_ABERTO);
			
			BigDecimal valorTotalGuias = BigDecimal.ZERO;
			BigDecimal valorProfissionais = BigDecimal.ZERO;
			valorTotalGuias.setScale(2, BigDecimal.ROUND_HALF_UP);
			
			BigDecimal valorAcumulado = BigDecimal.ZERO;
			
			Utils.sort(todasAsGuias, true, "valorTotal");
			for (GuiaSimples<Procedimento> guia : todasAsGuias) {
				
				if(tetoPorPrestador.keySet().contains(prestador)) {
					valorAcumulado = valorAcumulado.add(guia.getValorTotal());
					if(valorAcumulado.compareTo(tetoPorPrestador.get(prestador)) <= 0) {
						guia.setValorPagoPrestador(guia.getValorTotal());
						valorProfissionais = gerarFaturamentoProfissionaisCredenciados(guia,mapProcedimentos, competenciaBase, faturamentos, faturamento);
						faturamento.addGuia(guia);
						valorTotalGuias = valorTotalGuias.add(guia.getValorTotal());
						valorTotalGuias = valorTotalGuias.subtract(valorProfissionais);
						faturamento.setSomatoriosGuias(guia);
//						if (!guia.isSituacaoAtual(SituacaoEnum.FATURADA.descricao())) {
//							guia.mudarSituacao(usuario, SituacaoEnum.FATURADA.descricao(),MotivoEnum.GERACAO_FATURAMENTO.getMessage(), new Date());
//						}
					}else {
						continue;
					}
				}else {
					valorAcumulado = valorAcumulado.add(guia.getValorTotal());
					guia.setValorPagoPrestador(guia.getValorTotal());
					valorProfissionais = gerarFaturamentoProfissionaisCredenciados(guia,mapProcedimentos, competenciaBase, faturamentos, faturamento);
					faturamento.addGuia(guia);
					valorTotalGuias = valorTotalGuias.add(guia.getValorTotal());
					valorTotalGuias = valorTotalGuias.subtract(valorProfissionais);
					faturamento.setSomatoriosGuias(guia);
//					if (!guia.isSituacaoAtual(SituacaoEnum.FATURADA.descricao())) {
//						guia.mudarSituacao(usuario, SituacaoEnum.FATURADA.descricao(),MotivoEnum.GERACAO_FATURAMENTO.getMessage(), new Date());
//					}
				}
				
			}
				
			faturamento.setValorBruto(faturamento.getValorBruto().add(valorTotalGuias));
			faturamento.setValorLiquido(faturamento.getValorLiquido().add(valorTotalGuias));
			
			System.out.println(faturamento.getValorBruto());
			
//			prestador.tocarObjetos();
			
		}
	}

	private List<GuiaSimples<ProcedimentoInterface>> buscarGuias(Date competenciaBase,Date dataGeracaoPlanilha) {
		Session session = HibernateUtil.currentSession();
		
		String[] situacoes = {SituacaoEnum.CONFIRMADO.descricao(),SituacaoEnum.AUDITADO.descricao()};
		
		session.setFlushMode(FlushMode.COMMIT);
		Criteria criteria = session.createCriteria(GuiaSimples.class);
		
		// Evita que duas pessoas gerem o faturamento ao mesmo tempo
		criteria.setLockMode(LockMode.WRITE);
		
		//Data do Termino de Atendimento no período de 21/12/08 à 20/01/09
		
//		criteria.add(Expression.eq("prestador", prestador));	
		criteria.add(Expression.in("situacao.descricao", situacoes));
		criteria.add(Expression.ge("dataTerminoAtendimento", CompetenciaUtils.getInicioCompetencia(competenciaBase)));		
		criteria.add(Expression.le("dataTerminoAtendimento", CompetenciaUtils.getFimCompetencia(competenciaBase)));
		criteria.add(Expression.lt("situacao.dataSituacao", dataGeracaoPlanilha));
		criteria.setFetchMode("guiaOrigem", FetchMode.JOIN);
		criteria.addOrder(Order.asc("dataTerminoAtendimento"));
		criteria.add(Expression.isNull("faturamento"));

		 List<GuiaSimples<ProcedimentoInterface>> todasAsGuias = criteria.list();
		return todasAsGuias;
	}

	protected void saveImpostos() throws Exception {
		Imposto.saveNovoInss();
		Imposto.saveNovoIss(AbstractImposto.PESSOA_FISICA);
		Imposto.saveNovoIss(AbstractImposto.PESSOA_JURIDICA);
		Imposto.saveNovoIr(AbstractImposto.PESSOA_FISICA);
		Imposto.saveNovoIr(AbstractImposto.PESSOA_JURIDICA);
	}

	protected Date ajustarCompetencia(Date competencia) {
		GregorianCalendar calendario = new GregorianCalendar();
		calendario.setTime(competencia);
		calendario.set(Calendar.DAY_OF_MONTH, 25);
		return calendario.getTime();
	}
	
	protected List<Faturamento> getFaturamentos(Date competencia,Banco banco, boolean incluirFechados) throws ValidateException {
		SearchAgent sa = getSearchAgent();
		sa.addParameter(new Equals("competencia", competencia));
		SearchAgent saPrest = new SearchAgent(); 
		if (banco != null)
			saPrest.addParameter(new Equals("informacaoFinanceira.banco", banco));
		
		List<Prestador> prestadores = saPrest.list(Prestador.class);
		sa.addParameter(new In("prestador", prestadores));
		sa.addParameter(new NotEquals("status", Constantes.FATURAMENTO_CANCELADO));
		if (!incluirFechados)
			sa.addParameter(new NotEquals("status", Constantes.FATURAMENTO_FECHADO)); 

		List<Faturamento> faturasGeradas = sa.list(AbstractFaturamento.class);
//		if (incluirFechados)
//			Assert.isNotEmpty(faturasGeradas,"Não foi gerado faturamento nessa competência.");
//		else
//			Assert.isNotEmpty(faturasGeradas,"Já foram geradas contas de faturamento para esta competência.");
		return faturasGeradas;
	}
	
	protected void validaCompetencia(Date competenciaEscolhida) throws SQLException, ValidateException {
		Calendar ultimaComp = Calendar.getInstance();
		ultimaComp.setTime(new Date());
		ultimaComp.set(Calendar.DATE, 1);
		Date competenciaAtual = ultimaComp.getTime();
		if (competenciaEscolhida.compareTo(competenciaAtual) > 0)
			throw new ValidateException("A competência informada deve ser menor que a competência atual.");
		
		SearchAgent sa = getSearchAgent();
		Criteria criteria = sa.createCriteriaFor(Faturamento.class);
		
		Date ultimaCompetencia = (Date) criteria.setProjection(Projections.projectionList().
									add(Projections.max("competencia"))).uniqueResult();

		if (ultimaCompetencia == null){
			ultimaComp.setTime(competenciaEscolhida);
		}else{
			ultimaComp.setTime(ultimaCompetencia);
			ultimaComp.add(Calendar.MONTH, 1);
		}
		
		Date ultimaCompADD = ultimaComp.getTime();
		
		if(!Utils.format(ultimaCompADD,"MM/yyyy").equals(Utils.format(competenciaEscolhida,"MM/yyyy")))
			throw new ValidateException("A competência informada deve ser " + Utils.format(ultimaCompADD,"MM/yyyy"));
	}	
	
	protected Date getCompetencia(String competencia) throws ValidateException {
		Date competenciaEscolhida = null;
		if (!Utils.isStringVazia(competencia)){
			try {
				competenciaEscolhida = Utils.gerarCompetencia(competencia);
			} catch (Exception e){
				e.printStackTrace();
				throw new ValidateException("A competência informada é inválida.");
			}
		}
		else
			throw new ValidateException("A competência deve ser informada.");
		return competenciaEscolhida;
	}	
	
	protected int getDataLimite(Date competencia){
		GregorianCalendar calendario = new GregorianCalendar();
		calendario.setTime(competencia);
		return calendario.getActualMaximum(Calendar.DATE);
	}
	
	public List<Faturamento> gerarContasFaturamento(Banco banco, Date competencia, UsuarioInterface usuario) throws Exception {
		List<Faturamento> faturas = this.getFaturamentos(competencia, banco, false);
		
		for (Faturamento faturamento : faturas) {
			faturamento.setStatus(Constantes.FATURAMENTO_FECHADO);
		}			
		return faturas;
	}
	
	public Faturamento getFaturamento(Prestador prestador, List<Faturamento> faturamentos){
		for (Faturamento abstractFaturamento : faturamentos) {
			if(abstractFaturamento.getPrestador().equals(prestador))
				return abstractFaturamento;
		}
		return null;
	}
}
