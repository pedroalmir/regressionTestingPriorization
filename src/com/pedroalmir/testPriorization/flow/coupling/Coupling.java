package com.pedroalmir.testPriorization.flow.coupling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import com.pedroalmir.testPriorization.flow.sqfd.SQFD;

public class Coupling {

	public static void run(String projectPath) {
		Collection<File> listOfJavaFiles = getListOfJavaFiles(projectPath);
		LinkedList<String> listOfJavaNames = getListOfJavaNames(listOfJavaFiles);

		Map<File, Integer> coupling = analyzeCoupling(listOfJavaFiles, listOfJavaNames);
		Integer major = Integer.MIN_VALUE;
		
		for (File classe : coupling.keySet()) {
			System.out.println("[" + classe.getName() + ": " + coupling.get(classe) + "]");
			if(coupling.get(classe) > major){
				major = coupling.get(classe);
			}
		}
		
		for (File classe : coupling.keySet()) {
			Integer value = coupling.get(classe);
			value = (value*10)/major;
		}
	}

	/**
	 * @param listOfJavaFiles
	 * @param listOfJavaNames
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Map<File, Integer> analyzeCoupling(Collection<File> listOfJavaFiles, LinkedList<String> listOfJavaNames) {
		Map<File, Integer> result = new LinkedHashMap<File, Integer>();
		Integer couplingValue = 0;
		
		for (File classe : listOfJavaFiles) {
			LinkedList<String> names = (LinkedList<String>) listOfJavaNames.clone();
			names.remove(classe.getName());
			try {
				/* */
				BufferedReader in = new BufferedReader(new FileReader(classe));
				while (in.ready()) {
					couplingValue += process(in.readLine(), names);
				}
				in.close();
				/* */
				result.put(classe, couplingValue);
				couplingValue = 0;
			} catch (IOException ioE) {
				ioE.printStackTrace();
			}
		}
		return result;
	}


	/**
	 * @param readLine
	 * @param names
	 * @return
	 */
	private static Integer process(String readLine, LinkedList<String> names) {
		Integer value = 0;
		if(!readLine.contains("import")){
			for (String sub : names) {
				value += StringUtils.countMatches(readLine, FilenameUtils.removeExtension(sub));
			}
		}
		return value;
	}

	/**
	 * @param listOfJavaFiles
	 * @return
	 */
	private static LinkedList<String> getListOfJavaNames(Collection<File> listOfJavaFiles) {
		LinkedList<String> result = new LinkedList<String>();
		for (File classe : listOfJavaFiles) {
			result.add(classe.getName());
		}
		return result;
	}

	/**
	 * @param projectPath
	 * @return
	 */
	private static Collection<File> getListOfJavaFiles(String projectPath) {
		Collection<File> files = FileUtils.listFiles(new File(projectPath), new String[] { "java" }, true);
		Collection<File> filesToAnalyze = new LinkedList<File>();
		for (File classe : files) {
			if (!classe.getName().equals("package-info.java")) {
				/* Just for debug */
				/* System.out.println(classe.getAbsolutePath()); */
				filesToAnalyze.add(classe);
			}
		}
		return filesToAnalyze;
	}

	public static void main(String[] args) {
		Coupling.run("C:/Users/Pedro Almir/Desktop/MeusProjetos/IC_TCC/Athena/workspaceKepler/RegressionTestingPriorization");
		new SQFD();
		new SQFD();
	}
}
