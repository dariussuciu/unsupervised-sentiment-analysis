package com.unsupervisedsentiment.analysis.test.constants.relations;

public class NNRel extends GenericRelation {
	public enum NN {
		NN,
		NNP,
		NNPS,
		NNS
	}
	
	private static NNRel nnRel;
	
	private NNRel() {}
	
	public static NNRel getInstance() {
		if(isInstantiated)
			return nnRel;
		
		isInstantiated = true;
		return nnRel = new NNRel();
	}

	@Override
	public boolean contains(String word) {
		return super.isInEnum(word, NN.class);
	};
}
