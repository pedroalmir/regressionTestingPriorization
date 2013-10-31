package br.com.infowaypi.ecarebc.utils;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;

/**
 * Classe respons�vel por tocar campos de uma guia.
 * Utilizada para facilitar a constru��o de m�todos "tocarObjetos" dentro de services,
 * evitando repeti��o de c�digo. 
 * @author patricia
 * @since 17/04/2010
 *
 */
public class TocarObjetoUtils {
	
	/**
	 * Toca os campos de uma guia, de acordo com a lista de campos passado pelo m�todo usu�rio.
	 * @param guiacompleta
	 * @param campos
	 */
	public static void tocarObjeto(GuiaCompleta<ProcedimentoInterface> guiacompleta, TocarObjetoEnum... campos){
		for (TocarObjetoEnum campo : campos) {
			campo.tocarCampoGuia(guiacompleta);
		}
	}

	/**
	 * Enum que fornece o servi�o de tocar o campo de guia nela definido.
	 * @author patricia
	 *
	 */
	public enum TocarObjetoEnum {
		CID_GUIA{
			@Override
			public void tocarCampoGuia(GuiaCompleta<ProcedimentoInterface> guiaCompleta) {
				guiaCompleta.getCids().size();
			}
		},
		OBSERVACOES_GUIA{
			@Override
			public void tocarCampoGuia(GuiaCompleta<ProcedimentoInterface> guiaCompleta) {
				guiaCompleta.getObservacoes().size();
			}
		},
		ITENS_PACOTE_GUIA{
			@Override
			public void tocarCampoGuia(GuiaCompleta<ProcedimentoInterface> guiaCompleta) {
				guiaCompleta.getItensPacote().size();
			}
		},
		ITENS_GASOTERAPIA_GUIA{
			@Override
			public void tocarCampoGuia(GuiaCompleta<ProcedimentoInterface> guiaCompleta) {
				guiaCompleta.getItensGasoterapia().size();
			}
		},
		ITENS_DIARIA_GUIA{
			@Override
			public void tocarCampoGuia(GuiaCompleta<ProcedimentoInterface> guiaCompleta) {
				guiaCompleta.getItensDiaria().size();
				for (ItemDiaria itemDiaria : guiaCompleta.getItensDiaria()) {
					itemDiaria.getSituacoes().size();
				}
			}
		},
		ITENS_TAXA_GUIA{
			@Override
			public void tocarCampoGuia(GuiaCompleta<ProcedimentoInterface> guiaCompleta) {
				guiaCompleta.getItensTaxa().size();
			}
		},
		PROCEDIMENTOS_GUIA{
			@Override
			public void tocarCampoGuia(GuiaCompleta<ProcedimentoInterface> guiaCompleta) {
				guiaCompleta.getProcedimentos().size();
			}
		};
		
		/**
		 * Respons�vel por tocar o campo da guia passada como par�metro, de acordo com
		 * o tipo de campo definido na enum.
		 * @param guiaCompleta
		 */
		public abstract void tocarCampoGuia(GuiaCompleta<ProcedimentoInterface> guiaCompleta);
	}
}
