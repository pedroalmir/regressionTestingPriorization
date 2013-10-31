package br.com.infowaypi.ecare.segurados;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.com.infowaypi.ecare.constantes.Constantes;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.SearchAgentInterface;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Respons�vel pela Impress�o de Carteiras para todos os segurados referentes 
 * a matr�cula informada.
 * @author M�rio S�rgio Coelho Marroquim
 */
public class ImpressaoDeCarteiraService {

	@SuppressWarnings("unchecked")
	public ResumoSegurados buscarSegurado(String matricula) throws ValidateException {
		if(Utils.isStringVazia(matricula))
			throw new ValidateException("Matr�cula deve ser preenchida.");

		if(matricula.length() != 6)
			throw new ValidateException("Matr�cula Inv�lida. Formato: 999999.");
		
		SearchAgentInterface sa = new SearchAgent();
		sa.addParameter(new Equals("matricula", matricula));
		
		List<Segurado> segurados = sa.list(Segurado.class);
		if(segurados == null || segurados.isEmpty())
			throw new ValidateException("N�o foi encontrado segurado com a matr�cula " + matricula + ".");
		
		List<Segurado> seguradosFiltrados = new ArrayList<Segurado>();
		for(Segurado segurado : segurados){
			if(segurado.getSituacao().getDescricao().equals(Constantes.SITUACAO_ATIVO)){
				segurado.tocarObjetos();
				seguradosFiltrados.add(segurado);
			}
		}
		
		if(seguradosFiltrados == null || seguradosFiltrados.isEmpty())
			throw new ValidateException("Nenhum segurado foi encontrado!");
		
		return new ResumoSegurados(seguradosFiltrados);
	}
	
	public ResumoSegurados selecionarSegurados(Collection<Segurado> seguradosEscolhidos) throws ValidateException {
		if(seguradosEscolhidos == null || seguradosEscolhidos.isEmpty())
			throw new ValidateException("Selecione um ou mais segurados!");
		return new ResumoSegurados(new ArrayList<Segurado>(seguradosEscolhidos));
	}
	
	public void finalizar(ResumoSegurados resumoSeguradosEscolhidos){}
}