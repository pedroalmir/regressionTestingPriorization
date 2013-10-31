package br.com.infowaypi.ecarebc.service.autorizacoes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExameOdonto;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOdonto;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Service básico para autorização de procedimentos odontológicos pela central de serviços
 * @author Danilo Nogueira Portela
 */
public class AutorizarProcedimentosOdontoEspeciaisService extends Service{
	
	public AutorizarProcedimentosOdontoEspeciaisService(){
		super();
	}
	
	public  ResumoGuias buscarGuias(String autorizacao, String dataInicial, String dataFinal,Prestador prestador) throws Exception {
		List<GuiaExameOdonto> guias = new ArrayList<GuiaExameOdonto>();
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("situacao.descricao", SituacaoEnum.AGENDADA.descricao()));
				
		guias = super.buscarGuias(sa, autorizacao, null, dataInicial, dataFinal, prestador,  false,false, GuiaExameOdonto.class, GuiaSimples.DATA_DE_SITUACAO);
		
		Assert.isNotEmpty(guias, MensagemErroEnum.NENHUMA_GUIA_ENCONTRADA.getMessage());
		
		ResumoGuias<GuiaExameOdonto> resumo = new ResumoGuias<GuiaExameOdonto>(guias, ResumoGuias.SITUACAO_AGENDADA,false);
		return resumo;
	}
	
	public GuiaExameOdonto selecionarGuia(GuiaExameOdonto guia) {
		Assert.isNotNull(guia, MensagemErroEnum.NENHUMA_GUIA_SELECIONADA.getMessage());
		guia.tocarObjetos();
		return guia;
	}
	
	public GuiaExameOdonto autorizarProcedimentosOdontoEspeciais(
			GuiaExameOdonto guia, 
			Boolean ignorarValidacao, 
			Collection<ProcedimentoOdonto> procedimentos, 
			UsuarioInterface usuario) throws Exception {
		
		if(procedimentos != null) 
			guia.addAllProcedimentos(procedimentos);
		
		if(!ignorarValidacao)
			guia.validate();
		
		return guia;
	}
	
	public GuiaExameOdonto conferirDados(GuiaExameOdonto guia) throws Exception {
		ImplDAO.save(guia);
		ImplDAO.save(guia.getPrestador().getConsumoIndividual());
		ImplDAO.save(guia.getSegurado().getConsumoIndividual());
		return guia;
	}
}
