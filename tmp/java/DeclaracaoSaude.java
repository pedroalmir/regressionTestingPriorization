package br.com.infowaypi.ecarebc.produto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import br.com.infowaypi.ecare.arquivos.ArquivoDownloadJPG;
import br.com.infowaypi.ecare.enums.TipoDePerguntaEnum;
import br.com.infowaypi.ecare.questionarioqualificado.AdapterSubgrupoCBHPM;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecarebc.procedimentos.CID;
import br.com.infowaypi.ecarebc.procedimentos.SubgrupoCBHPM;
import br.com.infowaypi.ecarebc.utils.ComponentColecaoAlteracoes;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

public class DeclaracaoSaude implements Serializable{

	private static final long serialVersionUID = 1L;

	private Long idDeclaracaoSaude;
	
	private Segurado segurado;

	private Questionario questionario;

	private Date dataCriacao;

	private Date dataAlteracao;
	private UsuarioInterface usuario;

	private BigDecimal peso = BigDecimal.ZERO;

	private BigDecimal altura = BigDecimal.ZERO;

	private Set<RespostaDeclarada> respostasDeclaradas;
	
	private Set<CID> cids;

	private Set<SubgrupoCBHPM> subgruposProcedimentos = new HashSet<SubgrupoCBHPM>();
	
	/** contem os arquivos jpg associados no formato zip*/
	private List<ArquivoDownloadJPG> arquivos = new ArrayList<ArquivoDownloadJPG>();
	
	private ComponentColecaoAlteracoes alteracoesDaDeclaracao = new ComponentColecaoAlteracoes();
	
	/**Arquivo temporário necessário na aplicação de um novo questionário qualificado*/
	private transient String numeroDoCartao;
	private transient Set<AdapterSubgrupoCBHPM> adaptersSubgrupo;
	
	public DeclaracaoSaude(String numeroDoCartaoTemp){
		this.numeroDoCartao = numeroDoCartaoTemp;
	}
	
	public DeclaracaoSaude() {
		this.respostasDeclaradas = new TreeSet<RespostaDeclarada>();
		this.cids = new HashSet<CID>();
	}
	
	public Long getIdDeclaracaoSaude() {
		return idDeclaracaoSaude;
	}

	public void setIdDeclaracaoSaude(Long idDeclaracaoSaude) {
		this.idDeclaracaoSaude = idDeclaracaoSaude;
	}

	public Questionario getQuestionario() {
		return questionario;
	}

	public void setQuestionario(Questionario questionario) {
		this.questionario = questionario;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	public BigDecimal getPeso() {
		return peso;
	}

	public void setPeso(BigDecimal peso) {
		this.peso = peso;
	}

	public BigDecimal getAltura() {
		return altura;
	}

	public void setAltura(BigDecimal altura) {
		this.altura = altura;
	}
	
	public Set<RespostaDeclarada> getRespostasDeclaradas() {
		return respostasDeclaradas;
	}

	public void setRespostasDeclaradas(Set<RespostaDeclarada> respostasDeclaradas) {
		this.respostasDeclaradas = respostasDeclaradas;
	}

	public Set<RespostaDeclarada> getRespostasDeclaradasValueTrue() {
		Set<RespostaDeclarada> declaradas = new HashSet<RespostaDeclarada>();
		for (RespostaDeclarada resposta : this.respostasDeclaradas) {
			if(resposta.getValor() != null && resposta.getValor().booleanValue()){
				declaradas.add(resposta);
			}
		}
		return declaradas;
	}
	
	public Set<CID> getCids() {
		return cids;
	}

	public void setCids(Set<CID> cids) {
		this.cids = cids;
	}

	public List<ArquivoDownloadJPG> getArquivos() {
		return arquivos;
	}

	public void setArquivos(List<ArquivoDownloadJPG> arquivos) {
		this.arquivos = arquivos;
	}

	public UsuarioInterface getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioInterface usuario) {
		this.usuario = usuario;
	}

	public ComponentColecaoAlteracoes getAlteracoesDaDeclaracao() {
		return alteracoesDaDeclaracao;
	}

	public void setAlteracoesDaDeclaracao(
			ComponentColecaoAlteracoes alteracoesDaDeclaracao) {
		this.alteracoesDaDeclaracao = alteracoesDaDeclaracao;
	}

	public Set<SubgrupoCBHPM> getSubgruposProcedimentos() {
		return subgruposProcedimentos;
	}

	public void setSubgruposProcedimentos(Set<SubgrupoCBHPM> subgruposProcedimentos) {
		this.subgruposProcedimentos = subgruposProcedimentos;
	}
	
	public Segurado getSegurado() {
		return segurado;
	}

	public void setSegurado(Segurado segurado) {
		this.segurado = segurado;
	}
	
	public String getNumeroDoCartao() {
		return numeroDoCartao;
	}

	public void setNumeroDoCartao(String numeroDoCartao) {
		this.numeroDoCartao = numeroDoCartao;
	}

	public Set<AdapterSubgrupoCBHPM> getAdaptersSubgrupo() {
		return adaptersSubgrupo;
	}

	public void setAdaptersSubgrupo(Set<AdapterSubgrupoCBHPM> adaptersSubgrupo) {
		this.adaptersSubgrupo = adaptersSubgrupo;
	}

	public void validarPreenchimento() throws ValidateException{
		
		for (RespostaDeclarada resposta : this.getRespostasDeclaradas()) {
			if (resposta.getValor()){
				Assert.isNotEmpty(resposta.getObservacoes(), "O campo observação deve ser preenchido para respostas marcadas como \"SIM\".");
			}
		}
		
		if(this.getPeso().compareTo(BigDecimal.ZERO) == 0){
			throw new ValidateException("O campo peso deve ser preenchido.");
		}
		if(this.getAltura().compareTo(BigDecimal.ZERO) == 0){
			throw new ValidateException("O campo altura deve ser preenchido.");
		}
	}
	
	public void addCid(CID cid) {
		this.cids.add(cid);
	}
	
	public void removeCid(CID cid) {
		this.cids.remove(cid);
	}
	
	/**
	 * @return as respostas com perguntas do tipo DOENCA
	 */
	public List<RespostaDeclarada> getRespostasDoenca() {
		List<RespostaDeclarada> respostaDoenca = new ArrayList<RespostaDeclarada>();
		for (RespostaDeclarada resposta : respostasDeclaradas) {
			if(resposta.getPergunta().getTipoDePergunta().equals(TipoDePerguntaEnum.DOENCA.getDescricao())){
				respostaDoenca.add(resposta);
			}
		}
		return respostaDoenca;
	}

	/**
	 * @return as respostas com perguntas do tipo TRATAMENTO
	 */
	public List<RespostaDeclarada> getRespostasTratamento() {
		List<RespostaDeclarada> respostaTratamento = new ArrayList<RespostaDeclarada>();
		for (RespostaDeclarada resposta : respostasDeclaradas) {
			if(resposta.getPergunta().getTipoDePergunta().equals(TipoDePerguntaEnum.TRATAMENTO.getDescricao())){
				respostaTratamento.add(resposta);
			}
		}
		return respostaTratamento;
	}
	
	/**
	 * calcula e retorna o IMC, desde que os campos peso e altura estejam povoados 
	 */
	public BigDecimal getIMC(){
		if(this.getPeso() == null || this.getAltura() == null){
			return null;
		}
		BigDecimal imc = this.getPeso().divide(this.getAltura().pow(2), BigDecimal.ROUND_HALF_UP);
		return imc;
	}
	
	public void inserirAdapterSubgrupo(AdapterSubgrupoCBHPM adapter) throws ValidateException{
		for (SubgrupoCBHPM subGrupo : this.getSubgruposProcedimentos()) {
			if(subGrupo.equals(adapter.getSubgrupo())){
				throw new ValidateException("O mesmo subgrupo não pode ser inserido duas vezes.");
			}
		}
		this.getSubgruposProcedimentos().add(adapter.getSubgrupo());
	}
	
	/**
	 * apaga a relação so questionário com o subgrupo
	 */
	public void excluirAdapterSubgrupo(AdapterSubgrupoCBHPM adapter){
		Iterator<SubgrupoCBHPM> iterator = this.getSubgruposProcedimentos().iterator();
		while (iterator.hasNext()) {
			SubgrupoCBHPM next = iterator.next();
			if(next.equals(adapter.getSubgrupo())){
				iterator.remove();
			}
		}
	}

}
