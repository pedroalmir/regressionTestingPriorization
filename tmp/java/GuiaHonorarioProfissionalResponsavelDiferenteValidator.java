package br.com.infowaypi.ecare.services.honorarios.validators;

import java.util.Set;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.enums.GrauDeParticipacaoEnum;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.LikeFull;
import br.com.infowaypi.msr.exceptions.ValidateException;

public class GuiaHonorarioProfissionalResponsavelDiferenteValidator extends CommandValidator {

	@Override
	public void execute() throws ValidateException {
		boolean isGrauResponsavel = this.getGrauDeParticipacao() == GrauDeParticipacaoEnum.RESPONSAVEL.getCodigo();
		boolean isPrestadorMedico = this.getPrestador().isGeraHonorarioPessoaFisica();

		if (isGrauResponsavel && isPrestadorMedico){
			Set<ProcedimentoInterface> procedimentosQueVaoGerarHonorario = this.getGuiaOrigem().getProcedimentosQueVaoGerarHonorario();

			for (ProcedimentoInterface procedimento : procedimentosQueVaoGerarHonorario) {
				Profissional responsavel = procedimento.getProfissionalResponsavel();

				if (responsavel != null ){
					if (!responsavel.equals(this.getMedico())) {
						throw new RuntimeException(MensagemErroEnum.NAO_GERAR_HONORARIO_INDIVIDUAL_PARA_PRESTADOR_PESSOA_FISICA_DIFERENTE_DO_INFORMADO_NO_PROCEDIMENTO.getMessage(procedimento.getProcedimentoDaTabelaCBHPM().getCodigoEDescricao()));
					}
				}else {
					Profissional medicoDoPrestador = this.getMedicoDoPrestadorPessoaFisicaPeloCPF(this.getPrestador());
					boolean encontrouProssionalPeloCPF = medicoDoPrestador != null;
					if (!encontrouProssionalPeloCPF) {
						medicoDoPrestador = this.getMedicoDoPrestadorPessoaFisicaPeloNome(this.getPrestador());
						throw new RuntimeException(MensagemErroEnum.PRESTADOR_PESSOA_FISICA_NAO_ENCONTRADO_PARA_O_PROFISSIONAL_INFORMADO.getMessage());
					} else if (!this.getMedico().equals(medicoDoPrestador)){
						throw new RuntimeException(MensagemErroEnum.NAO_GERAR_HONORARIO_INDIVIDUAL_PARA_RESPONSAVEL_DIFERENTE_PROFISSIONAL_DO_PRESTADOR_MEDICO.getMessage(medicoDoPrestador.getPessoaFisica().getNome()));
					}
				}

			}
		}
	}

	private Profissional getMedicoDoPrestadorPessoaFisicaPeloCPF(Prestador prestador) {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new LikeFull("pessoaFisica.cpf", prestador.getPessoaJuridica().getCnpj()));

		Profissional resultado = sa.uniqueResult(Profissional.class);

		return resultado;
	}

	private Profissional getMedicoDoPrestadorPessoaFisicaPeloNome(Prestador prestador) {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("pessoaFisica.nome", prestador.getPessoaJuridica().getFantasia()));

		Profissional resultado = sa.uniqueResult(Profissional.class);

		return resultado;
	}
}
