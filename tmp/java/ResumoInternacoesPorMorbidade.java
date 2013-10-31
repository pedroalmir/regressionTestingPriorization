package br.com.infowaypi.ecare.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.infowaypi.ecarebc.procedimentos.CID;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;

public class ResumoInternacoesPorMorbidade {
	private String codigoMorbidade;
	private String descricaoMorbidade;
	
	private Integer qtdeInternacoes;
	
	private List<ResumoPrestadorInternacoes> resumoPrestadorInternacoes;
	
	public ResumoInternacoesPorMorbidade() {
		this.resumoPrestadorInternacoes = new ArrayList<ResumoPrestadorInternacoes>();
	}

	public ResumoInternacoesPorMorbidade(Object morbidade, Integer qtdeInternacoes, List<ResumoPrestadorInternacoes> resumoPrestadorInternacoes) {
		if (morbidade instanceof CID) {
			CID cid = (CID) morbidade;
			this.codigoMorbidade = cid.getCodigo();
			this.descricaoMorbidade = cid.getDescricaoDaDoenca();
		}
		
		if (morbidade instanceof TabelaCBHPM) {
			TabelaCBHPM procedimento = (TabelaCBHPM) morbidade;
			this.codigoMorbidade = procedimento.getCodigo();
			this.descricaoMorbidade = procedimento.getDescricao();
		}

		this.qtdeInternacoes = qtdeInternacoes;
		
		this.resumoPrestadorInternacoes = this.ordenarResumo(resumoPrestadorInternacoes);
	}

	private List<ResumoPrestadorInternacoes> ordenarResumo(List<ResumoPrestadorInternacoes> resumoPrestadorInternacoes){
		Collections.sort(resumoPrestadorInternacoes, new Comparator<ResumoPrestadorInternacoes>(){
			public int compare(ResumoPrestadorInternacoes a, ResumoPrestadorInternacoes b) {
				ResumoPrestadorInternacoes obja = a;
				ResumoPrestadorInternacoes objb = b;
				return objb.getQtdeInternacoes().compareTo(obja.getQtdeInternacoes());
			}
		});
		
		return resumoPrestadorInternacoes;
	}

	public String getCodigoMorbidade() {
		return codigoMorbidade;
	}

	public void setCodigoMorbidade(String codigoMorbidade) {
		this.codigoMorbidade = codigoMorbidade;
	}

	public String getDescricaoMorbidade() {
		return descricaoMorbidade.toUpperCase();
	}

	public void setDescricaoMorbidade(String descricaoMorbidade) {
		this.descricaoMorbidade = descricaoMorbidade;
	}

	public Integer getQtdeInternacoes() {
		return qtdeInternacoes;
	}

	public void setQtdeInternacoes(Integer qtdeInternacoes) {
		this.qtdeInternacoes = qtdeInternacoes;
	}

	public List<ResumoPrestadorInternacoes> getResumoPrestadorInternacoes() {
		return resumoPrestadorInternacoes;
	}

	public void setResumoPrestadorInternacoes(
			List<ResumoPrestadorInternacoes> resumoPrestadorInternacoes) {
		this.resumoPrestadorInternacoes = resumoPrestadorInternacoes;
	}
	
}
