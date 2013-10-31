package br.com.infowaypi.ecarebc.financeiro.faturamento.retencao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.msr.utils.Utils;

public class Imposto{
	
	@SuppressWarnings("unchecked")
	public static Retencao getRetencao(int tipoDeImposto, BigDecimal valorBase, Integer tipoDePessoa){
		ImpostoInterface imposto = null;
		Retencao novaRetencao = null;
		SearchAgent sa = new SearchAgent();
		if(Constantes.INSS == tipoDeImposto && AbstractImposto.PESSOA_FISICA == tipoDePessoa){
			imposto = (ImpostoInterface) sa.list(Inss.class).get(0);
		}
		else if(Constantes.IMPOSTO_DE_RENDA == tipoDeImposto){
			sa = new SearchAgent();
			sa.addParameter(new Equals("tipoDePessoa", tipoDePessoa));
			List<ImpostoDeRendaInterface> impostos = sa.list(ImpostoDeRenda.class);
			if(!impostos.isEmpty()){
				if(AbstractImposto.PESSOA_JURIDICA == tipoDePessoa){
					imposto = impostos.get(0);
				}
				else{
					for (ImpostoDeRendaInterface impostoAtual : impostos) {
						if((valorBase.compareTo(impostoAtual.getValorFaixaDe()) > 0) && (valorBase.compareTo(impostoAtual.getValorFaixaAte()) <= 0)){
							imposto = impostoAtual;
							break;
						}
					}
				}
			}
			else return null;
		}
		else if(Constantes.ISS == tipoDeImposto){
			sa = new SearchAgent();
			sa.addParameter(new Equals("tipoDePessoa", tipoDePessoa));
			List<ImpostoInterface> lista = sa.list(Iss.class);
			if(!lista.isEmpty())
				imposto = lista.get(0);
			else return null;
		}
		else
			return null;
		
 		if(imposto == null) return null;
		
		novaRetencao = prepareRetencao(tipoDeImposto, valorBase, imposto);
		
		return novaRetencao;
	}

	private static Retencao prepareRetencao(int tipoDeImposto,
			BigDecimal valorBase, ImpostoInterface imposto) {
		
		imposto.setValorBaseCalculo(valorBase);
		Retencao novaRetencao = new Retencao();
		novaRetencao.setValorBaseDoCalculo(imposto.getValorBaseCalculo());
		novaRetencao.setPercentualDoCalculo(imposto.getAliquota());
		novaRetencao.setValorDeducaoBaseDoCalculo(imposto.getValorDeducao());
		novaRetencao.setValor(imposto.getValor().compareTo(BigDecimal.ZERO) > 0 ? imposto.getValor() : BigDecimal.ZERO);
		novaRetencao.setDescricao(imposto.getDescricao());
		novaRetencao.setTipoDeRetencao(tipoDeImposto);
		return novaRetencao;
	}
	
	public static boolean saveNovoIss(Integer tipoDePessoa) throws Exception{
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new In("competencia", Utils.gerarCompetencia().getTime()));
		sa.addParameter(new Equals("tipoDePessoa", tipoDePessoa));
		List<Iss> impostos = sa.list(Iss.class);
		if(!impostos.isEmpty())
			return false;
		
		sa.addParameter(new In("competencia", Utils.incrementaMes(Utils.gerarCompetencia(), -1)));
		List<Iss> impostosAnteriores = sa.list(Iss.class);
		
		if(!impostosAnteriores.isEmpty()) {
			ImpostoInterface imposto = impostosAnteriores.get(0).clone();
			imposto.setCompetencia(Utils.gerarCompetencia().getTime());
			ImplDAO.save(imposto);
			return true;
		}
		return false;
	}
	
	public static boolean saveNovoIr(Integer tipoDePessoa) throws Exception{
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new In("competencia", Utils.gerarCompetencia().getTime()));
		sa.addParameter(new Equals("tipoDePessoa", tipoDePessoa));
		List<ImpostoDeRenda> impostosAtuais = sa.list(ImpostoDeRenda.class);
		if(!impostosAtuais.isEmpty()){
			if(AbstractImposto.PESSOA_FISICA.equals(tipoDePessoa)){
				if(!(impostosAtuais.size() >= 3)){
					for (ImpostoDeRenda impostoAtual : impostosAtuais) {
						sa.addParameter(new In("competencia", Utils.incrementaMes(Utils.gerarCompetencia(), -1)));
						List<ImpostoDeRenda> impostosAnteriores = sa.list(ImpostoDeRenda.class);
						for (ImpostoDeRenda impostoAnterior : impostosAnteriores) {
							if(!impostoAtual.equals(impostoAnterior)){
								ImpostoInterface imposto = impostoAnterior.clone();
								imposto.setCompetencia(Utils.gerarCompetencia().getTime());
								ImplDAO.save(imposto);		
							}
						}
					}
					return true;
				}
				else{
					return false;
				}
			}
			else{
				return false;
			}
		}
		sa.addParameter(new In("competencia", Utils.incrementaMes(Utils.gerarCompetencia(), -1)));
		List<ImpostoDeRenda> impostosAnteriores = sa.list(ImpostoDeRenda.class);
		for (ImpostoDeRenda impostoDeRenda : impostosAnteriores) {
			ImpostoInterface imposto = impostoDeRenda.clone();
			imposto.setCompetencia(Utils.gerarCompetencia().getTime());
			ImplDAO.save(imposto);
		}
		return true;
	}
	
	public static boolean saveNovoInss() throws Exception{
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new In("competencia", Utils.gerarCompetencia().getTime()));
		List<Inss> impostos = sa.list(Inss.class);
		if(!impostos.isEmpty())
			return false;
		
		sa.addParameter(new In("competencia", Utils.incrementaMes(Utils.gerarCompetencia(), -1)));
		List<Inss> impostosAnteriores = new ArrayList<Inss>(); 
		impostosAnteriores = sa.list(Inss.class);
		
		if(!impostosAnteriores.isEmpty()) {
			ImpostoInterface imposto = impostosAnteriores.get(0).clone();
			imposto.setCompetencia(Utils.gerarCompetencia().getTime());
			ImplDAO.save(imposto);
			return true;
		}
		
		return false;
	}
	
}