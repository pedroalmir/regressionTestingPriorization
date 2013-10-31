package br.com.infoway.sistema;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;

import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * 
 * Classe para criar log de monitoramento de memória para a aplicação.
 * Motivada pelo aumento do consumo de memória do SR.
 * 
 * @author Rondinele
 *
 */
public class FilterLog implements Filter {
	
	private static PrintStream file = null;
	private static String lineSeparator = System.getProperty("line.separator");
	private static final DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private static final String filePath = "/home/recife/arquivos/";
	private static final String fileName = "log.txt";
	private static Set<String> noParams = null;
	private static final String separator = "|";
	
	private static long contador = 0;
	
	static {
		iniciarArquivo();
		noParams = new HashSet<String>();
		noParams.add("senha");
		noParams.add("submit");
		noParams.add("discriminator");
		noParams.add("triesRtt");
		noParams.add("org.apache.struts.taglib.html.TOKEN");
		noParams.add("fieldsError");
	}

	public void destroy() { }
	public void init(FilterConfig arg0) throws ServletException { }

	/**
	 * Gera log antes da execução e outro depois da execução.
	 */
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain chain) throws IOException, ServletException {
		
		long id = getID();
		
		gerarLog(arg0, arg1, chain, id);
		
		chain.doFilter(arg0, arg1);
		
		gerarLog(arg0, arg1, chain, id);
	
	}
	
	/**
	 * Gera uma linha de log.
	 */
	@SuppressWarnings("unchecked")
	private void gerarLog(ServletRequest arg0, ServletResponse arg1, FilterChain chain, long id) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) arg0;
		
		StringBuilder builder = new StringBuilder();
		
		builder.append(id);
		builder.append(separator);

		String[] roles = (String[]) request.getSession().getAttribute("roles");
		UsuarioInterface usuario = (UsuarioInterface) request.getSession()
				.getAttribute("usuario");

		builder.append(usuario != null ? usuario.getLogin() : "-");
		builder.append(separator);

		builder.append(roles != null ? Arrays.toString(roles) : "-");
		builder.append(separator);

		Date dataInicial = new Date();
		builder.append(format.format(dataInicial));
		builder.append(separator);

		builder.append(request.getServletPath());
		builder.append(separator);

		Enumeration<String> params = request.getParameterNames();
		while (params.hasMoreElements()) {
			String param = (String) params.nextElement();
			if (!noParams.contains(param)) {
				builder.append(param);
				builder.append(":");
				builder.append(request.getParameter(param));
				builder.append("->");
			}
		}

		builder.append(separator);

		Date dataFinal = new Date();
		builder.append(((float) (dataFinal.getTime() - dataInicial.getTime())) / 1000);
		builder.append(separator);

		Session session = HibernateUtil.currentSession();
		builder.append(session.getStatistics().getEntityCount());
		builder.append(separator);

		builder.append(session.getStatistics().getCollectionCount());
		builder.append(separator);

		builder.append(HibernateUtil.currentSession().getStatistics().getEntityCount());
		builder.append(separator);

		builder.append(HibernateUtil.currentSession().getStatistics().getCollectionCount());
		builder.append(separator);

		long memoriaLivre = Runtime.getRuntime().freeMemory();
		long memoriaUtilizada = Runtime.getRuntime().totalMemory() - memoriaLivre;

		builder.append(new BigDecimal(((float) memoriaLivre) / (1024 * 1024)).setScale(2, BigDecimal.ROUND_HALF_UP));
		builder.append(separator);

		builder.append(new BigDecimal(((float) memoriaUtilizada) / (1024 * 1024)).setScale(2, BigDecimal.ROUND_HALF_UP));
		builder.append(separator);

		builder.append(lineSeparator);

		writeLog(builder);
	}

	/**
	 * Escreve um log no arquivo txt.
	 */
	private synchronized void writeLog(StringBuilder strBuf) {
		file.print(strBuf.toString());
	}
	
	/**
	 * @return Um id sequencial, para identificar o registro 
	 * de ida e volta para a mesma requisição.
	 */
	private synchronized long getID(){
		return ++contador;
	}

	/**
	 * Cria o arquivo e insere o cabeçalho.
	 */
	private static void iniciarArquivo() {
		try {
			if (file == null) {
				file = createWriter(filePath + fileName);
				
				StringBuilder builder = new StringBuilder();
				builder.append("ID" + separator);
				builder.append("Usuario" + separator);
				builder.append("Role" + separator);
				builder.append("Data" + separator);
				builder.append("URL" + separator);
				builder.append("Parameters" + separator);
				builder.append("Tempo" + separator);
				builder.append("Qt entidades" + separator);
				builder.append("Qt colecoes" + separator);
				builder.append("Entidades carregadas" + separator);
				builder.append("ColeÃ§Ãµes carregadas" + separator);
				builder.append("Memoria livre" + separator);
				builder.append("Memoria utilizada" + separator);
				builder.append(lineSeparator);
				file.print(builder.toString());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Criar o arquivo de log no disco.
	 */
	private static PrintStream createWriter(String fileName) throws FileNotFoundException {
		FileOutputStream fileOutputStream = null;
		File file = new File(fileName);
		
		try {
			fileOutputStream = new FileOutputStream(file, true);
		} catch (FileNotFoundException e) {
			File parentDirectory = file.getParentFile();
			if ((parentDirectory != null) && !parentDirectory.exists() && parentDirectory.mkdirs()) {
				fileOutputStream = new FileOutputStream(file, true);
			}
			else {
				throw e;
			}
		}
		
		PrintStream printStream = new PrintStream(new BufferedOutputStream(fileOutputStream), true);
		
		return printStream;
	}
	
}
