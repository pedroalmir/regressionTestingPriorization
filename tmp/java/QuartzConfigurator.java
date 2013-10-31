package br.com.infowaypi.ecare.scheduller;

import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;

 
public class QuartzConfigurator implements PlugIn {

	public void init(ActionServlet actionServlet, ModuleConfig config) {
		  try{
			  RunTasks.execute();
		  } catch (Exception e){
		     e.printStackTrace();
		  }
	}
	
	public void destroy() {
		
	}
}
