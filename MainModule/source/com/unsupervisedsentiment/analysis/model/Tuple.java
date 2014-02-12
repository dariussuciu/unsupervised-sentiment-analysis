package com.unsupervisedsentiment.analysis.model;

public class Tuple {

	private Word opinion;
	private Word target;

	private Dependency dependency;

	
	private TupleType tupleType;
	
	public Tuple() {
		
	}
	
	public Tuple(Word opinion, Word target, Dependency dependency,
			TupleType tupleType) {
		super();
		this.opinion = opinion;
		this.target = target;
		this.dependency = dependency;
		this.tupleType = tupleType;
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

	public TupleType getTupleType() {
		return tupleType;
	}

	public void setTupleType(TupleType tupleType) {
		this.tupleType = tupleType;
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
