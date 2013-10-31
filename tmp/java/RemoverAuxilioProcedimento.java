package br.com.infowaypi.ecare.correcaomanual;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Transaction;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.honorario.Honorario;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ItemGuiaFaturamento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.user.Usuario;

public class RemoverAuxilioProcedimento {
	
	//52 - GUIA 1708703 - AJUSTE OPERACIONAL	
//	private static final String guia = "1708703";
//	private static final int nivel = Honorario.AUXILIAR_2;
//	private static final String procedimentoCBHPM = "31003079";
	
	//53 - GLOSAR COBRANÇA DE AUX - AJUSTE OPERACIONAL	
//	private static final String guia = "1714841";
//	private static final int nivel = Honorario.AUXILIAR_1;
//	private static final String procedimentoCBHPM = "30729149";
	
	//55 - Exclusão de auxílio cirúrgico - Ajuste operacional
//	private static final String guia = "1673264";
//	private static final int nivel = Honorario.AUXILIAR_1;
//	private static final String procedimentoCBHPM = "31309127";

	//56 - Exluir honorário Guia 1714842 - Ajuste operacional
//	private static final String guia = "1714842";
//	private static final int nivel = Honorario.AUXILIAR_3;
//	private static final String procedimentoCBHPM = "30725127";
	

	public static void main(String[] args) throws Exception {
		Transaction tx = HibernateUtil.currentSession().beginTransaction();
		
		removerHonorario("1708703", Honorario.AUXILIAR_2, "31003079");
		System.out.println("========================================================");
		removerHonorario("1714841", Honorario.AUXILIAR_1, "30729149");
		System.out.println("========================================================");
		removerHonorario("1673264", Honorario.AUXILIAR_1, "31309127");
		System.out.println("========================================================");
		removerHonorario("1714842", Honorario.AUXILIAR_3, "30725127");
		
		tx.commit();
		
	}

	private static void removerHonorario(String guia, int nivel, String procedimentoCBHPM) throws Exception {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("autorizacao", guia));
		
		GuiaCompleta<ProcedimentoInterface> guiaAlteracao = sa.uniqueResult(GuiaCompleta.class);
		
		System.out.println("Procedimentos a serem analisados: " + guiaAlteracao.getProcedimentos().size());

		imprimeHonorario(guiaAlteracao, nivel, procedimentoCBHPM);
		
		BigDecimal valorTotalGuiaAntesIntervencao = guiaAlteracao.getValorTotal();
		
		for(ProcedimentoInterface procedimento : guiaAlteracao.getProcedimentos()){
			if(procedimento.getProcedimentoDaTabelaCBHPM().getCodigo().equals(procedimentoCBHPM) && 
					!procedimento.getHonorariosGuiaOrigem().isEmpty()){
				
				
				for(Honorario honorario : procedimento.getHonorariosGuiaOrigem()){
					if(honorario.getGrauDeParticipacao() == nivel && honorario.isSituacaoAtual(SituacaoEnum.GERADO.descricao())){
						honorario.mudarSituacao(ImplDAO.findById(1L, Usuario.class), SituacaoEnum.GLOSADO.descricao(), "Solicitado pela diretoria de saúde.", new Date());
						guiaAlteracao.recalcularValores();
						guiaAlteracao.updateValorCoparticipacao();
						System.out.println("Honorario glosado...");
						System.out.println("Situação do honorario alvo: " + honorario.getSituacao().getDescricao());
						System.out.println("Valor total da guia após intervenção: " + guiaAlteracao.getValorTotalFormatado());
						System.out.println("Diferença após glosa: " + calculaDiferenca(valorTotalGuiaAntesIntervencao,guiaAlteracao.getValorTotal()));
						ImprimirItemGuiaFaturamento(procedimento, nivel);
						removerItemGuiaFaturamento(guiaAlteracao, procedimento, nivel);
					}
				}
			}
		}
	}
	
	private static String calculaDiferenca(BigDecimal valorTotalGuiaAntesIntervencao, BigDecimal valorAtual){
		return StringUtils.leftPad(new DecimalFormat("###,###,##0.00")
		.format((valorTotalGuiaAntesIntervencao.subtract(valorAtual))
		.setScale(2, BigDecimal.ROUND_HALF_UP)), 9, " ");
	}
	
	private static void imprimeHonorario(GuiaSimples<ProcedimentoInterface> guia, int nivel, String procedimentoCBHPM){
		for(ProcedimentoInterface procedimento : guia.getProcedimentos()){
			if(procedimento.getProcedimentoDaTabelaCBHPM().getCodigo().equals(procedimentoCBHPM) && 
					!procedimento.getHonorariosGuiaOrigem().isEmpty()){
				for(Honorario honorario : procedimento.getHonorariosGuiaOrigem()){
					if(honorario.getGrauDeParticipacao() == nivel && honorario.isSituacaoAtual(SituacaoEnum.GERADO.descricao())){
						System.out.println("Guia: " + guia.getAutorizacao() + " - " + guia.getSituacao().getDescricao());
						System.out.println("Procedimento: " + procedimento.getProcedimentoDaTabelaCBHPM().getCodigoEDescricao());
						System.out.println("Honorario - " + honorario.getProfissional().getPessoaFisica().getNome() + " grau: " + honorario.getGrauDeParticipacaoFormatado() + " - Valor honorario: " + honorario.getValorTotalFormatado());
						System.out.println("Situação do honorario alvo: " + honorario.getSituacao().getDescricao());
						System.out.println("Valor total da guia: " + guia.getValorTotalFormatado());
						
					}
				}
			}
		}
	}
	
	private static void ImprimirItemGuiaFaturamento(ProcedimentoInterface procedimento, int nivel){
		List<ItemGuiaFaturamento> itensGuiaFaturamento = getItesnGuiaFaturamento(procedimento, nivel);
		
		if(itensGuiaFaturamento.isEmpty()){
			System.out.println("Não foram encontrados IGF");
		}else{
			for(ItemGuiaFaturamento itemGuiaFaturamento : itensGuiaFaturamento){
				System.out.println("Item Guia Faturamento: " + itemGuiaFaturamento.getIdItemGuiaFaturamento() + " - Profissional: "
						+ itemGuiaFaturamento.getProfissional().getPessoaFisica().getNome());
			}
		}
	}
	
	private static void removerItemGuiaFaturamento(GuiaSimples<ProcedimentoInterface> guia, ProcedimentoInterface procedimento, int nivel) throws Exception{
		List<ItemGuiaFaturamento> itensGuiaFaturamento = getItesnGuiaFaturamento(procedimento, nivel);
		
		for(ItemGuiaFaturamento itemGuiaFaturamento : itensGuiaFaturamento){
			System.out.println("Os seguintes IGF's foram removidos:");
			System.out.println("   IGF: " + itemGuiaFaturamento.getIdItemGuiaFaturamento() + " - Profissional: "
					+ itemGuiaFaturamento.getProfissional().getPessoaFisica().getNome() + " - Valor IGF: " + itemGuiaFaturamento.getValor());
			
			guia.getItensGuiaFaturamento().remove(itemGuiaFaturamento);
			ImplDAO.delete(itemGuiaFaturamento);
		}
	}

	private static List<ItemGuiaFaturamento> getItesnGuiaFaturamento(ProcedimentoInterface procedimento, int nivel) {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("procedimento", procedimento));
		sa.addParameter(new Equals("grauParticipacao", nivel));
		
		List<ItemGuiaFaturamento> itensGuiaFaturamento = sa.list(ItemGuiaFaturamento.class);
		return itensGuiaFaturamento;
	}
}
