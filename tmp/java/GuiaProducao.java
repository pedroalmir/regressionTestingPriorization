package br.com.infowaypi.ecare.relatorio.producao;

import java.math.BigDecimal;

import br.com.infowaypi.ecare.atendimentos.TipoGuiaEnum;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;

/**
 * Representa uma guia para apresentação no relatório de produção.	<br>
 * 									<br>
 * OBS.: ManagerProducao.getGuias() faz referencia a apenas um construtor de GuiaProducao no criteria, ao adicionar outro construtor verificar o funcionamento desse método.
 * 
 * @author Leonardo Sampaio
 * @since 28/08/2012
 *
 */
public class GuiaProducao {
    
    private Long idGuia;
    private Prestador prestador;
    private BigDecimal valorTotalApresentado;
    private BigDecimal valorPagoPrestador;
    private TipoGuiaEnum tipoDeGuia;
    private int tipoDeTratamento;
    private boolean cirurgiaAmbulatorial;
    
    public GuiaProducao(Long idGuia, Prestador prestador, BigDecimal valorTotalApresentado, BigDecimal valorPagoPrestador, String tipoDeGuia, int tipoTratamento, boolean cirurgiaAmbulatorial) {
	
	this.idGuia = idGuia;
	this.prestador = prestador;
	this.valorTotalApresentado = valorTotalApresentado;
	this.tipoDeGuia = TipoGuiaEnum.getTipoDeGuia(tipoDeGuia);
	this.tipoDeTratamento = tipoTratamento;
	this.valorPagoPrestador = valorPagoPrestador;
	this.cirurgiaAmbulatorial = cirurgiaAmbulatorial;
	
    }
    
    public boolean isCirurgiaAmbulatorial() {
	return cirurgiaAmbulatorial;
    }
    
    public boolean isConsultaAtendimentoUrgencia() {
	
	if (tipoDeGuia == TipoGuiaEnum.CONSULTA_URGENCIA || tipoDeGuia == TipoGuiaEnum.ATENDIMENTO_URGENCIA)
	    return true;
	return false;
	
    }
    
    public BigDecimal getValorPagoPrestador() {
        return valorPagoPrestador;
    }

    public TipoGuiaEnum getTipoDeGuia() {
        return tipoDeGuia;
    }

    public int getTipoDeTratamento() {
        return tipoDeTratamento;
    }

    public Prestador getPrestador() {
	return prestador;
    }
    
    public Long getIdGuia() {
        return idGuia;
    }

    public BigDecimal getValorTotalApresentado() {
        return valorTotalApresentado;
    }
    
    public boolean isInternacao() {
	
	if (tipoDeGuia == TipoGuiaEnum.INTERNACAO_ELETIVA || tipoDeGuia == TipoGuiaEnum.INTERNACAO_URGENCIA)
	    return true;
	return false;
	
    }

    public boolean isInternacaoCirurgica() {

	if(isInternacao() && isTratamentoCirurgico())
	    return true;
	return false;
	
    }
    
    public boolean isInternacaoClinica() {

	if(isInternacao() && this.tipoDeTratamento == GuiaCompleta.TRATAMENTO_CLINICO)
	    return true;
	return false;
	
    }
    
    private boolean isTratamentoCirurgico() {
	return this.tipoDeTratamento == GuiaCompleta.TRATAMENTO_CIRURGICO;
    }
    
    public boolean isTratamentoSeriado() {
	if(tipoDeGuia == TipoGuiaEnum.TRATAMENTO_SERIADO)
	    return true;
	return false;
    }

}
