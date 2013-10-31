package br.com.infowaypi.ecarebc.procedimentos;

import java.math.BigDecimal;
import java.util.Date;

import br.com.infowaypi.ecarebc.atendimentos.ItemGlosavel;
import br.com.infowaypi.ecarebc.atendimentos.honorario.Honorario;
import br.com.infowaypi.ecarebc.atendimentos.visitors.ItemGlosavelVisitor;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Classe que  representa um procedimento cirurgico de uma guiade internação
 * @author root
 * @changes Danilo Nogueira Portela, Marcus bOolean
 */
public class ProcedimentoCirurgico extends Procedimento implements ProcedimentoCirurgicoInterface, ItemGlosavel {
 
	private static final long serialVersionUID = 1L;

	private BigDecimal porcentagem;
	private String motivoInsercao;
	
	private ItemGlosavel itemGlosavelAnterior;
	
		/**
	 * Atributo transiente para marcar , no durante o fechamento, que o procedimento pertence a esta guia atual 
	 */
	private transient Boolean realizadoNestaParcial;
	
	public ProcedimentoCirurgico() {
		this(null);
	}
	
	public ProcedimentoCirurgico(UsuarioInterface usuario) {
		super(usuario);
		porcentagem = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface#getTipoProcedimento()
	 */
	@Override
	public int getTipoProcedimento() {
		return PROCEDIMENTO_CIRURGICO;
	}
	
	@Override
	public BigDecimal getPorcentagem() {
		return porcentagem;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface#setPorcentagem(java.math.BigDecimal)
	 */
	public void setPorcentagem(BigDecimal porcentagem) {
		this.porcentagem = porcentagem;
	}
	
	/**
	 * Retorna a porcetagem acrescida do caractere especial '%' 
	 * @return String
	 */
	public String getPorcentagemFormatada() {
		if(porcentagem == null)
			return "";
		
		return porcentagem.intValue() + "%";
	}
	
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface#tocarObjetos()
	 */
	@Override
	public void tocarObjetos() {
		super.tocarObjetos();
		if(this.getProfissionalResponsavel() != null)
			this.getProfissionalResponsavel().getCrmNome();
		if(this.getProfissionalAuxiliar1()!= null)
			this.getProfissionalAuxiliar1().getCrmNome();
		if(this.getProfissionalAuxiliar2() != null)
			this.getProfissionalAuxiliar2().getCrmNome();
		if(this.getProfissionalAuxiliar3() != null)
			this.getProfissionalAuxiliar3().getCrmNome();
		if(this.getAnestesista()!= null)
			this.getAnestesista().getCrmNome();
		
	}

	@Override
	public void glosar(UsuarioInterface usuario) {
		Assert.isNotNull(this.getMotivoGlosaProcedimento(), MensagemErroEnum.PROCEDIMENTO_GLOSADO_SEM_MOTIVO_DE_GLOSA.getMessage(this.getProcedimentoDaTabelaCBHPM().getCodigoEDescricao()));
		this.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), this.getMotivoGlosaProcedimento().getDescricao(), new Date());
		for (Honorario honorario : this.getHonorariosGuiaOrigem()) {
			honorario.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), MotivoEnum.GLOSADO_AUTOMATICAMENTE.getMessage(), new Date());
		}
		BigDecimal novoValor = this.getGuia().getValorTotal().subtract(this.getValorTotal());
		this.getGuia().setValorTotal(novoValor);
		
	}

	public String getMotivoInsercao() {
		return motivoInsercao;
	}

	public void setMotivoInsercao(String motivoInsercao) {
		this.motivoInsercao = motivoInsercao;
	}
	public Boolean getRealizadoNestaParcial() {
		return realizadoNestaParcial;
	}

	public void setRealizadoNestaParcial(Boolean realizadoNestaParcial) {
		this.realizadoNestaParcial = realizadoNestaParcial;
	}
	
	public void recalcular() {
		BigDecimal porcentagemDecimal = this.getPorcentagem().movePointLeft(2);//divide por 100
		BigDecimal valor = this.getValorTotal();
		this.setValorAtualDoProcedimento(valor.multiply(porcentagemDecimal));
	}

	@Override
	public ItemGlosavel clone() {
		ProcedimentoCirurgico clone = null;
		clone = (ProcedimentoCirurgico) super.clone();
		return clone;
	}
	
	@Override
	public ProcedimentoInterface newInstance(){
		return new ProcedimentoCirurgico();
	}
	
	@Override
	public void setItemGlosavelAnterior(ItemGlosavel anterior) {
		itemGlosavelAnterior = anterior;
	}

	@Override
	public ItemGlosavel getItemGlosavelAnterior() {
		return itemGlosavelAnterior;
	}

	@Override
	public ItemGlosavel accept(ItemGlosavelVisitor visitor) {
		return visitor.visit(this);
	}
}