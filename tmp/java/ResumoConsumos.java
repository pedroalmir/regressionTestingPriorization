package br.com.infowaypi.ecarebc.consumo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.msr.utils.Utils;


/**
 * Resumo de consumos, organizados por prestador, de acordo com a competência
 * informada.
 * @author Mário Sérgio Coelho Marroquim
 */
public class ResumoConsumos {

	private Date competencia;
	private List<Prestador> prestadores;
	private List<ConsumoInterface> consumos;
	private int numeroTotalDePrestadoresComConsumo;
	private float consumoGeralDeConsultas;
	private float consumoGeralDeExames;
	
	private float consumoGeralDeUrgencias;
	private float consumoGeralDeInternacoesEletivas;
	
	private float consumoGeral;

	private int numeroGeralDeConsultas;
	private int numeroGeralDeExames;
	
	private int numeroGeralDeUrgencias;
	private int numeroGeralDeInternacoes;
	
	private int numeroGeral;
	private int numeroGeralDeProcedimentos;

	public ResumoConsumos(List<Prestador> prestadores, Date competencia) throws Exception {
		super();
		numeroTotalDePrestadoresComConsumo = 0;
		consumoGeralDeConsultas = 0;
		consumoGeralDeExames = 0;
		consumoGeralDeInternacoesEletivas = 0;
		consumoGeralDeUrgencias = 0;
		consumoGeral = 0;
		this.competencia = competencia;
		this.prestadores = prestadores;
		this.consumos = new ArrayList<ConsumoInterface>();
		computarResumo();
	}

	/**
	 * Calcula os somatórios gerais e completa a coleção de consumos para esta competência.
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private void computarResumo() throws Exception {
		
		
		if(this.getCompetencia() == null)
			return;
		if(this.getPrestadores() == null)
			return;
		
		for (Prestador prestador : this.getPrestadores()){
			
			ConsumoInterface consumoEncontrado = prestador.getConsumoPorCompetencia(this.getCompetencia());
			
			if(consumoEncontrado != null){
				
				int consumoConsultas = Math.round(consumoEncontrado.getSomatorioConsultasConfirmadas().floatValue());
				int consumoExames = Math.round(consumoEncontrado.getSomatorioExamesConfirmados().floatValue());
				int consumoUrgencias = Math.round(consumoEncontrado.getSomatorioUrgencias().floatValue());
				int consumoInternacoes = Math.round(consumoEncontrado.getSomatorioInternacoes().floatValue());
				
				if(consumoConsultas == 0 && consumoExames == 0 && consumoInternacoes == 0 && consumoUrgencias == 0)
					continue;
				
				consumoEncontrado.setDescricao(prestador.getPessoaJuridica().getFantasia());
				
				this.consumos.add(consumoEncontrado);
				this.consumoGeralDeConsultas += consumoEncontrado.getSomatorioConsultasConfirmadas().floatValue();
				this.consumoGeralDeExames += consumoEncontrado.getSomatorioExamesConfirmados().floatValue();
				this.consumoGeralDeInternacoesEletivas += consumoEncontrado.getSomatorioInternacoes().floatValue();
				this.consumoGeralDeUrgencias += consumoEncontrado.getSomatorioUrgencias().floatValue();
				
				this.numeroGeralDeConsultas += consumoEncontrado.getNumeroConsultasConfirmadas();
				this.numeroGeralDeExames += consumoEncontrado.getNumeroExamesConfirmados();
				this.numeroGeralDeInternacoes += consumoEncontrado.getNumeroInternacoes();
				this.numeroGeralDeUrgencias += consumoEncontrado.getNumeroUrgencias();
				
				this.numeroGeralDeProcedimentos += consumoEncontrado.getNumeroProcedimentosExames();
				numeroTotalDePrestadoresComConsumo += 1;
			}
		}
		this.consumoGeral = this.consumoGeralDeConsultas + this.consumoGeralDeExames + this.consumoGeralDeInternacoesEletivas+
							this.consumoGeralDeUrgencias;
		this.numeroGeral = this.numeroGeralDeExames + this.numeroGeralDeConsultas + this.numeroGeralDeInternacoes
							+ this.numeroGeralDeUrgencias;
//		Collections.sort(consumos);
		//Ordena do maior consumo para o menor. SOLICITAÇÃO DO ADALTON.
		Collections.sort(consumos, new Comparator(){
			public int compare(Object a, Object b){
				ConsumoInterface obja = (ConsumoInterface)a;
				ConsumoInterface objb = (ConsumoInterface)b;
				return objb.getConsumoGeral().compareTo(obja.getConsumoGeral());
			}
		});
		
	}

	public float getPorcentagemConsultas(){
		if(numeroGeralDeConsultas > 0 && numeroGeralDeConsultas > 0)
			return((float)numeroGeralDeConsultas/(float)numeroGeral);
		return 0;
	}

	public float getPorcentagemUrgencias(){
		if(numeroGeralDeUrgencias > 0 && numeroGeralDeUrgencias > 0)
			return((float)numeroGeralDeUrgencias/(float)numeroGeral);
		return 0;
	}

	public float getPorcentagemInternacoes(){
		if(numeroGeralDeInternacoes > 0 && numeroGeralDeInternacoes > 0)
			return((float)numeroGeralDeInternacoes/(float)numeroGeral);
		return 0;
	}

	public float getPorcentagemExames(){
		if(numeroGeralDeExames > 0 && numeroGeralDeExames > 0)
			return((float)numeroGeralDeExames/(float)numeroGeral);
		return 0;
	}

	public String getMediaNumeroProcedimentosPorGuia(){
		if(numeroGeralDeProcedimentos > 0 && numeroGeralDeExames > 0)
			return Utils.format(((float)numeroGeralDeProcedimentos/(float)numeroGeralDeExames));
		return "0";
	}
	
	public Date getCompetencia() {
		return competencia;
	}

	public float getConsumoGeral() {
		return consumoGeral;
	}

	public float getConsumoGeralDeConsultas() {
		return consumoGeralDeConsultas;
	}

	public float getConsumoGeralDeExames() {
		return consumoGeralDeExames;
	}

	public List<ConsumoInterface> getConsumos() {
		return consumos;
	}

	public int getNumeroGeral() {
		return numeroGeral;
	}

	public int getNumeroGeralDeConsultas() {
		return numeroGeralDeConsultas;
	}

	public int getNumeroGeralDeExames() {
		return numeroGeralDeExames;
	}

	public int getNumeroTotalDePrestadoresComConsumo() {
		return numeroTotalDePrestadoresComConsumo;
	}

	public List<Prestador> getPrestadores() {
		return prestadores;
	}

	public int getNumeroGeralDeProcedimentos() {
		return numeroGeralDeProcedimentos;
	}

	public float getConsumoGeralDeInternacoes() {
		return consumoGeralDeInternacoesEletivas;
	}

	public void setConsumoGeralDeInternacoes(float consumoGeralDeInternacoes) {
		this.consumoGeralDeInternacoesEletivas = consumoGeralDeInternacoes;
	}

	public int getNumeroGeralDeInternacoes() {
		return numeroGeralDeInternacoes;
	}

	public void setNumeroGeralDeInternacoes(int numeroGeralDeInternacoes) {
		this.numeroGeralDeInternacoes = numeroGeralDeInternacoes;
	}

	public float getConsumoGeralDeUrgencias() {
		return consumoGeralDeUrgencias;
	}

	public void setConsumoGeralDeUrgencias(float consumoGeralDeUrgencias) {
		this.consumoGeralDeUrgencias = consumoGeralDeUrgencias;
	}

	public int getNumeroGeralDeUrgencias() {
		return numeroGeralDeUrgencias;
	}

	public void setNumeroGeralDeUrgencias(int numeroGeralDeUrgencias) {
		this.numeroGeralDeUrgencias = numeroGeralDeUrgencias;
	}
	
	
	
}