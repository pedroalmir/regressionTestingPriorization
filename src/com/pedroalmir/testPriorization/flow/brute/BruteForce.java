/**
 * 
 */
package com.pedroalmir.testPriorization.flow.brute;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.pedroalmir.testPriorization.model.RegressionTestingPriorizationProblem;
import com.pedroalmir.testPriorization.model.Solution;
import com.pedroalmir.testPriorization.model.TestCase;

/**
 * @author Pedro Almir
 * 
 */
public class BruteForce {
	
	/**
	 * @param testCases
	 * @param capacidade
	 */
	public static Solution execute(RegressionTestingPriorizationProblem problem) {
		//System.out.println("Executando Força Bruta...");
		/* Variables */
		double maiorCriticidade = 0.0;
		double melhorTempo = 0.0;
		List<TestCase> betterTestCases = new LinkedList<TestCase>();
		List<TestCase> testCases = problem.getTestCases();
		double capacidade = problem.getCapacidade();
		
		List<double[]> bruteForceValues = new LinkedList<double[]>();
		
		/* Creating all the possibilities */
		BigDecimal total = calculeAllPossibilitiesValue(testCases.size());
		BigDecimal count = new BigDecimal(0);
		//System.out.println("Espaço de Busca: " + total);
		
		while(!count.equals(total)){
			//printPercentagem(count, total);
			String probableSolution = count.toBigInteger().toString(2);
			double tempoAcumulado = 0.0, criticidadeAcumulada = 0.0;
			boolean excedeuCapacidade = false;
			List<TestCase> probableSolutionTestCases = new LinkedList<TestCase>();
			
			/* Just for debug
			if(probableSolution.equals("101010100000010100")){}
			*/
			int testCaseIndex = testCases.size()-1;
			
			for(int i = probableSolution.length()-1; i >= 0; i--){
				if(tempoAcumulado > capacidade){
					excedeuCapacidade = true;
					break;
				}else if(probableSolution.charAt(i) == '1'){
					if(tempoAcumulado + testCases.get(testCaseIndex).getTime() > capacidade){
						excedeuCapacidade = true;
						break;
					}else{
						criticidadeAcumulada += testCases.get(testCaseIndex).getCriticality();
						tempoAcumulado += testCases.get(testCaseIndex).getTime();
						probableSolutionTestCases.add(testCases.get(testCaseIndex));
					}
				}
				testCaseIndex--;
			}
			
			bruteForceValues.add(new double[]{tempoAcumulado, criticidadeAcumulada});
			
			if(!excedeuCapacidade){
				if(criticidadeAcumulada > maiorCriticidade){
					maiorCriticidade = criticidadeAcumulada;
					melhorTempo = tempoAcumulado;
					betterTestCases = probableSolutionTestCases;
				}
			}
			//System.out.println(count);
			count = count.add(new BigDecimal(1));
		}
		
		//System.out.println("Maior Criticidade: " + maiorCriticidade + ", Melhor Tempo: " + melhorTempo);
		//	for(int i = betterTestCases.size()-1; i >= 0; i--){
		//     System.out.print(String.format("%02d", betterTestCases.get(i).getId()) + "-");
		//}
		Solution solution = new Solution("Forca Bruta", betterTestCases, capacidade, problem.getKlasses().size(), problem.getRequirements().size(), 
				testCases.size(), melhorTempo, maiorCriticidade, 0l);
		solution.setBruteForceValues(bruteForceValues);
		return solution;
	}
	
	@SuppressWarnings("unused")
	private static void printPercentagem(BigDecimal count, BigDecimal total) {
		BigDecimal percentagem = count.divide(total).multiply(new BigDecimal(10));
		if(percentagem.intValue() == 10){
			System.out.println("Percentual concluído: 10%");
		}else if(percentagem.intValue() == 20){
			System.out.println("Percentual concluído: 20%");
		}else if(percentagem.intValue() == 30){
			System.out.println("Percentual concluído: 30%");
		}else if(percentagem.intValue() == 40){
			System.out.println("Percentual concluído: 40%");
		}else if(percentagem.intValue() == 50){
			System.out.println("Percentual concluído: 50%");
		}else if(percentagem.intValue() == 60){
			System.out.println("Percentual concluído: 60%");
		}else if(percentagem.intValue() == 70){
			System.out.println("Percentual concluído: 70%");
		}else if(percentagem.intValue() == 80){
			System.out.println("Percentual concluído: 80%");
		}else if(percentagem.intValue() == 90){
			System.out.println("Percentual concluído: 90%");
		}else if(percentagem.intValue() == 100){
			System.out.println("Percentual concluído: 100%");
		}
	}

	/**
	 * @param numberOfElements
	 */
	private static BigDecimal calculeAllPossibilitiesValue(int numberOfElements){
		BigDecimal total = new BigDecimal(1l);
		for(int i = 0; i < numberOfElements/30; i++){
			total = total.multiply(new BigDecimal(1 << 30));
		}
		
		if(numberOfElements % 30 != 0){
			total =  total.multiply(new BigDecimal(1 << (numberOfElements % 30)));
		}
		return total;
	}

	/**
	 * @param testCases
	 * @return
	 */
	@SuppressWarnings("unused")
	private static List<Integer[]> subconjuntos(List<TestCase> testCases) {
		int qtdSubconjuntos = 1 << testCases.size();
		
		BigDecimal total = new BigDecimal(1l);
		for(int i = 0; i < testCases.size()/30; i++){
			total = total.multiply(new BigDecimal(1 << 30));
		}
		
		if(testCases.size() % 30 != 0){
			total =  total.multiply(new BigDecimal(1 << (testCases.size() % 30)));
		}
		
		int indiceLista = 0;
		
		/* Tamanho da lista deve ser a quantidade de subconjuntos possiveis. */
		List<Integer[]> listaSubconjuntos = new ArrayList<Integer[]>(qtdSubconjuntos);

		for (int i = 0; i < qtdSubconjuntos; i++) {
			int pos = testCases.size() - 1;
			int bitmask = i;
			Integer[] listaAux = new Integer[testCases.size()];
			while (bitmask > 0) {
				if ((bitmask & 1) == 1) {
					listaAux[indiceLista] = testCases.get(pos).getId().intValue();
					indiceLista++;
				}
				bitmask >>= 1;
				pos--;
			}
			listaSubconjuntos.add(listaAux);
			indiceLista = 0;
		}
		return listaSubconjuntos;
	}

	class PossibleSolution {
		/**
		 * 
		 */
		List<TestCase> testCases;
		
		public PossibleSolution() {
			this.testCases = new LinkedList<TestCase>();
		}
		
		/**
		 * @param tests
		 */
		public PossibleSolution(TestCase[] tests){
			this.testCases = new ArrayList<TestCase>(Arrays.asList(tests));
		}
		
		/**
		 * @param testCase
		 */
		public void addTestCase(TestCase testCase){
			testCases.add(testCase);
		}

		/**
		 * @return the testCases
		 */
		public List<TestCase> getTestCases() {
			return testCases;
		}

		/**
		 * @param testCases the testCases to set
		 */
		public void setTestCases(List<TestCase> testCases) {
			this.testCases = testCases;
		}
	}
}
