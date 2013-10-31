package br.com.infowaypi.ecare.relatorio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemGasoterapia;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.Faturamento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

public class RelatorioGlosas { 
	
	public static final BigDecimal zero = BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP);	
	public static final String quebraDeLinha = System.getProperty("line.separator");
	public static String separator = ";";
	
	private List<GuiaExame<ProcedimentoInterface>> guiasExame = new ArrayList<GuiaExame<ProcedimentoInterface>>();
	private List<GuiaCompleta> guiasCompleta = new ArrayList<GuiaCompleta>();
	
	public List<GuiaExame<ProcedimentoInterface>> buscarGuiasExame(Date competencia, Prestador prestador) {
//		Criteria crit = HibernateUtil.currentSession().createCriteria(GuiaExame.class);
//		crit.add(Restrictions.eq("prestador", prestador));
		
		Character[] situacoes = {'A', 'F'};
		
		Faturamento faturamentoPrestador = (Faturamento) HibernateUtil.currentSession().createCriteria(Faturamento.class)
			.add(Expression.eq("competencia", competencia))
			.add(Expression.eq("prestador", prestador))
			.add(Expression.in("status", situacoes))
			.setFetchMode("guias", FetchMode.SELECT)
			.setFetchMode("retencoes", FetchMode.SELECT)
			.setFetchMode("honorariosMedicos", FetchMode.SELECT)
			.setFetchMode("alteracoesFaturamento", FetchMode.SELECT)
			.setFetchMode("colecaoContas", FetchMode.SELECT)
			.setFetchMode("informacaoFinanceira", FetchMode.SELECT)
			.setFetchMode("prestador", FetchMode.SELECT)
			.uniqueResult();
		
		System.out.println("prestador do faturamento: "+faturamentoPrestador.getPrestador().getPessoaJuridica().getFantasia());
		
		this.guiasExame = HibernateUtil.currentSession().createCriteria(GuiaExame.class)
			.add(Expression.eq("faturamento", faturamentoPrestador))
			.add(Expression.ge("valorMedicamentoComplementarSolicitado", BigDecimal.ZERO))
			.add(Expression.ge("valorMaterialComplementarSolicitado", BigDecimal.ZERO))
			.list();
		
		System.out.println(guiasExame.size());
		
		return guiasExame;
	}
	
	public List<GuiaCompleta> buscarGuiasCompleta(Date competencia, Prestador prestador) {
		Character[] situacoes = {'A', 'F'};
		
		Faturamento faturamentoPrestador = (Faturamento) HibernateUtil.currentSession().createCriteria(Faturamento.class)
			.add(Expression.eq("competencia", competencia))
			.add(Expression.eq("prestador", prestador))
			.add(Expression.in("status", situacoes))
			.setFetchMode("guias", FetchMode.SELECT)
			.setFetchMode("retencoes", FetchMode.SELECT)
			.setFetchMode("honorariosMedicos", FetchMode.SELECT)
			.setFetchMode("alteracoesFaturamento", FetchMode.SELECT)
			.setFetchMode("colecaoContas", FetchMode.SELECT)
			.setFetchMode("informacaoFinanceira", FetchMode.SELECT)
			.setFetchMode("prestador", FetchMode.SELECT)
			.uniqueResult();
		
		this.guiasCompleta = HibernateUtil.currentSession().createCriteria(GuiaCompleta.class)
			.add(Expression.eq("faturamento", faturamentoPrestador))
			.list();
		
		System.out.println(guiasCompleta.size());
		
		return guiasCompleta;
	}
	
	public void processarGuiasExame(List<GuiaExame<ProcedimentoInterface>> guias) throws Exception {
		BigDecimal valorTotalMateriaisGlosados = BigDecimal.ZERO;
		BigDecimal valorTotalMedicamentosGlosados = BigDecimal.ZERO;
		StringBuffer buffer = new StringBuffer();
		buffer.append("Exame");
		buffer.append(quebraDeLinha);
		buffer.append("Guia");
		buffer.append(separator);
		buffer.append("ValorMateriaisGlosados");
		buffer.append(separator);
		buffer.append("ValorMedicamentosGlosados");
		buffer.append(quebraDeLinha);
		
		
		for (GuiaExame<ProcedimentoInterface> guiaExame : guias) {
			BigDecimal valorMedicamentosPrestador = guiaExame.getValorMedicamentoComplementarSolicitado();
			BigDecimal valorMedicamentosAuditor = guiaExame.getValorMedicamentoComplementarAuditado();
			BigDecimal valorMateriaisPrestador = guiaExame.getValorMaterialComplementarSolicitado();
			BigDecimal valorMateriaisAuditor = guiaExame.getValorMaterialComplementarAuditado();
			
			
			
			boolean isValorMedicamentoGlosado = valorMedicamentosAuditor.compareTo(valorMedicamentosPrestador) < 0;
			boolean isValorMateriaisGlosado = valorMateriaisAuditor.compareTo(valorMateriaisPrestador) < 0;
			boolean houveGlosa = false;
			
			if(isValorMateriaisGlosado || isValorMedicamentoGlosado) {
				houveGlosa = true;
			}
			
			if(houveGlosa) {
//				System.out.println("GUIA: "+ guiaExame.getAutorizacao());
				buffer.append(guiaExame.getAutorizacao());
				buffer.append(separator);
				if(isValorMateriaisGlosado) {
					buffer.append(valorMateriaisPrestador.subtract(valorMateriaisAuditor).setScale(2,BigDecimal.ROUND_HALF_UP));
					buffer.append(separator);
//					System.out.println("Valor Materiais glosado: R$"+ valorMateriaisPrestador.subtract(valorMateriaisAuditor).setScale(2,BigDecimal.ROUND_HALF_UP));
					valorTotalMateriaisGlosados = valorTotalMateriaisGlosados.add(valorMateriaisPrestador.subtract(valorMateriaisAuditor)).setScale(2,BigDecimal.ROUND_HALF_UP);
				}else{
					buffer.append(zero);
					buffer.append(separator);
				}
				if(isValorMedicamentoGlosado) {
					buffer.append(valorMedicamentosPrestador.subtract(valorMedicamentosAuditor).setScale(2,BigDecimal.ROUND_HALF_UP));
//					System.out.println("Valor Medicamentos glosado: R$"+ valorMedicamentosPrestador.subtract(valorMedicamentosAuditor).setScale(2,BigDecimal.ROUND_HALF_UP));
					valorTotalMedicamentosGlosados = valorTotalMedicamentosGlosados.add(valorMedicamentosPrestador.subtract(valorMedicamentosAuditor)).setScale(2,BigDecimal.ROUND_HALF_UP);
				}else {
					buffer.append(zero);
				}
//				System.out.println("----------------------------------------");
				buffer.append(System.getProperty("line.separator"));
			}
		}
		
		Utils.criarArquivo("C:\\Glosas_Guias_De_Exame.txt", "", buffer);
//		System.out.println("Valor total de materiais glosados: R$"+ valorTotalMateriaisGlosados);
//		System.out.println("Valor total de medicamentos glosados: R$"+ valorTotalMedicamentosGlosados);
		
//		System.out.println();
//		System.out.println("****************************************");
//		System.out.println();
		buffer.append(quebraDeLinha);
		buffer.append(quebraDeLinha);
		System.out.println(buffer.toString()); 
		
	}
	
	public void processarGuiasCompletas(List<GuiaCompleta> guias) {
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("Guia");
		buffer.append(separator);
		
		buffer.append("Procedimento");
		buffer.append(separator);
		
		buffer.append("");
		buffer.append(separator);
		
		buffer.append("Diária");
		buffer.append(separator);
		
		buffer.append("");
		buffer.append(separator);
		
		buffer.append("Gasoterapia");
		buffer.append(separator);
		
		buffer.append("");
		buffer.append(separator);
		
		buffer.append("Pacote");
		buffer.append(separator);
		
		buffer.append("");
		buffer.append(separator);
		
//		buffer.append("Procedimento");
//		buffer.append(separator);
//
//		buffer.append("ValorMateriaisGlosados");
//		buffer.append(separator);
//		buffer.append("ValorMedicamentosGlosados");
		buffer.append(quebraDeLinha);
		buffer.append(quebraDeLinha);
		buffer.append(quebraDeLinha);
		buffer.append(quebraDeLinha);
		buffer.append(quebraDeLinha);
		
		for (GuiaCompleta<ProcedimentoInterface> guia : guias) {
			boolean glosa = false;
//			System.out.println(guia.getAutorizacao());
			for (ProcedimentoInterface procedimento : guia.getProcedimentos()) {
				boolean isProcedimentoCancelado = (procedimento.getSituacao().getDescricao().equals(SituacaoEnum.CANCELADO.descricao()) ||procedimento.getSituacao().getDescricao().equals(SituacaoEnum.NAO_AUTORIZADO.descricao()));
				boolean isSituacaoAnteriorDoAuditor = (isProcedimentoCancelado && procedimento.getSituacaoAnterior(procedimento.getSituacao()).getUsuario().getRole().equals(UsuarioInterface.ROLE_AUDITOR));
				
				if(!isSituacaoAnteriorDoAuditor) {
					glosa = true;
					buffer.append(procedimento.getProcedimentoDaTabelaCBHPM().getDescricao());
					buffer.append(separator);
					buffer.append(procedimento.getValorTotal().setScale(2,BigDecimal.ROUND_HALF_UP));
					buffer.append(separator);
					System.out.println("Procedimento: "+ procedimento.getProcedimentoDaTabelaCBHPM().getDescricao()+ " Valor R$"+ procedimento.getValorTotal().setScale(2,BigDecimal.ROUND_HALF_UP));
				}
			}
			for (ItemDiaria itemDiaria: guia.getItensDiaria()) {
				boolean isItemDiariaCancelado = (itemDiaria.getSituacao().getDescricao().equals(SituacaoEnum.CANCELADO.descricao()) || itemDiaria.getSituacao().getDescricao().equals(SituacaoEnum.NAO_AUTORIZADO.descricao()));
				boolean isSituacaoAnteriorDoAuditor = (isItemDiariaCancelado && ( itemDiaria.getSituacaoAnterior(itemDiaria.getSituacao()).getUsuario() == null || itemDiaria.getSituacaoAnterior(itemDiaria.getSituacao()).getUsuario().getRole().equals(UsuarioInterface.ROLE_AUDITOR)));
				
				if(!isSituacaoAnteriorDoAuditor) {
					glosa = true;
					System.out.println("Diaria: "+ itemDiaria.getDiaria().getDescricao() + "Valor: R$"+ itemDiaria.getValor().getValor());
				}
			}
			
			for (ItemGasoterapia itemGaso: guia.getItensGasoterapia()) {
				boolean isItemGasoCancelado = (itemGaso.getSituacao().getDescricao().equals(SituacaoEnum.CANCELADO.descricao()) || itemGaso.getSituacao().getDescricao().equals(SituacaoEnum.NAO_AUTORIZADO.descricao()));
				boolean isSituacaoAnteriorDoAuditor = (isItemGasoCancelado && itemGaso.getSituacaoAnterior(itemGaso.getSituacao()).getUsuario().getRole().equals(UsuarioInterface.ROLE_AUDITOR));
				
				if(!isSituacaoAnteriorDoAuditor) {
					glosa = true;
					System.out.println("Gasoterapia: "+ itemGaso.getGasoterapia().getDescricao() + "Valor: R$"+ itemGaso.getValor().getValor());
				}
			}
			
			for (ItemPacote itemPacote: guia.getItensPacote()) {
				boolean isItemPacoteCancelado = (itemPacote.getSituacao().getDescricao().equals(SituacaoEnum.CANCELADO.descricao()) || itemPacote.getSituacao().getDescricao().equals(SituacaoEnum.NAO_AUTORIZADO.descricao()));
				boolean isSituacaoAnteriorDoAuditor = (isItemPacoteCancelado && itemPacote.getSituacaoAnterior(itemPacote.getSituacao()).getUsuario().getRole().equals(UsuarioInterface.ROLE_AUDITOR));
				
				if(!isSituacaoAnteriorDoAuditor) {
					glosa = true;
					System.out.println("Pacote: "+ itemPacote.getPacote().getDescricao() + "Valor: R$"+ itemPacote.getValor().getValor());
				}
			}
			
			BigDecimal valorTotalMateriaisMedicosApartamento = guia.getValoresMatMed().getValorTotalMateriaisMedicosApartamentoGlosado();
			BigDecimal valorTotalMateriaisMedicosBlocoCirurgico = guia.getValoresMatMed().getValorTotalMateriaisMedicosBlocoCirurgicoGlosado();
			BigDecimal valorTotalMateriaisMedicosUTI = guia.getValoresMatMed().getValorTotalMateriaisMedicosUTIGlosado();
			BigDecimal valorTotalMateriaisProntoSocorro = guia.getValoresMatMed().getValorTotalMateriaisProntoSocorroGlosado();
			BigDecimal valorTotalSoulucoesParenterais = guia.getValoresMatMed().getValorTotalSolucoesParenteraisGlosado();
			
			BigDecimal valorTotalMedicamentosApartamento = guia.getValoresMatMed().getValorTotalMedicamentosApartamentoGlosado();
			BigDecimal valorTotalMedicamentosBlocoCirurgico = guia.getValoresMatMed().getValorTotalMedicamentosBlocoCirurgicoGlosado();
			BigDecimal valorTotalMedicamentosUTI = guia.getValoresMatMed().getValorTotalMedicamentosUTIGlosado();
			BigDecimal valorTotalMedicamentosProntoSocorro = guia.getValoresMatMed().getValorTotalMedicamentosProntoSocorroGlosado();
			
			
			if(valorTotalMateriaisMedicosApartamento.compareTo(BigDecimal.ZERO) > 0) {
				glosa = true;
				System.out.println("Valor Materiais Médicos em Apartamento: R$"+ valorTotalMateriaisMedicosApartamento.setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			
			if(valorTotalMateriaisMedicosBlocoCirurgico.compareTo(BigDecimal.ZERO) > 0) {
				glosa = true;
				System.out.println("Valor Materiais Médicos em Bloco Cirúrgico: R$"+ valorTotalMateriaisMedicosBlocoCirurgico.setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			
			if(valorTotalMateriaisMedicosUTI.compareTo(BigDecimal.ZERO) > 0) {
				glosa = true;
				System.out.println("Valor Materiais Médicos em UTI: R$"+ valorTotalMateriaisMedicosUTI.setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			
			if(valorTotalMateriaisProntoSocorro.compareTo(BigDecimal.ZERO) > 0) {
				glosa = true;
				System.out.println("Valor Materiais Médicos em Pronto Socorro: R$"+ valorTotalMateriaisProntoSocorro.setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			
			if(valorTotalSoulucoesParenterais.compareTo(BigDecimal.ZERO) > 0) {
				glosa = true;
				System.out.println("Valor Soluções Parenterais: R$"+ valorTotalSoulucoesParenterais.setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			
			if(valorTotalMedicamentosApartamento.compareTo(BigDecimal.ZERO) > 0) {
				glosa = true;
				System.out.println("Valor Medicamentos em Apartamento: R$"+ valorTotalMedicamentosApartamento.setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			
			if(valorTotalMedicamentosBlocoCirurgico.compareTo(BigDecimal.ZERO) > 0) {
				glosa = true;
				System.out.println("Valor Medicamentos em Bloco Cirúrgico: R$"+ valorTotalMedicamentosBlocoCirurgico.setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			
			if(valorTotalMedicamentosUTI.compareTo(BigDecimal.ZERO) > 0) {
				glosa = true;
				System.out.println("Valor Medicamentos em UTI: R$"+ valorTotalMedicamentosUTI.setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			
			if(valorTotalMedicamentosProntoSocorro.compareTo(BigDecimal.ZERO) > 0) {
				glosa = true;
				System.out.println("Valor Medicamentos em ProntoSocorro: R$"+ valorTotalMedicamentosProntoSocorro.setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			
			if(glosa) {
				System.out.println("GUIA: "+ guia.getAutorizacao());
				System.out.println("-------------------------------------");
			}
		}
	}

	public List<GuiaExame<ProcedimentoInterface>> getGuiasExame() {
		return guiasExame;
	}

	public void setGuiasExame(List<GuiaExame<ProcedimentoInterface>> guiasExame) {
		this.guiasExame = guiasExame;
	}
	
	public static void main(String[] args) throws Exception {
		RelatorioGlosas relatorio = new RelatorioGlosas();
		Prestador hospitalSaoMarcos = ImplDAO.findById(374002l, Prestador.class);
		System.out.println(hospitalSaoMarcos.getPessoaJuridica().getFantasia());
		Date competencia = Utils.parse("01/12/2007");
		relatorio.processarGuiasExame(relatorio.buscarGuiasExame(competencia, hospitalSaoMarcos));
		relatorio.processarGuiasCompletas(relatorio.buscarGuiasCompleta(competencia, hospitalSaoMarcos));
		
//		GuiaCompleta guia = ImplDAO.findById(73392l, GuiaCompleta.class);
//		
//		for (ProcedimentoInterface procedimento : guia.getProcedimentos()) {
//			System.out.println("Procedimento: " + procedimento.getProcedimentoDaTabelaCBHPM().getDescricao());
//			System.out.println("Situação: "+ procedimento.getSituacao().getDescricao());
//		}
//		
//		for (ItemDiaria itemDiaria : guia.getItensDiaria()) {
//			boolean isItemDiariaCancelado = (itemDiaria.getSituacao().getDescricao().equals(SituacaoEnum.CANCELADO.descricao()) || itemDiaria.getSituacao().getDescricao().equals(SituacaoEnum.NAO_AUTORIZADO.descricao()));
//			System.out.println(isItemDiariaCancelado);
//			System.out.println("Diaria: "+ itemDiaria.getDiaria().getDescricao());
//			System.out.println("Situacao: "+ itemDiaria.getSituacao().getDescricao());
//			System.out.println("Situação Anterior: "+ itemDiaria.getSituacaoAnterior(itemDiaria.getSituacao()).getDescricao()+ "Usuario: "+ itemDiaria.getSituacaoAnterior(itemDiaria.getSituacao()).getUsuario() );
//			System.out.println("-------------------------------------");
//		}
//	
//		Long i = (Long) HibernateUtil.currentSession().createCriteria(ItemGuia.class)
//		.setProjection(Projections.rowCount())
//		.add(Expression.eq("situacao.descricao", SituacaoEnum.NAO_AUTORIZADO.descricao()))
//		.uniqueResult();
//		
//		System.out.println(i);
		
	}
	
	
}
