package br.com.infowaypi.sensews.client;

import br.com.infowaypi.ecarebc.atendimentos.GuiaConsulta;
import br.com.infowaypi.molecular.ImplDAO;

public class Test {
	public static void main(String[] args) throws Exception {
		
//		System.out.println("Update base de dados");
//		Configuration conf = new Configuration().configure();
//		new SchemaUpdate(conf).execute(true,true);
		
//		SearchAgent sa = new SearchAgent();
//		sa.addParameter(new OrderByDesc("dataMarcacao"));
//		List<GuiaConsulta> list = sa.list(GuiaConsulta.class, 0, 1000);
//		
//		System.out.println("Guias: " + list.size());
//		
//		Thread.currentThread().sleep(5000);
//		
//		for (GuiaConsulta g : list) {
//			System.out.println(g.getIdGuia() + " " + g.getDataMarcacao());
//			Guia guiaSense = new SenseGuiaConverter().convertGuia(g, false);
//			SenseAnalizer.execute(guiaSense);
//			Thread.currentThread().sleep(1000);
//		}
		
//		GuiaConsulta guiaOriginal = ImplDAO.findById(1822231L, GuiaConsulta.class);
		GuiaConsulta guiaOriginal = ImplDAO.findById(47L, GuiaConsulta.class);
//		SenseAnalizer.execute(guiaOriginal);
	}
}