package com.unsupervisedsentiment.analysis.core.constants.relations;

public class Pos_JJRel extends GenericRelation {

	public enum JJ {
		JJ, JJR, JJS
	}

	private static Pos_JJRel jjRel;

	private Pos_JJRel() {
	}

	public static Pos_JJRel getInstance() {
		if (isInstantiated)
			return jjRel;

		isInstantiated = true;
		return jjRel = new Pos_JJRel();
	}

	@Override
	public Class<? extends Enum<?>> getContainingEnum(final String word) {
		if (super.isInEnum(word, JJ.class)) {
			return JJ.class;
		}
		return null;
	}

	@Override
	public boolean contains(final String word) {
		return super.isInEnum(word, JJ.class);
	};

	public String getAllEnumElementsAsString() {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append(getPrettyStringFromEnumValues(JJ.values()));
		return sBuilder.toString();
	}
}
