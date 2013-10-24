/**
 * 
 */
package com.pedroalmir.testPriorization.model;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Pedro Almir
 *
 */
public class Requirement {
	
	private Long id;
	private String name;
	private String description;
	private int importance;
	private List<Integer> clientPriority;
	
	/**
	 * 
	 */
	public Requirement(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
		this.description = "Not used yet!!!";
		this.importance = 0;
		clientPriority = new LinkedList<Integer>();
	}
	
	/**
	 * @param id
	 * @param name
	 * @param description
	 * @param importance
	 */
	public Requirement(Long id, String name, String description, int importance) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.importance = importance;
	}
	
	/**
	 * @param value
	 */
	public void addClientPriority(Integer value){
		this.clientPriority.add(value);
	}
	
	/**
	 * 
	 */
	public void calculateImportance(){
		int amount = 0;
		for(Integer value : this.clientPriority){
			amount += value;
		}
		this.importance = amount/this.clientPriority.size();
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the importance
	 */
	public int getImportance() {
		return importance;
	}
	/**
	 * @param importance the importance to set
	 */
	public void setImportance(int importance) {
		this.importance = importance;
	}

	/**
	 * @return the clientPriority
	 */
	public List<Integer> getClientPriority() {
		return clientPriority;
	}

	/**
	 * @param clientPriority the clientPriority to set
	 */
	public void setClientPriority(List<Integer> clientPriority) {
		this.clientPriority = clientPriority;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Requirement [name=" + name + ", importance=" + importance + "]";
	}

}
