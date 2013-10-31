package br.com.infowaypi.utilServelet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.services.recurso.GuiaRecursoGlosa;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.LoteDeGuias;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.utils.CompetenciaUtils;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Between;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.molecular.parameter.LowerEquals;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

public class ServletLote extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter pw = response.getWriter();  
		

		String funcao = request.getParameter("funcao");

		if(funcao.equals("entregarLote")){
			Long idPrestador = new Long(request.getParameter("prestador"));
			String competencia = request.getParameter("competencia");
			pw.write("" + isExistenciaDeGRGparaComporLote(idPrestador, competencia));
		}
		
		if(funcao.equals("receberLote")){
			String identificador = request.getParameter("identificador");
			pw.write("" + isLoteGRG(identificador));
		}
	}

	private boolean isLoteGRG(String identificador) {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("identificador", identificador));
		LoteDeGuias lote = sa.uniqueResult(LoteDeGuias.class);
		
		boolean isLoteGRG = false;
		
		if(lote != null){
			isLoteGRG = lote.isLoteGRG();
		}
		return isLoteGRG;
	}
	
	private boolean isExistenciaDeGRGparaComporLote(Long idPrestador, String competencia){
		
		Date competenciaDate = Utils.createData("01/" + competencia);
		
		Prestador prestador = ImplDAO.findById(idPrestador, Prestador.class);
		Assert.isTrue(prestador.isExigeEntregaLote(), "Esse prestador não está habilitado para registrar o envio de lotes.");
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("guiaOrigem.prestador", prestador));
		Date comp = Utils.gerarCompetencia(competenciaDate);
		sa.addParameter(new GreaterEquals("situacao.dataSituacao", CompetenciaUtils.getInicioCompetencia(comp)));
		sa.addParameter(new LowerEquals("situacao.dataSituacao", Utils.incrementaDias(CompetenciaUtils.getFimCompetencia(comp), 1)));
		sa.addParameter(new In("situacao.descricao", SituacaoEnum.RECURSADO.descricao(), SituacaoEnum.DEVOLVIDO.descricao()));
//		sa.addParameter(new Between("situacao.dataSituacao", inicioCompetencia, fimCompetencia));
		
		List<GuiaRecursoGlosa> guias = sa.list(GuiaRecursoGlosa.class);
		
		return !guias.isEmpty();
	}
}
