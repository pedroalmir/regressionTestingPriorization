package com.pedroalmir.testPriorization.flow.ant;





public class Vertex {
	int vertexId;   // N�mero que identifica o v�rtice
//	List<Ant> ants = new ArrayList<Ant>();
//// Adiciona fomiga ao v�rtice
//public void addAntVertex(Ant ant){
//	this.ants.add(ant);
//}
	double criticidade, tempo, pheromone;
	
	public Vertex(int vertexId, double criticidade, double tempo, double pheromone) {
		this.vertexId = vertexId;
		this.criticidade = criticidade;
		this.tempo = tempo;
		this.pheromone = pheromone;
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


}
