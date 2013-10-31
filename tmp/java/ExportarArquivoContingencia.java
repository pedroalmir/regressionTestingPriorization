package br.com.infowaypi.ecare.services.suporte;

import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;

public class ExportarArquivoContingencia {
	
	public static void main(String[] args) {
		List<Segurado> segurados = HibernateUtil.currentSession().createCriteria(Segurado.class)
		.setFetchSize(500)
		.setCacheable(false)
		.setFetchMode("segurado.consultasPromocionais", FetchMode.SELECT)
		.setFetchMode("segurado.matriculas", FetchMode.SELECT)
		.setFetchMode("segurado.cartoes", FetchMode.SELECT)
		.setFetchMode("situacaoCadastral", FetchMode.SELECT)
		.setFetchMode("guias", FetchMode.SELECT)
		.setFetchMode("consumoIndividual", FetchMode.SELECT)
		.setFetchMode("odontograma", FetchMode.SELECT)
		.setFetchMode("titularOrigem", FetchMode.SELECT)
		.setFetchMode("titular", FetchMode.SELECT)
		.add(Expression.eq("situacao.descricao", SituacaoEnum.ATIVO.descricao()))
		.list();
		
		
		for (Segurado segurado : segurados) {
			System.out.println("------------------------------");
			System.out.println(segurado.getPessoaFisica().getNome());
			System.out.println(segurado.getCarenciaRestanteUrgencias());
			System.out.println(segurado.getCarenciaRestanteConsultasExamesDeBaixaComplexidade());
			System.out.println(segurado.getCarenciaRestanteExamesEspeciaisDeAltaComplexidadeCirurgiasEInternamento());
			System.out.println(segurado.getCarenciaRestanteParaDoencasPreExistentes());
		}
	}

}
