package br.com.infowaypi.ecare.resumos;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.msr.utils.Utils;

public class ResumoRelatorioAprazamento {

	private List<Valor> agrupamentoValores = new ArrayList<Valor>();
	private int quantGuias = 0;
	private int quantDias = 0;
	private BigDecimal media = BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP);
	private boolean exibeGuias;
	
	
//	autorizacao,g.datamarcacao,g.datasituacao,s.nome,p.fantasia, valortotal,date_part('day',(g.datasituacao-g.datamarcacao)) as dias
	
	
	public ResumoRelatorioAprazamento(ResultSet resultSet, boolean exibeGuias) throws SQLException {
		while (resultSet.next()) {
			Valor valor = new Valor();
			valor.setAutorizacao(resultSet.getString(1));
			valor.setDataMarcacao(resultSet.getDate(2));
			valor.setDataAtendimento(resultSet.getDate(3));
			valor.setSituacao(resultSet.getString(4));
			valor.setNome(resultSet.getString(5));
			valor.setPrestador(resultSet.getString(6));
			valor.setValor(resultSet.getBigDecimal(7));
			valor.setQuantidadeDias(resultSet.getInt(8));
			
			agrupamentoValores.add(valor);
			quantDias += valor.getQuantidadeDias();
			quantGuias++;
		}
		
		if(quantGuias <= 0)
			throw new RuntimeException("Nenhuma guia encontrada.");
		
		this.exibeGuias = exibeGuias;
		media = new BigDecimal(quantDias).divide(new BigDecimal(quantGuias),2,BigDecimal.ROUND_HALF_UP);
	}
	
	
	public List<Valor> getAgrupamentoValores() {
		Utils.sort(agrupamentoValores,true, "quantidadeDias");
		return this.agrupamentoValores;
	}

	public void setAgrupamentoValores(List<Valor> agrupamentoValores) {
		this.agrupamentoValores = agrupamentoValores;
	}

	public int getQuantGuias() {
		return quantGuias;
	}


	public void setQuantGuias(int quantGuias) {
		this.quantGuias = quantGuias;
	}


	public int getQuantDias() {
		return quantDias;
	}


	public void setQuantDias(int quantDias) {
		this.quantDias = quantDias;
	}


	public BigDecimal getMedia() {
		return media;
	}


	public void setMedia(BigDecimal media) {
		this.media = media;
	}
	
	public boolean isExibeGuias() {
		return exibeGuias;
	}


	public void setExibeGuias(boolean exibeGuias) {
		this.exibeGuias = exibeGuias;
	}
	

	public class Valor {
		
		String autorizacao;
		Date dataMarcacao;
		Date dataSituacao;
		Date dataAtendimento;
		int quantidadeDias;
		String nome;
		String prestador;
		String situacao;
		BigDecimal valor;
		
		
		public Valor() {
		}

		public String getAutorizacao() {
			return autorizacao;
		}

		public void setAutorizacao(String autorizacao) {
			this.autorizacao = autorizacao;
		}

		public Date getDataMarcacao() {
			return dataMarcacao;
		}

		public void setDataMarcacao(Date dataMarcacao) {
			this.dataMarcacao = dataMarcacao;
		}

		public Date getDataSituacao() {
			return dataSituacao;
		}

		public void setDataSituacao(Date dataSituacao) {
			this.dataSituacao = dataSituacao;
		}

		public int getQuantidadeDias() {
			return quantidadeDias;
		}

		public void setQuantidadeDias(int quantidadeDias) {
			this.quantidadeDias = quantidadeDias;
		}

		public String getNome() {
			return nome;
		}

		public void setNome(String nome) {
			this.nome = nome;
		}

		public String getPrestador() {
			return prestador;
		}

		public void setPrestador(String prestador) {
			this.prestador = prestador;
		}

		public String getSituacao() {
			return situacao;
		}

		public void setSituacao(String situacao) {
			this.situacao = situacao;
		}

		public BigDecimal getValor() {
			return valor;
		}

		public void setValor(BigDecimal valor) {
			this.valor = valor;
		}

		public Date getDataAtendimento() {
			return dataAtendimento;
		}

		public void setDataAtendimento(Date dataAtendimento) {
			this.dataAtendimento = dataAtendimento;
		}
		
	}


}
