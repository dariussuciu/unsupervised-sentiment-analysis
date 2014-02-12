package com.unsupervisedsentiment.analysis.model;

/**
 * 
 * Tuple contains a source word and a target. The source is the starting
 * word/point and the target is found through this source.
 * 
 */
public class Tuple {

	private Word source;
	private Word target;

	private Dependency dependency;

	private TupleType tupleType;

	public Tuple() {

	}

	public Tuple(Word source, Word target, Dependency dependency, TupleType tupleType) {
		super();
		this.source = source;
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

	public Word getSource() {
		return source;
	}

	public void setSource(Word source) {
		this.source = source;
	}

}
