package br.com.infowaypi.ecarebc.service;

import java.util.Calendar;
import java.util.Collection;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.service.exames.MarcacaoExameService;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Service para solicitação de exames especiais no plano de saúde
 * @author Marcus bOolean
 */
public class SolicitarExamesEspeciaisService extends MarcacaoExameService {
	public static final Integer TIPO_EXTERNO = 1;
	public static final Integer TIPO_INTERNO = 2;

	public SolicitarExamesEspeciaisService() {
		super();
	}

	public GuiaSimples adicionarProcedimentos(Integer tipoDeSolicitacao,Collection<Procedimento> examesEspeciais, GuiaCompleta guia,
			UsuarioInterface usuario) throws Exception {

		if (tipoDeSolicitacao.equals(TIPO_INTERNO)) {
			for (ProcedimentoInterface procedimento : examesEspeciais) {
				guia.addProcedimento(procedimento);
			}
			
			return guia;
		} else {
			
			if(guia.isAtendimentoUrgencia() || guia.isConsultaUrgencia()){
				Calendar hoje = Calendar.getInstance();
				Calendar dataLimiteSolicitacao = Calendar.getInstance();
				dataLimiteSolicitacao.setTime(guia.getSituacao(SituacaoEnum.ABERTO.descricao()).getDataSituacao());
				dataLimiteSolicitacao.add(Calendar.HOUR, GuiaExame.PRAZO_SOLICITACAO_EXAMES_URGENCIA);
				if(hoje.after(dataLimiteSolicitacao)){
					throw new RuntimeException("O prazo para solicitação de exames expirou. Exames tem um prazo de 12 horas após a início do atendimento para serem solicitados. "+Utils.format(dataLimiteSolicitacao.getTime(), "dd/MM/yyyy HH:mm"));
				}
			}
			
			GuiaSimples guiaFilha = criarGuiaExterna(guia.getSegurado(), true, null, guia.getProfissional(), examesEspeciais, usuario);
			guia.addGuiaFilha(guiaFilha);
			guiaFilha.validate();
			
			return guiaFilha;
		}
	}

	@Override
	public void onAfterCriarGuia(GuiaExame<ProcedimentoInterface> guia,
			Collection<? extends ProcedimentoInterface> procedimentos) throws Exception {

		for (ProcedimentoInterface procedimento : procedimentos) {
			procedimento.calcularCampos();
//			((Procedimento)procedimento).calcularCoParticipacao(guia.getPrestador());
			guia.addProcedimento(procedimento);
		}
	}

}
