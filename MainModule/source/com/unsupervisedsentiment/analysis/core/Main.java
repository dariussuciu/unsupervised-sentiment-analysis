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
		inputData.setInput(StanfordNLPTestConstants.SENTENCE_LIU);
		
		DoublePropagationAlgorithm algorithm = new DoublePropagationAlgorithm(inputData);
		algorithm.Execute();	
	}

}
