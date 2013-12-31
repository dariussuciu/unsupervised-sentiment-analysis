package com.unsupervisedsentiment.analysis.core;

import com.unsupervisedsentiment.analysis.test.constants.StanfordNLPTestConstants;
import com.unsupervisedsentiment.analysis.tests.StanfordNLPTests;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		StanfordNLPTests tests = new StanfordNLPTests();
		String text = StanfordNLPTestConstants.SENTENCE_TWO;
		tests.testPreprocessing(text);
	}

}
