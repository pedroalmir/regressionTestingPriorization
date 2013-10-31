package br.com.infowaypi.ecare.financeiro.faturamento;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecare.resumos.ResumoFaturamentoNovo;
import br.com.infowaypi.ecare.resumos.ResumoFaturamentosSR;
import br.com.infowaypi.ecare.services.recurso.ItemRecursoGlosa;
import br.com.infowaypi.ecare.services.recurso.GuiaRecursoGlosa;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.LoteDeGuias;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AbstractFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.Faturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.FaturamentoPassivo;
import br.com.infowaypi.ecarebc.financeiro.faturamento.GuiaFaturavel;
import br.com.infowaypi.ecarebc.financeiro.faturamento.OcorrenciaFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ResumoFaturamentos;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author Marcus bOolean
 * @changes Patrícia 
 */
@SuppressWarnings("unchecked")
public class RegistrarPagamentoService {

	public ResumoFaturamentos buscarFaturamento(Date competencia, Prestador prestador, Integer tipoFaturamento) throws ValidateException {
		Criteria criteria = HibernateUtil.currentSession().createCriteria(getKlass(tipoFaturamento));
		
		Assert.isNotNull(competencia, "Caro usuário, preencha o campo Competência.");
		
		criteria.add(Expression.eq("prestador", prestador));
		criteria.add(Expression.eq("competencia", competencia));
		criteria.add(Expression.ne("status", Constantes.FATURAMENTO_CANCELADO));
		List<AbstractFaturamento> faturamentos = criteria.list();
		
		ResumoFaturamentos resumo;
		
		if (Utils.compararCompetencia(competencia, Utils.gerarCompetencia("07/2010")) >= 0) {
			resumo = new ResumoFaturamentoNovo(faturamentos, prestador, ResumoFaturamentos.RESUMO_CATEGORIA, competencia, false);
		} else {
			resumo = new ResumoFaturamentosSR(faturamentos, prestador, ResumoFaturamentos.RESUMO_CATEGORIA, competencia, false);
		}
		
		Assert.isNotEmpty(faturamentos, "Não foi encontrado nenhum faturamento para o prestador \""+prestador.getNome()+"\". \n O Registro de pagamento pode ser feito apenas para faturamentos \"FECHADOS\".");
		
		for (AbstractFaturamento faturamento : faturamentos) {
			if (faturamento.getStatus()== Constantes.FATURAMENTO_PAGO){
				throw new RuntimeException("Esse faturamento já foi pago.");
			}
			if (faturamento.getStatus()== Constantes.FATURAMENTO_ABERTO){
				throw new RuntimeException("Esse faturamento deve ser fechado antes de ser pago.");
			}
			if (tipoFaturamento.equals(Constantes.TIPO_FATURAMENTO_CAPITACAO) && !((Faturamento)faturamento).isCapitacao()){
				throw new RuntimeException("Não existe um faturamento de Capitação para para o prestador \""+prestador.getNome()+"\" nessa competência.");
			}
			tocarObjetos(faturamento);
		}
		
		return resumo;
	}

	public ResumoFaturamentos efetuarPagamento(AcaoPagamentoFaturamentoEnum acaoPagamento, 
			Date dataPagamento, String numeroEmpenho, Collection<OcorrenciaFaturamento> ocorrencias, 
			ResumoFaturamentos resumo, UsuarioInterface usuario) {
		
		for (AbstractFaturamento faturamento : resumo.getFaturamentos()) {

			if (acaoPagamento == AcaoPagamentoFaturamentoEnum.INFORMAR_PENDENCIA) {
				Assert.isNull(dataPagamento, "Caso não efetue o pagamento do faturamento, não é permitido informar a data de pagamento.");
				Assert.isTrue(Utils.isStringVazia(numeroEmpenho), "Caso não efetue o pagamento do faturamento, não é permitido informar o número de empenho."); 
				Assert.isNotEmpty(ocorrencias, "Informe o motivo do não-pagamento do faturamento.");
				faturamento.setStatus(Constantes.FATURAMENTO_EM_DEBITO);
			}

			if (acaoPagamento == AcaoPagamentoFaturamentoEnum.REGISTRAR_PAGAMENTO) {
				if (dataPagamento == null) {
					throw new RuntimeException("Caso efetue o pagamento do faturamento, preencha o campo DATA DE PAGAMENTO.");
				}

				if (Utils.isStringVazia(numeroEmpenho)) {
					throw new RuntimeException("Caso efetue o pagamento do faturamento, é necessário informar o NÚMERO DE EMPENHO.");
				}

				if(dataPagamento.before(faturamento.getDataGeracao())) {
					throw new RuntimeException("Caro usuário, a data de pagamento não pode ser anterior à data de geração do faturamento.");
				}
				
				for (GuiaFaturavel guia : faturamento.getGuiasFaturaveis()) {
					guia.mudarSituacao(usuario, SituacaoEnum.PAGO.descricao(), "Registro de pagamento", new Date());
				}
				
				faturamento.setNumeroEmpenho(numeroEmpenho);
				faturamento.setDataPagamento(dataPagamento);
				faturamento.setStatus(Constantes.FATURAMENTO_PAGO);
			}

			for (OcorrenciaFaturamento ocorrenciaFaturamento : ocorrencias) {
				ocorrenciaFaturamento.setDataOcorrencia(new Date());
				ocorrenciaFaturamento.setFaturamento(faturamento);
				ocorrenciaFaturamento.setUsuario(usuario);
				resumo.getOcorrencias().add(ocorrenciaFaturamento);
			}

			faturamento.getOcorrencias().addAll(ocorrencias);
		}
		resumo.setStatus(SituacaoEnum.PAGO.descricao());
		resumo.setDataPagamento(dataPagamento);
		
		return resumo;
	}
	
	public ResumoFaturamentos salvarFaturamento(ResumoFaturamentos resumo) throws Exception {
		for (AbstractFaturamento faturamento : resumo.getFaturamentos()) {
			ImplDAO.save(faturamento);
		}
		
		return resumo;
	}
	
	public void finalizar(LoteDeGuias lote){}

	private Class getKlass(Integer tipoFaturamento) {
		if(tipoFaturamento.equals(Constantes.TIPO_FATURAMENTO_NORMAL) || tipoFaturamento.equals(Constantes.TIPO_FATURAMENTO_CAPITACAO))
			return Faturamento.class;
		else {
			return FaturamentoPassivo.class;
		}
	}
	
	private void tocarObjetos(AbstractFaturamento faturamento) {
		for (GuiaFaturavel guia : faturamento.getGuiasFaturaveis()) {
			guia.getSituacoes().size();
		}
	}
	
}

