package br.com.infowaypi.ecarebc.atendimentos;


import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.hibernate.LockMode;
import org.hibernate.Query;

import br.com.infowaypi.ecare.services.ProcedimentoCirurgicoLayer;
import br.com.infowaypi.ecare.services.layer.ItemDiariaLayer;
import br.com.infowaypi.ecare.services.layer.ItemPacoteLayer;
import br.com.infowaypi.ecare.services.recurso.GuiaRecursoGlosa;
import br.com.infowaypi.ecare.services.recurso.ItemRecursoGlosa;
import br.com.infowaypi.ecare.services.recurso.ItemRecursoLayer;
import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Prestador.TipoIncremento;
import br.com.infowaypi.ecarebc.atendimentos.acordos.AcordoPacote;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemGasoterapia;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemTaxa;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GuiaHonorarioMedico;
import br.com.infowaypi.ecarebc.atendimentos.honorario.HonorarioExterno;
import br.com.infowaypi.ecarebc.atendimentos.validators.fechamento.FechamentoDefaultStrategy;
import br.com.infowaypi.ecarebc.atendimentos.visitors.ItemGlosavelVisitor;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.Role;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ItemGuiaFaturamento;
import br.com.infowaypi.ecarebc.opme.ItemOpme;
import br.com.infowaypi.ecarebc.painelDeControle.PainelDeControle;
import br.com.infowaypi.ecarebc.procedimentos.CID;
import br.com.infowaypi.ecarebc.procedimentos.Pacote;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoAnamnese;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoLayer;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOutros;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOutrosLayer;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.ecarebc.promocao.PromocaoConsulta;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.CollectionUtils;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe para representar a estrutura de uma guia complexa
 * @author Erick Passos
 * @CHANGES IDELVANE
 */
@SuppressWarnings({ "unchecked", "serial"})
public abstract class GuiaCompleta<P extends ProcedimentoInterface> extends GuiaSimples<P> implements GuiaFechavel, ItemGlosavel{
	
	//Constantes
	public static final int LIMITE_DIAS_FECHAMENTO = 90;
	
	public static final int PRAZO_ATENDIMENTO_URGENCIA = 6;
	public static final int PRAZO_URGENCIA = 12;
	public static final int PRAZO_1DIA = 1;
	public static final int PRAZO_2DIAS = 2;
	public static final int PRAZO_3DIAS = 3;
	public static final int PRAZO_4DIAS = 4;
	public static final int PRAZO_5DIAS = 5;
	
	public static final int TIPO_ACOMODACAO_APTO = 1;
	public static final int TIPO_ACOMODACAO_UTI = 2;
	public static final int TIPO_ACOMODACAO_SEMI_INTENSIVA_NEONATAL = 3;

	public static final int TIPO_ACOMODACAO_EXTERNO = 0;
	public static final int TIPO_ACOMODACAO_INTERNO = 1;
	
	public static final int TRATAMENTO_CLINICO = 1;
	public static final int TRATAMENTO_CIRURGICO = 2;
	public static final int TRATAMENTO_OBSTETRICO = 3;
	
	private int 					tipoAcomodacao;
	private int 					tipoTratamento;
	private int 					numeroAnteriorProcedimentosGuia;
	/**
	 * Campo que representa a data inicial do período de um atendimento de uma parcial 
	 * em internações.
	 */
	private Date 					dataInicial;
	private Date 					dataCirurgia;
	private Date 					dataEntregaGuiaFisica;
	private boolean 				fechamentoParcial;
	private Integer 				quantidadePacotes;
	private ValoresMatMed 			valoresMatMed;
	private Set<CID> 				cids;
	private Set<ItemTaxa> 			itensTaxa;
	private Set<ItemDiaria> 		itensDiaria;
	private Set<ItemPacote> 		itensPacote;
	private Set<ItemGasoterapia> 	itensGasoterapia;
	private Set<ItemOpme> 			itensOpme;
	private List<QuadroClinico> 	quadrosClinicos;
	private Set<RegistroTecnicoDaAuditoria> registrosTecnicosDaAuditoria;
	private GuiaRecursoGlosa guiaRecursoGlosa;
	
	//Atributos Transientes
	private Set<ProcedimentoCirurgico> 				procedimentosCirurgicosDaSolicitacao 	= new HashSet<ProcedimentoCirurgico>();
	private List<ProcedimentoCirurgicoInterface> 	procedimentosCirurgicosTemp 			= new ArrayList<ProcedimentoCirurgicoInterface>();
	private Boolean 								autorizado								= Boolean.FALSE;
	private transient Set<ItemPacoteLayer>  		itensPacoteAuditoriaLayer;
	private transient Set<ItemPacote> itensPacoteTemp = new HashSet<ItemPacote>();

	private transient Set<ItemDiariaLayer> itensDiariaAuditoriaLayer;
	private transient Set<ItemDiaria> itensDiariaTemp = new HashSet<ItemDiaria>();

	private transient Especialidade especialidadeTemp;
	private transient Set<ItemTaxa> itensTaxaTemp = new HashSet<ItemTaxa>();
	private transient Set<ItemGasoterapia> itensGasoterapiaTemp = new HashSet<ItemGasoterapia>();
	
	private transient Set<ItemGasoterapiaLayer> itensGasoterapiaLayer;
	private transient Set<ItemTaxaLayer> itensTaxaLayer; 

	private transient Set<ProcedimentoCirurgicoLayer> procedimentosCirurgicosLayer = new HashSet<ProcedimentoCirurgicoLayer>();

	private transient Set<ProcedimentoInterface> procedimentoTemp = new HashSet<ProcedimentoInterface>();
	private transient Set<ProcedimentoInterface> procedimentoOutrosTemp = new HashSet<ProcedimentoInterface>();

	private transient Set<ProcedimentoLayer> procedimentoLayer;
	private transient Set<ProcedimentoOutrosLayer> procedimentoOutrosLayer;

	private transient List<ItemRecursoLayer> layersRecursoProcedimentoCirurgico;
	private transient List<ItemRecursoLayer> layersRecursoProcedimentosExame;
	private transient List<ItemRecursoLayer> layersRecursoProcedimentosOutros;
	private transient List<ItemRecursoLayer> layersRecursoItensGasoterapia;
	private transient List<ItemRecursoLayer> layersRecursoItensTaxa;
	private transient List<ItemRecursoLayer> layersRecursoItensDiaria;
	private transient List<ItemRecursoLayer> layersRecursoItensPacote;
	private transient List<ItemRecursoLayer> layerRecursoGuia;
	private ItemRecursoGlosa itemRecurso;

	private transient boolean isRecursando;
	
	private transient ManagerLayerRecurso managerLayerRecurso;
	
	private PromocaoConsulta promocaoConsulta;
	
	private transient GuiaAcompanhamentoAnestesico guiaAcompanhamentoAnestesicoDeExame;
	
	public GuiaCompleta(){
		this(null);
	}

	public GuiaCompleta(UsuarioInterface usuario){
		super(usuario);
		this.cids 					= new HashSet<CID>();
		this.quadrosClinicos 		= new ArrayList<QuadroClinico>();
		this.valoresMatMed 			= new ValoresMatMed();
		this.itensGasoterapia 		= new HashSet<ItemGasoterapia>();
		this.itensPacote 			= new HashSet<ItemPacote>();
		this.itensTaxa 				= new HashSet<ItemTaxa>();
		this.itensDiaria 			= new HashSet<ItemDiaria>();
		this.itensOpme              = new HashSet<ItemOpme>();
		this.fechamentoParcial 		= false;
		this.registrosTecnicosDaAuditoria = new HashSet<RegistroTecnicoDaAuditoria>();
	}
	
	public int getQuantidadeDiasAutorizadosDaInternacao(){
		return this.getQuantidadeDiasDaInternacao(SituacaoEnum.AUTORIZADO.descricao());
	}
	
	private int getQuantidadeDiasDaInternacao(String situacao){
		int quantidadeDias = 0;
		for (ItemDiaria itemDiaria : this.getItensDiaria()) {
			if(!SituacaoEnum.NAO_AUTORIZADO.descricao().equals(itemDiaria.getSituacao().getDescricao())){
				if (SituacaoEnum.AUTORIZADO.descricao().equals(situacao)) {
					quantidadeDias += itemDiaria.getQuantidadeAutorizada();
				}else if(SituacaoEnum.SOLICITADO.descricao().equals(situacao)){
					quantidadeDias += itemDiaria.getQuantidadeSolicitada();
				}
			}
		}
		return quantidadeDias;
	}	
	
	public Set<CID> getCids() {
		return cids;
	}

	public void setCids(Set<CID> cids) {
		this.cids = cids;
	}
	
	public void addCid(CID cid) {
		this.cids.add(cid);
	}
	
	public void removeCid(CID cid) {
		this.cids.remove(cid);
	}

	public List<QuadroClinico> getQuadrosClinicos() {
		return quadrosClinicos;
	}

	public void setQuadrosClinicos(List<QuadroClinico> quadrosClinicos) {
		this.quadrosClinicos = quadrosClinicos;
	}
	
	public void addQuadroClinico(String justificativa){
		QuadroClinico quadro = new QuadroClinico();
		quadro.setJustificativa(justificativa);
		quadro.setGuia(this);
		this.getQuadrosClinicos().add(quadro);
	}

	/**
	 * Retorna o somatório da quantidade de Diárias Solicitadas.
	 */
	public int getPrazoProrrogado() {
		int quantidadeDias = 0;
		for (ItemDiaria itemDiaria : this.getItensDiaria()) {
			if(!SituacaoEnum.NAO_AUTORIZADO.descricao().equals(itemDiaria.getSituacao().getDescricao())){
				if (itemDiaria.getSituacao().getDescricao().equals(SituacaoEnum.AUTORIZADO.descricao())) {
					quantidadeDias += itemDiaria.getQuantidadeAutorizada();
				}else if (itemDiaria.getSituacao().getDescricao().equals(SituacaoEnum.SOLICITADO.descricao())) {
					quantidadeDias += itemDiaria.getQuantidadeSolicitada();
				}
			}
		}
		return quantidadeDias;
	}	

	public void addItemOpme(ItemOpme item) {
		item.mudarSituacao(getUsuarioDoFluxo(), SituacaoEnum.SOLICITADO.descricao(), "Solicitação de Órtese, Prótese ou Material Especial.", new Date());
		this.getItensOpme().add(item);
	}
	
	public void removeItemOpme(ItemOpme item) {
		this.getItensOpme().remove(item);
	}
	
	/**
	 * 
	 * @param autorizado
	 * @return Itens opmes solicitados tenha seu atributo 'autorizado' igual ao passado como parâmetro
	 */
	private Set<ItemOpme> getItensOpmeEmAutorizacao(Boolean autorizado) {

		Set<ItemOpme> opmesEmAutorizacao = new HashSet<ItemOpme>();
		
		for (ItemOpme itemOpme : this.getOpmesSolicitados()) {
			if (itemOpme.getAutorizado() == autorizado) {
				opmesEmAutorizacao.add(itemOpme);
			}
		}
		
		return opmesEmAutorizacao;
	}
	
	/**
	 * 
	 * @return Itens opmes solicitados e que foram marcados como autorizados no fluxo de autorização 
	 */
	public Set<ItemOpme> getItensOpmeAutorizadosEmAutorizacao() {
		return this.getItensOpmeEmAutorizacao(Boolean.TRUE);
	}
	
	/**
	 * 
	 * @return Itens pacotes solicitados e que foram marcados como não autorizados no fluxo de autorização 
	 */
	public Set<ItemOpme> getItensOpmeNaoAutorizadosEmAutorizacao() {
		return this.getItensOpmeEmAutorizacao(Boolean.FALSE);
	}
	
	public List<ItemOpme> getOpmesSolicitados(){
		List<ItemOpme> itensOpme = new ArrayList<ItemOpme>();
		for (ItemOpme opme : this.getItensOpme()) {
			if(opme.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())){
				itensOpme.add(opme);
			}
		}
		return itensOpme;
	}
	
	public List<ItemOpme> getOpmesAutorizados(){
		List<ItemOpme> itensOpme = new ArrayList<ItemOpme>();
		if (this.getItensOpme() != null) {
			for (ItemOpme opme : this.getItensOpme()) {
				if (opme.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao())) {
					itensOpme.add(opme);
				}
			}
		}
		return itensOpme;
	}
	
	public void addItemDiaria(ItemDiaria item) throws Exception{
		addItemDiariaTemplate(item);
		this.getItensDiaria().add(item);
	}

	//Usado apenas no fluxo de auditar guia para adicionar diarias já como auditadas.
	public void addItemDiariaAuditoria(ItemDiaria item) throws Exception {
		addItemDiariaTemplate(item);
		item.mudarSituacao(getUsuarioDoFluxo(), SituacaoEnum.AUTORIZADO.descricao(), "Autorizado pelo auditor" , new Date());
		item.setJustificativa( MotivoEnum.INCLUIDO_PELO_AUDITOR.getMessage());

		//Setando as quantidades solicitada e autorizada
		item.setQuantidadeSolicitada(item.getValor().getQuantidade());
		item.setQuantidadeAutorizada(item.getValor().getQuantidade());
		
		this.getItensDiariaTemp().add(item);
	}
	
	private void addItemDiariaTemplate(ItemDiaria item) throws Exception {
		item.setGuia(this);
		item.recalcularCampos();
		item.calculaDataInicial();
		BigDecimal valorTotalItem = item.getValor().getValor().multiply(BigDecimal.valueOf(Double.valueOf(item.getValor().getQuantidade())));
		this.setValorTotal(valorTotalItem.add(this.getValorTotal()));
		this.setValorParcial(BigDecimal.ZERO);
		this.setValorParcial(this.getValorParcial().add(valorTotalItem));
		
		/* if_not[REMOVE_OBRIGATORIEDADE_DE_INFORMAR_PRESTADOR_PARA_INTERNACAO] */
		this.getPrestador().atualizarConsumoFinanceiro(this, 0, TipoIncremento.POSITIVO);
		/* end[REMOVE_OBRIGATORIEDADE_DE_INFORMAR_PRESTADOR_PARA_INTERNACAO] */
		
		this.getSegurado().atualizarLimites(this, TipoIncremento.POSITIVO,0);
	}
	
	public void autorizarDiaria(ItemDiaria item) throws Exception{
		item.mudarSituacao(null, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.INCLUIDO_PELO_AUDITOR.getMessage(), new Date());
		item.setJustificativa( MotivoEnum.INCLUIDO_PELO_AUDITOR.getMessage());
		this.addItemDiaria(item);
	}
	
	public void removeItemDiaria(ItemDiaria itemDiaria) throws Exception{
		boolean isCancelado = itemDiaria.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao());
		boolean isNaoAutorizado = itemDiaria.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao());
		Set<ItemDiaria> itens = new HashSet<ItemDiaria>(this.getItensDiaria());
		itens.addAll(this.getItensDiariaTemp());
		for (ItemDiaria item: itens) {
			this.getItensDiaria().add(item);
			item.setGuia(this);
			if(item.equals(itemDiaria) && !isCancelado && !isNaoAutorizado){
				item.tocarObjetos();
				item.mudarSituacao(null,SituacaoEnum.NAO_AUTORIZADO.descricao(),MotivoEnum.NAO_AUTORIZADO_AUDITOR.getMessage(), new Date());
				BigDecimal valorTotalItem = item.getValor().getValor().multiply(BigDecimal.valueOf(Double.valueOf(item.getValor().getQuantidade())));
				this.setValorTotal(this.getValorTotal().subtract(valorTotalItem));
				this.setValorParcial(BigDecimal.ZERO);
				this.setValorParcial(this.getValorParcial().add(valorTotalItem));
				this.getPrestador().atualizarConsumoFinanceiro(this, 0, TipoIncremento.NEGATIVO);
				this.getSegurado().atualizarLimites(this, TipoIncremento.NEGATIVO,0);
			}
		}
	}
	
	public List<ItemDiaria> getDiariasSolicitadas(){
		List<ItemDiaria> itensDiarias = new ArrayList<ItemDiaria>();
		for (ItemDiaria diaria : this.itensDiaria) {
			if(diaria.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())){
				itensDiarias.add(diaria);
			}
		}
		return itensDiarias;
	}
	
	public List<ItemDiaria> getDiariasAutorizadas(){
		List<ItemDiaria> itensDiarias = new ArrayList<ItemDiaria>();
		for (ItemDiaria diaria : this.itensDiaria) {
			if(diaria.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao())){
				itensDiarias.add(diaria);
			}
		}
		return itensDiarias;
	}
	
	public int getQuantidadeDeDiariasAutorizadas(){
		List<ItemDiaria> itensDiarias = getDiariasAutorizadas();
		int numeroDeDiarias = 0;
		for (ItemDiaria diaria : itensDiarias) {
			numeroDeDiarias += diaria.getValor().getQuantidade();
		}
		return numeroDeDiarias;
	}

	public void addItemTaxa(ItemTaxa item) throws Exception{
		addItemTaxaTemplate(item);
		this.getItensTaxa().add(item);
	}

	public void addItemTaxaAuditoria(ItemTaxa item) throws Exception {
		addItemTaxaTemplate(item);
		this.getItensTaxaTemp().add(item);
	}
	
	private void addItemTaxaTemplate(ItemTaxa item) throws Exception {
		item.setGuia(this);
		item.recalcularCampos();
		BigDecimal valorTotalItem = item.getValor().getValor().multiply(BigDecimal.valueOf(Double.valueOf(item.getValor().getQuantidade())));
		this.setValorTotal(valorTotalItem.add(this.getValorTotal()));
		this.setValorParcial(BigDecimal.ZERO);
		this.setValorParcial(this.getValorParcial().add(valorTotalItem));
		this.getPrestador().atualizarConsumoFinanceiro(this, 0, TipoIncremento.POSITIVO);
		this.getSegurado().atualizarLimites(this, TipoIncremento.POSITIVO,0);
	}
	
	public void removeItemTaxa(ItemTaxa itemTaxa) throws Exception{
		boolean isCancelado = itemTaxa.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao());
		boolean isNaoAutorizado = itemTaxa.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao());
		Set<ItemTaxa> itens = new HashSet<ItemTaxa>(this.getItensTaxa());
		itens.addAll(this.getItensTaxaTemp());
		for (ItemTaxa item: itens) {
			if(item.equals(itemTaxa) && !isCancelado && !isNaoAutorizado){
				item.tocarObjetos();
				item.mudarSituacao(null,SituacaoEnum.NAO_AUTORIZADO.descricao(),MotivoEnum.NAO_AUTORIZADO_AUDITOR.getMessage(), new Date());
				BigDecimal valorTotalItem = item.getValor().getValor().multiply(BigDecimal.valueOf(Double.valueOf(item.getValor().getQuantidade())));
				this.setValorTotal(this.getValorTotal().subtract(valorTotalItem));
				this.setValorParcial(BigDecimal.ZERO);
				this.setValorParcial(this.getValorParcial().add(valorTotalItem));
				this.getPrestador().atualizarConsumoFinanceiro(this, 0, TipoIncremento.NEGATIVO);
				this.getSegurado().atualizarLimites(this, TipoIncremento.NEGATIVO,0);
			}
		}
	}
	
	public void addItemGasoterapia(ItemGasoterapia item) throws Exception{
		addItemGasoterapiaTemplate(item);
		this.getItensGasoterapia().add(item);
	}

	public void addItemGasoterapiaAuditoria(ItemGasoterapia item) throws Exception{
		addItemGasoterapiaTemplate(item);
		this.getItensGasoterapiaTemp().add(item);
	}
	
	private void addItemGasoterapiaTemplate(ItemGasoterapia item)
			throws Exception {
		item.setGuia(this);
		item.recalcularCampos();
		BigDecimal valorTotalItem = item.getValor().getValor().multiply(item.getQuantidadeEmHoras()).setScale(2, BigDecimal.ROUND_HALF_UP);
		this.setValorTotal(valorTotalItem.add(this.getValorTotal()));
		this.setValorParcial(BigDecimal.ZERO);
		this.setValorParcial(this.getValorParcial().add(valorTotalItem));
		this.getPrestador().atualizarConsumoFinanceiro(this, 0, TipoIncremento.POSITIVO);
		this.getSegurado().atualizarLimites(this, TipoIncremento.POSITIVO,0);
	}
	
	public void removeItemGasoterapia(ItemGasoterapia itemGasoterapia) throws Exception{
		boolean isCancelado = itemGasoterapia.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao());
		boolean isNaoAutorizado = itemGasoterapia.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao());
		Set<ItemGasoterapia> itens = new HashSet<ItemGasoterapia>(this.getItensGasoterapia());
		itens.addAll(this.getItensGasoterapiaTemp());
		for (ItemGasoterapia item: itens) {
			if(item.equals(itemGasoterapia) && !isCancelado && !isNaoAutorizado){
				item.tocarObjetos();
				BigDecimal valorTotalItem = item.getValor().getValor().multiply(item.getQuantidadeEmHoras());
				this.setValorTotal(this.getValorTotal().subtract(valorTotalItem));
				this.setValorParcial(BigDecimal.ZERO);
				this.setValorParcial(this.getValorParcial().add(valorTotalItem));
				this.getPrestador().atualizarConsumoFinanceiro(this, 0, TipoIncremento.NEGATIVO);
				this.getSegurado().atualizarLimites(this, TipoIncremento.NEGATIVO,0);
				item.mudarSituacao(null,SituacaoEnum.NAO_AUTORIZADO.descricao(),MotivoEnum.NAO_AUTORIZADO_AUDITOR.getMessage(), new Date());
			}
		}
	}
	
	public List<ItemOpme> getItensOpmeJaAutorizados() {

		if (this.getItensOpme() != null && this.getItensOpme().size() > 0) {
			List<String> opmes = new ArrayList<String>();
			for (ItemOpme item : this.getItensOpme()) {
				opmes.add(item.getOpme().getDescricao());
			}
			String query = "Select item from ItemOpme item WHERE "
					+ "item.opme.descricao in (:opmes) AND "
					+ "item.situacao.descricao = :situacao "
					+ "order by item.situacao.dataSituacao";
			Query hql = HibernateUtil.currentSession().createQuery(query)
					.setParameterList("opmes", opmes)
					.setString("situacao", SituacaoEnum.AUTORIZADO.descricao())
					.setMaxResults(30);
			return hql.list();
		}
		else {
			return new ArrayList<ItemOpme>();
		}
	}
	
	public List<ItemOpme> getItensOpmeAnamnese() {

		String query = "Select item from ItemOpme item WHERE " +
				"(item.situacao.descricao = :situacaoA OR " +
				"item.situacao.descricao = :situacaoB) AND " +
				"item.guia.segurado = :segurado " +
				"order by item.situacao.dataSituacao";
		
		Query hql = HibernateUtil.currentSession().createQuery(query)
				.setString("situacaoA", SituacaoEnum.AUTORIZADO.descricao())
				.setString("situacaoB", SituacaoEnum.NAO_AUTORIZADO.descricao())
				.setEntity("segurado", this.getSegurado())
				.setMaxResults(30);
				
		return hql.list();
	}
	
	/**
	 * Adiciona um item pacote na guia fazendo a validação de se o prestador possui acordo
	 * e mudando a situação do item para solicitado.
	 * @param item
	 * @param usuario
	 * @throws Exception
	 * @see {@link GuiaSimples#addProcedimento(ProcedimentoInterface)}
	 */
	public void addItemPacoteComValidacao(ItemPacote item) throws Exception{
		boolean isPrestadorPossuiAcordoPacote;
		item.tocarObjetos();
		isPrestadorPossuiAcordoPacote = false;
		for (AcordoPacote acordo : this.getPrestador().getAcordosPacoteAtivos()) {
			if(acordo.getPacote().getDescricao().equals(item.getPacote().getDescricao())){
				isPrestadorPossuiAcordoPacote = true;
				if (getUsuarioDoFluxo() != null  && (getUsuarioDoFluxo().isPossuiRole(Role.ROOT.getValor()) || getUsuarioDoFluxo().isPossuiRole(Role.AUDITOR.getValor()) || getUsuarioDoFluxo().isPossuiRole(Role.DIRETORIA_MEDICA.getValor()))){
					item.mudarSituacao(getUsuarioDoFluxo(),	SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZADO_PELO_ROLE_INFORMADO.getMessage(getUsuarioDoFluxo().getRole()), new Date());
				}else{
					item.mudarSituacao(getUsuarioDoFluxo(), SituacaoEnum.SOLICITADO.descricao(), MotivoEnum.SOLICITADO_PELO_PRESTADOR.getMessage(), new Date());
				}
				this.addItemPacote(item);
			}
		}
		if(!isPrestadorPossuiAcordoPacote){
			throw new RuntimeException(MensagemErroEnum.PRESTADOR_SEM_ACORDO_ATIVO_PARA_O_PACOTE.getMessage(item.getPacote().getDescricao()));	
		}
	}
	
	/**
	 * adiciona um item na guia sem nenhuma validação e sem alterar situação.
	 * @param item
	 * @throws Exception
	 */
	public void addItemPacote(ItemPacote item) throws Exception{
		addItemPacoteTemplate(item);
		this.getItensPacote().add(item);
	}

	public void addItemPacoteAuditoria(ItemPacote item) throws Exception{
		addItemPacoteTemplate(item);
		this.getItensPacoteTemp().add(item);
	}

	private void addItemPacoteTemplate(ItemPacote item) throws Exception {
		item.setGuia(this);
		item.recalcularCampos();
		this.setValorTotal(this.getValorTotal().add(item.getValor().getValor()));
		this.setValorParcial(BigDecimal.ZERO);
		this.setValorParcial(this.getValorParcial().add(item.getValor().getValor()));
		this.getPrestador().atualizarConsumoFinanceiro(this, 0, TipoIncremento.POSITIVO);
		this.getSegurado().atualizarLimites(this, TipoIncremento.POSITIVO,0);
		item.mudarSituacao(null, SituacaoEnum.AUTORIZADO.descricao(), "" , new Date());
	}

	public List<ProcedimentoAnamnese> getProcedimentosAnamnese() {
		
		Set<TabelaCBHPM> cbhpm = new HashSet<TabelaCBHPM>();
		for (P procedimento : this.getProcedimentos()) {
			cbhpm.add(procedimento.getProcedimentoDaTabelaCBHPM());
		}
		
		
		String query = "select procedimento from " +
				"Procedimento procedimento where " +
				"procedimento.procedimentoDaTabelaCBHPM.nivel <> :nivelCbhpm and " +
				"procedimento.guia.situacao.descricao <> :situacaoA and " +
				"procedimento.guia.situacao.descricao <> :situacaoB and " +
				"procedimento.guia.tipoDeGuia <> :tipoGuiaA and " +
				"procedimento.guia.tipoDeGuia <> :tipoGuiaB and " +
				"procedimento.guia.tipoDeGuia <> :tipoGuiaC and " +
				"procedimento.guia.segurado = :segurado order by procedimento.situacao.dataSituacao DESC";
		
		Query hql = HibernateUtil.currentSession().createQuery(query)
				.setInteger("nivelCbhpm", 1)
				.setString("situacaoA", SituacaoEnum.CANCELADO.descricao())
				.setString("situacaoB", SituacaoEnum.GLOSADO.descricao())
				.setString("tipoGuiaA", "CIROD")
				.setString("tipoGuiaB", "GEXOD")
				.setString("tipoGuiaC", "GCSOD")
				.setEntity("segurado", this.getSegurado())
				.setMaxResults(30);
				
		List<ProcedimentoAnamnese> resultList = new ArrayList<ProcedimentoAnamnese>();
		for (Procedimento p : (List<Procedimento>) hql.list()) {
			if ((p.getSituacao(SituacaoEnum.AUTORIZADO.descricao()) != null ||
				p.getSituacao(SituacaoEnum.NAO_AUTORIZADO.descricao()) != null ) &&
				!p.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())) {
					resultList.add(new ProcedimentoAnamnese(p));
			}
		}

		return resultList;
	}
	
	public void addItemPacoteAlteracao(ItemPacote item) throws Exception{
		addItemPacote(item);
		item.mudarSituacao(null, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.INCLUSAO_ITEM_PACOTE.getMessage() , new Date());
	}

	public void removeItemPacote(ItemPacote itemPacote) throws Exception{
		boolean isCancelado 	= itemPacote.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao());
		boolean isNaoAutorizado = itemPacote.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao());
		Set<ItemPacote> itens = new HashSet<ItemPacote>(this.getItensPacote());
		itens.addAll(this.getItensPacoteTemp());
		for (ItemPacote item: itens) {
			if(item.equals(itemPacote) && !isCancelado && !isNaoAutorizado){
				item.tocarObjetos();
				mudarSituacaoPacote(itemPacote, null, SituacaoEnum.NAO_AUTORIZADO.descricao(), MotivoEnum.NAO_AUTORIZADO_AUDITOR.getMessage(), new Date());
			}
		}
	}

	public void cancelarItemPacote(ItemPacote itemPacote) throws Exception{
		String situacao 		= SituacaoEnum.CANCELADO.descricao();
		boolean isCancelado 	= itemPacote.isSituacaoAtual(situacao);
		boolean isNaoAutorizado = itemPacote.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao());
		
		for (ItemPacote item: this.getItensPacote()) {
			if(item.equals(itemPacote) && !isCancelado && !isNaoAutorizado){
				UsuarioInterface usuario = this.getUsuarioDoFluxo();
				String motivo 			 = MotivoEnum.CANCELADO_AUDITOR.getMessage();
				Date data 				 = new Date();
				mudarSituacaoPacote(itemPacote, usuario, situacao, motivo, data);
			}
		}
	}

	private void mudarSituacaoPacote(ItemPacote itemPacote, UsuarioInterface usuario, String situacao, String motivo, Date data) throws Exception {
		this.setValorTotal(this.getValorTotal().subtract(itemPacote.getValor().getValor()));
		this.setValorParcial(BigDecimal.ZERO);
		this.setValorParcial(this.getValorParcial().add(itemPacote.getValor().getValor()));
		this.getPrestador().atualizarConsumoFinanceiro(this, 0, TipoIncremento.NEGATIVO);
		this.getSegurado().atualizarLimites(this, TipoIncremento.NEGATIVO,0);
		itemPacote.mudarSituacao(usuario ,situacao, motivo, data);
	}
	
	
	public void removeItemPacoteAlteracao(ItemPacote itemPacote, UsuarioInterface usuario) throws Exception {
		String situacao = SituacaoEnum.CANCELADO.descricao();
		String motivo = MotivoEnum.EXCLUSAO_ITEM_PACOTE.getMessage();
		Date data = new Date();

		mudarSituacaoPacote(itemPacote, usuario, situacao, motivo, data);
	}
	
	/**
	 * Retorna o somatório da quantidade de Diárias Autorizadas.
	 */
	public int getPrazoAutorizado() {
		return this.getQuantidadeDiasDaInternacao(SituacaoEnum.AUTORIZADO.descricao());
	}
	
	/**
	 * Retorna o somatório da quantidade de Diárias Solicitadas.
	 */
	public int getPrazoSolicitado() {
		return this.getQuantidadeDiasDaInternacao(SituacaoEnum.SOLICITADO.descricao());
	}
	
	@Deprecated
	public int getTipoAcomodacao() {
		return tipoAcomodacao;
	}
	
	@Deprecated
	public String getTipoAcomodacaoDescricao() {
		if (this.tipoAcomodacao == TIPO_ACOMODACAO_UTI)
			return "UTI";
		else if (this.tipoAcomodacao == TIPO_ACOMODACAO_APTO)
			return "Apartamento";
		else if (this.tipoAcomodacao == TIPO_ACOMODACAO_SEMI_INTENSIVA_NEONATAL)
			return "Semi-intensiva Neonatal";
		else return "";
	}
	
	@Deprecated
	public void setTipoTratamento(int tipoTratamento) {
		this.tipoTratamento = tipoTratamento;
	}
	
	public int getTipoTratamento() {
		return tipoTratamento;
	}
	
	public String getTipoTratamentoFormatado() {
		if(tipoTratamento == TRATAMENTO_CIRURGICO) {
			return "Cirúrgica";
		}else if (tipoTratamento == TRATAMENTO_CLINICO) {
			return "Clínica";
		}else {
			return "Obstétrica";
		}
	}
	
	public void setTipoAcomodacao(int tipoAcomodacao) {
		this.tipoAcomodacao = tipoAcomodacao;
	}
	
	public Set<ProcedimentoCirurgico> getProcedimentosCirurgicos(){
		Set<ProcedimentoCirurgico> procedimentos = new HashSet<ProcedimentoCirurgico>();
		for(ProcedimentoInterface procedimento : this.getProcedimentosNaoCanceladosENegados()){ 
			if (procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO) {
				procedimentos.add((ProcedimentoCirurgico) procedimento);
			}
			procedimento.setPassouPelaAutorizacao(true);
		}
		return procedimentos;
	}
	
	public Set<ProcedimentoCirurgico> getProcedimentosCirurgicosComHonorariosNaoGerados() {
		Set<ProcedimentoCirurgico> procedimentos = new HashSet<ProcedimentoCirurgico>();
		for (ProcedimentoCirurgico procedimento : this.getProcedimentosCirurgicos()) {
			if (!procedimento.isSituacaoAtual(SituacaoEnum.PROCEDIMENTO_COM_HONORARIO_GERADO.descricao())) {  
				procedimentos.add(procedimento);
			}
		}
		return procedimentos;
	}
	
	public List<ProcedimentoCirurgico> getProcedimentosCirurgicosOrdenado(){
		return ordenarProcedimentosPorSituacaoEDescricao(getProcedimentosCirurgicos());
	}
	
	public Set<ProcedimentoCirurgico> getProcedimentosCirurgicosDosPacotes(){
		Set<ProcedimentoCirurgico> procedimentos = new HashSet<ProcedimentoCirurgico>();
		for (ItemPacote pacote : this.itensPacote) {
			for (TabelaCBHPM procedimento : pacote.getPacote().getProcedimentosCBHPM()) {
				if(procedimento.getGrupo() == 3) {
					ProcedimentoCirurgico procedimentoCir = new ProcedimentoCirurgico();
					procedimentoCir.setProcedimentoDaTabelaCBHPM(procedimento);
					procedimentos.add(procedimentoCir);
				}
			}
		}
		return procedimentos;
	}
	
	public Set<ProcedimentoOutros> getProcedimentosOutros(){
		Set<ProcedimentoOutros> procedimentos = new HashSet<ProcedimentoOutros>();
		
		for(ProcedimentoInterface procedimento : this.getProcedimentosNaoCanceladosENegados()){
			if (procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_OUTROS){
				procedimentos.add((ProcedimentoOutros)procedimento);
			}
		}
		return procedimentos;
	}

	public List<ProcedimentoOutros> getProcedimentosOutrosOrdenado(){
		return ordenarProcedimentosPorSituacaoEDescricao(this.getProcedimentosOutros());
	}

	public ProcedimentoInterface getProcedimentoConsultaNaoCancelado(){
		for (ProcedimentoInterface procedimento : getProcedimentosSimples()) {
			if(ManagerGuia.isProcedimentoDeConsulta(procedimento) 
					&& !procedimento.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()) 
					&& !procedimento.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao())){
				
				return procedimento;
			}
		}
		return null;
	}
	
	public Set<ProcedimentoInterface> getProcedimentosSimplesNaoConsulta(){
		Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();
		for(ProcedimentoInterface procedimento : this.getProcedimentosSimples()){
			if (!ManagerGuia.isProcedimentoDeConsulta(procedimento) ){
				procedimentos.add(procedimento);
			}
		}			
		return procedimentos;
	}
	
	public Set<ProcedimentoInterface> getProcedimentosSimples(){
		Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();
		for(ProcedimentoInterface procedimento : this.getProcedimentosNaoCanceladosENegados()){
			if (!(procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO)
					&& !(procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_OUTROS))
				procedimentos.add(procedimento);
			}
		return procedimentos;
	}
	
	public List<ProcedimentoInterface> getProcedimentosSimplesOrdenado(){
		return ordenarProcedimentosPorSituacaoEDescricao(getProcedimentosSimples());
	}
	
	public List<ProcedimentoInterface> getProcedimentosAutorizadosENaoAutorizados() {
		Set<ProcedimentoInterface> procedimentos = (Set<ProcedimentoInterface>) getProcedimentos();
		Set<ProcedimentoInterface> procedimentosOrdenados = new HashSet<ProcedimentoInterface>();
		for (ProcedimentoInterface p : procedimentos) {
			if (!ManagerGuia.isProcedimentoDeConsulta(p)
					&& p.getTipoProcedimento() != ProcedimentoInterface.PROCEDIMENTO_CIRURGICO
					&& p.getTipoProcedimento() != ProcedimentoInterface.PROCEDIMENTO_OUTROS) {
				if (!p.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()) 
						&& !p.isSituacaoAtual(SituacaoEnum.NEGADO.descricao())) {
					procedimentosOrdenados.add(p);
				}
			}
		}
		return ordenarProcedimentosPorSituacaoEDescricao(procedimentosOrdenados);
	}
	
	/**
	 * Agrupa os procedimentos por CBHPM e por Situação.
	 * Ex: 
	 * <li> 10 hemogramas sendo 3 glosados, 2 solicitados e 5 Autorizados, iram aparecer como sendo apenas tres procedimentos:
	 *  um glosado com a quantidade 3, um solicitado com  a quantidade 2 e um autorizado com a quantidade 5.</li>
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public List<ProcedimentoInterface> getProcedimentosSimplesAgrupadoEOrdenado() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		Map<TabelaCBHPM, Set<ProcedimentoInterface>> mapProcedimentosAgrupadosPorCBHPM = CollectionUtils.groupBy(getProcedimentosAutorizadosENaoAutorizados(), "procedimentoDaTabelaCBHPM", TabelaCBHPM.class);
		List<ProcedimentoInterface> procedimentosAgrupados = new ArrayList<ProcedimentoInterface>();
		
		//Agrupa por CBHPM
		for(TabelaCBHPM tabela : mapProcedimentosAgrupadosPorCBHPM.keySet()){
			Set<ProcedimentoInterface> procsTabela = mapProcedimentosAgrupadosPorCBHPM.get(tabela);
			Map<String, Set<ProcedimentoInterface>> mapProcedimentosAgrupadosPorSituacao = CollectionUtils.groupBy(procsTabela, "situacao.descricao", String.class);

			//Agrupo por situação
			for(String situacao : mapProcedimentosAgrupadosPorSituacao.keySet()){
				Set<ProcedimentoInterface> procsSituacao = mapProcedimentosAgrupadosPorSituacao.get(situacao);
				ProcedimentoInterface procedimento = (ProcedimentoInterface) procsSituacao.iterator().next().clone();
				setarQuantidadeDoProcedimento(procedimento, procsSituacao);
				procedimentosAgrupados.add(procedimento);
			}
		}
		return procedimentosAgrupados;
	}
	
	private void setarQuantidadeDoProcedimento(ProcedimentoInterface procedimento, Set<ProcedimentoInterface> procedimentos){
		Integer qtd = 0;
		procedimento.setQuantidade(qtd);
		for(ProcedimentoInterface p : procedimentos){
			qtd = qtd + p.getQuantidade();
		}
		procedimento.setQuantidade(qtd);
	}
	
	public Set<ProcedimentoInterface> getProcedimentosSimplesNaoCancelados(){
		Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();
		for (ProcedimentoInterface procedimento : getProcedimentosSimples()) {
			if(!ManagerGuia.isProcedimentoDeConsulta(procedimento) 
					&& !procedimento.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()) 
					&& !procedimento.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao()))
				procedimentos.add(procedimento);
		}
		return procedimentos;
	}
	
	public List<ProcedimentoInterface> getProcedimentosSimplesNaoCanceladosOrdenado(){
		return ordenarProcedimentosPorDescricao(this.getProcedimentosSimplesNaoCancelados());
	}
	
	public Set<ProcedimentoInterface> getProcedimentosCirurgicosNaoCancelados(){
		Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();
		for (ProcedimentoInterface procedimento : getProcedimentosCirurgicos()) {
			if(!procedimento.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()) && !procedimento.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao()))
				procedimentos.add(procedimento);
		}
		return procedimentos;
	}
	
	public Set<ProcedimentoInterface> getProcedimentosOutrosNaoCancelados(){
		Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();
		for (ProcedimentoInterface procedimento : getProcedimentosOutros()) {
			if(!procedimento.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()) && !procedimento.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao()))
				procedimentos.add(procedimento);
		}
		return procedimentos;
	}
	
	public List<ProcedimentoInterface> getProcedimentosOutrosNaoCanceladosOrdenado(){;
		return ordenarProcedimentosPorDescricao(getProcedimentosOutrosNaoCancelados());
	}
	
	public Set<ProcedimentoCirurgicoInterface> getProcedimentosCirurgicosSolicitados(){
		Set<ProcedimentoCirurgicoInterface> procedimentos = new HashSet<ProcedimentoCirurgicoInterface>();
		for(ProcedimentoCirurgicoInterface procedimento : this.getProcedimentosCirurgicos())
			if (procedimento.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao()))
				procedimentos.add(procedimento);
		return procedimentos;
	}
	
	public List<ProcedimentoCirurgicoInterface> getProcedimentosCirurgicosAutorizados() {
		List<ProcedimentoCirurgicoInterface> resultado = new ArrayList<ProcedimentoCirurgicoInterface>();
		for (ProcedimentoInterface procedimento : getProcedimentos()) {
			if(procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO 
				&& procedimento.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao()))
				resultado.add((ProcedimentoCirurgico) procedimento);
		}
		return resultado;
	}
	
	public Set<ProcedimentoCirurgicoInterface> getProcedimentosCirurgicosNaoCanceladosENegados(){
		Set<ProcedimentoCirurgicoInterface> procedimentos = new HashSet<ProcedimentoCirurgicoInterface>();
		for(ProcedimentoInterface procedimento : getProcedimentosNaoCanceladosENegados()){
			if (procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO){
				procedimentos.add((ProcedimentoCirurgicoInterface)procedimento);
			}
		}
		return procedimentos;
	}
	
	public Set<ProcedimentoInterface> getProcedimentosCirurgicosNaoGlosadosNemCanceladosNemNegados(){
		Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();
		for(ProcedimentoInterface procedimento : getProcedimentosNaoGlosadosNemCanceladosNemNegados()){
			if (procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO){
				procedimentos.add(procedimento);
			}
		}
		return procedimentos;
	}
	
	public List<ProcedimentoInterface> getProcedimentosCirurgicosNaoGlosadosNemCanceladosNemNegadosOrdenado(){
		return ordenarProcedimentosPorDescricao(this.getProcedimentosCirurgicosNaoGlosadosNemCanceladosNemNegados());
	}
	
	public Set<ProcedimentoInterface> getProcedimentosCirurgicosNaoCanceladosENegadosSemNenhumHonorarioExterno(){
		Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();
		for(ProcedimentoInterface procedimento : getProcedimentosCirurgicosNaoCanceladosENegados()){
			boolean gerouAlgumHonorarioExterno = !procedimento.getHonorariosExternosNaoCanceladosEGlosados().isEmpty();
			if (!gerouAlgumHonorarioExterno){
				procedimentos.add(procedimento);
			}
		}
		return procedimentos;
	}
	
	/**
	 * Método que retorna procedimentos que não estejam nas seguintes situações:
	 * <ul>
	 * 	<li>Realizado(a)</li>
	 * 	<li>Não Autorizado(a)</li>
	 * 	<li>Solicitado(a)</li>
	 * 	<li>Glosado(a)</li>
	 * 	<li>Cancelado(a)</li>
	 * 	<li>Negado(a)</li>
	 * </ul>
	 * @return
	 */
	public Set<ProcedimentoCirurgico> getProcedimentosCirurgicosNaoCanceladosENegadosNaoNaoAutorizadosENaoSolicitados(){
		Set<ProcedimentoCirurgico> procedimentos = new HashSet<ProcedimentoCirurgico>();
		
		for(ProcedimentoInterface procedimento : getProcedimentosNaoRealizadosNaoNaoAutorizadosENaoSolicitados()){
			if (procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO){
				procedimentos.add((ProcedimentoCirurgico)procedimento);
			}
		}
		return procedimentos;
	}
	
	public Set<ItemPacote> getItensPacoteTemp() {
		return itensPacoteTemp;
	}

	public void setItensPacoteTemp(Set<ItemPacote> itensPacoteTemp) {
		this.itensPacoteTemp = itensPacoteTemp;
	}

	public Set<ItemDiaria> getItensDiariaTemp() {
		return itensDiariaTemp;
	}

	public void setItensDiariaTemp(Set<ItemDiaria> itensDiariaTemp) {
		this.itensDiariaTemp = itensDiariaTemp;
	}

	public Set<ItemPacoteLayer> getItensAuditoriaLayer() {
		return this.itensPacoteAuditoriaLayer;
	}
		
	public Set<ItemDiariaLayer> getItensDiariaAuditoriaLayer() {
		return this.itensDiariaAuditoriaLayer;
	}
	
	public Set<ProcedimentoCirurgico> getProcedimentosCirurgicosDaSolicitacao() {
		return procedimentosCirurgicosDaSolicitacao;
	}
	

	/**
	 * Verifica a existência de um profissional e data de realização antes de chamar o addProcedimentoDaSolicitacao
	 * Chamado no jhm SolicitarProcedimentos.jhm.xml
	 * @see this.addProcedimentoDaSolicitacao(ProcedimentoCirurgico procedimento);
	 */
	public void addProcedimentoDaSolicitacaoVerificandoProfissionalEDataRealizacao(ProcedimentoCirurgico procedimento) throws Exception {
		
		Assert.isNotNull(procedimento.getProfissionalResponsavel(), 
				MensagemErroEnum.PROFISSIONAL_NAO_INFORMADO.getMessage());
		
		Date dataRealizacao = procedimento.getDataRealizacao();
		String codigo = procedimento.getProcedimentoDaTabelaCBHPM().getCodigo();
		
		Assert.isNotNull(dataRealizacao, MensagemErroEnum.DATA_REALIZACAO_REQUERIDA
				.getMessage(codigo));
		
		addProcedimentoDaSolicitacao(procedimento);
	}
	
	/**
	 * Método que seta a porcentagem dos procedimentos cirúrgicos em guias de internação.
	 * Não permite 3 procedimentos iguais com mesma porcentagem em uma mesma guia.
	 * @param procedimento cirúrgico a ser inserido na guia
	 * @throws Exception
	 */
	public void addProcedimentoDaSolicitacao(ProcedimentoCirurgico procedimento) throws Exception {
		
		validacaoParaSituacaoSolicitado(procedimento);
		if (procedimento.getProfissionalResponsavel() != null) {
			procedimento.getProfissionalResponsavel().tocarObjetos();
		}
		
		int count = 0;
		
		Set<ProcedimentoCirurgicoInterface> procedimentosCirurgicos = new HashSet<ProcedimentoCirurgicoInterface>();
		procedimentosCirurgicos.addAll(this.getProcedimentosCirurgicosDaSolicitacao());
		procedimentosCirurgicos.addAll((Collection<ProcedimentoCirurgicoInterface>) this.getProcedimentosCirurgicosNaoCanceladosENegados());
		
		for (ProcedimentoInterface proc : procedimentosCirurgicos) {
			if (!proc.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao())) {
			if (proc.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO) {
				boolean isMesmoProcedimento = proc.getProcedimentoDaTabelaCBHPM().equals(procedimento.getProcedimentoDaTabelaCBHPM());
				
				Date dataRealizacao = ((ProcedimentoCirurgico) procedimento).getDataRealizacao();
				Date dtRealizacaoProcDaGuia = ((ProcedimentoCirurgico) proc).getDataRealizacao();

				boolean isDatasNulas = dataRealizacao == null && dtRealizacaoProcDaGuia == null;
				if (isMesmoProcedimento && isDatasNulas) {
					throw new ValidateException("O procedimento já se encontra na guia a 100%.");
				}
				
				boolean isMesmaData = false;
				if (dataRealizacao != null && dtRealizacaoProcDaGuia != null) {
					isMesmaData = (Utils.compareData(dataRealizacao, dtRealizacaoProcDaGuia) == 0);
				}
				if (isMesmaData && isMesmoProcedimento) {
					count++;
				}
			}
		}
		}
		
		if (count == 0) {
			procedimento.setPorcentagem(ProcedimentoCirurgico.PORCENTAGEM_100);
		} else if (count == 1) {
			procedimento.setPorcentagem(ProcedimentoCirurgico.PORCENTAGEM_70);
		} else if (count == 2) {
			procedimento.setPorcentagem(ProcedimentoCirurgico.PORCENTAGEM_50);
		} else if (count != 0) {
			throw new Exception("Procedimento "+ procedimento.getProcedimentoDaTabelaCBHPM().getCodigo()+" já se encontra na lista com as porcentagens 100, 70 e 50!");
		}
		
		procedimento.tocarObjetos();
		procedimentosCirurgicosDaSolicitacao.add(procedimento);
		this.addProcedimento((P) procedimento);
	}

	/**
	 * Verifica se a data de realização do procedimento é menor do que a data atual, caso sim, lança uma exceção.
	 * @param procedimento
	 * @throws ValidateException
	 */
	private void validacaoParaSituacaoSolicitado (ProcedimentoCirurgico procedimento) throws ValidateException {
		if (this.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao()) || this.isSituacaoAtual(SituacaoEnum.SOLICITADO_INTERNACAO.descricao())) {
			//Matheus: Ajuste de funcionalidade, onde os procedimentos de internacao eletiva devem permitir datas retroativas
			if(!this.isInternacaoEletiva()){
				if (procedimento.getDataRealizacao() != null && Utils.compareData(new Date(), procedimento.getDataRealizacao()) > 0) {
					throw new ValidateException(MensagemErroEnum.DATA_PROCEDIMENTO_MAIOR_QUE_DATA_AUTORIZACAO.getMessage());
				}
			}
		}
	}

	public void removeProcedimentoDaSolicitacao(ProcedimentoCirurgico procedimento){
		procedimentosCirurgicosDaSolicitacao.remove(procedimento);
		this.getProcedimentos().remove(procedimento);
	}
		
	
	public boolean containsProcedimentosCirurgicosIguaisEComMesmaPorcentagem(ProcedimentoCirurgicoInterface procedimento){
		boolean isMesmoProcedimento = false;
		boolean isMesmaPorcentagem = false;
		for (ProcedimentoCirurgicoInterface proc : getProcedimentosCirurgicosDaSolicitacao()) {
			if (proc.getIdProcedimento().equals(procedimento.getIdProcedimento()))
				continue;
			
			isMesmoProcedimento = proc.getProcedimentoDaTabelaCBHPM().equals(procedimento.getProcedimentoDaTabelaCBHPM());
			isMesmaPorcentagem = proc.getPorcentagem().equals(procedimento.getPorcentagem());
			if(isMesmoProcedimento && isMesmaPorcentagem)
				return true;
		}
		return false;
	}

	/**
	 * Fornece um conjunto de ProcedimentosOutros na situação Autorizado(a)
	 * @return
	 */
	public Set<ProcedimentoOutros> getProcedimentosOutrosAutorizados(){
		return getProcedimentosOutrosNaSituacao(SituacaoEnum.AUTORIZADO.descricao());
	}
	
	/**
	 * Fornece um conjunto de ProcedimentosOutros na situação Solicitado(a)
	 * @return
	 */
	public Set<ProcedimentoOutros> getProcedimentosOutrosSolicitados(){
		return getProcedimentosOutrosNaSituacao(SituacaoEnum.SOLICITADO.descricao());
	}
	
	/**
	 * Fornece um conjunto de ProcedimentosOutros na situação informada
	 * @param situacao
	 * @return
	 */
	private Set<ProcedimentoOutros> getProcedimentosOutrosNaSituacao(String situacao){
		Set<ProcedimentoOutros> procedimentos = new HashSet<ProcedimentoOutros>();
		for(ProcedimentoOutros procedimento : this.getProcedimentosOutros()){
			if(procedimento.isSituacaoAtual(situacao))
				procedimentos.add(procedimento);
		}
		return procedimentos;
	}
	
	public abstract int getPrazoInicial();

	@Override
	public boolean isCompleta() {
		return true;
	}
	

	public Set<ItemDiaria> getItensDiaria() {
		return itensDiaria;
	}
	
	/**
	 * Fornece os itens diária ordenados por data de solicitação, em um mapa
	 * cujo índice é um inteiro que representa a ordem desse item diária na
	 * coleção.
	 * @return
	 */
	public Map<Integer, ItemDiaria> getItensDiariaSorted(){
		List<ItemDiaria> sorted = new ArrayList<ItemDiaria>(itensDiaria);
		
		Collections.sort(sorted, new Comparator<ItemDiaria>() {
			@Override
			public int compare(ItemDiaria arg0, ItemDiaria arg1) {
				if(arg0.getDataInicial() == null || arg1.getDataInicial() == null){
					return arg0.getSituacao().getDataSituacao().compareTo(arg1.getSituacao().getDataSituacao());
				}else {
					return arg0.getDataInicial().compareTo(arg1.getDataInicial());
				}
			}
		});
		
		Map<Integer, ItemDiaria> mapGuias = new TreeMap<Integer, ItemDiaria>();
		int indice = 0;
		for (ItemDiaria itemDiaria : sorted) {
			indice = sorted.indexOf(itemDiaria) + 1;
			mapGuias.put(indice, itemDiaria);
		}
		
		return mapGuias;
	}

	public Set<ItemDiaria> getItensDiaria(String... situacoes) {
		Collection situacoesList = Arrays.asList(situacoes);
		Set<ItemDiaria> resultado = new HashSet<ItemDiaria>();
		for (ItemDiaria diaria : itensDiaria) {
			if(situacoesList.contains(diaria.getSituacao().getDescricao()))
				resultado.add(diaria);
		}
		return resultado;
	}
	
	public Set<ItemDiaria> getItensDiariaSolicitados() {
		return getItensDiaria(SituacaoEnum.SOLICITADO.descricao());
	}
	
	public Set<ItemDiaria> getItensDiariaAutorizados() {
		return getItensDiaria(SituacaoEnum.AUTORIZADO.descricao());
	}

	public Set<ItemDiaria> getItensDiariaSolicitadosEAutorizados() {
		return getItensDiaria(SituacaoEnum.SOLICITADO.descricao(),SituacaoEnum.AUTORIZADO.descricao());
	}
	
	/**
	 * Fornece o último item diária solicitado ou autorizado da guia.
	 * @return
	 */
	public ItemDiaria getUltimoItemDiaria(){
		Set<ItemDiaria> autorizados = getItensDiaria(SituacaoEnum.AUTORIZADO.descricao());
		Set<ItemDiaria> solicitados = new HashSet<ItemDiaria>();
		
		List<ItemDiaria> itens = new ArrayList<ItemDiaria>();
		itens.addAll(autorizados);
		
		if (!this.getItensDiariaSolicitados().isEmpty()) {
			Iterator<ItemDiaria> it = this.getItensDiariaSolicitados().iterator();
			while (it.hasNext()) {
				ItemDiaria next = it.next();
				if (next.getDataInicial()!= null)
					solicitados.add(next);
			}
			itens.addAll(solicitados);
		}
		
		Utils.sort(itens, "dataInicial");
		//retorna o ultimo item diaria q tenha uma dtInicial setada, esteja ele solicitado ou autorizado
		return itens.get(itens.size()-1);
	}

	public void setItensDiaria(Set<ItemDiaria> itensDiaria) {
		this.itensDiaria = itensDiaria;
	}

	public Set<ItemGasoterapia> getItensGasoterapia() {
		return itensGasoterapia;
	}

	public void setItensGasoterapia(Set<ItemGasoterapia> itensGasoterapia) {
		this.itensGasoterapia = itensGasoterapia;
	}

	public Set<ItemTaxa> getItensTaxa() {
		return itensTaxa;
	}

	public void setItensTaxa(Set<ItemTaxa> itensTaxa) {
		this.itensTaxa = itensTaxa;
	}
	
	public Set<ItemPacote> getItensPacote() {
		return itensPacote;
	}
	
	/**
	 * Método que retorna os Pacotes que compõem os itens pacotes da guia.
	 * @return
	 */
	public Set<Pacote> getPacotes(){
		Set<Pacote> pacotesContidosNosItensPacotesDaGuia = new HashSet<Pacote>();
		for (ItemPacote item : itensPacote) {
			pacotesContidosNosItensPacotesDaGuia.add(item.getPacote());
		}
		return pacotesContidosNosItensPacotesDaGuia;
	}
	
	public Set<ItemPacote> getItensPacoteNaoCanceladosENegados() {
		Set<ItemPacote> itens = new HashSet<ItemPacote>();
		for (ItemPacote pacote : itensPacote) {
			if(!pacote.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()) && 
					!pacote.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao())){
					itens.add(pacote);
				}
		}
		return itens;
	}
	
	public void setItensPacote(Set<ItemPacote> itensPacote) {
		this.itensPacote = itensPacote;
	}

	public boolean getFechamentoParcial() {
		return fechamentoParcial;
	}

	public void setFechamentoParcial(boolean fechamentoParcial) {
		this.fechamentoParcial = fechamentoParcial;
	}

	public void addItensDiaria(Collection<? extends ItemDiaria> itens) throws Exception{
		for (ItemDiaria diaria : itens) {
			this.addItemDiaria(diaria);
		}
	}
	
	public void addItensTaxa(Collection<? extends ItemTaxa> itens) throws Exception{
		for (ItemTaxa taxa : itens) {
			this.addItemTaxa(taxa);
		}
	}
	
	public void addItensGasoterapia(Collection<? extends ItemGasoterapia> itens) throws Exception{
		for (ItemGasoterapia gasoterapia: itens) {
			this.addItemGasoterapia(gasoterapia);
		}
	}
	
	public void addItensPacote(Collection<? extends ItemPacote> itens) throws Exception{
		for (ItemPacote pacote : itens) {
			this.addItemPacote(pacote);
		}
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public ValoresMatMed getValoresMatMed() {
		return valoresMatMed;
	}

	public void setValoresMatMed(ValoresMatMed valoresMatMed) {
		this.valoresMatMed = valoresMatMed;
	}

	public int getNumeroAnteriorProcedimentosGuia() {
		return numeroAnteriorProcedimentosGuia;
	}

	public void setNumeroAnteriorProcedimentosGuia(
			int numeroAnteriorProcedimentosGuia) {
		this.numeroAnteriorProcedimentosGuia = numeroAnteriorProcedimentosGuia;
	}

	@Override
	public void negarProcedimento(P procedimento) throws Exception {
		super.negarProcedimento(procedimento);
		this.setValorParcial(BigDecimal.ZERO);
		this.setValorParcial(this.getValorParcial().add(procedimento.getValorTotal()));
		this.getPrestador().atualizarConsumoFinanceiro(this, procedimento.getQuantidade(), TipoIncremento.NEGATIVO);
		@SuppressWarnings("unused")
		int nivel = procedimento.getProcedimentoDaTabelaCBHPM().getNivel();
		this.getSegurado().atualizarLimites(this, TipoIncremento.NEGATIVO, procedimento.getQuantidade());
	}
	
	@Override
	public void addAllProcedimentos(Collection<? extends ProcedimentoInterface> procedimentos) throws Exception {
		super.addAllProcedimentos(procedimentos);
		this.setValorParcial(BigDecimal.ZERO);
		for(ProcedimentoInterface proc : procedimentos)
			this.setValorParcial(this.getValorParcial().add(proc.getValorTotal()));
		
		if(this.getIdGuia() != null){
			this.getPrestador().atualizarConsumoFinanceiro(this, this.getQuantidadeProcedimentosValidosParaConsumo(), TipoIncremento.POSITIVO);
			this.getSegurado().atualizarLimites(this, TipoIncremento.POSITIVO, this.getQuantidadeProcedimentosValidosParaConsumo());
		}
	}

	@Override
	public void cancelarProcedimento(P procedimento) throws Exception {
		super.cancelarProcedimento(procedimento);
	}
	
	/**
	 * Matheus:
	 * Método refatorado, permitindo a inserção de procedimentos com datas retroativas. 
	 * 
	 * @param procedimento
	 * @throws Exception
	 */
	@Override
	public void addProcedimento(P procedimento) throws Exception {
		addProcedimentoNaGuia(procedimento, true);
	}
	
	/**
	 * Método para adição de procedimentos durante o fechamento de uma guia
	 * 
	 * @author Matheus
	 * @param procedimento
	 * @throws Exception
	 */
	public void addProcedimentoDuranteFechamentoDeGuia(P procedimento) throws Exception {
		addProcedimentoNaGuia(procedimento, false);
	}
	
	/**
	 * Método criado para possibilitar adicionar procedimentos sem validar datas retroativas. Refatoração ocorreu de forma a manter a compatibilidade com o código legado.
	 * 
	 * @author Matheus
	 * @param procedimento
	 * @param validaDataRetroativa
	 * @throws Exception
	 */
	public void addProcedimentoNaGuia(P procedimento, boolean validaDataRetroativa) throws Exception {
				
		validacaoGeralProcedimento(procedimento, validaDataRetroativa);
		
		corrigeValorProcedimentoInclusoEmPacoteQueInclueHonorario(procedimento);
		
		super.addProcedimentoNaGuia(procedimento, validaDataRetroativa);
		
		if(this.getIdGuia() != null){
			if (this.getPrestador()!=null) {
				this.getPrestador().atualizarConsumoFinanceiro(this, procedimento.getQuantidade(), TipoIncremento.POSITIVO);
			}
			this.getSegurado().atualizarLimites(this, TipoIncremento.POSITIVO,procedimento.getQuantidade());
		}
	}
	
	/**
	 * Verifica se a data de realização do procedimento é menor do que a data de atendimento ou maior do que a data de termino de atendimento, caso sim, lança uma exceção.
	 * @param procedimento
	 * @throws ValidateException
	 */
	public void validacaoDataDeRealizacaoDoProcedimentoCirurgicoNaAuditoria (ProcedimentoCirurgico procedimento) throws ValidateException{
		Assert.isNotNull(procedimento.getDataRealizacao(), MensagemErroEnum.DATA_CIRURGIA_REQUERIDA.getMessage(procedimento.getProcedimentoDaTabelaCBHPM().getCodigoEDescricao()));
		
		if ( this.isSituacaoAtual(SituacaoEnum.RECEBIDO.descricao()) 
				|| this.isSituacaoAtual(SituacaoEnum.FECHADO.descricao())
				|| this.isSituacaoAtual(SituacaoEnum.AUDITADO.descricao()))
			if ( (Utils.compareData(procedimento.getDataRealizacao(), this.getDataAtendimento()) < 0) 
					||  (Utils.compareData(procedimento.getDataRealizacao(), this.getDataTerminoAtendimento()) > 0))
				throw new ValidateException(MensagemErroEnum.DATA_PROCEDIMENTO_DEVE_ESTAR_ENTRE_INICIO_E_TERMINO_DO_ATENDIMENTO
								.getMessage(procedimento.getProcedimentoDaTabelaCBHPM().getDescricao(),
										Utils.format(this.getDataAtendimento()),
										Utils.format(this.getDataTerminoAtendimento())));
	}
	
	
	/**
	 * Corrige os valores dos procedimentos inclusos em pacotes da guia, e recalcula seus valores.
	 */
	public void corrigeValorProcedimentoInclusoEmPacoteQueInclueHonorario(Collection<? extends ProcedimentoInterface> procedimentos){
		for (ProcedimentoInterface procedimento : procedimentos) {
			corrigeValorProcedimentoInclusoEmPacoteQueInclueHonorario(procedimento);
		}
		this.recalcularValores();
	}
	
	
	/**
	 * Verifica se um procedimento está incluso em algum pacote não cancelado ou negado da guia.
	 * Se o prestador possuir acordo-pacote que inclui honorário, o procedimento tem seu valor zerado.
	 * Caso contrário, o procedimento tem seus valores corrigidos.
	 * LEMBRAR DE RECALCULAR OS VALORES DA GUIA.
	 * @param procedimento
	 */
	public void corrigeValorProcedimentoInclusoEmPacoteQueInclueHonorario(ProcedimentoInterface procedimento){
		boolean isInclusoEmPacoteQueInclueHonorario = isProcedimentoInclusoEmPacoteQueIncluiHonorario(procedimento);
		if (isInclusoEmPacoteQueInclueHonorario){
			procedimento.setValorAtualDoProcedimento(MoneyCalculation.rounded(BigDecimal.ZERO));
		}else if(MoneyCalculation.compare(procedimento.getValorAtualDoProcedimento(), BigDecimal.ZERO) == 0){
			procedimento.calcularCampos();
			procedimento.aplicaValorAcordo();
		}
	}

	public boolean isProcedimentoInclusoEmPacoteQueIncluiHonorario(ProcedimentoInterface procedimento){
		for (ItemPacote itemPacote : getItensPacoteNaoCanceladosENegados()) {
			boolean isProcedimentoContidoNoPacote = itemPacote.getPacote().getProcedimentosCBHPM().contains(procedimento.getProcedimentoDaTabelaCBHPM());
			if(isProcedimentoContidoNoPacote){
				AcordoPacote acordoPacote = getPrestador().getAcordoPacote(itemPacote.getPacote());
				if(acordoPacote != null && acordoPacote.getIncluiHonorario()){
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public void removeProcedimento(P procedimento, UsuarioInterface usuario) throws Exception {
		super.removeProcedimento(procedimento, usuario);
		this.setValorParcial(BigDecimal.ZERO);
		this.setValorParcial(this.getValorParcial().add(procedimento.getValorTotal()));
	}
	
	@Override
	public void tocarObjetos(){
		super.tocarObjetos();
		tocarItensDiaria();
		tocarItensGasoterapia();
		tocarItensTaxa();
		tocarItensPacote();
		tocarCids();
		tocarObservacoes();
		tocarCriticas();
		this.getGuiasFilhasDeHonorarioMedico().size();
		
		for (GuiaHonorarioMedico ghm : this.getGuiasFilhasDeHonorarioMedico()) {
			ghm.getHonorarios().size();
		}
		
		for (RegistroTecnicoDaAuditoria registro : registrosTecnicosDaAuditoria) {
			registro.tocarObjetos();
		}

		if (this.getItensOpme() != null) {
			this.tocarOpmes();
		}

		this.getQuadrosClinicos().size();
		this.getItensGuiaFaturamento().size();
	}

	public void tocarOpmes(){
		this.getItensOpme().size();
		for (ItemOpme item : this.getItensOpme()) {
			item.tocarObjetos();
		}
	}
	
	protected void tocarCriticas(){
		this.getCriticas().size();
	}
	
	protected void tocarObservacoes() {
		for (AbstractObservacao obs : this.getObservacoes()) {
			obs.tocarObjetos();
		}
	}

	protected void tocarCids() {
		for (CID cid : this.getCids()) {
			cid.tocarObjetos();
		}
	}

	protected void tocarItensPacote() {
		for (ItemPacote item : itensPacote) {
			item.tocarObjetos();
		}
	}

	protected void tocarItensTaxa() {
		for (ItemTaxa item : itensTaxa) {
			item.tocarObjetos();
		}
	}

	public Set<ItemOpme> getItensOpme() {
		return itensOpme;
	}

	public void setItensOpme(Set<ItemOpme> itensOpme) {
		this.itensOpme = itensOpme;
	}

	protected void tocarItensGasoterapia() {
		for (ItemGasoterapia item : itensGasoterapia) {
			item.tocarObjetos();
		}
	}

	protected void tocarItensDiaria() {
		for (ItemDiaria item : itensDiaria) {
			item.tocarObjetos();
		}
	}
	
	public Set<GuiaSimples> getGuiasExameExterno(){
		Set<GuiaSimples> guiasExame = new HashSet<GuiaSimples>();
		for (GuiaSimples<Procedimento> guia : getGuiasFilhas()) {
			if (guia.isExame()) {
				guiasExame.add(guia);
			}
		}
		return guiasExame;
	}
	
	public Set<GuiaSimples> getGuiasDeAcompanhamentoAnestesico(){
		Set<GuiaSimples> guiasFilhasDeAcompanhamento = new HashSet<GuiaSimples>();
		for (GuiaSimples guia : super.getGuiasFilhas()) {
			if (guia.isAcompanhamentoAnestesico()){
				guiasFilhasDeAcompanhamento.add(guia);
			}
				
		}		
		return guiasFilhasDeAcompanhamento;
	}
	
	/**
	 * Fornece dinamicamente os honorários gerados na guia.
	 * Substituído pelo método {@link GuiaSimples#getHonorarios()}
	 */
	@Deprecated
	public List<ValoresProfissional> getValoresProfissionais() {
		
		List<ValoresProfissional> valores = super.getValoresProfissionais();
		
		for (ProcedimentoInterface procedimento : getProcedimentosNaoCanceladosENegados()) {
			if(procedimento.getProfissionalAuxiliar1() != null)  {
				ValoresProfissional valorProfissional = new ValoresProfissional();
				valorProfissional.setProcedimento(procedimento);
				valorProfissional.setFuncao(ValoresProfissional.PRIMEIRO_AUXILIAR);
				valorProfissional.setValor(procedimento.getValorAuxiliar1());
				valorProfissional.setProfissional(procedimento.getProfissionalAuxiliar1());
				valores.add(valorProfissional);
			}
			if(procedimento.getProfissionalAuxiliar2() != null)  {
				ValoresProfissional valorProfissional = new ValoresProfissional();
				valorProfissional.setProcedimento(procedimento);
				valorProfissional.setFuncao(ValoresProfissional.SEGUNDO_AUXILIAR);
				valorProfissional.setValor(procedimento.getValorAuxiliar2());
				valorProfissional.setProfissional(procedimento.getProfissionalAuxiliar2());
				valores.add(valorProfissional);
			}
			if(procedimento.getProfissionalAuxiliar3() != null)  {
				ValoresProfissional valorProfissional = new ValoresProfissional();
				valorProfissional.setProcedimento(procedimento);
				valorProfissional.setFuncao(ValoresProfissional.TERCEIRO_AUXILIAR);
				valorProfissional.setValor(procedimento.getValorAuxiliar3());
				valorProfissional.setProfissional(procedimento.getProfissionalAuxiliar3());
				valores.add(valorProfissional);
			}
			
		}
		return valores;
	}
	
	public List<ValoresProfissional> getHonorariosCalculadosNaGuia() {
		
		List<ValoresProfissional> valores = new ArrayList<ValoresProfissional>();
		
		for (ProcedimentoCirurgicoInterface procedimento : getProcedimentosCirurgicos()) {
			if(procedimento.getProfissionalAuxiliar1() != null)  {
				ValoresProfissional valorProfissional = new ValoresProfissional();
				valorProfissional.setProcedimento(procedimento);
				valorProfissional.setFuncao(ValoresProfissional.PRIMEIRO_AUXILIAR);
				valorProfissional.setValor(procedimento.getValorAuxiliar1());
				valorProfissional.setProfissional(procedimento.getProfissionalAuxiliar1());
				valores.add(valorProfissional);
			}
			if(procedimento.getProfissionalAuxiliar2() != null)  {
				ValoresProfissional valorProfissional = new ValoresProfissional();
				valorProfissional.setProcedimento(procedimento);
				valorProfissional.setFuncao(ValoresProfissional.SEGUNDO_AUXILIAR);
				valorProfissional.setValor(procedimento.getValorAuxiliar2());
				valorProfissional.setProfissional(procedimento.getProfissionalAuxiliar2());
				valores.add(valorProfissional);
			}
			if(procedimento.getProfissionalAuxiliar3() != null)  {
				ValoresProfissional valorProfissional = new ValoresProfissional();
				valorProfissional.setProcedimento(procedimento);
				valorProfissional.setFuncao(ValoresProfissional.TERCEIRO_AUXILIAR);
				valorProfissional.setValor(procedimento.getValorAuxiliar3());
				valorProfissional.setProfissional(procedimento.getProfissionalAuxiliar3());
				valores.add(valorProfissional);
			}
			
		}
		return valores;
	}

	
	public String getNomeUsuarioQueLiberou() {
		for (SituacaoInterface situacao : this.getSituacoes()) {
			if(situacao.getDescricao().equals(SituacaoEnum.AUTORIZADO.descricao())) {
				return situacao.getUsuario().getNome();
			}
		}
		
		return " ";
	}
	
	
	public String getDescricaoTipoTratamento(){
		if(TRATAMENTO_CIRURGICO == this.tipoTratamento)
			return "Cirúrgico";
		else if(TRATAMENTO_CLINICO == this.tipoTratamento)
			return "Clínico";
		else
			return "Obstétrico";
	}
	
	public BigDecimal getTotalHonorariosCalculadosNaGuia(){
		BigDecimal total = MoneyCalculation.rounded(BigDecimal.ZERO);
		for (ValoresProfissional honorario : getHonorariosCalculadosNaGuia()) {
			total = total.add(honorario.getValor());
		}
		return total;
	}
	
	public BigDecimal getTotalPacotes(){
		Float total = 0.00f;
		for (ItemPacote item : getItensPacoteNaoCanceladosENegados()) {
			total += item.getValorTotal();
		}
		return MoneyCalculation.rounded(new BigDecimal(total));
	}
	
	
	public int getQuantidadeTaxas(){
		int quant = 0;
		for (ItemTaxa itemTaxa : getItensTaxa()) {
			quant += itemTaxa.getValor().getQuantidade();
		}
		return quant;
	}
	
	public BigDecimal getTotalTaxas(){
		float total = 0.00f;
		for (ItemTaxa itemTaxa : getItensTaxa()) {
			total += itemTaxa.getValorTotal();
		}
		return MoneyCalculation.rounded(new BigDecimal(total));
	}
	
	public BigDecimal getQuantidadeGasoterapias(){
		BigDecimal quantidade = MoneyCalculation.rounded(BigDecimal.ZERO);
		for (ItemGasoterapia itemGasoterapia : getItensGasoterapia()) {
			quantidade = quantidade.add(itemGasoterapia.getQuantidadeEmHoras());
		}
		return quantidade;
	}
	
	public BigDecimal getTotalGasoterapias(){
		float total = 0.00f;
		for (ItemGasoterapia itemGasoterapia : getItensGasoterapia()) {
			total += itemGasoterapia.getValorTotal();
		}
		return MoneyCalculation.rounded(new BigDecimal(total));
	}
	
	public int getQuantidadeDiariasSolicitadasEAutorizadas(){
		int quantidade = 0;
		for (ItemDiaria item : getItensDiariaSolicitadosEAutorizados()) {
			quantidade += item.getValor().getQuantidade();
		}
		return quantidade;
	}
	
	public BigDecimal getTotalDiariasSolicitadasEAutorizadas(){
		float total = 0.00f;
		for (ItemDiaria item : getItensDiariaSolicitadosEAutorizados()) {
			total += item.getValorTotal();
		}
		return MoneyCalculation.rounded(new BigDecimal(total));
	}
	
	/**
	 * Informa se a guia permite a inserção de valores de materiais complementares.
	 * Se a guia possuir algum procedimento que permita materiais complementares, ela
	 * própria permitirá materiais complementares.</br>
	 * Essa verificação também deve ser usada para validar o uso de OPMEs.
	 * @return boolean
	 */
	public boolean isPermiteMaterialComplementar() {
		for (ProcedimentoInterface procedimento : getProcedimentosNaoCanceladosENegados()) {
			if (procedimento.getProcedimentoDaTabelaCBHPM().isPermiteMaterialComplementar())
				return true;
		}
		return false;
	}
	
	public Date getDataCirurgia() {
		return dataCirurgia;
	}

	public void setDataCirurgia(Date dataCirurgia) {
		this.dataCirurgia = dataCirurgia;
	}
	
	public Date getDataEntregaGuiaFisica() {
		return dataEntregaGuiaFisica;
	}

	public void setDataEntregaGuiaFisica(Date dataEntregaGuiaFisica) {
		this.dataEntregaGuiaFisica = dataEntregaGuiaFisica;
	}
	
	public void setQuantidadePacotes(Integer quantidadePacotes) {
		this.quantidadePacotes = quantidadePacotes;
	}
	
	public Integer getQuantidadePacotes() {
		return quantidadePacotes;
	}
	
	public List<ProcedimentoCirurgicoInterface> getProcedimentosCirurgicosTemp() {
		return procedimentosCirurgicosTemp;
	}
	
	/**
	 * Método responsável pelo fechamento da guia.
	 * É sobrescrito em <code> GuiaInternacao.java </code>
	 * 
	 * @param parcial
	 * @param observacao
	 * @param dataFinal
	 * @param usuario
	 * @throws ValidateException
	 */
	public void fechar(Boolean parcial, Boolean ignorarValidacao, String observacao, Date dataFinal, UsuarioInterface usuario) throws ValidateException {

		if (this.isAtendimentoUrgencia() || this.isConsultaUrgencia()){
			this.setDataTerminoAtendimento(this.getDataAtendimento());
		}
		if (!ignorarValidacao){
			validaFechamento(parcial, dataFinal, usuario);
		}
		if (observacao != null){
			Observacao obs = new Observacao(observacao,usuario);
			this.addObservacao(obs);
		}
		
		this.corrigeValorProcedimentoInclusoEmPacoteQueInclueHonorario(this.getProcedimentosNaoCanceladosENegados());
		this.mudarSituacao(usuario, SituacaoEnum.FECHADO.descricao(), MotivoEnum.FECHAMENTO_GUIA.getMessage(), new Date());
		this.updateValorCoparticipacao();
	}

	/**
	 * Verifica se a guia pode ser fechada. 
	 * É sobrescrito em <code> GuiaInternacao.java </code>
	 * 
	 * @param parcial
	 * @param dataFinal
	 * @param usuario
	 * @throws ValidateException
	 */
	protected void validaFechamento(Boolean parcial, Date dataFinal, UsuarioInterface usuario) throws ValidateException {
		FechamentoDefaultStrategy.validaFechamento(this, parcial, dataFinal, usuario);
	}
	
	/**
	 * Informa se a guia já contém algum procedimento cirúrgico autorizado a 100%.
	 * @return
	 */
	public boolean isPossuiProcedimentoAutorizadoA100Porcento() {
		for (ProcedimentoCirurgicoLayer layer : this.getProcedimentosCirurgicosLayer()) {
			if (MoneyCalculation.compare(layer.getPorcentagem(), new BigDecimal(100)) == 0)
				return true;
		}
		return false;
	}
	
	/**
	 * Retorna os itensPacote da guia que estão na situação Solicitado(a).
	 * @return
	 */
	public Set<ItemPacote> getItensPacoteSolicitados() {

		Set<ItemPacote> itensPacoteSolicitados = new HashSet<ItemPacote>(
				getItensPacotes(SituacaoEnum.SOLICITADO.descricao()));
		
		return itensPacoteSolicitados;
	}
	
	/**
	 * Retorna os itensPacote da guia que não estão na situação CANCELADO(A).
	 * @return
	 */
	public List<ItemPacote> getItensPacoteNaoCancelados() {
		List<ItemPacote> itensPacoteValidos = new ArrayList<ItemPacote>();
		
		if (this.getItensPacote() != null) {
			for (ItemPacote item : this.getItensPacote()) {
				if (!item.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())) {
					itensPacoteValidos.add(item);
				}
			}
		}
		
		return itensPacoteValidos;
	}
	
	/**
	 * Retorna uma coleção de ItemPacote que estejam nas situações fornecidas.
	 * @param descricao
	 * @return
	 */
	public List<ItemPacote> getItensPacotes(String... descricao) {
		List<ItemPacote> itensPacoteValidos = new ArrayList<ItemPacote>();
		
		if (this.getItensPacote() != null) {
			for (ItemPacote item : this.getItensPacote()) {
				boolean isValido = false;
				for (String situacao : descricao)
					if (item.isSituacaoAtual(situacao)) {
						isValido = true;
						break;
					}
				
				if (isValido)
					itensPacoteValidos.add(item);
			}
		}
		
		return itensPacoteValidos;
	}
	
	/**
	 * 
	 * @param autorizado
	 * @return Itens pacotes solicitados tenha seu atributo 'autorizado' igual ao passado como parâmetro
	 */
	private Set<ItemPacote> getItensPacoteEmAutorizacao(Boolean autorizado) {

		Set<ItemPacote> pacotesEmAutorizacao = new HashSet<ItemPacote>();
		
		for (ItemPacote itemPacote : this.getItensPacotes(SituacaoEnum.SOLICITADO.descricao())) {
			if (itemPacote.getAutorizado() == autorizado) {
				pacotesEmAutorizacao.add(itemPacote);
			}
		}
		
		return pacotesEmAutorizacao;
	}
	
	/**
	 * 
	 * @return Itens pacotes solicitados e que foram marcados como autorizados no fluxo de autorização 
	 */
	public Set<ItemPacote> getItensPacoteAutorizadosEmAutorizacao() {
		return this.getItensPacoteEmAutorizacao(Boolean.TRUE);
	}
	
	/**
	 * 
	 * @return Itens pacotes solicitados e que foram marcados como não autorizados no fluxo de autorização 
	 */
	public Set<ItemPacote> getItensPacoteNegadosEmAutorizacao() {
		return this.getItensPacoteEmAutorizacao(Boolean.FALSE);
	}
	
	/**
	 * Indica se a guia possui procedimentos especiais ou pacotes, na situação "Solicitado(a)".
	 * @return
	 */
	public boolean isPossuiItensSolicitados(){
		return this.isPossuiProcedimentosSolicitados() || !this.getItensPacoteSolicitados().isEmpty();
	}
	
	/**
	 * Indica se a guia possui algum ItemDiaria autorizado e sem data inicial.
	 * <p>
	 * Isso porque algumas guias, mesmo tendo sido criadas após a entrada em produção
	 * da demanda de colocar data inicial e final em cada item-diaria (19/11/2009),
	 * elas podem "herdar" de suas respectivas
	 * guias-origem itens-diaria que não possuem essas datas.
	 * 
	 */
	public boolean isPossuiItemDiariaAutorizadoSemDataInicial(){
		for (ItemDiaria item : this.getItensDiariaAutorizados()) {
			if (item.getDataInicial() == null)
				return true;
		}
		return false;
	}

	@Override
	public final boolean isHonorarioMedico() {
		return false;
	}

	public Set<Diaria> getDiarias() {
		Set<Diaria> diarias = new HashSet<Diaria>();
		List<ItemDiaria> diariasAutorizadas = this.getDiariasAutorizadas();
		for (ItemDiaria itemDiaria : diariasAutorizadas) {
			diarias.add(itemDiaria.getDiaria());
		}
		return diarias;
	}
	
	public Boolean isAutorizado() {
		return autorizado;
	}

	public Boolean getAutorizado() {
		return autorizado;
	}

	public void setAutorizado(Boolean autorizado) {
		this.autorizado = autorizado;
	}
	
	@Override
	public boolean isAcompanhamentoAnestesico() {
		return false;
	}
	
	@Override
	public boolean isGeraExameExterno() {
		return true;
	}
	
	@Override
	public boolean isGeraExameExternoParaPacientesCronicos() {
		return true;
	}

	public void addItemGuiaFaturamento(ItemGuiaFaturamento itemGuiaFaturamento) {
		if(itemGuiaFaturamento != null){
			super.getItensGuiaFaturamento().add(itemGuiaFaturamento);
			itemGuiaFaturamento.setGuia(this);
			this.setValorPagoPrestador(MoneyCalculation.rounded(this.getValorPagoPrestador().subtract(itemGuiaFaturamento.getValor())));
		}
	}
	
	public Set<RegistroTecnicoDaAuditoria> getRegistrosTecnicosDaAuditoria() {
		return registrosTecnicosDaAuditoria;
	}
	
	public void setRegistrosTecnicosDaAuditoria(
			Set<RegistroTecnicoDaAuditoria> registrosTecnicosDaAuditoria) {
		this.registrosTecnicosDaAuditoria = registrosTecnicosDaAuditoria;
	}
	
	/**
	 * @return Fornece somente os registros de auditor cujo autor ({@link RegistroTecnicoDaAuditoria#getUsuario()})
	 * é o usuário do fluxo que está manipulando a guia.
	 */
	public List<RegistroTecnicoDaAuditoria> getRegistrosTecnicosDaAuditoriaDoUsuarioDoFluxo() {
		List<RegistroTecnicoDaAuditoria> registrosTecnicosDaAuditoriaDoUsuarioDoFluxo = new ArrayList<RegistroTecnicoDaAuditoria>();
		if (this.getUsuarioDoFluxo() != null){
			for (RegistroTecnicoDaAuditoria rta : registrosTecnicosDaAuditoria) {
				if (rta.getUsuario().equals(this.getUsuarioDoFluxo()))
					registrosTecnicosDaAuditoriaDoUsuarioDoFluxo.add(rta);
			}
		}
		return registrosTecnicosDaAuditoriaDoUsuarioDoFluxo;
	}
	
	/**
	 * Adiciona uma coleção de registros técnicos da auditoria.
	 * @param registros
	 * @param usuario
	 */
	public void addRegistrosTecnicosDaAuditoria(Collection<RegistroTecnicoDaAuditoria> registros, UsuarioInterface usuario){
		if (this.getRegistrosTecnicosDaAuditoria() == null)
			this.setRegistrosTecnicosDaAuditoria(new HashSet<RegistroTecnicoDaAuditoria>());

		for (RegistroTecnicoDaAuditoria registro : registros) {
			registro.setDataDeCriacao(new Date());
			registro.setGuia(this);
			registro.setUsuario(usuario);
		}

		this.getRegistrosTecnicosDaAuditoria().addAll(registros);
	}

	public Especialidade getEspecialidadeTemp() {
		return especialidadeTemp;
	}

	public void setEspecialidadeTemp(Especialidade especialidadeTemp) {
		this.especialidadeTemp = especialidadeTemp;
	}
	
	public int getPrazoMaximofechamento(){
		int resultado = 0;
		
		if((Utils.compareData(this.getDataMarcacao(), PainelDeControle.getPainel().getDataVigenciaPrazoFinalEntregaDeLote()) < 0)){
			resultado = LIMITE_DIAS_FECHAMENTO; 
		} else {
			resultado = PainelDeControle.getPainel().getPrazoFinalParaEntregaDeLote();
		}
		
		return resultado;
	}
	
	/**
	 * @return Os honorários de pacotes válidos gerados à partir desta guia
	 */
	public Set<HonorarioExterno> getHonorariosPacote(){
		Set<HonorarioExterno> honorarios = new HashSet<HonorarioExterno>();
		
		for (GuiaHonorarioMedico ghm : this.getGuiasFilhasDeHonorarioMedico()) {
			if(ghm.isGuiaSomentePacote()){
				for (HonorarioExterno honorario : ghm.getHonorariosPacote()) {
					if (!(honorario.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())
							|| honorario.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao()))) {
						honorarios.add(honorario);
					}
				}
			}
		}
		return honorarios;
	}
	
	public boolean temConsultaPromocionalDeUrgenciaLiberada() { 
		Set<PromocaoConsulta> consultasPromocionais = this.getSegurado().getConsultasPromocionais();
		for (PromocaoConsulta consultaPromocional : consultasPromocionais) {
			if(consultaPromocional.getEspecialidade() != null){				
				HibernateUtil.currentSession().lock(consultaPromocional.getEspecialidade(), LockMode.NONE);
			}
			boolean consultaUrgencia = this.isConsultaUrgencia();
			boolean urgencia = consultaPromocional.isUrgencia();
			boolean naoEstaVencido = !consultaPromocional.isVencido();
			boolean naoEstaUtilizado = !consultaPromocional.isUtilizado();
			boolean mesmaEspecialidade = consultaPromocional.getEspecialidade().equals(this.getEspecialidade());
			if(consultaUrgencia && urgencia && naoEstaVencido && naoEstaUtilizado && mesmaEspecialidade) {
				setConsultaPromocionalLiberada(consultaPromocional);
				return true;
			}
		}
		return false;
	}	
	
	public void setConsultaPromocionalLiberada(PromocaoConsulta consulta){
		promocaoConsulta = consulta;
	}

	public PromocaoConsulta getPromocaoConsultaLiberada() {
		return promocaoConsulta;
	}
	
	public Set<ItemTaxa> getItensTaxaTemp() {
		return itensTaxaTemp;
	}
	
	public Set<ItemGasoterapia> getItensGasoterapiaTemp() {
		return itensGasoterapiaTemp;
	}
	
	
	public Set<ProcedimentoCirurgicoLayer> getLayerProcedimentos() {
		return procedimentosCirurgicosLayer;
	}

	public void setLayerProcedimentos(Set<ProcedimentoCirurgicoLayer> layerProcedimentos) {
		this.procedimentosCirurgicosLayer = layerProcedimentos;
	}
	
	public Set<ProcedimentoInterface> getProcedimentoTemp() {
		return procedimentoTemp;
	}

	public void setProcedimentoTemp(Set<ProcedimentoInterface> procedimentoTemp) {
		this.procedimentoTemp = procedimentoTemp;
	}

	public Set<ProcedimentoInterface> getProcedimentoOutrosTemp() {
		return procedimentoOutrosTemp;
	}

	public void setProcedimentoOutrosTemp(Set<ProcedimentoInterface> procedimentoOutrosTemp) {
		this.procedimentoOutrosTemp = procedimentoOutrosTemp;
	}

	public Set<ProcedimentoLayer> getProcedimentoLayer() {
		return procedimentoLayer;
	}

	public List<ProcedimentoLayer> getProcedimentoLayerOrdenadosCBHPM() {
		
		List<ProcedimentoLayer> procedimentos = new ArrayList<ProcedimentoLayer>();
		
		procedimentos.addAll(this.procedimentoLayer);
		
		Utils.sort(procedimentos, false, "procedimentoDaTabelaCBHPM.descricao");
		
		return procedimentos;
	}
	
	public void setProcedimentoLayer(Set<ProcedimentoLayer> procedimentoLayer) {
		this.procedimentoLayer = procedimentoLayer;
	}

	public Set<ProcedimentoOutrosLayer> getProcedimentoOutrosLayer() {
		return procedimentoOutrosLayer;
	}

	public List<ProcedimentoOutrosLayer> getProcedimentoOutrosLayerOrdenadosData() {
		
		List<ProcedimentoOutrosLayer> procedimentos = new ArrayList<ProcedimentoOutrosLayer>();
		
		procedimentos.addAll(this.procedimentoOutrosLayer);
		
		Utils.sort(procedimentos, false, "dataRealizacaoOutros");
		
		return procedimentos;
	}
	
	public void setProcedimentoOutrosLayer(Set<ProcedimentoOutrosLayer> procedimentoOutrosLayer) {
		this.procedimentoOutrosLayer = procedimentoOutrosLayer;
	}

	public void setItensTaxaTemp(Set<ItemTaxa> itensTaxaTemp) {
		this.itensTaxaTemp = itensTaxaTemp;
	}

	public void setItensGasoterapiaTemp(Set<ItemGasoterapia> itensGasoterapiaTemp) {
		this.itensGasoterapiaTemp = itensGasoterapiaTemp;
	}

	public Set<ItemGasoterapiaLayer> getItensGasoterapiaLayer() {
		return itensGasoterapiaLayer;
	}

	public Set<ItemTaxaLayer> getItensTaxaLayer() {
		return itensTaxaLayer;
	}

	public Set<ItemPacoteLayer> getItensPacoteAuditoriaLayer() {
		return itensPacoteAuditoriaLayer;
	}

	public void setItensPacoteAuditoriaLayer(
			Set<ItemPacoteLayer> itensPacoteAuditoriaLayer) {
		this.itensPacoteAuditoriaLayer = itensPacoteAuditoriaLayer;
	}

	public Set<ProcedimentoCirurgicoLayer> getProcedimentosCirurgicosLayer() {
		return procedimentosCirurgicosLayer;
	}

	public void setProcedimentosCirurgicosLayer(
			Set<ProcedimentoCirurgicoLayer> procedimentosCirurgicosLayer) {
		this.procedimentosCirurgicosLayer = procedimentosCirurgicosLayer;
	}
	
	
	public boolean getProrrogacao() {
	    if (getSituacao().getDescricao().equals(SituacaoEnum.SOLICITADO_PRORROGACAO.descricao())) {
	    	return true;
	    }
	    return false;
	}
	
	public boolean getInternacaoNaoProrrogacao() {
	    if ((this instanceof GuiaInternacaoEletiva || this instanceof GuiaInternacaoUrgencia) && !getProrrogacao()){
	    	return true;
	    }
	    return false;
	}
	
	public boolean getInternacao() {
		if (this instanceof GuiaInternacaoEletiva || this instanceof GuiaInternacaoUrgencia){
			return true;
		}
		return false;
	}

	public void fillLayersRecurso() {
		initializeLayers();
		managerLayerRecurso = new ManagerLayerRecurso((GuiaCompleta<ProcedimentoInterface>) this, isRecursando);
	}

	public List<ItemRecursoLayer> getLayersRecursoProcedimentoCirurgico() {
		return layersRecursoProcedimentoCirurgico;
	}

	public void setLayersRecursoProcedimentoCirurgico(
			List<ItemRecursoLayer> layersRecursoProcedimentoCirurgico) {
		this.layersRecursoProcedimentoCirurgico = layersRecursoProcedimentoCirurgico;
	}

	public List<ItemRecursoLayer> getLayersRecursoItensGasoterapia() {
		return layersRecursoItensGasoterapia;
	}

	public void setLayersRecursoItensGasoterapia(
			List<ItemRecursoLayer> layersRecursoItensGasoterapia) {
		this.layersRecursoItensGasoterapia = layersRecursoItensGasoterapia;
	}

	public List<ItemRecursoLayer> getLayersRecursoProcedimentosExame() {
		return layersRecursoProcedimentosExame;
	}

	public void setLayersRecursoProcedimentosExame(
			List<ItemRecursoLayer> layersRecursoProcedimentosExame) {
		this.layersRecursoProcedimentosExame = layersRecursoProcedimentosExame;
	}

	public List<ItemRecursoLayer> getLayersRecursoProcedimentosOutros() {
		return layersRecursoProcedimentosOutros;
	}

	public void setLayersRecursoProcedimentosOutros(
			List<ItemRecursoLayer> layersRecursoProcedimentosOutros) {
		this.layersRecursoProcedimentosOutros = layersRecursoProcedimentosOutros;
	}

	public List<ItemRecursoLayer> getLayersRecursoItensTaxa() {
		return layersRecursoItensTaxa;
	}

	public void setLayersRecursoItensTaxa(
			List<ItemRecursoLayer> layersRecursoItensTaxa) {
		this.layersRecursoItensTaxa = layersRecursoItensTaxa;
	}

	public List<ItemRecursoLayer> getLayersRecursoItensDiaria() {
		return layersRecursoItensDiaria;
	}

	public void setLayersRecursoItensDiaria(
			List<ItemRecursoLayer> layersRecursoItensDiaria) {
		this.layersRecursoItensDiaria = layersRecursoItensDiaria;
	}

	public List<ItemRecursoLayer> getLayersRecursoItensPacote() {
		return layersRecursoItensPacote;
	}

	public void setLayersRecursoItensPacote(
			List<ItemRecursoLayer> layersRecursoItensPacote) {
		this.layersRecursoItensPacote = layersRecursoItensPacote;
	}

	public GuiaRecursoGlosa getGuiaRecursoGlosa() {
		return guiaRecursoGlosa;
	}

	public void setGuiaRecursoGlosa(GuiaRecursoGlosa recursoGlosa) {
		this.guiaRecursoGlosa = recursoGlosa;
	}

	public void fillLayerGuiaGlosada() {
		initializeLayers();
		layerRecursoGuia.add(new ItemRecursoLayer(this, null, this.getMotivoGlosa().getDescricao()));
	}

	private void initializeLayers() {
		this.layerRecursoGuia = new ArrayList<ItemRecursoLayer>();
		this.layersRecursoItensDiaria = new ArrayList<ItemRecursoLayer>();
		this.layersRecursoItensGasoterapia = new ArrayList<ItemRecursoLayer>();
		this.layersRecursoItensPacote = new ArrayList<ItemRecursoLayer>();
		this.layersRecursoItensTaxa = new ArrayList<ItemRecursoLayer>();
		this.layersRecursoProcedimentoCirurgico = new ArrayList<ItemRecursoLayer>();
		this.layersRecursoProcedimentosExame = new ArrayList<ItemRecursoLayer>();
		this.layersRecursoProcedimentosOutros = new ArrayList<ItemRecursoLayer>();
	}

	public List<ItemRecursoLayer> getLayerRecursoGuia() {
		if (layerRecursoGuia == null) {
			return new ArrayList<ItemRecursoLayer>();
		}
		return layerRecursoGuia;
	}

	public void setLayerRecursoGuia(List<ItemRecursoLayer> layerRecursoGuia) {
		this.layerRecursoGuia = layerRecursoGuia;
	}
	
	public void setItemGlosavelAnterior(ItemGlosavel anterior){}
	public ItemGlosavel getItemGlosavelAnterior(){
		return null;
	}

	public ItemGlosavel accept(ItemGlosavelVisitor visitor) {
		return null;
	}
	
	public boolean isTipoGuia() {
		return true;
	}
	public boolean isTipoItemGuia() {
		return false;
	}
	public boolean isTipoProcedimento(){
		return false;
	}

	public ItemRecursoGlosa getItemRecurso() {
		return itemRecurso;
	}

	public void setItemRecurso(ItemRecursoGlosa itemRecurso) {
		this.itemRecurso = itemRecurso;
	}

	public void setItensDiariaAuditoriaLayer(
			Set<ItemDiariaLayer> itensDiariaAuditoriaLayer) {
		this.itensDiariaAuditoriaLayer = itensDiariaAuditoriaLayer;
	}

	public void setItensGasoterapiaLayer(
			Set<ItemGasoterapiaLayer> itensGasoterapiaLayer) {
		this.itensGasoterapiaLayer = itensGasoterapiaLayer;
	}

	public void setItensTaxaLayer(Set<ItemTaxaLayer> itensTaxaLayer) {
		this.itensTaxaLayer = itensTaxaLayer;
	}

	public boolean isRecursando() {
		return isRecursando;
	}

	public void setRecursando(boolean isRecursando) {
		this.isRecursando = isRecursando;
	}
	
	public GuiaAcompanhamentoAnestesico getGuiaAcompanhamentoAnestesicoDeExame() {
		for (GuiaSimples gaa : this.getGuiasDeAcompanhamentoAnestesico()) {
			if (gaa.isAcompanhamentoAnestesico()) {
				guiaAcompanhamentoAnestesicoDeExame = (GuiaAcompanhamentoAnestesico) gaa;
			}
		}
		
		return guiaAcompanhamentoAnestesicoDeExame;
	}
	
}
