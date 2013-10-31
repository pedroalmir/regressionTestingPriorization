package br.com.infowaypi.ecare.services.internacao;

import java.util.Date;

import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacaoEletiva;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.service.internacao.ConfirmacaoGuiaInternacaoEletivaService;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;

public class ConfirmarGuiaInternacao extends ConfirmacaoGuiaInternacaoEletivaService<Segurado> {
	
	public ResumoGuias<GuiaInternacaoEletiva> buscarGuiasConfirmacao(String numeroDoCartao,String cpfDoTitular, Prestador prestador) throws Exception {
		ResumoSegurados resumo = BuscarSegurados.buscar(numeroDoCartao, cpfDoTitular, Segurado.class);
		return super.buscarGuiasConfirmacao(resumo.getSegurados(), prestador);
	}
	
	public GuiaInternacaoEletiva setarData(Date dataConfirmacaoInternacao, GuiaInternacaoEletiva guiaInternacaoEletiva) throws ValidateException {
		
		if ( dataConfirmacaoInternacao != null ) {
			validaDataAtendimentoMaiorQueDataAtual(dataConfirmacaoInternacao);
			
			//evitar que solicitações de internações eletivas retroativas fiquem bloqueadas por conta da data de autorização
			//validaDataAtendimentoInferiorADatadDeAutorizacao(dataConfirmacaoInternacao, guiaInternacaoEletiva.getDataAutorizacao());
			
			guiaInternacaoEletiva.setDataAtendimento( dataConfirmacaoInternacao );
		} else 
			guiaInternacaoEletiva.setDataAtendimento(new Date());
		
		return guiaInternacaoEletiva;
	}
	
	
	/**
	 *  Utilizado apena pelo cliente Uniplam quando o corte 'REMOVE_OBRIGATORIEDADE_DE_INFORMAR_PRESTADOR_PARA_INTERNACAO' estiver ativo
	 * @param dataConfirmacaoInternacao
	 * @param guiaInternacaoEletiva
	 * @param prestador
	 * @return
	 * @throws ValidateException
	 */
	
	public GuiaInternacaoEletiva setarData(Date dataConfirmacaoInternacao, GuiaInternacaoEletiva guiaInternacaoEletiva, Prestador prestador) throws ValidateException {
		
		if(guiaInternacaoEletiva.getPrestador() == null){
			Prestador prest = ImplDAO.findById(prestador.getIdPrestador(), Prestador.class);
			prest.tocarObjetos();
			guiaInternacaoEletiva.setPrestador(prest);
			
			for(ItemDiaria item : guiaInternacaoEletiva.getItensDiaria()){
				item.recalcularCamposConfirmacaoInternacao();
			}
			guiaInternacaoEletiva.recalcularValores();
		}
		
		return  setarData(dataConfirmacaoInternacao, guiaInternacaoEletiva);
	}

	private void validaDataAtendimentoMaiorQueDataAtual(Date dataConfirmacaoInternacao) throws ValidateException {
		if ( Utils.compareData( dataConfirmacaoInternacao, new Date() ) > 0 )
			throw new ValidateException( MensagemErroEnum.DATA_ATENDIMENTO_SUPERIOR_A_DATA_ATUAL.getMessage() );
	}
	
	private void validaDataAtendimentoInferiorADatadDeAutorizacao(Date dataConfirmacaoInternacao, Date dataAutorizacaoGuia) throws ValidateException {
		if ( Utils.compareData(dataAutorizacaoGuia, dataConfirmacaoInternacao) > 0 )
			throw new ValidateException( MensagemErroEnum.DATA_ATENDIMENTO_INFERIOR_A_DATA_MARCACAO.getMessage() );
		
	}
}
