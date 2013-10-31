package br.com.infowaypi.ecare.segurados;

import java.util.Collection;

import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;

public class ImprimirSegundaViaCartao {
	
	public ResumoSegurados buscarSegurado(String cpf, String numeroDoCartao) throws ValidateException{
		SearchAgent sa = new SearchAgent();
		
		boolean isCPFNulo = Utils.isStringVazia(cpf);
		boolean isNumeroDoCartaoNulo = Utils.isStringVazia(numeroDoCartao);
		
		if(isCPFNulo && isNumeroDoCartaoNulo) {
			throw new RuntimeException("Caro usuário, informe ao menos um dos campos abaixo");
		}
		
		if(!isCPFNulo){
			sa.addParameter(new Equals("pessoaFisica.cpf", cpf));
			sa.addParameter(new Equals("situacao.descricao", SituacaoEnum.ATIVO.descricao()));
		}
		if(!isNumeroDoCartaoNulo){
			sa.addParameter(new Equals("numeroDoCartao", numeroDoCartao));
		}
		
		ResumoSegurados resumo  = criaResumoSeguradosParaTitular(sa);

		if (resumo==null){
			resumo  = criaResumoSeguradosParaPensionista(sa);
		}
		
		if (resumo == null && !isCPFNulo){
			throw new ValidateException(MensagemErroEnum.CPF_SEGURADO_NAO_ENCONTRADO.getMessage(cpf));
		}
		
		if (resumo == null && !isNumeroDoCartaoNulo){
			throw new ValidateException(MensagemErroEnum.CARTAO_SEGURADO_NAO_ENCONTRADO.getMessage(numeroDoCartao));
		}
		
		return resumo;
	}

	private ResumoSegurados criaResumoSeguradosParaPensionista(SearchAgent sa) {
		Pensionista pensionista = sa.uniqueResult(Pensionista.class);
		if (pensionista!= null ){
			return criarResumoPensionista(pensionista);
		}
		return null;
	}

	private ResumoSegurados criaResumoSeguradosParaTitular(SearchAgent sa) {
		Titular titular = sa.uniqueResult(Titular.class);
		if (titular != null){
			return criarResumoTitular(titular);	
		}
		return null;
	}

	private ResumoSegurados criarResumoPensionista(Pensionista pensionista) {
		ResumoSegurados resumo = new ResumoSegurados();
		resumo.getSegurados().add(pensionista);
		return resumo;
	}

	private ResumoSegurados criarResumoTitular(Titular titular) {
		ResumoSegurados resumo = new ResumoSegurados();
		resumo.getSegurados().add(titular);
		resumo.getSegurados().addAll((Collection<? extends Segurado>) titular.getDependentes());
		resumo.getSegurados().addAll((Collection<? extends Segurado>) titular.getDependentesSuplementares());
		return resumo;
	}

	public Segurado selecionarSegurado(Segurado segurado) throws ValidateException {
		segurado.tocarObjetos();
		
		if(segurado.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())){
			throw new ValidateException(MensagemErroEnumSR.SEGURADO_CANCELADO_NO_SISTEMA.getMessage());
		}
		
		if(segurado.getCartaoAtual().getRemessa() == null){
			throw new ValidateException(MensagemErroEnumSR.SEGURADO_POSSUI_2VIA_PENDENTE.getMessage());
		}
		
		if (!segurado.isAtivo()){
			throw new ValidateException(MensagemErroEnum.SEGURADO_INATIVO_NO_SISTEMA.getMessage());
		}
		
		return segurado;
	}
	
	public Cartao criarSegundaVia(Segurado segurado) throws Exception{
		Cartao cartao = segurado.gerarCartao();
		ImplDAO.save(segurado);
		return cartao;
	}

	public void finalizar(){
	
	}
	
}
