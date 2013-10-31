package br.com.infowaypi.ecarebc.atendimentos.honorario;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.com.infowaypi.ecare.atendimentos.TipoGuiaEnum;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacoteHonorario;
import br.com.infowaypi.ecarebc.atendimentos.validators.AbstractGuiaValidator;
import br.com.infowaypi.ecarebc.enums.GrauDeParticipacaoEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ItemGuiaFaturamento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoHonorario;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/** 
 * Guia de Honorário Médico, representa uma entidade agregadora de
 * honorários externos no sistema. A guia é gerada durante o processo
 * de registro de honorários individuais (RegistrarHonorarioIndividual).
 * A GHM possui como guiaOrigem a guia que contem os procedimentos que 
 * deram origem aos honorários médicos e é uma guiaFilha desta. 
 * 
 * @changes Eduardo
 *
 */
@SuppressWarnings({ "unchecked", "serial", "rawtypes"})
public class GuiaHonorarioMedico extends GuiaSimples<ProcedimentoInterface> {

	public static Collection<AbstractGuiaValidator> guiaHonorarioValidators = new ArrayList<AbstractGuiaValidator>();

	/**
	 * Atributo transiente para setar o grau de participação da guia.
	 */
	private int 	grauDeParticipacao;
	/**
	 * São os honorários criados em uma guia de honorário médico individual.
	 */
	private Set<HonorarioExterno> honorariosMedico;
	
	public GuiaHonorarioMedico() {
		super();
		this.setDataTerminoAtendimento(new Date());
		honorariosMedico = new HashSet<HonorarioExterno>();
	}

	private static GuiaHonorarioMedico criarGHM(Profissional medico,
			GeradorGuiaHonorarioInterface guiaOrigem, UsuarioInterface usuario,
			AbstractSegurado segurado, Prestador prestador, String situacao) {

		GuiaHonorarioMedico ghm = new GuiaHonorarioMedico();
		ghm.setProfissional(medico);
		
		ghm.setSegurado(segurado);
		ghm.setPrestador(prestador);
		
		ghm.mudarSituacao(usuario, situacao, MotivoEnum.GERACAO_DE_HONORARIO.getMessage(), new Date());
		
		GuiaSimples<ProcedimentoInterface> guiaSimplesOrigem = (GuiaSimples<ProcedimentoInterface>) guiaOrigem;
		guiaSimplesOrigem.addGuiaFilha(ghm);
		ghm.setGuiaOrigem(guiaSimplesOrigem);
		ghm.setDataRecebimento(new Date());
		ghm.setGrauDeParticipacao(GrauDeParticipacaoEnum.RESPONSAVEL.getCodigo());
		
		/*
		 * Essa linha de código foi incluida para conformizar
		 * a funcionalidade atualizaValorApresentado da classe guia simples
		 * que acessa o valorTotalApresentado e como antes o set era privado
		 * não era possível informar esse valor durante os testes, apenas usando
		 * o metodo receberGuia. */
		ghm.setValorTotalApresentado(BigDecimal.ZERO);
		
		return ghm;
	}
	
	public String getGrauDeParticipacaoFormatado(){
		return GrauDeParticipacaoEnum.getEnum(this.getGrauDeParticipacao()).getDescricao();
	}
	
	public int getGrauDeParticipacao() {
		/*
		 * Se a guia ja tiver algum honorario, retorna o valor real(persistido).
		 */
		if (this.honorariosMedico != null && !this.honorariosMedico.isEmpty())
			return this.honorariosMedico.iterator().next().getGrauDeParticipacao();
		return grauDeParticipacao;
	}
	
	/**
	 * Como a guia não tem a propriedade "grau de participação", esse método percorre
	 * todos os seus honorários, setando seus respectivos grauDeParticipacao para o 
	 * valor informado.
	 * {@link Honorario#setGrauDeParticipacao(int)}
	 * @param grauDeParticipacao
	 */
	public void setGrauDeParticipacao(int grauDeParticipacao) {
		this.grauDeParticipacao = grauDeParticipacao;
		setGrauDeParticipacaoDosHonorarios(grauDeParticipacao);
	}
	
	/**
	 * Seta o grau de participação dos honorarios da guia.
	 * @param grauDeParticipacao
	 */
	public void setGrauDeParticipacaoDosHonorarios(int grauDeParticipacao){
		if (this.honorariosMedico != null){
			for (HonorarioExterno honorario : this.honorariosMedico) {
				honorario.setGrauDeParticipacao(grauDeParticipacao);
			}
		}
	}
	
	@Override
	public Date getDataVencimento() {
		return null;
	}

	@Override
	public TipoGuiaEnum getTipoGuiaEnum() {
		return TipoGuiaEnum.HONORARIO_MEDICO;
	}

	@Override
	public String getTipo() {
		return "Guia Honorário Médico";
	}

	@Override
	public boolean isAtendimentoUrgencia() {
		return false;
	}

	@Override
	public boolean isCirurgiaOdonto() {
		return false;
	}

	@Override
	public boolean isCompleta() {
		return false;
	}

	@Override
	public boolean isConsulta() {
		return false;
	}

	@Override
	public boolean isConsultaEletiva() {
		return false;
	}

	@Override
	public boolean isConsultaOdonto() {
		return false;
	}

	@Override
	public boolean isConsultaOdontoUrgencia() {
		return false;
	}

	@Override
	public boolean isConsultaUrgencia() {
		return false;
	}

	@Override
	public boolean isExame() {
		return false;
	}

	@Override
	public boolean isExameEletivo() {
		return false;
	}

	@Override
	public boolean isExameOdonto() {
		return false;
	}

	@Override
	public boolean isInternacao() {
		return false;
	}

	@Override
	public boolean isInternacaoEletiva() {
		return false;
	}

	@Override
	public boolean isInternacaoUrgencia() {
		return false;
	}

	@Override
	public boolean isRegulaConsumo() {
		return false;
	}

	@Override
	public boolean isSimples() {
		return true;
	}

	@Override
	public boolean isTratamentoSeriado() {
		return false;
	}

	@Override
	public boolean isUrgencia() {
		return false;
	}

	@Override
	public boolean isUrgenciaOuAtendimentoSubsequente() {
		return false;
	}

	@Override
	public boolean isHonorarioMedico() {
		return true;
	}
	
	public Set<HonorarioExterno> getHonorariosMedico() {
		return honorariosMedico;
	}
	
	/**
	 * Fornece os honorários, de uma guia de honorário médico NÃO CANCELADA,
	 * que estejam na(s) situação(ões) informada(s).
	 * @param situacoes
	 * @return
	 */
	private Set<HonorarioExterno> getHonorariosNaSituacao(String... situacoes){
		Collection<String> situacoesList = Arrays.asList(situacoes);
		Set<HonorarioExterno> resultado = new HashSet<HonorarioExterno>();
		if (!this.isCancelada()) {
			for (HonorarioExterno honorario: this.getHonorariosMedico()){
				if(situacoesList.contains(honorario.getSituacao().getDescricao())){
					resultado.add(honorario);
				}
			}
		}
		return resultado;
	}
	
	public Set<HonorarioExterno> getHonorariosEmSituacaoGerado(){
		return getHonorariosNaSituacao(SituacaoEnum.GERADO.descricao());
	}
	
	public Set<HonorarioExterno> getHonorariosEmSituacaoGeradoEGlosado(){
		return getHonorariosNaSituacao(SituacaoEnum.GLOSADO.descricao(), SituacaoEnum.GERADO.descricao());
	}
	
	/**
	 * Método sobrescrito para que, em se tratando de uma guia de honorário médico, ele
	 * forneça os honorários da guia, uma vez que ela não contém procedimentos (a implementação
	 * do método da classe mãe percorre a  lista de procedimentos da guia, e por isso não serve
	 * no caso de guia de honorário médico).
	 * Fornece exatamente o mesmo que {@link #getHonorariosMedico()}
	 * @return
	 */
	@Override
	public List<Honorario> getHonorarios() {
		return new ArrayList<Honorario>(getHonorariosMedico());
	}
	
	public void setHonorariosMedico(Set<HonorarioExterno> honorariosMedico) {
		this.honorariosMedico = honorariosMedico;
	}
	
	@Override
	public void tocarObjetos() {
		super.tocarObjetos();
		this.getHonorariosMedico().size();
	}
	
	public void removeHonorarioInternoAposGeracaoHonorarioExterno() throws Exception {
		Set<HonorarioExterno> honorariosGHM = this.getHonorariosMedico();
		List<Honorario> honorariosGuiaOrigem = this.getGuiaOrigem().getHonorarios();
		Set<ItemGuiaFaturamento> igfs = new HashSet<ItemGuiaFaturamento>();
			
		for (Honorario honorarioExterno : honorariosGHM) {
			if (this.getGuiaOrigem().getItensGuiaFaturamento() != null) {
				for(Iterator<ItemGuiaFaturamento> it = this.getGuiaOrigem().getItensGuiaFaturamento().iterator(); it.hasNext();){
					ItemGuiaFaturamento igf = it.next();
					if (igf.getGrauParticipacao().equals(honorarioExterno.getGrauDeParticipacao()) &&
							igf.getProcedimento().equals(honorarioExterno.getProcedimento())) {
						it.remove();
						ImplDAO.delete(igf);
					} else {
						igfs.add(igf);
					}
				}
			}

			for (Honorario honorarioInterno : honorariosGuiaOrigem) {
				if (honorarioInterno.getGrauDeParticipacao() == honorarioExterno.getGrauDeParticipacao() &&
						honorarioInterno.getProcedimento().equals(honorarioExterno.getProcedimento())) {
					honorarioInterno.mudarSituacao(this.getUsuarioDoFluxo(), SituacaoEnum.GLOSADO.descricao(), "Geração de Honorário Externo.", new Date());
				}
			}
		}
		this.getGuiaOrigem().setItensGuiaFaturamento(igfs);
	}
	
	public HonorarioExterno addHonorario(UsuarioInterface usuario, ProcedimentoInterface procedimento) throws ValidateException {
		HonorarioExterno honorario = criarHonorario(usuario, procedimento);
		procedimento.addHonorarioGuiaHonorarioMedico(honorario);
		this.getHonorariosMedico().add(honorario);
		
		return honorario;
	}
	
	private HonorarioExterno criarHonorario(UsuarioInterface usuario, ProcedimentoInterface procedimento) {
		HonorarioExterno honorario = new HonorarioExterno(usuario);
		
		honorario.setGrauDeParticipacao(this.getGrauDeParticipacao());
		honorario.setProfissional(this.getProfissional());
		honorario.setProcedimento(procedimento);
		
		honorario.setIncluiVideo(procedimento.getIncluiVideoProxy());
		honorario.setHorarioEspecial(procedimento.getHorarioEspecialProxy());
		mudarSituacaoParaGeradoCasoOHonorarioSejaResponsavel(procedimento,
				honorario.getGrauDeParticipacao(), usuario, honorario.getProfissional());
		
		this.processaPorcetagem(honorario, procedimento);

		honorario.setGuiaHonorario(this);
		honorario.recalculaValorTotal();
		return honorario;
	}
	
	public HonorarioExterno addHonorario(UsuarioInterface usuario, ItemPacoteHonorario item) {
		HonorarioExterno honorario = new HonorarioExterno(usuario);
		honorario.setGrauDeParticipacao(this.getGrauDeParticipacao());
		honorario.setProfissional(this.getProfissional());
		honorario.setItemPacoteHonorario(item);
		honorario.setGuiaHonorario(this);
		item.setHonorarioExterno(honorario);
		this.getHonorariosMedico().add(honorario);
		
		for (ProcedimentoInterface procedimento : item.getProcedimentosCompativeisComAGuiaOrigem()) {
			mudarSituacaoParaGeradoCasoOHonorarioSejaResponsavel(procedimento,
					honorario.getGrauDeParticipacao(), usuario, honorario.getProfissional());
		}
		honorario.setPorcentagem(item.getPorcentagem());
		
		honorario.setGuiaHonorario(this);
		honorario.recalculaValorTotal();
		return honorario;
	}
	
	private void processaPorcetagem(HonorarioExterno honorario, ProcedimentoInterface procedimento) {
		boolean isGrauResponsavel = this.getGrauDeParticipacao() != GrauDeParticipacaoEnum.RESPONSAVEL.getCodigo();
		boolean isGrauAnestesista = this.getGrauDeParticipacao() != GrauDeParticipacaoEnum.ANESTESISTA.getCodigo();
		
		if (isGrauResponsavel || isGrauAnestesista) {
			BigDecimal porcentagemProxy = procedimento.getPorcentagemProxy();
			if (porcentagemProxy != null){
				honorario.setPorcentagem(porcentagemProxy);
			}else{
				honorario.setPorcentagem(procedimento.getPorcentagem());
			}
		} else {
			processaPorcentagemAuxialiaresEAuxiliarAnestesita(honorario, procedimento);
		}
	}

	private void processaPorcentagemAuxialiaresEAuxiliarAnestesita(HonorarioExterno honorario, ProcedimentoInterface procedimento) {
		boolean isGrauAuxiliarAnestesista = this.getGrauDeParticipacao() == GrauDeParticipacaoEnum.ANESTESISTA.getCodigo();
		
		if (isGrauAuxiliarAnestesista){
			Honorario honorarioAnestesista = procedimento.getHonorarioExterno(GrauDeParticipacaoEnum.ANESTESISTA.getCodigo());
			if (honorarioAnestesista != null){
				honorario.setPorcentagem(honorarioAnestesista.getPorcentagem());
			}
		}else{
			honorario.setPorcentagem(procedimento.getPorcentagem());
		}
		
		boolean isGrauAuxiliar1 = this.getGrauDeParticipacao() == GrauDeParticipacaoEnum.PRIMEIRO_AUXILIAR.getCodigo();
		boolean isGrauAuxiliar2 = this.getGrauDeParticipacao() == GrauDeParticipacaoEnum.SEGUNDO_AUXILIAR.getCodigo();
		boolean isGrauAuxiliar3 = this.getGrauDeParticipacao() == GrauDeParticipacaoEnum.TERCEIRO_AUXILIAR.getCodigo();
		
		if (isGrauAuxiliar1 || isGrauAuxiliar2 || isGrauAuxiliar3){
			Honorario honorarioResponsavel = procedimento.getHonorarioExterno(GrauDeParticipacaoEnum.RESPONSAVEL.getCodigo());
			if (honorarioResponsavel != null){
				honorario.setPorcentagem(honorarioResponsavel.getPorcentagem());
			}
		}else{
			honorario.setPorcentagem(procedimento.getPorcentagem());
		}
	}
	
	public Integer getQuantidadeHonorarios() {
		return this.getHonorariosMedico().size();
	}
	
	@Override
	public boolean isGuiaImpressaoNova(){
		return true;
	}
	
	@Override
	public void recalcularValores() {
		this.setValorAnterior(this.getValorTotal());
		this.setValorTotal(BigDecimal.ZERO);
		
		for (HonorarioExterno honorario : this.getHonorariosEmSituacaoGerado()) {
			honorario.recalculaValorTotal();
			this.setValorTotal(this.getValorTotal().add(honorario.getValorTotal()));
		}
		
		for (ProcedimentoInterface procedimentoOutro : this.getProcedimentosNaoCanceladosENegados()) {
			if (!procedimentoOutro.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao())) {
				this.setValorTotal(this.getValorTotal().add(procedimentoOutro.getValorTotal()));
			}
		}
		this.setValorPagoPrestador(this.getValorTotal());
	}
	
	public boolean isGuiaHonorarioAnestesista() {
		for (HonorarioExterno honorario : honorariosMedico) {
			if(honorario.getGrauDeParticipacao() == GrauDeParticipacaoEnum.ANESTESISTA.getCodigo() || honorario.getGrauDeParticipacao() == GrauDeParticipacaoEnum.AUXILIAR_ANESTESISTA.getCodigo()) {
				return true;
			}
		}
		
		return false;
	}
	
	public void removerHonorarioExterno(HonorarioExterno honorario){
		honorario.mudarSituacao(null, SituacaoEnum.CANCELADO.descricao(), "Excluido pelo auditor", new Date());
		this.recalcularValores();
	}
	
	public static GuiaHonorarioMedico criarGuiaHonorarioPacote(Profissional medico, GeradorGuiaHonorarioInterface guiaOrigem, UsuarioInterface usuario,
			AbstractSegurado segurado, Prestador prestador, Collection<ItemPacoteHonorario> itensPacote) throws ValidateException {

		GuiaHonorarioMedico guiaHonorario = criarGHM(medico, guiaOrigem, usuario, segurado, prestador, SituacaoEnum.FECHADO.descricao());

		for (ItemPacoteHonorario itemPacoteHonorario : itensPacote) {
			guiaHonorario.addHonorario(usuario, itemPacoteHonorario);
		}
		
		GuiaSimples<ProcedimentoInterface> guiaSimples = (GuiaSimples<ProcedimentoInterface>) guiaOrigem;
		guiaSimples.recalcularValores();
		guiaHonorario.recalcularValores();
		return guiaHonorario;
		}
		
	public static GuiaHonorarioMedico criarGuiaHonorario(Profissional medico, Integer grauDeParticipacao,
			GeradorGuiaHonorarioInterface guiaOrigem, UsuarioInterface usuario, AbstractSegurado segurado, Prestador prestador, 
			Collection<ProcedimentoHonorario> procedimentosOutros) throws Exception {

		Prestador prestadorProprio = medico.getPrestadorProprioPeloCPF();
		Prestador prestadorDaGuia = prestadorProprio != null ? prestadorProprio : prestador; 
		
		GuiaHonorarioMedico guiaHonorario = criarGHM(medico, guiaOrigem, usuario, segurado, prestadorDaGuia, SituacaoEnum.RECEBIDO.descricao());
		
		if (grauDeParticipacao != null) {
			guiaHonorario.setGrauDeParticipacao(grauDeParticipacao);
			Set<ProcedimentoInterface> procedimentosComHonorariosMedicos = guiaOrigem.getProcedimentosQueVaoGerarHonorario();

			for (ProcedimentoInterface procedimento : procedimentosComHonorariosMedicos) {
				guiaHonorario.addHonorario(usuario, procedimento);
			}
		}

		GuiaSimples<ProcedimentoInterface> guiaSimples = (GuiaSimples<ProcedimentoInterface>) guiaOrigem;
		guiaSimples.recalcularValores();

		if (procedimentosOutros != null && !procedimentosOutros.isEmpty()) {
			for (ProcedimentoInterface proc : procedimentosOutros) {
				proc.setProfissionalResponsavel(medico);

				proc.getSituacao().setDescricao(SituacaoEnum.AUTORIZADO.descricao());
				proc.getSituacao().setMotivo(MotivoEnum.AUTORIZADO_NO_REGISTRO_DE_HONORARIOS.getMessage());
			}
			guiaHonorario.addAllProcedimentos(procedimentosOutros);
		}
		guiaHonorario.recalcularValores();

		return guiaHonorario;
	}
	
	/**
	 * Muda a situação do procedimento para honorário gerado e seta o profissional responsável,
	 * caso o honorário gerado seja de grau = RESPONSAVEL
	 */
	private static void mudarSituacaoParaGeradoCasoOHonorarioSejaResponsavel(ProcedimentoInterface procedimento,
					Integer grauDeParticipacao, UsuarioInterface usuario, Profissional medico) {
		
		if (GrauDeParticipacaoEnum.RESPONSAVEL.getCodigo().equals(grauDeParticipacao)) {
			/*Utilizado para manter a compatibilidade da informação. Sempre que um procedimento não tiver profissioanl responsavel informado e for gerado um honorario
			para grau de cirurgião, o profissional informado no honorário, será inserido dentro do procedimento.*/
			if (procedimento.getProfissionalResponsavel() == null){
				procedimento.setProfissionalResponsavel(medico);
			}

			if (!procedimento.isSituacaoAtual(SituacaoEnum.PROCEDIMENTO_COM_HONORARIO_GERADO.descricao())) {
				procedimento.mudarSituacao(usuario, SituacaoEnum.PROCEDIMENTO_COM_HONORARIO_GERADO.descricao(), MotivoEnum.GERACAO_DE_HONORARIO.getMessage(), new Date());
			}
		}
	}
	
	public Set<ProcedimentoHonorario> getProcedimentosHonorario(){
		Set<ProcedimentoHonorario> procedimentosHonorarios = new HashSet<ProcedimentoHonorario>();
		
		for (ProcedimentoInterface proc: this.getProcedimentosNaoGlosadosNemCanceladosNemNegados()){
			if (proc.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_HONORARIO){
				procedimentosHonorarios.add((ProcedimentoHonorario)proc);
			}
		}
		
		return procedimentosHonorarios;
	}
	
	public Set<ProcedimentoHonorario> getProcedimentosHonorarioNaoGlosadosNemCanceladosNemNegados(){
		Set<ProcedimentoHonorario> procedimentosHonorarios = new HashSet<ProcedimentoHonorario>();
		
		for (ProcedimentoHonorario proc: this.getProcedimentosHonorario()){
			if (!proc.getSituacao().getDescricao().equals(SituacaoEnum.CANCELADO.descricao())
					&& !proc.getSituacao().getDescricao().equals(SituacaoEnum.NAO_AUTORIZADO.descricao())
					&& !proc.getSituacao().getDescricao().equals(SituacaoEnum.NEGADO.descricao())
					&& !proc.getSituacao().getDescricao().equals(SituacaoEnum.GLOSADO.descricao()))
				procedimentosHonorarios.add(proc);
		}
		
		return procedimentosHonorarios;
	}
	
	@Override
	public boolean isAcompanhamentoAnestesico() {
		return false;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof GuiaHonorarioMedico)) {
			return false;
		}
		
		GuiaHonorarioMedico otherObject = (GuiaHonorarioMedico) object;
		boolean isEquals = new EqualsBuilder()
					.append(this.getIdGuia(), otherObject.getIdGuia())
					.append(this.getTipo(), otherObject.getTipo())
					.append(this.getGrauDeParticipacao(), otherObject.getGrauDeParticipacao())
					.append(this.getProfissional(), otherObject.getProfissional())
					.isEquals();
	 
		return isEquals;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
						.append(this.getIdGuia())
						.append(this.getTipo())
						.append(this.getGrauDeParticipacao())
						.append(this.getProfissional())
						.toHashCode();
	}

	@Override
	public void addItemGuiaFaturamento(ItemGuiaFaturamento itemGuiaFaturamento) {
		throw new UnsupportedOperationException("Guias de Honorário Médico não podem ter ItemGuiaFaturamento!");
	}

	@Override
	public Set<ItemGuiaFaturamento> getItensGuiaFaturamento() {
		return new HashSet<ItemGuiaFaturamento>();
	}

	@Override
	public void setItensGuiaFaturamento(Set<ItemGuiaFaturamento> itensFaturamento) {}
		
	public void glosar(HonorarioExterno honorario, UsuarioInterface usuario, Date date) {
		glosar(honorario, usuario, date, MotivoEnum.GLOSADO_AUTOMATICAMENTE.getMessage());
	}
	
	public void glosar(HonorarioExterno honorario, UsuarioInterface usuario, Date date, String motivoGlosa) {
		Assert.isTrue(contemHonorario(honorario), "Este honorário não pertence a esta guia.");
		honorario.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), motivoGlosa, date);
		this.recalcularValores();
		
		boolean naoContemHonorario = this.getHonorariosEmSituacaoGerado().isEmpty();
		if (naoContemHonorario) {
			this.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), motivoGlosa, date);
		}
	}

	public boolean contemHonorario(HonorarioExterno honorario) {
		for (HonorarioExterno he : this.honorariosMedico) {
			if (he.equals(honorario)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected GuiaSimples<ProcedimentoInterface> newInstance() {
		return new GuiaHonorarioMedico();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		GuiaHonorarioMedico clone = (GuiaHonorarioMedico) super.clone();
		for (HonorarioExterno honorario : this.getHonorariosMedico()) {
			clone.getHonorariosMedico().add((HonorarioExterno) honorario.clone());
		}
		return clone;
	}
	
	/**
	 * @return os honorários relativos à pacote
	 */
	public Set<HonorarioExterno> getHonorariosPacote(){
		Set<HonorarioExterno> honorariosPacotes = new HashSet<HonorarioExterno>();
		for (HonorarioExterno honorario : this.getHonorariosMedico()) {
			if(honorario.isHonorarioPacote()){
				honorariosPacotes.add(honorario);
			}
		}
		return honorariosPacotes;
}
	
	public boolean isGuiaSomentePacote(){
		return !this.getHonorariosPacote().isEmpty();
	}
	
	@Override
	public boolean isRecursoGlosa() {
		// TODO Auto-generated method stub
		return false;
	}
}