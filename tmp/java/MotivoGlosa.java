package br.com.infowaypi.ecare.cadastros;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import br.com.infowaypi.msr.utils.Utils;

public class MotivoGlosa implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long idMotivoGlosa;
	private String descricao;
	private boolean ativo;
	private String motivo;
	private Integer codigoMensagem;
	private String codigoDescricao;
	private GrupoMotivoGlosa grupo;
	private String observacao;
	
	private Boolean aplicavelGuiaCompleta;
	private Boolean aplicavelProcedimentos;
	private Boolean aplicavelProcedimentosExames;
	private Boolean aplicavelOutrosProcedimentos;
	private Boolean aplicavelGasoterapias;
	private Boolean aplicavelTaxas;
	private Boolean aplicavelPacotes;
	private Boolean aplicavelDiarias;
	private Boolean aplicavelTodos;
	
	private Set<ItemAplicavel> itensGuiaAplicaveis;
	
	public MotivoGlosa() {
		this.ativo = true;
		this.motivo = "Criação de Motivo de glosa.";
		this.itensGuiaAplicaveis = new HashSet<ItemAplicavel>();
		this.aplicavelTodos = false;
		this.aplicavelGuiaCompleta = false;
		this.aplicavelProcedimentos = false;
		this.aplicavelProcedimentosExames = false;
		this.aplicavelOutrosProcedimentos = false;
		this.aplicavelGasoterapias = false;
		this.aplicavelTaxas = false;
		this.aplicavelPacotes = false;
		this.aplicavelDiarias = false;
	}

	public Long getIdMotivoGlosa() {
		return idMotivoGlosa;
	}

	public void setIdMotivoGlosa(Long idMotivoGlosa) {
		this.idMotivoGlosa = idMotivoGlosa;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	public Set<ItemAplicavel> getItensGuiaAplicaveis() {
		return itensGuiaAplicaveis;
	}
	
	public void setItensGuiaAplicaveis(Set<ItemAplicavel> itensGuiaAplicaveis) {
		this.itensGuiaAplicaveis = itensGuiaAplicaveis;
	}

	public void ativar(String motivo) throws Exception {
		if (Utils.isStringVazia(motivo))
			throw new Exception("O motivo deve ser preenchido!");
		this.ativo = true;
		this.motivo = motivo;
	}

	public void desativar(String motivo) throws Exception {
		if (Utils.isStringVazia(motivo))
			throw new Exception("O motivo deve ser preenchido!");
		this.ativo = false;
		this.motivo = motivo;
	}
	
	public Boolean getAplicavelGuiaCompleta() {
		for (ItemAplicavel item : this.itensGuiaAplicaveis) {
			if(item.getDescricao().equals(ItemAplicavelEnum.ITEM_GUIA_COMPLETA.getDescricao())
					|| getAplicavelTodos() == true){
				aplicavelGuiaCompleta = true;
			}
		}
		if(aplicavelTodos) aplicavelGuiaCompleta = true;
		return aplicavelGuiaCompleta;
	}

	public void setAplicavelGuiaCompleta(Boolean aplicavelGuiaCompleta) {
		this.aplicavelGuiaCompleta = aplicavelGuiaCompleta;
	}

	public Boolean getAplicavelProcedimentos() {
		for (ItemAplicavel item : this.itensGuiaAplicaveis) {
			if(item.getDescricao().equals(ItemAplicavelEnum.ITEM_PROCEDIMENTO.getDescricao())
					|| getAplicavelTodos() == true){
				aplicavelProcedimentos = true;
			}
		}
		if(aplicavelTodos) aplicavelProcedimentos = true;
		return aplicavelProcedimentos;
	}

	public void setAplicavelProcedimentos(Boolean aplicavelProcedimentos) {
		this.aplicavelProcedimentos = aplicavelProcedimentos;
	}

	public Boolean getAplicavelProcedimentosExames() {
		for (ItemAplicavel item : this.itensGuiaAplicaveis) {
			if(item.getDescricao().equals(ItemAplicavelEnum.ITEM_PROCEDIMENTO_EXAME.getDescricao())
					|| getAplicavelTodos() == true){
				aplicavelProcedimentosExames = true;
			}
		}
		if(aplicavelTodos) aplicavelProcedimentosExames = true;
		return aplicavelProcedimentosExames;
	}

	public void setAplicavelProcedimentosExames(Boolean aplicavelProcedimentosExames) {
		this.aplicavelProcedimentosExames = aplicavelProcedimentosExames;
	}

	public Boolean getAplicavelOutrosProcedimentos() {
		for (ItemAplicavel item : this.itensGuiaAplicaveis) {
			if(item.getDescricao().equals(ItemAplicavelEnum.ITEM_OUTROS_PROCEDIMENTOS.getDescricao())
					|| getAplicavelTodos() == true){
				aplicavelOutrosProcedimentos = true;
			}
		}
		if(aplicavelTodos) aplicavelOutrosProcedimentos = true;
		return aplicavelOutrosProcedimentos;
	}

	public void setAplicavelOutrosProcedimentos(Boolean aplicavelOutrosProcedimentos) {
		this.aplicavelOutrosProcedimentos = aplicavelOutrosProcedimentos;
	}

	public Boolean getAplicavelGasoterapias() {
		for (ItemAplicavel item : this.itensGuiaAplicaveis) {
			if(item.getDescricao().equals(ItemAplicavelEnum.ITEM_GASOTERAPIA.getDescricao())
					|| getAplicavelTodos() == true){
				aplicavelGasoterapias = true;
			}
		}
		if(aplicavelTodos) aplicavelGasoterapias = true;
		return aplicavelGasoterapias;
	}

	public void setAplicavelGasoterapias(Boolean aplicavelGasoterapias) {
		this.aplicavelGasoterapias = aplicavelGasoterapias;
	}

	public Boolean getAplicavelTaxas() {
		for (ItemAplicavel item : this.itensGuiaAplicaveis) {
			if(item.getDescricao().equals(ItemAplicavelEnum.ITEM_TAXA.getDescricao())
					|| getAplicavelTodos() == true){
				aplicavelTaxas = true;
			}
		}
		if(aplicavelTodos) aplicavelTaxas = true;
		return aplicavelTaxas;
	}

	public void setAplicavelTaxas(Boolean aplicavelTaxas) {
		this.aplicavelTaxas = aplicavelTaxas;
	}

	public Boolean getAplicavelPacotes() {
		for (ItemAplicavel item : this.itensGuiaAplicaveis) {
			if(item.getDescricao().equals(ItemAplicavelEnum.ITEM_PACOTE.getDescricao())
					|| getAplicavelTodos() == true){
				aplicavelPacotes = true;
			}
		}
		if(aplicavelTodos) aplicavelPacotes = true;
		return aplicavelPacotes;
	}

	public void setAplicavelPacotes(Boolean aplicavelPacotes) {
		this.aplicavelPacotes = aplicavelPacotes;
	}

	public Boolean getAplicavelDiarias() {
		for (ItemAplicavel item : this.itensGuiaAplicaveis) {
			if(item.getDescricao().equals(ItemAplicavelEnum.ITEM_DIARIA.getDescricao())
					|| getAplicavelTodos() == true){
				aplicavelDiarias = true;
			}
		}
		if(aplicavelTodos) aplicavelDiarias = true;
		return aplicavelDiarias;
	}

	public void setAplicavelDiarias(Boolean aplicavelDiarias) {
		this.aplicavelDiarias = aplicavelDiarias;
	}

	public Boolean getAplicavelTodos() {
		for (ItemAplicavel item : this.itensGuiaAplicaveis) {
			if(item != null && item.getDescricao().equals(ItemAplicavelEnum.TODOS.getDescricao())){
				aplicavelTodos = true;
			}
		}
		if(aplicavelTodos) aplicavelTodos = true;
		return aplicavelTodos;
	}

	public void setAplicavelTodos(Boolean aplicavelTodos) {
		this.aplicavelTodos = aplicavelTodos;
	}

	public Integer getCodigoMensagem() {
		return codigoMensagem;
	}

	public void setCodigoMensagem(Integer codigoMensagem) {
		this.codigoMensagem = codigoMensagem;
	}

	public GrupoMotivoGlosa getGrupo() {
		return grupo;
	}

	public void setGrupo(GrupoMotivoGlosa grupo) {
		this.grupo = grupo;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public String getCodigoDescricao() {
		if(codigoMensagem != null && descricao != null){
			codigoDescricao = codigoMensagem + " - " + descricao;
		}
		return codigoDescricao;
	}

	public void setCodigoDescricao(String codigoDescricao) {
		this.codigoDescricao = codigoDescricao;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
}
