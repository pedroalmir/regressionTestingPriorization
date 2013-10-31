package br.com.infowaypi.ecarebc.procedimentos;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import br.com.infowaypi.ecare.cadastros.MotivoGlosa;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ItemGlosavel;
import br.com.infowaypi.ecarebc.atendimentos.enums.TipoProcedimentoEnum;
import br.com.infowaypi.ecarebc.atendimentos.honorario.Honorario;
import br.com.infowaypi.ecarebc.atendimentos.honorario.HonorarioExterno;
import br.com.infowaypi.ecarebc.atendimentos.validators.Criticavel;
import br.com.infowaypi.ecarebc.enums.GrauDeParticipacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.Faturamento;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.ColecaoSituacoesInterface;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;

public interface ProcedimentoInterface extends Criticavel, ColecaoSituacoesInterface, Cloneable, ItemGlosavel {

	// Constantes
	public static final int 	PROCEDIMENTO_NORMAL 			= 1;
	public static final int 	PROCEDIMENTO_ODONTO 			= 2;
	public static final int		PROCEDIMENTO_CIRURGICO 			= 3;
	public static final int 	PROCEDIMENTO_OUTROS 			= 4;
	public static final int 	PROCEDIMENTO_ODONTO_RESTAURACAO = 5;
	public static final int 	PROCEDIMENTO_HONORARIO 			= 6;
	public static final Float 	PORC_BILATERAL 					= 1.7f;

	public abstract Boolean validate(GuiaSimples guia) throws Exception;

	public abstract void calcularCampos();

	public abstract Boolean validate(UsuarioInterface usuario) throws Exception;

	public Integer getActionRegulacao();

	public void setActionRegulacao(Integer actionRegulacao);
	
	public abstract void aplicaValorAcordo();

	public abstract boolean getBilateral();

	public abstract void setBilateral(boolean bilateral);

	public abstract GuiaSimples getGuia();

	public abstract void setGuia(GuiaSimples guia);

	public abstract Long getIdProcedimento();

	public abstract void setIdProcedimento(Long idProcedimento);

	public abstract TabelaCBHPM getProcedimentoDaTabelaCBHPM();

	public abstract void setProcedimentoDaTabelaCBHPM(TabelaCBHPM procedimentoDaTabelaCBHPM);

	public abstract Float getValorAtualDaModeracao();

	public abstract void setValorAtualDaModeracao(Float valorAtualDaModeracao);

	public abstract BigDecimal getValorAtualDoProcedimento();
	
	public abstract BigDecimal getValorVirtualDoProcedimento();

	public abstract void setValorAtualDoProcedimento(
			BigDecimal valorAtualDoProcedimento);

	public abstract BigDecimal getValorTotal();
	
	public BigDecimal getValorTotalComAcordo(Prestador prestador);

	public abstract String getValorTotalFormatado();

	/**
	 * Retorna a data da última marcação do procedimento de um conjunto de guias
	 * 
	 * @param guias
	 * @return
	 */
	public abstract Date getDataUltimaMarcacao(Collection<GuiaSimples> guias)
			throws ValidateException;

	public abstract void tocarObjetos();

	public abstract int getTipoProcedimento();

	public abstract boolean equals(Object obj);

	public abstract BigDecimal getValorCoParticipacao();

	public abstract String getValorCoParticipacaoFormatado();

	public abstract void setValorCoParticipacao(BigDecimal valorCoParticipacao);

	public abstract boolean isGeraCoParticipacao();

	public abstract void setGeraCoParticipacao(boolean geraCoParticipacao);

	public abstract Integer getQuantidade();

	public abstract void setQuantidade(Integer quantidade);

	public abstract void setQuantidadeText(int quantidade);

	public abstract int getQuantidadeText();

	public abstract Boolean getAutorizado();

	public abstract void setAutorizado(Boolean autorizado);

	public abstract Profissional getAnestesista();

	public abstract void setAnestesista(Profissional anestesista) throws ValidateException ;
	
	public abstract Profissional getAuxiliarAnestesista();

	public abstract void setAuxiliarAnestesista(Profissional auxiliarAnestesista) throws ValidateException ;

	public abstract BigDecimal getValorAnestesista();
	
	public abstract BigDecimal getValorAnestesistaDoHonorario();

	public void setValorAnestesista(BigDecimal valorAnestesista);

	public void atualizaValorAnestesista();

	public abstract BigDecimal getPorcentagem();

	public abstract String getValorAnestesistaFormatado();

	public abstract Boolean isIncluiVideo();

	public abstract Boolean getIncluiVideo();
	
	public abstract void setIncluiVideo(Boolean incluiVideo);

	public abstract boolean isHorarioEspecial();

	public abstract void setHorarioEspecial(boolean horarioEspecial);

	public abstract void setPrestadorAnestesista(Prestador prestadorAnestesista);

	public abstract Prestador getPrestadorAnestesista();

	public abstract void setFaturamento(Faturamento faturamento);

	public abstract Faturamento getFaturamento();

	public abstract SituacaoInterface getSituacaoAnterior(SituacaoInterface situacao);

	public Boolean getSelecionado();

	public void setSelecionado(Boolean selecionado);

	public String getMotivo();

	public void setMotivo(String motivo);

	/*
	 * Ambos os métodos abaixo reference a um atributo transiente em
	 * Procedimento para verificar se ele está em processo de autorização
	 */
	public boolean isPassouPelaAutorizacao();

	public void setPassouPelaAutorizacao(boolean passouPelaAutorizacao);
	
	public abstract Profissional getProfissionalResponsavel();

	public abstract void setProfissionalResponsavel(Profissional profissionalResponsavel);

	public Profissional getProfissionalAuxiliar1();

	public void setProfissionalAuxiliar1(Profissional profissionalAuxiliar1) throws ValidateException ;

	public Profissional getProfissionalAuxiliar2();

	public void setProfissionalAuxiliar2(Profissional profissionalAuxiliar2) throws ValidateException ;

	public Profissional getProfissionalAuxiliar3();

	public void setProfissionalAuxiliar3(Profissional profissionalAuxiliar3) throws ValidateException ;
	
	public Profissional getProfissionalAuxiliar1Temp();
	public void setProfissionalAuxiliar1Temp(Profissional profissionalAuxiliar1);

	public Profissional getProfissionalAuxiliar2Temp();
	public void setProfissionalAuxiliar2Temp(Profissional profissionalAuxiliar2);

	public Profissional getProfissionalAuxiliar3Temp();
	public void setProfissionalAuxiliar3Temp(Profissional profissionalAuxiliar3);
	
	public Profissional getAnestesistaTemp();

	public Profissional getAuxiliarAnestesistaTemp();
	
	public abstract Profissional getProfissionalResponsavelTemp();

	public abstract void setProfissionalResponsavelTemp(Profissional profissionalResponsavelTemp);
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface#getValorAuxiliar1()
	 */
	public BigDecimal getValorAuxiliar1();
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface#getValorAuxiliar2()
	 */
	public BigDecimal getValorAuxiliar2();
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface#getValorAuxiliar3()
	 */
	public BigDecimal getValorAuxiliar3();
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface#getValorProfissionalResponsavel()
	 */
	public BigDecimal getValorProfissionalResponsavel();
	
	public BigDecimal getValorProfissionalResponsavelDoHonorario();
	
	/**
	 * Atributos transiente  utilizado no fluxo de solicitar exames pela central
	 */
	public void setPossuiPeriodicidade(boolean possuiPeriodicidade);
	public boolean isPossuiPeriodicidade();
	
	public Set<Honorario> getTodosOsHonorariosDoProcedimento();
	public Set<Honorario> getHonorariosGuiaOrigem();
	public Set<HonorarioExterno> getHonorariosGuiaHonorarioMedico();
	public void setHonorariosGuiaOrigem(Set<Honorario> honorariosGuiaOrigem);
	public void addHonorarioGuiaOrigem(UsuarioInterface usuario, int grauDeParticipacao, Profissional profissional, BigDecimal valorHonorario) ;
	public void addHonorarioGuiaHonorarioMedico(HonorarioExterno honorario) throws ValidateException;
	public boolean containsHonorario(int grauDeParticipacao);
	public boolean containsHonorarioInterno(int grauDeParticipacao);
	public boolean containsHonorarioExterno(int grauDeParticipacao);
	public Honorario getHonorarioInterno(int grauDeParticipacao);
	public Honorario getHonorarioExterno(int grauDeParticipacao);
	
	public Boolean getAdicionarHonorario();

	public void setAdicionarHonorario(Boolean adicionarHonorario);

	public BigDecimal getValorAuxiliarAnestesista();

	public Boolean getIncluiVideoProxy();

	public void setIncluiVideoProxy(Boolean incluiVideoProxy);

	public Boolean getHorarioEspecialProxy();

	public void setHorarioEspecialProxy(Boolean horarioEspecialProxy);

	public BigDecimal getPorcentagemProxy();

	public void setPorcentagemProxy(BigDecimal porcentagemProxy);
	
	public Map<GrauDeParticipacaoEnum, Profissional> getProfissionaisTemp();

	public Map<GrauDeParticipacaoEnum, Profissional> getProfissionaisAnestesistasTemp();
	
	/**
	 * Indica se o procedimento tem algum honorário gerado.
	 * Utilizado para proibir a GLOSA de qualquer procedimento que tenha honorário gerado (não cancelado nem glosado).
	 */
	public boolean contemAlgumHonorario();
	
	
	/**
	 * Indica se o procedimento já gerou algum honorário, independente da situação.
	 * Utilizado para proibir a EXCLUSÃO de qualquer procedimento que tenha gerado honorário.
	 * Assim, será possível apenas glosar o procedimento que já tenha gerado honorário.
	 * @return
	 */
	public boolean jaPossuiuAlgumHonorario();

	public Set<HonorarioExterno> getHonorariosExternosNaoCanceladosEGlosados();
	
	/**
	 * Atributo transiente usado na auditoria para glosa de procedimento cirúrgico.
	 * @return se o procedimento deve ser glosado
	 */
	public boolean isGlosar();
	
	/**
	 * Atributo transiente usado no cancelamento de procedimentos da guia.
	 * @return se o procedimento deve ser cancelado
	 */
	public boolean isCancelar();
	/**
	 * Atributo transiente usado na auditoria para glosa de procedimento cirúrgico.
	 */
	public void setGlosar(boolean glosar);
	
	/**
	 * Atributo transiente usado na auditoria para glosa de procedimento cirúrgico.
	 * @return O motivo da glosa.
	 */
	public MotivoGlosa getMotivoGlosaProcedimento();
	
	/**
	 * Atributo transiente usado na auditoria para glosa de procedimento cirúrgico.
	 */
	public void setMotivoGlosaProcedimento(MotivoGlosa motivoGlosaProcedimento);
	
	/**
	 * Atributo usado na auditoria para justificativa da glosa.
	 * @return A justificativa da glosa.
	 */
	public String getJustificativaGlosa();
	
	public void setJustificativaGlosa(String justificativaGlosa);

	public BigDecimal getValorDoProcedimento100PorCento();

	public void setPorcentagem(BigDecimal porcentagem);

	public SituacaoInterface getSituacaoAtual();
	
	public ItemGlosavel clone();
	
	public void calculaCoParticipacao();
	
	public ProcedimentoInterface newInstance();
	
	public ProcedimentoInterface newInstanceOthers();
	
	/**
	 * Informa se o procedimento está com seus honorarios auditados
	 */
	public boolean isAuditado();

	public void setAuditado(boolean auditado);
	
	/**
	 * Atributo usado na Auditoria de Guia com exames especiais
	 * @return o motivo de Insercao 
	 */
	public String getMotivoInsercao();
	
	/**
	 * Atributo usado na Auditoria de Guia com exames especiais
	 */
	public void setMotivoInsercao(String motivoInsercao);
	
	/**
	 * Método abstrato implementado em todas as subclasses para utilização no novo validate do SR
	 * @return TipoGuiaEnum
	 */
	public abstract TipoProcedimentoEnum getTipoProcedimentoEnum();
	
	public boolean isAnular();
	
	public void setAnular(boolean anular);
	
	public boolean getAnular();
	
	public void anular(UsuarioInterface usuario);
	
	public void desfazerAnular(UsuarioInterface usuario);
	
	public BigDecimal getValorModeradoDaTabelaCbhpmAoDobrar();

	public void setValorModeradoDaTabelaCbhpmAoDobrar(BigDecimal valorModeradoDaTabelaCbhpmAoDobrar);

}