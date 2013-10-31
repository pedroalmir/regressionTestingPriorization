package br.com.infowaypi.ecare.services.consultas;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import br.com.infowaypi.ecare.constantes.Constantes;
import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsulta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.odonto.EstruturaOdonto;
import br.com.infowaypi.ecarebc.odonto.Odontograma;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.ecarebc.service.consultas.ConfirmacaoConsultaService;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.molecular.parameter.LowerEquals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.sensews.client.SenseManager;

/*if[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO]
import br.com.infowaypi.ecare.services.ValidacaoAtendimentoSegmentacaoAssistencial;
import br.com.infowaypi.ecarebc.produto.SegmentacaoAssistencialEnum;
end[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */

/* if[ATUALIZAR_ODONTOGRAMA_NA_CONFIRMACAO_DE_CONSULTA]	
import org.hibernate.FlushMode;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
end[ATUALIZAR_ODONTOGRAMA_NA_CONFIRMACAO_DE_CONSULTA]*/

public class ConfirmacaoConsulta extends ConfirmacaoConsultaService<Segurado> {
	
	public ResumoSegurados buscarSegurados(String cpfDoTitular,String numeroDoCartao) throws ValidateException {
		/*if[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO]
		ResumoSegurados resumo = ValidacaoAtendimentoSegmentacaoAssistencial.buscar(cpfDoTitular, numeroDoCartao, Segurado.class, false, SegmentacaoAssistencialEnum.ODONTOLOGICO.getSegmentacaoAssistencial());
		else[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */
		ResumoSegurados resumo = BuscarSegurados.buscar(cpfDoTitular, numeroDoCartao, Segurado.class);
		/*end[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */
		return resumo;
	}
	
	public ResumoGuias selecionarSegurado(Prestador prestador,Segurado segurado) {
		List<GuiaConsulta> guias = new Service().buscarGuiasPorPeriodo(prestador, segurado, true, 30, GuiaConsulta.class, SituacaoEnum.AGENDADA);

		Assert.isNotEmpty(guias, "Não foram encontradas guias deste segurado para serem confirmadas.");
		
		ResumoGuias resumoGuia = new ResumoGuias<GuiaConsulta>(guias, ResumoGuias.SITUACAO_AGENDADA, false);
		resumoGuia.setPrestador(prestador);
		
		return resumoGuia;
	}
	
	public <G extends GuiaSimples> G selecionarGuia(G guia) throws Exception {
		super.selecionarGuia(guia);
		guia.setDataAtendimento(new Date());
		guia.setDataTerminoAtendimento(guia.getDataAtendimento());
		guia.getSegurado().getConsultasPromocionais().size();
		
		/* if[ATUALIZAR_ODONTOGRAMA_NA_CONFIRMACAO_DE_CONSULTA]	
		AbstractSegurado segurado = guia.getSegurado();
		if(segurado.getOdontogramaCompleto() == null){
			HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
			Odontograma<EstruturaOdonto> odontograma = new Odontograma<EstruturaOdonto>();
			odontograma.setBeneficiario(segurado);
			segurado.setOdontogramaCompleto(odontograma);
			odontograma.construirOdontogramaDente();
		}
		end[ATUALIZAR_ODONTOGRAMA_NA_CONFIRMACAO_DE_CONSULTA]*/
		
		return guia;
	}
	
	public ResumoGuias<GuiaConsulta> buscarGuiasConfirmacao(String cpfDoTitular,String numeroDoCartao,  Prestador prestador) throws Exception {
		ResumoSegurados resumo = BuscarSegurados.buscar(cpfDoTitular,numeroDoCartao, Segurado.class);
		return buscarGuiasConfirmacao(resumo.getSegurados(),prestador);
		
	}
	
	public <G extends GuiaSimples> G atualizarOdontograma(G guia, Odontograma<EstruturaOdonto> odontograma) throws Exception {
		return guia;
	}
	
	@Override
	public void confirmarGuiaDeConsulta(GuiaConsulta<Procedimento> guia, UsuarioInterface usuario) throws Exception {
		super.consumirConsultaPromocional(guia,usuario);

		for (Procedimento procedimento : guia.getProcedimentos()) {
			if(!geraCoParticipacao(procedimento)){
				procedimento.setGeraCoParticipacao(false);
				procedimento.setValorCoParticipacao(BigDecimal.ZERO);
			}
		}
		
		guia.updateValorCoparticipacao();
		/* if[ATUALIZAR_ODONTOGRAMA_NA_CONFIRMACAO_DE_CONSULTA]	
		HibernateUtil.currentSession().saveOrUpdate(guia.getSegurado().getOdontogramaCompleto());
		end[ATUALIZAR_ODONTOGRAMA_NA_CONFIRMACAO_DE_CONSULTA]*/
		super.confirmarGuiaDeConsulta(guia, usuario);
		/* if[SENSE_MANAGER] */
		SenseManager.CONFIRMACAO_CONSULTA.analisar(guia);
		/* end[SENSE_MANAGER] */
	}
	
	/**
	 * @param guia
	 * @return
	 */
	public boolean geraCoParticipacao(Procedimento procedimento){

		String especialidade = procedimento.getGuia().getEspecialidade().getDescricao();
		String situacoes[] = {Constantes.SITUACAO_CONFIRMADO, Constantes.SITUACAO_FATURADA};
		int idade = procedimento.getGuia().getSegurado().getPessoaFisica().getIdade();
		int quantGuias = 0;
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("segurado", procedimento.getGuia().getSegurado()));
		sa.addParameter(new In("situacao.descricao",situacoes));
		
		Calendar cal = new GregorianCalendar();
		Date dataAtual = new Date();
		if(especialidade.equals("Pediatria")){
			sa.addParameter(new Equals("especialidade", procedimento.getGuia().getEspecialidade()));
			if(idade <= 2){
				cal.add(Calendar.DAY_OF_MONTH, -30);
				sa.addParameter(new GreaterEquals("dataAtendimento", cal.getTime()));
				sa.addParameter(new LowerEquals("dataAtendimento", dataAtual));
				quantGuias = sa.resultCount(GuiaSimples.class);
				if(quantGuias == 0){
					return false;
				}
			}
			else if((idade >= 2) && (idade <= 10)){
				cal.add(Calendar.DAY_OF_MONTH, -180);
				sa.addParameter(new GreaterEquals("dataAtendimento", cal.getTime()));
				sa.addParameter(new LowerEquals("dataAtendimento", dataAtual));
				quantGuias = sa.resultCount(GuiaSimples.class);
				if(quantGuias < 2){
					return false;
				}
			}
			else if((idade > 10) && (idade <= 18)){
				cal.add(Calendar.DAY_OF_MONTH, -180);
				sa.addParameter(new GreaterEquals("dataAtendimento", cal.getTime()));
				sa.addParameter(new LowerEquals("dataAtendimento", dataAtual));
				quantGuias = sa.resultCount(GuiaSimples.class);
				if(quantGuias == 0){
					return false;
				}
			}

		}else if(especialidade.equals("Ginecologia")){
			cal.add(Calendar.DAY_OF_MONTH, -180);
			sa.addParameter(new Equals("especialidade", procedimento.getGuia().getEspecialidade()));
			sa.addParameter(new GreaterEquals("dataAtendimento", cal.getTime()));
			sa.addParameter(new LowerEquals("dataAtendimento", dataAtual));
			quantGuias = sa.resultCount(GuiaSimples.class);
			if(quantGuias == 0){
				return false;
			}
		}
		return true;
	}
	
}