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
	public boolean contains(String word) {
		return super.isInEnum(word, JJ.class);
	};
}
