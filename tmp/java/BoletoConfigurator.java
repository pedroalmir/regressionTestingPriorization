package br.com.infowaypi.ecarebc.financeiro.conta;

import java.io.Serializable;
import java.util.Date;

import javax.swing.ImageIcon;

import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * @author Marcus Boolean
 * Classe que representa uma configuração para geração de boletos no sistema
 *
 */
public class BoletoConfigurator implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public static final int LARGURA_IMAGEM_ENVELOPE = 545;
	public static final int ALTURA_IMAGEM_ENVELOPE = 796;
	private Integer periodoParaSuspenderTitular;
	private Long idBoletoConfigurator;
	private Date dataDoCadastro;
	private Float multa; //Percentual mensal
	private Float juros; //Valor em % por mês
	private int validade; //Quantidade de dias que o boleto irá valer após o vencimento
   	private String mensagemReciboDoSacado1;
   	private String mensagemReciboDoSacado2;
   	private String mensagemReciboDoSacado3;
   	private String mensagemReciboDoSacado4;	
	private String mensagemFichaDeCompensacao1;
	private String mensagemFichaDeCompensacao2;
	private boolean ativo;//Informa se a configuração encontra-se ativa
	private UsuarioInterface usuario;//Usuario que criou o configurator
	private byte[] imagemComunicacaoBoleto;
	private byte[] imagemEnvelope;
	
	
	public BoletoConfigurator() {
		ativo = true;
		dataDoCadastro = new Date();
	}
	
	public Date getDataDoCadastro() {
		return dataDoCadastro;
	}

	public void setDataDoCadastro(Date dataDoCadastro) {
		this.dataDoCadastro = dataDoCadastro;
	}

	public Long getIdBoletoConfigurator() {
		return idBoletoConfigurator;
	}
	
	public void setIdBoletoConfigurator(Long idBoletoConfigurator) {
		this.idBoletoConfigurator = idBoletoConfigurator;
	}
	
	public Float getMulta() {
		return multa;
	}

	public void setMulta(Float multa) {
		this.multa = multa;
	}

	public Float getJuros() {
		return juros;
	}

	public void setJuros(Float juros) {
		this.juros = juros;
	}

	public int getValidade() {
		return validade;
	}
	
	public void setValidade(int validade) {
		this.validade = validade;
	}
	
	public String getMensagemReciboDoSacado1() {
		return mensagemReciboDoSacado1;
	}
	
	public void setMensagemReciboDoSacado1(String mensagemReciboDoSacado1) {
		this.mensagemReciboDoSacado1 = mensagemReciboDoSacado1;
	}
	
	public String getMensagemFichaDeCompensacao1() {
		return mensagemFichaDeCompensacao1;
	}
	
	public void setMensagemFichaDeCompensacao1(String mensagemFichaDeCompensacao1) {
		this.mensagemFichaDeCompensacao1 = mensagemFichaDeCompensacao1;
	}
	
	public String getMensagemReciboDoSacado2() {
		return mensagemReciboDoSacado2;
	}

	public void setMensagemReciboDoSacado2(String mensagemReciboDoSacado2) {
		this.mensagemReciboDoSacado2 = mensagemReciboDoSacado2;
	}

	public String getMensagemReciboDoSacado3() {
		return mensagemReciboDoSacado3;
	}

	public void setMensagemReciboDoSacado3(String mensagemReciboDoSacado3) {
		this.mensagemReciboDoSacado3 = mensagemReciboDoSacado3;
	}

	public String getMensagemReciboDoSacado4() {
		return mensagemReciboDoSacado4;
	}

	public void setMensagemReciboDoSacado4(String mensagemReciboDoSacado4) {
		this.mensagemReciboDoSacado4 = mensagemReciboDoSacado4;
	}

	public String getMensagemFichaDeCompensacao2() {
		return mensagemFichaDeCompensacao2;
	}

	public void setMensagemFichaDeCompensacao2(String mensagemFichaDeCompensacao2) {
		this.mensagemFichaDeCompensacao2 = mensagemFichaDeCompensacao2;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public UsuarioInterface getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioInterface usuario) {
		this.usuario = usuario;
	}
	
	public Integer getPeriodoParaSuspenderTitular() {
		return periodoParaSuspenderTitular;
	}

	public void setPeriodoParaSuspenderTitular(Integer periodoParaSuspenderTitular) {
		this.periodoParaSuspenderTitular = periodoParaSuspenderTitular;
	}

	public BoletoConfigurator clone() {
		BoletoConfigurator clonedConfigurator = new BoletoConfigurator();
		
		clonedConfigurator.setAtivo(this.isAtivo());
		clonedConfigurator.setDataDoCadastro(this.getDataDoCadastro());
		clonedConfigurator.setJuros(this.getJuros());
		clonedConfigurator.setMensagemFichaDeCompensacao1(this.getMensagemFichaDeCompensacao1());
		clonedConfigurator.setMensagemFichaDeCompensacao2(this.getMensagemFichaDeCompensacao2());
		clonedConfigurator.setMensagemReciboDoSacado1(this.getMensagemReciboDoSacado1());
		clonedConfigurator.setMensagemReciboDoSacado2(this.getMensagemReciboDoSacado2());
		clonedConfigurator.setMensagemReciboDoSacado3(this.getMensagemReciboDoSacado3());
		clonedConfigurator.setMensagemReciboDoSacado4(this.getMensagemReciboDoSacado4());
		clonedConfigurator.setMulta(this.getMulta());
		clonedConfigurator.setUsuario(this.getUsuario());
		clonedConfigurator.setValidade(this.getValidade());
		
		return clonedConfigurator;
	}

	public Boolean validate(UsuarioInterface usuario) throws Exception {
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
		
		if(this.getIdBoletoConfigurator() != null) {
			throw new RuntimeException("Caro usuário, não é permitido alterar uma Configuração de Boletos.");
		}
		
		this.dataDoCadastro = new Date();
		this.usuario = usuario;
		
		Criteria c = HibernateUtil.currentSession().createCriteria(BoletoConfigurator.class);
		c.add(Expression.eq("ativo", true));
		BoletoConfigurator configuratorAntigo = (BoletoConfigurator) c.uniqueResult();
		
		if(configuratorAntigo != null) {
			configuratorAntigo.setAtivo(false);
			ImplDAO.save(configuratorAntigo);
		}
		
		ImageIcon imagemEnvelope = new ImageIcon(this.getImagemEnvelope());
		if(imagemEnvelope.getIconHeight() != ALTURA_IMAGEM_ENVELOPE)
			throw new ValidateException(MensagemErroEnum.IMAGEM_DE_ENVELOPE_ALTURA_ERRADA.getMessage(String.valueOf(ALTURA_IMAGEM_ENVELOPE)));
		
		if(imagemEnvelope.getIconWidth() != LARGURA_IMAGEM_ENVELOPE)
			throw new ValidateException(MensagemErroEnum.IMAGEM_DE_ENVELOPE_LARGURA_ERRADA.getMessage(String.valueOf(LARGURA_IMAGEM_ENVELOPE)));
		
		
		return true;
	}

	public byte[] getImagemComunicacaoBoleto() {
		return imagemComunicacaoBoleto;
	}

	public void setImagemComunicacaoBoleto(byte[] imagemComunicacaoBoleto) {
		this.imagemComunicacaoBoleto = imagemComunicacaoBoleto;
	}

	public byte[] getImagemEnvelope() {
		return imagemEnvelope;
	}

	public void setImagemEnvelope(byte[] imagemEnvelope) {
		this.imagemEnvelope = imagemEnvelope;
	}
	
	


}
