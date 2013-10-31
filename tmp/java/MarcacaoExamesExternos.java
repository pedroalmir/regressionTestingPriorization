package br.com.infowaypi.ecare.services.exame;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecare.validacao.services.InclusaoProcedimentoSituacaoGuiaValidator;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.exceptions.BeneficiarioCanceladoException;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.service.SolicitarInclusaoProcedimento;
import br.com.infowaypi.ecarebc.service.exames.MarcacaoExameService;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * 
 * @author Idelvane
 *
 */
@SuppressWarnings("unchecked")
public class MarcacaoExamesExternos extends SolicitarInclusaoProcedimento{
	
	public GuiaSimples buscarGuiasExternas(String autorizacao, Prestador prestador) throws Exception{
		GuiaSimples guia = super.buscarGuiasExternas(autorizacao, prestador);

		boolean geraExameExterno = guia.isGeraExameExterno();
		boolean geraExameExternoParaPacientesCronicos = guia.isGeraExameExternoParaPacientesCronicos();
		if (geraExameExternoParaPacientesCronicos) {
			if (!geraExameExterno) {
				Assert.isTrue(prestador.isAtendePacientesCronicos(), "Apenas prestadores que atendem pacientes crônicos podem registrar exames externos para guias do tipo " + guia.getTipo());
			}
		} else {
			throw new RuntimeException("Esta guia não gera exames externos.");
		}
		
		validaSituacoes(guia, prestador);
		return guia;
	}
	
	public GuiaSimples criarGuiaExterna(Prestador prestador,
			Collection<Procedimento> procedimentos, GuiaSimples guia,
			UsuarioInterface usuario) throws Exception {

		Assert.isNotEquals(prestador, guia.getPrestador(), "Guias de exames externos devem ser geradas para um prestador diferente.");
		Assert.isNotEmpty(procedimentos, "Devem ser adicionados procedimentos ambulatoriais.");

		guia.tocarObjetos();
		for (Procedimento procedimento : procedimentos) {
			if(procedimento.getProcedimentoDaTabelaCBHPM().getEspecial()){
				boolean isPrestadorHemopi = prestador.getIdPrestador().equals(Prestador.HEMOPI);
				boolean isPossuiAcordo = prestador.isPossuiAcordo(procedimento.getProcedimentoDaTabelaCBHPM());
				
				if(!(isPrestadorHemopi && isPossuiAcordo)) {
					throw new RuntimeException(MensagemErroEnum.ERRO_EXAME_ESPECIAL_EM_GUIA_EXTERNA.getMessage(procedimento.getProcedimentoDaTabelaCBHPM().getCodigo()));
				}
			}
		}

		return this.criarGuiaExameExterno(prestador, guia, procedimentos, usuario);
	}
	
	
	private GuiaSimples criarGuiaExameExterno(Prestador prestador,
			GuiaSimples guia, Collection<Procedimento> procedimentos,
			UsuarioInterface usuario) throws Exception {

		MarcacaoExameService service = new MarcacaoExameService();
		service.setIgnoreValidacao(true);
		service.setInternacao(true);
		
		List<Procedimento> procedimentosExames = new ArrayList<Procedimento>();
		procedimentosExames.addAll(procedimentos);
		
		GuiaExame exame = service.criarGuiaPrestador(guia.getSegurado(), prestador, usuario, guia.getProfissional(), procedimentosExames);
		exame.setGuiaOrigem(guia);
		exame.setExameExterno(true);
		
		if (guia.isInternacao() || guia.isExame()) {
			exame.setSolicitante(guia.getSolicitante());
		} else {
			exame.setSolicitante(guia.getProfissional());
		}
		try {
			exame.validate();
		} catch (BeneficiarioCanceladoException e) {
			if(!usuario.getLogin().equals("hemope")) {
				throw e;
			}
			
			Date dataCancelamentoBeneficiario = guia.getSegurado().getSituacao().getDataSituacao();
			Calendar dataLimite = Calendar.getInstance();
			dataLimite.add(Calendar.DAY_OF_MONTH, -30);
			
			boolean canceladoHaMaisDe30Dias = (Utils.compareData(dataCancelamentoBeneficiario, dataLimite.getTime()) < 0);
			if (canceladoHaMaisDe30Dias) {
				throw e;
			}
		}
		
		exame.recalcularValores();
		
		guia.getGuiasFilhas().add(exame);
		
		return exame;
	}

	private void validaSituacoes(GuiaSimples guia, Prestador prestador) {
		
		if (guia.isConsultaUrgencia() || guia.isAtendimentoUrgencia()) {
			SituacaoInterface dataAtendimento = guia.getSituacao(SituacaoEnum.ABERTO.descricao());
			Assert.isFalse(isUltrapassouPrazoDeSolicitacao(dataAtendimento.getData()), "O prazo para solicitar exames externos a partir de uma consulta de urgência é de " + PRAZO_URGENCIA + " horas");
		}
		
		boolean isHemopi = prestador.getIdPrestador().equals(Prestador.HEMOPI);
		new InclusaoProcedimentoSituacaoGuiaValidator(guia, isHemopi);
	}

	private boolean isUltrapassouPrazoDeSolicitacao(Date atendimento) {
		Calendar dataAtendimento = Utils.createCalendarFromDate(atendimento);
		Calendar newDate = Calendar.getInstance();
		int diferencaEmHoras = Utils.diferencaEmHoras(dataAtendimento, newDate);
		return (diferencaEmHoras > PRAZO_URGENCIA);
	}
}
