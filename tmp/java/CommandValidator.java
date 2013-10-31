package br.com.infowaypi.ecare.services.honorarios.validators;

import java.util.Collection;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacoteHonorario;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GeradorGuiaHonorarioInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoHonorario;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * Representa um Command de validações para a Geração de Honorários Individuais.
 * 
 * @author Eduardo
 */
public abstract class CommandValidator {

	private Profissional medico;
	private Integer grauDeParticipacao;
	private GeradorGuiaHonorarioInterface guiaOrigem;
	private AbstractSegurado segurado;
	private Prestador prestador;
	private Collection<ProcedimentoHonorario> procedimentosOutros;
	private Collection<ItemPacoteHonorario> itensPacoteHonorario;

	public abstract void execute() throws ValidateException;

	public Profissional getMedico() {
		return medico;
	}

	public void setMedico(Profissional medico) {
		this.medico = medico;
	}

	public Integer getGrauDeParticipacao() {
		return grauDeParticipacao;
	}

	public void setGrauDeParticipacao(Integer grauDeParticipacao) {
		this.grauDeParticipacao = grauDeParticipacao;
		
	}

	public GeradorGuiaHonorarioInterface getGuiaOrigem() {
		return guiaOrigem;
	}

	public void setGuiaOrigem(GeradorGuiaHonorarioInterface guiaOrigem) {
		this.guiaOrigem = guiaOrigem;
		
	}

	public AbstractSegurado getSegurado() {
		return segurado;
	}

	public void setSegurado(AbstractSegurado segurado) {
		this.segurado = segurado;
		
	}

	public Prestador getPrestador() {
		return prestador;
	}

	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
	}
		

	public Collection<ProcedimentoHonorario> getProcedimentosOutros() {
		return procedimentosOutros;
	}

	public void setProcedimentosOutros(Collection<ProcedimentoHonorario> procedimentosOutros) {
		this.procedimentosOutros = procedimentosOutros;
	}
	
	public boolean isGeraApenasProcedimentosOutros() {
		return this.getGrauDeParticipacao() == null && this.guiaOrigem.getProcedimentosQueVaoGerarHonorario().isEmpty();
	}
	
	public boolean possuiPacotesHonorario(){
		return this.getItensPacoteHonorario() != null && !this.getItensPacoteHonorario().isEmpty();
	}
	/**
	 * Verifica se a guia possui procedimentos com porte anestésico maior do que zero, ou seja, se ela possui ou não procedimentos
	 * aptos para gerar honorários. 
	 * @return
	 */
	public boolean temProcedimenosComPorteAnestesicoMaiorQueZero(){
		return this.getGuiaOrigem().getProcedimentosAptosAGerarHonorariosMedicos().isEmpty();
	}

	public Collection<ItemPacoteHonorario> getItensPacoteHonorario() {
		return itensPacoteHonorario;
	}

	public void setItensPacoteHonorario(
			Collection<ItemPacoteHonorario> itensPacoteHonorario) {
		this.itensPacoteHonorario = itensPacoteHonorario;
	}

}
