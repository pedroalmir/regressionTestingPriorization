package br.com.infowaypi.ecare.relatorio.financeiro;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecare.financeiro.Cobranca;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.segurados.TitularFinanceiroSR;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecare.services.Service;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;


/**
 * Classe que implementa o metodo que sera chamado para gerar o relatorio de boletos para aplicação SR
 * */
public class RelatorioBoletoService extends Service{
	/**
	 * Retorna todos os boletos 
	 * @throws ValidateException 
	 * */
	public ResumoBoleto buscarCobrancas(String dataInicial, String dataFinal, String numeroCartao, String tipoSituacao, Integer tipoData) throws Exception {
	
		Criteria criteria= HibernateUtil.currentSession().createCriteria(Cobranca.class);
		
		List<Cobranca> listaCobranca = new ArrayList<Cobranca>();
		
		Date dataInicialPesquisa = Utils.parse(dataInicial);
		Date dataFinalPesquisa = Utils.parse(dataFinal); 
		
		Calendar dataInicalCalendar = Calendar.getInstance();
		Calendar dataFinalCalendar = Calendar.getInstance();

		dataInicalCalendar.setTime(dataInicialPesquisa);
		dataFinalCalendar.setTime(dataFinalPesquisa);

		if((Utils.diferencaEmDias(dataInicalCalendar, dataFinalCalendar)) < 0) 
			throw new ValidateException(MensagemErroEnum.DATA_INICIAL_MAIOR_Q_A_FINAL.getMessage());
		
		if(numeroCartao != null){
			
			List<Segurado> titulares = BuscarSegurados.buscar("", numeroCartao, TitularFinanceiroSR.class).getSegurados();

			if(!titulares.isEmpty()){
				criteria.add(Expression.in("titular", titulares));
			}

			if(!tipoSituacao.equals(ResumoBoleto.TODOS_BOLETOS)){
				if(tipoSituacao.equals(ResumoBoleto.BOLETOS_SITUACAO_ABERTO)) {
					criteria.add(Expression.eq("situacao.descricao", tipoSituacao));
				}else if(tipoSituacao.equals(ResumoBoleto.BOLETOS_SITUACAO_CANCELADO)) {
					criteria.add(Expression.eq("situacao.descricao", tipoSituacao));
				}else if(tipoSituacao.equals(ResumoBoleto.BOLETOS_SITUACAO_PAGO)) {
					criteria.add(Expression.eq("situacao.descricao", tipoSituacao));
				}
			}

			if(tipoData.equals(ResumoBoleto.DATA_DE_VENCIMENTO)) {
				criteria.add(Expression.between("dataVencimento", dataInicialPesquisa , dataFinalPesquisa));
			}else if(tipoData.equals(ResumoBoleto.DATA_DE_PAGAMENTO)) {
				criteria.add(Expression.between("dataPagamento", dataInicialPesquisa, dataFinalPesquisa));
			}
		
			listaCobranca.addAll(criteria.list());
		}
		else {
			if(!tipoSituacao.equals(ResumoBoleto.TODOS_BOLETOS)){
				if(tipoSituacao.equals(ResumoBoleto.BOLETOS_SITUACAO_ABERTO)) {
					criteria.add(Expression.eq("situacao.descricao", tipoSituacao));
				}else if(tipoSituacao.equals(ResumoBoleto.BOLETOS_SITUACAO_CANCELADO)) {
					criteria.add(Expression.eq("situacao.descricao", tipoSituacao));
				}else if(tipoSituacao.equals(ResumoBoleto.BOLETOS_SITUACAO_PAGO)) {
					criteria.add(Expression.eq("situacao.descricao", tipoSituacao));
				}
			}

			if(tipoData.equals(ResumoBoleto.DATA_DE_VENCIMENTO)) {
				criteria.add(Expression.between("dataVencimento", dataInicialPesquisa , dataFinalPesquisa));
			}else if(tipoData.equals(ResumoBoleto.DATA_DE_PAGAMENTO)) {
				criteria.add(Expression.between("dataPagamento", dataInicialPesquisa, dataFinalPesquisa));
			}

			listaCobranca.addAll(criteria.list());
		}
		Utils.sort(listaCobranca, "dataVencimento","titular.pessoaFisica.nome");
		
		return new ResumoBoleto(listaCobranca);

	}
	
	public ResumoBoleto buscarCobrancas(String dataInicial, String dataFinal, String tipoSituacao, Integer tipoData) throws Exception {
		
		String numeroCartao = null;
	
		Date dataInicialPesquisa = Utils.parse(dataInicial);
		Date dataFinalPesquisa = Utils.parse(dataFinal); 
		
		Calendar dataInicalCalendar = Calendar.getInstance();
		Calendar dataFinalCalendar = Calendar.getInstance();

		dataInicalCalendar.setTime(dataInicialPesquisa);
		dataFinalCalendar.setTime(dataFinalPesquisa);

		if((Utils.diferencaEmDias(dataInicalCalendar, dataFinalCalendar)) < 0) 
			throw new ValidateException(MensagemErroEnum.DATA_INICIAL_MAIOR_Q_A_FINAL.getMessage());
		
		return buscarCobrancas(dataInicial, dataFinal, numeroCartao ,tipoSituacao, tipoData);
	}
	
	
}
	