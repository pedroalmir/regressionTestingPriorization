package br.com.infowaypi.ecare.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.FlushMode;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecare.arquivos.ArquivoCartoes;
import br.com.infowaypi.ecare.arquivos.RemessaDeCartao;
import br.com.infowaypi.ecare.segurados.Cartao;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.segurados.Titular;
import br.com.infowaypi.ecare.utils.CriteriaUtils;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;

public class GerarRemessaDeCartoes {
	
	public RemessaDeCartao gerar(){
		
		HibernateUtil.currentSession().setFlushMode(FlushMode.NEVER);
		
		RemessaDeCartao remessa = new RemessaDeCartao();
		remessa.setDataGeracao(new Date());

		List<Segurado> segurados = CriteriaUtils.getCriteriaSegurado(Titular.class)
		.add(Expression.eq("situacao.descricao", SituacaoEnum.ATIVO.descricao()))
		.add(Expression.eq("recadastrado", true))
		.add(Expression.isNull("dataGeracaoDoCartao"))
		.add(Expression.isNotNull("dataAdesao"))
		.list();
		
		List<Cartao> cartoesTitulares = new ArrayList<Cartao>();
		for (Segurado segurado : segurados) {
			cartoesTitulares.add(segurado.getCartaoAtual());
		}
		ArquivoCartoes arquivo = new ArquivoCartoes(cartoesTitulares);
		try {
			arquivo.fileGenerate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Falha ao gerar remessa de cartões.");
		}
		
		remessa.setConteudoArquivo(arquivo.getConteudo());
		remessa.setCartoes(arquivo.getCartoes());
		return remessa;
	}
	
	public RemessaDeCartao salvar(RemessaDeCartao remessa) throws Exception{
		ImplDAO.save(remessa);
		return remessa;	
	}
	
	public void finalizar(RemessaDeCartao remessa){
		
	}

}
