package br.com.infowaypi.ecarebc.financeiro.faturamento;

import java.math.BigDecimal;
import java.util.Date;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOutros;
import br.com.infowaypi.msr.utils.Utils;

public class DetalheHonorarioMedico {
	
	public static final String PROFISSIONAL_RESPONSAVEL = "Responsável";
	public static final String CIRURGIAO = "Cirurgião";
	public static final String AUXILIAR_1 = "Primeiro Auxiliar";
	public static final String AUXILIAR_2 = "Segundo Auxiliar";
	public static final String AUXILIAR_3 = "Terceiro Auxiliar";
	public static final String ANESTESISTA = "Anestesista";
	public static final String PROFISSIONAL_TODOS = "Profissional Todos";
	public static final Date ULTIMO_FAT_ERRADO =  Utils.parse("01/04/2010");
	
	private Long idDetalheHonorarioMedico;
	private GuiaFaturavel guia;
	private ProcedimentoInterface procedimento;
	private Profissional profissional;
	private Prestador prestador;
	private String nomeSegurado;
	private String funcao;
	private BigDecimal valor;
	private static Long id = 0l;
	
	public DetalheHonorarioMedico(){
		idDetalheHonorarioMedico = id++;
	}
	
	public Long getIdDetalheHonorarioMedico() {
		return idDetalheHonorarioMedico;
	}

	@SuppressWarnings("unused")
	private void setIdDetalheHonorarioMedico(Long idDetalheHonorarioMedico) {
		this.idDetalheHonorarioMedico = idDetalheHonorarioMedico;
	}

	public GuiaFaturavel getGuia() {
		return guia;
	}

	public void setGuia(GuiaFaturavel guia) {
		this.guia = guia;
		if (prestador == null) {
			this.prestador = guia.getPrestador();
		}
	}

	public ProcedimentoInterface getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(ProcedimentoInterface procedimento) {
		this.procedimento = procedimento;
	}

	public Profissional getProfissional() {
		return profissional;
	}

	public void setProfissional(Profissional profissional) {
		this.profissional = profissional;
	}

	public String getFuncao() {
		return funcao;
	}

	public void setFuncao(String funcao) {
		this.funcao = funcao;
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

	public BigDecimal updateValor() {
		if(this.getProcedimento().getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO){
			if(this.getFuncao().equals(CIRURGIAO) && !procedimento.isSituacaoAtual(SituacaoEnum.PROCEDIMENTO_COM_HONORARIO_GERADO.descricao())){
				return procedimento.getValorProfissionalResponsavel();
			}
			else if(this.getFuncao().equals(AUXILIAR_1)){
				return procedimento.getValorAuxiliar1();
			}
			else if(this.getFuncao().equals(AUXILIAR_2)){
				return procedimento.getValorAuxiliar2();
			}
			else if(this.getFuncao().equals(AUXILIAR_3)){
				return procedimento.getValorAuxiliar3();
			}
			else if(this.getFuncao().equals(ANESTESISTA)){
				return procedimento.getValorAnestesista();
			}
		} else if(this.getProcedimento().getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_NORMAL){
			if(this.getFuncao().equals(ANESTESISTA)){
				return procedimento.getValorAnestesista();
			}
		} else if(this.getProcedimento().getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_OUTROS){
			if(this.getFuncao().equals(PROFISSIONAL_RESPONSAVEL)){
				if (this.getGuia().getFaturamento().getCompetencia().after(ULTIMO_FAT_ERRADO)) {
					return procedimento.getValorProfissionalResponsavel();
				} else {
					ProcedimentoOutros proc = (ProcedimentoOutros) this.getProcedimento();
					return proc.getValorProfissionalResponsavelLegado();
				}
			}
			if(this.getFuncao().equals(ANESTESISTA)){
				return procedimento.getValorAnestesista();
			}
		}
		return BigDecimal.ZERO;
	}

	public Prestador getPrestador() {
		return prestador;
	}

	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
	}

	public String getNomeSegurado() {
		return nomeSegurado;
	}

	public void setNomeSegurado(String nomeSegurado) {
		this.nomeSegurado = nomeSegurado;
	}
}
