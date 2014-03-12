package com.unsupervisedsentiment.analysis.model;

public class EvaluationModel {
	
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

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

}
