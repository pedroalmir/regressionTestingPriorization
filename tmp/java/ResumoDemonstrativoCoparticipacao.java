package br.com.infowaypi.ecare.relatorio.portalBeneficiario.extratoCoparticipacao;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecare.financeiro.conta.DetalheContaTitular;
import br.com.infowaypi.ecare.segurados.DependenteSR;
import br.com.infowaypi.ecare.segurados.TitularFinanceiroSR;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.financeiro.FluxoFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.TitularFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.consignacao.Consignacao;
import br.com.infowaypi.jbank.boletos.Posicionable;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
/**
 * 
 * @author Wislanildo
 *
 */
public class ResumoDemonstrativoCoparticipacao {

	private TitularFinanceiroSR titular;
	private Date competencia;
	private Set<GuiaSimples> guias = new HashSet<GuiaSimples>();
	private List<Posicionable> posicionables;
	
	private BigDecimal valorFinanciamento = BigDecimal.ZERO;
	private BigDecimal valorCoparticipacao = BigDecimal.ZERO;
	private BigDecimal valorSegundaViaCartao = BigDecimal.ZERO;

	public ResumoDemonstrativoCoparticipacao(List<FluxoFinanceiroInterface> fluxos, Date competencia) {
		this.titular = getTitular(fluxos.get(0).getTitularFinanceiro());
		tocarTitularEDependentes();
		this.competencia = competencia;
		this.posicionables = new ArrayList<Posicionable>();
		this.calculaValores(fluxos);
		this.carregaGuias(fluxos);
	}
	
	private TitularFinanceiroSR getTitular(TitularFinanceiroInterface titular){
		if(titular.getClass().isInstance(TitularFinanceiroSR.class)){
			return (TitularFinanceiroSR) titular;
		}else {
			SearchAgent sa = new SearchAgent();
			sa.addParameter(new Equals("pessoaFisica.cpf", titular.getIdentificacao()));
			return (TitularFinanceiroSR) sa.uniqueResult(TitularFinanceiroSR.class);
		}
	}
	
	private void tocarTitularEDependentes(){
		this.titular.getDetalhesContaTitular().size();
		this.titular.getDependentes().size();
		for(DependenteSR dependente : this.titular.getDependentes()){
			dependente.getDetalhesContaDependente().size();
		}
		
	}

	private void carregaGuias(List<FluxoFinanceiroInterface> fluxos) {
		for (FluxoFinanceiroInterface fluxo : fluxos) {
			this.guias.addAll(fluxo.getGuias());
		}
	}

	private void calculaValores(List<FluxoFinanceiroInterface> fluxos) {
		for (FluxoFinanceiroInterface f : fluxos) {
			if (f.isConsignacao()) {
				Consignacao consignacao = (Consignacao) f;
				valorFinanciamento = valorFinanciamento.add(consignacao.getValorFinanciamento());
				valorCoparticipacao = valorCoparticipacao.add(consignacao.getValorCoparticipacao());
			} else {
				DetalheContaTitular detalhes = ((TitularFinanceiroSR)f.getTitularFinanceiro()).getDetalheContaTitular(getCompetencia());
				valorFinanciamento = valorFinanciamento.add(detalhes.getValorFinanciamento());
				valorCoparticipacao = valorCoparticipacao.add(detalhes.getValorCoparticipacao());
				valorSegundaViaCartao = valorSegundaViaCartao.add(detalhes.getValorSegundaViaCartao()); 
			}
		}
	}
	
	public byte[] getFile() throws Exception{
		ExtratoCoparticipacaoCreator extratoCreator = new ExtratoCoparticipacaoCreator();
		extratoCreator.create(this);

		PDFGeneratorCoparticipacao pdfGeneratorCoparticipacao = new PDFGeneratorCoparticipacao();
		pdfGeneratorCoparticipacao.generate(this);
		
		pdfGeneratorCoparticipacao.writeToFile("Demonstrativo_Coparticipacao.pdf");
		byte[] conteudoBoleto = pdfGeneratorCoparticipacao.getBoletoFile(new File("Demonstrativo_Coparticipacao.pdf"));
		
		return conteudoBoleto;
	}
	
	public String getFileName(){
		return "Demonstrativo_de_Coparticipacao.pdf";
	}

	public TitularFinanceiroSR getTitular() {
		return titular;
	}

	public Date getCompetencia() {
		return competencia;
	}

	public Set<GuiaSimples> getGuias() {
		return guias;
	}

	public List<Posicionable> getPosicionables() {
		return posicionables;
	}

	public BigDecimal getValorFinanciamento() {
		return valorFinanciamento;
	}

	public BigDecimal getValorCoparticipacao() {
		return valorCoparticipacao;
	}

	public BigDecimal getValorSegundaViaCartao() {
		return valorSegundaViaCartao;
	}

	public BigDecimal getValorTotal(){
		BigDecimal valor = BigDecimal.ZERO;
		valor = valor.add(this.getValorFinanciamento());
		valor = valor.add(this.getValorCoparticipacao());
		valor = valor.add(this.getValorSegundaViaCartao());
		return valor.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
}
