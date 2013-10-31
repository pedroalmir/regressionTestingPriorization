/**
 * 
 */
package com.pedroalmir.testPriorization.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Pedro Almir
 *
 */
public class Klass {
	private Long id;
	private String name;
	private String absolutePath;
	private String content;
	private int coupling;
	private Map<Requirement, Integer> correlation;
	private Map<TestCase, Double> coverage;
	private double relevance;
	private double relevanceNormalized;
	/* result of fuzzy */
	private double criticality;
	private double cyclomaticComplexity;
	private double cyclomaticComplexityNormalized;
	/**
	 * Default constructor 
	 */
	public Klass(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
		this.absolutePath = "Not used yet!";
		this.coupling = 0;
		this.correlation = new LinkedHashMap<Requirement, Integer>();
		this.coverage = new LinkedHashMap<TestCase, Double>();
		this.cyclomaticComplexity = 0.0;
	}
	
	public Klass(Long id, String name, double cyclomaticComplexity) {
		super();
		this.id = id;
		this.name = name;
		this.absolutePath = "Not used yet!";
		this.coupling = 0;
		this.correlation = new LinkedHashMap<Requirement, Integer>();
		this.coverage = new LinkedHashMap<TestCase, Double>();
		this.cyclomaticComplexity = cyclomaticComplexity;
	}
	
	/**
	 * @param id
	 * @param name
	 * @param absolutePath
	 * @param coupling
	 * @param correlation
	 */
	public Klass(Long id, String name, String absolutePath, int coupling, Map<Requirement, Integer> correlation) {
		super();
		this.id = id;
		this.name = name;
		this.absolutePath = absolutePath;
		this.coupling = coupling;
		this.correlation = correlation;
		this.cyclomaticComplexity = 0.0;
	}

	/**
	 * @param requirement
	 * @param value
	 */
	public void addCorrelation(Requirement requirement, Integer value){
		this.correlation.put(requirement, value);
	}
	
	/**
	 * @param testCase
	 * @param value
	 */
	public void addCoverageRelation(TestCase testCase, Double value){
		this.coverage.put(testCase, value);
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return the coupling
	 */
	public int getCoupling() {
		return coupling;
	}
	/**
	 * @param coupling the coupling to set
	 */
	public void setCoupling(int coupling) {
		this.coupling = coupling;
	}
	/**
	 * @return the correlation
	 */
	public Map<Requirement, Integer> getCorrelation() {
		return correlation;
	}
	/**
	 * @param correlation the correlation to set
	 */
	public void setCorrelation(Map<Requirement, Integer> correlation) {
		this.correlation = correlation;
	}

	/**
	 * @return the coverage
	 */
	public Map<TestCase, Double> getCoverage() {
		return coverage;
	}

	/**
	 * @param coverage the coverage to set
	 */
	public void setCoverage(Map<TestCase, Double> coverage) {
		this.coverage = coverage;
	}

	/**
	 * @return the relevance
	 */
	public double getRelevance() {
		return relevance;
	}

	/**
	 * @param relevance
	 */
	public void setRelevance(double relevance) {
		this.relevance = relevance;
	}

	/**
	 * @return the relevanceNormalized
	 */
	public double getRelevanceNormalized() {
		return relevanceNormalized;
	}

	/**
	 * @param relevanceNormalized
	 */
	public void setRelevanceNormalized(double relevanceNormalized) {
		this.relevanceNormalized = relevanceNormalized;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Klass [name=" + name + ", coupling=" + coupling + ", relevanceNormalized=" + relevanceNormalized
				+ ", criticality=" + criticality + "]";
	}

	/**
	 * @return the cyclomaticComplexity
	 */
	public double getCyclomaticComplexity() {
		return cyclomaticComplexity;
	}

	/**
	 * @param cyclomaticComplexity the cyclomaticComplexity to set
	 */
	public void setCyclomaticComplexity(double cyclomaticComplexity) {
		this.cyclomaticComplexity = cyclomaticComplexity;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Klass other = (Klass) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/**
	 * @return the cyclomaticComplexityNormalized
	 */
	public double getCyclomaticComplexityNormalized() {
		return cyclomaticComplexityNormalized;
	}

	/**
	 * @param cyclomaticComplexityNormalized the cyclomaticComplexityNormalized to set
	 */
	public void setCyclomaticComplexityNormalized(double cyclomaticComplexityNormalized) {
		this.cyclomaticComplexityNormalized = cyclomaticComplexityNormalized;
	}
}
