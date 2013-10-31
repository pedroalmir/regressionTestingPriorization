package br.com.infowaypi.ecare.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * Encapsulates all file operations.
 * @author Mário Sérgio Coelho Marroquim and Jonhnny Weslley
 */
public class FileUtils {
		
	/**
	 * Puts the given items into the given key´s place by replacing it.
	 * @param items
	 * @param key
	 * @param fileUrl
	 * @throws Exception
	 */
	public static void putKeyValue(String key, String value, String sourceUrl) throws Exception {
		putKeyValue(key, value, sourceUrl, sourceUrl);				
	}
	
	/**
	 * Puts the given items into the given key´s place by replacing it.
	 * @param key
	 * @param value
	 * @param sourceUrl
	 * @param targetUrl
	 * @throws Exception
	 */
	public static void putKeyValue(String key, String value, String sourceUrl, String targetUrl) throws Exception {
		Map<String, String> strings = new HashMap<String, String>();
		strings.put(key, value);
		putStrings(strings, sourceUrl, targetUrl);				
	}
	
	/**
	 * Replaces the file keys with the given key-value map.
	 * @param map
	 * @param sourceUrl
	 * @return String
	 * @throws Exception
	 */
	public static String replaceAll(Map<String, String> map, String sourceUrl) throws Exception {
		FileInputStream source = null;		
		try {
			source = new FileInputStream(sourceUrl);
			if(source == null)
				throw new FileNotFoundException("File "+sourceUrl+"doesn´t exists.");
			
			byte[] content = new byte[source.available()];
			source.read(content);			
			String layout = new String(content);
			
			for (String key : map.keySet()) 				
				layout = StringUtils.replace(layout, key, map.get(key));
			
			return layout;			
		}
		catch (Exception e) {
			return "";
		}
		finally {
			if(source != null)
				source.close();		
		}
	}
	
	/**
	 * Puts the given strings into the file.
	 * @param map
	 * @param sourceUrl
	 * @param targetUrl
	 * @throws Exception
	 */
	public static void putStrings(Map<String, String> map, String sourceUrl, String targetUrl) throws Exception {		
		FileOutputStream target = null;
		try {			
			byte[] content = replaceAll(map, sourceUrl).getBytes();			
			target = new FileOutputStream(targetUrl);
			target.write(content);			
		}
		catch (Exception e){
			e.printStackTrace();
		}
		finally {	
			if(target != null)
				target.close();
		}		
	}		
	
	/**
	 * Gets a resource into the classpath.
	 * @param resource
	 * @return InputStream
	 * @throws FileNotFoundException
	 */
	public static InputStream getResourceAsStream(final String resource) throws FileNotFoundException {
		InputStream stream = null;

		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();		
		
		if (contextClassLoader != null) {			
			stream = contextClassLoader.getResourceAsStream(resource);
		}
		
		if (stream == null) {			
			stream = FileUtils.class.getClassLoader().getResourceAsStream(resource);
		}		

		if (stream==null) {
			stream = ClassLoader.class.getResourceAsStream(resource);
		}
		
		if (stream ==null && contextClassLoader!=null) {
			stream = ClassLoader.getSystemClassLoader().getResourceAsStream(resource);
		}
		
		return stream;
	}	
}