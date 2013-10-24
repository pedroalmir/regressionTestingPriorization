/**
 * 
 */
package com.pedroalmir.testPriorization.model;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Pedro Almir
 *
 */
public class TestCase {
	private Long id;
	private String testName;
	private String absolutePath;
	private Date executionTime;
	private Double time;
	private double criticality;
	private Map<Klass, Double> coverage;
	
	/**
	 * @param id
	 * @param testName
	 * @param time
	 */
	public TestCase(Long id, String testName, Double time) {
		super();
		this.id = id;
		this.testName = testName;
		this.absolutePath = "Not used yet!!!";
		this.time = time;
		this.coverage = new LinkedHashMap<Klass, Double>();
	}
	
	/**
	 * @param id
	 * @param testName
	 * @param absolutePath
	 * @param executionTime
	 * @param importance
	 */
	public TestCase(Long id, String testName, String absolutePath, Date executionTime, int importance) {
		super();
		this.id = id;
		this.testName = testName;
		this.absolutePath = absolutePath;
		this.executionTime = executionTime;
		this.criticality = importance;
		this.coverage = new LinkedHashMap<Klass, Double>();
	}
	
	
	
	/**
	 * @param testCase
	 */
	public TestCase(TestCase testCase) {
		super();
		this.id = testCase.getId();
		this.testName = testCase.getTestName();
		this.absolutePath = testCase.getAbsolutePath();
		this.executionTime = testCase.getExecutionTime();
		this.time = testCase.getTime();
		
		this.criticality = testCase.getCriticality();
		this.coverage = new LinkedHashMap<Klass, Double>(testCase.getCoverage());
	}


	/**
	 * @param klass
	 * @param value
	 */
	public void addCoverageRelation(Klass klass, Double value){
		this.coverage.put(klass, value);
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
	 * @return the testName
	 */
	public String getTestName() {
		return testName;
	}
	/**
	 * @param testName the testName to set
	 */
	public void setTestName(String testName) {
		this.testName = testName;
	}
	/**
	 * @return the absolutePath
	 */
	public String getAbsolutePath() {
		return absolutePath;
	}
	/**
	 * @param absolutePath the absolutePath to set
	 */
	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}
	/**
	 * @return the executionTime
	 */
	public Date getExecutionTime() {
		return executionTime;
	}
	/**
	 * @param executionTime the executionTime to set
	 */
	public void setExecutionTime(Date executionTime) {
		this.executionTime = executionTime;
	}
	/**
	 * @return the importance
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
	 * @return the time
	 */
	public Double getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(Double time) {
		this.time = time;
	}

	/**
	 * @return the coverage
	 */
	public Map<Klass, Double> getCoverage() {
		return coverage;
	}

	/**
	 * @param coverage the coverage to set
	 */
	public void setCoverage(Map<Klass, Double> coverage) {
		this.coverage = coverage;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TestCase [testName=" + testName + ", time=" + time + ", criticality=" + criticality + "]";
	}
}
