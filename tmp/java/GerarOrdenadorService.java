package br.com.infowaypi.ecare.financeiro.ordenador;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.com.infowaypi.ecare.financeiro.faturamento.GeracaoFaturamentoNormal;
import br.com.infowaypi.ecare.financeiro.faturamento.GeracaoFaturamentoPassivo;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AbstractFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.GuiaFaturavel;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ItemGuiaFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.TetoPrestadorFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ordenador.InformacaoOrdenador;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ordenador.Ordenador;
import br.com.infowaypi.ecarebc.service.financeiro.FaturamentoDAO;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Service responsável pela geraçao do Ordenador.
 * @author <a href="mailto:mquixaba@gmail.com">Marcus Quixabeira</a>
 * @author <a href="mailto:eduardo@infoway-pi.com.br">Eduardo Filipe</a>
 * @since 2010-07-19 17:33
 *
 */
public class GerarOrdenadorService {
	
	protected ProgressBarFinanceiro progressBar;
	
		Ordenador ultimoOrdenador = null;
		public Ordenador gerarOrdenador(Integer identificador, Date competencia, Date dataRecebimento, UsuarioInterface usuario, ProgressBarFinanceiro progressBar) throws Exception {
		
		this.progressBar = progressBar;
		
		boolean isIdendificadorNulo = identificador == null;
		boolean isCompetenciaNula = competencia == null;
		boolean isDataRecebimentoNula = dataRecebimento == null;
		
		if(isIdendificadorNulo && isCompetenciaNula && isDataRecebimentoNula) {
			throw new RuntimeException("Caro usuário, informe um Identificador ou uma Competencia e uma Data de Recebimento para prosseguir.");
		}
		
		if(!isIdendificadorNulo) {
			progressBar.setProgressTitle("Alteração Ordenador");
			progressBar.setProgressStatusText("Carregando ordenador..");
			progressBar.startMockProgress(0L, 99, 150000);
			
			Ordenador ordenadorFromBase = this.buscarOrdenadorPeloID(identificador);
			Assert.isNotNull(ordenadorFromBase, "Não foi possivel encontrar um ordenador com o identificador informado.");
			
			Ordenador ordenadorNovo = (Ordenador) ordenadorFromBase.clone();
			ultimoOrdenador = this.buscarUltimoOrdenador(ordenadorNovo.getCompetencia());
			ordenadorNovo.atualizaIdentificador(ultimoOrdenador);
			ordenadorNovo.setUsuario(usuario);
			
			return ordenadorNovo;
		} else {
			progressBar.progressTitle = "Geração do ordenador";
			Assert.isTrue(!isCompetenciaNula, "O campo Competência deve ser informado.");
			Assert.isTrue(!isDataRecebimentoNula, "O campo Data de Recebimento das Contas deve ser informado.");
			
			Ordenador ordenadorNovo = new Ordenador();
			ordenadorNovo.setCompetencia(competencia);
			ordenadorNovo.setDataGeracao(new Date());
			ordenadorNovo.setDataRecebimento(dataRecebimento);
			ordenadorNovo.setUsuario(usuario);
			
			ultimoOrdenador = this.buscarUltimoOrdenador(competencia);
			ordenadorNovo.atualizaIdentificador(ultimoOrdenador);
			
			List<AbstractFaturamento> faturamentosNormal = gerarFaturamentoNormal(competencia, dataRecebimento, usuario, ordenadorNovo);
			
			List<AbstractFaturamento> faturamentosPassivo = gerarFaturamentoPassivo(competencia, dataRecebimento, usuario, ordenadorNovo);
			
			List<AbstractFaturamento> faturamentos = new ArrayList<AbstractFaturamento>();
			faturamentos.addAll(faturamentosNormal);
			faturamentos.addAll(faturamentosPassivo);
			
			progressBar.setProgressStatusText("Finalizando geração do ordenador...");
			Map<Prestador, InformacaoOrdenador> informacaoOrdenadorPorPrestador = faturamentosInformacaoOrdenadorConverter(faturamentos, competencia);
			
			List<Prestador> prestadoresRestantes = new ArrayList<Prestador>();
			prestadoresRestantes = buscarPrestadoresRestantes(informacaoOrdenadorPorPrestador.keySet());
			
			for (Prestador prestador : prestadoresRestantes) {
				InformacaoOrdenador informacaoOrdenadorNovo = new InformacaoOrdenador();
				informacaoOrdenadorNovo.setCompetencia(competencia);
				informacaoOrdenadorNovo.setPrestador(prestador);
				informacaoOrdenadorPorPrestador.put(prestador, informacaoOrdenadorNovo);
			}
			
			for (InformacaoOrdenador informacao : informacaoOrdenadorPorPrestador.values()) {
				informacao.setOrdenador(ordenadorNovo);
			}
			ordenadorNovo.setInformacoesOrdenador(new HashSet<InformacaoOrdenador>(informacaoOrdenadorPorPrestador.values()));
			ordenadorNovo.update();

			return ordenadorNovo;
		}
	}

	/**
	 * Percorre a lista de faturamentos e retorna um mapa contendo Informação Ordenador de acordo com o faturamento
	 * @param faturamentos
	 * @param competencia
	 * @return
	 */
	private Map<Prestador, InformacaoOrdenador> faturamentosInformacaoOrdenadorConverter(List<AbstractFaturamento> faturamentos, Date competencia){
		Map<Prestador, InformacaoOrdenador> informacaoOrdenadorPorPrestador = new HashMap<Prestador, InformacaoOrdenador>();
		for (AbstractFaturamento faturamento : faturamentos) {
			InformacaoOrdenador informacaoOrdenador;
			if(informacaoOrdenadorPorPrestador.keySet().contains(faturamento.getPrestador())) {
				informacaoOrdenador = informacaoOrdenadorPorPrestador.get(faturamento.getPrestador());
			} else {
				informacaoOrdenador = new InformacaoOrdenador();
				informacaoOrdenador.setCompetencia(competencia);
				informacaoOrdenador.setPrestador(faturamento.getPrestador());
				informacaoOrdenadorPorPrestador.put(faturamento.getPrestador(), informacaoOrdenador);
			}
			
			if (faturamento.getTipoFaturamento().equals(AbstractFaturamento.FATURAMENTO_PASSIVO)){
				informacaoOrdenador.setValorPassivo(faturamento.getValorBruto());
				informacaoOrdenador.setValorAFaturarPassivo(faturamento.getValorBruto());
			} else {
				informacaoOrdenador.setValorNormal(faturamento.getValorBruto());
				informacaoOrdenador.setValorAFaturarNormal(faturamento.getValorBruto());
			}
		}
		return informacaoOrdenadorPorPrestador;
	}
	
	private Ordenador buscarOrdenadorPeloID(Integer identificador) {
	
		Criteria criteria = HibernateUtil.currentSession().createCriteria(
				Ordenador.class);
		criteria.setLockMode(LockMode.WRITE);

		criteria.add(Expression.eq("identificador", identificador));

		Ordenador ordenadorEncontrado = (Ordenador) criteria.uniqueResult();
		
		if(ordenadorEncontrado != null){
			tocarOrdenador(ordenadorEncontrado);
		}
		
		return ordenadorEncontrado;
	}

	private void tocarOrdenador(Ordenador ordenadorEncontrado) {

		Map<Long, GuiaFaturavel> guiasOrdenador = new HashMap<Long, GuiaFaturavel>();
		for (GuiaFaturavel guia : ordenadorEncontrado.getGuias()) {
			guia.setItensGuiaFaturamento(new HashSet<ItemGuiaFaturamento>());
			guiasOrdenador.put(guia.getIdGuia(), guia);
		}
		Map<Long, List<ItemGuiaFaturamento>> idGuiaItemGuia = buscarItensGuiaFaturamento(guiasOrdenador
				.values());

		for (Long id : idGuiaItemGuia.keySet()) {
			guiasOrdenador.get(id).getItensGuiaFaturamento().addAll(idGuiaItemGuia.get(id));
		}

		for (InformacaoOrdenador informacoesOrdenador : ordenadorEncontrado
				.getInformacoesOrdenador()) {
			informacoesOrdenador.getPrestador().isPessoaFisica();
		}
	}
	
	private Map<Long, List<ItemGuiaFaturamento>> buscarItensGuiaFaturamento(Collection<GuiaFaturavel> guias){
		StringBuilder sb = new StringBuilder();
		List<Long> idsList = new ArrayList<Long>();
		for (GuiaFaturavel guia : guias) {
			idsList.add(guia.getIdGuia());
		}
		sb.append("select item.guia.idGuia, item from ItemGuiaFaturamento item where item.guia.idGuia in (:ids)");
		
		Map<Long, List<ItemGuiaFaturamento>> guiaItem = new HashMap<Long, List<ItemGuiaFaturamento>>();
		List<Long> subList;
		int size = idsList.size();
		int de = 0;
		int ate = 0;
		
		while(de < size){
			if (de + 5000 < size){
				ate = de + 5000;
			} else {
				ate = size;
			}
			subList = idsList.subList(de, ate);
			de = ate;
			
			Query query = HibernateUtil.currentSession().createQuery(sb.toString())
							.setParameterList("ids", subList);
			List<Object[]> list = query.list();
			
			for (Object[] object : list) {
				Long id = (Long) object[0];
				ItemGuiaFaturamento item = (ItemGuiaFaturamento) object[1];
				if(guiaItem.containsKey(id)){
					guiaItem.get(id).add(item);
				} else {
					List<ItemGuiaFaturamento> itens = new ArrayList<ItemGuiaFaturamento>();
					itens.add(item);
					guiaItem.put(id, itens);
				}
			}
		}
		
		return guiaItem;
	}

	private List<AbstractFaturamento>  gerarFaturamentoPassivo(Date competencia,
			Date dataRecebimento, UsuarioInterface usuario, Ordenador ordenadorNovo) throws Exception {
		
		GeracaoFaturamentoPassivo servicePassivo = new GeracaoFaturamentoPassivo(progressBar);
		
		progressBar.setProgressStatusText("Buscando guias...");
		progressBar.startMockProgress(30, 80000);
		List<GuiaFaturavel> guiasFaturamentoPassivo = FaturamentoDAO.buscarGuiasFaturamentoPassivo(competencia, dataRecebimento, new HashSet<Prestador>());
		List<AbstractFaturamento> faturamentos = servicePassivo.gerarFaturamento(competencia, dataRecebimento, new HashSet<TetoPrestadorFaturamento>(), guiasFaturamentoPassivo);
		
		ordenadorNovo.getGuiasFaturamentoPassivo().addAll(guiasFaturamentoPassivo);
		
		return faturamentos;
	}

	private List<AbstractFaturamento> gerarFaturamentoNormal(Date competencia,
			Date dataRecebimento, UsuarioInterface usuario,
			Ordenador ordenadorNovo) throws Exception {
		GeracaoFaturamentoNormal service = new GeracaoFaturamentoNormal(progressBar);
		
		progressBar.setProgressStatusText("Buscando guias...");
		progressBar.startMockProgress(30, 80000);
		
		List<GuiaFaturavel> guiasFaturamentoNormal = FaturamentoDAO.buscarGuias(competencia, dataRecebimento, null);
		List<AbstractFaturamento> faturamento = service.gerarFaturamento(competencia, dataRecebimento, new HashSet<TetoPrestadorFaturamento>(), guiasFaturamentoNormal );

		ordenadorNovo.getGuiasFaturamentoNormal().addAll(guiasFaturamentoNormal);
		return faturamento;
	}
	
	public Ordenador verificarGeracao(Integer identificador, Date competencia, Date dataRecebimento, UsuarioInterface usuario) throws Exception{
		if (ProgressBarFinanceiro.getExcecaoDoFluxo() != null){
			throw ProgressBarFinanceiro.getExcecaoDoFluxo();
		}
		return ProgressBarFinanceiro.getOrdenador();
	}
	
	/**
	 * Método responsável por buscar prestadores ativos sem ordenador
	 * @param prestadores que ja possuem ordenador
	 * @return prestadores ativos sem ordenador
	 */
	private List<Prestador> buscarPrestadoresRestantes(Set<Prestador> prestadores) {
		List<Long> ids = new ArrayList<Long>();
		for (Prestador prest : prestadores) {
			ids.add(prest.getIdPrestador());
		}
		List<Prestador> prestadoresRestantes = HibernateUtil.currentSession().createCriteria(Prestador.class)
			.add(Expression.not(Expression.in("idPrestador", ids)))
			.add(Expression.eq("situacao.descricao", SituacaoEnum.ATIVO.descricao()))
			.list();
		
		return prestadoresRestantes;
	}

	/**
	 * atualiza os dados do ordenador à partir dos tetos informados
	 * @param ordenador atualizado no update-param (tetos) no fluxo de geração do ordenador
	 * @param isAvancar flag que marca se a página vai apenas atualizar ou passar para a próxima tela "conferir dados" 
	 * @return Ordenador que será terá os tetos editados novamente ou o ordenador pronto, dependendo do isAvancar 
	 * @throws Exception
	 */
	public Ordenador informarDados(Ordenador ordenador, boolean isAvancar) throws Exception {
		ordenador.validateTetos();
		atualizarInformacaoOrdenador(ordenador);
		ordenador.update();
		ordenador.validarValoresAFaturar();
		ordenador.setFlag(isAvancar);
		return ordenador;
	}
	/**
	 * Gera o faturamento e atualiza os InformacaoOrdenador 
	 * @throws Exception 
	 */
	public void atualizarInformacaoOrdenador(Ordenador ordenador) throws Exception{
		Date competencia = ordenador.getCompetencia();
		Date dataRecebimento = ordenador.getDataRecebimento();
		Set<TetoPrestadorFaturamento> tetosNormal = ordenador.getTetos(true);
		Set<TetoPrestadorFaturamento> tetosPassivo = ordenador.getTetos(false);
		ArrayList<GuiaFaturavel> guiasNormal = new ArrayList<GuiaFaturavel>(ordenador.getGuiasFaturamentoNormal());
		ArrayList<GuiaFaturavel> guiasPassivo = new ArrayList<GuiaFaturavel>(ordenador.getGuiasFaturamentoPassivo());
		ProgressBarFinanceiro progressBar = new ProgressBarFinanceiro();
		GeracaoFaturamentoNormal serviceNormal = new GeracaoFaturamentoNormal(progressBar);
		List<AbstractFaturamento> faturamentosNormal = serviceNormal.gerarFaturamento(competencia, dataRecebimento, tetosNormal, guiasNormal);
		
		GeracaoFaturamentoPassivo servicePassivo = new GeracaoFaturamentoPassivo(progressBar);
		List<AbstractFaturamento> faturamentosPassivo = servicePassivo.gerarFaturamento(competencia, dataRecebimento, tetosPassivo,	guiasPassivo);

		for (InformacaoOrdenador informacaoOrdenador : ordenador.getInformacoesOrdenador()) {
			informacaoOrdenador.setValorAFaturarNormal(BigDecimal.ZERO);
			informacaoOrdenador.setValorAFaturarPassivo(BigDecimal.ZERO);
		}
		
		Map<Prestador, InformacaoOrdenador> mapInformacaoOrdenador = ordenador.getMapInformacaoOrdenador();
		
		for (AbstractFaturamento faturamento : faturamentosNormal) {
			mapInformacaoOrdenador.get(faturamento.getPrestador()).setValorAFaturarNormal(faturamento.getValorBruto());
		}
		
		for (AbstractFaturamento faturamento : faturamentosPassivo) {
			mapInformacaoOrdenador.get(faturamento.getPrestador()).setValorAFaturarPassivo(faturamento.getValorBruto());
		}
		
	}
	
	public Ordenador save(Ordenador ordenador) throws Exception {
		ImplDAO.save(ordenador);
		return ordenador;
	}
	
	public void finalizar(Ordenador ordenador) throws Exception {}
	
	/**
	 * Busca o ultimo ordenador dentro de uma competencia.
	 * @param competencia
	 * @return
	 */
	public Ordenador buscarUltimoOrdenador(Date competencia) {
		List<Ordenador> ordenadores = HibernateUtil.currentSession().createCriteria(Ordenador.class)
			.add(Expression.eq("competencia", competencia))
			.addOrder(Order.desc("identificador"))
			.list();
		
		if(!ordenadores.isEmpty() )
			return ordenadores.get(0);
		else {
			return null;
		}
	}
}
