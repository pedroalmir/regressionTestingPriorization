package br.com.infowaypi.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.Greater;
import br.com.infowaypi.molecular.parameter.NotEquals;
import br.com.infowaypi.molecular.parameter.OrderBy;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.utils.Utils;

import com.google.gson.Gson;

public class Notifier extends HttpServlet{

	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		resp.setContentType("application/json");
		Usuario usuario = ((Usuario) req.getSession().getAttribute("usuario"));
		PrintWriter out = resp.getWriter();
		Gson gson = new Gson();
		String json = gson.toJson(new Notification(Notification.ERROR_CODE_NO_ROLE_ALLOWED, "Role invalido", new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>()));
		if (usuario != null && usuario.getRole() != null) {
			if (usuario.getRole().equals("prestadorAnestesista") ||
					usuario.getRole().equals("prestadorCompleto") ||
					usuario.getRole().equals("prestadorConsExmIntUrg") ||
					usuario.getRole().equals("prestadorConsIntUrg") ||
					usuario.getRole().equals("prestadorConsultaExame") ||
					usuario.getRole().equals("prestadorInternacaoExameUrgencia") ||
					usuario.getRole().equals("prestadorInternacaoUrgencia") ||
					usuario.getRole().equals("prestadorOdonto") ||
					usuario.getRole().equals("prestadorExame")) {

				String query = "SELECT guia.autorizacao, segurado.numeroDoCartao, guia.tipoDeGuia FROM " +
						"GuiaSimples as guia, AbstractSegurado as segurado WHERE " +
						"guia.segurado = segurado AND " +
						"guia.prestador.usuario = :usuario AND " +
						"guia.situacao.descricao = :situacao AND " +
						"guia.situacao.dataSituacao > :dataSituacao " +
						"ORDER BY guia.situacao.dataSituacao";
				
				Query hql = HibernateUtil.currentSession().createQuery(query)
						.setEntity("usuario", usuario)
						.setString("situacao", SituacaoEnum.AUTORIZADO.descricao())
						.setDate("dataSituacao", Utils.incrementaDias(new Date(), -60));
				
				List<String> autorizacoes = new ArrayList<String>();
				List<String> segurados = new ArrayList<String>();
				List<String> tipoDeGuia = new ArrayList<String>();

				List<Object[]> result = hql.list();
				for (Object[] strs : result) {
					autorizacoes.add(strs[0].toString());
					segurados.add(strs[1].toString());
					tipoDeGuia.add(strs[2].toString());
				}
				
				if (autorizacoes.isEmpty()) {
					json = gson.toJson(new Notification(Notification.ERROR_CODE_GUIAS_NAO_ENCONTRADAS, "Nenhuma guia encontrada", new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>()));
				} else {
					json = gson.toJson(new Notification(Notification.SUCCESS, "", autorizacoes, segurados, tipoDeGuia));
				}
			}
		}
		out.println(json);
		out.flush();
		out.close();
	}
	
}
