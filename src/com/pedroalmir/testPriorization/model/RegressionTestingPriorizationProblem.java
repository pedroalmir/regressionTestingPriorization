/**
 * 
 */
package com.pedroalmir.testPriorization.model;

import java.util.ArrayList;
import java.util.Date;
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
	
	private List<Klass> klasses;
	private List<Requirement> requirements;
	private List<TestCase> testCases;
	
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
		this.klasses = new ArrayList<Klass>(classNumber);
		this.requirements = new ArrayList<Requirement>(requirementNumber);
		/* Same number of requirements */
		this.testCases = new ArrayList<TestCase>(requirementNumber);
		
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
	public List<Klass> getKlasses() {
		return klasses;
	}
	/**
	 * @param klasses the klasses to set
	 */
	public void setKlasses(List<Klass> klasses) {
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
	public void setRequirements(List<Requirement> requirements) {
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
	public void setTestCases(List<TestCase> testCases) {
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
}
