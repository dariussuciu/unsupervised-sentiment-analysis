package com.unsupervisedsentiment.analysis.test.constants;

public class StanfordNLPTestConstants {

	public static final String SENTENCE_ONE = "The quick brown fox jumps over the lazy dog.";
	public static final String SENTENCE_TWO = "The quick brown fox jumps over the lazy dog. And the good dog mauled the fox.";

	public static final String SENTENCE_LIU = "Canon G3 takes great pictures. The picture is amazing. You may have to get more storage to store high quality pictures and recorded movies. The software is amazing.";

	public static String generateNSentencesString(int n) {
		StringBuilder sentence = new StringBuilder("");
		for (int i = 0; i < n; i++) {
			sentence.append(SENTENCE_ONE);
			sentence.append(" ");
		}
		return sentence.toString();
	}
}
