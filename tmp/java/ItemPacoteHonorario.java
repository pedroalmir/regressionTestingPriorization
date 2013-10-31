package br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecare.resumos.ResumoGuiasHonorarioMedico;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.acordos.AcordoPacoteHonorario;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GuiaHonorarioMedico;
import br.com.infowaypi.ecarebc.atendimentos.honorario.HonorarioExterno;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Pacote;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.exceptions.ValidateException;

public class ItemPacoteHonorario implements Serializable,Constantes{

	private static final long serialVersionUID = 1L;
	
	private Long idItemPacoteHonorario;
	private Pacote pacote;
	private HonorarioExterno honorarioExterno;
	
	/**
	 * Valor do acordo de pacote de honorário para o prestador
	 */
	private BigDecimal valorAcordo;
	
	private BigDecimal porcentagem;
	
	public ItemPacoteHonorario() {
		this.pacote = new Pacote();
		this.valorAcordo = BigDecimal.ZERO;
		this.porcentagem = new BigDecimal(100);
	}
	
	public Pacote getPacote() {
		return pacote;
	}
	public void setPacote(Pacote pacote) {
		this.pacote = pacote;
	}
	
	public BigDecimal getPorcentagem() {
		return porcentagem;
	}

	public void setPorcentagem(BigDecimal porcentagem) {
		this.porcentagem = porcentagem;
	}

	public String getPorcentagemFormatada() {
		if(porcentagem == null)
			return "";
		
		return porcentagem.intValue() + "%";
	}
	
	public Long getIdItemPacoteHonorario() {
		return idItemPacoteHonorario;
	}

	public void setIdItemPacoteHonorario(Long idItemPacoteHonorario) {
		this.idItemPacoteHonorario = idItemPacoteHonorario;
	}

	public HonorarioExterno getHonorarioExterno() {
		return honorarioExterno;
	}

	public void setHonorarioExterno(HonorarioExterno honorarioExterno) {
		this.honorarioExterno = honorarioExterno;
	}

	public BigDecimal getValorAcordo() {
		return valorAcordo;
	}

	public void setValorAcordo(BigDecimal valorAcordo) {
		this.valorAcordo = valorAcordo;
	}

	/**
	 * Valida se o prestador possui acordo para o pacote informado
	 * @throws ValidateException 
	 */
	public Boolean validate(Prestador prestador) throws ValidateException{
		
		if(prestador != null){
			for (AcordoPacoteHonorario acordo : prestador.getAcordosPacoteHonorarioAtivos()) {
				if(acordo.getPacote().equals(this.pacote)){
					this.setValorAcordo(BigDecimal.valueOf(acordo.getValor()));
					return true;
				}
			}
		
			throw new ValidateException(MensagemErroEnum.PRESTADOR_SEM_ACORDO_ATIVO_PARA_O_PACOTE
					.getMessage(this.pacote.getDescricao()));
		}
	    
		return true;
	}

	public Collection<ProcedimentoInterface> getProcedimentosCompativeisComAGuiaOrigem() {
		
		List<ProcedimentoCirurgico> procedimentosGuia = getGuiaOrigem().getProcedimentosNotIn(ProcedimentoCirurgico.class, 
				SituacaoEnum.CANCELADO, SituacaoEnum.NEGADO, SituacaoEnum.GLOSADO, SituacaoEnum.NAO_AUTORIZADO);
		Set<TabelaCBHPM> procedimentosCBHPMDaGuia = new HashSet<TabelaCBHPM>();
		
		for (ProcedimentoInterface proc : procedimentosGuia) {
			procedimentosCBHPMDaGuia.add(proc.getProcedimentoDaTabelaCBHPM());
		}
		
		Set<ProcedimentoInterface> procedimentosCompativeis = new HashSet<ProcedimentoInterface>();
		
		Set<TabelaCBHPM> procedimentosCBHPMPacote = this.getPacote().getProcedimentosCBHPM();
		for (ProcedimentoInterface proc : procedimentosGuia) {
			if(procedimentosCBHPMPacote.contains(proc.getProcedimentoDaTabelaCBHPM())){
				procedimentosCompativeis.add(proc);
			}
		}
		
		return procedimentosCompativeis;
	}

	@SuppressWarnings("unchecked")
	private GuiaSimples<ProcedimentoInterface> getGuiaOrigem() {
		return this.getHonorarioExterno().getGuiaHonorario().getGuiaOrigem();
	}

}
