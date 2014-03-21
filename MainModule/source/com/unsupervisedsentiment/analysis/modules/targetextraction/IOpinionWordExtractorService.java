package com.unsupervisedsentiment.analysis.modules.targetextraction;

import java.util.Set;

import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.Word;

import edu.stanford.nlp.semgraph.SemanticGraph;

public interface IOpinionWordExtractorService {
	public Set<Tuple> extractOpinionWordsUsingR2(
			final SemanticGraph semanticGraph, final Set<Word> targets,
			final Set<Tuple> existingOpinionWords, final int semanticGraphIndex);

	public Set<Tuple> extractOpinionWordsUsingR4(
			final SemanticGraph semanticGraph, final Set<Word> opinionWords,
			final Set<Tuple> existingOpinionWords, final int semanticGraphIndex);
}
