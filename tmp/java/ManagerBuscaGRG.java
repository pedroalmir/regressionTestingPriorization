package br.com.infowaypi.ecare.services.recurso;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.molecular.parameter.LowerEquals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe responsável pelas buscas de GRGs.
 * @author Luciano Rocha
 * @since 23/11/2012
 */
public class ManagerBuscaGRG {
	
	public static final int LIMITE_DE_DIAS = 60;
	
	/**
	 * Atributo que representa a data de implantação do módulo de Recurso de Glosa.
	 * Fez-se necessário esse atributo para que fossem recursadas guias com data de 
	 * atendimento só a partir dessa data.
	 */
	public static String DATA_IMPLANTACAO_RECURSO_GLOSA = "22/01/2013";
	
	/**
	 * Método responsável pela busca das GRGs no fluxos de relatórios de GRGs e deferimento/indeferimento de GRGs.
	 * @author Luciano Rocha
	 * @since 23/11/2012
	 * @param autorizacao
	 * @param dataInicial
	 * @param dataFinal
	 * @param prestador
	 * @param situacao
	 * @param usuario
	 * @return
	 * @throws ValidateException
	 */
	@SuppressWarnings("unchecked")
	public static List<GuiaRecursoGlosa> buscaGuiasRecursoGlosas(
			String autorizacao, String dataInicial, String dataFinal,
			Prestador prestador, String situacao, UsuarioInterface usuario) throws ValidateException {

		SearchAgent sa = new SearchAgent();
		Boolean pesquisouAutorizacao = null;
		
		Date dataInicialFormatada = Utils.parse(dataInicial);
		Date dataFinalFormatada = new Date();
		
		if (dataFinal != null) {
			dataFinalFormatada = Utils.parse(dataFinal);
		}
		
		if(!Utils.isStringVazia(autorizacao)) {
			sa.addParameter(new Equals("autorizacao", autorizacao.trim()));
			pesquisouAutorizacao = true;
		} 
		else if (prestador != null && !usuario.isPrestador()) {
			sa.addParameter(new Equals("guiaOrigem.prestador", prestador));
			pesquisouAutorizacao = false;
		}
		
		if (dataInicial != null) {
			sa.addParameter(new GreaterEquals("situacao.dataSituacao", dataInicialFormatada));
			sa.addParameter(new LowerEquals("situacao.dataSituacao", dataFinalFormatada));
			if (pesquisouAutorizacao == null) {
				validaDatas(dataInicialFormatada, dataFinalFormatada);
				validaLimiteIntervaloDeDatas(dataInicial, dataFinal);
			}
		}
		
		if (pesquisouAutorizacao == null){
			sa.addParameter(new Equals("situacao.descricao", situacao));
		}
		
		List<GuiaRecursoGlosa> recursos = sa.list(GuiaRecursoGlosa.class);

		validateGuiasCampos(recursos, dataInicialFormatada, dataFinalFormatada, pesquisouAutorizacao);
		
		if (pesquisouAutorizacao == null && prestador != null){
			sa.addParameter(new Equals("guiaOrigem.prestador", prestador));
		}
		
		if (recursos == null || recursos.size() < 1) {
			sa.clearAllParameters();
			sa.addParameter(new Equals("situacao.descricao", situacao));
		}
		
		recursos = sa.list(GuiaRecursoGlosa.class);
		
		return recursos;
	}
	
	/**
	 * Método que valida o limite no intervalo de datas.
	 * @author Luciano Rocha
	 * @param dataInicial
	 * @param dataFinal
	 * @throws ValidateException
	 */
	private static void validaLimiteIntervaloDeDatas(String dataInicial, String dataFinal) throws ValidateException {
		Date dataInicialFormatada = Utils.parse(dataInicial);
		Date dataFinalFormatada = new Date();

		if (dataFinal != null) {
			dataFinalFormatada = Utils.parse(dataFinal);
		}
		
		Calendar dataInicalCalendar = Calendar.getInstance();
		Calendar dataFinalCalendar = Calendar.getInstance();
		
		dataInicalCalendar.setTime(dataInicialFormatada);
		dataFinalCalendar.setTime(dataFinalFormatada);
		
		if ((Utils.diferencaEmDias(dataInicalCalendar, dataFinalCalendar)) > LIMITE_DE_DIAS){ 
			throw new ValidateException(MensagemErroEnum.LIMITE_DIAS_ULTRAPASSADO_RECURSO_GLOSA.getMessage());
		}
	}
	
	/**
	 * Método que filtra as GRGs por intervalo de datas.
	 * @param recursos
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 * @throws ValidateException
	 */
	private static void validaDatas(Date dataInicialFormatada, Date dataFinalFormatada) throws ValidateException {
		if (dataInicialFormatada.after(new Date())) {
			throw new ValidateException("A data inicial não pode ser maior que a data de hoje.");
		}
		
		if (dataFinalFormatada.before(dataInicialFormatada)) {
			throw new ValidateException("A data final não pode ser menor que a inicial.");
		}
	}
	
	/**
	 * Verifica se foi encontrado algum recurso de glosa para os parâmetros preenchidos na busca.
	 * @author Luciano Rocha
	 * @since 23/11/2012
	 */
	public static void validateGuiasCampos(List<GuiaRecursoGlosa> recursos, Date dataInicial, Date dataFinal, Boolean pesquisouAutorizacao) throws ValidateException {
		if (recursos == null || recursos.size() < 1) {
			if (pesquisouAutorizacao == null && dataInicial != null) { 
				throw new ValidateException("Nenhum recurso de glosa foi encontrado para este intervalo de datas.");
			}
			else if(pesquisouAutorizacao != null && pesquisouAutorizacao){
				throw new ValidateException("Nenhum recurso de glosa foi encontrado para esta autorização.");
			}
			else if(pesquisouAutorizacao != null && !pesquisouAutorizacao) {
				throw new ValidateException("Nenhum recurso de glosa foi encontrado para este prestador.");
			}
		}
	}
}
