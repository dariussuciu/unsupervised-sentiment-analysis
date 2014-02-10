package com.unsupervisedsentiment.analysis.core;

import com.unsupervisedsentiment.analysis.model.DoublePropagationData;
import com.unsupervisedsentiment.analysis.modules.doublepropagation.DoublePropagationAlgorithm;
import com.unsupervisedsentiment.analysis.test.constants.*;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		DoublePropagationData inputData = new DoublePropagationData();
		//inputData.setInput(StanfordNLPTestConstants.SENTENCE_LIU);
		//inputData.setInput(StanfordNLPTestConstants.SENTENCE_TEST1);
		//inputData.setInput(StanfordNLPTestConstants.SENTENCE_TEST2);
		//inputData.setInput(StanfordNLPTestConstants.SENTENCE_TEST3);
		//inputData.setInput(StanfordNLPTestConstants.SENTENCE_TEST4);
		//inputData.setInput(StanfordNLPTestConstants.SENTENCE_TEST5);
		inputData.setInput(StanfordNLPTestConstants.SENTENCE_TEST6);
		DoublePropagationAlgorithm algorithm = new DoublePropagationAlgorithm(inputData);
		algorithm.Execute();	
	}

}
