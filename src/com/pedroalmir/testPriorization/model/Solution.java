/**
 * 
 */
package com.pedroalmir.testPriorization.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import au.com.bytecode.opencsv.CSVWriter;

import com.pedroalmir.testPriorization.model.solution.IterationSolution;

/**
 * @author Pedro Almir
 *
 */
public class Solution {
	private String algorithm;
	private List<TestCase> betterTestCases;
	private double capacidade;
	private int numberOfClasses;
	private int numberOfRequirements;
	private int numberOfTests;
	
	private double timeOfTests;
	private double criticality;
	private long executionTime;
	
	private List<IterationSolution> iterationSolutions;
	private List<double[]> bruteForceValues;
	private AntConfiguration antConfiguration;
	
	/**
	 * @param betterTestCases
	 * @param capacidade
	 * @param numberOfClasses
	 * @param numberOfRequirements
	 * @param numberOfTests
	 * @param timeOfTests
	 * @param criticality
	 * @param executionTime
	 */
	public Solution(String algorithm, List<TestCase> betterTestCases, double capacidade, int numberOfClasses, int numberOfRequirements,
			int numberOfTests, double timeOfTests, double criticality, long executionTime) {
		super();
		this.algorithm = algorithm;
		this.betterTestCases = betterTestCases;
		this.capacidade = capacidade;
		this.numberOfClasses = numberOfClasses;
		this.numberOfRequirements = numberOfRequirements;
		this.numberOfTests = numberOfTests;
		this.timeOfTests = timeOfTests;
		this.criticality = criticality;
		this.executionTime = executionTime;
		this.iterationSolutions = new LinkedList<IterationSolution>();
	}
	
	/**
	 * 
	 */
	public Solution() {
		this.iterationSolutions = new LinkedList<IterationSolution>();
	}
	
	/**
	 * @return the betterTestCases
	 */
	public List<TestCase> getBetterTestCases() {
		return betterTestCases;
	}
	/**
	 * @param betterTestCases the betterTestCases to set
	 */
	public void setBetterTestCases(List<TestCase> betterTestCases) {
		this.betterTestCases = betterTestCases;
	}
	/**
	 * @return the capacidade
	 */
	public double getCapacidade() {
		return capacidade;
	}
	/**
	 * @param capacidade the capacidade to set
	 */
	public void setCapacidade(double capacidade) {
		this.capacidade = capacidade;
	}
	/**
	 * @return the numberOfClasses
	 */
	public int getNumberOfClasses() {
		return numberOfClasses;
	}
	/**
	 * @param numberOfClasses the numberOfClasses to set
	 */
	public void setNumberOfClasses(int numberOfClasses) {
		this.numberOfClasses = numberOfClasses;
	}
	/**
	 * @return the numberOfRequirements
	 */
	public int getNumberOfRequirements() {
		return numberOfRequirements;
	}
	/**
	 * @param numberOfRequirements the numberOfRequirements to set
	 */
	public void setNumberOfRequirements(int numberOfRequirements) {
		this.numberOfRequirements = numberOfRequirements;
	}
	/**
	 * @return the numberOfTests
	 */
	public int getNumberOfTests() {
		return numberOfTests;
	}
	/**
	 * @param numberOfTests the numberOfTests to set
	 */
	public void setNumberOfTests(int numberOfTests) {
		this.numberOfTests = numberOfTests;
	}
	/**
	 * @return the timeOfTests
	 */
	public double getTimeOfTests() {
		return timeOfTests;
	}
	/**
	 * @param timeOfTests the timeOfTests to set
	 */
	public void setTimeOfTests(double timeOfTests) {
		this.timeOfTests = timeOfTests;
	}
	/**
	 * @return the criticality
	 */
	public double getCriticality() {
		return criticality;
	}
	/**
	 * @param criticality the criticality to set
	 */
	public void setCriticality(double criticality) {
		this.criticality = criticality;
	}
	/**
	 * @return the executionTime
	 */
	public long getExecutionTime() {
		return executionTime;
	}
	/**
	 * @param executionTime the executionTime to set
	 */
	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		/*StringBuffer buffer = new StringBuffer();
		for(int i = 0; i < betterTestCases.size(); i++){
			if(i < betterTestCases.size()-2){
				buffer.append("(" + betterTestCases.get(i).getId() + ", " + betterTestCases.get(i).getTime() + ")-");
			}else{
				buffer.append("(" + betterTestCases.get(i).getId() + ", " + betterTestCases.get(i).getTime() + ")");
			}
		}*/
		
		Collections.sort(betterTestCases, new Comparator<TestCase>() {
			@Override
			public int compare(TestCase t1, TestCase t2) {
				return t1.getId().compareTo(t2.getId());
			}
		});
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		for(int i = 0; i < betterTestCases.size(); i++){
			if(i < betterTestCases.size()-1){
				//buffer.append(String.format(Locale.US, "%02d(%.2f,%.2f)", betterTestCases.get(i).getId(), 
						//betterTestCases.get(i).getCriticality(), betterTestCases.get(i).getTime()) + "-");
				buffer.append(String.format(Locale.US, "%02d", betterTestCases.get(i).getId()) + "-");
			}else{
				//buffer.append(String.format(Locale.US, "%02d(%.2f,%.2f)", betterTestCases.get(i).getId(),
						//betterTestCases.get(i).getCriticality(), betterTestCases.get(i).getTime()) + "]");
				buffer.append(String.format(Locale.US, "%02d", betterTestCases.get(i).getId()) + "]");
			}
		}
		
		String time = timeOfTests + "";
		time = time.replace(".", ",");
		
		String criticidade = criticality + "";
		criticidade = criticidade.replace(".", ",");
		
		/*return "Solution [algorithm=" + algorithm + ", betterTestCases=" + buffer.toString() + ", capacidade=" + capacidade + ", numberOfClasses="
				+ numberOfClasses + ", numberOfRequirements=" + numberOfRequirements + ", numberOfTests=" + numberOfTests
				+ ", criticality=" + criticidade + ", timeOfTests=" + time + ", executionTime=" + new SimpleDateFormat("ss:SSS").format(executionTime) + "]";*/
		return "Solution [algorithm=" + algorithm + ", betterTestCases=" + buffer.toString() + 
				", criticality=" + criticidade + ", timeOfTests=" + time + ", executionTime=" + new SimpleDateFormat("mm:ss:SSS").format(executionTime) + "]";		
	}
	
	/**
	 * Print this in CSV File
	 * @param csvFile
	 */
	public void printThis(File csvFile){
        try {
        	List<String[]> data = new ArrayList<String[]>();
			CSVWriter writer = new CSVWriter(new FileWriter(csvFile), ';');
			
			String[] emptyLine = new String[]{""};
			/* Default */
			data.add(new String[]{"Analise do Comportamento do Algoritmo: " + this.algorithm});
			data.add(emptyLine);
			data.add(new String[]{"Quantidade de Classes", this.numberOfClasses + "", "Quantidade de Testes", this.numberOfTests + ""});
			data.add(new String[]{"Quantidade de Requisitos", this.numberOfRequirements + "", "Espaco de Busca", String.format("%.2f", Math.pow(2, this.numberOfTests)) + ""});
			data.add(new String[]{"Capacidade da Mochila", String.format("%.2f", this.capacidade) + ""});
			
			if(this.antConfiguration != null){
				data.add(emptyLine);
				data.add(new String[]{"Numero de Formigas", this.antConfiguration.getNumAgents() + "", "Q", String.format("%.4f", this.antConfiguration.getQ()) + ""});
				data.add(new String[]{"Alfa", String.format("%.2f", this.antConfiguration.getAlpha()) + "", "Beta", String.format("%.2f", this.antConfiguration.getBeta()) + ""});
				data.add(new String[]{"Quantidade Inicial de Feromonio", String.format("%.2f", this.antConfiguration.getInitialPheromone()) + "", "Taxa de Evaporacao", String.format("%.2f", this.antConfiguration.getPheromonePersistence()) + ""});
				data.add(new String[]{"Maximo de Iteracoes", this.antConfiguration.getMaxIterations() + "", "Maximo de Execucoes", this.antConfiguration.getMaxExecutions() + ""});
				data.add(emptyLine);
				data.add(new String[]{"Testes Selecionados", this.printTest(this.betterTestCases) + "", "Tempo de Execucao", new SimpleDateFormat("mm:ss:SSS").format(this.executionTime) + ""});
				data.add(new String[]{"Criticidade Total", String.format("%.4f", this.criticality) + ""});
				data.add(new String[]{"Tempo Total dos Testes", String.format("%.4f", this.timeOfTests) + ""});
				
				data.add(emptyLine);
				data.add(emptyLine);
				
				int count = 1;
				String[] header = new String[this.iterationSolutions.size()+1];
				String[] betterSolution = new String[this.iterationSolutions.size()+1];
				String[] worstSolution = new String[this.iterationSolutions.size()+1];
				String[] average = new String[this.iterationSolutions.size()+1];
				String[] standardDeviation = new String[this.iterationSolutions.size()+1];
				String[] elitism = new String[this.iterationSolutions.size()+1];
				String[] test = new String[this.iterationSolutions.size()+1];
				String[] criticality = new String[this.iterationSolutions.size()+1];
				String[] time = new String[this.iterationSolutions.size()+1];
				
				header[0] 			 = "";
				betterSolution[0] 	 = "BetterSolution";
				worstSolution[0] 	 = "WorstSolution";
				average[0] 			 = "Average";
				standardDeviation[0] = "StandardDeviation";
				elitism[0] 			 = "Elitism";
				test[0] 			 = "Tests";
				criticality[0] 		 = "Criticality";
				time[0] 			 = "Time";
				

				for(IterationSolution it : this.iterationSolutions){
					header[count] 			 = "Iteracao " + count;
					betterSolution[count] 	 = "" + String.format("%.4f", it.getBetterSolution());
					worstSolution[count] 	 = "" + String.format("%.4f", it.getWorstSolution());
					average[count] 			 = "" + String.format("%.4f", it.getAverage());
					standardDeviation[count] = "" + String.format("%.4f", it.getStandardDeviation());
					elitism[count] 			 = "" + String.format("%.4f", it.getTheBest());
					
					test[count] 			 = "" + this.printTest(it.getTestCases());
					criticality[count] 		 = "" + String.format("%.4f", it.getCriticidadeTotal());
					time[count] 			 = "" + String.format("%.4f", it.getTempoTotal());
					count++;
				}
				
				data.add(header);
				data.add(betterSolution);
				data.add(worstSolution);
				data.add(average);
				data.add(standardDeviation);
				data.add(elitism);
				
				data.add(emptyLine);
				
				data.add(test);
				data.add(criticality);
				data.add(time);
				
			}else if(this.bruteForceValues != null){
				data.add(emptyLine);
				data.add(new String[]{"Dispersao do espaco de Busca"});
				data.add(emptyLine);
				data.add(new String[]{"Criticality", "Time"});
				for(double[] d : this.bruteForceValues){
					data.add(new String[]{String.format("%.2f", d[1]) + "", String.format("%.2f", d[0]) + ""});
				}
			}
			
			writer.writeAll(data);
	        writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return
	 */
	private String printTest(List<TestCase> testCases){
		String format = "%02d";
		if(this.numberOfTests >= 100){
			format = "%03d";
		}else if(this.numberOfTests >= 1000){
			format = "%04d";
		}
		
		Collections.sort(testCases, new Comparator<TestCase>() {
			@Override
			public int compare(TestCase t1, TestCase t2) {
				return t1.getId().compareTo(t2.getId());
			}
		});
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		for(int i = 0; i < testCases.size(); i++){
			if(i < testCases.size()-1){
				buffer.append(String.format(format, testCases.get(i).getId()) + "-");
			}else{
				buffer.append(String.format(format, testCases.get(i).getId()) + "]");
			}
		}
		return buffer.toString();
	}

	/**
	 * @return the algorithm
	 */
	public String getAlgorithm() {
		return algorithm;
	}

	/**
	 * @param algorithm the algorithm to set
	 */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * @return the iterationSolutions
	 */
	public List<IterationSolution> getIterationSolutions() {
		return iterationSolutions;
	}

	/**
	 * @param iterationSolutions the iterationSolutions to set
	 */
	public void setIterationSolutions(List<IterationSolution> iterationSolutions) {
		this.iterationSolutions = iterationSolutions;
	}

	/**
	 * @return the bruteForceValues
	 */
	public List<double[]> getBruteForceValues() {
		return bruteForceValues;
	}

	/**
	 * @param bruteForceValues the bruteForceValues to set
	 */
	public void setBruteForceValues(List<double[]> bruteForceValues) {
		this.bruteForceValues = bruteForceValues;
	}

	/**
	 * @return the antConfiguration
	 */
	public AntConfiguration getAntConfiguration() {
		return antConfiguration;
	}

	/**
	 * @param antConfiguration the antConfiguration to set
	 */
	public void setAntConfiguration(AntConfiguration antConfiguration) {
		this.antConfiguration = antConfiguration;
	}
}

