package br.com.infowaypi.ecarebc.service;

import static br.com.infowaypi.msr.utils.Assert.isNotEmpty;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.FlushMode;
import org.hibernate.LockMode;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemGasoterapia;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemGuia;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemTaxa;
import br.com.infowaypi.ecarebc.atendimentos.alta.MotivoAlta;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOutros;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.ecarebc.utils.CommandCorrecaoValorGuia;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Service para fechamento de guias completas do plano de saúde; 
 * @author root
 * @changes Jefferson
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class FecharGuiaService<S extends SeguradoInterface> extends MarcacaoService<GuiaCompleta> {
	
	public static final int LIMITE_DIAS_FECHAMENTO = 90;
	private static final String CODIGO_ALTA_ADMINISTRATIVA = "16";
	public Class tipoGuia; 
	private Set<ItemDiaria> diariasDaNovaGuia;
	protected List<ProcedimentoInterface> procedimentosDaNovaGuia;
	protected List<ItemPacote> itensPacoteDaNovaGuia;
	
	public FecharGuiaService() {
		super();
		
		diariasDaNovaGuia = new HashSet<ItemDiaria>();
		procedimentosDaNovaGuia = new ArrayList<ProcedimentoInterface>();
		itensPacoteDaNovaGuia = new ArrayList<ItemPacote>();
	}
	
	public ResumoGuias<GuiaCompleta> buscarGuiasFechamento(S segurado, String dataInicial, String dataFinal, Prestador prestador) throws Exception{
		List<GuiaCompleta> guias = super.buscarGuias(dataInicial, dataFinal,segurado, prestador,false, GuiaCompleta.class, SituacaoEnum.ABERTO, SituacaoEnum.PRORROGADO, SituacaoEnum.NAO_PRORROGADO);
		isNotEmpty(guias, MensagemErroEnum.GUIAS_NAO_ENCONTRADAS_PARA_O_SEGURADO.getMessage("CONFIRMADAS"));
		return new ResumoGuias<GuiaCompleta>(guias, ResumoGuias.SITUACAO_TODAS, false);
	}
	
	public ResumoGuias<GuiaCompleta> buscarGuiasFechamento(Collection<S> segurados, Prestador prestador) throws Exception{
		SearchAgent sa = getSearchSituacoes(SituacaoEnum.ABERTO, SituacaoEnum.PRORROGADO, SituacaoEnum.NAO_PRORROGADO);
		List<GuiaCompleta> guias = super.buscarGuias(sa,segurados, prestador,false, GuiaCompleta.class);
		isNotEmpty(guias, MensagemErroEnum.GUIAS_NAO_ENCONTRADAS_PARA_O_SEGURADO.getMessage("CONFIRMADAS"));
		return new ResumoGuias<GuiaCompleta>(guias, ResumoGuias.SITUACAO_TODAS, false);
	}
	
	public GuiaCompleta buscarGuiasFechamento(String autorizacao, Prestador prestador) throws Exception{
		if (Utils.isStringVazia(autorizacao))
			throw new ValidateException(MensagemErroEnum.NUMERO_AUTORIZACAO_INVALIDO.getMessage());

		GuiaCompleta guia = super.buscarGuias(autorizacao, prestador,false, GuiaCompleta.class, SituacaoEnum.AGENDADA,SituacaoEnum.ABERTO,SituacaoEnum.CONFIRMADO, 
				SituacaoEnum.INTERNADO, SituacaoEnum.NAO_PRORROGADO, SituacaoEnum.PRORROGADO, SituacaoEnum.ALTA_REGISTRADA, SituacaoEnum.FECHADO);		
			
		Assert.isNotNull(guia, MensagemErroEnum.GUIA_NAO_ENCONTRADA.getMessage());
		Assert.isFalse(guia.isExame(), "Guias de exame não podem ser fechadas através desse fluxo."); 
		guia.tocarObjetos();
		if(guia.getGuiaOrigem() != null){
			guia.getGuiaOrigem().getGuiasFilhas().size();
		}
		
		return guia;
	}
	 
	public GuiaCompleta adicionarItens(GuiaCompleta<ProcedimentoInterface> guia,Collection<ProcedimentoOutros> outrosProcedimentos, Collection<ItemTaxa> taxas, 
			Collection<ItemGasoterapia> gasoterapias,
			BigDecimal valorTotalMateriaisMedicosApartamento,
			BigDecimal valorTotalMateriaisMedicosUTI,
			BigDecimal valorTotalMateriaisMedicosBlocoCirurgico,
			BigDecimal valorTotalMateriaisProntoSocorro,
			BigDecimal valorTotalMedicamentosApartamento,
			BigDecimal valorTotalMedicamentosUTI,
			BigDecimal valorTotalMedicamentosBlocoCirurgico, 
			BigDecimal valorTotalMedicamentosProntoSocorro, 
			BigDecimal valorTotalMateriaisEspeciais,
			BigDecimal valorTotalMedicamentosEspeciais,
			BigDecimal valorTotalOPMES,
			BigDecimal valorOutros,
			String descricaoOutros,
			Boolean fechamentoParcial,
			Date dataFinal,
			Prestador prestador,
			UsuarioInterface usuario) throws Exception{
		
		fechamentoParcial = fechamentoParcial == null ? false : fechamentoParcial;
		
		
		Calendar atendimento = new GregorianCalendar();
		atendimento.setTime(guia.getDataAtendimento());
		
		Calendar fechamento = new GregorianCalendar();
		
		int diferenca = Utils.diferencaEmDias(atendimento, fechamento);
		
		if (diferenca > LIMITE_DIAS_FECHAMENTO){
			throw new RuntimeException(MensagemErroEnum.GUIA_NAO_PODE_SER_FECHADA.getMessage(guia.getAutorizacao(),String.valueOf(LIMITE_DIAS_FECHAMENTO)));
		}
		
		if (guia.isAtendimentoUrgencia() || guia.isConsultaUrgencia()){
			guia.setDataTerminoAtendimento(guia.getDataAtendimento());
		}

		if (guia.isInternacao()) {
			GuiaInternacao gi = (GuiaInternacao) guia;
			boolean isGuiaFechada = gi.isSituacaoAtual(SituacaoEnum.FECHADO.descricao());
			boolean isGuiaParcial = gi.isGuiaParcial();

			if (fechamentoParcial && !gi.isUltimaGuiaDaInternacao()){
				throw new RuntimeException("Caro usuário, não é possível fechar parcialmente esta guia.");
			}
			
			if(!isGuiaFechada && fechamentoParcial){
				validaDataTerminoAtendimento(guia, dataFinal);
			}
			
			if (isGuiaFechada && isGuiaParcial && !fechamentoParcial) {
				boolean dataFinalNaoENula = dataFinal != null;
				if (dataFinalNaoENula) {
					boolean naoEMesmoDia = !(Utils.getDiferencaEmDias(dataFinal, guia.getDataTerminoAtendimento()) == 0);
					if  (naoEMesmoDia){
						throw new RuntimeException("Não é possível alterar a data de término de atendimento de uma guia parcial.");
					}
				}
			}
			
			if(guia.getDataCirurgia() != null){
				if (guia.getDataCirurgia().before(guia.getDataAtendimento())){
					throw new RuntimeException(MensagemErroEnum.DATA_CIRURGIA_INFERIOR_A_DATA_ATENDIMENTO.getMessage());
				}
				if(guia.getDataCirurgia().after(dataFinal)){
					throw new RuntimeException(MensagemErroEnum.DATA_CIRURGIA_SUPERIOR_A_DATA_FECHAMENTO.getMessage());
				}
			}

			//para guia de internação com alta hospitalar
			boolean isPossuiAlta = (((GuiaInternacao) guia).getAltaHospitalar() != null);
			
			if(!isPossuiAlta  && (!fechamentoParcial) ){
				throw new ValidateException("Caro usuário, para realizar o fechamento total desta guia é necessário registrar a alta hospitalar.");
			}
			 
			//para guia de internacao sem alta
			if (!isPossuiAlta && fechamentoParcial){
				GuiaInternacao guiaInternacao = (GuiaInternacao)guia;
				HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
				
				SearchAgent sa = new SearchAgent();
				sa.addParameter(new  Equals("codigo",CODIGO_ALTA_ADMINISTRATIVA));
				MotivoAlta motivo = sa.uniqueResult(MotivoAlta.class);
				
				guiaInternacao.registrarAlta(dataFinal, usuario, motivo);
				guia.setDataTerminoAtendimento(dataFinal);
			}
		}
		
		if(!guia.isInternacao() && (fechamentoParcial)){
			throw new ValidateException("Uma guia de Consulta de Urgência/Atendimento Subsequente não pode ter fechamento parcial.");
		}

		BigDecimal valorAnteriorDaGuia = guia.getValorTotal();
		guia.setNumeroAnteriorProcedimentosGuia(guia.getQuantidadeProcedimentosValidosParaConsumo());
		guia.mudarSituacao(usuario, SituacaoEnum.FECHADO.descricao(), MotivoEnum.FECHAMENTO_GUIA.getMessage(), new Date());
		
		if(fechamentoParcial != null){
			guia.setFechamentoParcial(fechamentoParcial);
		}
		
		guia.addItensTaxa(taxas);
		if(outrosProcedimentos.size() >0) {
			for (ProcedimentoOutros procedimento : outrosProcedimentos) {
				guia.addProcedimento(procedimento);
			}
		}
		guia.addItensGasoterapia(gasoterapias);

		if (valorTotalMateriaisMedicosApartamento != null){
			guia.getValoresMatMed().setValorTotalMateriaisMedicosApartamento(valorTotalMateriaisMedicosApartamento);
			guia.getValoresMatMed().setValorTotalMateriaisMedicosApartamentoAuditado(valorTotalMateriaisMedicosApartamento);
		}	
		if (valorTotalMateriaisMedicosUTI != null) {
			guia.getValoresMatMed().setValorTotalMateriaisMedicosUTI(valorTotalMateriaisMedicosUTI);
			guia.getValoresMatMed().setValorTotalMateriaisMedicosUTIAuditado(valorTotalMateriaisMedicosUTI);
		}
		if (valorTotalMateriaisMedicosBlocoCirurgico != null) {
			guia.getValoresMatMed().setValorTotalMateriaisMedicosBlocoCirurgico(valorTotalMateriaisMedicosBlocoCirurgico);
			guia.getValoresMatMed().setValorTotalMateriaisMedicosBlocoCirurgicoAuditado(valorTotalMateriaisMedicosBlocoCirurgico);
		}
		if (valorTotalMateriaisProntoSocorro != null) {
			guia.getValoresMatMed().setValorTotalMateriaisProntoSocorro(valorTotalMateriaisProntoSocorro);
			guia.getValoresMatMed().setValorTotalMateriaisProntoSocorroAuditado(valorTotalMateriaisProntoSocorro);
		}
		if (valorTotalMedicamentosApartamento != null) {
			guia.getValoresMatMed().setValorTotalMedicamentosApartamento(valorTotalMedicamentosApartamento);
			guia.getValoresMatMed().setValorTotalMedicamentosApartamentoAuditado(valorTotalMedicamentosApartamento);
		}
		if (valorTotalMedicamentosUTI != null) {
			guia.getValoresMatMed().setValorTotalMedicamentosUTI(valorTotalMedicamentosUTI);
			guia.getValoresMatMed().setValorTotalMedicamentosUTIAuditado(valorTotalMedicamentosUTI);
		}
		if (valorTotalMedicamentosBlocoCirurgico != null) {
			guia.getValoresMatMed().setValorTotalMedicamentosBlocoCirurgico(valorTotalMedicamentosBlocoCirurgico);
			guia.getValoresMatMed().setValorTotalMedicamentosBlocoCirurgicoAuditado(valorTotalMedicamentosBlocoCirurgico);
		}	
		if (valorTotalMedicamentosProntoSocorro != null) {
			guia.getValoresMatMed().setValorTotalMedicamentosProntoSocorro(valorTotalMedicamentosProntoSocorro);
			guia.getValoresMatMed().setValorTotalMedicamentosProntoSocorroAuditado(valorTotalMedicamentosProntoSocorro);
		}
		if (valorTotalMedicamentosEspeciais != null) {
			guia.getValoresMatMed().setValorTotalMedicamentosEspeciais(valorTotalMedicamentosEspeciais);
			guia.getValoresMatMed().setValorTotalMedicamentosEspeciaisAuditado(valorTotalMedicamentosEspeciais);
		}
		if (valorTotalMateriaisEspeciais != null) {
			guia.getValoresMatMed().setValorTotalMateriaisEspeciais(valorTotalMateriaisEspeciais);
			guia.getValoresMatMed().setValorTotalMateriaisEspeciaisAuditado(valorTotalMateriaisEspeciais);
		}
		
		boolean isValorOutrosInformado = valorOutros != null && !MoneyCalculation.rounded(BigDecimal.ZERO).equals(MoneyCalculation.rounded(valorOutros));
		
		if (isValorOutrosInformado) {
			
			if(StringUtils.isEmpty(descricaoOutros)){
				throw new RuntimeException("Quando o campo OUTROS VALORES for informado o campo  DESCRIÇÃO OUTROS também deve ser informado.");
			}
			
			guia.getValoresMatMed().setValorOutros(valorOutros);
			guia.getValoresMatMed().setValorOutrosAuditado(valorOutros);
			guia.getValoresMatMed().setDescricaoOutros(descricaoOutros);
		}
		
		guia.setValorTotal(MoneyCalculation.getSoma(guia.getValorTotal(), guia.getValoresMatMed().getValorTotalInformado().floatValue()));
		
		// recalcula-se o valor da guia baseado no porte dos procedimentos e profissionais inseridos
		for(ProcedimentoCirurgicoInterface procedimento : guia.getProcedimentosCirurgicos()) {
			if(procedimento.getProfissionalAuxiliar1() != null) {
				guia.setValorTotal(guia.getValorTotal().add(procedimento.getValorAuxiliar1()));
			}
			if(procedimento.getProfissionalAuxiliar2() != null) {
				guia.setValorTotal(guia.getValorTotal().add(procedimento.getValorAuxiliar2()));
			}
			if(procedimento.getProfissionalAuxiliar3() != null) {
				guia.setValorTotal(guia.getValorTotal().add(procedimento.getValorAuxiliar3()));
			}
		}
		
		guia.updateValorCoparticipacao();
				
		guia.setValorAnterior(valorAnteriorDaGuia);
		return guia;
	}

	private void validaDataTerminoAtendimento(GuiaCompleta<ProcedimentoInterface> guia, Date dataFinal) {
		Assert.isNotNull(dataFinal, MensagemErroEnum.DATA_FECHAMENTO_REQUERIDA.getMessage());
		if(Utils.compareData(dataFinal, new Date()) > 0){
			throw new RuntimeException(MensagemErroEnum.DATA_FECHAMENTO_SUPERIOR_A_DATA_ATUAL.getMessage());
		}
		
		if(dataFinal.before(guia.getDataAtendimento())){
			throw new RuntimeException(MensagemErroEnum.DATA_FECHAMENTO_INFERIOR_A_DATA_ATENDIMENTO.getMessage());
		}
	}

	/**
	 * Método que remove diarias e procedimentos solicitados ou autorizados baseado
	 * na data de fechamento da guia e na data de solicitação dessas diarias e pro-
	 * cedimentos inseridos.
	 * @param guia 
	 * @param dataFechamento
	 * @return guia com procedimentos e diarias removidas caso estes tenham sido solicitados
	 * no dia ou após a data de fechamento
	 * @throws Exception
	 */
	public void removeItens(GuiaCompleta guia, Date dataDeFechamento) throws Exception {

		migrarDiarias(guia, dataDeFechamento);
		migrarProcedimentos(guia);
		migrarPacotes(guia);
		guia.recalcularValores();
	}

	private void migrarPacotes(GuiaCompleta guia) {
		Iterator iterator = guia.getItensPacote().iterator();
		while (iterator.hasNext()) {
			ItemPacote itemPacote = (ItemPacote) iterator.next();
			if (!itemPacote.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()) && !itemPacote.isSituacaoAtual(SituacaoEnum.NEGADO.descricao())) {
				if (!itemPacote.getNestaParcial()){
					itensPacoteDaNovaGuia.add(itemPacote);
					iterator.remove();
				}
			}	
		}
	}

	private void migrarProcedimentos(GuiaCompleta guia) {
		Set<ProcedimentoInterface> procedimentos = guia.getProcedimentos();
		Iterator<ProcedimentoInterface> iterator = procedimentos.iterator();
		while (iterator.hasNext()){
			ProcedimentoInterface procedimento = iterator.next();
			if(procedimento != null && procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO){
				Boolean realizadoNestaParcial = ((ProcedimentoCirurgico) procedimento).getRealizadoNestaParcial();
				if(realizadoNestaParcial != null && !realizadoNestaParcial){
					procedimentosDaNovaGuia.add(procedimento);
					iterator.remove();
				}
			}
		}
		guia.setProcedimentos(procedimentos);		
	}

	private void migrarDiarias(GuiaCompleta guia, Date dataDeFechamento) {
		Iterator<ItemDiaria> diarias = guia.getItensDiaria().iterator();
		
		while (diarias.hasNext()) {
			ItemDiaria itemDiaria 					= diarias.next();
			if (itemDiaria.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao()) || itemDiaria.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao())) {
				boolean inicioDaDiariaAposFechamento	= false;
				boolean solicitacaoAposFechamento 	 	= false;
	
				if(itemDiaria.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao())) {
					Date inicioDaDiaria 		 = itemDiaria.getDataInicial();
					inicioDaDiariaAposFechamento = inicioDaDiaria != null && inicioDaDiaria.after(dataDeFechamento);
				}else{
					Date dataDaSolicitacao 		= itemDiaria.getSituacao().getDataSituacao();
					solicitacaoAposFechamento	= Utils.compareData(dataDaSolicitacao, dataDeFechamento) >= 0;
				}
	
				if(inicioDaDiariaAposFechamento || solicitacaoAposFechamento) {
					diarias.remove();
					diariasDaNovaGuia.add(itemDiaria);
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private void tocarItemGuia(Collection<? extends ItemGuia> itens){
		for (ItemGuia item : itens) {
			item.tocarObjetos();
		}
	}
	
	@SuppressWarnings("deprecation")
	public GuiaCompleta salvarGuia(GuiaCompleta<ProcedimentoInterface> guia) throws Exception {
		CommandCorrecaoValorGuia command = null;
		GuiaCompleta guiaReturn = guia;
		
		boolean isFechamentoParcial = guia.getFechamentoParcial();
		boolean isUltimaGuiaDaInternacao = false;
		if (guia.isInternacao()) {
			isUltimaGuiaDaInternacao = ((GuiaInternacao) guia).isUltimaGuiaDaInternacao();
		}

		if(isFechamentoParcial && isUltimaGuiaDaInternacao){
			this.removeItens(guia, guia.getDataTerminoAtendimento());
			setIgnoreValidacao(true);
					
			if(guia.getSolicitante() != null) {
				HibernateUtil.currentSession().lock(guia.getSolicitante(), LockMode.NONE);
			}
			
			this.tipoGuia = guia.getClass();
			
			GuiaInternacao guiaNova  = (GuiaInternacao) super.criarGuia(guia.getSegurado(), guia.getPrestador(), guia.getPrestador().getUsuario(), guia.getSolicitante(), guia.getProfissional(), guia.getEspecialidade(), null, Utils.format(guia.getDataTerminoAtendimento()),MotivoEnum.FECHAMENTO_PARCIAL.getMessage() , Agente.PRESTADOR);
			guiaNova.setTipoTratamento(guia.getTipoTratamento());
			guiaNova.getSituacao().setDescricao(SituacaoEnum.ABERTO.descricao());
			//DESCOMENTAR QUANDO FOR FAZER DEPLOY DESSE CAMPO
			guiaNova.setGuiaParcial(true);
			
			if(guia.isGuiaFilha() && guia.getGuiaOrigem().isInternacao()){
				guia.getGuiaOrigem().addGuiaFilha(guiaNova);
			} else{
				guia.addGuiaFilha(guiaNova);
			}
			
			guiaNova.setAutorizacao(getAutenticacao(guiaNova));
			
			for (ProcedimentoInterface procedimento : procedimentosDaNovaGuia){ 
				guiaNova.addProcedimento(procedimento);
			}

			for (ItemDiaria diaria : diariasDaNovaGuia){ 
				guiaNova.addItemDiaria(diaria);
			}
			
			for (ItemPacote itemPacote : itensPacoteDaNovaGuia){ 
				guiaNova.addItemPacote(itemPacote);
			}

			guiaNova.setValorAnterior(new BigDecimal(0));
			
			guiaNova.recalcularValores();
			
			ImplDAO.save(guiaNova);
			guiaReturn = guiaNova;
		}
		
		guia.recalcularValores();
		
		//Salva a guia com atualização dos indices relacionados
		super.salvarGuia(guia);

		return guiaReturn;
	}
	
	public GuiaCompleta getGuiaInstanceFor(UsuarioInterface usuario) {
		String mensagem = "Não foi possível fechar guia.";
		try {
			Class[] paramType = {UsuarioInterface.class};
			Constructor<GuiaCompleta> construtor = tipoGuia.getConstructor(paramType);
			GuiaCompleta guiaNova = construtor.newInstance(usuario);
			return guiaNova;
			
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new RuntimeException(mensagem);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new RuntimeException(mensagem);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException(mensagem);
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new RuntimeException(mensagem);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException(mensagem);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException(mensagem);
		}
	}

	public GuiaCompleta selecionarGuia(GuiaCompleta guia) throws Exception {
		guia.tocarObjetos();
		return super.selecionarGuia(guia);
	}

	protected Date getData(String data,String mensagem) throws ValidateException{
		try {
			return Utils.parse(data);
		} catch (Exception e) {
			throw new ValidateException(mensagem);
		}
	}

	protected String getAutenticacao(GuiaSimples guia) throws ValidateException {	
		Boolean isGuiaFilhaNova = guia.getGuiaOrigem() != null && guia.getAutorizacao() == null;  
		if(isGuiaFilhaNova){
			int index = guia.getGuiaOrigem().getGuiasFilhasDoFechamentoParcial().size();
			return (guia.getGuiaOrigem().getIdGuia().toString()+ getSufixo(index));
		}else return null; 
	}
}