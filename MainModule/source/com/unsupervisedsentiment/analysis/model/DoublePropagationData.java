package com.unsupervisedsentiment.analysis.model;

import java.util.ArrayList;
import java.util.HashSet;

public class DoublePropagationData {
	//input
	private HashSet<DoublePropagationElement> opinionWordDictionary;
	private ArrayList<String> inputSentences;
	
	//output
	private HashSet<DoublePropagationElement> featuresDictionary;
	private HashSet<DoublePropagationElement> expandedOpinionWordDictionary;
	
	
	public HashSet<DoublePropagationElement> getOpinionWordDictionary() {
		return opinionWordDictionary;
	}
	public void setOpinionWordDictionary(
			HashSet<DoublePropagationElement> opinionWordDictionary) {
		this.opinionWordDictionary = opinionWordDictionary;
	}
	public ArrayList<String> getInputSentences() {
		return inputSentences;
	}
	public void setInputSentences(ArrayList<String> inputSentences) {
		this.inputSentences = inputSentences;
	}
	public HashSet<DoublePropagationElement> getFeaturesDictionary() {
		return featuresDictionary;
	}
	public void setFeaturesDictionary(
			HashSet<DoublePropagationElement> featuresDictionary) {
		this.featuresDictionary = featuresDictionary;
	}
	public HashSet<DoublePropagationElement> getExpandedOpinionWordDictionary() {
		return expandedOpinionWordDictionary;
	}
	public void setExpandedOpinionWordDictionary(
			HashSet<DoublePropagationElement> expandedOpinionWordDictionary) {
		this.expandedOpinionWordDictionary = expandedOpinionWordDictionary;
	}
}
