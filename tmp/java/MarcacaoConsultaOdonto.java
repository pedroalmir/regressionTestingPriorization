package br.com.infowaypi.ecare.services.odonto;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaOdonto;
import br.com.infowaypi.ecarebc.atendimentos.enums.TipoConsultaEnum;
import br.com.infowaypi.ecarebc.atendimentos.validators.ValidateProcedimentoEnum;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.ecarebc.service.odonto.MarcacaoConsultaOdontoService;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/*if[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO]
import br.com.infowaypi.ecare.services.ValidacaoAtendimentoSegmentacaoAssistencial;
import br.com.infowaypi.ecarebc.produto.SegmentacaoAssistencialEnum;
end[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */

/**
 * Service para registrar uma consulta odontológica no sistema.
 * 
 * <br>
 * Exemplo de uso:
 * 
 * <pre>
 * 
 *    
 * </pre>
 * 
 * <br>
 * Limitações:
 * 
 * @author Danilo Nogueira Portela
 * @version
 * @see br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaOdonto
 */
public class MarcacaoConsultaOdonto extends MarcacaoConsultaOdontoService<Segurado> {

	private static final int DIAS_INTERVALO_GUIA_ODONTOLOGIA_GERAL_ANTERIOR = 180;
	private Especialidade especialidadeOdontologiaGeral;

	public ResumoSegurados buscarSegurado(String cpfDoTitular, String numeroDoCartao) throws Exception {
		/*if[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO]
		ResumoSegurados resumo = ValidacaoAtendimentoSegmentacaoAssistencial.buscar(cpfDoTitular, numeroDoCartao, Segurado.class, false, SegmentacaoAssistencialEnum.ODONTOLOGICO.getSegmentacaoAssistencial());
		else[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */
		ResumoSegurados resumo = BuscarSegurados.buscar(cpfDoTitular, numeroDoCartao,Segurado.class);
		/* end[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */
		return resumo;
	}

	public GuiaConsultaOdonto criarGuiaPrestador(Segurado segurado, Prestador prestador, UsuarioInterface usuario,
			Profissional profissional, Especialidade especialidade) throws Exception {
		Assert.isNotNull(especialidade, "Caro Usuário, informe a Especialidade da Consulta para poder prosseguir.");
		
		especialidadeOdontologiaGeral = getEspecialidadeOdontologiaGeral();
		
		if (especialidade != especialidadeOdontologiaGeral ) { //valida consulta geral previa
		    validaBeneficiarioPossuiGuiaOdontologiaGeralAnterior(segurado);
		}
		
		GuiaConsultaOdonto guiaGerada = super.criarGuiaPrestador(segurado, prestador, usuario, profissional, especialidade);
		
		for (Procedimento consulta : guiaGerada.getProcedimentos()) {
			ValidateProcedimentoEnum.PROCEDIMENTO_CARENCIA_VALIDATOR.getValidator().execute(consulta, guiaGerada);
		}
		
		super.consumirConsultaPromocional(guiaGerada,usuario);
		
		guiaGerada.recalcularValores();
		return guiaGerada;
	}
	
	
	/**
	 * Verifica se beneficiario possui consulta odontologica previamente CONFIRMADA com especialidade 
	 * ODONTOLOGIA GERAL de acordo com o prazo determinado em DIAS_INTERVALO_GUIA_ODONTOLOGIA_GERAL_ANTERIOR.
	 * 
	 * @param segurado segurado da guia
	 */
	private void validaBeneficiarioPossuiGuiaOdontologiaGeralAnterior(SeguradoInterface segurado) {

	    String[] situacoes = {SituacaoEnum.CONFIRMADO.descricao(),SituacaoEnum.FATURADA.descricao(),SituacaoEnum.PAGO.descricao()};
	    
	    Criteria criteria = HibernateUtil.currentSession().createCriteria(GuiaConsultaOdonto.class)
			.add(Restrictions.eq("segurado", segurado))
			.add(Restrictions.between("dataAtendimento", DateUtils.addDays(new Date(), -DIAS_INTERVALO_GUIA_ODONTOLOGIA_GERAL_ANTERIOR), new Date()))
			.createAlias("profissional.especialidades","especialidade")
			.add(Restrictions.eq("especialidade", especialidadeOdontologiaGeral))
			.add(Restrictions.in("situacao.descricao", situacoes))
			;

	    @SuppressWarnings("unchecked")
	    List<GuiaConsultaOdonto> consultas = criteria.list();

	    Boolean possuiConsultaOdontoGeralAnterior = ((consultas.size())!=0);
	    
	    Assert.isTrue(possuiConsultaOdontoGeralAnterior, MensagemErroEnum.BENEFICIARIO_NAO_POSSUI_CONSULTA_ODONTO_ANTERIOR.getMessage(
		    segurado.getPessoaFisica().getNome(), especialidadeOdontologiaGeral.getDescricao(), Integer.toString(DIAS_INTERVALO_GUIA_ODONTOLOGIA_GERAL_ANTERIOR)));
	}

	@Override
	public GuiaConsultaOdonto criarGuiaPrestador(Segurado segurado, Prestador prestador, UsuarioInterface usuario,
			Profissional profissional, TipoConsultaEnum tipoConsulta) throws Exception {
		Assert.isNotNull(tipoConsulta, "Caro Usuário, informe o Tipo de Consulta para poder prosseguir.");
		GuiaConsultaOdonto guiaGerada = super.criarGuiaPrestador(segurado, prestador, usuario, profissional, tipoConsulta);
		super.consumirConsultaPromocional(guiaGerada,usuario);

		guiaGerada.recalcularValores();
		return guiaGerada;
	}

	@Override
	public GuiaConsultaOdonto criarGuiaMarcador(Segurado segurado, UsuarioInterface usuario) throws Exception {
		GuiaConsultaOdonto guiaGerada = super.criarGuiaMarcador(segurado, usuario);
		super.consumirConsultaPromocional(guiaGerada,usuario);
		return guiaGerada;
	}

	@Override
	public GuiaConsultaOdonto criarGuiaLancamento(Segurado segurado, Boolean ignorarValidacao, Prestador prestador, Profissional profissional, 
			UsuarioInterface usuario) throws Exception {

		GuiaConsultaOdonto guiaGerada = super.criarGuiaLancamento(segurado, ignorarValidacao, prestador, profissional, usuario);
		super.consumirConsultaPromocional(guiaGerada,usuario);

		return guiaGerada;

	}
 
}
