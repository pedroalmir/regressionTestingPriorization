package com.pedroalmir.testPriorization.flow.ant;



import java.util.ArrayList;
import java.util.List;

public class Graph {
	int numberAnt;          // N�mero de formigas
	int numberVertex;       // N�mero de vertice
//	double pheromone[][];   // Matriz dos fer�monio para os link (i, j)
	double heuristic[][];   // Matrix Da Heur�stica d(i, j)
	List<Edge> edges = new ArrayList<Edge>(); // Lista de arestas
	
	public void positionAntsSamePlace(List<AntColonySystem> ants, Vertex vertex){
		for (AntColonySystem ant : ants){
			vertex.addAntVertex(ant);
		}
	} 
	// Vai ter um metodo que vai percorrer todos os vertives procurando as formigas 
	// e contruindo seus caminho isso por que tem problemas que as formigas s�o distribu�das 
	// pelo grafo

	public void positionAntsDiferentPlaces(List<AntColonySystem> ants){
	   	
		
	} 
	// Distribui o ferom�nio nas Arestas
	// O param indica se a distribui��o vai ser igual [T0] a todos ou aleat�ria [0, T0]
	public void distribuitionPheromone(List<Edge> edges, int param, int t0){
		if (param == 0){ // Distribui��o uniforme
			for (Edge edge: edges){
				edge.setPheromone(t0);
			}
		} else {
			
		}
	}

}
