package br.com.infoway.ecarebc.service.noticias;

import java.util.ArrayList;
import java.util.List;

import br.com.infowaypi.msr.noticia.NoticiaInterface;
import br.com.infowaypi.msr.utils.Assert;

public class ResumoNoticias {
	
    private List<NoticiaInterface> noticias = new ArrayList<NoticiaInterface>();
	
	public ResumoNoticias(List<NoticiaInterface> notices){
		for(NoticiaInterface noticia: notices){
			if(noticia.isAtiva()){
				noticias.add(noticia);
			}
		}
	}
	
	public List<NoticiaInterface> getNoticias() {
		Assert.isNotEmpty(this.noticias, "Nenhuma notícia ativa foi encontrada.");
		return noticias;
		
	}

}
