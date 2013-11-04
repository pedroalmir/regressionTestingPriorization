/**
 * 
 */
package com.pedroalmir.testPriorization.flow.antSystem.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pedroalmir.testPriorization.flow.antSystem.model.graph.Edge;
import com.pedroalmir.testPriorization.flow.antSystem.model.graph.Graph;
import com.pedroalmir.testPriorization.flow.antSystem.model.graph.Node;
import com.pedroalmir.testPriorization.flow.antSystem.model.problem.Problem;
import com.pedroalmir.testPriorization.model.TestCase;

/**
 * @author Pedro Almir
 * 
 */
public class ProblemFactory {
	/**
	 * @param numberAnt
	 * @param t0 Valor máximo de feromônio
	 * @param alfa Constante que amplifica a influência da concentração de feromonio
	 * @param beta Constante que amplifica a influência da heurística
	 * @param q Não sei definir, mas esse valor é utilizado 
	 * @param maxIterations Número máximo de iterações
	 * @param maxExecution Número máximo de execuções
	 * @param p Taxa de Evaporação
	 * @param q0 Usa-se um q0 para fazer a escolha do proximo vertice (na construção do caminho) 
	 * de forma pseudo aleatória quando se usa Ant Colony System, no caso esta modelado 
	 * como um Ant System (AS).
	 * @param bound Limite máximo da mochila
	 * @param testCases Lista de casos de teste
	 * @return problem
	 */
	public static Problem createProblem(int numberAnt, double t0, double alfa, double beta, double q, int maxIterations,
			int maxExecution, double p, double q0, double bound, List<TestCase> testCases) {
		/* */
		Problem problem = new Problem(numberAnt, t0, alfa, beta, q, maxIterations, maxExecution, p, q0, bound);

		List<Node> vertexs = new ArrayList<Node>();
		List<Edge> edges = new ArrayList<Edge>();
		
		/* Node com id, criticidade, tempo */
		for (int i = 0; i < testCases.size(); i++) {
			Map<String, Object> informations = new HashMap<String, Object>();
			informations.put("criticidade", new Double(testCases.get(i).getCriticality()));
			informations.put("tempo", testCases.get(i).getTime());
			
			vertexs.add(new Node(new Long(i+1), informations));
		}
		/* */
		int indexEdge = 1;
		for (int i = 0; i < testCases.size(); i++) {
			for (int j = 0; j < testCases.size(); j++) {
				if (i != j) {
					edges.add(new Edge(new Long(indexEdge++), vertexs.get(i), vertexs.get(j), -1, -1));
				}
			}
		}
		/* Create Graph */
		problem.setGraph(new Graph(1L, "", vertexs, edges));
		problem.setTestCases(testCases);
		/* Return problem */
		return problem;
	}
}
