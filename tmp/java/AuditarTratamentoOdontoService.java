
package br.com.infowaypi.ecarebc.service.autorizacoes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCirurgiaOdonto;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExameOdonto;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.odonto.enums.PericiaEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOdonto;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.ecarebc.utils.CommandCorrecaoValorGuia;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Service básico para a realização de perícia final em tratamentos odontológicos
 * @author Danilo Nogueira Portela
 */
public class AuditarTratamentoOdontoService extends Service {
	
	public AuditarTratamentoOdontoService(){
		super();
	}
	
	@SuppressWarnings("unchecked")
	public <G extends GuiaSimples> ResumoGuias<G> buscarGuias(Class klass, String autorizacao, String dataInicial, 
			String dataFinal,Prestador prestador) throws Exception {
		
		Collection situacoes = null;
		if(prestador != null){
			if(prestador.isExigeEntregaLote())
				situacoes = Arrays.asList(SituacaoEnum.RECEBIDO.descricao(), SituacaoEnum.AUDITADO.descricao());
			else situacoes = Arrays.asList(SituacaoEnum.FECHADO.descricao(), SituacaoEnum.AUDITADO.descricao());
		}else situacoes = Arrays.asList(SituacaoEnum.FECHADO.descricao(), SituacaoEnum.RECEBIDO.descricao(), SituacaoEnum.AUDITADO.descricao());
		
		SearchAgent sa = new SearchAgent();
		
		sa.addParameter(new In("situacao.descricao",situacoes));
		
		List<G> guias = super.buscarGuias(sa, autorizacao, null, dataInicial, dataFinal, prestador,  false, false, klass, GuiaSimples.DATA_DE_SITUACAO);
		List<G> guiasAuditoria = new ArrayList<G>();
		
		for (G g : guias) {
			if(g.isExameOdonto())
				if(((GuiaExameOdonto)g).isRealizarPericia(PericiaEnum.FINAL))
					guiasAuditoria.add(g);
			
			if(g.isCirurgiaOdonto())
				guiasAuditoria.add(g);
		}
		
		Assert.isNotEmpty(guiasAuditoria, MensagemErroEnum.GUIAS_NAO_ENCONTRADAS_PARA_AUDITORIA.getMessage());
		
		ResumoGuias<G> resumo = new ResumoGuias<G>(guiasAuditoria, ResumoGuias.SITUACAO_FECHADA, false);
		return resumo;
	}

	protected SearchAgent buscarGuiasPorSituacao(Prestador prestador) {
		SearchAgent sa = new SearchAgent();
		Collection situacoes = Arrays.asList(SituacaoEnum.FECHADO.descricao(), SituacaoEnum.AUDITADO.descricao());
		sa.addParameter(new In("situacao.descricao",situacoes));
		return sa;
	}
	
	@SuppressWarnings("unchecked")
	public <G extends GuiaSimples> G selecionarGuia(G guia, UsuarioInterface usuario) {
		guia.tocarObjetos();
		guia.setUsuarioDoFluxo(usuario);
		
		BigDecimal valorAnterior = guia.getValorTotal();
		guia.setValorAnterior(valorAnterior);
		
		return guia;
	}

	public GuiaCirurgiaOdonto auditarGuia(Boolean glosarTotalmente, String motivoDeGlosa, GuiaCirurgiaOdonto<ProcedimentoInterface> guia, UsuarioInterface usuario) throws Exception {
		for (ProcedimentoCirurgicoInterface procedimentoCirurgico : guia.getProcedimentosCirurgicos()) {
			if(procedimentoCirurgico.getIdProcedimento() == null){
				if(procedimentoCirurgico.getProfissionalAuxiliar1() != null) {
					guia.setValorTotal(guia.getValorTotal().add(procedimentoCirurgico.getValorAuxiliar1()));
				}
				if(procedimentoCirurgico.getProfissionalAuxiliar2() != null) {
					guia.setValorTotal(guia.getValorTotal().add(procedimentoCirurgico.getValorAuxiliar2()));
				}
				if(procedimentoCirurgico.getProfissionalAuxiliar3() != null) {
					guia.setValorTotal(guia.getValorTotal().add(procedimentoCirurgico.getValorAuxiliar3()));
				}
			}
		}
		
		guia.setValorTotal(guia.getValorTotal().subtract(guia.getValoresMatMed().getValorTotalInformado()));
		guia.setValorTotal(guia.getValorTotal().add(guia.getValoresMatMed().getValorTotalAuditado()));
		
		CommandCorrecaoValorGuia command = new CommandCorrecaoValorGuia(guia);
		command.execute();
		
		if(glosarTotalmente) {
			if(!Utils.isStringVazia(motivoDeGlosa)) {
				guia.setMotivoParaGlosaTotal(motivoDeGlosa);
				guia.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), MotivoEnum.GUIA_GLOSADA_DURANTE_AUDITORIA.getMessage(), new Date());
				
				return guia;
			}
			else{
				throw new ValidateException(MensagemErroEnum.CAMPO_MOTIVO_DE_GLOSA_REQUERIDO.getMessage());
			}
		}
		
		guia.mudarSituacao(usuario, SituacaoEnum.AUDITADO.descricao() , MotivoEnum.GUIA_AUDITADA.getMessage(), new Date());
		
		return guia;
	}
	
	public GuiaExameOdonto periciarGuia(GuiaExameOdonto guia, UsuarioInterface usuario) throws Exception{
		BigDecimal valorParcial = guia.getValorTotal();
		guia.setValorAnterior(valorParcial);
		
		for (ProcedimentoOdonto p : guia.getProcedimentos()) {
			Boolean isSituacaoCancelado = p.getSituacao().getDescricao().equals(SituacaoEnum.CANCELADO.descricao());
			Boolean isSituacaoAtivo = p.getSituacao().getDescricao().equals(SituacaoEnum.ATIVO.descricao());
			
			if(!isSituacaoAtivo && !isSituacaoCancelado)
				p.mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(), MotivoEnum.PROCEDIMENTO_AUDITADO.getMessage(), new Date());
		}
		
		guia.mudarSituacao(usuario, SituacaoEnum.AUDITADO.descricao() , MotivoEnum.GUIA_AUDITADA.getMessage(), new Date());
		
		return guia;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void salvarGuia(GuiaSimples guia) throws Exception{
		String situacaoAnterior = guia.getSituacaoAnterior(guia.getSituacao()).getDescricao();
		Boolean atualizarIndice = false;
		if(situacaoAnterior.equals(SituacaoEnum.FECHADO.descricao())){
			atualizarIndice = true;
		}	
		
		super.salvarGuia(guia, atualizarIndice, atualizarIndice);
	}
}
