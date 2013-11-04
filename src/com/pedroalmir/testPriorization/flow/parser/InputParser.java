/**
 * 
 */
package com.pedroalmir.testPriorization.flow.parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.pedroalmir.testPriorization.flow.complexity.CyclomaticComplexityService;
import com.pedroalmir.testPriorization.flow.correlation.CorrelationFeaturesAndAssetsThreeLevelXML;
import com.pedroalmir.testPriorization.flow.requirement.RequirementExtractor;
import com.pedroalmir.testPriorization.model.Context;
import com.pedroalmir.testPriorization.model.Klass;
import com.pedroalmir.testPriorization.model.RegressionTestingPriorizationProblem;
import com.pedroalmir.testPriorization.model.Requirement;
import com.pedroalmir.testPriorization.model.TestCase;
import com.pedroalmir.testPriorization.util.file.FileAnalyzer;
import com.pedroalmir.testPriorization.util.file.MyDirectoryWalker;
import com.pedroalmir.testPriorization.util.svn.SVNService;

/**
 * @author Pedro Almir
 *
 */
public class InputParser {
	
	/**
	 * @param context
	 * @param capacidade
	 * @return
	 */
	public static RegressionTestingPriorizationProblem createProblem(Context context, Integer capacidade) {
		try {
			RegressionTestingPriorizationProblem problem = createProblem(context.getBaseFolder(), context.getCouplingFile(), context.getSqfdFile(),
					context.getTestCoverageFile(), context.getCyclomaticFile());
			problem.setCapacidade(capacidade);
			return problem;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param baseFolder
	 * @param couplingFile
	 * @param sqfdFile
	 * @param testCoverageFile
	 * @param classNumber
	 * @param requirementNumber
	 * @return RegressionTestingPriorizationProblem
	 * @throws IOException
	 */
	public static RegressionTestingPriorizationProblem createProblem(String baseFolder, String couplingFile, String sqfdFile,
			String testCoverageFile, Integer classNumber, Integer requirementNumber) throws IOException {
		/* Creating regression testing priorization problem */
		RegressionTestingPriorizationProblem priorizationProblem = new RegressionTestingPriorizationProblem(1L, classNumber, requirementNumber);
		
		/* Read and extract data from SQFD File */
		CSVReader reader = new CSVReader(new FileReader(baseFolder + sqfdFile), ';');
		List<String[]> myEntries = reader.readAll();
		/* */
		LinkedList<Klass> klasses = new LinkedList<Klass>();
		LinkedList<Requirement> requirements = new LinkedList<Requirement>();
		LinkedList<TestCase> testCases = new LinkedList<TestCase>();
		
		/* Create list of klass based on first line */
		String[] firstLine = myEntries.get(0);
		for(int i = 1; i < firstLine.length; i++){
			if(firstLine[i].contains("Class") || firstLine[i].contains("class") || firstLine[i].contains(".java")){
				Klass klass = new Klass(Long.valueOf(i), firstLine[i]);
				klasses.add(klass);
			}
		}
		/* Iterate over the rest of lines */
		for(int i = 1; i < myEntries.size(); i++){
			String[] line = myEntries.get(i);
			Requirement requirement = new Requirement(Long.valueOf(i), line[0]);
			/* Create correlation between class and requirement */
			for(int j = 0; j < klasses.size(); j++){
				klasses.get(j).addCorrelation(requirement, Integer.valueOf(line[j+1].trim()));
			}
			/* Add list of client priority */
			for(int j = klasses.size() + 1; j < line.length; j++){
				requirement.addClientPriority(Integer.valueOf(line[j].trim()));
			}
			/* Calculate importance */
			requirement.calculateImportance();
			/* Add requirement to list of requirements */
			requirements.add(requirement);
		}
		
		/* Read and extract data from Coupling File */
		reader = new CSVReader(new FileReader(baseFolder + couplingFile), ';');
		myEntries = reader.readAll();
		/* */
		firstLine = myEntries.get(0);
		String[] secondLine = myEntries.get(1);
		for(int i = 0; i < secondLine.length; i++){
			Klass klass = klasses.get(i);
			if(klass.getName().equalsIgnoreCase(firstLine[i].trim())){
				klass.setCoupling(Integer.valueOf(secondLine[i].trim()));
			}
		}
		
		/* Read and extract data from Test Coverage File */
		reader = new CSVReader(new FileReader(baseFolder + testCoverageFile), ';');
		myEntries = reader.readAll();
		
		/* Iterate over the rest of lines */
		for(int i = 1; i < myEntries.size(); i++){
			String[] line = myEntries.get(i);
			TestCase testCase = new TestCase(Long.valueOf(i), line[0], Double.valueOf(line[line.length-1].replace(",", ".")));
			
			/* Create relation between class and test coverage */
			for(int j = 0; j < klasses.size(); j++){
				klasses.get(j).addCoverageRelation(testCase, Double.valueOf(line[j+1].trim().replace(",", ".")));
				testCase.addCoverageRelation(klasses.get(j), Double.valueOf(line[j+1].trim().replace(",", ".")));
			}
			/* Add test case to list of tests */
			testCases.add(testCase);
		}
		/* Update problem */
		priorizationProblem.setKlasses(klasses);
		priorizationProblem.setRequirements(requirements);
		priorizationProblem.setTestCases(testCases);
		/* Close reader */
		reader.close();
		/* Return priorization problem */
		return priorizationProblem;
	}
	
	public static RegressionTestingPriorizationProblem createProblem(String baseFolder, String couplingFile, String sqfdFile,
			String testCoverageFile, String cyclomaticFile) throws IOException {
		/* Creating regression testing priorization problem */
		RegressionTestingPriorizationProblem priorizationProblem = new RegressionTestingPriorizationProblem(1L);
		
		/* Read and extract data from SQFD File */
		CSVReader reader = new CSVReader(new FileReader(baseFolder + sqfdFile), ';');
		List<String[]> myEntries = reader.readAll();
		/* */
		LinkedList<Klass> klasses = new LinkedList<Klass>();
		LinkedList<Requirement> requirements = new LinkedList<Requirement>();
		LinkedList<TestCase> testCases = new LinkedList<TestCase>();
		
		/* Create list of klass based on first line */
		String[] firstLine = myEntries.get(0);
		for(int i = 1; i < firstLine.length; i++){
			if(firstLine[i].contains("Class") || firstLine[i].contains("class") || firstLine[i].contains(".java")){
				Klass klass = new Klass(Long.valueOf(i), firstLine[i]);
				klasses.add(klass);
			}
		}
		/* Iterate over the rest of lines */
		for(int i = 1; i < myEntries.size(); i++){
			String[] line = myEntries.get(i);
			Requirement requirement = new Requirement(Long.valueOf(i), line[0]);
			/* Create correlation between class and requirement */
			for(int j = 0; j < klasses.size(); j++){
				klasses.get(j).addCorrelation(requirement, Integer.valueOf(line[j+1].trim()));
			}
			/* Add list of client priority */
			for(int j = klasses.size() + 1; j < line.length; j++){
				requirement.addClientPriority(Integer.valueOf(line[j].trim()));
			}
			/* Calculate importance */
			requirement.calculateImportance();
			/* Add requirement to list of requirements */
			requirements.add(requirement);
		}
		
		/* Read and extract data from Coupling File */
		reader = new CSVReader(new FileReader(baseFolder + couplingFile), ';');
		myEntries = reader.readAll();
		/* */
		firstLine = myEntries.get(0);
		String[] secondLine = myEntries.get(1);
		for(int i = 0; i < secondLine.length; i++){
			Klass klass = klasses.get(i);
			if(klass.getName().equalsIgnoreCase(firstLine[i].trim())){
				klass.setCoupling(Integer.valueOf(secondLine[i].trim()));
			}
		}
		
		/* Read and extract data from Cyclomatic Complexity File */
		reader = new CSVReader(new FileReader(baseFolder + cyclomaticFile), ';');
		myEntries = reader.readAll();
		/* */
		firstLine = myEntries.get(0);
		secondLine = myEntries.get(1);
		for(int i = 0; i < secondLine.length; i++){
			Klass klass = klasses.get(i);
			if(klass.getName().equalsIgnoreCase(firstLine[i].trim())){
				klass.setCyclomaticComplexity(Integer.valueOf(secondLine[i].trim()));
				klass.setCyclomaticComplexityNormalized(Integer.valueOf(secondLine[i].trim()));
			}
		}
		
		/* Read and extract data from Test Coverage File */
		reader = new CSVReader(new FileReader(baseFolder + testCoverageFile), ';');
		myEntries = reader.readAll();
		
		/* Iterate over the rest of lines */
		for(int i = 1; i < myEntries.size(); i++){
			String[] line = myEntries.get(i);
			TestCase testCase = new TestCase(Long.valueOf(i), line[0], Double.valueOf(line[line.length-1].replace(",", ".")));
			
			/* Create relation between class and test coverage */
			for(int j = 0; j < klasses.size(); j++){
				klasses.get(j).addCoverageRelation(testCase, Double.valueOf(line[j+1].trim().replace(",", ".")));
				testCase.addCoverageRelation(klasses.get(j), Double.valueOf(line[j+1].trim().replace(",", ".")));
			}
			/* Add test case to list of tests */
			testCases.add(testCase);
		}
		/* Update problem */
		priorizationProblem.setKlasses(klasses);
		priorizationProblem.setRequirements(requirements);
		priorizationProblem.setTestCases(testCases);
		/* Close reader */
		reader.close();
		/* Return priorization problem */
		return priorizationProblem;
	}
	
	public static RegressionTestingPriorizationProblem createProblem(String baseURL, String baseFolder, 
			String username, String password, boolean needLogin) throws IOException {
		/* .java and .jhm.xml */
		List<File> listAllFiles = SVNService.listAllFiles(baseURL, baseFolder, username, password, needLogin);
		/* Creating regression testing priorization problem */
		RegressionTestingPriorizationProblem priorizationProblem = new RegressionTestingPriorizationProblem(1L);
		List<File> javaFiles = getOnlyJavaFiles(listAllFiles);
		List<File> jhmFiles = getOnlyJhmFiles(listAllFiles); 
		LinkedList<Requirement> extractedRequirements = RequirementExtractor.extractRequirements(jhmFiles, javaFiles);
		/* Add all requirements */
		priorizationProblem.setRequirements(extractedRequirements);
		/* Add all klasses */
		priorizationProblem.setKlasses(getAllClasses(listAllFiles));
		/* Correlations */
		CorrelationFeaturesAndAssetsThreeLevelXML.fillCorrelation(priorizationProblem);
		/* Cyclomatic Complexity */
		CyclomaticComplexityService.getCyclomaticComplexity(priorizationProblem.getKlasses());
		return priorizationProblem;
	}
	
	/**
	 * @param baseFolder
	 * @return
	 * @throws IOException
	 */
	public static RegressionTestingPriorizationProblem createProblem(String baseFolder) throws IOException {
		/* Base folder */
		File dir = new File(baseFolder);
		/* .java and .jhm.xml */
		MyDirectoryWalker walker = new MyDirectoryWalker(".java", 10);
		MyDirectoryWalker walkerII = new MyDirectoryWalker(".jhm.xml", 10);
		List<File> javaFiles = new ArrayList<File>();
		List<File> jhmFiles = new ArrayList<File>();
		/* Collect all files with specified extension inside directory */
		if (dir.isDirectory()) {
			javaFiles = walker.getExtensionFiles(dir);
			jhmFiles = walkerII.getExtensionFiles(dir);
		} else {
			System.out.println("Isn't a folder!");
			throw new RuntimeException("Isn't a folder!");
		}
		/* Creating regression testing priorization problem */
		RegressionTestingPriorizationProblem priorizationProblem = new RegressionTestingPriorizationProblem(1L);
		LinkedList<Requirement> extractedRequirements = RequirementExtractor.extractRequirements(jhmFiles, javaFiles);
		/* Add all requirements */
		priorizationProblem.setRequirements(extractedRequirements);
		/* Add all classes */
		priorizationProblem.setKlasses(getAllClasses(javaFiles));
		/* Correlations */
		System.out.println("Correlation Features And Assets Three Level XML...");
		CorrelationFeaturesAndAssetsThreeLevelXML.fillCorrelation(priorizationProblem);
		/* Cyclomatic Complexity */
		System.out.println("Cyclomatic Complexity...");
		CyclomaticComplexityService.getCyclomaticComplexity(priorizationProblem.getKlasses());
		/* Cyclomatic Complexity Normalized */
		normalizeCyclomaticComplexity(priorizationProblem.getKlasses());
		return priorizationProblem;
	}
	
	/**
	 * @param klasses
	 */
	private static void normalizeCyclomaticComplexity(LinkedList<Klass> klasses) {
		double count = 0.0;
		for (Klass klass : klasses) {
			count += klass.getCyclomaticComplexity();
		}
		for (Klass klass : klasses) {
			klass.setCyclomaticComplexityNormalized((klass.getCyclomaticComplexity()/count)*10);
		}
	}

	/**
	 * @param listAllFiles
	 * @return list of klass
	 * @throws IOException 
	 */
	private static LinkedList<Klass> getAllClasses(List<File> listAllFiles) throws IOException{
		LinkedList<Klass> klasses = new LinkedList<Klass>();
		long id = 1l;
		for (File file : listAllFiles) {
			if(FilenameUtils.getExtension(file.getName()).equals("java")){
				Klass klass = new Klass(id++, file.getName());
				String klassContent = FileAnalyzer.getContent(file);
				if(klassContent.isEmpty()){
					continue;
				}
				klass.setContent(klassContent);
				klasses.add(klass);
			}
		}
		return klasses;
	}
	
	public static void main(String[] args) {
		try {
			InputParser.createProblem("svn://10.0.0.209:3692/MAA/", "trunk/src/main/resources", "pedroalmir", "pedroalmir", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param listAllFiles
	 * @return
	 */
	private static List<File> getOnlyJavaFiles(List<File> listAllFiles) {
		List<File> result = new LinkedList<File>();
		for (File f : listAllFiles) {
			if(FilenameUtils.getExtension(f.getName()).equals("java")){
				result.add(f);
			}
		}
		return result;
	}
	
	/**
	 * @param listAllFiles
	 * @return
	 */
	private static List<File> getOnlyJhmFiles(List<File> listAllFiles) {
		List<File> result = new LinkedList<File>();
		for (File f : listAllFiles) {
			if(f.getName().contains(".jhm.xml")){
				result.add(f);
			}
		}
		return result;
	}
	
}
