package br.com.infowaypi.ecarebc.atendimentos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.infowaypi.ecare.cadastros.MotivoGlosa;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemGasoterapia;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.Valor;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * Esta classe é apenas para receber os dados editáveis dos itensGasoterapia da guia.
 * 
 * @author Luciano Rocha
 *
 */
public class ItemGasoterapiaLayer implements ItemGlosavelLayer {
	
	/** atributo que recebe o codigoDescrição da Gasoterapia do ItemGasoterapia da guia. */
	private Gasoterapia gasoterapia;
	
	/** atributo que recebe a quantidade de horas e minutos fomatada da classe ItemGasoterapia da guia. */
	private String quantidadeFormatada;
	
	private int horas;
	private int minutos;
	private String quantidadeHorasMinutos;
	
	private ItemGasoterapia itemGasoterapia;
	
	private MotivoGlosa motivoGlosa;

	private Valor valor;
	
	private boolean glosar;

	private String justificativaGlosa;
	
	public ItemGasoterapiaLayer(ItemGasoterapia itemGas) {
		this.itemGasoterapia = itemGas;
		this.gasoterapia = itemGasoterapia.getGasoterapia();
		this.horas = itemGasoterapia.getHoras();
		this.minutos = itemGasoterapia.getMinutos();
		this.quantidadeFormatada = itemGasoterapia.getQuantidadeFormatada();
		this.valor = itemGasoterapia.getValor();
	}
	
	public Gasoterapia getGasoterapia() {
		return gasoterapia;
	}

	public void setGasoterapia(Gasoterapia gasoterapia) {
		this.gasoterapia = gasoterapia;
	}

	public String getQuantidadeFormatada() {
		return quantidadeFormatada;
	}
	
	public void setQuantidadeFormatada(String quantidadeFormatada) {
		this.quantidadeFormatada = quantidadeFormatada;
	}

	public ItemGasoterapia getItemGasoterapia() {
		return itemGasoterapia;
	}

	public void setItemGasoterapia(ItemGasoterapia itemGasoterapia) {
		this.itemGasoterapia = itemGasoterapia;
	}

	@Override
	public boolean isGlosar() {
		return glosar;
	}

	@Override
	public void setGlosar(boolean glosar) {
		this.glosar = glosar;
	}

	@Override
	public MotivoGlosa getMotivoGlosa() {
		return motivoGlosa;
	}

	public void setMotivoGlosa(MotivoGlosa motivoGlosa) {
		this.motivoGlosa = motivoGlosa;
	}

	public int getHoras() {
		horas = Integer.parseInt(separaHorasMinutos(quantidadeFormatada).get(0)); 
		return horas;
	}

	public void setHoras(int horas) {
		this.horas = horas;
	}

	public int getMinutos() {
		minutos = Integer.parseInt(separaHorasMinutos(quantidadeFormatada).get(1));
		return minutos;
	}

	public void setMinutos(int minutos) {
		this.minutos = minutos;
	}
	
	public BigDecimal getQuantidadeEmHorasFormatado(){
		return getQuantidadeEmHoras().setScale(3, BigDecimal.ROUND_HALF_UP);
	}
	
	public BigDecimal getQuantidadeEmHoras() {
		BigDecimal quantidade = BigDecimal.ZERO;
		quantidade = quantidade.add(new BigDecimal(this.horas));
		quantidade = quantidade.add(new BigDecimal(this.minutos).divide(new BigDecimal(60), 9, BigDecimal.ROUND_HALF_UP));
		
		return quantidade;
	}
	
	public String getQuantidadeHorasMinutos(){
		return this.quantidadeHorasMinutos;
	}
	
	public void setQuantidadeHorasMinutos(String horasMinutos) throws ValidateException{
		String hora    = horasMinutos.substring(0,3);
		String minutos = horasMinutos.substring(4,6);

		this.horas   = Integer.parseInt(hora);
		this.minutos = Integer.parseInt(minutos); 

		this.quantidadeHorasMinutos = horasMinutos;
	}
	
	public List<String> separaHorasMinutos(String horasMinutosComDoisPontos){
		
		List<String> horasMinutosSeparados = new ArrayList<String>();
		String[] separacao = horasMinutosComDoisPontos.split(":");
		
		for (String s : separacao) {
			horasMinutosSeparados.add(s);
		}
		
		return horasMinutosSeparados;
	}
	
	public Boolean validate() throws ValidateException{
		if(quantidadeHorasMinutos == null) {
			throw new ValidateException("Preeencha o campo \"Horas e minutos\".");
		}
		
		if(horas ==0 && minutos == 0) {
			throw new ValidateException("Não é permitida a inserção de Gasoterapias com valores zerados.");
		}
		
		if(minutos>59)
			throw new ValidateException(MensagemErroEnum.VALIDACAO_MINUTOS.getMessage());	
		
		return true;
	}

	public Valor getValor() {
		return valor;
	}

	public void setValor(Valor valor) {
		this.valor = valor;
	}

	@Override
	public void setJustificativaGlosa(String justificativaGlosa) {
		this.justificativaGlosa = justificativaGlosa;
	}

	@Override
	public String getJustificativaGlosa() {
		return justificativaGlosa;
	}
}