package com.unsupervisedsentiment.analysis.modules.targetextraction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.unsupervisedsentiment.analysis.model.*;

import edu.stanford.nlp.semgraph.SemanticGraph;

public interface ITargetExtractorService {
	public Set<Tuple> extractTargetUsingR1(SemanticGraph semanticGraph);

	public Set<Tuple> extractTargetUsingR3(SemanticGraph semanticGraph);
}
