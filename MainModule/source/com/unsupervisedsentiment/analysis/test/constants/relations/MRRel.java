package com.unsupervisedsentiment.analysis.test.constants.relations;

public class MRRel extends GenericRelation{
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
	
	private static MRRel mrRel;
	
	private MRRel() {}
	
	public static MRRel getInstance() {
		if(isInstantiated)
			return mrRel;
		
		isInstantiated = true;
		return mrRel = new MRRel();
	}

	@Override
	public boolean contains(String word) {
		return     super.isInEnum(word, mod.class)
				|| super.isInEnum(word, pmod.class)
				|| super.isInEnum(word, subj.class)
				|| super.isInEnum(word, s.class)
				|| super.isInEnum(word, obj.class)
				|| super.isInEnum(word, obj2.class)
				|| super.isInEnum(word, desc.class);
	};
}
