package br.com.infowaypi.ecare.services.odonto;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaOdonto;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaOdontoUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaUrgencia;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.ecarebc.service.odonto.MarcacaoConsultaOdontoUrgenciaService;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/*if[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO]
import br.com.infowaypi.ecare.services.ValidacaoAtendimentoSegmentacaoAssistencial;
import br.com.infowaypi.ecarebc.produto.SegmentacaoAssistencialEnum;
/* end[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */

/** 
 * Service proxy responsavel pela marcação de uma guia odontológica de urgência.
 * @author Marcus bOolean
 *
 */
public class MarcacaoConsultaOdontoUrgencia extends MarcacaoConsultaOdontoUrgenciaService {

	public ResumoSegurados buscarSegurado(String cpfDoTitular, String numeroDoCartao) throws Exception {
		/*if[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO]
		ResumoSegurados resumo = ValidacaoAtendimentoSegmentacaoAssistencial.buscar(cpfDoTitular, numeroDoCartao, Segurado.class, false, SegmentacaoAssistencialEnum.ODONTOLOGICO.getSegmentacaoAssistencial());
		else[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */
		ResumoSegurados resumo = BuscarSegurados.buscar(cpfDoTitular, numeroDoCartao,Segurado.class);
		/* end[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */
		
		return resumo;
	}

	public GuiaConsultaOdontoUrgencia criarGuiaOdontoUrgenciaPrestador(AbstractSegurado segurado, Prestador prestador, UsuarioInterface usuario, Profissional profissional) throws Exception {
		//verifcar a existencia de consulta de urgencia com este profissional nos ultimos 30 dias para este beneficiário.
		verificaValidadeConsultaOdontoDeUrgencia(segurado, profissional);
		return super.criarGuiaOdontoUrgenciaPrestador(segurado, prestador, usuario, profissional, null);
	}

	/** Este método proíbe que o usuário realiza uma consulta de urgência e uma eletiva com o mesmo profissional 
	 *  no intervalo de 30 dias
	 * @param segurado
	 * @param profissional
	 */
	@SuppressWarnings("unchecked")
	public void verificaValidadeConsultaOdontoDeUrgencia(SeguradoInterface segurado, Profissional profissional) {

		Criteria crit = HibernateUtil.currentSession().createCriteria(GuiaConsultaOdonto.class);
		crit.add(Restrictions.eq("segurado", segurado));
		crit.add(Restrictions.between("dataMarcacao", DateUtils.addDays(new Date(), -30), new Date()));
		crit.add(Restrictions.eq("profissional", profissional));
		crit.add(Restrictions.ne("situacao.descricao", "Cancelado(a)"));

		List<GuiaConsultaUrgencia> consultas = crit.list();

		Boolean isIgualAZero = ((consultas.size())==0);

		Assert.isTrue(isIgualAZero, MensagemErroEnum.BENEFICIARIO_JA_REALIZOU_CONSULTA_COM_PROFISSIONAL_ODONTO.getMessage(segurado.getPessoaFisica().getNome(), profissional.getNome()));
	}

}
