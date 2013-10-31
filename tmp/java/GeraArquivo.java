/**
 * 
 */
package br.com.infowaypi.ecare.arquivos;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.junit.Test;

import br.com.infowaypi.ecare.segurados.Cartao;
import br.com.infowaypi.ecare.segurados.DependenteSR;
import br.com.infowaypi.ecare.segurados.Empresa;
import br.com.infowaypi.ecare.segurados.Titular;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;

/**
 * @author Marcus
 *
 */
public class GeraArquivo {

	@Test
	public void gerarArquivoCartoes(){
		Transaction tx = HibernateUtil.currentSession().beginTransaction();
		
		List<Cartao> cartoesTitulares = HibernateUtil.currentSession().createCriteria(Cartao.class)
		.add(Expression.eq("segurado.recadastrado", Boolean.TRUE))
		.add(Expression.isNull("remessa"))
		.add(Expression.eq("segurado.situacao.descricao", SituacaoEnum.ATIVO.descricao()))
		.add(Expression.isNotNull("segurado.dataAdesao"))
		.list();
		
		ArquivoCartoes arquivo = new ArquivoCartoes(cartoesTitulares);
		try {
			RemessaDeCartao remessa = arquivo.getRemessaDeCartao();
			ImplDAO.save(remessa);
		} catch (Exception e) {
			System.out.println("Falha durante a geração do arquivo de segurados!.");
			e.printStackTrace();
		}
		
		tx.commit();
	}
	
	private static void geraArquivoDeEntradaDeLancamentos() {
		Empresa empresa =  (Empresa) ImplDAO.findById(26L, Empresa.class);
		ArquivoDeEntradaDeLancamentos arquivo = new ArquivoDeEntradaDeLancamentos(empresa);
		arquivo.geraRegistroTipo1();
		arquivo.geraRegistroTipo2();
		arquivo.geraRegistroTipo9();
		
		try {
			arquivo.geraArquivo();
		} catch (Exception e) {
			System.out.println("Crashed!!!");
			e.printStackTrace();
		}
	}
	
	private static void geraArquivoDeSegurados() {
		
		List<Titular> seguradosNoSistema = getCriteriaTitular(null)
		.add(Expression.eq("recadastrado", Boolean.TRUE))
		.list();
		System.out.println(seguradosNoSistema.size());
		ArquivoSegurados arquivo = new ArquivoSegurados(seguradosNoSistema);
		try {
			arquivo.fileGenerate();
		} catch (Exception e) {
			System.out.println("Falha durante a geração do arquivo de segurados!");
			e.printStackTrace();
		}
	}
	
	public static Criteria getCriteriaTitular(Class tipo){
		
		return HibernateUtil.currentSession().createCriteria(tipo)
		.setFetchMode("dependentes", FetchMode.SELECT)
		.setFetchMode("consumoIndividual", FetchMode.SELECT)
		.setFetchMode("guias", FetchMode.SELECT)
		.setFetchMode("odontograma", FetchMode.SELECT)
		.setFetchMode("informacaoFinanceira", FetchMode.SELECT)
		.setFetchMode("colecaoSituacoes", FetchMode.SELECT)
		.setFetchMode("consultasPromocionais", FetchMode.SELECT)
		.setFetchMode("consignacoes", FetchMode.SELECT)
		.setFetchMode("grupo", FetchMode.SELECT)
		.setFetchMode("matriculas", FetchMode.SELECT);
	}
	
	private List<DependenteSR> filtrarDependentes(List<DependenteSR> dependentes){
		
		List<DependenteSR> resultado = new ArrayList<DependenteSR>();
		for (DependenteSR dependente : dependentes) {
			if(dependente.getTitular().getDataGeracaoDoCartao() != null)
				resultado.add(dependente);
		}
		
		return resultado;
	}

}
