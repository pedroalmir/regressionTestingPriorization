package com.pedroalmir.testPriorization.flow.ant;


/**
 * 
 * @author Jonathas
 * 
 */
public class Edge implements Link{
	public int edgeId;
	public Vertex origin;    // Origem (Ponto de partida da fomiga)
	public Vertex destiny;   // Destino (Vertice de chegada da formiga)
	public double pheromone; // Intesidade de ferom�nio na Aresta ("Edge").
    public double distance;      // Custo ou distancia	
	public double pheromoneInicial; // Essa informa��o � usada apenas no ACS, pois o deposito usa o valor do primeiro deposito de ferom�nio
	public double getPheromone() {
		return pheromone;
	}

	public Edge(Vertex vertex1, Vertex vertex2) {
		// TODO Auto-generated constructor stub
		this.origin = vertex1;
		this.destiny = vertex2;
	}
	
	
	public Edge(Vertex origin, Vertex destiny, int edgeId) {
		this.origin = origin;
		this.destiny = destiny;
		this.edgeId = edgeId;
	}
	// Este contrutor, foi modificado para operar de acordo com o ACS
	public Edge(Vertex vertex1, Vertex vertex2, int edgeId, double pheromone, double distance) {
		this(vertex1, vertex2, edgeId);
		this.pheromone = pheromone;
		this.pheromoneInicial = pheromone;
		this.distance = distance;
	}

	public int getEdgeId() {
		return edgeId;
	}
	
	@Override
	public double transitionProbability() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public double evaporation() {
		// TODO Auto-generated method stub
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
