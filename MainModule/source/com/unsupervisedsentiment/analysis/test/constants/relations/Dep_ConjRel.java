package com.unsupervisedsentiment.analysis.test.constants.relations;

public class Dep_ConjRel extends GenericRelation {
	public static enum CONJ {
		conj("conj"), conjand("conj-and");

		CONJ(final String text) {
			this.text = text;
		}

		private final String text;

		@Override
		public String toString() {
			return text;
		}
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
