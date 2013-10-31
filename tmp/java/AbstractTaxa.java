package br.com.infowaypi.ecarebc.atendimentos;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;

import br.com.infowaypi.ecarebc.atendimentos.enums.TipoInternacao;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

public class AbstractTaxa extends ImplColecaoSituacoesComponent implements Constantes, Cloneable{
	
	private static final long serialVersionUID = 1L;
	
	private Long idAbstractTaxa;
	//não usado!
	private String codigoLegado;
	/**
	 * Nome da taxa/gasoterapia.
	 */
	private String descricao;
	/**
	 * Unidade de "consumo", que é por hora para a gasoterapia, e pode ser diária ou por uso para taxa.
	 * NÃO USADO.
	 */
	private String unidade;
	/**
	 * Valor da taxa/gasoterapia pago pelo Saúde Recife ao prestador.
	 */
	private float valor;
	private String codigo;
	//não usado!
	private String tipoInternacao;
	/**
	 * concatenção dos campos {@link #codigo} e {@link #descricao}
	 */
	private String codigoDescricao;
	
	/**
	 * Indica o nível de acesso à taxa/gasoterapia nos fluxos do sistema.
	 * NÃO USADO.
	 */
	private Integer visibilidadeDoUsuario;
	
	/**
	 * atributo trasiente para glosar o objeto da sessão
	 */
	private transient boolean glosar;
	
	private String motivoGlosa;
	
	private transient BigDecimal porcentagemProxy;
	
	private transient boolean selecionadoParaExclusao;
	
	private transient boolean cancelar;
	
	private GuiaSimples guia;
	
	private BigDecimal porcentagem;
	
	/**
	 * os atributos "selecionado" e "motivo" são transientes e são utilizado em fluxos onde é informado motivo via update-param, 
	 * por exemplo procedimentos ou autorizar procedimentos
	 */
	private transient Boolean selecionado;
	
	public Integer getVisibilidadeDoUsuario() {
		return visibilidadeDoUsuario;
	}

	public void setVisibilidadeDoUsuario(Integer visibilidadeDoUsuario) {
		this.visibilidadeDoUsuario = visibilidadeDoUsuario;
	}
	
	public String getCodigoDescricao() {
		return codigoDescricao;
	}

	public void setCodigoDescricao(String codigoDescricao) {
		this.codigoDescricao = codigoDescricao;
	}

	public String getTipoInternacao() {
		return tipoInternacao;
	}

	public void setTipoInternacao(TipoInternacao tipoInternacao) {
		this.tipoInternacao = tipoInternacao.getDescricao();
	}
	
	//TODO tirar depois da migração da tabela nova
	public void setTipoInternacao(String tipoInternacao) {
		this.tipoInternacao = tipoInternacao;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	public Long getIdAbstractTaxa() {
		return idAbstractTaxa;
	}
	
	public void setIdAbstractTaxa(Long idAbstractTaxa) {
		this.idAbstractTaxa = idAbstractTaxa;
	}
	
	public String getCodigoLegado() {
		return codigoLegado;
	}
	
	public void setCodigoLegado(String codigo) {
		this.codigoLegado = codigo;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String desricao) {
		this.descricao = desricao;
	}
	
	public String getUnidade() {
		return unidade;
	}
	
	public void setUnidade(String fracao) {
		this.unidade = fracao;
	}
	
	public float getValor() {
		return valor;
	}
	
	public boolean isGlosar() {
		return glosar;
	}

	public void setGlosar(boolean glosar) {
		this.glosar = glosar;
	}

	public String getMotivoGlosa() {
		return motivoGlosa;
	}

	public void setMotivoGlosa(String motivoGlosa) {
		this.motivoGlosa = motivoGlosa;
	}

	public BigDecimal getPorcentagemProxy() {
		return porcentagemProxy;
	}

	public void setPorcentagemProxy(BigDecimal porcentagemProxy) {
		this.porcentagemProxy = porcentagemProxy;
	}

	public boolean isSelecionadoParaExclusao() {
		return selecionadoParaExclusao;
	}

	public void setSelecionadoParaExclusao(boolean selecionadoParaExclusao) {
		this.selecionadoParaExclusao = selecionadoParaExclusao;
	}

	public boolean isCancelar() {
		return cancelar;
	}

	public void setCancelar(boolean cancelar) {
		this.cancelar = cancelar;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	public void setValor(float valor) {
		this.valor = valor;
	}
	
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		
		AbstractTaxa outro = (AbstractTaxa)obj;
		
		return new EqualsBuilder()
			.append(this.descricao,outro.descricao)
			.append(this.unidade,outro.getUnidade())
			.isEquals();
	}
	
	/**
	 * @param motivo
	 * @param usuario
	 * @throws Exception
	 * Método responsável por mudar a situação desta classe para 'Inativo(a)' no seu cadastro.
	 */
	public void inativar(String motivo, UsuarioInterface usuario) throws Exception {
		if (isSituacaoAtual(SituacaoEnum.INATIVO.descricao()))
			throw new Exception("Já se encontra inativado!");
		if (Utils.isStringVazia(motivo))
			throw new Exception("O motivo deve ser preenchido!");
		
		mudarSituacao(usuario, SituacaoEnum.INATIVO.descricao(), motivo, new Date());
	}
	
	/**
	 * @param motivo
	 * @param usuario
	 * @throws Exception
	 * Método responsável por mudar a situação desta classe para 'Ativo(a)' no seu cadastro.
	 */
	public void reativar(String motivo, UsuarioInterface usuario) throws Exception {
		if (isSituacaoAtual(SituacaoEnum.ATIVO.descricao()))
			throw new Exception("Já se encontra ativado!");
		if (Utils.isStringVazia(motivo))
			throw new Exception("O motivo deve ser preenchido!");
		
		mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(), motivo, new Date());
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return null;
	}
	
	public void glosar(UsuarioInterface usuario){}

	public GuiaSimples getGuia() {
		return guia;
	}

	public void setGuia(GuiaSimples guia) {
		this.guia = guia;
	}
	
	public String getPorcentagemFormatada() {
		if(porcentagem == null)
			return "";
		
		return porcentagem.intValue() + "%";
	}
	
	public BigDecimal getPorcentagem() {
		return porcentagem;
	}

	public void setPorcentagem(BigDecimal porcentagem) {
		this.porcentagem = porcentagem;
	}
}