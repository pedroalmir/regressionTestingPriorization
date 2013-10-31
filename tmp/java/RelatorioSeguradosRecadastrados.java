/**
 * 
 */
package br.com.infowaypi.ecare.services;

import java.util.Calendar;

import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/**
 * @author Idelvane
 *
 */
public class RelatorioSeguradosRecadastrados {
	
	public ResumoSegurados gerarRelatorio(String dataInicial, String dataFinal, Boolean exibirSegurados, UsuarioInterface usuario) throws ValidateException{
		
		Assert.isNotEmpty(dataInicial, "Data inicial deve ser informada.");
		Assert.isNotEmpty(dataFinal, "Data final deve ser informada.");
		
		this.validaData(dataInicial, dataFinal);
				
		ResumoSegurados resumo = new ResumoSegurados(dataInicial, dataFinal, exibirSegurados);
		resumo.computarRecadastramento();
		
//		Assert.isNotEmpty(resumo.getSegurados(), "Nenhum segurado foi recadastrado neste período!");
		
		return resumo;
	}
	
	public ResumoSegurados gerarRelatorio(String dataInicial, String dataFinal, UsuarioInterface usuario) throws ValidateException{
		return gerarRelatorio(dataInicial, dataFinal, false, usuario);
	}
	
	
	
	public void validaData(String dataInicial, String dataFinal) throws ValidateException{
		
		int anoInicial = Integer.parseInt(dataInicial.substring(6));
		int anoFinal = Integer.parseInt(dataFinal.substring(6));
		
		int mesInicial = Integer.parseInt(dataInicial.substring(3,5));
		int mesFinal = Integer.parseInt(dataFinal.substring(3,5));

		if(anoFinal < anoInicial || (anoFinal == anoInicial && (mesInicial > mesFinal)))
			throw new ValidateException("A data inicial é maior que a data final.");
	}

	public static void main(String[] args) throws ValidateException {
		RelatorioSeguradosRecadastrados resumo = new RelatorioSeguradosRecadastrados();
		resumo.gerarRelatorio("18/06/2007", "21/06/2007", true, null);
	}
}
