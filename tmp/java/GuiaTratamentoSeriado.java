package br.com.infowaypi.ecarebc.atendimentos;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.com.infowaypi.ecare.atendimentos.TipoGuiaEnum;
import br.com.infowaypi.ecarebc.atendimentos.enums.SubTipoTratamentoSeriadoEnum;
import br.com.infowaypi.ecarebc.atendimentos.enums.TipoTratamentoSeriadoEnum;
import br.com.infowaypi.ecarebc.atendimentos.enums.TratamentoSeriadoEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.MoneyCalculation;
 
/**
 * Classe que representa uma guia de tratamento seriado do plano de saúde.
 * @author Danilo Nogueira Portela
 * @changes Benedito Barbosa
 * @changes patrícia
 */
@SuppressWarnings("serial")
public class GuiaTratamentoSeriado<P extends ProcedimentoInterface> extends GuiaSimples<P> {
	
	private Integer tratamento;
	private Integer tipoTratamentoSeriado;
	private Integer subTipoTratamentoSeriado;
	
	private Integer quantidadeRealizado;
	private Integer quantidadeAuditado;

	public GuiaTratamentoSeriado() {
		super(null);
	}

	public GuiaTratamentoSeriado(UsuarioInterface usuario) {
		super(usuario);
		this.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AGENDAMENTO_TRATAMENTO_SERIADO.getMessage(), new Date());
	}
	
	@Override
	public TipoGuiaEnum getTipoGuiaEnum() {
		return TipoGuiaEnum.TRATAMENTO_SERIADO;
	}
	
	@Override
	public String getTipo() {
		return "Tratamento Seriado";
	}

	@Override
	public boolean isConsulta() {
		return false;
	}

	@Override
	public boolean isConsultaOdonto() {
		return false;
	}

	@Override
	public boolean isConsultaUrgencia() {
		return false;
	}

	@Override
	public boolean isExame() {
		return false;
	}

	@Override
	public boolean isExameOdonto() {
		return false;
	}
	//Medicina Nuclear
	@Override
	public boolean isInternacao() {
		return false;
	}

	@Override
	public boolean isInternacaoEletiva() {
		return false;
	}

	@Override
	public boolean isInternacaoUrgencia() {
		return false;
	}

	@Override
	public boolean isCompleta() {
		return false;
	}

	@Override
	public boolean isSimples() {
		return true;
	}

	@Override
	public boolean isAtendimentoUrgencia() {
		return false;
	}

	@Override
	public boolean isUrgencia() {
		return false;
	}
	
	@Override
	public BigDecimal getValorCoParticipacao() {
		if(this.getGuiaOrigem()== null || !this.getGuiaOrigem().isInternacao())
			return super.getValorCoParticipacao();
		else
			return MoneyCalculation.rounded(BigDecimal.ZERO);
	}

	@Override
	public boolean isConsultaOdontoUrgencia() {
		return false;
	}

	@Override
	public boolean isCirurgiaOdonto() {
		return false;
	}

	@Override
	public boolean isRegulaConsumo() {
		
		if(isExameExterno())
			return false;
		
		return true;
	}

	@Override
	public boolean isTratamentoSeriado() {
		return true;
	}

	@Override
	public boolean isConsultaEletiva() {
		return false;
	}

	@Override
	public boolean isExameEletivo() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Date getDataVencimento() {
		Calendar vencimento = Calendar.getInstance();
		vencimento.setTime(getDataAtendimento());
		vencimento.add(Calendar.DAY_OF_MONTH, PRAZO_VENCIMENTO_CONSULTAS_E_EXAMES);
		return vencimento.getTime();
	}

	public Integer getTratamento() {
		return tratamento;
	}

	public void setTratamento(Integer tratamento) {
		this.tratamento = tratamento;
	}

	public String getNomeTratamento(){
		return TratamentoSeriadoEnum.getTratamento(this.tratamento).descricao();
	}
	
	public String getTipoTratamento(){
		if(this.tipoTratamentoSeriado == null)
			return "";
		
		return TipoTratamentoSeriadoEnum.getTipoTratamento(this.tipoTratamentoSeriado).tipo();
	}
	
	public Integer getTipoTratamentoSeriado() {
		return tipoTratamentoSeriado;
	}

	public void setTipoTratamentoSeriado(Integer tipoTratamentoSeriado) {
		this.tipoTratamentoSeriado = tipoTratamentoSeriado;
	}
	
	public Integer getSubTipoTratamentoSeriado() {
		return subTipoTratamentoSeriado;
	}

	public void setSubTipoTratamentoSeriado(Integer subtipoTratamentoSeriado) {
		this.subTipoTratamentoSeriado = subtipoTratamentoSeriado;
	}
	
	public String getSubTipoTratamento(){
		if(this.subTipoTratamentoSeriado == null)
			return "";
		return SubTipoTratamentoSeriadoEnum.getSubTipoTratamento(this.subTipoTratamentoSeriado).subTipo();
	}

	public Map<Integer, String> getTipos(Integer tratamento){
		Map<Integer, String> mapa = new HashMap<Integer, String>();
			if (tratamento.equals(TratamentoSeriadoEnum.FISIOTERAPIA.valor())){
				mapa.put(TipoTratamentoSeriadoEnum.MOTORA.valor(), TipoTratamentoSeriadoEnum.MOTORA.tipo());
				mapa.put(TipoTratamentoSeriadoEnum.RESPIRATORIA.valor(), TipoTratamentoSeriadoEnum.RESPIRATORIA.tipo());
				mapa.put(TipoTratamentoSeriadoEnum.CARDIACA.valor(), TipoTratamentoSeriadoEnum.CARDIACA.tipo());
				mapa.put(TipoTratamentoSeriadoEnum.NEUROLOGICA.valor(), TipoTratamentoSeriadoEnum.NEUROLOGICA.tipo());
			}
			
			if (tratamento.equals(TratamentoSeriadoEnum.QUIMIOTERAPIA.valor())){
				mapa.put(TipoTratamentoSeriadoEnum.PRIMEIRO_CICLO.valor(), TipoTratamentoSeriadoEnum.PRIMEIRO_CICLO.tipo());
				mapa.put(TipoTratamentoSeriadoEnum.CICLOS_SUBSEQUENTES.valor(), TipoTratamentoSeriadoEnum.CICLOS_SUBSEQUENTES.tipo());
				mapa.put(TipoTratamentoSeriadoEnum.TRATAMENTO_ADJUVANTE.valor(), TipoTratamentoSeriadoEnum.TRATAMENTO_ADJUVANTE.tipo());
			}
			
			if (tratamento.equals(TratamentoSeriadoEnum.RADIOTERAPIA.valor())){
				mapa.put(TipoTratamentoSeriadoEnum.CLINICA.valor(), TipoTratamentoSeriadoEnum.CLINICA.tipo());
				mapa.put(TipoTratamentoSeriadoEnum.BRAQUITERAPIA.valor(), TipoTratamentoSeriadoEnum.BRAQUITERAPIA.tipo());
			}
			
			if (tratamento.equals(TratamentoSeriadoEnum.TRS.valor())){
				mapa.put(TipoTratamentoSeriadoEnum.HEMODIALISE.valor(), TipoTratamentoSeriadoEnum.HEMODIALISE.tipo());
				mapa.put(TipoTratamentoSeriadoEnum.CAPD.valor(), TipoTratamentoSeriadoEnum.CAPD.tipo());
		}
		
		return mapa;
	}
	
	public Map<Integer, String> getSubTipos(Integer tipoTratamento){
		Map<Integer, String> mapa = new HashMap<Integer, String>();
			if(tipoTratamento.equals(TipoTratamentoSeriadoEnum.PRIMEIRO_CICLO.valor())){
				mapa.put(SubTipoTratamentoSeriadoEnum.PRIMEIRA_LINHA.valor(), SubTipoTratamentoSeriadoEnum.PRIMEIRA_LINHA.subTipo());
				mapa.put(SubTipoTratamentoSeriadoEnum.SEGUNDA_LINHA.valor(), SubTipoTratamentoSeriadoEnum.SEGUNDA_LINHA.subTipo());
				mapa.put(SubTipoTratamentoSeriadoEnum.TERCEIRA_LINHA.valor(), SubTipoTratamentoSeriadoEnum.TERCEIRA_LINHA.subTipo());
				mapa.put(SubTipoTratamentoSeriadoEnum.QUARTA_LINHA.valor(), SubTipoTratamentoSeriadoEnum.QUARTA_LINHA.subTipo());
			}
			
			if (tipoTratamento.equals(TipoTratamentoSeriadoEnum.HEMODIALISE.valor())){
				mapa.put(SubTipoTratamentoSeriadoEnum.AGUDA_POR_SESSAO.valor(), SubTipoTratamentoSeriadoEnum.AGUDA_POR_SESSAO.subTipo());
				mapa.put(SubTipoTratamentoSeriadoEnum.CRONICA_POR_SESSAO.valor(), SubTipoTratamentoSeriadoEnum.CRONICA_POR_SESSAO.subTipo());
				mapa.put(SubTipoTratamentoSeriadoEnum.CONTINUA.valor(), SubTipoTratamentoSeriadoEnum.CONTINUA.subTipo());
			}
		return mapa;
	}

//	@SuppressWarnings("unchecked")
//	public Map<String, String> getProcedimentosPorGrupo(Integer tipoTratamento){
//		Map<String, String> mapa = new HashMap<String, String>();
//		
//		if (tipoTratamento.equals(TipoTratamentoSeriadoEnum.FISIOTERAPIA.valor())){
//			Criteria criteria = HibernateUtil.currentSession().createCriteria(TabelaCBHPM.class);
//			criteria.add(Expression.eq("DescGrSIP_Detalhado", TipoTratamentoSeriadoEnum.FISIOTERAPIA.descricao()));
//
//			List<TabelaCBHPM> procedimentos = criteria.list();
//			for (TabelaCBHPM procedimento : procedimentos) {
//				mapa.put(procedimento.getCodigo(), procedimento.getDescricao());
//			}
//		}
//		
//		if (tipoTratamento.equals(TipoTratamentoSeriadoEnum.QUIMIOTERAPIA.valor())){
//			Criteria criteria = HibernateUtil.currentSession().createCriteria(TabelaCBHPM.class);
//			criteria.add(Expression.eq("DescGrSIP_Detalhado", TipoTratamentoSeriadoEnum.QUIMIOTERAPIA.descricao()));
//
//			List<TabelaCBHPM> procedimentos = criteria.list();
//			for (TabelaCBHPM procedimento : procedimentos) {
//				mapa.put(procedimento.getCodigo(), procedimento.getDescricao());
//			}
//		}
//		
//		if (tipoTratamento.equals(TipoTratamentoSeriadoEnum.RADIOTERAPIA.valor())){
//			Criteria criteria = HibernateUtil.currentSession().createCriteria(TabelaCBHPM.class);
//			criteria.add(Expression.eq("DescGrSIP_Detalhado", TipoTratamentoSeriadoEnum.RADIOTERAPIA.descricao()));
//
//			List<TabelaCBHPM> procedimentos = criteria.list();
//			for (TabelaCBHPM procedimento : procedimentos) {
//				mapa.put(procedimento.getCodigo(), procedimento.getDescricao());
//			}
//		}
//		
//		return mapa;
//	}

	public boolean isGuiaImpressaoNova(){
		return true;
	}

	public Integer getQuantidadeSessoes() {
		int quantidade = 0;
		for (ProcedimentoInterface procedimento : getProcedimentosNaoCanceladosENegados()) {
			quantidade += procedimento.getQuantidade();
		}
		return quantidade;
	}
	
	public boolean isTratamentoIniciado(){
		return isSituacaoAtual(SituacaoEnum.INICIADO.descricao());
	}
	
	public Integer getQuantidadeRealizado() {
		return quantidadeRealizado;
	}
	
	public void setQuantidadeRealizado(Integer quantidadeRealizado) {
		this.quantidadeRealizado = quantidadeRealizado;
	}
	
	public Integer getQuantidadeAuditado() {
		return quantidadeAuditado;
	}

	public void setQuantidadeAuditado(Integer quantidadeAuditado) {
		this.quantidadeAuditado = quantidadeAuditado;
	}

	@Override
	public boolean isUrgenciaOuAtendimentoSubsequente() {
		return false;
	}

	@Override
	public boolean isHonorarioMedico() {
		return false;
	}

	@Override
	public boolean isAcompanhamentoAnestesico() {
		return false;
	}

	@Override
	protected GuiaSimples<ProcedimentoInterface> newInstance() {
		return new GuiaTratamentoSeriado<ProcedimentoInterface>();
	}
}