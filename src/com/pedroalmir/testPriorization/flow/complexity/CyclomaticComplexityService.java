/**
 * 
 */
package com.pedroalmir.testPriorization.flow.complexity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pedroalmir.testPriorization.util.file.FileAnalyzer;
import com.pedroalmir.testPriorization.util.file.MyDirectoryWalker;

public class CyclomaticComplexityService {

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
	}

	public Map<String, Integer> getCiclomaticComplexity(List<File> files) throws IOException {
		String[] patterns = new String[] { "(\\s+)(for|if|while|switch)(\\s*\\()", // if,for,while,switch
				"(\\s*)(&&|\\|\\|)(\\s*)", // &&, ||
				".*\\?.*:.*;" // conditional
		};
		// remove patterns inside comments
		String[] removePatterns = new String[] { "[\\\"].*(&&|\\|\\|).*[\\\"]", "[\\\"].*(for|if|while|switch).*[\\\"]" };

		String content = "";
		Map<String, Integer> complexityByClass = new HashMap<String, Integer>();

		for (int i = 0; i < files.size(); i++) {
			File file = files.get(i);
			content = FileAnalyzer.getFileContent(file);
			int countPattern = 0;
			for (String string : patterns) {
				countPattern += FileAnalyzer.countPattern(content, string, false);
			}
			int countPatternRemove = 0;
			for (String string : removePatterns) {
				countPatternRemove += FileAnalyzer.countPattern(content, string, false);
			}
			// remove patterns from comments
			countPattern = countPattern > 0 ? countPattern - countPatternRemove : 0;
			// if contains that key, insert with plus
			complexityByClass
					.put((complexityByClass.containsKey(file.getName()) == true ? file.getName() + "+" : file.getName()),
							countPattern);
		}

		return complexityByClass;
	}

}