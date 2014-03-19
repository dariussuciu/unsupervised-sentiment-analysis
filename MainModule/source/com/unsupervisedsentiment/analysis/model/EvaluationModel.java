package com.unsupervisedsentiment.analysis.model;

import java.io.Serializable;

public class EvaluationModel implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1928906304054077977L;
	private String opinionWord;
	private String sentence;
	private int sentenceIndex;
	
	public EvaluationModel(String opinionWord, String sentence, int index) {
		super();
		this.opinionWord = opinionWord;
		this.setSentence(sentence);
		this.setSentenceIndex(index);
	}
	
	public String getOpinionWord() {
		return opinionWord;
	}
	public void setOpinionWord(String opinionWord) {
		this.opinionWord = opinionWord;
	}

	public int getSentenceIndex() {
		return sentenceIndex;
	}

	public void setSentenceIndex(int sentenceIndex) {
		this.sentenceIndex = sentenceIndex;
	}

	public String getSentence() {
		return sentence;
	}
	
	public String getCleanSentence() {
		String cleanSentence = sentence.replaceAll("(###)|(%%%)|(\\$\\$\\$)", "");
		return cleanSentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	@Override
	public String toString() {
		return "EvaluationModel [opinionWord=" + opinionWord + ", sentence="
				+ sentence + ", sentenceIndex=" + sentenceIndex + "]";
	}

}
