package br.com.infowaypi.ecarebc.atendimentos.validators;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecarebc.atendimentos.GuiaConsulta;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.Role;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.molecular.parameter.NotEquals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe para validação de Periodicidade em guias de consulta
 * @author root
 * @changes Danilo Nogueira Portela, Idelvane 
 */
@SuppressWarnings("unchecked")
public class ConsultaPeriodicidadeValidator<G extends GuiaConsulta> extends AbstractGuiaValidator<G> {

	public boolean templateValidator(G guia) throws ValidateException {

		if (guia.getUsuarioDoFluxo()!= null && guia.getUsuarioDoFluxo().getRole().equals(Role.AUDITOR.getValor())){
			return true;
		}	
			
		TabelaCBHPM cbhpm = ((ProcedimentoInterface) guia.getProcedimentos().iterator().next()).getProcedimentoDaTabelaCBHPM();

		int periodo = cbhpm.getPeriodicidade();
		Collection<GuiaConsulta> consultasDoSegurado = buscarGuiasDoSeguradoPorPeriodo(guia, cbhpm);
		
		if(consultasDoSegurado.isEmpty()){
			return true;
		}
		
		boolean isAgendaPeloAuditor = isAgendadaPorSuperUsuario(guia);
		
		for (GuiaConsulta<Procedimento> g : consultasDoSegurado) {
			boolean isEspecialidadeEquals 	= false;
			boolean isProcedimentoEquals 	= false;
			
			g.getProcedimentos().iterator();
			
			if(g.getEspecialidade() != null && guia.getEspecialidade() != null){
				isEspecialidadeEquals = g.getEspecialidade().equals(guia.getEspecialidade());
			}
			if(g.getProcedimentos().iterator().next().getProcedimentoDaTabelaCBHPM().equals(cbhpm)){
				isProcedimentoEquals = g.getEspecialidade().equals(guia.getEspecialidade());
			}
			
			if(isEspecialidadeEquals && isProcedimentoEquals && !isAgendaPeloAuditor){
				Date dataUltimaMarcacao = g.getDataAtendimento();
				Calendar dataVencimentoPeriodicidade = Calendar.getInstance();
				dataVencimentoPeriodicidade.setTime(dataUltimaMarcacao);
				dataVencimentoPeriodicidade.add(Calendar.DAY_OF_MONTH, periodo + 1);
				
				if (Utils.compareData(dataVencimentoPeriodicidade.getTime(),guia.getDataAtendimento()) > 0){
					throw new ValidateException(MensagemErroEnum.CONSULTA_NAO_CUMPRIU_PERIODICIDADE.
								getMessage(g.getEspecialidade().getDescricao(), Utils.format(dataUltimaMarcacao), 
											Utils.format(dataVencimentoPeriodicidade.getTime())));
				}
			}
		}
		return true;
	}

	private boolean isAgendadaPorSuperUsuario(GuiaConsulta<Procedimento> g) {
		SituacaoInterface situacao = g.getSituacao(SituacaoEnum.AGENDADA.descricao());
		UsuarioInterface usuario = null;
		
		if(situacao != null){
			usuario = situacao.getUsuario();

			if (usuario.isPossuiRole(UsuarioInterface.ROLE_ROOT) 
					|| usuario.isPossuiRole(UsuarioInterface.ROLE_AUDITOR)
					|| usuario.isPossuiRole(UsuarioInterface.ROLE_AUDITOR_ODONTO)
					|| usuario.isPossuiRole(UsuarioInterface.ROLE_DIRETORIA_MEDICA)){
				return true;
			}
		}
		
		 
		return false;
	}
	
	public  List<GuiaConsulta> buscarGuiasDoSeguradoPorPeriodo(G guia, TabelaCBHPM cbhpm){
		SearchAgent sa = new SearchAgent();
		
		sa.addParameter(new GreaterEquals("dataAtendimento",Utils.incrementaDias(guia.getDataAtendimento(), -cbhpm.getPeriodicidade())));
		sa.addParameter(new Equals("segurado", guia.getSegurado()));
		sa.addParameter(new NotEquals("situacao.descricao", SituacaoEnum.REALIZADO.descricao()));
		sa.addParameter(new NotEquals("situacao.descricao", SituacaoEnum.CANCELADO.descricao()));
		sa.addParameter(new NotEquals("situacao.descricao", SituacaoEnum.INAPTO.descricao()));
		
		if(guia.getIdGuia() != null){
			sa.addParameter(new NotEquals("idGuia", guia.getIdGuia()));
		}
		
		return sa.list(GuiaConsulta.class);
	}
	
}
