package br.com.infowaypi.ecarebc.planos;

import static java.util.Collections.reverseOrder;
import static java.util.Collections.sort;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecarebc.segurados.GrupoBC;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

public class AcordoGrupo implements Serializable, Comparable<AcordoGrupo> {

	private static final long serialVersionUID = 1L;
	
	private long idAcordo;
	private String descricao;
	private Date dataAtivacao;
	private Plano plano;
	private Carencia carencia;
	private GrupoBC grupoBC;
	private boolean padrao;
	private Set<Tabela> tabelas;
	
	public AcordoGrupo() {
		carencia = new Carencia();
		tabelas = new HashSet<Tabela>();
	}
	
	public Carencia getCarencia() {
		return carencia;
	}
	public void setCarencia(Carencia carencia) {
		this.carencia = carencia;
	}
	
	public Date getDataAtivacao() {
		return dataAtivacao;
	}
	
	public void setDataAtivacao(Date dataAtivacao) {
		this.dataAtivacao = dataAtivacao;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public GrupoBC getGrupo() {
		return grupoBC;
	}
	
	public void setGrupo(GrupoBC grupoBC) {
		this.grupoBC = grupoBC;
	}
	
	public long getIdAcordo() {
		return idAcordo;
	}
	
	public void setIdAcordo(long idAcordo) {
		this.idAcordo = idAcordo;
	}
	
	public Plano getPlano() {
		return plano;
	}
	
	public void setPlano(Plano plano) {
		this.plano = plano;
	}

	public Set<Tabela> getTabelas() {
		return tabelas;
	}
	
	public void setTabelas(Set<Tabela> tabelas) {
		this.tabelas = tabelas;
	}
	
	public boolean isPadrao() {
		return padrao;
	}

	public void setPadrao(boolean padrao) {
		this.padrao = padrao;
	}
	
	public Tabela selecionarTabela(TipoTabela tipoTabela, Date date) {
		Assert.isNotEmpty(tabelas, "O Acordo " + getDescricao() + " não possui tabelas.");
		List<Tabela> list =  new ArrayList<Tabela>();
		for (Tabela tabela : tabelas){
			if (tipoTabela.getDescricaoTipo().equals(tabela.getTipo()))
				list.add(tabela);
		}
		
		if (list.size() == 1) {
			return list.iterator().next();
		}
		
		sort(list, reverseOrder(new Tabela.TabelaDataAtivacaoComparator()));

		Tabela tabela = null;
		
		Date dataInicial;
		Date dataFinal;
		Iterator<Tabela> iter = list.iterator();
		Tabela tabelaAtual = iter.next();
		dataFinal = tabelaAtual.getDataAtivacao();
		
		if (date.compareTo(dataFinal) >= 0)
			return tabelaAtual;
		
		boolean selected = false;
		while (iter.hasNext() && !selected) {
			tabela = iter.next();
			dataInicial = tabela.getDataAtivacao();
			selected = Utils.between(dataInicial, dataFinal, date);
			dataFinal = dataInicial;
		}

		return tabela;
	}
	
	public int compareTo(AcordoGrupo acordo) {
		Comparator<Date> dataComp = Utils.dataComparator();
		return dataComp.compare(dataAtivacao, acordo.getDataAtivacao());
	}

	public static class AcordoGrupoDataAtivacaoComparator implements Comparator<AcordoGrupo> {
		public int compare(AcordoGrupo acordo1, AcordoGrupo acordo2) {
			return Utils.dataComparator().compare(acordo1.dataAtivacao, acordo2.dataAtivacao);
		}
	}

	
}
