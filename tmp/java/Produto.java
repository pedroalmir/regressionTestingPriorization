package br.com.infowaypi.ecarebc.produto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe que representa um produto do Plano de Saúde. Cada produto deve ter seu
 * próprio conjunto de tabelas com seus valores específicos.
 * 
 * @author Diogo Vinícius
 * 
 */
public class Produto implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private long idProduto;
	private String registroANS;
	private String nomeComercial;
	private Date dataAutorizacao;
	
	private ModalidadeContratacao modalidadeContratacao;
	private TipoSegmentacao tipoSegmentacao;
	private TipoAcomodacao tipoAcomodacao;
	private AbrangenciaGeografica abrangenciaGeografica;
	private AcessoRede acessoRede;
	
	private String descricao;
	private Set<Tabela> tabelas;
	
	private String codProposta;
	private long propostaInicial;
	private long propostaAtual;
	private boolean ativo;
	private Date dataCadastro;
	
	private TipoProdutoEnum tipoProdutoEnum;
	
	private Questionario questionario;
	
	private String sigla;
	
	private Set<SegmentacaoAssistencial> segmentacoesAssistenciais;
	
	public Produto() {
		setTabelas(new HashSet<Tabela>());
		this.segmentacoesAssistenciais = new HashSet<SegmentacaoAssistencial>();
		this.ativo = true;
	}
	
	/**
	 * Proposta Gerada: 99-99999<br />
	 * <br />
	 * <em>Obs.: Mude de %05d para %0Nd, onde N &eacute; a quantidade de d&iacute;gitos, para
	 * mudar a quantidade de d&iacute;gitos ap&oacute;s o h&iacute;fen.</em>
	 * 
	 * @return N&uacute;mero da proposta
	 */
	public String gerarProposta() {
		Formatter formatter = new Formatter();
		return this.getCodProposta() + "-" + formatter.format("%05d", getPropostaAtual() + 1).toString();
	}
	
	/**
	 * Método responsável por buscar a tabela correspondente à data passada como
	 * parâmetro. <br />
	 * É considerado se a tabela está ativa e a data de ativação mais próxima.
	 * 
	 * @param dataAdesao
	 * @return tabela correspondente
	 */
	public Tabela getTabelaVigente(Date dataAdesao) {
		
		Calendar dataAdesaoCalendar = Calendar.getInstance();
		dataAdesaoCalendar.setTime(dataAdesao);
		
		Calendar compatenciaTabelaCalendar = Calendar.getInstance();
		
		Tabela tabelaVigente = null;
		// TODO Ver se o if vai ficar
		ArrayList<Tabela> tabelasOrdenadas = getTabelasOrdenadas();
		
		if (tabelasOrdenadas.size() == 1) {
			tabelaVigente = tabelasOrdenadas.get(0);
		}
		
		for (Tabela tabela : tabelasOrdenadas) {
			if (tabela.isAtiva() && dataAdesao != null && (Utils.compareData(dataAdesao, tabela.getDataAtivacao()) >= 0)) {
				return tabela;
			}
		}
		
		return tabelaVigente;
	}
	
	/**
	 * Retorna todas as tabelas do produto ordenadas em ordem decrescente da
	 * Data de Ativação.
	 * 
	 * @return coleção de tabelas ordenadas
	 */
	private ArrayList<Tabela> getTabelasOrdenadas() {
		ArrayList<Tabela> tabelas = new ArrayList<Tabela>(this.getTabelas());
		
		Collections.sort(tabelas, new Comparator<Tabela>() {
			public int compare(Tabela o1, Tabela o2) {
				return o2.getDataAtivacao().compareTo(o1.getDataAtivacao());
			}
		});
		return tabelas;
	}
	
	public long getIdProduto() {
		return idProduto;
	}
	
	public void setIdProduto(long idProduto) {
		this.idProduto = idProduto;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public Set<Tabela> getTabelas() {
		return tabelas;
	}
	
	public void setTabelas(Set<Tabela> tabelas) {
		this.tabelas = tabelas;
	}
	
	public String getCodProposta() {
		return codProposta;
	}
	
	public void setCodProposta(String codProposta) {
		this.codProposta = codProposta;
	}
	
	public long getPropostaInicial() {
		return propostaInicial;
	}
	
	public void setPropostaInicial(long propostaInicial) {
		this.propostaInicial = propostaInicial;
	}
	
	public long getPropostaAtual() {
		return propostaAtual;
	}
	
	public void setPropostaAtual(long propostaAtual) {
		this.propostaAtual = propostaAtual;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	@SuppressWarnings("unused")
	private String getDescricaoModalidadeContratacao() {
		if (this.modalidadeContratacao != null)
			return this.modalidadeContratacao.getDescricao();
		return "";
	}
	
	@SuppressWarnings("unused")
	private void setDescricaoModalidadeContratacao(String descricaoModalidadeContratacao) {
		if (descricaoModalidadeContratacao == null) {
			return;
		}
		for (ModalidadeContratacao tipo : ModalidadeContratacao.values()) {
			if (descricaoModalidadeContratacao.equals(tipo.getDescricao()))
				this.modalidadeContratacao = tipo;
		}
	}
	
	@SuppressWarnings("unused")
	private String getDescricaoTipoSegmentacao() {
		if (this.tipoSegmentacao != null)
			return this.tipoSegmentacao.getDescricao();
		return "";
	}
	
	@SuppressWarnings("unused")
	private void setDescricaoTipoSegmentacao(String descricaoTipoSegmentacao) {
		if (descricaoTipoSegmentacao == null) {
			return;
		}
		for (TipoSegmentacao tipo : TipoSegmentacao.values()) {
			if (descricaoTipoSegmentacao.equals(tipo.getDescricao()))
				this.tipoSegmentacao = tipo;
		}
	}

	@SuppressWarnings("unused")
	private String getDescricaoTipoAcomodacao() {
		if (this.tipoAcomodacao != null)
			return this.tipoAcomodacao.getDescricao();
		return "";
	}
	
	@SuppressWarnings("unused")
	private void setDescricaoTipoAcomodacao(String descricaoTipoAcomodacao) {
		if (descricaoTipoAcomodacao == null) {
			return;
		}
		for (TipoAcomodacao tipo : TipoAcomodacao.values()) {
			if (descricaoTipoAcomodacao.equals(tipo.getDescricao()))
				this.tipoAcomodacao = tipo;
		}
	}
	
	@SuppressWarnings("unused")
	private String getDescricaoAcessoRede() {
		if (this.acessoRede != null)
			return this.acessoRede.getDescricao();
		return "";
	}
	
	@SuppressWarnings("unused")
	private void setDescricaoAcessoRede(String descricaoAcessoRede) {
		if (descricaoAcessoRede == null) {
			return;
		}
		for (AcessoRede tipo : AcessoRede.values()) {
			if (descricaoAcessoRede.equals(tipo.getDescricao()))
				this.acessoRede = tipo;
		}
	}

	@SuppressWarnings("unused")
	private String getDescricaoAbrangenciaGeografica() {
		if (this.abrangenciaGeografica != null)
			return this.abrangenciaGeografica.getDescricao();
		return "";
	}
	
	@SuppressWarnings("unused")
	private void setDescricaoAbrangenciaGeografica(String descricaoAbrangenciaGeografica) {
		if (descricaoAbrangenciaGeografica == null) {
			return;
		}
		for (AbrangenciaGeografica tipo : AbrangenciaGeografica.values()) {
			if (descricaoAbrangenciaGeografica.equals(tipo.getDescricao()))
				this.abrangenciaGeografica = tipo;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (!(obj instanceof Produto)) {
			return false;
		}
		
		Produto produto = (Produto) obj;
		
		return new EqualsBuilder()
			.append(this.getDataCadastro(), produto.getDataCadastro())
			.append(produto.getDescricao(), this.getDescricao())
			.append(produto.getCodProposta(), this.getCodProposta())
			.append(this.getTipoSegmentacao(), produto.getTipoSegmentacao())
			.append(this.getTipoProdutoEnum(), produto.getTipoProdutoEnum())
			.isEquals();
	}

	public void addTabela(Tabela tabela) {
		tabela.setProduto(this);
		this.getTabelas().add(tabela);
	}

	public String getRegistroANS() {
		return registroANS;
	}

	public void setRegistroANS(String registroANS) {
		this.registroANS = registroANS;
	}

	public String getNomeComercial() {
		return nomeComercial;
	}

	public void setNomeComercial(String nomeComercial) {
		this.nomeComercial = nomeComercial;
	}

	public Date getDataAutorizacao() {
		return dataAutorizacao;
	}

	public void setDataAutorizacao(Date dataAutorizacao) {
		this.dataAutorizacao = dataAutorizacao;
	}

	public ModalidadeContratacao getModalidadeContratacao() {
		return modalidadeContratacao;
	}

	public void setModalidadeContratacao(ModalidadeContratacao modalidadeContratacao) {
		this.modalidadeContratacao = modalidadeContratacao;
	}

	public TipoSegmentacao getTipoSegmentacao() {
		return tipoSegmentacao;
	}

	public void setTipoSegmentacao(TipoSegmentacao tipoSegmentacao) {
		this.tipoSegmentacao = tipoSegmentacao;
	}

	public TipoAcomodacao getTipoAcomodacao() {
		return tipoAcomodacao;
	}
	
	/**
	 * Método retorna tipo de acomodação formatado: 
	 * Apto Ind => para uniplam conforto
	 * Apto Col=> para uniplam família
	 * 
	 * Usado na geração de cartoes em ArquivoCartões
	 * @return
	 */
	public String getTipoAcomodacaoFormatado() {
		
		if(this.tipoAcomodacao != null){
			if(this.tipoAcomodacao.equals(TipoAcomodacao.ENFERMARIA)) {
				return "Apto Col";
			} else if(this.tipoAcomodacao.equals(TipoAcomodacao.APARTAMENTO)){
				return "Apto Ind";
			}	
		}
		
		return "";
	}

	public void setTipoAcomodacao(TipoAcomodacao tipoAcomodacao) {
		this.tipoAcomodacao = tipoAcomodacao;
	}

	public AbrangenciaGeografica getAbrangenciaGeografica() {
		return abrangenciaGeografica;
	}

	public void setAbrangenciaGeografica(AbrangenciaGeografica abrangenciaGeografica) {
		this.abrangenciaGeografica = abrangenciaGeografica;
	}

	public AcessoRede getAcessoRede() {
		return acessoRede;
	}

	public void setAcessoRede(AcessoRede acessoRede) {
		this.acessoRede = acessoRede;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public TipoProdutoEnum getTipoProdutoEnum() {
		return tipoProdutoEnum;
	}

	public void setTipoProdutoEnum(TipoProdutoEnum tipoProdutoEnum) {
		this.tipoProdutoEnum = tipoProdutoEnum;
	}

	public String getTipoProduto() {
		if(tipoProdutoEnum != null){
			return tipoProdutoEnum.getDescricao();
		} 
		return null;
	}

	public void setTipoProduto(String tipoProduto) {
		for (TipoProdutoEnum tipo : TipoProdutoEnum.values()) {
			if (tipo.getDescricao().equals(tipoProduto)) {
				this.tipoProdutoEnum = tipo;
				break;
			}
		}
		//TipoProdutoEnum.getProdutoEnumByDescricao(tipoProduto);
	}
	
	public Questionario getQuestionario() {
		return questionario;
	}

	public void setQuestionario(Questionario questionario) {
		this.questionario = questionario;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	
	public void setSegmentacoesAssistenciais(Set<SegmentacaoAssistencial> segmentacoesAssistenciais) {
		this.segmentacoesAssistenciais = segmentacoesAssistenciais;
	}
	
	public Set<SegmentacaoAssistencial> getSegmentacoesAssistenciais() {
		return segmentacoesAssistenciais;
	}

	public boolean isProdutoCoberturaOdontologica(){
		for (SegmentacaoAssistencial segmentacao : segmentacoesAssistenciais) {
			if(segmentacao.getTipoSegmentacao().equals(TipoSegmentacao.ODONTOLOGICO.descricao())){
				return true;
			}
		}
		return false;
	}
	
	public boolean isProdutoCoberturaHospitalar(){
		for (SegmentacaoAssistencial segmentacao : segmentacoesAssistenciais) {
			if(segmentacao.getTipoSegmentacao().equals(TipoSegmentacao.HOSPITALAR_COM_OBSTETRICIA.descricao())){
				return true;
			}
		}
		return false;
	}
	
	@Override
    public int hashCode() {
		return new HashCodeBuilder()
			.append(this.getCodProposta())
			.append(this.getDescricao())
			.append(this.getTipoSegmentacao())
			.append(this.getTipoProdutoEnum())
			.toHashCode();
    }
}
