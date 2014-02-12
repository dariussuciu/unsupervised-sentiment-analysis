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
			String relationHOpinion, String relationHTarget) {
		super(opinion, target, dependency, tupleType);
		H = h;
		this.relationHOpinion = relationHOpinion;
		this.relationHTarget = relationHTarget;
	}

	public Triple() {
		super();
	}

}
