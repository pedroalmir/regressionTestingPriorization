package br.com.infoway.ecare.services.noticias;

import br.com.infoway.ecarebc.service.noticias.NoticiaService;
import br.com.infoway.ecarebc.service.noticias.ResumoNoticias;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.noticia.Noticia;
import br.com.infowaypi.msr.noticia.NoticiaInterface;

public class NoticiaServiceProxy extends NoticiaService{
	
	public NoticiaInterface inserirNova(String tipoNoticia,String titulo, String corpo, String destino, Boolean pagPrincipal, Boolean ativa) throws Exception{
		return super.inserirNova(tipoNoticia, titulo, corpo, destino, pagPrincipal, ativa);
	}
	
	public ResumoNoticias pesquisarNoticia(String titulo) throws ValidateException{
		return super.pesquisarNoticia(titulo);
	}
	
	public Noticia cancelarNoticia(Noticia noticia) throws Exception{
		return super.cancelarNoticia(noticia);
	}
	
	public void finalizar(NoticiaInterface noticia){}

}
