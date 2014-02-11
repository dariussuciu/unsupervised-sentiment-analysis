package com.unsupervisedsentiment.analysis.modules.targetextraction;

import java.util.Set;

import com.unsupervisedsentiment.analysis.model.Tuple;

import edu.stanford.nlp.semgraph.SemanticGraph;

public interface IOpinionWordExtractorService {
	public Set<Tuple> extractOpinionWordR2(SemanticGraph semanticGraph);

	public Set<Tuple> extractOpinionWordR4(SemanticGraph semanticGraph);
}
