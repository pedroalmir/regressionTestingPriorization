package br.com.infowaypi.ecare.autenticacao;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.com.infowaypi.ecarebc.enums.Role;
import br.com.infowaypi.jheat.webcontent.controller.JHeatAuthenticatorAction;


public class JHeatAuthenticatorActionSR extends JHeatAuthenticatorAction {
	private static final String TITULAR = "Titular";
	private static final String DEPENDENTE = "Dependente";
	
	@Override
	public ActionForward authentic(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) 	throws Exception {
			ActionForward forward = super.authentic(mapping, form, request, response);
			String[] roles = (String[]) request.getSession().getAttribute(ROLES);
			
			if(forward.getName().equals(HOME)) {
				List<String> rolesList = Arrays.asList(roles);
				
				if (rolesList.contains(Role.TITULAR.getValor())) {
					return mapping.findForward(TITULAR);
					
				} else
					if (rolesList.contains(Role.DEPENDENTE.getValor())) {
						return mapping.findForward(DEPENDENTE);
					}
			}
			
			return forward;
	}

}
