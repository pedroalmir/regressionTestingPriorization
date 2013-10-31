package br.com.infowaypi.ecare.services;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.utils.ProfissionalUtils;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.atendimentos.enums.ValidateGuiaEnum;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.ecarebc.service.FecharGuiaService;
import br.com.infowaypi.ecarebc.utils.CommandCorrecaoCalculoValorProcedimento;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/**
 * @author Marcus bOolean
 *
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class FecharGuiaProxy extends FecharGuiaService<SeguradoInterface> {
	
	Set<Procedimento> procCancelados = new HashSet<Procedimento>();
	Set<ItemPacote> pacotesCancelados = new HashSet<ItemPacote>();
	
	@Override
	public GuiaCompleta buscarGuiasFechamento(String autorizacao,
			Prestador prestador) throws Exception {
		
		GuiaCompleta gc = super.buscarGuiasFechamento(autorizacao, prestador);
		
		ValidateGuiaEnum.PROCEDIMENTO_NIVEL_2_SITUACAO_VALIDATOR.getValidator().execute(gc);
		
		gc.setValorAnterior(gc.getValorAnterior());
		
		gc.tocarObjetos();
		return gc;
	}
	
	/**
	 * Método usado para guias que não são de internação.
	 * @author Luciano Rocha
	 * @since 28/12/2012
	 * @param numeroFatura
	 * @param guia
	 * @param observacao
	 * @param prestador
	 * @param usuario
	 * @return
	 * @throws Exception
	 */
	public GuiaCompleta adicionarItens(String numeroFatura, Collection<Procedimento> procedimentosCirurgicos ,GuiaCompleta guia, String observacao, Prestador prestador, UsuarioInterface usuario) throws Exception {
		return adicionarItens(false, null, null, numeroFatura, procedimentosCirurgicos, guia, observacao, prestador, usuario);
	}
	
	/**
	 * Método usado para guias que não possuem o profissional informado. (munge 'NAO_OBRIGATORIEDADE_DE_INFORMAR_PROFISSIONAL_NO_ATENDIMENTO').
	 * @param numeroFatura
	 * @param profissionalCRM
	 * @param procedimentosCirurgicos
	 * @param guia
	 * @param observacao
	 * @param prestador
	 * @param usuario
	 * @return
	 * @throws Exception
	 */
	public GuiaCompleta adicionarItens(String numeroFatura, Profissional profissionalCRM, Profissional profissionalNOME, Collection<Procedimento> procedimentosCirurgicos, GuiaCompleta guia, 
			String observacao, Prestador prestador, UsuarioInterface usuario) throws Exception {
		setarProfissionalOuSolicitante(profissionalCRM, profissionalNOME, guia);
		return adicionarItens(false, null, null, numeroFatura, procedimentosCirurgicos, guia, observacao, prestador, usuario); 
	}              
	
	/**
	 * Método usado para guias que não possuem o profissional informado. (munge 'NAO_OBRIGATORIEDADE_DE_INFORMAR_PROFISSIONAL_NO_ATENDIMENTO').
	 * @param fechamentoParcial
	 * @param dataInicial
	 * @param dataFinal
	 * @param numeroFatura
	 * @param guia
	 * @param procedimentosCirurgicos
	 * @param guia
	 * @param observacao
	 * @param prestador
	 * @param usuario
	 * @return
	 * @throws Exception
	 */
	public GuiaCompleta adicionarItens(Boolean fechamentoParcial, Date dataInicial, Date dataFinal, String numeroFatura, Profissional profissionalCRM, Profissional profissionalNOME, Collection<Procedimento> procedimentosCirurgicos ,GuiaCompleta guia, String observacao, Prestador prestador, UsuarioInterface usuario)
			throws Exception {
		setarProfissionalOuSolicitante(profissionalCRM, profissionalNOME, guia);
		return adicionarItens(fechamentoParcial, dataInicial, dataFinal, numeroFatura, procedimentosCirurgicos, guia, observacao, prestador, usuario);
	}

	private void setarProfissionalOuSolicitante(Profissional profissionalCRM,
			Profissional profissionalNOME, GuiaCompleta guia) {
		if(guia.isConsultaUrgencia()){
			guia.setProfissional(ProfissionalUtils.getProfissionalSeInformado(profissionalCRM, profissionalNOME));
		}else{
			guia.setSolicitante(ProfissionalUtils.getProfissionalSeInformado(profissionalCRM, profissionalNOME));
		}
	}
	
	/**
	 * Método usado para guias de internação.
	 * @author Luciano Rocha
	 * @since 28/12/2012
	 * @param fechamentoParcial
	 * @param dataInicial
	 * @param dataFinal
	 * @param numeroFatura
	 * @param guia
	 * @param observacao
	 * @param prestador
	 * @param usuario
	 * @return
	 * @throws Exception
	 */
	public GuiaCompleta adicionarItens(Boolean fechamentoParcial, Date dataInicial, Date dataFinal, String numeroFatura, Collection<Procedimento> procedimentosCirurgicos ,GuiaCompleta guia, String observacao, Prestador prestador, UsuarioInterface usuario)
			throws Exception {
	    
		/* if[NAO_OBRIGATORIEDADE_DE_INFORMAR_PROFISSIONAL_NO_ATENDIMENTO]
		if(guia.isConsultaUrgencia()){
			Assert.isNotNull(guia.getProfissional(), MensagemErroEnum.PROFISSIONAL_NAO_INFORMADO.getMessage());
		}else{
			Assert.isNotNull(guia.getSolicitante(), MensagemErroEnum.PROFISSIONAL_SOLICITANTE_NAO_INFORMADO.getMessage());
		}
		end[NAO_OBRIGATORIEDADE_DE_INFORMAR_PROFISSIONAL_NO_ATENDIMENTO] */
		
    	CommandCorrecaoCalculoValorProcedimento cmd = new CommandCorrecaoCalculoValorProcedimento(guia);
    	cmd.execute();
		
		validaSeAltaRegistradaEmCasoDeFechamentoParcial(fechamentoParcial, guia);
		validaSeAltaRegistradaEmCasoDeFechamentoTotal(fechamentoParcial, guia);
		validaDataTerminoAtendimento(dataFinal, guia);

		if(fechamentoParcial){
			validaDataInicioAtendimento(dataInicial, dataFinal);
			validaDataRealizacaoProcedimentosFechamentoParcial(guia, dataFinal);
			validarintensPacoteFechamentoParcial(guia);
		}else{
			validaDataRealizacaoProcedimentosFechamentoTotal(guia, dataFinal,usuario);
			validarIntensPacoteFechamentoTotal(guia,usuario);
			cancelarProcedimentos(usuario);
			cancelarPacotes(usuario);
		}
		
		guia.setNumeroDeRegistro(numeroFatura);
		guia.calculaHonorariosInternos(usuario);
		guia.setDataInicial(dataInicial);
		guia.fechar(fechamentoParcial, false, observacao, dataFinal, usuario);
		guia.getGuiasFilhas().size();

		return guia;
	}

	private void cancelarPacotes(UsuarioInterface usuario) {
		 Iterator<ItemPacote> iterator = pacotesCancelados.iterator();
		while (iterator.hasNext()) {
			ItemPacote itemPacote = (ItemPacote) iterator.next();
			cancelarItemPacote(usuario, itemPacote);
		}
		 
	}

	private void cancelarProcedimentos(UsuarioInterface usuario) {
		Iterator<Procedimento> iterator = procCancelados.iterator();
		while (iterator.hasNext()) {
			Procedimento procedimento = (Procedimento) iterator.next();
			cancelarProcedimento(procedimento, usuario);
		}
	}

	private void validarIntensPacoteFechamentoTotal(GuiaCompleta guia,UsuarioInterface usuario) {

		Iterator iterator = guia.getItensPacoteNaoCanceladosENegados().iterator();
		while (iterator.hasNext()) {
			ItemPacote itempacote = (ItemPacote) iterator.next();
			verificarSelecaoPacoteNaParcial(itempacote);
			boolean isNestaParcial = itempacote.getNestaParcial()!= null  &&  itempacote.getNestaParcial();
			if (!isNestaParcial){
				pacotesCancelados.add(itempacote);
			}
		}
		
	}

	private void cancelarItemPacote(UsuarioInterface usuario,ItemPacote itenpacote) {
		String motivo = "o pacote não foi realizado e, no ato de fechamento total da guia, foi cancelado.";
		itenpacote.mudarSituacao(usuario, SituacaoEnum.CANCELADO.descricao(), motivo , new Date());
	}

	private void validarintensPacoteFechamentoParcial(GuiaCompleta guia) {
		Iterator iterator = guia.getItensPacoteNaoCanceladosENegados().iterator();
		while (iterator.hasNext()) {
			ItemPacote itenpacote = (ItemPacote) iterator.next();
			verificarSelecaoPacoteNaParcial(itenpacote);
		}
	}

	private void verificarSelecaoPacoteNaParcial(ItemPacote itenpacote) {
		Assert.isNotNull(itenpacote.getNestaParcial(), MensagemErroEnum.NECESSARIO_INFORMAR_PACOTE_FOI_REALIZADO_NA_PARCIAL.getMessage(itenpacote.getPacote().getCodigo()));
	}

	private void validaDataInicioAtendimento(Date dataInicial, Date dataFinal) throws ValidateException {
		if(dataInicial != null && dataFinal != null){
			if(dataInicial.after(dataFinal)){
				throw new ValidateException(MensagemErroEnumSR.DATA_INICIO_ATENDIMENTO_POSTERIOR_A_DATA_TERMINO_ATENDIMENTO.getMessage());
			}
		}
	}

	private void validaSeAltaRegistradaEmCasoDeFechamentoParcial(Boolean fechamentoParcial, GuiaCompleta guia) throws ValidateException{
		boolean altaHospitalar = (guia.isInternacao() && ((GuiaInternacao)guia).getAltaHospitalar() != null);
		boolean apenasFechar = fechamentoParcial && altaHospitalar;
		Assert.isFalse(apenasFechar, "Caro usuário, esta guia de internação possui um registro de alta e só pode ser fechada totalmente.");
	}
	
	private void validaSeAltaRegistradaEmCasoDeFechamentoTotal(Boolean fechamentoParcial, GuiaCompleta guia) throws ValidateException{
		if (!guia.isUrgenciaOuAtendimentoSubsequente() && guia.getDataTerminoAtendimento() == null && !fechamentoParcial){
				throw new ValidateException(MensagemErroEnum.FECHAMENTO_EXIGE_ALTA.getMessage());
		}
	}

	private void validaDataTerminoAtendimento(Date dataFinal, GuiaCompleta guia) throws ValidateException {
		//Só validar quando a guia não possuir data de término de atendimento e não for guia de GuiaConsultaUrgendia ou GuiaAtendimentoUrgencia,
		//pois nestes casos não sera um fechamento parcial.
		if (!guia.isUrgenciaOuAtendimentoSubsequente() && guia.getDataTerminoAtendimento() == null){
			if (dataFinal == null){
				throw new ValidateException(MensagemErroEnum.DATA_FECHAMENTO_REQUERIDA.getMessage());
			}
		}
	}
	
	/**
	 * Verifica se a data de realização é compatível com a parcial que ele deve pertencer.
	 * @throws ValidateException 
	 */
	protected void validaDataRealizacaoProcedimentosFechamentoParcial(GuiaCompleta<ProcedimentoCirurgico> guia, Date dataFinal) throws ValidateException{
		for (ProcedimentoCirurgico procedimento : guia.getProcedimentosIn(ProcedimentoCirurgico.class, SituacaoEnum.AUTORIZADO)) {
			
			verificarSelecaoProcedimentoNaParcial(procedimento);

			if(procedimento.getRealizadoNestaParcial()){
				Date dataRealizacao = procedimento.getDataRealizacao();
				validaDataRealizacao(guia, dataFinal, procedimento, dataRealizacao);
			}
		}
	}

	private void verificarSelecaoProcedimentoNaParcial(ProcedimentoCirurgico procedimento) {
		Assert.isNotNull(procedimento.getRealizadoNestaParcial(), MensagemErroEnum.NECESSARIO_INFORMAR_SE_FOI_REALIZADO_NA_PARCIAL.getMessage(procedimento.getProcedimentoDaTabelaCBHPM().getCodigo()));
	}
	
	/**
	 * Valida as datas dos procedimentos da guia que vai ser fechada 
	 * @param usuario 
	 */
	protected void validaDataRealizacaoProcedimentosFechamentoTotal(GuiaCompleta<ProcedimentoCirurgico> guia, Date dataFinal, UsuarioInterface usuario) throws ValidateException {
		for (ProcedimentoCirurgico procedimento : guia.getProcedimentosIn(ProcedimentoCirurgico.class, SituacaoEnum.AUTORIZADO)) {
			verificarSelecaoProcedimentoNaParcial(procedimento);
			//validação comentada para que o processo não trave caso haja erro no preenchimento das datas. Por exemplo, caso a data do atendimento tenha sido informada errada, os procedimentos cirurgicos não seriam validados. 
			//validarRealizacaoDosProcedimentoNaParcial(guia,dataFinal,procedimento,usuario);
		}
	}

	private void validarRealizacaoDosProcedimentoNaParcial(GuiaCompleta guia, Date dataFinal, ProcedimentoCirurgico procedimento, UsuarioInterface usuario) throws ValidateException {
		Boolean realizadoNestaParcial =  procedimento.getRealizadoNestaParcial()  ;
		
		if (!realizadoNestaParcial){
			procCancelados.add(procedimento);
		}else{
			Date dataRealizacao = procedimento.getDataRealizacao();
			validaDataRealizacao(guia, dataFinal, procedimento, dataRealizacao);
		}
		
	}

	private void cancelarProcedimento(Procedimento procedimento,
			UsuarioInterface usuario) {
		String motivo = "o procedimento não foi realizado e, no ato de fechamento total da guia, foi cancelado";
		procedimento.mudarSituacao(usuario, SituacaoEnum.CANCELADO.descricao(), motivo, new Date());
	}

	private void validaDataRealizacao(GuiaCompleta guia, Date dataFinal, ProcedimentoCirurgico proc, Date dataRealizacao) throws ValidateException {
		Assert.isNotNull(dataRealizacao, MensagemErroEnum.DATA_CIRURGIA_REQUERIDA.getMessage(proc.getProcedimentoDaTabelaCBHPM().getCodigo()));
		
		if (dataRealizacao.before(guia.getDataAtendimento())){
			throw new ValidateException(MensagemErroEnum.DATA_CIRURGIA_INFERIOR_A_DATA_ATENDIMENTO.getMessage());
		}
		
		if (dataFinal != null && dataRealizacao.after(dataFinal)){
			throw new ValidateException(MensagemErroEnumSR.DATA_CIRURGIA_POSTERIOR_A_DATA_FINAL.getMessage(proc.getProcedimentoDaTabelaCBHPM().getCodigo()));
		}
	}

}