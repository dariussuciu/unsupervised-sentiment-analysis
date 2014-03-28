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
	public Class<? extends Enum<?>> getContainingEnum(final String word) {
		if (super.isInEnum(word, CONJ.class)) {
			return CONJ.class;
		}
		return null;
	}

	@Override
	public boolean contains(final String word) {
		return super.isInEnum(word, CONJ.class);
	}

	public String getAllEnumElementsAsString() {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append(getPrettyStringFromEnumValues(CONJ.values()));
		return sBuilder.toString();
	}
}
