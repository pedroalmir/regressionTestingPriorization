package br.com.infowaypi.ecare.services.honorariomedico.adapter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.honorario.Honorario;
import br.com.infowaypi.ecarebc.atendimentos.honorario.HonorarioExterno;
import br.com.infowaypi.ecarebc.enums.GrauDeParticipacaoEnum;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.ecarebc.utils.ProcedimentoUtils;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;

@SuppressWarnings({"unchecked"})
public class AdapterProcedimento {

	private static final int PORTE_ANESTESISCO_7 = 7;
	
	private Procedimento procedimento;
	private TabelaCBHPM cbhpm;
	private BigDecimal 	porcentagem;
	private Date 		dataRealizacao;
	
	private Set<AdapterHonorario> honorarios;
	private Set<AdapterHonorario> honorariosAnestesistas;
	
	private Set<HonorarioExterno> honorariosNovos;
	private ProcedimentoInterface procedimentoNovo;
	
	private Map<Integer, AdapterHonorario> 				honorariosPorGrau = new HashMap<Integer, AdapterHonorario>();
	private static final Comparator<AdapterHonorario> 	HONORARIO_COMPARATOR;
	
	/**
	 * usado para comparacao
	 */
	private boolean auditado;
	
	static {
		HONORARIO_COMPARATOR = new Comparator<AdapterHonorario>() {
			@Override
			public int compare(AdapterHonorario o1, AdapterHonorario o2) {
				Integer grauParticipacao1 = o1.getHonorario().getGrauDeParticipacao();
				Integer grauParticipacao2 = o2.getHonorario().getGrauDeParticipacao();
				return grauParticipacao1.compareTo(grauParticipacao2);
			}
		};
	}
	
	public AdapterProcedimento(Procedimento procedimento) {
		this.procedimento 	= procedimento;
		this.cbhpm 			= procedimento.getProcedimentoDaTabelaCBHPM();
		this.porcentagem 	= procedimento.getPorcentagem();
		this.auditado 		= procedimento.isAuditado();
		
		this.honorariosAnestesistas = new HashSet<AdapterHonorario>();
		this.honorarios 			= new HashSet<AdapterHonorario>();
		this.honorariosNovos 		= new HashSet<HonorarioExterno>();

		if (procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO) {
			this.dataRealizacao = ((ProcedimentoCirurgico) procedimento).getDataRealizacao();
		}

		this.povoar();
	}

	private void povoar() {
		Set<HonorarioExterno> honorariosDoProcedimento = procedimento.getHonorariosExternosNaoCanceladosEGlosados();
		
		for (HonorarioExterno honorario : honorariosDoProcedimento) {
			AdapterHonorario adapterHonorario = new AdapterHonorario(honorario);
			
			if (honorario.isHonorarioAnestesista()) {
				this.honorariosAnestesistas.add(adapterHonorario);
			} else {
				this.honorarios.add(adapterHonorario);
			}
			
			honorariosPorGrau.put(honorario.getGrauDeParticipacao(), adapterHonorario);
		}
	}

	public Procedimento getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(Procedimento procedimento) {
		this.procedimento = procedimento;
	}

	public TabelaCBHPM getCbhpm() {
		return cbhpm;
	}

	public void setCbhpm(TabelaCBHPM cbhpm) {
		this.cbhpm = cbhpm;
	}

	public BigDecimal getPorcentagem() {
		return porcentagem;
	}

	public void setPorcentagem(BigDecimal porcentagem) {
		this.porcentagem = porcentagem;
	}
	
	public Set<AdapterHonorario> getHonorarios() {
		return honorarios;
	}

	public void setHonorarios(Set<AdapterHonorario> honorarios) {
		this.honorarios = honorarios;
	}

	public Set<AdapterHonorario> getHonorariosAnestesistas() {
		return honorariosAnestesistas;
	}

	public void setHonorariosAnestesistas(Set<AdapterHonorario> honorariosAnestesistas) {
		this.honorariosAnestesistas = honorariosAnestesistas;
	}

	public Set<HonorarioExterno> getHonorariosNovos() {
		return honorariosNovos;
	}

	public void setHonorariosNovos(Set<HonorarioExterno> honorariosNovos) {
		this.honorariosNovos = honorariosNovos;
	}

	public Set<AdapterHonorario>  getTodosHonorariosNaoMarcadosPraGlosa(){
		Set<AdapterHonorario> resultado = new HashSet<AdapterHonorario>();
		
		for (AdapterHonorario adapterHonorario : this.getHonorarios()) {
			if (!adapterHonorario.getHonorario().getGlosado()){
				resultado.add(adapterHonorario);
			}
		}
		
		for (AdapterHonorario adapterHonorario : this.getHonorariosAnestesistas()) {
			if (!adapterHonorario.getHonorario().getGlosado()){
				resultado.add(adapterHonorario);
			}
		}
		
		return resultado; 
	}
	
	public Set<AdapterHonorario>  getTodosHonorarios(){
		Set<AdapterHonorario> resultado = new HashSet<AdapterHonorario>();
		
		resultado.addAll(this.getHonorarios());
		resultado.addAll(this.getHonorariosAnestesistas());
		
		return resultado; 
	}
	
	/**
	 * Adiciona um novo honorário no procedimento
	 *
	 * @param honorario
	 * @throws Exception
	 */
	public void addHonorario(HonorarioExterno honorario) throws Exception {
		
		//VALIDAR A INSERÇÃO DE HONORÁRIOS PARA AUXILIARES-ANESTESISTAS 
		validaInsercaoGrauAuxiliarAnestesista(honorario);
		validaInsercaoGrauCirurgiaoParaGrauCirurgiao(honorario);

		AdapterHonorario honorarioJaInserido = this.honorariosPorGrau.get(honorario.getGrauDeParticipacao());
		if (honorarioJaInserido == null) {
			honorario.getProfissional().getNome();
			honorario.getProfissional().getPrestadores().size();
			honorario.setProcedimento(this.procedimento);
			
			ProcedimentoInterface procedimentoDoHonorario = honorario.getProcedimento();
			BigDecimal valorHonorario = GrauDeParticipacaoEnum.getEnum(honorario.getGrauDeParticipacao()).getValorHonorario(procedimentoDoHonorario);
			honorario.setValorTotal(valorHonorario);
			
			this.honorariosNovos.add(honorario);
			AdapterHonorario adapterHonorario = new AdapterHonorario(honorario);
			this.honorariosPorGrau.put(honorario.getGrauDeParticipacao(), adapterHonorario);
		} else {
			throw new Exception("Este procedimento já contém um honorário para " + honorario.getGrauDeParticipacaoFormatado() + ".");
		}
		
	}

	private void validaInsercaoGrauCirurgiaoParaGrauCirurgiao(HonorarioExterno honorario) throws ValidateException {
		boolean isAuxiliarAnestesista 	= honorario.getGrauDeParticipacao() == GrauDeParticipacaoEnum.RESPONSAVEL.getCodigo();
		boolean isProcedimentoGlosado	= this.getProcedimento().getSituacao().getDescricao().equals(SituacaoEnum.GLOSADO.descricao());
		
		if(isAuxiliarAnestesista && isProcedimentoGlosado){
			throw new ValidateException(MensagemErroEnum.NAO_GERAR_HONORARIO_INDIVIDUAL_PARA_RESPONSAVEL_PRA_PROCEDIMENTO_GLOSADO.getMessage());
		}
	}

	private void validaInsercaoGrauAuxiliarAnestesista(HonorarioExterno honorario) throws Exception {
		
		boolean isAuxiliarAnestesista = honorario.getGrauDeParticipacao() == GrauDeParticipacaoEnum.AUXILIAR_ANESTESISTA.getCodigo();
		int porteDoProcedimento = this.getProcedimento().getProcedimentoDaTabelaCBHPM().getPorteAnestesicoFormatado();

		if(isAuxiliarAnestesista && !(porteDoProcedimento >= PORTE_ANESTESISCO_7)){
			throw new Exception(MensagemErroEnum.GERAR_HONORARIO_ANESTESISTA_AUXILIAR_APENAS_PARA_PORTE_ANESTESICO_SETE_OU_OITO.getMessage());
		}
	}
	
	public void removeHonorario(HonorarioExterno honorario) throws Exception {
		this.honorariosNovos.remove(honorario);
		this.honorariosPorGrau.remove(honorario.getGrauDeParticipacao());
	}

	public ProcedimentoInterface getNovoProcedimento(UsuarioInterface usuario) throws Exception {
		if (this.procedimentoNovo == null) {
			this.procedimentoNovo = createProcedimentoNovo(usuario);
		}
		
		return procedimentoNovo;
	}

	
	private ProcedimentoInterface createProcedimentoNovo(UsuarioInterface usuario) throws Exception {
		Procedimento procedimentoNovo = new Procedimento();
		
		procedimentoNovo.setAuditado(getProcedimento().isAuditado());
		procedimentoNovo.setPorcentagem(this.porcentagem);
		procedimentoNovo.setPorcentagemProxy(this.porcentagem);
		if (procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO) {
			procedimentoNovo.setDataRealizacao(this.procedimento.getDataRealizacao());
			
		}
		procedimentoNovo.setProcedimentoDaTabelaCBHPM(this.cbhpm);
		
		GuiaSimples guia = this.procedimento.getGuia();
		guia.addProcedimento(procedimentoNovo);
		
		this.setProfissionalResponsavel(procedimentoNovo);
		
		procedimentoNovo.setBilateral(this.procedimento.getBilateral());
		procedimentoNovo.setIncluiVideo(this.procedimento.isIncluiVideo());
		procedimentoNovo.setHorarioEspecial(this.procedimento.isHorarioEspecial());
		procedimentoNovo.mudarSituacao(usuario, SituacaoEnum.CONFIRMADO.descricao(), MotivoEnum.AUDITADO_AUTOMATICAMENTE.getMessage(), new Date());
		
		ProcedimentoUtils.calculaCamposConsiderandoPorcentagem(procedimentoNovo);		
		return procedimentoNovo;
	}

	private void setProfissionalResponsavel(ProcedimentoInterface procedimentoNovo) {
		//Procura dentre os honorarios não glosados
		Honorario honorario  = procedimento.getHonorarioExterno(GrauDeParticipacaoEnum.RESPONSAVEL.getCodigo());
		
		//se não encontrar, procura em meio aos glosados também o que foi recem-glosado
		if(honorario == null){
			for(Honorario hon: procedimento.getHonorariosGuiaHonorarioMedico()){
				boolean isGrauResponsavel 	= hon.getGrauDeParticipacaoFormatado().equals(GrauDeParticipacaoEnum.RESPONSAVEL.getDescricao());
				boolean isPersistido		= hon.getSituacao().getIdSituacao() != null;
				
				if(isGrauResponsavel && !isPersistido){
					honorario = hon;
				}
			}
		}
		
		if (honorario != null) {
			procedimentoNovo.setProfissionalResponsavel(honorario.getProfissional());
		}	
	}
	
	public List<AdapterHonorario> getHonorariosOrdenados() {
		List<AdapterHonorario> result = new ArrayList<AdapterHonorario>(honorarios);
		Collections.sort(result, HONORARIO_COMPARATOR);
		return result;
	}
	
	public List<AdapterHonorario> getHonorariosAnestesistasOrdenados() {
		List<AdapterHonorario> result = new ArrayList<AdapterHonorario>(honorariosAnestesistas);
		Collections.sort(result, HONORARIO_COMPARATOR);
		return result;
	}
	
	public Set<AdapterHonorario> getHonorariosFromProcedimento() {
		Set<AdapterHonorario> result = new HashSet<AdapterHonorario>();
		
		Set<HonorarioExterno> honorariosExternos = procedimento.getHonorariosGuiaHonorarioMedico();
		for (HonorarioExterno honorarioExterno : honorariosExternos) {
			result.add(new AdapterHonorario(honorarioExterno));
		}
		
		return result;
	}

	public Date getDataRealizacao() {
		return dataRealizacao;
	}

	public void setDataRealizacao(Date dateRealizacao) {
		this.dataRealizacao = dateRealizacao;
	}

	public boolean isAuditado() {
		return auditado;
	}

	public void setAuditado(boolean auditado) {
		this.auditado = auditado;
	}
	
}
