package br.com.infowaypi.ecarebc.service;

import static br.com.infowaypi.msr.utils.Assert.isNotNull;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsulta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaOdontoUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.enums.TipoConsultaEnum;
/*if[INTERNACAO_OBSERVACAO_SEGURADO_CUMPRINDO_CARENCIA]
import br.com.infowaypi.ecarebc.atendimentos.enums.ValidateGuiaEnum;
end[INTERNACAO_OBSERVACAO_SEGURADO_CUMPRINDO_CARENCIA] */
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/*if[INTERNACAO_OBSERVACAO_SEGURADO_CUMPRINDO_CARENCIA]
import br.com.infowaypi.ecarebc.atendimentos.enums.ValidateGuiaEnum;
end[INTERNACAO_OBSERVACAO_SEGURADO_CUMPRINDO_CARENCIA] */

public abstract class MarcacaoService<G extends GuiaSimples> extends Service {
	
	private boolean ignoreValidacao;

	public abstract G getGuiaInstanceFor(UsuarioInterface usuario);
	
	protected UsuarioInterface usuarioDoFluxo;

	public UsuarioInterface getUsuarioDoFluxo() {
		return usuarioDoFluxo;
	}

	public void setIgnoreValidacao(boolean ignoreValidacao) {
		this.ignoreValidacao = ignoreValidacao;
	}
	
	public boolean isIgnoreValidacao() {
		return ignoreValidacao;
	}
	 
	protected Date getDataDeAtendimento(String dataDeAtendimento) throws ValidateException {
		Date dataAtendimento = null;
		try {
			dataAtendimento = Utils.parse(dataDeAtendimento);
		} catch (Exception e) {
			throw new ValidateException(MensagemErroEnum.DATA_ATENDIMENTO_INVALIDA.getMessage());
		}
//		if(Utils.compareData(dataAtendimento, new Date()) < 0) {
//			throw new ValidateException("A data de atendimento é muito antiga!");
//		}
		return dataAtendimento;
	}
	
	protected <P extends ProcedimentoInterface> G criarGuia(
			AbstractSegurado segurado, 
			Prestador prestador, 
			UsuarioInterface usuario, 
			Profissional solicitante,
			Profissional profissional,
			Especialidade especialidade,
			Collection<P> procedimentos,
			String dataDeAtendimento, 
			String motivo, Agente agente) throws Exception {
		
		isNotNull(segurado, MensagemErroEnum.SEGURADO_NAO_INFORMADO.getMessage());
		segurado.tocarObjetos();
		
		if(solicitante != null) {
			solicitante.tocarObjetos(); 
		}
		if(prestador != null) {
			prestador.tocarObjetos();
		}
		
		Date dataAtendimento = getDataDeAtendimento(dataDeAtendimento);
		G guia = getGuiaInstanceFor(usuario);
		SituacaoInterface situacao = null;
		this.usuarioDoFluxo = usuario;
		
		if(!(guia.isInternacaoUrgencia() || guia.isUrgencia())){
			situacao = guia.getSituacao(/*SituacaoEnum.AGENDADA.descricao()*/);
			situacao.setMotivo(motivo);
			situacao.setUsuario(usuario);
		}

		guia.setFromPrestador(agente.isFromPrestador());
		guia.setDataAtendimento(dataAtendimento);
		guia.setPrestador(prestador);
		guia.setSolicitante(solicitante);
		guia.setEspecialidade(especialidade);
		guia.setProfissional(profissional);
		guia.setSegurado(segurado);
		
		guia.setEspecial(agente.isEspecial() 
				|| usuario.isPossuiRole(UsuarioInterface.ROLE_ROOT) 
				|| usuario.isPossuiRole(UsuarioInterface.ROLE_CENTRAL_DE_SERVICO)
				|| usuario.isPossuiRole(UsuarioInterface.ROLE_AUDITOR)
				|| usuario.isPossuiRole(UsuarioInterface.ROLE_DIRETORIA_MEDICA));		
		
		//Adicionando guia origem
		if(guia.isExame()){
			List<GuiaConsulta> guias = null;
			GuiaConsulta consultaOrigem = null;
			
			if(guia.isExameOdonto())
				guias = guia.buscarConsultaOdontoOrigem();
			else
				guias = guia.buscarConsultaOrigem();
			
			if(!guias.isEmpty()){
				consultaOrigem = guias.get(0);
				consultaOrigem.addGuiaFilha(guia);
			}
		}

		onAfterCriarGuia(guia, procedimentos);
		
		guia.setUsuarioDoFluxo(usuario);

		/*if[INTERNACAO_OBSERVACAO_SEGURADO_CUMPRINDO_CARENCIA]
		
		if(guia.getSegurado().isRegistraInternacaoDeObcervacaoParaDependenteEmCarencia()){
			guia.removeFlowValidator(ValidateGuiaEnum.CARENCIA_INTERNACAO_VALIDATOR.getValidator());
		}
		
		end[INTERNACAO_OBSERVACAO_SEGURADO_CUMPRINDO_CARENCIA] */
		
		if (!isIgnoreValidacao() && !usuario.isPossuiRole(UsuarioInterface.ROLE_DIRETORIA_MEDICA)) {
			validateGuia(guia);
		}else validateGuiaSuperUsuario(guia);
		
		guia.setValorAnterior(BigDecimal.ZERO);
		return guia;
	}
	
	protected void validateGuia(G guia) throws Exception { 
		guia.validate();
	}

	protected void validateGuiaSuperUsuario(G guia) throws Exception {}
	
	public void salvarGuia(GuiaSimples guia, Boolean atualizarQuantidades, Boolean atualizarCustos) throws Exception {
		super.salvarGuia(guia, atualizarQuantidades, atualizarCustos);
		onSalvarGuia(guia);
	}
	
	/**
	 * Carrega do banco especialidade ODONTOLOGIA GERAL
	 * 
	 * FIXME armazenar em outro local essa constante (não realizar busca por descrição)
	 */
	protected Especialidade getEspecialidadeOdontologiaGeral() {
	    SearchAgent sa = new SearchAgent();
	    sa.addParameter(new Equals("descricao", "ODONTOLOGIA GERAL"));
	    return (Especialidade) sa.list(Especialidade.class).get(0);
	}
	
	protected Especialidade getEspecialidadeOdonto(){
		return getEspecialidadeOdonto(TipoConsultaEnum.CONSULTA_ODONTOLOGICA);
	}
	
	protected Especialidade getEspecialidadeOdonto(TipoConsultaEnum tipoConsulta){
		if (tipoConsulta.equals(TipoConsultaEnum.CONSULTA_ODONTOLOGICA) || tipoConsulta.equals(TipoConsultaEnum.CONSULTA_INICIAL))
			return getEspecialidadeOdontologia("ODONTOLOGIA GERAL");
		else if (tipoConsulta.equals(TipoConsultaEnum.CLINICO_PROMOTOR))
			return getEspecialidadeOdontologia("CLINICO PROMOTOR");
		return new Especialidade();		
	}

	public static Especialidade getEspecialidadeOdontologia(String descricao){
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("descricao", descricao));
		return sa.uniqueResult(Especialidade.class);
	}
	
	/** Este método verifica se o usuário já possui uma consulta odontológica de urgência com este profissional 
	 *  realizada nos últimos 30 dias
	 * @param segurado
	 * @param profissional
	 */
	public void verificaValidadeConsultaOdontoDeUrgencia(SeguradoInterface segurado, Profissional profissional) {
		Criteria crit = HibernateUtil.currentSession().createCriteria(GuiaConsultaOdontoUrgencia.class);
		crit.add(Restrictions.eq("segurado", segurado));
		crit.add(Restrictions.between("dataMarcacao", DateUtils.addDays(new Date(), -30), new Date()));
		crit.add(Restrictions.eq("profissional", profissional));
		crit.add(Restrictions.ne("situacao.descricao", "Cancelado(a)"));
		
		List<GuiaConsultaUrgencia> consultas = crit.list();
		
		Boolean isIgualAZero = ((consultas.size())==0);

		Assert.isTrue(isIgualAZero, MensagemErroEnum.BENEFICIARIO_JA_REALIZOU_CONSULTA_COM_PROFISSIONAL_ODONTO.getMessage(segurado.getPessoaFisica().getNome(), profissional.getNome()));
	}
	
	
	public void onAfterCriarGuia(G guia, Collection<? extends ProcedimentoInterface> procedimentos) throws Exception {};	

	public void onSalvarGuia(GuiaSimples guia) throws Exception {}

}