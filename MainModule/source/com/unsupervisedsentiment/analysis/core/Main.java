package com.unsupervisedsentiment.analysis.core;

import java.util.HashSet;

import com.unsupervisedsentiment.analysis.model.DoublePropagationData;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.Word;
import com.unsupervisedsentiment.analysis.modules.doublepropagation.DoublePropagationAlgorithm;
import com.unsupervisedsentiment.analysis.test.constants.*;
import com.unsupervisedsentiment.analysis.test.constants.relations.Pos_JJRel;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		boolean ok = Pos_JJRel.getInstance().contains("JJR");
		
		DoublePropagationData inputData = new DoublePropagationData();
		//inputData.setInput(StanfordNLPTestConstants.SENTENCE_LIU);
		inputData.setInput(StanfordNLPTestConstants.SENTENCE_TEST1);
		//inputData.setInput(StanfordNLPTestConstants.SENTENCE_TEST2);
		//inputData.setInput(StanfordNLPTestConstants.SENTENCE_TEST3);
		//inputData.setInput(StanfordNLPTestConstants.SENTENCE_TEST4);
		//inputData.setInput(StanfordNLPTestConstants.SENTENCE_TEST5);
		//inputData.setInput(StanfordNLPTestConstants.SENTENCE_TEST6);
		DoublePropagationAlgorithm algorithm = new DoublePropagationAlgorithm(inputData);
		
		HashSet<Tuple>  seed = new HashSet<Tuple>();
		
		Tuple test = new Tuple();
		
		test.setOpinion(new Word("JJ", "good"));
		
		seed.add(test);
		
		algorithm.execute(seed);	
	}

}
