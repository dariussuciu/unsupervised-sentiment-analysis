package com.unsupervisedsentiment.analysis.core.constants.relations;

public class RelationsContainer {

	public static Dep_MRRel DEP_MR = Dep_MRRel.getInstance();
	public static Dep_ConjRel DEP_CONJ = Dep_ConjRel.getInstance();
	public static Pos_JJRel POS_JJ = Pos_JJRel.getInstance();
	public static Pos_NNRel POS_NN = Pos_NNRel.getInstance();

	public static boolean arePosEquivalent(final String pos1, final String pos2) {
		final Class<? extends Enum<?>> e1 = POS_JJ.getContainingEnum(pos1);
		final Class<? extends Enum<?>> e2 = POS_JJ.getContainingEnum(pos2);
		final Class<? extends Enum<?>> e3 = POS_NN.getContainingEnum(pos1);
		final Class<? extends Enum<?>> e4 = POS_NN.getContainingEnum(pos2);

		if ((e1 != null) && (e2 != null) && (e1.equals(e2))) {
			return true;
		}

		if ((e3 != null) && (e4 != null) && (e3.equals(e4)))
			return true;

		return false;
	}

	public static String getAllEnumElementsAsString() {
		StringBuilder sb = new StringBuilder();
		sb.append(DEP_MR.getAllEnumElementsAsString());
		sb.append(DEP_CONJ.getAllEnumElementsAsString());
		sb.append(POS_JJ.getAllEnumElementsAsString());
		sb.append(POS_NN.getAllEnumElementsAsString());
		return sb.toString();
	}
}
