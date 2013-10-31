package br.com.infowaypi.ecare.services.honorariomedico;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import br.com.infowaypi.ecare.services.honorariomedico.adapter.AdapterHonorario;
import br.com.infowaypi.ecare.services.honorariomedico.adapter.AdapterProcedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoHonorario;


/**
 * Classe que contem os metodos que podem ser reaproveitados no flow @AuditarGuiaHonorarioMedico
 * @author Emanuel
 *
 */
public class UtilsAuditarGuiaHonorarioMedico {
	
	public static Collection<AdapterProcedimento> getProcedimentosCirurgicosNaoMarcadosParaAuditar(Collection<AdapterProcedimento> adapterProcedimentos){
		Set<AdapterProcedimento> procedimentos = new HashSet<AdapterProcedimento>();
		for(AdapterProcedimento adapterProcedimento : adapterProcedimentos){
			if(!adapterProcedimento.getProcedimento().isAuditado()){
				procedimentos.add(adapterProcedimento);
			}
		}
		return procedimentos;
	}
	
	public static Collection<AdapterProcedimento> getProcedimentosCirurgicosMarcadosParaAuditar(Collection<AdapterProcedimento> adapterProcedimentos){
		Set<AdapterProcedimento> procedimentos = new HashSet<AdapterProcedimento>();
		for(AdapterProcedimento adapterProcedimento : adapterProcedimentos){
			if(adapterProcedimento.getProcedimento().isAuditado()){
				procedimentos.add(adapterProcedimento);
			}
		}
		return procedimentos;
	}
	
	public static Set<ProcedimentoHonorario> getProcedimentosClinicosNaoMarcadosParaAuditar(Set<ProcedimentoHonorario> procedimentos){
		Set<ProcedimentoHonorario> honorarioProcedimentos = new HashSet<ProcedimentoHonorario>();
		for(ProcedimentoHonorario procedimento : procedimentos){
			if(!procedimento.isAuditado()){
				honorarioProcedimentos.add(procedimento);
			}
		}
		return honorarioProcedimentos;
	}
	
	public static Collection<AdapterHonorario> getPacotesNaoMarcadosParaAuditar(Collection<AdapterHonorario> adapterHonorarios){
		Set<AdapterHonorario> pacotes = new HashSet<AdapterHonorario>();
		for(AdapterHonorario adapterHonorario : adapterHonorarios){
			if(!adapterHonorario.getHonorario().isAuditado()){
				pacotes.add(adapterHonorario);
			}
		}
		return pacotes;
	}
}
