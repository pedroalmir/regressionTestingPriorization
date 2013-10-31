package br.com.infowaypi.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;

public class ServletRegulacao extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		resp.setContentType("application/json");
		String autorizacao = req.getParameter("autorizacao");
		PrintWriter out = resp.getWriter();
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("autorizacao", autorizacao));
		
		out.print(new Gson().toJson(new GuiaView(sa.uniqueResult(GuiaSimples.class))));
		out.flush();
		out.close();
	}
	
}
