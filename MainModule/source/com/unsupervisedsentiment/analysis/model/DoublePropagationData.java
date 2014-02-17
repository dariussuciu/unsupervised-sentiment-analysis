package com.unsupervisedsentiment.analysis.model;

import java.util.HashSet;
import java.util.List;

import edu.stanford.nlp.semgraph.SemanticGraph;

public class DoublePropagationData {
	// input
	private HashSet<Word> seedWords;
	private String input;

	// processed sentences, an HashSet for each sentence
	private List<SemanticGraph> sentancesSemanticGraphs;

	// output
	private HashSet<Tuple> features;
	private HashSet<Tuple> expandedOpinionWords;

	public DoublePropagationData() {
		setSeedWords(new HashSet<Word>());
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

	public void setSentancesSemanticGraphs(List<SemanticGraph> sentancesSemanticGraph) {
		this.sentancesSemanticGraphs = sentancesSemanticGraph;
	}

	public HashSet<Tuple> getFeatureTuples() {
		return features;
	}

	public void setFeatures(HashSet<Tuple> features) {
		this.features = features;
	}

	public HashSet<Tuple> getExpandedOpinionWordsTuples() {
		return expandedOpinionWords;
	}

	public void setExpandedOpinionWords(HashSet<Tuple> expandedOpinionWords) {
		this.expandedOpinionWords = expandedOpinionWords;
	}

	public HashSet<Word> getExpandedOpinionWords() {
		HashSet<Word> opinionWords = new HashSet<Word>();
		for (Tuple tuple : expandedOpinionWords) {
			Word foundOpinionWord = getWord(tuple, ElementType.OPINION_WORD);
			if(foundOpinionWord != null)
				opinionWords.add(foundOpinionWord);
		}
		return opinionWords;
	}

	public HashSet<Word> getFeatures() {
		HashSet<Word> featureWords = new HashSet<Word>();
		for (Tuple tuple : features) {
			Word foundFeature = getWord(tuple, ElementType.FEATURE);
			if(foundFeature != null)
				featureWords.add(foundFeature);
		}
		return featureWords;
	}
	
	private Word getWord(Tuple tuple, ElementType type)
	{
		// seed words tuples don`t have targets 
		if(tuple.getTupleType().equals(TupleType.Seed) && tuple.getSource().getType().equals(type))
		{
			return tuple.getSource();
		}
		
		ElementType sourceType = tuple.getSource().getType();
		ElementType targetType = tuple.getTarget().getType();
		if(sourceType.equals(type))
			return tuple.getSource();
		if(targetType.equals(type))
			return tuple.getTarget();
		return null;
	}

	public HashSet<Word> getSeedWords() {
		return seedWords;
	}

	public void setSeedWords(HashSet<Word> seedWords) {
		this.seedWords = seedWords;
	}

}
