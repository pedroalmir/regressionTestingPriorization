package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GuiaHonorarioMedico;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;

/**
 * @author Marcus Vinicius
 *
 */
public class GuiaHonorarioProfissionalCredenciadoValidator{
	
	public static void execute(GuiaHonorarioMedico guia) throws Exception{
		validate(guia.getProfissional(), guia.getPrestador(), true);
	}
	
	public static void validate(Profissional profissional, Prestador prestador, boolean validaCorpoClinico){ 
		
		if(!profissional.isCredenciado()) {
			throw new RuntimeException(MensagemErroEnum.PROFISSIONAl_NAO_CREDENCIADO.getMessage(profissional.getPessoaFisica().getNome()));
		}

		if(validaCorpoClinico && !prestador.getProfissionais().contains(profissional)) {
			throw new RuntimeException(MensagemErroEnum.PROFISSIONAl_NAO_ASSOCIADO_AO_PRESTADOR.getMessage(profissional.getPessoaFisica().getNome(), prestador.getPessoaJuridica().getFantasia()));
		}
	}
}
