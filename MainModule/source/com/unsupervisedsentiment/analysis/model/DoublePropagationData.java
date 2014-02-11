package com.unsupervisedsentiment.analysis.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DoublePropagationData {
	//input
	private HashSet<Tuple> opinionWordDictionary;
	private String input;
	
	//processed sentences, an HashSet for each sentence
	private List<HashSet<Tuple>> allOpinionWords;
	private List<HashSet<Tuple>> allTargets;
	
	//output
	private HashSet<Tuple> featuresDictionary;
	private HashSet<Tuple> expandedOpinionWordDictionary;
	
	public DoublePropagationData() {
		opinionWordDictionary = new HashSet<Tuple>();
		allOpinionWords = new ArrayList<HashSet<Tuple>>();
		allTargets = new ArrayList<HashSet<Tuple>>();
		featuresDictionary = new HashSet<Tuple>();
		expandedOpinionWordDictionary = new HashSet<Tuple>();
	}
	
	public HashSet<Tuple> getOpinionWordDictionary() {
		return opinionWordDictionary;
	}
	public void setOpinionWordDictionary(
			HashSet<Tuple> opinionWordDictionary) {
		this.opinionWordDictionary = opinionWordDictionary;
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
	public List<HashSet<Tuple>> getAllOpinionWords() {
		return allOpinionWords;
	}
	public void setAllOpinionWords(List<HashSet<Tuple>> allOpinionWords) {
		this.allOpinionWords = allOpinionWords;
	}
	public List<HashSet<Tuple>> getAllTargets() {
		return allTargets;
	}
	public void setAllTargets(List<HashSet<Tuple>> allTargets) {
		this.allTargets = allTargets;
	}
	public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
}
