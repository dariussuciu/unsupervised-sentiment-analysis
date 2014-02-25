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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dependency == null) ? 0 : dependency.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result
				+ ((tupleType == null) ? 0 : tupleType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tuple other = (Tuple) obj;
		if (dependency != other.dependency)
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		if (tupleType != other.tupleType)
			return false;
		return true;
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

	public Word getOpinionWord(){
		Word word = getSource().getType().equals(ElementType.OPINION_WORD) ? getSource() : getTarget();
		return word;
	}
}
