package br.com.infowaypi.ecarebc.atendimentos.validators.fechamento;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;

public class FechamentoInternacaoStrategy {

	private static final  List<FechamentoGuiaInternacaoValidator> internacaoValidators = new ArrayList<FechamentoGuiaInternacaoValidator>();
	
	static {
		internacaoValidators.add(new DataFechamentoGuiaParcialValidator());
		internacaoValidators.add(new FechamentoSituacaoSeguradoValidator());
		internacaoValidators.add(new FechamentoAltaValidator());
		internacaoValidators.add(new FechamentoParcialInternacaoValidator());
		//TODO deletar a classe depois
		//internacaoValidators.add(new FechamentoDataCirurgiaValidator());
	}
	
	public static void validaFechamento(GuiaInternacao guia, Boolean parcial,
			Date dataFinal, UsuarioInterface usuario) throws ValidateException {
		
		for (FechamentoGuiaInternacaoValidator giValidator : internacaoValidators) {
			giValidator.execute(guia, parcial, dataFinal, usuario);
		}
		
	}
}
