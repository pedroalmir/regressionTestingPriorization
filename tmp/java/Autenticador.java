package br.com.infowaypi.ecare.autenticacao;

import java.util.List;

import br.com.infowaypi.ecare.enums.DestinatarioNoticiaEnum;
import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.manager.ContadorAcessoManager;
import br.com.infowaypi.ecare.manager.SeguradoManager;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.associados.UsuarioDoPrestador;
import br.com.infowaypi.ecarebc.enums.Role;
import br.com.infowaypi.ecarebc.painelDeControle.PainelDeControle;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.SearchAgentInterface;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.OR;
import br.com.infowaypi.molecular.parameter.OrderByDesc;
import br.com.infowaypi.msr.noticia.Noticia;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * Autenticador básico para a aplicação.
 * 
 * @author Mário Sérgio Coelho Marroquim
 */
public class Autenticador {

	private static final ThreadLocal<UsuarioInterface> threadUsuario = new ThreadLocal<UsuarioInterface>();

	private static final ThreadLocal<Prestador> threadPrestador = new ThreadLocal<Prestador>();

	private static final ThreadLocal<Profissional> threadProfissional = new ThreadLocal<Profissional>();

	private static final ThreadLocal<Segurado> threadSegurado = new ThreadLocal<Segurado>();
	
	private static final ThreadLocal<List<Noticia>> threadNoticias = new ThreadLocal<List<Noticia>>();
	
	
	public Autenticador() {
	}

	public UsuarioInterface getUsuario() {
		return threadUsuario.get();
	}

	public Prestador getPrestador() {
		return threadPrestador.get();
	}

	public Profissional getProfissional() {
		return threadProfissional.get();
	}

	public Segurado getSegurado() {
		return threadSegurado.get();
	}
	
	public PainelDeControle getPainelDeControle() {
		return PainelDeControle.getPainel();
	}

	public List<Noticia> getNoticias(){
		return 	threadNoticias.get();
	}
	
	/**
	 * Retorna os roles disponível para o usuário.
	 * 
	 * @param login
	 * @param senha
	 * @return String[]
	 * @throws Exception 
	 */
	public String[] getRoles(String login, String senha) throws Exception {
		UsuarioInterface usuario = Usuario.getInstance(login);
		
		if(usuario != null && (usuario.isPossuiRole(Role.TITULAR.getValor()) || usuario.isPossuiRole(Role.DEPENDENTE.getValor()))){
			boolean isAtivo = usuario.getStatus().equals(Usuario.ATIVO);
			
			if(!isAtivo){
				throw new Exception(MensagemErroEnumSR.ACESSO_NEGADO_PORTAL.getMessage());
			}
			
			threadUsuario.set(usuario);
			threadSegurado.set(SeguradoManager.buscarSegurado(usuario));
			threadNoticias.set(getListaNoticias(false,DestinatarioNoticiaEnum.SEGURADO.getValor()));
			ContadorAcessoManager.incrementarContador();
			
			return new String[]{usuario.getRole()};
		}
		
        if(usuario != null && usuario.autentica(senha)){
        	threadUsuario.set(usuario);
        	
        	Boolean isPrestador = Role.PRESTADOR_COMPLETO.getValor().equals(usuario.getRole()) ||
        		Role.PRESTADOR_CONS_EXM.getValor().equals(usuario.getRole()) ||
        		Role.PRESTADOR_CONSULTA.getValor().equals(usuario.getRole()) ||
        		Role.PRESTADOR_EXAME.getValor().equals(usuario.getRole()) ||
        		Role.PRESTADOR_INT_URG.getValor().equals(usuario.getRole()) ||
        		Role.PRESTADOR_INT_EXM_URG.getValor().equals(usuario.getRole()) ||
        		Role.PRESTADOR_CONS_EXM_INT_URG.getValor().equals(usuario.getRole()) ||
        		Role.PRESTADOR_ANESTESISTA.getValor().equals(usuario.getRole()) ||
        		Role.PRESTADOR_ODONTOLOGICO.getValor().equals(usuario.getRole());

        	if (isPrestador){
        		buscarPrestador(usuario);
        		threadNoticias.set(getListaNoticias(true,DestinatarioNoticiaEnum.TODOS_EXCETO_SEGURADO.getValor()));
        	}
        	else {
        		threadNoticias.set(getListaNoticias(false,DestinatarioNoticiaEnum.TODOS_EXCETO_SEGURADO.getValor()));
        	}
        	
    		return new String[]{usuario.getRole()};
        }			
		return null;
	}

	private static List<Noticia> getListaNoticias(boolean prestador,String destinatario) {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("ativa", true));
		if(prestador){
		sa.addParameter(new OR(new Equals("destino", destinatario),new  Equals("destino", DestinatarioNoticiaEnum.PRESTADOR.getValor())));
		}else {
			sa.addParameter(new Equals("destino", destinatario) );	
		}
		sa.addParameter(new OrderByDesc("dataInclusao"));
		return sa.list(Noticia.class, 0, 10);
	}

	@SuppressWarnings("rawtypes")
	private void buscarPrestador(UsuarioInterface usuario) {
		SearchAgentInterface sa = new SearchAgent();
		sa.addParameter(new Equals("usuario", usuario));
		List prestadores = sa.list(Prestador.class);
		if (prestadores != null && !prestadores.isEmpty()) {
			Prestador prestador = (Prestador) prestadores.get(0);
			threadPrestador.set(prestador);
		} else {
			UsuarioDoPrestador usuarioDoPrestador = (UsuarioDoPrestador) ImplDAO
					.findById(usuario.getIdUsuario(), UsuarioDoPrestador.class);
			if (usuarioDoPrestador != null)
				threadPrestador.set((Prestador) usuarioDoPrestador.getPrestador());
		}
	}
}