package br.com.infowaypi.ecare.services.recurso;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.ItemGlosavel;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemGasoterapia;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemGuia;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemTaxa;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOutros;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.user.UsuarioInterface;

public class ItemRecursoGlosa extends ImplColecaoSituacoesComponent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long idItemRecursoGlosa;
	private GuiaRecursoGlosa guiaRecursoGlosa;
	private Date dataEntradaRecurso;
	private String justificativa;
	private String justificativaDeferimentoIndeferimento;
	private Boolean deferir;
	
	private GuiaCompleta<ProcedimentoInterface> guiaGlosada;
	private ItemGuia itemGuiaGlosada;
	private Procedimento procedimentoGlosado;
	private TipoItemRecursavelEnum tipoItemRecurso;
	
	public ItemRecursoGlosa() {
	}
	
	@SuppressWarnings("unchecked")
	public ItemRecursoGlosa(UsuarioInterface usuario, ItemGlosavel item, GuiaRecursoGlosa recurso, String justificativa) { 
		boolean isGuia = item.isTipoGuia();
		boolean isProcedimento = item.isTipoProcedimento();
		boolean isItemGuia = item.isTipoItemGuia();

		if (isGuia) {
			guiaGlosada = (GuiaCompleta<ProcedimentoInterface>) item;
			guiaGlosada.setItemRecurso(this);
			this.tipoItemRecurso = TipoItemRecursavelEnum.GUIA_COMPLETA;
		} 
		else if (isProcedimento) {
			procedimentoGlosado = (Procedimento) item;
			procedimentoGlosado.setItemRecurso(this);
			
			if (procedimentoGlosado instanceof ProcedimentoCirurgico) {
				this.tipoItemRecurso = TipoItemRecursavelEnum.PROCEDIMENTO_CIRURGICO;
			} 
			else if (procedimentoGlosado instanceof ProcedimentoOutros){
				this.tipoItemRecurso = TipoItemRecursavelEnum.PROCEDIMENTO_OUTROS;
			} 
			else {
				this.tipoItemRecurso = TipoItemRecursavelEnum.PROCEDIMENTO_EXAME;
			}
		} 
		else if (isItemGuia) {
			itemGuiaGlosada = (ItemGuia) item;
			itemGuiaGlosada.setItemRecurso(this);
			
			if (itemGuiaGlosada instanceof ItemGasoterapia) {
				this.tipoItemRecurso = TipoItemRecursavelEnum.GASOTERAPIA;
			} 
			else if (itemGuiaGlosada instanceof ItemTaxa){
				this.tipoItemRecurso = TipoItemRecursavelEnum.TAXA;
			}
			else if (itemGuiaGlosada instanceof ItemPacote){
				this.tipoItemRecurso = TipoItemRecursavelEnum.PACOTE;
			}
			else if (itemGuiaGlosada instanceof ItemDiaria){
				this.tipoItemRecurso = TipoItemRecursavelEnum.DIARIA;
			}
		}
		this.justificativa = justificativa;
		this.dataEntradaRecurso = new Date();
		this.guiaRecursoGlosa = recurso;
		this.mudarSituacao(usuario, SituacaoEnum.RECURSADO.descricao(), "Recurso de Glosa Gerado.", new Date());
	}
	
	public GuiaRecursoGlosa getGuiaRecursoGlosa() {
		return guiaRecursoGlosa;
	}
	
	public String getMotivoGlosa() {
		if (guiaGlosada != null) {
			return guiaGlosada.getMotivoGlosa().getDescricao();
		} else if (procedimentoGlosado != null) {
			if (procedimentoGlosado.getMotivoGlosaProcedimento() == null) {
				return ((Procedimento) procedimentoGlosado.getItemGlosavelAnterior()).getMotivoGlosaProcedimento().getDescricao();
			} else {
				return procedimentoGlosado.getMotivoGlosaProcedimento().getDescricao();
			}
		} else {
			if (itemGuiaGlosada.getMotivoGlosa() == null) {
				return ((ItemGuia) itemGuiaGlosada.getItemGlosavelAnterior()).getMotivoGlosa().getDescricao();
			} else {
				return itemGuiaGlosada.getMotivoGlosa().getDescricao();
			}
		}
	}
	
	public void setGuiaRecursoGlosa(GuiaRecursoGlosa recursoGlosa) {
		this.guiaRecursoGlosa = recursoGlosa;
	}
	public Date getDataEntradaRecurso() {
		return dataEntradaRecurso;
	}
	public void setDataEntradaRecurso(Date dataEntradaRecurso) {
		this.dataEntradaRecurso = dataEntradaRecurso;
	}
	public String getJustificativa() {
		return justificativa;
	}
	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public Long getIdItemRecursoGlosa() {
		return idItemRecursoGlosa;
	}

	public void setIdItemRecursoGlosa(Long idItemRecursoGlosa) {
		this.idItemRecursoGlosa = idItemRecursoGlosa;
	}

	public GuiaCompleta<ProcedimentoInterface> getGuiaGlosada() {
		if (guiaGlosada != null) {
			return guiaGlosada;
		}
		return null;
	}

	public void setGuiaGlosada(GuiaCompleta<ProcedimentoInterface> guiaGlosada) {
		this.guiaGlosada = guiaGlosada;
	}

	public ItemGuia getItemGuiaGlosada() {
		return itemGuiaGlosada;
	}

	public void setItemGuiaGlosada(ItemGuia itemGuiaGlosada) {
		this.itemGuiaGlosada = itemGuiaGlosada;
	}

	public Procedimento getProcedimentoGlosado() {
		return procedimentoGlosado;
	}

	public void setProcedimentoGlosado(Procedimento procedimentoGlosado) {
		this.procedimentoGlosado = procedimentoGlosado;
	}

	public Boolean getDeferir() {
		return deferir;
	}

	public void setDeferir(Boolean deferir) {
		this.deferir = deferir;
	}

	public String getJustificativaDeferimentoIndeferimento() {
		return justificativaDeferimentoIndeferimento;
	}

	public void setJustificativaDeferimentoIndeferimento(
			String justificativaDeferimentoIndeferimento) {
		this.justificativaDeferimentoIndeferimento = justificativaDeferimentoIndeferimento;
	}
	
	public String getDataFormatada() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(this.getDataEntradaRecurso());
	}
	
	public String getDataDeferimentoIndeferimentoFormatada(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (this.getJustificativaDeferimentoIndeferimento() != null) {
			return sdf.format(this.getSituacao().getDataSituacao());
		} else {
			return "";
		}
	}

	public TipoItemRecursavelEnum getTipoItemRecurso() {
		return tipoItemRecurso;
	}
	
	public ItemGlosavel getItemGlosavel() {
		if (this.getGuiaGlosada()!= null) {
			return this.getGuiaGlosada();
		} else if (this.getItemGuiaGlosada()!=null) {
			return this.getItemGuiaGlosada();
		} else {
			return this.getProcedimentoGlosado();
		}
	}

	@Override
	public boolean isSituacaoAtual(String descricao) {
		return super.isSituacaoAtual(descricao);
	}
	
}