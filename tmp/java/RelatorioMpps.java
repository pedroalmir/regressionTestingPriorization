package br.com.infowaypi.ecare.programaPrevencao.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.infowaypi.ecare.enums.TipoLiberacaoForaLimiteEnum;
import br.com.infowaypi.ecare.programaPrevencao.AssociacaoSeguradoPrograma;
import br.com.infowaypi.ecare.programaPrevencao.ProgramaDePrevencao;
import br.com.infowaypi.ecare.programaPrevencao.fluxos.ResumoBeneficiarioMpps;
import br.com.infowaypi.ecare.programaPrevencao.fluxos.ResumoGeralMpps;
import br.com.infowaypi.ecare.validacao.services.ValidatorDataDoProgramaRelatorioMPPS;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;
/**
 * Relatório do módulo de prevenção à saúde , que faz o levantamento das guias dos sergurados associados ao longo do programa de prevenção.
 * @author jefferson
 *
 */
public class RelatorioMpps {

	Set<GuiaSimples> guiasDosSegurados = new HashSet<GuiaSimples>();

	Map<AbstractSegurado, ResumoBeneficiarioMpps> mapResumosBeneficiarios = new HashMap<AbstractSegurado, ResumoBeneficiarioMpps>();

	List<String> situacoes = Arrays.asList(SituacaoEnum.AGENDADA.descricao(), 
			SituacaoEnum.CONFIRMADO.descricao(), SituacaoEnum.ABERTO.descricao(),
			SituacaoEnum.FECHADO.descricao(), SituacaoEnum.AUDITADO.descricao(),
			SituacaoEnum.FATURADA.descricao(), SituacaoEnum.PAGO.descricao(), 
			SituacaoEnum.AUTORIZADO.descricao(), SituacaoEnum.ENVIADO.descricao(),
			SituacaoEnum.RECEBIDO.descricao(),SituacaoEnum.DEVOLVIDO.descricao(), 
			SituacaoEnum.SOLICITADO.descricao());

	public ResumoGeralMpps executarRelatorio(ProgramaDePrevencao programa,String datainicial, String dataFinal) throws ValidateException{

		ResumoGeralMpps resumo = construirRelarotioGeralMpps(programa,datainicial,dataFinal);

		return resumo;
	}

	private ResumoGeralMpps construirRelarotioGeralMpps(ProgramaDePrevencao programa, String dataInicial, String dataFinal) throws ValidateException {

		Date dtInicial = Utils.createData(dataInicial);

		Date dtFinal = Utils.createData(dataFinal);

		List<AbstractSegurado> seguradosAssociados = extrairSeguradosAssociados(programa);
		
		ValidatorDataDoProgramaRelatorioMPPS validator = new ValidatorDataDoProgramaRelatorioMPPS();
		validator.verificaDataDoProgramaRelatorioMPPS(dtInicial, dtFinal, dataFinal, programa);
		
		BigDecimal quantidadeDeGuiasDeConsulta  = calcularQuantidadeDeGuiasDeConsulta(seguradosAssociados,programa.getInicio(), dtInicial,dtFinal);

		BigDecimal quantidadeConcultasDeUrgencia  = calcularQuantidadeDeConsultasDeUrgencia(seguradosAssociados,programa.getInicio(),dtInicial,dtFinal);

		BigDecimal quantidadeDeinternacoes = calcularQuantidadeDeIntercacoes(seguradosAssociados,programa.getInicio(),dtInicial,dtFinal);

		BigDecimal quantidadeDeCoParticipacoesLiberadas = calcularQuantidadeDeCoparticipacoesLiberadas(seguradosAssociados,programa.getInicio(),dtInicial,dtFinal);

		BigDecimal quantidadeLimitesUltrapassadpos = calcularLimitesUltrapassados(seguradosAssociados,programa.getInicio(),dtInicial,dtFinal);

		ResumoGeralMpps resumo = processarResultados(quantidadeDeGuiasDeConsulta,quantidadeConcultasDeUrgencia,quantidadeDeinternacoes,quantidadeDeCoParticipacoesLiberadas,quantidadeLimitesUltrapassadpos,mapResumosBeneficiarios);

		return resumo;
	}

	private BigDecimal calcularQuantidadeDeIntercacoes(List<AbstractSegurado> seguradosAssociados, Date inicio,Date dtInicial, Date dtFinal) throws ValidateException {

		BigDecimal counterGeral = BigDecimal.ZERO;

		for (AbstractSegurado abstractSegurado : seguradosAssociados) {
           
			Set<GuiaSimples> guias = abstractSegurado.getGuias();

			BigDecimal valor = pesquisarSobreGuiasDeinternacao(inicio, dtInicial, dtFinal, guias);

			criarMapaSobrePesquisaDeGuiasDeInteracao(abstractSegurado, valor);

			counterGeral =counterGeral.add(valor);
		}

		return counterGeral;
	}

	private void criarMapaSobrePesquisaDeGuiasDeInteracao(AbstractSegurado abstractSegurado,
			BigDecimal counterSegurado) {
		if (mapResumosBeneficiarios.containsKey(abstractSegurado)){
			ResumoBeneficiarioMpps resumo = mapResumosBeneficiarios.get(abstractSegurado);
			resumo.getResumoSegurado().setnInteracoes(counterSegurado);
		}else {
			ResumoBeneficiarioMpps resumo = new ResumoBeneficiarioMpps();
			resumo.setIdResumoBeneficiarioMpps(abstractSegurado.getIdSegurado());
			resumo.setSegurado(abstractSegurado);
			resumo.getResumoSegurado().setnInteracoes(counterSegurado);
			mapResumosBeneficiarios.put(abstractSegurado, resumo);
		}
	}

	private BigDecimal pesquisarSobreGuiasDeinternacao(Date inicio, Date dtInicial, Date dtFinal,Set<GuiaSimples> guias) throws ValidateException {

		BigDecimal counterSegurado = BigDecimal.ZERO;
		
		for (GuiaSimples guiaSimples : guias) {
			
			if (guiaSimples.isInternacao() && isGuiaNormal(guiaSimples) ){
				if (verificarData(inicio,dtInicial, dtFinal,guiaSimples)){
					counterSegurado  = addCounter(counterSegurado);
				}
			}
		}
		return counterSegurado;
	}

	private ResumoGeralMpps processarResultados(
			BigDecimal quantidadeDeGuiasDeConsulta,
			BigDecimal quantidadeConcultasDeUrgencia,
			BigDecimal quantidadeDeinternacoes,
			BigDecimal quantidadeDeCoParticipacoesLiberadas,
			BigDecimal quantidadeLimitesUltrapassadpos,
			Map<AbstractSegurado,
			ResumoBeneficiarioMpps> mapResumosBeneficiarios) {

		ResumoGeralMpps resumo  = new ResumoGeralMpps();

		resumo.setnConsultasRealizadasPeloPrograma(quantidadeDeGuiasDeConsulta);
		resumo.setnConsultasDeUrgencia(quantidadeConcultasDeUrgencia);
		resumo.setnInteracoes(quantidadeDeinternacoes);
		resumo.setnCoparticipacoesLiberadas(quantidadeDeCoParticipacoesLiberadas);
		resumo.setnLimitesUltrapassadosDePacientesInscritosEmProgramas(quantidadeLimitesUltrapassadpos);

		Set<AbstractSegurado> segurados = mapResumosBeneficiarios.keySet();
		for (AbstractSegurado abstractSegurado : segurados) {
			ResumoBeneficiarioMpps resumoBeneficiarioMpps = mapResumosBeneficiarios.get(abstractSegurado);
			resumo.getResumosBeneficiarios().add(resumoBeneficiarioMpps);
		}

		return resumo;
	}

	private BigDecimal calcularQuantidadeDeCoparticipacoesLiberadas(List<AbstractSegurado> seguradosAssociados,Date dtInicialPrograma, Date dtInicial,	Date dtFinal) throws ValidateException {

		BigDecimal counter = BigDecimal.ZERO;

		for (AbstractSegurado abstractSegurado : seguradosAssociados) {

			Set<GuiaSimples> guias = abstractSegurado.getGuias();

			BigDecimal valor =  pesquisarGuiasComCoPartciacoeLiberadas(dtInicialPrograma, dtInicial, dtFinal,guias);

			criarMapaDaPesquisaDeCoParticipacoes(abstractSegurado, valor);

			counter = counter.add(valor);
		}

		return counter;
	}

	private void criarMapaDaPesquisaDeCoParticipacoes(AbstractSegurado abstractSegurado,BigDecimal counterSegurado) {

		if (mapResumosBeneficiarios.containsKey(abstractSegurado)){

			ResumoBeneficiarioMpps resumo = mapResumosBeneficiarios.get(abstractSegurado);
			resumo.getResumoSegurado().setnCoparticipacoesLiberadas(counterSegurado);

		}else {

			ResumoBeneficiarioMpps resumo = new ResumoBeneficiarioMpps();
			resumo.setIdResumoBeneficiarioMpps(abstractSegurado.getIdSegurado());
			resumo.setSegurado(abstractSegurado);
			resumo.getResumoSegurado().setnCoparticipacoesLiberadas(counterSegurado);
			mapResumosBeneficiarios.put(abstractSegurado, resumo);

		}
	}

	private void criarMapaDaPesquisaDeGuiasComLimitesUltrapassados(AbstractSegurado abstractSegurado,
			BigDecimal counterSegurado) {
		if (mapResumosBeneficiarios.containsKey(abstractSegurado)){

			ResumoBeneficiarioMpps resumo = mapResumosBeneficiarios.get(abstractSegurado);
			resumo.getResumoSegurado().setnLimitesUltrapassadosDePacientesInscritosEmProgramas(counterSegurado);

		}else {

			ResumoBeneficiarioMpps resumo = new ResumoBeneficiarioMpps();
			resumo.setIdResumoBeneficiarioMpps(abstractSegurado.getIdSegurado());
			resumo.setSegurado(abstractSegurado);
			resumo.getResumoSegurado().setnLimitesUltrapassadosDePacientesInscritosEmProgramas(counterSegurado);
			mapResumosBeneficiarios.put(abstractSegurado, resumo);

		}
	}



	private BigDecimal pesquisarGuiasComCoPartciacoeLiberadas(Date dtInicialPrograma, Date dtInicial,Date dtFinal, Set<GuiaSimples> guias) throws ValidateException {
		BigDecimal counterSegurado = BigDecimal.ZERO;

		for (GuiaSimples guiaSimples : guias) {

			if ( verificarData(dtInicialPrograma,dtInicial, dtFinal,guiaSimples)){
				if (guiaSimples.isCoParticipacaoLiberada()  && isGuiaNormal(guiaSimples)){
					counterSegurado = addCounter(counterSegurado);
				}
			}
		}
		return counterSegurado;
	}

	private BigDecimal calcularLimitesUltrapassados(List<AbstractSegurado> seguradosAssociados,Date dtInicialPrograma, Date dtInicial, Date dtFinal) throws ValidateException {

		BigDecimal counter = BigDecimal.ZERO;

		for (AbstractSegurado abstractSegurado : seguradosAssociados) {

			Set<GuiaSimples> guias = abstractSegurado.getGuias();

			BigDecimal valor =  pesquisarGuiasComLimitesUltrapassados(dtInicialPrograma, dtInicial, dtFinal,guias);

			criarMapaDaPesquisaDeGuiasComLimitesUltrapassados(abstractSegurado, valor);

			counter = counter.add(valor);
		}

		return counter;
	}

	private BigDecimal pesquisarGuiasComLimitesUltrapassados(Date dtInicialPrograma, Date dtInicial, Date dtFinal,Set<GuiaSimples> guias) throws ValidateException {
		BigDecimal counterGuia = BigDecimal.ZERO;
		for (GuiaSimples guiaSimples : guias) {
			boolean isLiberadoAuditor = (guiaSimples.getLiberadaForaDoLimite().intValue() == TipoLiberacaoForaLimiteEnum.LIBERACAO_AUDITOR.codigo().intValue());
			boolean isLiberadoMpps = (guiaSimples.getLiberadaForaDoLimite().intValue() == TipoLiberacaoForaLimiteEnum.LIBERACAO_MPPS.codigo().intValue());
			if (isGuiaNormal(guiaSimples) && (isLiberadoAuditor || isLiberadoMpps)){
				if (verificarData(dtInicialPrograma,dtInicial, dtFinal,guiaSimples)){
					counterGuia = addCounter(counterGuia);
				}
			}
		}
		return counterGuia;
	}

	private boolean isGuiaNormal(GuiaSimples guiaSimples) {
		return  situacoes.contains(guiaSimples.getSituacao().getDescricao());
	}

	private BigDecimal calcularQuantidadeDeConsultasDeUrgencia(List<AbstractSegurado> seguradosAssociados,Date dtInicialPrograma, Date dtInicial,Date dtFinal) throws ValidateException {

		BigDecimal counterGeral = BigDecimal.ZERO;

		for (AbstractSegurado abstractSegurado : seguradosAssociados) {

			Set<GuiaSimples> guias = abstractSegurado.getGuias();

			BigDecimal valor = pesquisaGuiasDeConsultaDeUrgencia(dtInicialPrograma, dtInicial, dtFinal, guias);
			
			construirMapaSobrePesquisaDeConsultasDeUrgencia(abstractSegurado, valor);

			counterGeral =counterGeral.add(valor);
		}

		return counterGeral;
	}

	private void construirMapaSobrePesquisaDeConsultasDeUrgencia(AbstractSegurado abstractSegurado,
			BigDecimal counterSegurado) {
		if (mapResumosBeneficiarios.containsKey(abstractSegurado)){
			ResumoBeneficiarioMpps resumo = mapResumosBeneficiarios.get(abstractSegurado);
			resumo.getResumoSegurado().setnConsultasDeUrgencia(counterSegurado);
		}else {
			ResumoBeneficiarioMpps resumo = new ResumoBeneficiarioMpps();
			resumo.setIdResumoBeneficiarioMpps(abstractSegurado.getIdSegurado());
			resumo.setSegurado(abstractSegurado);
			resumo.getResumoSegurado().setnConsultasDeUrgencia(counterSegurado);
			mapResumosBeneficiarios.put(abstractSegurado, resumo);
		}
	}

	private BigDecimal pesquisaGuiasDeConsultaDeUrgencia(Date dtInicialPrograma, Date dtInicial,Date dtFinal, Set<GuiaSimples> guias) throws ValidateException {
		BigDecimal counterSegurado = BigDecimal.ZERO;

		for (GuiaSimples guiaSimples : guias) {

			boolean isConcultaDeUrgencia = guiaSimples.isConsultaUrgencia();

			if (isConcultaDeUrgencia && isGuiaNormal(guiaSimples)){
				if (verificarData(dtInicialPrograma,dtInicial, dtFinal,guiaSimples)){
					counterSegurado = addCounter(counterSegurado);
				}
			}
		}
		return counterSegurado;
	}

	private BigDecimal calcularQuantidadeDeGuiasDeConsulta(List<AbstractSegurado> seguradosAssociados, Date dtInicioPrograma, Date dtInicial, Date dtFinal) throws ValidateException {

		BigDecimal counter = BigDecimal.ZERO;

		for (AbstractSegurado abstractSegurado : seguradosAssociados) {
			
			Set<GuiaSimples> guias = abstractSegurado.getGuias();
            
			BigDecimal valor = pesquisaGuiasDeConsulta(dtInicioPrograma, dtInicial, dtFinal, guias);
			
			construirMapaParaPesquisaDeConsultas(abstractSegurado, valor);

			counter =counter.add(valor);
		}
		return counter;
	}

	private void construirMapaParaPesquisaDeConsultas(AbstractSegurado abstractSegurado,BigDecimal counterGuias) {

		if (mapResumosBeneficiarios.containsKey(abstractSegurado)){

			ResumoBeneficiarioMpps resumo = mapResumosBeneficiarios.get(abstractSegurado);
			resumo.getResumoSegurado().setnConsultasRealizadasPeloPrograma(counterGuias);

		}else {
			ResumoBeneficiarioMpps resumo = new ResumoBeneficiarioMpps();
			resumo.setIdResumoBeneficiarioMpps(abstractSegurado.getIdSegurado());
			resumo.setSegurado(abstractSegurado);
			resumo.getResumoSegurado().setnConsultasRealizadasPeloPrograma(counterGuias);
			mapResumosBeneficiarios.put(abstractSegurado, resumo);

		}
	}

	private BigDecimal pesquisaGuiasDeConsulta(Date dtInicioPrograma, Date dtInicial, Date dtFinal,Set<GuiaSimples> guias) throws ValidateException {
		BigDecimal counterGuias = BigDecimal.ZERO;
		for (GuiaSimples guiaSimples : guias) {
			boolean isConsulta = guiaSimples.isConsultaEletiva();
			if(isConsulta && isGuiaNormal(guiaSimples)){
				if (verificarData(dtInicioPrograma,dtInicial, dtFinal,guiaSimples)){
					counterGuias = addCounter(counterGuias);
				}
			}
		}
		return counterGuias;
	}

	private BigDecimal addCounter(BigDecimal counter) {
		return counter =counter.add(BigDecimal.ONE);
	}

	private boolean verificarData(Date dtInicialPrograma, Date dtInicial, Date dtFinal, GuiaSimples guiaSimples) throws ValidateException {
		Date dataAtendimento = guiaSimples.getDataAtendimento();
		Date dataFinal;
		if (dataAtendimento != null){
			if (dtFinal == null) {
				dataFinal = new Date();
			} else {
				dataFinal = dtFinal;
			}
			boolean acimaDaDataInicialDoPrograma = Utils.compareData(dataAtendimento, dtInicialPrograma) >= 0;
			boolean dentroDoPeriodoDebusca = Utils.between(dtInicial, dataFinal, dataAtendimento);
			if (acimaDaDataInicialDoPrograma && dentroDoPeriodoDebusca){
				return true;
			}
		}

		return false;
	}

	private List<AbstractSegurado> extrairSeguradosAssociados(ProgramaDePrevencao programaDePrevencao) {

		List<AbstractSegurado> seguradosAssociados = new ArrayList<AbstractSegurado>();

		Set<AssociacaoSeguradoPrograma> associacaoSegurados = programaDePrevencao.getAssociacaoSeguradosRegularizados();

		for (AssociacaoSeguradoPrograma associacaoSeguradoPrograma : associacaoSegurados) {
			seguradosAssociados.add(associacaoSeguradoPrograma.getSegurado());
		}
		return seguradosAssociados;
	}

}
