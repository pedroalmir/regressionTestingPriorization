package br.com.infowaypi.ecare.relatorio.producao;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import br.com.infowaypi.ecare.relatorio.producao.RelatorioProducao.TipoContador;
import br.com.infowaypi.ecare.relatorio.producao.RelatorioProducao.TipoValor;


public class ProducaoCompetencia {

	private final Map<TipoContador, Integer> contadoresApurados;
	private final Map<TipoValor, BigDecimal> somatoriosApurados;
	
	private final Map<TipoContador, Integer> contadoresApresentados;
	private final Map<TipoValor, BigDecimal> somatoriosApresentados;

	public ProducaoCompetencia() {
		super();

		this.contadoresApresentados = new HashMap<TipoContador, Integer>();
		this.somatoriosApresentados = new HashMap<TipoValor, BigDecimal>();
		
		this.contadoresApurados = new HashMap<TipoContador, Integer>();
		this.somatoriosApurados = new HashMap<TipoValor, BigDecimal>();
	}

	
	public int getQtdAtendimentoUrgenciaApre(){
		return contadoresApresentados.get(TipoContador.URGENCIA);
	}

	public int getQtdInternacaoClinicaApre(){
		return contadoresApresentados.get(TipoContador.INTERNACAO_CLINICA);
	}
	
	public int getQtdInternacaoCirurgicaApre(){
		return contadoresApresentados.get(TipoContador.INTERNACAO_CIRURGICA);
	}
	
	public int getQtdCirurgiaAmbulatorialApre(){
		return contadoresApresentados.get(TipoContador.CIRURGIA_AMBULATORIAL);
	}
	
	public int getQtdTerapiaSeriadaApre(){
		return contadoresApresentados.get(TipoContador.TERAPIA_SERIADA);
	}
	
	public int getQtdOutrosApre(){
		return contadoresApresentados.get(TipoContador.OUTROS);
	}
	
	public int getQtdTotalEventosApre(){
		return contadoresApresentados.get(TipoContador.TOTAL_EVENTOS);
	}
	
	public int getQtdAtendimentoUrgenciaApur(){
		return contadoresApurados.get(TipoContador.URGENCIA);
	}

	public int getQtdInternacaoClinicaApur(){
		return contadoresApurados.get(TipoContador.INTERNACAO_CLINICA);
	}
	
	public int getQtdInternacaoCirurgicaApur(){
		return contadoresApurados.get(TipoContador.INTERNACAO_CIRURGICA);
	}
	
	public int getQtdCirurgiaAmbulatorialApur(){
		return contadoresApurados.get(TipoContador.CIRURGIA_AMBULATORIAL);
	}
	
	public int getQtdTerapiaSeriadaApur(){
		return contadoresApurados.get(TipoContador.TERAPIA_SERIADA);
	}
	
	public int getQtdOutrosApur(){
		return contadoresApurados.get(TipoContador.OUTROS);
	}
	
	public int getQtdTotalEventosApur(){
		return contadoresApurados.get(TipoContador.TOTAL_EVENTOS);
	}	

	public BigDecimal getValorAtendimentoUrgenciaApre(){
		return somatoriosApresentados.get(TipoValor.VALOR_URGENCIA);
	}
	public BigDecimal getValorInternacaoApre(){
		return somatoriosApresentados.get(TipoValor.VALOR_INTERNACAO);
	}
	
	public BigDecimal getValorCirurgiaAmbulatorialApre(){
		return somatoriosApresentados.get(TipoValor.VALOR_CIRURGIA_AMBULATORIAL);
	}
	
	public BigDecimal getValorTerapiaSeriadaApre(){
		return somatoriosApresentados.get(TipoValor.VALOR_TERAPIA_SERIADA);
	}
	
	public BigDecimal getValorOutrosApre(){
		return somatoriosApresentados.get(TipoValor.VALOR_OUTROS);
	}

	public BigDecimal getValorTotalApre(){
		return somatoriosApresentados.get(TipoValor.VALOR_TOTAL_EVENTOS);
	}

	public BigDecimal getValorAtendimentoUrgenciaApur(){
		return somatoriosApurados.get(TipoValor.VALOR_URGENCIA);
	}
	public BigDecimal getValorInternacaoApur(){
		return somatoriosApurados.get(TipoValor.VALOR_INTERNACAO);
	}
	
	public BigDecimal getValorCirurgiaAmbulatorialApur(){
		return somatoriosApurados.get(TipoValor.VALOR_CIRURGIA_AMBULATORIAL);
	}
	
	public BigDecimal getValorTerapiaSeriadaApur(){
		return somatoriosApurados.get(TipoValor.VALOR_TERAPIA_SERIADA);
	}
	
	public BigDecimal getValorOutrosApur(){
		return somatoriosApurados.get(TipoValor.VALOR_OUTROS);
	}

	public BigDecimal getValorTotalApur(){
		return somatoriosApurados.get(TipoValor.VALOR_TOTAL_EVENTOS);
	}

	public Map<TipoContador, Integer> getContadoresApresentados() {
		return contadoresApresentados;
	}

	public Map<TipoContador, Integer> getContadoresApurados() {
		return contadoresApurados;
	}

	public Map<TipoValor, BigDecimal> getSomatoriosApurados() {
		return somatoriosApurados;
	}

	public Map<TipoValor, BigDecimal> getSomatoriosApresentados() {
		return somatoriosApresentados;
	}

	public void putSomaApurada(TipoValor tipo, BigDecimal soma) {
		this.somatoriosApurados.put(tipo, soma);
	}
	
	public void putContadorApurada(TipoContador tipo, int valor){
		this.contadoresApurados.put(tipo, valor);
	}
	
	public void putSomaApresentada(TipoValor tipo, BigDecimal soma) {
		this.somatoriosApresentados.put(tipo, soma);
	}
	
	public void putContadorApresentada(TipoContador tipo, int valor){
		this.contadoresApresentados.put(tipo, valor);
	}
	@Override
	public String toString() {
		StringBuilder apre = new StringBuilder();
		String quebra = "\n";
		StringBuilder apur = new StringBuilder();
		for (TipoValor tipo : TipoValor.values()) {
			apre.append(tipo.name() + " : "+this.getSomatoriosApresentados().get(tipo) + quebra);
			apur.append(tipo.name() + " : "+this.getSomatoriosApurados().get(tipo) + quebra);
		}
		
		for (TipoContador tipo : TipoContador.values()) {
			apre.append(tipo.name() + " : "+this.getContadoresApresentados().get(tipo) + quebra);
			apur.append(tipo.name() + " : "+this.getContadoresApurados().get(tipo) + quebra);
		}
		return apre.toString();
	}
}
