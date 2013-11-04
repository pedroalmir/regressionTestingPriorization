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

/**
 * @author Pedro Almir
 *
 */
public class AntColonyOptimizationController {
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
	/**
	 * Constructor
	 * 
	 * @param graph
	 * @param problem
	 */
	public AntColonyOptimizationController(Graph graph, Problem problem) {
		super();
		this.graph = graph;
		this.problem = problem;
		this.random = new Random();
		this.ants = new LinkedList<Ant>();
		/* Ant Initialization */
		if(this.problem.isKnapsackProblem()){
			/* Se for um problema da mochila as formigas são colocadas aleatoriamente pelo grafo. */
			for(int i = 0; i < this.problem.getNumberAnt(); i++){
				ants.add(new Ant("Ant number: " + (i+1), this.graph.getNodes().get(this.random.nextInt(this.graph.getNodes().size()))));
			}
		}else if(this.problem.getBeginNodes().size() == 1){
			/* Se não for um problema da mochila e houver apenas um nó de origem, todas as formigas
			 * serão iniciadas nesse nó. */
			for(int i = 0; i < this.problem.getNumberAnt(); i++){
				ants.add(new Ant("Ant number: " + (i+1), this.problem.getBeginNodes().get(0)));
			}
		}else if(this.problem.getBeginNodes().size() > 1){
			/* Se não for um problema da mochila e houver mais um nó de origem, as formigas
			 * serão iniciadas de forma uniforme sobre os nós */
			int numberOfAntPerNode = this.problem.getNumberAnt()/this.problem.getBeginNodes().size();
			int count = 1;
			for(int i = 0; i < this.problem.getBeginNodes().size(); i++){
				for(int j = 0; j < numberOfAntPerNode; j++){
					ants.add(new Ant("Ant number: " + (count++), this.problem.getBeginNodes().get(i)));
				}
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
	
	/**
	 * 
	 */
	public void execute(){
		int execution = 0;
		/* Loop by executions */
		while(execution < this.problem.getMaxExecution()){
			int iteration = 0;
			/* Loop by iterations */
			while(iteration < this.problem.getMaxIterations()){
				/* Construção do caminho para todas as formigas. Se o problema
				 * for da mochila, leva-se em consideração o peso de cada elemento
				 * para criar a lista de possíveis nós seguintes. */
				construction();
				/* Evaporação do feromônio */
				makeEvaporation();
				/* Atualização do feromônio */
				makeAtualization();
				iteration++;
			}
			execution++;
		}
	}
	
	/* ######################################################################## */
	/* ######################################################################## */
	
	/** Construction */
	private void construction(){
		if(this.problem.isKnapsackProblem()){
			for(Ant ant : this.ants){
				Node node = chooseNextNode(ant);
				while(node != null){
					ant.setActualNode(node);
					ant.getTabuList().add(node);
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
			if(ant.getTabuList().contains(e.getBegin()) && ant.getTabuList().contains(e.getEnd())){
				iterator.remove();
			}
		}
		/* Calculando o acumulado da formula de probabilidade */
		List<Auxiliar> values = new LinkedList<Auxiliar>();
		double amount = 0.0;
		for(Edge e : edges){
			if(this.useHeuristic){
				amount += Math.pow(e.getPheromone(), this.problem.getAlfa()) * Math.pow(getHeuristicValue(e.getEnd()), this.problem.getBeta());
			}else{
				amount += Math.pow(e.getPheromone(), this.problem.getAlfa());
			}
		}
		/* Valores individuais da formula */
		double sum = 0.0;
		for(Edge e : edges){
			double pheromoneNode = Math.pow(e.getPheromone(), this.problem.getAlfa());
			if(this.useHeuristic){
				double heuristic = Math.pow(getHeuristicValue(e.getEnd()), this.problem.getBeta());
				/* */
				values.add(new Auxiliar(e.getEnd(), ((pheromoneNode * heuristic)/amount) * 100));
				sum += ((pheromoneNode * heuristic)/amount) * 100;
			}else{
				values.add(new Auxiliar(e.getEnd(), (pheromoneNode/amount) * 100));
				sum += (pheromoneNode/amount) * 100;
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
			if(randomValue <= d.getProbabilidade()){
				return d.getNo();
			}
		}
		return null;
	}
	/** Get Heuristic Value */
	private double getHeuristicValue(Node node) {
		return (Double) node.getInformations().get("criticidade")/ (Double) node.getInformations().get("tempo");
	}
	
	/* ######################################################################## */
	/* ######################################################################## */
	

	/** Evaporation */
	private void makeEvaporation(){
		for(Edge e : this.graph.getEdges()){
			e.setPheromone((1 - this.problem.getP()) * e.getPheromone());
		}
	}
	
	/* ######################################################################## */
	/* ######################################################################## */
	
	/**
	 * 
	 */
	private void makeAtualization(){
		
	}
	
	/* ######################################################################## */
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
	}
}
