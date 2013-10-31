package br.com.infowaypi.sensews.client.relatorio;

import java.io.File;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;

import br.com.infowaypi.ecarebc.atendimentos.GuiaConsulta;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.sensews.client.tolerancia.sqlite.DBConnection;
import br.com.infowaypi.sensews.client.tolerancia.sqlite.EventoJSON;

/**
 * Relatorio que informará ao Sense quais os eventos
 * não puderam ser enviados
 * 
 * @author Jefferson Henrique
 */
public class RelatorioSenseInspect implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResumoSqlite getResumo(String valor) throws Exception {
		ResumoSqlite resumo = new ResumoSqlite();

		try {
			DBConnection instance = DBConnection.getInstance();
			if (valor == null) {
				List<EventoJSON> eventos = instance
						.getEventosNaoEnviados();
				resumo.setEventos(eventos);
	
				File file = new File(instance.getPath());
				if (file.exists()) {
					resumo.setLocal(file.getAbsolutePath());
				} else {
					resumo.setLocal("Arquivo nao existe");
				}
			} else {
				valor = valor.trim();
				if (valor.startsWith("add")) {
						EventoJSON ej = new EventoJSON(null, valor.split(":")[1], "java.lang", null);
						instance.addEventoNaoEnviado(ej);
				} else if (valor.startsWith("send")) {
					Long idGuia = Long.valueOf(valor.split(":")[1]);
					
					GuiaConsulta guia = ImplDAO.findById(idGuia, GuiaConsulta.class);
					
//					br.com.infowaypi.sense.iapep.ans.Guia guia2 = new SenseGuiaConverter()
//							.convertGuia(guia, false);
//					
//					resumo.setLocal("Nome beneficiario: " + guia2.getBeneficiario().getNome()
//							+ "<br />"
//							+ "Nome profissional: " + guia2.getSolicitacao().getProfissional().getNome());
//					
//					SenseAnalizer.execute(guia2);
				} else if (valor.startsWith("clear")) {
					if (valor.contains(":")) {
						Long idEvento = Long.valueOf(valor.split(":")[1]);
						instance.removeEventoNaoEnviado(idEvento);
					} else {
						instance.initDB();
					}
				}
			}
		} catch (Exception e) {
			StringWriter out = new StringWriter();
			e.printStackTrace(new PrintWriter(out));
			resumo.setLocal(out.toString().replaceAll("\n", "<br />"));
		}
		
		return resumo;
	}

}