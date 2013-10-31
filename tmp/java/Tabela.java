package br.com.infowaypi.ecarebc.produto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.TransactionManagerHibernate;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Tabela de preços do plano
 * @author Diogo Vinícius
 *
 */
public class Tabela implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Long idTabela;
	private Set<Faixa> faixas;
	private boolean ativa;
	private Date competencia;
	private Date dataAtivacao;
	private Date dataCadastro;
	private int indice;
	private Produto produto;
	
	
	public static void main(String[] args) throws Exception {
		Produto produtoPiaui = ImplDAO.findById(1L, Produto.class);
		Produto produtoMaranhao = ImplDAO.findById(2L, Produto.class);
		Produto produtoPernambuco = ImplDAO.findById(3L, Produto.class);

		criarTabela(produtoPiaui);
		criarTabela(produtoMaranhao);
		criarTabela(produtoPernambuco);
	}

	private static void criarTabela(Produto produto) throws Exception {
		TransactionManagerHibernate tm = new TransactionManagerHibernate();
		tm.beginTransaction();
		
		Tabela tabelaNova = new Tabela();
		tabelaNova.setAtiva(true);
		tabelaNova.setProduto(produto);
		tabelaNova.setDataCadastro(new Date());
		tabelaNova.setDataAtivacao(Utils.parse("01/01/2011"));
		tabelaNova.setCompetencia(Utils.parse("01/01/2011"));
		tabelaNova.getFaixas().add(new Faixa(0, 18, new BigDecimal(44.95), tabelaNova));
		tabelaNova.getFaixas().add(new Faixa(19, 23, new BigDecimal(54.09), tabelaNova));
		tabelaNova.getFaixas().add(new Faixa(24, 28, new BigDecimal(61.80), tabelaNova));
		tabelaNova.getFaixas().add(new Faixa(29, 33, new BigDecimal(67.43), tabelaNova));
		tabelaNova.getFaixas().add(new Faixa(34, 38, new BigDecimal(82.88), tabelaNova));
		tabelaNova.getFaixas().add(new Faixa(39, 43, new BigDecimal(101.14), tabelaNova));
		tabelaNova.getFaixas().add(new Faixa(44, 48, new BigDecimal(130.64), tabelaNova));
		tabelaNova.getFaixas().add(new Faixa(49, 53, new BigDecimal(143.99), tabelaNova));
		tabelaNova.getFaixas().add(new Faixa(54, 58, new BigDecimal(169.28), tabelaNova));
		tabelaNova.getFaixas().add(new Faixa(59, 999, new BigDecimal(269.71), tabelaNova));
		
		ImplDAO.save(tabelaNova);
		tm.commit();
		tm.closeSession();
	}
	
	public Tabela() {
		setFaixas(new HashSet<Faixa>());
		setDataCadastro(new Date());
		setAtiva(false);
	}
	
	public BigDecimal getValor(int idade) {
		for (Faixa faixa : this.getFaixas()) {
			if (faixa.isNaFaixa(idade)) {
				return faixa.getValor();
			}
		}
		
		return null;
	}
	
	public void ativar() {
		setAtiva(true);
		setDataAtivacao(new Date());
	}
	
	public void ativar(boolean ativa) {
		setAtiva(ativa);
		if (isAtiva()) {
			setDataAtivacao(new Date());
		}
	}
	
	public List<Integer> getIdadesMudancaDeFaixa() {
		List<Integer> idades = new ArrayList<Integer>();
		for (Faixa faixa : this.getFaixasOrdenadas()) {
			idades.add(faixa.getIdadeInicial());
		}
		return idades;
	}
	
	/**
	 * Método que retorna a diferença de valor a pagar ao plano entre a faixa atual do segurado e 
	 * a faixa anterior. Caso a idade informada seja da primeira faixa retorna zero.
	 * @param contrato
	 * @param idadeSegurado
	 * @return valorAcrescido
	 */

	//TODO criar casos  de testes para este método
	public BigDecimal getDiferencaValorEntreFaixas(int idadeSegurado) {
		
		List<Faixa> faixas = getFaixasOrdenadas();
		int count = -1;
		for (Faixa faixaAtual: faixas) {
			count++;
			Faixa faixaAnterior = null; 
			if (faixaAtual.isNaFaixa(idadeSegurado)) {
				if (count == 0) {
					return BigDecimal.ZERO;
				}
				faixaAnterior = faixas.get(--count);
				BigDecimal valorAcrescido = faixaAtual.getValor().subtract(faixaAnterior.getValor());
				return valorAcrescido;
			}
		}
		return BigDecimal.ZERO;
	}

	public List<Faixa> getFaixasOrdenadas() {
		List<Faixa> faixas = new ArrayList<Faixa>(this.getFaixas());
		Collections.sort(faixas);
		return faixas;
	}
	
	public Boolean validate(UsuarioInterface usuario) throws Exception {
		if (this.idTabela == null) {
			setDataCadastro(new Date());
		}
		return true;
	}
	
	public Tabela(boolean ativa) {
		this();
		ativar(ativa);
	}
	
	public Long getIdTabela() {
		return idTabela;
	}
	
	public void setIdTabela(Long idTabela) {
		this.idTabela = idTabela;
	}
	
	public Set<Faixa> getFaixas() {
		return faixas;
	}
	
	public void setFaixas(Set<Faixa> faixas) {
		this.faixas = faixas;
	}
	
	public boolean isAtiva() {
		return ativa;
	}
	
	public void setAtiva(boolean ativa) {
		this.ativa = ativa;
	}
	
	public Date getDataAtivacao() {
		return dataAtivacao;
	}
	
	public void setDataAtivacao(Date dataAtivacao) {
		this.dataAtivacao = dataAtivacao;
	}
	
	public Date getDataCadastro() {
		return dataCadastro;
	}
	
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}
	
	public Date getCompetencia() {
		return competencia;
	}
	
	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}
	
	public int getIndice() {
		return indice;
	}
	
	public void setIndice(int indice) {
		this.indice = indice;
	}
	
	public Produto getProduto() {
		return produto;
	}
	
	public void setProduto(Produto produto) {
		this.produto = produto;
	}


	public String getFaixaFormatada(int idade) {
		for (Faixa faixa : faixas) {
			if (faixa.isNaFaixa(idade)) {
				return faixa.getIdadeInicial() + " - " + faixa.getIdadeFinal();   
			}
		}
		return null;
	}
}
