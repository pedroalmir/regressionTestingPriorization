/**
 * 
 */
package com.pedroalmir.testPriorization.flow.antSystem.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.pedroalmir.testPriorization.flow.antSystem.model.Problem;
import com.pedroalmir.testPriorization.flow.antSystem.model.graph.Edge;
import com.pedroalmir.testPriorization.flow.antSystem.model.graph.Vertex;

/**
 * @author Pedro Almir
 * 
 */
public class ExecuteAntColonySystem {
	@SuppressWarnings("unused")
	public static void executeAlgorithm(Problem problem) {
		/* */
		Random rand = new Random();
		/* */
		double vMelhor[] = new double[problem.getMaxExecution()];
		double vPior[] = new double[problem.getMaxExecution()];
		double vMedia[] = new double[problem.getMaxExecution()];

		double vetorSolucoes[];
		int execucao = 0;
		int iteration = 0;

		do {
			// Criando as nf formigas Ant Colony System
			List<AntColonySystem> ants = new ArrayList<AntColonySystem>();
			for (int i = 0; i < problem.getNumberAnt(); i++) {
				ants.add(new AntColonySystem(problem.getCargaMaxima()));
			}

			// Informacoes dos melhor e pior caminho global da execucao corrente
			double valorMelhorCaminho = Double.MAX_VALUE;
			double valorPiorCaminho = Double.MIN_VALUE;
			List<Vertex> vertexsPiorSolucao = new ArrayList<Vertex>();
			List<Vertex> vertexsMelhorSolucao = new ArrayList<Vertex>();

			// Armazena melhor formiga para fazer a atualizacao global.
			AntColonySystem antBestEvaluation = new AntColonySystem(problem.getCargaMaxima());
			AntColonySystem antWorstEvaluation = new AntColonySystem(problem.getCargaMaxima());

			// Para cada execucao o vetorSolucoes vai deve ser reiniciado
			vetorSolucoes = new double[problem.getNumberAnt() * problem.getMaxExecution()];

			do {
				// Posicionando as nf formigas no vertice 2 e limpando a lista tabu e a de vertices
				for (int i = 0; i < ants.size(); i += 1) {
					ants.get(i).setPosition(problem.getVertexs().get(rand.nextInt(problem.getVertexs().size())));
					ants.get(i).setTabuList(new ArrayList<Edge>());
					ants.get(i).setVertexs(new ArrayList<Vertex>());
					ants.get(i).getVertexs().add(ants.get(i).getPosition());
				}

				// Construindo o caminho
				for (AntColonySystem ant : ants) {
					ant.buildPath(ant.getPosition(), problem.getEdges(), problem.getBeta(), problem.getQ0(), problem.getAlfa());
				}

				AntColonySystem.deposityGlobalPheromone(problem.getP1(), problem.getQ(), ants, problem.getVertexs());

				// Calculos das solucoes melhor e pior e soma delas.
				double melhorSolucaoIteracao = Double.MIN_VALUE;
				double piorSolucaoIteracao = Double.MAX_VALUE;

				double somaSolucoesIteracao = 0;
				double somaSolucoesAoQuadradoIteracao = 0;

				double vetorSolucoesIteracao[] = new double[problem.getNumberAnt()];
				int w = 0;


				// Armazenado Informacoes para os calculos
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

				for (int j = 0; j < problem.getNumberAnt(); j++) {
					if (melhorSolucaoIteracao < vetorSolucoesIteracao[j]) {
						melhorSolucaoIteracao = vetorSolucoesIteracao[j];
					}
					if (piorSolucaoIteracao > vetorSolucoesIteracao[j]) {
						piorSolucaoIteracao = vetorSolucoesIteracao[j];
					}
					somaSolucoesIteracao += vetorSolucoesIteracao[j];
					somaSolucoesAoQuadradoIteracao += Math.pow(vetorSolucoesIteracao[j], 2);
				}

				// calculo do desvio padrao da iteracao
				double desvioPadraoIteracao = 0;
				double variancia = somaSolucoesAoQuadradoIteracao / problem.getNumberAnt()
						- Math.pow(somaSolucoesIteracao / problem.getNumberAnt(), 2);

				if (variancia > 0) {
					desvioPadraoIteracao = Math.sqrt(somaSolucoesAoQuadradoIteracao / problem.getNumberAnt()
							- Math.pow(somaSolucoesIteracao / problem.getNumberAnt(), 2));
				}

				// Armazenado Informacoes para os calculos
				for (AntColonySystem ant : ants) {
					vetorSolucoes[problem.getK()] = ant.custo();
					//System.out.println("K: " + vetorSolucoes[problem.getK()]);
					problem.setK(problem.getK() + 1);
				}

				iteration++;
			} while (iteration < problem.getMaxIterations());

			// Calculos das solucoes melhor e pior e soma delas.
			double melhorSolucao = Double.MIN_VALUE;
			double piorSolucao = Double.MAX_VALUE;
			double somaSolucoes = 0;
			double somaSolucoesAoQuadrado = 0;

			for (int j = 0; j < problem.getNumberAnt() * problem.getMaxIterations(); j++) {
				if (melhorSolucao < vetorSolucoes[j]) {
					melhorSolucao = vetorSolucoes[j];
				}
				if (piorSolucao > vetorSolucoes[j]) {
					piorSolucao = vetorSolucoes[j];
				}
				somaSolucoes += vetorSolucoes[j];
				somaSolucoesAoQuadrado += Math.pow(vetorSolucoes[j], 2);
			}

			// calculo do desvio padrao
			double desvioPadrao = Math.sqrt(somaSolucoesAoQuadrado / (problem.getNumberAnt() * problem.getMaxIterations())
					- Math.pow(somaSolucoes / (problem.getNumberAnt() * problem.getMaxIterations()), 2));

			System.out.println("Execução " + execucao);
			System.out.println("Melhor:: " + melhorSolucao);
			System.out.println("Pior:: " + piorSolucao);
			System.out.println("Media:: " + somaSolucoes / (problem.getNumberAnt() * problem.getMaxIterations()));
			System.out.println();

			vPior[execucao] = piorSolucao;
			vMelhor[execucao] = melhorSolucao;
			vMedia[execucao] = somaSolucoes / (problem.getNumberAnt() * problem.getMaxIterations());

			// Caminho das soluções
			System.out.println("\n\nSoluções:\n");
			for (int i = 0; i < vertexsPiorSolucao.size(); i++) {
				System.out.print(vertexsPiorSolucao.get(i).getVertexIdToS());
				if (i != vertexsPiorSolucao.size() - 1)
					System.out.print("-");
			}
			System.out.print(" # ");
			for (int i = 0; i < vertexsMelhorSolucao.size(); i++) {
				System.out.print(vertexsMelhorSolucao.get(i).getVertexIdToS());
				if (i != vertexsMelhorSolucao.size() - 1)
					System.out.print("-");
			}
			
			System.out.println("\n\n");
			
			problem.setK(0);
			iteration = 0;
			execucao += 1;
		} while (execucao < problem.getMaxExecution());

		calculaDesvios(vPior, vMelhor, vMedia, problem.getMaxExecution());
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
		// Desvios padrões
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
			System.out.println("vp: " + vPior[i]);
			System.out.println("vm: " + vMelhor[i]);
			System.out.println("vme: " + vMedia[i]);
			System.out.println();
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

		System.out.println();
		System.out.println("Media Pior: " + somaPior / tam);
		System.out.println("Media Melhor: " + somaMelhor / tam);
		System.out.println("Media da Media: " + somaMedia / tam);
		System.out.println("Desvio pior Global: " + desvioPadraoPior);
		System.out.println("Desvio melhor Global: " + desvioPadraoMelhor);
		System.out.println("Desvio media Global: " + desvioPadraoMedia);

	}
}
