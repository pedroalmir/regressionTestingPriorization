/**
 * 
 */
package com.pedroalmir.testPriorization.flow.guloso;

import java.util.LinkedList;
import java.util.List;

import com.pedroalmir.testPriorization.model.RegressionTestingPriorizationProblem;
import com.pedroalmir.testPriorization.model.Solution;
import com.pedroalmir.testPriorization.model.TestCase;

/**
 * @author Pedro Almir
 * 
 */
public class MochilaGulosa {
	/**
	 * @param testCases
	 * @param capacidade
	 */
	public static Solution execute(RegressionTestingPriorizationProblem problem) {
		List<TestCase> solution = new LinkedList<TestCase>();
		List<TestCase> testCases = problem.getTestCases();
		double capacidade = problem.getCapacidade();
		
		int itemIndice = 0;
		/* pegar itens até que a mochila esteja cheia ou não haja mais itens para colocar */
		while (capacidade > 0 && itemIndice < testCases.size()) {
			if (testCases.get(itemIndice).getTime() <= capacidade) {
				// se cabe inteiro, pegue
				capacidade -= testCases.get(itemIndice).getTime();
				solution.add(testCases.get(itemIndice));
			}
			itemIndice++;
		}
		
		
		double criticidade = 0.0;
		double tempo = 0.0;
		
		for(TestCase testCase : solution){
			//System.out.print(String.format("%02d", testCase.getId()) + "-");
			criticidade += testCase.getCriticality();
			tempo += testCase.getTime();
		}
		
		//System.out.println("\nMaior Criticidade: " + criticidade + ", Melhor Tempo: " + tempo);
		return new Solution("Busca Gulosa", solution, capacidade, problem.getKlasses().size(), problem.getRequirements().size(), 
				testCases.size(), tempo, criticidade, 0l);
	}
}
