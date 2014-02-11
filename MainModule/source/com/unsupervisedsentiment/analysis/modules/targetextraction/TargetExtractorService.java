package com.unsupervisedsentiment.analysis.modules.targetextraction;

import java.util.HashSet;
import java.util.Set;

import com.unsupervisedsentiment.analysis.model.Tuple;

import edu.stanford.nlp.semgraph.SemanticGraph;

public class TargetExtractorService implements ITargetExtractorService {

	@Override
	public Set<Tuple> extractTargetUsingR1(SemanticGraph semanticGraph) {
		Set<Tuple> targets = new HashSet<Tuple>();

		return targets;
	}

	@Override
	public Set<Tuple> extractTargetUsingR3(SemanticGraph semanticGraph) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Tuple> extractTargetUsingR11(HashSet<Tuple> opinionWords) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Tuple> extractTargetUsingR12(HashSet<Tuple> opinionWords) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Tuple> extractTargetUsingR31(HashSet<Tuple> targets) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Tuple> extractTargetUsingR32(HashSet<Tuple> targets) {
		// TODO Auto-generated method stub
		return null;
	}

}
