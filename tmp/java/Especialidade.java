package br.com.infowaypi.ecarebc.associados;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.com.infowaypi.ecarebc.associados.validators.AbstractValidator;
import br.com.infowaypi.ecarebc.associados.validators.EspecialidadeValidator;
import br.com.infowaypi.ecarebc.atendimentos.enums.ClasseEspecialidade;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.pessoa.PessoaFisicaInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * Classe que representa uma especialidade médica no sistema.
 * @author root
 * @changes Danilo Nogueira Portela, Marcos Roberto 08.08.2012 
 */
public class Especialidade implements Constantes, Serializable {
	
	public static final Integer CLASSE_ODONTOLOGIA = ClasseEspecialidade.ODONTOLOGIA.getCodigo();
	public static final Integer CLASSE_MEDICINA = ClasseEspecialidade.MEDICINA.getCodigo();
	public static final Integer CLASSE_FISIOTERAPIA = ClasseEspecialidade.FISIOTERAPIA.getCodigo();
	public static final Integer CLASSE_PSICOLOGIA = ClasseEspecialidade.PSICOLOGIA.getCodigo();
	public static final Integer CLASSE_NUTRICAO = ClasseEspecialidade.NUTRICAO.getCodigo();
	
	private static final long serialVersionUID = 1L;
	private Long idEspecialidade;
	/**
	 * Área na qual a especialidade se enquadra.
	 * @see ClasseEspecialidade
	 */
	private ClasseEspecialidade classe;
	//NÃO USADO
	private String identificadorNoSMC;
	/**
	 * Nome da especialidade.
	 */
	private String descricao;
	/**
	 * Sexo para o qual a especialidade se aplica.
	 * @see PessoaFisicaInterface#SEXO_MASCULINO
	 */
	private Integer sexo;
	
	/**
	 * Intervalo do limite de idade permitido para utilização de atendimento por especialidade.
	 * Quando não houver valor informado, ou seja estiver nulo, esta restrição não será executada.
	 * Ex: até 14 anos para especialidade (Pediatria)
	 *     acima de 60 anos (Geriatria)
	 */
	private Integer idadeLimiteInicio;
	private Integer idadeLimiteFim;
	
	private Set<Profissional> profissionais;
	private Set<Prestador> prestadores;
	private Set<TabelaCBHPM> procedimentosDaTabelaCBHPM;
	/**
	 * Indica se a especialidade se encontra ativa no sistema.
	 */
	private boolean ativa;
	
	/**
	 * Código da especialidade anterior à migração da base de dados.
	 */
	private String codigoLegado;
	
	@SuppressWarnings("rawtypes")
	private static Collection<AbstractValidator> especialidadeValidators = new ArrayList<AbstractValidator>();
	static{
		especialidadeValidators.add(new EspecialidadeValidator());
	}
	
	public Especialidade() {
		this(null);
	}
	
	public Especialidade(UsuarioInterface usuario){
		super();
		profissionais = new HashSet<Profissional>();
		prestadores = new HashSet<Prestador>();
		procedimentosDaTabelaCBHPM = new HashSet<TabelaCBHPM>();
		classe = ClasseEspecialidade.NENHUMA;
//		this.mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(), "Cadastro da Especialidade.", new Date());
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public Integer getClasse() {
		return classe.getCodigo();
	}

	public void setClasse(Integer classe) {
		for (ClasseEspecialidade c : ClasseEspecialidade.values()) {
			if(c.getCodigo().equals(classe))
				this.classe = c;
		}
	}
	
	public String getIdentificadorNoSMC() {
		return identificadorNoSMC;
	}
	
	public void setIdentificadorNoSMC(String identificadorNoSMC) {
		this.identificadorNoSMC = identificadorNoSMC;
	}
	
	public Long getIdEspecialidade() {
		return idEspecialidade;
	}
	
	public void setIdEspecialidade(Long idEspecialidade) {
		this.idEspecialidade = idEspecialidade;
	}
	
	public Set<Prestador> getPrestadores() {
		return prestadores;
	}
	
	public void setPrestadores(Set<Prestador> prestadores) {
		this.prestadores = prestadores;
	}
	
	public Set<TabelaCBHPM> getProcedimentosDaTabelaCBHPM() {
		return procedimentosDaTabelaCBHPM;
	}
	
	public void setProcedimentosDaTabelaCBHPM(
			Set<TabelaCBHPM> procedimentosDaTabelaCBHPM) {
		this.procedimentosDaTabelaCBHPM = procedimentosDaTabelaCBHPM;
	}
	
	public Set<Profissional> getProfissionais() {
		return profissionais;
	}
	
	public void setProfissionais(Set<Profissional> profissionais) {
		this.profissionais = profissionais;
	}
	
	public Integer getSexo() {
		return sexo;
	}	
	
	public void setSexo(Integer sexo) {
		this.sexo = sexo;
	}
	
	public Integer getIdadeLimiteInicio() {
		return idadeLimiteInicio;
	}

	public void setIdadeLimiteInicio(Integer idadeLimiteInicio) {
		this.idadeLimiteInicio = idadeLimiteInicio;
	}

	public Integer getIdadeLimiteFim() {
		return idadeLimiteFim;
	}

	public void setIdadeLimiteFim(Integer idadeLimiteFim) {
		this.idadeLimiteFim = idadeLimiteFim;
	}

	public boolean isProcedimentoExistente(TabelaCBHPM procedimento){
		return this.getProcedimentosDaTabelaCBHPM().contains(procedimento);
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Especialidade)) {
			return false;
		}
		Especialidade otherObject = (Especialidade) object;
		return new EqualsBuilder()
			.append(this.descricao, otherObject.getDescricao())
			.append(this.sexo, otherObject.getSexo())
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.getDescricao())
			.append(this.getSexo())
			.toHashCode();
	}
	
	public void tocarObjetos(){
		this.getDescricao();
		this.getSexo();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Boolean validate() throws ValidateException {
		for (AbstractValidator validator : especialidadeValidators) {
			validator.execute(this);
		}
		return true;
	}

	public String getCodigoLegado() {
		return codigoLegado;
	}

	public void setCodigoLegado(String codigoLegado) {
		this.codigoLegado = codigoLegado;
	}

	public boolean isAtiva() {
		return ativa;
	}

	public void setAtiva(boolean ativa) {
		this.ativa = ativa;
	}
}
