package br.com.infowaypi.ecare.correcaomanual;

import java.util.ArrayList;
import java.util.List;

import javax.management.relation.Role;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.molecular.parameter.LowerEquals;
import br.com.infowaypi.molecular.parameter.OrderBy;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.utils.Utils;

public class RelatorioAdHocMonitoraCentral {
	
	public static void main(String[] args) {
		SearchAgent sa = new SearchAgent();
		
//		sa.addParameter(new In("situacao.descricao", SituacaoEnum.SOLICITADO.descricao(), SituacaoEnum.SOLICITADO_INTERNACAO.descricao(), SituacaoEnum.SOLICITADO_PRORROGACAO.descricao(), SituacaoEnum.AUTORIZADO.descricao()));
		sa.addParameter(new GreaterEquals("dataMarcacao", Utils.parse("02/04/2013")));
		sa.addParameter(new LowerEquals("dataMarcacao", Utils.parse("02/04/2013")));
		sa.addParameter(new OrderBy("situacao.usuario"));
		
		List<GuiaSimples> guias = sa.list(GuiaSimples.class);
		
//		Usuario usuario = ImplDAO.findById(37267061L, Usuario.class);//Marconi
		
		List<GuiaSimples> guiasFiltradas = new ArrayList<GuiaSimples>();
//		List<Procedimento> procedimentosFiltrados = new ArrayList<Procedimento>();
		
//		guiasFiltradas = filtrarGuiasPorUsuario(guias, usuario);
		
		guiasFiltradas = filtrarGuiaPorDataSolicitadoAutorizado(guias);
		
//		procedimentosFiltrados = filtraProcedimentos(guiasFiltradas, usuario);
		
//		System.out.println("TAM G: " + guiasFiltradas.size());
		for (GuiaSimples guia : guiasFiltradas) {
			imprimiGuia(guia);
		}
		
//		for (Procedimento proc : procedimentosFiltrados) {
//			imprimiProcedimento(proc, usuario);
//		}
	}
	
	private static List<GuiaSimples> filtrarGuiaPorDataSolicitadoAutorizado(List<GuiaSimples> guias) {

		List<GuiaSimples> guiasFiltradas = new ArrayList<GuiaSimples>();
		
		for (GuiaSimples guia : guias) {
			if (guia.getSituacao(SituacaoEnum.AUTORIZADO.descricao()) != null 
				&& Utils.compareData(guia.getSituacao(SituacaoEnum.AUTORIZADO.descricao()).getDataSituacao(), guia.getDataMarcacao()) == 0) {
				guiasFiltradas.add(guia);
			}
		}
		
		return guiasFiltradas;
	}

	private static List<Procedimento> filtraProcedimentos(List<GuiaSimples> guiasFiltradas, Usuario usuario) {
		List<Procedimento> procedimentosFiltrados = new ArrayList<Procedimento>();
		
		for (GuiaSimples guia : guiasFiltradas) {
			List<Procedimento> procedimentos = guia.getProcedimentos(SituacaoEnum.AUTORIZADO.descricao(), SituacaoEnum.NAO_AUTORIZADO.descricao(), SituacaoEnum.CONFIRMADO.descricao());
			for (Procedimento p : procedimentos) {
				if (p.getSituacao().getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
					procedimentosFiltrados.add(p);
				}
			}
		}
		
		return procedimentosFiltrados;
	}

	private static List<GuiaSimples> filtrarGuiasPorUsuario(List<GuiaSimples> guias, Usuario usuario) {
		
		List<GuiaSimples> guiasFiltradas = new ArrayList<GuiaSimples>();
		
		for (GuiaSimples guia : guias) {
			if (guia.getSituacao(SituacaoEnum.AUTORIZADO.descricao()) != null && guia.getSituacao(SituacaoEnum.AUTORIZADO.descricao()).getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
				guiasFiltradas.add(guia);
			}
			else if (guia.getSituacao(SituacaoEnum.NAO_AUTORIZADO.descricao()) != null && guia.getSituacao(SituacaoEnum.NAO_AUTORIZADO.descricao()).getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
				guiasFiltradas.add(guia);
			}
		}
		
		return guiasFiltradas; 
	}

	private static void imprimiGuia(GuiaSimples guia){
		System.out.print(guia.getSituacao().getUsuario().getNome()+";");
		System.out.print(guia.getSituacao().getUsuario().getRole()+";");
		System.out.print(guia.getAutorizacao()+";");
		System.out.print(guia.getTipo()+";");
		System.out.print(guia.getSituacao().getDescricao()+";");
		System.out.print(Utils.convert(guia.getSituacao().getDataSituacao())+";");
		System.out.print(guia.getSegurado().getPessoaFisica().getNome()+";");
		System.out.println(guia.getValorTotal());
	}
	
	private static void imprimiProcedimento(Procedimento procedimento, Usuario usuario){
		System.out.print(procedimento.getGuia().getAutorizacao()+";");
		System.out.print(procedimento.getProcedimentoDaTabelaCBHPM().getCodigoEDescricao()+";");
		System.out.print(procedimento.getProcedimentoDaTabelaCBHPM().getNivel()+";");
		System.out.print(procedimento.getSituacao().getDescricao()+";");
		System.out.print(Utils.convert(procedimento.getSituacao().getDataSituacao())+";");
		System.out.print(procedimento.getGuia().getSegurado().getPessoaFisica().getNome()+";");
		System.out.println(procedimento.getValorTotal());
	}

}
