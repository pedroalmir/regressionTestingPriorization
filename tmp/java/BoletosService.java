package br.com.infowaypi.ecare.financeiro.boletos;

import java.util.Calendar;
import java.util.Date;

import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecare.financeiro.Cobranca;
import br.com.infowaypi.ecare.financeiro.ResumoCobrancas;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.segurados.TitularFinanceiroSR;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.conta.BoletoConfigurator;
import br.com.infowaypi.ecarebc.financeiro.conta.ContaInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;

/**
 * 
 * @author Diogo Vin�cius
 *
 */

public class BoletosService {

	public static final String PAGAMENTO_EFETUADO_DIRETO_NO_SAUDE_RECIFE = "Pagamento efetuado direto no Sa�de Recife";

	/**
	 * Busca os boletos do segurado recebendo como par�metro o segurado.
	 * M�todo usado pelo portal do beneficiari para reimpress�o de boleto.
	 * @param segurado
	 * @return
	 * @throws ValidateException
	 */
	public ResumoCobrancas buscarBoletos(Segurado segurado) throws ValidateException{
		TitularFinanceiroSR titular = (TitularFinanceiroSR) segurado;
		titular.getCobrancas().size();
		return buscaBoletos(titular);
	}
	
	/**
	 * Busca os boletos recebendo como parametro o CPF do Titular ou o numero do cart�o.
	 * @param cpfDoTitular
	 * @param numeroDoCartao
	 * @return
	 * @throws ValidateException
	 */
	public ResumoCobrancas buscarBoletos(String cpfDoTitular, String numeroDoCartao) throws ValidateException{
		if (!(Utils.isStringVazia(cpfDoTitular) ^ Utils.isStringVazia(numeroDoCartao))){
			throw new ValidateException("Preencha apenas o CPF ou o N�mero do Cart�o");
		}

		SearchAgent sa = new SearchAgent();
		TitularFinanceiroSR titular = null;

		if(!Utils.isStringVazia(cpfDoTitular)){
			sa.addParameter(new Equals("pessoaFisica.cpf", cpfDoTitular));
			titular = sa.uniqueResult(TitularFinanceiroSR.class);
			if(titular == null)
				throw new ValidateException("N�o foi encontrado Benefici�rio com CPF " + cpfDoTitular);
		} else if(!Utils.isStringVazia(numeroDoCartao)){
			sa.addParameter(new Equals("numeroDoCartao", numeroDoCartao));
			titular = sa.uniqueResult(TitularFinanceiroSR.class);
			if(titular == null){
				throw new ValidateException("N�o foi encontrado Benefici�rio com N�mero de Cart�o " + numeroDoCartao);
			}
		}

		return buscaBoletos(titular);
	}
	/**
	 * M�todo que busca os boletos recebendo um TitularFinanceiroSR.
	 * @param titular
	 * @return
	 * @throws ValidateException
	 */
	private ResumoCobrancas buscaBoletos(TitularFinanceiroSR titular)
			throws ValidateException {
		if (titular == null){
			throw new ValidateException("Nenhum Benefici�rio encontrado!");
		}

		boolean hasCobrancaAberta = false;
		for (Cobranca cobranca : titular.getCobrancas()) {
			if (cobranca.isSituacaoAtual(SituacaoEnum.ABERTO.descricao())) {
				hasCobrancaAberta = true;
			}
		}

		if(!hasCobrancaAberta){
			throw new ValidateException("N�o h� boletos na situa��o Aberto(a) para este Benefici�rio.");
		}

		ResumoCobrancas resumo = new ResumoCobrancas();

		for (Cobranca cobranca : titular.getCobrancas()){
			if (cobranca.isSituacaoAtual(SituacaoEnum.ABERTO.descricao())){
				resumo.getCobrancas().add(cobranca);
			}
		}

		return resumo;
	}

	public Cobranca gerarBoleto(Cobranca cobranca) throws Exception{
		boolean isCobrancaSemNossoNumero = false;

		for (ContaInterface conta : cobranca.getColecaoContas().getContas()) {
			if (conta.getNossoNumero() == null) {
				isCobrancaSemNossoNumero = true;
			}
		}

		if (isCobrancaSemNossoNumero)
			throw new ValidateException("O Boleto n�o p�de ser gerado porque esta cobran�a n�o foi gerada pelo sistema!");

		cobranca.tocarObjetos();

		validaValidade(cobranca);

		return cobranca;
	}

	private void validaValidade(Cobranca cobranca) throws ValidateException {

		if (cobranca.getDataValidade() != null){
			if (isValidadeVencida(cobranca.getDataValidade())){
				throw new ValidateException("O Boleto n�o p�de ser reimpresso porque est� fora do seu prazo de validade!");
			}
		}else{
			BoletoConfigurator boletoConfigurator = (BoletoConfigurator) HibernateUtil.currentSession().createCriteria(BoletoConfigurator.class)
			.add(Expression.eq("ativo", true)).uniqueResult();

			Date validade = calculaValidade(cobranca, boletoConfigurator);

			if (isValidadeVencida(validade)){
				throw new ValidateException("O Boleto n�o p�de ser reimpresso porque est� fora do seu prazo de validade!");
			}
		}
	}

	private boolean isValidadeVencida(Date validade) {
		if (Utils.compareData(new Date(), validade) == 1){
			return true;
		}

		return false;
	}

	public Date calculaValidade(Cobranca cobranca, BoletoConfigurator boletoConfigurator){

		Calendar calendarVencimento = Calendar.getInstance();

		calendarVencimento.setTime(cobranca.getDataVencimento());

		return Utils.incrementaDias(calendarVencimento, boletoConfigurator.getValidade());	
	}

	public static void main(String[] args) throws Exception {
		BoletosService reimpressao = new BoletosService();
		Cobranca cobranca = ImplDAO.findById(87037l, Cobranca.class);
		reimpressao.gerarBoleto(cobranca);
	}
}
