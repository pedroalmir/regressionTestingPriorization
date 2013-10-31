package br.com.infowaypi.ecarebc.atendimentos;

import java.util.Date;

import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * Classe que armazena observa��es de auditores externos.<br>
 * Essas observa��es, aqui denominadas <i>Registros T�cnicos</i>, s� poder�o ser visualizadas pelo role
 *  <b>auditor</b>.
 * 
 * <br>
 * Exemplo de uso:
 * 
 * <pre>
 * public GuiaInternacao gravarRegistrosTecnicos(UsuarioInterface usuario, GuiaInternacao guia, Collection<RegistroTecnicoDaAuditoria> registros) {
		guia.tocarObjetos();
		
		if (guia.getRegistrosTecnicosDaAuditoria() == null)
			guia.setRegistrosTecnicosDaAuditoria(new ArrayList<RegistroTecnicoDaAuditoria>());

		for (RegistroTecnicoDaAuditoria registro : registros) {
			registro.setDataDeCriacao(new Date());
			registro.setGuia(guia);
			registro.setUsuario(usuario);
		}
		
		guia.getRegistrosTecnicosDaAuditoria().addAll(registros);
		
		return guia;
	}
 * </pre>
 * 
 * <p>
 * Al�m das funcionalidades fornecidas pela superclasse, RegistroTecnicoDaAuditoria possui um t�tulo
 * para a observa��o, o que permite a visualiza��o somente do titulo com um drilldown para o texto
 * completo quando da apresenta��o da guia.
 * H� tamb�m um campo que registra a data da �ltima altera��o feita, seja no t�tulo ou no texto da
 * observa��o, para efeitos de auditoria.
 * </p>
 * 
 * @author patricia
 * @version
 * @see br.com.infowaypi.ecarebc.atendimentos.Observacao
 * @see br.com.infowaypi.ecare.services.auditor.InserirRegistroTecnicoDaAuditoriaService
 */
@SuppressWarnings("unchecked")
public class RegistroTecnicoDaAuditoria extends AbstractObservacao {

	private static final long serialVersionUID = 1L;
	private String titulo;
	private Date dataAlteracao;
	/**
	 * Atributo transiente relativo a {@link #titulo}, para que seja poss�vel detectar alguma altera��o no conte�do do t�tulo.
	 */
	private String tituloAnterior;
	/**
	 * Atributo transiente relativo a {@link #getTexto()}, para que seja poss�vel detectar alguma altera��o no conte�do do texto.
	 */
	private String textoAnterior;
	private GuiaSimples guiaInternacao;
	
	private boolean fromAuditorExterno;
	
	public RegistroTecnicoDaAuditoria(){
		super();
	}
	
	public RegistroTecnicoDaAuditoria(Date dataDeCriacao, String texto, UsuarioInterface usuario) {
		super (dataDeCriacao,texto,usuario);
	}
	
	public RegistroTecnicoDaAuditoria (String texto, UsuarioInterface usuario) {
		super(texto, usuario);
	}
	
	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	public String getTituloAnterior() {
		return tituloAnterior;
	}

	public void setTituloAnterior(String tituloAnterior) {
		this.tituloAnterior = tituloAnterior;
	}

	public String getTextoAnterior() {
		return textoAnterior;
	}

	public void setTextoAnterior(String textoAnterior) {
		this.textoAnterior = textoAnterior;
	}

	/**
	 * Seta os atributos transientes {@link #textoAnterior} e {@link #tituloAnterior}
	 * para que seja poss�vel uma posterior compara��o.
	 */
	public void setStringsAntesDeAlgumaAlteracao(){
		this.textoAnterior = getTexto();
		this.tituloAnterior = titulo;
	}

	public GuiaSimples getGuiaInternacao() {
		return this.guiaInternacao;
	}
	
	public void setGuiaInternacao(GuiaSimples guia) {
		this.guiaInternacao = guia;
	}
	
	/**
	 * Indica se os campos {@link #getTexto()} e {@link #titulo} tiveram seus valores mudados,
	 * sem levar em conta diferencia��es de mai�sculas e min�sculas.
	 * @return
	 */
	public boolean isAlterouStrings(){
		if (textoAnterior == null || tituloAnterior == null) {
			return false;
		}
		return !textoAnterior.equalsIgnoreCase(getTexto()) || !tituloAnterior.equalsIgnoreCase(titulo);
	}
	@Override
	public void tocarObjetos() {
		super.tocarObjetos();
	}

	@Override
	public boolean isRegistroAuditoria() {
		return true;
	}

	@Override
	public boolean isMotivo() {
		return false;
	}

	@Override
	public boolean isObervacao() {
		return false;
	}

	public boolean isFromAuditorExterno() {
		return fromAuditorExterno;
	}

	public void setFromAuditorExterno(boolean fromAuditorExterno) {
		this.fromAuditorExterno = fromAuditorExterno;
	}
	
	public String getOrigem() {
		if(this.isFromAuditorExterno()) {
			return "Auditoria Externa";
		}else {
			return "Auditoria Pr�-Pagamento";
		}
	}
	
	@Override
	public Object clone(){
		RegistroTecnicoDaAuditoria clone = new RegistroTecnicoDaAuditoria();
		
		clone.setDataAlteracao(this.getDataAlteracao());
		clone.setDataDeCriacao(this.getDataDeCriacao());
		clone.setFromAuditorExterno(this.isFromAuditorExterno());
		clone.setUsuario(this.getUsuario());
		clone.setTexto(this.getTexto());
		clone.setTitulo(this.getTitulo());
		clone.setTituloAnterior(this.getTituloAnterior());
		clone.setTextoAnterior(this.getTituloAnterior());
		
		return clone;
	}
}
