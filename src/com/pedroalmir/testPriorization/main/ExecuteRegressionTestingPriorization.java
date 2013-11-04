/**
 * 
 */
package com.pedroalmir.testPriorization.main;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sourceforge.jFuzzyLogic.FIS;

import com.pedroalmir.testPriorization.flow.ant.AntColonySystemAlgorithm;
import com.pedroalmir.testPriorization.flow.antSystem.core.KnapsackController;
import com.pedroalmir.testPriorization.flow.antSystem.factory.ProblemFactory;
import com.pedroalmir.testPriorization.flow.antSystem.model.graph.Edge;
import com.pedroalmir.testPriorization.flow.antSystem.model.graph.Graph;
import com.pedroalmir.testPriorization.flow.antSystem.model.graph.Node;
import com.pedroalmir.testPriorization.flow.antSystem.model.problem.Problem;
import com.pedroalmir.testPriorization.flow.brute.BruteForce;
import com.pedroalmir.testPriorization.flow.guloso.MochilaGulosa;
import com.pedroalmir.testPriorization.flow.parser.InputParser;
import com.pedroalmir.testPriorization.model.AntConfiguration;
import com.pedroalmir.testPriorization.model.Context;
import com.pedroalmir.testPriorization.model.Klass;
import com.pedroalmir.testPriorization.model.RegressionTestingPriorizationProblem;
import com.pedroalmir.testPriorization.model.Requirement;
import com.pedroalmir.testPriorization.model.Solution;
import com.pedroalmir.testPriorization.model.TestCase;

/**
 * @author Pedro Almir
 *
 */
public class ExecuteRegressionTestingPriorization {
	
	/** */
	private final static String BASE_FOLDER = "C:/Users/Pedro Almir/Dropbox/UFPI/Mestrado/Priorização de Casos de Teste/Arquivos Base/AleatoriosDistNormal/cl_1000_req_20/";
	private final static String COUPLING_FILE = "New-Coupling-cl-1000.csv";
	private final static String SQFD_FILE = "New-SQFD-cl-1000-req-20.csv";
	private final static String TEST_COVERAGE_FILE = "New-TestCoverage-cl-1000-req-20.csv";
	private final static String CYCLOMATIC_FILE = "CyclomaticComplexity-cl-1000.csv";
	/** */
	private final static String FCL_PATH = "C:/Users/Pedro Almir/Desktop/MeusProjetos/IC_TCC/Athena/workspaceKepler/RegressionTestingPriorization/data/RegressionTestingPriorization.fcl";
	/** */
	private final static Integer CAPACIDADE = 500000;
	
	public static void main(String[] args) throws Exception {
		try {
			RegressionTestingPriorizationProblem problem = InputParser.createProblem(BASE_FOLDER, COUPLING_FILE, SQFD_FILE, TEST_COVERAGE_FILE, CYCLOMATIC_FILE);
			problem.setCapacidade(CAPACIDADE);
			/*  */
			executeAntColonyOptimization(problem);
			executeMySytem(problem);
			executeBruteForce(problem, true);
			executeBuscaGulosa(problem);
		} catch (IOException e) {
			e.printStackTrace();
		}
				
	}
	
	public static Solution startProcess(Context context, AntConfiguration antConfiguration, Integer capacidade){
		RegressionTestingPriorizationProblem problem = InputParser.createProblem(context, capacidade);
		return executeAntColonyOptimization(problem, antConfiguration, capacidade, context.getFclFile());
	}
	
	public static Solution startJonathasACO(Context context, AntConfiguration antConfiguration, Integer capacidade){
		try {
			RegressionTestingPriorizationProblem problem = InputParser.createProblem(context, capacidade);
			return executeMySytem(problem, antConfiguration, capacidade, context.getFclFile());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Solution getExactBetterSolution(Context context, Integer capacidade){
		RegressionTestingPriorizationProblem problem = InputParser.createProblem(context, capacidade);
		
		long begin = System.currentTimeMillis();
		/* execute step one: SQFD */
		executeSQFDAnalysis(problem);
		/* execute step two: Fuzzy System */
		executeFuzzySystem(problem, context.getFclFile());
		/* execute step three: Calculating the criticality for the test cases */
		executeCalculateCriticalityForTestCases(problem);
		/* */
		problem.setPartial(System.currentTimeMillis() - begin);
		
		return executeBruteForce(problem, false);
	}
	
	/* ################################################################################################################################################ */
	/* ### Ant Colony Optimization: By Pedro Almir.																									### */
	/* ################################################################################################################################################ */
	
	
	private static Solution executeAntColonyOptimization(RegressionTestingPriorizationProblem problem, AntConfiguration antConfiguration, int capacidade, 
			String fclPath) {
		long begin = System.currentTimeMillis();
		/* execute step one: SQFD */
		executeSQFDAnalysis(problem);
		/* execute step two: Fuzzy System */
		executeFuzzySystem(problem, fclPath);
		/* execute step three: Calculating the criticality for the test cases */
		executeCalculateCriticalityForTestCases(problem);
		/* */
		problem.setPartial(System.currentTimeMillis() - begin);
		/* execute step four: Ant Colony System */
		Problem p = new Problem(antConfiguration.getNumAgents(), antConfiguration.getInitialPheromone(), antConfiguration.getAlpha(), 
				antConfiguration.getBeta(), antConfiguration.getQ(), antConfiguration.getMaxIterations(), antConfiguration.getMaxExecutions(), 
				antConfiguration.getPheromonePersistence(), 0.0, capacidade);
		p.setKnapsackProblem(true);
		p.setTestCases(problem.getTestCases());
		
		List<Node> nodes = new LinkedList<Node>();
		List<Edge> edges = new LinkedList<Edge>();
		
		/* Vertex com id, criticidade, tempo e feromonio inicial */
		for (int i = 0; i < problem.getTestCases().size(); i++) {
			Map<String, Object> info = new LinkedHashMap<String, Object>();
			info.put("criticidade", problem.getTestCases().get(i).getCriticality());
			info.put("tempo", problem.getTestCases().get(i).getTime());
			info.put("testCase", problem.getTestCases().get(i));
			
			nodes.add(new Node(Long.valueOf(i+1), info));
		}
		/* */
		for (int i = 0; i < problem.getTestCases().size(); i++) {
			for (int j = 0; j < problem.getTestCases().size(); j++) {
				if (i != j) {
					Edge edge = new Edge("Edge[" + (i+1) + "-" + (j+1) + "]", nodes.get(i), nodes.get(j));
					nodes.get(i).getAdjacentEdges().add(edge);
					nodes.get(j).getAdjacentEdges().add(edge);
					edges.add(edge);
				}
			}
		}
		/* */
		Graph graph = new Graph(1L, "", nodes, edges);
		p.setGraph(graph);
		
		/* execute step four: Ant Colony System */
		KnapsackController controller = new KnapsackController(graph, p);
		Solution solution = controller.execute();
		solution.setExecutionTime(System.currentTimeMillis() - begin);
		//System.out.println(solution.toString());
		return solution;
	}

	private static void executeAntColonyOptimization(RegressionTestingPriorizationProblem problem){
		long begin = System.currentTimeMillis();
		/* execute step one: SQFD */
		executeSQFDAnalysis(problem);
		/* execute step two: Fuzzy System */
		executeFuzzySystem(problem);
		/* execute step three: Calculating the criticality for the test cases */
		executeCalculateCriticalityForTestCases(problem);
		/* */
		problem.setPartial(System.currentTimeMillis() - begin);
		/* execute step four: Ant Colony System */
		/* greedy */
		double ALPHA = 0.5d;
		/* rapid selection */
		double BETA = 2.0d;
		/* heuristic parameters. Somewhere between 0 and 1 */
		double Q = 0.0001d;
		/* between 0 and 1 */
		double PHEROMONE_PERSISTENCE = 0.3d;
		/* can be anything */
		double INITIAL_PHEROMONES = 0.8d;
		/* use power of 2 */
		int NUM_AGENTS = 2048;
		
		Problem p = new Problem(NUM_AGENTS, INITIAL_PHEROMONES, ALPHA, BETA, Q, 10, 30, PHEROMONE_PERSISTENCE, 0.0, CAPACIDADE);
		p.setKnapsackProblem(true);
		p.setTestCases(problem.getTestCases());
		
		List<Node> nodes = new LinkedList<Node>();
		List<Edge> edges = new LinkedList<Edge>();
		
		/* Vertex com id, criticidade, tempo e feromonio inicial */
		for (int i = 0; i < problem.getTestCases().size(); i++) {
			Map<String, Object> info = new LinkedHashMap<String, Object>();
			info.put("criticidade", problem.getTestCases().get(i).getCriticality());
			info.put("tempo", problem.getTestCases().get(i).getTime());
			info.put("testCase", problem.getTestCases().get(i));
			
			nodes.add(new Node(Long.valueOf(i+1), info));
		}
		/* */
		for (int i = 0; i < problem.getTestCases().size(); i++) {
			for (int j = 0; j < problem.getTestCases().size(); j++) {
				if (i != j) {
					Edge edge = new Edge("Edge[" + (i+1) + "-" + (j+1) + "]", nodes.get(i), nodes.get(j));
					nodes.get(i).getAdjacentEdges().add(edge);
					nodes.get(j).getAdjacentEdges().add(edge);
					edges.add(edge);
				}
			}
		}
		/* */
		Graph graph = new Graph(1L, "", nodes, edges);
		p.setGraph(graph);
		
		/* execute step four: Ant Colony System */
		KnapsackController controller = new KnapsackController(graph, p);
		Solution solution = controller.execute();
		solution.setExecutionTime(System.currentTimeMillis() - begin);
		System.out.println(solution.toString());
	}
	
	/* ################################################################################################################################################ */
	/* ### Ant Colony Optimization: Implementado pelo Jonathas.																						### */
	/* ################################################################################################################################################ */
	
	
	private static Solution executeMySytem(RegressionTestingPriorizationProblem problem, AntConfiguration antConfiguration, int capacidade, 
			String fclPath) throws Exception{
		long begin = System.currentTimeMillis();
		/* execute step one: SQFD */
		executeSQFDAnalysis(problem);
		/* execute step two: Fuzzy System */
		executeFuzzySystem(problem, fclPath);
		/* execute step three: Calculating the criticality for the test cases */
		executeCalculateCriticalityForTestCases(problem);
		
		/* Número de formigas */
		int numberAnt = antConfiguration.getNumAgents();
		/* Quantidade máxima de ferômonio (0, 0.1) */
		double t0 = antConfiguration.getInitialPheromone();
		/* Constante que amplifica a influência da concentração de feromonio. */
		double alfa = antConfiguration.getAlpha();
		/* Constante que amplifica a influência da heurística. */
		double beta = antConfiguration.getBeta();
		/* Q */
		double q = antConfiguration.getQ();
		/* Número máximo de iterações */
        int maxIterations = antConfiguration.getMaxIterations();
        /* Número máximo de execuções */
        int maxExecution = antConfiguration.getMaxExecutions();
        /* Taxa de Evaporação */
		double p1 = antConfiguration.getPheromonePersistence(), q0 = 0.5;
        
		Problem antProblem = ProblemFactory.createProblem(numberAnt, t0, alfa, beta, q, maxIterations, maxExecution, p1, q0, capacidade, problem.getTestCases());
		
		Solution solution = AntColonySystemAlgorithm.executeAntSystem(antProblem, problem);
		solution.setExecutionTime((System.currentTimeMillis() - begin));
		//System.out.println(solution.toString());
		return solution;
	}
	
	private static void executeMySytem(RegressionTestingPriorizationProblem problem) throws Exception{
		long begin = System.currentTimeMillis();
		/* execute step one: SQFD */
		//executeSQFDAnalysis(problem);
		/* execute step two: Fuzzy System */
		//executeFuzzySystem(problem);
		/* execute step three: Calculating the criticality for the test cases */
		//executeCalculateCriticalityForTestCases(problem);
		
		/* Número de formigas */
		int numberAnt = 20;
		/* Quantidade máxima de ferômonio (0, 0.1) */
		double t0 = 0.1;
		/* Constante que amplifica a influência da concentração de feromonio. */
		double alfa = 0.6;
		/* Constante que amplifica a influência da heurística. */
		double beta = 0.4;
		/*  */
		double q = 0.1;
		/* Número máximo de iterações */
        int maxIterations = 10;
        /* Número máximo de execuções */
        int maxExecution = 30;
        /**/
		double p1 = 0.2, q0 = 0.5;
        
		Problem antProblem = ProblemFactory.createProblem(numberAnt, t0, alfa, beta, q, maxIterations, maxExecution, p1, q0, CAPACIDADE, problem.getTestCases());
		
		Solution solution = AntColonySystemAlgorithm.executeAntSystem(antProblem, problem);
		solution.setExecutionTime((System.currentTimeMillis() - begin) + problem.getPartial());
		System.out.println(solution.toString());
	}
	
	/* ################################################################################################################################################ */
	/* ### Força Bruta: Varre todo o espaço de busca procurando a melhor solução.																	### */
	/* ################################################################################################################################################ */
	
	private static Solution executeBruteForce(RegressionTestingPriorizationProblem problem, boolean debug){
		long begin = System.currentTimeMillis();
		/* execute step four: Brute Force */
		Solution solution = BruteForce.execute(problem);
		solution.setExecutionTime((System.currentTimeMillis() - begin) + problem.getPartial());
		if(debug){
			System.out.println(solution.toString());
		}
		return solution;
	}
	
	/* ################################################################################################################################################ */
	/* ### Busca Gulosa																																### */
	/* ################################################################################################################################################ */
	
	private static void executeBuscaGulosa(RegressionTestingPriorizationProblem problem){
		long begin = System.currentTimeMillis();
		/* execute step four: Brute Force */
		Solution solution = MochilaGulosa.execute(problem);
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		solution.setExecutionTime((System.currentTimeMillis() - begin) + problem.getPartial());
		System.out.println(solution.toString());
	}

	/* ################################################################################################################################################ */
	/* ### Calculo da Criticidade dos casos de teste. Somatório da criticidade das classes, multiplicado pela cobertura do teste.					### */
	/* ################################################################################################################################################ */
	
	/**
	 * Execute step three: Calculating the criticality for the test cases
	 * @param problem
	 */
	private static void executeCalculateCriticalityForTestCases(RegressionTestingPriorizationProblem problem) {
		/* */
		for(TestCase testCase : problem.getTestCases()){
			double criticality = 0.0;
			double amount = 0.0;
			for(Klass klass : testCase.getCoverage().keySet()){
				criticality += testCase.getCoverage().get(klass) * klass.getCriticality();
				amount += klass.getCriticality();
			}
			testCase.setCriticality(criticality/amount);
			/* Just for debug */
			//String value = criticality/amount + "";
			//System.out.println(value.replace(".",  ","));
		}
	}
	
	/* ################################################################################################################################################ */
	/* ### Fuzzy System: Utilizado para inferir a criticidade das classes com base em três parâmetros. 1-Acoplamento, 2-Relevância e 3-Complexidade	### */
	/* ################################################################################################################################################ */

	/**
	 * Execute step two: Fuzzy System
	 * @param problem
	 */
	private static void executeFuzzySystem(RegressionTestingPriorizationProblem problem) {
		/* Load from 'FCL' file */
		FIS fis = FIS.load(FCL_PATH, true);
		System.out.println();
		for(Klass klass : problem.getKlasses()){
			/* Set inputs [Importancia x Acoplamento] of klass */
			fis.setVariable("Importancia", klass.getRelevanceNormalized());
			fis.setVariable("Acoplamento", klass.getCoupling());
			fis.setVariable("Complexidade", klass.getCyclomaticComplexityNormalized());
			/* Evaluate */
	        fis.evaluate();
	        double defuzzifiedValue = fis.getVariable("CriticidadeClasse").getLatestDefuzzifiedValue();
	        klass.setCriticality(defuzzifiedValue);
	        /* Just for debug */
	        //String value = defuzzifiedValue + ";";
	        //System.out.print(value.replace(".", ","));
		}
	}
	
	/**
	 * Execute step two: Fuzzy System
	 * @param problem
	 * @param fclPath
	 */
	private static void executeFuzzySystem(RegressionTestingPriorizationProblem problem, String fclPath) {
		/* Load from 'FCL' file */
		FIS fis = FIS.load(fclPath, true);
		System.out.println();
		for(Klass klass : problem.getKlasses()){
			/* Set inputs [Importancia x Acoplamento] of klass */
			fis.setVariable("Importancia", klass.getRelevanceNormalized());
			fis.setVariable("Acoplamento", klass.getCoupling());
			fis.setVariable("Complexidade", klass.getCyclomaticComplexityNormalized());
			/* Evaluate */
	        fis.evaluate();
	        double defuzzifiedValue = fis.getVariable("CriticidadeClasse").getLatestDefuzzifiedValue();
	        klass.setCriticality(defuzzifiedValue);
		}
	}
	
	/* ################################################################################################################################################ */
	/* ### SQFD: Utilizado para cruzar a correlação das classes com os requisitos e extrair a relevância, a partir da importância dada pelo cliente ### */
	/* ################################################################################################################################################ */

	/**
	 * Execute step one: SQFD
	 * @param problem
	 */
	private static void executeSQFDAnalysis(RegressionTestingPriorizationProblem problem) {
		double criticality = 0.0;
		double bigger = Double.MIN_VALUE;
		for(Klass klass : problem.getKlasses()){
			for(Requirement req : klass.getCorrelation().keySet()){
				criticality += klass.getCorrelation().get(req) * req.getImportance();
			}
			if(criticality > bigger){
				bigger = criticality;
			}
			klass.setRelevance(criticality);
			criticality = 0.0;
		}
		/* Normalize [0..10] */
		for(Klass klass : problem.getKlasses()){
			klass.setRelevanceNormalized(((klass.getRelevance()*10)/bigger));
			/* Just for debug */
			//System.out.print(klass.getRelevanceNormalized() + " - ");
		}
	}
}
