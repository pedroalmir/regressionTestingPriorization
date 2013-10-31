import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.utils.Utils;

/**
 * 
 */

/**
 * @author Infoway
 *
 */
public class RelatorioFaturamento {
	
	
	
	private static Map<Prestador, Set<GuiaSimples>> mapaGuiasPorPrestador = new HashMap<Prestador, Set<GuiaSimples>>();
	private static Date competencia = Utils.parse("01/08/2008");
	private static String[] situacoes = {"Auditado(a)", "Confirmado(a)"};
	private static final char NOVO_CAMPO = ';';
	private static final String NOVA_LINHA = System.getProperty("line.separator");
	
	public static void main(String[] args) throws Exception {
		Criteria c = HibernateUtil.currentSession().createCriteria(GuiaSimples.class);
		c.add(Expression.ge("dataAtendimento", Utils.parse("21/02/2008")));
		c.add(Expression.le("dataAtendimento", Utils.parse("20/07/2008")));
		c.add(Expression.in("situacao.descricao", situacoes));
		
		alimentarMapa(c.list());
		
		processarArquivo();
	}
	
	private static void processarArquivo() throws Exception {
		StringBuffer buffer = new StringBuffer();
		buffer.append("PRESTADOR;GUIA;TIPO DE GUIA;DATA ATENDIMENTO;SITUACAO;VALOR");
		buffer.append(NOVA_LINHA);
		
		for (Prestador prestador : mapaGuiasPorPrestador.keySet()) {
			for (GuiaSimples guia : mapaGuiasPorPrestador.get(prestador)) {
				buffer.append(prestador.getPessoaJuridica().getFantasia());
				buffer.append(NOVO_CAMPO);
				buffer.append(guia.getAutorizacao());
				buffer.append(NOVO_CAMPO);
				buffer.append(guia.getTipo());
				buffer.append(NOVO_CAMPO);
				buffer.append(Utils.format(guia.getDataAtendimento()));
				buffer.append(NOVO_CAMPO);
				buffer.append(guia.getSituacao().getDescricao());
				buffer.append(NOVO_CAMPO);
				buffer.append(guia.getValorTotal());
				buffer.append(NOVA_LINHA);
			}
		}
		
		Utils.criarArquivo("c:\\Guias_Por_Prestador.csv", "", buffer);
		
	}

	public static void alimentarMapa(List<GuiaSimples> guias) {
		for (GuiaSimples guia : guias) {
			if(!mapaGuiasPorPrestador.keySet().contains(guia.getPrestador())) {
				Set<GuiaSimples> guiasDoPrestador = new HashSet<GuiaSimples>();
				guiasDoPrestador.add(guia);
				mapaGuiasPorPrestador.put(guia.getPrestador(), guiasDoPrestador);
			}else {
				mapaGuiasPorPrestador.get(guia.getPrestador()).add(guia);
			}
		}
	}

}
