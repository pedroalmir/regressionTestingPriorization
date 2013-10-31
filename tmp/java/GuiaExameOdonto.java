package br.com.infowaypi.ecarebc.atendimentos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import br.com.infowaypi.ecare.atendimentos.TipoGuiaEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.odonto.enums.PericiaEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOdonto;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOdontoRestauracao;
import br.com.infowaypi.msr.user.UsuarioInterface;

/** 
 * Classe que representa uma guia de tratamento odontológico
 * @author Danilo Nogueira Portela
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class GuiaExameOdonto extends GuiaExame<ProcedimentoOdonto> implements Serializable{

	private static final long serialVersionUID = 1L;
	public static final int NUMERO_MAXIMO_PROCEDIMENTOS = 15;
	public static final Collection<String> SITUACOES_TRATAMENTOS_REALIZADOS = Arrays.asList(
			SituacaoEnum.FECHADO.descricao(),SituacaoEnum.AUDITADO.descricao(), 
			SituacaoEnum.FATURADA.descricao(), SituacaoEnum.PAGO.descricao());
	
	public static final Collection<String> SITUACOES_TRATAMENTOS_NAO_AUTORIZADOS = Arrays.asList(
			SituacaoEnum.NAO_AUTORIZADO.descricao());
	
	public static final Collection<String> SITUACOES_TRATAMENTOS_SOLICITADOS = Arrays.asList(
			SituacaoEnum.SOLICITADO.descricao(),SituacaoEnum.AUTORIZADO.descricao(),
			SituacaoEnum.PARCIALMENTE_AUTORIZADO.descricao(),SituacaoEnum.AUTORIZADO.descricao()
			,SituacaoEnum.PENDENTE.descricao());

	public GuiaExameOdonto() {
		this(null);
	}
	
	public GuiaExameOdonto(UsuarioInterface usuario) {
		super(usuario);
		this.getSituacao().setDescricao(SituacaoEnum.SOLICITADO.descricao());
		this.getSituacao().setMotivo(MotivoEnum.SOLICITACAO_DE_TRATAMENTO_ODONTOLOGICO.getMessage());
	}
	
	@Override
	public TipoGuiaEnum getTipoGuiaEnum() {
		return TipoGuiaEnum.EXAME_ODONTOLOGICO;
	}
	
	@Override
	public String getTipo() {
		return "Tratamento Odontológico";
	}
	
	@Override
	public boolean isExameOdonto() {
		return true;
	}
	
	public Boolean isRealizarPericia(PericiaEnum pericia){
		for (ProcedimentoOdonto p : this.getProcedimentos()) {
			Boolean isPericiaInicial = pericia.equals(PericiaEnum.INICIAL) && p.getPericiaInicial();
			Boolean isPericiaFinal = pericia.equals(PericiaEnum.FINAL) && p.getPericiaFinal();
			
			if(isPericiaInicial || isPericiaFinal)
				return true;
		}
		return false;
	}
	
	/**
	 * Método que define a prioridade da Autorização.
	 * Se o tratamento está marcado como "Perícia Inicial", possui prioridade na autorização.
	 */
	@Override
	public int getPrioridadeAutorizacao(){
		if (this.isRealizarPericia(PericiaEnum.INICIAL))
			return PRIORIDADE_ALTA;
		return PRIORIDADE_BAIXA;
	}
	
	public List<ProcedimentoOdonto> getProcedimentosOutrosAtivosComPericiaFinal() {
		return getProcedimentosAtivosComPericiaFinal(ProcedimentoOdonto.class);
	}
	
	public List<ProcedimentoOdontoRestauracao> getProcedimentosRestauracaoAtivosComPericiaFinal() {
		return getProcedimentosAtivosComPericiaFinal(ProcedimentoOdontoRestauracao.class);
	}
	
	private <P extends ProcedimentoOdonto> List<P> getProcedimentosAtivosComPericiaFinal(Class classeProcedimento) {
		List<P> procedimentosAtivos = (List<P>) this.getProcedimentosAtivos();
		List<P> procedimentosValidos = new ArrayList<P>();
		
		if (procedimentosAtivos != null) {
			for (P procedimento : procedimentosAtivos) {
				if (procedimento.getPericiaFinal()){
					Boolean isMesmaClasse = classeProcedimento.equals(procedimento.getClass());
					if(isMesmaClasse)
						procedimentosValidos.add(procedimento);
				}
			}
		}
		return procedimentosValidos;
	}
	
	private List<ProcedimentoOdonto> getProcedimentosSolcitadosPorPericia(Boolean periciaInicial) {
		List<ProcedimentoOdonto> procedimentosSolicitados = this.getProcedimentosSolicitados();
		List<ProcedimentoOdonto> procedimentosValidos = new ArrayList<ProcedimentoOdonto>();
		
		if (procedimentosSolicitados != null) {
			for (ProcedimentoOdonto procedimento : procedimentosSolicitados) {
				Boolean comPericia = periciaInicial && procedimento.getPericiaInicial();
				Boolean semPericia = !periciaInicial && !procedimento.getPericiaInicial();
					
				if (comPericia || semPericia) 
					procedimentosValidos.add(procedimento);
			}
		}
		return procedimentosValidos;
	}
	
	public List<ProcedimentoOdonto> getProcedimentosSolicitadosComPericiaInicial() {
		return this.getProcedimentosSolcitadosPorPericia(true);
	}
	
	public List<ProcedimentoOdonto> getProcedimentosSolicitadosSemPericiaInicial() {
		return getProcedimentosSolcitadosPorPericia(false);
	}
	
	@Override
	public void addProcedimento(ProcedimentoOdonto procedimento) throws Exception {
		super.addProcedimento(procedimento);
	}
	
	@Override
	public boolean isExameEletivo() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isGuiaImpressaoNova() {
		return false;
	}
	
//	@Override
//	public VariavelIndice getVariavelIndice() {
//		return VariavelIndice.TRATAMENTOS_ODONTO;
//	}
	
}
