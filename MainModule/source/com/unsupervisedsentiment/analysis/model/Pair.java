package com.unsupervisedsentiment.analysis.model;

public class Pair extends Tuple{

	private String relation;
	
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

	public Pair(Word opinion, Word target, Dependency dependency,
			TupleType tupleType, String relation) {
		super(opinion, target, dependency, tupleType);
		this.relation = relation;
	}
	
	public Pair() {
		super();
	}
}
