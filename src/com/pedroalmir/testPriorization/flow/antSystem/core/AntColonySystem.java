package com.pedroalmir.testPriorization.flow.antSystem.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.pedroalmir.testPriorization.flow.antSystem.model.AntHill;
import com.pedroalmir.testPriorization.flow.antSystem.model.graph.Edge;
import com.pedroalmir.testPriorization.flow.antSystem.model.graph.Vertex;

/**
 * @author Pedro Almir
 *
 */
public class AntColonySystem implements AntHill {
	/**
	 * Identificador da formiga
	 */
	private int antId;
	/**
	 * Posição atual da formiga no grado, ou seja o vertice que ela está
	 */
	private Vertex position;
	/**
	 * Lista tabu (cidades visitadas pela formiga - Vertices)
	 */
	private List<Edge> tabuList;
	/**
	 * Lista de vertices visitados
	 */
	private List<Vertex> vertexs;
	/**
	 * Quantidade de feromônio a ser depositado
	 */
	private double pheromoneDeposit;
	private double cargaMaxima;
	
	/**
	 * @param problem
	 */
	public AntColonySystem(double cargaMaxima) {
		this.tabuList = new ArrayList<Edge>();
		this.vertexs = new ArrayList<Vertex>();
		this.cargaMaxima = cargaMaxima;
	}

	/**
	 * Construção do caminho da solução, a partir do caminho inicial e de todas arestas do grafo.
	 * q0 é o parâmetro que equilibra o aproveitamento e a exploração
	 * 
	 * @param initial
	 * @param edges
	 * @param beta
	 * @param q0
	 * @param alfa
	 */
	public void buildPath(Vertex initial, List<Edge> edges, double beta, double q0, double alfa) {
		/* Lista de caminhos factíveis */
		List<Edge> edgeEighborFeasible;
		double probabilities[];
		int i, posicaoDoIntervalo;
		Random rand = new Random();
		double randomico;
		/* Nesse caso as probabilidades ficam ajustadas de 0 a 1. */
		double probabilitiesAjustadas[];
		/* Colocar posição formiga */
		edgeEighborFeasible = getEighborFeasible(edges);
		
		while (!edgeEighborFeasible.isEmpty()) {
			if (rand.nextDouble() <= -1) {
				double avaliacaoTranscisao = Double.MIN_VALUE;
				int numMelhorAresta = 0;
				int aux = 0;
				for (Edge edge : edgeEighborFeasible) {
					if (edge.getPheromone() * Math.pow(1 / edge.getDistance(), beta) > avaliacaoTranscisao) {
						avaliacaoTranscisao = edge.getPheromone() * Math.pow(1 / edge.getDistance(), beta);
						numMelhorAresta = aux;
					}
					aux += 1;
				}
				/* Just for debug */
				// System.out.println("Melhor aresta: "+ edgeEighborFeasible.get(numMelhorAresta).edgeId);
				/* Incluir o caminho escolhido na lista tabu */
				tabuList.add(edgeEighborFeasible.get(numMelhorAresta));
				
				if (!vertexs.contains(edgeEighborFeasible.get(numMelhorAresta).origin)){
					vertexs.add(edgeEighborFeasible.get(numMelhorAresta).origin);
				}
				if (!vertexs.contains(edgeEighborFeasible.get(numMelhorAresta).destiny)){
					vertexs.add(edgeEighborFeasible.get(numMelhorAresta).destiny);
				}
				
				/* Atualizar a posição da formiga */
				if (edgeEighborFeasible.get(numMelhorAresta).origin.getVertexId() == this.position.getVertexId()) {
					position = edgeEighborFeasible.get(numMelhorAresta).destiny;
				} else if (edgeEighborFeasible.get(numMelhorAresta).destiny.getVertexId() == this.position.getVertexId()) {
					position = edgeEighborFeasible.get(numMelhorAresta).origin;
				}
				edgeEighborFeasible = getEighborFeasible(edges);
			} else {
				i = 0;
				probabilities = new double[edges.size()];
				probabilitiesAjustadas = new double[edges.size()];
				for (Edge edge : edgeEighborFeasible) {
					/* Para cada aresta calcula a probabilidade */
					probabilities[i] = transitionProbability(edge, edgeEighborFeasible, beta, alfa);
					i += 1;
				}
				/* De acordo com as probabilidades ajutadas ex: [0.5, 0.2, 0.3] --> normal */
				/* Escolher qual caminho seguir [0.5, 0.7, 1.0] --> ajustadas (distribuidas de 0 à 1). */
				probabilitiesAjustadas[0] = probabilities[0];
				for (int j = 1; j < probabilities.length; j += 1) {
					if (j == probabilities.length - 1) {
						probabilitiesAjustadas[j] = 1.0;
					} else {
						probabilitiesAjustadas[j] = probabilitiesAjustadas[j - 1] + probabilities[j];
					}
				}
				
				/* Escolhendo agora */
				randomico = rand.nextDouble();
				posicaoDoIntervalo = 0;
				for (int j = 0; j < probabilitiesAjustadas.length; j += 1) {
					if (!(randomico > probabilitiesAjustadas[j])) {
						posicaoDoIntervalo = j;
						j = probabilitiesAjustadas.length;
					}
				}
				/* Incluir o caminho escolhido na lista tabu */
				tabuList.add(edgeEighborFeasible.get(posicaoDoIntervalo));
				
				if (!vertexs.contains(edgeEighborFeasible.get(posicaoDoIntervalo).origin)){
					vertexs.add(edgeEighborFeasible.get(posicaoDoIntervalo).origin);
				}
				if (!vertexs.contains(edgeEighborFeasible.get(posicaoDoIntervalo).destiny)){
					vertexs.add(edgeEighborFeasible.get(posicaoDoIntervalo).destiny);
				}
				
				/* Atualizar a posição da formiga */
				if (edgeEighborFeasible.get(posicaoDoIntervalo).origin.getVertexId() == this.position.getVertexId()) {
					position = edgeEighborFeasible.get(posicaoDoIntervalo).destiny;
				} else if (edgeEighborFeasible.get(posicaoDoIntervalo).destiny.getVertexId() == this.position.getVertexId()) {
					position = edgeEighborFeasible.get(posicaoDoIntervalo).origin;
				}
				edgeEighborFeasible = getEighborFeasible(edges);
			}
		}
		
		/* Just for debug */
		// Mostrando o caminho que foi registrado na lista tabu
		// for (Edge edge: tabuList){
		// System.out.println("Lista tabu: " + edge.edgeId);
		// }
	}

	/**
	 * Método que retorna o caminho (arestas) percorrido pela formiga
	 */
	public void pathEdges() {
		for (Edge edge : tabuList) {
			System.out.println("Aresta: " + edge.edgeId);
		}
	}

	/**
	 * Método que retorna as cidades (vértices) percorridas pela formiga
	 */
	public void pathVertex() {
		System.out.print("Vertexs: ");
		for (Vertex vertex : vertexs) {
			System.out.print("  " + vertex.getVertexId());
		}
	}

	/**
	 * @return
	 */
	public List<Vertex> returnPathVertexs() {
		List<Vertex> vertexsClone = new ArrayList<Vertex>();
		for (Vertex vertex : this.vertexs) {
			vertexsClone.add(vertex);
		}
		return vertexsClone;
	}

	/**
	 * Metodo que retorna o comprimento do caminho percorrido pela formiga
	 * 
	 * @return
	 */
	public double evaluation() {
		double evaluation = 0;
		for (Edge edge : tabuList) {
			evaluation += edge.distance;
		}
		return evaluation;
	}

	/**
	 * @return
	 */
	public double custo() {
		double custo = 0;
		for (Vertex vertex : vertexs) {
			custo += vertex.getCriticidade();
		}
		return custo;
	}

	/**
	 * Metodo que reduz a concentração de feromônio em todas as arestas do grafo.
	 * @param edges
	 * @param rateOfEvaporation
	 */
	public static void evaporationPheromone(List<Edge> edges, double rateOfEvaporation) {
		for (Edge edge : edges) {
			edge.pheromone = (1 - rateOfEvaporation) * edge.pheromone;
		}
	}

	/**
	 * Metodo que calcula o feromônio a ser depositado e deposita de acordo com a regra do AntCycleAS.
	 * 
	 * @param constantQ
	 */
	public void calculeDepositPheromoneAntCycle(double constantQ) {
		pheromoneDeposit = constantQ / evaluation();
	}

	/**
	 * 
	 */
	public void depositPheromoneAntCycle() {
		for (Edge edge : tabuList) {
			edge.pheromone += pheromoneDeposit;
		}
	}

	/**
	 * Metodo que calcula o feromônio a ser depositado e deposita de acordo com a regra do AntDensityAS
	 * .
	 * @param constantQ
	 */
	public void deposityPheromoneAntDensity(double constantQ) {
		for (Edge edge : tabuList) {
			edge.pheromone += constantQ;
		}
	}

	/**
	 * Metodo que calcula o feromônio a ser depositado e deposita de acordo com a regra do AntQuantityAS.
	 * 
	 * @param constantQ
	 */
	public void deposityPheromoneAntQuantity(double constantQ) {
		for (Edge edge : tabuList) {
			edge.pheromone += constantQ / edge.getDistance();
		}
	}

	/**
	 * Vertices vizinhos
	 * 
	 * @param edges
	 * @param vertex
	 * @return
	 */
	public List<Vertex> vetexNeigbor(List<Edge> edges, Vertex vertex) {
		List<Vertex> neighbor = new ArrayList<Vertex>();
		// verifica em quais arestas o vertex esta presente e retorna a lista dos vizihos
		for (Edge edge : edges) {
			// Se o vértice está em algum extremo da aresta e adiciona o v�rtice da outra ponta lista de vizinho.
			if (vertex.getVertexId() == edge.getOrigin().getVertexId()) {
				neighbor.add(edge.getDestiny());
				// System.out.println("Vertex origin: " + edge.getOrigin().getVertexId());
				// System.out.println("Vertex destiny: " + edge.getDestiny().getVertexId());
			} else if (vertex.getVertexId() == edge.getDestiny().getVertexId()) {
				// System.out.println("Vertex origin: " + edge.getOrigin().getVertexId());
				// System.out.println("Vertex destiny: " + edge.getDestiny().getVertexId());
				neighbor.add(edge.getOrigin());
			}
		}

		return neighbor;
	}

	/**
	 * Arestas vizinhas
	 * 
	 * @param edges
	 * @param vertex
	 * @return
	 */
	public List<Edge> edgeNeighbor(List<Edge> edges, Vertex vertex) {
		List<Edge> neighbor = new ArrayList<Edge>();
		// verifica em quais arestas o vertex esta presente e retorna a lista dos vizihos
		for (Edge edge : edges) {
			// Se o vértice está em algum extremo da aresta e adiciona o vértice da outra ponta lista de vizinho.
			if (vertex.getVertexId() == edge.getOrigin().getVertexId()) {
				neighbor.add(edge);
				// System.out.println("Vertex origin: " + edge.getOrigin().getVertexId());
				// System.out.println("Vertex destiny: " + edge.getDestiny().getVertexId());
			} else if (vertex.getVertexId() == edge.getDestiny().getVertexId()) {
				// System.out.println("Vertex origin: " + edge.getOrigin().getVertexId());
				// System.out.println("Vertex destiny: " + edge.getDestiny().getVertexId());
				neighbor.add(edge);
			}
		}
		return neighbor;
	}

	/**
	 * Retorna as arestas factiveis de exploração (as que não estam na listaTabu).
	 * @param edges
	 * @return
	 */
	public List<Edge> getEighborFeasible(List<Edge> edges) {
		List<Edge> edgeNeighbor = edgeNeighbor(edges, this.position);
		List<Edge> edgeFeasible = new ArrayList<Edge>();
		for (Edge edge : edgeNeighbor) {
			if (edge.getOrigin().getVertexId() == position.getVertexId()) {
				// Se vértice oposto da aresta não tiver na lista tabu essa aresta pode ser explorada até o vértice oposto.
				if (!this.tabuList.contains(edge) && !this.vertexs.contains(edge.destiny)) {
					if (this.verifyLimite(edge.getDestiny())) {
						edgeFeasible.add(edge);
					}
				}
			} else if (edge.getDestiny().getVertexId() == position.getVertexId()) {
				// Se vértice oposto da aresta não tiver na lista tabu essa aresta pode ser explorada até o vértice oposto.
				if (!this.tabuList.contains(edge) && !this.vertexs.contains(edge.origin)) {
					if (this.verifyLimite(edge.getOrigin())) {
						edgeFeasible.add(edge);
					}
				}
			}

		}
		return edgeFeasible;
	}

	/**
	 * @param vertexDestiny
	 * @return
	 */
	private boolean verifyLimite(Vertex vertexDestiny) {
		double tempo = tempoCarga();
		if ((tempo + vertexDestiny.getTempo()) > this.cargaMaxima) {
			return false;
		} else
			return true;
	}

	/* (non-Javadoc)
	 * @see com.pedroalmir.ant.model.AntHill#transitionProbability(com.pedroalmir.ant.model.graph.Edge, java.util.List, double, double)
	 */
	@Override
	public double transitionProbability(Edge edge, List<Edge> edgeEighborFeasible, double beta, double alfa) {

		double somatorio = 0;
		for (Edge edgeEighbor : edgeEighborFeasible) {
			if (edgeEighbor.origin.getVertexId() == this.position.getVertexId()) {
				somatorio += (Math.pow(edgeEighbor.destiny.getPheromone(), alfa) * Math.pow(edgeEighbor.destiny.getCriticidade() / edgeEighbor.destiny.getTempo(), beta));
			} else if (edgeEighbor.destiny.getVertexId() == this.position.getVertexId()) {
				somatorio += (Math.pow(edgeEighbor.origin.getPheromone(), alfa) * Math.pow(edgeEighbor.origin.getCriticidade() / edgeEighbor.destiny.getTempo(), beta));
			}
		}
		if (somatorio == 0) {
			return 0.0;
		} else {
			if (edge.origin.getVertexId() == position.getVertexId()) {
				return (Math.pow(edge.destiny.getPheromone(), alfa) * Math.pow(edge.destiny.getCriticidade() / edge.destiny.getTempo(), beta) / somatorio);
			} else {
				return (Math.pow(edge.origin.getPheromone(), alfa) * Math.pow(edge.origin.getCriticidade() / edge.destiny.getTempo(), beta) / somatorio);
			}
		}
	}

	/**
	 * @param p2
	 */
	public void deposityLocalPheromoneACS(double p2) {
		for (Edge edge : tabuList) {
			edge.pheromone = (1 - p2) * edge.pheromone + p2 * edge.pheromoneInicial;
		}
	}

	public static void deposityGlobalPheromone(double p1, double Q, List<AntColonySystem> ants, List<Vertex> vertexs) {
		for (Vertex vertex : vertexs) {
			// edge.pheromone = (1 - p1)*edge.pheromone + p1*edge.pheromoneInicial;
			double somatorioDeposito = 0;

			for (AntColonySystem ant : ants) {
				if (ant.vertexs.contains(vertex)) {
					somatorioDeposito += ((vertex.getTempo()) * Q) / ant.tempoCarga();
				}
			}
			// System.out.println("Deposito:" + somatorioDeposito);
			vertex.setPheromone(vertex.getPheromone() + p1 * vertex.getPheromone() + somatorioDeposito);
		}
	}

	public double tempoCarga() { // retorna a carga ou seja
		double cargatempo = 0;
		for (Vertex vertex : vertexs) {
			// edge.pheromone = (1 - p1)*edge.pheromone + p1*edge.pheromoneInicial;
			cargatempo += vertex.getTempo();
		}
		return cargatempo;
	}

	/**
	 * @return the antId
	 */
	public int getAntId() {
		return antId;
	}

	/**
	 * @param antId the antId to set
	 */
	public void setAntId(int antId) {
		this.antId = antId;
	}

	/**
	 * @return the position
	 */
	public Vertex getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(Vertex position) {
		this.position = position;
	}

	/**
	 * @return the tabuList
	 */
	public List<Edge> getTabuList() {
		return tabuList;
	}

	/**
	 * @param tabuList the tabuList to set
	 */
	public void setTabuList(List<Edge> tabuList) {
		this.tabuList = tabuList;
	}

	/**
	 * @return the vertexs
	 */
	public List<Vertex> getVertexs() {
		return vertexs;
	}

	/**
	 * @param vertexs the vertexs to set
	 */
	public void setVertexs(List<Vertex> vertexs) {
		this.vertexs = vertexs;
	}

	/**
	 * @return the pheromoneDeposit
	 */
	public double getPheromoneDeposit() {
		return pheromoneDeposit;
	}

	/**
	 * @param pheromoneDeposit the pheromoneDeposit to set
	 */
	public void setPheromoneDeposit(double pheromoneDeposit) {
		this.pheromoneDeposit = pheromoneDeposit;
	}

}
