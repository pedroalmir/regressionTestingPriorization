package br.com.infowaypi.ecare.relatorio.portalBeneficiario.demostrativoIR;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecarebc.financeiro.FluxoFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.consignacao.Consignacao;
import br.com.infowaypi.jbank.boletos.Posicionable;
import br.com.infowaypi.msr.utils.Utils;

public class ResumoDemonstrativoPagamento {
	
	private List<FluxoFinanceiroInterface> fluxos;
	private List<CompetenciaFluxo> compFluxos = new ArrayList<CompetenciaFluxo>();
	
	private Segurado segurado;
	private Integer ano;
	private List<Posicionable> posicionables = new ArrayList<Posicionable>();
	
	public ResumoDemonstrativoPagamento(Segurado segurado, Integer ano, List<FluxoFinanceiroInterface> fluxos) {
		this.segurado = segurado;
		this.ano = ano;
		this.fluxos = fluxos;
		agruparPagamentos(this.fluxos);
	}
	
	public void agruparPagamentos(List<FluxoFinanceiroInterface> fluxos){
		Map<Date, CompetenciaFluxo> mapa = new HashMap<Date, CompetenciaFluxo>();
		for (FluxoFinanceiroInterface f : fluxos) {
			Date competencia = f.getCompetencia();
			if(!mapa.containsKey(competencia)){
				CompetenciaFluxo c = new CompetenciaFluxo(competencia, new ArrayList<FluxoFinanceiroInterface>());
				c.getFluxos().add(f);
				mapa.put(competencia,c);
			} else{
				mapa.get(competencia).getFluxos().add(f);
			}
		}
		
		compFluxos.addAll(mapa.values());
	}
	
	public BigDecimal getValorPago(){
		BigDecimal valor = BigDecimal.ZERO;
		for (FluxoFinanceiroInterface f : fluxos) {
			if(f.isConsignacao()){
				valor = valor.add(((Consignacao)f).getValorFinanciamento());
			}
			else{
				valor = valor.add(f.getValorPago());
			}
		}
		return valor;
	}

	public BigDecimal getValorCobrado(){
		BigDecimal valor = BigDecimal.ZERO;
		for (FluxoFinanceiroInterface f : fluxos) {
			valor = valor.add(f.getValorCobrado());
		}
		return valor;
	}
	
	public String  getTextoDeclacacao(){
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("          Declaramos para fins de apresentação à Receita Federal do Brasil, que o ");
		stringBuilder.append("beneficiário do Saúde Recife " + getSegurado().getNomeFormatado() + ". ");
		stringBuilder.append("Portador do CPF/MF " + getSegurado().getPessoaFisica().getCpf());
		stringBuilder.append(" é beneficiário titular do plano Saúde Recife, CNPJ/MF sob nº 05.244.336/0001-13 sediado a Avenida Manoel Borba nº 488, Boa Vista, Pernambuco, desde ");
		stringBuilder.append(Utils.format(getSegurado().getDataAdesao()) + ", pagou no ano de " + getAno());
		stringBuilder.append(" , a titulo de contribuição mensal e co-participação ao plano de saúde o valor abaixo:");
		
		return stringBuilder.toString();
	}

	public byte[] getFile() throws Exception{
		PDFGeneratorDemonstrativoIR pdfGenerator = new PDFGeneratorDemonstrativoIR();
		pdfGenerator.generate(this);
		
		pdfGenerator.writeToFile("Demonstrativo_IR.pdf");
		byte[] conteudoBoleto = pdfGenerator.getBoletoFile(new File("Demonstrativo_IR.pdf"));
		
		return conteudoBoleto;
	}
	
	public String getFileName(){
		return "Demonstrativo_de_IR.pdf";
	}

	public int getQuantidade(){
		return compFluxos.size();
	}

	public List<FluxoFinanceiroInterface> getFluxos() {
		return fluxos;
	}

	public void setFluxos(List<FluxoFinanceiroInterface> fluxos) {
		this.fluxos = fluxos;
	}

	public List<CompetenciaFluxo> getCompFluxos() {
		Utils.sort(compFluxos, "competencia");
		return compFluxos;
	}

	public void setCompFluxos(List<CompetenciaFluxo> compFluxos) {
		this.compFluxos = compFluxos;
	}

	public List<Posicionable> getPosicionables() {
		return posicionables;
	}

	public Segurado getSegurado() {
		return segurado;
	}

	public void setSegurado(Segurado segurado) {
		this.segurado = segurado;
	}
	
	public Integer getAno() {
		return ano;
	}

	public void setPosicionables(List<Posicionable> posicionables) {
		this.posicionables = posicionables;
	}
}
