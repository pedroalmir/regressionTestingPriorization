package br.com.infowaypi.ecarebc.service;

import static br.com.infowaypi.msr.utils.Assert.isNotEmpty;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecare.validacao.services.InclusaoProcedimentoSituacaoGuiaValidator;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.Role;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.CID;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.ecarebc.service.internacao.MarcacaoInternacaoService;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Service para inclusão de procedimentos numa guia completa aberta.
 * @author root 
 * @changes Luciano Rocha: mudei a validação para inclusão de procedimentos que antes permitia para guia na situação Pago(a). 
 */
public class SolicitarInclusaoProcedimento<S extends SeguradoInterface> extends MarcacaoInternacaoService<GuiaCompleta>{

	public ResumoGuias<GuiaCompleta> buscarGuiasRealimentacao(S segurado, String dataInicial, String dataFinal, Prestador prestador) throws Exception{
		List<GuiaCompleta> guias = super.buscarGuias(dataInicial, dataFinal,segurado, prestador,false, GuiaCompleta.class, SituacaoEnum.ABERTO, SituacaoEnum.PRORROGADO,SituacaoEnum.SOLICITADO_INTERNACAO,SituacaoEnum.SOLICITADO_PRORROGACAO);
		isNotEmpty(guias, MensagemErroEnum.GUIAS_NAO_ENCONTRADAS_PARA_REALIMENTACAO.getMessage());
		return new ResumoGuias<GuiaCompleta>(guias, ResumoGuias.SITUACAO_TODAS, false);
	}
	
	public ResumoGuias<GuiaCompleta> buscarGuiasRealimentacao(Collection<S> segurados, Prestador prestador) throws Exception{
		SearchAgent sa = getSearchSituacoes(SituacaoEnum.ABERTO, SituacaoEnum.PRORROGADO,SituacaoEnum.SOLICITADO_INTERNACAO,SituacaoEnum.SOLICITADO_PRORROGACAO);
		List<GuiaCompleta> guias = super.buscarGuias(sa,segurados, prestador,false, GuiaCompleta.class);
		isNotEmpty(guias, MensagemErroEnum.GUIAS_NAO_ENCONTRADAS_PARA_REALIMENTACAO.getMessage());
		return new ResumoGuias<GuiaCompleta>(guias, ResumoGuias.SITUACAO_TODAS, false);
	}
	
	public GuiaSimples<ProcedimentoInterface> buscarGuiasRealimentacao(String autorizacao) throws Exception{
		if (Utils.isStringVazia(autorizacao))
			throw new ValidateException(MensagemErroEnum.NUMERO_AUTORIZACAO_INVALIDO.getMessage());
		GuiaCompleta guia = super.buscarGuias(autorizacao, null,false, GuiaCompleta.class);
		Assert.isNotNull(guia, MensagemErroEnum.GUIA_NAO_ENCONTRADA.getMessage());
		new InclusaoProcedimentoSituacaoGuiaValidator(guia, false);
		guia.tocarObjetos();
		
		Prestador prestador = guia.getPrestador();
		
		if (prestador != null){
			prestador.tocarObjetos();
		}
		
		return guia;
	}
	
	public GuiaCompleta buscarGuiasRealimentacao(String autorizacao, Prestador prestador) throws Exception{
		if (Utils.isStringVazia(autorizacao))
			throw new ValidateException(MensagemErroEnum.NUMERO_AUTORIZACAO_INVALIDO.getMessage());
		GuiaCompleta guia = super.buscarGuias(autorizacao, prestador,false, GuiaCompleta.class, SituacaoEnum.ABERTO, SituacaoEnum.PRORROGADO,SituacaoEnum.SOLICITADO_INTERNACAO,SituacaoEnum.SOLICITADO_PRORROGACAO,SituacaoEnum.AUTORIZADO);
		Assert.isNotNull(guia, MensagemErroEnum.GUIA_NAO_ENCONTRADA.getMessage());
		guia.tocarObjetos();
		return guia;
	}
	 
	public GuiaCompleta selecionarGuia(GuiaCompleta guia) throws Exception {
		return super.selecionarGuia(guia); 
	}
	
	public void salvarGuia(GuiaSimples guia) throws Exception {
		Boolean updateQuantidades = (guia.getValorAnterior().floatValue() > 0) ? false : true;
		super.salvarGuia(guia, updateQuantidades, true);
	}
	
	public GuiaSimples addProcedimentos(Prestador prestador, Collection<Procedimento> procedimentos,Collection<CID> cids, String justificativa, GuiaCompleta guia, UsuarioInterface usuario) throws Exception{
		Assert.isNotEmpty(procedimentos, MensagemErroEnum.GUIA_SEM_PROCEDIMENTOS.getMessage());

		//Validação de inclusão de procedimentos especiais
		for (ProcedimentoInterface p : procedimentos) {
			TabelaCBHPM cbhpm = p.getProcedimentoDaTabelaCBHPM();
			Assert.isFalse(cbhpm.getEspecial(), MensagemErroEnum.PROCEDIMENTO_ESPECIAL_NAO_SOLICITADO.getMessage(cbhpm.getCodigo()));
		}
		
		if(!Utils.isStringVazia(justificativa)){
			guia.addQuadroClinico(justificativa);
		}
		
		guia.getCids().addAll(cids);

		return 	super.addProcedimentos(prestador,procedimentos, null, guia,usuario, Agente.PRESTADOR);
	}
	
	//Marcação de exames externos 
	public GuiaSimples buscarGuiasExternas(String autorizacao, Prestador prestador) throws Exception{
		if (Utils.isStringVazia(autorizacao))
			throw new ValidateException(MensagemErroEnum.NUMERO_AUTORIZACAO_INVALIDO.getMessage());
		GuiaSimples guia = super.buscarGuias(autorizacao, GuiaSimples.class, null);
		
		if(guia.getPrestador() != null && guia.getPrestador().equals(prestador))
			throw new RuntimeException(MensagemErroEnum.EXAMES_EXTERNOS_SOMENTE_PARA_GUIA_EXTERNAS.getMessage());
		
		guia.tocarObjetos();
		return guia;
	}
	
	//Marcação de exames externos 
	public GuiaSimples addProcedimentos(Prestador prestador, Collection<Procedimento> procedimentos,GuiaCompleta guia, UsuarioInterface usuario) throws Exception{
		
		for (ProcedimentoInterface procedimento : procedimentos) {
			
			if(procedimento.getProcedimentoDaTabelaCBHPM().getEspecial()){
				
				if(prestador != null){
					
					boolean isPrestadorHemopi = prestador.getIdPrestador().equals(Prestador.HEMOPI);
					boolean isPossuiAcordo = prestador.isPossuiAcordo(procedimento.getProcedimentoDaTabelaCBHPM());

					if(isPrestadorHemopi && isPossuiAcordo) {
						return super.addProcedimentos(prestador,procedimentos,guia,usuario, Agente.PRESTADOR);
					} else {
						throw new RuntimeException(MensagemErroEnum.ERRO_EXAME_ESPECIAL_EM_GUIA_EXTERNA.getMessage(procedimento.getProcedimentoDaTabelaCBHPM().getCodigo()));
					}
				}
				
			}
		}
		
		return super.addProcedimentos(prestador,procedimentos,guia,usuario, Agente.PRESTADOR);
	}
	
	public GuiaCompleta buscarGuia(String autorizacao, Prestador prestador) throws Exception{
		if (Utils.isStringVazia(autorizacao))
			throw new ValidateException(MensagemErroEnum.NUMERO_AUTORIZACAO_INVALIDO.getMessage());
		
		SearchAgent sa = new SearchAgent();
//		sa = super.getNotSearchSituacoes(SituacaoEnum.CANCELADO, SituacaoEnum.SOLICITADO_INTERNACAO, SituacaoEnum.FATURADA, SituacaoEnum.FECHADO, SituacaoEnum.AGENDADA);
		GuiaCompleta guia = super.buscarGuias(sa,autorizacao, GuiaCompleta.class, null,false);
		
		Assert.isNotNull(guia, MensagemErroEnum.GUIA_NAO_ENCONTRADA.getMessage());
		
		new InclusaoProcedimentoSituacaoGuiaValidator(guia, false);

		guia.tocarObjetos();
		return guia;
	}
	
	@SuppressWarnings("unchecked")
	public GuiaSimples addProcedimentos(Prestador prestador,Collection<Procedimento> procedimentos, Collection<ProcedimentoCirurgico> procedimentosCirurgicos,Collection<CID> cids, String justificativa, GuiaCompleta guia, UsuarioInterface usuario) throws Exception{
		if(!guia.isInternacao() && !procedimentosCirurgicos.isEmpty())
			throw new RuntimeException(MensagemErroEnum.PROCEDIMENTO_CIRURGICO_PODE_SER_INSERIDO_SOMENTE_PARA_INTERNACAO.getMessage());
		
		if(usuario.isPossuiRole(Role.DIRETORIA_MEDICA.getValor()) || usuario.isPossuiRole(Role.AUDITOR.getValor()) || usuario.isPossuiRole(Role.ROOT.getValor()) || usuario.isPossuiRole(Role.DIGITADOR.getValor())){
			
			for (ProcedimentoInterface procedimento : procedimentos) {
				procedimento.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), "Inclusão de procedimentos simples", new Date());
			}
			
			for (ProcedimentoInterface procedimento : procedimentosCirurgicos) {
				procedimento.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), "Inclusão de procedimentos cirúrgicos", new Date());
			}
		}
		
		GuiaSimples guiaNova = super.addProcedimentos(prestador,procedimentos, procedimentosCirurgicos, guia,usuario, Agente.SUPERVISOR);
		if (!Utils.isStringVazia(justificativa))
			guia.addQuadroClinico(justificativa);
		
		if (guia.getCids()== null && cids != null && !cids.isEmpty())
			guia.getCids().addAll(cids);
		
		guiaNova.recalcularValores();
		return guiaNova;
	}
	
	@Override
	public GuiaCompleta getGuiaInstanceFor(UsuarioInterface usuario) {
		return null;
	}
	
	
	
}