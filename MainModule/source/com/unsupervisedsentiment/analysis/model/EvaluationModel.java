package com.unsupervisedsentiment.analysis.model;

import java.util.List;

public class EvaluationModel {
	
	private String opinionWord;
	private List<String> sentence;
	
	public EvaluationModel(String opinionWord, List<String> sentence) {
		super();
		this.opinionWord = opinionWord;
		this.sentence = sentence;
	}
	
	public String getOpinionWord() {
		return opinionWord;
	}
	public void setOpinionWord(String opinionWord) {
		this.opinionWord = opinionWord;
	}
	public List<String> getSentence() {
		return sentence;
	}
	public void setSentence(List<String> sentence) {
		this.sentence = sentence;
	}

}
