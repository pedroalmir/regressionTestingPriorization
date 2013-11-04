package com.pedroalmir.testPriorization.flow.ant;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.pedroalmir.testPriorization.flow.antSystem.model.problem.Problem;
import com.pedroalmir.testPriorization.model.RegressionTestingPriorizationProblem;
import com.pedroalmir.testPriorization.model.Solution;
import com.pedroalmir.testPriorization.model.TestCase;
import com.pedroalmir.testPriorization.model.solution.IterationSolution;

public class AntColonySystemAlgorithm {
	
	/**
	 * @param problem 
	 * @param args
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	public static Solution executeAntSystem(Problem antProblem, RegressionTestingPriorizationProblem problem) throws Exception {
		int numberAnt = antProblem.getNumberAnt();

		double alfa = antProblem.getAlfa();
		double beta = antProblem.getBeta();
		double Q = antProblem.getQ();

		int iteration = 0, execucao = 0;
		int maxIterarions = antProblem.getMaxIterations();
		int maxExecucao = antProblem.getMaxExecution();

		double p1 = antProblem.getP();
		int k = antProblem.getK();

		Random rand = new Random();

		double vMelhor[] = new double[maxExecucao];
		double vPior[] = new double[maxExecucao];
		double vMedia[] = new double[maxExecucao];
		double vetorSolucoes[];
		
		//String prints[] = new String[maxExecucao];
		List<Solution> solutions = new LinkedList<Solution>();
		double melhor = Double.MIN_VALUE;
		
		/* ###################################################################### 
		List<String[]> data = new ArrayList<String[]>();
        CSVWriter writer = new CSVWriter(new FileWriter("results/AntColony.csv"), ';');
        String[] firstLine = new String[maxExecucao+1];
        firstLine[0] = "";
        String[] secondLine = new String[maxExecucao+1];
        secondLine[0] = "BetterSolution";
        String[] thirdLine = new String[maxExecucao+1];
        thirdLine[0] = "WorstSolution";
        String[] fourthLine = new String[maxExecucao+1];
        fourthLine[0] = "Average";
        String[] fifthLine = new String[maxExecucao+1];
        fifthLine[0] = "StandardDeviation";
        String[] sixthLine = new String[maxExecucao+1];
        sixthLine[0] = "Solucao Elitista";
        
        int myCount = 0;
        String[] lineMaster = new String[maxIterarions*maxExecucao];
        lineMaster[0] = "";
        String[] lineMasterII = new String[maxIterarions*maxExecucao];
        lineMasterII[0] = "Melhor Solução até o momento: ";
		/* ###################################################################### */

		do {
			List<Vertex> vertexs = new ArrayList<Vertex>();
			List<Edge> edges = new ArrayList<Edge>();
			
			createGraph(antProblem.getTestCases(), vertexs, edges);

			// Criando as n formigas: Ant Colony System
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

			/* Para cada execucao o vetorSolucoes vai deve ser reiniciado */
			vetorSolucoes = new double[numberAnt * maxIterarions];
			/* Lista de soluções das iterações */
			List<IterationSolution> solutionsOfIterations = new LinkedList<IterationSolution>();
			
			/* ######################################################################
	        String[] lineA = new String[maxIterarions+1];
	        lineA[0] = "";
	        String[] lineB = new String[maxIterarions+1];
	        lineB[0] = "BetterSolution";
	        String[] lineC = new String[maxIterarions+1];
	        lineC[0] = "WorstSolution";
	        String[] lineD = new String[maxIterarions+1];
	        lineD[0] = "Average";
	        String[] lineF = new String[maxIterarions+1];
	        lineF[0] = "StandardDeviation";
			/* ###################################################################### */
			
			do {
				/* Posicionando as n formigas nos vertices e limpando a lista tabu */
				for (int i = 0; i < ants.size(); i += 1) {
					ants.get(i).position = vertexs.get(rand.nextInt(vertexs.size()));
					ants.get(i).tabuList = new ArrayList<Edge>();
					ants.get(i).vertexs = new ArrayList<Vertex>();
					ants.get(i).vertexs.add(ants.get(i).position);
				}

				/* Construindo o caminho */
				for (AntColonySystem ant : ants) {
					ant.buildPath(ant.position, edges, beta, 0.0, alfa);
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
					/* Verifica para guadar o melhor e o pior caminho */
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
				/* */
				double criticidade = 0.0, tempoTotal = 0.0;
				List<TestCase> selected = null;
				
				for (int j = 0; j < numberAnt; j++) {
					if (melhorSolucaoIteracao < vetorSolucoesIteracao[j]) {
						melhorSolucaoIteracao = vetorSolucoesIteracao[j];
						criticidade = ants.get(j).custo();
						tempoTotal = ants.get(j).tempoTotal();
						
						selected = new LinkedList<TestCase>();
						for(Vertex v : ants.get(j).returnPathVertexs()){
							selected.add(v.getTestCase());
						}
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
				
				/* ######################################################################
				lineA[iteration+1] = "Iteration" + (iteration+1);
		        lineB[iteration+1] = (melhorSolucaoIteracao + "").replaceAll("\\.", ",");
		        lineC[iteration+1] = (piorSolucaoIteracao + "").replaceAll("\\.", ",");
		        lineD[iteration+1] = ((somaSolucoesIteracao/numberAnt) + "").replaceAll("\\.", ",");
		        lineF[iteration+1] = (desvioPadraoIteracao + "").replaceAll("\\.", ",");
		        sixthLine[iteration+1] = (melhor + "").replaceAll("\\.", ",");
		        /* ###################################################################### */
		        
				for (int j = 0; j < numberAnt * maxIterarions; j++) {
					if (melhor < vetorSolucoes[j]) {
						melhor = vetorSolucoes[j];
					}
				}
				
				IterationSolution iterationSolution = new IterationSolution(iteration+1, melhorSolucaoIteracao, piorSolucaoIteracao, somaSolucoesIteracao/numberAnt, desvioPadraoIteracao);
				iterationSolution.setCriticidadeTotal(criticidade);
				iterationSolution.setTempoTotal(tempoTotal);
				iterationSolution.setTheBest(melhor);
				iterationSolution.setTestCases(selected);
				
				solutionsOfIterations.add(iterationSolution);
				iteration++;
				
		        //lineMaster[myCount] = "Iteration " + (myCount+1);
		        //lineMasterII[myCount++] = (melhor + "").replaceAll("\\.", ",");
			} while (iteration < maxIterarions);
			
			/* ###################################################################### 
			data.add(new String[31]);
			data.add(lineA);
			data.add(lineB);
			data.add(lineC);
			data.add(lineD);
			data.add(lineF);
			data.add(sixthLine);
			data.add(new String[31]);
			/* ###################################################################### */

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
			
			//StringBuffer bufferII = new StringBuffer();
			//bufferII.append("Execution " + execucao + "\n");
			//bufferII.append("BetterSolution:    " + melhorSolucao + "\n");
			//bufferII.append("WorstSolution:     " + piorSolucao + "\n");
			//bufferII.append("Average:           " + somaSolucoes / (numberAnt * maxIterarions) + "\n");
			//bufferII.append("StandardDeviation: " + desvioPadrao + "\n");
			//System.out.println(bufferII.toString().replaceAll("\\.", ","));
			
			/* ######################################################################
			firstLine[execucao+1] = "Execution " + (execucao+1);
			secondLine[execucao+1] = (melhorSolucao + "").replaceAll("\\.", ",");
			thirdLine[execucao+1] = (piorSolucao + "").replaceAll("\\.", ",");
			fourthLine[execucao+1] = ((somaSolucoes/(numberAnt * maxIterarions)) + "").replaceAll("\\.", ",");
			fifthLine[execucao+1] = (desvioPadrao + "").replaceAll("\\.", ",");
			/* ###################################################################### */
			
			Solution solution = new Solution();
			solution.setAlgorithm("AntColonySystem");
			solution.setCapacidade(problem.getCapacidade());
			solution.setNumberOfClasses(problem.getKlasses().size());
			solution.setNumberOfRequirements(problem.getRequirements().size());
			solution.setNumberOfTests(problem.getTestCases().size());
			solution.setIterationSolutions(solutionsOfIterations);
			
			//StringBuffer buffer = new StringBuffer();
			//buffer.append("Execução " + execucao + ", Melhor: " + melhorSolucao + ", Pior: " + piorSolucao + ", Media: " + (somaSolucoes / numberAnt * maxIterarions));
			//buffer.append("\n# ");
			
			double custo = 0.0, tempo = 0.0;
			List<TestCase> selected = new LinkedList<TestCase>();
			
			/* Caminho das soluções */
			for (int i = 0; i < vertexsPiorSolucao.size(); i++) {
				custo += vertexsPiorSolucao.get(i).criticidade;
				tempo += vertexsPiorSolucao.get(i).tempo;
				selected.add(vertexsPiorSolucao.get(i).getTestCase());
				
				//buffer.append(String.format("%02d", vertexsPiorSolucao.get(i).getVertexId()));
				//if (i != vertexsPiorSolucao.size() - 1)
					//buffer.append("-");
			}
			
			solution.setCriticality(custo);
			solution.setTimeOfTests(tempo);
			
			//buffer.append(" # Custo: " + custo + ", Tempo: " + tempo);
			//buffer.append(" # ");
			
			custo = 0.0;
			tempo = 0.0;
			
			for (int i = 0; i < vertexsMelhorSolucao.size(); i++) {
				custo += vertexsMelhorSolucao.get(i).criticidade;
				tempo += vertexsMelhorSolucao.get(i).tempo;
				
				//buffer.append(String.format("%02d", vertexsMelhorSolucao.get(i).getVertexId()));
				//if (i != vertexsMelhorSolucao.size() - 1)
					//buffer.append("-");
			}
			//buffer.append(" # Custo: " + custo + ", Tempo: " + tempo);
			//buffer.append("\n");
			
			solution.setBetterTestCases(selected);
			solutions.add(solution);
			//prints[execucao] = buffer.toString();

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
		
		//System.out.println(prints[index]);
		
		/* ######################################################################
		data.add(new String[maxExecucao+1]);
		data.add(firstLine);
		data.add(secondLine);
		data.add(thirdLine);
		data.add(fourthLine);
		data.add(fifthLine);
		/* ###################################################################### */
		
		/* ######################################################################
		data.add(new String[maxExecucao+1]);
		data.add(new String[]{"Melhor Solucao ate o momento"});
		data.add(lineMaster);
		data.add(lineMasterII);
		data.add(new String[maxExecucao+1]);
		/* ###################################################################### */
		
		/* ######################################################################
		String[] line1 = new String[solutions.get(index).getIterationSolutions().size()+1];
		line1[0] = "";
		String[] line2 = new String[solutions.get(index).getIterationSolutions().size()+1];
		line2[0] = "BetterSolution";
		String[] line3 = new String[solutions.get(index).getIterationSolutions().size()+1];
		line3[0] = "WorstSolution";
		String[] line4 = new String[solutions.get(index).getIterationSolutions().size()+1];
		line4[0] = "Average";
		String[] line5 = new String[solutions.get(index).getIterationSolutions().size()+1];
		line5[0] = "StandardDeviation";
		/* ######################################################################
		int count = 1;
		for(IterationSolution i : solutions.get(index).getIterationSolutions()){
			//System.out.println(i);
			line1[count] = "Iteration " + count;
			line2[count] = (i.getBetterSolution() + "").replaceAll("\\.", ",");
			line3[count] = (i.getWorstSolution() + "").replaceAll("\\.", ",");
			line4[count] = (i.getAverage() + "").replaceAll("\\.", ",");
			line5[count] = (i.getStandardDeviation() + "").replaceAll("\\.", ",");
	        count++;
		}
		/* ######################################################################
		data.add(new String[maxExecucao+1]);
		data.add(new String[]{"Melhor Execução"});
		data.add(line1);
		data.add(line2);
		data.add(line3);
		data.add(line4);
		data.add(line5);
		/* ###################################################################### */
		
        //writer.writeAll(data);
        //writer.close();
		
		//calculaDesvios(vPior, vMelhor, vMedia, maxExecucao);
		return solutions.get(index);
	}

	/**
	 * @param testCases
	 * @param vertexs
	 * @param edges
	 */
	private static void createGraph(List<TestCase> testCases, List<Vertex> vertexs, List<Edge> edges) {
		/* Vertex com id, criticidade, tempo e feromonio inicial */
		for (int i = 0; i < testCases.size(); i++) {
			vertexs.add(new Vertex(i+1, testCases.get(i).getCriticality(), testCases.get(i).getTime(), 0.01, testCases.get(i)));
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

	@SuppressWarnings("unused")
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

		//System.out.println("Media Pior: " + somaPior / tam);
		//System.out.println("Media Melhor: " + somaMelhor / tam);
		//System.out.println("Media da Media: " + somaMedia / tam);
		//System.out.println("Desvio pior Global: " + desvioPadraoPior);
		//System.out.println("Desvio melhor Global: " + desvioPadraoMelhor);
		//System.out.println("Desvio media Global: " + desvioPadraoMedia);

	}
}