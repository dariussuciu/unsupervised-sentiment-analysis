package com.unsupervisedsentiment.analysis.modules.targetextraction;
import java.util.Set;

import com.unsupervisedsentiment.analysis.model.*;

import edu.stanford.nlp.semgraph.SemanticGraph;

public interface ITargetExtractorService {
	public Set<Tuple> extractTargetsUsingR1(SemanticGraph semanticGraph, Set<Word> opinionWords, Set<Tuple> existingFeatures, int semanticGraphIndex);

	public Set<Tuple> extractTargetsUsingR3(SemanticGraph semanticGraph, Set<Word> features, Set<Tuple> existingFeatures, int semanticGraphIndex);
}
