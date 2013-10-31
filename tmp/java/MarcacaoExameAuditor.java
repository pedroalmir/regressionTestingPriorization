package br.com.infowaypi.ecare.services.exame;

import java.util.Collection;

import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecare.services.Service;
import br.com.infowaypi.ecare.utils.ProfissionalUtils;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.enums.TipoCriticaEnum;
import br.com.infowaypi.ecarebc.atendimentos.validators.ManagerCritica;
import br.com.infowaypi.ecarebc.atendimentos.validators.ServiceApresentacaoCriticasFiltradas;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.service.exames.MarcacaoExameService;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
/***
 * Service para marcação de exames no sistema
 * @author Infoway
 * @changes Danilo Nogueira Portela
 */
@SuppressWarnings("rawtypes")
public class MarcacaoExameAuditor extends MarcacaoExameService implements ServiceApresentacaoCriticasFiltradas{
	
	public ResumoSegurados buscarSegurado(String numeroDoCartao,String cpfDoTitular){
		return BuscarSegurados.buscar(numeroDoCartao,cpfDoTitular,Segurado.class);
	}
	
	public GuiaExame<Procedimento> criarGuiaLancamento(Segurado segurado, Profissional profissionalCRM,Profissional profissionalNOME, 
			Collection<Procedimento> procedimentos, UsuarioInterface usuario) throws Exception {
		
		Assert.isNotNull(segurado, MensagemErroEnum.SEGURADO_NAO_INFORMADO.getMessage());
		
		if(segurado.getDataAdesao() == null)
			throw new RuntimeException(MensagemErroEnumSR.SEGURADO_SEM_DATA_DE_ADESAO.getMessage());
		
		if(segurado.getSituacao().getDescricao().equals(SituacaoEnum.SUSPENSO)
				&& segurado.getSituacao().getMotivo().equals(MotivoEnum.BENEFICIARIO_NAO_RECADASTRADO.getMessage())) {
				throw new ValidateException(MensagemErroEnumSR.SEGURADO_NAO_RECADASTRADO.getMessage());
		}
		

		Profissional profissional = ProfissionalUtils.getProfissionais(profissionalCRM, profissionalNOME);
		
		GuiaExame<Procedimento> guia = super.criarGuiaLancamento(segurado,false, null, profissional, procedimentos, usuario);
		
		this.filtrarCriticasApresentaveis(guia);
		
		return guia; 
	}

	@Override
	public void onAfterCriarGuia(GuiaExame<ProcedimentoInterface> guia, Collection<? extends ProcedimentoInterface> procedimentos) throws Exception {
		Service.verificaRecadastramento(guia);
		super.onAfterCriarGuia(guia, procedimentos);
	}
	
	public void salvarGuia(GuiaExame guia) throws Exception {
		processaSituacaoCriticas(guia);
		super.salvarGuia(guia);
	}
	
	@Override
	public void filtrarCriticasApresentaveis(GuiaSimples<?> guia) {
		ManagerCritica.processaApresentaveis(guia, TipoCriticaEnum.CRITICA_DLP_SUBGRUPO.valor());
	}

	@Override
	public void processaSituacaoCriticas(GuiaSimples<?> guia) {
		ManagerCritica.processaSituacao(guia);
	}

}
