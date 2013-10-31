package br.com.infowaypi.ecare.atendimentos;

import java.util.HashSet;
import java.util.Set;

import br.com.infowaypi.ecare.services.recurso.GuiaRecursoGlosa;
import br.com.infowaypi.ecarebc.atendimentos.GuiaAcompanhamentoAnestesico;
import br.com.infowaypi.ecarebc.atendimentos.GuiaAtendimentoUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCirurgiaOdonto;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsulta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaOdonto;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaOdontoUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExameOdonto;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacaoEletiva;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacaoUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaTratamentoSeriado;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GuiaHonorarioMedico;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;

public enum TipoGuiaEnum {

	CONSULTA("GCS","Guia de Consulta",GuiaConsulta.class) {
		@Override
		public Set<String> getSituacoesPossiveis() {
			Set<String> situacoes = new HashSet<String>();
			situacoes.add(SituacaoEnum.CANCELADO.descricao());
			situacoes.add(SituacaoEnum.AGENDADA.descricao());
			situacoes.add(SituacaoEnum.CONFIRMADO.descricao());
			situacoes.add(SituacaoEnum.FATURADA.descricao());
			situacoes.add(SituacaoEnum.PAGO.descricao());
			return situacoes;
		}
	},
	CONSULTA_URGENCIA("CUR","Consulta de Urgência", GuiaConsultaUrgencia.class) {
		@Override
		public Set<String> getSituacoesPossiveis() {
			Set<String> situacoes = new HashSet<String>();
			situacoes.add(SituacaoEnum.CANCELADO.descricao());
			situacoes.add(SituacaoEnum.ABERTO.descricao());
			situacoes.add(SituacaoEnum.FECHADO.descricao());
			situacoes.add(SituacaoEnum.ENVIADO.descricao());
			situacoes.add(SituacaoEnum.RECEBIDO.descricao());
			situacoes.add(SituacaoEnum.DEVOLVIDO.descricao());
			situacoes.add(SituacaoEnum.AUDITADO.descricao());
			situacoes.add(SituacaoEnum.GLOSADO.descricao());
			situacoes.add(SituacaoEnum.FATURADA.descricao());
			situacoes.add(SituacaoEnum.PAGO.descricao());
			return situacoes;
		}
	},
	ATENDIMENTO_URGENCIA("AUR", "Atendimento urgência",GuiaAtendimentoUrgencia.class) {
		@Override
		public Set<String> getSituacoesPossiveis() {
			Set<String> situacoes = new HashSet<String>();
			situacoes.add(SituacaoEnum.CANCELADO.descricao());
			situacoes.add(SituacaoEnum.ABERTO.descricao());
			situacoes.add(SituacaoEnum.FECHADO.descricao());
			situacoes.add(SituacaoEnum.ENVIADO.descricao());
			situacoes.add(SituacaoEnum.RECEBIDO.descricao());
			situacoes.add(SituacaoEnum.DEVOLVIDO.descricao());
			situacoes.add(SituacaoEnum.AUDITADO.descricao());
			situacoes.add(SituacaoEnum.GLOSADO.descricao());
			situacoes.add(SituacaoEnum.FATURADA.descricao());
			situacoes.add(SituacaoEnum.PAGO.descricao());
			return situacoes;
		}
	},
	INTERNACAO_ELETIVA("IEL", "Internação Eletiva", GuiaInternacaoEletiva.class) {
		@Override
		public Set<String> getSituacoesPossiveis() {
			Set<String> situacoes = new HashSet<String>();
			situacoes.add(SituacaoEnum.CANCELADO.descricao());
			situacoes.add(SituacaoEnum.SOLICITADO_INTERNACAO.descricao());
			situacoes.add(SituacaoEnum.AUTORIZADO.descricao());
			situacoes.add(SituacaoEnum.NAO_AUTORIZADO.descricao());
			situacoes.add(SituacaoEnum.ABERTO.descricao());
			situacoes.add(SituacaoEnum.SOLICITADO_PRORROGACAO.descricao());
			situacoes.add(SituacaoEnum.PRORROGADO.descricao());
			situacoes.add(SituacaoEnum.NAO_PRORROGADO.descricao());
			situacoes.add(SituacaoEnum.ALTA_REGISTRADA.descricao());
			situacoes.add(SituacaoEnum.FECHADO.descricao());
			situacoes.add(SituacaoEnum.ENVIADO.descricao());
			situacoes.add(SituacaoEnum.RECEBIDO.descricao());
			situacoes.add(SituacaoEnum.DEVOLVIDO.descricao());
			situacoes.add(SituacaoEnum.AUDITADO.descricao());
			situacoes.add(SituacaoEnum.GLOSADO.descricao());
			situacoes.add(SituacaoEnum.FATURADA.descricao());
			situacoes.add(SituacaoEnum.PAGO.descricao());
			return situacoes;
		}
	},
	INTERNACAO_URGENCIA("IUR","Internação Urgência",GuiaInternacaoUrgencia.class) {
		@Override
		public Set<String> getSituacoesPossiveis() {
			Set<String> situacoes = new HashSet<String>();
			situacoes.add(SituacaoEnum.CANCELADO.descricao());
			situacoes.add(SituacaoEnum.SOLICITADO_INTERNACAO.descricao());
			situacoes.add(SituacaoEnum.NAO_AUTORIZADO.descricao());
			situacoes.add(SituacaoEnum.ABERTO.descricao());
			situacoes.add(SituacaoEnum.SOLICITADO_PRORROGACAO.descricao());
			situacoes.add(SituacaoEnum.PRORROGADO.descricao());
			situacoes.add(SituacaoEnum.NAO_PRORROGADO.descricao());
			situacoes.add(SituacaoEnum.ALTA_REGISTRADA.descricao());
			situacoes.add(SituacaoEnum.FECHADO.descricao());
			situacoes.add(SituacaoEnum.ENVIADO.descricao());
			situacoes.add(SituacaoEnum.RECEBIDO.descricao());
			situacoes.add(SituacaoEnum.DEVOLVIDO.descricao());
			situacoes.add(SituacaoEnum.AUDITADO.descricao());
			situacoes.add(SituacaoEnum.GLOSADO.descricao());
			situacoes.add(SituacaoEnum.FATURADA.descricao());
			situacoes.add(SituacaoEnum.PAGO.descricao());
			return situacoes;
		}
	},
	EXAME("GEX", "Exame", GuiaExame.class) {
		@Override
		public Set<String> getSituacoesPossiveis() {
			Set<String> situacoes = new HashSet<String>();
			situacoes.add(SituacaoEnum.CANCELADO.descricao());
			situacoes.add(SituacaoEnum.SOLICITADO.descricao());
			situacoes.add(SituacaoEnum.NAO_AUTORIZADO.descricao());
			situacoes.add(SituacaoEnum.CONFIRMADO.descricao());
			situacoes.add(SituacaoEnum.REALIZADO.descricao());
			situacoes.add(SituacaoEnum.ABERTO.descricao());
			situacoes.add(SituacaoEnum.FECHADO.descricao());
			situacoes.add(SituacaoEnum.ENVIADO.descricao());
			situacoes.add(SituacaoEnum.RECEBIDO.descricao());
			situacoes.add(SituacaoEnum.DEVOLVIDO.descricao());
			situacoes.add(SituacaoEnum.AUDITADO.descricao());
			situacoes.add(SituacaoEnum.GLOSADO.descricao());
			situacoes.add(SituacaoEnum.FATURADA.descricao());
			situacoes.add(SituacaoEnum.PAGO.descricao());
			situacoes.add(SituacaoEnum.AUTORIZADO.descricao());
			situacoes.add(SituacaoEnum.PENDENTE.descricao());
			situacoes.add(SituacaoEnum.PARCIALMENTE_AUTORIZADO.descricao());
			return situacoes;
		}
	},
	CONSULTA_ODONTOLOGIA("GCSOD","Consulta Odontológica",GuiaConsultaOdonto.class) {
		@Override
		public Set<String> getSituacoesPossiveis() {
			Set<String> situacoes = new HashSet<String>();
			situacoes.add(SituacaoEnum.CANCELADO.descricao());
			situacoes.add(SituacaoEnum.AGENDADA.descricao());
			situacoes.add(SituacaoEnum.CONFIRMADO.descricao());
			situacoes.add(SituacaoEnum.FATURADA.descricao());
			situacoes.add(SituacaoEnum.PAGO.descricao());
			return situacoes;
		}
	},
	CONSULTA_ODONTOLOGICA_URGENCIA("GCOU","Consulta Odontológica de Urgência",GuiaConsultaOdontoUrgencia.class) {
		@Override
		public Set<String> getSituacoesPossiveis() {
			Set<String> situacoes = new HashSet<String>();
			situacoes.add(SituacaoEnum.CANCELADO.descricao());
			situacoes.add(SituacaoEnum.CONFIRMADO.descricao());
			situacoes.add(SituacaoEnum.FATURADA.descricao());
			situacoes.add(SituacaoEnum.PAGO.descricao());
			return situacoes;
		}
	},
	EXAME_ODONTOLOGICO("GEXOD","Exame Odontológico", GuiaExameOdonto.class) {
		@Override
		public Set<String> getSituacoesPossiveis() {
			Set<String> situacoes = new HashSet<String>();
			situacoes.add(SituacaoEnum.SOLICITADO.descricao());
			situacoes.add(SituacaoEnum.AUTORIZADO.descricao());
			situacoes.add(SituacaoEnum.PENDENTE.descricao());
			situacoes.add(SituacaoEnum.PARCIALMENTE_AUTORIZADO.descricao());
			situacoes.add(SituacaoEnum.NAO_AUTORIZADO.descricao());
			situacoes.add(SituacaoEnum.CANCELADO.descricao());
			situacoes.add(SituacaoEnum.AUDITADO.descricao());
			situacoes.add(SituacaoEnum.FATURADA.descricao());
			situacoes.add(SituacaoEnum.PAGO.descricao());
			situacoes.add(SituacaoEnum.FECHADO.descricao());
			situacoes.add(SituacaoEnum.REALIZADO.descricao());
			return situacoes;
		}
	},
	CIRURGIA_ODONTOLOGICA("CIROD", "Cirurgia Odontológica", GuiaCirurgiaOdonto.class) {
		@Override
		public Set<String> getSituacoesPossiveis() {
			Set<String> situacoes = new HashSet<String>();
			situacoes.add(SituacaoEnum.CANCELADO.descricao());
			situacoes.add(SituacaoEnum.AUTORIZADO.descricao());
			situacoes.add(SituacaoEnum.ABERTO.descricao());
			situacoes.add(SituacaoEnum.FECHADO.descricao());
			situacoes.add(SituacaoEnum.ENVIADO.descricao());
			situacoes.add(SituacaoEnum.RECEBIDO.descricao());
			situacoes.add(SituacaoEnum.DEVOLVIDO.descricao());
			situacoes.add(SituacaoEnum.AUDITADO.descricao());
			situacoes.add(SituacaoEnum.GLOSADO.descricao());
			situacoes.add(SituacaoEnum.FATURADA.descricao());
			situacoes.add(SituacaoEnum.PAGO.descricao());
			return situacoes;
		}
	},
	HONORARIO_MEDICO("GHM","Honorário Médico", GuiaHonorarioMedico.class) {
		@Override
		public Set<String> getSituacoesPossiveis() {
			Set<String> situacoes = new HashSet<String>();
			situacoes.add(SituacaoEnum.CANCELADO.descricao());
			situacoes.add(SituacaoEnum.FECHADO.descricao());
			situacoes.add(SituacaoEnum.ENVIADO.descricao());
			situacoes.add(SituacaoEnum.RECEBIDO.descricao());
			situacoes.add(SituacaoEnum.DEVOLVIDO.descricao());
			situacoes.add(SituacaoEnum.AUDITADO.descricao());
			situacoes.add(SituacaoEnum.GLOSADO.descricao());
			situacoes.add(SituacaoEnum.FATURADA.descricao());
			situacoes.add(SituacaoEnum.PAGO.descricao());
			return situacoes;
		}
	},
	ACOMPANHAMENTO_ANESTESICO("GAA","Acompanhamento Anestésico", GuiaAcompanhamentoAnestesico.class) {
		@Override
		public Set<String> getSituacoesPossiveis() {
			Set<String> situacoes = new HashSet<String>();
			situacoes.add(SituacaoEnum.CANCELADO.descricao());
			situacoes.add(SituacaoEnum.FECHADO.descricao());
			situacoes.add(SituacaoEnum.ENVIADO.descricao());
			situacoes.add(SituacaoEnum.RECEBIDO.descricao());
			situacoes.add(SituacaoEnum.DEVOLVIDO.descricao());
			situacoes.add(SituacaoEnum.AUDITADO.descricao());
			situacoes.add(SituacaoEnum.GLOSADO.descricao());
			situacoes.add(SituacaoEnum.FATURADA.descricao());
			situacoes.add(SituacaoEnum.PAGO.descricao());
			return situacoes;
		}
	},
	TRATAMENTO_SERIADO("GTS", "Tratamento Seriado", GuiaTratamentoSeriado.class) {
		@Override
		public Set<String> getSituacoesPossiveis() {
			return new HashSet<String>();
		}
	},
	GUIA_RECURSO_GLOSA("GRG", "Guia de Recurso de Glosa", GuiaRecursoGlosa.class) {
		@Override
		public Set<String> getSituacoesPossiveis() {
			Set<String> situacoes = new HashSet<String>();
			situacoes.add(SituacaoEnum.RECURSADO.descricao());
			situacoes.add(SituacaoEnum.DEFERIDO.descricao());
			situacoes.add(SituacaoEnum.INDEFERIDO.descricao());
			situacoes.add(SituacaoEnum.AUDITADO.descricao());
			situacoes.add(SituacaoEnum.ENVIADO.descricao());
			situacoes.add(SituacaoEnum.RECEBIDO.descricao());
			situacoes.add(SituacaoEnum.FATURADA.descricao());
			situacoes.add(SituacaoEnum.PAGO.descricao());
			return situacoes;
		}
	};
	
	private String tipo;
	private String descricao;
	private Class<?> classe;
	
	TipoGuiaEnum(String tipo, String descricao, Class<?> classe) {
		this.tipo = tipo;
		this.descricao = descricao;
		this.classe = classe;
	}
	
	public static TipoGuiaEnum getTipoDeGuia(String discriminator){
		for (TipoGuiaEnum tipo : TipoGuiaEnum.values()) {
			if (tipo.tipo.equalsIgnoreCase(discriminator))
				return tipo;
		}
		return null;
	}

	public String tipo() {
		return tipo;
	}

	public String descricao() {
		return descricao;
	}

	public Class<?> classe() {
		return classe;
	}
	
	//TODO linkar com {@link ResumoGuias#getSituacao(Integer), lá se propõe a fazer a mesma coisa}
	public abstract Set<String> getSituacoesPossiveis();
}