package br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos;

import java.math.BigDecimal;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import br.com.infowaypi.ecarebc.atendimentos.Gasoterapia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.ItemGlosavel;
import br.com.infowaypi.ecarebc.atendimentos.acordos.AcordoGasoterapia;
import br.com.infowaypi.ecarebc.atendimentos.visitors.ItemGlosavelVisitor;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.SituacaoInterface;

@SuppressWarnings({"rawtypes"})
public class ItemGasoterapia extends ItemGuia {

	private static final long serialVersionUID = 1L;
	private Long idItemGasoterapia;
	private Gasoterapia gasoterapia;
	private GuiaCompleta guia;
	private int horas;
	private int minutos;
	private String quantidadeHorasMinutos;
	private ItemGlosavel itemGlosavelAnterior;

	public ItemGasoterapia() {
		super();
		mudarSituacao(null, SituacaoEnum.SOLICITADO.descricao(),
				MotivoEnum.INCLUSAO_ITEM.getMessage(), new Date());
	}

	public Gasoterapia getGasoterapia() {
		return gasoterapia;
	}

	public void setGasoterapia(Gasoterapia gasoterapia) {
		this.gasoterapia = gasoterapia;
	}

	public GuiaCompleta getGuia() {
		return guia;
	}

	public void setGuia(GuiaCompleta guia) {
		this.guia = guia;
	}

	public Long getIdItemGasoterapia() {
		return idItemGasoterapia;
	}

	public void setIdItemGasoterapia(Long idItemGasoterapia) {
		this.idItemGasoterapia = idItemGasoterapia;
	}

	public Boolean validate() throws ValidateException {
		this.getValor().setValor(BigDecimal.valueOf(gasoterapia.getValor()));

		if (quantidadeHorasMinutos == null) {
			throw new ValidateException(
					"Preeencha o campo \"Horas e minutos\".");
		}

		if (horas == 0 && minutos == 0) {
			throw new ValidateException(
					"Não é permitida a inserção de Gasoterapias com valores zerados.");
		}

		if (minutos > 59)
			throw new ValidateException(
					MensagemErroEnum.VALIDACAO_MINUTOS.getMessage());

		return true;
	}

	public void recalcularCampos() {
		if (this.guia != null) {
			for (AcordoGasoterapia acordo : guia.getPrestador()
					.getAcordosGasoterapiaAtivos()) {
				if (acordo.getGasoterapia().equals(this.gasoterapia)) {
					this.getValor().setValor(acordo.getValor());
				}
			}
		}
	}

	public void tocarObjetos() {
		super.tocarObjetos();
		this.getSituacao();
		this.getGasoterapia().getDescricao();
		for (SituacaoInterface situacao : getSituacoes()) {
			situacao.getUsuario();
		}
	}

	public BigDecimal getQuantidadeEmHorasFormatado() {
		return getQuantidadeEmHoras().setScale(3, BigDecimal.ROUND_HALF_UP);
	}

	public BigDecimal getQuantidadeEmHoras() {
		BigDecimal quantidade = BigDecimal.ZERO;
		quantidade = quantidade.add(new BigDecimal(this.horas));
		quantidade = quantidade.add(new BigDecimal(this.minutos).divide(
				new BigDecimal(60), 9, BigDecimal.ROUND_HALF_UP));

		return quantidade;
	}

	public String getQuantidadeFormatada() {
		String resultado = "";
		resultado = getHoras() + ":";
		resultado += StringUtils.leftPad(getMinutos() + "", 2, "0");
		return resultado;
	}

	public String getQuantidadeHorasMinutos() {
		return this.quantidadeHorasMinutos;

	}

	public void setQuantidadeHorasMinutos(String horasMinutos)
			throws ValidateException {
		StringTokenizer strToken = new StringTokenizer(horasMinutos, ":");
		String hora = strToken.nextToken();
		String minutos = strToken.nextToken();

		this.horas = Integer.parseInt(hora);
		this.minutos = Integer.parseInt(minutos);

		this.quantidadeHorasMinutos = horasMinutos;

	}

	@Override
	public float getValorTotal() {
		float resultado = this.getValor().getValor()
				.multiply(this.getQuantidadeEmHoras()).floatValue();
		return resultado;
	}

	public int getHoras() {
		return horas;
	}

	public void setHoras(int horas) {
		this.horas = horas;
	}

	public int getMinutos() {
		return minutos;
	}

	public void setMinutos(int minutos) {
		this.minutos = minutos;
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
		return true;
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
	public ItemGasoterapia getItemGuiaGasoterapia() {
		return ImplDAO.findById(getIdItemGuia(), ItemGasoterapia.class);
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
