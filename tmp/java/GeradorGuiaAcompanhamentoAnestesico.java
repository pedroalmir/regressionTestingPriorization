package br.com.infowaypi.ecare.atendimentos;

import java.util.Collection;
import java.util.Date;

import org.hibernate.criterion.Restrictions;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaAcompanhamentoAnestesico;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.validators.ProcedimentoAptoASolicitacaoDeAcompanhamentoAnestesicoValidator;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.enums.CapituloProcedimentoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoAnestesico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.ecarebc.utils.ProcedimentoUtils;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;

public class GeradorGuiaAcompanhamentoAnestesico {
	
	private static final String CODIGO_PROCEDIMENTO_PORTE_0 = "90000004";
	private static final String CODIGO_PROCEDIMENTO_PORTE_1 = "90000001";
	private static final String CODIGO_PROCEDIMENTO_PORTE_2 = "90000002";
	private static final String CODIGO_PROCEDIMENTO_PORTE_3 = "90000003";
	
	
	private static final int PORTE_0 = 0;
	private static final int PORTE_1 = 1;
	private static final int PORTE_2 = 2;
	private static final int PORTE_3 = 3;
	
	private TabelaCBHPM procedimento_porte_0;
	private TabelaCBHPM procedimento_porte_1;
	private TabelaCBHPM procedimento_porte_2;
	private TabelaCBHPM procedimento_porte_3;
	private Long idCooperativaAnestesistas = 0L;

	public GuiaAcompanhamentoAnestesico gerarGuiaDeAcompanhamentoAnestesico(Collection<Procedimento> exames, GuiaSimples<ProcedimentoInterface> guia,
			UsuarioInterface usuario, AbstractSegurado segurado, String motivo, String situacao)throws ValidateException, Exception {
	
		ProcedimentoAptoASolicitacaoDeAcompanhamentoAnestesicoValidator.validate(exames);
		
		Prestador coopanest = ImplDAO.findById(Constantes.ID_COOPANEST,Prestador.class);
		
		if (coopanest == null){
			Long idCooperativaAnestesistas = getIdCooperativaAnestesistas();
			coopanest = ImplDAO.findById(idCooperativaAnestesistas,Prestador.class);
		}
		coopanest.tocarObjetos();
		
		GuiaAcompanhamentoAnestesico guiaAcompanhamento = criarGuiaDeAcompanhamentoAnestesicoComProcedimentosDeAcompanhamentoAnestesico(
				exames, usuario, segurado, coopanest, motivo, situacao, guia);
		
		guia.recalcularValores();
		guia.addGuiaFilha(guiaAcompanhamento);
		
		return guiaAcompanhamento;
	}
	
	@SuppressWarnings("unchecked")
	private GuiaAcompanhamentoAnestesico criarGuiaDeAcompanhamentoAnestesicoComProcedimentosDeAcompanhamentoAnestesico(Collection<Procedimento> procedimentos,
			UsuarioInterface usuario, AbstractSegurado segurado, Prestador coopanest, String motivo, String situacao, GuiaSimples guia)	throws Exception {

		GuiaAcompanhamentoAnestesico guiaAcompanhamento = new GuiaAcompanhamentoAnestesico(usuario);
		guiaAcompanhamento.setSegurado(segurado);
		guiaAcompanhamento.mudarSituacao(usuario, situacao, motivo, new Date());
		guiaAcompanhamento.setPrestador(coopanest);
		guiaAcompanhamento.setGuiaOrigem(guia);
		guiaAcompanhamento.setExameExterno(true);
		
		criarProcedimentosAnestesicos(guiaAcompanhamento, procedimentos, usuario);
		ProcedimentoUtils.ajustarAPorcentagemDosProcedimentosDeAcordoComOPorte(guiaAcompanhamento);
			
		return guiaAcompanhamento;
	}
			
	private void criarProcedimentosAnestesicos(GuiaAcompanhamentoAnestesico guia, Collection<Procedimento> procedimentos, UsuarioInterface usuario) throws Exception{
		int index =0;
		for(Procedimento proc : procedimentos){
			index++;
			String codigo = proc.getProcedimentoDaTabelaCBHPM().getCodigo();
			int porteDoProcedimento = CapituloProcedimentoEnum.getPorteAnestesicoDoCapituloDoProcedimentos(codigo);
			ProcedimentoAnestesico procedimentoAcompanhamentoAnestesico = getProcedimentoAcompanhamentoAnestesico(porteDoProcedimento, usuario);
			procedimentoAcompanhamentoAnestesico.setIndex(index);
			guia.addProcedimento(procedimentoAcompanhamentoAnestesico);
			}
		}
		
	private ProcedimentoAnestesico getProcedimentoAcompanhamentoAnestesico(int porteDoProcedimento, UsuarioInterface usuario) {

		if (porteDoProcedimento == PORTE_0) {
			return getProcedimentoDeAcompanhamentoAnestesico(CODIGO_PROCEDIMENTO_PORTE_0, usuario);
	}
	
		if (porteDoProcedimento == PORTE_1) {
			return getProcedimentoDeAcompanhamentoAnestesico(CODIGO_PROCEDIMENTO_PORTE_1, usuario);
		}
		
		if (porteDoProcedimento == PORTE_2) {
			return getProcedimentoDeAcompanhamentoAnestesico(CODIGO_PROCEDIMENTO_PORTE_2, usuario);
		}
		
		if (porteDoProcedimento == PORTE_3) {
			return getProcedimentoDeAcompanhamentoAnestesico(CODIGO_PROCEDIMENTO_PORTE_3, usuario);
		}
	
		return null;
	}
	
	private ProcedimentoAnestesico getProcedimentoDeAcompanhamentoAnestesico(String codigoProcedimento, UsuarioInterface usuario) {
		ProcedimentoAnestesico procedimento = new ProcedimentoAnestesico(usuario);

		if ( codigoProcedimento.equals(CODIGO_PROCEDIMENTO_PORTE_0)){
			if (procedimento_porte_0 == null ){
				procedimento_porte_0 = pesquisaProcedimentoNoBanco(codigoProcedimento);
				procedimento.setProcedimentoDaTabelaCBHPM(procedimento_porte_0);
			}else{
				procedimento.setProcedimentoDaTabelaCBHPM(procedimento_porte_0);
			}
		}
					
		if ( codigoProcedimento.equals(CODIGO_PROCEDIMENTO_PORTE_1)){
			if (procedimento_porte_1 == null ){
				procedimento_porte_1 = pesquisaProcedimentoNoBanco(codigoProcedimento);
				procedimento.setProcedimentoDaTabelaCBHPM(procedimento_porte_1);
			}else{
				procedimento.setProcedimentoDaTabelaCBHPM(procedimento_porte_1);
			}
		}
			
		if ( codigoProcedimento.equals(CODIGO_PROCEDIMENTO_PORTE_2)){
			if (procedimento_porte_2 == null ){
				procedimento_porte_2 = pesquisaProcedimentoNoBanco(codigoProcedimento);
				procedimento.setProcedimentoDaTabelaCBHPM(procedimento_porte_2);
			}else{
				procedimento.setProcedimentoDaTabelaCBHPM(procedimento_porte_2);
			}
		}
		
		if ( codigoProcedimento.equals(CODIGO_PROCEDIMENTO_PORTE_3)){
			if (procedimento_porte_3 == null ){
				procedimento_porte_3 =  pesquisaProcedimentoNoBanco(codigoProcedimento);
				procedimento.setProcedimentoDaTabelaCBHPM(procedimento_porte_3);
			}else{
				procedimento.setProcedimentoDaTabelaCBHPM(procedimento_porte_3);
			}
		}
			
		return procedimento;
	}
	
	private TabelaCBHPM pesquisaProcedimentoNoBanco(String codigoProcedimento) {
		return (TabelaCBHPM) HibernateUtil.currentSession().createCriteria(TabelaCBHPM.class).
																add(Restrictions.eq("codigo", codigoProcedimento)).uniqueResult();
	}

	public Long getIdCooperativaAnestesistas() {
		return idCooperativaAnestesistas;
	}

	public void setIdCooperativaAnestesistas(Long idCooperativaAnestesistas) {
		this.idCooperativaAnestesistas = idCooperativaAnestesistas;
	}
	
	
}
