package br.com.infowaypi.ecare.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * Relatório de consultas (eletivas e de urgência) realizadas em um determinado mês, agrupadas por seus respectivos prestadores.
 * @author benedito
 *
 */
public class RelatorioConsultasPorPrestador {
	public static final String TIPO_GUIA_CONSULTA_ELETIVA = "GCS";
	public static final String TIPO_GUIA_CONSULTA_URGENCIA = "CUR";
	
	public static final String PESSOA_FISICA = "Pessoa Física";
	public static final String PESSOA_JURIDICA = "Pessoa Jurídica";

	public static final String POR_PRESTADOR = "Prestador";
	public static final String POR_PRODUCAO = "Producao";
	public static final String POR_FATURAMENTO = "Faturamento";

	/**
	 * Intervalo para geração do relatório de consultas por prestador.
	 * Ex.: 01/2008 (Janeiro) - 01/01/2008 a 31/01/2008.
	 */
	private Date mes;
	
	/**
	 * Tipo de prestador informado pelo usuário.
	 * Ex.: Pessoa Física.
	 */
	private String tipoPrestador;
	
	/**
	 * Quantidade de prestadores com produção em um determinado mês, ou seja, aqueles que possuem pelo menos uma guia.
	 */
	private int qtdePrestadores;
	
	/**
	 * Quantidade de consultas eletivas confirmadas ou faturadas em um determinado mês.
	 */
	private int qtdeConsultasEletivas;
	
	/**
	 * Valor das consultas eletivas confirmadas ou faturadas em um determinado mês.
	 */
	private BigDecimal vlrConsultasEletivas;
	
	/**
	 * Quantidade de consultas de urgência fechadas ou faturadas em um determinado mês.
	 */
	private int qtdeConsultasUrgencia;
	
	/**
	 * Valor das consultas de urgência fechadas ou faturadas em um determinado mês.
	 */
	private BigDecimal vlrConsultasUrgencia;
	
	/**
	 * Quantidade de consultas eletivas e de urgência realizadas em um determinado mês.
	 */
	private int qtdeTotalGeral;
	
	/**
	 * Valor das consultas eletivas e de urgência realizadas em um determinado mês.
	 */
	private BigDecimal vlrTotalGeral;
	
	/**
	 * Representa o detalhamento do relatório de consultas por prestador.
	 */
	private List<ResumoPrestadorGuia> resumoPrestadorGuia;
	
	public RelatorioConsultasPorPrestador() {
		this.vlrConsultasEletivas = BigDecimal.ZERO;
		this.vlrConsultasUrgencia = BigDecimal.ZERO;
		this.vlrTotalGeral = BigDecimal.ZERO;
		this.resumoPrestadorGuia = new ArrayList<ResumoPrestadorGuia>();
	}

	/**
	 * Gera o relatório de consultas por prestador de um determinado mês.
	 * O relatório pode ser para todos os prestadores com produção, ou para um prestador em específico.
	 * @param mes
	 * @param pessoa
	 * @param prestadorPesquisado
	 * @param ordem
	 * @return Resumo e detalhamento do relatório.
	 * @throws ValidateException
	 */
	public RelatorioConsultasPorPrestador gerarRelatorio(Date mes, String pessoa, Prestador prestadorPesquisado, String ordem) throws ValidateException{
		this.validarParametros(mes, pessoa, ordem);
		
		this.mes = mes;

		if (prestadorPesquisado != null){
			if (prestadorPesquisado.isPessoaFisica()){
				this.tipoPrestador = PESSOA_FISICA;
			} else {
				this.tipoPrestador = PESSOA_JURIDICA;
			}
		} else {
			this.tipoPrestador = pessoa;
		}

		boolean pessoaFisica = false;
		if (pessoa.equals(PESSOA_FISICA)){
			pessoaFisica = true;
		}
		
		List<GuiaSimples<ProcedimentoInterface>> consultasEletivas = this.buscarConsultas(TIPO_GUIA_CONSULTA_ELETIVA, prestadorPesquisado, pessoaFisica, mes);
		List<GuiaSimples<ProcedimentoInterface>> consultasUrgencia = this.buscarConsultas(TIPO_GUIA_CONSULTA_URGENCIA, prestadorPesquisado, pessoaFisica, mes);
		
		Map<Prestador, Set<GuiaSimples<ProcedimentoInterface>>> mapaConsultasEletivas = this.agruparConsultas(consultasEletivas);
		Map<Prestador, Set<GuiaSimples<ProcedimentoInterface>>> mapaConsultasUrgencia = this.agruparConsultas(consultasUrgencia);
		
		Set<Prestador> prestadores = new HashSet<Prestador>();
		prestadores.addAll(this.getPrestadores(mapaConsultasEletivas));
		prestadores.addAll(this.getPrestadores(mapaConsultasUrgencia));

		this.qtdePrestadores = prestadores.size();
		
		this.qtdeConsultasEletivas = getGuias(mapaConsultasEletivas).size();
		this.vlrConsultasEletivas = getValorTotal(mapaConsultasEletivas);
		
		this.qtdeConsultasUrgencia = getGuias(mapaConsultasUrgencia).size();
		this.vlrConsultasUrgencia = getValorTotal(mapaConsultasUrgencia);
		
		this.qtdeTotalGeral = this.qtdeConsultasEletivas + this.qtdeConsultasUrgencia;
		this.vlrTotalGeral = this.vlrConsultasEletivas.add(this.vlrConsultasUrgencia);
		
		for (Prestador prestador : prestadores) {
			this.resumoPrestadorGuia.add(new ResumoPrestadorGuia(
					prestador.getPessoaJuridica().getFantasia(),
					this.getGuiasPrestador(prestador, mapaConsultasEletivas).size(),
					this.getValorTotalPrestador(prestador, mapaConsultasEletivas),
					this.getGuiasPrestador(prestador, mapaConsultasUrgencia).size(),
					this.getValorTotalPrestador(prestador, mapaConsultasUrgencia),
					this.qtdeTotalGeral,
					this.vlrTotalGeral)
			);
		}
		
		this.ordenarDetalhamento(this.resumoPrestadorGuia, ordem);
		
		this.resumoPrestadorGuia.add(new ResumoPrestadorGuia(
				"TOTAL",
				this.qtdeConsultasEletivas,
				this.vlrConsultasEletivas,
				this.qtdeConsultasUrgencia,
				this.vlrConsultasUrgencia,
				this.qtdeTotalGeral,
				this.vlrTotalGeral)
		);

		return this;
	}
	
	/**
	 * Busca no banco de dados todas as consultas (eletivas e de urgência) realizadas em um determinado mês.
	 * A busca pode ser feita para todos os prestadores com produção, ou para um prestador em específico.
	 * <h2>Observação:</h2>
	 * <ul>Consultas Eletivas - Confirmadas ou Faturadas.</ul>
	 * <ul>Consultas de Urgência - Fechadas ou Faturadas.</ul>
	 * @param tipoDeGuia
	 * @param prestador
	 * @param pessoaFisica
	 * @param mes
	 * @return Lista de guias.
	 * @throws ValidateException
	 */
	@SuppressWarnings("unchecked")
	private List<GuiaSimples<ProcedimentoInterface>> buscarConsultas(String tipoDeGuia, Prestador prestador, Boolean pessoaFisica, Date mes) throws ValidateException{
		Calendar dataInicial = new GregorianCalendar();
		dataInicial.setTime(mes);
		
		Calendar dataFinal = new GregorianCalendar();
		dataFinal.setTime(mes);
		dataFinal.set(Calendar.DAY_OF_MONTH, dataFinal.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		Criteria criteria = HibernateUtil.currentSession().createCriteria(GuiaSimples.class);
		criteria.add(Expression.between("dataAtendimento", dataInicial.getTime(), dataFinal.getTime()));
		criteria.add(Expression.eq("tipoDeGuia", tipoDeGuia));
		
		if (tipoDeGuia.equals(TIPO_GUIA_CONSULTA_ELETIVA)){
			criteria.add(Expression.or(Expression.eq("situacao.descricao", SituacaoEnum.CONFIRMADO.descricao()), Expression.eq("situacao.descricao", SituacaoEnum.FATURADA.descricao())));
		}
		if (tipoDeGuia.equals(TIPO_GUIA_CONSULTA_URGENCIA)){
			criteria.add(Expression.or(Expression.eq("situacao.descricao", SituacaoEnum.FECHADO.descricao()), Expression.eq("situacao.descricao", SituacaoEnum.FATURADA.descricao())));
		}

		if (prestador != null){
			criteria.add(Expression.eq("prestador", prestador));
			return criteria.list();
		}
		
		criteria.createAlias("prestador", "p");
		criteria.add(Expression.eq("p.pessoaFisica", pessoaFisica));

		return criteria.list();
	}
	
	/**
	 * Agrupa as consultas por meio de seus respectivos prestadores.
	 * @param consultas
	 * @return Mapa de prestadores (chave) com suas respectivas guias (valores).
	 */
	private Map<Prestador, Set<GuiaSimples<ProcedimentoInterface>>> agruparConsultas(List<GuiaSimples<ProcedimentoInterface>> consultas){
		Map<Prestador, Set<GuiaSimples<ProcedimentoInterface>>> prestadorGuia = new HashMap<Prestador, Set<GuiaSimples<ProcedimentoInterface>>>();
		
		for (GuiaSimples<ProcedimentoInterface> consulta : consultas) {
			Prestador prestador = consulta.getPrestador();
			if (prestadorGuia.containsKey(prestador)) {
				Set<GuiaSimples<ProcedimentoInterface>> guiaInterna = prestadorGuia.get(prestador);
				guiaInterna.add(consulta);
				prestadorGuia.put(prestador, guiaInterna);
			} else {
				Set<GuiaSimples<ProcedimentoInterface>> guiaInterna = new HashSet<GuiaSimples<ProcedimentoInterface>>();
				guiaInterna.add(consulta);
				prestadorGuia.put(prestador, guiaInterna);
			}
		}

		return prestadorGuia;
	}
	
	/**
	 * Retorna os prestadores do mapa.
	 * @param prestadorGuia
	 * @return Conjunto de prestadores.
	 */
	private Set<Prestador> getPrestadores(Map<Prestador, Set<GuiaSimples<ProcedimentoInterface>>> prestadorGuia){
		return prestadorGuia.keySet();
	}
	
	/**
	 * Retorna as guias do mapa.
	 * @param prestadorGuia
	 * @return Conjunto de guias.
	 */
	private Set<GuiaSimples<ProcedimentoInterface>> getGuias(Map<Prestador, Set<GuiaSimples<ProcedimentoInterface>>> prestadorGuia){
		Set<GuiaSimples<ProcedimentoInterface>> guias = new HashSet<GuiaSimples<ProcedimentoInterface>>();
		
		for (Prestador prestador : this.getPrestadores(prestadorGuia)) {
			guias.addAll(this.getGuiasPrestador(prestador, prestadorGuia));
		}
		
		return guias;
	}
	
	/**
	 * Retorna o valor das guias do mapa.
	 * @param prestadorGuia
	 * @return Valor das guias.
	 */
	private BigDecimal getValorTotal(Map<Prestador, Set<GuiaSimples<ProcedimentoInterface>>> prestadorGuia){
		BigDecimal valorTotal = new BigDecimal(0);
		
		for (GuiaSimples<ProcedimentoInterface> guia : this.getGuias(prestadorGuia)) {
			valorTotal = valorTotal.add(guia.getValorTotal());
		}
		
		return valorTotal;
	}
	
	/**
	 * Retorna as guias do mapa que pertencem a um determinado prestador.
	 * @param prestador
	 * @param prestadorGuia
	 * @return Conjunto de guias.
	 */
	private Set<GuiaSimples<ProcedimentoInterface>> getGuiasPrestador(Prestador prestador, Map<Prestador, Set<GuiaSimples<ProcedimentoInterface>>> prestadorGuia){
		Set<GuiaSimples<ProcedimentoInterface>> guias = new HashSet<GuiaSimples<ProcedimentoInterface>>();

		if (prestadorGuia.containsKey(prestador)){
			guias = prestadorGuia.get(prestador);
		}
		
		return guias;
	}
	
	/**
	 * Retorna o valor das guias do mapa que pertencem a um determinado prestador.
	 * @param prestador
	 * @param prestadorGuia
	 * @return Valor das guias.
	 */
	private BigDecimal getValorTotalPrestador(Prestador prestador, Map<Prestador, Set<GuiaSimples<ProcedimentoInterface>>> prestadorGuia){
		BigDecimal valorTotal = new BigDecimal(0);
		
		for (GuiaSimples<ProcedimentoInterface> guia : this.getGuiasPrestador(prestador, prestadorGuia)) {
			valorTotal = valorTotal.add(guia.getValorTotal());
		}
		
		return valorTotal;
	}

	/**
	 * Ordena o detalhamento do relatório pelo prestador ou pela produção ou pelo faturamento.
	 * @param resumoPrestadorGuia
	 * @param ordem
	 */
	private void ordenarDetalhamento(List<ResumoPrestadorGuia> resumoPrestadorGuia, String ordem){
		if (ordem.equals(POR_PRESTADOR)){
			Collections.sort(resumoPrestadorGuia, new Comparator<ResumoPrestadorGuia>(){
				public int compare(ResumoPrestadorGuia a, ResumoPrestadorGuia b) {
					ResumoPrestadorGuia obja = a;
					ResumoPrestadorGuia objb = b;
					return obja.getPrestadorFantasia().compareTo(objb.getPrestadorFantasia());
				}
			});
		}
		if (ordem.equals(POR_PRODUCAO)){
			Collections.sort(resumoPrestadorGuia, new Comparator<ResumoPrestadorGuia>(){
				public int compare(ResumoPrestadorGuia a, ResumoPrestadorGuia b) {
					ResumoPrestadorGuia obja = a;
					ResumoPrestadorGuia objb = b;
					return objb.getPercentualProducao().compareTo(obja.getPercentualProducao());
				}
			});
		}
		if (ordem.equals(POR_FATURAMENTO)){
			Collections.sort(resumoPrestadorGuia, new Comparator<ResumoPrestadorGuia>(){
				public int compare(ResumoPrestadorGuia a, ResumoPrestadorGuia b) {
					ResumoPrestadorGuia obja = a;
					ResumoPrestadorGuia objb = b;
					return objb.getPercentualFaturamento().compareTo(obja.getPercentualFaturamento());
				}
			});
		}
	}

	/**
	 * Valida os parâmetros necessários para geração do relatório de consultas por prestador.
	 * @param mes
	 * @param pessoa
	 * @param ordem
	 * @throws ValidateException
	 */
	private void validarParametros(Date mes, String pessoa, String ordem) throws ValidateException{
		if (mes == null){
			throw new ValidateException("O intervalo para geração do relatório deve ser informado.");
		}
		
		if (!pessoa.equals(PESSOA_FISICA) && !pessoa.equals(PESSOA_JURIDICA)){
			throw new ValidateException("Tipo de Pessoa Inválido.");
		}
		
		if (!ordem.equals(POR_PRESTADOR) && !ordem.equals(POR_PRODUCAO) && !ordem.equals(POR_FATURAMENTO)){
			throw new ValidateException("Ordenação Inválida.");
		}
	}

	public Date getMes() {
		return mes;
	}

	public void setMes(Date mes) {
		this.mes = mes;
	}

	public String getTipoPrestador() {
		return tipoPrestador;
	}

	public void setTipoPrestador(String tipoPrestador) {
		this.tipoPrestador = tipoPrestador;
	}

	public int getQtdePrestadores() {
		return qtdePrestadores;
	}

	public void setQtdePrestadores(int qtdePrestadores) {
		this.qtdePrestadores = qtdePrestadores;
	}

	public int getQtdeConsultasEletivas() {
		return qtdeConsultasEletivas;
	}

	public void setQtdeConsultasEletivas(int qtdeConsultasEletivas) {
		this.qtdeConsultasEletivas = qtdeConsultasEletivas;
	}

	public BigDecimal getVlrConsultasEletivas() {
		return vlrConsultasEletivas;
	}

	public void setVlrConsultasEletivas(BigDecimal vlrConsultasEletivas) {
		this.vlrConsultasEletivas = vlrConsultasEletivas;
	}

	public int getQtdeConsultasUrgencia() {
		return qtdeConsultasUrgencia;
	}

	public void setQtdeConsultasUrgencia(int qtdeConsultasUrgencia) {
		this.qtdeConsultasUrgencia = qtdeConsultasUrgencia;
	}

	public BigDecimal getVlrConsultasUrgencia() {
		return vlrConsultasUrgencia;
	}

	public void setVlrConsultasUrgencia(BigDecimal vlrConsultasUrgencia) {
		this.vlrConsultasUrgencia = vlrConsultasUrgencia;
	}

	public int getQtdeTotalGeral() {
		return qtdeTotalGeral;
	}

	public void setQtdeTotalGeral(int qtdeTotalGeral) {
		this.qtdeTotalGeral = qtdeTotalGeral;
	}

	public BigDecimal getVlrTotalGeral() {
		return vlrTotalGeral;
	}

	public void setVlrTotalGeral(BigDecimal vlrTotalGeral) {
		this.vlrTotalGeral = vlrTotalGeral;
	}

	public List<ResumoPrestadorGuia> getResumoPrestadorGuia() {
		return resumoPrestadorGuia;
	}

	public void setResumoPrestadorGuia(List<ResumoPrestadorGuia> resumoPrestadorGuia) {
		this.resumoPrestadorGuia = resumoPrestadorGuia;
	}

}
