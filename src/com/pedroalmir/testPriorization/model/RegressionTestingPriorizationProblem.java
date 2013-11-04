/**
 * 
 */
package com.pedroalmir.testPriorization.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Pedro Almir
 *
 */
public class RegressionTestingPriorizationProblem {
	
	private Long id;
	private Date creationDate;
	private Date lastExecution;
	
	private String codePath;
	private String sqfdFilePath;
	private String testCaseFilePath;
	
	private LinkedList<Klass> klasses;
	private LinkedList<Requirement> requirements;
	private LinkedList<TestCase> testCases;
	
	private double capacidade;
	private long partial;
	
	/**
	 * Base Constructor
	 * 
	 * @param id
	 * @param classNumber
	 * @param requirementNumber
	 */
	public RegressionTestingPriorizationProblem(Long id, int classNumber, int requirementNumber) {
		this.id = id;
		this.creationDate = new Date();
		this.klasses = new LinkedList<Klass>();
		this.requirements = new LinkedList<Requirement>();
		/* Same number of requirements */
		this.testCases = new LinkedList<TestCase>();
		
		this.testCaseFilePath = "Not used yet!";
		this.sqfdFilePath = "Not used yet!";
		this.codePath = "Not used yet!";
	}
	
	
	/**
	 * @param id
	 */
	public RegressionTestingPriorizationProblem(Long id) {
		this.id = id;
		this.creationDate = new Date();
		this.klasses = new LinkedList<Klass>();
		this.requirements = new LinkedList<Requirement>();
		/* Same number of requirements */
		this.testCases = new LinkedList<TestCase>();
		
		this.testCaseFilePath = "Not used yet!";
		this.sqfdFilePath = "Not used yet!";
		this.codePath = "Not used yet!";
	}
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the lastExecution
	 */
	public Date getLastExecution() {
		return lastExecution;
	}
	/**
	 * @param lastExecution the lastExecution to set
	 */
	public void setLastExecution(Date lastExecution) {
		this.lastExecution = lastExecution;
	}
	/**
	 * @return the codePath
	 */
	public String getCodePath() {
		return codePath;
	}
	/**
	 * @param codePath the codePath to set
	 */
	public void setCodePath(String codePath) {
		this.codePath = codePath;
	}
	/**
	 * @return the sqfdFilePath
	 */
	public String getSqfdFilePath() {
		return sqfdFilePath;
	}
	/**
	 * @param sqfdFilePath the sqfdFilePath to set
	 */
	public void setSqfdFilePath(String sqfdFilePath) {
		this.sqfdFilePath = sqfdFilePath;
	}
	/**
	 * @return the testCaseFilePath
	 */
	public String getTestCaseFilePath() {
		return testCaseFilePath;
	}
	/**
	 * @param testCaseFilePath the testCaseFilePath to set
	 */
	public void setTestCaseFilePath(String testCaseFilePath) {
		this.testCaseFilePath = testCaseFilePath;
	}
	/**
	 * @return the klasses
	 */
	public LinkedList<Klass> getKlasses() {
		return klasses;
	}
	/**
	 * @param klasses the klasses to set
	 */
	public void setKlasses(LinkedList<Klass> klasses) {
		this.klasses = klasses;
	}
	/**
	 * @return the requirements
	 */
	public List<Requirement> getRequirements() {
		return requirements;
	}
	/**
	 * @param requirements the requirements to set
	 */
	public void setRequirements(LinkedList<Requirement> requirements) {
		this.requirements = requirements;
	}
	/**
	 * @return the testCases
	 */
	public List<TestCase> getTestCases() {
		return testCases;
	}
	/**
	 * @param testCases the testCases to set
	 */
	public void setTestCases(LinkedList<TestCase> testCases) {
		this.testCases = testCases;
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
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
	 * @return the partial
	 */
	public long getPartial() {
		return partial;
	}


	/**
	 * @param partial the partial to set
	 */
	public void setPartial(long partial) {
		this.partial = partial;
	}
}
