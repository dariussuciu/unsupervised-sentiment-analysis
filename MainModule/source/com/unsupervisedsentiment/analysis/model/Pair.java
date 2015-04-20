package com.unsupervisedsentiment.analysis.model;

public class Pair extends Tuple {

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

	public Pair(Word opinion, Word target, Dependency dependency, TupleType tupleType, String relation,
			int sentenceIndex, String sentence) {
		super(opinion, target, dependency, tupleType, sentenceIndex, sentence);
		this.relation = relation;
	}

	public Pair() {
		super();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((relation == null) ? 0 : relation.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair other = (Pair) obj;
		if (relation == null) {
			if (other.relation != null)
				return false;
		} else if (!relation.equals(other.relation))
			return false;
		return super.equals(obj);
	}
}
