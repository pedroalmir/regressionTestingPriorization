package br.com.infowaypi.ecarebc.service.internacao;

import static br.com.infowaypi.msr.utils.Assert.isNotEmpty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemGuia;
import br.com.infowaypi.ecarebc.atendimentos.enums.ValidateGuiaEnum;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.ecarebc.utils.CommandCorrecaoCalculoValorProcedimento;
import br.com.infowaypi.ecarebc.utils.TocarObjetoUtils;
import br.com.infowaypi.ecarebc.utils.TocarObjetoUtils.TocarObjetoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Service para a solicitação de prorrogação de internações no plano de saúde.
 * @author root
 */
public class ProrrogarInternacaoService<S extends SeguradoInterface> extends Service {
	
	public static final Boolean AUTORIZA = true;
	public static final Boolean NA0_AUTORIZA = false;
	
	public ProrrogarInternacaoService(){
		super();
	}
	
	public ResumoGuias<GuiaCompleta> buscarGuiasAProrrogar(S segurado, String dataInicial, String dataFinal, Prestador prestador) throws Exception{
		List<GuiaCompleta> guias = super.buscarGuias(dataInicial, dataFinal,segurado, prestador,false, GuiaCompleta.class, SituacaoEnum.ABERTO, SituacaoEnum.PRORROGADO);
		isNotEmpty(guias, "Não existem guias desse segurado para serem prorrogadas!");
		return new ResumoGuias<GuiaCompleta>(guias, ResumoGuias.SITUACAO_TODAS, false);
	}
	
	public ResumoGuias<GuiaCompleta> buscarGuiasAProrrogar(Collection<S> segurados, Prestador prestador) throws Exception{
		SearchAgent sa = getSearchSituacoes(SituacaoEnum.ABERTO,SituacaoEnum.PRORROGADO,SituacaoEnum.AUTORIZADO);
		ArrayList<String> tiposDeGuia = new ArrayList<String>();
		tiposDeGuia.add("IEL");
		tiposDeGuia.add("IUR");
		sa.addParameter(new In("tipoDeGuia",tiposDeGuia));
		List<GuiaCompleta> guias = super.buscarGuias(sa,segurados, prestador,false, GuiaCompleta.class);
		isNotEmpty(guias, "Não existem guias desse segurado para serem prorrogadas!");
		return new ResumoGuias<GuiaCompleta>(guias, ResumoGuias.SITUACAO_TODAS, false);
	} 
	
	public GuiaCompleta buscarGuiasAProrrogar(String autorizacao, Prestador prestador) throws Exception{
		if (Utils.isStringVazia(autorizacao))
			throw new ValidateException(MensagemErroEnum.NUMERO_AUTORIZACAO_INVALIDO.getMessage());
		
		GuiaCompleta guia = super.buscarGuias(autorizacao,prestador,false, GuiaInternacao.class,SituacaoEnum.ABERTO, SituacaoEnum.PRORROGADO,SituacaoEnum.AUTORIZADO, SituacaoEnum.NAO_PRORROGADO, SituacaoEnum.SOLICITADO_PRORROGACAO);

		if(guia.isSituacaoAtual(SituacaoEnum.SOLICITADO_PRORROGACAO.descricao()))
			throw new RuntimeException(MensagemErroEnum.GUIA_SOLICITACAO_PENDENTE.getMessage());
		
		Assert.isNotNull(guia, "Guia não encontrada!");
		Assert.isFalse(guia.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao()), "A guia "+autorizacao+" possui situação AUTORIZADO(A) e deve ser confirmada antes da solicitação de prorrogação.");
		
		this.tocarObjeto(guia);
			
		return guia;
	}
	
	public void tocarObjeto(GuiaCompleta<ProcedimentoInterface> guia){
		TocarObjetoUtils.tocarObjeto(guia, TocarObjetoEnum.CID_GUIA, TocarObjetoEnum.OBSERVACOES_GUIA,
											TocarObjetoEnum.ITENS_PACOTE_GUIA, TocarObjetoEnum.ITENS_GASOTERAPIA_GUIA,
											TocarObjetoEnum.ITENS_TAXA_GUIA, TocarObjetoEnum.PROCEDIMENTOS_GUIA);
		
		guia.getSegurado().tocarObjetos();
		guia.getPrestador().tocarObjetos();
		guia.getColecaoSituacoes().getSituacoes().size();
		
		for (SituacaoInterface situacao : guia.getColecaoSituacoes().getSituacoes()) {
			situacao.getDataSituacao();
		}
		
		for (ItemDiaria itemdiaria : guia.getItensDiaria()) {
			for (SituacaoInterface situacao : itemdiaria.getColecaoSituacoes().getSituacoes()) {
				situacao.getDataSituacao();
			}
		}
	}
	
	public GuiaCompleta prorrogarInternacao(GuiaCompleta guia, UsuarioInterface usuario) throws Exception{
		this.tocarObjeto(guia);
		
		guia.mudarSituacao(usuario, SituacaoEnum.SOLICITADO_PRORROGACAO.descricao(), MotivoEnum.SOLICITACAO_PRORROGACAO.getMessage(), new Date());
		
		return guia;
	}
	
	public GuiaCompleta prorrogarInternacao(Collection<ItemDiaria> diarias, String motivoProrrogacao, GuiaCompleta guia, UsuarioInterface usuario) throws Exception{
		BigDecimal valorParcial = guia.getValorTotal();
		guia.setValorParcial(valorParcial);
		
		if (!guia.isInternacao())
			throw new RuntimeException("Só é permitido solicitar prorrogação para guias de internação.");
		
		if(Utils.isStringVazia(motivoProrrogacao)){
			throw new ValidateException("Preencha o campo Motivo da Prorrogação.");
		}
		
		if(diarias.isEmpty())
			throw new ValidateException("Informe o Tipo de Acomodação com a Quantidade de Diárias.");
		else if (diarias.size() > 1)
			throw new ValidateException("Informe apenas 1 (uma) Acomodação.");
		
		this.tocarObjeto(guia);
		
		int prazoProrrogado = 0; 
		for (ItemDiaria diaria : diarias) {
			diaria.mudarSituacao(usuario, SituacaoEnum.SOLICITADO.descricao(), MotivoEnum.SOLICITACAO_PRORROGACAO.getMessage(), new Date());
			diaria.setQuantidadeSolicitada(diaria.getValor().getQuantidade());
			prazoProrrogado += diaria.getValor().getQuantidade();
		}
		
		//Matheus: remoção da validação de quantidade máxima de diárias 
		/*
		int qtdeMaxDiarias = 30;
		if (guia.getQuantidadeDiasAutorizadosDaInternacao()+prazoProrrogado > qtdeMaxDiarias)
			throw new ValidateException(
					"Uma internação só pode conter no máximo "+qtdeMaxDiarias
					+" diárias. Esta guia já possui "+guia.getQuantidadeDiasAutorizadosDaInternacao()
					+" dias autorizados e só pode conter mais "
					+(qtdeMaxDiarias-guia.getQuantidadeDiasAutorizadosDaInternacao())+".");
		*/
		
		if(prazoProrrogado > 5){
			throw new ValidateException("Só é possível solicitar no máximo 5 dias.");
		}
		
		for (ItemDiaria diaria : diarias) {
			diaria.setJustificativa(motivoProrrogacao);
			guia.addItemDiaria(diaria);
		}
		
		GuiaCompleta guiaProrrogada = this.prorrogarInternacao(guia, usuario);
		
		guia.addFlowValidator(ValidateGuiaEnum.CARENCIA_INTERNACAO_VALIDATOR.getValidator()).executeFlowValidators();
		
		CommandCorrecaoCalculoValorProcedimento cmd = new CommandCorrecaoCalculoValorProcedimento(guiaProrrogada);
		cmd.execute();
		
		return guiaProrrogada;
	}
	
	public ResumoGuias<GuiaCompleta> buscarGuiasProrrogadas(S segurado, String dataInicial, String dataFinal, Prestador prestador) throws Exception{
		List<GuiaCompleta> guias = super.buscarGuias(dataInicial, dataFinal,segurado, prestador,false, GuiaCompleta.class, SituacaoEnum.SOLICITADO);
		isNotEmpty(guias, "Não existem guias desse segurado para serem prorrogadas!");
		return new ResumoGuias<GuiaCompleta>(guias, ResumoGuias.SITUACAO_TODAS, false);
	}
	
	public ResumoGuias<GuiaCompleta> buscarGuiasProrrogadas(Collection<S> segurados, Prestador prestador) throws Exception{
		SearchAgent sa = getSearchSituacoes(SituacaoEnum.SOLICITADO_PRORROGACAO);
		List<GuiaCompleta> guias = super.buscarGuias(sa,segurados, prestador,false, GuiaCompleta.class);
		isNotEmpty(guias, "Não existem guias desse segurado para serem prorrogadas!");
		return new ResumoGuias<GuiaCompleta>(guias, ResumoGuias.SITUACAO_TODAS, false);
	}
	
	public GuiaCompleta buscarGuiasProrrogadas(String autorizacao, Prestador prestador) throws Exception{
		if (Utils.isStringVazia(autorizacao))
			throw new ValidateException(MensagemErroEnum.NUMERO_AUTORIZACAO_INVALIDO.getMessage());	
		GuiaCompleta guia;
		if(prestador != null)
			guia = super.buscarGuias(autorizacao,prestador,true, GuiaCompleta.class, SituacaoEnum.SOLICITADO_PRORROGACAO);
		else{
			SearchAgent sa = new SearchAgent();
			sa.addParameter(new Equals("autorizacao",autorizacao));
			List<GuiaCompleta> guias = sa.list(GuiaCompleta.class);
			Assert.isNotEmpty(guias, "Nenhuma guia encontrada com a autorização "+autorizacao+".");
			guia =  guias.get(0);
			if(!guia.isSituacaoAtual(SituacaoEnum.SOLICITADO_PRORROGACAO.descricao()))
				throw new RuntimeException("Não existe solicitação de prorrogação para a guia "+autorizacao+".");
			
		}

		Assert.isNotNull(guia, "Guia não encontrada!");
		guia.tocarObjetos();
		return guia;
		
	}

	public GuiaCompleta autorizarProrrogacao(Boolean autorizaProrrogacao, GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario) throws Exception{
		guia.tocarObjetos();
		if (autorizaProrrogacao){
			//TODO tirar o atributo prazoAutorizado na lista de parametros
			guia.mudarSituacao(usuario, SituacaoEnum.PRORROGADO.descricao(), MotivoEnum.AUTORIZACAO_PRORROGACAO_GUIA.getMessage(), new Date());
			
			for (ItemDiaria diaria : guia.getItensDiaria()) {
				if(diaria.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao()) && diaria.isAutorizado()){
					diaria.calculaDataInicial();
					diaria.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZACAO_SOLICITACAO_PRORROGACAO.getMessage(), new Date());
				}
			}
		}
		else{
			guia.mudarSituacao(usuario, SituacaoEnum.NAO_PRORROGADO.descricao(), MotivoEnum.PRORROGACAO_GUIA_NAO_AUTORIZADA.getMessage(), new Date());
			
			for (ItemDiaria diaria : guia.getItensDiaria()) {
				if(diaria.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao()) && !diaria.isAutorizado()){
					diaria.mudarSituacao(usuario, SituacaoEnum.NAO_AUTORIZADO.descricao(), MotivoEnum.NAO_AUTORIZACAO_SOLICITACAO_PRORROGACAO.getMessage(), new Date());
				}
			}
		}
		return guia;
	}
	
	public GuiaCompleta autorizarProrrogacao(Boolean autorizaProrrogacao, String motivo, GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario) throws Exception{
		guia.tocarObjetos();
		BigDecimal valorParcial = guia.getValorTotal();
		guia.setValorParcial(valorParcial);
		
		if (autorizaProrrogacao){
			guia.mudarSituacao(usuario, SituacaoEnum.PRORROGADO.descricao(), MotivoEnum.AUTORIZACAO_PRORROGACAO_GUIA.getMessage(), new Date());
			
			for (ItemDiaria diaria : guia.getItensDiaria()) {
				if(diaria.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())){
					diaria.calculaDataInicial();//calcula a dt inicial
					diaria.setJustificativaNaoAutorizacao(motivo);
					diaria.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZACAO_SOLICITACAO_PRORROGACAO.getMessage(), new Date());
				}
			}
		}
		else{
			//Assert.isNotEmpty(motivo, "Preencha o campo Motivo no caso de não autorização da prorrogação.");
			guia.mudarSituacao(usuario, SituacaoEnum.NAO_PRORROGADO.descricao(), MotivoEnum.PRORROGACAO_GUIA_NAO_AUTORIZADA.getMessage(), new Date());
			
			for (ItemDiaria diaria : guia.getItensDiaria()) 
				if(diaria.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())){
					diaria.setJustificativaNaoAutorizacao(motivo);
					diaria.mudarSituacao(usuario, SituacaoEnum.NAO_AUTORIZADO.descricao(), MotivoEnum.NAO_AUTORIZACAO_SOLICITACAO_PRORROGACAO.getMessage(), new Date());
				
			}
		}
		
		guia.recalcularValores();
		return guia;
	}
	
	public GuiaCompleta autorizarProrrogacao(GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario) throws Exception{
		guia.tocarObjetos();
		boolean autorizaProrrogacao = false;
		
		for (ItemDiaria diaria : guia.getItensDiaria()) {
			if(diaria.isAutorizado()){
				autorizaProrrogacao = true;
			}
		}
		
		return this.autorizarProrrogacao(autorizaProrrogacao, guia, usuario);
	}	
	
	public GuiaCompleta buscarGuiasComSolicitacaoDeProrrogacao(String autorizacao, UsuarioInterface usuario) throws ValidateException{
		
		if (Utils.isStringVazia(autorizacao))
			throw new ValidateException(MensagemErroEnum.NUMERO_AUTORIZACAO_INVALIDO.getMessage());
		
		GuiaCompleta guia = (GuiaCompleta) HibernateUtil.currentSession()
														.createCriteria(GuiaCompleta.class)
														.add(Expression.eq("autorizacao", autorizacao))
														.uniqueResult();
	
		Assert.isNotNull(guia, "Guia não encontrada!");
		
		if(!guia.isSituacaoAtual(SituacaoEnum.SOLICITADO_PRORROGACAO.descricao())){
				throw new RuntimeException("Não existe solicitação de prorrogação para a guia "+autorizacao+".");
		}
			
		this.tocarObjeto(guia);
		guia.setUsuarioDoFluxo(usuario);
		return guia;
	}
	
	public GuiaCompleta  selecionarGuia(GuiaCompleta guia) throws Exception {	
		return super.selecionarGuia(guia);
	}
	
	public void salvarGuia(GuiaSimples guia) throws Exception {
		super.salvarGuia(guia);
	}
	
}
