package com.unsupervisedsentiment.analysis.model;

import java.io.Serializable;

public class EvaluationModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1928906304054077977L;
	private final String element;
	private final String sentence;
	private final int sentenceIndex;
	private double opinionWordScore;

	public EvaluationModel(final String element, final String sentence,
			final int sentenceIndex) {
		super();
		this.element = element;
		this.sentence = sentence;
		this.sentenceIndex = sentenceIndex;
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
		return "EvaluationModel [element=" + element + ", sentence="
				+ sentence + ", sentenceIndex=" + sentenceIndex + "]";
	}
	
	public double getOpinionWordScore() {
		return opinionWordScore;
	}

	public void setOpinionWordScore(double opinionWordScore) {
		this.opinionWordScore = opinionWordScore;
	}

	public String getElement() {
		return element;
	}

}
