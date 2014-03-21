package com.unsupervisedsentiment.analysis.modules.targetextraction;

import java.util.Set;

import com.unsupervisedsentiment.analysis.model.*;

import edu.stanford.nlp.semgraph.SemanticGraph;

public interface ITargetExtractorService {
	public Set<Tuple> extractTargetsUsingR1(final SemanticGraph semanticGraph,
			final Set<Word> opinionWords, final Set<Tuple> existingFeatures,
			final int semanticGraphIndex);

	public Set<Tuple> extractTargetsUsingR3(final SemanticGraph semanticGraph,
			final Set<Word> features, final Set<Tuple> existingFeatures,
			final int semanticGraphIndex);
}
