package com.unsupervisedsentiment.analysis.model;

import java.io.Serializable;

public class EvaluationModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1928906304054077977L;
	private final String opinionWord;
	private final String sentence;
	private final int sentenceIndex;

	public EvaluationModel(final String opinionWord, final String sentence,
			final int sentenceIndex) {
		super();
		this.opinionWord = opinionWord;
		this.sentence = sentence;
		this.sentenceIndex = sentenceIndex;
	}

	public String getOpinionWord() {
		return opinionWord;
	}

	public int getSentenceIndex() {
		return sentenceIndex;
	}

	public String getSentence() {
		return sentence;
	}

	public String getCleanSentence() {
		String cleanSentence = sentence.replaceAll("(###)|(%%%)|(\\$\\$\\$)",
				"");
		return cleanSentence;
	}

	@Override
	public String toString() {
		return "EvaluationModel [opinionWord=" + opinionWord + ", sentence="
				+ sentence + ", sentenceIndex=" + sentenceIndex + "]";
	}

}
