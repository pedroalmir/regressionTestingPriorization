package br.com.infowaypi.ecarebc.segurados;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Prestador.TipoIncremento;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.consumo.ColecaoConsumosInterface;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.planos.Faixa;
import br.com.infowaypi.ecarebc.promocao.PromocaoConsulta;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.pessoa.PessoaFisicaInterface;
import br.com.infowaypi.msr.situations.ColecaoSituacoesInterface;
import br.com.infowaypi.msr.situations.ComponenteColecaoSituacoes;
import br.com.infowaypi.msr.situations.SituacaoInterface;

public interface SeguradoInterface extends ColecaoSituacoesInterface {
	
	public static final int CARENCIA_URGENCIAS_EMERGENCIA = 1;
	public static final int CARENCIA_EXAMES_BAIXA_COMPLEXIDADE = 15;
	public static final int CARENCIA_EXAMES_ALTA_COMPLEXIDADE = 180;
	public static final int CARENCIA_CIRURGIAS_INTERNACOES = 180;
	public static final int CARENCIA_PARTOS = 300;
	public static final int CARENCIA_DOENCAS_PRE_EXISTENTES = 730;
	//Contante de Parametrização para consultas clinico promotor
	public static final int CARENCIA_CLINICO_PROMOTOR = 730;
	
	
	public abstract Long getIdSegurado();
	
	public abstract void setIdSegurado(Long idSegurado);
	
	public abstract SituacaoInterface getSituacao();
	
	public abstract boolean isCumpriuCarencia(Integer carencia) throws ValidateException;
	
	public abstract String getContrato();
	
	public abstract String getTipoDeSegurado();
	
	public abstract <T extends TitularInterfaceBC> T getTitular();	
	
	public Set<GuiaSimples> getGuias(Date competencia);
	
	public Set<GuiaSimples> getGuiasAptasAoCalculoDeCoparticipacao(Date dataLimite);
	
	public Set<GuiaSimples> getGuiasAptasAoCalculoDeCoparticipacao(Date dataInicial, Date dataLimite);
	
	public abstract Set<GuiaSimples> getGuias();
	
	public List<GuiaSimples> getGuiasConfirmadasEFechadas(Date competencia)throws ValidateException;	
	
	public abstract boolean isSeguradoTitular();
	
//	public abstract boolean isCumpriuCarencia(TabelaCBHPM procedimento);
	
	public abstract PessoaFisicaInterface getPessoaFisica();
	
	public abstract boolean isBeneficiario();
	
	public abstract void tocarObjetos();
	
	public void decrementarLimites(GuiaSimples guiaCriada) throws Exception;
	
	public void incrementarLimites(GuiaSimples guiaCriada) throws Exception;
	
	public void atualizarLimites(GuiaSimples guia, TipoIncremento tipo, int numeroProcedimentos) throws Exception ;
	
	public BigDecimal getValorIndividual();
	
	public GrupoBC getGrupo();
	
	public Faixa getFaixa();
	
	public abstract ColecaoConsumosInterface getConsumoIndividual();

	public abstract void setConsumoIndividual(ColecaoConsumosInterface consumoIndividual);

	public abstract void setContrato(String contrato);

	public abstract Date getDataAdesao();

	public abstract void setDataAdesao(Date dataAdesao);

	public abstract void setGuias(Set<GuiaSimples> guias);

	public abstract void setPessoaFisica(PessoaFisicaInterface pessoaFisica);

	public abstract String getCodigoLegado();

	public abstract void setCodigoLegado(String codigoLegado);

	public abstract BigDecimal getValorIndividual(Date date);
	
	public ComponenteColecaoSituacoes getColecaoSituacoes();

	public void setColecaoSituacoes(ComponenteColecaoSituacoes colecaoSituacoes);
	
	public void setSituacao(SituacaoInterface situacaoAtual);

	public abstract boolean equals(Object object);
	
	public Set<PromocaoConsulta> getConsultasPromocionais();
	
	public String getNumeroDoCartao();
	
	public boolean temLimite(Date data, GuiaSimples guia) throws Exception ;
	
	public <G extends GuiaSimples> GuiaSimples getUltimaGuia(Prestador prestador, Class<G> klassGuia, SituacaoEnum... situacoesGuia);
	
	void addParameterEquals(SearchAgent sa);
	
	public List<GuiaSimples> getGuiasInternacao();
	
	public List<GuiaSimples> getGuiasInternacaoNaoFaturadas();
}
