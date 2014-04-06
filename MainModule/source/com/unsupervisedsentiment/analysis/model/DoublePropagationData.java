package com.unsupervisedsentiment.analysis.model;

import java.util.List;
import java.util.LinkedHashSet;

import edu.stanford.nlp.semgraph.SemanticGraph;

public class DoublePropagationData {
	// input
	private String filename;
	private String input;

	// processed sentences, an HashSet for each sentence
	private List<SemanticGraph> sentancesSemanticGraphs;

	// output
	private LinkedHashSet<Tuple> features;
	private LinkedHashSet<Tuple> expandedOpinionWords;

	public DoublePropagationData() {
		setFeatures(new LinkedHashSet<Tuple>());
		setExpandedOpinionWords(new LinkedHashSet<Tuple>());
	}

	public String getInput() {
		return input;
	}

	public void setInput(final String input) {
		this.input = input;
	}

	public List<SemanticGraph> getSentancesSemanticGraphs() {
		return sentancesSemanticGraphs;
	}

	public void setSentancesSemanticGraphs(
			final List<SemanticGraph> sentancesSemanticGraph) {
		this.sentancesSemanticGraphs = sentancesSemanticGraph;
	}

	public LinkedHashSet<Tuple> getFeatureTuples() {
		return features;
	}

	public void setFeatures(final LinkedHashSet<Tuple> features) {
		this.features = features;
	}

	public LinkedHashSet<Tuple> getExpandedOpinionWordsTuples() {
		return expandedOpinionWords;
	}

	public void setExpandedOpinionWords(
			final LinkedHashSet<Tuple> expandedOpinionWords) {
		this.expandedOpinionWords = expandedOpinionWords;
	}

	public LinkedHashSet<Word> getExpandedOpinionWords() {
		final LinkedHashSet<Word> opinionWords = new LinkedHashSet<Word>();
		for (Tuple tuple : expandedOpinionWords) {
			Word foundOpinionWord = getWord(tuple, ElementType.OPINION_WORD);
			if (foundOpinionWord != null)
				opinionWords.add(foundOpinionWord);
		}
		return opinionWords;
	}

	public LinkedHashSet<Word> getFeatures() {
		final LinkedHashSet<Word> featureWords = new LinkedHashSet<Word>();
		for (Tuple tuple : features) {
			Word foundFeature = getWord(tuple, ElementType.FEATURE);
			if (foundFeature != null)
				featureWords.add(foundFeature);
		}
		return featureWords;
	}

	private Word getWord(final Tuple tuple, final ElementType type) {
		// seed words tuples don`t have targets
		if (tuple.getTupleType().equals(TupleType.Seed)
				&& tuple.getSource().getType().equals(type)) {
			return tuple.getSource();
		}

		ElementType sourceType = tuple.getSource().getType();
		ElementType targetType = tuple.getTarget().getType();
		if (sourceType.equals(type))
			return tuple.getSource();
		if (targetType.equals(type))
			return tuple.getTarget();
		return null;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(final String filename) {
		this.filename = filename;
	}

}
