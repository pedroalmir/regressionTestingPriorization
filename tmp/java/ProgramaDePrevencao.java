package br.com.infowaypi.ecare.programaPrevencao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.CID;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.CollectionUtils;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author SR Team - Jefferson
 *                   Marcos Roberto 15.02.2012
 * 
 * Entidade responsável por encapsular as informações referentes ao evento, associação 
 * a um programa de prevenção a saude, e a associação dos beneficiários.
 */
public class ProgramaDePrevencao extends ImplColecaoSituacoesComponent {

	private static final long serialVersionUID = 1L;

	private Long idProgramaDePrevencao;
	private String nome;
	private String codigoENome;
	private String objetivo;
	private Date inicio;
	private Date fim;
	private Profissional responsavel;
	private Set<CID> cids;
	private Set<ProfissionalEspecialidade> medicos;
	private Set<AssociacaoSeguradoPrograma> associacaoSegurados;
	private Set<SeguradoDoPrograma> seguradosSelecionados;
	private Set<SeguradoDoPrograma> seguradosSelecionadosTemp = new HashSet<SeguradoDoPrograma>();
	private Boolean removido;
	private String motivoRemocao;

	private Set<Evento> eventos;

	public ProgramaDePrevencao() {
		cids = new HashSet<CID>();
		eventos = new HashSet<Evento>();
		medicos = new HashSet<ProfissionalEspecialidade>();
		associacaoSegurados = new HashSet<AssociacaoSeguradoPrograma>();
		seguradosSelecionados = new HashSet<SeguradoDoPrograma>();
		removido = false;
	}
	
	public List<SeguradoDoPrograma> getSeguradosSelecionados() {
		List<SeguradoDoPrograma> segurados = new ArrayList<SeguradoDoPrograma>(seguradosSelecionados);
		Utils.sort(segurados, "nomeSegurado");
		return segurados;
	}
	
	private boolean existeSeguradoNaLista(AbstractSegurado segurado){
		
		if (seguradosSelecionados.size()==0){
			return false;
		}
		else{
			for (SeguradoDoPrograma seguradoDoPrograma : seguradosSelecionados) {
				if (seguradoDoPrograma.getSegurado().equals(segurado)){
					return true;
				}
			}
		}
		return false;
	}
	
	public Set<SeguradoDoPrograma> getSeguradosCadastrados() {
		for (AssociacaoSeguradoPrograma associacao : associacaoSegurados) {
			if (!existeSeguradoNaLista(associacao.getSegurado()) && !associacao.getSituacao().getDescricao().equals(SituacaoEnum.REMOVIDO.descricao())){
				SeguradoDoPrograma seg = new SeguradoDoPrograma();
				seg.setCpf(associacao.getSegurado().getPessoaFisica().getCpf());
				seg.setNumeroDoCartao(associacao.getSegurado().getNumeroDoCartao());
				seg.setNomeSegurado(associacao.getSegurado().getPessoaFisica().getNome());
				seg.setSegurado(associacao.getSegurado());
				seg.setDataInsercao(associacao.getDataInsercao());
				seg.setDescricao(associacao.getSituacao().getDescricao());
				seguradosSelecionados.add(seg);
			}
		}
		return seguradosSelecionados;
	}

	public void setSeguradosSelecionados(Set<SeguradoDoPrograma> seguradosSelecionados) {
		this.seguradosSelecionados = seguradosSelecionados;
	}

	public Set<SeguradoDoPrograma> getSeguradosSelecionadosTemp() {
		return seguradosSelecionadosTemp;
	}

	public void setSeguradosSelecionadosTemp(
			Set<SeguradoDoPrograma> seguradosSelecionadosTemp) {
		this.seguradosSelecionadosTemp = seguradosSelecionadosTemp;
	}

	public Set<AssociacaoSeguradoPrograma> getAssociacaoSegurados() {
		return associacaoSegurados;
	}
	
	public String getCodigoENome() {
		return codigoENome;
	}
	
	public void setCodigoENome(String codigoENome) {
		this.codigoENome = codigoENome;
	}
	
	public Set<AssociacaoSeguradoPrograma> getAssociacaoSeguradosRegularizados() {
		 Set<AssociacaoSeguradoPrograma>  lista  = new HashSet<AssociacaoSeguradoPrograma>();
			for (AssociacaoSeguradoPrograma associacao : associacaoSegurados) {
				 if (!associacao.getSituacao().getDescricao().equals(SituacaoEnum.REMOVIDO.descricao())){
					 lista.add(associacao);
				 }
			}
		return lista;
	}

	public void setAssociacaoSegurados(
			Set<AssociacaoSeguradoPrograma> associacaoSegurados) {
		this.associacaoSegurados = associacaoSegurados;
	}
	
	public Long getIdProgramaDePrevencao() {
		return idProgramaDePrevencao;
	}

	public void setIdProgramaDePrevencao(Long idProgramaDePrevencao) {
		this.idProgramaDePrevencao = idProgramaDePrevencao;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getObjetivo() {
		return objetivo;
	}

	public void setObjetivo(String objetivo) {
		this.objetivo = objetivo;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public Set<CID> getCids() {
		return cids;
	}

	public void setCids(Set<CID> cids) {
		this.cids = cids;
	}

	public Profissional getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(Profissional responsavel) {
		this.responsavel = responsavel;
	}

	public Set<ProfissionalEspecialidade> getMedicos() {
		return medicos;
	}

	public void setMedicos(Set<ProfissionalEspecialidade> medicos) {
		this.medicos = medicos;
	}
	
	public String getMotivoRemocao() {
		return motivoRemocao;
	}

	public void setMotivoRemocao(String motivoRemocao) {
		this.motivoRemocao = motivoRemocao;
	}

	public Boolean getRemovido() {
		return removido;
	}

	public void setRemovido(Boolean removido) {
		this.removido = removido;
	}
	
	public Set<Evento> getEventos() {
		return eventos;
	}

	public void setEventos(Set<Evento> eventos) {
		this.eventos = eventos;
	}

	public Boolean validate(UsuarioInterface usuario) throws ValidateException {
		
		HibernateUtil.currentSession().save(this);
		this.codigoENome = this.getIdProgramaDePrevencao().toString() + " - " + this.getNome();
		
		this.mudarSituacao(usuario, SituacaoEnum.CADASTRADO.descricao(), MotivoEnum.CADASTRO_NOVO.getMessage(), new Date());
		
		return true;
	}
	
	public void encerrar(Date dataEncerramento, String motivo, UsuarioInterface usuario) throws Exception {
		if(this.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())){
			throw new ValidateException("Este Programa encontra-se na situação Cancelado(a) e portanto não pode ser encerrado.");
		}
		if(this.isSituacaoAtual(SituacaoEnum.ENCERRADO.descricao())){
			throw new ValidateException("Este Programa encontra-se na situação Encerrado(a) e portanto não pode ser encerrado novamente.");
		}
		if (dataEncerramento == null){
			throw new ValidateException("A data de encerramento deve ser preenchida!");
		}
		if (Utils.isStringVazia(motivo)){
			throw new ValidateException("O motivo deve ser preenchido!");
		}
		this.mudarSituacao(usuario, SituacaoEnum.ENCERRADO.descricao(), motivo, dataEncerramento);
		this.setFim(dataEncerramento);
	}
	
	public void addSegurado(SeguradoDoPrograma seguradoDoPrograma) throws ValidateException{
		Segurado seguradoBuscado = BuscarSegurados.getSegurado(seguradoDoPrograma.getNumeroDoCartao(),seguradoDoPrograma.getCpf(),  Segurado.class, false);
		if (seguradoBuscado != null ){
			Set<AssociacaoSeguradoPrograma> associacaoSegurados = this.getAssociacaoSegurados();
			boolean jaCadastrado = false;
			for (AssociacaoSeguradoPrograma associacaoSeguradoPrograma : associacaoSegurados) {
				if(associacaoSeguradoPrograma.getSegurado().getIdSegurado().equals(seguradoBuscado.getIdSegurado())){
					jaCadastrado = true;
					Date dataInsercao = seguradoDoPrograma.getDataInsercao()!= null ? seguradoDoPrograma.getDataInsercao() : new Date();
					associacaoSeguradoPrograma.mudarSituacao(null,SituacaoEnum.ATIVO.descricao(), "O beneficiário " +seguradoBuscado.getNomeFormatado()+" foi reinserido no programa" , dataInsercao);
					setSeguradoDoPrograma(seguradoDoPrograma, seguradoBuscado, dataInsercao);
					this.getSeguradosSelecionados().add(seguradoDoPrograma);
				}
			}
			if (!jaCadastrado){
				criarAssociacaoDoSeguradoAoPrograma(seguradoDoPrograma,seguradoBuscado);
			}
		}
	}
	
	public void addSeguradoTemp(SeguradoDoPrograma seguradoDoPrograma) throws ValidateException {
		Segurado seguradoBuscado = BuscarSegurados.getSegurado(seguradoDoPrograma.getNumeroDoCartao(),seguradoDoPrograma.getCpf(),  Segurado.class, false);
		if (seguradoBuscado != null) {
			Date dataInsercao = seguradoDoPrograma.getDataInsercao()!= null ? seguradoDoPrograma.getDataInsercao() : new Date();
			setSeguradoDoPrograma(seguradoDoPrograma, seguradoBuscado, dataInsercao);
		}
		for (SeguradoDoPrograma segurado : this.getSeguradosSelecionados()) {
			if (segurado.getSegurado().getIdSegurado().equals(seguradoDoPrograma.getSegurado().getIdSegurado())) {
				throw new ValidateException("Beneficiário já ativo no programa.");
			}
		}
		addSegurado(seguradoDoPrograma);
	}
	
	public void removeSeguradoTemp(SeguradoDoPrograma seguradoDoPrograma) {
		seguradosSelecionadosTemp.remove(seguradoDoPrograma);
	}
	
	public void validateAssociados(ProgramaDePrevencao programa) throws ValidateException{
		for (SeguradoDoPrograma segurado : this.getSeguradosSelecionados()) {
			if (segurado.getRemover()) {
				if (segurado.getMotivo() != null) {
					segurado.setDescricao(SituacaoEnum.REMOVIDO.descricao());
					removeSegurado(segurado);
				} 
				else {
					throw new ValidateException("O motivo de remoção do segurado " + segurado.numeroDoCartao + " deve ser informado");
				}
			}
		}
	}
	
	/**
	 * @param seguradoDoPrograma
	 * @param seguradoBuscado
	 */
	private void criarAssociacaoDoSeguradoAoPrograma(SeguradoDoPrograma seguradoDoPrograma, Segurado seguradoBuscado) {
		AssociacaoSeguradoPrograma associacao = new AssociacaoSeguradoPrograma();
		associacao.setPrograma(this);
		associacao.setSegurado(seguradoBuscado);
		Date dataInsercao = seguradoDoPrograma.getDataInsercao() != null ? seguradoDoPrograma.getDataInsercao() : new Date();
		associacao.setDataInsercao(dataInsercao);
		associacao.mudarSituacao(null,SituacaoEnum.ATIVO.descricao(), "O beneficiário " +seguradoBuscado.getNomeFormatado()+" foi inserido no programa" , dataInsercao);
		seguradoBuscado.getAssociacaoSegurados().add(associacao);
		this.getAssociacaoSegurados().add(associacao);
		setSeguradoDoPrograma(seguradoDoPrograma, seguradoBuscado, dataInsercao);
		this.getSeguradosSelecionados().add(seguradoDoPrograma);
	}

	private void setSeguradoDoPrograma(SeguradoDoPrograma seguradoDoPrograma,
			Segurado seguradoBuscado, Date dataInsercao) {
		seguradoDoPrograma.setCpf(seguradoBuscado.getPessoaFisica().getCpf());
		seguradoDoPrograma.setNumeroDoCartao(seguradoBuscado.getNumeroDoCartao());
		seguradoDoPrograma.setNomeSegurado(seguradoBuscado.getPessoaFisica().getNome());
		seguradoDoPrograma.setSegurado(seguradoBuscado);
		seguradoDoPrograma.setDescricao(seguradoBuscado.getSituacao().getDescricao());
		seguradoDoPrograma.setDataInsercao(dataInsercao);
	}

	public void removeSegurado(SeguradoDoPrograma seguradoDoPrograma) throws ValidateException{
		Segurado seguradoBuscado = BuscarSegurados.getSegurado(seguradoDoPrograma.getNumeroDoCartao(),seguradoDoPrograma.getCpf(),  Segurado.class, false);
		if (seguradoBuscado != null ){
			Set<AssociacaoSeguradoPrograma> associacaoSegurados2 = this.getAssociacaoSegurados();
			for (AssociacaoSeguradoPrograma associacaoSeguradoPrograma : associacaoSegurados2) {
				if(associacaoSeguradoPrograma.getSegurado().equals(seguradoBuscado)){
					associacaoSeguradoPrograma.mudarSituacao(null,SituacaoEnum.REMOVIDO.descricao(), seguradoDoPrograma.getMotivo() , new Date());
				}
			}
		}
	}
	
	public void tocarObjetos(){
		this.getSeguradosCadastrados().size();
		this.getSeguradosSelecionados().size();
		 this.getAssociacaoSegurados().size();
		 Set<AssociacaoSeguradoPrograma> associacaoSegurados2 = this.getAssociacaoSegurados();
		 for (AssociacaoSeguradoPrograma associacaoSeguradoPrograma : associacaoSegurados2) {
			 associacaoSeguradoPrograma.getSegurado().tocarObjetos();
			 associacaoSeguradoPrograma.getColecaoSituacoes().getSituacoes().size();
		}
		 if (responsavel != null ){
			 responsavel.tocarObjetos();
		 }
		Set<ProfissionalEspecialidade> medicos2 = this.getMedicos();
		for (ProfissionalEspecialidade profissionalEspecialidade : medicos2) {
			profissionalEspecialidade.getProfissional().tocarObjetos();
			profissionalEspecialidade.getEspecialidade().tocarObjetos();
		}
		Set<CID> cids = this.getCids();
		for (CID cid : cids) {
			cid.tocarObjetos();
		}
		this.getColecaoSituacoes();
		this.getColecaoSituacoes().getSituacoes().size();
		
	}
}
