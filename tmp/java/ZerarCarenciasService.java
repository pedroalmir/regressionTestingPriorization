package br.com.infowaypi.ecare.services.suporte;

import java.util.Calendar;
import java.util.Collection;

import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.segurados.Titular;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;

public class ZerarCarenciasService {

	public Segurado buscarSegurado(String cpf, String numeroDoCartao) throws ValidateException{
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
		
		Segurado segurado = sa.uniqueResult(Segurado.class);
		
		if (segurado == null && !isCPFNulo)
			throw new ValidateException(MensagemErroEnum.CPF_SEGURADO_NAO_ENCONTRADO.getMessage(cpf));
		if (segurado == null && !isNumeroDoCartaoNulo)
			throw new ValidateException(MensagemErroEnum.CARTAO_SEGURADO_NAO_ENCONTRADO.getMessage(numeroDoCartao));
		if(segurado.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()))
			throw new ValidateException(MensagemErroEnumSR.SEGURADO_CANCELADO_NO_SISTEMA.getMessage());
		if (!segurado.isAtivo())
			throw new ValidateException(MensagemErroEnum.SEGURADO_INATIVO_NO_SISTEMA.getMessage());
		
		return segurado;
	}
	
	public Segurado zerarCarencias(Segurado segurado) throws Exception {
		Calendar inicioDaCarencia = Calendar.getInstance();
		inicioDaCarencia.add(Calendar.YEAR, -2);
		
		segurado.setInicioDaCarencia(inicioDaCarencia.getTime());
		ImplDAO.save(segurado);
		return segurado;

	}
	
}
