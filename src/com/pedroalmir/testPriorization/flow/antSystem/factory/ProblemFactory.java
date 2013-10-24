/**
 * 
 */
package com.pedroalmir.testPriorization.flow.antSystem.factory;

import java.util.ArrayList;
import java.util.List;

import com.pedroalmir.testPriorization.flow.antSystem.model.Problem;
import com.pedroalmir.testPriorization.flow.antSystem.model.graph.Edge;
import com.pedroalmir.testPriorization.flow.antSystem.model.graph.Vertex;
import com.pedroalmir.testPriorization.model.TestCase;

/**
 * @author Pedro Almir
 * 
 */
public class ProblemFactory {
	public static Problem createProblem(int numberAnt, double t0, double alfa, double beta, double q, int maxIterations,
			int maxExecution, double p1, double p2, double q0, double constantQ, int k, double cargaMaxima,
			List<TestCase> testCases) {
		Problem problem = new Problem(numberAnt, t0, alfa, beta, q, maxIterations, maxExecution, p1, p2, q0, constantQ, k,
				cargaMaxima);

		List<Vertex> vertexs = new ArrayList<Vertex>();
		List<Edge> edges = new ArrayList<Edge>();
		
		/* Vertex com id, criticidade, tempo e feromonio inicial */
		for (int i = 0; i < testCases.size(); i++) {
			vertexs.add(new Vertex(i+1, testCases.get(i).getCriticality(), testCases.get(i).getTime(), 0.01));
		}
		/* */
		int indexEdge = 1;
		for (int i = 0; i < testCases.size(); i++) {
			for (int j = 0; j < testCases.size(); j++) {
				if (i != j) {
					edges.add(new Edge(vertexs.get(i), vertexs.get(j), indexEdge, -1, -1));
					indexEdge++;
				}
			}
		}
		/* */
		problem.setVertexs(vertexs);
		problem.setEdges(edges);
		problem.setTestCases(testCases);
		/* */
		return problem;
	}
}
