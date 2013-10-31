package br.com.infowaypi.ecare.relatorio.financeiro;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import com.lowagie.tools.concat_pdf;

import br.com.infowaypi.ecare.financeiro.Cobranca;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.segurados.TitularFinanceiroSR;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecare.services.Service;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.CollectionUtils;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author anderson
 * Classe responsavel por realizar a busca de cobrancas por um determinado periodo
 * [Data Inicial - Data Final]
 * */
public class RelatorioFluxoBoletosService extends Service{
	
	/**
	 * Metodo que busca as cobrancas em um dado periodo com situacao Pago(a)
	 * @param dataInicial
	 * @param dataFinal
	 * */
	public ResumoFluxoBoletos buscarCobrancas(String dataInicial, String dataFinal) throws Exception {
		
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
		
		criteria.add(Expression.between("dataPagamento", dataInicialPesquisa, dataFinalPesquisa));
		
		listaCobranca.addAll(criteria.list());
		//Agrupa um conjunto de cobrancas com chave a data de pagamento da cobranca
		Map<Date, Set<Cobranca>> mapaCobranca = CollectionUtils.groupBy(listaCobranca, "dataPagamento", Date.class);
				
		return new ResumoFluxoBoletos(mapaCobranca);
				
	}

}
