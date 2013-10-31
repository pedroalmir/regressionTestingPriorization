/**
 * 
 */
package com.pedroalmir.testPriorization.flow.complexity;

import java.io.IOException;
import java.util.List;

import com.pedroalmir.testPriorization.model.Klass;
import com.pedroalmir.testPriorization.util.file.FileAnalyzer;

/**
 * @author Pedro Almir
 *
 */
public class CyclomaticComplexityService {
	/*
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws IOException {
		CyclomaticComplexityService cyc = new CyclomaticComplexityService();
		// C:\Eclipse\junojee\infoway\JPA_Hibernate\src\main\java\com\pasn\core\dao
		File dir = new File("C:/Eclipse/junojee/infoway/desenvolvimento_MAA/src/");
		MyDirectoryWalker walker = new MyDirectoryWalker(".java", 10);
		List<File> files = new ArrayList<File>();

		// collect all files with specified extension inside dir
		if (dir.isDirectory()) {
			files = walker.getExtensionFiles(dir);
		} else {
			System.out.println("Isn't a folder!");
		}

		Map<String, Integer> ciclomaticComplexity = cyc.getCiclomaticComplexity(files);
		Set<String> keySet = ciclomaticComplexity.keySet();
		for (String key : keySet) {
			System.out.println(key + " - " + ciclomaticComplexity.get(key));
		}
	}*/

	/**
	 * @param files
	 * @return
	 * @throws IOException
	 */
	public static void getCyclomaticComplexity(List<Klass> javaFiles) throws IOException {
		String[] patterns = new String[] { "(\\s+)(for|if|while|switch)(\\s*\\()", // if,for,while,switch
				"(\\s*)(&&|\\|\\|)(\\s*)", // &&, ||
				".*\\?.*:.*;", // conditional
				"(\\s+)(throw[s]?)(\\s+)" //throw(s)  
		};
		// remove patterns inside comments
		String[] removePatterns = new String[] { "[\\\"].*(&&|\\|\\||throw[s]?).*[\\\"]", "[\\\"].*(for|if|while|switch).*[\\\"]" };

		for (Klass k : javaFiles) {
			int countPattern = 0;
			for (String string : patterns) {
				countPattern += FileAnalyzer.countPattern(k.getContent(), string, false);
			}
			int countPatternRemove = 0;
			for (String string : removePatterns) {
				countPatternRemove += FileAnalyzer.countPattern(k.getContent(), string, false);
			}
			/* remove patterns from comments */
			countPattern = countPattern > 0 ? countPattern - countPatternRemove : 0;
			/* if contains that key, insert with plus */
			k.setCyclomaticComplexity(countPattern);
		}
	}

}