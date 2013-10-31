package br.com.infowaypi.ecare.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecarebc.segurados.DependenteInterfaceBC;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

/**
 * 
 * @author Idelvane, Marcus Boolean
 *
 */
public class InclusaoTitularDependente {
	
	private Segurado segurado;
	private BigDecimal valorBruto;
	private BigDecimal valorTotal;
	private BigDecimal valorDependentesExistentes;
	private BigDecimal valorDependentesNovos;
	private String numeroDoCartao;
	private String cpf;
	
	
//	private double percentagem;
	
	
	private Collection<ResumoIncluidos> incluidos;
	private Collection<ResumoIncluidos> incluidosAntigos;

	public InclusaoTitularDependente() {
		this.valorBruto = BigDecimal.ZERO;
		this.valorTotal = BigDecimal.ZERO;
		this.valorDependentesExistentes = BigDecimal.ZERO;
		this.valorDependentesNovos = BigDecimal.ZERO;
		this.incluidos = new ArrayList<ResumoIncluidos>();
		this.incluidosAntigos = new ArrayList<ResumoIncluidos>();
	}
	
	public BigDecimal getValorTotalCalculado() {
		BigDecimal valor = BigDecimal.ZERO;
		valor = getValorColecoes(valor);
		valor = valor.add(this.getValorBrutoCalculado());
		return valor;
	}

	private BigDecimal getValorColecoes(BigDecimal valor) {
		for (ResumoIncluidos incluido : incluidos) {
			valor = valor.add(incluido.getValorIndividual());
		}
		for (ResumoIncluidos resumo : incluidosAntigos) {
			valor = valor.add(resumo.getValorIndividual());
		}
		return valor;
	}
	
	public BigDecimal getValorTotalDependentes() {
		BigDecimal valor = BigDecimal.ZERO;
		valor = getValorColecoes(valor);
		return valor;
	}
	
	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
	
	public BigDecimal getValorBruto() {
		return valorBruto;
	}
	public void setValorBruto(BigDecimal valorBruto) {
		this.valorBruto = valorBruto;
	}
	
	public BigDecimal getValorBrutoCalculado(){
		if(!Utils.isStringVazia(this.getCpf()) || !Utils.isStringVazia(this.getNumeroDoCartao()))
			this.segurado = BuscarSegurados.buscar(this.getCpf(), this.getNumeroDoCartao(), Segurado.class).getSegurados().get(0);
		
		if(this.segurado != null)
			this.calcularPrecoDependentesExistentes(segurado);
			
		return MoneyCalculation.rounded(MoneyCalculation.multiplica(this.getValorBruto(), 3.5f).divide(new BigDecimal(100f)));
	}
	
	public Faixa getFaixa(int idade){
		Faixa faixaEncontrada = null;
		
		for (Faixa faixa : Faixa.values()) {
			if((idade >= faixa.getIdadeMinima()) && (idade <= faixa.getIdadeMaxima()))
				faixaEncontrada = faixa;
		}
		
		return faixaEncontrada;
	}
	
	public FaixaValorFixo getFaixaFixa(int idade){
		FaixaValorFixo faixaEncontrada = null;
		
		for (FaixaValorFixo faixa : FaixaValorFixo.values()) {
			if((idade >= faixa.getFaixaInicial()) && (idade <= faixa.getFaixaFinal()))
				faixaEncontrada = faixa;
		}
		
		return faixaEncontrada;
	}
	public BigDecimal getValorPorDependente(int idade, boolean checaColecoes){
		Faixa faixa = null;
		FaixaValorFixo faixaFixa = null;
		BigDecimal valor = BigDecimal.ZERO;
		
		if(checaColecoes){
			if((this.incluidos.size() + this.incluidosAntigos.size()) > 3){
				faixaFixa = getFaixaFixa(idade);
			}else{
				faixa = getFaixa(idade);
			}
		}else{
			if(this.incluidos.size() > 3){
				faixaFixa = getFaixaFixa(idade);
			}else{
				faixa = getFaixa(idade);
			}
		}
		
		if(faixa == null){
			valor = MoneyCalculation.rounded(faixaFixa.getPreco());
			return valor;
			
		}
		else{ 
			valor =  MoneyCalculation.rounded(MoneyCalculation.multiplica(this.getValorBruto(), (float)faixa.getAliquota()));
			valor = MoneyCalculation.divide(valor, 100f);
			return valor;
		}
		
	}

	public Collection<ResumoIncluidos> getIncluidos() {
		return incluidos;
	}

	public void setIncluidos(Collection<ResumoIncluidos> incluidos) {
		this.incluidos = incluidos;
	}
	
	public Boolean validate(){
		return true;
	}

	public void addDependente(ResumoIncluidos incluso) throws Exception{
		
		if(!Utils.isStringVazia(incluso.getCpf()) || !Utils.isStringVazia(incluso.getNumeroDoCartao()))
			this.segurado = BuscarSegurados.buscar(incluso.getCpf(), incluso.getNumeroDoCartao(), Segurado.class).getSegurados().get(0);
		
		int maiorOrdem =0;
		
		for(ResumoIncluidos incluido : this.getIncluidos()) {
			if(incluido.getOrdem() > maiorOrdem)
				maiorOrdem = incluido.getOrdem();
		}
		for(ResumoIncluidos incluido : this.getIncluidos()) {
			if(maiorOrdem > 3)
				break;
			
			this.setValorBruto(incluso.getValorBruto());
			incluido.setValorIndividual(this.getValorPorDependente(incluido.getIdade(), true));
		}
		
		this.setValorBruto(incluso.getValorBruto());
		
		if(this.segurado != null) {
			this.calcularPrecoDependentesExistentes(segurado);
		}	
		
		incluidos.add(incluso);
		incluso.setDescricao("DEPENDENTE " + (incluidos.size() + incluidosAntigos.size()));
		incluso.setOrdem((incluidos.size() + incluidosAntigos.size()));		
		incluso.setValorIndividual(this.getValorPorDependente(incluso.getIdade(), true));
		
	}
	
	public void excluirDependente(ResumoIncluidos incluso){
		incluidos.remove(incluso);
		int maiorOrdem =0;
		
		maiorOrdem = incluidos.size() + incluidosAntigos.size();
		
		for(int i = 1; i <= incluidos.size(); i++){
			int j = 0;
			
			for (ResumoIncluidos incluido : incluidos) {
				j++;
				incluido.setOrdem(j + incluidosAntigos.size());
				incluido.setDescricao("DEPENDENTE " + incluido.getOrdem());
				if(maiorOrdem > 3)
					continue;
				incluido.setValorIndividual(this.getValorPorDependente(incluido.getIdade(), false));
			}
		}
	}
	
	public void calcularPrecoDependentesExistentes(Segurado segurado){
		
		for (DependenteInterfaceBC dependente: segurado.getTitular().getDependentes()) {
			ResumoIncluidos incluido = new ResumoIncluidos();
			for (ResumoIncluidos resumo : incluidosAntigos) {
				if(resumo.getCartaoDependente() != null ){
					if(resumo.getCartaoDependente().equals(dependente.getNumeroDoCartao()))
						return;
				}
			}
			incluidosAntigos.add(incluido);
			incluido.setIdade(dependente.getPessoaFisica().getIdade());
			incluido.setDescricao(dependente.getPessoaFisica().getNome());
			incluido.setCartaoDependente(dependente.getNumeroDoCartao());
			incluido.setValorIndividual(this.getValorPorDependente(dependente.getPessoaFisica().getIdade(), true));
		}
		for (ResumoIncluidos resumo : incluidosAntigos) {
			this.valorDependentesExistentes = MoneyCalculation.getSoma(this.valorDependentesExistentes, resumo.getValorIndividual().floatValue());
		}
	}

	public Segurado getSegurado() {
		return segurado;
	}

	public void setSegurado(Segurado segurado) {
		this.segurado = segurado;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public BigDecimal getValorDependentesExistentes() {
	
		return valorDependentesExistentes;
	}

	public void setValorDependentesExistentes(BigDecimal valorDependentesExistentes) {
		this.valorDependentesExistentes = valorDependentesExistentes;
	}

	public BigDecimal getValorDependentesNovos() {
		for (ResumoIncluidos resumo : incluidos) {
			this.valorDependentesNovos = MoneyCalculation.getSoma(this.valorDependentesNovos,resumo.getValorIndividual().floatValue());
		}
		return valorDependentesNovos;
	}

	public void setValorDependentesNovos(BigDecimal valorDependentesNovos) {
		this.valorDependentesNovos = valorDependentesNovos;
	}

	public Collection<ResumoIncluidos> getIncluidosAntigos() {
		return incluidosAntigos;
	}

	public void setIncluidosAntigos(Collection<ResumoIncluidos> incluidosNovos) {
		this.incluidosAntigos = incluidosNovos;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getNumeroDoCartao() {
		return numeroDoCartao;
	}

	public void setNumeroDoCartao(String numeroDoCartao) {
		this.numeroDoCartao = numeroDoCartao;
	}
	
}
