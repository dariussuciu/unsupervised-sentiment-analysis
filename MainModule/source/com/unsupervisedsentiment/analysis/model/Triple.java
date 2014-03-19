package com.unsupervisedsentiment.analysis.model;

public class Triple extends Tuple {
	private Word H;
	private String relationHOpinion;
	private String relationHTarget;

	public Word getH() {
		return H;
	}

	public void setH(Word h) {
		H = h;
	}

	public String getRelationHOpinion() {
		return relationHOpinion;
	}

	public void setRelationHOpinion(String relationHOpinion) {
		this.relationHOpinion = relationHOpinion;
	}

	public String getRelationHTarget() {
		return relationHTarget;
	}

	public void setRelationHTarget(String relationHTarget) {
		this.relationHTarget = relationHTarget;
	}

	public Triple(Word opinion, Word target, Word h, Dependency dependency, TupleType tupleType,
			String relationHOpinion, String relationHTarget, int sentenceIndex, String sentence) {
		super(opinion, target, dependency, tupleType, sentenceIndex, sentence);
		H = h;
		this.relationHOpinion = relationHOpinion;
		this.relationHTarget = relationHTarget;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((H == null) ? 0 : H.hashCode());
		result = prime
				* result
				+ ((relationHOpinion == null) ? 0 : relationHOpinion.hashCode());
		result = prime * result
				+ ((relationHTarget == null) ? 0 : relationHTarget.hashCode());
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
		Triple other = (Triple) obj;
		if (H == null) {
			if (other.H != null)
				return false;
		} else if (!H.equals(other.H))
			return false;
		if (relationHOpinion == null) {
			if (other.relationHOpinion != null)
				return false;
		} else if (!relationHOpinion.equals(other.relationHOpinion))
			return false;
		if (relationHTarget == null) {
			if (other.relationHTarget != null)
				return false;
		} else if (!relationHTarget.equals(other.relationHTarget))
			return false;
		return super.equals(obj);
	}

	public Triple() {
		super();
	}

}
