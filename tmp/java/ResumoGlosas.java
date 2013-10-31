package br.com.infowaypi.ecarebc.financeiro.faturamento;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;
/**
 * 
 * @author Idelvane
 * 
 * Utilizado nos flows de Revisão de Glosas e relatório de Glosas.
 *
 */
public class ResumoGlosas {
	
	private BigDecimal somatorioGlosas;
	private BigDecimal valorOutros;
	private String motivoValorOutros;
	private int quantidadeGuias;
	private int quantidadeProcedimentos;
	private Date competencia;
	private List<ProcedimentoInterface> procedimentos = new ArrayList<ProcedimentoInterface>();
	private Prestador prestador;
	
	public ResumoGlosas(Faturamento faturamento, Date competenciaFormatada, Prestador prestador, Boolean validaProcedimentos) throws ValidateException{
		
		this.prestador = prestador;
		if(validaProcedimentos){
			if(procedimentos.isEmpty())
				throw new ValidateException("Não foram encontrados procedimentos glosados nessa competência.");
		}
		
		this.competencia = competenciaFormatada;
		int quantidadeGuias = 0;
		
		this.quantidadeProcedimentos = this.procedimentos.size();
		this.quantidadeGuias = quantidadeGuias;
		this.somatorioGlosas = faturamento.getValorProcedimentosGlosados().setScale(2, BigDecimal.ROUND_HALF_UP);
		this.valorOutros = new BigDecimal(faturamento.getValorOutros().floatValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
		this.motivoValorOutros = faturamento.getMotivoValorOutros();
	}
	public BigDecimal getSomatorioGlosas() {
		return somatorioGlosas;
	}

	public void setSomatorioGlosas(BigDecimal somatorioGlosas) {
		this.somatorioGlosas = somatorioGlosas;
	}
	
	public String getValorGlosas() {
		return StringUtils.leftPad((new DecimalFormat("###,###,##0.00")).format(somatorioGlosas), 13, " ");
	}
	
	
	public int getQuantidadeGuias() {
		return quantidadeGuias;
	}

	public void setQuantidadeGuias(int quantidadeGuias) {
		this.quantidadeGuias = quantidadeGuias;
	}
	
	public String getCompetencia(){
		return Utils.format(competencia,"MM/yyyy");
	}

	public List<ProcedimentoInterface> getProcedimentos() {
		return procedimentos;
	}

	public int getQuantidadeProcedimentos() {
		return quantidadeProcedimentos;
	}
	public Prestador getPrestador() {
		return prestador;
	}
	
	public String getData() {
		return StringUtils.rightPad((new SimpleDateFormat("dd 'de' MMMM 'de' yyyy")).format(new Date()),10," ");
	}
	
	public String getNomePrestador(){
		String nome = this.prestador.getPessoaJuridica().getFantasia();
		return nome;
	}
	public String getMotivoValorOutros() {
		return motivoValorOutros;
	}
	public BigDecimal getValorOutros() {
		return valorOutros;
	}
	
}
