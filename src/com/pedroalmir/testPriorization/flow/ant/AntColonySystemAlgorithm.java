package com.pedroalmir.testPriorization.flow.ant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.pedroalmir.testPriorization.flow.antSystem.model.Problem;
import com.pedroalmir.testPriorization.model.TestCase;

public class AntColonySystemAlgorithm {

	/**
	 * @param args
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	public static void executeAntSystem(Problem antProblem) throws Exception {
		int numberAnt = antProblem.getNumberAnt();

		double t0 = antProblem.getT0();
		double alfa = antProblem.getAlfa();
		double beta = antProblem.getBeta();
		double Q = antProblem.getQ();

		int iteration = 0;

		int maxIterarions = antProblem.getMaxIterations();
		int maxExecucao = antProblem.getMaxExecution();

		double p1 = antProblem.getP1(), p2 =antProblem.getP2(), q0 = antProblem.getQ0(), constantQ = antProblem.getConstantQ();
		int k = antProblem.getK();

		Random rand = new Random();

		double vMelhor[] = new double[maxExecucao];
		double vPior[] = new double[maxExecucao];
		double vMedia[] = new double[maxExecucao];
		
		String prints[] = new String[maxExecucao];
		
		double vetorSolucoes[];
		int execucao = 0;

		do {
			List<Vertex> vertexs = new ArrayList<Vertex>();
			List<Edge> edges = new ArrayList<Edge>();
			
			createGraph(antProblem.getTestCases(), vertexs, edges);

			// Criando as nf formigas Ant Colony System
			List<AntColonySystem> ants = new ArrayList<AntColonySystem>();
			for (int i = 0; i < numberAnt; i += 1) {
				ants.add(new AntColonySystem());
			}

			double valorMelhorCaminho = Double.MAX_VALUE;
			double valorPiorCaminho = Double.MIN_VALUE;
			List<Vertex> vertexsPiorSolucao = new ArrayList<Vertex>();
			List<Vertex> vertexsMelhorSolucao = new ArrayList<Vertex>();
			
			AntColonySystem antBestEvaluation = new AntColonySystem();
			AntColonySystem antWorstEvaluation = new AntColonySystem();

			// Para cada execucao o vetorSolucoes vai deve ser reiniciado
			vetorSolucoes = new double[numberAnt * maxIterarions];

			do {
				// Posicionando as nf formigas no vertice 2 e limpando a lista tabu e a de vertices
				for (int i = 0; i < ants.size(); i += 1) {
					ants.get(i).position = vertexs.get(rand.nextInt(vertexs.size()));
					ants.get(i).tabuList = new ArrayList<Edge>();
					ants.get(i).vertexs = new ArrayList<Vertex>();
					ants.get(i).vertexs.add(ants.get(i).position);
				}

				// Construindo o caminho
				for (AntColonySystem ant : ants) {
					ant.buildPath(ant.position, edges, beta, q0, alfa);
				}

				AntColonySystem.deposityGlobalPheromone(p1, Q, ants, vertexs);

				double melhorSolucaoIteracao = Double.MIN_VALUE;
				double piorSolucaoIteracao = Double.MAX_VALUE;

				double somaSolucoesIteracao = 0;
				double somaSolucoesAoQuadradoIteracao = 0;

				double vetorSolucoesIteracao[] = new double[numberAnt];
				int w = 0;
				
				for (AntColonySystem ant : ants) {
					vetorSolucoesIteracao[w] = ant.custo();

					// Verifica para guadar o melhor e o pior caminho
					if (valorMelhorCaminho >= ant.custo()) {
						valorMelhorCaminho = ant.custo();
						vertexsMelhorSolucao = ant.returnPathVertexs();
						antBestEvaluation = ant;

					}
					if (valorPiorCaminho <= ant.custo()) {
						valorPiorCaminho = ant.custo();
						vertexsPiorSolucao = ant.returnPathVertexs();
						antWorstEvaluation = ant;
					}
					w++;
				}

				for (int j = 0; j < numberAnt; j++) {
					if (melhorSolucaoIteracao < vetorSolucoesIteracao[j]) {
						melhorSolucaoIteracao = vetorSolucoesIteracao[j];
					}
					if (piorSolucaoIteracao > vetorSolucoesIteracao[j]) {
						piorSolucaoIteracao = vetorSolucoesIteracao[j];
					}
					somaSolucoesIteracao += vetorSolucoesIteracao[j];
					somaSolucoesAoQuadradoIteracao += Math.pow(vetorSolucoesIteracao[j], 2);
				}

				double desvioPadraoIteracao = 0;
				double variancia = somaSolucoesAoQuadradoIteracao / numberAnt - Math.pow(somaSolucoesIteracao / numberAnt, 2);
				if (variancia > 0) {
					desvioPadraoIteracao = Math.sqrt(somaSolucoesAoQuadradoIteracao / numberAnt - Math.pow(somaSolucoesIteracao / numberAnt, 2));
				}

				for (AntColonySystem ant : ants) {
					vetorSolucoes[k] = ant.custo();
					k++;
				}

				iteration += 1;
			} while (iteration < maxIterarions);

			double melhorSolucao = Double.MIN_VALUE;
			double piorSolucao = Double.MAX_VALUE;
			double somaSolucoes = 0;
			double somaSolucoesAoQuadrado = 0;
			for (int j = 0; j < numberAnt * maxIterarions; j++) {
				if (melhorSolucao < vetorSolucoes[j]) {
					melhorSolucao = vetorSolucoes[j];
				}
				if (piorSolucao > vetorSolucoes[j]) {
					piorSolucao = vetorSolucoes[j];
				}
				somaSolucoes += vetorSolucoes[j];
				somaSolucoesAoQuadrado += Math.pow(vetorSolucoes[j], 2);
			}

			double desvioPadrao = Math.sqrt(somaSolucoesAoQuadrado / (numberAnt * maxIterarions) - Math.pow(somaSolucoes / (numberAnt * maxIterarions), 2));
			
			vPior[execucao] = piorSolucao;
			vMelhor[execucao] = melhorSolucao;
			vMedia[execucao] = somaSolucoes / (numberAnt * maxIterarions);
			
			StringBuffer buffer = new StringBuffer();
			buffer.append("Execução " + execucao + ", Melhor: " + melhorSolucao + ", Pior: " + piorSolucao + ", Media: " + (somaSolucoes / numberAnt * maxIterarions));
			buffer.append("\n# ");
			
			double custo = 0.0;
			double tempo = 0.0;
			// Caminho das soluções
			for (int i = 0; i < vertexsPiorSolucao.size(); i++) {
				custo += vertexsPiorSolucao.get(i).criticidade;
				tempo += vertexsPiorSolucao.get(i).tempo;
				
				buffer.append(String.format("%02d", vertexsPiorSolucao.get(i).getVertexId()));
				if (i != vertexsPiorSolucao.size() - 1)
					buffer.append("-");
			}
			
			buffer.append(" # Custo: " + custo + ", Tempo: " + tempo);
			buffer.append(" # ");
			
			custo = 0.0;
			tempo = 0.0;
			
			for (int i = 0; i < vertexsMelhorSolucao.size(); i++) {
				custo += vertexsMelhorSolucao.get(i).criticidade;
				tempo += vertexsMelhorSolucao.get(i).tempo;
				
				buffer.append(String.format("%02d", vertexsMelhorSolucao.get(i).getVertexId()));
				if (i != vertexsMelhorSolucao.size() - 1)
					buffer.append("-");
			}
			buffer.append(" # Custo: " + custo + ", Tempo: " + tempo);
			buffer.append("\n");
			
			prints[execucao] = buffer.toString();

			k = 0;
			iteration = 0;
			execucao++;
		} while (execucao < maxExecucao);
		
		double major = Double.MIN_VALUE;
		int index = 0;
		for(int i = 0; i < vMelhor.length; i++){
			if(vMelhor[i] > major){
				major = vMelhor[i];
				index = i;
			}
		}
		
		System.out.println(prints[index]);
		
		calculaDesvios(vPior, vMelhor, vMedia, maxExecucao);
	}

	/**
	 * @param testCases
	 * @param vertexs
	 * @param edges
	 */
	private static void createGraph(List<TestCase> testCases, List<Vertex> vertexs, List<Edge> edges) {
		/* Vertex com id, criticidade, tempo e feromonio inicial */
		for (int i = 0; i < testCases.size(); i++) {
			vertexs.add(new Vertex(i+1, testCases.get(i).getCriticality(), testCases.get(i).getTime(), 0.01));
		}
		/* */
		int indexEdge = 1;
		for (int i = 0; i < testCases.size(); i++) {
			for (int j = 0; j < testCases.size(); j++) {
				if (i != j) {
					edges.add(new Edge(vertexs.get(i), vertexs.get(j), indexEdge, -1, -1));
					indexEdge++;
				}
			}
		}
	}

	public static void calculaDesvios(double[] vPior, double[] vMelhor, double[] vMedia, int tam) {
		double[] vPiorAoQuadrado = new double[tam];
		double somaPior = 0;
		double[] vMelhorAoQuadrado = new double[tam];
		double somaMelhor = 0;
		double[] vMediaAoQuadrado = new double[tam];
		double somaMedia = 0;
		double somaPiorAoQuadrado = 0;
		double somaMelhorAoQuadrado = 0;
		double somaMediaAoQuadrado = 0;
		// Desvios padr�es
		double desvioPadraoPior = 0;
		double desvioPadraoMelhor = 0;
		double desvioPadraoMedia = 0;

		for (int i = 0; i < tam; i++) {
			vPiorAoQuadrado[i] = Math.pow(vPior[i], 2.0);
			vMelhorAoQuadrado[i] = Math.pow(vMelhor[i], 2.0);
			vMediaAoQuadrado[i] = Math.pow(vMedia[i], 2.0);
		}
		for (int i = 0; i < tam; i++) {
			somaPior += vPior[i];
			somaMelhor += vMelhor[i];
			somaMedia += vMedia[i];

			somaPiorAoQuadrado += vPiorAoQuadrado[i];
			somaMelhorAoQuadrado += vMelhorAoQuadrado[i];
			somaMediaAoQuadrado += vMediaAoQuadrado[i];
			//System.out.println("vp: " + vPior[i]);
			//System.out.println("vm: " + vMelhor[i]);
			//System.out.println("vme: " + vMedia[i]);
		}
		double varianciaPior = somaPiorAoQuadrado / tam - Math.pow(somaPior / tam, 2);
		double varianciaMelhor = somaMelhorAoQuadrado / tam - Math.pow(somaMelhor / tam, 2);
		double varianciaMedia = somaMediaAoQuadrado / tam - Math.pow(somaMedia / tam, 2);

		if (varianciaPior >= 0)
			desvioPadraoPior = Math.sqrt(somaPiorAoQuadrado / ((double) tam) - Math.pow(somaPior / ((double) tam), 2));
		if (varianciaMelhor >= 0)
			desvioPadraoMelhor = Math.sqrt(somaMelhorAoQuadrado / ((double) tam) - Math.pow(somaMelhor / ((double) tam), 2));
		if (varianciaMedia >= 0)
			desvioPadraoMedia = Math.sqrt(somaMediaAoQuadrado / ((double) tam) - Math.pow(somaMedia / ((double) tam), 2));

		System.out.println("Media Pior: " + somaPior / tam);
		System.out.println("Media Melhor: " + somaMelhor / tam);
		System.out.println("Media da Media: " + somaMedia / tam);
		System.out.println("Desvio pior Global: " + desvioPadraoPior);
		System.out.println("Desvio melhor Global: " + desvioPadraoMelhor);
		System.out.println("Desvio media Global: " + desvioPadraoMedia);

	}
}
