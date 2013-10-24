package com.pedroalmir.testPriorization.flow.antSystem.model.graph;

import com.pedroalmir.testPriorization.flow.antSystem.model.graph.base.Link;

/**
 * 
 * @author Pedro Almir
 * 
 */
public class Edge implements Link {
	
	public int edgeId;
	/**
	 * Origem (Ponto de partida da formiga)
	 */
	public Vertex origin;
	/**
	 * Destino (Vertice de chegada da formiga)
	 */
	public Vertex destiny;
	/**
	 * Intensidade de feromonio na aresta ("Edge")
	 */
	public double pheromone;
	/**
	 * Custo ou distância
	 */
	public double distance;
	/**
	 * Essa informação é usada apenas no ACS, pois o deposito usa o valor
	 * do primeiro deposito de feromônio.
	 */
	public double pheromoneInicial;

	/**
	 * @param vertex1
	 * @param vertex2
	 */
	public Edge(Vertex vertex1, Vertex vertex2) {
		this.origin = vertex1;
		this.destiny = vertex2;
	}

	/**
	 * @param origin
	 * @param destiny
	 * @param edgeId
	 */
	public Edge(Vertex origin, Vertex destiny, int edgeId) {
		this.origin = origin;
		this.destiny = destiny;
		this.edgeId = edgeId;
	}

	/**
	 * Este contrutor, foi modificado para operar de acordo com o ACS
	 * 
	 * @param vertex1
	 * @param vertex2
	 * @param edgeId
	 * @param pheromone
	 * @param distance
	 */
	public Edge(Vertex vertex1, Vertex vertex2, int edgeId, double pheromone, double distance) {
		this(vertex1, vertex2, edgeId);
		this.pheromone = pheromone;
		this.pheromoneInicial = pheromone;
		this.distance = distance;
	}

	public int getEdgeId() {
		return edgeId;
	}
	
	public double getPheromone() {
		return pheromone;
	}

	@Override
	public double transitionProbability() {
		return 0;
	}

	@Override
	public double evaporation() {
		return 0;
	}

	public void setPheromone(double pheromone) {
		this.pheromone = pheromone;
	}

	public Vertex getOrigin() {
		return origin;
	}

	public Vertex getDestiny() {
		return destiny;
	}

	public double getDistance() {
		return distance;
	}
}
