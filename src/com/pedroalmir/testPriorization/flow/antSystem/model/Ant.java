/**
 * 
 */
package com.pedroalmir.testPriorization.flow.antSystem.model;

import java.util.LinkedList;
import java.util.List;

import com.pedroalmir.testPriorization.flow.antSystem.model.graph.Node;


/**
 * @author Pedro Almir
 *
 */
public class Ant {
	/** ID */
	private Long id;
	/** Information */
	private String information;
	/** ActualNode */
	private Node actualNode;
	/** */
	private List<Node> tabuList;
	/**
	 * @param id
	 * @param information
	 * @param actualNode
	 */
	public Ant(String information, Node actualNode) {
		this.information = information;
		this.actualNode = actualNode;
		this.tabuList = new LinkedList<Node>();
		this.tabuList.add(actualNode);
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
	 * @return the information
	 */
	public String getInformation() {
		return information;
	}
	/**
	 * @param information the information to set
	 */
	public void setInformation(String information) {
		this.information = information;
	}
	/**
	 * @return the actualNode
	 */
	public Node getActualNode() {
		return actualNode;
	}
	/**
	 * @param actualNode the actualNode to set
	 */
	public void setActualNode(Node actualNode) {
		this.actualNode = actualNode;
	}
	/**
	 * @return the tabuList
	 */
	public List<Node> getTabuList() {
		return tabuList;
	}
	/**
	 * @param tabuList the tabuList to set
	 */
	public void setTabuList(List<Node> tabuList) {
		this.tabuList = tabuList;
	}
}
