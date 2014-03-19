package com.unsupervisedsentiment.analysis.modules.targetextraction;

import java.util.Set;

import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.Word;

import edu.stanford.nlp.semgraph.SemanticGraph;

public interface IOpinionWordExtractorService {
	public Set<Tuple> extractOpinionWordsUsingR2(SemanticGraph semanticGraph, Set<Word> targets, Set<Tuple> existingOpinionWords, int semanticGraphIndex);

	public Set<Tuple> extractOpinionWordsUsingR4(SemanticGraph semanticGraph, Set<Word> opinionWords, Set<Tuple> existingOpinionWords, int semanticGraphIndex);
}
