package br.com.infowaypi.ecare.services.odonto;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaOdonto;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExameOdonto;
import br.com.infowaypi.ecarebc.atendimentos.Observacao;
import br.com.infowaypi.ecarebc.atendimentos.enums.TipoConsultaEnum;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOdonto;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOdontoRestauracao;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOdontoRestauracaoLayer;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.ecarebc.service.Agente;
import br.com.infowaypi.ecarebc.service.odonto.SolicitacaoTratamentoOdontoService;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/*if[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO]
import br.com.infowaypi.ecare.services.ValidacaoAtendimentoSegmentacaoAssistencial;
import br.com.infowaypi.ecarebc.produto.SegmentacaoAssistencialEnum;
end[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */

/**
 * Service para solicitação de um tratamento odontológico
 * @author Danilo Nogueira Portela
 * @changes Jefferson Gonçalves de Oliveira, Leonardo Sampaio
 */ 
@SuppressWarnings("unchecked")
public class SolicitacaoTratamentoOdonto extends SolicitacaoTratamentoOdontoService {

	private final int PRAZO_MAXIMO_GUIA_CONSULTA_ODONTO = 180;

	public ResumoSegurados buscarSegurado(String cpfDoTitular, String numeroDoCartao) throws ValidateException {
		/*if[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO]
		ResumoSegurados resumo = ValidacaoAtendimentoSegmentacaoAssistencial.buscar(cpfDoTitular, numeroDoCartao, Segurado.class, false, SegmentacaoAssistencialEnum.ODONTOLOGICO.getSegmentacaoAssistencial());
		else[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */
		ResumoSegurados resumo = BuscarSegurados.buscar(cpfDoTitular, numeroDoCartao, Segurado.class);
		/* end[VALIDAR_SEGMENTACAO_ASSISTENCIAL_PRODUTO] */
		return resumo;
	}

	@SuppressWarnings("deprecation")
	public GuiaExameOdonto criarGuiaPrestador(Segurado segurado, Prestador prestador, Profissional solicitante, 
			Collection<ProcedimentoOdonto> procedimentos, UsuarioInterface usuario) throws Exception {

		GuiaExameOdonto guiaGerada = criarGuiaPrestador(segurado, prestador, usuario, solicitante, procedimentos);
		return guiaGerada;
	}

	public <P extends ProcedimentoOdonto>GuiaExameOdonto criarGuiaEspecialLayer(Segurado segurado, Prestador prestador, Profissional solicitante, 
			Collection<ProcedimentoOdontoRestauracaoLayer> procedimentosLayer, Collection<Observacao> observacoes, UsuarioInterface usuario) throws Exception {
		Collection<ProcedimentoOdontoRestauracao> procedimentos = convertProcedimentosLayers(procedimentosLayer, usuario);
		return criarGuiaEspecial(segurado, prestador, solicitante, procedimentos, observacoes, usuario);
	}
	
	private Collection<ProcedimentoOdontoRestauracao> convertProcedimentosLayers(Collection<ProcedimentoOdontoRestauracaoLayer> procedimentosLayer, UsuarioInterface usuario) throws Exception {
		Set<ProcedimentoOdontoRestauracao> procedimentos = new HashSet<ProcedimentoOdontoRestauracao>(); 
		
		for (ProcedimentoOdontoRestauracaoLayer layer : procedimentosLayer) {
			procedimentos.add(layer.getProcedimentoOR());
		}
		
		return procedimentos;
	}

	public <P extends ProcedimentoOdonto>GuiaExameOdonto criarGuiaEspecial(Segurado segurado, Prestador prestador, Profissional solicitante, 
			Collection<P> procedimentos, Collection<Observacao> observacoes, UsuarioInterface usuario) throws Exception {
		
		this.validaInformacoesFornecidas(segurado, prestador, solicitante, procedimentos);
		this.tocarObjetos(segurado, prestador, solicitante);
		
		GuiaExameOdonto guiaGerada = this.criarGuiaExameOdonto(segurado, prestador, solicitante, usuario, procedimentos);
		
		Assert.isNotNull(guiaGerada.getGuiaOrigem(), MensagemErroEnum.GUIA_SEM_CONSULTA_ORIGEM.getMessage(String.valueOf(PRAZO_MAXIMO_GUIA_CONSULTA_ODONTO)));
		
		/*  if[REPLICAR_ESPECIALIDADE_DA_CONSULTA_NO_TRATAMENTO_ODONTO]
			guiaGerada.setEspecialidade(guiaGerada.getGuiaOrigem().getEspecialidade());
		 end[REPLICAR_ESPECIALIDADE_DA_CONSULTA_NO_TRATAMENTO_ODONTO]*/
			
		/*
		 * Solução temporárea para corrigir um equívoco na validação do método isEspecial() na solicitação de tratamentos odontológicos.
		 * No fluxo de solicitação, ele estava lançando uma exceção impeditiva dizendo que o procedimento necessitava de autorização.
		 * Assim que a parametrização da TPSR entrar no ar, a nova arquitetura solucionará por si só esse problema.
		 */
		try{
			guiaGerada.validate();
		}catch (Exception e) {
			boolean lancouExcessaoParaAlgumProcedimento = false;
			/* Percorre todos or procedimentos da guia para verificar se foi lançada exceção de autorização para algum deles.
			 */
			for (Procedimento procedimento: guiaGerada.getProcedimentos()){
				TabelaCBHPM procedimentoDaTabela = procedimento.getProcedimentoDaTabelaCBHPM();
				if (e.getMessage().equals(MensagemErroEnum.PROCEDIMENTO_ESPECIAL_NAO_SOLICITADO.getMessage(procedimentoDaTabela.getCodigo()))){
					lancouExcessaoParaAlgumProcedimento = true;
					break;
				}
			}
			
			/* Caso não tenha sido lançada esta exceção para nenhum dos proceidimentos contidos na guia. A exceção é passada pra frente.
			 * Pois a intenção é somente calar as exceções do procedimento isEspecial();
			 */
			if (!lancouExcessaoParaAlgumProcedimento){
				throw e;
			}
		}
		
		// Gravando observações
		super.gravarObservacoes(observacoes, guiaGerada, usuario);
		guiaGerada.recalcularValores();
		
		return guiaGerada;
	}

	/**
	 * Cria guia de TratamentoOdonto
	 * 
	 * @param segurado segurado da solicitação
	 * @param prestador prestador
	 * @param solicitante profissional solicitante (<b>pode ser</b> diferente da ConsultaOdonto ativa do segurado)
	 * @param usuario usuario do fluxo
	 * @param procedimentos procedimentos solicitados
	 * @return guia de tratamento criada
	 * @throws Exception
	 */
	private <P extends ProcedimentoOdonto>GuiaExameOdonto criarGuiaExameOdonto(Segurado segurado, Prestador prestador, Profissional solicitante, UsuarioInterface usuario,
			Collection<P> procedimentos) throws Exception {
		GuiaExameOdonto guiaGerada = new GuiaExameOdonto(usuario);
		
		Agente agente = Agente.PRESTADOR;
		Date dataAtendimento = new Date();
		Especialidade especialidadeOdonto = super.getEspecialidadeOdonto(TipoConsultaEnum.CONSULTA_ODONTOLOGICA);
		
		guiaGerada.setFromPrestador(agente.isFromPrestador());
		guiaGerada.setDataAtendimento(dataAtendimento);
		guiaGerada.setPrestador(prestador);
		guiaGerada.setSolicitante(solicitante);
		guiaGerada.setEspecialidade(especialidadeOdonto);
		guiaGerada.setSegurado(segurado);
		
		SituacaoInterface situacao = guiaGerada.getSituacao(/*SituacaoEnum.AGENDADA.descricao()*/);
		situacao.setMotivo(MotivoEnum.SOLICITACAO_DE_TRATAMENTO_ODONTOLOGICO.getMessage());
		situacao.setUsuario(usuario);
				
		/*if[INTEGRACAO]
		//Nao leva em consideração profissional da guia ConsultaOdonto origem
		List<GuiaConsultaOdonto> guias = guiaGerada.buscarGuiaOrigem(GuiaConsultaOdonto.class,false,PRAZO_MAXIMO_GUIA_CONSULTA_ODONTO);
		else[INTEGRACAO]*/
		List<GuiaConsultaOdonto> guias = guiaGerada.buscarConsultaOdontoOrigem();
		/*end[INTEGRACAO]*/
		if(!guias.isEmpty()){
			GuiaConsultaOdonto consultaOrigem = guias.get(0); 
			consultaOrigem.addGuiaFilha(guiaGerada);
		}
		
		for (ProcedimentoOdonto procedimento : (Collection<ProcedimentoOdonto>) procedimentos) {
			procedimento.calcularCampos();
			procedimento.setPericiaInicial(false);
			procedimento.setPericiaFinal(false);
			guiaGerada.addProcedimento(procedimento);
		}
		
		guiaGerada.setValorAnterior(BigDecimal.ZERO);
		return guiaGerada;
	}

	private void tocarObjetos(Segurado segurado, Prestador prestador, Profissional solicitante) {
		segurado.tocarObjetos();
		prestador.tocarObjetos();
		solicitante.tocarObjetos();
	}

	private <P> void validaInformacoesFornecidas(Segurado segurado, Prestador prestador, Profissional solicitante, Collection<P> procedimentos) {
		Assert.isNotEmpty(procedimentos, MensagemErroEnum.PROCEDIMENTO_NAO_INFORMADO.getMessage());
		Assert.isNotNull(segurado, MensagemErroEnum.SEGURADO_NAO_INFORMADO.getMessage());
		Assert.isNotNull(prestador, MensagemErroEnum.PRESTADOR_NAO_INFORMADO.getMessage());
		Assert.isNotNull(solicitante, MensagemErroEnum.SOLICITANTE_NAO_INFORMADO.getMessage());
	}

	public GuiaExameOdonto criarGuiaMarcador(AbstractSegurado segurado, UsuarioInterface usuario, Profissional solicitante, 
			Collection<ProcedimentoOdonto> procedimentos) throws Exception {

		GuiaExameOdonto guiaGerada = criarGuiaMarcador(segurado, usuario, solicitante, procedimentos);
		return guiaGerada;
	}

	public GuiaExameOdonto criarGuiaLancamento(AbstractSegurado segurado, Boolean ignorarValidacao, String dataDeAtendimento, 
			Profissional solicitante, Collection procedimentos, UsuarioInterface usuario) throws Exception {

		GuiaExameOdonto guiaGerada  = criarGuiaLancamento(segurado, ignorarValidacao, dataDeAtendimento, solicitante, procedimentos, usuario);
		return guiaGerada;
	}

	public void salvarGuia(GuiaExameOdonto guia) throws Exception {
		super.salvarGuia(guia);
	}
}
