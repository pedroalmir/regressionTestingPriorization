	package br.com.infowaypi.ecarebc.service.odonto;

import java.math.BigDecimal;
import java.util.Date;

import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaOdontoUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.Observacao;
import br.com.infowaypi.ecarebc.atendimentos.acordos.AcordoCBHPM;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.ecarebc.service.Agente;
import br.com.infowaypi.ecarebc.service.MarcacaoService;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;
 
/**
 * Service reponsável pela marcação de uma consulta odontológica de urgencia no prestador
 * @author Marcus bOolean
 */
public class MarcacaoConsultaOdontoUrgenciaService extends MarcacaoService<GuiaConsultaOdontoUrgencia> {
	
	public static final String CODIGO_PROCEDIMENTO_CONSULTA_DE_URGENCIA = "99000003";
	
	public MarcacaoConsultaOdontoUrgenciaService(){
		super();
	}
	
	public GuiaConsultaOdontoUrgencia criarGuiaOdontoUrgenciaPrestador(AbstractSegurado segurado, Prestador prestador, UsuarioInterface usuario, Profissional profissional, String observacao) throws Exception {
		
		Especialidade especialidadeOdonto = this.getEspecialidadeOdonto();
		
		Boolean isProfissionalOdonto =  profissional.getEspecialidades().contains(especialidadeOdonto);
		Boolean isPrestadorOdonto =  prestador.getEspecialidades().contains(especialidadeOdonto);
		
		Assert.isTrue(isProfissionalOdonto, MensagemErroEnum.PROFISSIONAL_NAO_ATENDE_ESPECIALIDADE.getMessage("Odontologia"));
		Assert.isTrue(isPrestadorOdonto, MensagemErroEnum.PRESTADOR_NAO_ATENDE_ESPECIALIDADE.getMessage("Odontologia"));
		
		GuiaConsultaOdontoUrgencia guiaGerada = criarGuia(segurado, prestador, usuario, null, profissional, especialidadeOdonto, null, Utils.format(new Date()), 
				MotivoEnum.CONFIRMADA_NO_PRESTADOR.getMessage(), Agente.PRESTADOR);
		
		if (observacao != null){
			Observacao obs = new Observacao(observacao, usuario);
			guiaGerada.addObservacao(obs);
		}
		//procura nos acordos do prestador, se ele possui acordo pro procedimento consulta, se tiver, o valor cobrado pelo procedimento será o do acordo.
		for (AcordoCBHPM acordo : guiaGerada.getPrestador().getAcordosCBHPMAtivos()) {
			if(acordo.getTabelaCBHPM().getCodigo().equals(CODIGO_PROCEDIMENTO_CONSULTA_DE_URGENCIA)) {
				// a guia é gerada com apenas 1 procedimento de consulta de urgencia na situacao Confirmado
				guiaGerada.getProcedimentosConfirmados().get(0).setValorAtualDoProcedimento(acordo.getValor());
				guiaGerada.getProcedimentosConfirmados().get(0).setValorCoParticipacao(BigDecimal.ZERO);
				guiaGerada.getProcedimentosConfirmados().get(0).calcularCampos();
			}
		}
		
		guiaGerada.recalcularValores();
		guiaGerada.setDataTerminoAtendimento(guiaGerada.getDataAtendimento());
		return guiaGerada;
	}

	@Override
	public GuiaConsultaOdontoUrgencia getGuiaInstanceFor(UsuarioInterface usuario) {
		GuiaConsultaOdontoUrgencia guia = new GuiaConsultaOdontoUrgencia(usuario);
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("codigo", CODIGO_PROCEDIMENTO_CONSULTA_DE_URGENCIA));
		
		TabelaCBHPM procedimentoDaTabela = sa.uniqueResult(TabelaCBHPM.class);
		Assert.isNotNull(procedimentoDaTabela, "Procedimento de consulta odontológica de urgência não encontrado.");
		
		Procedimento procedimento = new Procedimento();
		procedimento.setProcedimentoDaTabelaCBHPM(procedimentoDaTabela);
		procedimento.setValorAtualDoProcedimento(procedimentoDaTabela.getValorModerado());
		procedimento.setValorCoParticipacao(BigDecimal.ZERO);
		procedimento.calcularCampos();
		procedimento.mudarSituacao(usuario, SituacaoEnum.CONFIRMADO.descricao(), MotivoEnum.CONFIRMADA_NO_PRESTADOR.getMessage(), new Date());
		
		guia.getProcedimentos().add(procedimento);
		procedimento.setGuia(guia);
		guia.recalcularValores();
		return guia;
	}
	
}
