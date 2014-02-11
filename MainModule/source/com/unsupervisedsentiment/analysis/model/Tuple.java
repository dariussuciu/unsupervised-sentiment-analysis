package com.unsupervisedsentiment.analysis.model;

public class Tuple {

	private Word opinion;
	private Word target;

	private Dependency dependency;
	private String relation;
	
	public Tuple() {
		
	}
	
	public Tuple(Word opinion, Word target, Dependency dependency,
			String relation) {
		super();
		this.opinion = opinion;
		this.target = target;
		this.dependency = dependency;
		this.relation = relation;
	}

	/**
	 * @return the dependency
	 */
	public Dependency getDependency() {
		return dependency;
	}

	/**
	 * @param dependency
	 *            the dependency to set
	 */
	public void setDependency(Dependency dependency) {
		this.dependency = dependency;
	}

	/**
	 * @return the relation
	 */
	public String getRelation() {
		return relation;
	}

	/**
	 * @param relation
	 *            the relation to set
	 */
	public void setRelation(String relation) {
		this.relation = relation;
	}

	public Word getTarget() {
		return target;
	}

	public void setTarget(Word target) {
		this.target = target;
	}

	public Word getOpinion() {
		return opinion;
	}

	public void setOpinion(Word opinion) {
		this.opinion = opinion;
	}

}
