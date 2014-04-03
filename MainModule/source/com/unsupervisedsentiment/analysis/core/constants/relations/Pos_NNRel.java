package com.unsupervisedsentiment.analysis.core.constants.relations;

public class Pos_NNRel extends GenericRelation {
	/*
	 * Added: - PRP - she / he
	 */
	public enum NN {
		NN, NNP, NNPS, NNS, PRP, FW
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
	public Class<? extends Enum<?>> getContainingEnum(final String word) {
		if (super.isInEnum(word, NN.class)) {
			return NN.class;
		}
		return null;
	}

	@Override
	public boolean contains(final String word) {
		return super.isInEnum(word, NN.class);
	};

	public String getAllEnumElementsAsString() {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append(getPrettyStringFromEnumValues(NN.values()));
		return sBuilder.toString();
	}
}
