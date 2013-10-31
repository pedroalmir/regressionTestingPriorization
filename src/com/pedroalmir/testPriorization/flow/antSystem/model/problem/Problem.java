/**
 * 
 */
package com.pedroalmir.testPriorization.flow.antSystem.model.problem;

import java.util.List;

import com.pedroalmir.testPriorization.flow.antSystem.model.graph.Graph;
import com.pedroalmir.testPriorization.model.TestCase;

/**
 * @author Pedro Almir
 *
 */
public class Problem {
	
	/** Número de formigas */
	private int numberAnt;
	/** Quantidade máxima de ferômonio (0, 0.1) */
	private double t0;
	/** Constante que amplifica a influência da concentração de feromonio */
	private double alfa;
	/** Constante que amplifica a influência da heurística */
	private double beta;
	/** */
	private double q;
	/** */
	private int maxIterations;
	/** */
	private int maxExecution;
	/** */
	private double p1, p2, q0, constantQ;
	/** */
	private int k;
	/** */
	private double bound;
	/** */
	private Graph graph;
	/** */
	private List<TestCase> testCases;
	
	/**
	 * 
	 */
	public Problem() {
		
	}
	
	/**
	 * @param numberAnt
	 * @param t0
	 * @param alfa
	 * @param beta
	 * @param q
	 * @param maxIterations
	 * @param maxExecution
	 * @param p1
	 * @param p2
	 * @param q0
	 * @param constantQ
	 * @param k
	 * @param cargaMaxima
	 */
	public Problem(int numberAnt, double t0, double alfa, double beta, double q, int maxIterations, int maxExecution, double p1,
			double p2, double q0, double constantQ, int k, double cargaMaxima) {
		super();
		this.numberAnt = numberAnt;
		this.t0 = t0;
		this.alfa = alfa;
		this.beta = beta;
		this.q = q;
		this.maxIterations = maxIterations;
		this.maxExecution = maxExecution;
		this.p1 = p1;
		this.p2 = p2;
		this.q0 = q0;
		this.constantQ = constantQ;
		this.k = k;
		this.bound = cargaMaxima;
	}



	/**
	 * @return the numberAnt
	 */
	public int getNumberAnt() {
		return numberAnt;
	}
	/**
	 * @param numberAnt the numberAnt to set
	 */
	public void setNumberAnt(int numberAnt) {
		this.numberAnt = numberAnt;
	}
	/**
	 * @return the t0
	 */
	public double getT0() {
		return t0;
	}
	/**
	 * @param t0 the t0 to set
	 */
	public void setT0(double t0) {
		this.t0 = t0;
	}
	/**
	 * @return the alfa
	 */
	public double getAlfa() {
		return alfa;
	}
	/**
	 * @param alfa the alfa to set
	 */
	public void setAlfa(double alfa) {
		this.alfa = alfa;
	}
	/**
	 * @return the beta
	 */
	public double getBeta() {
		return beta;
	}
	/**
	 * @param beta the beta to set
	 */
	public void setBeta(double beta) {
		this.beta = beta;
	}
	/**
	 * @return the q
	 */
	public double getQ() {
		return q;
	}
	/**
	 * @param q the q to set
	 */
	public void setQ(double q) {
		this.q = q;
	}
	/**
	 * @return the maxIterations
	 */
	public int getMaxIterations() {
		return maxIterations;
	}
	/**
	 * @param maxIterations the maxIterations to set
	 */
	public void setMaxIterations(int maxIterations) {
		this.maxIterations = maxIterations;
	}
	/**
	 * @return the maxExecution
	 */
	public int getMaxExecution() {
		return maxExecution;
	}
	/**
	 * @param maxExecution the maxExecution to set
	 */
	public void setMaxExecution(int maxExecution) {
		this.maxExecution = maxExecution;
	}
	/**
	 * @return the p1
	 */
	public double getP1() {
		return p1;
	}
	/**
	 * @param p1 the p1 to set
	 */
	public void setP1(double p1) {
		this.p1 = p1;
	}
	/**
	 * @return the p2
	 */
	public double getP2() {
		return p2;
	}
	/**
	 * @param p2 the p2 to set
	 */
	public void setP2(double p2) {
		this.p2 = p2;
	}
	/**
	 * @return the q0
	 */
	public double getQ0() {
		return q0;
	}
	/**
	 * @param q0 the q0 to set
	 */
	public void setQ0(double q0) {
		this.q0 = q0;
	}
	/**
	 * @return the constantQ
	 */
	public double getConstantQ() {
		return constantQ;
	}
	/**
	 * @param constantQ the constantQ to set
	 */
	public void setConstantQ(double constantQ) {
		this.constantQ = constantQ;
	}
	/**
	 * @return the k
	 */
	public int getK() {
		return k;
	}
	/**
	 * @param k the k to set
	 */
	public void setK(int k) {
		this.k = k;
	}

	/**
	 * @return the bound
	 */
	public double getBound() {
		return bound;
	}

	/**
	 * @param bound the bound to set
	 */
	public void setBound(double bound) {
		this.bound = bound;
	}

	/**
	 * @return the graph
	 */
	public Graph getGraph() {
		return graph;
	}

	/**
	 * @param graph the graph to set
	 */
	public void setGraph(Graph graph) {
		this.graph = graph;
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
