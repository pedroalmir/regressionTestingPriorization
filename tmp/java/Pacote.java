package br.com.infowaypi.ecarebc.procedimentos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

public class Pacote extends ImplColecaoSituacoesComponent implements Serializable, Constantes{

	private static final long serialVersionUID = 1L;
	
	private String codigo;
	private Long idPacote;
	private BigDecimal valorTotal;
	private String descricao;
	
	private String descricaoCompleta;
	
	private boolean incluiTaxa;
	private boolean incluiGasoterapia;
	private boolean incluiDiaria;
	
	
	private int quantidadeDiarias;
	private BigDecimal valorDiarias;
	private BigDecimal valorTaxas;
	private BigDecimal valorGasoterapia;
	private BigDecimal valorMateriais;
	private BigDecimal valorMedicamentos;
	private BigDecimal valorMateriaisEspeciais;
	private BigDecimal valorMedicamentosEspeciais;
	private BigDecimal valorHonorarios;
	private String codigoDescricao;
	
	private Integer visibilidadeDoUsuario;
	
	private Set<ItemPacote> ItensPacote;
	private Set<TabelaCBHPM> procedimentosCBHPM;
	
	public Pacote(){
		ItensPacote = new HashSet<ItemPacote>();
		procedimentosCBHPM = new HashSet<TabelaCBHPM>();
		valorDiarias = BigDecimal.ZERO;
		valorTaxas = BigDecimal.ZERO;
		valorGasoterapia = BigDecimal.ZERO;
		valorMateriais = BigDecimal.ZERO;
		valorMedicamentos = BigDecimal.ZERO;
		valorMateriaisEspeciais = BigDecimal.ZERO;
		valorMedicamentosEspeciais = BigDecimal.ZERO;
		valorHonorarios = BigDecimal.ZERO;
		valorTotal = BigDecimal.ZERO;
	}
	
	public Integer getVisibilidadeDoUsuario() {
		return visibilidadeDoUsuario;
	}

	public void setVisibilidadeDoUsuario(Integer visibilidadeDoUsuario) {
		this.visibilidadeDoUsuario = visibilidadeDoUsuario;
	}
	
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getDescricaoCompleta() {
		return descricaoCompleta;
	}
	public void setDescricaoCompleta(String descricaoCompleta) {
		this.descricaoCompleta = descricaoCompleta;
	}
	public Long getIdPacote() {
		return idPacote;
	}
	public void setIdPacote(Long idPacote) {
		this.idPacote = idPacote;
	}

	public boolean isIncluiDiaria() {
		return incluiDiaria;
	}
	
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	public void setIncluiDiaria(boolean incluiDiaria) {
		this.incluiDiaria = incluiDiaria;
	}
	
	public boolean isIncluiGasoterapia() {
		return incluiGasoterapia;
	}
	
	public void setIncluiGasoterapia(boolean incluiGasoterapia) {
		this.incluiGasoterapia = incluiGasoterapia;
	}
	
	public boolean isIncluiTaxa() {
		return incluiTaxa;
	}
	
	public void setIncluiTaxa(boolean incluiTaxa) {
		this.incluiTaxa = incluiTaxa;
	}
	
	public BigDecimal getValorTotal() {
		return valorTotal;
	}
	
	public BigDecimal getValorTotalAnestesia() {
		BigDecimal valor = BigDecimal.ZERO;
		BigDecimal valorTotal = BigDecimal.ZERO;
		for (TabelaCBHPM tabela : this.getProcedimentosCBHPM()) {
			valor = new BigDecimal(tabela.getPorteAnestesico().getValorPorte());
			valor.multiply(new BigDecimal(1.5));
			valorTotal = valorTotal.add(valor);
		}
		return  valorTotal;
	}
	
	public int getQuantidadeProcedimentosCirurgicos(){
		int quantidade = 0;
		for (TabelaCBHPM tabela : this.getProcedimentosCBHPM()) {
			if(tabela.isProcedimentoCirurgico()){
				quantidade++;
			}
		}
		return quantidade;
	}
	
	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
	
	public Set<ItemPacote> getItensPacote() {
		return ItensPacote;
	}
	
	public void setItensPacote(Set<ItemPacote> itensPacote) {
		ItensPacote = itensPacote;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.codigo)
			.append(this.descricao)
			.append(this.descricaoCompleta)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if (!(obj instanceof Pacote))
			return false;
		
		Pacote outro = (Pacote)obj;
		
		return new EqualsBuilder()
			.append(this.codigo,outro.codigo)
			.append(this.descricao,outro.descricao)
			.append(this.descricaoCompleta,outro.getDescricaoCompleta())
			.isEquals();
	}
	
	
	public BigDecimal getValorMateriais() {
		return valorMateriais;
	}
	public void setValorMateriais(BigDecimal valorMateriais) {
		this.valorMateriais = valorMateriais;
	}
	public BigDecimal getValorMateriaisEspeciais() {
		return valorMateriaisEspeciais;
	}
	public void setValorMateriaisEspeciais(BigDecimal valorMateriaisEspeciais) {
		this.valorMateriaisEspeciais = valorMateriaisEspeciais;
	}
	public BigDecimal getValorMedicamentos() {
		return valorMedicamentos;
	}
	public void setValorMedicamentos(BigDecimal valorMedicamentos) {
		this.valorMedicamentos = valorMedicamentos;
	}
	public BigDecimal getValorMedicamentosEspeciais() {
		return valorMedicamentosEspeciais;
	}
	public void setValorMedicamentosEspeciais(BigDecimal valorMedicamentosEspeciais) {
		this.valorMedicamentosEspeciais = valorMedicamentosEspeciais;
	}
	
	public int getQuantidadeDiarias() {
		return quantidadeDiarias;
	}

	public void setQuantidadeDiarias(int quantidadeDiarias) {
		this.quantidadeDiarias = quantidadeDiarias;
	}

	public BigDecimal getValorDiarias() {
		return valorDiarias;
	}

	public void setValorDiarias(BigDecimal valorDiarias) {
		this.valorDiarias = valorDiarias;
	}

	public BigDecimal getValorGasoterapia() {
		return valorGasoterapia;
	}

	public void setValorGasoterapia(BigDecimal valorGasoterapia) {
		this.valorGasoterapia = valorGasoterapia;
	}

	public BigDecimal getValorTaxas() {
		return valorTaxas;
	}

	public void setValorTaxas(BigDecimal valorTaxas) {
		this.valorTaxas = valorTaxas;
	}
	
	public BigDecimal getValorHonorarios() {
		return valorHonorarios;
	}
	
	public void setValorHonorarios(BigDecimal valorHonorarios) {
		this.valorHonorarios = valorHonorarios;
	}

	
	/**
	 * @param usuario
	 * @return Boolean
	 * Método usado para setar a situação do objeto como 'Ativo(a)' no momento de seu cadastramento.
	 */
	public Boolean validate(UsuarioInterface usuario){
		
		this.codigoDescricao = this.getCodigo() +" - "+  this.getDescricao();
		
//		valorTotal =  MoneyCalculation.getSoma(valorTotal, MoneyCalculation.multiplica(valorDiarias, new Float(quantidadeDiarias)).floatValue());
//		valorTotal =  MoneyCalculation.getSoma(valorTotal, valorTaxas.floatValue());
//		valorTotal =  MoneyCalculation.getSoma(valorTotal, valorGasoterapia.floatValue());
//		valorTotal =  MoneyCalculation.getSoma(valorTotal, valorMateriais.floatValue());
//		valorTotal =  MoneyCalculation.getSoma(valorTotal, valorMedicamentos.floatValue());
//		valorTotal =  MoneyCalculation.getSoma(valorTotal, valorMateriaisEspeciais.floatValue());
//		valorTotal =  MoneyCalculation.getSoma(valorTotal, valorMedicamentosEspeciais.floatValue());
//		valorTotal =  MoneyCalculation.getSoma(valorTotal, valorHonorarios.floatValue());
		
		if (this.getSituacao()==null){
			mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(), "Cadastro de novo pacote", new Date());
			return true;
		}
		return true;
	}

	public Set<TabelaCBHPM> getProcedimentosCBHPM() {
		return procedimentosCBHPM;
	}

	public void setProcedimentosCBHPM(Set<TabelaCBHPM> procedimentosCBHPM) {
		this.procedimentosCBHPM = procedimentosCBHPM;
	}
	
	/**
	 * @param motivo
	 * @param usuario
	 * @throws Exception
	 * Método responsável por mudar a situação da pacote para 'Inativo(a)' no seu cadastro.
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
	 * Método responsável por mudar a situação do Pacote para 'Ativo(a)' no seu cadastro.
	 */
	public void reativar(String motivo, UsuarioInterface usuario) throws Exception {
		if (isSituacaoAtual(SituacaoEnum.ATIVO.descricao()))
			throw new Exception("Já se encontra ativado!");
		if (Utils.isStringVazia(motivo))
			throw new Exception("O motivo deve ser preenchido!");
		
		mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(), motivo, new Date());
	}
	
	public String getCodigoDescricao() {
		return codigoDescricao;
	}

	public void setCodigoDescricao(String codigoDescricao) {
		this.codigoDescricao = codigoDescricao;
	}
	
}
