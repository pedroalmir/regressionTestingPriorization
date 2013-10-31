/**
 * 
 */
package com.pedroalmir.testPriorization.main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.jFuzzyLogic.FIS;
import au.com.bytecode.opencsv.CSVWriter;

import com.pedroalmir.testPriorization.flow.ant.AntColonySystemAlgorithm;
import com.pedroalmir.testPriorization.flow.antSystem.factory.ProblemFactory;
import com.pedroalmir.testPriorization.flow.antSystem.model.problem.Problem;
import com.pedroalmir.testPriorization.flow.brute.BruteForce;
import com.pedroalmir.testPriorization.flow.guloso.MochilaGulosa;
import com.pedroalmir.testPriorization.flow.parser.InputParser;
import com.pedroalmir.testPriorization.model.Klass;
import com.pedroalmir.testPriorization.model.RegressionTestingPriorizationProblem;
import com.pedroalmir.testPriorization.model.Requirement;
import com.pedroalmir.testPriorization.model.Solution;
import com.pedroalmir.testPriorization.model.TestCase;
import com.pedroalmir.testPriorization.model.solution.IterationSolution;

/**
 * @author Pedro Almir
 *
 */
public class ExecuteRegressionTestingPriorization {
	
	private final static String BASE_FOLDER = "C:/Users/Pedro Almir/Dropbox/UFPI/Mestrado/Priorização de Casos de Teste/Arquivos Base/AleatoriosNovaAbordagem/cl_1000_req_28/";
	private final static String COUPLING_FILE = "Coupling-cl-1000.csv";
	private final static String SQFD_FILE = "SQFD-cl-1000-req-28.csv";
	private final static String TEST_COVERAGE_FILE = "TestCoverage-cl-1000-req-28.csv";
	private final static String CYCLOMATIC_FILE = "CyclomaticComplexity-cl-1000.csv";
	private final static Integer CAPACIDADE = 800;
	@SuppressWarnings("unused")
	private final static Integer CLASS_NUMBER = 1000;
	@SuppressWarnings("unused")
	private final static Integer REQUIREMENT_NUMBER = 25;
	
	private final static String FCL_PATH = "C:/Users/Pedro Almir/Desktop/MeusProjetos/IC_TCC/Athena/workspaceKepler/RegressionTestingPriorization/data/RegressionTestingPriorization.fcl";
	
	public static void main(String[] args) {
		try {
			RegressionTestingPriorizationProblem problem = InputParser.createProblem(BASE_FOLDER, COUPLING_FILE, SQFD_FILE, TEST_COVERAGE_FILE, CYCLOMATIC_FILE);
			problem.setCapacidade(CAPACIDADE);
			
			executeMySytem(problem);
			//executeBuscaGulosa(problem);
			//executeBruteForce(problem);
		} catch (IOException e) {
			e.printStackTrace();
		}
				
	}
	
	private static void executeMySytem(RegressionTestingPriorizationProblem problem) throws IOException{
		long begin = System.currentTimeMillis();
		/* execute step one: SQFD */
		executeSQFDAnalysis(problem);
		/* execute step two: Fuzzy System */
		executeFuzzySystem(problem);
		/* execute step three: Calculating the criticality for the test cases */
		executeCalculateCriticalityForTestCases(problem);
		/* execute step four: Ant Colony System */
		Solution solution = executeAntColonySystem(problem);
		solution.setExecutionTime(System.currentTimeMillis()-begin);
		System.out.println(solution.toString());
		
		/* ###################################################################### */
		List<String[]> data = new ArrayList<String[]>();
        CSVWriter writer = new CSVWriter(new FileWriter("results/AntColonyBest.csv"), ';');
        String[] firstLine = new String[solution.getIterationSolutions().size()+1];
        firstLine[0] = "";
        String[] secondLine = new String[solution.getIterationSolutions().size()+1];
        secondLine[0] = "BetterSolution";
        String[] thirdLine = new String[solution.getIterationSolutions().size()+1];
        thirdLine[0] = "WorstSolution";
        String[] fourthLine = new String[solution.getIterationSolutions().size()+1];
        fourthLine[0] = "Average";
        String[] fifthLine = new String[solution.getIterationSolutions().size()+1];
        fifthLine[0] = "StandardDeviation";
		/* ###################################################################### */
		int count = 1;
		for(IterationSolution i : solution.getIterationSolutions()){
			//System.out.println(i);
			firstLine[count] = "Iteration " + count;
	        secondLine[count] = (i.getBetterSolution() + "").replaceAll("\\.", ",");
	        thirdLine[count] = (i.getWorstSolution() + "").replaceAll("\\.", ",");
	        fourthLine[count] = (i.getAverage() + "").replaceAll("\\.", ",");
	        fifthLine[count] = (i.getStandardDeviation() + "").replaceAll("\\.", ",");
	        count++;
		}
		/* ###################################################################### */
		data.add(new String[31]);
		data.add(firstLine);
		data.add(secondLine);
		data.add(thirdLine);
		data.add(fourthLine);
		data.add(fifthLine);
		/* ###################################################################### */
		writer.writeAll(data);
        writer.close();
	}
	
	private static void executeBruteForce(RegressionTestingPriorizationProblem problem){
		long begin = System.currentTimeMillis();
		/* execute step four: Brute Force */
		Solution solution = BruteForce.execute(problem);
		solution.setExecutionTime(System.currentTimeMillis()-begin);
		System.out.println(solution.toString());
	}
	
	private static void executeBuscaGulosa(RegressionTestingPriorizationProblem problem){
		long begin = System.currentTimeMillis();
		/* execute step four: Brute Force */
		Solution solution = MochilaGulosa.execute(problem);
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		solution.setExecutionTime(System.currentTimeMillis()-begin);
		System.out.println(solution.toString());
	}

	/**
	 * Execute step four: Ant Colony System
	 * @param problem
	 */
	private static Solution executeAntColonySystem(RegressionTestingPriorizationProblem problem) {
		/* Número de formigas */
		int numberAnt = 5;
		/* Quantidade máxima de ferômonio (0, 0.1) */
		double t0 = 0.1;
		/* Constante que amplifica a influência da concentração de feromonio. */
		double alfa = 0.6;
		/* Constante que amplifica a influência da heurística. */
		double beta = 0.4;
		/**/
		double q = 10;
		/**/
        int maxIterations = 10;
        /**/
        int maxExecution = 30;
        /**/
        double p1 = 0.5, p2 = 0.5, q0 = 0.5, constantQ = 1, cargaMaxima = 800;
        int k = 0;
        
		Problem antProblem = ProblemFactory.createProblem(numberAnt, t0, alfa, beta, q, maxIterations, maxExecution, 
				p1, p2, q0, constantQ, k, cargaMaxima, problem.getTestCases());
		// ExecuteAntColonySystem.executeAlgorithm(antProblem);
		try {
			return AntColonySystemAlgorithm.executeAntSystem(antProblem, problem);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

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
