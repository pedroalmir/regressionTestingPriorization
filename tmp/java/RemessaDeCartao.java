package br.com.infowaypi.ecare.arquivos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.FlushMode;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;

import br.com.infowaypi.ecare.segurados.Cartao;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.segurados.TitularFinanceiroSR;
import br.com.infowaypi.ecare.utils.CriteriaUtils;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.utils.Utils;

public class RemessaDeCartao implements Serializable{

	private Long idRemessaDeCartao;
	private Set<Cartao> cartoes;
	private Date dataGeracao;
	private byte[] conteudoArquivo;
	private boolean enviado;

	public RemessaDeCartao(){
		this.cartoes = new HashSet<Cartao>();
	}
	
	public Set<Cartao> getCartoes() {
		return cartoes;
	}

	public void setCartoes(Set<Cartao> cartoes) {
		this.cartoes = cartoes;
	}

	public byte[] getConteudoArquivo() {
		return conteudoArquivo;
	}

	public void setConteudoArquivo(byte[] conteudoArquivo) {
		this.conteudoArquivo = conteudoArquivo;
	}

	public Date getDataGeracao() {
		return dataGeracao;
	}

	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	public boolean isEnviado() {
		return enviado;
	}

	public void setEnviado(boolean enviado) {
		this.enviado = enviado;
	}

	public Long getIdRemessaDeCartao() {
		return idRemessaDeCartao;
	}

	public void setIdRemessaDeCartao(Long idRemessaDeCartao) {
		this.idRemessaDeCartao = idRemessaDeCartao;
	}
	
	public void addCartoes(Collection<Cartao> cartoes) {
		for (Cartao cartao : cartoes) {
			addCartao(cartao); 
		}
	}

	public void addCartao(Cartao cartao) {
		this.getCartoes().add(cartao);
		cartao.setRemessa(this);
	}
	
	public Boolean validate() {
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
		
		if (idRemessaDeCartao == null){
			Date data = (Date) HibernateUtil.currentSession().createCriteria(RemessaDeCartao.class)
			.setProjection(Projections.max("dataGeracao"))
			.uniqueResult();
			
			if((data!= null) && Utils.compareData(data,new Date()) == 0 )
				throw new RuntimeException("Prezado(a) usuário(a), não é permitido gerar duas remessas de cartão no mesmo dia.");
			
			setDataGeracao(new Date());

			List<Segurado> segurados = CriteriaUtils.getCriteriaSegurado(TitularFinanceiroSR.class)
			.add(Expression.eq("situacao.descricao", SituacaoEnum.ATIVO.descricao()))
			.add(Expression.eq("recadastrado", true))
			.add(Expression.isNull("dataGeracaoDoCartao"))
			.add(Expression.isNotNull("dataAdesao"))
			.add(Expression.sizeGt("matriculas",0))
			.setFetchSize(100)
			.list();

			
			List<Cartao> cartoesTitulares = new ArrayList<Cartao>();
			for (Segurado segurado : segurados) {
				if(segurado.getCartaoAtual() != null)
					cartoesTitulares.add(segurado.getCartaoAtual());
			}
			ArquivoCartoes arquivo = new ArquivoCartoes(cartoesTitulares);
			try {
				arquivo.fileGenerate();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Falha ao gerar remessa de cartões.");
			}
			
			setConteudoArquivo(arquivo.getConteudo());
			setCartoes(arquivo.getCartoes());
		}
		return true;
		
	}
	
	public String getNomeArquivo(){
		return "RemessaDeCartao_"+Utils.format(getDataGeracao(), "dd_MM_yyyy")+".csv";
	}


}
