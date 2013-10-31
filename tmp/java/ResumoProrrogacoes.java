package br.com.infowaypi.ecare.resumos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;

@SuppressWarnings("unchecked")
public class ResumoProrrogacoes {

	private int diariasSolicitadas = 0;
	private int diariasAutorizadas = 0;
	private int diariasNaoAutorizadas = 0;
	
	private GuiaCompleta guia;
	
	public ResumoProrrogacoes(GuiaCompleta guia) {
		this.guia = guia;
		processar();
	}

	private void processar() {
		Set<ItemDiaria> itensDiaria = this.guia.getItensDiaria();
		for (ItemDiaria diaria : itensDiaria) {
			if (diaria.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())) {
				diariasSolicitadas++;
			}
			if (diaria.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao())) {
				diariasAutorizadas++;
			}
			if (diaria.isSituacaoAtual(SituacaoEnum.NAO_AUTORIZADO.descricao())) {
				diariasNaoAutorizadas++;
			}
		}
	}

	public int getDiariasSolicitadas() {
		return diariasSolicitadas;
	}

	public int getDiariasAutorizadas() {
		return diariasAutorizadas;
	}

	public int getDiariasNaoAutorizadas() {
		return diariasNaoAutorizadas;
	}

	public List<ItemDiaria> getDiarias() {
		Set itensDiariaSet = guia.getItensDiaria();
		ArrayList<ItemDiaria> diariasList = new ArrayList<ItemDiaria>(itensDiariaSet);
		Collections.sort(diariasList, new Comparator<ItemDiaria>() {
			@Override
			public int compare(ItemDiaria o1, ItemDiaria o2) {
				Date inicioDiaria1 = o1.getDataInicial();
				Date inicioDiaria2 = o2.getDataInicial();

				if (inicioDiaria1 != null && inicioDiaria2 != null) {
					if (inicioDiaria1.before(inicioDiaria2)) {
						return -1;
					} else {
						return 1;
					}
				} else {
					if (inicioDiaria1 == null && inicioDiaria2 == null) {
						return 0;
					} else {
						if (inicioDiaria1 != null) {
							return -1;
						} else {
							return 1;
						}
					}
				}
			}
		});
		return diariasList;
	}

	public GuiaCompleta getGuia() {
		return guia;
	}

}
