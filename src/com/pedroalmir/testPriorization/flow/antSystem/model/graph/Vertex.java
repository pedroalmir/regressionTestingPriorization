package com.pedroalmir.testPriorization.flow.antSystem.model.graph;

import com.pedroalmir.testPriorization.flow.antSystem.core.AntColonySystem;

/**
 * @author Pedro Almir
 *
 */
public class Vertex {
	/**
	 * Número que identifica o vértica
	 */
	private int vertexId;
	/**
	 * 
	 */
	private double criticidade, tempo, pheromone;

	/**
	 * @param vertexId
	 * @param criticidade
	 * @param tempo
	 * @param pheromone
	 */
	public Vertex(int vertexId, double criticidade, double tempo, double pheromone) {
		this.vertexId = vertexId;
		this.criticidade = criticidade;
		this.tempo = tempo;
		this.pheromone = pheromone;
	}

	/**
	 * @return
	 */
	public int getVertexId() {
		return vertexId;
	}

	/**
	 * @return
	 */
	public String getVertexIdToS() {
		return String.valueOf(vertexId);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Vertex [vertexId=" + vertexId + ", criticidade=" + criticidade + ", tempo=" + tempo + "]";
	}

	/**
	 * @param ant
	 */
	public void addAntVertex(AntColonySystem ant) {

	}

	/**
	 * @return
	 */
	public double getPheromone() {
		return pheromone;
	}

	/**
	 * @return the criticidade
	 */
	public double getCriticidade() {
		return criticidade;
	}

	/**
	 * @param criticidade the criticidade to set
	 */
	public void setCriticidade(double criticidade) {
		this.criticidade = criticidade;
	}

	/**
	 * @return the tempo
	 */
	public double getTempo() {
		return tempo;
	}

	/**
	 * @param tempo the tempo to set
	 */
	public void setTempo(double tempo) {
		this.tempo = tempo;
	}

	/**
	 * @param vertexId the vertexId to set
	 */
	public void setVertexId(int vertexId) {
		this.vertexId = vertexId;
	}

	/**
	 * @param pheromone the pheromone to set
	 */
	public void setPheromone(double pheromone) {
		this.pheromone = pheromone;
	}

}
