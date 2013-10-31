package br.com.infowaypi.ecarebc.service.internacao;

import static br.com.infowaypi.msr.utils.Assert.isNotNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.atendimentos.alta.MotivoAlta;
import br.com.infowaypi.ecarebc.atendimentos.enums.ValidateGuiaEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

@SuppressWarnings("unchecked")
public class RegistrarEvolucaoService extends Service implements Serializable{

	public ResumoGuias<GuiaSimples> buscarGuias(String autorizacao, String cartao) throws Exception{
		
		boolean isAutorizacaoVazia = StringUtils.isEmpty(autorizacao);
		boolean isCartaoVazio = StringUtils.isEmpty(cartao);
		
		ResumoGuias<GuiaSimples> resumo = new ResumoGuias<GuiaSimples>();
		
		if(isAutorizacaoVazia && isCartaoVazio) {
			throw new Exception("Caro Usu�rio, informe pelo menos um campo!");
		}
		if (!isAutorizacaoVazia && !isCartaoVazio) {
			throw new Exception("Caro Usu�rio, informe somente um dos campos!");
		}
		
		List<String> situacoesDasGuias = Arrays.asList(
				SituacaoEnum.ABERTO.descricao(), SituacaoEnum.PRORROGADO.descricao(), 
				SituacaoEnum.NAO_PRORROGADO.descricao());
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new In("situacao.descricao", situacoesDasGuias));
		
		if (!isAutorizacaoVazia) {
			
			sa.addParameter(new Equals("autorizacao",autorizacao));
			GuiaInternacao guia = sa.uniqueResult(GuiaInternacao.class);
			
			Assert.isNotNull(guia, "Caro usu�rio, n�o foram encontradas guias de interna��o aptas ao processo de registro de evolu��o com esta autoriza��o.");
			Assert.isNull(guia.getAltaHospitalar(), "Caro usu�rio, j� foi registrado alta para essa interna��o.");
			
			ValidateGuiaEnum.PROCEDIMENTO_NIVEL_2_SITUACAO_VALIDATOR.getValidator().execute(guia);

			guia.tocarObjetos();
			resumo.getGuias().add(guia);
			
		} else if (!isCartaoVazio) {
			
			sa.addParameter(new Equals("segurado.numeroDoCartao", cartao));
			
			resumo.getGuias().addAll(sa.list(GuiaInternacao.class));
			Assert.isNotEmpty(resumo.getGuias(), "Caro usu�rio, n�o foi encontrada guias de interna��o aptas ao processo de registro de evolu��o para este segurado.");
		}
		return resumo; 
	}
	

	public GuiaSimples<ProcedimentoInterface> selecionarGuiaEvolucao(GuiaInternacao guia) throws Exception {
		isNotNull(guia, "Nenhuma guia foi selecionada!");
		guia.tocarObjetos();
		if (guia.getAltaHospitalar() != null)
			throw new Exception("Caro usu�rio, j� foi registrado alta para essa interna��o.");
		
		return guia;
	}
	
	public void salvarGuia(GuiaInternacao guia) throws Exception {
		guia.getGuiasFilhas().size();
		ImplDAO.save(guia);
	}

	public GuiaInternacao registrarEvolucao(UsuarioInterface usuario,GuiaInternacao guia,
			String descricao,Boolean isRegistrarAlta,Date dataAlta, MotivoAlta motivo) throws Exception{
		
		if (isRegistrarAlta){
			Assert.isNotNull(dataAlta, "Caro Usu�rio, data de alta � um campo requerido para o registro de alta.");
			
			if (dataAlta.after(new Date())){
				throw new Exception("A data da alta n�o pode ser maior que hoje.");
			}
			else if (dataAlta.before(guia.getDataAtendimento())) {
				throw new Exception("A data da alta n�o pode ser antes da data de atendimento.");
			}
			
			guia.registrarAlta(dataAlta, usuario, motivo);
			guia.setDataTerminoAtendimento(dataAlta);
		}
		else{
			Assert.isNotNull(descricao, "Caro Usu�rio, informe o Quadro cl�nico.");
			Assert.isNull(dataAlta, "Caro Usu�rio, data de alta � um campo requerido apenas para o registro de alta.");
			Assert.isNull(motivo, "Caro Usu�rio, motivo de alta � um campo requerido apeanas para o registro de alta.");
			guia.addQuadroClinico(descricao);
		}
		
		return guia;
	}	
}
