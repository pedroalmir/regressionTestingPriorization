/**
 * 
 */
package com.pedroalmir.testPriorization.flow.brute;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
	public static void execute(List<TestCase> testCases, double capacidade) {
		List<Integer[]> possibleSolutions = subconjuntos(testCases);
		double maiorCriticidade = Double.MIN_VALUE;
		double melhorTempo = Double.MIN_VALUE;
		Integer[] solution = null;
		
		for (Integer[] possibleSolution : possibleSolutions) {
			double tempoAcumulado = 0.0;
			double criticidadeAcumulada = 0.0;
			boolean excedeuCapacidade = false;
			
			for(int i = 0; i < possibleSolution.length; i++){
				if(tempoAcumulado > capacidade){
					excedeuCapacidade = true;
					break;
				}else{
					if(possibleSolution[i] != null){
						criticidadeAcumulada += testCases.get(possibleSolution[i]-1).getCriticality();
						tempoAcumulado += testCases.get(possibleSolution[i]-1).getTime();
					}
				}
			}
			
			if(!excedeuCapacidade){
				if(criticidadeAcumulada > maiorCriticidade){
					maiorCriticidade = criticidadeAcumulada;
					melhorTempo = tempoAcumulado;
					solution = possibleSolution;
				}
			}
		}
		
		System.out.println("Maior Criticidade: " + maiorCriticidade + ", Melhor Tempo: " + melhorTempo);
		for(int i = 0; i < solution.length; i++){
			if(solution[i] != null){
				System.out.print(String.format("%02d", testCases.get(solution[i]-1).getId()) + "-");
			}
		}
	}

	/**
	 * @param testCases
	 * @return
	 */
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
