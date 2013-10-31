package br.com.infoway.ecare.services.tarefasCorrecao.relatorioMedAlliance;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import br.com.infowaypi.ecarebc.enums.SituacaoEnum;

public enum SituacoesParaORelatorio {
	FECHADA {
		@Override
		public String getCabecalho() {
			return "Foram consideradas guias de consulta, exame eletivo, urgências (consulta de urgência " +
			"e atendimento subsequente) e internações (urgência e eletiva) que estão nas situações " +
			"\"Fechado(a)\".";
		}

		@Override
		public String getPrefixoDoArquivo() {
			return "ArquivoMA_Fechadas";
		}

		@Override
		public List<String> getTiposDeGuia() {
			return Arrays.asList(SituacaoEnum.FECHADO.descricao());
		}

		@Override
		public String getPasta() {
			return "Fechadas";
		}
	},
	AUDITADA {
		@Override
		public String getCabecalho() {
			return "Foram consideradas guias de consulta, exame eletivo, urgências (consulta de urgência " +
			"e atendimento subsequente) e internações (urgência e eletiva) que estão nas situações " +
			"\"Confirmado(a)\", \"Auditado(a)\", \"Faturado(a)\" e \"Pago(a)\".";
		}

		@Override
		public String getPrefixoDoArquivo() {
			return "ArquivoMA_Auditadas";
		}

		@Override
		public List<String> getTiposDeGuia() {
			return Arrays.asList(SituacaoEnum.CONFIRMADO.descricao(), SituacaoEnum.AUDITADO.descricao(),
					SituacaoEnum.FATURADA.descricao(), SituacaoEnum.PAGO.descricao());
		}

		@Override
		public String getPasta() {
			return "Auditadas";
		}
	},
	FATURADA {
		@Override
		public String getCabecalho() {
			return "Foram consideradas guias de consulta, exame eletivo, urgências (consulta de urgência " +
			"e atendimento subsequente) e internações (urgência e eletiva) que estão nas situações " +
			"\"Faturado(a)\" e \"Pago(a)\".";
		}

		@Override
		public String getPrefixoDoArquivo() {
			return "ArquivoMA_Faturadas";
		}

		@Override
		public List<String> getTiposDeGuia() {
			return Arrays.asList(SituacaoEnum.FATURADA.descricao(), SituacaoEnum.PAGO.descricao());
		}

		@Override
		public String getPasta() {
			return "Faturadas";
		}
	};
	
	public final String getSituacoes() {
		StringBuilder builder = new StringBuilder();
		Iterator<String> iterator = this.getTiposDeGuia().iterator();
		while (iterator.hasNext()) {
			String situacao = iterator.next();
			builder.append(situacao);
			if (iterator.hasNext()) {
				builder.append(", ");
			}
		}
		return builder.toString();
	}
	
	public abstract List<String> getTiposDeGuia();
	public abstract String getCabecalho();
	public abstract String getPrefixoDoArquivo();
	public abstract String getPasta();
}
