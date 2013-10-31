package br.com.infowaypi.ecarebc.financeiro.faturamento;

import java.math.BigDecimal;
import java.util.Date;

import br.com.infowaypi.ecare.services.recurso.GuiaRecursoGlosa;
import br.com.infowaypi.msr.utils.Utils;

public class DetalheRecursoDeferido {
	
	public static final Date ULTIMO_FAT_ERRADO =  Utils.parse("01/04/2010");
	
	private Long idDetalheRecursoDeferido;
	private GuiaRecursoGlosa guia;
	private BigDecimal valor;
	private static Long id = 0l;
	
	public DetalheRecursoDeferido(){
		idDetalheRecursoDeferido = id++;
	}
	
	public Long getIdDetalheRecursoDeferido() {
		return idDetalheRecursoDeferido;
	}

	@SuppressWarnings("unused")
	private void setIdDetalheRecursoDeferido(Long idDetalheRecursoDeferido) {
		this.idDetalheRecursoDeferido = idDetalheRecursoDeferido;
	}

	public GuiaRecursoGlosa getGuia() {
		return guia;
	}

	public void setGuia(GuiaRecursoGlosa guia) {
		this.guia = guia;
	}

	public BigDecimal getValor() {
		if(valor == null){
			return BigDecimal.ZERO;
		}
		return valor;
	}
	
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
}
