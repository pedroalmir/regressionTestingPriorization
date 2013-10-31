package br.com.infowaypi.ecare.financeiro.boletos;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.FlushMode;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecare.constantes.Constantes;
import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.enums.MotivoEnumSR;
import br.com.infowaypi.ecare.financeiro.Cobranca;
import br.com.infowaypi.ecare.financeiro.CobrancaInterface;
import br.com.infowaypi.ecare.segurados.AbstractMatricula;
import br.com.infowaypi.ecare.segurados.DependenteSR;
import br.com.infowaypi.ecare.segurados.TitularFinanceiroSR;
import br.com.infowaypi.ecarebc.enums.Role;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.consignacao.Consignacao;
import br.com.infowaypi.ecarebc.financeiro.conta.Conta;
import br.com.infowaypi.ecarebc.financeiro.conta.ContaInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.Not;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Service do mapeamento <b>BaixaManualDeBoletos.jhm.xml</b> responsável <br>
 * por efetuar baixas manuais em cobranças de titulares que tem matrículas <br>
 * com pagamento do tipo Boleto. Caso o titular não tenha cobranças para a <br>
 * competência informada o sistema gera automaticamente uma nova cobrança, <br>
 * notificando o usuário. Esta cobrança já é gerada na situação <b>Pago(a)<b>.
 * @author Diogo Vinícius
 * @changes Luciano Rocha (Baixa indiscriminada de boletos no role suporte)
 */
@SuppressWarnings("unchecked")
public class BaixaManualDeBoletosService extends BoletosService{

//	private static Date competencia;
	public static Date primeiraCompetenciaSR = Utils.createData("01/07/2007");
	/**
	 * Método do 1º step-method <br>
	 * Responsável por buscar o titular e suas cobranças fazendo as validações necessárias
	 * @param cpf
	 * @param competencia
	 * @return a cobrança encontrada do titular informado
	 * @throws ValidateException 
	 * @throws Exception
	 */
	public ResumoBaixaManual buscarBoleto(String cpf, String numeroDoCartao, String competencia) throws ValidateException {
		TitularFinanceiroSR titular;
		
		SearchAgent sa = new SearchAgent();
		if(!Utils.isStringVazia(cpf))
			sa.addParameter(new Equals("pessoaFisica.cpf", cpf));
		if(!Utils.isStringVazia(numeroDoCartao))
			sa.addParameter(new Equals("numeroDoCartao", numeroDoCartao));
		
		sa.addParameter(new Not(new Equals("situacao.descricao", SituacaoEnum.CANCELADO.descricao())));
		
		String parametroExibido = cpf != null ? cpf : numeroDoCartao;
		
		List<TitularFinanceiroSR> titulares = sa.list(TitularFinanceiroSR.class);
		if (titulares.size() > 1) {
			throw new ValidateException(MensagemErroEnumSR.MAIS_DE_UM_BENEFICIARIO_PARA_O_CPF.getMessage(parametroExibido));
		}else if (titulares.size() == 0) {
			throw new ValidateException(MensagemErroEnumSR.BENEFICIARIO_NAO_ENCONTRADO_PARA_O_CPF.getMessage(parametroExibido));			
		}
		else {
			titular = titulares.get(0);
		}
		
		boolean isPagaBoleto = false;
		for (AbstractMatricula matricula : titular.getMatriculas()) {
			if (matricula.getTipoPagamento().equals(Constantes.BOLETO)) {
				isPagaBoleto = true;
			}
		}
		
		if (!isPagaBoleto && !titular.isSeguradoDependenteSuplementar()) {
			throw new ValidateException(MensagemErroEnumSR.BENEFICIARIO_SEM_MATRICULA_BOLETO.getMessage(titular.getPessoaFisica().getNome()));
		}
		
		Date comp = Utils.createData("01/" + competencia);
		boolean existeCobranca = false;
		Cobranca cobranca = (Cobranca) titular.getCobranca(comp);
		if(cobranca != null && cobranca.isSituacaoAtual(SituacaoEnum.PAGO.descricao())){
			existeCobranca = true;
		}
		
		CobrancaInterface primeiraCobranca = titular.getPrimeiraCobranca();
		Date competenciaPrimeiraCobranca = null;
		if (primeiraCobranca != null) {
			competenciaPrimeiraCobranca = primeiraCobranca.getCompetencia();
		}
		
		if(existeCobranca){
			throw new ValidateException(MensagemErroEnumSR.BENEFICIARIO_COM_COBRANCA_NA_COMPETENCIA.getMessage());
		} else {
			if (Utils.compararCompetencia(comp, new Date()) > 0) {
				throw new ValidateException(MensagemErroEnumSR.COBRANCA_NAO_ENCONTRADA_E_IMPOSSIBILIDADE_DE_GERACAO_COBRANCA_EM_COMPETENCIA_POSTERIOR.getMessage());
			} else if (competenciaPrimeiraCobranca == null){
				if(Utils.compararCompetencia(comp, primeiraCompetenciaSR) < 0) {
					throw new ValidateException(MensagemErroEnumSR.COBRANCA_NAO_ENCONTRADA_E_IMPOSSIBILIDADE_DE_GERACAO_COBRANCA_EM_COMPETENCIA_ANTIGA.getMessage());					
				}
			} else if (Utils.compararCompetencia(comp, competenciaPrimeiraCobranca) < 0) {
				throw new ValidateException(MensagemErroEnumSR.COBRANCA_NAO_ENCONTRADA_E_IMPOSSIBILIDADE_DE_GERACAO_COBRANCA_EM_COMPETENCIA_ANTERIOR_DA_PRIMEIRA_COBRANCA.getMessage());
			}
		}
		
//		Assert.isNotNull(cobranca, MensagemErroEnumSR.COBRANCA_NAO_ENCONTRADA.getMessage());
		
		tocarObjetos(cobranca, titular);
		if (cobranca != null){
			if (cobranca.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())){
				throw new RuntimeException(MensagemErroEnumSR.BAIXA_MANUAL_COBRANCA_CANCELADA.getMessage());
			}
		}
		
		ResumoBaixaManual resumo = new ResumoBaixaManual(titular,cobranca, comp);
		
		return resumo;
	}
	
	/**
	 * Método do 1º step-method <br>
	 * Responsável por buscar o titular e suas cobranças fazendo as validações necessárias para fazer a baixa manual
	 * indiscriminada.
	 * @param cpf
	 * @param numeroDoCartao
	 * @param competencia
	 * @param usuario
	 * @return
	 * @throws ValidateException
	 * @author Luciano Rocha
	 * @since 12/12/2012
	 */
	public ResumoBaixaManual buscarBoleto(String cpf, String numeroDoCartao, String competencia, UsuarioInterface usuario) throws ValidateException {
		TitularFinanceiroSR titular;
		
		SearchAgent sa = new SearchAgent();
		if(!Utils.isStringVazia(cpf))
			sa.addParameter(new Equals("pessoaFisica.cpf", cpf));
		if(!Utils.isStringVazia(numeroDoCartao))
			sa.addParameter(new Equals("numeroDoCartao", numeroDoCartao));
		
		sa.addParameter(new Not(new Equals("situacao.descricao", SituacaoEnum.CANCELADO.descricao())));
		
		String parametroExibido = cpf != null ? cpf : numeroDoCartao;
		
		if (parametroExibido == null) {
			throw new ValidateException("Digite o CPF ou o NÚMERO DO CARTÃO do beneficiário.");
		}
		
		List<TitularFinanceiroSR> titulares = sa.list(TitularFinanceiroSR.class);
		if (titulares.size() > 1) {
			throw new ValidateException(MensagemErroEnumSR.MAIS_DE_UM_BENEFICIARIO_PARA_O_CPF.getMessage(parametroExibido));
		}else if (titulares.size() == 0) {
			throw new ValidateException(MensagemErroEnumSR.BENEFICIARIO_NAO_ENCONTRADO_PARA_O_CPF.getMessage(parametroExibido));			
		}
		else {
			titular = titulares.get(0);
		}
		
		boolean isPagaBoleto = false;
		for (AbstractMatricula matricula : titular.getMatriculas()) {
			if (matricula.getTipoPagamento().equals(Constantes.BOLETO)) {
				isPagaBoleto = true;
			}
		}
		
		if (!isPagaBoleto && !titular.isSeguradoDependenteSuplementar()) {
			throw new ValidateException(MensagemErroEnumSR.BENEFICIARIO_SEM_MATRICULA_BOLETO.getMessage(titular.getPessoaFisica().getNome()));
		}
		
		Date comp = Utils.createData("01/" + competencia);
		Cobranca cobranca = (Cobranca) titular.getCobranca(comp);
		
		CobrancaInterface primeiraCobranca = titular.getPrimeiraCobranca();
		Date competenciaPrimeiraCobranca = null;
		if (primeiraCobranca != null) {
			competenciaPrimeiraCobranca = primeiraCobranca.getCompetencia();
		}
		
		if (Utils.compararCompetencia(comp, new Date()) > 0) {
			throw new ValidateException(MensagemErroEnumSR.COBRANCA_NAO_ENCONTRADA_E_IMPOSSIBILIDADE_DE_GERACAO_COBRANCA_EM_COMPETENCIA_POSTERIOR.getMessage());
		} else if (competenciaPrimeiraCobranca == null){
			if(Utils.compararCompetencia(comp, primeiraCompetenciaSR) < 0) {
				throw new ValidateException(MensagemErroEnumSR.COBRANCA_NAO_ENCONTRADA_E_IMPOSSIBILIDADE_DE_GERACAO_COBRANCA_EM_COMPETENCIA_ANTIGA.getMessage());					
			}
		} else if (Utils.compararCompetencia(comp, competenciaPrimeiraCobranca) < 0) {
			throw new ValidateException(MensagemErroEnumSR.COBRANCA_NAO_ENCONTRADA_E_IMPOSSIBILIDADE_DE_GERACAO_COBRANCA_EM_COMPETENCIA_ANTERIOR_DA_PRIMEIRA_COBRANCA.getMessage());
		}
		
		
		tocarObjetos(cobranca, titular);
		
		ResumoBaixaManual resumo = new ResumoBaixaManual(titular,cobranca, comp);
		
		return resumo;
	}
	
	private void tocarObjetos(Cobranca cobranca, TitularFinanceiroSR titular) {
		if(cobranca != null){
			cobranca.getSituacoes().size();
			for (ContaInterface conta : cobranca.getColecaoContas().getContas()) {
				conta.getSituacoes().size();
			}
		}

		titular.tocarObjetos();

		//Só toca a situação do titular e seus dependentes nos casos em que o mesmo estiver suspenso, 
		//ou seja, nos casos onde o mesmo será reativado
		if (titular.isSituacaoAtual(SituacaoEnum.SUSPENSO.descricao())){
			titular.getColecaoSituacoes().getSituacoes().size();
			for (DependenteSR dependente: titular.getDependentes()){
				dependente.getColecaoSituacoes().getSituacoes().size();
			}
		}


		for (Consignacao consig : titular.getConsignacoes()) {
			consig.getSituacoes().size();
		}
	}

	/**
	 * Método do 2º step-method <br>
	 * Responsável por setar na cobrança criada os valores vindos do fluxo. Este
	 * método só é executado quando há uma cobrança encontrada para o titular, caso
	 * contrário o método seguinte será executado diretamente
	 * @param valorPago
	 * @param dataPagamento
	 * @param cobranca
	 * @param usuario
	 * @return a cobrança com os valores vindos do fluxo
	 * @throws Exception
	 */
	public Cobranca informarValores(
			BigDecimal valorPago, 
			String dataPagamento, 
			ResumoBaixaManual resumo, 
			UsuarioInterface usuario) throws Exception {
		
		return informarValores(BigDecimal.ZERO,"01/01/2007",valorPago,dataPagamento,resumo,usuario);
	}

	/**
	 * Método do 2º step-method <br>
	 * Responsável por setar na cobrança criada os valores vindos do fluxo.
	 * @param valorCobrado
	 * @param dataVencimento
	 * @param valorPago
	 * @param dataPagamento
	 * @param cobranca
	 * @param usuario
	 * @return a cobrança com os valores vindos do fluxo
	 * @throws Exception
	 */
	public Cobranca informarValores(BigDecimal valorCobrado, String dataVencimento, BigDecimal valorPago, String dataPagamento,
									ResumoBaixaManual resumo, UsuarioInterface usuario) throws Exception{
		
		Cobranca cobranca = resumo.getCobranca();
		TitularFinanceiroSR titular = resumo.getTitular();
		Date competencia = resumo.getCompetencia();
		
		corrigeVoltaDeFluxo(titular);
		
		if (cobranca == null) {
			boolean isValorCobradoEmpty   = valorCobrado == null;
			boolean isValorPagoEmpty      = valorPago == null;
			boolean isDataVencimentoEmpty = dataVencimento == null;
			boolean isDataPagamentoEmpty  = dataPagamento == null;
			
			if (isValorCobradoEmpty || isValorPagoEmpty || isDataVencimentoEmpty || isDataPagamentoEmpty) {
				throw new ValidateException(MensagemErroEnumSR.OBRIGATORIEDADE_NO_PREENCHIMENTO_DOS_CAMPOS.getMessage());
			}
			
			cobranca = new Cobranca();
			ContaInterface conta = new Conta();
			
			cobranca.getColecaoContas().add(conta);
			cobranca.setTitular(titular);
			titular.getCobrancas().add(cobranca);
			
			cobranca.mudarSituacao(usuario, SituacaoEnum.PAGO.descricao(), MotivoEnumSR.GERACAO_AUTOMATICA_BOLETO_NA_BAIXA_MANUAL.getMessage(), new Date());
			cobranca.setCompetencia(competencia);
			cobranca.setValorCobrado(valorCobrado);
			cobranca.setValorPago(valorPago);
			cobranca.setDataVencimento(Utils.createData(dataVencimento));
			cobranca.setDataPagamento(Utils.createData(dataPagamento));
		
			for (ContaInterface c : cobranca.getColecaoContas().getContas()) {
				c.mudarSituacao(usuario, SituacaoEnum.PAGO.descricao(), MotivoEnumSR.GERACAO_AUTOMATICA_BOLETO_NA_BAIXA_MANUAL.getMessage(), new Date());
				c.setCompetencia(competencia);
				c.setValorCobrado(valorCobrado);
				c.setValorPago(valorPago);
				c.setDataVencimento(Utils.createData(dataVencimento));
				c.setDataPagamento(Utils.createData(dataPagamento));		
			}			
		} else {
			if((valorPago == null) || (valorPago.compareTo(BigDecimal.ZERO) == 0)){
				valorPago = cobranca.getValorCobrado();
			}
			
			cobranca.mudarSituacao(usuario, SituacaoEnum.PAGO.descricao(), MotivoEnumSR.PAGAMENTO_ATRAVES_BAIXA_MANUAL.getMessage(), new Date());
			cobranca.setValorPago(valorPago);
			cobranca.setDataPagamento(Utils.createData(dataPagamento));
			
			for (ContaInterface c : cobranca.getColecaoContas().getContas()) {
				c.mudarSituacao(usuario, SituacaoEnum.PAGO.descricao(), MotivoEnumSR.PAGAMENTO_ATRAVES_BAIXA_MANUAL.getMessage(), new Date());
				c.setValorPago(valorPago);
				c.setDataPagamento(Utils.createData(dataPagamento));
			}
		}
		
		if (titular.isSituacaoAtual(SituacaoEnum.SUSPENSO.descricao())){
			titular.reativarTitularEDependente(usuario,cobranca.getDataPagamento(), "Reativado durante o processo de baixa manual de boletos", new Date());
		}
		
		if(valorPago.compareTo(cobranca.getValorCobrado()) < 0 && !usuario.isPossuiRole("suporte")){
			throw new ValidateException(MensagemErroEnumSR.VALOR_PAGO_MENOR_QUE_VALOR_COBRADO.getMessage());
		}
		
		return cobranca;
	}
	
	/**
	 * Método do 3º step-method <br>
	 * Responsável por salvar as modificações na cobrança
	 * @param cobranca
	 * @throws Exception
	 */
	public void efetuarBaixaEmBoleto(Cobranca cobranca) throws Exception{
		if(cobranca.getIdFluxoFinanceiro() == null){
			HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
			
			List<Cobranca> cobrancas = HibernateUtil.currentSession().createCriteria(Cobranca.class)
													.add(Expression.eq("competencia", cobranca.getCompetencia()))
													.add(Expression.eq("titular", cobranca.getTitular()))
													.add(Expression.ne("situacao.descricao", SituacaoEnum.CANCELADO.descricao()))
													.list();
			
			if(cobrancas.size() > 0){
				throw new RuntimeException("Já existe uma cobrança para essa competência.");
			}
		}
		ImplDAO.save(cobranca);
	}
	
	/**
	 * Método do 3º step-method <br>
	 * Responsável por salvar as modificações na cobrança
	 * @param cobranca
	 * @throws Exception
	 */
	public void efetuarBaixaEmBoleto(Cobranca cobranca, UsuarioInterface usuario) throws Exception{
		if(cobranca.getIdFluxoFinanceiro() == null){
			HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
			
			List<Cobranca> cobrancas = HibernateUtil.currentSession().createCriteria(Cobranca.class)
													.add(Expression.eq("competencia", cobranca.getCompetencia()))
													.add(Expression.eq("titular", cobranca.getTitular()))
													.list();
			
			if(cobrancas.size() > 0){
				throw new RuntimeException("Já existe uma cobrança para essa competência.");
			}
		}
		ImplDAO.save(cobranca);
	}
	
	/**
	 * Método auxiliar para verificar se uma cobrança é da competência informada no primeiro passo do fluxo
	 * @param cobranca
	 * @return true caso a cobrança seja da competência informada no primeiro passo do fluxo
	 */
	private boolean isFromCompetencia(Cobranca cobranca, Date competencia) {
		return Utils.compararCompetencia(cobranca.getCompetencia(), competencia) == 0;
	}
	
	private void corrigeVoltaDeFluxo(TitularFinanceiroSR titular){
		Iterator<Cobranca> cobrancas = titular.getCobrancas().iterator();
		while (cobrancas.hasNext()) {
			Cobranca cobranca = (Cobranca) cobrancas.next();
			if(cobranca.getIdFluxoFinanceiro() == null){
				cobranca.setTitular(null);
				cobrancas.remove();
			}
		}
	}

}
