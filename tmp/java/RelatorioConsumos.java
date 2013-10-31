package br.com.infowaypi.ecarebc.consumo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;


/**
 * Prepara o objeto de relat�rio de acordo com os par�metros informados.
 * @author M�rio S�rgio Coelho Marroquim
 */
public class RelatorioConsumos {
	
	@SuppressWarnings("unchecked")
	public ResumoConsumos buscarConsumos(String competencia, Prestador prestador) throws Exception {
		
		List<Prestador> prestadores = new ArrayList<Prestador>();
		 
		if (prestador == null)
			prestadores = getPrestadores();
		else
			prestadores.add(prestador);
		
		Date competenciaEscolhida = null;
		if (!Utils.isStringVazia(competencia)){
			try {
				competenciaEscolhida = Utils.gerarCompetencia(competencia);
			} catch (Exception e){
				e.printStackTrace();
				throw new ValidateException("A compet�ncia informada � inv�lida: " + e.getMessage());
			}
		}
		else
			throw new ValidateException("A compet�ncia deve ser informada.");
		
		ResumoConsumos resumo = new ResumoConsumos(prestadores, competenciaEscolhida);
		if(resumo == null || resumo.getConsumos().isEmpty())
			throw new ValidateException("N�o foram encontrados consumos para a compet�ncia informada.");
		
		return resumo;
	}
	
	@SuppressWarnings("unchecked")
	public List<Prestador> getPrestadores() {
		return ImplDAO.findByParam((Iterator)null, Prestador.class);
	}

	public void finalizar(ResumoConsumos resumo){}

}