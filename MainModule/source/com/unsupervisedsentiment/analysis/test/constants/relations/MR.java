package com.unsupervisedsentiment.analysis.test.constants.relations;

public class MR extends GenericRelation{
	public enum mod {
		amod,
		appos,
		advcl,
		det,
		predet,
		preconj,
		vmod,
		mwe,
		mark,
		advmod,
		neg,
		rcmod,
		quantmod,
		nn,
		npadvmod,
		tmod,
		num,
		number,
		prep,
		poss,
		possessive,
		prt
	};
	
	public enum pmod {
		
	};
	
	public enum subj {
		nsubj,
		nsubjpass,
		csubj,
		csubjpass
	};
	
	public enum s {
		
	};
	
	public enum obj {
		dobj,
		iobj,
		pobj
	};
	
	public enum obj2 {
		
	};
	
	public enum desc {
		
	}

	@Override
	public boolean Contains(String word) {
		// TODO Auto-generated method stub
		return false;
	};
}
