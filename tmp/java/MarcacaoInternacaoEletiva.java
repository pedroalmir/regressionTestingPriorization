package br.com.infowaypi.ecare.services.internacao;

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
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacaoEletiva;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.CID;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.service.internacao.MarcacaoInternacaoEletivaService;
import br.com.infowaypi.ecarebc.utils.ProcedimentoUtils;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

public class MarcacaoInternacaoEletiva extends MarcacaoInternacaoEletivaService<Segurado> {
	
	public ResumoSegurados buscarSegurado(String cpfDoTitular, String numeroDoCartao) throws Exception {
		return BuscarSegurados.buscar(cpfDoTitular, numeroDoCartao, Segurado.class);
	}
	
	
	public GuiaInternacaoEletiva criarGuiaEletiva(Segurado segurado,
			Integer tipoTratamento, Prestador prestador, UsuarioInterface usuario,Profissional solicitanteCRM, Profissional solicitanteNOME, 
			Especialidade especialidade, Collection<CID> cids, String justificativa) throws Exception {
		
		Assert.isNotNull(segurado, MensagemErroEnum.SEGURADO_NAO_INFORMADO.getMessage());
		Profissional profissional = ProfissionalUtils.getProfissionais(solicitanteCRM, solicitanteNOME);
		
		GuiaInternacaoEletiva guia  = super.criarGuiaEletiva(segurado, tipoTratamento, null, prestador, usuario, profissional, especialidade, cids, justificativa); 
//		guia.addItensDiaria(itensDiarias);
		
		if(segurado.getDataAdesao() == null)
			throw new RuntimeException(MensagemErroEnumSR.SEGURADO_SEM_DATA_DE_ADESAO.getMessage());
		
		if(segurado.getSituacao().getDescricao().equals(SituacaoEnum.SUSPENSO)
				&& segurado.getSituacao().getMotivo().equals("Não recadastrado")) {
				throw new ValidateException(MensagemErroEnumSR.SEGURADO_NAO_RECADASTRADO.getMessage());
		}
		
		return guia;
	}
	
	public <P extends Procedimento> void onAfterCriarGuia(GuiaInternacaoEletiva guia, Collection<P> procedimentos) throws Exception {
		Service.verificaRecadastramento(guia);
		super.onAfterCriarGuia(guia, procedimentos);
	}
	
	public GuiaInternacaoEletiva addProcedimentosAuditoria(
			GuiaInternacaoEletiva guia,
			UsuarioInterface usuario,
			Collection<ItemDiaria> diarias,
			Collection<ProcedimentoCirurgico> procedimentosCirurgicos,
			Collection<ItemPacote> pacotes) throws Exception {
		
		GuiaInternacaoEletiva guiaAuditada = super.addProcedimentos(guia, usuario, diarias, procedimentosCirurgicos, pacotes);
		ProcedimentoUtils.aplicaDescontoDaViaDeAcesso(guia); 
		guiaAuditada.recalcularValores();
		
		return guiaAuditada;
	}
	
}
