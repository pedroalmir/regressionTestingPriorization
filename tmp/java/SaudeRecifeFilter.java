package br.com.infowaypi.ecare.utils;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.utils.Utils;

public class SaudeRecifeFilter implements Filter{

	static String pathLog = "/home/recife/SRlog.log";
	static Logger logger = startLog();
	   
	@Override
	public void destroy() {}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		gerarLog(request, true);
		chain.doFilter(request,response);
		gerarLog(request, false);
	}

	private void gerarLog(ServletRequest request, boolean ida){
		StringBuilder sb = new StringBuilder();
		if(ida){
			sb.append("ida,");
		} else {
			sb.append("volta,");
		}
		sb.append("class - "+request.getParameter("className")+",");
		sb.append("flow - "+request.getParameter("flowName")+",");
		sb.append("step - "+request.getParameter("step")+",");
		sb.append("report - "+request.getParameter("reportName")+",");
			
		sb.append(Utils.format(new Date(), "dd/MM/yyyy  HH:mm:ss")+",");
		sb.append(Runtime.getRuntime().freeMemory()/1024/1024+",");
			
		HttpServletRequest httRequest = (HttpServletRequest) request;
		sb.append(httRequest.getSession().getId()+",");
		Usuario user =(Usuario) httRequest.getSession().getAttribute("usuario");
		
		if(user != null){
			sb.append("Nome: "+user.getNome()+" Role: "+user.getRole()+",");
		} else {
			sb.append("Nome:  Role: ,");
		}
			
		sb.append("sessoes ativas "+SRServletListener.contador.contador+";");
		
		logger.debug(sb.toString());
	}
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {}
	
	private static Logger startLog(){
		Logger logger = Logger.getLogger(SaudeRecifeFilter.class);  
		
        BasicConfigurator.configure();  
        
        try {  
	        Appender fileAppender = new FileAppender(  
	            new SimpleLayout(), pathLog);  
//	        Layout
	        logger.addAppender(fileAppender);  
	        
	        logger.setLevel(Level.DEBUG);  
          
        } catch (Exception e) {  
            logger.error("Oops, deu erro: " + e.getMessage());  
        } 
        return logger;
	}
}
