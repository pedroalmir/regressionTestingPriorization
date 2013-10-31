package br.com.infowaypi.ecarebc.service.financeiro;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AlteracaoFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.Faturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.retencao.Retencao;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

public class AlteracaoDeFaturamento extends FinanceiroService{
	public Faturamento buscarFaturamento(String competencia, Prestador prestador) throws ValidateException {
		if(prestador == null)
			throw new ValidateException("O prestador deve ser informado.");
		
		if(Utils.isStringVazia(competencia))
			throw new ValidateException("A competência deve ser informada.");
		
		Date competenciaEscolhida = getCompetencia(competencia);
		
		Faturamento faturamento = searchFaturamento(prestador, competenciaEscolhida);
		if(faturamento == null)
			throw new ValidateException("Faturamento não encontrado ou já está fechado!");
		faturamento.getAlteracoesFaturamento().size();
		return faturamento;
	}
	
	private Faturamento searchFaturamento(Prestador prestador, Date competencia) throws ValidateException{
		SearchAgent sa = getSearchAgent();
		sa.addParameter(new Equals("prestador", prestador));
		sa.addParameter(new Equals("competencia", competencia));
		sa.addParameter(new Equals("status", Constantes.FATURAMENTO_ABERTO));
		List<Faturamento> faturamentos = sa.list(Faturamento.class);
		if (faturamentos.size() == 0)
			throw new ValidateException("Esse prestador não teve faturamento este mês");
		return faturamentos.get(0);
	}
	
	public Faturamento alterarDeducao(Faturamento faturamento, Retencao retencao, String valorDeducao) throws Exception {
		if (faturamento == null)
			throw new ValidateException("Faturamento inválido.");
		
		if (retencao == null)
			throw new ValidateException("Retenção inválida.");
		
		if (!Utils.isStringVazia(valorDeducao)) {
			for(Retencao retencaoAtual : faturamento.getRetencoes()){
				if(retencaoAtual.getIdRetencao().equals(retencao.getIdRetencao()))
					retencaoAtual.setValorDeducao(new BigDecimal(Utils.createFloat(valorDeducao)));
				ImplDAO.save(retencaoAtual);
			}
			//faturamento.processarRetencoes();
			ImplDAO.save(faturamento);
		}
		return faturamento;
	}
	
	public Faturamento alterarValor(Faturamento faturamento, BigDecimal valorIncremento, BigDecimal valorDecremento, String motivo, UsuarioInterface usuario) throws Exception {
		if (faturamento == null)
			throw new ValidateException("Faturamento inválido.");

		if(valorIncremento == null)
			valorIncremento = BigDecimal.ZERO;
		if(valorDecremento == null)
			valorDecremento = BigDecimal.ZERO;
		
		BigDecimal saldo = valorIncremento.subtract(valorDecremento);
		BigDecimal novoValorBruto = faturamento.getValorBruto().add(saldo);
		BigDecimal novoValorLiquido = faturamento.getValorLiquido().add(saldo);
		
		if(novoValorBruto.compareTo(BigDecimal.ZERO) < 0)
			throw new  ValidateException("Valores inválidos. O Valor Bruto não pode ficar negativo.");
		
//		if(novoValorLiquido.compareTo(BigDecimal.ZERO) < 0)
//			throw new  ValidateException("Valores inválidos. O Valor Liquido não pode ficar negativo.");
		
		if (!Utils.isStringVazia(motivo)) {
			AlteracaoFaturamento alteracaoFaturamento = new AlteracaoFaturamento();
			alteracaoFaturamento.setValorIncremento(valorIncremento);
			alteracaoFaturamento.setValorDecremento(valorDecremento);
			alteracaoFaturamento.setMotivo(motivo);
			alteracaoFaturamento.setData(new Date());
			alteracaoFaturamento.setStatus(AlteracaoFaturamento.STATUS_ATIVO);
			faturamento.getAlteracoesFaturamento().add(alteracaoFaturamento);
			alteracaoFaturamento.setFaturamento(faturamento);
			alteracaoFaturamento.setUsuario(usuario);
			faturamento.setValorBruto(novoValorBruto);
			//faturamento.processarRetencoes();
			
		}
		else{
			throw new ValidateException("Preencha o motivo");
		}
		return faturamento;
	}

	public Faturamento conferirDados(Faturamento faturamento) throws Exception{
		ImplDAO.save(faturamento);
		return faturamento;
	}
	
	public void finalizar(Faturamento faturamento){}
	
//	private void recalcularRentencoes(Faturamento faturamento){
//		
//		Integer tipoDePessoa = faturamento.getPrestador().isPessoaFisica() ? AbstractImposto.PESSOA_FISICA : AbstractImposto.PESSOA_JURIDICA;
//		
//		BigDecimal valorBase = MoneyCalculation.rounded(faturamento.getValorBruto().add(faturamento.getValorOutros()));
//		BigDecimal valorBruto = MoneyCalculation.rounded(faturamento.getValorBruto().add(faturamento.getValorOutros()));
//		
//		SearchAgent sa = new SearchAgent();
//		ImpostoInterface impostoINSS = (ImpostoInterface) sa.list(Inss.class).get(0);
//		ImpostoInterface impostoISS = (ImpostoInterface) sa.list(Iss.class).get(0);
//		ImpostoInterface impostoDeRenda = (ImpostoInterface) sa.list(ImpostoDeRenda.class).get(0);
//		
//		for (Retencao retencao : faturamento.getRetencoes()) {
//			retencao.setValorBaseDoCalculo(valorBruto);
//			retencao.
////			if(retencao.getDescricao().equals(impostoISS.getDescricao())){
////				retencao.
////			}else if(retencao.getDescricao().equals(impostoINSS.getDescricao())){
////				
////			}else if(retencao.getDescricao().equals(impostoDeRenda.getDescricao())){
////				
////			}
//		}
//		
//	}

}
