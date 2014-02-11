package com.unsupervisedsentiment.analysis.test.constants.relations;

public class ConjRel extends GenericRelation{
	public static enum CONJ {
		conj("conj"),
		conjand("conj-and");
		
		CONJ(final String text){
			this.text = text;
		}
		
		private final String text;
		
	    @Override
	    public String toString() {
	        return text;
	    }
	}
	
	private static ConjRel conjRel;
	
	private ConjRel() {}
	
	public static ConjRel getInstance() {
		if(isInstantiated)
			return conjRel;
		
		isInstantiated = true;
		return conjRel = new ConjRel();
	}

	@Override
	public boolean contains(String word) {
		return  super.isInEnum(word, CONJ.class);
	};
}
