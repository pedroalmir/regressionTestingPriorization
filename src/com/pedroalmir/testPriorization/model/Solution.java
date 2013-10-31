/**
 * 
 */
package com.pedroalmir.testPriorization.model;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

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
				buffer.append(String.format("%02d", betterTestCases.get(i).getId()) + "-");
			}else{
				buffer.append(String.format("%02d", betterTestCases.get(i).getId()) + "]");
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
}
