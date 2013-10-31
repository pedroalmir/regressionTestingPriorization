package br.com.infowaypi.ecarebc.enums;

import java.math.BigDecimal;

import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;

/**
 * Enum respons�vel por determinar o Grau de Participa��o de um profissional em procedimentos cir�rgicos.
 * @author patricia
 * @since 01/12/2009
 *
 */
public enum GrauDeParticipacaoEnum {

	RESPONSAVEL (0, "Cirurgi�o") {
		@Override
		public BigDecimal getValorHonorario(ProcedimentoInterface procedimento) {
			return procedimento.getValorProfissionalResponsavelDoHonorario();
		}
	},
	PRIMEIRO_AUXILIAR (1, "1� Auxiliar") {
		@Override
		public BigDecimal getValorHonorario(ProcedimentoInterface procedimento) {
			return procedimento.getValorAuxiliar1();
		}
	},
	SEGUNDO_AUXILIAR (2, "2� Auxiliar") {
		@Override
		public BigDecimal getValorHonorario(ProcedimentoInterface procedimento) {
			return procedimento.getValorAuxiliar2();
		}
	},
	TERCEIRO_AUXILIAR (3, "3� Auxiliar") {
		@Override
		public BigDecimal getValorHonorario(ProcedimentoInterface procedimento) {
			return procedimento.getValorAuxiliar3();
		}
	},
	ANESTESISTA (4, "Anestesista") {
		@Override
		public BigDecimal getValorHonorario(ProcedimentoInterface procedimento) {
			procedimento.atualizaValorAnestesista();
			return procedimento.getValorAnestesista();
		}
	},
	AUXILIAR_ANESTESISTA (5, "Auxiliar de Anestesista") {
		@Override
		public BigDecimal getValorHonorario(ProcedimentoInterface procedimento) {
			procedimento.atualizaValorAnestesista();
			return procedimento.getValorAuxiliarAnestesista();
		}
	};
	
	private String descricao;
	private int codigo;
	
	GrauDeParticipacaoEnum(int codigo,String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}
	
	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public static GrauDeParticipacaoEnum getEnum(int codigo){
		GrauDeParticipacaoEnum[] grausDeParticipacao = GrauDeParticipacaoEnum.values();
		for (GrauDeParticipacaoEnum grau : grausDeParticipacao) {
			if (grau.codigo == codigo) {
				return grau;
			}
		}
		return null;
	}
	
	public abstract BigDecimal getValorHonorario(ProcedimentoInterface procedimento);
}
