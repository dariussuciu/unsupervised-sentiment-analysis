package com.unsupervisedsentiment.analysis.test.constants.relations;

public abstract class GenericRelation {
	public abstract boolean Contains(String word);
	
	public static <E extends Enum<E>> boolean isInEnum(String value, Class<E> enumClass) {
		  for (E e : enumClass.getEnumConstants()) {
		    if(e.name().equals(value)) { return true; }
		  }
		  return false;
		}
	
	protected static boolean isInstantiated;
}
