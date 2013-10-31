package br.com.infowaypi.ecarebc.associados;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.com.infowaypi.ecarebc.associados.validators.AbstractValidator;
import br.com.infowaypi.ecarebc.associados.validators.ProfissionalValidator;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.DependenteFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.FluxoFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.TitularFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.faturamento.Faturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.HonorarioMedico;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.Like;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.financeiro.InformacaoFinanceira;
import br.com.infowaypi.msr.financeiro.InformacaoFinanceiraInterface;
import br.com.infowaypi.msr.pessoa.PessoaFisica;
import br.com.infowaypi.msr.pessoa.PessoaFisicaInterface;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;


/**
 * Classe que representa um profissional assosciado no sistema.
 * @author root
 * @changes Danilo Nogueira Portela
 */
@SuppressWarnings("unchecked")
public class Profissional extends ImplColecaoSituacoesComponent implements TitularFinanceiroInterface, Constantes{
  
	private static final long serialVersionUID = 1L;
	
	public static final int TIPO_PROFISSIONAL_MEDICO = 1;
	public static final int TIPO_PROFISSIONAL_DENTISTA = 2;
	public static final int TIPO_PROFISSIONAL_NUTRICIONISTA = 3;
	public static final int TIPO_PROFISSIONAL_PSICOLOGO = 4;
	public static final int TIPO_PROFISSIONAL_FISIOTERAPEUTA = 6;
	public static final int TIPO_PROFISSIONAL_OUTROS = 5;
	
	private Long idProfissional;
	private PessoaFisicaInterface pessoaFisica;
	private Set<Prestador> prestadores;
	private Set<Faturamento> faturamentos;
	private Set<Especialidade> especialidades;
	private Set<GuiaSimples> guias;
	/**
	 * Número de inscrição do profissional no Conselho Regional de Medicina.
	 */
	private String crm;
	private String conselho;
	/**
	 * concatenação dos campos {@link #crm} e  nome ({@link br.com.infowaypi.msr.pessoa.PessoaFisica})
	 */
	private String crmNome;
	private InformacaoFinanceiraInterface informacaoFinanceira;
	private Integer tipoProfissional;
	/**
	 * Se refere ao prestador onde o profissional recebeu seus honorários no último faturamento.
	 */
	private Long destinoFaturamento;
	private Set<HonorarioMedico> honorariosMedicos;

	//para migração
	private String codigoLegado;
	
	private static Collection<AbstractValidator> profissionalValidator = new ArrayList<AbstractValidator>();
	static{
		profissionalValidator.add(new ProfissionalValidator());
	}
	
	public Profissional() {
		this(null);
	}
	
	public Profissional(UsuarioInterface usuario){
		this.mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao() , MotivoEnum.CADASTRO_PROFISSIONAL.getMessage(), new Date());
		informacaoFinanceira = new InformacaoFinanceira();
		pessoaFisica = new PessoaFisica();
		prestadores = new HashSet<Prestador>();
		especialidades = new HashSet<Especialidade>();
		guias = new HashSet<GuiaSimples>();
		faturamentos = new HashSet<Faturamento>();
		honorariosMedicos = new HashSet<HonorarioMedico>();
	}

	public Long getDestinoFaturamento() {
		return destinoFaturamento;
	}

	public void setDestinoFaturamento(Long destinoFaturamento) {
		this.destinoFaturamento = destinoFaturamento;
	}

	public HonorarioMedico getHonorarioMedico(Date competencia) {
		for (HonorarioMedico honorarioMedico : this.getHonorariosMedicos()) {
			if(Utils.compareData(competencia, honorarioMedico.getCompetencia()) == 0 && !honorarioMedico.getFaturamento().isFaturamentoPassivo()){
				return honorarioMedico;
			}
		}
		return null;
	}
	
	public HonorarioMedico getHonorarioMedicoPassivo(Date competencia) {
		for (HonorarioMedico honorarioMedico : this.getHonorariosMedicos()) {
			if(Utils.compareData(competencia, honorarioMedico.getCompetencia()) == 0 && honorarioMedico.getFaturamento().isFaturamentoPassivo()){
				return honorarioMedico;
			}
		}
		return null;
	}
	
	public Set<HonorarioMedico> getHonorariosMedicos() {
		return honorariosMedicos;
	}

	public void setHonorariosMedicos(Set<HonorarioMedico> honorariosMedicos) {
		this.honorariosMedicos = honorariosMedicos;
	}

	public String getCrm() {
		return crm;
	}

	public void setCrm(String crm) {
		this.crm = crm;
	}

	public Set<Especialidade> getEspecialidades() {
		return especialidades;
	}

	public void setEspecialidades(Set<Especialidade> especialidades) {
		this.especialidades = especialidades;
	}

	public Set<GuiaSimples> getGuias() {
		return guias;
	}

	public void setGuias(Set<GuiaSimples> guias) {
		this.guias = guias;
	}

	public Long getIdProfissional() {
		return idProfissional;
	}

	public void setIdProfissional(Long idProfissional) {
		this.idProfissional = idProfissional;
	}

	public PessoaFisicaInterface getPessoaFisica() {
		return pessoaFisica;
	}

	public void setPessoaFisica(PessoaFisicaInterface pessoaFisica) {
		this.pessoaFisica = pessoaFisica;
	}

	public Set<Prestador> getPrestadores() {
		return prestadores;
	}

	public void setPrestadores(Set<Prestador> prestadores) {
		this.prestadores = prestadores;
	}
	
	public Set<Faturamento> getFaturamentos() {
		return faturamentos;
	}

	public Faturamento getFaturamento(Date competencia) {
		for (Faturamento faturamento : this.getFaturamentos()) {
			if((faturamento.getCompetencia().compareTo(competencia) == 0) && (faturamento.getStatus() == Constantes.FATURAMENTO_ABERTO)){
				return faturamento;
			}
		}
		return null;
	}
	
	public void setFaturamentos(Set<Faturamento> faturamentos) {
		this.faturamentos = faturamentos;
	}

	public String getCrmNome() {
		return crmNome;
	}
	
	public void setCrmNome(String crmNome) {
		this.crmNome = crmNome;
	}
	
	public InformacaoFinanceiraInterface getInformacaoFinanceira() {
		if(this.informacaoFinanceira == null){
			informacaoFinanceira = new InformacaoFinanceira();
		}
		return this.informacaoFinanceira;
	}

	public void setInformacaoFinanceira(InformacaoFinanceiraInterface informacaoFinanceira) {
		this.informacaoFinanceira = informacaoFinanceira;
	}

	public boolean isCredenciado(){
		if(prestadores != null && prestadores.size() > 0)
			return true;
		else 
			return false;
	}
	
	public List<Profissional> getCredenciado(String caracteres){
		List<Profissional> profissionais  = null;
		
		profissionais =  buscaProfissionais(caracteres);

		profissionais = removerNaoCredenciados(profissionais);
		
		return profissionais;
	}
	
	@SuppressWarnings("unchecked")
	private List<Profissional> buscaProfissionais(String caracteres){
		SearchAgent sa = new SearchAgent();
		
		Criteria crit = sa.createCriteriaFor(Profissional.class);
		crit.add(Restrictions.ilike("crmNome", caracteres, MatchMode.ANYWHERE));
		crit.add(Restrictions.eq("situacao.descricao", SituacaoEnum.ATIVO.descricao()));
		
		return (List<Profissional>)crit.list();
	}
	
	@SuppressWarnings("unchecked")
	private List<Profissional> removerNaoCredenciados(List<Profissional> profissionais) {
		List<Profissional> credenciados = new ArrayList<Profissional>();
		
		for (Iterator iterator = profissionais.iterator(); iterator.hasNext();) {
			Profissional profissional = (Profissional) iterator.next();
			
			if(profissional.isCredenciado())
				credenciados.add(profissional);
		}
		
		return credenciados;
	}

	//TODO mudar a comparação para cpf quando o cadastro tiver ok
	public boolean hasPrestadorProprio() {
		for (Prestador prestador : this.getPrestadores()) {
			if(this.getPessoaFisica().getNome().equals(prestador.getPessoaJuridica().getFantasia())){
				return true;
			}
		}
		return !this.getPrestadores().isEmpty();
	}
	
	//TODO mudar a comparação para cpf quando o cadastro tiver ok
	public Prestador getPrestadorProprio(){
		for (Prestador prestador : this.getPrestadores()) {
			if(this.getPessoaFisica().getNome().equals(prestador.getPessoaJuridica().getFantasia())){
				return prestador;
			}
		}
		return null;
	}

	public Boolean validate() throws ValidateException {
		this.crmNome = this.getPessoaFisica().getNome() + " - " + this.getCrm(); 
		
		for (AbstractValidator validator : this.profissionalValidator) {
			validator.execute(this);
		}
		return true;
	}
	
	/**
	 * Indica se já existe algum profissional na base de dados que tenha o mesmo número de conselho
	 * e mesmo conselho que o do profissional em questão.
	 * @return true se existe algum profissional com mesmo conselho e crm(número de conselho)
	 */
	public boolean hasSalvoComMesmoConselhoECrm() {
    	Criteria crit = HibernateUtil.currentSession().createCriteria(Profissional.class);
        ProjectionList proList = Projections.projectionList();
        proList.add(Projections.property("crm"));
        crit.setProjection(proList);
        
        List list = crit.list();
    	
    	for (Object crm : list) {
			if(crm.equals(this.getCrm())) {
				SearchAgent agent = new SearchAgent();
				agent.addParameter(new Equals("crm",crm));
				agent.addParameter(new Like("conselho",this.getConselho()));
				List<Profissional> profissionais = agent.list(Profissional.class);
				
				if (profissionais.isEmpty()){
					return false;
				} else if (profissionais.size()>1){
					return true;
				} else {
					Profissional profissionalDoBanco = profissionais.get(0);
					if (profissionalDoBanco!= null && !profissionalDoBanco.equals(this)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public void tocarObjetos(){
		this.getEspecialidades();
		this.getColecaoSituacoes();
		
		/* 
		 * Pedro Almir:
		 * Esse código foi incluído para resolver um problema
		 * relacionado ao acesso a colecaoSituacoes, entretanto
		 * não está "cheirando bem"!
		 * 
		 * Se alguém tiver uma solução melhor, por favor, melhore isso.*/
		try{
			HibernateUtil.currentSession().refresh(this.getColecaoSituacoes());
		}catch(Exception ex){
			System.out.println("This instance does not yet exist as a row in the database");
		}
		
		this.getColecaoSituacoes().getSituacoes().size();
		this.getSituacao();
		this.getSituacao().getDescricao();
		this.getSituacoes();
	}
	
	public boolean isPodeAtenderProcedimento(TabelaCBHPM procedimento){
		boolean flag = false;
		for(Especialidade especialidadeAtual : this.getEspecialidades()){
			if(especialidadeAtual.isProcedimentoExistente(procedimento)){
				flag = true;
				break; 
			}
		}
		return flag;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Profissional))
			return false;
		Profissional profissional = (Profissional) obj;
		return new EqualsBuilder()
				.append(profissional.getCrm(),this.crm)
				.append(profissional.getPessoaFisica().getNome(),this.pessoaFisica.getNome())
				.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.crm)
			.append(this.pessoaFisica.getNome())
			.toHashCode();	
	}

	public String getCodigoLegado() {
		return codigoLegado;
	}

	public void setCodigoLegado(String codigoLegado) {
		this.codigoLegado = codigoLegado;
	}
	
	public Set<Especialidade> getEspecialidades(Profissional prof){
		Set<Especialidade> especialidades = new HashSet<Especialidade>();
		for (Especialidade especialidade : prof.getEspecialidades()) {
			if(especialidade.isAtiva() && !especialidade.getDescricao().equals("Odontologia"))
				especialidades.add(especialidade);
		}
		return especialidades;
	}
	
	public Set<Especialidade> getEspecialidades(Profissional prof, boolean ativa){
		Set<Especialidade> especialidades = new HashSet<Especialidade>();
		for (Especialidade especialidade : prof.getEspecialidades()) {
			if(especialidade.isAtiva() == ativa && !especialidade.getDescricao().equals("Odontologia"))
				especialidades.add(especialidade);
		}
		return especialidades;
	}
	
	public Boolean getOdontologico(){
		for (Especialidade esp : this.getEspecialidades()) {
			if(esp.getDescricao().equals("Odontologia"))
				return true;
		}
		return false;
	}

	public Integer getTipoProfissional() {
		return tipoProfissional;
	}

	public void setTipoProfissional(Integer tipoProfissional) {
		this.tipoProfissional = tipoProfissional;
	}

	public Set<DependenteFinanceiroInterface> getDependentesFinanceiro() {
		return new HashSet<DependenteFinanceiroInterface>();
	}

	public Set<FluxoFinanceiroInterface> getFluxosFinanceiros() {
		Set<FluxoFinanceiroInterface> fluxos = new HashSet<FluxoFinanceiroInterface>();
		for (Faturamento faturamento : faturamentos) {
			fluxos.add(faturamento);
		}
		return fluxos;
	}
	
	/**
	 * Retorna o cpf do profissional
	 */
	public String getIdentificacao() {
		return this.getPessoaFisica().getCpf();
	}

	/**
	 * Retorna o nome do profissional
	 */
	public String getNome() {
		return this.getPessoaFisica().getNome();
	}

	public String getTipo() {
		return "Profissional";
	}
	
	public Prestador getPrestadorDestinoHonorario() {
		Prestador prestadorProprio = this.getPrestadorProprio();
		if(prestadorProprio != null && prestadorProprio.isSituacaoAtual(SituacaoEnum.ATIVO.descricao())){
			return prestadorProprio;
		}
		else{
			for (Prestador prest : this.getPrestadores()) {
				if (prest.isSituacaoAtual(SituacaoEnum.ATIVO.descricao()))
					return prest;
			}	
		}	
		return null;
	}

	public void ativar(String motivo) throws Exception {
		if (Utils.isStringVazia(motivo))
			throw new Exception("O motivo deve ser preenchido!");
		this.mudarSituacao(null, Constantes.SITUACAO_ATIVO, motivo, new Date());
	}

	public void desativar(String motivo) throws Exception {
		if (Utils.isStringVazia(motivo))
			throw new Exception("O motivo deve ser preenchido!");
		this.mudarSituacao(null, Constantes.SITUACAO_INATIVO, motivo,
				new Date());
	}
	
	public String getConselho() {
		return conselho;
	}

	public void setConselho(String conselho) {
		this.conselho = conselho;
	}
	
	public Prestador getPrestadorProprioPeloCPF(){
		String cpf = this.getPessoaFisica().getCpf();
		
		for (Prestador prestador: this.getPrestadores()){
			if (prestador.getPessoaJuridica().getCnpj().equals(cpf)){
				return prestador;
			}
		}
		
		return null;
	}

	/**
	 * retorna o primeito prestador Clinica do cadastro do profissional
	 * @return
	 */
	public Prestador getPrestadorClinica() {
		for (Prestador prest : this.getPrestadores()) {
			if (prest.isClinica() && prest.isSituacaoAtual(SituacaoEnum.ATIVO.descricao())){
				return prest;
			}
		}
		return null;
	}
	
	/**
	 * retorna o primeito prestador do tipo TIPO_PRESTADOR_MEDICOS (mesmo Médico Credenciado) do cadastro do profissional
	 * @return
	 */
	public Prestador getPrestadorMedico() {
		for (Prestador prest : this.getPrestadores()) {
			if (prest.getTipoPrestador().equals(TIPO_PRESTADOR_MEDICOS) && prest.isSituacaoAtual(SituacaoEnum.ATIVO.descricao())){
				return prest;
			}
		}
		return null;
	}
}