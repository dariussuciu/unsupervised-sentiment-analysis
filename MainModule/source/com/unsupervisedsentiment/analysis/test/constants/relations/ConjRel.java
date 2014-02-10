package com.unsupervisedsentiment.analysis.test.constants.relations;

public class ConjRel extends GenericRelation{
	public enum CONJ {
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

	@Override
	public boolean Contains(String word) {
		// TODO Auto-generated method stub
		return false;
	};
}
