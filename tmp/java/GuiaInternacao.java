package br.com.infowaypi.ecarebc.atendimentos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemGuia;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.atendimentos.alta.AltaHospitalar;
import br.com.infowaypi.ecarebc.atendimentos.alta.MotivoAlta;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GeradorGuiaHonorarioComponent;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GeradorGuiaHonorarioInterface;
import br.com.infowaypi.ecarebc.atendimentos.validators.fechamento.FechamentoInternacaoStrategy;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoHonorario;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.utils.ProcedimentoUtils;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author Marcus bOolean
 *
 */
@SuppressWarnings("serial")
public abstract class GuiaInternacao extends GuiaCompleta<ProcedimentoInterface> implements GeradorGuiaHonorarioInterface {
	
	public static final int PRIORIDADE_ALTA 	= 1;
	public static final int PRIORIDADE_MEDIA 	= 2;
	public static final int PRIORIDADE_BAIXA 	= 3;

	private AltaHospitalar altaHospitalar;
	/**
	 * Informa se a guia foi originada a partir de fechamento parcial de outra guia
	 */
	private boolean guiaParcial;
	
	private GeradorGuiaHonorarioComponent geradorHonorarioComponent;
	
	public GuiaInternacao() {
		this(null);
	}
	
	public GuiaInternacao(UsuarioInterface usuario) {
		super(usuario);
		this.geradorHonorarioComponent = new GeradorGuiaHonorarioComponent(this);
	}
	
	public AltaHospitalar getAltaHospitalar() {
		return altaHospitalar;
	}

	public void setAltaHospitalar(AltaHospitalar altaHospitalar) {
		this.altaHospitalar = altaHospitalar;
	}
	
	@Override
	public int getPrazoInicial() {
		return 0;
	}

	@Override
	public String getTipo() {
		return null;
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
		return true;
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
		return false;
	}

	@Override
	public boolean isTratamentoSeriado() {
		return false;
	}

	@Override
	public boolean isUrgencia() {
		return false;
	}

	/**
	 * 
	 * @return retorna o valor gasto com diarias na guia.
	 */
	public BigDecimal getValorDiariasAutorizadas(){
		BigDecimal valor = BigDecimal.ZERO;
		for (ItemDiaria item : getDiariasAutorizadas()) {
			valor = MoneyCalculation.getSoma(valor, item.getValorTotal());
		}
		return valor;
	}
	
	/**
	 * 
	 * @return Retorna a primeira acomodação autorizada para a internação.
	 */
	public ItemDiaria getPrimeiraDiaria(){
		List<ItemDiaria> itens = this.getDiariasAutorizadas();
		Collections.sort(itens, new Comparator<ItemDiaria>(){

			public int compare(ItemDiaria item1, ItemDiaria item2) {
				return item1.getSituacao().getDataSituacao().compareTo(item2.getSituacao().getDataSituacao());
			}
			
		});
		if(itens.isEmpty())
			return null;
		
		return itens.get(0);
	}
	
	/**
	 * @return Retorna o procedimento principal de uma internação.
	 */
	public ProcedimentoInterface getProcedimentoPrincipal(){
		List<ProcedimentoInterface> procedimentos = new ArrayList<ProcedimentoInterface>(this.getProcedimentosCirurgicosNaoCanceladosENegados());

		Collections.sort(procedimentos, new Comparator<ProcedimentoInterface>(){
			public int compare(ProcedimentoInterface p1, ProcedimentoInterface p2) {
				
				return p1.getSituacao(1).getDataSituacao().compareTo(p2.getSituacao(1).getDataSituacao());
			}
		});
		
		if(procedimentos.isEmpty()){
			return null;
		}
		
		return procedimentos.get(0);
	}

	public void registrarAlta(Date dataAlta, UsuarioInterface usuario,
			MotivoAlta motivo) {
		
		AltaHospitalar altaHospitalar = new AltaHospitalar(dataAlta, usuario, motivo, this);
		this.setAltaHospitalar(altaHospitalar);
		this.mudarSituacao(usuario, SituacaoEnum.ALTA_REGISTRADA.descricao(), motivo.getDescricaoCompleta(), new Date());
		this.setDataTerminoAtendimento(dataAlta);
	}
	
	public boolean isGuiaParcial() {
		return guiaParcial;
	}

	public void setGuiaParcial(boolean guiaParcial) {
		this.guiaParcial = guiaParcial;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void tocarObjetos() {
		super.tocarObjetos();
		
		if(this.getAltaHospitalar() != null) {
			this.getAltaHospitalar().getDataDeAlta();
			this.getAltaHospitalar().getMotivo().getDescricao();
		}	
		
		for (GuiaSimples guiaFilha : getGuiasFilhasDoFechamentoParcial()) {
			guiaFilha.getSituacoes().size();
		}
	}
	
	public String getNaturezaInternacao(){
		if(this.getTipoTratamento() == GuiaCompleta.TRATAMENTO_CLINICO)
			return "Clínico";
		if(this.getTipoTratamento() == GuiaCompleta.TRATAMENTO_CIRURGICO)
			return "Cirúrgico";
		if(this.getTipoTratamento() == GuiaCompleta.TRATAMENTO_OBSTETRICO)
			return "Obstétrico";
		return "";
	}


	
	@Override
	public Date getDataVencimento() {
		Calendar vencimento = Calendar.getInstance();
		vencimento.setTime(getDataMarcacao());
		vencimento.add(Calendar.DAY_OF_MONTH, PRAZO_VENCIMENTO_INTERNACOES);
		return vencimento.getTime();
	}

	public Boolean getAutorizado() {
		return null;
	}

	
	public void setAutorizado(Boolean autorizado) {
		if(autorizado == null){
		}else if(autorizado){
			for (ProcedimentoCirurgicoInterface procedimento : this.getProcedimentosCirurgicos()) {
				procedimento.mudarSituacao(this.getUsuarioDoFluxo(), SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZACAO_PROCEDIMENTO_CIRURGICO.getMessage(), new Date());
			}
			
			if(this.isInternacaoUrgencia())
				this.mudarSituacao(this.getUsuarioDoFluxo(), SituacaoEnum.ABERTO.descricao(), MotivoEnum.PACIENTE_INTERNACAO_URGENCIA.getMessage(), new Date());
			else
				this.mudarSituacao(this.getUsuarioDoFluxo(), SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.INTERNACAO_ELETIVA.getMessage(), new Date());
			
			if (!this.getInternacaoNaoProrrogacao()){
				for (ItemDiaria item : this.getItensDiaria()) {
					if(item.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())){
						if(this.isInternacaoUrgencia())
							item.calculaDataInicial();
						item.mudarSituacao(this.getUsuarioDoFluxo(), SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZADO_PELO_AUDITOR.getMessage(), new Date());
					}
				}
			}
			
			for (ItemPacote item : this.getItensPacote()) {
				if(item.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())){
					item.mudarSituacao(this.getUsuarioDoFluxo(), SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZADO_PELO_AUDITOR.getMessage(), new Date());
				}
			}
		}else{
			this.mudarSituacao(this.getUsuarioDoFluxo(), SituacaoEnum.NAO_AUTORIZADO.descricao(), MotivoEnum.SOLICITACAO_NAO_AUTORIZADA.getMessage(), new Date());
			
			for (ItemGuia item : this.getItensDiaria()) {
				if(item.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())){
					item.mudarSituacao(this.getUsuarioDoFluxo(), SituacaoEnum.NAO_AUTORIZADO.descricao(), MotivoEnum.SOLICITACAO_NAO_AUTORIZADA.getMessage(), new Date());
				}
			}
		}
	}
	
	public boolean isGuiaImpressaoNova(){
		if (this.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao())|| this.isSituacaoAtual(SituacaoEnum.SOLICITADO_INTERNACAO.descricao()))
			return true;
		return false;	
	}

	public boolean isAltaRegistrada() {
		return getAltaHospitalar() != null;
	}
	
	public boolean isAutorizada(){
		if (this.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao()))
			return true;
		return false;
	}
	
	public boolean isSolicitada(){
		if (this.isSituacaoAtual(SituacaoEnum.SOLICITADO_INTERNACAO.descricao()))
			return true;
		return false;
	}
	
	public Date getDataSolicitacao(){
		SituacaoInterface situacao = this.getSituacao(SituacaoEnum.SOLICITADO_INTERNACAO.descricao());
		if (situacao!=null)
			return situacao.getDataSituacao();
		return null;
	}
	
	public Date getDataAutorizacao(){
		SituacaoInterface situacao = this.getSituacao(SituacaoEnum.AUTORIZADO.descricao());
		if (situacao!=null)
			return situacao.getDataSituacao();
		return null;
	}
	
	
	public int getPrioridadeAutorizacao(){
		if (this.isSituacaoAtual(SituacaoEnum.SOLICITADO_INTERNACAO.descricao())){
			Calendar dtSolicitacao = new GregorianCalendar();
			dtSolicitacao.setTime(this.getSituacao().getDataSituacao());
			int diferenca = Utils.diferencaEmDias(dtSolicitacao, Calendar.getInstance());
			if(diferenca <=2)
				return PRIORIDADE_BAIXA;
			if (diferenca > 2 && diferenca<=3)
				return PRIORIDADE_MEDIA;
			
			return PRIORIDADE_ALTA;
		} else
			return 0;
	}

	@SuppressWarnings("unchecked")
	public boolean isUltimaGuiaDaInternacao() {
		if (this.isGuiaParcial()) {
			Set<GuiaSimples<Procedimento>> guiasFilhasDoFechamentoParcial = this.getGuiaOrigem().getGuiasFilhasDoFechamentoParcial();
			GuiaSimples<Procedimento> ultimaParcial = Collections.max(guiasFilhasDoFechamentoParcial, new Comparator<GuiaSimples>() {
					@Override
					public int compare(GuiaSimples o1, GuiaSimples o2) {
						if(o1.getAutorizacao() == null)
							return -1;
						if(o2.getAutorizacao() == null)
							return 1;
						if(o1.getAutorizacao().length() < o2.getAutorizacao().length()) {
							return -1;
						} else if (o1.getAutorizacao().length() >  o2.getAutorizacao().length()) {
							return 1;
						} else {
							return o1.getAutorizacao().compareTo(o2.getAutorizacao());
						}
					}
				}
			);
			return this.equals(ultimaParcial);
		}else 
			return this.getGuiasFilhasDoFechamentoParcial().isEmpty();
	}
	
	/**
	 * Fornece todas as guias parciais de uma mesma internação, menos a própria guia.
	 * Por exemplo: as guias 123, 123A, 123B e 123C. Se estou visualizando a guia 123B,
	 * esse método deve retornar 123, 123A e 123C.
	 * @return List<GuiaInternacao>
	 */
	public Set<GuiaInternacao> getOutrasParciais(){
		Set<GuiaInternacao> outrasParciais = new HashSet<GuiaInternacao>();
		
		if (this.isGuiaParcial()){
			outrasParciais.addAll(this.getGuiaOrigem().getGuiasFilhasDoFechamentoParcial());
			if (this.getGuiaOrigem().isInternacao()){
				GuiaInternacao guiaOrigemRecarregada = recarregarGuiaOrigem();
				this.setGuiaOrigem(guiaOrigemRecarregada);
				outrasParciais.add((GuiaInternacao)this.getGuiaOrigem());
			}
			outrasParciais.remove(this);
		} else
			outrasParciais.addAll(this.getGuiasFilhasDoFechamentoParcial());
		
		return outrasParciais;
	}

	private GuiaInternacao recarregarGuiaOrigem() {
		GuiaInternacao guiaRecarrregada = ImplDAO.findById(this.getGuiaOrigem().getIdGuia(), GuiaInternacao.class);
		guiaRecarrregada.getGuiasFilhas().size();
		return guiaRecarrregada;
	}

	public void fechar(Boolean parcial, Boolean ignorarValidacao, String observacao, Date dataFinal, UsuarioInterface usuario)
			throws ValidateException {
		this.setFechamentoParcial(parcial);
		super.fechar(parcial, ignorarValidacao, observacao, dataFinal, usuario);
		if (this.getFechamentoParcial())
			this.getSituacao().setMotivo(MotivoEnum.FECHAMENTO_PARCIAL.getMessage());
		ProcedimentoUtils.aplicaDescontoDaViaDeAcesso(this);
	}
	
	@Override
	protected void validaFechamento(Boolean parcial, Date dataFinal, UsuarioInterface usuario) throws ValidateException {
		super.validaFechamento(parcial, dataFinal, usuario);
		FechamentoInternacaoStrategy.validaFechamento(this, parcial, dataFinal, usuario);
	}

	@Override
	public boolean isAberta() {
		return super.isAberta() || this.isSituacaoAtual(SituacaoEnum.PRORROGADO.descricao());
	}
	
	@Override
	public Set<ProcedimentoInterface> getProcedimentosAptosAGerarHonorariosMedicos() {	
		return this.geradorHonorarioComponent.getProcedimentosAptosAGerarHonorariosMedicos();
	}
	
	@Override
	public Set<Diaria> getAcomodacoes() {
		return this.geradorHonorarioComponent.getAcomodacoes();
	}
	
	@Override
	public Set<ProcedimentoHonorario> getProcedimentosHonorario() {
		return this.geradorHonorarioComponent.getProcedimentosHonorario();
	}

	@Override
	public Set<ProcedimentoInterface> getProcedimentosQueVaoGerarHonorario() {
		return this.geradorHonorarioComponent.getProcedimentosQueVaoGerarHonorario();
	}
	
	@Override
	public Set<ProcedimentoInterface> getProcedimentosQueVaoGerarHonorarioAnestesista() {
		return this.geradorHonorarioComponent.getProcedimentosQueVaoGerarHonorarioAnestesista();
	}
	
	@Override
	public Set<ProcedimentoInterface> getProcedimentosQueAindaPodemGerarHonorariosAnestesitas() {
		return this.geradorHonorarioComponent.getProcedimentosQueAindaPodemGerarHonorariosAnestesitas();
	}
	
	@Override
	public Set<ProcedimentoInterface> getProcedimentosQueAindaPodemGerarHonorarios() {
		return this.geradorHonorarioComponent.getProcedimentosQueAindaPodemGerarHonorarios();
	}
	
	@Override
	public boolean isPossuiProcdimentosQueAindaGeramHonorarios() {
		return this.geradorHonorarioComponent.isPossuiProcdimentosQueAindaGeramHonorarios();
	}
	
	@Override
	public ProcedimentoCirurgicoInterface getProcedimentoMaisRecenteRealizadoPeloProfissional(Profissional profissional) {
		return this.geradorHonorarioComponent.getProcedimentoMaisRecenteRealizadoPeloProfissional(profissional);
	}
	
	@Override
	public Set<ProcedimentoCirurgicoInterface> getProcedimentoQueJaPossuemDataDeRealizacao() {
		return this.geradorHonorarioComponent.getProcedimentoQueJaPossuemDataDeRealizacao();
	}
	
	@Override
	public Set<ProcedimentoCirurgicoInterface> getProcedimentoQueNaoPossuemDataDeRealizacao() {
		return this.geradorHonorarioComponent.getProcedimentoQueNaoPossuemDataDeRealizacao();
	}
	
	@Override
	public boolean isProfissionalPodeRegistrarHonorarioIndividual(Profissional profissional) {
		return this.geradorHonorarioComponent.isProfissionalPodeRegistrarHonorarioIndividual(profissional);
	}
	
	/**
	 * Indica se a guia está em uma das seguintes situações:
	 * <ul>
	 * <li> Enviado(a) para o SAÚDE RECIFE </li>
	 * <li> Recebido(a) </li>
	 * <li> Devolvido(a) </li>
	 * <li> Auditado(a) </li>
	 * <li> Glosado(a) </li>
	 * <li> Faturado(a) </li>
	 * <li> Pago(a) </li>
	 * <li> Inapto(a) </li>
	 * </ul>
	 * @return
	 */
	public boolean foiEnviada() {

		List<String> situacoesAposEnvio = Arrays.asList(SituacaoEnum.ENVIADO.descricao(), 
				SituacaoEnum.RECEBIDO.descricao(), SituacaoEnum.DEVOLVIDO.descricao(), 
				SituacaoEnum.AUDITADO.descricao(), SituacaoEnum.GLOSADO.descricao(), 
				SituacaoEnum.FATURADA.descricao(), SituacaoEnum.PAGO.descricao(),
				SituacaoEnum.INAPTO.descricao());

		return situacoesAposEnvio.contains(this.getSituacao().getDescricao());
	}
	
	@Override
	public void setProfissionalDoFluxo(Profissional profissional) {
		this.geradorHonorarioComponent.setProfissionalDoFluxo(profissional);
	}
	
	@Override
	public void setGeracaoParaPrestadorMedico(boolean geracaoParaHonorarioMedico) {
		this.geradorHonorarioComponent.setGeracaoParaPrestadorMedico(geracaoParaHonorarioMedico);
	}

	@Override
	public Set<ProcedimentoCirurgico> getProcedimentosQueGeraramHonorariosExternos() {
		return this.geradorHonorarioComponent.getProcedimentosQueGeraramHonorariosExternos();
	}
	
	@Override
	public Set<ProcedimentoInterface> getProcedimentosAptosAGerarHonorariosAnestesista() {
		return this.geradorHonorarioComponent.getProcedimentosAptosAGerarHonorariosAnestesista();
	}
	
	@Override
	public int getPrioridadeEmAuditoria() {
		return this.geradorHonorarioComponent.getPrioridadeEmAuditoria();
	}
	
	@Override 
	public Set<ProcedimentoCirurgico> getProcedimentosCirurgicosQueAindaPodemGerarHonorariosAnestesitas() {
		return this.geradorHonorarioComponent.getProcedimentosCirurgicosQueAindaPodemGerarHonorariosAnestesitas();
	}
	
	@Override
	public Set<Procedimento> getProcedimentosExameQueGeraramHonorariosExternos() {
		return this.geradorHonorarioComponent.getProcedimentosExameQueGeraramHonorariosExternos();
	}

	
	@Override 
	public Set<Procedimento> getProcedimentosNormaisQueAindaPodemGerarHonorariosAnestesitas() {
		return this.geradorHonorarioComponent.getProcedimentosNormaisQueAindaPodemGerarHonorariosAnestesitas();
	}
	

	@Override
	public Object clone() throws CloneNotSupportedException {
		GuiaInternacao clone = (GuiaInternacao) super.clone();
		
		for(RegistroTecnicoDaAuditoria registroTecnico: this.getRegistrosTecnicosDaAuditoria()){
			RegistroTecnicoDaAuditoria novoRegistroTecnico = (RegistroTecnicoDaAuditoria)registroTecnico.clone();
			novoRegistroTecnico.setGuia(clone);
			clone.getRegistrosTecnicosDaAuditoria().add(novoRegistroTecnico);
		}
		
		return clone;
	}
	
	public void setGeradorHonorarioComponent(GeradorGuiaHonorarioComponent geradorHonorarioComponent) {
		this.geradorHonorarioComponent = geradorHonorarioComponent;
	}
}
