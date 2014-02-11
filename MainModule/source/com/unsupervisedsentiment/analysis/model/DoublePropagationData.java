package com.unsupervisedsentiment.analysis.model;

import java.util.HashSet;
import java.util.List;

import edu.stanford.nlp.semgraph.SemanticGraph;

public class DoublePropagationData {
	// input
	private HashSet<Tuple> opinionWords;
	private String input;

	// processed sentences, an HashSet for each sentence
	private List<SemanticGraph> sentancesSemanticGraphs;

	// output
	private HashSet<Tuple> features;
	private HashSet<Tuple> expandedOpinionWords;

	public DoublePropagationData() {
		opinionWords = new HashSet<Tuple>();
		setFeatures(new HashSet<Tuple>());
		setExpandedOpinionWords(new HashSet<Tuple>());
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public List<SemanticGraph> getSentancesSemanticGraphs() {
		return sentancesSemanticGraphs;
	}

	public void setSentancesSemanticGraphs(
			List<SemanticGraph> sentancesSemanticGraph) {
		this.sentancesSemanticGraphs = sentancesSemanticGraph;
	}

	public HashSet<Tuple> getFeatures() {
		return features;
	}

	public void setFeatures(HashSet<Tuple> features) {
		this.features = features;
	}

	public HashSet<Tuple> getExpandedOpinionWords() {
		return expandedOpinionWords;
	}

	public void setExpandedOpinionWords(HashSet<Tuple> expandedOpinionWords) {
		this.expandedOpinionWords = expandedOpinionWords;
	}

	public HashSet<Tuple> getOpinionWords() {
		return opinionWords;
	}

	public void setOpinionWords(HashSet<Tuple> opinionWords) {
		this.opinionWords = opinionWords;
	}

}
