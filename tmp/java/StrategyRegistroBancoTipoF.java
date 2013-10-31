package br.com.infowaypi.ecarebc.financeiro.arquivos.strategy.tipoArquivo;

import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.financeiro.DependenteFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.FluxoFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.TitularFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.arquivos.ArquivoDeRetorno;
import br.com.infowaypi.ecarebc.financeiro.conta.Conta;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.financeiro.Banco;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

public class StrategyRegistroBancoTipoF implements StrategyRegistroBancoInterface {

	public void executar(String linha, ArquivoDeRetorno arquivo, UsuarioInterface usuario) throws Exception {
		String codigoConta = linha.substring(69, 79);
		Long idConta = Utils.createLong(codigoConta);

		Conta conta = (Conta) ImplDAO.findById(idConta, Conta.class);
		if (conta == null)
    		throw new Exception("Nenhuma Cobrança com o identificador: " + idConta);
		//usado para verificar o tipo de parcela. Verificar quando
		String tipoCobranca = linha.substring(79, 86);
		//TODO criar um booleano para indicar se uma conta ou cobranca já foi processada de alguma forma
		//TODO rever esta mensagem de erro.
		if (conta.getArquivoRetorno() != null){
			throw new Exception("Arquivo de Retorno já processado anteriormente para esta cobranca (" + conta.getIdConta() + ")...");
		}
		
		String codigoRetorno = linha.substring(67, 69);
		String competenciaConta = linha.substring(86, 96);
		if ("00".equals(codigoRetorno)){
			Date dataPagamento = Utils.parse(linha.substring(44, 52), "yyyyMMdd");
			mudarSituacao(conta, tipoCobranca, competenciaConta, usuario, Constantes.SITUACAO_PAGO, StrategyRegistroBancoInterface.DEBITO_EFETUADO, dataPagamento, arquivo);
		}
		else
			mudarSituacao(conta, tipoCobranca, competenciaConta, usuario, Constantes.SITUACAO_INADIMPLENTE, getMotivo(codigoRetorno, arquivo.getBanco()), null, arquivo);
	}
	
	private void mudarSituacao(Conta conta, String tipo, String competenciaConta, UsuarioInterface usuario, String descricaoDaSituacao, String motivoSituacao, Date dataPagamento, ArquivoDeRetorno arquivo) throws Exception{
		conta.setValorPago(conta.getValorCobrado());
		conta.setDataPagamento(dataPagamento);
		//TODO em conta setar o atributo competencia ou a competencia da cobranca?
		conta.setArquivoRetorno(arquivo);
		arquivo.getContas().add(conta);
		
		//TODO verificar se a situação da conta ou cobranca já estar paga. Se estiver não mudar a Situação.
		if(!conta.isSituacaoAtual(Constantes.SITUACAO_PAGO))
			conta.mudarSituacao(usuario,descricaoDaSituacao,motivoSituacao, dataPagamento);
		
		SearchAgent sa = new SearchAgent();
		sa.clearAllParameters();
		sa.addParameter(new Equals("colecaoContas", conta.getColecaoContas()));
		List<FluxoFinanceiroInterface> cobrancaIndividuals = sa.list(FluxoFinanceiroInterface.class);
		for (FluxoFinanceiroInterface cobranca: cobrancaIndividuals) {
			if(cobranca.getColecaoContas().getContas().contains(conta)){
			
				if(!cobranca.isSituacaoAtual(Constantes.SITUACAO_PAGO))
					cobranca.mudarSituacao(usuario,descricaoDaSituacao,motivoSituacao, dataPagamento);
				
				TitularFinanceiroInterface titularDaCobranca = cobranca.getTitularFinanceiro();
				
				if (Constantes.SITUACAO_PAGO.equals(descricaoDaSituacao)){
					cobranca.setDataPagamento(dataPagamento);
					cobranca.setValorPago(cobranca.getValorCobrado());

					if((!titularDaCobranca.isSituacaoAtual(Constantes.SITUACAO_ATIVO))&&(!titularDaCobranca.isSituacaoAtual(Constantes.SITUACAO_CANCELADO)))
						titularDaCobranca.mudarSituacao(usuario,Constantes.SITUACAO_ATIVO,"Processamento automático de cobranca.",new Date());
				}else
					titularDaCobranca.mudarSituacao(usuario,Constantes.SITUACAO_SUSPENSO,"Não pagamento da cobrança.",new Date());
				
				conta.setCompetencia(cobranca.getCompetencia());
				
				for (DependenteFinanceiroInterface dependente : titularDaCobranca.getDependentesFinanceiro()) {
					if (Constantes.SITUACAO_PAGO.equals(descricaoDaSituacao)){
						if ((!dependente.isSituacaoAtual(Constantes.SITUACAO_ATIVO))&&(!dependente.isSituacaoAtual(Constantes.SITUACAO_CANCELADO)))
							dependente.mudarSituacao(null,Constantes.SITUACAO_ATIVO,"Processamento automático de cobranca.",new Date());
					} else
						dependente.mudarSituacao(usuario,Constantes.SITUACAO_SUSPENSO,"Não pagamento da cobrança.",new Date());
				}
				ImplDAO.save(cobranca);
			}
		}
		ImplDAO.save(conta);
	}
	
	private String getMotivo(String codigoRetorno, Banco banco){
		int codigo = Integer.parseInt(codigoRetorno);
		
		// Banco do Brasil ou Bradesco 
		if("001".equals(banco.getCodigoFebraban()) || "237".equals(banco.getCodigoFebraban())){
			switch(codigo){
				case  1 : return StrategyRegistroBancoInterface.BB_DNE_01;
				case  2 : return StrategyRegistroBancoInterface.BB_DNE_02;
				case  4 : return StrategyRegistroBancoInterface.BB_DNE_04;
				case 10 : return StrategyRegistroBancoInterface.BB_DNE_10;
				case 12 : return StrategyRegistroBancoInterface.BB_DNE_12;
				case 13 : return StrategyRegistroBancoInterface.BB_DNE_13;
				case 14 : return StrategyRegistroBancoInterface.BB_DNE_14;
				case 15 : return StrategyRegistroBancoInterface.BB_DNE_15;
				case 18 : return StrategyRegistroBancoInterface.BB_DNE_18;
				case 30 : return StrategyRegistroBancoInterface.BB_DNE_30;
				case 96 : return StrategyRegistroBancoInterface.BB_DNE_96;
				case 97 : return StrategyRegistroBancoInterface.BB_DNE_97;
				case 98 : return StrategyRegistroBancoInterface.BB_DNE_98;
				case 99 : return StrategyRegistroBancoInterface.BB_DNE_99;
			}
		}
		// Caixa Econômica
		else if("104".equals(banco.getCodigoFebraban())){
			switch(codigo){
				case  1 : return StrategyRegistroBancoInterface.CEF_DNE_01;
				case  2 : return StrategyRegistroBancoInterface.CEF_DNE_02;
				case  3 : return StrategyRegistroBancoInterface.CEF_DNE_03;
				case  4 : return StrategyRegistroBancoInterface.CEF_DNE_04;
				case  5 : return StrategyRegistroBancoInterface.CEF_DNE_05;
				case  7 : return StrategyRegistroBancoInterface.CEF_DNE_07;
				case  8 : return StrategyRegistroBancoInterface.CEF_DNE_08;
				case  9 : return StrategyRegistroBancoInterface.CEF_DNE_09;
				case 11 : return StrategyRegistroBancoInterface.CEF_DNE_11;
				case 12 : return StrategyRegistroBancoInterface.CEF_DNE_12;
				case 15 : return StrategyRegistroBancoInterface.CEF_DNE_15;
				case 19 : return StrategyRegistroBancoInterface.CEF_DNE_19;
				case 20 : return StrategyRegistroBancoInterface.CEF_DNE_20;
				case 21 : return StrategyRegistroBancoInterface.CEF_DNE_21;
				case 22 : return StrategyRegistroBancoInterface.CEF_DNE_22;
				case 34 : return StrategyRegistroBancoInterface.CEF_DNE_34;
				case 39 : return StrategyRegistroBancoInterface.CEF_DNE_39;
				case 40 : return StrategyRegistroBancoInterface.CEF_DNE_40;
				case 41 : return StrategyRegistroBancoInterface.CEF_DNE_41;
				case 46 : return StrategyRegistroBancoInterface.CEF_DNE_46;
				case 47 : return StrategyRegistroBancoInterface.CEF_DNE_47;
				case 50 : return StrategyRegistroBancoInterface.CEF_DNE_50;
				case 51 : return StrategyRegistroBancoInterface.CEF_DNE_51;
				case 52 : return StrategyRegistroBancoInterface.CEF_DNE_52;
				case 53 : return StrategyRegistroBancoInterface.CEF_DNE_53;
				case 57 : return StrategyRegistroBancoInterface.CEF_DNE_57;
				case 60 : return StrategyRegistroBancoInterface.CEF_DNE_60;
				case 62 : return StrategyRegistroBancoInterface.CEF_DNE_62;
				case 63 : return StrategyRegistroBancoInterface.CEF_DNE_63;
				case 64 : return StrategyRegistroBancoInterface.CEF_DNE_64;
				case 65 : return StrategyRegistroBancoInterface.CEF_DNE_65;
				case 66 : return StrategyRegistroBancoInterface.CEF_DNE_66;
				case 67 : return StrategyRegistroBancoInterface.CEF_DNE_67;
				case 76 : return StrategyRegistroBancoInterface.CEF_DNE_76;
				case 78 : return StrategyRegistroBancoInterface.CEF_DNE_78;
				case 81 : return StrategyRegistroBancoInterface.CEF_DNE_81;
				case 82 : return StrategyRegistroBancoInterface.CEF_DNE_82;
				case 85 : return StrategyRegistroBancoInterface.CEF_DNE_85;
				case 86 : return StrategyRegistroBancoInterface.CEF_DNE_86;
				case 87 : return StrategyRegistroBancoInterface.CEF_DNE_87;
				case 89 : return StrategyRegistroBancoInterface.CEF_DNE_89;
				case 91 : return StrategyRegistroBancoInterface.CEF_DNE_91;
				case 95 : return StrategyRegistroBancoInterface.CEF_DNE_95;
				case 96 : return StrategyRegistroBancoInterface.CEF_DNE_96;
				case 99 : return StrategyRegistroBancoInterface.CEF_DNE_99;
			}
		}
		return null;
	}
}