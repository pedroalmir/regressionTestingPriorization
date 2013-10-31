/**
 * 
 */
package br.com.infowaypi.ecare.financeiro.consignacao;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Scanner;

import org.apache.commons.lang.builder.EqualsBuilder;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.molecular.ImplDAO;

/**
 * @author Marcus bOolean
 *
 */
public class ArquivosConsignacoes implements Serializable {
	
	public static String NOVO_ARQUIVO = "@#_@#_@#_@#_";
	
	private static final long serialVersionUID = 1L;
	private Long idArquivosConsignacoes;
	private byte[] arquivoDeBaseSalarial;
	private byte[] arquivoDelinhasErradasBaseSalarial;
	private byte[] arquivoDeLogConsignacoes;
	private byte[] arquivoDeConsignacoes;
	private byte[] arquivoDeRetorno;
	private Date competencia;
	
	public ArquivosConsignacoes() {
		
	}
	
	public byte[] getArquivoDeBaseSalarial() {
		return arquivoDeBaseSalarial;
	}
	public void setArquivoDeBaseSalarial(byte[] arquivoDeBaseSalarial) {
		this.arquivoDeBaseSalarial = arquivoDeBaseSalarial;
	}
	public byte[] getArquivoDelinhasErradasBaseSalarial() {
		return arquivoDelinhasErradasBaseSalarial;
	}
	public void setArquivoDelinhasErradasBaseSalarial(
			byte[] arquivoDelinhasErradasBaseSalarial) {
		this.arquivoDelinhasErradasBaseSalarial = arquivoDelinhasErradasBaseSalarial;
	}
	public byte[] getArquivoDeLogConsignacoes() {
		return arquivoDeLogConsignacoes;
	}
	public void setArquivoDeLogConsignacoes(byte[] arquivoDeLogConsignacoes) {
		this.arquivoDeLogConsignacoes = arquivoDeLogConsignacoes;
	}

	public byte[] getArquivoDeConsignacoes() {
		return arquivoDeConsignacoes;
	}

	public void setArquivoDeConsignacoes(byte[] arquivoDeConsignacoes) {
		this.arquivoDeConsignacoes = arquivoDeConsignacoes;
	}

	public byte[] getArquivoDeRetorno() {
		return arquivoDeRetorno;
	}

	public void setArquivoDeRetorno(byte[] arquivoDeRetorno) {
		this.arquivoDeRetorno = arquivoDeRetorno;
	}

	public Date getCompetencia() {
		return competencia;
	}

	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}
	
	public boolean equals(Object object) {
		if (!(object instanceof ArquivosConsignacoes)) {
			return false;
		}
		ArquivosConsignacoes otherObject = (ArquivosConsignacoes) object;
		return new EqualsBuilder()
			.append(this.competencia, otherObject.getCompetencia())
			.append(this.getClass(), otherObject.getClass())
			.isEquals();
	}

	public Long getIdArquivosConsignacoes() {
		return idArquivosConsignacoes;
	}

	public void setIdArquivosConsignacoes(Long idArquivosConsignacoes) {
		this.idArquivosConsignacoes = idArquivosConsignacoes;
	}

	
	public static void main(String[] args) {
		ArquivosConsignacoes a = ImplDAO.findById(3297226l, ArquivosConsignacoes.class);
		Scanner in = new Scanner(new ByteArrayInputStream(a.getArquivoDelinhasErradasBaseSalarial()));
		while(in.hasNextLine()){
			System.out.println(in.nextLine());
		}
	}
}
