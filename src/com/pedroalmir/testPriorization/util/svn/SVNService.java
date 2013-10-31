/**
 * 
 */
package com.pedroalmir.testPriorization.util.svn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * This example shows how to fetch a file and its properties from the repository
 * at the latest (HEAD) revision . If the file is a text (either it has no
 * svn:mime-type property at all or if has and the property value is text/-like)
 * its contents as well as properties will be displayed in the console,
 * otherwise - only properties.
 * 
 * ---------------------------------------------
 * 
 * @author Pedro Almir
 *
 */
public class SVNService {
	
	private static final String TEMP_FOLDER_JAVA = "C:\\Users\\Pedro Almir\\Desktop\\MeusProjetos\\IC_TCC\\Athena\\workspaceKepler\\RegressionTestingPriorization\\tmp\\java\\";
	private static final String TEMP_FOLDER_JHM = "C:\\Users\\Pedro Almir\\Desktop\\MeusProjetos\\IC_TCC\\Athena\\workspaceKepler\\RegressionTestingPriorization\\tmp\\jhm\\";
	
	public static void main(String[] args) {
		//SVNService.listAllFiles("svn://10.0.0.209:3692/MAA/", "trunk", "pedroalmir", "pedroalmir", true);
		SVNService.listAllFiles("http://symja.googlecode.com/svn/trunk/", "matheclipse-core/src/main/java/org/matheclipse/core/stat/descriptive", "pedroalmir", "pedroalmir", false);
	}
	
	/**
	 * Parameters is used to obtain a repository location URL, user's
	 * account name & password to authenticate him to the server, the file path
	 * in the repository (the file path should be relative to the the
	 * path/to/repository part of the repository location URL).
	 */
	public static List<File> listAllFiles(String urlBase, String path, String username, String password, boolean needLogin) {
		/* Initializes the library (it must be done before ever using the library itself) */
		setupLibrary();

		SVNRepository repository = null;
		try {
			/*
			 * Creates an instance of SVNRepository to work with the repository.
			 * All user's requests to the repository are relative to the
			 * repository location used to create this SVNRepository.
			 * SVNURL is a wrapper for URL strings that refer to repository locations.
			 */
			repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(urlBase));
		} catch (SVNException svne) {
			/* Perhaps a malformed URL is the cause of this exception */
			System.err.println("Error while creating an SVNRepository for the location '" + urlBase + "': " + svne.getMessage());
		}

		/*
		 * User's authentication information (name/password) is provided via an ISVNAuthenticationManager instance. 
		 * SVNWCUtil creates a default authentication manager given user's name and password.
		 * Default authentication manager first attempts to use provided user name and password and then falls back to the credentials stored in the
		 * default Subversion credentials storage that is located in Subversion configuration area. If you'd like to use provided user name and password
		 * only you may use BasicAuthenticationManager class instead of default authentication manager: 
		 * 
		 * authManager = new BasicAuthenticationsManager(userName, userPassword);
		 * 
		 * You may also skip this point - anonymous access will be used.
		 * 
		 * ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);
		 * repository.setAuthenticationManager(authManager);
		 */
		if(needLogin){
			ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);
			repository.setAuthenticationManager(authManager);
		}
		
		List<File> extractedFiles = listEntries(repository, path);
		/*
		for (File file : extractedFiles) {
			System.out.println("[File Name: " + file.getName() + "]");
		}
		*/
		return extractedFiles;
	}
	
	/**
	 * @param repository
	 * @param path
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static List<File> listEntries(SVNRepository repository, String path) {
		/*
		 * This Map will be used to get the file properties. Each Map key is a
		 * property name and the value associated with the key is the property
		 * value.
		 */
		List<File> result = new LinkedList<File>();
		
		try {
			/*
			 * Checks up if the specified path really corresponds to a file. If
			 * doesn't the program exits. SVNNodeKind is that one who says what is
			 * located at a path in a revision. -1 means the latest revision.
			 * 
			 * SVNNodeKind nodeKind = repository.checkPath(path, -1);
			 */
			SVNNodeKind nodeKind = repository.checkPath(path, -1);
			SVNProperties fileProperties = new SVNProperties();
			
			if (nodeKind == SVNNodeKind.FILE) {
				/*
				 * Gets the contents and properties of the file located at filePath
				 * in the repository at the latest revision (which is meant by a negative revision number).
				 */
				if (FilenameUtils.getExtension(path).equalsIgnoreCase("java") && !path.contains("package-info.java")) {
					result.add(extractFile(path, TEMP_FOLDER_JAVA, repository, fileProperties));
				}else if(FilenameUtils.getExtension(path).equalsIgnoreCase("xml") && path.contains(".jhm.xml")){
					result.add(extractFile(path, TEMP_FOLDER_JHM, repository, fileProperties));
				}
			} else if (nodeKind == SVNNodeKind.DIR) {
				
				Collection entries = repository.getDir(path, -1, null, (Collection) null);
				Iterator iterator = entries.iterator();
				
				while (iterator.hasNext()) {
					SVNDirEntry entry = (SVNDirEntry) iterator.next();
					
					if (entry.getKind() == SVNNodeKind.DIR) {
						result.addAll(listEntries(repository, (path.equals("")) ? entry.getName() : path + "/" + entry.getName()));
					} else if (entry.getKind() == SVNNodeKind.FILE) {
						/*
						 * Gets the contents and properties of the file located at filePath
						 * in the repository at the latest revision (which is meant by a negative revision number).
						 */
						if (FilenameUtils.getExtension(entry.getName()).equalsIgnoreCase("java") && !entry.getName().equals("package-info.java")) {
							result.add(extractFile(path + "/" + entry.getName(), TEMP_FOLDER_JAVA, repository, fileProperties));
						}else if(FilenameUtils.getExtension(entry.getName()).equalsIgnoreCase("xml") && entry.getName().contains(".jhm.xml")){
							result.add(extractFile(path + "/" + entry.getName(), TEMP_FOLDER_JHM, repository, fileProperties));
						}
					}
				}
			}
		} catch (SVNException svne) {
			System.err.println("Error while fetching the file contents and properties: " + svne.getMessage());
			System.exit(1);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * @param path
	 * @return
	 * @throws SVNException 
	 * @throws IOException 
	 */
	private static File extractFile(String path, String folder, SVNRepository repository, SVNProperties fileProperties) throws SVNException, IOException {
		String fileName = path.substring(path.lastIndexOf('/')+1, path.length());
		/* Just for debug */
		//System.out.println("[Entry Name: " + fileName + "]");
		
		File file = new File(folder + fileName);
		FileOutputStream outputSream = new FileOutputStream(file);
		
		/* if file doesn't exists, then create it */
		if (!file.exists()) {
			file.createNewFile();
		}
		repository.getFile(path, -1, fileProperties, outputSream);
		try {
			outputSream.flush();
			outputSream.close();
		} catch (IOException ioE) {
			ioE.printStackTrace();
		}
		return file;
	}

	/**
	 * Initializes the library to work with a repository via different protocols.
	 */
	private static void setupLibrary() {
		/* For using over http:// and https:// */
		DAVRepositoryFactory.setup();
		/* For using over svn:// and svn+xxx:// */
		SVNRepositoryFactoryImpl.setup();
		/* For using over file:/// */
		FSRepositoryFactory.setup();
	}
}
