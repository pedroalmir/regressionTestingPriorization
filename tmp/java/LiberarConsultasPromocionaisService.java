package br.com.infowaypi.ecare.services.consultas;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsulta;
import br.com.infowaypi.ecarebc.atendimentos.Observacao;
import br.com.infowaypi.ecarebc.consumo.ConsumoInterface;
import br.com.infowaypi.ecarebc.consumo.periodos.Periodo;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.exceptions.ConsumoException;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.ecarebc.promocao.PromocaoConsulta;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author Marcus Boolean
 * @changes Danilo Nogueira Portela
 */
public class LiberarConsultasPromocionaisService{
	
	public static ResumoSegurados buscar(String cpfDoTitular,String numeroDoCartao) {
			return BuscarSegurados.buscar(cpfDoTitular, numeroDoCartao, Segurado.class);
	}
	
	public SeguradoInterface selecionarSegurado(Segurado segurado) {
		segurado.tocarObjetos();
		
		Set<PromocaoConsulta> consultasPromocionais = segurado.getConsultasPromocionais();
		consultasPromocionais.size();
		
		for (PromocaoConsulta promocaoConsulta : consultasPromocionais) {
			promocaoConsulta.getEspecialidade().getDescricao();
		}
		
		return segurado;
	}
	
	/**
	 * Método que verifica se o beneficiário já estourou algum limite.
	 * A co-participação não poderá ser liberada caso o limite de consultas eletivas tenha sido estourado.
	 * @param segurado
	 */
	private void validaLimitesConsumos(Segurado segurado){
		Date dtUltimaConsulta = getDataUltimaConsultaDoSegurado(segurado);
		
		ConsumoInterface consumoMensal = segurado.getConsumoIndividual().getConsumo(new Date(), Periodo.MENSAL);
		ConsumoInterface consumoTrimestral = segurado.getConsumoIndividual().getConsumo(new Date(), Periodo.TRIMESTRAL);
		ConsumoInterface consumoSemestral = segurado.getConsumoIndividual().getConsumo(new Date(), Periodo.SEMESTRAL);
		ConsumoInterface consumoAnual = segurado.getConsumoIndividual().getConsumo(new Date(), Periodo.ANUAL);
		
		if(consumoAnual!= null&& consumoAnual.getLimiteConsultas().compareTo(consumoAnual.getSomatorioConsultas()) <= 0){
			throw new ConsumoException(MensagemErroEnum.SEGURADO_COM_LIMITE_ESTOURADO_INFORMA_PROXIMA_DATA.getMessage(Periodo.ANUAL.getChave(), Utils.format(dtUltimaConsulta), Utils.format(consumoAnual.getProximaDataValida())));
		} else if (consumoSemestral!= null && consumoSemestral.getLimiteConsultas().compareTo(consumoSemestral.getSomatorioConsultas()) <= 0){
			throw new ConsumoException(MensagemErroEnum.SEGURADO_COM_LIMITE_ESTOURADO_INFORMA_PROXIMA_DATA.getMessage(Periodo.SEMESTRAL.getChave(), Utils.format(dtUltimaConsulta), Utils.format(consumoSemestral.getProximaDataValida())));
		} else if (consumoTrimestral!= null && consumoTrimestral.getLimiteConsultas().compareTo(consumoTrimestral.getSomatorioConsultas()) <= 0){
			throw new ConsumoException(MensagemErroEnum.SEGURADO_COM_LIMITE_ESTOURADO_INFORMA_PROXIMA_DATA.getMessage(Periodo.TRIMESTRAL.getChave(), Utils.format(dtUltimaConsulta), Utils.format(consumoTrimestral.getProximaDataValida())));
		} else if(consumoMensal!= null && consumoMensal.getLimiteConsultas().compareTo(consumoMensal.getSomatorioConsultas()) <= 0){
			throw new ConsumoException(MensagemErroEnum.SEGURADO_COM_LIMITE_ESTOURADO_INFORMA_PROXIMA_DATA.getMessage(Periodo.MENSAL.getChave(), Utils.format(dtUltimaConsulta), Utils.format(consumoMensal.getProximaDataValida())));
		}
	}
	
	/**
	 * Fornece a data de atendimento da última consulta agendada ou confirmada do segurado.
	 * @param segurado
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Date getDataUltimaConsultaDoSegurado(Segurado segurado) {
		Calendar primeiroDiaDoAno = new GregorianCalendar(GregorianCalendar.getInstance().get(GregorianCalendar.YEAR), GregorianCalendar.JANUARY, 1);
		Calendar ultimoDiaDoAno = new GregorianCalendar(GregorianCalendar.getInstance().get(GregorianCalendar.YEAR), GregorianCalendar.DECEMBER, 31);
		
		List<String> situacoes = Arrays.asList(SituacaoEnum.AGENDADA.descricao(), SituacaoEnum.CONFIRMADO.descricao());
		
		Criteria crit = HibernateUtil.currentSession().createCriteria(GuiaConsulta.class);
		crit.add(Expression.eq("segurado", segurado));
		crit.add(Expression.between("dataAtendimento", primeiroDiaDoAno.getTime(), ultimoDiaDoAno.getTime()));
		crit.add(Expression.in("situacao.descricao", situacoes));
		
		List<GuiaConsulta> guias = crit.list();
		Utils.sort(guias, "dataAtendimento");
		
		if (!guias.isEmpty()){
			return guias.get(guias.size()-1).getDataAtendimento();
		}
		
		return null;
	}

	public Segurado criarConsultaPromocional(UsuarioInterface usuario, Segurado segurado, Integer tipo, Especialidade especialidade, String observacao) throws ValidateException{
		
		PromocaoConsulta consultaPromo = null;

		if(tipo != null && especialidade != null){
			if (tipo ==  PromocaoConsulta.TIPO_ELETIVA){
				validaLimitesConsumos(segurado);
			}
			
			consultaPromo = criaConsultaPromocional(usuario, segurado, tipo, especialidade, observacao);
			segurado.setConsultaPromocional(consultaPromo);
			verificaCarencia(segurado, consultaPromo);
		}else {
			if (tipo != null && especialidade == null){
				throw new ValidateException(MensagemErroEnum.CONSULTA_PROMOCIONAL_INFORMAR_TODOS_OS_PARAMETROS.getMessage("\"Especialidade\""));
			}
			
			if (especialidade != null && tipo == null){
				throw new ValidateException(MensagemErroEnum.CONSULTA_PROMOCIONAL_INFORMAR_TODOS_OS_PARAMETROS.getMessage("\"Tipo\""));
			}
		}

		return segurado;
	}

	private PromocaoConsulta criaConsultaPromocional(UsuarioInterface usuario,
			Segurado segurado, Integer tipo, Especialidade especialidade,
			String observacao) {
		PromocaoConsulta consultaPromo = new PromocaoConsulta(usuario,segurado);
		
		consultaPromo.setEspecialidade(especialidade);
		consultaPromo.setTipo(tipo);
		
		if(!Utils.isStringVazia(observacao)){
			Observacao observ = new Observacao(new Date(), observacao, usuario);
			consultaPromo.setObservacao(observ);
		}
		
		int numeroConsultasPromocionaisLiberadas = 0;
		if(!segurado.getConsultasPromocionais().isEmpty()) {
			for (PromocaoConsulta consultaPromocional : segurado.getConsultasPromocionais()) {
				if(consultaPromocional.isLiberado()){
					numeroConsultasPromocionaisLiberadas ++;
				}
			}
		}
		return consultaPromo;
	}

	private void verificaCarencia(Segurado segurado, PromocaoConsulta consultaPromo) throws ValidateException {
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
		
		TabelaCBHPM tabela;
		
		if(consultaPromo.getTipo() ==  PromocaoConsulta.TIPO_ELETIVA){
			tabela = (TabelaCBHPM) HibernateUtil.currentSession().createCriteria(TabelaCBHPM.class)
												.add(Expression.eq("codigo", GuiaConsulta.PROCEDIMENTO_PADRAO_CONSULTA))
												.uniqueResult();
		}else{
			tabela = (TabelaCBHPM) HibernateUtil.currentSession().createCriteria(TabelaCBHPM.class)
												.add(Expression.eq("codigo", GuiaConsulta.PROCEDIMENTO_CONSULTA_URGENCIA))
												.uniqueResult();
		}
		
		if(!segurado.isCumpriuCarencia(tabela.getCarencia())){
			throw new ValidateException(MensagemErroEnumSR.BENEFICIARIO_SEM_CARENCIA_PARA_CONSULTA_INFORMADA.getMessage());
		}
	}
	
	public Segurado conferirDados(Segurado segurado) throws Exception {
		for (PromocaoConsulta consulta: segurado.getConsultasPromocionaisLiberadas()){
			ImplDAO.save(consulta);
		}
		
		return segurado;
	}
	
	public void finalizar(Segurado segurado) {}
	
}
