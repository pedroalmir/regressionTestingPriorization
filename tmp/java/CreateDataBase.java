package br.com.infowaypi.ecare.correcaomanual;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;

public class CreateDataBase {

	public static void main(String[] args) throws Exception {
		gereDatabase();
	}
	
	public static void gereDatabase() {
		new SchemaUpdate(getConfiguration()).execute(true, true);
	}

	public static Configuration getConfiguration() {
		return new Configuration().configure();
	}
}