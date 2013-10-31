/**
 * 
 */
package com.pedroalmir.testPriorization.util.csv;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;

import com.pedroalmir.testPriorization.flow.parser.InputParser;
import com.pedroalmir.testPriorization.model.Klass;
import com.pedroalmir.testPriorization.model.RegressionTestingPriorizationProblem;
import com.pedroalmir.testPriorization.model.Requirement;

/**
 * @author Pedro Almir
 *
 */
public class CSVUtil {
	
	private final static String PROBLEM = "C:/Users/Pedro Almir/Desktop/problem.csv";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			CSVUtil.printProblem(PROBLEM, InputParser.createProblem("C:/Users/Pedro Almir/Desktop/Infoway/workspaceKepler/trunk_MAA/src/main"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void printProblem(String filePath, RegressionTestingPriorizationProblem problem) throws IOException{
		String[] stringLine;
		List<String[]> data = new ArrayList<String[]>();
		CSVWriter writer = new CSVWriter(new FileWriter(filePath), ';');
		
		int totalSize = problem.getKlasses().size() + 4;
		/* Header */
		String[] header = new String[totalSize];
		header[0] = "Nome";
		header[1] = "Descricao";
		header[2] = "Tipo";
		header[3] = "Importancia";
		for(int i = 4; i < totalSize; i++){
			header[i] = problem.getKlasses().get(i-4).getName();
		}
		data.add(header);
		/* Body */
		for(Requirement req : problem.getRequirements()){
			stringLine = new String[totalSize];
			stringLine[0] = req.getName();
			stringLine[1] = req.getDescription();
			stringLine[2] = req.getEnumJhmType().getDescription();
			stringLine[3] = "Informe a importancia!!!";
			int count = 4;
			for(Klass klass : req.getCorrelation().keySet()){
				stringLine[count++] = req.getCorrelation().get(klass).toString();
			}
			data.add(stringLine);
		}
		writer.writeAll(data);
		writer.close();
	}
	
	/**
	 * @return csv file
	 * @throws IOException 
	 */
	public static void printProblemInSQFD(String filePath, RegressionTestingPriorizationProblem problem) throws IOException{
		String[] stringLine;
		List<String[]> data = new ArrayList<String[]>();
		CSVWriter writer = new CSVWriter(new FileWriter(filePath), ';');
		
		
		for(int req = 0; req < problem.getRequirements().size(); req++){
			stringLine = new String[problem.getKlasses().size()];
			//each column
			for(int clas = 0; clas < problem.getKlasses().size() + 3; clas++){
				//title of first row
				if(req == 0){
					if(clas == 0){
						stringLine[clas] = "Requirement\\Class";
					}else if(clas < problem.getKlasses().size() - 3){
						stringLine[clas] = problem.getKlasses().get(clas).getName();
					}else{
						stringLine[clas] = "Client Priority";
					}
				}else{
					//title of row
					if(clas == 0){
						stringLine[clas] = problem.getRequirements().get(req).getName();
					}else if(clas < problem.getKlasses().size() - 3){
						stringLine[clas] = problem.getRequirements().get(req).getCorrelation().get(problem.getKlasses().get(clas)) + "";
					}else{
						stringLine[clas] = "Insira um valor!!!";
					}
					
				}
			}
			data.add(stringLine);
		}
		writer.writeAll(data);
		writer.close();
	}
}
