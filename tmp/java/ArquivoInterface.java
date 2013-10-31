package br.com.infowaypi.ecarebc.financeiro.arquivos;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import br.com.infowaypi.ecarebc.financeiro.conta.Conta;
import br.com.infowaypi.ecarebc.financeiro.conta.ContaInterface;
import br.com.infowaypi.msr.financeiro.Banco;
import br.com.infowaypi.msr.financeiro.InformacaoFinanceiraInterface;

public interface ArquivoInterface extends Serializable {
	public static final String REPOSITORIO = "/home/recife/web/consignacoes/";
	public static final String REPOSITORIOTESTE = "D:\\workspaces\\deploy\\Recife_Web\\template\\consignacoes\\";
	
	public static final String REPOSITORIO_MIGRACAO_TPSR = "D:\\workspaces\\deploy\\Recife_Web\\template\\novaTPSR\\";

	public static final char ARQUIVO_ABERTO = 'A';
	public static final char ARQUIVO_ENVIADO = 'E';
	public static final char ARQUIVO_CANCELADO = 'C';
	
	public static final Integer CANCELADO = 1;
	
	public static final Integer REMESSA = 2;
	public static final Integer RETORNO = 3;

	public abstract byte[] getArquivo();

	public abstract void setArquivo(byte[] arquivo);

	public abstract Long getIdArquivo();

	public abstract void setIdArquivo(Long idArquivo);

	public abstract String getObservacao();

	public abstract void setObservacao(String observacao);

	public abstract Date getCompetencia();

	public abstract void setCompetencia(Date competencia);

	public abstract String getFileName();

	public abstract void setFileName(String fileName);

	public abstract char getStatusArquivo();

	public abstract void setStatusArquivo(char statusArquivo);

	public abstract Integer getTipoArquivo();

	public abstract Float getValorContas();

	public abstract void setValorContas(Float valorContas);

	public void setContas(Set<ContaInterface> contas);
	
	public Set<ContaInterface> getContas();
	
	public boolean isImpresso();

	public void setImpresso(boolean impresso);

	public Integer getTipoPagamento();

	public void setTipoPagamento(Integer tipoPagamento);
	
	public Banco getBanco();
	
	public void setBanco(Banco banco);
	public abstract void criarArquivo() throws Exception;
	public abstract <C extends Conta, IF extends InformacaoFinanceiraInterface> void populate(C conta, IF informacaoFinanceira) throws Exception;
}