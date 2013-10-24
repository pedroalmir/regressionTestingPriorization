/**
 * 
 */
package com.pedroalmir.testPriorization.main;

import java.io.IOException;
import java.text.SimpleDateFormat;

import net.sourceforge.jFuzzyLogic.FIS;

import com.pedroalmir.testPriorization.flow.ant.AntColonySystemAlgorithm;
import com.pedroalmir.testPriorization.flow.antSystem.factory.ProblemFactory;
import com.pedroalmir.testPriorization.flow.antSystem.model.Problem;
import com.pedroalmir.testPriorization.flow.brute.BruteForce;
import com.pedroalmir.testPriorization.flow.guloso.MochilaGulosa;
import com.pedroalmir.testPriorization.flow.parser.InputParser;
import com.pedroalmir.testPriorization.model.Klass;
import com.pedroalmir.testPriorization.model.RegressionTestingPriorizationProblem;
import com.pedroalmir.testPriorization.model.Requirement;
import com.pedroalmir.testPriorization.model.TestCase;

/**
 * @author Pedro Almir
 *
 */
public class ExecuteRegressionTestingPriorization {
	
	private final static String BASE_FOLDER = "C:/Users/Pedro Almir/Dropbox/UFPI/Mestrado/Priorização de Casos de Teste/Arquivos Base/Aleatorios/DadosAleatorios/Dados_1000_cl_100_req/";
	private final static String COUPLING_FILE = "Coupling-cl-1000.csv";
	private final static String SQFD_FILE = "SQFD-cl-1000-req-100.csv";
	private final static String TEST_COVERAGE_FILE = "TestCoverage-cl-1000-req-100.csv";
	private final static Integer CLASS_NUMBER = 1000;
	private final static Integer REQUIREMENT_NUMBER = 20;
	
	private final static String FCL_PATH = "C:/Users/Pedro Almir/Desktop/MeusProjetos/IC_TCC/Athena/workspaceKepler/RegressionTestingPriorization/data/RegressionTestingPriorization.fcl";
	
	public static void main(String[] args) {
		try {
			long begin = System.currentTimeMillis();
			RegressionTestingPriorizationProblem problem = InputParser.createProblem(BASE_FOLDER, COUPLING_FILE, SQFD_FILE, TEST_COVERAGE_FILE, CLASS_NUMBER, REQUIREMENT_NUMBER);
			/* execute step one: SQFD */
			executeSQFDAnalysis(problem);
			/* execute step two: Fuzzy System */
			executeFuzzySystem(problem);
			/* execute step three: Calculating the criticality for the test cases */
			executeCalculateCriticalityForTestCases(problem);
			/* execute step four: Ant Colony System */
			//executeAntColonySystem(problem);
			/* execute step four: Brute Force */
			BruteForce.execute(problem.getTestCases(), 800);
			/* execute step four: Busca Gulosa */
			//MochilaGulosa.execute(problem.getTestCases(), 500);
			System.out.println("Tempo de Execução: " + new SimpleDateFormat("ss:SSS").format(System.currentTimeMillis()-begin));
		} catch (IOException e) {
			e.printStackTrace();
		}
				
	}

	/**
	 * Execute step four: Ant Colony System
	 * @param problem
	 */
	private static void executeAntColonySystem(RegressionTestingPriorizationProblem problem) {
		/* Número de formigas */
		int numberAnt = 20;
		/* Quantidade máxima de ferômonio (0, 0.1) */
		double t0 = 0.1;
		/* Constante que amplifica a influência da concentração de feromonio. */
		double alfa = 0.5;
		/* Constante que amplifica a influência da heurística. */
		double beta = 0.5;
		/**/
		double q = 10;
		/**/
        int maxIterations = 10;
        /**/
        int maxExecution = 30;
        /**/
        double p1 = 0.5, p2 = 0.5, q0 = 0.5, constantQ = 1, cargaMaxima = 1000;
        int k = 0;
        
		Problem antProblem = ProblemFactory.createProblem(numberAnt, t0, alfa, beta, q, maxIterations, maxExecution, 
				p1, p2, q0, constantQ, k, cargaMaxima, problem.getTestCases());
		
		// ExecuteAntColonySystem.executeAlgorithm(antProblem);
		try {
			AntColonySystemAlgorithm.executeAntSystem(antProblem);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
