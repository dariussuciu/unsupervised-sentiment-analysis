package com.unsupervisedsentiment.analysis.test.constants.relations;

public class Pos_NNRel extends GenericRelation {
	public enum NN {
		NN, NNP, NNPS, NNS
	}

	private static Pos_NNRel nnRel;

	private Pos_NNRel() {
	}

	public static Pos_NNRel getInstance() {
		if (isInstantiated)
			return nnRel;

		isInstantiated = true;
		return nnRel = new Pos_NNRel();
	}

	@Override
	public boolean contains(String word) {
		return super.isInEnum(word, NN.class);
	};
}
