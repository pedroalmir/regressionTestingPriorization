package br.com.infowaypi.ecarebc.atendimentos;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.atendimentos.enums.TipoInternacao;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.TipoAcomodacao;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

public class Diaria extends ImplColecaoSituacoesComponent implements Constantes{
	
	private static final long serialVersionUID = 1L;
	
	private Long idDiaria;
	/**
	 * Descrição da Diária
	 */
	private String descricao;
	private String observacoes;
	private Float valor;
	private Set<ItemDiaria> itensDiarias;
	private String codigo;
	
	/**
	 * Campo não usado
	 */
	private String tipoInternacao;
	
	/**
 	 * Concatenação dos campos {@link #codigo} e {@link #descricao} 
	 */
	private String codigoDescricao;
	
	
	/**
	 * Indica o nível de acesso às diárias nos fluxos do sistema - NÃO USADO
	 */
	private Integer visibilidadeDoUsuario;
	
	private TipoAcomodacao tipoAcomodacao;
	
	
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

	public Long getIdDiaria() {
		return idDiaria;
	}
	
	public void setIdDiaria(Long idDiaria) {
		this.idDiaria = idDiaria;
	}
	
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Float getValor() {
		return valor;
	}
	public void setValor(Float valor) {
		this.valor = valor;
	}
	
	public Set<ItemDiaria> getItensDiarias() {
		return itensDiarias;
	}
	
	public void setItensDiarias(Set<ItemDiaria> itensDiarias) {
		this.itensDiarias = itensDiarias;
	}
	
	public String getObservacoes() {
		return observacoes;
	}
	
	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}
	
	public String getValorFormatado() {
		BigDecimal valor = new BigDecimal(this.valor);
		if (valor == null)
			return "0,00";
		
		return StringUtils.leftPad(new DecimalFormat("###,###,##0.00").format(valor.setScale(2,BigDecimal.ROUND_HALF_UP)), 9, " "); 
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if (!(obj instanceof Diaria))
			return false;
		
		Diaria outro = (Diaria)obj;
		
		return new EqualsBuilder()
			.append(this.descricao,outro.descricao)
			.append(this.observacoes,outro.getObservacoes())
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(1308791639, 309108973).appendSuper(
				super.hashCode())
				.append(this.descricao)
				.append(this.observacoes)
				.toHashCode();
	}
	
	/**
	 * @param motivo
	 * @param usuario
	 * @throws Exception
	 * Método responsável por mudar a situação da Diária para 'Inativo(a)' no seu cadastro.
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
	 * Método responsável por mudar a situação da Diária para 'Ativo(a)' no seu cadastro.
	 */
	public void reativar(String motivo, UsuarioInterface usuario) throws Exception {
		if (isSituacaoAtual(SituacaoEnum.ATIVO.descricao()))
			throw new Exception("Já se encontra ativado!");
		if (Utils.isStringVazia(motivo))
			throw new Exception("O motivo deve ser preenchido!");
		
		mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(), motivo, new Date());
	}
	
	
	/**
	 * @param usuario
	 * @return Boolean
	 * Método usado para setar a situação do objeto como 'Ativo(a)' no momento de seu cadastramento.
	 */
	public Boolean validate(UsuarioInterface usuario){
		
		this.codigoDescricao = this.getCodigo() +" - "+  this.getDescricao(); 
		
		if (this.getSituacao()==null){
			mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(), "Cadastro de nova taxa", new Date());
			return true;
		}
		return true;
	}

	public TipoAcomodacao getTipoAcomodacao() {
		return tipoAcomodacao;
	}

	public void setTipoAcomodacao(TipoAcomodacao tipoAcomodacao) {
		this.tipoAcomodacao = tipoAcomodacao;
	}
	
}
