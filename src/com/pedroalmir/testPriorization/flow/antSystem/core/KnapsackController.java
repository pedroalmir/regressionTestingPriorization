/**
 * 
 */
package com.pedroalmir.testPriorization.flow.antSystem.core;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.pedroalmir.testPriorization.flow.antSystem.model.Ant;
import com.pedroalmir.testPriorization.flow.antSystem.model.graph.Edge;
import com.pedroalmir.testPriorization.flow.antSystem.model.graph.Graph;
import com.pedroalmir.testPriorization.flow.antSystem.model.graph.Node;
import com.pedroalmir.testPriorization.flow.antSystem.model.problem.Problem;
import com.pedroalmir.testPriorization.model.Solution;
import com.pedroalmir.testPriorization.model.TestCase;
import com.pedroalmir.testPriorization.model.solution.IterationSolution;

/**
 * Knapsack Controller
 * 
 * @author Pedro Almir
 *
 */
public class KnapsackController {
	/** Grafo que defini o problema */
	private Graph graph;
	/** Problema */
	private Problem problem;
	/** Random */
	private Random random;
	/** List of ants */
	private List<Ant> ants;
	/** */
	private boolean useHeuristic;
	/** */
	private Solution solution;
	private double theBest;
	/**
	 * Constructor
	 * 
	 * @param graph
	 * @param problem
	 */
	public KnapsackController(Graph graph, Problem problem) {
		super();
		this.graph = graph;
		this.problem = problem;
		this.random = new Random();
		this.ants = new LinkedList<Ant>();
		this.useHeuristic = true;
		this.theBest = 0.0;
		/* Ant Initialization */
		if(this.problem.isKnapsackProblem()){
			/* Se for um problema da mochila as formigas são colocadas aleatoriamente pelo grafo. */
			for(int i = 0; i < this.problem.getNumberAnt(); i++){
				ants.add(new Ant("Ant number: " + (i+1), this.graph.getNodes().get(this.random.nextInt(this.graph.getNodes().size()))));
			}
		}
		/* Pheromone initialization */
		for(Edge edge : this.graph.getEdges()){
			if(this.problem.getT0() > 0){
				/* Valor randomicos entre 0.0 e 1.0 */
				edge.setPheromone(this.problem.getT0());
			}else{
				/* Valor randomicos entre 0.0 e 1.0 */
				edge.setPheromone(random.nextDouble());
			}
		}
	}
	
	/** Execute 
	 * @return */
	public Solution execute(){
		int execution = 0;
		/* Loop by executions */
		List<Solution> solutions = new LinkedList<Solution>();
		while(execution < this.problem.getMaxExecution()){
			int iteration = 0;
			/* Loop by iterations */
			LinkedList<IterationSolution> itSolutions = new LinkedList<IterationSolution>();
			long begin = System.currentTimeMillis();
			while(iteration < this.problem.getMaxIterations()){
				/* Construção do caminho para todas as formigas. Se o problema
				 * for da mochila, leva-se em consideração o peso de cada elemento
				 * para criar a lista de possíveis nós seguintes. */
				construction();
				/* Evaporação do feromônio */
				makeEvaporation();
				/* Atualização do feromônio */
				makeActualization();
				/*  */
				itSolutions.add(getBetterResultAndResetSystem(iteration));
				iteration++;
			}
			long end = System.currentTimeMillis();
			
			IterationSolution theBest = findTheBest(itSolutions);
			
			Solution solutionIT = new Solution("AntColonyOptimization", theBest.getTestCases(), this.problem.getBound(), 0, 0, this.problem.getTestCases().size(), 
					theBest.getTempoTotal(), theBest.getCriticidadeTotal(), end-begin);
			solutionIT.setIterationSolutions(itSolutions);
			
			solutions.add(solutionIT);
			execution++;
		}
		this.solution = findTheBest(solutions);
		/* Just for debug */
		//System.out.println(this.solution);
		return this.solution;
	}
	
	/* ######################################################################## */
	/* ########################	       Solutions       ######################## */
	/* ######################################################################## */
	
	private Solution findTheBest(List<Solution> solutions) {
		double best = Double.MIN_VALUE;
		Solution bestSolution = null;
		for (Solution solution : solutions) {
			if(solution.getCriticality() > best){
				best = solution.getCriticality();
				bestSolution = solution;
			}
		}
		return bestSolution;
	}

	private IterationSolution findTheBest(LinkedList<IterationSolution> itSolutions) {
		double best = Double.MIN_VALUE;
		IterationSolution bestSolution = null;
		for (IterationSolution iterationSolution : itSolutions) {
			if(iterationSolution.getBetterSolution() > best){
				best = iterationSolution.getBetterSolution();
				bestSolution = iterationSolution;
			}
		}
		return bestSolution;
	}

	@SuppressWarnings("unchecked")
	private IterationSolution getBetterResultAndResetSystem(int iteration){
		double betterSolution = Double.MIN_VALUE;
		double worstSolution = Double.MAX_VALUE;
		
		double average = 0.0;
		double standardDeviation = 0.0;
		
		double somaSolucoesIteracao = 0.0;
		double somaSolucoesAoQuadradoIteracao = 0.0;
		
		LinkedList<TestCase> testCases = null;
		double quality = 0.0;
		double tempoTotal = 0.0;
		
		for(Ant ant : this.getAnts()){
			
			LinkedList<TestCase> testCasesAux = new LinkedList<TestCase>();
			double qualityAux = 0.0;
			double tempoTotalAux = 0.0;
			
			for(Node node : ant.getTabuList()){
				qualityAux += (Double) node.getInformations().get("criticidade");
				tempoTotalAux += (Double) node.getInformations().get("tempo");
				testCasesAux.add((TestCase) node.getInformations().get("testCase"));
			}
			
			if(qualityAux > betterSolution){
				betterSolution = qualityAux;
				
				quality = qualityAux;
				tempoTotal = tempoTotalAux;
				testCases = (LinkedList<TestCase>) testCasesAux.clone();
			}
			
			if(qualityAux < worstSolution){
				worstSolution = qualityAux;
			}
			
			somaSolucoesIteracao += qualityAux;
			somaSolucoesAoQuadradoIteracao += Math.pow(qualityAux, 2);
			/* Reset */
			ant.setActualNode(this.graph.getNodes().get(this.random.nextInt(this.graph.getNodes().size())));
			ant.setTabuList(new LinkedList<Node>());
			ant.getTabuList().add(ant.getActualNode());
		}
		
		average = somaSolucoesIteracao/this.getAnts().size();
		double variancia = (somaSolucoesAoQuadradoIteracao/this.getAnts().size()) - Math.pow(average, 2);
		standardDeviation = Math.sqrt(variancia);
		IterationSolution iterationSolution = new IterationSolution(iteration, betterSolution, worstSolution, average, standardDeviation);
		iterationSolution.setCriticidadeTotal(quality);
		iterationSolution.setTempoTotal(tempoTotal);
		iterationSolution.setTestCases(testCases);
		
		if(iterationSolution.getBetterSolution() >= this.theBest){
			iterationSolution.setTheBest(iterationSolution.getBetterSolution());
			this.setTheBest(iterationSolution.getBetterSolution());
		}else{
			iterationSolution.setTheBest(this.theBest);
		}
		
		return iterationSolution;
	}
	
	/* ######################################################################## */
	/* ########################	     Construction      ######################## */
	/* ######################################################################## */
	
	/** Construction */
	private void construction(){
		if(this.problem.isKnapsackProblem()){
			for(Ant ant : this.ants){
				Node node = chooseNextNode(ant);
				while(node != null){
					ant.setActualNode(node);
					ant.getTabuList().add(node);
					node = chooseNextNode(ant);
				}
			}
		}
	}
	
	/** Choose Next Node */
	@SuppressWarnings("unchecked")
	private Node chooseNextNode(Ant ant) {
		LinkedList<Edge> edges = (LinkedList<Edge>) ant.getActualNode().getAdjacentEdges().clone();
		Iterator<Edge> iterator = edges.iterator();
		/* Try remove loops */
		while(iterator.hasNext()){
			Edge e = iterator.next();
			if((ant.getTabuList().contains(e.getBegin()) && ant.getTabuList().contains(e.getEnd()))){
				iterator.remove();
				continue;
			}
			if(!e.getBegin().equals(ant.getActualNode())){
				iterator.remove();
				continue;
			}
			if(ultrapassouCapacidade(ant, e.getEnd())){
				iterator.remove();
				continue;
			}
		}
		
		if(edges.isEmpty()){
			return null;
		}
		
		/* Calculando o acumulado da formula de probabilidade */
		List<Auxiliar> values = new LinkedList<Auxiliar>();
		double amount = 0L;
		for(Edge e : edges){
			if(this.useHeuristic){
				amount += Math.pow(e.getPheromone(), this.problem.getAlfa()) * Math.pow(getHeuristicValue(e.getEnd()), this.problem.getBeta());
			}
		}
		/* Valores individuais da formula */
		double sum = 0L;
		for(Edge e : edges){
			double pheromoneNode = Math.pow(e.getPheromone(), this.problem.getAlfa());
			if(this.useHeuristic){
				double heuristic = Math.pow(getHeuristicValue(e.getEnd()), this.problem.getBeta());
				/* */
				values.add(new Auxiliar(e.getEnd(), ((pheromoneNode * heuristic)/amount)));
				sum += ((pheromoneNode * heuristic)/amount);
			}
		}
		/* Ordenando os valores */
		Collections.sort(values, new Comparator<Auxiliar>() {
			@Override
			public int compare(Auxiliar o1, Auxiliar o2) {
				return o1.getProbabilidade().compareTo(o2.getProbabilidade());
			}
		});
		/* Aqui estou normalizando os valores. Agora a soma de todos é igual a 1. 
		 * Depois eu crio a lista com os valores acumulados. */
		int count = 0;
		for(Auxiliar d : values){
			d.setProbabilidade(d.getProbabilidade()/sum);
			if(count > 0){
				d.setProbabilidade(values.get(count-1).getProbabilidade() + d.getProbabilidade());
			}
			count++;
		}
		/* Valor randomico escolhido */
		double randomValue = random.nextDouble();
		for(Auxiliar d : values){
			if(randomValue <= d.getProbabilidade().doubleValue()){
				return d.getNo();
			}
		}
		return null;
	}

	/** Verifica se o próximo nós não ultrapassa a capacidade da mochila */
	private boolean ultrapassouCapacidade(Ant ant, Node node) {
		double actualValue = 0.0;
		for(Node n : ant.getTabuList()){
			actualValue += (Double) n.getInformations().get("tempo");
		}
		double nextValue = (Double) node.getInformations().get("tempo");
		if(actualValue + nextValue > this.problem.getBound()){
			return true;
		}
		return false;
	}

	/** Get Heuristic Value */
	private double getHeuristicValue(Node node) {
		return (Double) node.getInformations().get("criticidade");
	}
	
	/* ######################################################################## */
	/* ########################	     Evaporation       ######################## */
	/* ######################################################################## */
	

	/** Evaporation */
	private void makeEvaporation(){
		for(Edge e : this.graph.getEdges()){
			e.setPheromone((1 - this.problem.getP()) * e.getPheromone());
		}
	}
	
	/* ######################################################################## */
	/* ########################	     Actualization     ######################## */
	/* ######################################################################## */
	
	/** Actualization */
	private void makeActualization(){
		for(Ant ant : this.ants){
			for(Node n : ant.getTabuList()){
				for(Edge e : this.graph.getEdges()){
					if(e.getBegin().equals(n) || e.getEnd().equals(n)){
						e.setPheromone(e.getPheromone() + 1/calcQualityOfResult(ant));
					}
				}
			}
		}
	}
	
	/** Calc Quality of Result */
	private double calcQualityOfResult(Ant ant) {
		double quality = 0.0;
		for(Node node : ant.getTabuList()){
			quality += (Double) node.getInformations().get("criticidade");
		}
		return quality;
	}
	
	/* ######################################################################## */
	/* ########################	         Bean	   	   ######################## */
	/* ######################################################################## */

	/**
	 * @return the graph
	 */
	public Graph getGraph() {
		return graph;
	}

	/**
	 * @param graph the graph to set
	 */
	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	/**
	 * @return the problem
	 */
	public Problem getProblem() {
		return problem;
	}

	/**
	 * @param problem the problem to set
	 */
	public void setProblem(Problem problem) {
		this.problem = problem;
	}

	/**
	 * @return the random
	 */
	public Random getRandom() {
		return random;
	}

	/**
	 * @param random the random to set
	 */
	public void setRandom(Random random) {
		this.random = random;
	}

	/**
	 * @return the ants
	 */
	public List<Ant> getAnts() {
		return ants;
	}

	/**
	 * @param ants the ants to set
	 */
	public void setAnts(List<Ant> ants) {
		this.ants = ants;
	}

	/**
	 * @return the useHeuristic
	 */
	public boolean isUseHeuristic() {
		return useHeuristic;
	}

	/**
	 * @param useHeuristic the useHeuristic to set
	 */
	public void setUseHeuristic(boolean useHeuristic) {
		this.useHeuristic = useHeuristic;
	}
	
	/**
	 * Classe auxiliar
	 * 
	 * @author Pedro Almir
	 */
	class Auxiliar{
		private Node no;
		private Double probabilidade;
		
		/**
		 * @param no
		 * @param probabilidade
		 */
		public Auxiliar(Node no, Double probabilidade) {
			super();
			this.no = no;
			this.probabilidade = probabilidade;
		}
		/**
		 * @return the no
		 */
		public Node getNo() {
			return no;
		}
		/**
		 * @param no the no to set
		 */
		public void setNo(Node no) {
			this.no = no;
		}
		/**
		 * @return the probabilidade
		 */
		public Double getProbabilidade() {
			return probabilidade;
		}
		/**
		 * @param probabilidade the probabilidade to set
		 */
		public void setProbabilidade(Double probabilidade) {
			this.probabilidade = probabilidade;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Auxiliar [probabilidade=" + probabilidade + "]";
		}
	}

	/**
	 * @return the solution
	 */
	public Solution getSolution() {
		return solution;
	}

	/**
	 * @param solution the solution to set
	 */
	public void setSolution(Solution solution) {
		this.solution = solution;
	}

	/**
	 * @return the theBest
	 */
	public double getTheBest() {
		return theBest;
	}

	/**
	 * @param theBest the theBest to set
	 */
	public void setTheBest(double theBest) {
		this.theBest = theBest;
	}
}
