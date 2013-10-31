package br.com.infowaypi.ecare.services.relacionamento;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.com.infowaypi.ecare.resumos.ResumoSimulacaoTratamento;
import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;

public class SimulacaoServicoSaudeService {

	public ResumoSegurados buscarSegurado(String numeroDoCartao,String cpfDoTitular){
		return BuscarSegurados.buscar(numeroDoCartao,cpfDoTitular,Segurado.class);
	}
	
	public ResumoSimulacaoTratamento inclusaoProcedimentos(Segurado segurado, Collection<Procedimento> procedimentos) throws ValidateException{
		Assert.isNotNull(segurado, "Segurado deve ser informado!");
		if(procedimentos.isEmpty())
			throw new ValidateException("Algum procedimento deve ser informado!");
		
		List<Procedimento> proced = new ArrayList<Procedimento>(procedimentos);
		ResumoSimulacaoTratamento resumo = new ResumoSimulacaoTratamento(segurado, proced);
		return resumo;
	}
	
	public void resultado(ResumoProcedimentos resumoProcedimentos) { }
	
} 