package br.com.infowaypi.ecare.services.honorarios.validators;

import java.util.Date;
import java.util.Set;

import org.hibernate.Query;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe respons�vel por validar se a data de realiza��o de um procedimento (Informado no fluxo de gera��o de honorario individual)
 * est� entre a data de atendimento e a data atual. Aplicada especificamente nos procedimentos que n�o possuem data de realiza��o no 
 * momento da entrada no fluxo em quest�o.

 * @author Eduardo
 * 
 */
public class GuiaHonorarioDataDeRealizacaoProcedimentoCirurgicoValidator extends CommandValidator {

	/**
	 * Valida a data de realiza��o dos procedimentos aptos a gerar honor�rios no fluxo de gerar honor�rio individual.
	 * 
	 * @param procedimentosAptosAGerarHonorario
	 * @param dataAtendimento
	 * @throws ValidateException
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void execute() throws ValidateException {
		Set<ProcedimentoInterface> procedimentosAptosAGerarHonorarios = this.getGuiaOrigem().getProcedimentosAptosAGerarHonorariosMedicos();
		GuiaCompleta guiaCompleta = (GuiaCompleta)this.getGuiaOrigem();
		String situacao = guiaCompleta.getSituacao().getDescricao();
		Date dataBase = situacao.equals(SituacaoEnum.AUTORIZADO.descricao()) ? guiaCompleta.getDataAutorizacao() : guiaCompleta.getDataAtendimento();
		
		for (ProcedimentoInterface procedimento : procedimentosAptosAGerarHonorarios) {
			ProcedimentoCirurgicoInterface procedimentoCirurgico = (ProcedimentoCirurgicoInterface) procedimento;
	
			Boolean isMarcadoParaGerarHonorario = procedimentoCirurgico.getAdicionarHonorario();
			boolean isProcedimentoVaiGerarHonorario = isMarcadoParaGerarHonorario != null && isMarcadoParaGerarHonorario;		

			if (isProcedimentoVaiGerarHonorario) {
				verificaSeADataFoiPreenchidaEEhValida(dataBase, procedimentoCirurgico);
			}
			
			verificaSeADataFoiAlterada(procedimentoCirurgico, isProcedimentoVaiGerarHonorario);
		}
	}

	/**
	 * Verifica se a data de realiza��o de um procedimento n�o marcado para gerar honor�rio foi alterada.
	 * Se ocorreu, lan�a uma exce��o.
	 *  
	 * @param procedimento
	 * @throws RuntimeException
	 */
	private static void verificaSeADataFoiAlterada(ProcedimentoCirurgicoInterface procedimento, boolean isProcedimentoVaiGerarHonorario) {
		
		Long idProcedimento = procedimento.getIdProcedimento();
		Date dataRealizacao = procedimento.getDataRealizacao();
		
		Date dataRealizacaoNoBanco = buscarDataRealizacaoNoBanco(idProcedimento);

		if (dataRealizacaoNoBanco == null && !isProcedimentoVaiGerarHonorario) {
			Assert.isNull(dataRealizacao, MensagemErroEnum.DATA_CIRURGIA_INFORMADA_PARA_PROCEDIMENTO_QUE_NAO_IRA_GERAR_HONORARIO.getMessage(procedimento.getProcedimentoDaTabelaCBHPM().getCodigo()));
		} else  if (dataRealizacaoNoBanco != null){
			boolean isMesmaData = Utils.compareData(dataRealizacao, dataRealizacaoNoBanco) == 0;
			Assert.isTrue(isMesmaData, MensagemErroEnum.DATA_CIRURGIA_NAO_PODE_SER_ALTERADA.getMessage(procedimento.getProcedimentoDaTabelaCBHPM().getCodigo()));
		}
	}

	/**
	 * Verifica se a data de realiza��o de um procedimento que vai gerar honor�rio n�o � nula ou se est� compreendida entre
	 * a data de atendimento da guia e a data atual (momento da execu��o do fluxo).
	 * 
	 * @param dataAtendimento
	 * @param procedimento
	 */
	private static void verificaSeADataFoiPreenchidaEEhValida(Date dataAtendimento, ProcedimentoCirurgicoInterface procedimento) {

		Date dataRealizacaoDoProcedimento = procedimento.getDataRealizacao();
		TabelaCBHPM procedimentoDaTabelaCBHPM = procedimento.getProcedimentoDaTabelaCBHPM();
		
		Assert.isNotNull(dataRealizacaoDoProcedimento, MensagemErroEnum.DATA_CIRURGIA_REQUERIDA.getMessage(procedimentoDaTabelaCBHPM.getCodigo()));
		
		boolean isRealizacaoAntesDoAtendimento = Utils.compareData(dataRealizacaoDoProcedimento, dataAtendimento) < 0;
		boolean isRealizacaoAposDataAtual = Utils.compareData(dataRealizacaoDoProcedimento, new Date()) > 0;

		Assert.isFalse(isRealizacaoAntesDoAtendimento, MensagemErroEnum.DATA_CIRURGIA_INFERIOR_A_DATA_DE_ATENDIMENTO.getMessage(procedimentoDaTabelaCBHPM.getCodigo()));
		Assert.isFalse(isRealizacaoAposDataAtual, MensagemErroEnum.DATA_CIRURGIA_SUPERIOR_A_DATA_ATUAL.getMessage(procedimentoDaTabelaCBHPM.getCodigo()));
	}

	/**
	 * Busca a data de realizacao do procedimento no banco.
	 * Usado para verificar se ela foi mudada nos procedimentos que n�o v�o gerar honor�rio.
	 * 
	 * @param idProcedimento
	 * @return Data de realiza��o do procedimento.
	 */
	private static Date buscarDataRealizacaoNoBanco(Long idProcedimento) {

		String buscaDataRealizacaoNoBanco = "SELECT dataRealizacao FROM ProcedimentoCirurgico WHERE idProcedimento = :idProcedimento";

		Query buscarData = HibernateUtil.currentSession().createQuery(buscaDataRealizacaoNoBanco);
		buscarData.setLong("idProcedimento", idProcedimento);
		Date dataRealizacaoNoBanco = (Date) buscarData.uniqueResult();
	
		return dataRealizacaoNoBanco;
	}

}
