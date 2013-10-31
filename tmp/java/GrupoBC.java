package br.com.infowaypi.ecarebc.segurados;

import static java.util.Collections.reverseOrder;
import static java.util.Collections.sort;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;

import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.planos.AcordoGrupo;
import br.com.infowaypi.ecarebc.planos.Tabela;
import br.com.infowaypi.ecarebc.planos.TipoTabela;
import br.com.infowaypi.ecarebc.planos.Carencia.TipoCarencia;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

public class GrupoBC implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long idGrupo;
	private String codigoLegado;
	protected String descricao;
	private Set<SubGrupo> subGrupos;
	private Set<AcordoGrupo> acordos;
	private Set<TitularInterfaceBC> titulares;
	private boolean geral;

	public GrupoBC(){
		subGrupos = new HashSet<SubGrupo>();
		acordos = new HashSet<AcordoGrupo>();
	}
	
	public Set<SubGrupo> getSubGrupos() {
		return subGrupos;
	}
	
	public void setSubGrupos(Set<SubGrupo> subGrupos) {
		this.subGrupos = subGrupos;
	}
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Long getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(Long idGrupo) {
		this.idGrupo = idGrupo;
	}

	public String getCodigoLegado() {
		return codigoLegado;
	}

	public void setCodigoLegado(String codigoLegado) {
		this.codigoLegado = codigoLegado;
	}
	
	public Set<AcordoGrupo> getAcordos() {
		return acordos;
	}

	public void setAcordos(Set<AcordoGrupo> acordos) {
		this.acordos = acordos;
	}
	
	public Set<TitularInterfaceBC> getTitulares() {
		return titulares;
	}

	public void setTitulares(Set<TitularInterfaceBC> titulares) {
		this.titulares = titulares;
	}
	
	public int getQuantidadeTitularesAtivos(){
		int quantidade = 0;
		for (TitularInterfaceBC titular : this.getTitulares()) {
			if (titular.isSituacaoAtual(SituacaoEnum.ATIVO.descricao())){
				quantidade++;
			}	
		}
		return quantidade;
	}
	
	public boolean isGeral() {
		return geral;
	}

	public void setGeral(boolean geral) {
		this.geral = geral;
	}

	public Boolean validate() throws ValidateException {
		return true;
	}
	
	public BigDecimal getValor(AbstractSegurado segurado, Date date) {
		return getValor(getTipoTabela(), segurado, date);
	}
	
	public BigDecimal getValor(TipoTabela tipoTabela, AbstractSegurado segurado, Date date){
		Tabela tabela = this.selecionarAcordo(date).selecionarTabela(tipoTabela, date);
		return new BigDecimal(tabela.getValor(segurado));
	}
	
	
	public int getCarencia(TipoCarencia tipoCarencia){
		return this.selecionarAcordo(new Date()).getCarencia().getCarencia(tipoCarencia);
	}
	
	public int getCarencia(TipoCarencia tipoCarencia, Date date){
		return this.selecionarAcordo(date).getCarencia().getCarencia(tipoCarencia);
	}

	public TipoTabela getTipoTabela(){
		if (this.isGeral())
			return TipoTabela.TIPO_I;
		
		return TipoTabela.getTipoTabela(this.getQuantidadeTitularesAtivos());
	}
	
	public AcordoGrupo selecionarAcordo(Date dataAtivacao){
		Assert.isNotEmpty(acordos, "O GrupoBC " + getDescricao() + " não possui acordos.");
		List<AcordoGrupo> list =  new ArrayList<AcordoGrupo>();
		
		if (list.size() == 1) {
			return list.iterator().next();
		}
		
		sort(list, reverseOrder(new AcordoGrupo.AcordoGrupoDataAtivacaoComparator()));

		AcordoGrupo acordo = null;
		
		Date dataInicial;
		Date dataFinal;
		Iterator<AcordoGrupo> iter = list.iterator();
		AcordoGrupo acordoAtual = iter.next();
		dataFinal = acordoAtual.getDataAtivacao();
		
		if (dataAtivacao.compareTo(dataFinal) >= 0)
			return acordoAtual;
		
		boolean selected = false;
		while (iter.hasNext() && !selected) {
			acordo = iter.next();
			dataInicial = acordo.getDataAtivacao();
			selected = Utils.between(dataInicial, dataFinal, dataAtivacao);
			dataFinal = dataInicial;
		}

		return acordo;
	}

	public void tocarObjetos(){
		this.getDescricao();
		this.getSubGrupos();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof GrupoBC))
			return false;
		GrupoBC grupo = (GrupoBC)obj;
		return new EqualsBuilder().
			append(this.codigoLegado, grupo.codigoLegado).
			append(this.descricao, grupo.descricao)
			.isEquals();
	}
	
	
}