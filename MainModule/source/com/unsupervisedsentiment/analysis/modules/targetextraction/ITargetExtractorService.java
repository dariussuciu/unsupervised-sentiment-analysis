package com.unsupervisedsentiment.analysis.modules.targetextraction;

import java.util.Collection;
import java.util.Set;

import com.unsupervisedsentiment.analysis.model.*;

import edu.stanford.nlp.semgraph.SemanticGraph;

public interface ITargetExtractorService {
	public Collection<? extends Tuple> extractTargetUsingR1(SemanticGraph semanticGraph, Set<Word> opinionWords);

	public Set<Tuple> extractTargetUsingR3(SemanticGraph semanticGraph, Set<Word> targets);
}
