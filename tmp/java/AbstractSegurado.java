package br.com.infowaypi.ecarebc.segurados;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.FlushMode;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;

import br.com.infowaypi.ecare.contrato.ContratoSR;
import br.com.infowaypi.ecare.enums.TipoLiberacaoForaLimiteEnum;
import br.com.infowaypi.ecare.programaPrevencao.AssociacaoSeguradoPrograma;
import br.com.infowaypi.ecare.programaPrevencao.ProfissionalEspecialidade;
import br.com.infowaypi.ecare.questionarioqualificado.Questionario;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Prestador.TipoIncremento;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaOdonto;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.consumo.ColecaoConsumosInterface;
import br.com.infowaypi.ecarebc.consumo.ComponenteColecaoConsumos;
import br.com.infowaypi.ecarebc.consumo.ConsumoInterface;
import br.com.infowaypi.ecarebc.consumo.periodos.Periodo;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.enums.TipoSeguradoEnum;
import br.com.infowaypi.ecarebc.exceptions.ConsumoException;
import br.com.infowaypi.ecarebc.odonto.Odontograma;
import br.com.infowaypi.ecarebc.planos.Faixa;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.molecular.parameter.NotEquals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.pessoa.PessoaFisica;
import br.com.infowaypi.msr.pessoa.PessoaFisicaInterface;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

/**
 * 
 * Fiz alterações nessa classe com relação aos contadores de liberações fora do limite de consumos do segurado.
 * @changes Luciano Rocha
 * @since 19/09/2012
 */
@SuppressWarnings({ "unchecked", "rawtypes", "serial"})
public abstract class AbstractSegurado extends ImplColecaoSituacoesComponent implements SeguradoInterface {

	private Long idSegurado;
	protected String codigoLegado;
	protected String contrato;
	protected Set<GuiaSimples> guias;
	protected ColecaoConsumosInterface consumoIndividual;
	protected PessoaFisicaInterface pessoaFisica;
	protected Date dataAdesao;
	protected transient Odontograma odontograma;
	protected Odontograma odontogramaCompleto;
	private Set<AssociacaoSeguradoPrograma> associacaoSegurados;
	
	/**
	 * Campo criado para contabilizar as liberações pelo Auditor que passam fora do limite de consumos do beneficiário.
	 * @author Luciano Rocha
	 * @since 19/09/2012
	 */
	private int contadorLiberacaoPeloAuditor;
	/**
	 * Campo criado para contabilizar as liberações pelo MPPS que passam fora do limite de consumos do beneficiário.
	 * @author Luciano Rocha
	 * @since 19/09/2012
	 */
	private int contadorLiberacaoPeloMpps;
	
	private boolean pacienteCronico;
	private Set<RegistroPacientesCronicos> registrosPacientesCronicos;
	
	private Set<ContratoSR> contratos;
	
	private Date inicioDaCarencia;

	public int getContadorLiberacaoPeloAuditor() {
		return contadorLiberacaoPeloAuditor;
	}

	public void setContadorLiberacaoPeloAuditor(int contadorLiberacaoPeloAuditor) {
		this.contadorLiberacaoPeloAuditor = contadorLiberacaoPeloAuditor;
	}

	public int getContadorLiberacaoPeloMpps() {
		return contadorLiberacaoPeloMpps;
	}

	public void setContadorLiberacaoPeloMpps(int contadorLiberacaoPeloMpps) {
		this.contadorLiberacaoPeloMpps = contadorLiberacaoPeloMpps;
	}

	public Set<AssociacaoSeguradoPrograma> getAssociacaoSegurados() {
		return associacaoSegurados;
	}
	
	public void setAssociacaoSegurados(
			Set<AssociacaoSeguradoPrograma> associacaoSegurados) {
		this.associacaoSegurados = associacaoSegurados;
	}
	
	public Odontograma getOdontograma() {
		return odontograma;
	}

	public void setOdontograma(Odontograma odontograma) {
		this.odontograma = odontograma;
	}

	public Odontograma getOdontogramaCompleto() {
		return odontogramaCompleto;
	}

	public void setOdontogramaCompleto(Odontograma odontogramaCompleto) {
		this.odontogramaCompleto = odontogramaCompleto;
	}

	public AbstractSegurado() {
		this(null);
	}

	public AbstractSegurado (UsuarioInterface usuario) {
		this.pessoaFisica = new PessoaFisica();
		this.guias = new HashSet<GuiaSimples>();
		this.consumoIndividual = new ComponenteColecaoConsumos();
		this.associacaoSegurados = new HashSet<AssociacaoSeguradoPrograma>();
		this.mudarSituacao(usuario, SituacaoEnum.CADASTRADO.descricao(), MotivoEnum.CADASTRO_NOVO.getMessage(), new Date());
	}

	public ColecaoConsumosInterface getConsumoIndividual() {
		return consumoIndividual;
	}

	public void setConsumoIndividual(ColecaoConsumosInterface consumoIndividual) {
		this.consumoIndividual = consumoIndividual;
	}
	
	public String getContrato() {
		return contrato;
	}

	public void setContrato(String contrato) {
		this.contrato = contrato;
	}

	public Date getDataAdesao() {
		/* if[INICIO_CARENCIA_BASEADO_NO_CONTRATO]
		ContratoSR contratoAtual = getContratoAtual();
		if (contratoAtual != null){
			return contratoAtual.getDataEfetivacao();
		}
		return null;
		else[INICIO_CARENCIA_BASEADO_NO_CONTRATO]*/
	
		return dataAdesao;
		/* end[INICIO_CARENCIA_BASEADO_NO_CONTRATO]*/
	}

	public void setDataAdesao(Date dataAdesao) {
		this.dataAdesao = dataAdesao;
	}
	
	public Date getInicioDaCarencia() {
		/* if[INICIO_CARENCIA_BASEADO_NO_CONTRATO]
			ContratoSR contratoAtual = getContratoAtual();
			if (contratoAtual != null){
				Date dataEfetivacao = contratoAtual.getDataEfetivacao();
				if(dataEfetivacao != null){
					return dataEfetivacao;				
				}
			}
			
			return new Date();
		else[INICIO_CARENCIA_BASEADO_NO_CONTRATO]*/
		
		return inicioDaCarencia;
		/* end[INICIO_CARENCIA_BASEADO_NO_CONTRATO]*/
	}

	public void setInicioDaCarencia(Date inicioDaCarencia) {
		this.inicioDaCarencia = inicioDaCarencia;
	}

	public Set<GuiaSimples> getGuias() {
		return guias;
	}

	public Set<GuiaSimples> getGuias(Date competencia) {
		Set<GuiaSimples> guias = new HashSet<GuiaSimples>();
		for (GuiaSimples guia : this.getGuias()) {
			if(Utils.compararCompetencia(competencia, guia.getDataMarcacao()) == 0){
				guias.add(guia);
			}
		}
		return guias;
	}

	public Set<GuiaSimples> getGuiasAtendimentoEm(Date competencia) {
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);

		Calendar dataInicial = Calendar.getInstance();
		dataInicial.setLenient(false);
		dataInicial.setTime(competencia);
		dataInicial.set(Calendar.DAY_OF_MONTH, 1);

		Calendar dataFinal = Calendar.getInstance();
		dataFinal.setLenient(false);
		dataFinal.setTime(competencia);
		dataFinal.set(Calendar.DAY_OF_MONTH, dataFinal.getMaximum(Calendar.DAY_OF_MONTH));

		List<GuiaSimples> guias = HibernateUtil.currentSession().createCriteria(GuiaSimples.class)
				.add(Expression.ge("dataAtendimento", dataInicial.getTime()))
				.add(Expression.le("dataAtendimento", dataFinal.getTime()))
				.list();

		return new HashSet<GuiaSimples>(guias);
	}



	public BigDecimal getValorCoparticipacao(Date competencia){
		BigDecimal valorCoparticipacao = BigDecimal.ZERO;
		for (GuiaSimples guia : getGuias(competencia)) {
			valorCoparticipacao = valorCoparticipacao.add(guia.getValorCoParticipacao());
		}
		return valorCoparticipacao;
	}

	/**
	 * Retorna todas as guias do segurado que estão aptas a entrarem no cálculo <br>
	 * de coparticipação. A data de atendimento da guias deve estar entre as datas fornecidas.<br>
	 * como parâmetro.
	 * @param dataLimite
	 * @return as guias que entrarão no cálculo da coparticipação que estão antes da data passada como parâmetro
	 */
	public Set<GuiaSimples> getGuiasAptasAoCalculoDeCoparticipacao(Date dataLimite) {
		return this.getGuiasAptasAoCalculoDeCoparticipacao(null, dataLimite);
	}

	/**
	 * Retorna todas as guias do segurado que estão aptas a entrarem no cálculo <br>
	 * de coparticipação. A data de atendimento da guias deve ser menor do que a passada <br>
	 * como parâmetro.
	 * @param dataInicial, dataLimite
	 * @return as guias que entrarão no cálculo da coparticipação que estão no intervalo passado como parâmetro
	 */
	public Set<GuiaSimples> getGuiasAptasAoCalculoDeCoparticipacao(Date dataInicial, Date dataLimite) {
		Set<GuiaSimples> guias = new HashSet<GuiaSimples>();

		if (dataInicial == null)
			dataInicial = Utils.createData("01/01/1900");

		String[] situacoes = {SituacaoEnum.CONFIRMADO.descricao(),SituacaoEnum.FATURADA.descricao(),SituacaoEnum.AUDITADO.descricao()};

		Criteria criteria = HibernateUtil.currentSession().createCriteria(GuiaSimples.class);
		criteria.add(Expression.eq("segurado", this))
				.add(Expression.ge("dataAtendimento", dataInicial))
				.add(Expression.le("dataAtendimento", dataLimite))
				.add(Expression.isNull("fluxoFinanceiro"))
				.add(Expression.in("situacao.descricao",situacoes))
				.setFetchMode("prestador", FetchMode.SELECT);

		List<GuiaSimples> listGuias = criteria.list();
		guias.addAll(listGuias);

		return guias;

	}

	public List<GuiaSimples> getGuiasConfirmadasEFechadas(Date competencia)throws ValidateException {
		List<GuiaSimples> guiasConfirmadas = new ArrayList<GuiaSimples>();
		for (GuiaSimples guia : this.getGuias()) {
			if(guia.getDataAtendimento() != null){
				Date competenciaAtendimento = Utils.gerarCompetencia(Utils.format(guia.getDataAtendimento(), "MM/yyyy"));
				competencia = Utils.convert(competencia);
				competenciaAtendimento = Utils.convert(competenciaAtendimento);
				boolean isGuiaConfirmada = guia.isSituacaoAtual(SituacaoEnum.CONFIRMADO.descricao());
				boolean isGuiaFechada = guia.isSituacaoAtual(SituacaoEnum.FECHADO.descricao());
				if (((isGuiaConfirmada) || ((isGuiaFechada))) && (competenciaAtendimento.toString().equals(competencia.toString()))) {
					guiasConfirmadas.add(guia);
				}
			}
		}
		return guiasConfirmadas;
	}

	public void setGuias(Set<GuiaSimples> guias) {
		this.guias = guias;
	}

	public Long getIdSegurado() {
		return idSegurado;
	}

	public void setIdSegurado(Long idSegurado) {
		this.idSegurado = idSegurado;
	}

	public PessoaFisicaInterface getPessoaFisica() {
		return this.pessoaFisica;
	}

	public void setPessoaFisica(PessoaFisicaInterface pessoaFisica) {
		this.pessoaFisica = pessoaFisica;
	}

	public String getCodigoLegado() {
		return codigoLegado;
	}

	public void setCodigoLegado(String codigoLegado) {
		this.codigoLegado = codigoLegado;
	}

	public String getTipoDeSegurado() {
		if(this.isSeguradoTitular()) {
			return TipoSeguradoEnum.TITULAR.descricao();
		}else if(this.isSeguradoDependente()) {
			return TipoSeguradoEnum.DEPENDENTE.descricao();
		}else if(this.isSeguradoDependenteSuplementar()) {
			return TipoSeguradoEnum.DEPENDENTE_SUPLEMENTAR.descricao();
		}else {
			return TipoSeguradoEnum.PENSIONISTA.descricao();
		}
	}

	public Faixa getFaixa() {
		return Faixa.getFaixaPorIdade(pessoaFisica.getIdade());
	}

	public Faixa getFaixa(Date dataBase) {
		return Faixa.getFaixaPorIdade(pessoaFisica.getIdadeAt(dataBase));
	}

	public GrupoBC getGrupo() {
		return this.getTitular().getGrupo();
	}

	public boolean temLimite(Date data, GuiaSimples guia) throws Exception {
		verificarConsumo(guia, data, Periodo.ANUAL);
		verificarConsumo(guia, data, Periodo.SEMESTRAL);
		verificarConsumo(guia, data, Periodo.TRIMESTRAL);
		verificarConsumo(guia, data, Periodo.MENSAL);
		return true;
	}

	/**
	 * Cria um limite de consumo para o segurado se for sua primeira utilização e verifica se o segurado 
	 * está com algum limite de consumo estourado.
	 * @param <G>
	 * @param guia
	 * @param data
	 * @param periodo
	 * @throws Exception
	 */
	private <G extends GuiaSimples> void verificarConsumo(G guia, Date data, Periodo periodo) throws Exception {

		ConsumoInterface consumo = null;
		ColecaoConsumosInterface c = null;

		if(guia.isConsulta() || guia.isExame() || guia.isConsultaOdontoUrgencia()){

			if (this.getConsumoIndividual() == null){
				this.setConsumoIndividual(new ComponenteColecaoConsumos());
				ImplDAO.save(this);
			}
			
		}

		c = this.getConsumoIndividual();
		consumo = c.getConsumo(data, periodo);

		if (consumo == null){
			if(guia.isExame() && periodo.equals(Periodo.ANUAL)){
				if(periodo.getLimiteExames() < guia.getQuantidadeProcedimentosValidosParaConsumo()){
					throw new ConsumoException(MensagemErroEnum.SEGURADO_SEM_LIMITE_PARA_CONSUMO_DE_EXAMES.getMessage(String.valueOf(periodo.getLimiteExames()),"EXAMES"));
				}
			}else{
				if(guia.isExameOdonto()){
					if(periodo.getLimiteTratamentosOdonto() < guia.getQuantidadeProcedimentosValidosParaConsumo()){
						throw new ConsumoException(MensagemErroEnum.SEGURADO_SEM_LIMITE_PARA_CONSUMO_DE_EXAMES.getMessage(String.valueOf(periodo.getLimiteExames()), "TRATAMENTOS"));
					}
				}else{
					if(periodo.getLimiteExames() < guia.getQuantidadeProcedimentosValidosParaConsumo())
						throw new ConsumoException(MensagemErroEnum.SEGURADO_SEM_LIMITE_PARA_CONSUMO_DE_EXAMES.getMessage(String.valueOf(periodo.getLimiteExames()),"EXAMES"));
				}
			}
		}else if(consumo.isLimiteEstourado(guia)){
			throw new ConsumoException(MensagemErroEnum.SEGURADO_COM_LIMITE_ESTOURADO.getMessage(periodo.getChave()));
		}
	}

	public void atualizarLimites(GuiaSimples guia, TipoIncremento tipo, int numeroProcedimentos) throws Exception {
		if (guia.getLiberadaForaDoLimite() != TipoLiberacaoForaLimiteEnum.LIBERACAO_MPPS.codigo()){
			atualizarConsumo(guia, Periodo.MENSAL, tipo, numeroProcedimentos);
			atualizarConsumo(guia, Periodo.TRIMESTRAL, tipo,numeroProcedimentos);
			atualizarConsumo(guia, Periodo.SEMESTRAL, tipo,numeroProcedimentos);
			atualizarConsumo(guia, Periodo.ANUAL, tipo,numeroProcedimentos);
		}
	}

	public void decrementarLimites(GuiaSimples guiaCriada) throws Exception {
		if (guiaCriada.getLiberadaForaDoLimite() != TipoLiberacaoForaLimiteEnum.LIBERACAO_MPPS.codigo()){
			alterarConsumos(guiaCriada, Periodo.MENSAL, TipoIncremento.NEGATIVO);
			alterarConsumos(guiaCriada, Periodo.TRIMESTRAL, TipoIncremento.NEGATIVO);
			alterarConsumos(guiaCriada, Periodo.SEMESTRAL, TipoIncremento.NEGATIVO);
			alterarConsumos(guiaCriada, Periodo.ANUAL, TipoIncremento.NEGATIVO);
		}
	}

	public void incrementarLimites(GuiaSimples guiaCriada) throws Exception {
		if (guiaCriada.getLiberadaForaDoLimite() != TipoLiberacaoForaLimiteEnum.LIBERACAO_MPPS.codigo()){
			alterarConsumos(guiaCriada, Periodo.MENSAL, TipoIncremento.POSITIVO);
			alterarConsumos(guiaCriada, Periodo.TRIMESTRAL, TipoIncremento.POSITIVO);
			alterarConsumos(guiaCriada, Periodo.SEMESTRAL, TipoIncremento.POSITIVO);
			alterarConsumos(guiaCriada, Periodo.ANUAL, TipoIncremento.POSITIVO);
		}
	}	

	public abstract boolean isBeneficiario();

	public abstract boolean isCumpriuCarencia(Integer carencia) throws ValidateException;
	public abstract boolean isSeguradoTitular();
	public abstract boolean isSeguradoPensionista();
	public abstract boolean isSeguradoDependente();
	public abstract boolean isSeguradoDependenteSuplementar();

	public void tocarObjetos() {
		this.pessoaFisica.getNome();
		if (this.pessoaFisica.getEndereco() != null){
			this.pessoaFisica.getEndereco().tocarObjetos();
		}
		this.getSituacao().getDescricao();

		if (this.getConsumoIndividual() != null){
			this.getConsumoIndividual().getConsumos().size();
		}
		if (this.getAssociacaoSegurados() != null) {
			this.getAssociacaoSegurados().size();
		}
		Set<AssociacaoSeguradoPrograma> associacaoSegurados2 = getAssociacaoSegurados();
		if(associacaoSegurados2 != null){
			for (AssociacaoSeguradoPrograma associacaoSeguradoPrograma : associacaoSegurados2) {
				associacaoSeguradoPrograma.getPrograma().getSituacao();
				associacaoSeguradoPrograma.getPrograma().getMedicos().size();
				Set<ProfissionalEspecialidade> medicos = associacaoSeguradoPrograma.getPrograma().getMedicos();
				for (ProfissionalEspecialidade profissionalEspecialidade : medicos) {
					profissionalEspecialidade.getEspecialidade().tocarObjetos();
					profissionalEspecialidade.getProfissional().tocarObjetos();
				}
			}
		}
	}

	public BigDecimal getValorIndividual() {
		return this.getTitular().getGrupo().getValor(this, new Date());
	}

	public BigDecimal getValorIndividual(Date date) {
		return this.getTitular().getGrupo().getValor(this, date);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof SeguradoInterface)) {
			return false;
		}
		SeguradoInterface otherObject = (SeguradoInterface) object;
		return new EqualsBuilder()
		.append(this.idSegurado, otherObject.getIdSegurado())
		.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(1308791639, 309108973)
		.appendSuper(super.hashCode())
		.append(this.idSegurado)
		.toHashCode();
	}

	//TODO refatorar este método
	private void atualizarConsumo(GuiaSimples guia,Periodo periodo,TipoIncremento tipo, int numeroProcedimentos) throws Exception {
		boolean guiaFechada 		= guia.isSituacaoAtual(SituacaoEnum.FECHADO.descricao());
		boolean guiaAuditada 		= guia.isSituacaoAtual(SituacaoEnum.AUDITADO.descricao());
		boolean guiaAgendada 		= guia.isSituacaoAtual(SituacaoEnum.AGENDADA.descricao());
		boolean guiaConfirmada 		= guia.isSituacaoAtual(SituacaoEnum.CONFIRMADO.descricao());
		boolean guiaSolicitada 		= guia.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao());
		boolean guiaAutorizada 		= guia.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao());
		boolean guiaNaoAutorizada 	= guia.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao());

		Date dataConsumo = guia.getDataAtendimento() != null ? guia.getDataAtendimento() : new Date();
		ConsumoInterface consumoAtual = this.getConsumoIndividual().getConsumo(dataConsumo, periodo);

		if(this.getConsumoIndividual() == null){
			this.setConsumoIndividual(new ComponenteColecaoConsumos());
			ImplDAO.save(this);
		}

		if (consumoAtual == null){
			Date dataAtend = (guia.getDataAtendimento() == null) ? new Date() : guia.getDataAtendimento(); 

			consumoAtual = this.getConsumoIndividual().criarConsumo(dataAtend, periodo, periodo.getLimiteConsultas(), periodo.getLimiteExames(), 
					periodo.getLimiteConsultasOdonto(), periodo.getLimiteTratamentosOdonto());

			consumoAtual.setDescricao(this.idSegurado + "-" + this.getPessoaFisica().getNome());
		}

		BigDecimal valor = TipoIncremento.POSITIVO.equals(tipo) ? new BigDecimal(numeroProcedimentos)
		: new BigDecimal(numeroProcedimentos)
		.multiply(BigDecimal.valueOf(Double.valueOf(-1)));
		if(guia.isUrgencia()){
			if (guiaFechada || guiaAuditada){
				consumoAtual.atualizaSomatorioUrgenciasFechadas(valor, numeroProcedimentos,tipo);
				if(guia.isConsultaUrgencia() || guia.isAtendimentoUrgencia()){
					consumoAtual.atualizaSomatorioConsultasAtendimentosUrgenciasFechadas(valor, numeroProcedimentos, tipo);
				}
			}
			else{
				consumoAtual.atualizaSomatorioUrgenciasAbertas(valor, numeroProcedimentos,tipo);
				if(guia.isConsultaUrgencia() || guia.isAtendimentoUrgencia()){
					consumoAtual.atualizaSomatorioConsultasAtendimentosUrgenciasAbertas(valor, numeroProcedimentos, tipo);
				}
			}
		}

		if(guia.isInternacao()){
			if (guiaFechada || guiaAuditada)
				consumoAtual.atualizaSomatorioInternacoesFechadas(valor, numeroProcedimentos,tipo);
			else
				consumoAtual.atualizaSomatorioInternacoesAbertas(valor, numeroProcedimentos,tipo);
		}

		if(guia.isExame() && !guia.isExameExterno()){
			if(guiaConfirmada)
				consumoAtual.atualizaSomatorioExamesConfirmados(valor,numeroProcedimentos,tipo);
			else if (guiaAgendada || guiaSolicitada || guiaAutorizada || guiaNaoAutorizada)
				consumoAtual.atualizaSomatorioExamesAgendados(valor,numeroProcedimentos,tipo);
		}

	}

	//TODO refatorar este método
	private void alterarConsumos(GuiaSimples guia, Periodo periodo, TipoIncremento tipo) throws Exception {
		ConsumoInterface consumoAtual;

		if(this.getConsumoIndividual() == null){
			this.setConsumoIndividual(new ComponenteColecaoConsumos());
			ImplDAO.save(this);
		}
		Date dataConsumo = guia.getDataAtendimento() != null ? guia.getDataAtendimento() : new Date();
		consumoAtual = this.getConsumoIndividual().getConsumo(dataConsumo, periodo);

		if (consumoAtual == null){
			Date dataAtend = (guia.getDataAtendimento() == null) ? new Date() : guia.getDataAtendimento(); 

			consumoAtual = this.getConsumoIndividual().criarConsumo(dataAtend, periodo, periodo.getLimiteConsultas(), periodo.getLimiteExames(), 
					periodo.getLimiteConsultasOdonto(), periodo.getLimiteTratamentosOdonto());

			consumoAtual.setDescricao(this.idSegurado + "-" + this.getPessoaFisica().getNome());
		}

		int valor = 0;

		SituacaoInterface situacaoAtual = guia.getSituacao();
		if (SituacaoEnum.CANCELADO.descricao().equals(situacaoAtual.getDescricao()))
			situacaoAtual = guia.getSituacaoAnterior(situacaoAtual);

		if (guia.isConsulta()) {
			valor = TipoIncremento.POSITIVO.equals(tipo) ? 1 : -1;

			if(guia.isConsultaOdonto()){
				GuiaConsultaOdonto guiaOdonto = (GuiaConsultaOdonto) guia;
				
				// apenas guias de consulta odonto normal, alteram o consumo do beneficiario. 
				boolean isConsultaOdontoNormalParaConsumo = guiaOdonto.isConsultaOdontoNormal() && (guiaOdonto.isSituacaoAtual(SituacaoEnum.AGENDADA.descricao()) || guiaOdonto.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()));

				if(isConsultaOdontoNormalParaConsumo)
					consumoAtual.incrementaSomatorioConsultasOdontoConfirmadas(new BigDecimal(valor), guia.getDataMarcacao(),tipo);
			}else{
				if (situacaoAtual.getDescricao().equals(SituacaoEnum.CONFIRMADO.descricao()))
					consumoAtual.incrementaSomatorioConsultasConfirmadas(new BigDecimal(valor),tipo, dataConsumo, true);
				else
					consumoAtual.incrementaSomatorioConsultasAgendadas(new BigDecimal(valor),tipo);
			}
		}

		if (guia.isExame() && !guia.isExameExterno()) {
			valor = TipoIncremento.POSITIVO.equals(tipo) ? guia.getQuantidadeProcedimentosValidosParaConsumo() : -1 * guia.getQuantidadeProcedimentosValidosParaConsumo();

			boolean decrementaExamesAgendados = false;

			if(guia.isExameOdonto()){
				if (situacaoAtual.getDescricao().equals(SituacaoEnum.CONFIRMADO.descricao()))
					consumoAtual.incrementaSomatorioTratamentosOdontoConfirmados(new BigDecimal(valor),tipo, guia.getQuantidadeProcedimentosValidosParaConsumo(),guia.getDataMarcacao(),true);
				else
					consumoAtual.incrementaSomatorioTratamentosOdontoAgendados(new BigDecimal(valor), guia.getQuantidadeProcedimentosValidosParaConsumo(),tipo);
			}
			else{
				if (situacaoAtual.getDescricao().equals(SituacaoEnum.CONFIRMADO.descricao()))
					consumoAtual.incrementaSomatorioExamesConfirmados(new BigDecimal(valor), guia.getQuantidadeProcedimentosValidosParaConsumo(),tipo,guia.getDataMarcacao(),guia.isFromPrestador(), decrementaExamesAgendados);
				else
					consumoAtual.incrementaSomatorioExamesAgendados(new BigDecimal(valor), guia.getQuantidadeProcedimentosValidosParaConsumo(),tipo);
			}
		}

		if (guia.isUrgencia()){
			if(guia.isConsultaOdontoUrgencia()){


			}else{

				valor = TipoIncremento.POSITIVO.equals(tipo) ? guia.getQuantidadeProcedimentosValidosParaConsumo() : -1 * guia.getQuantidadeProcedimentosValidosParaConsumo();
				BigDecimal valorPreFechamento = new BigDecimal(((GuiaCompleta)guia).getNumeroAnteriorProcedimentosGuia());
				int numeroProcedimentosPreFechamento =  ((GuiaCompleta)guia).getNumeroAnteriorProcedimentosGuia();

				if (situacaoAtual.getDescricao().equals(SituacaoEnum.FECHADO.descricao())){
					consumoAtual.incrementaSomatorioUrgenciasFechadas(valorPreFechamento, numeroProcedimentosPreFechamento,new BigDecimal(valor), guia.getQuantidadeProcedimentosValidosParaConsumo(),tipo);
					if(guia.isConsultaUrgencia() || guia.isAtendimentoUrgencia()){
						consumoAtual.incrementaSomatorioConsultasAtendimentosUrgenciasFechadas(valorPreFechamento, numeroProcedimentosPreFechamento,new BigDecimal(valor), guia.getQuantidadeProcedimentosValidosParaConsumo(),tipo);
					}
				}
				else{
					consumoAtual.incrementaSomatorioUrgenciasAbertas(new BigDecimal(valor),guia.getQuantidadeProcedimentosValidosParaConsumo(),tipo);
					if(guia.isConsultaUrgencia() || guia.isAtendimentoUrgencia()){
						consumoAtual.incrementaSomatorioConsultasAtendimentosUrgenciasAbertas(valorPreFechamento, numeroProcedimentosPreFechamento, tipo);
					}
				}
			}
		}

		if (guia.isInternacao()){
			valor = TipoIncremento.POSITIVO.equals(tipo) ? guia.getQuantidadeProcedimentosValidosParaConsumo() : -1 * guia.getQuantidadeProcedimentosValidosParaConsumo();
			BigDecimal valorPreFechamento = new BigDecimal(((GuiaCompleta)guia).getNumeroAnteriorProcedimentosGuia());
			int numeroProcedimentosPreFechamento =  ((GuiaCompleta)guia).getNumeroAnteriorProcedimentosGuia();

			if (situacaoAtual.getDescricao().equals(SituacaoEnum.FECHADO.descricao())){
				consumoAtual.incrementaSomatorioInternacoesFechadas(valorPreFechamento, numeroProcedimentosPreFechamento,new BigDecimal(valor), guia.getQuantidadeProcedimentosValidosParaConsumo(),tipo);
			}
			else
				consumoAtual.incrementaSomatorioInternacoesAbertas(new BigDecimal(valor), guia.getQuantidadeProcedimentosValidosParaConsumo(),tipo);
		}

	}

	public int getIdade() {
		return this.pessoaFisica.getIdade();
	}

	public int getSexo() {
		return this.pessoaFisica.getSexo();
	}

	public boolean isPacienteCronico() {
		return pacienteCronico;
	}

	public void setPacienteCronico(boolean pacienteCronico) {
		this.pacienteCronico = pacienteCronico;
	}

	public Set<RegistroPacientesCronicos> getRegistrosPacientesCronicos() {
		return registrosPacientesCronicos;
	}

	public void setRegistrosPacientesCronicos(
			Set<RegistroPacientesCronicos> registrosPacientesCronicos) {
		this.registrosPacientesCronicos = registrosPacientesCronicos;
	}

	public <G extends GuiaSimples> GuiaSimples getUltimaGuia(Prestador prestador, Class<G> klassGuia, SituacaoEnum... situacoesGuia) {
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);

		SearchAgent sa = new SearchAgent();
		sa.clearAllParameters();

		if (prestador != null)
			sa.addParameter(new Equals("prestador", prestador));

		Criteria criteria = sa.createCriteriaFor(klassGuia);
		java.sql.Date ultimaDataAtendimento = (java.sql.Date)criteria.setProjection(Projections.max("dataAtendimento")).uniqueResult();

		sa.addParameter(new Equals("dataAtendimento", ultimaDataAtendimento));
		sa.addParameter(new NotEquals("situacao.descricao", SituacaoEnum.CANCELADO.descricao()));
		addParameterEquals(sa);

		if (situacoesGuia.length > 0){
			Object[] situacoes = new Object[situacoesGuia.length];
			for (int i = 0; i < situacoes.length; i++)
				situacoes[i] = situacoesGuia[i].descricao();
			sa.addParameter(new In("situacao.descricao", situacoes));
		}

		//Criteria critaria = sa.createCriteriaFor(klassGuia);
		List<GuiaSimples> guias = sa.createCriteriaFor(klassGuia).list();
		if(guias.isEmpty())
			return null;

		return (GuiaSimples) guias.get(0);
	}

	public void addParameterEquals(SearchAgent sa) {
		sa.addParameter(new Equals("segurado",this));
	}

	public void addParameterEquals(Criteria criteria) {
		criteria.add(Expression.eq("segurado", this));
	}

	public abstract boolean isInternado();


	public SituacaoInterface getSituacaoAnterior(SituacaoInterface situacao) {
		int ordemSituacao = situacao.getOrdem();
		if (ordemSituacao == 1)
			return null;

		for (SituacaoInterface situacaoAtual : this.getSituacoes()) {
			if (situacaoAtual.getOrdem() == ordemSituacao - 1)
				return situacaoAtual;
		}
		return situacao;
	}

	/**
	 * Retorna as guias de tratamento seriado realizadas por um seguado
	 * */
	public Set<GuiaSimples> getGuiasTratamentoSeriado(){
		Set<GuiaSimples> guiasTratamentoSeriado = this.getGuias();
		for (GuiaSimples guiaSimples : guiasTratamentoSeriado) {
			if(guiaSimples.isTratamentoSeriado()){
				guiasTratamentoSeriado.add(guiaSimples);
			}
		}
		System.out.println("Qtd de GTS: "+guiasTratamentoSeriado.size());
		return guiasTratamentoSeriado;
	}
	public List<GuiaSimples> getGuiasInternacao(){

		List<GuiaSimples> guiasInternacao = new ArrayList<GuiaSimples>();
		for (GuiaSimples guia : this.getGuias()) {
			if (guia.isInternacao()) {
				guiasInternacao.add(guia);
			}
		}

		return guiasInternacao;
	}

	public List<GuiaSimples> getGuiasInternacaoNaoFaturadas(){

		List<String> situacoesNaoConsideradas = Arrays.asList(SituacaoEnum.FATURADA.descricao(), 
				SituacaoEnum.PAGO.descricao(), SituacaoEnum.CANCELADO.descricao(), 
				SituacaoEnum.SOLICITADO_INTERNACAO.descricao(), SituacaoEnum.INAPTO.descricao());

		List<GuiaSimples> guiasInternacao = new ArrayList<GuiaSimples>();
		for (GuiaSimples guia : this.getGuias()) {
			boolean guiaDentroDasSituacoesPermitidas = situacoesNaoConsideradas.contains(guia.getSituacao().getDescricao());
			if (guia.isInternacao() && !guiaDentroDasSituacoesPermitidas) {
				guiasInternacao.add(guia);
			}
		}

		return guiasInternacao;
	}

	public Set<ContratoSR> getContratos() {
		return contratos;
	}

	public void setContratos(Set<ContratoSR> contratos) {
		this.contratos = contratos;
	}

	public ContratoSR getContratoAtual() {
		if (this.getContratos() == null || this.getContratos().isEmpty()) {
			return null;
		} else {
			ContratoSR ultimoContrato = null;
			for (ContratoSR c : this.getContratos()) {
				if (ultimoContrato == null) {
					ultimoContrato = c;
				}
				if (c.getIdContrato() == null || c.getIdContrato().compareTo(ultimoContrato.getIdContrato()) > 0) {
					ultimoContrato = c;
				}
			}
			return ultimoContrato;
		}
	}
	
	public abstract Questionario getQuestionario();
	public abstract void setQuestionario(Questionario questionario);
	
	public boolean isRegistraInternacaoDeObcervacaoParaDependenteEmCarencia() throws ValidateException {
		return !this.isCumpriuCarencia(180);
	}
}
