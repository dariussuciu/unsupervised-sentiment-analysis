package com.unsupervisedsentiment.analysis.core.constants.relations;

public abstract class GeneralPosRelationEnum {

	protected GeneralPosRelationEnum() {
		isInstantiated = false;
	}

	protected static boolean isInstantiated = false;

	public abstract boolean contains(final String word);

	protected abstract Class<? extends Enum<?>> getContainingEnum(final String word);

	protected <E extends Enum<E>> boolean isInEnum(final String value, final Class<E> enumClass) {
		for (E e : enumClass.getEnumConstants()) {
			if (e.name().equals(value)) {
				return true;
			}
		}
		return false;
	}

	protected String getPrettyStringFromEnumValues(Enum<?>[] values) {
		StringBuilder sBuilder = new StringBuilder();
		for (Enum<?> e : values) {
			sBuilder.append(e.name() + " ");
		}
		return sBuilder.toString();
	}

}
