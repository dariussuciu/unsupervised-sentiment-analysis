package com.unsupervisedsentiment.analysis.model;

import java.util.ArrayList;
import java.util.HashSet;

public class DoublePropagationData {
	//input
	private HashSet<Tuple> opinionWordDictionary;
	private ArrayList<String> inputSentences;
	
	//output
	private HashSet<Tuple> featuresDictionary;
	private HashSet<Tuple> expandedOpinionWordDictionary;
	
	
	public HashSet<Tuple> getOpinionWordDictionary() {
		return opinionWordDictionary;
	}
	public void setOpinionWordDictionary(
			HashSet<Tuple> opinionWordDictionary) {
		this.opinionWordDictionary = opinionWordDictionary;
	}
	public ArrayList<String> getInputSentences() {
		return inputSentences;
	}
	public void setInputSentences(ArrayList<String> inputSentences) {
		this.inputSentences = inputSentences;
	}
	public HashSet<Tuple> getFeaturesDictionary() {
		return featuresDictionary;
	}
	public void setFeaturesDictionary(
			HashSet<Tuple> featuresDictionary) {
		this.featuresDictionary = featuresDictionary;
	}
	public HashSet<Tuple> getExpandedOpinionWordDictionary() {
		return expandedOpinionWordDictionary;
	}
	public void setExpandedOpinionWordDictionary(
			HashSet<Tuple> expandedOpinionWordDictionary) {
		this.expandedOpinionWordDictionary = expandedOpinionWordDictionary;
	}
}
