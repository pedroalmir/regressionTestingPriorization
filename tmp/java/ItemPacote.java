package br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.ItemGlosavel;
import br.com.infowaypi.ecarebc.atendimentos.acordos.AcordoPacote;
import br.com.infowaypi.ecarebc.atendimentos.visitors.ItemGlosavelVisitor;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Pacote;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.situations.SituacaoInterface;

public class ItemPacote extends ItemGuia implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long idItemPacote;
	private Pacote pacote;
	private GuiaCompleta guia;
	private Profissional profissionalResponsavel;
	private Profissional profissionalAuxiliar1;
	private Profissional profissionalAuxiliar2;
	private Profissional profissionalAuxiliar3;
	private Profissional anestesista;
	
	private BigDecimal porcentagem;
	
	private ItemGlosavel itemGlosavelAnterior;
	
	private transient Boolean nestaParcial;
	
	private String observacaoRegulacao;
	
	public Boolean getNestaParcial() {
		return nestaParcial;
	}

	public void setNestaParcial(Boolean realizadoNestaParcial) {
		this.nestaParcial = realizadoNestaParcial;
	}

	/**
	 * Atributo transiente usado somente para a autorização.
	 */
	private Boolean autorizado;
	
	public ItemPacote() {
		this.pacote = new Pacote();
		this.getValor().setQuantidade(1);
		this.porcentagem = new BigDecimal(100);
		this.nestaParcial = null;
		mudarSituacao(null, SituacaoEnum.SOLICITADO.descricao(), MotivoEnum.INCLUSAO_ITEM.getMessage(), new Date());
	}
	
	public GuiaCompleta getGuia() {
		return guia;
	}
	public void setGuia(GuiaCompleta guia) {
		this.guia = guia;
	}
	public Long getIdItemPacote() {
		return idItemPacote;
	}
	public void setIdItemPacote(Long idItemPacote) {
		this.idItemPacote = idItemPacote;
	}
	public Pacote getPacote() {
		return pacote;
	}
	public void setPacote(Pacote pacote) {
		this.pacote = pacote;
	}
	
	public BigDecimal getPorcentagem() {
		return porcentagem;
	}

	public void setPorcentagem(BigDecimal porcentagem) {
		this.porcentagem = porcentagem;
	}

	public String getPorcentagemFormatada() {
		if(porcentagem == null)
			return "";
		
		return porcentagem.intValue() + "%";
	}
	
	public String getObservacaoRegulacao() {
		return observacaoRegulacao;
	}

	public void setObservacaoRegulacao(String observacaoRegulacao) {
		this.observacaoRegulacao = observacaoRegulacao;
	}

	public void tocarObjetos(){
		super.tocarObjetos();
		this.getPacote().getProcedimentosCBHPM().size();
		this.getPacote().getDescricao();
		for (SituacaoInterface situacao : getSituacoes()) {
			situacao.getUsuario();
			situacao.getDescricao();
		}
	}
	
	public Boolean validate(){
		if(this.getValor().getValor().equals(BigDecimal.ZERO)){
			if(this.getGuia() != null) {
				for (AcordoPacote acordo : guia.getPrestador().getAcordosPacoteAtivos()) {
					if(acordo.getPacote().equals(this.pacote)){
						this.getValor().setValor(BigDecimal.valueOf(acordo.getValor()));
						return true;
					}
				}
				this.getValor().setValor(pacote.getValorTotal());
			}	
		}
		return true;
	}

	public void recalcularCampos(){
		if(this.guia != null){
			for (AcordoPacote acordo : guia.getPrestador().getAcordosPacoteAtivos()) {
				if(acordo.getPacote().getDescricao().equals(this.getPacote().getDescricao())){
					BigDecimal porcentagemDecimal = this.getPorcentagem().movePointLeft(2);//divide por 100
					BigDecimal valorAcordo = new BigDecimal(acordo.getValor());
					this.getValor().setValor(valorAcordo.multiply(porcentagemDecimal));
				}
			}
		}
	}
	
	public void recalcular() {
		BigDecimal porcentagemDecimal = this.getPorcentagem().movePointLeft(2);//divide por 100
		BigDecimal valor = this.getValor().getValor();
		this.getValor().setValor(valor.multiply(porcentagemDecimal));
	}
	
	public Profissional getAnestesista() {
		return anestesista;
	}

	public void setAnestesista(Profissional anestesista) {
		this.anestesista = anestesista;
	}

	public Profissional getProfissionalAuxiliar1() {
		return profissionalAuxiliar1;
	}

	public void setProfissionalAuxiliar1(Profissional profissionalAuxiliar1) {
		this.profissionalAuxiliar1 = profissionalAuxiliar1;
	}

	public Profissional getProfissionalAuxiliar2() {
		return profissionalAuxiliar2;
	}

	public void setProfissionalAuxiliar2(Profissional profissionalAuxiliar2) {
		this.profissionalAuxiliar2 = profissionalAuxiliar2;
	}

	public Profissional getProfissionalAuxiliar3() {
		return profissionalAuxiliar3;
	}

	public void setProfissionalAuxiliar3(Profissional profissionalAuxiliar3) {
		this.profissionalAuxiliar3 = profissionalAuxiliar3;
	}

	public Profissional getProfissionalResponsavel() {
		return profissionalResponsavel;
	}

	public void setProfissionalResponsavel(Profissional profissionalResponsavel) {
		this.profissionalResponsavel = profissionalResponsavel;
	}

	public Boolean getAutorizado() {
		return autorizado;
	}

	public void setAutorizado(Boolean autorizado) {
		this.autorizado = autorizado;
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

	@Override
	public boolean isItemDiaria() {
		return false;
	}
	
	@Override
	public boolean isItemGasoterapia() {
		return false;
	}
	
	@Override
	public boolean isItemTaxa() {
		return false;
	}
	
	@Override
	public boolean isItemPacote() {
		return true;
	}
	
	@Override
	public ItemPacote getItemGuiaPacote() {
		return ImplDAO.findById(getIdItemGuia(), ItemPacote.class);
	}

	@Override
	public boolean isTipoGuia() {
		return false;
	}

	@Override
	public boolean isTipoItemGuia() {
		return true;
	}

	@Override
	public boolean isTipoProcedimento() {
		return false;
	}
	
}