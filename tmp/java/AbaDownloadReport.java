
package br.com.infowaypi.ecare.relatorio;

import br.com.infowaypi.ecare.arquivos.ArquivoDownload;
import br.com.infowaypi.ecare.resumos.ResumoGeral;
import br.com.infowaypi.ecarebc.enums.Role;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.OrderBy;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * @author Dannylvan
 *
 */
public class AbaDownloadReport {
	
	/**
	 * Buscar arquivos ativos no sistema 
	 */
	@SuppressWarnings("unchecked")
	public ResumoGeral<ArquivoDownload> getArquivosDownload(UsuarioInterface usuario){
		SearchAgent sa = new SearchAgent();
		if(!usuario.getRole().equals(Role.AUDITOR.getValor()) && !usuario.getRole().equals(Role.DIRETORIA_MEDICA.getValor())
				&& !usuario.getRole().equals(Role.ROOT.getValor())){
			sa.addParameter(new Equals("disponivelSomenteParaDiretoriaAuditorERoot", false));
		}
		sa.addParameter(new Equals("situacao.descricao", SituacaoEnum.ATIVO.descricao()));
		sa.addParameter(new OrderBy("ordemArquivo"));
		ResumoGeral<ArquivoDownload> resumo = new ResumoGeral<ArquivoDownload>();
		resumo.setLista(sa.list(ArquivoDownload.class));
		return resumo; 
	}

}
