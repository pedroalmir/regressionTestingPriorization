package br.com.infowaypi.ecare.relatorio;

import java.sql.Time;
import java.util.Date;

import br.com.infowaypi.ecare.resumos.ResumoProcedimentosPorPrestador;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;

public class RelatorioProcedimentosPorPrestador{
	
	public ResumoProcedimentosPorPrestador gerarRelatorio(TabelaCBHPM procedimento, Prestador prestador, Date dataInicial, Date dataFinal) throws Exception {
		long inicio = System.currentTimeMillis();
		
		long agora = System.currentTimeMillis();
		ResumoProcedimentosPorPrestador resumo = new ResumoProcedimentosPorPrestador(prestador, procedimento, dataInicial, dataFinal);
		
		
		resumo.buscarGuias();
		agora = System.currentTimeMillis();
		
		resumo.alimentarMapaGuiasPorPrestador();
		agora = System.currentTimeMillis();
		
		resumo.alimentarMapaProcedimentosPorPrestador();
		agora = System.currentTimeMillis();
		
		
		resumo.processarMapa();
		agora = System.currentTimeMillis();
		
		return resumo;
	}

}
