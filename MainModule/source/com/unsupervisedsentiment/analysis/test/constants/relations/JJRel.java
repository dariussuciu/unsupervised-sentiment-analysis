package com.unsupervisedsentiment.analysis.test.constants.relations;

public class JJRel extends GenericRelation{
	
	public enum JJ{
		JJ,
		JJR,
		JJS
	}
	
	private static JJRel jjRel;
	
	private JJRel() {}
	
	public static JJRel getInstance() {
		if(isInstantiated)
			return jjRel;
		
		isInstantiated = true;
		return jjRel = new JJRel();
	}

	@Override
	public boolean Contains(String word) {
		return super.isInEnum(word, JJ.class);
	};
}
