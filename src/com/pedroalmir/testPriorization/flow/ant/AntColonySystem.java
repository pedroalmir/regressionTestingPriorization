package com.pedroalmir.testPriorization.flow.ant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AntColonySystem implements Anthill {
	int antId; // Identificador da formiga
	List<Edge> tabuList = new ArrayList<Edge>(); // Lista tabu (cidades visitadas pela formiga - V�rtices)
	Vertex position; // Posi��o atual da formiga no grafo, ou seja o vertice que ela est�
	List<Vertex> vertexs = new ArrayList<Vertex>(); // Lista de vertices visitados.

	static double cargaMaxima = 800;//12.6828;// 12.0753333;//13.98066667; //4.558666667;//.9492;//= 14.237333333333333333333;

	double pheromoneDeposit; // Quantidade de ferom�nio a ser depositado

	// Constru��o do caminho da solu��o do caminho, apartir do caminho inicial e de todas arestas do grafo. q0 � o
	// par�metro
	// que equilibra o apeoveitamento e a explora��o
	public void buildPath(Vertex initial, List<Edge> edges, double beta, double q0, double alfa) {
		List<Edge> edgeEighborFeasible; // Lista de caminhos fact�veis
		double probabilities[];
		int i;
		int posicaoDoIntervalo;
		Random rand = new Random();
		double randomico;
		double probabilitiesAjustadas[]; // Nesse caso as
											// probabilidades ficam
											// ajustadas de 0 a 1.
		edgeEighborFeasible = getEighborFeasible(edges); // colocar posicao formiga
		while (!edgeEighborFeasible.isEmpty()) {
			if (rand.nextDouble() <= -1) {// q0){ // pseudo-random-proportional
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
				// System.out.println("Melhor aresta: "+ edgeEighborFeasible.get(numMelhorAresta).edgeId);
				// Incluir o caminho escolhido na lista tabu, e os vertices dala na
				tabuList.add(edgeEighborFeasible.get(numMelhorAresta));
				/*
				 * / lista vertexs
				 * if (edgeEighborFeasible.get(numMelhorAresta).origin.vertexId == position.vertexId){
				 * // Se a posi��o atual � igual ao de origem da proxima aresta a ser percorrida ent�o o vertice de
				 * origem � adicionado
				 * vertexs.add(edgeEighborFeasible.get(numMelhorAresta).origin);
				 * } else { // Sen�o o v�rtice de destino � adicionado
				 * vertexs.add(edgeEighborFeasible.get(numMelhorAresta).destiny);
				 * }
				 */
				if (!vertexs.contains(edgeEighborFeasible.get(numMelhorAresta).origin))
					vertexs.add(edgeEighborFeasible.get(numMelhorAresta).origin);
				if (!vertexs.contains(edgeEighborFeasible.get(numMelhorAresta).destiny))
					vertexs.add(edgeEighborFeasible.get(numMelhorAresta).destiny);

				// / Atualizar a posi��o da formiga
				if (edgeEighborFeasible.get(numMelhorAresta).origin.vertexId == position.vertexId) {
					position = edgeEighborFeasible.get(numMelhorAresta).destiny;
				} else if (edgeEighborFeasible.get(numMelhorAresta).destiny.vertexId == position.vertexId) {
					position = edgeEighborFeasible.get(numMelhorAresta).origin;
				}
				edgeEighborFeasible = getEighborFeasible(edges); // colocar posicao formiga no m�todo
				// if (edgeEighborFeasible.isEmpty()) {vertexs.add(position);} // Adiciona o �ltimo v�rtice na lista de
				// v�rtices visitados

			} else {

				i = 0;
				probabilities = new double[edges.size()];
				probabilitiesAjustadas = new double[edges.size()];
				for (Edge edge : edgeEighborFeasible) { // Para cada aresta calcura
														// a probabilidade
					probabilities[i] = transitionProbability(edge, edgeEighborFeasible, beta, alfa);
					i += 1;
				}
				// De acordo com as probabilidades ajutadas ex: [0.5, 0.2, 0.3] --> normal
				// escolher qual caminho seguir [0.5, 0.7, 1.0] --> ajustadas (distribuidas de 0 � 1).
				probabilitiesAjustadas[0] = probabilities[0];
				for (int j = 1; j < probabilities.length; j += 1) {
					if (j == probabilities.length - 1) {
						probabilitiesAjustadas[j] = 1.0;
					} else {
						probabilitiesAjustadas[j] = probabilitiesAjustadas[j - 1] + probabilities[j];
					}
				}
				// Escolhendo agora
				randomico = rand.nextDouble();
				posicaoDoIntervalo = 0;
				for (int j = 0; j < probabilitiesAjustadas.length; j += 1) {
					if (!(randomico > probabilitiesAjustadas[j])) {
						posicaoDoIntervalo = j;
						j = probabilitiesAjustadas.length;
					}
				}// Incluir o caminho escolhido na lista tabu, e os vertices dala na
					// System.out.println("Posicoa do int: " + posicaoDoIntervalo);
				tabuList.add(edgeEighborFeasible.get(posicaoDoIntervalo));
				// lista vertexs
				// if (edgeEighborFeasible.get(posicaoDoIntervalo).origin.vertexId == position.vertexId){
				// // Se a posi��o atual � igual ao de origem da proxima aresta a ser percorrida ent�o o vertice de
				// origem � adicionado
				// vertexs.add(edgeEighborFeasible.get(posicaoDoIntervalo).origin);
				// } else { // Sen�o o v�rtice de destino � adicionado
				// vertexs.add(edgeEighborFeasible.get(posicaoDoIntervalo).destiny);
				// }
				if (!vertexs.contains(edgeEighborFeasible.get(posicaoDoIntervalo).origin))
					vertexs.add(edgeEighborFeasible.get(posicaoDoIntervalo).origin);
				if (!vertexs.contains(edgeEighborFeasible.get(posicaoDoIntervalo).destiny))
					vertexs.add(edgeEighborFeasible.get(posicaoDoIntervalo).destiny);

				// Atualizar a posi��o da formiga
				if (edgeEighborFeasible.get(posicaoDoIntervalo).origin.vertexId == position.vertexId) {
					position = edgeEighborFeasible.get(posicaoDoIntervalo).destiny;
				} else if (edgeEighborFeasible.get(posicaoDoIntervalo).destiny.vertexId == position.vertexId) {
					position = edgeEighborFeasible.get(posicaoDoIntervalo).origin;
				}
				edgeEighborFeasible = getEighborFeasible(edges); // colocar posicao formiga no m�todo
				// if (edgeEighborFeasible.isEmpty()) {vertexs.add(position);} // Adiciona o �ltimo v�rtice na lista de
				// v�rtices visitados

			}
		}

		// Mostrando o caminho que foi registrado na lista tabu
		// for (Edge edge: tabuList){
		// System.out.println("Lista tabu: " + edge.edgeId);
		// }
	}

	// M�todo que retorna o caminho (arestas) percorrido pela formiga
	public void pathEdges() {
		for (Edge edge : tabuList) {
			System.out.println("Aresta: " + edge.edgeId);
		}
	}

	// M�todo que retorna as cidades (v�rtices) percorridas pela formiga
	public void pathVertex() {
		System.out.print("Vertexs: ");
		for (Vertex vertex : vertexs) {
			System.out.print("  " + vertex.vertexId);
		}
	}

	public List<Vertex> returnPathVertexs() {
		List<Vertex> vertexsClone = new ArrayList<Vertex>();
		for (Vertex vertex : this.vertexs) {
			vertexsClone.add(vertex);
		}
		return vertexsClone;
	}

	// M�todo que retorna o comprimento do caminho percorrido pela formiga
	public double evaluation() {
		double evaluation = 0;
		for (Edge edge : tabuList) {
			evaluation += edge.distance;
		}
		return evaluation;
	}

	public double custo() {
		double custo = 0;
		for (Vertex vertex : vertexs) {
			custo += vertex.criticidade;
		}
		return custo;
	}

	// Metodo que reduz a concentra��o de ferom�nio em todas as arestas do grafo.
	public static void evaporationPheromone(List<Edge> edges, double rateOfEvaporation) {
		for (Edge edge : edges) {
			edge.pheromone = (1 - rateOfEvaporation) * edge.pheromone;
		}
	}

	// Metodo que calcula o ferom�nio a ser depositado e deposita de acordo com a regra do AntCycleAS.
	public void calculeDepositPheromoneAntCycle(double constantQ) {
		pheromoneDeposit = constantQ / evaluation();
	}

	public void depositPheromoneAntCycle() {
		for (Edge edge : tabuList) {
			edge.pheromone += pheromoneDeposit;
		}
	}

	// Metodo que calcula o ferom�nio a ser depositado e deposita de acordo com a regra do AntDensityAS.
	public void deposityPheromoneAntDensity(double constantQ) {
		for (Edge edge : tabuList) {
			edge.pheromone += constantQ;
		}
	}

	// Metodo que calcula o ferom�nio a ser depositado e deposita de acordo com a regra do AntQuantityAS.
	public void deposityPheromoneAntQuantity(double constantQ) {
		for (Edge edge : tabuList) {
			edge.pheromone += constantQ / edge.getDistance();
		}
	}

	// Vertices vizinhos
	public List<Vertex> vetexNeigbor(List<Edge> edges, Vertex vertex) {
		List<Vertex> neighbor = new ArrayList<Vertex>();
		// verifica em quais arestas o vertex esta presente e retorna a lista dos vizihos
		for (Edge edge : edges) {
			// Se o v�rtice est� em algum extremo da aresta e adiciona o v�rtice da outra ponta lista de vizinho.
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

	// Arestas vizinhas
	public List<Edge> edgeNeighbor(List<Edge> edges, Vertex vertex) {
		List<Edge> neighbor = new ArrayList<Edge>();
		// verifica em quais arestas o vertex esta presente e retorna a lista
		// dos vizihos
		for (Edge edge : edges) {
			// Se o v�rtice est� em algum extremo da aresta e adiciona o v�rtice
			// da outra ponta lista de vizinho.
			if (vertex.getVertexId() == edge.getOrigin().getVertexId()) {
				neighbor.add(edge);
				// System.out.println("Vertex origin: " +
				// edge.getOrigin().getVertexId());
				// System.out.println("Vertex destiny: " +
				// edge.getDestiny().getVertexId());
			} else if (vertex.getVertexId() == edge.getDestiny().getVertexId()) {
				// System.out.println("Vertex origin: " +
				// edge.getOrigin().getVertexId());
				// System.out.println("Vertex destiny: " +
				// edge.getDestiny().getVertexId());
				neighbor.add(edge);
			}
		}
		return neighbor;
	}

	// Retornas as arestas f�ctiveis de explora��o (as que n�o estam na listaTabu).
	public List<Edge> getEighborFeasible(List<Edge> edges) {
		List<Edge> edgeNeighbor = edgeNeighbor(edges, this.position);
		List<Edge> edgeFeasible = new ArrayList<Edge>();
		for (Edge edge : edgeNeighbor) {
			if (edge.getOrigin().getVertexId() == position.getVertexId()) {
				// Se v�rtice oposto da aresta n�o tiver na lista tabu essa aresta pode ser explorada at� o v�rtice
				// oposto.
				if (!this.tabuList.contains(edge) && !this.vertexs.contains(edge.destiny)) {
					if (this.verifyLimite(edge.getDestiny())) {
						edgeFeasible.add(edge);
						// System.out.println("auqi1");
					}
					// System.out.println("auqi1");
				}
			} else if (edge.getDestiny().getVertexId() == position.getVertexId()) {
				// Se v�rtice oposto da aresta n�o tiver na lista tabu essa aresta pode ser explorada at� o v�rtice
				// oposto.
				if (!this.tabuList.contains(edge) && !this.vertexs.contains(edge.origin)) {
					if (this.verifyLimite(edge.getOrigin())) {
						edgeFeasible.add(edge);
						// System.out.println("auqi2");
					}
					// System.out.println("auqi2");
				}
			}

		}
		return edgeFeasible;
	}

	private boolean verifyLimite(Vertex vertexDestiny) {
		double tempo = tempoCarga();
		if ((tempo + vertexDestiny.tempo) > cargaMaxima) {
			// System.out.println("CC:: " + (tempo + vertexDestiny.tempo));
			return false;
		} else
			return true;
	}

	@Override
	public double transitionProbability(Edge edge, List<Edge> edgeEighborFeasible, double beta, double alfa) {
		// TODO Auto-generated method stub

		double somatorio = 0;
		for (Edge edgeEighbor : edgeEighborFeasible) {
			if (edgeEighbor.origin.vertexId == position.vertexId) {
				somatorio += (Math.pow(edgeEighbor.destiny.getPheromone(), alfa) * Math.pow(edgeEighbor.destiny.criticidade
						/ edgeEighbor.destiny.tempo, beta));
			} else if (edgeEighbor.destiny.vertexId == position.vertexId) {
				somatorio += (Math.pow(edgeEighbor.origin.getPheromone(), alfa) * Math.pow(edgeEighbor.origin.criticidade
						/ edgeEighbor.destiny.tempo, beta));
			}
		}
		if (somatorio == 0) {
			return 0.0;
		} else {
			// System.out.println("Probabilidadte trasi��o: "+ (Math.pow(edge.getPheromone(), alfa) * Math.pow(1 /
			// edge.getDistance(), beta))
			// / somatorio);
			if (edge.origin.vertexId == position.vertexId) {
				return (Math.pow(edge.destiny.getPheromone(), alfa)
						* Math.pow(edge.destiny.criticidade / edge.destiny.tempo, beta) / somatorio);
			} else {
				return (Math.pow(edge.origin.getPheromone(), alfa) * Math.pow(edge.origin.criticidade / edge.destiny.tempo, beta) / somatorio);
			}
		}
	}

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
					somatorioDeposito += ((vertex.tempo) * Q) / ant.tempoCarga();
				}
			}
			// System.out.println("Deposito:" + somatorioDeposito);
			vertex.pheromone += p1 * vertex.pheromone + somatorioDeposito;

		}
	}

	public double tempoCarga() { // retorna a carga ou seja
		double cargatempo = 0;
		for (Vertex vertex : vertexs) {
			// edge.pheromone = (1 - p1)*edge.pheromone + p1*edge.pheromoneInicial;
			cargatempo += vertex.tempo;
		}
		return cargatempo;
	}

}
