package br.com.infoway.ecarebc.service.noticias;

import java.util.List;

import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.noticia.Noticia;
import br.com.infowaypi.msr.noticia.NoticiaInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

public class NoticiaService {
	
	public NoticiaInterface inserirNova(String tipoNoticia,String titulo, String corpo, String destino, Boolean pagPrincipal, Boolean ativa) throws Exception{
		
		if(Utils.isStringVazia(titulo))
			throw new ValidateException("O campo T�tulo deve ser preenchido!");
		
		if(Utils.isStringVazia(corpo))
			throw new ValidateException("O campo Corpo deve ser preenchido!");
		
		NoticiaInterface noticia = new Noticia();
		noticia.setAtiva(ativa);
		noticia.setTitulo(titulo);
//		noticia.setTipoAviso(tipoNoticia);
		noticia.setCorpo(corpo);
		noticia.setSomenteNaPaginaInicial(pagPrincipal);
		noticia.setDestino(destino);
		ImplDAO.save(noticia);	
		
		return noticia;
	}
	
	@SuppressWarnings("unchecked")
	public ResumoNoticias pesquisarNoticia(String titulo) throws ValidateException{
		SearchAgent sa = new SearchAgent();
		if(Utils.isStringVazia(titulo))
			throw new ValidateException("Informe o t�tulo da not�cia a ser cancelada!");
		
		sa.addParameter(new Equals("titulo", titulo));
		List<NoticiaInterface> noticias = sa.list(Noticia.class);
		Assert.isNotEmpty(noticias, "N�o foi encontrada not�cia com esse t�tulo!");
		ResumoNoticias resumo = new ResumoNoticias(noticias);
		if(resumo == null || resumo.getNoticias().isEmpty())
			throw new ValidateException( "N�o foram encontradas not�cias ativas com esse t�tulo!");
		return resumo;
		
	}
	
	public Noticia cancelarNoticia(Noticia noticia) throws Exception{
		Assert.isNotNull(noticia,"Not�cia inv�lida!");
		noticia.setAtiva(false);
		ImplDAO.save(noticia);
		return noticia;
	}
	
	public void finalizar(NoticiaInterface noticia){}

}
