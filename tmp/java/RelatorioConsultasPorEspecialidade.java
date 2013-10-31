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

import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * Relatório de consultas (eletivas e de urgência) realizadas em um determinado mês, agrupadas por suas respectivas especialidades.
 * @author benedito
 *
 */
public class RelatorioConsultasPorEspecialidade {
	public static final String TIPO_GUIA_CONSULTA_ELETIVA = "GCS";
	public static final String TIPO_GUIA_CONSULTA_URGENCIA = "CUR";
	
	public static final String POR_ESPECIALIDADE = "Especialidade";
	public static final String POR_ELETIVAS = "Vlr. Consultas Eletivas";
	public static final String POR_URGENCIA = "Vlr. Consultas Urgência";

	/**
	 * Intervalo para geração do relatório de consultas por especialidade.
	 * Ex.: 01/2008 (Janeiro) - 01/01/2008 a 31/01/2008.
	 */
	private Date mes;
	
	/**
	 * Quantidade de especialidades com produção em um determinado mês, ou seja, aquelas que estão presentes em pelo menos uma guia.
	 */
	private int qtdeEspecialidades;
	
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
	 * Representa o detalhamento do relatório de consultas por especialidade.
	 */
	private List<ResumoEspecialidadeGuia> resumoEspecialidadeGuia;
	
	public RelatorioConsultasPorEspecialidade() {
		this.vlrConsultasEletivas = BigDecimal.ZERO;
		this.vlrConsultasUrgencia = BigDecimal.ZERO;
		this.vlrTotalGeral = BigDecimal.ZERO;
		this.resumoEspecialidadeGuia = new ArrayList<ResumoEspecialidadeGuia>();
	}

	/**
	 * Gera o relatório de consultas por especialidade de um determinado mês
	 * @param mes
	 * @param ordem
	 * @return Resumo e detalhamento do relatório.
	 * @throws ValidateException
	 */
	public RelatorioConsultasPorEspecialidade gerarRelatorio(Date mes, String ordem) throws ValidateException{
		this.validarParametros(mes, ordem);
		
		this.mes = mes;
		
		List<GuiaSimples<ProcedimentoInterface>> consultasEletivas = this.buscarConsultas(TIPO_GUIA_CONSULTA_ELETIVA, mes);
		List<GuiaSimples<ProcedimentoInterface>> consultasUrgencia = this.buscarConsultas(TIPO_GUIA_CONSULTA_URGENCIA, mes);
		
		Map<Especialidade, Set<GuiaSimples<ProcedimentoInterface>>> mapaConsultasEletivas = this.agruparConsultas(consultasEletivas);
		Map<Especialidade, Set<GuiaSimples<ProcedimentoInterface>>> mapaConsultasUrgencia = this.agruparConsultas(consultasUrgencia);
		
		Set<Especialidade> especialidades = new HashSet<Especialidade>();
		especialidades.addAll(this.getEspecialidades(mapaConsultasEletivas));
		especialidades.addAll(this.getEspecialidades(mapaConsultasUrgencia));
		
		this.qtdeEspecialidades = especialidades.size();
		
		this.qtdeConsultasEletivas = getGuias(mapaConsultasEletivas).size();
		this.vlrConsultasEletivas = getValorTotal(mapaConsultasEletivas);
		
		this.qtdeConsultasUrgencia = getGuias(mapaConsultasUrgencia).size();
		this.vlrConsultasUrgencia = getValorTotal(mapaConsultasUrgencia);
		
		this.qtdeTotalGeral = this.qtdeConsultasEletivas + this.qtdeConsultasUrgencia;
		this.vlrTotalGeral = this.vlrConsultasEletivas.add(this.vlrConsultasUrgencia);
		
		for (Especialidade especialidade : especialidades) {
			this.resumoEspecialidadeGuia.add(new ResumoEspecialidadeGuia(
					especialidade.getDescricao(),
					this.getGuiasEspecialidade(especialidade, mapaConsultasEletivas).size(),
					this.getValorTotalEspecialidade(especialidade, mapaConsultasEletivas),
					this.getGuiasEspecialidade(especialidade, mapaConsultasUrgencia).size(),
					this.getValorTotalEspecialidade(especialidade, mapaConsultasUrgencia))
			);
		}
		
		this.ordenarDetalhamento(this.resumoEspecialidadeGuia, ordem);
		
		this.resumoEspecialidadeGuia.add(new ResumoEspecialidadeGuia(
				"TOTAL",
				this.qtdeConsultasEletivas,
				this.vlrConsultasEletivas,
				this.qtdeConsultasUrgencia,
				this.vlrConsultasUrgencia)
		);
		
		return this;
	}

	/**
	 * Busca no banco de dados todas as consultas (eletivas e de urgência) realizadas em um determinado mês.
	 * <h2>Observação:</h2>
	 * <ul>Consultas Eletivas - Confirmadas ou Faturadas.</ul>
	 * <ul>Consultas de Urgência - Fechadas ou Faturadas.</ul>
	 * @param tipoDeGuia
	 * @param mes
	 * @return Lista de guias.
	 * @throws ValidateException
	 */
	@SuppressWarnings("unchecked")
	private List<GuiaSimples<ProcedimentoInterface>> buscarConsultas(String tipoDeGuia, Date mes) throws ValidateException{
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

		return criteria.list();
	}
	
	/**
	 * Agrupa as consultas por meio de suas respectivas especialidades.
	 * @param consultas
	 * @return Mapa de especialidades (chave) com suas respectivas guias (valores).
	 */
	private Map<Especialidade, Set<GuiaSimples<ProcedimentoInterface>>> agruparConsultas(List<GuiaSimples<ProcedimentoInterface>> consultas){
		Map<Especialidade, Set<GuiaSimples<ProcedimentoInterface>>> especialidadeGuia = new HashMap<Especialidade, Set<GuiaSimples<ProcedimentoInterface>>>();
		
		for (GuiaSimples<ProcedimentoInterface> consulta : consultas) {
			Especialidade especialidade = consulta.getEspecialidade();
			if (especialidadeGuia.containsKey(especialidade)) {
				Set<GuiaSimples<ProcedimentoInterface>> guiaInterna = especialidadeGuia.get(especialidade);
				guiaInterna.add(consulta);
				especialidadeGuia.put(especialidade, guiaInterna);
			} else {
				Set<GuiaSimples<ProcedimentoInterface>> guiaInterna = new HashSet<GuiaSimples<ProcedimentoInterface>>();
				guiaInterna.add(consulta);
				especialidadeGuia.put(especialidade, guiaInterna);
			}
		}

		return especialidadeGuia;
	}
	
	/**
	 * Retorna as especialidades do mapa.
	 * @param especialidadeGuia
	 * @return Conjunto de especialidades.
	 */
	private Set<Especialidade> getEspecialidades(Map<Especialidade, Set<GuiaSimples<ProcedimentoInterface>>> especialidadeGuia){
		return especialidadeGuia.keySet();
	}
	
	/**
	 * Retorna as guias do mapa.
	 * @param especialidadeGuia
	 * @return Conjunto de guias.
	 */
	private Set<GuiaSimples<ProcedimentoInterface>> getGuias(Map<Especialidade, Set<GuiaSimples<ProcedimentoInterface>>> especialidadeGuia){
		Set<GuiaSimples<ProcedimentoInterface>> guias = new HashSet<GuiaSimples<ProcedimentoInterface>>();
		
		for (Especialidade especialidade : this.getEspecialidades(especialidadeGuia)) {
			guias.addAll(this.getGuiasEspecialidade(especialidade, especialidadeGuia));
		}
		
		return guias;
	}
	
	/**
	 * Retorna o valor das guias do mapa.
	 * @param especialidadeGuia
	 * @return Valor das guias.
	 */
	private BigDecimal getValorTotal(Map<Especialidade, Set<GuiaSimples<ProcedimentoInterface>>> especialidadeGuia){
		BigDecimal valorTotal = new BigDecimal(0);
		
		for (GuiaSimples<ProcedimentoInterface> guia : this.getGuias(especialidadeGuia)) {
			valorTotal = valorTotal.add(guia.getValorTotal());
		}
		
		return valorTotal;
	}
	
	/**
	 * Retorna as guias do mapa que possuem uma determinada especialidade.
	 * @param especialidade
	 * @param especialidadeGuia
	 * @return Conjunto de guias.
	 */
	private Set<GuiaSimples<ProcedimentoInterface>> getGuiasEspecialidade(Especialidade especialidade, Map<Especialidade, Set<GuiaSimples<ProcedimentoInterface>>> especialidadeGuia){
		Set<GuiaSimples<ProcedimentoInterface>> guias = new HashSet<GuiaSimples<ProcedimentoInterface>>();
		
		if (especialidadeGuia.containsKey(especialidade)){
			guias = especialidadeGuia.get(especialidade);
		}
		
		return guias;
	}
	
	/**
	 * Retorna o valor das guias do mapa que possuem uma determinada especialidade.
	 * @param especialidade
	 * @param especialidadeGuia
	 * @return Valor das guias.
	 */
	private BigDecimal getValorTotalEspecialidade(Especialidade especialidade, Map<Especialidade, Set<GuiaSimples<ProcedimentoInterface>>> especialidadeGuia){
		BigDecimal valorTotal = new BigDecimal(0);
		
		for (GuiaSimples<ProcedimentoInterface> guia : this.getGuiasEspecialidade(especialidade, especialidadeGuia)) {
			valorTotal = valorTotal.add(guia.getValorTotal());
		}
		
		return valorTotal;
	}
	
	/**
	 * Ordena o detalhamento do relatório pela especialidade ou pelo valor das consultas eletivas ou pelo valor das consultas de urgência.
	 * @param resumoEspecialidadeGuia
	 * @param ordem
	 */
	private void ordenarDetalhamento(List<ResumoEspecialidadeGuia> resumoEspecialidadeGuia, String ordem){
		if (ordem.equals(POR_ESPECIALIDADE)){
			Collections.sort(resumoEspecialidadeGuia, new Comparator<ResumoEspecialidadeGuia>(){
				public int compare(ResumoEspecialidadeGuia a, ResumoEspecialidadeGuia b) {
					ResumoEspecialidadeGuia obja = a;
					ResumoEspecialidadeGuia objb = b;
					return obja.getEspecialidadeDescricao().compareTo(objb.getEspecialidadeDescricao());
				}
			});
		}
		if (ordem.equals(POR_ELETIVAS)){
			Collections.sort(resumoEspecialidadeGuia, new Comparator<ResumoEspecialidadeGuia>(){
				public int compare(ResumoEspecialidadeGuia a, ResumoEspecialidadeGuia b) {
					ResumoEspecialidadeGuia obja = a;
					ResumoEspecialidadeGuia objb = b;
					return objb.getVlrConsultasEletivas().compareTo(obja.getVlrConsultasEletivas());
				}
			});
		}
		if (ordem.equals(POR_URGENCIA)){
			Collections.sort(resumoEspecialidadeGuia, new Comparator<ResumoEspecialidadeGuia>(){
				public int compare(ResumoEspecialidadeGuia a, ResumoEspecialidadeGuia b) {
					ResumoEspecialidadeGuia obja = a;
					ResumoEspecialidadeGuia objb = b;
					return objb.getVlrConsultasUrgencia().compareTo(obja.getVlrConsultasUrgencia());
				}
			});
		}
	}
	
	/**
	 * Valida os parâmetros necessários para geração do relatório de consultas por especialidade.
	 * @param mes
	 * @param ordem
	 * @throws ValidateException
	 */
	private void validarParametros(Date mes, String ordem) throws ValidateException{
		if (mes == null){
			throw new ValidateException("O intervalo para geração do relatório deve ser informado.");
		}
		
		if (!ordem.equals(POR_ESPECIALIDADE) && !ordem.equals(POR_ELETIVAS) && !ordem.equals(POR_URGENCIA)){
			throw new ValidateException("Ordenação Inválida.");
		}
	}

	public Date getMes() {
		return mes;
	}

	public void setMes(Date mes) {
		this.mes = mes;
	}

	public int getQtdeEspecialidades() {
		return qtdeEspecialidades;
	}

	public void setQtdeEspecialidades(int qtdeEspecialidades) {
		this.qtdeEspecialidades = qtdeEspecialidades;
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

	public List<ResumoEspecialidadeGuia> getResumoEspecialidadeGuia() {
		return resumoEspecialidadeGuia;
	}

	public void setResumoEspecialidadeGuia(List<ResumoEspecialidadeGuia> resumoEspecialidadeGuia) {
		this.resumoEspecialidadeGuia = resumoEspecialidadeGuia;
	}

}
