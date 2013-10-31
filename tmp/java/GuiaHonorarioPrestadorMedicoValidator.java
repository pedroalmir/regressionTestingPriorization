package br.com.infowaypi.ecare.services.honorarios.validators;

import java.util.Set;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;

public class GuiaHonorarioPrestadorMedicoValidator extends CommandValidator {

	@Override
	public void execute() throws ValidateException { 
		if (this.getPrestador().isGeraHonorarioPessoaFisica()){
			
			if (!this.getGuiaOrigem().isProfissionalPodeRegistrarHonorarioIndividual(getMedicoDoPrestadorPessoaFisica(this.getPrestador()))) {
				throw new ValidateException(MensagemErroEnum.NAO_GERAR_HONORARIO_INDIVIDUAL_PARA_PRESTADOR_PESSOA_FISICA_DIFERENTE.getMessage(this.getPrestador().getPessoaJuridica().getFantasia(), this.getGuiaOrigem().getAutorizacao()));
			}
			
			String cpf, cnpj;
			boolean prestadorPessoaFisicaHabilitado;
			
			Set<ProcedimentoInterface> procedimentosQueVaoGerarHonorario = this.getGuiaOrigem().getProcedimentosQueVaoGerarHonorario();
			for (ProcedimentoInterface procedimento : procedimentosQueVaoGerarHonorario) {
				
				cpf = procedimento.getProfissionalResponsavel() != null ? procedimento.getProfissionalResponsavel().getPessoaFisica().getCpf(): null;
				cnpj = this.getPrestador().getPessoaJuridica().getCnpj();
				prestadorPessoaFisicaHabilitado = procedimento.getProfissionalResponsavel() == null ? true :(cpf != null && cpf.equals(cnpj));
				
				if (!prestadorPessoaFisicaHabilitado) {
					throw new ValidateException(MensagemErroEnum.NAO_GERAR_HONORARIO_INDIVIDUAL_PARA_PRESTADOR_PESSOA_FISICA_DIFERENTE_DO_INFORMADO_NO_PROCEDIMENTO.getMessage(procedimento.getProcedimentoDaTabelaCBHPM().getCodigoEDescricao()));
				}
			}
		}
	}

	private Profissional getMedicoDoPrestadorPessoaFisica(Prestador prestador) {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("pessoaFisica.cpf", prestador.getPessoaJuridica().getCnpj()));
		return sa.uniqueResult(Profissional.class);
	}
}
	

