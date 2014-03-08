package com.unsupervisedsentiment.analysis.core.constants.relations;

public class Dep_MRRel extends GenericRelation {

	/**
	 * Removed: <br>
	 * - advmod: very... <br>
	 * - nn: <br>
	 * - det: a... <br>
	 * - mark: She sais that you like to swim. mark(that,swim) <br>
	 * - mwe: I like dogs as well as cats. mwe(well, as) <br>
	 * - npadvmod: 6 feet long. npadvmod(long, feet) <br>
	 * - num: Sam ate 3 sheep. num(sheep, 3) <br>
	 * - number: I have four thousand sheep. number(thousand, four) <br>
	 * - poss: Their offces. poss(offces, their) <br>'
	 * - possesive: Bill's clothes. possesive(Bill, 's) <br>
	 * - preconj: Both the boys and the girls are here. preconj(boys, both)
	 * - predet: All the boys are here. predet(boys, all) <br>
	 * - prep: I saw a cat in a hat" prep(cat, in) <br>
	 * - prt: They shut down the station. prt(shut, down) <br>
	 * - quantmod: About 200 people came to the party. quantmod(200, About) <br>
	 * - tmod: Last night, I swam in the pool. tmod(swam, night) <br>
	 * - vmod: I don't have anything to say to you. vmod(anything, say)
	 * - rcmod: ? <br>
	 * <br>
	 * Added: <br>
	 * - acomp: She looks very beautiful<br>
	 */
	public enum mod {
		acomp, amod, appos, advcl
	};

	public enum pmod {

	};

	/**
	 * Removed: <br>
	 * - csubj: What she said makes sense. csubj(makes, said) <br>
	 * - csubjpass: That she lied was suspected by everyone. csubjpass(suspected, lied) <br>
	 * 
	 */
	public enum subj {
		nsubj, nsubjpass
	};

	public enum s {

	};

	/**
	 * Removed: <br>
	 * - dobj: She gave me a raise. dobj(gave, raise) <br>
	 * - iobj: She gave me a raise. iobj(gave, me) <br>
	 */
	public enum obj {
		pobj
	};

	public enum obj2 {

	};

	public enum desc {

	}

	private static Dep_MRRel mrRel;

	private Dep_MRRel() {
	}

	public static Dep_MRRel getInstance() {
		if (isInstantiated)
			return mrRel;

		isInstantiated = true;
		return mrRel = new Dep_MRRel();
	}

	@Override
	public Class<? extends Enum<?>> getContainingEnum(String word) {
		if (super.isInEnum(word, mod.class)) {
			return mod.class;
		} else if (super.isInEnum(word, pmod.class)) {
			return pmod.class;
		} else if (super.isInEnum(word, subj.class)) {
			return subj.class;
		} else if (super.isInEnum(word, s.class)) {
			return s.class;
		} else if (super.isInEnum(word, obj.class)) {
			return obj.class;
		} else if (super.isInEnum(word, obj2.class)) {
			return obj2.class;
		} else if (super.isInEnum(word, desc.class)) {
			return desc.class;
		}
		return null;
	}

	@Override
	public boolean contains(String word) {
		return super.isInEnum(word, mod.class) || super.isInEnum(word, pmod.class) || super.isInEnum(word, subj.class)
				|| super.isInEnum(word, s.class) || super.isInEnum(word, obj.class) || super.isInEnum(word, obj2.class)
				|| super.isInEnum(word, desc.class);
	};
}
