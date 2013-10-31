package br.com.infowaypi.ecare.segurados;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import br.com.infowaypi.ecare.constantes.Constantes;
import br.com.infowaypi.ecarebc.odonto.enums.DenticaoEnum;
import br.com.infowaypi.ecarebc.planos.Faixa;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.ecarebc.promocao.PromocaoConsulta;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.pessoa.PessoaFisicaInterface;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * Interface para a classe Segurado. 
 * @author Josino
 */
public interface SeguradoBasico extends SeguradoInterface,Constantes, SeguradoConsignacaoInterface {
	

	public static final String DENTICAO_PERMANENTE = DenticaoEnum.PERMANENTE.getDescricao();
	public static final String DENTICAO_DECIDUO = DenticaoEnum.DECIDUO.getDescricao();
	
	/*(non-Javadoc)
	 * Constantes para as formas de pagamento.
	 */
	public static final int CONTA_CORRENTE = Constantes.CONTA_CORRENTE;
	public static final int FOLHA = Constantes.FOLHA;
	public static final int BOLETO = Constantes.BOLETO;
	
	
	/* (non-Javadoc)
	 * Constantes para os tipos de Dependência.
	 */
	Integer TIPO_DEPENDENCIA_TITULAR = 0;
	Integer TIPO_FILHO_MENOR_Q_21= 1;
	Integer TIPO_FILHO_MENOR_25_ANOS = 3;
	Integer TIPO_FILHO_EXCEPCIONAL_INVALIDO = 4;
	Integer TIPO_ENTEADO = 5;
	Integer TIPO_TUTELADO = 6;
	Integer TIPO_CONJUGUE = 10;
	Integer TIPO_COMPANHEIRO = 11;
	Integer TIPO_EX_ESPOSA = 12;
	Integer TIPO_EX_COMPANHEIRO = 13;
	Integer TIPO_MAE = 15;
	Integer TIPO_PAI = 16;
	int TIPO_IRMAO = 20;
	
	String DESCRICAO_TIPO_DEPENDENCIA_TITULAR = "Titular";
	String DESCRICAO_TIPO_FILHO_MENOR_Q_21= "Filho(a) menor que 21 anos";
	String DESCRICAO_TIPO_FILHO_MENOR_25_ANOS = "Filho(a) menor que 25 anos(estudante)";
	String DESCRICAO_TIPO_FILHO_EXCEPCIONAL_INVALIDO = "Filho(a) excepcional/inválido";
	String DESCRICAO_TIPO_ENTEADO = "Enteado(a)";
	String DESCRICAO_TIPO_TUTELADO = "Tutelado(a)";
	String DESCRICAO_TIPO_CONJUGUE = "Conjuge";
	String DESCRICAO_TIPO_COMPANHEIRO = "Companheiro(a)";
	String DESCRICAO_TIPO_EX_ESPOSA = "Ex-esposo(a)";
	String DESCRICAO_TIPO_EX_COMPANHEIRO = "Ex-companheiro(a)";
	String DESCRICAO_TIPO_MAE = "Mãe";
	String DESCRICAO_TIPO_PAI = "Pai";
	String DESCRICAO_TIPO_IRMAO = "Irmão";
	
	
	int TIPO_SUPLEMENTAR_PAIS = 21;
	int TIPO_SUPLEMENTAR_AVOS = 22;
	int TIPO_SUPLEMENTAR_BISAVOS = 23;
	int TIPO_SUPLEMENTAR_TATARAVOS = 24;
	int TIPO_SUPLEMENTAR_SOBRINHO = 25;
	int TIPO_SUPLEMENTAR_FILHO = 26;
	int TIPO_SUPLEMENTAR_NETO = 27;
	int TIPO_SUPLEMENTAR_BISNETO = 28;
	int TIPO_SUPLEMENTAR_TATARANETO = 29;
	int TIPO_SUPLEMENTAR_TIOS = 30;
	
	String DESCRICAO_TIPO_SUPLEMENTAR_PAIS = "Pai/Mãe";
	String DESCRICAO_TIPO_SUPLEMENTAR_AVOS = "Avô/Avó";
	String DESCRICAO_TIPO_SUPLEMENTAR_BISAVOS = "Bisavô/Bisavó";
	String DESCRICAO_TIPO_SUPLEMENTAR_TATARAVOS = "Tataravô/Tataravó";
	String DESCRICAO_TIPO_SUPLEMENTAR_SOBRINHO = "Sobrinho(a)";
	String DESCRICAO_TIPO_SUPLEMENTAR_FILHO = "Filho(a)";
	String DESCRICAO_TIPO_SUPLEMENTAR_NETO = "Neto(a)";
	String DESCRICAO_TIPO_SUPLEMENTAR_BISNETO = "Bisneto(a)";
	String DESCRICAO_TIPO_SUPLEMENTAR_TATARANETO = "Tataraneto(a)";
	String DESCRICAO_TIPO_SUPLEMENTAR_TIOS = "Tio(a)";
	
	public SituacaoInterface getSituacaoCadastral() ;
	public void setSituacaoCadastral(SituacaoInterface situacaoCadastral);
	
//	1	FILHO(A) MENOR QUE 21 ANOS
//	3	FILHO(A) MENOR 25 ANOS ESTUDANTE
//	4	FILHO(A) EXCEPCIONAL/INVÁLIDO
//	5	ENTEADO(A)
//	6	TUTELADO(A)
//	10	CONJUGUE
//	11	COMPANHEIRO(A)
//	12	EX-ESPOSA(O)
//	13	EX-COMPANHEIRO(A)
//	15	MAE
//	16	PAI
//	20	IRMÃO(Ã)
	
	
	/* (non-Javadoc)
	 * Constantes para os estados civis das pessoas.
	 */
	
	public static final Integer ESTADO_CIVIL_OUTROS = 1;
	public static final Integer ESTADO_CIVIL_SOLTEIRO = 2;
	public static final Integer ESTADO_CIVIL_CASADO = 3;
	public static final Integer ESTADO_CIVIL_VIUVO = 4;	
	public static final Integer ESTADO_CIVIL_SEPARADO_JUDICIALMENTE = 5;
	public static final Integer ESTADO_CIVIL_DIVORCIADO = 6;
	public static final Integer ESTADO_CIVIL_ESTADO_MARITAL = 7;
	
	//MOTIVOS DE DESCONTO
	public static final String SEM_DESCONTO = "(Sem Desconto)";
	public static final String FUNCIONARIO = "Funcionário(a)";
	public static final String PROMOCAO = "Promoção";	
	
	
	public abstract Boolean validate(UsuarioInterface usuario) throws ValidateException;
	
	public abstract void reativar(String motivo, Date dataAdesao, UsuarioInterface usuario) throws Exception;
	
	public abstract void cancelar(String motivo, UsuarioInterface usuario) throws Exception;
	
	public abstract void suspender(String motivo, UsuarioInterface usuario) throws Exception;
	
	public void tocarObjetos();
	
	public abstract Map<Date, TabelaCBHPM> getProcedimentosRealizados() throws ValidateException;
	
	public abstract String getTipoDeSegurado();
	
	public abstract boolean isSeguradoTitular();
	
	public abstract boolean isSeguradoDependente();
	
	public abstract PessoaFisicaInterface getPessoaFisica();

	public abstract void setPessoaFisica(PessoaFisicaInterface pessoaFisica);
	
	public abstract Long getIdSegurado();

	public abstract String getContrato();

	public abstract void setContrato(String matricula);

	public abstract Date getDataVencimentoCarteira();

	public abstract void setDataVencimentoCarteira(Date dataVencimentoCarteira);

	public abstract String getDescricaoLesao();

	public abstract void setDescricaoLesao(String descricaoLesao);

	public abstract String getObservacao();

	public abstract void setObservacao(String observacao);

	public Faixa getFaixa();
	
	public abstract int getCarenciaCumprida();
	
	public abstract int getCarenciaCumpridaEmHoras();	
	
	public Set<PromocaoConsulta> getConsultasPromocionais();

	public String getNumeroDoCartao();
	
	public void setNumeroDoCartao(String numeroDoCartao);
	
	public void setRecadastrado(boolean recadastrado);
	
	public boolean isRecadastrado();
	
	public void setConsultasPromocionais(Set<PromocaoConsulta> consultasPromocionais);
	
	public void setConsultaPromocional(PromocaoConsulta consultaPromocional) throws ValidateException;
	
	public int getOrdem();

	public void setOrdem(int ordem);
	
	public Cartao gerarCartao();
	
	public Cartao getCartaoAtual();
	
	public Set<Cartao> getCartoes();
	
	public Date getDataGeracaoDoCartao();

	public void setDataGeracaoDoCartao(Date dataGeracaoDoCartao);

	public int getIdade();
}