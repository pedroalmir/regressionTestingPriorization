package br.com.infowaypi.ecare.services.honorarios.validators;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacoteHonorario;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GeradorGuiaHonorarioInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoHonorario;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.msr.exceptions.ValidateException;

public class RegistrarHonorarioIndividualCommandRunner {

	private Set<CommandValidator> validacoes = new HashSet<CommandValidator>();
	
	public RegistrarHonorarioIndividualCommandRunner(CommandValidator... validacoes) {
		for (CommandValidator commandValidator : validacoes) {
			this.validacoes.add(commandValidator);
		}
	}

	public RegistrarHonorarioIndividualCommandRunner setValores(Integer grauDeParticipacao, 
				GeradorGuiaHonorarioInterface guiaOrigem, Profissional medico, Prestador prestador,
				AbstractSegurado segurado, Collection<ProcedimentoHonorario> procedimentosOutros, Collection<ItemPacoteHonorario> itensPacote) {
		
		for (CommandValidator command : this.validacoes) {
			command.setGrauDeParticipacao(grauDeParticipacao);
			command.setGuiaOrigem(guiaOrigem);
			command.setMedico(medico);
			command.setPrestador(prestador);
			command.setSegurado(segurado);
			command.setProcedimentosOutros(procedimentosOutros);
			command.setItensPacoteHonorario(itensPacote);
		}
		return this;
		
	}
	
	public void execute() throws ValidateException {
		for (CommandValidator command : this.validacoes) {
			command.execute();
		}
	}
}
