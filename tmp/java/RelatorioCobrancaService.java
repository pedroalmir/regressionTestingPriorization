package br.com.infowaypi.ecare.relatorio.financeiro;

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
 * @author anderson
 * Classe que implementa o metodo que sera chamado para gerar o
 *         relatorio de cobrancas para aplicação SR
 */
public class RelatorioCobrancaService extends Service {
	/**
	 * @param dataInicial
	 * @param dataFinal
	 * @param numeroCartao
	 * @param tipoSituacao
	 * @param tipoData
	 * Metodo que retorna todas as cobrancas a partir dos parametro
	 * passados
	 * @throws ValidateException
	 */
	public ResumoCobranca buscarCobrancas(Date dataInicial, Date dataFinal,
			String numeroCartao, String tipoSituacao, Integer tipoData)
			throws Exception {

		Criteria criteria = HibernateUtil.currentSession().createCriteria(Cobranca.class);

		Calendar dataInicalCalendar = Calendar.getInstance();
		dataInicalCalendar.setTime(dataInicial);

		Calendar dataFinalCalendar = Calendar.getInstance();
		dataFinalCalendar.setTime(dataFinal);

		if ((Utils.diferencaEmDias(dataInicalCalendar, dataFinalCalendar)) < 0)
			throw new ValidateException(MensagemErroEnum.DATA_INICIAL_MAIOR_Q_A_FINAL.getMessage());

		if (numeroCartao != null) {
			List<Segurado> titulares = BuscarSegurados.buscar("", numeroCartao, TitularFinanceiroSR.class).getSegurados();
			if (!titulares.isEmpty()) {
				criteria.add(Expression.in("titular", titulares));
			}
		}

		if (!tipoSituacao.equals(ResumoCobranca.TODOS_BOLETOS)) {
			criteria.add(Expression.eq("situacao.descricao", tipoSituacao));
		}
		
		if (tipoData.equals(ResumoCobranca.DATA_DE_VENCIMENTO)) {
			criteria.add(Expression.between("dataVencimento", dataInicial, dataFinal));
		} else if (tipoData.equals(ResumoCobranca.DATA_DE_PAGAMENTO)) {
			criteria.add(Expression.between("dataPagamento", dataInicial, dataFinal));
		}

		List<Cobranca> listaCobranca = criteria.list();
		Utils.sort(listaCobranca, "dataVencimento", "titular.pessoaFisica.nome");
		
		return new ResumoCobranca(listaCobranca);

	}

	/**
	 * @param dataInicial
	 * @param dataFinal
	 * @param tipoSituacao
	 * @param tipoData
	 * Metodo que busca e retorna uma cobranca a partir de
	 * paramentros passados
	 */
	public ResumoCobranca buscarCobrancas(Date dataInicial, Date dataFinal,
			String tipoSituacao, Integer tipoData) throws Exception {

		String numeroCartao = null;

		Calendar dataInicalCalendar = Calendar.getInstance();
		dataInicalCalendar.setTime(dataInicial);

		Calendar dataFinalCalendar = Calendar.getInstance();
		dataFinalCalendar.setTime(dataFinal);

		if ((Utils.diferencaEmDias(dataInicalCalendar, dataFinalCalendar)) < 0)
			throw new ValidateException(MensagemErroEnum.DATA_INICIAL_MAIOR_Q_A_FINAL.getMessage());

		return buscarCobrancas(dataInicial, dataFinal, numeroCartao, tipoSituacao, tipoData);
	}	
}
