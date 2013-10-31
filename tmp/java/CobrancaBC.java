package br.com.infowaypi.ecarebc.financeiro;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.financeiro.conta.ComponenteColecaoContas;
import br.com.infowaypi.ecarebc.financeiro.conta.ImplPagamento;
import br.com.infowaypi.msr.user.UsuarioInterface;

public abstract class CobrancaBC extends ImplPagamento implements FluxoFinanceiroInterface {	
		
	private static final long serialVersionUID = 1L;
	public static final String PROCESSAMENTO_AUTOMATICO_DE_COBRANCA = "Processamento automatico de cobranca.";
	public static final String NÃO_PAGAMENTO_DA_1A_PARCELA = "Não pagamento da 1ª parcela";
	
	private Long idFluxoFinanceiro;
	private long codigoLegado;
	// novos campos
	private  ComponenteColecaoContas colecaoContas;
	private Set<GuiaSimples> guias;
	
	public CobrancaBC() {
		this(null);
		colecaoContas = new ComponenteColecaoContas();
		guias = new HashSet<GuiaSimples>();
	}
	
	public CobrancaBC(UsuarioInterface usuario){
		super();
		this.mudarSituacao(usuario, ABERTO, "Geração automática de cobrança", null);
	}
	
	public void addGuia(GuiaSimples guia){
		this.getGuias().add(guia);
		guia.setFluxoFinanceiro(this);
	}
	
	public void addAllGuias(Collection<GuiaSimples> guias){
		for (GuiaSimples guia : guias) {
			addGuia(guia);
		}
	}
	
	public Set<GuiaSimples> getGuias() {
		return guias;
	}
	public void setGuias(Set<GuiaSimples> guias) {
		this.guias = guias;
	}
	
	public Long getIdFluxoFinanceiro() {
		return idFluxoFinanceiro;
	}
	public void setIdFluxoFinanceiro(Long idFluxoFinanceiro) {
		this.idFluxoFinanceiro = idFluxoFinanceiro;
	}
	public ComponenteColecaoContas getColecaoContas() {
		return colecaoContas;
	}
	public void setColecaoContas(ComponenteColecaoContas colecaoContas) {
		this.colecaoContas = colecaoContas;
	}
	public void processarVencimento() {
		// TODO Auto-generated method stub
		
	}
	public TitularFinanceiroInterface getTitularFinanceiro() {
		// TODO Auto-generated method stub
		return null;
	}
	public long getCodigoLegado() {
		return codigoLegado;
	}
	public void setCodigoLegado(long codigoLegado) {
		this.codigoLegado = codigoLegado;
	}
	
	public boolean isCobranca() {
		return true;
	}
	
	public boolean isFaturamento() {
		return false;
	}
	public boolean isConsignacao() {
		return false;
	}
}
