package br.com.infowaypi.ecare.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import br.com.infowaypi.ecare.arquivos.ArquivoBase;
import br.com.infowaypi.ecare.enums.TipoLiberacaoForaLimiteEnum;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.enuns.TipoLimiteEnum;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsulta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaOdonto;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExameOdonto;
import br.com.infowaypi.ecarebc.consumo.ConsumoInterface;
import br.com.infowaypi.ecarebc.consumo.periodos.Periodo;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.utils.DateUtils;
import br.com.infowaypi.molecular.HibernateUtil;

/**
 * @author Marcus bOolean
 * @modify Anderson, Eduardo
 * Classe que representa um resumo de limites de um benefciario.
 */
@SuppressWarnings("unchecked")
public class ResumoLimitesBeneficiario {
	
	private ArquivoBase arquivoPdf;

	private Segurado segurado;
	private Date dataBase;
	private Date primeiroDiaAno;
	private Date ultimoDiaAno;
	
	private ConsumoInterface consumoMensal;
	private ConsumoInterface consumoTrimestral;
	private ConsumoInterface consumoSemestral;
	private ConsumoInterface consumoAnual;
	
	private List<GuiaConsulta<ProcedimentoInterface>> consultas;
	private List<GuiaExame<ProcedimentoInterface>> exames;
	private List<GuiaConsultaOdonto> consultasOdonto;
	private List<GuiaExameOdonto> tratamentoOdonto;

	
	/**
	 * Constantes que retornam a quantidade de consultas utilizadas
	 * */
	private int consultasMensalUtilizadas = 0;
	private int consultasTrimestralUtilizadas = 0;
	private int consultasSemestralUtilizadas = 0;
	private int consultasAnualUtilizadas = 0;
	
	/**
	 * Constantes que retornam a quantidade de consultas permitidas
	 * */
	private int consultasMensalPermitidas = 0;
	private int consultasTrimestralPermitidas = 0;
	private int consultasSemestralPermitidas = 0;
	private int consultasAnualPermitidas = 0;
	
	/**
	 * Constantes que retornam a quantidade de consultas ainda disponiveis
	 * */
	private int consultasMensalDisponiveis = 0;
	private int consultasTrimestralDisponiveis = 0;
	private int consultasSemestralDisponiveis = 0;
	private int consultasAnualDisponiveis = 0;
	
	
	/**
	 * Constantes que retornam a quantidade de exames utilizados
	 * */
	private int examesMensalUtilizados = 0;
	private int examesTrimestralUtilizados = 0;
	private int examesSemestralUtilizados = 0;
	private int examesAnualUtilizados = 0;
	
	/**
	 * Constantes que retornam a quantidade de exames permitidos
	 * */
	private int examesMensalPermitidos = 99;
	private int examesTrimestralPermitidos = 0;
	private int examesSemestralPermitidos = 0;
	private int examesAnualPermitidos = 0;
	
	/**
	 * Constantes que retornam a quantidade de exames permitidos
	 * */
	private int examesMensalDisponiveis = 0;
	private int examesTrimestralDisponiveis = 0;
	private int examesSemestralDisponiveis = 0;
	private int examesAnualDisponiveis = 0;
	
	/**
	 * Constantes que retornam a quantidade de consultasOdontologico utilizados
	 * */
	private int consultasOdontologicoMensalUtilizados = 0;
	private int consultasOdontologicoTrimestralUtilizados = 0;
	private int consultasOdontologicoSemestralUtilizados = 0;
	private int consultasOdontologicoAnualUtilizados = 0;
	
	/**
	 * Constantes que retornam a quantidade de consultasOdontologico Disponiveis
	 * */
	private int consultasOdontologicoMensalDisponiveis = 0;
	private int consultasOdontologicoTrimestralDisponiveis = 0;
	private int consultasOdontologicoSemestralDisponiveis = 0;
	private int consultasOdontologicoAnualDisponiveis = 0;
	
	/**
	 * Constantes que retornam a quantidade de consultasOdontologico Permitidos
	 * */
	private int consultasOdontologicoMensalPermitidos = 0;
	private int consultasOdontologicoTrimestralPermitidos = 0;
	private int consultasOdontologicoSemestralPermitidos = 0;
	private int consultasOdontologicoAnualPermitidos = 0;

	/**
	 * Constantes que retornam a quantidade de tratamentoOdontologico Utilizados
	 * */
	private int tratamentoOdontologicoMensalUtilizados = 0;
	private int tratamentoOdontologicoTrimestralUtilizados = 0;
	private int tratamentoOdontologicoSemestralUtilizados = 0;
	private int tratamentoOdontologicoAnualUtilizados = 0;
	
	/**
	 * Constantes que retornam a quantidade de tratamentoOdontologico Disponiveis
	 * */
	private int tratamentoOdontologicoMensalDisponiveis = 0;
	private int tratamentoOdontologicoTrimestralDisponiveis = 0;
	private int tratamentoOdontologicoSemestralDisponiveis = 0;
	private int tratamentoOdontologicoAnualDisponiveis = 0;
	
	/**
	 * Constantes que retornam a quantidade de tratamentoOdontologico Permitidos
	 * */
	private int tratamentoOdontologicoMensalPermitidos = 0;
	private int tratamentoOdontologicoTrimestralPermitidos = 0;
	private int tratamentoOdontologicoSemestralPermitidos = 0;
	private int tratamentoOdontologicoAnualPermitidos = 0;
	
	private TipoLimiteEnum tipoLimite;
	
	/**
	 * Construtor da classe ResumoLimitesBeneficiario
	 * @param segurado
	 * @param dataBase
	 * */
	public ResumoLimitesBeneficiario(Segurado segurado, Date dataBase) throws Exception {
		this.segurado = segurado;
		this.dataBase = dataBase;
		primeiroDiaAno 	= DateUtils.getDataInicialAno(dataBase);
		ultimoDiaAno 	= DateUtils.getDataFinalAno(dataBase);
		this.computa();
	}
	
	public ResumoLimitesBeneficiario(Segurado segurado, Date dataBase, TipoLimiteEnum tipoLimite) throws Exception {
		this.segurado = segurado;
		this.dataBase = dataBase;
		primeiroDiaAno 	= DateUtils.getDataInicialAno(dataBase);
		ultimoDiaAno 	= DateUtils.getDataFinalAno(dataBase);
		this.tipoLimite = tipoLimite;
		this.computa();
	}

	/**
	 * Retorna um segurado [Segurado buscado pela classe RelatorioLimiteDoBeneficiário]
	 * */
	public Segurado getSegurado() {
		return segurado;
	}

	public List<GuiaConsulta<ProcedimentoInterface>> getConsultas() {
		return consultas;
	}

	public void setConsultas(List<GuiaConsulta<ProcedimentoInterface>> consultas) {
		this.consultas = consultas;
	}

	public List<GuiaExame<ProcedimentoInterface>> getExames() {
		return exames;
	}

	public void setExames(List<GuiaExame<ProcedimentoInterface>> exames) {
		this.exames = exames;
	}

	public List<GuiaConsultaOdonto> getConsultasOdonto() {
		return consultasOdonto;
	}

	public void setConsultasOdonto(List<GuiaConsultaOdonto> consultasOdonto) {
		this.consultasOdonto = consultasOdonto;
	}

	public List<GuiaExameOdonto> getTratamentoOdonto() {
		return tratamentoOdonto;
	}

	public void setTratamentoOdonto(List<GuiaExameOdonto> tratamentoOdonto) {
		this.tratamentoOdonto = tratamentoOdonto;
	}

	public TipoLimiteEnum getTipoLimite() {
		return tipoLimite;
	}

	public void setTipoLimite(TipoLimiteEnum tipoLimite) {
		this.tipoLimite = tipoLimite;
	}

	/**
	 * Configura um segurado [Segurado buscado pela classe RelatorioLimiteDoBeneficiário]
	 * @param segurado
	 * */
	public void setSegurado(Segurado segurado) {
		this.segurado = segurado;
	}
	/**
	 * Retorna uma data [Data base buscado pela classe RelatorioLimiteDoBeneficiário]
	 * */
	public Date getDataBase() {
		return dataBase;
	}

	/**
	 * Configura uma data
	 * @param dataBase
	 * */
	public void setDataBase(Date dataBase) {
		this.dataBase = dataBase;
	}

	/**
	 * Metodo que faz o calculo das quantidade referentes ao consumo individual 
	 * classificando procedimentos [Consultas, Exames, ConsutasOdontologicas, TratamentoOdontologico] por periodo
	 * @throws Exception 
	 * */
	public void computa() throws Exception {
		ConsumoInterface consumoAnual = segurado.getConsumoIndividual().getConsumo(new Date(), Periodo.ANUAL);
		consumoAnual = consumoAnual != null ? consumoAnual : criaConsumo(Periodo.ANUAL);
		computaConsumoAnual(consumoAnual);
		this.setConsumoAnual(consumoAnual);
		
		ConsumoInterface consumoSemestral = segurado.getConsumoIndividual().getConsumo(new Date(), Periodo.SEMESTRAL);
		consumoSemestral  = consumoSemestral != null ? consumoSemestral : criaConsumo(Periodo.SEMESTRAL);
		computaConsumoSemestral(consumoSemestral);
		this.setConsumoSemestral(consumoSemestral);
		
		ConsumoInterface consumoTrimestral = segurado.getConsumoIndividual().getConsumo(new Date(), Periodo.TRIMESTRAL);	
		consumoTrimestral  = consumoTrimestral != null ? consumoTrimestral : criaConsumo(Periodo.TRIMESTRAL);
		computaConsumoTrimestral(consumoTrimestral);
		this.setConsumoTrimestral(consumoTrimestral);
		
		ConsumoInterface consumoMensal = segurado.getConsumoIndividual().getConsumo(new Date(), Periodo.MENSAL);
		consumoMensal  = consumoMensal != null ? consumoMensal : criaConsumo(Periodo.MENSAL);
		computaConsumoMensal(consumoMensal);
		this.setConsumoMensal(consumoMensal);
		
		List<String> situacoes = Arrays.asList(SituacaoEnum.AGENDADA.descricao(), 
				SituacaoEnum.CONFIRMADO.descricao(), SituacaoEnum.ABERTO.descricao(),
				SituacaoEnum.FECHADO.descricao(), SituacaoEnum.AUDITADO.descricao(),
				SituacaoEnum.FATURADA.descricao(), SituacaoEnum.PAGO.descricao(), 
				SituacaoEnum.AUTORIZADO.descricao(), SituacaoEnum.ENVIADO.descricao(),
				SituacaoEnum.RECEBIDO.descricao(),SituacaoEnum.DEVOLVIDO.descricao(), 
				SituacaoEnum.SOLICITADO.descricao());
		
		buscarConsultas(situacoes);
		buscarExames(situacoes);
		buscarConsultaOdonto(situacoes);
		buscarTratamentoOdonto(situacoes);
	}

	private void buscarTratamentoOdonto(List<String> situacoes) {
		
		List<Integer> tiposLiberacao = new ArrayList<Integer>();
		tiposLiberacao.add(TipoLiberacaoForaLimiteEnum.LIBERACAO_AUDITOR.codigo());
		tiposLiberacao.add(TipoLiberacaoForaLimiteEnum.LIBERACAO_MPPS.codigo());
		
		this.tratamentoOdonto =  HibernateUtil.currentSession().createCriteria(GuiaExameOdonto.class)
							.add(Expression.eq("segurado", segurado))
							.add(Expression.between("dataAtendimento", primeiroDiaAno, ultimoDiaAno))
							.add(Expression.eq("tipoDeGuia", "GEXOD"))
							.add(Expression.in("liberadaForaDoLimite", tiposLiberacao))
							.add(Expression.in("situacao.descricao", situacoes))
							.addOrder(Order.asc("dataAtendimento"))
							.list();
	}

	private void buscarConsultaOdonto(List<String> situacoes) {
		
		List<Integer> tiposLiberacao = new ArrayList<Integer>();
		tiposLiberacao.add(TipoLiberacaoForaLimiteEnum.LIBERACAO_AUDITOR.codigo());
		tiposLiberacao.add(TipoLiberacaoForaLimiteEnum.LIBERACAO_MPPS.codigo());
		
		this.consultasOdonto =  HibernateUtil.currentSession().createCriteria(GuiaConsultaOdonto.class)
							.add(Expression.eq("segurado", segurado))
							.add(Expression.between("dataAtendimento", primeiroDiaAno, ultimoDiaAno))
							.add(Expression.eq("tipoDeGuia", "GCSOD"))
							.add(Expression.in("liberadaForaDoLimite", tiposLiberacao))
							.add(Expression.in("situacao.descricao", situacoes))
							.addOrder(Order.asc("dataAtendimento"))
							.list();
		
		Iterator<GuiaConsultaOdonto> iterator = this.consultasOdonto.iterator();
		while (iterator.hasNext()) {
			GuiaConsultaOdonto guia = iterator.next();
			boolean isClinicoPromotor = guia.isConsultaOdontoClinicoPromotor();
			boolean isPericiaInicial = guia.isConsultaOdontoPericiaInicial();
			if (isClinicoPromotor || isPericiaInicial) {
				iterator.remove();
			}
		}
		
	}

	private void buscarExames(List<String> situacoes) {
		this.exames =  HibernateUtil.currentSession().createCriteria(GuiaExame.class)
							.add(Expression.eq("segurado", segurado))
							.add(Expression.disjunction()
									.add(Expression.and(Expression.isNull("dataAtendimento"), Expression.between("dataMarcacao", 
											primeiroDiaAno, ultimoDiaAno)))
									.add(Expression.between("dataAtendimento", primeiroDiaAno, ultimoDiaAno)))
							.add(Expression.eq("tipoDeGuia", "GEX"))
							.add(Expression.eq("liberadaForaDoLimite", 0))
							.add(Expression.eq("exameExterno", false))
							.add(Expression.in("situacao.descricao", situacoes))
							.addOrder(Order.asc("dataAtendimento"))
							.list();
	}

	private void buscarConsultas(List<String> situacoes) {
		this.consultas =  HibernateUtil.currentSession().createCriteria(GuiaConsulta.class)
							.add(Expression.eq("segurado", segurado))
							.add(Expression.between("dataAtendimento", primeiroDiaAno, ultimoDiaAno))
							.add(Expression.eq("tipoDeGuia", "GCS"))
							.add(Expression.eq("liberadaForaDoLimite", 0))
							.add(Expression.in("situacao.descricao", situacoes))
							.addOrder(Order.asc("dataAtendimento"))
							.list();
	}

	private ConsumoInterface criaConsumo(Periodo periodo) {
		ConsumoInterface consumo;
		consumo = segurado.getConsumoIndividual().criarConsumo(new Date(), periodo, periodo.getLimiteConsultas(), periodo.getLimiteExames(), 
				periodo.getLimiteConsultasOdonto(), periodo.getLimiteTratamentosOdonto());
		return consumo;
	}

	private void computaConsumoAnual(ConsumoInterface consumo) {
		//CONSULTAS UTILIZADAS
		this.setConsultasAnualUtilizadas((consumo.getNumeroConsultasConfirmadas() + consumo.getNumeroConsultasAgendadas()));
		//LIMITE DE CONSULTAS
		this.setConsultasAnualPermitidas(consumo.getLimiteConsultas().intValue());
		//COONSULTAS PERMITIDAS
		this.setConsultasAnualDisponiveis(this.getConsultasAnualPermitidas() - this.getConsultasAnualUtilizadas());

		//CONSULTAS ODONTOLOGICAS UTILIZADAS
		this.setConsultasOdontologicoAnualUtilizados(consumo.getNumeroConsultasOdontoConfirmadas());
		//LIMITE DE CONS.ODONTOLOGICA
		this.setConsultasOdontologicoAnualPermitidos(consumo.getLimiteConsultasOdonto().intValue());
		//CONS. ODONTOLOGICAS DISPONIVEIS
		this.setConsultasOdontologicoAnualDisponiveis(this.getConsultasOdontologicoAnualPermitidos() - this.getConsultasOdontologicoAnualUtilizados());

		//EXAMES UTILIZADOS [Confirmado + Agendados]
		this.setExamesAnualUtilizados(consumo.getNumeroProcedimentosExamesConfirmados() + consumo.getNumeroProcedimentosExamesAgendados());
		//LIMITES DE EXAMES
		this.setExamesAnualPermitidos(consumo.getLimiteExames().intValue());
		//EXAMES DISPONIVEIS
		int examesDisponiveis =  ((this.getExamesAnualPermitidos() - this.getExamesAnualUtilizados()) >= 0)? 
																		(this.getExamesAnualPermitidos() - this.getExamesAnualUtilizados()): 0; 
		this.setExamesAnualDisponiveis(examesDisponiveis);

		//TRATAMENTO ODONTOLOGICO
		this.setTratamentoOdontologicoAnualUtilizados(consumo.getNumeroTratamentosOdontoAgendados() + consumo.getNumeroTratamentosOdontoConfirmados());
		//LIMITES DE TRAT. ODONTOLOGICO
		this.setTratamentoOdontologicoAnualPermitidos(consumo.getLimiteTratamentosOdonto().intValue());
		//TRAT. ODONTOLOGICO DISPONIVEIS
		this.setTratamentoOdontologicoAnualDisponiveis(this.getTratamentoOdontologicoAnualPermitidos() - this.getTratamentoOdontologicoAnualUtilizados());
	}

	private void computaConsumoSemestral(ConsumoInterface consumo) {
		//CONSULTAS UTILIZADAS
		this.setConsultasSemestralUtilizadas((consumo.getNumeroConsultasConfirmadas() + consumo.getNumeroConsultasAgendadas()));
		//LIMITE DE CONSULTAS
		this.setConsultasSemestralPermitidas(consumo.getLimiteConsultas().intValue());
		//CONSULTAS PERMITIDAS
		int consultasPermitidoMenosUtilizado = this.getConsultasSemestralPermitidas() - this.getConsultasSemestralUtilizadas();

		this.setConsultasSemestralDisponiveis(consultasPermitidoMenosUtilizado);

		//CONSULTAS ODONTOLOGICAS UTILIZADAS
		this.setConsultasOdontologicoSemestralUtilizados(consumo.getNumeroConsultasOdontoConfirmadas());
		//LIMITE DE CONS.ODONTOLOGICA
		this.setConsultasOdontologicoSemestralPermitidos(consumo.getLimiteConsultasOdonto().intValue());
		//CONS. ODONTOLOGICAS DISPONIVEIS
		int consultasOdontoPermitidoMenosUtilizado = this.getConsultasOdontologicoSemestralPermitidos() - this.getConsultasOdontologicoSemestralUtilizados();

		this.setConsultasOdontologicoSemestralDisponiveis(consultasOdontoPermitidoMenosUtilizado);

		//EXAMES UTILIZADOS [Confirmado + Agendados]
		this.setExamesSemestralUtilizados(consumo.getNumeroProcedimentosExamesConfirmados() + consumo.getNumeroProcedimentosExamesAgendados());
		//LIMITES DE EXAMES
		this.setExamesSemestralPermitidos(consumo.getLimiteExames().intValue());
		//EXAMES DISPONIVEIS
		int examesDisponiveis =  ((this.getExamesSemestralPermitidos() - this.getExamesSemestralUtilizados()) >= 0)? 
																		(this.getExamesSemestralPermitidos() - this.getExamesSemestralUtilizados()): 0;

		this.setExamesSemestralDisponiveis(examesDisponiveis);

		//TRATAMENTO ODONTOLOGICO
		this.setTratamentoOdontologicoSemestralUtilizados(consumo.getNumeroTratamentosOdontoAgendados() + consumo.getNumeroTratamentosOdontoConfirmados());
		//LIMITES DE TRAT. ODONTOLOGICO
		this.setTratamentoOdontologicoSemestralPermitidos(consumo.getLimiteTratamentosOdonto().intValue());
		//TRAT. ODONTOLOGICO DISPONIVEIS
		int tratamentoODPermitidoMenosUtilizado = this.getTratamentoOdontologicoSemestralPermitidos() - this.getTratamentoOdontologicoSemestralUtilizados();
//		if (tratamentoODPermitidoMenosUtilizado > this.getTratamentoOdontologicoAnualDisponiveis()) {
//			tratamentoODPermitidoMenosUtilizado = this.getTratamentoOdontologicoAnualDisponiveis();
//		}
		this.setTratamentoOdontologicoSemestralDisponiveis(tratamentoODPermitidoMenosUtilizado);
	}

	private void computaConsumoTrimestral(ConsumoInterface consumo) {
		//CONSULTAS UTILIZADAS
		this.setConsultasTrimestralUtilizadas((consumo.getNumeroConsultasConfirmadas() + consumo.getNumeroConsultasAgendadas()));
		//LIMITE DE CONSULTAS
		this.setConsultasTrimestralPermitidas(consumo.getLimiteConsultas().intValue());
		//CONSULTAS DISPONIVEIS
		int consultasPermitidoMenosUtilizado = this.getConsultasTrimestralPermitidas() - this.getConsultasTrimestralUtilizadas();
//		if (consultasPermitidoMenosUtilizado > this.getConsultasSemestralDisponiveis()) {
//			consultasPermitidoMenosUtilizado = this.getConsultasSemestralDisponiveis();
//		}
		this.setConsultasTrimestralDisponiveis(consultasPermitidoMenosUtilizado);

		//CONSULTAS ODONTOLOGICAS UTILIZADAS
		this.setConsultasOdontologicoTrimestralUtilizados(consumo.getNumeroConsultasOdontoConfirmadas());
		//LIMITE DE CONS.ODONTOLOGICA
		this.setConsultasOdontologicoTrimestralPermitidos(consumo.getLimiteConsultasOdonto().intValue());
		//CONS. ODONTOLOGICAS DISPONIVEIS
		int consultasODPermitidoMenosUtilizado = this.getConsultasOdontologicoTrimestralPermitidos() - this.getConsultasOdontologicoTrimestralUtilizados();
//		if (consultasODPermitidoMenosUtilizado > this.getConsultasOdontologicoSemestralDisponiveis()) {
//			consultasODPermitidoMenosUtilizado = this.getConsultasOdontologicoSemestralDisponiveis();
//		}
		this.setConsultasOdontologicoTrimestralDisponiveis(consultasODPermitidoMenosUtilizado);

		//EXAMES UTILIZADOS [Confirmado + Agendados]
		this.setExamesTrimestralUtilizados(consumo.getNumeroProcedimentosExamesConfirmados() + consumo.getNumeroProcedimentosExamesAgendados());
		//LIMITES DE EXAMES
		this.setExamesTrimestralPermitidos(consumo.getLimiteExames().intValue());
		//EXAMES DISPONIVEIS
		int examesDisponiveis =  ((this.getExamesTrimestralPermitidos() - this.getExamesTrimestralUtilizados()) >= 0)? 
																		(this.getExamesTrimestralPermitidos() - this.getExamesTrimestralUtilizados()): 0;

		this.setExamesTrimestralDisponiveis(examesDisponiveis);

		//TRATAMENTO ODONTOLOGICO
		this.setTratamentoOdontologicoTrimestralUtilizados(consumo.getNumeroTratamentosOdontoAgendados() + consumo.getNumeroTratamentosOdontoConfirmados());
		//LIMITES DE TRAT. ODONTOLOGICO
		this.setTratamentoOdontologicoTrimestralPermitidos(consumo.getLimiteTratamentosOdonto().intValue());
		//TRAT. ODONTOLOGICO DISPONIVEIS
		int tratamentoODPermitidoMenosUtilizado = this.getTratamentoOdontologicoTrimestralPermitidos() - this.getTratamentoOdontologicoTrimestralUtilizados();
//		if (tratamentoODPermitidoMenosUtilizado > this.getTratamentoOdontologicoSemestralDisponiveis()) {
//			tratamentoODPermitidoMenosUtilizado = this.getTratamentoOdontologicoSemestralDisponiveis();
//		}
		this.setTratamentoOdontologicoTrimestralDisponiveis(tratamentoODPermitidoMenosUtilizado);
	}

	private void computaConsumoMensal(ConsumoInterface consumo) {
		
		//CONSULTAS UTILIZADOS
		this.setConsultasMensalUtilizadas(consumo.getNumeroConsultasConfirmadas() + consumo.getNumeroConsultasAgendadas());
		//LIMITE CONSULTAS
		this.setConsultasMensalPermitidas(consumo.getLimiteConsultas().intValue());
		//CONSULTAS DISPONIVEIS
		int consultasPermitidoMenosUtilizado = this.getConsultasMensalPermitidas() - this.getConsultasMensalUtilizadas();
//		if (consultasPermitidoMenosUtilizado > this.getConsultasTrimestralDisponiveis()) {
//			consultasPermitidoMenosUtilizado = this.getConsultasTrimestralDisponiveis();
//		}
		this.setConsultasMensalDisponiveis(consultasPermitidoMenosUtilizado);

		//CONSULTAS ODONTOLOGICAS UTILIZADAS
		this.setConsultasOdontologicoMensalUtilizados(consumo.getNumeroConsultasOdontoConfirmadas());
		//LIMITE DE CONS.ODONTOLOGICA
		this.setConsultasOdontologicoMensalPermitidos(consumo.getLimiteConsultasOdonto().intValue());
		//CONS. ODONTOLOGICAS DISPONIVEIS
		int consultasODPermitidoMenosUtilizado = this.getConsultasOdontologicoMensalPermitidos() - this.getConsultasOdontologicoMensalUtilizados();
//		if (consultasODPermitidoMenosUtilizado > this.getConsultasOdontologicoTrimestralDisponiveis()) {
//			consultasODPermitidoMenosUtilizado = this.getConsultasOdontologicoTrimestralDisponiveis();
//		}
		this.setConsultasOdontologicoMensalDisponiveis(consultasODPermitidoMenosUtilizado);

		//EXAMES UTILIZADOS [Confirmado + Agendados]
		this.setExamesMensalUtilizados(consumo.getNumeroProcedimentosExamesConfirmados() + consumo.getNumeroProcedimentosExamesAgendados());
		//LIMITES DE EXAMES
		this.setExamesMensalPermitidos(consumo.getLimiteExames().intValue());
		//EXAMES DISPONIVEIS
		int examesDisponiveis =  ((this.getExamesMensalPermitidos() - this.getExamesMensalUtilizados()) >= 0)? 
																		(this.getExamesMensalPermitidos() - this.getExamesMensalUtilizados()): 0;

		this.setExamesMensalDisponiveis(examesDisponiveis);

		//TRATAMENTO ODONTOLOGICO
		this.setTratamentoOdontologicoMensalUtilizados(consumo.getNumeroTratamentosOdontoAgendados() + consumo.getNumeroTratamentosOdontoConfirmados());
		//LIMITES DE TRAT. ODONTOLOGICO
		this.setTratamentoOdontologicoMensalPermitidos(consumo.getLimiteTratamentosOdonto().intValue());
		//TRAT. ODONTOLOGICO DISPONIVEIS
		int tratamentoODPermitidoMenosUtilizado = this.getTratamentoOdontologicoMensalPermitidos() - this.getTratamentoOdontologicoMensalUtilizados();
//		if (tratamentoODPermitidoMenosUtilizado > this.getTratamentoOdontologicoTrimestralDisponiveis()) {
//			tratamentoODPermitidoMenosUtilizado = this.getTratamentoOdontologicoTrimestralDisponiveis();
//		}
		this.setTratamentoOdontologicoMensalDisponiveis(tratamentoODPermitidoMenosUtilizado);
	}
		
	/**
	 * Metodo que retorna o numero de consultas mensais utilizadas
	 * */
	public int getConsultasMensalUtilizadas() {
		return consultasMensalUtilizadas;
	}
	/**
	 * Metodo que configura o numero de de consultas mensais utilizadas
	 * @param consultasMensalUtilizadas
	 * */
	public void setConsultasMensalUtilizadas(int consultasMensalUtilizadas) {
		this.consultasMensalUtilizadas = consultasMensalUtilizadas;
	}
	/**
	 * Metodo que retorna o numero de consultas trimestrais utilizadas
	 * */
	public int getConsultasTrimestralUtilizadas() {
		return consultasTrimestralUtilizadas;
	}
	/**
	 * Metodo que configura o numero de de consultas trimestrais utilizadas
	 * @param consultasTrimestralUtilizadas
	 * */
	public void setConsultasTrimestralUtilizadas(int consultasTrimestralUtilizadas) {
		this.consultasTrimestralUtilizadas = consultasTrimestralUtilizadas;
	}
	/**
	 * Metodo que retorna o numero de consultas semestrais utilizadas
	 * */
	public int getConsultasSemestralUtilizadas() {
		return consultasSemestralUtilizadas;
	}
	/**
	 * Metodo que configura o numero de de consultas semestrais utilizadas
	 * @param consultasSemestralUtilizadas
	 * */
	public void setConsultasSemestralUtilizadas(int consultasSemestralUtilizadas) {
		this.consultasSemestralUtilizadas = consultasSemestralUtilizadas;
	}
	/**
	 * Metodo que retorna o numero de consultas anuais utilizadas
	 * */
	public int getConsultasAnualUtilizadas() {
		return consultasAnualUtilizadas;
	}
	/**
	 * Metodo que configura o numero de de consultas anual utilizadas
	 * @param consultasAnualUtilizadas
	 * */
	public void setConsultasAnualUtilizadas(int consultasAnualUtilizadas) {
		this.consultasAnualUtilizadas = consultasAnualUtilizadas;
	}
	/**
	 * Metodo que retorna o numero de consultas mensais permitidas
	 * */
	public int getConsultasMensalPermitidas() {
		return consultasMensalPermitidas;
	}
	/**
	 * Metodo que configura o numero de consultas mensais permitidas
	 * @param consultasMensalPermitidas
	 * */
	public void setConsultasMensalPermitidas(int consultasMensalPermitidas) {
		this.consultasMensalPermitidas = consultasMensalPermitidas;
	}
	/**
	 * Metodo que retorna o numero de consultas trimestrais permitidas
	 * */
	public int getConsultasTrimestralPermitidas() {
		return consultasTrimestralPermitidas;
	}
	/**
	 * Metodo que configura o numero de consultas trimestrais permitidas
	 * @param consultasTrimestralPermitidas
	 * */
	public void setConsultasTrimestralPermitidas(int consultasTrimestralPermitidas) {
		this.consultasTrimestralPermitidas = consultasTrimestralPermitidas;
	}
	/**
	 * Metodo que retorna o numero de consultas semestrais permitidas
	 * */
	public int getConsultasSemestralPermitidas() {
		return consultasSemestralPermitidas;
	}
	/**
	 * Metodo que configura o numero de consultas semestrais permitidas
	 * @param consultasSemestralPermitidas
	 * */
	public void setConsultasSemestralPermitidas(int consultasSemestralPermitidas) {
		this.consultasSemestralPermitidas = consultasSemestralPermitidas;
	}
	/**
	 * Metodo que retorna o numero de consultas anuais permitidas
	 * */
	public int getConsultasAnualPermitidas() {
		return consultasAnualPermitidas;
	}
	/**
	 * Metodo que confgura o numero de consultas anuais permitidas
	 * @param consultasAnuaisPermitidas
	 * */
	public void setConsultasAnualPermitidas(int consultasAnualPermitidas) {
		this.consultasAnualPermitidas = consultasAnualPermitidas;
	}
	/**
	 * Metodo que retorna o numero de consultas mensais disponiveis
	 * */
	public int getConsultasMensalDisponiveis() {
		return consultasMensalDisponiveis;
	}
	/**
	 * Metodo que configura o numero de consultas mensais disponiveis
	 * @param consultasMensalDisponiveis
	 * */
	public void setConsultasMensalDisponiveis(int consultasMensalDisponiveis) {
		this.consultasMensalDisponiveis = consultasMensalDisponiveis;
	}
	/**
	 * Metodo que retorna o numero de consultas trimestrais disponiveis
	 * */
	public int getConsultasTrimestralDisponiveis() {
		return consultasTrimestralDisponiveis;
	}
	/**
	 * Metodo que configura o numero de consultas trimestrais disponiveis
	 * @param consultasTrimestralDisponiveis
	 * */
	public void setConsultasTrimestralDisponiveis(int consultasTrimestralDisponiveis) {
		this.consultasTrimestralDisponiveis = consultasTrimestralDisponiveis;
	}
	/**
	 * Metodo que retorna o numero de consultas semestrais disponiveis
	 * */
	public int getConsultasSemestralDisponiveis() {
		return consultasSemestralDisponiveis;
	}
	/**
	 * Metodo que configura o numero de consultas mensais disponiveis
	 * @param consultasSemestralDisponiveis
	 * */
	public void setConsultasSemestralDisponiveis(int consultasSemestralDisponiveis) {
		this.consultasSemestralDisponiveis = consultasSemestralDisponiveis;
	}
	/**
	 * Metodo que retorna o numero de consultas anuais disponiveis
	 * */
	public int getConsultasAnualDisponiveis() {
		return consultasAnualDisponiveis;
	}
	/**
	 * Metodo que configura o numero de consultas anuais disponiveis
	 * @param consultasAnualDisponiveis
	 * */
	public void setConsultasAnualDisponiveis(int consultasAnualDisponiveis) {
		this.consultasAnualDisponiveis = consultasAnualDisponiveis;
	}
	/**
	 * Metodo que retorna o numero de exames mensais utilizados
	 * */
	public int getExamesMensalUtilizados() {
		return examesMensalUtilizados;
	}
	/**
	 * Metodo que configura o numero de exames mensais utilizados
	 * @param examesMensalUtilizados
	 * */
	public void setExamesMensalUtilizados(int examesMensalUtilizados) {
		this.examesMensalUtilizados = examesMensalUtilizados;
	}
	/**
	 * Metodo que retorna o numero de exames trimestrais utilizados
	 * */
	public int getExamesTrimestralUtilizados() {
		return examesTrimestralUtilizados;
	}
	/**
	 * Metodo que configura o numero de exames trimestrais utilizados
	 * @param examesTrimestralUtilizados
	 * */
	public void setExamesTrimestralUtilizados(int examesTrimestralUtilizados) {
		this.examesTrimestralUtilizados = examesTrimestralUtilizados;
	}
	/**
	 * Metodo que retorna o numero de exames semestais utilizados
	 * */
	public int getExamesSemestralUtilizados() {
		return examesSemestralUtilizados;
	}
	/**
	 * Metodo que configura o numero de exames semestais utilizados
	 * @param
	 * */
	public void setExamesSemestralUtilizados(int examesSemestralUtilizados) {
		this.examesSemestralUtilizados = examesSemestralUtilizados;
	}
	/**
	 * Metodo que retorna o numero de exames anuais utilizados
	 * */
	public int getExamesAnualUtilizados() {
		return examesAnualUtilizados;
	}
	/**
	 * Metodo que configura o numero de exames anuais utilizados
	 * @param examesAnualUtilizados
	 * */
	public void setExamesAnualUtilizados(int examesAnualUtilizados) {
		this.examesAnualUtilizados = examesAnualUtilizados;
	}
	/**
	 * Metodo que retorna o numero de consultas odontologicas mensais utilizadas 
	 * */
	public int getConsultasOdontologicoMensalUtilizados() {
		return consultasOdontologicoMensalUtilizados;
	}
	/**
	 * Metodo que configura o numero de consultas odontologicas mensais utilizadas 
	 * */
	public void setConsultasOdontologicoMensalUtilizados(
			int consultasOdontologicoMensalUtilizados) {
		this.consultasOdontologicoMensalUtilizados = consultasOdontologicoMensalUtilizados;
	}
	/**
	 * Metodo que retorna o numero de  consultas odontologicas trimestrais utilizadas 
	 * */
	public int getConsultasOdontologicoTrimestralUtilizados() {
		return consultasOdontologicoTrimestralUtilizados;
	}
	/**
	 * Metodo que configura o numero de  consultas odontologicas trimestrais utilizadas 
	 * @param consultasOdontologicoTrimestralUtilizados
	 * */
	public void setConsultasOdontologicoTrimestralUtilizados(
			int consultasOdontologicoTrimestralUtilizados) {
		this.consultasOdontologicoTrimestralUtilizados = consultasOdontologicoTrimestralUtilizados;
	}
	/**
	 * Metodo que retorna o numero de consultas odontologicas semestrais utilizadas
	 * */
	public int getConsultasOdontologicoSemestralUtilizados() {
		return consultasOdontologicoSemestralUtilizados;
	}
	/**
	 * Metodo que configura o numero de consultas odontologicas semestrais utilizadas
	 * @param consultasOdontologicoSemestralUtilizados
	 * */
	public void setConsultasOdontologicoSemestralUtilizados(
			int consultasOdontologicoSemestralUtilizados) {
		this.consultasOdontologicoSemestralUtilizados = consultasOdontologicoSemestralUtilizados;
	}
	/**
	 * Metodo que retorna o numero de consultas odontologicas anuais utilizadas
	 * */
	public int getConsultasOdontologicoAnualUtilizados() {
		return consultasOdontologicoAnualUtilizados;
	}
	/**
	 * Metodo que configura o numero de consultas odontologicas anuais utilizadas
	 * @param consultasOdontologicoAnualUtilizados
	 * */
	public void setConsultasOdontologicoAnualUtilizados(
			int consultasOdontologicoAnualUtilizados) {
		this.consultasOdontologicoAnualUtilizados = consultasOdontologicoAnualUtilizados;
	}
	/**
	 * Metodo que retorna o numero de trat. odontologicos mensais utilizados
	 * */
	public int getTratamentoOdontologicoMensalUtilizados() {
		return tratamentoOdontologicoMensalUtilizados;
	}
	/**
	 * Metodo que configura o numero de trat. odontologicos mensais utilizados
	 * @param tratamentoOdontologicoMensalUtilizados
	 * */
	public void setTratamentoOdontologicoMensalUtilizados(
			int tratamentoOdontologicoMensalUtilizados) {
		this.tratamentoOdontologicoMensalUtilizados = tratamentoOdontologicoMensalUtilizados;
	}
	/**
	 * Metodo que retorna o numero de  trat. odontologicos trimestral utilizados
	 * */
	public int getTratamentoOdontologicoTrimestralUtilizados() {
		return tratamentoOdontologicoTrimestralUtilizados;
	}
	/**
	 * Metodo que configura o numero de  trat. odontologicos trimestral utilizados
	 * @param tratamentoOdontologicoTrimestralUtilizados
	 * */
	public void setTratamentoOdontologicoTrimestralUtilizados(
			int tratamentoOdontologicoTrimestralUtilizados) {
		this.tratamentoOdontologicoTrimestralUtilizados = tratamentoOdontologicoTrimestralUtilizados;
	}
	/**
	 * Metodo que retorna o numero de trat. odontologicos semestrais utilizados
	 * */
	public int getTratamentoOdontologicoSemestralUtilizados() {
		return tratamentoOdontologicoSemestralUtilizados;
	}
	/**
	 * Metodo que configura o numero de trat. odontologicos semestrais utilizados
	 * @param tratamentoOdontologicoSemestralUtilizados
	 * */
	public void setTratamentoOdontologicoSemestralUtilizados(
			int tratamentoOdontologicoSemestralUtilizados) {
		this.tratamentoOdontologicoSemestralUtilizados = tratamentoOdontologicoSemestralUtilizados;
	}
	/**
	 * Metodo que retorna o numero de trat. odontologicos anuais utilizados
	 * */
	public int getTratamentoOdontologicoAnualUtilizados() {
		return tratamentoOdontologicoAnualUtilizados;
	}
	/**
	 * Metodo que configura o numero de trat. odontologicos anuais utilizados
	 * @param tratamentoOdontologicoAnualUtilizados
	 * */
	public void setTratamentoOdontologicoAnualUtilizados(
			int tratamentoOdontologicoAnualUtilizados) {
		this.tratamentoOdontologicoAnualUtilizados = tratamentoOdontologicoAnualUtilizados;
	}
	/**
	 * Metodo que retorna o numero de exames mensais permitidos
	 * */
	public int getExamesMensalPermitidos() {
		return examesMensalPermitidos;
	}
	/**
	 * Metodo que configura o numero de exames mensais permitidos
	 * @param examesMensalPermitidos
	 * */
	public void setExamesMensalPermitidos(int examesMensalPermitidos) {
		this.examesMensalPermitidos = examesMensalPermitidos;
	}
	/**
	 * Metodo que retorna o numero de exames trimestrais permitidos
	 * */
	public int getExamesTrimestralPermitidos() {
		return examesTrimestralPermitidos;
	}
	/**
	 * Metodo que configura o numero de exames trimestrais permitidos
	 * @param examesTrimestralPermitidos
	 * */
	public void setExamesTrimestralPermitidos(int examesTrimestralPermitidos) {
		this.examesTrimestralPermitidos = examesTrimestralPermitidos;
	}
	/**
	 * Metodo que retorna o numero de exames semestrais permitidos
	 * */
	public int getExamesSemestralPermitidos() {
		return examesSemestralPermitidos;
	}
	/**
	 * Metodo que configura o numero de exames semestrais permitidos
	 * @param examesSemestralPermitidos
	 * */
	public void setExamesSemestralPermitidos(int examesSemestralPermitidos) {
		this.examesSemestralPermitidos = examesSemestralPermitidos;
	}
	/**
	 * Metodo que retorna o numero de exames anuais permitidos
	 * */
	public int getExamesAnualPermitidos() {
		return examesAnualPermitidos;
	}
	/**
	 * Metodo que configura o numero de exames anuais permitidos
	 * @param examesAnualPermitidos
	 * */
	public void setExamesAnualPermitidos(int examesAnualPermitidos) {
		this.examesAnualPermitidos = examesAnualPermitidos;
	}
	/**
	 * Metodo que retorna o numero de exames mensais disponiveis
	 * */
	public int getExamesMensalDisponiveis() {
		return examesMensalDisponiveis;
	}
	/**
	 * Metodo que configura o numero de exames mensais disponiveis
	 * @param examesMensalDisponiveis
	 * */
	public void setExamesMensalDisponiveis(int examesMensalDisponiveis) {
		this.examesMensalDisponiveis = examesMensalDisponiveis;
	}
	/**
	 * Metodo que retorna o numero de exames trimestrais disponiveis
	 * */
	public int getExamesTrimestralDisponiveis() {
		return examesTrimestralDisponiveis;
	}
	/**
	 * Metodo que configura o numero de exames trimestrais disponiveis
	 * @param examesTrimestralDisponiveis
	 * */
	public void setExamesTrimestralDisponiveis(int examesTrimestralDisponiveis) {
		this.examesTrimestralDisponiveis = examesTrimestralDisponiveis;
	}
	/**
	 * Metodo que retorna o numero de exames semestrais disponiveis
	 * */
	public int getExamesSemestralDisponiveis() {
		return examesSemestralDisponiveis;
	}
	/**
	 * Metodo que configura o numero de exames semestrais disponiveis
	 * @param examesSemestralDisponiveis
	 * */
	public void setExamesSemestralDisponiveis(int examesSemestralDisponiveis) {
		this.examesSemestralDisponiveis = examesSemestralDisponiveis;
	}
	/**
	 * Metodo que retorna o numero de exames anuais disponiveis
	 * */
	public int getExamesAnualDisponiveis() {
		return examesAnualDisponiveis;
	}
	/**
	 * Metodo que configura o numero de exames anuais disponiveis
	 * @param examesAnualDisponiveis
	 * */
	public void setExamesAnualDisponiveis(int examesAnualDisponiveis) {
		this.examesAnualDisponiveis = examesAnualDisponiveis;
	}
	/**
	 * Metodo que retorna o numero de consultas odontologicas mensais permitidos
	 * */
	public int getConsultasOdontologicoMensalDisponiveis() {
		return consultasOdontologicoMensalDisponiveis;
	}
	/**
	 * Metodo que configura o numero de consultas odontologicas semestrais disponiveis
	 * @param consultasOdontologicoMensalDisponiveis
	 * */
	public void setConsultasOdontologicoMensalDisponiveis(
			int consultasOdontologicoMensalDisponiveis) {
		this.consultasOdontologicoMensalDisponiveis = consultasOdontologicoMensalDisponiveis;
	}
	/**
	 * Metodo que retorna o numero de consultas odontologicas trimestrais disponiveis
	 * */
	public int getConsultasOdontologicoTrimestralDisponiveis() {
		return consultasOdontologicoTrimestralDisponiveis;
	}
	/**
	 * Metodo que configura o numero de consultas odontologicas trimestrais disponiveis
	 * @param consultasOdontologicoTrimestralDisponiveis
	 * */
	public void setConsultasOdontologicoTrimestralDisponiveis(
			int consultasOdontologicoTrimestralDisponiveis) {
		this.consultasOdontologicoTrimestralDisponiveis = consultasOdontologicoTrimestralDisponiveis;
	}
	/**
	 * Metodo que retorna o numero de consultas odontologicas semestrais disponiveis
	 * */
	public int getConsultasOdontologicoSemestralDisponiveis() {
		return consultasOdontologicoSemestralDisponiveis;
	}
	/**
	 * Metodo que configura o numero de consultas odontologicas semestrais permitidos
	 * @param consultasOdontologicoSemestralPermitidos
	 * */
	public void setConsultasOdontologicoSemestralDisponiveis(
			int consultasOdontologicoSemestralDisponiveis) {
		this.consultasOdontologicoSemestralDisponiveis = consultasOdontologicoSemestralDisponiveis;
	}
	/**
	 * Metodo que configura o numero de consultas odontologicas semestrais permitidos
	 * @param consultasOdontologicoSemestralPermitidos
	 * */
	public int getConsultasOdontologicoAnualDisponiveis() {
		return consultasOdontologicoAnualDisponiveis;
	}
	/**
	 * Metodo que configura o numero de consultas odontologicas anuais disponiveis
	 * @param consultasOdontologicoAnualDisponiveis
	 * */
	public void setConsultasOdontologicoAnualDisponiveis(
			int consultasOdontologicoAnualDisponiveis) {
		this.consultasOdontologicoAnualDisponiveis = consultasOdontologicoAnualDisponiveis;
	}
	/**
	 * Metodo que retorna o numero de consultas odontologicas mensais permitidos
	 * */
	public int getConsultasOdontologicoMensalPermitidos() {
		return consultasOdontologicoMensalPermitidos;
	}
	/**
	 * Metodo que configura o numero de consultas odontologicas mensais permitidos
	 * @param consultasOdontologicoMensalPermitidos
	 * */
	public void setConsultasOdontologicoMensalPermitidos(
			int consultasOdontologicoMensalPermitidos) {
		this.consultasOdontologicoMensalPermitidos = consultasOdontologicoMensalPermitidos;
	}
	/**
	 * Metodo que retorna o numero de consultas odontologicas trimestrais permitidos
	 * */
	public int getConsultasOdontologicoTrimestralPermitidos() {
		return consultasOdontologicoTrimestralPermitidos;
	}
	/**
	 * Metodo que configura o numero de consultas odontologicas trimestral permitidos
	 * @param consultasOdontologicoTrimestralPermitidos
	 * */
	public void setConsultasOdontologicoTrimestralPermitidos(
			int consultasOdontologicoTrimestralPermitidos) {
		this.consultasOdontologicoTrimestralPermitidos = consultasOdontologicoTrimestralPermitidos;
	}
	/**
	 * Metodo que retorna o numero de consultas odontologicas semestrais permitidos
	 * */
	public int getConsultasOdontologicoSemestralPermitidos() {
		return consultasOdontologicoSemestralPermitidos;
	}
	/**
	 * Metodo que configura o numero de consultas odontologicas semestrais permitidos
	 * @param consultasOdontologicoSemestralPermitidos
	 * */
	public void setConsultasOdontologicoSemestralPermitidos(
			int consultasOdontologicoSemestralPermitidos) {
		this.consultasOdontologicoSemestralPermitidos = consultasOdontologicoSemestralPermitidos;
	}
	/**
	 * Metodo que retorna o numero de consultas odontologicas anuais permitidos
	 * */
	public int getConsultasOdontologicoAnualPermitidos() {
		return consultasOdontologicoAnualPermitidos;
	}
	/**
	 * Metodo que configura o numero de consultas odontologicas anuais permitidos
	 * @param tratamentoOdontologicoAnualPermitidos
	 * */
	public void setConsultasOdontologicoAnualPermitidos(
			int consultasOdontologicoAnualPermitidos) {
		this.consultasOdontologicoAnualPermitidos = consultasOdontologicoAnualPermitidos;
	}
	/**
	 * Metodo que retorna o numero de tratamento odontologicos anuais disponiveis
	 * @param tratamentoOdontologicoAnualPermitidos
	 * */
	public int getTratamentoOdontologicoMensalDisponiveis() {
		return tratamentoOdontologicoMensalDisponiveis;
	}
	/**
	 * Metodo que configura o numero de tratamento odontologicos mensais disponiveis
	 * @param tratamentoOdontologicoMensalDisponiveis
	 * */
	public void setTratamentoOdontologicoMensalDisponiveis(
			int tratamentoOdontologicoMensalDisponiveis) {
		this.tratamentoOdontologicoMensalDisponiveis = tratamentoOdontologicoMensalDisponiveis;
	}
	/**
	 * Metodo que retorna o numero de tratamento odontologicos trimestrais disponiveis
	 * */
	public int getTratamentoOdontologicoTrimestralDisponiveis() {
		return tratamentoOdontologicoTrimestralDisponiveis;
	}
	/**
	 * Metodo que configura o numero de tratamento odontologicos trimestrais disponiveis
	 * @param tratamentoOdontologicoTrimestralDisponiveis
	 * */
	public void setTratamentoOdontologicoTrimestralDisponiveis(
			int tratamentoOdontologicoTrimestralDisponiveis) {
		this.tratamentoOdontologicoTrimestralDisponiveis = tratamentoOdontologicoTrimestralDisponiveis;
	}
	/**
	 * Metodo que retorna o numero de tratamento odontologicos semestrais disponiveis
	 * */
	public int getTratamentoOdontologicoSemestralDisponiveis() {
		return tratamentoOdontologicoSemestralDisponiveis;
	}
	/**
	 * Metodo que configura o numero de tratamento odontologicos semestrais disponiveis
	 * @param tratamentoOdontologicoSemestralDisponiveis
	 * */
	public void setTratamentoOdontologicoSemestralDisponiveis(
			int tratamentoOdontologicoSemestralDisponiveis) {
		this.tratamentoOdontologicoSemestralDisponiveis = tratamentoOdontologicoSemestralDisponiveis;
	}
	/**
	 * Metodo que retorna o numero de tratamento odontologicos anuais disponiveis
	 * */
	public int getTratamentoOdontologicoAnualDisponiveis() {
		return tratamentoOdontologicoAnualDisponiveis;
	}
	/**
	 * Metodo que configura o numero de tratamento odontologicos anuais disponiveis
	 * @param tratamentoOdontologicoAnualDisponiveis
	 * */
	public void setTratamentoOdontologicoAnualDisponiveis(
			int tratamentoOdontologicoAnualDisponiveis) {
		this.tratamentoOdontologicoAnualDisponiveis = tratamentoOdontologicoAnualDisponiveis;
	}
	/**
	 * Metodo que retorna o numero de tratamento odontologicos mensais permitidos
	* */
	public int getTratamentoOdontologicoMensalPermitidos() {
		return tratamentoOdontologicoMensalPermitidos;
	}
	/**
	 * Metodo que configura o numero de tratamento odontologicos mensais permitidos
	 * @param tratamentoOdontologicoMensalPermitidos
	 * */
	public void setTratamentoOdontologicoMensalPermitidos(
			int tratamentoOdontologicoMensalPermitidos) {
		this.tratamentoOdontologicoMensalPermitidos = tratamentoOdontologicoMensalPermitidos;
	}
	/**
	 * Metodo que retorna o numero de tratamento odontologicos trimestral permitidos
	* */
	public int getTratamentoOdontologicoTrimestralPermitidos() {
		return tratamentoOdontologicoTrimestralPermitidos;
	}
	/**
	 * Metodo que configura o numero de tratamento odontologicos trimestrais permitidos
	 * @param tratamentoOdontologicoTrimestralPermitidos
	 * */
	public void setTratamentoOdontologicoTrimestralPermitidos(
			int tratamentoOdontologicoTrimestralPermitidos) {
		this.tratamentoOdontologicoTrimestralPermitidos = tratamentoOdontologicoTrimestralPermitidos;
	}
	/**
	 * Metodo que retorna o numero de tratamento odontologicos semestrais permitidos
	* */
	public int getTratamentoOdontologicoSemestralPermitidos() {
		return tratamentoOdontologicoSemestralPermitidos;
	}
	/**
	 * Metodo que configura o numero de tratamento odontologicos semestrais permitidos
	 * @param tratamentoOdontologicoSemestralPermitidos
	 * */
	public void setTratamentoOdontologicoSemestralPermitidos(
			int tratamentoOdontologicoSemestralPermitidos) {
		this.tratamentoOdontologicoSemestralPermitidos = tratamentoOdontologicoSemestralPermitidos;
	}
	/**
	 * Metodo que retorna o numero de tratamento odontologicos anuais permitidos
	 * */
	public int getTratamentoOdontologicoAnualPermitidos() {
		return tratamentoOdontologicoAnualPermitidos;
	}
	/**
	 * Metodo que configura o numero de tratamento odontologicos anuais permitidos
	 * @param tratamentoOdontologicoAnualPermitidos
	 * */
	public void setTratamentoOdontologicoAnualPermitidos(
			int tratamentoOdontologicoAnualPermitidos) {
		this.tratamentoOdontologicoAnualPermitidos = tratamentoOdontologicoAnualPermitidos;
	}

	public ConsumoInterface getConsumoMensal() {
		return consumoMensal;
	}

	public void setConsumoMensal(ConsumoInterface consumoMensal) {
		this.consumoMensal = consumoMensal;
	}

	public ConsumoInterface getConsumoTrimestral() {
		return consumoTrimestral;
	}

	public void setConsumoTrimestral(ConsumoInterface consumoTrimestral) {
		this.consumoTrimestral = consumoTrimestral;
	}

	public ConsumoInterface getConsumoSemestral() {
		return consumoSemestral;
	}

	public void setConsumoSemestral(ConsumoInterface consumoSemestral) {
		this.consumoSemestral = consumoSemestral;
	}

	public ConsumoInterface getConsumoAnual() {
		return consumoAnual;
	}

	public void setConsumoAnual(ConsumoInterface consumoAnual) {
		this.consumoAnual = consumoAnual;
	}

	public ArquivoBase getArquivoPdf() {
		return arquivoPdf;
	}
	
	public void setArquivoPdf(ArquivoBase arquivoPdf) {
		this.arquivoPdf = arquivoPdf;
	}
}
