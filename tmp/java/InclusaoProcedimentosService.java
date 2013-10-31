package br.com.infowaypi.ecare.services.urgencias;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.Critica;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.atendimentos.enums.TipoCriticaEnum;
import br.com.infowaypi.ecarebc.atendimentos.validators.ManagerCritica;
import br.com.infowaypi.ecarebc.atendimentos.validators.ServiceApresentacaoCriticasFiltradas;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.CID;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.service.SolicitarInclusaoProcedimento;
import br.com.infowaypi.ecarebc.utils.CommandCorrecaoCalculoValorProcedimento;
import br.com.infowaypi.ecarebc.utils.ProcedimentoUtils;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
/**
 * 
 * @author Idelvane
 *
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class InclusaoProcedimentosService extends SolicitarInclusaoProcedimento<Segurado> implements ServiceApresentacaoCriticasFiltradas{
	
	public static final int LIMITE_EXAMES_URGENCIA = 5;

	public ResumoGuias<GuiaCompleta> buscarGuias(String numeroDoCartao,String cpfDoTitular, Prestador prestador) throws Exception{
		
		ResumoSegurados resumo = BuscarSegurados.buscar(numeroDoCartao, cpfDoTitular, Segurado.class);
		return super.buscarGuiasRealimentacao(resumo.getSegurados(), prestador);	
 	}
	
	@Override
	public GuiaCompleta buscarGuiasRealimentacao(String autorizacao, Prestador prestador) throws Exception {
		
		GuiaCompleta guia = super.buscarGuia(autorizacao.trim(), prestador);
		guia.tocarObjetos();
		guia.getPrestador().tocarObjetos();
		prestador.tocarObjetos();
		Assert.isTrue((guia.isAtendimentoUrgencia() || guia
				.isConsultaUrgencia() || guia.isInternacao()),
				MensagemErroEnum.GUIA_NAO_PERTENCE_AO_TIPO
						.getMessage("CONSULTA ou ATENDIMENTO de URGÊNCIA"));

		guia.getCriticas().size();
		return guia;
	}
	
	@Override
	public GuiaSimples addProcedimentos(Prestador prestador, Collection<Procedimento> procedimentos, Collection<CID> cids,
			String justificativa, GuiaCompleta guia, UsuarioInterface usuario) throws Exception {
		
		Set<CID> setCID = new HashSet<CID>();
		
		
		for (Procedimento exame : procedimentos) {
			exame.validate(guia);
			exame.mudarSituacao(usuario, SituacaoEnum.PRE_AUTORIZADO.descricao(), MotivoEnum.INCLUSAO_DE_EXAMES_SIMPLES.getMessage(), new Date());
		}
		
		int quantidadeProcedimentos = examesContidosNaGuia(guia);
		int quantidadeSolicitada = quantidadeSolicitada(procedimentos);
		if (guia.isConsultaUrgencia() || guia.isAtendimentoUrgencia()) {
			if (quantidadeProcedimentos >= LIMITE_EXAMES_URGENCIA) {
				for (ProcedimentoInterface procedimento : procedimentos) {
					procedimento.getSituacao().setDescricao(SituacaoEnum.SOLICITADO.descricao());
				}
				
				Critica critica = new Critica();
				critica.setData(new Date());
				critica.setGuia(guia);
				critica.setMensagem("Os exames solicitados precisam passar pela autorização de um regulador.");
				
				guia.getCriticas().add(critica);
			} else {
				if (quantidadeSolicitada + quantidadeProcedimentos > LIMITE_EXAMES_URGENCIA) {
					throw new RuntimeException("Nessa solicitação só podem ser incluídos mais "+(LIMITE_EXAMES_URGENCIA-quantidadeProcedimentos)+" procedimentos.");
				}
			}
		}
		
		super.addProcedimentos(prestador, procedimentos, setCID, justificativa, guia, usuario);
		guia.recalcularValores();
		
		CommandCorrecaoCalculoValorProcedimento cmd = new CommandCorrecaoCalculoValorProcedimento(guia);
		cmd.execute();
		
		return guia;
	}

	private int examesContidosNaGuia(GuiaCompleta guia) {
		int result = 0;
		for (ProcedimentoInterface procedimento : (Set<ProcedimentoInterface>) guia.getProcedimentosSimplesNaoCancelados()) {
			int grupo = procedimento.getProcedimentoDaTabelaCBHPM().getGrupo();
			if (grupo == 2 || grupo == 4) {
				result += procedimento.getQuantidade();
			}
		}
		return result;
	}

	private int quantidadeSolicitada(Collection<Procedimento> procedimentos) {
		int result = 0;
		for (Procedimento procedimento : procedimentos) {
			result += procedimento.getQuantidade();
		}
		return result;
	}

//	@Override
	public GuiaSimples addProcedimentos(Prestador prestador,
			Collection<Procedimento> procedimentos,
			Collection<ProcedimentoCirurgico> procedimentosCirurgicos,
			Collection<CID> cids, String justificativa, GuiaSimples guia,
			UsuarioInterface usuario) throws Exception {
		
		GuiaCompleta g  = (GuiaCompleta) guia;
		ProcedimentoUtils.aplicaDescontoDaViaDeAcesso(g);
		
		GuiaSimples guiaNova = super.addProcedimentos(prestador, procedimentos, procedimentosCirurgicos, cids, justificativa, g, usuario);
			
		guiaNova.setUsuarioDoFluxo(usuario);
		guiaNova.validate();
		filtrarCriticasApresentaveis(guiaNova);
		
		return guiaNova;
	}
	
	public void salvarGuia(GuiaSimples guia) throws Exception{
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
