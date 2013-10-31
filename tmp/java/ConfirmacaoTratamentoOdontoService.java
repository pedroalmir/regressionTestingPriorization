package br.com.infowaypi.ecarebc.service.odonto;

import static br.com.infowaypi.msr.utils.Assert.isNotEmpty;
import static br.com.infowaypi.msr.utils.Assert.isNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExameOdonto;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.Observacao;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.odonto.EstruturaOdonto;
import br.com.infowaypi.ecarebc.odonto.EstruturaOdontoEF;
import br.com.infowaypi.ecarebc.odonto.Odontograma;
import br.com.infowaypi.ecarebc.odonto.enums.PericiaEnum;
import br.com.infowaypi.ecarebc.painelDeControle.PainelDeControle;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOdonto;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOdontoRestauracao;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Service básico para confirmação de procedimentos de um tratamento odontológico.
 * @author Danilo Nogueira Portela
 */
public class ConfirmacaoTratamentoOdontoService extends Service {

	public ConfirmacaoTratamentoOdontoService(){
		super();
	}
	
	public ResumoGuias<GuiaExameOdonto> buscarGuiasConfirmacao(SeguradoInterface segurado, String dataInicial, String dataFinal, Prestador prestador) throws Exception {
		List<GuiaExameOdonto> guias = super.buscarGuias(dataInicial, dataFinal, segurado, prestador, true, GuiaExameOdonto.class, SituacaoEnum.AGENDADA);
		isNotEmpty(guias, MensagemErroEnum.GUIAS_NAO_ENCONTRADAS_PARA_O_SEGURADO.getMessage("confirmadas"));
		return new ResumoGuias<GuiaExameOdonto>(guias, ResumoGuias.SITUACAO_TODAS, false);
	}

	public GuiaExameOdonto buscarGuiasConfirmacao(String autorizacao, Prestador prestador) throws Exception {
		if (Utils.isStringVazia(autorizacao)){
			throw new ValidateException(MensagemErroEnum.NUMERO_AUTORIZACAO_INVALIDO.getMessage());
		}
		
		GuiaExameOdonto guia = super.buscarGuias(autorizacao, prestador, true, GuiaExameOdonto.class, SituacaoEnum.AGENDADA);
		Assert.isNotNull(guia, MensagemErroEnum.GUIA_NAO_ENCONTRADA.getMessage(autorizacao));
		guia.tocarObjetos();
		return guia;
	}

	public ResumoGuias buscarGuias(String autorizacao, String dataInicial, String dataFinal, Prestador prestador) throws Exception {
		List<GuiaExameOdonto> guias = new ArrayList<GuiaExameOdonto>();
		SearchAgent sa = new SearchAgent();
		List<String> situacoes = new ArrayList<String>();
		situacoes.add(SituacaoEnum.SOLICITADO.descricao());
		situacoes.add(SituacaoEnum.PARCIALMENTE_AUTORIZADO.descricao());
		situacoes.add(SituacaoEnum.PENDENTE.descricao());
		sa.addParameter(new In("situacao.descricao", situacoes));
		
		Calendar dataLimite = Calendar.getInstance();
		dataLimite.add(Calendar.DATE, -90);
		sa.addParameter(new GreaterEquals("situacao.dataSituacao", dataLimite.getTime()));

		guias = super.buscarGuias(sa, autorizacao, null, dataInicial, dataFinal, prestador, false, false, GuiaExameOdonto.class, GuiaSimples.DATA_DE_SITUACAO);
		
		if (Utils.isStringVazia(autorizacao)) {
			List<GuiaExameOdonto> guiasExameOdonto = HibernateUtil.currentSession().createCriteria(GuiaExameOdonto.class).add(Expression.eq("situacao.descricao", SituacaoEnum.AGENDADA.descricao())).add(Expression.eq("especial", Boolean.FALSE)).add(Expression.ge("dataMarcacao", Utils.parse(dataInicial))).add(Expression.le("dataMarcacao", Utils.parse(dataFinal))).addOrder(Order.asc("situacao.dataSituacao")).list();
			guias.addAll(guiasExameOdonto);
		}
		
		Utils.sort(guias, "prioridadeAutorizacao", "segurado.pessoaFisica.nome");
		
		ResumoGuias<GuiaExameOdonto> resumo = new ResumoGuias<GuiaExameOdonto>(guias, ResumoGuias.SITUACAO_TODAS, false);
		Assert.isNotEmpty(resumo.getGuiasComProcedimentosSolicitados(), MensagemErroEnum.NENHUMA_GUIA_ENCONTRADA.getMessage());
		return resumo;
	}

	public <P extends ProcedimentoOdonto<E>, E extends EstruturaOdonto> GuiaExameOdonto selecionarProcedimentos(Collection<P> procedimentos, Prestador prestador, GuiaExameOdonto guiaAntiga, UsuarioInterface usuario) throws Exception {
		guiaAntiga.tocarObjetos();
		
		boolean isAutorizadaOuParcialmenteAutorizada = guiaAntiga.getSituacao().getDescricao().equals(SituacaoEnum.AUTORIZADO.descricao())
			|| guiaAntiga.getSituacao().getDescricao().equals(SituacaoEnum.PARCIALMENTE_AUTORIZADO.descricao());
		
		if (isAutorizadaOuParcialmenteAutorizada){
			isNotEmpty(procedimentos, MensagemErroEnum.PROCEDIMENTO_NAO_SELECIONADO.getMessage());
		}
		
		validarPeriodicidadeDeTratamentoOdonto(guiaAntiga.getSegurado(), procedimentos.size());

		mudarSituacaoGuia(guiaAntiga, usuario);
		
		if (isAutorizadaOuParcialmenteAutorizada){
			isNotEmpty(guiaAntiga.getProcedimentosAutorizados(), MensagemErroEnum.GUIA_SEM_PROCEDIMENTOS_PARA_FLUXO.getMessage("odontológicos", "CONFIRMADOS"));
		}

		GuiaExameOdonto guiaNova = new GuiaExameOdonto(usuario);

		for (ProcedimentoOdonto<E> procedimento : procedimentos) {
			ProcedimentoOdonto<E> novoProcedimento = null;
			novoProcedimento = (procedimento.isForRestauracao()) ? new ProcedimentoOdontoRestauracao(usuario) : new ProcedimentoOdonto(usuario);
			
			novoProcedimento.setProcedimentoDaTabelaCBHPM(procedimento.getProcedimentoDaTabelaCBHPM());
			novoProcedimento.setGuia(guiaNova);
			novoProcedimento.setValorAtualDoProcedimento(procedimento.getValorAtualDoProcedimento());
			novoProcedimento.setValorAtualDaModeracao(procedimento.getValorAtualDaModeracao());
			novoProcedimento.setValorAtualDoProcedimento(procedimento.getValorAtualDoProcedimento());
			novoProcedimento.setQuantidade(procedimento.getQuantidade());
			novoProcedimento.setValorCoParticipacao(procedimento.getValorCoParticipacao());
			novoProcedimento.setBilateral(procedimento.getBilateral());
			
			novoProcedimento.setPericiaInicial(procedimento.getPericiaInicial());
			novoProcedimento.setPericiaFinal(procedimento.getPericiaFinal());
			
			for (EstruturaOdonto e : procedimento.getEstruturas()){
				EstruturaOdonto est = null;
				est =  (e.isForRestauracao()) ? new EstruturaOdontoEF() : new EstruturaOdonto();  
					
				est.setDenticao(e.getDenticao());
				est.setArcada(e.getArcada());
				est.setQuadrante(e.getQuadrante());
				est.setDente(e.getDente());
				est.setFace(e.getFace());
				
				novoProcedimento.getEstruturas().add((E)est);
			}
			
			guiaNova.addProcedimento(novoProcedimento);
		}

		guiaNova.setSegurado(guiaAntiga.getSegurado());
		guiaNova.setSolicitante(guiaAntiga.getSolicitante());
		guiaNova.setPrestador(prestador);
		guiaNova.setEspecialidade(guiaAntiga.getEspecialidade());
		guiaNova.setDataAtendimento(new Date());
		guiaNova.setValorAnterior(BigDecimal.ZERO);
		
		guiaNova.tocarObjetos();
		return guiaNova;
	}

	/**
	 * Valida a periodicidade da realização de procedimento odonto simples.
	 */
	@SuppressWarnings("unchecked")
	public void validarPeriodicidadeDeTratamentoOdonto(AbstractSegurado segurado, int qtdeProcedimentosSelecionados) throws ValidateException {
		
		int periodo  = PainelDeControle.getPainel().getPeriodicidadeTratamentoOdonto();
		int quantidade = PainelDeControle.getPainel().getQuantidadeTratementoOdonto();
		if (periodo == 0){
			return;
		}
		
		SearchAgent sa = getSearchAgent();
		sa.addParameter(new In("situacao.descricao", SituacaoEnum.AUDITADO.descricao(), SituacaoEnum.ENVIADO.descricao(),
							SituacaoEnum.RECEBIDO.descricao(), SituacaoEnum.FATURADA.descricao(), SituacaoEnum.PAGO.descricao()));
		sa.addParameter(new GreaterEquals("dataAtendimento",Utils.incrementaDias(new GregorianCalendar(), 1-periodo)));
		sa.addParameter(new Equals("segurado", segurado));
		List<GuiaExameOdonto> guiasDoSegurado = sa.list(GuiaExameOdonto.class);
		
		
		ArrayList<Procedimento> procedimentosRealizados = new ArrayList<Procedimento>();
		for (GuiaExameOdonto guiaExameOdonto : guiasDoSegurado) {
			procedimentosRealizados.addAll(guiaExameOdonto.getProcedimentosAtivos());
		}

		int qtdeDeProcedimentosRealizados = procedimentosRealizados.size();

		if(qtdeDeProcedimentosRealizados >= quantidade){
			Utils.sort(procedimentosRealizados, true, "situacao.dataSituacao");
			
			Date dataLimite = procedimentosRealizados.get(quantidade-1).getSituacao().getDataSituacao();
			Date proximaDataPermitida =	Utils.incrementaDias(dataLimite, periodo);
			System.out.println("data Limite "+proximaDataPermitida);

			throw new ValidateException(MensagemErroEnum.PERIODICIDADE_TRATAMENTO_ODONTO.getMessage(Utils.format(proximaDataPermitida)));
					
		} else {
			int quantidadeDeProcedimentosQuePodemSerRealizadosHoje = quantidade - qtdeDeProcedimentosRealizados;
			if(qtdeProcedimentosSelecionados > quantidadeDeProcedimentosQuePodemSerRealizadosHoje){
				Utils.sort(procedimentosRealizados, true, "situacao.dataSituacao");
				
				Date dataLimite = new Date();
				if (qtdeDeProcedimentosRealizados > 0){
					dataLimite = procedimentosRealizados.get(qtdeDeProcedimentosRealizados-1).getSituacao().getDataSituacao();
				}
				Date proximaDataPermitida =	Utils.incrementaDias(dataLimite, periodo);
				
				throw new ValidateException(MensagemErroEnum.PERIODICIDADE_TRATAMENTO_ODONTO_PARCIAL
								.getMessage(Integer.toString(quantidadeDeProcedimentosQuePodemSerRealizadosHoje), Utils.format(proximaDataPermitida)));
			} 
		}
		
	}

	/**
	 * @Dannylvan
	 * Método sobrescrito para mostrar o odontograma durante a a autorização de tratamento odontológico.
	 */
	public GuiaExameOdonto selecionarProcedimentosAutorizados(Prestador prestador, Collection<Observacao> observacoes, 
			GuiaExameOdonto guia, Odontograma<EstruturaOdonto> odontograma, GuiaExameOdonto guiaApresentacao, UsuarioInterface usuario) throws Exception {
		return selecionarProcedimentosAutorizados(prestador, observacoes, guia, usuario);
	}
			
	public GuiaExameOdonto selecionarProcedimentosAutorizados(Prestador prestador, Collection<Observacao> observacoes, GuiaExameOdonto guia, UsuarioInterface usuario) throws Exception {
		
		guia.tocarObjetos();
		
		//autorizando procedimentos
		autorizarProcedimentos(guia, usuario);

		//muda a situação da guia de acordo com a situação de todos os procedimentos.
		mudarSituacaoGuia(guia, usuario);

		//atualizando o valor da coparticipacao
		guia.updateValorCoparticipacao();
		
		// Gravando observações
		gravarObservacoes(observacoes, guia, usuario);

		guia.tocarObjetos();
		usuario.tocarObjetos();
		guia.recalcularValores();
		return guia;
	}

	private void autorizarProcedimentos(GuiaExameOdonto guia, UsuarioInterface usuario) {
		guia.setValorAnterior(guia.getValorTotal());
		
		BigDecimal valorTotal = BigDecimal.ZERO;
		Boolean  isAutorizadoComPericiaInicial = false;
		for (ProcedimentoOdonto p : guia.getProcedimentos()) {
			p.getAutorizado();
			String codigo = p.getProcedimentoDaTabelaCBHPM().getCodigo();

			boolean procedimentoAutorizadoView = p.isPassouPelaAutorizacao() && p.getAutorizado()!= null && p.getAutorizado();
			
			if (procedimentoAutorizadoView){
				isAutorizadoComPericiaInicial = p.getPericiaInicial();
				Assert.isFalse(isAutorizadoComPericiaInicial, "Não é possível autorizar um procedimento com perícia inicial.");
			}
			else if (p.getAutorizado()!= null && !p.getAutorizado()){
				Assert.isTrue(!p.getPericiaFinal(), MensagemErroEnum.PROCEDIMENTO_EM_PERICIA_FINAL_NAO_AUTORIZADO.getMessage(codigo));
			}
			
			if(p.getAutorizado() == null){
			}else if(p.getAutorizado() == true){
				p.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZADO_PELO_AUDITOR.getMessage(), new Date());
			}else if(p.getAutorizado() == false){
				p.mudarSituacao(usuario, SituacaoEnum.NAO_AUTORIZADO.descricao(), MotivoEnum.NAO_AUTORIZADO_AUDITOR.getMessage(), new Date());
			}
			if (p.getSituacao().getDescricao().equals(SituacaoEnum.NAO_AUTORIZADO.descricao())) {
				p.setValorAtualDoProcedimento(BigDecimal.ZERO);
				p.setValorCoParticipacao(BigDecimal.ZERO);
			}

			valorTotal = valorTotal.add(p.getValorAtualDoProcedimento());
		}
		guia.setValorTotal(valorTotal);
	}

	/**
	 * Método que muda a situação da guia baseado na situação de todos os seus procedimentos.
	 * @author Luciano Infoway
	 * @since 11/04/2013
	 * @param guia
	 * @param usuario
	 */
	private void mudarSituacaoGuia(GuiaExame guia, UsuarioInterface usuario) {
		String situacaoGuia = pegaSituacaoGuia(guia.getProcedimentos(), usuario);
		
		if (situacaoGuia.equals(SituacaoEnum.AUTORIZADO.descricao())){
			guia.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZADO_PELO_AUDITOR.getMessage(), new Date());
		} 
	    else if (situacaoGuia.equals(SituacaoEnum.NAO_AUTORIZADO.descricao())){
			guia.mudarSituacao(usuario, SituacaoEnum.NAO_AUTORIZADO.descricao(), MotivoEnum.NAO_AUTORIZADO_AUDITOR.getMessage(), new Date());
		}
	    else if (situacaoGuia.equals(SituacaoEnum.PENDENTE.descricao())){
			guia.mudarSituacao(usuario, SituacaoEnum.PENDENTE.descricao(), MotivoEnum.PENDENTE_AUDITOR.getMessage(), new Date());
		}
	    else if (situacaoGuia.equals(SituacaoEnum.PARCIALMENTE_AUTORIZADO.descricao())){
			guia.mudarSituacao(usuario, SituacaoEnum.PARCIALMENTE_AUTORIZADO.descricao(), MotivoEnum.PARCIALMENTE_AUTORIZADA_AUDITOR.getMessage(), new Date());
		}
	}
	
	/**
	 * Método que verifica qual a situação em que a guia deve estar baseado na situação de todos os seus procedimentos.
	 * @author Luciano Infoway
	 * @since 11/04/2013
	 * @param procedimentos
	 * @return
	 */
	private String pegaSituacaoGuia(Set<ProcedimentoOdonto> procedimentos, UsuarioInterface usuario) {
		int autorizados = 0;
		int naoAutorizados = 0;
        int pendentes = 0;
		int totalProcedimentos = procedimentos.size();
        
		for (ProcedimentoOdonto procedimentoOdonto : procedimentos) {
			if (procedimentoOdonto.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao())) {
				autorizados++;
			}
			else if (procedimentoOdonto.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao())){
				naoAutorizados++;
			}
			else if (procedimentoOdonto.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())
					|| procedimentoOdonto.isSituacaoAtual(SituacaoEnum.PENDENTE.descricao())){
				if (procedimentoOdonto.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())) {
					procedimentoOdonto.mudarSituacao(usuario, SituacaoEnum.PENDENTE.descricao(), MotivoEnum.PROCEDIMENTO_PENDENTE_AUDITOR.getMessage(), new Date());
				}
				pendentes++;
			}
		}
		
		if (totalProcedimentos == autorizados){
			return SituacaoEnum.AUTORIZADO.descricao();
		}
		else if (totalProcedimentos == naoAutorizados) {
			return SituacaoEnum.NAO_AUTORIZADO.descricao();
		} 
		else if (totalProcedimentos == pendentes || (pendentes > 0 && naoAutorizados > 0 && autorizados == 0)){
			return SituacaoEnum.PENDENTE.descricao();
		}
		else {
			return SituacaoEnum.PARCIALMENTE_AUTORIZADO.descricao();
		}
		
	}

	public GuiaExameOdonto autorizarTratamentoOdonto(GuiaExameOdonto guia, UsuarioInterface usuario) throws Exception {
		usuario.tocarObjetos();
		guia.setUsuarioDoFluxo(usuario);
		guia.validate();
		super.salvarGuia(guia);
		
		return guia;
	}

	/**
	 * Método utilizado na confirmação de um tratamnto odontológico - ConfirmarTratamentoODPrestador.jhm.xml, passo: confirmarTratamentoOdonto. 
	 * @param guiaDeSolicitacao
	 * @param guiaDeConfirmacao
	 * @param usuario
	 * @return
	 * @throws Exception
	 */
	public GuiaExameOdonto confirmarTratamentoOdonto(GuiaExameOdonto guiaDeSolicitacao, GuiaExameOdonto guiaDeConfirmacao, UsuarioInterface usuario) throws Exception {
		isNotNull(guiaDeConfirmacao, MensagemErroEnum.GUIA_NAO_CRIADA.getMessage("confirmação"));
		isNotNull(guiaDeSolicitacao, MensagemErroEnum.GUIA_INVALIDA.getMessage("solicitação de tratamento"));

		guiaDeSolicitacao.tocarObjetos();
		guiaDeConfirmacao.tocarObjetos();
		
		//valida guia antiga
		guiaDeSolicitacao.validate();
		
		guiaDeSolicitacao.setValorAnterior(guiaDeSolicitacao.getValorTotal());
		
		// Atualiza os procedimentos da guia antiga e da guia nova
		for (ProcedimentoOdonto proc : guiaDeConfirmacao.getProcedimentos()) {
			atualizarSituacaoDoProcedimentoConfimadoNaGuiaDeSolicitacao(guiaDeSolicitacao, proc, usuario);
			proc.mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(), MotivoEnum.PROCEDIMENTO_CONFIRMADO.getMessage(), new Date());
		}
		
		String situacao = "";
		String motivo = "";
		if (guiaDeConfirmacao.isRealizarPericia(PericiaEnum.FINAL)){
			situacao = SituacaoEnum.FECHADO.descricao();
			motivo = MotivoEnum.FECHAMENTO_GUIA.getMessage();
		} else {
			situacao = SituacaoEnum.AUDITADO.descricao();
			motivo = MotivoEnum.AUDITADO_AUTOMATICAMENTE.getMessage();
			guiaDeConfirmacao.setDataRecebimento(new Date());
		}
		
	    guiaDeConfirmacao.getSituacao().setDescricao(situacao);
	    guiaDeConfirmacao.getSituacao().setUsuario(usuario);
	    guiaDeConfirmacao.getSituacao().setMotivo(motivo);
	    
		// Atualiza os procedimentos da guia antiga
		boolean mudarSituacao = true;
		for (ProcedimentoOdonto procedimento : guiaDeSolicitacao.getProcedimentos()) {
			if (procedimento != null && procedimento.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao())) {
				mudarSituacao = false;
				break;
			}
		}
		
		if (mudarSituacao){
			guiaDeSolicitacao.mudarSituacao(usuario, SituacaoEnum.REALIZADO.descricao(), "Guia realizada.", new Date());
		}
		
		guiaDeConfirmacao.setDataAtendimento(new Date());
		guiaDeConfirmacao.setDataTerminoAtendimento(new Date());
		guiaDeConfirmacao.recalcularValores();
		//Salvando a guia nova com atualizações nos índices
		super.salvarGuia(guiaDeConfirmacao);

		guiaDeSolicitacao.addGuiaFilha(guiaDeConfirmacao);
		
		guiaDeSolicitacao.recalcularValores();
		ImplDAO.save(guiaDeSolicitacao);
		
		return guiaDeConfirmacao;
	}

	private void atualizarSituacaoDoProcedimentoConfimadoNaGuiaDeSolicitacao(GuiaExameOdonto guia, ProcedimentoOdonto procedimentoDaGuiaDeConfirmacao, UsuarioInterface usuario) throws Exception {
		for (ProcedimentoOdonto procedimentoDaGuiaDeSolicitacao : guia.getProcedimentos()) {
			boolean isMesmoCBHPM = procedimentoDaGuiaDeSolicitacao.getProcedimentoDaTabelaCBHPM().equals(procedimentoDaGuiaDeConfirmacao.getProcedimentoDaTabelaCBHPM());
			boolean isProcedimentoAutorizado = procedimentoDaGuiaDeSolicitacao.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao());
			boolean isMesmaEstruturaOdontologica = isPossuiMesmasEstruturasOdontologicas(procedimentoDaGuiaDeSolicitacao, procedimentoDaGuiaDeConfirmacao);
			if (isMesmoCBHPM && isMesmaEstruturaOdontologica && isProcedimentoAutorizado) {

				boolean isUsuarioPrestador = usuario.isPrestador();

				String descr = isUsuarioPrestador ? MotivoEnum.CONFIRMADA_NO_PRESTADOR.getMessage() : MotivoEnum.CONFIRMADA_NO_LANCAMENTO_MANUAL.getMessage();
				procedimentoDaGuiaDeSolicitacao.mudarSituacao(usuario, SituacaoEnum.REALIZADO.descricao(), descr, new Date());
				break;
			}
		}
	}
	
	private boolean isPossuiMesmasEstruturasOdontologicas(ProcedimentoOdonto procedimentoDaGuiaDeConfirmacao, ProcedimentoOdonto procedimentoDaGuiaDeSolicitacao){
		boolean isEquals = false;
		
		Set<EstruturaOdonto> estruturasDoProcedimentoDaGuiaDeConfirmacao = procedimentoDaGuiaDeConfirmacao.getEstruturas();
		Set<EstruturaOdonto> estruturasDoProcedimentoDaGuiaDeSolicitacao = procedimentoDaGuiaDeSolicitacao.getEstruturas();
		
		for(EstruturaOdonto estruturaDoProcedimentoDaGuiaDeConfirmacao : estruturasDoProcedimentoDaGuiaDeConfirmacao){
			isEquals = estruturasDoProcedimentoDaGuiaDeSolicitacao.contains(estruturaDoProcedimentoDaGuiaDeConfirmacao);
			if(!isEquals){
				return isEquals;
			}
		}
		
		return isEquals;
	}

	public void salvarGuia(GuiaSimples guia) throws Exception {
		super.salvarGuia(guia);
	}
}
