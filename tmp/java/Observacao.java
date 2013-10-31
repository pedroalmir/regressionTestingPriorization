package br.com.infowaypi.ecarebc.atendimentos;

import java.util.Date;

import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * Classe que representa um texto descritivo sobre alguma observação.
 * As observações podem ser visualizadas por qualquer usuário do sistema.
 * 
 * <br>
 * Exemplo de uso:
 * 
 * <pre>
 * public void gravarObservacoes( Collection<Observacao> observacoes, GuiaExame guia, UsuarioInterface usuario) {
		if (guia.getObservacoes() == null)
			guia.setObservacoes(new ArrayList<Observacao>());

		for (Observacao obs : observacoes) {
			obs.setDataDeCriacao(new Date());
			obs.setGuia(guia);
			obs.setUsuario(usuario);
		}
		guia.getObservacoes().addAll(observacoes);
	}
 * </pre>
 * 
 * <p>
 * Limitações:<br>
 * Essa classe não implementa nenhuma funcionalidade adicional em relação à sua superclasse 
 * e comporta-se de modo semelhante a RegistroTecnicoDaAuditoria. 
 * No entanto foi criada para facilitar o entendimento do sistema a nível de implementação.
 * </p>
 * 
 * @author Marcus Boolean
 * @version
 * @see br.com.infowaypi.ecarebc.atendimentos.RegistroTecnicoDaAuditoria
 */
public class Observacao extends AbstractObservacao {
	
	private static final long serialVersionUID = 1L;
	
	public Observacao(){
		super();
	}
	
	public Observacao(Date dataDeCriacao, String texto, UsuarioInterface usuario) {
		super (dataDeCriacao,texto,usuario);
	}
	
	public Observacao (String texto, UsuarioInterface usuario) {
		super(texto, usuario);
	}
	
	@Override
	public void tocarObjetos() {
		super.tocarObjetos();
	}

	@Override
	public boolean isRegistroAuditoria() {
		return false;
	}

	@Override
	public boolean isMotivo() {
		return false;
	}

	@Override
	public boolean isObervacao() {
		return true;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		Observacao clone = new Observacao();
		
		clone.dataDeCriacao = this.dataDeCriacao;
		clone.texto = this.texto;
		clone.usuario = this.usuario;
		
		return clone;
	}
}
