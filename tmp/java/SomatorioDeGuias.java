package br.com.infowaypi.ecarebc.atendimentos;

import br.com.infowaypi.msr.utils.Utils;

/**
 * Somatório mínimo de guias.
 * @author Mário Sérgio Coelho Marroquim
 */ 
public class SomatorioDeGuias {

	private String descricao;
	
	private int numeroTotalGeral;
	private float valorTotalGeral;
	
	private int numeroTotalDeGuias;
	private float valorTotalDeGuias;
	
	private int numeroTotalDeConsultas;
	private float valorTotalDeConsultas;
	
	private int numeroTotalDeConsultasOdonto;
	private float valorTotalDeConsultasOdonto;
	
	private int numeroTotalDeTratamentosOdonto;
	private float valorTotalDeTratamentosOdonto;
	
	private int numeroTotalDeCirurgiaOdonto;
	private float valorTotalDeCirurgiaOdonto;
	
	private int numeroTotalDeExames;
	private int numeroTotalDeProcedimentosExame;
	private float valorTotalDeExames;
	
	private int numeroTotalDeExamesExternos;
	private float valorTotalDeExamesExternos;
	
	private int numeroTotalDeInternacoes;
	private float valorTotalDeInternacoes;
	
	private int numeroTotalDeInternacoesUrgencia;
	private float valorTotalDeInternacoesUrgencia;	
	
	private int numeroTotalDeUrgencias;
	private float valorTotalDeUrgencias;
	
	private int numeroTotalDeGuiasHonorariosMedicos;
	private float valorTotalDeGuiasHonorariosMedicos;
	
	private int numeroTotalDeGuiasAcompanhamentoAnestesico;
	private float valorTotalDeGuiasAcompanhamentoAnestesico;
	
	private int numeroTotalDeGuiasRecursoGlosa;
	private float valorTotalDeGuiasRecursoGlosa;
	
	private int numeroTotalDeProcedimentos;
	private int numeroTotalDeProcedimentosExternos;
	
	private int quantidadeDoProcedimento = 0;
	private float valorTotalProcedimento = 0;
	
	public void totalizar(){
		this.numeroTotalDeGuias = this.numeroTotalDeConsultas + this.numeroTotalDeConsultasOdonto 
			+ this.numeroTotalDeExames + this.numeroTotalDeTratamentosOdonto + this.numeroTotalDeInternacoes 
			+ this.numeroTotalDeUrgencias + this.numeroTotalDeCirurgiaOdonto + this.numeroTotalDeGuiasHonorariosMedicos 
			+ this.numeroTotalDeGuiasAcompanhamentoAnestesico + this.numeroTotalDeGuiasRecursoGlosa;
			
		this.valorTotalDeGuias = this.valorTotalDeConsultas + this.valorTotalDeConsultasOdonto 
			+ this.valorTotalDeExames + this.valorTotalDeTratamentosOdonto + this.valorTotalDeInternacoes 
			+ this.valorTotalDeUrgencias + this.valorTotalDeCirurgiaOdonto + this.valorTotalDeGuiasHonorariosMedicos 
			+ this.valorTotalDeGuiasAcompanhamentoAnestesico + this.valorTotalDeGuiasRecursoGlosa;
	}

	public String getMediaNumeroProcedimentosPorGuia(){
		this.totalizar();
		if(numeroTotalDeProcedimentos > 0 && numeroTotalDeExames > 0)
			return Utils.format(((float)numeroTotalDeProcedimentos/(float)numeroTotalDeExames));
		return "0";
	}

	public float getPorcentagemParticipacaoNumerica(){
		this.totalizar();
		if(numeroTotalDeGuias > 0 && numeroTotalGeral > 0)
			return ((float)numeroTotalDeGuias/(float)numeroTotalGeral)*100;
		return 0;
	}
	
	public float getPorcentagemParticipacaoValor(){
		this.totalizar();
		if(valorTotalDeGuias > 0 && valorTotalGeral > 0)
			return (valorTotalDeGuias/valorTotalGeral)*100;
		return 0; 
	}

	public float getPorcentagemParticipacaoNumericaConsultas(){
		this.totalizar();
		if(numeroTotalDeConsultas > 0 && numeroTotalDeGuias > 0)
			return ((float)numeroTotalDeConsultas/(float)numeroTotalDeGuias)*100;
		return 0;
	}
	
	public float getPorcentagemParticipacaoValorConsultas(){
		this.totalizar();
		if(valorTotalDeConsultas > 0 && valorTotalDeGuias > 0)
			return (valorTotalDeConsultas/valorTotalDeGuias)*100;
		return 0; 
	}
	
	public float getPorcentagemParticipacaoNumericaConsultasOdonto(){
		this.totalizar();
		if(numeroTotalDeConsultasOdonto > 0 && numeroTotalDeGuias > 0)
			return ((float)numeroTotalDeConsultasOdonto/(float)numeroTotalDeGuias)*100;
		return 0;
	}
	
	public float getPorcentagemParticipacaoValorConsultasOdonto(){
		this.totalizar();
		if(valorTotalDeConsultasOdonto > 0 && valorTotalDeGuias > 0)
			return (valorTotalDeConsultasOdonto/valorTotalDeGuias)*100;
		return 0; 
	}
	
	public float getPorcentagemParticipacaoNumericaExames(){
		this.totalizar();
		if(numeroTotalDeExames > 0 && numeroTotalDeGuias > 0)
			return ((float)numeroTotalDeExames/(float)numeroTotalDeGuias)*100;
		return 0;
	}
	
	public float getPorcentagemParticipacaoValorExames(){
		this.totalizar();
		if(valorTotalDeExames > 0 && valorTotalDeGuias > 0)
			return (valorTotalDeExames/valorTotalDeGuias)*100;
		return 0; 
	}
	
	public float getPorcentagemParticipacaoNumericaTratamentosOdonto(){
		this.totalizar();
		if(numeroTotalDeTratamentosOdonto > 0 && numeroTotalDeGuias > 0)
			return ((float)numeroTotalDeTratamentosOdonto/(float)numeroTotalDeGuias)*100;
		return 0;
	}
	
	public float getPorcentagemParticipacaoValorTratamentosOdonto(){
		this.totalizar();
		if(valorTotalDeTratamentosOdonto > 0 && valorTotalDeGuias > 0)
			return (valorTotalDeTratamentosOdonto/valorTotalDeGuias)*100;
		return 0; 
	}
	
	public float getPorcentagemParticipacaoNumericaInternacoes(){
		this.totalizar();
		if(numeroTotalDeInternacoes > 0 && numeroTotalDeGuias > 0)
			return ((float)numeroTotalDeInternacoes/(float)numeroTotalDeGuias)*100;
		return 0;
	}
	
	public float getPorcentagemParticipacaoValorInternacoes(){
		this.totalizar();
		if(valorTotalDeInternacoes > 0 && valorTotalDeGuias > 0)
			return (valorTotalDeInternacoes/valorTotalDeGuias)*100;
		return 0; 
	}
	
	
	//urgencias
	public float getPorcentagemParticipacaoNumericaUrgencias(){
		this.totalizar();
		if(numeroTotalDeUrgencias > 0 && numeroTotalDeGuias > 0)
			return ((float)numeroTotalDeUrgencias/(float)numeroTotalDeGuias)*100;
		return 0;
	}
	
	public float getPorcentagemParticipacaoValorUrgencias(){
		this.totalizar();
		if(valorTotalDeUrgencias > 0 && valorTotalDeGuias > 0)
			return (valorTotalDeUrgencias/valorTotalDeGuias)*100;
		return 0; 
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getNumeroTotalDeConsultas() {
		return numeroTotalDeConsultas;
	}

	public void setNumeroTotalDeConsultas(int numeroTotalDeConsultas) {
		this.numeroTotalDeConsultas = numeroTotalDeConsultas;
	}

	public int getNumeroTotalDeExames() {
		return numeroTotalDeExames;
	}

	public void setNumeroTotalDeExames(int numeroTotalDeExames) {
		this.numeroTotalDeExames = numeroTotalDeExames;
	}

	public int getNumeroTotalDeGuias() {
		return numeroTotalDeGuias;
	}

	public void setNumeroTotalDeGuias(int numeroTotalDeGuias) {
		this.numeroTotalDeGuias = numeroTotalDeGuias;
	}

	public int getNumeroTotalGeral() {
		return numeroTotalGeral;
	}

	public void setNumeroTotalGeral(int numeroTotalGeral) {
		this.numeroTotalGeral = numeroTotalGeral;
	}

	public float getValorTotalDeConsultas() {
		return valorTotalDeConsultas;
	}

	public void setValorTotalDeConsultas(float valorTotalDeConsultas) {
		this.valorTotalDeConsultas = valorTotalDeConsultas;
	}

	public float getValorTotalDeExames() {
		return valorTotalDeExames;
	}

	public void setValorTotalDeExames(float valorTotalDeExames) {
		this.valorTotalDeExames = valorTotalDeExames;
	}

	public int getNumeroTotalDeExamesExternos() {
		return numeroTotalDeExamesExternos;
	}

	public void setNumeroTotalDeExamesExternos(int numeroTotalDeExamesExternos) {
		this.numeroTotalDeExamesExternos = numeroTotalDeExamesExternos;
	}

	public float getValorTotalDeExamesExternos() {
		return valorTotalDeExamesExternos;
	}

	public void setValorTotalDeExamesExternos(float valorTotalDeExamesExternos) {
		this.valorTotalDeExamesExternos = valorTotalDeExamesExternos;
	}

	public int getNumeroTotalDeExamesInternos() {
		return numeroTotalDeExames - numeroTotalDeExamesExternos;
	}
	
	public float getValorTotalDeExamesInternos() {
		return  valorTotalDeExames - valorTotalDeExamesExternos;
	}	
	
	public float getValorTotalDeGuias() {
		return valorTotalDeGuias;
	}

	public void setValorTotalDeGuias(float valorTotalDeGuias) {
		this.valorTotalDeGuias = valorTotalDeGuias;
	}

	public float getValorTotalGeral() {
		return valorTotalGeral;
	}

	public void setValorTotalGeral(float valorTotalGeral) {
		this.valorTotalGeral = valorTotalGeral;
	}

	public int getNumeroTotalDeProcedimentos() {
		return numeroTotalDeProcedimentos;
	}

	public void setNumeroTotalDeProcedimentos(int numeroTotalDeProcedimentos) {
		this.numeroTotalDeProcedimentos = numeroTotalDeProcedimentos;
	}

	public int getNumeroTotalDeProcedimentosExternos() {
		return numeroTotalDeProcedimentosExternos;
	}

	public void setNumeroTotalDeProcedimentosExternos(
			int numeroTotalDeProcedimentosExternos) {
		this.numeroTotalDeProcedimentosExternos = numeroTotalDeProcedimentosExternos;
	}
	
	public int getNumeroTotalDeProcedimentosInternos() {
		return numeroTotalDeProcedimentos - numeroTotalDeProcedimentosExternos;
	}	

	public int getNumeroTotalDeInternacoes() {
		return numeroTotalDeInternacoes;
	}

	public void setNumeroTotalDeInternacoes(int numeroTotalDeInternacoes) {
		this.numeroTotalDeInternacoes = numeroTotalDeInternacoes;
	}

	public float getValorTotalDeInternacoes() {
		return valorTotalDeInternacoes;
	}

	public void setValorTotalDeInternacoes(float valorTotalDeInternacoes) {
		this.valorTotalDeInternacoes = valorTotalDeInternacoes;
	}

	public int getNumeroTotalDeInternacoesUrgencia() {
		return numeroTotalDeInternacoesUrgencia;
	}

	public void setNumeroTotalDeInternacoesUrgencia(
			int numeroTotalDeInternacoesUrgencia) {
		this.numeroTotalDeInternacoesUrgencia = numeroTotalDeInternacoesUrgencia;
	}
	
	public float getValorTotalDeInternacoesUrgencia() {
		return valorTotalDeInternacoesUrgencia;
	}

	public void setValorTotalDeInternacoesUrgencia(
			float valorTotalDeInternacoesUrgencia) {
		this.valorTotalDeInternacoesUrgencia = valorTotalDeInternacoesUrgencia;
	}

	public int getNumeroTotalDeInternacoesEletiva() {
		return numeroTotalDeInternacoes - numeroTotalDeInternacoesUrgencia;
	}
	
	public float getValorTotalDeInternacoesEletiva() {
		return valorTotalDeInternacoes - valorTotalDeInternacoesUrgencia;
	}
	
	public int getNumeroTotalDeUrgencias() {
		return numeroTotalDeUrgencias;
	}

	public void setNumeroTotalDeUrgencias(int numeroTotalDeUrgencias) {
		this.numeroTotalDeUrgencias = numeroTotalDeUrgencias;
	}

	public float getValorTotalDeUrgencias() {
		return valorTotalDeUrgencias;
	}

	public void setValorTotalDeUrgencias(float valorTotalDeUrgencias) {
		this.valorTotalDeUrgencias = valorTotalDeUrgencias;
	}

	public int getNumeroTotalDeConsultasOdonto() {
		return numeroTotalDeConsultasOdonto;
	}

	public void setNumeroTotalDeConsultasOdonto(int numeroTotalDeConsultasOdonto) {
		this.numeroTotalDeConsultasOdonto = numeroTotalDeConsultasOdonto;
	}

	public int getNumeroTotalDeTratamentosOdonto() {
		return numeroTotalDeTratamentosOdonto;
	}

	public void setNumeroTotalDeTratamentosOdonto(int numeroTotalDeTratamentosOdonto) {
		this.numeroTotalDeTratamentosOdonto = numeroTotalDeTratamentosOdonto;
	}

	public float getValorTotalDeConsultasOdonto() {
		return valorTotalDeConsultasOdonto;
	}

	public void setValorTotalDeConsultasOdonto(float valorTotalDeConsultasOdonto) {
		this.valorTotalDeConsultasOdonto = valorTotalDeConsultasOdonto;
	}

	public float getValorTotalDeTratamentosOdonto() {
		return valorTotalDeTratamentosOdonto;
	}

	public void setValorTotalDeTratamentosOdonto(float valorTotalDeTratamentosOdonto) {
		this.valorTotalDeTratamentosOdonto = valorTotalDeTratamentosOdonto;
	}
	
	public int getNumeroTotalDeCirurgiaOdonto() {
		return numeroTotalDeCirurgiaOdonto;
	}

	public void setNumeroTotalDeCirurgiaOdonto(int numeroTotalDeCirurgiaOdonto) {
		this.numeroTotalDeCirurgiaOdonto = numeroTotalDeCirurgiaOdonto;
	}

	public float getValorTotalDeCirurgiaOdonto() {
		return valorTotalDeCirurgiaOdonto;
	}

	public void setValorTotalDeCirurgiaOdonto(float valorTotalDeCirurgiaOdonto) {
		this.valorTotalDeCirurgiaOdonto = valorTotalDeCirurgiaOdonto;
	}

	public int getNumeroTotalDeGuiasHonorariosMedicos() {
		return numeroTotalDeGuiasHonorariosMedicos;
	}

	public void setNumeroTotalDeGuiasHonorariosMedicos(int numeroTotalDeGuiasHonorariosMedicos) {
		this.numeroTotalDeGuiasHonorariosMedicos = numeroTotalDeGuiasHonorariosMedicos;
	}

	public float getValorTotalDeGuiasHonorariosMedicos() {
		return valorTotalDeGuiasHonorariosMedicos;
	}

	public void setValorTotalDeGuiasHonorariosMedicos(float valorTotalDeGuiasHonorariosMedicos) {
		this.valorTotalDeGuiasHonorariosMedicos = valorTotalDeGuiasHonorariosMedicos;
	}

	public int getNumeroTotalDeProcedimentosExame() {
		return numeroTotalDeProcedimentosExame;
	}

	public void setNumeroTotalDeProcedimentosExame(
			int numeroTotalDeProcedimentosExame) {
		this.numeroTotalDeProcedimentosExame = numeroTotalDeProcedimentosExame;
	}

	public int getQuantidadeDoProcedimento() {
		return quantidadeDoProcedimento;
	}

	public float getValorTotalProcedimento() {
		return valorTotalProcedimento;
	}
	
	public void addQuantidadeProcedimento(int quantidade) {
		this.quantidadeDoProcedimento += quantidade;
	}
	
	public void addValorTotalProcedimento(float valor) {
		this.valorTotalProcedimento += valor;
	}

	public int getNumeroTotalDeGuiasAcompanhamentoAnestesico() {
		return numeroTotalDeGuiasAcompanhamentoAnestesico;
	}

	public void setNumeroTotalDeGuiasAcompanhamentoAnestesico(
			int numeroTotalDeGuiasAcompanhamentoAnestesico) {
		this.numeroTotalDeGuiasAcompanhamentoAnestesico = numeroTotalDeGuiasAcompanhamentoAnestesico;
	}

	public float getValorTotalDeGuiasAcompanhamentoAnestesico() {
		return valorTotalDeGuiasAcompanhamentoAnestesico;
	}

	public void setValorTotalDeGuiasAcompanhamentoAnestesico(
			float valorTotalDeGuiasAcompanhamentoAnestesico) {
		this.valorTotalDeGuiasAcompanhamentoAnestesico = valorTotalDeGuiasAcompanhamentoAnestesico;
	}

	public int getNumeroTotalDeGuiasRecursoGlosa() {
		return numeroTotalDeGuiasRecursoGlosa;
	}

	public void setNumeroTotalDeGuiasRecursoGlosa(int numeroTotalDeGuiasRecursoGlosa) {
		this.numeroTotalDeGuiasRecursoGlosa = numeroTotalDeGuiasRecursoGlosa;
	}

	public float getValorTotalDeGuiasRecursoGlosa() {
		return valorTotalDeGuiasRecursoGlosa;
	}

	public void setValorTotalDeGuiasRecursoGlosa(float valorTotalDeGuiasRecursoGlosa) {
		this.valorTotalDeGuiasRecursoGlosa = valorTotalDeGuiasRecursoGlosa;
	}
}