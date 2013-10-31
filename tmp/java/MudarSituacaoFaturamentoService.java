package br.com.infowaypi.ecare.financeiro.faturamento;

import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecare.resumos.ResumoFaturamentoNovo;
import br.com.infowaypi.ecare.resumos.ResumoFaturamentosSR;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AbstractFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.Faturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.FaturamentoPassivo;
import br.com.infowaypi.ecarebc.financeiro.faturamento.GuiaFaturavel;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ResumoFaturamentos;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Esse fluxo recupera um faturamento PAGO, e o altera a situação para a anterior (FECHADO).<br>
 * As guias tambem tem sua situação alterada de PAGA para FATURADA
 * @author Emanuel
 *
 */
@SuppressWarnings("unchecked")
public class MudarSituacaoFaturamentoService {

	public ResumoFaturamentos buscarFaturamento(Date competencia, Prestador prestador,Integer tipoFaturamento) throws ValidateException{
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("prestador", prestador));
		sa.addParameter(new Equals("competencia", competencia));
		sa.addParameter(new Equals("status", Constantes.FATURAMENTO_PAGO));
		List<AbstractFaturamento> faturamentos = sa.list(getClasseFaturamento(tipoFaturamento));
		
		Assert.isNotEmpty(faturamentos, "Não foi encontrado nenhum faturamento para o prestador \""+prestador.getNome()+"\". \n A mudança de situação pode ser feito apenas para faturamentos \"PAGOS\".");
		
		ResumoFaturamentos resumo;
		if (Utils.compararCompetencia(competencia, Utils.gerarCompetencia("07/2010")) >= 0) {
			resumo = new ResumoFaturamentoNovo(faturamentos, prestador, ResumoFaturamentos.RESUMO_CATEGORIA, competencia, false);
		} else {
			resumo = new ResumoFaturamentosSR(faturamentos, prestador, ResumoFaturamentos.RESUMO_CATEGORIA, competencia, false);
		}
		
		resumo.setDataPagamento(faturamentos.get(faturamentos.size()-1).getDataPagamento());
		
		for (AbstractFaturamento faturamento : faturamentos) {
			tocarFaturamento(faturamento);
		}
		
		return resumo;
	}

	private Class getClasseFaturamento(Integer tipoFaturamento) {
		if(tipoFaturamento.equals(Constantes.TIPO_FATURAMENTO_NORMAL) || tipoFaturamento.equals(Constantes.TIPO_FATURAMENTO_CAPITACAO))
			return Faturamento.class;
		else {
			return FaturamentoPassivo.class;
		}
	}

	private void tocarFaturamento(AbstractFaturamento faturamento) {
		for (GuiaFaturavel guia : faturamento.getGuiasFaturaveis()) {
			guia.getSituacoes().size();
		}
	}
	
	public ResumoFaturamentos mudarSituacaoFaturamento(String motivo, ResumoFaturamentos resumo, UsuarioInterface usuario) throws Exception{
		for (AbstractFaturamento faturamento : resumo.getFaturamentos()) {
			voltarSituacao(faturamento, usuario, motivo);
		}
		resumo.setMotivo(motivo);
		resumo.setStatus(SituacaoEnum.FECHADO.descricao());
		return resumo;
	}
	
	private void voltarSituacao(AbstractFaturamento faturamento, UsuarioInterface usuario, String motivo) throws Exception{
		faturamento.setStatus(Constantes.FATURAMENTO_FECHADO);
		faturamento.setNumeroEmpenho(null);
		faturamento.setDataPagamento(null);
		
		for (GuiaFaturavel guia : faturamento.getGuiasFaturaveis()) {
			SituacaoInterface situacaoAnterior = guia.getSituacaoAnterior(guia.getSituacao());
			guia.mudarSituacao(usuario, situacaoAnterior.getDescricao(), motivo, new Date());
		}
		
		ImplDAO.save(faturamento);
	}
	
}
