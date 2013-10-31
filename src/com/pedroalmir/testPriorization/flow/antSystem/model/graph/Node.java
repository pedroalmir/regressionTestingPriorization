/**
 * 
 */
package com.pedroalmir.testPriorization.flow.antSystem.model.graph;

import java.util.LinkedList;
import java.util.Map;

/**
 * @author Pedro Almir
 * 
 */
public class Node {
	
	/**
	 * id
	 */
	private Long id;
	/**
	 * information
	 */
	private Map<String, Object> informations;
	/**
	 * adjacentEdges
	 */
	private LinkedList<Edge> adjacentEdges;
	
	/**
	 * @param information
	 * @param feature
	 */
	public Node(Map<String, Object> informations) {
		this.informations = informations;
		this.adjacentEdges = new LinkedList<Edge>();
	}
	
	/**
	 * @param id
	 * @param informations
	 */
	public Node(Long id, Map<String, Object> informations) {
		super();
		this.id = id;
		this.informations = informations;
	}
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the adjacentEdges
	 */
	public LinkedList<Edge> getAdjacentEdges() {
		return adjacentEdges;
	}

	/**
	 * @param adjacentEdges the adjacentEdges to set
	 */
	public void setAdjacentEdges(LinkedList<Edge> adjacentEdges) {
		this.adjacentEdges = adjacentEdges;
	}

	/**
	 * @return the informations
	 */
	public Map<String, Object> getInformations() {
		return informations;
	}

	/**
	 * @param informations the informations to set
	 */
	public void setInformations(Map<String, Object> informations) {
		this.informations = informations;
	}
}
