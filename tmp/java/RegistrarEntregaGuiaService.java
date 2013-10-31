package br.com.infowaypi.ecare.services.internacao;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecare.enums.MotivoEnumSR;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

public class RegistrarEntregaGuiaService extends Service implements Serializable{

	public ResumoGuias<GuiaCompleta> buscarGuias(String autorizacao, Prestador prestador) throws Exception{
		List<String> situacoes = Arrays.asList(SituacaoEnum.ENVIADO.descricao(),
												SituacaoEnum.FECHADO.descricao(),
												SituacaoEnum.DEVOLVIDO.descricao());
		
		Criteria criteria = HibernateUtil.currentSession().createCriteria(GuiaCompleta.class)
							.add(Expression.in("situacao.descricao", situacoes));
		
		if (autorizacao == null && prestador ==  null)
			throw new RuntimeException("Pelo menos um campo deve ser preenchido.");
		
		if (autorizacao != null){
			criteria.add(Expression.eq("autorizacao", autorizacao));
		} else {
			Assert.isTrue(prestador.isExigeEntregaLote(), "Esse prestador não exige a entrega de lote.");
			
			criteria.add(Expression.eq("prestador", prestador));
		}
		
		List<GuiaCompleta> guias = criteria.list();
		Assert.isNotEmpty(guias,"Nenhuma guia foi encontrada.");
		
		if (guias.size() == 1) {
			Assert.isTrue(guias.get(0).getPrestador().isExigeEntregaLote(),
					"Esse prestador não exige a entrega de lote.");
		}
		
		for (GuiaCompleta guia : guias) {
			guia.getSituacoes().size();
		}
		ResumoGuias<GuiaCompleta> resumo = new ResumoGuias<GuiaCompleta>();
		resumo.getGuias().addAll(guias);
		
		return resumo;
	}
	
	public ResumoGuias<GuiaCompleta> selecionarGuias(Date dataEntregaGuiaFisica,Collection<GuiaCompleta> guias, ResumoGuias<GuiaCompleta> resumo, UsuarioInterface usuario) {
		Assert.isNotEmpty(guias, "Nenhuma guia foi selecionada!");
		Assert.isTrue(dataEntregaGuiaFisica.before(new Date()), "A data de recebimento deve ser menor ou igual à data atual.");
		
		for (GuiaCompleta guia : guias) {
			boolean dataEntregaPosteriorFechamento = Utils.compareData(dataEntregaGuiaFisica, guia.getSituacao().getData()) >= 0;
			Assert.isTrue(dataEntregaPosteriorFechamento,"A guia "+guia.getAutorizacao()+" deve possuir data de recebimento posterior à data de fechamento.");
			guia.setDataEntregaGuiaFisica(dataEntregaGuiaFisica);
			guia.setDataRecebimento(dataEntregaGuiaFisica);
			guia.mudarSituacao(usuario, SituacaoEnum.RECEBIDO.descricao(), MotivoEnumSR.GUIA_FISICA_ENTREGUE.getMessage(), new Date());
		}
		resumo.getGuias().clear();
		resumo.getGuias().addAll(guias);
		return resumo;
	}
	
//	@Override
	public void salvarGuia(ResumoGuias<GuiaCompleta> resumo) throws Exception {
		for (GuiaCompleta guia : resumo.getGuias()) {
			ImplDAO.save(guia);
		}
	}

}
