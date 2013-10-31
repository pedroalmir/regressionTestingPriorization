package com.pedroalmir.testPriorization.flow.ant;

import com.pedroalmir.testPriorization.model.TestCase;





public class Vertex {
	int vertexId;
	double criticidade, tempo, pheromone;
	private TestCase testCase;
	
	public Vertex(int vertexId, double criticidade, double tempo, double pheromone, TestCase testCase) {
		this.vertexId = vertexId;
		this.criticidade = criticidade;
		this.tempo = tempo;
		this.pheromone = pheromone;
		this.testCase = testCase;
	}
	
	public int getVertexId() {
		return vertexId;
	}

	public String getVertexIdToS() {
		return String.valueOf(vertexId);
	}
	
	@Override
	public String toString() {
		return "Vertex [vertexId=" + vertexId + "]";
	}

	public void addAntVertex(AntColonySystem ant) {
		// TODO Auto-generated method stub
		
	}

	public double getPheromone() {
		// TODO Auto-generated method stub
		return pheromone;
	}

	/**
	 * @return the testCase
	 */
	public TestCase getTestCase() {
		return testCase;
	}

	/**
	 * @param testCase the testCase to set
	 */
	public void setTestCase(TestCase testCase) {
		this.testCase = testCase;
	}


}
