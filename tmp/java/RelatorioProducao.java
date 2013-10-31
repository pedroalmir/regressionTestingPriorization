package br.com.infowaypi.ecare.relatorio.producao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Transaction;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Relatorio de Producao
 * Relaciona quantidades e valores apresentados e apurados por prestador
 * @author Danilo Medeiros
 * @changes Klebert, Leonardo Sampaio
 */
public class RelatorioProducao {

	public enum TipoContador{
		URGENCIA,
		INTERNACAO_CLINICA,	
		INTERNACAO_CIRURGICA,
		CIRURGIA_AMBULATORIAL,
		TERAPIA_SERIADA,
		OUTROS,
		TOTAL_EVENTOS	
	}

	public enum TipoValor{
		VALOR_URGENCIA,	
		VALOR_INTERNACAO,	
		VALOR_CIRURGIA_AMBULATORIAL,
		VALOR_TERAPIA_SERIADA,
		VALOR_OUTROS,
		VALOR_TOTAL_EVENTOS
	}
	
	private ResumoRelatorioProducao resumo;

	//reduce		 map	
	private Map<Prestador, Map<TipoValor, List<BigDecimal>>> somaApresentada;
	private Map<Prestador, Map<TipoValor, List<BigDecimal>>> somaApurada;
	private Map<Prestador, Map<TipoContador, List<Integer>>> contadoresApurados;
	private Map<Prestador, Map<TipoContador, List<Integer>>> contadoresApresentados;

	private Map<Prestador, Collection<GuiaProducao>> guiasProducao;
	private ManagerProducao manager;
	
	public RelatorioProducao(){
		somaApresentada = new HashMap<Prestador, Map<TipoValor, List<BigDecimal>>>();
		somaApurada = new HashMap<Prestador, Map<TipoValor, List<BigDecimal>>>();
		contadoresApurados = new HashMap<Prestador, Map<TipoContador, List<Integer>>>();
		contadoresApresentados = new HashMap<Prestador, Map<TipoContador, List<Integer>>>();
		
		this.resumo = new ResumoRelatorioProducao();
	}

	public ResumoRelatorioProducao gerarRelatorio(Date dataInicioAdmissao, Date dataFimAdmissao) throws ValidateException{
		if(this.getManager() == null){
			this.setManager(new ManagerProducao());
		}
		validarParametros(dataInicioAdmissao, dataFimAdmissao);
		
		buscarGuias(dataInicioAdmissao, dataFimAdmissao);
		map();
		reduce();
		return resumo;
	}

	private void validarParametros(Date dataInicioAdmissao,
			Date dataFimAdmissao) throws ValidateException {
		
		boolean dataInicioNotNull = dataInicioAdmissao != null;
		boolean dataFimNotNull = dataFimAdmissao != null;
		
		if((dataInicioNotNull && !dataFimNotNull) || (!dataInicioNotNull && dataFimNotNull)) {
			throw new ValidateException("A data inicial e final devem ser informadas!");
		}
		
		if(dataInicioNotNull && dataFimNotNull && Utils.compareData(dataInicioAdmissao, dataFimAdmissao) > 0){
			throw new ValidateException("A data do início do período não pode ser maior do que a data final.");
		}
	}

	private void buscarGuias(Date dataInicioAdmissao, Date dataFimAdmissao) {		
		guiasProducao = manager.getGuiasAgrupadasPorPrestador(dataInicioAdmissao, dataFimAdmissao);
	}

	/**
	 * Mapeia(agrupa) os valores e quantidades por tipo (consultas, internacoes clinicas e cirurgicas, pequena cirurgia e terapia seriada)
	 * @see {@link TipoContador} e {@link TipoValor}
	 */
	public void map() {
		TipoContador tipoContador;
		TipoValor tipoValor;
		for (Prestador prestador : getGuias().keySet()) {
			init(prestador);

			Map<TipoValor, List<BigDecimal>> somaApur = somaApurada.get(prestador);
			Map<TipoValor, List<BigDecimal>> somaApre = somaApresentada.get(prestador);
			Map<TipoContador, List<Integer>> contadoresApre = contadoresApresentados.get(prestador);

			Collection<GuiaProducao> guiasDoPrestador = getGuias().get(prestador);
			for (GuiaProducao guiaProducao : guiasDoPrestador) {
				
				if(guiaProducao.isConsultaAtendimentoUrgencia()){
					tipoContador = TipoContador.URGENCIA;
					tipoValor = TipoValor.VALOR_URGENCIA;
				
				}else if(guiaProducao.isInternacao()){

				    tipoValor = TipoValor.VALOR_INTERNACAO;

					if(guiaProducao.isInternacaoCirurgica()){
						tipoContador = TipoContador.INTERNACAO_CIRURGICA;
					}else{
						tipoContador = TipoContador.INTERNACAO_CLINICA;
					}
				
				} else if(guiaProducao.isCirurgiaAmbulatorial()){
					tipoContador = TipoContador.CIRURGIA_AMBULATORIAL;
					tipoValor = TipoValor.VALOR_CIRURGIA_AMBULATORIAL;
					
				} else if(guiaProducao.isTratamentoSeriado()){
					tipoContador = TipoContador.TERAPIA_SERIADA;
					tipoValor = TipoValor.VALOR_TERAPIA_SERIADA;
				} 
				
				else {
					tipoContador = TipoContador.OUTROS;
					tipoValor = TipoValor.VALOR_OUTROS;
				}
				contadoresApre.get(tipoContador).add(1);
				
				BigDecimal valorApresentado = null, valorApurado = null; 
				
				if (guiaProducao.getValorTotalApresentado()==null)
				    valorApresentado = new BigDecimal(0);
				else
				    valorApresentado = guiaProducao.getValorTotalApresentado();

				if (guiaProducao.getValorPagoPrestador()==null)
				    valorApurado = new BigDecimal(0);
				else
				    valorApurado = guiaProducao.getValorPagoPrestador();
				
				somaApre.get(tipoValor).add(valorApresentado);
				somaApur.get(tipoValor).add(valorApurado);
				
			}
		}
	}

	/**
	 * Reduz(somatorio) as quantidades e valores das guias por tipos de guia(consultas, internacoes, pequena cirurgia e terapia seriada) 
	 * por prestador, bem como totais para todo os prestadores
	 * @see {@link TipoContador} e {@link TipoValor}
	 */
	public void reduce() {
		for (Prestador prestador : getGuias().keySet()) {
			
			Map<TipoValor, List<BigDecimal>> somaApresentado = somaApresentada.get(prestador);
			Map<TipoValor, List<BigDecimal>> somaApurado = somaApurada.get(prestador);
			Map<TipoContador, List<Integer>> contadoresApre = contadoresApresentados.get(prestador);
			Map<TipoContador, List<Integer>> contadoresApur = contadoresApurados.get(prestador);
			
			BigDecimal totalValorApurado = BigDecimal.ZERO;
			BigDecimal totalValorApresentado = BigDecimal.ZERO;
			for (TipoValor tipoValor : TipoValor.values()) {
				BigDecimal somaApresentada = soma(somaApresentado.get(tipoValor));
				putSomaApresentada(tipoValor, prestador, somaApresentada);
				totalValorApresentado = totalValorApresentado.add(somaApresentada);
				
				BigDecimal somaApurada = soma(somaApurado.get(tipoValor));
				putSomaApurada(tipoValor, prestador, somaApurada);
				totalValorApurado = totalValorApurado.add(somaApurada);
				
			}
			
			int totalContadorApurado = 0;
			int totalContadorApresentado = 0;
			for (TipoContador tipoContador: TipoContador.values()) {
				putContadorApresentado(tipoContador, prestador, contadoresApre.get(tipoContador).size());
				totalContadorApresentado += contadoresApre.get(tipoContador).size();
				
				putContadorApurado(tipoContador, prestador, contadoresApur.get(tipoContador).size());
				totalContadorApurado+= contadoresApur.get(tipoContador).size();
			}
			
			putContadorApresentado(TipoContador.TOTAL_EVENTOS, prestador, totalContadorApresentado);
			putContadorApurado(TipoContador.TOTAL_EVENTOS, prestador, totalContadorApurado);
			
			putSomaApresentada(TipoValor.VALOR_TOTAL_EVENTOS, prestador, totalValorApresentado);
			putSomaApurada(TipoValor.VALOR_TOTAL_EVENTOS, prestador, totalValorApurado);
		}
		
		reduceTotais();
	}
	
	private void reduceTotais() {
		ProducaoCompetencia totais = resumo.getTotais();

		BigDecimal totalSomaApres = BigDecimal.ZERO;
		BigDecimal totalSomaApur = BigDecimal.ZERO;
		for (TipoValor tipo : TipoValor.values()) {			
			List<BigDecimal> subTotais = new ArrayList<BigDecimal>();
			for (ProducaoCompetenciaPrestador producaoPres : resumo.getMapProducoes().values()) {
				subTotais.add(producaoPres.getSomatoriosApresentados().get(tipo));
			}
			BigDecimal soma = soma(subTotais);
			totais.putSomaApresentada(tipo, soma);
			totalSomaApres = MoneyCalculation.getSoma(totalSomaApres, soma); 
					
			subTotais = new ArrayList<BigDecimal>();
			for (ProducaoCompetenciaPrestador producaoPres : resumo.getMapProducoes().values()) {
				subTotais.add(producaoPres.getSomatoriosApurados().get(tipo));
			}
			soma = soma(subTotais);
			totais.putSomaApurada(tipo, soma(subTotais));
			totalSomaApur =  MoneyCalculation.getSoma(totalSomaApur, soma);
		}
		
		totais.putSomaApurada(TipoValor.VALOR_TOTAL_EVENTOS, totalSomaApres);
		totais.putSomaApurada(TipoValor.VALOR_TOTAL_EVENTOS, totalSomaApur);
		
		for (TipoContador tipo : TipoContador.values()) {			
			List<Integer> subTotais = new ArrayList<Integer>();
			for (ProducaoCompetenciaPrestador producaoPres : resumo.getMapProducoes().values()) {
				subTotais.add(producaoPres.getContadoresApresentados().get(tipo));
			}
			int soma = somaInteger(subTotais);
			totais.putContadorApresentada(tipo, soma);
			
			subTotais = new ArrayList<Integer>();
			for (ProducaoCompetenciaPrestador producaoPres : resumo.getMapProducoes().values()) {
				subTotais.add(producaoPres.getContadoresApurados().get(tipo));
			}
			soma = somaInteger(subTotais);
			totais.putContadorApurada(tipo, soma);
		}
		
		//footer
		//contadores
		Map<TipoContador, Integer> ctdApres = totais.getContadoresApresentados();
		int qtdTotaisEventosApres = getSomatorio(ctdApres, 	TipoContador.URGENCIA, TipoContador.INTERNACAO_CIRURGICA, TipoContador.INTERNACAO_CLINICA, TipoContador.OUTROS, TipoContador.CIRURGIA_AMBULATORIAL, TipoContador.TERAPIA_SERIADA);
		totais.putContadorApresentada(TipoContador.TOTAL_EVENTOS, qtdTotaisEventosApres);

		Map<TipoContador, Integer> ctdApur = totais.getContadoresApurados();
		int qtdTotaisEventosApur = getSomatorio(ctdApur, 	TipoContador.URGENCIA, TipoContador.INTERNACAO_CIRURGICA, TipoContador.INTERNACAO_CLINICA, TipoContador.OUTROS, TipoContador.CIRURGIA_AMBULATORIAL, TipoContador.TERAPIA_SERIADA);		
		totais.putContadorApurada(TipoContador.TOTAL_EVENTOS, qtdTotaisEventosApur);
		
		//somas
		Map<TipoValor, BigDecimal> somaApresentados = totais.getSomatoriosApresentados();
		BigDecimal somaTotalApres = getSomatorio(somaApresentados, TipoValor.VALOR_URGENCIA, TipoValor.VALOR_INTERNACAO, TipoValor.VALOR_OUTROS,TipoValor.VALOR_CIRURGIA_AMBULATORIAL ,TipoValor.VALOR_TERAPIA_SERIADA);
		totais.putSomaApresentada(TipoValor.VALOR_TOTAL_EVENTOS, somaTotalApres);
		
		Map<TipoValor, BigDecimal> somaApurados = totais.getSomatoriosApurados();
		BigDecimal somaTotalApur = getSomatorio(somaApurados, TipoValor.VALOR_URGENCIA, TipoValor.VALOR_INTERNACAO, TipoValor.VALOR_OUTROS, TipoValor.VALOR_CIRURGIA_AMBULATORIAL, TipoValor.VALOR_TERAPIA_SERIADA);
		totais.putSomaApurada(TipoValor.VALOR_TOTAL_EVENTOS, somaTotalApur);
	}

	private BigDecimal getSomatorio(Map<TipoValor, BigDecimal> ctdApres,  TipoValor... tipos){
		BigDecimal soma = BigDecimal.ZERO;
		for (TipoValor tipo : tipos) {
			soma = soma.add(ctdApres.get(tipo));	
		}
		return soma;
	}
	
	private int getSomatorio(Map<TipoContador, Integer> ctdApres,  TipoContador... tipos){
		int soma = 0;
		for (TipoContador tipo : tipos) {
			soma += ctdApres.get(tipo);	
		}
		return soma;
	}
	
	public void putSomaApurada(TipoValor tipo, Prestador prestador, BigDecimal soma) {
		this.resumo.getMapProducoes().get(prestador).putSomaApurada(tipo, soma);
	}

	public void putSomaApresentada(TipoValor tipo, Prestador prestador, BigDecimal soma) {
		this.resumo.getMapProducoes().get(prestador).putSomaApresentada(tipo, soma);
	}

	public void putContadorApresentado(TipoContador tipoContador, Prestador prestador,
			int cont) {
		this.resumo.getMapProducoes().get(prestador).putContadorApresentada(tipoContador, cont);
	}

	public void putContadorApurado(TipoContador tipoContador, Prestador prestador,	int cont) {
		this.resumo.getMapProducoes().get(prestador).putContadorApurada(tipoContador, cont);
	}

	private Integer somaInteger(List<Integer> list) {
		int soma = 0;
		for (Integer integer : list) {
			soma += integer;
		}
		return soma;
	}
	
	private BigDecimal soma(List<BigDecimal> list) {
		BigDecimal[] valores = list.toArray(new BigDecimal[list.size()]);
		return MoneyCalculation.getSoma(valores);
	}

	public void init(Prestador prestador){

		Map<TipoValor, List<BigDecimal>> somatoriosPrestadorApresentado = new HashMap<TipoValor, List<BigDecimal>>();
		Map<TipoValor, List<BigDecimal>> somatoriosPrestadorApurado = new HashMap<TipoValor, List<BigDecimal>>();
		for (TipoValor tipo : TipoValor.values()) {
			somatoriosPrestadorApresentado.put(tipo, new ArrayList<BigDecimal>());
			somatoriosPrestadorApurado.put(tipo, new ArrayList<BigDecimal>());
		}
		this.somaApresentada.put(prestador, somatoriosPrestadorApresentado);
		this.somaApurada.put(prestador, somatoriosPrestadorApurado);

		Map<TipoContador, List<Integer>> contadoresPrestador = new HashMap<TipoContador, List<Integer>>();
		for (TipoContador tipo : TipoContador.values()) {
			contadoresPrestador.put(tipo, new ArrayList<Integer>());
		}
		
		this.contadoresApurados.put(prestador, contadoresPrestador);
		this.contadoresApresentados.put(prestador, contadoresPrestador);

		this.resumo.getMapProducoes().put(prestador, new ProducaoCompetenciaPrestador(prestador));
	}
	
	public Map<Prestador, Collection<GuiaProducao>> getGuias() {
		return guiasProducao;
	}
	
	public void setGuiasProducao(Map<Prestador, Collection<GuiaProducao>> guiasProducao) {
		this.guiasProducao = guiasProducao;
	}
	
	public ResumoRelatorioProducao getResumo() {
		return resumo;
	}
	
	public void setResumo(ResumoRelatorioProducao resumo) {
		this.resumo = resumo;
	}
	
	public ManagerProducao getManager() {
		return manager;
	}

	public void setManager(ManagerProducao manager) {
		this.manager = manager;
	}
	
}