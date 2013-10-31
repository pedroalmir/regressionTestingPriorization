package br.com.infowaypi.ecarebc.financeiro.faturamento;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOutros;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.utils.MoneyCalculation;

/**
 * 
 * @author Diogo Vinícius
 *
 */
public class HonorarioMedico implements Serializable{

	private static final long serialVersionUID = 1L;
	private Long idHonorarioMedico;
	private Profissional profissional;
	private Set<Procedimento> procedimentos;
	private AbstractFaturamento faturamento;

	public HonorarioMedico(){
		this.procedimentos = new HashSet<Procedimento>();
	}
	
	public HonorarioMedico(Date competencia,Procedimento procedimento,Profissional profissional,
			Prestador prestadorDestinoHonorario, boolean passivo) {
		this();
		this.getProcedimentos().add(procedimento);
		this.setProfissional(profissional);
		profissional.getHonorariosMedicos().add(this);
		
		AbstractFaturamento faturamento2 = passivo? prestadorDestinoHonorario.getFaturamentoPassivo(competencia) : prestadorDestinoHonorario.getFaturamento(competencia);
		this.setFaturamento(faturamento2);
		
	}
	
	public BigDecimal getValor(){
		BigDecimal valor = BigDecimal.ZERO;
		for (ProcedimentoInterface procedimento : this.getProcedimentos()) {
			valor = valor.add(this.getValor(procedimento, DetalheHonorarioMedico.PROFISSIONAL_TODOS));
		}
		return valor;
	}
	
	public BigDecimal getValor(ProcedimentoInterface procedimento, String funcao){
		BigDecimal valor = BigDecimal.ZERO;
		boolean isProfissionalResponsavel = (funcao.equals(DetalheHonorarioMedico.PROFISSIONAL_TODOS)) || (funcao.equals(DetalheHonorarioMedico.PROFISSIONAL_RESPONSAVEL));
		boolean isProfissionalCirurgiao   = (funcao.equals(DetalheHonorarioMedico.PROFISSIONAL_TODOS)) || (funcao.equals(DetalheHonorarioMedico.CIRURGIAO));
		boolean isProfissionalAuxiliar1   = (funcao.equals(DetalheHonorarioMedico.PROFISSIONAL_TODOS)) || (funcao.equals(DetalheHonorarioMedico.AUXILIAR_1)); 
		boolean isProfissionalAuxiliar2   = (funcao.equals(DetalheHonorarioMedico.PROFISSIONAL_TODOS)) || (funcao.equals(DetalheHonorarioMedico.AUXILIAR_2));
		boolean isProfissionalAuxiliar3   = (funcao.equals(DetalheHonorarioMedico.PROFISSIONAL_TODOS)) || (funcao.equals(DetalheHonorarioMedico.AUXILIAR_3));
		if(procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO){
//			ProcedimentoCirurgicoInterface proc = (ProcedimentoCirurgicoInterface) procedimento;
			ProcedimentoCirurgicoInterface proc = ImplDAO.findById(procedimento.getIdProcedimento(), ProcedimentoCirurgico.class);
			if(profissional.equals(proc.getProfissionalResponsavel()) && (isProfissionalCirurgiao)){
				valor = valor.add(proc.getValorProfissionalResponsavel());
			}
			if(profissional.equals(proc.getProfissionalAuxiliar1()) && (isProfissionalAuxiliar1)){
				valor = valor.add(proc.getValorAuxiliar1());
			}
			if(profissional.equals(proc.getProfissionalAuxiliar2()) && (isProfissionalAuxiliar2)){
				valor = valor.add(proc.getValorAuxiliar2());
			}
			if(profissional.equals(proc.getProfissionalAuxiliar3()) && (isProfissionalAuxiliar3)){
				valor = valor.add(proc.getValorAuxiliar3());
			}
		}
		else if(procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_OUTROS){
			ProcedimentoOutros proc = (ProcedimentoOutros) procedimento;
			if(profissional.equals(proc.getProfissionalResponsavel()) && (isProfissionalResponsavel)){
				valor = valor.add(proc.getValorProfissionalResponsavel());
			}
		}

		return MoneyCalculation.rounded(valor);
	}
	
	public String getFuncao(ProcedimentoInterface procedimento){
		String funcao = "";
		if(procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO){
			ProcedimentoCirurgicoInterface proc = (ProcedimentoCirurgicoInterface) procedimento;
			if(profissional.equals(proc.getProfissionalResponsavel())){
				funcao = DetalheHonorarioMedico.CIRURGIAO;
			}
			else if(profissional.equals(proc.getProfissionalAuxiliar1())){
				funcao = DetalheHonorarioMedico.AUXILIAR_1;
			}
			else if(profissional.equals(proc.getProfissionalAuxiliar2())){
				funcao = DetalheHonorarioMedico.AUXILIAR_2;
			}
			else if(profissional.equals(proc.getProfissionalAuxiliar3())){
				funcao = DetalheHonorarioMedico.AUXILIAR_3;
			}
		}
		else if(procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_OUTROS){
			ProcedimentoOutros proc = (ProcedimentoOutros) procedimento;
			if(profissional.equals(proc.getProfissionalResponsavel())){
				funcao = DetalheHonorarioMedico.PROFISSIONAL_RESPONSAVEL;
			}
		}
		return funcao;
	}
	
	public String getValorFormatado(){
		NumberFormat valor = new DecimalFormat("#,###,##0.00");
		return "R$ " + valor.format(this.getValor());
	}
	
	public Date getCompetencia(){
		return this.getFaturamento().getCompetencia();
	}
	
	public HonorarioMedico clone() {
		HonorarioMedico honorario = new HonorarioMedico();
		honorario.setFaturamento(this.getFaturamento());
		honorario.setProcedimentos(this.getProcedimentos());
		honorario.setProfissional(this.getProfissional());
		return honorario;
	}
	
	//just getters n' setters from fields
	public Profissional getProfissional() {
		return profissional;
	}

	public void setProfissional(Profissional profissional) {
		this.profissional = profissional;
	}

	public Set<Procedimento> getProcedimentos() {
		return procedimentos;
	}

	public void setProcedimentos(Set<Procedimento> procedimentos) {
		this.procedimentos = procedimentos;
	}

	public AbstractFaturamento getFaturamento() {
		return faturamento;
	}

	public void setFaturamento(AbstractFaturamento faturamento) {
		this.faturamento = faturamento;
	}

	public Long getIdHonorarioMedico() {
		return idHonorarioMedico;
	}

	public void setIdHonorarioMedico(Long idHonorarioMedico) {
		this.idHonorarioMedico = idHonorarioMedico;
	}
	
	
	
}
