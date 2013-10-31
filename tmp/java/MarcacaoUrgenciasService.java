package br.com.infowaypi.ecarebc.service.urgencia;

import java.util.Collection;
import java.util.Date;

import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaAtendimentoUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacaoEletiva;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacaoUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.procedimentos.CID;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.ecarebc.service.Agente;
import br.com.infowaypi.ecarebc.service.internacao.MarcacaoInternacaoService;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe base para criação de services para guias de urgência do plano de saúde 
 * @author root
 */
@SuppressWarnings("rawtypes")
public class MarcacaoUrgenciasService<S extends AbstractSegurado> extends MarcacaoInternacaoService<GuiaCompleta>{
	
	public int tipoDeGuia;
	private String motivoGuia;

	public MarcacaoUrgenciasService(){
		super();
	}
	
	@Override
	public GuiaCompleta getGuiaInstanceFor(UsuarioInterface usuario) {
		switch (tipoDeGuia){
			case GUIA_CONSULTA_URGENCIA : {motivoGuia = MotivoEnum.SOLICITACAO_DE_CONSULTA_URGENCIA.getMessage(); 
				return new GuiaConsultaUrgencia(usuario);}
			case GUIA_ATENDIMENTO_SUBSEQUENTE : {motivoGuia = MotivoEnum.ATENDIMENTO_URGENCIA.getMessage(); 
				return new GuiaAtendimentoUrgencia(usuario);}
			case GUIA_INTERNACAO_URGENCIA :{ motivoGuia = MotivoEnum.INTERNACAO_URGENCIA.getMessage();
				return new GuiaInternacaoUrgencia(usuario); }
			case GUIA_INTERNACAO_ELETIVA :{ motivoGuia = MotivoEnum.INTERNACAO_ELETIVA.getMessage();
				return new GuiaInternacaoEletiva(usuario); }
			default: return null;
		}	
	}
	
	public GuiaCompleta criarGuiaUrgencia(S segurado, Integer tipoDeGuia,Profissional profissional,Especialidade especialidade, Prestador prestador, UsuarioInterface usuario, Date dataAtendimento, Collection<CID> cids, String justificativa) throws Exception {

		this.tipoDeGuia = tipoDeGuia;
		
		/* if_not[NAO_OBRIGATORIEDADE_DE_INFORMAR_PROFISSIONAL_NO_ATENDIMENTO]
		Assert.isNotNull(profissional, "Profissional deve ser informado!");
		end[NAO_OBRIGATORIEDADE_DE_INFORMAR_PROFISSIONAL_NO_ATENDIMENTO] */
		
		if(segurado.isInternado()){
			throw new ValidateException(MensagemErroEnum.SEGURADO_INTERNADO_OU_COM_SOLICITACAO.getMessage());
		}
		
		GuiaCompleta guia =  super.criarGuia(segurado,tipoDeGuia,null, prestador, usuario, null ,profissional, 
								especialidade, null, cids, justificativa, Utils.format(dataAtendimento), motivoGuia, Agente.PRESTADOR);
		return guia;
	}
	
	public GuiaCompleta criarGuiaUrgencia(S segurado, Integer tipoDeGuia,Profissional profissional,Especialidade especialidade, Prestador prestador, UsuarioInterface usuario, Date dataAtendimento) throws Exception {
		return criarGuiaUrgencia(segurado, tipoDeGuia, profissional, especialidade, prestador, usuario, dataAtendimento, null, null);
	}
	
	public GuiaCompleta addProcedimentos(Collection<Procedimento> procedimentos,GuiaCompleta guia) throws Exception {
		return (GuiaCompleta)super.addProcedimentos(null,procedimentos,null,guia,null,Agente.PRESTADOR);
	}
	
	@Override
	public void salvarGuia(GuiaSimples guia) throws Exception {
		super.salvarGuia(guia);
	}
	
	@Override
	public void onAfterCriarGuia(GuiaCompleta guia, Collection<? extends ProcedimentoInterface> procedimentos) throws Exception {
		super.onAfterCriarGuia(guia, procedimentos);
		if (guia.isConsultaUrgencia()){
			((GuiaConsultaUrgencia)guia).addProcedimentoConsulta();
		}
		
		if(guia.isAtendimentoUrgencia()){
			((GuiaAtendimentoUrgencia)guia).addProcedimentoConsultaZerado();
		}
	}	
	
}
