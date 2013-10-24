/**
 * 
 */
package com.pedroalmir.testPriorization.flow.parser;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

import com.pedroalmir.testPriorization.model.Klass;
import com.pedroalmir.testPriorization.model.RegressionTestingPriorizationProblem;
import com.pedroalmir.testPriorization.model.Requirement;
import com.pedroalmir.testPriorization.model.TestCase;

/**
 * @author Pedro Almir
 *
 */
public class InputParser {

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
		List<Klass> klasses = new ArrayList<Klass>(classNumber);
		List<Requirement> requirements = new ArrayList<Requirement>(requirementNumber);
		List<TestCase> testCases = new ArrayList<TestCase>(requirementNumber);
		
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
		
		/* Read and extract data from Coupling File */
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
	
}
