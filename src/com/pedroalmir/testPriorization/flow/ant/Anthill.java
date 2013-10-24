package com.pedroalmir.testPriorization.flow.ant;


import java.util.List;

public interface Anthill {
	//public double transitionProbability(Edge edge, List<Edge> edges, double alfa, double beta);

	double transitionProbability(Edge edge, List<Edge> edgeEighborFeasible,
			double beta, double alfa);

}
