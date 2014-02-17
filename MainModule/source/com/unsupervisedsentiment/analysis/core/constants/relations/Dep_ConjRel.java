package com.unsupervisedsentiment.analysis.core.constants.relations;

public class Dep_ConjRel extends GenericRelation {
	public static enum CONJ {
		conj, conj_and
	}

	private static Dep_ConjRel conjRel;

	private Dep_ConjRel() {
	}

	public static Dep_ConjRel getInstance() {
		if (isInstantiated)
			return conjRel;

		isInstantiated = true;
		return conjRel = new Dep_ConjRel();
	}

	@Override
	public boolean contains(String word) {
		return super.isInEnum(word, CONJ.class);
	};
}
