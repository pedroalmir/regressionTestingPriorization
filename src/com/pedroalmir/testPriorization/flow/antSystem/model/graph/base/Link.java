package com.pedroalmir.testPriorization.flow.antSystem.model.graph.base;

/**
 * @author Pedro Almir
 * 
 */
public interface Link {
	/**
	 * Probabilidade de Transição (i, j)
	 * @return
	 */
	public double transitionProbability();

	/**
	 * Evaporação do link (i, j)
	 * @return
	 */
	public double evaporation();
}
