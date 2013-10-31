package br.com.infowaypi.ecarebc.atendimentos;

import java.io.Serializable;
import java.math.BigDecimal;

import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ComponentValores;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemGuia;
import br.com.infowaypi.ecarebc.utils.SituacaoUtils;

public class ValoresMatMed extends ItemGuia implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	private BigDecimal valorTotalMateriaisMedicosApartamento;
	private BigDecimal valorTotalMateriaisMedicosApartamentoAuditado;
	private String justificativaMateriaisMedicosApartamento;
	private BigDecimal valorTotalMateriaisMedicosUTI;
	private BigDecimal valorTotalMateriaisMedicosUTIAuditado;
	private String justificativaMateriaisMedicosUTI;
	private BigDecimal valorTotalMateriaisMedicosBlocoCirurgico;
	private BigDecimal valorTotalMateriaisMedicosBlocoCirurgicoAuditado;
	private String justificativaMateriaisMedicosBlocoCirurgico;
	private BigDecimal valorTotalMateriaisProntoSocorro;
	private BigDecimal valorTotalMateriaisProntoSocorroAuditado;
	private String justificativaMateriaisProntoSocorro;
	private BigDecimal valorTotalMedicamentosApartamento;
	private BigDecimal valorTotalMedicamentosApartamentoAuditado;
	private String justificativaMedicamentosApartamento;
	private BigDecimal valorTotalMedicamentosUTI;
	private BigDecimal valorTotalMedicamentosUTIAuditado;
	private String justificativaMedicamentosUTI;
	private BigDecimal valorTotalMedicamentosBlocoCirurgico;
	private BigDecimal valorTotalMedicamentosBlocoCirurgicoAuditado;
	private String justificativaMedicamentosBlocoCirurgico;
	private BigDecimal valorTotalMedicamentosProntoSocorro;
	private BigDecimal valorTotalMedicamentosProntoSocorroAuditado;
	private String justificativaMedicamentosProntoSocorro;
	private BigDecimal valorTotalMateriaisEspeciais;
	private BigDecimal valorTotalMateriaisEspeciaisAuditado;
	private BigDecimal valorTotalOPMES;
	private BigDecimal valorTotalOPMESAuditado;
	private String justificativaMateriaisEspeciais;
	private BigDecimal valorTotalMedicamentosEspeciais;
	private BigDecimal valorTotalMedicamentosEspeciaisAuditado;
	private String justificativaMedicamentosEspeciais;
	private BigDecimal valorOutros;
	private BigDecimal valorOutrosAuditado;
	private String descricaoOutros;
	private String justificativaOutros;
	
	public ValoresMatMed(){
		this.valorTotalMateriaisMedicosApartamento = BigDecimal.ZERO;
		this.valorTotalMateriaisMedicosApartamentoAuditado= BigDecimal.ZERO;
		this.valorTotalMateriaisMedicosUTI= BigDecimal.ZERO;
		this.valorTotalMateriaisMedicosUTIAuditado= BigDecimal.ZERO;
		this.valorTotalMateriaisMedicosBlocoCirurgico= BigDecimal.ZERO;
		this.valorTotalMateriaisMedicosBlocoCirurgicoAuditado= BigDecimal.ZERO;
		this.valorTotalMedicamentosApartamento= BigDecimal.ZERO;
		this.valorTotalMedicamentosApartamentoAuditado= BigDecimal.ZERO;
		this.valorTotalMedicamentosUTI= BigDecimal.ZERO;
		this.valorTotalMedicamentosUTIAuditado= BigDecimal.ZERO;
		this.valorTotalMedicamentosBlocoCirurgico= BigDecimal.ZERO;
		this.valorTotalMedicamentosBlocoCirurgicoAuditado= BigDecimal.ZERO;
		this.valorTotalMateriaisEspeciais= BigDecimal.ZERO;
		this.valorTotalMateriaisEspeciaisAuditado= BigDecimal.ZERO;
		this.valorTotalOPMES = BigDecimal.ZERO;
		this.valorTotalOPMESAuditado = BigDecimal.ZERO;
		this.valorTotalMedicamentosEspeciais= BigDecimal.ZERO;
		this.valorTotalMedicamentosEspeciaisAuditado= BigDecimal.ZERO;
		this.valorOutros = BigDecimal.ZERO;
		this.valorOutrosAuditado = BigDecimal.ZERO;
		this.valorTotalMateriaisProntoSocorro = BigDecimal.ZERO;
		this.valorTotalMateriaisProntoSocorroAuditado = BigDecimal.ZERO;
		this.valorTotalMedicamentosProntoSocorro = BigDecimal.ZERO;
		this.valorTotalMedicamentosProntoSocorroAuditado = BigDecimal.ZERO;
		
	}
	
	public BigDecimal getValorTotalMateriaisMedicosApartamento() {
		return valorTotalMateriaisMedicosApartamento;
	}

	public void setValorTotalMateriaisMedicosApartamento(
			BigDecimal valorTotalMateriaisMedicosApartamento) {
		this.valorTotalMateriaisMedicosApartamento = valorTotalMateriaisMedicosApartamento;
	}

	public BigDecimal getValorTotalMateriaisMedicosApartamentoAuditado() {
		return valorTotalMateriaisMedicosApartamentoAuditado;
	}

	public void setValorTotalMateriaisMedicosApartamentoAuditado(
			BigDecimal valorTotalMateriaisMedicosApartamentoAuditado) {
		this.valorTotalMateriaisMedicosApartamentoAuditado = valorTotalMateriaisMedicosApartamentoAuditado;
	}

	public BigDecimal getValorTotalMateriaisMedicosBlocoCirurgico() {
		return valorTotalMateriaisMedicosBlocoCirurgico;
	}

	public void setValorTotalMateriaisMedicosBlocoCirurgico(
			BigDecimal valorTotalMateriaisMedicosBlocoCirurgico) {
		this.valorTotalMateriaisMedicosBlocoCirurgico = valorTotalMateriaisMedicosBlocoCirurgico;
	}

	public BigDecimal getValorTotalMateriaisMedicosBlocoCirurgicoAuditado() {
		return valorTotalMateriaisMedicosBlocoCirurgicoAuditado;
	}

	public void setValorTotalMateriaisMedicosBlocoCirurgicoAuditado(
			BigDecimal valorTotalMateriaisMedicosBlocoCirurgicoAuditado) {
		this.valorTotalMateriaisMedicosBlocoCirurgicoAuditado = valorTotalMateriaisMedicosBlocoCirurgicoAuditado;
	}

	public BigDecimal getValorTotalMateriaisMedicosUTI() {
		return valorTotalMateriaisMedicosUTI;
	}

	public void setValorTotalMateriaisMedicosUTI(
			BigDecimal valorTotalMateriaisMedicosUTI) {
		this.valorTotalMateriaisMedicosUTI = valorTotalMateriaisMedicosUTI;
	}

	public BigDecimal getValorTotalMateriaisMedicosUTIAuditado() {
		return valorTotalMateriaisMedicosUTIAuditado;
	}

	public void setValorTotalMateriaisMedicosUTIAuditado(
			BigDecimal valorTotalMateriaisMedicosUTIAuditado) {
		this.valorTotalMateriaisMedicosUTIAuditado = valorTotalMateriaisMedicosUTIAuditado;
	}

	public BigDecimal getValorTotalMedicamentosApartamento() {
		return valorTotalMedicamentosApartamento;
	}

	public void setValorTotalMedicamentosApartamento(
			BigDecimal valorTotalMedicamentosApartamento) {
		this.valorTotalMedicamentosApartamento = valorTotalMedicamentosApartamento;
	}

	public BigDecimal getValorTotalMedicamentosApartamentoAuditado() {
		return valorTotalMedicamentosApartamentoAuditado;
	}

	public void setValorTotalMedicamentosApartamentoAuditado(
			BigDecimal valorTotalMedicamentosApartamentoAuditado) {
		this.valorTotalMedicamentosApartamentoAuditado = valorTotalMedicamentosApartamentoAuditado;
	}

	public BigDecimal getValorTotalMedicamentosBlocoCirurgico() {
		return valorTotalMedicamentosBlocoCirurgico;
	}

	public void setValorTotalMedicamentosBlocoCirurgico(
			BigDecimal valorTotalMedicamentosBlocoCirurgico) {
		this.valorTotalMedicamentosBlocoCirurgico = valorTotalMedicamentosBlocoCirurgico;
	}

	public BigDecimal getValorTotalMedicamentosBlocoCirurgicoAuditado() {
		return valorTotalMedicamentosBlocoCirurgicoAuditado;
	}

	public void setValorTotalMedicamentosBlocoCirurgicoAuditado(
			BigDecimal valorTotalMedicamentosBlocoCirurgicoAuditado) {
		this.valorTotalMedicamentosBlocoCirurgicoAuditado = valorTotalMedicamentosBlocoCirurgicoAuditado;
	}

	public BigDecimal getValorTotalMedicamentosUTI() {
		return valorTotalMedicamentosUTI;
	}

	public void setValorTotalMedicamentosUTI(BigDecimal valorTotalMedicamentosUTI) {
		this.valorTotalMedicamentosUTI = valorTotalMedicamentosUTI;
	}

	public BigDecimal getValorTotalMedicamentosUTIAuditado() {
		return valorTotalMedicamentosUTIAuditado;
	}

	public void setValorTotalMedicamentosUTIAuditado(
			BigDecimal valorTotalMedicamentosUTIAuditado) {
		this.valorTotalMedicamentosUTIAuditado = valorTotalMedicamentosUTIAuditado;
	}

	public BigDecimal getValorTotalInformado(){
		BigDecimal valorTotalInformado = BigDecimal.ZERO;
		
		valorTotalInformado = valorTotalInformado.add(this.valorTotalMateriaisMedicosApartamento);
		valorTotalInformado = valorTotalInformado.add(this.valorTotalMateriaisMedicosBlocoCirurgico);
		valorTotalInformado = valorTotalInformado.add(this.valorTotalMateriaisMedicosUTI);
		valorTotalInformado = valorTotalInformado.add(this.valorTotalMateriaisProntoSocorro);
		valorTotalInformado = valorTotalInformado.add(this.valorTotalMedicamentosApartamento);
		valorTotalInformado = valorTotalInformado.add(this.valorTotalMedicamentosBlocoCirurgico);
		valorTotalInformado = valorTotalInformado.add(this.valorTotalMedicamentosUTI);
		valorTotalInformado = valorTotalInformado.add(this.valorTotalMedicamentosProntoSocorro);
		valorTotalInformado = valorTotalInformado.add(this.valorTotalMateriaisEspeciais);
		valorTotalInformado = valorTotalInformado.add(this.valorTotalMedicamentosEspeciais);
		valorTotalInformado = valorTotalInformado.add(this.valorOutros);

		return valorTotalInformado;
	}
	
	public BigDecimal getValorTotalAuditado(){
		BigDecimal valorTotalAuditado = BigDecimal.ZERO;
		
		valorTotalAuditado = valorTotalAuditado.add(this.valorTotalMateriaisMedicosApartamentoAuditado);
		valorTotalAuditado = valorTotalAuditado.add(this.valorTotalMateriaisMedicosBlocoCirurgicoAuditado);
		valorTotalAuditado = valorTotalAuditado.add(this.valorTotalMateriaisMedicosUTIAuditado);
		valorTotalAuditado = valorTotalAuditado.add(this.valorTotalMateriaisProntoSocorroAuditado);
		valorTotalAuditado = valorTotalAuditado.add(this.valorTotalMedicamentosApartamentoAuditado);
		valorTotalAuditado = valorTotalAuditado.add(this.valorTotalMedicamentosBlocoCirurgicoAuditado);
		valorTotalAuditado = valorTotalAuditado.add(this.valorTotalMedicamentosUTIAuditado);
		valorTotalAuditado = valorTotalAuditado.add(this.valorTotalMedicamentosProntoSocorroAuditado);
		valorTotalAuditado = valorTotalAuditado.add(this.valorTotalMateriaisEspeciaisAuditado);
		valorTotalAuditado = valorTotalAuditado.add(this.valorTotalMedicamentosEspeciaisAuditado);
		valorTotalAuditado = valorTotalAuditado.add(this.valorOutrosAuditado);

		return valorTotalAuditado;
	}

	public BigDecimal getTotalMateriaisAuditado(){
		BigDecimal valorTotalAuditado = BigDecimal.ZERO;
		
		valorTotalAuditado = valorTotalAuditado.add(this.valorTotalMateriaisMedicosApartamentoAuditado);
		valorTotalAuditado = valorTotalAuditado.add(this.valorTotalMateriaisMedicosBlocoCirurgicoAuditado);
		valorTotalAuditado = valorTotalAuditado.add(this.valorTotalMateriaisMedicosUTIAuditado);
		valorTotalAuditado = valorTotalAuditado.add(this.valorTotalMateriaisProntoSocorroAuditado);
		valorTotalAuditado = valorTotalAuditado.add(this.valorTotalMateriaisEspeciaisAuditado);
		
		return valorTotalAuditado;
	}
	
	public BigDecimal getTotalMateriais(){
		BigDecimal valorTotal = BigDecimal.ZERO;
		
		valorTotal =  valorTotal.add(this.valorTotalMateriaisMedicosApartamento);
		valorTotal =  valorTotal.add(this.valorTotalMateriaisMedicosBlocoCirurgico);
		valorTotal =  valorTotal.add(this.valorTotalMateriaisMedicosUTI);
		valorTotal =  valorTotal.add(this.valorTotalMateriaisProntoSocorro);
		valorTotal =  valorTotal.add(this.valorTotalMateriaisEspeciaisAuditado);
		
		return valorTotal;
	}
	
	public BigDecimal getTotalMedicamentosAuditado(){
		BigDecimal valorTotalAuditado = BigDecimal.ZERO;
		
		valorTotalAuditado =  valorTotalAuditado.add(this.valorTotalMedicamentosApartamentoAuditado);
		valorTotalAuditado =  valorTotalAuditado.add(this.valorTotalMedicamentosBlocoCirurgicoAuditado);
		valorTotalAuditado =  valorTotalAuditado.add(this.valorTotalMedicamentosUTIAuditado);
		valorTotalAuditado =  valorTotalAuditado.add(this.valorTotalMedicamentosProntoSocorroAuditado);
		valorTotalAuditado =  valorTotalAuditado.add(this.valorTotalMedicamentosEspeciaisAuditado);
		return valorTotalAuditado;
	}
	
	public BigDecimal getTotalMedicamentos(){
		BigDecimal valorTotal = BigDecimal.ZERO;
		
		valorTotal =  valorTotal.add(this.valorTotalMedicamentosApartamento);
		valorTotal =  valorTotal.add(this.valorTotalMedicamentosBlocoCirurgico);
		valorTotal =  valorTotal.add(this.valorTotalMedicamentosUTI);
		valorTotal =  valorTotal.add(this.valorTotalMedicamentosProntoSocorro);
		valorTotal =  valorTotal.add(this.valorTotalMedicamentosEspeciais);
		
		return valorTotal;
	}
	
	public BigDecimal getValorTotalMateriaisProntoSocorro() {
		return valorTotalMateriaisProntoSocorro;
	}

	public void setValorTotalMateriaisProntoSocorro(
			BigDecimal valorTotalMateriaisProntoSocorro) {
		this.valorTotalMateriaisProntoSocorro = valorTotalMateriaisProntoSocorro;
	}

	public BigDecimal getValorTotalMateriaisProntoSocorroAuditado() {
		return valorTotalMateriaisProntoSocorroAuditado;
	}

	public void setValorTotalMateriaisProntoSocorroAuditado(
			BigDecimal valorTotalMateriaisProntoSocorroAuditado) {
		this.valorTotalMateriaisProntoSocorroAuditado = valorTotalMateriaisProntoSocorroAuditado;
	}

	public BigDecimal getValorTotalMedicamentosProntoSocorro() {
		return valorTotalMedicamentosProntoSocorro;
	}

	public void setValorTotalMedicamentosProntoSocorro(
			BigDecimal valorTotalMedicamentosProntoSocorro) {
		this.valorTotalMedicamentosProntoSocorro = valorTotalMedicamentosProntoSocorro;
	}

	public BigDecimal getValorTotalMedicamentosProntoSocorroAuditado() {
		return valorTotalMedicamentosProntoSocorroAuditado;
	}

	public void setValorTotalMedicamentosProntoSocorroAuditado(
			BigDecimal valorTotalMedicamentosProntoSocorroAuditado) {
		this.valorTotalMedicamentosProntoSocorroAuditado = valorTotalMedicamentosProntoSocorroAuditado;
	}
	
	
	public BigDecimal getValorTotalSolucoesParenteraisGlosado() {
		if(this.valorTotalMateriaisEspeciaisAuditado.compareTo(this.valorTotalMateriaisEspeciais) < 0) {
			return this.valorTotalMateriaisEspeciais.subtract(this.valorTotalMateriaisEspeciaisAuditado).setScale(2, BigDecimal.ROUND_HALF_UP);
		}else
			return BigDecimal.ZERO;
	}
	
	public BigDecimal getValorTotalMedicamentosBlocoCirurgicoGlosado() {
		if(this.valorTotalMedicamentosBlocoCirurgicoAuditado.compareTo(this.valorTotalMedicamentosBlocoCirurgico) < 0) {
			return this.valorTotalMedicamentosBlocoCirurgico.subtract(this.valorTotalMedicamentosBlocoCirurgicoAuditado).setScale(2, BigDecimal.ROUND_HALF_UP);
		}else
			return BigDecimal.ZERO;
	}
	
	
	public BigDecimal getValorTotalMedicamentosUTIGlosado() {
		if(this.valorTotalMedicamentosUTIAuditado.compareTo(this.valorTotalMedicamentosUTI) < 0) {
			return this.valorTotalMedicamentosUTI.subtract(this.valorTotalMedicamentosUTIAuditado).setScale(2, BigDecimal.ROUND_HALF_UP);
		}else
			return BigDecimal.ZERO;
	}
	
	public BigDecimal getValorTotalMedicamentosApartamentoGlosado() {
		if(this.valorTotalMedicamentosApartamentoAuditado.compareTo(this.valorTotalMedicamentosApartamento) < 0) {
			return this.valorTotalMedicamentosApartamento.subtract(this.valorTotalMedicamentosApartamentoAuditado).setScale(2, BigDecimal.ROUND_HALF_UP);
		}else
			return BigDecimal.ZERO;
	}
	
	public BigDecimal getValorTotalMedicamentosProntoSocorroGlosado() {
		if(this.valorTotalMedicamentosProntoSocorroAuditado.compareTo(this.valorTotalMedicamentosProntoSocorro) < 0) {
			return this.valorTotalMedicamentosProntoSocorro.subtract(this.valorTotalMedicamentosProntoSocorroAuditado).setScale(2, BigDecimal.ROUND_HALF_UP);
		}else
			return BigDecimal.ZERO;
	}
	
	
	public BigDecimal getValorTotalMateriaisMedicosApartamentoGlosado() {
		if(this.valorTotalMedicamentosApartamentoAuditado.compareTo(this.valorTotalMedicamentosApartamento) < 0) {
			return this.valorTotalMateriaisMedicosApartamento.subtract(this.valorTotalMateriaisMedicosApartamentoAuditado).setScale(2, BigDecimal.ROUND_HALF_UP);
		}else
			return BigDecimal.ZERO;
	}
	
	public BigDecimal getValorTotalMateriaisMedicosUTIGlosado() {
		if(this.valorTotalMateriaisMedicosUTIAuditado.compareTo(this.valorTotalMateriaisMedicosUTI) < 0) {
			return this.valorTotalMateriaisMedicosUTI.subtract(this.valorTotalMateriaisMedicosUTIAuditado).setScale(2, BigDecimal.ROUND_HALF_UP);
		}else
			return BigDecimal.ZERO;
	}
	
	public BigDecimal getValorTotalMateriaisMedicosBlocoCirurgicoGlosado() {
		if(this.valorTotalMateriaisMedicosBlocoCirurgicoAuditado.compareTo(this.valorTotalMateriaisMedicosBlocoCirurgico) < 0) {
			return this.valorTotalMateriaisMedicosBlocoCirurgico.subtract(this.valorTotalMateriaisMedicosBlocoCirurgicoAuditado).setScale(2, BigDecimal.ROUND_HALF_UP);
		}else
			return BigDecimal.ZERO;
	}
	
	public BigDecimal getValorTotalMateriaisProntoSocorroGlosado() {
		if(this.valorTotalMateriaisProntoSocorroAuditado.compareTo(this.valorTotalMateriaisProntoSocorro) < 0) {
			return this.valorTotalMateriaisProntoSocorro.subtract(this.valorTotalMateriaisProntoSocorroAuditado).setScale(2, BigDecimal.ROUND_HALF_UP);
		}else
			return BigDecimal.ZERO;
	}
	
	public BigDecimal getValorTotalOPMES() {
		valorTotalOPMES = BigDecimal.ZERO ;
		return valorTotalOPMES;
	}

	public void setValorTotalOPMES(BigDecimal valorTotalOPMES) {
		this.valorTotalOPMES = valorTotalOPMES;
	}
	
	public BigDecimal getValorTotalOPMESAuditado() {
		valorTotalOPMESAuditado = BigDecimal.ZERO;
		return valorTotalOPMESAuditado;
	}

	public void setValorTotalOPMESAuditado(BigDecimal valorTotalOPMESAuditado) {
		this.valorTotalOPMESAuditado = valorTotalOPMESAuditado;
	}

	public BigDecimal getValorTotalMateriaisEspeciais() {
		return valorTotalMateriaisEspeciais;
	}

	public void setValorTotalMateriaisEspeciais(
			BigDecimal valorTotalMateriaisEspeciais) {
		this.valorTotalMateriaisEspeciais = valorTotalMateriaisEspeciais;
	}

	public BigDecimal getValorTotalMateriaisEspeciaisAuditado() {
		return valorTotalMateriaisEspeciaisAuditado;
	}

	public void setValorTotalMateriaisEspeciaisAuditado(
			BigDecimal valorTotalMateriaisEspeciaisAuditado) {
		this.valorTotalMateriaisEspeciaisAuditado = valorTotalMateriaisEspeciaisAuditado;
	}

	public BigDecimal getValorTotalMedicamentosEspeciais() {
		return valorTotalMedicamentosEspeciais;
	}

	public void setValorTotalMedicamentosEspeciais(
			BigDecimal valorTotalMedicamentosEspeciais) {
		this.valorTotalMedicamentosEspeciais = valorTotalMedicamentosEspeciais;
	}

	public BigDecimal getValorTotalMedicamentosEspeciaisAuditado() {
		return valorTotalMedicamentosEspeciaisAuditado;
	}

	public void setValorTotalMedicamentosEspeciaisAuditado(
			BigDecimal valorTotalMedicamentosEspeciaisAuditado) {
		this.valorTotalMedicamentosEspeciaisAuditado = valorTotalMedicamentosEspeciaisAuditado;
	}

	public BigDecimal getValorOutros() {
		return valorOutros;
	}

	public void setValorOutros(BigDecimal valorOutros) {
		this.valorOutros = valorOutros;
	}

	public BigDecimal getValorOutrosAuditado() {
		return valorOutrosAuditado;
	}

	public void setValorOutrosAuditado(BigDecimal valorOutrosAuditado) {
		this.valorOutrosAuditado = valorOutrosAuditado;
	}

	public String getDescricaoOutros() {
		return descricaoOutros;
	}

	public void setDescricaoOutros(String descricaoOutros) {
		this.descricaoOutros = descricaoOutros;
	}
	
	public Object clone(){
		ValoresMatMed clone = new ValoresMatMed();
		
		clone.setValorOutros(this.getValorOutros());
		clone.setColecaoSituacoes(SituacaoUtils.clone(this.getColecaoSituacoes()));
		if(this.getComponentValores() != null){
			clone.setComponentValores((ComponentValores)this.getComponentValores().clone());
		}
		clone.setDescricaoOutros(this.descricaoOutros);
		
		if(this.getSituacao() != null){
			clone.setSituacao(SituacaoUtils.clone(this.getSituacao()));
		}
		
		clone.setValor(this.getValor());
		clone.setValorOutros(this.getValorOutros());
		clone.setValorOutrosAuditado(this.getValorOutrosAuditado());
		clone.setValorTotalMateriaisEspeciais(this.valorTotalMateriaisEspeciais);
		clone.setValorTotalMateriaisEspeciaisAuditado(this.valorTotalMateriaisEspeciaisAuditado);
		clone.setValorTotalMateriaisMedicosApartamento(this.valorTotalMateriaisMedicosApartamento);
		clone.setValorTotalMateriaisMedicosApartamentoAuditado(this.valorTotalMateriaisMedicosApartamentoAuditado);
		clone.setValorTotalMateriaisMedicosBlocoCirurgico(this.valorTotalMateriaisMedicosBlocoCirurgico);
		clone.setValorTotalMateriaisMedicosBlocoCirurgicoAuditado(this.valorTotalMateriaisMedicosBlocoCirurgicoAuditado);
		clone.setValorTotalMateriaisMedicosUTI(this.valorTotalMateriaisMedicosUTI);
		clone.setValorTotalMateriaisMedicosUTIAuditado(this.valorTotalMateriaisMedicosUTIAuditado);
		clone.setValorTotalMateriaisProntoSocorro(this.valorTotalMateriaisProntoSocorro);
		clone.setValorTotalMedicamentosApartamento(this.valorTotalMedicamentosApartamento);
		clone.setValorTotalMedicamentosApartamentoAuditado(this.valorTotalMedicamentosApartamentoAuditado);
		clone.setValorTotalMedicamentosBlocoCirurgico(this.valorTotalMedicamentosBlocoCirurgico);
		clone.setValorTotalMedicamentosBlocoCirurgicoAuditado(this.valorTotalMedicamentosBlocoCirurgicoAuditado);
		clone.setValorTotalMedicamentosEspeciais(this.valorTotalMedicamentosEspeciais);
		clone.setValorTotalMedicamentosEspeciaisAuditado(this.valorTotalMedicamentosEspeciaisAuditado);
		clone.setValorTotalMedicamentosProntoSocorro(this.valorTotalMedicamentosProntoSocorro);
		clone.setValorTotalMedicamentosProntoSocorroAuditado(this.valorTotalMedicamentosProntoSocorroAuditado);
		clone.setValorTotalMedicamentosUTI(this.valorTotalMedicamentosUTI);
		clone.setValorTotalMedicamentosUTIAuditado(this.valorTotalMedicamentosUTIAuditado);
		
		return clone;
	}
	
	public String getJustificativaMateriaisMedicosApartamento() {
		return justificativaMateriaisMedicosApartamento;
	}

	public void setJustificativaMateriaisMedicosApartamento(
			String justificativaMateriaisMedicosApartamento) {
		this.justificativaMateriaisMedicosApartamento = justificativaMateriaisMedicosApartamento;
	}

	public String getJustificativaMateriaisMedicosUTI() {
		return justificativaMateriaisMedicosUTI;
	}

	public void setJustificativaMateriaisMedicosUTI(
			String justificativaMateriaisMedicosUTI) {
		this.justificativaMateriaisMedicosUTI = justificativaMateriaisMedicosUTI;
	}

	public String getJustificativaMateriaisMedicosBlocoCirurgico() {
		return justificativaMateriaisMedicosBlocoCirurgico;
	}

	public void setJustificativaMateriaisMedicosBlocoCirurgico(
			String justificativaMateriaisMedicosBlocoCirurgico) {
		this.justificativaMateriaisMedicosBlocoCirurgico = justificativaMateriaisMedicosBlocoCirurgico;
	}

	public String getJustificativaMateriaisProntoSocorro() {
		return justificativaMateriaisProntoSocorro;
	}

	public void setJustificativaMateriaisProntoSocorro(
			String justificativaMateriaisProntoSocorro) {
		this.justificativaMateriaisProntoSocorro = justificativaMateriaisProntoSocorro;
	}

	public String getJustificativaMedicamentosApartamento() {
		return justificativaMedicamentosApartamento;
	}

	public void setJustificativaMedicamentosApartamento(
			String justificativaMedicamentosApartamento) {
		this.justificativaMedicamentosApartamento = justificativaMedicamentosApartamento;
	}

	public String getJustificativaMedicamentosUTI() {
		return justificativaMedicamentosUTI;
	}

	public void setJustificativaMedicamentosUTI(String justificativaMedicamentosUTI) {
		this.justificativaMedicamentosUTI = justificativaMedicamentosUTI;
	}

	public String getJustificativaMedicamentosBlocoCirurgico() {
		return justificativaMedicamentosBlocoCirurgico;
	}

	public void setJustificativaMedicamentosBlocoCirurgico(
			String justificativaMedicamentosBlocoCirurgico) {
		this.justificativaMedicamentosBlocoCirurgico = justificativaMedicamentosBlocoCirurgico;
	}

	public String getJustificativaMedicamentosProntoSocorro() {
		return justificativaMedicamentosProntoSocorro;
	}

	public void setJustificativaMedicamentosProntoSocorro(
			String justificativaMedicamentosProntoSocorro) {
		this.justificativaMedicamentosProntoSocorro = justificativaMedicamentosProntoSocorro;
	}

	public String getJustificativaMateriaisEspeciais() {
		return justificativaMateriaisEspeciais;
	}

	public void setJustificativaMateriaisEspeciais(
			String justificativaMateriaisEspeciais) {
		this.justificativaMateriaisEspeciais = justificativaMateriaisEspeciais;
	}

	public String getJustificativaMedicamentosEspeciais() {
		return justificativaMedicamentosEspeciais;
	}

	public void setJustificativaMedicamentosEspeciais(
			String justificativaMedicamentosEspeciais) {
		this.justificativaMedicamentosEspeciais = justificativaMedicamentosEspeciais;
	}

	public String getJustificativaOutros() {
		return justificativaOutros;
	}

	public void setJustificativaOutros(String justificativaOutros) {
		this.justificativaOutros = justificativaOutros;
	}
}
