package br.com.infowaypi.ecare.services.odonto;

import java.util.Collection;

import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecare.services.Service;
import br.com.infowaypi.ecare.utils.ProfissionalUtils;
import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCirurgiaOdonto;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.atendimentos.enums.TipoCriticaEnum;
import br.com.infowaypi.ecarebc.atendimentos.validators.ManagerCritica;
import br.com.infowaypi.ecarebc.atendimentos.validators.ServiceApresentacaoCriticasFiltradas;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.CID;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.service.odonto.MarcacaoCirurgiaOdontoService;
import br.com.infowaypi.ecarebc.utils.ProcedimentoUtils;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/*if[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO]
import br.com.infowaypi.ecare.services.ValidacaoAtendimentoSegmentacaoAssistencial;
import br.com.infowaypi.ecarebc.produto.SegmentacaoAssistencialEnum;
/* end[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */

/**
 * Service para a soplicitação de cirurgias odontológicas
 * @author Danilo Nogueira Portela
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class MarcacaoCirurgiaOdonto extends MarcacaoCirurgiaOdontoService<Segurado> implements ServiceApresentacaoCriticasFiltradas{

	public ResumoSegurados buscarSegurado(String cpfDoTitular, String numeroDoCartao) throws Exception {
		/*if[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO]
		ResumoSegurados resumo = ValidacaoAtendimentoSegmentacaoAssistencial.buscar(cpfDoTitular, numeroDoCartao, Segurado.class, false, SegmentacaoAssistencialEnum.ODONTOLOGICO.getSegmentacaoAssistencial());
		else[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */
		ResumoSegurados resumo = BuscarSegurados.buscar(cpfDoTitular, numeroDoCartao, Segurado.class);
		/* end[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */
		
		return resumo;
	}

	public GuiaCirurgiaOdonto criarGuiaCirurgia(Segurado segurado, Prestador prestador, UsuarioInterface usuario, Profissional solicitanteCRM, 
			Profissional solicitanteNOME, Especialidade especialidade, Collection<CID> cids, String justificativa) throws Exception {

		Assert.isNotNull(segurado, MensagemErroEnum.SEGURADO_NAO_INFORMADO.getMessage());
		Profissional profissional = ProfissionalUtils.getProfissionais(solicitanteCRM, solicitanteNOME);

		GuiaCirurgiaOdonto guia = super.criarGuiaCirurgia(segurado, null, prestador, usuario, profissional, especialidade, cids, justificativa);
		// guia.addItensDiaria(itensDiarias);

		if (segurado.getDataAdesao() == null)
			throw new RuntimeException(MensagemErroEnumSR.SEGURADO_SEM_DATA_DE_ADESAO.getMessage());

		if (segurado.getSituacao().getDescricao().equals(SituacaoEnum.SUSPENSO) && segurado.getSituacao().getMotivo().equals("Não recadastrado")) {
			throw new ValidateException(MensagemErroEnumSR.SEGURADO_NAO_RECADASTRADO.getMessage());
		}

		filtrarCriticasApresentaveis(guia);
		
		return guia;
	}

	public <P extends Procedimento> void onAfterCriarGuia(GuiaCirurgiaOdonto guia, Collection<P> procedimentos) throws Exception {
		Service.verificaRecadastramento(guia);
		super.onAfterCriarGuia(guia, procedimentos);
	}

	public GuiaCirurgiaOdonto addProcedimentosAuditoria(GuiaCirurgiaOdonto guia, UsuarioInterface usuario, Collection<ItemDiaria> diarias, 
			Collection<ProcedimentoCirurgico> procedimentosCirurgicos, Collection<ItemPacote> pacotes) throws Exception {

		GuiaCirurgiaOdonto guiaAuditada = super.addProcedimentos(guia, usuario, diarias, procedimentosCirurgicos, pacotes);
		ProcedimentoUtils.aplicaDescontoDaViaDeAcesso(guiaAuditada);//guiaauditada ou guia?
		guiaAuditada.recalcularValores();
		
		filtrarCriticasApresentaveis(guia);
		
		return guiaAuditada;
	}

	public void salvarGuia(GuiaSimples guia) throws Exception {
		processaSituacaoCriticas(guia);
		super.salvarGuia(guia);
	}

	@Override
	public void filtrarCriticasApresentaveis(GuiaSimples<?> guia) {
		ManagerCritica.processaApresentaveis(guia, TipoCriticaEnum.CRITICA_DLP_SUBGRUPO.valor(), TipoCriticaEnum.CRITICA_DLP_CID.valor());
	}

	@Override
	public void processaSituacaoCriticas(GuiaSimples<?> guia) {
		ManagerCritica.processaSituacao(guia);
	}

}
