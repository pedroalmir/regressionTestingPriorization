package com.pedroalmir.testPriorization.flow.antSystem.model.graph;

import java.util.ArrayList;
import java.util.List;

import com.pedroalmir.testPriorization.flow.antSystem.core.AntColonySystem;

/**
 * @author Pedro Almir
 *
 */
public class Graph {
	/**
	 * Número de formigas
	 */
	private int numberAnt;
	/**
	 * Número de vertices
	 */
	private int numberVertex;
	/**
	 * Matriz da heuristiva (i, j)
	 */
	private double heuristic[][];
	/**
	 * Lista de arestas
	 */
	List<Edge> edges = new ArrayList<Edge>();

	/**
	 * @param ants
	 * @param vertex
	 */
	public void positionAntsSamePlace(List<AntColonySystem> ants, Vertex vertex) {
		for (AntColonySystem ant : ants) {
			vertex.addAntVertex(ant);
		}
	}

	// Vai ter um metodo que vai percorrer todos os vertives procurando as formigas
	// e contruindo seus caminho isso por que tem problemas que as formigas sao distribuadas
	// pelo grafo
	public void positionAntsDiferentPlaces(List<AntColonySystem> ants) {

	}

	/**
	 * Distribui o feromanio nas Arestas
	 * O parametro indica se a distribuição vai ser igual [T0] a todos ou aleatória [0, T0]
	 * 
	 * @param edges
	 * @param param
	 * @param t0
	 */
	public void distribuitionPheromone(List<Edge> edges, int param, int t0) {
		if (param == 0) { // Distribuição uniforme
			for (Edge edge : edges) {
				edge.setPheromone(t0);
			}
		} else {

		}
	}

	/**
	 * @return the numberAnt
	 */
	public int getNumberAnt() {
		return numberAnt;
	}

	/**
	 * @param numberAnt the numberAnt to set
	 */
	public void setNumberAnt(int numberAnt) {
		this.numberAnt = numberAnt;
	}

	/**
	 * @return the numberVertex
	 */
	public int getNumberVertex() {
		return numberVertex;
	}

	/**
	 * @param numberVertex the numberVertex to set
	 */
	public void setNumberVertex(int numberVertex) {
		this.numberVertex = numberVertex;
	}

	/**
	 * @return the heuristic
	 */
	public double[][] getHeuristic() {
		return heuristic;
	}

	/**
	 * @param heuristic the heuristic to set
	 */
	public void setHeuristic(double[][] heuristic) {
		this.heuristic = heuristic;
	}

	/**
	 * @return the edges
	 */
	public List<Edge> getEdges() {
		return edges;
	}

	/**
	 * @param edges the edges to set
	 */
	public void setEdges(List<Edge> edges) {
		this.edges = edges;
	}

}
