package br.com.infowaypi.ecare.services.financeiro.faturamento;

import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecare.constantes.Constantes;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.Faturamento;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.NotEquals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.pessoa.PessoaJuridica;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

public class FaturamentoProcedimento {

	@SuppressWarnings("unchecked")
	public Faturamento buscarProcedimentos(String competencia, Boolean exibirProcedimentos, Prestador prestador) throws ValidateException{
		
		SearchAgent sa = new SearchAgent();
		Date competenciaFormatada = Utils.gerarCompetencia(competencia);

		sa.addParameter(new Equals("competencia", competenciaFormatada));
		sa.addParameter(new NotEquals("status", Constantes.FATURAMENTO_CANCELADO));

		if(prestador != null){
			sa.addParameter(new Equals("prestador",prestador));
			Faturamento faturamento = sa.uniqueResult(Faturamento.class);
			Assert.isNotNull(faturamento, MensagemErroEnum.FATURAMENTO_NAO_GERADO_PARA_O_PRESTADOR.getMessage());
//			faturamento.setTipoDoProcedimento(tipoDeProcedimento);
			faturamento.getResumoProcedimentos();
			return faturamento;
			
		}else{			
			List<Faturamento> faturamentos = sa.list(Faturamento.class);
			Assert.isNotEmpty(faturamentos, MensagemErroEnum.FATURAMENTOS_NAO_GERADOS.getMessage());

			Faturamento faturamentoTotal = new Faturamento();
			faturamentoTotal.setCompetencia(competenciaFormatada);
			
			for (Faturamento faturamento : faturamentos) {
				faturamentoTotal.getGuias().addAll(faturamento.getGuias());
				faturamentoTotal.getAlteracoesFaturamento().addAll(faturamento.getAlteracoesFaturamento());
				faturamentoTotal.setValorBruto(faturamentoTotal.getValorBruto().add(faturamento.getValorBruto()));
			}
			
			Prestador prestadorTotal = new Prestador();
			PessoaJuridica pj = new PessoaJuridica();
			pj.setFantasia("TODOS OS PRESTADORES");
			prestadorTotal.setPessoaJuridica(pj);
			
//			faturamentoTotal.setTipoDoProcedimento(tipoDeProcedimento);
			faturamentoTotal.setPrestador(prestadorTotal);
			faturamentoTotal.getResumoProcedimentos();
			
			return faturamentoTotal;
			
		}
	}
}
