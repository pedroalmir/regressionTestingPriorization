package br.com.infowaypi.ecare.segurados;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;

import br.com.infowaypi.ecare.arquivos.RemessaDeCartao;
import br.com.infowaypi.ecare.enums.SituacaoCartaoEnum;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

public class Cartao implements Serializable {
	
	/**
	 * 
	 */
	
	public static final BigDecimal VALOR_CARTAO = new BigDecimal(10);
	
	private static final long serialVersionUID = 1L;
	private Long idCartao;
	private String numeroDoCartao;
	private Integer viaDoCartao;
	private Segurado segurado;
	private RemessaDeCartao remessa;
	private String situacao;

	
	public Cartao(){
		super();
		this.situacao = SituacaoCartaoEnum.GERADO.getDescricao();
	}
	
	public Cartao(Segurado segurado) {
		this.segurado = segurado;
		segurado.getCartoes().add(this);
		this.viaDoCartao = segurado.getCartoes().size();
		this.numeroDoCartao = gerarNumeroDoCartao();
		this.situacao = SituacaoCartaoEnum.GERADO.getDescricao();
	}
	
	public RemessaDeCartao getRemessa() {
		return remessa;
	}

	public void setRemessa(RemessaDeCartao remessa) {
		this.remessa = remessa;
	}

	public Segurado getSegurado() {
		return segurado;
	}

	public void setSegurado(Segurado segurado) {
		this.segurado = segurado;
	}
	
	public String getNumeroDoCartao() {
		return numeroDoCartao;
	}
	public void setNumeroDoCartao(String numeroDoCartao) {
		this.numeroDoCartao = numeroDoCartao;
	}
	public Integer getViaDoCartao() {
		return viaDoCartao;
	}
	public void setViaDoCartao(Integer viaDoCartao) {
		this.viaDoCartao = viaDoCartao;
	}

	public Long getIdCartao() {
		return idCartao;
	}

	public void setIdCartao(Long idCartao) {
		this.idCartao = idCartao;
	}
	
	public boolean isEnviado(){
		if(remessa == null)
			return false;
			
		return remessa.isEnviado();
	}
	
	public String gerarNumeroDoCartao(){
		Set<AbstractMatricula> matriculas = new HashSet<AbstractMatricula>();
		
		if (segurado.isSeguradoPensionista()) 
			matriculas = ((Pensionista)segurado).getMatriculas();
		else if(segurado.isSeguradoDependente() || segurado.isSeguradoDependenteSuplementar())
			matriculas = ((Titular)segurado.getTitular()).getMatriculas();
		else matriculas = ((Titular)segurado).getMatriculas();
	 
		
		Assert.isNotEmpty(matriculas, "O Titular deve ter no mínimo uma matrícula.");
		
		StringBuffer numeroCartao = new StringBuffer();
		
		String idSegurado = StringUtils.leftPad(String.valueOf(segurado.getIdSegurado()), 10,"0");
		String numOrdem = StringUtils.leftPad(String.valueOf(segurado.getOrdem()),2,"0");
		
		String idEmpresa = null;
		
		for (AbstractMatricula matricula : matriculas) {
			if(matricula.getOrdem() == 1){
				idEmpresa = StringUtils.leftPad(String.valueOf(matricula.getEmpresa().getCodigoLegado()),2,"0");
				break;
			}
		}
		
		String via = String.valueOf(this.viaDoCartao);
		
		numeroCartao = numeroCartao.append(idEmpresa);
		numeroCartao = numeroCartao.append(idSegurado);
		numeroCartao = numeroCartao.append(numOrdem);
		numeroCartao = numeroCartao.append(via);
		
		String digitoVerificador = String.valueOf(Utils.getModulo10(numeroCartao.toString()));
		numeroCartao = numeroCartao.append(digitoVerificador);
		
		return Utils.applyMask(numeroCartao.toString(), "##.##########.##.#-#");
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Cartao)) {
			return false;
		}
		Cartao cartao = (Cartao) object;
		return new EqualsBuilder()
			.append(this.getNumeroDoCartao(), cartao.getNumeroDoCartao())
			.isEquals();
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}
	
	

}
