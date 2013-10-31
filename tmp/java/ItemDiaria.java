package br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import br.com.infowaypi.ecarebc.atendimentos.Diaria;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.ItemGlosavel;
import br.com.infowaypi.ecarebc.atendimentos.acordos.AcordoDiaria;
import br.com.infowaypi.ecarebc.atendimentos.visitors.ItemGlosavelVisitor;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.utils.Utils;

public class ItemDiaria extends ItemGuia implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 1L;
	
	public static final int H24 = 1;
	public static final int H48 = 2;
	public static final int H72 = 3;
	public static final int H96 = 4;
	public static final int H120 = 5;
	
	private Long idItemDiaria;
	private Diaria diaria;
	private GuiaCompleta guia;
	private Boolean autorizado;
	
	private ItemGlosavel itemGlosavelAnterior;
	
	/**
	 * Trata-se do motivo da solicitação desse item-diaria, seja no momento de
	 * solicitar a internação como no de solicitar a prorrogação de internação.
	 */
	private String justificativa;
	/**
	 * É o motivo para a não-autorização da diária em questão.
	 */
	private String justificativaNaoAutorizacao;
	/**
	 * Data em que se inicia a diária.
	 */
	private Date dataInicial;
	
	/**
	 * Atributo criado para regulação de diárias
	 */
	private int quantidadeAutorizada;
	
	private transient int quantidadeAutorizadaTransient;
	
	private int quantidadeSolicitada;
	
	public ItemDiaria(){
		super();
		mudarSituacao(null, SituacaoEnum.SOLICITADO.descricao(), MotivoEnum.INCLUSAO_ITEM.getMessage(), new Date());
	}
	
	public Diaria getDiaria() {
		return diaria; 
	}
	public void setDiaria(Diaria diaria) {
		this.diaria = diaria;
	}
	public GuiaCompleta getGuia() {
		return guia;
	}
	public void setGuia(GuiaCompleta guia) {
		this.guia = guia;
	}
	public Long getIdItemDiaria() {
		return idItemDiaria;
	}
	public void setIdItemDiaria(Long idItemDiaria) {
		this.idItemDiaria = idItemDiaria;
	}
	
	public Integer getHoras(){
		return this.getValor().getQuantidade();
	}
	public String getHorasFormatado(){
		Integer horas = this.getValor().getQuantidade();
		return horas.toString()+"dias";
	}

	public void recalcularCampos(){
		if(this.guia != null && guia.getPrestador() != null){
			for (AcordoDiaria acordo : guia.getPrestador().getAcordosDiariaAtivos()) {
				if(acordo.getDiaria().equals(this.diaria)){
					this.getValor().setValor(acordo.getValor());
				}
			}
		}
	}
	
	public void recalcularCamposConfirmacaoInternacao(){
		if(this.guia != null && guia.getPrestador() != null){
			for (AcordoDiaria acordo : guia.getPrestador().getAcordosDiariaAtivos()) {
				if(acordo.getDiaria().getIdDiaria().equals(this.diaria.getIdDiaria())){
					this.getValor().setValor(acordo.getValor());
				}
			}
		}
	}
	
	public Boolean validate(){
		this.getValor().setValor(BigDecimal.valueOf(diaria.getValor()));
		return true;
	}
	
	public void tocarObjetos(){
		super.tocarObjetos();
		this.getDiaria().getDescricao();
		this.getSituacao().getDescricao();
		for (SituacaoInterface situacao : getSituacoes()) {
			situacao.getUsuario();
			situacao.getDescricao();
		}
		this.getJustificativa();
		this.getDataInicial();
	}
	
	public Boolean getAutorizarDiaria() {
		return null;
	}

	public void setAutorizarDiaria(Boolean autorizarDiaria) {
		
		if(autorizarDiaria == null){
		}else if(autorizarDiaria == true){
			if(this.getGuia().isSituacaoAtual(SituacaoEnum.SOLICITADO_PRORROGACAO.descricao()))
				this.getGuia().mudarSituacao(this.getGuia().getUsuarioDoFluxo(), SituacaoEnum.PRORROGADO.descricao(), MotivoEnum.AUTORIZACAO_PRORROGACAO_GUIA.getMessage(), new Date());
			this.mudarSituacao(guia.getUsuarioDoFluxo(), SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZADO_PELO_AUDITOR.getMessage(), new Date());
			
		}else if(autorizarDiaria == false){
			if(this.getGuia().isSituacaoAtual(SituacaoEnum.SOLICITADO_PRORROGACAO.descricao()))
				this.getGuia().mudarSituacao(this.getGuia().getUsuarioDoFluxo(), SituacaoEnum.NAO_PRORROGADO.descricao(), MotivoEnum.PRORROGACAO_GUIA_NAO_AUTORIZADA.getMessage(), new Date());
			this.mudarSituacao(guia.getUsuarioDoFluxo(), SituacaoEnum.NAO_AUTORIZADO.descricao(), MotivoEnum.NAO_AUTORIZADO_AUDITOR.getMessage(), new Date());
		}
	}
	
	public Date getDataSolicitacao(){
		SituacaoInterface situacao = getSituacao(SituacaoEnum.SOLICITADO.descricao());
		if(situacao == null)
			return null;
		else
			return situacao.getDataSituacao();
	}
	
	public Date getDataAutorizacaoNegacao(){
		if(isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao()) || isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao()))
			return getSituacao().getDataSituacao();
		else
			return null;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}
	
	/**
	 * esse método seta a data inicial do itemdiaria, de acordo com a data da ultima diaria da guia.
	 * às 12:01 pm.
	 * @param dataInicial
	 */
	public void calculaDataInicial(){
		boolean isInternacaoEletivaComApenasUmaDiariaESemDataInicial = guia.getItensDiariaAutorizados().size() == 1 && ((ItemDiaria)guia.getItensDiariaAutorizados().iterator().next()).getDataInicial() == null;
		
		if (!ignoraDataInicial()) {
			if (guia.getItensDiariaAutorizados().isEmpty() || isInternacaoEletivaComApenasUmaDiariaESemDataInicial)
				this.dataInicial =  guia.getDataAtendimento()!= null ?  guia.getDataAtendimento(): new Date();
			else {
				Calendar dtInicialProximaDiaria = GregorianCalendar.getInstance();
				dtInicialProximaDiaria.setTime(guia.getUltimoItemDiaria().getDataFinal());
				this.dataInicial =(dtInicialProximaDiaria.getTime());
			}
			Calendar dt = GregorianCalendar.getInstance();
			dt.setTime(dataInicial);
			dt.set(GregorianCalendar.HOUR_OF_DAY, 12);
			dt.set(GregorianCalendar.MINUTE, 1);
			dt.set(GregorianCalendar.SECOND, 0);
			this.dataInicial = dt.getTime();
 		}
	}
	
	private boolean ignoraDataInicial(){
		//se data de atendimento anterior ao deploy da demanda, nao calcula as datas das diarias
		if (guia.getDataAtendimento() != null && guia.getDataAtendimento().before(Utils.parse("19/11/2009")))
			return true;
		
		if (guia.isInternacaoEletiva() && guia.isSituacaoAtual(SituacaoEnum.ABERTO.descricao())){//talvez nem precise dos outros ifs
			//se for a primeira diária de uma iel, ele DEVE calcular a data
			if(guia.getItensDiariaAutorizados().size() == 1 && ((ItemDiaria)guia.getItensDiariaAutorizados().iterator().next()).getDataInicial() == null)
				return false;
			//se a guia tiver algum item diaria sem data (exceto o primeiro), nao calcula as datas das diarias
			if(guia.getItensDiariaAutorizados().size() > 1 && guia.isPossuiItemDiariaAutorizadoSemDataInicial())
				return true;
			
		} else {
			//se a guia tiver algum item diaria sem data, nao calcula as datas das diarias
			if (guia.isPossuiItemDiariaAutorizadoSemDataInicial())
				return true;
		}
		
		return false;
	}

	/**
	 * Calcula a data final da diária com base na quantidade de dias autorizados, sempre às 12:00 pm
	 * @return
	 */
	public Date getDataFinal() {
		if (dataInicial != null){
			Calendar dt = GregorianCalendar.getInstance();
			dt.setTime(dataInicial);
			dt.add(GregorianCalendar.DAY_OF_MONTH, getValor().getQuantidade());
			dt.set(GregorianCalendar.MINUTE, 0);
			return dt.getTime();
		} else {
			return null;
		}
	}
	
	public String getDataInicialFormatada() {
		if (dataInicial == null)
			return null;
		return Utils.format(dataInicial);
	}
	
	public String getDataFinalFormatada() {
		if (this.getDataFinal() == null)
			return null;
		return Utils.format(this.getDataFinal());
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public String getJustificativaNaoAutorizacao() {
		return justificativaNaoAutorizacao;
	}

	public void setJustificativaNaoAutorizacao(String justificativaNaoAutorizacao) {
		this.justificativaNaoAutorizacao = justificativaNaoAutorizacao;
	}

	public Boolean isAutorizado() {
		return autorizado;
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
	public boolean isTipoGuia() {
		return false;
	}

	@Override
	public boolean isTipoProcedimento() {
		return false;
	}
	
	@Override
	public boolean isItemDiaria() {
		return true;
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
		return false;
	}
	
	@Override
	public ItemDiaria getItemGuiaDiaria() {
		return ImplDAO.findById(getIdItemGuia(), ItemDiaria.class);
	}

	@Override
	public boolean isTipoItemGuia() {
		return true;
	}

	public int getQuantidadeAutorizada() {
		return quantidadeAutorizada;
	}

	public void setQuantidadeAutorizada(int quantidadeAutorizada) {
		this.quantidadeAutorizada = quantidadeAutorizada;
	}

	public int getQuantidadeAutorizadaTransient() {
		return quantidadeAutorizadaTransient;
	}

	public void setQuantidadeAutorizadaTransient(int quantidadeAutorizadaTransient) {
		this.quantidadeAutorizadaTransient = quantidadeAutorizadaTransient;
	}

	public int getQuantidadeSolicitada() {
		return quantidadeSolicitada;
	}

	public void setQuantidadeSolicitada(int quantidadeSolicitada) {
		this.quantidadeSolicitada = quantidadeSolicitada;
	}
}