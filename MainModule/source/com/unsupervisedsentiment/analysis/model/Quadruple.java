package com.unsupervisedsentiment.analysis.model;

public class Quadruple {

	private String posTag_x;
	private Dependency dependency;
	private String relation;
	private String posTag_y;

	/**
	 * @return the posTag_x
	 */
	public String getPosTag_x() {
		return posTag_x;
	}

	/**
	 * @param posTag_x
	 *            the posTag_x to set
	 */
	public void setPosTag_x(String posTag_x) {
		this.posTag_x = posTag_x;
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

	/**
	 * @return the posTag_y
	 */
	public String getPosTag_y() {
		return posTag_y;
	}

	/**
	 * @param posTag_y
	 *            the posTag_y to set
	 */
	public void setPosTag_y(String posTag_y) {
		this.posTag_y = posTag_y;
	}

}
