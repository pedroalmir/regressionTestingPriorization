/**
 * 
 */
package com.pedroalmir.testPriorization.experiments;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.pedroalmir.testPriorization.main.ExecuteRegressionTestingPriorization;
import com.pedroalmir.testPriorization.model.AntConfiguration;
import com.pedroalmir.testPriorization.model.Context;
import com.pedroalmir.testPriorization.model.Solution;


/**
 * @author Pedro Almir
 *
 */
public class HeuristicExperiment {
	/** */
	private final static Integer CLASSES = 1000;
	private final static Integer REQUIREMENTS = 20;
	private final static Integer CAPACIDADE = 500000;
	private static Solution betterSolution = null;
	
	@BeforeClass
	public static void setBetterSolution(){
		betterSolution = ExecuteRegressionTestingPriorization.getExactBetterSolution(new Context(CLASSES, REQUIREMENTS), CAPACIDADE);
	}
	
	@Test
	public void testLitleMoreGreedyAlgorithm() throws IOException {
		/* greedy */
		final double ALPHA = -0.5d;
		/* rapid selection */
		final double BETA = 9.6d;
		/* heuristic parameters */
		final double Q = 0.0001d;
		/* between 0 and 1 */
		final double PHEROMONE_PERSISTENCE = 0.3d;
		final double INITIAL_PHEROMONES = 0.8d;
		final int NUM_AGENTS = 2048;
		
		final int MAX_ITERATIONS = 10;
		final int MAX_EXECUTIONS = 30;
		
		Context context = new Context(CLASSES, REQUIREMENTS);
		AntConfiguration antConfiguration = new AntConfiguration(ALPHA, BETA, Q, PHEROMONE_PERSISTENCE, INITIAL_PHEROMONES, 
				NUM_AGENTS, MAX_ITERATIONS, MAX_EXECUTIONS);
		Solution solution = ExecuteRegressionTestingPriorization.startProcess(context, antConfiguration, CAPACIDADE);
		
		solution.setNumberOfClasses(CLASSES);
		solution.setNumberOfRequirements(REQUIREMENTS);
		solution.setNumberOfTests(REQUIREMENTS);
		
		solution.setAntConfiguration(antConfiguration);
		
		AntConfiguration jACO = new AntConfiguration(0.6, 0.4, 0.1, 0.2, 0.1, 20, MAX_ITERATIONS, MAX_EXECUTIONS);
		Solution anotherSolution = ExecuteRegressionTestingPriorization.startJonathasACO(context, jACO, CAPACIDADE);
		anotherSolution.setAntConfiguration(jACO);
		
		System.out.println("       Exact solution: " + betterSolution);
		System.out.println("         ACO Solution: " + solution);
		System.out.println("Jonathas ACO Solution: " + anotherSolution);
		
		solution.printThis(new File("C:\\Users\\Pedro Almir\\Desktop\\MeusProjetos\\IC_TCC\\Athena\\workspaceKepler\\RegressionTestingPriorization\\results\\resultACO.csv"));
		betterSolution.printThis(new File("C:\\Users\\Pedro Almir\\Desktop\\MeusProjetos\\IC_TCC\\Athena\\workspaceKepler\\RegressionTestingPriorization\\results\\resultBruteForce.csv"));
	}
	
	@Ignore
	@Test
	public void testMoreGreedyAlgorithm() throws IOException {
		/* greedy */
		final double ALPHA = -0.75d;
		/* rapid selection */
		final double BETA = 9.6d;
		/* heuristic parameters */
		final double Q = 0.0001d;
		/* between 0 and 1 */
		final double PHEROMONE_PERSISTENCE = 0.3d;
		final double INITIAL_PHEROMONES = 0.8d;
		final int NUM_AGENTS = 2048;
		
		final int MAX_ITERATIONS = 10;
		final int MAX_EXECUTIONS = 30;
		
		Context context = new Context(CLASSES, REQUIREMENTS);
		AntConfiguration antConfiguration = new AntConfiguration(ALPHA, BETA, Q, PHEROMONE_PERSISTENCE, INITIAL_PHEROMONES, 
				NUM_AGENTS, MAX_ITERATIONS, MAX_EXECUTIONS);
		Solution solution = ExecuteRegressionTestingPriorization.startProcess(context, antConfiguration, CAPACIDADE);
		
		
		AntConfiguration jACO = new AntConfiguration(0.6, 0.4, 0.1, 0.2, 0.1, 20, MAX_ITERATIONS, MAX_EXECUTIONS);
		Solution anotherSolution = ExecuteRegressionTestingPriorization.startJonathasACO(context, jACO, CAPACIDADE);
		
		System.out.println("       Exact solution:" + betterSolution);
		System.out.println("         ACO Solution:" + solution);
		System.out.println("Jonathas ACO Solution:" + anotherSolution);
	}
	
	@Ignore
	@Test
	public void testMuchMoreGreedyAlgorithm() throws IOException {
		/* greedy */
		final double ALPHA = -1.0d;
		/* rapid selection */
		final double BETA = 9.6d;
		/* heuristic parameters */
		final double Q = 0.0001d;
		/* between 0 and 1 */
		final double PHEROMONE_PERSISTENCE = 0.3d;
		final double INITIAL_PHEROMONES = 0.8d;
		final int NUM_AGENTS = 2048;
		
		final int MAX_ITERATIONS = 10;
		final int MAX_EXECUTIONS = 30;
		
		Context context = new Context(CLASSES, REQUIREMENTS);
		AntConfiguration antConfiguration = new AntConfiguration(ALPHA, BETA, Q, PHEROMONE_PERSISTENCE, INITIAL_PHEROMONES, 
				NUM_AGENTS, MAX_ITERATIONS, MAX_EXECUTIONS);
		Solution solution = ExecuteRegressionTestingPriorization.startProcess(context, antConfiguration, CAPACIDADE);
		
		
		AntConfiguration jACO = new AntConfiguration(0.6, 0.4, 0.1, 0.2, 0.1, 20, MAX_ITERATIONS, MAX_EXECUTIONS);
		Solution anotherSolution = ExecuteRegressionTestingPriorization.startJonathasACO(context, jACO, CAPACIDADE);
		
		System.out.println("       Exact solution:" + betterSolution);
		System.out.println("         ACO Solution:" + solution);
		System.out.println("Jonathas ACO Solution:" + anotherSolution);
	}
	
	/*
	 * Next 3 tests are using different rapid selection	
	 */
	@Ignore
	@Test
	public void testSlowRapidSelectionAlgorithm() throws IOException {
		/* greedy */
		final double ALPHA = -0.2d;
		/* rapid selection */
		final double BETA = 1.6d;
		/* heuristic parameters */
		final double Q = 0.0001d;
		/* between 0 and 1 */
		final double PHEROMONE_PERSISTENCE = 0.3d;
		final double INITIAL_PHEROMONES = 0.8d;
		final int NUM_AGENTS = 2048;
		
		final int MAX_ITERATIONS = 10;
		final int MAX_EXECUTIONS = 30;
		
		Context context = new Context(CLASSES, REQUIREMENTS);
		AntConfiguration antConfiguration = new AntConfiguration(ALPHA, BETA, Q, PHEROMONE_PERSISTENCE, INITIAL_PHEROMONES, 
				NUM_AGENTS, MAX_ITERATIONS, MAX_EXECUTIONS);
		Solution solution = ExecuteRegressionTestingPriorization.startProcess(context, antConfiguration, CAPACIDADE);
		
		
		AntConfiguration jACO = new AntConfiguration(0.6, 0.4, 0.1, 0.2, 0.1, 20, MAX_ITERATIONS, MAX_EXECUTIONS);
		Solution anotherSolution = ExecuteRegressionTestingPriorization.startJonathasACO(context, jACO, CAPACIDADE);
		
		System.out.println("       Exact solution:" + betterSolution);
		System.out.println("         ACO Solution:" + solution);
		System.out.println("Jonathas ACO Solution:" + anotherSolution);
	}
	
	@Ignore
	@Test
	public void testNormalRapidSelectionAlgorithm() throws IOException {
		/* greedy */
		final double ALPHA = -0.2d;
		/* rapid selection */
		final double BETA = 3.6d;
		/* heuristic parameters */
		final double Q = 0.0001d;
		/* between 0 and 1 */
		final double PHEROMONE_PERSISTENCE = 0.3d;
		final double INITIAL_PHEROMONES = 0.8d;
		final int NUM_AGENTS = 2048;
		
		final int MAX_ITERATIONS = 10;
		final int MAX_EXECUTIONS = 30;
		
		Context context = new Context(CLASSES, REQUIREMENTS);
		AntConfiguration antConfiguration = new AntConfiguration(ALPHA, BETA, Q, PHEROMONE_PERSISTENCE, INITIAL_PHEROMONES, 
				NUM_AGENTS, MAX_ITERATIONS, MAX_EXECUTIONS);
		Solution solution = ExecuteRegressionTestingPriorization.startProcess(context, antConfiguration, CAPACIDADE);
		
		
		AntConfiguration jACO = new AntConfiguration(0.6, 0.4, 0.1, 0.2, 0.1, 20, MAX_ITERATIONS, MAX_EXECUTIONS);
		Solution anotherSolution = ExecuteRegressionTestingPriorization.startJonathasACO(context, jACO, CAPACIDADE);
		
		System.out.println("       Exact solution:" + betterSolution);
		System.out.println("         ACO Solution:" + solution);
		System.out.println("Jonathas ACO Solution:" + anotherSolution);
	}
	
	@Ignore
	@Test
	public void testFastRapidSelectionAlgorithm() throws IOException {
		/* greedy */
		final double ALPHA = -0.2d;
		/* rapid selection */
		final double BETA = 12.6d;
		/* heuristic parameters */
		final double Q = 0.0001d;
		/* between 0 and 1 */
		final double PHEROMONE_PERSISTENCE = 0.3d;
		final double INITIAL_PHEROMONES = 0.8d;
		final int NUM_AGENTS = 2048;
		
		final int MAX_ITERATIONS = 10;
		final int MAX_EXECUTIONS = 30;
		
		Context context = new Context(CLASSES, REQUIREMENTS);
		AntConfiguration antConfiguration = new AntConfiguration(ALPHA, BETA, Q, PHEROMONE_PERSISTENCE, INITIAL_PHEROMONES, 
				NUM_AGENTS, MAX_ITERATIONS, MAX_EXECUTIONS);
		Solution solution = ExecuteRegressionTestingPriorization.startProcess(context, antConfiguration, CAPACIDADE);
		
		
		AntConfiguration jACO = new AntConfiguration(0.6, 0.4, 0.1, 0.2, 0.1, 20, MAX_ITERATIONS, MAX_EXECUTIONS);
		Solution anotherSolution = ExecuteRegressionTestingPriorization.startJonathasACO(context, jACO, CAPACIDADE);
		
		System.out.println("       Exact solution:" + betterSolution);
		System.out.println("         ACO Solution:" + solution);
		System.out.println("Jonathas ACO Solution:" + anotherSolution);
	}
	
	/*
	 * Next 3 methods are testing the pheromone persistence
	 */
	@Ignore
	@Test
	public void testWithSmallInitialPheromones() throws IOException {
		/* greedy */
		final double ALPHA = -0.2d;
		/* rapid selection */
		final double BETA = 9.6d;
		/* heuristic parameters */
		final double Q = 0.0001d;
		/* between 0 and 1 */
		final double PHEROMONE_PERSISTENCE = 0.1d;
		final double INITIAL_PHEROMONES = 0.8d;
		final int NUM_AGENTS = 2048;
		
		final int MAX_ITERATIONS = 10;
		final int MAX_EXECUTIONS = 30;
		
		Context context = new Context(CLASSES, REQUIREMENTS);
		AntConfiguration antConfiguration = new AntConfiguration(ALPHA, BETA, Q, PHEROMONE_PERSISTENCE, INITIAL_PHEROMONES, 
				NUM_AGENTS, MAX_ITERATIONS, MAX_EXECUTIONS);
		Solution solution = ExecuteRegressionTestingPriorization.startProcess(context, antConfiguration, CAPACIDADE);
		
		
		AntConfiguration jACO = new AntConfiguration(0.6, 0.4, 0.1, 0.2, 0.1, 20, MAX_ITERATIONS, MAX_EXECUTIONS);
		Solution anotherSolution = ExecuteRegressionTestingPriorization.startJonathasACO(context, jACO, CAPACIDADE);
		
		System.out.println("       Exact solution:" + betterSolution);
		System.out.println("         ACO Solution:" + solution);
		System.out.println("Jonathas ACO Solution:" + anotherSolution);
	}
	
	@Ignore
	@Test
	public void testWithLitleMoreInitialPheromones() throws IOException {
		/* greedy */
		final double ALPHA = -0.2d;
		/* rapid selection */
		final double BETA = 9.6d;
		/* heuristic parameters */
		final double Q = 0.0001d;
		/* between 0 and 1 */
		final double PHEROMONE_PERSISTENCE = 0.5d;
		final double INITIAL_PHEROMONES = 0.8d;
		final int NUM_AGENTS = 2048;
		
		final int MAX_ITERATIONS = 10;
		final int MAX_EXECUTIONS = 30;
		
		Context context = new Context(CLASSES, REQUIREMENTS);
		AntConfiguration antConfiguration = new AntConfiguration(ALPHA, BETA, Q, PHEROMONE_PERSISTENCE, INITIAL_PHEROMONES, 
				NUM_AGENTS, MAX_ITERATIONS, MAX_EXECUTIONS);
		Solution solution = ExecuteRegressionTestingPriorization.startProcess(context, antConfiguration, CAPACIDADE);
		
		
		AntConfiguration jACO = new AntConfiguration(0.6, 0.4, 0.1, 0.2, 0.1, 20, MAX_ITERATIONS, MAX_EXECUTIONS);
		Solution anotherSolution = ExecuteRegressionTestingPriorization.startJonathasACO(context, jACO, CAPACIDADE);
		
		System.out.println("       Exact solution:" + betterSolution);
		System.out.println("         ACO Solution:" + solution);
		System.out.println("Jonathas ACO Solution:" + anotherSolution);
	}
	
	@Ignore
	@Test
	public void testWithLargeInitialPheromones() throws IOException {
		/* greedy */
		final double ALPHA = -0.2d;
		/* rapid selection */
		final double BETA = 9.6d;
		/* heuristic parameters */
		final double Q = 0.0001d;
		/* between 0 and 1 */
		final double PHEROMONE_PERSISTENCE = 1.0d;
		final double INITIAL_PHEROMONES = 0.8d;
		final int NUM_AGENTS = 2048;
		
		final int MAX_ITERATIONS = 10;
		final int MAX_EXECUTIONS = 30;
		
		Context context = new Context(CLASSES, REQUIREMENTS);
		AntConfiguration antConfiguration = new AntConfiguration(ALPHA, BETA, Q, PHEROMONE_PERSISTENCE, INITIAL_PHEROMONES, 
				NUM_AGENTS, MAX_ITERATIONS, MAX_EXECUTIONS);
		Solution solution = ExecuteRegressionTestingPriorization.startProcess(context, antConfiguration, CAPACIDADE);
		
		
		AntConfiguration jACO = new AntConfiguration(0.6, 0.4, 0.1, 0.2, 0.1, 20, MAX_ITERATIONS, MAX_EXECUTIONS);
		Solution anotherSolution = ExecuteRegressionTestingPriorization.startJonathasACO(context, jACO, CAPACIDADE);
		
		System.out.println("       Exact solution:" + betterSolution);
		System.out.println("         ACO Solution:" + solution);
		System.out.println("Jonathas ACO Solution:" + anotherSolution);
	}
	
	/*
	 * Next 3 methods are testing the initial pheromones
	 */
	@Ignore
	@Test
	public void testWithSmallPheromonePersistence() throws IOException {
		/* greedy */
		final double ALPHA = -0.2d;
		/* rapid selection */
		final double BETA = 5.0d;
		/* heuristic parameters */
		final double Q = 0.0001d;
		/* between 0 and 1 */
		final double PHEROMONE_PERSISTENCE = 0.3d;
		final double INITIAL_PHEROMONES = 0.5d;
		final int NUM_AGENTS = 2048;
		
		final int MAX_ITERATIONS = 10;
		final int MAX_EXECUTIONS = 30;
		
		Context context = new Context(CLASSES, REQUIREMENTS);
		AntConfiguration antConfiguration = new AntConfiguration(ALPHA, BETA, Q, PHEROMONE_PERSISTENCE, INITIAL_PHEROMONES, 
				NUM_AGENTS, MAX_ITERATIONS, MAX_EXECUTIONS);
		Solution solution = ExecuteRegressionTestingPriorization.startProcess(context, antConfiguration, CAPACIDADE);
		
		
		AntConfiguration jACO = new AntConfiguration(0.6, 0.4, 0.1, 0.2, 0.1, 20, MAX_ITERATIONS, MAX_EXECUTIONS);
		Solution anotherSolution = ExecuteRegressionTestingPriorization.startJonathasACO(context, jACO, CAPACIDADE);
		
		System.out.println("       Exact solution:" + betterSolution);
		System.out.println("         ACO Solution:" + solution);
		System.out.println("Jonathas ACO Solution:" + anotherSolution);
	}
	
	@Ignore
	@Test
	public void testWithLitleMorePheromonePersistence() throws IOException {
		/* greedy */
		final double ALPHA = -0.2d;
		/* rapid selection */
		final double BETA = 10.0d;
		/* heuristic parameters */
		final double Q = 0.0001d;
		/* between 0 and 1 */
		final double PHEROMONE_PERSISTENCE = 0.3d;
		final double INITIAL_PHEROMONES = 1.0d;
		final int NUM_AGENTS = 2048;
		
		final int MAX_ITERATIONS = 10;
		final int MAX_EXECUTIONS = 30;
		
		Context context = new Context(CLASSES, REQUIREMENTS);
		AntConfiguration antConfiguration = new AntConfiguration(ALPHA, BETA, Q, PHEROMONE_PERSISTENCE, INITIAL_PHEROMONES, 
				NUM_AGENTS, MAX_ITERATIONS, MAX_EXECUTIONS);
		Solution solution = ExecuteRegressionTestingPriorization.startProcess(context, antConfiguration, CAPACIDADE);
		
		
		AntConfiguration jACO = new AntConfiguration(0.6, 0.4, 0.1, 0.2, 0.1, 20, MAX_ITERATIONS, MAX_EXECUTIONS);
		Solution anotherSolution = ExecuteRegressionTestingPriorization.startJonathasACO(context, jACO, CAPACIDADE);
		
		System.out.println("       Exact solution:" + betterSolution);
		System.out.println("         ACO Solution:" + solution);
		System.out.println("Jonathas ACO Solution:" + anotherSolution);
	}
	
	@Ignore
	@Test
	public void testWithLargePheromonePersistence() throws IOException {
		/* greedy */
		final double ALPHA = -0.2d;
		/* rapid selection */
		final double BETA = 15.0d;
		/* heuristic parameters */
		final double Q = 0.0001d;
		/* between 0 and 1 */
		final double PHEROMONE_PERSISTENCE = 0.3d;
		final double INITIAL_PHEROMONES = 1.5d;
		final int NUM_AGENTS = 2048;
		
		final int MAX_ITERATIONS = 10;
		final int MAX_EXECUTIONS = 30;
		
		Context context = new Context(CLASSES, REQUIREMENTS);
		AntConfiguration antConfiguration = new AntConfiguration(ALPHA, BETA, Q, PHEROMONE_PERSISTENCE, INITIAL_PHEROMONES, 
				NUM_AGENTS, MAX_ITERATIONS, MAX_EXECUTIONS);
		Solution solution = ExecuteRegressionTestingPriorization.startProcess(context, antConfiguration, CAPACIDADE);
		
		
		AntConfiguration jACO = new AntConfiguration(0.6, 0.4, 0.1, 0.2, 0.1, 20, MAX_ITERATIONS, MAX_EXECUTIONS);
		Solution anotherSolution = ExecuteRegressionTestingPriorization.startJonathasACO(context, jACO, CAPACIDADE);
		
		System.out.println("       Exact solution:" + betterSolution);
		System.out.println("         ACO Solution:" + solution);
		System.out.println("Jonathas ACO Solution:" + anotherSolution);
	}

}
