package com.pedroalmir.testPriorization.flow.antSystem.model;

import java.util.List;

import com.pedroalmir.testPriorization.flow.antSystem.model.graph.Edge;

/**
 * @author Pedro Almir
 *
 */
public interface AntHill {

	/**
	 * @param edge
	 * @param edgeEighborFeasible
	 * @param beta
	 * @param alfa
	 * @return
	 */
	double transitionProbability(Edge edge, List<Edge> edgeEighborFeasible, double beta, double alfa);

}
