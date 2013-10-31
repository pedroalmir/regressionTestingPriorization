package br.com.infowaypi.ecare.services.financeiro.consignacao;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecare.enums.SituacaoEnum;
import br.com.infowaypi.ecare.segurados.tuning.MatriculaTuning;
import br.com.infowaypi.ecarebc.financeiro.TitularFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.arquivos.ArquivoInterface;
import br.com.infowaypi.ecarebc.financeiro.consignacao.Consignacao;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.OrderBy;
import br.com.infowaypi.msr.situations.Situacao;

@SuppressWarnings({"unchecked"})
public class InativacaoMatriculaDuplicada {

	public static List<MatriculaTuning> getMatriculas() {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("ativa", true));
		sa.addParameter(new OrderBy("idMatricula"));
		return sa.list(MatriculaTuning.class);
		
	}
	
	
	public static void main(String[] args) throws Exception {
		FileOutputStream fos = new FileOutputStream(new File(ArquivoInterface.REPOSITORIO + "MATRICULAS_DUPLICADAS_CANCELADAS.csv"));
		StringBuffer log = new StringBuffer();
		
		HibernateUtil.currentSession().getTransaction().begin();
			
		Set<MatriculaTuning> matriculas = new HashSet<MatriculaTuning>();
		List<MatriculaTuning> matriculasDuplicadas = new ArrayList<MatriculaTuning>();
		
		for (MatriculaTuning matricula : getMatriculas()) {
			if (!matriculas.add(matricula)) {
				log.append(matricula.getIdMatricula());
				log.append("\n");
				matriculasDuplicadas.add(matricula);
			}
		}

		fos.write(log.toString().getBytes());
		fos.close();
		
		for (MatriculaTuning matriculaDuplicada : matriculasDuplicadas) {
			for (Consignacao<TitularFinanceiroInterface> consignacao : matriculaDuplicada.getConsignacoes()) {
				consignacao.setSituacao(new Situacao(SituacaoEnum.INATIVO.getDescricao()));
				ImplDAO.save(consignacao);
			}
			matriculaDuplicada.setAtiva(false);
			ImplDAO.save(matriculaDuplicada);
			System.out.println(matriculaDuplicada.getIdMatricula());
			break;
		}
		HibernateUtil.currentSession().getTransaction().commit();
	}
}