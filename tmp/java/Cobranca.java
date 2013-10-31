package br.com.infowaypi.ecare.financeiro;

import java.io.File;
import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import br.com.infowaypi.ecare.constantes.Constantes;
import br.com.infowaypi.ecare.financeiro.boletos.BoletoCreator;
import br.com.infowaypi.ecare.financeiro.boletos.CaixaEconomicaSIGCB;
import br.com.infowaypi.ecare.financeiro.boletos.PDFGeneratorBoletoIndividual;
import br.com.infowaypi.ecare.financeiro.conta.DetalheContaDependente;
import br.com.infowaypi.ecare.segurados.DependenteInterface;
import br.com.infowaypi.ecare.segurados.DependenteSR;
import br.com.infowaypi.ecare.segurados.DependenteSuplementar;
import br.com.infowaypi.ecare.segurados.Titular;
import br.com.infowaypi.ecare.segurados.TitularFinanceiroSR;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.financeiro.CobrancaBC;
import br.com.infowaypi.ecarebc.financeiro.TitularFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.conta.ContaInterface;
import br.com.infowaypi.jbank.boletos.BoletoBean;
import br.com.infowaypi.jbank.boletos.BoletoFactory;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

@SuppressWarnings("serial")
public class Cobranca extends CobrancaBC implements CobrancaInterface {

	private TitularFinanceiroSR titular;
	public Cobranca(){}
	
	public Cobranca(UsuarioInterface usuario){
		super(usuario);
	}
	
	@Override 
	public void mudarSituacao(UsuarioInterface usuario,String descricao,String motivo,Date dataSituacao) {
		SituacaoInterface situacaoAnterior = this.getSituacao();
		if (PAGO.equals(descricao)){
			this.setValorPago(this.getValorCobrado());
		}
		
		super.mudarSituacao(usuario, descricao, motivo, null);
		
		if (situacaoAnterior != null && usuario != null && !usuario.isPossuiRole("suporte")){
			String descricaoAnterior = situacaoAnterior.getDescricao();
			if (descricaoAnterior.equals(CANCELADO) || descricaoAnterior.equals(PAGO))
				super.mudarSituacao(usuario, descricaoAnterior, "Correção de inconsistência", null);
		}
		
		if(!ABERTO.equals(descricao))
			this.atualizaSituacaoConsignacao(usuario, descricao);
	}
	
	private void atualizaSituacaoConsignacao(UsuarioInterface usuario, String descricao) {
		// TODO A Cobrança deve atualizar a situação da consignação...
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.CobrancaInterface#processarVencimento()
	 */
	public void processarVencimento() {
		boolean isVencidoAMaisDe3Dias = Utils.compareData(this.dataVencimento,new Date()) > 3;
		
		try {
			if (this.isVencida()){
				this.mudarSituacao(null, VENCIDO, CobrancaBC.PROCESSAMENTO_AUTOMATICO_DE_COBRANCA, null);	
			}else if(this.isVencida() && isVencidoAMaisDe3Dias){
				atualizaSituacaoSegurado(Constantes.SITUACAO_SUSPENSO);
			}else if(this.isPaga()){
				this.mudarSituacao(null,Constantes.SITUACAO_PAGO, CobrancaBC.PROCESSAMENTO_AUTOMATICO_DE_COBRANCA, null);
				atualizaSituacaoSegurado(Constantes.SITUACAO_ATIVO);
			}
			ImplDAO.save(this);
			
		} catch (Exception e) {
			System.out.println("Erro no processamento automático da Cobrança de ID: " + this.getIdFluxoFinanceiro());
			e.printStackTrace();
		}
	}
	
	public byte[] getBoletoRegerado() throws Exception{
		return gerarBoleto(true);
	}
	
	public String getFileName(){
		return "boleto.pdf";
	}
	
	/**
	 * Método responsável por regerar um boleto em formato pdf para a cobrança
	 * @return um array de bytes com o conteúdo do boleto
	 * @throws Exception
	 */
	public byte[] gerarBoleto(boolean isReimpressao) throws Exception{
		BoletoFactory boletoFactory = new BoletoFactory();
		boletoFactory.setGenerator(new PDFGeneratorBoletoIndividual());
		BoletoBean boleto;
		BoletoCreator boletoCreator = new BoletoCreator();
		try{
			boleto = boletoCreator.create(this, isReimpressao);
			//mudança na altura vai influir na geração do PDF da remess
			boletoFactory.addBoleto(boleto, new CaixaEconomicaSIGCB(boleto), BoletoFactory.TOPO_MODELO_2, 60f);
		} catch(NullPointerException e){
		}
		
		boletoFactory.writeToFile("reimpressao_boleto.pdf");
		byte[] conteudoBoleto = boletoFactory.getBoletoFile(new File("reimpressao_boleto.pdf"));
		return conteudoBoleto;
	}
	
	public boolean isPaga(){
		boolean isPaga = true; 
		
		for (ContaInterface conta : this.getColecaoContas().getContas()) {
			conta.processarVencimento();
			if(!conta.getSituacao().getDescricao().equals(PAGO))
				isPaga = false;
		}
		
		return isPaga;
	}
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.CobrancaInterface#isVencida()
	 */
	public boolean isVencida(){
		
		boolean cobrancaAberta = this.isSituacaoAtual(ABERTO);
		boolean cobrancaVenceu = false;
		
		for (ContaInterface conta : this.getColecaoContas().getContas()) {
			conta.processarVencimento();
			if(conta.isSituacaoAtual(VENCIDO)){
				cobrancaVenceu = true;
			}
		}
		
		if (cobrancaAberta && cobrancaVenceu)
			return true;
		return false;
	}
	
	private void atualizaSituacaoSegurado(String descricao){
		this.titular.mudarSituacao(null,descricao,CobrancaBC.PROCESSAMENTO_AUTOMATICO_DE_COBRANCA,null);
		for (DependenteInterface dependente : titular.getDependentes()) {
			dependente.mudarSituacao(null,descricao,CobrancaBC.PROCESSAMENTO_AUTOMATICO_DE_COBRANCA,null);
		}
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.CobrancaInterface#isCompetencia(java.util.Date)
	 */
	public boolean isCompetencia(Date competencia){
		if (competencia != null && Utils.compararCompetencia(competencia, this.competencia) == 0)
			return true;
		return false;
	}
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.CobrancaInterface#isCancelada()
	 */
	public boolean isCancelada() {
		return this.isSituacaoAtual(CANCELADO);
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.CobrancaInterface#cancelar(br.com.infowaypi.msr.user.UsuarioInterface, java.lang.String)
	 */
	public void cancelar(UsuarioInterface usuario, String motivo) throws Exception {
//		this.cancelarParcelas(usuario, motivo);
		this.mudarSituacao(usuario, CANCELADO, motivo, null);
		ImplDAO.save(this);
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.CobrancaInterface#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
				.append("idCobranca", this.getIdFluxoFinanceiro())
				.append("competencia", this.competencia)
				.append("dataVencimento", this.dataVencimento)
				.append("dataPagamento", this.dataPagamento)
				.append("valorCobrado", this.valorCobrado)
				.append("valorPago", this.valorPago)
				.toString();
		
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.CobrancaInterface#getTitular()
	 */
	public TitularFinanceiroSR getTitular() {
		return titular;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.CobrancaInterface#setTitular(br.com.infowaypi.ecare.segurados.Titular)
	 */
	public void setTitular(TitularFinanceiroSR titular) {
		this.titular = titular;
	}
	
	public Boolean validate() throws ValidateException {
		if(MoneyCalculation.compare(this.valorCobrado, this.valorPago) > 0)
			throw new ValidateException("Caro usuário, o valor pago deve ser maior que o valor cobrado.");
		return true;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof CobrancaInterface)) {
			return false;
		}
		CobrancaInterface cobranca = (CobrancaInterface) object;
		return new EqualsBuilder()
			.append(this.getTitular(), cobranca.getTitular())
			.append(this.getDataVencimento(), cobranca.getDataVencimento())
			.append(this.getValorCobrado(), cobranca.getValorCobrado())
			.append(this.getSituacao().getDescricao(), this.getSituacao().getDescricao())
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.getTitular())
			.append(this.getDataVencimento())
			.append(this.getValorCobrado())
			.append(this.getSituacao().getDescricao())
			.hashCode();
	}
	
	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}
	
	public TitularFinanceiroInterface getTitularFinanceiro() {
		return this.titular;
	}

	public void gerarDetalhesDependentesSuplementares(Date competencia) {
		if(this.getTitular().isSeguradoTitular()){
			Titular titular = (Titular) this.getTitular();
			DetalheContaDependente detalhe;
			Set<DependenteSuplementar> dependentes = titular.getDependentesSuplementares();
			
			for (DependenteSuplementar depSup : dependentes) {}
			
		}
	}


	public void tocarObjetos() {
		this.getColecaoContas().getContas().size();
		this.getTitular().getDetalhesContaTitular().size();
		this.getTitular().tocarObjetos();
		this.getTitular().getTitular().tocarObjetos();
		
		for (DependenteSR dependente : this.getTitular().getDependentes()) {
			dependente.getDetalhesContaDependente().size();
		}
		for (GuiaSimples guia : this.getGuias()) {
			guia.getPrestador().getPessoaJuridica();
			guia.getQuantidadeProcedimentos();
			guia.getValorCoParticipacao();
		}
	}
	
}
